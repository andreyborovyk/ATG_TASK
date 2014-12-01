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

package atg.integrations.cybersource;

import atg.payment.tax.*;
import atg.nucleus.*;

import java.io.*;
import java.net.*;
import java.text.*;

import com.cybersource.ics.client.*;
import com.cybersource.ics.client.message.*;
import com.cybersource.ics.base.exception.*;
import com.cybersource.ics.base.message.*;

/**
 *
 * <p>This class manages the communications to a CyberSource tax system
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/cybersource/CyberSourceTax.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * 
**/

public class CyberSourceTax extends GenericService implements TaxProcessor {
    
    //-------------------------------------
    // Class version string
    
    public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/cybersource/CyberSourceTax.java#2 $$Change: 651448 $";
    
    public static final String NoNexus = "no_nexus";
    public static final String SellerRegistration = "seller_registration";
    public static final String ShipFromCity = "ship_from_city";
    public static final String ShipFromState = "ship_from_state";
    public static final String ShipFromZip = "ship_from_zip";
    public static final String ShipFromCountry = "ship_from_country";
    public static final String AcceptanceCity = "order_acceptance_city";
    public static final String AcceptanceState = "order_acceptance_state";
    public static final String AcceptanceZip = "order_acceptance_zip";
    public static final String AcceptanceCountry = "order_acceptance_country";
    
    protected String mShipFromCountry;
    protected String mShipFromCity;
    protected String mShipFromState;   // state/province
    protected String mShipFromZip;
    
    protected String mOriginCountry;
    protected String mOriginCity;
    protected String mOriginState;   // state/province
    protected String mOriginZip;

    protected String mSellerRegistrationNumber;

    // ShipFromCountry
    /** Set ShipFromCountry used for tax calculations.*/
    public void setShipFromCountry(String pShipFromCountry) {
        mShipFromCountry = pShipFromCountry;
    }
    
    /** Get ShipFromCountry used for tax calculations.*/
    public String getShipFromCountry() { return(mShipFromCountry); }


    // ShipFromCity
    /** Set ShipFromCity used for tax calculations.*/
    public void setShipFromCity(String pShipFromCity) {
        mShipFromCity = pShipFromCity;
    }
    
    /** Get ShipFromCity used for tax calculations.*/
    public String getShipFromCity() { return(mShipFromCity); }
    
    
    // ShipFromState
    /** Set ShipFromState/Province used for tax calculations.*/
    public void setShipFromState(String pShipFromState) {
        mShipFromState = pShipFromState;
    }
    
    /** Get ShipFromState used for tax calculations.*/
    public String getShipFromState() { return(mShipFromState); }
    
    // ShipFromZip
    /** Set ShipFromZip/PostalCode used for tax calculations.*/
    public void setShipFromZip(String pShipFromZip) {
        mShipFromZip = pShipFromZip;
    }
    
    /** Get ShipFromZip/PostalCode used for tax calculations.*/
    public String getShipFromZip() { return(mShipFromZip); }
    
    // OriginCountry
    /** Set 2 character OriginCountry used for tax calculations.*/
    public void setOriginCountry(String pOriginCountry) {
        mOriginCountry = pOriginCountry;
    }
    
    /** Get OriginCountry used for tax calculations.*/
    public String getOriginCountry() { return(mOriginCountry); }
        
    // OriginCity
    /** Set OriginCity used for tax calculations.*/
    public void setOriginCity(String pOriginCity) {
        mOriginCity = pOriginCity;
    }
    
    /** Get OriginCity used for tax calculations.*/
    public String getOriginCity() { return(mOriginCity); }
    
    // OriginState
    /** Set OriginState/Province used for tax calculations.*/
    public void setOriginState(String pOriginState) {
        mOriginState = pOriginState;
    }
    
    /** Get OriginState used for tax calculations.*/
    public String getOriginState() { return(mOriginState); }
    
    // OriginZip
    /** Set OriginZip/PostalCode used for tax calculations.*/
    public void setOriginZip(String pOriginZip) {
        mOriginZip = pOriginZip;
    }
    
    /** Get OriginZip used for tax calculations. */
    public String getOriginZip() { return(mOriginZip); }
    
    // Seller Registration for a VAT service.
    // Every country has its own format for this number, see manual
    public void setSellerRegistrationNumber(String pSellerRegistrationNumber) {
        mSellerRegistrationNumber = pSellerRegistrationNumber;
    }

    public String getSellerRegistrationNumber() {
        return mSellerRegistrationNumber;
    }
    
    /**
     * Default constructor
     */
    public CyberSourceTax () {
    }
    
