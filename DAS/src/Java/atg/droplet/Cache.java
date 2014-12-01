/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
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

package atg.droplet;

import javax.transaction.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.JMSException;

import atg.dms.patchbay.MessageSink;
import atg.dtm.*;
import atg.nucleus.*;
import atg.nucleus.logging.*;

import atg.servlet.*;
import atg.servlet.pipeline.CachingServletOutputStream;
import atg.servlet.pipeline.CachingPrintWriter;
import atg.servlet.sessiontracking.SessionData;
import atg.servlet.pagecompile.PageEncodingTyper;

import java.io.*;
import java.util.*;
import java.text.*;

import atg.core.net.URLUtils;
import atg.core.util.ResourceUtils;

import javax.servlet.jsp.*;
import javax.servlet.jsp.tagext.BodyContent;
import atg.servlet.jsp.*;

import atg.servlet.pagefilter.HtmlFilterParser;
import java.net.URLDecoder;

/**
 *
 * This droplet caches the contents of its oparam named output.  It improves
 * performance for pages that generate dynamic content which is the same
 * for all users, or where there are a small number of versions of the content
 * (for example, one version for members and another for guests).
 * <p>
 * If your request is rewriting URLs to include the session id, the
 * session id and request id will be removed from the cached content. This
 * prevents session ids from being returned to incorrect users.
 * If you know that the contents of your oparam do not define any URLs and
 * you want to improve the performance of this case by avoiding the search
 * for session ids, you can specify the parameter <i>hasNoURLs</i> to "true".
 * <p>
 * Your cached content will periodically be regenerated the first request
 * after a specified time interval has elapsed since the content was cached.
 * You can specify what this interval should be with the request parameter
 * <i>cacheCheckSeconds</i>.  If no cacheCheckSeconds parameter is supplied,
 * the content is expired using the bean property defaultCacheCheckSeconds on
 * the cache droplet itself.
 * <p>
 * It is possible to have more than one view of the cached data associated
 * with each cache droplet invocation.  For example, if your page had different
 * views for members and non-members, you can still use the cache
 * droplet.  To do this, set the <i>key</i> request parameter to contain the
 * a value that uniquely identifies the content of the page.  In the above
 * example, we'd set the key parameter to "true" for members and "false"
 * for non-members.  The cache droplet maintains separate cached content
 * for each value of the key parameter.  You do not need to supply system
 * wide unique key values.  Each cache droplet gets its own name space of
 * key values.
 * <p>
 * All content managed by a single cache droplet instance is periodically
 * purged based on the property purgeCacheSeconds
 * (the default is every 6 hours).  This is to retrieve data storage for
 * instances of the cache droplet that are no longer in use.  This would happen
 * if you remove or recompile a jhtml page that has a cache droplet.
 * <p>
 * If you visit the admin page for this component in the component browser,
 * you can see statistics about the content which is cached.
 * <p>
 * The Cache component itself is a JMS message sink.  When it receives a
 * message, it flushes any content that it may define.  You can use multiple
 * instance of the Cache servlet bean if you would like to control which
 * content gets flushed for a JMS message.
 *
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/Cache.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: Droplet which caches the output of an OPARAM
 *   attribute: functionalComponentCategory Servlet Beans
 *   attribute: featureComponentCategory
 *   attribute: icon /atg/ui/common/images/dropletcomp.gif
 */
