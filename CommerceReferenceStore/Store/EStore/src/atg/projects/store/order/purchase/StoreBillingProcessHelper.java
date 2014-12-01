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
package atg.projects.store.order.purchase;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;

import atg.commerce.CommerceException;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.CreditCard;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PaymentGroupManager;
import atg.commerce.order.PaymentGroupOrderRelationship;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.Relationship;
import atg.commerce.order.RelationshipNotFoundException;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.StringUtils;
import atg.projects.store.StoreConfiguration;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.order.StorePaymentGroupManager;
import atg.projects.store.payment.StoreStoreCredit;
import atg.projects.store.profile.StoreProfileTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.userprofiling.Profile;
import atg.userprofiling.PropertyManager;


/**
 * Store implementation of the purchase process helper for billing sub-process.
 *
 * @author ATG
 * @version $Revision: #2 $
  */
public class StoreBillingProcessHelper extends StorePurchaseProcessHelper {
  
  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreBillingProcessHelper.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  private static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";
  private static ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Missing required address property.
   */
  protected static final String MSG_MISSING_REQUIRED_ADDRESS_PROPERTY = "missingRequiredAddressProperty";

  /**
   * Address property name map.
   */
  Map mAddressPropertyNameMap;

  /**
   * Store order tools.
   */
  private StoreOrderTools mStoreOrderTools;

  /**
   * Store configuration.
   */
  private StoreConfiguration mStoreConfiguration;

  /**
    * Dublicate user message key.
    */
   protected static final String MSG_DUPLICATE_USER = "userAlreadyExists";

  /**
   * E-mail is empty message key.
   */
  protected static final String MSG_EMAIL_IS_EMPTY = "emailIsEmpty";

  /**
   * Password don't match message key.
   */
  protected static final String MSG_PASSWORDS_DONT_MATCH = "passwordsDontMatch";

  /**
   * Password is empty message key.
   */
  protected static final String MSG_PASSWORD_IS_EMPTY = "passwordIsEmpty";

  /**
   * Password wrong length message key.
   */
  protected static final String MSG_PASSWORD_WRONG_LENGTH = "passwordWrongLength";

  /**
   * Wrong nickname length message key.
   */
  protected static final String MSG_NICKNAME_WRONG_LENGTH = "nickNameWrongLength";

  /**
   * Duplicate CC nickname message key.
   */
  protected static final String MSG_DUPLICATE_CC_NICKNAME = "duplicateCCNickname";

  /**
   * Store Credit payment group type name.
   */
  protected static final String SC_PAYMENT_GROUP_TYPE_NAME = "storeCredit";

  /**
   * Online credit insufficient.
   */
  protected static final String ONLINE_CREDIT_INSUFFICIENT = "onlineCreditInsufficient";

  /**
   * Store credit payment group error message key.
   */
  protected static final String STORE_CREDIT_ERROR = "storeCreditPaymentGroupError";

  /**
   * Invalid verification number message key.
   */
  protected static final String VERIFICATION_NUMBER_INVALID = "invalidCreditCardVerificationNumber";
  
  /**
    * New address constant.
    */
   protected static final String NEW_ADDRESS = "NEW";

   /**
   * New credit card constant.
   */
   protected static final String NEW_CREDIT_CARD = "NEW";


   private static final String LASTNAME_PROP_NAME = "lastname";

   public static final String MIDDLENAME_PROP_NAME = "middlename";

   public static final String FIRSTNAME_PROP_NAME = "firstname";

   public static final String AMOUNT_REMAINING_PROP_NAME = "amountRemaining";

   public static final String AMOUNT_AUTHORIZED_PROP_NAME = "amountAuthorized";

  //---------------------------------------------------------------------------
  // property: paymentGroupManager
  //---------------------------------------------------------------------------
  PaymentGroupManager mPaymentGroupManager;

  /**
   * Set the PaymentGroupManager property.
   * @param pPaymentGroupManager a <code>PaymentGroupManager</code> value
   */
  public void setPaymentGroupManager(PaymentGroupManager pPaymentGroupManager) {
    mPaymentGroupManager = pPaymentGroupManager;
  }

  /**
   * Return the PaymentGroupManager property.
   * @return a <code>PaymentGroupManager</code> value
   */
  public PaymentGroupManager getPaymentGroupManager() {
    return mPaymentGroupManager;
  }

  /**
   * property: Reference to the CheckoutOptionSelections component
   */
  private CheckoutOptionSelections mCheckoutOptionSelections;

