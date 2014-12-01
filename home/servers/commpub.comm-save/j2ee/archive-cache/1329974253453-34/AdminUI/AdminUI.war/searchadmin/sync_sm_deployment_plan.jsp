<%--
Deployment plan table.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_sm_deployment_plan.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>
  <admin-beans:getDeploymentPlan varDeploymentPlan="deploymentPlanData" syncTaskId="${syncTaskId}"/>
  <admin-ui:table renderer="/templates/table_data.jsp"
                  tableId="deploymentPlanTableId" modelVar="table"
                  var="deploymentPlan" items="${deploymentPlanData.deploymentPlanBeans}">
    <admin-ui:column title="synchronization_status_monitor.estimation.deployment_plan.table.header.search_environment"
                     type="notsortable" name="search_environment" width="35%">
      <c:out value="${deploymentPlan.searchEnvironmentName}"/>
    </admin-ui:column>
    <admin-ui:column title="synchronization_status_monitor.estimation.deployment_plan.table.header.available_se"
                     type="notsortable" name="available_search_environment" width="35%">
      <c:choose>
        <c:when test="${deploymentPlan.searchEnginesAvailable < syncTaskHistoryInfo.searchEnginesRequired}">
          <fmt:message key="synchronization_status_monitor.estimation.deployment_plan.error_tool_tip" var="errorSignToolTip"/>
          <c:out value="${deploymentPlan.searchEnginesAvailable}"/>
          <a class="dpErrorSign" title="${errorSignToolTip}" href="#" onclick="return false;">&nbsp;</a>
        </c:when>
        <c:otherwise>
          <c:choose>
            <c:when test="${deploymentPlan.searchEnginesAvailable > syncTaskHistoryInfo.searchEnginesRequired}">
              <fmt:message key="synchronization_status_monitor.estimation.deployment_plan.warning_tool_tip" var="warningSignToolTip"/>
              <c:out value="${deploymentPlan.searchEnginesAvailable}"/>
              <a class="dpWarningSign" title="${warningSignToolTip}" href="#" onclick="return false;">&nbsp;</a>
            </c:when>
            <c:otherwise>
              <c:out value="${deploymentPlan.searchEnginesAvailable}"/>
            </c:otherwise>
          </c:choose>
        </c:otherwise>
      </c:choose>
    </admin-ui:column>
    <admin-ui:column title="synchronization_status_monitor.estimation.deployment_plan.table.header.redundancy"
                     type="notsortable" name="redundancy" width="30%">
      <c:if test="${deploymentPlan.redundancy lt 0}">
        <d:img src="${pageContext.request.contextPath}/images/icons/icon_errorLarge.gif"/>
      </c:if>
      <c:out value="${deploymentPlan.redundancy}"/>
    </admin-ui:column>
  </admin-ui:table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_sm_deployment_plan.jsp#2 $$Change: 651448 $--%>
