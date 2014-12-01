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
 * Dynamo is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.droplet;

import java.beans.*;
import atg.beans.*;

/**
 * Represents a type of a component or property as an array of other types.
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ArrayObjectTypeInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ArrayObjectTypeInfo extends ObjectTypeInfo {
  static final long serialVersionUID = 1735781766615703615L;

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ArrayObjectTypeInfo.java#2 $$Change: 651448 $";

  ObjectTypeInfo mComponentInfo;

  /**
   * Constructs an ArrayObjectTypeInfo give the ObjectTypeInfo that
   * represents the type of an element of the array
   */
  public ArrayObjectTypeInfo(ObjectTypeInfo pComponentInfo) {
    mComponentInfo = pComponentInfo;
  }

  public boolean isArray() {
    return true;
  }
  public ObjectTypeInfo getComponentInfo() {
    return mComponentInfo;
  }

  /**
   * @return the DynamicBeanInfo associated with this type.  For array
   * components, there is no DynamicBeanInfo that describes the properties
   * of the array itself.  Instead, we return a BeanInfo for Object[]'s
   * class.
   */
  public DynamicBeanInfo getBeanInfo() {
    try {
      return DynamicBeans.getBeanInfoFromType(Object[].class);
    }
    catch (IntrospectionException exc) {}
    return null;
  }

  /**
   * @return the class represented by this type or null if the type
   * is represented by a BeanInfo.
   */
  public Class getClassInfo() {
    return Object[].class;
  }

}
