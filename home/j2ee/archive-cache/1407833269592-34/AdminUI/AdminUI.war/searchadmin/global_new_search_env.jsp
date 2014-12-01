<%--
  Page is using for creation of new attaching search environment.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/global_new_search_env.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="../templates/top.jspf"%>
<d:page>
  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SearchEnvironmentFormHandler"/>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:url var="errorUrl" value="/searchadmin/global_new_search_env.jsp">
      <c:param name="layout" value="partial"/>
    </c:url>
    <d:form action="${errorUrl}" method="post">
      <c:url var="successUrl" value="/searchadmin/search_env_configure_hosts.jsp"/>
      <d:input type="hidden" bean="SearchEnvironmentFormHandler.errorURL" value="${errorUrl}" name="errorUrl"/>
      <d:input type="hidden" bean="SearchEnvironmentFormHandler.successURL" value="${successUrl}" name="successUrl"/>
      <div id="paneContent">
        <p>
          <fmt:message key="project_search_envs_new_search_env.description"/>
          <span class="ea"><tags:ea key="embedded_assistant.project_search_envs.description" /></span>
        </p>
        <span class="seperator"></span>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.action" value="2" name="action"/>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">
                <span id="searchEnvironmentNameAlert">
                  <span class="required">
                    <fmt:message key="project_search_envs_new_search_env.required_field"/>
                  </span>
                </span>
                <label for="searchEnvironmentName">
                  <fmt:message key="project_search_envs_new_search_env.search_env_name_field"/>
                </label>
              </td>
              <td>
                <d:input type="text" id="searchEnvironmentName" name="searchEnvironmentName"
                         bean="SearchEnvironmentFormHandler.searchEnvironmentName" iclass="textField" />
              </td>
            </tr>
          <tbody>
        </table>
      </div>

      <div id="paneFooter">
        <fmt:message var="addButtonLabel" key="project_search_envs_new_search_env.button.add"/>
        <fmt:message var="addButtonToolTip" key="project_search_envs_new_search_env.button.add.tooltip"/>
        <d:input type="submit" value="${addButtonLabel}" title="${addButtonToolTip}" name="create"
                     bean="SearchEnvironmentFormHandler.create" iclass="formsubmitter"
                     onclick="return checkForm();"/>
        <fmt:message key="project_search_envs_new_search_env.button.cancel" var="cancelButtonTitle"/>
        <fmt:message key="project_search_envs_new_search_env.button.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="SearchEnvironmentFormHandler.cancel" iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </d:form>
  </div>
  <admin-validator:validate beanName="SearchEnvironmentFormHandler"/>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/global_new_search_env.jsp#2 $$Change: 651448 $--%>
