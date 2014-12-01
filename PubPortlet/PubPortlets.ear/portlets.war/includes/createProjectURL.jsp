<!-- BEGIN FILE createProjectURL.jsp -->
<%@ page import="javax.portlet.*" %>
<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>
<%@ page import="atg.servlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>

<portlet:defineObjects/>

<%-- this bean used to get process portlet ID based on workflow type --%>
<dspel:importbean bean="/atg/bizui/portlet/PortletConfiguration" var="bizuiPortletConfig"/>

<%-- for project views --%>
<%@ include file="/html/ProjectsPortlet/projectConstants.jspf" %>
<%
     String inputProjectId = (ServletUtil.getDynamoRequest(request).getParameter("inputProjectId"));
     String inputTaskId = (ServletUtil.getDynamoRequest(request).getParameter("inputTaskId"));
     String outputAttributeName = (ServletUtil.getDynamoRequest(request).getParameter("outputAttributeName"));
     String inputWorkflowPath = (ServletUtil.getDynamoRequest(request).getParameter("workflowPath"));
     // Need this code to generate a link to the Projects/Process page
     // Note: this is specific to PAF, and is not JSR 168 compliant
     PortalServletResponse portalServletResponse =
         (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
     PortalServletRequest portalServletRequest =
         (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
     
     PortalContextImpl portalContext = (PortalContextImpl)pageContext.findAttribute("BASE_PROJECT_URI_PORTAL_CONTEXT");
     String baseURI = (String)pageContext.findAttribute("BASE_PROJECT_URI");     
     if(portalContext == null  && baseURI == null){
	String projectPageId = portletConfig.getInitParameter("projectPageId");
    if (projectPageId == null || projectPageId.equals("")) projectPageId = "100004";
	String projectCommunityId = portletConfig.getInitParameter("projectCommunityId");
    if (projectCommunityId == null || projectCommunityId.equals("")) projectCommunityId = "100001";
	portalContext = new PortalContextImpl(portalServletRequest);
	Page projectPage=portalServletRequest.getPortal().getPageById(projectPageId);
  
	Community projectCommunity=portalServletRequest.getPortal().getCommunityById(projectCommunityId);
	portalContext.setPage(projectPage);
	portalContext.setCommunity(projectCommunity);
        //store the context in the request scope so that we don't create a new context on every include of this page.	     
	pageContext.setAttribute("BASE_PROJECT_URI_PORTAL_CONTEXT", portalContext, pageContext.REQUEST_SCOPE);	     
        baseURI=portalServletRequest.getPortalContextPath() + projectPage.getPageURI();
        pageContext.setAttribute("BASE_PROJECT_URI", baseURI, pageContext.REQUEST_SCOPE);	     
     }


    String projectParams="?projectView=" + (String)pageContext.getAttribute("PROJECT_DETAIL_VIEW") + "&project=";
    // if task ID is given, use task detail view
    if (inputTaskId != null) {
      projectParams="?projectView=" + (String)pageContext.getAttribute("PROJECT_TASK_DETAIL_VIEW") + "&project="; 
    }
    String baseProjectURI=baseURI+projectParams;
    String projectPageURI = baseProjectURI + inputProjectId; 

    if (inputTaskId != null) {
        projectPageURI = projectPageURI + "&taskId=" + inputTaskId;
    }
    %>

	  
    <%-- Added this to get portlet ID based on process "type" - currently a hack based
      on the workflow path --%>
    <c:set var="workflowPath"><dspel:valueof param="workflowPath"/></c:set>
    <%@ include file="/html/ProjectsPortlet/getWorkflowType.jspf" %>
    <c:set var="portletID" value='${bizuiPortletConfig.workflowPortletMap[workflowType]}'/>

    <%
      
    String portletParams="&processPortlet=" + (String)pageContext.getAttribute("portletID");
    String portletURI = projectPageURI + portletParams;

    String encodedURL=portalServletResponse.encodePortalURL(portletURI, portalContext);
    pageContext.setAttribute(outputAttributeName, encodedURL, pageContext.REQUEST_SCOPE);
   %>


</dspel:page>
<!-- END FILE createProjectURL.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/includes/createProjectURL.jsp#2 $$Change: 651448 $--%>
