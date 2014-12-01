<!-- BEGIN FILE site_detail.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>

<dspel:page>

<%
  atg.security.User user = atg.security.ThreadSecurityManager.currentUser();
%>

<style>
.borderGray {
  border: 1px solid #dadada;
  margin-bottom: 4px;
}
.bgBlue {
  background: #bdcfe7;
  color: #545454;
}
.bgRed {
  background: #f00;
  color: #FFFFFF;
}
</style>

<dspel:importbean scope="request" var="deploymentServer" bean="/atg/epub/DeploymentServer"/>

<fmt:setBundle var="depBundle" basename="atg.epub.portlet.DeploymentPortlet.Resources"/>
<portlet:defineObjects/>

<portlet:renderURL var="thisPageURL">
  <portlet:param name="atg_admin_menu_group" value="deployment"/>
  <portlet:param name="atg_admin_menu_1" value="overview"/>
  <portlet:param name="details_tabs" value="true"/>
  <portlet:param name="site_tab_name" value="details"/>
  <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
</portlet:renderURL>
    <portlet:renderURL var="agentsURL">
     <portlet:param name="atg_admin_menu_group" value="deployment"/>
     <portlet:param name="atg_admin_menu_1" value="overview"/>
     <portlet:param name="goto_details_tabs" value="true"/>
     <portlet:param name="site_tab_name" value="agents"/>
     <portlet:param name="target_id" value='<%=pageContext.findAttribute("TARGET_ID")+""%>'/>
    </portlet:renderURL>

