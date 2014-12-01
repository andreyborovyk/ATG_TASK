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

import atg.commerce.claimable.ClaimableException;
import atg.commerce.claimable.ClaimableManager;
import atg.commerce.order.Order;
import atg.commerce.order.PaymentGroup;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.StoreCredit;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * Validate a single instance of the StoreCredit payment group at checkout
 * time.  This processor verifies that all required fields in a StoreCredit
 * are present and that they have acceptable values, to the extent that this
 * can be determined in a generic way.
 *
 * @author Matt Landau (from original source by Manny Parasirakis)
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateStoreCredit.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ProcValidateStoreCredit
  extends ApplicationLoggingImpl implements PipelineProcessor
{  

  /** Class version string */
  public static String CLASS_VERSION =
      "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateStoreCredit.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  //---------------------------------------------------------------------------
  // Resource bundles
  
  private static ResourceBundle sResourceBundle =
      LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static ResourceBundle sUserResourceBundle =
      LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, Locale.getDefault());

  private final int SUCCESS = 1;

  //---------------------------------------------------------------------------

  /**
   * Returns the set of valid return codes for this processor.  
   * This processor always returns a value of 1, indicating successful
   * completion.  If any errors occur during validation, they are added
   * to the pipeline result object where the caller can examine them.
   **/

  protected int[] mRetCodes = { SUCCESS };
  
  public int[] getRetCodes()
  {
    return mRetCodes;
  } 

  //---------------------------------------------------------------------------

  /**
   * Verify that a StoreCredit object has a non-empty store credit number,
   * and that this number is recognized as valid by the claimable manager.
   **/

  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    ValidatePaymentGroupPipelineArgs args = (ValidatePaymentGroupPipelineArgs)pParam;
    Order order = args.getOrder();
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

    if (!(paymentGroup instanceof StoreCredit))
      throw new atg.commerce.order.InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidPaymentGroupParameter", RESOURCE_NAME, sResourceBundle));

    if (isLoggingDebug())
      logDebug("Validating one StoreCredit of type " + paymentGroup.getPaymentGroupClassType());
    
    validateStoreCreditFields((StoreCredit)paymentGroup, pResult, resourceBundle);

    return SUCCESS;
  }

  //---------------------------------------------------------------------------

  /**
   * This method validates the CreditCard properties. 
   *
   * @param pStoreCredit
   *    a StoreCredit object to validate
   * @pResult
   *    the PipelineResult object which was supplied in runProcess()
   * @resourceBundle
   *    a resource bundle which contains the error strings
   **/

  protected void validateStoreCreditFields(
    StoreCredit pStoreCredit, PipelineResult pResult, ResourceBundle resourceBundle)
  {
    // First check to make sure we have a store credit number.  If not, then
    // there's no sense in proceeding.

    String creditNumber = pStoreCredit.getStoreCreditNumber();
    
    if (isEmpty(creditNumber)) {
      String msg = resourceBundle.getString("StoreCreditNumberMissing");
      addHashedError(pResult, PipelineConstants.MISSINGSTORECREDITNUMBER, pStoreCredit.getId(), msg);
      return;
    }

    // Now verify that the store credit number we got isn't a fake.
    
    try
    {
      if (getClaimableManager().claimItem(creditNumber) == null) {
        String msg = resourceBundle.getString("StoreCreditNumberNotFound");
        addHashedError(pResult, PipelineConstants.STORECREDITNUMBERNOTFOUND, pStoreCredit.getId(), msg);
      }
    }
    catch (ClaimableException c) {
      addHashedError(pResult, PipelineConstants.STORECREDITNUMBERCLAIMEXCEPTION, pStoreCredit.getId(), c.getMessage());
    }
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

  //---------------------------------------------------------------------------
  // Properties
  //---------------------------------------------------------------------------

  private ClaimableManager mClaimableManager = null;

  /**
   * Returns the claimableManager
   * @return property claimableManager
   */
  public ClaimableManager getClaimableManager() {
    return mClaimableManager;
  }

  /**
   * Sets the claimableManager which is used to validate repository existance of any
   * claimable item existing as a PaymentGroup in the order.
   * @param pClaimableManager the value to set for property claimableManager
   */
  public void setClaimableManager(ClaimableManager pClaimableManager) {
    mClaimableManager = pClaimableManager;
  }

  //-------------------------------------
  // property: LoggingIdentifier

  String mLoggingIdentifier = "ProcValidateStoreCredit";

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
}
