<%@ page import="java.io.*,java.util.*,atg.portal.servlet.*,atg.portal.framework.*,java.util.*" errorPage="/error.jsp"%>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %>

<dspel:page>
<%
  PortalServletResponse portalServletResponse = 
     (PortalServletResponse)request.getAttribute(Attribute.PORTALSERVLETRESPONSE);
  PortalServletRequest portalServletRequest = 
     (PortalServletRequest)request.getAttribute(Attribute.PORTALSERVLETREQUEST);
%>
<c:set var="PORTALSERVLETREQUEST"><%= Attribute.PORTALSERVLETREQUEST %></c:set>
<c:set var="PORTALSERVLETRESPONSE"><%= Attribute.PORTALSERVLETRESPONSE %></c:set>
<c:set var="portalServletRequest"     value="${requestScope[PORTALSERVLETREQUEST]}"/>
<c:set var="portalServletResponse"    value="${requestScope[PORTALSERVLETRESPONSE]}"/>
<c:set var="page"                     value="${portalServletRequest.page}"/>
<c:set var="regionMap"                value="${page.regions}"/>
<c:set var="community"                value="${portalServletRequest.community}"/>
<c:set var="gearTextColor"            value="#${page.colorPalette.gearTextColor}"/>
<c:set var="gearBackgroundColor"      value="#${page.colorPalette.gearBackgroundColor}"/>


<%-- this bean used to get default process portlet ID --%>
<dspel:importbean bean="/atg/bizui/portlet/PortletConfiguration" var="bizuiPortletConfig"/>

<pws:getCurrentContext var="projectContext"/>
<c:set var="gearToRender"   value="${bizuiPortletConfig.defaultProcessPortletID}"/>
<c:choose>
  <c:when test='${param.workflowDefName != null && param.workflowDefName != ""}'>
    <%-- Do nothing here since it will render the defaultProjectPortlet. This logic is here for new project creation --%>
  </c:when>

  <c:when test='${param.projectView == "0"}'>
    <%-- Do nothing here since it will render the defaultProjectPortlet. This logic is here for browse projects --%>
  </c:when>

  <c:when test='${projectContext.process != null}'>
   <c:if test="${projectContext.process.status ne 'Completed'}">
    <%
      atg.epub.pws.taglib.GetCurrentProjectTag.Results context = (atg.epub.pws.taglib.GetCurrentProjectTag.Results) pageContext.findAttribute("projectContext");
      atg.epub.project.Process process = context.getProcess();
      if (process != null && process.getWorkflow() != null) {
        pageContext.setAttribute("workflowName", (String) process.getWorkflow().getPropertyValue("processName"));
    %>
    <biz:getRenderPortletId var="gearToRender" workflowName="${workflowName}"/>
    <%
      }
    %>
   </c:if>
   <c:if test="${projectContext.process.status eq 'Completed'}">
    <%
      atg.epub.pws.taglib.GetCurrentProjectTag.Results context = (atg.epub.pws.taglib.GetCurrentProjectTag.Results) pageContext.findAttribute("projectContext");
      atg.epub.project.Process process = context.getProcess();
      if (process != null) {
        if (process.getStatus() == atg.epub.project.ProcessEnumStatus.getCompleted()) {
          if ((process.getProcessData() != null) && process.getProcessData().getItemDescriptor().getItemDescriptorName().equals("communicationCampaignData")) {
            // this is probably a campaign type workflow
            // get the portlet id for campaign
    %>
          <c:set var="gearToRender" value='${bizuiPortletConfig.workflowPortletMap["campaign"]}'/>
    <%
          }
        }
        else if (process.getWorkflow() != null) {
          pageContext.setAttribute("workflowName", (String) process.getWorkflow().getPropertyValue("processName"));
    %>
          <biz:getRenderPortletId var="gearToRender" workflowName="${workflowName}"/>
    <%
        }
      }
    %>
   </c:if>
  </c:when>
</c:choose>


<div id="mainContent">

<c:set var="gears" value="${regionMap['100_Middle'].gears}"/>

<c:forEach var="gear" items="${gears}">
  <c:if test="${gear.id == gearToRender}">
    <%
      pageContext.setAttribute("gearContextType",GearContext.class);
      pageContext.setAttribute("gearMode",GearMode.CONTENT);
    %>
    <paf:context var="gearContext" type="${gearContextType}"/>
    <c:set target="${gearContext}" property="gear" value="${gear}"/>
    <c:set target="${gearContext}" property="gearMode" value="${gearMode}"/>
    <paf:include context="${gearContext}"/>
  </c:if>
</c:forEach>

</div>

</dspel:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/templates/layout/html/process100.jsp#2 $$Change: 651448 $--%>
