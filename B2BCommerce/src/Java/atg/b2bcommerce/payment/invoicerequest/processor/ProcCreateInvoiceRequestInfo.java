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

import atg.nucleus.GenericService;

import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.payment.*;
import atg.payment.invoicerequest.*;
import atg.b2bcommerce.payment.invoicerequest.*;

/**
 * This pipeline processor element is called to create generic
 * InvoiceRequestInfo objects from instances of the InvoiceRequest payment
 * group.  It places them into the pipeline argument dictionary so that
 * downstream pipeline processors can retrieve them by calling
 * <code>PaymentManagerPipelineArgs.getPaymentInfo()<code>.
 * <p>
 * This processor is designed so that the InvoiceRequestInfo class can
 * easily be extended.  See
 * {@link #setInvoiceRequestInfoClass "<code>setInvoiceRequestInfoClass</code>"} and
 * {@link "#addDataToInvoiceRequestInfo "<code>addDataToInvoiceRequestInfo</code>}
 * for more information.
 *
 * @author Matt Landau
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/payment/invoicerequest/processor/ProcCreateInvoiceRequestInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 * @see atg.payment.invoicerequest.InvoiceRequestInfo
 * @see atg.payment.invoicerequest.GenericInvoiceRequestInfo
 **/

public class ProcCreateInvoiceRequestInfo
  extends GenericService
  implements PipelineProcessor
{
  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/payment/invoicerequest/processor/ProcCreateInvoiceRequestInfo.java#2 $$Change: 651448 $";

  /** Return values for this processor. **/
  public static final int SUCCESS = 1;

  //---------------------------------------------------------------------------
  // property: InvoiceRequestInfoClass
  //---------------------------------------------------------------------------

  String mInvoiceRequestInfoClass = "atg.payment.invoicerequest.GenericInvoiceRequestInfo";

  /**
   * Specify the class to instantiate when creating a new InvoiceRequestInfo
   * object.  If the <code>GenericInvoiceRequestInfo</code> class is extended to
   * include more information, this property can be changed to reflect the
   * new class.
   **/
  
  public void setInvoiceRequestInfoClass(String pInvoiceRequestInfoClass) {
    mInvoiceRequestInfoClass = pInvoiceRequestInfoClass;
  }

  /**
   * Return the class to instantiate when creating a new InvoiceRequestInfo
   * object.
   **/
  
  public String getInvoiceRequestInfoClass() {
    return mInvoiceRequestInfoClass;
  }

  //---------------------------------------------------------------------------

  /**
   * This method populates the <code>InvoiceRequestInfo</code> object with
   * data.  If the additional data is required, a subclass of
   * <code>GenericInvoiceRequestInfo</code> can be created with additional
   * properties, the <code>invoiceRequestInfoClass</code> property can be
   * changed to specify the new class, and this method can be overridden to
   * add data for the new properties (or another pipeline processor could be
   * added after this processor to populate the additional properties).
   *
   * @param pOrder
   *    The order being paid for.
   * @param pPaymentGroup
   *    The payment group being processed.
   * @param pAmount
   *    The amount being authorized, debited, or credited
   * @param pParams
   *    The parameter dictionary passed to this pipeline processor
   * @param pInvoiceRequestInfo
   *    An object that holds information understood by the invoice
   *    request payment processor.
   */
  
  protected void addDataToInvoiceRequestInfo(
    Order pOrder, InvoiceRequest pPaymentGroup, double pAmount,
    PaymentManagerPipelineArgs pParams, GenericInvoiceRequestInfo pInvoiceRequestInfo)
  {
    pInvoiceRequestInfo.setPONumber(pPaymentGroup.getPONumber());
    pInvoiceRequestInfo.setBillingAddress(pPaymentGroup.getBillingAddress());
    pInvoiceRequestInfo.setPreferredFormat(pPaymentGroup.getPreferredFormat());
    pInvoiceRequestInfo.setPreferredDeliveryMode(pPaymentGroup.getPreferredDeliveryMode());
    pInvoiceRequestInfo.setBalanceDue(computeBalanceDue(pAmount, pPaymentGroup));
    pInvoiceRequestInfo.setPaymentDueDate(pPaymentGroup.getPaymentDueDate());
    pInvoiceRequestInfo.setPaymentNetDays(pPaymentGroup.getPaymentNetDays());
    pInvoiceRequestInfo.setPaymentDiscountDays(pPaymentGroup.getPaymentDiscountDays());
    pInvoiceRequestInfo.setPaymentDiscountPercent(pPaymentGroup.getPaymentDiscountPercent());
    pInvoiceRequestInfo.setPaymentGroup(pPaymentGroup);
    pInvoiceRequestInfo.setOrder(pOrder);
  }

  //---------------------------------------------------------------------------

  /**
   * Compute the balance due on the invoice by looking at the payment group
   * original amount, subtracting anything that's already been debited, and
   * adding back in any credits applied to the payment group.
   * <p>
   * The assumption here is that credits don't reduce the total amount
   * billed to the payment group, they only affect the outstanding balance.
   * If one wants to reduce the total amount billed, one would presumably
   * change the amount field in the payment group.  If this assumption is
   * incorrect and a different balance due behavior is desired, applications
   * can subclass this class and replace the computeBalanceDue method to
   * return a different value.
   *
   * @param pAmount
   *    The amount value passed by the payment manager as the amount to
   *    authorize, debit, or credit.  This value is not used by the default
   *    implementation of computeBalanceDue but is included in the method
   *    signature in case subclassed versions of the method require it.
   * @param pPmt
   *    The payment group for which an InvoiceRequestInfo object is
   *    being created.
   * @return
   *    The amount to list as "balance due" in the invoice request info.
   **/

  protected Double computeBalanceDue(double pAmount, PaymentGroup pPmt)
  {
    return new Double(pPmt.getAmount() - pPmt.getAmountDebited() + pPmt.getAmountCredited());
  }
  
  //---------------------------------------------------------------------------

  /**
   * Factory method to create a new InvoiceRequestInfo object.  The class
   * that is created is that specified by the
   * <code>invoiceRequestInfoClass</code> property, and must be a subclass
   * of <code>atg.payment.invoice.GenericInvoiceRequestInfo</code>.
   *
   * @return
   *    An object of the class specified by <code>invoiceRequestInfoClass</code>
   * @throws Exception
   *    if any instantiation error occurs when creating the info object
   **/

  protected GenericInvoiceRequestInfo getInvoiceRequestInfo()
    throws Exception
  {
    String className = getInvoiceRequestInfoClass();
    
    if (isLoggingDebug())
      logDebug("Making a new instance of type: " + className);

    GenericInvoiceRequestInfo info = (GenericInvoiceRequestInfo)Class.forName(className).newInstance();
    return info;
  }

  //---------------------------------------------------------------------------

  /**
   * Generate an InvoiceRequestInfo object of the class specified by
   * <code>invoiceRequestInfoClass</code>, populate it with data from an
   * <code>InvoiceRequest</code> payment group by calling
   * <code>addDataToInvoiceRequestInfo</code>, and add it to the pipeline
   * argument dictionary so that downstream pipeline processors can access
   * it.
   *
   * @param pParam
   *    Parameter dictionary of type PaymentManagerPipelineArgs.  
   * @param pResult
   *    Pipeline result object, not used by this method.
   * @return
   *    An integer value used to determine which pipeline processor is called next.
   * @throws Exception
   *    If any error occurs creating or populating the invoice request info object.
   **/
  
  public int runProcess(Object pParam, PipelineResult pResult)
    throws Exception 
  {
    PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs)pParam;
    Order order = params.getOrder();
    InvoiceRequest invoiceRequest = (InvoiceRequest)params.getPaymentGroup();
    double amount = params.getAmount();

    GenericInvoiceRequestInfo info = getInvoiceRequestInfo();
    addDataToInvoiceRequestInfo(order, invoiceRequest, amount, params, info);

    if (isLoggingDebug())
      logDebug("Adding InvoiceRequestInfo object to pipeline args: " + info.toString());

    params.setPaymentInfo(info);
    
    return SUCCESS;
  }

  //---------------------------------------------------------------------------

  /**
   * Return the possible return values for this processor.  This processor
   * always returns a success code.
   **/
  
  public int[] getRetCodes() {
    int retCodes[] = {SUCCESS};
    return retCodes;
  }
}
