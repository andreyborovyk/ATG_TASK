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

package atg.commerce.fulfillment;

// Commerce classes
import atg.commerce.*;
import atg.commerce.order.*;
import atg.commerce.inventory.*;
import atg.commerce.states.*;
import atg.commerce.messaging.*;
// Dynamo classes
import atg.dms.patchbay.*;
import atg.nucleus.*;
import atg.dtm.*;
import atg.repository.*;
import atg.service.lockmanager.DeadlockException;
import atg.service.pipeline.*;

// Java classes
import javax.jms.*;
import java.text.MessageFormat;
import javax.transaction.*;
import javax.transaction.xa.*;
import java.util.*;

import java.io.Serializable;

/**
 * <p> This class is responsible for fulfilling order fragments.  It
 * receives FulfillOrderFragment messages and allocates all the items
 * within each shipping group contained in the message using the
 * InventoryManager.  It sends ModifyOrderNotification messages
 * reflecting all the changes that are made during this process.  This
 * class may be extended to customize the fulfillment process.
 *
 * <p> For each item in each shipping group, the HardgoodFulfiller will
 * use the InventoryManager and either purchase the item, backorder
 * it, preordered it, or mark it as out of stock.  When the item's
 * stock is increased and an UpdateInventory message is sent, the
 * HardgoodFulfiller will try again to successfully purchase the item.
 *
 * <p> It receives the messages FulfillOrderFragment, ModifyOrder, 
 * ModifyOrderNotification, and UpdateInventory </p>
 *
 * <p> It sends ModifyOrderNotification messages. </p>
 *
 * @author Tareef Kawaf
 * @beaninfo
 *          description: The HardgoodFulfiller is responsible for fulfilling 
 *                       order fragments.  For each order fragment it receives,
 *                       it attempts to purchase the items from inventory.
 *          displayname: HardgoodFulfiller
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/HardgoodFulfiller.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see FulfillOrderFragment
 * @see ModifyOrder
 * @see ModifyOrderNotification
 * @see UpdateInventory
 * @see atg.commerce.inventory.InventoryManager
 **/
public class HardgoodFulfiller extends FulfillerSystem
{
  //-------------------------------------
  /** Class version string */
  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/HardgoodFulfiller.java#2 $$Change: 651448 $";

  public static final String DEFAULT_MODIFY_ORDER_NOTIFICATION_PORT = "ModifyNotificationPort";

  public static final String DEFAULT_SHIP_ITEM_REL_ORDER_PROPERTY_NAME = "order";
  public static final String DEFAULT_SHIP_ITEM_REL_SHIPPING_GROUP_PROPERTY_NAME = "shippingGroup";
  public static final String DEFUALT_SHIP_ITEM_REL_COMMERCE_ITEM_PROPERTY_NAME = "commerceItem";
  public static final String DEFUALT_SHIP_ITEM_REL_STATE_PROPERTY_NAME = "state";
  
  public static final String DEFAULT_SHIPPING_GROUP_SUBMITTED_DATE_PROPERTY_NAME = "submittedDate";
  public static final String DEFAULT_COMMERCE_ITEM_CATALOG_REF_ID_PROPERTY_NAME = "catalogRefId";

  public static final String DEFAULT_CATALOG_REF_DISPLAY_NAME = "displayName";

  public static final String DEFAULT_SHIP_ITEM_REL_VIEW_NAME = "shipItemRel";

  //---------------------------------------------------------------------------
  // property:OutOfStockIsError
  //---------------------------------------------------------------------------

  private boolean mOutOfStockIsError = false;
  public void setOutOfStockIsError(boolean pOutOfStockIsError) {
    mOutOfStockIsError = pOutOfStockIsError;
  }

  /**
   * If this is true, an OUT_OF_STOCK item is considered an error
   * resulting in the ShippingGroup being set to
   * PENDING_MERCHANT_ACTION. If this is false, OUT_OF_STOCK is
   * treated similarly to BACK_ORDERED or PRE_ORDERED.  By default, 
   * this property is false.
   *
   * @beaninfo 
   *           description: If OutOfStockIsError is true, an OUT_OF_STOCK 
   *                        item is considered an error resulting in the 
   *                        ShippingGroup being set to PENDING_MERCHANT_ACTION. 
   *                        If this is false, OUT_OF_STOCK is treated 
   *                        similarly to BACK_ORDERED or PRE_ORDERED.
   **/
  public boolean isOutOfStockIsError() {
    return mOutOfStockIsError;
  }

  //---------------------------------------------------------------------------
  // property:AllowPartialShipmentDefault
  //---------------------------------------------------------------------------

  private boolean mAllowPartialShipmentDefault;

  public void setAllowPartialShipmentDefault(boolean pAllowPartialShipmentDefault) {
    mAllowPartialShipmentDefault = pAllowPartialShipmentDefault;
  }

  /**
   * This property is consulted when the shipping group's special
   * instructions doesn't have a field indicating whether a
   * shipping group should be split on partial shipment.
   *
   * @beaninfo
   *          description: This specifies the default behavior when 
   *                       a shipping group is only partially allocated.
   *                       If this is true, the shipping group will be split
   *                       to ship anything that is allocated.  The shipping
   *                       group's specialInstructions overrides this value.
   **/
  public boolean isAllowPartialShipmentDefault() {
    return mAllowPartialShipmentDefault;
  }
  
  //---------------------------------------------------------------------------
  // property: PartialShipPropertyName
  //---------------------------------------------------------------------------

  String mPartialShipPropertyName;

  public void setPartialShipPropertyName(String pPartialShipPropertyName) {
    mPartialShipPropertyName = pPartialShipPropertyName;
  }

  /**
   * The profile's attribute name which represents a Boolean object containing the information
   * on whether the profile allows for partial shipments or not.
   *
   * @beaninfo
   *          description: This is the name of the attribute in the shipping group's
   *                       special instructions that specifies the behavior on partially
   *                       allocated shipping groups.
   **/
  public String getPartialShipPropertyName() {
    return mPartialShipPropertyName;
  }

  //---------------------------------------------------------------------------
  // property: ModificationHandler
  //---------------------------------------------------------------------------
  private ModificationHandler mModificationHandler;

  public void setModificationHandler(ModificationHandler pModificationHandler) {
    mModificationHandler = pModificationHandler;
  }

  /**
   * The ModificationHandler handles all processing associated
   * with ModifyOrderNotification and ModifyOrder messages that 
   * are received by HardgoodFulfiller.
   *
   * @beaninfo
   *          description: The ModificationHandler processes all ModifyOrder and 
   *                       ModifyOrderNotification messages.
   * @see HardgoodFulfillerModificationHandler
   * @see ModifyOrderNotificiation
   * @see ModifyOrder
   **/
  public ModificationHandler getModificationHandler() {
    return mModificationHandler;
  }

  //------------------------------------
  // property: InventoryManager
  //------------------------------------
  private InventoryManager mInventoryManager;

  public void setInventoryManager(InventoryManager pInventoryManager) {
    mInventoryManager = pInventoryManager;
  }

  /**
   * The InventoryManager is used to allocate items for shipment.
   * It is the interface that tells the fulfillment system if an item
   * is available or not.
   *
   * @beaninfo
   *          description: The inventory manager is used to allocate items 
   *                       that are in an order.
   * @see atg.commerce.inventory.InventoryManager
   **/
  public InventoryManager getInventoryManager() {
    return mInventoryManager;
  }

  
  //---------------------------------------------------------------------------
  // property:HardgoodShipper
  //---------------------------------------------------------------------------

  private HardgoodShipper mHardgoodShipper;
  public void setHardgoodShipper(HardgoodShipper pHardgoodShipper) {
    mHardgoodShipper = pHardgoodShipper;
  }

