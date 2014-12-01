<%--
  Page, showing "Content Items" tab of Topic page, when content items were categorized.
  This page is included into topic_content_items.jsp page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_content_items_categorized.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="topicId" var="topicId"/>
  <d:getvalueof param="searchEnvironment" var="searchEnvironment"/>
  <topic:topicFindByPrimaryKey topicId="${topicId}" var="currentTopic"/>

  <div id="paneContent">
    <h3>
      <fmt:message key="topic_content_items.title">
        <fmt:param>
          <c:out value="${currentTopic.name}"/>
        </fmt:param>
      </fmt:message>
    </h3>

    <c:set var="pageSize" value="20"/>
    
    <c:url var="itemsPaneUrl" value="${topicPath}/topic_content_items_pane.jsp">
      <c:param name="topicId" value="${currentTopic.id}"/>
      <c:param name="searchEnvironmentId" value="${searchEnvironment.id}"/>
      <c:param name="pageSize" value="${pageSize}"/>
      <c:param name="maxDocsPerSet" value="${pageSize}"/>
    </c:url>
    <tags:dojoPanePaging pageSize="${pageSize}"/>
    <div dojoType="dojox.layout.ContentPane" id="topicContentItemsPane" executeScripts="true" cacheContent="false"
         loadingMessage="<fmt:message key='tree.dojo.loading' />" scriptSeparation="false"
         href="${itemsPaneUrl}"></div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_content_items_categorized.jsp#2 $$Change: 651448 $--%>
