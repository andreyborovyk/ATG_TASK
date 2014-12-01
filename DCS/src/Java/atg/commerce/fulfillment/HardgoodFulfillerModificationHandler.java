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

package atg.commerce.fulfillment;

// atg classes
import atg.commerce.*;
import atg.commerce.fulfillment.*;
import atg.commerce.order.*;
import atg.commerce.inventory.*;
import atg.commerce.states.*;
import atg.service.pipeline.*;

// java classes
import java.text.MessageFormat;
import java.util.*;
import javax.jms.*;

/**
 * <p>The HardgoodFulfillerModificationHandler deals with the
 * ModifyOrder and ModifyOrderNotifications messages received by the
 * HardgoodFulfiller. The HardgoodFulfiller contains a
 * ModificationHandler property, which is set by default to the
 * HardgoodFulfillerModificationHandler. This class is similar to the
 * OrderFulfillerModificationHandler.</p>
 *
 * <p>To change the handling behavior of ModifyOrder and ModifyOrderNotification
 * messages, extend the HardgoodFulfillerModificationHandler class and change
 * the ModificationHandler property HardgoodFulfiller to point to the new class.</p>
 *
 * <p>The default implementation deals with the following ModifyOrder modification:
 *        Remove the shipping group from the order:
 *                       The fulfillers can remove shipping groups if they have 
 *                       not been shipped. Determining whether a shipping group 
 *                       has been shipped can be difficult because of the asynchronous
 *                       nature of shipping items. Consulting the states may not be 
 *                       enough to determine if the group has been shipped. 
 *                       DCS 5.0 consults the state to make sure that it isn't in a 
 *                       NO_PENDING_ACTION or REMOVED state. This is sufficient because 
 *                       out-of-the box there is no integration with a real warehouse 
 *                       so shipment is indicated by changing a set of states in the order
 *                       repository. Some vendors might decide to create business rules 
 *                       that limit the time in which cancellations can occur because it 
 *                       is difficult to determine the exact shipping time for a shipping group. 
 *        Ship the shipping group.
 *                       The HardgoodFulfiller can be notified that a shipping group has 
 *                       shipped through a ModifyOrder message (which is originally sent 
 *                       to the OrderFulfiller, then forwarded to the HardgoodFulfiller).  
 *                       The HardgoodFulfiller gets a GenericUpdate modification through 
 *                       the ModifyOrder message, checks the current state of the shipping 
 *                       group to ensure that it is PENDING_SHIPMENT.  If everything is 
 *                       fine, it sets the state to NO_PENDING_ACTION and notifies the 
 *                       rest of the system of the change with a ModifyOrderNotification
 *                       message.
 * 
 * <p>It also handles the following ModifyOrderNotification modification:
 *        Shipping group update
 *                       A shipping group is re-processed when the method 
 *                       processMultipleShippingGroups in HardgoodFulfiller
 *                       is called. This method is called when a modification
 *                       of type SHIPPING_GROUP_UPDATE is received.
 *
 *
 *
 * Created: Mon Jan 24 14:36:40 2000
 *
 * @author Sam Perman
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/HardgoodFulfillerModificationHandler.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $*/

public class HardgoodFulfillerModificationHandler implements ModificationHandler {
  
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/HardgoodFulfillerModificationHandler.java#2 $$Change: 651448 $";


  //---------------------------------------------------------------------------
  // property:HardgoodFulfiller
  //---------------------------------------------------------------------------

  private HardgoodFulfiller mHardgoodFulfiller;

  public void setHardgoodFulfiller(HardgoodFulfiller pHardgoodFulfiller) {
    mHardgoodFulfiller = pHardgoodFulfiller;
  }

