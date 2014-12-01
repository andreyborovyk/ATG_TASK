<%@ page import="atg.searchadmin.repository.beans.queryrule.QueryRuleGroup" %>
<%--
JSP, used as controller for dojo query rule set tree. Receives dojo request, prepare and send responce in json format.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/qr_nodes.jsp#2 $$Change: 651448 $
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
        <queryrule:queryRuleSetFindAll var="queryRuleSets"/>
        <c:set var="isFolder" value="${false}"/>
        <c:if test="${not empty queryRuleSets}">
          <c:set var="isFolder" value="${true}"/>
        </c:if>
        <fmt:message key="querysets.dojo.node.label.sets" var="title"/>
        <fmt:message key="querysets.dojo.node.tooltip.sets" var="tooltip"/>
        <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="rootQrNode"/>
            <admin-dojo:jsonValue name="titleText" value="${title}"/>
            <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
            <admin-dojo:jsonValue name="isFolder" value="${isFolder}"/>
        </admin-dojo:jsonObject>
      </c:if>
      <c:if test="${nodeType == 'rootQrNode'}">
        <queryrule:queryRuleSetFindAll var="unsortedQueryRuleSets"/>
        <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.queryRuleSetComparator" var="comparator"/>
        <admin-ui:sort var="queryRuleSets" items="${unsortedQueryRuleSets}" comparator="${comparator}" sortMode="name asc"/>
        <c:forEach items="${queryRuleSets}" var="queryRuleSet">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="${queryRuleSet.nodeType}:${queryRuleSet.id}"/>
            <admin-dojo:jsonValue name="titleText" value="${queryRuleSet.name}"/>
            <admin-dojo:jsonValue name="toolTip" value="${queryRuleSet.description}"/>
            <admin-dojo:jsonValue name="isFolder" value="${not empty queryRuleSet.childQueryRuleGroups}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
      </c:if>
      <c:if test="${nodeType == 'queryRuleSet'}">
        <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="node" var="dataNode"/>
        <admin-dojo:getSubObject jsonObjectData="${dataNode}" jsonSubObjectName="widgetId" var="parentNodeId"/>
        <queryrule:queryRuleSetFindByPrimaryKey var="queryRuleSet" queryRuleSetId="${parentNodeId}"/>
        <c:forEach items="${queryRuleSet.childQueryRuleGroups}" var="queryRuleGroup">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="${queryRuleGroup.nodeType}:${queryRuleGroup.id}"/>
            <admin-dojo:jsonValue name="titleText" value="${queryRuleGroup.name}"/>
            <admin-dojo:jsonValue name="toolTip" value="${queryRuleGroup.name}"/>
            <admin-dojo:jsonValue name="isFolder" value="${not empty queryRuleGroup.childQueryRuleGroups
              || not empty queryRuleGroup.childQueryRules}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
      </c:if>
      <c:if test="${nodeType == 'queryRuleGroup'}">
        <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="node" var="dataNode"/>
        <admin-dojo:getSubObject jsonObjectData="${dataNode}" jsonSubObjectName="widgetId" var="parentNodeId"/>
        <queryrule:queryRuleGroupFindByPrimaryKey var="queryRuleGroup" queryRuleGroupId="${parentNodeId}"/>
        <c:forEach items="${queryRuleGroup.childQueryRuleGroups}" var="childQueryRuleGroup">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="${childQueryRuleGroup.nodeType}:${childQueryRuleGroup.id}"/>
            <admin-dojo:jsonValue name="titleText" value="${childQueryRuleGroup.name}"/>
            <admin-dojo:jsonValue name="toolTip" value="${childQueryRuleGroup.name}"/>
            <admin-dojo:jsonValue name="isFolder" value="${not empty childQueryRuleGroup.childQueryRuleGroups
              || not empty childQueryRuleGroup.childQueryRules}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
        <c:forEach items="${queryRuleGroup.childQueryRules}" var="queryRule">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="queryRule:${queryRule.id}"/>
            <admin-dojo:jsonValue name="titleText" value="${queryRule.name}"/>
            <admin-dojo:jsonValue name="toolTip" value="${queryRule.name}"/>
            <admin-dojo:jsonValue name="isFolder" value="${false}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
      </c:if>
    </admin-dojo:jsonArray>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/qr_nodes.jsp#2 $$Change: 651448 $--%>
