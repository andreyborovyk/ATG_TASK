/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2010 Art Technology Group, Inc.
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

import atg.commerce.CommerceException;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.pricing.PricingException;
import atg.commerce.pricing.PricingTools;
import atg.commerce.promotion.DuplicatePromotionException;
import atg.commerce.util.PlaceUtils;

import atg.commerce.claimable.ClaimableException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.order.*;
import atg.commerce.order.purchase.*;

import atg.core.util.ContactInfo;
import atg.core.util.StringUtils;
import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;

import atg.projects.store.StoreConfiguration;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderTools;

import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;

import java.util.*;

import javax.transaction.SystemException;



/**
 * Store implementation of the purchase process helper.
 *
 * @author ATG
 * @version $Revision: #3 $
  */
public class StorePurchaseProcessHelper extends PurchaseProcessHelper {

  /** Class version string. */
  public static final String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StorePurchaseProcessHelper.java#3 $$Change: 635816 $";

  /**
   * Missing required address property.
   */
  public static final String MSG_MISSING_REQUIRED_ADDRESS_PROPERTY = "missingRequiredAddressProperty";

  /**
   * Pricing error invalid address message key.
   */
  public static String PRICING_ERROR_ADDRESS = "pricingErrorInvalidAddress";

  /**
   * Pricing error message key.
   */
  public static String PRICING_ERROR = "pricingError";

  /**
   * Multiple coupons per order error message key.
   */
  public static final String MSG_MULTIPLE_COUPONS_PER_ORDER = "multipleCouponsPerOrder";

  /**
   * Uncliamable coupon error message key.
   */
  public static final String MSG_UNCLAIMABLE_COUPON = "couponNotClaimable";

  /**
   * Address property name map.
   */
  Map mAddressPropertyNameMap;

  /**
   * @return the address property name map.
   */
  public Map getAddressPropertyNameMap() {
    return mAddressPropertyNameMap;
  }

  /**
   * @param pAddressPropertyNameMap - the address property name map to set.
   */
  public void setAddressPropertyNameMap(Map pAddressPropertyNameMap) {
    mAddressPropertyNameMap = pAddressPropertyNameMap;
  }

  /**
   * Required address property names.
   */
  private String[] mRequiredAddressPropertyNames = new String[0];

  /**
   * @return the address property names.
   */
  public String[] getRequiredAddressPropertyNames() {
    return mRequiredAddressPropertyNames;
  }

  /**
   * @param pRequiredAddressPropertyNames - the address property name map to set.
   */
  public void setRequiredAddressPropertyNames(String[] pRequiredAddressPropertyNames) {
    if (pRequiredAddressPropertyNames == null) {
      mRequiredAddressPropertyNames = new String[0];
    }
    else {
      mRequiredAddressPropertyNames = pRequiredAddressPropertyNames;
    }
  }

  /**
   * Store order tools.
   */
  private StoreOrderTools mStoreOrderTools;

  /**
   * @return the Store order tools property.
   */
  public StoreOrderTools getStoreOrderTools() {
    return mStoreOrderTools;
  }

  /**
   * @param pStoreOrderTools - the Store order tools property.
   */
  public void setStoreOrderTools(StoreOrderTools pStoreOrderTools) {
    mStoreOrderTools = pStoreOrderTools;
  }

  /**
   * Store configuration.
   */
  private StoreConfiguration mStoreConfiguration;

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
   * property: pricingTools
   */
  private PricingTools mPricingTools;

  /**
   * @param pPricingTools - pricing tools.
   */
  public void setPricingTools(PricingTools pPricingTools) {
    mPricingTools = pPricingTools;
  }

