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
 * parameter whose type is an element of an array based input parameter.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ArrayOfBeanTyperParamDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ArrayOfBeanTyperParamDescriptor extends DynamicParamDescriptor {
  static final long serialVersionUID = -7709809626773712001L;

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ArrayOfBeanTyperParamDescriptor.java#2 $$Change: 651448 $";

  //-------------------------------------------------
  public ArrayOfBeanTyperParamDescriptor () {
  }

  //-------------------------------------------------
  /**
   * Construct an ArrayOfBeanTyperParamDescriptor.  Most of these arguments
   * are the same as those to ParamDescriptor.  
   * The last argument, pBeanTyperParamName, is the name of the input parameter
   * that we should base our output type on.  This should be the parameter
   * name of an BeanTyper (a bean that describes the type of a class of
   * related beans).  Our output type is an array of these types.
   */
  public ArrayOfBeanTyperParamDescriptor(String pName,
                         String pDescription,
                         Class pParamClass,
                         boolean pOptional,
                         boolean pLocal,
			 String pBeanTyperParamName)
  {
    super(pName, pDescription, pParamClass, pOptional, pLocal);
    setBeanTyperParamName(pBeanTyperParamName); 
  }

  //--------- Property: BeanTyperParamName -----------
  String mBeanTyperParamName;
  /**
   * Sets the property BeanTyper.  This is the 
   * name of the input parameter, which should be a BeanTyper, that
   * we use to based our output parameter's type on.
   */
  public void setBeanTyperParamName(String pBeanTyperParamName) {
    mBeanTyperParamName = pBeanTyperParamName;
  }
  /**
   * @return The value of the property BeanTyperParamName.
   */
  public String getBeanTyperParamName() {
    return mBeanTyperParamName;
  }

  //-------------------------------------------------
  /**
   * Returns the type of the parameter by first getting the type of the
   * input parameter.  If this type is known, and it is known to be an
   * array, we return our type as the component type of that array.
   */
  public ObjectTypeInfo getParamInfo(ParamDescriptorResolver pResolver) {
    ObjectTypeInfo t = pResolver.getParamInfoFromType(mBeanTyperParamName);
    if (t != null) 
      return new ArrayObjectTypeInfo(t);
    else if (getParamClass() != null)
      return new ClassObjectTypeInfo(getParamClass());
    else return null;
  }
}