<c:set var="currentDeployment" value="${target.currentDeployment}"/>

   <div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0" width="100%">
  <tr>
  <td class="blankSpace">&nbsp;</td>
  <td><h2>        <fmt:message key="viewing-details-for-site" bundle="${depBundle}">
                                <fmt:param value="${target.name}"/></fmt:message>
                                 </h2></td>
    <td width="100%" class="error rightAlign"><dspel:include page="../includes/formErrors.jsp"/></td>
  </tr>
  </table>
  </div>

  <div id="adminDeployment">

 <c:choose>
   <c:when test="${target.halted}">
  <table border="0" cellpadding="10" cellspacing="0" width="100%" class="overview siteAlert">
   </c:when>
   <c:when test="${!target.accessible}">
  <table border="0" cellpadding="10" cellspacing="0" width="100%" class="overview siteAlert">
   </c:when>
   <c:otherwise>
  <table border="0" cellpadding="10" cellspacing="0" width="100%" class="overview">
          </c:otherwise>
 </c:choose>
  <tr>
  <td><h1><c:out value="${target.name}"/></h1></td>
  <td class="rightAligned">

  <table border="0" cellpadding="2" cellspacing="0" align="right">
   <tr>
   <td class="rightAligned"><p>      <p><fmt:message key="accepting-deployments" bundle="${depBundle}"/>: </p></td>
   <td class="leftAligned"><p>
        <c:if test="${target.deployable}"><fmt:message key="true" bundle="${depBundle}"/></c:if>
        <c:if test="${!target.deployable}"><fmt:message key="false" bundle="${depBundle}"/></c:if>
      </p></td>
   </tr>
   <tr>
   <td class="rightAligned" valign="top"><p><fmt:message key="agent-status" bundle="${depBundle}"/>:</p></td>
   <td class="leftAligned">
      <p>
        <c:choose>
          <c:when test="${target.accessible}">
            <a href="<c:out value='${agentsURL}'/>" onmouseover="status='';return true;">
              <c:choose>
                <c:when test="${deploymentServer.allowMissingNonEssentialAgents}">
                  <fmt:message key="all-essential-agents-accessible" bundle="${depBundle}"/>
                </c:when>
                <c:otherwise>
                  <fmt:message key="all-agents-accessible" bundle="${depBundle}"/>
                </c:otherwise>
              </c:choose>
            </a> </p>
          </c:when>
         <c:otherwise>
           <c:forEach var="agent" items="${target.agents}">
              <c:if test="${agent.status.stateError}">
              <a href="<c:out value='${agentsURL}'/>" onmouseover="status='';return true;"><span class="error">
          <fmt:message key="in-error-state" bundle="${depBundle}">
           <fmt:param value='${agent.name}'/>
          </fmt:message>
          </span></a>
               <br />
              </c:if>
             </c:forEach>
         </c:otherwise>
        </c:choose>
        </p></td>
   </tr>


   </table>
  </td>
  </tr>

  <tr>
  <td colspan="2"><p><c:out value="${target.description}"/> </p></td>
  </tr>

  <tr>
  <td>


  <table border="0" cellpadding="2" cellspacing="0">

    <c:choose>
    <c:when test="${!target.initialized}">

    <c:choose>
    <c:when test="${empty currentDeployment}">
   <tr>
   <td class="leftAligned">  <p><fmt:message key="currently-init-create-initial" bundle="${depBundle}"/></p></td>
   </tr>

    </c:when>
    <c:otherwise>
  <tr>
   <td class="leftAligned"> <p><fmt:message key="currently-init" bundle="${depBundle}"/></p></td></tr>
    </c:otherwise>
    </c:choose>

    </c:when>
    <c:otherwise>
   <tr>
   <td class="rightAligned">   <p><fmt:message key="recent-deployment" bundle="${depBundle}"/>:</p>
   <td class="leftAligned">      <c:forEach var="projectID" items="${currentDeployment.projectIDs}" varStatus="prst">
      <p>
      <pws:getProject var="project" projectId="${projectID}">
       <c:if test="${prst.count ne 1}">
        ,
       </c:if>
       <%
         atg.security.ThreadSecurityManager.setThreadUser(user);
       %>
       <dspel:include page="/includes/createProjectURL.jsp" flush="true">
        <dspel:param name="outputAttributeName" value="projectURL"/>
        <dspel:param name="inputProjectId" value="${projectID}"/>
       </dspel:include>
          <a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
        <c:out value="${project.displayName}"/>
       </a>
      </pws:getProject>
    </c:forEach> <%-- Projects --%>
     </p></td></tr>
   <tr>
   <td class="rightAligned"> <p><fmt:message key="planned-deployments" bundle="${depBundle}"/>:</p>
   <td class="leftAligned"> <p> <c:out value="${target.countOfPendingDeployments}"/></p>
   <tr>
   <td class="rightAligned"><p><fmt:message key="last-successful-deployment" bundle="${depBundle}"/>:</p>
   <td class="leftAligned">

      <c:choose>
       <c:when test="${target.accessible}">
        <c:catch var ="exp">
        <c:set var="deployedProject" value="${target.lastDeployedProject}"/>
        </c:catch>
        <c:if test="${empty exp && !empty deployedProject}">
         <%
           atg.security.ThreadSecurityManager.setThreadUser(user);
         %>
         <dspel:include page="/includes/createProjectURL.jsp" flush="true">
          <dspel:param name="outputAttributeName" value="projectURL"/>
          <dspel:param name="inputProjectId" value="${deployedProject.id}"/>
         </dspel:include>
         <a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
         <p>
         <c:out value="${deployedProject.displayName}"/>
         </a> </p></td></tr>
        </c:if>
        </c:when>
       <c:otherwise>
       <p><fmt:message key="cannot-discern-inaccessible" bundle="${depBundle}"/>
          </p>
       </c:otherwise>
      </c:choose>
      </td></tr>
   <tr>
   <td class="rightAligned"> <p><fmt:message key="next-deployment" bundle="${depBundle}"/>:</p>
   <td class="leftAligned"><p>
      <c:forEach var="projectID" items="${target.nextDeployment.projectIDs}" varStatus="prst">
      <pws:getProject var="project" projectId="${projectID}">
       <c:if test="${prst.count ne 1}">
        ,
       </c:if>
       <%
         atg.security.ThreadSecurityManager.setThreadUser(user);
       %>
       <dspel:include page="/includes/createProjectURL.jsp" flush="true">
        <dspel:param name="outputAttributeName" value="projectURL"/>
        <dspel:param name="inputProjectId" value="${projectID}"/>
       </dspel:include>
          <a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
        <c:out value="${project.displayName}"/>
       </a>
      </pws:getProject>
    </c:forEach> <%-- Projects --%>
     </p> </td></tr>

    <c:if test="${target.countOfPendingDeployments > 0}">
      <c:set var="currentTargetCluster" value="${target.currentStatus.clusterStatus.clusterID}"/>
      <c:set var="currentCACluster"     value="${deploymentServer.clusterName.clusterName}"/>
      <c:if test="${currentTargetCluster != currentCACluster}">
        <tr>
          <td class="rightAligned">
            <p><fmt:message key="comments" bundle="${depBundle}"/>: </p>
          </td>
          <td class="leftAligned">
            <p><%-- <c:out value="${currentTargetCluster}"/> -- <c:out value="${currentCACluster}"/><br/> --%>
            <c:choose>
              <c:when test="${! empty currentTargetCluster}">
                <c:choose>
                  <c:when test="${currentTargetCluster != currentCACluster}">
                    <fmt:message key="remote-site-deployment-running" bundle="${depBundle}"/>&nbsp;<c:out value="${currentTargetCluster}"/>
                  </c:when>
                  <c:otherwise>
                    <!-- no comment to print -->
                  </c:otherwise>
                </c:choose>
              </c:when>
              <c:otherwise>
                <fmt:message key="cannot-determine-clustername" bundle="${depBundle}"/>
              </c:otherwise>
            </c:choose>
            </p>
          </td>
        </tr>
      </c:if>
    </c:if>

