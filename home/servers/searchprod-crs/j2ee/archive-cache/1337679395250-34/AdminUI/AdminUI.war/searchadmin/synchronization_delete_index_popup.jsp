<%--
 Synchronization index history entry delete JSP. Allows to delete an index history entry.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_delete_index_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId" scope="page"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/DeleteSynchronizationIndexFormHandler"/>
  <c:set var="handler" value="DeleteSynchronizationIndexFormHandler"/>
  <d:form action="synchronization_history.jsp" method="post">
    <c:url var="successURL" value="/searchadmin/synchronization_history.jsp">
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <c:url var="errorURL" value="/searchadmin/synchronization_delete_index_popup.jsp">
      <c:param name="projectId" value="${projectId}"/>
      <c:param name="syncTaskId" value="${syncTaskId}"/>
    </c:url>
    <d:input type="hidden" bean="${handler}.projectId" value="${projectId}"/>
    <d:input type="hidden" bean="${handler}.syncHistoryEntryId" value="${syncTaskId}"/>
    <d:input type="hidden" bean="${handler}.successURL" value="${successURL}"/>
    <d:input type="hidden" bean="${handler}.errorURL" value="${errorURL}"/>
    <div class="content">
      <span id="alertListPopup"></span>
      <admin-beans:getSyncTaskGeneralStatus var="generalSyncTaskStatus" syncTaskId="${syncTaskId}"/>
      <common:syncTaskFindByPrimaryKey syncTaskId="${syncTaskId}" var="syncTask"/>
      <c:choose>
        <c:when test="${(generalSyncTaskStatus eq 'Succeeded' or generalSyncTaskStatus eq 'finished_with_error') and syncTask.index ne null}">
          <p>
            <fmt:message key="synchronization_delete_index.question"/>
          </p>
          <table>
            <tr>
              <td>
                <d:input type="radio" id="deleteIndexOnly" name="deleteHistoryEntry"
                    bean="${handler}.deleteHistoryEntry" value="false"/>
                <label for="deleteIndexOnly"><fmt:message key="synchronization_delete_index.delete.index"/></label>
              </td>
            </tr>
            <tr>
              <td>
                <d:input type="radio" id="deleteHistoryEntry" name="deleteHistoryEntry"
                         bean="${handler}.deleteHistoryEntry" value="true"/>
                <label for="deleteHistoryEntry"><fmt:message key="synchronization_delete_index.delete.index.historyentry"/></label>
              </td>
            </tr>
          </table>
        </c:when>
        <c:otherwise>
          <p>
            <fmt:message key="synchronization_delete_index.question.history_entry"/>
          </p>
          <d:input type="hidden" name="deleteHistoryEntry" bean="${handler}.deleteHistoryEntry" value="true"/>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="synchronization_delete_index.button.delete" var="deleteButton"/>
      <fmt:message key="synchronization_delete_index.button.delete.tooltip" var="deleteButtonToolTip"/>
      <d:input type="submit" value="${deleteButton}" iclass="formsubmitter"
               bean="${handler}.deleteIndex" title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='synchronization_delete_index.button.cancel'/>"
             onclick="closePopUp()"
             title="<fmt:message key='synchronization_delete_index.button.cancel.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_delete_index_popup.jsp#2 $$Change: 651448 $--%>
