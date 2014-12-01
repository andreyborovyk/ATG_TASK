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

 import javax.servlet.jsp.*;
 import javax.servlet.jsp.tagext.*;
 import java.util.ArrayList;

 import org.w3c.dom.Document;
 import org.w3c.dom.DocumentType;
 import org.w3c.dom.NamedNodeMap;
 import org.w3c.dom.Node;
 import org.w3c.dom.NodeList;
 import org.w3c.dom.Element;

/**
 * A generic implementation for retrieving XML over HTTP intended to demonstrate
 * how to build a stateful XmlProtocol adaptor.
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/GenericAdaptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public class GenericAdaptor extends XmlConversationAdaptor implements ConversationAdaptor{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/GenericAdaptor.java#2 $$Change: 651448 $";


    public static String ADAPTOR_DESCRIPTION              = "Generic XML News Service";
    public static String ADAPTOR_VERSION                  = "1.0";

    //set up default values
    public final static String GENERIC_AUTHENTICATE_URL   = "http://localhost:8840/genric.login.xml";
    public final static String GENERIC_HEADLINES_URL      = "http://localhost:8840/generic.headlines.xml";
    public final static String GENERIC_FOLDERS_URL        = "http://localhost:8840/generic.categories.xml";
    public final static String GENERIC_ARTICLE_URL        = "http://localhost:8840/generic.article.xml";

    public final static int GENERIC_STATUS_OK             = 0;
    public final static int GENERIC_STATUS_FAIL           = -1;

    public final static int METHOD_FAIL                   = 0;
    public final static int METHOD_SUCCESS                = 1;

    public final static String GENERIC_AUTHENTICATION_STATUS_OK  = "0";
    public final static String GENERIC_BOOKMARKS                 = "bookmarks";
    public final static String GENERIC_CATEGORY_ID               = "categoryid=";
    public final static String GENERIC_NUM_HEADLINES             = "numheadlines";


    //-------------------------------------
    // Properties

    //Set some things as basic defaults
    String mAuthenticationUrl = GENERIC_AUTHENTICATE_URL;
    String mCategoriesUrl     = GENERIC_FOLDERS_URL;
    String mHeadlinesUrl      = GENERIC_HEADLINES_URL;
    String mArticleUrl        = GENERIC_ARTICLE_URL;

    //Session id
    private String mSid;

    //-------------------------------------
    // Methods

    /**
     * Empty constructor to support dynamic class loading
     */

    public GenericAdaptor() {
    }

    /**
     * Initializes the class.  This method must be called after constructing the object
     *
     * @exception Exception if there is a problem initializing
     */
    public void init(PageContext pPageContext,  String pPoolUrl) throws Exception{
      super.init(pPageContext);
    }

    /**
     * Determines whether the client (e.g. a gear) session has already authenticated
     * with the Generic service
     *
     * @returns true if the client is already authenticated
     */
    private boolean getAuthenticated(){
      if (mSid == null){
        //We store the session ID in the user's session and keep it locally
        //here to make access cleaner
        mSid = (String) mPageContext.getSession().getAttribute("XMLPROTOCOL_GENERIC_SESSION_ID");
        if(isLoggingDebug()){
          logDebug("GenericConversation adaptor: looking up session id:" + mSid);
        }
        if (mSid == null){
          return false;
        }
      }
      return true;
    }

    /**
     * A convenience method for authenticating with the Generic service.
     * The userID and password must be set prior to invoking this method.
     *
     * @returns METHOD_FAIL or METHOD_SUCCESS
     */
    public int authenticate() throws ConversationException{
      if (this.getUserID() == null || this.getPassword() == null){
        throw new ConversationException("User Name or password not set");
      }
      return this.authenticate(mUserID,mPassword);
    }

    /**
     * Authenticates with the Generic news service
     * @param UserID and Password
     *
     * @returns METHOD_FAIL or METHOD_SUCCESS
     *
     */

    public int authenticate(String pUserID, String pPassword) throws ConversationException{
      Document theResponse;
      String theUrl = this.getAuthenticationUrl() + "?USER_ID=" + pUserID +"&USER_PASSWORD="+pPassword;
      theResponse = this.connect(theUrl);   //no caching since we are authenticating
      //make sure we have a response
      if (theResponse == null){
         if(isLoggingError()){
          logDebug("GenericConversation adaptor: error authenticating - no document received from Generic service");
        }
        return METHOD_FAIL;
      }
      theResponse.normalize();
      Element theDocElement = theResponse.getDocumentElement();

      String statusCode = theDocElement.getAttribute("status");
      if (statusCode.equals(GENERIC_AUTHENTICATION_STATUS_OK)){
         if (isLoggingDebug()){
           logDebug("GenericConversation adaptor: successful authentication");
         }
         mSid = theDocElement.getElementsByTagName("session.id").item(0).getFirstChild().getNodeValue();
         mPageContext.getSession().setAttribute("XMLPROTOCOL_GENERIC_SESSION_ID",mSid);
         return METHOD_SUCCESS;
      }else{
         String genMsg = theDocElement.getElementsByTagName("error").item(0).getFirstChild().getNodeValue();
         if (genMsg != null){
            if (isLoggingDebug()){
             logDebug("GenericConversation adaptor: error authenticating with Generic service \n Code: "+ statusCode +"\nMessage: \n" + genMsg );
            }
         }
         return METHOD_FAIL;
      }
    }

    Document mXMLHeadlines;

    /**
     * This method returns the Headlines as a DOM object after retrieveHeadlines()
     * called.
     *
     * @returns org3.w3c.Document
     */
    public Document getXMLHeadlines(){
      return mXMLHeadlines;
    }

    private void setXMLHeadlines(Document pXMLHeadlines){
      mXMLHeadlines = pXMLHeadlines;
    }

    Document mXMLCategories;

    public Document getXMLCategories(){
      return mXMLCategories;
    }

    private void setXMLCategories(Document pXMLCategories){
      mXMLCategories = pXMLCategories;
    }

    public String[] getCategoryNames(){
      if (mXMLCategories != null){
        Element theDocElement = mXMLCategories.getDocumentElement();
        theDocElement.normalize();
        NodeList nodes = theDocElement.getChildNodes();
        NodeList categoryNodes = theDocElement.getElementsByTagName("category.name");
        String[] theCategories = new String[categoryNodes.getLength()];
        for (int i=0; i < categoryNodes.getLength(); i++){
          theCategories[i] = categoryNodes.item(i).getFirstChild().getNodeValue();
        }
        return theCategories;
      }else{
        return null;
      }
   }

    public String[] getCategoryIDs(){
      if (mXMLCategories != null){
        Element theDocElement = mXMLCategories.getDocumentElement();
        theDocElement.normalize();
        NodeList nodes = theDocElement.getChildNodes();
        NodeList categoryNodes = theDocElement.getElementsByTagName("category.info");

        String[] theCategories = new String[categoryNodes.getLength()];
        for (int i=0; i < categoryNodes.getLength(); i++){
          theCategories[i] = ((Element)categoryNodes.item(i)).getAttribute("id"); // .getFirstChild().getNodeValue();
        }
        return theCategories;
      }else{
        return null;
      }
    }

    Document mXMLArticle;

    public Document getXMLArticle(){
      return mXMLArticle;
    }

    private void setXMLArticle(Document pXMLArticle){
      mXMLArticle = pXMLArticle;
    }

    String mBookmarks = "";
    public String getBookmarks(){
      return mBookmarks;
    }
    public void setBookmarks(String pBookmarks){
      mBookmarks = pBookmarks;
    }

    /**
     * Convenience method for retrieveHeadlines with no parameters
     */
      public void retrieveHeadlines() throws ConversationException{
        this.retrieveHeadlines(null);
      }

    /**
     * Retrieves news headlines from the Generic service and stores the in the XMLHeadlines
     * property.  If the client session is not authenticated, the method will try to do so.
     *
     * @exception ConversationException if there is an error communicating with Generic.
     *
     */

      public void retrieveHeadlines(ArrayList pParameterList) throws ConversationException{
        Document theResponse;

        //The first thing to do is check to see if we are configured to cache headlines
        //and store the expiration period
        mRequestCacheExpiration = mAdaptorConfig.getCacheHeadlines();
        System.out.println("cache expiration: " + mRequestCacheExpiration);

       // mRequestCacheExpiration = mAdaptorConfig.getCacheHeadlines();
        //default to no search continuation
        //check param values and substitute names with Generic-specific ones
        //With a real-world service provider, we would need to translate these url params
        if (pParameterList != null){
          int paramSize = pParameterList.size();
          String theValue;
          for (int i=0; i< paramSize; i++){
              theValue = (String) pParameterList.get(i);
               if (theValue.startsWith(PARAM_FEED_CATEGORIES+"=")){
                pParameterList.set(i,GENERIC_CATEGORY_ID+"="+theValue.substring(theValue.indexOf("=")+1));
              }else if (theValue.startsWith(PARAM_NUM_HEADLINES+"=")){
                pParameterList.set(i,GENERIC_NUM_HEADLINES+"="+theValue.substring(theValue.indexOf("=")+1));
              }else if (theValue.startsWith(PARAM_HEADLINES_BOOKMARKS+"=")){
                pParameterList.set(i,GENERIC_BOOKMARKS+"="+theValue.substring(theValue.indexOf("=")+1));
              }else{
                //default to pass through
                pParameterList.set(i,theValue);
              }
           }
          theResponse = this.retrieveHandler(pParameterList, getHeadlinesUrl());
        }else{
          //no params to pass
          theResponse = this.retrieveHandler(null, getHeadlinesUrl());
        }
        if(theResponse != null){
          //get bookmarks and save for continuation search in the session object
          NodeList nl = theResponse.getElementsByTagName("category.info");
          int nodeLen = nl.getLength();
          StringBuffer theBooks = new StringBuffer();
          for (int i=0; i<nodeLen-1;i++){
            theBooks.append(((Element)nl.item(i)).getAttribute("bookmark")+",");
          }
          theBooks.append(((Element)nl.item(nodeLen-1)).getAttribute("bookmark"));
          this.setBookmarks(theBooks.toString());
          this.setXMLHeadlines(theResponse);
        }
      }

    /**
     * Convenience method for retrieveHeadlines with no parameters
     */
      public void retrieveCategories() throws ConversationException{
        this.retrieveCategories(null);
      }

      /**
       * Retrieves news categories from the Generic service and stores the in the XMLCategories
       * property.  If the client session is not authenticated, the method will try to do so.
       *
       * @exception ConversationException if there is an error communicating with Generic.
       *
       */
        public void retrieveCategories(ArrayList pParameterList) throws ConversationException{
          Document theResponse;
          mRequestCacheExpiration = mAdaptorConfig.getCacheCategories();

          if (pParameterList != null){
            theResponse = this.retrieveHandler(pParameterList, getCategoriesUrl());
          }else{
            theResponse = this.retrieveHandler(null, getCategoriesUrl());

          }
          if(theResponse != null){
            this.setXMLCategories(theResponse);
          }
      }

      /**
       * Convenience method for retrieveHeadlines with no parameters
       */

      public void retrieveArticle(String pArticleID) throws ConversationException{
        this.retrieveArticle(pArticleID, null);
      }

      /**
       * Retrieves a specific new article from the Generic service and stores the in the XMLArticle
       * property.  If the client session is not authenticated, the method will try to do so.
       *
       * @exception ConversationException if there is an error communicating with Generic.
       *
       */

      public void retrieveArticle(String pArticleID, ArrayList pParameterList) throws ConversationException{
        String[] newParameterList;
        if (pParameterList == null){
          newParameterList = new String[1];
        }
        pParameterList.add("articleid="+pArticleID);
        //This is a hack we would not use in a real world example:
        //since we are using static files, we will just read in the url pointing to the
        //one we are interested in, e.g. articleId=1 will be mapped to http://{article base url}/article1.xml
        //String hackUrl = this.getArticleUrl()+ "/article"+pArticleID+".xml";

        mRequestCacheExpiration = mAdaptorConfig.getCacheArticles();
        //Document theResponse = this.retrieveHandler(pParameterList, hackUrl);
        Document theResponse = this.retrieveHandler(pParameterList, this.getArticleUrl());
        if(theResponse != null){
          this.setXMLArticle(theResponse);
        }
      }

      /**
       * This methods handles the generic communications process with Generic for
       * authenticating, retrieving categories, retrieving headlines, and retrieving articles
       *
       * We are assuming that authentication will be done directly through connect()
       * with no caching.  All other requests will be cached by default.
       *
       * @returns a DOM object representing the information received from Generic
       */
      private Document retrieveHandler(ArrayList pParameterList, String pUrl) throws ConversationException{
        Document theResponse;
        int n;

        //check to see if we have authenticated with the factiva service
        if (!this.getAuthenticated()){
           n=1;
           while (n<=mNumRetries){
              if (isLoggingDebug()){
                 logDebug("Generic Conversation: trying to authenticate attempt " + n);
              }
              if (this.authenticate() == METHOD_SUCCESS){
                 if (isLoggingDebug()){
                     logDebug("Generic Conversation: authenticate sucess");
                  }
                  break;
              }
              n++;
           }
          if (!this.getAuthenticated()){
            throw new ConversationException("Generic Conversation: maximum retries to authenticate exceeded");
          }
        }

        //construct the query string and cache id.  The cache id is the URL without the session id
        StringBuffer cacheIDParams = new StringBuffer();
        StringBuffer queryString = new StringBuffer();
        queryString.append("?XMLPROTOCOL_GENERIC_SESSION_ID="+mSid);    //we always need to pass session id...
        //convert the parameter list to a query string
        if (pParameterList != null){
          cacheIDParams.append(this.makeUrlParams(pParameterList));
          queryString.append(this.makeUrlParams(pParameterList));
        }
        String cacheID = pUrl+cacheIDParams.toString();

        n=1;
        //we now try to retrieve the document the specified number of times
        int status;
        while (n<=mNumRetries){
          theResponse = this.connect(pUrl+queryString.toString(), cacheID);
          if (theResponse != null){
            status = Integer.parseInt(this.getStatusCode(theResponse));
            if (status !=GENERIC_STATUS_OK){
              //TODO: check error message values before attempting to reauthenticate
              //We received an error.  See if session has expired.  If so, try re-authenticating and request the document again
              if (isLoggingDebug()){
                logDebug("GenericConversation adaptor: error retrieving document from Generic service: \n Code: " + status);
              }
              this.authenticate();
            }else{
              return theResponse;
            }
          }
          if (isLoggingDebug()){
            logDebug("GenericConversation adaptor: document retry retrieval attempt " + n );
          }
          n++;
        }
        if (isLoggingError()){
            logError("GenericConversation adaptor: error retrieving document - maximum retries exceeded." );
        }
        throw new ConversationException("GenericConversation adaptor: error retrieving document - maximum retries exceeded");  //assume a failure
      }

      /**
       * Generic supports headline retrieval accross more than one category
       */
      public boolean supportsMultipleCategories(){
        return true;
      }

      /**
       * Determines whether to cache the results.  Basically, we do not want
       * to cache error messages.
       */

      protected long shouldCache(Document theResponse){
        int status = Integer.parseInt(this.getStatusCode(theResponse));
        if (status == GENERIC_STATUS_OK){
           return mRequestCacheExpiration;
        }else{
          return NO_CACHE;
        }
      }

      private String getStatusCode(Document pDoc){
        Element theDocElement = pDoc.getDocumentElement();
        return theDocElement.getAttribute("status");
      }


   }
