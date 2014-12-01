/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

package atg.integrations.taxware;

import atg.payment.tax.*;
import atg.payment.avs.AddressVerificationInfo;
import atg.core.util.Address;
import atg.core.util.StringUtils;
import atg.nucleus.*;
import java.util.Date;
import java.text.DecimalFormat;

/** <p> Using the associated TaxService, this class calculates the
 * sales/world tax for the items. This class implements the multiple items
 * requests sent to Taxware, where items are represented
 * by the shipping destination group objects passed in TaxRequestInfo.
 * Also, it will use the VeraZip system to provide accurate means of
 * verifying the city-state-zip address info. Using verazip can be triggered,
 * the SalesUse system can verify address to some extent, but not as good as
 * VeraZip.
 *
 * @author Michael Traskunov
 * @see SalesTaxService
 * @see TaxRequest
 * @see TaxResult
 * @see TaxRequestInfo
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxWareCalculateTax.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class TaxWareCalculateTax extends GenericService implements TaxProcessor  {

    // Class version string
    public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxWareCalculateTax.java#2 $$Change: 651448 $";
    
    /* Taxware default maximum number of input records */
    public final static int MAXIMUM_RECORD_SIZE = 100;

    /* Service to perform the verazip check */
    protected TaxWareVerifyZipInfo mVerifyZipInfo;
    
    public TaxWareVerifyZipInfo getVerifyZipInfo() {
        return mVerifyZipInfo;
    }
    
    public void setVerifyZipInfo(TaxWareVerifyZipInfo pVerifyZipInfo) {
        mVerifyZipInfo = pVerifyZipInfo;
    }
    
    /* The object passed to the verazip check process, this class 
     * implements the VeraZipable interface
     */
    protected VeraZipOrderImpl zipInfo;
    
    /** Reference to the sales tax service. */
    protected TaxService mTaxService;
    
    /** Whether or not to submit shipping cost to TaxWare as freight. */
    protected boolean mSubmitShipping = true;
    
    /** Whether or not to attempt to use VeraZip. */
    protected boolean mUseVerazip = false;
    
    /** Set whether or not to attempt to use VeraZip. */
    public void setUseVerazip(boolean pUseVerazip) {
        mUseVerazip = pUseVerazip;
    }
    
    /** Get whether or not to attempt to use VeraZip. */
    public boolean getUseVerazip() {
        return mUseVerazip;
    }
    
    /** Set whether shipping cost from the order will be submitted to
     * taxware as freight 
     */
    public void setSubmitShipping(boolean pSubmitShipping) {
        mSubmitShipping = pSubmitShipping;
    }
    
    /** Get whether shipping cost from the order will be submitted to
     * taxware as freight 
     */
    public boolean getSubmitShipping() {
        return mSubmitShipping;
    }
    
    /**  Whether or not to write this transaction to the TaxWare audit file.   */
    protected boolean mWritingAudit = false;
    
    /**
     * Set whether to write this transaction to the TaxWare audit file.
     * The default is null.
     * @param boolean whether or not to write
     */
    public void setWritingAudit(boolean pWritingAudit) {
        mWritingAudit = pWritingAudit;
    }
    
    /**
     * Returns whether we are writing this transaction to the TaxWare audit file.
     */
    public boolean getWritingAudit(){
        return mWritingAudit;
    }
    
    /** Constructor for the service. */
    public TaxWareCalculateTax() {}
    
    /** Set the TaxService used to generate the TaxRequest. */
    public void setTaxService(TaxService pTaxService) {
        mTaxService = pTaxService;
    }
    
    /** Get the TaxService used to generate the TaxRequest. */
    public TaxService getTaxService() { return(mTaxService); }

    /** Returns the TaxService appropriate for the request.
     * The default implementation simply returns the taxService
     * property.
     */
    protected TaxService getAppropriateSalesTaxService(TaxRequestInfo pOrder)
    {
        // just return our TaxService by default. 
        // May be overridden by sub-classes
        return(getTaxService());
    }
    
    
    /** Sets the error message and returns the TaxStatus object */
    protected TaxStatus setErrorAndGetReturnValue(String pKey, String pMessage) {
        
        String error;
        
        error = SalesTaxService.msg.format(pKey, pMessage);
        if (isLoggingError()) {
            logError(error);
        }
        
        return new TaxWareStatus(error);
        
    }
    
    
    /** Modifies the request before submitting it to TaxWare.<p>
     *
     * In this version, adds GEOCODE information from verazip
     * if available. <p>
     *
     * You may override this method to prevent calling verazip
     * or to add addition information to the request, like a non
     * standard tax rates. <p>
     */
    protected void modifyRequest(AddressVerificationInfo pOrder, TaxRequest request) {
        
        if (pOrder instanceof VeraZipable) {
            VeraZipable zOrder = (VeraZipable)pOrder;
            
            ZipResultItem zriShipping = VeraZipAccess.getZipResultItemForAddrName(zOrder,
                                                      VeraZipable.SHIP_TO_ADDRESS_NAME);
            
            if (null != zriShipping) {
                if (isLoggingDebug())
                    logDebug("TaxWareCalculateSalesTax: Setting shipping geocode to " + 
                             zriShipping.getGeoCode());
                
                // set the ship to geo code here!!!
                request.setDstGeoCode(zriShipping.getGeoCode());
            }
            
            ZipResultItem zriBilling = VeraZipAccess.getZipResultItemForAddrName(zOrder,
                                                     VeraZipable.BILLING_ADDRESS_NAME);
            
            if (null != zriBilling) {
                if (isLoggingDebug()) 
                    logDebug("TaxWareCalculateSalesTax: Setting billing geocode to " + 
                             zriBilling.getGeoCode());
                
                // set the POA to geo code here!!!
                request.setPOAGeoCode(zriBilling.getGeoCode());
            }
        }

        // all of this could be actually added to the subclass method
        request.setFieldValue("AUDFILEIND", (getWritingAudit() ? "1" : "2"));
    
    }
    
    
    /* 
     * Implementation of the TaxProcessor interface 
     * TaxRequestInfo contains shipping destination objects that will represent
     * the individual items(records) passed in the Taxware request. <p>
     * 
     * This wrapper method returns a single taxStatus object where all taxes are summed up
     * <p>
     * @param ccinfo An object that contains information about items being purchased
     * @see TaxRequestInfo
     * @param TaxStatus object that contains results of the calculation
     * @see TaxStatus
     */
    public TaxStatus calculateTax(TaxRequestInfo ccinfo) {
        
        TaxResult[] taxresult = null;
        
        try {
            taxresult = calculateProcess(ccinfo);
        }
        catch (TaxwareException excpt) {
            String msg = excpt.getMessage();
            if (msg == null)
                msg = excpt.toString();
            return(setErrorAndGetReturnValue("TXWR_ERROR_CALC_TAX", msg));
        }
        
        // adds up all taxes if the call was successful
        return (recordResult(taxresult));
        
    }
 
    /* 
     * Implementation of the TaxProcessor interface 
     * TaxRequestInfo contains shipping destination objects that will represent
     * the individual items(records) passed in the Taxware request. <p>
     *
     * This method returns taxStatus objects at the shipping destination level
     * <p>
     * @param ccinfo An object that contains information about items being purchased
     * @see TaxRequestInfo
     * @param TaxStatus object that contains results of the calculation
     * @see TaxStatus
     */
    public TaxStatus[] calculateTaxByShipping(TaxRequestInfo ccinfo) {
        
        TaxResult[] taxresult = null;
                        
        try {
            taxresult = calculateProcess(ccinfo);
        }
        catch (TaxwareException excpt) {
            String msg = excpt.getMessage();
            if (msg == null)
                msg = excpt.toString();
            TaxStatus taxStat = setErrorAndGetReturnValue("TXWR_ERROR_CALC_TAX", msg);
            TaxStatus[] taxStatus = {taxStat};
            return taxStatus;
        }
        
        // format the tax amounts and return the TaxStatus array
        return recordResultByShipping(taxresult);
        
    }
     
    /*
     * The wrapper method to iterate through ShippingDestinations 
     * and make calls to Taxware
     */
    public TaxResult[] calculateProcess(TaxRequestInfo ccinfo) throws TaxwareException {
        
        TaxResult taxresult[] = null, taxcurrent[] = null;
        int until = 0, index = 0, ii = 0;
        
        if (ccinfo.getShippingDestinations() == null)
            throw new TaxwareMinorException(SalesTaxService.msg.format("TAXWARE_FIELD_NULL", 
                                                                       "ShippingDestinations"));
        
        int SIZE = ccinfo.getShippingDestinations().length;
        taxresult = new TaxResult[SIZE];
        
        while (index < SIZE) {
            if (index + MAXIMUM_RECORD_SIZE < SIZE)
                until = index + MAXIMUM_RECORD_SIZE;
            else
                until = SIZE;
            taxcurrent = calculateTaxes(ccinfo, index, until);
            for (int i = 0; i < taxcurrent.length; i++) {
                taxresult[ii++] = taxcurrent[i];
                if (isLoggingDebug())
                    System.out.println(taxcurrent[i].toString());
            }
            index += MAXIMUM_RECORD_SIZE;
        }
        
        return taxresult;
        
    }

    
    /* Calculate taxes with this mutliple item taxware request */
    public TaxResult[] calculateTaxes(TaxRequestInfo ccinfo, int index, int until) 
        throws TaxwareException 
    {
        
        // for the time being, we just assume that all
        // the items in a given order fall in the same tax category
        // and are fully taxable. Since Taxware supports multiple shipping
        // destinations per order, each individual record passed in the
        // Taxware input buffer designates all items shipped to a specific destination
        
        String error = null;
        int i = 0;
        
        TaxService taxService = getAppropriateSalesTaxService(ccinfo);
                        
        if (taxService == null)
            throw new TaxwareMinorException(SalesTaxService.msg.getString("TAXWARE_SERVICE_NULL"));
        if (taxService.getFailedToInitialize())
            throw new TaxwareMinorException(SalesTaxService.msg.getString("TAXWARE_FAILED_TO_INITIALIZE"));
        
        TaxWareVerifyZipInfo zipService = getVerifyZipInfo();
        
        if (getUseVerazip() && zipService == null)
            throw new TaxwareMinorException(SalesTaxService.msg.getString("VERAZIP_SERVICE_NULL"));
        
        Date dateTax = new Date(System.currentTimeMillis());
        
        if (ccinfo.getBillingAddress() == null)
            throw new TaxwareMinorException(SalesTaxService.msg.format("TAXWARE_FIELD_NULL", 
                                                                       "BillingAddress"));
        TaxRequest[] taxrequest = new TaxRequest[until-index];
        
        // billing address properties
        String strBillToCountry = ccinfo.getBillingAddress().getCountry();
        String strBillToCity = ccinfo.getBillingAddress().getCity();
        String strBillToState = ccinfo.getBillingAddress().getState();
        String strBillToZip = ccinfo.getBillingAddress().getPostalCode();
        
        if (strBillToCountry == null || strBillToCountry.toUpperCase().equals("USA"))
            strBillToCountry = "US";
        
        // iterate through the shipping destination groups, creating an array
        // of TaxRequest objects that will be passed as individual input
        // records to Taxware.
        for (int ii = index; ii < until; ii++) {
            
            // defaults shipping to billing
            String strShipToCountry = strBillToCountry;
            String strShipToCity = strBillToCity;
            String strShipToState = strBillToState;
            String strShipToZip = strBillToZip;
            
            if (ccinfo.getShippingDestination(ii) == null) {
                throw new TaxwareMinorException(
                                                SalesTaxService.msg.format("TAXWARE_FIELD_NULL", 
                                                "ShippingDestination[" + ii + "]"));
            }       
            
            if (ccinfo.getShippingDestination(ii).getShippingAddress() != null) {
                strShipToCountry = ccinfo.getShippingDestination(ii).getShippingAddress().getCountry();
                strShipToCity = ccinfo.getShippingDestination(ii).getShippingAddress().getCity();
                strShipToState = ccinfo.getShippingDestination(ii).getShippingAddress().getState();
                strShipToZip = ccinfo.getShippingDestination(ii).getShippingAddress().getPostalCode();
                if (strShipToCountry == null || strShipToCountry.toUpperCase().equals("USA"))
                    strShipToCountry = "US";
            }
            
            // total of the items in the shipping destination group
            double dSubtotal = ccinfo.getShippingDestination(ii).getTaxableItemAmount();
            // taxware might impose taxes on the shipping cost
            double dShipping = 0;
            if (getSubmitShipping())
                dShipping = ccinfo.getShippingDestination(ii).getShippingAmount();
            String strCurrencyCode = ccinfo.getShippingDestination(ii).getCurrencyCode();
            
            long centsSubtotal = (long)(dSubtotal * 100);
            long centsShipping = (long)(dShipping * 100);
            // !!! discount vs. non-discount???
            long centsDiscountPrice = centsSubtotal;
            
            // taxware doesn't automatically overwrites the billing info 
            // with shipping like other systems would do
            taxrequest[i] = taxService.createRequest(strShipToCountry,
                                                     TrimData.trimCity(strShipToCity),
                                                     strShipToState,
                                                     TrimData.trimZip(strShipToZip),
                                                     strBillToCountry,
                                                     TrimData.trimCity(strBillToCity),
                                                     strBillToState,
                                                     TrimData.trimZip(strBillToZip),
                                                     strCurrencyCode,
                                                     centsSubtotal, centsDiscountPrice, 
                                                     centsShipping, dateTax);
            
            if (isLoggingDebug()) {
                System.out.println(taxrequest[i].toString());
            }

            VeraZipOrderImpl veraZipInfo = null;
            
            // set the address properties for the verazip object
            if (getUseVerazip()) {
                veraZipInfo = new VeraZipOrderImpl(this); 
                veraZipInfo.setAddress1(new Address());
                veraZipInfo.setAddress2(new Address());

                veraZipInfo.setShipToCountry(strShipToCountry);
                veraZipInfo.setShipToCity(strShipToCity);
                veraZipInfo.setShipToState(strShipToState);
                veraZipInfo.setShipToZip(strShipToZip);
                veraZipInfo.setBillCountry(strBillToCountry);
                veraZipInfo.setBillCity(strBillToCity);
                veraZipInfo.setBillState(strBillToState);
                veraZipInfo.setBillZip(strBillToZip);
                
                // why verify twice if the addresses are the same...
                if (nullSafeStringEquals(strShipToCountry,strBillToCountry) && 
                    nullSafeStringEquals(strShipToCity,strBillToCity) && 
                    nullSafeStringEquals(strShipToState,strBillToState) &&
                    nullSafeStringEquals(strShipToZip,strBillToZip)) 
                    veraZipInfo.setUseBillAddress(true);
                else
                    veraZipInfo.setUseBillAddress(false);
                
                // call to verazip to verify the city-state-zip info
                boolean result = zipService.verifyZip(veraZipInfo);
                if (!result) {
                    //we know there is only one zip result since we only created 1 
                    //veraZipInfo
                   ZipResult zipResult = null;
                   String errorMsg = null;
                   for(int z=0;z<veraZipInfo.getMaxCountOfZipAddresses();z++) {
                     zipResult = veraZipInfo.getZipResultAt(z);
                     if(zipResult != null) {
                      errorMsg = zipResult.getErrorMessage();
                      if(!StringUtils.isEmpty(errorMsg))
                        throw new TaxwareCriticalException(errorMsg);
                     }
                   }
                }
                
            }
            
            // add the GEO code (optional) or perform some additional settings
            modifyRequest(veraZipInfo, taxrequest[i]);
            i++;
            
        }
        
        // calculate our taxes in this call
        return taxService.calculateSalesTax(taxrequest);
        
    }
    
    /* 
     * Helper function which returns a null-safe string equals.
     * Empty string does not count as null for equality purposes
     */
    static final boolean nullSafeStringEquals(String strOne, String strTwo) {
        if (strOne == null) {
            if (strTwo == null)
                return(true);
            return(false);
        } else 
            if (strTwo == null)
                return(false);
        
        return(strOne.equalsIgnoreCase(strTwo));
    }
    
    /**
     * Adds up the taxresults' tax properties 
     * @param An array of taxResult objects
     * @see TaxResult
     * @return TaxStatus that contains the results of the calculation
     * @see TaxStatus
     */
    protected TaxStatus recordResult(TaxResult[] taxresult) {
        
        double taxAmount = 0;
        double countryTax = 0;
        double territoryTax = 0;
        double provinceTax = 0;
        double countyTax = 0;
        double cityTax = 0;
        
        for (int i = 0; i < taxresult.length && taxresult[i] != null; i++) {
        
            countryTax += taxresult[i].getCountryTaxAmount();
            territoryTax += taxresult[i].getTerritoryTaxAmount();
            provinceTax += taxresult[i].getProvinceTaxAmount() +
                taxresult[i].getSecProvinceTaxAmount();
            countyTax += taxresult[i].getCountyTaxAmount() +
                taxresult[i].getSecCountyTaxAmount();
            cityTax += taxresult[i].getCityTaxAmount() +
                taxresult[i].getSecCityTaxAmount();
            
        }
        
        // is there a better way to format a double?
        //countyTax = DoubleFormat(countyTax);
        //countryTax = DoubleFormat(countryTax);
        //provinceTax = DoubleFormat(provinceTax);
        //cityTax = DoubleFormat(cityTax);
        //territoryTax = DoubleFormat(territoryTax);
        
        taxAmount = countryTax + territoryTax + 
            provinceTax + countyTax + cityTax;
        //taxAmount = DoubleFormat(taxAmount);
        
        return new TaxWareStatus(taxAmount, provinceTax,
                                 countyTax, cityTax, 
                                 territoryTax, countryTax); 

    }


    /**
     * Returns an array of taxStatus objects at the shipping level
     *
     * @param An array of taxResult objects
     * @see TaxResult
     * @return An array of taxStatus that contains the results of the calculation
     * @see TaxStatus
     */
    protected TaxStatus[] recordResultByShipping(TaxResult[] taxresult) {

        double taxAmount = 0;
        double countryTax = 0;
        double territoryTax = 0;
        double provinceTax = 0;
        double countyTax = 0;
        double cityTax = 0;
        
        TaxStatus[] taxstatus = new TaxStatus[taxresult.length];
        
        for (int i = 0; i < taxresult.length && taxresult[i] != null; i++) {
        
            countryTax = taxresult[i].getCountryTaxAmount();
            territoryTax = taxresult[i].getTerritoryTaxAmount();
            provinceTax = taxresult[i].getProvinceTaxAmount() +
                taxresult[i].getSecProvinceTaxAmount();
            countyTax = taxresult[i].getCountyTaxAmount() +
                taxresult[i].getSecCountyTaxAmount();
            cityTax = taxresult[i].getCityTaxAmount() +
                taxresult[i].getSecCityTaxAmount();
            
            // is there a better way to format a double?
            //countyTax = DoubleFormat(countyTax);
            //countryTax = DoubleFormat(countryTax);
            //provinceTax = DoubleFormat(provinceTax);
            //cityTax = DoubleFormat(cityTax);
            //territoryTax = DoubleFormat(territoryTax);
            
            taxAmount = countryTax + territoryTax + 
                provinceTax + countyTax + cityTax;
            //taxAmount = DoubleFormat(taxAmount);
            
            taxstatus[i] = new TaxWareStatus(taxAmount, provinceTax,
                                             countyTax, cityTax, 
                                             territoryTax, countryTax);  
            
        }
        
        return taxstatus;
        
    }
    
    /* Helper function to format a double */
    public static double DoubleFormat(double a) {
        DecimalFormat format = (DecimalFormat)DecimalFormat.getInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        format.setGroupingUsed(false);
        return Double.parseDouble(format.format(a));
    }

    /* Initialize the verazip data structure */
    public void doStartService() {
        if (getUseVerazip()) {
            GenericService ger = new GenericService();
            zipInfo = new VeraZipOrderImpl(ger); 
            zipInfo.setAddress1(new Address());
            zipInfo.setAddress2(new Address());
        }
    }

 
    /** Run some simple tests */
    public static void main(String args[]) {
        
        TaxWareCalculateTax tax = new TaxWareCalculateTax();
        TaxWareVerifyZipInfo zip = new TaxWareVerifyZipInfo();
        //zip.setLoggingDebug(true);
        tax.setLoggingDebug(true);
        
        //SalesTaxService ser = new SalesTaxService();
        WorldTaxService ser = new WorldTaxService();
        ser.setCompanyId("weewidgets");
        ser.setShipFromCountry("US");
        //ser.setTaxSelParm("3");
        
        /***************************WORLD TAX TESTING*****************************/
        
        //ser.setSellerRegNumber("IT12345678901");
        //ser.setSellerRegNumber("AU12 345 678 901");
        //ser.setDocumentNumber("121212");
        //ser.setTransactionType("2");
        //ser.setTaxCode("01");
        //ser.setCommodityCode("89809905");
        //ser.setCommodityCode("84716090");
        //ser.setCommodityCode("10110000");        
        /***************************************************************************/
        
        tax.setTaxService(ser);
        tax.setVerifyZipInfo(zip);
        tax.setSubmitShipping(false);
        tax.setUseVerazip(true);
        
        tax.doStartService();
        
        try {
            ser.doStartService();
        }
        catch (ServiceException e) {
            System.out.println(e.toString());
        }
        
        int n = Integer.parseInt(args[0]);
        
        TaxRequestInfoImpl ccinfo = new TaxRequestInfoImpl();
        ShippingDestination[] destinations = new ShippingDestination[n];
        
        ShippingDestinationImpl dest = new ShippingDestinationImpl();
        dest.setTaxableItemAmount(1600.00);
        dest.setShippingAmount(25.00);
        Address address = new Address();
        address.setCity("Boston");
        address.setState("MA");
        address.setPostalCode("02215");
        address.setCountry("US");

        //address.setCity("Kanata");
        //address.setState("Ontario");
        //address.setPostalCode("K2K3A2");
        //address.setCountry("CA");
        
        //address.setCity("Reading Berkshire");
        //address.setPostalCode("RG61PT");
        //address.setCountry("GB");
        
        // Billing Address
        ccinfo.setBillingAddress(address);
        
        // Shipping Address
        dest.setShippingAddress(address);
        //dest.setCurrencyCode("GBP");
        
        destinations[0] = dest;
                
        dest = new ShippingDestinationImpl();
        dest.setTaxableItemAmount(279.20);
        address = new Address();
        address.setCity("Chicago");
        address.setState("IL");
        address.setPostalCode("60606");
        address.setCountry("US");

        //address.setCity("Center");
        //address.setState("CO");
        //address.setPostalCode("81125");
        //address.setCountry("US");
        
        //address.setCity("Bordeaux");
        //address.setPostalCode("33000");
        //address.setCountry("FR");
        
        // Shipping Address
        dest.setShippingAddress(address);
        //dest.setCurrencyCode("GBP");

        for (int i=1; i < n; i++) 
            destinations[i] = dest;
        
        ccinfo.setShippingDestinations(destinations);
        
        TaxStatus status = null;
        
        status = tax.calculateTax(ccinfo);
        
        try {
            ser.doStopService();
        }
        catch (ServiceException e) {
            System.out.println(e.toString());
        }
        
        System.out.println(status.getAmount() + "\n" +
                           status.getCityTax() + "\n" +
                           status.getStateTax() + "\n" +
                           status.getCountyTax() + "\n" +
                           status.getCountryTax() + "\n" +
                           status.getErrorMessage());
        
    } 
    
}

