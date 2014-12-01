<%--
Index summary overview statistics  page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/index_verification_overview.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf"%>

<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="partitionId" var="partitionId"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>
  <admin-beans:getIndexingOverview var="overview" projectId="${projectId}"
      partitionId="${partitionId}" syncTaskId="${syncTaskId}" />
  <admin-beans:getSyncTaskHistoryInfo var="syncTaskHistoryInfo" taskId="${syncTaskId}" projectId="${projectId}" />
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <admin-beans:getIndexSummaryInfo taskId="${syncTaskHistoryInfo.syncTaskId}" var="indexSummaryInfo" />
      <br/>
      <fmt:message key="index_verification_overview.back_to" />
      <d:a href="${pageContext.request.contextPath}/searchadmin/project_index_summary.jsp" onclick="return loadRightPanel(this.href);">
        <d:param name="projectId" value="${projectId}" />
        <d:param name="syncTaskId" value="${syncTaskId}"/>
        <fmt:message key="index_verification_overview.back_to.link" />
      </d:a>
      <c:set var="firstContent" value="${true}"/>
      <c:set var="contentList">
        <c:forEach var="item" items="${indexSummaryInfo}">
          <c:if test="${partitionId != item.partition.id and not item.emptyIndexedItems}">
            <c:if test="${not firstContent}">,</c:if>
            <c:set var="firstContent" value="${false}"/>
            <d:a href="${pageContext.request.contextPath}/searchadmin/index_verification_overview.jsp" onclick="return loadRightPanel(this.href);">
              <d:param name="projectId" value="${projectId}" />
              <d:param name="syncTaskId" value="${syncTaskId}" />
              <d:param name="partitionId" value="${item.partition.id}"/>
              <c:out value="${item.partition.name}" />
            </d:a>
          </c:if>
        </c:forEach>
      </c:set>
      <c:if test="${not firstContent}">
        &nbsp; &nbsp;
        <fmt:message key="index_verification_overview.see_also" />
        ${contentList}
      </c:if>

      <d:include page="index_verification_overview_content.jsp">
        <d:param name="syncTaskHistoryInfo" value="${syncTaskHistoryInfo}"/>
        <d:param name="projectId" value="${projectId}"/>
        <d:param name="syncTaskId" value="${syncTaskHistoryInfo.syncTaskId}"/>
      </d:include>
      <br/>
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/index_verification_overview.jsp#1 $$Change: 651360 $--%>
