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

import atg.service.pipeline.*;
import atg.nucleus.logging.ApplicationLoggingImpl;

/**
 * This processor executes a chain from within a processor of another chain. The pipelineManager
 * property is the PipelineManager that the chain, specified by the chainToRun property, exists in.
 *
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcExecuteChain.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProcExecuteChain extends ApplicationLoggingImpl implements PipelineProcessor {
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcExecuteChain.java#2 $$Change: 651448 $";

  private final int SUCCESS = 1;

  //-----------------------------------------------
  public ProcExecuteChain() {
  }

  //-----------------------------------------------
  /**
   * Returns the valid return codes
   * 1 - The processor completed
   * @return an integer array of the valid return codes.
   */
  public int[] getRetCodes()
  {
    int[] ret = {getSuccessReturnValue(), getFailureReturnValue()};
    return ret;
  }

  //-------------------------------------
  // property: successReturnValue
  //-------------------------------------
  private int mSuccessReturnValue = SUCCESS;

  /**
   * Returns property successReturnValue
   *
   * @return returns property successReturnValue
   */
  public int getSuccessReturnValue() {
    return mSuccessReturnValue;
  }

  /**
   * Sets property successReturnValue
   *
   * @param pSuccessReturnValue the value to set for property successReturnValue
   */
  public void setSuccessReturnValue(int pSuccessReturnValue) {
    mSuccessReturnValue = pSuccessReturnValue;
  }

  //-------------------------------------
  // property: failureReturnValue
  //-------------------------------------
  private int mFailureReturnValue = STOP_CHAIN_EXECUTION_AND_ROLLBACK;

  /**
   * Returns property failureReturnValue
   *
   * @return returns property failureReturnValue
   */
  public int getFailureReturnValue() {
    return mFailureReturnValue;
  }

  /**
   * Sets property failureReturnValue
   *
   * @param pFailureReturnValue the value to set for property failureReturnValue
   */
  public void setFailureReturnValue(int pFailureReturnValue) {
    mFailureReturnValue = pFailureReturnValue;
  }

  //-------------------------------------
  // property: pipelineManager
  //-------------------------------------
  private PipelineManager mPipelineManager = null;

  /**
   * Returns the PipelineManager
   */
  public PipelineManager getPipelineManager() {
    return mPipelineManager;
  }

  /**
   * Sets the PipelineManager
   */
  public void setPipelineManager(PipelineManager pPipelineManager) {
    mPipelineManager = pPipelineManager;
  }

  //-------------------------------------
  // property: chainToRun
  //-------------------------------------
  private String mChainToRun = null;

  /**
   * Returns the chainToRun
   */
  public String getChainToRun() {
    return mChainToRun;
  }

  /**
   * Sets the chainToRun
   */
  public void setChainToRun(String pChainToRun) {
    mChainToRun = pChainToRun;
  }

  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcExecuteChain";

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

  //-----------------------------------------------
  /**
   * This method executes a chain from within a processor of another chain. The pipelineManager
   * property is the PipelineManager that the chain, specified by the chainToRun property, exists in.
   *
   * This method requires that an Order and optionally a Locale object be supplied
   * in pParam in a HashMap. Use the PipelineConstants class' static members to key
   * the objects in the HashMap.
   *
   * @param pParam a HashMap which must contain an Order and optionally a Locale object
   * @param pResult a PipelineResult object which stores any information which must
   *                be returned from this method invokation
   * @return an integer specifying the processor's return code
   * @exception Exception throws any exception back to the caller
   * @see atg.service.pipeline.PipelineProcessor#runProcess(Object, PipelineResult)
   */
  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    if (isLoggingDebug())
      logDebug("Executing chain " + getChainToRun());

    PipelineResult result = getPipelineManager().runProcess(getChainToRun(), pParam);

    if (result.hasErrors())
    {
      if (isLoggingDebug()) {
        logDebug("Found error objects in pipeline result -- returning value: " + getFailureReturnValue());
        logDebug("Errors: " + result.getErrors());
      }
      pResult.copyInto(result);
      return getFailureReturnValue();
    }
    else
    {
      if (isLoggingDebug())
        logDebug("No error objects in pipeline result -- returning value: " + getSuccessReturnValue());
      return getSuccessReturnValue();
    }
  }
}
