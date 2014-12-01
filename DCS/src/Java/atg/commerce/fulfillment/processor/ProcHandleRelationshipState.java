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
import atg.commerce.order.Relationship;
import atg.commerce.order.CommerceItem;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.RelationshipNotFoundException;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.repository.RemovedItemException;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor will route the execution resulted in a change of a relationship's state
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleRelationshipState.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcHandleRelationshipState implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleRelationshipState.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int REMOVE_RELATIONSHIP = 1;
  
  //-----------------------------------------------
  public ProcHandleRelationshipState() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The remove relationship processor
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {REMOVE_RELATIONSHIP};
    return ret;
  } 

  //-----------------------------------------------
  /**
   * Route the execution based on a new relationship's state
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
    Modification pModification = (Modification) map.get(PipelineConstants.MODIFICATION);

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
    ShipItemRelationshipStates sirs = of.getShipItemRelationshipStates();
    OrderStates os = of.getOrderStates();
    CommerceItemStates cis = of.getCommerceItemStates();
    ShippingGroupCommerceItemRelationship sgcir = null;

    // We are assuming this cast is okay.  If people add different kinds of Modifications
    // or choose to use a different message for that this method will need to be overriden.
    GenericUpdate genupMod = (GenericUpdate) pModification;
    
    // get the shipping group to be modified
    try {
        Relationship r = pOrder.getRelationship(genupMod.getTargetId());
        if(!(r instanceof ShippingGroupCommerceItemRelationship)) {
            if(of.isLoggingDebug())
                of.logDebug("Modification of a relationship other than a ShipItemRel.  Ignored.");
            return STOP_CHAIN_EXECUTION;
        }
        sgcir = (ShippingGroupCommerceItemRelationship) r;
    } catch(RelationshipNotFoundException e) {
        String[] msgArgs = { genupMod.getTargetId() };
        if (of.isLoggingWarning())
	  of.logWarning(ResourceUtils.getMsgResource("RelationshipNotInContainer",
                                    MY_RESOURCE_NAME, sResourceBundle, msgArgs));

        return STOP_CHAIN_EXECUTION;
    } catch(RemovedItemException e) {
        String[] msgArgs = { genupMod.getTargetId() };
        if (of.isLoggingWarning())
	    /*of.logWarning(ResourceUtils.getMsgResource("RelationshipNotInContainer",
	      MY_RESOURCE_NAME, sResourceBundle, msgArgs));*/
	    of.logWarning(e);

        return STOP_CHAIN_EXECUTION;
    } catch(InvalidParameterException i) {
        if(of.isLoggingError())
            of.logError(i);
        return STOP_CHAIN_EXECUTION;
    }
    
    if(genupMod.getPropertyName() == null) {
        if(of.isLoggingDebug())
            of.logDebug("The GenericUpdate modification has no propertyName.");
        return STOP_CHAIN_EXECUTION;
    }

    // If the property that was updated is state, and the value it was updated to is
    // not REMOVED, then the CommerceItem quantity needs to be updated
    if (genupMod.getPropertyName().equals("state")) {
        //int stateValue = ((Integer) genupMod.getNewValue()).intValue();
          Object newState = genupMod.getNewValue();
          int stateValue;
          if(newState instanceof Integer) {
              stateValue = ((Integer) newState).intValue();
          }
          else {
              stateValue = sirs.getStateFromString((String)newState);
          }
          if((stateValue == sirs.getStateValue(ShipItemRelationshipStates.REMOVED)) &&
             (genupMod.getModificationStatus() == Modification.STATUS_SUCCESS))
              {
                  if(of.isLoggingDebug())
                      of.logDebug("ShipItemRelationship " + sgcir.getId() + " was removed.");
                  
                  CommerceItem ci = sgcir.getCommerceItem();
                  if((pOrder.getState() == os.getStateValue(OrderStates.PENDING_REMOVE)) ||
                     (pOrder.getState() == os.getStateValue(OrderStates.REMOVED))) {
                      // the order was removed... no need to update the quantity
                  }
                  else if((ci.getState() == cis.getStateValue(CommerceItemStates.PENDING_REMOVE)) ||
                          (ci.getState() == cis.getStateValue(CommerceItemStates.REMOVED))) {
                      // the commerce item was removed... no need to update the quantity
                  }
                  else {
                      // update the quantity
                      return REMOVE_RELATIONSHIP;
                  }
              }
    }
    
    return STOP_CHAIN_EXECUTION;
  }
}
  
