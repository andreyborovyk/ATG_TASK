/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.commerce.order.purchase;

import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.pricing.PricingModelHolder;
import atg.commerce.order.ChangedProperties;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.CreditCard;
import atg.commerce.order.GiftCertificate;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.ItemQuantityChanged;
import atg.commerce.order.ItemRemovedFromOrder;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.util.RepeatingRequestMonitor;
import atg.core.util.StringUtils;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.repository.RepositoryItem;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletException;
import javax.transaction.Transaction;

/**
 * This formhandler is used to modify a ShoppingCart by adding items to it, deleting
 * items from it, modifying the quantities of items in it, and preparing it for the
 * checkout process.
 *
 * @beaninfo
 *   description: A formhandler which allows the user to modify their ShoppingCart.
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @author Ernesto Mireles
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CartModifierFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.purchase.PurchaseProcessFormHandler
 */
public class CartModifierFormHandler extends PurchaseProcessFormHandler
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/purchase/CartModifierFormHandler.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  public static final String MSG_INVALID_QUANTITY = "invalidQuantity";
  public static final String MSG_ERROR_UPDATE_ORDER = "errorUpdatingOrder";
  public static final String MSG_ERROR_MOVE_TO_PURCHASE_INFO = "errorMovingToPurchaseInfo";
  public static final String MSG_ERROR_REMOVING_ITEM = "errorRemovingItemFromOrder";
  public static final String MSG_UNSUPPORTED_RELATIONSHIP = "unsupportedRelationship";
  public static final String MSG_NO_ITEMS_TO_ADD = "noItemsToAdd";
  public static final String MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO = "quantityLessThanOrEqualToZero";
  public static final String MSG_QUANTITY_LESSTHAN_ZERO = "quantityLessThanZero";
  public static final String MSG_NO_ORDER_TO_MODIFY = "noOrderToModify";
  public static final String MSG_ERROR_ADDING_TO_ORDER = "errorAddingToOrder";
  public static final String MSG_AMBIGUOUS_INPUT_FOR_ADD = "ambiguousInputForAdd";

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //-------------------------------------
  // property: deletedSkus
  //-------------------------------------

  private List mDeletedSkus;

  /**
   * Deleted skus is a list that maintains which items were removed by the deleteItems call and
   * is consulted in the addItemToOrder call to determine whether a quantity of 0 is being
   * added or whether it is simply an artifact of a design that calls handleAddItemToOrder in
   * the handleRemoveAndAddItem.
   *
   * @param pDeletedSkus - the list of skus that were removed
   **/
  protected void setDeletedSkus(List pDeletedSkus) {
    mDeletedSkus = pDeletedSkus;
  }

  /**
   * @return the list of deleted skus
   **/
  protected List getDeletedSkus() {
    return mDeletedSkus;
  }

  //-------------------------------------
  // property: commerceItemType
  //-------------------------------------
  private String mCommerceItemType = "default";

  /**
   * Sets the default item type for added commerce items
   */
  public void setCommerceItemType(String pCommerceItemType) {
    mCommerceItemType = pCommerceItemType;
  }

  /**
   * Returns the default item type for added commerce items
   * @beaninfo description: The default item type for added commerce items
   */
  public String getCommerceItemType() {
    return mCommerceItemType;
  }

  //-------------------------------------
  // property: configurableItemTypeName
  //-------------------------------------
  private String mConfigurableItemTypeName = "configurableCommerceItem";

  /**
   * Returns the configurableItemTypeName
   */
  public String getConfigurableItemTypeName() {
    return mConfigurableItemTypeName;
  }

  /**
   * Sets the configurableItemTypeName
   */
  public void setConfigurableItemTypeName(String pConfigurableItemTypeName) {
    mConfigurableItemTypeName = pConfigurableItemTypeName;
  }  
  
  //-------------------------------------
  // property: CatalogRefIds
  String [] mCatalogRefIds;

  /**
   * Sets property CatalogRefIds
   *
   * @param pCatalogRefIds a <code>String[]</code> value
   */
  public void setCatalogRefIds(String [] pCatalogRefIds) {
    mCatalogRefIds = pCatalogRefIds;
  }

  /**
   * Returns property CatalogRefIds
   *
   * @return a <code>String[]</code> value
   */
  public String [] getCatalogRefIds() {
    return mCatalogRefIds;
  }

  //---------------------------------------------------------------------------
  // property: CommerceIds
  //---------------------------------------------------------------------------

  String[] mCommerceIds;

  /**
   * Sets the CommerceIds property which is a list of the CommerceItems to be operated on.
   *
   * @param pCommerceIds a <code>String[]</code> value of the commerce items to be operated on.
   **/
  public void setCommerceIds(String[] pCommerceIds) {
    mCommerceIds = pCommerceIds;
  }

  /**
   * Return the list of CommerceIds to be operated on.
   *
   * @return a <code>String[]</code> value of the CommerceItem ids to be operated on.
   **/
  public String[] getCommerceIds() {
    return mCommerceIds;
  }

  /** productIds */
  String [] mProductIds;

  //-------------------------------------
  /**
   * Sets productIds
   *
   * @param pProductIds a <code>String[]</code> value
   **/
  public void setProductIds(String [] pProductIds) {
    mProductIds = pProductIds;
  }

  //-------------------------------------
  /**
   * Returns productIds
   *
   * @return a <code>String[]</code> value
   **/
  public String [] getProductIds() {
    return mProductIds;
  }


  //-------------------------------------
  // property: ProductId
  String mProductId;

  /**
   * Sets property ProductId
   *
   * @param pProductId a <code>String</code> value
   */
  public void setProductId(String pProductId) {
    mProductId = pProductId;
  }

  /**
   * Returns property ProductId
   *
   * @return a <code>String</code> value
   */
  public String getProductId() {
    return mProductId;
  }

  //-------------------------------------
  // property: SiteId
  String mSiteId;

  /**
   * Sets property SiteId
   *
   * @param pSiteId a <code>String</code> value
   */
  public void setSiteId(String pSiteId) {
    mSiteId = pSiteId;
  }

  /**
   * Returns property SiteId
   *
   * @return a <code>String</code> value
   */
  public String getSiteId() {
    return mSiteId;
  }

  //-------------------------------------
  // property: Value
  Dictionary mValue = new Hashtable();

  /**
   * Returns property Value
   *
   * @return a <code>Dictionary</code> containing item level
   *         properties for add operations
   */
  public Dictionary getValue() {
    return mValue;
  }

  //-------------------------------------
  // property: Items
  AddCommerceItemInfo [] mItems;

  /**
   * Returns items array of property holders for add operations
   *
   * @return an array of item level property holders for add operations
   */
  public AddCommerceItemInfo [] getItems() {
    return mItems;
  }

  //-------------------------------------
  // property: AddItemInfoClass
  String mAddItemInfoClass = "atg.commerce.order.purchase.AddCommerceItemInfo";

  /**
   * Sets the name of the class to be used for elements in the items array
   **/
  public void setAddItemInfoClass(String pAddItemInfoClass) {
    mAddItemInfoClass = pAddItemInfoClass;
  }

  /**
   * Returns the name of the class to be used for elements in the items array
   *
   * @beaninfo
   *   description: The name of the class used for objects containing per-item properties
   **/
  public String getAddItemInfoClass() {
    return mAddItemInfoClass;
  }

  //-------------------------------------
  // property: AddItemCount
  int mAddItemCount;

  /**
   * Sets the number of elements to allocate in the items array
   * and allocates the array
   *
   * @param pAddItemCount an <code>int</code> value
   */
  public void setAddItemCount(int pAddItemCount) {
    if (pAddItemCount <= 0) {
      mAddItemCount = 0;
      mItems = null;
    }
    else {
      mAddItemCount = pAddItemCount;
      mItems = new AddCommerceItemInfo[mAddItemCount];
      Throwable caught = null;
      try {
        for (int index = 0; index < pAddItemCount; index++) {
          mItems[index] = (AddCommerceItemInfo)Class.forName(getAddItemInfoClass()).newInstance();
        }
      }
      catch (Throwable thrown) {
        caught = thrown;
      }
      if (caught != null) {
        if (isLoggingError()) {
          logError(caught);
        }

        // Throw away partially built array.
        //
        mItems = null;
      }
    }
  }

  /**
   * Returns the number of elements to allocate in the items array
   *
   * @return an <code>int</code> value
   */
  public int getAddItemCount() {
    return mAddItemCount;
  }

  //-------------------------------------
  // property: Quantity
  long mQuantity = 0;

  /**
   * Sets property Quantity
   *
   * @param pQuantity a <code>long</code> value
   */
  public void setQuantity(long pQuantity) {
    mQuantity = pQuantity;
  }

  /**
   * Returns property Quantity
   *
   * @return a <code>long</code> value
   */
  public long getQuantity() {
    return mQuantity;
  }

  //---------------------------------------------------------------------
  // property: invalidQuantityCancelsAdd
  boolean mInvalidQuantityCancelsAdd = true;

  /**
   * Determine if the FormHandler should treat invalid quantities as errors
   * when adding multiple items to an order in a single form submission.
   * If set to true, then any invalid quantity will block all additions.
   * If set to false, items with invalid quantities will be skipped and
   * others will be added.
   */
  public boolean getInvalidQuantityCancelsAdd() {
    return mInvalidQuantityCancelsAdd;
  }

  /**
   * Set the invalidQuantityCancelsAdd property.  Set to true if FormHandler should
   * block all item additions when some items have invalid quantities.
   * @param pInvalidQuantityCancelsAdd false if formhandler should add some
   * items even when others have invalid quantities.
   */
  public void setInvalidQuantityCancelsAdd(boolean pInvalidQuantityCancelsAdd) {
    mInvalidQuantityCancelsAdd = pInvalidQuantityCancelsAdd;
  }


  /** payment group bean to use for modifiing payment groups */
  PaymentGroup mPaymentGroup;

  //-------------------------------------
  /**
   * Sets payment group bean to use for modifying payment groups
   *
   * @param pPaymentGroup a <code>PaymentGroup</code> value
   */
  public void setPaymentGroup(PaymentGroup pPaymentGroup) {
	mPaymentGroup = pPaymentGroup;
  }

  /**
   * Returns property PaymentGroup
   * @return a <code>PaymentGroup</code> value
   * @beaninfo description: The PaymentGroup used during modifications to payment groups
   *
   */
  public PaymentGroup getPaymentGroup() {
    if (mPaymentGroup == null) {
      if (getOrder().getPaymentGroups() != null &&
          getOrder().getPaymentGroups().size() > 0) {
        mPaymentGroup = (PaymentGroup)getOrder().getPaymentGroups().get(0);
      }
      else {
        try {
          getOrder().addPaymentGroup(getPaymentGroupManager().createPaymentGroup());
          mPaymentGroup = (PaymentGroup)getOrder().getPaymentGroups().get(0);
        }
        catch (CommerceException ce) {
          if (isLoggingError()) {
            logError(ce);
          }
          return null;
        }
      }
    }

    return mPaymentGroup;
  }

  /** credit card bean to use for modifying of billing groups */
  CreditCard mCreditCard;

  //-------------------------------------
  /**
   * Sets credit card bean to use for modifying of billing groups
   *
   * @param pCreditCard a <code>CreditCard</code> value
   */
  public void setCreditCard(CreditCard pCreditCard) {
    mCreditCard = pCreditCard;
  }

  //-------------------------------------
  /**
   * Returns credit card bean to use for modifying of billing groups
   *
   * @return a <code>CreditCard</code> value
   */
  public CreditCard getCreditCard() {
    return mCreditCard;
  }


  /** gift certificate bean to use for modifying of billing grps */
  GiftCertificate mGiftCertificate;

  //-------------------------------------
  /**
   * Sets gift certificate bean to use for modifying of billing grps
   *
   * @param pGiftCertificate a <code>GiftCertificate</code> value
   */
  public void setGiftCertificate(GiftCertificate pGiftCertificate) {
    mGiftCertificate = pGiftCertificate;
  }

  //-------------------------------------
  /**
   * Returns gift certificate bean to use for modifying of billing grps
   *
   * @return a <code>GiftCertificate</code> value
   */
  public GiftCertificate getGiftCertificate() {
    return mGiftCertificate;
  }


  /** hardgood shipping group bean */
  HardgoodShippingGroup mHardgoodShippingGroup;

  //-------------------------------------
  /**
   * Sets hardgood shipping group bean
   *
   * @param pHardgoodShippingGroup a <code>HardgoodShippingGroup</code> value
   */
  public void setHardgoodShippingGroup(HardgoodShippingGroup pHardgoodShippingGroup) {
    mHardgoodShippingGroup = pHardgoodShippingGroup;
  }

  //-------------------------------------
  /**
   * Returns hardgood shipping group bean
   *
   * @return a <code>HardgoodShippingGroup</code> value
   */
  public HardgoodShippingGroup getHardgoodShippingGroup() {
    return mHardgoodShippingGroup;
  }

  /** shipping group bean to use for modifying the shipping groups */
  ShippingGroup mShippingGroup;

  //-------------------------------------
  /**
   * Sets shipping group bean to use for modifying the shipping groups
   *
   * @param pShippingGroup a <code>ShippingGroup</code> value
   */
  public void setShippingGroup(ShippingGroup pShippingGroup) {
    mShippingGroup = pShippingGroup;
  }

  /**
   * Returns property ShippingGroup.
   * @return a <code>ShippingGroup</code> value
   * @beaninfo description: The ShippingGroup used during modifications to shipping groups
   *
   */
  public ShippingGroup getShippingGroup() {

    if (mShippingGroup == null) {
      if (getOrder().getShippingGroups() != null &&
          getOrder().getShippingGroups().size() > 0)
      {
        mShippingGroup = (ShippingGroup)getOrder().getShippingGroups().get(0);
      }
      else {
        try {
          getOrder().addShippingGroup(getShippingGroupManager().createShippingGroup());
          mShippingGroup = (ShippingGroup)getOrder().getShippingGroups().get(0);
        }
        catch (CommerceException ce) {
          if (isLoggingError()) {
            logError(ce);
          }
          return null;
        }
      }
    }
    return mShippingGroup;
  }

  //---------------------------------------------------------------------------
  // property:Giftlist
  //---------------------------------------------------------------------------

  private String mGiftlistId;
  /**
   * Specifies the id of the Giftlist.
   *
   * @param pGiftlistId a <code>String</code> value
   */
  public void setGiftlistId(String pGiftlistId) {
    mGiftlistId = pGiftlistId;
  }

  /**
   * The giftlist that an item was added from
   * @return a <code>String</code> value
   * @beaninfo description: The id of the giftlist used when adding giftlist items
   *
   */
  public String getGiftlistId() {
    return mGiftlistId;
  }

  //---------------------------------------------------------------------------
  // property:GiftlistItemId
  //---------------------------------------------------------------------------

  private String mGiftlistItemId;
  /**
   * Specifies the id of the Giftlist Item
   *
   * @param pGiftlistItemId a <code>String</code> value
   */
  public void setGiftlistItemId(String pGiftlistItemId) {
    mGiftlistItemId = pGiftlistItemId;
  }

  /**
   * The giftlist item being added
   * @return a <code>String</code> value
   * @beaninfo description: The id of the giftlist item used when adding giftlist items
   *
   */
  public String getGiftlistItemId() {
    return mGiftlistItemId;
  }

  //-------------------------------------
  // property: AddItemToOrderPricingOp
  String mAddItemToOrderPricingOp;

  /**
   * This property should be set to the type of pricing operation that should be executed when
   * an item is added to the order. The operations which are acceptable are defined in the
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
   *
   * @param pAddItemToOrderPricingOp a <code>String</code> value
   */
  public void setAddItemToOrderPricingOp(String pAddItemToOrderPricingOp) {
    mAddItemToOrderPricingOp = pAddItemToOrderPricingOp;
  }

  /**
   * Returns the pricing operation that should be performed when an item is added to the order
   * @return a <code>String</code> value
   * @beaninfo description: The pricing operation that should be performed when an item is added to the order
   *
   */
  public String getAddItemToOrderPricingOp() {
    return mAddItemToOrderPricingOp;
  }

  //-------------------------------------
  // property: AddItemToOrderSuccessURL
  //-------------------------------------
  String mAddItemToOrderSuccessURL;

  /**
   * Sets property AddItemToOrderSuccessURL
   *
   * @param pAddItemToOrderSuccessURL a <code>String</code> value
   */
  public void setAddItemToOrderSuccessURL(String pAddItemToOrderSuccessURL) {
    mAddItemToOrderSuccessURL = pAddItemToOrderSuccessURL;
  }

  /**
   * Returns property AddItemToOrderSuccessURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when AddItemToOrder is successful
   *
   */
  public String getAddItemToOrderSuccessURL() {
    return mAddItemToOrderSuccessURL;
  }

  //-------------------------------------
  // property: AddItemToOrderErrorURL
  //-------------------------------------
  String mAddItemToOrderErrorURL;

  /**
   * Sets property AddItemToOrderErrorURL
   *
   * @param pAddItemToOrderErrorURL a <code>String</code> value
   */
  public void setAddItemToOrderErrorURL(String pAddItemToOrderErrorURL) {
    mAddItemToOrderErrorURL = pAddItemToOrderErrorURL;
  }

  /**
   * Returns property AddItemToOrderErrorURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when AddItemToOrder fails
   *
   */
  public String getAddItemToOrderErrorURL() {
    return mAddItemToOrderErrorURL;
  }

  //-------------------------------------
  // property: AddMultipleItemsToOrderSuccessURL
  //-------------------------------------


  String mAddMultipleItemsToOrderSuccessURL;

  //-------------------------------------
  /**
   * Sets property AddMultipleItemsToOrderSuccessURL
   *
   * @param pAddMultipleItemsToOrderSuccessURL a <code>String</code> value
   **/
  public void setAddMultipleItemsToOrderSuccessURL(String pAddMultipleItemsToOrderSuccessURL) {
    mAddMultipleItemsToOrderSuccessURL = pAddMultipleItemsToOrderSuccessURL;
  }

  //-------------------------------------
  /**
   * Returns property AddMultipleItemsToOrderSuccessURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when AddMultipleItemsToOrder is successful
   *

   **/
  public String getAddMultipleItemsToOrderSuccessURL() {
    return mAddMultipleItemsToOrderSuccessURL;
  }

  //-------------------------------------
  // property: AddMultipleItemsToOrderErrorURL
  //-------------------------------------

  String mAddMultipleItemsToOrderErrorURL;

  //-------------------------------------
  /**
   * Sets property AddMultipleItemsToOrderErrorURL
   *
   * @param pAddMultipleItemsToOrderErrorURL a <code>String</code> value
   **/
  public void setAddMultipleItemsToOrderErrorURL(String pAddMultipleItemsToOrderErrorURL) {
    mAddMultipleItemsToOrderErrorURL = pAddMultipleItemsToOrderErrorURL;
  }

  //-------------------------------------
  /**
   * Returns property AddMultipleItemsToOrderErrorURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when AddMultipleItemsToOrder fails
   *
   **/
  public String getAddMultipleItemsToOrderErrorURL() {
    return mAddMultipleItemsToOrderErrorURL;
  }


  //-------------------------------------
  // property: AddItemToOrderChainId
  //-------------------------------------
  String mAddItemToOrderChainId = null;

  /**
   * Sets property AddItemToOrderChainId
   *
   * @param pAddItemToOrderChainId a <code>String</code> value
   */
  public void setAddItemToOrderChainId(String pAddItemToOrderChainId) {
    mAddItemToOrderChainId = pAddItemToOrderChainId;
  }

  /**
   * Returns property AddItemToOrderChainId
   * @return a <code>String</code> value
   * @beaninfo description: The name of the pipeline chain used to add items to the order
   *
   */
  public String getAddItemToOrderChainId() {
    return mAddItemToOrderChainId;
  }

  //-------------------------------------
  // Property: RemoveItemFromOrderSuccessURL
  //-------------------------------------
  String mRemoveItemFromOrderSuccessURL;

  /**
   * Sets property RemoveItemFromOrderSuccessURL
   *
   * @param pRemoveItemFromOrderSuccessURL a <code>String</code> value
   */
  public void setRemoveItemFromOrderSuccessURL(String pRemoveItemFromOrderSuccessURL) {
    mRemoveItemFromOrderSuccessURL = pRemoveItemFromOrderSuccessURL;
  }

  /**
   * Returns property RemoveItemFromOrderSuccessURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when RemoveItemFromOrder is successful
   *
   */
  public String getRemoveItemFromOrderSuccessURL() {
    return mRemoveItemFromOrderSuccessURL;
  }

  //-------------------------------------
  // property: RemoveItemFromOrderErrorURL
  //-------------------------------------
  String mRemoveItemFromOrderErrorURL;

  /**
   * Sets property RemoveItemFromOrderErrorURL
   *
   * @param pRemoveItemFromOrderErrorURL a <code>String</code> value
   */
  public void setRemoveItemFromOrderErrorURL(String pRemoveItemFromOrderErrorURL) {
    mRemoveItemFromOrderErrorURL = pRemoveItemFromOrderErrorURL;
  }

  /**
   * Returns property RemoveItemFromOrderErrorURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when RemoveItemFromOrder fails
   *
   */
  public String getRemoveItemFromOrderErrorURL() {
    return mRemoveItemFromOrderErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: RemoveItemFromOrderByRelationshipIdSuccessURL
  String mRemoveItemFromOrderByRelationshipIdSuccessURL;

  /**
   * Set the RemoveItemFromOrderByRelationshipIdSuccessURL property.
   * @param pRemoveItemFromOrderByRelationshipIdSuccessURL a <code>String</code> value
   */
  public void setRemoveItemFromOrderByRelationshipIdSuccessURL(String pRemoveItemFromOrderByRelationshipIdSuccessURL) {
    mRemoveItemFromOrderByRelationshipIdSuccessURL = pRemoveItemFromOrderByRelationshipIdSuccessURL;
  }

  /**
   * Return the RemoveItemFromOrderByRelationshipIdSuccessURL property.
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when RemoveItemFromOrderByRelationship is successful
   */
  public String getRemoveItemFromOrderByRelationshipIdSuccessURL() {
    return mRemoveItemFromOrderByRelationshipIdSuccessURL;
  }


  //---------------------------------------------------------------------------
  // property: RemoveItemFromOrderByRelationshipIdErrorURL
  String mRemoveItemFromOrderByRelationshipIdErrorURL;

  /**
   * Set the RemoveItemFromOrderByRelationshipIdErrorURL property.
   * @param pRemoveItemFromOrderByRelationshipIdErrorURL a <code>String</code> value
   */
  public void setRemoveItemFromOrderByRelationshipIdErrorURL(String pRemoveItemFromOrderByRelationshipIdErrorURL) {
    mRemoveItemFromOrderByRelationshipIdErrorURL = pRemoveItemFromOrderByRelationshipIdErrorURL;
  }

  /**
   * Return the RemoveItemFromOrderByRelationshipIdErrorURL property.
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when RemoveItemFromOrderByRelationship fails
   */
  public String getRemoveItemFromOrderByRelationshipIdErrorURL() {
    return mRemoveItemFromOrderByRelationshipIdErrorURL;
  }

  //-------------------------------------
  // property: RemoveAndAddItemToOrderErrorURL
  //-------------------------------------
  String mRemoveAndAddItemToOrderErrorURL;

  /**
   * Sets property RemoveAndAddItemToOrderErrorURL
   *
   * @param pRemoveAndAddItemToOrderErrorURL a <code>String</code> value
   */
  public void setRemoveAndAddItemToOrderErrorURL(String pRemoveAndAddItemToOrderErrorURL) {
    mRemoveAndAddItemToOrderErrorURL = pRemoveAndAddItemToOrderErrorURL;
  }

  /**
   * Returns property RemoveAndAddItemToOrderErrorURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when RemoveAndAddItemFromOrder is successful
   *
   */
  public String getRemoveAndAddItemToOrderErrorURL() {
    return mRemoveAndAddItemToOrderErrorURL;
  }

  //-------------------------------------
  // property: RemoveAndAddItemToOrderSuccessURL
  //-------------------------------------
  String mRemoveAndAddItemToOrderSuccessURL;

  /**
   * Sets property RemoveAndAddItemToOrderSuccessURL
   *
   * @param pRemoveAndAddItemToOrderSuccessURL a <code>String</code> value
   */
  public void setRemoveAndAddItemToOrderSuccessURL(String pRemoveAndAddItemToOrderSuccessURL) {
    mRemoveAndAddItemToOrderSuccessURL = pRemoveAndAddItemToOrderSuccessURL;
  }

  /**
   * Returns property RemoveAndAddItemToOrderSuccessURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when RemoveAndAddItemFromOrder fails
   *
   */
  public String getRemoveAndAddItemToOrderSuccessURL() {
    return mRemoveAndAddItemToOrderSuccessURL;
  }

  //-------------------------------------
  // property: DeleteItemsFromOrderPricingOp
  String mDeleteItemsFromOrderPricingOp;

  /**
   * This property should be set to the type of pricing operation that should be executed when
   * an item is deleted from the order. The operations which are acceptable are defined in the
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
   *
   * @param pDeleteItemsFromOrderPricingOp a <code>String</code> value
   */
  public void setDeleteItemsFromOrderPricingOp(String pDeleteItemsFromOrderPricingOp) {
    mDeleteItemsFromOrderPricingOp = pDeleteItemsFromOrderPricingOp;
  }

  /**
   * Returns the pricing operation that should be performed when an item is deleted from the order
   * @return a <code>String</code> value
   * @beaninfo description: The pricing operation that should be performed when an item is deleted from the order
   *
   */
  public String getDeleteItemsFromOrderPricingOp() {
    return mDeleteItemsFromOrderPricingOp;
  }

  //-------------------------------------
  // property: ModifyOrderPricingOp
  String mModifyOrderPricingOp;

  /**
   * This property should be set to the type of pricing operation that should be executed when
   * the order is updated without adding or removing an item (e.g. quantity change).
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
   *
   * @param pModifyOrderPricingOp a <code>String</code> value
   */
  public void setModifyOrderPricingOp(String pModifyOrderPricingOp) {
    mModifyOrderPricingOp = pModifyOrderPricingOp;
  }

  /**
   * Returns the pricing operation that should be performed when the order is updated
   * @return a <code>String</code> value
   * @beaninfo description: The pricing operation that should be performed when the order is updated
   *
   */
  public String getModifyOrderPricingOp() {
    return mModifyOrderPricingOp;
  }

  //-------------------------------------
  // property: MoveToPurchaseInfoChainId
  //-------------------------------------
  String mMoveToPurchaseInfoChainId = null;

  /**
   * Sets property MoveToPurchaseInfoChainId
   *
   * @param pMoveToPurchaseInfoChainId a <code>String</code> value
   */
  public void setMoveToPurchaseInfoChainId(String pMoveToPurchaseInfoChainId) {
    mMoveToPurchaseInfoChainId = pMoveToPurchaseInfoChainId;
  }

  /**
   * Returns property MoveToPurchaseInfoChainId
   * @return a <code>String</code> value
   * @beaninfo description: The name of the pipeline chain use when moving to the purchase info
   *
   */
  public String getMoveToPurchaseInfoChainId() {
    return mMoveToPurchaseInfoChainId;
  }

  //-------------------------------------
  // property: SetOrderChainId
  //-------------------------------------
  String mSetOrderChainId = null;

  /**
   * Sets property SetOrderChainId
   *
   * @param pSetOrderChainId a <code>String</code> value
   */
  public void setSetOrderChainId(String pSetOrderChainId) {
    mSetOrderChainId = pSetOrderChainId;
  }

  /**
   * Returns property SetOrderChainId
   * @return a <code>String</code> value
   * @beaninfo description: The name of the pipeline chain used to set the order
   *
   */
  public String getSetOrderChainId() {
    return mSetOrderChainId;
  }

  //---------------------------------------------------------------------------
  // property: RepriceOrderErrorURL
  //---------------------------------------------------------------------------
  String mRepriceOrderErrorURL;

  /**
   * Set the RepriceOrderErrorURL property.
   * @param pRepriceOrderErrorURL a <code>String</code> value
   */
  public void setRepriceOrderErrorURL(String pRepriceOrderErrorURL) {
    mRepriceOrderErrorURL = pRepriceOrderErrorURL;
  }

  /**
   * Return the RepriceOrderErrorURL property.
   * @return a <code>String</code> value
   */
  public String getRepriceOrderErrorURL() {
    return mRepriceOrderErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: RepriceOrderSuccessURL
  //---------------------------------------------------------------------------
  String mRepriceOrderSuccessURL;

  /**
   * Set the RepriceOrderSuccessURL property.
   * @param pRepriceOrderSuccessURL a <code>String</code> value
   */
  public void setRepriceOrderSuccessURL(String pRepriceOrderSuccessURL) {
    mRepriceOrderSuccessURL = pRepriceOrderSuccessURL;
  }

  /**
   * Return the RepriceOrderSuccessURL property.
   * @return a <code>String</code> value
   */
  public String getRepriceOrderSuccessURL() {
    return mRepriceOrderSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: setOrderByRelationshipIdSuccessURL
  String mSetOrderByRelationshipIdSuccessURL;

  /**
   * Set the setOrderByRelationshipIdSuccessURL property.
   * @param pSetOrderByRelationshipIdSuccessURL a <code>String</code> value
   */
  public void setSetOrderByRelationshipIdSuccessURL(String pSetOrderByRelationshipIdSuccessURL) {
    mSetOrderByRelationshipIdSuccessURL = pSetOrderByRelationshipIdSuccessURL;
  }

  /**
   * Return the setOrderByRelationshipIdSuccessURL property.
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when SetOrderByRelationshipId is successful
   */
  public String getSetOrderByRelationshipIdSuccessURL() {
    return mSetOrderByRelationshipIdSuccessURL;
  }


  //---------------------------------------------------------------------------
  // property: SetOrderByRelationshipIdErrorURL
  String mSetOrderByRelationshipIdErrorURL;

  /**
   * Set the SetOrderByRelationshipIdErrorURL property.
   * @param pSetOrderByRelationshipIdErrorURL a <code>String</code> value
   */
  public void setSetOrderByRelationshipIdErrorURL(String pSetOrderByRelationshipIdErrorURL) {
    mSetOrderByRelationshipIdErrorURL = pSetOrderByRelationshipIdErrorURL;
  }

  /**
   * Return the SetOrderByRelationshipIdErrorURL property.
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when SetOrderByRelationshipId fails
   */
  public String getSetOrderByRelationshipIdErrorURL() {
    return mSetOrderByRelationshipIdErrorURL;
  }

  //-------------------------------------
  // property: SetOrderSuccessURL
  //-------------------------------------
  String mSetOrderSuccessURL;

  /**
   * Sets property SetOrderSuccessURL
   *
   * @param pSetOrderSuccessURL a <code>String</code> value
   */
  public void setSetOrderSuccessURL(String pSetOrderSuccessURL) {
    mSetOrderSuccessURL = pSetOrderSuccessURL;
  }

  /**
   * Returns property SetOrderSuccessURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when SetOrder is successful
   *
   */
  public String getSetOrderSuccessURL() {
    return mSetOrderSuccessURL;
  }

  //-------------------------------------
  // property: SetOrderErrorURL
  //-------------------------------------
  String mSetOrderErrorURL;

  /**
   * Sets property SetOrderErrorURL
   *
   * @param pSetOrderErrorURL a <code>String</code> value
   */
  public void setSetOrderErrorURL(String pSetOrderErrorURL) {
    mSetOrderErrorURL = pSetOrderErrorURL;
  }

  /**
   * Returns property SetOrderErrorURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when SetOrder fails
   *
   */
  public String getSetOrderErrorURL() {
    return mSetOrderErrorURL;
  }

  //---------------------------------------------------------------------
  // property: checkForChangedQuantity
  boolean mCheckForChangedQuantity = true;

  /**
   * Determine if the FormHandler should check for changes in quantities
   * of commerce items.  If it is set to true, then it will check.  By default
   * this property is set to true.  This only affects checking for changed
   * quantities. The removalCatalogRefId array will still be checked to determine
   * if a user has marked an item for deletion.
   * @return true if form should check for changed quantities
   */
  public boolean isCheckForChangedQuantity() {
    return mCheckForChangedQuantity;
  }

  /**
   * Set the checkForChangedQuantity property.  Set to true if FormHandler should
   * check for changed quantities of items.
   * @param pCheckForChangedQuantity true if formhandler should check for changed
   * quantities.
   */
  public void setCheckForChangedQuantity(boolean pCheckForChangedQuantity) {
    mCheckForChangedQuantity = pCheckForChangedQuantity;
  }

  //-------------------------------------
  // property: RemovalCatalogRefIds
  String [] mRemovalCatalogRefIds;

  /**
   * Sets property RemovalCatalogRefIds
   *
   * @param pRemovalCatalogRefIds a <code>String[]</code> value
   */
  public void setRemovalCatalogRefIds(String [] pRemovalCatalogRefIds) {
    mRemovalCatalogRefIds = pRemovalCatalogRefIds;
  }

  /**
   * Returns property RemovalCatalogRefIds
   * @return a <code>String[]</code> value
   * @beaninfo description: The list of catalog ref ids used during item removal
   *
   */
  public String [] getRemovalCatalogRefIds() {
    return mRemovalCatalogRefIds;
  }

  //---------------------------------------------------------------------------
  // property: removalRelationshipIds
  String[] mRemovalRelationshipIds;

  /**
   * Set the removalRelationshipIds property.
   * @param pRemovalRelationshipIds a <code>String[]</code> value
   */
  public void setRemovalRelationshipIds(String[] pRemovalRelationshipIds) {
    mRemovalRelationshipIds = pRemovalRelationshipIds;
  }

  /**
   * Return the removalRelationshipIds property.
   * @return a <code>String[]</code> value
   */
  public String[] getRemovalRelationshipIds() {
    return mRemovalRelationshipIds;
  }

  //-------------------------------------
  // property: RemovalCommerceIds
  String [] mRemovalCommerceIds;

  /**
   * Sets property RemovalCommerceIds
   *
   * @param pRemovalCommerceIds a <code>String[]</code> value
   */
  public void setRemovalCommerceIds(String [] pRemovalCommerceIds) {
    mRemovalCommerceIds = pRemovalCommerceIds;
  }

  /**
   * Returns property RemovalCommerceIds
   * @return a <code>String[]</code> value
   * @beaninfo description: The list of commerce ids used during item removal
   *
   */
  public String [] getRemovalCommerceIds() {
    return mRemovalCommerceIds;
  }

  //---------------------------------------------------------------------------
  // property: moveToPurchaseInfoByRelIdSuccessURL
  String mMoveToPurchaseInfoByRelIdSuccessURL;

  /**
   * Set the moveToPurchaseInfoByRelIdSuccessURL property.
   * @param pMoveToPurchaseInfoByRelIdSuccessURL a <code>String</code> value
   */
  public void setMoveToPurchaseInfoByRelIdSuccessURL(String pMoveToPurchaseInfoByRelIdSuccessURL) {
    mMoveToPurchaseInfoByRelIdSuccessURL = pMoveToPurchaseInfoByRelIdSuccessURL;
  }

  /**
   * Return the moveToPurchaseInfoByRelIdSuccessURL property.
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when MoveToPurchaseInfoByRelId is successful
   */
  public String getMoveToPurchaseInfoByRelIdSuccessURL() {
    return mMoveToPurchaseInfoByRelIdSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: moveToPurchaseInfoByRelIdErrorURL
  String mMoveToPurchaseInfoByRelIdErrorURL;

  /**
   * Set the moveToPurchaseInfoByRelIdErrorURL property.
   * @param pMoveToPurchaseInfoByRelIdErrorURL a <code>String</code> value
   */
  public void setMoveToPurchaseInfoByRelIdErrorURL(String pMoveToPurchaseInfoByRelIdErrorURL) {
    mMoveToPurchaseInfoByRelIdErrorURL = pMoveToPurchaseInfoByRelIdErrorURL;
  }

  /**
   * Return the moveToPurchaseInfoByRelIdErrorURL property.
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when MoveToPurchaseInfoByRelId fails
   */
  public String getMoveToPurchaseInfoByRelIdErrorURL() {
    return mMoveToPurchaseInfoByRelIdErrorURL;
  }

  //-------------------------------------
  // property: MoveToPurchaseInfoSuccessURL
  //-------------------------------------
  String mMoveToPurchaseInfoSuccessURL;

  /**
   * Sets property MoveToPurchaseInfoSuccessURL
   *
   * @param pMoveToPurchaseInfoSuccessURL a <code>String</code> value
   */
  public void setMoveToPurchaseInfoSuccessURL(String pMoveToPurchaseInfoSuccessURL) {
    mMoveToPurchaseInfoSuccessURL = pMoveToPurchaseInfoSuccessURL;
  }

  /**
   * Returns property MoveToPurchaseInfoSuccessURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when MoveToPurchaseInfo is successful
   *
   */
  public String getMoveToPurchaseInfoSuccessURL() {
    return mMoveToPurchaseInfoSuccessURL;
  }

  //-------------------------------------
  // property: MoveToPurchaseInfoErrorURL
  //-------------------------------------
  String mMoveToPurchaseInfoErrorURL;

  /**
   * Sets property MoveToPurchaseInfoErrorURL
   *
   * @param pMoveToPurchaseInfoErrorURL a <code>String</code> value
   */
  public void setMoveToPurchaseInfoErrorURL(String pMoveToPurchaseInfoErrorURL) {
    mMoveToPurchaseInfoErrorURL = pMoveToPurchaseInfoErrorURL;
  }

  /**
   * Returns property MoveToPurchaseInfoErrorURL
   * @return a <code>String</code> value
   * @beaninfo description: The URL to go to when MoveToPurchaseInfo fails
   *
   */
  public String getMoveToPurchaseInfoErrorURL() {
    return mMoveToPurchaseInfoErrorURL;
  }


  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  //--------------------------------------------------
  // Methods
  //--------------------------------------------------
  
  /**
   * This method is used to add configurable items to the order.  It will first make a call to
   * preAddItemToOrder, then it will call addConfigurableItemToOrder and finally it will call
   * postAddItemToOrder.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleAddConfigurableItemToOrder (DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleAddConfigurableItemToOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        //If any form errors found, redirect to error URL:
        if (! checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preAddItemToOrder(pRequest, pResponse);

          //If any form errors found, redirect to error URL:
          if (! checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse))
            return false;
	
          addConfigurableItemToOrder(pRequest, pResponse);

          //If any form errors found, redirect to error URL:
          if (! checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse))
            return false;
	
          postAddItemToOrder(pRequest, pResponse);
	
          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized
      
        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getAddItemToOrderSuccessURL(),
                                  getAddItemToOrderErrorURL(),
                                  pRequest,
                                  pResponse);
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
   * Adds one or more commerce items to the order (the shopping cart), using
   * <code>configurableItemTypeName</code> as the default commerce item type.
   * The list of catalog items to be added comes from the catalogRefIds array
   * and productId or from the items array.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   *
   * @see #mergeItemInputForAdd
   * @see #doAddItemsToOrder
   */
  protected void addConfigurableItemToOrder(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    boolean valid = mergeItemInputForAdd(pRequest, pResponse, getConfigurableItemTypeName(), false);
    if (valid) {
      doAddItemsToOrder(pRequest, pResponse);
    }
  }
  
  /**
   * Adds one or more commerce items to the order (the shopping cart), using
   * <code>configurableItemTypeName</code> as the default commerce item type.
   * The list of catalog items to be added comes from the catalogRefIds array
   * and productId or from the items array.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pIsGift ignored, covered by the <code>giftlistId</code> and
   *                 <code>giftlistItemId</code> properties
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   *
   * @deprecated replaced by addConfigurableItemToOrder(DynamoHttpServletRequest, DynamoHttpServletResponse)
   */
  protected void addConfigurableItemToOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse,
                                boolean pIsGift)
    throws ServletException, IOException
  {
    addConfigurableItemToOrder(pRequest, pResponse);
  }
  

  /**
   * Retrieve the quanity that should be used for the given catalog reference id or commerce item id.
   * @param pCatalogRefId a <code>String</code> value
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @return a <code>long</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @exception NumberFormatException if an error occurs
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
  protected String getCatalogKey(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    Locale usersLocale = getUserLocale(pRequest, pResponse);
    if (usersLocale == null) {
      return null;
    }
    else {
      return usersLocale.toString();
    }
  }

  /**
   * This is called before any processing is done by the handleSetOrder method.
   * It currently does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preSetOrder(DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This is called after all processing is done by the handleSetOrder method.
   * It currently does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postSetOrder(DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }


  /**
   * This method handles all of the work necessary to save
   * an Order.  It will first call preSetOrder.  Then it will call the
   * modifyOrder method, which does the actual work of recalculating a users order.
   * After the modifyOrder method has been called the runProcessSetOrder will get called
   * to run the appropriate pipeline chain.  After this, the postSetOrder method will get
   * called.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return a <code>boolean</code> value
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleSetOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleSetOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        if (! checkFormRedirect (null, getSetOrderErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preSetOrder(pRequest, pResponse);

          if (! checkFormRedirect (null, getSetOrderErrorURL(), pRequest, pResponse))
            return false;

          try {
            //Validate the changes the user made, and update the Order:
            modifyOrder(pRequest, pResponse);

            //Run the appropriate Pipeline Chain:
            runProcessSetOrder(getOrder(), getUserPricingModels(), getUserLocale(), getProfile(), null);
          }
          catch (Exception exc) {
            processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
          }

          if (! checkFormRedirect (null, getSetOrderErrorURL(), pRequest, pResponse))
            return false;

          postSetOrder(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getSetOrderSuccessURL(), getSetOrderErrorURL(), pRequest, pResponse);
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

  //-------------------------------------
  // method: modifyOrder
  //-------------------------------------
  /**
   * Modify the order (accessed from the <code>order</code> property) based on the changes in the request.
   * This method iterates over each of the current CommerceItems in the order and finds the current
   * quantity submitted. In addition we check to make sure that the item should even be included
   * in the order any longer. If the quantity is greater than zero we adjust the quantity in the CommerceItem
   * and the ShippingGroupCommerceItemRelationship. Otherwise we remove the CommerceItem and the
   * relationships.
   *
   * Items are identified by their catalogRefId (Sku).
   *
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @exception CommerceException if an error occurs
   * @exception RunProcessException if an error occurs
   */
  protected void modifyOrder(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException, RunProcessException
  {
    long quantity;
    Order order = getOrder();
    Map removedItemMap = new HashMap(); // remember which items were removed
    Map changedItemMap = new HashMap(); // remember which item's quantities changed
    if (order == null) {
      String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
      throw new ServletException(msg);
    }
    try {
      synchronized (order) {
        List items = order.getCommerceItems();

        if (items != null) {
          for (int i=0; i < items.size(); i++) {

            CommerceItem item = (CommerceItem)items.get(i);
            String catalogRefId = item.getCatalogRefId();
            if (isCheckForChangedQuantity()) {
              quantity = getQuantity(catalogRefId, pRequest, pResponse);
            }
            else {
              quantity = item.getQuantity();
            }
            // see if this item should be removed, by checking the removalCatalogRefIds
            // list of items to remove if its found, set quantity to 0 and the OrderManager
            // will take care of removing the item
            if ((haveId(catalogRefId, getRemovalCatalogRefIds())))
              quantity = 0;

            if (quantity > 0) {
              //if the user changed the QTY value:
              if (item.getQuantity() != quantity) {
                long oldQuantity = item.getQuantity();
                getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(order,item,quantity);

                //change the Commerce Item quantity:
                item.setQuantity(quantity);
                
                // remember what changed
                changedItemMap.put(item, Long.valueOf(oldQuantity));
              }
            }
            else {
              // The quantity is not above 0, so remove it from the order

              // iterate over all commerce item shipping group relationships
              // and remove handling instructions if required
              ShippingGroupCommerceItemRelationship sgrel = null;
              Iterator iter = item.getShippingGroupRelationships().iterator();
              while (iter.hasNext()){
                sgrel = (ShippingGroupCommerceItemRelationship) iter.next();
                ShippingGroup sg = sgrel.getShippingGroup();
                getHandlingInstructionManager().removeHandlingInstructionsFromShippingGroup(
                  order, sg.getId(), item.getId());
              }
              long qty = item.getQuantity();
              getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, item.getId());
              getCommerceItemManager().removeItemFromOrder(order, item.getId());
              removedItemMap.put(item, Long.valueOf(qty));

              // decrement the counter because or order size has decreased
              i--;
            }
          } // for

          // Allow further changes to order after item quantity
          // adjustment before the reprice
          modifyCommerceItemsProperties( pRequest, pResponse, changedItemMap, removedItemMap );

          Map extraParams = createRepriceParameterMap();
          runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(),
                                 getUserLocale(),getProfile(),extraParams);

          // Allow further processing of order after reprice
          modifyOrderPostReprice( pRequest, pResponse, changedItemMap, removedItemMap );

          Iterator itemIterator = removedItemMap.entrySet().iterator();
          while (itemIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)itemIterator.next(); 
            CommerceItem commerceItem = (CommerceItem)entry.getKey();
            Long longQuantity = (Long) entry.getValue();
            runProcessSendScenarioEvent(order, commerceItem, longQuantity.longValue(),
              ItemRemovedFromOrder.TYPE);
          }

          itemIterator = changedItemMap.entrySet().iterator();
          while (itemIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)itemIterator.next();
            CommerceItem commerceItem = (CommerceItem)entry.getKey();
            Long longQuantity = (Long) entry.getValue();
            runProcessSendScenarioEvent(order, commerceItem, longQuantity.longValue(),
              ItemQuantityChanged.TYPE);
          }

        } // if
      } // synchronized

    }
    catch (NumberFormatException nfe) {
      // invalid number given for quantity of item to add
      String msg = formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse);
      String propertyPath = generatePropertyPath("order");
      addFormException(new DropletFormException(msg, nfe, propertyPath, MSG_INVALID_QUANTITY));
    }
  }
  

  /**
   * This is called before any processing is done by the
   * handleSetOrderByRelationshipId method.  It is currently does nothing.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preSetOrderByRelationshipId(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This is called after all processing is done by the
   * handleSetOrderByRelationshipId method.  It is currently does nothing.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postSetOrderByRelationshipId(DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method is responsible for adjusting the quantities of commerce items that exist in
   * a users shopping cart.  This adjustment is done via calling the
   * modifyOrderByRelationshipId method, then calling the runProcessSetOrder
   * method to run the appropriate pipeline chain.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @return true if the method was handled
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleSetOrderByRelationshipId(DynamoHttpServletRequest pRequest,
                                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleSetOrderByRelationshipId";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        if (! checkFormRedirect (null, getSetOrderByRelationshipIdErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preSetOrderByRelationshipId(pRequest, pResponse);

          if (! checkFormRedirect (null, getSetOrderByRelationshipIdErrorURL(), pRequest, pResponse))
            return false;

          try {
            //Validate the changes the user made, and update the Order:
            modifyOrderByRelationshipId(pRequest, pResponse);

            //Run the appropriate Pipeline Chain:
            runProcessSetOrder(getOrder(), getUserPricingModels(), getUserLocale(), getProfile(), null);
          }
          catch (Exception exc) {
            processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
          }

          if (! checkFormRedirect (null, getSetOrderByRelationshipIdErrorURL(), pRequest, pResponse))
            return false;

          postSetOrderByRelationshipId(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getSetOrderByRelationshipIdSuccessURL(),
                                  getSetOrderByRelationshipIdErrorURL(),
                                  pRequest, pResponse);
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
   * This is called before any processing is done by the
   * handleSetOrderByCommerceId method.  It currently does nothing.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preSetOrderByCommerceId(DynamoHttpServletRequest pRequest,
				      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This is called after all processing is done by the
   * handleSetOrderByCommerceId method.  It currently does nothing.
   *
   * @param pRequest a value of type 'DynamoHttpServletRequest'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postSetOrderByCommerceId(DynamoHttpServletRequest pRequest,
				       DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method is responsible for adjusting the quantities of commerce items that exist in
   * a users shopping cart.  This adjustment is done via calling the
   * modifyOrderByCommerceId method, then calling the runProcessSetOrder
   * method to run the appropriate pipeline chain.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @return true if the method was handled
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleSetOrderByCommerceId(DynamoHttpServletRequest pRequest,
					    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleSetOrderByCommerceId";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        if (! checkFormRedirect (null, getSetOrderErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preSetOrderByCommerceId(pRequest, pResponse);

          if (! checkFormRedirect (null, getSetOrderErrorURL(), pRequest, pResponse))
            return false;

          try {
            //Validate the changes the user made, and update the Order:
            modifyOrderByCommerceId(pRequest, pResponse);

            //Run the appropriate Pipeline Chain:
            runProcessSetOrder(getOrder(), getUserPricingModels(), getUserLocale(), getProfile(), null);
          }
          catch (Exception exc) {
            processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
          }

          if (! checkFormRedirect (null, getSetOrderErrorURL(), pRequest, pResponse))
            return false;

          postSetOrderByCommerceId(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getSetOrderSuccessURL(), getSetOrderErrorURL(), pRequest, pResponse);
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
   * Called before any processing is done by the moveToPurchaseInfo method.
   * It currently does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preMoveToPurchaseInfo(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after all processing is done by the moveToPurchaseInfo method.
   * It currently does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postMoveToPurchaseInfo(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method is called when the user wants to starts the CHECKOUT process
   * for an order.  It will first call preMoveToPurchaseInfo method.  After this
   * has happened it will check for any changes to the order by calling the modifyOrder
   * method.  Then, the runProcessMoveToPurchaseInfo method gets called to run the
   * appropriate pipeline chain.  Finally, the postMoveToPurchaseInfo method will be called.
   *
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return a <code>boolean</code> value
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleMoveToPurchaseInfo(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleMoveToPurchaseInfo";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        if (! checkFormRedirect (null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preMoveToPurchaseInfo(pRequest, pResponse);

          if (! checkFormRedirect (null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse))
            return false;

          try {
            //Validate the changes the user made, and update the Order:
            modifyOrder(pRequest, pResponse);

            //Run the appropriate Pipeline Chain:
            runProcessMoveToPurchaseInfo(getOrder(),getUserPricingModels(),getUserLocale(),getProfile(),null);
          }
          catch (Exception exc) {
            processException(exc, MSG_ERROR_MOVE_TO_PURCHASE_INFO, pRequest, pResponse);
          }

          if (! checkFormRedirect (null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse)) {
            // The cause of the redirect may not have rolled back the transaction,
            // in which case we have to update the order before bailing out.
            // No update happens if the transaction is marked for rollback.
            updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
            return false;
          }

          postMoveToPurchaseInfo(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect(getMoveToPurchaseInfoSuccessURL(), getMoveToPurchaseInfoErrorURL(), pRequest, pResponse);
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
   * This method is called when the user wants to starts the CHECKOUT process
   * for an order.  It will first call preMoveToPurchaseInfo method.  After this
   * has happened it will check for any changes to the order by calling the
   * modifyOrderByRelationshipId method.  Then, the runProcessMoveToPurchaseInfo
   * method gets called to run the
   * appropriate pipeline chain.  Finally, the postMoveToPurchaseInfo method will be called.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return a <code>boolean</code> value
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleMoveToPurchaseInfoByRelId(DynamoHttpServletRequest pRequest,
                                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleMoveToPurchaseInfoByRelId";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        if (! checkFormRedirect (null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preMoveToPurchaseInfo(pRequest, pResponse);

          if (! checkFormRedirect (null, getMoveToPurchaseInfoByRelIdErrorURL(), pRequest, pResponse))
            return false;

          try {
            //Validate the changes the user made, and update the Order:
            modifyOrderByRelationshipId(pRequest, pResponse);

            //Run the appropriate Pipeline Chain:
            runProcessMoveToPurchaseInfo(getOrder(),getUserPricingModels(),getUserLocale(),getProfile(),null);
          }
          catch (Exception exc) {
            processException(exc, MSG_ERROR_MOVE_TO_PURCHASE_INFO, pRequest, pResponse);
          }

          if (! checkFormRedirect (null, getMoveToPurchaseInfoByRelIdErrorURL(), pRequest, pResponse)) {
            // The cause of the redirect may not have rolled back the transaction,
            // in which case we have to update the order before bailing out.
            // No update happens if the transaction is marked for rollback.
            updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
            return false;
          }

          postMoveToPurchaseInfo(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect(getMoveToPurchaseInfoByRelIdSuccessURL(),
                                 getMoveToPurchaseInfoByRelIdErrorURL(),
                                 pRequest, pResponse);
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

  //-------------------------------------
  // method: modifyOrderByRelationshipId
  //-------------------------------------
  /**
   * This method causes the contents of a users order to be adjusted by quantity.
   * Specifically, it will iterate through all of the shipping group commerce item
   * relationships that exist in a users order.  For each of these Shipping group
   * commerce item relationships, see what the quantity that was submitted in the
   * form was.  This is done by calling the getQuantity() method handing it the
   * the shipping group commerce item relationship id.  If the quantity has changed
   * then change the quantity in both the relationship object as well as the commerce
   * item.
   *
   * <P>
   *
   * If the quantity is now 0, or the item appears in the removalRelationshipIds
   * array then the quantity will be put to 0.  If the original quantity
   * of the shipping group commerce item relationship is equal to the quantity
   * of the commerce item then the commerce item and the relationship are removed
   * from the order.  If the quantity is less than the quantity of the commerce item
   * then the relationship is removed and the quantity of the commerce item
   * is adjusted appropriately.
   *
   * <P>
   *
   * This assumes that the type of relationship being used on the
   * ShippingGroupCommerceItemRelationship is of type SHIPPINGQUANTITY.  If it is not,
   * an exception wil be thrown.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @exception CommerceException if an error occurs
   * @exception RunProcessException if an error occurs
   */
  protected void modifyOrderByRelationshipId(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException, RunProcessException
  {
    ShippingGroupCommerceItemRelationship sgCiRel;
    long quantityDifference;
    long quantity;
    long ciQuantity;
    List removalRelationshipIdList = null;
    Map changedItemMap = new HashMap(); // remember which item's quantities changed

    Order order = getOrder();
    if (order == null) {
      String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
      throw new ServletException(msg);
    }
    try {
      synchronized (order) {
        List sgCiRels = getShippingGroupCommerceItemRelationships(order);

        if (sgCiRels != null) {
          for (int i=0; i < sgCiRels.size(); i++) {

            sgCiRel = (ShippingGroupCommerceItemRelationship)sgCiRels.get(i);
            if (sgCiRel.getRelationshipType() != RelationshipTypes.SHIPPINGQUANTITY) {
              String msg = formatUserMessage(MSG_UNSUPPORTED_RELATIONSHIP,
                                             sgCiRel.getRelationshipTypeAsString(),
                                             pRequest, pResponse);
              throw new CommerceException(msg);
            }

            if (isCheckForChangedQuantity()) {
              quantity = getQuantity(sgCiRel.getId(), pRequest, pResponse);
            }
            else {
              quantity = sgCiRel.getQuantity();
            }

            if (getRemovalRelationshipIds() != null
                && haveId(sgCiRel.getId(), mRemovalRelationshipIds))
              quantity = 0;

            if (quantity > 0) {
              //if the user changed the QTY value:
              ciQuantity = sgCiRel.getCommerceItem().getQuantity();
              if (sgCiRel.getQuantity() != quantity) {
                quantityDifference = quantity - sgCiRel.getQuantity();
                // change relationship quantity
                sgCiRel.setQuantity(quantity);
                //change the Commerce Item quantity
                long oldQuantity = sgCiRel.getCommerceItem().getQuantity();
                sgCiRel.getCommerceItem().setQuantity(ciQuantity + quantityDifference);

                // remember what changed
                changedItemMap.put(sgCiRel.getCommerceItem(), Long.valueOf(oldQuantity));
              }
            }
            else {
              // item needs to be deleted because quantity is 0
              // if item not in list of things to delete, add it
              // queue up things to delete and then make call to deleteItemsByRelationshipId
              if ( (getRemovalRelationshipIds() == null) ||
                   !(haveId(sgCiRel.getId(), getRemovalRelationshipIds()))) {
                if (removalRelationshipIdList == null) {
                  removalRelationshipIdList = new ArrayList();
                  if (getRemovalRelationshipIds() != null) {
                    for (int j=0; j<mRemovalRelationshipIds.length; j++)
                      removalRelationshipIdList.add(mRemovalRelationshipIds[j]);
                  }
                }
                removalRelationshipIdList.add(sgCiRel.getId());
              }
            }
          }
          if ((removalRelationshipIdList != null) && (mRemovalRelationshipIds != null))
            mRemovalRelationshipIds = (String[])
              removalRelationshipIdList.toArray(mRemovalRelationshipIds);
          if (mRemovalRelationshipIds != null && mRemovalRelationshipIds.length > 0) {
            deleteItemsByRelationshipId(pRequest, pResponse);
          }
        }

        // Hook to allow further changes to order after item quantity
        // adjustment. Since repricing previously only happened when
        // changedItemMap was populated, the hook method here can
        // return a boolean to force a reprce even when the
        // changedItemMap is empty. This allows the hook to make other
        // changes to the order that require a reprice.
        boolean reprice = modifyCommerceItemsProperties( pRequest, pResponse, changedItemMap,
          null );

        if ( reprice || changedItemMap.size() > 0 ) {
          Map extraParams = createRepriceParameterMap();
          // reprice
          runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(),
                                 getUserLocale(),getProfile(),extraParams);
          
          // Hook to allow processing of order post reprice and before
          // secnario events are fired
          modifyOrderPostReprice( pRequest, pResponse, changedItemMap, null );

          if ( changedItemMap.size() > 0 ) {
            Iterator itemIterator = changedItemMap.entrySet().iterator();
            CommerceItem commerceItem=null;
            Long longQuantity=null;
            while (itemIterator.hasNext()) {
              Map.Entry entry = (Map.Entry)itemIterator.next();
              commerceItem = (CommerceItem)entry.getKey();
              longQuantity = (Long) entry.getValue();
              runProcessSendScenarioEvent(order, commerceItem, longQuantity.longValue(), ItemQuantityChanged.TYPE);
            }
          }
        }
      }
    }
    catch (NumberFormatException nfe) {
      // invalid number given for quantity of item to add
      String msg = formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse);
      String propertyPath = generatePropertyPath("order");
      addFormException(new DropletFormException(msg, nfe, propertyPath, MSG_INVALID_QUANTITY));
    }
  }


  /**
   * This method is called when the user wants to starts the CHECKOUT process for an order.  It
   * will first call preMoveToPurchaseInfo method.  After this has happened it will check for
   * any changes to the order by calling the modifyOrderByCommerceId method.  Then, the
   * runProcessMoveToPurchaseInfo method gets called to run the appropriate pipeline chain.
   * Finally, the postMoveToPurchaseInfo method will be called.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return a <code>boolean</code> value
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io */
  public boolean handleMoveToPurchaseInfoByCommerceId(DynamoHttpServletRequest pRequest,
						      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleMoveToPurchaseInfoByCommerceId";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        if (! checkFormRedirect (null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preMoveToPurchaseInfo(pRequest, pResponse);

          if (! checkFormRedirect (null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse))
            return false;

          try {
            //Validate the changes the user made, and update the Order:
            modifyOrderByCommerceId(pRequest, pResponse);

            //Run the appropriate Pipeline Chain:
           runProcessMoveToPurchaseInfo(getOrder(), getUserPricingModels(), getUserLocale(),
                                        getProfile(), null);
          }
          catch (Exception exc) {
            processException(exc, MSG_ERROR_MOVE_TO_PURCHASE_INFO, pRequest, pResponse);
          }

          if (! checkFormRedirect (null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse)) {
            // The cause of the redirect may not have rolled back the transaction,
            // in which case we have to update the order before bailing out.
            // No update happens if the transaction is marked for rollback.
            updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
            return false;
          }

          postMoveToPurchaseInfo(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect(getMoveToPurchaseInfoSuccessURL(),
                                 getMoveToPurchaseInfoErrorURL(),
                                 pRequest, pResponse);
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
   * This method is invoked from the modifyOrder*() methods in order
   * to allow subclasses an opportunity to implement additional
   * processing after the quantity of items in the order has been
   * changed.
   * 
   * @param pRequest the request
   * @param pResponse the response
   * @param pQuantityChangedItems a map of items who's quantities have
   * been changed. The map key is the commerce item, and the value is
   * the old quantity.
   * @param pRemovedItems a map of items that have been removed from
   * the order because their quantity was set to something less than
   * or equal to zero.  1. The key is the commerce item and the value
   * is the old quantity. This parameter may be null.
   * @return true if order should be repriced (used only when called
   * from modifyOrderByRelationshipId())
   */
  protected boolean modifyCommerceItemsProperties( DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse, Map pQuantityChangedItems, Map pRemovedItems )
    throws IOException, CommerceException, ServletException, RunProcessException
  {
    return false;
  }

  /**
   *
   * @param pRequest the request
   * @param pResponse the response
   * @param pQuantityChangedItems a map of items who's quantities have
   * been changed. The map key is the commerce item, and the value is
   * the old quantity.
   * @param pRemovedItems a map of items that have been removed from
   * the order because their quantity was set to something less than
   * zero.  1. The key is the commerce item and the value is the old
   * quantity. This parameter may be null.
   */
  protected void modifyOrderPostReprice( DynamoHttpServletRequest pRequest,
    DynamoHttpServletResponse pResponse, Map pQuantityChangedItems, Map pRemovedItems )
    throws IOException, CommerceException, ServletException, RunProcessException    
  {
  }

  //-------------------------------------
  // method: modifyOrderByCommerceId
  //-------------------------------------
  /**
   * Modify the order (accessed from the <code>order</code> property) based on the changes in
   * the request.  This method iterates over each of the current CommerceItems in the order and
   * finds the current quantity submitted. Note, this replaces the use of modifyOrder method
   * because that method made use of the catalogRefId to identify the commerce item to be
   * modified instead of using the commerce item id itself.  In addition we check to make sure
   * that the item should even be included in the order any longer. If the quantity is greater
   * than zero we adjust the quantity in the CommerceItem and the
   * ShippingGroupCommerceItemRelationship. Otherwise we remove the CommerceItem and the
   * relationships.
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @exception CommerceException if an error occurs
   * @exception RunProcessException if an error occurs */
  protected void modifyOrderByCommerceId(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException, RunProcessException
  {
    long quantity;
    Order order = getOrder();
    Map removedItemMap = new HashMap(); // remember which items were removed
    Map changedItemMap = new HashMap(); // remember which item's quantities changed
    if (order == null) {
      String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
      throw new ServletException(msg);
    }
    try {
      synchronized (order) {
        List items = order.getCommerceItems();

        if (items != null) {
          for (int i=0; i < items.size(); i++) {

            CommerceItem item = (CommerceItem)items.get(i);
	    String commerceItemId = item.getId();
            if (isCheckForChangedQuantity()) {
              quantity = getQuantity(commerceItemId, pRequest, pResponse);
            }
            else {
              quantity = item.getQuantity();
            }
            // see if this item should be removed, by checking the removalCommerceIds
            // list of items to remove. If it is found, set quantity to 0 and the OrderManager
            // will take care of removing the item.
            if (haveId(commerceItemId, getRemovalCommerceIds()))
              quantity = 0;

            if (quantity > 0) {
              //if the user changed the QTY value:
              if (item.getQuantity() != quantity) {
                long oldQuantity = item.getQuantity();
                getPurchaseProcessHelper().adjustItemRelationshipsForQuantityChange(order,item,quantity);
                //change the Commerce Item quantity:
                item.setQuantity(quantity);
                // remember what changed
                changedItemMap.put(item, Long.valueOf(oldQuantity));
              }
              else {
              }
            }
            else {
              // The quantity is not above 0, so remove it from the order

              // iterate over all commerce item shipping group relationships
              // and remove handling instructions if required
              ShippingGroupCommerceItemRelationship sgrel = null;
              Iterator iter = item.getShippingGroupRelationships().iterator();
              while (iter.hasNext()){
                sgrel = (ShippingGroupCommerceItemRelationship) iter.next();
                ShippingGroup sg = sgrel.getShippingGroup();
                getHandlingInstructionManager().removeHandlingInstructionsFromShippingGroup(order, sg.getId(), item.getId());
              }
              long qty = item.getQuantity();
              getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, item.getId());
              getCommerceItemManager().removeItemFromOrder(order, item.getId());
              removedItemMap.put(item, Long.valueOf(qty));

              // decrement the counter because or order size has decreased
              i--;
            }
          } // for

          // Hook to allow further changes to order after item
          // quantity adjustment.
          modifyCommerceItemsProperties( pRequest, pResponse, changedItemMap, removedItemMap );

          Map extraParams = createRepriceParameterMap();
          runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(),
                                 getUserLocale(),getProfile(),extraParams);

          // Hook to allow further changes to order after item
          // quantity adjustment.
          modifyOrderPostReprice( pRequest, pResponse, changedItemMap, removedItemMap );

          Iterator itemIterator = removedItemMap.entrySet().iterator();
          while (itemIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)itemIterator.next();
            CommerceItem commerceItem = (CommerceItem)entry.getKey();
            Long longQuantity = (Long) entry.getValue();
            runProcessSendScenarioEvent(order, commerceItem, longQuantity.longValue(), ItemRemovedFromOrder.TYPE);
          }

          itemIterator = changedItemMap.entrySet().iterator();
          while (itemIterator.hasNext()) {
            Map.Entry entry = (Map.Entry)itemIterator.next();
            CommerceItem commerceItem = (CommerceItem)entry.getKey();
            Long longQuantity = (Long) entry.getValue();
            runProcessSendScenarioEvent(order, commerceItem, longQuantity.longValue(), ItemQuantityChanged.TYPE);
          }
        } // if
      } // synchronized

    }
    catch (NumberFormatException nfe) {
      // invalid number given for quantity of item to add
      String msg = formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse);
      String propertyPath = generatePropertyPath("order");
      addFormException(new DropletFormException(msg, nfe, propertyPath, MSG_INVALID_QUANTITY));
    }
  }

  /**
   * Get a list of all the shippingGroupCommerceItemRelationships that are
   * contained within a particular order.  This is done by getting the list
   * of commerce item relationships and shipping group relationships and then taking
   * the intersection of the two.
   *
   * @param pOrder the order whose relationships will be obtained
   * @return the list of shipping group commerce item relationships in the order
   * @exception CommerceException if an error occurs
   */
  protected List getShippingGroupCommerceItemRelationships(Order pOrder)
    throws CommerceException
  {
    List ciRels = getCommerceItemManager().getAllCommerceItemRelationships(pOrder);
    List sgRels = getShippingGroupManager().getAllShippingGroupRelationships(pOrder);
    ciRels.retainAll(sgRels);
    return ciRels;
  }

  /**
   * Returns true if the given id is within the supplied id array
   */
  final boolean haveId(String pId, String [] pIds) {
    if ((pIds != null) && (pId != null)) {
      int length = pIds.length;
      for (int c=0; c<length; c++) {
        if (pId.equals(pIds[c])) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * This method is responsible for deleting items from the order by shipping group
   * commerce item relationship id.  It will iterate through the shipping group commerce item
   * relationship ids that are found in the removalShippingGroupCommerceItemRelIds property,
   * operating on each one.  It will first ensure that the relationship type is of type
   * RelatinoshipType.SHIPPINGQUANTITY, if its not an error is logged.  Next, one of two
   * conditions can exist...
   *
   * <UL>
   *    <LI>If the quantity of the relationship is greater than or equal to the quantity
   *        of the commerce item then the item and all the relationships should be removed
   *        from the order
   *    <LI>If the quantity of the relationship is less than the quantity of the commerce item
   *        then the commerce item has relationships to other shipping groups.  In this case,
   *        the quantity of the commerce item should be reduced and the relationship
   *        removed.
   * </UL>
   *
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   * @exception CommerceException if an error occurs
   */
  protected void deleteItemsByRelationshipId(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException
  {
    ShippingGroupCommerceItemRelationship sgCiRel;
    long shippingQuantity;
    CommerceItem ci;
    Map changedItemMap = null; // remember which item's quantities changed

    Order order = getOrder();

    if (mRemovalRelationshipIds != null &&
        mRemovalRelationshipIds.length > 0) {
      try {

        for (int i=0; i<mRemovalRelationshipIds.length; i++) {
          sgCiRel = (ShippingGroupCommerceItemRelationship)
            order.getRelationship((mRemovalRelationshipIds[i]));
          if (sgCiRel.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
            ci = sgCiRel.getCommerceItem();
            getHandlingInstructionManager().removeHandlingInstructionsFromShippingGroup(order,
                                                                          sgCiRel.getShippingGroup().getId(),
                                                                          ci.getId());
            if (sgCiRel.getQuantity() >= ci.getQuantity()) {
              // remove item from order
              long qty = ci.getQuantity();
              getCommerceItemManager().removeAllRelationshipsFromCommerceItem(order, ci.getId());
              getCommerceItemManager().removeItemFromOrder(order, ci.getId());
              Map extraParams = createRepriceParameterMap();
              runProcessRepriceOrder(getDeleteItemsFromOrderPricingOp(), order,
                                     getUserPricingModels(), getUserLocale(),
                                     getProfile(),extraParams);
              runProcessSendScenarioEvent(order, ci, qty, ItemRemovedFromOrder.TYPE);

            }
            else {
              // don't remove commerce item from order, reduce quantity and remove relationship
              long oldQuantity = ci.getQuantity();
              ci.setQuantity(ci.getQuantity() - sgCiRel.getQuantity());
              getCommerceItemManager().removeItemQuantityFromShippingGroup(order, ci.getId(),
                                                                    sgCiRel.getShippingGroup().getId(), sgCiRel.getQuantity());

              // remember what changed
              if (changedItemMap == null)
                changedItemMap= new HashMap();
              changedItemMap.put(ci, Long.valueOf(oldQuantity));
            }
          }
          else {
            // unsupported relationship type log error
            if (isLoggingError()) {
              String msg = formatUserMessage(MSG_UNSUPPORTED_RELATIONSHIP,
                                             sgCiRel.getRelationshipTypeAsString(),
                                             pRequest, pResponse);
              throw new CommerceException(msg);
            }
          }
        }

        if(changedItemMap != null) {
            Map extraParams = createRepriceParameterMap();
          // reprice
          runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(),
                                 getUserLocale(),getProfile(),extraParams);

          if (changedItemMap != null) {
            Iterator itemIterator = changedItemMap.entrySet().iterator();
            CommerceItem commerceItem=null;
            Long longQuantity=null;
            while (itemIterator.hasNext()) {
              Map.Entry entry = (Map.Entry)itemIterator.next();
              commerceItem = (CommerceItem)entry.getKey();
              longQuantity = (Long) entry.getValue();
              runProcessSendScenarioEvent(order, commerceItem, longQuantity.longValue(), ItemQuantityChanged.TYPE);
            }
          }
        }

        synchronized (order) {
          // update the order in the repository
          getOrderManager().updateOrder(order);
        }
      }
      catch (Exception exc) {
        processException(exc, MSG_ERROR_REMOVING_ITEM, pRequest, pResponse);
      }
    }
  }

  /**
   * Delete all items from the order whose id appears in the RemovalCommerceIds
   * property.
   *
   * @param pRequest servlet request object
   * @param pResponse servlet response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void deleteItems(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    String commerceId;
    Order order = getOrder();

    Map extraParams = createRepriceParameterMap();

    try {
      List deletedSkus = 
        getPurchaseProcessHelper().deleteItems(
          order, 
          getRemovalCommerceIds(), 
          getUserPricingModels(), 
          getUserLocale(), 
          getProfile(),
          this,extraParams);
  
      setDeletedSkus(deletedSkus);
    }
    catch (CommerceException ce) {
      processException(ce, MSG_ERROR_REMOVING_ITEM, pRequest, pResponse);
    }
  }

  //----------------------------------------
  // AddItemToOrder
  //----------------------------------------

  /**
   * Called before any work is done by the handleAddItemToOrder method.  It currently
   * does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preAddItemToOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after all work is done by the handleAddItemToOrder method.  It currently
   * does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postAddItemToOrder(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method is used to add items to the order.  It will first make a call to
   * preAddItemToOrder, then it will call addItemToOrder and finally it will call
   * postAddItemToOrder.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return a <code>boolean</code> value
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleAddItemToOrder (DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleAddItemToOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        //If any form errors found, redirect to error URL:
        if (! checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preAddItemToOrder(pRequest, pResponse);

          //If any form errors found, redirect to error URL:
          if (! checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse))
            return false;

          addItemToOrder(pRequest, pResponse);

          //If any form errors found, redirect to error URL:
          if (! checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse))
            return false;

          postAddItemToOrder(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getAddItemToOrderSuccessURL(),
                                  getAddItemToOrderErrorURL(),
                                  pRequest,
                                  pResponse);
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
   * Adds one or more commerce items to the order (the shopping cart), using
   * <code>commerceItemType</code> as the default commerce item type.
   * The list of catalog items to be added comes from the catalogRefIds array
   * and productId or from the items array.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   *
   * @see #mergeItemInputForAdd
   * @see #doAddItemsToOrder
   */
  protected void addItemToOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    boolean valid = mergeItemInputForAdd(pRequest, pResponse, getCommerceItemType(), false);
    if (valid) {
      doAddItemsToOrder(pRequest, pResponse);
    }
  }

  /**
   * Adds one or more commerce items to the order (the shopping cart), using
   * <code>commerceItemType</code> as the default commerce item type.
   * The list of catalog items to be added comes from the catalogRefIds array
   * and productId or from the items array.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pIsGift ignored, covered by the <code>giftlistId</code> and
   *                 <code>giftlistItemId</code> properties
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   *
   * @deprecated replaced by addItemToOrder(DynamoHttpServletRequest, DynamoHttpServletResponse)
   */
  protected void addItemToOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse,
                                boolean pIsGift)
    throws ServletException, IOException
  {
    addItemToOrder(pRequest, pResponse);
  }

  /**
   * Construct or fill in default values in an <code>items</code> array containing
   * input values for the methods involved in adding items to orders.
   *
   * <p>
   * Most input validation occurs here, for example:
   * <ul>
   * <li>If the items property is non-null, items[*].quantity must be
   *     greater than or equal to zero.</li>
   * <li>If the items property is null, item quantities must be greater than zero.</li>
   * <li>Either items or catalogRefIds must be non-null, but not both.</li>
   * <li><code>validateShippingGroup</code> is called.</li>
   * </ul>
   * Quantity errors prevent further processing only if
   * <code>invalidQuantityCancelsAdd</code> is <code>true</code>.
   * </p>
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pCommerceItemType the type to use for creating new commerce items
   * @param pUseProductIds <code>true</code> if input comes from
   *                       <code>productIds</code> rather than <code>productId</code>
   * @return <code>true</code> if inputs are valid, <code>false</code> if not
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   *
   * @see #validateShippingGroupForAdd
   */
  protected boolean mergeItemInputForAdd(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse,
                                         String pCommerceItemType,
                                         boolean pUseProductIds)
    throws ServletException, IOException
  {
    String [] skuIds = getCatalogRefIds();
    String [] productIds = getProductIds();

    if (getItems() == null) {
      // There is no items array. The form supplied input in catalogRefIds
      // and its friends. The form is adding a single item or using the
      // older style of adding multiple items. We perform sanity checks
      // on the input and convert the input to an items array.
      //

      if ((skuIds == null) || (skuIds.length == 0)) {
        String msg = formatUserMessage(MSG_NO_ITEMS_TO_ADD, pRequest, pResponse);
        String propertyPath = generatePropertyPath("catalogRefIds");
        addFormException(new DropletFormException(msg, propertyPath, MSG_NO_ITEMS_TO_ADD));
        return false;
      }

      if (pUseProductIds) {
        if ((productIds == null) || (productIds.length == 0)) {
          String msg = formatUserMessage(MSG_NO_ITEMS_TO_ADD, pRequest, pResponse);
          String propertyPath = generatePropertyPath("productIds");
          addFormException(new DropletFormException(msg, propertyPath, MSG_NO_ITEMS_TO_ADD));
          return false;
        }
        if (productIds.length != skuIds.length) {
          String msg = formatUserMessage(MSG_ERROR_ADDING_TO_ORDER, pRequest, pResponse);
          addFormException(new DropletException(msg, MSG_ERROR_ADDING_TO_ORDER));
          return false;
        }
      }

      // Allocate an items array and fill it in from other properties.
      //
      setAddItemCount(skuIds.length);
      for (int index = 0; index < skuIds.length; index++) {
        AddCommerceItemInfo input = getItems()[index];

        input.setCatalogRefId(skuIds[index].trim());

        try {
          long quantity = getQuantity(skuIds[index], pRequest, pResponse);
          if (quantity <= 0) {
            // If the quantity is 0 it is possible this is due to the fact that the item was
            // just removed by the deleteItems call. Consult the deletedSkus list and
            // determine if that sku was a part of the deleted skus. If it was we will skip
            // this error.
            //
            List deletedSkus = getDeletedSkus();
            if (deletedSkus == null || !deletedSkus.contains(skuIds[index])) {
              String msg = formatUserMessage(MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO, pRequest, pResponse);
              String propertyPath = generatePropertyPath("quantity");
              addFormException(new DropletFormException(msg, propertyPath, MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO));
              if (getInvalidQuantityCancelsAdd()) {
                return false;
              }
            }
          }
          input.setQuantity(quantity);
        }
        catch (NumberFormatException nfe) {
          // Invalid number given for quantity of item to add
          //
          String msg = formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse);
          String propertyPath = generatePropertyPath("quantity");
          addFormException(new DropletFormException(msg, nfe, propertyPath, MSG_INVALID_QUANTITY));
          return false;
        }

        String productId;
        if (pUseProductIds) {
          productId = productIds[index];
        }
        else {
          productId = getProductId();
        }
        input.setProductId(productId);

        input.setSiteId(getSiteId());

        input.setGiftlistId(getGiftlistId());
        input.setGiftlistItemId(getGiftlistItemId());
      }
    }
    else {
      // There is an items array. Make sure the form didn't also fill
      // in the old-style sku ID and product ID properties.
      //
      if ((skuIds != null) && (skuIds.length > 0)) {
        String msg = formatUserMessage(MSG_AMBIGUOUS_INPUT_FOR_ADD, pRequest, pResponse);
        String propertyPath = generatePropertyPath("catalogRefIds");
        addFormException(new DropletFormException(msg, propertyPath, MSG_AMBIGUOUS_INPUT_FOR_ADD));
        return false;
      }
      if (pUseProductIds) {
        if ((productIds != null) && (productIds.length > 0)) {
          String msg = formatUserMessage(MSG_AMBIGUOUS_INPUT_FOR_ADD, pRequest, pResponse);
          String propertyPath = generatePropertyPath("productIds");
          addFormException(new DropletFormException(msg, propertyPath, MSG_AMBIGUOUS_INPUT_FOR_ADD));
          return false;
        }
      }
      else if (getProductId() != null) {
        String msg = formatUserMessage(MSG_AMBIGUOUS_INPUT_FOR_ADD, pRequest, pResponse);
        String propertyPath = generatePropertyPath("productId");
        addFormException(new DropletFormException(msg, propertyPath, MSG_AMBIGUOUS_INPUT_FOR_ADD));
        return false;
      }

      // Negative quantities need error messages and may or may not kill the add operation.
      // Zero quantities do not generate messages because that's the way the form
      // tells us which array entries to skip.
      //
      long total = 0;
      for (int index = 0; index < getItems().length; index++) {
        long quantity = getItems()[index].getQuantity();
        if (quantity < 0) {
          String msg = formatUserMessage(MSG_QUANTITY_LESSTHAN_ZERO, pRequest, pResponse);
          String propertyPath = generatePropertyPath("items[" + index + "].quantity");
          addFormException(new DropletFormException(msg, propertyPath, MSG_QUANTITY_LESSTHAN_ZERO));
          if (getInvalidQuantityCancelsAdd()) {
            return false;
          }
        }
        else {
          total += quantity;
        }
      }

      // Report an error if all of the item quantities are missing or invalid.
      //
      if (total == 0) {
        if ( isLoggingDebug() ) {
          logDebug( "items array with no items to add");
        }
        String msg = formatUserMessage(MSG_NO_ITEMS_TO_ADD, pRequest, pResponse);
        String propertyPath = generatePropertyPath("items");
        addFormException(new DropletFormException(msg, propertyPath, MSG_NO_ITEMS_TO_ADD));
        return false;
      }
    }

    // Check the shipping group for the new items.
    //
    if (!validateShippingGroupForAdd(pRequest, pResponse)) {
      return false;
    }

    // Fill in values for defaultable properties if they weren't
    // specified for individual items.
    //
    for (int index = 0; index < getItems().length; index++) {
      AddCommerceItemInfo input = getItems()[index];
      if (input.getCommerceItemType() == null) {
        input.setCommerceItemType(pCommerceItemType);
      }
      if (StringUtils.isBlank(input.getSiteId())) {
        input.setSiteId(getSiteId());
      }
      boolean ok = mergeValueDictionaries(getValue(), input.getValue(), index, pRequest, pResponse, null);
      if (!ok) {
        return false;
      }
    }

    return true;
  }

  /**
   * Merges the common value dictionary into one item-specific value dictionary.
   * If a key from the common dictionary already appears in the item dictionary
   * and both entries are child dictionaries, the children are merged. In
   * non-dictionary cases, the entry in the item dictionary is not modified
   * unless it is null or an empty String.
   *
   * @param pCommonValues the common dictionary
   * @param pItemValues the item-specific dictionary
   * @param pItemIndex the index of the <code>items</code> array element that
   *                   owns the item-specific dictionary
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pPropertyPathPrefix the name prefix for entries in child dictionaries
   *                            (<code>null</code> for the top level dictionaries)
   * @return true if the merge was successful, false if an error occurred
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   **/	  
  protected boolean mergeValueDictionaries(Dictionary pCommonValues, 
                                           Dictionary pItemValues, 
                                           int pItemIndex,
                                           DynamoHttpServletRequest pRequest,
                                           DynamoHttpServletResponse pResponse,
                                           String pPropertyPathPrefix) 
    throws ServletException, IOException
  {
    // Loop through all the common values
    //
    for (Enumeration enumeration = pCommonValues.keys(); enumeration.hasMoreElements(); ) {
      String key = (String)enumeration.nextElement();
      String fullPropertyName = (pPropertyPathPrefix == null) ? key : 
                                  pPropertyPathPrefix + '.' + key;

      Object commonValue = pCommonValues.get(key);
      Object itemValue = pItemValues.get(key);
      if (itemValue instanceof Dictionary && commonValue instanceof Dictionary) {
        // Recursively merge same-named child dictionaries
        //
        boolean ok = mergeValueDictionaries((Dictionary)commonValue, (Dictionary)itemValue, pItemIndex, 
                                            pRequest, pResponse, fullPropertyName);
        if (!ok) {
          return false;
        }
      }
      else if (itemValue == null ||
               itemValue instanceof String && ((String)itemValue).length() == 0) {
        // The common key doesn't exist in the item dictionary or exists with
        // an empty string value. Copy the common value.
        //
        if (isLoggingDebug()) {
          logDebug("mergeValueDictionaries: adding common property " + fullPropertyName + 
                   " with value " + commonValue);
        }
        try {
          DynamicBeans.setPropertyValue(pItemValues, key, commonValue);
        }
        catch (PropertyNotFoundException exc) {
          // The Dictionary version of setPropertyValue never throws this exception,
          // so in theory we can't get here. Generate a user message just in case.
          //
          String msg = formatUserMessage(MSG_ERROR_ADDING_TO_ORDER, pRequest, pResponse);
          addFormException(new DropletFormException(msg,
                            generatePropertyPath("items[" + pItemIndex + "].value." + fullPropertyName),
                            MSG_ERROR_ADDING_TO_ORDER));
          if (isLoggingError()) {
            logError(exc);
          }
          return false;
        }
      }
      else if (isLoggingDebug()) {
        // The common key already exists in the item dictionary
        //
        logDebug("mergeValueDictionaries: keeping item property " + fullPropertyName + 
                 " with value " + itemValue);
      }
    }
    return true;
  }

  /**
   * Validate shipping information for add operations. The
   * shippingGroup property must be non-null.
   * 
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return <code>true</code> if inputs are valid, <code>false</code> if not
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected boolean validateShippingGroupForAdd(DynamoHttpServletRequest pRequest,
                                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if (getShippingGroup() == null) {
      String msg = formatUserMessage(MSG_ERROR_ADDING_TO_ORDER, pRequest, pResponse);
      addFormException(new DropletException(msg, MSG_ERROR_ADDING_TO_ORDER));
      return false;
    }
    return true;
  }

  /**
   * Add one or more items to the order (the shopping cart). Details about
   * the items are found in the <code>items</code> array. Items with a quantity
   * of 0 or less are not added.
   *
   * <p>
   * Each commerce item is created by calling a series of small methods:
   * <ul>
   * <li>createCommerceItem - create an item and add it to the user's order</li>
   * <li>setCommerceItemProperties - transfer values from the value Dictionary to the item</li>
   * <li>addItemToShippingGroup - create a relationship between the item and a shipping group</li>
   * <li>createConfigurableSubitems - override to create subitems for a configurable base item</li>
   * <li>processGiftAddition - special handling if the item is being ordered from a giftlist</li>
   * <li>runProcessRepriceOrder - reprice the order</li>
   * <li>runProcessAddItemToOrder - run the optional pipeline named by addItemToOrderChainId</li>
   * <li>runProcessSendScenarioEvent - send an ItemAddedToOrder scenario event</li>
   * </ul>
   * </p>
   * <p>
   * Input property validation is the caller's responsibility.
   * </p>
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void doAddItemsToOrder(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if(isLoggingDebug())
      logDebug("Starting doAddItemsToOrder");

    // Fetch the order
    Order order = getOrder();
    if (order == null) {
      String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
      throw new ServletException(msg);
    }
    
    
    Map extraParams = createRepriceParameterMap();
    try {
      getPurchaseProcessHelper().addItemsToOrder(order, getShippingGroup(), getProfile(), 
              									getItems(), getUserLocale(), 
              									getCatalogKey(pRequest, pResponse), 
              									getUserPricingModels(), this,extraParams);
    }
    catch (CommerceException ce) {
      processException(ce, MSG_ERROR_ADDING_TO_ORDER, pRequest, pResponse);
    }
  }

  /**
   * Create a commerce item and add it to the order.
   *
   * @param pItemIndex the index of the <code>items</code> array element that
   *                   supplies input for the new item
   * @param pCatalogKey the catalog key reference (locale) for the new item
   * @return CommerceItem that was created
   * @exception CommerceException if there was an error while executing the code
   */
  protected CommerceItem createCommerceItem(int pItemIndex, String pCatalogKey)
    throws CommerceException
  {
    AddCommerceItemInfo input = getItems()[pItemIndex];
    CommerceItemManager cimgr = getCommerceItemManager();
    CommerceItem ci = cimgr.createCommerceItem(input.getCommerceItemType(),
                                               input.getCatalogRefId(), null,
                                               input.getProductId(), null,
                                               input.getQuantity(),
                                               pCatalogKey,
                                               null);
    if (isLoggingDebug()) {
      logDebug("Item created with ID " + ci.getId());
    }

    CommerceItem ciFinal = cimgr.addItemToOrder(getOrder(), ci);
    if (isLoggingDebug() && ciFinal != ci) {
      logDebug("New item " + ci.getId() + " merged into existing item " + ciFinal.getId());
    }

    return ciFinal;
  }

  /**
   * Fill in "extra" commerce item properties. This implementation
   * copies properties from the optional value dictionary to the commerce item.
   *
   * @param pItem the CommerceItem
   * @param pItemIndex the index of the <code>items</code> array element that
   *                   supplies input for the commerce item
   * @exception CommerceException if there was an error while executing the code
   */
  protected void setCommerceItemProperties(CommerceItem pItem, int pItemIndex)
    throws CommerceException
  {
    Dictionary values = getItems()[pItemIndex].getValue();
    applyValueDictionary(pItem, values, null);
  }

  /**
   * Copy properties from the value dictionary or one of its descendents.
   *
   * @param pBean the object to which to copy dictionary values
   * @param pValues the dictionary from which to copy values
   * @param pPropertyPathPrefix the name prefix for dictionary entries
   *                            (<code>null</code> for the top level dictionary)
   * @exception CommerceException if there was an error while executing the code
   */
  protected void applyValueDictionary(Object pBean, Dictionary pValues, String pPropertyPathPrefix)
    throws CommerceException
  {
    for (Enumeration enumeration = pValues.keys(); enumeration.hasMoreElements(); ) {
      String key = (String)enumeration.nextElement();
      Object value = pValues.get(key);
      String fullPropertyName = (pPropertyPathPrefix == null) ? key : 
                                  pPropertyPathPrefix + '.' + key;
      if (isLoggingDebug()) {
        logDebug("applyValueDictionary: copying property " + fullPropertyName + 
                 " with value " + value);
      }
      try {
        if (value instanceof Dictionary) {
          Object subbean = DynamicBeans.getPropertyValue(pBean, key);
          if (subbean == null) {
            throw new CommerceException("Null value for property " + fullPropertyName);
          }
          applyValueDictionary(subbean, (Dictionary)value, fullPropertyName);
        }
        else if (value instanceof String) {
          // Attempt to convert string value to whatever is appropriate
          //
          DynamicBeans.setPropertyValueFromString(pBean, key, (String)value);
        }
        else {
          DynamicBeans.setPropertyValue(pBean, key, value);
        }
        if (pBean instanceof ChangedProperties) {
          ((ChangedProperties)pBean).addChangedProperty(key);
        }
      }
      catch (Exception exc) {
        throw new CommerceException(exc);
      }
    }
  }

  /**
   * Associate a commerce item with the shipping group specified by
   * the <code>shippingGroup</code> property.
   *
   * @param pItem the CommerceItem
   * @param pItemIndex the index of the <code>items</code> array element that
   *                   supplies input for the commerce item
   * @exception CommerceException if there was an error while executing the code
   */
  protected void addItemToShippingGroup(CommerceItem pItem, int pItemIndex)
    throws CommerceException
  {
    // Pull the quantity from the original input rather than from the item
    // in case our new item was merged into a matching old item.
    //
    AddCommerceItemInfo input = getItems()[pItemIndex];
    long quantity = input.getQuantity();

    ShippingGroup sg = getShippingGroup();

    if (isLoggingDebug()) {
      logDebug("Adding item to the shipping group. Shipping group is " + sg.getId() + ", quantity is " + quantity + ", commerce item is " + pItem.getId());
    }

    getCommerceItemManager().addItemQuantityToShippingGroup(getOrder(), pItem.getId(), sg.getId(), quantity);
  }

  /**
   * Create subitems for a base configurable commerce item. This method does
   * nothing and can be overridden by a subclass if needed.
   *
   * @param pItem the base CommerceItem
   * @param pItemIndex the index of the <code>items</code> array element that
   *                   supplies input for the base commerce item
   * @exception CommerceException if there was an error while executing the code
   */
  protected void createConfigurableSubitems(CommerceItem pItem, int pItemIndex)
    throws CommerceException
  {
  }

  /**
   * Perform giftlist related processing for an item being ordered
   * from a giftlist. An item is considered ordered from a giftlist
   * if either its input giftlistId or giftlistItemId is non-null.
   *
   * @param pItem the CommerceItem
   * @param pItemIndex the index of the <code>items</code> array element that
   *                   supplies input for the commerce item
   * @exception CommerceException if there was an error while executing the code
   */
  protected void processGiftAddition(CommerceItem pItem, int pItemIndex)
    throws CommerceException
  {
    AddCommerceItemInfo input = getItems()[pItemIndex];
    if (input.getGiftlistId() != null || input.getGiftlistItemId() != null) {
      getGiftlistManager().addGiftToOrder(getProfile(), getOrder(), pItem.getId(),
                                          getShippingGroup(), input.getQuantity(), input.getGiftlistId(),
                                          input.getGiftlistItemId());
      if (isLoggingDebug()) {
        logDebug("Giftlist processing completed for commerce item " + pItem.getId());
      }
    }
  }

  /**
   * Called before any work is done by the handleAddMultipleItemsToOrder method.  It currently
   * does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preAddMultipleItemsToOrder(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after all work is done by the handleAddMultipleItemsToOrder method.  It currently
   * does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postAddMultipleItemsToOrder(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method is used to add items to the order.  It will first make a call to
   * preAddMultipleItemsToOrder, then it will call addMultipleItemsToOrder and finally it will call
   * postAddMultipleItemsToOrder.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return a <code>boolean</code> value
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */

  public boolean handleAddMultipleItemsToOrder(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleAddMultipleItemsToOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        //If any form errors found, redirect to error URL:
        if (! checkFormRedirect(null, getAddMultipleItemsToOrderErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preAddMultipleItemsToOrder(pRequest, pResponse);

          //If any form errors found, redirect to error URL:
          if (! checkFormRedirect(null, getAddMultipleItemsToOrderErrorURL(), pRequest, pResponse))
            return false;

          addMultipleItemsToOrder(pRequest, pResponse);

          //If any form errors found, redirect to error URL:
          if (! checkFormRedirect(null, getAddMultipleItemsToOrderErrorURL(), pRequest, pResponse))
            return false;

          postAddMultipleItemsToOrder(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        //If NO form errors are found, redirect to the success URL.
        //If form errors are found, redirect to the error URL.
        return checkFormRedirect (getAddMultipleItemsToOrderSuccessURL(),
                                  getAddMultipleItemsToOrderErrorURL(),
                                  pRequest,
                                  pResponse);
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
   * Adds commerce items to the order (the shopping cart).
   * The list of catalog items to be added comes from the catalogRefIds and productIds
   * arrays or from the items array.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   *
   * @see #mergeItemInputForAdd
   * @see #doAddItemsToOrder
   */
  protected void addMultipleItemsToOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    boolean valid = mergeItemInputForAdd(pRequest, pResponse, getCommerceItemType(), true);
    if (valid) {
      doAddItemsToOrder(pRequest, pResponse);
    }
  }

  /**
   * Adds commerce items to the order (the shopping cart).
   * The list of catalog items to be added comes from the catalogRefIds and productIds
   * arrays or from the items array.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pIsGift ignored, covered by the <code>giftlistId</code> and
   *                 <code>giftlistItemId</code> properties
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   *
   * @deprecated replaced by addConfigurableItemToOrder(DynamoHttpServletRequest, DynamoHttpServletResponse)
   */
  protected void addMultipleItemsToOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse,
                                boolean pIsGift)
    throws ServletException, IOException
  {
    addMultipleItemsToOrder(pRequest, pResponse);
  }


  //---------------------------------------
  // RemoveItemFromOrder
  //---------------------------------------

  /**
   * Called before any processing is done by the removeItemFromOrder method.
   * Currently, it does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preRemoveItemFromOrder(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after all processing is done by the removeItemFromOrder method.
   * It currently does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postRemoveItemFromOrder(DynamoHttpServletRequest pRequest,
                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This will make calls to the preRemoveItemFromOrder method and then will
   * call the deleteItems method.  Finally, the postRemoveItemFromOrder method will
   * get called.
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

          try {
            deleteItems(pRequest, pResponse);
          } catch (Exception exc) {
            processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
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

  //----------------------------
  // method: removeItemFromOrderByRelationshipId
  //----------------------------

  /**
   * This method is currently empty.  It can be overriden to provide functionality
   * for the removeItemFromOrderByRelationshipId methods.  It gets called
   * before any processing is done by that method.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preRemoveItemFromOrderByRelationshipId(DynamoHttpServletRequest pRequest,
                                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }


  /**
   * This method is currently empty.  It can be overriden to provide functionality
   * for the removeItemFromOrderByRelationshipId methods.  It gets called
   * after all processing is done by that method.
   *
   * @param pRequest a value of type 'DynamoHttpServletResponse'
   * @param pResponse a value of type 'DynamoHttpServletResponse'
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postRemoveItemFromOrderByRelationshipId(DynamoHttpServletRequest pRequest,
                                                      DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }


  /**
   * This method is similiar to the handleRemoveItemFromOrder method, except that it removes
   * items from the order via ShippingGroupCommerceItemRelationship object Ids.  This allows
   * for the instance when a single commerce item will be shipped to multiple places.  The display
   * will show quantity N going to the first shipping destination.  This delete allows the quantiy
   * associated with this shipping group to be removed.
   *
   * <P>
   *
   * This method will just make a call to the deleteItemsByRelationshipId.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @return true if the request was properly handled
   * @exception IOException if an error occurs
   * @exception ServletException if an error occurs
   */
  public boolean handleRemoveItemFromOrderByRelationshipId(DynamoHttpServletRequest pRequest,
                                                           DynamoHttpServletResponse pResponse)
    throws IOException, ServletException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleRemoveItemFromOrderByRelationshipId";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        if (! checkFormRedirect (null, getRemoveItemFromOrderByRelationshipIdErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preRemoveItemFromOrderByRelationshipId(pRequest, pResponse);

          if (! checkFormRedirect (null, getRemoveItemFromOrderByRelationshipIdErrorURL(), pRequest, pResponse))
            return false;

          try {
            deleteItemsByRelationshipId(pRequest, pResponse);
          } catch (Exception exc) {
            processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
          }

          if (! checkFormRedirect (null, getRemoveItemFromOrderByRelationshipIdErrorURL(), pRequest, pResponse))
            return false;

          postRemoveItemFromOrderByRelationshipId(pRequest, pResponse);

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
        } // synchronized

        return checkFormRedirect (getRemoveItemFromOrderByRelationshipIdSuccessURL(),
                                  getRemoveItemFromOrderByRelationshipIdErrorURL(),
                                  pRequest, pResponse);
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

  //---------------------------------------
  // RemoveAndAddItemToOrder
  //---------------------------------------

  /**
   * Called before any processing is done by the handleRemoveAndAddItemToOrder method.
   * It currently does nothing.
   *
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preRemoveAndAddItemToOrder(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after all processing is done by the handleRemoveAndAddItemToOrder method.
   * It currently does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postRemoveAndAddItemToOrder(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method is meant to be used on a page where a user wants to both remove an item
   * from the order as well as add an item to an order.  An example of this might be if
   * the user wants to modify an item in their cart.  It would be feasible to remove that
   * item from the cart and then automatically add their new selected item.
   *
   * <P>
   *
   * This method checks to see if any objects have been added to the RemovalCommerceIds property.
   * If they have, then remove them.  After removing them call the handleAddItemToOrder
   * method to handle the adding of an item to the order.
   *
   * @param pRequest servlet's request
   * @param pResponse servlet's response
   * @return true if processing was successful
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public boolean handleRemoveAndAddItemToOrder(DynamoHttpServletRequest pRequest,
                                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    RepeatingRequestMonitor rrm = getRepeatingRequestMonitor();
    String myHandleMethod = "CartModifierOrderFormHandler.handleRemoveAndAddItemToOrder";
    if ((rrm == null) || (rrm.isUniqueRequestEntry(myHandleMethod)))
    {
      Transaction tr = null;
      try {
        tr = ensureTransaction();
        if (getUserLocale() == null) setUserLocale(getUserLocale(pRequest, pResponse));

        //If any form errors found, redirect to error URL:
        if (! checkFormRedirect(null, getRemoveAndAddItemToOrderErrorURL(), pRequest, pResponse))
          return false;

        synchronized(getOrder()) {
          preRemoveAndAddItemToOrder(pRequest, pResponse);

          //If any form errors found, redirect to error URL:
          if (! checkFormRedirect(null, getRemoveAndAddItemToOrderErrorURL(), pRequest, pResponse))
            return false;

          deleteItems(pRequest, pResponse);

          //If any form errors found, redirect to error URL:
          if (! checkFormRedirect(null, getRemoveAndAddItemToOrderErrorURL(), pRequest, pResponse))
            return false;

          postRemoveAndAddItemToOrder(pRequest, pResponse);

          setAddItemToOrderErrorURL(getRemoveAndAddItemToOrderErrorURL());
          setAddItemToOrderSuccessURL(getRemoveAndAddItemToOrderSuccessURL());

          updateOrder(getOrder(), MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);

          return handleAddItemToOrder(pRequest, pResponse);
        } // synchronized
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
   * Utility method to update an order and process any exception
   * that happens during the update.
   *
   * @param pOrder the order to update
   * @param pMsgId the message ID to use in case of an exception
   * @param pRequest a <code>DynamoHttpServletRequest</code> value
   * @param pResponse a <code>DynamoHttpServletResponse</code> value
   */
  protected void updateOrder(Order pOrder, String pMsgId,
                             DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    try {
      getOrderManager().updateOrder(pOrder);
    }
    catch (Exception exc) {
      processException(exc, pMsgId, pRequest, pResponse);
    }
  }

  //----------------------------------------
  // PipelineChain execution utility methods
  //----------------------------------------

  /**
   * Run the pipeline which should be executed when the
   * <code>handleAddItemToOrder</code> method is invoked
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   * @exception RunProcessException if an error occurs
   * @deprecated Use runProcessAddItemToOrder(Order, CommerceItem, PricingModelHolder, Locale, RepositoryItem, Map) instead
   */
  protected void runProcessAddItemToOrder(Order pOrder, PricingModelHolder pPricingModels,
                                          Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    runProcessAddItemToOrder(pOrder, null, pPricingModels, pLocale, pProfile, pExtraParameters);
  }
  
  /**
   * Run the pipeline which should be executed when the
   * <code>handleAddItemToOrder</code> method is invoked
   * This method will add pCommerceItem to pExtraParameters unless the key
   * PipelineConstants.COMMERCEITEM is already in the map
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   */
  protected void runProcessAddItemToOrder(Order pOrder, CommerceItem pCommerceItem, PricingModelHolder pPricingModels,
                                          Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    // Allow the pipeline to have access to the newly created commerce item.
    Map extraParameters = pExtraParameters;
    if(pCommerceItem != null) {
      if(extraParameters == null)
        extraParameters = new HashMap();
    
      if(!(extraParameters.containsKey(PipelineConstants.COMMERCEITEM)))
        extraParameters.put(PipelineConstants.COMMERCEITEM, pCommerceItem);
    }
      
    PipelineResult result = runProcess(getAddItemToOrderChainId(), pOrder, pPricingModels, pLocale,
                                       pProfile, extraParameters);
    processPipelineErrors(result);
  }

  /**
   * The <code>runProcessSendScenarioEvent</code> method sends a scenario event.
   *
   * @param pOrder an <code>Order</code> value
   * @param pItem a <code>CommerceItem</code> value
   * @param pQuantity the quantity affected
   * @param pType a <code>String</code> value
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessSendScenarioEvent(Order pOrder, CommerceItem pItem,
                                              long pQuantity, String pType)
    throws RunProcessException
  { 
    runProcessSendScenarioEvent(pOrder, pItem, pQuantity, pType, null);
  }
  
  /**
   * The <code>runProcessSendScenarioEvent</code> method sends a scenario event.
   *
   * @param pOrder an <code>Order</code> value
   * @param pItem a <code>CommerceItem</code> value
   * @param pQuantity the quantity affected
   * @param pType a <code>String</code> value
   * @param pSiteId The site ID associated with a scenario event
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessSendScenarioEvent(Order pOrder, CommerceItem pItem,
                                              long pQuantity, String pType, String pSiteId)
    throws RunProcessException
  {
    // Send a scenario event on a successfully added item.
    PipelineResult result;
    HashMap params = new HashMap();

    params.put(PipelineConstants.ORDER, pOrder);
    params.put(PipelineConstants.COMMERCEITEM, pItem);
    params.put(PipelineConstants.EVENT, pType);
    params.put(PipelineConstants.QUANTITY, Long.valueOf(pQuantity));
    if (pSiteId !=null){
      params.put(PipelineConstants.SITEID, pSiteId);
    }

    result = runProcess(PipelineConstants.SENDSCENARIOEVENT, params);
    processPipelineErrors(result);

  }
  
  /**
   * The <code>runProcessSendScenarioEvent</code> method sends a
   * scenario event .
   *
   * @param pOrder an <code>Order</code> value
   * @param pItem a <code>CommerceItem</code> value
   * @param pType a <code>String</code> value
   * @param pParams a map with event specific parameters
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessSendScenarioEvent( Order pOrder,
    CommerceItem pItem, String pType, Map pParams ) throws RunProcessException
  {
    runProcessSendScenarioEvent(pOrder, pItem, pType, null, pParams);
  }

  /**
   * The <code>runProcessSendScenarioEvent</code> method sends a
   * scenario event .
   *
   * @param pOrder an <code>Order</code> value
   * @param pItem a <code>CommerceItem</code> value
   * @param pType a <code>String</code> value
   * @param pSiteId The site ID associated with a scenario event
   * @param pParams a map with event specific parameters
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessSendScenarioEvent( Order pOrder,
    CommerceItem pItem, String pType, String pSiteId, Map pParams ) throws RunProcessException
  {
    pParams.put( PipelineConstants.ORDER, pOrder );
    pParams.put( PipelineConstants.COMMERCEITEM, pItem );
    pParams.put( PipelineConstants.EVENT, pType );
    if (pSiteId !=null){
      pParams.put(PipelineConstants.SITEID, pSiteId);
    }
    
    processPipelineErrors(
      runProcess(PipelineConstants.SENDSCENARIOEVENT, pParams) );
  }

  /**
   * Run the pipeline which should be executed when the <code>handleSetOrder</code> method is invoked
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessSetOrder(Order pOrder, PricingModelHolder pPricingModels,
                                    Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    PipelineResult result = runProcess(getSetOrderChainId(), pOrder, pPricingModels, pLocale,
                                       pProfile, pExtraParameters);
    processPipelineErrors(result);
  }

  /**
   * Run the pipeline which should be executed when the <code>handleMoveToPurchaseInfo</code> method is invoked
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   * @exception RunProcessException if an error occurs
   */
  protected void runProcessMoveToPurchaseInfo(Order pOrder, PricingModelHolder pPricingModels,
                                              Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    HashMap map = new HashMap(19);
    map.put(PipelineConstants.VALIDATECOMMERCEITEMINORDER, Boolean.TRUE);
    map.put(PipelineConstants.VALIDATESHIPPINGGROUPINORDER, Boolean.FALSE);
    map.put(PipelineConstants.VALIDATEPAYMENTGROUPINORDER, Boolean.FALSE);

    PipelineResult result = runProcess(getMoveToPurchaseInfoChainId(), pOrder, pPricingModels, pLocale,
                                       pProfile, map, pExtraParameters);
    processPipelineErrors(result);
  }
  
  
}   // end of class
