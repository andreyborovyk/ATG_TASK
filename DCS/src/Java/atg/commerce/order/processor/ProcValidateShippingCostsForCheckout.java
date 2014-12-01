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
import java.util.ResourceBundle;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupOrderRelationship;
import atg.commerce.order.PaymentGroupShippingGroupRelationship;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This processor validates that all shipping costs are accounted for by a PaymentGroup.
 * Shipping costs are accounted for if there is only one PaymentGroup and it has no
 * Relationships, if the ShippingGroup has been assigned to a PaymentGroup, or if an
 * order level Relationship exists in the Order which covers the entire amount of the
 * Order.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateShippingCostsForCheckout.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcValidateShippingCostsForCheckout extends ApplicationLoggingImpl implements PipelineProcessor {  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateShippingCostsForCheckout.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  /** Resource Bundle **/
  private static ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static ResourceBundle sUserResourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcValidateShippingCostsForCheckout() {
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
  String mLoggingIdentifier = "ProcValidateShippingCostsForCheckout";

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
   * This method validates that all ShippingGroup costs are accounted for. In order
   * to be accounted for there must either be one PaymentGroup in the Order with
   * no Relationships, an order level Relationship which covers the entire cost
   * of the order, or the ShippingGroup must be assigned to a PaymentGroup. If any
   * ShippingGroup's costs are not accounted for, then an error will be added to
   * pResult. To do additional validation, override this method, do the validation
   * and call this class' runProcess() method to do the default validation.
   *
   * This method requires that an Order and optionally a Locale object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and optionally a Locale object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    Locale locale = (Locale) map.get(PipelineConstants.LOCALE);
    ResourceBundle resourceBundle;
    
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      RESOURCE_NAME, sResourceBundle));

    PaymentGroupOrderRelationship orel;
    PaymentGroupShippingGroupRelationship rel;
    ShippingGroup sg;
    ArrayList badShipGroups = new ArrayList(7);
    Iterator iter, iter2;
    double relTotalAmt, shipAmt, orderAmountAmount;
    int relType;
    boolean accountedFor = false;
    
    // the special case, if there is only one paymentGroup that has no Relationships,
    // then all the items, tax, and shipping costs implicitly belong to that paymentGroup
    if (order.getPaymentGroupCount() == 1) {
      PaymentGroup pg = (PaymentGroup) order.getPaymentGroups().get(0);
      if (pg.getCommerceItemRelationshipCount() == 0 &&
          pg.getShippingGroupRelationshipCount() == 0 &&
          pg.getOrderRelationshipCount() == 0)
        return SUCCESS;
    }
    
    // if there is a PaymentGroupOrderRelationship then check to see if it covers
    // the amount of the entire order for payment
    orderAmountAmount = 0.0;
    if (order.getPaymentGroupRelationshipCount() > 0) {
      iter = order.getPaymentGroupRelationships().iterator();
      while (iter.hasNext()) {
        orel = (PaymentGroupOrderRelationship) iter.next();
        switch (orel.getRelationshipType()) {
          case RelationshipTypes.ORDERAMOUNTREMAINING:
            return SUCCESS;
            
          case RelationshipTypes.ORDERAMOUNT:
            orderAmountAmount += orel.getAmount();
            if (orderAmountAmount >= order.getPriceInfo().getAmount())
              return SUCCESS;
          break;
        }
      }
    }

    // the standard case, if any Relationships exist in any paymentGroups then we must check to see
    // if all the shipping costs are accounted for
    iter = order.getShippingGroups().iterator();
    while (iter.hasNext()) {
      sg = (ShippingGroup) iter.next();
      accountedFor = false;
      relTotalAmt = 0.0;
      shipAmt = sg.getPriceInfo().getAmount();

      iter2 = sg.getPaymentGroupRelationships().iterator();
      while (iter2.hasNext()) {
        rel = (PaymentGroupShippingGroupRelationship) iter2.next();
        relType = rel.getRelationshipType();
        
        switch (relType) {
          case RelationshipTypes.SHIPPINGAMOUNTREMAINING:
            // if this is a remaining relationship then this item is accounted for
            accountedFor = true;
          break;
          case RelationshipTypes.SHIPPINGAMOUNT:
            // if this is an amount relationship then add the rel's amount to the counter and
            // check to see if the entire amount is accounted for
            relTotalAmt += rel.getAmount();
            if (relTotalAmt >= shipAmt)
              accountedFor = true;
          break;
        }
        
        if (accountedFor)
          break;
      } // while
      
      if (accountedFor == false)
        badShipGroups.add(sg.getDescription());
      
    } // while
    
    if (badShipGroups.size() > 0) {
      StringBuffer arg = new StringBuffer();
      boolean first = true;
      iter = badShipGroups.iterator();
      while (iter.hasNext()) {
        if (first)
          first = false;
        else
          arg.append(", ");
        arg.append((String) iter.next());
      }
      String msg = resourceBundle.getString("PayInfoNotGivenForAllShippingGroups");
      pResult.addError(PipelineConstants.VALIDATESHIPPINGCOSTSFAILED,
                       MessageFormat.format(msg, arg.toString()));
    }
    
    return SUCCESS;
  }
}
