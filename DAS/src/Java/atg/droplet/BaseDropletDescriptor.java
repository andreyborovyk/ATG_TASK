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

package atg.droplet;

import javax.servlet.*;
import javax.servlet.http.*;

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import java.beans.*;
import java.awt.Point;
import java.text.*;

import atg.servlet.*;
import atg.core.util.NumberTable;
import atg.naming.*;

import atg.beans.DynamicBeans;
import atg.beans.DynamicBeanInfo;
import atg.beans.DynamicPropertyConverterImpl;
import atg.beans.DynamicPropertyDescriptor;
import atg.beans.DynamicPropertyMapper;
import atg.beans.PropertyNotFoundException;

/*
 * This class contains type cached type information that we store for 
 * each tag added to a form or anchor.  It combines information used to 
 * set DynamicProperties (atg.beans) and regular bean properties (java.bean).
 * <p>
 * To figure out the type information necessary to set a dynamic property, 
 * we create an instance of one of these objects as we process a form 
 * submission.
 * <p>
 * DropletDescriptor extends this class.  If
 * the bean is a regular static bean property, we just use the
 * DropletDescriptor to get this information avoiding the overhead of 
 * creating a typer per instance.
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/BaseDropletDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class BaseDropletDescriptor implements DropletConstants {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/BaseDropletDescriptor.java#2 $$Change: 651448 $";

  Class propertyType = null;
  boolean isArray = false;
  boolean isList = false;
  DynamicPropertyDescriptor propertyDescriptor = null;
   
  BaseDropletDescriptor() {
  }

  BaseDropletDescriptor(Object pComponent, String pPropertyName) {
    try {
      DynamicBeanInfo dbi = DynamicBeans.getBeanInfo(pComponent);
      propertyDescriptor = dbi.getPropertyDescriptor(pPropertyName);

      if (propertyDescriptor != null) {
        propertyType = propertyDescriptor.getPropertyType();
        isArray = propertyDescriptor.isMultiValued();
        if (propertyType == null) // Problem with the bean info?
          propertyType = java.lang.String.class;
	if (isArray) {
          isList = List.class.isAssignableFrom(propertyType);
          propertyType = propertyDescriptor.getComponentPropertyType();
          if (propertyType == null) // Problem with the bean info?
            propertyType = java.lang.String.class;
        }
      }
    }
    catch (IntrospectionException exc) {
    }
  }

  Object cvtStringToProperty(String pValue, Object [] pArgs) 
    throws IllegalArgumentException, IllegalAccessException, 
           InvocationTargetException {
    if (pValue == null) return null;

    // property editors are defined to work on the entire value.  This
    // method gets called for each element when we have an array.
    if (isArray)
      return
	DynamicPropertyConverterImpl.CONVERTER.convertValue(propertyType, pValue);

    PropertyEditor editor;
    if (propertyDescriptor != null &&
        (editor = propertyDescriptor.createPropertyEditor()) != null) {
      editor.setAsText(pValue);
      return editor.getValue();
    }
    return pValue;
  }

  /*
   * Try to convert this Number to the type of the property we're associated
   * with.
   */
  Object cvtNumberToProperty(Number pValue, Object [] pArgs) 
     throws InvocationTargetException, 
            IllegalAccessException, IllegalArgumentException {
    if (pValue == null) return null;

    if (propertyDescriptor != null) {
      Class theClass = propertyDescriptor.getPropertyType();
      if (theClass == pValue.getClass()) return pValue;

      if (theClass == Double.class) {
        return NumberTable.getDouble(pValue.doubleValue());
      }
      else if (theClass == Float.class) { 
        return NumberTable.getFloat(pValue.floatValue());
      }
      else if (theClass == Integer.class) {
        return NumberTable.getInteger (pValue.intValue ());
      }
      else if (theClass == Long.class) {
        return NumberTable.getLong (pValue.longValue ());
      }
      else if (theClass == Short.class) {
	return NumberTable.getShort(pValue.shortValue ());
      }
      else if (theClass == Byte.class) {
        return NumberTable.getByte(pValue.byteValue ());
      }
    }
    return pValue;
  }
}
