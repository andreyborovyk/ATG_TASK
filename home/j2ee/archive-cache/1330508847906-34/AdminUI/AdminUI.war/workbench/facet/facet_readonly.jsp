<%--
Facet read only page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_readonly.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="adapterItemId" var="adapterItemId" />
  <d:getvalueof var="projectId" bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"/>

  <admin-beans:getIndexedFacetSets var="indexedFacet" projectId="${projectId}" parentId="${adapterItemId}" />

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:if test="${not empty indexedFacet}">
      <div id="paneContent">
        <h3>
          <fmt:message key="facet.basics"/>
        </h3>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label"><fmt:message key="facet.name"/></td>
              <td><c:out value="${indexedFacet.name}" /></td>
            </tr>
            <tr>
              <td class="label"><fmt:message key="facet.property"/></td>
              <td><c:out value="${indexedFacet.property}" /></td>
            </tr>
            <tr>
              <td class="label"><fmt:message key="facet.property_type"/></td>
              <td>
                <fmt:message key="facet.property_type.option.${indexedFacet.propertyType}"/>
              </td>
            </tr>
          </tbody>
        </table>

        <br/>

        <c:set var="length" value="${fn:length(indexedFacet.childFacets)}" />

        <h3>
          <fmt:message key="facet.child_facets">
            <fmt:param value="${length}" />
          </fmt:message>
        </h3>

        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="facet" items="${indexedFacet.childFacets}" >
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

        <d:include page="facet_edit_readonly.jsp">
          <d:param name="indexedFacet" value="${indexedFacet}" />
        </d:include>
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
    <c:if test="${empty indexedFacet}">
      refreshRightPane({activeProjectId:'${projectId}'});
    </c:if>
    //dojo tree refresh
    top.hierarchy = [{id:"rootIndexedFacetSetNode"}, {id:"<c:out value="${adapterItemId}"/>", treeNodeType:"indexedFacet"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_readonly.jsp#2 $$Change: 651448 $--%>
