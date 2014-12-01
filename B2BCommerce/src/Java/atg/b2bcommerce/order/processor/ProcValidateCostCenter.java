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

import atg.b2bcommerce.order.*;
import atg.commerce.order.*;
import atg.commerce.pricing.*;
import atg.repository.*;

import atg.service.pipeline.*;
import atg.core.util.ResourceUtils;
import atg.core.util.Address;
import atg.core.util.ContactInfo;
import atg.nucleus.logging.ApplicationLoggingImpl;

import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Validate a single Cost Center at checkout
 * time.  This is a dummy validator as there is no out-of-the-box validation
 * for cost centers.  This is here to be overridden.
 *
 * @author Paul O'Brien (from original source by Manny Parasirakis)
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/processor/ProcValidateCostCenter.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ProcValidateCostCenter
  extends ApplicationLoggingImpl implements PipelineProcessor
{  

  /** Class version string */
  public static String CLASS_VERSION =
      "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/b2bcommerce/order/processor/ProcValidateCostCenter.java#2 $$Change: 651448 $";

  static final String RESOURCE_NAME = "atg.b2bcommerce.order.OrderResources";
  static final String USER_MSGS_RES_NAME = "atg.b2bcommerce.order.UserMessages";

  //---------------------------------------------------------------------------
  // Resource bundles
  
  private static ResourceBundle sResourceBundle =
      ResourceBundle.getBundle(RESOURCE_NAME, atg.service.dynamo.LangLicense.getLicensedDefault());
  private static ResourceBundle sUserResourceBundle =
      ResourceBundle.getBundle(USER_MSGS_RES_NAME, Locale.getDefault());

  //---------------------------------------------------------------------------
  // property: LoggingIdentifier

  String mLoggingIdentifier = "ProcValidateCostCenter";

  /** Set the LoggingIdentifier used in log messages. **/
  public void setLoggingIdentifier(String pLoggingIdentifier) {
    mLoggingIdentifier = pLoggingIdentifier;
  }

  /** Return the LoggingIdentifier used in log messages. **/
  public String getLoggingIdentifier() {
    return mLoggingIdentifier;
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
   * Dummy Verifier of CostCenters.
   *
   **/

  public int runProcess(Object pParam, PipelineResult pResult) throws Exception
  {
    ValidateCostCenterPipelineArgs args = (ValidateCostCenterPipelineArgs)pParam;
    Locale locale = args.getLocale();
    CostCenter costCenter = args.getCostCenter();
    ResourceBundle resourceBundle;
    
    if (locale == null)
      resourceBundle = sUserResourceBundle;
    else
      resourceBundle = ResourceBundle.getBundle(USER_MSGS_RES_NAME, locale);

    if (costCenter == null)
      throw new atg.commerce.order.InvalidParameterException(
        ResourceUtils.getMsgResource("InvalidCostCenterParameter", RESOURCE_NAME, sResourceBundle));

    return SUCCESS;
  }

  //---------------------------------------------------------------------------

}
