<%--
  Project Search Environments page. Displays list of search environments of a particular project.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_search_envs.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <%-- Search project id --%>
  <d:getvalueof param="projectId" var="projectId"/>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client"
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:url value="/searchadmin/project_search_envs.jsp" var="refreshUrl">
      <c:param name="projectId" value="${projectId}"/>
    </c:url>

    <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SearchEnvironmentFormHandler" />
    <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>
    <d:importbean bean="/atg/searchadmin/adminui/formhandlers/ManageProjectFormHandler" var="manageProjectFormHandler"/>

    <admin-ui:initializeFormHandler handler="${manageProjectFormHandler}">
      <admin-ui:param name="projectId" value="${projectId}" />
      <admin-ui:param name="action" value="edit" />
    </admin-ui:initializeFormHandler>
    <d:form method="post" action="${formActionUrl}">
      <div id="paneContent">
        <p>
          <fmt:message key="project_search_envs.description"/>
          <span class="ea"><tags:ea key="embedded_assistant.project_search_envs.description" /></span>
        </p>
        <p>
          <c:url var="newSearchEnvironmentPage" value="/searchadmin/project_new_search_env.jsp">
            <c:param name="layout" value="full"/>
            <c:param name="projectId" value="${projectId}"/>
          </c:url>
          <input type="button" onclick="return loadRightPanel('${newSearchEnvironmentPage}');"
                 value="<fmt:message key='project_search_envs.button'/>"
                 title="<fmt:message key='project_search_envs.button.tooltip'/>"/>
        </p>

        <d:input type="hidden" bean="SortFormHandler.successURL" value="${refreshUrl}"/>
        <d:input type="submit" bean="SortFormHandler.sort" iclass="formsubmitter" style="display:none" id="sortInput"/>

        <d:input type="hidden" bean="SearchEnvironmentFormHandler.successURL" value="${refreshUrl}"/>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.errorURL" value="${refreshUrl}"/>

        <d:input bean="SearchEnvironmentFormHandler.stopEnvironment" type="submit" id="stopButtonId"
                 iclass="formsubmitter" style="display:none" name="stopEnvironment"/>
        <d:input bean="SearchEnvironmentFormHandler.startEnvironment" type="submit" id="startButtonId"
                 iclass="formsubmitter" style="display:none" name="startEnvironment"/>
        <d:input bean="SearchEnvironmentFormHandler.environmentId" type="hidden" id="environmentId" name="environmentId"/>
        <d:include page="search_environments_table.jsp">
          <d:param name="projectId" value="${projectId}"/>
          <d:param name="showDeleteColumn" value="true"/>
          <d:param name="tableId" value="searchEnvTable"/>
        </d:include>
      </div>
      <div id="paneFooter">
        <d:input bean="ManageProjectFormHandler.successURL" value="${refreshUrl}" type="hidden"/>
        <d:input bean="ManageProjectFormHandler.errorURL" value="${refreshUrl}" type="hidden"/>
        <d:input type="hidden" bean="ManageProjectFormHandler.needInitialization" value="false"/>
        <d:input type="hidden" bean="ManageProjectFormHandler.action" />
        <d:input bean="ManageProjectFormHandler.projectId" value="${projectId}" type="hidden" name="projectId"/>

        <fmt:message key="project_search_envs.button.save" var="saveButtonTitle"/>
        <fmt:message key="project_search_envs.button.save.tooltip" var="saveButtonTooltip"/>
        <d:input type="submit" bean="ManageProjectFormHandler.setPrimaryEnvironment" iclass="formsubmitter"
                 name="saveButton" value="${saveButtonTitle}" title="${saveButtonTooltip}"/>
      </div>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_search_envs.jsp#2 $$Change: 651448 $--%>
