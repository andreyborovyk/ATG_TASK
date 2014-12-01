<%--
  JSP, showing "used in search projects" tab on new/edit term weight set page. This page is included into term_weight_set.jsp

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_search_projects.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <br/>

  <p>
    <fmt:message key="term_weight.project.head"/>
  </p>

  <%-- Custom tag, retrieving all search projects --%>
  <common:searchProjectFindAll var="projects"/>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.searchProjectComparator" var="comparator"/>
  <admin-ui:sort var="projects" items="${projects}" comparator="${comparator}" sortMode="undefined"/>  

  <%-- Custom tag, used to show all projects in table --%>
  <admin-ui:table renderer="/templates/table_simple.jsp"
                  modelVar="table"
                  var="currentProject"
                  items="${projects}"
                  tableId="associatedProjects">
    <admin-ui:column type="checkbox simple">
      <d:input type="checkbox"
               value="${currentProject.id}"
               bean="/atg/searchadmin/workbenchui/formhandlers/ManageTermWeightSetFormHandler.projectsIds"/>
    </admin-ui:column>
    <admin-ui:column title="term_weight.project.table.project" type="static">
      <d:a href="${pageContext.request.contextPath}/searchadmin/project.jsp" onclick="return loadRightPanel(this.href);">
        <d:param name="projectId" value="${currentProject.id}"/>
        <c:out value="${currentProject.name}"/>
      </d:a>
    </admin-ui:column>
    <admin-ui:column title="term_weight.project.table.description" type="static">
      <c:out value="${currentProject.description}"/>
    </admin-ui:column>
  </admin-ui:table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_set_search_projects.jsp#2 $$Change: 651448 $--%>
