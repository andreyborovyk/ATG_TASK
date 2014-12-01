<%@ page import="javax.portlet.*,atg.epub.pws.taglib.*" %>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="pws"     uri="http://www.atg.com/taglibs/pws" %>
<%@ taglib prefix="biz"     uri="http://www.atg.com/taglibs/bizui" %> 
<%@ taglib prefix="paf"     uri="http://www.atg.com/taglibs/portal/paf1_3" %>
<%@ taglib prefix="pafrt"   uri="http://www.atg.com/taglibs/portal/paf-rt1_3" %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>

<!-- Begin ProjectsPortlet's projectTasks.jsp -->
<dspel:page>
<fmt:setBundle var="projectsBundle" basename="atg.epub.portlet.ProjectsPortlet.Resources"/>
<fmt:message var="claimButtonLabel" key="claim-button-label" bundle="${projectsBundle}"/>
<fmt:message var="assignButtonLabel" key="assign-button-label" bundle="${projectsBundle}"/>
<fmt:message var="releaseButtonLabel" key="release-button-label" bundle="${projectsBundle}"/>


<portlet:defineObjects/>

<%@ include file="projectConstants.jspf" %>

<%-- GET THE CURRENT PROJECT --%>
<pws:getCurrentProject var="projectContext"/>

<c:if test="${projectContext.project ne null}">

<c:set var="currentProjectId" value="${projectContext.project.id}"/>
<c:set var="currentProcessId" value="${projectContext.process.id}"/>

<dspel:importbean bean="/atg/epub/servlet/TaskActionFormHandler" var="taskActionFormHandler"/>
<dspel:importbean bean="/atg/epub/servlet/FireWorkflowOutcomeFormHandler" var="fireOutcomeFormHandler"/>
<dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>
<dspel:importbean var="userDirectory" bean="/atg/userprofiling/ProfileUserDirectory"/>

<%-- Current User ID --%>
<c:set var="currentUserId" value="${profile.repositoryId}"/>

<portlet:actionURL  var="actionURL"/>

<c:choose>
  <c:when test="${(projectView eq PROJECT_DETAIL_VIEW) || (projectView eq TASKS_VIEW)}">
    <c:set var="isProjectView" value="${true}"/>
  </c:when>
  <c:otherwise>
    <c:set var="isProjectView" value="${false}"/>
  </c:otherwise>
</c:choose>

<pws:getTargets var="oneOffTargets" oneOff="true"/>

