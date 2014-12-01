/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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

package atg.projects.store.droplet;

import atg.commerce.CommerceException;

import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.ShippingGroupManager;
import atg.commerce.order.purchase.PurchaseProcessHelper;

import atg.commerce.pricing.PricingModelHolder;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.projects.store.order.StoreOrderTools;
import atg.projects.store.order.purchase.StoreAddCommerceItemInfo;
import atg.projects.store.profile.StorePropertyManager;
import atg.projects.store.profile.UserItems;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;
import atg.servlet.RequestLocale;

import atg.userprofiling.Profile;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletException;


/**
 * This droplet is placed on the page that follows login during checkout
 * (currently the shipping page). It is required for the
 * case when a user is recognized by auto-login, but when he/she gets
 * to the login page during checkout, indicates that this is a case
 * of mistaken identity, and wants to check out anonymously. We need
 * to copy the contents of the recognized user's cart to a new cart.
 *
 * In this case, the login during checkout process will save the user's
 * items in a globally-scoped map, and pass the "key" for this map
 * as a query parameter. We also need to set the new user's email
 * address here after the user has been logged out.
 *
 * All this is caused by the fact that we send the "DPSLogout=true"
 * in the query string, which causes the servlet pipeline to log the
 * user out.
 *
 * Finally, we remove the entries from the globally-scoped Map so
 * that it doesn't just keep growing.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/AddItemsToOrder.java#2 $
 */
public class AddItemsToOrder extends DynamoServlet {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/AddItemsToOrder.java#2 $$Change: 651448 $";

  /** Input parameter name order. */
  public static final ParameterName ORDER = ParameterName.getParameterName("order");

  /** Input parameter name profile. */
  public static final ParameterName PROFILE = ParameterName.getParameterName("profile");

  /**
   * New email address parameter name.
   */
  public static final String NEW_EMAIL_ADDRESS = "newEmailAddress";

  /**
   * Locale input parameter name.
   */
  static final ParameterName LOCALE_PARAM = ParameterName.getParameterName("locale");

  /**
   * Purchase process helper.
   */
  PurchaseProcessHelper mPurchaseProcessHelper;

  /**
   * User items.
   */
  UserItems mUserItems;

  //-------------------------------------
  /**
   * Order manager.
   */
  OrderManager mOrderManager;

  //--------------------------------------------------------
  /**
   * Store property manager.
   */
  private StorePropertyManager mStorePropertyManager;

  //-------------------------------------
  /**
   * User pricing models.
   */
  PricingModelHolder mUserPricingModels;

  //---------------------------------------------------------------------------
  /**
   * Profile.
   */
  RepositoryItem mProfile;

  /**
   * Sets property purchaseProcessHelper.
   *
   * @param pPurchaseProcessHelper a <code>PurchaseProcessHelper</code> value
   */
  public void setPurchaseProcessHelper(PurchaseProcessHelper pPurchaseProcessHelper) {
    mPurchaseProcessHelper = pPurchaseProcessHelper;
  }

  /**
   * Returns property purchaseProcessHelper.
   * @return a <code>PurchaseProcessHelper</code> value
   */
  public PurchaseProcessHelper getPurchaseProcessHelper() {
    return mPurchaseProcessHelper;
  }

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

  /**
   * Sets property OrderManager.
   * @param pOrderManager - order manager
   **/
  public void setOrderManager(OrderManager pOrderManager) {
    mOrderManager = pOrderManager;
  }

  /**
   * @return property OrderManager.
   **/
  public OrderManager getOrderManager() {
    return mOrderManager;
  }

  /**
   * @return the mStorePropertyManager.
   */
  public StorePropertyManager getStorePropertyManager() {
    return mStorePropertyManager;
  }

