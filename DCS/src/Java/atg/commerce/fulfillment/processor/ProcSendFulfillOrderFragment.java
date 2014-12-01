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
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import javax.jms.*;
import java.text.*;

import java.util.*;

/**
 * This processor sends the FulfillOrderFragment messages to the fulfillers.
 * The messages contain the shipping groups the fulfillers are responsible for.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcSendFulfillOrderFragment.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcSendFulfillOrderFragment implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcSendFulfillOrderFragment.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcSendFulfillOrderFragment() {
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
   * Send the FulfillOrderFragment messages with the shipping groups to be
   * processed to the fulfillers by calling the sendOrderToFulfiller method.
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
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
    OrderFulfiller of = (OrderFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    CommerceMessage cMessage = (CommerceMessage) map.get(PipelineConstants.COMMERCEMESSAGE);
    String fulfiller = (String) map.get(PipelineConstants.FULFILLER);
    List shippingGroupIds = (List) map.get(PipelineConstants.SHIPPINGGROUPIDS);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (pOrder == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (cMessage == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
  
    SubmitOrder pSubmitOrder = (SubmitOrder) cMessage;
    
    OrderStates os = of.getOrderStates();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    String msg = Constants.ORDER_STATE_PROCESSING;
    
    // Set the Order's state to processing if it isn't set already.
    if (pOrder.getState() != os.getStateValue(OrderStates.PROCESSING))
        tools.setOrderState(pOrder, os.getStateValue(OrderStates.PROCESSING),
                            MessageFormat.format(msg, pOrder.getId()),
                            performedModifications);
    
    // Set the shipping group states to processing and modify their state detail to indicate
    // the processing hsa begun.
    setShippingGroupStateProcessing(of, pOrder, shippingGroupIds, performedModifications);
    
    FulfillOrderFragment fof = new FulfillOrderFragment();
    
    fof.setOrder(pOrder);
    fof.setShippingGroupIds((String[])
			    shippingGroupIds.toArray(new String[shippingGroupIds.size()]));
    fof.setSource(of.getMessageSourceName());
    fof.setId(tools.getNextMessageId());
    
    fof.setOriginalSource(pSubmitOrder.getOriginalSource());
    fof.setOriginalId(pSubmitOrder.getOriginalId());
    fof.setOriginalUserId(pSubmitOrder.getOriginalUserId());

    String portName=tools.getFulfillerPort(fulfiller);
    
    of.sendCommerceMessage(fof, portName);
    
    return SUCCESS;
  }
   

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
   **/
  protected void setShippingGroupStateProcessing(OrderFulfiller of, Order pOrder,
						 List pListShipGroupIds,
						 List pModificationList) {
    if(of.isLoggingDebug())
      of.logDebug("Inside setShippingGroupStateProcessing");

    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();

    ShippingGroup sg = null;
    ShippingGroupStates sgs = of.getShippingGroupStates();

    Iterator shipIdIterator = pListShipGroupIds.iterator();

    while (shipIdIterator.hasNext()) {
      try {
	sg = pOrder.getShippingGroup((String) shipIdIterator.next());
        String msg = Constants.SHIP_GROUP_STATE_PROCESSING;
	tools.setShippingGroupState(sg, sgs.getStateValue(ShippingGroupStates.PROCESSING),
                                    MessageFormat.format(msg, sg.getShippingGroupClassType()),
                                    pModificationList);
        setShippingGroupSubmittedDate(of, sg, pModificationList);
      }
      catch (ShippingGroupNotFoundException e) {
	// This should never happen since we got the ids from the order itself.
        if(of.isLoggingError())
          of.logError(e.getMessage());
      }
      catch (InvalidParameterException r) {
	// This should never happen since we got the ids from the order itself.
        if(of.isLoggingError())
          of.logError(r);
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
   **/
  protected void setShippingGroupSubmittedDate(OrderFulfiller of, 
                                               ShippingGroup pShippingGroup, 
                                               List pModificationList)
  {
    Date oldTime = pShippingGroup.getSubmittedDate();
    Date newTime = new Date();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();

    pShippingGroup.setSubmittedDate(newTime);
    Modification m = tools.createShipUpdateModification("submittedDate",
                                                        oldTime, newTime,
                                                        pShippingGroup.getId());
    pModificationList.add(m);
  }

}
