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
package atg.projects.store.profile;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.transaction.TransactionManager;

import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.gifts.GiftlistTools;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.profile.CommerceProfileFormHandler;
import atg.commerce.profile.CommercePropertyManager;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.i18n.LocaleUtils;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.droplet.DropletFormException;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import atg.multisite.SiteContextManager;
import atg.payment.creditcard.CreditCardInfo;
import atg.payment.creditcard.CreditCardTools;
import atg.payment.creditcard.ExtendableCreditCardTools;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.projects.store.payment.BasicStoreCreditCardInfo;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.security.IdentityManager;
import atg.security.SecurityException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;
import atg.userprofiling.address.AddressTools;


/**
 * Profile form handler.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/profile/StoreProfileFormHandler.java#3 $$Change: 635816 $
 * @updated $DateTime: 2011/02/23 04:31:11 $$Author: rmcvey $
 *
 * @see atg.commerce.profile.CommerceProfileFormHandler
 */
public class StoreProfileFormHandler extends CommerceProfileFormHandler {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/profile/StoreProfileFormHandler.java#3 $$Change: 635816 $";
  
  //-------------------------------------
  // Constants
  //-------------------------------------
  
  private static final String NO = "no";

  private static final String YES = "yes";

  // User messages
  /**
   * Error creating address message key.
   */
  static final String MSG_ERR_CREATING_ADDRESS = "errorCreatingAddress";

  /**
   * Error deleting address message key.
   */
  static final String MSG_ERR_DELETING_ADDRESS = "errorDeletingAddress";

  /**
   * Error updating address message key.
   */
  static final String MSG_ERR_UPDATING_ADDRESS = "errorUpdatingAddress";

  /**
   * Duplicate address nickname message key.
   */
  static final String MSG_DUPLICATE_ADDRESS_NICKNAME = "errorDuplicateNickname";

  /**
   * Error while modifying nickname message key.
   */
  static final String MSG_ERR_MODIFYING_NICKNAME = "errorModifyingNickname";

  /**
   * Missing address property message key.
   */
  static final String MSG_MISSING_ADDRESS_PROPERTY = "missingAddressProperty";

  /**
   * Error creating credit card message key.
   */
  static final String MSG_ERR_CREATING_CC = "errorCreatingCreditCard";

  /**
   * Error while updating credit card message key.
   */
  static final String MSG_ERR_UPDATING_CREDIT_CARD = "errorUpdatingCreditCard";

  /**
   * Missing credit card property message key.
   */
  static final String MSG_MISSING_CC_PROPERTY = "missingCreditCardProperty";

  /**
   * Duplicate CC nickname message key.
   */
  static final String MSG_DUPLICATE_CC_NICKNAME = "errorDuplicateCCNickname";

  /**
   * Invalid credit card message key.
   */
  static final String MSG_INVALID_CC = "errorInvalidCreditCard";

  /**
   * Missing default credit card message key.
   */
  static final String MSG_MISSING_DEFAULT_CC = "missingDefaultCreditCard";

  /**
   * Duplicate user message key.
   */
  static final String MSG_DUPLICATE_USER = "userAlreadyExists";

  /**
   * Error while creating e-mail recipient message key.
   */
  static final String MSG_ERR_CREATING_EMAIL_RECIPIENT = "errorCreatingEmailRecipient";

  /**
   * Error while removing e-mail recipient message key.
   */
  static final String MSG_ERR_REMOVING_EMAIL_RECIPIENT = "errorRemovingEmailRecipient";

  /**
   * Error while updating e-mail recipient message key.
   */
  static final String MSG_ERR_UPDATING_EMAIL_RECIPIENT = "errorUpdatingEmailRecipient";

  /**
   * Invalid password length message key.
   */
  static final String MSG_INVALID_PASSWORD_LENGTH = "invalidPasswordLength";

  /**
   * Invalid password message key.
   */
  static final String MSG_INVALID_PASSWORD = "invalidPassword";

  /**
   * Already logged in message key.
   */
  static final String MSG_ALREADY_LOGGEDIN = "invalidLoginAlreadyLoggedIn";

  /**
   * Missing e-mail message key.
   */
  static final String MSG_MISSING_EMAIL = "missingEmail";

  /**
   * Error while updating status message key.
   */
  static final String MSG_ERROR_UPDATE_STATUS = "errorUpdatingSecurityStatus";

  /**
   * Invalid e-mail address message key.
   */
  static final String MSG_INVALID_EMAIL = "invalidEmailAddress";

  /**
   * State required message key.
   */
  static final String MSG_STATE_REQUIRED = "stateRequired";


  /**
   * State is incorrect message key.
   */
  static final String MSG_STATE_IS_INCORRECT = "stateIsIncorrect";

  /**
   * Invalid date format message key.
   */
  static final String MSG_INVALID_DATE_FORMAT = "invalidDateFormat";

  /**
   * Resource bundle.
   */
  static final String RESOURCE_BUNDLE = "atg.commerce.profile.UserMessages";

  /**
   * Items to add constant.
   */
  static final String ITEMS_TO_ADD = "itemsToAdd";

  /**
   * New e-mail address constant.
   */
  static final String NEW_EMAIL_ADDRESS = "newEmailAddress";

  /**
   * Portal logout URL parameter.
   */
  static final String PARAM_PORTAL_LOGOUT_URL = "portalLogoutSuccessURL";

  /** User validation options during checkout. see property CheckoutLoginOption */

  /**
   * Create new user constant.
   */
  static final String CREATE_NEW_USER = "createnewuser";

  /**
   * Existing user constant.
   */
  static final String EXISTING_USER = "continueexistinguser";

  /**
   * Continue anonymous constant.
   */
  static final String CONTINUE_ANONYMOUS = "continueanonymous";

  /**
   * Checkout option parameter name.
   */
  static final String CHECKOUT_OPTION_PARAMNAME = "checkoutoption";

  /**
   * Date format constant.
   */
  static final String DATE_FORMAT = "yyyy-MM-dd";
  
  static final String MSG_ERR_DELETE_GIFT_ADDRESS = "errorDeletingGiftAddress";
  
  //-------------------------------------
  // Properties
  //-------------------------------------
  
  //--------------------------------------------------
  // property: SessionBean
  private SessionBean mSessionBean;
  
  public SessionBean getSessionBean()
  {
    return mSessionBean;
  }

  public void setSessionBean(SessionBean pSessionBean)
  {
    mSessionBean = pSessionBean;
  }

  //--------------------------------------------------
  // property: GiftlistManager
  private GiftlistManager mGiftlistManager;
  
  public GiftlistManager getGiftlistManager()
  {
    return mGiftlistManager;
  }

  public void setGiftlistManager(GiftlistManager pGiftlistManager)
  {
    mGiftlistManager = pGiftlistManager;
  }
  
  //--------------------------------------------------
  // property: nicknameValueMapKey
  private String mNicknameValueMapKey = "nickname";

  /**
   * @return the String
   */
  public String getNicknameValueMapKey() {
    return mNicknameValueMapKey;
  }

  /**
   * @param NicknameValueMapKey the String to set
   */
  public void setNicknameValueMapKey(String pNicknameValueMapKey) {
    mNicknameValueMapKey = pNicknameValueMapKey;
  }

//--------------------------------------------------
  /**
   * property: addressIdValueMapKey
   * The value located under this key will be used to check whether the address being updated is default or not. 
   * */ 
  private String mAddressIdValueMapKey = "addressId";

  /**
   * @return the String
   */
  public String getAddressIdValueMapKey() {
    return mAddressIdValueMapKey;
  }

  /**
   * @param AddressIdValueMapKey the String to set
   */
  public void setAddressIdValueMapKey(String pAddressIdValueMapKey) {
    mAddressIdValueMapKey = pAddressIdValueMapKey;
  }

  
  //--------------------------------------------------
  // property: newNicknameValueMapKey
  private String mNewNicknameValueMapKey = "newNickname";

  /**
   * @return the String
   */
  public String getNewNicknameValueMapKey() {
    return mNewNicknameValueMapKey;
  }

  /**
   * @param NewNicknameValueMapKey the String to set
   */
  public void setNewNicknameValueMapKey(String pNewNicknameValueMapKey) {
    mNewNicknameValueMapKey = pNewNicknameValueMapKey;
  }

  //--------------------------------------------------
  // property: shippingAddressNicknameMapKey
  private String mShippingAddressNicknameMapKey = "shippingAddrNickname";

  /**
   * @return the String
   */
  public String getShippingAddressNicknameMapKey() {
    return mShippingAddressNicknameMapKey;
  }

  /**
   * @param ShippingAddressNicknameMapKey the String to set
   */
  public void setShippingAddressNicknameMapKey(
      String pShippingAddressNicknameMapKey) {
    mShippingAddressNicknameMapKey = pShippingAddressNicknameMapKey;
  }
  
  //-------------------------------------
  // property: editValue

  HashMap mEditValue = new HashMap();
  /**
   * @return The value of the property EditValue.  This is a map
   * that stores the pending values for an editing operations on the B2CStore profile
   * @beaninfo
   * description: map storing edit value for the B2CStore profile.
   */
  public Map getEditValue() {
    return mEditValue;
  }
  
  //-------------------------------------
  // property: addressProperties
  String[] mAddressProperties = new String[] {"firstName", "middleName", "lastName",
                                              "address1","address2", "city", "state",
                                              "postalCode", "country", "ownerId"};
  
  /**
   * Sets property addressProperties, naming the properties in a
   * secondary address record.
   **/
  public void setAddressProperties(String[] pAddressProperties) {
    mAddressProperties = pAddressProperties;
  }

  /**
   * Returns property addressProperties, naming the properties in a
   * secondary address record.
   * @beaninfo
   *  description: List of address properties that are available
   **/
  public String[] getAddressProperties() {
    return mAddressProperties;
  }
  
  //-------------------------------------
  // property: cardProperties
  /**
   * Card properties names.
   */
  private String[] mCardProperties = new String[] { "creditCardNumber",
      "creditCardType", "expirationMonth", "expirationYear", "billingAddress" };
  
  /**
   * Sets property cardProperties, naming the properties in a
   * credit card entry.
   **/
  public void setCardProperties(String[] pCardProperties) {
    mCardProperties = pCardProperties;
  }

  /**
   * Returns property cardProperties, naming the properties in a
   * credit card entry.
   * @beaninfo
   *  description: The list of card properties that are available
   **/
  public String[] getCardProperties() {
    return mCardProperties;
  }
  
  /**
   * By default CC number and it's type are immutable.
   */
  private String[] mImmutableCardProperties = new String[] {"creditCardNumber", "creditCardType"};
  
  /**
   * Returns immutableCardProperties property value. This property contains names of credit card's immutable properties. 
   * @return immutableCardProperties
   */
  public String[] getImmutableCardProperties()
  {
    return mImmutableCardProperties;
  }
  
  /**
   * Sets immutableCardProperties property value.
   * @param pImmutableCardProperties property names to be immutable.
   */
  public void setImmutableCardProperties(String[] pImmutableCardProperties)
  {
    mImmutableCardProperties = pImmutableCardProperties;
  }
   
  //-------------------------------------
  // property: removeAddress
  String mRemoveAddress;

  /**
   * Sets property removeAddress, naming the address to be removed by
   * handleRemoveAddress().
   **/
  public void setRemoveAddress(String pRemoveAddress) {
    mRemoveAddress = pRemoveAddress;
  }

  /**
   * Returns property removeAddress, naming the address to be removed by
   * handleRemoveAddress().
   * @beaninfo
   *  expert: false
   **/
  public String getRemoveAddress() {
    return mRemoveAddress;
  }
  
  //-------------------------------------
  // property: editAddress
  String mEditAddress;

  /**
   * Sets property editAddress, naming the address to be edited
   **/
  public void setEditAddress(String pEditAddress) {
    mEditAddress = pEditAddress;
  }

  /**
   * Returns property editAddress, naming the address to be edited
   * @beaninfo
   *  description: The edit address property.
   **/
  public String getEditAddress() {
    return mEditAddress;
  }
  
  //-------------------------------------
  // property: removeCard
  String mRemoveCard;

  /**
   * Sets property removeCard, naming the address to be removed by
   * handleRemoveCard().
   **/
  public void setRemoveCard(String pRemoveCard) {
    mRemoveCard = pRemoveCard;
  }

