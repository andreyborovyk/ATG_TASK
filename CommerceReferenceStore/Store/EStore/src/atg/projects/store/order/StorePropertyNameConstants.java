/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2011 Art Technology Group, Inc.
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
package atg.projects.store.order;

import atg.core.i18n.LayeredResourceBundle;

import java.util.MissingResourceException;
import java.util.ResourceBundle;


/**
 * Constants for Store-specific order properties.
 * @author ATG
 * @version $Revision: #2 $
 */
public class StorePropertyNameConstants {
  /**
   * Class version string.
   */
  public static String CLASS_VERSION = "$Id: //hosting-blueprint/B2CBlueprint/version/10.0.3/EStore/src/atg/projects/store/order/StorePropertyNameConstants.java#2 $$Change: 651448 $";

  /**
   * Resource bundle.
   */
  private static ResourceBundle sResourceBundle = LayeredResourceBundle.getBundle("atg.projects.store.order.PropertyNameResources",
      atg.service.dynamo.LangLicense.getLicensedDefault());

  /**
   * Coupon code.
   */
  public static final String COUPONCODE = getStringResource("couponCode");

  /**
   * Oms order id.
   */
  public static final String OMSORDERID = getStringResource("omsOrderId");

  /**
   * Oms segments.
   */
  public static final String OMSSEGMENTS = getStringResource("omsSegments");

  /**
   * Tracking information.
   */
  public static final String TRACKINGINFOS = getStringResource("trackingInfos");

  //-------------------------------------
  // Getting resources.
  //-------------------------------------
  /**
   * Reads the string with the given key from the resource file.
   * @param pResourceName - resource name
   * @return string with the given key from the resource file
   * @throws java.util.MissingResourceException if resource was not found
   */
  public static String getStringResource(String pResourceName)
    throws MissingResourceException {
    try {
      String ret = sResourceBundle.getString(pResourceName);

      if (ret == null) {
        String str = "ERROR: Unable to load resource " + pResourceName;
        throw new MissingResourceException(str, "atg.commerce.order.Constants", pResourceName);
      } else {
        return ret;
      }
    } catch (MissingResourceException exc) {
      throw exc;
    }
  }
}
