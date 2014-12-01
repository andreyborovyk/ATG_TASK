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
package atg.projects.store.order;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import atg.commerce.CommerceException;
import atg.commerce.gifts.GiftlistHandlingInstruction;
import atg.commerce.gifts.GiftlistManager;
import atg.commerce.inventory.InventoryException;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.CommerceItemManager;
import atg.commerce.order.HandlingInstruction;
import atg.commerce.order.HandlingInstructionManager;
import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupRelationship;
import atg.commerce.order.SimpleOrderManager;
import atg.commerce.pricing.OrderPriceInfo;
import atg.commerce.pricing.ShippingPriceInfo;
import atg.commerce.pricing.TaxPriceInfo;
import atg.commerce.profile.CommercePropertyManager;
import atg.commerce.states.CommerceItemStates;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.core.util.StringUtils;
import atg.projects.store.logging.LogUtils;
import atg.projects.store.order.purchase.StoreAddCommerceItemInfo;
import atg.repository.RepositoryException;
import atg.repository.RepositoryItem;
import atg.service.idgen.IdGenerator;
import atg.service.idgen.IdGeneratorException;


/**
 * <p>
 * The class extends the ATG SimpleOrderManager. The main functionality
 * added to this class is related to gift services. The business logic for
 * addition and removal of gift message and gift wrap is here.
 *
 * <p>
 * Also included in this class is the logic for building a set of
 * AddCommerceItemInfo objects based on an Order.
 *
 * @author ATG
 * @version $Revision: #2 $
 */
public class StoreOrderManager extends SimpleOrderManager {
  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/StoreOrderManager.java#2 $$Change: 651448 $";

  /**
   * Resource bundle name.
   */
  private static final String MY_RESOURCE_NAME = "atg.commerce.inventory.Resources";