  /**
   * @param pStorePropertyManager - the mStorePropertyManager to set.
   */
  public void setStorePropertyManager(StorePropertyManager pStorePropertyManager) {
    mStorePropertyManager = pStorePropertyManager;
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
  public Locale getUserLocale(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object obj = pRequest.getObjectParameter(LOCALE_PARAM);

    if (obj instanceof Locale) {
      return (Locale) obj;
    } else if (obj instanceof String) {
      return RequestLocale.getCachedLocale((String) obj);
    } else {
      RequestLocale requestLocale = pRequest.getRequestLocale();

      if (requestLocale != null) {
        return requestLocale.getLocale();
      }
    }

    return Locale.getDefault();
  }

  /**
   * Get a string that will be used to identify the catalog to use when obtaining
   * a catalogRef and productRef for the creation of a commerce item.  CatalogTools
   * will maintain a mapping of key to catalog, currently this mapping will be maintained
   * by the users locale.
   *
   * @param pRequest servlet request object
   * @param pResponse servlet response object
   * @return the catalog key
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @see atg.commerce.catalog.CatalogTools
   */
  protected String getCatalogKey(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Locale usersLocale = getUserLocale(pRequest, pResponse);

    if (usersLocale == null) {
      return null;
    } else {
      return usersLocale.toString();
    }
  }

  /**
   * Sets property UserPricingModels.
   *
   * @param pUserPricingModels a <code>PricingModelHolder</code> value
   */
  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  /**
   * Returns property UserPricingModels.
   * @return a <code>PricingModelHolder</code> value
   */
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }

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

  /**
   * <p>
   * Adds items to the user's cart after being logged out during
   * the checkout process.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception javax.servlet.ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    // The user was just logged out during checkout. Add the items from the
    // old cart to the current cart. Also, set the temp user id so this
    // can be used for registration.
    Object orderItem = pRequest.getObjectParameter(ORDER);
    Profile profile = (Profile) pRequest.getObjectParameter(PROFILE);

    String email = pRequest.getQueryParameter(NEW_EMAIL_ADDRESS);
    String itemKey = (String) pRequest.getQueryParameter(UserItems.ITEM_KEY);

    // If no itemKey is passed as a query param, then don't do anything.
    if (itemKey == null) {
      return;
    }

    // Now that the user is anonymous, go ahead and set the login and email
    // properties on the profile.
    if (email != null) {
      StorePropertyManager pm = getStorePropertyManager();
      profile.setPropertyValue(pm.getLoginPropertyName(), email);
      profile.setPropertyValue(pm.getEmailAddressPropertyName(), email);
    }

    List itemsToAdd = (List) getUserItems().getItems().get(itemKey);

    if (itemsToAdd == null) {
      if (isLoggingDebug()) {
        logDebug("itemsToAdd == null. Not adding any items.");
      }

      return;
    }

    StoreOrderImpl order = (StoreOrderImpl) orderItem;

    copyItemsToOrder(itemsToAdd, order, getProfile(), getUserPricingModels(), getCatalogKey(pRequest, pResponse),
      getUserLocale(pRequest, pResponse));

    // Clean up UserItems so it doesn't grow incessantly.
    getUserItems().getItems().remove(itemKey);
  }

  /**
   * This method is used when a user enters the checkout process as
   * a recognized user, but decides to proceed anonymously. In this
   * case, the user gets a brand new cart (required b/c otherwise
   * the user might see address/cc info from persisted cart which
   * apparently is not his/hers). The items from the old cart
   * are copied into the new cart with this method.
   *
   * @param pItemsToAdd The list of AddCommerceItemInfo objects to add
   * @param pOrder the customer order
   * @see atg.commerce.order.purchase.AddCommerceItemInfo
   * @param pProfile - profile
   * @param pUserPricingModels - user pricing models
   * @param pCatalogKey - catalog key
   * @param pLocale - locale
   */
  public void copyItemsToOrder(List pItemsToAdd, Order pOrder, RepositoryItem pProfile,
    PricingModelHolder pUserPricingModels, String pCatalogKey, Locale pLocale) {
    if (isLoggingDebug()) {
      logDebug("Copying items to new order.");
    }

    StoreOrderManager om = (StoreOrderManager) getOrderManager();
    StoreOrderTools orderTools = (StoreOrderTools) om.getOrderTools();
    ShippingGroupManager sm = om.getShippingGroupManager();
    PurchaseProcessHelper helper = getPurchaseProcessHelper();
    StoreAddCommerceItemInfo[] items = new StoreAddCommerceItemInfo[pItemsToAdd.size()];

    try {
      // Fill in the items array and create shipping groups for
      // all the items that have special shipping information
      //
      Map sgs = new HashMap();

      for (int index = 0; index < pItemsToAdd.size(); index++) {
        StoreAddCommerceItemInfo itemInfo = (StoreAddCommerceItemInfo) pItemsToAdd.get(index);
        items[index] = itemInfo;
      }

      helper.addItemsToOrder(pOrder, orderTools.getShippingGroup(pOrder), pProfile, items, pLocale, pCatalogKey,
        pUserPricingModels, null, null);

      om.updateOrder(pOrder);
    } catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error creating commerce item "), ce);
      }
    }
  }
}