  /**
   * The hardgood shipper is a schedulable service that simulates the periodic
   * shipment of shipping groups.
   *
   * @beaninfo
   *          description: The hardgood shipper is a schedulable service that 
   *                       simulates the periodic shipment of shipping groups.
   * @see HardgoodShipper
   **/
  public HardgoodShipper getHardgoodShipper() {
    return mHardgoodShipper;
  }
  

  String mModifyOrderNotificationPort = DEFAULT_MODIFY_ORDER_NOTIFICATION_PORT;

  //---------------------------------------------------------------------------
  // property:FulfillerName
  //---------------------------------------------------------------------------
  private String mFulfillerName;

  public void setFulfillerName(String pFulfillerName) {
    mFulfillerName = pFulfillerName;
  }

  /**
   * This is the name of this fulfiller.  It should match the name
   * used when defining the fulfiller to fulfiller port mapping.
   *
   * @beaninfo
   *          description: This is the name of this fulfiller.  This is used when
   *                       determining if this fulfiller is responsible for any 
   *                       given shipping group
   **/
  public String getFulfillerName() {
    return mFulfillerName;
  }


  //---------------------------------------------------------------------------
  // property:ShipItemRelOrderPropertyName
  //---------------------------------------------------------------------------

  private String mShipItemRelOrderPropertyName = DEFAULT_SHIP_ITEM_REL_ORDER_PROPERTY_NAME;
  public void setShipItemRelOrderPropertyName(String pShipItemRelOrderPropertyName) {
    mShipItemRelOrderPropertyName = pShipItemRelOrderPropertyName;
  }

  /**
   * The name of the order property in a
   * ShippingGroupCommerceItemRelationship
   **/
  public String getShipItemRelOrderPropertyName() {
    return mShipItemRelOrderPropertyName;
  }

  
  //---------------------------------------------------------------------------
  // property:ShipItemRelShippingGroupPropertyName
  //---------------------------------------------------------------------------
  
  private String mShipItemRelShippingGroupPropertyName = DEFAULT_SHIP_ITEM_REL_SHIPPING_GROUP_PROPERTY_NAME;
  public void setShipItemRelShippingGroupPropertyName(String pShipItemRelShippingGroupPropertyName) {
    mShipItemRelShippingGroupPropertyName = pShipItemRelShippingGroupPropertyName;
  }

  /**
   * The name of the shipping group property in a
   * ShippingGroupCommerceItemRelationship
   **/
  public String getShipItemRelShippingGroupPropertyName() {
    return mShipItemRelShippingGroupPropertyName;
  }

    //---------------------------------------------------------------------------
  // property:ShipItemRelCommerceItemPropertyName
  //---------------------------------------------------------------------------

  private String mShipItemRelCommerceItemPropertyName = DEFUALT_SHIP_ITEM_REL_COMMERCE_ITEM_PROPERTY_NAME;
  public void setShipItemRelCommerceItemPropertyName(String pShipItemRelCommerceItemPropertyName) {
    mShipItemRelCommerceItemPropertyName = pShipItemRelCommerceItemPropertyName;
  }

  /**
   * The name of the commerce item property name in a ShippingGroupCommerceItemRelationship
   **/
  public String getShipItemRelCommerceItemPropertyName() {
    return mShipItemRelCommerceItemPropertyName;
  }
    
  //---------------------------------------------------------------------------
  // property:ShipItemRelStatePropertyName
  //---------------------------------------------------------------------------

  private String mShipItemRelStatePropertyName = DEFUALT_SHIP_ITEM_REL_STATE_PROPERTY_NAME;
  public void setShipItemRelStatePropertyName(String pShipItemRelStatePropertyName) {
    mShipItemRelStatePropertyName = pShipItemRelStatePropertyName;
  }

  /**
   * The name of the state property in a ShippingGroupCommerceItemRelationship
   **/
  public String getShipItemRelStatePropertyName() {
    return mShipItemRelStatePropertyName;
  }
    
  //---------------------------------------------------------------------------
  // property:ShippingGroupSubmittedDatePropertyName
  //---------------------------------------------------------------------------

  private String mShippingGroupSubmittedDatePropertyName = DEFAULT_SHIPPING_GROUP_SUBMITTED_DATE_PROPERTY_NAME;
  public void setShippingGroupSubmittedDatePropertyName(String pShippingGroupSubmittedDatePropertyName) {
    mShippingGroupSubmittedDatePropertyName = pShippingGroupSubmittedDatePropertyName;
  }

  /**
   * The name of the submitted date property in the shipping group
   **/
  public String getShippingGroupSubmittedDatePropertyName() {
    return mShippingGroupSubmittedDatePropertyName;
  }

  //---------------------------------------------------------------------------
  // property:CommerceItemCatalogRefIdPropertyName
  //---------------------------------------------------------------------------

  private String mCommerceItemCatalogRefIdPropertyName = DEFAULT_COMMERCE_ITEM_CATALOG_REF_ID_PROPERTY_NAME;
  public void setCommerceItemCatalogRefIdPropertyName(String pCommerceItemCatalogRefIdPropertyName) {
    mCommerceItemCatalogRefIdPropertyName = pCommerceItemCatalogRefIdPropertyName;
  }

  /**
   * The name of the catalog ref id property in a CommerceItem
   **/
  public String getCommerceItemCatalogRefIdPropertyName() {
    return mCommerceItemCatalogRefIdPropertyName;
  }


  //---------------------------------------------------------------------------
  // property:CatalogRefDisplayNamePropertyName
  //---------------------------------------------------------------------------

  private String mCatalogRefDisplayNamePropertyName = DEFAULT_CATALOG_REF_DISPLAY_NAME;
  public void setCatalogRefDisplayNamePropertyName(String pCatalogRefDisplayNamePropertyName) {
    mCatalogRefDisplayNamePropertyName = pCatalogRefDisplayNamePropertyName;
  }

  /**
   * The name of the property of the catalog ref (sku) to get the
   * items display name
   **/
  public String getCatalogRefDisplayNamePropertyName() {
    return mCatalogRefDisplayNamePropertyName;
  }

  
  //---------------------------------------------------------------------------
  // property:ShipItemRelViewName
  //---------------------------------------------------------------------------

  private String mShipItemRelViewName = DEFAULT_SHIP_ITEM_REL_VIEW_NAME;
  public void setShipItemRelViewName(String pShipItemRelViewName) {
    mShipItemRelViewName = pShipItemRelViewName;
  }

  /**
   * The name of the view in the order repository for ShipItemRels.
   **/
  public String getShipItemRelViewName() {
    return mShipItemRelViewName;
  }
  

  /**
   * The map of the chains to run to execute pipeline
   **/
  private Properties mChainToRunMap;
  
  public Properties getChainToRunMap() {
      return mChainToRunMap;
  }

  public void setChainToRunMap(Properties pChainToRunMap) {
      mChainToRunMap = pChainToRunMap;
  }


  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------