  /**
   * @return the CheckoutOptionSelections component.
   */
  public CheckoutOptionSelections getCheckoutOptionSelections() {
    return mCheckoutOptionSelections;
  }

  /**
   * @param pCheckoutOptionSelections the CheckoutOptionSelections component to set.
   */
  public void setCheckoutOptionSelections(CheckoutOptionSelections pCheckoutOptionSelections) {
    mCheckoutOptionSelections = pCheckoutOptionSelections;
  }

  //---------------------------------------------------------------------------
  // ResourceBundle support
  //---------------------------------------------------------------------------

  /**
   * Returns the error message ResourceBundle
   */
  protected ResourceBundle getResourceBundle() {
    return sResourceBundle;
  }

  /**
   * Returns the name of the error message ResourceBundle
   */
  protected String getResourceBundleName() {
    return MY_RESOURCE_NAME;
  }

 /**
   * Checks to see if user is paying with a stored credit card.
   *
   * @return true if success, false - otherwise
   */
  protected boolean isPayingWithOnlineCredit(String[] pOnlineCreditIds) {
    if ((pOnlineCreditIds != null) && (pOnlineCreditIds.length > 0)) {
      return true;
    }
    return false;
  }

  /**
   * This method saves the credit card from the payment group to the profile.
   * And Save credit card as profile default if it is
   * @param pOrder
   * @param pProfile
   * @param pCreditCardNickName
   */
  public void saveCreditCardToProfile(Order pOrder, RepositoryItem pProfile, String pCreditCardNickName) {

    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();

    // Copy the credit card to the profile
    CreditCard card = getCreditCard(pOrder);
    //false - user must be paying whole order amount with online credit.
    //true - some remaining amount is paid by credit card
    if(isPaymentGroupOrderRelationShipExist(card)) {

      if (StringUtils.isBlank(pCreditCardNickName)) {
        pCreditCardNickName = orderTools.createCreditCardNickname(card);
      }

      orderTools.copyCreditCardToProfile(pProfile, pOrder, pCreditCardNickName);

      // If user does not have a default credit card, then save this one
      // as default
      // Note: this will always be true for anonymous users
      if (profileTools.getDefaultCreditCard(pProfile) == null) {
        profileTools.setDefaultCreditCard(pProfile, pCreditCardNickName);
      }
    }
  }