  /**
   * Resource bundle.
   */
  static ResourceBundle sResources = LayeredResourceBundle.getBundle(MY_RESOURCE_NAME,
      atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Item not found property name.
   */
  private static final String ITEM_NOT_FOUND = "noSuchItem";

  /**
   * Shoulf adjust inventory on checkout.
   */
  private boolean mAdjustInventoryOnCheckout;

  /**
   * Commerce items states.
   */
  private CommerceItemStates mCommerceItemStates;

  /**
   * Gift list manager.
   */
  private GiftlistManager mGiftlistManager;

  /**
   * Add item information class.
   */
  String mAddItemInfoClass = "atg.projects.store.order.purchase.StoreAddCommerceItemInfo";

  /**
   * OMS transaction id generator.
   */
  private IdGenerator mOMSTransactionIdGenerator;

  /**
   * OMS transaction id space.
   */
  private String mOMSTransactionIdSpace = "storeOMSTransaction";

  /**
   * @return true if inventory should be decremented on checkout,
   * false - otherwise.
   */
  public boolean isAdjustInventoryOnCheckout() {
    return mAdjustInventoryOnCheckout;
  }

  /**
   * @param pAdjustInventoryOnCheckout -
   * boolean indicating if inventory should be decremented on checkout.
   */
  public void setAdjustInventoryOnCheckout(boolean pAdjustInventoryOnCheckout) {
    mAdjustInventoryOnCheckout = pAdjustInventoryOnCheckout;
  }

  /**
   * @return the commerce item states component.
   */
  public CommerceItemStates getCommerceItemStates() {
    return mCommerceItemStates;
  }

  /**
   * @param pCommerceItemStates - the commerce item states component.
   */
  public void setCommerceItemStates(CommerceItemStates pCommerceItemStates) {
    mCommerceItemStates = pCommerceItemStates;
  }

  /**
   * Specifies the GiftlistManager.
   * @param pGiftlistManager a <code>GiftlistManager</code> value
   */
  public void setGiftlistManager(GiftlistManager pGiftlistManager) {
    mGiftlistManager = pGiftlistManager;
  }

  /**
   * The giftlist manager.
   * @return a <code>GiftlistManager</code> value
   */
  public GiftlistManager getGiftlistManager() {
    return mGiftlistManager;
  }

  /**
   * Sets the name of the class to be used for elements in the items array.
   * @param pAddItemInfoClass -
   * the name of the class to be used for elements in the items array
   */
  public void setAddItemInfoClass(String pAddItemInfoClass) {
    mAddItemInfoClass = pAddItemInfoClass;
  }

  /**
   * @return the name of the class to be used for elements in the items array.
   *
   * @beaninfo
   *   description: The name of the class used for objects containing per-item properties
   **/
  public String getAddItemInfoClass() {
    return mAddItemInfoClass;
  }

  /**
   * Specifies the OMSTransactionIdGenerator.
   * @param pOMSTransactionIdGenerator a <code>OMSTransactionIdGenerator</code> value
   */
  public void setOMSTransactionIdGenerator(IdGenerator pOMSTransactionIdGenerator) {
    mOMSTransactionIdGenerator = pOMSTransactionIdGenerator;
  }

  /**
   * The OMSTransactionIdGenerator.
   * @return a <code>OMSTransactionIdGenerator</code> value
   */
  public IdGenerator getOMSTransactionIdGenerator() {
    return mOMSTransactionIdGenerator;
  }

  /**
   * Specifies the OMSTransactionIdSpace.
   * @param pOMSTransactionIdSpace a <code>OMSTransactionIdSpace</code> value
   */
  public void setOMSTransactionIdSpace(String pOMSTransactionIdSpace) {
    mOMSTransactionIdSpace = pOMSTransactionIdSpace;
  }

  /**
   * The OMSTransactionIdSpace.
   * @return a <code>OMSTransactionIdSpace</code> value
   */
  public String getOMSTransactionIdSpace() {
    return mOMSTransactionIdSpace;
  }

  /**
   * Adds the gift message to the order's special instructions.
   *
   * @param pOrder -
   *            the order to remove gift wrap from
   * @param pMessageTo -
   *            the "message to:" field
   * @param pMessage -
   *            the message body
   * @param pMessageFrom -
   *            the "message from:" field
   * @exception CommerceException
   *                if an error occurs removing item from order
   */
  public void addGiftMessage(StoreOrderImpl pOrder, String pMessageTo, String pMessage, String pMessageFrom)
    throws CommerceException {
    // Issues with using "put" when key/value pair is already in the Map.
    // As a result, remove keys and values explicitly before put.
    removeGiftMessage(pOrder);
    
    // Get the special instructions from the order, and add
    //  empty values for gift messages
    Map specialInstructions = pOrder.getSpecialInstructions();

    if (isLoggingDebug()) {
      logDebug("Adding gift message. Current gm to: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_TO_KEY) + ", gm: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_KEY) + ", gm from: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY));
    }

    specialInstructions.put(StoreOrderImpl.GIFT_MESSAGE_TO_KEY, pMessageTo);
    if (!StringUtils.isEmpty(pMessage)) {
      specialInstructions.put(StoreOrderImpl.GIFT_MESSAGE_KEY, pMessage);
    }
    specialInstructions.put(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY, pMessageFrom);

    updateOrder(pOrder);
  }

  /**
   * Removes the gift message from the order.
   *
   * @param pOrder -
   *            the order to remove gift wrap from
   * @exception CommerceException
   *                if an error occurs removing item from order
   */
  public void removeGiftMessage(StoreOrderImpl pOrder)
    throws CommerceException {
    Map specialInstructions = pOrder.getSpecialInstructions();

    if (isLoggingDebug()) {
      logDebug("Removing gift message. Current gm to: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_TO_KEY) + ", gm: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_KEY) + ", gm from: " +
        specialInstructions.get(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY));
    }

    // Remove the gift message special instructions from the order
    specialInstructions = pOrder.getSpecialInstructions();

    Set instructionKeys = specialInstructions.keySet();

    // Remove gift message to
    if (instructionKeys.contains(StoreOrderImpl.GIFT_MESSAGE_TO_KEY)) {
      if (isLoggingDebug()) {
        logDebug("Removing specialInstructions entry for gift message to.");
      }

      specialInstructions.remove(StoreOrderImpl.GIFT_MESSAGE_TO_KEY);

      if (isLoggingDebug()) {
        logDebug("Removing instructions key for gift message to.");
      }

      instructionKeys.remove(StoreOrderImpl.GIFT_MESSAGE_TO_KEY);
    }

    // Remove gift message
    if (instructionKeys.contains(StoreOrderImpl.GIFT_MESSAGE_KEY)) {
      if (isLoggingDebug()) {
        logDebug("Removing specialInstructions entry for gift message.");
      }

      specialInstructions.remove(StoreOrderImpl.GIFT_MESSAGE_KEY);

      if (isLoggingDebug()) {
        logDebug("Removing instructions key for gift message.");
      }

      instructionKeys.remove(StoreOrderImpl.GIFT_MESSAGE_KEY);
    }

    // Remove gift message from
    if (instructionKeys.contains(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY)) {
      if (isLoggingDebug()) {
        logDebug("Removing specialInstructions entry for gift message from.");
      }

      specialInstructions.remove(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY);

      if (isLoggingDebug()) {
        logDebug("Removing instructions key for gift message from.");
      }

      instructionKeys.remove(StoreOrderImpl.GIFT_MESSAGE_FROM_KEY);
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
  public void addRemoveGiftServices(StoreOrderImpl pOrder, boolean pAddGiftWrap, boolean pAddGiftMessage,
    String pGiftWrapSkuId, String pGiftWrapProductId) {
    try {
      if (pAddGiftWrap) {
        // user wants gift wrap
        if (!pOrder.getContainsGiftWrap()) {
          // gift wrap item not in order, add now
          addGiftWrap(pOrder, pGiftWrapSkuId, pGiftWrapProductId);
        }
      } else {
        // user does not want gift wrap
        if (pOrder.getContainsGiftWrap()) {
          removeGiftWrap(pOrder);
        }
      }

      if (pAddGiftMessage) {
        // user wants gift message
        if (!pOrder.getContainsGiftMessage() && !pOrder.isShouldAddGiftNote()) {
          pOrder.setShouldAddGiftNote(true);
        }
      } else {
        // user does not want gift message
        if (pOrder.getContainsGiftMessage() || pOrder.isShouldAddGiftNote()) {
          // remove it from the order
          removeGiftMessage(pOrder);
          pOrder.setShouldAddGiftNote(false);
        }
      }

      updateOrder(pOrder);
    } catch (CommerceException ce) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error adding/removing gift services." + ce));
      }
    }
  }

