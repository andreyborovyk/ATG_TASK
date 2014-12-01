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
 * An interface describing an adaptor responsable for communicating with a particular
 * XML feed service.  The adoptor should handle the communication protocol,
 * authentication and message semantics.
 *
 * The following model is enforced:
 *
 * - <code>retrieveCategories()</code> - retrieves categories of information such as
 *                                       news subjects or folders
 *
 * - <code>retrieveHeadlines()</code>  - retrieves item descriptions (headlines) associated
 *                                       with particular categories passed as parameters
 *
 * - <code>retrieveArticle()</code>    - retrieves single item of information (an article)
 *                                       based on a unique article ID
 *
 * This messaging semantics assume a stateful protocol in this interface (although a particular
 * adaptor may implement a stateless protocol by implementing a no-op method for authentication)
 *
 * Also, the following additional semantics are assumed to be implemented by the Adaptor class:
 *
 * - Parameters will be passed to <code>retrieveCategories()</code>, <code>retrieveHeadlines(),</code>
 *   and <code>retrieveArticle()</code>
 *
 *   Adaptors are responsible for translating these paramaters to the appropriate query
 *   string for the particular provider service
 *
 * - <code>retrieveHeadlines()<code> has a forward "cursor" capability - i.e. that ability
 *   to retrieve additional headlines starting from the position of the last request.
 *
 * When an adaptor class file is placed in the specified "adaptor deployment directory"
 * for a particular XmlProtocol gear instance, the gear instance will automatically
 * recognize that adaptor and allow a portal community administrator to select it for
 * use.  In order for this to work properly, the adaptor must implement the
 * <code>ConversationAdaptor</code> interface and <code>ADAPTOR_DESCRIPTION</code>
 * must be set to the value that should be displayed to the portal community
 * administrator.
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/ConversationAdaptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public interface ConversationAdaptor {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/ConversationAdaptor.java#2 $$Change: 651448 $";


  /**
   * Gear instance parameters.
   */

  public static final String PARAM_INSTANCE_USERID              = "instanceUserID";
  public static final String PARAM_INSTANCE_PASSWORD            = "instancePassword";
  public static final String PARAM_ENABLE_USERAUTHENTICATION    = "enableUserAuthentication";
  public static final String PARAM_URL_HEADLINES                = "headlinesUrl";
  public static final String PARAM_URL_CATEGORIES               = "categoriesUrl";
  public static final String PARAM_URL_AUTHENTICATION           = "authenticationUrl";
  public static final String PARAM_URL_ARTICLE                  = "articleUrl";
  public static final String PARAM_FEED_ADAPTER_CLASS           = "feedAdaptor";
  public static final String PARAM_STYLESHEET_FULL_HEADLINES    = "fullHeadlinesStylesheetUrl";
  public static final String PARAM_STYLESHEET_FULL_CATEGORIES   = "fullCategoriesStylesheetUrl";
  public static final String PARAM_STYLESHEET_FULL_ARTICLE      = "fullArticleStylesheetUrl";
  public static final String PARAM_STYLESHEET_SHARED_HEADLINES  = "sharedHeadlinesStylesheetUrl";
  public static final String PARAM_STYLESHEET_SHARED_CATEGORIES = "sharedCategoriesStylesheetUrl";

  //Gear user parameters
  public static final String PARAM_USER_ID                      = "userID";
  public static final String PARAM_USER_PASSWORD                = "password";

  //The request parameters
  public static final String PARAM_FEED_CATEGORIES              = "categories";
  public static final String PARAM_NUM_HEADLINES                = "numHeadlines";
  public static final String PARAM_HEADLINES_BOOKMARKS          = "bookmarks";

  public final static int METHOD_FAIL                      = 0;
  public final static int METHOD_SUCCESS                   = 1;

  /**
   * A static variable containing the service provider description.
   * This is used during instance-level configuration of the gear to display
   * a list of available adaptor classes for the portal administrator to select
   * from.
   */
  public static String ADAPTOR_DESCRIPTION                      = "XmlProtocol Adaptor Interface";

  /**
   * A static variable representing the version number of the adaptor
   */
  public static String ADAPTOR_VERSION                          = "1.0";

  public void setUserID(String pUserID);
  public void setPassword(String pPassword);
  public void setAuthenticationUrl(String pAuthenticationUrl);
  public void setCategoriesUrl(String pCategoriesUrl);
  public void setHeadlinesUrl(String pCategoriesUrl);
  public void setArticleUrl(String pArticleUrl);

  /**
   * Performs initialization.
   *
   * @param <code>pPageContext</code> so the adaptor can access the session context.
   *
   * @exception Exception if there was an error initializing.  We need to throw a generic
   *            exception since we cannot anticipate all exception types that may be thrown in
   *            subclasses.
   *
   *
   */
  public void init(javax.servlet.jsp.PageContext pPageContext) throws Exception;


  /**
   * Authenticates with the service provider using the user id and password set
   * previously.
   *
   * @return <code>1</code> if the attempt was successful, <code>0</code> if
   *          authentication failed.
   *
   * @exception ConversationException if there was an error
   *
   */
  public int authenticate() throws ConversationException;

  /**
   * Performs authentication with a service provider for protocols that are stateful.
   *
   * @return <code>METHOD_SUCCESS</code> if successful or <code>METHOD_FAIL</code>
   * if authentication failed.
   *
   * @exception ConversationException if there was an error
   *
   */
  public int authenticate (String pUserID, String pPassword) throws ConversationException;

  /**
   * Retrieves an <code>Document</code> object representing the list of available categories
   * for the given account that was used to authenticate.
   *
   * @exception ConversationException if there was an error
   *
   */
  public void retrieveCategories() throws ConversationException;

  /**
   * This method communicates with the provider service and retrieves an <code>Document</code>
   * object representing the list of available categories for the given account that was
   * used to authenticate.
   *
   * @param pParams an <code>ArraList</code> of paramters to pass to the service provider
   *
   * The categories document may be accessed through <code>getXMLCategories()</code>.
   *
   * The adaptor is responsible for constructing the appropriate service provider
   * query string from the given parameters.  The parameter format is "name=value"
   * and are passed as an ArrayList of strings. Consequently, the adaptor may have
   * to translate parameter names to protocol-specific names.  Currently, the
   * XmlProtocol Gear to does not pass specific category-retrieval parameters as
   * part of its specification. However, the adaptor should perform a "pass-though"
   * of any parameters to the service provider in case any custom parameters are being
   * used.
   *
   * @exception ConversationException if there was an error
   *
   */
  public void retrieveCategories(java.util.ArrayList pParams)  throws ConversationException;

  /**
   * This method communicates with the provider service and retrieves an <code>Document</code>
   * object representing a list of headlines for a set of categories for the given account
   * that was used to authenticate.
   *
   * The headlines document may be accessed through <code>getXMLHeadlines()</code>.
   *
   * @exception ConversationException if there was an error
   *
   */
  public void retrieveHeadlines() throws ConversationException;

  /**
   * This method communicates with the provider service and retrieves an <code>Document</code>
   * object representing a list of headlines for a set of categories for the given account
   * that was used to authenticate.  This method signature also passes parameters used
   * for retrieval. The headlines document may be accessed through
   * <code>getXMLHeadlines()<code>.
   *
   * @param pParams an <code>ArraList</code> of paramters to pass to the service provider
   *
   * The adaptor is responsible for constructing the appropriate service provider
   * query string from the given parameters.  The parameter format is <code>"name=value"</code>
   * and are passed as an ArrayList of strings. Consequently, the adaptor may have
   * to translate parameter names to protocol-specific names.
   *
   * Standard parameters include:
   *
   * - <code>PARAM_FEED_CATEGORIES</code>     A delimited list of category ids to search or display
   *                                          results from
   *
   * - <code>PARAM_NUM_HEADLINES</code>       The number of headlines to return per category
   *
   * - <code>PARAM_HEADLINES_BOOKMARKS</code> A delimited list of pointers marking the current
   *                                          position per category in a query.  Bookmarks are
   *                                          used to retrieve more headlines and are similar to
   *                                          a cursor position in a databse query.
   *
   * Note: In addition to these parameters, the adaptor should perform a "pass-through"
   * of any additional parameters to accommodate customizations.
   *
   * @exception ConversationException if there was an error
   *
   */
  public void retrieveHeadlines(java.util.ArrayList pParams) throws ConversationException;

   /**
   * This method communicates with the provider service and retrieves an <code>Document</code>
   * object representing an article.
   *
   * @param pArticleID an ID representing a unique identifier to retrieve
   *                   an article.
   *
   * @exception ConversationException if there was an error
   *
   */
  public void retrieveArticle(String pArticleID) throws ConversationException;

   /**
   * This method communicates with the provider service and retrieves a <code>Document</code>
   * object representing an article.
   *
   * @param pArticleID an ID representing a unique identifier to retrieve
   *                   an article.
   *
   * @param pParams Currently, the XmlProtocol Gear to does not pass specific
   *                artical-retrieval parameters as part of its specification.
   *                However, the adaptor should perform a "pass-though" of
   *                any parameters to the service provider in case any
   *                custom parameters are being used.
   *
   * @exception ConversationException if there was an error
   *
   */
  public void retrieveArticle(String pArticleID, java.util.ArrayList pParams) throws ConversationException;

  /**
   * Gets the list of categories previously retrieved by <code>retrieveCategories()</code>
   *
   * @return a <code>Document</code> object representing the category list
   */
  public org.w3c.dom.Document getXMLCategories();

  /**
   * Gets the headlines previously retrieved by <code>retrieveHeadlines()</code>
   *
   * @return a <code>Document</code> object representing the headlines
   *
   */
  public org.w3c.dom.Document getXMLHeadlines();

  /**
   * Gets the article previously retrieved by <code>retrieveArticle</code>
   *
  * @return a <code>Document</code> object representing the article
   */
  public org.w3c.dom.Document getXMLArticle();

  /**
   * Gets an array of category names for a particular account.  The account
   * corresponds to the authentication credentials used.
   *
   * This method is used by the XmlProtocl gear to configure user personalization.
   *
   * @return <code>String</code> representing the category names
   */
  public String[] getCategoryNames();

  /**
   * Gets an array of unique category identifiers for a particular account.
   * The identifier array will correspond to the array of category names
   * returned by getCategoryNames(). The account corresponds to the authentication
   * credentials used.
   *
   * This method is used by the XmlProtocl gear to configure user personalization.
   *
   * @return <code>String</code> representing the category names
   */
  public String[] getCategoryIDs();

  /**
   * Gets a delimited list of bookmarks representing the current search position
   * in a retrieve headlines request.  Bookmarks function similar to cursors
   * in database queries.
   *
   * @return <code>String</code> representing the category ids
   */

  /**
   * Returns whether the service provider supports headline retrieval accross
   * multiple categories.  If false, the service provider only supports headline
   * retrieval for single news categories.
   *
   * @return true if the service provider supports multiple category search
   */
  public boolean supportsMultipleCategories();

  /**
   * Returns the current bookmarks (i.e. cursor position) for search-forward
   * category retrieval.
   *
   * @return String containing the list of current bookmarks
   */
  public String getBookmarks();

}
