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

import atg.commerce.order.*;
import atg.b2bcommerce.order.*;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;

import java.util.*;
import java.text.MessageFormat;

/**
 * This processor validates CostCenters before checking an Order out.  If
 * <code>costCenterRequired</code> is set to true, this processor verifies
 * that all CommerceItems, shipping costs, and tax in the Order are assigned
 * to CostCenters.  The processor can also be configured to run a specified
 * pipeline chain to validate the properties of each cost center in turn.
 *
 * @author Paul O'Brien, Matt Landau
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/processor/ProcValidateCostCentersForCheckout.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcValidateCostCentersForCheckout extends ApplicationLoggingImpl implements PipelineProcessor
{  
  public static String CLASS_VERSION =
      "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/processor/ProcValidateCostCentersForCheckout.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String B2B_RESOURCE_NAME = "atg.b2bcommerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";
  static final String B2B_USER_MSGS_RES_NAME = "atg.b2bcommerce.order.UserMessages";

  /** Resource Bundle **/
  private static ResourceBundle sResourceBundle =
      ResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static ResourceBundle sB2BResourceBundle =
      ResourceBundle.getBundle(B2B_RESOURCE_NAME, Locale.getDefault());
  private static ResourceBundle sUserResourceBundle =
      ResourceBundle.getBundle(USER_MSGS_RES_NAME, Locale.getDefault());
  private static ResourceBundle sB2BUserResourceBundle =
      ResourceBundle.getBundle(B2B_USER_MSGS_RES_NAME, Locale.getDefault());

  private final int SUCCESS = 1;
  
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

  //---------------------------------------------------------------------------
  // property: validateCostCenterChain
  //---------------------------------------------------------------------------

  String mValidateCostCenterChain = "validateCostCenter";

  /**
   * Set the name of the pipeline chain to run to validate each individual
   * cost center.  If the chain name is set to null or the named chain does
   * not exist, no per-cost-center validation is performed.  By default the
   * chain name is set to "validateCostCenter", but the chain itself is not
   * defined.
   **/
  
  public void setValidateCostCenterChain(String pValidateCostCenterChain) {
    mValidateCostCenterChain = pValidateCostCenterChain;
  }

  /**
   * Get the name of the pipeline chain to run to validate each individual
   * cost center.  
   **/

  public String getValidateCostCenterChain() {
    return mValidateCostCenterChain;
  }

  //---------------------------------------------------------------------------
  // property: LoggingIdentifier
  //---------------------------------------------------------------------------

  String mLoggingIdentifier = "ProcValidateCostCentersForCheckout";

  /** Sets property LoggingIdentifier. **/
  
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /** Returns property LoggingIdentifier. **/
  
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //---------------------------------------------------------------------------
  // property: CostCenterRequired
  //---------------------------------------------------------------------------

  boolean mCostCenterRequired = false;

  /**
   * Specify whether all items must be assigned a cost center, or whether
   * cost centers are optional.
   **/
  
  public void setCostCenterRequired(boolean pCostCenterRequired) {
    mCostCenterRequired = pCostCenterRequired;
  }

  /**
   * Query whether all items must be assigned a cost center, or whether
   * cost centers are optional.
   **/
  
  public boolean isCostCenterRequired() {
    return mCostCenterRequired;
  }

  //---------------------------------------------------------------------------

  /**
   * When <code>costCenterRequired</code> is true, this method checks to see
   * if all the CommerceItems, shipping costs, and tax in the Order are
   * assigned to CostCenters.  It can also run an optional pipeline chain
   * to validate the properties of each individual cost center.
   * <p>
   * This method requires that an Order, an OrderManager, and optionally a Locale
   * object be supplied in pParam in a HashMap. Use the B2BPipelineConstants class
   * static members to key the objects in the HashMap.
   *
   * @param pParam
   *    a HashMap which must contain an Order, an OrderManager, and optionally
   *    a Locale object
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
    B2BOrder order = (B2BOrder) map.get(B2BPipelineConstants.ORDER);
    OrderManager om = (OrderManager)map.get(B2BPipelineConstants.ORDERMANAGER);
    Locale locale = (Locale)map.get(B2BPipelineConstants.LOCALE);
    ResourceBundle resourceBundle;
    ResourceBundle b2bResourceBundle;

    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = ResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (locale == null)
      b2bResourceBundle = sB2BUserResourceBundle;
    else
      b2bResourceBundle = ResourceBundle.getBundle(B2B_USER_MSGS_RES_NAME, locale);

    if (order == null)
      throw new InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidOrderParameter", RESOURCE_NAME, sResourceBundle));

    // First test the simple case where cost centers are optional.  In this
    // case all we have to do is call the per-cost-center validation chain;
    // we don't have to make sure that every item is allocated to a cost
    // center

    if (!isCostCenterRequired())
    {
      if (isLoggingDebug())
        logDebug("Skipping detailed cost center validation.  Checking pipeline chain.");
      validateCostCenters(order, om, locale, pResult);
      return SUCCESS;
    }

    // Otherwise we must make sure that every commerce item, the shipping
    // charges, and the tax are all assigned to a cost center before we run
    // the per-cost-center validation chain.
    
    CostCenterCommerceItemRelationship rel;
    B2BCommerceItem item;
    CostCenter cc;
    ArrayList badItems = new ArrayList(7);
    Iterator iter, iter2;
    long relTotalQty;
    double relTotalAmt, itemAmt, orderAmountAmount;
    int relType;
    boolean accountedFor = false;
    boolean detailCheck = true;
    
    // the special case, if there is only one costCenter that has no
    // Relationships, then all the items, tax, and shipping costs
    // implicitly belong to that costCenter

    if (order.getCostCenterCount() == 1) {
      cc = (CostCenter) order.getCostCenters().get(0);
      if (cc.getCommerceItemRelationshipCount() == 0 &&
          cc.getShippingGroupRelationshipCount() == 0 &&
          cc.getOrderRelationshipCount() == 0)
        detailCheck = false;
    }
    
    // if there is a CostCenterOrderRelationship then check to see if it covers
    // the amount of the entire order for payment

    if (detailCheck && order.getCostCenterRelationshipCount() > 0) {
      CostCenterOrderRelationship orel;
      orderAmountAmount = 0.0;
      for (iter = order.getCostCenterRelationships().iterator(); iter.hasNext(); ) {
        orel = (CostCenterOrderRelationship) iter.next();
        switch (orel.getRelationshipType()) {
          case B2BRelationshipTypes.CCORDERAMOUNTREMAINING:
            detailCheck = false;
          break;
          
          case B2BRelationshipTypes.CCORDERAMOUNT:
            orderAmountAmount += orel.getAmount();
            if (orderAmountAmount >= order.getPriceInfo().getAmount())
              detailCheck = false;
          break;
        }
        
        if (detailCheck == false)
          break;
      }
    }
    
    if (detailCheck)
    {
      // the standard case, if any Relationships exist in any costCenters then we must check to see
      // if all the items are accounted for
      iter = order.getCommerceItems().iterator();
      while (iter.hasNext()) {
        item = (B2BCommerceItem) iter.next();
        accountedFor = false;
        relTotalQty = 0;
        relTotalAmt = 0.0;
        itemAmt = item.getPriceInfo().getAmount();

        iter2 = item.getCostCenterRelationships().iterator();
        while (iter2.hasNext()) {
          rel = (CostCenterCommerceItemRelationship) iter2.next();
          relType = rel.getRelationshipType();
          
          switch (relType) {
            case B2BRelationshipTypes.CCQUANTITYREMAINING:
              // if this is a remaining relationship then this item is accounted for
              accountedFor = true;
            break;
            case B2BRelationshipTypes.CCQUANTITY:
              // if this is a quantity relationship then add the rel's qty to the counter and
              // check to see if the entire qty is accounted for
              relTotalQty += rel.getQuantity();
              if (relTotalQty >= item.getQuantity())
                accountedFor = true;
            break;
          } // switch
          
          if (accountedFor)
            break;
        } // while
        
        if (accountedFor == false)
          badItems.add(item.getId());
      } // while
      
      if (badItems.size() > 0) {
        String arg = new String();
        boolean first = true;
        for (iter = badItems.iterator(); iter.hasNext(); ) {
          if (first)
            first = false;
          else
            arg += ", ";
          arg += (String) iter.next();
        }
        String msg = b2bResourceBundle.getString("CostCenterNotGivenForAllItems");
        pResult.addError(B2BPipelineConstants.VALIDATECOSTCENTERSFAILED, MessageFormat.format(msg, arg));
      }
    }

    validateCostCenters(order, om, locale, pResult);
    return SUCCESS;
  }

  //---------------------------------------------------------------------------

  /**
   * Run the optional cost center validation chain, if one exists, for
   * each non-empty cost center.
   *
   * @pOrder
   *    The order whose cost centers are to be validated.
   * @pOrderManager
   *    The order manager whose pipeline manager is used to run the
   *    validation pipeline chain.
   * @pLocale
   *    The locale to use when looking up error messages.
   * @pResult
   *    The pipeline result object into which any pipeline errors
   *    from the validation chain will be copied.
   **/
  
  protected void validateCostCenters(
    B2BOrder pOrder, OrderManager pOrderManager, Locale pLocale, PipelineResult pResult)
    throws RunProcessException
  {
    // This section iterates over all cost centers and invokes a pipeline
    // chain to validate each cost center in turn.

    String chainName = getValidateCostCenterChain();
    PipelineManager pmgr = pOrderManager.getPipelineManager();
    
    if (chainName == null) {
      if (isLoggingDebug())
        logDebug("Cost center validation chain not specified.");
      return;
    }
    
    if (!pmgr.chainExists(chainName)) {
      if (isLoggingDebug())
        logDebug("Cost center validation chain " + chainName + " undefined.");
      return;
    }
    
    Iterator costCenters = pOrder.getCostCenters().iterator();
    ValidateCostCenterPipelineArgs param = new ValidateCostCenterPipelineArgs();
    PipelineResult newResult;
    CostCenter cc;
    
    param.setOrder(pOrder);
    param.setOrderManager(pOrderManager);
    if (pLocale != null)
      param.setLocale(pLocale);

    while (costCenters.hasNext())
    {
      cc = (CostCenter)costCenters.next();

      // Skip empty cost centers

      if (pOrder.getCostCenterCount() > 1
          && cc.getOrderRelationshipCount() == 0
          && cc.getCommerceItemRelationshipCount() == 0
          && cc.getShippingGroupRelationshipCount() == 0)
        continue;

      param.setCostCenter(cc);

      newResult = pmgr.runProcess(chainName, param);
      if (newResult.hasErrors())
        pResult.copyInto(newResult);
    }
  }
}
