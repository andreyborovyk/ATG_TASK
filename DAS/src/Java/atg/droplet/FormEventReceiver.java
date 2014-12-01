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
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.droplet;

import javax.servlet.*;

import java.io.*;
import java.util.*;

/**
 * This class implements the EventReceiving functionality for input, select,
 * and textarea tags.  It is the base class for InputTag, SelectTag, and 
 * TextArea tag.  Programmers should instance those classes to obtain
 * both the rendering and event receiving functionality.  
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/FormEventReceiver.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/
public 
class FormEventReceiver extends Tag implements EventReceiver {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/FormEventReceiver.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  //-------------------------------------
  /** The path name of the property to associate with this tag */
  String mPropertyPath;

  //-------------------------------------
  /** The path names of the entries */
  String [] mPathNames;

  //-------------------------------------
  /** The list of dimensions for each entry */
  int [][] mPathDims;

  //-------------------------------------
  /** true if this is an image input tag */
  boolean mIsImage;

  //-------------------------------------
  /** The value of the name field for this tag (the id to use for this tag) */
  String mName;

  //-------------------------------------
  /** The default value to use if the value is not specified in the request */
  String mDefault;

  //-------------------------------------
  /** The submit value to use for submit + image tags in place of the regular value */
  String mSubmitValue;

  //-------------------------------------
  /** True if any instance of this tag has had a submit value */
  boolean mMayHaveSubmitValue = false;

  //-------------------------------------
  /** The type of the tag */
  String mType;

  //-------------------------------------
  /** The priority of this handler which determines event delivery order */
  int mPriority = DropletConstants.PRIORITY_DEFAULT;

  //-------------------------------------
  FormEventReceiver() {
  }

  //-------------------------------------
  FormEventReceiver (String pName, String pPropertyPath, 
  	       String pType, String pDefault, int pPriority,
  	       String pSubmitValue, TagConverter pConverter,
  	       Properties pConverterArgs) 
       throws DropletException {
    mName = pName;
    setFormAttributes(pPropertyPath, pType, pDefault, pPriority, pSubmitValue);
    setConverter(pConverter);
    setConverterArgs(pConverterArgs);
  }

  //---------------------------
  /* 
   * The event receiver makes sure that it is in the parent's list of
   * EventReceivers.  This list is used by the Form when the form is
   * submitted.
   */
  synchronized void setForm(FormTag pForm) {
    if (mForm != null && mPropertyPath != null) 
      mForm.removeEventReceiver(this);
    mForm = pForm;
    if (mPropertyPath != null && mForm != null)
      mForm.addFormEventReceiver(this);
  }

  /**
   * Return the path name of the component 
   */
  public String getComponentPath() {
    return mPathNames[0];
  }

  //---------------------------
  void setFormAttributes(String pPropertyPath, String pType, String pDefault,
  		     int pPriority, String pSubmitValue) 
         throws DropletException {
    mDefault = pDefault;
    mPriority = pPriority;
    mSubmitValue = pSubmitValue;
    if (pSubmitValue != null) mMayHaveSubmitValue = true;
    setType(pType);
    setPropertyPath(pPropertyPath);
  }

  //---------------------------
  public void setType(String pType) {
    if (mType != pType) {
      mType = pType;
      mIsImage = pType != null && pType.equalsIgnoreCase("image");
    }
  }

  //---------------------------
  /**
   * Returns the type
   */
  public String getType() {
    return mType;
  }

  //---------------------------
  /** 
   * @return the default value to use in a set event if no value was
   * specified 
   */
  public String getDefault() {
    return mDefault;
  }

  /**
   * @return the submit value for this tag
   */
  public String getSubmitValue() {
    return mSubmitValue;
  }

  /**
   * @return the flag that indicates whether this tag has been rendered
   * yet with a submit value.
   */
  public boolean getMayHaveSubmitValue() {
    return mMayHaveSubmitValue;
  }

  public boolean setMayHaveSubmitValue(boolean pValue) {
    return mMayHaveSubmitValue = pValue;
  }

  public int getPriority() {
    return mPriority;
  }

  public void setPriority(int pPriority) {
    mPriority = pPriority;
  }

  public String getName() {
    return mName;
  }

  public void setName(String pName) {
    mName = pName;
  }

  public boolean isImage() {
    return mIsImage;
  }

  /**
   * Sets the property attribute for this tag.  The property is mapped
   * to an absolute property through any imports associated with the
   * tag.
   */
  public void setProperty(String pProperty) 
    throws DropletException {
    if (!DropletNames.isExplicitPath(pProperty)) {
      DropletImports imports = getImports();
      if (imports != null) 
        pProperty = imports.getCompletePath(pProperty);
    }
    setPropertyPath(pProperty);
  }

  /**
   * Sets the path name of the property that this Input/Select etc. tag
   * is associated with.
   */
  public synchronized void setPropertyPath(String pPropertyPath) 
     throws DropletException {
    String oldPropertyPath = mPropertyPath;

    /*
     * We need a valid Form and PropertyPath to be in the Form's event receiver
     * list.
     */
    if (oldPropertyPath != null && pPropertyPath == null && mForm != null) {
      mForm.removeEventReceiver(this);
    }
    mPropertyPath = pPropertyPath;
    DropletNames.initPathNames(this);

    if (mForm != null && oldPropertyPath == null && mPropertyPath != null) {
      mForm.addFormEventReceiver(this);
    }
  }

  /**
   * Return the complete path name of the property
   */
  public String getPropertyPath() {
    return mPropertyPath;
  }

  /**
   * Return the name of the property for the component 
   */
  public String [] getPathNames() {
    return mPathNames;
  }

  /**
   * Return the name of the property for the component 
   */
  public int [][] getPathDims() {
    return mPathDims;
  }

  /**
   * Sets the list of path names.
   */
  public void setPathNames(String [] pPathNames) {
    mPathNames = pPathNames;
  }

  /**
   * Sets the list of dimensions.
   */
  public void setPathDims(int [][] pPathDims) {
    mPathDims = pPathDims;
  }


  TagConverter mConverter = null;
  public TagConverter getConverter() {
    return mConverter;
  }
  public void setConverter(TagConverter pConverter) {
    mConverter = pConverter;
  }

  Properties mConverterArgs = null;
  public Properties getConverterArgs() {
    return mConverterArgs;
  }
  public void setConverterArgs(Properties pConverterArgs) {
    mConverterArgs = pConverterArgs;
  }

  //-------------------------------------
}
