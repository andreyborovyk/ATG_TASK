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
package atg.projects.store.order.purchase;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.order.CreditCard;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupRelationship;
import atg.commerce.order.purchase.CommitOrderFormHandler;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.profile.StoreProfile;
import atg.projects.store.profile.StoreProfileTools;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * Extends the default CommitOrderFormHandler as to implement custom preCommitOrder
 * and postCommitOrder functionality.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StoreCommitOrderHandler.java#3 $
 */
public class StoreCommitOrderHandler extends CommitOrderFormHandler {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StoreCommitOrderHandler.java#3 $$Change: 635816 $";

  /**
   * Verification number invalid message key.
   */
  protected static final String VERIFICATION_NUMBER_INVALID = "invalidCreditCardVerificationNumber";

  /**
   * Confirm Email Address invalid message key.
   */
  protected static final String CONFIRM_EMAIL_INVALID = "invalidConfirmEmailAddress";
  
  /**
   * Confirm Email Address already exists message key.
   */
  protected static final String CONFIRM_EMAIL_ALREADY_EXISTS = "confirmEmailAddressAlreadyExists";
  
  
  /**
   * Commit Order Helper.
   */
  protected StoreCommitOrderProcessHelper mCommitOrderHelper;
  
  /**
   * Confirm e-mail address.
   */
  protected String mConfirmEmailAddress;

  /**
   * Credit card verification number.
   */
  protected String mCreditCardVerificationNumber;
  
  private CheckoutProgressStates mCheckoutProgressStates;
  
  public CheckoutProgressStates getCheckoutProgressStates()
  {
    return mCheckoutProgressStates;
  }

  public void setCheckoutProgressStates(CheckoutProgressStates pCheckoutProgressStates)
  {
    mCheckoutProgressStates = pCheckoutProgressStates;
  }

  /**
   * @return the Order Helper component.
   */
  public StoreCommitOrderProcessHelper getCommitOrderHelper() {
    return mCommitOrderHelper;
  }

  /**
   * @param pCommitOrderHelper the order helper component to set.
   */
  public void setCommitOrderHelper(StoreCommitOrderProcessHelper pCommitOrderHelper) {
    mCommitOrderHelper = pCommitOrderHelper;
  }
  
  /**
   * @return the confirm e-mail address.
   */
  public String getConfirmEmailAddress() {
    return mConfirmEmailAddress;
  }

  /**
   * @param pConfirmEmailAddress - the confirm e-mail address to set.
   */
  public void setConfirmEmailAddress(String pConfirmEmailAddress) {
    mConfirmEmailAddress = pConfirmEmailAddress;
  }

  /**
   * @return the credit card verification number.
   */
  public String getCreditCardVerificationNumber() {
    return mCreditCardVerificationNumber;
  }

  /**
   * @param pCreditCardVerificationNumber - the credit card verification number to set.
   */
  public void setCreditCardVerificationNumber(String pCreditCardVerificationNumber) {
    mCreditCardVerificationNumber = pCreditCardVerificationNumber;
  }
  
  private String mCouponCode;

  /**
   * @return a coupon code to be claimed
   */
  public String getCouponCode()
  {
    return mCouponCode;
  }
  
  public void setCouponCode(String pCouponCode)
  {
    mCouponCode = pCouponCode;
  }

  /**
   * Claim the specified coupon, register a form exception if the coupon is invalid or an error occurs.
   *
   * @param pRequest - current HTTP servlet request
   * @param pResponse - current HTTP servlet response
   *
   * @return true if coupon has been claimed; false otherwise
   *
   * @throws ServletException if an error occurred during claiming the coupon
   * @throws IOException if an error occurred during claiming the coupon
   */
  private void tenderCoupon(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    try {
      boolean couponTendered = ((StorePurchaseProcessHelper) getPurchaseProcessHelper()).tenderCoupon(getCouponCode(),
                                                                                                      (StoreOrderImpl) getOrder(),
                                                                                                      getProfile(),
                                                                                                      getUserPricingModels(),
                                                                                                      getUserLocale());
      if (!couponTendered) {
        String errorMessage = formatUserMessage(StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON,
                                                pRequest, pResponse);

        addFormException(new DropletFormException(errorMessage,
                                                  "",
                                                  StorePurchaseProcessHelper.MSG_UNCLAIMABLE_COUPON));
      }
    }
    catch (Exception exception) {
      processException(exception,
                       StoreCartProcessHelper.MSG_UNCLAIMABLE_COUPON,
                       pRequest, pResponse);
    }
  }

  /**
   * Ensures that an email address is set in the billing address, as required
   * by Cybersource.  Also assures that the profile ID associated with the order
   * is correct.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   * @see atg.commerce.order.purchase.CommitOrderFormHandler#handleCommitOrder
   */
  public void preCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    tenderCoupon(pRequest, pResponse);

    if (getFormError()) {
      return;
    }
    
    RepositoryItem profile = getProfile();

