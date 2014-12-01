<%--
JSP, that allows to delete a queued task.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/global_queue_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="queuedTaskId" var="queuedTaskId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/IndexingQueueFormHandler" var="handler"/>
  
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="queuedTaskId" value="${queuedTaskId}"/>
  </admin-ui:initializeFormHandler>
  
  <c:if test="${handler.existingTask}">
    <common:queuedTaskFindByPrimaryKey queuedTaskId="${queuedTaskId}" var="queuedTask"/>
  
    <c:url var="errorURL" value="/searchadmin/global_queue_delete_popup.jsp">
      <c:param name="queuedTaskId" value="${queuedTaskId}"/>
    </c:url>
    
    <d:form action="global_queue_delete_popup.jsp" method="POST">
      <d:input type="hidden" bean="/atg/searchadmin/adminui/formhandlers/IndexingQueueFormHandler.errorURL"
               value="${errorURL}"/>
      <div class="content">
        <span id="alertListPopup"></span>
        <d:input type="hidden"
                 bean="/atg/searchadmin/adminui/formhandlers/IndexingQueueFormHandler.id" value="${queuedTaskId}"/>
        <p>
          <common:baseSyncTaskFindByPrimaryKey baseSyncTaskId="${queuedTask.baseSyncTaskId}" var="baseSyncTask"/>
          <fmt:message key="global_queue.delete.confirm.message">
            <fmt:param>
              <strong>
                 <c:out value="${baseSyncTask.subname}"/>
              </strong>
            </fmt:param>
            <fmt:param>
              <strong>
                 <fmt:message var="timeFormat" key="timeFormat"/>
                 <fmt:formatDate value="${queuedTask.qtime}" type="both" pattern="${timeFormat}"/>
              </strong>
            </fmt:param>
          </fmt:message>
        </p>
      </div>
      <div class="footer" id="popupFooter">
        <fmt:message key="global_queue.delete.button" var="delete_button_value"/>
        <fmt:message key="global_queue.delete.button.tooltip" var="delete_button_tooltip"/>
        <d:input bean="/atg/searchadmin/adminui/formhandlers/IndexingQueueFormHandler.deleteQueuedTask" type="submit"
                 value="${delete_button_value}" iclass="formsubmitter"
                 title="${delete_button_tooltip}"/>
        <input type="button" value="<fmt:message key='global_queue.cancel.button'/>" onclick="closePopUp()"
               title="<fmt:message key='global_queue.cancel.button.tooltip'/>"/>
      </div>
    </d:form>
  </c:if>
  <c:if test="${not handler.existingTask}">
    <script type="text/javascript">
      closePopUp();
      alerting.storedMessagesData = <admin-dojo:jsonObject><admin-dojo:jsonValue name="alerting" alreadyJson="true"><tags:ajax_messages/></admin-dojo:jsonValue></admin-dojo:jsonObject>;
      loadingStatus.setRedirectUrl("${pageContext.request.contextPath}/searchadmin/global_queue.jsp?");
    </script>
  </c:if>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/global_queue_delete_popup.jsp#2 $$Change: 651448 $--%>
