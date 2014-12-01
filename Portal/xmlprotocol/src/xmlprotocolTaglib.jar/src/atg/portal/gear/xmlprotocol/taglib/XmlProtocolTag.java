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

import java.util.ArrayList;

import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.JspException;

import atg.portal.framework.GearEnvironment;
import atg.portal.framework.Community;

import atg.portal.gear.xmlprotocol.ConversationAdaptor;

/**
 * An abstract class containing common properties and methods for XmlProtocol gear tags
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocolTaglib.jar/src/atg/portal/gear/xmlprotocol/taglib/XmlProtocolTag.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

abstract class XmlProtocolTag extends BodyTagSupport {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/xmlprotocolTaglib.jar/src/atg/portal/gear/xmlprotocol/taglib/XmlProtocolTag.java#2 $$Change: 651448 $";


  public static final String GEAR_DISPLAY_SHARED  = "shared";
  public static final String GEAR_DISPLAY_FULL    = "full";

  //Set up our instance and user params we use for various things
  public static final String PARAM_USER_ID                      = "userID";
  public static final String PARAM_PASSWORD                     = "password";

  public static final String PARAM_INSTANCE_USER_ID             = "instanceUserID";
  public static final String PARAM_INSTANCE_PASSWORD            = "instancePassword";
  public static final String PARAM_ENABLE_USERAUTHENTICATION    = "enableUserAuthentication";
  public static final String PARAM_URL_HEADLINES                = "headlinesUrl";
  public static final String PARAM_URL_CATEGORIES               = "categoriesUrl";
  public static final String PARAM_URL_AUTHENTICATION           = "authenticationUrl";
  public static final String PARAM_FEED_CATEGORIES              = "categories";
  public static final String PARAM_NUM_SHARED_HEADLINES         = "numSharedHeadlines";
  public static final String PARAM_NUM_FULL_HEADLINES            = "numFullHeadlines";

  public static final String PARAM_URL_ARTICLE                  = "articleUrl";

  public static final String PARAM_NUM_HEADLINES                = "numHeadlines";

  public static final String PARAM_FEED_ADAPTER_CLASS           = "feedAdaptor";
  public static final String PARAM_STYLESHEET_FULL_HEADLINES    = "fullHeadlinesStylesheetUrl";
  public static final String PARAM_STYLESHEET_FULL_CATEGORIES   = "fullCategoriesStylesheetUrl";
  public static final String PARAM_STYLESHEET_FULL_ARTICLE      = "fullArticleStylesheetUrl";
  public static final String PARAM_STYLESHEET_SHARED_HEADLINES  = "sharedHeadlinesStylesheetUrl";
  public static final String PARAM_STYLESHEET_SHARED_CATEGORIES = "sharedCategoriesStylesheetUrl";
  public static final String PARAM_LAST_ADAPATOR                = "lastAdaptor";

  protected ConversationAdaptor theConversation;

   /**
  * Constructor
  */
  public XmlProtocolTag(){
    super();
  }

  abstract public int doStartTag () throws JspException;


  //-------------------------------------
  // Properties

  /**
   * The id of the tag object
   */
  String mId;
  public String getId () {
    return mId;
  }
  public void setId (String pId) {
    mId = pId;
  }

  /**
   * The gear environment
   */
  GearEnvironment mPafEnv;
  public GearEnvironment getPafEnv(){
    return mPafEnv;
  }
  public void setPafEnv(GearEnvironment pPafEnv){
    mPafEnv = pPafEnv;
  }

  String mGearID;
  public String getGearID(){
    return mGearID;
  }
  public void setGearID(String pGearID){
    mGearID = pGearID;
  }

  /**
   * The URL for the adaptor to authenticate against
   */
  String mAuthenticationUrl;
  public String getAuthenticationUrl(){
    return mAuthenticationUrl;
  }
  public void setAuthenticationUrl(String pUrl){
    mAuthenticationUrl = pUrl;
  }

  /**
   * The user id for authenticating with the service provider
   */
  String mUserID;
  public String getUserID(){
    return mUserID;
  }
  public void setUserID(String pUserID){
    mUserID = pUserID;
  }

  /**
   * The password for authenticating with the service provider
   */
  String mPassword;
  public void setPassword(String pPassword){
    mPassword = pPassword;
  }
  public String getPassword(){
    return mPassword;
  }

  /**
   * A collection of generic name/value pairs to pass to the adaptor class for use
   * as request parameters.  The adaptor class is responsible for mapping
   * the generic name/value pairs to the appropriate parameters for the particular
   * request.
   *
   * Standard gear parameters are defined in the ConversationAdaptor interface as:
   *
   * public static final String PARAM_FEED_CATEGORIES              = "categories";
   * public static final String PARAM_NUM_HEADLINES                = "numHeadlines";
   * public static final String PARAM_HEADLINES_BOOKMARKS          = "bookmarks";
   *
   */
  ArrayList mParams = new ArrayList();
  public ArrayList getParams(){
    return mParams;
  }
  public void setParams(ArrayList pParams){
    mParams = pParams;
  }

  /**
   * Determines which adaptor class to use for communications
   */
  String mAdaptorClass;
  public void setAdaptorClass(String pAdaptorClass){
    mAdaptorClass = pAdaptorClass;
  }
  public String getAdaptorClass(){
    return mAdaptorClass;
  }

  protected void logError(String pMessage, Throwable pException){
    pageContext.getServletContext().log(pMessage, pException);
  }

  /**
   * Performs general initialization common to all gear custom tags.
   * This method should be called by all subclasses.
   *
   * @exception JspExceptionif user id, password or adaptor class are not set
   */
  protected void init() throws JspException{
     pageContext.setAttribute (getId (), this);
     GearEnvironment pafEnv=this.getPafEnv();
     this.setPafEnv(pafEnv);
     if (pafEnv == null){
      throw new JspException("Gear Environment is null");
     }

     this.setGearID(pafEnv.getGear().getId());
     if (mParams == null){
         mParams = new ArrayList();
     }
     String adaptorClass=pafEnv.getGearInstanceParameter(PARAM_FEED_ADAPTER_CLASS);
     String authenticationUrl=pafEnv.getGearInstanceParameter(PARAM_URL_AUTHENTICATION);

     String userID = pafEnv.getGearInstanceParameter(PARAM_INSTANCE_USER_ID);
     String password = pafEnv.getGearInstanceParameter(PARAM_INSTANCE_PASSWORD);

     /* Trap null values but do not test the authentication URL since
        it could be a service that does not require authentication */

     if(userID==null){
        throw new JspException("The Gear is not configured properly: user ID is null");
     }else if (password==null){
        throw new JspException("The Gear is not configured properly: password is null");
     }else if (adaptorClass==null){
        throw new JspException("The Gear is not configured properly: adaptor class is null");
     }
     this.setUserID(userID);
     this.setPassword(password);
     this.setAuthenticationUrl(authenticationUrl);
     this.setAdaptorClass(adaptorClass);
  }

  /**
   * Performs general clean-up for all gear custom tags.
   * This should be called by all subclasses.
   */
  public void release ()   {
    mId = null;
    mPafEnv = null;
    mParams = null;
    mUserID = null;
    if (mParams != null){
      mParams.clear();
    }
    mGearID = null;
  }
}