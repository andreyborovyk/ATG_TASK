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

package atg.commerce.profile;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.ServletException;

import atg.beans.DynamicBeans;
import atg.beans.DynamicBeanInfo;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.order.Constants;
import atg.commerce.order.CreditCard;
import atg.commerce.order.Order;
import atg.commerce.order.OrderHolder;
import atg.commerce.order.OrderManager;
import atg.commerce.order.OrderTools;
import atg.commerce.pricing.PricingConstants;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingTools;
import atg.commerce.promotion.PromotionTools;
import atg.commerce.states.OrderStates;
import atg.commerce.states.StateDefinitions;
import atg.commerce.util.NoLockNameException;
import atg.commerce.util.TransactionLockFactory;
import atg.commerce.util.TransactionLockService;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.multisite.SiteGroupManager;
import atg.nucleus.naming.ComponentName;
import atg.nucleus.naming.ParameterName;
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.Repository;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.repository.RepositoryItemDescriptor;
import atg.repository.RepositoryUtils;
import atg.service.localeservice.LocaleService;
import atg.service.lockmanager.DeadlockException;
import atg.service.lockmanager.LockManagerException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.RequestLocale;
import atg.servlet.ServletUtil;
import atg.userprofiling.Profile;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.address.AddressTools;

/**
 * This set of utilities provide additional profile functionality as required
 * by Dynamo Commerce Server (DCS).  The functions include creating and
 * editing primary and secondary addresses as well as credit card information
 * for a customer.
 *
 * @author Kerwin D. Moy
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/profile/CommerceProfileTools.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see ProfileTools
 * @see CommercePropertyManager
 */
