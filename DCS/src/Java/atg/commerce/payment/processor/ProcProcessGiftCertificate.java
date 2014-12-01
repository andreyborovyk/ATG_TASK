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
import atg.payment.giftcertificate.*;

/**
 * This pipeline processor element is called to authorize, debit,
 * and credit GiftCertificate payment groups.  It calls through to
 * a GiftCertificateProcessor object to perform these operations.
 *
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/processor/ProcProcessGiftCertificate.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.payment.giftcertificate.GiftCertificateProcessor
 */

public class ProcProcessGiftCertificate
  extends ProcProcessPaymentGroup
{
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/processor/ProcProcessGiftCertificate.java#2 $$Change: 651448 $";
    
  //---------------------------------------------------------------------------
  /**
   * Authorize billing against a GiftCertificate payment group.  This
   * method simply calls through to the <code>giftCertificateProcessor<code>
   * authorize method.
   *
   * @return
   *    The PaymentStatus object returned by the
   *    <code>giftCertificateProcessor</code> authorize method.
   * @param pParams
   *    The argument dictionary passed to this pipeline processor.
   * @throws CommerceException
   *    If there is any error authorizing the payment group.
   **/

  public PaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException
  {
    GiftCertificateInfo gci = (GiftCertificateInfo)pParams.getPaymentInfo();
    return pParams.getPaymentManager().getGiftCertificateProcessor().authorize(gci);
  }

  //---------------------------------------------------------------------------

  /**
   * Debit a GiftCertificate payment group.  This method simply calls through to
   * the <code>giftCertificateProcessor<code> debit method.
   *
   * @return
   *    The PaymentStatus object returned by the
   *    <code>giftCertificateProcessor</code> debit method.
   * @param pParams
   *    The argument dictionary passed to this pipeline processor.
   * @throws CommerceException
   *    If there is any error debiting the payment group.
   **/

  public PaymentStatus debitPaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException
  {
    GiftCertificateInfo gci = (GiftCertificateInfo)pParams.getPaymentInfo();
    PaymentGroup pg = pParams.getPaymentGroup();
    PaymentStatus authStatus = pParams.getPaymentManager().getLastAuthorizationStatus(pg);

    try {
      return pParams.getPaymentManager().getGiftCertificateProcessor().debit(gci, (GiftCertificateStatus)authStatus);
    }
    catch (ClassCastException cce) {
      throw new PaymentException(Constants.INVALID_AUTH_STATUS);
    }
  }

  //---------------------------------------------------------------------------

  /**
   * Credit a GiftCertificate payment group.  This method simply calls through to
   * the <code>giftCertificateProcessor<code> credit method.
   *
   * @return
   *    The PaymentStatus object returned by the
   *    <code>giftCertificateProcessor</code> credit method.
   * @param pParams
   *    The argument dictionary passed to this pipeline processor.
   * @throws CommerceException
   *    If there is any error crediting the payment group.
   **/

  public PaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException
  {
    GiftCertificateInfo gci = (GiftCertificateInfo)pParams.getPaymentInfo();
    PaymentGroup pg = pParams.getPaymentGroup();
    PaymentStatus debitStatus = pParams.getPaymentManager().getLastDebitStatus(pg);

    try {
      return pParams.getPaymentManager().getGiftCertificateProcessor().credit(gci, (GiftCertificateStatus)debitStatus);
    }
    catch (ClassCastException cce) {
      throw new PaymentException(Constants.INVALID_DEBIT_STATUS);
    }
  }
  
  /**
   * Method called to perform a decrease in authorization for a gift certificate.
   * <p>
   * Calls the decreaseAuthorization on the store credit processor returned by
   * the payment manager if it is an instance of <code>DecreaseCreditCardAuthorizationProcessor</code> 
   * @see PaymentManager#getStoreCreditProcessor()
   * @see DecreaseGiftCertificateAuthorizationProcessor 
   * @param pParams params handed to pipeline processor
   * @return a <code>PaymentStatus</code> value
   * @exception CommerceException if an error occurs
   */
  public PaymentStatus decreaseAuthorizationForPaymentGroup(PaymentManagerPipelineArgs pParams)
  throws CommerceException
  {
    GiftCertificateProcessor gcp = pParams.getPaymentManager().getGiftCertificateProcessor();
    if(gcp instanceof DecreaseGiftCertificateAuthorizationProcessor)
    {
      DecreaseGiftCertificateAuthorizationProcessor dgcp = (DecreaseGiftCertificateAuthorizationProcessor)gcp;
      GiftCertificateInfo gci = (GiftCertificateInfo)pParams.getPaymentInfo();
      PaymentGroup pg = pParams.getPaymentGroup();
      PaymentStatus authStatus = pParams.getPaymentManager().getLastAuthorizationStatus(pg);
      return dgcp.decreaseAuthorization(gci,(GiftCertificateStatus)authStatus);
    }
    return null;
  }
  
}
