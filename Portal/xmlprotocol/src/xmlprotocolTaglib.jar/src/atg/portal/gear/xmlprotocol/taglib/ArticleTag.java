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
import atg.portal.gear.xmlprotocol.*;

/**
 * A tag for retrieving an article from a serivce provider
 *
 * @see XmlProtocolTag
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocolTaglib.jar/src/atg/portal/gear/xmlprotocol/taglib/ArticleTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ArticleTag extends XmlProtocolTag {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocolTaglib.jar/src/atg/portal/gear/xmlprotocol/taglib/ArticleTag.java#2 $$Change: 651448 $";


  /**
   * Constructor
   */
  public ArticleTag()
  {
    super();
  }

  /**
   * Performs general initialization
   *
   * @exception JspException if theire was an error
   */
  protected void init() throws JspException{
   super.init();
   String articleUrl=this.getPafEnv().getGearInstanceParameter(PARAM_URL_ARTICLE);
   if (articleUrl!=null){
    this.setArticleUrl(articleUrl);
   }
  }

  /**
   * The unique id for retrieving the article
   */
  String mArticleID;
  public String getArticleID(){
    return mArticleID;
  }
  public void setArticleID(String pArticleID){
    mArticleID = pArticleID;
  }

  /**
   * The service provider URL for retrieving the article
   */
  String mArticleUrl;
  public String getArticleUrl(){
    return mArticleUrl;
  }
  public void setArticleUrl(String pArticleUrl){
    mArticleUrl = pArticleUrl;
  }

  /**
   * Gets a <code>Document</code> representing the retrieved article contents
   *
   * Returns Document
   */
  public Document getXMLArticle(){
    Document theDoc = theConversation.getXMLArticle();
    if (theDoc != null){
      Element theDocElement = theDoc.getDocumentElement();
    }else{
      return null;
    }
    return theDoc;
  }



  public int doStartTag () throws JspException  {
    long startTime;
    super.init();
    try {
      pageContext.setAttribute (getId (), this);
      startTime = System.currentTimeMillis ();
      this.init();

      //Instantiate the adpator class for handling communications with the service provider
      theConversation = (ConversationAdaptor) Class.forName(this.getAdaptorClass()).newInstance();

      if (theConversation == null){
        throw new JspException("The Gear is not configured properly: error instantiating adaptor class");
      }
      theConversation.init(pageContext);
      theConversation.setUserID(this.getUserID());
      theConversation.setPassword(this.getPassword());
      theConversation.setArticleUrl(this.getArticleUrl());
      theConversation.setAuthenticationUrl(this.getAuthenticationUrl());

      if (this.getParams() != null){
        theConversation.retrieveArticle(this.getArticleID(),this.getParams());
      }else{
        theConversation.retrieveArticle(this.getArticleID());
      }
    }catch(Exception e){
       this.logError("XmlProtocol Gear: Error in Article Tag", e);
       return SKIP_BODY;
    }
    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws javax.servlet.jsp.JspTagException {
      return EVAL_PAGE;
  }

  public void release ()   {
    mArticleUrl=null;
    mArticleID=null;
    super.release();
  }
}