  /**
   * Creates and adds the gift wrap commerce item to the order.
   *
   * @param pOrder - the order to add gift wrap to
   * @param pSkuId - sku id
   * @param pProductId - product id
   * @exception CommerceException
   *                if error creating item or adding item to order
   */
  @SuppressWarnings("unchecked") //Ok here, cause we know which Collections we use here
  public void addGiftWrap(StoreOrderImpl pOrder, String pSkuId, String pProductId)
    throws CommerceException {
    if (isLoggingDebug()) {
      logDebug("Gift wrap item being added. Sku id: " + pSkuId + ", prod id: " + pProductId);
    }

    if (pOrder.getContainsGiftWrap()) {
      if (isLoggingWarning()) {
        logWarning("This order already has gift wrap.");
      }

      return;
    }

    ShippingGroup sg = null;
    List<ShippingGroup> shippingGroups = pOrder.getShippingGroups();
    for (ShippingGroup shippingGroup: shippingGroups)
    {
      //Search for gift shipping groups first.
      List handlingInstructions = shippingGroup.getHandlingInstructions();
      if (handlingInstructions != null && handlingInstructions.size() > 0) //If found, use it
      {
        sg = shippingGroup;
        break;
      }
    }
    //If gift shipping group is not found, get the first comer
    sg = sg == null ? (ShippingGroup) pOrder.getShippingGroups().get(0) : sg;

    CommerceItemManager ciManager = getCommerceItemManager();
    long giftWrapQuantity = 1;

    StoreOrderTools orderTools = (StoreOrderTools) getOrderTools();

    CommerceItem giftWrapItem = ciManager.createCommerceItem(orderTools.getGiftWrapCommerceItemType(), pSkuId,
        pProductId, giftWrapQuantity);

    ciManager.addItemToOrder(pOrder, giftWrapItem);

    ciManager.addItemQuantityToShippingGroup(pOrder, giftWrapItem.getId(), sg.getId(), giftWrapQuantity);

    List list = sg.getHandlingInstructions();

    if ((list != null) && (list.size() > 0)) {
      // If this is a gift shipping group, and we are adding gift wrap to it, we need to add
      // a handling instruction so that ShippingGroupDroplet/FormHandler won't attempt to
      // add the gift wrap to a different shipping group
      HandlingInstructionManager himgr = getHandlingInstructionManager();
      HandlingInstruction handlingInstruction = (HandlingInstruction) list.get(0);
      HandlingInstruction newHandlingInstruction = himgr.copyHandlingInstruction(handlingInstruction);
      newHandlingInstruction.setCommerceItemId(giftWrapItem.getId());
      sg.addHandlingInstruction(newHandlingInstruction);
    }

    updateOrder(pOrder);
  }

