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
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import javax.jms.*;
import java.util.*;

/**
 * This processor allocates the hardgood shipping group by calling the corresponding chain
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcAllocateShippingGroup.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcAllocateShippingGroup implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcAllocateShippingGroup.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private static final int SUCCESS = 1;
  //-----------------------------------------------
  public ProcAllocateShippingGroup() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * 2 - The processor failed
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
   * Allocates the hargood shipping group by calling the corresponding pipeline chain
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
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    ShippingGroup pShippingGroup = (ShippingGroup) map.get(PipelineConstants.SHIPPINGGROUP);
    List performedModifications = (List) map.get(PipelineConstants.MODIFICATIONLIST);

    if (pOrder == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    ShippingGroupStates sgs = of.getShippingGroupStates();

    try {
        HashMap pMap = new HashMap(4);
        pMap.put(PipelineConstants.ORDER, pOrder);
        pMap.put(PipelineConstants.ORDERFULFILLER, of);
        pMap.put(PipelineConstants.SHIPPINGGROUP, pShippingGroup);
        pMap.put(PipelineConstants.MODIFICATIONLIST, performedModifications);
        PipelineResult result = 
            of.getFulfillmentPipelineManager().runProcess(getChainToRun(), pMap);

        if (result.hasErrors())
	  return SUCCESS; // some items may be available.
        
        if(of.isLoggingDebug())
            of.logDebug("Shipping group " + pShippingGroup.getId() + " " + 
                        sgs.getStateString(sgs.getStateValue(ShippingGroupStates.PENDING_SHIPMENT)));
        // if allocateShippingGroup returns true, then this group is shippable
        of.getOrderFulfillmentTools().setShippingGroupState(pShippingGroup,
                                                            sgs.getStateValue(ShippingGroupStates.PENDING_SHIPMENT),
                                                            null, performedModifications);
      }
      catch(RunProcessException e) {
          return STOP_CHAIN_EXECUTION;
      }
    return STOP_CHAIN_EXECUTION;
  }

}
