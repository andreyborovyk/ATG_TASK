<%--
JSP, used to show information about created project. Page includes 7 different pages for 7
different parts: head (shows information about current project status), general (shows information about project name,
project description and secure organizations), index (shows information about project index), search environments,
synchronization, customizations and footer (used to show "create", "update", "cancel" footer buttons).

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%--
    projectId parameter used to determine, which project information must be shown.
    if this parameter is null, it means than new project must be created.
  --%>
  <d:getvalueof param="projectId" var="projectId"/>

  <%--
    status attribute shows current project state:
    not created (1) (functionality was moved to new_project.jsp page),
    created but with no content sets (2),
    has not been synchronized (3),
    all steps are completed (4)
  --%>
  <admin-beans:getProjectStatus var="status" projectId="${projectId}"/>

  <%-- Head section --%>
  <d:include page="project_head.jsp">
    <d:param name="status" value="${status}"/>
  </d:include>
  
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">

      <c:if test="${status == 4}">
        <p>
          <fmt:message key="project_general.title"/>
        </p>
      </c:if>

      <%-- General section --%>
      <h3 class="overview">
        <d:include src="project_step.jsp">
          <d:param name="step" value="${status}"/>
          <d:param name="section" value="1"/>
        </d:include>
        <fmt:message key="project_general.base_name"/>
        <span class="headerLink">
          <c:url value="/searchadmin/project_general_settings.jsp" var="editGeneralUrl">
            <c:param name="projectId" value="${projectId}"/>
          </c:url>
          [<a href="${editGeneralUrl}" onclick="return loadRightPanel(this.href);"
              title="<fmt:message key='project_general.edit.tooltip'/>"><fmt:message key="project_general.edit"/></a>]
        </span>
      </h3>
      <d:include page="project_general_view.jsp">
        <d:param name="projectId" value="${projectId}"/>
      </d:include>

      <%-- Content for Indexing section --%>
      <c:url value="/searchadmin/project_manage_index.jsp" var="manageIndexUrl">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>
      <h3 class="overview">
        <d:include src="project_step.jsp">
          <d:param name="step" value="${status}"/>
          <d:param name="section" value="2"/>
        </d:include>
        <fmt:message key="project_index.base_name"/>
        <span class="headerLink">
          [<a href="${manageIndexUrl}" onclick="return loadRightPanel(this.href);"
              title="<fmt:message key='project_index.edit.tooltip'/>"><fmt:message key="project_index.edit"/></a>]
        </span>
      </h3>

      <d:include page="project_index.jsp">
        <d:param name="projectId" value="${projectId}"/>
        <d:param name="status" value="${status}"/>
      </d:include>

      <%-- Indexing section--%>
      <h3 class="overview">
        <d:include src="project_step.jsp">
          <d:param name="step" value="${status}"/>
          <d:param name="section" value="3"/>
        </d:include>
        <fmt:message key="project_synch.base_name"/>
        <span class="headerLink">
          <c:url value="/searchadmin/synchronization_manual.jsp" var="syncManualUrl">
            <c:param name="projectId" value="${projectId}"/>
          </c:url>
          [<a href="${syncManualUrl}" onclick="return loadRightPanel(this.href);"
              title="<fmt:message key='project_synch.edit.tooltip'/>"><fmt:message key="project_synch.edit"/></a>]
        </span>
      </h3>
      <p>
        <fmt:message key="project_synch.title"/>
      </p>

      <%-- Search Environments section--%>
      <h3 class="overview">
        <fmt:message key="project_senv.base_name"/>
        <span class="headerLink">
          <c:url value="/searchadmin/project_search_envs.jsp" var="seUrl">
            <c:param name="projectId" value="${projectId}"/>
          </c:url>
          <%-- Link to manage search environments page --%>
          [<a href="${seUrl}" onclick="return loadRightPanel(this.href);"
              title="<fmt:message key='project_senv.manage.tooltip'/>"><fmt:message key="project_senv.manage"/></a>]
        </span>
      </h3>
      <p>
        <fmt:message key="project_senv.title"/>
      </p>

      <d:form method="post" action="${formActionUrl}">
        <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SearchEnvironmentFormHandler" />
        <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>
        <d:importbean bean="/atg/searchadmin/adminui/formhandlers/ManageProjectFormHandler" var="manageProjectFormHandler"/>
        <c:url value="/searchadmin/project.jsp" var="refreshUrl">
          <c:param name="projectId" value="${projectId}"/>
        </c:url>
        <admin-ui:initializeFormHandler handler="${manageProjectFormHandler}">
          <admin-ui:param name="projectId" value="${projectId}" />
        </admin-ui:initializeFormHandler>
        <d:input type="hidden" bean="SortFormHandler.successURL" value="${refreshUrl}"/>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.successURL" value="${refreshUrl}"/>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.errorURL" value="${refreshUrl}"/>
        <d:input type="submit" bean="SortFormHandler.sort" style="display:none" iclass="formsubmitter" id="sortInput"/>

        <%--Hidden buttons for environment stop/start actions--%>
        <d:input bean="SearchEnvironmentFormHandler.stopEnvironment" type="submit" iclass="formsubmitter" 
          id="stopButtonId" style="display:none" name="stopEnvironment"/>
        <d:input bean="SearchEnvironmentFormHandler.startEnvironment" type="submit" iclass="formsubmitter" 
          id="startButtonId" style="display:none" name="startEnvironment"/>
        <d:input bean="SearchEnvironmentFormHandler.environmentId" type="hidden" iclass="forsubmitter" 
          id="environmentId" name="environmentId"/>
        <d:include page="search_environments_table.jsp">
          <d:param name="projectId" value="${projectId}"/>
          <d:param name="tableId" value="searchEnvTable"/>
        </d:include>
      </d:form>
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project.jsp#2 $$Change: 651448 $--%>
