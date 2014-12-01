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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.CommerceItemRelationship;
import atg.commerce.order.HandlingInstruction;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.RelationshipNotFoundException;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This processor validates that the total quantities in the HandlingInstructions do not
 * exceed the amount assigned to the ShippingGroup. It does this by iterating over all the
 * HandlingInstructions in the ShippingGroups and validating that the sum of the quantities
 * in the HandlingInstructions do not exceed that which is assigned to the ShippingGroup. It
 * will also catch errors if HandlingInstructions contain errors such as invalid
 * ShippingGroup and CommerceItem ids or CommerceItems which are not assigned to
 * the ShippingGroup which the HandlingInstruction is in.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateHandlingInstructionsForCheckout.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcValidateHandlingInstructionsForCheckout extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateHandlingInstructionsForCheckout.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static java.util.ResourceBundle sUserResourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());
  
  private static final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcValidateHandlingInstructionsForCheckout() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  } 

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcValidateHandlingInstructionsForCheckout";

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
   * This method iterates over all the HandlingInstructions validating the quanities
   * of all the HandlingInstructions in the current ShippingGroup. It also validates
   * that the ShippingGroup id is the current ShippingGroup's id, and that the
   * CommerceItem id refers to a CommerceItem in the Order and the ShippingGroup.
   *
   * To do additional validation for HandlingInstructions, override this method to validate
   * and then call this class' runProcess() method.
   *
   * This method requires that an Order, OrderManager, and optionally a Locale object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order, OrderManager, and optionally a Locale object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.order.HandlingInstruction
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    OrderManager orderManager = (OrderManager) map.get(PipelineConstants.ORDERMANAGER);
    java.util.Locale locale = (java.util.Locale) map.get(PipelineConstants.LOCALE);
    java.util.ResourceBundle resourceBundle;
    
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      RESOURCE_NAME, sResourceBundle));
    if (orderManager == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderManagerParameter",
                                      RESOURCE_NAME, sResourceBundle));

    ShippingGroup sg;
    HandlingInstruction hi;
    CommerceItemRelationship rel;
    Iterator iter, iter2, iter3;
    Map handMap = new HashMap(7);
    Map idMap;
    Long qty;
    String className, id;
    int shippingGroupCount = order.getShippingGroupCount();
    
    iter = order.getShippingGroups().iterator();
    while (iter.hasNext()) {
      sg = (ShippingGroup) iter.next();
      handMap.clear();

      // split the handling quantities out by class name and commerce item id
      // in a HashMap of HashMaps
      iter2 = sg.getHandlingInstructions().iterator();
      while (iter2.hasNext()) {
        hi = (HandlingInstruction) iter2.next();
        
        if (! sg.getId().equals(hi.getShippingGroupId())) {
          pResult.addError("HandlingInstHasBadShippingGroupId",
                    resourceBundle.getString("HandlingInstHasBadShippingGroupId"));
          continue;
        }
        
        try {
          order.getCommerceItem(hi.getCommerceItemId());
        }
        catch (CommerceItemNotFoundException e) {
          pResult.addError("HandlingInstHasBadCommerceItemId",
                    resourceBundle.getString("HandlingInstHasBadCommerceItemId"));
          continue;
        }
        
        try {
          if (!(shippingGroupCount == 1 && sg.getCommerceItemRelationshipCount() == 0))
            orderManager.getShippingGroupCommerceItemRelationship(order,
                        hi.getCommerceItemId(), hi.getShippingGroupId());
        }
        catch (RelationshipNotFoundException e) {
          pResult.addError("HandlingInstHasBadCommerceItemId",
                    resourceBundle.getString("HandlingInstHasBadCommerceItemId"));
          continue;
        }
        
        className = hi.getClass().getName();
        idMap = (Map) handMap.get(className);
        if (idMap == null) {
          idMap = new HashMap(8);
          handMap.put(className, idMap);
        }
        
        qty = (Long) idMap.get(hi.getCommerceItemId());
        if (qty == null)
          idMap.put(hi.getCommerceItemId(), Long.valueOf(hi.getQuantity()));
        else
          idMap.put(hi.getCommerceItemId(), Long.valueOf(hi.getQuantity() + qty.longValue()));
      } // while
      
      // check the quantities
      iter2 = handMap.entrySet().iterator();
      while (iter2.hasNext()) {
        Map.Entry entry2 = (Map.Entry)iter2.next();
        className = (String) entry2.getKey();
        idMap = (Map) entry2.getValue();
        
        iter3 = idMap.entrySet().iterator();
        while (iter3.hasNext()) {
          Map.Entry entry3 = (Map.Entry)iter3.next();
          id = (String) entry3.getKey();
          qty = (Long) entry3.getValue();
          
          rel = orderManager.getShippingGroupCommerceItemRelationship(order, id, sg.getId());
              
          if (rel.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
            if (qty.longValue() > rel.getQuantity())
              pResult.addError("InvalidHandlingInstQtyInSG",
                      resourceBundle.getString("InvalidHandlingInstQtyInSG"));
          }
          else if (rel.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITYREMAINING) {
            long remainingQty = orderManager.getRemainingQuantityForShippingGroup(rel.getCommerceItem());
            if (qty.longValue() > remainingQty)
              pResult.addError("InvalidHandlingInstQtyInSG",
                      resourceBundle.getString("InvalidHandlingInstQtyInSG"));
          }
        } // while
      } // while
    } // while
    
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
