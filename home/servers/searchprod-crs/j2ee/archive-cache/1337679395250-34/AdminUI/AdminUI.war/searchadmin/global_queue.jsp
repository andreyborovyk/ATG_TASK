<%--
  Global Indexing queue page. Displays table of queued indexing tasks.
  --%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <%-- Define sorting property --%>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>
  <d:getvalueof bean="SortFormHandler.sortTables.queuedTasks" var="sortValue"/>
  <c:if test="${empty sortValue}">
    <c:set var="sortValue" value="queued_task_qtime asc"/>
  </c:if>
  
  <admin-beans:getQueuedTasks var="queuedTasks"/>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.QueuedTaskComparator" var="comparator"/>
  <admin-ui:sort var="sortedQueuedTasks" items="${queuedTasks}" comparator="${comparator}" sortMode="${sortValue}"/>
  
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <br/>
      <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                  var="queuedTask" items="${sortedQueuedTasks}"
                  sort="${sortValue}" onSort="tableOnSort" tableId="queuedTasks">
        <admin-ui:column type="sortable" title="global_queue.table.project" name="queued_task_project">
          <c:url value="/searchadmin/project.jsp" var="projectUrl">
            <c:param name="projectId" value="${queuedTask.projectId}"/>
          </c:url>
          <a href="${projectUrl}" onclick="return loadRightPanel(this.href);">
            <c:out value="${queuedTask.projectName}"/>
          </a>
        </admin-ui:column>
        <admin-ui:column type="static" title="global_queue.table.content" name="queued_task_content">
          <c:if test="${not queuedTask.allContent}">
            <tags:join items="${queuedTask.contentSets}" var="contentSet" delimiter=", ">
              ${contentSet.name}
            </tags:join>
          </c:if>
          <c:if test="${queuedTask.allContent}">
            <fmt:message key="global_queue.all.content"/>
          </c:if>
        </admin-ui:column>
        <admin-ui:column type="sortable" title="global_queue.table.type" name="queued_task_type">
          <c:out value="${queuedTask.indexType}"/>
        </admin-ui:column>
        <admin-ui:column type="sortable" title="global_queue.table.qtime" name="queued_task_qtime">
          <fmt:message var="timeFormat" key="timeFormat"/>
          <fmt:formatDate value="${queuedTask.queueTime}" type="both" pattern="${timeFormat}"/>
        </admin-ui:column>
        <admin-ui:column type="icon">
          <%-- Icon, used like link to popup to delete content set --%>
          <fmt:message key="global_queue.delete.task.tooltip" var="deleteQueuedTaskTitle"/>
          <c:url value="/searchadmin/global_queue_delete_popup.jsp" var="popUrl">
            <c:param name="queuedTaskId" value="${queuedTask.queuedTaskId}"/>
          </c:url>
          <a class="icon propertyDelete" title="${deleteQueuedTaskTitle}" href="${popUrl}" 
             onclick="return showPopUp(this.href);">rem</a>
        </admin-ui:column>
      </admin-ui:table>
      <d:form method="POST" action="global_queue.jsp">
        <d:input id="hiddenQueuedTasksSortValue" type="hidden" bean="SortFormHandler.sortTables.queuedTasks"/>
        <d:input type="submit" bean="SortFormHandler.sort" iclass="formsubmitter" style="display:none" id="sortQueuedTaskInput"/>
      </d:form>
    </div>
  </div>
  
  <script type="text/javascript">
    function tableOnSort(tableId, columnName, sortDirection) {
      var sortFieldValue = document.getElementById('hiddenQueuedTasksSortValue');
      if (sortFieldValue) {
        sortFieldValue.value = columnName + " " + sortDirection;
      }
      var sortButton = document.getElementById('sortQueuedTaskInput');
      sortButton.click();
    }
    function customLoad() {
      alerting.showStoredMessages();
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/global_queue.jsp#2 $$Change: 651448 $--%>