  //-------------------------------------
  /**
   * <p> This is called to handle a newly received message. Before this method is called,
   * the message is subjected to basic validity checks, a transaction is established, and
   * an exclusive lock is acquired for the message's key. </p>
   *
   * <p> HardgoodFulfiller handles the following types of messages:
   * <ul>
   *   <li>FulfillOrderFragment - handleFulfillOrderFragment - This is 
   *                              sent to the hardgood fulfiller with a
   *                              list of all the shipping groups that this 
   *                              fulfiller is responsible for.  Each shipping
   *                              group's items are allocated using the 
   *                              inventory manager </li>
   *   <li>ModifyOrder - handleModifyOrder - This is sent to the hardgood 
   *                     fulfiller if someone requests a change to an object 
   *                     that is "owned" by this fulfiller, such as a shipping 
   *                     group that was already sent within a FulfillOrderFragment.</li>
   *   <li>ModifyOrderNotification - This is sent to notify this fulfiller that some
   *                                 object has changed.<li>
   *   <li>UpdateInventory - This is sent to notify this fulfiller that one or more
   *                         items in the inventory have been put in stock.  This fulfiller
   *                         is then responsible for finding the shipping groups that 
   *                         contain these items and are "owned" by the fulfiller and 
   *                         reprocessing them</li>
   *   <li>If an unknown message type comes in, handleNewMessageType is called.</li>
   * </ul>
   *
   * <p> HardgoodFulfiller can be extended to handle extra types of messages by overriding
   * the handleNewMessageType method. </p>
   *
   * @beaninfo
   *          description: This method is called when a new message has arrived
   *                       for the HardgoodFulfiller.
   * @param pPortName - the port on which this message was received
   * @param pMessage - the message that was sent on the port
   * @exception javax.jms.JMSException
   * @see FulfillOrderFragment
   * @see #handleFulfillOrderFragment
   * @see ModifyOrder
   * @see HardgoodFulfillerModificationHandler#handleModifyOrder
   * @see ModifyOrderNotification
   * @see HardgoodFulfillerModificationHandler#handleModifyOrderNotification
   * @see UpdateInventory
   * @see #handleUpdateInventory
   * @see FulfillerSystem#receive
   * @see FulfillerSystem#handleNewMessageType
   **/
  protected boolean handleMessage (String pPortName, ObjectMessage pMessage)
    throws JMSException
  {
    String messageType = pMessage.getJMSType();
    boolean handled = true;

    if (messageType.equals(FulfillOrderFragment.TYPE)) {
      handleFulfillOrderFragment(pPortName, pMessage);
    }
    else if (messageType.equals(ModifyOrder.TYPE)) {
      getModificationHandler().handleModifyOrder(pPortName, pMessage);
    }
    else if (messageType.equals(ModifyOrderNotification.TYPE)) {
      getModificationHandler().handleModifyOrderNotification(pPortName, pMessage);
    }
    else if (messageType.equals(UpdateInventory.TYPE)) {
      handleUpdateInventory(pPortName, pMessage);
    }
    else { 
      handled = false;
    }
    return handled;
  }


  //-------------------------------------
  /**
   * This method will return the key to be used for locking out other messages with the same
   * key while a thread is handling this message.
   *
   * @beaninfo
   *          description: This method will return the key to be used for locking 
   *                       out other messages with the same key while a thread 
   *                       is handling this message.
   * @param pMessage the ObjectMessage containing the CommerceMessage.
   * @return an Object which serves as the key for the message
   * @exception javax.jms.JMSException
   **/
  public Serializable getKeyForMessage(ObjectMessage oMessage)
    throws JMSException
  {

    String messageType = oMessage.getJMSType();
    CommerceMessage pMessage = (CommerceMessage) oMessage.getObject();
    
    if (messageType.equals(FulfillOrderFragment.TYPE)) {
        return getOrderIdFromMessage((FulfillOrderFragment)pMessage);
    }
    else if (messageType.equals(ModifyOrder.TYPE)) {
      return ((ModifyOrder) pMessage).getOrderId();
    }
    else if (messageType.equals(ModifyOrderNotification.TYPE)) {
      return ((ModifyOrderNotification) pMessage).getOrderId();
    }
    else if (messageType.equals(UpdateInventory.TYPE)) {
      return "inventoryupdate";
    }
    return null;
  }


  /**
   * This is called only for the FulfillOrderFragment messages
   * @return an Object which serves as the key for the message
   **/
  protected Serializable getOrderIdFromMessage(FulfillOrderFragment cMessage) {
      // see if we get it from the message itself
      if (getLookUpOrderIdFromMessage()) { 
          if(isLoggingDebug())
              logDebug("Reading the orderId from the FulfillOrderFragment message");
          String id = cMessage.getOrderId();
          if (id == null) {
              if (getLookUpOrderIdFromOrder()) {
                  if(isLoggingDebug())
                      logDebug("Reading the orderId from the order object sent in the " + 
                               "FulfillOrderFragment message");
                  if (cMessage.getOrder() == null) {
                      if (isLoggingError())
                          logError(Constants.ORDER_IS_NULL);
                      return null;
                  }
                  return cMessage.getOrder().getId();
              }
          }
          return id;
      }
      else { // get it from the order object sent in the message
          if (getLookUpOrderIdFromOrder()) {
              if(isLoggingDebug())
                  logDebug("Reading the orderId from the order object sent in the " + 
                           "FulfillOrderFragment message");
              if (cMessage.getOrder() == null) {
                  if (isLoggingError())
                      logError(Constants.ORDER_IS_NULL);
                  return null;
              }
              return cMessage.getOrder().getId();
          }
          else
              return null;
      }
  }

