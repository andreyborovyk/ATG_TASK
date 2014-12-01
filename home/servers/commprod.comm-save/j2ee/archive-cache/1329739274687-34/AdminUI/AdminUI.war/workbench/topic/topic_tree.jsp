<%--
JSP, used as controller for dojo topic tree. Receives dojo request, prepare and send responce in json format.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_tree.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page xml="true">
  <d:getvalueof var="action" param="action"/>
  <d:getvalueof var="data" param="data"/>

  <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="treeNodeType" var="nodeType"/>
  <admin-dojo:jsonObject>
    <admin-dojo:jsonArray name="children">
      <c:choose>
        <c:when test="${nodeType == 'rootNode'}">
          <topic:topicSetFindAll var="all"/>
          <c:set var="isFolder" value="${false}"/>
          <c:if test="${not empty all}">
            <c:set var="isFolder" value="${true}"/>
          </c:if>
          <fmt:message key="topicset.dojo.node.label.sets" var="title"/>
          <admin-dojo:jsonObject>
              <admin-dojo:jsonValue name="id" value="rootTopicSetNode"/>
              <admin-dojo:jsonValue name="titleText" value="${title}"/>
              <admin-dojo:jsonValue name="isFolder" value="${isFolder}"/>
          </admin-dojo:jsonObject>
        </c:when>
        <c:when test="${nodeType == 'rootTopicSetNode'}">
          <topic:topicSetFindAll var="unsortedTopicSets"/>
          <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.topicSetComparator" var="comparator"/>
          <admin-ui:sort var="topicSets" items="${unsortedTopicSets}" comparator="${comparator}" sortMode="name asc"/>
          <c:forEach items="${topicSets}" var="topicSet">
            <admin-dojo:jsonObject>
              <admin-dojo:jsonValue name="id" value="${topicSet.nodeType}:${topicSet.id}"/>
              <admin-dojo:jsonValue name="titleText" value="${topicSet.name}"/>
              <admin-dojo:jsonValue name="toolTip" value="${topicSet.description}"/>
              <admin-dojo:jsonValue name="isFolder" value="${not empty topicSet.children}"/>
              <%-- !don't insert \n before of after  c:out --%>
            </admin-dojo:jsonObject>
          </c:forEach>
        </c:when>
        <c:otherwise>
          <admin-dojo:getSubObject jsonObjectData="${data}" jsonSubObjectName="node" var="dataNode"/>
          <admin-dojo:getSubObject jsonObjectData="${dataNode}" jsonSubObjectName="widgetId" var="parentNodeId"/>
          <c:if test="${nodeType == 'TopicSet'}">
            <topic:topicSetFindByPrimaryKey topicSetId="${parentNodeId}" var="parent"/>
          </c:if>
          <c:if test="${nodeType == 'Topic'}">
            <topic:topicFindByPrimaryKey topicId="${parentNodeId}" var="parent"/>
          </c:if>
          <c:forEach items="${parent.children}" var="topic">
            <admin-dojo:jsonObject>
              <admin-dojo:jsonValue name="id" value="${topic.nodeType}:${topic.id}"/>
              <admin-dojo:jsonValue name="titleText" value="${topic.name}"/>
              <admin-dojo:jsonValue name="toolTip" value="${topic.description}"/>
              <admin-dojo:jsonValue name="isFolder" value="${not empty topic.children}"/>
            </admin-dojo:jsonObject>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    </admin-dojo:jsonArray>
  </admin-dojo:jsonObject>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_tree.jsp#2 $$Change: 651448 $--%>
