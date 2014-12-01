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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.commerce.order.purchase.CartModifierFormHandler;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.projects.store.gifts.StoreGiftlistFormHandler;
import atg.projects.store.inventory.StoreInventoryManager;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.projects.store.ui.AjaxUtils;
import atg.repository.RepositoryItem;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;


/**
 * Extends the default CartModifierFormHandler for custom functionality.
 * This class holds all the handle methods for the buttons on the cart
 * page. Since all buttons need to perform similar functionality,
 * including updating item quantities, adding gift wrap/gift message
 * and moving to the checkout process, all the methods have been
 * captured in this class. In the case of ExpressCheckout, this
 * class does the preliminary duties of modifying cart contents, and
 * then calls the ExpressCheckoutFormHandler to run the express
 * checkout pipeline.
 * <p>
 *
 * @author ATG
 * @version $Revision: #3 $
 */
public class StoreCartFormHandler extends CartModifierFormHandler {
    
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/StoreCartFormHandler.java#3 $$Change: 635816 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------
  
  private static final String PRODUCT = "product";

  private static final String CLOTHING_SKU_TYPE = "clothing";

  private static final String FURNITURE_SKU_TYPE = "furniture";

  private static final String MSG_SELECT_SKU = "addToCart_selectSku";

  private static final String MSG_SELECT_WOOD_FINISH = "addToCart_selectWoodFinish";

  private static final String MSG_SELECT_COLOR_SIZE = "addToCart_selectColorSize";
  
  /*
   * Error messages for order item quantity outside permitted min/max range
   */
  public static final String MSG_LESS_THAN_MIN_QUANTITY = "quantityLessThanMin";
  public static final String MSG_MORE_THAN_MAX_QUANTITY = "quantityMoreThanMax";
  public static final String MSG_ITEM_LESS_THAN_MIN_QUANTITY = "itemQuantityLessThanMin";
  public static final String MSG_ITEM_MORE_THAN_MAX_QUANTITY = "itemQuantityMoreThanMax";

  /**
   * Promotion resource bundle name.
   */
  protected static String sPromotionBundleName = "atg.commerce.promotion.PromotionResources";

  /**
   * Add item to giftlist success redirect URL.
   */
  Map<String,String> mAddItemToGiftlistSuccessURLMap = new HashMap<String,String> ();

  /**
   * Add item to giftlist error redirect URL.
   */
  String mAddItemToGiftlistErrorURL;

  /**
   * Add item to gift list login URL.
   */
  String mAddItemToGiftlistLoginURL;
  
  /**
   * AddItemToGift that contains gift list ID to add items to.
   */
  String mAddItemToGiftlist;
  
  /**
   * Contains gift(wish) list ID to add items to.
   */
  String mAddItemToWishlist;

  /**
   * Continue shopping success redirect URL.
   */
  private String mContinueShoppingSuccessURL;

  /**
   * Continue shopping error redirect URL.
   */
  private String mContinueShoppingErrorURL;

  /**
   * Update success redirect URL.
   */
  private String mUpdateSuccessURL;

  /**
   * Update error redirect URL.
   */
  private String mUpdateErrorURL;

  /**
   * Express checkout success redirect URL.
   */
  private String mExpressCheckoutSuccessURL;

  /**
   * Express checkout error redirect URL.
   */
  private String mExpressCheckoutErrorURL;

  /**
   * Store express checkout form handler.
   */
  private StoreExpressCheckoutFormHandler mStoreExpressCheckoutFormHandler;

  /**
   * Is gift wrap selected.
   */
  private boolean mGiftWrapSelected;

  /**
   * Gift wrap sku id.
   */
  private String mGiftWrapSkuId;

  /**
   * Gift wrap product id.
   */
  private String mGiftWrapProductId;

  /**
   * Is gift note selected.
   */
  private boolean mGiftNoteSelected;

  /**
   * Gift message URL.
   */
  private String mGiftMessageUrl;

  /**
   * Shipping info URL.
   */
  private String mShippingInfoURL;

  /**
   * Login during checkout URL.
   */
  private String mLoginDuringCheckoutURL;

  /**
   * Confirmation URL.
   */
  private String mConfirmationURL;

  /**
   * Shipping information.
   */
  Map mShippingInfo = new Hashtable();

  /**
   * Add item to order success redirect URL.
   */
  protected String mAjaxAddItemToOrderSuccessUrl;

  /**
   * Add item to order error redirect URL.
   */
  protected String mAjaxAddItemToOrderErrorUrl;
  
  /**
   * Should initialize shipping information from profile.
   */
  protected boolean mInitializeShippingInfoFromProfile;

  /**
   * Store shipping group.
   */
  ShippingGroup mStoreShippingGroup = null;

  /**
   * Minimum & maximum quantities permitted per order item
   */
  private long mMinQuantity = -1;
  private long mMaxQuantity = -1;
  
  private String mSkuType = null;
  
  /**
   * The skuType property is used by form handler when rendering a 'no sku' exception.
   * Different messages are displayed to the user depending on the skuType property specified.
   * @return current skuType property value.
   */
  public String getSkuType()
  {
    return mSkuType;
  }

  public void setSkuType(String pSkuType)
  {
    mSkuType = pSkuType;
  }
  
  /**
   * property: URL in the event an unavailable item is added to cart using
   * the javascript free picker
   */
  private String mSkuUnavailableURL;
  
  public String getSkuUnavailableURL(){
    return mSkuUnavailableURL;
  }
  
  public void setSkuUnavailableURL(String pSkuUnavailableURL){
    mSkuUnavailableURL = pSkuUnavailableURL;
  }
  
  /**
   * property: Inventory manager.
   */
  protected StoreInventoryManager mInventoryManager;

  /**
   * @return the inventoryManager.
   */
  public StoreInventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  /**
   * @param pInventoryManager - the inventoryManager to set.
   */
  public void setInventoryManager(StoreInventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }
  
  /**
   * property: Reference to the CartProcessHelper component
   */
  private StoreCartProcessHelper mCartHelper;
  
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
   * @return the Cart Helper component.
   */
  public StoreCartProcessHelper getCartHelper() {
    return mCartHelper;
  }

  /**
   * @param pShippingHelper the cart helper component to set.
   */
  public void setCartHelper(StoreCartProcessHelper pCartHelper) {
    mCartHelper = pCartHelper;
  }
  
