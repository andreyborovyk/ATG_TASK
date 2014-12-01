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

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import java.io.Reader;

/**
 *
 * <p>This is a parser that does very basic parsing of a JHTML page.
 * It just looks for things between <...> and turns them into tags
 * unless they are comments.  It doesn't do any sort of escaping of
 * other "<" tags.
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/core/jhtml2jsp/Parser.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class Parser
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/core/jhtml2jsp/Parser.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Constants
  //-------------------------------------

  //-------------------------------------
  // Properties
  //-------------------------------------

  //-------------------------------------
  // Member variables
  //-------------------------------------

  StringBuffer mCurrentToken;
  boolean mParsingTag;
  boolean mInsideQuotes;
  ArrayList mList;

  //-------------------------------------
  /**
   *
   * Constructor
   **/
  public Parser ()
  {
  }

  //-------------------------------------
  /**
   *
   * Parses the specified input stream into ParsedElement objects
   **/
  public ParsedElement [] parse (Reader pIn)
    throws IOException
  {
    mParsingTag = false;
    mInsideQuotes = false;
    mCurrentToken = new StringBuffer ();
    mList = new ArrayList ();

    while (true) {
      int intch = pIn.read ();
      if (intch < 0) break;
      char ch = (char) intch;

      // Process the character
      if (mParsingTag) {
	if (ch == '>' && !mInsideQuotes) {
          boolean selfClosing = false;
          if (mCurrentToken.length() > 0 &&
              mCurrentToken.charAt(mCurrentToken.length()-1) == '/') {
              mCurrentToken.deleteCharAt(mCurrentToken.length()-1);
              selfClosing = true;
          }
	  addTag (mCurrentToken.toString (), selfClosing);
	  mCurrentToken = new StringBuffer ();
	  mParsingTag = false;
	}
	else {
	  mCurrentToken.append (ch);

	  // See if this starts a comment or starts with whitespace or
	  // > or "/[ws]"
	  if ((mCurrentToken.length () == 3 &&
	       mCurrentToken.charAt (0) == '!' &&
	       mCurrentToken.charAt (1) == '-' &&
	       mCurrentToken.charAt (2) == '-') ||
	      (mCurrentToken.length () == 1 &&
	       (mCurrentToken.charAt (0) == '>' ||
		Character.isWhitespace (mCurrentToken.charAt (0)))) ||
	      (mCurrentToken.length () == 2 &&
	       mCurrentToken.charAt (0) == '/' &&
	       Character.isWhitespace (mCurrentToken.charAt (1)))) {
	    addText ("<");
	    addText (mCurrentToken.toString ());
	    mCurrentToken = new StringBuffer ();
	    mParsingTag = false;
            mInsideQuotes = false;
	  }
          else 
            if (ch == '"')
              mInsideQuotes = !mInsideQuotes;
	}
      }
      else {
	if (ch == '<') {
	  addText (mCurrentToken.toString ());
	  mCurrentToken = new StringBuffer ();
	  mParsingTag = true;
	}
	else {
	  mCurrentToken.append (ch);
	}
      }
    }

    // Finish off the existing token
    if (mParsingTag) {
      addText ("<");
    }
    addText (mCurrentToken.toString ());

    return (ParsedElement [])
      mList.toArray (new ParsedElement [mList.size ()]);
  }

  //-------------------------------------
  /**
   *
   * Adds text to the list
   **/
  public void addText (String pText)
  { 
   if (pText.length () > 0) {
      if (mList.size () > 0) {
	ParsedElement elem = (ParsedElement) mList.get (mList.size () - 1);
	if (elem instanceof TextParsedElement) {
	  ((TextParsedElement) elem).addText (pText);
	}
	else {
	  mList.add (new TextParsedElement (pText));
	}
      }
      else {
	mList.add (new TextParsedElement (pText));
      }
    }
  }

  //-------------------------------------
  /**
   *
   * Adds a tag containing the specified text
   **/
  public void addTag (String pText, boolean pSelfClosing)
  {
    // Handle "/"
    if ("/".equals (pText)) {
      addText ("</>");
      return;
    }

    // States

    StringBuffer buf = new StringBuffer ();
    boolean endTag = false;
    boolean insideQuote = false;
    int state = 0;
    String tagName = null;
    Map attributes = new HashMap ();
    String name = null;
    String value = null;
    int ix = 0;

    while (ix < pText.length ()) {
      char ch = pText.charAt (ix);
      switch (state) {

      case 0: // 0  - start
	if (ch == '/') {
	  endTag = true;
	  state = 1;
	}
	else {
	  buf.append (ch);
	  state = 2;
	}
	break;

      case 1: // 1  - [/]
	buf.append (ch);
	state = 2;
	break;

      case 2: // 2  - [/]?[ch]+
	if (Character.isWhitespace (ch)) {
	  tagName = buf.toString ().toLowerCase ();
	  buf = new StringBuffer ();
	  state = 3;
	}
	else {
	  buf.append (ch);
	}
	break;

      case 3: // 3  - [/]?[ch]+[ws]+
	if (!Character.isWhitespace (ch)) {
	  state = 4;
	  buf.append (ch);
	}
	break;

      case 4: // 4  - [/]?[ch]+[ws]+[ch]+
        if (ch == '=') {
	  name = buf.toString ().toLowerCase ();
	  buf = new StringBuffer ();
	  state = 5;
	}
	else if (Character.isWhitespace (ch)) {
          // test for [/]?[ch]+[ws][=]
          int ixnw = nextNotSpaceChar(pText, ix);
          if (ixnw == -1) {
            state = 7;
            break;
          }
          if (pText.charAt (ixnw) == '=')
            break;

	  name = buf.toString ().toLowerCase ();
	  buf = new StringBuffer ();
	  attributes.put (name, null);
	  state = 3;
	}
	else {
	  buf.append (ch);
	}
	break;

      case 5: // 5  - [/]?[ch]+[ws]+[ch]+'='
	if (ch == '\"') {
	  state = 6;
	}
	else if (Character.isWhitespace (ch)) {
          // test for [/]?[ch]+'='+[ws]
          int ixnw = nextNotSpaceChar(pText, ix);
          if (ixnw == -1) {
            state = 7;
            break;
          }
          if (pText.charAt (ixnw) == '\"') {
            break;
          }
          else {
             attributes.put (name, null);
             state = 3;
          }
	}
	else {
	  buf.append (ch);
	  state = 7;
	}
	break;

      case 6: // 6  - [/]?[ch]+[ws]+[ch]+'=''"'[any*]
        if (ch == '`') {
          if (insideQuote)
            insideQuote = false;
          else
            insideQuote = true;
          buf.append (ch);
	  state = 6;
        }
	else if (ch == '\"') {
          if (insideQuote) {
            buf.append (ch);
            state = 6;
          }
	  else {
            value = buf.toString ();
            buf = new StringBuffer ();
            attributes.put (name, value);
            state = 3;
          }
	}
	else {
	  buf.append (ch);
	  state = 6;
	}
	break;

      case 7: // 7  - [/]?[ch]+[ws]+[ch]+'='[ch]*
	if (Character.isWhitespace (ch)) {
	  value = buf.toString ();
	  buf = new StringBuffer ();
	  attributes.put (name, value);
	  state = 3;
	}
	else {
	  buf.append (ch);
	}
	break;
      }

      ix++;
    }


    // Handle the end
    switch (state) {

    case 2: // 2  - [/]?[ch]+
      tagName = buf.toString ().toLowerCase ();
      break;

    case 4: // 4  - [/]?[ch]+[ws]+[ch]+
      name = buf.toString ().toLowerCase ();
      attributes.put (name, null);
      break;

    case 5: // 5  - [/]?[ch]+[ws]+[ch]+'='
      attributes.put (name, null);
      break;

    case 6: // 6  - [/]?[ch]+[ws]+[ch]+'=''"'[any*]
      value = buf.toString ();
      attributes.put (name, value);
      break;

    case 7: // 7  - [/]?[ch]+[ws]+[ch]+'='[ch]*
      value = buf.toString ();
      attributes.put (name, value);
      break;
    }

    TagParsedElement elem =
      new TagParsedElement (pText,
			    tagName,
			    endTag,
			    attributes);
    elem.setSelfClosing(pSelfClosing);
    mList.add (elem);
  }

  //-------------------------------------
  // Test method
  //-------------------------------------
  public static void main (String [] pArgs)
    throws IOException
  {
    java.io.FileReader fin = new java.io.FileReader(pArgs [0]);
    java.io.BufferedReader bin = new java.io.BufferedReader(fin);

    Parser p = new Parser ();
    ParsedElement [] elems = p.parse (bin);

    for (int i = 0; i < elems.length; i++) {
      System.out.println ("[" + i + "] = " + elems [i]);
    }
  }

  //-------------------------------------
  private int nextNotSpaceChar(String pString, int pStartIndex){
    int ix = pStartIndex;
    while (ix < pString.length()) {
      if (pString.charAt(ix)!=' ')
        return ix;
      ix++;
    }
    if (ix >= pString.length())
      return -1;
    return ix;
  }
}