  /**
   * Removes the gift wrap commerce item from the order.
   *
   * @param pOrder -
   *            the order to remove gift wrap from
   * @exception CommerceException
   *                if an error occurs removing item from order
   */
  public void removeGiftWrap(StoreOrderImpl pOrder)
    throws CommerceException {
    int itemCount = pOrder.getCommerceItemCount();
    List items = pOrder.getCommerceItems();
    CommerceItem item;
    CommerceItemManager ciManager = getCommerceItemManager();
    String giftWrapItemId = null;

    for (int i = 0; i < itemCount; i++) {
      item = (CommerceItem) items.get(i);

      if (item instanceof GiftWrapCommerceItem) {
        giftWrapItemId = item.getId();
      }
    }

    // Don't remove the item while looping around commerce items because you'll
    // see an index out of bounds exception if the gift wrap item isn't the last
    // item. Instead, do it here, then update order.
    if (giftWrapItemId != null) {
      ciManager.removeItemFromOrder(pOrder, giftWrapItemId);
    }

    updateOrder(pOrder);
  }

  /**
   * Returns a List of AddCommerceItemInfos based on the order param. This is
   * used when logging the user out, and copying all the commerce items, but
   * not the Shipping and Payment groups to the new order. This method builds
   * the list of AddCommerceItemInfos to send to the copyItems method.
   *
   * @param pOrder order
   * @return list of AddCommerceItemInfos
   */
  public List buildItemInfos(Order pOrder) {
    List addItemInfos = new ArrayList();
    List commerceItems = pOrder.getCommerceItems();
    GiftlistManager glManager = getGiftlistManager();
    int commerceItemCount = pOrder.getCommerceItemCount();

    try {
      for (int i = 0; i < commerceItemCount; i++) {
        CommerceItem currentItem = (CommerceItem) commerceItems.get(i);
        long itemQuantity = currentItem.getQuantity();
        long giftQuantity = glManager.getGiftQuantity(pOrder, currentItem);

        if (giftQuantity > 0) {
          Collection itemInfos = createItemsForGift(pOrder, currentItem);
          addItemInfos.addAll(itemInfos);
          itemQuantity -= giftQuantity;
        }

        //handle anything left after gifts
        if (itemQuantity > 0) {
          StoreAddCommerceItemInfo itemInfo = createAddCommerceItemInfo(currentItem);
          itemInfo.setQuantity(itemQuantity);
          addItemInfos.add(itemInfo);
        }
      }
    } catch (CommerceException e) {
      if (isLoggingError()) {
        logError(e);
      }

      return null;
    }

    return addItemInfos;
  }

  /**
   * Creates a collection of AddCommerceItemInfo objects for each gift relationship for an item.
   * @param pOrder - order
   * @param pItem - item
   * @throws CommerceException
   *                If an error occurs creating item for gift
   * @return collection of AddCommerceItemInfo objects for each gift relationship for an item
   */
  protected Collection createItemsForGift(Order pOrder, CommerceItem pItem)
    throws CommerceException {
    GiftlistManager glManager = getGiftlistManager();
    List addItemInfos = new ArrayList();

    //loop through the relationships for the item
    //for each shipping group, get the gift quantity for the item
    //create a AddCommerceItemInfo for each quantity
    Collection shippingGroupRelationships = pItem.getShippingGroupRelationships();
    Iterator relerator = shippingGroupRelationships.iterator();
    ShippingGroupRelationship rel;
    ShippingGroup sg;
    Collection handlingInstructions;

    while (relerator.hasNext()) {
      rel = (ShippingGroupRelationship) relerator.next();
      sg = rel.getShippingGroup();
      handlingInstructions = glManager.getGiftHandlingForShippingGroup(sg, pItem);

      if ((handlingInstructions != null) && (handlingInstructions.size() > 0)) {
        //create a new item for each handling instruction
        Iterator hierator = handlingInstructions.iterator();
        GiftlistHandlingInstruction glhi;

        while (hierator.hasNext()) {
          glhi = (GiftlistHandlingInstruction) hierator.next();

          StoreAddCommerceItemInfo itemInfo = createAddCommerceItemInfo(pItem);
          itemInfo.setQuantity(glhi.getQuantity());
          itemInfo.setGiftlistId(glhi.getGiftlistId());
          itemInfo.setGiftlistItemId(glhi.getGiftlistItemId());
          addItemInfos.add(itemInfo);
        }
      }
    }

    return addItemInfos;
  }

