<%--
Index summary synchronization section page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_summary_sync_section.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof param="syncTaskHistoryInfo" var="syncTaskHistoryInfo"/>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>
  <table class="form double" cellspacing="0" cellpadding="0"  style="width:75%">
  <tbody>
    <tr>
      <td style="width:15%;" class="label" nowrap="">
        <fmt:message key="synchronization_index_summary.sync_task"/>
      </td>
      <td style="width:50%;" nowrap="">
        <fmt:message key="synchronization.task.${syncTaskHistoryInfo.syncTaskType}"/>
      </td>
      <td style="width:15%" class="label" nowrap="">
          <fmt:message key="synchronization_index_summary.start_time"/>
        </td>
      <td nowrap="">
          <fmt:message var="timeFormat" key="timeFormat"/>
          <fmt:formatDate value="${syncTaskHistoryInfo.currentStartDate}" pattern="${timeFormat}"/>
      </td>
    </tr>
    <tr>
      <td class="label" nowrap="">
        <fmt:message key="synchronization_index_summary.sync_status"/>
      </td>
      <td nowrap="">
        <admin-beans:getSyncTaskGeneralStatus var="generalSyncTaskStatus" syncTaskId="${syncTaskId}"/>
        <fmt:message key="synchronization_status_monitor.indexing_status.${generalSyncTaskStatus}"/>
      </td>
      <td class="label" nowrap="">
          <fmt:message key="synchronization_index_summary.stop_time"/>
        </td>
      <td nowrap="">
          <fmt:message var="timeFormat" key="timeFormat"/>
          <fmt:formatDate value="${syncTaskHistoryInfo.currentEndDate}" pattern="${timeFormat}"/>
      </td>
    </tr>
    <tr>
      <td class="label" nowrap="">
        <fmt:message key="synchronization_index_summary.errors"/>
      </td>
      <td nowrap="">
        <c:out value="${syncTaskHistoryInfo.currentErrors}"/>
        <fmt:message var="view_sync_log_link" key="synchronization_index_summary.view_sync_log"/>
        <fmt:message var="view_sync_log_tooltip" key="synchronization_index_summary.view_sync_log_tooltip"/>
        <c:if test="${adminfunctions:hasIndexingLog(syncTaskId)}">
          [<d:a href="${pageContext.request.contextPath}/searchadmin/sync_error_log.jsp"
              title="${view_sync_log_tooltip}" iclass="link" onclick="return openSeparateWindow(this.href);"
              ><d:param name="projectId" value="${projectId}" /><d:param name="syncTaskId" value="${syncTaskId}"
              /><c:out value="${view_sync_log_link}"/></d:a>]
        </c:if>
      </td>
      <td class="label" nowrap="">
         <fmt:message key="synchronization_index_summary.deploy_to"/>
      </td>
      <td nowrap="">
        <c:set var="deployedTo" value="${syncTaskHistoryInfo.currentDeployedTo}"/>
        <ul>
          <c:forEach items="${syncTaskHistoryInfo.currentDeployedTo}" var="searchEnv" varStatus="searchEnvStatus">
            <c:if test="${not empty searchEnv}">
              <c:out value="${searchEnv.envName}" /><c:if test="${not searchEnvStatus.last}">,</c:if>
            </c:if>
          </c:forEach>
        </ul>
      </td>
    </tr>
  </tbody>
  </table>
  <br/>
  <br/>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_summary_sync_section.jsp#2 $$Change: 651448 $--%>
