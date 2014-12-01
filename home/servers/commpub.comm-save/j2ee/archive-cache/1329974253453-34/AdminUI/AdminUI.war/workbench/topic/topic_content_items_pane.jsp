<%--
JSP page used by Topic content items pane. Retrieves Content Items of current Active Search Project attached to the given Topic.
Performs browse request to the given Environment.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_content_items_pane.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="pageNum" var="pageNum"/>
  <d:getvalueof param="pageSize" var="pageSize"/>
  <d:getvalueof param="topicId" var="topicId"/>
  <d:getvalueof param="searchEnvironmentId" var="searchEnvironmentId"/>

  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject" var="activeProject"/>
  <admin-beans:getSearchEnvironment var="searchEnvironment" environmentId="${searchEnvironmentId}"/>
  <topic:topicFindByPrimaryKey topicId="${topicId}" var="topic"/>

  <topic:topicGetTopicPath topicId="${topicId}" var="fullTopicPath"/>
  <admin-beans:getIndexedItems var="category" startingCategory="/Topics${fullTopicPath}"
      searchEnvironment="${searchEnvironment}" pageNum="${pageNum}" pageSize="${pageSize}" />
  <c:if test="${empty category.nodes}">
    <fmt:message key="topic_content_items_pane.no.items" />
  </c:if>
  <c:if test="${not empty category.nodes}">
  <table class="form"><tr><td>
    <table class="data simple" cellpadding="0" cellspacing="0">
      <c:forEach items="${category.nodes}" var="document">
        <c:set var="title" value="${document.meta[0].content}" />
        <c:set var="type" value="${document.meta[1].content}" />
        <fmt:message key="topic_content_items_pane.format" var="tooltipItem">
          <fmt:param><fmt:message key="topic_content_items_pane.doc.${type}" /></fmt:param>
        </fmt:message>
        <tr>
          <td width="20" class="fileIcon${type}" title="${tooltipItem}"></td>
          <td><div>${title}</div><div>${document.url}</div></td>
        </tr>
      </c:forEach>
    </table>
  </td></tr></table>
  </c:if>
  <script type="text/javascript">
    //fire afterContentPaneLoad dojo event.
    dojo.addOnLoad(function() {
      if (firstLoad){
        dojoLoadedCallback(dijit.byId('topicContentItemsPane'));
        firstLoad = false;
      }
      afterContentPaneLoad({totalCount:${category.total},itemsDisplayed:${fn:length(category.nodes)}});
    });
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_content_items_pane.jsp#2 $$Change: 651448 $--%>