  /**
   * Creates a StoreAddCommerceItemInfo object for an item. The (sub)class of
   * AddCommerceItemInfo is configurable. Our caller is responsible for filling
   * in the quantity property in the returned object.
   *
   * @param pItem the item whose properties should be copied
   * @return an object containing properties needed to recreate a commerce item
   * @throws atg.commerce.CommerceException if an error occurs
   */
  protected StoreAddCommerceItemInfo createAddCommerceItemInfo(CommerceItem pItem)
    throws CommerceException {
    try {
      StoreAddCommerceItemInfo itemInfo = (StoreAddCommerceItemInfo) Class.forName(getAddItemInfoClass())
                                                                                  .newInstance();
      itemInfo.setCommerceItemType(pItem.getCommerceItemClassType());
      itemInfo.setCatalogRefId(pItem.getCatalogRefId());
      itemInfo.setProductId(pItem.getAuxiliaryData().getProductId());

      return itemInfo;
    } catch (ClassNotFoundException cnfe) {
      throw new CommerceException(cnfe);
    } catch (IllegalAccessException iae) {
      throw new CommerceException(iae);
    } catch (InstantiationException ie) {
      throw new CommerceException(ie);
    }
  }

  /**
   * This method sets the state of each commerce item based on the sku's current
   * inventory availability.  If adjustInventoryOnCheckout = true, it also
   * decrements the inventory levels of the items in the order.
   *
   * @param pOrder Order whose commerce items statuses are to be updated in inventory.
   * description: This method takes the order and iterates through commerce items
   *              to update the inventory status
   * @throws atg.commerce.inventory.InventoryException if an error occurs
   */
  public void manageInventoryOnCheckout(Order pOrder) throws InventoryException {
    InventoryManager inventoryManager = getOrderTools().getInventoryManager();
    List commerceItems = pOrder.getCommerceItems();
    Iterator iter = commerceItems.iterator();

    while (iter.hasNext()) {
      CommerceItem commItem = (CommerceItem) iter.next();
      long quantity = commItem.getQuantity();
      String id = commItem.getCatalogRefId();

      if (isLoggingDebug()) {
        logDebug("inside adjustInventory . Sku id: " + id);
        logDebug("inside adjustInventory . quantity: " + quantity);
      }

      //check for the status and call the appropriate method
      try {
        int status = checkStatus(commItem);

        if (status == InventoryManager.AVAILABILITY_STATUS_PREORDERABLE) {
          if (isLoggingDebug()) {
            logDebug("item is preorderable");
          }

          commItem.setState(getCommerceItemStates().getStateValue(CommerceItemStates.PRE_ORDERED));

          if (isAdjustInventoryOnCheckout()) {
            int inventoryStatus = inventoryManager.preorder(id, quantity);

            if (inventoryStatus == InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
              inventoryManager.setPreorderLevel(id, 0);
            }
          }
        } else if (status == InventoryManager.AVAILABILITY_STATUS_BACKORDERABLE) {
          if (isLoggingDebug()) {
            logDebug("item is backorderable");
          }

          commItem.setState(getCommerceItemStates().getStateValue(CommerceItemStates.BACK_ORDERED));

          if (isAdjustInventoryOnCheckout()) {
            int inventoryStatus = inventoryManager.backorder(id, quantity);

            if (inventoryStatus == InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
              inventoryManager.setBackorderLevel(id, 0);
            }
          }
        } else if (status == InventoryManager.AVAILABILITY_STATUS_IN_STOCK) {
          if (isLoggingDebug()) {
            logDebug("item is in stock");
          }

          if (isAdjustInventoryOnCheckout()) {
            int inventoryStatus = inventoryManager.purchase(id, quantity);

            if (inventoryStatus == InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
              inventoryManager.setStockLevel(id, 0);
            }
          }
        } else {
          if (isLoggingDebug()) {
            logDebug("Availability Status: " + inventoryManager.queryAvailabilityStatus(id));
          }
        }
      } catch (InventoryException e) {
        Object[] args = { id };

        if (isLoggingWarning()) {
          logWarning(ResourceUtils.getMsgResource(ITEM_NOT_FOUND, MY_RESOURCE_NAME, sResources, args));
        }
      }
    }
  }

