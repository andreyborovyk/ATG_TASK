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

package atg.b2bcommerce.order.processor;

import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.processor.BillingAddrValidator;
import atg.commerce.order.processor.ValidatePaymentGroupPipelineArgs;
import atg.b2bcommerce.payment.invoicerequest.InvoiceRequest;

import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Validate a single instance of the InvoiceRequest payment group at checkout
 * time.  This processor verifies that all required fields in an InvoiceRequest
 * are present and that they have acceptable values, to the extent that this
 * can be determined in a generic way.
 *
 * @author Matt Landau 
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/processor/ProcValidateInvoiceRequest.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ProcValidateInvoiceRequest
  extends ApplicationLoggingImpl implements PipelineProcessor
{  
  public static String CLASS_VERSION =
      "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/processor/ProcValidateInvoiceRequest.java#2 $$Change: 651448 $";

  //---------------------------------------------------------------------------
  // Resource bundles
  
  private static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  private static final ResourceBundle sServerResources =
      ResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());

  private static final String USER_MSGS_RES_NAME = "atg.b2bcommerce.order.UserMessages";
  private static final ResourceBundle sUserResources =
      ResourceBundle.getBundle(USER_MSGS_RES_NAME, Locale.getDefault());

  //---------------------------------------------------------------------------
  // Validation Mode Constants
  private static final int REQUIRE_PO_NUMBER = 0;
  private static final int REQUIRE_REQUISITION_NUMBER = 1;
  private static final int REQUIRE_PO_AND_REQUISITION_NUMBER = 2;
  private static final int ACCEPT_PO_OR_REQUISITION_NUMBER = 3;
  
  //---------------------------------------------------------------------------
  // property: LoggingIdentifier

  String mLoggingIdentifier = "ProcValidateInvoiceRequest";

  /** Set the LoggingIdentifier used in log messages. **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /** Return the LoggingIdentifier used in log messages. **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //---------------------------------------------------------------------------
  // property: billingAddressValidator
  //---------------------------------------------------------------------------

  BillingAddrValidator mBillingAddressValidator;

  public void setBillingAddressValidator(BillingAddrValidator pBillingAddressValidator) {
    mBillingAddressValidator = pBillingAddressValidator;
  }

  public BillingAddrValidator getBillingAddressValidator() {
    return mBillingAddressValidator;
  }

  //---------------------------------------------------------------------------
  // property: InvoiceRequestValidationMode
  //---------------------------------------------------------------------------
  int mInvoiceRequestValidationMode = REQUIRE_PO_NUMBER;

  /**
   * Specifies the mode to use for validating an invoice request.
   * The following modes are supported:
   *
   * <ul>
   * <li> 0 - The InvoiceRequest must have a PONumber
   * <li> 1 - The InvoiceRequest must have a Requisition Number
   * <li> 2 - The InvoiceRequest must have both a PONumber and a Requisition Number
   * <li> 3 - The InvoiceRequest must have either a PONumber or a Requisition Number
   * </ul>
   *
   * The default mode is 0.
   */
  public void setInvoiceRequestValidationMode(int pInvoiceRequestValidationMode) {
    mInvoiceRequestValidationMode = pInvoiceRequestValidationMode;
  }

  /**
   * Queries the mode to use for validating an invoice request.
   */
  public int getInvoiceRequestValidationMode() {
    return mInvoiceRequestValidationMode;
  }

  //---------------------------------------------------------------------------

  /**
   * Returns the set of valid return codes for this processor.  
   * This processor always returns a value of 1, indicating successful
   * completion.  If any errors occur during validation, they are added
   * to the pipeline result object where the caller can examine them.
   **/

  private final int SUCCESS = 1;

  protected int[] mRetCodes = { SUCCESS };
  
  public int[] getRetCodes()
  {
    return mRetCodes;
  } 

  //---------------------------------------------------------------------------

  /**
   * Verify that all required properties are provided in an instance
   * of InvoiceRequest.
   **/

  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    ValidatePaymentGroupPipelineArgs args = (ValidatePaymentGroupPipelineArgs)pParam;
    Locale locale = args.getLocale();
    PaymentGroup paymentGroup = args.getPaymentGroup();
    ResourceBundle userResources;
    
    if (locale == null)
      userResources = sUserResources;
    else
      userResources = ResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (paymentGroup == null)
      throw new atg.commerce.order.InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidPaymentGroupParameter", RESOURCE_NAME, sServerResources));

    if (!(paymentGroup instanceof InvoiceRequest))
      throw new atg.commerce.order.InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidPaymentGroupParameter", RESOURCE_NAME, sServerResources));

    if (isLoggingDebug())
      logDebug("Validating one InvoiceRequest of type " + paymentGroup.getPaymentGroupClassType());

    validateInvoiceRequestFields((InvoiceRequest)paymentGroup, pResult, userResources);
    validateBillingAddressFields((InvoiceRequest)paymentGroup, pResult, locale);
    
    return SUCCESS;
  }

  //---------------------------------------------------------------------------

  /**
   * Validate the properties specific to an invoice request based on the
   * InvoiceRequestValidationMode property.
   *
   * @param pPayment
   *    The InvoiceRequest payment group being checked
   * @param pResult
   *    The PipelineResult object containing a HashMap of error messages
   * @param pResources
   *    The resource bundle to use when looking up error message strings
   **/
  
  protected void validateInvoiceRequestFields(
    InvoiceRequest pPayment, PipelineResult pResult, ResourceBundle pResources)
  {
    switch (getInvoiceRequestValidationMode()) {
    case REQUIRE_PO_NUMBER:
      if (isEmpty(pPayment.getPONumber()))
        addHashedError(pResult, "MissingPurchaseOrderNumber",
                       pPayment.getId(),
                       pResources.getString("PurchaseOrderNumberMissing"));
      break;
    case REQUIRE_REQUISITION_NUMBER:
      if (isEmpty(pPayment.getRequisitionNumber()))
        addHashedError(pResult, "MissingRequisitionNumber",
                       pPayment.getId(),
                       pResources.getString("RequisitionNumberMissing"));
      break;
    case REQUIRE_PO_AND_REQUISITION_NUMBER:
      if (isEmpty(pPayment.getPONumber()))
        addHashedError(pResult, "MissingPurchaseOrderNumber",
                       pPayment.getId(),
                       pResources.getString("PurchaseOrderNumberMissing"));
      if (isEmpty(pPayment.getRequisitionNumber()))
        addHashedError(pResult, "MissingRequisitionNumber",
                       pPayment.getId(),
                       pResources.getString("RequisitionNumberMissing"));
      break;
    case ACCEPT_PO_OR_REQUISITION_NUMBER:
      if (isEmpty(pPayment.getPONumber()) && isEmpty(pPayment.getRequisitionNumber()))
        addHashedError(pResult, "MissingPurchaseOrderOrRequisitionNumber",
                       pPayment.getId(),
                       pResources.getString("InvoiceRequestIncomplete"));
      break;
    }
  }

  //---------------------------------------------------------------------------

  /**
   * Validate the billing address in an invoice request.
   *
   * @param pPayment
   *    The InvoiceRequest payment group being checked
   * @param pResult
   *    The PipelineResult object containing a HashMap of error messages
   * @param pLocale
   *    The locale that the billing address validator component should
   *    use to look up error messages.
   **/
  
  protected void validateBillingAddressFields(
    InvoiceRequest pPayment, PipelineResult pResult, Locale pLocale)
  {    
    BillingAddrValidator bav = getBillingAddressValidator();
    bav.validateBillingAddress(pPayment.getBillingAddress(), pPayment.getId(), pResult, pLocale);
  }
  
  //---------------------------------------------------------------------------

  /** Return true if pStr is null or consists entirely of whitespace. **/
  
  public boolean isEmpty(String pStr)
  {
    return (pStr == null || pStr.trim().length() == 0);
  }

  //---------------------------------------------------------------------------

  /**
   * This method adds an error to the PipelineResult object. This method, rather than
   * just storing a single error object in pResult, stores a Map of errors. This allows more
   * than one error to be stored using the same key in the pResult object. pKey is
   * used to reference a HashMap of errors in pResult. So, calling
   * pResult.getError(pKey) will return an object which should be cast to a Map.
   * Each entry within the map is keyed by pId and its value is pError.
   *
   * @param pResult the PipelineResult object supplied in runProcess()
   * @param pKey the key to use to store the HashMap in the PipelineResult object
   * @param pId the key to use to store the error message within the HashMap in the
   *            PipelineResult object
   * @param pError the error object to store in the HashMap
   * @see atg.service.pipeline.PipelineResult
   * @see #runProcess(Object, PipelineResult)
   */
  protected void addHashedError(PipelineResult pResult, String pKey, String pId, Object pError)
  {
    Object error = pResult.getError(pKey);
    if (error == null) {
      HashMap map = new HashMap(5);
      pResult.addError(pKey, map);
      map.put(pId, pError);
    }
    else if (error instanceof Map) {
      Map map = (Map) error;
      map.put(pId, pError);
    }
  }
}