  /**
   * The fulfiller object that this class handles modifications for
   **/
  public HardgoodFulfiller getHardgoodFulfiller() {
    return mHardgoodFulfiller;
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


  //-------------------------------------
  /**
   * <p> This method is called to handle all messages of type
   * ModifyOrder.  Should developers wish to change the behavior of
   * the HardgoodFulfiller class on handling a ModifyOrder message
   * this method should be overridden. This method calls various
   * methods to respond to the ModifyOrder requests. Modifications on
   * orders are ignored since the HardgoodFulfiller does not have the
   * authority to edit orders.
   * <ul>
   *   <li>performShippingGroupModification - called if the request's 
   *       target type is Modification.TARGET_SHIPPING_GROUP</li>
   *   <li>performItemModification - called if the request's 
   *       target type is Modification.TARGET_ITEM</li>
   *   <li>performRelationshipModification - called if the request's 
   *       target type is Modification.TARGET_RELATIONSHIP</li>
   * </ul>
   *  
   * @param pPortName The port from which this message was received.
   * @param pMessage The message that was received.
   * @exception javax.jms.JMSException
   * @see ModifyOrder
   * @see #performShippingGroupModification
   * @see #performItemModification
   * @see #performRelationshipModification
   * @see IdTargetModification#getTargetType
   * @see Modification
   **/
  public void handleModifyOrder(String pPortName, ObjectMessage pMessage) throws JMSException
  {
      
      HardgoodFulfiller of = getHardgoodFulfiller();
      
      if (of.isLoggingDebug())
          of.logDebug("Handling a modifyOrder message in HardgoodFulfillerModificationHandler");

      // the input params to the chain
      HashMap map = new HashMap(10);
      map.put(PipelineConstants.MESSAGE, pMessage);
      map.put(PipelineConstants.ORDERFULFILLER, of);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("modifyOrderChain");
          // execute the submitOrder pipeline chain
          PipelineResult results = 
              of.getFulfillmentPipelineManager().runProcess(chainToRun, map);
      }
      catch (RunProcessException e) {
          Throwable p = e.getSourceException();
          if(of.isLoggingError())
              of.logError(p);
          // check the thrown exceptions
          if (p instanceof CommerceException) {
              try {
                  of.getTransactionManager().getTransaction().setRollbackOnly();
              }
              catch (javax.transaction.SystemException se) {
                  // Hopefully this will never happen.
                  if(of.isLoggingError())
                      of.logError(se);
              }
          }
          if (p instanceof JMSException) {
              throw (JMSException)p;
          }
      }
  }
  


  //-------------------------------------
  /**
   * <p> This method is called to handle all messages of type 
   * ModifyOrderNotification.  Should developers wish to 
   * change the behavior of the HardgoodFulfiller class on handling a
   * ModifyOrderNotification message this method should be
   * overridden. Currently only one modification type is handled by
   * this method. If it is an IdTargetModification then handleIdTargetModification
   * is called.
   * <ul>
   *   <li>handleShippingGroupUpdateModification - called if the notification is
   *       of type ShippingGroupUpdate
   * </ul>
   *
   * @param pPortName The port from which this message was received.
   * @param pMessage The message that was received.
   * @exception javax.jms.JMSException
   * @see ModifyOrderNotification
   * @see #handleShippingGroupUpdateModification
   * @see #handleIdTargetModification
   * @see ShippingGroupUpdate
   **/
  public void handleModifyOrderNotification(String pPortName, ObjectMessage pMessage) throws JMSException
  {

      HardgoodFulfiller of = getHardgoodFulfiller();
      
      if (of.isLoggingDebug())
          of.logDebug("Handling a modifyOrderNotification message in HardgoodFulfillerModificationHandler");
      
      // the input params to the chain
      HashMap map = new HashMap(10);
      map.put(PipelineConstants.MESSAGE, pMessage);
      map.put(PipelineConstants.ORDERFULFILLER, of);
      
      try {
          String chainToRun = (String) getChainToRunMap().get("modifyOrderNotificationChain");
          // execute the submitOrder pipeline chain
          PipelineResult results = 
              of.getFulfillmentPipelineManager().runProcess(chainToRun, map);
      }
      catch (RunProcessException e) {
          Throwable p = e.getSourceException();
          if(of.isLoggingError())
              of.logError(p);
          // check the thrown exceptions
          if (p instanceof CommerceException) {
              try {
                  of.getTransactionManager().getTransaction().setRollbackOnly();
              }
              catch (javax.transaction.SystemException se) {
                  // Hopefully this will never happen.
                  if(of.isLoggingError())
                      of.logError(se);
              }
          }
          if (p instanceof JMSException) {
              throw (JMSException)p;
          }
      }
  }


