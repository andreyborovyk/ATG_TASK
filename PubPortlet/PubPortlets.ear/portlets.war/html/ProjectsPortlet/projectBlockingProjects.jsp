<%--
  All blocking projects tab. Displays a table of all projects that hold asset 
  locks on any of the assets in the current project.
  

  @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectBlockingProjects.jsp#2 $$Change: 651448 $
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

<!-- Begin ProjectsPortlet's projectBlockingProjects.jsp -->
<dspel:page>
<portlet:defineObjects/>
<%@ include file="projectConstants.jspf" %>

<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<c:url var="excelDownloadURL" context="/PubPortlets" value="/excel">
  <c:param name="projectId" value="${currentProjectId}"/>
</c:url>

<div class="contentActionsAgents">
  <table width="100%" cellspacing="0" cellpadding="0" border="0">
    <tr>
      <td class="blankSpace"> </td>
      <td><h2><fmt:message key="active-projects-containing-locked-assets" bundle="${projectsBundle}"/></h2>
      <br><br>
      <h2> 
        <fmt:message var="downloadExcelTxt" key="download-excel-all-blocking-projects" bundle="${projectsBundle}"/>
        <c:choose>
          <c:when test="${project.checkedIn}">
            <c:out value="${downloadExcelTxt}"/>
          </c:when>
          <c:otherwise>
            <dspel:a href="${excelDownloadURL}"><c:out value="${downloadExcelTxt}"/></dspel:a>
          </c:otherwise>
        </c:choose>
      </h2>           
      </td>
      <td class="alertMessage"></td>
    </tr>
  </table>
</div>

