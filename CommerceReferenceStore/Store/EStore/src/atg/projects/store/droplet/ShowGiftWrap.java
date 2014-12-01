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

import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.ShippingGroupManager;

import atg.nucleus.naming.ParameterName;

import atg.projects.store.catalog.StoreCatalogProperties;
import atg.projects.store.order.StoreOrderImpl;
import atg.projects.store.order.StoreShippingGroupManager;

import atg.repository.RepositoryItem;

import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

import atg.targeting.TargetingResults;

import java.io.IOException;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;


/**
 * Checks to see if the gift wrap option should be shown or not.
 * If the order has any item that is not gift wrappable, then don't
 * show the option.
 * <p>
 * Also renders an output parameter to indicate if the order contains any hard good
 * items (other than the gift wrap sku.
 *
 * @author ATG
 * @version $Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/ShowGiftWrap.java#2 $
 */
public class ShowGiftWrap extends DynamoServlet {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/droplet/ShowGiftWrap.java#2 $$Change: 651448 $";

  /** Input parameter name order. */
  public static final ParameterName ORDER = ParameterName.getParameterName("order");

  /** Oparam true. */
  public static final ParameterName TRUE = ParameterName.getParameterName("true");

  /** Oparam false. */
  public static final ParameterName FALSE = ParameterName.getParameterName("false");

  /** Oparam isHardGoods. */
  public static final String IS_HARDGOODS = "isHardGoods";

  /**
   * Shipping group manager.
   */
  protected ShippingGroupManager mShippingGroupManager;

  /**
   * Catalog properties.
   */
  protected StoreCatalogProperties mCatalogProperties;

  /**
   * Gift wrap targeting results.
   */
  protected TargetingResults mGiftWrapTargetingResults;

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

  /**
   * Set catalog properties.
   *
   * @param pCatalogProperties - catalog properties.
   */
  public void setCatalogProperties(StoreCatalogProperties pCatalogProperties) {
    mCatalogProperties = pCatalogProperties;
  }

  /**
   * Get catalog properties.
   *
   * @return catalog properties.
   */
  public StoreCatalogProperties getCatalogProperties() {
    return mCatalogProperties;
  }

  /**
   * Get gift wrap targeting results.
   *
   * @return gift wrap targeting results.
   */
  public TargetingResults getGiftWrapTargetingResults() {
    return mGiftWrapTargetingResults;
  }

  /**
   * Set gift wrapping targeting results.
   *
   * @param giftWrapTargetingResults - gift wrap targeting results
   */
  public void setGiftWrapTargetingResults(TargetingResults giftWrapTargetingResults) {
    mGiftWrapTargetingResults = giftWrapTargetingResults;
  }

  /**
   * Services true oparam if the gift wrapping option should be shown, false if not.
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse)
    throws ServletException, IOException {
    Object orderItem = pRequest.getObjectParameter(ORDER);

    if ((orderItem == null) || !(orderItem instanceof StoreOrderImpl)) {
      if (isLoggingDebug()) {
        logDebug("Bad order parameter passed: null=" + (orderItem == null) +
          ". If null=false, then wrong object type.");
      }

      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);

      return;
    }

    StoreOrderImpl order = (StoreOrderImpl) orderItem;

    StoreShippingGroupManager sgm = (StoreShippingGroupManager) getShippingGroupManager();

    if (!sgm.isAnyHardgoodShippingGroups(order)) {
      pRequest.setParameter(IS_HARDGOODS, Boolean.FALSE);
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);

      return;
    }

    pRequest.setParameter(IS_HARDGOODS, Boolean.TRUE);

    // If order has hardgood items that are not gift-wrappable, then render false.
    List hgShippingGroups = ((StoreShippingGroupManager) getShippingGroupManager()).getHardgoodShippingGroups(order);
    Iterator shippingGrouperator = hgShippingGroups.iterator();
    ShippingGroup shippingGroup = null;
    List itemRels = null;
    Iterator itemRelerator = null;
    ShippingGroupCommerceItemRelationship rel = null;
    CommerceItem item = null;
    RepositoryItem sku = null;
    String giftWrapEligiblePropertyName = getCatalogProperties().getGiftWrapEligiblePropertyName();
    Boolean giftWrapEligible = null;

    boolean onlyGiftWrapItem = true;
    boolean isGiftItem = false;

    while (shippingGrouperator.hasNext()) {
      shippingGroup = (ShippingGroup) shippingGrouperator.next();
      itemRels = shippingGroup.getCommerceItemRelationships();
      itemRelerator = itemRels.iterator();

      while (itemRelerator.hasNext()) {
        isGiftItem = false;
        rel = (ShippingGroupCommerceItemRelationship) itemRelerator.next();
        item = rel.getCommerceItem();

        sku = (RepositoryItem) item.getAuxiliaryData().getCatalogRef();

        if (sku == null) {
          // This should never happen
          if (isLoggingWarning()) {
            logWarning("There is a commerce item without a SKU, " + " Commerce item id: " + item.getId() +
              " with catalog ref id: " + item.getCatalogRefId() + " on order: " + order.getId());
          }

          continue;
        }

        if (!productIsGiftWrap(item)) {
          onlyGiftWrapItem = false;
        } else {
          isGiftItem = true;
        }

        giftWrapEligible = (Boolean) sku.getPropertyValue(giftWrapEligiblePropertyName);

        if (!isGiftItem && ((giftWrapEligible != null && giftWrapEligible.booleanValue()))) {
          if (isLoggingDebug()) {
            logDebug("Item not gift wrappable: " + item.getCatalogRefId());
          }

          pRequest.serviceLocalParameter(TRUE, pRequest, pResponse);

          return;
        }
      }
    }

    //if the gift wrap item is the only hardgood item, then render false
    if (onlyGiftWrapItem) {
      pRequest.setParameter(IS_HARDGOODS, Boolean.FALSE);
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
    } else {
      pRequest.serviceLocalParameter(FALSE, pRequest, pResponse);
    }
  }

  /**
   * Test to see if the item is a gift wrap item. Use TargetingResults
   * to get the gift wrap item product id.
   * @param item - item
   * @return true if items is a gift wrap commerce item.
   */
  protected boolean productIsGiftWrap(CommerceItem item) {
    String productId = item.getAuxiliaryData().getProductId();
    TargetingResults giftWrapTargetingResults = getGiftWrapTargetingResults();
    Enumeration giftWrapItems = giftWrapTargetingResults.getResults();

    if ((giftWrapItems != null) && giftWrapItems.hasMoreElements()) {
      // Gift wrap item targeter produced results, we now make sure
      // the product we are testing isn't a gift wrap product.
      RepositoryItem giftWrapProduct = (RepositoryItem) giftWrapItems.nextElement();

      if (productId.equals(giftWrapProduct.getRepositoryId())) {
        if (isLoggingDebug()) {
          logDebug("Item is not gift wrappable, but it's a gift wrap item!");
        }

        return true;
      }
    }

    return false;
  }
}
