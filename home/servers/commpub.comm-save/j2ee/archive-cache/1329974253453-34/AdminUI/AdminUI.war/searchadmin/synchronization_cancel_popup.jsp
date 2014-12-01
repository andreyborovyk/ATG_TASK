<%--
 Synchronization cancel popup dialog window.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_cancel_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId" scope="page"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/SynchronizationCancelFormHandler"/>
  <c:set var="handler" value="SynchronizationCancelFormHandler"/>
  <d:form action="synchronization_status_monitor.jsp" method="post">
    <c:url var="successURL" value="/searchadmin/synchronization_status_monitor.jsp">
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <c:url var="errorURL" value="/searchadmin/synchronization_cancel_popup.jsp">
      <c:param name="syncTaskId" value="${syncTaskId}"/>
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <d:input type="hidden" name="syncTaskId" bean="${handler}.syncTaskId" value="${syncTaskId}"/>
    <d:input type="hidden" name="successUrl" bean="${handler}.successURL" value="${successURL}"/>
    <d:input type="hidden" name="errorUrl" bean="${handler}.errorURL" value="${errorURL}"/>
    <div class="content">
      <p>
        <fmt:message key="synchronization_cancel.question"/>
      </p>
    </div>
    <div class="footer" id="popupFooter">
      <fmt:message key="synchronization_cancel.button.yes" var="deleteButton"/>
      <fmt:message key="synchronization_cancel.button.yes.tooltip" var="deleteButtonToolTip"/>
      <d:input type="submit" value="${deleteButton}" iclass="formsubmitter"
               bean="${handler}.cancelProcess" title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='synchronization_cancel.button.no'/>"
             onclick="closePopUp()" title="<fmt:message key='synchronization_cancel.button.no.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_cancel_popup.jsp#2 $$Change: 651448 $--%>
