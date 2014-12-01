<%--
  JSP, showing top navigation panel on edit topic page. This page is included into topic.jsp page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_navigation.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="tab" var="tab"/>
  <d:getvalueof param="topicId" var="topicId"/>
  <script>
    var topicTab = ${tab};
    function switchTab(el, toTab) {
      if (toTab == 3 || topicTab == 3 || topicTab == toTab) {
        return loadRightPanel(el.href);
      } else {
        changeTab(toTab);
        switchContent(toTab, 'content');
        topicTab = toTab;
        if (toTab == 1) top.focusFirstFormElement(window);
        return false;
      }
    }
  </script>
  <div id="subNav" dojoType="dojox.layout.ContentPane" layoutAlign="top">
    <ul>
      <li <c:if test="${tab == '1'}">class="current"</c:if>>
        <c:url var="topicUrl1" value="${topicPath}/topic.jsp">
          <c:param name="topicId" value="${topicId}"/>
          <c:param name="tab" value="1"/>
        </c:url>
        <a href="${topicUrl1}" onclick="return switchTab(this, 1);">
          <fmt:message key="topic_navigation.tabs.first"/>
        </a>
      </li>
      <li <c:if test="${tab == '2'}">class="current"</c:if>>
        <c:url var="topicUrl2" value="${topicPath}/topic.jsp">
          <c:param name="topicId" value="${topicId}"/>
          <c:param name="tab" value="2"/>
        </c:url>
        <a href="${topicUrl2}" onclick="return switchTab(this, 2);">
          <fmt:message key="topic_navigation.tabs.second"/>
        </a>
      </li>
      <li <c:if test="${tab == '3'}">class="current"</c:if>>
        <c:url value="${topicPath}/topic_content_items.jsp" var="contentItemsUrl">
          <c:param name="topicId" value="${topicId}"/>
        </c:url>
        <a href="${contentItemsUrl}" onclick="return switchTab(this, 3);">
          <fmt:message key="topic_navigation.tabs.third"/>
        </a>
      </li>
    </ul>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_navigation.jsp#2 $$Change: 651448 $--%>
