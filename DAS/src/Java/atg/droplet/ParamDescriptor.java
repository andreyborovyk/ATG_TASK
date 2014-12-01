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

import atg.servlet.*;
import atg.core.util.ClassAlias;

/**
 * <P>BeanDescriptor subclass for droplets, permitting retrieval of
 * parameter descriptions.
 *
 * @author Joe Berkovitz
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ParamDescriptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ParamDescriptor extends atg.beans.SerializableFeatureDescriptor
{
  static final long serialVersionUID = 4943852733240406323L;

  //-------------------------------------
  // CONSTANTS
  //-------------------------------------
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ParamDescriptor.java#2 $$Change: 651448 $";

  //-------------------------------------
  // FIELDS
  //-------------------------------------

  /** True if this parameter is optional */
  boolean mOptional = false;

  /** True if this parameter is local */
  boolean mLocal = false;

  /** Class or interface of this param, if specified */
  ClassAlias mParamClass = null;

  /** Set of parameters required by an OPARAM, if this is one. */
  ParamDescriptor[] mRenderParamDescriptors = null;


  //-------------------------------------
  // CONSTRUCTORS
  //-------------------------------------

  public ParamDescriptor() {
  }

  public ParamDescriptor(String pName,
                         String pDescription,
                         Class pParamClass,
                         boolean pOptional,
                         boolean pLocal,
                         ParamDescriptor[] pRenderParamDescriptors)
  {
    setName(pName);
    setShortDescription(pDescription);
    setParamClass(pParamClass);
    setOptional(pOptional);
    setLocal(pLocal);
    setRenderParamDescriptors(pRenderParamDescriptors);
  }

  public ParamDescriptor(String pName,
                         String pDescription,
                         Class pParamClass,
                         boolean pOptional,
                         boolean pLocal)
  {
    this (pName, pDescription, pParamClass, pOptional, pLocal, null);
  }

  public ParamDescriptor(String pName,
                         String pDescription,
                         Class pParamClass)
  {
    this (pName, pDescription, pParamClass, false, false, null);
  }

  public ParamDescriptor(String pName,
                         String pDescription)
  {
    this (pName, pDescription, null, false, false, null);
  }


  //-------------------------------------
  // METHODS
  //-------------------------------------


  /**
   * Sets property Optional
   **/
  public void setOptional(boolean pOptional) {
    mOptional = pOptional;
  }

  /**
   * Returns property Optional
   **/
  public boolean isOptional() {
    return mOptional;
  }


  /**
   * Sets property Local
   **/
  public void setLocal(boolean pLocal) {
    mLocal = pLocal;
  }

  /**
   * Returns property Local
   **/
  public boolean isLocal() {
    return mLocal;
  }


  /**
   * Sets property ParamClass
   **/
  public void setParamClass(Class pParamClass) {
    mParamClass = new ClassAlias(pParamClass);
  }

  /**
   * Returns property ParamClass
   **/
  public Class getParamClass() {
    if ( null != mParamClass )
      return mParamClass.getAliasedClass();
    return null;
  }


  /**
   * Returns true if this parameter is renderable
   */
  public boolean isRenderable() {
    return DynamoServlet.class.isAssignableFrom(getParamClass());
  }


  /**
   * Sets property RenderParamDescriptors
   **/
  public void setRenderParamDescriptors(ParamDescriptor[] pRenderParamDescriptors) {
    mRenderParamDescriptors = pRenderParamDescriptors;
  }

  /**
   * Returns property RenderParamDescriptors
   **/
  public ParamDescriptor[] getRenderParamDescriptors() {
    return mRenderParamDescriptors;
  }


  //----------------------------------------
  /**
   * String representation
   */
  public String toString() {
    return getName();
  }
}