  /**
   * Sets property AddItemToGiftlistSuccessURLMap.
   * @param pAddItemToGiftlistSuccessURLMap  The property to store the success URLs map for addItemToGiftlist.
   *        The map's keys are gift list IDs.
   * @beaninfo description:  The property to store the success URLs map for addItemToGiftlist.
   **/
  public void setAddItemToGiftlistSuccessURLMap(Map<String,String> pAddItemToGiftlistSuccessURL) {
    mAddItemToGiftlistSuccessURLMap = pAddItemToGiftlistSuccessURL;
  }

  /**
   * Returns property AddItemToGiftlistSuccessURLMap.
   * @return The value of the property AddItemToGiftlistSuccessURLMap
   **/
  public Map<String,String> getAddItemToGiftlistSuccessURLMap() {
    return mAddItemToGiftlistSuccessURLMap;
  }

  /**
   * Sets property AddItemToGiftlistErrorURL.
   * @param pAddItemToGiftlistErrorURL  The property to store the error URL for addItemToGiftlist.
   * @beaninfo description:  The property to store the error URL for addItemToGiftlist.
   **/
  public void setAddItemToGiftlistErrorURL(String pAddItemToGiftlistErrorURL) {
    mAddItemToGiftlistErrorURL = pAddItemToGiftlistErrorURL;
  }

  /**
   * Returns property AddItemToGiftlistErrorURL.
   * @return The value of the property AddItemToGiftlistErrorURL
   **/
  public String getAddItemToGiftlistErrorURL() {
    return mAddItemToGiftlistErrorURL;
  }

  /**
   * Sets property AddItemToGiftlistLoginURL.
   * @param pAddItemToGiftlistLoginURL  The property to store the URL for where the user should be redirected
   *        if they attempt to add an item to the giftlist without being logged in.
   * @beaninfo description:  The property to store the URL for where the user should be redirected
   *           if they attempt to add an item to the giftlist without being logged in.
   **/
  public void setAddItemToGiftlistLoginURL(String pAddItemToGiftlistLoginURL) {
    mAddItemToGiftlistLoginURL = pAddItemToGiftlistLoginURL;
  }

  /**
   * @return add item to gift list login URL.
   **/
  public String getAddItemToGiftlistLoginURL() {
    return mAddItemToGiftlistLoginURL;
  }
  
  /**
   * Sets property AddItemToGiftlist.
   * @param pAddItemToGiftlist  The property to store the gift list Id to which
   *        item should be added.
   * @beaninfo description:  The property to store the gift list Id to which
   *        item should be added.
   **/
  public void setAddItemToGiftlist(String pAddItemToGiftlist) {
    mAddItemToGiftlist = pAddItemToGiftlist;
  }

  /**
   * @return AddItemToGiftlist property.
   **/
  public String getAddItemToGiftlist() {
    return mAddItemToGiftlist;
  }
  
  /**
   * Sets property AddItemToWishlist.
   * @param pAddItemToWishlist  The property to store the gift list Id to which
   *        item should be added.
   * @beaninfo description:  The property to store the gift list Id to which
   *        item should be added.
   **/
  public void setAddItemToWishlist(String pAddItemToWishlist) {
    mAddItemToWishlist = pAddItemToWishlist;
  }

  /**
   * @return AddItemToWishlist property.
   **/
  public String getAddItemToWishlist() {
    return mAddItemToWishlist;
  }

  /**
   * @return the continue shopping success redirect URL.
   */
  public String getContinueShoppingSuccessURL() {
    return mContinueShoppingSuccessURL;
  }

  /**
   * @param pContinueShoppingSuccessURL - the continue shopping
   * success redirect URL
   */
  public void setContinueShoppingSuccessURL(String pContinueShoppingSuccessURL) {
    mContinueShoppingSuccessURL = pContinueShoppingSuccessURL;
  }

  /**
   * @return the continue shopping error redirect URL.
   */
  public String getContinueShoppingErrorURL() {
    return mContinueShoppingErrorURL;
  }

  /**
   * @param pContinueShoppingErrorURL - the continue shopping error redirect URL.
   */
  public void setContinueShoppingErrorURL(String pContinueShoppingErrorURL) {
    mContinueShoppingErrorURL = pContinueShoppingErrorURL;
  }

  /**
    * @return the removal commerce ids.
    */
  public String[] getRemoveItemFromOrder() {
    return getRemovalCommerceIds();
  }

  /**
   * @param pRemoveItemFromOrder - the removal commerce ids.
   */
  public void setRemoveItemFromOrder(String[] pRemoveItemFromOrder) {
    if ((pRemoveItemFromOrder != null) && (pRemoveItemFromOrder.length > 0)) {
      setRemovalCommerceIds(pRemoveItemFromOrder);
    } else {
      setRemovalCommerceIds(null);
    }
  }

  /**
   * @return the update success redirect URL.
   */
  public String getUpdateSuccessURL() {
    return mUpdateSuccessURL;
  }

  /**
   * @param pUpdateSuccessURL - the update success redirect URL.
   */
  public void setUpdateSuccessURL(String pUpdateSuccessURL) {
    mUpdateSuccessURL = pUpdateSuccessURL;
  }

  /**
   * @return the update error redirect URL.
   */
  public String getUpdateErrorURL() {
    return mUpdateErrorURL;
  }

  /**
   * @param pUpdateErrorURL - the update error redirect URL.
   */
  public void setUpdateErrorURL(String pUpdateErrorURL) {
    mUpdateErrorURL = pUpdateErrorURL;
  }

  /**
   * @return the express checkout success redirect URL.
   */
  public String getExpressCheckoutSuccessURL() {
    return mExpressCheckoutSuccessURL;
  }

  /**
   * @param pExpressCheckoutSuccessURL - the express checkout
   * success redirect URL.
   */
  public void setExpressCheckoutSuccessURL(String pExpressCheckoutSuccessURL) {
    mExpressCheckoutSuccessURL = pExpressCheckoutSuccessURL;
  }

  /**
   * @return the express checkout error redirect URL.
   */
  public String getExpressCheckoutErrorURL() {
    return mExpressCheckoutErrorURL;
  }

