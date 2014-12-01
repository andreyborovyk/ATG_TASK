<%--
  Search Environments Table page. Displays a table which contains of search environments of a particular project or list of
  unattached environments.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_environments_table.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="showDeleteColumn" var="showDeleteColumn"/>
  <d:getvalueof param="tableId" var="tableId"/>
  <%--This parameters are added for synchronization_manual.jsp --%>
  <d:getvalueof param="indexingHandler" var="indexingHandler"/>
  <d:getvalueof param="deploymentOnly" var="deploymentOnly"/>

  <c:if test="${not empty indexingHandler}">
    <d:importbean bean="${indexingHandler}" var="synchronizationManualFormHandler"/>
  </c:if>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/ManageProjectFormHandler" var="manageProjectFormHandler"/>
  
  <d:getvalueof bean="SortFormHandler.sortTables.${tableId}" var="sortValue"/>
  <c:if test="${empty sortValue}">
    <c:set var="sortValue" value="environment_name asc"/>
  </c:if>
  <d:input id="hiddenSortValue${tableId}" type="hidden" bean="SortFormHandler.sortTables.${tableId}" name="sortTables.${tableId}"/>
  <c:if test="${empty deploymentOnly}">
    <c:set var="deploymentOnly" value="false"/>
  </c:if>
  <admin-beans:getSearchEnvironments var="searchEnvironments" projectId="${projectId}" deploymentOnly="${deploymentOnly}"/>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.searchEnvironmentComparator" var="comparator"/>
  <admin-ui:sort var="sortedSearchEnvironments" items="${searchEnvironments}" comparator="${comparator}"
                 sortMode="${sortValue}"/>
  <admin-beans:getSearchEnvironments var="searchEnvDepl" projectId="${projectId}" deploymentOnly="true"/>
  <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                  var="searchEnvironment" items="${sortedSearchEnvironments}"
                  sort="${sortValue}" onSort="tableOnSort" tableId="${tableId}">
    <%-- get search environment status --%>
    <admin-beans:getSearchEnvironmentStatus var="statusBean" environmentId="${searchEnvironment.id}"/>
    <c:if test="${not empty synchronizationManualFormHandler}">
      <c:set var="disabledEnv" value="false"/>
      <c:set var="disabledEnvironments" value="${synchronizationManualFormHandler.disabledEnvironments}"/>
      <c:forEach items="${disabledEnvironments}" var="env">
        <c:if test="${searchEnvironment.id == env}">
          <c:set var="disabledEnv" value="true"/>
        </c:if>
      </c:forEach>
    </c:if>
    <%--check when an index is partially served--%>
    <admin-beans:isIndexServedBySearchEnvId searchEnvId="${searchEnvironment.id}" isIndexServed="isIndexServed"/>
    <%-- columns --%>
    <c:if test="${not empty indexingHandler}">
      <c:set var="itemsHeaderContentValue">
        <input style="margin-left:2px;" type="checkbox" id="environmentsAll" class="selectAll"
               onclick="setChildEnvCheckboxesState('${tableId}', 'searchEnvironmentsList', this.checked);"/>
      </c:set>
      <admin-ui:column type="checkbox" headerContent="${itemsHeaderContentValue}">
        <d:input bean="${indexingHandler}.searchEnvironmentsList" value="${searchEnvironment.id}"
                 name="searchEnvironmentsList" type="checkbox" id="${searchEnvironment.id}"
                 disabled="${disabledEnv}"
                 onclick="setAllEnvironmentsCheckbox();"/>
      </admin-ui:column>
    </c:if>
    <admin-ui:column title="search_environments_table.search.environment" type="sortable" name="environment_name">
      <fmt:message var="tooltip" key="search_environments_table.view.tooltip"/>
      <c:url var="envUrl" value="/searchadmin/search_env_configure_hosts.jsp">
        <c:if test="${!empty projectId}">
          <c:param name="projectId" value="${projectId}"/>
        </c:if>
        <c:param name="showType" value="${tableId eq 'unattachedSearchEnvTable' ? 'unattached' : 'projectEnv'}"/>
        <c:param name="environmentId" value="${searchEnvironment.id}"/>
      </c:url>

      <a href="${envUrl}" title="${tooltip}" onclick="return loadRightPanel(this.href);">
        <c:out value="${searchEnvironment.envName}"/>
      </a>
      <c:if test="${not empty indexingHandler and empty searchEnvironment.hosts}">
        &nbsp;(<fmt:message key="synchronization_manual.no_hosts_configured"/>)
      </c:if>
      <c:if test="${!isIndexServed}">
        <span class="error" title="<fmt:message key='error_message.not.enough.search.engines'/>"></span>
      </c:if>
    </admin-ui:column>
    <c:if test="${tableId ne 'unattachedSearchEnvTable'}">
      <admin-ui:column title="search_environments_table.primary" type="static" name="environment_type">
        <c:choose>
          <c:when test="${searchEnvironment.envType ne 'staging' and searchEnvironment.envType ne 'production'}">
            <%-- show nothing --%>
          </c:when>
          <c:when test="${empty showDeleteColumn}">
            <input type="radio" name="${tableId}_primaryEnv" value="${searchEnvironment.id}" disabled="true"
                   <c:if test="${not empty searchEnvironment.targetName}">checked="true"</c:if> />
          </c:when>
          <c:otherwise>
            <d:input type="radio" bean="ManageProjectFormHandler.primaryEnvironmentId" name="primaryEnvironmentId"
                     value="${searchEnvironment.id}" />
          </c:otherwise>
        </c:choose>
      </admin-ui:column>
    </c:if>
    <admin-ui:column title="search_environments_table.hosts" type="sortable" name="environment_hosts">
      <c:out value="${statusBean.hostCount}"/>
    </admin-ui:column>
    <admin-ui:column title="search_environments_table.status" type="sortable" name="environment_status">
      <c:choose>
        <c:when test="${statusBean.running}">
          <fmt:message var="statusValue" key="search_environments_table.status.running"/>
          <c:set var="statusClass" value="envRunning"/>
        </c:when>
        <c:when test="${statusBean.partiallyRunning}">
          <fmt:message var="statusValue" key="search_environments_table.status.partially"/>
          <c:set var="statusClass" value="envPartiallyRunning"/>
        </c:when>
        <c:when test="${statusBean.stopped}">
          <fmt:message var="statusValue" key="search_environments_table.status.stopped"/>
          <c:set var="statusClass" value="envStop"/>
        </c:when>
      </c:choose>
      <span class="${statusClass}">
        <c:out value="${statusValue}"/>
      </span>
    </admin-ui:column>
    <c:if test="${empty indexingHandler}">
      <admin-ui:column title="" type="" name="environment_actions" width="20%">
        <div id="environmentAction_<c:out value='${searchEnvironment.id}'/>">
          <c:if test="${!statusBean.indexing}">
            <fmt:message key="search_environments_table.wait" var="waitMessage"/>
            <fmt:message key="search_environments.stop_search_environment" var="stopTitleMessage"/>
            <fmt:message key="search_environments.start_search_environment" var="startTitleMessage"/>
            <c:choose>
              <c:when test="${statusBean.running}">
                <a href="#" title="${stopTitleMessage}"
                   onclick="return submitEnvironmentAction('stopButtonId','${searchEnvironment.id}','${waitMessage}')">
                  <fmt:message key="search_environments_table.action_stop"/>
                </a>
              </c:when>
              <c:when test="${statusBean.partiallyRunning}">
                <a href="#" title="${stopTitleMessage}"
                   onclick="return submitEnvironmentAction('stopButtonId','${searchEnvironment.id}','${waitMessage}')">
                  <fmt:message key="search_environments_table.action_stop"/>
                </a>
                -
                <a href="#" title="${startTitleMessage}"
                   onclick="return submitEnvironmentAction('startButtonId','${searchEnvironment.id}','${waitMessage}')">
                  <fmt:message key="search_environments_table.action_remaining"/>
                </a>
              </c:when>
              <c:when test="${statusBean.stopped and statusBean.environmentStartable}">
                <a href="#" title="${startTitleMessage}"
                   onclick="return submitEnvironmentAction('startButtonId','${searchEnvironment.id}','${waitMessage}')">
                  <fmt:message key="search_environments_table.action_start"/>
                </a>
              </c:when>
              <c:when test="${statusBean.stopped}">
                <span title="<fmt:message key="search_environments_table.action_cant_start_title"/>">
                  <fmt:message key="search_environments_table.action_start"/>
                </span>
              </c:when>
            </c:choose>
          </c:if>
        </div>
      </admin-ui:column>
    </c:if>
    <c:if test="${not empty showDeleteColumn}">
      <admin-ui:column type="icon">
        <c:if test="${(((searchEnvironment.envType eq 'staging') or (searchEnvironment.envType eq 'production')) and
                (searchEnvironment.id ne manageProjectFormHandler.primaryEnvironmentId)) or
                (tableId eq 'unattachedSearchEnvTable')}">
          <fmt:message var="tooltipRemove" key="search_environments_table.remove.tooltip">
            <fmt:param><fmt:message key="search_environment.type.${searchEnvironment.envType}" /></fmt:param>
          </fmt:message>
          <c:url value="/searchadmin/search_env_delete_popup.jsp" var="popUrl">
            <c:if test="${!empty projectId}">
              <c:param name="projectId" value="${projectId}"/>
            </c:if>
            <c:param name="environmentId" value="${searchEnvironment.id}"/>
          </c:url>
          <a href="${popUrl}" class="icon propertyRemove" title="${tooltipRemove}"
             onclick="return showPopUp(this.href);">rem</a>
        </c:if>
      </admin-ui:column>
    </c:if>
  </admin-ui:table>

  <script type="text/javascript">
    function tableOnSort(pTableId, pColumnName, pSortDirection) {
      document.getElementById('hiddenSortValue' + pTableId).value = pColumnName + " " + pSortDirection;
      document.getElementById('sortInput').click();
    }
    function submitEnvironmentAction(pButtonId, pEnvId, pWaitMessage) {
      document.getElementById('environmentAction_' + pEnvId).innerHTML = pWaitMessage;
      document.getElementById('environmentId').value = pEnvId;
      document.getElementById(pButtonId).click();
      return false;
    }
    
    function setAllEnvironmentsCheckbox(){
      document.getElementById('environmentsAll').checked = getChildEnvCheckboxesState('${tableId}', 'searchEnvironmentsList');
    }
    <c:if test="${not empty indexingHandler}">
      setAllEnvironmentsCheckbox();
    </c:if>
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_environments_table.jsp#2 $$Change: 651448 $--%>
