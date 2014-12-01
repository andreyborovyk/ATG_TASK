<%--
  JSP allowes all search projects to be selected.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdict_search_projects.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TermDictsFormHandler" />

  <p>
    <fmt:message key="termdict_serarch_projects.head" />
  </p>
  <common:searchProjectFindAll var="projects"/>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.searchProjectComparator" var="comparator"/>
  <admin-ui:sort var="projects" items="${projects}" comparator="${comparator}" sortMode="undefined"/>

  <admin-ui:table renderer="/templates/table_simple.jsp"
                  modelVar="table"
                  var="project"
                  items="${projects}"
                  tableId="associatedProjects">

    <admin-ui:column type="checkbox simple">
      <d:input bean="/atg/searchadmin/workbenchui/formhandlers/TermDictsFormHandler.searchProject"
               type="checkbox"
               value="${project.id}"/>
    </admin-ui:column>

    <admin-ui:column title="termdict_serarch_projects.table.name" type="static">
      <d:a href="${pageContext.request.contextPath}/searchadmin/project.jsp" onclick="return loadRightPanel(this.href);">
        <d:param name="projectId" value="${project.id}"/>
        <c:out value="${project.name}"/>
      </d:a>
    </admin-ui:column>

    <admin-ui:column title="termdict_serarch_projects.table.description" type="static">
      <c:out value="${project.description}"/>
    </admin-ui:column>
  </admin-ui:table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdict_search_projects.jsp#2 $$Change: 651448 $--%>
