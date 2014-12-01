<%--
  JSP, showing "used in search projects" tab on new/edit topic set page. This page is included into new_topic_set.jsp

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_search_projects.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <br/>

  <p>
    <fmt:message key="topic_set.used_in_search_projects.head"/>
  </p>

  <%-- Custom tag, retrieving all search projects --%>
  <common:searchProjectFindAll var="projects"/>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.searchProjectComparator" var="comparator"/>
  <admin-ui:sort var="projects" items="${projects}" comparator="${comparator}" sortMode="undefined"/>  

  <%-- Custom tag, used to show all projects in table --%>
  <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                  var="currentProject" items="${projects}" tableId="associatedProjects">
    <admin-ui:column type="checkbox simple">
      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler.searchProjects"
               type="checkbox" value="${currentProject.id}"/>
    </admin-ui:column>
    <admin-ui:column title="topic_set.used_in_search_projects.table.project" type="static">
      <fmt:message key="topic_set.used_in_search_projects.table.project.tooltip" var="projectTooltip"/>
      <d:a href="${pageContext.request.contextPath}/searchadmin/project.jsp" title="${projectTooltip}" onclick="return loadRightPanel(this.href);">
        <d:param name="projectId" value="${currentProject.id}"/>
        <c:out value="${currentProject.name}"/>
      </d:a>
    </admin-ui:column>
    <admin-ui:column title="topic_set.used_in_search_projects.table.description" type="static">
      <c:out value="${currentProject.description}"/>
    </admin-ui:column>
  </admin-ui:table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topicset_search_projects.jsp#2 $$Change: 651448 $--%>
