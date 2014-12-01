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
import atg.nucleus.GenericService;
import atg.commerce.order.Order;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.states.*;
import atg.b2bcommerce.approval.*;

import javax.jms.*;
import java.beans.*;
import java.util.*;

/**
 *
 * @author Manny Parasirakis
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcApprovalCompleteAnalyzer.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProcApprovalCompleteAnalyzer extends GenericService implements PipelineProcessor
{
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcApprovalCompleteAnalyzer.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  public final int APPROVED = 1;
  public final int FAILED_APPROVAL = 2;
  
  //-------------------------------------
  // property: approvedStatus
  //-------------------------------------
  private String mApprovedStatus = ApprovalMessage.APPROVED;

  /**
   * Returns property approvedStatus
   *
   * @return returns property approvedStatus
   */
  public String getApprovedStatus() {
    return mApprovedStatus;
  }

  /**
   * Sets property approvedStatus
   *
   * @param pApprovedStatus the value to set for property approvedStatus
   */
  public void setApprovedStatus(String pApprovedStatus) {
    mApprovedStatus = pApprovedStatus;
  }

  //--------------------------------------------------
  public ProcApprovalCompleteAnalyzer() {
  }
  
  //--------------------------------------------------
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    Map params = (Map)pParam;
    Order order = (Order) params.get(ApprovalConstants.ORDER);
    String approvalStatus = (String) params.get(ApprovalConstants.APPROVAL_STATUS);
    Locale locale = (Locale) params.get(ApprovalConstants.LOCALE);

    if (order == null)
      throw new InvalidParameterException(Constants.INVALID_ORDER_PARAMETER);
    if (approvalStatus == null)
      throw new InvalidParameterException(Constants.INVALID_APPROVAL_STATUS_PARAMETER);
    if (locale == null)
      locale = Locale.getDefault();
      
    if (isLoggingDebug())
      logDebug("Approval Status is: " + approvalStatus);
      
    if (approvalStatus.equalsIgnoreCase(getApprovedStatus())) {
      return APPROVED;
    }
    else {
      return FAILED_APPROVAL;
    }
  }

  //--------------------------------------------------
  public int[] getRetCodes() {
    int[] retCodes = {APPROVED, FAILED_APPROVAL};
    return retCodes;
  }
} // end of class
