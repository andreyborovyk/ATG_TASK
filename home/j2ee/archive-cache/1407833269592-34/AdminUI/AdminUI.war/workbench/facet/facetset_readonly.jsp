<%--
  Provides facet set "read only" page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_readonly.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="adapterItemId" var="adapterItemId" />
  <d:getvalueof var="projectId" bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"/>
  <admin-beans:getIndexedFacetSets var="indexedFacetSet" projectId="${projectId}" parentId="${adapterItemId}" />
  <c:catch>
    <facets:facetSetFindByName var="facetSetByName" name="${indexedFacetSet.name}" />
  </c:catch>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:if test="${not empty indexedFacetSet}">
      <div id="paneContent">
        <h3>
          <fmt:message key="facetset_basics.title"/>
        </h3>
        <table class="form" cellspacing="0" cellpadding="0" >
          <tr>
            <td class="label">
              <fmt:message key="facetset_basics.name"/>
            </td>
            <td>
              ${indexedFacetSet.name}
            </td>
          </tr>
          <tr>
            <td class="label">
              <fmt:message key="facetset_basics.description"/>
            </td>
            <td>
              <c:if test="${not empty facetSetByName}">
                ${facetSetByName.description}
              </c:if>
            </td>
          </tr>
          <tr>
            <td class="label">
              <fmt:message key="facetset_basics.mappings"/>
            </td>
            <td>
              <c:forEach items="${indexedFacetSet.mappings}" var="mapping" varStatus="status">
                <c:if test="${not status.first}">
                  <fmt:message key="facetset_basics.delimiter"/>
                </c:if>
                ${mapping.mapping}
              </c:forEach>
            </td>
          </tr>
        </table>

        <h3>
          <fmt:message key="facetset.top.level.facets">
            <fmt:param value="${fn:length(indexedFacetSet.childFacets)}" />
          </fmt:message>
        </h3>

        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="facet" items="${indexedFacetSet.childFacets}" >
          <admin-ui:column title="table_readonly.facet.name" type="static">
            <c:url var="facetReadonlyUrl" value="${facetPath}/facet_readonly.jsp">
              <c:param name="adapterItemId" value="${facet.id}"/>
            </c:url>
            <a href="${facetReadonlyUrl}" onclick="return loadRightPanel(this.href);">
              <c:out value="${facet.name}" />
            </a>
          </admin-ui:column>
          <admin-ui:column title="table_readonly.facet.property" type="static">
            <c:out value="${facet.property}" />
          </admin-ui:column>
          <admin-ui:column title="table_readonly.facet.property.type" type="static">
            <fmt:message key="facet.property_type.option.${facet.propertyType}"/>
          </admin-ui:column>
        </admin-ui:table>
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
    <c:if test="${empty indexedFacetSet}">
      refreshRightPane({activeProjectId:'${projectId}'});
    </c:if>
    //dojo tree refresh
    top.hierarchy = [{id:"rootIndexedFacetSetNode"}, {id:"<c:out value="${adapterItemId}"/>", treeNodeType:"indexedFacetSet"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_readonly.jsp#2 $$Change: 651448 $--%>
