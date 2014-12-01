<%--
  JPS, showing content items tab on edit topic page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_content_items.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="topicId" var="topicId"/>

  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/dojo_pane_paging.js"></script>

  <%-- Navigation tabs --%>
  <d:include page="topic_navigation.jsp">
    <d:param name="topicId" value="${topicId}"/>
    <d:param name="tab" value="3"/>
  </d:include>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId" var="activeProjectId"/>
    <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.environmentId" var="environmentId"/>

    <c:choose>
      <c:when test="${empty activeProjectId}">
        <d:include page="topic_content_items_no_active_project.jsp"/>
      </c:when>
      <c:otherwise>
        <admin-beans:getTopicContentItemsCategorizationStatus
            var="statusBean" topicId="${topicId}" activeProjectId="${activeProjectId}"/>
        <c:if test="${statusBean.status eq 'not_a_part'}">
          <d:include page="topic_content_items_not_a_part.jsp">
            <d:param name="topicId" value="${topicId}"/>
          </d:include>
        </c:if>
        <c:if test="${statusBean.status eq 'not_indexed'}">
          <d:include page="topic_content_items_not_indexed.jsp">
            <d:param name="topicId" value="${topicId}"/>
          </d:include>
        </c:if>
        <c:if test="${statusBean.status eq 'categorized'}">
          <%--Find latest successfully indexed project--%>
          <admin-beans:getSearchEnvironment var="searchEnvironment" environmentId="${environmentId}"/>
          <c:choose>
            <c:when test="${searchEnvironment != null}">
              <d:include page="topic_content_items_categorized.jsp">
                <d:param name="topicId" value="${topicId}"/>
                <d:param name="searchEnvironment" value="${searchEnvironment}"/>
              </d:include>
            </c:when>
            <c:otherwise>
              <d:include page="topic_content_items_not_indexed.jsp">
                 <d:param name="topicId" value="${topicId}"/>
               </d:include>
            </c:otherwise>
          </c:choose>
        </c:if>
      </c:otherwise>
    </c:choose>
    <c:url value="${topicPath}/topic_content_items.jsp" var="successURL">
      <c:param name="topicId" value="${topicId}"/>
    </c:url>
    <d:form method="POST" action="${successURL}">
      <d:input type="hidden" name="successURL"
               bean="/atg/searchadmin/workbenchui/formhandlers/RefreshTopicContentItemsCategorizationFormHandler.successURL"
               value="${successURL}"/>
      <d:input
          bean="/atg/searchadmin/workbenchui/formhandlers/RefreshTopicContentItemsCategorizationFormHandler.checkStop"
          type="submit" name="checkStop" style="display:none" id="submitInput" iclass="formsubmitter"/>
    </d:form>
  </div>
  <script type="text/javascript">
    function refreshRightPane(obj) {
      document.getElementById("submitInput").click();
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/topic_content_items.jsp#2 $$Change: 651448 $--%>
