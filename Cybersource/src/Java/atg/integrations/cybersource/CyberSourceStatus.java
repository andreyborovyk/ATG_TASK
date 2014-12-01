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

import atg.payment.creditcard.AuthorizationAddressVerificationStatus;
import atg.payment.tax.TaxStatus;
import atg.payment.avs.AddressVerificationStatus;

import java.util.*;

import com.cybersource.ics.base.message.*;
import com.cybersource.ics.base.exception.*;

/**
 *
 * <p>This class encapsulates a standard CyberSource system response.  This data comes
 * back from a majority of the commands.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/cybersource/CyberSourceStatus.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class CyberSourceStatus extends AuthorizationAddressVerificationStatus implements TaxStatus, AddressVerificationStatus
{
    
    //-------------------------------------
    // Class version string
    
    public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/cybersource/CyberSourceStatus.java#2 $$Change: 651448 $";
    
    ICSReply mValues = null;
    
    //---------------------------------
    // default constructor
    public CyberSourceStatus() {
    }

    // ---------------------------------------
    // properties from the PaymentStatus interface
    // ---------------------------------------
    
    private double mAmount;
    
    public double getAmount() {
        return mAmount;
    }
    
    public void setAmount(double pAmount) {
        mAmount = pAmount;
    }
    
    private String mTransactionId;
    
    public String getTransactionId() {
        return mTransactionId;
    }
    
    public void setTransactionId(String pTransactionId) {
        mTransactionId = pTransactionId;
    }
    
    private String mRequestToken;
    
    public String getRequestToken() {
        return mRequestToken;
    }
    
    public void setRequestToken(String pRequestToken) {
        mRequestToken = pRequestToken;
    }
    
    private boolean mTransactionSuccess;
    
    public boolean getTransactionSuccess() {
        return mTransactionSuccess;
    }
    
    public void setTransactionSuccess(boolean pTransactionSuccess) {
        mTransactionSuccess = pTransactionSuccess;
    }
    
    private String mErrorMessage;
    
    public String getErrorMessage() {
        return mErrorMessage;
    }
    
    public void setErrorMessage(String pErrorMessage) {
        mErrorMessage = pErrorMessage;
    }
    
    private Date mTransactionTimestamp;
    
    public Date getTransactionTimestamp() {
        return mTransactionTimestamp;
    }
    
    public void setTransactionTimestamp(Date pTransactionTimestamp) {
        mTransactionTimestamp = pTransactionTimestamp;
    }
    
    // ---------------------------------------
    // properties from the CreditCard interface
    // ---------------------------------------
    
    private Date mAuthorizationExpiration;
    
    public Date getAuthorizationExpiration() {
        return mAuthorizationExpiration;
    }
    
    public void setAuthorizationExpiration(Date pAuthorizationExpiration) {
        mAuthorizationExpiration = pAuthorizationExpiration;
    }
    
    /**
     * Address Verification Result returned from authorization
     */
    private String mAvsCode;
    
    public void setAvsCode(String pAvsCode) {
        mAvsCode = pAvsCode;
    }
    
    public String getAvsCode() {
        return mAvsCode;
    }
    
    /**
     * A full descriptive address verification result message
     */
    private String mAvsDescriptiveResult;
    
    public void setAvsDescriptiveResult(String pAvsDescriptiveResult) {
        mAvsDescriptiveResult = pAvsDescriptiveResult;
    }
    
    public String getAvsDescriptiveResult() {
        return mAvsDescriptiveResult;
    }
    
    // -----------------------------------------
    // properties from the TaxStatus interface
    // -----------------------------------------
    
    private double mCityTax;
    
    public double getCityTax() {
        return mCityTax;
    }
    
    public void setCityTax(double pCityTax) {
        mCityTax = pCityTax;
    }
    
    private double mStateTax;
    
    public double getStateTax() {
        return mStateTax;
    }
    
    public void setStateTax(double pStateTax) {
        mStateTax = pStateTax;
    }
    
    private double mCountyTax;
    
    public double getCountyTax() {
        return mCountyTax;
    }
    
    public void setCountyTax(double pCountyTax) {
        mCountyTax = pCountyTax;
    }
    
    private double mDistrictTax;
    
    public double getDistrictTax() {
        return mDistrictTax;
    }
    
    public void setDistrictTax(double pDistrictTax) {
        mDistrictTax = pDistrictTax;
    }
    
    private double mCountryTax;
    
    public double getCountryTax() {
        return mCountryTax;
    }
    
    public void setCountryTax(double pCountryTax) {
        mCountryTax = pCountryTax;
    }
    

    /**
     * Sets property values. This is a dictionary of the raw data from the
     * CyberSource server.  When a new one is set, use it to determine some
     * common values and cache them in properties.  For now both keys and
     * values are strings, but this may change in the future.
     **/
    public void setValues(ICSReply mValues) {
        
        this.mValues = mValues;
        
        try {
            switch(mValues.getReplyCode()) 
                {
                case 1:
                    setTransactionSuccess(true);
                    break;
                case 0:
                    setTransactionSuccess(false);
                    setErrorMessage(mValues.getErrorMessage());
                    break;
                case -1:
                    setTransactionSuccess(false);
                    setErrorMessage(mValues.getErrorMessage());
                    break;
                default:
                    setTransactionSuccess(false);
                    setErrorMessage(mValues.getErrorMessage());
                    break;
                }
        }
        catch(ICSException exc) {
            setTransactionSuccess(false);
            String msg = exc.getMessage();
            if (msg == null)
              msg = exc.toString();
            setErrorMessage("Exception parsing ICSReply object: " + msg);
        }
        
        setTransactionId((String)getValue("request_id"));
        setRequestToken((String)getValue("request_token"));
        
        String tax_total = (String)getValue("tax_total_tax");
        if (tax_total != null)
            setAmount(Double.parseDouble(tax_total));
        String total = (String)getValue("total_amount");
        if (total != null)
            setAmount(Double.parseDouble(total));
        String tax_state = (String)getValue("tax_total_state_tax");
        if (tax_state != null)
            setStateTax(Double.parseDouble(tax_state));
        String tax_city = (String)getValue("tax_total_city_tax");
        if (tax_city != null)
            setCityTax(Double.parseDouble(tax_city));
        String tax_county = (String)getValue("tax_total_county_tax");
        if (tax_county != null)
            setCountyTax(Double.parseDouble(tax_county));
        String tax_district = (String)getValue("tax_total_district_tax");
        if (tax_district != null)
            setDistrictTax(Double.parseDouble(tax_district));
                
        setAvsCode((String)getValue("auth_auth_avs"));
        setAvsDescriptiveResult(getAvsDescriptiveValue(mAvsCode));
        if (mAvsCode != null && mAvsDescriptiveResult == null)
            setAvsDescriptiveResult("Refer to the manual to see the description of the result");
        
        Date timestamp = new Date(System.currentTimeMillis());
        setTransactionTimestamp(timestamp);
        
        String error = (String)getValue("error_mess_proc");
        if (error != null)
            setErrorMessage(error);
        
    }
    
    /**
     * Gets property values. This is a dictionary of the raw data from the
     * CyberSource server.
     **/
    public ICSReply getValues() {
        return mValues;
    }
    
    /**
     * Gets a single property value from the values dictionary of the raw data
     * from the CyberSource server.
     **/
    public Object getValue(String key) {
        if(mValues == null)
            return null;
        
        return mValues.getField(key);
    }
    
    /**
     * A constructor that takes an ICSReply object
     */
    public CyberSourceStatus(ICSReply pReply) {
        if (pReply != null)
            setValues(pReply);
        else
            setValues(new ICSReply());
    }
    
    /**
     * A String representation of the class
     */
    public String toString () {
        return mValues.toString();
    }
    
}
