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
 * This processor executes the shipping group update modification sent in the modify
 * order notification message to the OrderFulfiller. The message gets forwarded to
 * the fulfiller system
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleShippingGroupUpdateModification.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcHandleShippingGroupUpdateModification implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleShippingGroupUpdateModification.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcHandleShippingGroupUpdateModification() {
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
   * Processes the shipping group update modification. Forwards the message to the
   * fulfiller system.
   *
   * This method requires that an Order, CommerceMessage and OrderFulfiller object be supplied
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
    OrderFulfiller of = (OrderFulfiller) map.get(PipelineConstants.ORDERFULFILLER);
    Order pOrder = (Order) map.get(PipelineConstants.ORDER);
    ModifyOrderNotification pMessage = (ModifyOrderNotification) map.get(PipelineConstants.COMMERCEMESSAGE);
    Modification modification = (Modification) map.get(PipelineConstants.MODIFICATION);
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

    String[] shippingGroupIds = ((ShippingGroupUpdate) modification).getShippingGroupIds();
    List shippingGroupsList = new ArrayList();
    
    for(int i=0;i<shippingGroupIds.length;i++) {
        try {
            sg = pOrder.getShippingGroup(shippingGroupIds[i]);
            int sgState = sg.getState();
            if(sgState == sgs.getStateValue(ShippingGroupStates.INITIAL)) {
                String msg = Constants.SHIP_GROUP_STATE_PROCESSING;
                tools.setShippingGroupState(sg, sgs.getStateValue(ShippingGroupStates.PROCESSING),
                                            MessageFormat.format(msg, sg.getShippingGroupClassType()),
                                            performedModifications);
                setShippingGroupSubmittedDate(of, sg, performedModifications);
                shippingGroupsList.add(sg);
            }
            else if(sgState == sgs.getStateValue(ShippingGroupStates.PROCESSING)) {
                shippingGroupsList.add(sg);
            }
        }
        catch(ShippingGroupNotFoundException sgnfe) {
          String[] msgArgs = { shippingGroupIds[i] };
          if (of.isLoggingWarning())
	    of.logWarning(ResourceUtils.getMsgResource("ShippingGroupNotInContainer",
						       MY_RESOURCE_NAME, sResourceBundle, msgArgs));
	  
        }
        catch (InvalidParameterException ipe) {
            if(of.isLoggingError())
                of.logError(ipe);
        }
    } //for
    
    tools.sendShippingGroupUpdateModification(pOrder, shippingGroupsList, of, pMessage);

    return SUCCESS;
  
  }


  /**
   * This method sets the submitted date of the shipping group to the
   * current time.  To ensure this only happens once, it checks first
   * to make sure it is not currently set.
   *
   * @beaninfo
   *          description: Set the submittedDate of the given shipping group to the current time.
   * @param pShippingGroup The shipping group to set.
   * @param pModificationList Place to store new modifications.
   **/
  protected void setShippingGroupSubmittedDate(OrderFulfiller of, ShippingGroup pShippingGroup, 
                                               List pModificationList)
  {
    java.util.Date oldTime = pShippingGroup.getSubmittedDate();
    java.util.Date newTime = new java.util.Date();

    pShippingGroup.setSubmittedDate(newTime);
    Modification m = of.getOrderFulfillmentTools().createShipUpdateModification("submittedDate",
                                                                             oldTime, newTime,
                                                                             pShippingGroup.getId());
    pModificationList.add(m);
  }


}
