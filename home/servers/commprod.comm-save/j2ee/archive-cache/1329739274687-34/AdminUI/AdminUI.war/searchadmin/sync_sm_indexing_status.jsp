<%--
Indexing status section of the  synchronization status monitor page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_sm_indexing_status.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf"%>
<d:page>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>
  <d:getvalueof param="isWaitingForUserInput" var="isWaitingForUserInput"/>
  <table class="form" cellspacing="0" cellpadding="0">
    <thead>
      <tr>
        <td colspan="2">
          <span class="h3Bold"><fmt:message key="synchronization_status_monitor.indexing_status.title"/></span>
          <span class="h3Bold"><c:out value="${syncTaskHistoryInfo.syncTaskName}"/></span>
          <span class="h3Normal">
            <tags:join items="${syncTaskHistoryInfo.contentSets}" var="contentSet" prefix=" (" postfix=")" delimiter=", ">
              ${contentSet.name}
            </tags:join>
          </span>
        </td>
      </tr>
    </thead>
    <tbody>
    <%--Indexing Status--%>
    <tr>
      <td class="label"><fmt:message key="synchronization_status_monitor.top.table.header.sync.status"/></td>
      <td>
        <c:choose>
          <c:when test="${syncTaskId ne null}">
            <c:choose>
              <c:when test="${generalSyncTaskStatus eq 'Cancelled' or generalSyncTaskStatus eq 'Failed' or
                              generalSyncTaskStatus eq 'Succeeded' or generalSyncTaskStatus eq 'finished_with_error' or
                              generalSyncTaskStatus eq 'deploying'}">
                <strong><fmt:message key="synchronization_status_monitor.indexing_status.${generalSyncTaskStatus}"/></strong>
              </c:when>
              <c:otherwise>
                <c:set var="status" value="${syncTaskHistoryInfo.indexingState}"/>
                <%--TODO: Remove hard codes. For "Incremental" and "Load Post Index Customizations Only" --%>
                <c:if test="${(generalSyncTaskStatus eq 'running_estimation' or generalSyncTaskStatus eq 'running_with_error' or
                              generalSyncTaskStatus eq 'running') and syncTaskHistoryInfo.indexingState eq 'finished'}">
                  <c:set var="status" value="indexing"/>
                </c:if>
                <%--
                  If state is "paused" but user didn't select "Wait for input after estimation"
                  then we shouldn't show "paused" state but just show "estimating".
                  TODO: Maybe it's a temporary solution.
                --%>
                <c:if test="${syncTaskHistoryInfo.indexingState eq 'paused' and !isWaitingForUserInput}">
                  <c:set var="status" value="estimating"/>
                </c:if>
                <strong><fmt:message key="synchronization_status_monitor.indexing_status.${status}"/></strong>
              </c:otherwise>
            </c:choose>
          </c:when>
          <c:otherwise>
            <strong><fmt:message key="synchronization_status_monitor.indexing_status.not_started"/></strong>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <%--Start:--%>
    <tr>
      <td class="label"><fmt:message key="synchronization_status_monitor.indexing_status.start"/></td>
      <td>
        <c:choose>
          <c:when test="${syncTaskId ne null}">
            <fmt:message var="timeFormat" key="timeFormat"/>
            <fmt:formatDate value="${syncTaskHistoryInfo.currentStartDate}" type="both" pattern="${timeFormat}"/>
          </c:when>
          <c:otherwise>
            <fmt:message key="synchronization_status_monitor.indexing_status.empty.value"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <%--End:--%>
    <tr>                 
      <td class="label"><fmt:message key="synchronization_status_monitor.indexing_status.end"/></td>
      <td colspan="3">
        <c:choose>
          <c:when test="${syncTaskHistoryInfo.indexingState eq 'finished'}">
            <fmt:message var="timeFormat" key="timeFormat"/>
            <fmt:formatDate value="${syncTaskHistoryInfo.currentEndDate}" type="both" pattern="${timeFormat}"/>
          </c:when>
          <c:otherwise>
            <fmt:message key="synchronization_status_monitor.indexing_status.empty.value"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <c:if test="${syncTaskHistoryInfo.indexingState eq 'estimating'}">
      <%--Sampled Content Items:--%>
      <tr>
        <td class="label"><fmt:message key="synchronization_status_monitor.top.table.header.content.sampled.items"/></td>
        <td>
          <fmt:message key="synchronization_status_monitor.top.table.header.content.count">
            <fmt:param value="${syncTaskHistoryInfo.currentCItemCount}"/>
          </fmt:message>
        </td>
      </tr>
      <%--Total Content Items:--%>
      <tr>
        <td class="label"><fmt:message key="synchronization_status_monitor.top.table.header.content.total.items"/></td>
        <td>
          <fmt:message key="synchronization_status_monitor.top.table.header.content.count">
            <fmt:param value="${syncTaskHistoryInfo.estimatedCItemCount}"/>
          </fmt:message>
        </td>
      </tr>
    </c:if>
    <c:if test="${isWaitingForUserInput}">
      <%--Content Items:--%>
      <tr>
        <td class="label"><fmt:message key="synchronization_status_monitor.top.table.header.content.items"/></td>
        <td><c:out value="${syncTaskHistoryInfo.estimatedCItemCount}"/></td>
      </tr>
    </c:if>
    <c:if test="${syncTaskHistoryInfo.indexingState eq 'deploying' or syncTaskHistoryInfo.indexingState eq 'finished'}">
      <tr>
        <td class="label"><fmt:message key="synchronization_status_monitor.top.table.header.content.items"/></td>
        <td><c:out value="${syncTaskHistoryInfo.currentCItemCount}"/></td>
      </tr>
    </c:if>
    <c:if test="${syncTaskHistoryInfo.indexingState eq 'indexing' or syncTaskHistoryInfo.indexingState eq 'preIndexing'}">
      <tr>
        <td class="label"><fmt:message key="synchronization_status_monitor.top.table.header.content.items"/></td>
        <td>
          <c:out value="${syncTaskHistoryInfo.currentCItemCount}"/>
          <c:if test="${syncTaskHistoryInfo.estimatedCItemCount > 0}">
            <fmt:message key="synchronization_status_monitor.top.table.header.of"/>
            <c:out value="${syncTaskHistoryInfo.estimatedCItemCount}"/>
            <c:if test="${null != syncTaskHistoryInfo.percentItemsCompleteAsString}">
              (<c:out value="${syncTaskHistoryInfo.percentItemsCompleteAsString}"/>%)
            </c:if>
          </c:if>
        </td>
      </tr>
    </c:if>
    <c:if test="${syncTaskHistoryInfo.indexingState eq 'estimating' or syncTaskHistoryInfo.indexingState eq 'deploying' or
                  syncTaskHistoryInfo.indexingState eq 'finished'}">
      <%--Index size:--%>
      <tr>
        <td class="label"><fmt:message key="synchronization_status_monitor.top.table.header.size"/></td>
        <td>
          <c:choose>
            <c:when test="${generalSyncTaskStatus eq 'Cancelled' or generalSyncTaskStatus eq 'Failed' or
                           syncTaskHistoryInfo.currentSize eq 0}">
              <fmt:message key="synchronization_index_summary.constant.na" />
            </c:when>
            <c:otherwise>
              <c:out value="${syncTaskHistoryInfo.currentSize}"/>
              <fmt:message key="synchronization_status_monitor.top.table.header.MB"/>
            </c:otherwise>
          </c:choose>
        </td>
      </tr>
    </c:if>
    <c:if test="${syncTaskHistoryInfo.indexingState eq 'indexing' or syncTaskHistoryInfo.indexingState eq 'preIndexing'}">
      <tr>
        <td class="label">
          <fmt:message key="synchronization_status_monitor.top.table.header.size"/>
        </td>
        <td>
          <fmt:formatNumber value="${syncTaskHistoryInfo.currentSize}" maxFractionDigits="2" minFractionDigits="2"/>
          <fmt:message key="synchronization_status_monitor.top.table.header.MB"/>
          <fmt:message key="synchronization_status_monitor.top.table.header.of"/>
          <fmt:formatNumber  value="${syncTaskHistoryInfo.estimatedSize}" maxFractionDigits="2" minFractionDigits="2"/>
          <fmt:message key="synchronization_status_monitor.top.table.header.MB"/>
        </td>
      </tr>
      <%--Time Remaining:--%>
      <tr>
        <td class="label"><fmt:message key="synchronization_status_monitor.top.table.header.sync.time_remaining"/></td>
        <td>
          <fmt:message key="synchronization_status_monitor.top.table.header.sync.time_remaining.time" >
            <fmt:param value="${syncTaskHistoryInfo.timeLeftInSeconds / 3600}"/>
            <fmt:param value="${(syncTaskHistoryInfo.timeLeftInSeconds % 3600)/60}"/>
            <fmt:param value="${(syncTaskHistoryInfo.timeLeftInSeconds % 3600)%60}"/>
          </fmt:message>
        </td>
      </tr>
    </c:if>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/sync_sm_indexing_status.jsp#1 $$Change: 651360 $--%>
