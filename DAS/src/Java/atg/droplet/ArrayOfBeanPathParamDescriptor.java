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

/**
 *
 * This is a dynamic ParamDescriptor which allows you to define an output
 * parameter whose type is an array of another bean.  The other bean is
 * specified as an absolute path to a component in nucleus.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ArrayOfBeanPathParamDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ArrayOfBeanPathParamDescriptor extends DynamicParamDescriptor {
  static final long serialVersionUID = 4286501863518302752L;

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ArrayOfBeanPathParamDescriptor.java#2 $$Change: 651448 $";

  //-------------------------------------------------
  public ArrayOfBeanPathParamDescriptor () {
  }

  //-------------------------------------------------
  /**
   * Construct an ArrayOfBeanPathParamDescriptor.  Most of these arguments
   * are the same as those to ParamDescriptor.  
   * The last argument, pBeanPath, is the name of the input parameter
   * that we should base our output type on.  This should be the parameter
   * name of an BeanPath (a bean that describes the type of a class of
   * related beans).  Our output type is an array of these types.
   */
  public ArrayOfBeanPathParamDescriptor(String pName,
                         String pDescription,
                         Class pParamClass,
                         boolean pOptional,
                         boolean pLocal,
			 String pBeanPath)
  {
    super(pName, pDescription, pParamClass, pOptional, pLocal);
    setBeanPath(pBeanPath); 
  }

  //--------- Property: BeanPath -----------
  String mBeanPath;
  /**
   * Sets the property BeanPath.  This is the 
   * name of the input parameter, which should be a BeanPath, that
   * we use to based our output parameter's type on.
   */
  public void setBeanPath(String pBeanPath) {
    mBeanPath = pBeanPath;
  }
  /**
   * @return The value of the property BeanPath.
   */
  public String getBeanPath() {
    return mBeanPath;
  }

  //-------------------------------------------------
  /**
   * Returns the type of the parameter by first getting the type of
   * a bean that is specified in the nucleus hierarchy.  If that bean
   * is defined, we return the our type as an array of these elements.
   */
  public ObjectTypeInfo getParamInfo(ParamDescriptorResolver pResolver) {
    ObjectTypeInfo t = pResolver.getBeanInfo(mBeanPath);
    if (t != null) 
      return new ArrayObjectTypeInfo(t);
    else if (getParamClass() != null)
      return new ClassObjectTypeInfo(getParamClass());
    else return null;
  }
}
