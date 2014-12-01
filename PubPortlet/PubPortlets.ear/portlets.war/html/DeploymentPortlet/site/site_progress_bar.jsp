<!-- BEGIN FILE site_progress_bar.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>
<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>

<c:set var="deployId" value="${param.deployId}"/>
<c:set var="error"    value="${param.error}"/>

<c:set var="currentDeployment" value="${target.currentDeployment}"/>

<c:set var="isError" value="false"/>
<c:if test="${!empty currentDeployment}">
  <c:set var="isError" value="${currentDeployment.status.stateError}" />
</c:if>

<c:choose>
  <c:when test="${isError eq 'false'}">
    <c:set var="progressBarClass" value="bgBlue" />
  </c:when>
  <c:otherwise>
    <c:set var="progressBarClass" value="bgRed" />  
  </c:otherwise>
</c:choose>

<pws:getDeploymentPercentageComplete var="percent" deploymentId="${deployId}" />

<h2>
<c:if test="${error != 'yes'}">
  <fmt:message key="deployment-progress" bundle="${depBundle}"/>
</c:if>
</h2>
<table class="borderGray" style="width: 99%;" cellpadding="0" cellspacing="0">
  <tbody><tr>
    <td align="left">
      <table class="<c:out value='${progressBarClass}'/>" style="width: <c:out value='${percent}'/>%;" cellpadding="0" cellspacing="0">
        <tbody><tr>
          <td align="center">
            <strong><c:out value='${percent}'/>%</strong>
          </td>
        </tr>
      </tbody></table>
    </td>
  </tr></tbody>
</table>
<span class="tableInfo">
  <c:choose>
    <c:when test="${percent == '100'}">
      <fmt:message key="refresh-page" bundle="${depBundle}"/>
    </c:when>
    <c:when test="${!isError eq 'false' && !empty currentDeployment && empty param.norefresh}">
      <c:out value="${currentDeployment.status.userStateString}"/><br>
      <fmt:message key="refresh-page" bundle="${depBundle}"/>
    </c:when>
    <c:when test="${!isError eq 'false' && !empty currentDeployment}">
      <c:out value="${currentDeployment.status.userStateString}"/><br>
    </c:when>
    <c:when test="${!empty currentDeployment}">
      <c:out value="${currentDeployment.status.userStateString}"/><br>
    </c:when>
  </c:choose>
</span>

</dspel:page>
<!-- END FILE site_progress_bar.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/site_progress_bar.jsp#2 $$Change: 651448 $--%>
