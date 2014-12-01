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

package atg.portal.gear.xmlprotocol;

/**
 * This class acts as a base abstract class to build adaptors from.  It implements
 * "getter" and "setter" methods common to all adaptor classes.
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/ConversationAdaptorBase.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public abstract class ConversationAdaptorBase extends atg.nucleus.GenericService implements ConversationAdaptor {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/ConversationAdaptorBase.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Properties

  protected String mUserID;

  public String getUserID(){
    return mUserID;
  }

  public void setUserID(String pUserID){
    mUserID = pUserID;
  }

  protected String mPassword;

  public String getPassword(){
    return mPassword;
  }

  public void setPassword(String pPassword){
    mPassword = pPassword;
  }

  protected String mAuthenticationUrl;

  public String getAuthenticationUrl(){
    return mAuthenticationUrl;
  }

  public void setAuthenticationUrl(String pAuthenticationUrl){
    mAuthenticationUrl = pAuthenticationUrl;
  }

  protected String mCategoriesUrl;

  public String getCategoriesUrl(){
    return mCategoriesUrl;
  }

  public void setCategoriesUrl(String pCategoriesUrl){
    mCategoriesUrl = pCategoriesUrl;
  }

  protected String mHeadlinesUrl;

  public String getHeadlinesUrl(){
    return mHeadlinesUrl;
  }

  public void setHeadlinesUrl(String pHeadlinesUrl){
    mHeadlinesUrl = pHeadlinesUrl;
  }

  protected String mArticleUrl;

  public String getArticleUrl(){
    return mArticleUrl;
  }

  public void setArticleUrl(String pArticleUrl){
    mArticleUrl = pArticleUrl;
  }

   /**
    * Convenience method for retrieveHeadlines with no parameters
    */
    public void retrieveHeadlines() throws ConversationException{
      this.retrieveHeadlines(null);
    }

}