<div class="contentActions">
  <table cellpadding="0" cellspacing="0" border="0">
    <tr>
      <td class="blankSpace">&nbsp;</td>
      <dspel:tomap var="currentUser" value="${profile.dataSource}"/>
      <td><h2>
        <pws:getTasks var="allResults" active="${false}" unowned="${false}" userOnly="${false}" 
                      processId="${projectContext.process.id}"/>
        <c:choose>
         <c:when test="${empty allResults.tasks}">
           <fmt:message key="project-completed-msg" bundle="${projectsBundle}"/>
         </c:when>
         <c:otherwise>        
            <%-- avoid diplaying "null" in fmt tag --%>
            <c:set var="displayFirstName" value=""/>
            <c:set var="displayLastName" value=""/>
            <c:if test="${currentUser.firstName ne null}">
               <c:set var="displayFirstName" value="${currentUser.firstName}"/>
            </c:if>
            <c:if test="${currentUser.lastName ne null}">
               <c:set var="displayLastName" value="${currentUser.lastName}"/>
            </c:if>
            <fmt:message key="task-user-header" bundle="${projectsBundle}">
              <fmt:param value="${projectContext.process.displayName}"/>
            </fmt:message>
         </c:otherwise>
        </c:choose>
      </h2></td>
      <c:choose>
        <c:when test="${empty oneOffTargets}">
          <td width="100%" class="error" style="text-align: right;">
        </c:when>
        <c:otherwise>
          <td class="error" align="right">
        </c:otherwise>
      </c:choose>
      <td class="error" align="right">
        <c:forEach var="outcomeException" items="${fireOutcomeFormHandler.formExceptions}">
          <c:out value="${outcomeException.message}"/><br />
        </c:forEach>
        <c:forEach var="taskException" items="${taskActionFormHandler.formExceptions}">
          <c:out value="${taskException.message}"/><br />
        </c:forEach>
      </td>

      
      <c:if test="${!empty oneOffTargets}">
        <td nowrap="nowrap" style="text-align: right" class="draftDeploy" width="100%">
          <c:choose>
          <c:when test="${empty allResults.tasks}">
            <span class="disabled"><fmt:message key="one-off-deployment" bundle="${projectsBundle}"/>:</span>
            <c:set var="disabled" value="disabled"/>
            <c:set var="className" value="button disabled"/>
          </c:when>
          <c:otherwise>
            <span class="contentActionLabel"><fmt:message key="one-off-deployment" bundle="${projectsBundle}"/>:</span>
            <c:set var="disabled" value=""/>
            <c:set var="className" value="standardButton"/>
          </c:otherwise>
          </c:choose>
          <select id="oneOffDeployOption" style="width: auto" <c:out value='${disabled}'/> >
            <option value="" label=""><fmt:message key="select-target-server" bundle="${projectsBundle}"/></option>
            <c:forEach var="oneOffTarget" items="${oneOffTargets}">
              <c:url var="oneOffIframeUrl" context="/PubPortlets" value="/html/ProjectsPortlet/oneOffDeployIframe.jsp">
                <c:param name="projectId" value="${projectContext.project.id}"/>
                <c:param name="targetId"  value="${oneOffTarget.ID}"/>
              </c:url>
              <option value='<c:out value="${oneOffIframeUrl}"/>'><c:out value="${oneOffTarget.name}"/></option>
            </c:forEach>
          </select>
          <a href='javascript:oneOffDeploy();' class="<c:out value='${className}'/>"><fmt:message key="go-label" bundle="${projectsBundle}"/>&nbsp;&raquo;</a>
        </td>
      </c:if>
      <td class="blankSpace">&nbsp;</td>
    </tr>
  </table>
</div>
<script language="JavaScript" type="text/javascript">
  function oneOffDeploy () {
    var oneOffDeploySelect = document.getElementById("oneOffDeployOption");
    if (oneOffDeploySelect) {
     if (oneOffDeploySelect.options[oneOffDeploySelect.selectedIndex].value!="") {
       document.getElementById('oneOffDeployIframe').src=oneOffDeploySelect.options[oneOffDeploySelect.selectedIndex].value;
       showIframe('actionOneOffDeploy');
     }
    }
  }
</script>

