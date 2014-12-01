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

package atg.projects.b2bstore.servlet;

// Java classes
import javax.servlet.ServletException;
import java.io.IOException;

// DAS classes
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.HttpSessionRequest;
import atg.servlet.pipeline.InsertableServletImpl;
import atg.nucleus.ServiceException;
import atg.servlet.ServletUtil;
import atg.servlet.sessionsaver.SessionSaverServlet;
import atg.core.net.URLUtils;

/**
 * This Servlet checks the session for expiration and if the session is expired it redirects to
 * <code>expirationURL</code>. This also handles server failover conditions by checking against
 * the parameter <code>sessionRestored</code> in <code>DynamoHttpServletRequest</code>.
 * If the user is accessing dynamo site and the dynamo that is serving the user goes down, and
 * if another server handles the subsequest request from user, the session of the user will
 * be invalid and the new dynamo server creates a new session and restores session information
 * of the user in new session from session backup server. <code>sessionRestored</code> is set to
 * now invalid old session id.
 *
 * @author Manoj Potturu
 * @version $Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/servlet/CheckSessionExpiration.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @see atg.projects.b2bstore.servlet.WACheckSessionExpiration
 */

public class CheckSessionExpiration extends InsertableServletImpl
{

  //-------------------------------------
  // Class version string
  //-------------------------------------

  public static String CLASS_VERSION = "$Id: //product/B2BCommerce/version/10.0.3/Java/atg/projects/b2bstore/servlet/CheckSessionExpiration.java#2 $$Change: 651448 $";
    
  //--------------------------------------------------
  // Constants
  //--------------------------------------------------

  //--------------------------------------------------
  // Member Variables
  //--------------------------------------------------

  //--------------------------------------------------
  // Properties
  //--------------------------------------------------


  /** Expiration URl */
  String mExpirationURL;

  //-------------------------------------
  /**
   * Sets Expiration URl
   **/
  public void setExpirationURL(String pExpirationURL) {
    mExpirationURL = pExpirationURL;
  }

  //-------------------------------------
  /**
   * Returns Expiration URl
   **/
  public String getExpirationURL() {
    return mExpirationURL;
  }


  /** Path of directory to check for session expiration */
  String mSessionExpirationPath;

  //-------------------------------------
  /**
   * Sets Path of directory to check for session expiration
   **/
  public void setSessionExpirationPath(String pSessionExpirationPath) {
    mSessionExpirationPath = pSessionExpirationPath;
  }

  //-------------------------------------
  /**
   * Returns Path of directory to check for session expiration
   **/
  public String getSessionExpirationPath() {
    return mSessionExpirationPath;
  }

  
  
  //--------------------------------------------------
  // Constructors
  //--------------------------------------------------

  public CheckSessionExpiration() {
  }
  
  //--------------------------------------------------
  // Methods 
  //--------------------------------------------------


  /*
   * This method checks the session of incoming request for expiration and redirects to expirationURL
   * if the session is expired. It handles both the registered and anonymous customers alike. It also
   * checks for server failover condition when checking for session expiration.
   * <code>sessionRestored</code> property of <code>DynamoHttpServletRequest</code> will be set
   * to the  sessionId from browser which has been restored by the new Dynamo
   * server in case of session restore.
   *
   */
  public void service (DynamoHttpServletRequest pRequest,
                       DynamoHttpServletResponse pResponse)
    throws IOException, ServletException
  {

    HttpSessionRequest sessionRequest = pRequest.getSessionRequest();

    // HACK for Bug 73226
    //
    // For some unknown reason, on Websphere when the
    // TemplateEmailSender made a request to generate an email, this
    // servlet reported that the session was invalid and tried to
    // redirect the request to the top level of the web app.  However
    // TemplateEmailSender did not know how to handle the redirect
    // (which appeared as an HTTP 302 code) and failed.
    //
    // To get around this problem we simply don't do the session
    // check since the sessions are valid from a Dynamo point of
    // view.  Template Email continues to work if we ignore this
    // check.
    //
    if (ServletUtil.isWebSphere()) {
      String query = pRequest.getQueryString();
      if (isLoggingDebug())
        logDebug("Query string = " + pRequest.getQueryString());
      if (query != null && query.indexOf("_templateEmailRequest") != -1)
        passRequest (pRequest, pResponse);
    }

    // We need to make sure that the request does have a session id, if it doesn't
    // we don't want to redirect.
    String requestedSessionId = pRequest.getRequestedSessionId();


    // Get the session id , if the session is the from the same server than the
    // the sessionId will be null, otherwise it will be sessionId from failed dynamo server.
    String isSessionRestored = pRequest.getParameter(SessionSaverServlet.SESSION_RESTORED);
    
    // session id will be invalid if session expires.
    boolean sessionIdInvalid = sessionRequest.isRequestedSessionIdInvalid();


    if (isLoggingDebug())
    {
      // check where session ID is from
      String whereFrom;
      if (pRequest.isRequestedSessionIdFromCookie()) {
        whereFrom = "from cookie";
      }
      else {
        whereFrom = "from URL";
      }

      logDebug("Requested session id " + whereFrom + 
               " = " + requestedSessionId +
               ", sessionIdInvalid = " + sessionIdInvalid +
               ", sessionRestored = " + isSessionRestored);
    }
    
    // check for expired session without session restore.
    if ( requestedSessionId != null && ( sessionIdInvalid && (isSessionRestored == null))) {
      // Check whether the url invoked is under the invoked configured path.
      String requestURI = pRequest.getRequestURI();
      if (isLoggingDebug())
        logDebug("Found expired session for " + requestURI);

      if (requestURI.indexOf(getSessionExpirationPath()) == 0 ) {
        String errorURL = getExpirationURL();
        if (URLUtils.isRelative(errorURL)) {
          if (isLoggingDebug())
            logDebug("Redirecting expired session to local URL " + errorURL);
          pResponse.sendLocalRedirect(errorURL, pRequest);
          return;
        }
        else {
          if (isLoggingDebug())
            logDebug("Redirecting expired session to non-local URL " + errorURL);
          pResponse.sendRedirect(errorURL);
          return;
        }
      } // end of if ()
    } // end of if ()
    passRequest (pRequest, pResponse);
  }
}   // end of class
