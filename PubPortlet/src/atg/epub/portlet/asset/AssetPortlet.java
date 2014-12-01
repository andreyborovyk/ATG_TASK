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

package atg.epub.portlet.asset;

import atg.core.util.StringUtils;
import atg.epub.portlet.util.Util;
import atg.epub.servlet.VersioningLayerTools;
import atg.portlet.DispatchPortlet;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.PortletRequestDispatcher;
import javax.portlet.PortletSecurityException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.UnavailableException;
import javax.servlet.ServletException;

/**
 * A portlet class for the publishing AssetPortlet that includes the
 * appropriate jsp page based on a request param.
 *
 * @author Peter Eddy
 * @version $Id: //product/PubPortlet/version/10.0.3/classes.jar/atg/epub/portlet/asset/AssetPortlet.java#2 $
 */

public class AssetPortlet extends DispatchPortlet
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/PubPortlet/version/10.0.3/classes.jar/atg/epub/portlet/asset/AssetPortlet.java#2 $$Change: 651448 $";

  //

  // These must be kept in sync with projectConstants.jspf
  public static final int
    // ASSET_VIEW parameter options
    SEARCH_INITIAL = 0,         // Initial search or asset list view
    SEARCH_RESULTS = 1,         // Search results view
    ASSET_PROPERTIES = 2,       // Asset properties view
    ASSET_HISTORY = 3,          // Asset history view
    ASSET_NOTES = 4,            // Asset notes view
    ASSET_DIFF = 5;            // Asset diff view  
    
  private static final String
    // request parameters
    ASSET_VIEW = "assetView",   // Either SEARCH_VIEW or DETAIL_VIEW
    ASSET_URI = "assetURI",
    // Root directory for assets portlet pages
    ASSETS_PORTLET_ROOT = "/html/AssetsPortlet/",
    // The various assets portlet pages
    SEARCH_INITIAL_PAGE   = ASSETS_PORTLET_ROOT + "search.jsp", 
    SEARCH_RESULTS_PAGE   = ASSETS_PORTLET_ROOT + "searchResults.jsp", 
    ASSET_DETAIL_PAGE = ASSETS_PORTLET_ROOT + "detail.jsp";

  //
  // Constructors
  //

  /**
   * Constructs an instanceof ProjectsPortlet
   */
  public AssetPortlet() { setLoggingDebug(false); }

  //
  // Accessors of static data for EL (yuck!)
  //

  public int getSEARCH_INITIAL() { return SEARCH_INITIAL; }
  public int getSEARCH_RESULTS() { return SEARCH_RESULTS; }
  public int getASSET_PROPERTIES() { return ASSET_PROPERTIES; }
  public int getASSET_HISTORY() { return ASSET_HISTORY; }
  public int getASSET_NOTES() { return ASSET_NOTES; }
  public int getASSET_DIFF() { return ASSET_DIFF; }    

  /**
   * Unset the project context so we don't see assets in the current
   * project
   *
   */
  protected void unsetProjectContext( DynamoHttpServletRequest dynRequest )
  {
    VersioningLayerTools vlt = Util.getVersioningLayerTools();
    try {
      vlt.setProjectContext(null);
    }
    catch (ServletException e) {
      if (isLoggingWarning())
        logWarning("Unable to unset project context");      
    }
  }

  /**
   * Things to do during portlet action requests
   *
   */
  public void handleProcessAction( ActionRequest pActionRequest,
    ActionResponse pActionResponse ) throws PortletException, IOException
  {
    if ( isLoggingDebug() ) {
      logDebug( "handleProcessAction()");
      logDebug( "  assetView: " + pActionRequest.getParameter(ASSET_VIEW) );
      logDebug( "  assetURI: " + pActionRequest.getParameter(ASSET_URI) );
      logDebug( "  before unsetProjectContext" );
    }
    
    // Don't want process (project) in our session while handling
    // actions because this portlet should always be operating outside
    // of any process (project)
    DynamoHttpServletRequest dynRequest = ServletUtil.getCurrentRequest();
    unsetProjectContext( dynRequest );

    // These parameters need to be copied from the action request to
    // the action response so they aren't lost (unset) by portals.
    Util.copyActionParameter( pActionRequest, pActionResponse, ASSET_VIEW );
    Util.copyActionParameter( pActionRequest, pActionResponse, ASSET_URI );    
  }

  /**
   * Servicing render requests for this portlet
   *
   * This method uses a request parameter (assetView) to override the
   * default page for the portlet.
   *
   * @param pRenderRequest the render request 
   * @param pRenderResponse the render response
   * @exception PortletException if the portlet has problems fulfilling the
   * rendering request
   * @exception UnavailableException if the portlet is unavailable to perform
   * render at this time
   * @exception PortletSecurityException if the portlet cannot fullfill this
   * request because of security reasons
   * @exception java.io.IOException if the streaming causes an I/O problem
   */
  public void handleRender(RenderRequest pRenderRequest,
    RenderResponse pRenderResponse) throws PortletException, IOException
  {
    PortletContext portletContext = getPortletContext();
    DynamoHttpServletRequest dynRequest = ServletUtil.getCurrentRequest();

    // Don't want process (project) in our session while handling
    // actions because this portlet should always be operating outside
    // of any process (project)
    unsetProjectContext( dynRequest );

    // Don't want process (project) in our session while handling
    // actions because this portlet should always be operating outside
    // of any process (project)

    // Get asset view and (possibly) asset ID parameters from session
    // or request.
    String
      assetView = Util.updateATGSessionValueFromRequest( pRenderRequest, ASSET_VIEW ),
      assetURI  = Util.updateATGSessionValueFromRequest( pRenderRequest, ASSET_URI );

    if ( isLoggingDebug() ) {
      logDebug( "assetView: " + assetView );
      logDebug( "assetURI: " + assetURI );
    }

    int viewMode = SEARCH_INITIAL;

    if ( ! StringUtils.isEmpty(assetView) ) {
      try {
        viewMode = Integer.parseInt( assetView );
      }
      catch( NumberFormatException nfe ) {
        if ( isLoggingWarning() )
          logWarning( "invalid asset view parameter: " + assetView );
      }
    }
      
    String includePage = null;

    switch ( viewMode ) {
      case SEARCH_RESULTS:
        includePage = SEARCH_RESULTS_PAGE;
        break;
      case ASSET_PROPERTIES:
      case ASSET_HISTORY:
      case ASSET_NOTES:
      case ASSET_DIFF:        
        includePage = ASSET_DETAIL_PAGE;
        break;
      default:
        includePage = SEARCH_INITIAL_PAGE;
        break;
    }
    
    if ( isLoggingDebug() )
      logDebug( "assetPortletPage = " + includePage );

    // Set a preferred content type
    pRenderResponse.setContentType( pRenderRequest.getResponseContentType() );

    // Dispatch
    if ( isLoggingDebug() ) {
      logDebug( "dispatching page?: " +
        (portletContext!=null && includePage!=null) );
    }

    if ( portletContext != null && includePage != null ) {
      PortletRequestDispatcher portletRequestDispatcher =
        portletContext.getRequestDispatcher( includePage );
      portletRequestDispatcher.include( pRenderRequest, pRenderResponse );
    }
  }
}
