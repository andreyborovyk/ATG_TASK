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

package atg.b2bcommerce.payment.invoicerequest.processor;

import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.*;
import atg.commerce.payment.*;
import atg.commerce.payment.processor.*;

import atg.payment.*;
import atg.payment.invoicerequest.*;

/**
 * This pipeline processor element is called to authorize, debit,
 * and credit InvoiceRequest payment groups.  It calls through to
 * an InvoiceRequestProcessor object to perform these operations.
 *
 * @author Matt Landau
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/payment/invoicerequest/processor/ProcProcessInvoiceRequest.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.payment.invoicerequest.InvoiceRequestProcessor
 */

public class ProcProcessInvoiceRequest
  extends ProcProcessPaymentGroup
{
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/payment/invoicerequest/processor/ProcProcessInvoiceRequest.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------
  // property: InvoiceRequestProcessor
  //---------------------------------------------------------------------------

  InvoiceRequestProcessor mInvoiceRequestProcessor;

  /**
   * Set the invoice request processor that will actually be used
   * to authorize, debit, and credit invoice request payment groups.
   **/
  
  public void setInvoiceRequestProcessor(InvoiceRequestProcessor pInvoiceRequestProcessor) {
    mInvoiceRequestProcessor = pInvoiceRequestProcessor;
  }

  /**
   * Get the invoice request processor that will actually be used
   * to authorize, debit, and credit invoice request payment groups.
   **/
  
  public InvoiceRequestProcessor getInvoiceRequestProcessor() {
    return mInvoiceRequestProcessor;
  }

  //---------------------------------------------------------------------------

  /**
   * Authorize billing against an InvoiceRequest payment group.  This
   * method simply calls through to the <code>invoiceRequestProcessor<code>
   * authorize method.
   *
   * @return
   *    The PaymentStatus object returned by the
   *    <code>invoiceRequestProcessor</code> authorize method.
   * @param pParams
   *    The argument dictionary passed to this pipeline processor.
   * @throws CommerceException
   *    If there is any error authorizing the payment group.
   **/
  
  public PaymentStatus authorizePaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException
  {
    InvoiceRequestInfo info = (InvoiceRequestInfo)pParams.getPaymentInfo();
    return getInvoiceRequestProcessor().authorize(info);
  }

  //---------------------------------------------------------------------------

  /**
   * Debit an InvoiceRequest payment group.  This method simply calls through
   * to the <code>invoiceRequestProcessor<code> debit method.
   *
   * @return
   *    The PaymentStatus object returned by the
   *    <code>invoiceRequestProcessor</code> debit method.
   * @param pParams
   *    The argument dictionary passed to this pipeline processor.
   * @throws CommerceException
   *    If there is any error debiting the payment group.
   **/

  public PaymentStatus debitPaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException
  {
    InvoiceRequestInfo info = (InvoiceRequestInfo)pParams.getPaymentInfo();
    PaymentGroup pg = pParams.getPaymentGroup();
    PaymentStatus authStatus = pParams.getPaymentManager().getLastAuthorizationStatus(pg);

    return getInvoiceRequestProcessor().debit(info, authStatus);
  }
  
  //---------------------------------------------------------------------------

  /**
   * Credit an InvoiceRequest payment group.  This method simply calls through
   * to the <code>invoiceRequestProcessor<code> credit method.
   *
   * @return
   *    The PaymentStatus object returned by the
   *    <code>invoiceRequestProcessor</code> credit method.
   * @param pParams
   *    The argument dictionary passed to this pipeline processor.
   * @throws CommerceException
   *    If there is any error crediting the payment group.
   **/

  public PaymentStatus creditPaymentGroup(PaymentManagerPipelineArgs pParams)
    throws CommerceException
  {
    InvoiceRequestInfo info = (InvoiceRequestInfo)pParams.getPaymentInfo();
    PaymentGroup pg = pParams.getPaymentGroup();
    PaymentStatus debitStatus = pParams.getPaymentManager().getLastDebitStatus(pg);

    return getInvoiceRequestProcessor().credit(info, debitStatus);
  }
}
