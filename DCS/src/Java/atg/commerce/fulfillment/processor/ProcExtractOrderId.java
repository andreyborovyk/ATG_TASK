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
import atg.commerce.messaging.*;
import atg.commerce.order.InvalidParameterException;
import atg.service.lockmanager.*;

import java.util.*;
import javax.jms.*;

/**
 * This processor uses the ClientLockManager to guarrantee that only one thread 
 * dealing with a message for a given key is running through the system at any
 * moment in time.  The key used to acquire the lock is returned by the method
 * getKeyForMessage().
 *
 * @author Michael Traskunov
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcExtractOrderId.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcExtractOrderId implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/fulfillment/processor/ProcExtractOrderId.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.fulfillment.FulfillmentResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  private final int SUCCESS = 1;
  
  //-----------------------------------------------
  public ProcExtractOrderId() {
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
   * This method gets a message received by a Fulfiller router and locks it by
   * calling the getKeyForMessage routine.
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
    ObjectMessage oMessage = (ObjectMessage) map.get(PipelineConstants.MESSAGE);
    FulfillerSystem of = (FulfillerSystem) map.get(PipelineConstants.ORDERFULFILLER);

    if (of == null)
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderFulfillerParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    if (oMessage == null) {
        if (of.isLoggingError())
            of.logError(Constants.MESSAGE_IS_NULL);
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    }

    CommerceMessage pMessage = (CommerceMessage) oMessage.getObject();
    
    // should never happen
    if (pMessage == null) {
        if (of.isLoggingError())
            of.logError(Constants.COMMERCE_MESSAGE_NULL);
        throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidCommerceMessageParameter",
                                                                         MY_RESOURCE_NAME, sResourceBundle));
    }

    String messageType = oMessage.getJMSType();
    String orderId = getKeyForMessage(messageType, pMessage);
    map.put(PipelineConstants.ORDERID, orderId);
    map.put(PipelineConstants.COMMERCEMESSAGE, pMessage);
    
    return SUCCESS;
  }


  /**
   * This method will return the key to be used for locking out other messages with the same
   * key while a thread is handling this message.
   *
   * @beaninfo
   *          description: This method will return the key to be used for locking 
   *                       out other messages with the same key while a thread 
   *                       is handling this message.
   * @param pMessage the ObjectMessage containing the CommerceMessage.
   * @return an Object which serves as the key for the message
   **/
  protected String getKeyForMessage(String messageType, CommerceMessage cMessage)
      throws InvalidParameterException
  {
      if (messageType.equals(SubmitOrder.TYPE)) {
          return ((SubmitOrder) cMessage).getOrderId();
      }
      else if (messageType.equals(FulfillOrderFragment.TYPE)) {
          return ((FulfillOrderFragment) cMessage).getOrderId();
      }
      else if (messageType.equals(ModifyOrder.TYPE)) {
          return ((ModifyOrder) cMessage).getOrderId();
      }
      else if (messageType.equals(ModifyOrderNotification.TYPE)) {
          return ((ModifyOrderNotification) cMessage).getOrderId();
      }
      return null;
  }
    
}
