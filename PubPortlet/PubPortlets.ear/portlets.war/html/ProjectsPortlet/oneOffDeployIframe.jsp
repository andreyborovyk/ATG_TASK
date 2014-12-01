<%@ page import="javax.portlet.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui" %>

<!-- Begin oneOffDeployIframe.jsp -->

<dspel:page>

<dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>

<%-- if not logged in (session expired) close window and refresh parent --%>
<c:if test="${profile.transient}">
  <script language="JavaScript" type="text/javascript">
    parent.showIframe('actionOneOffDeploy');
    parent.location=parent.location;
   </script>
</c:if>

<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>

<style>
   @import url("<c:url value='/templates/style/css/style.jsp' context="${initParam['atg.bizui.ContextPath']}"/>");
</style>


</head>

<body class="actionContent">
  <div id="confirmHeader">
    <h2><fmt:message key="confirmation-header" bundle="${projectsBundle}"/></h2>
  </div>

  <c:set var="deploymentFormHandlerName" value="/atg/epub/deployment/DeploymentFormHandler"/>
  <dspel:importbean var="deploymentFormHandler" bean="${deploymentFormHandlerName}"/>

 <c:set var="formErrors" value="${false}"/>
 <c:if test="${!empty deploymentFormHandler.formExceptions}">
    <c:set var="formErrors" value="${true}"/>
 </c:if>

<%-- Get project and target info --%>
<c:set var="projectId" value="${param.projectId}"/>
<c:set var="targetId"  value="${param.targetId}"/>
<pws:getProject var="project" projectId="${projectId}"/>
<pws:getTarget  var="oneOffTarget" targetId="${targetId}"/>

<c:choose>
  <c:when test="${ (param.formSubmitted != null) && formErrors}">
    <div id="actionContent">
       <c:forEach var="outcomeException" items="${deploymentFormHandler.formExceptions}">
          <c:set var="exMessage" value="${outcomeException.message}"/>
          <c:if test="${exMessage eq null}">
            <c:set var="exMessage" value="${outcomeException}"/>
          </c:if>
          <p><span class="error"><c:out value="${exMessage}"/></span></p>
       </c:forEach>
      <br />
    </div>

    <div class="actionOK">
      <table border="0" cellpadding="0">
        <tr>
          <td width="100%">&nbsp;</td>
          <td class="buttonImage">
            <a href="javascript:parent.showIframe('actionOneOffDeploy')" class="mainContentButton close" onmouseover="status='';return true;"><fmt:message key="close-button" bundle="${projectsBundle}"/></a>
          </td>
          <td class="blankSpace"></td>
        </tr>
      </table>
    </div>
  </c:when> 
  
  <c:otherwise>
    <div id="actionContent">
      <c:if test="${!formErrors}">
        <h3>
          <fmt:message key="one-off-deploy-confirmation" bundle="${projectsBundle}">
            <fmt:param value="${project.displayName}"/>
            <fmt:param value="${oneOffTarget.name}"/>
          </fmt:message>
        </h3>
        <br/>
      </c:if>

      <dspel:form name="actionFormName" id="actionFormName"  formid="actionFormName"  action="#" method="post">
        <input type="hidden" name="formSubmitted" value="true" />
        <input type="hidden" name="projectId" value='<c:out value="${projectId}"/>'/>
        <input type="hidden" name="targetId"  value='<c:out value="${targetId}"/>'/>
        <dspel:input type="hidden" bean="${deploymentFormHandlerName}.deployProjectIDs" value="${projectId}"/>
        <dspel:input type="hidden" bean="${deploymentFormHandlerName}.useNowForTime" value="true"/>
        <dspel:input type="hidden" bean="${deploymentFormHandlerName}.forceFull" value="false"/>
        <dspel:input type="hidden" bean="${deploymentFormHandlerName}.deployTargetId" value="${targetId}"/>
        <dspel:input type="hidden" bean="${deploymentFormHandlerName}.scheduleDeployment" value="1"/>
      </dspel:form>
    </div>

    <div class="actionOK">
      <table border="0" cellpadding="0">
        <tr>
          <td width="100%">&nbsp;</td>
          <td class="buttonImage">
            <a id="okActionButton" href="javascript:document.actionFormName.submit();parent.showIframe('actionOneOffDeploy');" class="mainContentButton go" onmouseover="status='';return true;"><fmt:message key="ok-button" bundle="${projectsBundle}"/></a>
          </td>

          <td class="buttonImage">
            <a href="javascript:parent.showIframe('actionOneOffDeploy')" class="mainContentButton cancel" onmouseover="status='';return true;"><fmt:message key="cancel-button" bundle="${projectsBundle}"/></a>
          </td>
          <td class="blankSpace"></td>
        </tr>
      </table>
    </div>

  </c:otherwise>
</c:choose>

</body>
</html>

</dspel:page>
<!-- End oneOffDeployIframe.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/oneOffDeployIframe.jsp#2 $$Change: 651448 $--%>
