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
import atg.commerce.order.RelationshipNotFoundException;
import atg.commerce.order.ShippingGroupCommerceItemRelationship;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;

import java.util.*;
import javax.jms.*;
import java.text.*;

/**
 * This processor executes the relationship id target modification passed to the OrderFulfiller
 * in the modifyOrder message. It simply passes it down to the fulfiller responsible for
 * this relationship object 
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcPerformRelationshipModification.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcPerformRelationshipModification implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcPerformRelationshipModification.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcPerformRelationshipModification() {
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
   * This processor executes the relationship id target modification passed to the OrderFulfiller
   * in the modifyOrder message. It simply passes it down to the fulfiller responsible for
   * this relationship object.
   *
   * This method requires that a Commerce message, Order and OrderFulfiller object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain a Commerce message and OrderFulfiller object
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
    ShippingGroup sg = null;
    ShippingGroupStates sgs = of.getShippingGroupStates();
    ShippingGroupCommerceItemRelationship sgcir = null;

    // We are assuming this cast is okay.  If people add different kinds of Modifications
    // or choose to use a different message for that this method will need to be overriden.
    IdTargetModification targetMod = (IdTargetModification) pModification;

    try {
      sgcir = (ShippingGroupCommerceItemRelationship) pOrder.getRelationship(targetMod.getTargetId());
      sg = sgcir.getShippingGroup();
    } catch(RelationshipNotFoundException s) {
      String errMessage = MessageFormat.format(Constants.TARGET_SHIP_GROUP_NOT_FOUND, pMessage.getId());
      if (of.isLoggingError())
        of.logError(errMessage);

      tools.modificationFailed(targetMod, performedModifications, errMessage);
    }
    catch(InvalidParameterException i) {
      if (of.isLoggingError())
        of.logError(i);

      tools.modificationFailed(targetMod, performedModifications, "InvalidParameterException");
    }

    if((sg.getState() == sgs.getStateValue(ShippingGroupStates.INITIAL)) ||
       (sg.getState() == sgs.getStateValue(ShippingGroupStates.REMOVED))) {
      // ie the OrderFulfiller owns the shipping group
      of.getOrderFulfillmentTools().modificationFailed(pModification, performedModifications,
                                                       Constants.MODIFICATION_NOT_SUPPORTED);
    }
    else {
      String fulfiller = tools.getFulfillerForShippingGroup(sg);
      List mods = new ArrayList();
      mods.add(pModification);

      try {
        tools.sendModifyOrder(pMessage.getOrderId(), mods, of,
                              tools.getFulfillerPort(fulfiller), pMessage);
      }
      catch(javax.jms.JMSException j) {
        if(of.isLoggingError())
          of.logError(j);
      }
    }

    return SUCCESS;
  }
}
