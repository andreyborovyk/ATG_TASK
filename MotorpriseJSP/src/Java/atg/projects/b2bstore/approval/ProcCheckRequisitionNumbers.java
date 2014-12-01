/*<ATGCOPYRIGHT>
 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.projects.b2bstore.approval;

import atg.nucleus.GenericService;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.InvalidParameterException;
import atg.b2bcommerce.approval.ApprovalConstants;
import atg.b2bcommerce.approval.Constants;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import java.util.Map;
import java.util.List;
import java.util.Iterator;

/**
 * This pipeline processor returns one of two values, depending on whether
 * or not an order contains any payment group with an associated requisition
 * number.  Pipeline chains can use this processor to branch and treat
 * requisition-based orders differently from other orders (e.g., to
 * automatically require approval of orders that specify requisition
 * numbers).
 * <p>
 * This processor can also add an error message to the pipeline result
 * object in cases where the order specifies a requisition number, which
 * allows the {@link atg.commerce.order.processor.ProcExecuteChain
 * ProcExecuteChain} processor to return a different value to an enclosing
 * pipeline chain.
 *
 * @author Dynamo Business Commerce Solution Set Team
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/approval/ProcCheckRequisitionNumbers.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ProcCheckRequisitionNumbers
  extends GenericService
  implements PipelineProcessor
{
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/approval/ProcCheckRequisitionNumbers.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------

  /**
   * Iterate over payment groups in an order, returning
   * <code>requisitionUsedValue</code> if any payment group in
   * the order specifies a requisition number, and returning
   * <code>requisitionNotUsedValue</code> and optionally
   * adding an error to the pipeline result otherwise.  
   **/
  
  public int runProcess(Object pParam, PipelineResult pResult)
    throws InvalidParameterException
  {
    Map params = (Map)pParam;
    Order order = null;
    List paymentGroupList;
    
    // Get the order object from the pipeline args and verify that
    // it is non-null and is of the correct type, and that it contains
    // at least one payment group.

    try {
      order = (Order)params.get(ApprovalConstants.ORDER);      
      if (order == null)
        throw new InvalidParameterException(Constants.INVALID_ORDER_PARAMETER);
    }
    catch (ClassCastException cce) {
      throw new InvalidParameterException(Constants.INVALID_ORDER_PARAMETER, cce);
    }

    if ((paymentGroupList = order.getPaymentGroups()) == null)
      return getRequisitionNotUsedValue();

    // Iterate over all payment groups in the order.  If any payment group
    // specifies a non-empty requisition number, we (optionally) add a sentinel
    // error object to the pipeline result and then return the "requisitions
    // were used" value.  

    Iterator paymentGroups = paymentGroupList.iterator();

    while (paymentGroups.hasNext())
    {
      PaymentGroup pg = (PaymentGroup)paymentGroups.next();
      boolean usedRequisition;

      // Emit some debugging messages so we can see what's going on.
      
      if (isBlank(pg.getRequisitionNumber()))
      {
        usedRequisition = false;
        if (isLoggingDebug())
          logDebug("Payment group " + pg.getId() + " specified no requisition number");
      }
      else
      {
        usedRequisition = true;
        if (isLoggingDebug())
          logDebug("Payment group " + pg.getId() + " specified requisition number " + pg.getRequisitionNumber());
      }

      // If the order used requisition numbers, add the optional error
      // object to the pipeline result and return the appropriate return
      // value.
      
      if (usedRequisition)
      {
        if (isRequisitionUsedAddsPipelineError())
          pResult.addError(pg.getId(), getRequisitionUsedPipelineMessage());
        return getRequisitionUsedValue();
      }
    }

    // No payment groups included requisition numbers, so we return the "no
    // requisition used" value.

    if (isLoggingDebug())
      logDebug("No payment groups included requisition numbers.");
    return getRequisitionNotUsedValue();
  }
  

  //---------------------------------------------------------------------------

  /**
   * Return an array of possible return values for this processor.  The
   * array will contain two entries, whose values are specified by
   * <code>requisitionUsedValue</code> and <code>requisitionNotUsedValue</code>.
   **/
  
  public int[] getRetCodes()
  {
    return new int[] {
      getRequisitionUsedValue(),
      getRequisitionNotUsedValue()
    };
  }
  

  //---------------------------------------------------------------------------

  /**
   * Return true if the string specified by pStr is null, an empty
   * string, or consists entirely of whitespace.
   **/
  
  protected boolean isBlank(String pStr)
  {
    return (pStr == null || pStr.length() == 0 || pStr.trim().length() == 0);
  }
  
  //---------------------------------------------------------------------------
  // property: requisitionUsedValue
  //---------------------------------------------------------------------------

  int mRequisitionUsedValue = STOP_CHAIN_EXECUTION_AND_COMMIT;

  /**
   * Set the value to return if an order does contain requisition numbers
   * associated with any of its payment groups.
   **/
  
  public void setRequisitionUsedValue(int pRequisitionUsedValue) {
    mRequisitionUsedValue = pRequisitionUsedValue;
  }

  /**
   * Set the value to return if an order does contain requisition numbers
   * associated with any of its payment groups.  This value defaults to
   * STOP_CHAIN_EXECUTION_AND_COMMENT (i.e., to zero).
   **/
  
  public int getRequisitionUsedValue() {
    return mRequisitionUsedValue;
  }

  //---------------------------------------------------------------------------
  // property: requisitionNotUsedValue
  //---------------------------------------------------------------------------

  int mRequisitionNotUsedValue = 1;

  /**
   * Set the value to return if an order does not contain requisition numbers
   * in any of its payment groups.
   **/
  
  public void setRequisitionNotUsedValue(int pRequisitionNotUsedValue) {
    mRequisitionNotUsedValue = pRequisitionNotUsedValue;
  }

  /**
   * Get the value to return if an order does not contain requisition numbers
   * in any of its payment groups.  This value defaults to 1.
   **/
  
  public int getRequisitionNotUsedValue() {
    return mRequisitionNotUsedValue;
  }

  //---------------------------------------------------------------------------
  // property: requisitionUsedAddsPipelineError
  //---------------------------------------------------------------------------

  boolean mRequisitionUsedAddsPipelineError = false;

  /**
   * Specify whether to add an error object to the pipeline result if
   * an order contains requisition numbers in any of its payment groups.
   **/
  
  public void setRequisitionUsedAddsPipelineError(boolean pRequisitionUsedAddsPipelineError) {
    mRequisitionUsedAddsPipelineError = pRequisitionUsedAddsPipelineError;
  }

  /**
   * Query whether to add an error object to the pipeline result if
   * an order contains requisition numbers in any of its payment groups.
   * The default value is false.
   **/

  public boolean isRequisitionUsedAddsPipelineError() {
    return mRequisitionUsedAddsPipelineError;
  }

  //---------------------------------------------------------------------------
  // property: requisitionUsedPipelineMessage
  //---------------------------------------------------------------------------

  String mRequisitionUsedPipelineMessage = "requisition number used";

  /**
   * Ste the string to use as the error object if
   * <code>requisitionUsedAddsPipelineError</code> is true and the order
   * contains requisition numbers in any of its payment groups.
   **/
  
  public void setRequisitionUsedPipelineMessage(String pRequisitionUsedPipelineMessage) {
    mRequisitionUsedPipelineMessage = pRequisitionUsedPipelineMessage;
  }


  /**
   * Get the string to use as the error object if
   * <code>requisitionUsedAddsPipelineError</code> is true and the order
   * contains requisition numbers in any of its payment groups.
   **/
  
  public String getRequisitionUsedPipelineMessage() {
    return mRequisitionUsedPipelineMessage;
  }
}
