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
import java.util.Map;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupCommerceItemRelationship;
import atg.commerce.order.PaymentGroupOrderRelationship;
import atg.commerce.order.PaymentGroupRelationship;
import atg.commerce.order.PaymentGroupShippingGroupRelationship;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.commerce.payment.PaymentException;
import atg.commerce.pricing.PricingTools;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * This processor determines the values that need to be set into the amount field of
 * a PaymentGroup. This processor iterates through all the Relationships determining
 * the amounts to set into the PaymentGroups based on the Relationship type. Any amounts
 * set in the amount property of a PaymentGroup will be overwritten here.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSetPaymentGroupAmount.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.PaymentGroup
 */
public class ProcSetPaymentGroupAmount extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSetPaymentGroupAmount.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static java.util.ResourceBundle sUserResourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcSetPaymentGroupAmount() {
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
  // property: marginOfError
  //-------------------------------------
  private double mMarginOfError = 0.00001;

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

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcSetPaymentGroupAmount";

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

  //---------------------------------------------------------------------------
  // property:PricingTools
  //---------------------------------------------------------------------------
  private PricingTools mPricingTools;
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  /**
   * PricingTools class used for rounding
   * @beaninfo description: PricingTools class used for rounding
   **/
  public PricingTools getPricingTools() {
    return mPricingTools;
  }

