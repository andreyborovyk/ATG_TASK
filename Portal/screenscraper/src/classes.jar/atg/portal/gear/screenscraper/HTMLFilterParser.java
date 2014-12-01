/*<ATGCOPYRIGHT>
 * Copyright (C) 1998-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of thisadd
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

package atg.portal.gear.screenscraper;


import atg.core.util.StringUtils;
import atg.core.util.StringList;
import atg.servlet.pagefilter.*;
import atg.core.net.URLUtils;
import atg.nucleus.GenericService;

import java.util.Dictionary;
import java.util.Hashtable;
import java.io.*;
import java.net.URL;
import java.net.MalformedURLException;

/**
* 
* Parses html content, filtering any tags specified and rewriting attributes
* for the following tags: 
*<table BORDER CELLSPACING=0 CELLPADDING=0 COLS=2 WIDTH="30%" >
*<tr>
*<td>TAG</td>
*<td>ATTRIBUTE</td>
*</tr>
*<tr>
*<td>a</td>
*<td>href</td>
*</tr>
*<tr>
*<td>area</td>
*<td>href</td>
*</tr>
*<tr>
*<td>form</td>
*<td>action</td>
*</tr>
*<tr>
*<td>go</td>
*<td>href</td>
*</tr>
*<tr>
*<td>iframe</td>
*<td>src</td>
*</tr>
*<tr>
*<td>script</td>
*<td>src</td>
*</tr>
*<tr>
*<td>input</td>
*<td>src</td>
*</tr>
*<tr>
*<td>body</td>
*<td>background</td>
*</tr>
*<tr>
*<td>img</td>
*<td>src</td>
*</tr>
*<tr>
*<td>embed</td>
*<td>src</td>
*</tr>
*<tr>
*<td>link</td>
*<td>src</td>
*</tr>
*<tr>
*<td>link</td>
*<td>href</td>
*</tr>
*<tr>
*<td>td</td>
*<td>background</td>
*</tr>
*<tr>
*<td>table</td>
*<td>background</td>
*</tr>
*<tr>
*<td>body</td>
*<td>background</td>
*</tr>
*<tr>
*<td>frame</td>
*<td>src</td>
*</tr>
*</table>
* 
* @author Ashish Dwivedi
 * @version $Id: //app/portal/version/10.0.3/screenscraper/src/atg/portal/gear/screenscraper/HTMLFilterParser.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
*/

