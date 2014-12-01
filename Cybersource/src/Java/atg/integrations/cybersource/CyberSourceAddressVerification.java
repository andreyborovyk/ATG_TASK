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

import atg.payment.avs.*;
import atg.nucleus.*;
import atg.core.util.StringUtils;

import java.net.*;

import com.cybersource.ics.client.*;
import com.cybersource.ics.client.message.*;
import com.cybersource.ics.base.exception.*;
import com.cybersource.ics.base.message.*;

/**
 *
 * <p>This class manages the communications to a CyberSource AVS system
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/cybersource/CyberSourceAddressVerification.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 **/

public class CyberSourceAddressVerification extends GenericService implements AddressVerificationProcessor {
    
    //-------------------------------------
    // Class version string
    
    public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/cybersource/CyberSourceAddressVerification.java#2 $$Change: 651448 $";
    
    /**
     * Default constructor
     */
    public CyberSourceAddressVerification () {
    }
    
    /**
     * CyberSourceConnection module that is resposible for communication
     * with the cybersource payment system
     */
    public CyberSourceConnection mCsCon = null;
    
    public void setCsCon(CyberSourceConnection pCsCon) {
        mCsCon = pCsCon;
    }
    
    public CyberSourceConnection getCsCon() {
        return mCsCon;
    }  
    
    /**
     * Used to check the zip code for the shipping address
     */
    public boolean mCheckZip;
    
    public void setCheckZip(boolean pCheckZip) {
        mCheckZip = pCheckZip;
    }

    public boolean getCheckZip() {
        return mCheckZip;
    }