  //-------------------------------------
  /**
   * Perform the requested Modifications.  The only modification requests
   * that this method currently implements are a GenericRemove 
   * (ie. cancel this order) and a GenericUpdate to change the state from
   * PENDING_SHIPMENT to NO_PENDING_ACTION (i.e. ship the order).  In the
   * first case then removeShippingGroup is called.  In the second case
   * HardgoodFulfiller.shippingGroupHasShipped
   *
   * @param pMessage The original message that contained the modifications
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications All new modifications are stored here.
   * @see GenericRemove
   * @see GenericUpdate
   * @see #removeShippingGroup
   * @see HardgoodFulfiller#shippingGroupHasShipped
   **/
  private void performShippingGroupModification(ModifyOrder pMessage, Order pOrder,
                                                Modification pModification,
                                                List pPerformedModifications)
  {
    HardgoodFulfiller hf = getHardgoodFulfiller();
    OrderFulfillmentTools tools = hf.getOrderFulfillmentTools();
    Modification mod = null;
    ShippingGroup sg = null;
    ShippingGroupStates sgs = hf.getShippingGroupStates();

    switch(pModification.getModificationType()) {
    case Modification.ADD :
      {
        tools.modificationFailed(pModification, pPerformedModifications, 
                                 Constants.MODIFICATION_NOT_SUPPORTED);
        break;
      }
    case Modification.REMOVE :
      {
        /*
         * Remove this shipping group from the order.
         */
        GenericRemove genRemoveMod = (GenericRemove) pModification;
        
        if(pMessage.getOrderId() != genRemoveMod.getContainerId()) {
	  String errorMsg = MessageFormat.format(Constants.CONTAINER_ID_MISMATCH, pMessage.getId());
	  
	  if (hf.isLoggingError())
	    hf.logError(errorMsg);
	  
          tools.modificationFailed(genRemoveMod, pPerformedModifications, errorMsg);
          break;
        }        
        
        try {
          sg = pOrder.getShippingGroup(genRemoveMod.getTargetId());
        } catch(ShippingGroupNotFoundException s) {
	  String errMessage = MessageFormat.format(Constants.TARGET_SHIP_GROUP_NOT_FOUND, pMessage.getId());

	  if (hf.isLoggingError())
	    hf.logError(errMessage);
          tools.modificationFailed(genRemoveMod, pPerformedModifications, errMessage);
          break;
        }
	catch(InvalidParameterException i) {
          if (hf.isLoggingError())
	    hf.logError(i);

          tools.modificationFailed(genRemoveMod, pPerformedModifications, "InvalidParameterException");
          break;
        }

        if(!tools.getFulfillerForShippingGroup(sg).equals(hf.getFulfillerName())) {
          // this shipping group is owned by another fulfiller
          // return true since this isn't an error.
          break;
        }        

	if (hf.isLoggingDebug())
	  hf.logDebug("Shipping Group is to be removed.");
	
        if(removeShippingGroup(pOrder, sg, genRemoveMod, pPerformedModifications)) {
          // nothing else to do
        }
	else {
          tools.modificationFailed(genRemoveMod, pPerformedModifications, 
                                   MessageFormat.format(Constants.SHIPPING_GROUP_REMOVE_FAILED, sg.getId()));
        }
        
        break;
      }
      
    case Modification.UPDATE :
      {
        // We are assuming this cast is okay.  If people add different kinds of Modifications
        // or choose to use a different message for that this method will need to be overriden.
        GenericUpdate genupMod = (GenericUpdate) pModification;
        
        // get the shipping group to be modified
        try {
          sg = pOrder.getShippingGroup(genupMod.getTargetId());
        } catch(ShippingGroupNotFoundException e) {
          if(hf.isLoggingError())
            hf.logError(Constants.SHIPPING_GROUP_NOT_IN_ORDER, e);
          tools.modificationFailed(pModification,
                                   pPerformedModifications,
                                   Constants.SHIPPING_GROUP_NOT_IN_ORDER);
          break;
        } catch(InvalidParameterException i) {
          if(hf.isLoggingError())
            hf.logError(i);
          tools.modificationFailed(pModification,
                                   pPerformedModifications,
                                   i.toString());
          break;
        }
        
        if(!tools.getFulfillerForShippingGroup(sg).equals(hf.getFulfillerName())) {
          // this shipping group is owned by another fulfiller
          // return true since this isn't an error.
          if(hf.isLoggingDebug())
            hf.logDebug("This shipping group is owned by another fulfiller: " + sg.getId());
          break;
        }        

        if(genupMod.getPropertyName() == null) {
          if(hf.isLoggingDebug())
            hf.logDebug("The GenericUpdate modification has no propertyName.");
          break;
        }

        // If the property that should be updated is state, and the value it should be set
        // to NO_PENDING_ACTION then ship it.
        if (genupMod.getPropertyName().equals("state")) {
          Object newState = genupMod.getNewValue();
          int stateValue;
          if(newState instanceof Integer) {
            stateValue = ((Integer) newState).intValue();
          } 
          else {
            stateValue = sgs.getStateFromString((String)newState);
          }
          if(stateValue == sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION))
          {
            // we only do this if the current state is PENDING_SHIPMENT
            boolean shipped = hf.shippingGroupHasShipped(pOrder, sg.getId(), genupMod, pPerformedModifications);
            break;
          }
        }
        else {
          tools.modificationFailed(pModification,
                                   pPerformedModifications,
                                   Constants.MODIFICATION_NOT_SUPPORTED);
          break;
        }
      }
    } //switch
  }

  //-------------------------------------
  /**
   * In this version of the order fulfillment system, no ModifyOrder
   * messages targeting Itemss are supported by HardgoodFulfiller
   *
   * @param pMessage The original message that contained the modifications
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications All new modifications are stored here.
   **/
  private void performItemModification(ModifyOrder pMessage, Order pOrder,
                                        Modification pModification,
                                        List pPerformedModifications)
  {
    HardgoodFulfiller hf = getHardgoodFulfiller();
    OrderFulfillmentTools tools = hf.getOrderFulfillmentTools();
    Modification mod = null;
    GenericUpdate genupMod = null;
    CommerceItem item = null;

    switch(pModification.getModificationType()) {
    case Modification.ADD :
      {
        tools.modificationFailed(pModification,
				 pPerformedModifications,
				 Constants.MODIFICATION_NOT_SUPPORTED);
        break;
      }
    case Modification.REMOVE :
      {
        tools.modificationFailed(pModification,
				 pPerformedModifications,
				 Constants.MODIFICATION_NOT_SUPPORTED);
        break;
      }
    case Modification.UPDATE :
      {
        tools.modificationFailed(pModification,
				 pPerformedModifications,
				 Constants.MODIFICATION_NOT_SUPPORTED);
        break;
      } //case
    } //switch
  }

  //-------------------------------------
  /**
   * In this version of the order fulfillment system, no ModifyOrder
   * messages targeting Relationships are supported by HardgoodFulfiller
   *
   * @param pMessage The original message that contained the modifications
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications All new modifications are stored here.
   **/
  private void performRelationshipModification(ModifyOrder pMessage, Order pOrder,
                                                Modification pModification,
                                                List pPerformedModifications)
  {
    HardgoodFulfiller hf = getHardgoodFulfiller();
    OrderFulfillmentTools tools = hf.getOrderFulfillmentTools();
    Relationship relationship = null;
    Modification mod = null;
    GenericUpdate genupMod = null;

    switch(pModification.getModificationType()) {
    case Modification.ADD :
      {
        tools.modificationFailed(pModification,
				 pPerformedModifications,
				 Constants.MODIFICATION_NOT_SUPPORTED);
        break;
      }
    case Modification.REMOVE :
      {
        tools.modificationFailed(pModification,
				 pPerformedModifications,
				 Constants.MODIFICATION_NOT_SUPPORTED);
        break;
      }
    case Modification.UPDATE :
      {
        tools.modificationFailed(pModification,
				 pPerformedModifications,
				 Constants.MODIFICATION_NOT_SUPPORTED);
        break;
      }// case
    } // switch
  }

  //-------------------------------------
  /**
   * This method will reprocess each of the shipping groups in the modification
   * Unlike the other handle methods, this only works on one kind of modification
   * @see HardgoodFulfiller#processMultipleShippingGroups
   *
   * @param pMessage The ModifyOrderNotification received
   * @param pOrder The order that was modified
   * @param pModification The modification containing the shipping group ids that were updated
   * @param pPerformedModifications All new modifications are stored here.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void handleShippingGroupUpdateModification(ModifyOrderNotification pMessage, 
                                                     Order pOrder, 
                                                     ShippingGroupUpdate pModification, 
                                                     List pPerformedModifications)
  {
    HardgoodFulfiller hf = getHardgoodFulfiller();
    OrderFulfillmentTools tools = hf.getOrderFulfillmentTools();

    String[] shipIds = pModification.getShippingGroupIds();      

    List thisGroupsModificationList = new ArrayList();
    
    try {
      hf.processMultipleShippingGroups(pOrder, shipIds, pPerformedModifications);
    } catch(CommerceException c) {
      try {
	hf.getTransactionManager().getTransaction().setRollbackOnly();
      }
      catch (javax.transaction.SystemException se) {
	// Hopefully this will never happen.
	hf.logError(se);
      }

    }
  }

  //-------------------------------------
  /**
   * Remove the given shipping group by setting its state to REMOVED
   * If the group cannot be removed, then fail the Modification
   *
   * @param pOrder The order we are removing the shipping group from
   * @param pShippingGroup The shipping group to remove
   * @param pModification The modification request this action is in response to
   * @param pPerformedModifications The list to store our performed modifications in
   * @return true if the shipping group was successfully removed, false if the removal was not complete
   **/
  private boolean removeShippingGroup(Order pOrder, ShippingGroup pShippingGroup,
                                   Modification pModification, List pPerformedModifications)
  {
    if(getHardgoodFulfiller().isLoggingDebug())
      getHardgoodFulfiller().logDebug("Inside removeShippingGroup");
    
    HardgoodFulfiller hf = getHardgoodFulfiller();
    ShippingGroupStates sgs = hf.getShippingGroupStates();
    OrderFulfillmentTools tools = hf.getOrderFulfillmentTools();
    boolean success = true;

    if(pShippingGroup.getState() == sgs.getStateValue(ShippingGroupStates.REMOVED)) {
      // This shipping group has already been removed.
      return true;
    }

    if(canShippingGroupBeRemoved(pShippingGroup)) {
      // cycle through the item relationships and remove them
      List itemRels = pShippingGroup.getCommerceItemRelationships();
      Iterator itemRelIter = itemRels.iterator();
      ShippingGroupCommerceItemRelationship sgcir = null;
        
      while(itemRelIter.hasNext()) {
	sgcir = (ShippingGroupCommerceItemRelationship) itemRelIter.next();
	// use and here so we return false if any part of the removal did not complete
	success = success & removeShippingGroupItemRelationship(pOrder, sgcir, pModification, pPerformedModifications);
      }


      if (success) {
        String msg = Constants.SHIP_GROUP_STATE_REMOVED;
	tools.setShippingGroupState(pShippingGroup,
				    sgs.getStateValue(ShippingGroupStates.REMOVED),
                                    MessageFormat.format(msg, pShippingGroup.getId()),
				    pPerformedModifications);
      }
      
      else  { // one of the items could not be removed... PENDING_MERCHANT_ACTION
        String msg = Constants.ITEM_RELATIONSHIP_FAILED_REMOVE;
	tools.setShippingGroupState(pShippingGroup,
				    sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION), 
                                    MessageFormat.format(msg, pShippingGroup.getId()),
				    pPerformedModifications);
      }
        
    }
    else {
      if(hf.isLoggingDebug())
        hf.logDebug("Shipping group is not modifiable.");
	
      tools.modificationFailed(pModification,
			       pPerformedModifications,
			       MessageFormat.format(Constants.SHIP_GROUP_NOT_MODIFIABLE, pShippingGroup.getId()));
      success = false;
    }
    return success;
  }

  //-------------------------------------
  /**
   * Remove the given shipping group by setting its state to REMOVED
   * If the group cannot be removed, then fail the Modification
   * Assume the group is not PENDING_SHIPMENT
   *
   * @param pOrder The order we are removing the shipping group item relationship from
   * @param pShippingGroupCommerceItemRelationship The shipping group item relationship to remove
   * @param pModification The modification request this action is in response to
   * @param pPerformedModifications The list to store our performed modifications in
   * @return true if the shipping group item relationship was successfully removed, false if the removal was not complete
   **/
  private boolean removeShippingGroupItemRelationship(Order pOrder, 
                                                      ShippingGroupCommerceItemRelationship pShippingGroupItemRelationship,
                                                      Modification pModification,
						      List pPerformedModifications)
  {
    if(getHardgoodFulfiller().isLoggingDebug())
      getHardgoodFulfiller().logDebug("Inside removeShippingGroupItemRelationship");

    HardgoodFulfiller hf = getHardgoodFulfiller();
    ShipItemRelationshipStates sirs = hf.getShipItemRelationshipStates();
    ShippingGroup sg = pShippingGroupItemRelationship.getShippingGroup();
    OrderFulfillmentTools tools = hf.getOrderFulfillmentTools();
    boolean success = true;

    if(pShippingGroupItemRelationship.getState() == sirs.getStateValue(ShipItemRelationshipStates.REMOVED)) {
      // This shipping group has already been removed.
      return true;
    }

    if(canShipItemRelBeRemoved(pShippingGroupItemRelationship)) {
      String sku = pShippingGroupItemRelationship.getCommerceItem().getCatalogRefId();
      long quantity = 0;
      
      if(pShippingGroupItemRelationship.getRelationshipType() ==
         RelationshipTypes.SHIPPINGQUANTITY)
      {
        quantity = pShippingGroupItemRelationship.getQuantity();
      }
      else { // SHIPPINGQUANTITYREMAINING
        quantity=hf.getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(pShippingGroupItemRelationship.getCommerceItem());
      }

      int inventoryReturnValue = InventoryManager.INVENTORY_STATUS_SUCCEED;
      try {
        if(pShippingGroupItemRelationship.getState() ==
           sirs.getStateValue(ShipItemRelationshipStates.PENDING_DELIVERY)) {
          // update the inventory
          inventoryReturnValue = hf.getInventoryManager().increaseStockLevel(sku, quantity); 
        }
        else if(pShippingGroupItemRelationship.getState() ==
                sirs.getStateValue(ShipItemRelationshipStates.BACK_ORDERED)) {
          // update the inventory
          inventoryReturnValue = hf.getInventoryManager().increaseBackorderLevel(sku, quantity); 
        }
        else if(pShippingGroupItemRelationship.getState() ==
                sirs.getStateValue(ShipItemRelationshipStates.PRE_ORDERED)) {
          // update the inventory
          inventoryReturnValue = hf.getInventoryManager().increasePreorderLevel(sku, quantity); 
        }
      } catch(InventoryException e) {
        if (hf.isLoggingError())
          hf.logError(e);
        
        tools.modificationFailed(pModification, pPerformedModifications, e.toString());
        sg.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION));
        success = false;
        return success;
      }
      if( inventoryReturnValue == InventoryManager.INVENTORY_STATUS_SUCCEED) {
        // do nothing
      }
      else if ( inventoryReturnValue == InventoryManager.INVENTORY_STATUS_ITEM_NOT_FOUND) {
        String errMsg = MessageFormat.format(Constants.INVENTORY_ITEM_NOT_FOUND, sku);
        if (hf.isLoggingError())
          hf.logError(errMsg);
        success = false;
        sg.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION));
        return false;
      }
      else if ( inventoryReturnValue == InventoryManager.INVENTORY_STATUS_FAIL) {
        String errMsg = MessageFormat.format(Constants.INVENTORY_STATUS_FAILED, sku, Long.toString(quantity));

        if (hf.isLoggingError())
          hf.logError(errMsg);
        sg.setState(StateDefinitions.SHIPPINGGROUPSTATES.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION));
        success = false;
        return false;
      }
      	  
      String msg = Constants.ITEM_RELATIONSHIP_STATE_REMOVED;
      tools.setItemRelationshipState(pShippingGroupItemRelationship,
                                     sirs.getStateValue(ShipItemRelationshipStates.REMOVED),
                                     MessageFormat.format(msg, pShippingGroupItemRelationship.getCommerceItem().getId(),
                                         pShippingGroupItemRelationship.getShippingGroup().getId()),
                                     pPerformedModifications);
            
    } else {
      // create failed
      if((pShippingGroupItemRelationship.getState() == sirs.getStateValue(ShipItemRelationshipStates.REMOVED)) ||
         (pShippingGroupItemRelationship.getState() == sirs.getStateValue(ShipItemRelationshipStates.PENDING_REMOVE))) {
        // this is fine
      } else {
        tools.modificationFailed(pModification, pPerformedModifications, 
                                 Constants.INVALID_SHIP_ITEM_REL_STATE);
        success = false;
      }
    }
    return success;
  }

  //-------------------------------------
  /**
   * Checks to see if the given shipping group is in a state that
   * allows it to be removed
   * States that cause this to return false: REMOVED, NO_PENDING_ACTION
   *
   * @param pShippingGroup The shipping group we want to change
   * @return true if the shipping group can be changed, false otherwise
   **/
  private boolean canShippingGroupBeRemoved(ShippingGroup pShippingGroup)
  {
    ShippingGroupStates sgs = getHardgoodFulfiller().getShippingGroupStates();
    int sgState = pShippingGroup.getState();
    if((sgState == sgs.getStateValue(ShippingGroupStates.REMOVED)) ||
       (sgState == sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION)))
    {
      return false;
    } 
    else {
      return true;
    }
  }

  //-------------------------------------
  /**
   * Checks to see if the given shipping group item relationship is in
   * a state that allows it to be changed
   * States that cause this to return false: DELIVERED, REMOVED, PENDING_RETURN
   *
   * @param pShippingGroupItemRelationship The shipping group item relationship we want to change
   * @return true if the shipping group item relationship can be changed, false otherwise
   **/
  private boolean canShipItemRelBeRemoved
    (ShippingGroupCommerceItemRelationship pShippingGroupItemRelationship)
  {
    ShipItemRelationshipStates sirs = getHardgoodFulfiller().getShipItemRelationshipStates();
    int sgcirState = pShippingGroupItemRelationship.getState();
    if((sgcirState == sirs.getStateValue(ShipItemRelationshipStates.DELIVERED)) ||
       (sgcirState == sirs.getStateValue(ShipItemRelationshipStates.REMOVED)) ||
       (sgcirState == sirs.getStateValue(ShipItemRelationshipStates.PENDING_RETURN))) 
    {
      return false;
    }
    else {
      return true;
    }
  }

  //-------------------------------------
  /**
   * This method is called to perform modifications on an Order level.  The default
   * implementation leaves this method as empty.  All modifications made should be added to the
   * pPerformedModifications list that is passed in.
   * @param pMessage the ModifyOrder message that was sent
   * @param pOrder the Order on which the modifications are to be performed
   * @param pModifications the order level modifications to be made
   * @param pPerformedModifications a list to which the modifications made in this method are added
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void performOrderModification(ModifyOrder pMessage, Order pOrder, 
					  List pModifications, List pPerformedModifications)
  {
    return;
  }
  

  //-------------------------------------
  /**
   * This method handles all @see IdTargetModification's that are
   * included in a @see ModifyOrderNotification message.  It calls 
   * the following methods:
   * <ul>
   *   <li>handleShippingGroupModification - called if the request's 
   *             TargetType is Modification.TARGET_SHIPPING_GROUP</li>
   *   <li>handleItemModification - called if the request's 
   *            TargetType is Modification.TARGET_ITEM</li>
   *   <li>handleRelationshipModification - called if the request's 
   *            TargetType is Modification.TARGET_RELATIONSHIP</li>
   * </ul>
   *
   * @see IdTargetModification
   * @see ModifyOrderNotification
   * @see #handleShippingGroupModification
   * @see #handleItemModification
   * @see #handleRelationshipModification
   * @see Modification
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void handleIdTargetModification(ModifyOrderNotification pMessage, Order pOrder, 
                                            IdTargetModification pModification, List pModificationList)
    throws javax.jms.JMSException
  {
    switch(pModification.getTargetType())
    {
    case Modification.TARGET_SHIPPING_GROUP:
      {
        handleShippingGroupModification(pMessage, pOrder, pModification, pModificationList);
        break;
      }            
    case Modification.TARGET_ITEM:
      {
        handleItemModification(pMessage, pOrder, pModification, pModificationList);
        break;
      }            
    case Modification.TARGET_RELATIONSHIP:
      {
        handleRelationshipModification(pMessage, pOrder, pModification, pModificationList);
        break;
      }
    } //switch
  }
  
  //-------------------------------------
  /**
   * This method is called to handle ModifyOrderNotification messages that affect the order
   * level.  The default implementation leaves this method as empty.  All modifications made as
   * a result of this method should be added to the should be added to the
   * pPerformedModifications list that is passed in.  Note: When dealing with
   * ModifyOrderNotification messages it is possible that although the modifications have
   * occured elsewhere this method might need to write information back to the Order repository
   * locally.  See the documentation for more details on the potential to build a distributed
   * fulfillment system.
   * @param pMessage the ModifyOrderNotification message that was sent
   * @param pOrder the Order object on which the modifications had been performed
   * @param pModifications the order level modifications to be made
   * @param pPerformedModifications a list to which the modifications made in this method are added
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void handleOrderModification(ModifyOrderNotification pMessage, 
					 Order pOrder,
					 List pOModifications, 
					 List pPerformedModifications)
  {
    return;
  }
  
  //-------------------------------------
  /**
   * This method is called to handle ModifyOrderNotification messages that affect the shipping
   * group level.  The default implementation leaves this method as empty.  All modifications
   * made as a result of this method should be added to the should be added to the
   * pPerformedModifications list that is passed in.  Note: When dealing with
   * ModifyOrderNotification messages it is possible that although the modifications have
   * occured elsewhere this method might need to write information back to the Order repository
   * locally.  See the documentation for more details on the potential to build a distributed
   * fulfillment system.
   * @param pMessage the ModifyOrderNotification message that was sent
   * @param pOrder the Order object on which the modifications had been performed
   * @param pModifications the order level modifications to be made
   * @param pPerformedModifications a list to which the modifications made in this method are added
   *
   * @deprecated Replaced by the pipeline processor 
   **/
  protected void handleShippingGroupModification(ModifyOrderNotification pMessage, 
                                               Order pOrder,
                                               Modification pModification,
                                               List pPerformedModifications)
  {
    return;
  }
  

  //-------------------------------------
  /**
   * This method is called to handle ModifyOrderNotification messages that affect the item
   * level.  The default implementation leaves this method as empty.  All modifications made as
   * a result of this method should be added to the should be added to the
   * pPerformedModifications list that is passed in.  Note: When dealing with
   * ModifyOrderNotification messages it is possible that although the modifications have
   * occured elsewhere this method might need to write information back to the Order repository
   * locally.  See the documentation for more details on the potential to build a distributed
   * fulfillment system.
   * @param pMessage the ModifyOrderNotification message that was sent
   * @param pOrder the Order object on which the modifications had been performed
   * @param pModifications the order level modifications to be made
   * @param pPerformedModifications a list to which the modifications made in this method are added
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void handleItemModification(ModifyOrderNotification pMessage, 
                                      Order pOrder,
                                      Modification pModification,
                                      List pPerformedModifications)
  {
    return;
  }
  
  //-------------------------------------
  /**
   * This method is called to handle ModifyOrderNotification messages that affect the
   * relationship level.  The default implementation leaves this method as empty.  All
   * modifications made as a result of this method should be added to the should be added to
   * the pPerformedModifications list that is passed in.  Note: When dealing with
   * ModifyOrderNotification messages it is possible that although the modifications have
   * occured elsewhere this method might need to write information back to the Order repository
   * locally.  See the documentation for more details on the potential to build a distributed
   * fulfillment system.
   * @param pMessage the ModifyOrderNotification message that was sent
   * @param pOrder the Order object on which the modifications had been performed
   * @param pModifications the order level modifications to be made
   * @param pPerformedModifications a list to which the modifications made in this method are added
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void handleRelationshipModification(ModifyOrderNotification pMessage, 
                                              Order pOrder,
                                              Modification pModification,
                                              List pPerformedModifications)
  {
    return;
  }

} // HardgoodFulfillerModificationHandler
