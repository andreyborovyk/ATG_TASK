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
 * <p>Represents one element parsed from a file, either a tag or text.
 * 
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/jhtml2jsp/ParsedElement.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public abstract class ParsedElement
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/jhtml2jsp/ParsedElement.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Properties
  //-------------------------------------
  // property shouldRender

  boolean mShouldRender = true;
  public boolean getShouldRender ()
  { return mShouldRender; }
  public void setShouldRender (boolean pShouldRender)
  { mShouldRender = pShouldRender; }

  //-------------------------------------
  // property selfClosing

  boolean mSelfClosing = false;
  public boolean isSelfClosing ()
  { return mSelfClosing; }
  public void setSelfClosing (boolean pSelfClosing)
  { mSelfClosing = pSelfClosing; }

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public ParsedElement ()
  {
  }

  //-------------------------------------

}
