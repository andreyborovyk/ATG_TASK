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
import atg.repository.MutableRepository;
import atg.repository.MutableRepositoryItem;
import atg.droplet.GenericFormHandler;
import atg.droplet.DropletConstants;
import atg.droplet.DropletException;
import atg.droplet.DropletFormException;
import atg.servlet.HttpSessionRequest;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.commerce.catalog.CatalogTools;
import atg.commerce.CommerceException;
import atg.core.util.ResourceUtils;
import atg.core.util.Address;
import atg.commerce.pricing.*;
import atg.service.pipeline.PipelineManager;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;
import atg.commerce.order.CommerceItemNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.beans.DynamicBeans;
import atg.beans.PropertyNotFoundException;
import atg.commerce.gifts.GiftlistManager;
import atg.nucleus.ServiceException;
import atg.servlet.ContainerServletException;

import java.io.IOException;
import javax.servlet.ServletException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.text.DateFormat;
import java.beans.IntrospectionException;
import javax.transaction.*;

/**
 *
 * This form handler is used to control many aspects of the user purchase process.
 * It interfaces between the front end GUI and the backend management of the users Order.
 * Its functionality can be broken out into two main functional areas: Shopping Cart managment
 * and the checkout process.
 *
 * <P>
 * * For Shopping cart management, the form handler controls any modification to a users current
 * shopping cart.  This includes; adding items to the cart, removing items from the cart and
 * adjusting the items that are currently in the cart.  This adjusmtent can take the form of
 * quantity changes etc.  This functionality is invoked via the various handleXXX methods of
 * the form handler.
 *
 * <P>
 *
 * The checkout process is broken down into several distinct steps.  These steps include: taking of
 * user information (shipping address, billing address, payment information), order confirmation (all of
 * a users purchase information is displayed, and finally the order commit process.
 *
 * <P>
 *
 * All handle methods in this form handler mirror a similiar pattern.  Each handleXXX process has an
 * associated preXXX and postXXX method.  For example, the <code>handleAddItemToOrder</code> has an
 * associate <code>preAddItemToOrder</code> and <code>postAddItemToOrder</code>.  These pre/post
 * methods provide an easy way for a user to extend the functionality of this form handler.  Additionally,
 * there are several pipelines that are executed upon the user through the checkout process.
 *
 * @beaninfo
 *   description: A form handler which can be used to modify the shopping cart
 *   attribute: functionalComponentCategory Form Handlers
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/formhandlercomp.gif
 *
 * @see atg.droplet.GenericFormHandler
 * @see atg.commerce.order.FullShoppingCartFormHandler
 * @author Bob Mason
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/ShoppingCartFormHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ShoppingCartFormHandler
  extends OrderModifierFormHandler
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/ShoppingCartFormHandler.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  // Constants used for error messages
  protected static final String MSG_ERROR_REPRICE_ORDER = "errorRepricingOrder";
  protected static final String MSG_ERROR_UPDATE_ORDER = "errorUpdatingOrder";
  protected static final String MSG_ERROR_MOVE_TO_PURCHASE_INFO = "errorMovingToPurchaseInfo";
  protected static final String MSG_ERROR_MOVE_TO_CONFIRMATION = "errorMovingToConfirmation";
  protected static final String MSG_ERROR_MOVE_TO_PAYMENT = "errorMovingToPayment";
  protected static final String MSG_ERROR_MOVE_TO_ORDER_COMMIT = "errorMovingToOrderCommit";
  protected static final String MSG_ERROR_REMOVING_ITEM = "errorRemovingItemFromOrder";
  protected static final String MSG_ERROR_ADDING_ITEM = "errorAddingItemToOrder";
  protected static final String MSG_ERROR_EDIT_ITEM = "errorEditingItem";
  protected static final String MSG_UNABLE_TO_COPY_ADDRESS = "errorCopyingAddress";
  protected static final String MSG_INVALID_QUANTITY = "invalidQuantity";
  protected static final String MSG_ORDER_ALREADY_SUBMITTED = "orderAlreadySubmitted";
  protected static final String MSG_UNSUPPORTED_RELATIONSHIP = "unsupportedRelationship";
  protected static final String GC_PAYMENT_GROUP_TYPE_NAME = "giftCertificate";
  protected static final String CC_PAYMENT_GROUP_TYPE_NAME = "creditCard";
  protected static final String NO_GIFTCERTIFICATE_FOUND = "noGiftCertificateFound";
  protected static final String MSG_DUPLICATE_ORDER_DESCRIPTION = "duplicateOrderDescription";



  //-------------------------------------
  // property: Configuration
  ShoppingCartModifierConfiguration mConfiguration;

  /**
   * Sets property Configuration, and in the process configures the following properties:
   * <UL>
   * <LI>GiftlistManager
   * <LI>UserPricingModels
   * <LI>ShoppingCart
   * <LI>PipelineManager
   * <LI>AddItemToOrderChainId
   * <LI>MoveToPurchaseInfoChainId
   * <LI>MoveToConfirmationChainId
   * <LI>SetOrderChainId
   * <LI>RepriceOrderChainId
   * <LI>Profile
   * <LI>OrderManager
   * <LI>CatalogTools
   * <LI>LocalLockManager
   * </UL>
   **/
  public void setConfiguration(ShoppingCartModifierConfiguration pConfiguration) {
    mConfiguration = pConfiguration;
    if (mConfiguration != null) {

      // from super class
      setProfile(mConfiguration.getProfile());
      setOrderManager(mConfiguration.getOrderManager());
      setCatalogTools(mConfiguration.getCatalogTools());
      setTransactionManager(mConfiguration.getTransactionManager());
      setPipelineManager(mConfiguration.getPipelineManager());
      setLocalLockManager(mConfiguration.getLocalLockManager());
      
      // defined in this class
      setGiftlistManager(mConfiguration.getGiftlistManager());
      setUserPricingModels(mConfiguration.getUserPricingModels());
      setShoppingCart(mConfiguration.getShoppingCart());
      setAddItemToOrderChainId(mConfiguration.getAddItemToOrderChainId());
      setMoveToPurchaseInfoChainId(mConfiguration.getMoveToPurchaseInfoChainId());
      setMoveToConfirmationChainId(mConfiguration.getMoveToConfirmationChainId());
      setSetOrderChainId(mConfiguration.getSetOrderChainId());
      setRepriceOrderChainId(mConfiguration.getRepriceOrderChainId());
      setAddItemToOrderPricingOp(mConfiguration.getAddItemToOrderPricingOp());
      setModifyOrderPricingOp(mConfiguration.getModifyOrderPricingOp());
      setDeleteItemsFromOrderPricingOp(mConfiguration.getDeleteItemsFromOrderPricingOp());
    }
  }

  /**
   * Returns property Configuration
   * @beaninfo description: The configuration for the shopping cart form handler
   **/
  public ShoppingCartModifierConfiguration getConfiguration() {
    return mConfiguration;
  }


  //---------------------------------------------------------------------
  // property: checkForChangedQuantity
  boolean mCheckForChangedQuantity = true;

  /**
   * Determine if the FormHandler should checked for changes in quantities
   * of commerce items.  If it is set to true, then it will check.  By default
   * this property is set to true.  This only affects checking for changed
   * quantities, the removalCatalogRefId array will still be checked to determine
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


  //---------------------------------------------------------------------------
  // property: DeletedSkus
  //---------------------------------------------------------------------------

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

  

  //---------------------------------------------------------------------------
  // property: OrderId
  String mOrderId;

  /**
   * Set the OrderId property.
   */
  public void setOrderId(String pOrderId) {
    mOrderId = pOrderId;
  }

  /**
   * Return the OrderId property.
   * @beaninfo description: The id of the order used during modifications
   */
  public String getOrderId() {
    return mOrderId;
  }

  //---------------------------------------------------------------------------
  // property: giftCertificateNumbers
  String mGiftCertificateNumbers;

  /**
   * Set the giftCertificateNumbers property.
   */
  public void setGiftCertificateNumbers(String pGiftCertificateNumbers) {
    mGiftCertificateNumbers = pGiftCertificateNumbers;
  }

  /**
   * Return the giftCertificateNumbers property.
   * @beaninfo description: The gift certificate numbers used during modifications
   */
  public String getGiftCertificateNumbers() {
    return mGiftCertificateNumbers;
  }

  //---------------------------------------------------------------------------
  // property: CreditCardPaymentTypeName
  String mCreditCardPaymentTypeName = CC_PAYMENT_GROUP_TYPE_NAME;

  /**
   * Set the CreditCardPaymentTypeName property.
   */
  public void setCreditCardPaymentTypeName(String pCreditCardPaymentTypeName) {
    mCreditCardPaymentTypeName = pCreditCardPaymentTypeName;
  }

  /**
   * Return the CreditCardPaymentTypeName property.
   * @beaninfo description: The name of the credit card payment type
   */
  public String getCreditCardPaymentTypeName() {
    return mCreditCardPaymentTypeName;
  }

  //---------------------------------------------------------------------------
  // property: giftCertificatePaymentTypeName
  String mGiftCertificatePaymentTypeName = GC_PAYMENT_GROUP_TYPE_NAME;

  /**
   * Set the giftCertificatePaymentTypeName property.
   */
  public void setGiftCertificatePaymentTypeName(String pGiftCertificatePaymentTypeName) {
    mGiftCertificatePaymentTypeName = pGiftCertificatePaymentTypeName;
  }

  /**
   * Return the giftCertificatePaymentTypeName property.
   * @beaninfo description: THe name of the gift certificate payment type
   */
  public String getGiftCertificatePaymentTypeName() {
    return mGiftCertificatePaymentTypeName;
  }

  //---------------------------------------------------------------------------
  // property: giftCertificatePaymentGroups
  List mGiftCertificatePaymentGroups;

  /**
   * Set the giftCertificatePaymentGroups property.
   */
  public void setGiftCertificatePaymentGroups(List pGiftCertificatePaymentGroups) {
    mGiftCertificatePaymentGroups = pGiftCertificatePaymentGroups;
  }

  /**
   * Return the giftCertificatePaymentGroups property.
   * @beaninfo description: The list of payment groups using gift certificates
   */
  public List getGiftCertificatePaymentGroups() {
    return mGiftCertificatePaymentGroups;
  }

  //---------------------------------------------------------------------------
  // property: CreditCardPaymentGroups
  List mCreditCardPaymentGroups = new ArrayList(1);

  /**
   * Set the CreditCardPaymentGroups property.
   */
  public void setCreditCardPaymentGroups(List pCreditCardPaymentGroups) {
    mCreditCardPaymentGroups = pCreditCardPaymentGroups;
  }

  /**
   * Return the CreditCardPaymentGroups property.
   * @beaninfo description: The list of payment groups using credit cards
   */
  public List getCreditCardPaymentGroups() {
    return mCreditCardPaymentGroups;
  }

  //-------------------------------------
  // property: RemovalCatalogRefIds
  String [] mRemovalCatalogRefIds;

  /**
   * Sets property RemovalCatalogRefIds
   **/
  public void setRemovalCatalogRefIds(String [] pRemovalCatalogRefIds) {
    mRemovalCatalogRefIds = pRemovalCatalogRefIds;
  }

  /**
   * Returns property RemovalCatalogRefIds
   * @beaninfo description: The list of catalog ref ids used during item removal
   **/
  public String [] getRemovalCatalogRefIds() {
    return mRemovalCatalogRefIds;
  }



  //---------------------------------------------------------------------------
  // property: removalRelationshipIds
  String[] mRemovalRelationshipIds;

  /**
   * Set the removalRelationshipIds property.
   */
  public void setRemovalRelationshipIds(String[] pRemovalRelationshipIds) {
    mRemovalRelationshipIds = pRemovalRelationshipIds;
  }

  /**
   * Return the removalRelationshipIds property.
   */
  public String[] getRemovalRelationshipIds() {
    return mRemovalRelationshipIds;
  }


  //-------------------------------------
  // property: RemovalCommerceIds
  String [] mRemovalCommerceIds;

  /**
   * Sets property RemovalCommerceIds
   **/
  public void setRemovalCommerceIds(String [] pRemovalCommerceIds) {
    mRemovalCommerceIds = pRemovalCommerceIds;
  }

  /**
   * Returns property RemovalCommerceIds
   * @beaninfo description: The list of commerce ids used during item removal
   **/
  public String [] getRemovalCommerceIds() {
    return mRemovalCommerceIds;
  }


  //---------------------------------------------------------------------------
  // property: SaveOrderErrorURL

  String mSaveOrderErrorURL;
  /**
   * Set the SaveOrderErrorURL property.
   */
  public void setSaveOrderErrorURL(String pSaveOrderErrorURL) {
    mSaveOrderErrorURL = pSaveOrderErrorURL;
  }
  /**
   * Return the SaveOrderErrorURL property.
   * @beaninfo description: The SaveOrderErrorURL used during handleSaveOrder method
   */

  public String getSaveOrderErrorURL() {
    return mSaveOrderErrorURL;
  }

  //---------------------------------------------------------------------------
  // property: SaveOrderSuccessURL

  String mSaveOrderSuccessURL;
  /**
   * Set the SaveOrderSuccessURL property.
 */
  public void setSaveOrderSuccessURL(String pSaveOrderSuccessURL) {
    mSaveOrderSuccessURL = pSaveOrderSuccessURL;
  }

  /**
   * Return the getSaveOrderSuccessURL property.
   * @beaninfo description: The SaveOrderSucessURL used during handleSaveOrder method
   */

  public String getSaveOrderSuccessURL() {
    return mSaveOrderSuccessURL;
  }

  //---------------------------------------------------------------------------
  // property: Description

  String mDescription;
  /**
   * Return the Description property.
   * @beaninfo description: The Description property of the order set during hanldeSaveOrder.
   */

  public String getDescription() {
    return mDescription;
  }
  /**
   * Set the Description property.
   */

  public void setDescription(String pDescription) {
    mDescription = pDescription;
  }

  //-------------------------------------
  // property: ShippingGroup
  //-------------------------------------

  ShippingGroup mShippingGroup;

  /**
   * Sets property ShippingGroup.
   **/
  public void setShippingGroup(ShippingGroup pShippingGroup) {
    mShippingGroup = pShippingGroup;
  }

  /**
   * Returns property ShippingGroup.
   * @beaninfo description: The shipping group used during modifications to shipping groups
   **/
  public ShippingGroup getShippingGroup() {

    if (mShippingGroup == null) {
      if (getOrder().getShippingGroups() != null &&
          getOrder().getShippingGroups().size() > 0)
      {
        mShippingGroup = (ShippingGroup)getOrder().getShippingGroups().get(0);
      }
      else {
        try {
          getOrder().addShippingGroup(mOrderManager.getShippingGroupManager().createShippingGroup());
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


  //-------------------------------------
  // property: PaymentGroup
  //-------------------------------------
  PaymentGroup mPaymentGroup;

  /**
   * Sets property PaymentGroup
   **/
  public void setPaymentGroup(PaymentGroup pPaymentGroup) {
    mPaymentGroup = pPaymentGroup;
  }

  /**
   * Returns property PaymentGroup
   * @beaninfo description: The payment group used during modifications to payment groups
   **/
  public PaymentGroup getPaymentGroup() {
    if (mPaymentGroup == null) {
      if (getOrder().getPaymentGroups() != null &&
          getOrder().getPaymentGroups().size() > 0) {
        mPaymentGroup = (PaymentGroup)getOrder().getPaymentGroups().get(0);
      }
      else {
        try {
          getOrder().addPaymentGroup(mOrderManager.getPaymentGroupManager().createPaymentGroup());
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

  //---------------------------------------------------------------------------
  // property:GiftlistManager
  //---------------------------------------------------------------------------

  private GiftlistManager mGiftlistManager;
  public void setGiftlistManager(GiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }

  /**
   * The giftlist that an item was added from
   * @beaninfo description: The service used for managing manipulations of giftlists
   **/
  public GiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }


  //---------------------------------------------------------------------------
  // property:Giftlist
  //---------------------------------------------------------------------------

  private String mGiftlistId;
  public void setGiftlistId(String pGiftlistId) {
    mGiftlistId = pGiftlistId;
  }

  /**
   * The giftlist that an item was added from
   * @beaninfo description: The id of the giftlist used when adding giftlist items
   **/
  public String getGiftlistId() {
    return mGiftlistId;
  }


  //---------------------------------------------------------------------------
  // property:GiftlistItemId
  //---------------------------------------------------------------------------

  private String mGiftlistItemId;
  public void setGiftlistItemId(String pGiftlistItemId) {
    mGiftlistItemId = pGiftlistItemId;
  }

  /**
   * The giftlist item being added
   * @beaninfo description: The id of the giftlist item used when adding giftlist items
   **/
  public String getGiftlistItemId() {
    return mGiftlistItemId;
  }

  //-------------------------------------
  // property: UserPricingModels
  //-------------------------------------
  PricingModelHolder mUserPricingModels;

  /**
   * Sets property UserPricingModels
   **/
  public void setUserPricingModels(PricingModelHolder pUserPricingModels) {
    mUserPricingModels = pUserPricingModels;
  }

  /**
   * Returns property UserPricingModels
   * @beaninfo description: The pricing models available to the user
   **/
  public PricingModelHolder getUserPricingModels() {
    return mUserPricingModels;
  }

  //-------------------------------------
  // property: ShoppingCart
  //-------------------------------------
  OrderHolder mShoppingCart;

  /**
   * Sets property ShoppingCart
   **/
  public void setShoppingCart(OrderHolder pShoppingCart) {
    mShoppingCart = pShoppingCart;
  }

  /**
   * Returns property ShoppingCart
   * @beaninfo description: The current shopping cart
   **/
  public OrderHolder getShoppingCart() {
    return mShoppingCart;
  }

  //-------------------------------------
  // property: Order
  //-------------------------------------
  /**
   * Returns property Order, if the shoppingCart property is not null, then
   * return the current order of the shopping cart order holder
   * @beaninfo description: The current order
   **/
  public Order getOrder() {
    if (getShoppingCart() != null)
      return getShoppingCart().getCurrent();
    else
      return super.getOrder();
  }

  //-------------------------------------
  // property: LastOrder
  //-------------------------------------
  Order mLastOrder;

  /**
   * Returns the last order that was committed.
   * @beaninfo description: The last order that was committed
   */
  public Order getLastOrder() {
    return mLastOrder;
  }

  //-------------------------------------
  // property: AddItemToOrderChainId
  //-------------------------------------
  String mAddItemToOrderChainId = null;

  /**
   * Sets property AddItemToOrderChainId
   **/
  public void setAddItemToOrderChainId(String pAddItemToOrderChainId) {
    mAddItemToOrderChainId = pAddItemToOrderChainId;
  }

  /**
   * Returns property AddItemToOrderChainId
   * @beaninfo description: The name of the pipeline chain used to add items to the order
   **/
  public String getAddItemToOrderChainId() {
    return mAddItemToOrderChainId;
  }

  //-------------------------------------
  // property: ItemAddedToOrderEventType
  //-------------------------------------
  String mItemAddedToOrderEventType = ItemAddedToOrder.TYPE;

  /**
   * Sets property ItemAddedToOrderEventType
   **/
  public void setItemAddedToOrderEventType(String pItemAddedToOrderEventType) {
    mItemAddedToOrderEventType = pItemAddedToOrderEventType;
  }

  /**
   * Returns property ItemAddedToOrderEventType
   * @beaninfo description: The event type when an item is added to the order
   **/
  public String getItemAddedToOrderEventType() {
    return mItemAddedToOrderEventType;
  }

  //-------------------------------------
  // property: ItemRemovedFromOrderEventType
  //-------------------------------------
  String mItemRemovedFromOrderEventType = ItemRemovedFromOrder.TYPE;

  /**
   * Sets property ItemRemovedFromOrderEventType
   **/
  public void setItemRemovedFromOrderEventType(String pItemRemovedFromOrderEventType) {
    mItemRemovedFromOrderEventType = pItemRemovedFromOrderEventType;
  }

  /**
   * Returns property ItemRemovedFromOrderEventType
   * @beaninfo description: The event type when an item is removed from the order
   **/
  public String getItemRemovedFromOrderEventType() {
    return mItemRemovedFromOrderEventType;
  }

  //-------------------------------------
  // property: OrderSavedEventType
  //-------------------------------------
  String mOrderSavedEventType = OrderSaved.TYPE;

  /**
   * Sets property OrderSavedEventType
   **/
  public void setOrderSavedEventType(String pOrderSavedEventType) {
    mOrderSavedEventType = pOrderSavedEventType;
  }

  /**
   * Returns property OrderSavedEventType
   * @beaninfo description: The event type when an order is saved
   **/
  public String getOrderSavedEventType() {
    return mOrderSavedEventType;
  }

  //-------------------------------------
  // property: MoveToPurchaseInfoChainId
  //-------------------------------------
  String mMoveToPurchaseInfoChainId = null;

  /**
   * Sets property MoveToPurchaseInfoChainId
   **/
  public void setMoveToPurchaseInfoChainId(String pMoveToPurchaseInfoChainId) {
    mMoveToPurchaseInfoChainId = pMoveToPurchaseInfoChainId;
  }

  /**
   * Returns property MoveToPurchaseInfoChainId
   * @beaninfo description: The name of the pipeline chain use when moving to the purchase info
   **/
  public String getMoveToPurchaseInfoChainId() {
    return mMoveToPurchaseInfoChainId;
  }

  //-------------------------------------
  // property: MoveToConfirmationChainId
  //-------------------------------------
  String mMoveToConfirmationChainId = null;

  /**
   * Sets property MoveToConfirmationChainId
   **/
  public void setMoveToConfirmationChainId(String pMoveToConfirmationChainId) {
    mMoveToConfirmationChainId = pMoveToConfirmationChainId;
  }

  /**
   * Returns property MoveToConfirmationChainId
   * @beaninfo description: The name of the pipeline chain use when moving to confirmation
   **/
  public String getMoveToConfirmationChainId() {
    return mMoveToConfirmationChainId;
  }

  //-------------------------------------
  // property: SetOrderChainId
  //-------------------------------------
  String mSetOrderChainId = null;

  /**
   * Sets property SetOrderChainId
   **/
  public void setSetOrderChainId(String pSetOrderChainId) {
    mSetOrderChainId = pSetOrderChainId;
  }

  /**
   * Returns property SetOrderChainId
   * @beaninfo description: The name of the pipeline chain used to set the order
   **/
  public String getSetOrderChainId() {
    return mSetOrderChainId;
  }

  //-------------------------------------
  // property: RepriceOrderChainId
  //-------------------------------------
  String mRepriceOrderChainId = null;

  /**
   * Sets property RepriceOrderChainId
   **/
  public void setRepriceOrderChainId(String pRepriceOrderChainId) {
    mRepriceOrderChainId = pRepriceOrderChainId;
  }

  /**
   * Returns property RepriceOrderChainId
   * @beaninfo description: The name of the pipeline chain used to reprice the order
   **/
  public String getRepriceOrderChainId() {
    return mRepriceOrderChainId;
  }

  //-------------------------------------
  // property: AddItemToOrderSuccessURL
  //-------------------------------------
  String mAddItemToOrderSuccessURL;

  /**
   * Sets property AddItemToOrderSuccessURL
   **/
  public void setAddItemToOrderSuccessURL(String pAddItemToOrderSuccessURL) {
    mAddItemToOrderSuccessURL = pAddItemToOrderSuccessURL;
  }

  /**
   * Returns property AddItemToOrderSuccessURL
   * @beaninfo description: The URL to go to when AddItemToOrder is successful
   **/
  public String getAddItemToOrderSuccessURL() {
    return mAddItemToOrderSuccessURL;
  }

  //-------------------------------------
  // property: AddItemToOrderErrorURL
  //-------------------------------------
  String mAddItemToOrderErrorURL;

  /**
   * Sets property AddItemToOrderErrorURL
   **/
  public void setAddItemToOrderErrorURL(String pAddItemToOrderErrorURL) {
    mAddItemToOrderErrorURL = pAddItemToOrderErrorURL;
  }

  /**
   * Returns property AddItemToOrderErrorURL
   * @beaninfo description: The URL to go to when AddItemToOrder fails
   **/
  public String getAddItemToOrderErrorURL() {
    return mAddItemToOrderErrorURL;
  }


  //---------------------------------------------------------------------------
  // property: RemoveItemFromOrderByRelationshipIdSuccessURL
  String mRemoveItemFromOrderByRelationshipIdSuccessURL;

  /**
   * Set the RemoveItemFromOrderByRelationshipIdSuccessURL property.
   */
  public void setRemoveItemFromOrderByRelationshipIdSuccessURL(String pRemoveItemFromOrderByRelationshipIdSuccessURL) {
    mRemoveItemFromOrderByRelationshipIdSuccessURL = pRemoveItemFromOrderByRelationshipIdSuccessURL;
  }

  /**
   * Return the RemoveItemFromOrderByRelationshipIdSuccessURL property.
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
   */
  public void setRemoveItemFromOrderByRelationshipIdErrorURL(String pRemoveItemFromOrderByRelationshipIdErrorURL) {
    mRemoveItemFromOrderByRelationshipIdErrorURL = pRemoveItemFromOrderByRelationshipIdErrorURL;
  }

  /**
   * Return the RemoveItemFromOrderByRelationshipIdErrorURL property.
   * @beaninfo description: The URL to go to when RemoveItemFromOrderByRelationship fails
   */
  public String getRemoveItemFromOrderByRelationshipIdErrorURL() {
    return mRemoveItemFromOrderByRelationshipIdErrorURL;
  }

  //-------------------------------------
  // Property: RemoveItemFromOrderSuccessURL
  //-------------------------------------
  String mRemoveItemFromOrderSuccessURL;

  /**
   * Sets property RemoveItemFromOrderSuccessURL
   **/
  public void setRemoveItemFromOrderSuccessURL(String pRemoveItemFromOrderSuccessURL) {
    mRemoveItemFromOrderSuccessURL = pRemoveItemFromOrderSuccessURL;
  }

  /**
   * Returns property RemoveItemFromOrderSuccessURL
   * @beaninfo description: The URL to go to when RemoveItemFromOrder is successful
   **/
  public String getRemoveItemFromOrderSuccessURL() {
    return mRemoveItemFromOrderSuccessURL;
  }

  //-------------------------------------
  // property: RemoveItemFromOrderErrorURL
  //-------------------------------------
  String mRemoveItemFromOrderErrorURL;

  /**
   * Sets property RemoveItemFromOrderErrorURL
   **/
  public void setRemoveItemFromOrderErrorURL(String pRemoveItemFromOrderErrorURL) {
    mRemoveItemFromOrderErrorURL = pRemoveItemFromOrderErrorURL;
  }

  /**
   * Returns property RemoveItemFromOrderErrorURL
   * @beaninfo description: The URL to go to when RemoveItemFromOrder fails
   **/
  public String getRemoveItemFromOrderErrorURL() {
    return mRemoveItemFromOrderErrorURL;
  }

  //-------------------------------------
  // property: RemoveAndAddItemToOrderErrorURL
  //-------------------------------------
  String mRemoveAndAddItemToOrderErrorURL;

  /**
   * Sets property RemoveAndAddItemToOrderErrorURL
   **/
  public void setRemoveAndAddItemToOrderErrorURL(String pRemoveAndAddItemToOrderErrorURL) {
    mRemoveAndAddItemToOrderErrorURL = pRemoveAndAddItemToOrderErrorURL;
  }

  /**
   * Returns property RemoveAndAddItemToOrderErrorURL
   * @beaninfo description: The URL to go to when RemoveAndAddItemFromOrder is successful
   **/
  public String getRemoveAndAddItemToOrderErrorURL() {
    return mRemoveAndAddItemToOrderErrorURL;
  }

  //-------------------------------------
  // property: RemoveAndAddItemToOrderSuccessURL
  //-------------------------------------
  String mRemoveAndAddItemToOrderSuccessURL;

  /**
   * Sets property RemoveAndAddItemToOrderSuccessURL
   **/
  public void setRemoveAndAddItemToOrderSuccessURL(String pRemoveAndAddItemToOrderSuccessURL) {
    mRemoveAndAddItemToOrderSuccessURL = pRemoveAndAddItemToOrderSuccessURL;
  }

  /**
   * Returns property RemoveAndAddItemToOrderSuccessURL
   * @beaninfo description: The URL to go to when RemoveAndAddItemFromOrder fails
   **/
  public String getRemoveAndAddItemToOrderSuccessURL() {
    return mRemoveAndAddItemToOrderSuccessURL;
  }


  //-------------------------------------
  // property: EditItemSuccessURL
  //--------------------------------------
  String mEditItemSuccessURL;

  /**
   * Sets property EditItemSuccessURL.  This property
   * is normally set on a jhtml page.  It indicates
   * which page we should redirect to if NO errors
   * occur when the user pushes the EDIT button
   * on the Shopping Cart page.
   **/
  public void setEditItemSuccessURL(String pEditItemSuccessURL) {
    mEditItemSuccessURL = pEditItemSuccessURL;
  }

  /**
   * Returns property EditItemSuccessURL
   * @beaninfo description: The URL to go to when EditItem is successful
   **/
  public String getEditItemSuccessURL() {
    return mEditItemSuccessURL;
  }

  //-------------------------------------
  // property: EditItemErrorURL
  //--------------------------------------
  String mEditItemErrorURL;

  /**
   * Sets property EditItemErrorURL.  This property
   * is normally set on the a jhtml page.  It indicates
   * which page we should redirect to if errors
   * occur when the user pushes the EDIT button
   * on the Shopping Cart page.
   **/
  public void setEditItemErrorURL(String pEditItemErrorURL) {
    mEditItemErrorURL = pEditItemErrorURL;
  }

  /**
   * Returns property EditItemErrorURL
   * @beaninfo description: The URL to go to when EditItem fails
   **/
  public String getEditItemErrorURL() {
    return mEditItemErrorURL;
  }

  //-------------------------------------
  // property: MoveToPurchaseInfoSuccessURL
  //-------------------------------------
  String mMoveToPurchaseInfoSuccessURL;

  /**
   * Sets property MoveToPurchaseInfoSuccessURL
   **/
  public void setMoveToPurchaseInfoSuccessURL(String pMoveToPurchaseInfoSuccessURL) {
    mMoveToPurchaseInfoSuccessURL = pMoveToPurchaseInfoSuccessURL;
  }

  /**
   * Returns property MoveToPurchaseInfoSuccessURL
   * @beaninfo description: The URL to go to when MoveToPurchaseInfo is successful
   **/
  public String getMoveToPurchaseInfoSuccessURL() {
    return mMoveToPurchaseInfoSuccessURL;
  }

  //-------------------------------------
  // property: MoveToPurchaseInfoErrorURL
  //-------------------------------------
  String mMoveToPurchaseInfoErrorURL;

  /**
   * Sets property MoveToPurchaseInfoErrorURL
   **/
  public void setMoveToPurchaseInfoErrorURL(String pMoveToPurchaseInfoErrorURL) {
    mMoveToPurchaseInfoErrorURL = pMoveToPurchaseInfoErrorURL;
  }

  /**
   * Returns property MoveToPurchaseInfoErrorURL
   * @beaninfo description: The URL to go to when MoveToPurchaseInfo fails
   **/
  public String getMoveToPurchaseInfoErrorURL() {
    return mMoveToPurchaseInfoErrorURL;
  }


  //---------------------------------------------------------------------------
  // property: moveToPurchaseInfoByRelIdSuccessURL
  String mMoveToPurchaseInfoByRelIdSuccessURL;

  /**
   * Set the moveToPurchaseInfoByRelIdSuccessURL property.
   */
  public void setMoveToPurchaseInfoByRelIdSuccessURL(String pMoveToPurchaseInfoByRelIdSuccessURL) {
    mMoveToPurchaseInfoByRelIdSuccessURL = pMoveToPurchaseInfoByRelIdSuccessURL;
  }

  /**
   * Return the moveToPurchaseInfoByRelIdSuccessURL property.
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
   */
  public void setMoveToPurchaseInfoByRelIdErrorURL(String pMoveToPurchaseInfoByRelIdErrorURL) {
    mMoveToPurchaseInfoByRelIdErrorURL = pMoveToPurchaseInfoByRelIdErrorURL;
  }

  /**
   * Return the moveToPurchaseInfoByRelIdErrorURL property.
   * @beaninfo description: The URL to go to when MoveToPurchaseInfoByRelId fails
   */
  public String getMoveToPurchaseInfoByRelIdErrorURL() {
    return mMoveToPurchaseInfoByRelIdErrorURL;
  }


  //-------------------------------------
  // property: MoveToConfirmationSuccessURL
  //-------------------------------------
  String mMoveToConfirmationSuccessURL;

  /**
   * Sets property MoveToConfirmationSuccessURL
   **/
  public void setMoveToConfirmationSuccessURL(String pMoveToConfirmationSuccessURL) {
    mMoveToConfirmationSuccessURL = pMoveToConfirmationSuccessURL;
  }

  /**
   * Returns property MoveToConfirmationSuccessURL
   * @beaninfo description: The URL to go to when MoveToConfirmation is successful
   **/
  public String getMoveToConfirmationSuccessURL() {
    return mMoveToConfirmationSuccessURL;
  }

  //-------------------------------------
  // property: MoveToConfirmationErrorURL
  //-------------------------------------
  String mMoveToConfirmationErrorURL;

  /**
   * Sets property MoveToConfirmationErrorURL
   **/
  public void setMoveToConfirmationErrorURL(String pMoveToConfirmationErrorURL) {
    mMoveToConfirmationErrorURL = pMoveToConfirmationErrorURL;
  }

  /**
   * Returns property MoveToConfirmationErrorURL
   * @beaninfo description: The URL to go to when MoveToConfirmation fails
   **/
  public String getMoveToConfirmationErrorURL() {
    return mMoveToConfirmationErrorURL;
  }

  //-------------------------------------
  // property: MoveToOrderCommitSuccessURL
  //-------------------------------------
  String mMoveToOrderCommitSuccessURL;

  /**
   * Sets property MoveToOrderCommitSuccessURL
   **/
  public void setMoveToOrderCommitSuccessURL(String pMoveToOrderCommitSuccessURL) {
    mMoveToOrderCommitSuccessURL = pMoveToOrderCommitSuccessURL;
  }

  /**
   * Returns property MoveToOrderCommitSuccessURL
   * @beaninfo description: The URL to go to when MoveToOrderCommit is successful
   **/
  public String getMoveToOrderCommitSuccessURL() {
    return mMoveToOrderCommitSuccessURL;
  }

  //-------------------------------------
  // property: MoveToOrderCommitErrorURL
  //-------------------------------------
  String mMoveToOrderCommitErrorURL;

  /**
   * Sets property MoveToOrderCommitErrorURL
   **/
  public void setMoveToOrderCommitErrorURL(String pMoveToOrderCommitErrorURL) {
    mMoveToOrderCommitErrorURL = pMoveToOrderCommitErrorURL;
  }

  /**
   * Returns property MoveToOrderCommitErrorURL
   * @beaninfo description: The URL to go to when MoveToOrderCommit fails
   **/
  public String getMoveToOrderCommitErrorURL() {
    return mMoveToOrderCommitErrorURL;
  }


  //---------------------------------------------------------------------------
  // property: setOrderByRelationshipIdSuccessURL
  String mSetOrderByRelationshipIdSuccessURL;

  /**
   * Set the setOrderByRelationshipIdSuccessURL property.
   */
  public void setSetOrderByRelationshipIdSuccessURL(String pSetOrderByRelationshipIdSuccessURL) {
    mSetOrderByRelationshipIdSuccessURL = pSetOrderByRelationshipIdSuccessURL;
  }

  /**
   * Return the setOrderByRelationshipIdSuccessURL property.
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
   */
  public void setSetOrderByRelationshipIdErrorURL(String pSetOrderByRelationshipIdErrorURL) {
    mSetOrderByRelationshipIdErrorURL = pSetOrderByRelationshipIdErrorURL;
  }

  /**
   * Return the SetOrderByRelationshipIdErrorURL property.
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
   **/
  public void setSetOrderSuccessURL(String pSetOrderSuccessURL) {
    mSetOrderSuccessURL = pSetOrderSuccessURL;
  }

  /**
   * Returns property SetOrderSuccessURL
   * @beaninfo description: The URL to go to when SetOrder is successful
   **/
  public String getSetOrderSuccessURL() {
    return mSetOrderSuccessURL;
  }

  //-------------------------------------
  // property: SetOrderErrorURL
  //-------------------------------------
  String mSetOrderErrorURL;

  /**
   * Sets property SetOrderErrorURL
   **/
  public void setSetOrderErrorURL(String pSetOrderErrorURL) {
    mSetOrderErrorURL = pSetOrderErrorURL;
  }

  /**
   * Returns property SetOrderErrorURL
   * @beaninfo description: The URL to go to when SetOrder fails
   **/
  public String getSetOrderErrorURL() {
    return mSetOrderErrorURL;
  }


  //-------------------------------------
  // property: RepriceOrderErrorURL
  //-------------------------------------
  String mRepriceOrderErrorURL;

  /**
   * Sets property RepriceOrderErrorURL
   **/
  public void setRepriceOrderErrorURL(String pRepriceOrderErrorURL) {
    mRepriceOrderErrorURL = pRepriceOrderErrorURL;
  }

  /**
   * Returns property RepriceOrderErrorURL
   * @beaninfo description: The URL to go to when RepriceOrder fails
   **/
  public String getRepriceOrderErrorURL() {
    return mRepriceOrderErrorURL;
  }

  //-------------------------------------
  // property: RepriceOrderSuccessURL
  //-------------------------------------
  String mRepriceOrderSuccessURL;

  /**
   * Sets property RepriceOrderSuccessURL
   **/
  public void setRepriceOrderSuccessURL(String pRepriceOrderSuccessURL) {
    mRepriceOrderSuccessURL = pRepriceOrderSuccessURL;
  }

  /**
   * Returns property RepriceOrderSuccessURL
   * @beaninfo description: The URL to go to when RepriceOrder is successful
   **/
  public String getRepriceOrderSuccessURL() {
    return mRepriceOrderSuccessURL;
  }

  //-------------------------------------
  // property: copyBillingAddrToShippingAddr
  //-------------------------------------
  boolean mCopyBillingAddrToShippingAddr = false;

  public void setCopyBillingAddrToShippingAddr(boolean pCopyBillingAddrToShippingAddr) {
    mCopyBillingAddrToShippingAddr = pCopyBillingAddrToShippingAddr;
  }

  /**
   * If this is true then the billing address is copied to the shipping address
   * @beaninfo description: If this is true then the billing address is copied to the shipping address
   **/
  public boolean getCopyBillingAddrToShippingAddr() {
    return mCopyBillingAddrToShippingAddr;
  }

  //-------------------------------------
  // property: SesionExpirationURL
  //-------------------------------------
  String mSessionExpirationURL;

  public void setSessionExpirationURL(String pSessionExpirationURL) {
    mSessionExpirationURL = pSessionExpirationURL;
  }

  /**
   * The URL to go to if the session has expired
   * @beaninfo description: The URL to go to if the session has expired
   **/
  public String getSessionExpirationURL() {
    return mSessionExpirationURL;
  }

  //-------------------------------------
  // property: CommerceItemIdToEdit
  //-------------------------------------
  String mCommerceItemIdToEdit;

  /**
   * Sets property CommerceItemIdToEdit.  This property
   * contains the CommerceItem the user has selected for editing.
   * Setting this property sets other related properties at the
   * start of the Edit Item process.
   **/
  public void setCommerceItemIdToEdit(String pCommerceItemIdToEdit) {
    mCommerceItemIdToEdit = pCommerceItemIdToEdit;
  }

  /**
   * Returns property CommerceItemIdToEdit.  This property
   * returns the CommerceItem the user has selected for editing.
   * @beaninfo description: The id of the CommerceItem the user has selected for editing
   **/
  public String getCommerceItemIdToEdit() {
    return mCommerceItemIdToEdit;
  }

  //-------------------------------------
  // property: EditItem
  //-------------------------------------
  String mEditItem;

  /**
   * Sets property EditItem.  This property
   * contains the CommerceItem ID (as a string) the user wants to edit.
   * This method is called just before the handleEditItem method
   * is invoked when user pushes the "Edit" button for a Commerce Item.
   **/
  public void setEditItem(String pEditItem) {
    mEditItem = pEditItem;
  }

  /**
   * Returns property EditItem.  This property
   * contains the ProductId the user is editing.
   * @beaninfo description: The product Id the user is editing
   **/
  public String getEditItem() {
    return mEditItem;
  }


  //-------------------------------
  // property: CommerceItemToEdit

  /**
   * Return a reference to the commerce item to edit that lives in the
   * users order.  The object is retrieved whose id is equal
   * to the value of the property editItem.
   * @beaninfo description: The commerce item the user is editing
   *
   * @return a value of type 'CommerceItem'
   */
  public CommerceItem getCommerceItemToEdit()
    throws CommerceItemNotFoundException, InvalidParameterException
  {
    return getOrder().getCommerceItem(mEditItem);
  }

  //-------------------------------------
  // property: RepriceOrder
  String mRepriceOrder;

  /**
   * Corresponds to the handle reprice order method. This property should be set to the
   * type of pricing operation that should be executed when the reprice order handler method
   * is invoked. The operations which are acceptable are defined in the
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
  public void setRepriceOrder(String pRepriceOrder) {
    mRepriceOrder = pRepriceOrder;
  }

  /**
   * Returns the pricing operation that should be performed by the reprice order handler method.
   * @beaninfo description: The pricing operation that should be performed by the reprice order handler
   **/
  public String getRepriceOrder() {
    return mRepriceOrder;
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
   **/
  public void setAddItemToOrderPricingOp(String pAddItemToOrderPricingOp) {
    mAddItemToOrderPricingOp = pAddItemToOrderPricingOp;
  }

  /**
   * Returns the pricing operation that should be performed when an item is added to the order
   * @beaninfo description: The pricing operation that should be performed when an item is added to the order
   **/
  public String getAddItemToOrderPricingOp() {
    return mAddItemToOrderPricingOp;
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
   **/
  public void setModifyOrderPricingOp(String pModifyOrderPricingOp) {
    mModifyOrderPricingOp = pModifyOrderPricingOp;
  }

  /**
   * Returns the pricing operation that should be performed when the order is updated
   * @beaninfo description: The pricing operation that should be performed when the order is updated
   **/
  public String getModifyOrderPricingOp() {
    return mModifyOrderPricingOp;
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
   **/
  public void setDeleteItemsFromOrderPricingOp(String pDeleteItemsFromOrderPricingOp) {
    mDeleteItemsFromOrderPricingOp = pDeleteItemsFromOrderPricingOp;
  }

  /**
   * Returns the pricing operation that should be performed when an item is deleted from the order
   * @beaninfo description: The pricing operation that should be performed when an item is deleted from the order
   **/
  public String getDeleteItemsFromOrderPricingOp() {
    return mDeleteItemsFromOrderPricingOp;
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
  // Constructors

  /**
   * Empty Constructor.
   */
  public ShoppingCartFormHandler() {
  }

  //-----------------------------------
  // Methods

  /**
   * This method gets called by Nucleus when the component is first created.  It will
   * create a single credit card payment group and add it to the credit card payment group
   * list (creditCardPaymentGroups).
   *
   * @exception ServiceException if an error occurs
   */
  public void doStartService()
    throws ServiceException
  {
    try {
      mCreditCardPaymentGroups.add(mOrderManager.getPaymentGroupManager().createPaymentGroup(mCreditCardPaymentTypeName));
    } catch (CommerceException ce) {
      if (isLoggingError())
        logError(ce);
    }
  }

  /**
   * If it is a form submission then return true.
   */
  public boolean isFormSubmission(DynamoHttpServletRequest pRequest) {
    String args = pRequest.getQueryParameter(DropletConstants.DROPLET_ARGUMENTS);
    if (args == null) return false;
    return true;
  }

  // This method needs to stay here for backwards compatibility. Bug 36389
  public boolean isFormSubmition(DynamoHttpServletRequest pRequest) {
    return isFormSubmission(pRequest);
  }


  /**
   * If NO form errors are found, redirect to the SuccessURL.
   * If form errors are found, redirect to the FailureURL.
   * If the request coming in is for a new session (which would indicate that
   * the session has expired or been failed over) AND is a form submission
   * then we optionally redirect to a session expiration url.
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
    if ((getSessionExpirationURL() != null) &&
        (pRequest.getSession().isNew()) && (isFormSubmission(pRequest))) {
      if (getProfile().isTransient()) {
        // The session has expired, redirect to error page
        if (isLoggingDebug())
          logDebug("Session expired: redirecting to " +  getSessionExpirationURL());
        redirectOrForward(pRequest, pResponse, getSessionExpirationURL());
        return false;
      }
    }

    return super.checkFormRedirect(pSuccessURL, pFailureURL, pRequest, pResponse);
  }

  /**
   * This method gets called before an order is repriced.  It provides a hook
   * to easily extend the functionality of repriceOrder.  Currently, it does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preRepriceOrder(DynamoHttpServletRequest pRequest,
                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method gets called after all repricing is done to the order.  It currently
   * does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postRepriceOrder(DynamoHttpServletRequest pRequest,
                               DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Reprice the order.  It will first call the preRepriceOrder method.  Then it will
   * make a call into the pipeline chain that will reprice the order.  This performed
   * by calling the runProcessRepriceOrder method.  When this has completed, it will
   * call postRepriceOrder.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleRepriceOrder(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();

      //If any form errors found, redirect to error URL
      if (! checkFormRedirect(null, getRepriceOrderErrorURL(), pRequest, pResponse))
        return false;
      
      Order order = getOrder();
      synchronized(order) {
	preRepriceOrder(pRequest, pResponse);
	if (order == null) {
	  String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
	  throw new ServletException(msg);
	}

	try {
          String repriceOrderPricingOp = getRepriceOrder();
          if (repriceOrderPricingOp == null)
            repriceOrderPricingOp = PricingConstants.OP_REPRICE_ORDER_TOTAL;
          runProcessRepriceOrder(repriceOrderPricingOp, order, getUserPricingModels(), getUserLocale(pRequest,pResponse),getProfile(),null);
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_REPRICE_ORDER, pRequest, pResponse);
	}
	
	postRepriceOrder(pRequest, pResponse);
      
	try {
	  getOrderManager().updateOrder(order);
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized
      
      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect (getRepriceOrderSuccessURL(), getRepriceOrderErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

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
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleAddItemToOrder (DynamoHttpServletRequest pRequest,
                                       DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preAddItemToOrder(pRequest, pResponse);
	
	addItemToOrder(pRequest, pResponse, false);
	
	postAddItemToOrder(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
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
    }
  }

  /**
   * Adds the given item to the order (the shopping cart).  This is done by obtaining
   * the list of catalog items to be added from the catalogRefIds array.  For each
   * of these items, get the quantity that is to be added.  This is done by calling
   * the getQuantity method.  After this information has been obtained, for each
   * item that is to be added create a commerce item using this information plus the
   * properties set in the productId and catalogKey.  This commerce item is created
   * by using the OrderManager.  After the item has been created, the OrderManager
   * is used to
   *
   * <P>
   *
   * <UL>
   *    <LI>addItemToOrder - handing it the created commerce item with the users order
   *    <LI>addItemQuantityToShippingGroup - a relationship is created between the commerce
   *        item added to the order and the shipping group that is associated with the
   *        property shippingGroup.  If the shipping group property is not set on the form,
   *        then it will default to the first shipping group found in the order.
   * </UL>
   *
   * <P>
   *
   * After the item has been added to the order, the order will be repriced and a scenario
   * event will be fired of type ItemAddedToOrder.
   *
   * <P>
   * If pIsGift is true, this item is a gift and requires special handling.  The giftlistManagers
   * addGiftToOrder will get called.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pIsGift If this is true, the added items are gifts.  see
   *                the giftlist property.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void addItemToOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse,
                                boolean pIsGift)
    throws ServletException, IOException
  {
    //Fetch the order
    Order order = getOrder();
    if (order == null) {
      String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
      throw new ServletException(msg);
    }

    String [] skuIds = getCatalogRefIds();
    if ((skuIds == null) || (skuIds.length == 0)) {
      String msg = formatUserMessage(MSG_NO_ITEMS_TO_ADD, pRequest, pResponse);
      String propertyPath = generatePropertyPath("catalogRefIds");
      addFormException(new DropletFormException(msg, propertyPath, MSG_NO_ITEMS_TO_ADD));
      return;
    }

    ShippingGroup sg = getShippingGroup();
    if (sg == null) {
      String msg = formatUserMessage(MSG_ERROR_ADDING_ITEM, pRequest, pResponse);
      String propertyPath = generatePropertyPath("shippingGroup");
      addFormException(new DropletFormException(msg, propertyPath, MSG_ERROR_ADDING_ITEM));
      return;
    }

    try {
      synchronized (order) {

        //For each of the parts, check if the QTY is > 0.
        //If not, generate a form error.
        //Otherwise, add it to the order:
        for (int c=0; c<skuIds.length; c++) {
          long quantity = getQuantity(skuIds[c], pRequest, pResponse);
          if (quantity <= 0) {
	    // If the quantity is 0 it is possible this is due to the fact that the item was
	    // just removed by the deleteItems call.  Consult the deletedSkus list and
	    // determine if that sku was a part of the deleted skus.  If it was we will skip
	    // this error.
	    List deletedSkus = getDeletedSkus();
	    if (deletedSkus != null && deletedSkus.contains(skuIds[c]) ) 
	      continue;
	    
	    
            String msg = formatUserMessage(MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO, pRequest, pResponse);
            String propertyPath = generatePropertyPath("quantity");
            addFormException(new DropletFormException(msg, propertyPath, MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO));
            continue;

            //NOTE: Somewhere the QTY gets reset to 1.  Not sure we should show
            //an error about QTY being less then 0, and change the QTY. It
            //is confusing...
          }
          CommerceItemManager cim = getOrderManager().getCommerceItemManager();
          CommerceItem ci = cim.createCommerceItem(skuIds[c], getProductId(), quantity, getCatalogKey(pRequest, pResponse));
          ci = cim.addItemToOrder(getOrder(), ci);
          setCommerceItemProperties(ci, c);
          cim.addItemQuantityToShippingGroup(getOrder(), ci.getId(),
                                                           sg.getId(), quantity);

          // gifthandling
          if(pIsGift) {
            getGiftlistManager().addGiftToOrder(getProfile(), getOrder(), ci.getId(),
                                                sg, quantity, getGiftlistId(),
                                                getGiftlistItemId());
          }

          runProcessRepriceOrder(getAddItemToOrderPricingOp(), order, getUserPricingModels(),
                                 getUserLocale(pRequest,pResponse),getProfile(),null);

          runProcessAddItemToOrder(order, ci, getUserPricingModels(), getUserLocale(pRequest, pResponse), getProfile(), null);

          if (isTransactionMarkedAsRollBack())
            return;

          runProcessSendScenarioEvent(order, ci, quantity, getItemAddedToOrderEventType());
        } // for
      } // synchronized

    } catch (NumberFormatException nfe) {
      // invalid number given for quantity of item to add
      String msg = formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse);
      addFormException(new DropletException(msg, nfe, MSG_INVALID_QUANTITY));
    } catch (Exception exc) {
      processException(exc, MSG_ERROR_ADDING_TO_ORDER, pRequest, pResponse);
    }
  }

  /**
   * This method is used to add items to the order.  It will first make a call to
   * preAddItemToOrder, then it will call addItemToOrder and finally it will call
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
    Transaction tr = null;
    try {
      tr = ensureTransaction();
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getAddItemToOrderErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preAddItemToOrder(pRequest, pResponse);
	
	addConfigurableItemToOrder(pRequest, pResponse, false);
	
	postAddItemToOrder(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
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
    }
  }

  /**
   * Adds the given item to the order (the shopping cart).  This is done by obtaining
   * the list of catalog items to be added from the catalogRefIds array.  For each
   * of these items, get the quantity that is to be added.  This is done by calling
   * the getQuantity method.  After this information has been obtained, for each
   * item that is to be added create a commerce item using this information plus the
   * properties set in the productId and catalogKey.  This commerce item is created
   * by using the OrderManager.  After the item has been created, the OrderManager
   * is used to
   *
   * <P>
   *
   * <UL>
   *    <LI>addItemToOrder - handing it the created commerce item with the users order
   *    <LI>addItemQuantityToShippingGroup - a relationship is created between the commerce
   *        item added to the order and the shipping group that is associated with the
   *        property shippingGroup.  If the shipping group property is not set on the form,
   *        then it will default to the first shipping group found in the order.
   * </UL>
   *
   * <P>
   *
   * After the item has been added to the order, the order will be repriced and a scenario
   * event will be fired of type ItemAddedToOrder.
   *
   * <P>
   * If pIsGift is true, this item is a gift and requires special handling.  The giftlistManagers
   * addGiftToOrder will get called.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @param pIsGift If this is true, the added items are gifts.  see
   *                the giftlist property.
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  protected void addConfigurableItemToOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse,
                                boolean pIsGift)
    throws ServletException, IOException
  {
    //Fetch the order
    Order order = getOrder();
    if (order == null) {
      String msg = formatUserMessage(MSG_NO_ORDER_TO_MODIFY, pRequest, pResponse);
      throw new ServletException(msg);
    }

    String [] skuIds = getCatalogRefIds();
    if ((skuIds == null) || (skuIds.length == 0)) {
      String msg = formatUserMessage(MSG_NO_ITEMS_TO_ADD, pRequest, pResponse);
      String propertyPath = generatePropertyPath("catalogRefIds");
      addFormException(new DropletFormException(msg, propertyPath, MSG_NO_ITEMS_TO_ADD));
      return;
    }

    ShippingGroup sg = getShippingGroup();
    if (sg == null) {
      String msg = formatUserMessage(MSG_ERROR_ADDING_ITEM, pRequest, pResponse);
      String propertyPath = generatePropertyPath("shippingGroup");
      addFormException(new DropletFormException(msg, propertyPath, MSG_ERROR_ADDING_ITEM));
      return;
    }

    try {
      synchronized (order) {

        //For each of the parts, check if the QTY is > 0.
        //If not, generate a form error.
        //Otherwise, add it to the order:
        for (int c=0; c<skuIds.length; c++) {
          long quantity = getQuantity(skuIds[c], pRequest, pResponse);
          if (quantity <= 0) {
	          // If the quantity is 0 it is possible this is due to the fact that the item was
	          // just removed by the deleteItems call.  Consult the deletedSkus list and
	          // determine if that sku was a part of the deleted skus.  If it was we will skip
	          // this error.
	          List deletedSkus = getDeletedSkus();
	          if (deletedSkus != null && deletedSkus.contains(skuIds[c]) ) 
	            continue;
      	    
      	    
            String msg = formatUserMessage(MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO, pRequest, pResponse);
            String propertyPath = generatePropertyPath("quantity");
            addFormException(new DropletFormException(msg, propertyPath, MSG_QUANTITY_LESSTHAN_OR_EQUALTO_ZERO));
            continue;

            //NOTE: Somewhere the QTY gets reset to 1.  Not sure we should show
            //an error about QTY being less then 0, and change the QTY. It
            //is confusing...
          }
          CommerceItemManager cim = getOrderManager().getCommerceItemManager();
          ConfigurableCommerceItem ci = (ConfigurableCommerceItem) cim.createCommerceItem(
                  getConfigurableItemTypeName(), skuIds[c], null, getProductId(), null,
                  quantity, getCatalogKey(pRequest, pResponse), null);
          ci = (ConfigurableCommerceItem) cim.addItemToOrder(getOrder(), ci);
          setCommerceItemProperties(ci, c);
          cim.addItemQuantityToShippingGroup(getOrder(), ci.getId(), sg.getId(), quantity);

          // gifthandling
          if(pIsGift) {
            getGiftlistManager().addGiftToOrder(getProfile(), getOrder(), ci.getId(),
                                                sg, quantity, getGiftlistId(),
                                                getGiftlistItemId());
          }

          runProcessRepriceOrder(getAddItemToOrderPricingOp(), order, getUserPricingModels(),
                                  getUserLocale(pRequest,pResponse),getProfile(),null);

          runProcessAddItemToOrder(order, ci, getUserPricingModels(), getUserLocale(pRequest, pResponse), getProfile(), null);

          if (isTransactionMarkedAsRollBack())
            return;

          runProcessSendScenarioEvent(order, ci, quantity, getItemAddedToOrderEventType());
        } // for
      } // synchronized

    } catch (NumberFormatException nfe) {
      // invalid number given for quantity of item to add
      String msg = formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse);
      addFormException(new DropletException(msg, nfe, MSG_INVALID_QUANTITY));
    } catch (Exception exc) {
      processException(exc, MSG_ERROR_ADDING_TO_ORDER, pRequest, pResponse);
    }
  }

  /**
   * Called to fill in "extra" commerce item properties. This implementation
   * does nothing.
   *
   * @param pItem the CommerceItem
   * @param pItemIndex the index of the <code>catalogRefIds</code> array element that
   *                   corresponds to the commerce item
   * @exception CommerceException if there was an error while executing the code
   */
  protected void setCommerceItemProperties(CommerceItem pItem, int pItemIndex)
    throws CommerceException
  {
  }

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
    Transaction tr = null;
    try {
      tr = ensureTransaction();
      if (! checkFormRedirect (null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preRemoveItemFromOrder(pRequest, pResponse);
	
	try {
	  deleteItems(pRequest, pResponse);
	} catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
	
	postRemoveItemFromOrder(pRequest, pResponse);

	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized
      
      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect (getRemoveItemFromOrderSuccessURL(), getRemoveItemFromOrderErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
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
    Transaction tr = null;
    try {
      tr = ensureTransaction();
      if (! checkFormRedirect (null, getRemoveItemFromOrderErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preRemoveItemFromOrderByRelationshipId(pRequest, pResponse);
	
	try {
	  deleteItemsByRelationshipId(pRequest, pResponse);
	} catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
	
	postRemoveItemFromOrderByRelationshipId(pRequest, pResponse);

	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      return checkFormRedirect (getRemoveItemFromOrderByRelationshipIdSuccessURL(),
                                getRemoveItemFromOrderByRelationshipIdErrorURL(),
                                pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
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
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleSetOrder(DynamoHttpServletRequest pRequest,
                                DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();
      if (! checkFormRedirect (null, getSetOrderErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preSetOrder(pRequest, pResponse);
	
	try {
	  //Validate the changes the user made, and update the Order:
	  modifyOrder(pRequest, pResponse);
	  
	  //Run the appropriate Pipeline Chain:
	  runProcessSetOrder(getOrder(), getUserPricingModels(), getUserLocale(pRequest, pResponse), getProfile(), null);
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
	
	postSetOrder(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized
	
      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect (getSetOrderSuccessURL(), getSetOrderErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
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
    Transaction tr = null;
    try {
      tr = ensureTransaction();
      if (! checkFormRedirect (null, getSetOrderErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preSetOrderByRelationshipId(pRequest, pResponse);
	
	try {
	  //Validate the changes the user made, and update the Order:
	  modifyOrderByRelationshipId(pRequest, pResponse);
	  
	  //Run the appropriate Pipeline Chain:
	  runProcessSetOrder(getOrder(), getUserPricingModels(), getUserLocale(pRequest, pResponse), getProfile(), null);
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
	
	postSetOrderByRelationshipId(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect (getSetOrderByRelationshipIdSuccessURL(),
                                getSetOrderByRelationshipIdErrorURL(),
                                pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
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
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleMoveToPurchaseInfo(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();
      if (! checkFormRedirect (null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preMoveToPurchaseInfo(pRequest, pResponse);
	
	try {
	  //Validate the changes the user made, and update the Order:
	  modifyOrder(pRequest, pResponse);
	  
	  //Run the appropriate Pipeline Chain:
	  runProcessMoveToPurchaseInfo(getOrder(),getUserPricingModels(),getUserLocale(pRequest,pResponse),getProfile(),null);
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_MOVE_TO_PURCHASE_INFO, pRequest, pResponse);
	}
	
	postMoveToPurchaseInfo(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect(getMoveToPurchaseInfoSuccessURL(), getMoveToPurchaseInfoErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
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
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleMoveToPurchaseInfoByRelId(DynamoHttpServletRequest pRequest,
                                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();
      if (! checkFormRedirect (null, getMoveToPurchaseInfoErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preMoveToPurchaseInfo(pRequest, pResponse);
	
	try {
	  //Validate the changes the user made, and update the Order:
	  modifyOrderByRelationshipId(pRequest, pResponse);
	  
	  //Run the appropriate Pipeline Chain:
	  runProcessMoveToPurchaseInfo(getOrder(),getUserPricingModels(),getUserLocale(pRequest,pResponse),getProfile(),null);
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_MOVE_TO_PURCHASE_INFO, pRequest, pResponse);
	}
	
	postMoveToPurchaseInfo(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect(getMoveToPurchaseInfoByRelIdSuccessURL(),
                               getMoveToPurchaseInfoByRelIdErrorURL(),
                               pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

  /**
   * This method is called before any processing is done on the handleMoveToConfirmation
   * method is dealt with.  It allows the user to easily extend the functionality for
   * the moveToConfirmation method to take on their own functinoality.  It checks to see
   * if the billing address should be copied to the shipping address.  If it should be
   * then it calls the copyBillingAddrToShippingAddr method.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   */
  public void preMoveToConfirmation(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    // see if we need to perform any copying of address object
    if (getCopyBillingAddrToShippingAddr()) {
      if (isLoggingDebug())
        logDebug("going to copy billing address to shipping address");

      copyBillingAddrToShippingAddr(pRequest, pResponse);
    }
  }

  /**
   * Called after all processing is done by the handleMoveToConfirmation method.
   * It currently does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postMoveToConfirmation(DynamoHttpServletRequest pRequest,
                                     DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called when the user is moving to the order confirmation page.  It will call
   * the preMoveToConfirmation method, then the runProcessMoveToConfirmation method.
   * Finally, the postMoveToConfirmation will be called.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleMoveToConfirmation(DynamoHttpServletRequest pRequest,
                                          DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getMoveToConfirmationErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preMoveToConfirmation(pRequest, pResponse);
	
	if (!checkFormRedirect(null, getMoveToConfirmationErrorURL(), pRequest, pResponse))
          return false;

	try {
	  //Run the appropriate Pipeline Chain:
	  runProcessMoveToConfirmation(getOrder(),getUserPricingModels(),
				       getUserLocale(pRequest,pResponse),
				       getProfile(),null);
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_MOVE_TO_CONFIRMATION, pRequest, pResponse);
	}
	
	postMoveToConfirmation(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized
      
      return checkFormRedirect (getMoveToConfirmationSuccessURL(), getMoveToConfirmationErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

  /**
   * Called before any processing is done by the handleMoveToOrderCommit method.
   * It currently does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preMoveToOrderCommit(DynamoHttpServletRequest pRequest,
                                   DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Called after all processing is done by the handleMoveToOrderCommit method.
   * It currently does nothing.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postMoveToOrderCommit(DynamoHttpServletRequest pRequest,
                                    DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * This method is used to submit the order.  It will first call the preMoveToOrderCommit
   * method.  After this, it will ensure that the person is not trying to double submit the
   * order by seeing if the users lastOrder is is equal to the current order id.
   * If the order is okay to submit, the OrderManagers processOrder method will get called.
   * If there are no errors with processing the order, then the current order in the users
   * OrderHolder will get set to null and the submitted order will get assigned to the last
   * order property.

   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleMoveToOrderCommit(DynamoHttpServletRequest pRequest,
                                         DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();

      if (!checkFormRedirect(null, getMoveToOrderCommitErrorURL(), pRequest, pResponse))
        return false;

      // make sure they are not trying to double submit an order
      synchronized (getShoppingCart()) {
        preMoveToOrderCommit(pRequest, pResponse);

        if (!checkFormRedirect(null, getMoveToOrderCommitErrorURL(), pRequest, pResponse))
          return false;

        Order lastOrder = getShoppingCart().getLast();
        if (mOrderId != null && lastOrder != null &&
            mOrderId.equals(lastOrder.getId())) {
          // invalid number given for quantity of item to add
          String msg = formatUserMessage(MSG_ORDER_ALREADY_SUBMITTED, pRequest, pResponse);
          addFormException(new DropletException(msg, MSG_ORDER_ALREADY_SUBMITTED));
        }
        else {
          try {
            Order order = getOrder();
            synchronized(order) {
              Locale userLocale = getUserLocale(pRequest,pResponse);
              PipelineResult result = getOrderManager().processOrder(order, userLocale);
              if (! processPipelineErrors(result) &&
                  (! isTransactionMarkedAsRollBack())) {
                mLastOrder = order;
                if (getShoppingCart() != null) {
                  getShoppingCart().setLast(mLastOrder);
                  getShoppingCart().setCurrent(null);
                }
              }
            } // synchronized
          } catch (Exception exc) {
            processException(exc, MSG_ERROR_MOVE_TO_ORDER_COMMIT, pRequest, pResponse);
          }
        }

        if (!checkFormRedirect(null, getMoveToOrderCommitErrorURL(), pRequest, pResponse))
          return false;

        postMoveToOrderCommit(pRequest, pResponse);
      } // synchronized

      //If NO form errors are found, redirect to the success URL.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect (getMoveToOrderCommitSuccessURL(), getMoveToOrderCommitErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

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
   * This method checks to see if any objects have been added to RemovalCatalogRefIds property.
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
    Transaction tr = null;
    try {
      tr = ensureTransaction();
      //If any form errors found, redirect to error URL:
      if (! checkFormRedirect(null, getRemoveAndAddItemToOrderErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preRemoveAndAddItemToOrder(pRequest, pResponse);
	
	deleteItems(pRequest, pResponse);
	
	postRemoveAndAddItemToOrder(pRequest, pResponse);
	
	setAddItemToOrderErrorURL(getRemoveAndAddItemToOrderErrorURL());
	setAddItemToOrderSuccessURL(getRemoveAndAddItemToOrderSuccessURL());
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}

	return handleAddItemToOrder(pRequest, pResponse);
      } // synchronized
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }


  public void preEditItem(DynamoHttpServletRequest pRequest,
                          DynamoHttpServletResponse pRespone)
    throws ServletException, IOException
  {
  }

  public void postEditItem(DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  //-------------------------------------
  // method: handleEditItem
  //-------------------------------------

  /**
   * This method handles the push of the EDIT button for a particular Commerce Item.
   * Note: Before this is invoked, the setEditItem method is invoked.  It
   * sets the CommerceItemIdToEdit property to the ID of the item being edited.
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleEditItem (DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    Transaction tr = null;
    try {
      tr = ensureTransaction();

      //Check if any form errors exist.  If they do, redirect to Error URL:
      if (!checkFormRedirect(null, getEditItemErrorURL(), pRequest, pResponse))
        return false;

      synchronized(getOrder()) {
	preEditItem(pRequest, pResponse);
	
	postEditItem(pRequest, pResponse);
	
	try {
	  getOrderManager().updateOrder(getOrder());
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_UPDATE_ORDER, pRequest, pResponse);
	}
      } // synchronized

      //If NO form errors are found, redirect to the success URL: The Edit Item page.
      //If form errors are found, redirect to the error URL.
      return checkFormRedirect (getEditItemSuccessURL(), getEditItemErrorURL(), pRequest, pResponse);
    }
    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

  //-------------------------------------
  // method: modifyOrder
  //-------------------------------------
  /**
   * Modify the order (accessed from the <code>order</code> property) based on the changes in the request.
   * This method iterates over each of the current CommerceItems in the order and finds the current
   * quantity submitted. In addition we check to make sure that the item should even be included
   * in the order any long. If the quantity is greater than zero we adjust the quantity in the CommerceItem
   * and the ShippingGroupCommerceItemRelationship. Otherwise we remove the CommerceItem and the
   * relationships.
   */
  protected void modifyOrder(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException, RunProcessException
  {
    long quantity;
    Map changedItemMap = null; // remember which item's quantities changed
    Order order = getOrder();
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
                //change the Commerce Item quantity:
                long oldQuantity = item.getQuantity();
                item.setQuantity(quantity);
                //update the Shipping Group relationship so all
                //additions are accounted for, or are removed:
                ((ShippingGroupCommerceItemRelationship)item.getShippingGroupRelationships().get(0)).setQuantity(quantity);
              
                // remember what changed
                if (changedItemMap == null)
                  changedItemMap= new HashMap();
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
                getOrderManager().getHandlingInstructionManager().removeHandlingInstructionsFromShippingGroup(order, sg.getId(), item.getId());
              }
              long qty = item.getQuantity();
              CommerceItemManager cim = getOrderManager().getCommerceItemManager();
              cim.removeAllRelationshipsFromCommerceItem(order, item.getId());
              cim.removeItemFromOrder(order, item.getId());
              runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(),
                                     getUserLocale(pRequest,pResponse),getProfile(),null);
              runProcessSendScenarioEvent(order, item, qty, getItemRemovedFromOrderEventType());

              // decrement the counter because or order size has decreased
              i--;
            }
          }
          if(changedItemMap != null) {
            // reprice
            runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(),
                                   getUserLocale(pRequest, pResponse),getProfile(),null);
            
            if (changedItemMap != null) {
              Iterator itemIterator = changedItemMap.entrySet().iterator();
              CommerceItem commerceItem=null;
              Long longQuantity=null;
              while (itemIterator.hasNext()) {
                Map.Entry entry = (Map.Entry)itemIterator.next();
                commerceItem = (CommerceItem)entry.getKey();
                longQuantity = (Long)entry.getValue();
                runProcessSendScenarioEvent(order, commerceItem, longQuantity.longValue(), ItemQuantityChanged.TYPE);
              }
            }
          }
        }
      }
    }
    catch (NumberFormatException nfe) {
      // invalid number given for quantity of item to add
      String msg = formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse);
      addFormException(new DropletException(msg, nfe, MSG_INVALID_QUANTITY));
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
   * ShippingGroupCommerceItemRelationship is of type SHIPPINGAMOUNT.  If it is not,
   * an exception wil be thrown.
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
    Map changedItemMap = null; // remember which item's quantities changed

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
                if (changedItemMap == null)
                  changedItemMap= new HashMap();
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
          if (removalRelationshipIdList != null)
            mRemovalRelationshipIds = (String[])
              removalRelationshipIdList.toArray(mRemovalRelationshipIds);
          if (mRemovalRelationshipIds != null && mRemovalRelationshipIds.length > 0) {
            deleteItemsByRelationshipId(pRequest, pResponse);
          }
        }
        if(changedItemMap != null) {
          // reprice
          runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(),
                                 getUserLocale(pRequest, pResponse),getProfile(),null);
          
          if (changedItemMap != null) {
            Iterator itemIterator = changedItemMap.entrySet().iterator();
            CommerceItem commerceItem=null;
            Long longQuantity=null;
            while (itemIterator.hasNext()) {
              Map.Entry entry = (Map.Entry)itemIterator.next();
              commerceItem = (CommerceItem)entry.getKey();
              longQuantity = (Long)entry.getValue();
              runProcessSendScenarioEvent(order, commerceItem, longQuantity.longValue(), ItemQuantityChanged.TYPE);
            }
          }
        }
      }
    }
    catch (NumberFormatException nfe) {
      // invalid number given for quantity of item to add
      String msg = formatUserMessage(MSG_INVALID_QUANTITY, pRequest, pResponse);
      addFormException(new DropletException(msg, nfe, MSG_INVALID_QUANTITY));
    }
  }

  /**
   * Returns true if the supplied id was submitted in the form,
   * available through the catalogRefIds property
   */
  final boolean haveCatalogRefId(String pId) {
    boolean found = haveId(pId, getCatalogRefIds());
    if ((found) && (haveId(pId, getRemovalCatalogRefIds())))
      found = false;
    return found;
  }

  /**
   * Returns true if the given id is within the supplied id array
   */
  protected final boolean haveId(String pId, String [] pIds) {
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


  //-------------------------------------
  // method: runProcessAddItemToOrder
  //-------------------------------------
  /**
   * Run the pipeline which should be executed when the
   * <code>handleAddItemToOrder</code> method is invoked
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
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

  //-------------------------------------
  // method: runProcessMoveToPurchaseInfo
  //-------------------------------------
  /**
   * Run the pipeline which should be executed when the <code>handleMoveToPurchaseInfo</code> method is invoked
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
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

  //-------------------------------------
  // method: runProcessMoveToConfirmation
  //-------------------------------------
  /**
   * Run the pipeline which should be executed when the <code>handleMoveToConfirmation</code> method is invoked
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
   */
  protected void runProcessMoveToConfirmation(Order pOrder, PricingModelHolder pPricingModels,
                                              Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    HashMap params = new HashMap(11);
    params.put(PipelineConstants.CATALOGTOOLS, getCatalogTools());
    params.put(PipelineConstants.INVENTORYMANAGER, getOrderManager().getOrderTools().getInventoryManager());
    
    PipelineResult result = runProcess(getMoveToConfirmationChainId(), pOrder, pPricingModels, pLocale,
                                       pProfile, params, pExtraParameters);
    processPipelineErrors(result);
  }

  //-------------------------------------
  // method: runProcessSetOrder
  //-------------------------------------
  /**
   * Run the pipeline which should be executed when the <code>handleSetOrder</code> method is invoked
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
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
   * Run the pipeline which should be executed when the order needs to be repriced. This method
   * call defaults to the the pricing operation ORDER_TOTAL.
   * @see atg.commerce.pricing.PricingConstants.OP_REPRICE_ORDER_TOTAL.
   * @param pOrder the order to reprice
   * @param pPricingModels the set of all pricing models for the user (item, order, shipping, tax)
   * @param pLocale the locale that the order should be priced within
   * @param pProfile the user who owns the order
   * @param pExtraParameters A Map of extra parameters to be used in the pricing
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
   * This method is here to preserve backward compatibility. We needed to add a quantity param
   * to add to the message.
   */
  protected void runProcessSendScenarioEvent(Order pOrder,
                                             CommerceItem pItem, String pType)
    throws RunProcessException
  {
    runProcessSendScenarioEvent(pOrder, pItem, 1, pType);
  }

  protected void runProcessSendScenarioEvent(Order pOrder,
                                             CommerceItem pItem, long pQuantity, String pType)
    throws RunProcessException
  {
    // Send a scenario event on a successfully added item.
    PipelineResult result;
    HashMap params = new HashMap();

    params.put(PipelineConstants.ORDER, pOrder);
    params.put(PipelineConstants.COMMERCEITEM, pItem);
    params.put(PipelineConstants.EVENT, pType);
    params.put(PipelineConstants.QUANTITY, Long.valueOf(pQuantity));

    addMoreParams(params);

    result = getPipelineManager().runProcess(PipelineConstants.SENDSCENARIOEVENT, params);
    processPipelineErrors(result);

  }

  /**
   * This helper method allows me to easily add more parameters before running the pipeline process.
   * Override it when more params need to be added.
   */
  protected void addMoreParams(HashMap pParam) {
    
  }


  //-------------------------------------
  // method: runRepricingProcess
  //-------------------------------------

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
  protected PipelineResult runRepricingProcess(String pChainId, String pPricingOperation,
                                               Order pOrder, PricingModelHolder pPricingModels,
                                               Locale pLocale, RepositoryItem pProfile, Map pExtraParameters)
    throws RunProcessException
  {
    if (pChainId == null)
      return null;

    try {
      HashMap params = new HashMap();
      if (pPricingOperation != null)
        params.put(PricingConstants.PRICING_OPERATION_PARAM, pPricingOperation);
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
   * This method obtains the default shipping and billing group from the order
   * object.  It then attempt to cast these object to a specific implementation
   * of the ShippingGroup and PaymentGroup interface (namely HardgoodShippingGroup
   * and CreditCard).  If there is a problem doing the cast then an exception is logged
   * and this method exits with a false value.  The cast is necessary in order to get at
   * an object that actually has address information.
   *
   * @param pRequest the ServletRequest object
   * @param pResponse the ServletResponse object
   * @return true if the address was copied successfully.
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void copyBillingAddrToShippingAddr(DynamoHttpServletRequest pRequest,
                                            DynamoHttpServletResponse pResponse)
    throws ServletException,
           IOException

  {
    try {
      ShippingGroup sg = getShippingGroup();
      if (sg == null) {
        String msg = formatUserMessage(MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
        addFormException(new DropletException(msg, MSG_UNABLE_TO_COPY_ADDRESS));
        return;
      }

      PaymentGroup pg = getPaymentGroup();
      if (pg == null) {
        String msg = formatUserMessage(MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
        addFormException(new DropletException(msg, MSG_UNABLE_TO_COPY_ADDRESS));
        return;
      }

      HardgoodShippingGroup hgShippingGroup = (HardgoodShippingGroup) sg;
      CreditCard ccBillingGroup = (CreditCard) pg;

      Address billingAddress = ccBillingGroup.getBillingAddress();
      Address shippingAddress = hgShippingGroup.getShippingAddress();
      OrderTools.copyAddress(billingAddress, shippingAddress);
    } catch (ClassCastException cce) {
      processException(cce, MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
    } catch (CommerceException ce) {
      processException(ce, MSG_UNABLE_TO_COPY_ADDRESS, pRequest, pResponse);
    }
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
   */
  protected void deleteItemsByRelationshipId(DynamoHttpServletRequest pRequest,
                                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException, CommerceException
  {
    ShippingGroupCommerceItemRelationship sgCiRel;
    long shippingQuantity;
    CommerceItem ci;
    Order order = getOrder();
    Map changedItemMap = null; // remember which item's quantities changed

    synchronized(order) {
      if (mRemovalRelationshipIds != null &&
	  mRemovalRelationshipIds.length > 0) {
	try {
	  
	  for (int i=0; i<mRemovalRelationshipIds.length; i++) {
	    sgCiRel = (ShippingGroupCommerceItemRelationship)
	      order.getRelationship((mRemovalRelationshipIds[i]));
	    if (sgCiRel.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
	      ci = sgCiRel.getCommerceItem();
	      getOrderManager().getHandlingInstructionManager().removeHandlingInstructionsFromShippingGroup(order,
													    sgCiRel.getShippingGroup().getId(),
													    ci.getId());
	      CommerceItemManager cim = getOrderManager().getCommerceItemManager();
	      if (sgCiRel.getQuantity() >= ci.getQuantity()) {
		// remove item from order
		long quantity = ci.getQuantity();
		cim.removeAllRelationshipsFromCommerceItem(order, ci.getId());
		cim.removeItemFromOrder(order, ci.getId());
		runProcessRepriceOrder(getDeleteItemsFromOrderPricingOp(), order,
				       getUserPricingModels(), getUserLocale(pRequest,pResponse),
				       getProfile(),null);
		runProcessSendScenarioEvent(order, ci, quantity, getItemRemovedFromOrderEventType());
		
	      }
	      else {
          // don't remove commerce item from order, reduce quantity and remove relationship
          long oldQuantity = ci.getQuantity();
          ci.setQuantity(ci.getQuantity() - sgCiRel.getQuantity());
          cim.removeItemQuantityFromShippingGroup(order, ci.getId(),
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
      // reprice
      runProcessRepriceOrder(getModifyOrderPricingOp(), order, getUserPricingModels(),
                             getUserLocale(pRequest, pResponse),getProfile(),null);
      
      if (changedItemMap != null) {
        Iterator itemIterator = changedItemMap.entrySet().iterator();
        CommerceItem commerceItem=null;
        Long longQuantity=null;
        while (itemIterator.hasNext()) {
          Map.Entry entry = (Map.Entry)itemIterator.next();
          commerceItem = (CommerceItem)entry.getKey();
          longQuantity = (Long)entry.getValue();
              runProcessSendScenarioEvent(order, commerceItem, longQuantity.longValue(), ItemQuantityChanged.TYPE);
        }
      }
    }
	  // update the order in the repository
          getOrderManager().updateOrder(order);
        
	}
	catch (Exception exc) {
	  processException(exc, MSG_ERROR_REMOVING_ITEM, pRequest, pResponse);
	}
      }
    } // synchronized
  }

  /**
   * Delete all items from the order whose id appears in the RemovalCommerceIds
   * property.
   *
   * @param pRequest servlet request object
   * @param pResponse servlet response object
   */
  protected void deleteItems(DynamoHttpServletRequest pRequest,
                             DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    String commerceId;
    Order order = getOrder();
    List deletedSkus = new ArrayList();
    synchronized(order) {
      if (mRemovalCommerceIds != null && mRemovalCommerceIds.length > 0) {
	try {
	  for (int i=0; i<mRemovalCommerceIds.length; i++) {
	    commerceId = mRemovalCommerceIds[i];
	    // get the CommerceItem
	    CommerceItem item = order.getCommerceItem(commerceId);
	    long quantity = item.getQuantity();
	    CommerceItemManager cim = getOrderManager().getCommerceItemManager();
	    cim.removeAllRelationshipsFromCommerceItem(order, commerceId);
	    cim.removeItemFromOrder(order, commerceId);
	    deletedSkus.add(item.getCatalogRefId());
	    runProcessRepriceOrder(getDeleteItemsFromOrderPricingOp(), order, getUserPricingModels(),
				   getUserLocale(pRequest,pResponse),getProfile(),null);
	    runProcessSendScenarioEvent(order, item, quantity, getItemRemovedFromOrderEventType());
	  }
	  
	  getOrderManager().updateOrder(order);
	} catch (Exception e) {
	  processException(e, MSG_ERROR_REMOVING_ITEM, pRequest, pResponse);
	}
      }
    } // synchronized

    setDeletedSkus(deletedSkus);
  }

  /**
   * Get a string that will be used to identify the catalog to use when obtaining
   * a catalogRef and productRef for the creation of a commerce item.  CatalogTools
   * will maintain a mapping of key to catalog, currently this mapping will be maintained
   * by the users locale.
   *
   * @see atg.commerce.catalog.CatalogTools
   * @param pRequest servlet request object
   * @param pResponse servlet response object
   * @return the catalog key
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
    List ciRels = getOrderManager().getCommerceItemManager().getAllCommerceItemRelationships(pOrder);
    List sgRels = getOrderManager().getShippingGroupManager().getAllShippingGroupRelationships(pOrder);
    ciRels.retainAll(sgRels);
    return ciRels;
  }

  /**
   * Empty method that can be overriden to provide additional functionality
   * if desired.  Called before any processing is done by the
   * handleSaveOrder method.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void preSaveOrder(DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }

  /**
   * Empty method that can be overriden to provide additional functionality
   * if desired.  Called after all processing is done by the
   * handleSaveOrder method.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  public void postSaveOrder(DynamoHttpServletRequest pRequest,
                            DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
  }
  /* This method actually saves the order and making a new empty order as current shopping
   * cart. The description is set to order before saving the order,
   * if description is not set then a String in user's locale representing date
   * and time is stored by dafault.
   *
   * @param pRequest the request object
   * @param pResponse the response object
   * @exception ServletException if an error occurs
   * @exception IOException if an error occurs
   */
  protected void saveOrder(DynamoHttpServletRequest pRequest,
                           DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    try {
      OrderHolder ShoppingCart = getShoppingCart();
      Order oldOrder = ShoppingCart.getCurrent();

      if (oldOrder != null) {

        String description = getDescription();
        if (checkDuplicateDescription(description)) {
          if (isStringEmpty(description)) {
            Locale userLocale = getUserLocale(pRequest,pResponse);
            description = DateFormat.getDateTimeInstance(
                                                         DateFormat.FULL,
                                                         DateFormat.FULL,
                                                         userLocale).format(new Date());
          }
          oldOrder.setDescription(description);
          getOrderManager().updateOrder(oldOrder);
          ShoppingCart.getSaved().add(oldOrder);
          ShoppingCart.setCurrent(null);
          runProcessSendScenarioEvent(oldOrder, null, 0,
                                      getOrderSavedEventType());
        }
        else
        {
          String msg = formatUserMessage(MSG_DUPLICATE_ORDER_DESCRIPTION,
                                         pRequest, pResponse);
          String propertyPath = generatePropertyPath("discription");
          addFormException(new DropletFormException(msg, propertyPath, MSG_DUPLICATE_ORDER_DESCRIPTION));
        }

      }
    }
    catch (CommerceException exc) {
      if (isLoggingError())
        logError(exc);
      throw new ContainerServletException(exc);
    }
    catch (RunProcessException exc) {
      if (isLoggingError())
        logError(exc);
      throw new ContainerServletException(exc);
    }
  }

  /**
   * This method is used to save order.  It will first make a call to
   * <code>preSaveOrder</code>, then it will call<code>saveOrder</code> and finally it will call
   * <code>postSaveOrder</code>. It sets the description to the order if provided, otherwise
   * it just sets a String in user's Locale representing date and time.
   *
   * @param pRequest the servlet's request
   * @param pResponse the servlet's response
   * @return true if the request was properly handled
   * @exception ServletException if there was an error while executing the code
   * @exception IOException if there was an error with servlet io
   */
  public boolean handleSaveOrder(DynamoHttpServletRequest pRequest,
                                 DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    OrderHolder ShoppingCart = getShoppingCart();
    Transaction tr = null;
    try {
      tr = ensureTransaction();

      synchronized(getOrder()) {
	preSaveOrder(pRequest, pResponse);
	saveOrder(pRequest, pResponse);
	postSaveOrder(pRequest, pResponse);
      } // synchronized

      return checkFormRedirect (getSaveOrderSuccessURL(),
                                getSaveOrderErrorURL(),
                                pRequest, pResponse);
    }

    finally {
      if (tr != null) commitTransaction(tr);
    }
  }

  /**
   * Checks for duplicate Descriptions among the Saved orders in OrderHolder
   * componenet.
   * @return true if no duplicates are found
   */
  protected boolean checkDuplicateDescription(String pDescription)
  {
    OrderHolder ShoppingCart = getShoppingCart();
    Iterator iterator = ShoppingCart.getSaved().iterator();
    while (iterator.hasNext()) {
      Order order = (Order)iterator.next();
      if (order != null) {
        if (order.getDescription() == null ||
            !order.getDescription().equals(pDescription)) {
          continue;
        }
        else
        {
          return false;
        }
      }
    }
    return true;
  }

  //---------------------------------------------------------------------------

  /**
   * Check to see if the user's session has expired and redirect
   * to <tt>sessionExpirationURL</tt> if so.
   **/
  
  public boolean handleCheckForExpiredSession(DynamoHttpServletRequest pRequest,
                                              DynamoHttpServletResponse pResponse)
    throws ServletException, IOException
  {
    if (isLoggingDebug())
      logDebug("Checking for expired session");
    return checkFormRedirect(null, null, pRequest, pResponse);
  }

} // end of ShoppingCartFormHandler class




