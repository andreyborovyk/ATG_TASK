<%--
Synchronization history page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_history.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId"/>

  <script type="text/javascript">
    function doSelect(checkValue) {
      setChildCheckboxesState('itemsTable', 'taskIdCheckBox', checkValue);
      var button = document.getElementById('deleteSelected');
      if (button) {
        button.disabled = !checkValue;
      }
      return false;
    }
    function doSelectItem() {
      document.getElementById('itemsAll').checked = getChildCheckboxesState('itemsTable', 'taskIdCheckBox');
      checkButton();
    }
    function doDeleteSelected(redirectUrl) {
      var taskIdCheckBoxes = document.getElementsByName('taskIdCheckBox');
      if (taskIdCheckBoxes && taskIdCheckBoxes.length) {
        var ids = "";
        for (var i = 0; i < taskIdCheckBoxes.length; i++) {
          if (taskIdCheckBoxes[i].checked) {
            ids = ids + taskIdCheckBoxes[i].value + ",";
          }
        }
        if (ids != "") {
          var popupUrl = redirectUrl + "&syncTaskIds=" + ids.substring(0,ids.length-1);
          showPopUp(popupUrl);
        }
      }
      return false;
    }
    function checkButton() {
      var taskIdCheckBoxes = document.getElementsByName('taskIdCheckBox');
      if (taskIdCheckBoxes && taskIdCheckBoxes.length) {
        var disabled = true;
        for (var i = 0; i < taskIdCheckBoxes.length; i++) {
          if (taskIdCheckBoxes[i].checked) {
            disabled = false;
            break;
          }
        }
        var button = document.getElementById('deleteSelected');
        button.disabled = disabled;
      }
      return false;
    }
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

  <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationHistoryFormHandler"
                var="synchronizationHistoryFormHandler"/>
  
  <admin-ui:initializeFormHandler handler="${synchronizationHistoryFormHandler}">
    <admin-ui:param name="projectId" value="${projectId}"/>
  </admin-ui:initializeFormHandler>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form formid="history" method="post" action="synchronization_history.jsp">

      <div id="paneContent">
        <h3><fmt:message key="synchronization_history.index_cleanup_rules"/></h3>

        <p><span id="minInactiveIndexesAlert"></span><fmt:message key="synchronization_history.minimum_inactive_indexes"/>
          <d:input bean="SynchronizationHistoryFormHandler.minInactiveIndexes"
                   type="text" id="minInactiveIndexes" iclass="textField number small" name="minInactiveIndexes"/>
          <fmt:message key="synchronization_history.keep_at_least"/>
        </p>

        <p>
          <fmt:message key="synchronization_history.minimum_number"/>
        </p>
        <table class="formsmall">
          <tr>
            <td class="rowSelector">
              <d:input name="NMostRecentlySyncCheckBox" id="NMostRecentlySyncCheckBox" type="checkbox"
                       bean="SynchronizationHistoryFormHandler.NMostRecentlySyncCheckBox" />
            </td>
            <td>
              <span id="nMostRecentlySyncAlert"></span>
              <fmt:message key="synchronization_history.index_is_one"/>
              <d:input name="NMostRecentlySync" type="text" id="NMostRecentlySync"
                       iclass="textField number small" bean="SynchronizationHistoryFormHandler.NMostRecentlySync"/>
              <fmt:message key="synchronization_history.most_recently"/>
            </td>
          </tr>
          <tr>
            <td class="rowSelector">
              <d:input name="syncLastnDaysCheckBox" type="checkbox" id="syncLastnDaysCheckBox"
                       bean="SynchronizationHistoryFormHandler.syncLastnDaysCheckBox"/>
            </td>
            <td>
              <span id="syncLastnDaysAlert"></span>
              <fmt:message key="synchronization_history.index_was_synchronized"/>
              <d:input bean="SynchronizationHistoryFormHandler.syncLastnDays"
                       name="syncLastnDays" type="text" id="syncLastnDays" iclass="textField number small"/>
              <fmt:message key="synchronization_history.days"/>
            </td>
          </tr>
        </table>
        
        <h3><fmt:message key="synchronization_history.index_history"/></h3>
        
        <d:getvalueof bean="SynchronizationHistoryFormHandler.sortValue" var="sortValue"/>

        <admin-beans:getSyncTaskHistoryInfoList projectId="${projectId}" var="syncTaskHistoryInfoList"/>
        <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.syncTaskHistoryComparator" var="comparator"/>
        <admin-ui:sort var="sortedSyncTaskHistoryInfoList" items="${syncTaskHistoryInfoList}" comparator="${comparator}" sortMode="${sortValue}"/>

        <admin-ui:paging var="preparedSyncTaskHistoryInfoList" items="${sortedSyncTaskHistoryInfoList}"
                page="${synchronizationHistoryFormHandler.currentPage}" itemsPerPage="${synchronizationHistoryFormHandler.itemsPerPage}"
                varCurrentPage="currentPage" varTotalPages="totalPages" />

        <d:input bean="SynchronizationHistoryFormHandler.paging" type="submit" id="pagingButton"
               style="display:none" iclass="formsubmitter" name="paging"/>
        <d:input bean="SynchronizationHistoryFormHandler.currentPage" type="hidden"
               name="currentPage" id="currentPageHidden" />
        <br/>
        <d:include page="/templates/paging.jsp">
          <d:param name="totalItems" value="${fn:length(syncTaskHistoryInfoList)}" />
          <d:param name="currentPage" value="${currentPage}" />
          <d:param name="totalPages" value="${totalPages}" />
          <d:param name="itemsPerPage" value="${synchronizationHistoryFormHandler.itemsPerPage}" />
          <d:param name="onPage" value="doPaging" />
        </d:include>
        
        <fmt:message var="checkboxToolTip" key="synchronization_history.checkbox.tooltip"/>
        <c:set var="itemsHeaderContentValue">
          <input style="margin-left:2px;" type="checkbox" id="itemsAll" class="selectAll"
                 onclick="doSelect(this.checked);" title="${checkboxToolTip}"/>
        </c:set>

        <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                        var="syncTaskHistoryInfo" items="${preparedSyncTaskHistoryInfoList}"
                        sort="${sortValue}" onSort="tableOnSort" tableId="itemsTable">
          <admin-ui:column type="checkbox" headerContent="${itemsHeaderContentValue}">
            <d:input bean="SynchronizationHistoryFormHandler.taskIdCheckBox" value="${syncTaskHistoryInfo.syncTaskId}"
                     name="taskIdCheckBox" type="checkbox" id="${syncTaskHistoryInfo.syncTaskId}_input"
                     onclick="doSelectItem();"/>
          </admin-ui:column>
           
          <admin-ui:column title="synchronization_history.table.header.sync_task" type="sortable" name="task_name">
            <fmt:message key="synchronization_history.table.view" var="tooltipViewContentSummary"/>
            <c:url var="summaryUrl" value="/searchadmin/content_summary_view_popup.jsp"/>
            <d:a iclass="link" title="${tooltipViewContentSummary}" href="${summaryUrl}"
                name="${syncTaskHistoryInfo.currentRowInTable ? 'activeTask' : ''}"
                onclick="return showPopUp(this.href);">
               <d:param name="projectId" value="${projectId}"/>
               <d:param name="syncTaskId" value="${syncTaskHistoryInfo.syncTaskId}"/>
               <c:out value="${syncTaskHistoryInfo.syncTaskName}"/>
            </d:a>
          </admin-ui:column>

          <admin-ui:column title="synchronization_history.table.header.start" type="sortable" name="task_start">
            <fmt:message var="timeFormat" key="timeFormat"/>
            <fmt:formatDate value="${syncTaskHistoryInfo.currentStartDate}" pattern="${timeFormat}"/>
          </admin-ui:column>

          <admin-ui:column title="synchronization_history.table.header.stop" type="sortable" name="task_end">
            <fmt:message var="timeFormat" key="timeFormat"/>
            <fmt:formatDate value="${syncTaskHistoryInfo.currentEndDate}" pattern="${timeFormat}"/>
          </admin-ui:column>

          <admin-ui:column title="synchronization_history.table.header.status" type="sortable" name="task_status">
            <fmt:message key="synchronization_status_monitor.indexing_status.${syncTaskHistoryInfo.currentStatus}"/>
          </admin-ui:column>

          <admin-ui:column title="synchronization_history.table.header.errors" type="sortable" name="task_errors">
            <c:if test="${!syncTaskHistoryInfo.actionDisable}">
              <c:out value="${syncTaskHistoryInfo.currentErrors}"/>
            </c:if>
          </admin-ui:column>

          <admin-ui:column title="synchronization_history.table.header.c_items" type="sortable" name="task_citems">
            <c:if test="${!syncTaskHistoryInfo.actionDisable}">
              <c:out value="${syncTaskHistoryInfo.currentCItemCount}"/>
            </c:if>
          </admin-ui:column>

          <admin-ui:column title="synchronization_history.table.header.size" type="sortable" name="task_size">
            <c:if test="${!syncTaskHistoryInfo.actionDisable}">
              <c:choose>
                <c:when test="${syncTaskHistoryInfo.currentStatus eq 'Cancelled' or syncTaskHistoryInfo.currentStatus eq 'Failed'}">
                  <fmt:message key="synchronization_index_summary.constant.na" />
                </c:when>
                <c:otherwise>
                  <c:out value="${syncTaskHistoryInfo.currentSize}"/>
                </c:otherwise>
              </c:choose>
            </c:if>
          </admin-ui:column>

          <admin-ui:column title="synchronization_history.table.header.deployed_to" type="sortable" name="task_deployedto">
            <fmt:message key="synchronization_history.table.tooltip.deployed_to" var="tooltipDeployedTo"/>
            <c:forEach items="${syncTaskHistoryInfo.currentDeployedTo}" var="item" varStatus="index">
              <%-- Search environment has been deleted - this is valid thing that can happen.--%>
              <c:catch var="ex">
                <c:choose>
                  <c:when test="${syncTaskHistoryInfo.currentActive}">
                    <c:url var="envUrl" value="/searchadmin/search_env_configure_hosts.jsp" >
                      <c:param name="projectId" value="${projectId}"/>
                      <c:param name="environmentId" value="${item.id}"/>
                    </c:url>
                    <a href="${envUrl}" title="${tooltipDeployedTo}" onclick="return loadRightPanel(this.href);">
                      <c:out value="${item.envName}"/>
                    </a>
                    <c:if test="${!index.last}">
                      <br/>
                    </c:if>
                  </c:when>
                  <c:otherwise>
                    <c:out value="${item.envName}"/>
                    <c:if test="${!index.last}">
                      <br/>
                    </c:if>
                  </c:otherwise>
                </c:choose>
              </c:catch>
            </c:forEach>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <c:if test="${adminfunctions:hasIndexingLog(syncTaskHistoryInfo.syncTaskId)}">
              <fmt:message key="synchronization_history.table.view.icon.tooltip" var="viewIconToolTip"/>
              <d:a iclass="icon propertyViewSyncLog" title="${viewIconToolTip}"
                  href="${pageContext.request.contextPath}/searchadmin/sync_error_log.jsp"
                  onclick="return openSeparateWindow(this.href);">
                <d:param name="projectId" value="${projectId}" />
                <d:param name="syncTaskId" value="${syncTaskHistoryInfo.syncTaskId}" />
                sync
              </d:a>
            </c:if>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <admin-beans:getSyncTask taskId="${syncTaskHistoryInfo.syncTaskId}" var="currentTask" />
            <c:if test="${syncTaskHistoryInfo.syncTaskType != 'Estimate Index Size' && !syncTaskHistoryInfo.actionDisable && syncTaskHistoryInfo.currentStatus ne 'Cancelled' and syncTaskHistoryInfo.currentStatus ne 'Failed'}">
              <c:if test="${not empty currentTask.index}">
                <fmt:message key="synchronization_history.table.rollback.icon.tooltip" var="rollbackIconToolTip"/>
                <c:url value="/searchadmin/synchronization_rollback_index_popup.jsp" var="rollbackUrl">
                  <c:param name="syncTaskId" value="${syncTaskHistoryInfo.syncTaskId}"/>
                  <c:param name="projectId" value="${projectId}"/>
                </c:url>
                <a class="icon propertyRestoreIndex" title="${rollbackIconToolTip}" href="${rollbackUrl}"
                     onclick="return showPopUp(this.href);">indx</a>
              </c:if>
            </c:if>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <c:if test="${!syncTaskHistoryInfo.actionDisable}">
              <fmt:message key="synchronization_history.table.delete.icon.tooltip" var="deleteIconToolTip"/>
              <c:url value="/searchadmin/synchronization_delete_index_popup.jsp" var="deleteUrl">
                  <c:param name="syncTaskId" value="${syncTaskHistoryInfo.syncTaskId}"/>
                  <c:param name="projectId" value="${projectId}"/>
              </c:url>
              <a class="icon propertyDelete" title="${deleteIconToolTip}" href="${deleteUrl}"
                   onclick="return showPopUp(this.href);">del</a>
            </c:if>
          </admin-ui:column>
          
          <admin-ui:footer>
            <fmt:message var="buttonDelete" key="synchronization_history.button.delete"/>
            <fmt:message var="buttonDeleteToolTip" key="synchronization_history.button.delete.tooltip"/>
            <c:url value="/searchadmin/synchronization_delete_selected_index_popup.jsp" var="popUrl">
              <c:param name="projectId" value="${projectId}"/>
            </c:url>
            <span style="float:left;">
              <input type="button" id="deleteSelected" disabled="true"
                     class="tableButton" value="${buttonDelete}" title="${buttonDeleteToolTip}"
                     onclick="doDeleteSelected('${popUrl}')"/>
            </span>
          </admin-ui:footer>
        </admin-ui:table>
        <d:include page="/templates/paging.jsp">
          <d:param name="totalItems" value="${fn:length(syncTaskHistoryInfoList)}" />
          <d:param name="currentPage" value="${currentPage}" />
          <d:param name="totalPages" value="${totalPages}" />
          <d:param name="itemsPerPage" value="${synchronizationHistoryFormHandler.itemsPerPage}" />
          <d:param name="onPage" value="doPaging" />
        </d:include>
      </div>

      <c:url var="successURL" value="/searchadmin/synchronization_history.jsp">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>
      <script type="text/javascript">
        function tableOnSort(tableId, columnName, sortDirection){
          document.getElementById('sortInput').value = columnName + " " + sortDirection;
          document.getElementById('sortButton').click();
        }
      </script>
      <d:input type="submit" bean="SynchronizationHistoryFormHandler.sort" style="display:none" id="sortButton" iclass="formsubmitter"/>
      <d:input type="hidden" bean="SynchronizationHistoryFormHandler.sortValue" id="sortInput" />
      <d:input type="hidden" bean="SynchronizationHistoryFormHandler.successURL" value="${successURL}"/>
      <d:input type="hidden" bean="SynchronizationHistoryFormHandler.errorURL" value="${successURL}"/>
      <d:input type="hidden" bean="SynchronizationHistoryFormHandler.projectId" value="${projectId}"/>

      <div id="paneFooter">
        <fmt:message var="buttonSave" key="synchronization_history.button.save_rules"/>
        <fmt:message var="buttonSaveToolTip" key="synchronization_history.button.save_rules.tooltip"/>
        <d:input bean="SynchronizationHistoryFormHandler.saveIndexCleanupRules"
                 type="submit" value="${buttonSave}" title="${buttonSaveToolTip}"
                 iclass="formsubmitter" onclick="return checkForm();"/>
      </div>

      <admin-validator:validate beanName="SynchronizationHistoryFormHandler"/>

    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_history.jsp#2 $$Change: 651448 $--%>
