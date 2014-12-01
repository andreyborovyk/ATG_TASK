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

/**
 * A DynamicParamDescriptor that assumes the same type as the named parameter
 * 
 * @author Joe Berkovitz
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/IdentityParamDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class IdentityParamDescriptor extends DynamicParamDescriptor {

  static final long serialVersionUID = 8017425086012386443L;

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/IdentityParamDescriptor.java#2 $$Change: 651448 $";

  public IdentityParamDescriptor() {}

  //-------------------------------------------------
  /**
   * Construct an ArrayElementParamDescriptor.  Most of these arguments
   * are the same as those to ParamDescriptor.  
   * The last argument, pArrayParamName, is the name of the input parameter
   * that we should base our output parameter's type on.
   */
  public IdentityParamDescriptor(String pName,
                         String pDescription,
                         Class pParamClass,
                         boolean pOptional,
                         boolean pLocal,
                         String pParamName)
  {
    super(pName, pDescription, pParamClass, pOptional, pLocal);
    setParamName(pParamName); 
  }

  //--------- Property: ParamName -----------
  String mParamName;
  /**
   * Sets the property ParamName.  This is the name of the input
   * parameter that we should base our output parameter's type on.
   */
  public void setParamName(String pParamName) {
    mParamName = pParamName;
  }
  /**
   * @return The value of the property ParamName.
   */
  public String getParamName() {
    return mParamName;
  }

  //-------------------------------------------------
  /**
   * Returns the type of the parameter as the type of some
   * input parameter.
   */
  public ObjectTypeInfo getParamInfo(ParamDescriptorResolver pResolver) {
    ObjectTypeInfo t = pResolver.getParamInfo(mParamName);
    if (t != null)
      return t;
    else if (getParamClass() != null)
      return new ClassObjectTypeInfo(getParamClass());
    else return null;
  }
}

