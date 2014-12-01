/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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
package atg.projects.store.pricing;

import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;

import atg.commerce.pricing.*;
import atg.core.util.Range;
import atg.core.util.StringUtils;

import atg.integrations.cybersource.CyberSourceStatus;

import atg.payment.tax.TaxStatus;

import com.cybersource.ics.base.message.ICSReply;

import java.text.MessageFormat;

import java.util.List;


/**
 * This class is an extension of the ATG
 * AddressVerificationTaxProcessorTaxCalculator. It will add the assignment of
 * item TaxPriceInfos to the StoreItemPriceInfo and shipping group's TaxPriceInfos to the
 * StoreShippingPriceInfo based on the CyberSource reply.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Cybersource/src/atg/projects/store/pricing/StoreTaxProcessorTaxCalculator.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 */
public class StoreTaxProcessorTaxCalculator extends AddressVerificationTaxProcessorTaxCalculator {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Cybersource/src/atg/projects/store/pricing/StoreTaxProcessorTaxCalculator.java#3 $$Change: 635816 $";

  /**
   * Key separator used in CyberSource reply.
   */
  public static final String KEY_SEPARATOR = "_";

  /**
   * This method is overridden to assign the TaxPriceInfo objects to the
   * StoreItemPriceInfo and StoreShippingPriceInfo objects. ICSReply contains
   * tax details per each offer where each ShippingGroupCommerceItemRelationship's amount and
   * each shipping group's amount are considered as separate offers.
   * To get taxes for ShippingGroupCommerceItemRelationship it is needed to retrieve entries from
   * ICSReply that have the following suffix: {relIndex}_{sgIndex}, where {relIndex} - the index
   * of ShippingGroupCommerceItemRelationship in shipping group and {sgIndex} - the index of shipping
   * group. Retrieved taxes are assigned to corresponding commerce item's taxPriceInfo object.
   * The tax field of commerce items pricing details are also populated with corresponding tax value.
   * For that purpose the total tax amount for commerce item range in relationship is divided among
   * the pricing details for that commerce item range based on the each particular detail's amount.  
   * 
   * To get taxes for shipping cost it is needed to retrieve entries from
   * ICSReply that have the following suffix: {relSize}_{sgIndex}, where {relSize} - the number
   * of ShippingGroupCommerceItemRelationships in shipping group and {sgIndex} - the index of the shipping
   * group. Retrieved taxes are assigned to corresponding shipping group's taxPriceInfo object.
   *
   * @param pOrder - order
   * @param pOrderTax - order tax
   * @param pStatus - status
   * @throws PricingException if error occurs
   */
  protected void assignItemTaxAmounts(Order pOrder, TaxPriceInfo pOrderTax, TaxStatus pStatus)
    throws PricingException {
    CyberSourceStatus status = (CyberSourceStatus) pStatus;
    ICSReply icsReply = status.getValues();

    if (icsReply == null) {
      // This is confusing, because this will occur when tax
      // is $0.0, although it seems like it should be an
      // error condition.
      if (isLoggingDebug()) {
        logDebug("Cybersource returned with " + "null response. Tax is $0.0.");
      }

      return;
    }

    if (isLoggingDebug()) {
      logDebug("Assign tax using order tax");
    }

    if (pOrder == null) {
      return;
    }

    if (pOrderTax == null) {
      return;
    }
    
    // assign zero-cost tax infos to all commerce items
    initializeItemTaxPriceInfos(pOrder);
    
    List shippingGroups = pOrder.getShippingGroups();
    
    // loop through shipping groups in order
    for (int sgIndex = 0; sgIndex<shippingGroups.size(); sgIndex++){
      ShippingGroup sg = (ShippingGroup) shippingGroups.get(sgIndex);
      
      //get ShippingGroupCommerceItemRelationships
      List rels = sg.getCommerceItemRelationships();
      
      // construct shippingOfferKey as {relSize}_{sgIndex}, where {relSize} - the number
      // of ShippingGroupCommerceItemRelationships in shipping group and {sgIndex} - the index of 
      // the shipping group.
      String shippingOfferKey = Integer.toString(rels.size())+ KEY_SEPARATOR+sgIndex;
      
      StoreShippingPriceInfo shippingPriceInfo = (StoreShippingPriceInfo)sg.getPriceInfo();
      
      // assign TaxPriceInfo to the shipping group      
      TaxPriceInfo shippingTaxInfo = assignOfferTax(shippingOfferKey, icsReply, shippingPriceInfo.getTaxPriceInfo(), false);
      shippingPriceInfo.setTaxPriceInfo(shippingTaxInfo);
      
      // loop through shipping group's relationships
      for (int relIndex = 0; relIndex < rels.size(); relIndex++){
        // get relationship
        ShippingGroupCommerceItemRelationship rel = (ShippingGroupCommerceItemRelationship) rels.get(relIndex);
        // get commerce item from relationship
        CommerceItem item = rel.getCommerceItem();
        
        // construct shippingOfferKey as {relIndex}_{sgIndex}, where {relIndex} - the index
        // of ShippingGroupCommerceItemRelationship in shipping group and {sgIndex} - the index of 
        // the shipping group.
        String offerKey = Integer.toString(relIndex) + KEY_SEPARATOR + sgIndex;
        StoreItemPriceInfo itemPriceInfo = (StoreItemPriceInfo)item.getPriceInfo();
        TaxPriceInfo itemTaxPriceInfo = itemPriceInfo.getTaxPriceInfo();
        
        // add tax amounts that correspond current ShippingGroupCommerceItemRelationship 
        //to commerce item's TaxPriceInfo
        itemTaxPriceInfo = assignOfferTax(offerKey, icsReply, itemTaxPriceInfo, true);
        itemPriceInfo.setTaxPriceInfo(itemTaxPriceInfo);
        
        // get item's pricing details for the relationship range
        Range relationshipRange = rel.getRange();
        List details = itemPriceInfo.getCurrentPriceDetailsForRange(relationshipRange);
        
        //get total tax for relationship
        double relTaxTotal = 0.0;
        String cybersourceOfferTaxTotalReply = icsReply.getField("tax_tax_amount" + offerKey);
        if (!StringUtils.isEmpty(cybersourceOfferTaxTotalReply)) {
          relTaxTotal = Double.parseDouble(cybersourceOfferTaxTotalReply);
        }
        
        // get total amount for relationship
        double itemRangeTotal = 0.0;
        for(int d=0;d<details.size();d++) {
          DetailedItemPriceInfo detail = (DetailedItemPriceInfo)details.get(d);
          
          itemRangeTotal += detail.getAmount();
        }
          
        // assign tax amounts to pricing details 
        double detailTotal = 0.0;
        for(int d=0;d<details.size();d++) {
          DetailedItemPriceInfo detail = (DetailedItemPriceInfo)details.get(d);
          
          
          double detailTaxRounded = getTaxAmountByRatio(detail.getAmount(), itemRangeTotal,
                                    relTaxTotal, detailTotal,
                                    details.size(), d);
          detailTotal += detailTaxRounded;
          detail.setTax(detailTaxRounded);
        }
      }
    }
  }
  
