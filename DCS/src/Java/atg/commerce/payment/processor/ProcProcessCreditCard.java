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

package atg.commerce.payment.processor;

import atg.commerce.order.PaymentGroup;
import atg.commerce.*;
import atg.commerce.payment.*;

import atg.payment.*;
import atg.payment.creditcard.*;

/**
 * This pipeline processor element is called to authorize, debit,
 * and credit CreditCard payment groups.  It calls through to
 * a CreditCardProcessor object to perform these operations.
 *
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/processor/ProcProcessCreditCard.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.payment.creditcard.CreditCardProcessor
 */

public class ProcProcessCreditCard
  extends ProcProcessPaymentGroup
{
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/processor/ProcProcessCreditCard.java#2 $$Change: 651448 $";
    
  //---------------------------------------------------------------------------
  /**
   * Authorize billing against a CreditCard payment group.  This
   * method simply calls through to the <code>creditCardProcessor<code>
   * authorize method.
   *
   * @return
   *    The PaymentStatus object returned by the
   *    <code>creditCardProcessor</code> authorize method.
   * @param pParams
   *    The argument dictionary passed to this pipeline processor.
   * @throws CommerceException
   *    If there is any error authorizing the payment group.
   **/

  public PaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException
  {
    CreditCardInfo cci = (CreditCardInfo)pParams.getPaymentInfo();
    return pParams.getPaymentManager().getCreditCardProcessor().authorize(cci);
  }

  //---------------------------------------------------------------------------

  /**
   * Debit a CreditCard payment group.  This method simply calls through to
   * the <code>creditCardProcessor<code> debit method.
   *
   * @return
   *    The PaymentStatus object returned by the
   *    <code>creditCardProcessor</code> debit method.
   * @param pParams
   *    The argument dictionary passed to this pipeline processor.
   * @throws CommerceException
   *    If there is any error debiting the payment group.
   **/

  public PaymentStatus debitPaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException
  {
    CreditCardInfo cci = (CreditCardInfo)pParams.getPaymentInfo();
    PaymentGroup pg = pParams.getPaymentGroup();
    PaymentStatus authStatus = pParams.getPaymentManager().getLastAuthorizationStatus(pg);

    try {
      return pParams.getPaymentManager().getCreditCardProcessor().debit(cci, (CreditCardStatus)authStatus);
    }
    catch (ClassCastException cce) {
      throw new PaymentException(Constants.INVALID_AUTH_STATUS);
    }
  }
  
  //---------------------------------------------------------------------------

  /**
   * Credit a CreditCard payment group.  This method simply calls through to
   * the <code>creditCardProcessor<code> credit method.
   *
   * @return
   *    The PaymentStatus object returned by the
   *    <code>creditCardProcessor</code> credit method.
   * @param pParams
   *    The argument dictionary passed to this pipeline processor.
   * @throws CommerceException
   *    If there is any error crediting the payment group.
   **/

  public PaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException
  {
    CreditCardInfo cci = (CreditCardInfo)pParams.getPaymentInfo();
    PaymentGroup pg = pParams.getPaymentGroup();
    PaymentStatus debitStatus = pParams.getPaymentManager().getLastDebitStatus(pg);

    try {
      return pParams.getPaymentManager().getCreditCardProcessor().credit(cci, (CreditCardStatus)debitStatus);
    }
    catch (ClassCastException cce) {
      throw new PaymentException(Constants.INVALID_DEBIT_STATUS);
    }
  }
  
  
  /**
   * Method called to perform a decrease in authorization for a credit card.
   * <p>
   * Calls the decreaseAuthorization on the credit card processor returned by
   * the payment manager if it is an instance of <code>DecreaseCreditCardAuthorizationProcessor</code> 
   * @see PaymentManager#getCreditCardProcessor()
   * @see DecreaseCreditCardAuthorizationProcessor 
   * @param pParams params handed to pipeline processor
   * @return a <code>PaymentStatus</code> value
   * @exception CommerceException if an error occurs
   */
  public PaymentStatus decreaseAuthorizationForPaymentGroup(PaymentManagerPipelineArgs pParams)
  throws CommerceException
  {
    CreditCardProcessor ccp = pParams.getPaymentManager().getCreditCardProcessor();
    if(ccp instanceof DecreaseCreditCardAuthorizationProcessor)
    {
      DecreaseCreditCardAuthorizationProcessor dccp = (DecreaseCreditCardAuthorizationProcessor)ccp;
      CreditCardInfo cci = (CreditCardInfo)pParams.getPaymentInfo();
      PaymentGroup pg = pParams.getPaymentGroup();
      PaymentStatus authStatus = pParams.getPaymentManager().getLastAuthorizationStatus(pg);
      return dccp.decreaseAuthorization(cci,(CreditCardStatus)authStatus);
    }
    return null;
  }
  
}