<dspel:droplet name="/atg/epub/project/LockingProjectsDroplet">
  <dspel:param name="projectId" value="${currentProjectId}"/>
  
  <dspel:oparam name="outputStart">
    <table cellspacing="0" cellpadding="0" border="0" class="dataTable">
      <tr>
        <th class="leftAligned"><span class="tableHeader"><fmt:message key="projects-column-header" bundle="${projectsBundle}"/></span></th>
        <th class="centerAligned"><span class="tableHeader"><fmt:message key="current-target-column-header" bundle="${projectsBundle}"/></span></th>
        <th class="centerAligned"><span class="tableHeader"><fmt:message key="scheduled-deployment-column-header" bundle="${projectsBundle}"/></span></th>
        <th class="centerAligned"><span class="tableHeader"><fmt:message key="scheduled-target-column-header" bundle="${projectsBundle}"/></span></th>
        <th class="centerAligned"><span class="tableHeader"><fmt:message key="locked-assets-column-header" bundle="${projectsBundle}"/></span></th>
      </tr>
  </dspel:oparam>

  <dspel:oparam name="empty">
    <tr>
      <td colspan="7" class="centerAligned error">
        <fmt:message key="no-projects-containing-asset-locks" bundle="${projectsBundle}"/>
      </td>
    </tr>
  </dspel:oparam>

  <dspel:oparam name="output">
    <dspel:getvalueof var="idx" param="index"/>
    <dspel:getvalueof var="pendingDeployments" param="element.pendingDeployments"/>
    <dspel:getvalueof var="numberOfDeployments" param="element.numberOfDeployments"/>
    
    <c:choose>
      <%-- No pending deployments --%>
      <c:when test="${empty pendingDeployments}">
        <%-- Row highlighting --%>
        <c:choose>
          <c:when test="${idx % 2 != 0}">
            <c:set var="rowHighlight" value='class="alternateRowHighlight"'/>
          </c:when>
          <c:otherwise>
            <c:set var="rowHighlight" value=''/>
          </c:otherwise>
        </c:choose>
        <tr <c:out value="${rowHighlight}" escapeXml="false"/>>

        <%-- Projects --%>
        <td class="leftAligned">
          <span class="tableInfo">
            <dspel:getvalueof var="createURLForProjectId" param="element.project.id"/>
            <%@ include file="createProcessURL.jspf" %>
            <a onmouseover="status='';return true;" href='<c:out value="${processURL}"/>'>
              <img width="16" height="16" border="0" style="margin-right: 6px; vertical-align: middle;" alt="Process" src="/PubPortlets/html/ProjectsPortlet/images/icon_process.gif"/>
              <dspel:valueof param="element.project.displayName"/>
            </a>
            &nbsp;
          </span>
        </td>
        <%-- Current Target --%>
        <td class="leftAligned">
          <span class="tableInfo">
            <dspel:getvalueof var="deployedTargets" param="element.deployedTargets"/>
            <c:forEach var="deployedTarget" items="${deployedTargets}" varStatus="tarStat">
              <c:out value="${deployedTarget.name}"/><c:if test="${!tarStat.last}">,&nbsp;</c:if>
            </c:forEach>
            &nbsp;
          </span>
        </td>
        <%-- Scheduled Deployment --%>
        <td class="leftAligned">
          <span class="tableInfo">&nbsp;</span>
        </td>
        <%-- Scheduled Target --%>
        <td class="leftAligned">
          <span class="tableInfo">&nbsp;</span>
        </td>
        <%-- Locked Assets Link --%>
        <td class="centerAligned">
          <span class="tableInfo">
            <dspel:getvalueof var="lockingProjectId" param="element.project.id"/>
            <portlet:renderURL var="lockedAssetTabletViewURL">
              <portlet:param name="projectView"       value='<%=(String)pageContext.getAttribute("LOCKED_ASSETS_VIEW")%>'/>
              <portlet:param name="lockingProjectId"  value='<%=(String)pageContext.getAttribute("lockingProjectId")%>'/>
              <portlet:param name="lockedAssetTablet" value='assets'/>
            </portlet:renderURL>
            <a href="<c:out value='${lockedAssetTabletViewURL}'/>"><fmt:message key="view-locked-assets" bundle="${projectsBundle}"/></a>
            &nbsp;
          </span>
        </td></tr>
      </c:when>

      <%-- Pending deployments exist --%>
      <c:otherwise>
        <c:forEach var="pendingDeployment" items="${pendingDeployments}" varStatus="depStat">
          <%-- Row highlighting --%>
          <c:choose>
            <c:when test="${idx % 2 != 0}">
              <c:set var="rowHighlight" value='class="alternateRowHighlight"'/>
            </c:when>
            <c:otherwise>
              <c:set var="rowHighlight" value=''/>
            </c:otherwise>
          </c:choose>
          <tr <c:out value="${rowHighlight}" escapeXml="false"/>>
          
          <%-- Projects --%>
          <c:if test="${numberOfDeployments > 1}">
            <c:set var='rowspan' value='valign=\"top\" rowspan=\"${numberOfDeployments}\"'/>
          </c:if>
          <c:if test="${depStat.first}">
            <td class="leftAligned" <c:out escapeXml="false" value='${rowspan}'/>>
              <span class="tableInfo">
                <dspel:getvalueof var="createURLForProjectId" param="element.project.id"/>
                <%@ include file="createProcessURL.jspf" %>
                <a onmouseover="status='';return true;" href='<c:out value="${processURL}"/>'>
                  <img width="16" height="16" border="0" style="margin-right: 6px; vertical-align: middle;" alt="Process" src="/PubPortlets/html/ProjectsPortlet/images/icon_process.gif"/>
                  <dspel:valueof param="element.project.displayName"/>
                </a>
                &nbsp;
              </span>
            </td>
          </c:if>
          <%-- Current Target --%>
          <c:if test="${depStat.first}">
            <td class="leftAligned" <c:out escapeXml="false" value='${rowspan}'/>>
              <span class="tableInfo">
                <dspel:getvalueof var="deployedTargets" param="element.deployedTargets"/>
                <c:forEach var="deployedTarget" items="${deployedTargets}" varStatus="tarStat">
                  <c:out value="${deployedTarget.name}"/><c:if test="${!tarStat.last}">,&nbsp;</c:if>
                </c:forEach>
                &nbsp;
              </span>
            </td>
          </c:if>
          <%-- Scheduled Deployment --%>
          <td class="leftAligned">
            <span class="tableInfo">
              <fmt:formatDate type="date" value="${pendingDeployment.deployTime.time}"/>&nbsp;<fmt:formatDate type="time" timeStyle="short" value="${pendingDeployment.deployTime.time}"/>
              <c:if test="${pendingDeployment.revertDeployment}"><span class="deploymentInfo">&nbsp;(<fmt:message key="revert-deployment" bundle="${projectsBundle}"/>)</span></c:if>
              &nbsp;
            </span>
          </td>
          <%-- Scheduled Target --%>
          <td class="leftAligned">
            <span class="tableInfo">
              <c:out value="${pendingDeployment.target.name}"/>
              &nbsp;
            </span>
          </td>
          <%-- Locked Assets Link --%>
          <c:if test="${depStat.first}">
            <td class="centerAligned" <c:out escapeXml="false" value='${rowspan}'/>>
              <span class="tableInfo">
                <dspel:getvalueof var="lockingProjectId" param="element.project.id"/>
                <portlet:renderURL var="lockedAssetTabletViewURL">
                  <portlet:param name="projectView"       value='<%=(String)pageContext.getAttribute("LOCKED_ASSETS_VIEW")%>'/>
                  <portlet:param name="lockingProjectId"  value='<%=(String)pageContext.getAttribute("lockingProjectId")%>'/>
                  <portlet:param name="lockedAssetTablet" value='assets'/>
                </portlet:renderURL>
                <a href="<c:out value='${lockedAssetTabletViewURL}'/>"><fmt:message key="view-locked-assets" bundle="${projectsBundle}"/></a>
                &nbsp;
              </span>
            </td>
          </c:if>
          </tr>
        </c:forEach>
      </c:otherwise>
    </c:choose>
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
<!-- End ProjectsPortlet's projectBlockingProjects.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectBlockingProjects.jsp#2 $$Change: 651448 $--%>
