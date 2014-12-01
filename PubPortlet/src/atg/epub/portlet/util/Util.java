/*<ATGCOPYRIGHT>
 * Copyright (C) 1997-2011 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution ofthis
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

package atg.epub.portlet.util;

import javax.servlet.http.HttpSession;
import javax.portlet.RenderRequest;
import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import atg.core.util.StringUtils;
import atg.epub.servlet.VersioningLayerTools;
import atg.nucleus.Nucleus;
import atg.portal.servlet.Attribute;
import atg.portal.servlet.PortalServletRequest;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.web.ATGSession;

/**
 * Utility class for portlet related functions
 *
 * @author Jeff Banister
 * @author Peter Eddy
 * @version $Id: //product/PubPortlet/version/10.0.3/classes.jar/atg/epub/portlet/util/Util.java#2 $
 */
public class Util
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/PubPortlet/version/10.0.3/classes.jar/atg/epub/portlet/util/Util.java#2 $$Change: 651448 $";

  
  private static final String
    VERSIONING_LAYER_TOOLS_PATH = "/atg/epub/servlet/VersioningLayerTools";

  private static final String 
    ATG_SESSION = "/atg/web/ATGSession";

  private static VersioningLayerTools sVersioningLayerTools;

  /**
   * Lazily create VersioningLayerTools
   *
   */
  public static VersioningLayerTools getVersioningLayerTools()
  {
    if ( sVersioningLayerTools == null ) {
      sVersioningLayerTools = (VersioningLayerTools)
        Nucleus.getGlobalNucleus().resolveName( VERSIONING_LAYER_TOOLS_PATH );
    }
    return sVersioningLayerTools;
  }

  /**
   * Get the HttpSession from a Portlet RenderRequest
   *
   * @return the HTTP session
   */
  public static HttpSession getSessionFromRequest( RenderRequest pRequest )
  {
    PortalServletRequest portalServletRequest =
      (PortalServletRequest) pRequest.getAttribute( Attribute.PORTALSERVLETREQUEST );

    return portalServletRequest.getSession( true );
  }

  /**
   * Get the HttpSession from a Portlet RenderRequest
   *
   * @return the HTTP session
   */
  public static HttpSession getSessionFromRequest( ActionRequest pRequest )
  {
    PortalServletRequest portalServletRequest =
      (PortalServletRequest) pRequest.getAttribute( Attribute.PORTALSERVLETREQUEST );

    return portalServletRequest.getSession( true );
  }

  /**
   * Get named value from the request. Prefer values set in the
   * portlet request to those set in the portal request.
   *
   * @param pRenderRequest the portlet request
   * @param pValueName the name of the request parameter
   * @return the corresponding request value or null
   */
  public static String getPortalRequestValue( RenderRequest pRenderRequest,
    String pValueName )
  {
    // Check the portal request first, as this should override the portlet request
      PortalServletRequest portalServletRequest =
        (PortalServletRequest) pRenderRequest.getAttribute( Attribute.PORTALSERVLETREQUEST );
      String value = portalServletRequest.getParameter( pValueName );

    // If not there, check the portlet request
    if ( value == null ) {
      value = pRenderRequest.getParameter( pValueName );
    }

    return value;
  }

  /**
   * Get named value from request. The portlet request takes
   * precedence over the portal request, which in turn takes
   * precendence over the session's value
   *
   * @param pRenderRequest the portlet render request
   * @param pValueName the name of the parameter or session-scoped
   * value
   * @return the corresponding value or null
   */
  public static String getSessionOrPortalRequestValue( RenderRequest pRequest,
    String pValueName )
  {
    String value = getPortalRequestValue( pRequest, pValueName );

    if ( value == null )
      value = (String) getSessionFromRequest( pRequest ).getAttribute( pValueName );

    return value;
  }

  /**
   * Get the value of the specified parameter from the portlet or
   * portal request and, if that value is not null, update the value
   * of the same name in the session. Return resulting session value.
   *
   * @param pRenderRequest the Portlet render request
   * @param pValueName the name of the request parameter
   * @return the named value or null
   */
  public static String updateSessionValueFromRequest( RenderRequest pRenderRequest,
    String pValueName )
  {
    // Get the named value from either the portlet or portal request
    String value = getPortalRequestValue( pRenderRequest, pValueName );

    HttpSession session = getSessionFromRequest( pRenderRequest );

    if ( value != null ) {
      // If it's not null, update that value in the session
      session.setAttribute( pValueName, value );
    }
    else {
      // otherwise, get value from session to return it
      value = (String) session.getAttribute( pValueName );
    }

    return value;
  }
  
  
  /**
   * Get the value of the specified parameter from the portlet or
   * portal request and, if that value is not null, update the value
   * of the same name in the ATGSession. Return resulting ATGSession 
   * value.
   *
   * @param pRenderRequest the Portlet render request
   * @param pValueName the name of the request parameter
   * @return the named value or null
   */
  public static String updateATGSessionValueFromRequest ( RenderRequest pRenderRequest,
    String pValueName )
  {
    // Get the named value from either the portlet or portal request
    String value = getPortalRequestValue( pRenderRequest, pValueName );

    DynamoHttpServletRequest dynRequest = ServletUtil.getCurrentRequest();
    ATGSession session = (ATGSession)dynRequest.resolveName(ATG_SESSION);

    if ( value != null ) 
    {
      session.setAttribute( pValueName, value );
    }
    else
    {
      value = (String) session.getAttribute( pValueName );
    }
    
    return value;
  }


  /**
   * Copy an action request parameter to an action response if the
   * named parameter is not null.
   *
   * @param pRequest the request
   * @param pResponse the response
   * @param pParamName name of parameter to copy
   * @return the parameter value
   */
  public static String copyActionParameter( ActionRequest pRequest,
    ActionResponse pResponse, String pParamName )
  {
    String param = pRequest.getParameter( pParamName );

    if ( ! StringUtils.isEmpty(param) )
      pResponse.setRenderParameter( pParamName, param );

    return param;
  }
}
