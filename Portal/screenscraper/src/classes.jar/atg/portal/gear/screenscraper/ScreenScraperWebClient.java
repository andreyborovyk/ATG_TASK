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
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

package atg.portal.gear.screenscraper;

import atg.portal.gear.screenscraper.HTMLFilterParser;
import atg.portal.gear.screenscraper.ScreenScraperHTTPConnection;
import atg.portal.gear.screenscraper.Configuration;
import atg.service.bytecache.ByteCache;
import atg.service.bytecache.ByteCacheEntry;
import java.util.ResourceBundle;
import java.io.OutputStream;
import java.util.ArrayList;
import java.net.URL;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.Writer;
import java.io.InputStreamReader;
import atg.service.bytecache.LRUByteCacheEntry;
import atg.nucleus.GenericService;
import atg.servlet.ServletUtil;
import atg.core.net.URLUtils;


/**
 * @beaninfo
 * description:Retreives, parses and caches web content from the given http URL
 * Does not support authentication.
 * @author Ashish Dwivedi
 * @version $Id: //app/portal/version/10.0.3/screenscraper/src/atg/portal/gear/screenscraper/ScreenScraperWebClient.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public class ScreenScraperWebClient
  extends  GenericService 
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //app/portal/version/10.0.3/screenscraper/src/atg/portal/gear/screenscraper/ScreenScraperWebClient.java#2 $$Change: 651448 $";
  
  

  //-------------------------------------
  // Constants
  //-------------------------------------
//  static final String MY_RESOURCE_NAME = "atg.portal.gear.screenscraper.WebClientResources";
//  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME);
//  public static final String ERROR_WRITING_TO_OUTPUT_STREAM= "errorWritingToOutputStream";
   
  //Key names in the given userBundle not the static bundle of this class
  public static final String INVALID_URL_SPECIFIED  = "invalidUrlSpecified";
  public static final String FILE_NOT_FOUND  = "fileNotFound";
  public static final String UNKNOWN_HOST  = "unknownHost";
  public static final String CONNECTION_REFUSED  = "connectionRefused";
  public static final String CONTENT_NOT_HTML_OR_TEXT= "contentNotHtmlOrText";
  
  //ArrayList to hold host which could not be resolved the first time
  protected static ArrayList sHostNotFound = new ArrayList(10);

  //-------------------------------------
  // Constructors
  //-------------------------------------

  public ScreenScraperWebClient()
  {
  }

  //-------------------------------------
  // properties
  //-------------------------------------
  /**
   * property htmlFilterParser
   */
  HTMLFilterParser mHtmlFilterParser;
  public HTMLFilterParser getHtmlFilterParser () {
    return mHtmlFilterParser;
  }
  public void setHtmlFilterParser (HTMLFilterParser pHtmlFilterParser) {
    if (mHtmlFilterParser != pHtmlFilterParser) {
      mHtmlFilterParser = pHtmlFilterParser;
    }
  }

  // property configuration
  Configuration mConfiguration;
  public Configuration getConfiguration () {
    return mConfiguration;
  }
  public void setConfiguration (Configuration pConfiguration) {
    if (mConfiguration != pConfiguration) {
      mConfiguration = pConfiguration;
    }
  } 

  String mErrorMessage;
  private String getErrorMessage () {
    return mErrorMessage;
  }
  private void setErrorMessage (String pErrorMessage) {
      mErrorMessage = pErrorMessage;
  }
  
  // property parsedPageCache 
  ByteCache mParsedPageCache;
  public ByteCache getParsedPageCache () {
    return mParsedPageCache;
  }
  public void setParsedPageCache (ByteCache pParsedPageCache) {
    if (mParsedPageCache != pParsedPageCache) {
      mParsedPageCache = pParsedPageCache;
    }
  }

  // property useProxyServer
  boolean mUseProxyServer=false;
  public boolean getUseProxyServer () {
    return mUseProxyServer;
  }
  public void setUseProxyServer (boolean pUseProxyServer) {
    if (mUseProxyServer != pUseProxyServer) {
      mUseProxyServer = pUseProxyServer;
    }
  }

  // property proxyPort
  int mProxyPort=-1;
  public int getProxyPort () {
    return mProxyPort;
  }
  public void setProxyPort (int pProxyPort) {
    if (mProxyPort != pProxyPort) {
      mProxyPort = pProxyPort;
    }
  }

  // property proxyHost
  String mProxyHost;
  public String getProxyHost () {
    return mProxyHost;
  }
  public void setProxyHost (String pProxyHost) {
    if (mProxyHost != pProxyHost) {
      mProxyHost = pProxyHost;
    }
  }
  
  // property readTimeOut
  int mReadTimeOut = 0;
  public int getReadTimeOut () {
    return mReadTimeOut;
  }
  public void setReadTimeOut (int pReadTimeOut) {
      mReadTimeOut = pReadTimeOut;
  }

  // property escapeURL
  boolean mUnEscapeURL=true;
  public boolean getUnEscapeURL () {
    return mUnEscapeURL;
  }
  public void setUnEscapeURL (boolean pUnEscapeURL) {
      mUnEscapeURL = pUnEscapeURL;
  }
  
  /**
  * Retrieves a web page from the given url, parses the content using the HTMLFilterParser
  * and writes the contents to the given outputstream, caching if Configuration.disableCache
  * is false.
  */
  public String retrieveWebPage(String pUrlStr, ResourceBundle pUserBundle, Writer pOut) 
    throws IllegalArgumentException, IOException {
    if(pUrlStr == null) throw new IllegalArgumentException("URL string must be specified before calling this method.");
    if(pOut == null) throw new IllegalArgumentException("OutputStream supplied cannot be null.");
    if(pUserBundle == null) throw new IllegalArgumentException("ResourceBundle supplied cannot be null.");
    URL url  = null;
    setErrorMessage(null);
    try{   
      if(getUnEscapeURL()){
        url = new URL(escapeSpaces(URLUtils.unescapeUrlString(pUrlStr)));
        if(isLoggingDebug())
          logDebug("UnEscaping URL "+pUrlStr+" to "+url.toString());
      }
      else                    
        url = new URL(escapeSpaces(pUrlStr));
      //we do not want an expensive DNS lookup every time so we cache hosts not found.
      if(sHostNotFound.contains(url.getHost()))
       throw new UnknownHostException(url.getHost());
       
      String key = url.toString();
      //check cache for content
      if(!getConfiguration().getDisableCaching()){
        ByteCacheEntry entry = (ByteCacheEntry) getParsedPageCache().getEntry(key); 
        if(entry!=null){
          if(isLoggingDebug()) 
            logDebug("Cache hit for key: "+url.toString());
	  ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
	  baOutStream.write(entry.getData());
          pOut.write( baOutStream.toString() );
	  pOut.flush();
          return getErrorMessage();      
        }
        if(isLoggingDebug())
            logDebug("Cache missed for key: "+url.toString());
      }
      else 
        if(isLoggingDebug())
            logDebug("Caching disabled.");
      
      ScreenScraperHTTPConnection connection = new ScreenScraperHTTPConnection(url);
      if(getUseProxyServer()){
       if(isLoggingError()){
         if(getProxyHost()==null)
           logError("Invalid proxy server configuration, proxyHost required. Ignoring proxy configuration.");
         if(getProxyPort()==-1)  
           logError("Invalid proxy server configuration, proxyPort required. Ignoring proxy configuration.");
       }
      }     
      if(getUseProxyServer() && getProxyHost()!=null && getProxyPort()!=-1){
        if(isLoggingDebug()){
          logDebug("Using proxy server : "+getProxyHost()+":"+getProxyPort());  
        }
        connection.setProxyServer(getProxyHost(), getProxyPort());  
      }
      connection.setRequestProperty("Connection", "Close");
      connection.setTimeOut(getReadTimeOut());
      InputStream inputStream = connection.getDataInputStream();
       
      if(!connection.getContentType().startsWith("text/html") && 
         !connection.getContentType().startsWith("text/plain"))
      {
         setErrorMessage(MessageFormat.format
          (pUserBundle.getString(CONTENT_NOT_HTML_OR_TEXT),
          new Object [] { url,connection.getContentType() }));
         connection.close();
         return getErrorMessage();
       }
      if(isLoggingDebug()) {
        logDebug("Connection status code : "+connection.getStatusCode());
        logDebug("Connection status message : "+connection.getStatusMessage());
      }
      String followedURLStr = connection.getURL().toString();
      HTMLFilterParser filterParser = getHtmlFilterParser();
      filterParser.setUrl(followedURLStr);
      if(!getConfiguration().getDisableCaching()){
        ByteArrayOutputStream cacheOut = new ByteArrayOutputStream();
        filterParser.parse(inputStream, cacheOut);
         byte [] data = cacheOut.toByteArray ();
        LRUByteCacheEntry entry = new LRUByteCacheEntry(key, data);
        if(isLoggingDebug())
            logDebug("Caching "+data.length +" bytes of data for key :"+key);
        getParsedPageCache().putEntry(key, entry);
	cacheOut.reset();
	cacheOut.write(data);
        pOut.write(cacheOut.toString());
	pOut.flush();
        cacheOut.close();
      }
      else{
        // no caching write directly to the given output stream
        filterParser.parse(new InputStreamReader(inputStream), pOut);   
        if(isLoggingDebug())
              logDebug("Caching disabled.");
     } 
      connection.close(); 
    }
    catch(MalformedURLException mex){
      setErrorMessage(MessageFormat.format
      (pUserBundle.getString(INVALID_URL_SPECIFIED),
      new Object [] {url}));
      return getErrorMessage();
    }
    catch(ConnectException cex){
      setErrorMessage(MessageFormat.format
      (pUserBundle.getString(CONNECTION_REFUSED),
      new Object [] { url}));
      return getErrorMessage();  
    } 
    catch(UnknownHostException hex){
    //cache the host not found to save expensive DNS lookups later
      if(!sHostNotFound.contains(hex.getMessage()))
         sHostNotFound.add(hex.getMessage());
      setErrorMessage(MessageFormat.format
      (pUserBundle.getString(UNKNOWN_HOST),
      new Object [] {hex.getMessage()}));
     return getErrorMessage();  
    }
    
    return getErrorMessage();
  }
 
 /**
  * escapes only the white spaces in the given string with %20
  * required to make a successfull http connection
  */
 
  private String escapeSpaces(String pUrlStr){
    if(pUrlStr==null) return null;
    StringBuffer sb = new StringBuffer();
    char [] str = pUrlStr.toCharArray();
    for (int i=0; i<str.length; i++) {
      char c = str[i];
      if (c == ' ')
        sb.append("%20");
      else
        sb.append((char) c);
     }  
     return sb.toString();
  }
  
 
}


