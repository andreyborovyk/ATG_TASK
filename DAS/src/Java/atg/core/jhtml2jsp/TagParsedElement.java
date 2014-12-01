/*<ATGCOPYRIGHT>
 * Copyright (C) 2000-2011 Art Technology Group, Inc.
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

package atg.core.jhtml2jsp;

import java.util.Map;
import java.util.Set;
import java.util.Iterator;

/**
 *
 * <p>A parsed element that is a tag
 * 
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/jhtml2jsp/TagParsedElement.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class TagParsedElement
  extends ParsedElement
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/jhtml2jsp/TagParsedElement.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Properties
  //-------------------------------------
  // property text

  String mText;
  public String getText ()
  { return mText; }

  //-------------------------------------
  // property tagName

  String mTagName;
  public String getTagName ()
  { return mTagName; }

  //-------------------------------------
  // property endTag

  boolean mEndTag;
  public boolean getEndTag ()
  { return mEndTag; }

  //-------------------------------------
  // property Wrapped

  boolean mWrapped;
  public boolean getWrapped ()
  { return mWrapped; }

  public void setWrapped(boolean pVal) 
  {mWrapped = pVal;}

  //-------------------------------------
  // property attributes

  Map mAttributes;
  public Map getAttributes ()
  { return mAttributes; }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public TagParsedElement (String pText,
			   String pTagName,
			   boolean pEndTag,
			   Map pAttributes)
  {
    mText = pText;
    mTagName = pTagName;
    mEndTag = pEndTag;
    mAttributes = pAttributes;
    mWrapped = false;
  }

  //-------------------------------------
  /**
   *
   * Returns all of the attributes as a String
   **/
  public String getAttributesString ()
  {
    StringBuffer buf = new StringBuffer ();

    Set keySet = mAttributes.keySet ();
    Iterator iter = keySet.iterator ();
    while (iter.hasNext ()) {
      String key = (String) iter.next ();
      String value = (String) mAttributes.get (key);


      buf.append (" ");
      buf.append (key);
      if (value != null) {
        if (value.startsWith("<dsp:valueof") ||  value.indexOf('\"') != -1) {
          buf.append ("='");
          buf.append (value);
          buf.append ("'");
        }
        else {
          buf.append ("=\"");
          buf.append (value);
          buf.append ("\"");
        }
      }
    }

    return buf.toString ();
  }

  //-------------------------------------
  public String toString ()
  {
    StringBuffer buf = new StringBuffer ();
    buf.append ("TAG: ");
    if (mEndTag) {
      buf.append ("/ ");
    }
    buf.append (mTagName);

    Set keySet = mAttributes.keySet ();
    Iterator iter = keySet.iterator ();
    while (iter.hasNext ()) {
      String key = (String) iter.next ();
      String value = (String) mAttributes.get (key);

      buf.append ("  ");
      buf.append (key);
      if (value != null) {
	buf.append ("=\"");
	buf.append (value);
	buf.append ("\"");
      }
    }

    return buf.toString ();
  }
}