  /**
   * @param pExpressCheckoutErrorURL - the express checkout
   * error redirect URL.
   */
  public void setExpressCheckoutErrorURL(String pExpressCheckoutErrorURL) {
    mExpressCheckoutErrorURL = pExpressCheckoutErrorURL;
  }

  /**
   * @return the Store express checkout form handler.
   */
  public StoreExpressCheckoutFormHandler getStoreExpressCheckoutFormHandler() {
    return mStoreExpressCheckoutFormHandler;
  }

  /**
   * @param pStoreExpressCheckoutFormHandler -
   * the Store express checkout form handler.
   */
  public void setStoreExpressCheckoutFormHandler(
    StoreExpressCheckoutFormHandler pStoreExpressCheckoutFormHandler) {
    mStoreExpressCheckoutFormHandler = pStoreExpressCheckoutFormHandler;
  }

  /**
   * @return the gift wrap selected property.
   */
  public boolean getGiftWrapSelected() {
    return mGiftWrapSelected;
  }

  /**
   * @param  pGiftWrapSelected - the gift wrap selected property.
   */
  public void setGiftWrapSelected(boolean pGiftWrapSelected) {
    mGiftWrapSelected = pGiftWrapSelected;
  }

  /**
   * @return the gift wrap sku id property.
   */
  public String getGiftWrapSkuId() {
    return mGiftWrapSkuId;
  }

  /**
   * Sets the mGiftWrapSkuId property.
   * This is set by the page after getting the SKU id from the
   * targeter.
   * @param pGiftWrapSkuId - gift wrap sku id property
   */
  public void setGiftWrapSkuId(String pGiftWrapSkuId) {
    mGiftWrapSkuId = pGiftWrapSkuId;
  }

  /**
   * @return the gift wrap product id property.
   */
  public String getGiftWrapProductId() {
    return mGiftWrapProductId;
  }

  /**
   * @param pGiftWrapProductId - the gift wrap product id property.
   */
  public void setGiftWrapProductId(String pGiftWrapProductId) {
    mGiftWrapProductId = pGiftWrapProductId;
  }

  /**
   * @return the gift note selected property.
   */
  public boolean getGiftNoteSelected() {
    return mGiftNoteSelected;
  }

  /**
   * @param pGiftNoteSelected - the gift note selected property.
   */
  public void setGiftNoteSelected(boolean pGiftNoteSelected) {
    mGiftNoteSelected = pGiftNoteSelected;
  }

  /**
   * @return the gift message url property.
   */
  public String getGiftMessageUrl() {
    return mGiftMessageUrl;
  }

  /**
   * @param pGiftMessageUrl - the gift message url property.
   */
  public void setGiftMessageUrl(String pGiftMessageUrl) {
    mGiftMessageUrl = pGiftMessageUrl;
  }

  /**
   * @return the shipping information URL property.
   */
  public String getShippingInfoURL() {
    return mShippingInfoURL;
  }

  /**
   * @param pShippingInfoURL - the shipping information URL property.
   */
  public void setShippingInfoURL(String pShippingInfoURL) {
    mShippingInfoURL = pShippingInfoURL;
  }

  /**
   * @return the login during checkout URL property.
   */
  public String getLoginDuringCheckoutURL() {
    return mLoginDuringCheckoutURL;
  }

