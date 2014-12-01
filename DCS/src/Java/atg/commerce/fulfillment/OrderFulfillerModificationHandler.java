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

// commerce classes
import atg.commerce.*;
import atg.commerce.fulfillment.*;
import atg.commerce.order.*;
import atg.commerce.payment.*;
import atg.commerce.states.*;
import atg.service.pipeline.*;

// dynamo classes

// java classes
import java.text.MessageFormat;
import java.util.*;
import javax.jms.*;

/**
 *<p>The OrderFulfillerModificationHandler deals with the
 * ModifyOrder and ModifyOrderNotifications messages received by the
 * OrderFulfiller. The OrderFulfiller contains a
 * ModificationHandler property, which is set by default to the
 * OrderFulfillerModificationHandler. This class is similar to the
 * HardgoodFulfillerModificationHandler.</p>
 *
 * <p>To change the handling behavior of ModifyOrder and ModifyOrderNotification
 * messages, extend the OrderFulfillerModificationHandler class and change
 * the ModificationHandler property OrderFulfiller to point to the new class.</p>
 *
 * <p>The default implementation deals with the following ModifyOrder modification:
 *      Remove an order:
 *                Remove an order by sending a ModifyOrder message containing a Modification
 *                of type REMOVE. The OrderFulfiller receives this message. If the order and
 *                its shipping group are not in a NO_PENDING_ACTION state, then ModifyOrder
 *                messages are sent to the various fulfillers handling the shipping groups.
 *                Every fulfiller who can cancel the shipping group responds by setting the
 *                state of the shipping group to PENDING_REMOVE. If all of the shipping group
 *                states are change to PENDING_REMOVE, then the order state changes to REMOVED
 *                and all of the shipping group states can be changed to REMOVED. An order
 *                cannot be canceled if any of its shipping groups have been shipped. If a
 *                GenericRemove modification is attempted on an order that cannot be removed
 *                (e.g. one of the shipping groups in the order has shipped) then the order is
 *                set to PENDING_MERCHANT_ACTION.
 *      Ship a shipping group:
 *                Notify the fulfillment system that a shipping group has shipped by sending
 *                a ModifyOrder message with a GenericUpdate (link to GenericUpdate section,
 *                it has this example) that changes the state of the shipping group from
 *                PENDING_SHIPMENT to NO_PENDING_ACTION.  The OrderFulfiller will receive this
 *                message and forward it to the responsible fulfiller
 *
 * <p>It also handles the following ModifyOrderNotification modification:
 *      Shipping group state change:
 *                Shipping group's state changes to NO_PENDING_ACTION, PENDING_MERCHANT_ACTION,
 *                PENDING_REMOVE, or failure to change to PENDING_REMOVE.
 *      ShippingGroupUpdate:
 *                When a customer updates a shipping group, the OrderFulfiller sends a
 *                ModifyOrderNotification message to the fulfiller responsible for this
 *                shipping group. This forces a reprocessing of the shipping group.
 *
 *
 *
 * Created: Mon Jan 24 15:21:02 2000
 *
 * @author Sam Perman
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/OrderFulfillerModificationHandler.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class OrderFulfillerModificationHandler implements ModificationHandler {

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/OrderFulfillerModificationHandler.java#2 $$Change: 651448 $";


  //---------------------------------------------------------------------------
  // property:OrderFulfiller
  //---------------------------------------------------------------------------

  /**
   * The fulfiller object that this class handles modifications for
   **/
  private OrderFulfiller mOrderFulfiller;

  public void setOrderFulfiller(OrderFulfiller pOrderFulfiller) {
    mOrderFulfiller = pOrderFulfiller;
  }

  public OrderFulfiller getOrderFulfiller() {
    return mOrderFulfiller;
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
   * the OrderFulfiller class on handling a ModifyOrder message this
   * method should be overridden. This method calls to the pipeline chain to process the
   * message.
   * <ul>
   *   <li>performIdTargetModification - called if the modification is an
   *       IdTargetModification </li>
   * </ul>
   *
   * @param pPortName The port from which this message was received.
   * @param pMessage The message that was received.
   * @exception javax.jms.JMSException
   * @see ModifyOrder
   * @see #performIdTargetModification
   * @see IdTargetModification
   **/
  public void handleModifyOrder(String pPortName,
				ObjectMessage pMessage)
    throws JMSException
  {
      
      OrderFulfiller of = getOrderFulfiller();
      
      if (of.isLoggingDebug())
          of.logDebug("Handling a modifyOrder message in OrderFulfillerModificationHandler");
      
      // the input params to the chain
      HashMap map = new HashMap(10);
      map.put(PipelineConstants.MESSAGE, pMessage);
      map.put(PipelineConstants.ORDERFULFILLER, of);
      
      try {
          String chainToRun = (String) getChainToRunMap().get("modifyOrderChain");
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
   * change the behavior of the OrderFulfiller class on handling a
   * ModifyOrderNotification message this method should be
   * overridden. This method calls to the pipeline chain to process the
   * message.
   * <ul>
   *   <li>handleShippingGroupUpdateModification - called if the modification is
   *       of type ShippingGroupUpdate
   *   <li>handlePaymentGroupUpdateModification - called if the modification is
   *       of type PaymentGroupUpdate
   *   <li>handleIdTargetModification - called if the modificatoin is
   *       of type IdTargetModification
   * </ul>
   *
   * @param pPortName The port from which this message was received.
   * @param pMessage The message that was received.
   * @exception javax.jms.JMSException
   * @see ModifyOrderNotification
   * @see #handleShippingGroupUpdateModification
   * @see ShippingGroupUpdate
   * @see #handleIdTargetModification
   * @see IdTargetModification
   **/
  public void handleModifyOrderNotification(String pPortName,
					    ObjectMessage pMessage)
    throws JMSException
  {
      OrderFulfiller of = getOrderFulfiller();
      
      if (of.isLoggingDebug())
          of.logDebug("Handling a modifyOrderNotification message in OrderFulfillerModificationHandler");
      
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
   * This method performs all @see IdTargetModification's that are
   * included in a @see ModifyOrder message.  It calls the following
   * methods:
   * <ul>
   *   <li>performOrderModification - called if the request's
   *            TargetType is Modification.TARGET_ORDER</li>
   *   <li>performShippingGroupModification - called if the request's
   *             TargetType is Modification.TARGET_SHIPPING_GROUP</li>
   *   <li>performItemModification - called if the request's
   *            TargetType is Modification.TARGET_ITEM</li>
   *   <li>performRelationshipModification - called if the request's
   *    \        TargetType is Modification.TARGET_RELATIONSHIP</li>
   * </ul>
   *
   * @see IdTargetModification
   * @see ModifyOrder
   * @see #performOrderModification
   * @see #performShippingGroupModification
   * @see #performItemModification
   * @see #performRelationshipModification
   * @see Modification
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void performIdTargetModification(ModifyOrder pMessage, Order pOrder,
                                             IdTargetModification pModification,
                                             List pModificationList)
  {
    switch(pModification.getTargetType())
    {
    case Modification.TARGET_ORDER:
      {
        performOrderModification(pMessage, pOrder, pModification, pModificationList);
        break;
      }
    case Modification.TARGET_SHIPPING_GROUP:
      {
        performShippingGroupModification(pMessage, pOrder, pModification, pModificationList);
        break;
      }
    case Modification.TARGET_ITEM:
      {
        performItemModification(pMessage, pOrder, pModification, pModificationList);
        break;
      }
    case Modification.TARGET_RELATIONSHIP:
      {
        performRelationshipModification(pMessage, pOrder, pModification, pModificationList);
        break;
      }
    default:
      {
        OrderFulfiller of = getOrderFulfiller();
        if(of.isLoggingError())
          of.logError(Constants.MODIFICATION_NOT_SUPPORTED);

        // Mark the Modifications as unsupported and fail the ModifyOrder
        of.getOrderFulfillmentTools().modificationFailed(pModification,
                                                         pModificationList,
                                                         Constants.MODIFICATION_NOT_SUPPORTED);

        break;
      }
    } //switch
  }

  //-------------------------------------
  /**
   * Perform the requested Modifications.  The only modification request
   * that this method currently implements is a GenericRemove
   * (ie. cancel this order).  If such a modification request is received,
   * removeOrder is called.
   *
   * @param pMessage The original message that contained the modifications
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications All new modifications are stored here.
   * @see GenericRemove
   * @see #removeOrder
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void performOrderModification(ModifyOrder pMessage, Order pOrder,
                                          Modification pModification, List pPerformedModifications)
  {
    OrderFulfiller of = getOrderFulfiller();

    if (of.isLoggingDebug())
      of.logDebug("Inside OrderFulfillerModificationHandler.performOrderModifications.");

    // Get the order that contains those items.
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();

    switch(pModification.getModificationType())
    {
    case Modification.ADD :
      {
        tools.modificationFailed(pModification, pPerformedModifications,
                                 Constants.MODIFICATION_NOT_SUPPORTED);
        break;
      }
    case Modification.REMOVE :
      {
        GenericRemove genRemoveMod = (GenericRemove) pModification;

        // Someone wants to remove this order.
        if(removeOrder(pOrder, genRemoveMod, pPerformedModifications)) {
          tools.modificationSucceeded(genRemoveMod, pPerformedModifications);
        } else {
          tools.modificationFailed(genRemoveMod, pPerformedModifications,
                                   MessageFormat.format(Constants.REMOVE_ORDER_FAILED, pOrder.getId()));
        }
        break;
      }
    case Modification.UPDATE :
      {
        tools.modificationFailed(pModification, pPerformedModifications,
                                 Constants.MODIFICATION_NOT_SUPPORTED);
        break;
      } //case
    } // switch
  } // performOrderModification

  //-------------------------------------
  /**
   * In this version of the order fulfillment system, no ModifyOrder
   * messages targeting ShippingGroups are supported
   *
   * @param pMessage The original message that contained the modifications
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications Place to store notifications of changes
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void performShippingGroupModification(ModifyOrder pMessage, Order pOrder,
                                                  Modification pModification,
                                                  List pPerformedModifications)
  {
    OrderFulfiller of = getOrderFulfiller();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    Modification mod = null;
    ShippingGroup sg = null;
    ShippingGroupStates sgs = of.getShippingGroupStates();

    // We are assuming this cast is okay.  If people add different kinds of Modifications
    // or choose to use a different message for that this method will need to be overriden.
    IdTargetModification targetMod = (IdTargetModification) pModification;

    try {
      sg = pOrder.getShippingGroup(targetMod.getTargetId());
    } catch(ShippingGroupNotFoundException s) {
      String errMessage = MessageFormat.format(Constants.TARGET_SHIP_GROUP_NOT_FOUND, pMessage.getId());

      if (of.isLoggingError())
        of.logError(errMessage);
      tools.modificationFailed(targetMod, pPerformedModifications, errMessage);
    }
    catch(InvalidParameterException i) {
      if (of.isLoggingError())
        of.logError(i);

      tools.modificationFailed(targetMod, pPerformedModifications, "InvalidParameterException");
    }

    if((sg.getState() == sgs.getStateValue(ShippingGroupStates.INITIAL)) ||
       (sg.getState() == sgs.getStateValue(ShippingGroupStates.REMOVED))) {
      // ie the OrderFulfiller owns the shipping group
      getOrderFulfiller().getOrderFulfillmentTools().modificationFailed(pModification, pPerformedModifications,
                                                                        Constants.MODIFICATION_NOT_SUPPORTED);
    }
    else {
      String fulfiller = tools.getFulfillerForShippingGroup(sg);
      List mods = new ArrayList();
      mods.add(pModification);

      try {
        tools.sendModifyOrder(pMessage.getOrderId(), mods, of,
                              tools.getFulfillerPort(fulfiller), pMessage);
      }
      catch(javax.jms.JMSException j) {
        if(of.isLoggingError())
          of.logError(j);
      }
    }
  }

  //------------------------------------
  /**
   * In this version of the order fulfillment system, no ModifyOrder
   * messages targeting Items are supported
   *
   * @param pMessage The original message that contained the modifications
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications All new modifications are stored here.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void performItemModification(ModifyOrder pMessage, Order pOrder,
                                         Modification pModification, List pPerformedModifications)
  {
    getOrderFulfiller().getOrderFulfillmentTools().modificationFailed(pModification, pPerformedModifications,
                                                                      Constants.MODIFICATION_NOT_SUPPORTED);
  }

  //------------------------------------
  /**
   * In this version of the order fulfillment system, no ModifyOrder
   * messages targeting Relationships are supported
   *
   * @param pMessage The original message that contained the modifications
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications All new modifications are stored here.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void performRelationshipModification(ModifyOrder pMessage, Order pOrder,
                                                 Modification pModification, List pPerformedModifications)
  {
    OrderFulfiller of = getOrderFulfiller();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    Modification mod = null;
    ShippingGroup sg = null;
    ShippingGroupCommerceItemRelationship sgcir = null;
    ShippingGroupStates sgs = of.getShippingGroupStates();

    // We are assuming this cast is okay.  If people add different kinds of Modifications
    // or choose to use a different message for that this method will need to be overriden.
    IdTargetModification targetMod = (IdTargetModification) pModification;

    try {
      sgcir = (ShippingGroupCommerceItemRelationship) pOrder.getRelationship(targetMod.getTargetId());
      sg = sgcir.getShippingGroup();
    } catch(RelationshipNotFoundException s) {
      String errMessage = MessageFormat.format(Constants.TARGET_SHIP_GROUP_ITEM_REL_NOT_FOUND, pMessage.getId());

      if (of.isLoggingError())
        of.logError(errMessage);
      tools.modificationFailed(targetMod, pPerformedModifications, errMessage);
    }
    catch(InvalidParameterException i) {
      if (of.isLoggingError())
        of.logError(i);

      tools.modificationFailed(targetMod, pPerformedModifications, "InvalidParameterException");
    }

    if((sg.getState() == sgs.getStateValue(ShippingGroupStates.INITIAL)) ||
       (sg.getState() == sgs.getStateValue(ShippingGroupStates.REMOVED))) {
      // ie the OrderFulfiller owns the shipping group
      getOrderFulfiller().getOrderFulfillmentTools().modificationFailed(pModification, pPerformedModifications,
                                                                        Constants.MODIFICATION_NOT_SUPPORTED);
    }
    else {
      String fulfiller = tools.getFulfillerForShippingGroup(sg);
      List mods = new ArrayList();
      mods.add(pModification);

      try {
        tools.sendModifyOrder(pMessage.getOrderId(), mods, of,
                              tools.getFulfillerPort(fulfiller), pMessage);
      }
      catch(javax.jms.JMSException j) {
        if(of.isLoggingError())
          of.logError(j);
      }
    }
  }


  //-------------------------------------
  /**
   * This method handles all @see IdTargetModification's that are
   * included in a @see ModifyOrderNotification message.  It calls
   * the following methods:
   * <ul>
   *   <li>handleOrderModification - called if the request's
   *            TargetType is Modification.TARGET_ORDER</li>
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
   * @see #handleOrderModification
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
    case Modification.TARGET_ORDER:
      {
        handleOrderModification(pMessage, pOrder, pModification, pModificationList);
        break;
      }
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
   * In this version of the order fulfillment system, ModifyOrderNotification
   * messages targeting Orders are ignored
   *
   * @param pMessage the ModifyOrderNotification received
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications All new modifications are stored here.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void handleOrderModification(ModifyOrderNotification pMessage,
                                         Order pOrder,
                                         Modification pModification,
                                         List pPerformedModifications)
  {

  }


  //-------------------------------------
  /**
   * This method initiates any processing that should be done as a
   * result of a change to a ShippingGroup.  The following
   * modifications are listened for.
   * <ul>
   *   <li>GenericRemove - If a GenericRemove modification notification is received
   *       and the status of that modification is Modification.STATUS_FAILED, then
   *       the order is placed in a OrderStates.PENDING_MERCHANT_ACTION state</li>
   *   <li>GenericUpdate - if the property being update is ShippingGroup.getState and the
   *       value is:
   *       <ul>
   *         <li>ShippingGroupStates#REMOVED then it is possible that
   *                  someone request the whole order be removed.
   *                  finishRemovingOrder is called</li>
   *         <li>ShippingGroupStates.NO_PENDING_ACTION then it is possible that
   *                  the order is ready to settle payment.</li>
   *         <li>ShippingGroupStates.PENDING_MERCHANT_ACTION then place the order
   *                  in a OrderStates.PENDING_MERCHANT_ACTION state
   *       </ul></li>
   * </ul>
   *
   * @param pMessage the ModifyOrderNotification received
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications All new modifications are stored here.
   * @see GenericRemove
   * @see GenericUpdate
   * @see atg.commerce.states.ShippingGroupStates
   * @see atg.commerce.states.OrderStates
   * @see #finishRemovingOrder
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void handleShippingGroupModification(ModifyOrderNotification pMessage,
                                                 Order pOrder,
                                                 Modification pModification,
                                                 List pPerformedModifications)
    throws javax.jms.JMSException
  {
    // Get the order that contains those shipping groups.
    OrderFulfiller of = getOrderFulfiller();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShippingGroupStates sgs = of.getShippingGroupStates();
    OrderStates os = of.getOrderStates();
    ShippingGroup sg = null;

    switch(pModification.getModificationType()) {
    case Modification.ADD :
      {
        // nothing to do in response to this.
        break;
      }
    case Modification.REMOVE :
      {
        // nothing to do in response to this.
        if(pModification.getModificationStatus() == Modification.STATUS_FAILED) {
          cancelRemoveOrder(pOrder, pPerformedModifications);
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
          if(of.isLoggingError())
            of.logError(Constants.SHIPPING_GROUP_NOT_IN_ORDER, e);
          break;
        } catch(InvalidParameterException i) {
          if(of.isLoggingError())
            of.logError(i);
          break;
        }

        if(genupMod.getPropertyName() == null) {
          if(of.isLoggingDebug())
            of.logDebug("The GenericUpdate modification has no propertyName.");
          break;
        }

        // If the property that was updated is state, and the value it was updated to is
        // not NO_PENDING_ACTION, then this order is not finished, otherwise it is
        if (genupMod.getPropertyName().equals("state")) {
          //int stateValue = ((Integer) genupMod.getNewValue()).intValue();
          Object newState = genupMod.getNewValue();
          int stateValue;
          if(newState instanceof Integer) {
            stateValue = ((Integer) newState).intValue();
          }
          else {
            stateValue = sgs.getStateFromString((String)newState);
          }
          if(stateValue == sgs.getStateValue(ShippingGroupStates.REMOVED))
          {
            if((pOrder.getState() == os.getStateValue(OrderStates.PENDING_REMOVE)) &&
               (genupMod.getModificationStatus() == Modification.STATUS_SUCCESS)) {
              if(areAllGroupsRemoved(pOrder)) {
                  finishRemovingOrder(pOrder, pPerformedModifications);
              }
            } else {
              // finish removing shipping group
              //finishRemovingShippingGroup(sg, pPerformedModifications);
            }

          }
         else if(stateValue == sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION))
         {
           if(genupMod.getModificationStatus() == Modification.STATUS_SUCCESS) {
             try {
               if(tools.isOrderSettleable(pOrder, of.isSettleOnFirstShipment())) {
                 of.settleOrder(pOrder, pPerformedModifications);
               }
               if(tools.isOrderFinished(pOrder)) {
                 of.finishOrder(pOrder, pPerformedModifications);
               }
             }
             catch (PaymentException pe) {
               tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
                                   pe.toString(), pPerformedModifications);
             }
           }
         }
         else if(stateValue == sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION))
         {
           // something went wrong... stop the order
           if(genupMod.getModificationStatus() == Modification.STATUS_SUCCESS) {
             String msg = Constants.SHIP_GROUP_STATE_PENDING_MERCHANT_ACTION;
             tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
                                 MessageFormat.format(msg, sg.getId()),
                                 pPerformedModifications);
           }
         }
        } // if
        break;
      } // case
    } // switch
  }

  //-------------------------------------
  /**
   * In this version of the order fulfillment system, ModifyOrderNotification
   * messages targeting Items are ignored
   *
   * @param pMessage the ModifyOrderNotification received
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications All new modifications are stored here.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void handleItemModification(ModifyOrderNotification pMessage,
                                        Order pOrder,
                                        Modification pModification,
                                        List pPerformedModifications)
  {
  }


  //-------------------------------------
  /**
   * This method initiates any processing that should be done as a
   * result of a change to a Relationship.  Only one modification
   * is listened for.
   * <ul>
   *   <li>GenericUpdate - if the property being updated is Relationship.getState, the
   *       type of Relationship is ShippingGroupCommerceItemRelationship, and the value is:
   *       <ul>
   *         <li>ShipItemRelationshipStates#REMOVED then decrement the relationship quantity
   *             from its commerce item.  (If the order or the commerce item is either PENDING_REMOVE
   *             or REMOVED, then the quantity is not updated.)
   *         </li>
   *       </ul>
   *   </li>
   * </ul>
   *
   * @param pMessage the ModifyOrderNotification received
   * @param pOrder The order that was modified.
   * @param pModification The modification that was performed
   * @param pPerformedModifications All new modifications are stored here.
   * @see GenericUpdate
   * @see atg.commerce.states.ShipItemRelationshipStates
   * @see #shipItemRelWasRemoved
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void handleRelationshipModification(ModifyOrderNotification pMessage,
                                                Order pOrder,
                                                Modification pModification,
                                                List pPerformedModifications)
  {
    // Get the order that contains those shipping groups.
    OrderFulfiller of = getOrderFulfiller();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    OrderStates os = of.getOrderStates();
    CommerceItemStates cis = of.getCommerceItemStates();
    ShippingGroupCommerceItemRelationship sgcir = null;

    if(of.isLoggingDebug())
      of.logDebug("Inside handleRelationshipModification");

    switch(pModification.getModificationType()) {
    case Modification.ADD :
      {
        // nothing to do in response to this.
        break;
      }
    case Modification.REMOVE :
      {
        // nothing to do in response to this.
        break;
      }
    case Modification.UPDATE :
      {
        // We are assuming this cast is okay.  If people add different kinds of Modifications
        // or choose to use a different message for that this method will need to be overriden.
        GenericUpdate genupMod = (GenericUpdate) pModification;

        // get the shipping group to be modified
        try {
          Relationship r = pOrder.getRelationship(genupMod.getTargetId());
          if(!(r instanceof ShippingGroupCommerceItemRelationship)) {
            if(of.isLoggingDebug())
              of.logDebug("Modification of a relationship other than a ShipItemRel.  Ignored.");
            break;
          }
          sgcir = (ShippingGroupCommerceItemRelationship) r;
        } catch(RelationshipNotFoundException e) {
          if(of.isLoggingError()) {
            of.logError(MessageFormat.format(Constants.ITEM_RELATIONSHIP_NOT_IN_ORDER, genupMod.getTargetId(), pOrder.getId()), e);
          }
          break;
        } catch(InvalidParameterException i) {
          if(of.isLoggingError())
            of.logError(i);
          break;
        }

        if(genupMod.getPropertyName() == null) {
          if(of.isLoggingDebug())
            of.logDebug("The GenericUpdate modification has no propertyName.");
          break;
        }

        // If the property that was updated is state, and the value it was updated to is
        // not REMOVED, then the CommerceItem quantity needs to be updated
        if (genupMod.getPropertyName().equals("state")) {
          //int stateValue = ((Integer) genupMod.getNewValue()).intValue();
          Object newState = genupMod.getNewValue();
          int stateValue;
          if(newState instanceof Integer) {
            stateValue = ((Integer) newState).intValue();
          }
          else {
            stateValue = sirs.getStateFromString((String)newState);
          }
          if((stateValue == sirs.getStateValue(ShipItemRelationshipStates.REMOVED)) &&
             (genupMod.getModificationStatus() == Modification.STATUS_SUCCESS))
          {
            if(of.isLoggingDebug())
              of.logDebug("ShipItemRelationship " + sgcir.getId() + " was removed.");

            CommerceItem ci = sgcir.getCommerceItem();
            if((pOrder.getState() == os.getStateValue(OrderStates.PENDING_REMOVE)) ||
               (pOrder.getState() == os.getStateValue(OrderStates.REMOVED))) {
              // the order was removed... no need to update the quantity
            }
            else if((ci.getState() == cis.getStateValue(CommerceItemStates.PENDING_REMOVE)) ||
                    (ci.getState() == cis.getStateValue(CommerceItemStates.REMOVED))) {
              // the commerce item was removed... no need to update the quantity
            }
            else {
              // update the quantity
              shipItemRelWasRemoved(pOrder, sgcir);
            }
          }
        }
      }
    }
  }

  //-------------------------------------
  /**
   * This method will reprocess each of the shipping groups in the
   * ShippingGroupUpdate modification.  Sends a
   * ModifyOrderNotification to each fulfiller that owns a each of the
   * updated shipping groups.
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
    throws JMSException
  {
    OrderFulfiller of = getOrderFulfiller();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShippingGroupStates sgs = of.getShippingGroupStates();

    String[] shippingGroupIds = pModification.getShippingGroupIds();
    List shippingGroupsList = new ArrayList();
    ShippingGroup sg = null;

    try {
      for(int i=0;i<shippingGroupIds.length;i++) {
        sg = pOrder.getShippingGroup(shippingGroupIds[i]);
        int sgState = sg.getState();
        if(sgState == sgs.getStateValue(ShippingGroupStates.INITIAL))
        {
          String msg = Constants.SHIP_GROUP_STATE_PROCESSING;
          tools.setShippingGroupState(sg, sgs.getStateValue(ShippingGroupStates.PROCESSING),
                                      MessageFormat.format(msg, sg.getShippingGroupClassType()),
                                      pPerformedModifications);
          of.setShippingGroupSubmittedDate(sg, pPerformedModifications);
          shippingGroupsList.add(sg);
        }
        else if(sgState == sgs.getStateValue(ShippingGroupStates.PROCESSING))
        {
          shippingGroupsList.add(sg);
        }
      }// for

      tools.sendShippingGroupUpdateModification(pOrder, shippingGroupsList, of, pMessage);
    }
    catch(ShippingGroupNotFoundException sgnfe) {
      if(of.isLoggingError())
        of.logError(sgnfe);
    }
    catch (InvalidParameterException ipe) {
      if(of.isLoggingError())
        of.logError(ipe);
    }

  }

  //-------------------------------------
  /**
   * This method does not do anything.  If processing is needed when
   * a payment group is changed, extend this class and overwrite this
   * method.
   *
   * @param pMessage The ModifyOrderNotification received
   * @param pOrder The order that was modified
   * @param pModification The modification containing the payment group ids that were updated
   * @param pPerformedModifications All new modifications are stored here.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void handlePaymentGroupUpdateModification(ModifyOrderNotification pMessage,
                                                     Order pOrder,
                                                     PaymentGroupUpdate pModification,
                                                     List pPerformedModifications)
    throws JMSException
  {

  }

  //-------------------------------------
  /**
   * This is called as a result of a ModifyOrder message with a
   * GenericRemove of an order.  It requests that each shipping group
   * is removed by the appropriate fulfillers, by sending a
   * ModifyOrder message to that fulfiller.  It also sets each CommerceItem
   * to PENDING_REMOVE
   *
   * @param pOrder The order being removed.
   * @param pUpdate The modification originally requesting that this order be removed
   * @param pPerformedModifications A place to store all new modifications.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected boolean removeOrder(Order pOrder, Modification pUpdate, List pPerformedModifications)
  {
    OrderFulfiller of = getOrderFulfiller();

    if (of.isLoggingDebug())
      of.logDebug("Removing order.");

    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    OrderStates os = of.getOrderStates();
    ShippingGroupStates sgs = of.getShippingGroupStates();
    CommerceItemStates cis = of.getCommerceItemStates();
    HashMap fulfillerToShippingGroups = tools.
      getFulfillersForShippingGroups(pOrder.getShippingGroups());

    Set fulfillers = fulfillerToShippingGroups.keySet();
    Iterator fulfillerIter = fulfillers.iterator();
    HashMap messagesToSend = new HashMap();
    boolean anythingShipped = false;  // set to true if any shipping group is set to NO_PENDING_ACTION
    boolean anythingPayed = false;  // set to true if any payment group is set to SETTLED
    boolean success = true;

    if(pOrder.getState() == os.getStateValue(OrderStates.PENDING_REMOVE)) {
      // orderFulfiller has already processed this request
      return true;
    }

    try {
      of.verifyOrderFulfillment(pOrder);
    } catch(IllegalOrderStateException e) {
      if(of.isLoggingError())
        of.logError(MessageFormat.format(Constants.ORDER_STATE_NO_MODIFY, pOrder.getId()));
      return false;
    }

    // if any groups have shipped, can't remove order.

    if(tools.hasAnyGroupShipped(pOrder)) {
      if(of.isLoggingInfo())
        of.logInfo(MessageFormat.format(Constants.SOMETHING_SHIPPED, pOrder.getId()));
      return false;
    }

    if(!of.isAllowRemoveOrderWithPendingShipment()) {
      if(tools.isAnyGroupPendingShipment(pOrder)) {
        if(of.isLoggingInfo())
          of.logInfo(MessageFormat.format(Constants.SOMETHING_SHIPPED, pOrder.getId()));
        return false;
      }
    }

    // change the state of the order
    String msg = Constants.REMOVE_ORDER_REQUESTED;
    tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_REMOVE),
                        MessageFormat.format(msg, pOrder.getId()),
                        pPerformedModifications);

    // then process each CommerceItem
    List commerceItems = pOrder.getCommerceItems();
    Iterator cItemIter = commerceItems.iterator();
    while(cItemIter.hasNext()) {
      CommerceItem commerceItem = (CommerceItem) cItemIter.next();
      if(commerceItem.getState() != cis.getStateValue(CommerceItemStates.REMOVED)) {
        msg = Constants.REMOVE_ORDER_REQUESTED;
        tools.setItemState(commerceItem, cis.getStateValue(CommerceItemStates.PENDING_REMOVE),
                           MessageFormat.format(msg, pOrder.getId()),
                           pPerformedModifications);
      }
    }

    // then process each shipping group
    while(fulfillerIter.hasNext()) {
      String fulfiller = (String) fulfillerIter.next();
      String fulfillerPort = tools.getFulfillerPort(fulfiller);
      List shippingGroups = (List) fulfillerToShippingGroups.get(fulfiller);
      List newModifications = new ArrayList();

      // for each shipping group, create a modification that sets its state to REMOVED
      Iterator shippingGroupIter = shippingGroups.iterator();
      while(shippingGroupIter.hasNext()) {
        ShippingGroup sg = (ShippingGroup) shippingGroupIter.next();

        // we already checked if anything shipped.
        if(sg.getState() == sgs.getStateValue(ShippingGroupStates.INITIAL)) {
          msg = Constants.REMOVE_ORDER_REQUESTED;
          tools.setShippingGroupState(sg, sgs.getStateValue(ShippingGroupStates.REMOVED),
                                      MessageFormat.format(msg, pOrder.getId()),
                                      pPerformedModifications);
          break;
        } else {
          GenericRemove removeMod = tools.createGenericRemoveModification(Modification.TARGET_SHIPPING_GROUP,
                                                                          sg.getId(),
                                                                          Modification.TARGET_ORDER,
                                                                          pOrder.getId());
          newModifications.add(removeMod);
        }
      }

      try {
        tools.sendModifyOrder(pOrder.getId(), newModifications, of, fulfillerPort, null);
      } catch(JMSException j) {
        if(of.isLoggingError())
          of.logError(j);
        return false;
      }
    }
    return success;
  }

  /**
   * Check if all the groups in this object are PENDING_REMOVE
   *
   * @param pOrder The order we are checking
   * @return true if all groups are removed, false otherwise
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected boolean areAllGroupsPendingRemove(Order pOrder)
  {
    List shippingGroups = pOrder.getShippingGroups();
    Iterator shipIter = shippingGroups.iterator();
    ShippingGroupStates sgs = getOrderFulfiller().getShippingGroupStates();

    while(shipIter.hasNext()) {
      ShippingGroup sg = (ShippingGroup) shipIter.next();

      if(sg.getState() == sgs.getStateValue(ShippingGroupStates.PENDING_REMOVE))
      {
        // nothing to do
        continue;
      }
      else {
        return false;
      }
    }

    return true;
  }

  /**
   * Check if all the groups in this object are REMOVED
   *
   * @param pOrder The order we are checking
   * @return true if all groups are removed, false otherwise
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected boolean areAllGroupsRemoved(Order pOrder)
  {
    List shippingGroups = pOrder.getShippingGroups();
    Iterator shipIter = shippingGroups.iterator();
    ShippingGroupStates sgs = getOrderFulfiller().getShippingGroupStates();

    while(shipIter.hasNext()) {
      ShippingGroup sg = (ShippingGroup) shipIter.next();

      if(sg.getState() == sgs.getStateValue(ShippingGroupStates.REMOVED))
      {
        // nothing to do
        continue;
      }
      else {
        return false;
      }
    }

    return true;
  }

  /**
   * Set the order to REMOVED and sets each CommerceItem to REMOVED,
   * and sets each PaymentGroup to REMOVED.  Assumes the order is
   * currently in a PENDING_REMOVE state.  Also credits any
   * PaymentGroups that have been debited.  Assumes that the person
   * calling this method has verified that all the shippinggroups are
   * in a state that allows them to be removed.
   *
   * @param pOrder the order we are changing
   * @param pModificationList A place to store our modifications
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void finishRemovingOrder(Order pOrder, List pModificationList)
  {

    OrderFulfiller of = getOrderFulfiller();
    OrderStates os = of.getOrderStates();
    ShippingGroupStates sgs = of.getShippingGroupStates();
    CommerceItemStates cis = of.getCommerceItemStates();
    PaymentGroupStates pgs = of.getPaymentGroupStates();
    List shippingGroups = pOrder.getShippingGroups();
    Iterator shipIter = shippingGroups.iterator();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();

    while(shipIter.hasNext()) {
      ShippingGroup sg = (ShippingGroup) shipIter.next();
      //finishRemovingShippingGroup(sg, pModificationList);
      //if(sg.getState() != sgs.getStateValue(sgs.REMOVED))
        // <TBD>
        //throw new CommerceException("// <TBD>");
    }

    List commerceItems = pOrder.getCommerceItems();
    Iterator itemIter = commerceItems.iterator();
    while(itemIter.hasNext()) {
      CommerceItem ci = (CommerceItem) itemIter.next();
      if(ci.getState() != cis.getStateValue(CommerceItemStates.REMOVED))
        tools.setItemState(ci, cis.getStateValue(CommerceItemStates.REMOVED),
                           Constants.REMOVE_ORDER_FINISHED,
                           pModificationList);
    }

    // set the order state
    if(creditOrder(pOrder, pModificationList)) {
      tools.setOrderState(pOrder, os.getStateValue(OrderStates.REMOVED),
                          Constants.REMOVE_ORDER_FINISHED,
                          pModificationList);
      // remove each payment group
      List paymentGroups = pOrder.getPaymentGroups();
      Iterator payIter = paymentGroups.iterator();
      while(payIter.hasNext()) {
        PaymentGroup pg = (PaymentGroup) payIter.next();
        tools.setPaymentGroupState(pg, pgs.getStateValue(PaymentGroupStates.REMOVED),
                                   Constants.REMOVE_ORDER_FINISHED,
                                   pModificationList);
      }
    } else {
      if(of.isLoggingError()) {
        of.logError(MessageFormat.format(Constants.ORDER_CREDIT_FAILED, pOrder.getId()));
        of.logError(MessageFormat.format(Constants.REMOVE_ORDER_FAILED, pOrder.getId()));
      }
    }
  }

  /**
   * Set the shipping group to REMOVED, and set each item relattion ship to REMOVED Assumes the
   * shipping group is currently in a PENDING_REMOVE state.  Assumes that the item
   * relationship state was checked before this is called.
   *
   * @param pShippingGroup the shipping group we are changing
   * @param pModificationList A place to store our modifications
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void finishRemovingShippingGroup(ShippingGroup pShippingGroup,
                                           List pModificationList)
  {

    OrderFulfiller of = getOrderFulfiller();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    ShippingGroupStates sgs = of.getShippingGroupStates();
    List itemRelationships = pShippingGroup.getCommerceItemRelationships();
    Iterator itemIter = itemRelationships.iterator();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();

    while(itemIter.hasNext()) {
      ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) itemIter.next();

      tools.setItemRelationshipState(sgcir, sirs.getStateValue(ShipItemRelationshipStates.REMOVED),
                                     Constants.REMOVE_SHIPPING_GROUP_FINISHED,
                                     pModificationList);

    }

    // set the shipping group state
    tools.setShippingGroupState(pShippingGroup, sgs.getStateValue(ShippingGroupStates.REMOVED),
                                Constants.REMOVE_SHIPPING_GROUP_FINISHED,
                                pModificationList);
  }

  /**
   * This removes the quantity of the removed shipItemRel from the quantity of its CommerceItem
   *
   * @param pOrder The order containing both the ShipItemRel
   * @param pShipItemRel The ShippingGroupCommerceItemRelationship that was removed
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void shipItemRelWasRemoved(Order pOrder,
                                       ShippingGroupCommerceItemRelationship pShipItemRel)
  {
    CommerceItem ci = pShipItemRel.getCommerceItem();
    long quantity = 0;
    OrderFulfiller of = getOrderFulfiller();

    // find out how many items to allocate from inventory
    switch(pShipItemRel.getRelationshipType())
    {
    case RelationshipTypes.SHIPPINGQUANTITY:
      {

        quantity = pShipItemRel.getQuantity();
        break;
      }
    case RelationshipTypes.SHIPPINGQUANTITYREMAINING:
      {
        // calculate the quantity
        quantity = of.getOrderManager().getShippingGroupManager().getRemainingQuantityForShippingGroup(ci);
        break;
      }
    } //switch

    long itemQuantity = ci.getQuantity();
    long newQuantity = itemQuantity - quantity;
    if(newQuantity < 0) {
      if(of.isLoggingWarning())
        of.logWarning("Attempt to decrement item quantity to less than zero.  Setting to zero for item " + ci.getId());
      newQuantity = 0;
    }

    if(of.isLoggingDebug())
      of.logDebug("Updating quantity of " + ci.getId() + " to " + newQuantity);
    ci.setQuantity(newQuantity);
  }

  //-------------------------------------
  /**
   * This method is called if a request to remove an order failed
   * because one of the shipping groups could not be removed.  Usually
   * because that group had shipped.  It sets the Order to PENDING_MERCHANT_ACTION.
   *
   * @param pOrder The order that should not be removed.
   * @param pModificationList A place to store all modifications.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void cancelRemoveOrder(Order pOrder, List pModificationList)
  {
    OrderFulfiller of = getOrderFulfiller();
    OrderStates os = of.getOrderStates();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    List shippingGroups = pOrder.getShippingGroups();
    Iterator shipIter = shippingGroups.iterator();

    if(pOrder.getState() == os.getStateValue(OrderStates.PENDING_REMOVE)) {
      // set the order state
      tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
                          Constants.REMOVE_ORDER_CANCELLED,
                          pModificationList);
    } else {
      // do nothing.
      if(of.isLoggingDebug())
        of.logDebug(MessageFormat.format(Constants.ORDER_NOT_PENDING_REMOVE, pOrder.getId()));
    }
  }

  /**
   * This method is called if a PENDING_REMOVE order needs to be set
   * back to normal (ie it is not being removed anymore).  If the
   * shipping group is in a PENDING_REMOVE state, it sets the group to
   * PROCESSING, resets all the items in the shipping group to
   * INITIAL, and sends a ShippingGroupUpdate modification
   * notification to its fulfiller.  If the shipping group is not in a
   * PENDING_REMOVE state, nothing happens.  If the shipping group was
   * changed, return true.  If nothing happened, return false.
   *
   * @param pShippingGroup the shipping group we are changing
   * @param pModificationList A place to store our modifications
   * @return true if the shipping group state was changed.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected boolean cancelRemoveShippingGroup(ShippingGroup pShippingGroup,
                                           List pModificationList)
  {
    OrderFulfiller of = getOrderFulfiller();
    ShippingGroupStates sgs = of.getShippingGroupStates();
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    List itemRelationships = pShippingGroup.getCommerceItemRelationships();
    Iterator itemIter = itemRelationships.iterator();

    if(pShippingGroup.getState() == sgs.getStateValue(ShippingGroupStates.PENDING_REMOVE)) {
      while(itemIter.hasNext()) {
        ShippingGroupCommerceItemRelationship sgcir = (ShippingGroupCommerceItemRelationship) itemIter.next();

        tools.setItemRelationshipState(sgcir, sirs.getStateValue(ShipItemRelationshipStates.INITIAL),
                                       Constants.REMOVE_SHIPPING_GROUP_CANCELLED,
                                       pModificationList);

      }
      // set the shipping group state
      tools.setShippingGroupState(pShippingGroup, sgs.getStateValue(ShippingGroupStates.PROCESSING),
                                  Constants.REMOVE_SHIPPING_GROUP_FINISHED,
                                  pModificationList);
      return true;
    } else {
      // do nothing
      // this is not unexpected.
      return false;
    }
  }

  /**
   * Use the payment manager to credit each payment group in the
   * order.  Typically called if an order is removed.
   *
   * @param pOrder The order being credited
   * @param pModificationList A place to store all new modifications.
   * @return true if the order was successfully credited, false otherwise
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected boolean creditOrder(Order pOrder, List pModificationList)
  {
    OrderFulfiller of = getOrderFulfiller();
    PaymentGroupStates pgs = of.getPaymentGroupStates();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    PaymentManager pm = of.getPaymentManager();
    boolean success = true;

    if(of.isLoggingDebug())
      of.logDebug("Inside creditOrder");

    List paymentGroupList = pOrder.getPaymentGroups();
    if(paymentGroupList != null) {
      Iterator paymentGroups = paymentGroupList.iterator();

      while(paymentGroups.hasNext()) {
        PaymentGroup pg = (PaymentGroup) paymentGroups.next();
        if(pg.getState() == pgs.getStateValue(PaymentGroupStates.SETTLED)) {
          // otherwise we don't need to credit
          try {
            pm.credit(pOrder, pg);
            tools.setPaymentGroupState(pg, pgs.getStateValue(PaymentGroupStates.INITIAL),
                                       Constants.ORDER_WAS_CREDITED,
                                       pModificationList);
          } catch(CommerceException c) {
            // credit failed for some reason
            if(of.isLoggingError())
              of.logError(MessageFormat.format(Constants.PAYMENT_CREDIT_FAILED, pg.getId()));
            success = false;
          }
        }
        else if(pg.getPaymentGroupClassType().equals(tools.getGiftCertificateClassType())) {
          if(pg.getState() != pgs.getStateValue(PaymentGroupStates.REMOVED)) {
            try {
              pm.expireGiftCertificateAuthorization(pOrder, (GiftCertificate)pg, pg.getAmountAuthorized());
            }
            catch(CommerceException c) {
              // credit failed for some reason
              if(of.isLoggingError())
                of.logError(MessageFormat.format(Constants.PAYMENT_CREDIT_FAILED, pg.getId()));
              success = false;
            }
          }
        }
      }
    }
    return success;
  }
} // OrderFulfillerModificationHandler