  /**
   * This method saves the Billing Address to the profile.
   * @param pOrder
   * @param pProfile
   * @param pBillingAddressNickname
   */
  public void saveBillingAddressToProfile(Order pOrder, RepositoryItem pProfile, String pBillingAddressNickname) {
  // Save the billing info as a possible shipping address
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
    CreditCard card = getCreditCard(pOrder);

    try {
      String billingAddressNickname = pBillingAddressNickname;

      if (StringUtils.isBlank(billingAddressNickname)) {
        billingAddressNickname = profileTools.getUniqueShippingAddressNickname(card.getBillingAddress(), pProfile, null);
      }

      orderTools.saveAddressToAddressBook(pProfile, card.getBillingAddress(), billingAddressNickname);
    } catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMinor(ce.getMessage()));
      }
    }
  }

  /**
   * This method will register a new user during checkout.
   * @param pOrder
   * @param pProfile
   * @param pProfileValues
   * @throws CommerceException
   */
  protected void registerNewUserDuringCheckout(Order pOrder, Profile pProfile, Map pProfileValues)
    throws CommerceException {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    try {
      orderTools.registerNewUserDuringCheckout(pProfile, pOrder, pProfileValues);
    } catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error registering user: " + ce));
      }
      throw ce;

    }
  }

  /**
   * Adds user to email opt-in list.
   * @param pProfile
   * @param pEmailOptInSourceCode
   */
  protected void addUserToOptInList(Profile pProfile, String pEmailOptInSourceCode) {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    StoreProfileTools pTools = (StoreProfileTools) orderTools.getProfileTools();
    PropertyManager pm = pTools.getPropertyManager();

    String email = (String) pProfile.getPropertyValue(pm.getEmailAddressPropertyName());
    try{

      if (isLoggingDebug()) {
        logDebug("Adding emailRecipient to database");
      }
      // Add EmailRecipient to db
      pTools.createEmailRecipient(email, pEmailOptInSourceCode);
    }catch(RepositoryException re) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error creating email recipient: " + re));
      }
    }
  }

  /**
   * Saves the address on the credit card to the user's default billing
   * address.
   * @param pProfile
   * @param pCreditCard
   */
  protected void saveDefaultBillingAddress(RepositoryItem pProfile, CreditCard pCreditCard) {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
    StorePropertyManager pm = (StorePropertyManager) profileTools.getPropertyManager();

    if (pProfile.getPropertyValue(pm.getBillingAddressPropertyName()) == null) {
      if (pCreditCard != null) {
        if (isLoggingDebug()) {
          logDebug("Saving default billing address.");
        }

        orderTools.saveAddressToDefaultBilling(pCreditCard.getBillingAddress(), pProfile);
      }
    }
  }


  /**
   * This method checks to see if the user chose a profile address. If so, the
   * address is copied from the address book to the credit card.
   * @param pCard
   * @param pStoredAddressSelection
   * @param pProfile
   * @param pOrder
   * @throws CommerceException
   */
  protected void addBillingAddressToCard(CreditCard pCard, boolean pUsingStoredAddress, String pStoredAddressSelection,
                                             RepositoryItem pProfile, Order pOrder)

    throws CommerceException {

    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    // If user is using a stored address with the new credit
    // card, copy the address book address to this credit card.

    if (pUsingStoredAddress) {
      if (isLoggingDebug()) {
        logDebug("Copying address: " + pStoredAddressSelection + " to credit card.");
      }

      // User chose a stored address, copy it from the address book
      // to the current CC payment group.
      try {
        // User entered the card info, but is copying the
        // profile address to card. We need to preserve the
        // name on card setting. The "copyAddress" method will
        // overwrite the name on card. Don't want to override,
        // b/c not applicable to other cases of copyAddress.
        StorePropertyManager pm = (StorePropertyManager) orderTools.getProfileTools().getPropertyManager();

        Map addresses = (Map) pProfile.getPropertyValue(pm.getSecondaryAddressPropertyName());
        RepositoryItem storedAddress = (RepositoryItem) addresses.get(pStoredAddressSelection);

        String firstname = null, middlename = null, lastname = null;
        
        if(storedAddress != null) {
          firstname = (String) storedAddress.getPropertyValue(FIRSTNAME_PROP_NAME);
          middlename = (String) storedAddress.getPropertyValue(MIDDLENAME_PROP_NAME);
          lastname = (String) storedAddress.getPropertyValue(LASTNAME_PROP_NAME);
        }

        orderTools.copyProfileAddressToCreditCard(pProfile, pOrder, pStoredAddressSelection);
        pCard.getBillingAddress().setFirstName(firstname);
        pCard.getBillingAddress().setMiddleName(middlename);
        pCard.getBillingAddress().setLastName(lastname);

      } catch (CommerceException ce) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor("Error copying address: " + ce));
        }
        throw ce;
      }
    } else {
      if (isLoggingDebug()) {
        logDebug("No profile address selected, user adding a new address.");
        
        // If user is paying with new card, AND not using an
        // address-book address, then set prefill to true
        getCheckoutOptionSelections().setPrefillBillingAddress(true);
      }
    }
 }

  /**
   * Utility method to copy credit card from profile to order.
   *
   * @param pCreditCard - Instance of Credit Card Payment Group
   * @param pNickname - nickname profile stored credit card nick name
   * @param pProfile -  Instance of Profile Repository
   * @param pUserLocale - Locale
   */
  protected void copyCreditCardFromProfile(CreditCard pCreditCard, String pNickname, RepositoryItem pProfile, Locale pUserLocale) {
    if (isLoggingDebug()) {
      logDebug("Using credit card: " + pNickname);
    }

    try {
      StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
      StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();

      profileTools.copyCreditCardToPaymentGroup(pNickname, pCreditCard, pProfile, pUserLocale);
    } catch (Exception e) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error copying credit card from profile: " + e.toString()));
      }
    }
  }

  /**
   * This method verifies if the online credit(s) total is sufficient to cover
   * the order total. Note the need to calculate the true amount remaining
   * by subtracting the amount authorized.
   *
   * The uncovered amount of the order total is returned
   *
   * @return true if success, false - otherwise
   */
  protected double validateSufficientOnlineCredit(String[] pOnlineCreditIds, Order pOrder) {

    if (isLoggingDebug()) {
      logDebug("Checking to see online credit amount will cover order total");
    }

    if (!isPayingWithOnlineCredit(pOnlineCreditIds)) {
      return pOrder.getPriceInfo().getTotal();
    }

    StorePaymentGroupManager paymtGrpMgr = (StorePaymentGroupManager) getPaymentGroupManager();
    List onlineCredits = paymtGrpMgr.getOnlineCredits(pOnlineCreditIds);
    Iterator itr = onlineCredits.iterator();
    double totalCredit = 0.00;
    Double amountRemaining = null;
    Double amountAuthorized = null;
    double orderTotal = pOrder.getPriceInfo().getTotal();

    while (itr.hasNext()) {
      RepositoryItem onlineCredit = (RepositoryItem) itr.next();
      amountRemaining = (Double) onlineCredit.getPropertyValue(AMOUNT_REMAINING_PROP_NAME);
      amountAuthorized = (Double) onlineCredit.getPropertyValue(AMOUNT_AUTHORIZED_PROP_NAME);
      totalCredit += (amountRemaining.doubleValue() - amountAuthorized.doubleValue());
    }

    if (isLoggingDebug()) {
      logDebug("Order total: " + orderTotal);
      logDebug("Online credit total: " + totalCredit);
    }

    if (orderTotal > totalCredit) {
      return orderTotal - totalCredit;
    }

    return 0;
  }

  /**
   * Get the remaining Amount from the order.
   * 
   * @throws atg.commerce.CommerceException if commerce error occurs
   */
  public double getOrderRemaningAmount(Order pOrder) throws CommerceException {

    if(pOrder == null) { }

    Order order = pOrder;

    PricingTools pricingTools = getOrderManager().getOrderTools().getProfileTools().getPricingTools();
    double usedStoreCreditAmount = 0;
    double orderTotal = order.getPriceInfo().getTotal();
    double amountAppliedToOrder;
    double orderReminingAmount;

    if(order.getPaymentGroupRelationshipCount() > 0) {

      List groups = order.getPaymentGroupRelationships();
      if (groups != null) {
        Iterator pgOrderRelsIterator = groups.iterator();
        while(pgOrderRelsIterator.hasNext()) {

          PaymentGroupOrderRelationship pgOrderRel = (PaymentGroupOrderRelationship) pgOrderRelsIterator.next();
          PaymentGroup paymentGroup =  pgOrderRel.getPaymentGroup();
          if(paymentGroup.getPaymentGroupClassType().equals(SC_PAYMENT_GROUP_TYPE_NAME)) {

            if(pgOrderRel.getRelationshipType() == RelationshipTypes.ORDERAMOUNT) {

              amountAppliedToOrder = (pgOrderRel.getAmount() > 0) ? pgOrderRel.getAmount() : ((StoreStoreCredit)paymentGroup).getAmountAppliedToOrder();
              usedStoreCreditAmount += amountAppliedToOrder;
              if (isLoggingDebug()) {
                logDebug("Current Store Credit PaymentGroup  Amount: "+ amountAppliedToOrder);
              }

            } else {
              if (isLoggingDebug()) {
                logDebug("All Order Amount paid by store credits. Order Remaining Amount is 0");
              }
              return 0;
            }
          }
        }
      }
    }
    orderReminingAmount = pricingTools.round(orderTotal - usedStoreCreditAmount);

    if (isLoggingDebug()) {
      logDebug("Total Amount of StoreCredits PaymentGroup = " + usedStoreCreditAmount);
      logDebug("Order Remaning Amount = " + orderReminingAmount);
    }

    return orderReminingAmount;
  }

  /**
   * @return true if credit cards are new, false - otherwise.
   */
  protected boolean isNewCreditCards(Order pOrder) {
    int size;

    if ((pOrder.getPaymentGroups() != null) && (pOrder.getPaymentGroups().size() > 0)) {
      size = pOrder.getPaymentGroups().size();

      for (int i = 0; i < size; i++) {
        if ((pOrder.getPaymentGroups().get(i)) instanceof CreditCard) {
          if (!isCreditCardEmpty((CreditCard) pOrder.getPaymentGroups().get(i))) {
            return true;
          }
        }
      }
    }

    return false;
  }

  /**
   * Checks to see if a CreditCard object is empty.  Empty
   * means that certain necessary fields are missing.  The properties
   * that it checks for are those specified by the creditCardProperties
   * String array.
   *
   * <P>
   *
   * This behavior can be overriden by making additions to the String array
   * creditCardProperties, or if necessary extending this method.
   *
   * @param pCreditCard a value of type 'CreditCard'
   * @return true if the payment group is empty.
   * @deprecated this method has been moved to CommerceProfileTools
   */
  protected boolean isCreditCardEmpty(CreditCard pCreditCard) {
    StoreProfileTools profileTools = (StoreProfileTools)getOrderManager().getOrderTools().getProfileTools();
    return profileTools.isCreditCardEmpty(pCreditCard);
  }

  /**
   * Remove any store credit payment groups from order
   * @param pOrder
   *
   **/
  protected void removeStoreCreditPaymentGroups(Order pOrder) {
      ((StorePaymentGroupManager)getPaymentGroupManager()).removeStoreCreditPaymentGroups(pOrder);
  }

  /**
   * Initialize Store Credit Payment Group, add amount to the payment group and add remaning amount
   * to other payment groups CreditCard
   * @param pOrder
   * @param pProfile
   * @param pOnlineCreditIds
   * @throws CommerceException if an error occurs
   * @throws RepositoryException if an error occurs
   * @throws IOException if an error occurs
   * @throws ServletException if an error occurs
   */
  public void setupStoreCreditPaymentGroupsForOrder(Order pOrder, RepositoryItem pProfile, String[] pOnlineCreditIds)
    throws CommerceException, RepositoryException, IOException, ServletException {

    // In case there are lingering store credit payment groups we need to remove them
    removeStoreCreditPaymentGroups(pOrder);

    // first preference given to any store credits, then credit cards
    // If user is paying with online credit initialize payment groups

    if (isPayingWithOnlineCredit(pOnlineCreditIds)) {
      initializePaymentGroups(pOrder, pProfile, pOnlineCreditIds);

    }

    addOrderAmountRemainingToCreditPaymentGroup(pOrder);
  }

 /**
   * Initializes the store credit payment method(s) based on the store credits
   * the user chose.
   *
   * @throws ServletException If servlet exception occurs
   * @throws IOException If IO exception occurs
   * @return true on success, false - otherwise
   */
  protected boolean initializePaymentGroups(Order pOrder, RepositoryItem pProfile,String[] pOnlineCreditIds )
    throws CommerceException, ServletException, IOException {

    if (isLoggingDebug()) {
      logDebug("Store credit being used, initializing store credit payment groups.");
    }

    StorePaymentGroupManager paymtGrpMgr = (StorePaymentGroupManager) getPaymentGroupManager();
    return paymtGrpMgr.initializePaymentMethods(pProfile, pOrder, pOnlineCreditIds);

  }

  /**
   * Add Credit Card Verification Number to the CreditCard Paymanet Group.
   * @param pOrder
   * @param pCreditCardVerificationNumber
   */
   public void addCreditCardAuthorizationNumber(Order pOrder,String pCreditCardVerificationNumber) {

     StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
     if (getStoreConfiguration().isRequireCreditCardVerification()) {

      if(orderTools.validateCreditCardAuthorizationNumber(pCreditCardVerificationNumber)) {
        orderTools.setCreditCardVerificationNumber(pOrder, pCreditCardVerificationNumber);
      }
    }
  }

  /**
   * Verify the Credit Card verification number
   * @param pCreditCardVerificationNumber
   * @return true if StoreConfiguration.isRequireCreditCardVerification is false and
   *         given authorization number is valid
   *         else return false
   */
  public boolean validateCreditCardAuthorizationNumber(String pCreditCardVerificationNumber) {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    if (getStoreConfiguration().isRequireCreditCardVerification()) {
      return orderTools.validateCreditCardAuthorizationNumber(pCreditCardVerificationNumber);
    }
    return true;
  }

  /**
   * Copy credit card from profile to order if paying with profile credit card.
   * @param pCreditCard
   * @param pStoredCreditCardName
   * @param pProfile
   * @param pUserLocale
   */
  public void addCreditCardDetails(CreditCard pCreditCard, boolean pUsingStoredCreditCard, String pStoredCreditCardName,
                                   RepositoryItem pProfile, Locale pUserLocale) {
    if (pUsingStoredCreditCard) {
      // If user is paying with a stored credit card, copy from profile.
      copyCreditCardFromProfile(pCreditCard, pStoredCreditCardName, pProfile, pUserLocale);
    } else {
      getCheckoutOptionSelections().setPrefillCreditCard(true);
    }
  }

  /**
   * Utility method to fetch credit card and set properties from page.
   *
   * @return credit card for this order
   */
  public CreditCard getCreditCard(Order pOrder) {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    return orderTools.getCreditCard(pOrder);
  }

  /**
   * Added Remaining Order Amount relationship To Credit Card PaymentGroup.
   * Removed remaining Order Amount relationship if all payment payed by other payment groups
   * Like Online Credit.
   * 
   * @param pOrder
   * @throws CommerceException
   * @throws InvalidParameterException
   */
  public void addOrderAmountRemainingToCreditPaymentGroup(Order pOrder)
    throws CommerceException,InvalidParameterException {

    CreditCard creditCard = getCreditCard(pOrder);
    double orderRemainingAmount = getOrderRemaningAmount(pOrder);
    if( orderRemainingAmount <= 0 ) {
      try {

       getOrderManager().removeRemainingOrderAmountFromPaymentGroup(pOrder, creditCard.getId());
      } catch (RelationshipNotFoundException exc) {
        if (isLoggingDebug()) {
          logDebug("Credit Card RelationShip not found:");
        }
       return;
      }
    } else {
      try {
        
        creditCard.getOrderRelationship();

      } catch (RelationshipNotFoundException exc) {

        getOrderManager().addRemainingOrderAmountToPaymentGroup(pOrder, creditCard.getId());

      } catch (InvalidParameterException exc) {

        throw exc;
      }
    }
  }

  /**
   *  Added remaining order amount to the payment group for given
   *  <code>pStoredCreditCardName</code>. If <code>pStoredCreditCardName</code>
   *  is not empty, then copy credit card details from <code>pProfile</code> to <code>pOrder</code>  
   *
   * @param pOrder order
   * @param pProfile user profile
   * @param pStoredCreditCardName credit card nickname
   * @param pUserLocale
   * @return true if credit card details added to the order
   * @throws CommerceException
   * @throws InvalidParameterException
   */
  public boolean setupCreditCardPaymentGroupsForOrder(Order pOrder, RepositoryItem pProfile, boolean pUsingStoredCreditCard,
                                                   String pStoredCreditCardName, Locale pUserLocale )
    throws CommerceException, InvalidParameterException {

    // to fix issue CRS-167694 we should
    // check if another credit card already exists with this
    // relationship type. 
    boolean exists = false;
    List paymentRelationships = pOrder.getPaymentGroupRelationships();
    
    for(Object paymentRelationship : paymentRelationships) {
      if( ((Relationship)paymentRelationship).getRelationshipType() 
          == RelationshipTypes.ORDERAMOUNTREMAINING ) {
        exists = true;
        break;
      }
    }

    if(!exists) {
      addOrderAmountRemainingToCreditPaymentGroup(pOrder);
    } 
    
    if (pStoredCreditCardName != null) {
      addCreditCardDetails(getCreditCard(pOrder), pUsingStoredCreditCard, 
          pStoredCreditCardName, pProfile, pUserLocale);
      
      return true;
    } else {
      return false;
    }
  }

  /**
   * Run the pipeline which should be executed at the last
   * of the billing process.
   *
   * @param pOrder
   *            the order to reprice
   * @param pPricingModels
   *            the set of all pricing models for the user (item, order,
   *            shipping, tax)
   * @param pLocale
   *            the locale that the order should be priced within
   * @param pProfile
   *            the user who owns the order
   * @param pExtraParameters
   *            A Map of extra parameters to be used in the pricing
   * @throws atg.service.pipeline.RunProcessException
   *             if error running pipeline process
   */
  protected PipelineResult runProcessMoveToConfirmation(Order pOrder, PricingModelHolder pPricingModels,String pMoveToConfirmationChainId,
                                              Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException {

    OrderManager orderManager = getOrderManager();
    OrderTools orderTools =  orderManager.getOrderTools();
    CatalogTools catalogTools = getOrderManager().getOrderTools().getCatalogTools();
    InventoryManager inventoryManager = orderTools.getInventoryManager();

    HashMap params = new HashMap(11);

    params.put(PipelineConstants.CATALOGTOOLS, catalogTools);

    params.put(PipelineConstants.INVENTORYMANAGER, inventoryManager);

    PipelineResult result = runProcess(pMoveToConfirmationChainId, pOrder, pPricingModels, pLocale, pProfile,
        params, pExtraParameters);

    return result;
  }

  /**
   * Set the EmailId and LoginId into the profile repository
   * @param pProfile
   * @param pEmail
   */
  protected void setProfileEmailAndLogin(RepositoryItem pProfile, String pEmail) {

    StorePropertyManager pm =
            (StorePropertyManager) getOrderManager().getOrderTools().getProfileTools().getPropertyManager();
    Profile profile = (Profile) pProfile;
    String email = pEmail;

    if (isLoggingDebug()) {
      logDebug("Adding email: " + email);
    }

    profile.setPropertyValue(pm.getEmailAddressPropertyName(), email);
    // Store login in lower case to support case-insensitive logins
    profile.setPropertyValue(pm.getLoginPropertyName(), email.toLowerCase());
  }

  /**
   * Add the Order into the Repository if it is transient
   * @param pOrder
   */
  public void addOrder(Order pOrder) {
    //add the order to the repository if transient
    Order order = pOrder;

    if (order.isTransient()) {
      try {
        getOrderManager().addOrder(order);
      } catch (CommerceException e) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor(""), e);
          logError(LogUtils.formatMajor("Error saving the order to repository after registration : " +
              e.getMessage()));
        }
      }
    }
  }

  /**
   * Set the password and LoginId into the profile repository
   * @param pProfile
   * @param pNewUserPassword
   */
  protected void setProfilePassword(RepositoryItem pProfile, String pNewUserPassword) {
    StorePropertyManager pm =
            (StorePropertyManager) getOrderManager().getOrderTools().getProfileTools().getPropertyManager();
    Profile profile = (Profile) pProfile;

    if (isLoggingDebug()) {
      logDebug("Adding password to profile.");
    }

    profile.setPropertyValue(pm.getPasswordPropertyName(), pNewUserPassword);
  }

  /**
   * Utility method to validate email address.
   *
   * @return true if success, false -  otherwise
   */
  protected boolean validateEmailAddress(RepositoryItem pProfile, String pEmail)
    throws StorePurchaseProcessException {

    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();

    String email = pEmail;

    // Is email here
    if (StringUtils.isBlank(email)) {
      // add form exception
      if (isLoggingDebug()) {
        logDebug("User email is empty, adding form exception.");
      }
      throw new StorePurchaseProcessException(MSG_EMAIL_IS_EMPTY);

    } else {

      if (profileTools.isDuplicateEmailAddress(email)) {
        throw new StorePurchaseProcessException(MSG_DUPLICATE_USER);
      }
    }

    return true;
  }

  /**
   * Utility method to validate password input.
   *
   * @return true if success, false - otherwise
   */
  protected boolean validatePassword(RepositoryItem pProfile, String pNewUserPassword, String pRetypeNewPassword)
    throws StorePurchaseProcessException {
    boolean success = true;

    Profile profile = (Profile) pProfile;

    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();

    StorePropertyManager pm = (StorePropertyManager) profileTools.getPropertyManager();

    String password = pNewUserPassword;

    // Did user enter password information?
    if (StringUtils.isBlank(password)) {

      throw new StorePurchaseProcessException(MSG_PASSWORD_IS_EMPTY);
    }

    String retypedPassword = pRetypeNewPassword;

    // Are passwords the same?
    if (!password.equals(retypedPassword)) {

      throw new StorePurchaseProcessException(MSG_PASSWORDS_DONT_MATCH);
    }

    if (!profileTools.isValidPasswordLength(password)) {

      throw new StorePurchaseProcessException(MSG_PASSWORD_WRONG_LENGTH);
    }

    if (success) {
      if (isLoggingDebug()) {
        logDebug("Password passed validation.");
      }
    }

    return success;
  }

  /**
   * Utility method to check if user's NickName meets the min & max length.
   *
   * @param pNickName
   *            is NickName string
   * @return True if NickName meets min/max requirements False if NickName
   *         fails min/max requirements
   */
  public boolean isValidNickNameLength(String pNickName, int pMinNickNameLength, int pMaxNickNameLength) {
    int nickNameLength = pNickName.length();

    // Check to see if NickName.length is between min and max values
    if ((nickNameLength >= pMinNickNameLength) && (nickNameLength <= pMaxNickNameLength)) {
      return true;
    }

    return false;
  }

  /**
   * This method validates the credit card nickname if one is required.
   * @param pOrder
   * @param pProfile
   * @param pCreditCardNickname
   * @param pMinNickNameLength
   * @param pMaxNickNameLength
   * @return true if success else return false
   */
  public boolean validateCreditCardNicknameInput(Order pOrder, RepositoryItem pProfile, String pCreditCardNickname,
                                                 int pMinNickNameLength,int pMaxNickNameLength)
    throws StorePurchaseProcessException {

    // check for a valid nickname only if the save credit card option is checked and the
    // registration is taking place or the user is already logged in
    Profile profile = (Profile) pProfile;
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();
    StoreProfileTools profileTools = (StoreProfileTools) orderTools.getProfileTools();
    StorePropertyManager pmgr = (StorePropertyManager) profileTools.getPropertyManager();

    String nickname = pCreditCardNickname;

    // If nickname is not supplied by the user, create a new one
    if (StringUtils.isBlank(nickname)) {
      nickname = orderTools.createCreditCardNickname(getCreditCard(pOrder));
    }
    boolean isValidNickNameLength = isValidNickNameLength(nickname.trim(), pMinNickNameLength, pMaxNickNameLength);
    if (!isValidNickNameLength) {
      // BUGS-FIXED: 136133 :: case where nick name length exceeds max 42 chars
      if (isLoggingDebug()) {
        logDebug("Credit card nickname Length is invalid, must be 1-42 characters, adding form exception.");
      }

      throw new StorePurchaseProcessException(MSG_NICKNAME_WRONG_LENGTH);
    } else {

      Map creditCards = (Map) profile.getPropertyValue(pmgr.getCreditCardPropertyName());

      if (creditCards.get(nickname) != null) {
        if (isLoggingDebug()) {
          logDebug("Credit card nickname is already used, adding form exception.");
        }

        throw new StorePurchaseProcessException(MSG_DUPLICATE_CC_NICKNAME);
      }
    }

    return true;
  }

  /**
   * Verify, whether Order Relationship exist in the given Payment Group or not.
   * @param pPaymentGroup
   * @return true if relation ship found else return false
   */
  public boolean isPaymentGroupOrderRelationShipExist(PaymentGroup pPaymentGroup) {
    try {


      pPaymentGroup.getOrderRelationship();
    } catch (RelationshipNotFoundException rnfexc) {
      if (isLoggingDebug()) {
        logDebug("BillingProcessHelper: No Relationship Found for credit card" +
                " is paying with a new Credit card.");
        logDebug(rnfexc);
      }

      return false;

    }  catch (InvalidParameterException exc) {

      if (isLoggingDebug()) {
        logDebug("BillingProcessHelper: "+ exc);
      }

      return false;
    }
    return true;
  }

  /**
   * Saves all addresses in the shipping group container's shipping group map.
   * <p>
   * The key is used as the nick name. The shipping group's address is used for the
   * address.
   * <p>
   * Also sets the default shipping method on the profile
   *
   */
  public void saveShippingInfo(RepositoryItem pProfile, ShippingGroupMapContainer pShippingGroupMapContainer) {

    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    RepositoryItem profile = pProfile;
    ShippingGroupMapContainer sgmc = pShippingGroupMapContainer;

    Map shippinggroupmap = sgmc.getShippingGroupMap();
    Collection shippingGroupNames = sgmc.getShippingGroupNames();
    Iterator shippingGroupNamesIterator = shippingGroupNames.iterator();
    HardgoodShippingGroup hgsg = null;

    while (shippingGroupNamesIterator.hasNext()) {
      String nickName = (String) shippingGroupNamesIterator.next();
      ShippingGroup sg = (ShippingGroup) shippinggroupmap.get(nickName);

      if (!(sg instanceof HardgoodShippingGroup)) {
        continue;
      }

      hgsg = (HardgoodShippingGroup) shippinggroupmap.get(nickName);

      try {
        orderTools.saveAddressToAddressBook(profile, hgsg.getShippingAddress(), nickName);
      } catch (CommerceException ce) {
        if (isLoggingError()) {
          logError(LogUtils.formatMinor(ce.getMessage()));
        }
      }
    }

    if (hgsg != null) {
      orderTools.saveDefaultShippingMethod(profile, hgsg.getShippingMethod());
    }
  }

  /**
   * This method returns store credits that for the given profile
   * that can be used for the order.
   * 
   * @param pProfile profile repository item
   * @return array of store credit IDs
   */
  public String[] getStoreCreditIds(RepositoryItem pProfile) {
    String[] availableCreditIds = null;
    ClaimableManager cm = ((StorePaymentGroupManager)getPaymentGroupManager()).getClaimableManager();
    List storeCredits = null;

    if (isLoggingDebug()) {
      logDebug("Checking for store credits for " + pProfile.getRepositoryId());
    }
    
    try {
    storeCredits = cm.getStoreCreditsForProfile(pProfile.getRepositoryId(), false);

    if ((storeCredits != null) && (storeCredits.size() > 0)) {
      availableCreditIds = new String[storeCredits.size()];
      int i = 0;
      for(Object storeCredit : storeCredits) {
        availableCreditIds[i++] = ((RepositoryItem)storeCredit).getRepositoryId();
      }
    }
    } catch(CommerceException ce) {
      logError("Cannot retrieve store credits for the given profile", ce);
    }
    
    return availableCreditIds;
  }
}