  /**
   * Assigns TaxPriceInfos to commerce items with zero costs
   * @param pOrder order
   */
  protected void initializeItemTaxPriceInfos(Order pOrder){
    List<CommerceItem> items = (List<CommerceItem>) pOrder.getCommerceItems();
    for (CommerceItem item : items){
      TaxPriceInfo taxPriceInfo = createZeroCostTax();
      ((StoreItemPriceInfo)item.getPriceInfo()).setTaxPriceInfo(taxPriceInfo);
    }
  }
  
  /**
   * The method calculates tax amount for the partial amount using the tax amount for
   * the total amount. The returned tax amount will be calculates as 
   *   {total tax} * {partial amount} / {total amount}
   * 
   * To avoid rounding errors the method checks if the sum of partial tax amounts equals total tax
   * and if it doesn't it adds leftovers to the last partial tax amount. The index of partial tax is
   * the number of partial taxes are passed through <code>itemIndex</code> and <code>itemSize</code>
   * correspondingly. The sum of already calculated partial tax amounts are passed through 
   * <code>pTaxAmountProcessed</code> parameter.
   *  
   * @param pPartialAmount partial amount to calculate tax for
   * @param pTotalAmount total amount
   * @param pTotalTaxAmount tax that correspond to total amount
   * @param pTaxAmountProcessed the sum of partial tax amount that is already processed
   * @param itemSize number of parts that build total amount
   * @param itemIndex the index of partial amount
   * @return
   */
  protected double getTaxAmountByRatio (double pPartialAmount, double pTotalAmount,
                                        double pTotalTaxAmount, double pTaxAmountProcessed,
                                        int itemSize, int itemIndex){
    double ratio = pPartialAmount/pTotalAmount;
    
    // if we divided by zero... use zero instead
    if(Double.isNaN(ratio) || Double.isInfinite(ratio)) {
      if(isLoggingDebug()) {
        logDebug(MessageFormat.format(Constants.QUOTIENT_IS_NAN, "assignItemTaxAmounts", Double.toString(pPartialAmount), Double.toString(pTotalAmount)));
      }
      ratio = 0.0;
    }
    
    // calculate tax for partial amount
    double partialTaxAmount = pTotalTaxAmount * ratio;
    double partialTaxAmountRounded = getPricingTools().roundDown(partialTaxAmount);
    
    // add calculated tax to total tax amount processed
    pTaxAmountProcessed += partialTaxAmountRounded;
    
    // if this is the last part that builds total amount check if the sum of partial tax amounts
    // equals total tax amount, if not add the leftovers to the calculated tax
    if(itemIndex == (itemSize - 1)) {
      double leftovers = pTotalTaxAmount - pTaxAmountProcessed;
       
      partialTaxAmountRounded = getPricingTools().roundDown(partialTaxAmountRounded + leftovers);
       
     }
     return partialTaxAmountRounded;
  }
  
