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
 * A tag for retrieving categories from a service provider
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocolTaglib.jar/src/atg/portal/gear/xmlprotocol/taglib/CategoriesTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class CategoriesTag extends XmlProtocolTag {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocolTaglib.jar/src/atg/portal/gear/xmlprotocol/taglib/CategoriesTag.java#2 $$Change: 651448 $";


  /**
   * Constructor
   */
  public CategoriesTag(){
    super();
  }

  //-------------------------------------
  // Properties

  /**
   * The URL to retrieve categories from the service provider
   */
  String mCategoriesUrl;
  public String getCategoriesUrl(){
    return mCategoriesUrl;
  }
  public void setCategoriesUrl(String pUrl){
    mCategoriesUrl=pUrl;
  }


 /**
  * Returns a DOM representation of the retrieved category list
  */
  public Document getXMLCategories(){
    Document theDoc = theConversation.getXMLCategories();
    if (theDoc != null){
      Element theDocElement = theDoc.getDocumentElement();
    }else{
      return null;
    }
    return theDoc;
  }


  /**
   * Returns a list of category names retrieved
   *
   * @returns String[] representing the category names
   */
  public String[] getCategoryNames(){
    return theConversation.getCategoryNames();
  }

  /**
   * Returns a list of unique ids for categories retrieved
   *
   * @returns String[] representing the unique ids
   */
  public String[] getCategoryIDs(){
    return theConversation.getCategoryIDs();
  }

  /**
   * Performs initialization
   *
   * @exception JspException
   */
  protected void init() throws JspException{
   super.init();

   String categoriesUrl=this.getPafEnv().getGearInstanceParameter(PARAM_URL_CATEGORIES);
   if (categoriesUrl!=null){
    this.setCategoriesUrl(categoriesUrl);
   }
  }

  public int doStartTag () throws JspException  {
    long startTime;
    try {
      pageContext.setAttribute (getId (), this);
      startTime = System.currentTimeMillis ();
      this.init();

      //Instantiate the adpator class for handling communications with the news service
      theConversation = (ConversationAdaptor) Class.forName(this.getAdaptorClass()).newInstance();

      if (theConversation == null){
        throw new JspException("The Gear is not configured properly: error instantiating adaptor class");
      }
      theConversation.init(pageContext);
      theConversation.setUserID(this.getUserID());
      theConversation.setPassword(this.getPassword());
      theConversation.setCategoriesUrl(this.getCategoriesUrl());
      theConversation.setAuthenticationUrl(this.getAuthenticationUrl());

      if (this.getParams() != null){
        theConversation.retrieveCategories(this.getParams());
      }else{
        theConversation.retrieveCategories();
      }
    }catch(Exception e){
       this.logError("XmlProtocol Gear: Error in Categories Tag", e);
       return SKIP_BODY;
    }
    return EVAL_BODY_INCLUDE;
  }

  public int doEndTag() throws javax.servlet.jsp.JspTagException {
      return EVAL_PAGE;
  }

  public void release ()   {
    mCategoriesUrl=null;
    super.release();
  }
}