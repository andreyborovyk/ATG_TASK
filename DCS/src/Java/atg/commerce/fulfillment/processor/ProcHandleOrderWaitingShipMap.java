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
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.commerce.CommerceException;
import atg.service.lockmanager.*;

import java.util.*;
import javax.jms.*;

/**
 * This processor handles the HashMap object that contains the orders with shipping
 * groups whose items could not be allocated from the repository in the former allocation
 * procedures. It will call to the chain to reprocess those shipping groups.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleOrderWaitingShipMap.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcHandleOrderWaitingShipMap implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleOrderWaitingShipMap.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcHandleOrderWaitingShipMap() {
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
   * This processor handles the HashMap object that contains the orders with shipping
   * groups whose items could not be allocated from the repository in the former allocation
   * procedures. It will call to the chain to reprocess those shipping groups.
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
    HashMap orderMap = (HashMap) map.get(PipelineConstants.ORDERWAITINGSHIPMAP);
    ObjectMessage message = (ObjectMessage) map.get(PipelineConstants.MESSAGE);
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    
    if (message == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
        
    if (orderMap == null)
        return STOP_CHAIN_EXECUTION;
    
    Iterator keys = orderMap.keySet().iterator();
    String currentOrderId = null;
    Iterator shipGroupIds = null;

    if(of.isLoggingDebug())
        of.logDebug("Query for waiting orders returned: ");
      
    while (keys.hasNext()) {
	currentOrderId = (String) keys.next();
        if(of.isLoggingDebug())
            of.logDebug(" - " + currentOrderId);

        shipGroupIds = ((Set) orderMap.get(currentOrderId)).iterator();
        String[] shipIds = new String[((Set)orderMap.get(currentOrderId)).size()];        
        int i=0;
	while (shipGroupIds.hasNext()) {
	  shipIds[i++] = (String) shipGroupIds.next();
        }
        
        HashMap pMap = new HashMap(5);
        pMap.put(PipelineConstants.ORDERFULFILLER, of);
        pMap.put(PipelineConstants.ORDERID, currentOrderId);
        pMap.put(PipelineConstants.SHIPPINGGROUPIDS, shipIds);
        pMap.put(PipelineConstants.MESSAGE, message);
        PipelineResult result =
            of.getFulfillmentPipelineManager().runProcess(getChainToRun(), pMap);
        if (result.hasErrors())
            return STOP_CHAIN_EXECUTION;
 
    } // while
    
    return SUCCESS;
  }
}
