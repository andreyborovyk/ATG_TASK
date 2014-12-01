<%--
  Search environment host machine config JSP. Allows to configure search host settings.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_host_machine_config.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="hostId" var="hostId"/>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="environmentId" var="environmentId"/>
  <d:getvalueof param="environmentType" var="environmentType"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SearchHostFormHandler"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
    executeScripts="true" cacheContent="false" scriptSeparation="false">
    <!--//TODO: update error URL-->
    <c:url var="errorURL" value="/searchadmin/search_env_host_machine_config.jsp">
      <c:if test="${!empty projectId}">
        <c:param name="projectId" value="${projectId}"/>
      </c:if>
      <c:param name="environmentId" value="${environmentId}"/>
      <c:param name="hostId" value="${hostId}"/>
    </c:url>

    <d:form action="${errorURL}" method="POST">
      <%-- form handler initialization --%>
      <d:getvalueof bean="/atg/searchadmin/adminui/formhandlers/SearchHostFormHandler" var="handler"/>
      <admin-ui:initializeFormHandler handler="${handler}">
        <admin-ui:param name="hostId" value="${hostId}"/>
      </admin-ui:initializeFormHandler>
      <c:url var="successURL" value="/searchadmin/search_env_configure_hosts.jsp">
        <c:if test="${!empty projectId}">
          <c:param name="projectId" value="${projectId}"/>
        </c:if>
        <c:param name="environmentId" value="${environmentId}"/>
        <c:param name="hostId" value="${hostId}"/>
      </c:url>

      <div id="paneContent">
        <c:set var="title">
          <fmt:message key="search_env_host_machine_config.description"/>
        </c:set>
        <c:if test="${ not empty title }">
          <p><fmt:message key="search_env_host_machine_config.description"/></p>
          <span class="seperator"></span>
        </c:if>
        <p><fmt:message key="search_env_host_also_used_by_table.description"/></p>

        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label"><fmt:message key="search_env_host_machine_config.maximum.number"/></td>
              <td>
                <label>
                  <d:input default="1" type="text" iclass="textField small" name="maximumSearchEngines"
                           bean="SearchHostFormHandler.maximumSearchEngines" maxlength="3"/>
                </label>
              </td>
              <td align="left">
                <fmt:message key="search_env_host_machine_config.maximum.number.message">
                </fmt:message>
              </td>
            </tr>
          </tbody>
        </table>

        <admin-beans:getProjectEnvHostName environmentId="${environmentId}" hostId="${hostId}" var="hostname"/>
        <admin-beans:getSearchEnvironmentsByHostName var="searchHosts" hostName="${hostname}"/>
        <table class="data" cellspacing="0" cellpadding="0">
          <thead>
            <tr>
              <th><fmt:message key="search_env_host_also_used_by_table.environment_name"/></th>
              <th><fmt:message key="search_env_host_also_used_by_table.num_engines"/></th>
              <th><fmt:message key="search_env_host_also_used_by_table.num_engines_running"/></th>
            </tr>
          </thead>
          <tbody>
            <c:set var="totalHostCount" value="0"/>
            <c:set var="totalRunningCount" value="0"/>
            <c:forEach items="${searchHosts}" var="currentHost">
              <c:set var="totalHostCount" value="${totalHostCount + currentHost.hostCount}"/>
              <c:set var="totalRunningCount" value="${totalRunningCount + currentHost.runningEngineCount}"/>
              <tr>
                <td><c:out value="${currentHost.environmentName}" /></td>
                <td><c:out value="${currentHost.hostCount}" /><br/></td>
                <td><c:out value="${currentHost.runningEngineCount}" /><br/></td>
              </tr>
            </c:forEach>
            <tr>
              <td><fmt:message key="search_env_host_also_used_by_table.total.colunm"/></td>
              <td><c:out value="${totalHostCount}"/></td>
              <td><c:out value="${totalRunningCount}"/></td>
            </tr>
          </tbody>
        </table>

        <h3>
          <fmt:message key="advanced_settings.head"/>
          <span id="advSettings:hideLink" class="headerLink" style="display:none">
            [<a href="#" onclick="return showHideSettings('advSettings', false);"
              title="<fmt:message key='advanced_setting.hide_link.tooltip'/>"><fmt:message key="advanced_setting.hide_link"/></a>]
          </span>
          <span id="advSettings:showLink" class="headerLink">
            [<a href="#" onclick="return showHideSettings('advSettings', true);"
              title="<fmt:message key='advanced_setting.show_link.tooltip'/>"><fmt:message key="advanced_setting.show_link"/></a>]
          </span>
        </h3>
        <script type="text/javascript">
          function setCheckBox(pParetnElement, pTargetCheckBox) {
            document.getElementById(pTargetCheckBox).checked = true;
            pParetnElement.focus();
            return true;
          }
        </script>

        <div id="advSettings" style="display:none">
          <table class="form" cellspacing="0" cellpadding="0">
            <tbody>
              <tr>
                <td><fmt:message key="search_env_host_machine_config.working.directory"/></td>
                <td>
                  <fmt:message key="search_env_host_machine_config.message">
                  </fmt:message>
                </td>
              </tr>
              <tr>
                <td class="label"></td>
                <td>
                  <d:input type="radio" name="defaultWorkingDirectory" id="defaultWorkingDirectory_true"
                             bean="SearchHostFormHandler.defaultWorkingDirectory" value="true"/>
                  <label for="defaultWorkingDirectory_true">
                    <d:getvalueof bean="SearchHostFormHandler.defaultWorkingDirectoryPath" var="defaultWorkingDirectoryPath"/>
                    <fmt:message key="search_env_host_machine_config.system.default.path">
                      <fmt:param value="${defaultWorkingDirectoryPath}"/>
                    </fmt:message>
                  </label>
                  <br/>
                  <d:input type="radio" name="defaultWorkingDirectory" id="defaultWorkingDirectory_false"
                           bean="SearchHostFormHandler.defaultWorkingDirectory" value="false"/>
                  <span id="workingDirectoryPathAlert"></span>
                  <fmt:message key="search_env_host_machine_config.path"/>
                  <d:input type="text" iclass="textField" name="workingDirectoryPath"
                           bean="SearchHostFormHandler.workingDirectoryPath" onfocus="setCheckBox(this, 'defaultWorkingDirectory_false');"/>
                </td>
              </tr>
              <tr>
                <td><fmt:message key="search_env_host_machine_config.port.numbers"/></td>
                <td><fmt:message key="search_env_host_machine_config.port.message"/></td>
              </tr>
              <tr>
                <td class="label"></td>
                <td>
                  <d:input type="radio" name="defaultPortNumbers" id="defaultPortNumber_true"
                           bean="SearchHostFormHandler.defaultPortNumbers" value="true"/>
                  <label for="defaultPortNumber_true">
                    <d:getvalueof bean="SearchHostFormHandler.defaultEndPort" var="defaultEndPort"/>
                    <d:getvalueof bean="SearchHostFormHandler.defaultStartPort" var="defaultStartPort"/>
                    <fmt:message key="search_env_host_machine_config.system.default.port"/>
                    <c:out value="${defaultStartPort}"/>
                    <fmt:message key="search_env_host_machine_config.system.default.dash"/>
                    <c:out value="${defaultEndPort}"/>
                  </label>
                  <br/>
                  <d:input type="radio" name="defaultPortNumbers" id="defaultPortNumbers_false"
                           bean="SearchHostFormHandler.defaultPortNumbers" value="false"/>
                  <span id="portRangeFromAlert"></span>
                  <fmt:message key="search_env_host_machine_config.range.from"/>
                  <d:input type="text" iclass="textField small" name="portRangeFrom"
                           bean="SearchHostFormHandler.portRangeFrom" onfocus="setCheckBox(this,'defaultPortNumbers_false');"/>
                  <span id="portRangeToAlert"></span>
                  <fmt:message key="search_env_host_machine_config.range.to"/>
                  <d:input type="text" iclass="textField small" name="portRangeTo"
                           bean="SearchHostFormHandler.portRangeTo" onfocus="setCheckBox(this,'defaultPortNumbers_false');"/>
                </td>
              </tr>
              <tr>
                <td><fmt:message key="search_env_host_machine_config.launch_service"/></td>
                <td><fmt:message key="search_env_host_machine_config.launch_service.message"/></td>
              </tr>
              <tr>
                <td class="label"></td>
                <td>
                  <d:input type="radio" name="defaultLauncherPort" id="defaultLauncherPort_true"
                           bean="SearchHostFormHandler.defaultLaunchServicePort" value="true"/>
                  <label for="defaultLauncherPort_true">
                    <d:getvalueof bean="SearchHostFormHandler.defaultLaunchServicePortVal" var="defaultLaunchServicePort"/>
                    <fmt:message key="search_env_host_machine_config.launch_service.system.default.port"/>
                    <c:out value="${defaultLaunchServicePort}"/>
                  </label>
                  <br/>
                  <d:input type="radio" name="defaultLauncherPort" id="defaultLauncherPort_false"
                           bean="SearchHostFormHandler.defaultLaunchServicePort" value="false"/>
                  <fmt:message key="search_env_host_machine_config.launch_service.port"/>
                  <d:input type="text" iclass="textField small" name="launchServicePort"
                           bean="SearchHostFormHandler.launchServicePort" onfocus="setCheckBox(this,'defaultLauncherPort_false');"/>
                  <br/>
                  <label>
                    <d:getvalueof bean="SearchHostFormHandler.defaultLaunchServiceProtocolVal" var="defaultLaunchServiceProtocol"/>
                    <fmt:message key="search_env_host_machine_config.launch_service.system.default.protocol">
                      <fmt:param value="${defaultLaunchServiceProtocol}"/>
                    </fmt:message>
                  </label>
                  <br/>
                  <label>
                    <d:getvalueof bean="SearchHostFormHandler.defaultLaunchServiceNameVal" var="defaultLaunchServiceName"/>
                    <fmt:message key="search_env_host_machine_config.launch_service.system.default.name">
                      <fmt:param value="${defaultLaunchServiceName}"/>
                    </fmt:message>
                  </label>
                </td>
              </tr>
              <tr>
                <td><fmt:message key="search_env_host_machine_config.machine.info"/></td>
              </tr>
              <tr>
                <td class="label"></td>
                <td>
                  <label>
                    <d:getvalueof bean="SearchHostFormHandler.cpuCount" var="cpuCount"/>
                    <fmt:message key="search_env_host_machine_config.launch_service.system.cpus">
                      <fmt:param value="${cpuCount}"/>
                    </fmt:message>
                  </label>
                  <br/>
                  <fmt:message key="search_env_host_machine_config.launch_service.system.os.unknown" var="unknown"/>
                  <label>
                    <d:getvalueof bean="SearchHostFormHandler.osName" var="osName"/>
                    <fmt:message key="search_env_host_machine_config.launch_service.system.os.name">
                      <c:if test="${not empty osName}">
                        <fmt:param value="${osName}"/>
                      </c:if>
                      <c:if test="${empty osName}">
                        <fmt:param value="${unknown}"/>
                      </c:if>
                    </fmt:message>
                  </label>
                  <br/>
                  <label>
                    <d:getvalueof bean="SearchHostFormHandler.osArchitecture" var="osArch"/>
                    <fmt:message key="search_env_host_machine_config.launch_service.system.os.arch">
                      <c:if test="${not empty osArch}">
                        <fmt:param value="${osArch}"/>
                      </c:if>
                      <c:if test="${empty osArch}">
                        <fmt:param value="${unknown}"/>
                      </c:if>
                    </fmt:message>
                  </label>
                  <br/>
                  <label>
                    <d:getvalueof bean="SearchHostFormHandler.osVersion" var="osVersion"/>
                    <fmt:message key="search_env_host_machine_config.launch_service.system.os.version">
                      <c:if test="${not empty osVersion}">
                        <fmt:param value="${osVersion}"/>
                      </c:if>
                      <c:if test="${empty osVersion}">
                        <fmt:param value="${unknown}"/>
                      </c:if>
                    </fmt:message>
                  </label>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
      <div id="paneFooter">
        <d:input type="hidden" bean="SearchHostFormHandler.hostId" name="hostId"/>
        <d:input type="hidden" bean="SearchHostFormHandler.successURL" value="${successURL}" name="successUrl"/>
        <d:input type="hidden" bean="SearchHostFormHandler.errorURL" value="${errorURL}" name="errorUrl"/>
        <d:input type="hidden" bean="SearchHostFormHandler.needInitialization"
                 value="false" name="needInitialization"/>

        <fmt:message var="saveButton" key='search_env_host_machine_config.button.save'/>
        <fmt:message var="saveButtonToolTip" key='search_env_host_machine_config.button.save.tooltip'/>
        <fmt:message var="cancelButton" key='search_env_host_machine_config.button.cancel'/>
        <fmt:message var="cancelButtonToolTip" key='search_env_host_machine_config.button.cancel.tooltip'/>
        <d:input type="submit" value="${saveButton}" name="save" iclass="formsubmitter"
                 title="${saveButtonToolTip}" bean="SearchHostFormHandler.save"
                 onclick="return checkForm();"/>
        <d:input type="submit" value="${cancelButton}" iclass="formsubmitter" 
                 title="${cancelButtonToolTip}" bean="SearchHostFormHandler.cancel" name="cancel"/>
      </div>
  
      <admin-validator:validate beanName="SearchHostFormHandler"/>

    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_host_machine_config.jsp#2 $$Change: 651448 $--%>
