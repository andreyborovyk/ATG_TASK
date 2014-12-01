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

package atg.epub.portlet.project;

import atg.portal.servlet.Attribute;
import atg.portal.servlet.PortalServletRequest;

import atg.core.util.StringUtils;
import atg.epub.portlet.util.Util;
import atg.epub.project.Project;
import atg.epub.project.ProjectConstants;
import atg.epub.project.ProjectHome;
import atg.epub.servlet.VersioningLayerTools;
import atg.portlet.DispatchPortlet;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import atg.web.ATGSession;

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
 * A portlet class for the publishing ProjectPortlet that includes the appropriate
 * jsp page based on a request param.
 *
 * @author Jeff Banister
 * @version $Id: //product/PubPortlet/version/10.0.3/classes.jar/atg/epub/portlet/project/ProjectPortlet.java#2 $
 */

public
class ProjectPortlet
  extends DispatchPortlet
{

  //-------------------------------------
  /** Class version string */

  public static String CLASS_VERSION = "$Id: //product/PubPortlet/version/10.0.3/classes.jar/atg/epub/portlet/project/ProjectPortlet.java#2 $$Change: 651448 $";


  //-------------------------------------
  // Constants
  //-------------------------------------
  // These must be kept in sync with projectConstants.jspf in portlets.war
  public static final int LIST_VIEW=0;
  public static final int PROJECT_DETAIL_VIEW=1;
  public static final int PROCESS_PROPERTIES_VIEW=2;
  public static final int ASSETS_VIEW=3;
  public static final int CREATE_VIEW=4;
  public static final int TASKS_VIEW=5;
  public static final int NOTES_VIEW=6;
  public static final int ASSET_EDIT_VIEW=7;
  public static final int ASSET_HISTORY_VIEW=8;
  public static final int ASSET_NOTES_VIEW=9;
  public static final int ACTION_NOTE_IFRAME=10;
  public static final int ASSET_DIFF_VIEW=11;
  public static final int ASSET_CONFLICT_VIEW=12;
  public static final int NEW_ASSET_VIEW=13;
  public static final int PROJECT_TASK_DETAIL_VIEW=14;
  public static final int PROCESS_TASK_DETAIL_VIEW=15;
  public static final int PROCESS_DETAIL_VIEW=16;
  public static final int PROCESS_TASKS_VIEW=17;
  public static final int PROCESS_NOTES_VIEW=18;
  public static final int UNSUPPLIED_ASSETS_VIEW=19;
  public static final int LOCKED_ASSETS_VIEW=20;
  public static final int CREATE_PROCESS_FORM=99;

  public static final String LIST_PAGE="/html/ProjectsPortlet/projectBrowse.jsp";
  public static final String PROJECT_DETAIL_PAGE="/html/ProjectsPortlet/projectDetail.jsp";
  public static final String ASSET_DETAIL_PAGE="/html/ProjectsPortlet/projectAssetDetail.jsp";
  public static final String CREATE_PAGE="/html/ProjectsPortlet/createProcessStep1.jsp";
  public static final String CREATE_PAGE_STEP_2="/html/ProjectsPortlet/createProcessStep2.jsp";
  public static final String ACTION_NOTE_PAGE="/html/ProjectsPortlet/actionNote.jsp";
  public static final String ASSET_CONFLICT_PAGE="/html/ProjectsPortlet/assetConflict.jsp";
  public static final String NEW_ASSET_PAGE="/html/ProjectsPortlet/createAsset.jsp";
  public static final String TASK_DETAIL_PAGE="/html/ProjectsPortlet/taskDetail.jsp";

  private static final String LAST_PROJECTID_SESSION_ATTR = "LAST_PUBLISHING_PROJECT_ID";


  public static final String COPY_PARAMS = "copyParams";
  public static final String ADDITIONAL_COPY_PARAMS = "addCopyParams";  
  
  private static final String ATG_SESSION = "/atg/web/ATGSession";
  
  //-------------------------------------
  // Member Variables
  //-------------------------------------

  private static final ProjectHome sProjectHome = ProjectConstants.getPersistentHomes().getProjectHome();

  //-------------------------------------
  /**
   * Returns the persistent ProjectHome object
  */
  public ProjectHome getProjectHome() {
    return sProjectHome;
  }

  //-------------------------------------
  // Constructors
  //-------------------------------------

  /**
   * Constructs an instanceof ProjectsPortlet
   */
  public ProjectPortlet() {
  }

  /**
   * Things to do during portlet action requests
   *
   */
  public void handleProcessAction( ActionRequest pActionRequest,
    ActionResponse pActionResponse ) throws PortletException, IOException
  {
    if (isLoggingDebug()) {
      logDebug("" + this + ".handleProcessAction()");
    }
    
    // These parameters need to be copied from the action request to
    // the action response so they aren't lost (unset) by portals.
    String param = pActionRequest.getParameter( COPY_PARAMS );
    if (isLoggingDebug()) {
      logDebug(COPY_PARAMS + "= " + param);
    }
    
    if ( "true".equals(param) ) {
      Util.copyActionParameter( pActionRequest, pActionResponse, "projectView" );
      Util.copyActionParameter( pActionRequest, pActionResponse, "taskId" );

      String strAddParams =
        pActionRequest.getParameter( ADDITIONAL_COPY_PARAMS );      

      if (isLoggingDebug()) {
        logDebug(ADDITIONAL_COPY_PARAMS + "= " + strAddParams);
      }

      if (!StringUtils.isEmpty(strAddParams)) {
        String[] rgstrParams = StringUtils.splitStringAtCharacter(
          strAddParams, ',');
        for (int i = 0; i < rgstrParams.length; i++) {
          Util.copyActionParameter( pActionRequest, pActionResponse,
                                    rgstrParams[i].trim());
        }
      }
    }
  }

  /**
   * Called by the portlet service to indicate that a portlet should
   * handle a render request. Derived classes should override
   * this method to handle the servicing of a render request.
   * @param pRenderRequest the render request
   * @param pRenderResponse the render response
   * @exception PortletException if the portlet has problems fulfilling the
   * rendering request
   * @exception UnavailableException if the portlet is unavailable to perform
   * render at this time
   * @exception PortletSecurityException if the portlet cannot fullfill this
   * request because of security reasons
   * @exception java.io.IOException if the streaming causes an I/O problem
   *
   *  This method uses a request parameter (projectView) to override the default
   *  page for the portlet.
   */
  public void handleRender(RenderRequest pRenderRequest,
      RenderResponse pRenderResponse)
  throws PortletException, IOException {
    
    PortletContext portletContext = getPortletContext();
    
    // default page for portlet
    String path = getPath(pRenderRequest,pRenderResponse);
    String includePage=path;
    
    // get the portalServletRequest so we can get at non-portlet parameters
    PortalServletRequest portalServletRequest =
      (PortalServletRequest)pRenderRequest.getAttribute(Attribute.PORTALSERVLETREQUEST);
    
    // get project view and Id parameters from session to use as default
    DynamoHttpServletRequest dynRequest = ServletUtil.getCurrentRequest();
    VersioningLayerTools vlt = Util.getVersioningLayerTools();

    // Override the portal project id if passed in non-portlet param
    String overrideProjectId = dynRequest.getQueryParameter("projectId");
    if (overrideProjectId!=null && !overrideProjectId.equals("")) {
      try {
        vlt.setProjectInSession( dynRequest, overrideProjectId );

      } catch (ServletException se) {
        if ( isLoggingWarning() )
          logWarning( "Error in attempting to override project in session");
      }
    }

    // Override the project view if passed in non-portlet param
    String overrideProjectView = dynRequest.getQueryParameter("projectView");
    if (overrideProjectView!=null && !overrideProjectView.equals("")) {
	getATGSession(dynRequest).setAttribute("projectView", overrideProjectView);
    }
    
    // Get the project from the session
    Project project = vlt.getProjectInSession( dynRequest );
    if ( isLoggingDebug() ) {
      if ( project != null ) 
        logDebug( "project in session is: " + project.getId() );
      else	
        logDebug( "project in session is null ");
    }
    if ( project == null ) {
      // If there's no project in the session, see if there's a
      // LAST_PUBLISHING_PROJECT_ID attribute in the session
      String lastProjectId = vlt.getPrevProjectInSession(dynRequest);
      if ( isLoggingDebug() )
        logDebug( "last projectId in session is: " + lastProjectId );
      if ( ! StringUtils.isEmpty(lastProjectId) ) {
        // The LAST_PUBLISHING_PROJECT_ID exists, use it to set the
        // current project
        dynRequest.setParameter( "project", lastProjectId );
        try {
          vlt.setProjectContext( dynRequest, dynRequest.getResponse() );
        }
        catch ( ServletException se ) {
          throw new PortletException( "Error setting project context", se );
        }
        project = vlt.getProjectInSession( dynRequest );
        if ( isLoggingDebug() )
          logDebug( "new project is: " + project );
      }
    }
    
    if ( project != null ) {
      // Set 'lastProject' session attribute in case 'project' is
      // cleared by another portlet
      vlt.setPrevProjectInSession(dynRequest, project.getId());
    }
    else {
      // Still no project, remove last project ID attribute
      vlt.clearPrevProjectInSession(dynRequest);
    }
    
    
    String projectView = (String)getATGSession(dynRequest).getAttribute("projectView");
    
    // Check the portlet request then the http (portal) request for projectView attribute
    String requestProjectView = Util.getPortalRequestValue(pRenderRequest,"projectView");
    if ( requestProjectView!=null ) {
      projectView = requestProjectView;
      getATGSession(dynRequest).setAttribute("projectView", requestProjectView);
    } 
    
    pRenderRequest.setAttribute("projectView", projectView);
    
    // Check the portlet request then the http (portal) request for projectView attribute
    String requestTaskId = Util.getPortalRequestValue(pRenderRequest,"taskId");
    if ( requestTaskId!=null ) {
      getATGSession(dynRequest).setAttribute("processTaskId", requestTaskId);
    } 
    
    // check if current project is set (and valid) - if not, then we can't display a detail view,
    // so set the view to the list page, unless the view is CREATE_VIEW, which is the only
    // other view that is valid without a current project.
    
    if (projectView!=null) {
      
      int viewMode=Integer.parseInt(projectView);
      if (viewMode!=CREATE_VIEW && viewMode!=CREATE_PROCESS_FORM && viewMode!=LIST_VIEW) {
        Project currentProject=vlt.getProjectInSession(dynRequest);
        if (currentProject==null) {
          viewMode=LIST_VIEW;
          if ( isLoggingDebug() )
            logDebug( "no current project - setting view to process List view");
        }
        else {
          // try to lookup project to determine if it still exists
          try {
            Project testProject = getProjectHome().findById(currentProject.getId());
          }
          catch (Exception e) {
            // project not valid - reset view
            viewMode=LIST_VIEW;
            if ( isLoggingDebug() )
              logDebug( "current project not valid - setting view to process List view. Project Id: " + currentProject.getId());
          }
        }
      }
      
      switch (viewMode) {
        case PROJECT_DETAIL_VIEW:
        case ASSETS_VIEW:
        case TASKS_VIEW:
        case NOTES_VIEW:
        case PROCESS_TASKS_VIEW:
        case PROCESS_PROPERTIES_VIEW:
        case PROCESS_TASK_DETAIL_VIEW:
        case PROJECT_TASK_DETAIL_VIEW:
        case UNSUPPLIED_ASSETS_VIEW:
        case LOCKED_ASSETS_VIEW:
          includePage=PROJECT_DETAIL_PAGE;
          break;
        case ASSET_EDIT_VIEW:
        case ASSET_HISTORY_VIEW:
        case ASSET_DIFF_VIEW:
        case ASSET_NOTES_VIEW:
          includePage=ASSET_DETAIL_PAGE;
          break;
        case CREATE_VIEW:
          includePage=CREATE_PAGE;
          break;
        case CREATE_PROCESS_FORM:
          includePage=CREATE_PAGE_STEP_2;
          break;
        case NEW_ASSET_VIEW:
          includePage=NEW_ASSET_PAGE;
          break;
        case ASSET_CONFLICT_VIEW:
          includePage=ASSET_CONFLICT_PAGE;
          break;
        default:
          includePage=LIST_PAGE;
        break;
      }
    }
    
    if(isLoggingDebug())
      logDebug("projectPortletPage = " + includePage);
    //set a preferred content type
    pRenderResponse.setContentType(pRenderRequest.getResponseContentType());
    
    //Dispatch
    if(portletContext != null && includePage != null) {
      PortletRequestDispatcher portletRequestDispatcher =
        portletContext.getRequestDispatcher(includePage);
      portletRequestDispatcher.include(pRenderRequest,pRenderResponse);
    }
  }
  
  public ATGSession getATGSession(DynamoHttpServletRequest request) {
    return (ATGSession)request.resolveName(ATG_SESSION);
  }
} // end of class
