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

import java.net.*;
import java.io.*;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.*;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import atg.xml.tools.XMLToolsFactory;
import atg.xml.XMLFileEntityResolver;
import atg.xml.tools.XMLToDOMParser;

import atg.nucleus.GenericContext;
import atg.nucleus.ServiceListener;
import atg.nucleus.ServiceEvent;

import java.net.URLConnection;


import atg.portal.gear.xmlprotocol.cache.*;

/**
 * An abstract class to implement low-level services for retrieval of XML documents over HTTP
 * This class performs HTTP communications and handles content caching using an LRU
 * mechanism.
 *
 * These low-level services should be handled at the adaptor level since it should
 * choose the optimal communications method for a particular provider.
 *
 * XmlConversationAdaptor should be subclassed to add protocol features including
 * document parsing and semantics handling for the particular provider.  It should also
 * override the shouldCache() method to determine when to cache particular content.
 * For example, received error messages should not be cached.
 *
 * @author J Marino
 * @version $Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/XmlConversationAdaptor.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

public abstract class XmlConversationAdaptor extends ConversationAdaptorBase {

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/xmlprotocol/classes.jar/src/atg/portal/gear/xmlprotocol/XmlConversationAdaptor.java#2 $$Change: 651448 $";


  public static String ADAPTOR_DESCRIPTION            = "XmlConversation Adaptor";
  public static String ADAPTOR_VERSION                = "1.0";

  public final static long NO_CACHE                    = -1;
  public final static long CACHE_FOREVER               = 0;


  /**
   * The JNDI location of the bootstrap adaptor config class set as a global bean in Nucleus
   * We assume the name will be the adaptor class name with "Config" apended or, if not found,
   * a default configuration file
   **/

  protected String ADAPTOR_CONFIG          = "dynamo:/" + (this.getClass().getName()).replace('.','/') +"Config";
  protected String ADAPTOR_DEFAULT_CONFIG  = "dynamo:/atg/portal/gear/xmlprotocol/DefaultAdaptorConfig";
  protected AdaptorConfig mAdaptorConfig = null;

  protected CacheConfig mCacheConfig = null;
  /**
   * This is a placeholder for how long to cache content of a given request.
   * We set this here so we do not have to parse a response document to find its
   * type since we know what it is based on the request.
   */
  protected long mRequestCacheExpiration = -1;

  //The page context from the web application
  protected PageContext mPageContext;

  //JNDI location of XML processor in Nucleus we will be using for this adaptor
  protected XMLToolsFactory mXMLToolsFactory;
  protected XMLToDOMParser mXMLParser;

  //The LRU cache for contents
  protected LRUCache mTheCache;

  //Number of times to retry a request in the event of an error
  protected int mNumRetries;

  public long mCacheArticles;
  public void setCacheArticles(long pCacheTime){
    mCacheArticles = pCacheTime;
  }
  public long getCacheArticles(){
    return mCacheArticles;
  }

  public long mCacheCategories;
  public void setCacheCategories(long pCacheTime){
    mCacheCategories = pCacheTime;
  }
  public long getCacheCategories(){
    return mCacheCategories;
  }

  public long mCacheHeadlines;
  public void setCacheHeadlines(long pCacheTime){
    mCacheHeadlines = pCacheTime;
  }
  public long getCacheHeadlines(){
    return mCacheHeadlines;
  }

   /**
   * Empty constructor to support dynamic class loading by the gear.
   */
  public XmlConversationAdaptor() {}

  /**
   * Initializes the class including the HTTP connection pool and response cahce, if ncessary.
   * This method must be called after constructing the object.
   *
   * @exception Exception if there is a problem initializing
   */

  public void init(PageContext pPageContext) throws Exception{

    // we need to store the web application page conxtex to we have access to
    // session information
    mPageContext = pPageContext;
    String nucleusLocation;

    javax.naming.Context ctx = new javax.naming.InitialContext();

    //get the configuration object
    mAdaptorConfig = (AdaptorConfig) ctx.lookup(ADAPTOR_CONFIG);

    if (mAdaptorConfig == null){
      if (this.isLoggingDebug()){
        this.logDebug("XmlProtocol Gear: Adaptor configuration not found in Nucleus for " +
              ADAPTOR_CONFIG +" \nUsing default configuration instead.");
      }
      mAdaptorConfig = (AdaptorConfig) ctx.lookup(ADAPTOR_DEFAULT_CONFIG);
      if (mAdaptorConfig == null){
        throw new ConversationException("Adaptor configruation not found in Nucleus: " + ADAPTOR_DEFAULT_CONFIG);
      }
    }

    nucleusLocation = mAdaptorConfig.getXMLToolsContext();
    if (nucleusLocation == null){
      throw new ConversationException("No nucleus location specified for XMLTools");
    }
    mXMLToolsFactory = (XMLToolsFactory) ctx.lookup(nucleusLocation);
    if (mXMLToolsFactory == null){
      throw new ConversationException("XML parser factory not found in Nucleus: " + mAdaptorConfig.getXMLToolsContext());
    }
    mXMLParser = mXMLToolsFactory.createXMLToDOMParser();

    //Bind to the response cache context for the gear in Nucleus
    nucleusLocation = mAdaptorConfig.getCacheConfig();
    if (nucleusLocation == null){
       throw new ConversationException("No nucleus location specified for cache configuration");
    }
    mCacheConfig = (CacheConfig) ctx.lookup(nucleusLocation);
    if (mCacheConfig == null){
      throw new ConversationException("Could not find cache configuration in adaptor configuration object");
    }

    nucleusLocation = mCacheConfig.getCacheContext();
    if (nucleusLocation == null){
       throw new ConversationException("No nucleus location specified for cache context");
    }
    GenericContext cacheContext = (GenericContext) ctx.lookup(nucleusLocation);
    if (cacheContext == null){
      throw new ConversationException("Could not find cache context in adaptor configuration object");
    }
    //See if the cache has already been created.  If not, create, configure, and start it
    //Get the name of our cache for setting it up in Nucleus
    String cacheName = mCacheConfig.getCacheName();
    if (cacheName == null){
      throw new ConversationException("Nucleus cache name not found in adaptor configuration object");
    }

    mTheCache = (LRUCache) cacheContext.getElement(cacheName);
    if(mTheCache == null){  //There was no cache so set one up
      //The cache will store its contents as byte[] so we point it to the class responsible for storing
      //byte[] in the cache
      mTheCache = new LRUCache("atg.portal.gear.xmlprotocol.cache.CacheByteArrayElement", mCacheConfig.getMaxCacheSize());
      //put the cache in the Nucleus context
      cacheContext.putElement(cacheName, mTheCache);
      ServiceEvent theEvent = new ServiceEvent(cacheContext, mTheCache, cacheContext.getNucleus(), null);
      ((ServiceListener) mTheCache).startService (theEvent);
    }

    mNumRetries = mAdaptorConfig.getNumRetries();
    if (mNumRetries < 1){
      mNumRetries = 3;
      if (this.isLoggingDebug()){
        this.logDebug("XmlProtocol Gear: number of retries not properly specified in adaptor configuration setting.  Resorting to default: " + mNumRetries);
      }
    }
    if (this.isLoggingDebug()){
      this.logDebug("XmlProtocol Gear: finished XmlConversationAdaptor init()");
    }

  }

  /**
   * Connects to a URL using HTTP and retrieves an XML Document without caching
   *
   * @param pUrl the URL to connect to
   *
   * @exception ConversationException if there was a problem communicating with
   * the service provider
   *
   */
  protected Document connect(String pUrl)throws ConversationException{
    return this.connect(pUrl,null);
  }

  /**
   * Connects to a URL using HTTP and retrieves an XML Document.
   * The subclass determines when caching is appropriate and what key to use.
   *
   * @param pUrl the URL to connect to
   * @param pCacheKey the unique cache identifier for the returned contents
   *
   * @returns Document as a representation of the XML Document
   *
   * @exception ConversationException if there was a problem communicating with
   * the service provider
   */
  protected Document connect(String pUrl, String pCacheKey) throws ConversationException{
    long startTime;
    long totalTime;
    int code;

    Document theXMLResponse = null;
    InputStream theStream = null;
    ByteArrayElementContents theContents = null;
    ByteArrayInputStream byteInputStream = null;

    //We use an optimized HTTP connection since the one in the standard Java library
    //is so slow
    java.net.URLConnection theConnection=null;

    try{
      URL u = new URL(pUrl);
      
      //System.out.println("The url is:" + pUrl);
      startTime = System.currentTimeMillis();
      if (pCacheKey != null){   // search in the cache if we have a key
         StringKey cacheKey = new StringKey(pCacheKey);
         // try and get the content out of the cache
         CacheByteArrayElement byteElement = (CacheByteArrayElement) mTheCache.get(cacheKey);
         if (byteElement != null){     // we found the contents in the cache
            theContents = byteElement.getContents();
            if (theContents != null){  // null should not happen
              //parse the contents
              theXMLResponse = mXMLParser.parse(theContents.getContentsAsStream(),false,null);
              totalTime = System.currentTimeMillis () - startTime;
              //System.out.println("XMLConversation: Total elapsed time retrieve from cache and parse contents: " + totalTime);

              if (this.isLoggingDebug()){
                totalTime = System.currentTimeMillis () - startTime;
                this.logDebug("XMLConversation: Total elapsed time retrieve from cache and parse contents: " + totalTime);
                //System.out.println("XMLConversation: Total elapsed time retrieve from cache and parse contents: " + totalTime);
              }
            }
         }else{  // not found in cache so go fetch...
            theConnection = u.openConnection();
            theStream = new BufferedInputStream(theConnection.getInputStream());

            byteInputStream = new ByteArrayInputStream(this.readStream(theStream));

            //First get a new input stream of the contents since the parser will close it.
            ByteArrayElementContents cacheContents = new ByteArrayElementContents(byteInputStream);
            // Parse the contents first to avoid caching invalid responses or error messages
            // We could have chosen to cache the parsed contents to avoid do this repeatedly but
            // the trade-off in memory overhead of storing DOM objects versus the minimal performance
            // improvement gained from avoiding re-parsing and stream copies did not seem worth it.
            theXMLResponse = mXMLParser.parse(cacheContents.getContentsAsStream(),false,null);
            totalTime = System.currentTimeMillis () - startTime;

            //System.out.println("XMLConversation: Total elapsed time retrieve and parse contents from the service provider: " + totalTime);

            if (this.isLoggingDebug()){
              totalTime = System.currentTimeMillis () - startTime;
              this.logDebug("XMLConversation: Total elapsed time retrieve and parse contents from the service provider: " + totalTime);
              //System.out.println("XMLConversation: Total elapsed time retrieve and parse contents from the service provider: " + totalTime);
            }

            //now cache the contents if it is appropriate - e.g. do not cache error messages
            // returned by the service provider
            long expires = this.shouldCache(theXMLResponse);
            //System.out.println("XMLConversation: caching contents with key: " + cacheKey + " and expiration: "+ expires);

            if (this.isLoggingDebug()){
              //System.out.println("XMLConversation: caching contents with key: " + cacheKey + " and expiration: "+ expires);
              this.logDebug("XMLConversation: caching contents with key: " + cacheKey + " and expiration: "+ expires);
            }

            if (expires > 0){
              mTheCache.put(cacheKey, cacheContents,System.currentTimeMillis() + expires);
            }else if (expires == CACHE_FOREVER){   //we cache indefinitely
              mTheCache.put(cacheKey, cacheContents,CACHE_FOREVER);
            }
         }
      }else{  // cacheKey == null, i.e. we are not caching

        
          theConnection = u.openConnection(); 
          theXMLResponse = mXMLParser.parse(theConnection.getInputStream() ,false,null);   //theStream,false,null);
          totalTime = System.currentTimeMillis () - startTime;

          //System.out.println("XMLConversation: Caching disabled: Total elapsed time retrieve and parse contents from the service provider without caching: " + totalTime);

          if (this.isLoggingDebug()){
            totalTime = System.currentTimeMillis () - startTime;
            this.logDebug("XMLConversation: Total elapsed time retrieve and parse contents from the service provider without caching: " + totalTime);
            //System.out.println("XMLConversation: Total elapsed time retrieve and parse contents from the service provider without caching: " + totalTime);
          }
      }
    }catch (Exception e){   //we have to catch a generic exception since that is what XMLTools throws...
      throw new ConversationException (this.getClass().getName()+ ": Error connecting to external URL: \n", e);
    }finally{
      if (theStream !=null){
        try{
          theStream.close();
        }catch (IOException e){
        }
      }
    }
    return theXMLResponse;
  }

  /**
   * Reads an input stream and returns it as a byte array.
   * We use this for properly reading the HTTP response stream.
   *
   * @returns byte[] representing the contents of the response stream
   */

  protected byte[] readStream(InputStream pInputStream) throws IOException{
    int size=10000;  //starting size of the buffer
    int offset=0;      // the offset position for reading
    int byteRead;
    byte [] byteArray = new byte [size];
    while (true){
      byteRead = pInputStream.read (byteArray, offset, size-offset);
      if (byteRead==-1){ // End of stream
          break;
      }
      offset+=byteRead;
      if (offset==size){ // resize if we ran out of buffer space
          size *= 2;
          byte [] tmp = new byte [size];
          System.arraycopy (byteArray, 0, tmp, 0, offset);
          byteArray = tmp;
      }
    }
    // the buffer is larger than we need so resize it
    if (offset != size){
        byte [] tmp = new byte [offset];
        System.arraycopy (byteArray, 0, tmp, 0, offset);
        byteArray = tmp;
    }
    return byteArray;
  }

  /**
   * This method creates a paramter list for a URL query string - i.e. it returns
   * a set of name-value pairs that can be appended to the query
   * string of a URL.  Values are assumed to be encoded already.
   *
   * @returns String The parameter list
   *
   */

  protected String makeUrlParams(ArrayList pParamList){
      StringBuffer theUrlList = new StringBuffer();
      int pSize = pParamList.size();
      for (int i=0; i<pSize; i++){
       theUrlList.append("&"+pParamList.get(i));
      }
      return theUrlList.toString();
  }

  /**
   * This method should be overriden by subclasses in order to determine
   * whether to cache a particular response or not.  For example, we do
   * not want to cache error messages.
   *
   * @returns long indicating whether the contents should be cached and, if so,
   * the duration accoring to the following scheme:
   *
   * -1 - Contents should not be cached
   *  0 - Contents should be cached permanently
   * >0 - length of time in milliseconds that the content should be cached from
   *      current time
   */
  protected long shouldCache(Document theResponse){
    return NO_CACHE;
  }

}