  /**
   * Returns property removeCard, naming the address to be removed by
   * handleRemoveCard().
   * @beaninfo
   *  expert: false
   **/
  public String getRemoveCard() {
    return mRemoveCard;
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
      StorePropertyManager propertyManager = getStorePropertyManager();
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

  //-------------------------------------
  // property: billingAddressPropertyList 
  /**
   * Billing address property list.
   */
  private List mBillingAddressPropertyList = null;

  /**
   * Sets the Address property list, which is a list that mirrors the original
   * design of the AddressProperties property with the property names defined
   * in a configuration file. This List will be created by the
   * initializeAddressPropertyList method creating the appropriate list
   * containing the values from the property manager.
   *
   * @param pBillingAddressPropertyList -
   *            Billing address property list
   */
  public void setBillingAddressPropertyList(List pBillingAddressPropertyList) {
    mBillingAddressPropertyList = pBillingAddressPropertyList;
  }

  /**
   * Returns the BillingAddressPropertyList.
   *
   * @return a List that contains the Address properties that are available
   * @beaninfo description: The address property list.
   */
  public List getBillingAddressPropertyList() {
    return mBillingAddressPropertyList;
  }
  
  //-------------------------------------
  // property: defaultCard 
  /**
   * Default card.
   */
  private String mDefaultCard;

  /**
   * Sets property efaultCard, naming the credit card to be the default.
   *
   * @param pDefaultCard - nickname of default credit card
   */
  public void setDefaultCard(String pDefaultCard) {
    mDefaultCard = pDefaultCard;
  }

  /**
   * Returns property editCard, naming the credit card to be edited.
   *
   * @beaninfo description: The edit credit card property.
   * @return Default credit card nickname
   */
  public String getDefaultCard() {
    return mDefaultCard;
  }
  
  //-------------------------------------
  // property: editCard 
  /**
   * Edit card.
   */
  private String mEditCard;
  
  /**
   * Sets property editCard, naming the credit card to be edited.
   *
   * @param pEditCard - nickname of card being edited
   */
  public void setEditCard(String pEditCard) {
    mEditCard = pEditCard;
  }

  /**
   * Returns property editCard, naming the credit card to be edited.
   *
   * @beaninfo description: The edit credit card property.
   * @return The nickname of the credit card being edited
   */
  public String getEditCard() {
    return mEditCard;
  }
  
  //-------------------------------------
  // property: billAddrValue 
  /**
   * Billing address values.
   */
  private HashMap mBillAddrValue = new HashMap();

  /**
   * @return The value of the property EditValue. This is a map that stores
   *         the pending values for an editing operations on the B2CStore
   *         profile.
   * @beaninfo description: map storing edit value for the B2CStore profile.
   */
  public Map getBillAddrValue() {
    return mBillAddrValue;
  }

  //-------------------------------------
  // property: defaultShippingAddress 
  /**
   * Default shipping address.
   */
  private String mDefaultShippingAddress;

  /**
   * @param pDefaultShippingAddress - default shipping address.
   */
  public void setDefaultShippingAddress(String pDefaultShippingAddress) {
    mDefaultShippingAddress = pDefaultShippingAddress;
  }

  /**
   * @return default shipping address.
   */
  public String getDefaultShippingAddress() {
    return mDefaultShippingAddress;
  }
  
  //-------------------------------------
  // property: defaultShippingAddress 
  /**
   * Default shipping address.
   */
  private boolean mUseShippingAddressAsDefault;

  
  public boolean isUseShippingAddressAsDefault() {
    return mUseShippingAddressAsDefault;
  }

  public void setUseShippingAddressAsDefault(boolean pUseShippingAddressAsDefault) {
    mUseShippingAddressAsDefault = pUseShippingAddressAsDefault;
  }

  //-------------------------------------
  // property: sourceCode 
  /**
   * Source code.
   */
  private String mSourceCode;

  /**
   * @return source code.
   */
  public String getSourceCode() {
    return mSourceCode;
  }

  /**
   * @param pSourceCode - source code.
   */
  public void setSourceCode(String pSourceCode) {
    mSourceCode = pSourceCode;
  }
  
  //-------------------------------------
  // property: checkoutLoginOption 
  /**
   * Checkout login option.
   */
  private String mCheckoutLoginOption;

  /**
   * Sets property mCheckoutLoginOption.
   *
   * @param pCheckoutLoginOption - true if login should be checked,
   * false - otherwise
   */
  public void setCheckoutLoginOption(String pCheckoutLoginOption) {
    mCheckoutLoginOption = pCheckoutLoginOption;
  }

  /**
   * @return true if login should be checked,
   * false - otherwise.
   */
  public String getCheckoutLoginOption() {
    if (mCheckoutLoginOption == null) {
      return CONTINUE_ANONYMOUS;
    }

    return mCheckoutLoginOption;
  }


  //-------------------------------------
  // property: loginEmailAddress 
  /**
   * Used to display message after email address is changed.
   */
  private String mLoginEmailAddress;

  /**
   * @return login e-mail address.
   */
  public String getLoginEmailAddress() {
    return mLoginEmailAddress;
  }

  /**
   * @param pLoginEmailAddress - login e-mail address.
   */
  public void setLoginEmailAddress(String pLoginEmailAddress) {
    mLoginEmailAddress = pLoginEmailAddress;
  }
  
  //-------------------------------------
  // property: emailAddress 
  /**
   * E-mail address.
   */
  private String mEmailAddress;

  /**
   * @return e-mail address.
   */
  public String getEmailAddress() {
    return mEmailAddress;
  }

  /**
   * @param pEmailAddress - e-mail address.
   */
  public void setEmailAddress(String pEmailAddress) {
    mEmailAddress = pEmailAddress;
  }
  
  //--------------------------------------------------
  // property: NewCustomerEmailAddress
  private String mNewCustomerEmailAddress;

  /**
   * @return new customer email address
   */
  public String getNewCustomerEmailAddress() {
    return mNewCustomerEmailAddress;
  }

  /**
   * @param pNewCustomerEmailAddress email address
   */
  public void setNewCustomerEmailAddress(String pNewCustomerEmailAddress) {
    mNewCustomerEmailAddress = pNewCustomerEmailAddress;
  }
  
  //--------------------------------------------------
  // property: AnonymousEmailAddress
  private String mAnonymousEmailAddress;

  /**
   * @return anonymous customer email address
   */
  public String getAnonymousEmailAddress() {
    return mAnonymousEmailAddress;
  }

  /**
   * @param pAnonymousEmailAddress email address
   */
  public void setAnonymousEmailAddress(String pAnonymousEmailAddress) {
    mAnonymousEmailAddress = pAnonymousEmailAddress;
  }
  
  //-------------------------------------
  // property: order 
  private Order mOrder;

  /**
   * Set the Order property.
   *
   * @param pOrder
   *            an <code>Order</code> value
   */
  public void setOrder(Order pOrder) {
    mOrder = pOrder;
  }

  /**
   * Return the Order property.
   *
   * @return an <code>Order</code> value
   */
  public Order getOrder() {
    if (mOrder != null) {
      return mOrder;
    } else {
      return getShoppingCart().getCurrent();
    }
  }
  
  //-------------------------------------
  // property: userItems 
  /**
   * User items.
   */
  private UserItems mUserItems;

  /**
   * @return user items.
   */
  public UserItems getUserItems() {
    return mUserItems;
  }

  /**
   * @param pUserItems - user items.
   */
  public void setUserItems(UserItems pUserItems) {
    mUserItems = pUserItems;
  }
  
  //-------------------------------------
  // property: shoppingCart 
  /**
   * Shopping cart.
   */
  private OrderHolder mShoppingCart;

  /**
   * Sets property ShoppingCart.
   *
   * @param pShoppingCart
   *            an <code>OrderHolder</code> value
   */
  public void setShoppingCart(OrderHolder pShoppingCart) {
    mShoppingCart = pShoppingCart;
  }

  /**
   * Returns property ShoppingCart.
   *
   * @return an <code>OrderHolder</code> value
   */
  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }
  
  //-------------------------------------
  // property: addCommerceItemInfos   
  /**
   * Add commerce item infos.
   */
  private List mAddCommerceItemInfos;

  /**
   * @param pAddCommerceItemInfos - add commerce item
   * information.
   */
  public void setAddCommerceItemInfos(List pAddCommerceItemInfos) {
    mAddCommerceItemInfos = pAddCommerceItemInfos;
  }

  /**
   * @return add commerce item information.
   */
  public List getAddCommerceItemInfos() {
    return mAddCommerceItemInfos;
  }
  
  //-------------------------------------
  // property: requiredBillingAddressPropertyList  
  
  /**
   * Required billing address property list.
   */
  private List mRequiredBillingAddressPropertyList;

  /**
   * @return required billing address property list.
   */
  public List getRequiredBillingAddressPropertyList() {
    return mRequiredBillingAddressPropertyList;
  }

  /**
   * @param pRequiredBillingAddressPropertyList -
   * required billing address property list.
   */
  public void setRequiredBillingAddressPropertyList(
      List pRequiredBillingAddressPropertyList) {
    mRequiredBillingAddressPropertyList = pRequiredBillingAddressPropertyList;
  }
  
  //-------------------------------------
  // property: emailOptIn   
  /**
   * E-mail opt in.
   */
  private boolean mEmailOptIn;

  /**
   * @return true if e-mail opt in is turned on,
   * false - otherwise.
   */
  public boolean isEmailOptIn() {
    return mEmailOptIn;
  }

  /**
   * @param pEmailOptIn - true to turn on e-mail opt in, false - otherwise.
   */
  public void setEmailOptIn(boolean pEmailOptIn) {
    mEmailOptIn = pEmailOptIn;
  }
  
  //-------------------------------------
  // property: previousOptInStatus   
  /**
   * Previous opt in status.
   */
  private boolean mPreviousOptInStatus;

  /**
   * @return previous opt in status.
   */
  public boolean isPreviousOptInStatus() {
    return mPreviousOptInStatus;
  }

