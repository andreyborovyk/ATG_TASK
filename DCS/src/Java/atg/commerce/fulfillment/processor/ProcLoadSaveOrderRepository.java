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
 * repository, it will be saved (a new order arrived). After the order is loaded, 
 * it's added to the Hashmap object that is then passed to other processors.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcLoadSaveOrderRepository.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcLoadSaveOrderRepository implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcLoadSaveOrderRepository.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcLoadSaveOrderRepository() {
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
   * Loads the order from the repository and saves the order if it does not exist
   * (a new order)

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
    FulfillerSystem of = (FulfillerSystem) map.get(PipelineConstants.ORDERFULFILLER);

    if (pMessage == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidCommerceMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    CommerceMessage cMessage = (CommerceMessage) pMessage.getObject();
    
    // should never happen, checking is done in a fulfiller
    if (cMessage == null) {
        if (of.isLoggingError())
            of.logError(Constants.COMMERCE_MESSAGE_NULL);
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidCommerceMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    }

    Order pOrder = null;

    if (cMessage instanceof SubmitOrder)
        pOrder = ((SubmitOrder)cMessage).getOrder();
    else if(cMessage instanceof FulfillOrderFragment) {
        pOrder = ((FulfillOrderFragment)cMessage).getOrder();
        String[] pShippingGroupIds = ((FulfillOrderFragment)cMessage).getShippingGroupIds();
        map.put(PipelineConstants.SHIPPINGGROUPIDS, pShippingGroupIds);
    }

    // should never happen, checking is done in a fulfiller
    if (pOrder == null) {
      if (of.isLoggingError())        
	of.logError(Constants.ORDER_IS_NULL);
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                       MY_RESOURCE_NAME, sResourceBundle));
    }

    if (of.isLoggingDebug()) {
        of.logDebug("In LoadSaveOrder: ");
        tools.printOrder(pOrder);
    }

    // try to load the order.  If nothing returned, that means that this fulfiller
    // is using a different repository, in which case we should save instead
    Order retrievedOrder = null;
    try {
       if(of.getOrderManager().orderExists(pOrder.getId())) {
          if(of.isLoggingDebug())
             of.logDebug("Loading newly arrived order instead of saving.");
          retrievedOrder = of.getOrderManager().loadOrder(pOrder.getId());
        }
        else {
          if(of.isLoggingDebug())
             of.logDebug("Saving newly arrived order since it does not exist.");
        }
      } 
    catch(CommerceException c) {
        if(of.isLoggingError())
           of.logError(MessageFormat.format(Constants.ORDER_LOAD_FAILED, pOrder.getId()), c);
        return STOP_CHAIN_EXECUTION;
    }
    
    if(retrievedOrder == null) {
        // if the order is not in the repository, add it
        of.getOrderManager().addOrder(pOrder);
    }
    else
        pOrder = retrievedOrder;
    
    // add order to the Hashmap object
    map.put(PipelineConstants.ORDER, pOrder);
    
    List performedModifications = new ArrayList();
    // add the list to the Hashmap
    map.put(PipelineConstants.MODIFICATIONLIST, performedModifications);
    map.put(PipelineConstants.COMMERCEMESSAGE, cMessage);

    return SUCCESS;
  }

}
