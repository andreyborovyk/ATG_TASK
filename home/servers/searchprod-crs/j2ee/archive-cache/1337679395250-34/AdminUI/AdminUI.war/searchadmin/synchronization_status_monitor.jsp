<%--
Synchronization status monitor page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_status_monitor.jsp#1 $$Change: 651360 $
@updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId"/>
  <d:getvalueof param="isQueuedTask" var="isQueuedTask"/>
  <c:if test="${empty isQueuedTask}"><c:set var="isQueuedTask" value="false"/></c:if>

  <d:importbean var="refreshHandler" bean="/atg/searchadmin/adminui/formhandlers/SynchronizationStatusMonitorFormHandler"/>
  <script type="text/javascript">
    loadingStatus.customResponseFunction = function(data) {
      return syncStatusMonitor.refreshHandleResponse(data, ${refreshHandler.timeout});
    };
    function customLoad() {
      syncStatusMonitor.setAutoRefresh(true);
      syncStatusMonitor.savingMessage = null;
      syncStatusMonitor.needSavingMessage = true;
    }
  </script>

  <c:if test="${empty syncTaskId}">
    <admin-beans:getRecentSyncTasks projectId="${projectId}" var="recentSyncTasks" recentSyncTaskId="syncTaskId"/>
  </c:if>
  <c:if test="${not empty syncTaskId}">
    <admin-beans:getRecentSyncTasks projectId="${projectId}" var="recentSyncTasks"/>
  </c:if>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <c:choose>
      <c:when test="${syncTaskId eq null}">
        <div id="paneContent">
          <h3><fmt:message key="synchronization_status_monitor.indexing_status.not.indexed.title"/></h3>
          <p><fmt:message key="synchronization_status_monitor.no_task"/></p>
        </div>
      </c:when>
      <c:otherwise>
        <d:form method="POST" action="${formActionUrl}" id="refreshForm">
          <div id="paneContent">
            <c:if test="${isQueuedTask}">
              <p>
                <fmt:message key="synchronization_status_monitor.top.to_queue.label"/>
                <c:url var="queueUrl" value="/searchadmin/global_queue.jsp"/>
                <a href="${queueUrl}" onclick="return loadRightPanel(this.href);">
                  <fmt:message key="synchronization_status_monitor.top.to_queue.link"/>
                </a>
              </p>
            </c:if>
            <c:if test="${not isQueuedTask}"><br/></c:if>
            <%--Current running sync tasks section--%>
            <c:if test="${fn:length(recentSyncTasks) > 1}">
              <fmt:message var="timeFormat" key="timeFormat"/>
              <table class="form monitorAlsoTable">
                <tr>
                  <td class="label">
                    <strong><fmt:message key="synchronization_status_monitor.indexing_status.also.title"/></strong>
                  </td>
                  <td>
                    <c:forEach items="${recentSyncTasks}" var="recentTask" varStatus="cursor">
                      <c:if test="${recentTask.id ne syncTaskId}">
                        <admin-beans:getSyncTaskHistoryInfo var="syncTaskHistoryInfo" taskId="${recentTask.id}" projectId="${projectId}" />
                        <fmt:formatDate value="${syncTaskHistoryInfo.currentStartDate}" type="both" pattern="${timeFormat}"/>
                        &nbsp;
                        <c:url value="/searchadmin/synchronization_status_monitor.jsp" var="monitorUrl">
                          <c:param name="projectId" value="${projectId}"/>
                          <c:param name="syncTaskId" value="${recentTask.id}"/>
                        </c:url>
                        <a href="${monitorUrl}" onclick="return loadRightPanel(this.href);">
                          <c:out value="${syncTaskHistoryInfo.syncTaskName}"/>
                        </a>
                        &nbsp;
                        <tags:join items="${syncTaskHistoryInfo.contentSets}" var="contentSet"
                            prefix=" (" postfix=")" delimiter="; ">
                            ${contentSet.name}
                        </tags:join>
                        <c:if test="${not cursor.last}">
                          <br/>
                        </c:if>
                      </c:if>
                    </c:forEach>
                  </td>
                </tr>
              </table>
            </c:if>
            <div id="refreshContent" dojoType="dojox.layout.ContentPane" executeScripts="true" cacheContent="false"
                 scriptSeparation="false" loadingMessage="<fmt:message key='tree.dojo.loading'/>">
            </div> 

          </div>

          <c:url var="contentViewURL" value="/searchadmin/sync_status_monitor_content.jsp">
            <c:param name="syncTaskId" value="${syncTaskId}"/>
            <c:param name="projectId" value="${projectId}"/>
          </c:url>
          <c:url var="successURL" value="/searchadmin/synchronization_status_monitor.jsp">
            <c:param name="projectId" value="${projectId}"/>
          </c:url>

          <div id="paneFooter">
            <d:input type="hidden" id="contentViewURL" name="contentViewURL"
                     bean="SynchronizationStatusMonitorFormHandler.contentViewURL" value="${contentViewURL}" />
            <d:input bean="SynchronizationStatusMonitorFormHandler.refreshView" type="submit" iclass="formsubmitter"
                     style="display:none;" name="refreshView" id="refreshView" value=""></d:input>
            <d:input type="hidden" id="successURL" name="successURL" bean="SynchronizationStatusMonitorFormHandler.successURL" value="${successURL}"/>
            <d:input type="hidden" name="errorURL" bean="SynchronizationStatusMonitorFormHandler.errorURL" value="${errorURL}"/>
            <d:input type="hidden" bean="SynchronizationStatusMonitorFormHandler.syncTaskId" value="${syncTaskId}"/>
            <d:input type="hidden" bean="SynchronizationStatusMonitorFormHandler.showDetails" id="showDetails"/>
            <%-- Refresh button --%>
            <fmt:message var="refresh" key="refresh_footer.button.refresh"/>
            <fmt:message var="refresh_tooltip" key="refresh_footer.button.refresh.tooltip"/>
            <input type="button" name="refresh" id="refreshIndexingButton" onclick="return syncStatusMonitor.refresh();"
                     value="${refresh}" title="${refresh_tooltip}" style="display:none"/>
            <%-- Enable/disable buttons --%>
            <input type="button" id="enableRefreshButton" onclick="return syncStatusMonitor.setAutoRefresh(true);"
                   value="<fmt:message key='refresh_footer.button.enable_refresh'/>"  style="display:none"
                   title="<fmt:message key='refresh_footer.button.enable_refresh.tooltip'/>"/>
            <input type="button" id="disableRefreshButton" onclick="return syncStatusMonitor.setAutoRefresh(false);"
                   value="<fmt:message key='refresh_footer.button.disable_refresh'/>"  style="display:none"
                   title="<fmt:message key='refresh_footer.button.disable_refresh.tooltip'/>"/>
            <%-- If general sync task has deploying state then continue button should be disabled--%>
            <fmt:message var="continue" key="refresh_footer.button.continue"/>
            <fmt:message var="continue_tooltip" key="refresh_footer.button.continue.tooltip"/>
            <d:input bean="SynchronizationStatusMonitorFormHandler.continueProcess" type="submit" iclass="formsubmitter"
                     id="continueIndexingButton" onclick="return syncStatusMonitor.beforeContinue();"
                     value="${continue}" title="${continue_tooltip}" style="display:none"/>
            <%-- Cancel button --%>
            <fmt:message var="cancel" key="refresh_footer.button.cancel"/>
            <fmt:message var="cancel_tooltip" key="refresh_footer.button.cancel.tooltip"/>
            <c:url var="cancelPopupUrl" value="/searchadmin/synchronization_cancel_popup.jsp">
              <c:param name="syncTaskId" value="${syncTaskId}" />
              <c:param name="projectId" value="${projectId}" />
            </c:url>
            <input type="button" value="<fmt:message key='refresh_footer.button.cancel'/>" id="cancelIndexingButton"
                     title="<fmt:message key='refresh_footer.button.cancel.tooltip'/>"
                     onclick="return showPopUp('${cancelPopupUrl}');" />
          </div>
          <d:input bean="SynchronizationStatusMonitorFormHandler.recalculate" type="submit" iclass="formsubmitter"
                   style="display:none;" name="recalculate" id="recalculate" value="recalculate"></d:input>
          <admin-validator:validate beanName="SynchronizationStatusMonitorFormHandler"/>
        </d:form>
      </c:otherwise>
    </c:choose>
  </div>
</d:page>

<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_status_monitor.jsp#1 $$Change: 651360 $--%>
