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

import atg.core.util.ClassAlias;
import atg.beans.*;
import java.beans.IntrospectionException;

/**
 * Represents the type of a component or property as Java class
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ClassObjectTypeInfo.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ClassObjectTypeInfo extends ObjectTypeInfo {
  static final long serialVersionUID = 7822783065599658307L;

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ClassObjectTypeInfo.java#2 $$Change: 651448 $";

  ClassAlias mClassAlias;
  public ClassObjectTypeInfo(Class pClass) {
    mClassAlias = new ClassAlias(pClass);
  }
  public Class getClassInfo() {
    return mClassAlias.getAliasedClass();
  }
  public boolean isArray() {
    Class theClass = mClassAlias.getAliasedClass();
    if (theClass == null) return false;
    return theClass.isArray();
  }
  /**
   * @return the DynamicBeanInfo associated with this type.
   */
  public DynamicBeanInfo getBeanInfo() {
    Class theClass = mClassAlias.getAliasedClass();
    if (theClass == null) return null;
    try {
      return DynamicBeans.getBeanInfoFromType(theClass);
    }
    catch (IntrospectionException exc) {}
    return null;
  }
  public ObjectTypeInfo getComponentInfo() {
    Class theClass = mClassAlias.getAliasedClass();
    if (theClass == null) return null;
    if (theClass.isArray()) 
      return new ClassObjectTypeInfo(theClass.getComponentType());
    return null;
  }
}
