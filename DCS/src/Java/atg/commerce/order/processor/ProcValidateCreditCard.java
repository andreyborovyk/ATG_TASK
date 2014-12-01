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

package atg.commerce.order.processor;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import atg.commerce.order.CreditCard;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PipelineConstants;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * Validate a single instance of the CreditCard payment group at checkout
 * time.  This processor verifies that all required fields in a CreditCard
 * are present and that they have acceptable values, to the extent that this
 * can be determined in a generic way.
 *
 * @author Matt Landau (from original source by Manny Parasirakis)
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateCreditCard.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ProcValidateCreditCard
  extends ApplicationLoggingImpl implements PipelineProcessor
{  

  /** Class version string */
  public static String CLASS_VERSION =
      "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateCreditCard.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  //---------------------------------------------------------------------------
  // Resource bundles
  
  private static ResourceBundle sResourceBundle =
      LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static ResourceBundle sUserResourceBundle =
    LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, Locale.getDefault());

  //---------------------------------------------------------------------------
  // property: LoggingIdentifier

  String mLoggingIdentifier = "ProcValidateCreditCard";

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
   * Verify that all required billing adddress properties are provided
   * in an instance of CreditCard.
   *
   * @see atg.commerce.order.processor.BillingAddrValidator#validateBillingAddress BillingAddrValidator
   **/

  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    ValidatePaymentGroupPipelineArgs args = (ValidatePaymentGroupPipelineArgs)pParam;
    Locale locale = args.getLocale();
    PaymentGroup paymentGroup = args.getPaymentGroup();
    ResourceBundle resourceBundle;
    
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (paymentGroup == null)
      throw new atg.commerce.order.InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidPaymentGroupParameter", RESOURCE_NAME, sResourceBundle));

    if (!(paymentGroup instanceof CreditCard))
      throw new atg.commerce.order.InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidPaymentGroupParameter", RESOURCE_NAME, sResourceBundle));

    if (isLoggingDebug())
      logDebug("Validating one CreditCard of type " + paymentGroup.getPaymentGroupClassType());

    validateCreditCardFields((CreditCard)paymentGroup, pResult, resourceBundle);

    return SUCCESS;
  }

  //---------------------------------------------------------------------------

  /**
   * This method validates the CreditCard properties. 
   *
   * @param cc
   *    a CreditCard object to validate
   * @pResult
   *    the PipelineResult object which was supplied in runProcess()
   * @resourceBundle
   *    a resource bundle which contains the error strings
   **/

  protected void validateCreditCardFields(
    CreditCard cc, PipelineResult pResult, ResourceBundle pResourceBundle)
  {
    if (isEmpty(cc.getCreditCardNumber())) {
      String msg = pResourceBundle.getString("CreditCardNumberMissing");
      addHashedError(pResult, PipelineConstants.MISSINGCREDITCARDNUMBER, cc.getId(), msg);
    }

    if (isEmpty(cc.getCreditCardType())) {
      String msg = pResourceBundle.getString("CreditCardTypeMissing");
      addHashedError(pResult, PipelineConstants.MISSINGCREDITCARDTYPE, cc.getId(), msg);
    }

    if (isEmpty(cc.getExpirationMonth())) {
      String msg = pResourceBundle.getString("ExpirationMonthMissing");
      addHashedError(pResult, PipelineConstants.MISSINGEXPIRATIONMONTH, cc.getId(), msg);
    }

    if (isEmpty(cc.getExpirationYear())) {
      String msg = pResourceBundle.getString("ExpirationYearMissing");
      addHashedError(pResult, PipelineConstants.MISSINGEXPIRATIONYEAR, cc.getId(), msg);
    }

    BillingAddrValidator bav = getBillingAddressValidator();
    bav.validateBillingAddress(cc.getBillingAddress(), cc.getId(), pResult, pResourceBundle);
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
