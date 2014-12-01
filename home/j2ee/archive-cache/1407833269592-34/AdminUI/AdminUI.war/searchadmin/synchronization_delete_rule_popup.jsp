<%--
 Synchronization rule delete JSP. Allows to delete an sych rule.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_delete_rule_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId" scope="page"/>
  <d:getvalueof param="syncTaskId" var="syncTaskId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/DeleteSynchronizationRuleFormHandler"/>
  <c:set var="handler" value="DeleteSynchronizationRuleFormHandler"/>
  <d:form action="synchronization_automatic.jsp" method="post">
    <c:url var="successURL" value="/searchadmin/synchronization_automatic.jsp">
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <c:url var="errorURL" value="/searchadmin/synchronization_delete_rule_popup.jsp">
      <c:param name="syncTaskId" value="${syncTaskId}"/>
      <c:param name="projectId" value="${projectId}"/>
    </c:url>
    <d:input type="hidden" name="projectId" bean="${handler}.projectId" value="${projectId}"/>
    <d:input type="hidden" name="syncRuleId" bean="${handler}.syncRuleId" value="${syncTaskId}"/>
    <d:input type="hidden" name="errorUrl" bean="${handler}.errorURL" value="${errorURL}"/>
    <d:input type="hidden" name="successUrl" bean="${handler}.successURL" value="${successURL}"/>
    <div class="content">

      <p>
        <fmt:message key="synchronization_rule_delete.question">
          <span id="alertListPopup"></span>
          <fmt:param>
            <strong>
              <fmt:message key="synchronization_rule_delete.rule"/>
              <admin-beans:getSyncTaskDefinition var="syncTaskDef" taskDefId="${syncTaskId}"/>
              <c:set var="subname" value="${syncTaskDef.baseSyncTask.subname}"/>
              <c:if test="${not empty subname}">
                <c:out value="${subname}"/>
              </c:if>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="synchronization_rule_delete.button.delete" var="deleteButton"/>
      <fmt:message key="synchronization_rule_delete.button.delete.tooltip" var="deleteButtonToolTip"/>
      <d:input type="submit" value="${deleteButton}" iclass="formsubmitter"
               bean="${handler}.deleteSyncRule" title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='synchronization_rule_delete.button.cancel'/>"
             onclick="closePopUp()" title="<fmt:message key='synchronization_rule_delete.button.cancel.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/synchronization_delete_rule_popup.jsp#2 $$Change: 651448 $--%>