  /**
   * @param pPreviousOptInStatus - previous opt in status.
   */
  public void setPreviousOptInStatus(boolean pPreviousOptInStatus) {
    mPreviousOptInStatus = pPreviousOptInStatus;
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
  
  //------------------------------------
  // ------ Redirection properties ------
  // ------------------------------------
  //
  // The following properties are set in a form to instruct a handler
  // to redirect the user in case of success or failure.
  //
  // 1. newAddressSuccessURL
  //    newAddressErrorURL
  // 2. updateAddressSuccessURL
  //    updateAddressErrorURL
  // 3. createCardSuccessURL
  //    createCardErrorURL
  // 4. updateCardSuccessURL
  //    updateCardErrorURL
  // 5. removeCardSuccessURL
  //    removeCardErrorURL

  //-------------------------------------
  // property: newAddressSuccessURL
  String mNewAddressSuccessURL;

  /**
   * Sets property newAddressSuccessURL, used to redirect user after
   * successfully creating an address.
   **/
  public void setNewAddressSuccessURL(String pNewAddressSuccessURL) {
    mNewAddressSuccessURL = pNewAddressSuccessURL;
  }

  /**
   * Returns property newAddressSuccessURL, used to redirect user after
   * successfully creating an address.
   * @beaninfo
   *  description: The the url to redirect user to after successfully creating an address
   **/
  public String getNewAddressSuccessURL() {
    return mNewAddressSuccessURL;
  }

  //-------------------------------------
  // property: newAddressErrorURL
  String mNewAddressErrorURL;

  /**
   * Sets property newAddressErrorURL, used to redirect user in
   * case of an error creating an address.
   **/
  public void setNewAddressErrorURL(String pNewAddressErrorURL) {
    mNewAddressErrorURL = pNewAddressErrorURL;
  }

  /**
   * Returns property newAddressErrorURL, used to redirect user in
   * case of an error creating an address.
   * @beaninfo
   *  description: The URL to redirect the user to in case of an error creating the address.
   **/
  public String getNewAddressErrorURL() {
    return mNewAddressErrorURL;
  }

  //-------------------------------------
  // property: updateAddressSuccessURL
  String mUpdateAddressSuccessURL;

  /**
   * Sets property updateAddressSuccessURL, used to redirect user when
   * an address is successfully updated.
   **/
  public void setUpdateAddressSuccessURL(String pUpdateAddressSuccessURL) {
    mUpdateAddressSuccessURL = pUpdateAddressSuccessURL;
  }

  /**
   * Returns property updateAddressSuccessURL, used to redirect user when
   * an address is successfully updated.
   * @beaninfo
   *  description: The URL to redirect the user to when the address has been updated successfully.
   **/
  public String getUpdateAddressSuccessURL() {
    return mUpdateAddressSuccessURL;
  }


  //-------------------------------------
  // property: updateAddressErrorURL
  String mUpdateAddressErrorURL;

  /**
   * Sets property updateAddressErrorURL, used to redirect user in
   * case of an error updating an address.
   **/
  public void setUpdateAddressErrorURL(String pUpdateAddressErrorURL) {
    mUpdateAddressErrorURL = pUpdateAddressErrorURL;
  }

  /**
   * Returns property updateAddressErrorURL, used to redirect user in
   * case of an error updating an address.
   * @beaninfo
   *  description: The URL to redirect the user to in case of an error updating the address.
   **/
  public String getUpdateAddressErrorURL() {
    return mUpdateAddressErrorURL;
  }
  
  //-------------------------------------
  // property: createCardSuccessURL
  String mCreateCardSuccessURL;

  /**
   * Sets property createCardSuccessURL, used to redirect user if
   * a new credit card was successfully added.
   **/
  public void setCreateCardSuccessURL(String pCreateCardSuccessURL) {
    mCreateCardSuccessURL = pCreateCardSuccessURL;
  }

  /**
   * Returns property createCardSuccessURL, used to redirect user if
   * a new credit card was successfully added.
   * @beaninfo
   *  description: The URL to redirect the user to if a new credit card was successfully added.
   **/
  public String getCreateCardSuccessURL() {
    return mCreateCardSuccessURL;
  }


  //-------------------------------------
  // property: createCardErrorURL
  String mCreateCardErrorURL;

  /**
   * Sets property createCardErrorURL, used to redirect user in
   * case of an error adding a new credit card.
   **/
  public void setCreateCardErrorURL(String pCreateCardErrorURL) {
    mCreateCardErrorURL = pCreateCardErrorURL;
  }

  /**
   * Returns property createCardErrorURL, used to redirect user in
   * case of an error adding a new credit card.
   * @beaninfo
   *  description: The URL to redirect the user to in case of an error while adding a new credit card.
   **/
  public String getCreateCardErrorURL() {
    return mCreateCardErrorURL;
  }
  
  //-------------------------------------
  // property: updateCardSuccessURL 
  /**
   * Update card success redirect URL.
   */
  private String mUpdateCardSuccessURL;
  
  /**
   * Sets property updateCardSuccessURL.
   *
   * @param pUpdateCardSuccessURL -
   *            credit card update success URL
   */
  public void setUpdateCardSuccessURL(String pUpdateCardSuccessURL) {
    mUpdateCardSuccessURL = pUpdateCardSuccessURL;
  }

  /**
   * Returns property updateCardSuccessURL.
   *
   * @beaninfo description: The URL to redirect the user to in case of an
   *           error while adding a new credit card.
   * @return mUpdateCardSuccessURL
   */
  public String getUpdateCardSuccessURL() {
    return mUpdateCardSuccessURL;
  }
  
  //-------------------------------------
  // property: updateCardErrorURL 
  /**
   * Update card error redirect URL.
   */
  private String mUpdateCardErrorURL;
  
  /**
   * Sets property updateCardErrorURL, used to redirect user in case of an
   * error updating a new credit card.
   *
   * @param pUpdateCardErrorURL - credit card update error URL
   */
  public void setUpdateCardErrorURL(String pUpdateCardErrorURL) {
    mUpdateCardErrorURL = pUpdateCardErrorURL;
  }

  /**
   * Returns property updateCardErrorURL, used to redirect user in case of an
   * error updating a new credit card.
   *
   * @beaninfo description: The URL to redirect the user to in case of an
   *           error while adding a new credit card.
   * @return update credit card error URL
   */
  public String getUpdateCardErrorURL() {
    return mUpdateCardErrorURL;
  }

  //-------------------------------------
  // property: removeCardSuccessURL
  String mRemoveCardSuccessURL;

  /**
   * Sets property removeCardSuccessURL, used to redirect user when
   * a credit card is successfully removed.
   **/
  public void setRemoveCardSuccessURL(String pRemoveCardSuccessURL) {
    mRemoveCardSuccessURL = pRemoveCardSuccessURL;
  }

  /**
   * Returns property removeCardSuccessURL, used to redirect user in
   * a credit card is successfully removed.
   * @beaninfo
   *  description: The URL to redirect the user to if the removal of credit card was successful
   **/
  public String getRemoveCardSuccessURL() {
    return mRemoveCardSuccessURL;
  }


  //-------------------------------------
  // property: removeCardErrorURL
  String mRemoveCardErrorURL;

  /**
   * Sets property removeCardErrorURL, used to redirect user in
   * case of an error removing a credit card.
   **/
  public void setRemoveCardErrorURL(String pRemoveCardErrorURL) {
    mRemoveCardErrorURL = pRemoveCardErrorURL;
  }

  /**
   * Returns property removeCardErrorURL, used to redirect user in
   * case of an error removing a credit card.
   * @beaninfo
   *  description: The URL to redirect the user to in case of an error while removing a credit card.
   **/
  public String getRemoveCardErrorURL() {
    return mRemoveCardErrorURL;
  }  
  
  String mPreRegisterSuccessURL;
  
      
  public String getPreRegisterSuccessURL() {
    return mPreRegisterSuccessURL;
  }

  public void setPreRegisterSuccessURL(String pPreRegisterSuccessURL) {
    mPreRegisterSuccessURL = pPreRegisterSuccessURL;
  }

  String mPreRegisterErrorURL;
  
  public String getPreRegisterErrorURL() {
    return mPreRegisterErrorURL;
  }

  public void setPreRegisterErrorURL(String pPreRegisterErrorURL) {
    mPreRegisterErrorURL = pPreRegisterErrorURL;
  }
  
  //-----------------------------------
  // property: shippingGroupMapContainer
  private ShippingGroupMapContainer mShippingGroupMapContainer;

  /**
   * Set the ShippingGroupMapContainer property.
   * @param pShippingGroupMapContainer a <code>ShippingGroupMapContainer</code> value
   */
  public void setShippingGroupMapContainer(ShippingGroupMapContainer pShippingGroupMapContainer) {
    mShippingGroupMapContainer = pShippingGroupMapContainer;
  }

  /**
   * Return the ShippingGroupMapContainer property.
   * @return a <code>ShippingGroupMapContainer</code> value
   */
  public ShippingGroupMapContainer getShippingGroupMapContainer() {
    return mShippingGroupMapContainer;
  }
      
  //-------------------------------------
  // Methods
  //-------------------------------------
  
  //---------------------------------------
  // handleLoginUser related methods 
  //---------------------------------------

  /**
   * Override OOTB method so that auto-logged in user is not logged out if
   * they provide an invalid password.
   * {@inheritDoc}
   *
   * @see atg.userprofiling.ProfileForm#preLoginUser(atg.servlet.DynamoHttpServletRequest,
   *                                                 atg.servlet.DynamoHttpServletResponse)
   */
  protected void preLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {

    // if the there's already a user logged in, see if this is the same user
    Profile profile = getProfile();

    if (!profile.isTransient()) {
      ProfileTools profileTools = getProfileTools();
      PropertyManager propertyManager = profileTools.getPropertyManager();

      // get the submitted form's login/pwd and hash the pwd
      String loginPropertyName = propertyManager.getLoginPropertyName();
      String login = getStringValueProperty(loginPropertyName).trim().toLowerCase();
      String pwdPropertyName = propertyManager.getPasswordPropertyName();
      String pwd = getStringValueProperty(pwdPropertyName).trim();
      String prLogin = profile.getPropertyValue(loginPropertyName).toString();

      boolean authSuccessful;
      IdentityManager identityManager = getUserLoginManager().getIdentityManager(pRequest);

      try {
        if (isLoggingDebug()) {
          logDebug("Checking user auth with login: " + login + " and pwd: " + pwd);
        }

        authSuccessful = identityManager.checkAuthenticationByPassword(login, pwd);

        if (isLoggingDebug()) {
          logDebug("Auth success: " + authSuccessful);
        }
      } catch (SecurityException e) {
        if (isLoggingDebug()) {
          logDebug("Exception during auth.");
        }

        authSuccessful = false;
      }
      
      // Compare logins ignoring case as logins are considered to be case-insensitive
      if (authSuccessful) {
        if (login.equalsIgnoreCase(prLogin)) {
          boolean addError = true;
  
          // user matches, check the security status,
          // if there's a security status property
          if (profileTools.isEnableSecurityStatus()) {
            int securityStatus = -1;
            String securityStatusPropertyName = propertyManager.getSecurityStatusPropertyName();
            Object ss = profile.getPropertyValue(securityStatusPropertyName);
  
            if (ss != null) {
              securityStatus = ((Integer) ss).intValue();
            }
  
            // See if the user is auto-logged in. If so, this could
            // indicate that this form is re-authenticating the user
            // for access to sensitive content
            addError = !getProfileTools().isAutoLoginSecurityStatus(securityStatus);
  
            // try to reset to securityLogin if it's not set already
            try {
              if (securityStatus != propertyManager.getSecurityStatusLogin()) {
                profileTools.setLoginSecurityStatus(profile, pRequest);
              }
            } catch (RepositoryException exc) {
              generateFormException(MSG_ERROR_UPDATE_STATUS, exc, pRequest);
  
              if (isLoggingError()) {
                logError(LogUtils.formatMinor(exc.getMessage()), exc);
              }
            }
          }
  
          // Only do this if the user is not auto-logged in
          if (addError) {
            // in any event, user's already logged in...          
            Object[] args = new Object[] { login };
            generateFormException(MSG_ALREADY_LOGGEDIN, args, new String(getAbsoluteName()), pRequest);          
          }
        } else {
          // User logged in, but tries to re-login as another person. Display an error to him/her.
          // We can re-use this message code, because login page will not be displayed to logged in users anymore
          // hence this method will never be invoked on logged in user
          generateFormException(MSG_ALREADY_LOGGEDIN, new Object[] {login}, getAbsoluteName(), pRequest);
        }
      } else {
        // submitted login/pwd do NOT match those of existing session,
        // add form exception
        generateFormException(MSG_INVALID_PASSWORD, getAbsoluteName(), pRequest);
      }
    }
  }
  
  /**
   * This method adds product into giftlist.
   * All neccessary IDs (product ID, SKU ID, giftlist ID etc.) are taken from appropriate <code>values</code> properties of the
   * <code>SessionBean</code> component.
   * @throws ServletException if unable to add item to giftlist
   */
  private void postLoginAddItemToGiftlist() throws ServletException {
    String skuId = (String) getSessionBean().getValues().get(SessionBean.SKU_ID_TO_GIFTLIST_PROPERTY_NAME);
    String productId = (String) getSessionBean().getValues().get(SessionBean.PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME);
    String giftListId = (String) getSessionBean().getValues().get(SessionBean.GIFTLIST_ID_PROPERTY_NAME);
    giftListId = giftListId != null ? giftListId :
        ((RepositoryItem) getProfile().getPropertyValue(getStorePropertyManager().getWishlistPropertyName())).getRepositoryId();
    String commerceItemId = (String) getSessionBean().getValues().get(SessionBean.COMMERCE_ITEM_ID_PROPERTY_NAME);
    Long quantity = (Long) getSessionBean().getValues().get(SessionBean.QUANTITY_TO_ADD_TO_GIFTLIST_PROPERTY_NAME);
    
    try
    {
      if (skuId != null && productId != null)
      {
        if (giftListId == null){
          // set to profile wish list
          giftListId = ((RepositoryItem) getProfile().getPropertyValue(getStorePropertyManager().getWishlistPropertyName()))
              .getRepositoryId();
        }
        long qty = 1L;
        if (quantity != null){
          qty = quantity.longValue();
        }
        getGiftlistManager().addCatalogItemToGiftlist(skuId, productId, giftListId, SiteContextManager.getCurrentSiteId(), qty);
        
        if (!StringUtils.isEmpty(commerceItemId)){
          // complete moving of item from cart to wish list,
          // decrease item's quantity in cart by the specified quantity
          CommerceItem item = getOrder().getCommerceItem(commerceItemId);
          long itemQuantity = item.getQuantity();
          if (itemQuantity - qty > 0) {
            item.setQuantity(itemQuantity - qty);
          } else {
            getOrderManager().getCommerceItemManager().removeItemFromOrder(getOrder(), commerceItemId);
          }
          getOrderManager().updateOrder(getOrder());
        }
      }
    } catch (CommerceException e)
    {
      throw new ServletException(e);
    } finally
    {
      clearLoginPropertiesFromSession();
    }
  }
  
  @SuppressWarnings("unchecked") //ok, we know what to get and put from SessionBean values
  protected void postLoginUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException,
      IOException
  {
    super.postLoginUser(pRequest, pResponse);
    
    String loginSuccess = pRequest.getParameter(HANDLE_LOGIN);
    if (HANDLE_SUCCESS.toString().equals(loginSuccess))
    {
      String redirectUrl = (String) getSessionBean().getValues().get(SessionBean.REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME);
      if (redirectUrl != null)
      {
        setLoginSuccessURL(redirectUrl);
      }
      
      postLoginAddItemToGiftlist();
    }
  }
  
  //---------------------------------------
  // handleLoginDuringCheckout and related methods
  //---------------------------------------

  /**
   * Handle login during the checkout process. This method must allow a
   * logout/login during the checkout process. If the user is recognized and
   * decides they are a new user, then this method must log them out and
   * register them as a new user. It must make sure the correct cart gets
   * delivered.
   * <p>
   * This method will not allow the carts to merge and will ensure the current
   * 'anonymous' cart is kept current.
   *
   * @param pRequest
   *            DynamoHttpServletRequest
   * @param pResponse
   *            DynamoHttpServletResponse
   * @return true if success, false - otherwise
   * @throws ServletException
   *             if there was an error while executing the code
   * @throws IOException
   *             if there was an error with servlet io
   */
  public boolean handleLoginDuringCheckout(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    //remember the select option for later in the check out process when the user will either
    //have to select a password or choose if an account should be created.

    //This value is passed using a query parameter rather than just setting it here in the session
    //bean because if handleLogout is called the session bean becomes new and the value is lost.
    String checkoutLoginOption = getCheckoutLoginOption();
    pRequest.addQueryParameter(CHECKOUT_OPTION_PARAMNAME, checkoutLoginOption);

    StorePropertyManager propertyManager = getStorePropertyManager();
    Profile user = (Profile) getProfile();
    String email = evalEmailAddress();

    if (StringUtils.isBlank(email) && !CONTINUE_ANONYMOUS.equals(checkoutLoginOption)) {
      generateFormException(MSG_MISSING_EMAIL, getAbsoluteName(), pRequest);

      return checkFormRedirect(null, getLoginErrorURL(), pRequest, pResponse);
    }
    
    // validate email address for non-anonymous users
    if(!CONTINUE_ANONYMOUS.equals(checkoutLoginOption)) { 
      if(!((StoreProfileTools)getProfileTools()).validateEmailAddress(email)) { 
        generateFormException(MSG_INVALID_EMAIL, getAbsoluteName(), pRequest);
      }
    }

    if (isLoggingDebug()) {
      logDebug("handleLoginDuringCheckout here.");
    }

    Dictionary value = getValue();

    //-------------------------
    // Registered user case:
    //-------------------------
    if (!isNewUser()) {
      // Need to set the editvalue for existing users to login.
      // put login value in lower case as logins are considered to be case-insensitive
      value.put(propertyManager.getLoginPropertyName(), email.toLowerCase());
      value.put(propertyManager.getEmailAddressPropertyName(), email);

      if (isLoggingDebug()) {
        logDebug("User is logging in with email address: " + email);
        logDebug("String value email: " + getStringValueProperty("email"));
        logDebug("String value login: " + getStringValueProperty("login"));
      }

      pRequest.setParameter("userCheckingOut", new Boolean("true"));

      return handleLogin(pRequest, pResponse);
    }

    //if the user wants to create a new profile, check that the email address is
    //unique in the profile repository.
    if (checkoutLoginOption.equals(CREATE_NEW_USER)) {
      // make sure a user with this email address doesn't already exist
      // UPDATE: Do we need to check all types of profiles?
      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

      if (profileTools.isDuplicateEmailAddress(email)) {
        generateFormException(MSG_DUPLICATE_USER, getAbsoluteName(), pRequest);

        return checkFormRedirect(null, getLoginErrorURL(), pRequest, pResponse);
      }

      pRequest.addQueryParameter(NEW_EMAIL_ADDRESS, email);
    }

    //The user selected to either continue with a new account or continue anonymous.

    //if we are cookie authenticated already then the current user must be logged out.
    //the logout operation will result in a new transient profile.
    //The shipping page will copy the items in UserItems into the new order and set
    //the email property of the profile.
    String securityProperty = propertyManager.getSecurityStatusPropertyName();
    int securityStatus = ((Integer) user.getPropertyValue(securityProperty)).intValue();

    //--------------------------
    // Auto-logged in user case:
    //--------------------------
    if (securityStatus > propertyManager.getSecurityStatusAnonymous()) {
      if (isLoggingDebug()) {
        logDebug("User recognized, but wants to checkout anonymously.");
      }

      // Need to get cart contents, log them out, and add items to new
      // cart.
      StoreOrderManager om = (StoreOrderManager) getOrderManager();
      List addCommerceItemInfos = om.buildItemInfos(getOrder());

      // The current items are stored in this Map
      // (getUserItems().getItems()) and
      // are retrieved by the droplet "AddItemsToOrder" which must be
      // present
      // on the following page.
      if ((addCommerceItemInfos != null) && (addCommerceItemInfos.size() > 0)) {
        getUserItems().getItems().put(getProfile().getRepositoryId(), addCommerceItemInfos);
      }

      // Set these 2 properties in the request. These will be picked up,
      // and will
      // be used to set the new user's email address, and add the current
      // items
      // to the new cart.
      pRequest.addQueryParameter(UserItems.ITEM_KEY, getProfile().getRepositoryId());

      setExpireSessionOnLogout(true);

      return super.handleLogout(pRequest, pResponse);
    }

    //if we make it this far then the we didn't have to logout first. If the user
    //have selected to create a new account then set the profile's login and email
    //address properties.
    if (checkoutLoginOption.equals(CREATE_NEW_USER)) {
      user.setPropertyValue(propertyManager.getEmailAddressPropertyName(), email);
      // Store login in lower case to support case-insensitive logins
      user.setPropertyValue(propertyManager.getLoginPropertyName(), email.toLowerCase());
    }

    return checkFormRedirect(getLoginSuccessURL(), getLoginErrorURL(), pRequest, pResponse);
  }
  
  /**
   * This method eval appropriate email address  
   * depends on selected <code>checkoutLoginOption</code>.
   * <ul>
   * <li>If <code>checkoutLoginOption</code> equals CONTINUE_ANONYMOUS
   * then use <code>anonymousEmailAddress</code>.
   * <li>If <code>checkoutLoginOption</code> equals CREATE_NEW_USER 
   * then use <code>newCustomerEmailAddress</code>.
   * <li>Otherwise, use registered user's email address.
   * </ul> 
   * @return email address
   */
  public String evalEmailAddress() {
    String checkoutloginOption = getCheckoutLoginOption();
    // using email address of existing user by default
    String emailAddress = getEmailAddress();    
    
    if(!StringUtils.isEmpty(checkoutloginOption)) {
      // if anonymous customer
      if(checkoutloginOption.equals(CONTINUE_ANONYMOUS)) {
        emailAddress = getAnonymousEmailAddress();
      } 
      // if new customer
      else if(checkoutloginOption.equals(CREATE_NEW_USER)) {
        emailAddress = getNewCustomerEmailAddress();
      }
    }
    
    return emailAddress;
  }
  
  /**
   * Checks if user is not exists, i.e. <code>checkoutLoginOption</code>
   * equals <code>CREATE_NEW_USER</code> or <code>CONTINUE_ANONYMOUS</code>
   * 
   * @return true if user is new or anonymous, false - otherwise.
   */
  public boolean isNewUser() {
    String checkoutLoginOption = getCheckoutLoginOption();

    return ((checkoutLoginOption != null) &&
        (checkoutLoginOption.equals(CREATE_NEW_USER) || checkoutLoginOption.equals(CONTINUE_ANONYMOUS)));
  }
  
  //---------------------------------------
  // handleLogoutUser related methods 
  //---------------------------------------
  
  /**
   * Operation called just after the user is logged out.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   */
  public void postLogoutUser(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    if (isLoggingDebug()) {
      logDebug("User status before postLogout: " + getProfile().getPropertyValue("securityStatus"));
      logDebug("User id before postLogout: " + getProfile().getRepositoryId());
      logDebug("Order id before postLogout: " + getOrder().getId());
      logDebug("before: " + getLogoutSuccessURL());
    }

    String portalLogoutSuccessURL = pRequest.getParameter(PARAM_PORTAL_LOGOUT_URL);

    if (portalLogoutSuccessURL != null) {
      setLogoutSuccessURL(portalLogoutSuccessURL);
    }

    super.postLogoutUser(pRequest, pResponse);

    if (isLoggingDebug()) {
      logDebug("after: " + getLogoutSuccessURL());
      logDebug("User status after postLogout: " + getProfile().getPropertyValue("securityStatus"));
      logDebug("User id after postLogout: " + getProfile().getRepositoryId());
      logDebug("Order id after postLogout: " + getOrder().getId());
    }
  }
  
  //---------------------------------------
  // handleCreateUser related methods 
  //---------------------------------------

  /**
   * Operation called just before the user creation process is started.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   */
  protected void preCreateUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    super.preCreateUser(pRequest, pResponse);

    //validation
    validatePassword(getStringValueProperty( getStorePropertyManager().getPasswordPropertyName()), pRequest,pResponse);
    validateEmail(pRequest);

    // update properties that didn't update directly
    updateDateOfBirthProperty(pRequest);
    updateReceiveEmailProperty();
  }

