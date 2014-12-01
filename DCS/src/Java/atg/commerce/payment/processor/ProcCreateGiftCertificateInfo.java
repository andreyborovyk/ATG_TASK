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
import atg.payment.giftcertificate.*;

/**
 * This pipeline processor element is called to create generic
 * GiftCertificateInfo objects from instances of the GiftCertificate
 * payment group.  It places them into the pipeline argument dictionary so
 * that downstream pipeline processors can retrieve them by calling
 * <code>PaymentManagerPipelineArgs.getPaymentInfo()<code>.
 * <p>
 * This processor is designed so that the GiftCertificateInfo class can
 * easily be extended.  See
 * {@link #setGiftCertificateInfoClass "<code>setGiftCertificateInfoClass</code>"} and
 * {@link #addDataToGiftCertificateInfo "<code>addDataToGiftCertificateInfo</code>"}
 * for more information.
 *
 * @author Ashley J. Streb
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/processor/ProcCreateGiftCertificateInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 *
 * @see atg.payment.creditcard.GiftCertificateInfo
 * @see atg.payment.creditcard.GenericGiftCertificateInfo
 */

public class ProcCreateGiftCertificateInfo
  extends GenericService
  implements PipelineProcessor
{
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/payment/processor/ProcCreateGiftCertificateInfo.java#2 $$Change: 651448 $";
    
  /** The possible return value for this processor. **/
  public static final int SUCCESS = 1;

  //---------------------------------------------------------------------
  // property: GiftCertificateInfoClass

  String mGiftCertificateInfoClass = "atg.payment.giftcertificate.GenericGiftCertificateInfo";

  /**
   * Return the class to instantiate when creating a new GiftCertificateInfo
   * object.
   **/
  
  public String getGiftCertificateInfoClass() {
    return mGiftCertificateInfoClass;
  }

  /**
   * Specify the class to instantiate when creating a new GiftCertificateInfo
   * object.  If the <code>GenericGiftCertificateInfo</code> class is extended to
   * include more information, this property can be changed to reflect the
   * new class.
   **/
  
  public void setGiftCertificateInfoClass(String pGiftCertificateInfoClass) {
    mGiftCertificateInfoClass = pGiftCertificateInfoClass;
  }

  //---------------------------------------------------------------------------

  /**
   * This method populates the <code>GiftCertificateInfo</code> object with
   * data.  If the additional data is required, a subclass of
   * <code>GenericGiftCertificateInfo</code> can be created with additional
   * properties, the <code>giftCertificateInfoClass</code> property can be
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
   * @param pGiftCertificateInfo
   *    An object that holds information understood by the gift
   *    certificate payment processor.
   **/
  
  protected void addDataToGiftCertificateInfo(Order pOrder, 
                                              GiftCertificate pPaymentGroup,
                                              double pAmount,
                                              PaymentManagerPipelineArgs pParams,
                                              GenericGiftCertificateInfo pGiftCertificateInfo) 
  {
    pGiftCertificateInfo.setProfileId(pPaymentGroup.getProfileId());
    pGiftCertificateInfo.setGiftCertificateNumber(pPaymentGroup.getGiftCertificateNumber());
    pGiftCertificateInfo.setAmount(pAmount);
  }

  //---------------------------------------------------------------------------

  /**
   * Factory method to create a new GiftCertificateInfo object.  The class
   * that is created is that specified by the
   * <code>giftCertificateInfoClass</code> property, and must be a subclass
   * of <code>atg.payment.giftcertificate.GenericGiftCertificateInfo</code>.
   *
   * @return
   *    An object of the class specified by <code>giftCertificateInfoClass</code>
   * @throws Exception
   *    if any instantiation error occurs when creating the info object
   **/

  protected GenericGiftCertificateInfo getGiftCertificateInfo() 
    throws Exception
  {
    if (isLoggingDebug())
      logDebug("Making a new instance of type: " + getGiftCertificateInfoClass());

    GenericGiftCertificateInfo ggci = (GenericGiftCertificateInfo)
      Class.forName(getGiftCertificateInfoClass()).newInstance();
    
    return ggci;
  }

  //---------------------------------------------------------------------------

  /**
   * Generate a GiftCertificateInfo object of the class specified by
   * <code>giftCertificateInfoClass</code>, populate it with data from a
   * <code>GiftCertificate</code> payment group by calling
   * <code>addDataToGiftCertificateInfo</code>, and add it to the pipeline
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
   *    If any error occurs creating or populating the gift certificate info object.
   **/
  
  public int runProcess(Object pParam, PipelineResult pResult)
    throws Exception 
  {
    PaymentManagerPipelineArgs params = (PaymentManagerPipelineArgs)pParam;
    Order order = params.getOrder();
    GiftCertificate giftCertificate = (GiftCertificate)params.getPaymentGroup();
    double amount = params.getAmount();

    // create and populate credit card info class
    GenericGiftCertificateInfo ggci = getGiftCertificateInfo();
    addDataToGiftCertificateInfo(order, giftCertificate, amount, params, ggci);

    if (isLoggingDebug())
      logDebug("Putting GiftCertificateInfo object into pipeline: " + ggci.toString());

    params.setPaymentInfo(ggci);
    
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
