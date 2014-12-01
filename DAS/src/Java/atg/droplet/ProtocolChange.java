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

import javax.servlet.*;
import javax.servlet.http.*;

import atg.core.net.URLUtils;

import atg.servlet.*;
import atg.nucleus.naming.ParameterName;

import java.io.*;
import java.net.*;

/**
 * The ChangeProtocol droplet takes in a url and outputs a full url to 
 * that location for both secure and insecure protocols.  Enable this 
 * by setting the enable property to true.
 *
 * <dl>
 *
 * <dt>inUrl 
 *
 * <dd>Required output param. The relative or full url that you want to convert to another protocol.
 *
 * <dt>output
 *
 * <dd>The oparam rendered when the request is serviced.
 *
 * <dt>secureUrl
 *
 * <dd>The url in secure protocol.
 *
 * <dt>nonSecureUrl
 *
 * <dd>The url in secure protocol.
 *
 * </dl>
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/droplet/ProtocolChange.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
public class ProtocolChange extends DynamoServlet {
  //-------------------------------------
  // Class version string

  public static String CLASS_VERSION = "$Id: //product/DAS/version/10.0.3/Java/atg/droplet/ProtocolChange.java#2 $$Change: 651448 $";
  
  public final static int DEFAULT_SECURE_PORT = 443;
  public final static int DEFAULT_NON_SECURE_PORT = 80;

  public final static ParameterName IN_URL = ParameterName.getParameterName("inUrl");
  public final static ParameterName OUTPUT = ParameterName.getParameterName("output");
  public final static ParameterName SECURE_URL = ParameterName.getParameterName("secureUrl");
  public final static ParameterName NON_SECURE_URL = ParameterName.getParameterName("nonSecureUrl");

  //-------------------------------------
  /**
   * Service the request
   */
  public void service(DynamoHttpServletRequest pReq, 
                      DynamoHttpServletResponse pRes) 
    throws ServletException, IOException 
  {
    String inUrl;
    if ((inUrl = pReq.getParameter(IN_URL)) != null) {
      if (!isEnable()) {
        if (inUrl != null) {
          // on websphere (and perhaps other application servers)
          // relative paths are NOT handled well, so we'll
          // absolutize things here
        	
          // Bug fix for 70888: The default EncodeContextPathMode is ENCODE_CONTEXT_PATH, and in this case,
          // when ProtocolChange is disabled, we only want to encode it if it's not there. 
          pReq.setEncodeContextPathMode(DynamoHttpServletRequest.ENCODE_IF_NOT_THERE);
          
          inUrl = ServletUtil.makeURIAbsolute(inUrl, pReq);
        }

      	/* Make sure to put session id in there if necessary */
        inUrl = pRes.encodeRedirectURL(inUrl);

        // Avoid Caching this in the Cache droplet if the session ID is encoded
        // in the URL - Bug#66179
        // See also:
        //     /atg/droplet/Cache.java
        //     /atg/taglib/dspjsp/Droplet.java
        pReq.setAttribute(Cache.PROTOCOL_CHANGE_DROPLET, "true");
        if (isLoggingDebug())
          logDebug("Setting protocolChangeDroplet attribute to true");

        pReq.setParameter("secureUrl", inUrl);
        pReq.setParameter("nonSecureUrl", inUrl);
        pReq.serviceLocalParameter(OUTPUT, pReq, pRes);
        return;
      }

      try {
        // the url that comes in may be full or relative.  make a full local url
        // by making a url of the inUrl with the request's url as context. 
        URL context = new URL ("http", pReq.getServerName(), pReq.getServerPort(), pReq.getRequestURI());
        if (isLoggingDebug())
          logDebug("context = " + context.toString());

        URL temp = new URL (context, inUrl);
        if (isLoggingDebug())
          logDebug("temp = " + temp.toString());
        String file = temp.getFile();
        
        StringBuffer secure = new StringBuffer("https://");
        secure.append(getSecureHost());
        if (getSecurePort() != DEFAULT_SECURE_PORT)
          secure.append(":").append(getSecurePort());
        if (!file.startsWith("/"))
          secure.append("/");
        secure.append(file);

	if (isLoggingDebug()){
	  logDebug("default secure port = " + DEFAULT_SECURE_PORT);
	  logDebug("secure port = " + getSecurePort());
	  logDebug("secure url = " + secure.toString());
	}
        
        StringBuffer nonSecure = new StringBuffer("http://");
        nonSecure.append(getNonSecureHost());
        if (getNonSecurePort() != DEFAULT_NON_SECURE_PORT)
          nonSecure.append(":").append(getNonSecurePort());
        if (!file.startsWith("/"))
          nonSecure.append("/");
        nonSecure.append(file); 

	if (isLoggingDebug()){
	  logDebug("default non secure port = " + DEFAULT_NON_SECURE_PORT);
	  logDebug("non secure port = " + getNonSecurePort());
	  logDebug("non secure url = " + nonSecure.toString());
	}

          /* Make sure to put session id in there if necessary: 44635 */  
        pReq.setParameter("secureUrl", pRes.encodeRedirectURL(secure.toString()));
        pReq.setParameter("nonSecureUrl", pRes.encodeRedirectURL(nonSecure.toString()));
        
        // Avoid Caching this in the Cache droplet if the session ID is encoded
        // in the URL - Bug#66179
        // See also:
        //     /atg/droplet/Cache.java
        //     /atg/taglib/dspjsp/Droplet.java
        pReq.setAttribute(Cache.PROTOCOL_CHANGE_DROPLET, "true");
        if (isLoggingDebug())
          logDebug("Setting protocolChangeDroplet attribute to true");

        pReq.serviceLocalParameter(OUTPUT, pReq, pRes);
      }
      catch (MalformedURLException e) {
        if (isLoggingError())
          logError(e);
      }
    }
  }

  //-------------------------------------
  // property: enable 
  boolean mEnable;
  /**
   * If this is true then the secureUrl and nonSecureUrl 
   * output parameters will be fully qualified urls to 
   * the appropriate secure or non secure server.
   * If false, then the secureUrl and nonSecureUrl will
   * just be the same value as the inUrl with no processing
   * at all.
   */  
  public void setEnable(boolean pEnable) {
    mEnable = pEnable;
  }
  public boolean isEnable() {
    return mEnable;
  }
  
  //-------------------------------------
  // property: secureHost
  String mSecureHost;
  public void setSecureHost(String pSecureHost) {
    mSecureHost = pSecureHost;
  }
  public String getSecureHost() {
    return mSecureHost;
  }

  //-------------------------------------
  // property: nonSecureHost
  String mNonSecureHost;
  public void setNonSecureHost(String pNonSecureHost) {
    mNonSecureHost = pNonSecureHost;
  }
  public String getNonSecureHost() {
    return mNonSecureHost;
  }

  //-------------------------------------
  // property: securePort
  int mSecurePort;
  public void setSecurePort(int pSecurePort) {
    mSecurePort = pSecurePort;
  }
  public int getSecurePort() {
    return mSecurePort;
  }

  //-------------------------------------
  // property: nonSecurePort
  int mNonSecurePort;
  public void setNonSecurePort(int pNonSecurePort) {
    mNonSecurePort = pNonSecurePort;
  }
  public int getNonSecurePort() {
    return mNonSecurePort;
  }

  //-------------------------------------
  // property: secureProtocol
  String mSecureProtocol = "https";
  /**
   * Sets the property <code>secureProtocol</code>.<BR>
   * Note, changing this property will NOT change
   * the protocol used for secure requests.<BR>
   * https:// will always be used as the secure
   * protocol.
   **/
  public void setSecureProtocol(String pSecureProtocol) {
    mSecureProtocol = pSecureProtocol;
  }
  public String getSecureProtocol() {
    return mSecureProtocol;
  }

  //-------------------------------------
  // property: nonSecureProtocol
  String mNonSecureProtocol = "http";
  /**
   * Sets the property <code>nonSecureProtocol</code>.<BR>
   * Note, changing this property will NOT change
   * the protocol used for non secure requests.<BR>
   * http:// will always be used as the non secure
   * protocol.
   **/
  public void setNonSecureProtocol(String pNonSecureProtocol) {
    mNonSecureProtocol = pNonSecureProtocol;
  }
  public String getNonSecureProtocol() {
    return mNonSecureProtocol;
  }  

  //-------------------------------------
  // property: defaultSecurePort
  int mDefaultSecurePort = 443;
  /** @deprecated **/
  public void setDefaultSecurePort(int pDefaultSecurePort) {
    if (isLoggingWarning())
      logWarning("Property has been deprecated. Refer to DEFAULT_SECURE_PORT");
  }
  /** @deprecated **/
  public int getDefaultSecurePort() {
    if (isLoggingWarning())
      logWarning("Property has been deprecated. Refer to DEFAULT_SECURE_PORT");
    return DEFAULT_SECURE_PORT;
  }

  //-------------------------------------
  // property: defaultNonSecurePort
  /** @deprecated **/
  int mDefaultNonSecurePort = 80;
  public void setDefaultNonSecurePort(int pDefaultNonSecurePort) {
     if (isLoggingWarning())
      logWarning("Property has been deprecated. Refer to DEFAULT_NON_SECURE_PORT");
  }
  /** @deprecated **/
  public int getDefaultNonSecurePort() {
    if (isLoggingWarning())
      logWarning("Property has been deprecated. Refer to DEFAULT_NON_SECURE_PORT");
    return DEFAULT_NON_SECURE_PORT;
  }
  
}