    /** 
     * CyberSourceConnection module that contains the merchant 
     * related data
     */
    public CyberSourceConnection mCsCon = null;
    
    public void setCsCon(CyberSourceConnection pCsCon) {
        mCsCon = pCsCon;
    }
    
    public CyberSourceConnection getCsCon() {
        return mCsCon;
    }
    
    /**
     * No Nexus field specifies states/provinces excluded from tax
     * responsibilities (the list is delimited by comma: NY,NJ,MA)
     */
    public String mNoNexus = null;
    
    public void setNoNexus(String pNoNexus) {
        mNoNexus = pNoNexus;
    }
    
    public String getNoNexus() {
        return mNoNexus;
    }

    /**
     * This property defines a pre-taxableItem request
     * rather than a pre-shipping group request
     */
    public boolean mUseProductCode = false;
    
    public void setUseProductCode(boolean pUseProductCode) {
        mUseProductCode = pUseProductCode;
    }

    public boolean getUseProductCode() {
        return mUseProductCode;
    }

    /*
     * Implement the TaxProcessor interface to return a total tax
     * on all shipping destinations
     * 
     * @param ccinfo Object that contains info about items being purchased
     * @see TaxRequestInfo
     * @return TaxStatus object with the result of the calculation
     * @see TaxStatus
     */
    public TaxStatus calculateTax(TaxRequestInfo ccinfo) {
        
        TaxStatus[] taxStatus = null;

        try {
            taxStatus = performTax(ccinfo);
        }
        catch(ICSException exc) {
            String msg = exc.getMessage();
            if (msg == null)
                msg = exc.toString();
            return mCsCon.processError("CyberSourceTaxFailed", msg);
        }
        
        // add up the taxes
        return calculateAllTax(taxStatus);
        
    }

    /*
     * Implement the TaxProcessor interface to return tax status objects
     * at a shipping destination level
     *
     * @param ccinfo Object that contains info about items being purchased
     * @see TaxRequestInfo
     * @return Array of TaxStatus objects with the result of the calculation
     * @see TaxStatus
     *
     */
    public TaxStatus[] calculateTaxByShipping(TaxRequestInfo ccinfo) {
        
        TaxStatus[] taxStatus = null;

        try {
            taxStatus = performTax(ccinfo);
        }
        catch(ICSException exc) {
            String msg = exc.getMessage();
            if (msg == null)
                msg = exc.toString();
            TaxStatus tax = mCsCon.processError("CyberSourceTaxFailed", msg);
            TaxStatus[] taxStat = {tax};
            return taxStat;
        }
        
        return taxStatus;
        
    }
    
