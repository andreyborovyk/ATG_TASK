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

package atg.b2bcommerce.order.processor;

import atg.service.pipeline.*;
import atg.b2bcommerce.order.*;
import atg.commerce.order.*;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.commerce.CommerceException;

import java.text.MessageFormat;
import java.util.*;

/**
 * This processor determines the values that need to be set into the amount field of
 * a CostCenter. This processor iterates through all the Relationships determining
 * the amounts to set into the CostCenters based on the Relationship type. Any amounts
 * set in the amount property of a CostCenter will be overwritten here.
 *
 * @author Paul O'Brien
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/processor/ProcSetCostCenterAmount.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.b2bcommerce.order.CostCenter
 */
public class ProcSetCostCenterAmount extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/processor/ProcSetCostCenterAmount.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String B2B_RESOURCE_NAME = "atg.b2bcommerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";
  static final String B2B_USER_MSGS_RES_NAME = "atg.b2bcommerce.order.UserMessages";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static java.util.ResourceBundle sB2BResourceBundle = java.util.ResourceBundle.getBundle(B2B_RESOURCE_NAME, java.util.Locale.getDefault());
  private static java.util.ResourceBundle sUserResourceBundle = java.util.ResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());
  private static java.util.ResourceBundle sB2BUserResourceBundle = java.util.ResourceBundle.getBundle(B2B_USER_MSGS_RES_NAME, java.util.Locale.getDefault());
  
  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcSetCostCenterAmount() {
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
  String mLoggingIdentifier = "ProcSetCostCenterAmount";

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
   * This method determines the amount that needs to be set into the amount field of
   * a CostCenter. This processor iterates through all the Relationships determining
   * the amounts to set into the CostCenters based on the Relationship type. Any amounts
   * set in the amount property of a CostCenter will be overwritten here.
   *
   * This method can be executed in one of two modes, return an error in pResult, or throw
   * an exception when the first error is encountered. The default mode is to throw
   * exceptions. To change it, add a RETURNERRORS entry into pParam with the value set
   * to a Boolean object with a value of true.
   *
   * This method requires that an Order, CostCenterManager, and optionally a Locale object 
   * and a flag which instructs the processor to return errors be supplied
   * in pParam in a HashMap. Use the B2BPipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order, CostCenterManager, and optionally
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
    B2BOrder order = (B2BOrder) map.get(B2BPipelineConstants.ORDER);
    java.util.Locale locale = (java.util.Locale) map.get(B2BPipelineConstants.LOCALE);
    Boolean returnErrorsBoolean = (Boolean) map.get(B2BPipelineConstants.RETURNERRORS);
    boolean returnErrors = false;
    java.util.ResourceBundle resourceBundle;
    java.util.ResourceBundle b2bResourceBundle;
    CostCenterRelationship remainingRel;
    Iterator relIter;
    
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = java.util.ResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (locale == null)
      b2bResourceBundle = sB2BUserResourceBundle;
    else
      b2bResourceBundle = java.util.ResourceBundle.getBundle(B2B_USER_MSGS_RES_NAME, locale);

    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    if (returnErrorsBoolean != null)
      returnErrors = returnErrorsBoolean.booleanValue();

    // get all the individual totals
    double totalOrderAmount = order.getPriceInfo().getTotal();
    double orderAmountRemaining = totalOrderAmount;

    double totalTaxAmount = order.getTaxPriceInfo().getAmount();
    double taxAmountRemaining = totalTaxAmount;
    
    double totalShippingAmount = 0.0;
    Iterator iter = order.getShippingGroups().iterator();
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
    
    // run through all the CostCenters setting the amount property to 0.0 and currency code
    iter = order.getCostCenters().iterator();
    while (iter.hasNext()) {
      CostCenter costCenter = (CostCenter) iter.next();
      costCenter.setAmount(0.0);
    }

    // assign the amounts from the CostCenterCommerceItemRelationships to the CostCenters
    Iterator commerceItemIter = order.getCommerceItems().iterator();
    while (commerceItemIter.hasNext()) {
      B2BCommerceItem commerceItem = (B2BCommerceItem) commerceItemIter.next();
      
      remainingRel = null;
      double itemAmount = commerceItem.getPriceInfo().getAmount();
      double fullItemAmount = itemAmount;
      long itemQuantity = commerceItem.getQuantity();

      relIter = commerceItem.getCostCenterRelationships().iterator();
      while (relIter.hasNext()) {
	CostCenterCommerceItemRelationship rel = (CostCenterCommerceItemRelationship) relIter.next();
        CostCenter costCenter = rel.getCostCenter();
	if (rel.getRelationshipType() == B2BRelationshipTypes.CCAMOUNTREMAINING) {
          remainingRel = rel;
          continue;
        }
        else if (rel.getRelationshipType() == B2BRelationshipTypes.CCQUANTITYREMAINING) {
	  costCenter.setAmount(costCenter.getAmount() + itemAmountRemaining);
	  remainingRel = rel;
	  continue;
	}
	else if (rel.getRelationshipType() == B2BRelationshipTypes.CCQUANTITY) {
	  rel.setAmount((fullItemAmount * rel.getQuantity()) / itemQuantity);
	  costCenter.setAmount(rel.getAmount());
          orderAmountRemaining -= rel.getAmount();
          itemAmountRemaining -= rel.getAmount();
          itemAmount -= rel.getAmount();
	  continue;
	}

	if (itemAmount > rel.getAmount()) {
	  costCenter.setAmount(costCenter.getAmount() + rel.getAmount());
          if (isLoggingDebug())
            logDebug("Added " + rel.getAmount() + " to CostCenter " + costCenter.getId() +
                    ". Total is " + costCenter.getAmount());
          orderAmountRemaining -= rel.getAmount();
          itemAmountRemaining -= rel.getAmount();
          itemAmount -= rel.getAmount();
        }
        else {
          costCenter.setAmount(costCenter.getAmount() + itemAmount);
          if (isLoggingDebug())
            logDebug("Added " + itemAmount + " to CostCenter " + costCenter.getId() +
                    ". Total is " + costCenter.getAmount());
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
            String[] msgArgs = { costCenter.getId() };
            throw new CommerceException(ResourceUtils.getMsgResource("AttemptedToSetInvalidAmount",
                                          MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          }
        }
      } // while (relationships)
      
      if (remainingRel != null && itemAmount > getMarginOfError()) {
        CostCenter costCenter = remainingRel.getCostCenter();
        costCenter.setAmount(costCenter.getAmount() + itemAmount);
        if (isLoggingDebug())
          logDebug("Added " + itemAmount + " to CostCenter " + costCenter.getId() +
                  ". Total is " + costCenter.getAmount());
        orderAmountRemaining -= itemAmount;
        itemAmountRemaining -= itemAmount;
        itemAmount = 0.0;
      }
    } // while (commerceItems)
    
    // assign the amounts from the CostCenterShippingGroupRelationships to the CostCenters
    Iterator shippingGroupIter = order.getShippingGroups().iterator();
    while (shippingGroupIter.hasNext()) {
      B2BShippingGroup shippingGroup = (B2BShippingGroup) shippingGroupIter.next();

      remainingRel = null;
      double shippingAmount = shippingGroup.getPriceInfo().getAmount();
      relIter = shippingGroup.getCostCenterRelationships().iterator();
      while (relIter.hasNext()) {
        CostCenterShippingGroupRelationship rel = (CostCenterShippingGroupRelationship) relIter.next();
        if (rel.getRelationshipType() == B2BRelationshipTypes.CCSHIPPINGAMOUNTREMAINING) {
	  remainingRel = rel;
          continue;
        }
        
	CostCenter costCenter = rel.getCostCenter();
	if (shippingAmount > rel.getAmount()) {
          costCenter.setAmount(costCenter.getAmount() + rel.getAmount());
          if (isLoggingDebug())
            logDebug("Added " + rel.getAmount() + " to CostCenter " + costCenter.getId() +
                    ". Total is " + costCenter.getAmount());
          orderAmountRemaining -= rel.getAmount();
          shippingAmountRemaining -= rel.getAmount();
          shippingAmount -= rel.getAmount();
        }
        else {
          costCenter.setAmount(costCenter.getAmount() + shippingAmount);
          if (isLoggingDebug())
            logDebug("Added " + shippingAmount + " to CostCenter " + costCenter.getId() +
                    ". Total is " + costCenter.getAmount());
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
            String[] msgArgs = { costCenter.getId() };
            throw new CommerceException(ResourceUtils.getMsgResource("AttemptedToSetInvalidAmount",
                                          MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          }
        }
      } // while (relationships)
      
      if (remainingRel != null && shippingAmount > getMarginOfError()) {
        CostCenter costCenter = remainingRel.getCostCenter();
        costCenter.setAmount(costCenter.getAmount() + shippingAmount);
        if (isLoggingDebug())
          logDebug("Added " + shippingAmount + " to CostCenter " + costCenter.getId() +
                  ". Total is " + costCenter.getAmount());
        orderAmountRemaining -= shippingAmount;
        shippingAmountRemaining -= shippingAmount;
        shippingAmount = 0.0;
      }
    } // while (shippingGroups)

    // assign the tax amounts from the CostCenterOrderRelationships to the CostCenters
    remainingRel = null;
    double taxAmount = order.getTaxPriceInfo().getAmount();
    relIter = order.getCostCenterRelationships().iterator();
    while (relIter.hasNext()) {
      CostCenterOrderRelationship rel = (CostCenterOrderRelationship) relIter.next();
      if (rel.getRelationshipType() == B2BRelationshipTypes.CCTAXAMOUNTREMAINING) {
        remainingRel = rel;
        continue;
      }
      else if (rel.getRelationshipType() == B2BRelationshipTypes.CCTAXAMOUNT) {
        CostCenter costCenter = rel.getCostCenter();
        if (taxAmount > rel.getAmount()) {
          costCenter.setAmount(costCenter.getAmount() + rel.getAmount());
          if (isLoggingDebug())
            logDebug("Added " + rel.getAmount() + " to CostCenter " + costCenter.getId() +
                    ". Total is " + costCenter.getAmount());
          orderAmountRemaining -= rel.getAmount();
          taxAmountRemaining -= rel.getAmount();
          taxAmount -= rel.getAmount();
        }
        else {
          costCenter.setAmount(costCenter.getAmount() + taxAmount);
          if (isLoggingDebug())
            logDebug("Added " + taxAmount + " to CostCenter " + costCenter.getId() +
                    ". Total is " + costCenter.getAmount());
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
            String[] msgArgs = { costCenter.getId() };
            throw new CommerceException(ResourceUtils.getMsgResource("AttemptedToSetInvalidAmount",
                                          MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          }
        }
      }
      else { // ignore ORDER relationship types
        continue;
      }
    } // while (relationships)
      
    if (remainingRel != null && taxAmount > getMarginOfError()) {
      CostCenter costCenter = remainingRel.getCostCenter();
      costCenter.setAmount(costCenter.getAmount() + taxAmount);
      if (isLoggingDebug())
        logDebug("Added " + taxAmount + " to CostCenter " + costCenter.getId() +
                ". Total is " + costCenter.getAmount());
      orderAmountRemaining -= taxAmount;
      taxAmountRemaining -= taxAmount;
      taxAmount = 0.0;
    }

    // assign the order amounts from the CostCenterOrderRelationships to the CostCenter
    remainingRel = null;
    double orderAmount = orderAmountRemaining;
    relIter = order.getCostCenterRelationships().iterator();
    while (relIter.hasNext()) {
      CostCenterOrderRelationship rel = (CostCenterOrderRelationship) relIter.next();
      if (rel.getRelationshipType() == B2BRelationshipTypes.CCORDERAMOUNTREMAINING) {
        remainingRel = rel;
        continue;
      }
      else if (rel.getRelationshipType() == B2BRelationshipTypes.CCORDERAMOUNT) {
        CostCenter costCenter = rel.getCostCenter();
        if (orderAmount > rel.getAmount()) {
          costCenter.setAmount(costCenter.getAmount() + rel.getAmount());
          if (isLoggingDebug())
            logDebug("Added " + rel.getAmount() + " to CostCenter " + costCenter.getId() +
                    ". Total is " + costCenter.getAmount());
          orderAmountRemaining -= rel.getAmount();
          orderAmount -= rel.getAmount();
        }
        else {
          costCenter.setAmount(costCenter.getAmount() + orderAmount);
          if (isLoggingDebug())
            logDebug("Added " + orderAmount + " to CostCenter " + costCenter.getId() +
                    ". Total is " + costCenter.getAmount());
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
            String[] msgArgs = { costCenter.getId() };
            throw new CommerceException(ResourceUtils.getMsgResource("AttemptedToSetInvalidAmount",
                                          MY_RESOURCE_NAME, sResourceBundle, msgArgs));
          }
        }
      }
      else { // ignore TAX relationship types
        continue;
      }
    } // while (relationships)
      
    if (remainingRel != null && orderAmount > getMarginOfError()) {
      CostCenter costCenter = remainingRel.getCostCenter();
      costCenter.setAmount(costCenter.getAmount() + orderAmount);
      if (isLoggingDebug())
        logDebug("Added " + orderAmount + " to CostCenter " + costCenter.getId() +
                ". Total is " + costCenter.getAmount());
      orderAmountRemaining -= orderAmount;
      orderAmount = 0.0;
    }

    // final code
    if (isLoggingDebug()) {
      logDebug("orderAmountRemaining = " + orderAmountRemaining);
      logDebug("taxAmountRemaining = " + taxAmountRemaining);
      logDebug("shippingAmountRemaining = " + shippingAmountRemaining);
      logDebug("itemAmountRemaining = " + itemAmountRemaining);
      iter = order.getCostCenters().iterator();
      while (iter.hasNext()) {
        CostCenter costCenter = (CostCenter) iter.next();
        logDebug("CostCenter " + costCenter.getId() + " amount is " + costCenter.getAmount());
      }
    }

    /*if (Math.abs(orderAmountRemaining) >= getMarginOfError()) {
      String[] msgArgs = { order.getId() };
      if (returnErrors) {
        String msg = sB2BResourceBundle.getString("CostCenterCalculationsIncorrect");
        pResult.addError("CostCenterCalculationsIncorrect", MessageFormat.format(msg, msgArgs));
        return SUCCESS;
      }
      else {
        throw new CommerceException(ResourceUtils.getMsgResource("CostCenterCalculationsIncorrect",
                                      B2B_RESOURCE_NAME, sB2BResourceBundle, msgArgs));
      }
      }*/
    
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