    /* 
     * Implementation of the AddressVerificationProcessor interface
     *
     * @param An Object that contains the user's address info
     * @see AddressVerificationInfo
     * @return An object that contains the results
     * @see AddressVerificationStatus
     */
    public AddressVerificationStatus verifyAddress(AddressVerificationInfo ccinfo) {
        try {
            return verification(ccinfo);
        }
        catch(ICSException exc) {
          String msg = exc.getMessage();
          if (msg == null)
            msg = exc.toString();
          return mCsCon.processError("CyberSourceAddressVerifyFailed", msg);
        }
    }
    
    
    /*
     * Perform address verification process with this CyberSource request
     */
    public AddressVerificationStatus verification(AddressVerificationInfo ccinfo) 
        throws ICSException
    {

        ICSReply reply = null;
        String[] address;
        
        if (!mCsCon.getInitialized())
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFailedInitialize", "see above"));
 
        ICSClient client = mCsCon.getICSClient();
        ICSClientRequest request = mCsCon.getICSClientRequest();
        
        /*
         * fill in the required fields, check if not null, otherwise throw the
         * exception back to the payment manager
         */
        
        request.addApplication("ics_dav");
        
        // PaymentId
        if (ccinfo.getPaymentId() != null) 
            request.setMerchantRefNo(ccinfo.getPaymentId());
        else 
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "PaymentId")); 

        // MerchantId
        if (mCsCon.getMerchantId() != null) 
            request.setMerchantID(mCsCon.getMerchantId());
        else 
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "MerchantId"));
                 
        // Billing Address
        if (ccinfo.getAddress1() == null)
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "BillingAddress"));
                 
        // Billing Address Line1
        if (ccinfo.getAddress1().getAddress1() == null)
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "BillingAddressLine1"));
         
        /* CS DAV doesn't really care about apartment or suite number, it only checks */
        /* the street number, street name, city, state, zip combination */
        /* Apartment or suite number is optional and must be only entered in the */
        /* Address2 field, otherwise the CS DAV check will fail */
        if (ccinfo.getAddress1().getAddress2() == null) {
            
            /* split address on the first possible comma delimiter */
            address = new String[2];
            StringUtils.splitStringAtCharacter(ccinfo.getAddress1().getAddress1(), ',',
                                               address, 0);
            request.setBillAddress1(address[0]);
            if (address[1] != null)
                request.setBillAddress2(address[1]);

        }
        else {
            request.setBillAddress1(ccinfo.getAddress1().getAddress1());
            request.setBillAddress2(ccinfo.getAddress1().getAddress2());
        }
            
        // Billing Address City
        if (ccinfo.getAddress1().getCity() != null)
            request.setBillCity(ccinfo.getAddress1().getCity());
        else
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldNull", "BillingAddressCity"));

        // Billing Address Country
        if (ccinfo.getAddress1().getCountry() == null)
            ccinfo.getAddress1().setCountry("US");
        request.setBillCountry(ccinfo.getAddress1().getCountry());
        
        // Billing Address State - required for US and CA
        if (ccinfo.getAddress1().getState() != null)
            request.setBillState(ccinfo.getAddress1().getState());
        else
            if (ccinfo.getAddress1().getCountry().toUpperCase().equals("US") ||
                ccinfo.getAddress1().getCountry().toUpperCase().equals("CA"))
                throw new CyberSourceException(CyberSourceConnection.msg.format(
                                               "CyberSourceFieldNull", "BillingAddressState"));
        
        // Billing Address Postal Code - required for US and CA
        if (ccinfo.getAddress1().getPostalCode() != null)
            request.setBillZip(ccinfo.getAddress1().getPostalCode());
        else
            if (ccinfo.getAddress1().getCountry().toUpperCase().equals("US") ||
                ccinfo.getAddress1().getCountry().toUpperCase().equals("CA"))
                throw new CyberSourceException(CyberSourceConnection.msg.format(
                                               "CyberSourceFieldNull", "BillingAddressZip"));
                          
        // Shipping Address - optional
        if (ccinfo.getAddress2() != null) {
            
            // Shipping Address Line1
            if (ccinfo.getAddress2().getAddress1() != null) {
                // same as the billing address parsing
                if (ccinfo.getAddress2().getAddress2() == null) {
                    
                    /* split on the first possible comma delimiter */
                    address = new String[2];
                    StringUtils.splitStringAtCharacter(ccinfo.getAddress2().getAddress1(), 
                                                       ',', address, 0);
                    request.setShipToAddress1(address[0]);
                    if (address[1] != null)
                        request.setShipToAddress2(address[1]);
                    
                }
                else {
                    request.setShipToAddress1(ccinfo.getAddress2().getAddress1());
                    request.setShipToAddress2(ccinfo.getAddress2().getAddress2());
                }
            }
            else
                throw new CyberSourceException(CyberSourceConnection.msg.format(
                                               "CyberSourceFieldNull", "ShippingAddressLine1"));
                      
            // Shipping Address City
            if (ccinfo.getAddress2().getCity() != null)
                request.setShipToCity(ccinfo.getAddress2().getCity());
            else
                throw new CyberSourceException(CyberSourceConnection.msg.format(
                                               "CyberSourceFieldNull", "ShippingAddressCity"));
                       
            // Shipping Address Country
            if (ccinfo.getAddress2().getCountry() == null)
                ccinfo.getAddress2().setCountry("US");
            request.setShipToCountry(ccinfo.getAddress2().getCountry());
            
            // Shipping Address State
            if (ccinfo.getAddress2().getState() != null)
                request.setShipToState(ccinfo.getAddress2().getState());
            else
                if (ccinfo.getAddress2().getCountry().toUpperCase().equals("US") ||
                    ccinfo.getAddress2().getCountry().toUpperCase().equals("CA"))
                    throw new CyberSourceException(CyberSourceConnection.msg.format(
                                                   "CyberSourceFieldNull", "ShippingAddressState"));
                                     
            // Shipping Address Zip Code
            if (ccinfo.getAddress2().getPostalCode() != null)
                if (getCheckZip())
                    request.setShipToZip(trimZip(ccinfo.getAddress2().getPostalCode()));
                else
                    request.setShipToZip(ccinfo.getAddress2().getPostalCode());
            else
                if (ccinfo.getAddress2().getCountry().toUpperCase().equals("US") ||
                    ccinfo.getAddress2().getCountry().toUpperCase().equals("CA"))
                    throw new CyberSourceException(CyberSourceConnection.msg.format(
                                                   "CyberSourceFieldNull", "ShippingAddressZip"));
                                     
                     
        }
        
        if (isLoggingDebug()) {
            /* String server = client.url.toString(); */
            logDebug("----- request -----");
            System.out.println(request.toString());
        }
        
        /* blocking call to CyberSource server */
        reply = client.send(request);
        
        if (isLoggingDebug()) {
            logDebug("----- response -----");
            System.out.println(reply.toString());
        }
        
        return new CyberSourceStatus(reply);
        
    }
    
    /**
     * Initialize and start the service
     */
    public void doStartService() throws ServiceException {
        if (mCsCon == null)
            throw new ServiceException(CyberSourceConnection.msg.getString(
                                       "CyberSourceServiceNull"));
    }

    /** 
     *  Checks if the zip code is less than 5 digits
     *
     * @param pZip A zip code string to be trimed and checked
     * @return A trimed zip code string
     */
    public String trimZip(String pZip) throws ICSException {
    
        String strZip = pZip.trim();

        if (strZip.length() < 5)
            throw new CyberSourceException(CyberSourceConnection.msg.format(
                                           "CyberSourceFieldInvalid", "ShippingAddressZip"));
        return (strZip);

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

}
