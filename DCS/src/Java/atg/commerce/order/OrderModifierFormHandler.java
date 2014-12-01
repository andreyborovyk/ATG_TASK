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

package atg.commerce.order;

import atg.repository.RepositoryItem;
import atg.repository.RepositoryException;
import atg.droplet.GenericFormHandler;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.service.pipeline.*;
import atg.commerce.pricing.*;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.pricing.ItemPriceInfo;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.util.ConcurrentUpdateDetector;
import atg.commerce.CommerceException;
import atg.commerce.util.NoLockNameException;
import atg.commerce.util.PipelineUtils;
import atg.commerce.util.PipelineErrorHandler;
import atg.commerce.util.TransactionLockFactory;
import atg.commerce.util.TransactionLockService;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.Address;
import atg.nucleus.naming.ParameterName;
import atg.service.lockmanager.ClientLockManager;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.droplet.*;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.*;
import javax.transaction.*;

/**
 * This abstract class defines the FormHandling functionality necessary in order
 * to manager Order modifications for existing or new orders.
 *
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/OrderModifierFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public abstract class OrderModifierFormHandler
extends GenericFormHandler implements PipelineErrorHandler
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION =
  "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/OrderModifierFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");

  // Constants used for user-visible error messages
  protected static final String MSG_NO_ITEMS_TO_ADD = "noItemsToAdd";
  protected static final String MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO = "quantityLessThanOrEqualToZero";
  protected static final String MSG_NO_ORDER_TO_MODIFY = "noOrderToModify";
  protected static final String MSG_NO_SKU_WITH_ID = "noSKUWithId";
  protected static final String MSG_ERROR_ADDING_TO_ORDER = "errorAddingToOrder";
  protected static final String MSG_ERROR_REMOVING_RROM_ORDER = "errorRemovingFromOrder";
  protected static final String MSG_CONCURRENT_UPDATE_ATTEMPT = "ConcurrentUpdateAttempt";
  //fixing typo above
  protected static final String MSG_ERROR_REMOVING_FROM_ORDER = MSG_ERROR_REMOVING_RROM_ORDER;
  protected static final String MSG_NO_ITEM_FOUND = "noItemFound";
  protected static final String PARAM_TRANSACTION_CREATED = "transactionCreated";
  protected static final String PARAM_VALUE_TRUE = "true";

  // Constants used for logged error messages
  protected static final String ERROR_MISSING_LOCK_MANAGER = "missingLocalLockManager";
  protected static final String ERROR_MISSING_LOCK_PROFILE = "missingUseLocksProfile";
  protected static final String ERROR_MISSING_TRANSACTION_MANAGER = "missingTransactionManager";

  private static final String RESOURCE_BUNDLE_NAME = "atg.commerce.order.purchase.PurchaseProcessResources";
  private static ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle(RESOURCE_BUNDLE_NAME);

  //-------------------------------------
  // Member Variables
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: PipelineManager
  //-------------------------------------
  PipelineManager mPipelineManager;

  /**
   * Sets property PipelineManager
   **/
  public void setPipelineManager(PipelineManager pPipelineManager) {
    mPipelineManager = pPipelineManager;
  }

  /**
   * Returns property PipelineManager
   **/
  public PipelineManager getPipelineManager() {
    return mPipelineManager;
  }

  //-------------------------------------
  // property: OrderManager
  SimpleOrderManager mOrderManager;

  /**
   * Sets property OrderManager
   **/
  public void setOrderManager(SimpleOrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * Returns property OrderManager
   **/
  public SimpleOrderManager getOrderManager() {
    return mOrderManager;
  }

  //-------------------------------------
  // property: CatalogTools
  CatalogTools mCatalogTools;

  /**
   * Sets property CatalogTools
   **/
  public void setCatalogTools(CatalogTools pCatalogTools) {
    mCatalogTools = pCatalogTools;
  }

  /**
    * Returns property CatalogTools
   **/
   public CatalogTools getCatalogTools() {
       return mCatalogTools;
   }

  //---------------------------------------------------------------------------
  // property: useLocksAroundTransactions
  //---------------------------------------------------------------------------

  boolean mUseLocksAroundTransactions = true;
  boolean mUseLocksAroundTransactionsSet = false;

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
  // property:transactionLockFactory
  //---------------------------------------------------------------------------
  private TransactionLockFactory mTransactionLockFactory;

  /**
   * The transactionLockFactory is used to get the locks if 
   * <code>useLocksArountTransactions</code> is true
   **/
  public void setTransactionLockFactory(TransactionLockFactory pTransactionLockFactory) {
    mTransactionLockFactory = pTransactionLockFactory;
  }

  /**
   * The transactionLockFactory is used to get the locks if 
   * <code>useLocksArountTransactions</code> is true
   **/
  public TransactionLockFactory getTransactionLockFactory() {
    return mTransactionLockFactory;
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

  //-------------------------------------
  // property: Order
  Order mOrder;

  /**
   * Sets property Order
   **/
  public void setOrder(Order pOrder) {
    mOrder = pOrder;
  }

  /**
   * Returns property Order
   **/
  public Order getOrder() {
    return mOrder;
  }

 
  //-------------------------------------
  // property: Profile
  RepositoryItem mProfile;

  /**
   * Sets property Profile
   **/
  public void setProfile(RepositoryItem pProfile) {
    mProfile = pProfile;
  }

  /**
   * Returns property Profile
   **/
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

  //-------------------------------------
  // property: CatalogRefIds
  String [] mCatalogRefIds;

  /**
   * Sets property CatalogRefIds
   **/
  public void setCatalogRefIds(String [] pCatalogRefIds) {
    mCatalogRefIds = pCatalogRefIds;
  }

  /**
   * Returns property CatalogRefIds
   **/
  public String [] getCatalogRefIds() {
    return mCatalogRefIds;
  }

  //-------------------------------------
  // property: ProductId
  String mProductId;

  /**
   * Sets property ProductId
   **/
  public void setProductId(String pProductId) {
    mProductId = pProductId;
  }

  /**
   * Returns property ProductId
   **/
  public String getProductId() {
    return mProductId;
  }

  //-------------------------------------
  // property: Quantity
  long mQuantity = 0;

  /**
   * Sets property Quantity
   **/
  public void setQuantity(long pQuantity) {
    mQuantity = pQuantity;
  }

  /**
   * Returns property Quantity
   **/
  public long getQuantity() {
    return mQuantity;
  }


  /** an address bean to be used when modifying an address for a shipping or a billing group */
  Address mAddress = new Address();

  //-------------------------------------
  /**
   * Sets an address bean to be used when modifying an address for a shipping or a billing group
   **/
  public void setAddress(Address pAddress) {
    mAddress = pAddress;
  }

  //-------------------------------------
  /**
    * Returns an address bean to be used when modifying an address for a shipping or a billing group
   **/
  public Address getAddress() {
    return mAddress;
  }

  /** payment group bean to use for modifiing payment groups */
  PaymentGroup mPaymentGroup;
  
  //-------------------------------------
  /**
   * Sets payment group bean to use for modifiing payment groups
   **/
  public void setPaymentGroup(PaymentGroup pPaymentGroup) {
	mPaymentGroup = pPaymentGroup;
  }
  
  //-------------------------------------
  /**
   * Returns payment group bean to use for modifiing payment groups
   **/
  public PaymentGroup getPaymentGroup() {
    return mPaymentGroup;
  }
  
  /** credit card bean to use for modifying of billing groups */
  CreditCard mCreditCard;
  
  //-------------------------------------
  /**
   * Sets credit card bean to use for modifying of billing groups
   **/
  public void setCreditCard(CreditCard pCreditCard) {
    mCreditCard = pCreditCard;
  }
  
  //-------------------------------------
  /**
   * Returns credit card bean to use for modifying of billing groups
   **/
  public CreditCard getCreditCard() {
    return mCreditCard;
  }

  
  /** gift certificate bean to use for modifying of billing grps */
  GiftCertificate mGiftCertificate;
  
  //-------------------------------------
  /**
   * Sets gift certificate bean to use for modifying of billing grps
     **/
  public void setGiftCertificate(GiftCertificate pGiftCertificate) {
    mGiftCertificate = pGiftCertificate;
  }
  
  //-------------------------------------
    /**
     * Returns gift certificate bean to use for modifying of billing grps
     **/
  public GiftCertificate getGiftCertificate() {
    return mGiftCertificate;
  }
  
  
  /** hardgood shipping group bean */
  HardgoodShippingGroup mHardgoodShippingGroup;
  
  //-------------------------------------
  /**
   * Sets hardgood shipping group bean
   **/
  public void setHardgoodShippingGroup(HardgoodShippingGroup pHardgoodShippingGroup) {
    mHardgoodShippingGroup = pHardgoodShippingGroup;
  }
  
  //-------------------------------------
  /**
   * Returns hardgood shipping group bean
   **/
  public HardgoodShippingGroup getHardgoodShippingGroup() {
    return mHardgoodShippingGroup;
  }
  
  /** shipping group bean to use for modifying the shipping groups */
  ShippingGroup mShippingGroup;
  
  //-------------------------------------
  /**
   * Sets shipping group bean to use for modifying the shipping groups
   **/
  public void setShippingGroup(ShippingGroup pShippingGroup) {
    mShippingGroup = pShippingGroup;
  }
  
  //-------------------------------------
  /**
   * Returns shipping group bean to use for modifying the shipping groups
   **/
  public ShippingGroup getShippingGroup() {
    return mShippingGroup;
  }
  
  //-------------------------------------
  // property: DefaultLocale
  Locale mDefaultLocale;

  /**
   * Sets property DefaultLocale
   **/
  public void setDefaultLocale(Locale pDefaultLocale) {
    mDefaultLocale = pDefaultLocale;
  }

  /**
   * Returns property DefaultLocale. If the property value is null, then
   * JVM's default locale is returned.
   **/
  public Locale getDefaultLocale() {
    if (mDefaultLocale != null)
      return mDefaultLocale;
    else
      return Locale.getDefault();
  }

  //-------------------------------------
  // property: UseRequestLocale
  boolean mUseRequestLocale = true;

  /**
   * Sets property UseRequestLocale
   **/
  public void setUseRequestLocale(boolean pUseRequestLocale) {
    mUseRequestLocale = pUseRequestLocale;
  }

  /**
   * Returns property UseRequestLocale
   **/
  public boolean isUseRequestLocale() {
    return mUseRequestLocale;
  }

  /** A Geneneric bean to store a success URL into */
  String mGeneralSuccessURL;
  
  //-------------------------------------
  /**
   * Sets A Geneneric bean to store a success URL into
   **/
  public void setGeneralSuccessURL(String pGeneralSuccessURL) {
    mGeneralSuccessURL = pGeneralSuccessURL;
  }
  
  //-------------------------------------
  /**
   * Returns A Geneneric bean to store a success URL into
   **/
  public String getGeneralSuccessURL() {
    return mGeneralSuccessURL;
  }
  
  
  /** A Generic bean to store a failure URL into */
  String mGeneralFailureURL;
  
  //-------------------------------------
  /**
   * Sets A Generic bean to store a failure URL into
   **/
  public void setGeneralFailureURL(String pGeneralFailureURL) {
    mGeneralFailureURL = pGeneralFailureURL;
  }
  
  //-------------------------------------
  /**
   * Returns A Generic bean to store a failure URL into
   **/
  public String getGeneralFailureURL() {
    return mGeneralFailureURL;
  }

  //-------------------------------------
  Transaction mCurrentTransaction = null;

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
          logError(ResourceUtils.getMsgResource(ERROR_MISSING_TRANSACTION_MANAGER,
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
   * Sets property currentTransaction
   *
   * @param pCurrentTransaction the value to set for property currentTransaction
   */
  public void setCurrentTransaction(Transaction pCurrentTransaction) {
    mCurrentTransaction = pCurrentTransaction;
  }
  
  //-------------------------------------
  // property: TransactionManager
  TransactionManager mTransactionManager;

  /**
   * Sets property TransactionManager
   **/
  public void setTransactionManager(TransactionManager pTransactionManager) {
    mTransactionManager = pTransactionManager;
  }

  /**
   * Returns property TransactionManager
   **/
  public TransactionManager getTransactionManager() {
    return mTransactionManager;
  }

  //-------------------------------------
  // property: EnsureTransaction
  boolean mEnsureTransaction = true;

  /**
   * Sets property EnsureTransaction
   **/
  public void setEnsureTransaction(boolean pEnsureTransaction) {
    mEnsureTransaction = pEnsureTransaction;
  }

  /**
   * Returns property EnsureTransaction
   **/
  public boolean isEnsureTransaction() {
    return mEnsureTransaction;
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
   * Returns the name of the error message ResourceBundle
   */
  protected String getResourceBundleName() {
    return RESOURCE_BUNDLE_NAME;
  }
    
  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof OrderModifierFormHandler
   */
  public OrderModifierFormHandler() {
  }

  /**
   * This method ensures that a transaction exists before returning.  
   * If there is no transaction, a new one is started and returned.  In
   * this case, you must call commitTransaction when the transaction 
   * completes.
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
          logError(ResourceUtils.getMsgResource(ERROR_MISSING_TRANSACTION_MANAGER,
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
   */
  protected void commitTransaction(Transaction pTransaction) 
  {
   boolean exception = false;

   if (pTransaction != null) {
      try {
        TransactionManager tm = getTransactionManager();
        if (isTransactionMarkedAsRollBack()) {
          if (tm != null){
            tm.rollback();
          }else{
            pTransaction.rollback();  // PR72259: rollback() before invalidateOrder() prevent deadlocks due to thread synchronization in the invalidateOrder()
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

      } catch (RollbackException exc) {
        exception = true;
        if (isLoggingError())
          logError(exc);
      } catch (HeuristicMixedException exc) {
        exception = true;
        if (isLoggingError())
          logError(exc);
      } catch (HeuristicRollbackException exc) {
        exception = true;
        if (isLoggingError())
          logError(exc);
      } catch (SystemException exc) {
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
    }
  }

  /** 
   * Sets the transaction to rollback
   */
  protected void setTransactionToRollbackOnly() throws SystemException
  {
    getTransactionManager().setRollbackOnly();
  }

  /**
   * Returns true if the transaction associated with the current thread
   * is marked for rollback. This is useful if you do not want to perform
   * some action (e.g. updating the order) if some other subservice already
   * needs the transaction rolledback.
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

  /**
   * If the transaction is marked as rollback, then redirect to the FailureURL,
   * otherwise allow the super-class behavior to perform.
   * @return If redirect (for whatever reason) to a new page occurred,
   *         return false.  If NO redirect occurred, return true.
   * @param pNoErrorsURL The URL to redirect to if there were no form errors.
   *                     If a null value is passed in, no redirect occurs.
   * @param pErrorsURL The URL to redirect to if form errors were found.
   *                   If a null value is passed in, no redirect occurs.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
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


  // -------------------------------------
  // General Commerce Item Management
  // -------------------------------------


  /**
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleRemoveItemFromOrder(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException 
  {
    return true;
  }

  /**
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleAddItemToOrder(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    return true;
  }

  /**
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleSetOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    return true;
  }

					
    /**
     * Adds the designated item to the given order.  First checks to see if the catalogRefId is valid.  
     * If it is, it creates a new commerce item with the information provided, and passes that item to 
     * OrderManager's addItemToOrder method.
     *
     * @param pOrder the order to add the item to
     * @param pCatalogRefId the sku id of the commerce item to add
     * @param pProductId the product id of the commerce item to add
     * @param pQuantity the quantity of the item
     * @param pRequest the servlet's request
     * @param pResponse the servlet's response
     * @exception ServletException if there was an error while executing the code
     * @exception IOException if there was an error with servlet io
     * @return the item created and added
     */
    public CommerceItem addNewItemToOrder(Order pOrder, String pCatalogRefId, String pProductId, long pQuantity,
					  DynamoHttpServletRequest pRequest,
					  DynamoHttpServletResponse pResponse)
	throws ServletException, IOException, CommerceException
    {
	try {
	    RepositoryItem sku = getCatalogTools().findSKU(pCatalogRefId);
	    if (sku == null) {
		String msg = formatUserMessage(MSG_NO_SKU_WITH_ID, pRequest, pResponse);
		addFormException(new DropletException(msg, MSG_NO_SKU_WITH_ID));
		return null;
	    }
            String skuId = sku.getRepositoryId();
            String productId = pProductId;
            RepositoryItem product = getCatalogTools().findProduct(pProductId);
            if (product != null)
		productId = product.getRepositoryId();

	    CommerceItem item = createCommerceItem(skuId, sku, productId, product, pQuantity);
	    getOrderManager().getCommerceItemManager().addItemToOrder(pOrder, item);
	    return item;
	}
	catch (RepositoryException exc) {
	    throw new CommerceException(exc);
	}
    }


  /**
   * This simply calls OrderManager's removeItemFromOrder method with the given order and item ID
   *
   * @param pOrder the order the item is to be removed from
   * @param pItemId the ID of the commerce item to remove
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
    public boolean removeItemFromOrder(Order pOrder, String pItemId,
                                          DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
	throws ServletException, IOException, CommerceException
    {
	try {
	    getOrderManager().getCommerceItemManager().removeItemFromOrder(pOrder, pItemId);
	    return true;
	}
	catch (InvalidParameterException exc) {
	    throw new CommerceException(exc);
	}
	catch (CommerceItemNotFoundException exc) {
	    String msg = formatUserMessage(MSG_NO_ITEM_FOUND, pRequest, pResponse);
	    addFormException(new DropletException(msg, MSG_NO_ITEM_FOUND));
	    return false;
	}
	catch (ObjectRemovalException exc) { 
	    String msg = formatUserMessage(MSG_ERROR_REMOVING_RROM_ORDER, pRequest, pResponse);
	    addFormException(new DropletException(msg, MSG_ERROR_REMOVING_RROM_ORDER));
	    return false;
	}
    }


  /**
   * Retrieve the quanity that should be used for the given item.  This method assumes that parameters are
   * passed from the HTML form where the names of the parameters are the catalogRefIds, and the values are the 
   * quantities.  This method will find the parameter with the given catalogRefId as its name, and will
   * return its value.
   *
   * @param pCatalogRefId the sku id of the item whose quantity we want
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   */
  public long getQuantity(String pCatalogRefId,
                             DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
       throws ServletException, 
	      IOException,
	      NumberFormatException
  {
    Object value = pRequest.getParameter(pCatalogRefId);
    if (value != null) {
      return Long.parseLong(value.toString());
    }
    return getQuantity();
  }

  /**
   * With the given parameters create a new CommerceItem that will be added to the order.
   * 
   * @param pCatalogRefId the sku id of the commerce item
   * @param pCatalogRef the sku repository item
   * @param pProductId the product id of the commerce item
   * @param pProductRef the product repository item
   * @param pQuantity the quantity of the commerce item
   */
  public CommerceItem createCommerceItem(String pCatalogRefId, Object pCatalogRef,
                                            String pProductId, Object pProductRef,
                                            long pQuantity)
       throws CommerceException
  {
    return getOrderManager().getCommerceItemManager().createCommerceItem(pCatalogRefId, pCatalogRef, pProductId, pProductRef, pQuantity);
  }

  /**
   * Look for a CommerceItem within the order that has the same catalogRefId
   *
   * @deprecated This method should no longer be used. Instead call
   * <code>Order.findCommerceItemsByCatalogRefId(String)</code>
   */
  public CommerceItem findCommerceItem(Order pOrder, String pCatalogRefId)
       throws CommerceException
  {
    List items = pOrder.getCommerceItems();
    if (items != null) {
      for (int c=0; c<items.size(); c++) {
        CommerceItem item = (CommerceItem)items.get(c);
        if (item != null) {
          String itemId = item.getCatalogRefId();
          if ((itemId != null) && (itemId.equals(pCatalogRefId)))
            return item;
        }
      }
    }
    return null;
  }

  /**
   * Returns a Nucleus property path which can be used in a DropletFormException
   *
   * @param pPropertyName the name of the property
   */
  public String generatePropertyPath(String pPropertyName) {
    return getAbsoluteName() + "." + pPropertyName;
  }

  // -------------------------------
  // Helpful Administrative Methods
  // -------------------------------

  /**
   * Add a user error message to the form exceptions, and then display
   * the exception in the logs
   *
   * @param pException the exception
   * @param pMsgId the ID of the message to add
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response   
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
        msg = formatUserMessage(MSG_CONCURRENT_UPDATE_ATTEMPT, order.getId(), pRequest, pResponse);
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
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response   
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
   * @param pResponse the servlet's response   
   * @return the formatted message
   * @see OrderUserMessage
   **/
  public String formatUserMessage(String pKey,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return OrderUserMessage.format(pKey, getUserLocale(pRequest, pResponse));
  }

  /**
   * Utility method to format a message with one argument using the Locale of the user
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParam the first (and only argument) in the message
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response   
   * @return the formatted message
   * @see OrderUserMessage
   **/
  public String formatUserMessage(String pKey, Object pParam,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return OrderUserMessage.format(pKey, pParam, getUserLocale(pRequest, pResponse));
  }

  /**
   * Utility method to format a message with two arguments using our resource bundle.
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParam1 the first parameter in the message
   * @param pParam2 the second parameter in the message
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response   
   * @return the formatted message
   * @see OrderUserMessage
   **/
  public String formatUserMessage(String pKey, Object pParam1, Object pParam2,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    Object[] params = { pParam1, pParam2, };
    return OrderUserMessage.format(pKey, params, getUserLocale(pRequest, pResponse));
  }

  // ---------------------------------------------------- //
  //                 Helper Methods                       //
  // ---------------------------------------------------- //

  //---------------------------------------------------------------------------

  /**
   * Attempt to acquire a local lock before creating a transaction
   * that may modify the order, but only if
   * <code>useLocksAroundTransactions</code> is set to true.
   **/
   
  protected void acquireTransactionLock()
    throws DeadlockException
  {
    try {
      TransactionLockService service = getTransactionLockService();
      if(service != null) {
        service.acquireTransactionLock();
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
   **/
   
   protected void releaseTransactionLock()
    throws LockManagerException
  {
    TransactionLockService service = getTransactionLockService();
    if(service != null)
      service.releaseTransactionLock();
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
      
    TransactionLockFactory factory = getTransactionLockFactory();

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
   **/
  
  protected String transactionLockName()
  {
    RepositoryItem profile;

    if ((profile = getProfile()) == null) {
      if ((profile = defaultUserProfile(true)) == null) {
        if (isLoggingError())
          logError(ResourceUtils.getMsgResource(ERROR_MISSING_LOCK_PROFILE,
                                                getResourceBundleName(), getResourceBundle()));
        return null;
      }
    }
    
    return profile.getRepositoryId();
  }

  //---------------------------------------------------------------------------

  /**
   * Create a transaction if necessary at the beginning of the form
   * submission process, optionally obtaining a local lock to prevent
   * multiple forms from creating transactions that may modify the
   * same order.
   *
   * @see #afterSet
   * @see #ensureTransaction
   * @see #setUseLocksAroundTransactions
   **/
  
  public boolean beforeSet(DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pResponse) throws DropletFormException
  {
    try {
      acquireTransactionLock();
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
   * Commit or roll back any transaction created in beforeSet,
   * and release any lock that was acquired at the time.
   *
   * @see #beforeSet
   * @see #ensureTransaction
   * @see #setUseLocksAroundTransactions
   **/

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
        releaseTransactionLock();
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
   * Utility method to format a message with two arguments using our resource bundle.
   * @param pKey the identifier for the message to retrieve out of the ResourceBundle
   * @param pParams a set of parameters to use in the formatting.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response   
   * @return the formatted message
   * @see OrderUserMessage
   **/
  public String formatUserMessage(String pKey, Object [] pParams,
                           DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    return OrderUserMessage.format(pKey, pParams, getUserLocale(pRequest, pResponse));
  }

  protected boolean isStringEmpty(String pString) {
    if (pString == null || pString.trim().length() == 0) {
      return true;
    }
    return false;
  }

  // -------------------------------
  // Pipeline Utilities
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
   */
  protected PipelineResult runProcess(String pChainId, Order pOrder, PricingModelHolder pPricingModels,
                                      Locale pLocale, RepositoryItem pProfile, Map pParameters,
                                      Map pExtraParameters)
    throws RunProcessException
  {
    if (pChainId == null)
      return null;
      
    Map params = null;
    if (pParameters == null)
      params = new HashMap();
    else
      params = pParameters;

    try {
      params.put(PricingConstants.ORDER_PARAM, pOrder);
      params.put(PricingConstants.PRICING_MODELS_PARAM, pPricingModels);
      params.put(PricingConstants.LOCALE_PARAM, pLocale);
      params.put(PricingConstants.PROFILE_PARAM, pProfile);
      params.put(PipelineConstants.ORDERMANAGER, getOrderManager());
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
   */
  protected PipelineResult runProcess(String pChainId, Map pParameters)
    throws RunProcessException
  {
    if (pChainId == null)
      return null;

    try {
      return getPipelineManager().runProcess(pChainId, pParameters);
    }
    catch (RunProcessException exc) {
      throw exc;
    }
  }

  /**
   * Examine the pipeline result looking for any error messages, creates DropletExceptions
   * and adds them as form exceptions
   * @return true if errors were found
   */
  protected boolean processPipelineErrors(PipelineResult pResult) {
    if (pResult == null)
      return false;
    return PipelineUtils.processPipelineErrors(pResult, this, this);
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
  
} // end of class
