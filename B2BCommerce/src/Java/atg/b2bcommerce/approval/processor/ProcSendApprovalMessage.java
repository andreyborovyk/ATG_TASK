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

import atg.service.pipeline.*;
import atg.repository.RepositoryItem;
import atg.commerce.order.Order;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.processor.EventSender;
import atg.b2bcommerce.approval.ApprovalMessage;
import atg.b2bcommerce.approval.Constants;
import atg.b2bcommerce.approval.ApprovalConstants;
import atg.userprofiling.Profile;

import java.util.*;
import java.io.Serializable;

/**
 * This processor instantiates an ApprovalMessage object and populates it. The runProcess() method
 * is inherited from the atg.commerce.order.processor.EventSender class. The EventSender class handles
 * sending the message to the messaging system.
 * 
 * @author Manny Parasirakis
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcSendApprovalMessage.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProcSendApprovalMessage extends EventSender
{
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcSendApprovalMessage.java#2 $$Change: 651448 $";
  
  //---------------------------------------------------------------------
  // property: messageSourceName
  String mMessageSourceName;

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

  //--------------------------------------------------
  /**
   * Creates a new <code>ProcSendApprovalMessage</code> instance.
   */
  public ProcSendApprovalMessage() {
  }

  //--------------------------------------------------
  /**
   * This method creates the ApprovalMessage object and populates it.
   */
  public Serializable createEventToSend(Object pParam, PipelineResult pResult) 
        throws Exception
  {
     // We don't need to check anything here, we can just send the message on.
    HashMap map = (HashMap) pParam;
    Order order = (Order) map.get(ApprovalConstants.ORDER);
    Profile profile = (Profile) map.get(ApprovalConstants.PROFILE);
    RepositoryItem orderOwnerProfile = (RepositoryItem) map.get(ApprovalConstants.ORDEROWNERPROFILE);
    String approvalStatus = (String) map.get(ApprovalConstants.APPROVAL_STATUS);
    
    if (order == null)
      throw new InvalidParameterException(Constants.INVALID_ORDER_PARAMETER);

    if (profile == null)
      throw new InvalidParameterException(Constants.INVALID_PROFILE_PARAMETER);
    
    if (orderOwnerProfile == null)
      throw new InvalidParameterException(Constants.INVALID_ORDER_OWNER_PROFILE_PARAMETER);
    
    if (approvalStatus == null)
      throw new InvalidParameterException(Constants.INVALID_APPROVAL_STATUS_PARAMETER);

    // order and profile are valid either, we would have thrown exception
    ApprovalMessage auMessage = new ApprovalMessage();
    auMessage.setOrder(order);
    auMessage.setProfile(profile.getDataSource());
    auMessage.setOrderOwnerProfile(orderOwnerProfile);
    auMessage.setApprovalStatus(approvalStatus);
    auMessage.setSource(getMessageSourceName());
    auMessage.setId(getNextMessageId());
    auMessage.setOriginalSource(getMessageSourceName());
    auMessage.setOriginalId(auMessage.getId());
    auMessage.setSiteId(getSiteId(pParam));
    
    return auMessage;
  }
} // end of class
