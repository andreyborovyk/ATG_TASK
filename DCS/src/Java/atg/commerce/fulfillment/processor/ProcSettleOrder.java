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
import atg.payment.PaymentStatus;
import atg.commerce.payment.PaymentManager;
import atg.commerce.payment.PaymentException;
import atg.commerce.fulfillment.*;
import atg.commerce.messaging.*;
import atg.commerce.order.Order;
import atg.commerce.order.OrderManager;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor will settle the order by calling the PaymentManager for each PaymentGroup
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcSettleOrder.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcSettleOrder implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcSettleOrder.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcSettleOrder() {
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
   * Settles the order by calling the PaymentManager for all PaymentGroups
   *
   * This method requires that a JMS message and OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain a JMS message and OrderFulfiller object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    OrderFulfiller of = (OrderFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));

    if (pOrder == null) {
        if (of.isLoggingError())
            of.logError(Constants.ORDER_IS_NULL);
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    }
    
    PaymentManager pm = of.getPaymentManager();
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    PaymentGroupStates pgs = of.getPaymentGroupStates();
    OrderStates os = of.getOrderStates();
    
    if (of.isLoggingDebug())
        of.logDebug("Inside settleOrder: " + pOrder.getId());

    of.getOrderManager().removeEmptyShippingGroups(pOrder);

    if (!tools.isOrderSettleable(pOrder, of.isSettleOnFirstShipment()))
        return STOP_CHAIN_EXECUTION;

      List paymentGroupList = pOrder.getPaymentGroups();
      Iterator paymentGroups = paymentGroupList.iterator();
      while(paymentGroups.hasNext()) {
	PaymentGroup pg = (PaymentGroup) paymentGroups.next();
	try {
	  PipelineResult result = pm.debit(pOrder, pg);
	  
	  if (result != null && result.hasErrors()) {
            tools.setPaymentGroupState(pg, pgs.getStateValue(PaymentGroupStates.SETTLE_FAILED),
                                       Constants.PAYMENT_SETTLE_FAILED,
                                       performedModifications);
            // set the order to pending merchant action
            tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
                                Constants.PAYMENT_SETTLE_FAILED,
                                performedModifications);
	    
	    return STOP_CHAIN_EXECUTION;
	  }      
	      // check for success (last debit status)
	  List debitStatuses = pg.getDebitStatus();

	  PaymentStatus debitStatus = null;
	  String msg = null;

	  if((debitStatuses != null) && (debitStatuses.size() > 0))
	    debitStatus = (PaymentStatus) debitStatuses.get(debitStatuses.size() - 1);
	  else {
	    if(of.isLoggingError())
	      of.logError(Constants.NO_DEBIT_STATUS);
	    msg = Constants.NO_DEBIT_STATUS;
	  }
	  if(!debitStatus.getTransactionSuccess()) {
	    msg = debitStatus.getErrorMessage();
	  }
	  if(msg != null) {
	    tools.setPaymentGroupState(pg, pgs.getStateValue(PaymentGroupStates.SETTLE_FAILED),
				       msg, performedModifications);
	    // set the order to pending merchant action
	    tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
				msg, performedModifications);
	  }
	  else 
            tools.setPaymentGroupState(pg, pgs.getStateValue(PaymentGroupStates.SETTLED),
				       Constants.PAYMENT_SETTLED,
				       performedModifications);
	  } catch(PaymentException p) {
            if(of.isLoggingError())
               of.logError(p);
            tools.setPaymentGroupState(pg, pgs.getStateValue(PaymentGroupStates.SETTLE_FAILED),
                                       Constants.PAYMENT_SETTLE_FAILED,
                                       performedModifications);
            // set the order to pending merchant action
            tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
                                Constants.PAYMENT_SETTLE_FAILED,
                                performedModifications);
	    
	    return STOP_CHAIN_EXECUTION;
          } catch(CommerceException p) {
            if(of.isLoggingError())
               of.logError(p);
            tools.setPaymentGroupState(pg, pgs.getStateValue(PaymentGroupStates.SETTLE_FAILED),
                                       Constants.PAYMENT_SETTLE_FAILED,
                                       performedModifications);
            // set the order to pending merchant action
            tools.setOrderState(pOrder, os.getStateValue(OrderStates.PENDING_MERCHANT_ACTION),
                                Constants.PAYMENT_SETTLE_FAILED,
                                performedModifications);
	    
	    return STOP_CHAIN_EXECUTION;
          } 
        
      }
    
      return SUCCESS;
  }

}
    