public class CommerceProfileTools extends ProfileTools
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/profile/CommerceProfileTools.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------
  static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");
  protected static final int ZERO = 0;
  protected static final int ONE = 1;
  protected static final int TWO = 2;

  protected static final String[] CREDIT_CARD_PROPERTIES = {"creditCardNumber"};
  protected static final String NO_CREDIT_CARD = "noCreditCard";
  protected static final String LOCK_NAME_ATTRIBUTE_NAME = "atg.CommerceProfileToolsLock";

  /** resource bundle name */
  private static final String RESOURCE_BUNDLE_NAME = "atg.commerce.profile.Resources";

  // resource message keys
  private static final String MSG_NULL_CREDIT_CARD = "nullCreditCard";
  private static final String MSG_PROPERTY_NOT_FOUND = "propertyNotFound";
  private static final String MSG_NULL_PROFILE_REPOSITORY = "nullProfileRepository";
  private static final String MSG_ADDRESS_COMPARE_FAILED = "failedToCompareAddresses";
  protected static final String ID_PROPERTY_NAME = "ID";

  //------------------------------------
  // Resource bundle
  private static ResourceBundle sResourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // property: orderManager
  OrderManager mOrderManager = null;

  /**
   * Returns property orderManager
   *
   * @return returns property orderManager
   */
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  /**
   * Sets property orderManager
   *
   * @param pOrderManager the value to set for property orderManager
   */
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }


  //-------------------------------------
  // property: localeService
  LocaleService mLocaleService;
  /**
   * Returns property localeService
   *
   * @return returns property localeService
   */
  public LocaleService getLocaleService() {
    return mLocaleService;
  }

  /**
   * Sets property localeService
   *
   * @param pLocaleService the value to set for property localeService
   */
  public void setLocaleService(LocaleService pLocaleService) {
    mLocaleService = pLocaleService;
  }


  //-------------------------------------
  // property: siteGroupManager
  SiteGroupManager mSiteGroupManager = null;

  /**
   * Returns the SiteGroupManager
   *
   * @return returns property siteGroupManager
   */
  public SiteGroupManager getSiteGroupManager() {
    return mSiteGroupManager;
  }

  /**
   * Sets the SiteGroupManager
   *
   * @param pSiteGroupManager the value to set for property siteGroupManager
   */
  public void setSiteGroupManager(SiteGroupManager pSiteGroupManager) {
    mSiteGroupManager = pSiteGroupManager;
  }


  //-------------------------------------
  // property: pricingTools
  PricingTools mPricingTools = null;

  /**
   * Returns property pricingTools
   *
   * @return returns property pricingTools
   */
  public PricingTools getPricingTools() {
    return mPricingTools;
  }

  /**
   * Sets property pricingTools
   *
   * @param pPricingTools the value to set for property pricingTools
   */
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  //-------------------------------------
  // property: UserPricingModelsPath
  protected ComponentName mUserPricingModelsPath;

  /**
   * Sets property UserPricingModelsPath
   **/
  public void setUserPricingModelsPath(String pUserPricingModelsPath) {
    if (pUserPricingModelsPath != null)
      mUserPricingModelsPath = ComponentName.getComponentName(pUserPricingModelsPath);
    else
      mUserPricingModelsPath = null;
  }

  /**
   * Returns property UserPricingModelsPath
   **/
  public String getUserPricingModelsPath() {
    if (mUserPricingModelsPath != null)
      return mUserPricingModelsPath.getName();
    else
      return null;
  }

  //-------------------------------------
  // property: ShoppingCartPath
  protected ComponentName mShoppingCartPath = null;

  /**
   * Sets property ShoppingCartPath
   **/
  public void setShoppingCartPath(String pShoppingCartPath) {
    if (pShoppingCartPath != null)
      mShoppingCartPath = ComponentName.getComponentName(pShoppingCartPath);
    else
      mShoppingCartPath = null;
  }

  /**
   * Returns property ShoppingCartPath
   **/
  public String getShoppingCartPath() {
    if (mShoppingCartPath != null)
      return mShoppingCartPath.getName();
    else
      return null;
  }

  //-------------------------------------
  // property: ProfilePath
  protected ComponentName mProfilePath = null;

  /**
   * Sets property ProfilePath
   **/
  public void setProfilePath(String pProfilePath) {
    if (pProfilePath != null)
      mProfilePath = ComponentName.getComponentName(pProfilePath);
    else
      mProfilePath = null;
  }

  /**
   * Returns property ProfilePath
   **/
  public String getProfilePath() {
    if (mProfilePath != null)
      return mProfilePath.getName();
    else
      return null;
  }

  //-------------------------------------
  // property: mergeOrders
  boolean mMergeOrders = true;

  /**
   * Set the mergeOrders property.
   */
  public void setMergeOrders(boolean pMergeOrders) {
    mMergeOrders = pMergeOrders;
  }

  /**
   * Return the mergeOrders property.
   */
  public boolean isMergeOrders() {
    return mMergeOrders;
  }

  //---------------------------------------------------------------------------
  // property: SelectLastModifiedOrder
  //---------------------------------------------------------------------------

  boolean mSelectLastModifiedOrder=true;

  /**
   * If set to true will return the order that was last modified in the list of open orders for
   * the user.  If it is set to false, the behavior of selecting the first non-explicitly
   * saved order is used instead.  The default is set to true.
   *
   **/
  public void setSelectLastModifiedOrder(boolean pSelectLastModifiedOrder) {
    mSelectLastModifiedOrder = pSelectLastModifiedOrder;
  }


  /**
   * @see setSelectLastModifiedOrder
   **/
  public boolean isSelectLastModifiedOrder() {
    return mSelectLastModifiedOrder;
  }


  //-------------------------------------
  // property: SortOrdersByProperty
  String mSortOrdersByProperty;

  /**
   * Sets property SortOrdersByProperty
   **/
  public void setSortOrdersByProperty(String pSortOrdersByProperty) {
    mSortOrdersByProperty = pSortOrdersByProperty;
  }

  /**
   * Returns property SortOrdersByProperty
   **/
  public String getSortOrdersByProperty() {
    return mSortOrdersByProperty;
  }

  //-------------------------------------
  // property: AscendingOrder
  boolean mAscendingOrder = false;

  /**
   * Sets property AscendingOrder
   **/
  public void setAscendingOrder(boolean pAscendingOrder) {
    mAscendingOrder = pAscendingOrder;
  }

  /**
   * Returns property AscendingOrder
   **/
  public boolean isAscendingOrder() {
    return mAscendingOrder;
  }


  //-------------------------------------
  // property: RepriceOrderOnLogin
  boolean mRepriceOrderOnLogin = true;

  /**
   * Sets property RepriceOrderOnLogin
   **/
  public void setRepriceOrderOnLogin(boolean pRepriceOrderOnLogin) {
    mRepriceOrderOnLogin = pRepriceOrderOnLogin;
  }

  /**
   * Returns property RepriceOrderOnLogin
   **/
  public boolean isRepriceOrderOnLogin() {
    return mRepriceOrderOnLogin;
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

  //-------------------------------------
  // property: RepriceOrderPricingOp
  String mRepriceOrderPricingOp = PricingConstants.OP_REPRICE_ORDER_SUBTOTAL;

  /**
   * This property should be set to the type of pricing operation that should be executed when
   * the order(s) is repriced after login.
   * The operations which are acceptable are defined in the
   * <code>atg.commerce.pricing.PricingConstants</code> interface. They include:
   * <UL>
   * <LI>PricingConstants.OP_REPRICE_ORDER_TOTAL -> <code>ORDER_TOTAL</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL -> <code>ORDER_SUBTOTAL</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING -> <code>ORDER_SUBTOTAL_SHIPPING</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_TAX -> <code>ORDER_SUBTOTAL_TAX</code>
   * <LI>PricingConstants.OP_REPRICE_ITEMS -> <code>ITEMS</code>
   * <LI>PricingConstants.OP_REPRICE_SHIPPING -> <code>SHIPPING</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER -> <code>ORDER</code>
   * <LI>PricingConstants.OP_REPRICE_TAX -> <code>TAX</code>
   * <LI>PricingConstants.OP_NO_REPRICE -> <code>NO_REPRICE</code>
   * </UL>
   * If this value is null, then the system will default to ORDER_TOTAL.
   **/
  public void setRepriceOrderPricingOp(String pRepriceOrderPricingOp) {
    mRepriceOrderPricingOp = pRepriceOrderPricingOp;
  }

  /**
   * Returns the pricing operation that should be performed when the order(s) is priced after login.
   **/
  public String getRepriceOrderPricingOp() {
    return mRepriceOrderPricingOp;
  }

  //---------------------------------------------------------------------------
  // property: creditCardProperties

  /**
   * This is a list of properties that will be checked by the system to
   * determine if a credit card is "empty.".  By default, it is set
   * to the constant CommerceProfileTools.CREDIT_CARD_PROPERTIES
   */
  String[] mCreditCardProperties = CREDIT_CARD_PROPERTIES;

  /**
   * Set the creditCardProperties property.
   */
  public void setCreditCardProperties(String[] pCreditCardProperties) {
    mCreditCardProperties = pCreditCardProperties;
  }

  /**
   * Return the creditCardProperties property.
   */
  public String[] getCreditCardProperties() {
    return mCreditCardProperties;
  }

  //---------------------------------------------------------------------------
  // property: orderItemDescriptorName
  //---------------------------------------------------------------------------
  private String mOrderItemDescriptorName = "order";

  /**
   * Set the orderItemDescriptorName property.
   */
  public void setOrderItemDescriptorName(String pOrderItemDescriptorName) {
    mOrderItemDescriptorName = pOrderItemDescriptorName;
  }

  /**
   * Return the orderItemDescriptorName property.
   */
  public String getOrderItemDescriptorName() {
    return mOrderItemDescriptorName;
  }
  //---------------------------------------------------------------------------
  // property: profileIdPropertyName
  //---------------------------------------------------------------------------
  private String mProfileIdPropertyName = "profileId";

  /**
   * Set the ProfileIdPropertyName property.
   */
  public void setProfileIdPropertyName(String pProfileIdPropertyName) {
    mProfileIdPropertyName = pProfileIdPropertyName;
  }

  /**
   * Return the ProfileIdPropertyName property.
   */
  public String getProfileIdPropertyName() {
    return mProfileIdPropertyName;
  }

  //-------------------------------------
  // property: emptyAddressPropertyNames
  //-------------------------------------
  private String[] mEmptyAddressPropertyNames = null;

  /**
   * Returns the emptyAddressPropertyNames
   */
  public String[] getEmptyAddressPropertyNames() {
    return mEmptyAddressPropertyNames;
  }

  /**
   * Sets the emptyAddressPropertyNames
   */
  public void setEmptyAddressPropertyNames(String[] pEmptyAddressPropertyNames) {
    mEmptyAddressPropertyNames = pEmptyAddressPropertyNames;
  }

  //-------------------------------------
  // property: CompareAddressPropertyNames
  //-------------------------------------
  private String[] mCompareAddressPropertyNames = {"companyName","address1","address2","address3","city","state","postalCode"};

  /**
   * Returns the CompareAddressPropertyNames
   */
  public String[] getCompareAddressPropertyNames() {
    return mCompareAddressPropertyNames;
  }

  /**
   * Sets the CompareAddressPropertyNames
   * <p>
   * These property names are used to compare two address to one another.
   * @see #areAddressesEqual(RepositoryItem, RepositoryItem)
   */
  public void setCompareAddressPropertyNames(String[] pCompareAddressPropertyNames) {
    mCompareAddressPropertyNames = pCompareAddressPropertyNames;
  }
  //---------------------------------------------------------------------------
  // property:PromotionTools
  //---------------------------------------------------------------------------
  private PromotionTools mPromotionTools;
  public void setPromotionTools(PromotionTools pPromotionTools) {
    mPromotionTools = pPromotionTools;
  }

  /**
   * The promotion management utility
   * @beaninfo description: The promotion management utility
   **/
  public PromotionTools getPromotionTools() {
    return mPromotionTools;
  }

  //---------------------------------------------------------------------------
  // property:TransactionLockFactory
  //---------------------------------------------------------------------------
  private TransactionLockFactory mTransactionLockFactory;
  public void setTransactionLockFactory(TransactionLockFactory pTransactionLockFactory) {
    mTransactionLockFactory = pTransactionLockFactory;
  }

  /**
   * The factory used to get the service that is used to get and release locks
   * before modifying the order
   **/
  public TransactionLockFactory getTransactionLockFactory() {
    return mTransactionLockFactory;
  }

  String mUniqueNicknameSeparator;

  /**
   * @return Returns the uniqueNicknameSeparator.
   */
  public String getUniqueNicknameSeparator() {
    return mUniqueNicknameSeparator;
  }

  /**
   * @param pUniqueNicknameSeparator The uniqueNicknameSeparator to set.
   */
  public void setUniqueNicknameSeparator(String pUniqueNicknameSeparator) {
    mUniqueNicknameSeparator = pUniqueNicknameSeparator;
  }

  String [] mShippingAddressMapProperties;
  String [] mBillingAddressMapProperties;

  /**
   * @return the billingAddressMapProperties
   */
  public String[] getBillingAddressMapProperties() {
    return mBillingAddressMapProperties;
  }

  /**
   * Sets an array of profile map properties that contain addresses. These addresses will be
   * included to obtain list of all available billing addresses.
   * @param pBillingAddressMapProperties The billingAddressMapProperties to set.
   */
  public void setBillingAddressMapProperties(String[] pBillingAddressMapProperties) {
    mBillingAddressMapProperties = pBillingAddressMapProperties;
  }

  /**
   * @return the shippingAddressMapProperties
   */
  public String[] getShippingAddressMapProperties() {
    return mShippingAddressMapProperties;
  }
  
  /**
   * Sets an array of profile map properties that contain addresses. These addresses will be
   * included to obtain list of all available shipping addresses.
   * @see this.getAllAvailableAddresses()
   * @param pShippingAddressMapProperties The shippingAddressMapProperties to set.
   */
  public void setShippingAddressMapProperties(
      String[] pShippingAddressMapProperties) {
    mShippingAddressMapProperties = pShippingAddressMapProperties;
  }
  
  /**
   * Returns whether shopping carts should be loaded.  
   * By default this returns true if PersistOrders is true.
   * 
   * Subclasses may override this method to implement different
   * behavior if necessary. 
   */
            
  public boolean shouldLoadShoppingCarts(OrderHolder pShoppingCart) {
     return pShoppingCart.isPersistOrders();
  }
  

  //-------------------------------------
  /**
   * Constructs an CommerceProfileTools object.
   */
  public CommerceProfileTools() {
  }

  /**
   * Returns a list of all the names that the user has in their address book.
   * This list is composed of the nicknames from this book as well as the
   * default shipping/billing address that are associated with the Profile
   * object.  The "names" given to the default shipping/billing address are
   * defined by the propeties DefaultBillingAddrName and
   * DefaultShippingAddrName.
   *
   * @param pProfile the customer profile.
   * @return the collection of address names
   **/
  public Collection getProfileAddressNames(RepositoryItem pProfile) {
    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
    ArrayList profileAddressNames = new ArrayList();

    // add the default addresses from Proflie
    profileAddressNames.add(cpmgr.getDefaultShippingAddrName(getLocaleService().getLocale()));
    profileAddressNames.add(cpmgr.getDefaultBillingAddrName(getLocaleService().getLocale()));

    // Fetch the map of secondary addresses:
    Map secondaryAddressMap = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());

    //Fetch the keys (ie names) for all of the secondary addresses...
    Collection secondaryAddressKeys = secondaryAddressMap.keySet();

    Iterator iterator = secondaryAddressKeys.iterator();
    while (iterator.hasNext())
      profileAddressNames.add(iterator.next());

    // Fetch the map of giftlist addresses:
    Map giftlistAddressMap = (Map) pProfile.getPropertyValue(cpmgr.getGiftlistAddressPropertyName());

    //Fetch the keys (ie names) for all of the giftlist addresses...
    Collection giftlistAddressKeys = giftlistAddressMap.keySet();

    iterator = giftlistAddressKeys.iterator();
    while (iterator.hasNext())
      profileAddressNames.add(iterator.next());

    return profileAddressNames;
  }

  //-------------------------------------
  // method:  getProfileAddresses
  //-------------------------------------
  /**
   * Returns a map of all addresses by id that the user has in their
   * address book.  The list is componsed of both primary and secondary
   * addresses.
   *
   * @param pProfile the customer profile.
   * @return the collection of address names
  **/

  public Map getProfileAddresses(RepositoryItem pProfile) {

    HashMap addresses = new HashMap();
    RepositoryItem address;
    String id = (String) pProfile.getPropertyValue("id");

    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
    Locale locale = getLocaleService().getLocale();
    address = getProfileAddress(pProfile, cpmgr.getDefaultBillingAddrName(locale));
    if(address != null)
      addresses.put(address.getPropertyValue("id"), cpmgr.getDefaultBillingAddrName(locale));
    address = getProfileAddress(pProfile, cpmgr.getDefaultShippingAddrName(locale));
    if(address != null)
      addresses.put(address.getPropertyValue("id"), cpmgr.getDefaultShippingAddrName(locale));

    // Retrieve the map of secondary addresses
    Map secondaryAddressMap = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());

    if (!(secondaryAddressMap.isEmpty())){
      // For each address entry in map, get key (name)
      Collection secondaryAddressKeys = secondaryAddressMap.keySet();
      Iterator iterator = secondaryAddressKeys.iterator();
      while (iterator.hasNext()){
        // Store name, id in addresses
        String addressName = (String)iterator.next();
        address = (RepositoryItem) secondaryAddressMap.get(addressName);
        String ownerId = (String) address.getPropertyValue(cpmgr.getAddressOwnerPropertyName());
        if (id.equalsIgnoreCase(ownerId))
          addresses.put(address.getPropertyValue("id"), addressName);
      }
    }
    return addresses;
  }

  //-------------------------------------
  // method:  createProfileRepositorySecondaryAddress
  //-------------------------------------
  /**
   * Creates a new address in the user's Profile (under the secondaryAddresses
   * attribute. Only the name is filled in.  All other values will be filled
   * in later when the user goes to the "Detail Shipping Addresses" screen.
   * @param pProfile the customer profile.
   * @param pAddressName The name of the new address in the address book.
   * @param pAddress The Address object.
   * @return the repository address id
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public String createProfileRepositorySecondaryAddress (RepositoryItem pProfile, String pAddressName, Address pAddress)
      throws RepositoryException
    {
        MutableRepository repository = ((MutableRepository)pProfile.getRepository());
        CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();

        //Create a new address repository object:
        //Fill in the name the user specified....all other
        //info will be filled in by user later...
        MutableRepositoryItem item = repository.createItem(cpmgr.getContactInfoItemDescriptorName());

        if (pAddress != null) {
          try {
            OrderTools.copyAddress(pAddress,item);
          } catch (CommerceException ce) {
            Throwable src = ce.getSourceException();
            if (src instanceof RepositoryException) {
              throw (RepositoryException)src;
            } else {
              throw new RepositoryException(src);
            }
          }

          // set owner of contactInfo rep item
          item.setPropertyValue(cpmgr.getAddressOwnerPropertyName(), pProfile.getPropertyValue("id"));
        }

        // Insert new RepositoryItem into the db
        repository.addItem(item);

        //Get 2ndary addresses from Profile:
        Map mymap = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());

        //add the new address to map of 2ndary addresses:
        mymap.put(pAddressName, item);

        // return item id
        return (String)item.getPropertyValue("id");
  }

  //-------------------------------------
  // method:  createProfileRepositoryPrimaryAddress
  //-------------------------------------
  /**
   * Creates a new address in the user's Profile primary address book.
   * @param pProfile the customer profile.
   * @param pAddressProperty The name of the Profile property that will hold the new address.
   * @param pAddress The Address object.
   * @return the repository address id
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public String createProfileRepositoryPrimaryAddress (RepositoryItem pProfile, String pAddressProperty, Address pAddress)
      throws RepositoryException
    {
        MutableRepository repository = ((MutableRepository)pProfile.getRepository());
        CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
        MutableRepositoryItem profile = (MutableRepositoryItem) pProfile;

        //Create a new address repository object:
        //Fill in the name the user specified....all other
        //info will be filled in by user later...
        MutableRepositoryItem item = repository.createItem(cpmgr.getContactInfoItemDescriptorName());

        if (pAddress != null) {
          try {
            OrderTools.copyAddress(pAddress,item);
          } catch (CommerceException ce) {
            Throwable src = ce.getSourceException();
            if (src instanceof RepositoryException) {
              throw (RepositoryException)src;
            } else {
              throw new RepositoryException(src);
            }
          }

          // set owner of contactInfo rep item
          item.setPropertyValue(cpmgr.getAddressOwnerPropertyName(), pProfile.getPropertyValue("id"));
        }

        // Insert new RepositoryItem into the db
        repository.addItem(item);

        // Add to profile
        profile.setPropertyValue(pAddressProperty, item);

        // return item id
        return (String)item.getPropertyValue("id");
  }

  //-------------------------------------
  // method:  updateProfileRepositoryAddress
  //-------------------------------------
  /**
   * updates a repository address item with values in address object passed
   * in.
   * @param pRepositoryAddress the repository address
   * @param pAddress the address object
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public void updateProfileRepositoryAddress (RepositoryItem pRepositoryAddress, Address pAddress)
      throws RepositoryException
    {
        CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();

        //Create a new address repository object:
        //Fill in the name the user specified....all other
        //info will be filled in by user later...
        MutableRepositoryItem item = (MutableRepositoryItem) pRepositoryAddress;

        if (pAddress != null) {
          try {
            OrderTools.copyAddress(pAddress,item);
          } catch (CommerceException ce) {
            Throwable src = ce.getSourceException();
            if (src instanceof RepositoryException) {
              throw (RepositoryException)src;
            } else {
              throw new RepositoryException(src);
            }
          }
        }
  }

  /**
   * Given a nickname for an address to retrieve, grab it out of the users Profile
   * object and return this as a RepositoryItem.
   *
   * @param pProfile the customer profile.
   * @param pProfileAddressName nickname for a users address object
   * @return the address object
   */

  public RepositoryItem getProfileAddress(RepositoryItem pProfile, String pProfileAddressName) {

  CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
  if (pProfileAddressName.equalsIgnoreCase(cpmgr.getDefaultBillingAddrName(getLocaleService().getLocale()))) {
    return (RepositoryItem)pProfile.getPropertyValue(cpmgr.getBillingAddressPropertyName());
    } else if (pProfileAddressName.equalsIgnoreCase(cpmgr.getDefaultShippingAddrName(getLocaleService().getLocale()))) {
      return (RepositoryItem)pProfile.getPropertyValue(cpmgr.getShippingAddressPropertyName());
    } else {
        Map secondaryAddressMap = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
        return (RepositoryItem)secondaryAddressMap.get(pProfileAddressName);
    }
 }

  /**
   * Given an id for an address to retrieve, grab it out of the users Profile
   * object and return this as a RepositoryItem.
   *
   * @param pProfileAddressId id for a users address object
   * @return the address object
   */

  public RepositoryItem getProfileAddressById(String pProfileAddressId) {

    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
    if (pProfileAddressId != null)
      try{
        return getProfileRepository().getItem(pProfileAddressId, cpmgr.getContactInfoItemDescriptorName());
      }
      catch (RepositoryException ex){
      if (isLoggingError())
        logError(ex);
      }
    return null;
  }


  /**
   * Given an id for credit card to retrieve, grab it out of the users Profile
   * object and return this as a RepositoryItem.
   *
   * @param pCreditCardId id for a users credit card object
   * @return the credit card object
   */

  public RepositoryItem getCreditCardById(String pCreditCardRepositoryId) {

    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
    if (pCreditCardRepositoryId != null)
      try{
        return getProfileRepository().getItem(pCreditCardRepositoryId, cpmgr.getCreditCardItemDescriptorName());
      }
      catch (RepositoryException ex){
      if (isLoggingError())
        logError(ex);
      }
    return null;
  }

  /**
   * Get the profile repository item for the given user
   * If the given id is null, returns the current user
   * @param pProfileId The profile id to look for
   * @return The repository item for the given profile.  null if the id is invalid
   * @throws RepositoryException
   */
  public MutableRepositoryItem getProfileItem(String pProfileId) throws RepositoryException
  {
    MutableRepository profileRepository = getProfileRepository();
    if(profileRepository == null)  {
      String msg = msg(MSG_NULL_PROFILE_REPOSITORY);
      throw new RepositoryException(msg);
    }

    // Arbitrarily using the loginProfileType here
    String profileType = getDefaultProfileType();

    if(pProfileId != null) {
      return profileRepository.getItemForUpdate(pProfileId, profileType);
    } else {
      // get current user
      RepositoryItem current = ServletUtil.getCurrentUserProfile();
      if(current != null)
        return profileRepository.getItemForUpdate(current.getRepositoryId(), profileType);
    }
    return null;
  }

  //-------------------------------------
  // method:  getCreditCardByNickname
  //-------------------------------------
  /**
   * Retrieves credit card by nickname and profile.
   * @param pCreditCardNickname the credit card nickname.
   * @param pProfile the customer profile.
   * @return the credit card repository item.
   */
  public RepositoryItem getCreditCardByNickname(String pCreditCardNickname,
                                                RepositoryItem pProfile)
  {
    if (!StringUtils.isBlank(pCreditCardNickname)) {
      CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
      Map ccMap = (Map)pProfile.getPropertyValue(cpmgr.getCreditCardPropertyName());
      RepositoryItem creditCard = (RepositoryItem)ccMap.get(pCreditCardNickname);
      return creditCard;
    }
    return null;
  }

  //-------------------------------------
  // method:  getUsersCreditCardMap
  //-------------------------------------
  /**
   * Retrieves map of credit cards for a given profile.
   * @param pProfile the customer profile.
   * @return the map of credit cards
   */
  public Map getUsersCreditCardMap(RepositoryItem pProfile) {
    if (pProfile != null) {
      CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
      return (Map)pProfile.getPropertyValue(cpmgr.getCreditCardPropertyName());
    }
    return null;
  }

  //-------------------------------------
  // method:  createCreditCardItem
  //-------------------------------------
  /**
   * Creates a new credit card repository item for the given profile
   * @param pProfile the customer profile.
   * @return The new credit card repository item.
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public MutableRepositoryItem createCreditCardItem(RepositoryItem pProfile)
    throws RepositoryException
  {
    MutableRepositoryItem creditCardRepositoryItem;
    MutableRepositoryItem contactInfoRepositoryItem;

    if (pProfile != null) {
      CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
      MutableRepository repository = (MutableRepository)pProfile.getRepository();
      // create new credit card object
      creditCardRepositoryItem = repository.createItem(cpmgr.getCreditCardItemDescriptorName());
      contactInfoRepositoryItem = repository.createItem(cpmgr.getContactInfoItemDescriptorName());

      // populate the credit card with the necessary info
      creditCardRepositoryItem.setPropertyValue(cpmgr.getCreditCardItemDescriptorBillingAddressPropertyName(),
                                                contactInfoRepositoryItem);
      repository.addItem(contactInfoRepositoryItem);
      repository.addItem(creditCardRepositoryItem);

      return creditCardRepositoryItem;
    }
    return null;
  }

  //-------------------------------------
  // method:  addCreditCardToUsersMap
  //-------------------------------------
  /**
   * Adds the new credit card to the users profile.
   * @param pProfile the customer profile.
   * @param pCreditCard the credit card.
   * @deprecated
   */
  public void addCreditCardToUsersMap(RepositoryItem pProfile,
                                      RepositoryItem pCreditCard)
  {
    addCreditCardToUsersMap (pProfile,pCreditCard,null);
  }

  /**
   * Adds the new credit card to the users profile.
   * @param pProfile the customer profile.
   * @param pCreditCard the credit card.
   */
  public void addCreditCardToUsersMap(RepositoryItem pProfile,
                                      RepositoryItem pCreditCard,
                                      String pNickName)
  {
      if (pCreditCard != null) {
          CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
          Map ccMap = getUsersCreditCardMap(pProfile);
          if (StringUtils.isEmpty(pNickName)){
            ccMap.put(getUniqueCreditCardNickname(pCreditCard, pProfile, pNickName), pCreditCard);
          } else {
            ccMap.put(pNickName, pCreditCard);
          }
      } else {
          if (isLoggingError()) {
            logError(msg(MSG_NULL_CREDIT_CARD));
        }
      }
  }

  //-------------------------------------
  // method:  getAddressFromRepository
  //-------------------------------------
  /**
   * This method creates an address object and sets the property values
   * to values in the repository item passed in.
   * @param pItem the repository item
   * @return address the address object with data from repository
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public Address getAddressFromRepositoryItem (RepositoryItem pItem)
      throws RepositoryException
    {
      CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
      
      Address address;
      
      if (pItem.getItemDescriptor().getItemDescriptorName().equals(cpmgr.getContactInfoItemDescriptorName())) {
        address = new ContactInfo();
      } else {
        address = new Address();
      }
      
      // update item with values in address
      try {
        OrderTools.copyAddress(pItem,address);
      } catch (CommerceException ce) {
        Throwable src = ce.getSourceException();
        if (src instanceof RepositoryException) {
          throw (RepositoryException)src;
        } else {
          throw new RepositoryException(src);
        }
      }
      return address;
  }
  
  //-------------------------------------
  // method:  addProfileRepositoryAddress
  //-------------------------------------
  /**
   * This method adds a profile repository address to the customer's secondary
   * addresses or address book.
   * @param pProfile the profile of the user.
   * @param pAddressName The name of the new address entry in address book.
   * @param pAddress The address to add to the address book.
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public void addProfileRepositoryAddress (RepositoryItem pProfile, String pAddressName, RepositoryItem pAddress)
      throws RepositoryException
    {
        MutableRepository repository = ((MutableRepository)pProfile.getRepository());
        CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();

        //Get 2ndary addresses from Profile:
        Map mymap = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());

        //add the new address to map of 2ndary addresses:
        if (!(mymap.containsValue(pAddress)))
          mymap.put(pAddressName, pAddress);
  }

  //-------------------------------------
  // method:  removeProfileRepositoryAddress
  //-------------------------------------
  /**
   * Removes the named address entry from the users Profile.  First, check to see if its the
   * default billing or shipping address, if it is then remove those.  Else, its in the users
   * secondary address map so obtain those entries and remove.
   *
   * @param pProfile profile from which the named address will be removed
   * @param pAddressName the nickname of the address that will be removed
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public void removeProfileRepositoryAddress(RepositoryItem pProfile, String pAddressName)
      throws RepositoryException {
    removeProfileRepositoryAddress(pProfile, pAddressName, false);
  }

  /**
   * Removes the named address entry from the users Profile.  First, check to see if its the
   * default billing or shipping address, if it is then remove those.  Else, its in the users
   * secondary address map so obtain those entries and remove. If address in the user's secondary
   * address map and <code>pCheckIfDefault</code> is true then checks if it is also default 
   * shipping address and if so sets default shipping address to null 
   *
   * @param pProfile profile from which the named address will be removed
   * @param pAddressName the nickname of the address that will be removed
   * @param pCheckIfDefault if true checks if secondary address that should
   *        be removed is also is default shipping address
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public void removeProfileRepositoryAddress(RepositoryItem pProfile, String pAddressName,
                                             boolean pCheckIfDefault) throws RepositoryException {

    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();

    if (pAddressName.equalsIgnoreCase(cpmgr.getDefaultBillingAddrName(getLocaleService().getLocale()))) {

      updateProperty(cpmgr.getBillingAddressPropertyName(), null, pProfile);

    } else if (pAddressName.equalsIgnoreCase(cpmgr.getDefaultShippingAddrName(getLocaleService().getLocale()))) {

      updateProperty(cpmgr.getShippingAddressPropertyName(), null, pProfile);

    } else {
      Map secondaryAddressMap = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
      if (secondaryAddressMap.containsKey(pAddressName)) {
        RepositoryItem address = getProfileAddress(pProfile, pAddressName);
        secondaryAddressMap.remove(pAddressName);
        updateProperty(cpmgr.getSecondaryAddressPropertyName(), secondaryAddressMap, pProfile);
        if (pCheckIfDefault) {
          //Check if the address is also default shipping address, if so 
          //set default shipping address to null          
          String repositoryId = address.getRepositoryId();
          CommercePropertyManager propertyManager = (CommercePropertyManager) getPropertyManager();
          RepositoryItem defaultShipAddress = getDefaultShippingAddress(pProfile);

          if ((defaultShipAddress != null)
              && (repositoryId.equalsIgnoreCase(defaultShipAddress.getRepositoryId()))) {

            updateProperty(propertyManager.getShippingAddressPropertyName(), null, pProfile);

          }
        }

      }
    }
  }

  //-------------------------------------
  // method:  addProfileGiftlistAddress
  //-------------------------------------
  /**
   * This method adds a giftlift address to the customer's giftlist address
   * property.
   * @param pProfile the profile of the user.
   * @param pAddressName The name of the new address entry in address book.
   * @param pAddress The address to add to the address book.
   * @exception RepositoryException if there was an error when creating the new repository item.
   */
  public void addProfileGiftlistAddress (RepositoryItem pProfile, String pAddressName, RepositoryItem pAddress)
      throws RepositoryException
    {
        MutableRepository repository = ((MutableRepository)pProfile.getRepository());
        CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();

        //Get 2ndary addresses from Profile:
        Map mymap = (Map) pProfile.getPropertyValue(cpmgr.getGiftlistAddressPropertyName());

        //add the new address to map of 2ndary addresses:
        if (!(mymap.containsValue(pAddress))) {
          mymap.put(pAddressName, pAddress);
        }
  }

  //-------------------------------------
  // method:  isAddressEmpty
  //-------------------------------------
  /**
   * Determines if the given repository address is empty.  The notion of empty
   * can vary, but in this implementation the properties are obtained from the emptyAddressPropertyNames
   * property in this class. If all of them are null or have no characters then the address is considered empty.
   * Override this method if the notion of empty should be different.
   *
   * @param pAddress a value of type RepositoryItem
   * @return true if the address is empty, false otherwise
   */
  public boolean isAddressEmpty(RepositoryItem pAddress) {
    return isAddressEmpty((Object) pAddress);
  }

  /**
   * Determines if the given address is empty.  The notion of empty
   * can vary, but in this implementation the properties are obtained from the emptyAddressPropertyNames
   * property in this class. If all of them are null or have no characters then the address is considered empty.
   * Override this method if the notion of empty should be different.
   *
   * @param pAddress a value of type Object
   * @return true if the address is empty, false otherwise
   */
  public boolean isAddressEmpty(Object pAddress) {
    if (pAddress == null)
      return true;

    String[] emptyProps = getEmptyAddressPropertyNames();
    String propValue = null;

    for (int i = 0; i < emptyProps.length; i++) {
      try {
        DynamicBeanInfo beanInfo = DynamicBeans.getBeanInfo(pAddress);

        if (beanInfo.hasProperty(emptyProps[i])) {
          propValue = (String) DynamicBeans.getPropertyValue(pAddress, emptyProps[i]);
          if (!StringUtils.isBlank(propValue))
            return false;
        }
        else if (isLoggingDebug()) {
          String[] msgArgs = { emptyProps[i], beanInfo.getBeanDescriptor().getDisplayName() };
          logDebug(msg(MSG_PROPERTY_NOT_FOUND, msgArgs));
        }
      }
      catch (IntrospectionException e) {
        if (isLoggingError())
          logError(e);
      }
      catch (PropertyNotFoundException e) {
        if (isLoggingError())
          logError(e);
      }
    }

    return true;
  }

  //-------------------------------------
  // method:  getDefaultShippingAddress
  //-------------------------------------
  /**
   * Get the default shipping address repository item by getting property name from the
   * property manager.
   *
   * @param pProfile profile that shipping address will be extracted from
   * @return the shipping address
   */
  public RepositoryItem getDefaultShippingAddress(RepositoryItem pProfile) {
    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
    return (RepositoryItem)pProfile.getPropertyValue(cpmgr.getShippingAddressPropertyName());
  }

  /**
   * Get the default Billing address repository item by getting property name from the
   * property manager.
   *
   * @param pProfile profile that Billing address will be extracted from
   * @return the Billing address
   */
  public RepositoryItem getDefaultBillingAddress(RepositoryItem pProfile) {
    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
    return (RepositoryItem)pProfile.getPropertyValue(cpmgr.getBillingAddressPropertyName());
  }


  /**
   * Get the default credit card repository item by getting property name from the
   * property manager.
   *
   * @param pProfile profile that will be extracted from
   * @return the Credit Card
   */
  public RepositoryItem getDefaultCreditCard(RepositoryItem pProfile) {
    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
    return (RepositoryItem)pProfile.getPropertyValue(cpmgr.getDefaultCreditCardPropertyName());
  }


  /**
   * Compares two contactInfo repository items to determine if they are equal.
   * <p>
   * True is returned if the two items are the same repository item.
   * <p>
   * Also compares the values of each property defined in the <code>compareAddressPropertyNames</code>
   * array if an array of property names is not provided.
   * <p>
   * Case is ignored for string comparisons.
   *
   * @param pAddress1 address to compare to pAddress2
   * @param pAddress2 address to compare to pAddress1
   * @param pPropertyNamesToCompare array of property names to use in the comparison. If null the
   * properties defined by <code>compareAddressPropertyNames</code> are used.
   * @return true if the addresses are considered equal
   */
  public boolean areAddressesEqual(RepositoryItem pAddress1, RepositoryItem pAddress2, String [] pPropertyNamesToCompare)
  {

      // If they are the same repository item then return true
      if(pAddress1.equals(pAddress2)) {
          return true;
      }

      return areAddressesEqual ((Object) pAddress1, (Object) pAddress2, pPropertyNamesToCompare);

  }


  /**
   * Compares two addresses to determine if they are equal.
   *
   * This method is used to compare any two address objects.
   *
   * Compares the values of each property defined in the <code>compareAddressPropertyNames</code>
   * array if an array of property names is not provided.
   * <p>
   * Case is ignored for string comparisons.
   *
   * @param pAddress1 address to compare to pAddress2
   * @param pAddress2 address to compare to pAddress1
   * @param pPropertyNamesToCompare array of property names to use in the comparison. If null the
   * properties defined by <code>compareAddressPropertyNames</code> are used.
   * @return true if the addresses are considered equal
   */
  public boolean areAddressesEqual(Object pAddress1, Object pAddress2, String [] pPropertyNamesToCompare)
  {
      String[] addrPropNames;
      if(pPropertyNamesToCompare == null || pPropertyNamesToCompare.length ==0)
          addrPropNames = getCompareAddressPropertyNames();
      else
          addrPropNames = pPropertyNamesToCompare;


      try  {
          for (int i = 0; i < addrPropNames.length; i++)
          {
              Object address1value = DynamicBeans.getSubPropertyValue(pAddress1, addrPropNames[i]);
              Object address2value = DynamicBeans.getSubPropertyValue(pAddress2, addrPropNames[i]);

              //if they are both null continue
              if(address1value == null && address2value == null)
                  continue;

              //if one is null and not the other, they are not equal
              if((address1value == null && address2value != null) || (address2value == null && address1value != null))
                  return false;

              //if they are strings, handle them a bit special.
              if(address1value instanceof String)
              {
                  String stringvalue1 = (String) address1value;
                  String stringvalue2 = (String) address2value;

                  if(StringUtils.isBlank(stringvalue1) && StringUtils.isBlank(stringvalue2))
                      continue;
                  if(!stringvalue1.equalsIgnoreCase(stringvalue2))
                      return false;

              }
              else
                  if(!address1value.equals(address2value))
                      return false;
          }
      }
      catch (PropertyNotFoundException e)
      {
          if(isLoggingError())
              logError(msg(MSG_ADDRESS_COMPARE_FAILED));
          if(isLoggingError())
              logError(e.toString());
          return false;
      }

      return true;
  }


  //-----------------------------------------------
  // These methods are used by the CommerceProfileFormHandler
  // and the CommerceProfileRequestServlet to load a user's
  // orders when they log in.
  //-----------------------------------------------

  //-----------------------------------------------
  /**
   * When an auto-login operation occurs, we attempt to find any old shopping carts
   * from previous sessions. In this process we may move items from any transient
   * shopping carts to their previous persistent shopping carts. However if the
   * <code>mergeOrders</code> property is to set false (default true), then we persist
   * any existing transient shopping carts that may have built up while navigating as an
   * anonymous user. All the shopping carts are also repriced to make sure that their
   * prices are up to date. This can be toggeled through the <code>repriceOrderOnLogin</code>
   * property. This method will resolve the Profile out of the request, plus the
   * user's session-scoped pricing model holder, order holder, and their locale.
   * @param pRequest the request object that the login operation occurs within
   * @param pResponse the response object that the login operation occurs within
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void loadUserShoppingCartForLogin(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    RepositoryItem profile = (RepositoryItem)pRequest.resolveName(mProfilePath);
    loadUserShoppingCartForLogin(profile, pRequest, pResponse);
  }

 /**
   * When an auto-login operation occurs, we attempt to find any old shopping carts
   * from previous sessions. In this process we may move items from any transient
   * shopping carts to their previous persistent shopping carts. However if the
   * <code>mergeOrders</code> property is to set false (default true), then we persist
   * any existing transient shopping carts that may have built up while navigating as an
   * anonymous user. All the shopping carts are also repriced to make sure that their
   * prices are up to date. This can be toggeled through the <code>repriceOrderOnLogin</code>
   * property. This method will use the supplied Profile, and resolve the user's session-scoped
   * pricing model holder, order holder, and their locale.
   * @param pProfile the user who under went auto-login
   * @param pRequest the request object that the login operation occurs within
   * @param pResponse the response object that the login operation occurs within
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public void loadUserShoppingCartForLogin(RepositoryItem pProfile,
                                           DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (pProfile != null) {
      PricingModelHolder pricingModelHolder=(PricingModelHolder)pRequest.resolveName(mUserPricingModelsPath);
      if (pricingModelHolder != null)
        pricingModelHolder.initializePricingModels();
      OrderHolder orderHolder = (OrderHolder)pRequest.resolveName(mShoppingCartPath);
      Locale locale = getUserLocale(pRequest, pResponse);

      try {
        loadUserShoppingCartForLogin(pProfile, orderHolder, pricingModelHolder, locale);
      }
      catch (CommerceException exc) {
        if (isLoggingError())
          logError(exc);
      }
    }
  }

  //-----------------------------------------------
  /**
   * When a login operation occurs, we attempt to find any old shopping carts
   * from previous sessions. In this process we may move items from any transient
   * shopping carts to their previous persistent shopping carts. However if the
   * <code>mergeOrders</code> property is to set false (default true), then we persist
   * any existing transient shopping carts that may have built up while navigating as an
   * anonymous user. All the shopping carts are also repriced to make sure that their
   * prices are up to date. This can be toggeled through the <code>repriceOrderOnLogin</code>
   * property.
   * @param pProfile the user profile
   * @param pShoppingCart the OrderHolder component for this user
   * @param pUserPricingModels the pricing models for this user
   * @param pLocale the locale of the user
   * @exception CommerceException is any errors occur while loading, repricing or persisting
   * the shopping carts
   */
  public void loadUserShoppingCartForLogin(RepositoryItem pProfile,
                                           OrderHolder pShoppingCart,
                                           PricingModelHolder pUserPricingModels,
                                           Locale pLocale)
       throws CommerceException
  {
    try {
      if (isLoggingDebug())
        logDebug("Acquiring transaction lock within loadUserShoppingCartForLogin");
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

    try {
      loadShoppingCarts(pProfile, pShoppingCart);
      repriceShoppingCarts(pProfile, pShoppingCart, pUserPricingModels, pLocale, getRepriceOrderPricingOp());
      persistShoppingCarts(pProfile, pShoppingCart);
    }

    // Release the transaction lock in a finally clause in case any of the code
    // above throws exceptions.  We don't want to end up holding the lock forever
    // in this case.

    finally
    {
      if (isLoggingDebug())
        logDebug("Releasing transaction lock within loadUserShoppingCartForLogin");

      releaseTransactionLock();
    }
  }

  /**
   * This method finds all the shopping carts for the user and places them into the
   * session-scope OrderHolder component. "Shopping Carts" are defined to be orders
   * whose state is INCOMPLETE.
   * Only orders that are allowed to share the shopping cart with the current site are found.
   * The orders are sorted by last activity date, and the
   * last order touched is made the current order. This method will optionally
   * merge the current shopping cart for the session (from the OrderHolder.current property)
   * with the first persistent order loaded from the database. This functionality is
   * toggeled through the <code>mergeOrders</code> property of this component.
   *
   * NOTE: This method used to acquire a transaction lock, but we've found deadlock
   * issues and have decided to remove the locks.  See bug 146012.  This method now
   * performs its actions without a lock.
   *
   * @param pProfile the user profile
   * @param pShoppingCart the OrderHolder component for this user
   * @exception CommerceException is any errors occur while loading the shopping carts
   */
  public void loadShoppingCarts(RepositoryItem pProfile, OrderHolder pShoppingCart)
       throws CommerceException
  {
    if (isLoggingDebug())
      logDebug("loading orders for profile " + pProfile);

    if ((pProfile == null) || (pShoppingCart == null))
      return;

    // only load the shopping carts if orders are being persisted
    if(!shouldLoadShoppingCarts(pShoppingCart)) 
      return;

    Collection<String> siteIds = getSiteGroupManager().getSharingSiteIds(Constants.SHOPPING_CART_SHAREABLE_TYPE);
    int[] orderStates = { StateDefinitions.ORDERSTATES.getStateValue(OrderStates.INCOMPLETE) };
    List orders = getOrderManager().getOrderQueries().getOrdersForProfileInState(pProfile.getRepositoryId(),
                                                               0, -1, orderStates,
                                                               getSortOrdersByProperty(), isAscendingOrder(),
                                                               siteIds);

    Collection currentOrders = pShoppingCart.getSaved();
    if ( currentOrders != null) {
      // If all the orders are already in the shopping cart then do nothing
      if ( currentOrders.containsAll(orders) ) {
        if (isLoggingDebug())
          logDebug("All orders are already in there so we can just return.");
        return;
      }
      else {
        // Otherwise, cut back to the ones that are missing from the saved
        // list. They'll get added later on.
        try {
          orders.removeAll(currentOrders);
        }
        catch ( UnsupportedOperationException uoe) {
          logError(uoe);
        }
        catch ( ClassCastException cce) {
          logError(cce);
        }
        catch ( IllegalArgumentException iae) {
          logError(iae);
        }
      }
    }

    if (isLoggingDebug())
      logDebug("Found orders for profile[" + pProfile.getRepositoryId() + "]=" + orders);
    if ((orders != null) && (orders.size() > 0)) {

      int order_index = findOrderToLoad(orders);

      if (isLoggingDebug())
        logDebug("The index returned is: " + order_index);

      Order persistentCurrent = null;

      if (order_index >= 0)
        persistentCurrent = (Order)orders.remove(order_index);
      else {
        if (isLoggingDebug())
          logDebug("The index returned is not in the list");
      }

      if (persistentCurrent != null)
      {
        //set the new order as current if current is empty
        if (pShoppingCart.isCurrentEmpty())
        {
          if (isLoggingDebug())
            logDebug("Current order is empty, so make " + persistentCurrent + " current");
          pShoppingCart.setCurrent(persistentCurrent);
        }
        else
        {
          Order activeCurrent = pShoppingCart.getCurrent();
          if (isMergeOrders())
          {
            // if we are supposed to merge the orders, then put
            // everything in the current order for this session into
            // the first persistent order, and make the persistent order
            // the current for this session
            if (isLoggingDebug())
              logDebug("Merge order " + activeCurrent + " into order " + persistentCurrent);

            synchronized(persistentCurrent) {
              getOrderManager().mergeOrders(activeCurrent, persistentCurrent);
            }

            if (isLoggingDebug())
              logDebug("Make merged order " + persistentCurrent + " current");
            pShoppingCart.setCurrent(persistentCurrent);
          }
          //add it to the saved list only if it doesn't match the active order
          else if(!activeCurrent.getId().equals(persistentCurrent.getId()))
          {
            if (isLoggingDebug())
              logDebug("Do not merge, so add " + persistentCurrent + " to saved list");
            pShoppingCart.getSaved().add(persistentCurrent);
          }
        }
      }

      // If we have any more persistent orders left, also add those to the saved list
      int size = orders.size();
      if (size > 0) {
        if (isLoggingDebug())
          logDebug("Add the rest of the orders " + orders + " to the saved list");
        for (int c=0; c<size; c++) {
          Order order = (Order)orders.get(c);
          if (isLoggingDebug())
            logDebug("Adding order " + order + " to saved list");
          pShoppingCart.getSaved().add(order);
        }
      }
    }
  }

  /**
   * @deprecated Please use loadShoppingCarts instead
   * @param pProfile the user profile
   * @param pShoppingCart the OrderHolder component for this user
   * @exception CommerceException is any errors occur while loading the shopping carts
   */
  public void loadShoppingCartsWithLock(RepositoryItem pProfile, OrderHolder pShoppingCart)
       throws CommerceException
  {
    loadShoppingCarts(pProfile, pShoppingCart);
  }

  /**
   * Reprice all of the orders in the OrderHolder component. The locale and the UserPricingModels
   * component are resolved out of the request object
   * @param pProfile the user profile
   * @param pShoppingCart the OrderHolder component for this user
   * @param pUserPricingModels the pricing models for this user
   * @param pLocale the locale of the user
   * @exception CommerceException is any errors occur while repricing the shopping carts
   */
  public void repriceShoppingCarts(RepositoryItem pProfile,
                                   OrderHolder pShoppingCart,
                                   String pPricingOperation,
                                   DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
       throws ServletException, IOException, CommerceException
  {
    PricingModelHolder pricingModelHolder=(PricingModelHolder)pRequest.resolveName(mUserPricingModelsPath);
    Locale locale = getUserLocale(pRequest, pResponse);
    repriceShoppingCarts(pProfile, pShoppingCart, pricingModelHolder, locale, pPricingOperation);
  }

  /**
   * Reprice all of the orders in the OrderHolder component
   * @param pProfile the user profile
   * @param pShoppingCart the OrderHolder component for this user
   * @param pUserPricingModels the pricing models for this user
   * @param pLocale the locale of the user
   * @exception CommerceException is any errors occur while repricing the shopping carts
   */
  public void repriceShoppingCarts(RepositoryItem pProfile,
                                   OrderHolder pShoppingCart,
                                   PricingModelHolder pUserPricingModels,
                                   Locale pLocale)
       throws CommerceException
  {
    repriceShoppingCarts(pProfile, pShoppingCart, pUserPricingModels, pLocale, getRepriceOrderPricingOp());
  }

  /**
   * Reprice all of the orders in the OrderHolder component
   * @param pProfile the user profile
   * @param pShoppingCart the OrderHolder component for this user
   * @param pUserPricingModels the pricing models for this user
   * @param pLocale the locale of the user
   * @param pPricingOperation The operations which are acceptable are defined in the
   * <code>atg.commerce.pricing.PricingConstants</code> interface. They include:
   * <UL>
   * <LI>PricingConstants.OP_REPRICE_ORDER_TOTAL -> <code>ORDER_TOTAL</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL -> <code>ORDER_SUBTOTAL</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING -> <code>ORDER_SUBTOTAL_SHIPPING</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_TAX -> <code>ORDER_SUBTOTAL_TAX</code>
   * <LI>PricingConstants.OP_REPRICE_ITEMS -> <code>ITEMS</code>
   * <LI>PricingConstants.OP_REPRICE_SHIPPING -> <code>SHIPPING</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER -> <code>ORDER</code>
   * <LI>PricingConstants.OP_REPRICE_TAX -> <code>TAX</code>
   * <LI>PricingConstants.OP_NO_REPRICE -> <code>NO_REPRICE</code>
   * </UL>
   *
   * NOTE: This method used to acquire a transaction lock, but we've found deadlock
   * issues and have decided to remove the locks.  See bug 146012.  This method now
   * performs its actions without a lock.
   *
   * @exception CommerceException is any errors occur while repricing the shopping carts
   */
  public void repriceShoppingCarts(RepositoryItem pProfile,
                                   OrderHolder pShoppingCart,
                                   PricingModelHolder pUserPricingModels,
                                   Locale pLocale,
                                   String pPricingOperation)
       throws CommerceException
  {
    if (isLoggingDebug())
      logDebug("In repriceShoppingCarts");

    if (! isRepriceOrderOnLogin())
      return;

    // If the current order is not empty, then reprice the order
    if (! pShoppingCart.isCurrentEmpty()) {
      Order order = pShoppingCart.getCurrent();
      repriceOrder(order, pProfile, pUserPricingModels, pLocale, pPricingOperation);
    }

    // For each of the saved carts, if they are not empty then reprice them
    if (! pShoppingCart.isSavedEmpty()) {
      Collection savedOrders = pShoppingCart.getSaved();
      Iterator iterator = savedOrders.iterator();
      while (iterator.hasNext()) {
        Order order = (Order)iterator.next();
        if (! orderEmpty(order)) {
          repriceOrder(order, pProfile, pUserPricingModels, pLocale, pPricingOperation);
        }
      }
    }

  }

  /**
   * @deprecated use repriceShoppingCarts
   * @exception CommerceException is any errors occur while repricing the shopping carts
   */
  public void repriceShoppingCartsWithLock(RepositoryItem pProfile,
                                           OrderHolder pShoppingCart,
                                           PricingModelHolder pUserPricingModels,
                                           Locale pLocale,
                                           String pPricingOperation)
       throws CommerceException
  {
    repriceShoppingCarts(pProfile, pShoppingCart, pUserPricingModels, pLocale, pPricingOperation);
  }

  /**
   * Reprice a single given order, this calls into the PricingTools.performPricingOperation
   * method.
   * @param pOrder the order to reprice
   * @param pProfile the user profile
   * @param pUserPricingModels the pricing models for this user
   * @param pLocale the locale of the user
   * @param pPricingOperation The operations which are acceptable are defined in the
   * <code>atg.commerce.pricing.PricingConstants</code> interface. They include:
   * <UL>
   * <LI>PricingConstants.OP_REPRICE_ORDER_TOTAL -> <code>ORDER_TOTAL</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL -> <code>ORDER_SUBTOTAL</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_SHIPPING -> <code>ORDER_SUBTOTAL_SHIPPING</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER_SUBTOTAL_TAX -> <code>ORDER_SUBTOTAL_TAX</code>
   * <LI>PricingConstants.OP_REPRICE_ITEMS -> <code>ITEMS</code>
   * <LI>PricingConstants.OP_REPRICE_SHIPPING -> <code>SHIPPING</code>
   * <LI>PricingConstants.OP_REPRICE_ORDER -> <code>ORDER</code>
   * <LI>PricingConstants.OP_REPRICE_TAX -> <code>TAX</code>
   * <LI>PricingConstants.OP_NO_REPRICE -> <code>NO_REPRICE</code>
   * </UL>
   * @exception CommerceException is any errors occur while repricing the shopping carts
   * @see atg.commerce.pricing.PricingTools.performPricingOperation
   */
  protected void repriceOrder(Order pOrder,
                                    RepositoryItem pProfile,
                                    PricingModelHolder pUserPricingModels,
                                    Locale pLocale,
                                    String pPricingOperation)
       throws CommerceException
  {
    if (isLoggingDebug())
      logDebug("reprice order " + pOrder);
    synchronized(pOrder) {
      getPricingTools().performPricingOperation(pPricingOperation,
                                                pOrder,
                                                pUserPricingModels,
                                                pLocale,
                                                pProfile,
                                                getPricingParameters());
      getOrderManager().updateOrder(pOrder);
    }
  }

  /**
   * This returns the map that will be passed into pricingTools.performPricingOperation
   * as the pExtraParameters.  The default implementation of this is to return a
   * new HashMap
   * @return A new empty HashMap
   **/
  public Map getPricingParameters()
  {
    return new HashMap();
  }


  /**
   * This method will persist the current and any "saved" shopping carts.
   *
   * NOTE: This method used to acquire a transaction lock, but we've found deadlock
   * issues and have decided to remove the locks.  See bug 146012.  This method now
   * performs its actions without a lock.
   *
   * @param pProfile the user profile
   * @param pShoppingCart the OrderHolder component for this user
   * @exception CommerceException is any errors occur while persisting the shopping carts
   */
  public void persistShoppingCarts(RepositoryItem pProfile, OrderHolder pShoppingCart)
       throws CommerceException
  {
    if (isLoggingDebug())
      logDebug("In persistShoppingCarts");

    if (pShoppingCart.isCurrentExists()) {
      try {
        Order current = pShoppingCart.getCurrent();
        synchronized(current) {
          updateOrderProfileId(current, pProfile);
          if ((current != null) && (current.isTransient()) &&
              (pShoppingCart.isPersistOrders())) {
            persistOrderIfNeeded(current);
          }
        }
      }
      catch (CommerceException exc) {
        if (isLoggingError())
          logError(exc);
      }
    }

    if (! pShoppingCart.isSavedEmpty()) {
      Collection savedList = pShoppingCart.getSaved();
      Iterator iterator = savedList.iterator();
      while (iterator.hasNext()) {
        Order order = (Order)iterator.next();
        try {
          synchronized(order) {
            updateOrderProfileId(order, pProfile);
            if ((order.isTransient()) &&
                (pShoppingCart.isPersistOrders())) {
              persistOrderIfNeeded(order);
            }
          }
        }
        catch (CommerceException exc) {
          if (isLoggingError())
            logError(exc);
        }
      }
    }
  }

  /**
   * @deprecated use persistShoppingCarts
   * @param pProfile the user profile
   * @param pShoppingCart the OrderHolder component for this user
   * @exception CommerceException is any errors occur while persisting the shopping carts
   */
  public void persistShoppingCartsWithLock(RepositoryItem pProfile, OrderHolder pShoppingCart)
       throws CommerceException
  {
    persistShoppingCarts(pProfile, pShoppingCart);
  }

  /**
   * Returns the locale associated with the request. The method first searches
   * for a request paramater named <code>locale</code>. This value can be
   * either a java.util.Locale object or a String which represents the locale.
   * Next if the <code>useRequestLocale</code> property is true, then the locale
   * of the request will be returned. Finally, if the locale cannot be determined,
   * the the <code>defaultLocale</code> property is used.
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
   * Returns true is the supplied Order reference is null or if the
   * CommerceItem count is less than or equal to 0.
   */
  private final boolean orderEmpty(Order pOrder) {
    if ((pOrder == null) || (pOrder.getCommerceItemCount() <= 0))
      return true;
    else
      return false;
  }

  /**
   * If the order is transient and not empty then make it persistent through the Order Manager.
   */
  protected void persistOrderIfNeeded(Order pOrder)
       throws CommerceException
  {
    if ((pOrder != null) && (pOrder.isTransient())) {
      if (isLoggingDebug())
        logDebug("Persist transient order " + pOrder);
      getOrderManager().addOrder(pOrder);
    }
  }

  /**
   * This method makes sure the profile id of the order is the same as the current
   * user. (e.g. in case user ids change during registration).
   */
  protected void updateOrderProfileId(Order pOrder, RepositoryItem pProfile)
       throws CommerceException
  {
    if ((pOrder != null) && (pProfile != null)) {
      String orderProfileId = pOrder.getProfileId();
      String profileId = pProfile.getRepositoryId();
      if ((orderProfileId == null) || (! profileId.equals(orderProfileId))) {
        if (isLoggingDebug())
          logDebug("Profile Id for order " + pOrder + " is not up to date, reset to " + profileId);
        // Make sure the profile id of the order is now the same as the registered user
        pOrder.setProfileId(profileId);
        getOrderManager().updateOrder(pOrder);
      }
    }
  }

  //-----------------------------------------------
  // Credit Card Utility Functions
  //-----------------------------------------------


  /**
   * Copy the credit card passed in the parameter pCreditCard to the profile
   * passed in the parameter pProfile.  This is done by creating a new Credit Card itemDescriptor
   * object by making use of the @see atg.commmerce.profile.CommerceProfileTools
   * class to create a creditCardItem.  Next, the @see #copyCreditCard(CreditCard, RepositoryItem)
   * is called to perform the actual copying of data.  The resulting object is then
   * updated to the repository and copied into the users map of credit cards.
   *
   * @param pCreditCard the credit card that is to be copied to a users profile
   * @param pProfile the user profile
   * @deprecated
   */
  public void copyCreditCardToProfile(CreditCard pCreditCard,
                                      RepositoryItem pProfile)
  {
    copyCreditCardToProfile (pCreditCard, pProfile, null);
  }
  /**
   * Copy the credit card passed in the parameter pCreditCard to the profile
   * passed in the parameter pProfile.  This is done by creating a new Credit Card itemDescriptor
   * object by making use of the @see atg.commmerce.profile.CommerceProfileTools
   * class to create a creditCardItem.  Next, the @see #copyCreditCard(CreditCard, RepositoryItem)
   * is called to perform the actual copying of data.  The resulting object is then
   * updated to the repository and copied into the users map of credit cards.
   *
   * @param pCreditCard the credit card that is to be copied to a users profile
   * @param pProfile the user profile
   */
  public void copyCreditCardToProfile(CreditCard pCreditCard,
                                      RepositoryItem pProfile,
                                      String pNickName)
  {

    MutableRepositoryItem creditCardRepositoryItem;
    MutableRepository repository = (MutableRepository)pProfile.getRepository();

    if (!isCreditCardEmpty(pCreditCard)) {
      if (isLoggingDebug())
        logDebug("adding credit card to users profile");

      try {
        creditCardRepositoryItem = createCreditCardItem(pProfile);
        copyCreditCard(pCreditCard, creditCardRepositoryItem);
        repository.updateItem(creditCardRepositoryItem);
        addCreditCardToUsersMap(pProfile, creditCardRepositoryItem, pNickName);
      } catch (RepositoryException re) {
        if (isLoggingError())
          logError(re);
      }
    }
  }


  /**
   * Copy the credit card from the users profile to the payment group.  The credit
   * card item named by pNickname is obtained from the pProfile parameter and then
   * copied in theo the paymentGroup parameter.  This is done by making calls
   * to @see #copyCreditCard(RepositoryItem, CreditCard)
   *
   * @param pNickname a value of type 'String'
   * @param paymentGroup a value of type 'CreditCard'
   * @param pProfile the user profile
   * @param pLocale the user local
   */
  public void copyCreditCardToPaymentGroup(String pNickname,
                                           CreditCard paymentGroup,
                                           RepositoryItem pProfile,
                                           Locale pLocale)
  {
    RepositoryItem creditCard = getCreditCardByNickname(pNickname, pProfile);
    if (creditCard != null) {
      copyCreditCard(creditCard, paymentGroup);
    } else {
      if (isLoggingError()) {
        String msg = CommerceProfileUserMessage.format(NO_CREDIT_CARD, pNickname, pLocale);
        logError(msg);
      }
    }
  }

  /**
   * Copying a credit card is a two step process.
   *
   * <P>
   *
   * The first is copying the shallow properties of the credit card.  These are objects
   * like String, Integer etc. that can be copied.  This shallow copying is performed
   * by the @see #copyShallowCreditCardProperties method.
   *
   * <P>
   *
   * Next, any post copying is done.  The single object that must have a "deep" coyp
   * performed on it is the billingAddress of the credit card.  The billingAddress
   * is obtained from both objects and then a call is made to the
   * @see OrderManager to perform the copy.
   *
   * <P>
   *
   * If there is additional deep copying that needs to be done, this method should be overriden.
   *
   *
   * @param pFromCreditCard a value of type 'RepositoryItem'
   * @param pToCreditCard a value of type 'CreditCard'
   */
  public void copyCreditCard(RepositoryItem pFromCreditCard,
                             CreditCard pToCreditCard)
  {
    //RepositoryItem toAddress;
    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
    try {
      copyShallowCreditCardProperties(pFromCreditCard, pToCreditCard);
      RepositoryItem fromAddress = (RepositoryItem)pFromCreditCard.getPropertyValue(cpmgr.getCreditCardBillingAddressPropertyName());
      OrderTools.copyAddress(fromAddress, pToCreditCard.getBillingAddress());
    } catch (PropertyNotFoundException pnfe) {
      if (isLoggingError())
        logError(pnfe);
    } catch (CommerceException ce) {
      if (isLoggingError())
        logError(ce);
    }
  }

  /**
   * Copying a credit card is a two step process.
   *
   * <P>
   *
   * The first is copying the shallow properties of the credit card.  These are objects
   * like String, Integer etc. that can be copied.  This shallow copying is performed
   * by the @see #copyShallowCreditCardProperties method.
   *
   * <P>
   *
   * Next, any post copying is done.  The single object that must have a "deep" coyp
   * performed on it is the billingAddress of the credit card.  The billingAddress
   * is obtained from both objects and then a call is made to the
   * @see OrderManager to perform the copy.
   *
   * <P>
   *
   * If there is additional deep copying that needs to be done, this method should be overriden.
   *
   *
   * @param pFromCreditCard a value of type 'RepositoryItem'
   * @param pToCreditCard a value of type 'CreditCard'
   */
  public void copyCreditCard(CreditCard pFromCreditCard,
                             RepositoryItem pToCreditCard)
  {
    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
    try {
      copyShallowCreditCardProperties(pFromCreditCard, pToCreditCard);
      RepositoryItem toAddress = (RepositoryItem)pToCreditCard.getPropertyValue(cpmgr.getCreditCardBillingAddressPropertyName());


      OrderTools.copyAddress(pFromCreditCard.getBillingAddress(), toAddress);
    } catch (PropertyNotFoundException pnfe) {
      if (isLoggingError())
        logError(pnfe);
    } catch (CommerceException ce) {
      if (isLoggingError())
        logError(ce);
    }
  }
  
 
  /**
   * The shallow properties of a credit card are copied.  This is done by consulting
   * the @see #propertyManager for a String array of properties to copy, as named
   * by the <code>shallowCreditCardPropertyNames</code> property.  These properties
   * are then copied via DynamicBeans
   *
   * @param pFromCreditCard the credit card that the address is copied from
   * @param pToCreditCard the destination credit card for address
   * @exception PropertyNotFoundException if a property listed by shallowCreditCardPropertyNames
   * is not found
   */
  public void copyShallowCreditCardProperties(Object pFromCreditCard,
                                              Object pToCreditCard)
    throws PropertyNotFoundException
  {
    Object value;
    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
    String[] cardProperties = cpmgr.getShallowCreditCardPropertyNames();

    for (int i=0; i<cardProperties.length; i++) {
      value = DynamicBeans.getPropertyValue(pFromCreditCard, cardProperties[i]);
      DynamicBeans.setPropertyValue(pToCreditCard, cardProperties[i], value);
    }
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
   */
  public boolean isCreditCardEmpty(CreditCard pCreditCard)
  {
    try {
      if (mCreditCardProperties != null) {
        for (int i=0; i<mCreditCardProperties.length; i++) {
          Object value = DynamicBeans.getPropertyValue(pCreditCard, mCreditCardProperties[i]);
          if (!(value == null || value.toString().equals("")))
            return false;
        }
      }
    } catch (PropertyNotFoundException pnfe) {
      if (isLoggingError())
        logError(pnfe);
    }
    return true;
  }

  //---------------------------------------------------------------------------

  /**
   * Return the user profile of the user who placed a given order.
   *
   * @param pOrder
   *    The order whose owner is being retreived.
   * @throws RepositoryException
   *    if any error occurs while looking up the user profile in the
   *    profile repository.
   **/

  public RepositoryItem getProfileForOrder(Order pOrder)
    throws RepositoryException
  {
    return getProfileRepository().getItem(pOrder.getProfileId(), getDefaultProfileType());
  }

  //---------------------------------------------------------------------------

  /**
   * Return the user profile of the user who placed the order with
   * id <code>pOrderId</code>.
   *
   * @param pOrderId
   *    The order id of the order whose owner is being retreived.
   * @throws CommerceException
   *    if any error occurs while loading the order from the order
   *    repository.
   * @throws RepositoryException
   *    if any error occurs while looking up the user profile in the
   *    profile repository.
   **/

  public RepositoryItem getProfileForOrder(String pOrderId)
    throws RepositoryException, CommerceException
  {
    Repository rep = getOrderManager().getOrderTools().getOrderRepository();

    RepositoryItem order = rep.getItem(pOrderId, getOrderItemDescriptorName());
    if (order == null)
      return null;

    String profileId = (String) order.getPropertyValue(getProfileIdPropertyName());
    if (profileId == null)
      return null;

    return getProfileRepository().getItem(profileId, getDefaultProfileType());
  }

  //---------------------------------------------------------------------------

  /**
   * Return the index of the order that should be used as the primary order for this user.
   * This method will return the most recently changed if the @see isSelectLastModifiedOrder is
   * set to true which is the 0th element in the default implementation due to the order by
   * clause.
   *
   * @param pOrders
   *    A List of the orders to select from
   * @return the index in the list which should be used, -1 if no order is found
   **/

  public int findOrderToLoad(List pOrders) {
    if (pOrders.size() < 0)
      return -1;

    // To support the original functionality
    if (isSelectLastModifiedOrder())
      return 0;

    // Otherwise we will scan the list of orders for the order which is not marked as
    // explicitly saved

    Iterator i = pOrders.iterator();
    Order curOrder = null;
    int counter=0;
    while (i.hasNext()) {
      curOrder = (Order) i.next();
      if (!curOrder.isExplicitlySaved()) {
        if (isLoggingDebug())
          logDebug("The selected order is: " + curOrder.getId());
        return counter;
      }
      counter++;
    }
    // The order was not found return -1 indicating that they are all marked as explicitly saved
    return -1;
  }

  /**
   * Copy the active promotions from the guest user to the authenticated user
   *
   * @param pGuestUser the user to copy values from
   * @param pAuthenticatedUser the user to copy values to
   */
  protected void addActivePromotions(RepositoryItem pGuestUser,
                                     RepositoryItem pAuthenticatedUser)
    throws RepositoryException
  {
    PromotionTools promotionTools = getPromotionTools();
    String activePromosProp = promotionTools.getActivePromotionsProperty();
    Object promoStatuses = pGuestUser.getPropertyValue(activePromosProp);
    if(isLoggingDebug())
      logDebug("Adding activePromotions for user " + pAuthenticatedUser);

    if (promoStatuses instanceof Collection) {
      Collection promotions = promotionTools.convertPromoStatusToPromo((Collection) promoStatuses);
      MutableRepository repository = getProfileRepository();
      MutableRepositoryItem mutableItem = null;
      try {
        mutableItem = (MutableRepositoryItem)pAuthenticatedUser;
      }
      catch (ClassCastException exc) {
        String id = pAuthenticatedUser.getRepositoryId();
        mutableItem = repository.getItemForUpdate(id, getDefaultProfileType());
      }

      Object activePromotionsValue = pAuthenticatedUser.getPropertyValue(promotionTools.getActivePromotionsProperty());

      // Grant the promotion to the logged in user if they don't already
      // have it and revoke it from the guest user, so that apps like Analytics
      // that track promotion grants, revokes, and uses have accurate data
      if(promotions != null) {
        Iterator promoterator = promotions.iterator();
        RepositoryItem promotion = null;
        while(promoterator.hasNext()) {
          promotion = (RepositoryItem) promoterator.next();

          boolean activePromotion = false;

          if (activePromotionsValue instanceof Collection) {
            Collection promotionStatuses = (Collection)activePromotionsValue;
            activePromotion = promotionTools.isPromotionInPromotionStatuses(promotion, promotionStatuses);
          }

          if (!activePromotion) {
            promotionTools.addPromotion(mutableItem, promotion, PromotionTools.COPIED_DURING_LOGIN);
            promotionTools.sendPromotionRevokedEvent(pGuestUser, promotion, PromotionTools.REVOKED_DURING_LOGIN);
          }
        }
      }
    }
  }


  /**
   * After registration, any transient orders are made persistent through the
   * <code>persistShoppingCarts</code> method of the CommerceProfileTools component
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pProfile The profile of the just created user
   * @param pShoppingCart The shopping cart for the new user
   * @exception ServletException if there was an error while executing the code
   */
  protected void postCreateUser(DynamoHttpServletRequest pRequest,
                                  DynamoHttpServletResponse pResponse,
                                  RepositoryItem pProfile,
                                  OrderHolder pShoppingCart)
    throws ServletException
  {
    if (pShoppingCart != null) {
      try {
        persistShoppingCarts(pProfile, pShoppingCart);
      }
      catch (CommerceException exc) {
        if (isLoggingError())
          logError(exc);
      }
    }
  }

  /**
   * After logging in the user's session cached promotions are reloaded into the PricingModelHolder.
   * In addition any non-transient orders are made persistent and old shopping carts are loaded
   * from the database.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pProfile The profile of the user that just logged in
   * @param pShoppingCart The shopping cart for the user that just logged in
   * @param pPricingModelHolder The pricing models for the user that just logged in
   * @exception ServletException if there was an error while executing the code
   */
  protected void postLoginUser(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse,
                                 RepositoryItem pProfile,
                                 OrderHolder pShoppingCart,
                                 PricingModelHolder pPricingModelHolder)
    throws ServletException
  {
    if (pShoppingCart != null) {
      synchronized (pShoppingCart) {
        if (pPricingModelHolder != null) {
          pPricingModelHolder.initializePricingModels();
        }
        try {
          Locale locale = getUserLocale(pRequest, pResponse);
          loadUserShoppingCartForLogin(pProfile, pShoppingCart, pPricingModelHolder, locale);
        }
        catch (CommerceException exc) {
          if (isLoggingError())
            logError(exc);
        }
        catch(IOException io) {
          if(isLoggingError())
            logError(io);
        }
      } // synchronized
    } // if
  }



  //-------------------------------------
  /**
   * Utility method to format a message with no
   * arguments using our resource bundle.
   * @param pMsgKey key of message to format
   * @return the formatted message
   **/
  private static final String msg(String pMsgKey)
  {
    return ResourceUtils.getMsgResource(pMsgKey, RESOURCE_BUNDLE_NAME, sResourceBundle);
  }

  /**
   * Utility method to format a message with
   * arguments using our resource bundle.
   * @param pMsgKey key of message to format
   * @return the formatted message
   **/
  private static final String msg(String pMsgKey, String[] pMsgArgs)
  {
    return ResourceUtils.getMsgResource(pMsgKey, RESOURCE_BUNDLE_NAME, sResourceBundle, pMsgArgs);
  }

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
        RepositoryItem profileItem = ServletUtil.getCurrentUserProfile();
        if (profileItem != null) {
          String profileId = profileItem.getRepositoryId();
          DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
          request.setAttribute(LOCK_NAME_ATTRIBUTE_NAME, profileId);
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

  /**
   * Attempt to release a local lock
   **/

  protected void releaseTransactionLock()
  {
    try {
      TransactionLockService service = getTransactionLockService();
      if(service != null) {
        DynamoHttpServletRequest request = ServletUtil.getCurrentRequest();
        String lockName = (String) request.getAttribute(LOCK_NAME_ATTRIBUTE_NAME);
        if (lockName != null) {
          service.releaseTransactionLock(lockName);
          request.removeAttribute(LOCK_NAME_ATTRIBUTE_NAME);
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

    TransactionLockFactory factory = getTransactionLockFactory();

    if(factory != null)
      mTransactionLockService = factory.getServiceInstance(this);
    else
      if(isLoggingWarning())
        logWarning(ResourceUtils.getMsgResource("missingTransactionLockFactory",
                                                RESOURCE_BUNDLE_NAME, sResourceBundle));

    return mTransactionLockService;
  }
  /**
   * Clones a contact info repository item.
   * @see RepositoryUtils#cloneItem(RepositoryItem, boolean, Map, Map, MutableRepository, String)
   * @param pAddressItem the item to clone
   * @return the clone
   */
  public MutableRepositoryItem cloneContactInfoItem(RepositoryItem pContactInfoItem,
                              Map pExcludedProperties,Map pPropExceptions, boolean pDeepCopy,
                              String pRepositoryId)
  throws RepositoryException
  {
      MutableRepository repository = (MutableRepository)pContactInfoItem.getRepository();
      MutableRepositoryItem clonedItem = RepositoryUtils.cloneItem(pContactInfoItem,pDeepCopy,
                                                              pExcludedProperties,
                                                              pPropExceptions,
                                                              repository,
                                                              pRepositoryId);
      return clonedItem;
  }

  /**
   * Clones the address item and changes the item's property to reference
   * the clone.
   * @see #cloneContactInfoItem(RepositoryItem, Map, Map, boolean, String)
   * @param pItem the repository item that will reference the address
   * @param pAddress the address to be cloned
   * @param pPropertyName the name of the property on the item that will reference the clone
   * @param pExcludedProperties Properties to exclude from clone. Keys are
   *  item-type names and the values are collections of property names to
   *  exclude. Optional.
   * @param pPropExceptions Hierarchical map of property name exceptions to the
   *  above mode. Keys are property names while values are null or another Map
   *  if the property is an item. For example, if you clone a product using
   *  pDeepCopy=true, you could add the key "parentCategory" with a null value
   *  into pPropExceptions to cause a shallow copy of the product.parentCategory.
   *  Alternatively, you could add the key "parentCategory" but set the value to
   *  another map of exceptions that included the key/value pair "keywords"/null
   *  to do a deep copy of product.parentCategory but a shallow copy of the
   *  product.parentCategory.keywords. Optional.
   * @return the clone repository item
   * @throws RepositoryException
   * @deprecated
   */
  public MutableRepositoryItem setItemPropertyToAddress(RepositoryItem pItem, RepositoryItem pAddress, String pPropertyName, Map pExcludedProperties,Map pPropExceptions )
  throws RepositoryException
  {
    return setItemPropertyToAddress(pItem,pAddress,pPropertyName,pExcludedProperties,pPropExceptions,true);
  }


  /**
   * If desired (based on the performCloning flag), Clones the address item and changes the item's property to reference
   * the clone. Otherwise this method does not clone. Without cloning, this method uses, the same address item that has been passed to this
   * method.
   *
   * @see #cloneContactInfoItem(RepositoryItem, Map, Map, boolean, String)
   * @param pItem the repository item that will reference the address
   * @param pAddress the address to be cloned
   * @param pPropertyName the name of the property on the item that will reference the clone
   * @param pExcludedProperties Properties to exclude from clone. Keys are
   *  item-type names and the values are collections of property names to
   *  exclude. Optional.
   * @param pPropExceptions Hierarchical map of property name exceptions to the
   *  above mode. Keys are property names while values are null or another Map
   *  if the property is an item. For example, if you clone a product using
   *  pDeepCopy=true, you could add the key "parentCategory" with a null value
   *  into pPropExceptions to cause a shallow copy of the product.parentCategory.
   *  Alternatively, you could add the key "parentCategory" but set the value to
   *  another map of exceptions that included the key/value pair "keywords"/null
   *  to do a deep copy of product.parentCategory but a shallow copy of the
   *  product.parentCategory.keywords. Optional.
   * @return the clone repository item
   * @throws RepositoryException
   */
  public MutableRepositoryItem setItemPropertyToAddress(RepositoryItem pItem, RepositoryItem pAddress, String pPropertyName, Map pExcludedProperties,Map pPropExceptions, boolean pPerformCloning )
  throws RepositoryException {

    if(pItem ==  null)
      return null;
    if(pAddress == null)
      return null;
    if(StringUtils.isBlank(pPropertyName))
      return null;

    //set the item's property to the address. This is accomplished
    //by cloning the address item and setting the profile's property to
    //reference the clone.

    MutableRepositoryItem addressItemToSet = null;
    if (pPerformCloning) {
      addressItemToSet = cloneContactInfoItem(pAddress,pExcludedProperties,pPropExceptions,true,null);
    } else {
      addressItemToSet = (MutableRepositoryItem) pAddress;
    }
    if(addressItemToSet != null) {
      MutableRepository itemRepository = (MutableRepository)pItem.getRepository();
      RepositoryItemDescriptor itemDesc = pItem.getItemDescriptor();
      MutableRepositoryItem mutItem = itemRepository.getItemForUpdate(pItem.getRepositoryId(),itemDesc.getItemDescriptorName());
      mutItem.setPropertyValue(pPropertyName,addressItemToSet);
      itemRepository.updateItem(mutItem);
      return addressItemToSet;
    }
    return null;

  }

  /**
   * Determines if the nickname is already in the profile's address map.
   * @param pProfile
   * @param pNickname
   * @return
   */
  public boolean isDuplicateAddressNickName(RepositoryItem pProfile, String pNewNickname)  {
    Collection nicknames = getProfileAddressNames(pProfile);
    return isDuplicateNickname (nicknames,pNewNickname);
  }


  /**
   * Determines if the nickname is already in the profile's credit card map.
   * @param pProfile
   * @param pNickname
   * @return
   */
  public boolean isDuplicateCreditCardNickname(RepositoryItem pProfile, String pNewNickname)  {
    Map nicknames = getUsersCreditCardMap(pProfile);
    if (nicknames == null) {
      return false;
    }

    return isDuplicateNickname (nicknames.keySet(), pNewNickname);
  }

  /**
   * Determines if the nickname is already in the collection.
   * @param pNickname
   * @param pNicknames
   * @return
   */
  public boolean isDuplicateNickname(Collection pNicknames, String pNewNickname) {
    if (pNicknames == null
        || StringUtils.isEmpty(pNewNickname)) {
      return false;
    }
    if (pNicknames.contains(pNewNickname)){
      return true;
    } else {
      return false;
    }
  }

  /**
  *
  * This method returns a unique shipping address nickname. This method checks to see if there is any existence of
  * nickname parameter value (pNickName). If there is any matching nickname found, this
  * method appends numbers to qualify the unique string. Once the unique string is found, this method returns the
  * unique nickname string.
  *
  * If the user is trying to create the nickname with 'home', this method returns home##0. If you do not pass the nickname parameter (pNickName),
  * this method returns 'Address' or 'Address##0' or 'Address##1'. If there is already an nickname with 'Address', this method will return 'Address##0'. Also the 'Address' text is 
  * resourced. Based on the Locale, different text could be used.
  *
  * @param pAddress -- The address object is not used anymore.
  * @param pProfile -- From the profile, the existing nick names are obtained.
  * @param pNickname This is optional. In case if the user did not provide nickname and the system wants
  *        generate the nickname dynamically, then pass in null for the nickname.
  * @return
  */
  public String getUniqueShippingAddressNickname (Object pAddress, RepositoryItem pProfile, String pNewNickname) {

    if (isLoggingDebug()) {
      logDebug ("Entering getUniqueShippingAddressNickname()");
    }
    Collection nicknames = getProfileAddressNames(pProfile);

    return getUniqueAddressNickname (pAddress, nicknames, pNewNickname);

  }

  /**
   *
   * This method returns a unique address nickname. This method checks to see if there is any existence of
   * nickname parameter value (pNickName). If there is any matching nickname found, this
   * method appends numbers to qualify the unique string. Once the unique string is found, this method returns the
   * unique nickname string.
   *
   * If the user is trying to create the nickname with 'home', this method returns home##0. If you do not pass the nickname parameter (pNickName),
   * this method returns 'Address' or 'Address##0' or 'Address##1'. If there is already an nickname with 'Address', this method will return 'Address##0'. Also the 'Address' text is 
   * resourced. Based on the Locale, different text could be used.
   *
   * @param pAddress  -- The address is not used anymore
   * @param pNicknames -- List of nicknames to be checked against
   * @param pNickname This is optional. In case if the user did not provide nickname and the system wants
   *        generate the nickname dynamically, then pass in null for the nickname.
   * @return
   */
  public String getUniqueAddressNickname (Object pAddress, Collection pNicknames, String pNewNickname) {

    if (isLoggingDebug()) {
      logDebug ("Entering getUniqueAddressNickname()");
      logDebug ("Nickname parameter value is ::" + pNewNickname);
    }

    if (pAddress == null || isAddressEmpty(pAddress)) return null;

    String nickname = null;

    if (StringUtils.isEmpty(pNewNickname)) {
      nickname = ResourceUtils.getMsgResource("addressNicknamePrefix", RESOURCE_BUNDLE_NAME, sResourceBundle);
    } else {
      nickname = pNewNickname;
    }

    if (isLoggingDebug()) logDebug("The current nickname value is" + nickname);

    if (pNicknames == null) {
      return nickname;
    }

    return getUniqueNickname (pNicknames, nickname);

  }

  /**
   *
   * This method returns a unique credit card nickname. This method gets all the credit cards nicknames
   * from the profile and checks to see if there is any existence of nickname parameter value (pNickName)
   * or ( pCreditCard.getCreditCardType()+ " - " + last four digits of pCreditCard.getCcreditCardNumber()).
   * If there is any matching nickname found, this method appends numbers to identify the unique string.
   * Once the unique string is found, this method returns the unique nickname string.
   *
   * For example, let us assume that a user has two credit card with following nicknames.
   * Home  ==> Visa, 4111111111111111
   * Work  ==> Visa, 1111111111111111
   *
   * If the user is trying to create the nickname with Home, this method retuns Home##0. If you do not pass the nickname parameter (pNickName),
   * this method uses ( pCreditCard.getCreditCardType()+ " - " + last four digits of pCreditCard.getCcreditCardNumber().
   *
   * @param pCreditCard
   * @param pProfile
   * @param pNickname This is optional. In case if the user did not provide nickname and the system wants
   *        generate the nickname dynamically, then pass in null for the nickname.
   * @return
   */
  public String getUniqueCreditCardNickname (Object pCreditCard, RepositoryItem pProfile, String pNewNickname) {

    Collection nicknames = null;
    Map nicknamesMap = getUsersCreditCardMap(pProfile);
    if (nicknamesMap == null) {
      nicknames = new ArrayList();
    } else {
      nicknames = nicknamesMap.keySet();
    }

    return getUniqueCreditCardNickname (pCreditCard, nicknames, pNewNickname);

  }

  /**
   * This method returns a unique credit card nickname. This method checks to see if there is any existence of
   * nickname parameter value (pNickName) or ( pCreditCard.getCreditCardType()+ " - " + last four digits of pCreditCard.getCcreditCardNumber()).
   * If there is any matching nickname found, this method appends numbers to identify the unique string.
   * Once the unique string is found, this method returns the unique nickname string.
   *
   * For example, let us assume that a user has two credit card with following nicknames.
   * Home  ==> Visa, 4111111111111111
   * Work  ==> Visa, 1111111111111111
   *
   * If the user is trying to create the nickname with Home, this method retuns Home##0. If you do not pass the nickname parameter (pNickName),
   * this method uses ( pCreditCard.getCreditCardType()+ " - " + last four digits of pCreditCard.getCcreditCardNumber().
   *
   * @param pNicknames
   * @param pProfile
   * @param pNickname This is optional. In case if the user did not provide nickname and the system wants
   *        generate the nickname dynamically, then pass in null for the nickname.
   * @return
   */
  public String getUniqueCreditCardNickname (Object pCreditCard, Collection pNicknames, String pNewNickname) {

    if (isLoggingDebug()) {
      logDebug ("Entering getUniqueCreditCardNickname()");
      logDebug ("Nickname parameter value is ::" + pNewNickname);
    }

    String nickname = null;

    if (pCreditCard == null) {
      if (isLoggingDebug()) logDebug("CreditCard that is being passed is NULL");
      return null;
    }
    String creditCardType = null;
    String creditCardNumber = null;

    if (pCreditCard instanceof CreditCard) {
      creditCardType = ((CreditCard)pCreditCard).getCreditCardType();
      creditCardNumber = ((CreditCard)pCreditCard).getCreditCardNumber();
    } else if (pCreditCard instanceof RepositoryItem) {
      CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();
      creditCardType = (String) ((RepositoryItem)pCreditCard).getPropertyValue(cpmgr.getCreditCardTypePropertyName());
      creditCardNumber = (String) ((RepositoryItem)pCreditCard).getPropertyValue(cpmgr.getCreditCardNumberPropertyName());
    } else {
      return null;
    }

    if ( StringUtils.isEmpty(creditCardType)
        || StringUtils.isEmpty(creditCardNumber) ) {
      if (isLoggingDebug()) logDebug("Credit Card type or number is NULL");
      return null;
    }

    if (StringUtils.isEmpty(pNewNickname)) {
      StringBuffer sb = new StringBuffer(creditCardType);
      int cardNumberLength = creditCardNumber.length();
      String lastFour = creditCardNumber.substring(cardNumberLength - 4, cardNumberLength);
      sb.append(" - " + lastFour);
      if (isLoggingDebug()) {
        logDebug("New nickname: " + sb.toString());
      }
      nickname = sb.toString();
    } else {
      nickname = pNewNickname;
    }

    if (isLoggingDebug()) {
      logDebug("nickname: " + nickname);
    }

    if (pNicknames == null) {
      return nickname;
    }

    return getUniqueNickname (pNicknames, nickname);

  }
  /**
   *
   * This method returns a unique name from profile and a collection of names.
   * Profile repository is not always central location of all possible nicknames. In order to gaurantee that system provides
   * a unique nickname, this method take a collection and profile. this method will check from the profile and in the collection.
   * It will make sure that the nickname does not exist in the profile and in colletion that passed in.
   *
   * @param pAddress
   * @param pProfile
   * @return String unique nickname from the list.
   */
  public String getUniqueNickname(Collection pNickNames, String pCurrentNickname) {
    if (isLoggingDebug()) {
      logDebug ("Entering getUniqueNickname()");

      if (StringUtils.isEmpty(pCurrentNickname)) {
        logDebug ("Current Nickname value is ::" + pCurrentNickname);
      }
    }

    if ( pNickNames == null ) {
      return pCurrentNickname;
    }

    if (pNickNames != null && pNickNames.size() == ZERO) {
      return pCurrentNickname;
    }

    String uniqueNickname = pCurrentNickname;
    Collection nicknames = pNickNames;
    boolean uniqueNicknameFound = false;

    while (!uniqueNicknameFound) {
      if (!isDuplicateNickname(nicknames, uniqueNickname)) {
        if (isLoggingDebug()) logDebug ("Unique name is found in the collection");
        uniqueNicknameFound = true;
      } else {
        uniqueNickname = generateUniqueNickname(uniqueNickname);
      }
    }//end while
    return uniqueNickname;
  }


  /**
   *
   * This method generates the unique nickname. This method checks to see if there is two tokens in the string based
   * on the separator. If there is no token, then this method adds the separator and zero (0) to the string and returns.
   * Also the supplied string has two tokens, then the obtain the second token and increment the number by one and return the string.
   *
   * @param pNickName
   * @return
   */
  public String generateUniqueNickname (String pNickname) {
    //if the separator is already in place, then separate the token and increment the number.
    //otherwise if hte token is not there, then add the separator and increment the number.

    if (isLoggingDebug()) {
      logDebug ("Entering generateUniqueName()");
      logDebug ("Nnickname value is ::" + pNickname);
    }

    if (StringUtils.isEmpty(pNickname)) {
      return null;
    }
    int separatorIndex = pNickname.lastIndexOf(getUniqueNicknameSeparator());
    StringBuffer sbf = new StringBuffer();

    if (separatorIndex == -1) {
      sbf.append(pNickname);
      sbf.append(getUniqueNicknameSeparator());
      sbf.append(ZERO);
      return sbf.toString();
    } else {
      sbf.append(pNickname.substring(0, separatorIndex));
      sbf.append(getUniqueNicknameSeparator());
      try {
        Integer number = new Integer(pNickname.substring(separatorIndex+getUniqueNicknameSeparator().length()));
        sbf.append(number.intValue()+1);
      } catch (NumberFormatException nfe) {
        if (isLoggingError()) logError("There was an error while generating unique address name." + nfe);
      }
      return sbf.toString();
    }
  }

  /**
   * This method constructs a consolidated collection of addresses that are associated with
   * the current customer profile. The collection includes that default shipping address,
   * default billing address, all the addresses included in the configured maps, and all the billing addresses
   * from existing credit cards.
   * @return a Collection of all address items
   */
  public List getAllAvailableAddresses(RepositoryItem pProfile)  {

    if (isLoggingDebug()) {
      logDebug ("Entering getAllAvailableAddresses()");
    }

    ArrayList addresses = new ArrayList();

    if (isLoggingDebug()) {
      logDebug ("Collecting all shipping addresses.");
    }

    Iterator addressIterator = getAllShippingAddresses(pProfile).iterator();
    while(addressIterator.hasNext()) {
      RepositoryItem address = (RepositoryItem) addressIterator.next();
      addUniqueAddressToAddressList(addresses,address);
    }

    if (isLoggingDebug()) {
      logDebug ("Collecting all billing addresses.");
    }

    addressIterator = getAllBillingAddresses(pProfile).iterator();
    while(addressIterator.hasNext()) {
      RepositoryItem address = (RepositoryItem) addressIterator.next();
      addUniqueAddressToAddressList(addresses,address);
    }

    return addresses;
  }


  /**
   * This method constructs a consolidated collection of all shipping addresses that are associated with
   * the current customer profile. The collection includes that default shipping address,
   * all the addresses included in the configured maps.
   * @param pProfile
   * @return
   */
  public List getAllShippingAddresses (RepositoryItem pProfile){

    if (isLoggingDebug()) {
      logDebug ("Entering getAllShippingAddresses()");
    }

    ArrayList shippingAddresses = new ArrayList();

    if (isLoggingDebug()) {
      logDebug ("Collecting default shipping address.");
    }

    RepositoryItem defaultShippingAddress = getDefaultShippingAddress(pProfile);
    if (!isAddressEmpty (defaultShippingAddress)) {
      addUniqueAddressToAddressList(shippingAddresses,defaultShippingAddress);
    }

    if (isLoggingDebug()) {
      logDebug ("Collecting all addresses in the addressMapProperties.");
    }

    addAddressesFromMapProperties (pProfile, getShippingAddressMapProperties(),shippingAddresses);

    return shippingAddresses;
  }

  /**
   *  This method adds addresses from map properties to the all available address list.
   *
   * @param pProfile
   * @param pPropertyNames
   * @param pDestination
   */
  public void addAddressesFromMapProperties (RepositoryItem pProfile, String[] pPropertyNames, List pDestination) {

    if (isLoggingDebug()) {
      logDebug ("Entering addAddressesFromMapProperties()");
    }

    if (pProfile == null || pPropertyNames == null || pDestination == null) {
      return;
    }

    for(int i =0;i< pPropertyNames.length;i++) {
      Map mapProperty = (Map) pProfile.getPropertyValue(pPropertyNames[i]);
      Collection mapValues = mapProperty.values();
      if(mapValues != null && mapValues.size()>0) {
        Iterator valuerator = mapValues.iterator();
        while(valuerator.hasNext()) {
          RepositoryItem address = (RepositoryItem) valuerator.next();
          if (!isAddressEmpty (address)) {
            addUniqueAddressToAddressList(pDestination,address);
          }
        }
      }
    }
  }

  /**
   *
   * This method constructs a consolidated collection of all billing addresses that are associated with
   * the current customer profile. The collection includes that default billing address and credit card addresses.
   * @param pProfile
   * @return
   */
  public List getAllBillingAddresses (RepositoryItem pProfile){

    if (isLoggingDebug()) {
      logDebug ("Entering getAllBillingAddresses().");
    }

    ArrayList billingAddresses = new ArrayList();
    //add default shipping and billing
    CommercePropertyManager cpmgr = (CommercePropertyManager)getPropertyManager();

    if (isLoggingDebug()) {
      logDebug ("Collecting default billing address.");
    }

    RepositoryItem defaultBillingAddress = getDefaultBillingAddress(pProfile);
    if (!isAddressEmpty (defaultBillingAddress)) {
      addUniqueAddressToAddressList(billingAddresses,defaultBillingAddress);
    }

    if (isLoggingDebug()) {
      logDebug ("Collecting default credit card address.");
    }

    //add the default credit card address
    RepositoryItem defaultCreditCard = getDefaultCreditCard(pProfile);
    if(defaultCreditCard != null)  {
      RepositoryItem creditCardAddress = (RepositoryItem) defaultCreditCard.getPropertyValue(cpmgr.getCreditCardBillingAddressPropertyName());
      if (!isAddressEmpty (creditCardAddress)) {
        addUniqueAddressToAddressList(billingAddresses,creditCardAddress);
      }
    }

    if (isLoggingDebug()) {
      logDebug ("Collecting all credit card addresses.");
    }

    //add all the address from the credit card map
    Map creditCardMap = (Map)pProfile.getPropertyValue(cpmgr.getCreditCardPropertyName());
    Collection creditCardMapValues = creditCardMap.values();
    if(creditCardMapValues != null && creditCardMapValues.size() > 0)  {
      Iterator creditCarderator = creditCardMapValues.iterator();
      while(creditCarderator.hasNext())  {
        RepositoryItem creditCard = (RepositoryItem) creditCarderator.next();
        RepositoryItem creditCardAddress = (RepositoryItem) creditCard.getPropertyValue(cpmgr.getCreditCardBillingAddressPropertyName());
        if (!isAddressEmpty (creditCardAddress)) {
          addUniqueAddressToAddressList(billingAddresses,creditCardAddress);
        }
      }
    }
    //This is to add any configurable map properties

    addAddressesFromMapProperties (pProfile, getBillingAddressMapProperties(),billingAddresses);

    return billingAddresses;
  }


  /**
   * This method iterates through list of addresses and if any same address found in the collection, this method
   * ends the process and returns. If there is no duplicate address found, then this method adds the address to the
   * list.
   * @param pAddresses
   * @param pNewAddress
   */
  public void addUniqueAddressToAddressList(Collection pAddresses, RepositoryItem pNewAddress) {

    if(pNewAddress == null)
      return;

    Iterator addresserator = pAddresses.iterator();
    while(addresserator.hasNext()) {
      RepositoryItem address = (RepositoryItem) addresserator.next();
      if(areAddressesEqual(address,pNewAddress,null)) {
        return;
      }
    }
    pAddresses.add(pNewAddress);
  }// end of addUniqueAddressToAddressList
  
  /**
   * Make the credit card identified by pCreditCardName default in the
   * given profile.
   *
   * @param pProfile The Profile repository item
   * @param pCreditCardNickname The Nickname of the credit card to be default
   * 
   * @throws RepositoryException */
  public boolean setDefaultCreditCard(RepositoryItem pProfile, String pCreditCardNickname)
      throws RepositoryException {
    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    RepositoryItem card = getCreditCardByNickname(pCreditCardNickname, pProfile);
    if (card != null) {
      updateProperty(cpmgr.getDefaultCreditCardPropertyName(), card, pProfile);
      return true;
    }
    return false;
  }

  /**
   * Sets specified by nickname shipping address as default shipping address 
   * if profile's default shipping address is not specified
   * 
   * @param pProfile profile repository item
   * @param pAddressName shipping address nickname to be set as default
   * 
   * @return true if specified shipping address was set as default
   * @throws RepositoryException 
   */
  public boolean setDefaultShippingAddressIfNull(RepositoryItem pProfile, String pAddressName)
      throws RepositoryException {
    RepositoryItem defaultShippingAddr = getDefaultShippingAddress(pProfile);
    if (defaultShippingAddr == null) {
      setDefaultShippingAddress(pProfile, pAddressName);
      return true;
    }
    return false;
  }

  /**
   * Sets specified by nickname secondary address as default shipping address.
   * Firstly checks if there is already default shipping address in profile, 
   * if so makes copy of current default shipping address and adds it to
   * the map of user's secondary addresses.
   * 
   * @param pProfile profile repository item
   * @param pAddressName nickname of the address to be set as default
   * @throws RepositoryException
   */
  public boolean setDefaultShippingAddress(RepositoryItem pProfile, String pAddressName)
      throws RepositoryException {

    CommercePropertyManager propertyManager = (CommercePropertyManager) getPropertyManager();

    // Get the current default shipping address 
    RepositoryItem oldDefaultShippingAddress = getDefaultShippingAddress(pProfile);
    // Get new default shipping address
    RepositoryItem newDefaultShippingAddress = getProfileAddress(pProfile, pAddressName);

    if (newDefaultShippingAddress == null) {
      //if there is no such address in profile do nothing
      return false;
    }

    if (oldDefaultShippingAddress == null) {
      // No current default, just set default to point to
      // the address with the desired nickname.
      if (isLoggingDebug()) {
        logDebug("Setting default shipping address to Address-book address: "
            + newDefaultShippingAddress.getRepositoryId());
      }
      updateProperty(propertyManager.getShippingAddressPropertyName(), newDefaultShippingAddress,
                     pProfile);

    } else {
      //copy current default shipping address
      RepositoryItem copyOfOldDefaultAddress = cloneContactInfoItem(oldDefaultShippingAddress,
                                                                    null, null, true, null);
      //remove old default shipping address from secondary shipping adresses
      // and from default shipping address
      String oldDefaultNickname = null;
      oldDefaultNickname = getProfileAddressName(pProfile, oldDefaultShippingAddress);
      removeProfileRepositoryAddress(pProfile, oldDefaultNickname);

      // Set the requested default address as default
      if (isLoggingDebug()) {
        logDebug("Setting shipping address to new shipping Address: "
            + newDefaultShippingAddress.getRepositoryId());
      }

      updateProperty(propertyManager.getShippingAddressPropertyName(), newDefaultShippingAddress,
                     pProfile);

      // Add the previous default address back into the secondary addresses
      // It gets wiped out when the new Default is set in its place.
      if (oldDefaultNickname != null) {
        if (isLoggingDebug()) {
          logDebug("Putting old default back in secondary address map");
        }

        addProfileRepositoryAddress(pProfile, oldDefaultNickname, copyOfOldDefaultAddress);
      }
    }
    return true;
  }

  /**
   * Sets specified by nickname credit card as default credit card 
   * if profile's default credit card is not specified yet
   * 
   * @param pProfile The Profile repository item
   * @param pCreditCardName credit card nickname 
   * 
   * @return true if specified credit card wsa set as dafult
   * @throws RepositoryException 
   */
  public boolean setDefaultCreditCardIfNull(RepositoryItem pProfile, String pCreditCardName)
      throws RepositoryException {
    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    RepositoryItem defaultCreditCard = getDefaultCreditCard(pProfile);
    if (defaultCreditCard == null) {
      return setDefaultCreditCard(pProfile, pCreditCardName);
    }
    return false;
  }

  /**
   * Gets nickname for the given profile's address. Firstly check
   * if address is stored in the map of user's secondary addresses,
   * if so returns corresponding address nickname. Otherwise check if 
   * address is user's default shipping address or default billing
   * address and if so returns corresponding names.
   * 
   * @param pProfile The profile repository item
   * @param pAddress Address repository item 
   * @return nickname for secondary address repository item
   */
  public String getProfileAddressName(RepositoryItem pProfile, RepositoryItem pAddress) {
    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    Map secondaryAddresses = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
    String nickname = null;
    String addressId = pAddress.getRepositoryId();
    Set entries = secondaryAddresses.entrySet();
    Iterator it = entries.iterator();
    Map.Entry entry = null;

    while (it.hasNext()) {
      entry = (Map.Entry) it.next();
      String entryId = ((RepositoryItem) entry.getValue()).getRepositoryId();
      if (entryId.equals(addressId)) {
        nickname = (String) entry.getKey();

        if (isLoggingDebug()) {
          logDebug("Nickname for secondary address " + pAddress + " found: " + nickname);
        }
        break;
      }
    }
    if (nickname != null)
      return nickname;
    //check if address is stored as default shipping address
    RepositoryItem defaultShippingAddr = getDefaultShippingAddress(pProfile);
    if (defaultShippingAddr != null) {
      if (addressId.equals(defaultShippingAddr.getRepositoryId())) {
        nickname = cpmgr.getDefaultShippingAddrName(getLocaleService().getLocale());
      }
    } else {
      //check if address is stores as default billing address
      RepositoryItem defaultBillingAddr = getDefaultBillingAddress(pProfile);
      if (defaultBillingAddr != null) {
        if (addressId.equals(defaultBillingAddr.getRepositoryId())) {
          nickname = cpmgr.getDefaultBillingAddrName(getLocaleService().getLocale());
        }
      }
    }

    return nickname;
  }

  /**
   * Gets nickname for the given profile's secondary address
   * 
   * @param pProfile user's profile object
   * @param pAddress secondary address repository item 
   * @return nickname for secondary address repository item
   */
  public String getCreditCardNickname(RepositoryItem pProfile, RepositoryItem pCreditCard) {
    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    Map creditCards = (Map) pProfile.getPropertyValue(cpmgr.getCreditCardPropertyName());
    String nickname = null;
    Set entries = creditCards.entrySet();
    Iterator it = entries.iterator();
    Map.Entry entry = null;

    while (it.hasNext()) {
      entry = (Map.Entry) it.next();
      String entryId = ((RepositoryItem) entry.getValue()).getRepositoryId();
      if (entryId.equals(pCreditCard.getRepositoryId())) {
        nickname = (String) entry.getKey();

        if (isLoggingDebug()) {
          logDebug("Nickname for credit card " + pCreditCard + " found: " + nickname);
        }
        break;
      }
    }
    return nickname;
  }

  /**
   * Changes secondary address nickname
   * 
   * @param pProfile profile repository item
   * @param pOldAddressName old secondary address nickname
   * @param pNewAddressName new secondary address nickname
   * @throws RepositoryException 
   */
  public void changeSecondaryAddressName(RepositoryItem pProfile, String pOldAddressName,
                                         String pNewAddressName) throws RepositoryException {
    if (StringUtils.isBlank(pNewAddressName) || pNewAddressName.equalsIgnoreCase(pOldAddressName)) {
      return;
    }

    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    Map secondaryAddresses = (Map) pProfile.getPropertyValue(cpmgr.getSecondaryAddressPropertyName());
    RepositoryItem address = getProfileAddress(pProfile, pOldAddressName);
    if (address != null) {
      secondaryAddresses.remove(pOldAddressName);
      secondaryAddresses.put(pNewAddressName, address);
      updateProperty(cpmgr.getSecondaryAddressPropertyName(), secondaryAddresses, pProfile);
    }
  }

  /**
   * Changes credit card nickname in the map of user's credit cards
   * 
   * @param pProfile profile repository item
   * @param pOldCreditCardNickname credit card's old nickname
   * @param pNewCreditCardNickname credit card's new nickname
   * @throws RepositoryException 
   */
  public void changeCreditCardNickname(RepositoryItem pProfile, String pOldCreditCardNickname,
                                       String pNewCreditCardNickname) throws RepositoryException {
    if (StringUtils.isBlank(pNewCreditCardNickname)
        || pNewCreditCardNickname.equalsIgnoreCase(pOldCreditCardNickname)) {
      return;
    }

    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    Map creditCards = (Map) pProfile.getPropertyValue(cpmgr.getCreditCardPropertyName());
    RepositoryItem card = getCreditCardByNickname(pOldCreditCardNickname, pProfile);
    if (card != null) {
      creditCards.remove(pOldCreditCardNickname);
      creditCards.put(pNewCreditCardNickname, card);
      updateProperty(cpmgr.getCreditCardPropertyName(), creditCards, pProfile);
    }
  }

  /**
   * This method constructs a new credit card.
   * Created credit card repository item is added to user's map of credit cards.
   * If user's default billing address is null than new credit card's billing
   * address is set as default billing address.
   * If user's default credit card is null that new credit card is set as default
   * credit card for this user.
   *
   * @param pProfile user's profile object
   * @param pNewCreditCard map of credit cards properties' values
   * @param pCreditCardNickname nickname for credit card
   * @param pBillingAddress Map or RepositoryItem that should be taken as billing address
   * @return created credit card's nickname
   *            
   * @throws RepositoryException
   *             if there was an error accessing the repository
   * @throws IntrospectionException 
   * @throws PropertyNotFoundException 
   */
  public String createProfileCreditCard(RepositoryItem pProfile, Map pNewCreditCard,
                                        String pCreditCardNickname, Object pBillingAddress)
      throws RepositoryException, IntrospectionException, PropertyNotFoundException {

    MutableRepository repository = (MutableRepository) pProfile.getRepository();
    CommercePropertyManager propertyManager = (CommercePropertyManager) getPropertyManager();

    // Generate unique credit card nickname if it's not specified
    String creditCardNickname = pCreditCardNickname;
    if (StringUtils.isBlank(creditCardNickname)) {
      creditCardNickname = getUniqueCreditCardNickname(pNewCreditCard, pProfile, null);
    }
    // Create credit card repository item 
    RepositoryItem newCreditCard = createCreditCardItem(pProfile);
    copyShallowCreditCardProperties(pNewCreditCard, newCreditCard);

    if (pBillingAddress != null) {
      // Copy specified secondary address properties to card's billing address
      RepositoryItem billingAddress = (RepositoryItem) newCreditCard.getPropertyValue(propertyManager.getCreditCardItemDescriptorBillingAddressPropertyName());

      Set<String> excludedProperties = new HashSet<String>();
      excludedProperties.add(ID_PROPERTY_NAME);

      AddressTools.copyObject(pBillingAddress, billingAddress, excludedProperties);
      repository.updateItem((MutableRepositoryItem) billingAddress);

      // if default billingAddress is null, make this the default
      // billingAddress
      if (getDefaultBillingAddress(pProfile) == null) {
        updateProperty(propertyManager.getBillingAddressPropertyName(), billingAddress, pProfile);
      }

      repository.updateItem((MutableRepositoryItem) newCreditCard);
    }

    //add created credit card to user's map of credit cards
    addCreditCardToUsersMap(pProfile, newCreditCard, creditCardNickname);

    return creditCardNickname;
  }

  /**
   * Updates profile's credit card. Changes nickname if new one is provided.
   * 
   * @param pCartToUpdate credit card repository item to update
   * @param pProfile user's profile object
   * @param pUpdatedCreditCard map of credit card properties's values
   * @param pNewCreditCardNickname new credit card nickname
   * @param pBillingAddress map of credit card' billing address properties values
   * 
   * @throws IntrospectionException 
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   * @throws RepositoryException 
   */
  public void updateProfileCreditCard(RepositoryItem pCartToUpdate, RepositoryItem pProfile,
                                      Map pUpdatedCreditCard, String pNewCreditCardNickname,
                                      Map pBillingAddress, String pBillingAddressClassName)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException,
      IntrospectionException, RepositoryException {

    MutableRepository repository = (MutableRepository) pProfile.getRepository();
    MutableRepositoryItem oldCard = (MutableRepositoryItem) pCartToUpdate;
    String nickname = getCreditCardNickname(pProfile, pCartToUpdate);
    CommercePropertyManager cpmgr = (CommercePropertyManager) getPropertyManager();
    if (pUpdatedCreditCard != null) {

      String[] cardProperties = cpmgr.getShallowCreditCardPropertyNames();
      Object property;
      for (int i = 0; i < cardProperties.length; i++) {
        property = pUpdatedCreditCard.get(cardProperties[i]);
        if (property != null) {
          oldCard.setPropertyValue(cardProperties[i], property);
        }

      }
      repository.updateItem(oldCard);
    }
    MutableRepositoryItem billingAddress = (MutableRepositoryItem) oldCard.getPropertyValue(cpmgr.getBillingAddressPropertyName());
    if (pBillingAddress != null) {
      Address addressObject = AddressTools.createAddressFromMap(pBillingAddress,
                                                                pBillingAddressClassName);
      updateProfileRepositoryAddress(billingAddress, addressObject);
      repository.updateItem(billingAddress);
    }

    if (!StringUtils.isBlank(pNewCreditCardNickname)
        && !pNewCreditCardNickname.equalsIgnoreCase(nickname)) {
      changeCreditCardNickname(pProfile, nickname, pNewCreditCardNickname);
    }
  }

  /**
   * Removes credit card from profile's credit cards map. Checks if credit cards 
   * billing address is profile's default billing address, if so sets default
   * billing address to null. Checks if credit card is profile's default credit card
   * if so sets default credit card to null.
   * 
   * @param pProfile user's profile object
   * @param pCreditCardName credit card nickname
   * 
   * @throws RepositoryException
   */
  public void removeProfileCreditCard(RepositoryItem pProfile, String pCreditCardName)
      throws RepositoryException {

    RepositoryItem card = getCreditCardByNickname(pCreditCardName, pProfile);
    String repositoryId = null;
    if (card != null) {
      CommercePropertyManager propertyManager = (CommercePropertyManager) getPropertyManager();
      //check if billing address is default billing address
      RepositoryItem billingAddress = (RepositoryItem) card.getPropertyValue(propertyManager.getBillingAddressPropertyName());
      RepositoryItem defaultBillingAddress = (RepositoryItem) pProfile.getPropertyValue(propertyManager.getBillingAddressPropertyName());
      if (defaultBillingAddress != null
          && defaultBillingAddress.getRepositoryId().equals(billingAddress.getRepositoryId())) {
        updateProperty(propertyManager.getBillingAddressPropertyName(), null, pProfile);
      }
      
      //Check if the card is also default credit card, if so 
      //set default credit card to null
      repositoryId = card.getRepositoryId();

      RepositoryItem defaultCreditCard = getDefaultCreditCard(pProfile);

      if ((defaultCreditCard != null)
          && (repositoryId.equalsIgnoreCase(defaultCreditCard.getRepositoryId()))) {
        updateProperty(propertyManager.getDefaultCreditCardPropertyName(), null, pProfile);
      }

      //remove card from credit cards map
      Map creditCards = (Map) pProfile.getPropertyValue(propertyManager.getCreditCardPropertyName());
      creditCards.remove(pCreditCardName);

      updateProperty(propertyManager.getCreditCardPropertyName(), creditCards, pProfile);
      
    }
  }

} // end of class
