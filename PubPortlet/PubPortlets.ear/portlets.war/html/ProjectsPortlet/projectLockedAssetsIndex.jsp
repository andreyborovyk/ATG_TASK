<%--
  Comment goes here

  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectLockedAssetsIndex.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ page import="javax.portlet.*" %>
<%@ page import="atg.portal.framework.*" %>
<%@ page import="atg.portal.servlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<!-- Begin ProjectsPortlet's projectLockedAssetsIndex.jsp -->
<dspel:page>
<portlet:defineObjects/>
<%@ include file="projectConstants.jspf" %>

<%-- Get the current project id --%>
<pws:getCurrentProject var="projectContext"/>
<c:set var="project" value="${projectContext.project}" scope="request"/>
<c:if test="${project ne null}">
<c:set var="currentProjectId" value="${project.id}" scope="request"/>
</c:if>

<%-- Set the selected tablet into the session for this project --%>
<c:if test="${param.lockedAssetTablet != null}">
  <c:set var="lockedAssetTablet" value="${param.lockedAssetTablet}" scope="request"/>
  <%pageContext.setAttribute(pageContext.findAttribute("currentProjectId")+"lockedAssetTablet", request.getParameter("lockedAssetTablet"), pageContext.SESSION_SCOPE);%>
</c:if>
<c:if test="${param.lockedAssetTablet == null}">
  <%
   if(pageContext.findAttribute(pageContext.findAttribute("currentProjectId")+"lockedAssetTablet") != null)
     pageContext.setAttribute("lockedAssetTablet", pageContext.findAttribute(pageContext.findAttribute("currentProjectId")+"lockedAssetTablet"), pageContext.REQUEST_SCOPE);
   %>
</c:if>

<%-- Include the appropriate tablet --%>
<c:choose>
  <c:when test="${lockedAssetTablet == 'projects'}">
    <dspel:include page="projectBlockingProjects.jsp" />
  </c:when>
  <c:when test="${lockedAssetTablet == 'assets'}">
    <dspel:include page="projectLockedAssets.jsp" />
  </c:when>
  <c:otherwise>
    <dspel:include page="projectBlockingProjects.jsp" />
  </c:otherwise>
</c:choose>

</dspel:page>
<!-- End ProjectsPortlet's projectLockedAssetsIndex.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectLockedAssetsIndex.jsp#2 $$Change: 651448 $--%>
