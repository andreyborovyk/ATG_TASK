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
import java.util.HashMap;
import java.util.Iterator;

import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupOrderRelationship;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.RelationshipTypes;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This processor validates that all order costs are accounted for by a PaymentGroup.
 * Order costs are accounted for if there is only one PaymentGroup and it has no
 * Relationships or if order level Relationships exist in the Order which cover
 * the entire amount of the Order.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateOrderCostsForCheckout.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcValidateOrderCostsForCheckout extends ApplicationLoggingImpl implements PipelineProcessor {  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateOrderCostsForCheckout.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static java.util.ResourceBundle sUserResourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcValidateOrderCostsForCheckout() {
  }

  private double mMarginOfError=0.00001;
  
  //-------------------------------------
  // property: marginOfError
  //-------------------------------------
  /**
   * Returns the marginOfError
   */
  public double getMarginOfError() {
    return mMarginOfError;
  }

  /**
   * Sets the marginOfError. The margin of error determines what margin of error
   * the double arithmatic will allow. The default value is 0.00001. To widen the
   * margin, increase the value, to narrow it, decrease the value.
   */
  public void setMarginOfError(double pMarginOfError) {
    mMarginOfError = pMarginOfError;
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
  String mLoggingIdentifier = "ProcValidateOrderCostsForCheckout";

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
   * This processor validates that all order costs are accounted for by a PaymentGroup.
   * Order costs are accounted for if there is only one PaymentGroup and it has no
   * Relationships or if order level Relationships exist in the Order which cover
   * the entire amount of the Order.
   *
   * To do additional validation for Order costs, override this method to validate
   * and then call this class' runProcess() method.
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
    if (isLoggingDebug()) {
      logDebug("Validating order costs for checkout");
    }

    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    java.util.Locale locale = (java.util.Locale) map.get(PipelineConstants.LOCALE);
    java.util.ResourceBundle resourceBundle;
    
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);
    
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      RESOURCE_NAME, sResourceBundle));

    PaymentGroupOrderRelationship rel;
    Iterator iter;
//    double relTotalOrderPct = 0.0;
    double relTotalOrderAmt = 0.0;
    double relTotalTaxAmt = 0.0;
    double orderAmountAmount = 0.0;
    boolean accountedFor = false;
    boolean taxAccountedFor = false;
    
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
    if (order.getPaymentGroupRelationshipCount() > 0) {
      iter = order.getPaymentGroupRelationships().iterator();
      while (iter.hasNext()) {
        rel = (PaymentGroupOrderRelationship) iter.next();
        switch (rel.getRelationshipType()) {
          case RelationshipTypes.ORDERAMOUNTREMAINING:
            return SUCCESS;
            
          case RelationshipTypes.ORDERAMOUNT:
            orderAmountAmount += rel.getAmount();
            if (isGreaterOrEqual(orderAmountAmount, order.getPriceInfo().getTotal()))
              return SUCCESS;
          break;
        }
      }
    }
    
    // the standard case, if any Relationships exist in any paymentGroups then we must check to see
    // if all the order costs are accounted for
    double orderAmt = order.getPriceInfo().getAmount();

    if (isLoggingDebug()) {
      logDebug("Verifying that order amount " + orderAmt + " and tax amount " + order.getTaxPriceInfo().getAmount() + " are accounted for");
    }

    if (order.getPaymentGroupRelationshipCount() > 0) {
      for (iter = order.getPaymentGroupRelationships().iterator(); iter.hasNext(); ) {
        rel = (PaymentGroupOrderRelationship) iter.next();
        switch (rel.getRelationshipType()) {
          case RelationshipTypes.ORDERAMOUNTREMAINING:
            // if this is a remaining relationship then the tax, ship cost, and items are accounted for
            accountedFor = true;
          break;
          case RelationshipTypes.TAXAMOUNTREMAINING:
            // if this is a tax remaining relationship then the tax is accounted for
            taxAccountedFor = true;
          break;
          case RelationshipTypes.ORDERAMOUNT:
            relTotalOrderAmt += rel.getAmount();
            if (isGreaterOrEqual(relTotalOrderAmt, order.getPriceInfo().getAmount()))
              accountedFor = true;
          break;
  //          case RelationshipTypes.ORDERPERCENTAGE:
  //            if (rel.getPercentage() >= 100.0)
  //              accountedFor = true;
  //          break;
          case RelationshipTypes.TAXAMOUNT:
            relTotalTaxAmt += rel.getAmount();
            if (isGreaterOrEqual(relTotalTaxAmt, order.getTaxPriceInfo().getAmount()))
              taxAccountedFor = true;
            if (isLoggingDebug()) {
              logDebug("Tax amount on payment group is " + rel.getAmount() + "; current tax accounted for is " + relTotalTaxAmt);
            }
          break;
        } // switch
      } // for
    } // if

    // if the tax is 0.0 then there is no need for a relationship
    if (amountsEqual(order.getTaxPriceInfo().getAmount(),0.0, getMarginOfError()))
      taxAccountedFor = true;

    if (taxAccountedFor == false) {
      if (isLoggingDebug()) {
        logDebug("Tax not accounted for");
      }
      String msg = resourceBundle.getString("PayInfoNotGivenForTax");
      pResult.addError(PipelineConstants.VALIDATETAXCOSTFAILED,
                      MessageFormat.format(msg, order.getId()));
    }

    return SUCCESS;
  }

  /**
   * A convenience method that returns true if pAmount1 is greater than or equal to pAmount2
   * within the specified margin of error.
   * @param pAmount1 - the first operand in comparison (x in the x > y)
   * @param pAmount2 - the second operand in comparison (y in the x > y)
   * @param pMarginOfError - the acceptable difference between the values for a valid comparison
   **/
  private boolean isGreaterOrEqual(double pAmount1, double pAmount2, double pMarginOfError) {
    if (pAmount1 > pAmount2)
      return true;
    return amountsEqual(pAmount1, pAmount2, pMarginOfError);
  }

  /**
   * A convenience method that returns true if pAmount1 is equal to pAmount2 within a specified
   * margin of error.
   * @param pAmount1 - the first operand 
   * @param pAmount2 - the second operand
   * @param pMarginOfError - the acceptable difference between the values for a valid comparison
   **/
  private boolean amountsEqual(double pAmount1, double pAmount2, double pMarginOfError) {
    return (Math.abs(pAmount1 - pAmount2) <= pMarginOfError);
  }
  

  /**
   * Convenience method that calls into isGreaterOrEqual with the property getMarginOfError.
   **/ 
  private boolean isGreaterOrEqual(double pAmount1, double pAmount2) {
    return isGreaterOrEqual(pAmount1, pAmount2, getMarginOfError());
  }
  
}

