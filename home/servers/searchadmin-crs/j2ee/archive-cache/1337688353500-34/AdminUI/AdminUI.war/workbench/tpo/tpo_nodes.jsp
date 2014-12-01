<%--
JSP, used as controller for dojo topic tree. Receives dojo request, prepare and send responce in json format.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_nodes.jsp#2 $$Change: 651448 $
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
        <fmt:message key="tpo_sets.dojo.node.label.sets" var="title"/>
        <fmt:message key="tpo_sets.dojo.node.tooltip.sets" var="tooltip"/>
        <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="rootTpoNode"/>
            <admin-dojo:jsonValue name="titleText" value="${title}"/>
            <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
            <admin-dojo:jsonValue name="isFolder" value="${true}"/>
        </admin-dojo:jsonObject>
      </c:if>
      <c:if test="${nodeType == 'rootTpoNode'}">
        <tpo:textProcessingOptionsSetFindByLevel level="index" var="indexTpoSets"/>
        <c:set var="isIndexFolder" value="${false}"/>
        <c:if test="${not empty indexTpoSets}">
          <c:set var="isIndexFolder" value="${true}"/>
        </c:if>
        <fmt:message key="tpo_sets.dojo.node.label.index.sets" var="title"/>
        <fmt:message key="tpo_sets.dojo.node.tooltip.index.sets" var="tooltip"/>
        <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="indexTpoNode"/>
            <admin-dojo:jsonValue name="titleText" value="${title}"/>
            <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
            <admin-dojo:jsonValue name="isFolder" value="${isIndexFolder}"/>
        </admin-dojo:jsonObject>
        
        <tpo:textProcessingOptionsSetFindByLevel level="content" var="contentTpoSets"/>
        <c:set var="isContentFolder" value="${false}"/>
        <c:if test="${not empty contentTpoSets}">
          <c:set var="isContentFolder" value="${true}"/>
        </c:if>
        <fmt:message key="tpo_sets.dojo.node.label.content.sets" var="title"/>
        <fmt:message key="tpo_sets.dojo.node.tooltip.content.sets" var="tooltip"/>
        <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="contentTpoNode"/>
            <admin-dojo:jsonValue name="titleText" value="${title}"/>
            <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
            <admin-dojo:jsonValue name="isFolder" value="${isContentFolder}"/>
        </admin-dojo:jsonObject>
      </c:if>
      <c:if test="${nodeType == 'indexTpoNode'}">
        <tpo:textProcessingOptionsSetFindByLevel level="index" var="tpoSets"/>
        <c:forEach items="${tpoSets}" var="tpoSet">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="index:${tpoSet.id}"/>
            <admin-dojo:jsonValue name="titleText" value="${tpoSet.name}"/>
            <admin-dojo:jsonValue name="toolTip" value="${tpoSet.name}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
      </c:if>
      <c:if test="${nodeType == 'contentTpoNode'}">
        <tpo:textProcessingOptionsSetFindByLevel level="content" var="tpoSets"/>
        <c:forEach items="${tpoSets}" var="tpoSet">
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="content:${tpoSet.id}"/>
            <admin-dojo:jsonValue name="titleText" value="${tpoSet.name}"/>
            <admin-dojo:jsonValue name="toolTip" value="${tpoSet.name}"/>
          </admin-dojo:jsonObject>
        </c:forEach>
      </c:if>
    </admin-dojo:jsonArray>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_nodes.jsp#2 $$Change: 651448 $--%>
