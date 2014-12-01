<%--
Estimation section of the  synchronization status monitor page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_sm_estimation.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf"%>
<d:page>
  <h3><fmt:message key="synchronization_status_monitor.estimation.title"/></h3>
  <table class="form" cellpadding="0" cellspacing="0">
    <tbody>
    <tr>
      <td class="label">
        <fmt:message key="synchronization_status_monitor.top.table.header.estimated.size"/>
      </td>
      <td>
        <c:out value="${syncTaskHistoryInfo.estimatedSize}"/>
        <fmt:message key="synchronization_status_monitor.top.table.header.MB"/>
      </td>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="synchronization_status_monitor.estimation.indexing_duration"/>
      </td>
      <td>
        <fmt:message key="synchronization_status_monitor.top.table.header.sync.time_remaining.time" >
          <fmt:param value="${syncTaskHistoryInfo.timeLeftInSeconds / 3600}"/>
          <fmt:param value="${(syncTaskHistoryInfo.timeLeftInSeconds % 3600)/60}"/>
          <fmt:param value="${(syncTaskHistoryInfo.timeLeftInSeconds % 3600)%60}"/>
        </fmt:message>
      </td>
    </tr>
    <tr>
      <td class="label">
          <fmt:message key="synchronization_status_monitor.estimation.partitioning_plan.title"/>
      </td>
      <td><fmt:message key="synchronization_status_monitor.estimation.partitioning_plan.text"/></td>
    </tr>
    <tr>
      <td></td>
      <td>
        <%--Partitioning Plan table --%>
        <admin-ui:table renderer="/templates/table_data.jsp" tableId="partitioningPlanTableId" modelVar="table"
                        var="partitioningPlanBean" items="${syncTaskHistoryInfo.partitionPlanBeans}">
          <admin-ui:column title="synchronization_status_monitor.estimation.partitioning_plan.table.header.content_set"
                           type="notsortable" name="content_set" width="25%">
            <c:out value="${partitioningPlanBean.contentSetName}"/>
          </admin-ui:column>
          <admin-ui:column title="synchronization_status_monitor.estimation.partitioning_plan.table.header.estimated_size"
                           type="notsortable" name="estimated_size" width="25%">
            <fmt:formatNumber value="${partitioningPlanBean.estimatedSizeMB}" maxFractionDigits="2" minFractionDigits="2"/>
            <fmt:message key="synchronization_status_monitor.top.table.header.MB"/>
          </admin-ui:column>
          <admin-ui:column title="synchronization_status_monitor.estimation.partitioning_plan.table.header.min_pp"
                           type="notsortable" name="min_pp" width="25%">
            <c:out value="${partitioningPlanBean.minRecommendedPhysicalPartitions}"/>
          </admin-ui:column>
          <admin-ui:column title="synchronization_status_monitor.estimation.partitioning_plan.table.header.pp"
                           type="notsortable" name="pp" width="25%"
                           messageEA="embedded_assistant.sync_sm_estimation.phisical_partitions">
            <input name="physicalPartition" id="${partitioningPlanBean.logicalPartitionId}"
                   type="text" class="textField" size="2" value="${partitioningPlanBean.requestedPhysicalPartitions}"
                   onkeyup="syncStatusMonitor.updateDeplPlan(${syncTaskHistoryInfo.syncTaskId}, ${partitioningPlanBean.logicalPartitionId});"/>
          </admin-ui:column>
        </admin-ui:table>
        <script type="text/javascript">
          // TODO JS strings should probably be escaped
          syncStatusMonitor.physicalPartitionError = '<fmt:message key="synchronization_status_monitor.estimation.physicalPartition.error_message"/>';
        </script>
      </td>
    </tr>
    <%-- Deployment Plan--%>
    <tr>
      <td class="label">
        <fmt:message key="synchronization_status_monitor.estimation.deployment_plan.title"/>
      </td>
      <td>
        <fmt:message key="synchronization_status_monitor.estimation.deployment_plan.min_seng_se"/>
        <c:out value="${syncTaskHistoryInfo.searchEnginesRequired}"/>
      </td>
    </tr>
    <tr>
      <td></td>
      <td><fmt:message key="synchronization_status_monitor.estimation.deployment_plan.text"/></td>
    </tr>
    <tr>
      <td></td>
      <td id="output" dojoType="dojox.layout.ContentPane" executeScripts="true" scriptSeparation="false" cacheContent="false"
          loadingMessage="<fmt:message key='tree.dojo.loading'/>">
        <c:import url="sync_sm_deployment_plan.jsp" charEncoding="${charEncoding}">
          <c:param name="syncTaskId" value="${syncTaskHistoryInfo.syncTaskId}" />
        </c:import>
      </td>
    </tr>
  </table>
  <script>
    if (dijit.byId('assistanceBase')) { 
      dijit.byId('assistanceBase').buildEA();
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_sm_estimation.jsp#1 $$Change: 651360 $--%>
