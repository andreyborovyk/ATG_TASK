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
 * This interface is implemented by various nodes in the SGML tree.  It is 
 * used by the DynamicParamDescriptors to return the dynamic type for 
 * a particular parameter
 *
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ParamDescriptorResolver.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public interface ParamDescriptorResolver {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ParamDescriptorResolver.java#2 $$Change: 651448 $";

  /**
   * Returns the Class for the named parameter, if one is defined, null
   * otherwise.
   */  
  public ObjectTypeInfo getParamInfo(String name);

  /**
   * Resolves the name of a nucleus component path specified and returns
   * the class of the object referenced
   */
  public ObjectTypeInfo getBeanInfo(String beanPath);

  /**
   * Given the path to a bean that is a "dynamic bean type" (i.e. a class
   * for which a DynamicBeanTyper has been registered), this returns the
   * ObjectTypeInfo that this bean typer describes.
   */
  public ObjectTypeInfo getBeanInfoFromType(String beanTypePath);

  /**
   * Given a param name whose value should be a "dynamic bean type", return
   * the ObjectTypeInfo that this bean typer describes.
   */
  public ObjectTypeInfo getParamInfoFromType(String paramName);
}
