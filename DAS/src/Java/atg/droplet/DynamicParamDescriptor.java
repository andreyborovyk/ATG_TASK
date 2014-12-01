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
 * This is an abstract class for ParamDescriptors that should be extended
 * by the various ways to derive the type of a parameter dynamically.
 * The resolver can be used to access the types + values of other parameters
 * that this parameter may depend upon.
 * <p>
 * Sub-classes of this can be relatively generic: ArrayElementOfParamDescriptor
 * ArrayOfParamDescriptor, or fairly specific: TargetingArrayParamDescriptor.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/DynamicParamDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public abstract class DynamicParamDescriptor extends ParamDescriptor {
  static final long serialVersionUID = -2458315552926176035L;

  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/DynamicParamDescriptor.java#2 $$Change: 651448 $";

  //-------------------------------------------------
  public DynamicParamDescriptor() {
  }

  //-------------------------------------------------
  /**
   * Construct an ArrayElementParamDescriptor.  Most of these arguments
   * are the same as those to ParamDescriptor.  
   * The last argument, pArrayParamName, is the name of the input parameter
   * that we should base our output parameter's type on.
   */
  public DynamicParamDescriptor(String pName,
                         String pDescription,
                         Class pParamClass,
                         boolean pOptional,
                         boolean pLocal)
  {
    super (pName, pDescription, pParamClass, pOptional, pLocal);
  }

  /**
   * Returns an ObjectTypeInfo that describes the type of this parameter.
   * This method uses the pResolver argument to lookup the types of its
   * input parameters and defines this parameter in terms of those types.
   */
  public abstract ObjectTypeInfo getParamInfo(ParamDescriptorResolver pResolver);
}
