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
import atg.commerce.CommerceException;
import atg.commerce.fulfillment.*;
import atg.commerce.messaging.*;
import atg.commerce.order.Order;
import atg.commerce.order.InvalidParameterException;

import java.util.*;
import javax.jms.*;

/**
 * This processor will execute the chain to delegate the FulfillOrderFragment messages
 * to the fulfiller systems.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcExecuteFulfillOrderFragment.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcExecuteFulfillOrderFragment implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcExecuteFulfillOrderFragment.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcExecuteFulfillOrderFragment() {
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

  /**
   * The chain to run from within this processor
   **/
  private String mChainToRun;
  
  public String getChainToRun() {
      return mChainToRun;
  }

  public void setChainToRun(String pChainToRun) {
      mChainToRun = pChainToRun;
  }


  //-----------------------------------------------
  /**
   * This processor will execute the chain to delegate the FulfillOrderFragment messages
   * to the fulfillers responsible for different parts of the order (shipping groups)
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
    CommerceMessage message = (CommerceMessage) map.get(PipelineConstants.COMMERCEMESSAGE);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (pOrder == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (message == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
  
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    HashMap fulfillerMap = tools.getFulfillersForShippingGroups(pOrder.getShippingGroups());
    
    Iterator fulfillerIterator = fulfillerMap.keySet().iterator();
    String fulfiller = null, fulfillerPort = null;
    List shippingGroups = null, shippingGroupIds = null;
    PipelineResult result;
    
    while (fulfillerIterator.hasNext()) {
        fulfiller = (String) fulfillerIterator.next();
        fulfillerPort = tools.getFulfillerPort(fulfiller);
        
        if (of.isLoggingDebug())
            of.logDebug("Port for fulfiller: " + fulfiller + " is : " + fulfillerPort);
        
        // Send the message for the shipping groups for that fulfiller (from the map)
        shippingGroups = (List) fulfillerMap.get(fulfiller);
        shippingGroupIds = tools.extractShippingGroupIds(shippingGroups);
        // build the input Hashmap object for the chain
        HashMap pMap = new HashMap(7);
        pMap.put(PipelineConstants.ORDER, pOrder);
        pMap.put(PipelineConstants.ORDERFULFILLER, of);
        pMap.put(PipelineConstants.COMMERCEMESSAGE, message);
        pMap.put(PipelineConstants.FULFILLER, fulfiller);
        pMap.put(PipelineConstants.SHIPPINGGROUPIDS, shippingGroupIds);
        pMap.put(PipelineConstants.MODIFICATIONLIST, performedModifications);
        try {
            result = 
                of.getFulfillmentPipelineManager().runProcess(getChainToRun(), pMap);
        }
        catch(RunProcessException e) {
            Throwable t = e.getSourceException();
            if (t instanceof JMSException)
                throw (JMSException)t;
            else
                break;
        }
    }
    
    return SUCCESS;
  }
   
}
