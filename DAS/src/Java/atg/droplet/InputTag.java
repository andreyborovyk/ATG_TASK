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

import atg.servlet.*;

import java.io.*;
import java.util.*;

/**
 * This class is used to define an InputTag that is generated dynamically
 * from Java code.  It is a Java Bean that implements properties for each
 * of the input tag attributes.  This includes standard HTML attributes and
 * those that are extensions used for delivering events with droplets.
 * 
 * @author Jeff Vroom
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/InputTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public 
class InputTag extends FormEventReceiver {
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/InputTag.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Type constants

  //-------------------------------------
  public final static String TAG_NAME = "input";

  public final static String TYPE_IMAGE = "image";
  public final static String TYPE_SUBMIT = "submit";
  public final static String TYPE_HIDDEN = "hidden";
  public final static String TYPE_TEXT = "text";
  public final static String TYPE_BUTTON = "button";
  public final static String TYPE_RADIO = "radio";
  public final static String TYPE_RESET = "reset";
  public final static String TYPE_FILE = "file";
  public final static String TYPE_PASSWORD = "password";

  public final static String ALIGN_TOP = "top";
  public final static String ALIGN_MIDDLE = "middle";
  public final static String ALIGN_BOTTOM = "botton";
  public final static String ALIGN_LEFT = "left";
  public final static String ALIGN_RIGHT = "right";

  final static int DEFAULT_SIZE = -666;

  //-------------------------------------
  // Member variables

  //---------------------------
  int mSize = DEFAULT_SIZE;

  //---------------------------
  String mType;

  //---------------------------
  String mSrc;

  //---------------------------
  String mValue;

  //---------------------------
  boolean mChecked = false;

  //---------------------------
  int mMaxLength = DEFAULT_SIZE;

  //---------------------------
  String mAlign;

  //---------------------------
  /** Constructor sets all required attributes */
  public InputTag() {
  }

  //-------------------------------------
  public String getTagName() {
    return TAG_NAME;
  }

  //------------------------------
  /** Sets the value of the size attribute */
  public void setSize(int pSize) {
    mSize = pSize;
  }

  //------------------------------
  /** Returns the value of the size attribute */
  public int getSize() {
    return mSize;
  }

  //------------------------------
  /** Returns the value of the type attribute */
  public String getType() {
    return mType;
  }

  //------------------------------
  /** Sets the value of the type attribute */
  public void setType(String pType) {
    super.setType(pType);
    mType = pType;
  }

  //------------------------------
  /** Returns the value of the value attribute */
  public String getValue() {
    return mValue;
  }

  //------------------------------
  /** Sets the value of the value attribute */
  public void setValue(String pValue) {
    mValue = pValue;
  }

  //------------------------------
  /** Returns the value of the align attribute */
  public String getAlign() {
    return mAlign;
  }

  //------------------------------
  /** Sets the value of the align attribute */
  public void setAlign(String pAlign) {
    mAlign = pAlign;
  }

  //------------------------------
  /** Returns the value of the checked attribute */
  public boolean getChecked() {
    return mChecked;
  }

  //------------------------------
  /** Sets the value of the checked attribute */
  public void setChecked(boolean pChecked) {
    mChecked = pChecked;
  }

  //------------------------------
  /** Returns the value of the maxlength attribute */
  public int getMaxLength() {
    return mMaxLength;
  }

  //------------------------------
  /** Sets the value of the maxlength attribute */
  public void setMaxLength(int pMaxLength) {
    mMaxLength = pMaxLength;
  }

  //------------------------------
  /**
   * Renders this input tag 
   */
  public void service(DynamoHttpServletRequest pReq, 
                      DynamoHttpServletResponse pRes) 
     throws ServletException, IOException {
    ServletOutputStream out = pRes.getOutputStream();

    out.print('<');
    out.print(getTagName());

    if (mType != null) {
      out.print(" type=");
      out.print(mType);
    }
    if (mValue != null) {
      out.print(" value=");
      out.print('"');
      out.print(mValue);
      out.print('"');
    }
    if (mName != null) {
      out.print(" name=");
      out.print('"');
      out.print(mName);
      out.print('"');
    }
    if (mSize != DEFAULT_SIZE) {
      out.print(" size=");
      out.print(mSize);
    }
    if (mSrc != null) {
      out.print(" src=");
      out.print('"');
      out.print(mSrc);
      out.print('"');
    }
    if (mMaxLength != DEFAULT_SIZE) {
      out.print(" maxlength=");
      out.print(mMaxLength);
    }
    if (mAlign != null) {
      out.print(" align=");
      out.print(mAlign);
    }
    if (mChecked) {
      out.print(" checked");
    }

    serviceAttributes(pReq, pRes);

    out.println('>');
  }

  //-------------------------------------
}
