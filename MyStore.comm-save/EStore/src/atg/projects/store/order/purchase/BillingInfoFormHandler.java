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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.purchase.PurchaseProcessFormHandler;
import atg.commerce.order.purchase.PurchaseUserMessage;
import atg.commerce.util.PlaceUtils;
import atg.commerce.util.PlaceList.Place;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.projects.store.StoreConfiguration;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import java.util.TreeMap;


/**
 * Form Handler for taking billing information.
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class BillingInfoFormHandler extends PurchaseProcessFormHandler {

  private static final String MSG_INVALID_DATE_FORMAT = "invalidDateFormat";

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/BillingInfoFormHandler.java#3 $$Change: 635816 $";

  static final String DATE_FORMAT = "yyyy-MM-dd";

  private static final String YES = "yes";

  private static final String NO = "no";

  public static final String COUNTRY_KEY_PREFIX = "CountryCode.";

  public static final String COUNTRY_STATE_RESOURCES = "atg.commerce.util.CountryStateResources";

  /**
   * Error while confirmation message key.
   */
  protected static final String MSG_ERROR_MOVE_TO_CONFIRM = "errorWithBillingInfo";


  /**
   * Error updating order message key.
   */
  protected static final String MSG_ERROR_UPDATE_ORDER = "errorUpdatingOrder";

 /**
  * Error message for incorrect state
  */
  protected static final String MSG_ERROR_INCORRECT_STATE = "stateIsIncorrect";


  private boolean mCreditCardSelectionInitialized = false;
  private boolean mBillingAddressSelectionInitialized = false;

  private CheckoutProgressStates mCheckoutProgressStates;

  public CheckoutProgressStates getCheckoutProgressStates()
  {
    return mCheckoutProgressStates;
  }

  public void setCheckoutProgressStates(CheckoutProgressStates pCheckoutProgressStates)
  {
    mCheckoutProgressStates = pCheckoutProgressStates;
  }

  StorePropertyManager mProfilePropertyManager;

  public StorePropertyManager getProfilePropertyManager() {
    return mProfilePropertyManager;
  }

  public void setProfilePropertyManager(
      StorePropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }

  /**
   * Property BillingHelper
   */
  private StoreBillingProcessHelper mBillingHelper;

  public StoreBillingProcessHelper getBillingHelper() {
    return mBillingHelper;
  }

  public void setBillingHelper(StoreBillingProcessHelper pBillingHelper) {
    mBillingHelper = pBillingHelper;
  }

  /**
   * Property for holding the credit card verification number for a stored credit card.
   */
  protected String mCreditCardVerificationNumber;

  /**
   * @return the credit card verification number.
   */
  public String getCreditCardVerificationNumber() {
    return mCreditCardVerificationNumber;
  }

  /**
   * @param pCreditCardVerificationNumber -
   * the credit card verification number to set.
   */
  public void setCreditCardVerificationNumber(String pCreditCardVerificationNumber) {
    mCreditCardVerificationNumber = pCreditCardVerificationNumber;
  }

   /**
   * Place utils helper
   */
  PlaceUtils mPlaceUtils;

  /**
   *
   * @return place utils
   */
  public PlaceUtils getPlaceUtils() {
    return mPlaceUtils;
  }

  /**
   * Sets place utils
   * @param pPlaceUtils
   */
  public void setPlaceUtils(PlaceUtils pPlaceUtils) {
    mPlaceUtils = pPlaceUtils;
  }


  /**
   * Property for holding the credit card verification number for a new credit card.
   */
  protected String mNewCreditCardVerificationNumber;

  /**
   * @return the new credit card verification number.
   */
  public String getNewCreditCardVerificationNumber() {
    return mNewCreditCardVerificationNumber;
  }

  /**
   * @param pNewCreditCardVerificationNumber -
   * the credit card verification number to set.
   */
  public void setNewCreditCardVerificationNumber(String pNewCreditCardVerificationNumber) {
    mNewCreditCardVerificationNumber = pNewCreditCardVerificationNumber;
  }

  /**
   * Should save credit card data.
   */
  protected boolean mSaveCreditCard;

  /**
   * @return true if user selected to save the credit card,
   * false - otherwise.
   */
  public boolean getSaveCreditCard() {
    return mSaveCreditCard;
  }

  /**
   * @param pSaveCreditCard -
   * if user selects to save the credit card.
   */
  public void setSaveCreditCard(boolean pSaveCreditCard) {
    mSaveCreditCard = pSaveCreditCard;
  }

  /**
   * Is e-mail option turned in.
   */
  protected boolean mEmailOptIn;

  /**
   * @return ture if user selected to opt in,
   * false - otherwise.
   */
  public boolean isEmailOptIn() {
    return mEmailOptIn;
  }

  /**
   * @param pEmailOptIn - true if user selects to opt in,
   * false - otherwise.
   */
  public void setEmailOptIn(boolean pEmailOptIn) {
    mEmailOptIn = pEmailOptIn;
  }

  /**
   * E-mail option source code.
   */
  protected String mEmailOptInSourceCode;

  /**
   * @return E-mail opt in source code.
   */
  public String getEmailOptInSourceCode() {
    return mEmailOptInSourceCode;
  }

  /**
   * @param pEmailOptInSourceCode -
   * e-mail opt in source code.
   */
  public void setEmailOptInSourceCode(String pEmailOptInSourceCode) {
    mEmailOptInSourceCode = pEmailOptInSourceCode;
  }

  /**
   * Stores credit card name.
   */
  protected String mStoredCreditCardName;

  /**
   * @return stores credit card name.
   */
  public String getStoredCreditCardName() {
    initializeCreditCardSelection();
    return mStoredCreditCardName;
  }

  /**
   * @param pStoredCreditCardName - stored credit card name.
   */
  public void setStoredCreditCardName(String pStoredCreditCardName) {
    mStoredCreditCardName = pStoredCreditCardName;
  }

  /**
   * Is registration required.
   */
  protected boolean mRequiresRegistration;

  /**
   * @return true if registration is required,
   * false - otherwise.
   */
  public boolean getRequiresRegistration() {
    return mRequiresRegistration;
  }

  /**
   * @param pRequiresRegistration - true if registration is
   * required, false - otherwise.
   */
  public void setRequiresRegistration(boolean pRequiresRegistration) {
    mRequiresRegistration = pRequiresRegistration;
  }

  /**
   * Stored address selection.
   */
  protected String mStoredAddressSelection;

  /**
   * @return stored address selection.
   */
  public String getStoredAddressSelection() {
    initializeBillingAddressSelection();
    return mStoredAddressSelection;
  }

  /**
   * @param pStoredAddressSelection - stored address selection.
   */
  public void setStoredAddressSelection(String pStoredAddressSelection) {
    mStoredAddressSelection = pStoredAddressSelection;
  }

  /**
   * New user password.
   */
  protected String mNewUserPassword;

  /**
   * @return new user password.
   */
  public String getNewUserPassword() {
    return mNewUserPassword;
  }

  /**
   * @param pNewUserPassword - new user password.
   */
  public void setNewUserPassword(String pNewUserPassword) {
    mNewUserPassword = pNewUserPassword;
  }

  /**
   * Retypes new password.
   */
  protected String mRetypeNewPassword;

  /**
   * @return retyped new password.
   */
  public String getRetypeNewPassword() {
    return mRetypeNewPassword;
  }

  /**
   * @param pRetypeNewPassword - retyped new password.
   */
  public void setRetypeNewPassword(String pRetypeNewPassword) {
    mRetypeNewPassword = pRetypeNewPassword;
  }

  /**
   * Property to hold profile values
   */
  protected HashMap mValue = new HashMap();

  /**
   * Returns map of profile values
   *
   * @return map of profile values
   */
  public HashMap getValue() {
    return mValue;
  }

  /**
   * Sets map of profile values
   *
   * @param pValue map of values
   */
  public void setValue(HashMap pValue) {
    mValue = pValue;
  }

  //-------------------------------------
  // property: dateOfBirth
  /**
   * Date of birth.
   */
  private String mDateOfBirth = null;


  /**
   * @return date of birth.
   */
  public String getDateOfBirth() {
    if (mDateOfBirth == null) {
      StorePropertyManager propertyManager =
        (StorePropertyManager) getBillingHelper().getOrderManager().getOrderTools().getProfileTools().getPropertyManager();

      Object dateOfBirth = getProfile().getPropertyValue(
          propertyManager.getDateOfBirthPropertyName());

      if (dateOfBirth != null) {
        mDateOfBirth = getDateByFormat(dateOfBirth, getDateFormat());
      }
    }

    return mDateOfBirth;
  }

  /**
   * @param pDateBirth - date of birth.
   */
  public void setDateOfBirth(String pDateOfBirth) {
    mDateOfBirth = pDateOfBirth;
  }

  //-------------------------------------
  // property: dateFormat
  /**
   * Current date format.
   */
  private String mDateFormat = null;

  /**
  * @return date format.
  */
  public String getDateFormat() {
    return mDateFormat;
  }

  /**
   * @param pDateFormat - date format.
   */
  public void setDateFormat(String pDateFormat) {
    mDateFormat = pDateFormat;
  }


  /**
   * Move to confirmation chain id.
   */
  protected String mMoveToConfirmationChainId;

  /**
   * @return move to confirmation chain id.
   */
  public String getMoveToConfirmationChainId() {
    return mMoveToConfirmationChainId;
  }

  /**
   * @param pMoveToConfirmationChainId - move
   * to confirmation chain id.
   */
  public void setMoveToConfirmationChainId(String pMoveToConfirmationChainId) {
    mMoveToConfirmationChainId = pMoveToConfirmationChainId;
  }

  /**
   * Billing Address nickname
   */
  protected String mBillingAddressNickname;

  /**
   * @return the billing address nickname.
   */
  public String getBillingAddressNickname() {
    return mBillingAddressNickname;
  }

  /**
   * @param pBillingAddressNickname -
   * the billing address nickname to set.
   */
  public void setBillingAddressNickname(String pBillingAddressNickname) {
    mBillingAddressNickname = pBillingAddressNickname;
  }

  /**
   * E-mail.
   */
  protected String mEmail;

  /**
   * @return Email property.
   */
  public String getEmail() {
    return mEmail;
  }

  /**
   * @param pEmail -
   * property for the email property.
   */
  public void setEmail(String pEmail) {
    if (isLoggingDebug()) {
      logDebug("Setting email.");
    }
    mEmail = pEmail;
  }

  /**
   * Should save shipping information.
   */
  protected boolean mSaveShippingInfo = false;

  /**
   * @return the save shippingInfo.
   */
  public boolean isSaveShippingInfo() {
    return mSaveShippingInfo;
  }

  /**
   * @param pSaveShippingInfo - the save shipping
   * information to set.
   */
  public void setSaveShippingInfo(boolean pSaveShippingInfo) {
    mSaveShippingInfo = pSaveShippingInfo;
  }

  /**
   * Minimum nickname length.
   */
  protected int mMinNickNameLength;

  /**
   * @return minimum nickname length.
   */
  public int getMinNickNameLength() {
    return mMinNickNameLength;
  }

  /**
   * @param pMinNickNameLength - minimum nickname length.
   */
  public void setMinNickNameLength(int pMinNickNameLength) {
    mMinNickNameLength = pMinNickNameLength;
  }

  /**
   * Maximum nickname length.
   */
  protected int mMaxNickNameLength;

  /**
  * @return maximum nickname length.
  */
  public int getMaxNickNameLength() {
    return mMaxNickNameLength;
  }

  /**
   * @param pMaxNickNameLength - maximum nickname length.
   */
  public void setMaxNickNameLength(int pMaxNickNameLength) {
    mMaxNickNameLength = pMaxNickNameLength;
  }

  /**
   * Credit card nickname.
   */
  protected String mCreditCardNickname;

  /**
   * @return the credit card nickname.
   */
  public String getCreditCardNickname() {
    return mCreditCardNickname;
  }

  /**
   * @param pCreditCardNickname -
   * the credit card nickname to set.
   */
  public void setCreditCardNickname(String pCreditCardNickname) {
    mCreditCardNickname = pCreditCardNickname;
  }

  /**
   * Move to confirm error redirect URL.
   */
  protected String mMoveToConfirmErrorURL;

  /**
   * @return move to confirm error redirect URL.
   */
  public String getMoveToConfirmErrorURL() {
    return mMoveToConfirmErrorURL;
  }

  /**
   * @param pMoveToConfirmErrorURL - move to confirm
   * error redirect URL.
   */
  public void setMoveToConfirmErrorURL(String pMoveToConfirmErrorURL) {
    mMoveToConfirmErrorURL = pMoveToConfirmErrorURL;
  }

  /**
   * Move to confirm success redirect URL.
   */
  protected String mMoveToConfirmSuccessURL;

  /**
   * @return move to confirm error redirect URL.
   */
  public String getMoveToConfirmSuccessURL() {
    return mMoveToConfirmSuccessURL;
  }

  /**
   * @param pMoveToConfirmSuccessURL -
   * move to confirm success redirect URL.
   */
  public void setMoveToConfirmSuccessURL(String pMoveToConfirmSuccessURL) {
    mMoveToConfirmSuccessURL = pMoveToConfirmSuccessURL;
  }

  protected String mRegisterAccountSuccessURL;


  public String getRegisterAccountSuccessURL() {
    return mRegisterAccountSuccessURL;
  }

  public void setRegisterAccountSuccessURL(String pRegisterAccountSuccessURL) {
    mRegisterAccountSuccessURL = pRegisterAccountSuccessURL;
  }

  protected String mRegisterAccountErrorURL;

  public String getRegisterAccountErrorURL() {
    return mRegisterAccountErrorURL;
  }

  public void setRegisterAccountErrorURL(String pRegisterAccountErrorURL) {
    mRegisterAccountErrorURL = pRegisterAccountErrorURL;
  }


  /**
   * Store configuration.
   */
  protected StoreConfiguration mStoreConfiguration;

  /**
   * @return the store configuration.
   */
  public StoreConfiguration getStoreConfiguration() {
    return mStoreConfiguration;
  }

  /**
   * @param pStoreConfiguration - the store configuration to set.
   */
  public void setStoreConfiguration(StoreConfiguration pStoreConfiguration) {
    mStoreConfiguration = pStoreConfiguration;
  }

  /**
   * Catalog tools.
   */
  protected CatalogTools mCatalogTools;

  /**
   * @return catalog tools.
   */
  public CatalogTools getCatalogTools() {
    return mCatalogTools;
  }

  /**
   * @param pCatalogTools - catalog tools.
   */
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
   * Is the credit card saved in the user's profile.
   */
  protected boolean mUsingProfileCreditCard = true;

  /**
   * @return Is the credit card saved in the user's profile.
   */
  public boolean isUsingProfileCreditCard() {
    initializeCreditCardSelection();
    return mUsingProfileCreditCard;
  }

  /**
   * @param pUsingProfileCreditCard - is the credit card saved in the user's profile.
   */
  public void setUsingProfileCreditCard(boolean pUsingProfileCreditCard) {
    mUsingProfileCreditCard = pUsingProfileCreditCard;
  }

  /**
   * Is the address in the user's profile.
   */
  protected boolean mUsingSavedAddress = true;

  /**
   * @return Is the address saved in the user's profile.
   */
  public boolean isUsingSavedAddress() {
    initializeBillingAddressSelection();
    return mUsingSavedAddress;
  }

  /**
   * @param pUsingSavedAddress - Is the address saved in the user's profile.
   */
  public void setUsingSavedAddress(boolean pUsingSavedAddress) {
    mUsingSavedAddress = pUsingSavedAddress;
  }

  /**
   * property: flag indicating if new billing address needs to be saved or not
   */
  private boolean mSaveBillingAddress = true;

  /**
   * @return true if billing address should be saved,
   * false - otherwise.
  */
  public boolean getSaveBillingAddress() {
    return mSaveBillingAddress;
  }

  /**
   * @param pSaveBillingAddress - true if billing address
   * should be saved, false - otherwise.
   */
  public void setSaveBillingAddress(boolean pSaveBillingAddress) {
    mSaveBillingAddress = pSaveBillingAddress;
  }

  /**
   * Is order covered with store credit only.
   */
  protected boolean mUsingStoreCredit = false;

  /**
   * @return Is order covered with store credit only.
   */
  public boolean isUsingStoreCredit() {
    return mUsingStoreCredit;
  }

  /**
   * @param pUsingStoreCredit - Is order covered with store credit only.
   */
  public void setUsingStoreCredit(boolean pUsingStoreCredit) {
    mUsingStoreCredit = pUsingStoreCredit;
  }


  //-------------------------------------
  // property: creditCardTools
  private ExtendableCreditCardTools mCreditCardTools;

  /**
   *
   * @return ExtendableCreditCardTools
   */
  public ExtendableCreditCardTools getCreditCardTools() {
    return mCreditCardTools;
  }

  /**
   * @param pCreditCardTools new ExtendableCreditCardTools
   */
  public void setCreditCardTools(ExtendableCreditCardTools pCreditCardTools) {
    mCreditCardTools = pCreditCardTools;
  }


  /**
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void preSetupStoreCreditPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method Initialize and setup StoreCredit Payment group for the order, if store credit is used
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void setupStoreCreditPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    try {

      getBillingHelper().setupStoreCreditPaymentGroupsForOrder(getOrder(), getProfile(), getBillingHelper().getStoreCreditIds(getProfile()));
    } catch (StorePurchaseProcessException exc) {

      String msg = ResourceUtils.getMsgResource(exc.getMessage(), getResourceBundleName(), getResourceBundle(getUserLocale(pRequest, pResponse)), exc.getParams());
      addFormException(new DropletFormException(msg,null));

    } catch (CommerceException ce) {
      processException(ce, StoreBillingProcessHelper.STORE_CREDIT_ERROR, pRequest, pResponse);

    } catch (Exception exc) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor(""), exc);
        logError(LogUtils.formatMajor("Error while processing Store Credit : " +
           exc.getMessage()));
      }
    }
  }

  /**
   * This method is post setup of store credit payment groups
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void postSetupStoreCreditPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void preAddCreditCardBillingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method add and validates the billing Address to the credit card if order payment
   * is payed by the credit card.
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void addCreditCardBillingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    try {
      boolean paymentGroupOrderRelationShipExist = getBillingHelper().isPaymentGroupOrderRelationShipExist(getCreditCard());
      //True if Order Remaining Amount is payed by Credit Card
      //False if all Order Amount is payed by other payment groups. Like OnlineCredits
      if(paymentGroupOrderRelationShipExist) {

    	if(!isUsingProfileCreditCard()){
    	  getBillingHelper().addBillingAddressToCard(getCreditCard(), isUsingSavedAddress(), getStoredAddressSelection(), getProfile(), getOrder());
      	}
        validateBillingAddress(pRequest, pResponse);
      }
    } catch (CommerceException ce) {
      addFormException(new DropletFormException(ce.getMessage(),null));
    }
  }

  /**
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void postAddCreditCardBillingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   *
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void preAddCreditCardAuthorizationNumber(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method add and validates the credit card authorization number the credit card if order payment
   * is payed by the credit card.
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   *
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void addCreditCardAuthorizationNumber(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    String creditCardVerificationNumber = null;

    if (isUsingProfileCreditCard()) {
      creditCardVerificationNumber = getCreditCardVerificationNumber();
    } else {

      creditCardVerificationNumber = getNewCreditCardVerificationNumber();
    }

    validateCreditCardAuthorizationNumber(creditCardVerificationNumber, pRequest, pResponse);
    // Ensure supplied CVV number is an non-empty numeric string
    getBillingHelper().addCreditCardAuthorizationNumber(getOrder(), creditCardVerificationNumber);
  }

  /**
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   *
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void postAddCreditCardAuthorizationNumber(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void preSetupCreditCardPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

  }

  /**
   * This method Initialize and setup Credit Card Payment group for the order,
   * if store credit is used, then all amount from the order deducted by the store credit,
   * then by credit card.
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   *
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void setupCreditCardPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    Locale userLocale = getOrderManager().getOrderTools().getProfileTools().getUserLocale(pRequest, pResponse);
    try {

      if (getStoredCreditCardName() != null) {

        getBillingHelper().setupCreditCardPaymentGroupsForOrder(getOrder(),getProfile(), isUsingProfileCreditCard(),
                                                                       getStoredCreditCardName(),userLocale);
        getBillingHelper().getCheckoutOptionSelections().setPrefillCreditCard(!(getSaveCreditCard() || isUsingProfileCreditCard()));
        validateCreditCardInput(pRequest, pResponse);
      }
    } catch (java.security.InvalidParameterException exc) {

      // never actually thrown
      addFormException(new DropletFormException(exc.getMessage(), null));
    } catch (CommerceException ce) {

      addFormException(new DropletFormException(ce.getMessage(), null));
    }
  }

  /**
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void postSetupCreditCardPaymentGroupsForOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

  }

  /**
   * This method is for any processing required after billing info is entered.
   * It will register the user if the user is required to register, and save
   * the credit card to the profile if the user chose that option.
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void preAddCreditCardToProfile(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * This method is for any processing required after billing info is entered.
   * It will register the user if the user is required to register, and save
   * the credit card to the profile if the user chose that option.
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void addCreditCardToProfile(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    CreditCard card = getCreditCard();

    if (getSaveCreditCard() && !isUsingProfileCreditCard() ) {
        getBillingHelper().saveCreditCardToProfile(getOrder(), getProfile(), getCreditCardNickname());
      }

      if (getSaveBillingAddress()) {
       getBillingHelper().saveBillingAddressToProfile(getOrder(), getProfile(), getBillingAddressNickname());
      }
        // Make sure user has a default billing address. Use this one
        // if current billingAddress is empty.
      getBillingHelper().saveDefaultBillingAddress(getProfile(), card);
  }

  /**
   * This method is for any processing required after billing info is entered.
   * It will register the user if the user is required to register, and save
   * the credit card to the profile if the user chose that option.
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void postAddCreditCardToProfile(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   *  This method should execute when the user chose to check out anonymous and then decided to register.
   *  And it will validate and set the required feilds for registered user
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public void preRegisterUserAfterCheckout (DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

      validateEmailAddress();
      validatePassword();
      validateDateOfBirth();

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Error with registration input.");
        }
        return;
      }
      // Only set these if email is valid
      getBillingHelper().setProfileEmailAndLogin(getProfile(),(String)getValue().get(getProfilePropertyManager().getEmailAddressPropertyName()));

      try {
        // set dateOfBirth as Date object, not as string
        String dateOfBirth = getDateOfBirth();
        if(!StringUtils.isEmpty(dateOfBirth)) {
          // we pass validation, so don't worry
          ((Profile)getProfile()).setPropertyValue(getProfilePropertyManager().getDateOfBirthPropertyName(),
              (new SimpleDateFormat(getDateFormat())).parse(getDateOfBirth()));
        }

      } catch (ParseException e) {
        addFormException(new DropletFormException(e.getMessage(), null));
      }

      if(isEmailOptIn()) {
        ((Profile)getProfile()).setPropertyValue(getProfilePropertyManager().getReceiveEmailPropertyName(), YES);
      } else {
        ((Profile)getProfile()).setPropertyValue(getProfilePropertyManager().getReceiveEmailPropertyName(), NO);
      }

      // If validations passed, set the password on the profile
      // (previously form input, and not tied to Profile).
      getBillingHelper().setProfilePassword(getProfile(),(String)getValue().get(getProfilePropertyManager().getPasswordPropertyName()));
  }

  /**
   * This method should execute when the user chose to check out anonymous and then decided to register.
   * Following are the executions
   * 1) Saving shipping address to profile
   * 2) Register user
   * 3) Add the order to the repository
   *
   * @param pRequest
   * @param pResponse
   * @throws ServletException
   * @throws IOException
   */
  public void registerUserAfterCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

      StoreBillingProcessHelper billingProcessHelper = getBillingHelper();
      try {
        // save the shipping address and shipping method in the profile.
        billingProcessHelper.saveShippingInfo(getProfile(), getShippingGroupMapContainer());

        // load last (submitted) order, register user,
        // associate this user with submitted order,
        // and save order back to the repository
        Order order = billingProcessHelper.getOrderManager().loadOrder(getShoppingCart().getLast().getId());
        billingProcessHelper.registerNewUserDuringCheckout(order,(Profile)getProfile(), getValue());
        billingProcessHelper.getOrderManager().updateOrder(order);

        // Add user to email opt-in list if selected
        if (isEmailOptIn()) {
          billingProcessHelper.addUserToOptInList((Profile)getProfile(), getEmailOptInSourceCode());
        }

      } catch (CommerceException ce) {
        addFormException(new DropletFormException(ce.getMessage(), null));
        return;
      }
  }

 /* This method will execute when the user chose to check out anonymous and then decided to register.
  *
  * @param pRequest
  * @param pResponse
  * @throws ServletException
  * @throws IOException
  */
  public void postRegisterUserAfterCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  }

  /**
   * Run the pipeline.
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   *
   * @throws RunProcessException
   *             if error running pipeline process
   */
  public void runProcessMoveToConfirmation(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws RunProcessException, ServletException, IOException {

    HashMap params = new HashMap(11);

    params.put(PipelineConstants.CATALOGTOOLS, getCatalogTools());

    params.put(PipelineConstants.INVENTORYMANAGER, getOrderManager().getOrderTools().getInventoryManager());

    PipelineResult result = runProcess(getMoveToConfirmationChainId(), getOrder(), getUserPricingModels(),
                                        getUserLocale(pRequest, pResponse), getProfile(), params, null);

    processPipelineErrors(result);
  }

  /**
   * Utility method to validate email address.
   *
   * @return true if success, false -  otherwise
   */
  protected boolean validateEmailAddress() {
    try {

      getBillingHelper().validateEmailAddress(getProfile(),
          (String)getValue().get(getProfilePropertyManager().getEmailAddressPropertyName()));
    }
    catch(StorePurchaseProcessException cex) {

      String msg = ResourceUtils.getMsgResource(cex.getMessage(), getResourceBundleName(), getResourceBundle(ServletUtil.getUserLocale()));
      addFormException(new DropletFormException(msg, cex.getMessage()));
      return false;
    }

    return true;
  }

  /**
   * Utility method to validate password input.
   *
   * @return true if success, false - otherwise
   */
  protected boolean validatePassword() {
    try {

      getBillingHelper().validatePassword(getProfile(),
          (String)getValue().get(getProfilePropertyManager().getPasswordPropertyName()), (String)getValue().get("confirmPassword"));
    }
    catch(StorePurchaseProcessException cex) {

      String msg = ResourceUtils.getMsgResource(cex.getMessage(), getResourceBundleName(), getResourceBundle(ServletUtil.getUserLocale()));
      addFormException(new DropletFormException(msg, cex.getMessage()));
      return false;
    }

    return true;
  }

  /**
   * Validates date of birth
   */
  private void validateDateOfBirth() {
    String dateOfBirth = getDateOfBirth();

    if(!StringUtils.isEmpty(dateOfBirth)) {
      DateFormat df = new SimpleDateFormat(getDateFormat());
      try {
        Date d = df.parse(dateOfBirth);
      }
      catch (ParseException e) {

        String msg = ResourceUtils.getMsgResource(MSG_INVALID_DATE_FORMAT, getResourceBundleName(), getResourceBundle(ServletUtil.getUserLocale()));
        addFormException(new DropletFormException(msg, e.getMessage()));
      }
    }
  }

  /**
   * This method uses the CreditCardTools to validate the credit card.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   * @return true if success, false - otherwise
   * @see atg.payment.creditcard.ExtendableCreditCardTools
   */
  public boolean validateCreditCardInput(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws CommerceException, IOException, ServletException {
    // check to see if a credit card nickname is needed & if so if a valid one is provided

    CreditCard card = getCreditCard();
    Order order = getOrder();
    StoreOrderManager orderManager = (StoreOrderManager) getOrderManager();
    StoreOrderTools orderTools = (StoreOrderTools) orderManager.getOrderTools();

    int validationResult = -1;

    //check for empty required fields
    if (isUsingProfileCreditCard()){
      if (StringUtils.isEmpty(getStoredCreditCardName()) || StringUtils.isEmpty(getCreditCardVerificationNumber())){

        addFormException(new DropletFormException("Required form fields are missing", this.getAbsoluteName(), "missingRequiredValue"));
        return false;
      }
    }else{
      if (StringUtils.isEmpty(card.getCreditCardNumber()) || StringUtils.isEmpty(card.getCreditCardType()) ||
          StringUtils.isEmpty(card.getExpirationMonth()) || StringUtils.isEmpty(card.getExpirationYear()) ||
          StringUtils.isEmpty(getNewCreditCardVerificationNumber()) || StringUtils.isEmpty(getCreditCardNickname()) ){

        addFormException(new DropletFormException("Required form fields are missing", this.getAbsoluteName(), "missingRequiredValue"));
        return false;
      }
    }

    if (!validateCreditCardNicknameInput(pRequest, pResponse)) {

      return false;
    }

    ExtendableCreditCardTools cardTools = getCreditCardTools();

    validationResult = cardTools.verifyCreditCard(card);

    if (validationResult != cardTools.SUCCESS) {
      //check if Credit card validation fails and Paying with Online credits are insufficient to pay the order
      boolean isPayingWithOnlineCredit = getBillingHelper().isPayingWithOnlineCredit(getBillingHelper().getStoreCreditIds(getProfile()));
      List storeCredits =  orderTools.getStoreCreditPaymentGroups(order);
      if (isPayingWithOnlineCredit || (storeCredits != null && storeCredits.size() > 0)) {

    	ResourceBundle resourceBundle = getResourceBundle(getUserLocale(pRequest, pResponse));
        String insufficientCredit = ResourceUtils.getMsgResource(StoreBillingProcessHelper.ONLINE_CREDIT_INSUFFICIENT, getResourceBundleName(),
            resourceBundle);
        addFormException(new DropletFormException(insufficientCredit, "common.additionalInfoRequired"));
      }

      String msg = cardTools.getStatusCodeMessage(validationResult, getUserLocale(pRequest, pResponse));
      String errorKey = "" + validationResult;
      addFormException(new DropletFormException(msg, "", errorKey));

      return false;
    }

    return true;
  }

  /**
   * This method validates the credit card nickname if one is required.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   * @return true if success, false - otherwise
   */
  public boolean validateCreditCardNicknameInput(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    // check for a valid nickname only if the save credit card option is checked and the
    // registration is taking place or the user is already logged in
    Profile profile = (Profile) getProfile();
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
    StorePropertyManager pmgr = (StorePropertyManager) profileTools.getPropertyManager();

    String securityProperty = pmgr.getSecurityStatusPropertyName();
    int securityStatus = ((Integer) profile.getPropertyValue(securityProperty)).intValue();

    if (getRequiresRegistration() || (securityStatus >= pmgr.getSecurityStatusLogin())) {

      if (getSaveCreditCard() && !isUsingProfileCreditCard()) {
        try {
          getBillingHelper().validateCreditCardNicknameInput(getOrder(), getProfile(), getCreditCardNickname(),
                                                                  getMinNickNameLength(), getMaxNickNameLength());

        } catch(StorePurchaseProcessException cex) {

          String msg = ResourceUtils.getMsgResource(cex.getMessage(), getResourceBundleName(), getResourceBundle(ServletUtil.getUserLocale(pRequest)));
          addFormException(new DropletFormException(msg, cex.getMessage()));
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Utility method to fetch credit card and set properties from page.
   *
   * @return credit card for this order
   */
  public CreditCard getCreditCard() {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    return orderTools.getCreditCard(getOrder());
  }

  /**
   * Verifies that the auth number is valued and a number.
   * @param pAuthNumber - authentication number
   */
  protected void validateCreditCardAuthorizationNumber(String pAuthNumber, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    if (getStoreConfiguration().isRequireCreditCardVerification()) {

      CreditCard card = getCreditCard();
      boolean paymentGroupOrderRelationShipExist = getBillingHelper().isPaymentGroupOrderRelationShipExist(card);
      //True if Order Remaining Amount is payed by Credit Card
      //False if all Order Amount is payed by other payment groups. Like OnlineCredits
      if(paymentGroupOrderRelationShipExist) {

        boolean validAuthorizationNumber = getBillingHelper().validateCreditCardAuthorizationNumber(pAuthNumber);
        if(!validAuthorizationNumber) {
          String msg = formatUserMessage(StoreBillingProcessHelper.VERIFICATION_NUMBER_INVALID, pRequest, pResponse);

          addFormException(new DropletFormException(msg, (String) null, StoreBillingProcessHelper.VERIFICATION_NUMBER_INVALID));
        }
      }
    }
  }

  /**
   * Validates the billing address,
   * Not Validate, if Order Amount is not payed by Credit Card
   *
   * @param pRequest - http request
   * @param pResponse - http response
   */
  protected boolean validateBillingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {

    List missingRequiredAddressProperties = null;

    ContactInfo billingAddress = null;

    CreditCard card = getCreditCard();

    boolean paymentGroupOrderRelationShipExist = getBillingHelper().isPaymentGroupOrderRelationShipExist(card);
    //True if Order Remaining Amount is payed by Credit Card
    //False if all Order Amount is payed by other payment groups. Like OnlineCredits
    if(paymentGroupOrderRelationShipExist) {

      billingAddress = (ContactInfo) card.getBillingAddress();
      missingRequiredAddressProperties =
                          getBillingHelper().checkForRequiredAddressProperties(billingAddress, pRequest);

      addAddressValidationFormError(missingRequiredAddressProperties, pRequest, pResponse);

      if ((missingRequiredAddressProperties == null || missingRequiredAddressProperties.isEmpty())){
        String country = billingAddress.getCountry();
        String state = billingAddress.getState();
        Place[] places = getPlaceUtils().getPlaces(country);
        if ((places == null && !StringUtils.isEmpty(state)) ||
            (places != null && !getPlaceUtils().isPlaceInCountry(country, state))){
          String msg = null;
          try {
            Locale userLocale = getOrderManager().getOrderTools().getProfileTools().getUserLocale(pRequest, pResponse);
            java.util.ResourceBundle countryStateBundle = atg.core.i18n.LayeredResourceBundle.getBundle(COUNTRY_STATE_RESOURCES,
                                                                                                        userLocale);
            String countryKey = COUNTRY_KEY_PREFIX + country;
            String countryName = countryStateBundle.getString(countryKey);
            msg = formatUserMessage(MSG_ERROR_INCORRECT_STATE, countryName, pRequest, pResponse);
            addFormException(new DropletFormException(msg, MSG_ERROR_INCORRECT_STATE));
          } catch (Exception e) {
            logError("Error validating state", e);
          }
        }
      }
    }

    return (missingRequiredAddressProperties == null || missingRequiredAddressProperties.isEmpty());
  }

  /**
   * Move to confirmation using saved credit card.
   */
  public boolean handleBillingWithSavedCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {
    setUsingProfileCreditCard(true);
    setSaveBillingAddress(false);
    return moveToConfirm(pRequest, pResponse);
  }

  /**
   * Move to confirmation using new billing address and new credit card info.
   */
  public boolean handleBillingWithNewAddressAndNewCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {
    setUsingProfileCreditCard(false);
    setUsingSavedAddress(false);
    return moveToConfirm(pRequest, pResponse);
  }

  /**
   * Move to confirmation using saved billing address and new credit card info.
   */
  public boolean handleBillingWithSavedAddressAndNewCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {
    setUsingProfileCreditCard(false);
    setUsingSavedAddress(true);
    setSaveBillingAddress(false);
    return moveToConfirm(pRequest, pResponse);
  }

  /**
   * Move to confirmation using only store credit as payment method.
   */
  public boolean handleBillingWithStoreCredit(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {
    setUsingStoreCredit(true);
    return moveToConfirm(pRequest, pResponse);
  }

  /**
   * Apply available store credits to order.
   */
  public boolean handleApplyStoreCreditsToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {
    Transaction tr = null;
    try {
      tr = ensureTransaction();
    synchronized (getOrder()) {
      preSetupStoreCreditPaymentGroupsForOrder(pRequest, pResponse);

      if (getFormError()) {

        if (isLoggingError()) {
          logDebug("Failure in preSetupStoreCreditPaymentGroupsForOrder process.");
        }
        return false;
      }

      setupStoreCreditPaymentGroupsForOrder(pRequest, pResponse);
      if (getFormError()) {
        if (isLoggingError()) {
          logDebug("Failure in setupStoreCreditPaymentGroupsForOrder process.");
        }
        return false;
      }

      try {
        getOrderManager().updateOrder(getOrder());
      }
      catch (Exception exc) {
        processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
      }
    }
    return true;
    }
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  /**
   * Register an account and link all the saved information like
   * the addresses and credit card info.
   */
  public boolean handleRegisterUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {


    preRegisterUserAfterCheckout(pRequest,pResponse);
    if (getFormError()) {
      return checkFormRedirect(null, getRegisterAccountErrorURL(), pRequest, pResponse);
    }

    registerUserAfterCheckout(pRequest, pResponse);

    postRegisterUserAfterCheckout(pRequest, pResponse);
    if (getFormError()) {
      return checkFormRedirect(null, getRegisterAccountErrorURL(), pRequest, pResponse);
    }

    return checkFormRedirect(getRegisterAccountSuccessURL(), getRegisterAccountErrorURL(), pRequest, pResponse);
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
   * This method runs the move to confirmation pipeline chain. It also calls
   * the preMoveToConfirm and postMoveToConfirm. The validation of inputs is
   * in preMoveToConfirm, and the saving of information is done in
   * postMoveToConfirm.
   *
   * @param pRequest
   *            a <code>DynamoHttpServletRequest</code> value
   * @param pResponse
   *            a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException
   *                if an error occurs
   * @exception IOException
   *                if an error occurs
   */
  public boolean moveToConfirm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (isLoggingDebug()) {
      debug();
    }

    Transaction tr = null;

    try {
      tr = ensureTransaction();

      // Check if session has expired, redirect to sessionExpired URL:
      if (!checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse)) {
        if (isLoggingDebug()) {
          logDebug("Form error at beginning of handleMoveToConfirm, redirecting.");
        }
        return false;
      }

      if (!isUsingStoreCredit()){
        // Validate credit card details if the user is entering new details
        if(isUsingProfileCreditCard()) {
          //In case of saved credit card, the address is always saved
          setUsingSavedAddress(true);
        }else{
          // Validate the billing address details if the user hasn't chosen an existing address
          if (!isUsingSavedAddress()) {
            if (!validateBillingAddress(pRequest, pResponse)) {
              return checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse);
            }
          }
          setStoredCreditCardName(StoreBillingProcessHelper.NEW_CREDIT_CARD);
        }
        if(!isUsingSavedAddress()) {
          setStoredAddressSelection(StoreBillingProcessHelper.NEW_ADDRESS);
        }
      }

      synchronized (getOrder()) {
        tenderCoupon(pRequest , pResponse);

        if (getFormError()) {
          return moveToConfirmExceptionHandling(pRequest, pResponse);
        }

        if (!isUsingStoreCredit()){
          if (getOrder().getPriceInfo().getTotal() > 0){
            // if order's amount is not covered by store credits
            // add credit card payment groups to order

            preSetupCreditCardPaymentGroupsForOrder(pRequest, pResponse);
            if (getFormError()) {
              return preMoveToConfirmExceptionHandling(pRequest, pResponse);
            }

            setupCreditCardPaymentGroupsForOrder(pRequest , pResponse);
            if (getFormError()) {
              return moveToConfirmExceptionHandling(pRequest , pResponse);
            }
            addCreditCardBillingAddress(pRequest, pResponse);
            if (getFormError()) {
              return moveToConfirmExceptionHandling(pRequest , pResponse);
            }

            addCreditCardAuthorizationNumber(pRequest, pResponse);
            if (getFormError()) {
              return moveToConfirmExceptionHandling(pRequest , pResponse);
            }
          }
        }

        try {
          runProcessMoveToConfirmation(pRequest, pResponse);
        }
        catch (Exception exc) {
          if (isLoggingError()) {
            logError(LogUtils.formatMajor(""), exc);
            logError(LogUtils.formatMajor("Error message from exception: " + exc.getMessage()));
          }

          processException(exc, MSG_ERROR_MOVE_TO_CONFIRM, pRequest, pResponse);
        }

        if (getFormError()) {
          if (isLoggingDebug()) {
            logDebug("Failure in pipeline process, returning.");
          }

          return checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse);
        }
        if (!isUsingStoreCredit()){
          addCreditCardToProfile(pRequest, pResponse);
        }

        try {
          getOrderManager().updateOrder(getOrder());
        }
        catch (Exception exc) {
          processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        }
      }

      if (mCheckoutProgressStates != null && !getFormError())
      {
        mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.CONFIRM.toString());
      }

      // synchronized
      return checkFormRedirect(getMoveToConfirmSuccessURL(), getMoveToConfirmErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  public boolean moveToConfirmExceptionHandling(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    try {
      // BUG 151435 - Because the form acts directly upon the payment group object,
      // we must be sure to rollback if anything fails verification
      setTransactionToRollbackOnly();
    } catch (javax.transaction.SystemException e) {
      processException(e, MSG_ERROR_MOVE_TO_CONFIRM, pRequest, pResponse);
    }
    if (isLoggingDebug()) {
      logDebug("Error in MoveToConfirm, redirecting to error URL.");
    }
    return checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse);
  }

  public boolean preMoveToConfirmExceptionHandling(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    if (isLoggingDebug()) {
      logDebug("Error in preMoveToConfirm, redirecting to error URL.");
    }
    return checkFormRedirect(null, getMoveToConfirmErrorURL(), pRequest, pResponse);
  }


  /**
   * Utility method to add form exception.
   *
   * @param pMissingProperties - missing properties list
   * @param pRequest - http request
   * @param pResponse - http response
   */
  public void addAddressValidationFormError(List pMissingProperties,
                                            DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {

    if (pMissingProperties != null && pMissingProperties.size() > 0) {

      Map addressPropertyNameMap = getBillingHelper().getAddressPropertyNameMap();

      Iterator properator = pMissingProperties.iterator();
      while (properator.hasNext()) {

        String property = (String) properator.next();
        if (isLoggingDebug()) {
          logDebug("Address validation error with: " + addressPropertyNameMap.get(property) + " property.");
        }

        // This is the default message, and will only display if there is
        // an exception getting the message from the resource bundle.
        String errorMsg = "Required properties are missing from the address.";
        try {

          errorMsg = formatUserMessage(StoreBillingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY,
              addressPropertyNameMap.get(property), pRequest, pResponse);
        } catch (Exception e) {

          if (isLoggingError()) {
            logError(LogUtils.formatMinor("Error getting error string with key: " + StoreBillingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY +
                " from resource " + PurchaseUserMessage.RESOURCE_BUNDLE + ": " + e.toString()));
          }
        }

        addFormException(new DropletFormException(errorMsg,
            (String) addressPropertyNameMap.get(property), StoreBillingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY));
      }
    }
  }

  /**
   * Sets initial values to storedCreditCardName and usingProfileCreditCard
   * fields based on the order
   */
  public void initializeCreditCardSelection(){
    if ( !mCreditCardSelectionInitialized && mStoredCreditCardName == null) {
      StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
      StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
      CreditCard creditCard = orderTools.getCreditCard(getOrder());
      Map creditCards = profileTools.getCreditCards(getProfile());

      // Initially set the selected credit card as the first entry in the user profile credit card map,
      // this is superceded by the credit card associated with the order or the user profile credit card
      // if no credit card is specified on the order.
      TreeMap sortedCreditCards = new TreeMap(creditCards);
      if (sortedCreditCards != null && !sortedCreditCards.isEmpty()){
        mStoredCreditCardName = (String)sortedCreditCards.firstKey();
      }
      
      if (!StringUtils.isBlank(creditCard.getCreditCardNumber() )){

        String ccNickname = profileTools.getCreditCardNickname(getProfile(), creditCard);
        if (ccNickname != null){
          mStoredCreditCardName = ccNickname;
        }else{
          //stored credit card is not from profile so set usingProfileCreditCard
          //flag to false
          setUsingProfileCreditCard(false);
        }
      }else{
        //there is no stored credit card in order
        //set stored credit card name to default profile's credit card
        RepositoryItem defaultCreditCard = profileTools.getDefaultCreditCard(getProfile());
        if (defaultCreditCard != null){
          mStoredCreditCardName = profileTools.getCreditCardNickname(getProfile(),defaultCreditCard);
        }

      }

      mCreditCardSelectionInitialized = true;
    }
  }

  /**
   * Sets initial values to storedAddressSelection and usingSavedAddress
   * fields based on the order
   */
  public void initializeBillingAddressSelection(){
    if (!mBillingAddressSelectionInitialized && mStoredAddressSelection == null){
      String storedAddressSelection = "";
      StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
      StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
      Address billingAddress = orderTools.getCreditCard(getOrder()).getBillingAddress();
      Map addresses = (Map) getProfile()
                              .getPropertyValue(((StorePropertyManager) orderTools.getProfileTools()
                              .getPropertyManager()).getSecondaryAddressPropertyName());

      if (!StringUtils.isBlank(billingAddress.getAddress1())){
        String addrNickname = profileTools.getProfileAddressName(getProfile(), billingAddress);
        if (addrNickname != null){
          storedAddressSelection = addrNickname;
        }else{
          //stored billing address is not from profile so set usingSavedAddress
          //flag to false
          setUsingSavedAddress(false);
        }
      }else{
        //there is no stored billing address in order
        //set billing address name to default profile's billing address
        RepositoryItem defaultBillingAddress = profileTools.getDefaultBillingAddress(getProfile());
        if (defaultBillingAddress != null){
          storedAddressSelection = profileTools.getProfileAddressName(getProfile(), defaultBillingAddress);
        }
      }

      // If the address is not present in the profile secondary address list then set the
      // selected address to the default shipping address or as the first entry in the
      // secondary address map guaranteeing we have a valid default selection.
      if (StringUtils.isEmpty(storedAddressSelection)
          || !(addresses != null && addresses.containsKey(storedAddressSelection))) {

        // Check if the default shipping address is in the secondary addresses.
        storedAddressSelection = profileTools.getDefaultShippingAddressNickname(getProfile());
        
        if (addresses != null && addresses.containsKey(storedAddressSelection)) {
          mStoredAddressSelection = storedAddressSelection;
        }
        else {
          TreeMap sortedAddresses = new TreeMap(addresses);
          if (sortedAddresses != null && !sortedAddresses.isEmpty()){
            mStoredAddressSelection = (String)sortedAddresses.firstKey();
          }
        }
      }
      else {
        mStoredAddressSelection = storedAddressSelection;
      }

      mBillingAddressSelectionInitialized = true;
    }
  }

  /**
   * Logs debugging info about the form settings and payment group.
   */
  protected void debug() {
    String[] storeCreditIds = getBillingHelper().getStoreCreditIds(getProfile());
    if (storeCreditIds != null) {
      logDebug("Number of store credits selected: " + storeCreditIds.length);
    } else {
      logDebug("Store credit ids == null.");
    }

    logDebug("Inside handleMoveToConfirmation");
    logDebug("Need to save credit card: " + getSaveCreditCard());
    logDebug("Stored credit card name: " + getStoredCreditCardName());
    logDebug("Requires registration: " + getRequiresRegistration());
  }

  /**
   * Operation called to get the Date on the basis of locale format.
   *
   * @param pDate
   *            getting from database
   * @param pFormat
   *            Date Format get by database
   * @return date in specified format
   */
  protected String getDateByFormat(Object pDate, String pFormat) {
    DateFormat df = new SimpleDateFormat(DATE_FORMAT);

    try {
      Date d = df.parse(pDate.toString());

      if (pFormat != null) {
        df = new SimpleDateFormat(pFormat);
      }

      return df.format(d);
    } catch (ParseException e) {
      return pDate.toString();
    }
  }


}
