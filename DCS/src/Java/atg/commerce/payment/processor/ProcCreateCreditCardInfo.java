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

import atg.nucleus.GenericService;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;
import atg.commerce.order.*;
import atg.commerce.payment.*;
import atg.payment.creditcard.*;

/**
 * This pipeline processor element is called to create generic
 * CreditCardInfo objects from instances of the CreditCard
 * payment group.  It places them into the pipeline argument dictionary so
 * that downstream pipeline processors can retrieve them by calling
 * <code>PaymentManagerPipelineArgs.getPaymentInfo()</code>.
 * <p>
 * This processor is designed so that the CreditCardInfo class can easily be
 * extended.  See
 * {@link #setCreditCardInfoClass "<code>setCreditCardInfoClass</code>"} and
 * {@link #addDataToCreditCard "<code>addDataToCreditCardInfo</code>"}
 * for more information.
 *
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/processor/ProcCreateCreditCardInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 * @see atg.payment.creditcard.CreditCardInfo
 * @see atg.payment.creditcard.GenericCreditCardInfo
 */

public class ProcCreateCreditCardInfo
  extends GenericService
  implements PipelineProcessor
{
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/processor/ProcCreateCreditCardInfo.java#2 $$Change: 651448 $";
    
  /** The possible return value for this processor. **/
  public static final int SUCCESS = 1;

  //---------------------------------------------------------------------
  // property: CreditCardInfoClass
 
  String mCreditCardInfoClass = "atg.payment.creditcard.GenericCreditCardInfo";

  /**
   * Return the class to instantiate when creating a new CreditCardInfo object.
   **/
  
  public String getCreditCardInfoClass() {
    return mCreditCardInfoClass;
  }

  /**
   * Specify the class to instantiate when creating a new CreditCardInfo
   * object.  If the <code>GenericCreditCardInfo</code> class is extended to
   * include more information, this property can be changed to reflect the
   * new class.
   **/
  
  public void setCreditCardInfoClass(String pCreditCardInfoClass) {
    mCreditCardInfoClass = pCreditCardInfoClass;
  }

  //---------------------------------------------------------------------------

  /**
   * This method populates the <code>CreditCardInfo</code> object with
   * data.  If the additional data is required, a subclass of
   * <code>GenericCreditCardInfo</code> can be created with additional
   * properties, the <code>creditCardInfoClass</code> property can be
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
   * @param pCreditCardInfo
   *    An object that holds information understood by the credit
   *    card payment processor.
   **/
  
  protected void addDataToCreditCardInfo(Order pOrder, 
                                         CreditCard pPaymentGroup,
                                         double pAmount,
                                         PaymentManagerPipelineArgs pParams,
                                         GenericCreditCardInfo pCreditCardInfo)
  {
    pCreditCardInfo.setAmount(pAmount);
    pCreditCardInfo.setBillingAddress(pPaymentGroup.getBillingAddress());
    pCreditCardInfo.setCreditCardNumber(pPaymentGroup.getCreditCardNumber());
    pCreditCardInfo.setCreditCardType(pPaymentGroup.getCreditCardType());
    pCreditCardInfo.setCurrencyCode(pPaymentGroup.getCurrencyCode());
    pCreditCardInfo.setExpirationMonth(pPaymentGroup.getExpirationMonth());
    pCreditCardInfo.setExpirationDayOfMonth(pPaymentGroup.getExpirationDayOfMonth());
    pCreditCardInfo.setExpirationYear(pPaymentGroup.getExpirationYear());
    pCreditCardInfo.setPaymentId(pPaymentGroup.getId());
    pCreditCardInfo.setOrder(pOrder);

    if (pCreditCardInfo instanceof CreditCardInfoWithSecurityCode)
      pCreditCardInfo.setSecurityCode(pPaymentGroup.getCardVerificationNumber());
  }

  //---------------------------------------------------------------------------

  /**
   * Factory method to create a new CreditCardInfo object.  The class
   * that is created is that specified by the
   * <code>creditCardInfoClass</code> property, and must be a subclass
   * of <code>atg.payment.creditcard.GenericCreditCardInfo</code>.
   *
   * @return
   *    An object of the class specified by <code>creditCardInfoClass</code>
   * @throws Exception
   *    if any instantiation error occurs when creating the info object
   **/

  protected GenericCreditCardInfo getCreditCardInfo() 
    throws Exception
  {
    if (isLoggingDebug())
      logDebug("Making a new instance of type: " + getCreditCardInfoClass());

    GenericCreditCardInfo cci = (GenericCreditCardInfo)
      Class.forName(getCreditCardInfoClass()).newInstance();
    
    return cci;
  }

  //---------------------------------------------------------------------------

  /**
   * Generate a CreditCardInfo object of the class specified by
   * <code>creditCardInfoClass</code>, populate it with data from a
   * <code>CreditCard</code> payment group by calling
   * <code>addDataToCreditCardInfo</code>, and add it to the pipeline
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
   *    If any error occurs creating or populating the credit card info object.
   **/
  
  public int runProcess(Object pParam, PipelineResult pResult)
    throws Exception 
  {
    PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs)pParam;
    Order order = params.getOrder();
    CreditCard creditCard = (CreditCard)params.getPaymentGroup();
    double amount = params.getAmount();

    // create and populate credit card info class
    GenericCreditCardInfo gcci = getCreditCardInfo();
    addDataToCreditCardInfo(order, creditCard, amount, params, gcci);

    if (isLoggingDebug())
      logDebug("Putting CreditCardInfo object into pipeline: " + gcci.toString());

    params.setPaymentInfo(gcci);
    
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