  /**
   * Validates email address
   * 
   * @param pRequest
   * @param propertyManager
   */
  private void validateEmail(DynamoHttpServletRequest pRequest) {
    String email = getStringValueProperty(getStorePropertyManager().getEmailAddressPropertyName());
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    
    if (StringUtils.isBlank(email) || !profileTools.validateEmailAddress(email)) {
      generateFormException(MSG_INVALID_EMAIL, getAbsoluteName(), pRequest);
    }
  }

  /**
   * Updates 'receiveEmail' profile property.
   */
  private void updateReceiveEmailProperty() {
    StorePropertyManager propertyManager = getStorePropertyManager();
    setValueProperty(propertyManager.getReceiveEmailPropertyName(), YES);
    
    if(isEmailOptIn()) {
      setValueProperty(propertyManager.getReceivePromoEmailPropertyName(), YES);
    } else {
      setValueProperty(propertyManager.getReceivePromoEmailPropertyName(), NO);
    }
  }
 
  /**
   * Override OOTB method so that if the profile has receiveEmail=yes, create
   * an EmailRecipient repository item for Email Campaigns.
   * {@inheritDoc}
   *
   * @see atg.userprofiling.ProfileForm#postCreateUser(atg.servlet.DynamoHttpServletRequest,
   *                                                   atg.servlet.DynamoHttpServletResponse)
   */
  protected void postCreateUser(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {

    Profile profile = getProfile();
    StorePropertyManager propertyManager = getStorePropertyManager();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

    // if the Profile's receiveEmail property is true, create an
    // EmailRecipient
    if (isEmailOptIn()) {
      if (isLoggingDebug()) {
        logDebug("User has opted to receive email");
      }

      String email = (String) profile.getPropertyValue(propertyManager.getEmailAddressPropertyName());
      try {
        profileTools.createEmailRecipient(profile, email, getSourceCode());
      } catch (RepositoryException repositoryExc) {
        generateFormException(MSG_ERR_CREATING_EMAIL_RECIPIENT, repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMajor(""), repositoryExc);
        }
      } //try
    }
    
    super.postCreateUser(pRequest, pResponse);

    if (!getFormError())
    {
      String redirectUrl = (String) getSessionBean().getValues().get(SessionBean.REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME);
      if (redirectUrl != null)
      {
        setCreateSuccessURL(redirectUrl);
      }
      
      postLoginAddItemToGiftlist();
    }
  }
  
  private void clearLoginPropertiesFromSession()
  {
    getSessionBean().getValues().remove(SessionBean.REDIRECT_AFTER_LOGIN_URL_PROPERTY_NAME);
    getSessionBean().getValues().remove(SessionBean.SKU_ID_TO_GIFTLIST_PROPERTY_NAME);
    getSessionBean().getValues().remove(SessionBean.PRODUCT_ID_TO_GIFTLIST_PROPERTY_NAME);
    getSessionBean().getValues().remove(SessionBean.GIFTLIST_ID_PROPERTY_NAME);
    getSessionBean().getValues().remove(SessionBean.QUANTITY_TO_ADD_TO_GIFTLIST_PROPERTY_NAME);
  }
  
  /**
   * Override OOTB method so that the user's email addres is copied over to
   * their login field.
   * {@inheritDoc}
   * @see atg.userprofiling.ProfileForm#preCreateUser(atg.servlet.DynamoHttpServletRequest,
   *      atg.servlet.DynamoHttpServletResponse)
   */
  public RepositoryItem createUser(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

    // Copy over user email to their login field
    StorePropertyManager propertyManager = (StorePropertyManager) profileTools.getPropertyManager();
    String emailPropertyName = propertyManager.getEmailAddressPropertyName();
    String email = getStringValueProperty(emailPropertyName);
    
    // Store login in lower case to support case-insensitive logins
    String loginPropertyName = propertyManager.getLoginPropertyName();
    setValueProperty(loginPropertyName, email.toLowerCase());

    return super.createUser(pRequest, pResponse);
  }
  
  //---------------------------------------
  // handleUpdateUser related methods 
  //---------------------------------------

  /**
   * This overriding method will check to see if a new email address that is
   * being submitted in an update already belongs to another user profile.
   * {@inheritDoc}
   *
   * @see atg.userprofiling.ProfileForm#preUpdateUser(atg.servlet.DynamoHttpServletRequest,
   *                                                  atg.servlet.DynamoHttpServletResponse)
   */
  protected void preUpdateUser(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    StorePropertyManager propertyManager = getStorePropertyManager();
    String newLogin = ((String) getValue().get(propertyManager.getEmailAddressPropertyName())).trim();
    String oldLogin = ((String) getProfile().getPropertyValue(propertyManager.getLoginPropertyName())).trim();

    MutableRepository repository = getProfileTools().getProfileRepository();
    
    //validate the newly provided emailAddress/Login
    //May need to change in future if we ever support separate Login & AlternateEmailAddress for Profile
    if (StringUtils.isBlank(newLogin)
        || !((StoreProfileTools) getProfileTools()).validateEmailAddress(newLogin)) {
      generateFormException(MSG_INVALID_EMAIL, getAbsoluteName(), pRequest);
    }

    try {
      //if login is new check it for duplication
      if(!(oldLogin.equalsIgnoreCase(newLogin))){
        
        // throw a form exception of login exists and if it does
        // not belong to the current profile
        if (userAlreadyExists(newLogin, repository, pRequest, pResponse) ) {
          generateFormException(MSG_DUPLICATE_USER, getAbsoluteName(),pRequest);          
        }
      }
      
    } catch (RepositoryException exc) {
      logError(LogUtils.formatMajor(MSG_DUPLICATE_USER));
    }
    
    //validating updated date of birth
    updateDateOfBirthProperty(pRequest);
    updateReceiveEmailProperty();

    super.preUpdateUser(pRequest, pResponse);
  }

