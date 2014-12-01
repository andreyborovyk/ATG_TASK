<%--
 Deletes several indexed from history page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_delete_selected_index_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId" scope="page"/>
  <d:getvalueof param="syncTaskIds" var="syncTaskIds" scope="page"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/DeleteSynchronizationIndexFormHandler"/>
  <c:set var="handler" value="DeleteSynchronizationIndexFormHandler"/>
  <d:form action="synchronization_history.jsp" method="post">
    <c:url var="successURL" value="/searchadmin/synchronization_history.jsp">
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <c:url var="errorURL" value="/searchadmin/synchronization_delete_selected_index_popup.jsp">
      <c:param name="projectId" value="${projectId}"/>
      <c:param name="syncTaskIds" value="${syncTaskIds}"/>
    </c:url>
    <d:input type="hidden" bean="${handler}.projectId" value="${projectId}"/>
    <d:input type="hidden" bean="${handler}.syncHistoryEntryIds" value="${syncTaskIds}"/>
    <d:input type="hidden" bean="${handler}.successURL" value="${successURL}"/>
    <d:input type="hidden" bean="${handler}.errorURL" value="${errorURL}"/>
    <div class="content">
      <span id="alertListPopup"></span>
      <p>
        <fmt:message key="synchronization_delete_selected_index.question"/>
      </p>
      <table>
        <tr>
          <td>
            <d:input type="radio" id="deleteIndexOnly" name="deleteHistoryEntry"
                bean="${handler}.deleteHistoryEntry" value="false" />
            <label for="deleteIndexOnly"><fmt:message key="synchronization_delete_selected_index.delete.index"/></label>
          </td>
        </tr>
        <tr>
          <td>
            <d:input type="radio" id="deleteHistoryEntry" name="deleteHistoryEntry"
                     bean="${handler}.deleteHistoryEntry" value="true"/>
            <label for="deleteHistoryEntry"><fmt:message key="synchronization_delete_selected_index.delete.index.historyentry"/></label>
          </td>
        </tr>
      </table>
    </div>
    <div class="footer" id="popupFooter">
      <fmt:message key="synchronization_delete_selected_index.button.delete" var="deleteButton"/>
      <fmt:message key="synchronization_delete_selected_index.button.delete.tooltip" var="deleteButtonToolTip"/>
      <d:input type="submit" value="${deleteButton}" iclass="formsubmitter"
               bean="${handler}.deleteIndexes" title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='synchronization_delete_selected_index.button.cancel'/>"
             onclick="closePopUp()"
             title="<fmt:message key='synchronization_delete_selected_index.button.cancel.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_delete_selected_index_popup.jsp#2 $$Change: 651448 $--%>