  //-----------------------------------------------
  /**
   * This method determines the amount that needs to be set into the amount field of
   * a PaymentGroup. This processor iterates through all the Relationships determining
   * the amounts to set into the PaymentGroups based on the Relationship type. Any amounts
   * set in the amount property of a PaymentGroup will be overwritten here.
   *
   * This method can be executed in one of two modes, return an error in pResult, or throw
   * an exception when the first error is encountered. The default mode is to throw
   * exceptions. To change it, add a RETURNERRORS entry into pParam with the value set
   * to a Boolean object with a value of true.
   *
   * This method requires that an Order, OrderManager, and optionally a Locale object 
   * and a flag which instructs the processor to return errors be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order, OrderManager, and optionally
   *               a Locale object and return errors flag.
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
    java.util.Locale locale = (java.util.Locale) map.get(PipelineConstants.LOCALE);
    Boolean returnErrorsBoolean = (Boolean) map.get(PipelineConstants.RETURNERRORS);
    boolean returnErrors = false;
    java.util.ResourceBundle resourceBundle;
    PaymentGroupRelationship remainingRel;
    Iterator relIter;
    
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    if (returnErrorsBoolean != null)
      returnErrors = returnErrorsBoolean.booleanValue();

    // Check for missing PriceInfos to avoid NullPointerExceptions below.
    // Normally, these checks occur in the validateForCheckout
    // pipeline. That can be reconfigured away, leaving an app developer
    // with insufficient info to understand the NPE here.
    if (order.getPriceInfo() == null) {
      if (returnErrors) {
        pResult.addError("NullOrderPriceInfo", resourceBundle.getString("NullOrderPriceInfo"));
        return SUCCESS;
      }
      else {
        throw new PaymentException(ResourceUtils.getMsgResource("NullOrderPriceInfo",
                                      MY_RESOURCE_NAME, sResourceBundle));
      }
    }
    if (order.getTaxPriceInfo() == null) {
      if (returnErrors) {
        pResult.addError("NullTaxPriceInfo", resourceBundle.getString("NullTaxPriceInfo"));
        return SUCCESS;
      }
      else {
        throw new PaymentException(ResourceUtils.getMsgResource("NullTaxPriceInfo",
                                      MY_RESOURCE_NAME, sResourceBundle));
      }
    }
    Iterator iter = order.getShippingGroups().iterator();
    for (ShippingGroup sg = null; iter.hasNext(); ) {
      sg = (ShippingGroup) iter.next();
      if (sg.getPriceInfo() == null) {
        if (returnErrors) {
          pResult.addError("NullShippingPriceInfo", resourceBundle.getString("NullShippingPriceInfo"));
          return SUCCESS;
        }
        else {
          throw new PaymentException(ResourceUtils.getMsgResource("NullShippingPriceInfo",
                                        MY_RESOURCE_NAME, sResourceBundle));
        }
      }
    }
    iter = order.getCommerceItems().iterator();
    for (CommerceItem item = null; iter.hasNext(); ) {
      item = (CommerceItem) iter.next();
      if (item.getPriceInfo() == null) {
        if (returnErrors) {
          pResult.addError("NullItemPriceInfo", resourceBundle.getString("NullItemPriceInfo"));
          return SUCCESS;
        }
        else {
          throw new PaymentException(ResourceUtils.getMsgResource("NullItemPriceInfo",
                                        MY_RESOURCE_NAME, sResourceBundle));
        }
      }
    }

    // get all the individual totals
    double totalOrderAmount = order.getPriceInfo().getTotal();
    double orderAmountRemaining = totalOrderAmount;

    double totalTaxAmount = order.getTaxPriceInfo().getAmount();
    double taxAmountRemaining = totalTaxAmount;
    
    double totalShippingAmount = 0.0;
    iter = order.getShippingGroups().iterator();
    for (ShippingGroup sg = null; iter.hasNext(); ) {
      sg = (ShippingGroup) iter.next();
      totalShippingAmount += sg.getPriceInfo().getAmount();
    }
    double shippingAmountRemaining = totalShippingAmount;
    
    double totalItemAmount = 0.0;
    iter = order.getCommerceItems().iterator();
    for (CommerceItem item = null; iter.hasNext(); ) {
      item = (CommerceItem) iter.next();
      totalItemAmount += item.getPriceInfo().getAmount();
    }
    double itemAmountRemaining = totalItemAmount;
    
    if (isLoggingDebug()) {
      logDebug("totalOrderAmount = " + totalOrderAmount);
      logDebug("orderAmountRemaining = " + orderAmountRemaining);
      logDebug("totalTaxAmount = " + totalTaxAmount);
      logDebug("taxAmountRemaining = " + taxAmountRemaining);
      logDebug("totalShippingAmount = " + totalShippingAmount);
      logDebug("shippingAmountRemaining = " + shippingAmountRemaining);
      logDebug("totalItemAmount = " + totalItemAmount);
      logDebug("itemAmountRemaining = " + itemAmountRemaining);
    }
    
    // run through all the PaymentGroups setting the amount property to 0.0 and currency code
    String code = order.getPriceInfo().getCurrencyCode();
    iter = order.getPaymentGroups().iterator();
    while (iter.hasNext()) {
      PaymentGroup paymentGroup = (PaymentGroup) iter.next();
      paymentGroup.setAmount(0.0);
      paymentGroup.setCurrencyCode(code);
    }

    // assign the amounts from the PaymentGroupCommerceItemRelationships to the PaymentGroups
    Iterator commerceItemIter = order.getCommerceItems().iterator();
    while (commerceItemIter.hasNext()) {
      CommerceItem commerceItem = (CommerceItem) commerceItemIter.next();

      remainingRel = null;
      double itemAmount = commerceItem.getPriceInfo().getAmount();
      relIter = commerceItem.getPaymentGroupRelationships().iterator();
      while (relIter.hasNext()) {
        PaymentGroupCommerceItemRelationship rel = (PaymentGroupCommerceItemRelationship) relIter.next();
        if (rel.getRelationshipType() == RelationshipTypes.PAYMENTAMOUNTREMAINING) {
          remainingRel = rel;
          continue;
        }
        
        PaymentGroup paymentGroup = rel.getPaymentGroup();
        if (itemAmount > rel.getAmount()) {
          setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + rel.getAmount());
          if (isLoggingDebug())
            logDebug("Added " + rel.getAmount() + " to PaymentGroup " + paymentGroup.getId() +
                    ". Total is " + paymentGroup.getAmount());
          orderAmountRemaining -= rel.getAmount();
          itemAmountRemaining -= rel.getAmount();
          itemAmount -= rel.getAmount();
        }
        else {
          setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + itemAmount);
          if (isLoggingDebug())
            logDebug("Added " + itemAmount + " to PaymentGroup " + paymentGroup.getId() +
                    ". Total is " + paymentGroup.getAmount());
          orderAmountRemaining -= itemAmount;
          itemAmountRemaining -= itemAmount;
          itemAmount = 0.0;
        }
        
        if (Math.abs(itemAmount) <= getMarginOfError())
          break;
        else if (itemAmount < 0.0) {
          if (returnErrors) {
            String msg = resourceBundle.getString("AttemptedToSetInvalidAmount");
            pResult.addError("AttemptedToSetInvalidAmount", msg);
            return SUCCESS;
          }
          else {
            String[] msgArgs = { paymentGroup.getId() };
            throw new PaymentException(ResourceUtils.getMsgResource("AttemptedToSetInvalidAmount",
                                          MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          }
        }
      } // while (relationships)
      
      if (remainingRel != null && itemAmount > getMarginOfError()) {
        PaymentGroup paymentGroup = remainingRel.getPaymentGroup();
        setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + itemAmount);
        remainingRel.setAmount(itemAmount);
        if (isLoggingDebug())
          logDebug("Added " + itemAmount + " to PaymentGroup " + paymentGroup.getId() +
                  ". Total is " + paymentGroup.getAmount());
        orderAmountRemaining -= itemAmount;
        itemAmountRemaining -= itemAmount;
        itemAmount = 0.0;
      }
    } // while (commerceItems)
    
    // assign the amounts from the PaymentGroupShippingGroupRelationships to the PaymentGroups
    Iterator shippingGroupIter = order.getShippingGroups().iterator();
    while (shippingGroupIter.hasNext()) {
      ShippingGroup shippingGroup = (ShippingGroup) shippingGroupIter.next();

      remainingRel = null;
      double shippingAmount = shippingGroup.getPriceInfo().getAmount();
      relIter = shippingGroup.getPaymentGroupRelationships().iterator();
      while (relIter.hasNext()) {
        PaymentGroupShippingGroupRelationship rel = (PaymentGroupShippingGroupRelationship) relIter.next();
        if (rel.getRelationshipType() == RelationshipTypes.SHIPPINGAMOUNTREMAINING) {
          remainingRel = rel;
          continue;
        }
        
        PaymentGroup paymentGroup = rel.getPaymentGroup();
        if (shippingAmount > rel.getAmount()) {
          setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + rel.getAmount());
          if (isLoggingDebug())
            logDebug("Added " + rel.getAmount() + " to PaymentGroup " + paymentGroup.getId() +
                    ". Total is " + paymentGroup.getAmount());
          orderAmountRemaining -= rel.getAmount();
          shippingAmountRemaining -= rel.getAmount();
          shippingAmount -= rel.getAmount();
        }
        else {
          setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + shippingAmount);
          if (isLoggingDebug())
            logDebug("Added " + shippingAmount + " to PaymentGroup " + paymentGroup.getId() +
                    ". Total is " + paymentGroup.getAmount());
          orderAmountRemaining -= shippingAmount;
          shippingAmountRemaining -= shippingAmount;
          shippingAmount = 0.0;
        }
        
        if (Math.abs(shippingAmount) <= getMarginOfError())
          break;
        else if (shippingAmount < 0.0) {
          if (returnErrors) {
            String msg = resourceBundle.getString("AttemptedToSetInvalidAmount");
            pResult.addError("AttemptedToSetInvalidAmount", msg);
            return SUCCESS;
          }
          else {
            String[] msgArgs = { paymentGroup.getId() };
            throw new PaymentException(ResourceUtils.getMsgResource("AttemptedToSetInvalidAmount",
                                          MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          }
        }
      } // while (relationships)
      
      if (remainingRel != null && shippingAmount > getMarginOfError()) {
        PaymentGroup paymentGroup = remainingRel.getPaymentGroup();
        setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + shippingAmount);
        remainingRel.setAmount(shippingAmount);
        if (isLoggingDebug())
          logDebug("Added " + shippingAmount + " to PaymentGroup " + paymentGroup.getId() +
                  ". Total is " + paymentGroup.getAmount());
        orderAmountRemaining -= shippingAmount;
        shippingAmountRemaining -= shippingAmount;
        shippingAmount = 0.0;
      }
    } // while (shippingGroups)

    // assign the tax amounts from the PaymentGroupOrderRelationships to the PaymentGroups
    remainingRel = null;
    double taxAmount = order.getTaxPriceInfo().getAmount();
    relIter = order.getPaymentGroupRelationships().iterator();
    while (relIter.hasNext()) {
      PaymentGroupOrderRelationship rel = (PaymentGroupOrderRelationship) relIter.next();
      if (rel.getRelationshipType() == RelationshipTypes.TAXAMOUNTREMAINING) {
        remainingRel = rel;
        continue;
      }
      else if (rel.getRelationshipType() == RelationshipTypes.TAXAMOUNT) {
        PaymentGroup paymentGroup = rel.getPaymentGroup();
        if (taxAmount > rel.getAmount()) {
          setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + rel.getAmount());
          if (isLoggingDebug())
            logDebug("Added " + rel.getAmount() + " to PaymentGroup " + paymentGroup.getId() +
                    ". Total is " + paymentGroup.getAmount());
          orderAmountRemaining -= rel.getAmount();
          taxAmountRemaining -= rel.getAmount();
          taxAmount -= rel.getAmount();
        }
        else {
          setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + taxAmount);
          if (isLoggingDebug())
            logDebug("Added " + taxAmount + " to PaymentGroup " + paymentGroup.getId() +
                    ". Total is " + paymentGroup.getAmount());
          orderAmountRemaining -= taxAmount;
          taxAmountRemaining -= taxAmount;
          taxAmount = 0.0;
        }
            
        if (Math.abs(taxAmount) <= getMarginOfError())
          break;
        else if (taxAmount < 0.0) {
          if (returnErrors) {
            String msg = resourceBundle.getString("AttemptedToSetInvalidAmount");
            pResult.addError("AttemptedToSetInvalidAmount", msg);
            return SUCCESS;
          }
          else {
            String[] msgArgs = { paymentGroup.getId() };
            throw new PaymentException(ResourceUtils.getMsgResource("AttemptedToSetInvalidAmount",
                                          MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          }
        }
      }
      else { // ignore ORDER relationship types
        continue;
      }
    } // while (relationships)
      
    if (remainingRel != null && taxAmount > getMarginOfError()) {
      PaymentGroup paymentGroup = remainingRel.getPaymentGroup();
      setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + taxAmount);
      remainingRel.setAmount(taxAmount);
      if (isLoggingDebug())
        logDebug("Added " + taxAmount + " to PaymentGroup " + paymentGroup.getId() +
                ". Total is " + paymentGroup.getAmount());
      orderAmountRemaining -= taxAmount;
      taxAmountRemaining -= taxAmount;
      taxAmount = 0.0;
    }

    // assign the order amounts from the PaymentGroupOrderRelationships to the PaymentGroups
    remainingRel = null;
    double orderAmount = orderAmountRemaining;
    relIter = order.getPaymentGroupRelationships().iterator();
    while (relIter.hasNext()) {
      PaymentGroupOrderRelationship rel = (PaymentGroupOrderRelationship) relIter.next();
      if (rel.getRelationshipType() == RelationshipTypes.ORDERAMOUNTREMAINING) {
        remainingRel = rel;
        continue;
      }
      else if (rel.getRelationshipType() == RelationshipTypes.ORDERAMOUNT) {
        PaymentGroup paymentGroup = rel.getPaymentGroup();
        if (orderAmount > rel.getAmount()) {
          setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + rel.getAmount());
          if (isLoggingDebug())
            logDebug("Added " + rel.getAmount() + " to PaymentGroup " + paymentGroup.getId() +
                    ". Total is " + paymentGroup.getAmount());
          orderAmountRemaining -= rel.getAmount();
          orderAmount -= rel.getAmount();
        }
        else {
          setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + orderAmount);
          if (isLoggingDebug())
            logDebug("Added " + orderAmount + " to PaymentGroup " + paymentGroup.getId() +
                    ". Total is " + paymentGroup.getAmount());
          orderAmountRemaining -= orderAmount;
          orderAmount = 0.0;
        }
            
        if (Math.abs(orderAmount) <= 0.0)
          break;
        else if (orderAmount < 0.0) {
          if (returnErrors) {
            String msg = resourceBundle.getString("AttemptedToSetInvalidAmount");
            pResult.addError("AttemptedToSetInvalidAmount", msg);
            return SUCCESS;
          }
          else {
            String[] msgArgs = { paymentGroup.getId() };
            throw new PaymentException(ResourceUtils.getMsgResource("AttemptedToSetInvalidAmount",
                                          MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          }
        }
      }
      else { // ignore TAX relationship types
        continue;
      }
    } // while (relationships)
      
    if (remainingRel != null && orderAmount > getMarginOfError()) {
      PaymentGroup paymentGroup = remainingRel.getPaymentGroup();
      setPaymentGroupAmount(paymentGroup, paymentGroup.getAmount() + orderAmount);
      remainingRel.setAmount(orderAmount);
      if (isLoggingDebug())
        logDebug("Added " + orderAmount + " to PaymentGroup " + paymentGroup.getId() +
                ". Total is " + paymentGroup.getAmount());
      orderAmountRemaining -= orderAmount;
      orderAmount = 0.0;
    }

    // final code
    if (isLoggingDebug()) {
      logDebug("orderAmountRemaining = " + orderAmountRemaining);
      logDebug("taxAmountRemaining = " + taxAmountRemaining);
      logDebug("shippingAmountRemaining = " + shippingAmountRemaining);
      logDebug("itemAmountRemaining = " + itemAmountRemaining);
      iter = order.getPaymentGroups().iterator();
      while (iter.hasNext()) {
        PaymentGroup paymentGroup = (PaymentGroup) iter.next();
        logDebug("PaymentGroup " + paymentGroup.getId() + " amount is " + paymentGroup.getAmount());
      }
    }

    if (Math.abs(orderAmountRemaining) >= getMarginOfError()) {
      if (returnErrors) {
        String msg = resourceBundle.getString("PaymentGroupCalculationsIncorrect");
        pResult.addError("PaymentGroupCalculationsIncorrect", MessageFormat.format(msg, order.getId()));
        return SUCCESS;
      }
      else {
        throw new PaymentException(ResourceUtils.getMsgResource("PaymentGroupCalculationsIncorrect",
                                      MY_RESOURCE_NAME, sResourceBundle, order.getId()));
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

  /**
   * Sets the payment group amount to the given value.  The value is always rounded first.
   * 
   * @param pPaymentGroup The payment whose amount is being set
   * @param pAmount The new amount
   */
  private void setPaymentGroupAmount(PaymentGroup pPaymentGroup, double pAmount)
  {
    double newPGAmount = 0.0; 
    
    if(isLoggingDebug())
      logDebug("Rounding " + pAmount);
      
    newPGAmount = getPricingTools().round(pAmount);
    pPaymentGroup.setAmount(newPGAmount);
  }
}
