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
 </ATGCOPYRIGHT>*/

package atg.commerce.fulfillment.processor;

import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.commerce.fulfillment.*;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.inventory.MissingInventoryItemException;
import atg.commerce.inventory.InventoryException;

import java.util.*;
import java.text.*;

/**
 * This processor allocates in stock item
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcPurchaseItem.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcPurchaseItem implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcPurchaseItem.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcPurchaseItem() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = { SUCCESS };
    return ret;
  } 

  //-----------------------------------------------
  /**
   * Allocate an in stock item
   *
   * This method requires that an OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an OrderFulfiller object
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
    ShippingGroupCommerceItemRelationship pShippingGroupItemRelationship = 
        (ShippingGroupCommerceItemRelationship) map.get(PipelineConstants.SHIPPINGGROUPCOMMERCEITEMREL);
    Long quantity = (Long) map.get(PipelineConstants.SGCIRQUANTITY);
    List pModificationList = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    ShippingGroupStates sgs = of.getShippingGroupStates();
    CommerceItem item = pShippingGroupItemRelationship.getCommerceItem();
    InventoryManager inventory = of.getInventoryManager();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    boolean successfulAllocation = false;

    // check the inventory
    String sku = item.getCatalogRefId();
    long pQuantity = quantity.longValue();

    try {
      int availability = inventory.queryAvailabilityStatus(sku);
      int allocation = inventory.purchase(sku, pQuantity);
      
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
          if(of.isLoggingError())
              of.logError(msg);
          tools.setItemRelationshipState(pShippingGroupItemRelationship, 
                                         sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND), msg, pModificationList);
          break;
        }
      case InventoryManager.INVENTORY_STATUS_FAIL :
      case InventoryManager.INVENTORY_STATUS_INSUFFICIENT_SUPPLY :
        {
          
          successfulAllocation = false;
          
          switch(availability) 
          {
          case InventoryManager.AVAILABILITY_STATUS_OUT_OF_STOCK:
            {
              if(of.isLoggingDebug())
                  of.logDebug("Item " + sku + " is OUT OF STOCK.");
              tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.OUT_OF_STOCK), 
                                             MessageFormat.format(Constants.ITEM_OUT_OF_STOCK, sku),
                                             pModificationList);
              break;
            }
          case InventoryManager.AVAILABILITY_STATUS_DISCONTINUED:
            {
              if(of.isLoggingDebug())
                  of.logDebug("Item " + sku + " is DISCONTINUED.");
              tools.setItemRelationshipState(pShippingGroupItemRelationship, sirs.getStateValue(ShipItemRelationshipStates.DISCONTINUED), 
                                             MessageFormat.format(Constants.ITEM_DISCONTINUED, sku),
                                             pModificationList);
              break;
            }
          case InventoryManager.AVAILABILITY_STATUS_PREORDERABLE:
            {              
              if(of.isLoggingDebug())
                  of.logDebug("Item " + sku + " can be PREORDERED, but is not available.");
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
              if(of.isLoggingDebug())
                  of.logDebug("Item " + sku + " can be BACKORDERED, but is not available.");
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
      if(of.isLoggingError())
        of.logError(i.getMessage());

//        System.out.println("Tareef:  Setting the shipping group state to: " + sgs.getStateValue(sgs.PENDING_MERCHANT_ACTION));
      
//        tools.setShippingGroupState(pShippingGroupItemRelationship.getShippingGroup(),
//  				  sgs.getStateValue(sgs.PENDING_MERCHANT_ACTION),
//  				  null,
//  				  pModificationList);
      pResult.addError(ShipItemRelationshipStates.ITEM_NOT_FOUND, i.toString());
      tools.setItemRelationshipState(pShippingGroupItemRelationship, 
                                     sirs.getStateValue(ShipItemRelationshipStates.ITEM_NOT_FOUND), 
                                     i.toString(), pModificationList);
      return STOP_CHAIN_EXECUTION;
    } catch(InventoryException i) {
      if(of.isLoggingError())
        of.logError(i);
      
//        tools.setShippingGroupState(pShippingGroupItemRelationship.getShippingGroup(),
//  				  sgs.getStateValue(sgs.PENDING_MERCHANT_ACTION),
//  				  i.toString(),
//  				  pModificationList);

      pResult.addError(ShipItemRelationshipStates.FAILED, i.toString());
      
      tools.setItemRelationshipState(pShippingGroupItemRelationship, 
                                     sirs.getStateValue(ShipItemRelationshipStates.FAILED), 
                                     i.toString(), pModificationList);
      return STOP_CHAIN_EXECUTION;
    }
    
    if (!successfulAllocation)
        pResult.addError("ShipItemRelationship", "The ShipItemRel didn't allocate: " +
                         pShippingGroupItemRelationship.getState());

    return SUCCESS;
  }
}
