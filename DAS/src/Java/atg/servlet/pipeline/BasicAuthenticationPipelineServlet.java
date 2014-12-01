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

package atg.servlet.pipeline;

import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.servlet.*;
import javax.servlet.http.*;

import atg.servlet.*;
import atg.nucleus.*;
import atg.core.util.Base64;
import atg.security.*;

/**
 *
 * <p>This pipeline servlet implements Basic authentication.  If the
 * request comes in bearing an Authorization header with Basic
 * authentication, then this servlet consults an Authenticator to see
 * if the specified id/password is authenticated, and passes the
 * request on if so.  If not, then a challenge is issued using the
 * specified realm property.
 *
 * @author Nathan Abramson
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/BasicAuthenticationPipelineServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 **/

public
class BasicAuthenticationPipelineServlet
extends PipelineableServletImpl
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/BasicAuthenticationPipelineServlet.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Resources
  private static ResourceBundle sResources = ResourceBundle.getBundle("atg.servlet.pipeline.BasicAuthenticationPipelineServletResources", atg.service.dynamo.LangLicense.getLicensedDefault());
  private static String SUCCESSFUL_AUTHENTICATION = sResources.getString("successfulAuthentication");
  private static String FAILED_AUTHENTICATION = sResources.getString("failedAuthentication");
  private static String ANONYMOUS_USER = sResources.getString("anonymousUser");

  //-------------------------------------
  // Properties

  /** The realm of the request */
  String mRealm;

  /** The header value for the authenticate header */
  String mAuthenticateHeaderValue;

  /** The Authenticator to use to authenticate */
  Authenticator mAuthenticator;

  /** Optional mappings from path prefixes to special-purpose authenticators */
  ServiceMap mPathAuthenticatorMap;

  /** Array of path prefixes derived from mPathAuthenticatorMap */
  String[] mPathList;

  /** Array of authenticators derived from mPathAuthenticatorMap */
  Authenticator[] mAuthenticatorList;

  /** Is authentication enabled? */
  boolean mEnabled = true;

  /** HTML message which is displayed when authentication fails */
  String mUnauthorizedMessage = null;

  /**
   * If true, informational log messages will be emitted whenever
   * a user is authenticated.
   */
  boolean mLogSuccessfulAuthentications = false;

  /**
   * If true, informational log messages will be emitted whenever
   * authentication for the request fails.
   */
  boolean mLogFailedAuthentications = true;

  //-------------------------------------
  /**
   * Constructs a new BasicAuthenticaionPipelineServlet
   **/
  public BasicAuthenticationPipelineServlet ()
  {
  }

  //-------------------------------------
  // Properties
  //-------------------------------------
  /**
   * Returns the realm that will be displayed to the user in the
   * authentication request.
   * @beaninfo
   *   description: The realm of the authentication
   **/
  public String getRealm ()
  {
    return mRealm;
  }

  //-------------------------------------
  /**
   * Sets the realm that will be displayed to the user in the
   * authentication request.
   **/
  public void setRealm (String pRealm)
  {
    mRealm = pRealm;
    mAuthenticateHeaderValue = null;
  }

  //-------------------------------------
  /**
   * Returns the header value for the authentication header
   * @beaninfo
   *   description: The value of the authentication header
   **/
  public String getAuthenticateHeaderValue ()
  {
    if (mAuthenticateHeaderValue == null) {
      synchronized (this) {
        if (mAuthenticateHeaderValue == null) {
          mAuthenticateHeaderValue = 
            "Basic realm=\"" + 
            getRealm () +
            "\"";
        }
      }
    }
    return mAuthenticateHeaderValue;
  }

  //---------------------------------------------------------------------------
  // property: noWarnIfNonexistantLoginNames

  List<String> mNoWarnIfNonExistantLoginNames = new CopyOnWriteArrayList<String>();

  /** The array of login names not warn for non-existant logins. */
  public void setNoWarnIfNonexistantLoginNames(String[] pNoWarnIfNonexistantLoginNames) {
    mNoWarnIfNonExistantLoginNames.clear();
    mNoWarnIfNonExistantLoginNames.addAll(Arrays.asList(pNoWarnIfNonexistantLoginNames));
  }

  /** The array of login names not warn for non-existant logins. */  
  public String[] getNoWarnIfNonexistantLoginNames() {
    return mNoWarnIfNonExistantLoginNames.toArray(new String[0]);
  }

  /** Add a login name to the list of login names not warn for non-existant logins. */  
  public void addNoWarnIfNonExistantLoginName(String pLoginName) {
    if (!mNoWarnIfNonExistantLoginNames.contains(pLoginName)) {
      mNoWarnIfNonExistantLoginNames.add(pLoginName);
    }
  }

  
  /** Remove a login name from the list of login names not warn for non-existant logins. */
  public boolean removeNoWarnIfNonExistantLoginName(String pLoginName) {  
    return mNoWarnIfNonExistantLoginNames.remove(pLoginName);
  }
  

  //-------------------------------------
  /**
   * Returns the authenticator used to verify id/password combinations
   * @beaninfo
   *   description: The authenticator used to verify ID/password combinations
   **/
  public Authenticator getAuthenticator ()
  {
    return mAuthenticator;
  }

  //-------------------------------------
  /**
   * Sets the authenticator used to verify id/password combinations
   **/
  public void setAuthenticator (Authenticator pAuthenticator)
  {
    mAuthenticator = pAuthenticator;
  }

  /**
   * Sets the path authenticator map.
   * @beaninfo
   *   description: The path authenticator map
   */
  public void setPathAuthenticatorMap(ServiceMap pMap)
  {
    mPathAuthenticatorMap = pMap;
  }

  /**
    Gets the path authenticator map.
    */
  public ServiceMap getPathAuthenticatorMap()
  {
    return mPathAuthenticatorMap;
  }
  
  /**
   * Is authentication enabled?
   * @beaninfo
   *   description: True if authentication is enabled
   **/
  public boolean getEnabled ()
  {
    return mEnabled;
  }

  //-------------------------------------
  /**
   * Returns the enabled flag for authentication.
   **/
  public void setEnabled (boolean pEnabled)
  {
    mEnabled = pEnabled;
  }

  /**
   * Returns true if informational log messages will be emitted whenever
   * a user is authenticated, false if no logging will be performed.<p>
   *
   * Default is false.
   *
   * @beaninfo
   *   description: True if all successful authentications should be logged.
   */
  public boolean getLogSuccessfulAuthentications()
  {
    return mLogSuccessfulAuthentications;
  }

  /**
   * If set to true, informational log messages will be emitted whenever
   * a user is authenticated.
   */
  public void setLogSuccessfulAuthentications(boolean pDoLog)
  {
    mLogSuccessfulAuthentications = pDoLog;
  }

  /**
   * Retturns true if informational log messages will be emitted whenever
   * authentication for the request fails, false if no logging will be
   * performed.<p>
   *
   * Default is true.
   *
   * @beaninfo
   *   description: True if all failed authentications should be logged.
   */
  public boolean getLogFailedAuthentications()
  {
    return mLogFailedAuthentications;
  }

  /**
   * If true, informational log messages will be emitted whenever
   * authentication for the request fails.
   */
  public void setLogFailedAuthentications(boolean pDoLog)
  {
    mLogFailedAuthentications = pDoLog;
  }

  //-------------------------------------
  /**
   * Returns the HTML message that will be displayed to the user if
   * authentication fails.
   * @beaninfo
   *   description: The HTML message which is displayed when authentication fails
   **/
  public String getUnauthorizedMessage ()
  {
    return mUnauthorizedMessage;
  }

  //-------------------------------------
  /**
   * Sets the HTML message that will be displayed to the user if
   * authentication fails.
   **/
  public void setUnauthorizedMessage (String pUnauthorizedMessage)
  {
    mUnauthorizedMessage = pUnauthorizedMessage;
  }

  //----------------------------------------
  /**
   * Initialize the prefix-to-authenticator correspondence arrays, if
   * needed, on initialization.
   */
  public void doStartService() throws ServiceException
  {
    super.doStartService();

    if (mPathAuthenticatorMap == null)
      return;  // no path-specific authenticators so forget it

    int count = mPathAuthenticatorMap.size();
    mPathList = new String[count];
    mAuthenticatorList  = new Authenticator[count];

    Enumeration vdirs = mPathAuthenticatorMap.keys();
    for (int i = 0; i < count; i++)
    {
      String vdir = (String) vdirs.nextElement();

      // Find the index of the last character which is not a slash.
      int j = vdir.length() - 1;
      while (vdir.charAt(j) == '/')
        j--;

      mPathList[i] = vdir.substring(0, j + 1); 
      mAuthenticatorList[i] = (Authenticator) mPathAuthenticatorMap.get(vdir);
    }

    if (isLoggingDebug())
    {
      StringBuffer buf = new StringBuffer();
      
      for (int i = 0; i < count; i++)
        buf.append(mPathList[i] + " --> " +
                   mAuthenticatorList[i] + "\n");

      logDebug("\n" + buf.toString());
    }
  }

  /**
    If the given pathInfo starts with an authenticator path, returns
    the index into the special authenticator list.  Otherwise, returns -1.
    */

  int getPathIndex(String pPathInfo)
  {
    if (mPathList == null || pPathInfo == null)
      return -1;

    // Loop through all the virtual directories, checking for a match
    // with pathInfo.
    for (int i = 0; i < mPathList.length; i++)
    {

      String vdir = mPathList[i];
      int vdirLen = vdir.length();

      // We have a virtual directory match in pathInfo only if
      // pathInfo starts with vdir AND it ends at a directory
      // delimiter (i.e., either the end of the string or a slash).
      // This avoids incorrectly identifying a scenario such as
      // vdir=/toys and pathInfo=/toystory/foo, as a virtual path match.
      if (pPathInfo.startsWith(vdir)  &&
          (pPathInfo.length() == vdirLen ||
           pPathInfo.charAt(vdirLen) == '/'))
        return i;

    }


    // If we make it this far, no virtual directories matched.
    return -1;
  }

  //-------------------------------------
  // PipelineableServletImpl methods
  //-------------------------------------
  /**
   * Services a DynamoHttpServletRequest/Response pair
   * @exception ServletException if an error occurred while processing
   * the servlet request
   * @exception IOException if an error occurred while reading or writing
   * the servlet request
   **/
  public void service (DynamoHttpServletRequest pRequest,
                       DynamoHttpServletResponse pResponse)
       throws IOException, ServletException 
  {
    boolean authenticated = false;
    Authenticator obj = null;
    User originalUser = null;
    String clientIP = pRequest.getRemoteAddr();

    try {
      // remember the User object that is bound to the thread before
      // we attempt to authenticate.  The authenticator may choose to
      // rebind it.
      originalUser = ThreadSecurityManager.currentUser();

      if (mPathList != null)
      {
        int pathIndex = getPathIndex(pRequest.getPathInfo());
        if (pathIndex != -1)
          obj = mAuthenticatorList[pathIndex];
      }

      if (obj == null)
        obj = getAuthenticator();
    
      if (mEnabled) {
        String auth = pRequest.getHeader ("Authorization");
        boolean logIfFailed = mLogFailedAuthentications;
        if (obj == null) {
          authenticated = true;
          if (isLoggingInfo() && mLogSuccessfulAuthentications)
            logInfo(MessageFormat.format(ANONYMOUS_USER, clientIP));
        }
        else if (auth == null) {
          authenticated = obj.authenticate("", "");
          if (authenticated) {
            if (isLoggingInfo() && mLogSuccessfulAuthentications)
              logInfo(MessageFormat.format(ANONYMOUS_USER, clientIP));
          }
          // we don't log a failure here because with basic auth the
          // first attempt will have no auth info and will always
          // fail.
        }
        else {
          int ix = auth.indexOf (' ');
          if (ix >= 0) {
            String authscheme = auth.substring (0, ix);
            if (authscheme.equalsIgnoreCase ("Basic")) {
              String authval = auth.substring (ix + 1);
              String plain = Base64.decodeToString (authval);
              int ix2 = plain.indexOf (':');
              if (ix2 >= 0) {
                String id = plain.substring (0, ix2);
                String password = plain.substring (ix2 + 1);
                if (obj.authenticate (id, password)) {
                  authenticated = true;
                  if (isLoggingInfo() && mLogSuccessfulAuthentications)
                    logInfo(MessageFormat.format(SUCCESSFUL_AUTHENTICATION, id, clientIP));
                }
                else if (mNoWarnIfNonExistantLoginNames.contains(id)) {
                  logIfFailed = false;
                }
              }
            }
          }
        }
        // this is the only case where a failed auth is both possible
        // and ought to be logged.
        if (!authenticated) {
          if (isLoggingInfo() && logIfFailed)
            logInfo(MessageFormat.format(FAILED_AUTHENTICATION, clientIP) + "(" + pRequest.getRequestURI() + ")");
        }
      }
      else {
        authenticated = true;
        if (isLoggingInfo() && mLogSuccessfulAuthentications)
          logInfo(MessageFormat.format(ANONYMOUS_USER, clientIP)  + "(" + pRequest.getRequestURI() + ")");
      }

      if (!authenticated) {
        pResponse.setHeader ("WWW-Authenticate",
                             getAuthenticateHeaderValue ());
        if (null == mUnauthorizedMessage) {
          pResponse.sendError (HttpServletResponse.SC_UNAUTHORIZED);
        }
        else {
          pResponse.sendError (HttpServletResponse.SC_UNAUTHORIZED,
                               mUnauthorizedMessage);
        }
      }
      else {
        passRequest (pRequest, pResponse);
      }
    }
    finally {
      // reset thread identity to whatever it was before authentication.
      ThreadSecurityManager.setThreadUser(originalUser);
    }
  }

  //-------------------------------------
}