  /**
   * This method uses the inventory manager to query the availability status of the
   * given commerce item.
   *
   * @param pCommItem commerce item whose status is to be checked in inventory.
   * @return return status either preoderable or backorderable or instock
   *
   * @throws atg.commerce.inventory.InventoryException if an error occurs
   */
  public int checkStatus(CommerceItem pCommItem) throws InventoryException {
    InventoryManager inventoryManager = getOrderTools().getInventoryManager();
    String skuId = pCommItem.getCatalogRefId();

    return inventoryManager.queryAvailabilityStatus(skuId);
  }

  /**
   * Use the OMSTransactionIdGenerator to get the next Transaction Id.
   *
   * @return New transactionId generated by OMSTransactionIdGenerator
   *
   */
  public String getOMSTransactionId() {
    IdGenerator IdGenerator = getOMSTransactionIdGenerator();
    String idSpace = getOMSTransactionIdSpace();

    if (IdGenerator == null) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Cannot generate OMSTransactionId. IdGenerator is null."));
      }

      return null;
    }

    //Generate an Id in the OMSTransaction IdSpace and return it
    try {
      String newId = IdGenerator.generateStringId(idSpace);

      if (isLoggingDebug()) {
        logDebug("OrderManager:getOMSTransactionId: Generated OMSTransactionId = " + newId);
      }

      return newId;
    } catch (IdGeneratorException ie) {
      if (isLoggingError()) {
        logError(LogUtils.formatMajor("Error in generating new OMSTransactionId."), ie);
      }

      return null;
    }
  }

  /**
   * Calls OrderManager.createOrder to create a new Order object using
   * the class mapped to the given name in pOrderType and whose id 
   * will be that which is supplied in pOrderId. Populates the Order with the
   * supplied data.
   * <p>
   * Sets shipping method to the shipping groups the same as profile's 
   * default shipping method.
   * 
   * @param pProfileId the id of the Profile object which this Order belongs to
   * @param pOrderId the id which will be assigned to the Order
   * @param pOrderPriceInfo the OrderPriceInfo object for this Order
   * @param pTaxPriceInfo the TaxPriceInfo object for this Order
   * @param pShippingPriceInfo the ShippingPriceInfo object for the default ShippingGroup
   * @param pOrderType the name that is mapped in the OrderTools.properties file to the class of
   *                   the desired type to create
   * @return the Order object which was created
   */
  public Order createOrder(String pProfileId, String pOrderId, OrderPriceInfo pOrderPriceInfo,
      TaxPriceInfo pTaxPriceInfo, ShippingPriceInfo pShippingPriceInfo,
      String pOrderType) throws CommerceException {

    Order order = super.createOrder(pProfileId, pOrderId, pOrderPriceInfo, pTaxPriceInfo,
        pShippingPriceInfo, pOrderType);
    
    try {
      RepositoryItem profile = getOrderTools().getProfileTools().getProfileForOrder(order);
        
      if(profile != null) {
        CommercePropertyManager propManager = 
          (CommercePropertyManager) getOrderTools().getProfileTools().getPropertyManager();
        
        String defaultShippingMethod = 
          (String) profile.getPropertyValue(propManager.getDefaultShippingMethodPropertyName());
        
        List shipGroups = order.getShippingGroups();
        
        if(shipGroups != null) {
          for(Object shipGroup : shipGroups) {
            if(shipGroup instanceof HardgoodShippingGroup) {
              ((HardgoodShippingGroup)shipGroup).setShippingMethod(defaultShippingMethod);
            }
          }
        }
      }
      
    } catch (RepositoryException e) {
      logError(LogUtils.formatMajor("Cannot retrieve user for order " + order.getId()), e);
    }

    return order;
  }
  
  
}
