/*<ATGCOPYRIGHT>
 * Copyright (C) 2010-2011 Art Technology Group, Inc.
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
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;

import atg.commerce.CommerceException;
import atg.commerce.catalog.custom.CustomCatalogTools;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.Order;
import atg.commerce.order.purchase.AddCommerceItemInfo;
import atg.projects.store.order.GiftWrapCommerceItem;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreOrderManager;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.userprofiling.ProfileTools;
import atg.userprofiling.PropertyManager;

/**
 * Extends the StorePurchaseProcessHelper to implement cart processing helper
 * methods.
 * <p>
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreCartProcessHelper.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class StoreCartProcessHelper extends StorePurchaseProcessHelper {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/purchase/StoreCartProcessHelper.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------  

  /**
   * Error modifying order message resource key.
   */
  public static final String MSG_ERROR_MODIFYING_ORDER = "errorUpdateOrder";

  /**
   * Error adding to giftlist message resource key.
   */
  public static final String MSG_ERROR_ADDING_TO_GIFTLIST = "errorAddingToGiftlist";

  /**
   * No items selected message resource key.
   */
  public static final String MSG_NO_ITEMS_SELECT = "errorNoItemsSelected";

  /**
   * Invalid items message resource key.
   */
  public static final String MSG_INVALID_ITEMS = "invalidItems";

  /**
   * Invalid recipient e-mail address resource key.
   */
  public static final String MSG_INVALID_RECIPIENT_EMAIL = "invalidRecipientEmailAddress";

  /**
   * Invalid sender e-mail address.
   */
  public static final String MSG_INVALID_SENDER_EMAIL = "invalidSenderEmailAddress";
    
  /**
   * Profile tools.
   */
  protected ProfileTools mProfileTools;

  /**
   * Profile property manager.
   */
  PropertyManager mProfilePropertyManager;

  /**
   * @return the profile tools.
   */
  public ProfileTools getProfileTools() {
    return mProfileTools;
  }
  
  /**
   * @param pProfileTools - the profile tools to set.
   */
  public void setProfileTools(ProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  /**
   * @param pProfilePropertyManager - the property manager for profiles, used to see if the user is logged in
   * @beaninfo description:  The PropertyManager for profiles, used to see if the user is logged in
   **/
  public void setProfilePropertyManager(PropertyManager pProfilePropertyManager) {
    mProfilePropertyManager = pProfilePropertyManager;
  }

  /**
   * @return profile property manager.
   **/
  public PropertyManager getProfilePropertyManager() {
    return mProfilePropertyManager;
  }
    
  /**
  * Adds the given item(s) to the selected giftlist.
  * <p>
  * This method uses the AddCommerceItemInfo items to determine which skus and quantities to add to the
  * gift list.
  * @param pGiftlistId - A <code>String</code> value.
  * @param pItems - The items to add to the giftlist, a <code>AddCommerceItemInfo[]</code> value.
  * @param pGiftlistManager - The giftlist manager, a <code>GiftlistManager</code> value.
  * @return 
  */
  public String addItemsToGiftlist(String pGiftlistId, AddCommerceItemInfo[] pItems, GiftlistManager pGiftlistManager)
    throws ServletException, IOException, CommerceException {

    boolean itemSelected = false;
    
    try {
      // get all skus selected

      if (pItems.length == 0) {
        return null;
      }

      String skuId;
      String productId;
      AddCommerceItemInfo info;
      RepositoryItem product;
      RepositoryItem sku;
      String giftId;
      String displayName = null;
      String description = null;

      atg.commerce.catalog.CatalogTools cattools = pGiftlistManager.getGiftlistTools().getCatalogTools();

      for (int i = 0; i < pItems.length; i++) {
        info = pItems[i];

        if (info.getQuantity() == 0) {
          continue;
        }

        itemSelected = true;
        skuId = info.getCatalogRefId();
        productId = info.getProductId();
        product = cattools.findProduct(productId);
        sku = cattools.findSKU(skuId);

        if (product != null) {
          displayName = (String) product.getPropertyValue(pGiftlistManager.getGiftlistTools().getDisplayNameProperty());
          description = (String) product.getPropertyValue(pGiftlistManager.getGiftlistTools().getDescriptionProperty());
        } else {
          displayName = null;
          description = null;
        }

        // if item is in giftlist, increment quantity otherwise add
        giftId = pGiftlistManager.getGiftlistItemId(pGiftlistId, skuId);

        if (giftId != null) {
          pGiftlistManager.increaseGiftlistItemQuantityDesired(pGiftlistId, giftId, info.getQuantity());
        } else {
          String itemId = pGiftlistManager.createGiftlistItem(skuId, sku, productId, product, info.getQuantity(), displayName,
              description);
          pGiftlistManager.addItemToGiftlist(pGiftlistId, itemId);
        }
      }
    } catch (RepositoryException exc) {
      throw new CommerceException(exc);
    }
    
    if(itemSelected) {
      return null;
    } else {
      return MSG_NO_ITEMS_SELECT;
    }
  }
  
  /**
   * Check if all items on the order are in the catalog.
   * @param pOrder - An <code>Order</code> value.
   * @param pOrderManager
   * @return 
   */
  public boolean areItemsInCatalog(Order pOrder) {
    
    CustomCatalogTools catalogTools = (CustomCatalogTools) getOrderManager().getOrderTools().getCatalogTools();
    List commerceItems = pOrder.getCommerceItems();
    
    if (commerceItems != null) {
      Iterator itemIter = commerceItems.iterator();

      while (itemIter.hasNext()) {
        CommerceItem item = (CommerceItem) itemIter.next();
        RepositoryItem product = (RepositoryItem) item.getAuxiliaryData().getProductRef();

        if (!catalogTools.verifyCatalog(product)) {
          return false;
        }
      }
    }
    
    return true;
  }
  
  /**
   * Get the current security status from the provided profile.
   * @param pProfile - A <code>RepositoryItem</code> value.
   * @return Profile's security status as a <code>int</code> value.
   */
  public int getSecurityStatus(RepositoryItem pProfile) {
    // Proceed to login or shipping depending on securityStatus.
    int status = -1;
    String securityStatusProperty = getStoreOrderTools().getProfileTools().getPropertyManager().getSecurityStatusPropertyName();
    Object securityStatus = pProfile.getPropertyValue(securityStatusProperty);

    if (securityStatus != null) {
      status = ((Integer) securityStatus).intValue();
    }
    return status;
  }
  
  /**
   * Check to see if gift wrap has been added or removed from the order.
   * @param pOrder - An <code>StoreOrderImpl</code> value.
   * @param pGiftWrapSelected - value for whether or not gift wrap is selected, a <code>boolean</code> value.
   * @return true if giftwrap has been added or removed from the order, a <code>boolean</code> value.
   */
  public boolean isGiftWrapAddedOrRemoved(StoreOrderImpl pOrder, boolean pGiftWrapSelected) {
    
    // If gift wrap is either being added or removed, we need to reprice
    boolean orderContainsGiftWrap = pOrder.getContainsGiftWrap();

    if ((pGiftWrapSelected && !orderContainsGiftWrap) || (!pGiftWrapSelected && orderContainsGiftWrap)) {
      return true;
    }
    return false;
  }
  
  /**
   * Check to see if the order contains only gift wrap items
   * @param pOrder - An <code>Order</code> value.
   * @return true if all items on the order are giftwrapped, a <code>boolean</code> value.
   */
  public boolean isAllGiftWrap(StoreOrderImpl pOrder) {
    Collection items = pOrder.getCommerceItems();
    Iterator itemerator = items.iterator();

    while (itemerator.hasNext()) {
      if (!(itemerator.next() instanceof GiftWrapCommerceItem)) {
        return false;
      }
    }
    return true;
  }
  
  /**
   * Check the security status of the profile to see if the user is Authorized.
   * @param pProfile - A <code>RepositoryItem</code> value.
   * @return true if the profile is logged in, a <code>boolean</code> value.
   */
  public boolean isAuthorizedUser(RepositoryItem pProfile) {
    if (getSecurityStatus(pProfile) <= getProfilePropertyManager().getSecurityStatusCookie()) {
      return false;
    }
    
    return true;
  }
  
  /**
   * Check the security status of the profile to see if the user is Logged in.
   * @param pProfile - A <code>RepositoryItem</code> value.
   * @return true if the profile is logged in, a <code>boolean</code> value.
   */
  public boolean isLoginUser(RepositoryItem pProfile) {
    if (getSecurityStatus(pProfile) < getProfilePropertyManager().getSecurityStatusLogin()) {
      return false;
    }
    
    return true;
  }
  
  /**
   * Rollback the provided transaction.
   * @param pTransactionManager - Transaction Manager containing the transaction to rollback, A <code>TransactionManager</code> value.
   */
  public void rollbackTransaction(TransactionManager pTransactionManager) {
    if (pTransactionManager != null) {
      try {
        Transaction t = pTransactionManager.getTransaction();

        if (t != null) {
          t.setRollbackOnly();
        }
      } catch (SystemException exc) {
        if (isLoggingError()) {
          logError(exc);
        }
      }
    }
  }
  
  /**
   * This method can be used by form handlers to add / remove gift message or
   * gift wrap from the order.
   *
   * @param pOrder -
   *            The order
   * @param pAddGiftWrap -
   *            boolean value indicating whether or not to add gift wrap
   * @param pAddGiftMessage -
   *            boolean value indicating whether or not to add gift message
   * @param pGiftWrapSkuId -
   *            String value indicating Sku Id of the gift wrapped
   * @param pGiftWrapProductId -
   *            String value indicating Product Id of the gift wrapped
   */
  public void addRemoveGiftServices(StoreOrderImpl pStoreOrder, boolean pAddGiftWrap, boolean pAddGiftMessage, 
       String pGiftWrapSkuId, String pGiftWrapProductId) {
    StoreOrderManager om = (StoreOrderManager) getOrderManager();
    om.addRemoveGiftServices(pStoreOrder, pAddGiftWrap, pAddGiftMessage, pGiftWrapSkuId, pGiftWrapProductId);
  }
 
  
}
