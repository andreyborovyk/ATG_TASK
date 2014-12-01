/*<ATGCOPYRIGHT>
 * Copyright (C) 1999-2011 Art Technology Group, Inc.
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

import java.util.Properties;
import atg.nucleus.logging.ApplicationLoggingImpl;

/**
 * This abstract class stores a list of properties to be loaded for a bean.
 * Processors which extend this class inherit the loadProperties and
 * propertyDescriptorToBeanPropertyMap properties for listing the property
 * names and mapping them.
 *
 * @see atg.commerce.order.ChangedProperties
 * @author Manny Parasirakis
 * @version $Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/LoadProperties.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public abstract class LoadProperties extends ApplicationLoggingImpl {
  
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DCS/version/10.0.3/Java/atg/commerce/order/processor/LoadProperties.java#2 $$Change: 651448 $";

  //-----------------------------------------------
  public LoadProperties() {
  }

  //-------------------------------------
  // property: loadProperties
  //-------------------------------------
  private String[] mLoadProperties = null;

  /**
   * Returns the loadProperties
   */
  public String[] getLoadProperties() {
    return mLoadProperties;
  }

  /**
   * Sets the loadProperties
   */
  public void setLoadProperties(String[] pLoadProperties) {
    mLoadProperties = pLoadProperties;
  }

  //-------------------------------------
  // property: propertyDescriptorToBeanPropertyMap
  //-------------------------------------
  private Properties mPropertyDescriptorToBeanPropertyMap = null;

  /**
   * Returns the propertyDescriptorToBeanPropertyMap
   */
  public Properties getPropertyDescriptorToBeanPropertyMap() {
    return mPropertyDescriptorToBeanPropertyMap;
  }

  /**
   * Sets the propertyDescriptorToBeanPropertyMap
   */
  public void setPropertyDescriptorToBeanPropertyMap(Properties pPropertyDescriptorToBeanPropertyMap) {
    mPropertyDescriptorToBeanPropertyMap = pPropertyDescriptorToBeanPropertyMap;
  }

  //-------------------------------------
  /**
   * Returns the repository property name mapped to the bean property name. If there is no
   * name mapped then the value supplied in pBeanPropertyName is returned because that is
   * assumed to be the repository property name also.
   *
   * @param pBeanPropertyName the bean property name
   * @return the repository property name
   */
  public String getMappedPropertyName(String pBeanPropertyName) {
    String repPropertyName = getPropertyDescriptorToBeanPropertyMap().getProperty(pBeanPropertyName);
    if (repPropertyName == null)
      return pBeanPropertyName.trim();
    return repPropertyName.trim();
  }
}
