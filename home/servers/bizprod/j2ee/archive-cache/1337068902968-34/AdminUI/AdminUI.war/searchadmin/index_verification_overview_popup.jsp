<%--
  JSP,used to view content summary of current task.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/index_verification_overview_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>
  <d:getvalueof param="partitionId" var="partitionId"/>
  <c:url value="/searchadmin/content_summary_view_popup.jsp" var="selectedIndexUrl">
    <c:param name="projectId" value="${projectId}"/>
    <c:param name="syncTaskId" value="${syncTaskId}"/>
  </c:url>
  
  
  <admin-beans:getIndexingOverview var="overview" projectId="${projectId}"
    partitionId="${partitionId}" syncTaskId="${syncTaskId}" />
  <admin-beans:getSyncTaskHistoryInfo var="syncTaskHistoryInfo" taskId="${syncTaskId}" projectId="${projectId}" />
  <admin-beans:getIndexSummaryInfo taskId="${syncTaskHistoryInfo.syncTaskId}" var="indexSummaryInfo" />
      
  <div class="content">
    <fmt:message key="index_verification_overview.back_to" />
    <a href="{'${selectedIndexUrl}'}" onclick="return showPopUp('${selectedIndexUrl}');">
      <fmt:message key="index_verification_overview.back_to.link" />
    </a>
    <c:set var="firstContent" value="${true}" />
    <c:set var="contentList">
      <c:forEach var="item" items="${indexSummaryInfo}">
        <c:if test="${partitionId != item.partition.id and not item.emptyIndexedItems}">
          <c:if test="${not firstContent}">,</c:if>
          <c:set var="firstContent" value="${false}"/>
          <c:url value="/searchadmin/index_verification_overview_popup.jsp" var="viewPartitionUrl">
            <c:param name="projectId" value="${projectId}"/>
            <c:param name="partitionId" value="${item.partition.id}"/>
            <c:param name="syncTaskId" value="${syncTaskId}"/>
          </c:url>
          <a href="{'${viewPartitionUrl}'}" onclick="return showPopup('${viewPartitionUrl}');">
            <d:param name="projectId" value="${projectId}" />
            <d:param name="syncTaskId" value="${syncTaskId}" />
            <d:param name="partitionId" value="${item.partition.id}"/>
            <c:out value="${item.partition.name}" />
          </a>
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
  <div class="footer" id="popupFooter">
   <input type="button" value="<fmt:message key='content_summary_view.cancel.button'/>"
          onclick="closePopUp()"
          title="<fmt:message key='content_summary_view.cancel.button.tooltip'/>"/>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/index_verification_overview_popup.jsp#2 $$Change: 651448 $--%>