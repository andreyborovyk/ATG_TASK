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

package atg.integrations.taxware;

import atg.payment.tax.TaxStatus;
import java.util.*;

/**
 *
 * <p>This class encapsulates a standard TaxWare system response
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxWareStatus.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class TaxWareStatus implements TaxStatus 
{
    

    // Class version string
    public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/TaxWareStatus.java#2 $$Change: 651448 $";
    
    // ---------------------------------------
    // properties from the PaymentStatus interface
    // ---------------------------------------
    
    private String mTransactionId;
    
    public String getTransactionId() {
        return mTransactionId;
    }
    
    public void setTransactionId(String pTransactionId) {
        mTransactionId = pTransactionId;
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
    
    // -----------------------------------------
    // properties from the TaxStatus interface
    // -----------------------------------------
    
    private double mAmount;
    
    public double getAmount() {
        return mAmount;
    }
    
    public void setAmount(double pAmount) {
        mAmount = pAmount;
    }
    
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
    
    // public constructor
    public TaxWareStatus (double amount, double stateTax, double countyTax, 
                   double cityTax, double districtTax, double countryTax) {
        
        setAmount(amount);
        setStateTax(stateTax);
        setCountyTax(countyTax);
        setCityTax(cityTax);
        setDistrictTax(districtTax);
        setCountryTax(countryTax);
        
        setTransactionSuccess(true);
        Date timestamp = new Date(System.currentTimeMillis());
        setTransactionTimestamp(timestamp);
        
    }
    
    // public constructor
    public TaxWareStatus (String error) {
        
        setErrorMessage(error);
        setTransactionSuccess(false);
        Date timestamp = new Date(System.currentTimeMillis());
        setTransactionTimestamp(timestamp);
        
    }
    
}
