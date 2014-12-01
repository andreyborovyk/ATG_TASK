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

import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.*;
import atg.repository.RepositoryItem;
import atg.commerce.CommerceException;
import atg.commerce.order.Order;
import atg.commerce.order.InvalidParameterException;
import atg.b2bcommerce.approval.*;
import atg.b2bcommerce.order.B2BOrder;

import java.util.*;

/**
 * This processor adds an approver's profile id to the order being approved.
 * 
 * @author Manny Parasirakis
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcAddApproverIdToOrder.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProcAddApproverIdToOrder extends ApplicationLoggingImpl implements PipelineProcessor
{
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcAddApproverIdToOrder.java#2 $$Change: 651448 $";

  /** code for successful process */
  public final int SUCCESS = 1;
  
  //--------------------------------------------------
  /**
   * Creates a new <code>ProcAddApproverIdToOrder</code> instance.
   */
  public ProcAddApproverIdToOrder() {
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcAddApproverIdToOrder";

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

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  /**
   * This method actually runs the process. This method adds the profile id to the
   * order's approver ids list. It requires that the Order and Profile be supplied in
   * pParam.
   *
   * @param pParam parameter of user params
   * @param pResult the pipeline result object where results are placed
   * @return an integer specifying the processors return code
   * @exception Exception throws an exception back to the caller
   */
  public int runProcess(Object pParam, PipelineResult pResult) 
    throws Exception 
  {
    Map params = (Map) pParam;
    B2BOrder order = (B2BOrder) params.get(ApprovalConstants.ORDER);
    RepositoryItem profile = (RepositoryItem) params.get(ApprovalConstants.PROFILE);
    
    if (order == null)
      throw new InvalidParameterException(Constants.INVALID_ORDER_PARAMETER);
    
    if (profile == null)
      throw new InvalidParameterException(Constants.INVALID_PROFILE_PARAMETER);
      
    order.getApproverIds().add(profile.getRepositoryId());
    
    if (isLoggingDebug())
      logDebug("Added " + profile.getRepositoryId() + " to the approverIds list in the order whose id is " + order.getId());
    
    return SUCCESS;
  }

  /**
   * Specifies the return codes from this processor.
   *
   * @return an integer array of valid codes
   */
  public int[] getRetCodes() {
    int retCodes[] = {SUCCESS};
    return retCodes;
  }  
} // end of class
