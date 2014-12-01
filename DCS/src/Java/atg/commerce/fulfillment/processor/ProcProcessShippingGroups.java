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
import atg.commerce.fulfillment.*;
import atg.commerce.order.Order;
import atg.commerce.order.ShippingGroup;
import atg.commerce.order.ChangedProperties;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.commerce.CommerceException;
import atg.commerce.order.ShippingGroupNotFoundException;

import javax.jms.*;
import java.util.*;

/**
 * This processor processes (fulfills) shipping groups for both hardgood and softgood fulfillers
 * It will call to the corresponding fulfillers' chains to actually process the groups.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcProcessShippingGroups.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcProcessShippingGroups implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcProcessShippingGroups.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcProcessShippingGroups() {
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
   * Calls to the chains to process shipping groups both for hardgood and softgood fulfillers.
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
    FulfillerSystem of = (FulfillerSystem) map.get(PipelineConstants.ORDERFULFILLER);
    String[] pShippingGroupIds = (String[]) map.get(PipelineConstants.SHIPPINGGROUPIDS);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (pOrder == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    if(of.isLoggingDebug())
       of.logDebug("Inside processMultipleShippingGroups");
    ShippingGroupStates sgs = of.getShippingGroupStates();

    for (int i = 0; i < pShippingGroupIds.length; i++) {
      ShippingGroup sg = null;
      try {
        sg = pOrder.getShippingGroup(pShippingGroupIds[i]);
      
      // if the processing failed, don't save the changes 
      // that might have been made for that shipping group
        HashMap pMap = new HashMap(5);
        pMap.put(PipelineConstants.ORDER, pOrder);
        pMap.put(PipelineConstants.ORDERFULFILLER, of);
        pMap.put(PipelineConstants.SHIPPINGGROUP, sg);
        pMap.put(PipelineConstants.MODIFICATIONLIST, performedModifications);

        PipelineResult result = 
            of.getFulfillmentPipelineManager().runProcess(getChainToRun(), pMap);
        if (result.hasErrors())
            pResult.copyInto(result);
      }
      catch(ShippingGroupNotFoundException sgnfe) {
        String[] msgArgs = { pShippingGroupIds[i] };
        if (of.isLoggingWarning())
	  of.logWarning(ResourceUtils.getMsgResource("ShippingGroupNotInContainer",
                                    MY_RESOURCE_NAME, sResourceBundle, msgArgs));

      }
      catch(RunProcessException e) {
          // ((ChangedProperties) sg).clearChangedProperties();
          of.getOrderFulfillmentTools().setShippingGroupState(sg, 
                                                              sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION),
                                                              e.toString(), performedModifications);
          pResult.addError("RunProcessException", e.toString());
      }
    } // for
    
    return SUCCESS;
  }

}
