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
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.b2bcommerce.order.B2BOrder;
import atg.b2bcommerce.approval.ApprovalConstants;
import atg.b2bcommerce.approval.Constants;
import atg.commerce.order.InvalidParameterException;

import java.util.*;

/**
 * This processor adds the error messages in the PipelineResult object to the approverMessages
 * property in the Order object.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcAddApprovalSystemMessagesToOrder.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProcAddApprovalSystemMessagesToOrder extends ApplicationLoggingImpl implements PipelineProcessor
{
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcAddApprovalSystemMessagesToOrder.java#2 $$Change: 651448 $";

  //-------------------------------------
  public static final int SUCCESS = 1;

  //-------------------------------------
  // property: clearOldMessages
  //-------------------------------------
  private boolean mClearOldMessages = true;

  /**
   * Returns property clearOldMessages
   *
   * @return returns property clearOldMessages
   */
  public boolean getClearOldMessages() {
    return mClearOldMessages;
  }

  /**
   * Sets property clearOldMessages
   *
   * @param pClearOldMessages the value to set for property clearOldMessages
   */
  public void setClearOldMessages(boolean pClearOldMessages) {
    mClearOldMessages = pClearOldMessages;
  }

  //-------------------------------------
  // property: removeMessagesFromPipelineResult
  //-------------------------------------
  private boolean mRemoveMessagesFromPipelineResult = true;

  /**
   * Returns property removeMessagesFromPipelineResult
   *
   * @return returns property removeMessagesFromPipelineResult
   */
  public boolean getRemoveMessagesFromPipelineResult() {
    return mRemoveMessagesFromPipelineResult;
  }

  /**
   * Sets property removeMessagesFromPipelineResult
   *
   * @param pRemoveMessagesFromPipelineResult the value to set for property removeMessagesFromPipelineResult
   */
  public void setRemoveMessagesFromPipelineResult(boolean pRemoveMessagesFromPipelineResult) {
    mRemoveMessagesFromPipelineResult = pRemoveMessagesFromPipelineResult;
  }

  //-------------------------------------
  /**
   * Creates a new <code>ProcAddApprovalSystemMessagesToOrder</code> instance.
   */
  public ProcAddApprovalSystemMessagesToOrder() {
  }

  //-------------------------------------
  /**
   * This method puts all the approval error messages into the approvalSystemMessages property
   * of an order.
   *
   * @param pParam list of params to pipeline
   * @param pResult the pipeline result object
   * @return code indicating state
   * @exception Exception if an error occurs
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    Map pParams = (Map) pParam;
    B2BOrder order = (B2BOrder)pParams.get(ApprovalConstants.ORDER);

    if (order == null)
      throw new InvalidParameterException(Constants.INVALID_ORDER_PARAMETER);

    List messages = order.getApprovalSystemMessages();

    if (getClearOldMessages())
      messages.clear();

    boolean removeMessages = getRemoveMessagesFromPipelineResult();
    Object[] keys = pResult.getErrorKeys();
    for (int i = 0; i < keys.length; i++) {
      messages.add(pResult.getError(keys[i]));

      if (isLoggingDebug())
        logDebug("Added approval system message: [" + pResult.getError(keys[i]) + "] to order with id: " + order.getId());

      if (removeMessages)
        pResult.removeError(keys[i]);
    }

    return SUCCESS;
  }

  /**
   * The return codes that this processor can return.
   * The list of return codes are:
   * <UL>
   *   <LI>1 - The processor completed
   * </UL>
   *
   * @return an <code>int[]</code> of the valid return codes
   */
  public int[] getRetCodes() {
    int[] retCodes = {SUCCESS};
    return retCodes;
  }
} // end of class
