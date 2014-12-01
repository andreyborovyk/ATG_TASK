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
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.Hashtable;
import java.util.Enumeration;
import atg.servlet.*;
import atg.nucleus.ServiceMap;
import atg.core.util.Base64;
import atg.core.util.ArraySorter;
import atg.core.util.SortOrder;

/**
 * Internal helper class that sorts two strings by length, shortest first
 */

class ReverseLengthSort implements SortOrder
{
  public int order(Object obj1, Object obj2) {
    return ((String)obj1).length() - ((String)obj2).length();
  }
}

/**
 * This pipeline servlet implements Basic authentication for a
 * set of request URL paths.  If a request comes in bearing an
 * Authorization header with Basic authentication, then this servlet
 * consults an Authenticator to see if the specified id/password
 * is authenticated, and passes the request on if so.  If not,
 * then a challenge is issued using the specified realm property.
 *
 * @author Matt Landau (working from Nate's BasicAuthenticationPipelineServlet)
 * @version $Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/PathAuthenticationPipelineServlet.java#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 * @beaninfo
 *   description: Implements Basic authentication for a set of URL paths
 *   attribute: functionalComponentCategory Services
 *   attribute: featureComponentCategory Pipeline
 *   attribute: icon /atg/ui/common/images/pipelinecomp.gif
 **/

public
class PathAuthenticationPipelineServlet
extends PipelineableServletImpl
{
  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION =
  "$Id: //product/DAS/version/10.0.3/Java/atg/servlet/pipeline/PathAuthenticationPipelineServlet.java#2 $$Change: 651448 $";

  //-------------------------------------
  // Properties

  /** The realm of the request */
  String mRealm;

  /** The header value for the authenticate header */
  String mAuthenticateHeaderValue;

  /** The list of Authenticators to use to authenticate various paths */
  ServiceMap mAuthenticators;

  /** The list of authenticated paths, sorted by length (shortest to longest) */
  String[] mAuthenticatedPaths;

  /** Is authentication enabled? */
  boolean mEnabled = true;

  /** Should we cache results? */
  boolean mCacheResults = false;

  //-------------------------------------
  /**
   * Constructs a new BasicAuthenticaionPipelineServlet
   **/
  public PathAuthenticationPipelineServlet ()
  {
  }

  //-------------------------------------
  // Properties
  //-------------------------------------
  /**
   * Returns the realm that will be displayed to the user in the
   * authentication request.
   * @beaninfo
   *   description: The realm of the authentication request
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
   **/
  public String getAuthenticateHeaderValue (Authenticator pObj)
  {
    if (pObj instanceof BasicAuthenticator) {
      String realm = ((BasicAuthenticator)pObj).getRealm();
      if (realm != null)
        return "Basic realm=\"" + realm + "\"";
    }
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

  //-------------------------------------
  /**
   * Returns the authenticator used to verify id/password combinations for a specific path
   **/

  private Hashtable mPathCache = new Hashtable();

  public Authenticator getAuthenticator (String pRequestPath)
  {
    Authenticator obj = null;
    String prefix = null;

    /* First check the path cache */

    if (mCacheResults)
    {
      if ((obj = (Authenticator)mPathCache.get(pRequestPath)) != null)
      {
        if (isLoggingDebug())
          logDebug("Returning cached authenticator for " + pRequestPath);
        return obj;
      }
    }

    /* Not found, so check each authenticated path prefix and find longest match */

    if (mAuthenticatedPaths != null)
    {
      for (int n = mAuthenticatedPaths.length ; --n >= 0 ; )
      {
        prefix = mAuthenticatedPaths[n];
        if (pRequestPath.startsWith(prefix))
        {
          obj = (Authenticator)mAuthenticators.get(prefix);
          break;
        }
      }
    }

    if (isLoggingDebug())
    {
      if (obj == null)
        logDebug("No authenticator for " + pRequestPath);
      else
        logDebug("Found authenticator for " + pRequestPath + " based on " + prefix);
    }

    if (obj != null && mCacheResults)
    {
      if (isLoggingDebug())
        logDebug("Caching authenticator for " + pRequestPath);
      mPathCache.put(pRequestPath, obj);
    }

    return obj;
  }

  //-------------------------------------
  /**
   * Returns the authenticators used to verify id/password combinations for paths.
   * When a path is authenticated, the list of authenticators will be searched for
   * the longest match between the authenticator key and the requested path, and
   * the corresponding authenticator will be used.
   * @beaninfo
   *   description: Mapping of paths to authenticators
   **/

  public ServiceMap getAuthenticators ()
  {
    return mAuthenticators;
  }

  //-------------------------------------
  /**
   * Sets the authenticator used to verify id/password combinations.
   * When a path is authenticated, the list of authenticators will be searched for
   * the longest match between the authenticator key and the requested path, and
   * the corresponding authenticator will be used.
   **/

  public void setAuthenticators (ServiceMap pAuthenticators)
  {
    mAuthenticators = pAuthenticators;

    if (mAuthenticators == null)
      mAuthenticatedPaths = null;
    else
    {
      mAuthenticatedPaths = new String[mAuthenticators.size()];
      Enumeration e;
      int n;

      for (n = 0, e = mAuthenticators.keys() ; e.hasMoreElements() ; )
        mAuthenticatedPaths[n++] = (String)e.nextElement();

      ArraySorter.sort(mAuthenticatedPaths, new ReverseLengthSort());
    }
  }

  //-------------------------------------

  /**
   * Do we cache results when looking up authenticators?
   * @beaninfo
   *   description: True if we should cache results when looking up
   *                authenticators
   */
  public boolean getCacheResults ()
  {
    return mCacheResults;
  }

  /**
   * Do we cache results when looking up authenticators?
   */
  public boolean isCacheResults()
  {
    return mCacheResults;
  }

  //-------------------------------------

  /**
   * Set whether to cache results when looking up authenticators
   */
  public void setCacheResults (boolean pCacheResults)
  {
    mCacheResults = pCacheResults;
  }

  //-------------------------------------

  /**
   * Is authentication enabled?
   * @beaninfo
   *   description: True if authentication is enabled
   */
  public boolean getEnabled ()
  {
    return mEnabled;
  }

  /** Is authentication enabled? */
  public boolean isEnabled()
  {
    return mEnabled;
  }

  //-------------------------------------

  /**
   * Set whether or not authentication is enabled
   */
  public void setEnabled (boolean pEnabled)
  {
    mEnabled = pEnabled;
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
    String path = isUsePathInfo() ? pRequest.getPathInfo() : pRequest.getRequestURI();
    Authenticator obj = null;

    if (isLoggingDebug())
      logDebug("Servicing " + path);

    if (mEnabled) {
      if (isLoggingDebug())
        logDebug("Authenticating " + path);

      String auth = pRequest.getHeader ("Authorization");
      obj = getAuthenticator (path);

      if (obj == null)
        authenticated = true;
      else if (auth == null)
        authenticated = obj.authenticate("", "");
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
              }
            }
          }
        }
      }
    }
    else authenticated = true;

    if (!authenticated) {
      if (isLoggingDebug())
        logDebug("Sending UNAUTHORIZED response for " + path);
      pResponse.setHeader ("WWW-Authenticate", getAuthenticateHeaderValue (obj));
      pResponse.sendError (HttpServletResponse.SC_UNAUTHORIZED);
    }
    else {
      if (isLoggingDebug())
        logDebug("Approving access to " + path);
      passRequest (pRequest, pResponse);
    }
  }
  //-------------------------------------
}