public class Cache
  extends DynamoServlet
  implements  MessageSink
{
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Cache.java#2 $$Change: 651448 $";

  static final String MY_RESOURCE_NAME = "atg.droplet.DropletResources";

  private static java.util.ResourceBundle sResourceBundle = java.util.ResourceBundle.getBundle(MY_RESOURCE_NAME);

  public static final String DO_NOT_CACHE_RESPONSE = "DoNotCacheResponse";
  public static final String PROTOCOL_CHANGE_DROPLET = "ProtocolChangeDroplet";


  //-----------------------------
  // Properties

  volatile Dictionary mCache = new Hashtable();

  public Cache() {
    mLastPurgedTime = System.currentTimeMillis();
  }


  static String sJsessionid = null;


  //---------------------------
  // property: sessionURLName

  String mSessionURLName;

  /** The name for the session id. Usually "jsessionid". */
  public void setSessionURLName(String pSessionURLName) {
    mSessionURLName = pSessionURLName;
  }

  /** The name for the session id. Usually "jsessionid". */
  public String getSessionURLName() {
    return mSessionURLName;
  }



  /**
   * Determine whether we have a valid cached entry.  If so, render from
   * the cache, otherwise, render the output parameter, caching the results
   * for rendering the next time.
   */
  public void service(DynamoHttpServletRequest pRequest,
                      DynamoHttpServletResponse pResponse)
       throws ServletException, IOException
  {
    if (!mEnabled) {
      if (!pRequest.serviceLocalParameter("output", pRequest, pResponse)) {
	if (isLoggingError())
	  logError(ResourceUtils.getMsgResource("cacheNoOutput", MY_RESOURCE_NAME, sResourceBundle));
	return;
      }
      return;
    }

    // Reset the ProtocolChangeDroplet attribute. This may be set later
    // in the ProctocolChange droplet if so, we avoid caching - Bug# 66179
    // See also:
    //     /atg/droplet/ProtocolChange.java
    //     /atg/taglib/dspjsp/Droplet.java
    pRequest.setAttribute(PROTOCOL_CHANGE_DROPLET, null);
    if (isLoggingDebug())
      logDebug("**** unsetting the protocolChangeDroplet attribute ****");

    long expireTime = mDefaultCacheCheckSeconds * 1000;

    Object key = pRequest.getLocalParameter("key");
    Object outputKey = pRequest.getLocalParameter("output");
    if (outputKey == null) {
      if (isLoggingError())
        logError(ResourceUtils.getMsgResource("cacheNoOutput", MY_RESOURCE_NAME, sResourceBundle));
      return;
    }

    if (key == null) {
      key = outputKey;
    }
    else {
      String keyStr = key.toString();
      keyStr += "---" + outputKey.hashCode();
      key = keyStr;
    }

    String expireTimeStr = pRequest.getParameter("cacheCheckSeconds");
    if (expireTimeStr != null) {
      try {
        expireTime = Long.parseLong(expireTimeStr) * 1000;
      }
      catch (NumberFormatException e) {
        if (isLoggingError())
          logError("bad value for cacheCheckSeconds parameter");
      }
    }

    CacheDataEntry cdata = (CacheDataEntry) mCache.get(key);

    // cdata could have either char[] data or byte[] data
    if (cdata != null && (cdata.charData != null || cdata.data != null) &&
        !shouldRegenerateCachedData(pRequest, cdata, expireTime))
    {
      // Now write the data. If cookies disabled pass through
      // HTMLFilterParser before writing out else do as default and write
      // straight out.

      if (isLoggingDebug())
        logDebug("Retrieved item from cache");

      // cdata could have either char[] data or byte[] data
      String outputData = null;
      if (cdata.data != null)
        outputData = new String(cdata.data);
      else
        outputData = new String(cdata.charData);

      Object hasNoURLs;
      if (ServletUtil.isUsingURLRewriting(pRequest) &&
          (hasNoURLs = pRequest.getLocalParameter("hasNoURLs")) != null &&
          !("true".equalsIgnoreCase(hasNoURLs.toString())))
      {
        if (isLoggingDebug()) {
          logDebug("Rewriting Cache entry");
        }
        HtmlFilterParser parser = new HtmlFilterParser(
                                        pResponse.getOutputStream(),
                                        pRequest, true);
        parser.parse(outputData);
      }
      else {
        if (isLoggingDebug()) {
          logDebug("Returning from the Cache entry no rewriting necessary");
        }
        pResponse.getOutputStream().print(outputData);
      }
    }
    else {
      // Otherwise we pass the request but cache the bytes & headers
      // hang on to the original output stream
      ServletOutputStream originalOut = pResponse.getOutputStream();
      String encoding = pResponse.getCharacterEncoding();
      PrintWriter originalWriter = null;
      /*
       * If we have a non-default encoding, we need to backup the Writer
       * as well
       */
      if (mEncodingTyper != null &&
          (!mEncodingTyper.isNullEncoding(encoding) ||
           mEncodingTyper.getEncodingType(ServletUtil.getCurrentPathInfo(pRequest)) != null))
        originalWriter = pResponse.getWriter();

      PageContext pageContext = (PageContext)pRequest.getAttribute(
	DynamoJspPageContext.REQUEST_PAGE_CONTEXT_ATTR);

      // Set up a stream to capture the output
      CachingServletOutputStream cachingOut;
      PrintWriter cachingWriter = null;


      // Set up a stream to capture the output
      if (pageContext != null) {
	// if we have a page context, then we base our caching output
	// stream that doesn't echo, since we'll write the
	// content to the underlying stream using the JSP-ish way.
	cachingOut = new CachingServletOutputStream(null, false);
      }
      else {
	cachingOut = new CachingServletOutputStream(originalOut);
      }

      if (originalWriter != null) {
	cachingWriter = new PrintWriter(
	  new OutputStreamWriter(cachingOut, pResponse.getCharacterEncoding()));
      }

      if (pageContext != null) {
	// if we have a page context, then we're doing the JspWriter
	// thang... so we should use pushBody/popBody as though
	// we were a JSP tag.
	BodyContent bc = pageContext.pushBody();
	boolean bSuccess = false;

	try {
	  if (!pRequest.serviceLocalParameter("output", pRequest, pResponse)) {
	    if (isLoggingError())
	      logError(ResourceUtils.getMsgResource("cacheNoOutput", MY_RESOURCE_NAME, sResourceBundle));
	    return;
	  }

	  // so write out to the enclosing writer, which sends
	  // the normal output right along.
	  bc.writeOut(bc.getEnclosingWriter());

	  if (cachingWriter != null) {
	    // if we have a writer, we can use the normal, standard
	    // "writeOut" method.
	    bc.writeOut(cachingWriter);
	    cachingWriter.flush();
	  }
	  else {
	    // otherwise, we don't have a writer, so what do we do?
	    // (we can getString().getBytes(), I suppose)

	    // since we don't have an encoding, we should be using
	    // a ByteBufferedBodyContent, but we'll support
	    // not having one, as well.
	    if (bc instanceof ByteBufferedBodyContent) {
	      // this is more efficient than generating a big
	      // String and then tossing it.
	      ((ByteBufferedBodyContent)bc).writeOut(cachingOut);
	    }
	    else {
	      // but if we have to generating a string, we'll do
	      // that so we can be portable.
	      cachingOut.print(bc.getString());
	    }
	  }

	  bc.clearBuffer();
	  bSuccess = true;
	}
	finally {
	  if (!bSuccess) {
	    try {
	      bc.writeOut(cachingWriter);
	    }
	    catch (IOException e) {
	      // just swallow the exception, since we should
	      // be in the midst of throwing one here, and
	      // we don't want to mask the original exception
	    }
	  }
	  pageContext.popBody();
	}
      }
      else {
	// otherwise, do the non-JSP thing with out substituted stream.

	try {
        pResponse.setOutputStream(cachingOut);
        if (originalWriter != null){
          cachingWriter = new CachingPrintWriter(originalWriter);
          pResponse.setWriter(cachingWriter);
        }
        if (!pRequest.serviceLocalParameter("output", pRequest, pResponse)) {
          if (isLoggingError())
            logError(ResourceUtils.getMsgResource("cacheNoOutput", MY_RESOURCE_NAME, sResourceBundle));
          return;
        }
      }
	// restore the output stream
	finally {
	  if (originalWriter != null) {
	    pResponse.setWriter(originalWriter);
	  }
	  pResponse.setOutputStream(originalOut);
	}
      }

      // don't cache if there was a redirect!
      if (pResponse.containsHeader("Location")) {
        /* In case there was an entry here */
        mCache.remove(key);
	return;
      }

      // Get the data & cache it, but only if nobody explicity has told us
      // to avoid caching this object.  This might occur in special
      // cases where a database operation failed for example to generate
      // the right content.

      // The PROTOCOL_CHANGE_DROPLET attribute is unset in the beginning of
      // the service routine (see above) and maybe set later in the ProtocolChange
      // droplet to avoid caching - Bug# 66179
      // See also:
      //     /atg/droplet/ProtocolChange.java
      //     /atg/taglib/dspjsp/Droplet.java
      if (pRequest.getAttribute(PROTOCOL_CHANGE_DROPLET) != null) {
        if (isLoggingDebug())
          logDebug("Found ProtocolChangeDroplet attribute in response - not caching");
      }
      else if (pRequest.getAttribute(DO_NOT_CACHE_RESPONSE) != null) {
        if (isLoggingDebug())
          logDebug("Found DoNotCacheResponse attribute in response - not caching");
      }
      else {
        if (isLoggingDebug()) {
          logDebug("Adding item to cache ");
        }

        String cacheData = null;
        if(pageContext == null && originalWriter != null
            && cachingWriter instanceof CachingPrintWriter){
          cacheData = ((CachingPrintWriter)cachingWriter).getContainedArrayWriter().toString();
        }
        else{
          byte[] byteArrayOutput = cachingOut.toByteArray();
          cacheData = new String(byteArrayOutput, encoding);
        }
        cacheData = ensureNoIds(pRequest, cacheData);
        cdata = new CacheDataEntry(cacheData.toCharArray());
        mCache.put(key, cdata);
      }
      cachingOut=null;
      cachingWriter=null;
    }
  }


  public String ensureNoIds(DynamoHttpServletRequest pRequest,
                             String cacheData)
  {
    if (ServletUtil.isUsingURLRewriting(pRequest)){
      if (isLoggingDebug()) {
        logDebug("Will remove session and request ids before storing in cache");
      }
      int sessionIdLength = pRequest.getSession().getId().length();

      //  Hack to get around different session id length when using
      //  Weblogic's  response.encodeURL and response.getSession().getId().
      //  The encodeURL will have a shorter session id than what getId()
      //  returns. Example:
      //
      //   encodeURL:
      //     AtQohCLYincFZzKQxhswuvKJ74dFAONVqxkakysRCMhhGMdrimeK!-1416001560
      //   request.getSession().getId():
      //     AtQohCLYincFZzKQxhswuvKJ74dFAONVqxkakysRCMhhGMdrimeK!-1416001560!1089294376099
      //
      //  if we don't do this, when removing the ids, we'll accidentally
      //  remove more than we need leading to invalid HTML in the cache.

      if (ServletUtil.isWebLogic()){
        String encodedLink = pRequest.getResponse().encodeURL("");
        int index = encodedLink.indexOf(sJsessionid);
        if (index != -1) {
          String sid = encodedLink.substring(index + sJsessionid.length());
          if (isLoggingDebug()) {
            logDebug("WebLogic detected. encoded sessionid=" + sid);
          }
          sessionIdLength = sid.length();
        }
      }

      return removeIds(cacheData, sessionIdLength);
    }
    return cacheData;
  }

  public void removeCachedElement(String key){
    mCache.remove(key);
  }

  int mDefaultCacheCheckSeconds = 60;
  //------------------------------
  /**
   * This property specifies the default time interval for invalidating
   * cached content if the droplet does not receive the request parameter
   * cacheCheckSeconds.
   *
   * @beaninfo
   *   description: Default time interval for invalidating cached content, in seconds
   */
  public void setDefaultCacheCheckSeconds(int pCheckCacheSeconds) {
    mDefaultCacheCheckSeconds = pCheckCacheSeconds;
  }

  private static String removeIds(String s, int sessionIdLength) {
    String jsessionid = sJsessionid;
    int index;
    while ((index = s.indexOf(jsessionid)) != -1) {
      s = s.substring(0, index) + s.substring(index + jsessionid.length() +
                                              sessionIdLength);
    }

    String requestId = "&" + DynamoHttpServletResponse.REQUEST_ID + "=";
    while ((index = s.indexOf(requestId)) != -1) {
      int i = index + requestId.length();
      while (i < s.length()) {
        char c = s.charAt(i);
        if (c < '0' || '9' < c)
          break;
        i++;
      }
      s = s.substring(0, index) + s.substring(i);
    }
    return s;
  }

  //------------------------------
  public int getDefaultCacheCheckSeconds() {
    return mDefaultCacheCheckSeconds;
  }

  /* Default value is 6 hours */
  long mPurgeCacheMilliseconds = 6*60*60*1000;

  //------------------------------
  /**
   * This property specifies the default time interval for purging the
   * entire collection of cached content.  This is necessary to free up
   * content associated with pages that are no longer in use by the system
   * (i.e. jhtml pages that have been recompiled).
   *
   * @beaninfo
   *   description: Time interval for purging the entire collection of cached content
   */
  public void setPurgeCacheSeconds(long pPurgeCacheSeconds) {
    mPurgeCacheMilliseconds = pPurgeCacheSeconds * 1000;
  }

  //------------------------------
  public long getPurgeCacheSeconds() {
    return mPurgeCacheMilliseconds/1000;
  }

  //--------- Property: Enabled -----------
  boolean mEnabled = true;
  /**
   * Sets the property Enabled.  If the servlet is not enabled, the output
   * parameter is sent to the browser without any caching.
   */
  public void setEnabled(boolean pEnabled) {
    mEnabled = pEnabled;
  }
  /**
   * @return The value of the property Enabled.
   */
  public boolean getEnabled() {
    return mEnabled;
  }


  //------------------------------
  /** Time interval for the last time we invalidated the cache */
  long mLastPurgedTime;

  /**
   * Returns true if we should check to see whether the cached output
   * parameter should be checked again.
   * <ul>
   * <li>We always check if CacheCheckMilliseconds is 0
   * <li>We check the time if the time interval has elapsed since we last
   * checked.
   * </ul>
   */
  boolean shouldRegenerateCachedData(DynamoHttpServletRequest pRequest,
  				     CacheDataEntry p,
  				     long pCheckTime) {
    long now = System.currentTimeMillis();

    /*
     * First see if we should purge the entire cache
     */
    if (now - mLastPurgedTime > mPurgeCacheMilliseconds) {
      mCache = new Hashtable();
      mLastPurgedTime = now;
      if (isLoggingDebug())
        logDebug("purged the entire cache for: " + ServletUtil.getCurrentPathInfo(pRequest));
      return true;
    }

    if (now - p.creationTime > pCheckTime) {
      if (isLoggingDebug())
        logDebug("invalidating the cache for: " + ServletUtil.getCurrentPathInfo(pRequest));
      return true;
    }
    return false;
  }

  /**
   * Forces cache to flush the data.
   */
  public void flushCache() {
    mCache = new Hashtable();
    if (isLoggingDebug())
        logDebug("flush the entire cache");
    return;
  }

  //-------------------------------------
  // MessageSink implementation
  //-------------------------------------

  //-------------------------------------
  /**
   * Called by DMS when a Message arrives through the given input
   * port.  There may be concurrent calls of this method from multiple
   * Threads.
   *
   * @exception JMSException if there is a problem processing the
   * message
   **/
  public synchronized void receiveMessage(String pPortName,
					  Message pMessage)
    throws JMSException
  {
    // ignore null messages
    if (pMessage == null)
      return;

    // only know how to deal with standard Dynamo messages
    if (!(pMessage instanceof ObjectMessage)) {
      Object[] args = { pMessage.getJMSType(), pPortName };
      throw new JMSException
	(ResourceUtils.getMsgResource
	 ("cacheInvalidMessage",
	  MY_RESOURCE_NAME, sResourceBundle, args));
    }
    // for now we don't use any properties of the message itself
    //unused ObjectMessage message = (ObjectMessage) pMessage;

    // just flush the entire cache
    flushCache();
  }

  //--------- Property: EncodingTyper -----------
  PageEncodingTyper mEncodingTyper;
  /**
   * Sets the property EncodingTyper.
   */
  public void setEncodingTyper(PageEncodingTyper pEncodingTyper) {
    mEncodingTyper = pEncodingTyper;
  }
  /**
   * @return The value of the property EncodingTyper.
   */
  public PageEncodingTyper getEncodingTyper() {
    return mEncodingTyper;
  }

  /**
   * Creates and returns a new Servlet that will administer this service.
   **/
  protected Servlet createAdminServlet () {
    return new CacheAdminServlet(this, this, getNucleus(), null);
  }

  /**
   *  Class that will administer this service
   **/
  class CacheAdminServlet extends atg.nucleus.ServiceAdminServlet
  {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Cache.java#2 $$Change: 651448 $";

    /**
     * Constructs an instanceof CacheAdminServlet
     */
    public CacheAdminServlet (Object pService,
                              ApplicationLogging pLogger, Nucleus pNucleus) {
      this(pService, pLogger, pNucleus, null);
    }

    /**
     * Constructs an instanceof CacheAdminServlet
     */
    public CacheAdminServlet (Object pService,
                              ApplicationLogging pLogger, Nucleus pNucleus,
                              TransactionManager pTransactionManager) {
      super(pService, pNucleus);
    }
    /**
     * Performs all  admin functionality, then adds on the cache statistics
     */
    protected void normalAdmin(HttpServletRequest pRequest,
                               HttpServletResponse pResponse,
                               ServletOutputStream pOut)
      throws ServletException, IOException
    {
      pOut.println("<form action=\"" + pRequest.getRequestURI() +
                   "\" method=\"post\">");
      pOut.println("<input type=\"hidden\" name=\"removeItemsFromCache\" value=\"true\">");
      pOut.println ("<h2>Cache usage statistics</h2>");
      pOut.println ("<table border>");
      String [] globalKeys = {
        "entry",
        "creationTime",
        "cacheSize",
        "cacheData",
        "removeItemsFromCache"
      };

      // print header
      pOut.println ("<tr>");
      for (int i = 0; i < globalKeys.length; i++) {
          pOut.println ("<th>" + globalKeys [i] + "</th>");
      }
      pOut.println ("</tr>");

      // print cache entries
      int nEntries = 0;
      int byteSize = 0;
      int byteEntries = 0;
      int charSize = 0;
      int charEntries = 0;
      final int maxViewableEntries = 20000;

      for (Enumeration e = mCache.keys(); e.hasMoreElements(); nEntries++) {
        if (nEntries > maxViewableEntries) {
          // too many entries, print ... and finish
          pOut.print("<tr><th colspan=3>   ..............");
          pOut.println("</th></tr>");
          break;
        }
        Object cacheDataKey = e.nextElement();
        CacheDataEntry cacheEntry = (CacheDataEntry) mCache.get(cacheDataKey);
        Date creationTime = new Date();
        creationTime.setTime(cacheEntry.creationTime);

	int len = 0;
        if (cacheEntry.data != null) {
          len = cacheEntry.data.length;
          byteSize += len;
          byteEntries++;
        }
        if (cacheEntry.charData != null) {
          len = cacheEntry.charData.length;
          charSize += len;
          charEntries++;
        }
        String [] itemCacheValues = {
          "" + nEntries,
          creationTime.toString(),
          "" + len,
          "<a href=\"?cacheDataKey=" +
            ServletUtil.escapeURLString(cacheDataKey.toString()) + "\">" +
            cacheDataKey + "</a>",
          "<input type=\"checkbox\" name=\"remove\" value=\"" +
            ServletUtil.escapeURLString(cacheDataKey.toString()) + "\">"
        };

        pOut.println ("<tr>");
        for (int i = 0; i < itemCacheValues.length; i++) {
          pOut.println ("<th>" + itemCacheValues [i] + "</th>");
        }
        pOut.println ("</tr>");
      }
      pOut.println ("</table>");
      pOut.println("<input type=\"submit\" value=\"Remove items from Cache\" name\"submit\">");
      pOut.println("</form>");
      if (nEntries <= maxViewableEntries) {
        StringBuffer sb = new StringBuffer("<p>Cache contains ");
        sb.append(byteEntries).append(" entries, containing ");
        sb.append(byteSize).append(" bytes and ").append(charEntries);
        sb.append(" containing ").append(charSize).append(" characters.");
        pOut.println(sb.toString());
      }
    }


    protected void printAdmin(HttpServletRequest pRequest,
                              HttpServletResponse pResponse,
                              ServletOutputStream pOut)
      throws ServletException, IOException
    {
      String cacheDataKey = pRequest.getParameter("cacheDataKey");
      String removeItemsFromCache = pRequest.getParameter("removeItemsFromCache");
      // then you are going to the normal admin page use to work
      if (cacheDataKey == null && removeItemsFromCache == null) {
        // normalAdmin(pRequest, pResponse,  pOut);
        normalAdmin(pRequest, pResponse, pOut);
      }
      if (cacheDataKey != null) {
        // you want to see the data for one cache name
        viewCacheItemAdmin(pRequest, pResponse, pOut);
      }
      if (removeItemsFromCache != null) {
        // you want to remove one or more items from the cache
        removeItemsFromCacheAdmin(pRequest, pResponse, pOut);
      }
    }


    private void viewCacheItemAdmin(HttpServletRequest pRequest,
                                    HttpServletResponse pResponse,
                                    ServletOutputStream pOut)
      throws ServletException, IOException
    {
      String cacheDataKey = pRequest.getParameter("cacheDataKey");
      if (cacheDataKey == null) {
        logWarning("Error: cacheDataKey was null");
        return;
      }

      pOut.println("<h2>Cache item data is below</h2>");
      pOut.println("<table border>");

      pOut.println("<tr><td><h3>Data</h3></td></tr>");
      pOut.println("<tr><td>");
      CacheDataEntry cacheEntry = null;
      Enumeration e = mCache.keys();
      while (e.hasMoreElements()) {
        Object elem = e.nextElement();
        if (elem.toString().equals(cacheDataKey)) {
          cacheEntry = (CacheDataEntry)mCache.get(elem);
          break;
        }
      }
      String cacheData = null;
      if (cacheEntry != null){
        cacheData = ServletUtil.escapeHtmlString(cacheEntry.toString());
      }
      else {
        cacheData = "couldn't find data for key =" + cacheDataKey;
      }

      pOut.println(cacheData);
      pOut.println("</td></tr>");

      pOut.println("<tr><td><h3>Html</h3></td></tr>");
      pOut.println("<tr><td>");
      cacheData = cacheEntry == null ? "null" : cacheEntry.toString();
      pOut.println(cacheData);
      pOut.println("</td></tr>");

      pOut.println("</table>");
    }
  }

  void removeItemsFromCacheAdmin(HttpServletRequest pRequest,
                                         HttpServletResponse pResponse,
                                         ServletOutputStream pOut)
    throws ServletException, IOException
  {
    String[] removeItems = pRequest.getParameterValues("remove");
    //URLDecoder urlDecoder = new URLDecoder();
    if (removeItems != null) {
      for (int i=0; i < removeItems.length; i++) {
        String decodeKeyName = URLDecoder.decode(removeItems[i]);
        removeCachedElement(decodeKeyName);
      }
      pOut.println("<h2>Cache items have been removed</h2>");
    }
    else {
      pOut.println("<h2>No cache items were selected for removal</h2>");
    }
  }

  /*
   * The object stored with each cache entry
   */
  class CacheDataEntry {

  //-------------------------------------
  /** Class version string */

  public static final String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/Cache.java#2 $$Change: 651448 $";

    /** The last time we recreated this content **/
    long creationTime;

    /** The data */
    byte [] data;

    /** Char data (for dsp4jsp) **/
    char [] charData;

    public CacheDataEntry (byte [] pData) {
      creationTime = System.currentTimeMillis();
      data = pData;
    }

    public CacheDataEntry (char [] pCharData) {
      creationTime = System.currentTimeMillis();
      charData = pCharData;
    }

    public String toString() {
      if (data != null) {
	if (data.length < 100)
	  return new String(data);
	return new String(data, 0, 50) + "...." +
	  new String(data, data.length-50, 50);
      }
      else if (charData != null) {
	if (charData.length < 100)
	  return new String(charData);
	return new String(charData, 0, 50) + "...." +
	  new String(charData, charData.length-50, 50);
      }
      else {
	return "no data";
      }
    }
  }

  //-------------------------------------
  // dsp4jsp methods - these methods are here to allow the dsp4jsp
  // DropletTag to use the CacheDroplet's mechanisms.  The
  // CacheDroplet must be treated as a special case in dsp4jsp, which
  // means that the dsp4jsp DropletTag has to do its best to simulate
  // the behavior of CacheDroplet.  Rather than copying the logic from
  // CacheDroplet to DropletTag, the logic is separated into these
  // methods which the DropletTag can call.  The CacheDroplet has not
  // been changed to use these methods because I didn't want to change
  // any existing code paths (for fear of introducing any regression),
  // so yes, we have duplicate code in the same class - Nathan.
  //-------------------------------------
  /**
   *
   * If this returns false, then the droplet should proceed as if no
   * caching is involved at all.  This would be false if the enabled
   * property is set to false, or if session tracking is enabled and
   * URL rewriting is happening (and hasNoURLs is not set in the
   * request).
   **/
  public boolean isCachingEnabled (DynamoHttpServletRequest pRequest)
  {
    if (!mEnabled) {
      return false;
    }

    // The logic for detecting url rewriting has to change to work on
    // other app servers.  Now we determine that url's are not going
    // to be rewritten if the session id came in over a cookie, or
    // there is no session.  This might not work for app servers that
    // rewrite url's even if cookies are working - in that case, there
    // isn't much else to do but just not use the cache tag.

    boolean mightRewriteUrls =
      pRequest.getSession (false) != null &&
      (!pRequest.isRequestedSessionIdFromCookie () ||
       !pRequest.isRequestedSessionIdValid ());

    Object hasNoURLs;
    if (mightRewriteUrls &&
        ((hasNoURLs = pRequest.getLocalParameter("hasNoURLs")) == null ||
	 hasNoURLs.toString().equalsIgnoreCase("false"))) {
      if (isLoggingDebug())
        logDebug("not using cache because this session is encoding URLs: " +
                 ServletUtil.getCurrentPathInfo(pRequest));
      return false;
    }

    return true;
  }

  //-------------------------------------
  /**
   *
   * Returns the cached content for the given request.  The key for
   * the content is a combination of the request URI and the "key"
   * parameter - the "key" parameter must be specified.  The optional
   * "cacheCheckSeconds" parameter indicates how long the content
   * should remain in the cache.
   **/
  public char [] getCachedContent (DynamoHttpServletRequest pRequest)
  {
    Object key = pRequest.getLocalParameter ("key");
    if (key == null) {
      if (isLoggingError())
        logError(ResourceUtils.getMsgResource("cacheNoKey", MY_RESOURCE_NAME, sResourceBundle));
      return null;
    }
    else {
      // Form the cache key
      String cacheKey = ServletUtil.getCurrentRequestURI(pRequest) + "###" + key;

      // Get the expire time
      long expireTime = mDefaultCacheCheckSeconds * 1000;
      String expireTimeStr = pRequest.getParameter("cacheCheckSeconds");
      if (expireTimeStr != null) {
	try {
	  expireTime = Long.parseLong(expireTimeStr) * 1000;
	}
	catch (NumberFormatException e) {
	  if (isLoggingError())
	    logError("bad value for cacheCheckSeconds parameter");
	}
      }

      // Find in the cache
      CacheDataEntry cdata = (CacheDataEntry) mCache.get(cacheKey);

      if (cdata != null && cdata.charData != null && !shouldRegenerateCachedData(pRequest, cdata, expireTime)) {
        if (isLoggingDebug())
          logDebug("Retrieved item from cache");
        
	return cdata.charData;
      }

      // Otherwise, nothing from the cache
      return null;
    }
  }

  //-------------------------------------
  /**
   *
   * Sets the cached content for the given request.  The key for the
   * content is a combination of the request URI and the "key"
   * parameter, which must be specified.
   **/
  public void setCachedContent (DynamoHttpServletRequest pRequest,
				char [] pContent)
  {
    Object key = pRequest.getLocalParameter ("key");
    if (key == null) {
      if (isLoggingError())
        logError(ResourceUtils.getMsgResource("cacheNoKey", MY_RESOURCE_NAME, sResourceBundle));
      return;
    }

    // Form the cache key
    String cacheKey = ServletUtil.getCurrentRequestURI(pRequest) + "###" + key;

    // This PROTOCOL_CHANGE_ATTRIBUTE is unset in the Droplet tag and maybe
    // set later in the ProtocolChange droplet to avoid caching - Bug# 66179
    // See also:
    //     /atg/droplet/ProtocolChange.java
    //     /atg/taglib/dspjsp/Droplet.java
    if (pRequest.getAttribute(PROTOCOL_CHANGE_DROPLET) != null) {
      if (isLoggingDebug())
        logDebug("Found ProtocolChangeDroplet attribute in response - not caching");
    }
    else if (pRequest.getAttribute(DO_NOT_CACHE_RESPONSE) != null) {
      if (isLoggingDebug())
	logDebug("Found DoNotCacheResponse attribute in response - not caching");
    }
    else {
      if (isLoggingDebug()) {
        logDebug("Adding item to cache ");
      }

      CacheDataEntry cdata = new CacheDataEntry(pContent);
      mCache.put(cacheKey, cdata);
    }
  }

  //-------------------------------------

  /**
   * Initializes the servlet. This is called automatically by the system
   * when the servlet is first loaded.
   * @param pServletConfig servlet configuration information
   * @exception ServletException if a servlet exception has occurred
   */
  public void init(ServletConfig pConfig) throws ServletException {
    super.init(pConfig);

    synchronized (Cache.class) {
      if (sJsessionid == null) {
        // go ahead and set it, so no matter what happens it will
        // have been set
        sJsessionid = ";jsessionid=";

        if (getSessionURLName() != null) {
          sJsessionid = ";" + getSessionURLName() + "=";
        }
      }
    }
  }

}



