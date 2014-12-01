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

import java.util.*;
import java.text.*;

/**
 * This processor verifies if the shipping group is in a state that allows it to be removed
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcVerifyShippingGroupForRemoval.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcVerifyShippingGroupForRemoval implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcVerifyShippingGroupForRemoval.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcVerifyShippingGroupForRemoval() {
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
   * Verifies if the shipping group is in a state that allows it to be removed
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
    HardgoodFulfiller of = (HardgoodFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
    ModifyOrder pMessage = (ModifyOrder) map.get(PipelineConstants.COMMERCEMESSAGE);
    Modification pModification = (Modification) map.get(PipelineConstants.MODIFICATION);
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
    
    if (pMessage == null) {
        if (of.isLoggingError())
            of.logError(Constants.COMMERCE_MESSAGE_NULL);
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    }

    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShippingGroupStates sgs = of.getShippingGroupStates();
    ShippingGroup sg = null;
    
    GenericRemove genRemoveMod = (GenericRemove) pModification;
        
    if(pMessage.getOrderId() != genRemoveMod.getContainerId()) {
        String errorMsg = MessageFormat.format(Constants.CONTAINER_ID_MISMATCH, pMessage.getId());  
        if (of.isLoggingError())
	    of.logError(errorMsg);  
        tools.modificationFailed(genRemoveMod, performedModifications, errorMsg);
        return STOP_CHAIN_EXECUTION;
    }        
    
    try {
        sg = pOrder.getShippingGroup(genRemoveMod.getTargetId());
    } catch(ShippingGroupNotFoundException s) {
        String errMessage = MessageFormat.format(Constants.TARGET_SHIP_GROUP_NOT_FOUND, pMessage.getId());
        if (of.isLoggingError())
	    of.logError(errMessage);
        tools.modificationFailed(genRemoveMod, performedModifications, errMessage);
        return STOP_CHAIN_EXECUTION;
    }
    catch(InvalidParameterException i) {
        if (of.isLoggingError())
	    of.logError(i);
        tools.modificationFailed(genRemoveMod, performedModifications, "InvalidParameterException");
        return STOP_CHAIN_EXECUTION;
    }

    if(!tools.getFulfillerForShippingGroup(sg).equals(of.getFulfillerName())) {
        // this shipping group is owned by another fulfiller
        // return true since this isn't an error.
        return STOP_CHAIN_EXECUTION;
    }        
    
    if (of.isLoggingDebug())
        of.logDebug("Shipping Group is to be removed.");
        
    if(sg.getState() == sgs.getStateValue(ShippingGroupStates.REMOVED)) {
        // This shipping group has already been removed.
        return STOP_CHAIN_EXECUTION;
    }
    if(sg.getState() == sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION)) {
        if(of.isLoggingDebug())
            of.logDebug("Shipping group is not modifiable.");
        tools.modificationFailed(genRemoveMod, performedModifications, 
                                 MessageFormat.format(Constants.SHIP_GROUP_NOT_MODIFIABLE, sg.getId()));
        return STOP_CHAIN_EXECUTION;
    }

    map.put(PipelineConstants.SHIPPINGGROUP, sg);
    
    return SUCCESS;
    
  }
}
