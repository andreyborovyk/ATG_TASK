/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES 
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, 
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.commerce.order.processor;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This processor validates ShippingGroups before checking an Order out. The two main
 * things which are checked for are that all CommerceItems in the Order are assigned to
 * ShippingGroups and that all the required fields in all the ShippingGroups, regardless
 * of type, are not null or empty String.
 *
 * The first check, that all CommerceItems are assigned to a ShippingGroup, first checks
 * to see if there is only one ShippingGroup and it has no Relationships. If so, then all
 * CommerceItems are implicitly assigned to that ShippingGroup. If there is more than one
 * ShippingGroup or the ShippingGroup has at least one Relationship, then all
 * CommerceItems must be assigned to one or more ShippingGroups. The CommerceItem's entire
 * quantity must be assigned.
 *
 * The second check, that required fields are provided, iterates over all the ShippingGroups
 * in the order and invokes a separate pipeline chain for each shipping group.  The first
 * processor in * that chain is responsible for examining the type of the shipping group
 * and dispatching to an appropriate validation processor or processor chain that knows
 * which fields are required for each shipping method.
 *
 * @author Manny Parasirakis, Matt Landau
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateShippingGroupsForCheckout.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcValidateShippingGroupsForCheckout
  extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateShippingGroupsForCheckout.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  /** Resource Bundle **/
  private static ResourceBundle sResourceBundle =
      LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static ResourceBundle sUserResourceBundle =
      LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, Locale.getDefault());
  
  private final int SUCCESS = 1;
  
  //---------------------------------------------------------------------------
  // property: validateShippingGroupChain
  //---------------------------------------------------------------------------

  String mValidateShippingGroupChain = "validateShippingGroup";

  /**
   * Set the name of the pipeline chain to run to validate each individual
   * shipping group.
   **/
  
  public void setValidateShippingGroupChain(String pValidateShippingGroupChain) {
    mValidateShippingGroupChain = pValidateShippingGroupChain;
  }

  /**
   * Get the name of the pipeline chain to run to validate each individual
   * shipping group.  The default chain name is "validateShippingGroup".
   **/
  
  public String getValidateShippingGroupChain() {
    return mValidateShippingGroupChain;
  }

  //---------------------------------------------------------------------------

  /**
   * Returns the valid return codes.  This processor always returns
   * a status of 1 indicating successful completion.
   **/

  protected int[] mRetCodes = { SUCCESS };
  
  public int[] getRetCodes()
  {
    return mRetCodes;
  } 

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcValidateShippingGroupsForCheckout";

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //-----------------------------------------------
  /**
   * This method performs two checks on all the ShippingGroups in the given Order.
   * The first check is to see if all the CommerceItems in the Order are assigned
   * to ShippingGroups. The second check is to determine if all the required
   * properties are not null or empty String.
   *
   * This method requires that an Order, an OrderManager, and optionally a Locale
   * object be supplied in pParam in a HashMap. Use the PipelineConstants class
   * static members to key the objects in the HashMap.
   *
   * @param pParam
   *    a HashMap which must contain an Order, an OrderManager, and optionally a
   *    Locale object
   * @param pResult
   *    a PipelineResult object which stores any information which must
   *    be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   **/
  
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager om = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    Locale locale = (Locale) map.get(PipelineConstants.LOCALE);
    ResourceBundle resourceBundle;
    
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (order == null)
      throw new InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidOrderParameter", RESOURCE_NAME, sResourceBundle));

    ShippingGroupCommerceItemRelationship rel;
    CommerceItem item;
    ShippingGroup sg;
    ArrayList badItems = new ArrayList(7);
    Iterator iter, iter2;
    long relTotalQty;
    int relType;
    boolean accountedFor = false;
    boolean detailCheck = true;
    
    // the special case, if there is only one shippingGroup that has no CommerceItemRelationships,
    // then all the items implicitly belong to that shippingGroup
    if (order.getShippingGroupCount() == 1) {
      sg = (ShippingGroup) order.getShippingGroups().get(0);
      if (sg.getCommerceItemRelationshipCount() == 0)
        detailCheck = false;
    }
    
    if (detailCheck)
    {    
      // the standard case, if any Relationships exist in any shippingGroups then we must check to see
      // if all the items are accounted for
      iter = order.getCommerceItems().iterator();
      while (iter.hasNext()) {
        item = (CommerceItem) iter.next();
        accountedFor = false;
        relTotalQty = 0;

        iter2 = item.getShippingGroupRelationships().iterator();
        while (iter2.hasNext()) {
          rel = (ShippingGroupCommerceItemRelationship) iter2.next();
          relType = rel.getRelationshipType();
          
          switch (relType) {
            case RelationshipTypes.SHIPPINGQUANTITYREMAINING:
              // if this is a remaining relationship then this item is accounted for
              accountedFor = true;
            break;
            case RelationshipTypes.SHIPPINGQUANTITY:
              // if this is a quantity relationship then add the rel's qty to the counter and
              // check to see if the entire qty is accounted for
              relTotalQty += rel.getQuantity();
              if (relTotalQty >= item.getQuantity())
                accountedFor = true;
            break;
          }
          
          if (accountedFor)
            break;
        } // while
        
        if (accountedFor == false)
          badItems.add(item.getId());
        
      } // while
      
      if (badItems.size() > 0) {
        StringBuffer arg = new StringBuffer();
        boolean first = true;
        for (iter = badItems.iterator(); iter.hasNext(); ) {
          if (first)
            first = false;
          else
            arg.append(", ");
          arg.append((String) iter.next());
        }
        String msg = resourceBundle.getString("ShipInfoNotGivenForAllItems");
        pResult.addError(PipelineConstants.VALIDATESHIPPINGGROUPSFAILED,
                        MessageFormat.format(msg, arg.toString()));
      }
    } // if


    // This section iterates over shipping groups and invokes a pipeline
    // chain to validate each shipping group in turn.

    Iterator shippingGroups = order.getShippingGroups().iterator();
    ValidateShippingGroupPipelineArgs param = new ValidateShippingGroupPipelineArgs();
    PipelineResult newResult;
    
    param.setOrder(order);
    param.setOrderManager(om);
    if (locale != null)
      param.setLocale(locale);

    while (shippingGroups.hasNext())
    {
      sg = (ShippingGroup) shippingGroups.next();
      
      // Skip empty shipping groups
      
      if (sg.getCommerceItemRelationshipCount() == 0 && order.getShippingGroupCount() > 1)
        continue;

      param.setShippingGroup(sg);

      newResult = om.getPipelineManager().runProcess(getValidateShippingGroupChain(), param);
      if (newResult.hasErrors())
      {
        pResult.copyInto(newResult);
        return STOP_CHAIN_EXECUTION_AND_ROLLBACK;
      }
    }
    return SUCCESS;
  }

  //--------------------------------------

  /**
   * This method adds an error to the PipelineResult object. This method, rather than
   * just storing a single error object in pResult, stores a Map of errors. This allows more
   * than one error to be stored using the same key in the pResult object. pKey is
   * used to reference a HashMap of errors in pResult. So, calling
   * pResult.getError(pKey) will return an object which should be cast to a Map.
   * Each entry within the map is keyed by pId and its value is pError.
   *
   * @param pResult the PipelineResult object supplied in runProcess()
   * @param pKey the key to use to store the HashMap in the PipelineResult object
   * @param pId the key to use to store the error message within the HashMap in the
   *            PipelineResult object
   * @param pError the error object to store in the HashMap
   * @see atg.service.pipeline.PipelineResult
   * @see #runProcess(Object, PipelineResult)
   */
  protected void addHashedError(PipelineResult pResult, String pKey, String pId, Object pError)
  {
    Object error = pResult.getError(pKey);
    if (error == null) {
      HashMap map = new HashMap(5);
      pResult.addError(pKey, map);
      map.put(pId, pError);
    }
    else if (error instanceof Map) {
      Map map = (Map) error;
      map.put(pId, pError);
    }
  }
}
