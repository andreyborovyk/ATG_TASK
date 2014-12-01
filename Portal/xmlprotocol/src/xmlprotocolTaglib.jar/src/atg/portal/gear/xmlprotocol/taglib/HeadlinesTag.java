/*<ATGCOPYRIGHT>

 * Copyright (C) 2001-2011 Art Technology Group, Inc.
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

package atg.portal.gear.xmlprotocol.taglib;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.*;

import atg.core.util.StringUtils;
import atg.portal.framework.GearEnvironment;
import atg.portal.framework.Community;

import atg.portal.gear.xmlprotocol.*;

/**
 * A tag to retrieve headlines from a service provider
 *
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocolTaglib.jar/src/atg/portal/gear/xmlprotocol/taglib/HeadlinesTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class HeadlinesTag extends XmlProtocolTag {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocolTaglib.jar/src/atg/portal/gear/xmlprotocol/taglib/HeadlinesTag.java#2 $$Change: 651448 $";


  public static final String DEFAULT_NUM_HEADLINES = "5";

  /**
   * Constructor
   */
  public HeadlinesTag(){
    super();
  }

  //-------------------------------------
  // Properties

  /**
   * The URL to retrieve headlines from the service provider
   */
  String mHeadlinesUrl;
  public String getHeadlinesUrl(){
    return mHeadlinesUrl;
  }
  public void setHeadlinesUrl(String pUrl){
    mHeadlinesUrl=pUrl;
  }

  /**
   * Property to set personalized category filtering on.  Note that if this is
   * set to "true" then categories should not be passed as a parameter since
   * this tag will determine the category to retrieve from the user settings
   */
  String mCategoryFilter = "false";
  public String getCategoryFilter(){
    return mCategoryFilter;
  }

  public void setCategoryFilter(String pCategoryFilter){
    mCategoryFilter = pCategoryFilter;
  }


  /**
   * An indicator for if the headline retrieval is a continuation from a previous search, i.e. "more headlines"
   */
  String mContinueSearch="false";
  public String getContinueSearch(){
    return mContinueSearch;
  }
  public void setContinueSearch(String pContinueSearch){
    mContinueSearch = pContinueSearch;
  }

  /**
   * A marker indicating the next set of headlines to retrienve, like a cursor
   */
  String mBookmarks;
  public String getBookmarks(){
    return mBookmarks;
  }
  public void setBookmarks(String pBookmarks){
    mBookmarks = pBookmarks;
  }

  //-------------------------------------
  // Methods

  /**
   * Returns a DOM representation of the headlines retrieved
   */
  public Document getXMLHeadlines(){
    Document theDoc = theConversation.getXMLHeadlines();
    if (theDoc != null){
      Element theDocElement = theDoc.getDocumentElement();
    }else{
      return null;
    }
    return theDoc;
  }

  protected void init() throws JspException{
     super.init();
     String headlinesUrl=this.getPafEnv().getGearInstanceParameter(PARAM_URL_HEADLINES);
     if (headlinesUrl!=null){
      this.setHeadlinesUrl(headlinesUrl);
     }
     String numHeadlines;
     if (this.getPafEnv().getDisplayMode().equals(GEAR_DISPLAY_SHARED)){
       numHeadlines = this.getPafEnv().getGearUserParameter(PARAM_NUM_SHARED_HEADLINES);
     }else{
       numHeadlines = this.getPafEnv().getGearUserParameter(PARAM_NUM_FULL_HEADLINES);
     }
     if (numHeadlines == null){
      numHeadlines = DEFAULT_NUM_HEADLINES;
     }

     //We need to check to see if the community admin has changed the service provider
     //and the current user personalization settings are for the former service provider.
     //In that case, we should apply the default values.
     String currentAdaptor = this.getAdaptorClass();
     String selectedCategories;
     if (!currentAdaptor.equals(this.getPafEnv().getGearUserParameter(PARAM_LAST_ADAPATOR))){
        selectedCategories = this.getPafEnv().getGearUserDefaultValue(PARAM_FEED_CATEGORIES);
       
     }else{
        
        selectedCategories = this.getPafEnv().getGearUserParameter(PARAM_FEED_CATEGORIES);
     }
     // See if we are doing category filtering and the filter has a list of categories...
     // If so, we add it to the param list passed to the conversation adaptor

     if (selectedCategories!=null&&this.getCategoryFilter().equalsIgnoreCase("true")&&!StringUtils.isBlank(selectedCategories)){
        this.getParams().add(PARAM_FEED_CATEGORIES+"="+selectedCategories);
     }

     this.getParams().add(PARAM_NUM_HEADLINES+"="+numHeadlines);

  }

  public int doStartTag () throws JspException  {
    this.init();
    long startTime;
    try {
       //Instantiate the adpator class for handling communications with the news service
       theConversation = (ConversationAdaptor) Class.forName(this.getAdaptorClass()).newInstance();
       if (theConversation == null){
         return SKIP_BODY;
       }
       theConversation.init(pageContext);
       theConversation.setUserID(this.getUserID());
       theConversation.setPassword(this.getPassword());
       theConversation.setHeadlinesUrl(this.getHeadlinesUrl());
       theConversation.setAuthenticationUrl(this.getAuthenticationUrl());
       if (this.getParams() != null){
         theConversation.retrieveHeadlines(this.getParams());
         this.setBookmarks(theConversation.getBookmarks());
       }else{
         theConversation.retrieveHeadlines();
       }

    }catch(Exception e){
      this.logError("Error in XmlProtocol Gear: ", e);
      return SKIP_BODY;
     }
    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws javax.servlet.jsp.JspTagException {
      return EVAL_PAGE;
  }

  public void release ()   {
    super.release();
    mCategoryFilter="false";
    mContinueSearch="false";
    mBookmarks=null;
    mHeadlinesUrl=null;
  }
}
