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
import atg.commerce.payment.PaymentManager;
import atg.commerce.fulfillment.*;
import atg.commerce.messaging.*;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.ShippingGroupNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.GiftCertificate;
import atg.commerce.states.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor will credit the order
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcCreditOrder.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcCreditOrder implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcCreditOrder.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcCreditOrder() {
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
   * Credits the order by calling the PaymentManager for each PaymentGroup
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
    
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    PaymentGroupStates pgs = of.getPaymentGroupStates();
    PaymentManager pm = of.getPaymentManager();
    boolean success = true;

    if(!tools.areAllGroupsRemoved(pOrder))
        return STOP_CHAIN_EXECUTION;

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
                                       performedModifications);
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
    
    if (!success) {
        if(of.isLoggingError()) {
            of.logError(MessageFormat.format(Constants.ORDER_CREDIT_FAILED, pOrder.getId()));
            of.logError(MessageFormat.format(Constants.REMOVE_ORDER_FAILED, pOrder.getId()));
        }
        return STOP_CHAIN_EXECUTION;
    }
    
    return SUCCESS;
  }
}
