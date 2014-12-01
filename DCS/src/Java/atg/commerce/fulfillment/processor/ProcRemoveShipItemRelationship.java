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
import atg.commerce.*;
import atg.commerce.fulfillment.*;
import atg.commerce.messaging.*;
import atg.commerce.order.Order;
import atg.commerce.order.RelationshipTypes;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.commerce.inventory.InventoryManager;
import atg.commerce.inventory.InventoryException;

import java.util.*;
import java.text.*;

/**
 * This processor removes the item relationship from the shipping group by 
 * reallocating its quantity in the inventory.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRemoveShipItemRelationship.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcRemoveShipItemRelationship implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcRemoveShipItemRelationship.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcRemoveShipItemRelationship() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  } 

  //-----------------------------------------------
  /**
   * This processor removes the item relationship from the shipping group by 
   * reallocating its quantity in the inventory. 
   *
   * This method requires that a JMS message and HardgoodFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   * 
   * If the item in the relationship is "PENDING_DELIVERY" then the stock level
   * of the item is increased using <code>InventoryManager.increaseStockLevel</code>
   * and the sku Id is added to the pipeline parameter <code>PipelineConstants.INVENTORYUPDATES</code>
   * 
   * If the item in the relationship is "BACK_ORDERED" then the backorder level
   * of the item is increased using <code>InventoryManager.increaseBackorderLevel</code>
   *
   * If the item in the relationship is "PRE_ORDERED" then the preorder level
   * of the item is increased using <code>InventoryManager.increasePreorderLevel</code>
   *
   * @param pParam a HashMap which must contain a JMS message and OrderFulfiller object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.inventory.InventoryManager
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    ShippingGroupCommerceItemRelationship sgcir =
        (ShippingGroupCommerceItemRelationship) map.get(PipelineConstants.SHIPPINGGROUPCOMMERCEITEMREL);
    Modification pModification = (Modification) map.get(PipelineConstants.MODIFICATION);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);
    Set inventoryUpdates = (Set) map.get(PipelineConstants.INVENTORYUPDATES);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));

    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    String sku = sgcir.getCommerceItem().getCatalogRefId();
    long quantity = 0;
    boolean success = true;
    
    if(sgcir.getRelationshipType() == RelationshipTypes.SHIPPINGQUANTITY) {
        quantity = sgcir.getQuantity();
    }
    else {
        quantity = 
            of.getOrderManager().getRemainingQuantityForShippingGroup(sgcir.getCommerceItem());
    }

    int inventoryReturnValue = InventoryManager.INVENTORY_STATUS_SUCCEED;
    try {
        if(sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY)) {
            // update the inventory
            inventoryReturnValue = of.getInventoryManager().increaseStockLevel(sku, quantity);
            if(inventoryUpdates == null) {
              inventoryUpdates = new HashSet();
              map.put(PipelineConstants.INVENTORYUPDATES, inventoryUpdates);
            }
            inventoryUpdates.add(sku); 
        }
        else if(sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED)) {
            // update the inventory
            inventoryReturnValue = of.getInventoryManager().increaseBackorderLevel(sku, quantity); 
        }
        else if(sgcir.getState() == sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED)) {
            // update the inventory
            inventoryReturnValue = of.getInventoryManager().increasePreorderLevel(sku, quantity); 
        }
    } catch(InventoryException e) {
        if (of.isLoggingError())
            of.logError(e); 
        tools.modificationFailed(pModification, performedModifications, e.toString());
        throw e;
    }

    if( inventoryReturnValue == InventoryManager.INVENTORY_STATUS_SUCCEED) {
        // do nothing
    }
    else if ( inventoryReturnValue == InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND) {
        String errMsg = MessageFormat.format(Constants.INVENTORY_ITEM_NOT_FOUND, sku);
        if (of.isLoggingError())
            of.logError(errMsg);
    }
    else if ( inventoryReturnValue == InventoryManager.INVENTORY_STATUS_FAIL) {
        String errMsg = MessageFormat.format(Constants.INVENTORY_STATUS_FAILED, sku, Long.toString(quantity));
        if (of.isLoggingError())
            of.logError(errMsg);
    }
    String msg = Constants.ITEM_RELATIONSHIP_STATE_REMOVED;
    tools.setItemRelationshipState(sgcir,
                                   sirs.getStateValue(ShipItemRelationshipStates.REMOVED),
                                   MessageFormat.format(msg, sgcir.getCommerceItem().getId(), sgcir.getShippingGroup().getId()),
                                   performedModifications);
    
    return SUCCESS;
  }
}
