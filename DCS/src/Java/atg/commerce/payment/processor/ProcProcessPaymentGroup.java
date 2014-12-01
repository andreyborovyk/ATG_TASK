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

package atg.commerce.payment.processor;

// Java classes
import java.util.*;
import java.text.MessageFormat;
import atg.payment.*;

// DAS classes
import atg.service.pipeline.*;
import atg.nucleus.*;

// DPS classes

// DSS classes

// DCS classes
import atg.commerce.order.*;
import atg.commerce.payment.*;
import atg.commerce.payment.Constants;
import atg.commerce.*;

/**
 * This is the base class which processors that would like to perform
 * auth/debit/credit actions on a particular payment group can extend
 * from.  The intention is that this class should provide utility methods
 * which would be applicable to all PaymentGroup auth/debit/credits.
 * Concrete uses of this class can be seen in ProcProcessCreditCard.
 *
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/processor/ProcProcessPaymentGroup.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.commerce.payment.processor.ProcProcessCreditCard
 * @see atg.commerce.payment.processor.ProcProcessGiftCertificate
 * @see atg.commerce.payment.processor.ProcProcessStoreCredit
 */

public abstract class ProcProcessPaymentGroup
  extends GenericService
  implements PipelineProcessor
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/processor/ProcProcessPaymentGroup.java#2 $$Change: 651448 $";

  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  /**
   * The possible return value for this processor.
   */
  public static final int SUCCESS = 1;

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------
  
  //--------------------------------------------------
  // Properties
  //--------------------------------------------------

  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  /**
   * Empty Constructor.
   */
  public ProcProcessPaymentGroup() {
  }

  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------
  
  /**
   * Method which can distribute a particular chain invocation
   * to the correct method to invoke.  This is useful in the case
   * where a single processor is designed to perform all
   * the possible payment group actions. (auth, debit, and credit).
   * The dispatching to the appropriate method is handled by
   * inspecting the pProcessorAction and determing what
   * method to call.  The mapping is as follows:
   *
   * <ul>
   *  <li>Constants.AUTH_ACTION <---> authPaymentGroup()
   *  <li>Constants.DEBIT_ACTION <---> debitPaymentGroup()
   *  <li>Constants.CREDIT_ACTION <---> creditPaymentGroup()
   * </ul>
   *
   * Each of the above methods shoudl return a PaymentStatus
   * object.  This PaymentStatus object is then placed into
   * the <code>pParams</code> map using <code>PipelineConstants.PAYMENT_STATUS
   * </code> as the key.
   *
   * @param pProcessorAction the action to perform.  Should match
   * a value in atg.commerce.payment.Constants
   * @param pParams map of params handed to pipeline chain
   * @exception CommerceException if an error occurs
   */
  protected void invokeProcessorAction(PaymentManagerAction pProcessorAction,
                                       PaymentManagerPipelineArgs pParams) 
    throws CommerceException
  {
    PaymentStatus status = null;

    if (isLoggingDebug())
      logDebug("Obtained processorAction with: " + pProcessorAction);

    if (pProcessorAction == PaymentManagerAction.AUTHORIZE)
      status = authorizePaymentGroup(pParams);
    else if (pProcessorAction == PaymentManagerAction.DEBIT)
      status = debitPaymentGroup(pParams);
    else if (pProcessorAction == PaymentManagerAction.CREDIT)
      status = creditPaymentGroup(pParams);
    else if (pProcessorAction == PaymentManagerAction.DECREASE_AUTH_AMT)
      status = decreaseAuthorizationForPaymentGroup(pParams);
    else
      throw new CommerceException("Invalid processor action specified: " + pProcessorAction);

    pParams.setPaymentStatus(status);
  }

  
  /**
   * Method called to perform a decrease in authorization for a payment group.
   * <p>
   * This implementations does nothing by default and returns null. 
   *
   * @param pParams params handed to pipeline processor
   * @return a <code>PaymentStatus</code> value
   * @exception CommerceException if an error occurs
   */
  public PaymentStatus decreaseAuthorizationForPaymentGroup(PaymentManagerPipelineArgs pParams)
  throws CommerceException
  {
    return null;
  }
  /**
   * Method called to perform an authorization of a PaymentGroup.
   * Needs to be implemented by subclasses.
   *
   * @param pParams params handed to pipeline processor
   * @return a <code>PaymentStatus</code> value
   * @exception CommerceException if an error occurs
   */
  public abstract PaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException;

  /**
   * Method called to perform a debit of a PaymentGroup.
   * Needs to be implemented by subclasses.
   *
   * @param pParams params handed to pipeline processor
   * @return a <code>PaymentStatus</code> value
   * @exception CommerceException if an error occurs
   */
  public abstract PaymentStatus debitPaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException;

  /**
   * Method called to perform a credit of a PaymentGroup.
   * Needs to be implemented by subclasses.
   *
   * @param pParams params handed to pipeline processor
   * @return a <code>PaymentStatus</code> value
   * @exception CommerceException if an error occurs
   */
  public abstract PaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException;


  /**
   * This method is the one called by the pipelin chain.  Its
   * purpose is to extract out a <code>PipelineConstants.PAYMENT_PROC_ACTION
   * </code> value from the pipeline and then call 
   * <code>invokeProcessorAction() with this values.
   *
   * <P>Classes will have to subclass this class in order to provide
   * meaningful implementations of the auth, debit and credit methods.
   *
   * @param pParam an <code>Object</code> value
   * @param pResult a <code>PipelineResult</code> value
   * @return an <code>int</code> value
   * @exception Exception if an error occurs
   */
  public int runProcess(Object pParam, PipelineResult pResult)
    throws Exception 
  {
    PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs)pParam;
    PaymentManagerAction action = params.getAction();
    try {
      invokeProcessorAction(action, params);
    }
    catch(CommerceException e) {
      logError(e);
      pResult.addError("ProcProcessPaymentGroupFailed", e);
      return STOP_CHAIN_EXECUTION;
    }

    return SUCCESS;
  }
  
 /**
   * The possible return values for this processor.
   *
   * @return an <code>int[]</code> value.  This is
   * just the <code>SUCCESS</code> value.
   */
  public int[] getRetCodes() {
    int retCodes[] = {SUCCESS};
    return retCodes;
  }
}