  //-------------------------------------
  /**
   * <p> This method is called to handle all messages of type
   * FulfillOrderFragment.  Should developers wish to change the
   * behavior of the HardgoodFulfiller class in handling a
   * FulfillOrderFragment message this method should be overriden. It 
   * will call to the pipeline chain to process the message.
   * The chain first loads the order with loadOrder, then calls
   * processMultipleShippingGroups for each order and its shipping
   * groups in the message.  When processing has finished, it calls
   * updateOrder and sends all the changes that were made using
   * <code>OrderFulfillmentTools.sendModifyOrderNotification()</code>
   *
   * @beaninfo
   *          description: This method is called to process a newly arrived FulfillOrderFragment message.
   * @param pPortName The port from which the FulfillOrderFragment was received.
   * @param pMessage The message that was received.
   * @exception javax.jms.JMSException
   * @see FulfillOrderFragment
   * @see #updateOrder
   * @see #processMultipleShippingGroups
   * @see OrderFulfillmentTools#sendModifyOrderNotification
   **/
  public void handleFulfillOrderFragment(String pPortName,
					 ObjectMessage pMessage) throws JMSException
  {
      if (isLoggingDebug())
          logDebug("Handling a fulfillOrderFragment message");

      // the input params to the chain
      HashMap map = new HashMap(10);
      map.put(PipelineConstants.MESSAGE, pMessage);
      map.put(PipelineConstants.ORDERFULFILLER, this);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("fulfillOrderFragmentChain");
          // execute the submitOrder pipeline chain
          PipelineResult results = getFulfillmentPipelineManager().runProcess(chainToRun, map);
      }
      catch (RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);
          // check the thrown exceptions
          if (p instanceof CommerceException) {
              try {
                  getTransactionManager().getTransaction().setRollbackOnly();
              }
              catch (javax.transaction.SystemException se) {
                  // Hopefully this will never happen.
                  if(isLoggingError())
                      logError(se);
              }
          }
          if (p instanceof JMSException) {
              throw (JMSException)p;
          }
      }

  }
  
  /**
   * <p> This method is called to handle all messages of type
   * UpdateInventory.  Should developers wish to change the behavior
   * of the HardgoodFulfiller class in handling an UpdateInventory
   * message the method should be overridden.  This message gets all
   * the orders waiting on the items contained in the message using
   * retrieveBackorderedShipMap and retrievePreorderedShipMap then
   * loads each order (loadOrder) and calls processShippingGroup for
   * each shipping group "owned" by this fulfiller.  Once finished it
   * saves the changes (updateOrder) and send its modifications with
   * <code>OrderFulfillmentTools.sendModifyOrderNotification</code></p>
   *
   * @beaninfo
   *          description: This method is called to process all newly arrived UpdateInventory message.
   * @param pPortName The port from which this message was received.
   * @param pMessage The message that was received.
   * @exception javax.jms.JMSException
   * @see UpdateInventory
   * @see #retrieveWaitingShipMap
   * @see #loadOrder
   * @see #processShippingGroup
   * @see OrderFulfillmentTools.#sendModifyOrderNotification
   **/
  public void handleUpdateInventory(String pPortName,
				    ObjectMessage pMessage) throws JMSException
  {
      if (isLoggingDebug())
          logDebug("Handling an updateInventory message");

      // the input params to the chain
      HashMap map = new HashMap(10);
      map.put(PipelineConstants.MESSAGE, pMessage);
      map.put(PipelineConstants.ORDERFULFILLER, this);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("updateInventoryChain");
          // execute the submitOrder pipeline chain
          PipelineResult results = getFulfillmentPipelineManager().runProcess(chainToRun, map);
      }
      catch (RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);
          // check the thrown exceptions
          if (p instanceof CommerceException || p instanceof DeadlockException ) {
              try {
                  getTransactionManager().getTransaction().setRollbackOnly();
              }
              catch (javax.transaction.SystemException se) {
                  // Hopefully this will never happen.
                  if(isLoggingError())
                      logError(se);
              }
          }
          if (p instanceof JMSException) {
              throw (JMSException)p;
          }
      }
  }

  /**
   * This method processes the shipping group by first verifying that
   * it may be changed.  (verifyShippingGroupFulfillment) then
   * allocating all of its items (allocateShippingGroup).  If
   * everything is successfully allocated then it prepares the group
   * for shipping (queueForShipping).  If only some of the items are
   * available it checks if it should shipAsItemsAreAvailable and if
   * so, splits the group into shippable and unshippable items
   * (splitShippingGroupWithAvailableItems) and ships the shippable
   * group.
   *
   * @beaninfo
   *          description: Process the given shipping group. This includes verifying
   *                       that it may be processed then allocating each of its items.
   * @param pOrder The order containing the shipping group we are processing
   * @param pShippingGroup The shipping group we will try to allocate and ship
   * @param pModificationList List holding any modifications we might make
   * @return true if successful, false if there is an error.  If the shipping is in a state
   *         that doesn't allow it to be changed, an info is logged, but true is returned since 
   *         this isn't an error
   * @see #verifyShippingGroupFulfillment
   * @see #allocateShippingGroup
   * @see #queueForShipping
   * @see #shipAsItemsAreAvailable
   * @see #splitShippingGroupWithAvailableItems
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void processShippingGroup(Order pOrder, ShippingGroup pShippingGroup, List pModificationList) 
    throws CommerceException
  {
    if(isLoggingDebug())
      logDebug("Inside processShippingGroup");
    
    ShippingGroupStates sgs = getShippingGroupStates();
    ShipItemRelationshipStates sirs = getShipItemRelationshipStates();

    if(pShippingGroup.getCommerceItemRelationshipCount() == 0) {
      if(isLoggingDebug())
        logDebug("This shipping group [" + pShippingGroup.getId() + "] is empty.");
      return;
    }
    
    try {
      if(!getOrderFulfillmentTools().getFulfillerForShippingGroup(pShippingGroup).equals(getFulfillerName())) {
        // this shipping group is owned by another fulfiller
        // return true since this isn't an error.
        if(isLoggingDebug())
          logDebug("This shipping group: " + pShippingGroup.getId() + " is owned by another fulfiller.");
        return;
      }        

      verifyShippingGroupFulfillment(pShippingGroup);

      if(allocateShippingGroup(pOrder, pShippingGroup, pModificationList)) {
        if(isLoggingDebug())
         logDebug("Shipping group " + pShippingGroup.getId() + " " + 
                  sgs.getStateString(sgs.getStateValue(ShippingGroupStates.PENDING_SHIPMENT)));
        // if allocateShippingGroup returns true, then this group is shippable
        getOrderFulfillmentTools().setShippingGroupState(pShippingGroup,
                                                         sgs.getStateValue(ShippingGroupStates.PENDING_SHIPMENT),
                                                         null, pModificationList);
      } else { // some or all of the items were not allocated in inventory

        if(shipAsItemsAreAvailable(pShippingGroup)) {
          List newShippingGroups = splitShippingGroupWithAvailableItems(pOrder, pShippingGroup, pModificationList);
              
          if(newShippingGroups.size() > 0) {
            for(int groupNum=0;groupNum<newShippingGroups.size();groupNum++) {
              pShippingGroup = (ShippingGroup) newShippingGroups.get(groupNum);

              // We should be able to get buy with only checking one
              // relationship since splitShippingGroupWithAvailableItems
              // only splits if some items are PENDING_DELIVERY and
              // always splits all PENDING_DELIVERY items into a
              // separate group with no items that are not
              // PENDING_DELIVERY
              ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) 
                pShippingGroup.getCommerceItemRelationships().get(0);
              if(sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY)) {
                getOrderFulfillmentTools().setShippingGroupState(pShippingGroup,
                                                                 sgs.getStateValue(ShippingGroupStates.PENDING_SHIPMENT),
                                                                 null, pModificationList);

                break; // since we know the other group has no shippable items
              } // else this group is not shippable

            } //for
          } else {
            if(isLoggingDebug())
                logDebug("Split shipping groups returned an empty list... no new groups created.");
          }
        } //if
      } // else
    } //try
    catch (IllegalShippingGroupStateException e){
      if(isLoggingInfo())
        logInfo(e.toString());
      return;
   }
    catch (InvalidParameterException i){
      if(isLoggingError())
        logError(i);
      throw new CommerceException(i);
   }
    catch (ObjectCreationException o){
      if(isLoggingError())
        logError(o);
      throw new CommerceException(o);
   }
    catch (DuplicateShippingGroupException d){
      if(isLoggingError())
        logError(d);
      throw new CommerceException(d);
   }
    catch (CommerceException c){
      if(isLoggingError())
        logError(c);
      throw new CommerceException(c);
   }
  }

  /**
   * This method verifies that the shipping group to be fulfilled is in a state which allows it
   * to be fulfilled.  If the shipping group is in an illegal state we will throw an exception.
   * Illegal states include: 
   *
   * @beaninfo
   *          description: This method verifies that the given shipping group is in a state
   *                       which allows it to be fulfulled.
   * @param pShippingGroup The shipping group we are verifying
   * @exception IllegalShippingGroupStateException
   * @see atg.commerce.states.ShippingGroupStates#REMOVED, 
   * @see atg.commerce.states.ShippingGroupStates#FAILED, 
   * @see atg.commerce.states.ShippingGroupStates#NO_PENDING_ACTION, 
   * @see atg.commerce.states.ShippingGroupStates#PENDING_MERCHANT_ACTION
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void verifyShippingGroupFulfillment(ShippingGroup pShippingGroup)
    throws IllegalShippingGroupStateException
  {
    int state=pShippingGroup.getState();
    ShippingGroupStates sgs = getShippingGroupStates();

    if(state == sgs.getStateValue(ShippingGroupStates.PROCESSING)) {
      return;
    }
    else if(state == sgs.getStateValue(ShippingGroupStates.REMOVED))
    {	  
      throw new IllegalShippingGroupStateException(MessageFormat.format(Constants.SHIP_GROUP_STATE_REMOVED,
      		pShippingGroup.getId()));
    }
    else if(state == sgs.getStateValue(ShippingGroupStates.PENDING_REMOVE))
    {
      throw new IllegalShippingGroupStateException(MessageFormat.format(Constants.SHIP_GROUP_STATE_PENDING_REMOVE,
      		pShippingGroup.getId()));
    }
    else if(state == sgs.getStateValue(ShippingGroupStates.FAILED))
    {
      throw new IllegalShippingGroupStateException(MessageFormat.format(Constants.SHIP_GROUP_STATE_FAILED, 
      		pShippingGroup.getId()));
    }
    else if(state == sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION))
    {
      throw new IllegalShippingGroupStateException(MessageFormat.format(Constants.SHIP_GROUP_STATE_FAILED, 
      		pShippingGroup.getId()));
    }
    else if(state == sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION))
    {
      throw new IllegalShippingGroupStateException(MessageFormat.format(Constants.SHIP_GROUP_STATE_PENDING_MERCHANT_ACTION,
      		pShippingGroup.getId()));
    }
  }

  //------------------------------------ 
  /**
   * Check all the items in the given ShippingGroup.  If all items are PENDING_DELIVERY, 
   * then ship the group. Add the modifications to the modification list and return true.
   * If some of the items are not PENDING_DELIVERY, attempt to allocate those items, update the state 
   * of the item, add the modification to the list, and return false.
   *
   * @beaninfo
   *          description: This method cycles through all the items in a shipping group and
   *                       attempts to allocate each one.  If all items are allocated, the flag
   *                       the group for shipment.
   * @param pOrder The affect Order owning this ShippingGroup
   * @param pShippingGroup The ShippingGroup we are checking
   * @param pModificationList Place to store our changes.
   * @return True if shippable, false if not.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected boolean allocateShippingGroup(Order pOrder, ShippingGroup pShippingGroup, List pModificationList)
  {
    if(isLoggingDebug())
      logDebug("Inside allocateShippingGroup");
    
    // Get the list of item relationships from the shipping group
    List shippingGroupItemRelationships = pShippingGroup.getCommerceItemRelationships();
    Iterator shippingGroupItemRelIterator = shippingGroupItemRelationships.iterator();
    ShippingGroupCommerceItemRelationship sgcir = null;
    ShipItemRelationshipStates sirs = getShipItemRelationshipStates();
    ShippingGroupStates sgs = getShippingGroupStates();

    int oldState;
    boolean shippable = true; 
    boolean allocated = false;

    // make sure we have the necessary inventory locks
    try {
      List skuIds = getOrderManager().getUniqueCatalogRefIds(pOrder);
      getInventoryManager().acquireInventoryLocks(skuIds);
    }
    catch (InventoryException ie) {
      if(isLoggingError())
        logError(ie);
      getOrderFulfillmentTools().setShippingGroupState(pShippingGroup, sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION),
                                                       ie.getMessage(),
                                                       pModificationList);
      return false;
    }

    while (shippingGroupItemRelIterator.hasNext()) {
      // For each of these item relationships get the item.
      sgcir = (ShippingGroupCommerceItemRelationship) shippingGroupItemRelIterator.next();

      // Keep the old state and state detail information for the item
      oldState=sgcir.getState();

      if((oldState == sirs.getStateValue(ShipItemRelationshipStates.INITIAL)) ||
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.FAILED)) || // maybe someone corrected the problem
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND)) || // maybe someone added it
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK)) ||   // maybe someone change stockLevel
	 (oldState == sirs.getStateValue(ShipItemRelationshipStates.DISCONTINUED)) || // maybe it's made a comeback
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED)) ||
         (oldState == sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED)))
      {
        //attempt to allocate
        allocated = allocateShippingGroupItem(pOrder, sgcir, pModificationList);
        // for shippable to be true, allocated must never be false
        // if allocated is false then there exists at least one item
        // that was not allocated meaning the group is not shippable
        shippable = allocated & shippable;

        // if the item failed, reset the shipping group to PENDING_MERCHANT_ACTION
        // if the item is out of stock, it is only considered failed if the outOfStockIsError
        // property is true.
        if((sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.FAILED)) ||
           (sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND)) ||
	   (sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.DISCONTINUED)) ||
           ((sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK)) &&
            isOutOfStockIsError())) {
          int pendingMerchantActionState = sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION);
          CommerceItem item = sgcir.getCommerceItem();
          String id = null;
          String name = null;
          if(item != null) {
            id = item.getCatalogRefId();
            RepositoryItem catalogRef = (RepositoryItem) item.getAuxiliaryData().getCatalogRef();
            if(catalogRef != null)
              name = (String) catalogRef.getPropertyValue(getCatalogRefDisplayNamePropertyName());
          }
          if(pShippingGroup.getState() != pendingMerchantActionState) {
            String msg = Constants.ITEM_ALLOCATE_FAILED;
            getOrderFulfillmentTools().setShippingGroupState(pShippingGroup, pendingMerchantActionState,
                                                             MessageFormat.format(msg, name, id),
                                                             pModificationList);
          }
        }
      }
      else if(oldState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY))
      {
        // do nothing
      }
      else if((oldState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_REMOVE)) ||
              (oldState == sirs.getStateValue(ShipItemRelationshipStates.DELIVERED)))
      {
        if(isLoggingError())
          logError(MessageFormat.format(Constants.STATE_DOES_NOT_ALLOW_ALLOCATION, sgcir.getId()));
      }
    }

    return shippable;
  }

  //-------------------------------------
  /**
   * This method calculates the quantity to allocate from inventory and calls
   * allocateItemQuantityFromInventory
   *
   * @beaninfo
   *          description: Purchase the given item from inventory.
   * @param pOrder the order in which this shipping group is
   * @param pShipGroup the shipping group whose item states we are changing
   * @param pModificationList the list of the modifications made to the items
   * @return true if the item has been successfully allocated
   * @see #allocateItemQuantityFromInventory
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected boolean allocateShippingGroupItem(Order pOrder,
                                              ShippingGroupCommerceItemRelationship pShipItemRelationship,
                                              List pModificationList)
  {
    boolean success = false;
 
    if(isLoggingDebug())
      logDebug("Inside allocateShippingGroupItem");

    CommerceItem item=null;
    int relationshipType;
    long quantity=0;
 
    item = pShipItemRelationship.getCommerceItem();
 
    relationshipType = pShipItemRelationship.getRelationshipType();
 
    // find out how many items to allocate from inventory
    switch(relationshipType)
    {
    case RelationshipTypes.SHIPPINGQUANTITY:
      {
        
        quantity = pShipItemRelationship.getQuantity();
        break;
      }
    case RelationshipTypes.SHIPPINGQUANTITYREMAINING:
      {
        // calculate the quantity
        quantity = getOrderManager().getRemainingQuantityForShippingGroup(item);
        break;
      }
    } //switch

    try {
      success = allocateItemQuantityFromInventory(pOrder, pShipItemRelationship, quantity, pModificationList);
    } catch(CommerceException c) {
      if(isLoggingError())
        logError(c);
      try {
	getTransactionManager().getTransaction().setRollbackOnly();
      }
      catch (javax.transaction.SystemException se) {
	// Hopefully this will never happen.
	logError(se);
      }
      return false;
    }
    
    return success;
  }
  
  //-------------------------------------
  /**
   * Allocates the given item for the given quantity from the InventoryManager.  
   * Updates the state of the item accordingly.  If the item cannot be purchased, backordered
   * or preordered, the state of the item is FAILED, ITEM_NOT_FOUND, OUT_OF_STOCK, or DISCONTINUED.
   *
   * @beaninfo
   *          description: Purchase the given quantity of the given item from inventory.
   * @param pOrder The order containing this item.   
   * @param pShippingGroupItemRelationship The item to allocate
   * @param pQuantity The quantity of the item to allocate.  Does not verify that this quantity
   *                  and the quantity in the item match.
   * @param pModificationList Place to store any modifications that are made.
   * @return true if the item is allocated, false otherwise
   * @exception CommerceException
   * @see atg.commerce.inventory.InventoryManager
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected boolean allocateItemQuantityFromInventory(Order pOrder,
                                                   ShippingGroupCommerceItemRelationship pShippingGroupItemRelationship,
                                                   long pQuantity,
                                                   List pModificationList) 
    throws CommerceException
  {
    ShipItemRelationshipStates sirs = getShipItemRelationshipStates();
    CommerceItem item = pShippingGroupItemRelationship.getCommerceItem();
    InventoryManager inventory = getInventoryManager();
    OrderFulfillmentTools tools = getOrderFulfillmentTools();
    boolean successfulAllocation = false;

    if(inventory == null)
      throw new CommerceException(Constants.NO_INVENTORY_MANAGER);
    
    // check the inventory
    String sku = item.getCatalogRefId();

    int currentState = pShippingGroupItemRelationship.getState();

    try {
      int availability = inventory.queryAvailabilityStatus(sku);
      int allocation;
      if (currentState == sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED)) {
	allocation = inventory.purchaseOffBackorder(sku, pQuantity);
        switch(allocation) {
        case InventoryManager.INVENTORY_STATUS_FAIL :
        case InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY :
          {
            if(availability == InventoryManager.AVAILABILITY_STATUS_BACKORDERABLE) {
              // this item is already backordered... no need to continue
              return successfulAllocation;
            }
            else {
              // this won't be backorderable anymore... reset the inventory
              inventory.increaseBackorderLevel(sku, pQuantity);
            }
            break;
          }
        }
      }
      else if (currentState == sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED)) {
	allocation = inventory.purchaseOffPreorder(sku, pQuantity);
        switch(allocation) {
        case InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY :
        case InventoryManager.INVENTORY_STATUS_FAIL :
          {
            if(availability == InventoryManager.AVAILABILITY_STATUS_PREORDERABLE) {
              // this item is already preordered... no need to continue
              return successfulAllocation;
            }
            else {
              // this won't be preorderable anymore, reset the inventory
              inventory.increasePreorderLevel(sku, pQuantity);
            }
            break;
          }
        }
      }
      else 
	allocation = inventory.purchase(sku, pQuantity);
      
      // is there stock available
      switch(allocation) {
      case InventoryManager.INVENTORY_STATUS_SUCCEED :
        {
          // we can return true
          successfulAllocation = true;
          
          tools.setItemRelationshipState(pShippingGroupItemRelationship, 
                                         sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY), null, pModificationList);
          break;
        }
      case InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND : 
        {
          String msg = MessageFormat.format(Constants.ITEM_NOT_FOUND, sku);
          if(isLoggingError())
            logError(msg);
          tools.setItemRelationshipState(pShippingGroupItemRelationship, 
                                         sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND), msg, pModificationList);
          break;
        }
      case InventoryManager.INVENTORY_STATUS_FAIL :
      case InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY :
        {
          // we must return false
          successfulAllocation = false;
          
          switch(availability) 
          {
          case InventoryManager.AVAILABILITY_STATUS_OUT_OF_STOCK:
            {
              if(isLoggingDebug())
                logDebug("Item " + sku + " is OUT OF STOCK.");
              tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK), 
                                             MessageFormat.format(Constants.ITEM_OUT_OF_STOCK, sku),
                                             pModificationList);
              break;
            }
          case InventoryManager.AVAILABILITY_STATUS_DISCONTINUED:
            {
              if(isLoggingDebug())
                logDebug("Item " + sku + " is DISCONTINUED.");
              tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.DISCONTINUED), 
                                             MessageFormat.format(Constants.ITEM_DISCONTINUED, sku),
                                             pModificationList);
              break;
            }
          case InventoryManager.AVAILABILITY_STATUS_PREORDERABLE:
            {              
              if(isLoggingDebug())
                logDebug("Item " + sku + " can be PREORDERED, but is not available.");
              // preorder the item
              int preorderSuccess = inventory.preorder(sku, pQuantity);
              if(preorderSuccess == InventoryManager.INVENTORY_STATUS_SUCCEED) {
                tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED), 
                                               null, pModificationList);
              } 
              else if(preorderSuccess == InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND) {
                tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND), 
                                               Constants.ITEM_PREORDER_FAILED,
                                               pModificationList);
              }
              else if(preorderSuccess == InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
                tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK), 
                                               Constants.ITEM_PREORDER_FAILED,
                                               pModificationList);
              }
              else {
                tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.FAILED), 
                                               Constants.ITEM_PREORDER_FAILED,
                                               pModificationList);
              }

              break;
            }
          case InventoryManager.AVAILABILITY_STATUS_IN_STOCK: 
            // we tried to allocate more than were available
          case InventoryManager.AVAILABILITY_STATUS_BACKORDERABLE:
            {
              if(isLoggingDebug())
                logDebug("Item " + sku + " can be BACKORDERED, but is not available.");
              // back order the item
              int backorderSuccess = inventory.backorder(sku, pQuantity);
              if(backorderSuccess == InventoryManager.INVENTORY_STATUS_SUCCEED) {
                tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED),
                                               null, pModificationList);
              }
              else if(backorderSuccess == InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND) {
                tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND), 
                                               Constants.ITEM_BACKORDER_FAILED,
                                               pModificationList);
              }
              else if(backorderSuccess == InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY) {
                tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK), 
                                               Constants.ITEM_BACKORDER_FAILED,
                                               pModificationList);
              }
              else {
                tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.FAILED),
                                               Constants.ITEM_BACKORDER_FAILED,
                                               pModificationList);                
              }
              break;
            }
          default:
            {
            
            }
          } // switch
        } // else
      } //switch
    } catch(MissingInventoryItemException i) {
      if(isLoggingError())
        logError(i.getMessage());
      tools.setItemRelationshipState(pShippingGroupItemRelationship, 
                                     sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND), 
                                     i.toString(), pModificationList);
      return false;
    } catch(InventoryException i) {
      if(isLoggingError())
        logError(i);
      tools.setItemRelationshipState(pShippingGroupItemRelationship, 
                                     sirs.getStateValue(ShipItemRelationshipStates.FAILED), 
                                     i.toString(), pModificationList);
      return false;
    }
    
    return successfulAllocation;
  }
  
  /**
   * Split the shipping group into two shipping groups, one with items PENDING_DELIVERY
   * and one with items that aren't PENDING_DELIVERY
   *
   * @beaninfo
   *          desription: Split the given shipping group into two groups.  One whose items
   *                      are all allocated, and ones whose items are not.
   * @param pOrder The order containing the shippingGroup
   * @param pShippingGroup The shipping group to split
   * @param pModificationList A place to store all new modifications
   * @return A list of the newly created shipping groups.
   * @exception atg.commerce.order.InvalidParameterException
   * @exception atg.commerce.ObjectCreationException
   * @exception atg.commerce.order.DuplicateShippingGroupException
   * @exception atg.commerce.CommerceException
   **/
  private List splitShippingGroupWithAvailableItems(Order pOrder, ShippingGroup pShippingGroup,
                                                    List pModificationList)
    throws InvalidParameterException, ObjectCreationException, DuplicateShippingGroupException, CommerceException
  {
    ShipItemRelationshipStates sirs = getShipItemRelationshipStates();
    List itemIds = new ArrayList();
    List quantities = new ArrayList();
    List relationships = null;
    Iterator relationshipIter = null;
    ShippingGroupCommerceItemRelationship sgcir = null;
    int shippableItemCount = 0;
    List newShippingGroups = new ArrayList();
    List returnedGroups = null;
    
    if(isLoggingDebug())
      logDebug("Inside splitShippingGroupsWithAvailableItems");

    relationships = pShippingGroup.getCommerceItemRelationships();
    relationshipIter = relationships.iterator();

    while(relationshipIter.hasNext()) {
      sgcir = (ShippingGroupCommerceItemRelationship) relationshipIter.next();
      if(sgcir.getState() != sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY)) {
        itemIds.add(sgcir.getCommerceItem().getId());
        if(sgcir.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
          quantities.add(Long.valueOf(sgcir.getQuantity()));
        } else {
          quantities.add(Long.valueOf(0));
        } //else
      } else {
        shippableItemCount++;
      }// else
    } // while

    if(shippableItemCount > 0) {

      long[] quantityArray = new long[quantities.size()];
      String[] itemIdArray = null;

      for(int i=0;i<quantities.size();i++) {
        quantityArray[i] = ((Long) quantities.get(i)).longValue();
      }
      itemIdArray = (String []) itemIds.toArray(new String[itemIds.size()]);

      returnedGroups = getOrderManager().splitShippingGroup(pOrder, 
                                                            pShippingGroup.getId(), 
                                                            itemIdArray,
                                                            quantityArray);
      // create modifications
      Iterator newGroups = returnedGroups.iterator();
      while(newGroups.hasNext()) {
        ShippingGroup newSg = (ShippingGroup)newGroups.next();
        if(newSg == pShippingGroup) {
          //this is not the new group
        } else {
          // this is the new group
          GenericAdd ga = 
            getOrderFulfillmentTools().createGenericAddValueToIdModification(Modification.TARGET_SHIPPING_GROUP,
                                                                             newSg,
                                                                             Modification.TARGET_ORDER,
                                                                             pOrder.getId());
          pModificationList.add(ga);
          // add an update for each relationship in the group
          List rels = newSg.getCommerceItemRelationships();
          Iterator sgcirs = rels.iterator();
          ShippingGroupCommerceItemRelationship changedSgcir = null;
          while(sgcirs.hasNext()) {
            changedSgcir = (ShippingGroupCommerceItemRelationship)sgcirs.next();
            GenericUpdate gu = getOrderFulfillmentTools().createGenericUpdateModification(getShipItemRelShippingGroupPropertyName(),
                                                                                          pShippingGroup,
                                                                                          newSg,
                                                                                          changedSgcir.getId(),
                                                                                          Modification.TARGET_RELATIONSHIP,
                                                                                          true);
            pModificationList.add(gu);
          }
        }
      }

      newShippingGroups.addAll(returnedGroups);

    } else {
      // do nothing
    }

    return newShippingGroups;

  } // method

  /**
   * Check with the special instructions of the shipping group to see what the behavior should be if only
   * part of the group has been allocated.
   * 
   * @beaninfo
   *          description: Return true if the given shipping group should ship as much as possible.
   *                       False if all items must be allocated before anything ships.
   *@param pOrder The order we are interested in
   * @return true if groups should be split, false otherwise
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected boolean shipAsItemsAreAvailable(ShippingGroup sg)
  {
    if(isLoggingDebug())
      logDebug("Inside shipAsItemsAreAvailable");

    Map specialInstructions = null;
    boolean partialShipment = false;

    specialInstructions = sg.getSpecialInstructions();
 
    if(specialInstructions != null) {
      Object allowPartialShipment = specialInstructions.get(getPartialShipPropertyName());
      if(allowPartialShipment == null) {
        if(isLoggingDebug())
          logDebug("SpecialInstruction " + getPartialShipPropertyName() + " was not found for " + sg.getId());
        return isAllowPartialShipmentDefault();
      }
      else if(allowPartialShipment instanceof Boolean) {
        return ((Boolean)allowPartialShipment).booleanValue();
      }
      else if(allowPartialShipment instanceof String) {
        if(((String)allowPartialShipment).compareToIgnoreCase("true") == 0)
          partialShipment = true;
        else
          partialShipment = false;
        return partialShipment;
      }
      else {    
        if(isLoggingDebug())
          logDebug("SpecialInstruction " + getPartialShipPropertyName() + " was not a String or Boolean for " + sg.getId());
        return isAllowPartialShipmentDefault();
      }
    } else {
      if(isLoggingDebug())
        logDebug("SpecialInstructions was null for " + sg.getId());
      return isAllowPartialShipmentDefault();
    }
  }
  

  /**
   * This method can be called in three ways.
   * <ul><li>The HardgoodShipper ran.</li>
   *     <li>The ShippingDroplet was used.</li>
   *     <li>A ModifyOrder message was received with a GenericUpdate setting a 
   * shipping group's state to NO_PENDING_ACTION.</li>
   * </ul>
   * This version of the method will grab a lock on the order using the ClientLockManager.
   *
   * @beaninfo
   *          description: This method is called after a shipping group has shipped.
   *                       It will update the states of each object and send out the
   *                       appropriate message.
   * @param pOrder The order containing the shipping group
   * @param pShippingGroup The shipping group to ship
   * @return true if the group was successfully set to NO_PENDING_ACTION, false otherwise
   * @see HardgoodShipper
   * @see ShippingDroplet
   * @see ModifyOrder
   * @see GenericUpdate
   **/
  public boolean shippingGroupHasShipped(String pOrderId, String pShippingGroupId)
  {

    TransactionDemarcation td = new TransactionDemarcation();
    try {
      td.begin(getTransactionManager(), TransactionDemarcation.REQUIRED);

      HashMap pMap = new HashMap(4); // HashMap is optimized for powers of two, unlike the old Hashtable
      pMap.put(PipelineConstants.ORDERFULFILLER, this);
      pMap.put(PipelineConstants.ORDERID, pOrderId);
      pMap.put(PipelineConstants.SHIPPINGGROUPID, pShippingGroupId);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("shipShippingGroupChain");
          PipelineResult result = getFulfillmentPipelineManager().runProcess(chainToRun, pMap);
          if (result.hasErrors())
              return false;
      }
      catch(RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);  

          if (p instanceof DeadlockException) {
              return false;
          }
          if (p instanceof JMSException) {
              return false;
          } 
          if (p instanceof CommerceException) {
              return false;
          }
      }
    }
    catch(TransactionDemarcationException t) {
        if(isLoggingError())
            logError(t);
        return false;
    }
    finally {
        try {
            td.end();
        }
        catch(TransactionDemarcationException tde) {
            if(isLoggingError())
                logError(tde);
        }
    }
    
    return true;
  }

  /**
   * This method updates the states of all the items and the shipping 
   * group itself to reflect that the shipping group has shipped.  Each 
   * ShippingGroupCommerceItemRelationship gets set to DELIVERED, and the
   * shipping group gets set to NO_PENDING_ACTION.  It also calls
   * <code>setShippedDate</code> with the given shipping group.
   * If for some reason, the group could not be shipped, this method will return false.
   *
   * @beaninfo
   *          description: This method is called after a shipping group has shipped.
   *                       It will update the states of each object.
   *
   * @see #setShippedDate
   *
   * @param pOrder The order containing the shipping group
   * @param pShippingGroupId The id of the shipping group to ship
   * @param pModification The modification request this action is in response to
   * @param pPerformedModifications The list to store our performed modifications in
   * @return true if the group was successfully set to NO_PENDING_ACTION, false otherwise
   **/
  public boolean shippingGroupHasShipped(Order pOrder, String pShippingGroupId,
                                         Modification pModification, List pPerformedModifications)
  {

      HashMap pMap = new HashMap(6);
      pMap.put(PipelineConstants.ORDERFULFILLER, this);
      pMap.put(PipelineConstants.ORDER, pOrder);
      pMap.put(PipelineConstants.SHIPPINGGROUPID, pShippingGroupId);
      pMap.put(PipelineConstants.MODIFICATION, pModification);
      pMap.put(PipelineConstants.MODIFICATIONLIST, pPerformedModifications);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("shippingGroupHasShippedChain");
          PipelineResult result = getFulfillmentPipelineManager().runProcess(chainToRun, pMap);
          if (result.hasErrors())
              return false;
      }
      catch(RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);
          return false;
      }

      return true;
  }
  

  //-------------------------------------
  /**
   * This method will process each shipping group in the given array.
   *
   * @beaninfo
   *          description: This method will process each shipping group in the given array.
   * @param pOrder The order containing the shipping groups.
   * @param pShippingGroupIds The array of shipping group ids to process.
   * @param pPerformedModifications A place to store all new modifications.
   * @return true if everything processed fine, false otherwise.
   **/
  public boolean processMultipleShippingGroups(Order pOrder,
                                               String[] pShippingGroupIds,
                                               List pPerformedModifications)
      throws CommerceException
  {

      HashMap pMap = new HashMap(5);
      pMap.put(PipelineConstants.ORDERFULFILLER, this);
      pMap.put(PipelineConstants.ORDER, pOrder);
      pMap.put(PipelineConstants.SHIPPINGGROUPIDS, pShippingGroupIds);
      pMap.put(PipelineConstants.MODIFICATIONLIST, pPerformedModifications);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("processMutlipleShippingGroupsChain");
          PipelineResult result = getFulfillmentPipelineManager().runProcess(chainToRun, pMap);
      }
      catch(RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);
          
          if (p instanceof CommerceException)
              throw (CommerceException)p;

          return false;
      }

      return true;      

  }

  //-------------------------------------
  /**
   * This method sets the date when the shipping group shipped to the
   * given time.  The property that gets set is <code>actualShipDate</code>
   *
   * @beaninfo
   *          description: Set the date the given shipping group shipped to the given time.
   * @param pShippingGroup The shipping group that shipped.
   * @param pShipDate When the shipping group shipped.
   * @param pModificationList Place to store new modifications.
   **/
  public void setShippedDate(ShippingGroup pShippingGroup, Date pShipDate, List pModificationList)
  {
    java.util.Date oldTime = pShippingGroup.getActualShipDate();

    pShippingGroup.setActualShipDate(pShipDate);
    Modification m = getOrderFulfillmentTools().createShipUpdateModification("actualShipDate",
                                                                             oldTime, pShipDate,
                                                                             pShippingGroup.getId());

    pModificationList.add(m);
  }

  //-------------------------------------
  /**
   * Returns a HashMap where the keys are the order ids and the values are a Set of shipping
   * group ids of the shipping groups who have items that were on preorder and are contained
   * in the pCatalogRefIds that are passed in.
   *
   * @beaninfo
   *          description: Find all orders with preordered items.
   * @param pCatalogRefIds - the array of sku ids that have been moved off preorder
   * @return a hashmap where the keys are the order ids and the values are a Set of shipping
   * group ids.
   **/
  public HashMap retrieveWaitingShipMap(String[] pCatalogRefIds) {

      UpdateInventoryImpl message = new UpdateInventoryImpl();
      message.setItemIds(pCatalogRefIds);
      
      HashMap resultMap = null;
      HashMap pMap = new HashMap(2);
      pMap.put(PipelineConstants.ORDERFULFILLER, this);
      pMap.put(PipelineConstants.COMMERCEMESSAGE, message);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("retrieveWaitingShipMapChain");
          PipelineResult result = getFulfillmentPipelineManager().runProcess(chainToRun, pMap);
          resultMap = (HashMap)pMap.get(PipelineConstants.ORDERWAITINGSHIPMAP);
      }
      catch(RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);
      }

      return resultMap;
  }


  //-------------------------------------
  /**
   * This method will return a HashMap where the Order ids are the
   * keys and the values are a Set of shipping group ids whose
   * shipItemRel state is equal to one of the pState passed in and
   * contain the items listed in pCatalogRefIds.
   *
   * @beaninfo
   *          description: retrieve all orders with items in the given state.
   * @param pCatalogRefIds - the ids of the items we are interested in finding in the shipping
   * groups.
   * @param pState - the array of states of the item relationship that we want to match.
   * @return a hashmap where the keys are the order ids and the values are a Set of shipping group ids.
   **/
  private HashMap retrieveShipMap(String[] pCatalogRefIds,
				  String[] pStates)
  {
    
    Repository orderRepository = getOrderManager().getOrderTools().getOrderRepository();

    if ((pCatalogRefIds == null) ||
	(pCatalogRefIds.length == 0))
      return null;
    
    if((pStates == null) ||
       (pStates.length == 0))
      return null;

    try
    {
      RepositoryView shipRelationshipView = orderRepository.getView(getShipItemRelViewName());

      QueryBuilder qb = shipRelationshipView.getQueryBuilder();

      // create the constant query expression for backordered items
      QueryExpression stateProperty = qb.createPropertyQueryExpression(getShipItemRelStatePropertyName());
      Query[] qOredStateEquality = new Query[pStates.length];
      for(int i=0;i<pStates.length;i++) {
        QueryExpression state =
          qb.createConstantQueryExpression(pStates[i]);        
        
        qOredStateEquality[i] = qb.createComparisonQuery( stateProperty, state, QueryBuilder.EQUALS);
        
      }

      Query qStateEquality;
      if(pStates.length == 1)
        qStateEquality = qOredStateEquality[0];
      else
        qStateEquality = qb.createOrQuery(qOredStateEquality);

      Query qIncludesAny = qb.
	createIncludesQuery(qb.createConstantQueryExpression(pCatalogRefIds),
			    qb.createPropertyQueryExpression(qb.createPropertyQueryExpression(getShipItemRelCommerceItemPropertyName()), getCommerceItemCatalogRefIdPropertyName()));

      //      order by commerceItem.shippingGroup.submittedDate
      String orderbyProperty = getShipItemRelShippingGroupPropertyName() + "."
        + getShippingGroupSubmittedDatePropertyName();
      SortDirective sd = new SortDirective(orderbyProperty, SortDirective.DIR_ASCENDING);
      SortDirectives sds = new SortDirectives();
      sds.addDirective(sd);
      
      Query[] andedArray = new Query[2];
      andedArray[0] = qStateEquality;
      andedArray[1] = qIncludesAny;
      Query combinedQuery = qb.createAndQuery(andedArray);

      RepositoryItem[] items = shipRelationshipView.executeQuery(combinedQuery, sds);
      
//        System.out.println("______________________________\n\n");
//        if (items != null) 
//      	System.out.println("The query returned: " + items.length);
//        else
//      	System.out.println("The query didn't return anything ");
      
//        System.out.println("______________________________\n\n");
      
      if (items != null && items.length > 0)
	return makeOrderShipGroupHashMap(items);
    }
    catch (RepositoryException re) {
      if(isLoggingError())
        logError(re);
    }
    return null;
  }

  //-------------------------------------
  /**
   * Takes an array of repository items that correspond to shipItemRel repository items.  Will
   * return a HashMap where the keys are the order ids and the values are sets of the shipping
   * groups.
   **/
  private HashMap makeOrderShipGroupHashMap(RepositoryItem[] pItems)
  {
    HashMap returnMap = new HashMap(7);
    Set currentSet = null;
    RepositoryItem currentRelationship = null;
    RepositoryItem currentShipGroup = null;
    RepositoryItem currentOrder = null;

    String orderId = null;
    
    for (int i=0; i < pItems.length; i++) {
      currentRelationship = pItems[i];      
      currentShipGroup = (RepositoryItem) currentRelationship.getPropertyValue(getShipItemRelShippingGroupPropertyName()); 
      currentOrder = (RepositoryItem) currentRelationship.getPropertyValue(getShipItemRelOrderPropertyName());
      orderId = currentOrder.getRepositoryId();
      currentSet = (Set) returnMap.get(orderId);
      if (currentSet == null) {
	currentSet = new HashSet();
      }
      currentSet.add(currentShipGroup.getRepositoryId());
      returnMap.put(orderId, currentSet);
    }
    return returnMap;
  }
  
  /**
   * This method will return the Order from the order repository with the given id.  In the
   * standard implementation this will call the OrderManager and load the order.
   *
   * @beaninfo
   *          description: Load the order using the order manager.
   * @param pId the id of the order to be retrieved
   * @return the order corresponding to the id passed in
   * @exception CommerceException
   **/
  public Order loadOrder(String pId) 
    throws CommerceException 
  {
    Order order = null;
    try {
      order = getOrderManager().loadOrder(pId);
    } catch(InvalidParameterException i) {        
    }
    
    return order;
  }

  //-------------------------------------
  /**
   * This method will save the order passed in to the repository that is being used.
   *
   * @beaninfo
   *          description: Save the given order using the order manager.
   * @param pOrder - the order to be saved
   * @exception CommerceException
   **/
  public void updateOrder(Order pOrder) 
      throws CommerceException 
  {
      if (isLoggingDebug()) {
          logDebug("Inside updateOrder");
          getOrderFulfillmentTools().printOrder(pOrder);
      }
        
      getOrderManager().updateOrder(pOrder);
  } 
    
}
