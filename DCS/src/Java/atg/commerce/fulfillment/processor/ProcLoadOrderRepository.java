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
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor loads the order by the id. If the order doesn't exist in the
 * repository, return. After the order is loaded, it's added to the Hashmap object 
 * that is then passed to other processors.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcLoadOrderRepository.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcLoadOrderRepository implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcLoadOrderRepository.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcLoadOrderRepository() {
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
   * Loads the order from the repository.
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
    ObjectMessage pMessage = (ObjectMessage) map.get(PipelineConstants.MESSAGE);
    CommerceMessage cMessage = (CommerceMessage) map.get(PipelineConstants.COMMERCEMESSAGE);
    String orderId = (String) map.get(PipelineConstants.ORDERID);
    FulfillerSystem of = (FulfillerSystem) map.get(PipelineConstants.ORDERFULFILLER);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));

    // if the orderId is still not set, exit
    if (orderId == null) {
        if(of.isLoggingError())
            of.logError(Constants.ORDER_ID_IS_NULL);
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderIdParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    }

    Order order = null;

    // if the order is not found, don't rollback or we'll 
    // just get the message again
    if (of.isLoggingDebug())
        of.logDebug("Order id: " + orderId);
    
    try {
        // if orderId does not exist, message can't be processed
        if(of.getOrderManager().orderExists(orderId))
            order = of.getOrderManager().loadOrder(orderId);
        else {
            String msg = null;
            if (pMessage != null) {
                msg = Constants.BAD_ORDER_IN_MESSAGE;
                if(of.isLoggingError())
                    of.logError(MessageFormat.format(msg, pMessage.getJMSMessageID(), orderId));
            }
            else {
                msg = Constants.ORDER_DOES_NOT_EXIST;
                if(of.isLoggingError())
                    of.logError(MessageFormat.format(msg, orderId));
            }
            pResult.addError("OrderId", msg); 
            return STOP_CHAIN_EXECUTION;
        }
    }
    catch(CommerceException c) {
      // don't rollback
      if(of.isLoggingError())
          of.logError(MessageFormat.format(Constants.ORDER_LOAD_FAILED, orderId), c);
      pResult.addError("LoadOrder", c.toString());
      return STOP_CHAIN_EXECUTION;
    }
    
    //put the Order object to the Hashmap
    map.put(PipelineConstants.ORDER, order);

    // create the modification list and add it to the Hashmap object
    List performedModifications = new ArrayList();
    map.put(PipelineConstants.MODIFICATIONLIST, performedModifications);
    
    // handle fulfillOrderFragment properties
    if(cMessage instanceof FulfillOrderFragment) {
        String[] pShippingGroupIds = ((FulfillOrderFragment)cMessage).getShippingGroupIds();
        map.put(PipelineConstants.SHIPPINGGROUPIDS, pShippingGroupIds);
    }
    
    return SUCCESS;
  }
}
