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
import javax.jms.*;
import java.text.*;

/**
 * This processor routes the execution resulted in an update of a shipping group 
 * based on the shipping group's new state value.
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleShippingGroupState.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcHandleShippingGroupState implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleShippingGroupState.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int COMPLETE_REMOVE_ORDER = 1;
  private final int COMPLETE_ORDER = 2;
  private final int FAIL_ORDER = 3;
  private final int SHIP_SHIPPING_GROUP = 4;
  private final int MODIFICATION_NOT_SUPPORTED = 5;  
  
  //-----------------------------------------------
  public ProcHandleShippingGroupState() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The order remove completion processor
   * 2 - The order completion processor
   * 3 - The order failure processor
   * 4 - The shipping of a shipping group processor
   * 5 - The unsupported modification processor
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {COMPLETE_REMOVE_ORDER, COMPLETE_ORDER, 
                 FAIL_ORDER, SHIP_SHIPPING_GROUP,
                 MODIFICATION_NOT_SUPPORTED};
    return ret;
  } 

  //-----------------------------------------------
  /**
   * Executes an operation on an order after the shipping group's state has been changed
   *
   * This method requires that an Order and Modification object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and Modification object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    FulfillerSystem of = (FulfillerSystem) map.get(PipelineConstants.ORDERFULFILLER);
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
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
    
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();
    ShippingGroupStates sgs = of.getShippingGroupStates();
    OrderStates os = of.getOrderStates();
    ShippingGroup sg = null;
    
    // We are assuming this cast is okay. If people add different kinds of Modifications
    // or choose to use a different message for that this method will need to be overriden.
    GenericUpdate genupMod = (GenericUpdate) pModification;

    // get the shipping group to be modified
    try {
        sg = pOrder.getShippingGroup(genupMod.getTargetId());
    } catch(ShippingGroupNotFoundException e) {
        String[] msgArgs = { genupMod.getTargetId() };
        if (of.isLoggingWarning())
	  of.logWarning(ResourceUtils.getMsgResource("ShippingGroupNotInContainer",
                                    MY_RESOURCE_NAME, sResourceBundle, msgArgs));

        // it is a modifyOrder for HardgoodFulfiller
        if (of instanceof HardgoodFulfiller)
            tools.modificationFailed(pModification,
                                     performedModifications,
                                     Constants.SHIPPING_GROUP_NOT_IN_ORDER);
        return STOP_CHAIN_EXECUTION;
    } catch(InvalidParameterException i) {
        if(of.isLoggingError())
            of.logError(i);
        // it is a modifyOrder for HardgoodFulfiller
        if (of instanceof HardgoodFulfiller)
            tools.modificationFailed(pModification,
                                     performedModifications,
                                     i.toString());
        return STOP_CHAIN_EXECUTION;
    }
    
    if (of instanceof HardgoodFulfiller) {
        if(!tools.getFulfillerForShippingGroup(sg).equals(((HardgoodFulfiller)of).getFulfillerName())){
            // this shipping group is owned by another fulfiller
            // return true since this isn't an error.
            if(of.isLoggingDebug())
               of.logDebug("This shipping group is owned by another fulfiller: " + sg.getId());
            return STOP_CHAIN_EXECUTION;
        }        
    }
    
    if(genupMod.getPropertyName() == null) {
        if(of.isLoggingDebug())
            of.logDebug("The GenericUpdate modification has no propertyName.");
        return STOP_CHAIN_EXECUTION;
    }

    // If the property that was updated is state, and the value it was updated to is
    // not NO_PENDING_ACTION, then this order is not finished, otherwise it is
    if (genupMod.getPropertyName().equals("state")) {
        //int stateValue = ((Integer) genupMod.getNewValue()).intValue();
        Object newState = genupMod.getNewValue();
        int stateValue;
        if(newState instanceof Integer) {
            stateValue = ((Integer) newState).intValue();
        }
        else {
            stateValue = sgs.getStateFromString((String)newState);
        }

        if(stateValue == sgs.getStateValue(ShippingGroupStates.REMOVED)) {
            if((pOrder.getState() == os.getStateValue(OrderStates.PENDING_REMOVE)) &&
               (genupMod.getModificationStatus() == Modification.STATUS_SUCCESS)) {
                return COMPLETE_REMOVE_ORDER;
            }
        }
        else if(stateValue == sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION)) {
            if (of instanceof OrderFulfiller) {
                if(genupMod.getModificationStatus() == Modification.STATUS_SUCCESS) {
                    return COMPLETE_ORDER;
                }
            }
            else {
                // assume this is HardgoodFulfiller
                map.put(PipelineConstants.SHIPPINGGROUPID, sg.getId());
                return SHIP_SHIPPING_GROUP;
            }
        }
        else if(stateValue == sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION)) {
            if(genupMod.getModificationStatus() == Modification.STATUS_SUCCESS) {
                return FAIL_ORDER;
            }
        }
    } // if
    else if(of instanceof HardgoodFulfiller)
        return MODIFICATION_NOT_SUPPORTED;

    return STOP_CHAIN_EXECUTION;
    
  }
}
