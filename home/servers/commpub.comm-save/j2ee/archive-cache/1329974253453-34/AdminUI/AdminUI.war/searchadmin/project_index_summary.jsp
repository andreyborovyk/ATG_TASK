<%--
Index summary page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_summary.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>
  <d:getvalueof param="projectId" var="projectId"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <admin-beans:getSyncTaskHistoryInfo var="syncTaskHistoryInfo" taskId="${syncTaskId}" projectId="${projectId}" />
      <c:if test="${syncTaskHistoryInfo == null}">
        <p><fmt:message key="project_index_summary.description" /></p>
        <p><fmt:message key="project_index_review.unavailable" /></p>
      </c:if>
      <c:if test="${syncTaskHistoryInfo != null}">
        <%-- synchronization section --%>
        <h3><fmt:message key="synchronization_index_summary.synchronization"/></h3>
        <d:include page="project_index_summary_sync_section.jsp">
          <d:param name="syncTaskHistoryInfo" value="${syncTaskHistoryInfo}"/>
          <d:param name="projectId" value="${projectId}"/>
          <d:param name="syncTaskId" value="${syncTaskHistoryInfo.syncTaskId}"/>
        </d:include>
        <common:syncTaskFindByPrimaryKey syncTaskId="${syncTaskHistoryInfo.syncTaskId}" var="syncTask"/>
        <c:if test="${syncTask.index eq null}">
          <fmt:message key="project_index_summary.message.statistics.empty"/>
        </c:if>
        <c:if test="${syncTask.index ne null}">
          <%-- statistics section --%>
          <d:include page="project_index_summary_content.jsp">
            <d:param name="projectId" value="${projectId}"/>
            <d:param name="syncTaskHistoryInfo" value="${syncTaskHistoryInfo}"/>
          </d:include>
        </c:if>
      </c:if>
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_summary.jsp#1 $$Change: 651360 $--%>
