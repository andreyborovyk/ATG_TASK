<%--
JSP, used as controller for dojo term weights tree. Receives dojo request, prepare and send responce in json format.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_nodes.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page xml="true">

  <d:getvalueof var="action" param="action"/>
  <d:getvalueof var="data" param="data"/>
  <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="treeNodeType" var="nodeType"/>
  <admin-dojo:jsonObject>
    <admin-dojo:jsonArray name="children">
      <c:if test="${nodeType == 'rootNode'}">
        <termweights:termWeightSetFindAll var="all"/>
        <c:set var="isFolder" value="${false}"/>
        <c:if test="${not empty all}">
          <c:set var="isFolder" value="${true}"/>
        </c:if>
        <fmt:message key="termweights.dojo.node.label.sets" var="title"/>
        <fmt:message key="termweights.dojo.node.tooltip.sets" var="tooltip"/>
        <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="rootTermWeightNode"/>
            <admin-dojo:jsonValue name="titleText" value="${title}"/>
            <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
            <admin-dojo:jsonValue name="isFolder" value="${isFolder}"/>
        </admin-dojo:jsonObject>
      </c:if>
      <c:if test="${nodeType == 'rootTermWeightNode'}">
        <termweights:termWeightSetFindAll var="unsortedTermWeightSets"/>
        <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.termWeightSetComparator"
                      var="comparator"/>
        <admin-ui:sort var="termWeightSets" items="${unsortedTermWeightSets}" comparator="${comparator}"
                       sortMode="name asc"/>
        <c:forEach items="${termWeightSets}" var="termWeightSet">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="termWeightSet:${termWeightSet.id}"/>
            <admin-dojo:jsonValue name="titleText" value="${termWeightSet.name}"/>
            <admin-dojo:jsonValue name="toolTip" value="${termWeightSet.description}"/>
            <admin-dojo:jsonValue name="isFolder" value="${false}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
      </c:if>
    </admin-dojo:jsonArray>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_nodes.jsp#2 $$Change: 651448 $--%>
