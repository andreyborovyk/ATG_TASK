<%--
  Locked assets tab. Displays a table of all the locked assets within a project
  that is blocking the current project from being deployed.


  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectLockedAssets.jsp#2 $$Change: 651448 $
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

<!-- Begin ProjectsPortlet's projectLockedAssets.jsp -->
<dspel:page>
<portlet:defineObjects/>
<%@ include file="projectConstants.jspf" %>

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<portlet:renderURL var="lockingProjectsTabletViewURL">
  <portlet:param name="projectView"       value='<%=(String)pageContext.getAttribute("LOCKED_ASSETS_VIEW")%>'/>
  <portlet:param name="lockedAssetTablet" value='projects'/>
</portlet:renderURL>

<%-- Set the selected locking project into the session for this project's tablet --%>
<c:if test="${param.lockingProjectId != null}">
  <c:set var="lockingProjectId" value="${param.lockingProjectId}"/>
  <%pageContext.setAttribute(pageContext.findAttribute("currentProjectId")+"lockingProjectView", request.getParameter("lockingProjectId"), pageContext.SESSION_SCOPE);%>
</c:if>
<c:if test="${param.lockingProjectId == null}">
  <%
   if(pageContext.findAttribute(pageContext.findAttribute("currentProjectId")+"lockingProjectView") != null)
     pageContext.setAttribute("lockingProjectId", pageContext.findAttribute(pageContext.findAttribute("currentProjectId")+"lockingProjectView"), pageContext.REQUEST_SCOPE);
   %>
</c:if>

<pws:getProject var="lockingProject" projectId="${lockingProjectId}"/>

<%-- Build a URL to link to the locking project --%>
<c:set var="createURLForProjectId" value="${lockingProjectId}"/>
<%@ include file="createProcessURL.jspf" %>

<div class="contentActionsAgents">
  <table width="100%" cellspacing="0" cellpadding="0" border="0">
    <tr>
      <td class="blankSpace"> </td>
      <td>
        <h2><fmt:message key="assets-locked-in-project" bundle="${projectsBundle}"/>: <a href='<c:out value="${processURL}"/>'><c:out value="${lockingProject.displayName}"/></a></h2>
        <br><br>
        <h2>&laquo;&nbsp;<a href="<c:out value='${lockingProjectsTabletViewURL}'/>"><fmt:message key="all-projects-tablet-link" bundle="${projectsBundle}"/></a></h2>
      </td>
      <td class="alertMessage"></td>
    </tr>
  </table>
</div>

<dspel:droplet name="/atg/epub/project/LockingAssetsDroplet">
  <dspel:param name="projectId" value="${currentProjectId}"/>
  <dspel:param name="lockingProjectId" value="${lockingProjectId}"/>

  <dspel:oparam name="outputStart">
    <table cellspacing="0" cellpadding="0" border="0" class="dataTable">
      <tr>
        <th class="leftAligned" width="100%"><span class="tableHeader"><fmt:message key="asset-name" bundle="${projectsBundle}"/></span></th>
        <th class="centerAligned"><span class="tableHeader"><fmt:message key="asset-type" bundle="${projectsBundle}"/></span></th>
        <th class="centerAligned"><span class="tableHeader"><fmt:message key="base-version" bundle="${projectsBundle}"/></span></th>
        <th class="centerAligned"><span class="tableHeader"><fmt:message key="latest-version" bundle="${projectsBundle}"/></span></th>
        <th class="centerAligned"><span class="tableHeader"><fmt:message key="asset-status-header" bundle="${projectsBundle}"/></span></th>
      </tr>
  </dspel:oparam>

  <dspel:oparam name="empty">
    <tr>
      <td colspan="7" class="centerAligned error">
        <dspel:valueof param="element"/>
      </td>
    </tr>
  </dspel:oparam>

  <dspel:oparam name="output">
    <dspel:getvalueof var="idx" param="index"/>
    <c:choose>
      <c:when test="${idx % 2 != 0}">
        <tr class="alternateRowHighlight">
      </c:when>
      <c:otherwise>
        <tr>
      </c:otherwise>
    </c:choose>
    <%-- Asset Name --%>
    <td class="leftAligned">
      <span class="tableInfo">
        <img width="14" height="14" border="0" style="margin-right: 6px; vertical-align: middle;" alt="Process" src="/PubPortlets/html/ProjectsPortlet/images/icon_lockedAsset.gif" /><dspel:valueof param="element.displayName"/>
      </span>
    </td>
    <%-- Type --%>
    <td class="centerAligned">
      <span class="tableInfo">
        <dspel:valueof param="element.type"/>
      </span>
    </td>
    <%-- Base Version --%>
    <td class="centerAligned">
      <span class="tableInfo">
        <dspel:valueof param="element.baseVersion"/>
      </span>
    </td>
    <%-- Current Version --%>
    <td class="centerAligned">
      <span class="tableInfo">
        <dspel:valueof param="element.currentVersion"/>
      </span>
    </td>
    <%-- Status --%>
    <td class="centerAligned">
      <span class="tableInfo">
        <dspel:getvalueof var="isDeleted" param="element.deleted"/>
        <dspel:getvalueof var="isAdded"   param="element.added"/>
        <c:choose>
          <c:when test="${isDeleted}">
            <fmt:message key="asset-status-deleted" bundle="${projectsBundle}"/>
          </c:when>
          <c:when test="${isAdded}">
            <fmt:message key="asset-status-created" bundle="${projectsBundle}"/>
          </c:when>
          <c:otherwise>
            <fmt:message key="asset-status-modified" bundle="${projectsBundle}"/>
          </c:otherwise>
        </c:choose>
      </span>
    </td></tr>
  </dspel:oparam>

  <dspel:oparam name="outputEnd">
    </table>
  </dspel:oparam>

  <dspel:oparam name="error">
    <tr>
      <td colspan="7" class="centerAligned error">
        <dspel:valueof param="element"/>
      </td>
    </tr>
  </dspel:oparam>
</dspel:droplet>

<div class="contentActions">
<table cellspacing="0" cellpadding="0" border="0">
    <tr>
      <td width="100%" class="paddingLeft"></td>
      <td class="blankSpace"> </td>
    </tr>
  </tbody>
</table>
</div>

</dspel:page>
<!-- End ProjectsPortlet's projectLockedAssets.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectLockedAssets.jsp#2 $$Change: 651448 $--%>