<c:forEach items="${projectContext.process.workflowInstances}" var="workflow" varStatus="workflowLoop">
  <pws:getTasks var="results" active="${false}" unowned="${false}" userOnly="${false}"
                  processId="${projectContext.process.id}" workflowId="${workflow.repositoryId}"/>
  <c:set var="taskSet" value="${results.tasks}"/>
  <pws:getWorkflowDescriptor var="descResults" processId="${projectContext.process.id}"
                  workflowId="${workflow.repositoryId}"/>

  <c:if test="${!empty taskSet}">
    <table cellpadding="0" cellspacing="0" border="0" class="dataTable">
      <tr>
        <th class="leftAligned" width="40%">
          <span class="tableHeader">
            <web-ui:getWorkflowElementDisplayName var="workflowDisplayName" element="${descResults.workflowDescriptor}"/>
            <c:out value="${workflowDisplayName}"/><c:out value=" "/><fmt:message key="task-label" bundle="${projectsBundle}"/>
          </span>
        </th>
        <th class="centerAligned" width="35%"><span class="tableHeader"><fmt:message key="owner-label" bundle="${projectsBundle}"/></span></th>
        <th class="centerAligned" width="25%"><span class="tableHeader"><fmt:message key="outcomes-label" bundle="${projectsBundle}"/></span></th>
      </tr>
      
      <c:forEach items="${taskSet}" var="taskInfo" varStatus="taskLoop">
  
        <%-- determine whether current user has permission for this task --%>
        <pws:canFireTaskOutcome var="taskPermission" taskInfo="${taskInfo}"/>
        <%-- determine whether current user has permission to assign this task --%>
        <pws:canSetTaskOwner var="setOwnerPermission" taskInfo="${taskInfo}"/>
        <web-ui:getWorkflowElementDisplayName var="taskDisplayName" element="${taskInfo.taskDescriptor}"/>
        <web-ui:getWorkflowElementDescription var="taskDescription" element="${taskInfo.taskDescriptor}"/>
  
        <!-- tr -->
        <c:choose>
          <c:when test="${taskLoop.count % 2 == 0}"><tr class="alternateRowHighlight"></c:when> 
          <c:otherwise><tr></c:otherwise>
        </c:choose>
        <c:choose>
          <c:when test="${taskInfo.active && taskPermission.hasAccess}">

            <td class="leftAligned current" width="30%" title='<c:out value="${taskDescription}"/>'>
              <span class="tableInfo">

                <c:set var="taskId" value="${taskInfo.taskDescriptor.taskElementId}"/>
                <biz:getTaskURL var="activityURL" task="${taskInfo}" activityId="${projectContext.process.activity}"/>

                <c:choose>
                  <c:when test='${activityURL.page != null}'>
                    <img src='<c:url context="/PubPortlets" value="/html/ProjectsPortlet/images/icon_task.gif"/>'
                         alt="Task" width="16" height="16" border="0" style="margin-right: 6px; vertical-align: middle;" />
                    <c:out value="${taskDisplayName}"/>
                  </c:when>

                  <c:otherwise>
                    <c:set var="taskURL" value="${activityURL.URL}"/>

                    <a href='<c:out value="${taskURL}"/>'>
                      <img src='<c:url context="/PubPortlets" value="/html/ProjectsPortlet/images/icon_task.gif"/>'
                           alt="Task" width="16" height="16" border="0" style="margin-right: 6px; vertical-align: middle;" />
                      <c:out value="${taskDisplayName}"/>
                    </a>
                  </c:otherwise>
                </c:choose>

              </span>
            </td>

          </c:when>
          <%-- don't show link to detail if user has no permission --%>
          <c:when test="${taskInfo.active && !taskPermission.hasAccess}">
             <td class="leftAligned current" width="30%"><span class="tableInfo" title='<c:out value="${taskDescription}"/>'>
              <img src='<c:url context="/PubPortlets" value="/html/ProjectsPortlet/images/icon_task.gif"/>' alt="Task" width="16" height="16" border="0" style="margin-right: 6px; vertical-align: middle;" /><c:out value="${taskDisplayName}"/>
             </span></td>
          </c:when>
          <c:otherwise>
             <td class="leftAligned" width="30%"><span class="tableInfo" title='<c:out value="${taskDescription}"/>'>
              <img src='<c:url context="/PubPortlets" value="/html/ProjectsPortlet/images/icon_task.gif"/>' alt="Task" width="16" height="16" border="0" style="margin-right: 6px; vertical-align: middle;" /><c:out value="${taskDisplayName}"/>
             </span></td>
          </c:otherwise>
        </c:choose>
  
        <%-- CLAIM/ASSIGN FORM --%>
        <c:set var="formName" value="assignForm${workflowLoop.count}${taskLoop.count}"/>
    <dspel:form formid="${formName}" name="${formName}" method="post" action="${actionURL}">
      <c:choose>
        <c:when test="${taskInfo.taskDescriptor.assignable eq false}">
          <td nowrap width="35%"><span class="tableInfo">&nbsp;</span></td>
        </c:when>
        <c:otherwise>
          <dspel:input type="hidden" bean="TaskActionFormHandler.processId" value="${projectContext.process.id}"/>
          <c:if test="${isProjectView eq true}">
            <dspel:input type="hidden" bean="TaskActionFormHandler.projectId" value="${projectContext.project.id}"/>
          </c:if>
          <dspel:input type="hidden" bean="TaskActionFormHandler.taskElementId" value="${taskInfo.taskDescriptor.taskElementId}"/>
          <dspel:input type="hidden" priority="-1" bean="TaskActionFormHandler.assignTask" value="1"/>
          <pws:getAssignableUsers var="assUsers" taskDescriptor="${taskInfo.taskDescriptor}"/> 
          <c:set var="unowned" value="${taskInfo.owner eq null}"/>
  
          <c:choose>
            <c:when test="${taskStatus.completed}">
              <td class="centerAligned noWrap" width="30%"><span class="tableInfo"><c:out value="${taskInfo.owner.firstName} ${taskInfo.owner.lastName}"/>&nbsp;</span></td>
            </c:when>
  
      <%-- if user doesn't have task permission, show info only, not form --%>
            <c:when test ="${!setOwnerPermission.hasAccess}">
              <td class="centerAligned nowrap" width="30%"><span class="tableInfo">&nbsp;
                <c:choose>
                  <c:when test="${taskInfo.owner ne null}" >
                     <c:out value="${taskInfo.owner.firstName} ${taskInfo.owner.lastName}"/>&nbsp;
                  </c:when>
                  <c:otherwise>
                    <fmt:message key="unassigned-label" bundle="${projectsBundle}"/>
                  </c:otherwise>
                </c:choose>
                </span>
              </td>
            </c:when>
            <c:otherwise>
        <td class="centerAligned nowrap" width="30%">
              <dspel:select bean="TaskActionFormHandler.assignee" style="width:165px">
                <c:choose>
                  <c:when test="${taskInfo.owner ne null}" >
                    <dspel:option value="RELEASE"><fmt:message key="release-button-label" bundle="${projectsBundle}"/></dspel:option>
                  </c:when>
                  <c:otherwise>
                    <dspel:option value="UNASSIGNED" selected="${unowned}"><fmt:message key="unassigned-label" bundle="${projectsBundle}"/></dspel:option>
                  </c:otherwise>
                </c:choose>
  
                <option value="" disabled>_____________________</option>
  
                <dspel:option value="${currentUserId}:${userDirectory.userDirectoryName}" selected="${currentUserId eq taskInfo.owner.primaryKey}"><c:out value="${currentUser.firstName}"/> <c:out value="${currentUser.lastName}"/>&nbsp;</dspel:option>
  
                <option value="" disabled>_____________________</option>
  
                <c:forEach var="user" items="${assUsers}">
                  <c:if test="${currentUserId ne user.primaryKey}">
                    <dspel:option value="${user.primaryKey}:${user.userDirectory.userDirectoryName}" selected="${user.primaryKey eq taskInfo.owner.primaryKey}"><c:out value="${user.firstName} ${user.lastName}"/></dspel:option>
                  </c:if>
                </c:forEach>
              </dspel:select>
              &nbsp;
              <a href='<c:out value="javascript:document.${formName}.submit()"/>' class="standardButton" onmouseover="status='';return true;"><fmt:message key="assign-button-label" bundle="${projectsBundle}"/></a>
              &nbsp;
              </td>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
    </dspel:form>
  
    <script language="JavaScript" type="text/javascript">
      function setIframe( elementId ) {
        var actionSelect = document.getElementById( elementId );
        if (actionSelect) {
         if (actionSelect.options[actionSelect.selectedIndex].value!="") {
           document.getElementById('workflowIframe').src=actionSelect.options[actionSelect.selectedIndex].value;
           showIframe('approveAction'); 
         }
        }
      }
    </script>
  
       <%-- WORKFLOW ACTION FORM --%>
       <%-- value to indicated if there is a required placeholder asset that is missing --%>
       <c:set var="missingAssetName" value="${null}"/>
       <c:if test="${taskInfo.taskDescriptor.subject ne null}">
         <c:if test="${taskInfo.taskDescriptor.subject.required}">
            <pws:getPlaceholderAsset var="phItem" assetDescriptor="${taskInfo.taskDescriptor.subject}" 
                               processId="${currentProcessId}" />
        <c:if test="${phItem eq null}">
         <c:set var="missingAssetName" value="${taskInfo.taskDescriptor.subject.name}"/>
              </c:if>
         </c:if>
       </c:if>
    <c:set var="actionFormName" value="actionForm${workflowLoop.count}${taskLoop.count}"/>
    <form name='<c:out value="actionFormName"/>'>
        <td class="centerAligned noWrap" width="40%">
          <c:if test ="${taskPermission.hasAccess}">
            <c:if test="${taskInfo.active}">
              <c:choose>
                <c:when test="${missingAssetName ne null}">
                  <span  class="table tableInfo">
                        <fmt:message key="missing-asset-message" bundle="${projectsBundle}">
                          <fmt:param value="${missingAssetName}"/>
                        </fmt:message>
                  </span>
                </c:when>
                <c:otherwise>
              <%--
                    <select id="actionOption" class="table tableInfo" onChange="javascript:if (this.options[selectedIndex].value !='0'){document.getElementById('workflowIframe').src=this.options[selectedIndex].value;}">
              --%>

                    <select id="actionOption<c:out value="${taskLoop.count}${workflowLoop.count}"/>" class="table tableInfo" style="width: auto;">

                      <option value="" label=""><fmt:message key="select-action-label" bundle="${projectsBundle}"/></option>

                      <option disabled>_____________________</option>

                      <c:forEach var="outcome" items="${taskInfo.taskDescriptor.outcomes}" varStatus="outcomeLoop">
                         <web-ui:getWorkflowElementDisplayName var="outcomeDisplayName" element="${outcome}"/>
                         <web-ui:encodeParameterValue var="encodedOutcomeName" value="${outcomeDisplayName}"/>
                         <c:url var="iframeUrl" context="/PubPortlets" value="/html/ProjectsPortlet/actionNote.jsp">
                             <c:param name="processId" value="${projectContext.process.id}"/>
                             <c:if test="${isProjectView eq true}">
                               <c:param name="projectId" value="${projectContext.project.id}"/>
                             </c:if>
                             <c:param name="taskId" value="${taskInfo.taskDescriptor.taskElementId}"/>
                             <c:param name="outcomeId" value="${outcome.outcomeElementId}"/>
                             <c:param name="outcomeName" value="${encodedOutcomeName}"/>
                         <c:if test="${outcome.formURI ne null}">
                               <c:param name="outcomeFormURI" value="${outcome.formURI}"/>
                         </c:if>
                         </c:url>
                         <option value='<c:out value="${iframeUrl}"/>'><c:out value="${outcomeDisplayName}"/></option>
                       </c:forEach>
                     </select>

                &nbsp;<a href='javascript:setIframe("actionOption<c:out value="${taskLoop.count}${workflowLoop.count}"/>");' class="standardButton"
  onmouseover="status='';return true;"><fmt:message key="go-label"
  bundle="${projectsBundle}"/>&nbsp;&raquo;</a>

              </c:otherwise>  <%-- if required asset has been supplied --%>
             </c:choose>  
            </c:if>
          </c:if>
          &nbsp;
        </td>
    </form>

  </tr>

</c:forEach>


  </table>

 </c:if> <%-- if results not empty --%>

</c:forEach>

  <div class="contentActions">
    <table cellpadding="0" cellspacing="0" border="0">
      <tr>
        <td class="blankSpace" width="100%">&nbsp;</td>
        <td class="blankSpace">&nbsp;</td>
      </tr>
    </table>
  </div> 

</c:if>

<c:if test="${projectContext.project eq null}">
   <fmt:message key="no-current-project-message" bundle="${projectsBundle}"/>
   <br>project is null
</c:if> 
<c:if test="${projectContext.process eq null}">
   <br>process is null
</c:if> 
</dspel:page>
<!-- End ProjectsPortlet's projectTasks.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/html/ProjectsPortlet/projectTasks.jsp#2 $$Change: 651448 $--%>
