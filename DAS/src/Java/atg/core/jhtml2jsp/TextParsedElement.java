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

/**
 *
 * <p>A parsed element that is a text
 * 
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/jhtml2jsp/TextParsedElement.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class TextParsedElement
  extends ParsedElement
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/jhtml2jsp/TextParsedElement.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Properties
  //-------------------------------------
  // property text

  String mText;
  public String getText ()
  { return mText; }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public TextParsedElement (String pText)
  {
    mText = pText;
  }

  //-------------------------------------
  /**
   *
   * Adds more text to the existing text
   **/
  public void addText (String pText)
  {
    mText = mText + pText;
  }

  //-------------------------------------
  public String toString ()
  {
    return "\"" + mText + "\"";
  }

  //-------------------------------------

}
