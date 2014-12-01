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
import atg.b2bcommerce.approval.ApprovalException;
import atg.commerce.order.InvalidParameterException;
import atg.repository.*;
import atg.userprofiling.ProfileTools;

import java.text.*;
import java.util.*;

/**
 * This processor adds the list of valid approver ids to the approverIds property of the order.
 * If no approvers are defined, then an exception is thrown unless the allowCheckoutIfApproversNotDefined
 * property is set to true. Then, the order will just be checked out without approval. The default
 * value of allowCheckoutIfApproversNotDefined is false.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcAddApproverIdsToOrder.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class ProcAddApproverIdsToOrder extends ApplicationLoggingImpl implements PipelineProcessor
{
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/approval/processor/ProcAddApproverIdsToOrder.java#2 $$Change: 651448 $";

  //-------------------------------------
  public static final int SUCCESS = 1;

  //-------------------------------------
  // property: approversPropertyName
  //-------------------------------------
  private String mApproversPropertyName = "approvers";

  /**
   * Returns property approversPropertyName
   *
   * @return returns property approversPropertyName
   */
  public String getApproversPropertyName() {
    return mApproversPropertyName;
  }

  /**
   * Sets property approversPropertyName
   *
   * @param pApproversPropertyName the value to set for property approversPropertyName
   */
  public void setApproversPropertyName(String pApproversPropertyName) {
    mApproversPropertyName = pApproversPropertyName;
  }

  //---------------------------------------------------------------------
  // property: profileItemDescName
  private String mProfileItemDescName = "user";

  /**
   * Return the profileItemDescName property.
   * @return
   */
  public String getProfileItemDescName() {
    return mProfileItemDescName;
  }

  /**
   * Set the profileItemDescName property.
   * @param pProfileItemDescName
   */
  public void setProfileItemDescName(String pProfileItemDescName) {
    mProfileItemDescName = pProfileItemDescName;
  }

  //---------------------------------------------------------------------
  // property: ProfileTools
  private ProfileTools mProfileTools;

  /**
   * Return the ProfileTools property.
   * @return
   */
  public ProfileTools getProfileTools() {
    return mProfileTools;
  }

  /**
   * Set the ProfileTools property.
   * @param pProfileTools
   */
  public void setProfileTools(ProfileTools pProfileTools) {
    mProfileTools = pProfileTools;
  }

  //-------------------------------------
  // property: allowCheckoutIfApproversNotDefined
  //-------------------------------------
  private boolean mAllowCheckoutIfApproversNotDefined = false;

  /**
   * Returns the allowCheckoutIfApproversNotDefined
   */
  public boolean getAllowCheckoutIfApproversNotDefined() {
    return mAllowCheckoutIfApproversNotDefined;
  }

  /**
   * Sets the allowCheckoutIfApproversNotDefined
   */
  public void setAllowCheckoutIfApproversNotDefined(boolean pAllowCheckoutIfApproversNotDefined) {
    mAllowCheckoutIfApproversNotDefined = pAllowCheckoutIfApproversNotDefined;
  }

  //-------------------------------------
  /**
   * Creates a new <code>ProcAddApproverIdsToOrder</code> instance.
   */
  public ProcAddApproverIdsToOrder() {
  }

  //-------------------------------------
  /**
   * This method adds the list of valid approver ids to the approverIds property of the order.
   * It gets the valid approver ids from the profile property named approvers from the order owner's
   * profile.
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

    RepositoryItem profile = loadProfile(order);
    List approvers = (List) profile.getPropertyValue(getApproversPropertyName());
    if (approvers.size() == 0) {
      if (getAllowCheckoutIfApproversNotDefined()) {
        if (isLoggingDebug())
          logDebug("Approvers not defined for this profile. AllowCheckoutIfApproversNotDefined is set to true, continuing checkout.");
        return STOP_CHAIN_EXECUTION_AND_COMMIT;
      }
      else {
        if (isLoggingDebug())
          logDebug("Approvers not defined for this profile. AllowCheckoutIfApproversNotDefined is set to false, terminating checkout.");
        throw new ApprovalException(MessageFormat.format(Constants.APPROVERS_NOT_DEFINED, profile.getRepositoryId()));
      }
    }

    List approverIds = order.getAuthorizedApproverIds();

    Iterator iter = approvers.iterator();
    while (iter.hasNext()) {
      RepositoryItem item = (RepositoryItem) iter.next();
      approverIds.add(item.getRepositoryId());
      if (isLoggingDebug())
        logDebug("Added approver id: " + item.getRepositoryId() + " to order with id: " + order.getId());
    }

    return SUCCESS;
  }

  /**
   * Loads the profile for the given order
   */
  protected RepositoryItem loadProfile(B2BOrder pOrder) throws RepositoryException {
    return getProfileTools().getProfileRepository().getItem(pOrder.getProfileId(), getProfileItemDescName());
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
