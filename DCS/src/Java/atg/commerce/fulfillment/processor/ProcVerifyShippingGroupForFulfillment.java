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

import javax.jms.*;
import java.util.*;
import java.text.*;

/**
 * This method verifies that the shipping group to be fulfilled is in a state which allows it
 * to be fulfilled.  If the shipping group is in an illegal state we will throw an exception. 
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcVerifyShippingGroupForFulfillment.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcVerifyShippingGroupForFulfillment implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcVerifyShippingGroupForFulfillment.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcVerifyShippingGroupForFulfillment() {
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
   * This method verifies that the shipping group to be fulfilled is in a state which allows it
   * to be fulfilled.  If the shipping group is in an illegal state we will throw an exception.
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
    
    if(of.isLoggingDebug())
       of.logDebug("Inside processShippingGroup");
    
    OrderFulfillmentTools tools = of.getOrderFulfillmentTools();

    if(pShippingGroup.getCommerceItemRelationshipCount() == 0) {
      if(of.isLoggingDebug())
          of.logDebug("This shipping group [" + pShippingGroup.getId() + "] is empty.");
      return STOP_CHAIN_EXECUTION;
    }
    
    if(!tools.getFulfillerForShippingGroup(pShippingGroup).equals(of.getFulfillerName())) {
       if(of.isLoggingDebug())
          of.logDebug("This shipping group: " + pShippingGroup.getId() + " is owned by another fulfiller.");
       return STOP_CHAIN_EXECUTION;
    }        
    
    try {
        verifyShippingGroupFulfillment(of, pShippingGroup);
    }
    catch(IllegalShippingGroupStateException e){
        if(of.isLoggingInfo())
            of.logInfo(e.toString());
        return STOP_CHAIN_EXECUTION;
    }
    
    return SUCCESS;
  }


  /**
   * This method verifies that the shipping group to be fulfilled is in a state which allows it
   * to be fulfilled.  If the shipping group is in an illegal state we will throw an exception.
   * Illegal states include: 
   *
   * @beaninfo
   *          description: This method verifies that the given shipping group is in a state
   *                       which allows it to be fulfulled.
   * @param pShippingGroup The shipping group we are verifying
   * @exception IllegalShippingGroupStateException
   * @see atg.commerce.states.ShippingGroupStates#REMOVED, 
   * @see atg.commerce.states.ShippingGroupStates#FAILED, 
   * @see atg.commerce.states.ShippingGroupStates#NO_PENDING_ACTION, 
   * @see atg.commerce.states.ShippingGroupStates#PENDING_MERCHANT_ACTION
   **/
  protected void verifyShippingGroupFulfillment(HardgoodFulfiller of, ShippingGroup pShippingGroup)
    throws IllegalShippingGroupStateException
  {
    int state=pShippingGroup.getState();
    ShippingGroupStates sgs = of.getShippingGroupStates();

    if(state == sgs.getStateValue(ShippingGroupStates.PROCESSING)) {
      return;
    }
    else if(state == sgs.getStateValue(ShippingGroupStates.REMOVED))
    {	  
      throw new IllegalShippingGroupStateException(MessageFormat.format(Constants.SHIP_GROUP_STATE_REMOVED,
      		pShippingGroup.getId()));
    }
    else if(state == sgs.getStateValue(ShippingGroupStates.PENDING_REMOVE))
    {
      throw new IllegalShippingGroupStateException(MessageFormat.format(Constants.SHIP_GROUP_STATE_PENDING_REMOVE,
      		pShippingGroup.getId()));
    }
    else if(state == sgs.getStateValue(ShippingGroupStates.FAILED))
    {
      throw new IllegalShippingGroupStateException(MessageFormat.format(Constants.SHIP_GROUP_STATE_FAILED, 
      		pShippingGroup.getId()));
    }
    else if(state == sgs.getStateValue(ShippingGroupStates.NO_PENDING_ACTION))
    {
      throw new IllegalShippingGroupStateException(MessageFormat.format(Constants.SHIP_GROUP_STATE_FAILED, 
      		pShippingGroup.getId()));
    }
    else if(state == sgs.getStateValue(ShippingGroupStates.PENDING_MERCHANT_ACTION))
    {
      throw new IllegalShippingGroupStateException(MessageFormat.format(Constants.SHIP_GROUP_STATE_PENDING_MERCHANT_ACTION,
      		pShippingGroup.getId()));
    }
  }

}   
