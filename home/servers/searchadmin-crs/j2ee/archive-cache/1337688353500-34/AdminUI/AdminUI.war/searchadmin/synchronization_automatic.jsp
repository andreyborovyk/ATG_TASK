<%--
Automatic synchronization page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_automatic.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof param="projectId" var="projectId"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationAutomaticFormHandler"
                var="synchronizationAutomaticFormHandler"/>

  <c:if test="${projectId==null}">
    <c:set var="projectId" value="${synchronizationAutomaticFormHandler.projectId}"/>
  </c:if>
  
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="synchronization_automatic.jsp" method="post">

    <div id="paneContent">
      <h3><fmt:message key="synchronization_automatic.automatic_sync_rules"/></h3>
      <p><fmt:message key="synchronization_automatic.description"/></p>

      <admin-beans:getSyncronizationAutomaticTasks projectId="${projectId}" var="synchronizationAutomaticTasks" />
      <admin-beans:getProjectStatus var="projectStatus" projectId="${projectId}"/>
      <admin-beans:getProjectCustomizations varHasLanguageErrors="hasLanguageErrors" checkLanguages="${true}" projectId="${projectId}" />
      <admin-beans:getDuplicatedContentLabels projectId="${projectId}" varHasDuplication="labelsDuplicated"/>
      <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>

      <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                      var="syncAutoTask" items="${synchronizationAutomaticTasks}">
        <admin-ui:column title="synchronization_automatic.table.header.sync_task" type="static">
          <c:set var="subname" value="${syncAutoTask.syncTaskDefinition.baseSyncTask.subname}"/>
          <c:if test="${not empty subname}">
            <c:out value="${subname}"/>
          </c:if>
        </admin-ui:column>
        <admin-ui:column title="synchronization_automatic.table.header.status" type="static">
          <c:choose>
            <c:when test="${syncAutoTask.syncTaskDefinition.enabled}">
              <fmt:message key="synchronization_automatic.status.enabled"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="synchronization_automatic.status.disabled"/>
            </c:otherwise>
          </c:choose>
        </admin-ui:column>
        <admin-ui:column title="synchronization_automatic.table.header.content_sets" type="static">
          <c:choose>
            <c:when test="${syncAutoTask.syncTaskDefinition.baseSyncTask.includeAllContentSources}">
              <fmt:message key="synchronization_automatic.table.all_content_sets"/>
            </c:when>
            <c:otherwise>
              <c:set var="contentList">
                <tags:join delimiter="; " items="${project.index.logicalPartitions}" var="logicalPartition">
                  <tags:join items="${syncAutoTask.syncTaskDefinition.baseSyncTask.contentSourceSetSelections}" var="contentSourceSetSelection"
                      prefix="${logicalPartition.name}: " delimiter=", ">
                    <c:if test="${contentSourceSetSelection.parentLogicalPartition == logicalPartition}">
                      ${contentSourceSetSelection.name}
                    </c:if>
                  </tags:join>
                  <c:forEach items="${syncAutoTask.syncTaskDefinition.baseSyncTask.logicalPartitions}" var="logPart">
                    <c:if test="${logPart == logicalPartition}">
                      ${logicalPartition.name}
                    </c:if>
                  </c:forEach>
                </tags:join>
              </c:set>
              <c:choose>
                <c:when test="${not empty contentList}">
                  <c:out value="${contentList}" />
                </c:when>
                <c:otherwise>
                  <fmt:message key="synchronization_automatic.table.none"/>
                </c:otherwise>
              </c:choose>
            </c:otherwise>
          </c:choose>
        </admin-ui:column>
        <admin-ui:column title="synchronization_automatic.table.header.criteria" type="static">
          <c:choose>
            <c:when test="${not empty syncAutoTask.syncTaskDefinition.schedule and syncAutoTask.syncTaskDefinition.schedule ne 'MANUAL'}">
              <fmt:message key="synchronization_automatic.table.header.criteria.schedule"/>
            </c:when>
            <c:when test="${empty syncAutoTask.syncTaskDefinition.caDeploymentType}">
              <fmt:message key="synchronization_automatic.table.header.criteria.request"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="synchronization_automatic.table.header.criteria.deployment"/>
            </c:otherwise>
          </c:choose>
        </admin-ui:column>
        <admin-ui:column title="synchronization_automatic.table.header.settings" type="static">
          <c:choose>
            <c:when test="${not empty syncAutoTask.syncTaskDefinition.schedule and syncAutoTask.syncTaskDefinition.schedule ne 'MANUAL'}">
              <c:out value="${syncAutoTask.scheduleAsString}"/>
            </c:when>
            <c:when test="${not empty syncAutoTask.syncTaskDefinition.caDeploymentType}">
              <fmt:message key="synchronization_automatic.table.settings.deployment">
                <fmt:param>
                  <fmt:message key="synchronization_schedule.deployment.type.${syncAutoTask.syncTaskDefinition.caDeploymentType}" />
                </fmt:param>
                <fmt:param><c:out value="${syncAutoTask.syncTaskDefinition.caDeploymentTarget}"/></fmt:param>
              </fmt:message>
            </c:when>
          </c:choose>
        </admin-ui:column>
        <c:if test="${projectStatus != 2 and !hasLanguageErrors and !labelsDuplicated}">
          <admin-ui:column type="icon">
            <fmt:message key="synchronization_automatic.table.perform.icon.tooltip" var="performIconToolTip"/>
            <a class="icon propertySync" title="${performIconToolTip}" href="#"
                 onclick="return perform('${syncAutoTask.syncTaskDefinition.id}', '${projectId}');">sync</a>
          </admin-ui:column>
        </c:if>
        <admin-ui:column type="icon">
          <fmt:message key="synchronization_automatic.table.edit.icon.tooltip" var="editIconToolTip"/>
          <c:url var="synchEditUrl" value="/searchadmin/synchronization_edit_rule.jsp"/>
          <d:a iclass="icon propertyEdit" title="${editIconToolTip}" href="${synchEditUrl}" onclick="return loadRightPanel(this.href);">
            <d:param name="projectId" value="${projectId}"/>
            <d:param name="syncTaskId" value="${syncAutoTask.syncTaskDefinition.id}"/>
            edit
          </d:a>
        </admin-ui:column>
        <admin-ui:column type="icon">
          <fmt:message key="synchronization_automatic.table.delete.icon.tooltip" var="deleteIconToolTip"/>
          <c:url value="/searchadmin/synchronization_delete_rule_popup.jsp" var="popUrl">
            <c:param name="syncTaskId" value="${syncAutoTask.syncTaskDefinition.id}"/>
            <c:param name="projectId" value="${projectId}"/>
          </c:url>
          <a class="icon propertyDelete" title="${deleteToolTip}" href="${popUrl}"
             onclick="return showPopUp(this.href);">del</a>
        </admin-ui:column>
      </admin-ui:table>
      <script type="text/javascript">
        function perform(syncTaskDefId, projectId){
          document.getElementById('syncRuleId').value=syncTaskDefId;
          document.getElementById('successUrl').value="${pageContext.request.contextPath}/searchadmin/synchronization_status_monitor.jsp?projectId=" + projectId;
          document.getElementById('performSyncTask').click();
          return false;
        }
      </script>

      <c:url var="refreshUrl" value="/searchadmin/synchronization_automatic.jsp">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>
      <c:url var="queueURL" value="/searchadmin/synchronization_status_monitor.jsp">
        <c:param name="projectId" value="${projectId}"/>
        <c:param name="isQueuedTask" value="true"/>
      </c:url>
      <d:input type="hidden" id="queueURL" name="queueURL" bean="SynchronizationAutomaticFormHandler.queueURL" value="${queueURL}" />
      <d:input type="hidden" bean="SynchronizationAutomaticFormHandler.successURL" name="successUrl" id="successUrl"
               value="${refreshUrl}" />
      <d:input type="hidden" bean="SynchronizationAutomaticFormHandler.errorURL" name="errorUrl" id="errorUrl"
               value="${refreshUrl}" />

      <d:input type="hidden" bean="SynchronizationAutomaticFormHandler.projectId" value="${projectId}" />

      <p>
        <fmt:message var="enable_all" key="synchronization_automatic.status.button.enable_all"/>
        <fmt:message var="enable_all_tooltip" key="synchronization_automatic.status.button.enable_all.tooltip"/>
        <d:input bean="SynchronizationAutomaticFormHandler.setAllSyncRuleToEnabled"
                     type="submit" value="${enable_all}" iclass="formsubmitter"
                     title="${enable_all_tooltip}"/>
        <fmt:message var="disable_all" key="synchronization_automatic.status.button.disable_all"/>
        <fmt:message var="disable_all_tooltip" key="synchronization_automatic.status.button.disable_all.tooltip"/>
        <d:input bean="SynchronizationAutomaticFormHandler.setAllSyncRuleToDisabled"
                     type="submit" value="${disable_all}" iclass="formsubmitter"
                     title="${disable_all_tooltip}"/>
        <d:input id="syncRuleId" type="hidden" bean="SynchronizationAutomaticFormHandler.syncRuleId" value=""/>
        <d:input id="performSyncTask" type="submit" bean="SynchronizationAutomaticFormHandler.performSyncTask" 
                iclass="formsubmitter" style="display:none"/>
      </p>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_automatic.jsp#2 $$Change: 651448 $--%>
