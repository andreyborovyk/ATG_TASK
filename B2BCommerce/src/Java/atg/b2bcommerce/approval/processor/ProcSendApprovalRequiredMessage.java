/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

package atg.b2bcommerce.approval.processor;

import atg.repository.RepositoryItem;
import atg.service.pipeline.*;
import atg.b2bcommerce.approval.*;
import atg.commerce.order.Order;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.processor.EventSender;

import java.io.Serializable;
import java.util.*;

/**
 * This processor creates and sends an ApprovalRequiredMessage to the messaging system.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcSendApprovalRequiredMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProcSendApprovalRequiredMessage extends EventSender
{
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcSendApprovalRequiredMessage.java#2 $$Change: 651448 $";
    
  //-------------------------------------
  /**
   * Creates a new <code>ProcSendApprovalRequiredMessage</code> instance.
   */
  public ProcSendApprovalRequiredMessage() {
  }

  //-------------------------------------
  // property: messageSourceName
  private String mMessageSourceName;

  /**
   * Return the messageSourceName property.
   * @return the name of the message source
   */
  public String getMessageSourceName() {
    return mMessageSourceName;
  }

  /**
   * Set the messageSourceName property.
   * @param pMessageSourceName the name of the message source
   */
  public void setMessageSourceName(String pMessageSourceName) {
    mMessageSourceName = pMessageSourceName;
  }

  //-------------------------------------
  /**
   * This method creates and populates an ApprovalRequireMessage object. 
   * This message is submitted to the approval system when an order is
   * marked as requiring approval.
   *
   * @param pParam the Map which contains all the message parameters
   * @param pResult the PipelineResult object which is supplied to runProcess()
   * @return the Serializable message object
   */
  public Serializable createEventToSend(Object pParam, PipelineResult pResult) throws Exception
  {
    // We don't need to check anything here, we can just send the message on.
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(ApprovalConstants.ORDER);
    RepositoryItem profile = (RepositoryItem) map.get(ApprovalConstants.PROFILE);
    
    if (order == null)
      throw new InvalidParameterException(Constants.INVALID_ORDER_PARAMETER);

    if (profile == null)
      throw new InvalidParameterException(Constants.INVALID_PROFILE_PARAMETER);

    // order and profile are valid either, we would have thrown exception
    ApprovalRequiredMessage arMessage = new ApprovalRequiredMessage();
    arMessage.setOrder(order);
    arMessage.setProfile(profile);
    arMessage.setSource(getMessageSourceName());
    arMessage.setId(getNextMessageId());
    arMessage.setOriginalSource(getMessageSourceName());
    arMessage.setOriginalId(arMessage.getId());
    arMessage.setSiteId(getSiteId(pParam));
    return arMessage;
  }  
} // end of class
