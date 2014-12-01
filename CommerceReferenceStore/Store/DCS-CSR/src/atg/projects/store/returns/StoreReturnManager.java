/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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
package atg.projects.store.returns;

import java.util.Collection;
import java.util.Iterator;

import atg.commerce.csr.returns.ReturnItem;
import atg.commerce.csr.returns.ReturnManager;
import atg.commerce.csr.returns.ReturnRequest;
import atg.commerce.csr.returns.ItemCostAdjustment;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.pricing.TaxPriceInfo;
import atg.core.util.Range;
import atg.projects.store.pricing.StoreShippingPriceInfo;

/**
 * This class extends CSC's ReturnManager to include shipping tax in total tax refund
 * for the return item.
 *  
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/DCS-CSR/src/atg/projects/store/returns/StoreReturnManager.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class StoreReturnManager extends ReturnManager{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/DCS-CSR/src/atg/projects/store/returns/StoreReturnManager.java#2 $$Change: 651448 $";

 
  /**
   * Overrides base method to add shipping tax refund to the total tax refund
   * for the given return item.
   */
  protected double calculateTaxRefundForItem(ReturnRequest pReturnRequest,
      ReturnItem pReturnItem, Range pReturnRange) {
    
    double taxRefund =  super.calculateTaxRefundForItem(pReturnRequest, pReturnItem,
        pReturnRange);
        
    taxRefund += getShippingTaxRefundForItem(pReturnRequest, pReturnItem);
    
    return taxRefund;
  }


  /**
   * Calculates shipping tax refund for the given return item.
   * 
   * @param pReturnRequest return request
   * @param pReturnItem return item
   * @return shipping tax refund amount for the return item
   */
  protected double getShippingTaxRefundForItem(ReturnRequest pReturnRequest, ReturnItem pReturnItem) {
    double totalShippingTaxRefund = 0.0;
    
    // Iterate through item cost adjustments
    Collection itemCostAdjustments = pReturnItem.getItemCostAdjustments();
    Iterator adjusterator = itemCostAdjustments.iterator();    
    while(adjusterator.hasNext()){
      ItemCostAdjustment ica = (ItemCostAdjustment)adjusterator.next();
      
      // obtain shipping group that corresponds the current item cost adjustment
      String sgId = ica.getShippingGroupId();
      ShippingGroup sg;
      try {
        sg = pReturnRequest.getOrder().getShippingGroup(sgId);
      
      // retrieve taxPriceInfo from the shipping group's price info
      TaxPriceInfo taxPriceInfo = ((StoreShippingPriceInfo)sg.getPriceInfo()).getTaxPriceInfo();
      if (taxPriceInfo == null){
        // there no shipping TaxPriceInfo, nothing to return
        continue;
      }
      
      // shipping tax is specified so calculate the amount to return
      double shippingShareAdjustment = ica.getShippingShareAdjustment();
      double totalShippingCostForSG = sg.getPriceInfo().getAmount();
      double totalShippingTax = taxPriceInfo.getAmount();
      double ratio = shippingShareAdjustment / totalShippingCostForSG;
      
      if (Double.isNaN(ratio)){
        ratio = 0.0;
      }
      double shippingTaxRefund = totalShippingTax * ratio;
      shippingTaxRefund = getPricingTools().round(shippingTaxRefund);
      
      // add the shipping tax refund for the current item cost adjustment to the total item's 
      // shipping tax refund
      totalShippingTaxRefund += shippingTaxRefund;
      } catch (ShippingGroupNotFoundException ex) {
        if(isLoggingError())
          logError(ex);
     } catch (InvalidParameterException ex) {
       if(isLoggingError())
         logError(ex);
     }
    }
    
    if(Double.isNaN(totalShippingTaxRefund) || Double.isInfinite(totalShippingTaxRefund))
      totalShippingTaxRefund = 0.0;

    //flip the sign to deal with the reverse way in which ReturnItems store refund value, + = credit, - = debit)
    if(totalShippingTaxRefund != 0)
      totalShippingTaxRefund = -totalShippingTaxRefund;
    
    return totalShippingTaxRefund;
  }

}
