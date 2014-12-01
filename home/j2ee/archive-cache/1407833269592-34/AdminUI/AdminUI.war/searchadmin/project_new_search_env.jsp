<%--
  Page is using for creation of new search environment.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_new_search_env.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="../templates/top.jspf"%>

<d:page>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SearchEnvironmentFormHandler" var="handler" />
  <%-- Search project id --%>
  <d:getvalueof param="projectId" var="projectId"/>

  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="environmentId" value=""/>
  </admin-ui:initializeFormHandler>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client"
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:url var="successUrl" value="/searchadmin/search_env_configure_hosts.jsp">
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <c:url var="successAlternativeUrl" value="/searchadmin/project_search_envs.jsp">
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <c:url var="errorUrl" value="/searchadmin/project_new_search_env.jsp">
      <c:param name="layout" value="full"/>
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <d:form action="${errorUrl}" method="post">
      <d:setvalue bean="SearchEnvironmentFormHandler.projectId" value="${projectId}"/>
      <div id="paneContent">
        <p>
          <fmt:message key="project_search_envs_new_search_env.description"/>
          <span class="ea"><tags:ea key="embedded_assistant.project_search_envs.description" /></span>
        </p>
        <span class="seperator"></span>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.projectId" value="${projectId}" name="projectId"/>
        <p><fmt:message key="project_search_envs_new_search_env.select"/></p>
        <%-- value=1:attach, value=2:create --%>
        <ul class="simple" id="searchEnvNameDiv">
          <li>
            <admin-beans:getSearchEnvironments var="searchEnvironments" />
            <c:choose>
              <c:when test="${empty searchEnvironments}">
                <d:input type="radio" name="action" onclick="radioSwitch(this);"
                         bean="SearchEnvironmentFormHandler.action" value="1" disabled="true"/>
                <fmt:message key="project_search_envs_new_search_env.add.unassociated.environment"/>
                <%--TODO: appropriate style for empty disabled select is needed  --%>
                <d:select bean="SearchEnvironmentFormHandler.environmentId" style="font-size:.9em;width:80px;" disabled="true"/>
              </c:when>
              <c:otherwise>
                <d:input type="radio" onclick="radioSwitch(this);" name="action"
                         bean="SearchEnvironmentFormHandler.action" value="1"/>
                <fmt:message key="project_search_envs_new_search_env.add.unassociated.environment"/>
                <d:select bean="SearchEnvironmentFormHandler.environmentId" iclass="small" name="environmentId">
                  <c:forEach var="environment"  items="${searchEnvironments}">
                    <c:set var="environmentName" value="${environment.envName}"/>
                    <d:option value="${environment.id}"><c:out value="${environmentName}"/></d:option>
                  </c:forEach>
                </d:select>
              </c:otherwise>
            </c:choose>
          </li>
          <li>
            <d:input type="radio" onclick="radioSwitch(this);" name="action"
                     bean="SearchEnvironmentFormHandler.action" value="2" checked="true"/>
            <fmt:message key="project_search_envs_new_search_env.create.environment"/>
          </li>
        </ul>
        <%-- value=1:attach, value=2:create --%>
        <div id="searchEnvNameDiv1" style="display:none;">
        </div>
        <div id="searchEnvNameDiv2">
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
            <tbody>
          </table>
        </div>
      </div>
      <div id="paneFooter">
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.errorURL" value="${errorUrl}" name="errorUrl"/>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.successURL" value="${successUrl}" name="successUrl"/>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.successAlternativeURL" value="${successAlternativeUrl}"
            name="successAlternativeUrl"/>
        <fmt:message var="addButtonLabel" key="project_search_envs_new_search_env.button.add"/>
        <fmt:message var="addButtonToolTip" key="project_search_envs_new_search_env.button.add.tooltip"/>
        <d:input type="submit" value="${addButtonLabel}" title="${addButtonToolTip}"
                     bean="SearchEnvironmentFormHandler.create" iclass="formsubmitter"
                     onclick="return checkForm();"/>
        <fmt:message key="project_search_envs_new_search_env.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="project_search_envs_new_search_env.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="SearchEnvironmentFormHandler.cancel" iclass="formsubmitter"
                 name="cancelButton" value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </d:form>
  </div>
  <admin-validator:validate beanName="SearchEnvironmentFormHandler"/>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_new_search_env.jsp#2 $$Change: 651448 $--%>
