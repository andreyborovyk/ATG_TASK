<%--
  Facet sets page, shows all facet sets for definite adaptor. Provides export of facet sets.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetsets_general_readonly.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="projectId" bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"/>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:if test="${not empty projectId}">
      <admin-beans:getIndexedFacetSets var="indexedFacetSets" projectId="${projectId}" />
      <div id="paneContent">
        <br/>
        <c:if test="${empty indexedFacetSets.nodes}">
          <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>
          <fmt:message key="facetsets_general_readonly.no_index.message" >
            <strong>
              <fmt:param value="${project.name}" />
            </strong>
          </fmt:message>
        </c:if>
        <c:if test="${not empty indexedFacetSets.nodes}">
          <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                          var="facetSet" items="${indexedFacetSets.nodes}" >
            <admin-ui:column title="facetsets_general_readonly.table.facet.set.name" type="static">
              <c:url var="facetsetReadonlyUrl" value="${facetPath}/facetset_readonly.jsp">
                <c:param name="adapterItemId" value="${facetSet.name}"/>
              </c:url>
              <a href="${facetsetReadonlyUrl}" onclick="return loadRightPanel(this.href);">
                <c:out value="${facetSet.name}" />
              </a>
            </admin-ui:column>
            <c:catch var="exception">
              <facets:facetSetFindByName var="facetSetByName" name="${facetSet.name}" />
            </c:catch>
            <admin-ui:column type="icon">
            <c:if test="${not empty facetSetByName}">
              <fmt:message key="facetsets_general_readonly.table.edit" var="editTitle"/>
              <c:url var="facetsetUrl" value="${facetPath}/facetset.jsp">
                <c:param name="facetSetId" value="${facetSetByName.id}"/>
              </c:url>
              <a class="icon propertyEdit" title="${editTitle}" href="${facetsetUrl}"
                 onclick="return loadRightPanel(this.href);">edit</a>
            </c:if>
            </admin-ui:column>
            <admin-ui:column type="icon">
            <fmt:message key="facetsets_general_readonly.table.export" var="exportTitle"/>
             <c:url var="facetsetExportUrl" value="${facetPath}/facetset_export.jsp">
              <c:param name="facetSetName" value="${facetSet.name}"/>
              <c:param name="projectId" value="${projectId}"/>
             </c:url>
            <a class="icon propertyExport" title="${exportTitle}" href="${facetsetExportUrl}"
               onclick="return loadRightPanel(this.href);">exp</a>
            </admin-ui:column>
          </admin-ui:table>
        </c:if>
      </div>
    </c:if>
  </div>

  <script type="text/javascript">
    function refreshRightPane(obj) {
      <c:url value="${facetPath}/facetsets_general_readonly.jsp" var="backActiveURL"/>
      <c:url value="${facetPath}/facetsets_general.jsp" var="backEmptyURL"/>
      if (obj.activeProjectId){
        loadRightPanel('${backActiveURL}');
      } else {
        loadRightPanel('${backEmptyURL}');
      }
    }
    top.hierarchy = [{id:"rootIndexedFacetSetNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetsets_general_readonly.jsp#2 $$Change: 651448 $--%>
