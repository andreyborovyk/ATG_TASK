/*<ATGCOPYRIGHT>
 * Copyright (C) 2006-2010 Art Technology Group, Inc.
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
package atg.projects.store.pricing;

import atg.commerce.pricing.PricingModelProperties;


/**
 * Adds properties to the Store pricing models.
 *
 * @author ATG
 * @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/pricing/StorePricingModelProperties.java#3 $
 */
public class StorePricingModelProperties extends PricingModelProperties {

  /** Class version string. */
  public static String CLASS_VERSION = "$Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/EStore/src/atg/projects/store/pricing/StorePricingModelProperties.java#3 $$Change: 635816 $";

  /**
   * Qualifier service.
   */
  private String mQualifierPropertyName = "qualifierService";

  /**
   * Default qualifier value.
   */
  private String mDefaultQualifierValue = "default";

  /**
   * Sets the name of the property on the pricing model that indicates
   * the qualifier.
   * @param pQualifierPropertyName - name of the property
   */
  public void setQualifierPropertyName(String pQualifierPropertyName) {
    mQualifierPropertyName = pQualifierPropertyName;
  }

  /**
   * Returns the name of the property on the pricing model that indicates
   * the qualifier.
   * @return name of the property
   */
  public String getQualifierPropertyName() {
    return mQualifierPropertyName;
  }

  /**
   * Sets the value that indicates the default qualifier.
   *
   * @param pDefaultQualifierValue qualifier value
   */
  public void setDefaultQualifierValue(String pDefaultQualifierValue) {
    mDefaultQualifierValue = pDefaultQualifierValue;
  }

  /**
   * Returns the value that indicates the default qualifier.
   *
   * @return qualifier value
   */
  public String getDefaultQualifierValue() {
    return mDefaultQualifierValue;
  }
}
