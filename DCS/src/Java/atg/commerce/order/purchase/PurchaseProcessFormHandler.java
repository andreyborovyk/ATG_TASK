/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.commerce.order.purchase;

import atg.repository.Repository;
import atg.repository.RepositoryItem;
import atg.droplet.*;
import atg.servlet.*;
import atg.service.pipeline.*;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.order.*;
import atg.commerce.pricing.*;
import atg.commerce.pricing.priceLists.PriceListManager;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.util.ConcurrentUpdateDetector;
import atg.commerce.util.NoLockNameException;
import atg.commerce.util.PipelineUtils;
import atg.commerce.util.PipelineErrorHandler;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.commerce.util.TransactionLockFactory;
import atg.commerce.util.TransactionLockService;
import atg.nucleus.ServiceException;
import atg.nucleus.naming.ParameterName;
import atg.servlet.ServletUtil;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.core.util.ResourceUtils;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.*;

import javax.transaction.*;

/**
 * This abstract class defines the very general FormHandling functionality
 * that the purchase process formhandlers require, including Transaction
 * management, PipelineChain execution, central configuration,
 * and error processing.
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PurchaseProcessFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public abstract class PurchaseProcessFormHandler extends GenericFormHandler
                                                 implements PipelineErrorHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/PurchaseProcessFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  private static final String MY_RESOURCE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");

  protected static final String PARAM_TRANSACTION_CREATED = "transactionCreated";
  protected static final String PARAM_VALUE_TRUE = "true";
  protected static final String LOCK_NAME_ATTRIBUTE_NAME = "atg.PurchaseProcessFormHandlerLock";
  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------
  // property: PriceListId
  String mPriceListId;

  /**
   * Sets property PriceListId
   * <p>
   * This price list will be used when repricing after add
   * and delete of items.
   *
   * @param pPriceListId a <code>String</code> value
   * @beaninfo description: sets the price list id to use in pricing operations
  */
   public void setPriceListId(String pPriceListId) {
     mPriceListId = pPriceListId;
   }

   /**
    * Returns property PriceListId
    *
    * @return a <code>String</code> value
    */
   public String getPriceListId() {
     return mPriceListId;
   }
  // property: PriceList
  RepositoryItem mPriceList;

  /**
   * Returns the price list identified by the <code>priceListId</code>
   * @return price list repository item
   */
  protected RepositoryItem getPriceList()
  {
      if(getPriceListId() == null)
          return null;
      else
      {
          try
          {
              RepositoryItem priceList = getPriceListManager().getPriceList(getPriceListId());
              return priceList;
          }
          catch (atg.commerce.pricing.priceLists.PriceListException e)
          {
              if(isLoggingError())
                  logError(e);
              return null;
          }
      }
  }

  //-------------------------------------
  // property: configuration
  //-------------------------------------
  PurchaseProcessConfiguration mConfiguration;

  /**
   * Sets property Configuration
   */
  public void setConfiguration(PurchaseProcessConfiguration pConfiguration) {
    mConfiguration = pConfiguration;
  }

  /**
   * Returns property Configuration
   */
  public PurchaseProcessConfiguration getConfiguration() {
    return mConfiguration;
  }

  // Property: CommercePropertyManager defines properties of the profile for Commerce
  CommercePropertyManager mCommercePropertyManager;

  /**
  * Set the CommercePropertyManager property.
  * @param pCommercePropertyManager the Commerce profile property manager
  * @beaninfo description: the claimable manager - used for store credits
  */
  public void setCommercePropertyManager(CommercePropertyManager pCommercePropertyManager) {
      mCommercePropertyManager = pCommercePropertyManager;
  }

  /**
  * Return the CommercePropertyManager property.
  */
  public CommercePropertyManager getCommercePropertyManager() {
      return mCommercePropertyManager;
  }

  PriceListManager mPriceListManager;

  /**
  * Sets the Price List Manager
  * @param pPriceListManager the Price List Manager
  * @beaninfo description: the Price List Manager
  **/
  public void setPriceListManager(PriceListManager pPriceListManager)
  {
      mPriceListManager = pPriceListManager;
  }

  /**
  * Returns the Price List Manager
  **/
  public PriceListManager getPriceListManager()
  {
      return mPriceListManager;
  }
  //-------------------------------------
  // property: DefaultLocale
  //-------------------------------------
  Locale mDefaultLocale;

  /**
   * Sets property DefaultLocale
   *
   * @param pDefaultLocale a <code>Locale</code> value
   */
  public void setDefaultLocale(Locale pDefaultLocale) {
    mDefaultLocale = pDefaultLocale;
  }

  /**
   * Returns property DefaultLocale. If the property value is null, then
   * JVM's default locale is returned.
   *
   * @return a <code>Locale</code> value
   */
  public Locale getDefaultLocale() {
    if (mDefaultLocale != null)
      return mDefaultLocale;
    else
      return Locale.getDefault();
  }

  //---------------------------------------------------------------------------
  // property: UserLocale
  //---------------------------------------------------------------------------
  Locale mUserLocale;

  /**
   * Set the UserLocale property.
   * @param pUserLocale a <code>Locale</code> value
   */
  public void setUserLocale(Locale pUserLocale) {
    mUserLocale = pUserLocale;
  }

  /**
   * Return the UserLocale property.
   * @return a <code>Locale</code> value
   */
  public Locale getUserLocale() {
    return mUserLocale;
  }

  //-------------------------------------
  // property: UseRequestLocale
  //-------------------------------------
  boolean mUseRequestLocale = true;

  /**
   * Sets property UseRequestLocale
   *
   * @param pUseRequestLocale a <code>boolean</code> value
   */
  public void setUseRequestLocale(boolean pUseRequestLocale) {
    mUseRequestLocale = pUseRequestLocale;
  }

  /**
   * Returns property UseRequestLocale
   *
   * @return a <code>boolean</code> value
   */
  public boolean isUseRequestLocale() {
    return mUseRequestLocale;
  }

  //---------------------------------------------------------------------------
  // property: RepriceOrderChainId
  //---------------------------------------------------------------------------
  String mRepriceOrderChainId;

  /**
   * Set the RepriceOrderChainId property.
   * @param pRepriceOrderChainId a <code>String</code> value
   */
  public void setRepriceOrderChainId(String pRepriceOrderChainId) {
    mRepriceOrderChainId = pRepriceOrderChainId;
  }

  /**
   * Return the RepriceOrderChainId property.
   * @return a <code>String</code> value
   */
  public String getRepriceOrderChainId() {
    return mRepriceOrderChainId;
  }

  //---------------------------------------------------------------------------
  // property: useLocksAroundTransactions
  //---------------------------------------------------------------------------

  boolean mUseLocksAroundTransactions = true;
  private boolean mUseLocksAroundTransactionsSet = false;

  /**
   * Specify whether to use a local lock manager to obtain a lock
   * before starting transactions in beforeSet and to release the
   * lock after committing transacations in afterSet.
   * <p>
   * If this property is set to true, locking will be used to prevent one
   * user from trying to modify orders in multiple concurrent threads.
   * This has a small performance impact as we obtain and release the lock,
   * but minimizes the possibility of concurrent update exceptions if
   * two threads try to modify the same order for the same user.
   *
   * The default value for <code>useLocksAroundTransactions</code> is true.
   *
   * @deprecated Use the TransactionLockFactory instead
   **/

  public void setUseLocksAroundTransactions(boolean pUseLocksAroundTransactions) {
    mUseLocksAroundTransactions = pUseLocksAroundTransactions;
    mUseLocksAroundTransactionsSet = true;
  }

  /**
   * Inquire whether to use a local lock manager to obtain a lock
   * before starting transactions in beforeSet, and to release the
   * lock after committing transactions in afterSet.
   *
   * @deprecated Use the TransactionLockFactory instead
   **/

  public boolean isUseLocksAroundTransactions() {
    return mUseLocksAroundTransactions;
  }

  //---------------------------------------------------------------------------
  // property: localLockManager
  //---------------------------------------------------------------------------

  ClientLockManager mLocalLockManager;

  /**
   * Specify the lock manager to use if <code>useLocksAroundTransactions</code>
   * is true.
   *
   * @see #setUseLocksAroundTransactions
   * @deprecated Use the TransactionLockFactory instead
   **/

  public void setLocalLockManager(ClientLockManager pLocalLockManager) {
    mLocalLockManager = pLocalLockManager;
  }

  /**
   * Return the lock manager to use if <code>useLocksAroundTransactions</code>
   * is true.
   *
   * @see #setUseLocksAroundTransactions
   * @deprecated Use the TransactionLockFactory instead
   **/

  public ClientLockManager getLocalLockManager() {
    return mLocalLockManager;
  }

  //---------------------------------------------------------------------------

  // Nucleus path of the default lock manager to try if the localLockManager
  // property has not been set.  This is here to minimize migration pain for
  // customers moving from DCS version 5 to DCS version 6.  Ideally they will
  // go and edit the configurations of their own form handler components to
  // specify the lock manager to use, but in case they don't we have one to
  // fall back on.

  private final static String DEFAULT_LOCK_MANAGER = "/atg/commerce/order/LocalLockManager";

  /**
   * Get the default local lock manager to use if <code>localLockManager</code>
   * has not been set.  If <code>pSetLocalLockManager</code> is true, then also
   * set <code>localLockManager</code> to the value returned so future calls to
   * <code>getLocalLockManager</code> will succeed.
   *
   * @deprecated Use the TransactionLockFactory instead
   **/

  protected ClientLockManager defaultLocalLockManager(boolean pSetLocalLockManager)
  {
    if (isLoggingDebug())
      logDebug("Looking for defaultLocalLockManager");

    try {
      ClientLockManager clm = (ClientLockManager)resolveName(DEFAULT_LOCK_MANAGER);
      if (pSetLocalLockManager)
        setLocalLockManager(clm);
      return clm;
    }
    catch (ClassCastException cce) {
      if (isLoggingError())
        logError(cce);
      return null;
    }
  }

  //---------------------------------------------------------------------------
  // property: Profile
  //---------------------------------------------------------------------------
  RepositoryItem mProfile;

  /**
   * Set the Profile property.
   * @param pProfile a <code>RepositoryItem</code> value
   */
  public void setProfile(RepositoryItem pProfile) {
    mProfile = pProfile;
  }

  /**
   * Return the Profile property.
   * @return a <code>RepositoryItem</code> value
   */
  public RepositoryItem getProfile() {
    return mProfile;
  }

  //---------------------------------------------------------------------------

  // Nucleus path of the default user profile to try if the profile property
  // has not been set.  This is here to minimize migration pain for customers
  // moving from DCS version 5 to DCS version 6.   In DCS 5 many of the purchase
  // process form handlers didn't use the profile at all, and didn't bother to
  // set it in their configuration files.  In DCS 6 users should go back and
  // edit the configurations of their own form handler components to add the
  // profile, but in case they don't we have one to fall back on.

  private final static String DEFAULT_USER_PROFILE = "/atg/userprofiling/Profile";

  /**
   * Get the default user profile to use if <code>profile</code> has not been
   * set.  If <code>pSetProfile</code> is true, then also set <code>profile</code>
   * to the value returned so future calls to <code>getProfile</code> will succeed.
   **/

  protected RepositoryItem defaultUserProfile(boolean pSetProfile)
  {
    if (isLoggingDebug())
      logDebug("Looking for default user profile");

    try {
      RepositoryItem profile = null;
      DynamoHttpServletRequest req;

      if ((req = ServletUtil.getCurrentRequest()) != null)
        profile = (RepositoryItem)req.resolveName(DEFAULT_USER_PROFILE);
      if (pSetProfile)
        setProfile(profile);
      return profile;
    }
    catch (ClassCastException cce) {
      if (isLoggingError())
        logError(cce);
      return null;
    }
  }

  //---------------------------------------------------------------------------
  // property: profileRepository
  //---------------------------------------------------------------------------
  Repository mProfileRepository;

  /**
   * Set the ProfileRepository property.
   * @param pProfileRepository a <code>Repository</code> value
   */
  public void setProfileRepository(Repository pProfileRepository) {
    mProfileRepository = pProfileRepository;
  }

  /**
   * Return the ProfileRepository property.
   * @return a <code>Repository</code> value
   */
  public Repository getProfileRepository() {
    return mProfileRepository;
  }

  //---------------------------------------------------------------------------
  // property: commerceProfileTools
  //---------------------------------------------------------------------------
  CommerceProfileTools mCommerceProfileTools;

  /**
   * Set the CommerceProfileTools property.
   * @param pCommerceProfileTools a <code>CommerceProfileTools</code> value
   */
  public void setCommerceProfileTools(CommerceProfileTools pCommerceProfileTools) {
    mCommerceProfileTools = pCommerceProfileTools;
  }

  /**
   * Return the CommerceProfileTools property.
   * @return a <code>CommerceProfileTools</code> value
   */
  public CommerceProfileTools getCommerceProfileTools() {
    return mCommerceProfileTools;
  }

  //---------------------------------------------------------------------------
  // property: Order
  //---------------------------------------------------------------------------
  Order mOrder;

  /**
   * Set the Order property.
   * @param pOrder an <code>Order</code> value
   */
  public void setOrder(Order pOrder) {
    mOrder = pOrder;
  }

  /**
   * Return the Order property.
   * @return an <code>Order</code> value
   */
  public Order getOrder() {
    if (mOrder != null)
      return mOrder;
    else
      return getShoppingCart().getCurrent();
  }

  //-------------------------------------
  // property: ShoppingCart
  //-------------------------------------
  OrderHolder mShoppingCart;

  /**
   * Sets property ShoppingCart
   *
   * @param pShoppingCart an <code>OrderHolder</code> value
   */
  public void setShoppingCart(OrderHolder pShoppingCart) {
    mShoppingCart = pShoppingCart;
  }

  /**
   * Returns property ShoppingCart
   * @return an <code>OrderHolder</code> value
   *
   */
  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }

  //---------------------------------------------------------------------------
  // property: commerceItemManager
  //---------------------------------------------------------------------------
  CommerceItemManager mCommerceItemManager;

  /**
   * Set the CommerceItemManager property.
   * @param pCommerceItemManager a <code>CommerceItemManager</code> value
   */
  public void setCommerceItemManager(CommerceItemManager pCommerceItemManager) {
    mCommerceItemManager = pCommerceItemManager;
  }

  /**
   * Return the CommerceItemManager property.
   * @return a <code>CommerceItemManager</code> value
   */
  public CommerceItemManager getCommerceItemManager() {
    return mCommerceItemManager;
  }

  //---------------------------------------------------------------------------
  // property: OrderManager
  //---------------------------------------------------------------------------
  SimpleOrderManager mOrderManager;

  /**
   * Set the OrderManager property.
   * @param pOrderManager a <code>SimpleOrderManager</code> value
   */
  public void setOrderManager(SimpleOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * Return the OrderManager property.
   * @return a <code>SimpleOrderManager</code> value
   */
  public SimpleOrderManager getOrderManager() {
    return mOrderManager;
  }

  //---------------------------------------------------------------------------
  // property: commerceIdentifierPaymentInfoContainer
  //---------------------------------------------------------------------------
  CommerceIdentifierPaymentInfoContainer mCommerceIdentifierPaymentInfoContainer;

  /**
   * Set the CommerceIdentifierPaymentInfoContainer property.
   * @param pCommerceIdentifierPaymentInfoContainer a <code>CommerceIdentifierPaymentInfoContainer</code> value
   */
  public void setCommerceIdentifierPaymentInfoContainer(CommerceIdentifierPaymentInfoContainer pCommerceIdentifierPaymentInfoContainer) {
    mCommerceIdentifierPaymentInfoContainer = pCommerceIdentifierPaymentInfoContainer;
  }

  /**
   * Return the CommerceIdentifierPaymentInfoContainer property.
   * @return a <code>CommerceIdentifierPaymentInfoContainer</code> value
   */
  public CommerceIdentifierPaymentInfoContainer getCommerceIdentifierPaymentInfoContainer() {
    return mCommerceIdentifierPaymentInfoContainer;
  }

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

  //---------------------------------------------------------------------------
  // property: paymentGroupMapContainer
  //---------------------------------------------------------------------------
  PaymentGroupMapContainer mPaymentGroupMapContainer;

  /**
   * Set the PaymentGroupMapContainer property.
   * @param pPaymentGroupMapContainer a <code>PaymentGroupMapContainer</code> value
   */
  public void setPaymentGroupMapContainer(PaymentGroupMapContainer pPaymentGroupMapContainer) {
    mPaymentGroupMapContainer = pPaymentGroupMapContainer;
  }

  /**
   * Return the PaymentGroupMapContainer property.
   * @return a <code>PaymentGroupMapContainer</code> value
   */
  public PaymentGroupMapContainer getPaymentGroupMapContainer() {
    return mPaymentGroupMapContainer;
  }

  //-------------------------------------
  // property: PipelineManager
  //-------------------------------------
  PipelineManager mPipelineManager;

  /**
   * Sets property PipelineManager
   *
   * @param pPipelineManager a <code>PipelineManager</code> value
   */
  public void setPipelineManager(PipelineManager pPipelineManager) {
    mPipelineManager = pPipelineManager;
  }

  /**
   * Returns property PipelineManager
   * @return a <code>PipelineManager</code> value
   *
   */
  public PipelineManager getPipelineManager() {
    return mPipelineManager;
  }

  //-------------------------------------
  private Transaction mCurrentTransaction = null;

  /**
   * Returns property currentTransaction
   *
   * @return returns property currentTransaction
   */
  public Transaction getCurrentTransaction() {
    try{
      TransactionManager tm = getTransactionManager();
      if (tm == null) {
        if (isLoggingError()) {
          logError(ResourceUtils.getMsgResource("missingTransactionManager",
                                                getResourceBundleName(), getResourceBundle()));
        }
        return null;
      }
      Transaction t = tm.getTransaction();

      return t;
    }
    catch (SystemException exc) {
      if (isLoggingError())
        logError(exc);
    }
    return null;

  }

  /**
   * Sets property CurrentTransaction
   *
   * @param pCurrentTransaction a <code>Transaction</code> value
   */
  public void setCurrentTransaction(Transaction pCurrentTransaction) {
    mCurrentTransaction = pCurrentTransaction;
  }

  //-------------------------------------
  // property: TransactionManager
  //-------------------------------------
  TransactionManager mTransactionManager;

  /**
   * Sets property TransactionManager
   *
   * @param pTransactionManager a <code>TransactionManager</code> value
   */
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   * Returns property TransactionManager
   *
   * @return a <code>TransactionManager</code> value
   */
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  //-------------------------------------
  // property: EnsureTransaction
  //-------------------------------------
  boolean mEnsureTransaction = true;
  private boolean mEnsureTransactionSet = false;

  /**
   * Sets property EnsureTransaction
   *
   * @param pEnsureTransaction a <code>boolean</code> value
   */
  public void setEnsureTransaction(boolean pEnsureTransaction) {
    mEnsureTransaction = pEnsureTransaction;
    mEnsureTransactionSet = true;
  }

  /**
   * Returns property EnsureTransaction
   *
   * @return a <code>boolean</code> value
   */
  public boolean isEnsureTransaction() {
    return mEnsureTransaction;
  }

  //---------------------------------------------------------------------------
  // property: claimableManager
  //---------------------------------------------------------------------------
  ClaimableManager mClaimableManager;

  /**
   * Specifies the ClaimableManager to use in determining the user's GiftCertificates.
   * @param pClaimableManager a <code>ClaimableManager</code> value
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }

  /**
   * Returns the ClaimableManager is used in determining the user's GiftCertificates.
   * @return a <code>ClaimableManager</code> value
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }

  //---------------------------------------------------------------------------
  // property: giftlistManager
  //---------------------------------------------------------------------------
  private GiftlistManager mGiftlistManager;

  /**
   * Specifies the GiftlistManager.
   * @param pGiftlistManager a <code>GiftlistManager</code> value
   */
  public void setGiftlistManager(GiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }

  /**
   * The giftlist manager
   * @return a <code>GiftlistManager</code> value
   *
   */
  public GiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }

  //---------------------------------------------------------------------------
  // property: handlingInstructionManager
  //---------------------------------------------------------------------------
  private HandlingInstructionManager mHandlingInstructionManager;

  /**
   * Specifies the HandlingInstructionManager.
   * @param pHandlingInstructionManager a <code>HandlingInstructionManager</code> value
   */
  public void setHandlingInstructionManager(HandlingInstructionManager pHandlingInstructionManager) {
    mHandlingInstructionManager = pHandlingInstructionManager;
  }

  /**
   * The HandlingInstruction manager
   * @return a <code>HandlingInstructionManager</code> value
   *
   */
  public HandlingInstructionManager getHandlingInstructionManager() {
    return mHandlingInstructionManager;
  }

  //---------------------------------------------------------------------------
  // property: shippingGroupManager
  //---------------------------------------------------------------------------
  ShippingGroupManager mShippingGroupManager;

  /**
   * Set the ShippingGroupManager property.
   * @param pShippingGroupManager a <code>ShippingGroupManager</code> value
   */
  public void setShippingGroupManager(ShippingGroupManager pShippingGroupManager) {
    mShippingGroupManager = pShippingGroupManager;
  }

  /**
   * Return the ShippingGroupManager property.
   * @return a <code>ShippingGroupManager</code> value
   */
  public ShippingGroupManager getShippingGroupManager() {
    return mShippingGroupManager;
  }

  //---------------------------------------------------------------------------
  // property: commerceItemShippingInfoContainer
  //---------------------------------------------------------------------------
  CommerceItemShippingInfoContainer mCommerceItemShippingInfoContainer;

  /**
   * Set the CommerceItemShippingInfoContainer property.
   * @param pCommerceItemShippingInfoContainer a <code>CommerceItemShippingInfoContainer</code> value
   */
  public void setCommerceItemShippingInfoContainer(CommerceItemShippingInfoContainer pCommerceItemShippingInfoContainer) {
    mCommerceItemShippingInfoContainer = pCommerceItemShippingInfoContainer;
  }

  /**
   * Return the CommerceItemShippingInfoContainer property.
   * @return a <code>CommerceItemShippingInfoContainer</code> value
   */
  public CommerceItemShippingInfoContainer getCommerceItemShippingInfoContainer() {
    return mCommerceItemShippingInfoContainer;
  }

  //---------------------------------------------------------------------------
  // property: shippingGroupMapContainer
  //---------------------------------------------------------------------------
  ShippingGroupMapContainer mShippingGroupMapContainer;

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

  //---------------------------------------------------------------------------
  // property: repeatingRequestMonitor
  //---------------------------------------------------------------------------
  RepeatingRequestMonitor mRepeatingRequestMonitor;

  /**
   * Set the RepeatingRequestMonitor property.
   * @param pRepeatingRequestMonitor a <code>RepeatingRequestMonitor</code> value
   */
  public void setRepeatingRequestMonitor(RepeatingRequestMonitor pRepeatingRequestMonitor) {
    mRepeatingRequestMonitor = pRepeatingRequestMonitor;
  }

  /**
   * Return the RepeatingRequestMonitor property.
   * @return a <code>RepeatingRequestMonitor</code> value
   */
  public RepeatingRequestMonitor getRepeatingRequestMonitor() {
    return mRepeatingRequestMonitor;
  }

  //---------------------------------------------------------------------------
  // property: SessionExpirationURL
  //---------------------------------------------------------------------------
  String mSessionExpirationURL;

  /**
   * Set the SessionExpirationURL property.
   * @param pSessionExpirationURL a <code>String</code> value
   */
  public void setSessionExpirationURL(String pSessionExpirationURL) {
    mSessionExpirationURL = pSessionExpirationURL;
  }

  /**
   * Return the SessionExpirationURL property.
   * @return a <code>String</code> value
   */
  public String getSessionExpirationURL() {
    return mSessionExpirationURL;
  }

  //-------------------------------------
  // property: userPricingModels
  //-------------------------------------
  PricingModelHolder mUserPricingModels;

  /**
   * Sets property UserPricingModels
   *
   * @param pUserPricingModels a <code>PricingModelHolder</code> value
   */
  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  /**
   * Returns property UserPricingModels
   * @return a <code>PricingModelHolder</code> value
   */
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }


  //-------------------------------------
  // property: purchaseProcessHelper
  //-------------------------------------
  PurchaseProcessHelper mPurchaseProcessHelper;

  /**
   * Sets property purchaseProcessHelper
   *
   * @param pPurchaseProcessHelper a <code>PurchaseProcessHelper</code> value
   */
  public void setPurchaseProcessHelper(PurchaseProcessHelper pPurchaseProcessHelper) {
    mPurchaseProcessHelper = pPurchaseProcessHelper;
  }

  /**
   * Returns property purchaseProcessHelper
   * @return a <code>PurchaseProcessHelper</code> value
   */
  public PurchaseProcessHelper getPurchaseProcessHelper() {
    return mPurchaseProcessHelper;
  }

  //-------------------------------------
  // ResourceBundle support
  //-------------------------------------

  /**
   * Returns the error message ResourceBundle
   */
  protected ResourceBundle getResourceBundle() {
    return sResourceBundle;
  }

  /**
   * This method returns ResourceBundle object for specified locale.
   *
   * @param pLocale The locale used to retrieve the resource bundle. If <code>null</code>
   *                then the default resource bundle is returned.
   *
   * @return the resource bundle.
   *
   */
  protected ResourceBundle getResourceBundle(Locale pLocale) {
    if (pLocale == null) {
      return getResourceBundle();
    }

    ResourceBundle rb = atg.core.i18n.LayeredResourceBundle.getBundle(getResourceBundleName(), pLocale);
    return rb;
  }

  /**
   * Returns the name of the error message ResourceBundle
   */
  protected String getResourceBundleName() {
    return MY_RESOURCE_NAME;
  }

  //-------------------------------------
  // User Utils
  //-------------------------------------

  /**
   * If the request coming in is for a new session (which would indicate that
   * the session has expired or been failed over) AND is a form submission
   * then we optionally redirect to a session expiration url.
   * If the transaction is marked as rollback, then redirect to the FailureURL,
   * otherwise allow the super-class behavior to perform.
   * @param pSuccessURL a <code>String</code> value
   * @param pFailureURL a <code>String</code> value
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return If redirect (for whatever reason) to a new page occurred,
   *         return false.  If NO redirect occurred, return true.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean checkFormRedirect (String pSuccessURL,
                                    String pFailureURL,
                                    DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
                                    throws ServletException,
                                           IOException
  {
    if ((getSessionExpirationURL() != null) &&
        (pRequest.getSession().isNew()) && (isFormSubmission(pRequest))) {

      RepositoryItem profile;

      if ((profile = getProfile()) == null)
        if ((profile = defaultUserProfile(true)) == null)
          if (isLoggingError())
            logError(ResourceUtils.getMsgResource("missingExpiredSessionProfile",
                                                  getResourceBundleName(), getResourceBundle()));

      if (profile != null && profile.isTransient()) {
        // The session has expired, redirect to error page
        if (isLoggingDebug())
          logDebug("Session expired: redirecting to " +  getSessionExpirationURL());
        redirectOrForward(pRequest, pResponse, getSessionExpirationURL());
        return false;
      }
    }

    //If form errors were found:
    if (isTransactionMarkedAsRollBack()) {
      //If FailureURL was not passed in, stay on same page, return true:
      if (pFailureURL == null) {
        if (isLoggingDebug()) {
          logDebug("Transaction Marked as Rollback - staying on same page.");
        }
        return true;
      }

      //If FailureURL was passed in, redirect and return false:
      else {
        if (isLoggingDebug()) {
          logDebug("Transaction Marked as Rollback - redirecting to: " + pFailureURL);
        }
        redirectOrForward(pRequest, pResponse, pFailureURL);
        return false;
      }
    }
    else {
      return super.checkFormRedirect(pSuccessURL, pFailureURL, pRequest, pResponse);
    }
  }


  /**
   * If it is a form submission then return true.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @return a <code>boolean</code> value
   */
  public boolean isFormSubmission(DynamoHttpServletRequest pRequest) {
    String args = pRequest.getQueryParameter(DropletConstants.DROPLET_ARGUMENTS);
    if (args == null) return false;
    return true;
  }

  /**
   * Returns a Nucleus property path which can be used in a DropletFormException
   * @param pPropertyName a <code>String</code> value
   * @return a <code>String</code> value
   */
  public String generatePropertyPath(String pPropertyName) {
    return getAbsoluteName() + "." + pPropertyName;
  }

  /**
   * Add a user error message to the form exceptions, and then display
   * the exception in the logs
   * @param pException an <code>Exception</code> value
   * @param pMsgId a <code>String</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void processException(Exception pException, String pMsgId,
                               DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    String msg = null;

    if (ConcurrentUpdateDetector.isConcurrentUpdate(pException)) {
      Order order = getOrder();
      if (order instanceof OrderImpl) {
        ((OrderImpl) order).invalidateOrder();
      }
      if (order != null) {
        msg = formatUserMessage("ConcurrentUpdateAttempt", order.getId(), pRequest, pResponse);
      }
      if (isLoggingWarning()) logWarning(pException);
    }
    else {
      if (isLoggingError()) logError(pException);
    }

    if (msg == null)
      msg = formatUserMessage(pMsgId, pRequest, pResponse);
    String propertyPath = generatePropertyPath("order");
    addFormException(new DropletFormException(msg, pException, propertyPath, pMsgId));

  }

  /**
   * Returns the locale associated with the request. The method first searches
   * for a request paramater named <code>locale</code>. This value can be
   * either a java.util.Locale object or a String which represents the locale.
   * Next if the <code>useRequestLocale</code> property is true, then the locale
   * of the request will be returned. Finally, if the locale cannot be determined,
   * the the <code>defaultLocale</code> property is used.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>Locale</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public Locale getUserLocale(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object obj = pRequest.getObjectParameter(LOCALE_PARAM);
    if (obj instanceof Locale)
      return (Locale)obj;
    else if (obj instanceof String)
      return RequestLocale.getCachedLocale((String)obj);
    else if (isUseRequestLocale()) {
      RequestLocale requestLocale = pRequest.getRequestLocale();
      if (requestLocale != null)
        return requestLocale.getLocale();
    }

    return getDefaultLocale();
  }

  /**
   * Utility method to format a message with no arguments using the Locale of the user
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pRequest the request object which can be used to extract the user's locale
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return the formatted message
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @see PurchaseUserMessage
   *
   */
  public String formatUserMessage(String pKey,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return PurchaseUserMessage.format(pKey, getUserLocale(pRequest, pResponse));
  }

  /**
   * Utility method to format a message with one argument using the Locale of the user
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParam the first (and only argument) in the message
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return the formatted message
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @see PurchaseUserMessage
   *
   */
  public String formatUserMessage(String pKey, Object pParam,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return PurchaseUserMessage.format(pKey, pParam, getUserLocale(pRequest, pResponse));
  }

  /**
   * Utility method to format a message with two arguments using our resource bundle.
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParam1 the first parameter in the message
   * @param pParam2 the second parameter in the message
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return the formatted message
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @see PurchaseUserMessage
   *
   */
  public String formatUserMessage(String pKey, Object pParam1, Object pParam2,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object[] params = { pParam1, pParam2, };
    return PurchaseUserMessage.format(pKey, params, getUserLocale(pRequest, pResponse));
  }

  /**
   * Utility method to format a message with two arguments using our resource bundle.
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParams a set of parameters to use in the formatting.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return the formatted message
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @see PurchaseUserMessage
   *
   */
  public String formatUserMessage(String pKey, Object [] pParams,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return PurchaseUserMessage.format(pKey, pParams, getUserLocale(pRequest, pResponse));
  }

  // -------------------------------
  // Pipeline Management Utilities
  // -------------------------------

  /**
   * Executes a Pipeline Chain and places the supplies method parameters into a HashMap which
   * is supplied to the chain to execute.
   * @param pChainId the pipeline chain to execute
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   * @return a <code>PipelineResult</code> value
   * @exception RunProcessException if an error occurs
   */
  protected PipelineResult runProcess(String pChainId, Order pOrder, PricingModelHolder pPricingModels,
                                      Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    return runProcess(pChainId, pOrder, pPricingModels, pLocale, pProfile, null, pExtraParameters);
  }

  /**
   * Executes a Pipeline Chain and places the supplies method parameters into a HashMap which
   * is supplied to the chain to execute.
   * @param pChainId the pipeline chain to execute
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pParameters A Map of parameters to be used when calling the PipelineChain
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   * @return a <code>PipelineResult</code> value
   * @exception RunProcessException if an error occurs
   */
  protected PipelineResult runProcess(String pChainId, Order pOrder, PricingModelHolder pPricingModels,
                                      Locale pLocale, RepositoryItem pProfile, Map pParameters,
                                      Map pExtraParameters)
    throws RunProcessException
  {
    if (pChainId == null) {
      if (isLoggingDebug()) {
        logDebug("runProcess skipped because chain ID is null");
      }
      return null;
    }
    Map params = null;
    if (pParameters == null)
      params = new HashMap();
    else
      params = pParameters;

    try {
      OrderManager orderManager = getOrderManager();
      OrderTools orderTools = orderManager.getOrderTools();

      params.put(PricingConstants.ORDER_PARAM, pOrder);
      params.put(PricingConstants.PRICING_MODELS_PARAM, pPricingModels);
      params.put(PricingConstants.LOCALE_PARAM, pLocale);
      params.put(PricingConstants.PROFILE_PARAM, pProfile);
      params.put(PipelineConstants.ORDERMANAGER, orderManager);
      params.put(PipelineConstants.CATALOGTOOLS, orderTools.getCatalogTools());
      params.put(PipelineConstants.INVENTORYMANAGER, orderTools.getInventoryManager());
      params.put(PricingConstants.EXTRA_PARAMETERS_PARAM, pExtraParameters);
      return runProcess(pChainId, params);
    }
    catch (RunProcessException exc) {
      throw exc;
    }
  }

  /**
   * Executes a Pipeline Chain.
   * @param pChainId the pipeline chain to execute
   * @param pParameters A Map of parameters to be passed to the chain during execution
   * @return a <code>PipelineResult</code> value
   * @exception RunProcessException if an error occurs
   */
  protected PipelineResult runProcess(String pChainId, Map pParameters)
    throws RunProcessException
  {
    if (isLoggingDebug()) {
      logDebug("runProcess called with chain ID = " + pChainId);
    }

    if (pChainId == null) {
      return null;
    }
    try {
      return getPipelineManager().runProcess(pChainId, pParameters);
    }
    catch (RunProcessException exc) {
      try {
        setTransactionToRollbackOnly();
      }
      catch(SystemException e) {
        if(isLoggingError()) {
          logError(e);
        }
      }
      throw exc;
    }
  }

  /**
   * Examine the pipeline result looking for any error messages, creates DropletFormExceptions
   * and adds them as form exceptions
   * @param pResult a <code>PipelineResult</code> value
   * @return true if errors were found
   */
  protected boolean processPipelineErrors(PipelineResult pResult) {
    if (pResult == null)
      return false;
    return PipelineUtils.processPipelineErrors(pResult, this, this);
  }

  /**
   * Run the pipeline which should be executed when the order needs to be repriced. This method
   * call defaults to the the pricing operation ORDER_TOTAL.
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   * @exception RunProcessException if an error occurs
   * @see atg.commerce.pricing.PricingConstants#OP_REPRICE_ORDER_TOTAL
   */
  protected void runProcessRepriceOrder(Order pOrder, PricingModelHolder pPricingModels,
                                        Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
      runProcessRepriceOrder(PricingConstants.OP_REPRICE_ORDER_TOTAL, pOrder, pPricingModels, pLocale, pProfile, pExtraParameters);
  }

  /**
   * Run the pipeline which should be executed when the order needs to be repriced
   * @param pPricingOperation the pricing operation (e.g. order total, subtotal, shipping, etc) that
   * should be performed.
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessRepriceOrder(String pPricingOperation,
                                        Order pOrder, PricingModelHolder pPricingModels,
                                        Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
      PipelineResult result = runRepricingProcess(getRepriceOrderChainId(), pPricingOperation,
          pOrder, pPricingModels, pLocale,
          pProfile, pExtraParameters);

      processPipelineErrors(result);
  }

  /**
   * Executes a Pipeline Chain and places the supplies method parameters into a HashMap which
   * is supplied to the chain to execute.
   * @param pChainId the pipeline chain to execute
   * @param pPricingOperation a <code>String</code> value
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   * @return a <code>PipelineResult</code> value
   * @exception RunProcessException if an error occurs
   */
  protected PipelineResult runRepricingProcess(String pChainId, String pPricingOperation,
                                               Order pOrder, PricingModelHolder pPricingModels,
                                               Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {


      return (getPurchaseProcessHelper().runRepricingProcess(pChainId, pPricingOperation,
                                      pOrder, pPricingModels, pLocale,
                                      pProfile, pExtraParameters));
  }

  // -------------------------------
  // Transaction Management Utilities
  // -------------------------------

  /**
   * This method ensures that a transaction exists before returning.
   * If there is no transaction, a new one is started and returned.  In
   * this case, you must call commitTransaction when the transaction
   * completes.
   * @return a <code>Transaction</code> value
   */
  protected Transaction ensureTransaction()
  {
    if (! isEnsureTransaction())
      return null;

    try {
      TransactionManager tm = getTransactionManager();
      /*
       * If we are not configured with a transaction manager, we just
       * have to skip this.
       */
      if (tm == null) {
        if (isLoggingError()) {
          logError(ResourceUtils.getMsgResource("missingTransactionManager",
                                                getResourceBundleName(), getResourceBundle()));
        }
        return null;
      }
      Transaction t = tm.getTransaction();
      if (t == null) {
        tm.begin();
        t = tm.getTransaction();
        return t;
      }
      return null;
    }
    catch (NotSupportedException exc) {
      if (isLoggingError())
        logError(exc);
    }
    catch (SystemException exc) {
      if (isLoggingError())
        logError(exc);
    }
    return null;
  }

  /**
   * Commits the supplied transaction
   * @param pTransaction a <code>Transaction</code> value
   */
  protected void commitTransaction(Transaction pTransaction)
  {
   boolean exception = false;

   if (pTransaction != null) {
      try {
        TransactionManager tm = getTransactionManager();
        if (isTransactionMarkedAsRollBack())  {
          if (tm != null){
            tm.rollback();
          }else{
            pTransaction.rollback();  // PR65109: rollback() before invalidateOrder() prevent deadlocks due to thread synchronization in the invalidateOrder()
          }
          if (getOrder() instanceof OrderImpl)
            ((OrderImpl)getOrder()).invalidateOrder();
        }
        else {
          if (tm != null){
            tm.commit();
          }else{
            pTransaction.commit();
          }
        }
      }
      catch (RollbackException exc) {
        exception = true;
        if (isLoggingError())
          logError(exc);
      }
      catch (HeuristicMixedException exc) {
        exception = true;
        if (isLoggingError())
          logError(exc);
      }
      catch (HeuristicRollbackException exc) {
        exception = true;
        if (isLoggingError())
          logError(exc);
      }
      catch (SystemException exc) {
        exception = true;
        if (isLoggingError())
          logError(exc);
      }
      finally {
        if (exception) {
          if (getOrder() instanceof OrderImpl)
            ((OrderImpl)getOrder()).invalidateOrder();

        } // if
      } // finally
    } // if
  }

  /**
   * Sets the transaction to rollback
   * @exception SystemException if an error occurs
   */
  protected void setTransactionToRollbackOnly() throws SystemException
  {
    TransactionManager tm = getTransactionManager();
    if(tm != null) {
      Transaction t = tm.getTransaction();
      if(t != null) {
        t.setRollbackOnly();
      }
    }
  }

  /**
   * Returns true if the transaction associated with the current thread
   * is marked for rollback. This is useful if you do not want to perform
   * some action (e.g. updating the order) if some other subservice already
   * needs the transaction rolledback.
   * @return a <code>boolean</code> value
   */
  protected boolean isTransactionMarkedAsRollBack() {
    try {
      TransactionManager tm = getTransactionManager();
      if (tm != null) {
        int transactionStatus = tm.getStatus();
        if (transactionStatus == javax.transaction.Status.STATUS_MARKED_ROLLBACK)
          return true;
      }
    }
    catch (SystemException exc) {
      if (isLoggingError())
        logError(exc);
    }
    return false;
  }

  //---------------------------------------------------------------------------

  /**
   * Attempt to acquire a local lock before creating a transaction
   * that may modify the order, but only if
   * <code>useLocksAroundTransactions</code> is set to true.
   *
   * @deprecated please use acquireTransactionLock(DynamoHttpServletRequest pRequest)
   *             instead
   **/

  protected void acquireTransactionLock()
    throws DeadlockException
  {
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    acquireTransactionLock(request);
  }

  /**
   * Attempt to acquire a local lock before creating a transaction
   * that may modify the order, but only if
   * <code>useLocksAroundTransactions</code> is set to true.
   **/

  protected void acquireTransactionLock(DynamoHttpServletRequest pRequest)
    throws DeadlockException
  {
    try {
      TransactionLockService service = getTransactionLockService();
      if(service != null) {
        RepositoryItem profileItem = getProfile();
        if (profileItem != null) {
          String profileId = profileItem.getRepositoryId();
          pRequest.setAttribute(LOCK_NAME_ATTRIBUTE_NAME, profileId);
          service.acquireTransactionLock(profileId);
        }
        else {
          service.acquireTransactionLock();
        }
      }
    } catch(NoLockNameException exc) {
      if(isLoggingError())
        logError(exc);
    }
  }

  //---------------------------------------------------------------------------

  /**
   * Attempt to release the local lock that was acquired before creating
   * a transaction that may have modified the order, but only if
   * <code>useLocksAroundTransactions</code> is set to true.
   *
   * @deprecated please use releaseTransactionLock(DynamoHttpServletRequest pRequest)
   *             instead
   **/

   protected void releaseTransactionLock()
    throws LockManagerException
  {
    DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
    releaseTransactionLock(request);
  }

  /**
   * Attempt to release the local lock that was acquired before creating
   * a transaction that may have modified the order, but only if
   * <code>useLocksAroundTransactions</code> is set to true.
   **/

   protected void releaseTransactionLock(DynamoHttpServletRequest pRequest)
    throws LockManagerException
  {
    try {
      TransactionLockService service = getTransactionLockService();
      if(service != null) {
        String lockName = (String) pRequest.getAttribute(LOCK_NAME_ATTRIBUTE_NAME);
        if (lockName != null) {
          service.releaseTransactionLock(lockName);
          pRequest.removeAttribute(LOCK_NAME_ATTRIBUTE_NAME);
        }
        else {
          service.releaseTransactionLock();
        }
      }
    } catch(LockManagerException exc) {
      if(isLoggingError())
        logError(exc);
    }
  }

  private TransactionLockService mTransactionLockService = null;

  /**
   * This method will us the TransactionLockFactory (if it has not already done so)
   * to get a TransactionLockService.  If necessary it will configure the "enabled" property
   * of the service.
   */
  private TransactionLockService getTransactionLockService() {
    if(mTransactionLockService != null)
      return mTransactionLockService;

    TransactionLockFactory factory = getConfiguration().getTransactionLockFactory();

    if(factory != null)
      mTransactionLockService = factory.getServiceInstance(this);
    else
      if(isLoggingWarning())
        logWarning(ResourceUtils.getMsgResource("missingTransactionLockFactory",
                                                getResourceBundleName(), getResourceBundle()));

    // use the default setting of the TransactionLockService unless we have been
    // explicitly configured
    if(mUseLocksAroundTransactionsSet)
      mTransactionLockService.setEnabled(isUseLocksAroundTransactions());

    return mTransactionLockService;
  }

  //---------------------------------------------------------------------------

  /**
   * Generate the lock name to use when acquiring and releasing locks
   * in <code>acquireTransactionLock</code> and <code>releaseTransactionLock</code>.
   * By default this returns the repository id of the current user profile,
   * generating an error message and returning null if no profile is available.
   * Subclasses may override this behavior to choose a different locking
   * strategy.
   *
   * @return
   *    The lock name to use when acquiring and releasing locks, or
   *    null if no lock name is available.  In this case locking will
   *    be disabled for the current form submission.
   *
   * @deprecated Use the TransactionLockFactory instead
   **/

  protected String transactionLockName()
  {
    RepositoryItem profile;

    if ((profile = getProfile()) == null) {
      if ((profile = defaultUserProfile(true)) == null) {
        if (isLoggingError())
          logError(ResourceUtils.getMsgResource("missingUseLocksProfile",
                                                getResourceBundleName(), getResourceBundle()));
        return null;
      }
    }

    return profile.getRepositoryId();
  }

  //---------------------------------------------------------------------------

  /**
   * Called before any setX methods on this form are set when a form
   * that modifies properties of this form handler is submitted.
   * Creates a transaction if necessary at the beginning of the form
   * submission process, optionally obtaining a local lock to prevent
   * multiple forms from creating transactions that may modify the
   * same order.
   *
   * @see #afterSet
   * @see #ensureTransaction
   * @see #setUseLocksAroundTransactions
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception DropletFormException if an error occurs
   */

  public boolean beforeSet(DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse) throws DropletFormException
  {
    try {
      acquireTransactionLock(pRequest);
    }
    catch (DeadlockException de) {

      // We are going to log the exception here and then ignore it because
      // the worst that should happen is that the user will get a concurrent
      // update exception if two threads try to modify the same order, and we
      // can recover from that.

      if (isLoggingError())
        logError(de);
    }

    Transaction t = ensureTransaction();
    if (t != null)
      setTransactionCreated(pRequest, pResponse);

    if (isLoggingDebug()) {
      if (t != null)
        logDebug("beforeSet created transaction " + t);
      else
        logDebug("beforeSet did not create a transaction.");
    }

    return super.beforeSet(pRequest,pResponse);
  }


  //---------------------------------------------------------------------------

  /**
   * Called after any setX methods on this form are set when a form
   * that modifies properties of this form handler is submitted.
   * Commits or rolls back any transaction created in beforeSet,
   * and releases any lock that was acquired at the time.
   *
   * @see #beforeSet
   * @see #ensureTransaction
   * @see #setUseLocksAroundTransactions
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception DropletFormException if an error occurs
   */

  public boolean afterSet(DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pResponse) throws DropletFormException
  {
    try
    {
      Transaction t = getCurrentTransaction();

      if (isLoggingDebug()) {
        if (t != null)
          logDebug("afterSet sees currentTransaction as " + t);
        else
          logDebug("afterSet sees no current transaction.");
      }

      // Try to keep the response to this page from being cached.
      ServletUtil.setNoCacheHeaders(pResponse);

      if (t != null && isTransactionCreated(pRequest, pResponse)) {
        if (isLoggingDebug())
          logDebug("afterSet committing transaction " + t);
        commitTransaction(t);
        unsetTransactionCreated(pRequest, pResponse);
      }
    }

    // Release the transaction lock in a finally clause in case any of the code
    // above throws exceptions.  We don't want to end up holding the lock forever
    // in this case.

    finally
    {
      try {
        releaseTransactionLock(pRequest);
      }
      catch (LockManagerException lme) {
        if (isLoggingError())
          logError(lme);
      }
    }

    return super.afterSet(pRequest,pResponse);
  }

  /*
   * Utility method to set the PARAM_TRANSACTION_CREATED to  true in the
   * request. This method is typically used by the beforeSet method to
   * indicate that this formhandler actually created the transaction, so
   * that afterSet method can commit the transaction.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   */
  protected void setTransactionCreated(DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
  {
    pRequest.setParameter(PARAM_TRANSACTION_CREATED, PARAM_VALUE_TRUE);
  }

  /*
   * this method checks whether the parameter PARAM_TRANSACTION_CREATED in
   * the request is true or not.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if the parameter PARAM_TRANSACTION_CREATED is present
   *              and true otherwise returns false
   */
  protected boolean isTransactionCreated(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
  {
    String txCreated = pRequest.getParameter(PARAM_TRANSACTION_CREATED);
    if ( txCreated != null && txCreated.equals(PARAM_VALUE_TRUE)) {
      return true;
    } // end of if ()
    return false;
  }

  /* this method removes the parameter PARAM_TRANSACTION_CREATED from the
   * request, this is typically used by the afterSet method which actually
   * removes this parameter once it commits the transaction.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   */
  protected void unsetTransactionCreated(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
  {
    pRequest.removeParameter(PARAM_TRANSACTION_CREATED);
  }

  /**
   * Add a pipeline error to the list of form exceptions.
   *
   * @param pError the error reported by the pipeline.
   * @param pErrorKey the key of the error, used for localization.
   */
  public void handlePipelineError(Object pError, String pErrorKey) {
    addFormException(new DropletException(pError.toString(), pErrorKey));
  }


  /**
   * Creates the extra parameter map that is used when the order is repriced after
   * form handler modifications to the order.
   *
   * @return Map of parameters or null
   */
  protected Map createRepriceParameterMap()
  {
      RepositoryItem pricelist = getPriceList();
      if(pricelist == null)
          return null;
      else
      {
          HashMap parameters = new HashMap();
          //add an entry for the price list
          CommercePropertyManager props = getCommercePropertyManager();
          if(props != null)
          {
              String priceListPropertyName = getCommercePropertyManager().getPriceListPropertyName();
              parameters.put(priceListPropertyName,pricelist);
          }
          return parameters;
      }
  }




  // -------------------------------
  // Centralized configuration
  // -------------------------------

  /**
   * Copy property settings from the optional
   * <code>PurchaseProcessConfiguration</code> component. Property
   * values that were configured locally are preserved.
   *
   * Configures the following properties (if not already set):
   * <UL>
   * <LI>claimableManager
   * <LI>commerceIdentifierPaymentInfoContainer
   * <LI>commerceItemManager
   * <LI>commerceItemShippingInfoContainer
   * <LI>commerceProfileTools
   * <LI>ensureTransaction
   * <LI>giftlistManager
   * <LI>handlingInstructionManager
   * <LI>localLockManager
   * <LI>orderManager
   * <LI>paymentGroupManager
   * <LI>paymentGroupMapContainer
   * <LI>pipelineManager
   * <LI>profile
   * <LI>profileRepository
   * <LI>repeatingRequestMonitor
   * <LI>repriceOrderChainId
   * <LI>sessionExpirationURL
   * <LI>shippingGroupManager
   * <LI>shippingGroupMapContainer
   * <LI>shoppingCart
   * <LI>transactionManager
   * <LI>useLocksAroundTransactions
   * <LI>userPricingModels
   * <LI>purchaseProcessHelper
   * </UL>
   **/
  protected void copyConfiguration() {
    if (mConfiguration != null) {
      if (getClaimableManager() == null) {
        setClaimableManager(mConfiguration.getClaimableManager());
      }
      if (getCommerceIdentifierPaymentInfoContainer() == null) {
        setCommerceIdentifierPaymentInfoContainer(mConfiguration.getCommerceIdentifierPaymentInfoContainer());
      }
      if (getCommerceItemManager() == null) {
        setCommerceItemManager(mConfiguration.getCommerceItemManager());
      }
      if (getCommerceItemShippingInfoContainer() == null) {
        setCommerceItemShippingInfoContainer(mConfiguration.getCommerceItemShippingInfoContainer());
      }
      if (getCommerceProfileTools() == null) {
        setCommerceProfileTools(mConfiguration.getCommerceProfileTools());
      }
      if (!mEnsureTransactionSet) {
        setEnsureTransaction(mConfiguration.isEnsureTransaction());
      }
      if (getGiftlistManager() == null) {
        setGiftlistManager(mConfiguration.getGiftlistManager());
      }
      if (getHandlingInstructionManager() == null) {
        setHandlingInstructionManager(mConfiguration.getHandlingInstructionManager());
      }
      if (getLocalLockManager() == null) {
        setLocalLockManager(mConfiguration.getLocalLockManager());
      }
      if (getOrderManager() == null) {
        setOrderManager(mConfiguration.getOrderManager());
      }
      if (getPaymentGroupManager() == null) {
        setPaymentGroupManager(mConfiguration.getPaymentGroupManager());
      }
      if (getPaymentGroupMapContainer() == null) {
        setPaymentGroupMapContainer(mConfiguration.getPaymentGroupMapContainer());
      }
      if (getPipelineManager() == null) {
        setPipelineManager(mConfiguration.getPipelineManager());
      }
      if (getProfile() == null) {
        setProfile(mConfiguration.getProfile());
      }
      if (getProfileRepository() == null) {
        setProfileRepository(mConfiguration.getProfileRepository());
      }
      if (getRepeatingRequestMonitor() == null) {
        setRepeatingRequestMonitor(mConfiguration.getRepeatingRequestMonitor());
      }
      if (getRepriceOrderChainId() == null) {
        setRepriceOrderChainId(mConfiguration.getRepriceOrderChainId());
      }
      if (getSessionExpirationURL() == null) {
        setSessionExpirationURL(mConfiguration.getSessionExpirationURL());
      }
      if (getShippingGroupManager() == null) {
        setShippingGroupManager(mConfiguration.getShippingGroupManager());
      }
      if (getShippingGroupMapContainer() == null) {
        setShippingGroupMapContainer(mConfiguration.getShippingGroupMapContainer());
      }
      if (getShoppingCart() == null) {
        setShoppingCart(mConfiguration.getShoppingCart());
      }
      if (getTransactionManager() == null) {
        setTransactionManager(mConfiguration.getTransactionManager());
      }
      if(mConfiguration.isUseLocksAroundTransactionExplicitlySet()) {
        // if we have not been explicitly set, and the Configuration has been, use the Configuration value
        if (!mUseLocksAroundTransactionsSet) {
          setUseLocksAroundTransactions(mConfiguration.isUseLocksAroundTransactions());
        }
      }
      if (getUserPricingModels() == null) {
        setUserPricingModels(mConfiguration.getUserPricingModels());
      }
      if (getPurchaseProcessHelper() == null) {
        setPurchaseProcessHelper(mConfiguration.getPurchaseProcessHelper());
      }

      if(getPriceListManager() == null){
          setPriceListManager(mConfiguration.getPriceListManager());
      }
      if(getCommercePropertyManager() == null){
          setCommercePropertyManager(mConfiguration.getCommercePropertyManager());
      }
    }
  }

  // -------------------------------
  // GenericService overrides
  // -------------------------------

  /**
   * Perform one-time startup operations, including copying property
   * settings from the optional <code>PurchaseProcessConfiguration</code>
   * component.
   */
  public void doStartService()
       throws ServiceException
  {
    super.doStartService();
    copyConfiguration();
  }

} // end of class
