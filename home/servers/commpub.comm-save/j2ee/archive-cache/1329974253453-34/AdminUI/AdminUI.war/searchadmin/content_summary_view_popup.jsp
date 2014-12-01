<%--
  JSP,used to view content summary of current task.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/content_summary_view_popup.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
  --%>
<%@ include file="/templates/top.jspf" %>
<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>

  <div class="content">
    <admin-beans:getSyncTaskHistoryInfo var="syncTaskHistoryInfo" taskId="${syncTaskId}" projectId="${projectId}" />
    <c:if test="${syncTaskHistoryInfo == null}">
      <p><fmt:message key="content_summary_view.description" /></p>
      <p><fmt:message key="content_summary_view.unavailable" /></p>
    </c:if>
    <c:if test="${syncTaskHistoryInfo != null}">
      <%-- synchronization section --%>
      <h3><fmt:message key="synchronization_index_summary.synchronization.selected"/></h3>
      <d:include page="project_index_summary_sync_section.jsp">
        <d:param name="syncTaskHistoryInfo" value="${syncTaskHistoryInfo}"/>
        <d:param name="projectId" value="${projectId}"/>
        <d:param name="syncTaskId" value="${syncTaskHistoryInfo.syncTaskId}"/>
      </d:include>
      <br/>
      <common:syncTaskFindByPrimaryKey syncTaskId="${syncTaskHistoryInfo.syncTaskId}" var="syncTask"/>
      <c:if test="${syncTask.index eq null}">
        <fmt:message key="content_summary_view.message.statistics.empty"/>
      </c:if>
      <c:if test="${syncTask.index ne null}">
        <%-- statistics section --%>
        <d:include page="project_index_summary_content.jsp">
          <d:param name="projectId" value="${projectId}"/>
          <d:param name="syncTaskHistoryInfo" value="${syncTaskHistoryInfo}"/>
          <d:param name="popupView" value="true"/>
        </d:include>
      </c:if>
    </c:if>
  </div>
  <div class="footer" id="popupFooter">
     <input type="button" value="<fmt:message key='content_summary_view.cancel.button'/>"
            onclick="closePopUp()"
            title="<fmt:message key='content_summary_view.cancel.button.tooltip'/>"/>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/content_summary_view_popup.jsp#1 $$Change: 651360 $--%>
