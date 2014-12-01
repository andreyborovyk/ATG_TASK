<%--
 Rollback to index JSP.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_rollback_index_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId" scope="page"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationManualFormHandler"/>

  <d:form action="synchronization_history.jsp" method="post">
    <c:url var="successURL" value="/searchadmin/synchronization_status_monitor.jsp">
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <c:url var="errorURL" value="/searchadmin/synchronization_rollback_index_popup.jsp">
      <c:param name="syncTaskId" value="${syncTaskId}"/>
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <c:url var="queueURL" value="/searchadmin/synchronization_status_monitor.jsp">
      <c:param name="projectId" value="${projectId}"/>
      <c:param name="isQueuedTask" value="true"/>
    </c:url>
    <d:input type="hidden" name="projectId" bean="SynchronizationManualFormHandler.projectId" value="${projectId}"/>
    <d:input type="hidden" name="syncHistoryId" bean="SynchronizationManualFormHandler.syncHistoryId" value="${syncTaskId}"/>
    <d:input type="hidden" bean="SynchronizationManualFormHandler.sourceIndexViaSyncTaskId" value="${syncTaskId}"/>
    <d:input type="hidden" name="errorURL" bean="SynchronizationManualFormHandler.errorURL" value="${errorURL}"/>
    <d:input type="hidden" name="successURL" bean="SynchronizationManualFormHandler.successURL" value="${successURL}"/>
    <d:input type="hidden" id="queueURL" name="queueURL" bean="SynchronizationManualFormHandler.queueURL" value="${queueURL}" />
    <d:input type="hidden" bean="SynchronizationManualFormHandler.task" value="deploy_historical"/>
    <d:input type="hidden" bean="SynchronizationManualFormHandler.deploymentStep" value="Deployment_DeployIndex"/>
    <d:input type="hidden" bean="SynchronizationManualFormHandler.allContentSets" value="none"/>

    <admin-validator:validate beanName="SynchronizationRollbackIndex" />
    
    <div class="content">
      <admin-beans:getSearchEnvironments var="searchEnvironments" projectId="${projectId}"/>
      <admin-beans:getSearchEnvRollbackIndex varSyncTaskInfo="syncTaskHistoryInfo" varSearchEnvList="searchEnvRollbackIndex"
                                             searchEnvironments="${searchEnvironments}" syncTaskId="${syncTaskId}"/>

      <span id="alertListPopup"></span>
      <br/>
      <table class="form" cellspacing="0" cellpadding="0">
        <tr>
          <td colspan="4"><fmt:message key="synchronization_rollback_index.deploy_to"/></td>
        </tr>
        <tr>
          <td class="label">
            <fmt:message key="synchronization_rollback_index.index" />
          </td>
          <td><c:out value="${syncTaskHistoryInfo.syncTaskName}"/></td>
          <td class="label">
            <fmt:message key="synchronization_rollback_index.start"/>
          </td>
          <td>
            <c:out value="${syncTaskHistoryInfo.currentStart}"/>
          </td>
        </tr>
        <tr>
          <td class="label">
            <fmt:message key="synchronization_rollback_index.size"/>
          </td>
          <td>
            <c:out value="${syncTaskHistoryInfo.currentSize}" />
            <fmt:message key="synchronization_index_summary.size.MB"/>
          </td>
          <td class="label">
            <fmt:message key="synchronization_rollback_index.end"/>
          </td>
          <td>
            <c:out value="${syncTaskHistoryInfo.currentEnd}"/>
          </td>
        </tr>
      </table>
      <fmt:message key="synchronization_rollback_index.deploy_to.search_env"/>
      <admin-ui:table renderer="/templates/table_data.jsp"
                      modelVar="table"
                      var="syncTaskHistoryInfo"
                      items="${searchEnvRollbackIndex}">
        <admin-ui:column title="synchronization_rollback_index.table.header.search_env" type="static" name="search_env">
          <d:input type="checkbox" id="${syncTaskHistoryInfo.id}" checked="${syncTaskHistoryInfo.checked}" disabled="${syncTaskHistoryInfo.disabled}"
                   name="searchEnvironmentsList" bean="SynchronizationManualFormHandler.searchEnvironmentsList" value="${syncTaskHistoryInfo.id}"/>
          <c:out value="${syncTaskHistoryInfo.name}"/>
        </admin-ui:column>
        <admin-ui:column title="synchronization_rollback_index.table.header.current_index" type="static" name="current_index">
          <c:choose>
            <c:when test="${syncTaskHistoryInfo.currentIndex eq null}">
              <fmt:message key="synchronization_rollback_index.table.data.empty"/>
            </c:when>
            <c:otherwise>
              <c:out value="${syncTaskHistoryInfo.currentIndex}"/>  
            </c:otherwise>
          </c:choose>
        </admin-ui:column>
        <admin-ui:column title="synchronization_rollback_index.table.header.start" type="static" name="start">
          <c:choose>
            <c:when test="${syncTaskHistoryInfo.start eq null}">
              <fmt:message key="synchronization_rollback_index.table.data.empty"/>
            </c:when>
            <c:otherwise>
              <c:out value="${syncTaskHistoryInfo.start}"/>
            </c:otherwise>
          </c:choose>
        </admin-ui:column>
        <admin-ui:column title="synchronization_rollback_index.table.header.end" type="static" name="end">
          <c:choose>
            <c:when test="${syncTaskHistoryInfo.end eq null}">
              <fmt:message key="synchronization_rollback_index.table.data.empty"/>
            </c:when>
            <c:otherwise>
              <c:out value="${syncTaskHistoryInfo.end}"/>
            </c:otherwise>
          </c:choose>
        </admin-ui:column>
        <admin-ui:column title="synchronization_rollback_index.table.header.size" type="static" name="size">
          <c:choose>
            <c:when test="${syncTaskHistoryInfo.size eq 0}">
              <fmt:message key="synchronization_rollback_index.table.data.empty"/>
            </c:when>
            <c:otherwise>
              <fmt:formatNumber value="${syncTaskHistoryInfo.size}" maxFractionDigits="2" minFractionDigits="2"/>
              <fmt:message key="synchronization_index_summary.size.MB"/>
            </c:otherwise>
          </c:choose>
        </admin-ui:column>
      </admin-ui:table>
      <%--
      <c:forEach items="${items}" var="item" varStatus="index">
        <d:input type="checkbox" id="${item.id}" checked="${item.checked}" name="searchEnvironmentsList" bean="SynchronizationManualFormHandler.searchEnvironmentsList" value="${item.id}"/>
        <c:out value="${item.name}"/>
        <c:if test="${!index.last}">
          <br/>
        </c:if>
      </c:forEach>
      --%>
      <p>
        <fmt:message key="synchronization_rollback_index.text"/>
      </p>
    </div>
    <div class="footer" id="popupFooter">
      <fmt:message key="synchronization_rollback_index.button.rollback" var="rollbackButton"/>
      <fmt:message key="synchronization_rollback_index.button.rollback.tooltip" var="rollbackButtonToolTip"/>
      <d:input type="submit" value="${rollbackButton}" title="${rollbackButtonToolTip}" iclass="formsubmitter"
               bean="SynchronizationManualFormHandler.performSynchronization" onclick="return checkForm();"/>
      <input type="button" value="<fmt:message key='synchronization_rollback_index.button.cancel'/>"
             onclick="closePopUp()" title="<fmt:message key='synchronization_rollback_index.button.cancel.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_rollback_index_popup.jsp#2 $$Change: 651448 $--%>
