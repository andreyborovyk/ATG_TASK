<%--
Index Audit page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_audit_select.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof var="projectId" param="projectId" />
  <d:getvalueof var="compareToIndexId" param="compareToIndexId" />
  <d:getvalueof var="showChangesOnly" param="showChangesOnly" />

  <d:importbean var="handler" bean="/atg/searchadmin/adminui/formhandlers/IndexAuditSelectFormHandler" />
  <c:if test="${empty handler.projectId}">
    <d:setvalue bean="IndexAuditSelectFormHandler.projectId" value="${projectId}"/>
    <d:setvalue bean="IndexAuditSelectFormHandler.compareToIndexId" value="${compareToIndexId}"/>
    <d:setvalue bean="IndexAuditSelectFormHandler.showChangesOnly" value="${showChangesOnly}"/>
  </c:if>
  <script type="text/javascript">
    function doPaging(pValue) {
      if (pValue) {
        var pagingHidden = document.getElementById('currentPageHidden');
        var v = parseInt(pValue);
        if (isNaN(v) || v <= 0) {
          v = 1;
        }
        pagingHidden.value = v;
      }
      var pagingButton = document.getElementById('pagingButton');
      pagingButton.click();
      return false;
    }
  </script>
  <d:form method="POST" action="project_index_audit_select.jsp">
    <div class="content">
      <d:input bean="IndexAuditSelectFormHandler.paging" type="submit" id="pagingButton"
               style="display:none" iclass="formsubmitter" name="paging"/>
      <d:input bean="IndexAuditSelectFormHandler.currentPage" type="hidden"
               name="currentPage" id="currentPageHidden" />
      <p><fmt:message key="project_index_audit_select.description" /></p>

      <admin-beans:getSyncTaskHistoryInfoList projectId="${handler.projectId}"
          var="syncTaskHistoryInfoList" indexAuditMode="true" />

      <admin-ui:paging var="pagedList" items="${syncTaskHistoryInfoList}"
            page="${handler.currentPage}" itemsPerPage="${handler.itemsPerPage}"
            varCurrentPage="currentPage" varTotalPages="totalPages" />
      <d:include page="/templates/paging.jsp">
        <d:param name="totalItems" value="${fn:length(syncTaskHistoryInfoList)}" />
        <d:param name="currentPage" value="${currentPage}" />
        <d:param name="totalPages" value="${totalPages}" />
        <d:param name="itemsPerPage" value="${handler.itemsPerPage}" />
        <d:param name="onPage" value="doPaging" />
      </d:include>

      <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                      var="syncTaskHistoryInfo" items="${pagedList}">
        <admin-ui:column type="checkbox">
          <c:if test="${empty handler.compareToIndexId}">
            <d:setvalue bean="IndexAuditSelectFormHandler.compareToIndexId" value="${syncTaskHistoryInfo.syncTaskId}"/>
          </c:if>
          <input value="${syncTaskHistoryInfo.syncTaskId}" name="compareToIndexIdOnUI" type="radio"
              id="compareToIndexIdOnUI.${syncTaskHistoryInfo.syncTaskId}"
              <c:if test="${syncTaskHistoryInfo.syncTaskId == handler.compareToIndexId}">checked=""</c:if>
              onclick="document.getElementById('compareToIndexIdField').value = this.value;" />
        </admin-ui:column>

        <admin-ui:column title="synchronization_history.table.header.sync_task.audit" type="text">
          <label for="compareToIndexIdOnUI.${syncTaskHistoryInfo.syncTaskId}">
            <c:out value="${syncTaskHistoryInfo.syncTaskName}"/>
          </label>
        </admin-ui:column>

        <admin-ui:column title="synchronization_history.table.header.stop.audit" type="text">
          <fmt:message var="timeFormat" key="timeFormat"/>
          <fmt:formatDate value="${syncTaskHistoryInfo.currentEndDate}" pattern="${timeFormat}"/>
        </admin-ui:column>

        <admin-ui:column title="synchronization_history.table.header.status" type="text">
          <admin-beans:getSyncTaskGeneralStatus var="syncTaskGeneralStatus" syncTaskId="${syncTaskHistoryInfo.syncTaskId}"/>
          <fmt:message key="synchronization_status_monitor.indexing_status.${syncTaskGeneralStatus}"/>
        </admin-ui:column>

        <admin-ui:column title="synchronization_history.table.header.errors" type="text">
          <c:if test="${!syncTaskHistoryInfo.actionDisable}">
            <c:out value="${syncTaskHistoryInfo.currentErrors}"/>
          </c:if>
        </admin-ui:column>

        <admin-ui:column title="synchronization_history.table.header.c_items" type="text">
          <c:if test="${!syncTaskHistoryInfo.actionDisable}">
            <c:out value="${syncTaskHistoryInfo.currentCItemCount}"/>
          </c:if>
        </admin-ui:column>

        <admin-ui:column title="synchronization_history.table.header.size" type="text">
          <c:if test="${!syncTaskHistoryInfo.actionDisable}">
            <c:out value="${syncTaskHistoryInfo.currentSize}"/>
          </c:if>
        </admin-ui:column>

        <admin-ui:column title="synchronization_history.table.header.deployed_to" type="text">
          <c:forEach items="${syncTaskHistoryInfo.currentDeployedTo}" var="item" varStatus="index">
            <c:out value="${item.envName}"/>
            <c:if test="${!index.last}">
              <br/>
            </c:if>
          </c:forEach>
        </admin-ui:column>
      </admin-ui:table>
      
      <d:include page="/templates/paging.jsp">
        <d:param name="totalItems" value="${fn:length(syncTaskHistoryInfoList)}" />
        <d:param name="currentPage" value="${currentPage}" />
        <d:param name="totalPages" value="${totalPages}" />
        <d:param name="itemsPerPage" value="${handler.itemsPerPage}" />
        <d:param name="onPage" value="doPaging" />
      </d:include>
    </div>
    <div class="footer" id="popupFooter">
      <d:input type="hidden" bean="IndexAuditSelectFormHandler.projectId" name="projectId" />
      <d:input bean="IndexAuditSelectFormHandler.compareToIndexId" name="compareToIndexId"
          type="hidden" id="compareToIndexIdField" />
      <d:input type="hidden" bean="IndexAuditSelectFormHandler.showChangesOnly" name="showChangesOnly" />

      <c:url var="errorUrl" value="/searchadmin/project_index_audit_select.jsp">
        <c:param name="projectId" value="${projectId}" />
        <c:param name="compareToIndexId" value="${compareToIndexId}" />
        <c:param name="showChangesOnly" value="${showChangesOnly}" />
      </c:url>
      <d:input type="hidden" bean="IndexAuditSelectFormHandler.errorURL" value="${errorUrl}" name="errorUrl"/>
      <c:url var="successUrl" value="/searchadmin/project_index_audit.jsp">
        <c:param name="projectId" value="${handler.projectId}" />
        <c:param name="showChangesOnly" value="${handler.showChangesOnly}" />
      </c:url>
      <d:input type="hidden" bean="IndexAuditSelectFormHandler.successURL" value="${successUrl}" name="successUrl"/>

      <fmt:message var="okButton" key="project_index_audit_select.ok" />
      <d:input type="submit" bean="IndexAuditSelectFormHandler.select" value="${okButton}" name="select" iclass="formsubmitter"/>
      <input type="button" value='<fmt:message key="project_index_audit_select.cancel" />'
          onclick="closePopUp()" />
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_audit_select.jsp#2 $$Change: 651448 $--%>
