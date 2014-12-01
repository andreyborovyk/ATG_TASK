/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.integrations.cybersource;

import atg.core.util.Address;

import atg.integrations.cybersource.CyberSourceConnection;
import atg.integrations.cybersource.CyberSourceException;
import atg.integrations.cybersource.CyberSourceStatus;
import atg.integrations.cybersource.CyberSourceTax;

import atg.payment.tax.ShippingDestination;
import atg.payment.tax.TaxRequestInfo;
import atg.payment.tax.TaxStatus;
import atg.payment.tax.TaxableItem;

import com.cybersource.ics.base.exception.ICSException;
import com.cybersource.ics.base.message.ICSReply;
import com.cybersource.ics.client.ICSClient;
import com.cybersource.ics.client.message.ICSClientOffer;
import com.cybersource.ics.client.message.ICSClientRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * This class extends the ATG CyberSourceTax class as to be able to set the
 * showTaxPerOffer on the ICSClientRequest.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/Cybersource/src/atg/projects/store/integrations/cybersource/StoreCyberSourceTax.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreCyberSourceTax extends CyberSourceTax {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/Cybersource/src/atg/projects/store/integrations/cybersource/StoreCyberSourceTax.java#2 $$Change: 651448 $";

  /**
   * Currency constant.
   */
  public static final String CURRENCY = "currency";

  /**
   * ICS application constant.
   */
  public static final String ICS_APPLICATIONS = "ics_applications";

  /**
   * Merchant id constant.
   */
  public static final String MERCHANT_ID = "merchant_id";

  /**
   * Merchant reference number constant.
   */
  public static final String MERCHANT_REF_NUMBER = "merchant_ref_number";

  /**
   * Merchant sku constant.
   */
  public static final String MERCHANT_SKU = "merchant_product_sku";

  /**
   * Nexus sku constant.
   */
  public static final String NEXUS = "nexus";

  /**
   * Amount constant.
   */
  public static final String AMOUNT = "amount";

  /**
   * Quantity constant.
   */
  public static final String QUANTITY = "quantity";

  /**
   * Billing city constant.
   */
  public static final String BILL_CITY = "bill_city";

  /**
   * Billing country constant.
   */
  public static final String BILL_COUNTRY = "bill_country";

  /**
   * Billing county constant.
   */
  public static final String BILL_COUNTY = "bill_county";

  /**
   * Billing state constant.
   */
  public static final String BILL_STATE = "bill_state";

  /**
   * Billing zip code constant.
   */
  public static final String BILL_ZIP = "bill_zip";

  /**
   * First shipping address constant.
   */
  public static final String SHIP_TO_ADDRESS1 = "ship_to_address1";

  /**
   * Second shipping address constant.
   */
  public static final String SHIP_TO_ADDRESS2 = "ship_to_address2";

  /**
   * Shipping address city constant.
   */
  public static final String SHIP_TO_CITY = "ship_to_city";

  /**
   * Shipping address country constant.
   */
  public static final String SHIP_TO_COUNTRY = "ship_to_country";

  /**
   * Shipping address county constant.
   */
  public static final String SHIP_TO_COUNTY = "ship_to_county";

  /**
   * Shipping address state constant.
   */
  public static final String SHIP_TO_STATE = "ship_to_state";

  /**
   * Shipping address zip code constant.
   */
  public static final String SHIP_TO_ZIP = "ship_to_zip";

  /**
   * Show tax per offer constant.
   */
  public static final String SHOW_TAX_PER_OFFER = "show_tax_per_offer";

  /**
   * Product code constant.
   */
  public static final String PRODUCT_CODE = "product_code";

  /**
   * Product name constant.
   */
  public static final String PRODUCT_NAME = "product_name";
  
  /**
   * Cybersource reply's key separator.
   */
  public static final String KEY_SEPARATOR = "_";

  /**
   * Show tax per offer property.
   */
  private String mShowTaxPerOffer;

  /**
   * Shipping product code.
   */
  private String mShippingProductCode;

  /**
   * Tax to product code map.
   */
  private Map mTaxToProductCodeMap = new HashMap();

  /**
   * @return show tax per offer property.
   */
  public String getShowTaxPerOffer() {
    return mShowTaxPerOffer;
  }

  /**
   * @param pShowTaxPerOffer - show tax per offer property.
   */
  public void setShowTaxPerOffer(String pShowTaxPerOffer) {
    mShowTaxPerOffer = pShowTaxPerOffer;
  }

  /**
   * @return shipping product code.
   */
  public String getShippingProductCode() {
    return mShippingProductCode;
  }

  /**
   * @param pShippingProductCode - shipping product code.
   */
  public void setShippingProductCode(String pShippingProductCode) {
    mShippingProductCode = pShippingProductCode;
  }
  
  //-------------------------------------
  // property: taxPerOfferProperties
  //-------------------------------------
  private String[] mTaxPerOfferProperties = null;
  /**
   * Returns the TaxPerOfferProperties
   */
  public String[] getTaxPerOfferProperties() {
    return mTaxPerOfferProperties;
  }

  /**
   * Sets the TaxPerOfferProperties
   */
  public void setTaxPerOfferProperties(String[] pTaxPerOfferProperties) {
    mTaxPerOfferProperties = pTaxPerOfferProperties;
  }

  /**
   * Call to cybersource to calculate taxes with this single CyberSource
   * request
   *
   * @param pTaxRequestInfo - tax request information
   * @param i - some i parameter
   *
   * @return tax status
   *
   * @throws ICSException if error occurs
   */
  public TaxStatus calculate(TaxRequestInfo pTaxRequestInfo, int i)
    throws ICSException {
    ICSReply reply = null;

    if (!mCsCon.getInitialized()) {
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFailedInitialize", "see above"));
    }

    ICSClient client = mCsCon.getICSClient();
    ICSClientRequest request = mCsCon.getICSClientRequest();

    setBillingInfo(request, pTaxRequestInfo, i);

    /*
     * fill in the required fields, check if not null, otherwise throw the
     * exception back to the tax manager
     */
    request.setField(ICS_APPLICATIONS, "ics_tax");

    // OrderId
    if (pTaxRequestInfo.getOrderId() != null) {
      request.setField(MERCHANT_REF_NUMBER, pTaxRequestInfo.getOrderId());
    } else {
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "OrderId"));
    }

    // MerchantId
    if (mCsCon.getMerchantId() != null) {
      request.setField(MERCHANT_ID, mCsCon.getMerchantId());
    } else {
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "MerchantId"));
    }

    // ShowTaxPerOffer property - optional
    if (getShowTaxPerOffer() != null) {
      request.setField(SHOW_TAX_PER_OFFER, getShowTaxPerOffer());

      if (isLoggingDebug()) {
        logDebug("Setting SHOW+TAX_PER_OFFER to " + getShowTaxPerOffer() + " on ICSClientRequest");
      }
    }

    setShipToInfo(request, pTaxRequestInfo, i);
    setShipFromInfo(request, pTaxRequestInfo, i);

    // list of states/provinces excluded from tax responsibilities
    // only for US/CA requests
    if (getNoNexus() != null) {
      request.setField(NoNexus, getNoNexus());
    }

    // Currency Code - optional
    String currency = pTaxRequestInfo.getShippingDestination(i).getCurrencyCode();

    if (currency != null) {
      request.setField(CURRENCY, currency);
    }

    // for VAT service (international requests)
    if (getSellerRegistrationNumber() != null) {
      request.setField(SellerRegistration, getSellerRegistrationNumber());
    }

    // The total amount of the shipping destination group
    double totald = pTaxRequestInfo.getShippingDestination(i).getTaxableItemAmount();

    /* check the item's amount against the limit */
    if (totald > Double.MAX_VALUE) {
      throw new CyberSourceException(CyberSourceConnection.msg.getString("CyberSourceTotalTooLarge"));
    }

    // taxable items in the destination group might be exposed to
    // different product codes that indicate whether the items should be
    // taxed with a non standard rate
    if (getUseProductCode()) {
      TaxableItem[] pItems = pTaxRequestInfo.getShippingDestination(i).getTaxableItems();

      if (pItems == null) {
        throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "TaxableItems"));
      }

      for (int ii = 0; ii < pItems.length; ii++) {
        ICSClientOffer offer = mCsCon.getICSClientOffer();

        // check the taxStatus to perform the product code setting
        // for each item in the group
        if (TaxProductCodeSetting(pItems[ii]) == true) {
          // may be overriden by a subclass
          assignTaxProductCode(pItems[ii], offer);
        }

        totald = pItems[ii].getAmount();

        if (totald > Double.MAX_VALUE) {
          throw new CyberSourceException(CyberSourceConnection.msg.getString("CyberSourceTotalTooLarge"));
        }

        String amount = new Double(totald).toString();
        offer.setField(AMOUNT, amount);
        request.addOffer(offer);
      }
    } else {
      // use the amount of the shipping group for the offer.
      // this single offer represents the shipping group
      ICSClientOffer offer = mCsCon.getICSClientOffer();
      String amount = new Double(totald).toString();
      offer.setField(AMOUNT, amount);
      request.addOffer(offer);
    }

    // to calculate tax on shipping amount, add it as the last offer
    ICSClientOffer shippingOffer = mCsCon.getICSClientOffer();
    String shippingAmount = new Double(pTaxRequestInfo.getShippingDestination(i).getShippingAmount()).toString();
    // set the product code on the offer to indicate it is for shipping
    shippingOffer.setField(PRODUCT_CODE, getShippingProductCode());
    shippingOffer.setField(AMOUNT, shippingAmount);
    shippingOffer.setField(PRODUCT_NAME, "shipping");
    shippingOffer.setField(MERCHANT_SKU, "shipping");
    shippingOffer.setField(QUANTITY, "1");

    request.addOffer(shippingOffer);

    if (isLoggingDebug()) {
      /* String server = client.url.toString(); */
      logDebug("\n----- request -----");
      logDebug(request.toString());
    }

    /* blocking call to CyberSource server */
    reply = client.send(request);

    if (isLoggingDebug()) {
      logDebug("----- response -----");
      logDebug(reply.toString());
    }

    return new CyberSourceStatus(reply);
  }
  
  
  /**
   * Overrides base method to not only calculate total taxes for passed TaxStatus objects
   * but to preserve per offer taxes details in final TaxStatus. Per offer taxes are stored into
   * final TaxStatus with "_{tax_status_index} suffix, where {tax_status_index} - the TaxStatus index.
   */
  public TaxStatus calculateAllTax(TaxStatus[] taxstatus) {
    int i;

    ICSReply reply = null;
    ICSReply combinedReply = null;
    
    double taxAmount = 0;
    double cityTax = 0;
    double stateTax = 0;
    double countyTax = 0;
    double districtTax = 0;
    
    combinedReply = ((CyberSourceStatus)taxstatus[0]).getValues();
    for (i = 0; i < taxstatus.length; i++) {
        taxAmount += taxstatus[i].getAmount();
        cityTax += taxstatus[i].getCityTax();
        stateTax += taxstatus[i].getStateTax();
        countyTax += taxstatus[i].getCountyTax();
        districtTax += taxstatus[i].getDistrictTax();
        reply =  ((CyberSourceStatus)taxstatus[i]).getValues();
               
        // we don't know how many offers in this reply so just break the loop when no more 
        // taxes per offer are found
        external : for (int ii=0;;ii++){
          for(String fieldName : getTaxPerOfferProperties()){
            // if there no tax fields with such offer number then break the loop
            String fieldKey = fieldName + ii;
            if (!reply.containsField(fieldKey)) break external;
          
            // copy offer's tax entry to final combined reply, modify field key by adding
            // suffix with tax status index
            // Note: tax status index corresponds to shipping destination index
            combinedReply.setField(fieldKey+KEY_SEPARATOR+i, reply.getField(fieldKey));
            if (i==0)
              combinedReply.removeField(fieldKey);
          }
        }
    }
       
    // take the last TaxStatus object's properties and reset the taxes
    // fields to the totals.
    combinedReply.setField("tax_total_tax", new Double(taxAmount).toString());
    combinedReply.setField("tax_total_state_tax", new Double(stateTax).toString());
    combinedReply.setField("tax_total_city_tax", new Double(cityTax).toString());
    combinedReply.setField("tax_total_county_tax", new Double(countyTax).toString());
    combinedReply.setField("tax_total_district_tax", new Double(districtTax).toString());
    
    return new CyberSourceStatus(combinedReply);   
  }

  /**
   * Utility method to set the billing address information on the
   * ICSCLientRequest.
   *
   * @param pRequest - cyberstore request
   * @param pTaxRequestInfo - tas request info
   * @param i - some i parameter
   * @throws ICSException if error occurs
   */
  protected void setBillingInfo(ICSClientRequest pRequest, TaxRequestInfo pTaxRequestInfo, int i)
    throws ICSException {
    Address addr;

    // Assume shipping info = billing info if billing info not set yet.
    ShippingDestination dest = pTaxRequestInfo.getShippingDestination(i);

    // Billing Address
    if (pTaxRequestInfo.getBillingAddress() == null || pTaxRequestInfo.getBillingAddress().getAddress1() == null) {
      if (dest != null) {
        addr = dest.getShippingAddress();
      } else {
        throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "BillingAddress"));
      }
    } else {
      addr = pTaxRequestInfo.getBillingAddress();
    }

    String strBillToAddress = addr.getAddress1();
    String strBillToCountry = addr.getCountry();
    String strBillToCity = addr.getCity();
    String strBillToState = addr.getState();
    String strBillToZip = addr.getPostalCode();

    // Billing Address Country
    if ((strBillToCountry != null) && (strBillToCountry.trim().length() != 0)) {
      pRequest.setField(BILL_COUNTRY, strBillToCountry);
    } else {
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "BillingAddressCountry"));
    }

    /*
     * Bill Address line are dprecated // Billing Address Line1 if
     * (strBillToAddress != null) {
     * pRequest.setBillAddress1(strBillToAddress); } // Billing Address
     * Line2 optional strBillToAddress =
     * pTaxRequestInfo.getBillingAddress().getAddress2(); if
     * (strBillToAddress != null) {
     * pRequest.setBillAddress2(strBillToAddress); }
     */

    // Billing Address City
    if ((strBillToCity != null) && (strBillToCity.trim().length() != 0)) {
      pRequest.setField(BILL_CITY, strBillToCity);
    } else if (strBillToCountry.toUpperCase().equals("US") || strBillToCountry.toUpperCase().equals("CA")) {
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "BillingAddressCity"));
    }

    // Billing Address State - required for US and CA
    if ((strBillToState != null) && (strBillToState.trim().length() != 0)) {
      pRequest.setField(BILL_STATE, strBillToState);
    } else if (strBillToCountry.toUpperCase().equals("US") || strBillToCountry.toUpperCase().equals("CA")) {
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "BillingAddressState"));
    }

    // Billing Address Postal Code - required for US and CA
    if ((strBillToZip != null) && (strBillToZip.trim().length() != 0)) {
      pRequest.setField(BILL_ZIP, strBillToZip);
    } else if (strBillToCountry.toUpperCase().equals("US") || strBillToCountry.toUpperCase().equals("CA")) {
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "BillingAddressZip"));
    }

    // Current shipping destination
    if (pTaxRequestInfo.getShippingDestination(i) == null) {
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull",
          "ShippingDestination[" + i + "]"));
    }
  }

  /**
   * Utility method to set the destination shipping address information on the
   * ICSCLientRequest.
   *
   * @param pRequest - cyberstore request
   * @param pTaxRequestInfo - tax request information
   * @param i - some i parameter
   * @throws ICSException if error occurs
   */
  protected void setShipToInfo(ICSClientRequest pRequest, TaxRequestInfo pTaxRequestInfo, int i)
    throws ICSException {
    // shipping properties default to billing if not provided
    String strShipToAddress = null;
    String strShipToCountry = null;
    String strShipToCity = null;
    String strShipToState = null;
    String strShipToZip = null;

    // shipping destination properties
    if (pTaxRequestInfo.getShippingDestination(i).getShippingAddress() != null) {
      strShipToAddress = pTaxRequestInfo.getShippingDestination(i).getShippingAddress().getAddress1();

      if (strShipToAddress != null) {
        pRequest.setField(SHIP_TO_ADDRESS1, strShipToAddress);
      }

      strShipToAddress = pTaxRequestInfo.getShippingDestination(i).getShippingAddress().getAddress2();

      if (strShipToAddress != null) {
        pRequest.setField(SHIP_TO_ADDRESS2, strShipToAddress);
      }

      strShipToCountry = pTaxRequestInfo.getShippingDestination(i).getShippingAddress().getCountry();

      if (strShipToCountry != null) {
        pRequest.setField(SHIP_TO_COUNTRY, strShipToCountry);
      } else {
        throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull",
            "ShippingAddressCountry[" + i + "]"));
      }

      strShipToCity = pTaxRequestInfo.getShippingDestination(i).getShippingAddress().getCity();

      if (strShipToCity != null) {
        pRequest.setField(SHIP_TO_CITY, strShipToCity);
      }

      strShipToState = pTaxRequestInfo.getShippingDestination(i).getShippingAddress().getState();

      if (strShipToState != null) {
        pRequest.setField(SHIP_TO_STATE, strShipToState);
      } else if (strShipToCountry.toUpperCase().equals("US") || strShipToCountry.toUpperCase().equals("CA")) {
        throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull",
            "ShippingAddressState[" + i + "]"));
      }

      strShipToZip = pTaxRequestInfo.getShippingDestination(i).getShippingAddress().getPostalCode();

      if (strShipToZip != null) {
        pRequest.setField(SHIP_TO_ZIP, strShipToZip);
      } else if (strShipToCountry.toUpperCase().equals("US") || strShipToCountry.toUpperCase().equals("CA")) {
        throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull",
            "ShippingAddressZip[" + i + "]"));
      }
    }
  }

  /**
   * Utility method to set the origin shipping address information on the
   * ICSCLientRequest.
   *
   * @param pRequest - cyberstore request
   * @param pTaxRequestInfo - tax request information
   * @param i - some i parameter
   * @throws ICSException if error occurs
   */
  protected void setShipFromInfo(ICSClientRequest pRequest, TaxRequestInfo pTaxRequestInfo, int i)
    throws ICSException {
    // Ship From properties - optional
    if (getShipFromCountry() != null) {
      pRequest.setField(ShipFromCountry, getShipFromCountry());
    }

    if (getShipFromCity() != null) {
      pRequest.setField(ShipFromCity, getShipFromCity());
    }

    if (getShipFromState() != null) {
      pRequest.setField(ShipFromState, getShipFromState());
    }

    if (getShipFromZip() != null) {
      pRequest.setField(ShipFromZip, getShipFromZip());
    }

    // Origin properties - optional
    if (getOriginCountry() != null) {
      pRequest.setField(AcceptanceCountry, getOriginCountry());
    }

    if (getOriginCity() != null) {
      pRequest.setField(AcceptanceCity, getOriginCity());
    }

    if (getOriginState() != null) {
      pRequest.setField(AcceptanceState, getOriginState());
    }

    if (getOriginZip() != null) {
      pRequest.setField(AcceptanceZip, getOriginZip());
    }
  }
}
