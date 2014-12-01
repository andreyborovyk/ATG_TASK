/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2010 Art Technology Group, Inc.
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

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.transaction.Transaction;

import atg.commerce.CommerceException;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.purchase.CommerceItemShippingInfo;
import atg.commerce.order.purchase.PurchaseUserMessage;
import atg.commerce.order.purchase.ShippingGroupFormHandler;
import atg.commerce.order.purchase.ShippingGroupMapContainer;
import atg.commerce.pricing.PricingException;
import atg.commerce.profile.CommerceProfileTools;
import atg.commerce.util.PlaceUtils;
import atg.commerce.util.PlaceList.Place;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.nucleus.ServiceMap;
import atg.projects.store.droplet.CountryRestrictionsDroplet;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.profile.StorePropertyManager;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import java.util.TreeMap;


/**
 * Form Handler for handling shipping related checkout processes.
 *
 * @author ATG
 * @version $Version$
 */
public class ShippingInfoFormHandler extends ShippingGroupFormHandler {

    /** Class version string. */
  public static String CLASS_VERSION =
      "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/order/purchase/ShippingInfoFormHandler.java#3 $$Change: 635816 $";

  private static final String MSG_NO_SHIPPING_ADDRESS_SELECTED = "noShippingAddressSelected";

  protected static String NICKNAME_SEPARATOR = ";;";

  public static final String COUNTRY_KEY_PREFIX = "CountryCode.";

  public static final String COUNTRY_STATE_RESOURCES = "atg.commerce.util.CountryStateResources";


  /**
   * Error Message keys
   */
  protected static String MSG_ERROR_UPDATE_ORDER = "errorUpdatingOrder";
  protected static String MSG_ERROR_MOVE_TO_BILLING = "errorWithShippingInfo";


  /**
   * property: Refernce to the ShippingProcessHelper component
   */
  private StoreShippingProcessHelper mShippingHelper;

  public CheckoutProgressStates getCheckoutProgressStates()
  {
    return mCheckoutProgressStates;
  }

  public void setCheckoutProgressStates(CheckoutProgressStates pCheckoutProgressStates)
  {
    mCheckoutProgressStates = pCheckoutProgressStates;
  }

  /**
   * @return the Shipping Helper component.
   */
  public StoreShippingProcessHelper getShippingHelper() {
    return mShippingHelper;
  }

  /**
   * @param pShippingHelper the shipping helper component to set.
   */
  public void setShippingHelper(StoreShippingProcessHelper pShippingHelper) {
    mShippingHelper = pShippingHelper;
  }

  /**
   * property: Reference to the CheckoutOptionSelections component
   */
  private CheckoutOptionSelections mCheckoutOptionSelections;

  private CheckoutProgressStates mCheckoutProgressStates;

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
  // property: ShippingGroupInitializers
  //---------------------------------------------------------------------------
  ServiceMap mShippingGroupInitializers;

  /**
   * Set the ShippingGroupInitializers property.
   * @param pShippingGroupInitializers a <code>ServiceMap</code> value
   */
  public void setShippingGroupInitializers(ServiceMap pShippingGroupInitializers) {
    mShippingGroupInitializers = pShippingGroupInitializers;
  }

  /**
   * Return the ShippingGroupInitializers property.
   * @return a <code>ServiceMap</code> value
   */
  public ServiceMap getShippingGroupInitializers() {
    return mShippingGroupInitializers;
  }

  /**
   * property: default shipping method
   */
  private String mDefaultShippingMethod = "Ground";

  /**
   * @return the default shipping method name.
   */
  public String getDefaultShippingMethod() {
    return mDefaultShippingMethod;
  }