    /**
     * Makes a call to cybersource for each shipping destination
     */
    public TaxStatus[] performTax(TaxRequestInfo ccinfo) throws ICSException {
        
        // Shipping Destination Groups
        if (ccinfo.getShippingDestinations() == null)
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "ShippingDestinations"));
        
        int size = ccinfo.getShippingDestinations().length;
        TaxStatus[] taxstatus = new TaxStatus[size];
        
        // Shipping Destination object represents a shipping group where items N
        // are shipped to the same destination. The amount is the total amount 
        // for the items shipped. Issue a tax request for each destination
        // adding up individual taxes at the end (or not depending on the wrapper function)
        // CyberSource does not support multiple shipping addresses per request.
        // This design may not be really robust since N consequtive requests 
        // have to be issued over the network to cybersource.
        for (int i = 0; i < size; i++) {
            taxstatus[i] = calculate(ccinfo, i);
            // break out if TaxStatus is bad
            if (taxstatus[i].getTransactionSuccess() == false) {
                TaxStatus tax[] = {taxstatus[i]};
                return tax;
            }
        }
        
        return taxstatus;
        
    }
    
    /*
     * Adds up all shipping destinations' taxes and builds the TaxStatus object
     */
    public TaxStatus calculateAllTax(TaxStatus[] taxstatus)
    {
        int i;

        ICSReply reply = null;
        double taxAmount = 0;
        double cityTax = 0;
        double stateTax = 0;
        double countyTax = 0;
        double districtTax = 0;
        
        for (i = 0; i < taxstatus.length; i++) {
            taxAmount += taxstatus[i].getAmount();
            cityTax += taxstatus[i].getCityTax();
            stateTax += taxstatus[i].getStateTax();
            countyTax += taxstatus[i].getCountyTax();
            districtTax += taxstatus[i].getDistrictTax();
        }

        // format the totals
        //taxAmount = CyberSourceConnection.DoubleFormat(taxAmount);
        //countyTax = CyberSourceConnection.DoubleFormat(countyTax);
        //stateTax = CyberSourceConnection.DoubleFormat(stateTax);
        //cityTax = CyberSourceConnection.DoubleFormat(cityTax);
        //districtTax = CyberSourceConnection.DoubleFormat(districtTax);
        
        // take the last TaxStatus object's properties and only reset the taxes
        // fields to the totals.
        reply = ((CyberSourceStatus)taxstatus[--i]).getValues();
        reply.setField("tax_total_tax", new Double(taxAmount).toString());
        reply.setField("tax_total_state_tax", new Double(stateTax).toString());
        reply.setField("tax_total_city_tax", new Double(cityTax).toString());
        reply.setField("tax_total_county_tax", new Double(countyTax).toString());
        reply.setField("tax_total_district_tax", new Double(districtTax).toString());
        
        return new CyberSourceStatus(reply);

    }

    
    /*
     * Call to cybersource to calculate taxes with this single CyberSource request
     */
    public TaxStatus calculate(TaxRequestInfo ccinfo, int i) throws ICSException {
        
        ICSReply reply = null;
        
        if (! mCsCon.getInitialized())
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFailedInitialize", "see above"));
        
        ICSClient client = mCsCon.getICSClient();
        ICSClientRequest request = mCsCon.getICSClientRequest();
                
        /*
         * fill in the required fields, check if not null, otherwise throw the
         * exception back to the tax manager
         */
        request.addApplication("ics_tax");
        
        // OrderId
        if (ccinfo.getOrderId() != null) 
            request.setMerchantRefNo(ccinfo.getOrderId());
        else 
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "OrderId")); 
                
        // MerchantId
        if (mCsCon.getMerchantId() != null) 
            request.setMerchantID(mCsCon.getMerchantId());
        else 
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "MerchantId")); 
                
        // Billing Address
        if (ccinfo.getBillingAddress() == null) 
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "BillingAddress"));
                
        String strBillToAddress = ccinfo.getBillingAddress().getAddress1();
        String strBillToCountry = ccinfo.getBillingAddress().getCountry();
        String strBillToCity = ccinfo.getBillingAddress().getCity();
        String strBillToState = ccinfo.getBillingAddress().getState();
        String strBillToZip = ccinfo.getBillingAddress().getPostalCode();

        // Billing Address Country
        if (strBillToCountry == null)
            strBillToCountry = "US";
        request.setBillCountry(strBillToCountry);
        
        // Billing Address Line1
        if (strBillToAddress != null)
            request.setBillAddress1(strBillToAddress);
        
        // Billing Address Line2 optional
        strBillToAddress = ccinfo.getBillingAddress().getAddress2();
        if (strBillToAddress != null)
            request.setBillAddress2(strBillToAddress);
        
        // Billing Address City
        if (strBillToCity != null)
            request.setBillCity(strBillToCity);
        else
            if (strBillToCountry.toUpperCase().equals("US") || 
                strBillToCountry.toUpperCase().equals("CA"))
                throw new CyberSourceException(CyberSourceConnection.msg.format(
                                               "CyberSourceFieldNull", "BillingAddressCity"));
        
        // Billing Address State - required for US and CA
        if (strBillToState != null)
            request.setBillState(strBillToState);
        else
            if (strBillToCountry.toUpperCase().equals("US") || 
                strBillToCountry.toUpperCase().equals("CA"))
                throw new CyberSourceException(CyberSourceConnection.msg.format(
                                               "CyberSourceFieldNull", "BillingAddressState"));
        
        // Billing Address Postal Code - required for US and CA
        if (strBillToZip != null)
            request.setBillZip(strBillToZip);
        else
            if (strBillToCountry.toUpperCase().equals("US") || 
                strBillToCountry.toUpperCase().equals("CA"))
                throw new CyberSourceException(CyberSourceConnection.msg.format(
                                               "CyberSourceFieldNull", "BillingAddressZip"));
        
        // Current shipping destination
        if (ccinfo.getShippingDestination(i) == null)
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", 
                                           "ShippingDestination[" + i + "]"));
        
        // shipping properties default to billing if not provided
        String strShipToAddress = null;
        String strShipToCountry = null;
        String strShipToCity = null;
        String strShipToState = null;
        String strShipToZip = null;
        
        // shipping destination properties
        if (ccinfo.getShippingDestination(i).getShippingAddress() != null) {
            
            strShipToAddress = ccinfo.getShippingDestination(i).getShippingAddress().getAddress1();
            if (strShipToAddress != null)
                request.setShipToAddress1(strShipToAddress);
            
            strShipToAddress = ccinfo.getShippingDestination(i).getShippingAddress().getAddress2();
            if (strShipToAddress != null)
                request.setShipToAddress2(strShipToAddress);
            
            strShipToCountry = ccinfo.getShippingDestination(i).getShippingAddress().getCountry();
            if (strShipToCountry == null)
                strShipToCountry = "US";
            request.setShipToCountry(strShipToCountry);
            
            strShipToCity = ccinfo.getShippingDestination(i).getShippingAddress().getCity();
            if (strShipToCity != null)
                request.setShipToCity(strShipToCity);
            
            strShipToState = ccinfo.getShippingDestination(i).getShippingAddress().getState();
            if (strShipToState != null)
                request.setShipToState(strShipToState);
            else
                if (strShipToCountry.toUpperCase().equals("US") || 
                    strShipToCountry.toUpperCase().equals("CA"))
                    throw new CyberSourceException(CyberSourceConnection.msg.format(
                                                   "CyberSourceFieldNull", 
                                                   "ShippingAddressState[" + i + "]"));
            
            strShipToZip = ccinfo.getShippingDestination(i).getShippingAddress().getPostalCode();
            if (strShipToZip != null)
                request.setShipToZip(strShipToZip);
            else
                if (strShipToCountry.toUpperCase().equals("US") ||
                    strShipToCountry.toUpperCase().equals("CA"))
                    throw new CyberSourceException(CyberSourceConnection.msg.format(
                                                   "CyberSourceFieldNull",
                                                   "ShippingAddressZip[" + i + "]"));
            
        }

        // Ship From properties - optional
        if (getShipFromCountry() != null)
            request.setField(ShipFromCountry, getShipFromCountry());

        if (getShipFromCity() != null)
            request.setField(ShipFromCity, getShipFromCity());
        
        if (getShipFromState() != null)
            request.setField(ShipFromState, getShipFromState());

        if (getShipFromZip() != null)
            request.setField(ShipFromZip, getShipFromZip());
        
        // Origin properties - optional
        if (getOriginCountry() != null)
            request.setField(AcceptanceCountry, getOriginCountry());

        if (getOriginCity() != null)
            request.setField(AcceptanceCity, getOriginCity());

        if (getOriginState() != null)
            request.setField(AcceptanceState, getOriginState());

        if (getOriginZip() != null)
            request.setField(AcceptanceZip, getOriginZip());
        
        // list of states/provinces excluded from tax responsibilities
        // only for US/CA requests
        if (getNoNexus() != null)
            request.setField(NoNexus, getNoNexus());

        // Currency Code - optional
        String currency = ccinfo.getShippingDestination(i).getCurrencyCode(); 
        if (currency != null)
            request.setCurrency(currency);
        
        // for VAT service (international requests)
        if (getSellerRegistrationNumber() != null)
            request.setField(SellerRegistration, getSellerRegistrationNumber());

        // The total amount of the shipping destination group
        double totald = ccinfo.getShippingDestination(i).getTaxableItemAmount();
        
        /* check the item's amount against the limit */
        if (totald > Double.MAX_VALUE)
            throw new CyberSourceException(CyberSourceConnection.msg.getString(
                                           "CyberSourceTotalTooLarge"));
        
        // taxable items in the destination group might be exposed to
        // different product codes that indicate whether the items should be
        // taxed with a non standard rate
        if (getUseProductCode()) {

            TaxableItem[] pItems = ccinfo.getShippingDestination(i).getTaxableItems();
            if (pItems == null) 
                throw new CyberSourceException(CyberSourceConnection.msg.format(
                                               "CyberSourceFieldNull", "TaxableItems"));

            for (int ii=0; ii < pItems.length; ii++) {
                ICSClientOffer offer = mCsCon.getICSClientOffer();
                // check the taxStatus to perform the product code setting 
                // for each item in the group
                if (TaxProductCodeSetting(pItems[ii]) == true)
                    // may be overriden by a subclass
                    assignTaxProductCode(pItems[ii], offer);
                totald = pItems[ii].getAmount();
                if (totald > Double.MAX_VALUE)
                    throw new CyberSourceException(CyberSourceConnection.msg.getString(
                                                   "CyberSourceTotalTooLarge"));
                offer.setAmount(totald);
                request.addOffer(offer);
            }
            
        }
        else {
            // use the amount of the shipping group for the offer.
            // this single offer represents the shipping group
            ICSClientOffer offer = mCsCon.getICSClientOffer();
            offer.setAmount(totald);
            request.addOffer(offer);
        }
        

        if (isLoggingDebug()) {
            /* String server = client.url.toString(); */
            System.out.println("\n----- request -----");
            System.out.println(request.toString());
        }
        
        /* blocking call to CyberSource server */
        reply = client.send(request);
        
        if (isLoggingDebug()) {
            System.out.println("----- response -----");
            System.out.println(reply.toString());
        }

        return new CyberSourceStatus(reply);
        
    }
    
    /**
     * Initialize the service
     */
    public void doStartService() throws ServiceException {
        if (mCsCon == null)
            throw new ServiceException(CyberSourceConnection.msg.getString(
                                       "CyberSourceServiceNull"));
    }
    
    /**
     * This function simply checks the taxStatus property in the taxableItem 
     *
     *
     * @param pItem An object that represents an item or a bunch of items of the same category
     * @see TaxableItem
     * @return true or false whether or not the taxStatus property is set
     */
    public boolean TaxProductCodeSetting(TaxableItem pItem) {
        if (pItem.getTaxStatus() != null)
            return true;
        return false;
    }

    /**
     * This method may be overidden by a subclass to set the product code
     * for each offer. Now the product code is provided by DCS in the taxStatus
     * property of the TaxableItem object. A user may choose another way to
     * propagate that data. In this case, this method will be overidden by a subclass.
     *
     * @param pItem An object in the destination group that represents an item or
     * a bunch of items of the same catergory
     * @see TaxableItem
     * @param pOffer Offer object to set data for the taxable item
     */
    public void assignTaxProductCode(TaxableItem pItem, ICSClientOffer pOffer)
        throws CyberSourceException {
        
        pOffer.setProductCode(pItem.getTaxStatus());
        pOffer.setQuantity(1);
        
        // some product codes require the product name and product sku
        // to be set
        if (pItem.getProductId() == null)
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "ProductId"));
        pOffer.setProductName(pItem.getProductId());
        
        if (pItem.getCatalogRefId() == null)
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "CatalogRefId"));
        pOffer.setMerchantProductSKU(pItem.getCatalogRefId());
        
    }

    /**
     * @return a String representation of this object
     */
    public String toString ()
    {
        StringBuffer buf = new StringBuffer ();
        buf.append (getClass ().getName ());
        buf.append ('[');
        buf.append (']');
        return buf.toString ();
    }

    /** Run some simple tests */
    public static void main(String args[]) {
        
        CyberSourceTax tax = new CyberSourceTax();
        tax.setLoggingDebug(true);
        
        CyberSourceConnection con = new CyberSourceConnection();
        con.setCsConfigFile(args[0]);
        con.setMerchantIdPropertyName("merchantID");

        try {
            con.doStartService();
        }
        catch (ServiceException e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }

        tax.setCsCon(con);
        //tax.setNoNexus("NY,CA");
        tax.setSellerRegistrationNumber("GB 123456789");
        
        TaxRequestInfoImpl ccinfo = new TaxRequestInfoImpl();
        ShippingDestination[] destinations = new ShippingDestination[2];
        ShippingDestinationImpl dest = new ShippingDestinationImpl();
        
        TaxableItem[] items = new TaxableItem[1];
        TaxableItemImpl item = new TaxableItemImpl();

        //ccinfo.setOrderId(args[0]);
        ccinfo.setOrderId("007");
        
        atg.core.util.Address address = new atg.core.util.Address();
        //address.setCity("Boston");
        //address.setState("MA");
        //address.setPostalCode("02215");
        //address.setCountry("US");
        
        //address.setCity("Chicago");
        //address.setState("IL");
        //address.setPostalCode("60606");
        address.setCountry("GB");
        ccinfo.setBillingAddress(address);
        
        dest.setTaxableItemAmount(279.20);
        item.setAmount(10.00);
        item.setTaxStatus("electronic_software");
        item.setProductId("Mike");
        item.setCatalogRefId("Pro2000");
        items[0] = item;

        dest.setTaxableItems(items);
        destinations[0] = dest;
              
        dest = new ShippingDestinationImpl();
        dest.setTaxableItemAmount(100.00);
        address = new atg.core.util.Address();
        address.setCity("Center");
        address.setState("CO");
        address.setPostalCode("81125");
        address.setCountry("US");

        dest.setShippingAddress(address);
        destinations[1] = dest;

        ccinfo.setShippingDestinations(destinations);
        
        TaxStatus status = null;
        
        status = tax.calculateTax(ccinfo);
        
        System.out.println(status.getAmount() + "\n" + 
                           status.getCityTax() + "\n" + 
                           status.getStateTax() + "\n" + 
                           status.getCountyTax() + "\n" + 
                           status.getTransactionId() + "\n" +
                           status.getErrorMessage()); 
                           
    } 


}