  /**
   * This method will get the Offer Tax from the CyberSource reply and
   * store it in TaxPriceInfo object.
   *
   * There are cases where no field is sent back from Cybersource. Therefore,
   * this method is careful to check for null objects coming back from
   * the ICSReply.
   *
   * @param pOfferKey - offer key
   * @param pIcsReply - CyberSource reply
   * @param pTaxPriceInfo - TaxPriceInfo object to store taxes to, if not passed a new object
   *                        is crested
   * @param pAddTaxes - if true adds taxes for the specified offer to the existing taxes
   *                    in TaxPriceInfo object, otherwise resets TaxPriceInfo's values to
   *                    the offer's tax values.
   */
  public TaxPriceInfo assignOfferTax(String pOfferKey, ICSReply pIcsReply, TaxPriceInfo pTaxPriceInfo, 
                                boolean pAddTaxes) {
    
    if (pTaxPriceInfo == null){
      pTaxPriceInfo = createZeroCostTax();
    }
    
    // if addTaxes is false set all taxes to 0.0 for fresh start
    if (!pAddTaxes){
      pTaxPriceInfo.setAmount(0.0);
      pTaxPriceInfo.setCityTax(0.0);
      pTaxPriceInfo.setCountyTax(0.0);
      pTaxPriceInfo.setStateTax(0.0);
      pTaxPriceInfo.setDistrictTax(0.0);
      pTaxPriceInfo.setCountryTax(0.0);
    }
    
    String offerTaxAmountKey = "tax_tax_amount" + pOfferKey;
    String offerCityTaxKey = "tax_city_tax" + pOfferKey;
    String offerCountyTaxKey = "tax_county_tax" + pOfferKey;
    String offerStateTaxKey = "tax_state_tax" + pOfferKey;
    String offerDistrictTaxKey = "tax_district_tax" + pOfferKey;

    // total tax on offer
    double offerTaxTotal = 0.0;
    String cybersourceOfferTaxTotalReply = pIcsReply.getField(offerTaxAmountKey);

    Double shippingTaxTotalResult = null;

    if (cybersourceOfferTaxTotalReply != null) {
      shippingTaxTotalResult = new Double(cybersourceOfferTaxTotalReply);
    }

    if (shippingTaxTotalResult != null) {
      offerTaxTotal = shippingTaxTotalResult.doubleValue();
    }

    pTaxPriceInfo.setAmount(pTaxPriceInfo.getAmount()+offerTaxTotal);

    // city tax on offer
    double offerCityTax = 0.0;
    String cybersourceShippingCityTaxReply = pIcsReply.getField(offerCityTaxKey);

    Double offerCityTaxResult = null;

    if (cybersourceShippingCityTaxReply != null) {
      offerCityTaxResult = new Double(cybersourceShippingCityTaxReply);
    }

    if (offerCityTaxResult != null) {
      offerCityTax = offerCityTaxResult.doubleValue();
    }

    pTaxPriceInfo.setCityTax(pTaxPriceInfo.getCityTax()+offerCityTax);

    // county tax on offer
    double offerCountyTax = 0.0;
    String cybersourceShippingCountyTaxReply = pIcsReply.getField(offerCountyTaxKey);

    Double offerCountyTaxResult = null;

    if (cybersourceShippingCountyTaxReply != null) {
      offerCountyTaxResult = new Double(cybersourceShippingCountyTaxReply);
    }

    if (offerCountyTaxResult != null) {
      offerCountyTax = offerCountyTaxResult.doubleValue();
    }

    pTaxPriceInfo.setCountyTax(pTaxPriceInfo.getCountyTax()+offerCountyTax);

    // state tax on offer
    double offerStateTax = 0.0;
    String cybersourceShippingStateTaxReply = pIcsReply.getField(offerStateTaxKey);

    Double offerStateTaxResult = null;

    if (cybersourceShippingStateTaxReply != null) {
      offerStateTaxResult = new Double(cybersourceShippingStateTaxReply);
    }

    if (offerStateTaxResult != null) {
      offerStateTax = offerStateTaxResult.doubleValue();
    }

    pTaxPriceInfo.setStateTax(pTaxPriceInfo.getStateTax()+offerStateTax);

    // district tax on offer
    double offerDistrictTax = 0.0;
    String cybersourceShippingDistrictTaxReply = pIcsReply.getField(offerDistrictTaxKey);

    Double offerDistrictTaxResult = null;

    if (cybersourceShippingDistrictTaxReply != null) {
      offerDistrictTaxResult = new Double(cybersourceShippingDistrictTaxReply);
    }

    if (offerDistrictTaxResult != null) {
      offerDistrictTax = offerDistrictTaxResult.doubleValue();
    }

    pTaxPriceInfo.setDistrictTax(pTaxPriceInfo.getDistrictTax()+offerDistrictTax);

    if (isLoggingDebug()) {
      logDebug("----------------------------------");
      logDebug("Tax Details for offer amount");
      logDebug(offerCityTaxKey + " : " + offerCityTax);
      logDebug(offerCountyTaxKey + " : " + offerCountyTax);
      logDebug(offerStateTaxKey + " : " + offerStateTax);
      logDebug(offerDistrictTaxKey + " : " + offerDistrictTax);
      logDebug("Item Tax Total: " + offerTaxTotal);
      logDebug("----------------------------------");
    }  
    
    return pTaxPriceInfo;
  }