  /**
   * @param pDefaultShippingMethod -
   * the default shipping method to set.
   */
  public void setDefaultShippingMethod(String pDefaultShippingMethod) {
    mDefaultShippingMethod = pDefaultShippingMethod;
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
   * property: shipping method to use for current order
   */
  private String mShippingMethod;

  /**
   * @return the shipping method.
   */
  public String getShippingMethod() {
    return mShippingMethod;
  }

  /**
   * @param pShippingMethod - the shipping method to set.
   */
  public void setShippingMethod(String pShippingMethod) {
    mShippingMethod = pShippingMethod;
  }

  /**
   * property: flag indicating shigle shipping checkout
   */
  private boolean mSingleShippingGroupCheckout = true;

  /**
   * @return true if single shipping group should be checkouted,
   * false - otherwise.
   */
  public boolean isSingleShippingGroupCheckout() {
    return mSingleShippingGroupCheckout;
  }

  /**
   * @param pSingleShippingGroupCheckout -
   * true if single shipping group should be checkouted,
   * false - otherwise.
   */
  public void setSingleShippingGroupCheckout(boolean pSingleShippingGroupCheckout) {
    mSingleShippingGroupCheckout = pSingleShippingGroupCheckout;
  }

  /**
   * property: flag indicating use of shipping address as billing address as well
   */
  private boolean mUseShippingForBilling;

  /**
   * @return true if shipping address should be used for
   * billing, false - otherwise.
   */
  public boolean getUseShippingForBilling() {
    return mUseShippingForBilling;
  }

  /**
   * @param pUseShippingForBilling - true if shipping address
   * should be used for billing, false otherwise.
   */
  public void setUseShippingForBilling(boolean pUseShippingForBilling) {
    mUseShippingForBilling = pUseShippingForBilling;
  }

  /**
   * property: flag indicating if new shipping address needs to be saved or not
   */
  private boolean mSaveShippingAddress = false;

  /**
   * @return true if shipping address should be saved,
   * false - otherwise.
  */
  public boolean getSaveShippingAddress() {
    return mSaveShippingAddress;
  }

  /**
   * @param pSaveShippingAddress - true if shipping address
   * should be saved, false - otherwise.
   */
  public void setSaveShippingAddress(boolean pSaveShippingAddress) {
    mSaveShippingAddress = pSaveShippingAddress;
  }

  /**
   * property: flag indicating whether shipping will be performed to the new address or not
   */
  private boolean mShipToNewAddress = false;

  /**
   * @return true if shipping will be performed to the new address,
   * false - otherwise.
  */
  public boolean getShipToNewAddress() {
    return mShipToNewAddress;
  }

  /**
   * @param pShipToNewAddress - true if shipping will be performed to the new address,
   * false - otherwise.
   */
  public void setShipToNewAddress(boolean pShipToNewAddress) {
    mShipToNewAddress = pShipToNewAddress;
  }

  /**
   * property: Nickname for selected shipping address
   */
  private String mShipToAddressName;

  /**
   * @return shipping address.
   */
  public String getShipToAddressName() {
    return mShipToAddressName;
  }

  /**
   * @param pShipToAddressName - shipping address.
   */
  public void setShipToAddressName(String pShipToAddressName) {
    mShipToAddressName = pShipToAddressName;
  }

  /**
   * property: Nickname for new shipping address
   */
  private String mNewShipToAddressName;

  /**
   * @return new shipping address.
   */
  public String getNewShipToAddressName() {
    return mNewShipToAddressName;
  }

  /**
   * @param pNewShipToAddressName - new shipping address.
   */
  public void setNewShipToAddressName(String pNewShipToAddressName) {
    mNewShipToAddressName = pNewShipToAddressName;

    if (mNewShipToAddressName != null) {
      mNewShipToAddressName = mNewShipToAddressName.trim();
    }
  }

  /**
   * property: Nickname of shipping address being modified
   */
  String mEditShippingAddressNickName;

  /**
   * @return the edit shipping address nickname.
   */
  public String getEditShippingAddressNickName() {
    return mEditShippingAddressNickName;
  }

  /**
   * @param pEditShippingAddressNickName -
   * the edit shipping address nickname to set.
   */
  public void setEditShippingAddressNickName(String pEditShippingAddressNickName) {
    mEditShippingAddressNickName = pEditShippingAddressNickName;
  }

  /**
   * property: Nickname of shipping address being removed
   */
  String mRemoveShippingAddressNickName;

  /**
   * @return the remove shipping address nickname.
   */
  public String getRemoveShippingAddressNickName() {
    return mRemoveShippingAddressNickName;
  }

  /**
   * @param pRemoveShippingAddressNickName -
   * the remove shipping address nickname to set.
   */
  public void setRemoveShippingAddressNickName(String pRemoveShippingAddressNickName) {
    mRemoveShippingAddressNickName = pRemoveShippingAddressNickName;
  }

  /**
   * property: New Nickname of shipping address
   */
  String mShippingAddressNewNickName;

  /**
   * @return the shipping address new nickname.
   */
  public String getShippingAddressNewNickName() {
    return mShippingAddressNewNickName;
  }

  /**
   * @param pShippingAddressNewNickName -
   * the shipping address new nickname to set.
   */
  public void setShippingAddressNewNickName(String pShippingAddressNewNickName) {
    mShippingAddressNewNickName = pShippingAddressNewNickName;
  }

  /**
   * property: shipping address
   */
  private Address mAddress = new ContactInfo();

  /**
   * @return the address.
   */
  public Address getAddress() {
    return mAddress;
  }

  /**
   * @param pAddress - the address to set.
   */
  public void setAddress(Address pAddress) {
    mAddress = pAddress;
  }

  /**
   * property: address to be modified
   */
  private Address mEditAddress = new ContactInfo();

  /**
   * @return the edit address.
   */
  public Address getEditAddress() {
    return mEditAddress;
  }

  /**
   * @param pEditAddress - the edit address to set.
   */
  public void setEditAddress(Address pEditAddress) {
    mEditAddress = pEditAddress;
  }

  /**
   * property: Error URL for Move To Billing process
   */
  private String mMoveToBillingErrorURL;

  /**
   * @return move to billing error redirect URL.
   */
  public String getMoveToBillingErrorURL() {
    return mMoveToBillingErrorURL;
  }

  /**
   * @param pMoveToBillingErrorURL - move to billing
   * error redirect URL.
   */
  public void setMoveToBillingErrorURL(String pMoveToBillingErrorURL) {
    mMoveToBillingErrorURL = pMoveToBillingErrorURL;
  }

  /**
   * property: Success URL for Move To Billing process
   */
  private String mMoveToBillingSuccessURL;

  /**
   * @return move to billing success redirect URL.
   */
  public String getMoveToBillingSuccessURL() {
    return mMoveToBillingSuccessURL;
  }

  /**
   * @param pMoveToBillingSuccessURL - move to billing
   * success redirect URL.
   */
  public void setMoveToBillingSuccessURL(String pMoveToBillingSuccessURL) {
    mMoveToBillingSuccessURL = pMoveToBillingSuccessURL;
  }

  /**
   * property: Success URL for Add Shipping Address
   */
  private String mAddShippingAddressSuccessURL;

  /**
   * @return the add shipping address success redirect URL.
   */
  public String getAddShippingAddressSuccessURL() {
    return mAddShippingAddressSuccessURL;
  }

  /**
   * @param pAddShippingAddressSuccessURL -
   * the add shipping address success redirect URL to set.
   */
  public void setAddShippingAddressSuccessURL(String pAddShippingAddressSuccessURL) {
    mAddShippingAddressSuccessURL = pAddShippingAddressSuccessURL;
  }

  /**
   * property: Error URL for Add Shipping Address
   */
  private String mAddShippingAddressErrorURL;

  /**
   * @return the add shipping address error redirect URL.
   */
  public String getAddShippingAddressErrorURL() {
    return mAddShippingAddressErrorURL;
  }

  /**
   * @param pAddShippingAddressErrorURL -
   * the add shipping address error redirect URL to set.
   */
  public void setAddShippingAddressErrorURL(String pAddShippingAddressErrorURL) {
    mAddShippingAddressErrorURL = pAddShippingAddressErrorURL;
  }

  /**
   * property: Success URL for Edit Shipping Address
   */
  String mEditShippingAddressSuccessURL;

 /**
   * @return the edit shipping address success redirect URL.
   */
  public String getEditShippingAddressSuccessURL() {
    return mEditShippingAddressSuccessURL;
  }

  /**
   * @param pEditShippingAddressSuccessURL -
   * the edit shipping address success redirect URL to set.
   */
  public void setEditShippingAddressSuccessURL(String pEditShippingAddressSuccessURL) {
    mEditShippingAddressSuccessURL = pEditShippingAddressSuccessURL;
  }

  /**
   * property: Error URL for Edit Shipping Address
   */
  String mEditShippingAddressErrorURL;

  /**
   * @return the edit shipping address error URL.
   */
  public String getEditShippingAddressErrorURL() {
    return mEditShippingAddressErrorURL;
  }

  /**
   * @param pEditShippingAddressErrorURL -
   * the edit shipping address error redirect URL to set.
   */
  public void setEditShippingAddressErrorURL(String pEditShippingAddressErrorURL) {
    mEditShippingAddressErrorURL = pEditShippingAddressErrorURL;
  }

  /**
   * property: Success URL for Remove Shipping Address
   */
  String mRemoveShippingAddressSuccessURL;

 /**
   * @return the remove shipping address success redirect URL.
   */
  public String getRemoveShippingAddressSuccessURL() {
    return mRemoveShippingAddressSuccessURL;
  }

  /**
   * @param pRemoveShippingAddressSuccessURL -
   * the remove shipping address success redirect URL to set.
   */
  public void setRemoveShippingAddressSuccessURL(String pRemoveShippingAddressSuccessURL) {
    mRemoveShippingAddressSuccessURL = pRemoveShippingAddressSuccessURL;
  }

  /**
   * property: Error URL for Remove Shipping Address
   */
  String mRemoveShippingAddressErrorURL;

  /**
   * @return the remove shipping address error URL.
   */
  public String getRemoveShippingAddressErrorURL() {
    return mRemoveShippingAddressErrorURL;
  }

  /**
   * @param pRemoveShippingAddressErrorURL -
   * the remove shipping address error redirect URL to set.
   */
  public void setRemoveShippingAddressErrorURL(String pRemoveShippingAddressErrorURL) {
    mRemoveShippingAddressErrorURL = pRemoveShippingAddressErrorURL;
  }

  /**
   * property: Success URL for Add Shipping Address and Move to Multiple Shipping
   */
  private String mAddAddressAndMoveToMultipleShippingSuccessURL;

  /**
   * @return the Add Shipping Address and Move to Multiple Shipping success redirect URL.
   */
  public String getAddAddressAndMoveToMultipleShippingSuccessURL() {
    return mAddAddressAndMoveToMultipleShippingSuccessURL;
  }

  /**
   * @param pAddAddressAndMoveToMultipleShippingSuccessURL -
   * the Add Shipping Address and Move to Multiple Shipping success redirect URL to set.
   */
  public void setAddAddressAndMoveToMultipleShippingSuccessURL(String pAddAddressAndMoveToMultipleShippingSuccessURL) {
    mAddAddressAndMoveToMultipleShippingSuccessURL = pAddAddressAndMoveToMultipleShippingSuccessURL;
  }

  /**
   * property: Error URL for Add Shipping Address and Move to Multiple Shipping
   */
  private String mAddAddressAndMoveToMultipleShippingErrorURL;

  /**
   * @return the Add Shipping Address and Move to Multiple Shipping error redirect URL.
   */
  public String getAddAddressAndMoveToMultipleShippingErrorURL() {
    return mAddAddressAndMoveToMultipleShippingErrorURL;
  }

  /**
   * @param pAddAddressAndMoveToMultipleShippingErrorURL -
   * the Add Shipping Address and Move to Multiple Shipping error redirect URL to set.
   */
  public void setAddAddressAndMoveToMultipleShippingErrorURL(String pAddAddressAndMoveToMultipleShippingErrorURL) {
    mAddAddressAndMoveToMultipleShippingErrorURL = pAddAddressAndMoveToMultipleShippingErrorURL;
  }

  /**
   * property: Success URL for Update Shipping method process
   */
  String mUpdateShippingMethodSuccessURL;

  /**
   * @return the update shipping method success redirect URL.
   */
  public String getUpdateShippingMethodSuccessURL() {
    return mUpdateShippingMethodSuccessURL;
  }

  /**
   * @param pUpdateShippingMethodSuccessURL -
   * the update shipping method success redirect URL to set.
   */
  public void setUpdateShippingMethodSuccessURL(String pUpdateShippingMethodSuccessURL) {
    mUpdateShippingMethodSuccessURL = pUpdateShippingMethodSuccessURL;
  }

  /**
   * property: Error URL for Update Shipping method process
   */
  String mUpdateShippingMethodErrorURL;

  /**
   * @return the update shipping method error redirect URL.
   */
  public String getUpdateShippingMethodErrorURL() {
    return mUpdateShippingMethodErrorURL;
  }

  /**
   * @param pUpdateShippingMethodErrorURL -
   * the update shipping method error redirect URL to set.
   */
  public void setUpdateShippingMethodErrorURL(String pUpdateShippingMethodErrorURL) {
    mUpdateShippingMethodErrorURL = pUpdateShippingMethodErrorURL;
  }

  /**
   * property: Gift shipping groups.
   */
  protected List mGiftShippingGroups = null;

  /**
   * @return a list of all shipping groups that contain gifts.
   */
  public List getGiftShippingGroups() {
    if (mGiftShippingGroups == null) {
      mGiftShippingGroups = getShippingHelper().getGiftShippingGroups(getOrder());
    }
    return mGiftShippingGroups;
  }

  /**
   * Get the List of all the CommerceItemShippingInfos for hardgoods
   * from the CommerceItemShippingInfoMap. If a CommerceItemShippingInfo
   * has no shipping group, assume the item represents hardgoods.
   *
   * @return a <code>List</code> value - All hardgood commerce item shipping information.
   */
  public List getAllHardgoodCommerceItemShippingInfos() {
    if (mAllHardgoodCommerceItemShippingInfos == null) {
      mAllHardgoodCommerceItemShippingInfos = getShippingHelper().getAllHardgoodCommerceItemShippingInfos(
          getShippingGroupMapContainer(), getCommerceItemShippingInfoContainer());
    }
    return mAllHardgoodCommerceItemShippingInfos;
  }

  /**
   * Determines if the total quantity of all non-gift hardgood items is more than one.
   * @return true if the the non-gift hg item quantity is more than one.
   */
  public boolean isMultipleNonGiftHardgoodItems() {
    return getShippingGroupManager().isMultipleNonGiftHardgoodItems(getOrder());
  }


  /**
   * Override handleCancel to clear form exceptions so that error message about
   * missing required fields is not displayed when a shopper hits "Cancel"
   */
  public boolean handleCancel(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    resetFormExceptions();
    return super.handleCancel(pRequest, pResponse);
  }

  /**
   * This handler method will validate new shipping address and apply the shipping groups to
   * the order.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleCreateAndMoveToBilling(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
  throws ServletException, IOException {
    setShipToNewAddress(true);

    return handleMoveToBilling(pRequest, pResponse);
  }

  /**
   * This handler method will validate shipping address and apply the shipping groups to
   * the order.
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
  public boolean handleMoveToBilling(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getMoveToBillingErrorURL(), pRequest, pResponse)) {
        return false;
      }

      synchronized (getOrder()) {
        try {
          preMoveToBilling(pRequest, pResponse);

          if (getFormError()) {
            if (isLoggingDebug()) {
              logDebug("Redirecting due to form error in preMoveToBilling.");
            }

            return checkFormRedirect(null, getMoveToBillingErrorURL(), pRequest, pResponse);
          }

          moveToBilling(pRequest, pResponse);

          if (getFormError()) {
            if (isLoggingDebug()) {
              logDebug("Redirecting due to form error in moveToBilling");
            }

            return checkFormRedirect(null, getMoveToBillingErrorURL(), pRequest, pResponse);
          }

          runProcessValidateShippingGroups(getOrder(), getUserPricingModels(), getUserLocale(pRequest, pResponse),
            getProfile(), null);
        } catch (Exception exc) {
          if (isLoggingDebug()) {
            logDebug("Resource bundle being used: " + getResourceBundleName());
          }

          processException(exc, exc.getMessage(), pRequest, pResponse);
        }

        if (getFormError()) {
          if (isLoggingDebug()) {
            logDebug("Redirecting due to form error in runProcessValidateShippingGroups");
          }

          return checkFormRedirect(null, getMoveToBillingErrorURL(), pRequest, pResponse);
        }

        postMoveToBilling(pRequest, pResponse);

        try {
          getOrderManager().updateOrder(getOrder());
        } catch (Exception exc) {
          processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        }
      } // synchronized

      //always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect(getMoveToBillingSuccessURL(), getMoveToBillingErrorURL(), pRequest, pResponse);
    } finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  private String mCouponCode;

  /**
   * @return a coupon code to be claimed
   */
  public String getCouponCode() {
    return mCouponCode;
  }

  public void setCouponCode(String pCouponCode) {
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
  * This method validates the user inputs for the Move To Billing process
  * @param pRequest a <code>DynamoHttpServletRequest</code> value
  * @param pResponse a <code>DynamoHttpServletResponse</code> value
  * @exception ServletException if an error occurs
  * @exception IOException if an error occurs
  */
  public void preMoveToBilling(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    tenderCoupon(pRequest, pResponse);

    if (getFormError()) {
      return;
    }

    if (isSingleShippingGroupCheckout()) {
      if (getShipToNewAddress()) {
        preShipToNewAddress(pRequest, pResponse);
      }
      else {
        preShipToProfileAddress(pRequest, pResponse);
      }
    }
    else {
      preShipToMultipleAddress(pRequest, pResponse);
    }

    preSetupGiftShippingDetails(pRequest, pResponse);
  }

  /**
   * Applies the data in the CommerceItemShippingInfoContainer and ShippingGroupMapContainer to the
   * order.
   * @see ShippingGroupFormHandler#applyShippingGroups(DynamoHttpServletRequest, DynamoHttpServletResponse)
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void moveToBilling(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (isSingleShippingGroupCheckout()) {
      if (getShipToNewAddress()) {
        shipToNewAddress(pRequest, pResponse);
      }
      else {
        shipToProfileAddress(pRequest, pResponse);
      }
    }
    else {
      shipToMultipleAddress(pRequest, pResponse);
    }

    if (getFormError()) {
      return;
    }

    // after applying shipping groups to the order we need to clear the cached list
    // that holds gift shipping groups so that it will be updated
    // during the next call to getGiftShippingGroups()
    mGiftShippingGroups = null;

    setupGiftShippingDetails(pRequest, pResponse);

    if (getFormError()) {
      return;
    }
  }

  /**
  * This method will reprice the order to catch address problems through CyberSource.
  * <p>Initializes the billing address from the shipping address if the user selected
  * that option.
  * <p>Saves addresses in the profile, if the user selected that option.
  *
  * @param pRequest a <code>DynamoHttpServletRequest</code> value
  * @param pResponse a <code>DynamoHttpServletResponse</code> value
  * @exception ServletException if an error occurs
  * @exception IOException if an error occurs
  */
  public void postMoveToBilling(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (getFormError()) {
      return;
    }

    //reprice catches problems reported by CyberSource
    repriceOrder(pRequest, pResponse);

    if (getFormError()) {
      return;
    }

    if (isSingleShippingGroupCheckout()) {
      if (getShipToNewAddress()) {
        postShipToNewAddress(pRequest, pResponse);
      }
      else {
        postShipToProfileAddress(pRequest, pResponse);
      }
    }
    else {
      postShipToMultipleAddress(pRequest, pResponse);
    }

    postSetupGiftShippingDetails(pRequest, pResponse);

    if (mCheckoutProgressStates != null && !getFormError())
    {
      mCheckoutProgressStates.setCurrentLevel(CheckoutProgressStates.DEFAULT_STATES.BILLING.toString());
    }
  }


  /**
   * This method provides inputa data validations for a profile shipping address selected by shopper
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void preShipToProfileAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

    if (isAnyNonGiftHardgoodShippingGroups()) {

      String shippingMethod = getShippingMethod();
      ShippingGroupMapContainer sgmc = getShippingGroupMapContainer();

      if (isSingleShippingGroupCheckout()) {

        Address address = getAddress();
        String addressName = getShipToAddressName();

        if (StringUtils.isEmpty(addressName))
        {
          addFormException(new DropletException(formatUserMessage(MSG_NO_SHIPPING_ADDRESS_SELECTED, pRequest, pResponse)));
          return;
        }

          // The shipping group for the selected address should already be in the map.
          HardgoodShippingGroup hgsg = (HardgoodShippingGroup) sgmc.getShippingGroup(addressName);
          address = hgsg.getShippingAddress();
          validateShippingAddress(address, pRequest, pResponse);

          // Make sure user isn't trying to Express ship to AK, etc
          validateShippingMethod(address, shippingMethod, pRequest, pResponse);

          if (getFormError()) {
            return;
          }

          validateShippingRestrictions(pRequest, pResponse);
      } //end if single sg checkout
    } // end if any non gift hgsg
  }


  /**
   * Setup single shipping details for shipping to profile addresses
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void shipToProfileAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

    if (!checkFormRedirect(null, getMoveToBillingErrorURL(), pRequest, pResponse)) {
      return;
    }

    StoreShippingProcessHelper shippingHelper = getShippingHelper();

    String shippingMethod = getShippingMethod();
    String addressName = null;

    if (isSingleShippingGroupCheckout()) {
      addressName = getShipToAddressName();

      // update the commerce item shipping infos for all hardgoods to point to the selected shipping group and method
      shippingHelper.changeShippingGroupForCommerceItemShippingInfos(
          getAllHardgoodCommerceItemShippingInfos(), addressName, shippingMethod);

      if (getFormError()) {
        return;
      }

      applyShippingGroups(pRequest, pResponse);
    }
  }

  /**
   * This method initializes the billing address from the shipping address if the user selected
   * that option. Saves addresses in the profile, if the user selected that option.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void postShipToProfileAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

    //set the profile's default shipping method if it isn't already set.
    getShippingHelper().saveDefaultShippingMethod(getProfile(), getShippingMethod());

    if (isSingleShippingGroupCheckout()) {
      // If user checked "use shipping for billing", copy address to payment group,
      // and set flag to prefill form fields on billing page.
      if (getUseShippingForBilling()) {
        getCheckoutOptionSelections().setPrefillBillingAddress(true);
        getShippingHelper().copyShippingToBilling(getOrder(), getAddress());
      }
    }
  }


  /**
   * This method performs input data validations for multiple shipping addresses specified by shopper
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void preShipToMultipleAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

    if (isAnyNonGiftHardgoodShippingGroups()) {
      if (!isSingleShippingGroupCheckout()) {
    	
      	// Validate the Addresses
      	validateMultiShippingAddresses(pRequest, pResponse);
        
        if (getFormError()) {
            return;
        }

        validateShippingMethodForContainerShippingGroups(pRequest, pResponse);

        if (getFormError()) {
          return;
        }

        validateShippingRestrictions(pRequest, pResponse);
      } // end multiple sg checkout
    } // end if any non gift hgsg
  }
  
  
  /**
   * Validate the shipping addresses on the multi shipping page
   * 
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void validateMultiShippingAddresses(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    //Retrieve the collection of all hardgood shipping groups referenced by commerce item infos.
    Collection hardgoodshippingGroups =
      getShippingHelper().getUniqueHardgoodShippingGroups(getShippingGroupMapContainer(),
                                                          getCommerceItemShippingInfoContainer());

    //iterator through them and verify their addresses
    Iterator sgerator = hardgoodshippingGroups.iterator();
    HardgoodShippingGroup hgsg;

    while (sgerator.hasNext()) {
      hgsg = (HardgoodShippingGroup) sgerator.next();
      validateShippingAddress(hgsg.getShippingAddress(), pRequest, pResponse);
    }
  }


  /**
   * Setup multi shipping details for shipping to profile addresses
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void shipToMultipleAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

    if (!checkFormRedirect(null, getMoveToBillingErrorURL(), pRequest, pResponse)) {
      return;
    }

    StoreShippingProcessHelper shippingHelper = getShippingHelper();

    if (!isSingleShippingGroupCheckout()) {
      // put shipping addresses with different shipping methods into separate
      // shipping groups
      try {
        splitShippingGroupsByMethod(pRequest, pResponse);
      } catch (CommerceException ex) {

        processException(ex, ex.getMessage(), pRequest, pResponse);
      }

      if (getFormError()) {
        return;
      }

      //if there are giftwrap items in the container, we must ensure they are in
      //one of the shipping groups containing the rest of the hardgood items
      shippingHelper.setGiftWrapItemShippingGroupInfos(getAllHardgoodCommerceItemShippingInfos());

      if (getFormError()) {
        return;
      }

      applyShippingGroups(pRequest, pResponse);
    }
  }

  /**
   * Creates separate shipping groups for the same shipping addresses but
   * with different shipping methods.
   * The method loops through all CISIs and finds items that have the same shipping
   * addresses but different shipping methods and if so creates separate shipping
   * groups for them. As a result all shipping groups that are referenced in CISIs will
   * have only one shipping method associated with them.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @throws CommerceException
   */
  protected void splitShippingGroupsByMethod(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws CommerceException{

    ShippingGroupManager sgm = getShippingGroupManager();
    // helper map to store ship group nickname to ship method
    Map<String, String> shipGroupToMethod = new HashMap<String, String>();
    // helper map to store 'ship group nickname' + 'shipping method' combination to shipping group
    Map<String, ShippingGroup> sgMethodToShipGroup = new HashMap<String, ShippingGroup>();

    Iterator iter = getAllHardgoodCommerceItemShippingInfos().iterator();

    //loop through all CISIs to find shipping groups with the same address
    // but different shipping methods
    while (iter.hasNext()){
      CommerceItemShippingInfo cisi = (CommerceItemShippingInfo) iter.next();
      String shipGroupName = cisi.getShippingGroupName();
      String shippingMethod = cisi.getShippingMethod();
      String shipGroupMethodKey = (shipGroupName + NICKNAME_SEPARATOR + shippingMethod);

      // Get shipping group from container for this CISI
      HardgoodShippingGroup shippingGroup =
        (HardgoodShippingGroup)getShippingGroupMapContainer().getShippingGroup(shipGroupName);

      // check if it's the first appearance of this shipping group
      if (shipGroupToMethod.containsKey(shipGroupName)){
        // there was already CISI with the same shipping group
        // check if the shipping method is the same as in previous CISI
        if (!sgMethodToShipGroup.containsKey(shipGroupMethodKey)){
          // shipping method is not the same as in previous CISI with this shipping address
          // clone shipping group and add it to the ShippingGroupMapContainer

          ShippingGroup clonedShipGroup = sgm.cloneShippingGroup(shippingGroup);

          //put it to the shipping group container and update CISI with the new shipping group
          getShippingGroupMapContainer().addShippingGroup(shipGroupMethodKey, clonedShipGroup);
          cisi.setShippingGroupName(shipGroupMethodKey);
        }else{
          // There was already such a combination of shipping group and shipping method
          // Update shipping group of this CISI with the one that contains the same shipping method
          // if needed
          if (!shipGroupToMethod.get(shipGroupName).equals(shippingMethod)){
            // update shipping group of CISI with the one that contains the same shipping method
            cisi.setShippingGroupName(shipGroupMethodKey);
          }
        }

      }else{
        //it's first occurrence of this shipping group
        //add corresponding ship group + method combination to shipGroupToMethod map
        shipGroupToMethod.put(shipGroupName, shippingMethod);
        sgMethodToShipGroup.put(shipGroupMethodKey, shippingGroup);
      }
    }

    //clear helper maps
    shipGroupToMethod.clear();
    sgMethodToShipGroup.clear();
  }

  /**
   * Sets the profile default shipping method if not set already
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void postShipToMultipleAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

    //set the profile's default shipping method if it isn't already set.
    getShippingHelper().saveDefaultShippingMethod(getProfile(), getShippingMethod());
  }


  /**
   * Performs input data validations for new shipping address specified by shopper
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void preShipToNewAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
              throws ServletException, IOException {
    if (isAnyHardgoodShippingGroups()) {
      //if we are in a single shipping group checkout then the user has the option of
      //specifying a new shipping address on the form.
      if (isSingleShippingGroupCheckout()) {

        CheckoutOptionSelections checkoutOptionSelections = getCheckoutOptionSelections();
        String shippingMethod = getShippingMethod();
        String storedAddress = getShipToAddressName();
        String addressName = getNewShipToAddressName();
        Address address = getAddress();

        // if user not using address-book address, then set prefill to true
        checkoutOptionSelections.setPrefillShippingAddress(true);

        validateShippingAddress(address, pRequest, pResponse);

        // Make sure user isn't trying to Express ship to AK, etc
        validateShippingMethod(address, shippingMethod, pRequest, pResponse);

        // Generate unique default address name if none supplied and
        // validation checks succeeded
        if (StringUtils.isBlank(addressName)
            && !getFormError()) {
          /*addressName =
                getShippingHelper().generateNewShipToAddressNickName(address, addressName, getShippingGroupMapContainer());*/
          CommerceProfileTools profileTools = getShippingHelper().getStoreOrderTools().getProfileTools();
          addressName = profileTools.getUniqueShippingAddressNickname(address, getProfile(), addressName);
          setNewShipToAddressName(addressName);
        }

        //validate nickname
        if (getSaveShippingAddress()) {
          validateAddressNickname(addressName, pRequest, pResponse);
        }
      } //end if single sg checkout
    }
  }

  /**
   * Setup single shipping details for shipping to a new address
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void shipToNewAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
          throws ServletException, IOException {

    if (!checkFormRedirect(null, getMoveToBillingErrorURL(), pRequest, pResponse)) {
      return;
    }
    if (isAnyHardgoodShippingGroups()) {
      //if we are in a single shipping group checkout then the user has the option of
      //specifying a new shipping address on the form.
      if (isSingleShippingGroupCheckout()) {

          String oldShippingGroupName = null;

          try {
            //create a new shipping group with the new address and put the new shipping group in the container
            oldShippingGroupName = getShippingHelper().findOrAddShippingAddress(getProfile(), getNewShipToAddressName(), getAddress(),
                                                   getShippingGroupMapContainer(), getSaveShippingAddress());
          }
          catch (StorePurchaseProcessException bppe) {
            String msg = ResourceUtils.getMsgResource(bppe.getMessage(), getResourceBundleName(), getResourceBundle(getUserLocale(pRequest, pResponse)));
            addFormException(new DropletFormException(msg, "", bppe.getMessage()));
            return;
          }
          catch (CommerceException e) {
            addFormException(new DropletFormException(e.toString(), null));
            return;
          } catch (IntrospectionException e) {
            addFormException(new DropletFormException(e.toString(), null));
            return;
          }

          //change commerce item infos to point to new shipping group
          getShippingHelper().changeShippingGroupForCommerceItemShippingInfos(
              getAllHardgoodCommerceItemShippingInfos(), getNewShipToAddressName(), getShippingMethod());

          /*
           * We should change shipping groups key in the shipping group map container, if shipping group has been reused
           * and its name has been changed.
           * If not change this key, commerce items shipping info tools would be unable to find a stored shipping group,
           * that is why it would be unable to property apply shipping groups.
           *
           * It's ok to do so, cause this method used by single shipping form only, so we can have only one hardgood shipping group.
           * After we've changed its name, the shipping group will have proper address data and proper name.
           */
          if (oldShippingGroupName != null && !oldShippingGroupName.equals(getNewShipToAddressName()))
          {
            ShippingGroup shippingGroup = getShippingGroupMapContainer().getShippingGroup(oldShippingGroupName);
            getShippingGroupMapContainer().removeShippingGroup(oldShippingGroupName);
            getShippingGroupMapContainer().addShippingGroup(getNewShipToAddressName(), shippingGroup);
          }

          if (getFormError()) {
            return;
          }

          applyShippingGroups(pRequest, pResponse);
        }
    }
  }

  /**
   * This method initializes the billing address from the shipping address if the user selected
   * that option. Saves addresses in the profile, if the user selected that option.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void postShipToNewAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
          throws ServletException, IOException {

    //set the profile's default shipping method if it isn't already set.
    getShippingHelper().saveDefaultShippingMethod(getProfile(), getShippingMethod());

    if (isSingleShippingGroupCheckout()) {
      // If user checked "use shipping for billing", copy address to payment group,
      // and set flag to prefill form fields on billing page.
      if (getUseShippingForBilling()) {
        getCheckoutOptionSelections().setPrefillBillingAddress(true);
        getShippingHelper().copyShippingToBilling(getOrder(), getAddress());
      }
    }
  }

  /**
   * Perfirms validations for Gift Shipping Groups in the order, against the selected shipping method and
   * shipping restrictions
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void preSetupGiftShippingDetails(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {

    // Validate GiftShipping Group Shipping Methods
    Collection giftShippingGroups = getGiftShippingGroups();
    if (giftShippingGroups != null && giftShippingGroups.size() > 0) {
      validateShippingMethodForGiftShippingGroups(pRequest, pResponse);

      validateShippingRestrictions(pRequest, pResponse);
    }
  }


  /**
   * Setup shipping details for gift shipping groups
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void setupGiftShippingDetails(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
    // Gift shipping groups can be treated as all other groups now, do not set shipping method on them.
  }

  /**
   * Perform any post actions after setting up gift shipping groups
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void postSetupGiftShippingDetails(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
            throws ServletException, IOException {
    // empty implementation
  }


  /**
   * Logic to reprice order, and parse any errors.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void repriceOrder(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
        throws ServletException, IOException {

    try {
      getShippingHelper().repriceOrder(getOrder(), getUserPricingModels(), getUserLocale(pRequest, pResponse), getProfile());
    }
    catch (PricingException pe) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error w/ PricingTools.priceOrderTotal: " + pe));
      }
      String pricingMessage = createPricingErrorMessage(pe, pRequest, pResponse);
      addFormException(new DropletFormException(pricingMessage, pe, ""));
    }
  }


  /**
   * Adds a new shipping group to the ShippingGroupMapContainer.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return true if success, false - otherwise
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  public boolean handleAddShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
          throws ServletException, IOException {
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getAddShippingAddressErrorURL(), pRequest, pResponse)) {
        return false;
      }

      preAddShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in preAddShippingAddress.");
        }

        return checkFormRedirect(null, getAddShippingAddressErrorURL(), pRequest, pResponse);
      }

      addShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in addShippingAddress");
        }

        return checkFormRedirect(null, getAddShippingAddressErrorURL(), pRequest, pResponse);
      }

      postAddShippingAddress(pRequest, pResponse);

      //always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect(getAddShippingAddressSuccessURL(), getAddShippingAddressErrorURL(), pRequest, pResponse);
    } finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  /**
   * Validates the selected nickname and address properties.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preAddShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    String addressName = getNewShipToAddressName();
    Address address = getShippingHelper().trimSpacesFromAddressValues(getAddress());

    //validate address and nickname
    if (getSaveShippingAddress()) {
      validateAddressNickname(getShippingHelper().getStoreOrderTools().getProfileTools().
          getUniqueShippingAddressNickname(address, getProfile(), addressName), pRequest, pResponse);
    }

    validateShippingAddress(address, pRequest, pResponse);
  }


  /**
   * Creates a new shipping group and adds it to the shipping group map container.
   * Optionally saves the shipping group address to the profile based on the
   * saveShippingAddress property.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void addShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
          throws ServletException, IOException {
    try {
      getShippingHelper().addShippingAddress(getProfile(), getNewShipToAddressName(), getAddress(),
          getShippingGroupMapContainer(), getSaveShippingAddress());
    }
    catch (StorePurchaseProcessException bppe) {
      String msg = ResourceUtils.getMsgResource(bppe.getMessage(), getResourceBundleName(), getResourceBundle(getUserLocale(pRequest, pResponse)));
      addFormException(new DropletFormException(msg, "", bppe.getMessage()));
    }
    catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMinor(ce.getMessage()));
      }
      addFormException(new DropletFormException(ce.getMessage(), ce, null));
    }
  }


  /**
   * Copies the new shipping group address to the order's credit card payment group address.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postAddShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (getUseShippingForBilling()) {
      getCheckoutOptionSelections().setPrefillBillingAddress(true);
      getShippingHelper().copyShippingToBilling(getOrder(), getAddress());
    }
  }


  /**
   * Handler for editing an address.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return true if success, false - otherwise
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  public boolean handleEditShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getEditShippingAddressErrorURL(), pRequest, pResponse)) {
        return false;
      }

      preEditShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in preEditShippingAddress.");
        }

        return checkFormRedirect(null, getEditShippingAddressErrorURL(), pRequest, pResponse);
      }

      editShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in editShippingAddress");
        }

        return checkFormRedirect(null, getEditShippingAddressErrorURL(), pRequest, pResponse);
      }

      postEditShippingAddress(pRequest, pResponse);

      //always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect(getEditShippingAddressSuccessURL(), getEditShippingAddressErrorURL(), pRequest, pResponse);
    } finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }


  /**
   * Validates the address properties.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preEditShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    String newNickName = getShippingAddressNewNickName();
    if(newNickName != null && !newNickName.equals(getEditShippingAddressNickName())){
      validateAddressNickname(newNickName, pRequest, pResponse);
    }
    validateShippingAddress(getEditAddress(), pRequest, pResponse);
  }


  /**
   * Edits a shipping group address in the container and saves the changes to the profile if the address is in the
   * profile's address map.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void editShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    getShippingHelper().modifyShippingAddress(getEditShippingAddressNickName(), getEditAddress(),
        getShippingGroupMapContainer());

    if (getShippingAddressNewNickName()!= null && !getShippingAddressNewNickName().equals(getEditShippingAddressNickName())){
      getShippingHelper().modifyShippingAddressNickname(getProfile(), getEditShippingAddressNickName(),
                                                        getShippingAddressNewNickName(), getShippingGroupMapContainer());
    }
  }

  /**
   * Post edit shipping address processing. If the address nick name is in the profile's map,
   * the updates are applied to that address too.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postEditShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    getShippingHelper().saveModifiedShippingAddressToProfile(getProfile(),
        getShippingAddressNewNickName() != null ? getShippingAddressNewNickName() : getEditShippingAddressNickName(),
        getEditAddress());
  }

  /**
   * Handler for removing a shipping address.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return true if success, false - otherwise
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  public boolean handleRemoveShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getRemoveShippingAddressErrorURL(), pRequest, pResponse)) {
        return false;
      }

      preRemoveShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in preRemoveShippingAddress.");
        }

        return checkFormRedirect(null, getRemoveShippingAddressErrorURL(), pRequest, pResponse);
      }

      removeShippingAddress(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in removeShippingAddress");
        }

        return checkFormRedirect(null, getRemoveShippingAddressErrorURL(), pRequest, pResponse);
      }

      postRemoveShippingAddress(pRequest, pResponse);

      //always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect(getRemoveShippingAddressSuccessURL(), getRemoveShippingAddressErrorURL(), pRequest, pResponse);
    } finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }


  /**
   * Pre remove shipping address processing.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preRemoveShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

  }


  /**
   * Removes a shipping group address from the container.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void removeShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    getShippingHelper().removeShippingAddress(getProfile(),getRemoveShippingAddressNickName(),
        getShippingGroupMapContainer());
  }

  /**
   * Post remove shipping address processing. If the address nickname is in the profile's map,
   * the address is removed from the profile too.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postRemoveShippingAddress(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    getShippingHelper().removeShippingAddressFromProfile(getProfile(),getRemoveShippingAddressNickName());
  }

  /**
   * This handler method will a new shipping group to the ShippingGroupMapContainer if new shipping address
   * is not empty. And will redirect to the multiple shipping URL. If new shipping address is empty it will
   * redirect to multiple shipping URL without adding new shipping group.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>boolean</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleAddAddressAndMoveToMultipleShipping(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (isEmptyNewAddress((ContactInfo)getAddress())){
      // create new address is empty just move to the multiple shipping without saving an address
      return checkFormRedirect(getAddAddressAndMoveToMultipleShippingSuccessURL(), getAddAddressAndMoveToMultipleShippingErrorURL(), pRequest, pResponse);
    }
    setAddShippingAddressSuccessURL(getAddAddressAndMoveToMultipleShippingSuccessURL());
    setAddShippingAddressErrorURL(getAddAddressAndMoveToMultipleShippingErrorURL());
    return handleAddShippingAddress(pRequest, pResponse);

  }

  /**
   * Checks whether new address is empty.
   * @param pAddress address to check
   * @return true if address is empty, false otherwise
   */
  public boolean isEmptyNewAddress(ContactInfo pAddress){
    if (!StringUtils.isEmpty(pAddress.getFirstName())) return false;
    if (!StringUtils.isEmpty(pAddress.getLastName())) return false;
    if (!StringUtils.isEmpty(pAddress.getAddress1())) return false;
    if (!StringUtils.isEmpty(pAddress.getCity())) return false;
    if (!StringUtils.isEmpty(pAddress.getState())) return false;
    if (!StringUtils.isEmpty(pAddress.getPostalCode())) return false;
    if (!StringUtils.isEmpty(pAddress.getPhoneNumber())) return false;
    return true;
  }




  /**
   * Determines if the customer is attempting to ship an item to a country that is restricted.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void validateShippingRestrictions(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    //validate shipping restrictions, if configured.
    if (getShippingHelper().getValidateShippingRestriction()) {
      if (isLoggingDebug()) {
        logDebug("Validating Shipping Restrctions");
      }
      try {
        //calling Shipping Restriction validate method
        List shippingValidationResult =
              getShippingHelper().checkShippingRestrictions(getCommerceItemShippingInfoContainer(), getShippingGroupMapContainer());
        if (shippingValidationResult != null && shippingValidationResult.size() > 0) {
          processShippingRestrictionsErrors(shippingValidationResult, pRequest, pResponse);
        }
      } catch (Exception exc) {
        processException(exc, exc.getMessage(), pRequest, pResponse);
      }
    }
  }


  /**
   * Validate address nickname for duplicacy
   *
   * @param pNickName - nickname
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void validateAddressNickname(String pNickName,DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    StoreOrderTools orderTools = (StoreOrderTools) getOrderManager().getOrderTools();

    if (orderTools.getProfileTools().isDuplicateAddressNickName(getProfile(), pNickName)) {
      try {
        String errorMessage = formatUserMessage(StoreShippingProcessHelper.MSG_DUPLICATE_NICKNAME,
                                                pRequest, pResponse);

        addFormException(new DropletFormException(errorMessage,
                                                  "",
                                                  StoreShippingProcessHelper.MSG_DUPLICATE_NICKNAME));
      }
      catch (Exception e) {
        processException(e, e.getMessage(), pRequest, pResponse);
      }
    }
  }


  /**
   * Validates the new address. Check for required properties, and
   * make sure street address doesn't include PO Box, AFO/FPO.
   *
   * @param pAddress - address
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void validateShippingAddress(Address pAddress,
                 DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws IOException, ServletException {

    ContactInfo shippingAddress = (ContactInfo) pAddress;

    List missingRequiredAddressProperties =
        getShippingHelper().checkForRequiredAddressProperties(shippingAddress, pRequest);
    addAddressValidationFormError(missingRequiredAddressProperties, pRequest, pResponse);

    List invalidStreetPatterns = getShippingHelper().checkForInvalidStreetAddress(shippingAddress);
    if (invalidStreetPatterns != null && invalidStreetPatterns.size() >0) {
      Iterator streetrator = invalidStreetPatterns.iterator();
      while (streetrator.hasNext()) {
        Object[] params = { (String)streetrator.next() };
        String msg = formatUserMessage(StoreShippingProcessHelper.MSG_INVALID_STREET_ADDRESS, params, pRequest, pResponse);
        addFormException(new DropletFormException(msg, null));
      }
    } else {
      if ((missingRequiredAddressProperties == null || missingRequiredAddressProperties.isEmpty())){
        String country = shippingAddress.getCountry();
        String state = shippingAddress.getState();
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
            msg = formatUserMessage(BillingInfoFormHandler.MSG_ERROR_INCORRECT_STATE, countryName, pRequest, pResponse);
            addFormException(new DropletFormException(msg, BillingInfoFormHandler.MSG_ERROR_INCORRECT_STATE));
          } catch (Exception e) {
            logError("Error validating state", e);
          }
        }
      }
    }

    try {
      getShippingHelper().validateShippingCity(shippingAddress);
    }
    catch (StorePurchaseProcessException bppe) {
      String msg = formatUserMessage(bppe.getMessage(), bppe.getParams(), pRequest, pResponse);
      addFormException(new DropletFormException(msg, null));
    }
  }

  /**
   * Validates the new address - Make sure user isn't trying to Express ship to AK, etc.
   *
   * @param pAddress - address
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void validateShippingMethod(Address pAddress, String pShippingMethod,
                 DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
      throws IOException, ServletException {
    try {
      getShippingHelper().validateShippingMethod(pAddress, pShippingMethod);
    } catch (StorePurchaseProcessException bppe) {
      String msg = formatUserMessage(bppe.getMessage(), bppe.getParams(), pRequest, pResponse);
      addFormException(new DropletFormException(msg, null));
    }
  }


  /**
   * Validate the gift shipping addresses against the shipping methods being used.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void validateShippingMethodForGiftShippingGroups(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    Collection giftShippingGroups = getGiftShippingGroups();

    if (giftShippingGroups != null) {
      Iterator giftsgerator = giftShippingGroups.iterator();
      HardgoodShippingGroup hg;

      while (giftsgerator.hasNext()) {
        hg = (HardgoodShippingGroup) giftsgerator.next();
        try {
          getShippingHelper().validateShippingMethod(hg.getShippingAddress(),hg.getShippingMethod());
        }
        catch (StorePurchaseProcessException bppe) {
          String msg = formatUserMessage(bppe.getMessage(), bppe.getParams(), pRequest, pResponse);
          addFormException(new DropletFormException(msg, null));
        }
      }
    }
  }


  /**
   * Validates the shipping group addresses against the provided shipping method.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void validateShippingMethodForContainerShippingGroups(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
  
	ShippingGroupManager sgm = getShippingGroupManager();
    // helper map to store ship group nickname to ship method
    Map<String, String> shipGroupToMethod = new HashMap<String, String>();
    // helper map to store 'ship group nickname' + 'shipping method' combination to shipping group
    Map<String, ShippingGroup> sgMethodToShipGroup = new HashMap<String, ShippingGroup>();

    Iterator iter = getAllHardgoodCommerceItemShippingInfos().iterator();

    //loop through all CISIs to find shipping groups with the same address
    // but different shipping methods
    while (iter.hasNext()){
      CommerceItemShippingInfo cisi = (CommerceItemShippingInfo) iter.next();
      String shipGroupName = cisi.getShippingGroupName();
      String shippingMethod = cisi.getShippingMethod();
      String shipGroupMethodKey = (shipGroupName + NICKNAME_SEPARATOR + shippingMethod);

      // Get shipping group from container for this CISI
      HardgoodShippingGroup shippingGroup =
        (HardgoodShippingGroup)getShippingGroupMapContainer().getShippingGroup(shipGroupName);
	  
      // The shipping group is null for the gift note. 
      if (shippingGroup != null) {
        try { 
      	  getShippingHelper().validateShippingMethod(shippingGroup.getShippingAddress(),
      	  											 shippingMethod);
        }  
        catch (StorePurchaseProcessException bppe) {
          String msg = formatUserMessage(bppe.getMessage(), bppe.getParams(), pRequest, pResponse);
          boolean isDuplicate = false;
	    
          Vector<Exception> exceptions = (Vector<Exception>) getFormExceptions(); 
          if ((exceptions != null) && (exceptions.size() > 0)) {
            for (Exception ex : exceptions) {
              if ((ex.getMessage() != null) && (ex.getMessage().equals(msg))) {
                isDuplicate = true;
                break;
              } 
            }  
          } 
          if (!isDuplicate) {
            addFormException(new DropletFormException(msg, null));
          }
        }
      }
    }   
  }


  /**
   * Initializes the form handler for single shipping group selection. Initialization only
   * occurs if there are no form errors.
   * <p>
   * This method expects that the ShippingGroupMapContainer has been initialized with shipping groups.
   * <p>
   * The following form properties are initialized: <br>
   * <dl>
   * <dt>shipToAddressName
   * <dd>initialied to the profile's address nick name used to originally create the address. This is
   * determined by matching the shipping address to profile addresses. If there's no match it is set
   * to <code>NEW_ADDRESS</code>
   * <dt>address
   * <dd>if the shipping address isn't in the profile this property is initialized with the address
   * <dt>shippingMethod
   * <dd>initialized from the current shipping group's shipping method value, or the profile's default
   * setting, or the default configured value.
   * </dl>
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return always true
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  public boolean handleInitSingleShippingForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    //only init if there aren't form errors.
    if (!getFormError()) {

      StoreShippingProcessHelper shippingHelper = getShippingHelper();
      StoreOrderTools orderTools = shippingHelper.getStoreOrderTools();
      ShippingGroupManager shippingGroupManager = getShippingGroupManager();

      ShippingGroupMapContainer sgmc = getShippingGroupMapContainer();
      List hSGroups = shippingGroupManager.getHardgoodShippingGroups(getOrder());

      HardgoodShippingGroup hgsg = null;

      String shippingGroupName = null;


      //The following code is about initializing the shipToAddress name property and address property with the values
      //that are currently in the order. If the shipping group has a recognized address from the profile the
      //shipToAddress name is set to that nick name. Otherwise, the address property is pre-populated with
      //the shipping group's address where it can be handled as a "new" address.

      //if we have a shipping group, try and figure out if the address came from the profile's saved addresses and
      //also set the shipping method.
      if (hSGroups != null && !hSGroups.isEmpty()) {
        hgsg = (HardgoodShippingGroup) hSGroups.get(0);
      }

      if (hgsg != null) {
        if (StringUtils.isBlank(getShipToAddressName())) {
          //unless otherwise detected, it's an address not in the profile and therefore handled as a "new" address
          setShipToAddressName(StoreShippingProcessHelper.NEW_ADDRESS);

          Address currentaddress = hgsg.getShippingAddress();

          // Initially set the selected shipping address as the first entry in the secondary address map,
          // this is superceded by the shipping address associated with the order or the user profile  
          // default address if no shipping address is specified on the order.
          Map addresses = (Map) getProfile()
                              .getPropertyValue(((StorePropertyManager) orderTools.getProfileTools()
                              .getPropertyManager()).getSecondaryAddressPropertyName());
          TreeMap sortedAddresses = new TreeMap(addresses);

          if (sortedAddresses != null && !sortedAddresses.isEmpty()){
            setShipToAddressName((String)sortedAddresses.firstKey());
          }

          //if the current address is populated, figure out the shipping group name in the map.
          if (!StringUtils.isBlank(currentaddress.getAddress1())) {
            //the shipping address already in the order might be one of the addresses in the map
            shippingGroupName = shippingGroupManager.getShippingGroupName(hgsg, sgmc,
                getShippingGroupInitializers().values());

            //figure out if the name is in the profile's shipping address map.
            if (shippingGroupName != null) {
              if (addresses != null) {
                if (addresses.containsKey(shippingGroupName) || sgmc.getShippingGroupMap().containsKey(shippingGroupName)) {
                  setShipToAddressName(shippingGroupName);
                }
                else {
                  setAddress(hgsg.getShippingAddress());
                }
              }
            }
            else {
              setAddress(hgsg.getShippingAddress());
            }
          }
          else {
            //we have a shipping group, but the address hasn't been filled in.
            //if the profile has a default address to choose from, init shipToAddressName to its name in the map
            HardgoodShippingGroup defaulthgsg = shippingHelper.createShippingGroupFromDefaultAddress(getProfile());

            if (defaulthgsg != null) {
              shippingGroupName = shippingGroupManager.getShippingGroupName(defaulthgsg, sgmc,
                  getShippingGroupInitializers().values());

              if (shippingGroupName != null) {
                setShipToAddressName(shippingGroupName);
              }
            }
          }
        }

        /*
         * Get nickname for not-saved shipping address.
         * It's stored nowhere but in the shipping group name, take first CISI and look into its shipping group name.
         */
        if (StringUtils.isBlank(getNewShipToAddressName()) && getShipToNewAddress()) {
          List<CommerceItemShippingInfo> allItems = getCommerceItemShippingInfoContainer().getAllCommerceItemShippingInfos();
          if (!allItems.isEmpty()) {
            setNewShipToAddressName(allItems.get(0).getShippingGroupName());
          }
        }

        //init the shipping method if not set yet
        if (StringUtils.isBlank(getShippingMethod())) {
          String shippingMethod =
              getShippingHelper().initializeShippingMethod(getProfile(), hgsg, getDefaultShippingMethod());
          setShippingMethod(shippingMethod);
        }
      }
    }

    return true;
  }


  /**
   * Initializes the form handler for multiple shipping group selection. Initialization only
   * occurs if there are no form errors.
   * <p>
   * The following form properties are initialized: <br>
   * <dl>
   * <dt>shippingMethod
   * <dd>initialized from the first shipping group's shipping method value, or the profile's default
   * setting, or the default configured value. Note that the application only allows one shipping
   * method to be used per order, so all the shipping groups have the same shipping method.
   * </dl>
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return always true
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  public boolean handleInitMultipleShippingForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    //only init if there aren't form errors.
    if (!getFormError()) {
      HardgoodShippingGroup hgsg = getFirstNonGiftHardgoodShippingGroupWithRels();

      //init the shipping method from the first shipping group in the order.
      if (hgsg != null) {
        if (StringUtils.isBlank(getShippingMethod())) {
          String shippingMethod =
              getShippingHelper().initializeShippingMethod(getProfile(), hgsg, getDefaultShippingMethod());
          setShippingMethod(shippingMethod);
        }
      }
    }
    return true;
  }


  /**
   * Initializes the address property with the shipping group address identified by the editNickName.
   * <p>
   * Initialization only takes place if there aren't any form errors.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return true if success, false - otherwise
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  public boolean handleInitEditAddressForm(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    if (!getFormError()) {
      String editNickName = getEditShippingAddressNickName();
      if (StringUtils.isBlank(editNickName)) {
        return true;
      }

      HardgoodShippingGroup hgsg = (HardgoodShippingGroup) getShippingGroupMapContainer().getShippingGroup(editNickName);
      if (hgsg != null) {
        setEditAddress(hgsg.getShippingAddress());
      }
    }
    return true;
  }

  /**
   * Process shipping restriction errors and add them to form exceptions
   *
   * @param pshippingValidationResult shipping validation results
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   */
  protected void processShippingRestrictionsErrors(List pshippingValidationResult,
                 DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws IOException, ServletException {

    //TODO: How to set error messages to be displayed on UI. Check the standard followed.
    if ((pshippingValidationResult != null) && (pshippingValidationResult.size() > 0)) {
      StringTokenizer st = null;
      String productName = null;
      String countryCode = null;
      String countryName = null;
      String msg = null;

      //Tokenizing the Country String and retrieving the country, product info in given format - ProductCode | countryCode
      for (int objNo = 0; objNo < pshippingValidationResult.size(); objNo++) {
        st = new StringTokenizer((String) pshippingValidationResult.get(objNo), StoreShippingProcessHelper.COUNTRY_DELIM);
        productName = st.nextToken();
        countryCode = st.nextToken();
        // setting country Name corresponding Country Code
        countryName = CountryRestrictionsDroplet.getCountryName(countryCode, getUserLocale(pRequest, pResponse));

        if (isLoggingDebug()) {
          logDebug("Product [" + productName + "] cannot be shipped to Country [" + countryCode + " - " + countryName + "]");
        }

        Object[] countryAndProductParams = { productName, countryName };
        msg = formatUserMessage(StoreShippingProcessHelper.MSG_RESTRICTED_SHIPPING, countryAndProductParams, pRequest, pResponse);
        addFormException(new DropletFormException(msg, null));
      }
    }
  }

  /**
   * Pulls out the Cybersource error message for invalid address.
   *
   * @param pe - pricing exception
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return error message
   */
  protected String createPricingErrorMessage(PricingException pe, DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse) {
    String msg = pe.getMessage();

    if (msg != null) {
      if (msg.indexOf("Invalid address") != -1) {
        try {
          return formatUserMessage(StoreShippingProcessHelper.PRICING_ERROR_ADDRESS, pRequest, pResponse);
        } catch (Exception e) {
          if (isLoggingError()) {
            logError(LogUtils.formatMinor(e.toString()));
          }
        }
      }
    }

    try {
      return formatUserMessage(StoreShippingProcessHelper.PRICING_ERROR, pRequest, pResponse);
    } catch (Exception e) {
      if (isLoggingError()) {
        logError(LogUtils.formatMinor(e.toString()));
      }

      return "";
    }
  }


  /**
   * Utility method to add form exception.
   *
   * @param pMissingProperties - missing properties list
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   */
  protected void addAddressValidationFormError(List pMissingProperties,
                                            DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) {
    if (pMissingProperties != null && pMissingProperties.size() > 0) {
      Map addressPropertyNameMap = getShippingHelper().getAddressPropertyNameMap();

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
          errorMsg = formatUserMessage(StoreShippingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY,
              addressPropertyNameMap.get(property), pRequest, pResponse);
        } catch (Exception e) {
          if (isLoggingError()) {
            logError(LogUtils.formatMinor("Error getting error string with key: " + StoreShippingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY +
                " from resource " + PurchaseUserMessage.RESOURCE_BUNDLE + ": " + e.toString()));
          }
        }

        addFormException(new DropletFormException(errorMsg,
            (String) addressPropertyNameMap.get(property), StoreShippingProcessHelper.MSG_MISSING_REQUIRED_ADDRESS_PROPERTY));
      }
    }
  }

  /**
   * Handler for editing shipping group's shipping method.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return true if success, false - otherwise
   * @throws ServletException if servlet error occurs
   * @throws IOException if IO error occurs
   */
  public boolean handleUpdateShippingMethod(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
                 throws ServletException, IOException {
    Transaction tr = null;

    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getUpdateShippingMethodErrorURL(), pRequest, pResponse)) {
        return false;
      }

      preUpdateShippingMethod(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in preUpdateShippingMethod.");
        }

        return checkFormRedirect(null, getUpdateShippingMethodErrorURL(), pRequest, pResponse);
      }

      updateShippingMethod(pRequest, pResponse);

      if (getFormError()) {
        if (isLoggingDebug()) {
          logDebug("Redirecting due to form error in UpdateShippingMethod");
        }

        return checkFormRedirect(null, getUpdateShippingMethodErrorURL(), pRequest, pResponse);
      }

      postUpdateShippingMethod(pRequest, pResponse);

      //always commit before a redirect!
      if (tr != null) {
        commitTransaction(tr);
        tr = null;
      }

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect(getUpdateShippingMethodSuccessURL(), getUpdateShippingMethodErrorURL(),
        pRequest, pResponse);
    } finally {
      if (tr != null) {
        commitTransaction(tr);
      }
    }
  }

  /**
   * Validates the shipping group's shipping method.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preUpdateShippingMethod(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {

    tenderCoupon(pRequest, pResponse);
    
    if (getFormError()) {
      return;
    }
    // Get the shipping group
    HardgoodShippingGroup hardgoodShippingGroup = (HardgoodShippingGroup) getOrder().getShippingGroups().get(0);
    // validate shipping method
    validateShippingMethod(hardgoodShippingGroup.getShippingAddress(), getShippingMethod(), pRequest, pResponse);
  }

  /**
   * Save changes to shipping groups.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void updateShippingMethod(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
          throws ServletException, IOException {
    try {
      Order order = getOrder();

      //just update order to save new setting for shipping groups
      getOrderManager().updateOrder(order);

      HardgoodShippingGroup object = (HardgoodShippingGroup) order.getShippingGroups().get(0);
      object.setShippingMethod(getShippingMethod());
    }
    catch (Exception exc) {
      processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
    }
  }

  /**
   * Post edit shipping method processing.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postUpdateShippingMethod(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    // reprice order to update shipping charges
    repriceOrder(pRequest, pResponse);

    String defaultCarrier = (String) getProfile().getPropertyValue(getCommercePropertyManager().getDefaultShippingMethodPropertyName());
    if (StringUtils.isEmpty(defaultCarrier)) {
      List shippingGroups = getShippingGroupManager().getHardgoodShippingGroups(getOrder());
      if (shippingGroups != null && shippingGroups.size() > 0){
        getShippingHelper().saveDefaultShippingMethod(getProfile(), ((ShippingGroup)shippingGroups.get(0)).getShippingMethod());
      }
    }
  }
}
