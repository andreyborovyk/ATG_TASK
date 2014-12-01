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
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import atg.commerce.CommerceException;
import atg.commerce.messaging.CommerceMessage;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.payment.PaymentException;
import atg.commerce.payment.PaymentManager;
import atg.commerce.states.OrderStates;
import atg.commerce.states.PaymentGroupStates;
import atg.commerce.states.ShippingGroupStates;
import atg.payment.PaymentStatus;
import atg.service.pipeline.PipelineResult;
import atg.service.pipeline.RunProcessException;

/**
 * <p> This class handles the start of the fulfillment process and is
 * responsible for the routing of various requests made to the
 * fulfillment subsystem.  In a sense, the OrderFulfiller is the hub
 * of communication relating to fulfillment. The order to be fulfilled
 * is received through a SubmitOrder message and is handled by the
 * handleSubmitOrder method .  The OrderFulfiller is responsible for
 * farming out FulfillOrderFragment messages to the various fulfillers
 * interested in pieces of the order. </p>
 *
 * <p> Different fulfillers such as HardgoodFulfiller receive the
 * FulfillOrderFragment message and begin processing of the shipping
 * groups specified within. </p>
 * 
 * <p> Additionally the OrderFulfiller receives
 * ModifyOrderNotification and ModifyOrder messages.  These messages
 * are handled by the OrderFulfillerModificationHandler which is then
 * responsible for making the changes requested or forwarding them to
 * the fulfillers who currently own the responsibility for the areas
 * in which the modifications are to be made. </p>
 *
 * <p>Once the FulfillOrderFragment messages are sent, the OrderFulfiller
 * loses control of the shipping groups and of the
 * ShippingGroupCommerceItemRelationships.  Control of these objects is regained 
 * once the shipping group ships or an error occurs. (i.e. the state of the shipping group
 * is set to NO_PENDING_ACTION or PENDING_MERHANT_ACTION)</p>
 *
 * <p>The order is settled (paid for) according to the value of the settleOnFirstShipment
 * property.  If this property is true, the order is settled after the first shipping group
 * in the order ships.  If this property is false, the order is settled after the last shipping
 * group in the order ships.</p>
 *
 * @see SubmitOrder
 * @see #handleSubmitOrder
 * @see FulfillOrderFragment
 * @see HardgoodFulfiller
 * @see ModifyOrder
 * @see ModifyOrderNotification
 * @see OrderFulfillerModificationHandler
 * @see #isSettleOnFirstShipment
 *
 * @author Tareef Kawaf 
 * @beaninfo 
 *          description: This class handles the start of the fulfillment process 
 *                       and is responsible for the routing of various
 *                       requests made to the fulfillment subsystem.  In a sense, the
 *                       OrderFulfiller is the hub of communication relating to fulfillment.
 *          displayname: OrderFulfiller
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/OrderFulfiller.java#2 $$Change: 651448 $ 
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public class OrderFulfiller extends FulfillerSystem
{
  //-------------------------------------
  // Class version string

  public static final String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/OrderFulfiller.java#2 $$Change: 651448 $";
    
  //---------------------------------------------------------------------------
  // property: SettleOnFirstShipment
  //---------------------------------------------------------------------------
  boolean mSettleOnFirstShipment=false;

  public void setSettleOnFirstShipment(boolean pSettleOnFirstShipment) {
    mSettleOnFirstShipment = pSettleOnFirstShipment;
  }

  /**
   * This property determines whether the order should be settled on the first shipment of a
   * shipping group in the order or after the last shipment of the shipping groups. 
   *
   * @see OrderFulfillmentTools#isOrderSettleable
   * @beaninfo 
   *       description: This property determines whether the order should be 
   *                    settled on the first shipment of a  shipping group 
   *                    in the order or the last shipment of the shipping groups. 
   **/
  public boolean isSettleOnFirstShipment() {
    return mSettleOnFirstShipment;
  }


  //---------------------------------------------------------------------------
  // property:AllowRemoveOrderWithPendingShipment
  //---------------------------------------------------------------------------

  private boolean mAllowRemoveOrderWithPendingShipment = true;
  public void setAllowRemoveOrderWithPendingShipment(boolean pAllowRemoveOrderWithPendingShipment) {
    mAllowRemoveOrderWithPendingShipment = pAllowRemoveOrderWithPendingShipment;
  }

  /**
   * A ModifyOrder request, requesting that an order be removed should
   * be processed even if there exists a shipping group that is in a
   * PENDING_SHIPMENT state.  (Keep in mind that the request is still
   * denied if there is a shipping group in a NO_PENDING_ACTION
   * state.)
   * defaults to true
   **/
  public boolean isAllowRemoveOrderWithPendingShipment() {
    return mAllowRemoveOrderWithPendingShipment;
  }

  
  //---------------------------------------------------------------------------
  // property:ModificationHandler
  //---------------------------------------------------------------------------
  private ModificationHandler mModificationHandler;

  public void setModificationHandler(ModificationHandler pModificationHandler) {
    mModificationHandler = pModificationHandler;
  }

  /**
   * The ModificationHandler that will be used to deal with all ModifyOrder 
   * and ModifyOrderNotification messages.  It has two methods that are used by
   * OrderFulfiller: handleModifyOrder and handleModifyOrderNotification.
   *
   * @beaninfo 
   *          description: The ModificationHandler processes all ModifyOrder and 
   *                       ModifyOrderNotification messages.
   * @see ModifyOrder
   * @see ModifyOrderNotification
   * @see OrderFulfillerModificationHandler
   **/
  public ModificationHandler getModificationHandler() {
    return mModificationHandler;
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
   * <p> The OrderFulfiller handles the following types of messages: SubmitOrder,
   * ModifyOrder and ModifyOrderNotification.  Handling messages of different types can be
   * done by extending the handleNewMessageType method and adding whatever logic is required in
   * there. </p>
   *
   * @beaninfo
   *          description: This method is called when a new message has arrived
   *                       for the OrderFulfiller.
   * @param pPortName - the port on which this message was received
   * @param pMessage - the message that was sent on the port
   * @exception javax.jms.JMSException
   * @see SubmitOrder
   * @see ModifyOrder
   * @see ModifyOrderNotification
   * @see FulfillerSystem#receive
   * @see FulfillerSystem#handleNewMessageType
   **/
  protected boolean handleMessage (String pPortName, ObjectMessage pMessage)
    throws JMSException
  {
    String messageType = pMessage.getJMSType();
    boolean handled = true;

    if (messageType.equals(SubmitOrder.TYPE)) {
      handleSubmitOrder(pPortName, pMessage);
    }
    else if (messageType.equals(ModifyOrder.TYPE)) {
      getModificationHandler().handleModifyOrder(pPortName, pMessage);
    }
    else if (messageType.equals(ModifyOrderNotification.TYPE)) {
      getModificationHandler().handleModifyOrderNotification(pPortName, pMessage);
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
   **/
  public Serializable getKeyForMessage(ObjectMessage oMessage)
    throws JMSException
  {

    String messageType = oMessage.getJMSType();
    CommerceMessage cMessage = (CommerceMessage) oMessage.getObject();
    
    if (messageType.equals(SubmitOrder.TYPE)) {
        return getOrderIdFromMessage((SubmitOrder)cMessage);
    }
    else if (messageType.equals(ModifyOrder.TYPE)) {
        return ((ModifyOrder) cMessage).getOrderId();
    }
    else if (messageType.equals(ModifyOrderNotification.TYPE)) {
        return ((ModifyOrderNotification) cMessage).getOrderId();
    }
    return null;
  }
  
    
  /**
   * This is called only for the submitOrder messages
   * @return an Object which serves as the key for the message
   **/
  protected Serializable getOrderIdFromMessage(SubmitOrder cMessage) {
      // see if we get it from the message itself
      if (getLookUpOrderIdFromMessage()) {
          if(isLoggingDebug())
              logDebug("Reading the orderId from the SubmitOrder message");
          String id = cMessage.getOrderId();
          if (id == null) {
              if (getLookUpOrderIdFromOrder()) {
                  if(isLoggingDebug())
                      logDebug("Reading the orderId from the order object sent in the " + 
                               "SubmitOrder message");
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
                           "SubmitOrder message");
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
   * SubmitOrder. It will call the pipeline chain to process the message.
   * Should developers wish to change the behavior of the OrderFulfiller 
   * class in handling a SubmitOrder message this method should be overridden 
   * </p>
   *
   * <p> The order is verified using verifyOrderFulfillment to make
   * sure it is in a state where fulfillment is possble.  If it isn't
   * an error is logged and no processing is done to that order.  Next
   * a list of shipping groups that need to be split are determined
   * using the retrieveShippingGroupsToBeSplit method.  If the method
   * returns any shipping groups that need to be split, the
   * splitShippingGroups method is called with the order and the
   * returned shipping groups, followed by a repricing of the
   * order. </p>
   *
   * <p> The chain then determines which fulfillers need to get which
   * shipping groups and uses the
   * OrderFulfillmentTools.getFulfillersForShippingGroups to return
   * the HashMap which contains this information about the various
   * fulfillers and the shipping groups that those fulfillers should
   * be receiving.  The OrderFulfillmentTools.getFulfillerPort will
   * return the port on which the message is sent for each fulfiller,
   * and sendOrderToFulfiller.  </p>
   *
   * <p> The order is written back to the repository using updateOrder </p>
   *
   * @beaninfo
   *          description: This method is called to process a newly arrived SubmitOrder message.
   * @param pPortName - the port on which this message is received
   * @param pMessage - the object message which should contain a SubmitOrder message
   * @exception javax.jms.JMSException is thrown on a failure to send a message
   * @see SubmitOrder
   * @see #verifyOrderFulfillment
   * @see #retrieveShippingGroupsToBeSplit
   * @see OrderFulfillmentTools#getFulfillersForShippingGroups
   * @see OrderFulfillmentTools#getFulfillerPort
   * @see #sendOrderToFulfiller
   * @see #updateOrder
   **/
  public void handleSubmitOrder(String pPortName,
				ObjectMessage pMessage) throws JMSException
  {
      if (isLoggingDebug())
          logDebug("Handling a SubmitOrder message");

      // the input params to the chain
      HashMap map = new HashMap(10);
      map.put(PipelineConstants.MESSAGE, pMessage);
      map.put(PipelineConstants.ORDERFULFILLER, this);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("submitOrderChain");
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


  //-------------------------------------
  /**
   * <p> This method verifies that the order to be fulfilled is in a state which allows it to be
   * fulfilled.  If the order is in an incomplete state or a no pending action state then an
   * exception is thrown. </p>
   *
   * @beaninfo
   *          description: This method verifies that a given order is in a state that 
   *                       allows it to be fulfilled.
   * @param pOrder - the order to be verified.
   * @exception atg.commerce.IllegalOrderStateException if the order is in an illegal state
   **/
  public void verifyOrderFulfillment(Order pOrder) throws IllegalOrderStateException
  {
      getOrderFulfillmentTools().verifyOrderFulfillment(pOrder);
  }

    
  //-------------------------------------
  /**
   * <p> This method will set the states of the shipping groups to processing, send a
   * ModifyOrderNotification using OrderFulfillmentTools.sendModifyOrderNotification and
   * will send a FulfillOrderFragment for the port of the fulfiller name passed in. This
   * method uses the OrderFulfillmentTools.getFulfillerPort to determine which port to
   * send the message out on.</p>
   *
   * @beaninfo
   *          description: This method forwards the relevant pieces of the order to 
   *                       each of the interested fulfillers.  It also updates the order state.
   * @param pOrder - the order to be fulfilled
   * @param pFulfiller - the fulfiller name
   * @param pShippingGroupIds - the ids of the shipping groups to be serviced by the fulfiller
   * @param pSubmitOrder - the original message that this is a fragment of
   * @exception javax.jms.JMSException is thrown on failure to send the message to the fulfiller.
   * @see ModifyOrderNotification
   * @see OrderFulfillmentTools#sendModifyOrderNotification
   * @see FulfillOrderFragment
   * @see OrderFulfillmentTools#getFulfillerPort
   **/
  public void sendOrderToFulfiller(Order pOrder, String pFulfiller,
				   List pShippingGroupIds, SubmitOrder pSubmitOrder,
                                   List pModificationList)
    throws CommerceException, JMSException
  {

      // the input params to the chain
      HashMap map = new HashMap(7);
      map.put(PipelineConstants.ORDER, pOrder);
      map.put(PipelineConstants.COMMERCEMESSAGE, pSubmitOrder);
      map.put(PipelineConstants.FULFILLER, pFulfiller);
      map.put(PipelineConstants.SHIPPINGGROUPIDS, pShippingGroupIds);
      map.put(PipelineConstants.MODIFICATIONLIST, pModificationList);
      map.put(PipelineConstants.ORDERFULFILLER, this);
      
      try {
          String chainToRun = (String)getChainToRunMap().get("sendFulfillOrderFragmentChain");
          // execute the submitOrder pipeline chain
          PipelineResult results = getFulfillmentPipelineManager().runProcess(chainToRun, map);
      }
      catch (RunProcessException e) {
          Throwable p = e.getSourceException();
          if(isLoggingError())
              logError(p);

          if (p instanceof JMSException)
              throw (JMSException)p;
          if (p instanceof CommerceException)
              throw (CommerceException)p;
      }

  }

  //-------------------------------------
  /**
   * Set the shipping group states to processing and add the
   * Modification objects created to the ModificationList that is
   * passed.  This method also sets the submittedDate of the order.The
   * caller of this method is responsible for packaging the changes in
   * a ModifyOrderNotification object and sending it out. This method
   * can be overriden if the state detail information should be
   * changed to something else or if any other work needs to be done
   * when the shippin group state change occurs.
   *
   * @beaninfo
   *          description: Set the state of the shipping group to PROCESSING.
   * @param pOrder the order to be operated on.
   * @param pShipGroupIds the ids of the shipping groups whose state will change
   * @param pModificationList the list to which the modification objects created will be added.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void setShippingGroupStateProcessing(Order pOrder,
						 List pListShipGroupIds,
						 List pModificationList) {
    if(isLoggingDebug())
      logDebug("Inside setShippingGroupStateProcessing");

    OrderFulfillmentTools oft = getOrderFulfillmentTools();

    ShippingGroup sg=null;
    ShippingGroupStates sgs = getShippingGroupStates();

    Iterator shipIdIterator = pListShipGroupIds.iterator();

    while (shipIdIterator.hasNext()) {
      try {
	sg = pOrder.getShippingGroup((String) shipIdIterator.next());
        String msg = Constants.SHIP_GROUP_STATE_PROCESSING;
	oft.setShippingGroupState(sg,
				  sgs.getStateValue(ShippingGroupStates.PROCESSING),
				  MessageFormat.format(msg, sg.getShippingGroupClassType()),
                                  pModificationList);
        setShippingGroupSubmittedDate(sg, pModificationList);
      }
      catch (ShippingGroupNotFoundException e) {
	// This should never happen since we got the ids from the order itself.
        if(isLoggingError())
          logError(e.getMessage());
      }
      catch (InvalidParameterException r) {
	// This should never happen since we got the ids from the order itself.
        if(isLoggingError())
          logError(r);
      }
      
    }
    
  }

  /**
   * This method sets the submitted date of the shipping group to the
   * current time.  To ensure this only happens once, it checks first
   * to make sure it is not currently set.
   *
   * @beaninfo
   *          description: Set the submittedDate of the given shipping group to the current time.
   * @param pShippingGroup The shipping group to set.
   * @param pModificationList Place to store new modifications.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void setShippingGroupSubmittedDate(ShippingGroup pShippingGroup, List pModificationList)
  {
    java.util.Date oldTime = pShippingGroup.getSubmittedDate();
    java.util.Date newTime = new java.util.Date();

    pShippingGroup.setSubmittedDate(newTime);
    Modification m = getOrderFulfillmentTools().createShipUpdateModification("submittedDate",
                                                                             oldTime, newTime,
                                                                             pShippingGroup.getId());
    pModificationList.add(m);
  }

  //-------------------------------------
  /**
   * This method will take an order and return a List of the shipping groups that need to be
   * split up into multiple shipping groups.  The default implementation will flag shipping
   * groups to be split if a shipping groups contains items that are fulfilled by multiple
   * fulfillers.  <BR>
   *
   * The default implementation uses OrderFulfillmentTools.isShippingGroupSingleFulfiller
   * to determine if all the items are fulfilled by one fulfiller.
   *
   *
   * @beaninfo
   *          description: Finds the shipping groups in a given order that need
   *                       to be split because the items within the group are
   *                       fulfilled by more than one fulfiller.
   * @param pOrder the order that contains the shipping groups
   * @return a List of the shipping groups that should be split.
   * @see OrderFulfillmentTools#isShippingGroupSingleFulfiller
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected List retrieveShippingGroupsToBeSplit(Order pOrder) {
    List returnList = null;

    Iterator shippingIterator = pOrder.getShippingGroups().iterator();
    
    ShippingGroup shipGroupToBeTested=null;

    try {
      while (shippingIterator.hasNext()) {
	shipGroupToBeTested = (ShippingGroup) shippingIterator.next();
	if (!getOrderFulfillmentTools().isShippingGroupSingleFulfiller(shipGroupToBeTested)) {
	  if (returnList == null) {
	    returnList = new ArrayList(3);
	  }
	  returnList.add(shipGroupToBeTested);
	}
      }
      return returnList;
    }
    catch (CommerceException e) {
      if(isLoggingError())
        logError(e);
    }

    return null;
  }

  //-------------------------------------
  /**
   * This method will split the shipping groups specified by whatever business logic is
   * required.  The default implementation will be splitting the shipping groups according to
   * fulfillers and will use the OrderFulfillmentTools.splitShippingGroupsByFulfiller.
   * Should the implementor wish to choose a different algorithm for how shipping groups are
   * split, this method should be overriden.
   *
   * @beaninfo
   *          description: Split each of the shipping groups provided according to the fulfiller.
   * @param pOrder the order which contains the shipping groups to be split
   * @param pShippingGroupsToBeSplit the List of @see ShippingGroup objects that need to be split
   * @return a List of the shipping groups that have been newly created.
   * @see OrderFulfillmentTools#splitShippingGroupsByFulfiller
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected List splitShippingGroups(Order pOrder,
				     List pShippingGroupsToBeSplit,
                                     List pModificationList)
    throws CommerceException
  {
    
    return getOrderFulfillmentTools().
      splitShippingGroupsByFulfiller(pOrder, pShippingGroupsToBeSplit, pModificationList);
  }


  //-------------------------------------
  /**
   * This method checks if it is okay to begin settlement and (if so)
   * settles the order and changes the states of each payment group
   * that is changed.
   *
   * @beaninfo
   *          description: Settle the order, if it is ok to do so.
   * @param pOrder The order to settle
   * @param pModificationList place to store all new modifications
   * @return true if the order was settled, false otherwise
   * @see OrderFulfillmentTools#isOrderSettleable
   * @see atg.payment.PaymentManager
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void settleOrder(Order pOrder, List pModificationList) throws PaymentException
  {
    PaymentManager pm = getPaymentManager();
    OrderFulfillmentTools tools = getOrderFulfillmentTools();
    PaymentGroupStates pgs = getPaymentGroupStates();
    OrderStates os = getOrderStates();
    
    if (isLoggingDebug())
      logDebug("Inside settleOrder: " + pOrder.getId());

    if (tools.isOrderSettleable(pOrder, isSettleOnFirstShipment())) {
      try {
        List paymentGroupList = pOrder.getPaymentGroups();
        Iterator paymentGroups = paymentGroupList.iterator();
        while(paymentGroups.hasNext()) {
          PaymentGroup pg = (PaymentGroup) paymentGroups.next();
          try {
            pm.debit(pOrder, pg);
            // check for success (last debit status)
            List debitStatuses = pg.getDebitStatus();

            PaymentStatus debitStatus = null;
            String msg = null;
            if((debitStatuses != null) && (debitStatuses.size() > 0))
              debitStatus = (PaymentStatus) debitStatuses.get(debitStatuses.size() - 1);
            else {
              if(isLoggingError())
                logError(Constants.NO_DEBIT_STATUS);
              msg = Constants.NO_DEBIT_STATUS;
            }
            if(debitStatus != null && !debitStatus.getTransactionSuccess()) {
              msg = debitStatus.getErrorMessage();
            }
            if(msg != null) {
              tools.setPaymentGroupState(pg, pgs.getStateValue(PaymentGroupStates.SETTLE_FAILED),
                                         msg,
                                         pModificationList);
              // set the order to pending merchant action
              tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
                                  msg,
                                  pModificationList);
            }
            else 
              tools.setPaymentGroupState(pg, pgs.getStateValue(PaymentGroupStates.SETTLED),
                                         Constants.PAYMENT_SETTLED,
                                         pModificationList);

          } catch(PaymentException p) {
            if(isLoggingError())
              logError(p);
            tools.setPaymentGroupState(pg, pgs.getStateValue(PaymentGroupStates.SETTLE_FAILED),
                                       Constants.PAYMENT_SETTLE_FAILED,
                                       pModificationList);
            // set the order to pending merchant action
            tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
                                Constants.PAYMENT_SETTLE_FAILED,
                                pModificationList);
          }
        }
      } catch(CommerceException e) {
        if(isLoggingError())
          logError(Constants.PAYMENT_SETTLE_FAILED, e);
      }
    } 
  }

  //-------------------------------------
  /**
   * This method performs all processing that must be done once all
   * the elements in an order are finished.  This includes setting the
   * orders state to NO_PENDING_ACTION and setting the completed date
   * property. It does no verification that the order is in a state
   *  that allows it to be finished, that is done in isOrderFinished.
   *
   * @beaninfo
   *          description: Finish the order by updating the state to NO_PENDING_ACTION.
   * @param pOrder The order to finish.
   * @param pModificationList A place to store all modifications made
   * @see atg.commerce.states.OrderStates#NO_PENDING_ACTION
   * @see OrderFulfillmentTools#isOrderFinished
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void finishOrder(Order pOrder, List pModificationList)
  {
    if (isLoggingDebug()) 
      logDebug("Inside finishOrder: " + pOrder.getId());

    OrderFulfillmentTools tools = getOrderFulfillmentTools();
    OrderStates os = getOrderStates();

    if(pOrder.getState() == os.getStateValue(OrderStates.NO_PENDING_ACTION)) {
      if(isLoggingDebug())
        logDebug("The order " + pOrder.getId() + " has already finished.");
      return;
    }
    String msg = Constants.ORDER_IS_DONE;
    tools.setOrderState(pOrder, os.getStateValue(OrderStates.NO_PENDING_ACTION), 
                        MessageFormat.format(msg, pOrder.getId()),
                        pModificationList);
    

    Long oldTime = Long.valueOf(pOrder.getCompletedTime());
    long completedTime = System.currentTimeMillis();
    Long completedTimeLong = Long.valueOf(completedTime);

    pOrder.setCompletedTime(completedTime);
    Modification m = tools.createOrderUpdateModification("completedTime",
                                                         oldTime, completedTimeLong,
                                                         pOrder.getId());

    pModificationList.add(m);
    
  }

  //-------------------------------------
  /**
   * This method will return the Order from the order repository with the given id.  In the
   * standard implementation this will call the OrderManager and load the order.
   *
   * @beaninfo
   *          description: Load the order using the OrderManager.
   * @param pId the id of the order to be retrieved
   * @return the order corresponding to the id passed in
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected Order loadOrder(String pId)
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
   * This method will save the order using the order manager.
   *
   * @beaninfo
   *          description: Save the order using the order manager
   * @param pOrder - the order to be saved
   * @exception atg.commerce.CommerceException is thrown if the order fails to save for any reason.
   *
   * @deprecated Replaced by the pipeline processor
   **/
  protected void updateOrder(Order pOrder)
    throws CommerceException
  {
    if (isLoggingDebug()) {
      logDebug("Inside updateOrder");
      getOrderFulfillmentTools().printOrder(pOrder);
    }    
    getOrderManager().updateOrder(pOrder);
  }  
} // end of class
