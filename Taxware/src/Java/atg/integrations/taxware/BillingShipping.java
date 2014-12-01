/* <ATGCOPYRIGHT>
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
 * "Dynamo" is a trademark of Art Technology Group, Inc. </ATGCOPYRIGHT>
 */

package atg.integrations.taxware;

import atg.payment.avs.GenericAddressVerificationInfo;
import atg.nucleus.*;
import atg.core.util.Address;

/*
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/BillingShipping.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class BillingShipping extends GenericAddressVerificationInfo {
    
    // Class version string
    public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/taxware/BillingShipping.java#2 $$Change: 651448 $";
    
    protected boolean mUseBillAddress;
    
    public void setUseBillAddress(boolean pUseBillAddress) {
        mUseBillAddress = pUseBillAddress;
    }
    
    public boolean getUseBillAddress() {
        return mUseBillAddress;
    }
    
    public String getShipToCity() {
        return getAddress2().getCity();
    }
    
    public void setShipToCity(String pShipToCity) {
        getAddress2().setCity(pShipToCity);
    }
    
    public String getShipToState() {
        return getAddress2().getState();
    }
    
    public void setShipToState(String pShipToState) {
        getAddress2().setState(pShipToState);
    }
    
    public String getShipToZip() {
        return getAddress2().getPostalCode();
    }
    
    public void setShipToZip(String pShipToZip) {
        getAddress2().setPostalCode(pShipToZip);
    }
    
    public String getShipToCountry() {
        return getAddress2().getCountry();
    }
    
    public void setShipToCountry(String pShipToCountry) {
        getAddress2().setCountry(pShipToCountry);
    }

    public String getBillCity() {
        return getAddress1().getCity();
    }

    public void setBillCity(String pBillCity) {
        getAddress1().setCity(pBillCity);
    }    
    
    public String getBillState() {
        return getAddress1().getState();
    }

    public void setBillState(String pBillState) {
        getAddress1().setState(pBillState);
    }    
    
    public String getBillZip() {
        return getAddress1().getPostalCode();
    }
    
    public void setBillZip(String pBillZip) {
        getAddress1().setPostalCode(pBillZip);
    }    

    public String getBillCountry() {
        return getAddress1().getCountry();
    }
    
    public void setBillCountry(String pBillCountry) {
        getAddress1().setCountry(pBillCountry);
    }    
    
}
