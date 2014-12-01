<%--
  Search environment configure hosts JSP. Allows to configure search environment settings.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_configure_hosts.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="environmentId" var="environmentId"/>
  <d:getvalueof param="projectId" var="projectId"/>

  <%--check when an index is partially served--%>
  <admin-beans:isIndexServedBySearchEnvId searchEnvId="${environmentId}"/>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SearchEnvironmentFormHandler"/>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SearchHostFormHandler"/>
  <admin-beans:getSearchEnvironment var="searchEnvironment" environmentId="${environmentId}"/>
  <c:set var="environmentType" value="${searchEnvironment.envType}"/>
  <%-- form handler initialization --%>
  <d:getvalueof bean="/atg/searchadmin/adminui/formhandlers/SearchEnvironmentFormHandler" var="handler"/>
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="environmentId" value="${environmentId}"/>
  </admin-ui:initializeFormHandler>
  <d:include page="search_env_tab_control.jsp">
    <d:param name="active" value="third"/>
  </d:include>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:url var="configureHostsURL" value="/searchadmin/search_env_configure_hosts.jsp">
      <c:if test="${!empty projectId}">
        <c:param name="projectId" value="${projectId}"/>
      </c:if>
      <c:param name="environmentId" value="${environmentId}"/>
    </c:url>
      
    <d:form action="${configureHostsURL}" method="post">
      <div id="paneContent">
        <p>
          <fmt:message key="search_env_configure_hosts.message"/>
        </p>
        <h3>
          <fmt:message key="search_env_configure_hosts.general"/>
        </h3>

        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td width="40%" class="label">
                <span id="searchEnvironmentNameAlert">
                  <span class="required">
                    <fmt:message key="project_search_envs_new_search_env.required_field"/>
                  </span>
                </span>
                <label for="searchEnvironmentName">
                  <fmt:message key="project_search_envs_new_search_env.search_env_name_field"/>
                </label>
              </td>
              <td width="60%">
                <d:input type="text" id="searchEnvironmentName" name="searchEnvironmentName"
                         bean="SearchEnvironmentFormHandler.searchEnvironmentName"
                         iclass="textField"/>
              </td>
            </tr>
            <tr>
              <td class="label">
                <fmt:message key="project_search_envs_new_search_env.search_env_type"/>
              </td>
              <td>
                <c:if test="${!empty projectId}">
                  <admin-beans:getSearchEnvironment var="searchEnvironment" environmentId="${environmentId}"/>
                  <fmt:message key="search_environment.type.${searchEnvironment.envType}"/>
                </c:if>
                <c:if test="${empty projectId}">
                  <fmt:message key="search_environment.type.default"/>
                </c:if>
              </td>
            </tr>
            <tr>
              <td class="label">
                <fmt:message key="project_search_envs_edit_search_env.search_env_status"/>
              </td>
              <td>
                <admin-beans:getSearchEnvironmentStatus var="statusBean" environmentId="${environmentId}"/>
                <fmt:message key="search_environments_table.wait" var="waitMessage"/>
                <div id="environmentAction">
                  <fmt:message key="search_environments.stop_search_environment" var="stopTitleMessage"/>
                  <fmt:message key="search_environments.start_search_environment" var="startTitleMessage"/>
                  <c:choose>
                    <c:when test="${statusBean.running}">
                      <span class="envRunning">
                        <fmt:message key="search_environments_table.status.running"/>
                      </span>
                      <c:if test="${!statusBean.indexing}">
                        <a href="#" title="${stopTitleMessage}"
                           onclick="return submitEnvironmentAction('stopButtonId','${searchEnvironment.id}','${waitMessage}')">
                          <fmt:message key="project_search_envs_edit_search_env.search_env_action_stop"/>
                        </a>
                      </c:if>
                    </c:when>
                    <c:when test="${statusBean.partiallyRunning}">
                      <span class="envPartiallyRunning">
                        <fmt:message key="search_environments_table.status.partially"/>
                      </span>
                      <c:if test="${!statusBean.indexing}">
                        <a href="#" title="${stopTitleMessage}"
                           onclick="return submitEnvironmentAction('stopButtonId','${searchEnvironment.id}','${waitMessage}')">
                          <fmt:message key="project_search_envs_edit_search_env.search_env_action_stop"/>
                        </a>
                        -
                        <a href="#" title="${startTitleMessage}"
                           onclick="return submitEnvironmentAction('startButtonId','${searchEnvironment.id}','${waitMessage}')">
                          <fmt:message key="project_search_envs_edit_search_env.search_env_action_remaining"/>
                        </a>
                      </c:if>
                    </c:when>
                    <c:when test="${statusBean.stopped}">
                      <span class="envStop">
                        <fmt:message key="search_environments_table.status.stopped"/>
                      </span>
                      <c:if test="${!statusBean.indexing}">
                        <a href="#" title="${startTitleMessage}"
                           onclick="return submitEnvironmentAction('startButtonId','${searchEnvironment.id}','${waitMessage}')">
                          <fmt:message key="project_search_envs_edit_search_env.search_env_action_start"/>
                        </a>
                      </c:if>
                    </c:when>
                  </c:choose>
                </div>
              </td>
            </tr>
          <tbody>
        </table>

        <h3>
          <fmt:message key="search_env_configure_hosts.host.machines"/>
        </h3>

        <p>
          <fmt:message key="search_env_configure_hosts.table.description"/>
        </p>

        <d:getvalueof bean="SearchHostFormHandler.sort" var="sortValue"/>

        <d:getvalueof bean="SearchEnvironmentFormHandler.hostBeans" var="hostBeans"/>
        <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.searchHostComparator" var="comparator"/>
        <admin-ui:sort var="sortedSearchEnvironmentHosts" items="${hostBeans}" comparator="${comparator}"
                       sortMode="${sortValue}"/>

        <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table" tableId="hostMachineTable"
                        var="searchEnvironmentHost" items="${hostBeans}"
                        sort="${sortValue}" onSort="tableOnSort" emptyRow="true">
          <c:set var="columnWidth" value="8%"/>
          <admin-ui:column title="search_env_configure_hosts.table.machine.name" type="sortable" name="host_name">
            <d:input bean="SearchEnvironmentFormHandler.id" value="${searchEnvironmentHost.id}" name="id" type="hidden"/>
            <d:input bean="SearchEnvironmentFormHandler.existHost" value="${searchEnvironmentHost.existHost}" name="existHost"
                     type="hidden"/>
            <c:if test="${!searchEnvironmentHost.existHost}">
              <d:input bean="SearchEnvironmentFormHandler.hostName" value="${searchEnvironmentHost.name}" name="hostName"
                       type="text" iclass="textField"
                       onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
            </c:if>
            <c:if test="${searchEnvironmentHost.existHost}">
              <d:input bean="SearchEnvironmentFormHandler.hostName" value="${searchEnvironmentHost.name}" name="hostName"
                       type="hidden"/>

              <fmt:message key="search_env_configure_hosts.table.machine.name.tooltip" var="hostMashineToolTip"/>
              <c:url var="searchEnvUrl" value="/searchadmin/search_env_host_machine_config.jsp"/>
              <d:a href="${searchEnvUrl}" title="${hostMashineToolTip}" onclick="return loadRightPanel(this.href);">
                <c:if test="${!empty projectId}">
                  <d:param name="projectId" value="${projectId}"/>
                </c:if>
                <d:param name="environmentId" value="${environmentId}"/>
                <d:param name="hostId" value="${searchEnvironmentHost.id}"/>
                <c:out value="${searchEnvironmentHost.name}"/>
              </d:a>
            </c:if>
          </admin-ui:column>
          <c:if test="${not empty projectId}">
            <admin-ui:column title="search_env_configure_hosts.table.content.sets" type="static" name="content_sets">
              <c:set var="contentSetNames" value="${searchEnvironmentHost.contentSetNames}" />
              <c:set var="contentSetIds" value="${searchEnvironmentHost.contentSetIds}" />
              <c:url var="selectSetsUrl" value="/searchadmin/search_environments_host_restrict_popup.jsp" >
                <c:param name="projectId" value="${projectId}"/>
                <c:param name="environmentId" value="${environmentId}"/>
                <c:param name="checkedItems" value="${contentSetIds}"/>
              </c:url>
              <c:if test="${not empty contentSetNames}">
                <c:set var="selectedSets" value="${contentSetNames}"/>
              </c:if>
              <c:if test="${empty contentSetNames}">
                <fmt:message var="selectedSets" key="search_env_configure_hosts.table.content.sets.select.empty"/>
              </c:if>
              <a href="${selectSetsUrl}" onclick="selectedContentLink=this;return showPopUp(this.href);" id="selectSets">
                ${selectedSets}
              </a>
              <d:input bean="SearchEnvironmentFormHandler.contentSetNames" value="${contentSetNames}" name="contentSetNames"
                         type="hidden"/>
              <d:input bean="SearchEnvironmentFormHandler.contentSetIds" value="${contentSetIds}" name="contentSetIds"
                         type="hidden"/>
            </admin-ui:column>
          </c:if>
          <admin-ui:column title="search_env_configure_hosts.table.engine.count" type="sortable" name="host_count" width="${columnWidth}">
            <c:if test="${!searchEnvironmentHost.existHost}">
              <d:input bean="SearchEnvironmentFormHandler.numEngine" value="${searchEnvironmentHost.numEngines}" name="numEngine"
                       type="text" iclass="textField" size="2"
                       onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
            </c:if>
            <c:if test="${searchEnvironmentHost.existHost}">
              <d:input bean="SearchEnvironmentFormHandler.numEngine" value="${searchEnvironmentHost.numEngines}" name="numEngine"
                       type="text" iclass="textField" size="2"/>
            </c:if>
          </admin-ui:column>
          <admin-ui:column title="search_env_configure_hosts.table.cpu.count" type="sortable" name="cpu_count" width="${columnWidth}">
            <c:if test="${searchEnvironmentHost.cpuCount > 0}">
              <fmt:message var="cpuCountTooltip" key="search_env_configure_hosts.table.cpu.count.tooltip"/>
              <d:input bean="SearchEnvironmentFormHandler.cpuCount" value="${searchEnvironmentHost.cpuCount}" name="cpuCount"
                       type="text" iclass="textField" size="2"
                       title="${cpuCountTooltip}"/>
            </c:if>
            <c:if test="${searchEnvironmentHost.cpuCount == 0}">
              <d:input bean="SearchEnvironmentFormHandler.cpuCount" value="${searchEnvironmentHost.cpuCount}" name="cpuCount"
                       type="hidden"/>
            </c:if>
          </admin-ui:column>
          <admin-ui:column title="search_env_configure_hosts.table.engine.total.running" type="sortable"
                           name="host_total_running" width="${columnWidth}">
            <c:if test="${searchEnvironmentHost.existHost}">
              <c:out value="${searchEnvironmentHost.totalSearchEnginesRunning}"/>
            </c:if>
          </admin-ui:column>
          <admin-ui:column title="search_env_configure_hosts.table.engine.total" type="sortable" name="host_total" width="${columnWidth}">
            <c:if test="${!searchEnvironmentHost.existHost}">
              <fmt:message key="search_env_configure_hosts.table.engine.new"/>
            </c:if>
            <c:if test="${searchEnvironmentHost.existHost}">
              <c:out value="${searchEnvironmentHost.totalSearchEngines}"/>
            </c:if>
          </admin-ui:column>
          <admin-ui:column title="search_env_configure_hosts.table.hardware.utilization.current" type="notsortable"
                           name="currentUtilization" width="${columnWidth}">
            <c:choose>
              <c:when test="${searchEnvironmentHost.currentUtilization == -1}">
              </c:when>
              <c:when test="${searchEnvironmentHost.currentUtilization == 0}">
                <span title="<fmt:message key='search_env_configure_hosts.table.hardware.current.utilization.under.tooltip'/>" class="warning">
                  <fmt:message key="search_env_configure_hosts.table.hardware.utilization.under"/>
                </span>
              </c:when>
              <c:when test="${searchEnvironmentHost.currentUtilization == 1}">
                <span title="<fmt:message key='search_env_configure_hosts.table.hardware.current.utilization.full.tooltip'/>" class="okay">
                  <fmt:message key="search_env_configure_hosts.table.hardware.utilization.full"/>
                </span>
              </c:when>
              <c:otherwise>
                <span title="<fmt:message key='search_env_configure_hosts.table.hardware.current.utilization.over.tooltip'/>" class="exceeded">
                  <fmt:message key="search_env_configure_hosts.table.hardware.utilization.over" />
                </span>
              </c:otherwise>
            </c:choose>
          </admin-ui:column>
          <admin-ui:column title="search_env_configure_hosts.table.hardware.utilization.potential" type="notsortable"
                           name="potentialUtilization" width="${columnWidth}">
            <c:choose>
              <c:when test="${searchEnvironmentHost.potentialUtilization == -1}">
              </c:when>
              <c:when test="${searchEnvironmentHost.potentialUtilization == 0}">
                <span title="<fmt:message key='search_env_configure_hosts.table.hardware.potential.utilization.under.tooltip'/>" class="warning">
                  <fmt:message key="search_env_configure_hosts.table.hardware.utilization.under"/>
                </span>
              </c:when>
              <c:when test="${searchEnvironmentHost.potentialUtilization == 1}">
                <span title="<fmt:message key='search_env_configure_hosts.table.hardware.potential.utilization.full.tooltip'/>" class="okay">
                  <fmt:message key="search_env_configure_hosts.table.hardware.utilization.full"/>
                </span>
              </c:when>
              <c:otherwise>
                <span title="<fmt:message key='search_env_configure_hosts.table.hardware.potential.utilization.over.tooltip'/>" class="exceeded">
                  <fmt:message key="search_env_configure_hosts.table.hardware.utilization.over"/>
                </span>
              </c:otherwise>
            </c:choose>
          </admin-ui:column>
          <admin-ui:column type="icon" width="20">
            <fmt:message var="tooltipRemove" key="search_env_configure_hosts.remove.tooltip"/>
            <c:if test="${!searchEnvironmentHost.existHost}">
              <a class="icon propertyDelete" href="#" onclick="return deleteField(this);">del</a>
            </c:if>
            <c:if test="${searchEnvironmentHost.existHost}">
              <c:url value="/searchadmin/search_host_delete_popup.jsp" var="popUrl">
                <c:param name="projectId" value="${projectId}"/>
                <c:param name="environmentId" value="${environmentId}"/>
                <c:param name="hostId" value="${searchEnvironmentHost.id}"/>
              </c:url>
              <a class="icon propertyRemove" href="${popUrl}" title="${tooltipRemove}"
                   onclick="setDeletefield(this); return showPopUp(this.href);">rem</a>
            </c:if>
          </admin-ui:column>
        </admin-ui:table>
        <p>
          <fmt:message key="search_env_configure_hosts.table.hardware.utilization.note"/>
        </p>
        <c:if test="${(environmentType eq 'staging') or (environmentType eq 'production')}">
          <d:include page="policy_settings.jsp"/>
        </c:if>
      </div>

      <%--Hidden buttons for environment stop/start actions--%>
      <d:input bean="SearchEnvironmentFormHandler.stopEnvironment" type="submit" iclass="formsubmitter" id="stopButtonId" style="display:none"/>
      <d:input bean="SearchEnvironmentFormHandler.startEnvironment" type="submit" iclass="formsubmitter" id="startButtonId" style="display:none"/>
      <d:input type="submit" bean="SearchHostFormHandler.sort" iclass="formsubmitter" 
          style="display:none" name="sortInput" id="sortInput"/>

      <div id="paneFooter">
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.successURL" value="${configureHostsURL}" name="successUrl"/>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.errorURL" value="${configureHostsURL}" name="errorUrl"/>
        <d:input type="hidden" bean="SearchHostFormHandler.errorURL" value="${configureHostsURL}" name="errorUrl1"/>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.environmentId" value="${environmentId}" name="environmentId"/>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.needInitialization"
                 value="false" name="needInitialization"/>

        <fmt:message var="saveButton" key="search_env_configure_hosts.button"/>
        <fmt:message var="saveButtonToolTip" key="search_env_configure_hosts.button.tooltip"/>
        <d:input type="submit" value="${saveButton}" bean="SearchEnvironmentFormHandler.update"
                 title="${saveButtonToolTip}" iclass="formsubmitter"
                 onclick="return checkForm();" name="updateButton"/>
        <script type="text/javascript">
          initTable(document.getElementById("hostMachineTable"));
          var deleteRow;

          function setDeletefield(field) {
            deleteRow = field;
          }

          function beforeClosePopUp() {
            return deleteField(deleteRow);
          }

          function tableOnSort(tableId, columnName, sortDirection) {
            var sortButton = document.getElementById('sortInput');
            sortButton.value = columnName + " " + sortDirection;
            sortButton.click();
          }
          function submitEnvironmentAction(buttonId, envId, waitMessage) {
            document.getElementById('environmentAction').innerHTML = waitMessage;
            document.getElementById(buttonId).click();
            return false;
          }
          
          var selectedContentLink;
        </script>
      </div>
    </d:form>
  </div>
  <admin-validator:validate beanName="SearchEnvironmentFormHandler"/>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_configure_hosts.jsp#1 $$Change: 651360 $--%>
