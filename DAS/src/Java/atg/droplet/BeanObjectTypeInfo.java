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

import atg.beans.*;
import atg.core.util.ClassAlias;
import java.beans.IntrospectionException;

/**
 * Represents the type of a component or property as a DynamicBeanInfo
 * with optional augmenting Class, and PropertyDescriptor information as well.
 * 
 * @see atg.beans.DynamicBeanInfo
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/BeanObjectTypeInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class BeanObjectTypeInfo extends ObjectTypeInfo {
  static final long serialVersionUID = 3371145100461789074L;

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/BeanObjectTypeInfo.java#2 $$Change: 651448 $";

  DynamicBeanInfo mBeanInfo;
  ClassAlias mClassAlias;
  DynamicPropertyDescriptor mPropertyDescriptor;

  /**
   * Constructs a BeanObjectTypeInfo from a class and a DynamicBeanInfo
   */
  public BeanObjectTypeInfo(Class pClass, DynamicBeanInfo pBeanInfo,
  			    DynamicPropertyDescriptor pPropertyDescriptor) {
    mClassAlias = new ClassAlias(pClass);
    mBeanInfo = pBeanInfo;
    mPropertyDescriptor = pPropertyDescriptor;
  }
  /**
   * Constructs a BeanObjectTypeInfo from a DynamicBeanInfo
   */
  public BeanObjectTypeInfo(DynamicBeanInfo pBeanInfo) {
    mBeanInfo = pBeanInfo;
  }
  /**
   * Returns the BeanInfo for this object.  We'll create it from the
   * class if we don't have a BeanInfo defined explicitly
   */
  public DynamicBeanInfo getBeanInfo() {
    if (mBeanInfo == null) {
      Class theClass = null; 
      if (mClassAlias != null) {
        theClass = mClassAlias.getAliasedClass();
      }
      if (theClass == null && mPropertyDescriptor != null) {
        theClass = mPropertyDescriptor.getPropertyType();
      }
      if (theClass != null) {
        try {
          return DynamicBeans.getBeanInfoFromType(theClass);
	}
	catch (IntrospectionException exc) {}
      }
    }
    return mBeanInfo;
  }
  public boolean isArray() {
    if (mClassAlias == null) return false;
    Class theClass = mClassAlias.getAliasedClass();
    if (theClass == null) return false;
    return theClass.isArray();
  }
  public ObjectTypeInfo getComponentInfo() {
    Class theClass = mClassAlias.getAliasedClass();
    if (theClass == null) return null;
    if (theClass.isArray()) 
      return new ClassObjectTypeInfo(theClass.getComponentType());
    return null;
  }
  public Class getClassInfo() {
    if (mClassAlias == null) return null;
    return mClassAlias.getAliasedClass();
  }
  public DynamicPropertyDescriptor getPropertyDescriptor() {
    return mPropertyDescriptor;
  }
}
