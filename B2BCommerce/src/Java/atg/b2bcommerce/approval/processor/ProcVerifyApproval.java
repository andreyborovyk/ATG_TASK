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
import atg.commerce.order.Order;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.b2bcommerce.approval.*;

import java.util.*;

/**
 * This class returns the value in the alreadyApprovedReturnValue property if the state or the
 * order is the same as the state in thge approvedOrderState property of this class. If not, then
 * the value in notYetApprovedReturnValue is returned.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcVerifyApproval.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProcVerifyApproval extends ApplicationLoggingImpl implements PipelineProcessor
{
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcVerifyApproval.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  public final int NOT_YET_APPROVED = 1;

  //-------------------------------------
  // property: notYetApprovedReturnValue
  //-------------------------------------
  private int mNotYetApprovedReturnValue = NOT_YET_APPROVED;

  /**
   * Returns property notYetApprovedReturnValue
   *
   * @return returns property notYetApprovedReturnValue
   */
  public int getNotYetApprovedReturnValue() {
    return mNotYetApprovedReturnValue;
  }

  /**
   * Sets property notYetApprovedReturnValue
   *
   * @param pNotYetApprovedReturnValue the value to set for property notYetApprovedReturnValue
   */
  public void setNotYetApprovedReturnValue(int pNotYetApprovedReturnValue) {
    mNotYetApprovedReturnValue = pNotYetApprovedReturnValue;
  }

  //-------------------------------------
  // property: alreadyApprovedReturnValue
  //-------------------------------------
  private int mAlreadyApprovedReturnValue = STOP_CHAIN_EXECUTION_AND_COMMIT;

  /**
   * Returns property alreadyApprovedReturnValue
   *
   * @return returns property alreadyApprovedReturnValue
   */
  public int getAlreadyApprovedReturnValue() {
    return mAlreadyApprovedReturnValue;
  }

  /**
   * Sets property alreadyApprovedReturnValue
   *
   * @param pAlreadyApprovedReturnValue the value to set for property alreadyApprovedReturnValue
   */
  public void setAlreadyApprovedReturnValue(int pAlreadyApprovedReturnValue) {
    mAlreadyApprovedReturnValue = pAlreadyApprovedReturnValue;
  }

  //-------------------------------------
  // property: approvedOrderState
  //-------------------------------------
  private String mApprovedOrderState = "APPROVED";

  /**
   * Returns property approvedOrderState
   *
   * @return returns property approvedOrderState
   */
  public String getApprovedOrderState() {
    return mApprovedOrderState;
  }

  /**
   * Sets property approvedOrderState
   *
   * @param pApprovedOrderState the value to set for property approvedOrderState
   */
  public void setApprovedOrderState(String pApprovedOrderState) {
    mApprovedOrderState = pApprovedOrderState;
  }

  //--------------------------------------------------
  public ProcVerifyApproval() {
  }

  //--------------------------------------------------
  /**
   * This method returns the value in the alreadyApprovedReturnValue property if the state or the
   * order is the same as the state in thge approvedOrderState property of this class. If not, then
   * the value in notYetApprovedReturnValue is returned.
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    Map params = (Map)pParam;
    Order order = (Order) params.get(ApprovalConstants.ORDER);

    if (order == null)
      throw new InvalidParameterException(Constants.INVALID_ORDER_PARAMETER);

    String orderState = StateDefinitions.ORDERSTATES.getStateString(order.getState());
    if (isLoggingDebug())
      logDebug("Order state for order: " + order.getId() + " is " + orderState);
    if (getApprovedOrderState().equalsIgnoreCase(orderState)) {
      if (isLoggingDebug())
        logDebug("The order is already approved. Returning value: " + getAlreadyApprovedReturnValue());
      return getAlreadyApprovedReturnValue();
    }
    else {
      if (isLoggingDebug())
        logDebug("The order is not yet approved. Returning value: " + getNotYetApprovedReturnValue());
      return getNotYetApprovedReturnValue();
    }
  }

  //--------------------------------------------------
  public int[] getRetCodes() {
    int[] retCodes = {getNotYetApprovedReturnValue(), getAlreadyApprovedReturnValue()};
    return retCodes;
  }
} // end of class
