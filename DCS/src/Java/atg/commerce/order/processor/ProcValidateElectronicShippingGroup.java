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

import atg.commerce.order.ElectronicShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.Order;
import atg.commerce.order.PipelineConstants;
import atg.commerce.order.ShippingGroup;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * Validate a single instance of ElectronicShippingGroup at checkout time.
 * This processor verifies that all required fields in an ElectronicShippingGroup
 * are present and that they have acceptable values, to the extent that this
 * can be determined in a generic way.
 *
 * @author Matt Landau (from original source by Manny Parasirakis)
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateElectronicShippingGroup.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ProcValidateElectronicShippingGroup
  extends ApplicationLoggingImpl implements PipelineProcessor
{
  /** Class version string */
  public static String CLASS_VERSION =
      "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateElectronicShippingGroup.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  //---------------------------------------------------------------------------
  // Resource bundles
  
  private static ResourceBundle sResourceBundle =
      LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static ResourceBundle sUserResourceBundle =
    LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());
  
  private final int SUCCESS = 1;

  //---------------------------------------------------------------------------

  /**
   * Returns the set of valid return codes for this processor.  
   * This processor always returns a value of 1, indicating successful
   * completion.  If any errors occur during validation, they are added
   * to the pipeline result object where the caller can examine them.
   **/
  
  public int[] getRetCodes()
  {
    int[] ret = {SUCCESS};
    return ret;
  } 

  //---------------------------------------------------------------------------

  /**
   * Verify that all required properties are present in an instance
   * of ElectronicShippingGroup.  By default the only required property
   * is the user's email address.
   **/

  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    ValidateShippingGroupPipelineArgs args = (ValidateShippingGroupPipelineArgs)pParam;
    Order order = args.getOrder();
    Locale locale = args.getLocale();
    ShippingGroup shippingGroup = args.getShippingGroup();
    ResourceBundle resourceBundle;
    
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (shippingGroup == null)
      throw new InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidShippingGroupParameter", RESOURCE_NAME, sResourceBundle));

    if (!(shippingGroup instanceof ElectronicShippingGroup))
      throw new InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidShippingGroupParameter", RESOURCE_NAME, sResourceBundle));

    if (isLoggingDebug())
      logDebug("Validating one ElectronicShippingGroup of type " + shippingGroup.getShippingGroupClassType());

    validateElectronicShippingGroupFields((ElectronicShippingGroup)shippingGroup, pResult, resourceBundle);

    return SUCCESS;
  }

  //---------------------------------------------------------------------------

  /**
   * This method validates the ElectronicShippingGroup properties.  By
   * default only the email address is checked to ensure that it has one
   * and only one '@' character.
   *
   * @param esg
   *    an ElectronicShippingGroup object to validate
   * @pResult
   *    the PipelineResult object which was supplied in runProcess()
   * @resourceBundle
   *    a resource bundle containing error messages
   **/
  
  protected void validateElectronicShippingGroupFields(
    ElectronicShippingGroup esg, PipelineResult pResult, ResourceBundle resourceBundle)
  {
    if (getValidateEmail())
    {
      String emailAddr = esg.getEmailAddress();
      
      if (isEmpty(emailAddr)) {
        String msg = resourceBundle.getString("ShipEmailMissing");
        addHashedError(pResult, PipelineConstants.MISSINGSHIPPINGEMAIL, esg.getId(), msg);
      }
      else if (!validateEmailAddress(emailAddr)) {
        String msg = resourceBundle.getString("ShipEmailInvalid");
        addHashedError(pResult, PipelineConstants.INVALIDSHIPPINGEMAIL, esg.getId(), msg);
      }
    }
  }

  //---------------------------------------------------------------------------

  /**
   * Validates an email address for correctness.  By default this method
   * checks to see that there is one and only one @ character. This method
   * can be overridden to do more sophisticated validation.
   **/

  protected boolean validateEmailAddress(String pEmail)
  {
    int ret = 0, count = 0, ind = 0;

    while ((ret = pEmail.indexOf("@", ind)) != -1) {
      count++;
      ind = ret + 1;
    }

    return (count == 1);
  }
  
  //---------------------------------------------------------------------------

  /** Return true if pStr is null or consists entirely of whitespace. **/
  
  public boolean isEmpty(String pStr)
  {
    return (pStr == null || pStr.trim().length() == 0);
  }

  //---------------------------------------------------------------------------

  /**
   * This method adds an error to the PipelineResult object. This method,
   * rather than just storing a single error object in pResult, stores a Map
   * of errors. This allows more than one error to be stored using the same
   * key in the pResult object. pKey is used to reference a HashMap of
   * errors in pResult. So, calling pResult.getError(pKey) will return an
   * object which should be cast to a Map.  Each entry within the map is
   * keyed by pId and its value is pError.
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
  // property: validateEmail
  //---------------------------------------------------------------------------

  private boolean mValidateEmail = true;

  /** Query whether or not to validate email addresses **/
  
  public boolean getValidateEmail() {
    return mValidateEmail;
  }

  /** Specify whether or not to validate email addresses. **/

  public void setValidateEmail(boolean pValidateEmail) {
    mValidateEmail = pValidateEmail;
  }


  //-------------------------------------
  // property: LoggingIdentifier
  String mLoggingIdentifier = "ProcValidateElectronicShippingGroup";

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