  /**
   * @return mPricingTools - pricing tools.
   */
  public PricingTools getPricingTools() {
    return mPricingTools;
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
  
  private ClaimableManager mClaimableManager;

  public ClaimableManager getClaimableManager()
  {
    return mClaimableManager;
  }

  public void setClaimableManager(ClaimableManager pClaimableManager)
  {
    mClaimableManager = pClaimableManager;
  }

  /**
   * Associate a commerce item with the shipping group specified by
   * the <code>pItemInfo.shippingGroup</code> property or the default
   * shipping group supplied by our caller.
   *
   * @param pItem the CommerceItem
   * @param pItemInfo the object that supplies input for the commerce item
   * @param pOrder the item's order
   * @param pShippingGroup the default shipping group for the order
   * @exception CommerceException if there was an error while executing the code
   */
  protected void addItemToShippingGroup(CommerceItem pItem, AddCommerceItemInfo pItemInfo, Order pOrder,
                                        ShippingGroup pShippingGroup) throws CommerceException {
    ShippingGroup sg = getShippingGroupForItem(pItemInfo, pShippingGroup);
    super.addItemToShippingGroup(pItem, pItemInfo, pOrder, sg);
  }

  /**
   * Perform giftlist related processing for an item being ordered
   * from a giftlist. This method extends the base by looking for
   * an item-specific shipping group.
   *
   * @param pItem the CommerceItem
   * @param pItemInfo the object that supplies input for the commerce item
   * @param pOrder the item's order
   * @param pProfile the owner of the order
   * @param pShippingGroup the default shipping group for the order
   * @exception CommerceException if there was an error while executing the code
   */
  protected void processGiftAddition(CommerceItem pItem, AddCommerceItemInfo pItemInfo, Order pOrder,
                                     RepositoryItem pProfile, ShippingGroup pShippingGroup)
    throws CommerceException {
    ShippingGroup sg = getShippingGroupForItem(pItemInfo, pShippingGroup);
    super.processGiftAddition(pItem, pItemInfo, pOrder, pProfile, sg);
  }

  /**
   * Figures out what shipping group to use for a new commerce item.
   * If <code>pItemInfo</code> mentions a specific shipping group, returns
   * that. Otherwise, returns the default shipping group.
   *
   * @param pItemInfo the object that supplies input for the commerce item
   * @param pShippingGroup the default shipping group for the order
   * @return the shipping group to use
   */
  protected ShippingGroup getShippingGroupForItem(AddCommerceItemInfo pItemInfo, ShippingGroup pShippingGroup) {
    if (pItemInfo instanceof StoreAddCommerceItemInfo) {
      StoreAddCommerceItemInfo itemInfo = (StoreAddCommerceItemInfo) pItemInfo;
      ShippingGroup sg = itemInfo.getShippingGroup();

      if (sg != null) {
        pShippingGroup = sg;
      }
    }
    return pShippingGroup;
  }

  /**
   * Get mandatory state country list.
   *
   * @param pRequest - http request
   *
   * @return mandatory state country list
   */
  protected List getMandatoryStateCountryList(DynamoHttpServletRequest pRequest) {
    return getStoreConfiguration().getMandatoryStateCountryList();
  }

  /**
   * Validates the required billing properties.
   *
   * @param pContactInfo - contact information
   * @return a list of required properties that are missing from the ContactInfo
   */
  public List checkForRequiredAddressProperties(ContactInfo pContactInfo, DynamoHttpServletRequest pRequest) {
    // You won't always have a repository item here, so the usual
    // getPropertyValue(<property_name>) won't work unfortunately.
    // Lots of hardcoded properties instead :(
    List missingRequiredAddressProperties = new ArrayList();
    List requiredAddressPropertyNames = Arrays.asList(getRequiredAddressPropertyNames());

    try {
      if (requiredAddressPropertyNames.contains("firstName")
            && StringUtils.isEmpty(pContactInfo.getFirstName())) {
        missingRequiredAddressProperties.add("firstName");
      }

      if (requiredAddressPropertyNames.contains("lastName")
            && StringUtils.isEmpty(pContactInfo.getLastName())) {
        missingRequiredAddressProperties.add("lastName");
      }

      if (requiredAddressPropertyNames.contains("address1")
            && StringUtils.isEmpty(pContactInfo.getAddress1())) {
        missingRequiredAddressProperties.add("address1");
      }

      if (requiredAddressPropertyNames.contains("city")
            && StringUtils.isEmpty(pContactInfo.getCity())) {
        missingRequiredAddressProperties.add("city");
      }

      /*List mandatoryStateCountryList = getMandatoryStateCountryList(pRequest);

      if ((pContactInfo.getCountry() != null) && (mandatoryStateCountryList != null) &&
          mandatoryStateCountryList.contains(pContactInfo.getCountry())) {
        if (StringUtils.isEmpty(pContactInfo.getCountry())) {
          missingRequiredAddressProperties.add("country");
        }
      }*/

      if (requiredAddressPropertyNames.contains("country")
            && StringUtils.isEmpty(pContactInfo.getCountry())) {
        missingRequiredAddressProperties.add("country");
      }

      if (requiredAddressPropertyNames.contains("state")
            && StringUtils.isEmpty(pContactInfo.getState())) {
        if( pContactInfo.getCountry()== null || getPlaceUtils().getPlaces(pContactInfo.getCountry())!=null){
          missingRequiredAddressProperties.add("state");  
        }        
      }

      if (requiredAddressPropertyNames.contains("city")
            && StringUtils.isEmpty(pContactInfo.getCity())) {
        missingRequiredAddressProperties.add("city");
      }


      if (requiredAddressPropertyNames.contains("postalCode")
            && StringUtils.isEmpty(pContactInfo.getPostalCode())) {
        missingRequiredAddressProperties.add("postalCode");
      }

      if (requiredAddressPropertyNames.contains("phoneNumber")
            && StringUtils.isEmpty(pContactInfo.getPhoneNumber())) {
        missingRequiredAddressProperties.add("phoneNumber");
      }
    } catch (Exception e) {
      if (isLoggingError()) {
        logError(LogUtils.formatMinor("Error getting message: " + e.toString()));
      }
    }

    return missingRequiredAddressProperties;
  }

  /**
   * Logic to reprice order, and parse any errors.
   *
   * @param pOrder the order to price
   * @param pUserLocale the locale of the user, may be null
   * @param pProfile the user, may be null
   * @param pUserPricingModels the PricingModelHolder is an object which contains all the
   * pricing models associated with a user (i.e. item, shipping, order and tax).
   * @exception PricingException if there was an error while computing the pricing information
   */
  public void repriceOrder(Order pOrder,
                           PricingModelHolder pUserPricingModels,
                           Locale pUserLocale,
                           RepositoryItem pProfile)
      throws PricingException {

    try {
      HashMap map = new HashMap();

      if (isLoggingDebug()) {
        logDebug("Repricing w/ pricing tools (priceOrderTotal.");
      }

      // Need to do priceOrderTotal here to catch errors in the shipping address.
      // CyberSource is the only means of doing city/state/zip validation, so use them here and
      // do tax calc to make sure city/state/zip is valid.
      getPricingTools()
        .priceOrderTotal(pOrder, pUserPricingModels, pUserLocale, pProfile, map);
    }
    catch (PricingException pe) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error w/ PricingTools.priceOrderTotal: " + pe));
      }
      throw pe;
    }
    catch (Exception e) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor(""), e);
      }
    }
  }

  /**
   * Attempt to claim the specified coupon code for a specific user and order.
   * If the tendered coupon code is empty then remove any existing coupon on the order, if the
   * tendered coupon is not empty then check this is a valid claimable item before removing any
   * existing coupon and claiming the tendered coupon. This avoids issues such as revoke promotion
   * events if we simply remove the coupon and then rollback when claiming the coupon fails.
   * 
   * @param pCouponCode - coupon code to be claimed
   * @param pOrder - order to be repriced when the coupon has been claimed
   * @param pProfile - user who claims a coupon
   * @param pUserPricingModels - user's pricing models to be used for order reprice process
   * @param pUserLocale - user's locale to be used when repricing order
   *
   * @return true if the coupon has been successfully removed or tendered/claimed; false otherwise
   *
   * @throws CommerceException if an error occurred during claiming the coupon
   */
  public boolean tenderCoupon(String pCouponCode,
                              StoreOrderImpl pOrder,
                              RepositoryItem pProfile,
                              PricingModelHolder pUserPricingModels,
                              Locale pUserLocale)
      throws CommerceException, IllegalArgumentException {

    TransactionDemarcation td = new TransactionDemarcation();
    boolean rollback = true;

    String tenderedCouponCode = pCouponCode;

    String currentCouponCode = pOrder.getCouponCode();

    boolean removeCouponOnly = false;

    boolean canClaimTenderedCoupon = false;


    try {
      /*
       * Only try to claim a coupon if it is not already claimed on the order.
       */
      if (!StringUtils.isEmpty(tenderedCouponCode)
          && tenderedCouponCode.equals(currentCouponCode)) {
        return( true );
      }

      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      /*
       * An empty coupon code indicates we want to remove any current coupon.
       */
      if (StringUtils.isEmpty(tenderedCouponCode)) {
        removeCouponOnly = true;
      }
      else {
        removeCouponOnly = false;
      }

      /*
       * Remove any existing coupon.
       */
      if (!StringUtils.isEmpty(currentCouponCode)) {
        /*
         * Remove the coupon from the order, if there is a coupon to claim then
         * wait until claiming this coupon before repricing the order. Otherwise
         * remove the coupon and reprice the order.
         */
        if (removeCouponOnly) {
          removeCoupon(pOrder,
                       pProfile,
                       true,
                       pUserPricingModels,
                       pUserLocale);
        }
        else {
          removeCoupon(pOrder,
                       pProfile,
                       false,
                       pUserPricingModels,
                       pUserLocale);
        }
      }

      /*
       * Try to claim the tendered coupon.
       */
      if (!removeCouponOnly) {
        canClaimTenderedCoupon = getClaimableManager().canClaimCoupon(pProfile.getRepositoryId(),
                                                                      tenderedCouponCode);

        /*
         * Claim the new coupon code if it is valid; otherwise reclaim the original coupon
         */
        if (canClaimTenderedCoupon) {
          claimCoupon(tenderedCouponCode,
                      pOrder,
                      pProfile,
                      true,
                      pUserPricingModels,
                      pUserLocale);
        }
        else {
          if (!StringUtils.isEmpty(currentCouponCode)) {
            claimCoupon(currentCouponCode,
                        pOrder,
                        pProfile,
                        false,
                        pUserPricingModels,
                        pUserLocale);
          }
        }
      }

      getOrderManager().updateOrder(pOrder);

      rollback = false;

      return (removeCouponOnly || canClaimTenderedCoupon);
    }
    catch (Exception exception) {
      throw new CommerceException(exception);
    }
    finally {
      try {
        td.end(rollback);
      }
      catch (TransactionDemarcationException transactionDemarcationException) {
        throw new CommerceException(transactionDemarcationException);
      }
    }
  }

  /**
   * This method claims a coupon specified by its code for a specific user and order and reprices the order.
   *
   * @param pCouponCode - coupon code to be claimed
   * @param pOrder - order to be repriced when the coupon has been claimed
   * @param pProfile - user who claims a coupon
   * @param pUserPricingModels - user's pricing models to be used for order reprice process
   * @param pUserLocale - user's locale to be used when repricing order
   * @throws CommerceException - if something goes wrong
   * @throws IllegalArgumentException - if order has a claimed coupon already
   */
  public void claimCoupon(String pCouponCode,
                          StoreOrderImpl pOrder,
                          RepositoryItem pProfile,
                          PricingModelHolder pUserPricingModels,
                          Locale pUserLocale)
      throws CommerceException, IllegalArgumentException {
    claimCoupon(pCouponCode,
                pOrder,
                pProfile,
                true,
                pUserPricingModels,
                pUserLocale);
  }

  /**
   * This method claims a coupon specified by its code for a specific user and order.
   * The order is repriced if the 'pRepriceOrder' parameter is true.
   *
   * @param pCouponCode - coupon code to be claimed
   * @param pOrder - order to be repriced when the coupon has been claimed
   * @param pProfile - user who claims a coupon
   * @param pRepriceOrder - boolean flag to indicate if order should be repriced
   * @param pUserPricingModels - user's pricing models to be used for order reprice process
   * @param pUserLocale - user's locale to be used when repricing order
   * @throws CommerceException - if something goes wrong
   * @throws IllegalArgumentException - if order has a claimed coupon already
   */
  public void claimCoupon(String pCouponCode,
                          StoreOrderImpl pOrder,
                          RepositoryItem pProfile,
                          boolean pRepriceOrder,
                          PricingModelHolder pUserPricingModels,
                          Locale pUserLocale)
      throws CommerceException, IllegalArgumentException {
    // First, check the coupon code to be used
    if (StringUtils.isBlank(pCouponCode)) {
      return;
    }

    // Then check if the order specified has a claimed coupon already; if true, throw an exception
    if (!StringUtils.isEmpty(pOrder.getCouponCode())) {
      throw new IllegalArgumentException("There is a coupon claimed for order specified!");
    }

    // And after that claim a coupon
    TransactionDemarcation td = new TransactionDemarcation();
    boolean shouldRollback = true; // We should rollback transaction, if any exception occurs

    try {
      td.begin(getTransactionManager());
      
      getClaimableManager().claimCoupon(pProfile.getRepositoryId(), pCouponCode);

      // Save claimed coupon on the order
      pOrder.setCouponCode(pCouponCode);

      if (pRepriceOrder) {
        // Initialize pricing models in order to use recently claimed coupon's promotions
        pUserPricingModels.initializePricingModels();

        // Re-price order in order to display most recent prices for items etc.
        repriceOrder(pOrder, pUserPricingModels, pUserLocale, pProfile);
      }

      shouldRollback = false;
    }
    catch (ClaimableException e) {
      // Propagate only exceptions that are not 'Duplicate promotion for current user'; this exact exception should be surpressed!
      if (e.getCause() instanceof DuplicatePromotionException) {
        return;
      }
      throw e;
    }
    catch (TransactionDemarcationException e) {
      throw new CommerceException(e);
    } 
    finally {
      // Commit or rollback transaction, only if we have created it
      // If someone's called this method, then he should take care of it
      try {
        td.end(shouldRollback);
      } catch (TransactionDemarcationException e)
      {
        throw new CommerceException(e);
      }
    }
  }

  /**
   * This method removes a coupon from the order specified and reprices the order.
   *
   * @param pOrder - order with coupon claimed
   * @param pProfile - user who removes a coupon
   * @param pUserPricingModels - user's pricing models to be used in order repricing process
   * @param pUserLocale - user's locale to be used when repricing order
   * @throws CommerceException - if something goes wrong
   */
  public void removeCoupon(StoreOrderImpl pOrder,
                           RepositoryItem pProfile,
                           PricingModelHolder pUserPricingModels,
                           Locale pUserLocale)
      throws CommerceException {
    removeCoupon(pOrder,
                 pProfile,
                 true,
                 pUserPricingModels,
                 pUserLocale);
  }


  /**
   * This method removes a coupon from the order specified.
   * The order is repriced if the 'pRepriceOrder' parameter is true.

   * @param pOrder - order with coupon claimed
   * @param pProfile - user who removes a coupon
   * @param pRepriceOrder - boolean flag to indicate if order should be repriced
   * @param pUserPricingModels - user's pricing models to be used in order repricing process
   * @param pUserLocale - user's locale to be used when repricing order
   * @throws CommerceException - if something goes wrong
   */
  public void removeCoupon(StoreOrderImpl pOrder,
                           RepositoryItem pProfile,
                           boolean pRepriceOrder,
                           PricingModelHolder pUserPricingModels,
                           Locale pUserLocale)
      throws CommerceException {
    String couponCode = pOrder.getCouponCode();
    // If there's no couponCode, there's nothing to remove
    if (couponCode == null) {
      return;
    }
    
    TransactionDemarcation td = new TransactionDemarcation();
    boolean shouldRollback = true; // We should rollback transaction, if any exception occurs

    try {
      td.begin(getTransactionManager());
      
      // Get and remove all coupon's promotions from order
      RepositoryItem coupon = getClaimableManager().claimItem(couponCode);
      String promotionsPropertyName = getClaimableManager().getClaimableTools().getPromotionsPropertyName();
      @SuppressWarnings("unchecked") //ok, cause coupon.promotions contains a set of propmotions (in form of repository items)
      Collection<RepositoryItem> promotions = (Collection<RepositoryItem>) coupon.getPropertyValue(promotionsPropertyName);

      // Ensure pProfile to be a MutableRepositoryItem
      if (!(pProfile instanceof MutableRepositoryItem)) {
        // pProfile uses a MutableRepository for sure
        pProfile = ((MutableRepository) pProfile.getRepository()).getItemForUpdate(pProfile.getRepositoryId(),
            pProfile.getItemDescriptor().getItemDescriptorName());
      }

      for (RepositoryItem promotion: promotions) {
        // Now we can cast pProfile to the type we need
        getClaimableManager().getPromotionTools().removePromotion((MutableRepositoryItem) pProfile, promotion, false);
      }

      // After this is done, we can remove coupon code from order
      pOrder.setCouponCode(null);

      if (pRepriceOrder) {
        // Initialize pricing models to use current promotions, that is exclude coupon's promotions from pricing models
        pUserPricingModels.initializePricingModels();

        // Re-price order to display most recent prices
        repriceOrder(pOrder, pUserPricingModels, pUserLocale, pProfile);
      }
 
      shouldRollback = false;
    }
    catch (TransactionDemarcationException e) {
      throw new CommerceException(e);
    }
    catch (RepositoryException e) {
      throw new CommerceException(e);
    }
    finally {
      // Commit or rollback transaction, only if we have created it
      // If someone's called this method, then he should take care of it
      try {
        td.end(shouldRollback);
      } catch (TransactionDemarcationException e)
      {
        throw new CommerceException(e);
      }
    }
  }
}
