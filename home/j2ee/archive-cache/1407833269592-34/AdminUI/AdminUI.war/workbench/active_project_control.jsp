<%--
JSP, active project control

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/active_project_control.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>

<d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"
              var="activeProjectId"/>
<d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.environmentId"
              var="environmentId"/>
<admin-beans:getSearchEnvironment environmentId="${environmentId}" var="environment"/>

<span>
  <div>
    <strong>
      <fmt:message key="active_project_control.title"/>
    </strong>
    <d:getvalueof bean="/atg/searchadmin/adminui/navigation/NavigationStateComponent.current" var="currentNode"/>
    <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.needUpdate" var="needUpdate"/>
    &nbsp;
    <c:url var="selectUrl" value="/workbench/select_active_project_popup.jsp" />
    <input type="button" value="<fmt:message key='active_project_control.button.select'/>"
           title="<fmt:message key='active_project_control.button.select.tooltip'/>"
           onclick="return showPopUp('${selectUrl}');" />
    
    <c:if test="${activeProjectId != null}">
      <c:url value="/workbench/remove_active_project_popup.jsp" var="removeURL">
        <c:param name="projectId" value="${activeProjectId}"/>
      </c:url>
      <input type="button" value="<fmt:message key='active_project_control.button.clear'/>"
             title="<fmt:message key='active_project_control.button.clear.tooltip'/>"
             onclick="return showPopUp('${removeURL}');"/>
    </c:if>
    <br/>
    <c:if test="${activeProjectId == null}">
      <fmt:message key="active_project_control.label.none"/>
    </c:if>
    <c:if test="${activeProjectId != null}">
      <common:searchProjectFindByPrimaryKey searchProjectId="${activeProjectId}" var="project"/>
      ${fn:escapeXml(project.name)}
      <c:if test="${environmentId != null}">
        (${fn:escapeXml(environment.envName)})
      </c:if>
      <br/>
      <fmt:message key="active_project_control.label.current.index"/>
      <c:if test="${environment.currentIndex == null}">
        <fmt:message key="active_project_control.label.none" />
      </c:if>
      <c:if test="${environment.currentIndex != null}">
        <admin-beans:getSyncTaskByEnvironment environment="${environment}" var="currentSyncTask"/>
        <fmt:message var="timeFormat" key="timeFormat"/>
        <fmt:formatDate value="${currentSyncTask.currentEndDate}" pattern="${timeFormat}"/>
        (<fmt:message key="synchronization.task.${currentSyncTask.syncTaskType}" />)
      </c:if>
    </c:if>
  </div>
  <script>
    dojo.addOnLoad(function() {
      areaSupport.onResize();
    });
  </script>
  <c:if test="${needUpdate}">
    <script type="text/javascript">
      if (top.refreshRightPane) {
        top.refreshRightPane({activeProjectId:'${activeProjectId}',
          activeEnvironmentId:'${environmentId}',
          activeProjectName:'${project.name}'});
      }
    </script>
    <d:setvalue bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.needUpdate" 
      value="false"/>
  </c:if>
</span>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/active_project_control.jsp#2 $$Change: 651448 $--%>