    //set the email provided by the anonymous shopper
    if (profile.isTransient()) {
      String confirmEmailAddress = getConfirmEmailAddress();
      boolean isValidConfirmEmailAddress = validateEmailAddress();

      if(!isValidConfirmEmailAddress) {
        String msg = formatUserMessage(CONFIRM_EMAIL_INVALID, pRequest, pResponse);
    	  addFormException(new DropletFormException(msg, (String) null, CONFIRM_EMAIL_INVALID));
    	  return;
      }
      
      if (!StringUtils.isEmpty(confirmEmailAddress)){
        if(emailAlreadyRegistered(confirmEmailAddress, pRequest, pResponse)) {
          String msg = formatUserMessage(CONFIRM_EMAIL_ALREADY_EXISTS, pRequest, pResponse);
          addFormException(new DropletFormException(msg, (String) null, CONFIRM_EMAIL_ALREADY_EXISTS));
          return;
        }
      }
      
      
      getCommitOrderHelper().addEmailToAnonymousUser(getConfirmEmailAddress(), profile);
    }

    //check if order uses credit card for payment
    if (isCreditCardRequired()){
      // credit card is used for payment in the order, verify  credit card verification number
      if(!getCommitOrderHelper().validateCreditCardVerificationNumber(getOrder(), getCreditCardVerificationNumber())) {
        String msg = formatUserMessage(VERIFICATION_NUMBER_INVALID, pRequest, pResponse);
        addFormException(new DropletFormException(msg, (String) null, VERIFICATION_NUMBER_INVALID));
        return;
      }
    }
    
    getCommitOrderHelper().addEmailToCreditCard(getOrder(), profile);
    
    getCommitOrderHelper().updateProfileIdOnOrder(getOrder(), profile);

    super.preCommitOrder(pRequest, pResponse);
  }
  
  /**
   * Checks whether an order uses a credit card for a payment. In case when there are
   * payment group relationships with a credit card - returns true,
   * otherwise when other payment methods are used to pay for the order - returns false.
   * @return true if there are payment group relationships with credit card
   */
  public boolean isCreditCardRequired(){
    /*
     * If there is nothing to pay (order total is 0), there is no need in credit card.
     * Return false.
     */
    if (getOrder().getPriceInfo().getTotal() == 0) {
      return false;
    }
    List rels = getOrder().getPaymentGroupRelationships();
    // return true if there are no still payment group relationships
    // this can happen if express checkout is used and credit card payment
    // group relationship will be created later
    if (rels == null || rels.size()== 0) return true;
    for (Iterator iter = rels.iterator(); iter.hasNext();){
      PaymentGroupRelationship rel = (PaymentGroupRelationship) iter.next();
      PaymentGroup paymentGroup = rel.getPaymentGroup();
      if (paymentGroup != null && paymentGroup instanceof CreditCard){
        return true;
      }
    }
    return false;
  }

  /**
   * Called after all processing is done by the handleCommitOrder method. This
   * method is responsible for populating the profile with the attributes on
   * the profile, such as itemsBought, lastPurchaseDate, numberOfOrders, etc.
   *
   * @param pRequest
   *            the request object
   * @param pResponse
   *            the response object
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   * @see atg.commerce.order.purchase.CommitOrderFormHandler#handleCommitOrder
   */
  public void postCommitOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    if (getFormError()) {
      // An error occurred, do not do any of this.
      return;
    }
    
    StoreOrderImpl commitedOrder = (StoreOrderImpl) getShoppingCart().getLast();
    try {
      getCommitOrderHelper().doPostCommitOrderProcessing(commitedOrder, getProfile());
      
      // Remove personal data from transient profile (for security reasons)
      StoreProfile profile = (StoreProfile) getProfile();
      if (profile.isTransient()) {
        profile.clearPersonalData();
      }
    }
    catch(RepositoryException re) {
      throw new ServletException(re);
    }
    super.postCommitOrder(pRequest, pResponse);
    
    if (mCheckoutProgressStates != null && !getFormError())
    {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CART.toString());
    }
  }

  /**
   * Method to allow page access to CreditCard object.
   *
   * @return credit card with this order
   */
  public CreditCard getCreditCard() {
    return getCommitOrderHelper().getStoreOrderTools().getCreditCard(getOrder());
  }
  
  /**
   * Validates an email address for correctness. An email is valid if it is null/blank or it passes the
   * call to validateEmailAddress in StoreProfileTools
   *
   * @return boolean true if email address is valid
   * @see atg.projects.store.profile.StoreProfileTools#validateEmailAddress
   */
  private boolean validateEmailAddress() {
	  boolean validEmail = false;
	  String email = getConfirmEmailAddress();
	  
	  StoreProfileTools profileTools = (StoreProfileTools)getCommitOrderHelper().getStoreOrderTools().getProfileTools();
	  
	  if(StringUtils.isBlank(email)) {
		  validEmail = true;
	  }
	  else {
	    validEmail = profileTools.validateEmailAddress(email);  
	  }      
      
      return validEmail;
  }
  
  /**
   * Returns true if a user already exists with the given email
   * @param pEmail the email which a user wants to use
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   */
  protected boolean emailAlreadyRegistered(String pEmail,
                                           DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
  { 
    // Check if user with such email already exists
    StoreProfileTools profileTools = (StoreProfileTools)getCommitOrderHelper().getStoreOrderTools().getProfileTools();
    RepositoryItem user = profileTools.getItem(pEmail.toLowerCase(), null, profileTools.getDefaultProfileType());
    return user != null;
  }
}