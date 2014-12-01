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

import atg.payment.PaymentStatus;
import atg.payment.creditcard.*;
import atg.core.util.ContactInfo;
import atg.core.util.Address;

import java.io.*;
import java.net.*;
import atg.nucleus.*;

import com.cybersource.ics.client.*;
import com.cybersource.ics.client.message.*;
import com.cybersource.ics.base.exception.*;
import com.cybersource.ics.base.message.*;

/**
 *
 * <p>This class manages the communications to a CyberSource payment server.
 *
 * <p>Currently only credit card transactions are supported, no CyberSource
 * coins or checks.
 *
 * <p>All information on the CyberSource API was aquired from
 * 19991112_ICS2_DG.pdf  version 2.
 *
 * @author Michael Traskunov, taken from Lawrence Byng
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/integrations/cybersource/CyberSourceCreditCard.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class CyberSourceCreditCard extends GenericService 
  implements CreditCardProcessor, DecreaseCreditCardAuthorizationProcessor {
    
  //-------------------------------------
  // Class version string
    
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/integrations/cybersource/CyberSourceCreditCard.java#2 $$Change: 651448 $";
    
  public static final String CMD_AUTHORIZE = "ics_auth";
  public static final String CMD_RETURN = "ics_credit";
  public static final String CMD_CAPTURE = "ics_bill";
    
  /** The name of the Cybersource field representing the credit card verification number. */
  public static final String CV_NUMBER_FIELD_NAME = "customer_cc_cv_number";

  /** The name of the Cybersource field representing the credit card verification number. */
  public static final String CV_NUMBER_INDICATOR_FIELD_NAME = "customer_cc_cv_indicator";

  /** Determines if the credit card verification number is used for authorizations. */
  public CyberSourceCreditCard() {}

  public CyberSourceConnection mCsCon = null;
    
  public void setCsCon(CyberSourceConnection pCsCon) {
    mCsCon = pCsCon;
  }

  public CyberSourceConnection getCsCon() {
    return mCsCon;
  }

  private boolean mSecurityCodeEnabled;

  /**
   * @return true if the the credit card security code should be added to an authorization request,
   * false - otherwise.
   */
  public boolean isSecurityCodeEnabled() {
    return mSecurityCodeEnabled;
  }

  /**
   * @param pSecurityCodeEnabled -true if credit card security code should be added to an authorization
   *        request, false - otherwise. 
   */
  public void setSecurityCodeEnabled(boolean pSecurityCodeEnabled) {
    mSecurityCodeEnabled = pSecurityCodeEnabled;
  }

  // ------------------------------------------------------
  /* Implementation of the CreditCardProcessor Interface */
  // ------------------------------------------------------
    
  /* Credit the amount after billing */
  public CreditCardStatus credit(CreditCardInfo ccinfo, CreditCardStatus pStatus) {
    try {
      String requestToken = null;
      if (pStatus instanceof CyberSourceStatus)
        requestToken = ((CyberSourceStatus) pStatus).getRequestToken();

      return performDebitORCredit(ccinfo, pStatus.getTransactionId(),
                                  requestToken, CMD_RETURN);
    }
    catch(ICSException exc) {
      String msg = exc.getMessage();
      if (msg == null)
        msg = exc.toString();
      double totald = ccinfo.getAmount();
      return mCsCon.processError("CyberSourceCreditCardFailed", msg, totald);
    }
  }

  /* Credit the amount when no billing record exists */
  public CreditCardStatus credit(CreditCardInfo ccinfo) {
        
    try {
      return performAuthORCredit(ccinfo, CMD_RETURN);
    }
    catch(ICSException exc) {
      String msg = exc.getMessage();
      if (msg == null)
        msg = exc.toString();
      double totald = ccinfo.getAmount();
      return mCsCon.processError("CyberSourceCreditCardFailed", msg, totald);
    }
  }

  /* Bill the amount after it has authorized */
  public CreditCardStatus debit(CreditCardInfo ccinfo, CreditCardStatus pStatus) {
    try {
      String requestToken = null;
      if (pStatus instanceof CyberSourceStatus)
        requestToken = ((CyberSourceStatus) pStatus).getRequestToken();

      return performDebitORCredit(ccinfo, pStatus.getTransactionId(),
                                  requestToken, CMD_CAPTURE);
    }
    catch(ICSException exc) {
      String msg = exc.getMessage();
      if (msg == null)
        msg = exc.toString();
      double totald = ccinfo.getAmount();
      return mCsCon.processError("CyberSourceCreditCardFailed", msg, totald);
    }
  }
    
  /* Authorize the amount which is a must before billing */
  public CreditCardStatus authorize(CreditCardInfo ccinfo) {
    try {
      return performAuthORCredit(ccinfo, CMD_AUTHORIZE);
    }
    catch(ICSException exc) {
      String msg = exc.getMessage();
      if (msg == null)
        msg = exc.toString();
      double totald = ccinfo.getAmount();
      return mCsCon.processError("CyberSourceCreditCardFailed", msg, totald);
    }
  }  

  /**
   * This performs either billing following an authorize, actually charging a 
   * credit card or credit a card following an already issued billing operation
   * <p> 
   * The below paramaters come from CreditCardInfo
   * @param pPaymentId The orderid of the order this is a return or debit for.(REQUIRED)
   * @param pMerchantId The id that identifies the merchant (comes from the props file)
   * @param pAmount Amount of money to return or debit (REQUIRED)
   * @param pCurrency Type of currency, currently usd | cad | frf.(REQUIRED)
   * 
   * @param pTransId The transaction id returned from auth or debit, to be used
   * for debit or credit
   *
   * @return A CyberSourceResponse object containing results of the call
   * @deprecated Please use performDebitORCredit(CreditCardInfo ccinfo, String pTransId,
   *                                             String pRequestToken, String pCommand)
   *             instead.
   **/
  public CreditCardStatus performDebitORCredit(CreditCardInfo ccinfo, String pTransId, 
                                               String pCommand) throws ICSException {
    return performDebitORCredit(ccinfo, pTransId, null, pCommand);
  }

  /**
   * This performs either billing following an authorize, actually charging a 
   * credit card or credit a card following an already issued billing operation
   * <p> 
   * The below paramaters come from CreditCardInfo
   * @param pPaymentId The orderid of the order this is a return or debit for.(REQUIRED)
   * @param pMerchantId The id that identifies the merchant (comes from the props file)
   * @param pAmount Amount of money to return or debit (REQUIRED)
   * @param pCurrency Type of currency, currently usd | cad | frf.(REQUIRED)
   * 
   * @param pTransId The transaction id returned from auth or debit, to be used
   * for debit or credit
   * @param pRequestToken The request token returned from auth or debit, to be used
   * for debit or credit
   *
   * @return A CyberSourceResponse object containing results of the call
   **/
  public CreditCardStatus performDebitORCredit(CreditCardInfo ccinfo, String pTransId, 
                                               String pRequestToken, String pCommand) throws ICSException {
    ICSReply reply = null;

    if (!mCsCon.getInitialized())
      throw new CyberSourceException(CyberSourceConnection.msg.format(
                                       "CyberSourceFailedInitialize", "see above"));
        
    ICSClient client = mCsCon.getICSClient();
    ICSClientOffer offer = mCsCon.getICSClientOffer();
    ICSClientRequest request = mCsCon.getICSClientRequest();

    /*
     * fill in the required fields, check if not null, otherwise throw the
     * exception back to the payment manager
     */

    request.addApplication(pCommand);

    // TransactionId
    if (pTransId == null)
      throw new CyberSourceException(CyberSourceConnection.msg.format(
                                       "CyberSourceFieldNull", "TransactionId")); 
        
    if (pCommand.equals(CMD_RETURN)) {
      request.setBillRequestId(pTransId);
      if (pRequestToken != null)
        request.setField("bill_request_token", pRequestToken);
    }
    else { 
      request.setAuthRequestId(pTransId);
      if (pRequestToken != null)
        request.setField("auth_request_token", pRequestToken);
    }

    addPaymentId(request, ccinfo);
    addMerchantId(request, ccinfo);
    addCurrencyCode(request, ccinfo);
    addAmount(request, offer, ccinfo);
    addExtendedInfo(request, offer, ccinfo, pCommand);

    if (isLoggingDebug()) {
      logDebug("----- request -----");
      System.out.println(request.toString());
    }

    // blocking call to CyberSource server
    reply = client.send(request);

    if (isLoggingDebug()) {
      logDebug("----- response -----");
      System.out.println(reply.toString());
    }

    reply.setField("total_amount", Double.toString(ccinfo.getAmount()));
    return new CyberSourceStatus(reply);
  }

  /**
   * This either authorizes an amount on a credit card which is a required operation before
   * billing or credits an amount on a card as a new order (no record of a previous billing)
   * The following list of properties of the CreditCardInfo are required.
   *
   * <UL>
   *   <LI> paymentId Arbitrary string associated with order.(REQUIRED)
   *   <LI> amount Amount of money to authorize or credit.(REQUIRED)
   *   <LI> currencyCode Type of currency, currently usd | cad | frf.(REQUIRED)
   *   <LI> cardNumber Credit card to operate on.(REQUIRED)
   *   <LI> billingAddress Billing address.(REQUIRED)
   *   <LI> expirationMonth The month that the card expires (REQUIRED)
   *   <LI> expirationYear The year that the card expires (REQUIRED)
   * </UL>
   *
   * From the billingAddress object the following properties are required.
   * <UL>
   *   <LI> firstName firstName on CC.(REQUIRED)
   *   <LI> lastName lastName on CC (REQUIRED)
   *   <LI> city Billing city.(REQUIRED)
   *   <LI> state Billing state.(REQUIRED)
   *   <LI> zip Billing zip.(REQUIRED)
   *   <LI> phoneNumber the billing phone number (REQUIRED)
   *   <LI> email the email address (REQUIRED)
   *   <LI> country Billing country(OPTIONAL)
   * </UL>
   *
   * @return A CyberSourceResponse.
   **/
  public CreditCardStatus performAuthORCredit(CreditCardInfo ccinfo, String pCommand)
    throws ICSException {
        
    ICSReply reply = null;

    if (!mCsCon.getInitialized())
      throw new CyberSourceException(CyberSourceConnection.msg.format(
                                       "CyberSourceFailedInitialize", "see above"));

    ICSClient client = mCsCon.getICSClient();
    ICSClientOffer offer = mCsCon.getICSClientOffer();
    ICSClientRequest request = mCsCon.getICSClientRequest();

    /*
     * fill in the required fields, check if not null, otherwise throw the
     * exception back to the payment manager
     */

    request.addApplication(pCommand);

    addPaymentId(request, ccinfo);
    addMerchantId(request, ccinfo);
    addBillingAddress(request, ccinfo);
    addContactInfo(request, ccinfo);
    addCreditCardInfo(request, ccinfo);
    addCurrencyCode(request, ccinfo);
    addAmount(request, offer, ccinfo);
    addSecurityCode(request, offer, ccinfo, pCommand);
    addExtendedInfo(request, offer, ccinfo, pCommand);

    if (isLoggingDebug()) {
      logDebug("----- request -----");
      //modify credit card number for PCI purposes
      modifyCreditCardInfo4digits(request, ccinfo);
      System.out.println(request.toString());
      //re-add credit card info for call to CyberSource server
      addCreditCardInfo(request, ccinfo);
    }

    // blocking call to CyberSource server
    reply = client.send(request);

    if (isLoggingDebug()) {
      logDebug("----- response -----");
      System.out.println(reply.toString());
    }

    reply.setField("total_amount", Double.toString(ccinfo.getAmount()));
    return new CyberSourceStatus(reply);
  }

  /**
   * Verify that the Cybersource connection is non-null
   **/
  public void doStartService() throws ServiceException {
    if (mCsCon == null)
      throw new ServiceException(CyberSourceConnection.msg.getString(
                                 "CyberSourceServiceNull"));
  }

  /**
   * @return a String representation of this object
   */
  public String toString () {
    StringBuffer buf = new StringBuffer ();
    buf.append (getClass ().getName ());
    buf.append ('[');
    buf.append (']');
    return buf.toString ();
  }

  /**
   * Copy the paymentId property from the CreditCardInfo into the client request.
   **/
  protected void addPaymentId(ICSClientRequest pRequest, CreditCardInfo pInfo)
    throws CyberSourceException {

    if (pInfo.getPaymentId() != null) 
      pRequest.setMerchantRefNo(pInfo.getPaymentId());
    else 
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "PaymentId")); 
  }

  /**
   * Copy the merchantId from the CyberSource connection into the client request.
   * The credit card info is not used by the default implementation of this method,
   * but it is provided as an argument in case subclasses wish to make use of it
   * somehow.
   **/
  
  protected void addMerchantId(ICSClientRequest pRequest, CreditCardInfo pInfo)
    throws CyberSourceException {

    if (mCsCon.getMerchantId() != null) 
      pRequest.setMerchantID(mCsCon.getMerchantId());
    else 
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "MerchantId")); 
  }
  
  /**
   * Copy the currencyCode from the credit card info into the CyberSource client request.
   **/
  protected void addCurrencyCode(ICSClientRequest pRequest, CreditCardInfo pInfo)
    throws CyberSourceException {

    if (pInfo.getCurrencyCode() != null)
      pRequest.setCurrency(pInfo.getCurrencyCode());
    else
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "CurrencyCode")); 
  }

  /**
   * Copy the billing address information from the credit card info into the CyberSource
   * client request.  This method assumes that if the billing country is not specified a
   * default value of "US" should be used.  It also checks to see whether the billing
   * address in the credit card info requires a state and postal code to be specified.
   * The logic for determining this can be changed by subclassing this class and
   * overriding the <code>addressRequiresState</code> and <code>addressRequiresPostalCode</code> 
   * methods in the subclass.
   **/
  
  protected void addBillingAddress(ICSClientRequest pRequest, CreditCardInfo pInfo)
    throws CyberSourceException {

    // Billing Address
    if (pInfo.getBillingAddress() == null)
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "BillingAddress")); 

    Address addr = pInfo.getBillingAddress();
    
    String strBillToAddress  = addr.getAddress1();
    String strBillToAddress2 = addr.getAddress2();
    String strBillToCountry  = addr.getCountry();
    String strBillToCity     = addr.getCity();
    String strBillToState    = addr.getState();
    String strBillToZip      = addr.getPostalCode();
        
    // Billing Address Line1
    if (strBillToAddress != null)
      pRequest.setBillAddress1(strBillToAddress);
    else
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "BillingAddressLine1")); 
        
    // Billing Address Line2 optional
    if (strBillToAddress2 != null)
      pRequest.setBillAddress2(strBillToAddress2);
    
    // Billing Address City
    if (strBillToCity != null)
      pRequest.setBillCity(strBillToCity);
    else
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "BillingAddressCity")); 
                
    // Billing Address Country
    if (strBillToCountry == null)
      strBillToCountry = "US";
    pRequest.setBillCountry(strBillToCountry);
    
    // Billing Address State - required for US and CA
    if (strBillToState != null)
      pRequest.setBillState(strBillToState);
    else if (addressRequiresState(pInfo))
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "BillingAddressState")); 
                
    // Billing Address Postal Code - required for US and CA
    if (strBillToZip != null)
      pRequest.setBillZip(strBillToZip);
    else if (addressRequiresPostalCode(pInfo))
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "BillingAddressZip")); 
  }


  /**
   * Copy the user's first and last name, phone number, and email address from the credit
   * card info's billing address to the CyberSource client request.  The phone number and
   * email address are copied only if the billing address is an instance of ContactInfo
   * rather than a simple Address.
   **/
  
  protected void addContactInfo(ICSClientRequest pRequest, CreditCardInfo pInfo)
    throws CyberSourceException {

    String firstName = pInfo.getBillingAddress().getFirstName();
    String lastName = pInfo.getBillingAddress().getLastName();
        
    // FirstName
    if (firstName != null)
      pRequest.setCustomerFirstName(firstName);
    else
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "FirstName")); 
                
    // LastName
    if (lastName != null)
      pRequest.setCustomerLastName(lastName);
    else
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "LastName")); 
                
    // PhoneNumber and EmailAddress
    if (pInfo.getBillingAddress() instanceof ContactInfo)
    {
      ContactInfo ci = (ContactInfo)pInfo.getBillingAddress();
      String phone = ci.getPhoneNumber();
      if (phone != null)
        pRequest.setCustomerPhone(phone);
      else
        throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "PhoneNumber"));
      
      String email = ci.getEmail(); 
      if (email != null)
        pRequest.setCustomerEmailAddress(email);
      else
        throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "EmailAddress")); 
    }
  }


  /**
   * Copy the credit card number and expiration date from the credit card info
   * info the CyberSource client request.
   **/
  protected void addCreditCardInfo(ICSClientRequest pRequest, CreditCardInfo pInfo)
    throws CyberSourceException {

    // CreditCardNumber
    if (pInfo.getCreditCardNumber() != null)
      pRequest.setCustomerCreditCardNumber(pInfo.getCreditCardNumber());
    else
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "CreditCardNumber")); 
        
    // CreditCard Expiration Month
    if (pInfo.getExpirationMonth() != null)
      pRequest.setCustomerCreditCardExpirationMonth(Integer.parseInt(pInfo.getExpirationMonth()));
    else
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "CreditCardMonth")); 
        
    // CreditCard Expiration Year
    if (pInfo.getExpirationYear() != null)
      pRequest.setCustomerCreditCardExpirationYear(Integer.parseInt(pInfo.getExpirationYear()));
    else
      throw new CyberSourceException(CyberSourceConnection.msg.format("CyberSourceFieldNull", "CreditCardYear")); 
  }

  /**
   * Modify the credit card number for Payment Card Industry (PCI) purposes to only display
   * the last 4 digits.
   **/
  protected void modifyCreditCardInfo4digits(ICSClientRequest pRequest, CreditCardInfo pInfo)
    throws CyberSourceException {

    String creditCardNumber = null;
    String lastFourDigits = null;

    if (pInfo.getCreditCardNumber() != null) {
      creditCardNumber = pInfo.getCreditCardNumber();
      if (creditCardNumber != null && creditCardNumber.length() > 4) {
        lastFourDigits = creditCardNumber.substring(creditCardNumber.length() - 4);        
      }
    }
    pRequest.setCustomerCreditCardNumber("**** **** **** " + lastFourDigits);
  }

  /**
   * Copy the amount to authorize, debit, or credit from the credit card info into
   * the CyberSource client offer, then add the offer to the request.
   **/  
  protected void addAmount(ICSClientRequest pRequest, ICSClientOffer pOffer, CreditCardInfo pInfo)
    throws CyberSourceException {

    double totald = pInfo.getAmount();
    if (totald > Double.MAX_VALUE)
      throw new CyberSourceException(CyberSourceConnection.msg.getString("CyberSourceTotalTooLarge")); 
        
    pOffer.setAmount(totald);
    pRequest.addOffer(pOffer);
  }

  /**
   * Copy the security code (CVV number) from the credit card info into
   * the CyberSource client offer, then add the offer to the request.
   **/  
  protected void addSecurityCode(ICSClientRequest pRequest, ICSClientOffer pOffer, CreditCardInfo pInfo, String pCommand)
    throws CyberSourceException {

    if (isSecurityCodeEnabled() && (pCommand != null) && pCommand.equals(CyberSourceCreditCard.CMD_AUTHORIZE)) {

      // The card verification number is specific to the StoreCreditCardInfo
      if (!(pInfo instanceof CreditCardInfoWithSecurityCode)) {
        if (isLoggingWarning()) {
          logWarning("Expected a card that implemented CreditCardInfoWithSecurityCode; actual was " +
                     pInfo.getClass().getName() + ".");
        }
        return;
      }

      CreditCardInfoWithSecurityCode creditCard = (CreditCardInfoWithSecurityCode) pInfo;

      if (creditCard.getSecurityCode() == null) {
        logDebug("The credit card has no security code.");
      }
      else {
        if (isLoggingDebug()) {
          logDebug("Adding security code [" + creditCard.getSecurityCode() +
                   "] to Cybersource request");
        }

        // Set the card verification number field
        pRequest.setField(CV_NUMBER_FIELD_NAME, creditCard.getSecurityCode());

        // Set the request flag that indicates a CV number is included.  Possible values are: 
        //  - 0: The CV number is not in the request
        //  - 1: The CV number is in the request (we'll use this here)
        //  - 2: The CV number on the card is illegible
        //  - 3: The CV number was not imprinted on the card
        pRequest.setField(CV_NUMBER_INDICATOR_FIELD_NAME, "1");
      }
    }
  }

  /**
   * Copy any additional information required from the credit card info into the
   * CyberSource request or the CyberSource client offer.  By default this method
   * does nothing.  Subclasses that need to propagate additional information from
   * ATG Commerce to CyberSource can override this method to augment the request
   * or offer objects before the request is sent to CyberSource.
   * <p>
   * <code>pCommand><code> will be set to <code>CMD_AUTHORIZE</code>,
   * <code>CMD_CAPTURE</code>, or <code>CMD_RETURN</code> depending on the
   * operation being performed.  Subclasses can test this value if they have to
   * add different extended information to the request for different operations.
   **/
  protected void addExtendedInfo(ICSClientRequest pRequest, ICSClientOffer pOffer,
                                 CreditCardInfo pInfo, String pCommand)
    throws CyberSourceException {}

  /**
   * Return true if the billing address in pInfo requires a non-null value for
   * <code>state</code> property.  By default this returns true if the billing
   * address's country is US or CA, and false otherwise.
   **/  
  protected boolean addressRequiresState(CreditCardInfo pInfo) {
    String country = pInfo.getBillingAddress().getCountry();
    return (country.equalsIgnoreCase("US") || country.equalsIgnoreCase("CA"));
  }

  /**
   * Return true if the billing address in pInfo requires a non-null value for
   * <code>postalCode</code> property.  By default this returns true if the billing
   * address's country is US or CA, and false otherwise.
   **/  
  protected boolean addressRequiresPostalCode(CreditCardInfo pInfo) {
    String country = pInfo.getBillingAddress().getCountry();
    return (country.equalsIgnoreCase("US") || country.equalsIgnoreCase("CA"));
  }

  /**
   * Decreases the authorized amount for the credit card.
   * <p>
   * This implementation does nothing but return a successful CreditCardStatus object.
   * <p>
   * Extend this method to do any cyber source credit card specific processing.  
   * @param pCreditCardInfo the CreditCardInfo reference which contains all the credit data
   * @return a CreditCardStatus object detailing the results of the decrease
   */
  public CreditCardStatus decreaseAuthorization(CreditCardInfo pCreditCardInfo, PaymentStatus pAuthStatus) {

    String creditCardNumber = null;
    String lastFourDigits = null;

    // if logging, ensure only last 4 credit card digits are logged. 
    // to comply with Payment Card Industry (PCI) regulations
    if (isLoggingDebug()) {
      if (pCreditCardInfo != null) {
        creditCardNumber = pCreditCardInfo.getCreditCardNumber();
        if(creditCardNumber != null && creditCardNumber.length() > 4) {
          lastFourDigits = creditCardNumber.substring(creditCardNumber.length() - 4);          
        }        
      }
      logDebug("Decreasing authorization credit card " + lastFourDigits + " for " + pCreditCardInfo.getAmount());
    }
    return new CreditCardStatusImpl(Long.toString(System.currentTimeMillis()), (pCreditCardInfo.getAmount() * -1),
                true, "", new java.util.Date(), new java.util.Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)));
  }  
}

