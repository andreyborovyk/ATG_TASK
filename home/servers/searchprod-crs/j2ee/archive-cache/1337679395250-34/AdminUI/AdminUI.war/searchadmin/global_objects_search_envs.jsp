<%--
  Global Objects Search Environments page. Displays list of unattached search environments and list of all projects
  with attached environments.

  --%>

<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">

      <c:set var="title">
        <fmt:message key="global_objects_search_envs.description"/>
      </c:set>
      <c:if test="${ not empty title }">
        <p>
          <fmt:message key="global_objects_search_envs.description"/>
        </p>
        <span class="seperator"></span>
      </c:if>
      
      <c:url var="newSearchEnvironmentPage" value="/searchadmin/global_new_search_env.jsp">
        <c:param name="layout" value="partial"/>
      </c:url>
      <p>
        <input type="button" onclick="return loadRightPanel('${newSearchEnvironmentPage}');"
               value="<fmt:message key='global_objects_search_envs.new_search_env.button'/>"
               title="<fmt:message key='global_objects_search_envs.new_search_env.button.tooltip'/>"/>
      </p>

      <h3><fmt:message key="global_objects_search_envs.environments.unattached"/></h3>

      <d:form method="post" action="${formActionUrl}">
        <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SearchEnvironmentFormHandler" />
        <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SortFormHandler"/>
        <d:importbean bean="/atg/searchadmin/adminui/formhandlers/ManageProjectFormHandler" var="manageProjectFormHandler"/>

        <c:url value="/searchadmin/global_objects_search_envs.jsp" var="refreshUrl"/>
        <d:input type="hidden" bean="SortFormHandler.successURL" value="${refreshUrl}"/>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.successURL" value="${refreshUrl}"/>
        <d:input type="hidden" bean="SearchEnvironmentFormHandler.errorURL" value="${refreshUrl}"/>
        <d:input type="submit" bean="SortFormHandler.sort" style="display:none" id="sortInput" iclass="formsubmitter"/>

        <%--Hidden buttons for environment stop/start actions--%>
        <d:input bean="SearchEnvironmentFormHandler.stopEnvironment" type="submit" id="stopButtonId" 
                 iclass="formsubmitter" style="display:none" name="stopEnvironment"/>
        <d:input bean="SearchEnvironmentFormHandler.startEnvironment" type="submit" id="startButtonId" 
                 iclass="formsubmitter" style="display:none" name="startEnvironment"/>
        <d:input bean="SearchEnvironmentFormHandler.environmentId" type="hidden" id="environmentId" name="environmentId"/>

        <d:include page="search_environments_table.jsp">
          <d:param name="showDeleteColumn" value="true"/>
          <d:param name="tableId" value="unattachedSearchEnvTable"/>
        </d:include>

        <common:searchProjectFindAll var="projects"/>
        <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.searchProjectComparator" var="comparator"/>
        <admin-ui:sort var="projects" items="${projects}" comparator="${comparator}" sortMode="undefined"/>

        <c:forEach items="${projects}" var="item">
          <h3>
            <fmt:message key="global_objects_search_envs.search.project"/>
            <c:out value="${item.name}"/>
            <span class="headerLink">
              &nbsp;
              <c:url var="editProjectURL" value="/searchadmin/project.jsp">
                <c:param name="projectId" value="${item.id}"/>
              </c:url>
              [<a href="${editProjectURL}" onclick="return loadRightPanel(this.href)"
                  title="<fmt:message key='global_objects_search_envs.search.project.edit.tooltip'/>"
                  ><fmt:message key="global_objects_search_envs.search.project.edit"/></a>]
              &nbsp;
              <c:url var="manageEnvsURL" value="/searchadmin/project_search_envs.jsp">
                <c:param name="projectId" value="${item.id}"/>
              </c:url>
              [<a href="${manageEnvsURL}" onclick="return loadRightPanel(this.href)"
                  title="<fmt:message key='global_objects_search_envs.manage_envs.tooltip'/>"
                  ><fmt:message key="global_objects_search_envs.manage_envs"/></a>]
            </span>
          </h3>
          <admin-ui:initializeFormHandler handler="${manageProjectFormHandler}">
            <admin-ui:param name="projectId" value="${item.id}" />
          </admin-ui:initializeFormHandler>
          <d:include page="search_environments_table.jsp">
            <d:param name="projectId" value="${item.id}"/>
            <d:param name="tableId" value="${item.id}"/>
          </d:include>
        </c:forEach>
      </d:form>
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/global_objects_search_envs.jsp#2 $$Change: 651448 $--%>
