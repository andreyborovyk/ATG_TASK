/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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
 * </ATGCOPYRIGHT>
 */
package atg.commerce.fulfillment.processor;

import atg.commerce.fulfillment.HardgoodFulfiller;
import atg.nucleus.logging.*;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.commerce.CommerceException;
import atg.commerce.fulfillment.Constants;
import atg.commerce.fulfillment.PipelineConstants;
import atg.commerce.fulfillment.OrderFulfillmentTools;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.Order;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.ConfigurableCommerceItem;
import atg.commerce.order.SubSkuCommerceItem;
import atg.commerce.states.*;
import atg.commerce.inventory.*;

import java.text.*;
import java.util.*;

/**
 * Handles allocating the items from inventory for a configurable commerce item.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcPurchaseConfigurableItem.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcPurchaseConfigurableItem extends ApplicationLoggingImpl
      implements PipelineProcessor
{
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcPurchaseConfigurableItem.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  private final int FAILURE = 2;
  
  //-----------------------------------------------
  public ProcPurchaseConfigurableItem() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = { SUCCESS, FAILURE };
    return ret;
  } 

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcPurchaseConfigurableItem";

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }


  //-----------------------------------------------
  /**
   * Send the ModifyOrderNotification message to the system.
   *
   * This method requires that an Order and OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and OrderFulfiller object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    ShippingGroupCommerceItemRelationship sgcir = 
        (ShippingGroupCommerceItemRelationship) map.get(PipelineConstants.SHIPPINGGROUPCOMMERCEITEMREL);
    Long quantity = (Long) map.get(PipelineConstants.SGCIRQUANTITY);
    List modificationList = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    boolean ret = allocateItemQuantityForConfigurableItem(of, sgcir, 
                                                          quantity.longValue(), 
                                                          modificationList);
    if (!ret)
        pResult.addError("ShipItemRelationship", "The ShipItemRel didn't allocate: " + sgcir.getState());
    
    return SUCCESS;
  }

  //-------------------------------------
  /**
   * Allocates the given configurable item and sub items for the given quantity from the InventoryManager.
   * Updates the state of the items accordingly. If the item cannot be purchased, backordered
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
   **/
    protected boolean allocateItemQuantityForConfigurableItem(HardgoodFulfiller of,
                                                              ShippingGroupCommerceItemRelationship sgcir,
                                                              long pQuantity,
                                                              List pModificationList) 
        throws CommerceException
  {
    InventoryManager inventory = of.getInventoryManager();
    if (inventory == null)
      throw new CommerceException(Constants.NO_INVENTORY_MANAGER);

    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    CommerceItemStates cis = of.getCommerceItemStates();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ConfigurableCommerceItem configItem = (ConfigurableCommerceItem) sgcir.getCommerceItem();
    SubSkuCommerceItem subItem = null;
    CommerceItem item;
    boolean successfulAllocation = false;
    boolean globalAllocation = true;
    List itemList;
    Iterator iter;
    int allocation, availability, currentState;

    if (isLoggingDebug())
      logDebug("Inside allocateItemQuantityFromInventoryForConfigurableItem for item " + configItem.getId());

    itemList = new ArrayList(configItem.getCommerceItemCount() + 1);
    iter = configItem.getCommerceItems().iterator();
    while (iter.hasNext())
      itemList.add(iter.next());
    // make sure that the ConfigurableCommerceItem is last in the list, otherwise none of this
    // method will work.
    itemList.add(configItem);      
    if (isLoggingDebug())
      logDebug("itemList: size: " + itemList.size() + " elements: " + itemList);
    
    iter = itemList.iterator();
    for (int i = 0; iter.hasNext(); i++) {
      item = (CommerceItem) iter.next();
      successfulAllocation = false;    
    
      if (item instanceof SubSkuCommerceItem)
        subItem = (SubSkuCommerceItem) item;
      else if (item instanceof ConfigurableCommerceItem)
        subItem = null; // just use configItem

      if (subItem == null) {
        currentState = sgcir.getState();
        if (isLoggingDebug())
          logDebug("Trying to allocate item " + item.getId() + " [state: " + sgcir.getStateAsString() + "]");
        if (currentState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_SUBITEM_DELIVERY)) {
          if (globalAllocation == true)
            tools.setItemRelationshipState(sgcir, 
                                           sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY), null, pModificationList);
          continue;
        }
      }
      else {
        currentState = subItem.getState();
        if (isLoggingDebug())
          logDebug("Trying to allocate item " + item.getId() + " [state: " + subItem.getStateAsString() + "]");
        if (currentState == cis.getStateValue(CommerceItemStates.SUBITEM_PENDING_DELIVERY))
          continue;
      }

      try {          
        availability = inventory.queryAvailabilityStatus(item.getCatalogRefId());
        if (isLoggingDebug())
          logDebug("Availability for item " + item.getId() + " is " + availability);

        if (currentState == sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED)) {
          if (subItem == null)
	          allocation = inventory.purchaseOffBackorder(configItem.getCatalogRefId(), pQuantity);
	        else
	          allocation = inventory.purchaseOffBackorder(subItem.getCatalogRefId(), subItem.getQuantity());

          switch(allocation) {
            case InventoryManager.INVENTORY_STATUS_FAIL :
            case InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY :
            {
              if(availability == InventoryManager.AVAILABILITY_STATUS_BACKORDERABLE) {
                // this item is already backordered... no need to continue
                successfulAllocation = false;
              }
              else {
                // this won't be backorderable anymore... reset the inventory
                if (subItem == null)
                  inventory.increaseBackorderLevel(configItem.getCatalogRefId(), pQuantity);
                else
                  inventory.increaseBackorderLevel(subItem.getCatalogRefId(), pQuantity * subItem.getIndividualQuantity());
              }
              break;
            }
          } // switch
        }
        else if (currentState == sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED)) {
          if (subItem == null)
  	        allocation = inventory.purchaseOffPreorder(configItem.getCatalogRefId(), pQuantity);
	        else
  	        allocation = inventory.purchaseOffPreorder(subItem.getCatalogRefId(), subItem.getQuantity());

          switch(allocation) {
            case InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY :
            case InventoryManager.INVENTORY_STATUS_FAIL :
            {
              if(availability == InventoryManager.AVAILABILITY_STATUS_PREORDERABLE) {
                // this item is already preordered... no need to continue
                successfulAllocation = false;
              }
              else {
                // this won't be preorderable anymore, reset the inventory
                if (subItem == null)
                  inventory.increasePreorderLevel(configItem.getCatalogRefId(), pQuantity);
                else
                  inventory.increasePreorderLevel(subItem.getCatalogRefId(), pQuantity * subItem.getIndividualQuantity());
              }
              break;
            }
          } // switch
        }
        else {
          if (subItem == null)
            allocation = inventory.purchase(configItem.getCatalogRefId(), pQuantity);
          else
            allocation = inventory.purchase(subItem.getCatalogRefId(), subItem.getQuantity());
        }

        if (isLoggingDebug())
          logDebug("Allocation for item " + item.getId() + " is " + allocation);

        // is there stock available
        switch(allocation) {
          case InventoryManager.INVENTORY_STATUS_SUCCEED :
          {
            successfulAllocation = true;
            if (subItem == null) {
              if (globalAllocation == true)
                tools.setItemRelationshipState(sgcir, 
                                               sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY), null, pModificationList);
              else
                tools.setItemRelationshipState(sgcir,
                                               sirs.getStateValue(ShipItemRelationshipStates.PENDING_SUBITEM_DELIVERY), null, pModificationList);
            }
            else
              tools.setSubSkuItemState(subItem, sgcir.getShippingGroup(),
                  cis.getStateValue(CommerceItemStates.SUBITEM_PENDING_DELIVERY), null, pModificationList);
            break;
          }
          case InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND : 
          {
            successfulAllocation = false;
            String msg = MessageFormat.format(Constants.ITEM_NOT_FOUND, item.getCatalogRefId());
            if(isLoggingError())
              logError(msg);
            if (subItem == null)
              tools.setItemRelationshipState(sgcir, 
                                             sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND), msg, pModificationList);
            else 
              tools.setSubSkuItemState(subItem, sgcir.getShippingGroup(),
                                       cis.getStateValue(CommerceItemStates.ITEM_NOT_FOUND), msg, pModificationList);
            break;
          }
          case InventoryManager.INVENTORY_STATUS_FAIL :
          case InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY :
          {
            successfulAllocation = false;
            queryAvailability(of, sgcir, pQuantity,
                              pModificationList, item.getCatalogRefId(), configItem, subItem, availability);
          } // else
        } //switch
      } catch (MissingInventoryItemException ii) {
        successfulAllocation = false;
        if(isLoggingError())
          logError(ii.getMessage());
        if (subItem == null)
          tools.setItemRelationshipState(sgcir, 
              sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND), ii.toString(), pModificationList);
        else
          tools.setItemRelationshipState(sgcir, 
              cis.getStateValue(CommerceItemStates.ITEM_NOT_FOUND), ii.toString(), pModificationList);

      } catch(InventoryException ii) {
        successfulAllocation = false;
        if(isLoggingError())
          logError(ii);
        if (subItem == null)
          tools.setItemRelationshipState(sgcir, 
              sirs.getStateValue(ShipItemRelationshipStates.FAILED), ii.toString(), pModificationList);
        else
          tools.setItemRelationshipState(sgcir, 
              cis.getStateValue(CommerceItemStates.FAILED), ii.toString(), pModificationList);
      }

      globalAllocation = (globalAllocation && successfulAllocation);

      if (isLoggingDebug()) {
        logDebug("successfulAllocation flag for item " + item.getId() + " is " + successfulAllocation);
        logDebug("globalfulAllocation flag for item " + item.getId() + " is " + globalAllocation);
      }
    } // for
      
    if (isLoggingDebug())
      logDebug("returning value for globalfulAllocation flag: " + globalAllocation);

    return globalAllocation;
  }

  protected void queryAvailability(HardgoodFulfiller of,
                                   ShippingGroupCommerceItemRelationship pShippingGroupItemRelationship,
                                   long pQuantity,
                                   List pModificationList,
                                   String sku,
                                   ConfigurableCommerceItem configItem,
                                   SubSkuCommerceItem subItem,
                                   int availability)
      throws InventoryException
  {
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    CommerceItemStates cis = of.getCommerceItemStates();

    switch(availability) {
      case InventoryManager.AVAILABILITY_STATUS_OUT_OF_STOCK:
      {
        if(isLoggingDebug())
          logDebug("Item " + sku + " is OUT OF STOCK.");
                      
        if (subItem == null)
          tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK),
              MessageFormat.format(Constants.ITEM_OUT_OF_STOCK, sku), pModificationList);
        else
          tools.setSubSkuItemState(subItem, pShippingGroupItemRelationship.getShippingGroup(), cis.getStateValue(CommerceItemStates.OUT_OF_STOCK),
              MessageFormat.format(Constants.ITEM_OUT_OF_STOCK, sku), pModificationList);
        break;
      }
      case InventoryManager.AVAILABILITY_STATUS_DISCONTINUED:
      {
        if(isLoggingDebug())
          logDebug("Item " + sku + " is DISCONTINUED.");

        if (subItem == null)
          tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.DISCONTINUED), 
              MessageFormat.format(Constants.ITEM_DISCONTINUED, sku), pModificationList);
        else
          tools.setSubSkuItemState(subItem, pShippingGroupItemRelationship.getShippingGroup(), cis.getStateValue(CommerceItemStates.DISCONTINUED),
              MessageFormat.format(Constants.ITEM_DISCONTINUED, sku), pModificationList);
        break;
      }
      case InventoryManager.AVAILABILITY_STATUS_PREORDERABLE:
      {              
        allocatePreOrderableItem(of, pShippingGroupItemRelationship, pQuantity,
                                  pModificationList, sku, configItem, subItem);
        break;
      } //case
      case InventoryManager.AVAILABILITY_STATUS_IN_STOCK: 
      // we tried to allocate more than were available
      case InventoryManager.AVAILABILITY_STATUS_BACKORDERABLE:
      {
        allocateBackOrderableItem(of, pShippingGroupItemRelationship, pQuantity,
                                  pModificationList, sku, configItem, subItem);
        break;
      }
    } // switch
  }
  
  protected void allocateBackOrderableItem(HardgoodFulfiller of,
                                           ShippingGroupCommerceItemRelationship pShippingGroupItemRelationship,
                                           long pQuantity,
                                           List pModificationList,
                                           String sku,
                                           ConfigurableCommerceItem configItem,
                                           SubSkuCommerceItem subItem)
      throws InventoryException
  {
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    CommerceItemStates cis = of.getCommerceItemStates();
    InventoryManager inventory = of.getInventoryManager();

    if(isLoggingDebug())
      logDebug("Item " + sku + " can be BACKORDERED, but is not available.");

    // back order the item
    int backorderSuccess = inventory.backorder(sku, pQuantity);
                        
    if(isLoggingDebug())
      logDebug("backorderSuccess is " + backorderSuccess);

    switch (backorderSuccess) {
      case InventoryManager.INVENTORY_STATUS_SUCCEED:
      {
        if (subItem == null)
          tools.setItemRelationshipState(pShippingGroupItemRelationship,
              sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED), null, pModificationList);
        else
          tools.setSubSkuItemState(subItem, pShippingGroupItemRelationship.getShippingGroup(),
              cis.getStateValue(CommerceItemStates.BACK_ORDERED), null, pModificationList);
        break;
      }
      case InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND:
      {
        if (subItem == null)
          tools.setItemRelationshipState(pShippingGroupItemRelationship,
              sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND), Constants.ITEM_BACKORDER_FAILED, pModificationList);
        else
          tools.setSubSkuItemState(subItem, pShippingGroupItemRelationship.getShippingGroup(),
              cis.getStateValue(CommerceItemStates.ITEM_NOT_FOUND), Constants.ITEM_BACKORDER_FAILED, pModificationList);
        break;
      }
      case InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY:
      {
        if (subItem == null)
          tools.setItemRelationshipState(pShippingGroupItemRelationship,
              sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK), Constants.ITEM_BACKORDER_FAILED, pModificationList);
        else
          tools.setSubSkuItemState(subItem, pShippingGroupItemRelationship.getShippingGroup(),
              cis.getStateValue(CommerceItemStates.OUT_OF_STOCK), Constants.ITEM_BACKORDER_FAILED, pModificationList);
        break;
      }
      default:
      {
        if (subItem == null)
          tools.setItemRelationshipState(pShippingGroupItemRelationship,
              sirs.getStateValue(ShipItemRelationshipStates.FAILED), Constants.ITEM_BACKORDER_FAILED, pModificationList);                
        else
          tools.setSubSkuItemState(subItem, pShippingGroupItemRelationship.getShippingGroup(),
              cis.getStateValue(CommerceItemStates.FAILED), Constants.ITEM_BACKORDER_FAILED, pModificationList);
        break;
      }
    }// switch
  }
  
  protected void allocatePreOrderableItem(HardgoodFulfiller of,
                                          ShippingGroupCommerceItemRelationship pShippingGroupItemRelationship,
                                          long pQuantity,
                                          List pModificationList,
                                          String sku,
                                          ConfigurableCommerceItem configItem,
                                          SubSkuCommerceItem subItem)
      throws InventoryException
  {
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    CommerceItemStates cis = of.getCommerceItemStates();
    InventoryManager inventory = of.getInventoryManager();

    if(isLoggingDebug())
      logDebug("Item " + sku + " can be PREORDERED, but is not available.");

    // preorder the item
    int preorderSuccess = inventory.preorder(sku, pQuantity);
    switch(preorderSuccess) {
      case InventoryManager.INVENTORY_STATUS_SUCCEED:
      {
        if (subItem == null)
          tools.setItemRelationshipState(pShippingGroupItemRelationship,
              sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED), null, pModificationList);
        else
          tools.setSubSkuItemState(subItem, pShippingGroupItemRelationship.getShippingGroup(),
              cis.getStateValue(CommerceItemStates.PRE_ORDERED), null, pModificationList);
        break;
      } 
      case InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND:
      {
        if (subItem == null)
          tools.setItemRelationshipState(pShippingGroupItemRelationship,
              sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND), Constants.ITEM_PREORDER_FAILED, pModificationList);
        else
          tools.setSubSkuItemState(subItem, pShippingGroupItemRelationship.getShippingGroup(),
              cis.getStateValue(CommerceItemStates.ITEM_NOT_FOUND), Constants.ITEM_PREORDER_FAILED, pModificationList);
        break;
      }
      case InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY:
      {
        if (subItem == null)
          tools.setItemRelationshipState(pShippingGroupItemRelationship,
              sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK), Constants.ITEM_PREORDER_FAILED, pModificationList);
        else
          tools.setSubSkuItemState(subItem, pShippingGroupItemRelationship.getShippingGroup(),
              cis.getStateValue(CommerceItemStates.OUT_OF_STOCK), Constants.ITEM_PREORDER_FAILED, pModificationList);
        break;
      }
      default:
      {
        if (subItem == null)
          tools.setItemRelationshipState(pShippingGroupItemRelationship,
              sirs.getStateValue(ShipItemRelationshipStates.FAILED), Constants.ITEM_PREORDER_FAILED, pModificationList);
        else
          tools.setSubSkuItemState(subItem, pShippingGroupItemRelationship.getShippingGroup(),
              cis.getStateValue(CommerceItemStates.FAILED), Constants.ITEM_PREORDER_FAILED, pModificationList);
        break;
      }
    } // switch
  }
}
