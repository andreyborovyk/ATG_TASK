<%--
JSP, that allows to copy a project.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_copy_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId" scope="page"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/ManageProjectFormHandler" var="handler" />
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="projectId" value="${projectId}"/>
    <admin-ui:param name="action" value="copy"/>
  </admin-ui:initializeFormHandler>

<c:choose>
<c:when test="${not empty handler.allTargetTypes}">

  <c:url value="/searchadmin/browse_all_projects.jsp" var="successUrl" />
  <c:url value="/searchadmin/project_copy_popup.jsp" var="errorUrl">
    <c:param name="projectId" value="${projectId}"/>
  </c:url>

  <%-- form, allowing to copy a project --%>
  <d:form action="${errorUrl}" method="POST">
    <d:input type="hidden" bean="ManageProjectFormHandler.projectId" name="projectId" />
    <d:input type="hidden" bean="ManageProjectFormHandler.needInitialization" value="false" />
    <d:input type="hidden" bean="ManageProjectFormHandler.action" />
    <d:input type="hidden" bean="ManageProjectFormHandler.popup" value="true" />
    <d:input type="hidden" bean="ManageProjectFormHandler.successURL" value="${successUrl}"/>
    <d:input type="hidden" bean="ManageProjectFormHandler.errorURL" value="${errorUrl}"/>

    <div class="content">
      <span id="alertListPopup"></span>

      <d:include src="project_general_edit.jsp" />

      <p><fmt:message key="copy_project.copy.note"/></p>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="copy_project.copy.button" var="copy_button_value"/>
      <fmt:message key="copy_project.copy.button.tooltip" var="copy_button_tooltip"/>
      <d:input type="submit" bean="/atg/searchadmin/adminui/formhandlers/ManageProjectFormHandler.copyProject"
               name="copyProject" iclass="formsubmitter"
               value="${copy_button_value}"
               title="${copy_button_tooltip}"
               onclick="return checkForm();"/>
      <input type="button" value="<fmt:message key='copy_project.cancel.button'/>"
             onclick="closePopUp()" name="cancel"
             title="<fmt:message key='copy_project.cancel.button.tooltip'/>"/>
    </div>
  </d:form>

  <admin-validator:validate beanName="ManageProjectFormHandler"/>

</c:when>
<c:otherwise>

    <div class="content">
      <p><fmt:message key="copy_project.no_target_type.note"/></p>
    </div>

    <div class="footer" id="popupFooter">
      <input type="button" value="<fmt:message key='copy_project.copy.button'/>"
             onclick="closePopUp()" name="cancel"
             title="<fmt:message key='copy_project.copy.button.tooltip'/>"/>
    </div>

</c:otherwise>
</c:choose>

</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_copy_popup.jsp#2 $$Change: 651448 $--%>