  /**
   * This method is overridden to assign the TaxPriceInfo objects to the
   * StoreItemPriceInfo objects. This method is used
   * if <code>calculateTaxByShipping</code> is true.
   *
   * @param pShippingGroup The shipping group whose details will be updated
   * @param pTaxPriceInfo The TaxPriceInfo for the shipping group
   * @param pOrderPrice The TaxPriceInfo for the entire order
   * @param pShippingSubtotal The price info containing the subtotal information for the shipping group
   * @param pStatus This is the tax status that was returned from the TaxProcessor.
   * @throws PricingException if error occurs
   */
  protected void assignItemTaxAmounts(ShippingGroup pShippingGroup, TaxPriceInfo pTaxPriceInfo,
      OrderPriceInfo pOrderPrice, OrderPriceInfo pShippingSubtotal, TaxStatus pStatus) throws PricingException {
    if (isLoggingDebug())
      logDebug("Assign tax using tax by shipping");

    if (pShippingGroup == null) {
      if (isLoggingDebug())
        logDebug("Couldn't assign item tax amounts.  The shipping group was null.");
      return;
    }
    if (pTaxPriceInfo == null) {
      if (isLoggingDebug())
        logDebug("Couldn't assign item tax amounts.  The tax price info was null.");
      return;
    }
    List relationships = getRelationshipsToAssignTaxTo(pShippingGroup);

    // If the relationships are null or they are empty then there is nothing to
    // distribute and
    // as such we should just return

    if (relationships == null || relationships.size() == 0)
      return;

    CyberSourceStatus taxStatus = (CyberSourceStatus) pStatus;
    for (int r = 0; r < relationships.size(); r++) {
      ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) relationships.get(r);
      ItemPriceInfo itemPrice = sgcir.getCommerceItem().getPriceInfo();
      List details = itemPrice.getCurrentPriceDetailsForRange(sgcir.getRange());
      // assume details.size==1
      StoreItemPriceInfo bbpi = (StoreItemPriceInfo) itemPrice;

      String cityTaxKey = "tax_city_tax" + r;
      String countyTaxKey = "tax_county_tax" + r;
      String stateTaxKey = "tax_state_tax" + r;
      String districtTaxKey = "tax_district_tax" + r;
      String itemTaxTotalKey = "tax_tax_amount" + r;

      String tax = (String) taxStatus.getValue(itemTaxTotalKey);

      if (tax != null) {
        for (int d = 0; d < details.size(); d++) {
          DetailedItemPriceInfo detail = (DetailedItemPriceInfo) details.get(d);
          detail.setTax(Double.parseDouble(tax));
        }
        TaxPriceInfo tpi = bbpi.getTaxPriceInfo();
        if (tpi != null) {
          tpi.setAmount(tpi.getAmount() + Double.parseDouble(tax));

          tax = (String) taxStatus.getValue(stateTaxKey);
          if (tax != null) {
            tpi.setStateTax(tpi.getStateTax() + Double.parseDouble(tax));
          } else {
            tpi.setStateTax(0.0);
          }

          tax = (String) taxStatus.getValue(cityTaxKey);
          if (tax != null) {
            tpi.setCityTax(tpi.getCityTax() + Double.parseDouble(tax));
          } else {
            tpi.setCityTax(0.0);
          }

          tax = (String) taxStatus.getValue(countyTaxKey);
          if (tax != null) {
            tpi.setCountyTax(tpi.getCountyTax() + Double.parseDouble(tax));
          } else {
            tpi.setCountyTax(0.0);
          }

          tax = (String) taxStatus.getValue(districtTaxKey);
          if (tax != null) {
            tpi.setDistrictTax(tpi.getDistrictTax() + Double.parseDouble(tax));
          } else {
            tpi.setDistrictTax(0.0);
          }
        }
      }
    }
  }

  /**
   * Creates a zero-cost tax item.
   *
   * @return created zero-cost tax item
   */
  public TaxPriceInfo createZeroCostTax() {
    TaxPriceInfo zeroTax = new TaxPriceInfo();
    zeroTax.setAmount(0.0);
    zeroTax.setCityTax(0.0);
    zeroTax.setCountyTax(0.0);
    zeroTax.setStateTax(0.0);
    zeroTax.setDistrictTax(0.0);
    zeroTax.setCountryTax(0.0);

    return zeroTax;
  }
}
