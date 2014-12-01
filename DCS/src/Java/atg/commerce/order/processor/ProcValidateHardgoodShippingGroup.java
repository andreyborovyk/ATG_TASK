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

import java.util.Locale;
import java.util.ResourceBundle;

import atg.commerce.order.HardgoodShippingGroup;
import atg.commerce.order.InvalidParameterException;
import atg.commerce.order.ShippingGroup;
import atg.core.i18n.LayeredResourceBundle;
import atg.core.util.ResourceUtils;
import atg.nucleus.logging.ApplicationLoggingImpl;
import atg.service.pipeline.PipelineProcessor;
import atg.service.pipeline.PipelineResult;

/**
 * Validate a single instance of HardgoodShippingGroup at checkout time.
 * This processor verifies that all required fields in a HardgoodShippingGroup
 * are present and that they have acceptable values, to the extent that this
 * can be determined in a generic way.
 *
 * @author Matt Landau (from original source by Manny Parasirakis)
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateHardgoodShippingGroup.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ProcValidateHardgoodShippingGroup
  extends ApplicationLoggingImpl implements PipelineProcessor
{
  /** Class version string */
  public static String CLASS_VERSION =
      "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/ProcValidateHardgoodShippingGroup.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.commerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.commerce.order.UserMessages";

  //---------------------------------------------------------------------------
  // Resource bundles
  
  private static ResourceBundle sResourceBundle =
    LayeredResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static ResourceBundle sUserResourceBundle =
      LayeredResourceBundle.getBundle(USER_MSGS_RES_NAME, java.util.Locale.getDefault());
  
  //---------------------------------------------------------------------------
  // property: LoggingIdentifier

  String mLoggingIdentifier = "ProcValidateHardgoodShippingGroup";

  /** Set the LoggingIdentifier used in log messages. **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /** Return the LoggingIdentifier used in log messages. **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
  }

  //---------------------------------------------------------------------------
  // property: ShippingAddressValidator
  //---------------------------------------------------------------------------

  ShippingAddrValidator mShippingAddressValidator;

  public void setShippingAddressValidator(ShippingAddrValidator pShippingAddressValidator) {
    mShippingAddressValidator = pShippingAddressValidator;
  }

  public ShippingAddrValidator getShippingAddressValidator() {
    return mShippingAddressValidator;
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
   * Verify that all required shipping adddress properties are provided
   * in a HardgoodShippingGroup.
   *
   * @see atg.commerce.order.processor.ShippingAddrValidator#validateShippingAddress ShippingAddrValidator
   **/

  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    ValidateShippingGroupPipelineArgs args = (ValidateShippingGroupPipelineArgs)pParam;
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

    if (!(shippingGroup instanceof HardgoodShippingGroup))
      throw new InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidShippingGroupParameter", RESOURCE_NAME, sResourceBundle));

    if (isLoggingDebug())
      logDebug("Validating one HardgoodShippingGroup of type " + shippingGroup.getShippingGroupClassType());

    validateHardgoodShippingGroupFields((HardgoodShippingGroup)shippingGroup, pResult, resourceBundle);

    return SUCCESS;
  }
  
  //---------------------------------------------------------------------------

  /**
   * This method validates the HardgoodShippingGroup properties.  By default
   * validation is limited to checking the fields in the shipping address by
   * calling <code>validateShippingAddress</code>.
   *
   * @param hsg
   *    a HardgoodShippingGroup object to validate
   * @pResult
   *    the PipelineResult object which was supplied in runProcess()
   * @pResourceBundle
   *    the resource bundle from which to retrieve error messages
   **/
  
  protected void validateHardgoodShippingGroupFields(
    HardgoodShippingGroup hsg, PipelineResult pResult, ResourceBundle pResourceBundle)
  {
    ShippingAddrValidator sav = getShippingAddressValidator();
    sav.validateShippingAddress(hsg.getShippingAddress(), hsg.getId(), pResult, pResourceBundle);
  }
}