  /**
   * @param pLoginDuringCheckoutURL - the login during checkout URL property.
   */
  public void setLoginDuringCheckoutURL(String pLoginDuringCheckoutURL) {
    mLoginDuringCheckoutURL = pLoginDuringCheckoutURL;
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
   * @return the confirmation URL property.
   */
  public String getConfirmationURL() {
    return mConfirmationURL;
  }

  /**
   * @param pConfirmationURL - the confirmation URL property.
   */
  public void setConfirmationURL(String pConfirmationURL) {
    mConfirmationURL = pConfirmationURL;
  }

  /**
   * Gets the AjaxAddItemToOrderSuccessUrl.
   * @return the add item to order success redirect Url.
   */
  public String getAjaxAddItemToOrderSuccessUrl() {
    return mAjaxAddItemToOrderSuccessUrl;
  }

  /**
   * @param pAjaxAddItemToOrderSuccessUrl -
   * the add item to order success redirect Url to set.
   */
  public void setAjaxAddItemToOrderSuccessUrl(String pAjaxAddItemToOrderSuccessUrl) {
    mAjaxAddItemToOrderSuccessUrl = pAjaxAddItemToOrderSuccessUrl;
  }

  /**
   * Gets the AjaxAddItemToOrderErrorUrl.
   * @return the add item to order error redirect Url
   */
  public String getAjaxAddItemToOrderErrorUrl() {
    return mAjaxAddItemToOrderErrorUrl;
  }

  /**
   * @param pAjaxAddItemToOrderErrorUrl -
   * the add item to order error Url to set.
   */
  public void setAjaxAddItemToOrderErrorUrl(String pAjaxAddItemToOrderErrorUrl) {
    mAjaxAddItemToOrderErrorUrl = pAjaxAddItemToOrderErrorUrl;
  }

  /**
   * @return true if shipping info should be initialized
   * from profile property.
   */
  public boolean getInitializeShippingInfoFromProfile() {
    return mInitializeShippingInfoFromProfile;
  }

  /**
   * Set the minimum quantity permitted per order item
   *
   * @param pMinQuantity minimum quantity permitted per order item
   */
  public void setMinQuantity(long pMinQuantity) {
      mMinQuantity = pMinQuantity;
  }

  /**
   * Returns the minimum quantity permitted per order item
   *
   * @return minimum quantity permitted per order item
   */
  public long getMinQuantity() {
      return mMinQuantity;
  }


  /**
   * Set the maximum quantity permitted per order item
   * 
   * @param pMaxQuantity maximum quantity permitted per order item
   */
  public void setMaxQuantity(long pMaxQuantity) {
      mMaxQuantity = pMaxQuantity;
  }

  /**
   * Returns the maximum quantity permitted per order item
   *
   * @return maximum quantity permitted per order item
   */
  public long getMaxQuantity() {
      return mMaxQuantity;
  }
  
  /**
   * Gift list form handler.
   */
  private StoreGiftlistFormHandler mStoreGiftlistFormHandler;
  
  /**
   * @return the Gift list form handler.
   */
  public StoreGiftlistFormHandler getStoreGiftlistFormHandler() {
    return mStoreGiftlistFormHandler;
  }

  /**
   * @param pStoreGiftlistFormHandler -
   * the Gift list form handler.
   */
  public void setStoreGiftlistFormHandler(
    StoreGiftlistFormHandler pStoreGiftlistFormHandler) {
    mStoreGiftlistFormHandler = pStoreGiftlistFormHandler;
  }

  //-------------------------------------
  // Methods
  //-------------------------------------

  /**
   * This method is called when the user wants to starts the CHECKOUT process
   * for an order.  It will first validates quantity.  If quantity is valid 
   * it checks if order contains only items with zero quantity and update order 
   * (remove all commerce items) if so. After this has happened it will 
   * claim coupons and invokes super.handleMoveToPurchaseInfoByCommerceId method.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return a <code>boolean</code> value
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (!getFormError()) {
      validateQuantity(pRequest, pResponse);
    }
    
    if(!getFormError()) {
      long overallQuantity = evalOverallItemsQuantity(pRequest, pResponse);
      
      // Zero overall quantity means that order contains only items with zero 
      // quantity. We want to mimic 'update' action to remove such items 
      // from the order and return to the empty shopping cart page.
      // Otherwise, we'll get an error later from ProcValidateOrderForCheckout pipeline
      // chain which checks items' quantity.
      if(overallQuantity == 0) {
        return handleUpdate(pRequest, pResponse);
      }
    }

    // claim any coupons entered by the user
    if (!getFormError()) {
      tenderCoupon(pRequest, pResponse);
    }   

    if (getFormError()) {
      if (isLoggingDebug()) {
        logDebug("Not calling handleMoveToPurchaseInfo, form error encountered.");
      }

      return checkFormRedirect(null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse);
    }
    
    return super.handleMoveToPurchaseInfoByCommerceId(pRequest, pResponse);
  }

  /**
   * The addRemoveGiftServices method should be done after modifying
   * cart contents. The modifyOrder method will remove the gw item from
   * the cart.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postMoveToPurchaseInfo(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    // add or remove any gift services.
    if (!getFormError()) {
      addRemoveGiftServices();
      // Must call determineSuccessURL after addRemoveGiftServices, otherwise
      // the gift note may not have been added yet, and redirect will not
      // be computed correctly.
      determineSuccessURL(pRequest);
    }
    
    super.postMoveToPurchaseInfo(pRequest, pResponse);
    
    if (mCheckoutProgressStates != null && !getFormError())
    {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.SHIPPING.toString());
    }
  }

  /**
   * This method will update the cart contents.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   *
   * @return true if success, false - otherwise
   *
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  public boolean handleUpdate(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    validateQuantity(pRequest, pResponse);

    if (getFormError()) {
      return checkFormRedirect(null, getUpdateErrorURL(), pRequest, pResponse);
    }

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleMoveToPurchaseInfoByCommerceId";

    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      Transaction tr = null;

      try {
        tr = ensureTransaction();

        if (getUserLocale() == null) {
          setUserLocale(getUserLocale(pRequest, pResponse));
        }

        if (!checkFormRedirect(null, getUpdateErrorURL(), pRequest, pResponse)) {
          return false;
        }

        synchronized (getOrder()) {
          // claim any coupons entered by the user
          if (!getFormError()) {
            tenderCoupon(pRequest, pResponse);
          }

          // now modify the order based upon what the user put in
          // the form
          if (!getFormError()) {
            modifyOrderByCommerceId(pRequest, pResponse);
          }
          
          // after taking care of the user's mods now do ours
          // start with gift services and gift wrap
          if (!getFormError()) {
            addRemoveGiftServices();
          }

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized
      } catch (CommerceException ce) {
        processException(ce, StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER, pRequest, pResponse);
      } catch (RunProcessException re) {
        processException(re, StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER, pRequest, pResponse);
      } finally {
        if (tr != null) {
          commitTransaction(tr);
        }

        if (rrm != null) {
          rrm.removeRequestEntry(myHandleMethod);
        }
      }
    }

    // If NO form errors are found, redirect to the success URL.
    // If form errors are found, redirect to the error URL.
    return checkFormRedirect(getUpdateSuccessURL(), getUpdateErrorURL(), pRequest, pResponse);
  }

  /**
   * Need to do the exact same thing as handleUpdate, but redirect to
   * different URL. Let page specify success URL based on Profile.categoryLastBrowsed.
   * Set updateSuccessURL here, and re-use handleUpdate code.
   *
   * @param pRequest the request
   * @param pResponse the response
   * @return boolean success or failure
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  public boolean handleContinueShopping(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    setUpdateSuccessURL(getContinueShoppingSuccessURL());
    setUpdateErrorURL(getContinueShoppingErrorURL());

    return handleUpdate(pRequest, pResponse);
  }

  
  /**
   * {@inheritDoc}
   *
   * @param pRequest the request object
   * @param pResponse the resonse object
   * @return true if the request was handled properly
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public boolean handleRemoveItemFromOrder(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws IOException, ServletException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleRemoveItemFromOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        if (! checkFormRedirect (null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preRemoveItemFromOrder(pRequest, pResponse);

          if (! checkFormRedirect (null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse))
            return false;

          if (getRemovalCommerceIds() != null) {
            try {
              deleteItems(pRequest, pResponse);
              Map extraParams = createRepriceParameterMap();
              runProcessRepriceOrder(getModifyOrderPricingOp(), getOrder(), getUserPricingModels(), getUserLocale(), getProfile(),
                  extraParams);
            } catch (Exception exc) {
              processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
            }
          }
          if (! checkFormRedirect (null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse))
            return false;

          postRemoveItemFromOrder(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getRemoveItemFromOrderSuccessURL(), getRemoveItemFromOrderErrorURL(), pRequest, pResponse);
      }
      finally {
        if (tr != null) commitTransaction(tr);
        if (rrm != null)
          rrm.removeRequestEntry(myHandleMethod);
      }
    }
    else {
      return false;
    }
  }

  /**
   * Need to use the same form handler from the page. Modify cart contents,
   * and then call the ExpressCheckoutFormHandler to use express checkout.
   * @param pRequest the request
   * @param pResponse the response
   * @return boolean success or failure
   * @throws ServletException If servlet exception occurs
   * @throws IOException If IO exception occurs
   */
  public boolean handleExpressCheckout(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Transaction tr = null;

    validateQuantity(pRequest, pResponse);

    if (getFormError()) {
      return checkFormRedirect(null, getExpressCheckoutErrorURL(), pRequest, pResponse);
    }

    try {
      tr = ensureTransaction();

      synchronized (getOrder()) {
        // claim any coupons entered by the user
        if (!getFormError()) {
          tenderCoupon(pRequest, pResponse);
        }

        // now modify the order based upon what the user put in
        // the form
        if (!getFormError()) {
          modifyOrderByCommerceId(pRequest, pResponse);
        } else {
          return checkFormRedirect(getExpressCheckoutSuccessURL(), getExpressCheckoutErrorURL(), pRequest, pResponse);
        }
        
        // after taking care of the user's mods now do ours
        // start with gift services and gift wra;
        if (!getFormError()) {
          addRemoveGiftServices();
        }

        // Need to check inventory. Just run moveToConfirmation
        // chain which contains inventory check.
        try {
          // Make sure inventory is valid for checkout
          if (getUserLocale() == null) {
            setUserLocale(getUserLocale(pRequest, pResponse));
          }

          runProcessMoveToPurchaseInfo(getOrder(), getUserPricingModels(), getUserLocale(), getProfile(), null);
        } catch (Exception exc) {
          if (isLoggingDebug()) {
            logDebug("exception: ", exc);
          }

          processException(exc, MSG_ERROR_MOVE_TO_PURCHASE_INFO, pRequest, pResponse);
        }

        if (getFormError()) {
          // If NO form errors are found, redirect to the success URL.
          // If form errors are found, redirect to the error URL.
          return checkFormRedirect(getExpressCheckoutSuccessURL(), getExpressCheckoutErrorURL(), pRequest, pResponse);
        }
      } // synchronized

      StoreExpressCheckoutFormHandler bsecfh = getStoreExpressCheckoutFormHandler();

      determineExpressCheckoutSuccessURL(pRequest, pResponse);
      bsecfh.setExpressCheckoutSuccessURL(getExpressCheckoutSuccessURL());
      bsecfh.setExpressCheckoutErrorURL(getExpressCheckoutErrorURL());
      
      bsecfh.setCheckoutProgressStates(mCheckoutProgressStates);

      return bsecfh.handleExpressCheckout(pRequest, pResponse);
    } catch (CommerceException ce) {
      String msg = formatUserMessage(StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER, pRequest, pResponse);
      addFormException(new DropletFormException(msg, ce, getCartHelper().MSG_ERROR_MODIFYING_ORDER));
    } catch (RunProcessException rpe) {
      String msg = formatUserMessage(StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER, pRequest, pResponse);
      addFormException(new DropletFormException(msg, rpe, StoreCartProcessHelper.MSG_ERROR_MODIFYING_ORDER));
    } finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }

    return checkFormRedirect(getExpressCheckoutSuccessURL(), getExpressCheckoutErrorURL(), pRequest, pResponse);
  }
 
