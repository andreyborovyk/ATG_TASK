<%--
Synchronization status monitor content

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_status_monitor_content.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page xml="true">
  <d:importbean var="handler" bean="/atg/searchadmin/adminui/formhandlers/SynchronizationStatusMonitorFormHandler"/>
  <admin-dojo:jsonObject>
    <admin-dojo:jsonValue name="syncStatusMonitor">
      <d:getvalueof param="syncTaskId" var="syncTaskId"/>
      <d:getvalueof param="projectId" var="projectId"/>
      <d:getvalueof bean="SynchronizationStatusMonitorFormHandler.showDetails" var="showDetails"/>
      <admin-beans:getSyncStatusMonitor varSyncTaskHistoryInfo="syncTaskHistoryInfo"
                          varValuesForSyncTaskSteps="valuesForSyncTaskSteps" syncTaskId="${syncTaskId}"
                          includingSubSteps="${showDetails}"
                          varEstimationResultsCallback="resultsCallback"/>
      <c:set var="syncTaskHistoryInfo" scope="request" value="${syncTaskHistoryInfo}" />
      <admin-beans:getSyncTaskGeneralStatus var="generalSyncTaskStatus" syncTaskId="${syncTaskId}"/>
      <c:set var="generalSyncTaskStatus" scope="request" value="${generalSyncTaskStatus}" />
      <c:set var="isWaitingForUserInput" value="${syncTaskHistoryInfo.indexingState eq 'paused' and resultsCallback != null}"/>
      <c:if test="${isWaitingForUserInput}">
        <p><fmt:message key="project_synchronization_status.header.estimation_complete"/></p>
      </c:if>
      <%--Indexing_status section--%>
      <c:import url="sync_sm_indexing_status.jsp" charEncoding="${charEncoding}">
        <c:param name="syncTaskId" value="${syncTaskId}"/>
        <c:param name="isWaitingForUserInput" value="${isWaitingForUserInput}"/>
      </c:import>
      <%--Estimation section--%>
      <c:if test="${isWaitingForUserInput}">
        <c:import url="sync_sm_estimation.jsp" charEncoding="${charEncoding}"/>
      </c:if>
      <%--Progress bar section--%>
      <c:if test="${syncTaskHistoryInfo.indexingState eq 'indexing' or syncTaskHistoryInfo.indexingState eq 'preIndexing'}">
        <c:import url="sync_sm_progress_bar.jsp" />
      </c:if>
      <br/>
      <fmt:message var="indexingErrorLogTooltip"  key='sync_error_log.tooltip'/>
      <table class="form" style="width:97% !important;">
        <tr><td colspan="2">
          <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table" var="stepEntry" items="${valuesForSyncTaskSteps}">
            <admin-ui:column title="synchronization_status_monitor.table.header.sync.step" type="static">
              <c:if test="${not stepEntry.subStep}">
                <fmt:message key="synchronization_status_monitor.table.header.super_step.${stepEntry.enumString}"/>
              </c:if>
              <c:if test="${stepEntry.subStep}">
                <%--&nbsp;&nbsp;&nbsp;&nbsp;<fmt:message key="synchronization_status_monitor.table.header.sub_step.${stepEntry.enumString}"/>--%>
                &nbsp;&nbsp;&nbsp;&nbsp;<c:out value="${stepEntry.item.stepOption.name}"/>
              </c:if>
            </admin-ui:column>
            <admin-ui:column title="synchronization_status_monitor.table.header.sync.status" type="static">
              <fmt:message key="synchronization_status_monitor.table.header.status.${stepEntry.status}"/>
            </admin-ui:column>
            <admin-ui:column title="synchronization_status_monitor.table.header.sync.errors" type="static">
              <c:choose>
                <c:when test="${stepEntry.errorCount > 0}">
                  <d:a href="${pageContext.request.contextPath}/searchadmin/sync_error_log.jsp"
                    title="${indexingErrorLogTooltip}" onclick="return openSeparateWindow(this.href);">
                    <d:param name="projectId" value="${projectId}" />
                    <d:param name="stepStatusId" value="${stepEntry.statusId}" />
                    <d:param name="isSuperStep" value="${not stepEntry.subStep}" />
                    <d:param name="syncTaskId" value="${syncTaskId}"/>
                    ${stepEntry.errorCount}
                  </d:a>
                </c:when>
                <c:otherwise>
                  ${stepEntry.errorCount}
                </c:otherwise>
              </c:choose>
            </admin-ui:column>
          </admin-ui:table>
        </td></tr>
      </table>
      <p>
        <a href="#" onclick="return syncStatusMonitor.displayDetails(${'true' != showDetails});">
          <fmt:message key="${'true' != showDetails ? 'synchronization_status_monitor.show_details' : 'synchronization_status_monitor.hide_details'}"/>
        </a>
      </p>
    </admin-dojo:jsonValue>
    <admin-dojo:jsonValue name="cancelButtonDisable" value="${generalSyncTaskStatus eq 'deploying'}"/>
    <admin-dojo:jsonValue name="showDetailsValue" value="${handler.showDetails}"/>
    <admin-dojo:jsonValue name="isInProgress" value="${generalSyncTaskStatus ne 'finished_with_error' and generalSyncTaskStatus ne 'Succeeded' and syncTaskId ne null and generalSyncTaskStatus ne 'Failed' and generalSyncTaskStatus ne 'Cancelled'}"/>
    <admin-dojo:jsonValue name="isWaitingForUserInput" value="${isWaitingForUserInput}"/>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_status_monitor_content.jsp#1 $$Change: 651360 $--%>
