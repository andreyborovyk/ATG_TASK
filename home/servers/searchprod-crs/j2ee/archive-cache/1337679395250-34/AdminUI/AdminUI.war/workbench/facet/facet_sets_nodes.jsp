<%--
JSP, used as controller for dojo topic tree. Receives dojo request, prepare and send responce in json format.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_sets_nodes.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page xml="true">
  <d:getvalueof var="action" param="action"/>
  <d:getvalueof var="data" param="data"/>
  <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="treeNodeType" var="nodeType"/>
  <admin-dojo:jsonObject>
    <admin-dojo:jsonArray name="children">
      <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"
                var="activeProjectId"/>
      <c:if test="${nodeType == 'rootNode'}">
        <c:if test="${not empty activeProjectId}">
          <common:searchProjectFindByPrimaryKey searchProjectId="${activeProjectId}" var="project"/>
          <fmt:message key="facetsets.dojo.node.label.indexed.sets" var="divLabel">
            <fmt:param value="${project.name}" />
          </fmt:message>
          <fmt:message key="facetsets.dojo.node.tooltip.indexed.sets" var="tooltip"/>
          <admin-beans:getIndexedFacetSets var="indexedFacetSets" projectId="${activeProjectId}" />
          <c:set var="isFolder" value="${false}"/>
          <c:if test="${not empty indexedFacetSets.nodes}">
            <c:set var="isFolder" value="${true}"/>
          </c:if>
          <admin-dojo:jsonObject>
              <admin-dojo:jsonValue name="id" value="rootIndexedFacetSetNode"/>
              <admin-dojo:jsonValue name="titleText" value="${divLabel}"/>
              <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
              <admin-dojo:jsonValue name="isFolder" value="${isFolder}"/>
          </admin-dojo:jsonObject>
        </c:if>
        
        <facets:facetSetFindAll var="all"/>
        <c:set var="isFolder" value="${false}"/>
        <c:if test="${not empty all}">
          <c:set var="isFolder" value="${true}"/>
        </c:if>
        <fmt:message key="facetsets.dojo.node.label.sets" var="title"/>
        <fmt:message key="facetsets.dojo.node.tooltip.sets" var="tooltip"/>
        <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="rootFacetSetNode"/>
            <admin-dojo:jsonValue name="titleText" value="${title}"/>
            <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
            <admin-dojo:jsonValue name="isFolder" value="${isFolder}"/>
        </admin-dojo:jsonObject>
        
        <fmt:message key="facetsets.dojo.node.label.sets.adaptors" var="title"/>
        <fmt:message key="facetsets.dojo.node.tooltip.sets.adaptors" var="tooltip"/>
        <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="adaptors"/>
            <admin-dojo:jsonValue name="titleText" value="${title}"/>
            <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
            <admin-dojo:jsonValue name="isFolder" value="${false}"/>
        </admin-dojo:jsonObject>
      </c:if>
      <c:if test="${nodeType == 'rootFacetSetNode'}">
        <facets:facetSetFindAll var="facetSets"/>
        <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.facetSetsComparator" var="comparator"/>
        <admin-ui:sort var="facetSets" items="${facetSets}" comparator="${comparator}" sortMode="asc"/>
        <c:forEach items="${facetSets}" var="facetSet">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="facetSet:${facetSet.id}"/>
            <admin-dojo:jsonValue name="titleText" value="${facetSet.name}"/>
            <admin-dojo:jsonValue name="toolTip" value="${facetSet.description}"/>
            <admin-dojo:jsonValue name="isFolder" value="${not empty facetSet.childFacets}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
      </c:if>
      <c:if test="${nodeType == 'rootIndexedFacetSetNode'}">
        <d:getvalueof var="projectId" bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"/>
        <admin-beans:getIndexedFacetSets var="indexedFacetSets" projectId="${projectId}" />
        <c:forEach var="indexedFacetSet" items="${indexedFacetSets.nodes}">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="indexedFacetSet:${indexedFacetSet.name}"/>
            <admin-dojo:jsonValue name="titleText" value="${indexedFacetSet.name}"/>
            <admin-dojo:jsonValue name="isFolder" value="${true}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
      </c:if>
      <c:if test="${nodeType == 'indexedFacetSet' || nodeType == 'indexedFacet'}">
        <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="node" var="dataNode"/>
        <admin-dojo:getSubObject jsonObjectData="${dataNode}" jsonSubObjectName="widgetId" var="parentNodeId"/>
        <d:getvalueof var="projectId" bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"/>
        <admin-beans:getIndexedFacetSets var="indexedFacets" projectId="${projectId}" parentId="${parentNodeId}" />
        <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.facetComparator" var="comparator"/>
        <admin-ui:sort var="facets" items="${indexedFacets.childFacets}" comparator="${comparator}" sortMode="undefined"/>
        <c:forEach items="${facets}" var="indexedFacet">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="indexedFacet:${indexedFacet.id}"/>
            <admin-dojo:jsonValue name="titleText" value="${indexedFacet.name}"/>
            <admin-dojo:jsonValue name="isFolder" value="${not empty indexedFacet.childFacets}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
      </c:if>
      <c:if test="${nodeType == 'facetSet' || nodeType == 'facet'}">
        <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="node" var="dataNode"/>
        <admin-dojo:getSubObject jsonObjectData="${dataNode}" jsonSubObjectName="widgetId" var="parentNodeId"/>
        <facets:baseFacetFindByPrimaryKey baseFacetId="${parentNodeId}" var="parentNode"/>
        <c:forEach items="${parentNode.childFacets}" var="facet">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="facet:${facet.id}"/>
            <admin-dojo:jsonValue name="titleText" value="${facet.name}"/>
            <admin-dojo:jsonValue name="toolTip" value="${facet.name}"/>
            <admin-dojo:jsonValue name="isFolder" value="${not empty facet.childFacets}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
      </c:if>
    </admin-dojo:jsonArray>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_sets_nodes.jsp#2 $$Change: 651448 $--%>