  /**
   * Removes gift services if all items in the order are gift wrap items.
   * @return true if all items are gift wrap items and gift wrap services were removed
   */
  protected boolean removeGiftServicesWhenAllGiftWrap() {
    StoreOrderImpl order = (StoreOrderImpl) getOrder();
    
    //if the order is all gift wrap, remove it unconditionally.
    if (getCartHelper().isAllGiftWrap(order)) {
      getCartHelper().addRemoveGiftServices(order, false, false, getGiftWrapSkuId(), getGiftWrapProductId());

      try {
        runProcessRepriceOrder(getModifyOrderPricingOp(), getOrder(), getUserPricingModels(), getUserLocale(),
          getProfile(), null);
      } catch (RunProcessException rpe) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor(rpe.toString()));
        }
      }

      return true;
    }

    return false;
  }

  /**
   * This is a convenience method for adding gift services. This needs to
   * be done when the update, checkout, delete or continue shopping buttons are
   * pushed.
   */
  protected void addRemoveGiftServices() {
    if (isLoggingDebug()) {
      logDebug("addRemoveGiftService(): giftwrap=" + getGiftWrapSelected() + " giftnote=" + getGiftNoteSelected() +
        " giftwrapsku=" + getGiftWrapSkuId() + " giftwrapprod=" + getGiftWrapProductId() +
        " order.getGiftMessagePopulated(): " + ((StoreOrderImpl) getOrder()).getGiftMessagePopulated());
    }

    StoreOrderManager om = (StoreOrderManager) getCartHelper().getOrderManager();
    StoreOrderImpl order = (StoreOrderImpl) getOrder();

    // If gift wrap is either being added or removed, we need to reprice
    boolean reprice = getCartHelper().isGiftWrapAddedOrRemoved(order, getGiftWrapSelected());
    

    if (!removeGiftServicesWhenAllGiftWrap()) {
      getCartHelper().addRemoveGiftServices(order, getGiftWrapSelected(), getGiftNoteSelected(), getGiftWrapSkuId(),
        getGiftWrapProductId());
    }

    if(reprice) {
      try {
        runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(), getUserLocale(), getProfile(),
          null);
      } catch (RunProcessException rpe) {
        if (isLoggingError()) {
          logError(LogUtils.formatMajor(rpe.toString()));
        }
      }
    }
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
   * Utility method to create success URL based on if the user and order state.
   * Not possible to set this in the page b/c we don't know if the
   * user selects gift message or not when clicking "checkout" button.
   * Set success URL based on current state of order and login status.
   *
   *    if ((order doesn't have samples AND user has not chosen to skip samples)
   *          OR (order has gift message))
   *        redirect to samples page.
   *    if (order has samples OR user explicitly chose no samples)
   *       if (user is logged in)
   *          redirect to shipping
   *       else
   *          redirect to login
   *
   * @param pRequest - http request
   */
  protected void determineSuccessURL(DynamoHttpServletRequest pRequest) {
    StorePropertyManager pm = (StorePropertyManager) getCartHelper().getStoreOrderTools().getProfileTools().getPropertyManager();

    StoreOrderImpl order = (StoreOrderImpl) getOrder();
    RepositoryItem profile = getProfile();

    // If order has gift message, but it is not populated yet,
    // then show gift message page.
    if (order.getContainsGiftMessage() || order.isShouldAddGiftNote()) {
      if (!order.getGiftMessagePopulated()) {
        if (isLoggingDebug()) {
          logDebug("User has a gift message that hasn't been filled out yet." + " Sending to gift message page.");
        }

        setMoveToPurchaseInfoSuccessURL(getGiftMessageUrl());

        return;
      }
    }

    // Don't show samples page, choose shipping or login depending on login
    if (getCartHelper().isLoginUser(getProfile())) {
      setMoveToPurchaseInfoSuccessURL(getShippingInfoURL());
    } else {
      // not loggged in yet
      setMoveToPurchaseInfoSuccessURL(getLoginDuringCheckoutURL());
    }
  }

  /**
   * Directs to a gift message page if the order contains a gift special instruction and the message
   * hasn't been filled out.
   * @param pRequest - http request
   * @param pResponse - http response
   */
  protected void determineExpressCheckoutSuccessURL(DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) {
    StoreOrderImpl order = (StoreOrderImpl) getOrder();

    // Initialize success URL to confirmation page.
    setExpressCheckoutSuccessURL(getConfirmationURL());

    // If order has gift message, then show gifts and samples page so
    // user can update gift message.
    if (order.getContainsGiftMessage() || order.isShouldAddGiftNote()) {
      if (!order.getGiftMessagePopulated()) {
        if (isLoggingDebug()) {
          logDebug("User has a gift message that hasn't been filled out yet." + " Sending to gift message page.");
        }

        setExpressCheckoutSuccessURL(getGiftMessageUrl());
        pRequest.addQueryParameter("express", "true");
        return;
      }
    }
    
    // Choose ExpressShipping or login depending on login
    if (getCartHelper().isLoginUser(getProfile())) {
      setExpressCheckoutSuccessURL(getConfirmationURL());
    } else {
      // not loggged in yet
      setExpressCheckoutSuccessURL(getLoginDuringCheckoutURL());
      pRequest.addQueryParameter("express", "true");
    }
  }

  /**
   * Sets form values from request parameters if they are present on the request
   * and haven't already been set in the form handler. This allows add operations
   * to be triggered by a link instead of a form submission.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  
  vlogDebug("Passed giftListID is {0}.",  getGiftlistId() );
  vlogDebug("Passed giftlistItemId is {0}.",  getGiftlistItemId() );
  vlogDebug("Passed productId is {0}.",  getProductId() );
  vlogDebug("Passed quantity is {0}.",  getQuantity() );

    validateOrderQuantity(pRequest, pResponse);
    if (getFormError()) {
        return;
    }
    
    validateSelectedSKUs(pRequest, pResponse);
  }

  /**
   * Override to set the current transaction for rollback if there are form errors.
   * @param pRequest - http request
   * @param pResponse - http response
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  protected void addItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    super.addItemToOrder(pRequest, pResponse);

    if (getFormError()) {
      //mark the transaction for roll back
      getCartHelper().rollbackTransaction(getTransactionManager());
    }
  }
  
  //------------------------------------------
  // method:  handleAddItemToWishlist
  //------------------------------------------
  /**
   * Adds items to a wish list based on the AddCommerceItemInfo array. Each item in the
   * array is added to the profiles wish list.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleAddItemToWishlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    StoreGiftlistFormHandler giftListFormHandler = getStoreGiftlistFormHandler();
    giftListFormHandler.setUseWishlist(true);
    return addItemToList(pRequest, pResponse);
  }

  //------------------------------------------
  // method:  handleAddItemToGiftlist
  //------------------------------------------
  /**
   * Adds items to a gift list based on the AddCommerceItemInfo array. Each item in the
   * array is added to the selected gift list.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  public boolean handleAddItemToGiftlist(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException {
    StoreGiftlistFormHandler giftListFormHandler = getStoreGiftlistFormHandler();
    giftListFormHandler.setUseWishlist(false);
    return addItemToList(pRequest, pResponse);
  }
  
  /**
   * Adds an item to a gift/wish list
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if successful, false otherwise.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   * @exception CommerceException if there was an error with Commerce
   */
  protected boolean addItemToList(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException{
    //If any form errors found, redirect to error URL:
    if (!checkFormRedirect(null, getAddItemToGiftlistErrorURL(), pRequest, pResponse)) {
      return false;
    }
    
    validateQuantity(pRequest, pResponse);

    //If any form errors found, redirect to error URL:
    if (!checkFormRedirect(null, getAddItemToGiftlistErrorURL(), pRequest, pResponse)) {
      return false;
    }

    StoreGiftlistFormHandler giftListFormHandler = getStoreGiftlistFormHandler();
    // set quantity of items to add to gift list
    long quantity = 1;
    if (getItems()!= null){
      quantity = getItems()[0].getQuantity();
    }
    giftListFormHandler.setQuantity(quantity);
    
    // set gift list ID and success URL
    String giftListId = null;
    if(giftListFormHandler.isUseWishlist()) {
      giftListId = getAddItemToWishlist();
    }
    else{
      giftListId = getAddItemToGiftlist();
    }
    giftListFormHandler.setGiftlistId(giftListId);
    giftListFormHandler.setAddItemToGiftlistSuccessURL(getAddItemToGiftlistSuccessURLMap().get(giftListId));
    try {
      return giftListFormHandler.handleAddItemToGiftlist(pRequest, pResponse);
    } catch (CommerceException oce) {
      processException(oce, getCartHelper().MSG_ERROR_ADDING_TO_GIFTLIST, pRequest, pResponse);
    }
    
    return false;
  }
  
  /**
   * Called after all processing is done by the removeItemFromOrder method.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postRemoveItemFromOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    
    if (!getFormError()) {
      addRemoveGiftServices();
    }
  }
  
  /**
   * Override of base class behavior to use a locally defined variable
   * for storing the shipping group.
   * @param pShippingGroup - shipping group
   */
  public void setShippingGroup(ShippingGroup pShippingGroup) {
    mStoreShippingGroup = pShippingGroup;
  }

  /**
   * Override of base class behavior to  ensure a non-gift hardgood shipping group
   * is returned if a shipping group hasn't already been set.
   * @return shipping group
   */
  public ShippingGroup getShippingGroup() {
    if (mStoreShippingGroup != null) {
      return mStoreShippingGroup;
    }

    mStoreShippingGroup = ((StoreOrderTools) getCartHelper().getStoreOrderTools()).getShippingGroup(getOrder());

    return mStoreShippingGroup;
  }

  /**
   * Add item to order.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   *
   * @return true if succcess, false - otherwise
   *
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  public boolean handleAddItemToOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    String successUrl = getAddItemToOrderSuccessURL();
    String errorUrl = getAddItemToOrderErrorURL();

    //  If this is an AJAX RichCart request, then handle it as such, sending a JSON response          
    if (AjaxUtils.isAjaxRequest(pRequest)) {
      if (isLoggingDebug()) {
        logDebug("Handling AJAX AddToCart request");
      }

      // This request has been sent from a Javascript component in the browser that is expecting
      // a JSON response, so we have to make sure that's what we send it.
      // Use the ajax success/error URLs from this point onwards.       
      successUrl = getAjaxAddItemToOrderSuccessUrl();
      errorUrl = getAjaxAddItemToOrderErrorUrl();
      String ajaxAddToCartSuccessReqParam = (String)pRequest.getParameter("ajaxAddItemToOrderSuccessUrl");
      String ajaxAddToCartErrorReqParam = (String)pRequest.getParameter("ajaxAddItemToOrderErrorUrl");

      if (StringUtils.isBlank(successUrl) && !StringUtils.isBlank(ajaxAddToCartSuccessReqParam))
      {
        successUrl = ajaxAddToCartSuccessReqParam;
        setAjaxAddItemToOrderSuccessUrl(ajaxAddToCartSuccessReqParam);
      }
      if (StringUtils.isBlank(errorUrl) && !StringUtils.isBlank(ajaxAddToCartErrorReqParam))
      {
        errorUrl = ajaxAddToCartErrorReqParam;
        setAjaxAddItemToOrderErrorUrl(ajaxAddToCartErrorReqParam);
      }
    }

    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleAddItemToOrder";

    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod))) {
      Transaction tr = null;

      try {
        tr = ensureTransaction();

        if (getUserLocale() == null) {
          setUserLocale(getUserLocale(pRequest, pResponse));
        }

        //If any form errors found, redirect to error URL:
        if (!checkFormRedirect(null, errorUrl, pRequest, pResponse)) {
          return false;
        }
        
        // If the sku isnt available redirect to the email page. This is used
        // only by the non javascript picker
        if(getSkuUnavailableURL() != null){
          if(!isSkuAvailable(getSkuUnavailableURL(), pRequest, pResponse)){
            return false;
          }
        }

        synchronized (getOrder()) {
          preAddItemToOrder(pRequest, pResponse);

          //If any form errors found, redirect to error URL:
          if (!checkFormRedirect(null, errorUrl, pRequest, pResponse)) {
            return false;
          }

          addItemToOrder(pRequest, pResponse);

          //If any form errors found, redirect to error URL:
          if (!checkFormRedirect(null, errorUrl, pRequest, pResponse)) {
            return false;
          }

          postAddItemToOrder(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect(successUrl, errorUrl, pRequest, pResponse);
      } finally {
        if (tr != null) {
          commitTransaction(tr);
        }

        if (rrm != null) {
          rrm.removeRequestEntry(myHandleMethod);
        }
      }
    }

    return false;
  }
  
  /**
   * Used by the non-javascript picker to determine if a product is available. 
   * If we are unable to determine if its in stock assume it is.
   * 
   * @param pUnavailableURL The url to redirect to in the event a product is not
   * available
   * @return A boolean indicating whether or not a product is available
   */
  protected boolean isSkuAvailable(String pUnavailableURL,
                                   DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
  {   
    // get skuId that was set in noJsPickerLayout.jsp
    Object skuIdParam = getCatalogRefIds()[0];
    if(skuIdParam == null){
      if(isLoggingDebug()){
        logDebug("Cannot detemine if sku is available as sku is null");
      }
      return true;
    }
    
    // get productId
    String productId = getProductId();
    if(productId == null){
      if(isLoggingDebug()){
        logDebug("Cannot determine if sku " + skuIdParam + " is available as productId is null");
      }
      return true;
    }
    
    try{
      StoreInventoryManager invManager = getInventoryManager();

      // get product repository item
      Object productParam = invManager.getCatalogRefRepository().getItem(productId, PRODUCT);
      if((productParam == null) || !(productParam instanceof RepositoryItem)){
        if(isLoggingDebug()){
          logDebug("Cannot get the product from the repository. Product is " + productParam);
        }
        return true;
      }
            
      // if its in stock back orderable or preorderable its not unavailable
      int availability = invManager.queryAvailabilityStatus((RepositoryItem) productParam, (String) skuIdParam);
      if(availability == invManager.getAvailabilityStatusInStockValue() || 
         availability == invManager.getAvailabilityStatusBackorderableValue() ||
         availability == invManager.getAvailabilityStatusBackorderableValue())
      {
        return true;
      }
    }
    catch(Exception e){
      if(isLoggingError()){
        logError("Error determining if sku was in stock.", e);
      }
    }
    
    try{
      //update the unavailable url with a flag
      //pUnavailableURL = pUnavailableURL + SKU_UNAVAILABLE_PARAM + SKU_PARAM + skuIdParam;
      //redirect
      redirectOrForward(pRequest, pResponse, pUnavailableURL);
    }
    catch(Exception e){
      if(isLoggingError()){
        logError("Error redirecting to url " + pUnavailableURL, e);
      }
    }
    return false;
  }

  /**
   * This method checks if all items to be added into the shopping cart contain a reference to a SKU repository item.
   * If no items specified, this method walks through catalogRefIds references, and if no reference found,
   * creates a 'no sku' form exception.
   * If there are some items specified, this method walks through them and checks all references to SKU repotisoty item.
   * If one or more items has no reference to SKU, this method creates a 'no sku' form exception.
   * @param pRequest - current HTTP request.
   * @param pResponse - current HTTP response.
   * @throws ServletException - if something goes wrong.
   * @throws IOException - if unable to modify HTTP request or response.
   */
  protected void validateSelectedSKUs(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
  {
    if (getItems() == null || getItems().length == 0) // no items? then there should be specified catalogRefIds to be added to cart
    {
      if (getCatalogRefIds() == null || getCatalogRefIds().length == 0)
      {
        // we have an error here, no SKUs passed into handler
        addNoSkuFormException(pRequest, pResponse);
      }
    } else // there are some items to be added
    {
      for (AddCommerceItemInfo itemInfo: getItems())
      {
        if (itemInfo.getCatalogRefId() == null || itemInfo.getCatalogRefId().length() == 0)
        {
          addNoSkuFormException(pRequest, pResponse);
        }
      }
    }
  }
  
  /**
   * This method creates a 'no sku' form exception based on the skuType passed into form handler.
   * If skuType is 'clothing', the 'no sku' exception says 'select color and size'.
   * If skuType is 'furniture', the 'no sku' exception says 'select wood finish'.
   * If nothing specified in the skuType property, the 'no sku' exception says 'select sku'.
   * @param pRequest - current HTTP request.
   * @param pResponse - current HTTP response.
   * @throws ServletException - if something goes wrong
   * @throws IOException - if unable to modify HTTP request or response.
   */
  private void addNoSkuFormException(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws ServletException, IOException
  {
    if (CLOTHING_SKU_TYPE.equals(getSkuType()))
    {
      addFormException(new DropletException(formatUserMessage(MSG_SELECT_COLOR_SIZE, pRequest, pResponse)));
    } else if (FURNITURE_SKU_TYPE.equals(getSkuType()))
    {
      addFormException(new DropletException(formatUserMessage(MSG_SELECT_WOOD_FINISH, pRequest, pResponse)));
    } else
    {
      addFormException(new DropletException(formatUserMessage(MSG_SELECT_SKU, pRequest, pResponse)));
    }
  }

  /**
   * Validate quantity.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   *
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  protected void validateOrderQuantity(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    long quantity;
    AddCommerceItemInfo[] items = getItems();

    if (items != null) {
      long itemsSelected = 0;
      for (int i = 0; i < items.length; i++) {
        AddCommerceItemInfo item = (AddCommerceItemInfo) items[i];

        try {
          quantity = item.getQuantity();

          if (isQuantityValid(quantity, pRequest, pResponse)){
            itemsSelected += quantity;
          }
        } catch (NumberFormatException nfe) {
          addFormException(new DropletFormException(formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse),
              (String) null, MSG_INVALID_QUANTITY));
        }
      }
      if (itemsSelected == 0)
      {
        addFormException(new DropletException(formatUserMessage(MSG_NO_ITEMS_TO_ADD, pRequest, pResponse)));
      }
    }else{
      quantity = getQuantity();
      if (isQuantityValid(quantity, pRequest, pResponse)){
        if (quantity <= 0) {
          addFormException(new DropletFormException(formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse),
              (String) null, MSG_INVALID_QUANTITY));          
        }
      }
    }

    if (getFormError() && isLoggingDebug()) {
      logDebug("validateQuantity(): Quantity is less than -1");
    }

  }

  /**
   * Validate quantity.
   *
   * @param pRequest - http request
   * @param pResponse - http response
   *
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  protected void validateQuantity(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    long quantity;
    List items = getOrder().getCommerceItems();

    if (items != null) {
      for (int i = 0; i < items.size(); i++) {
        CommerceItem item = (CommerceItem) items.get(i);
        String commerceItemId = item.getId();
        String productDisplayName = (String)((RepositoryItem)item.getAuxiliaryData().getProductRef()).getPropertyValue("displayName");
        
        try {
          quantity = getQuantity(commerceItemId, pRequest, pResponse);
          
          if (mMinQuantity > -1 && quantity < mMinQuantity) {
            addFormException(new DropletFormException(formatUserMessage(MSG_ITEM_LESS_THAN_MIN_QUANTITY, productDisplayName, mMinQuantity, pRequest, pResponse),
                (String) null, MSG_LESS_THAN_MIN_QUANTITY));
          }
          else if (mMaxQuantity > -1 && quantity > mMaxQuantity) {
            addFormException(new DropletFormException(formatUserMessage(MSG_ITEM_MORE_THAN_MAX_QUANTITY, productDisplayName, mMaxQuantity, pRequest, pResponse),
                (String) null, MSG_MORE_THAN_MAX_QUANTITY));
          }
          else if (quantity < 0) {
            addFormException(new DropletFormException(formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse),
                (String) null, MSG_INVALID_QUANTITY));
          }
        } catch (NumberFormatException nfe) {
          addFormException(new DropletFormException(formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse),
              (String) null, MSG_INVALID_QUANTITY));
        }
      }
    }

    if (getFormError() && isLoggingDebug()) {
      logDebug("validateQuantity(): Quantity is less than -1");
    }
  }
  
  /**
   * Validates quantity and adds form exceptions if quantity is invalid.
   * @param pQuantity quantity to validate
   * @param pRequest - http request
   * @param pResponse - http response
   * @return true is valid, false otherwise
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  protected boolean isQuantityValid(long pQuantity, DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException{
    
    if (mMinQuantity > -1 && pQuantity < mMinQuantity) {
      addFormException(new DropletFormException(formatUserMessage(MSG_LESS_THAN_MIN_QUANTITY, mMinQuantity, pRequest, pResponse),
          (String) null, MSG_LESS_THAN_MIN_QUANTITY));
      return false;
    }
    else if (mMaxQuantity > -1 && pQuantity > mMaxQuantity) {
      addFormException(new DropletFormException(formatUserMessage(MSG_MORE_THAN_MAX_QUANTITY, mMaxQuantity, pRequest, pResponse),
          (String) null, MSG_MORE_THAN_MAX_QUANTITY));
      return false;
    }
    else if (pQuantity < 0) {
      addFormException(new DropletFormException(formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse),
          (String) null, MSG_INVALID_QUANTITY));
      return false;
    }
    return true;
  }
  
  /**
   * Determines an overall quantity of commerce items in the order.
   * 
   * @param pRequest a Dynamo HTTP request 
   * @param pResponse a Dynamo HTTP response
   * @return sum of commerce items' quantities
   */
  private long evalOverallItemsQuantity(DynamoHttpServletRequest pRequest, 
      DynamoHttpServletResponse pResponse) 
    throws ServletException, IOException {
    
    long overallQuantity = 0;
    List items = getOrder().getCommerceItems();

    if(items != null) {
      for(Object item : items) {
        overallQuantity += getQuantity(((CommerceItem)item).getId(), pRequest, pResponse);
      }
    }
    
    return overallQuantity;
  }
}