</c:otherwise>
</c:choose>
   </table>
  </td>

  <td class="rightAligned">
<c:choose>
  <c:when test="${!target.accessible}">

  <div class="siteAlertText">
  <fmt:message key="site-inaccessible" bundle="${depBundle}"/>
  </div>
    </c:when>

  <c:when test="${target.halted}">

  <div class="siteAlertText">
  <fmt:message key="deployment-halted" bundle="${depBundle}"/>
  </div>
    </c:when>
</c:choose>
  <p><span class="siteAlertSubText"></span></p>
  </td>
  </tr>

  </table>

  </div>
  <div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0">
  <tr>
  <td class="paddingLeft" width="100%">
  </td>
    <pws:getTargetDef var="def" targetId="${target.ID}"/>
    <c:if test="${!target.initialized && currentDeployment == null && def.initialSnapshotName == null}">
    <td class="buttonImage"><a href="javascript:showIframe('initSiteAction')" class="mainContentButton go" onmouseover="status='';return true;"><fmt:message key="create-initial-dep" bundle="${depBundle}"/></a></td>
    </c:if>
    <c:if test="${empty currentDeployment && target.initialized}">
      <td class="buttonImage"><a href="javascript:showIframe('fullSiteAction')" class="mainContentButton go" onmouseover="status='';return true;"><fmt:message key="full-deployment" bundle="${depBundle}"/></a></td>
    </c:if>
    <c:if test="${!target.halted}">
    <td class="buttonImage"><a href="javascript:showIframe('haltSiteAction')" class="mainContentButton delete" onmouseover="status='';return true;"><fmt:message key="halt-deployments" bundle="${depBundle}"/></a></td>
    </c:if>
    <c:if test="${target.halted}">
    <td class="buttonImage"><a href="javascript:showIframe('resumeSiteAction')" class="mainContentButton go" onmouseover="status='';return true;"><fmt:message key="resume-deployments" bundle="${depBundle}"/></a></td>
    </c:if>
    <td class="buttonImage"><a href="javascript:showIframe('resetSiteAction')" class="mainContentButton delete" onmouseover="status='';return true;"><fmt:message key="critical-reset" bundle="${depBundle}"/></a></td>
  <td class="blankSpace"></td>
  </tr>
  </table>
  </div>
   <dspel:form method="post" action="${thisPageURL}" id="haltSiteForm" formid="haltSiteForm" name="haltSiteForm">
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.successURL" value="${thisPageURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.failureURL" value="${thisPageURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.haltTarget" value="${target.ID}" priority="-10"/>
    </dspel:form>
    <dspel:form method="post" action="${thisPageURL}" id="fullSiteForm" formid="fullSiteForm" name="fullSiteForm">
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.successURL" value="${thisPageURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.failureURL" value="${thisPageURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.fullDeployTarget" value="${target.ID}" priority="-10"/>
    </dspel:form>
    <dspel:form method="post" action="${thisPageURL}" id="resumeSiteForm" formid="resumeSiteForm" name="resumeSiteForm">
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.successURL" value="${thisPageURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.failureURL" value="${thisPageURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.resumeTarget" value="${target.ID}" priority="-10"/>
    </dspel:form>

   <dspel:form method="post" action="${thisPageURL}" id="resetSiteForm" formid="resetSiteForm" name="resetSiteForm">
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.successURL" value="${thisPageURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.failureURL" value="${thisPageURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.resetTarget" value="${target.ID}" priority="-10"/>
    </dspel:form>

   <dspel:form method="post" action="${thisPageURL}" id="initSiteForm" formid="initSiteForm" name="initSiteForm">
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.successURL" value="${thisPageURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.failureURL" value="${thisPageURL}"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.initialDeploymentTargetID" value="${target.ID}" />
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.flagAgents" value="false" id="flagAgentInitOption"/>
     <dspel:input type="hidden" bean="${deploymentFormHandlerName}.createInitialDeployment" value="foo" priority="-10"/>
    </dspel:form>


<c:if test="${!empty currentDeployment}">
<%-- Current deployment window start show this window only when there is a deployment to show--%>
 <div id="adminDeployment">
 <c:choose>
   <c:when test="${currentDeployment.status.stateError}">
  <table border="0" cellpadding="10" cellspacing="0" width="100%" class="overview siteAlert">
   </c:when>
   <c:otherwise>
  <table border="0" cellpadding="10" cellspacing="0" width="100%" class="overview">
          </c:otherwise>
 </c:choose>

  <tr>
    <td>

  <table border="0" cellpadding="2" cellspacing="0">
  <tr>
  <td><h2><fmt:message key="current-deployment-heading" bundle="${depBundle}"/></h2></td>
   </tr>
  <tr>
 <td class="rightAligned">   <p><fmt:message key="projects" bundle="${depBundle}"/>: </p></td>
  <td class="leftAligned"><p>
       <c:forEach var="projectID" items="${currentDeployment.projectIDs}" varStatus="prst">
      <pws:getProject var="project" projectId="${projectID}">
       <c:if test="${prst.count ne 1}">
        ,
       </c:if>
       <%
         atg.security.ThreadSecurityManager.setThreadUser(user);
       %>
       <dspel:include page="/includes/createProjectURL.jsp" flush="true">
        <dspel:param name="outputAttributeName" value="projectURL"/>
        <dspel:param name="inputProjectId" value="${projectID}"/>
       </dspel:include>
          <a href="<c:out value='${projectURL}'/>" onmouseover="status='';return true;">
        <c:out value="${project.displayName}"/>
       </a>
      </pws:getProject>
     </c:forEach> <%-- Projects --%>
      </p>
    </td>  </tr>

  <td class="rightAligned"><p>
    <fmt:message key="deployment-status" bundle="${depBundle}"/>:</p></td>
  <td class="leftAligned"><p>
     <c:if test="${currentDeployment.status.stateError}">
      <span class="error"><c:out value="${currentDeployment.status.userStateString}"></c:out></span>
     </c:if>
      <c:if test="${! currentDeployment.status.stateError}">
      <c:out value="${currentDeployment.status.userStateString}"><fmt:message key="not-deploying" bundle="${depBundle}"/></c:out>
     </c:if>

      </p>
   </td></tr>
  <td class="rightAligned"><p>
    <fmt:message key="last-execution-time" bundle="${depBundle}"/>:</p></td>
  <td class="leftAligned"><p>
      <c:out value="${currentDeployment.status.deploymentLastExecutionTime div 1000}"/>&nbsp;<fmt:message key="seconds" bundle="${depBundle}"/></p>
      </td></tr>
  <td class="rightAligned"><p>
    <fmt:message key="total-execution-time" bundle="${depBundle}"/>:</p></td>
  <td class="leftAligned"><p>
      <c:out value="${ currentDeployment.status.deploymentTotalExecutionTime div 1000}"/> <fmt:message key="seconds" bundle="${depBundle}"/>
      </p></td></tr>
  </table>
  <td>
  </td>
  <td class="rightAligned">
    <c:if test="${currentDeployment.status.stateError}">
      <div class="siteAlertText">
        <fmt:message key="deployment-failed" bundle="${depBundle}"/><br>
      </div>
    </c:if>
    <c:if test="${deploymentServer.useDafDeployment}">
      <script type="text/Javascript">
        <c:url var="progressBarURL" value="/html/DeploymentPortlet/site/site_progress_bar.jsp" >
          <c:param name="deployId" value="${currentDeployment.deploymentID}" />
        </c:url>
        var timeoutInterval = <c:out value="${deploymentServer.progressUpdatePollSleepTime}" />;

        function updateProgressBar(isError, isStop)
        {
          var progBarURL = '<c:out value="${progressBarURL}" />';
          if (isError == "true") {
            progBarURL += "&error=yes";
          }

          issueRequest( progBarURL, 'progressBar');

          var progBarDiv = document.getElementById('progressBar');
          if (progBarDiv != null) {
            var progBarTxt = progBarDiv.innerHTML;
            if (progBarTxt.indexOf('100%') != -1 ||
                progBarTxt.indexOf('Error') != -1)
            {
              isStop = "true";
            }
          }

          if (isStop != "true") {
            setTimeout("updateProgressBar()", timeoutInterval*1000);
          }
          else {
            window.location.reload(true);
          }
        }

        function updateProgressStatus(isError)
        {
          var progBarURL = '<c:out value="${progressBarURL}" />&norefresh=true';
          if (isError == "true") {
            progBarURL += "&error=yes";
          }

          issueRequest( progBarURL, 'progressBar');

          var progBarDiv = document.getElementById('progressBar');
          if (progBarDiv != null) {
            var progBarTxt = progBarDiv.innerHTML;
          }
        }

        var isError  = '<c:out value="${currentDeployment.status.stateError}" />';
        var isActive = '<c:out value="${currentDeployment.status.stateActive}" />';
        var isStop   = 'true';
        if (isActive == 'true')
          isStop = 'false';

        if (isError == 'true' || isStop == 'true')
          updateProgressStatus(isError);
        else
          updateProgressBar(isError, isStop);
      </script>
    </c:if>
    <div id="progressBar">
    </div>
  </td>
  </tr>

  <tr>
  <td colspan="2" class="error leftAlign"><p>
     <c:if test="${currentDeployment.status.stateError}">
   <fmt:message key="error-message" bundle="${depBundle}"/>:
     <div class="siteAlert">
     <c:out value="${currentDeployment.status.errorMessage}"/>
     </div>
    </c:if>
    </td>
  </tr>

  </table>

  <%-- If this is DAF deployment display any failure messages that may have occurred --%>
  <c:if test="${deploymentServer.useDafDeployment}">
    <%@ include file="site_failures.jspf" %>
  </c:if>

  </div>
<%-- Current deployment window end--%>
  <div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0">
  <tr>
  <td class="paddingLeft" width="100%">
  <div id="adminDeploymentDetails_container" class="containerIconClosed adminDetails"><a href="#" onmousedown="closeAdminDetails('adminDeploymentDetails');" onmouseover="status='';return true;"><fmt:message key="current-deployment-details" bundle="${depBundle}"/></a></div>
  </td>
     <c:if test="${currentDeployment.stoppable}">
      <dspel:form method="post" action="${thisPageURL}" id="stopDeploymentForm" formid="stopDeploymentForm" name="stopDeploymentForm">
       <dspel:input type="hidden" bean="${deploymentFormHandlerName}.successURL" value="${thisPageURL}"/>
       <dspel:input type="hidden" bean="${deploymentFormHandlerName}.failureURL" value="${thisPageURL}"/>
       <dspel:input type="hidden" bean="${deploymentFormHandlerName}.stopDeployment" value="${currentDeployment.deploymentID}" priority="-10"/>
      </dspel:form>
   <td class="buttonImage"><a href="javascript:showIframe('stopDeploymentAction')" class="mainContentButton delete" onmouseover="status='';return true;"><fmt:message key="stop" bundle="${depBundle}"/></a></td>
    </c:if>

    <c:if test="${currentDeployment.resumable}">
     <dspel:form method="post" action="${thisPageURL}" id="resumeDeploymentForm" formid="resumeDeploymentForm" name="resumeDeploymentForm">
      <dspel:input type="hidden" bean="${deploymentFormHandlerName}.successURL" value="${thisPageURL}"/>
      <dspel:input type="hidden" bean="${deploymentFormHandlerName}.failureURL" value="${thisPageURL}"/>
      <dspel:input type="hidden" bean="${deploymentFormHandlerName}.resumeDeployment" value="${currentDeployment.deploymentID}" priority="-10"/>
     </dspel:form>
   <td class="buttonImage"><a href="javascript:showIframe('resumeDeploymentAction')" class="mainContentButton go" onmouseover="status='';return true;"><fmt:message key="resume" bundle="${depBundle}"/></a></td>
    </c:if>
    <c:if test="${currentDeployment.rollbackable}">
     <dspel:form method="post" action="${thisPageURL}" id="rollbackDeploymentForm" formid="rollbackDeploymentForm" name="rollbackDeploymentForm">
      <dspel:input type="hidden" bean="${deploymentFormHandlerName}.successURL" value="${thisPageURL}"/>
      <dspel:input type="hidden" bean="${deploymentFormHandlerName}.failureURL" value="${thisPageURL}"/>
      <dspel:input type="hidden" bean="${deploymentFormHandlerName}.rollbackDeployment" value="${currentDeployment.deploymentID}" priority="-10"/>
     </dspel:form>
   <td class="buttonImage"><a href="javascript:showIframe('rollbackDeploymentAction')" class="mainContentButton diff" onmouseover="status='';return true;"><fmt:message key="rollback" bundle="${depBundle}"/></a></td>
    </c:if>

    <c:if test="${currentDeployment.removable}">
     <dspel:form method="post" action="${thisPageURL}" id="cancelDeploymentForm" formid="cancelDeploymentForm" name="cancelDeploymentForm">
      <dspel:input type="hidden" bean="${deploymentFormHandlerName}.successURL" value="${thisPageURL}"/>
      <dspel:input type="hidden" bean="${deploymentFormHandlerName}.failureURL" value="${thisPageURL}"/>
      <dspel:input type="hidden" bean="${deploymentFormHandlerName}.deleteDeployment" value="${currentDeployment.deploymentID}" priority="-10"/>
     </dspel:form>
     
     <c:choose>
       <c:when test="${!empty currentDeployment.status.failureMessages || currentDeployment.rollbackable}">
       <td class="buttonImage"><a href="javascript:showIframe('cancelCommittedDeploymentAction')" class="mainContentButton delete" onmouseover="status='';return true;"><fmt:message key="cancel" bundle="${depBundle}"/></a></td>
     </c:when> 
     
     <c:otherwise>
       <td class="buttonImage"><a href="javascript:showIframe('cancelDeploymentAction')" class="mainContentButton delete" onmouseover="status='';return true;"><fmt:message key="cancel" bundle="${depBundle}"/></a></td>
     </c:otherwise>   
     </c:choose>
    </c:if>


   <td class="blankSpace"></td>
  </tr>
  </table>
  </div>

    <div id="adminDeploymentDetails" class="hierarchyPaneClosed">

  <table cellpadding="0" cellspacing="0" border="0" class="dataTable">
  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="deployment-id" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo"><c:out value="${currentDeployment.deploymentID}"/></span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="deployed-from-server" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo"><c:out value="${currentDeployment.status.deploymentServer}"/></span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="deployed-by" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo"><c:out value="${currentDeployment.createInitiator}"/></span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="deployment_began" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo"><c:out value="${currentDeployment.status.deploymentBeginTimestampAsDate}"/></span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="deployment-projects" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo">
  <c:forEach var="projectID" items= "${currentDeployment.projectIDs}" varStatus="prStatus">
    <pws:getProject var="project" projectId="${projectID}">
        <c:if test="${prStatus.count gt 1}">
       ,&nbsp;
             </c:if>
      <c:out value="${project.displayName}"/>
    </pws:getProject>
  </c:forEach>
  </span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="deployment-snapshot-id" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo"><c:out value="${currentDeployment.status.deploymentToSnapshot}"/></span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="deployment-type" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo"><fmt:message key="deployment-type-${currentDeployment.type}" bundle="${depBundle}"/></span></td>
  </tr>

  <c:if test="${!deploymentServer.useDafDeployment}">
    <tr>
    <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="manifest-id" bundle="${depBundle}"/></span></td>
    <td class="rightColumn">
      <span class="tableInfo">
        <c:out value="${currentDeployment.status.deploymentManifestID}"/>
      </span>
    </td>
    </tr>
  </c:if>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="committed-data" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo">
        <c:choose>
        <c:when test="${currentDeployment.status.deploymentCommitted}">
                <fmt:message key="yes" bundle="${depBundle}"/>
        </c:when>
        <c:otherwise>
                <fmt:message key="no" bundle="${depBundle}"/>
        </c:otherwise>
        </c:choose>
        </span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="deployment-mode" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo">
  <fmt:message key="deployment-mode-${currentDeployment.mode}" bundle="${depBundle}"/>
  </span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="start-live-datastore" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo"><c:out value="${currentDeployment.status.deploymentAgentInitialLiveDataStore}"/></span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="switched-datastores" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo">

        <c:choose>
        <c:when test="${currentDeployment.status.deploymentSwitched}">
                <fmt:message key="yes" bundle="${depBundle}"/>
        </c:when>
        <c:otherwise>
                <fmt:message key="no" bundle="${depBundle}"/>
        </c:otherwise>
        </c:choose>

        </span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="flagged-for-rollback" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo">

        <c:choose>
        <c:when test="${currentDeployment.status.deploymentRollback}">
                <fmt:message key="yes" bundle="${depBundle}"/>
        </c:when>
        <c:otherwise>
                <fmt:message key="no" bundle="${depBundle}"/>
        </c:otherwise>
        </c:choose>

        </span></td>
  </tr>

  <c:if test="${!deploymentServer.useDafDeployment}">
    <tr>
    <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="rollback-manifest-id" bundle="${depBundle}"/></span></td>
    <td class="rightColumn"><span class="tableInfo"><c:out value="${currentDeployment.status.deploymentRollbackManifestID}"/></span></td>
    </tr>
  </c:if>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="current-status" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo"><c:out value="${currentDeployment.status.userStateString}"/></span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo">Stopped by</span></td>
  <td class="rightColumn"><span class="tableInfo"><c:out value="${currentDeployment.stopInitiator}"/></span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="status-last-changed" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo"><fmt:formatDate type="both" value="${currentdeployment.status.stateTimestampAsDate}"/></span></td>
  </tr>

  <tr>
  <td class="leftColumn" style="background-color: #F0F0F0; color: #545454; border-right: solid 1px #CCC;"><span class="tableInfo"><fmt:message key="error-message" bundle="${depBundle}"/></span></td>
  <td class="rightColumn"><span class="tableInfo"><c:out value="${currentDeployment.status.errorMessage}"/></span></td>
  </tr>
  </table>

  </div>

  </c:if> <%-- !empty currentDeployment --%>

  </td>
  </tr>
  </table>

 <!-- end content -->
 <script language="Javascript" type="text/javascript">
  <!--
  registerOnLoad(function() {init('initSiteAction', 'fullSiteAction', 'haltSiteAction', 'resumeSiteAction', 'resetSiteAction', 'stopDeploymentAction', 'resumeDeploymentAction', 'rollbackDeploymentAction', 'cancelDeploymentAction', 'cancelCommittedDeploymentAction');})
   -->
 </script>

 <div id="initSiteAction" class="confirmNoteAction">
 <dspel:iframe page="iframes/action_init_site.jsp" scrolling="no"/>
</div>
<div id="fullSiteAction" class="confirmNoteAction">
 <dspel:iframe page="iframes/action_full_site.jsp" scrolling="no"/>
</div>
<div id="haltSiteAction" class="confirmAction">
 <dspel:iframe page="iframes/action_halt_site.jsp" scrolling="no"/>
</div>
<div id="resumeSiteAction" class="confirmAction">
 <dspel:iframe page="iframes/action_resume_site.jsp" scrolling="no"/>
</div>
<div id="resetSiteAction" class="confirmNoteAction">
 <dspel:iframe page="iframes/action_reset_site.jsp" scrolling="no"/>
</div>
<div id="stopDeploymentAction" class="confirmAction">
 <dspel:iframe page="iframes/action_stop_deployment.jsp" scrolling="no"/>
</div>
<div id="resumeDeploymentAction" class="confirmAction">
 <dspel:iframe page="iframes/action_resume_deployment.jsp" scrolling="no"/>
</div>
<div id="rollbackDeploymentAction" class="confirmAction">
 <dspel:iframe page="iframes/action_rollback_deployment.jsp" scrolling="no"/>
</div>
<div id="cancelDeploymentAction" class="confirmAction">
 <dspel:iframe page="iframes/action_cancel_deployment.jsp" scrolling="no"/>
</div>
<div id="cancelCommittedDeploymentAction" class="confirmNoteAction">
 <dspel:iframe page="iframes/action_cancel_committed_deployment.jsp" scrolling="no"/>
</div>

</dspel:page>
<!-- END FILE site_detail.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/DeploymentPortlet/site/site_detail.jsp#2 $$Change: 651448 $--%>
