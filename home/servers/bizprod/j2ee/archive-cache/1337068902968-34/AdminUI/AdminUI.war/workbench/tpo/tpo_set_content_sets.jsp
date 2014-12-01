<%--
  JSP, showing "used in content sets" tab on new/edit topic set page. This page is included into new_topic_set.jsp

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_set_content_sets.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>

  <%-- Custom tag, retrieving all search projects --%>
  <common:searchProjectFindAll var="projects"/>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.searchProjectComparator" var="comparator"/>
  <admin-ui:sort var="projects" items="${projects}" comparator="${comparator}" sortMode="undefined"/>  

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TPOSetContentFormHandler"/>

  <c:forEach items="${projects}" var="project">

    <h3><c:out value="${project.name}"/></h3>

    <d:getvalueof bean="TPOSetContentFormHandler.projectsContent.${project.id}" var="contentSetsInfos"/>

    <admin-ui:table renderer="/templates/table_simple.jsp"
                    modelVar="table"
                    var="contentSetsInfo"
                    items="${contentSetsInfos}">
      <admin-ui:column type="checkbox simple">
        <d:input bean="TPOSetContentFormHandler.contentSets"
                 type="checkbox"
                 value="${contentSetsInfo.contentSource.id}" name="contentSets"/>
      </admin-ui:column>
      <admin-ui:column title="tpo_set.used_in_content_sets.table.content_set" type="static">
        <c:out value="${contentSetsInfo.contentSource.name}"/>
      </admin-ui:column>
      <admin-ui:column title="tpo_set.used_in_content_sets.table.partition" type="static">
        <c:out value="${contentSetsInfo.indexPartitionNumber}"/>
      </admin-ui:column>
    </admin-ui:table>
  </c:forEach>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_set_content_sets.jsp#2 $$Change: 651448 $--%>