public class HTMLFilterParser 
  extends  GenericService 
{
  //-------------------------------------
  /** Class version string */
  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/screenscraper/src/atg/portal/gear/screenscraper/HTMLFilterParser.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Member variables

  protected int mPos = 0;
  protected int mState = 0;
  protected int mAction = 0;	// the current action.

 
  StringBuffer mBuf = new StringBuffer();
  StringBuffer mTitleBuf = new StringBuffer();
  ParsedTag mTag = null;
  ParsedAttribute mAtt = null;
  OutputStream mOut;
  Writer mWriter;    
  String mLastTagNotWritingBodyFor;
  boolean mWriteThisTagBody= true;
  //weather to not output the current tag in the stream
  boolean mEatCurrentTag =false;
  /** True if we are in a <title></title> tag **/
  boolean mInTitleTag = false;
  protected int [][] mTransitionTable = sTransitionTable;
  protected int [][] mActionTable = sActionTable;

  // maximum number of states
  public static final int MAX_STATES = 20;

  // maximum number of actions currently used
  public static final int MAX_ACTIONS = 40;

  /** The begin and text state, synonyms. */
  public static final int STATE_BEGIN = 0;
  public static final int STATE_TEXT = 0;

  public static final int ACTION_INTO_DECL = 24;
  public static final int ACTION_WITHIN_DECL = 35;
  public static final int ACTION_OUTOF_DECL = 36;


 
  //-------------------------------------
  // Properties

  //-------------------------------------
    /**
    * Creates the dictionary mapping tags that need rewriting
    * (as uppercase) to the name of the attribute that needs rewriting.
    */
  
  protected static Dictionary createRewriteTags ()
  {
    Hashtable t = new Hashtable ();
    /*
     * These need to be rewritten with the given Url or value of HREF in BASE tag if found
     */
    t.put ("A", "HREF");
    t.put ("AREA", "HREF");
    t.put ("FORM", "ACTION");
    t.put ("GO", "HREF");
    t.put ("FRAME", "SRC");
    t.put ("IFRAME", "SRC");
    t.put ("SCRIPT", "SRC");
    
    t.put ("INPUT", "SRC");
    t.put ("BODY", "BACKGROUND");
    t.put ("IMG", "SRC");
    t.put ("EMBED", "SRC");
    t.put ("LINK", "SRC");
    t.put ("LINK", "HREF");
    t.put ("TD", "BACKGROUND");
    t.put ("TABLE", "BACKGROUND");
    t.put ("BODY", "BACKGROUND");
    t.put ("FRAME", "SRC");
    return t;
  }

    /**
     * property Url: The Url as String to be used for rewriting relative URIs.
     */
   
  String mUrl=null;
  
  public void setUrl(String pUrl){
    mUrl = pUrl;
  }
  
  
  public String getUrl(){
    return mUrl;
  }
  

  /** property: RewriteUrlsWithBaseHref, Determined if the HREF attribute value for a 
  * "BASE" tag to be used for rewriting relative urls which may be found in the stream 
  * instead of the Url property
  */
  boolean mRewriteUrlsWithBaseHref = true;
  
  /**
   * Sets property RewriteUrlsWithBaseHref 
   */
  public void setRewriteUrlsWithBaseHref(boolean pRewriteUrlsWithBaseHref) {
    mRewriteUrlsWithBaseHref = pRewriteUrlsWithBaseHref;
  }
  
  /**
  * Returns property RewriteUrlsWithBaseHref
  */
  public boolean  getRewriteUrlsWithBaseHref() {
    return mRewriteUrlsWithBaseHref;
  }
  
  /**
   * property: mTagsToRemove, list of tags to remove from the input stream.
   */
  StringList mTagsToRemove = null;
  
  /**
   * Sets property TagsToRemove 
   */
  public void setTagsToRemove(StringList pTagsToRemove) {
  mTagsToRemove = pTagsToRemove;
  }
  
  /**
   * Returns property TagsToRemove
   */
  public StringList  getTagsToRemove() {
  return mTagsToRemove;
  }
  
  
  /** property: mTagsToRemoveWithContent , remove any tags given here with their content.
   *  Contents will be removed to the end of file if an open tag is present but there is no 
   *  closing tag in the file.
   */
  StringList mTagsToRemoveWithContent = null;
  
  /**
   * Sets property TagsToRemoveWithContent 
   */
  public void setTagsToRemoveWithContent(StringList pTagsToRemoveWithContent) {
    mTagsToRemoveWithContent = pTagsToRemoveWithContent;
  }
  
   /**
    * Returns property TagsToRemoveWithContent
    */
  public StringList  getTagsToRemoveWithContent() {
    return mTagsToRemoveWithContent;
  }

  
    // property replaceBodyTagWithTableTag
  boolean mReplaceBodyTagWithTableTag = true;
  public boolean getReplaceBodyTagWithTableTag () {
    return mReplaceBodyTagWithTableTag;
  }
  public void setReplaceBodyTagWithTableTag (boolean pReplaceBodyTagWithTableTag) {
      mReplaceBodyTagWithTableTag = pReplaceBodyTagWithTableTag;
  }

  /**
   * Constructs an HTMLFilterParser 
   **/
  public HTMLFilterParser()
  {
  }

  /**
   * Constructs an HTMLFilterParser takes the url to be used for rewriting as String
   **/
  public HTMLFilterParser(String pUrlStr) throws MalformedURLException 
  {
    new URL(pUrlStr);
    this.setUrl(pUrlStr);
  }

  /**
   * Reset member variables which reflect a given state in the parser. 
   */
  protected synchronized void resetState() {
    mPos = 0;
    mState = 0;
    mBuf = new StringBuffer();
    mTitleBuf = new StringBuffer();
    mTag = null;
    mAtt = null;
  }

  /**
    Parses the given input stream, filtering out and rewriting
    any URLs found, removing specified tags, and passing everything else through directly to
    the Writer given .
    */
  public void parse(Reader pIn, Writer pWriter)
       throws IOException
  {
    long a = System.currentTimeMillis();  
    /** @todo do error handling here using resource bumdle if pOut is null*/
    mOut =null;
    mWriter = pWriter; 
    // Reset member variables which reflect a given state in the
    // parser. 
    mPos = 0;
    mState = 0;
    mAction = 0;
    /*
     * Clear out the string buffer instead of just setLength(0).  Otherwise
     * the string buffer's char array grows to the maximums size of the
     * buffer and each toString() call will create a new copy at that size.
     */
    mBuf = new StringBuffer();
    mTag = null;
    mAtt = null;
    if(getUrl()==null){
      if(isLoggingError()){
        logError("URL not specified. Please use setUrl() method to set a url before calling the parse method.");
      }
       return;
    }
    if(isLoggingDebug()) { 
      logDebug("URL :"+getUrl() +" specified for rewriting.");
      logDebug("Rewriting relative URIs with :"+getUrlToAppendToRelativeURIs(getUrl().toString()));
      logDebug("Rewriting pure relative URIs with :"+getUrlToAppendToPureRelativeURIs(getUrl().toString()));
    } 
    int ch;

    while ((ch = pIn.read ()) != -1)
    {
      parseChar(ch);
    }
    if(isLoggingDebug()) 
      logDebug("Total reading and parsing time in mili seconds : "+(System.currentTimeMillis()-a));

  }

  /**
   * Parse HTML from the given input stream and write it to the 
   * given OutputStream, rewriting any relative URLs found and 
   * removing tag(s).
   */
  public synchronized void parse(InputStream pIn, OutputStream pOut)
       throws IOException
  {
    long a = System.currentTimeMillis();
    resetState();
    mWriter =null;
    mOut = pOut;
    if(mOut==null) return;
    byte [] dataBuf = new byte[2048];
    
    if(getUrl()==null){
      if(isLoggingError()){
        logError("URL not specified. Please use setUrl() method to set a url before calling the parse method.");
      }
       return;
    }

   if(isLoggingDebug()){ 
      logDebug("URL :"+getUrl() +" specified for rewriting.");
      logDebug("Rewriting relative URIs with :"+getUrlToAppendToRelativeURIs(getUrl().toString()));
      logDebug("Rewriting pure relative URIs with :"+getUrlToAppendToPureRelativeURIs(getUrl().toString()));
    }

     
    int numBytes = 0;
    while (numBytes >= 0) {
      numBytes = pIn.read(dataBuf);  
      for (int c=0; c<numBytes; c++) {        
        parseChar((int)(dataBuf[c] & 0xff));
      }
    }
    if(isLoggingDebug()) 
      logDebug("Total reading and parsing time in mili seconds : "+(System.currentTimeMillis()-a));

  }

  /**
    Parses the given character, filtering out and rewriting
    any URLs found, removing tags, and passing everything else through directly to
    the output stream given to the constructor.  The state of the
    parser is initialized upon construction of the object and persists
    between calls to this method.  However, calls to any of the other
    parse methods, reinitializes the parser state.
    */
  protected void parseChar(int ch) throws IOException
  {

    if (mState >= STATE_APPLET) {
      // if we are in one of the special states for parsing
      // CDATA (APPLET, STYLE, or SCRIPT element content)
      // then handle that
      parseCdataChar(ch);
    }
    else {
      int chTrans = ch;

      if (ch >= 256) {
	// provide a means of substit
	chTrans = getEffectiveTransitionChar(ch, mState);
      }
      
      int newstate = mTransitionTable [mState][chTrans];
      int action = mActionTable [mState][newstate];


//      if (isLoggingDebug()) {
//        System.out.println ("pos: " + mPos + "  ch: " + ch + "  newstate: " + newstate + "  action: " + action + "  real ch: " + (char)ch);
//      }

      mAction = action;
      mState = newstate;

      if (action >= MAX_ACTIONS) {
	action = getEffectiveAction(action, ch);
	if (-1 == action)
	  return;
      }
	

      switch (action) {

       case 1:                   // first char ('<') of tag (or comment or decl)
        // delay writing until we know whether we are dealing with
        // an HTML tag, a comment or a declaration
        // writeTagChar(ch); 
        break;
      
       case 2:                  // start of a tag name
        mTag = new ParsedTag ();
        mTag.setTagPos (mPos);
        mBuf = new StringBuffer();
        writeTagChar('<');
        if (ch == '/')
          mTag.setClose(true);
        else
          mBuf.append ((char) ch);
        writeTagChar(ch);
        break;
       case 16:  // additional attrval chars -- attrVal, no quotes
       case 38:  // additional char in single quoted string attrval
       case 19:  // additional char in double quoted string attrval
        // -- attrVal between quotes  
        mBuf.append ((char) ch);  // collecting chars in an attr val, so don't print here
        break;
       case 3:   // collecting chars for other names/vals, so do print
       case 8:   // attr name
        mBuf.append ((char) ch);
        writeTagChar(ch);
        break;
       case 4:                     // closing '>'
       case 6:                     // closing '>' after whitespace
        mTag.setTagName (mBuf.toString ());
        mTag.setTagEndPos (mPos);
        // handleNewTag (mParsedHTML, mTag);
        handleNewTag (mTag);

        writeTagChar(ch);
        break;
       case 7:                     // begin attr name
       case 22:                    // attrname char after wspace
        mAtt = new ParsedAttribute ();
        mAtt.setAttributeNamePos (mPos);
        mBuf = new StringBuffer();
        mBuf.append ((char) ch);
        writeTagChar(ch);
        break;
       case 9:                     // close '>' after attrname
       case 12:                    // close '>' after equals
        mAtt.setAttributeName (mBuf.toString ());
        mTag.addAttribute (mAtt);
        mTag.setTagEndPos (mPos);
        //handleNewTag (mParsedHTML, mTag);
        handleNewTag (mTag);
        writeTagChar(ch);
        break;
       case 10:                 // '=' after attrname
        mAtt.setAttributeName (mBuf.toString ());
        writeTagChar(ch);
        break;
       case 11:              // wspace within attribute
        mAtt.setAttributeName (mBuf.toString ());
        mTag.addAttribute (mAtt);
        writeTagChar(ch);
        break;
       case 13:                    // wspace after equals
        mTag.addAttribute (mAtt);
        writeTagChar(ch);
        break;
       case 14:  // value or slash ('/') after equals -- AttVal without quote
        // mBuf = new StringBuffer ().append ((char) ch);
        mBuf = new StringBuffer();
        mBuf.append((char)ch);
        mAtt.setAttributeValuePos (mPos);
        break;
       case 15: // quotes('"') after equals -- AttVal with quotes
       case 37: // opening single quote of attrval
        // mBuf = new StringBuffer ();
        mBuf = new StringBuffer();
        mAtt.setAttributeValuePos (mPos);
        writeTagChar(ch);
        break;
       case 17:  // end '>' after attrval -- Completed AttrVal
        mAtt.setAttributeValue (mBuf.toString ());
        mTag.addAttribute (mAtt);
        mTag.setTagEndPos (mPos);
        handleNewAttr(mTag.getTagName(), mAtt);
        //handleNewTag (mParsedHTML, mTag);
        handleNewTag (mTag);
        writeTagChar(ch);
        break;
       case 18:  // wspace after attrval -- Completed AttrVal
        mAtt.setAttributeValue (mBuf.toString ());
        mTag.addAttribute (mAtt);
        handleNewAttr(mTag.getTagName(), mAtt);
        writeTagChar(ch);
        break;
       case 20:  // end quotes of string attrval -- Completed AttrVal
       case 39:  // close quote of single quote attrval
        mAtt.setAttributeValue (mBuf.toString ());
        mAtt.setQuoteChar ((char)ch);
        mTag.addAttribute (mAtt);
        handleNewAttr(mTag.getTagName(), mAtt);
        writeTagChar(ch);
        break;
       case 21:                    // end '>' after wspace
        mTag.setTagEndPos (mPos);
        //handleNewTag (mParsedHTML, mTag);
        handleNewTag (mTag);
        writeTagChar(ch);
        break;
       case 5:                     // between tag and attr
        mTag.setTagName (mBuf.toString ());
        writeTagChar(ch);
        break;
       case 24:			   // begin declaration
				   // should equal ACTION_INTO_DECL

        writeTextChar(ch);
        break;
       case 25:                    // begin possible comment
       case 26:                    // not a comment
       case 27:                    // still could be a comment
       case 28:                    // is a comment
       case 29:                    // possible end comment
       case 30:                    // not end comment
       case 31:                    // more possible end comment
       case 32:                    // not an end comment
       case 33:                    // > end comment
       case 34:                    // more dashes
       case 35:                    // inside of declaration
       case 36:                    // end of declaration
        writeTextChar(ch);
        break;
       case 23:                // opening '!' of a comment or declaration
        writeTextChar('<');
        writeTextChar(ch);
        break;
       default:
        writeTextChar(ch);
        break;
      }
    }
    mPos++;

  } // end parseChar

  /** If the source character to be parsed is greater than
   * 256 (and therefore off the standard transition) table, then this
   * method is called to get the effective transition character.
   */
  protected int getEffectiveTransitionChar(int pSourceChar,
					   int pCurrentState) {
    if (pSourceChar >= 256) {
      // treat the character in question just like an alphabetic character
      return 'z';
    }
    return pSourceChar;
  }


  /**
   * If the action is greater than the maximum known action
   * provide a chance for subclasses to translate it into
   * a known action. -1 means no processing done at all. For all
   * other unknown actions, then the default action, which is
   * writeTextChar().
   *
   * @param pChar the character which caused the action.
   * @param pAction the action from the action table.
   */
  protected int getEffectiveAction(int pAction, int pChar) {
    return pAction;
  }
  
  static final int STATE_APPLET = 100;
  static final int STATE_SCRIPT = 110;
  static final int STATE_STYLE = 120;


  /**
   * Okay... here's the strategy:
   *
   * Since some elements can contain non-html data (APPLET, STYLE, and
   * SCRIPT elements, in particular), we need to deal with those separately.
   * <P>
   *
   * So, we move into the states from 100 to 130. <P>
   *
   * STATE_APPLET = 100 <BR>
   * STATE_SCRIPT = 110 <BR>
   * STATE_STYLE = 120 <BR><P>
   *
   * the base states indicate we're just parsing content, <P>
   *
   * STATE_APPLET + 1 <BR>
   * STATE_SCRIPT + 1 <BR>
   * STATE_STYLE + 1 <BR>
   *
   * Indiciates we've parsed the '<' character <P>
   *
   * STATE_WHATEVER + 2 : parsed '/' after '<' <BR>
   * STATE_WHATEVER + 3 : matched first character of tag name <BR>
   * STATE_WHATEVER + 4 : matched second character of tag name <BR>
   * STATE_WHATEVER + TAGNAME_LENGTH : matched last character of tagname <P>
   *
   * After we've parsed the tag, we jump back to the normal state 2,
   * which indicated we have been parsing a tag name.
   */
  protected void parseCdataChar(int ch) throws java.io.IOException {
    // get the character offset
    int cchOffset = (mState - 100) % 10;
    int stateTag;
    String strTag;
    int tagLength;
    
    if (mState >= STATE_STYLE) {
      stateTag = STATE_STYLE;
      strTag = "STYLE";
      tagLength = 5;
    }
    else if (mState >= STATE_SCRIPT) {
      stateTag = STATE_SCRIPT;
      strTag = "SCRIPT";
      tagLength = 6;
    }
    else {
      stateTag = STATE_APPLET;
      strTag = "APPLET";
      tagLength = 6;
    }

    // look for "</" opening a tag
    if (cchOffset < 2) {
      if (cchOffset == 0) {
        if (ch == '<')
          mState++;
        else {
          writeTextChar(ch);
          mState = stateTag;
        }
      } else if (cchOffset == 1) {
        if (ch == '/') {
          mState++;
          mBuf = new StringBuffer();
        }
        else {
          writeTextChar('<');
          mState = stateTag;
          writeTextChar(ch);
        }
      }
    } else if ((cchOffset - 2) < tagLength) {
      if (strTag.charAt(cchOffset - 2) ==
          Character.toUpperCase((char)ch)) {
        mState++;
        mBuf.append((char)ch);

        if (tagLength == mBuf.length()) {

          // System.err.println("Matched tag " + mBuf.toString());

          mTag.setTagPos(mPos - tagLength);
          mTag.setClose(true);

          // dump out the whole thang as tags
          writeTagChar('<');
          writeTagChar('/');
          for (int i = 0; i < mBuf.length(); i++) {
            writeTagChar(mBuf.charAt(i));
          }
          mState = 2;           // go back to the regular parser
                                // for the next character

          // System.err.println("end cdata");
          
        }
      } else {
        // it's not the close tag in question
        // so dump out everything as text characters
        writeTextChar('<');
        writeTextChar('/');
        for (int i = 0; i < mBuf.length(); i++) {
          writeTextChar(mBuf.charAt(i));
        }
        writeTextChar(ch);
        mBuf = new StringBuffer();
        mState = stateTag;
      }
    } 
  }


  /*
   * Check to see if we are starting the content of a CDATA
   * tag (whose content is not HTML). <P>
   *
   * Go into a special state, if need be.
   */

  protected void checkAndHandleCdataTag(ParsedTag pTag) {

    // if it is a close tag, don't bother
    if (pTag.isClose())
      return;
    
    // state 100 is for APPLET
    // state 110 is for SCRIPT
    // state 120 is for STYLE
    // ... why does this feel like basic all of a sudden?
    String strName = pTag.getTagName();

    if (strName.equalsIgnoreCase("APPLET"))
      mState = STATE_APPLET;
    else if (strName.equalsIgnoreCase("SCRIPT"))
      mState = STATE_SCRIPT;
    else if (strName.equalsIgnoreCase("STYLE"))
      mState = STATE_STYLE;

    if (mState >= STATE_APPLET) {
      // System.err.println("Begin cdata");
      // create a new tag
      // clear out the string buffer
      mTag = new ParsedTag();
      mBuf = new StringBuffer();
    }
  }
  

  /**
   * Called every time an attribute is found inside a tag,
   * subclasses may override this method to do attribute level processing. 
   */
  protected void handleNewAttr(String pTagName, ParsedAttribute pAttr)
    throws IOException
  {
 
  }

  //-------------------------------------
  protected static int [][] sTransitionTable = createTransitionTable ();
  
  static int [][] createTransitionTable ()
  {
    int [][] ret = new int [MAX_STATES][256];

    // 0: ignored chars outside of tags or before a tagname
    ret [0]['<'] = 1;	
    
    // 1: start of a new tag, STATE_AFTER_GT
    assignAllTransitions (ret [1], 0);
    assignNonWhitePrintableTransitions (ret [1], 2);
    ret[1]['!'] = 9;

    // 2: tagname chars, STATE_TAGNAME
    assignAllTransitions (ret [2], 3);
    assignNonWhitePrintableTransitions (ret [2], 2);
    ret [2]['>'] = 0;

    // 3: between tagname and attr name, STATE_IN_TAG_AFTER_WS
    assignAllTransitions (ret [3], 3);
    assignNonWhitePrintableTransitions (ret [3], 4);
    ret [3]['>'] = 0;

    // 4: attribute name, STATE_ATTRNAME
    assignAllTransitions (ret [4], 8);
    assignNonWhitePrintableTransitions (ret [4], 4);
    ret [4]['='] = 5;
    ret [4]['>'] = 0;

    // 5: equals char between attr name & value, STATE_ATTR_EQUALS
    assignAllTransitions (ret [5], 8);
    assignNonWhitePrintableTransitions (ret [5], 6);
    ret [5]['\"'] = 7;
    ret [5]['\''] = 15;
    ret [5]['>'] = 0;
    ret [5]['/'] = 6;

    // 6: non-quoted attr value, STATE_UNQUOTED_ATTRVAL
    assignAllTransitions (ret [6], 8);
    assignNonWhitePrintableTransitions (ret [6], 6);
    ret [6]['>'] = 0;

    // 7: double quoted attr value
    assignAllTransitions (ret [7], 7);
    ret [7]['\"'] = 8;
    // Should add some other transition out of state 7 in
    // case a closing quote is not found???  e.g., [7]['>'] = 0

    // 8: ignored chars within an attribute (name or value),
    //    STATE_IN_TAG_IGNORED_CHARS
    assignAllTransitions (ret [8], 8);
    assignNonWhitePrintableTransitions (ret [8], 4);
    ret [8]['>'] = 0;

    // 9: possible comment -- read "<!"
    assignAllTransitions (ret [9], 14);
    ret [9]['-'] = 10;

    // 10: possible comment -- read "<!-"
    assignAllTransitions (ret [10], 0);
    ret [10]['-'] = 11;

    // 11: within comment -- looking for end comment "-->"
    assignAllTransitions (ret [11], 11);
    ret [11]['-'] = 12;

    // 12: possible end comment -- read "-" looking for "->"
    // default back to within comment    
    assignAllTransitions (ret [12], 11);
    ret [12]['-'] = 13;

    // 13: possible end comment -- read "--" looking for ">"
    // default back to within comment    
    assignAllTransitions (ret [13], 11);
    ret [13]['>'] = 0;
    ret [13]['-'] = 13;

    // 14: in declaration
    assignAllTransitions (ret [14], 14);
    ret[14]['>'] = 0;

    // 15: in single quoted attrval
    assignAllTransitions (ret [15], 15);
    ret [15]['\''] = 8;
    // Should add some other transition out of state 15 in
    // case a closing quote is not found???  e.g., [15]['>'] = 0

    return ret;
  }

  //-------------------------------------
  protected static int [][] sActionTable = createActionTable ();
  static int [][] createActionTable ()
  {
    // should always be sized to the #states/#states
    int [][] ret = new int [MAX_STATES][MAX_STATES];

    // Since the default value for all array elements is 0, state
    // transitions [0][0] and [0][1] are implicitly action 0.

    ret [0][1] = 1;             // opening '<'   -- was 36
    ret [1][2] = 2;             // first char of tag name
    // for ret [1][9] see 36 below
    ret [2][2] = 3;             // tagname chars
    ret [2][0] = 4;             // closing '>'
    ret [2][3] = 5;             // between tag and attr
    ret [3][0] = 6;             // closing '>' after whitespace
    ret [3][4] = 7;             // begin attr name
    ret [4][4] = 8;             // attr name
    ret [4][0] = 9;             // close '>' after attrname
    ret [4][5] = 10;            // '=' after attrname
    ret [4][8] = 11;            // wspace within attribute
    ret [5][0] = 12;            // close '>' after equals
    ret [5][8] = 13;            // wspace after equals
    ret [5][6] = 14;            // value or slash ('/') after equals
    ret [5][7] = 15;            // quotes('"') after equals
    ret [6][6] = 16;            // additional attrval char
    ret [6][0] = 17;            // end '>' after attrval
    ret [6][8] = 18;            // wspace after attrval
    ret [7][7] = 19;            // additional char in string attrval
    ret [7][8] = 20;            // end quotes of string attrval
    ret [8][0] = 21;            // end '>' after wspace
    ret [8][4] = 22;            // attrname char after wspace
    ret [1][9] = 23;            // opening '!' of comment or declaration
    ret [9][14] = 24;           // begin declaration
    
    if (ret [9][14] != ACTION_INTO_DECL) {
      throw new RuntimeException("Constants out of sync.");
    }
    
    ret [9][10] = 25;           // begin possible comment
    ret [10][0] = 26;           // not a comment
    ret [10][11] = 27;          // still could be a comment
    ret [11][11] = 28;          // is a comment
    ret [11][12] = 29;          // possible end comment
    ret [12][11] = 30;          // not end comment
    ret [12][13] = 31;          // more possible end comment
    ret [13][11] = 32;          // not an end comment
    ret [13][0] = 33;           // > end comment
    ret [13][13] = 34;          // more dashes
    ret [14][14] = 35;          // inside of declaration

    if (ret [14][14] != ACTION_WITHIN_DECL) {
      throw new RuntimeException("Constants out of sync.");
    }
    
    ret [14][0] = 36;           // end of declaration

    if (ret [14][0] != ACTION_OUTOF_DECL) {
      throw new RuntimeException("Constants out of sync.");
    }
    
    ret [5][15] = 37;           // opening single quote of attrval
    ret [15][15] = 38;          // additional char of single quote attrval
    ret [15][8] = 39;          // close quote of single quote attrval
    
    return ret;
  }

  //-------------------------------------
  protected static void assignNonWhitePrintableTransitions (int [] t, int state)
  {
    // Every printable but whitespace
    for (int i = 33; i < 127; i++) t [i] = state;
  }

  //-------------------------------------
  protected static void assignAllTransitions (int [] t, int state)
  {
    for (int i = 0; i < 256; i++) t [i] = state;
  }

  /**
   * Writes a tag to the Output Stream or the Writer for this parser properly 
   * creating an HTML tag from the given ParsedTag object.
   */
  protected void writeTag(ParsedTag pTag) throws java.io.IOException 
  {
    if ((mOut == null && mWriter ==null) || pTag ==null) return;
      StringBuffer buf = new StringBuffer ();
      buf.append ("<");
      if (pTag.isClose())
        buf.append("/");
      buf.append(pTag.getTagName ());
      ParsedAttribute attribute = null;
      for (int i = 0; i < pTag.getAttributeCount (); i++) {
        attribute= pTag.getAttribute (i);
        buf.append (" "+attribute.getAttributeName());
        if (attribute.getAttributeValue () != null) {
           buf.append ("=");
           if (attribute.getQuoted ()) buf.append ("\"");
           buf.append (attribute.getAttributeValue ());
           if (attribute.getQuoted ()) buf.append ("\"");
        }
      }
      buf.append ("> ");
      
      if(mOut!=null)
       mOut.write(buf.toString ().getBytes());
      else 
      mWriter.write(buf.toString ());
  }

  /**
   * Writes a tag given as a String to the Output Stream or the Writer associated
   * with this parser.
   */
  protected void writeTag(String pStr) throws java.io.IOException 
  {
    if(mOut!=null)
      mOut.write(pStr.getBytes ());
    else 
      mWriter.write(pStr.toString ());
  }

  /**
   * Writes char to the Output Stream or the Writer associated
   * with this parser.
   */
  protected void writeTagChar(int ch) throws java.io.IOException 
  {
  }

  /** 
   * Writes the tag's content to the Output Stream or Writer associated with 
   * this paraser.
   */

  protected void writeTextChar(int ch) throws java.io.IOException 
  {
    if(mWriteThisTagBody){ 
      if (mOut != null )
         mOut.write(ch);
      else 
        mWriter.write((char)ch);
    }
  }

  /**
   * if the tag is one of those in the Dictionary of tags to rewrite url for, the 
   * attribute of the tag which matches the value corresponding to the key(tag name) 
   * is rewritten with the mUrl property appended to the attribute value. Also if the 
   * tag given is the one present in TagsToRemoveWithContent the body of the tag is
   * not written to the output stream or writer associated with this parser.
   * e.g. if tagname is "A" and tag has an attribute "HREF". Value of HREF attribute is
   * rewritten 
   */

  protected void handleNewTag(ParsedTag pTag) throws java.io.IOException 
  {
    checkAndHandleCdataTag(mTag);
    String tagNameUC = StringUtils.toUpperCase(pTag.getTagName());
    //if a BASE is found and has a valid href rewrite remaining urls with that href
    if(tagNameUC.equalsIgnoreCase("BASE") && getRewriteUrlsWithBaseHref()){
      String baseHref = mTag.getAttributeValue("HREF");
      if(baseHref!=null && isValid(baseHref)){
       if(isLoggingDebug()) logDebug("BASE tag found with valid HREF:"+baseHref + " Using it for further"+
                                     " url rewriting.");
        setUrl(baseHref);
      }
    }

    String []tagsToRemove=null;
    if(getTagsToRemove()!=null)
     tagsToRemove= getTagsToRemove().getStrings();
    boolean writeThisTag = true;
    // if this tag is one of the tags to remove from the stream set flag to not write it
    if(tagPresent(tagsToRemove, tagNameUC)){
      if(isLoggingDebug()) logDebug("Removing tag :"+ pTag.toString());
         writeThisTag = false;
    }

    String []tagsToRemoveWithBody=null;
    if(getTagsToRemoveWithContent()!=null)
     tagsToRemoveWithBody = getTagsToRemoveWithContent().getStrings();
    
    //attempt to eat tag bodies here
    if(tagPresent(tagsToRemoveWithBody, tagNameUC)) {
      if(!mWriteThisTagBody){
        if ((mLastTagNotWritingBodyFor!=null) && 
                (mLastTagNotWritingBodyFor.equalsIgnoreCase(pTag.getTagName()))) {
          if(pTag.isClose()){ 
            mWriteThisTagBody=true;
            mLastTagNotWritingBodyFor=null;
          }
        }
      }
      else{
        if(isLoggingDebug()) logDebug("Removing tag with content:"+ pTag.toString());
        mWriteThisTagBody=false;
        mLastTagNotWritingBodyFor=tagNameUC;
      }
    writeThisTag=false;
    }
 
    if(writeThisTag && mWriteThisTagBody){ 
      String rewriteAttributeValue = pTag.getAttributeValue((String)createRewriteTags().get(tagNameUC));
      String newattributeValue = "";
      if(rewriteAttributeValue!=null){
        String rewriteAttributeValueUpper = rewriteAttributeValue.toUpperCase().trim();
        if (rewriteAttributeValueUpper.startsWith("#") ||
           rewriteAttributeValueUpper.startsWith("MAILTO:") ||
           rewriteAttributeValueUpper.startsWith("NEWS:") ||
           rewriteAttributeValueUpper.startsWith("JAVASCRIPT:") ||
           rewriteAttributeValueUpper.startsWith("HTTP:")||
           rewriteAttributeValueUpper.startsWith("HTTPS:")||
           rewriteAttributeValueUpper.startsWith("FILE:")||
           rewriteAttributeValueUpper.startsWith("FTP:") ||
           rewriteAttributeValueUpper.startsWith("//"))
           //do not rewrite attributes for 
           newattributeValue= rewriteAttributeValue;
        else{
          if(isLoggingDebug()) logDebug("Rewriting tag :"+ pTag.toString() + " with URL "+getUrl());
          newattributeValue = rewriteURL(rewriteAttributeValue, getUrl());
        }
      }
      ParsedAttribute attr = getAttributeFromName(pTag, (String)createRewriteTags().get(tagNameUC));
      if(attr!=null) attr.setAttributeValue(newattributeValue);
      writeTag(pTag);
    }
    //if this tag is body and it is being removed replace it with a table tag and set the background 
    //on the table tag
    if (tagNameUC.equalsIgnoreCase("BODY") && 
        (tagPresent(tagsToRemoveWithBody, "BODY") ||
          tagPresent(tagsToRemove, "BODY"))){
      handleBodyTagReplacementWithTable(pTag);
              
    }
  }// end handleNewTag();
  
  /**
   * Replaces the body tag with a table tag, taking the bodies background and bgcolor
   * and setting them as tables tag's background and bgcolor
   */
  
  private void handleBodyTagReplacementWithTable(ParsedTag pTag) throws IOException{
    if(!getReplaceBodyTagWithTableTag()) return;
    if(!pTag.isClose()){
      ParsedAttribute background =  getAttributeFromName(pTag, "BACKGROUND");
      String rewrittenbackground = "";
      if(background !=null)
        rewrittenbackground = rewriteURL(background.getAttributeValue(), getUrl().toString());
      
      ParsedAttribute bgcolor = getAttributeFromName(pTag, "BGCOLOR");
      String bgcolorstr = "";
      if(bgcolor!=null)
        bgcolorstr = bgcolor.getAttributeValue();
        
      ParsedAttribute text = getAttributeFromName(pTag, "TEXT");
      String textstr = "";
      if(text!=null)
        textstr = text.getAttributeValue();
       writeTag("<TABLE CELLPADDING=\"0\" CELLSPACING=\"0\" BORDER=\"0\" BACKGROUND=\""+rewrittenbackground+"\" BGCOLOR=\""+bgcolorstr+"\"><TR><TD>");
    }
    else {
      writeTag("</TD></TR></TABLE>");
    }
  }
  /**
   * Gets attribute object from the given tag where the attribute name is the given string
   * Returns null if not found.
   */
  private ParsedAttribute getAttributeFromName(ParsedTag pTag, String pAttributeName ){
    for (int i = 0; i < pTag.getAttributeCount (); i++) {
      ParsedAttribute att = pTag.getAttribute (i);
      if (att.getAttributeName ().equalsIgnoreCase (pAttributeName)) 
        return att;
    }
    return null;
  } //end getAttributeFromName()

  /**
   * Returns the result of rewriting the string given with the given url string prepended to it, handling 
   * pure relative urls (those starting with a '/')
   * @param attributeValue the relative or absolute url to be rewritten
   * @param url the url given as string to rewrite attributeValue with
   */
  protected String rewriteURL(String pAttributeValue, String pUrl) throws MalformedURLException{
    if(pUrl==null || pAttributeValue==null) return pAttributeValue;
    String url ="";
    String attributeValue = pAttributeValue.trim();
//    String urlPruned = removeTrailingChars(pUrl,'/');
    String urlPruned = pUrl.trim();
    String newAttributeValue= "";
    if(attributeValue.startsWith("/")){
      attributeValue= attributeValue.substring(1, attributeValue.length());
      url = getUrlToAppendToPureRelativeURIs(urlPruned);
    }
    else
      url = getUrlToAppendToRelativeURIs(urlPruned);

    if(url.trim().endsWith("/"))
      url = url.trim().substring(0, url.length()-1);
    
    newAttributeValue = url +"/"+ attributeValue;
    if(isLoggingDebug()) logDebug("Changing attribute value :"+ pAttributeValue + " to  "+newAttributeValue);
    
    return newAttributeValue   ;

  }// end rewriteURL()           

  /**
   * Returns the url to append to a relative URI given as a string. 
   **/
  private String  getUrlToAppendToRelativeURIs(String pUrlStr) throws MalformedURLException
  {
    if(pUrlStr==null) return "";
    URL url=null;
    if(pUrlStr.indexOf('@') != -1)
      url = new URL(pUrlStr.substring(0, pUrlStr.indexOf('@'))+URLUtils.stripURIArgs(pUrlStr.substring(pUrlStr.indexOf('@'))));
    else   
      url = new URL(URLUtils.stripURIArgs(pUrlStr));
    if((url.getPath().equals("/") || 
       url.getPath().equals("")) || 
       (URLUtils.URIFilename(url.toString()).indexOf('.') == -1))
      return url.toString(); 
    else{
      return URLUtils.URIDirectory(url.toString());
    }
  }


  /**
   * Returns the url to append to a pure relative URI( starting with '/') 
   * given as a string. 
   **/
  private String  getUrlToAppendToPureRelativeURIs(String pUrlStr) throws MalformedURLException{
    if(pUrlStr==null) return "";
    URL url=null;
    if(pUrlStr.indexOf('@') != -1)
      url = new URL(pUrlStr.substring(0, pUrlStr.indexOf('@'))+URLUtils.stripURIArgs(pUrlStr.substring(pUrlStr.indexOf('@'))));
    else   
      url = new URL(URLUtils.stripURIArgs(pUrlStr));
    String urlStr = url.toString();
    if(url.getPath().equals("/") || url.getPath().equals(""))
      return url.toString();
    else
      return urlStr.substring(0,urlStr.indexOf(url.getPath()));
  }
  
  /**
   * case insensitive search for a tag name in the given array
   */
 
  private boolean tagPresent (String [] pTagArray, String pTagName){
    if (pTagArray == null || pTagArray.length==0) return false;
    for (int i = 0; i <pTagArray.length; i++) {
      if(pTagArray[i].equalsIgnoreCase(pTagName)) 
        return true;
    }
    return false;
  }  
  
  /**
   * Remove trailing slashes from the given string.
   */
  private String removeTrailingChars(String pStr, char ch){
    if(pStr==null || pStr.equals("")) return pStr;
      while(pStr.lastIndexOf(ch)== pStr.length()-1)
        pStr  = pStr.substring(0,pStr.length()-1);
    return pStr;
  }

  /**
   * checks if the url given as string is valid
   */
  private boolean isValid(String pUrl){
    try {
      new URL(pUrl);
      return true;
    }
    catch(MalformedURLException ex ){
      return false;
    }
  }

} // end class

