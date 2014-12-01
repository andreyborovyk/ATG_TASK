/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
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

import java.io.Serializable;
import atg.beans.*;

/**
 * This is an abstract class that is used to store various ways of 
 * representing "type" information for a bean or property.  It is designed
 * to represent classes in one of 4 ways:
 * <ol>
 * <li>Class
 * <li>DynamicPropertyDescriptor
 * <li>DynamicBeanInfo
 * <li>Array of one of the above
 * </ol>
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ObjectTypeInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ObjectTypeInfo implements Serializable
{
  static final long serialVersionUID = -2138248690022606561L;

  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ObjectTypeInfo.java#2 $$Change: 651448 $";

  /**
   * @return true if this represents an array
   */
  public boolean isArray() {
    return false;
  }

  /**
   * @return the class represented by this type or null if the type
   * is represented by a BeanInfo.
   */
  public Class getClassInfo() {
    return null;
  }

  /**
   * @return the DynamicBeanInfo associated with this type or null if
   * the type is a class, array, or property.
   */
  public DynamicBeanInfo getBeanInfo() {
    return null;
  }

  /**
   * @return the DynamicPropertyDescriptor associated with this type or
   * null if the type is a class, bean, or array.
   */
  public DynamicPropertyDescriptor getPropertyDescriptor() {
    return null;
  }

  /**
   * @return the component type of the array represented by this type
   * or null if this type is not an array.
   */
  public ObjectTypeInfo getComponentInfo() {
    return null;
  }

  /*
   * Display a string representation of this object
   */
  public String toString() {
    StringBuffer buf = new StringBuffer ();
    buf.append (getClass ().getName ());
    buf.append ('[');
    buf.append ("class=");
    buf.append (getClassInfo());
    buf.append (", beanInfo=");
    buf.append (getBeanInfo());
    buf.append (", propertyDescriptor=");
    buf.append (getPropertyDescriptor());
    buf.append (", isArray=");
    buf.append (isArray());
    if (isArray()) {
      buf.append(", componentType=");
      buf.append(getComponentInfo());
    }
    buf.append (']');
    return buf.toString ();
  }
}
