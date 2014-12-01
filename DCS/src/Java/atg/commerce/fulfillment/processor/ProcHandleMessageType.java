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

import atg.commerce.fulfillment.*;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.commerce.order.InvalidParameterException;

import javax.jms.*;
import java.util.*;

/**
 * This processor determines the message type received by the Fulfiller router
 * and calls to multiple chains to process the message
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleMessageType.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcHandleMessageType implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcHandleMessageType.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int HANDLE_SUBMIT_ORDER = 1;
  private final int HANDLE_FULFILL_ORDER_FRAGMENT = 2;
  private final int HANDLE_UPDATE_INVENTORY = 3;
  private final int HANDLE_MODIFY_ORDER = 4;
  private final int HANDLE_MODIFY_ORDER_NOTIFICATION = 5;
  private final int HANDLE_NEWTYPE = 6;
  
  //-----------------------------------------------
  public ProcHandleMessageType() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The call to handle SubmitOrder message chain
   * 2 - The call to handle ModifyOrder message chain
   * 3 - The call to handle ModifyOrderNotification message chain
   * 4 - The call to handle any other message type
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {HANDLE_SUBMIT_ORDER, HANDLE_FULFILL_ORDER_FRAGMENT,
                 HANDLE_UPDATE_INVENTORY, HANDLE_MODIFY_ORDER, 
                 HANDLE_MODIFY_ORDER_NOTIFICATION, HANDLE_NEWTYPE}; 
    return ret;
  } 

  //-----------------------------------------------
  /**
   * This method calls updateOrder() on the supplied Order and then checks to see
   * if it is transient. If it is transient then addOrder() is called in the OrderManager
   * to make the Order persistent.
   *
   * This method requires that an Order and OrderManager object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and OrderManager object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   * @see atg.commerce.order.OrderManager#addOrder(Order)
   * @see atg.commerce.order.OrderManager#updateOrder(Order)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    HashMap map = (HashMap) pParam;
    Message message = (Message) map.get(PipelineConstants.MESSAGE);
    FulfillerSystem of = (FulfillerSystem) map.get(PipelineConstants.ORDERFULFILLER);
        
    if (message == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    
    String messageType = message.getJMSType();
    
    if (messageType.equals(ModifyOrder.TYPE)) {
        return HANDLE_MODIFY_ORDER;
    }
    else if (messageType.equals(ModifyOrderNotification.TYPE)) {
        return HANDLE_MODIFY_ORDER_NOTIFICATION;
    }
    else if (of instanceof OrderFulfiller) {
        if (messageType.equals(SubmitOrder.TYPE)) {
            return HANDLE_SUBMIT_ORDER;
        }
    }
    else if (of instanceof HardgoodFulfiller) {
        if (messageType.equals(FulfillOrderFragment.TYPE)) {
            return HANDLE_FULFILL_ORDER_FRAGMENT;
        }
        else if (messageType.equals(UpdateInventory.TYPE)) {
            return HANDLE_UPDATE_INVENTORY;
        }
    }
    else if (of instanceof ElectronicFulfiller) {
        if (messageType.equals(FulfillOrderFragment.TYPE)) {
            return HANDLE_FULFILL_ORDER_FRAGMENT;
        }
    }
  
    return HANDLE_NEWTYPE;
  }
    
}
