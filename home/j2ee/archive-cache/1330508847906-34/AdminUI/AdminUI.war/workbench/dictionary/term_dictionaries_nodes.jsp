<%--
JSP, used as controller for dojo dictionary tree. Receives dojo request, prepare and send responce in json format.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_dictionaries_nodes.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page xml="true">
  <d:importbean bean="/atg/searchadmin/adminui/Configuration" var="configuration" />

  <d:getvalueof var="action" param="action"/>
  <d:getvalueof var="data" param="data"/>

  <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="treeNodeType" var="nodeType"/>
  <admin-dojo:jsonObject>
    <admin-dojo:jsonArray name="children">
      <d:getvalueof var="maxRange" value="${configuration.treeBucketSize}"/>
      <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"
              var="activeProjectId"/>
      <c:choose>
        <%--- Dict ---%>
        <c:when test="${nodeType == 'rootNode'}">
          <dictionary:termDictionaryFindAll var="all"/>
          <c:set var="isFolder" value="${false}"/>
          <c:if test="${not empty all}">
            <c:set var="isFolder" value="${true}"/>
          </c:if>
          <fmt:message key="termdicts.dojo.node.label.sets" var="title"/>
          <fmt:message key="termdicts.dojo.node.tooltip.sets" var="tooltip"/>
          <admin-dojo:jsonObject>
              <admin-dojo:jsonValue name="id" value="rootDictNode"/>
              <admin-dojo:jsonValue name="titleText" value="${title}"/>
              <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
              <admin-dojo:jsonValue name="isFolder" value="${isFolder}"/>
          </admin-dojo:jsonObject>
          <fmt:message key="termdicts.dojo.node.label.inspection" var="title"/>
          <fmt:message key="termdicts.dojo.node.tooltip.inspection" var="tooltip"/>
          <admin-dojo:jsonObject>
              <admin-dojo:jsonValue name="id" value="rootDictInspectNode"/>
              <admin-dojo:jsonValue name="titleText" value="${title}"/>
              <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
              <admin-dojo:jsonValue name="isFolder" value="${true}"/>
          </admin-dojo:jsonObject>
        </c:when>
        <c:when test="${nodeType == 'rootDictNode'}">
          <dictionary:termDictionaryFindAll var="unsortedTermDicts"/>
          <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.termDictionaryComparator"
                        var="comparator"/>
          <admin-ui:sort var="termDicts" items="${unsortedTermDicts}" comparator="${comparator}"
                         sortMode="name asc"/>
          <c:forEach items="${termDicts}" var="termDict">
            <admin-dojo:jsonObject>
              <admin-dojo:jsonValue name="id" value="${fn:escapeXml(termDict.nodeType)}:${termDict.id}"/>
              <admin-dojo:jsonValue name="titleText" value="${termDict.name}"/>
              <admin-dojo:jsonValue name="toolTip" value="${termDict.description}"/>
              <admin-dojo:jsonValue name="isFolder" value="${not empty termDict.children}"/>
            </admin-dojo:jsonObject>
          </c:forEach>
        </c:when>

        <c:when test="${nodeType == 'termDictionary' or nodeType == 'term'}">
          <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="node" var="dataNode"/>
          <admin-dojo:getSubObject jsonObjectData="${dataNode}" jsonSubObjectName="widgetId" var="parentNodeId"/>
          <dictionary:termParentNodeFindByPrimaryKey termParentNodeId="${parentNodeId}" var="parentNode"/>

          <c:set var="termsSize" value="${fn:length(parentNode.children)}" />

          <%--- maxRange *2 terms without virtual nodes----%>
          <c:if test="${termsSize > maxRange * 2}">
            <c:set var="startIndex" value="0" />
            <c:set var="endIndex" value="${startIndex + maxRange - 1}" />
            <c:forEach items="${parentNode.children}" var="term" varStatus="step">
              <c:if test="${step.index eq startIndex}">
                <c:set var="virtualTitle" value="${term.name}" />
              </c:if>
              <c:if test="${step.index eq endIndex}">
                <c:set var="virtualTitle" value="${virtualTitle} - ${term.name}" />
                <fmt:message key="tree.term.dict.virtual.nodes.label" var="label" />
                <admin-dojo:jsonObject>
                  <admin-dojo:jsonValue name="id" value="virtualNode:${parentNodeId}:${startIndex}:${endIndex}"/>
                  <admin-dojo:jsonValue name="titleText" value="${virtualTitle}"/>
                  <admin-dojo:jsonValue name="toolTip" value="${startIndex + 1}-${endIndex + 1} ${label} ${virtualTitle}"/>
                  <admin-dojo:jsonValue name="startIndex" value="${startIndex}"/>
                  <admin-dojo:jsonValue name="endIndex" value="${endIndex}"/>
                  <admin-dojo:jsonValue name="toolTipLabel" value=" ${label} "/>
                  <admin-dojo:jsonValue name="isFolder" value="${true}"/>
                </admin-dojo:jsonObject>
                <c:set var="startIndex" value="${endIndex + 1}" />
                <c:set var="endIndex" value="${startIndex + maxRange - 1}" />
                <c:if test="${endIndex > termsSize - 1}">
                  <c:set var="endIndex" value="${termsSize - 1}" />
                </c:if>
              </c:if>
            </c:forEach>
          </c:if>

          <%--- maxRange *2 terms without virtual nodes----%>
          <c:if test="${termsSize <= maxRange * 2}">
            <c:forEach items="${parentNode.children}" var="term" varStatus="step">
              <c:set var="range" value="30" />
              <admin-dojo:jsonObject>
                <admin-dojo:jsonValue name="id" value="${fn:escapeXml(term.nodeType)}:${term.id}"/>
                <admin-dojo:jsonValue name="titleText" value="${term.name}"/>
                <admin-dojo:jsonValue name="isFolder" value="${not empty term.children}"/>
              </admin-dojo:jsonObject>
            </c:forEach>
          </c:if>
        </c:when>
        
        <%-- Dictionary Inspection --%>
        <c:when test="${nodeType == 'rootDictInspectNode'}">
          <fmt:message key="termdicts.dojo.node.label.saosets" var="title"/>
          <fmt:message key="termdicts.dojo.node.tooltip.saosets" var="tooltip"/>
          <admin-dojo:jsonObject>
            <admin-dojo:jsonValue name="id" value="saoSetsNode"/>
            <admin-dojo:jsonValue name="titleText" value="${title}"/>
            <admin-dojo:jsonValue name="toolTip" value="${tooltip}"/>
            <admin-dojo:jsonValue name="isFolder" value="${not empty activeProjectId}"/>
          </admin-dojo:jsonObject>
        </c:when>
        <c:when test="${nodeType == 'saoSetsNode'}">
          <c:if test="${not empty activeProjectId}">
            <admin-beans:getSAOSetsByActiveProject var="saoSets"/>
            <c:forEach items="${saoSets}" var="saoSet">
              <admin-dojo:jsonObject>
                <c:if test="${empty saoSet.projectId}">
                  <c:if test="${not empty saoSet.id}">
                    <c:set var="defaultId" value="${saoSet.id}"/>
                  </c:if>
                  <c:if test="${empty saoSet.id}">
                    <c:set var="defaultId" value="default"/>
                  </c:if>
                  <admin-dojo:jsonValue name="id" value="saoDefault:${defaultId}"/>
                </c:if>
                <c:if test="${not empty saoSet.projectId}">
                  <admin-dojo:jsonValue name="id" value="sao:${saoSet.id}"/>
                </c:if>
                <admin-dojo:jsonValue name="titleText" value="${saoSet.name}"/>
                <admin-dojo:jsonValue name="toolTip" value="${saoSet.name}"/>
              </admin-dojo:jsonObject>
            </c:forEach>
          </c:if>
        </c:when>
        <c:otherwise>
          <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="node" var="dataNode"/>
          <admin-dojo:getSubObject jsonObjectData="${dataNode}" jsonSubObjectName="widgetId" var="parentNodeId"/>
          <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="indexItems" var="indexItems"/>
          <dictionary:termParentNodeFindByPrimaryKey termParentNodeId="${parentNodeId}" var="parentNode"/>
          <c:forEach items="${parentNode.children}" var="term" varStatus="step">
            <c:if test="${indexItems ne null}">
              <c:if test="${step.index >= indexItems and step.index < indexItems + maxRange}">
                <admin-dojo:jsonObject>
                  <admin-dojo:jsonValue name="id" value="${fn:escapeXml(term.nodeType)}:${term.id}"/>
                  <admin-dojo:jsonValue name="titleText" value="${term.name}"/>
                  <admin-dojo:jsonValue name="isFolder" value="${not empty term.children}"/>
                </admin-dojo:jsonObject>
              </c:if>
            </c:if>
            <c:if test="${indexItems eq null}">
              <admin-dojo:jsonObject>
                <admin-dojo:jsonValue name="id" value="${fn:escapeXml(term.nodeType)}:${term.id}"/>
                <admin-dojo:jsonValue name="titleText" value="${term.name}"/>
                <admin-dojo:jsonValue name="isFolder" value="${not empty term.children}"/>
              </admin-dojo:jsonObject>
            </c:if>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </admin-dojo:jsonArray>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/term_dictionaries_nodes.jsp#2 $$Change: 651448 $--%>
