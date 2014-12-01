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

package atg.commerce.order.processor;

import atg.commerce.order.*;
import atg.commerce.fulfillment.SubmitOrder;
import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.repository.*;
import atg.commerce.profile.*;

import java.util.HashMap;
import java.io.Serializable;

/**
 * This processor sends a message to the fulfillment engine when an order has been
 * processed. This message tells the Fulfillment engine to begin fulfilling the
 * Order in the message.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSendFulfillmentMessage.java#2 $$Change: 651448 $\
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.order.processor.EventSender
 * @see atg.commerce.fulfillment.SubmitOrder
 */
public class ProcSendFulfillmentMessage extends EventSender {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcSendFulfillmentMessage.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.commerce.order.OrderResources";

  /** Resource Bundle **/
  private static java.util.ResourceBundle sResourceBundle = atg.core.i18n.LayeredResourceBundle.getBundle(MY_RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  
  //-----------------------------------------------
  public ProcSendFulfillmentMessage() {
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcSendFulfillmentMessage";

  /**
   * Sets property LoggingIdentifier
   **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /**
   * Returns property LoggingIdentifier
   **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //-------------------------------------
  // property: ProfileTools
  CommerceProfileTools mProfileTools;

  /**
   * Sets property ProfileTools
   **/
  public void setProfileTools(CommerceProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  /**
   * Returns property ProfileTools
   **/
  public CommerceProfileTools getProfileTools() {
    return mProfileTools;
  }

  //-----------------------------------------------
  /**
   * This method creates and populates a SubmitOrder object. The SubmitOrder
   * object is the message which will be sent to the Fulfillment engine.
   *
   * @param pParam the Map which contains all the message parameters
   * @param pResult the PipelineResult object which is supplied to runProcess()
   * @return the Serializable message object
   */
  public Serializable createEventToSend(Object pParam, PipelineResult pResult)
                  throws Exception
  {
    // We don't need to check anything here, we can just send the message on.
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(PipelineConstants.ORDER);
    
    if (order == null)
      throw new InvalidParameterException(ResourceUtils.getMsgResource("InvalidOrderParameter",
                                      MY_RESOURCE_NAME, sResourceBundle));

    // We know the order is good since createEventToSend would throw an exception if the order
    // wasn't there.  Construct the SubmitOrder message and return it.  <TBD> we should think
    // about the id of the message.  I don't know how we are doing that right now.
    SubmitOrder soMessage = new SubmitOrder();
    soMessage.setOrder(order);
    soMessage.setSource(getMessageSourceName());
    soMessage.setId(getNextMessageId());
    soMessage.setOriginalSource(getMessageSourceName());
    soMessage.setOriginalId(soMessage.getId());
    //Add by Charles
    RepositoryItem profile = getProfileTools().getProfileForOrder(order);
    soMessage.setProfile(profile);
    soMessage.setSiteId(getSiteId(pParam));
    return soMessage;
    
  }
  
  //---------------------------------------------------------------------------
  // property: MessageSourceName
  //---------------------------------------------------------------------------
  private String mMessageSourceName = null;

  public void setMessageSourceName(String pMessageSourceName) {
    mMessageSourceName = pMessageSourceName;
  }

  /**
   * This defines the string that the source property of messages will
   * be set to.
   *
   * @beaninfo
   *          description: This is the name of this message source.  All outgoing messages
   *                       will use this as the source.
   **/
  public String getMessageSourceName() {
    return mMessageSourceName;
  }
  
}
