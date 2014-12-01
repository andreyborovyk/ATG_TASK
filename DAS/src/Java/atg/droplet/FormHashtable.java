/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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
package atg.droplet;

import java.util.Hashtable;
import java.util.Dictionary;
import java.beans.IntrospectionException;

import atg.core.util.StringUtils;
import atg.beans.DynamicBeans;
import atg.beans.DynamicBeanInfo;
import atg.beans.DynamicPropertyMapper;
import atg.beans.PropertyNotFoundException;


//------------------------
/* 
 * Define a hashtable with a property mapper which is case insensitive and
 * does not give hard errors for properties that are not currently defined.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/FormHashtable.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class FormHashtable extends Hashtable {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/FormHashtable.java#2 $$Change: 651448 $";

  static final long serialVersionUID = -6910707286050277127L;

  Object rawGet(Object pKey) {
     return super.get(pKey);
  }

  /**
   * This registers the mapping between the FormHashtable class and
   * the "dynamic beans" mechanism which allows to set/get these values
   * from dynamo server pages.
   */
  static {
    DynamicBeans.registerPropertyMapper(FormHashtable.class, 
					new FormHashtablePropertyMapper());
  }
}

/**
 * Defines a dynamic property mapper for the FormHashtable class
 * so that we can get/set values of our value property.
 */
class FormHashtablePropertyMapper implements DynamicPropertyMapper {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/FormHashtable.java#2 $$Change: 651448 $";

  FormHashtablePropertyMapper() {}

  //-------------------------------------
  // METHODS
  //-------------------------------------

  /**
   * Gets the value of the dynamic property from the specified object.
   */
  public Object getPropertyValue(Object pBean, String pPropertyName) 
       throws PropertyNotFoundException
  {
    pPropertyName = StringUtils.toUpperCase(pPropertyName);
    return ((Dictionary)pBean).get(pPropertyName);
  }

  /**
   * Sets the value of the dynamic property from the specified object.
   */
  public void setPropertyValue(Object pBean, String pPropertyName, Object pValue) 
  {
    pPropertyName = StringUtils.toUpperCase(pPropertyName);
    ((Dictionary)pBean).put(pPropertyName, pValue);
  }


  //-------------------------------------
  /**
   * Gets a DynamicBeanInfo that describes the given dynamic bean.
   *
   * @return the DynamicBeanInfo describing the bean.
   * @throws IntrospectionException if no information is available.
   */
  public DynamicBeanInfo getBeanInfo(Object pBean)
       throws IntrospectionException
  {
    throw new IntrospectionException("No dynamic bean info available for " + pBean);
  }
}