  private void updateDateOfBirthProperty(DynamoHttpServletRequest pRequest) {
    if (!StringUtils.isBlank(getDateOfBirth())) {
      Date validDate = validateDateFormat(getDateOfBirth(), getDateFormat());

      if (validDate == null) {
        generateFormException(MSG_INVALID_DATE_FORMAT, getAbsoluteName(), pRequest);
      } else {
        setValueProperty(getStorePropertyManager().getDateOfBirthPropertyName(), validDate);
      }
    } else {
      setValueProperty(getStorePropertyManager().getDateOfBirthPropertyName(), null);
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see atg.userprofiling.ProfileForm#postUpdateUser(atg.servlet.DynamoHttpServletRequest,
   *                                                   atg.servlet.DynamoHttpServletResponse)
   */
  protected void postUpdateUser(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    Profile profile = getProfile();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    StorePropertyManager propertyManager = getStorePropertyManager();

    //Capture new and old Logins before updaing the Login with new email address
    String newLogin = ((String) getValue().get(propertyManager.getEmailAddressPropertyName())).trim();
    String oldLogin = ((String) profile.getPropertyValue(propertyManager.getLoginPropertyName())).trim();

    // Copy over updated email address to login
    profileTools.copyEmailToLogin(profile);

    //Check if emailOptIn status has changed
    if (isPreviousOptInStatus() != isEmailOptIn()) {
      // Email optIn/Out preference has changed
      if (isEmailOptIn()) {
        // User has chosen to opt in
        // In both cases create new emailRecipient with new email address ('newLogin')
        // If user has changed his email, then 'newLogin' is the updated email address
        // If user has not changed his email, then 'oldLogin' is same as 'newLogin'
        if (isLoggingDebug()) {
          logDebug("User has opted-in to subscribe to email while updating his profile");
        }

        //create Email Recipient
        try {

          profileTools.createEmailRecipient(profile, newLogin, getSourceCode());

        } catch (RepositoryException repositoryExc) {
          generateFormException(MSG_ERR_CREATING_EMAIL_RECIPIENT, repositoryExc, pRequest);

          if (isLoggingError()) {
            logError(LogUtils.formatMajor(""), repositoryExc);
          }
        } //try

      } else {
        // User has opted out. In both cases - whether user has changed/not changed his email
        // un-subscribe old email address of shopper from receiving emails
        if (isLoggingDebug()) {
          logDebug("User opted-out to unsubscribe email while updating his profile");
        }

        try {
          profileTools.removeEmailRecipient(oldLogin);
        } catch (RepositoryException repositoryExc) {
          generateFormException(MSG_ERR_REMOVING_EMAIL_RECIPIENT, repositoryExc, pRequest);

          if (isLoggingError()) {
            logError(LogUtils.formatMajor(""), repositoryExc);
          }
        } //try        
      }
    } else {
      // Email OptIn/Out preference has not changed
      // Check if user has changed emailAddress while updating profile
      if (!(oldLogin.equalsIgnoreCase(newLogin))) {
        if (isLoggingDebug()) {
          logDebug("User has changed his email, updating the registered emaill address");
        }
        try {
          profileTools.updateEmailRecipient(profile, oldLogin, newLogin, getSourceCode());
        } catch (RepositoryException repositoryExc) {
          generateFormException(MSG_ERR_UPDATING_EMAIL_RECIPIENT, repositoryExc, pRequest);

          if (isLoggingError()) {
            logError(LogUtils.formatMajor(""), repositoryExc);
          }
        } //try       
      } else {
        // Do nothing - neither opt-in/out status has changed nor email address has changed
      }
    }

    super.postUpdateUser(pRequest, pResponse);
  }

 
  
  /**
   * Operation called to validate the Date on the basis of locale format
   *
   * @param pDate
   *            User Date
   * @param pFormat
   *            the Date Format
   * @return date from string
   */
  protected Date validateDateFormat(String pDate, String pFormat) {
    DateFormat df = new SimpleDateFormat(pFormat.trim());
    Date d;

    try {
      df.setLenient(false);
      d = df.parse(pDate);
    } catch (ParseException e) {
      return null;
    }

    return d;
  }
  
  
  //---------------------------------------
  // handleChangePassword related methods 
  //---------------------------------------

  /**
   * Override the OOTB method to that a password length check is performed.
   * {@inheritDoc}
   *
   * @see atg.userprofiling.ProfileForm#preChangePassword(atg.servlet.DynamoHttpServletRequest,
   *                                                      atg.servlet.DynamoHttpServletResponse)
   */
  protected void preChangePassword(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    super.preChangePassword(pRequest, pResponse);

    StorePropertyManager propertyManager = getStorePropertyManager();
    String password = getStringValueProperty(propertyManager.getPasswordPropertyName());
    //validate password
    validatePassword(password, pRequest, pResponse);
  }
  
  /**
   * Validates password. If password is not valid adds form exception.
   * 
   * @param pPassword password to check
   * @param pRequest http request
   * @param pResponse http response
   * @return true if password
   */
  protected boolean validatePassword(String pPassword, DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse ) {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();    
    if (pPassword.length() != 0) {
      if (!(profileTools.isValidPasswordLength(pPassword))) {
        generateFormException(MSG_INVALID_PASSWORD_LENGTH, getAbsoluteName(), pRequest);
        return false;
      }
    }
    return true;
  }
  

  //---------------------------------------
  // handleCreateNewCreditCard and related methods
  //---------------------------------------
  
  /**
   * Creates a new credit card using the entries entered in the editValue map
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @return true if success, false - otherwise
   */
  public boolean handleCreateNewCreditCardAndAddress(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {

    // validate credit card information
    if (!validateCreateCreditCardInformation(pRequest, pResponse, true)) {
      return checkFormRedirect(null, getCreateCardErrorURL(), pRequest, pResponse);
    }

    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StorePropertyManager propertyManager = getStorePropertyManager();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Profile profile = getProfile();

    // Get editValue map, containing the credit card properties
    HashMap newCard = (HashMap) getEditValue();
    HashMap newAddress = (HashMap) getBillAddrValue();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      String secondaryAddrNickname = (String) newAddress.get(getShippingAddressNicknameMapKey());

      // Get credit card's nickname
      String cardNickname = (String) newCard.get(propertyManager.getCreditCardNicknamePropertyName());

      try {
        // Create credit card and add to profile
        cardNickname = profileTools.createProfileCreditCard(profile, newCard, cardNickname,
                                                            newAddress, secondaryAddrNickname,
                                                            true);

        String newCreditCard = (String) newCard.get(propertyManager.getNewCreditCard());
        
        if(!StringUtils.isEmpty(newCreditCard) && newCreditCard.equalsIgnoreCase("true")) {
          profileTools.setDefaultCreditCard(profile, cardNickname);
        }


        // empty out the map
        newCard.clear();
        newAddress.clear();
      } catch (RepositoryException repositoryExc) {
        generateFormException(MSG_ERR_CREATING_CC, repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMajor(repositoryExc.getMessage()), repositoryExc);
        }
      } catch (InstantiationException ex) {
        throw new ServletException(ex);
      } catch (IllegalAccessException ex) {
        throw new ServletException(ex);
      } catch (ClassNotFoundException ex) {
        throw new ServletException(ex);
      } catch (IntrospectionException ex) {
        throw new ServletException(ex);
      } catch (PropertyNotFoundException ex) {
        throw new ServletException(ex);
      }

      return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
      }
    }
  }
  
  /**
   * Creates a new credit card using the entries entered in the editValue map
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @return true if success, false - otherwise
   */
  public boolean handleCreateNewCreditCard(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {

    // validate credit card information
    if (!validateCreateCreditCardInformation(pRequest, pResponse, false)) {
      return checkFormRedirect(null, getCreateCardErrorURL(), pRequest, pResponse);
    }

    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StorePropertyManager propertyManager = getStorePropertyManager();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Profile profile = getProfile();

    // Get editValue map, containing the credit card properties
    HashMap newCard = (HashMap) getEditValue();
    HashMap newAddress = (HashMap) getBillAddrValue();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Check to see if user is selecting existing address
      String secondaryAddrNickname = (String) newAddress.get(getNewNicknameValueMapKey());

      // Get credit card's nickname
      String cardNickname = (String) newCard.get(propertyManager.getCreditCardNicknamePropertyName());

      try {
        // Create credit card and add to profile
        cardNickname = profileTools.createProfileCreditCard(profile, newCard, cardNickname,
                                                            newAddress, secondaryAddrNickname,
                                                            false);

        String newCreditCard = (String) newCard.get(propertyManager.getNewCreditCard());
        
        if(!StringUtils.isEmpty(newCreditCard) && newCreditCard.equalsIgnoreCase("true")) {
          profileTools.setDefaultCreditCard(profile, cardNickname);
        }

        // empty out the map
        newCard.clear();
        newAddress.clear();
      } catch (RepositoryException repositoryExc) {
        generateFormException(MSG_ERR_CREATING_CC, repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMajor(repositoryExc.getMessage()), repositoryExc);
        }
      } catch (InstantiationException ex) {
        throw new ServletException(ex);
      } catch (IllegalAccessException ex) {
        throw new ServletException(ex);
      } catch (ClassNotFoundException ex) {
        throw new ServletException(ex);
      } catch (IntrospectionException ex) {
        throw new ServletException(ex);
      } catch (PropertyNotFoundException ex) {
        throw new ServletException(ex);
      }

      return checkFormRedirect(getCreateCardSuccessURL(), getCreateCardErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
      }
    }
  }
  
  /**
   * Validates credit card information entered by user:
   * <ul>
   *  <li>all required fields are specified for credit card
   *  <li>all required fields are specified for new address
   *  <li>country/state combination is valid for new address
   *  <li>card number and expiry date are valid
   *  <li>not duplicate credit card nickname is used
   * </ul>
   * @param pRequest http request
   * @param pResponse http response
   * @param pIsNewAddress true if should validate new address 
   * 
   * @return true is validation succeeded
   * @throws ServletException
   * @throws IOException
   */
  protected boolean validateCreateCreditCardInformation(DynamoHttpServletRequest pRequest,
                                                        DynamoHttpServletResponse pResponse, 
                                                        boolean pIsNewAddress)
      throws ServletException, IOException {
    
    // return false if there were missing required properties
    if (getFormError()) {
      return false;
    }
    
    HashMap newCard = (HashMap) getEditValue();
    HashMap newAddress = (HashMap) getBillAddrValue();
    String shippingAddrNickname = (String) newAddress.get(getShippingAddressNicknameMapKey());

    //validate credit card fields
    validateCreditCardFields(pRequest, pResponse);

    // if new address should be created validate all address properties
    // and country/state combination
    if (pIsNewAddress) {
      validateBillingAddressFields(pRequest, pResponse);
      validateCountryStateCombination(newAddress, pRequest, pResponse);
    }

    if (getFormError()) {
      return false;
    }

    // Verify that card number and expiration date are valid
    ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
    if (!validateCreditCard(newCard, bundle)) {
      return false;
    }

    // Check that the nickname is not already used for a credit card
    StorePropertyManager propertyManager = getStorePropertyManager();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Profile profile = getProfile();
    String cardNickname = (String) newCard.get(propertyManager.getCreditCardNicknamePropertyName());
    if (profileTools.isDuplicateCreditCardNickname(profile, cardNickname)) {
      generateFormException(MSG_DUPLICATE_CC_NICKNAME, new String[] { cardNickname },
                            getAbsoluteName()+ "editValue.creditCardNickname", pRequest);
      return false;
    }
    return true;
  }
  
  /**
   * Validates that all required credit card's fields are entered by user
   * 
   * @param pRequest http request
   * @param pResponse http response
   * @return true if all required fields are entered
   */
  protected boolean validateCreditCardFields(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse) {
    Map newCard = getEditValue();
    String[] cardProps = getCardProperties();
    StorePropertyManager propertyManager = getStorePropertyManager();

    boolean missingFields = false;
    Object property = null;
    String propertyName = null;

    // Verify all required fields entered for credit card
    for (int i = 0; i < cardProps.length; i++) {
      propertyName = cardProps[i];
      
      //not check here billingAddress Property
      if (propertyName.equals(propertyManager.getCreditCardBillingAddressPropertyName()))
        continue;
      property = newCard.get(propertyName);

      if (StringUtils.isBlank((String) property)) {
        ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
        String[] args = new String[]{bundle.getString(propertyName)};
        generateFormException(MSG_MISSING_CC_PROPERTY, args, getAbsoluteName(), pRequest);
        missingFields = true;
      }
    }
    return !missingFields;
  }
  
  /**
   * Validates that all required fields are entered for billing address
   * 
   * @param pRequest http request
   * @param pResponse http response
   * @return true if all required fields are entered
   */
  protected boolean validateBillingAddressFields(DynamoHttpServletRequest pRequest,
                                                 DynamoHttpServletResponse pResponse) {
    Map newAddress = getBillAddrValue();
    Iterator addressPropertyIterator = getRequiredBillingAddressPropertyList().iterator();
    boolean missingFields = false;

    Object property = null;
    String propertyName = null;

    // Check to see all the address fields are entered
    while (addressPropertyIterator.hasNext()) {
      propertyName = (String) addressPropertyIterator.next();
      property = newAddress.get(propertyName);

      if (StringUtils.isBlank((String) property)) {
        ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
        String[] args = new String[]{bundle.getString(propertyName)};
        generateFormException(MSG_MISSING_CC_PROPERTY, args, getAbsoluteName(), pRequest);        
        missingFields = true;
      }
    }
    String billingAddressNickname = (String) newAddress.get(getShippingAddressNicknameMapKey());
    Map<String, Object> secondaryAddresses = (Map<String, Object>) getProfile().getPropertyValue(getStorePropertyManager().getSecondaryAddressPropertyName());
    boolean duplicateNickname = secondaryAddresses.keySet().contains(billingAddressNickname);
    if (duplicateNickname)
    {
      generateFormException(MSG_DUPLICATE_ADDRESS_NICKNAME, new String[] {billingAddressNickname}, getAbsoluteName(), pRequest);
    }
    return !(missingFields || duplicateNickname);
  }
  
  /**
   * Validates the credit card information using CreditCardTools.
   * {@inheritDoc}
   * @see CreditCardTools#verifyCreditCard(CreditCardInfo)
   */
  protected boolean validateCreditCard(HashMap pCard, ResourceBundle pBundle) {
    StorePropertyManager propertyManager = getStorePropertyManager();
    ExtendableCreditCardTools cardTools = getCreditCardTools();
    
    BasicStoreCreditCardInfo ccInfo = new BasicStoreCreditCardInfo();
    ccInfo.setExpirationYear((String) pCard.get(propertyManager.getCreditCardExpirationYearPropertyName()));
    ccInfo.setExpirationMonth((String) pCard.get(propertyManager.getCreditCardExpirationMonthPropertyName()));

    String ccNumber = (String) pCard.get(propertyManager.getCreditCardNumberPropertyName());

    if (ccNumber != null) {
      ccNumber = StringUtils.removeWhiteSpace(ccNumber);
    }

    ccInfo.setCreditCardNumber(ccNumber);
    ccInfo.setCreditCardType((String) pCard.get(propertyManager.getCreditCardTypePropertyName()));

    int ccreturn = cardTools.verifyCreditCard(ccInfo);

    if (ccreturn != cardTools.SUCCESS) {
      String msg = "";
      if ((pBundle != null) && (pBundle.getLocale() != null)) { 
        msg = cardTools.getStatusCodeMessage(ccreturn, pBundle.getLocale());
      }
      else {
        msg = cardTools.getStatusCodeMessage(ccreturn);
      }
      addFormException(new DropletFormException(msg, null));

      return false;
    }

    return true;
  }
  
  //---------------------------------------
  // handleUpdateCard and related methods
  //---------------------------------------
  
  /**
   * Called before handleUpdateCard logic is applied. Adds properties listed in immutableCardProperties property to
   * editValue map.
   * @param pRequest current request
   * @param pResponse current response
   * @throws RepositoryException if unable to obtain user card's properties.
   */
  protected void preUpdateCard(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws RepositoryException
  {
    RepositoryItem cardToUpdate = findCurrentCreditCard();
    for (String propertyName: mImmutableCardProperties)
    {
      getEditValue().put(propertyName, cardToUpdate.getPropertyValue(propertyName));
    }
  }

  /**
   * Searches current user's credit card by nick-name from editValue properties. 
   * @return credit card if found.
   */
  private RepositoryItem findCurrentCreditCard()
  {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    String cardNickname = (String) getEditValue().get(getNicknameValueMapKey());
    RepositoryItem cardToUpdate = profileTools.getCreditCardByNickname(cardNickname, getProfile());
    return cardToUpdate;
  }
  
  /**
   * Updates the credit card as modified by the user.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @exception RepositoryException
   *                if there was an error accessing the repository
   * @return true if success, false - otherwise
   */
  public boolean handleUpdateCard(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse) throws RepositoryException,
      ServletException, IOException {
    preUpdateCard(pRequest, pResponse);
    
    // validate credit card information
    if (!validateUpdateCreditCardInformation(pRequest, pResponse)) {
      return checkFormRedirect(null, getUpdateCardErrorURL(), pRequest, pResponse);
    }

    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      Profile profile = getProfile();
      Map edit = getEditValue();
      Map billAddrValue = getBillAddrValue();

      String newNickname = (String) edit.get(getNewNicknameValueMapKey());

      try {

        if (isLoggingDebug()) {
          logDebug("Updating credit card properties");
        }
        
        // Get credit card to update
        RepositoryItem cardToUpdate = findCurrentCreditCard();
        
        // Update credit card
        profileTools.updateProfileCreditCard(cardToUpdate, profile, edit, newNickname,
                                             billAddrValue, profileTools.getBillingAddressClassName());

        //save this card as default if needed
        String newCreditCard = (String) edit.get(getStorePropertyManager().getNewCreditCard());
        if(!StringUtils.isEmpty(newCreditCard) && newCreditCard.equalsIgnoreCase("true")) {
          profileTools.setDefaultCreditCard(profile, newNickname);
        } else if ("false".equalsIgnoreCase(newCreditCard)) {
          //current card should not be default
          RepositoryItem defaultCreditCard = profileTools.getDefaultCreditCard(profile);
          if (defaultCreditCard != null && cardToUpdate.getRepositoryId().equals(defaultCreditCard.getRepositoryId())) {
            //current card is default, make it not to be
            profileTools.updateProperty(getStorePropertyManager().getDefaultCreditCardPropertyName(), null, profile);
            //otherwise we shouldn't change anything more in the profile
          }
        }
      } catch (RepositoryException repositoryExc) {
        generateFormException(MSG_ERR_UPDATING_CREDIT_CARD, repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMajor(""), repositoryExc);
        }

        return checkFormRedirect(null, getUpdateCardErrorURL(), pRequest, pResponse);
      } catch (InstantiationException ex) {
        throw new ServletException(ex);

      } catch (IllegalAccessException ex) {
        throw new ServletException(ex);

      } catch (ClassNotFoundException ex) {
        throw new ServletException(ex);
      } catch (IntrospectionException ex) {
        throw new ServletException(ex);
      }
      billAddrValue.clear();
      edit.clear();
      return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
      }
    }
  }
  
  /**
   * Validates updated credit card information entered by user:
   * <ul>
   *  <li>country/state combination is valid for billing address
   *  <li>card expiry date are valid
   *  <li>not duplicate credit card nickname is used
   * </ul>
   * @param pRequest http request
   * @param pResponse http response
   * 
   * @return true is validation succeeded
   * @throws ServletException
   * @throws IOException
   */
  protected boolean validateUpdateCreditCardInformation(DynamoHttpServletRequest pRequest,
                                                        DynamoHttpServletResponse pResponse)
      throws ServletException, IOException {
    
    // return false if there were missing required properties
    if (getFormError()) {
      return false;
    }
    
    HashMap card = (HashMap) getEditValue();
    HashMap billingAddress = (HashMap) getBillAddrValue();
    String nickname = ((String) card.get(getNicknameValueMapKey())).trim();

    //Validate billing address fields
    validateBillingAddressFields(pRequest, pResponse);
    validateCountryStateCombination(billingAddress, pRequest, pResponse);

    if (getFormError()) {
      return false;
    }
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    Profile profile = getProfile();
    StorePropertyManager propertyManager = getStorePropertyManager();

    // Verify that card expiry date is valid
    RepositoryItem cardToUpdate = profileTools.getCreditCardByNickname(nickname, profile);
    
    // only expiry date is needed to be validated because credit card type and number
    // are not updated. But we need to set credit card type and number to map because 
    // they are needed for expiry date validation
    card.put(propertyManager.getCreditCardNumberPropertyName(),
             cardToUpdate.getPropertyValue(propertyManager.getCreditCardNumberPropertyName()));
    card.put(propertyManager.getCreditCardTypePropertyName(),
             cardToUpdate.getPropertyValue(propertyManager.getCreditCardTypePropertyName()));

    ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
    if (!validateCreditCard(card, bundle)) {
      return false;
    }

    // Check that the new  nickname is not already used for a credit card
    String newNickname = ((String) card.get(getNewNicknameValueMapKey())).trim();
    if (!StringUtils.isBlank(newNickname) && !newNickname.equals(nickname)) {
      if (profileTools.isDuplicateCreditCardNickname(profile, newNickname)) {
        generateFormException(MSG_DUPLICATE_CC_NICKNAME, new String[] { newNickname },
                              getAbsoluteName() + "editValue.creditCardNickname", pRequest);

        return false;
      }
    }

    return true;
  }
  
  //---------------------------------------
  // handleRemoveCard
  //--------------------------------------- 
 
  /**
   * Removes specified in <code>removeCard</code> property credit card
   * from user's credit cards map.
   * 
   * @param pRequest http request
   * @param pResponse http response
   * @return true if success, false - otherwise
   * @throws ServletException
   * @throws IOException
   */
  public boolean handleRemoveCard(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    
    String cardNickname = getRemoveCard();
    if (StringUtils.isBlank(cardNickname)) {
      if (isLoggingDebug()) {
        logDebug("A null or empty nickname was provided to handleRemoveAddress");
      }

      // if no nickname provided, do nothing.
      return true;
    }

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }
      Profile profile = getProfile();

      // remove credit card from user's credit cards map
      profileTools.removeProfileCreditCard(profile, cardNickname);

      return true;
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } catch (RepositoryException repositoryExc) {
      logError(LogUtils.formatMajor(""), repositoryExc);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
      }
    }

    return false;
  }
  
  //---------------------------------------
  // handleDefaultCard 
  //---------------------------------------

  /**
   * Makes the credit card identified by a nickname, default in the given
   * profile.
   * @deprecated
   * @param pRequest
   *            DynamoHttpServletRequest
   * @param pResponse
   *            DynamoHttpServletResponse
   * @return boolean true/false for success
   * @throws RepositoryException
   *             if there was an error accessing the repository
   * @throws ServletException
   *             if there was an error while executing the code
   * @throws IOException
   *             if there was an error with servlet io
   */
  public boolean handleDefaultCard(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse) throws RepositoryException,
      ServletException, IOException {
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Get nickname of the credit card that should be done default
      String targetCard = getDefaultCard();

      if (StringUtils.isBlank(targetCard)) {

        if (isLoggingDebug()) {
          logDebug("A null or empty nickname was provided to handleDefaultCard");
        }

        // if no nickname provided, do nothing.
        return true;
      }

      Profile profile = getProfile();
      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
      profileTools.setDefaultCreditCard(profile, targetCard);

      // Success; redirect if required to do so following success
      return checkFormRedirect(getRemoveCardSuccessURL(), getRemoveCardErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
      }
    }
  }
  
  //---------------------------------------
  // handleEditCard 
  //---------------------------------------

  /**
   * Copy the named credit card into the editValue map, allowing the user
   * to edit them.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @return true if success, false - otherwise
   */
  public boolean handleEditCard(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StorePropertyManager propertyManager = getStorePropertyManager();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // the nickname of the credit card we want to edit
      String targetCard = getEditCard();

      if ((targetCard == null) || (targetCard.trim().length() == 0)) {
        // we should only get here through a hyperlink that supplies the
        // secondary credit card nickname. Just in case, exit quietly.
        return true;
      }

      if (!getFormError()) {
        Profile profile = getProfile();
        Map creditCards = (Map) profile.getPropertyValue(propertyManager.getCreditCardPropertyName());
        MutableRepositoryItem card = (MutableRepositoryItem) creditCards.get(targetCard);
        MutableRepositoryItem cardAddress = (MutableRepositoryItem) card.getPropertyValue(propertyManager.getBillingAddressPropertyName());

        Map edit = getEditValue();
        Map billAddrMap = getBillAddrValue();

        // record the nickname 
        edit.put(getNicknameValueMapKey(), targetCard);

        edit.put(getNewNicknameValueMapKey(), targetCard);

        String[] cardProps = getCardProperties();

        // copy each property to the map
        Object property;

        for (int i = 0; i < cardProps.length; i++) {
          property = card.getPropertyValue(cardProps[i]);

          if (property != null) {
            edit.put(cardProps[i], property);
          }
        }

        // now copy billing address properties
        property = null;

        String[] addressProps = getAddressProperties();

        for (int i = 0; i < addressProps.length; i++) {
          property = cardAddress.getPropertyValue(addressProps[i]);

          if (property != null) {
            billAddrMap.put(addressProps[i], property);
          }
        }
      }

      return checkFormRedirect(getUpdateCardSuccessURL(), getUpdateCardErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
      }
    }
  }

  //---------------------------------------
  // handleNewAddress and related methods 
  //---------------------------------------

  /**
   * Creates a new shipping address using the entries entered in the editValue
   * map. The address will be indexed using the nickname provided by the user.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @return boolean returns true/false for success
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @throws IntrospectionException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public boolean handleNewAddress(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    //Validate address data entered by user
    if (!validateAddress(pRequest, pResponse)) {
      return checkFormRedirect(null, getNewAddressErrorURL(), pRequest, pResponse);
    }
    
    // Profile
    Profile profile = getProfile();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    
    // Get editValue map, containing the user form data
    HashMap newAddress = (HashMap) getEditValue();

    // Generate unique nickname if it is not provided by user
    String nickname = null;
    nickname = (String) newAddress.get(getNicknameValueMapKey());
    if (StringUtils.isBlank(nickname)) {
      nickname = profileTools.getUniqueShippingAddressNickname(newAddress, profile, null);
    }
        
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      try {
        // Populate Address object with new address properties
        Address addressObject = AddressTools.createAddressFromMap(newAddress,
                                                                  profileTools.getShippingAddressClassName());

        //Create secondary address repository item
        profileTools.createProfileRepositorySecondaryAddress(profile, nickname, addressObject);

        // Check to see Profile.shippingAddress is null, if it is,
        // add the new address as the default shipping address
        if(isUseShippingAddressAsDefault()){
          profileTools.setDefaultShippingAddress(profile, nickname);
        }

        // empty out the map
        newAddress.clear();

      } catch (RepositoryException repositoryExc) {
          generateFormException(MSG_ERR_CREATING_ADDRESS, new String[] { nickname },
                                repositoryExc, pRequest);       
  
          if (isLoggingError()) {
            logError(LogUtils.formatMajor(""), repositoryExc);
          }
  
          // failure - redirect if an error URL was specified
          return checkFormRedirect(null, getNewAddressErrorURL(), pRequest, pResponse);
      } catch (InstantiationException ex) {
        throw new ServletException(ex);
      } catch (IllegalAccessException ex) {
        throw new ServletException(ex);
      } catch (ClassNotFoundException ex) {
        throw new ServletException(ex);
      } catch (IntrospectionException ex) {
        throw new ServletException(ex);
      }

      // Success; redirect if required to do so following success
      return checkFormRedirect(getNewAddressSuccessURL(), getNewAddressErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
      }
    }
  }
  
  /**
   * Validates new address fields entered by user:
   * <ul>
   *  <li>all required fields are specified for new address
   *  <li>country/state combination is valid for new address
   *  <li>not duplicate address nickname is used for create address or update
   *      address operation
   * </ul>
   * @param pRequest http request
   * @param pResponse http response
   * 
   * @return true is validation succeeded
   * @throws ServletException
   * @throws IOException
   */
  protected boolean validateAddress(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    // return false if there were missing required properties
    if (getFormError()) {
      return false;
    }

    // Validate country/state combination
    HashMap newAddress = (HashMap) getEditValue();
    validateCountryStateCombination(newAddress, pRequest, pResponse);

    if (getFormError()) {
      return false;
    }

    // Validate address nickname or new nickname if it's  update address operation
    String nickname = (String) newAddress.get(getNicknameValueMapKey());
    String newNickname = (String) newAddress.get(getNewNicknameValueMapKey());
    if (!StringUtils.isBlank(newNickname)) {
      
      // It's not new address but update address operation
      // So validate only new nickname 
      if (!newNickname.equals(nickname)) {
        List ignoreList = new ArrayList();
        ignoreList.add(nickname);
        checkForDuplicateAddress(pRequest, pResponse, newNickname, ignoreList);
      }
    } else {
      
      //It's new address so validate nickname
      if (!StringUtils.isBlank(nickname)) {
        checkForDuplicateAddress(pRequest, pResponse, nickname, null);
      }
    }
    if (getFormError()) {
      return false;
    }
    
    //all validation passed successfully so return true
    return true;
  }
  
  /**
   * Validate country-state combination.
   *
   * @param pAddress - address
   * @param pRequest - http address
   * @param pResponse - http response
   */
  protected void validateCountryStateCombination(Map pAddress, DynamoHttpServletRequest pRequest,
                                                 DynamoHttpServletResponse pResponse) {
    if (pAddress != null) {
      CommercePropertyManager propertyManager = getStorePropertyManager();
      validateCountryStateCombination((String)pAddress.get(propertyManager.getAddressCountryPropertyName()),
          (String)pAddress.get(propertyManager.getAddressStatePropertyName()), pRequest, pResponse);
    }
  }

  /**
   * Validate country-state combination.
   * @param pCountry country code
   * @param pState county code
   * @param pRequest dynamo request
   * @param pResponse dynamo response
   */
  protected void validateCountryStateCombination(String pCountry, String pState, DynamoHttpServletRequest pRequest,
                                                 DynamoHttpServletResponse pResponse) {
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    if (!profileTools.isValidCountryStateCombination(pCountry, pState)) {
      Object[] args = new Object[] {LocaleUtils.constructLocale("en_" + pCountry).getDisplayCountry(getRequestLocale().getLocale())};
      generateFormException(MSG_STATE_IS_INCORRECT, args, getAbsoluteName(), pRequest);      
    }
  }
  
  /**
   * Checks if the given nickname already exists in the secondary address map. 
   * If it does a form error is added.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   * @param pProfile - profile
   * @param pNickname - nickname
   * @param pIgnoredNames - names we wont check for duplicates
   */
  protected void checkForDuplicateAddress(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse,
                                          String pNickname, 
                                          List<String> pIgnoredNames) 
  {
    // Get the current nicknames
    Profile profile = getProfile();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    List<String> profileNickNames = new ArrayList<String>();
    profileNickNames.addAll(profileTools.getProfileAddressNames(profile));
    
    // Remove the names we want to ignore
    if(pIgnoredNames != null){
      profileNickNames.removeAll(pIgnoredNames);
    }
    
    // Check for duplicates
    for(String profileNickName : profileNickNames){
      if(profileNickName.equalsIgnoreCase(pNickname)){
        generateFormException(MSG_DUPLICATE_ADDRESS_NICKNAME, new String[] { pNickname }, 
            getAbsoluteName(), pRequest);
        return;
      }
    }
  }
  
  //---------------------------------------
  // handleUpdateAddress 
  //---------------------------------------
  
  /**
   * Update the secondary address as modified by the user.
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   * @exception RepositoryException
   *                if there was an error accessing the repository
   * @return true for successful address update, false - otherwise
   */
  public boolean handleUpdateAddress(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
      throws RepositoryException, ServletException, IOException {

    //Validate address data entered by user
    if (!validateAddress(pRequest, pResponse)) {
      return checkFormRedirect(null, getUpdateAddressErrorURL(), pRequest, pResponse);
    }
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      Profile profile = getProfile();
      
      Map edit = getEditValue();
      String nickname = (String) edit.get(getNicknameValueMapKey());
      String newNickname = ((String) edit.get(getNewNicknameValueMapKey()));

      try {
        
        //Populate Address object data entered by user
        Address addressObject = AddressTools.createAddressFromMap(edit,
                                                                  profileTools.getShippingAddressClassName());
        // Get address repository item to be updated
        RepositoryItem oldAddress = profileTools.getProfileAddress(profile, nickname);
        
        // Update address repository item 
        profileTools.updateProfileRepositoryAddress(oldAddress, addressObject);
        
        if (!StringUtils.isBlank(newNickname)) {
          // Change nickname
          if(!newNickname.equalsIgnoreCase(nickname)){
            profileTools.changeSecondaryAddressName(profile, nickname, newNickname);
          }
          // Update nickname
          else{
            updateSecondaryAddressName(profile, nickname, newNickname);
          }
        }
        
        if(isUseShippingAddressAsDefault()){
          ((StoreProfileTools) getProfileTools()).setDefaultShippingAddress(profile, newNickname);
        } else if (newNickname.equalsIgnoreCase(profileTools.getDefaultShippingAddressNickname(profile))) {
          profileTools.setDefaultShippingAddress(profile, null);
        }
        
        // update secondary properties of the address in the order (e.g phone num)
        Order currentOrder = getShoppingCart().getCurrent();        
        if(currentOrder != null){
          List shippingGroupList = currentOrder.getShippingGroups();
          for(Object shippingGroup : shippingGroupList){
            if(shippingGroup instanceof HardgoodShippingGroup){
              Address orderAddress = ((HardgoodShippingGroup)shippingGroup).getShippingAddress();
              if(StoreAddressTools.compare(addressObject, orderAddress)){
                  updateSecondaryInfo((ContactInfo)orderAddress,
                                      (ContactInfo)addressObject);
              }
            }
          }
        }
      } catch (RepositoryException repositoryExc) {
        generateFormException(MSG_ERR_UPDATING_ADDRESS, repositoryExc, pRequest);

        if (isLoggingError()) {
          logError(LogUtils.formatMajor(""), repositoryExc);
        }

        return checkFormRedirect(null, getUpdateAddressErrorURL(), pRequest, pResponse);
      } catch (InstantiationException ex) {
        throw new ServletException(ex);
      } catch (IllegalAccessException ex) {
        throw new ServletException(ex);
      } catch (ClassNotFoundException ex) {
        throw new ServletException(ex);
      } catch (IntrospectionException ex) {
        throw new ServletException(ex);
      }

      edit.clear();
      return checkFormRedirect(getUpdateAddressSuccessURL(), getUpdateAddressErrorURL(), pRequest,
                               pResponse);
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
      }
    }
  }
  
  /**
   * Updates an address nickname
   * 
   * @param pProfile The users profile
   * @param pOldAddressName Old address name
   * @param pNewAddressName New address name
   */
  protected void updateSecondaryAddressName(RepositoryItem pProfile, String pOldAddressName,
      String pNewAddressName){
    
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
    CommercePropertyManager cpmgr = (CommercePropertyManager) profileTools.getPropertyManager();
    Map secondaryAddresses = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
    RepositoryItem address = profileTools.getProfileAddress(pProfile, pOldAddressName);
    if (address != null) {
      try {
        secondaryAddresses.remove(pOldAddressName);
        secondaryAddresses.put(pNewAddressName, address);
        profileTools.updateProperty(cpmgr.getSecondaryAddressPropertyName(), secondaryAddresses, pProfile);
      }
      catch (RepositoryException e){
        if(isLoggingError()){
          logError(e);
        }
      }
    }
  }
      
  /**
   * Updates all the properties of an address that don't effect where
   * the item is shipped to - e.g the phone number.
   * @param pTargetAddress The target Address
   * @param pSourceAddress The source Address
   */
  protected void updateSecondaryInfo(Address pTargetAddress, Address pSourceAddress){
    
    // Update secondary ContactInfo
    if((pTargetAddress instanceof ContactInfo) &&
       (pSourceAddress instanceof ContactInfo))
    {
      // Secondary Property Phone Number
      if(((ContactInfo)pTargetAddress).getPhoneNumber() != ((ContactInfo)pSourceAddress).getPhoneNumber())
      {
        ((ContactInfo)pTargetAddress).setPhoneNumber(((ContactInfo)pSourceAddress).getPhoneNumber());  
      }
    }
  }
  
  //---------------------------------------
  // handleRemoveAddress 
  //---------------------------------------
  
  /**
   * This handler deletes a secondary address named in the removeAddress
   * property. 
   *
   * @param pRequest
   *            the servlet's request
   * @param pResponse
   *            the servlet's response
   * @return boolean true/false for success
   * @exception ServletException
   *                if there was an error while executing the code
   * @exception IOException
   *                if there was an error with servlet io
   */
  public boolean handleRemoveAddress(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    preRemoveAddress(pRequest, pResponse);

    // Stop execution if we have form errors
    if (getFormError()) {
      return true;
    }
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      // Get nickaname of the address to be removed
      String nickname = getRemoveAddress();

      if ((nickname == null) || (nickname.trim().length() == 0)) {
        if (isLoggingDebug()) {
          logDebug("A null or empty nickname was provided to handleRemoveAddress");
        }

        // if no nickname provided, do nothing.
        return true;
      }

      Profile profile = getProfile();
      
      // Remove the Address from the Profile
      RepositoryItem purgeAddress = profileTools.getProfileAddress(profile, nickname);
      if(purgeAddress != null){
        profileTools.removeProfileRepositoryAddress(profile, nickname, true);
      }    

      // Get the shipping group id that contains the Address
      String shippingGroupId = null;
      Map shippingGroupMap = getShippingGroupMapContainer().getShippingGroupMap();
      if(shippingGroupMap != null){
        if(shippingGroupMap.containsKey(nickname)){
          shippingGroupId = ((ShippingGroup)(shippingGroupMap.get(nickname))).getId();
        }
      }
      
      // Remove the Address from the Order
      if(shippingGroupId != null){
        Order currentOrder = getOrder();
        if(currentOrder instanceof StoreOrderImpl){
          boolean purged = ((StoreOrderImpl)currentOrder).removeAddress(shippingGroupId);
          if(!purged){
            if(isLoggingDebug()){
              logDebug("The Address could not be removed from order " + currentOrder.getId());
            }
          }
        }
      }
      return true;
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } catch (RepositoryException repositoryExc) {
      logError(LogUtils.formatMajor(""), repositoryExc);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
      }
    }

    return false;
  }
  
  //---------------------------------------
  // handleDefaultShippingAddress 
  //---------------------------------------

  /**
   * This sets the default shipping address.
   * @deprecated
   * @param pRequest
   *            DynamoHttpServletRequest
   * @param pResponse
   *            DynamoHttpServletResponse
   * @return true for success, false - otherwise
   * @throws RepositoryException 
   */
  public boolean handleDefaultShippingAddress(DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse)
      throws RepositoryException, ServletException {
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();

    try {
      if (tm != null) {
        td.begin(tm, TransactionDemarcation.REQUIRED);
      }

      Profile profile = getProfile();
      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
      String addressNickname = getDefaultShippingAddress();

      if (StringUtils.isBlank(addressNickname)) {

        if (isLoggingDebug()) {
          logDebug("A null or empty nickname was provided to handleDefaultShippingAddress");
        }
        // if no nickname provided, do nothing.
        return true;
      }

      // Set requested shipping addres as default
      profileTools.setDefaultShippingAddress(profile, addressNickname);

      return true;
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null) {
          td.end();
        }
      } catch (TransactionDemarcationException e) {
      }
    }
  }

  //---------------------------------------
  // handleEditAddress 
  //---------------------------------------
 
  /**
   * Copy the named secondary address into the editValue map, allowing the
   * user to edit them.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleEditAddress(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse) throws ServletException,
      IOException {
    
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    StorePropertyManager propertyManager = getStorePropertyManager();

    try {
      if (tm != null)
        td.begin(tm, TransactionDemarcation.REQUIRED);

      String nickname = getEditAddress();
      if (nickname == null || nickname.trim().length() == 0) {
        
        // we should only get here through a hyperlink that supplies the
        // secondary address nickname.  Just in case, exit quietly.
        return true;
      }
      Profile profile = getProfile();
      Map secondaryAddress = (Map) profile.getPropertyValue(propertyManager.getSecondaryAddressPropertyName());
      MutableRepositoryItem theAddress = (MutableRepositoryItem) secondaryAddress.get(nickname);
      Map edit = getEditValue();

      // record the nickname for posterity (actually, the updateAddress handler)
      edit.put(getNicknameValueMapKey(), nickname);
      
      // the id will be used to check whether this address is default or not
      edit.put(getAddressIdValueMapKey(), theAddress.getRepositoryId());
      
      // and for the changeAddressNickname handler
      edit.put(getNewNicknameValueMapKey(), nickname);
      String[] addressProps = getAddressProperties();
      
      // Store each property in the map
      Object property;
      for (int i = 0; i < addressProps.length; i++) {
        property = theAddress.getPropertyValue(addressProps[i]);
        if (property != null)
          edit.put(addressProps[i], property);
      }
      
      return true;
    } catch (TransactionDemarcationException e) {
      throw new ServletException(e);
    } finally {
      try {
        if (tm != null)
          td.end();
      } catch (TransactionDemarcationException e) {
        if (isLoggingDebug())
          logDebug("Ignoring exception", e);
      }
    }
  }
  
  //---------------------------------------
  // handleClear 
  //---------------------------------------
  
  /**
   * Override to prevent clear if there are form errors.
   * {@inheritDoc}
   * <p>
   * This is here because postLogin will do a clear of the value dictionary even if the
   * login fails
   */
  public boolean handleClear(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    if (getFormError()) {
      return true;
    } else {
      return super.handleClear(pRequest, pResponse);
    }
  }  
  
  /**
   * Override to prevent clear if there are form errors.
   * {@inheritDoc}
   * <p>
   * This is here because postLogin will do a clear of the value dictionary even if the
   * login fails
   */
  public boolean handleClearForm(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
      if(mEditValue != null) {
        mEditValue.clear();
      }
      if(mBillAddrValue != null) {
        mBillAddrValue.clear();
      }
      
      resetFormExceptions();
      return super.handleClear(pRequest, pResponse);
  } 
  
  /**
   * Perform pre-registration actions before user actually registered. 
   * Check email address. If it is passed validation, redirect to 
   * the registration page with filled in email value. Otherwise,
   * add form error and stays on the same page.
   * 
   * @param pRequest Dynamo HTTP request
   * @param pResponse Dynamo HTTP response
   * @return If redirect to the registration page occurred,
   *         return false. If no redirect occurred, return true.
   * @throws ServletException if there was an error while executing the code
   * @throws IOException      if there was an error with servlet io
   */
  public boolean handlePreRegister(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws ServletException, IOException {
    
    //validate the email address
    String email = getStringValueProperty(getStorePropertyManager().getEmailAddressPropertyName());

    if (StringUtils.isBlank(email) || !((StoreProfileTools) getProfileTools()).validateEmailAddress(email)) {
      generateFormException(MSG_INVALID_EMAIL, getAbsoluteName(), pRequest);
    }
    else if (((StoreProfileTools) getProfileTools()).isDuplicateEmailAddress(email)) {
      generateFormException(MSG_DUPLICATE_USER, getAbsoluteName(), pRequest);
    }
    
    return checkFormRedirect(getPreRegisterSuccessURL(), getPreRegisterErrorURL(), pRequest,
        pResponse);
  }
  
  //---------------------------------------
  // findUser 
  //---------------------------------------
  
  protected RepositoryItem findUser(String pLogin, String pPassword,
      Repository pProfileRepository, DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) throws RepositoryException,
      ServletException, IOException {
    // convert login to lower case as case-insensitive logins are used
    // and they are stored in repository in lower case
    return super.findUser(pLogin.toLowerCase(), pPassword, pProfileRepository, pRequest,
        pResponse);
  }
  
  //---------------------------------------
  // Utility methods 
  //---------------------------------------

  /**
   * Utility method to retrieve the StorePropertyManager.
   * @return property manager 
   */
  protected StorePropertyManager getStorePropertyManager() {
    return (StorePropertyManager) getProfileTools().getPropertyManager();
  } 

  /**
   * Create a form exception, by looking up the exception code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param pWhatException
   *            String description of exception
   * @param pRepositoryExc
   *            RepositoryException
   * @param pRequest
   *            DynamoHttpServletRequest
   */
  protected void generateFormException(String pWhatException, RepositoryException pRepositoryExc,
                                       DynamoHttpServletRequest pRequest) {
    generateFormException(pWhatException,null,pRepositoryExc,pRequest);
  }
  
  /**
   * Create a form exception, by looking up the exception code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param pWhatException
   *            String description of exception
   * @param pArgs
   *            String array with arguments used message formatting
   * @param pRepositoryExc
   *            RepositoryException
   * @param pRequest
   *            DynamoHttpServletRequest
   */
  protected void generateFormException(String pWhatException, Object[] pArgs, RepositoryException pRepositoryExc,
                                       DynamoHttpServletRequest pRequest) {   
    ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
    String errorStr = bundle.getString(pWhatException);
    if (pArgs != null && pArgs.length > 0){
      errorStr = (new MessageFormat(errorStr)).format(pArgs);
    }
    addFormException(new DropletFormException(errorStr, pRepositoryExc, pWhatException));
  }

  /**
   * Creates a form exception, by looking up the exception code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param whatException
   *            String description of exception
   * @param pArgs
   *            String array with arguments used message formatting
   * @param pPath
   *            Full path to form handler property associated with the exception
   * @param pRequest
   *            DynamoHttpServletRequest
   */
  protected void generateFormException(String pWhatException, Object[] pArgs, 
                                       String pPath, DynamoHttpServletRequest pRequest) {
    ResourceBundle bundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, getLocale(pRequest));
    String errorStr = bundle.getString(pWhatException);

    if (pArgs != null && pArgs.length > 0){
      errorStr = (new MessageFormat(errorStr)).format(pArgs);
    }
    
    addFormException(new DropletFormException(errorStr, pPath, pWhatException));
  }
  
  /**
   * Creates a form exception, by looking up the exception code in a resource
   * file identified by the RESOURCE_BUNDLE constant (defined above).
   *
   * @param whatException
   *            String description of exception 
   * @param pPath
   *            Full path to form handler property associated with the exception
   * @param pRequest
   *            DynamoHttpServletRequest
   */
  protected void generateFormException(String pWhatException, 
                                       String pPath, DynamoHttpServletRequest pRequest) {
    generateFormException(pWhatException, null, pPath, pRequest);
  }
 

  /**
   * Determine the user's current locale, if available.
   *
   * @param pRequest
   *            DynamoHttpServletRequest
   * @return Locale Request's Locale
   */
  protected Locale getLocale(DynamoHttpServletRequest pRequest) {
    RequestLocale reqLocale = pRequest.getRequestLocale();

    if (reqLocale == null) {
      reqLocale = getRequestLocale();
    }
    
    if(reqLocale == null){
      return null;
    }
    else {
      return reqLocale.getLocale();
    }
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
  
  /**
   * Return a String message specific for the given locale.
   *
   * @param pKey
   *            the identifier for the message to retrieve out of the
   *            ResourceBundle
   * @param pLocale
   *            the locale of the user
   * @return the localized message
   */
  public static String getString(String pKey, Locale pLocale) {
    return (getResourceBundle(pLocale).getString(pKey));
  }  
  
  /**
   * Returns a ResourceBundle specific for the given locale.
   *
   * @param pLocale
   *            the locale of the user
   * @return ResourcerBundle
   * @throws MissingResourceException
   *             ResourceBundle could not be located
   */
  public static ResourceBundle getResourceBundle(Locale pLocale)
      throws MissingResourceException {
    return (LayeredResourceBundle.getBundle(RESOURCE_BUNDLE, pLocale));
  }

  /**
   * Returns the Locale for the user given the request.
   *
   * @param pRequest
   *            the request object which can be used to extract the user's
   *            locale
   * @return Locale
   */
  protected Locale getUserLocale(DynamoHttpServletRequest pRequest) {
    if (pRequest != null) {
      RequestLocale reqLocale = pRequest.getRequestLocale();

      if (reqLocale != null) {
        return reqLocale.getLocale();
      }
    }

    return null;
  }
  
  /**
   * Handles changes on the 'Checkout Defaults' page - default credit card, shipping address and shipping method.
   * @param pRequest - current HTTP request.
   * @param pResponse - current HTTP response.
   * @return true if request hasn't been redirected and false otherwise.
   * @throws ServletException if something goes wrong.
   * @throws IOException if unable to redirect current request.
   */
  public boolean handleCheckoutDefaults(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
  {
    TransactionManager tm = getTransactionManager();
    TransactionDemarcation td = getTransactionDemarcation();
    try
    {
      td.begin(tm, TransactionDemarcation.REQUIRED);
      
      StoreProfileTools profileTools = (StoreProfileTools) getProfileTools();
      StorePropertyManager propertyManager = getStorePropertyManager();
      
      String shippingAddressProperty = propertyManager.getShippingAddressPropertyName();
      String shippingAddressName = (String) getValue().get(shippingAddressProperty);
      if (!StringUtils.isEmpty(shippingAddressName))
      {
        RepositoryItem shippingAddress = profileTools.getProfileAddress(getProfile(), shippingAddressName);
        profileTools.updateProperty(shippingAddressProperty, shippingAddress, getProfile());
      }
      
      String creditCardProperty = propertyManager.getDefaultCreditCardPropertyName();
      String creditCardName = (String) getValue().get(creditCardProperty);
      if (!StringUtils.isEmpty(creditCardName))
      {
        RepositoryItem creditCard = profileTools.getCreditCardByNickname(creditCardName, getProfile());
        profileTools.updateProperty(creditCardProperty, creditCard, getProfile());
      }
      
      String defaultCarrierProperty = propertyManager.getDefaultShippingMethodPropertyName();
      profileTools.updateProperty(defaultCarrierProperty, getValue().get(defaultCarrierProperty), getProfile());
      
      return checkFormRedirect(getUpdateSuccessURL(), getUpdateErrorURL(), pRequest, pResponse);
    } catch (TransactionDemarcationException e)
    {
      throw new ServletException(e);
    } catch (RepositoryException e)
    {
      throw new ServletException(e);
    } finally
    {
      try
      {
        td.end();
      } catch (TransactionDemarcationException e)
      {
        throw new ServletException(e);
      }
    }
  }
  
  /**
   * Address that we'd like to remove could be associated
   * with a gift list. In this case, we'd like to prevent
   * address removal from the profile.
   * 
   * @param pRequest Dynamo http request
   * @param pResponse Dynamo http response
   */
  @SuppressWarnings("unchecked") // Ok, we cast 'secondaryAddresses' Map property
  public void preRemoveAddress(DynamoHttpServletRequest pRequest,
      DynamoHttpServletResponse pResponse) {
    
    // get collection of the gift lists that belongs to the profile,
    // iterate over this collection and check addresses
    // If giftlist address is equal to the address we want to remove,
    // add form exceprion
    String addressNickname = getRemoveAddress();
    Map<String, RepositoryItem> profileAddresses =
          (Map<String, RepositoryItem>) getProfile().getPropertyValue(getStorePropertyManager().getSecondaryAddressPropertyName());
    RepositoryItem profileAddress = (RepositoryItem) profileAddresses.get(addressNickname);
    if (profileAddress == null) { //Already removed? Do nothing then.
      return;
    }
    
    GiftlistTools glTools = getGiftlistManager().getGiftlistTools();
    try {
      Collection giftlists = glTools.getGiftlists(getProfile());

      if(giftlists != null) {
        
        for(Object giftlist : giftlists) {
          RepositoryItem giftlistAddress = 
            (RepositoryItem) ((RepositoryItem)giftlist).getPropertyValue(glTools.getShippingAddressProperty());
          
          if(giftlistAddress != null) {
            
            if(profileAddress.getRepositoryId().equals(giftlistAddress.getRepositoryId())) {
              
              String giftlistName = (String) ((RepositoryItem)giftlist).getPropertyValue(glTools.getEventNameProperty());
              generateFormException(MSG_ERR_DELETE_GIFT_ADDRESS, 
                  new String[] { addressNickname, giftlistName}, getAbsoluteName(), pRequest);
            }
          }
        }
      }
        
    } catch (CommerceException e) {
      e.printStackTrace();
    }
  }

}
