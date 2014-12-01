<%--
 Customization item removing popup.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_customization_remove_popup.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <c:set var="customItemName"><tags:i18GetParam paramName="customItemName"/></c:set>
  <d:getvalueof param="projectId" var="projectId" />
  <d:getvalueof param="customItemId" var="customItemId" />
  <d:getvalueof param="type" var="type" />

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/RemoveCustomItemFormHandler"/>
  <c:url var="errorURL" value="project_index_customization_remove_popup.jsp">
    <c:param name="projectId" value="${projectId}"/>
    <c:param name="customItemName" value="${customItemName}"/>
    <c:param name="customItemId" value="${customItemId}"/>
    <c:param name="type" value="${type}"/>
  </c:url>
  <c:url var="backURL" value="/searchadmin/project_manage_index.jsp">
    <c:param name="projectId" value="${projectId}"/>
  </c:url>

  <d:form action="${errorURL}" method="POST">
    <d:input type="hidden" bean="RemoveCustomItemFormHandler.projectId" value="${projectId}" name="projectId"/>
    <d:input type="hidden" bean="RemoveCustomItemFormHandler.customItemName" value="${customItemName}" name="customItemName"/>
    <d:input type="hidden" bean="RemoveCustomItemFormHandler.customItemId" value="${customItemId}" name="customItemId"/>
    <d:input type="hidden" bean="RemoveCustomItemFormHandler.type" value="${type}" name="type"/>
    <d:input type="hidden" bean="RemoveCustomItemFormHandler.errorURL" value="${errorURL}" name="errorUrl"/>

    <div class="content">
      <span id="alertListPopup"></span>
      <p>
        <fmt:message key="project_index_customizations_remove_popup.question">
          <fmt:param>
            <strong>${customItemName}</strong>
          </fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="project_index_customizations_remove_popup.button.remove" var="removeButton"/>
      <fmt:message key="project_index_customizations_remove_popup.button.remove.title" var="removeButtonToolTip"/>
      <d:input type="submit" value="${removeButton}" iclass="formsubmitter"
                   bean="RemoveCustomItemFormHandler.remove"
                   title="${removeButtonToolTip}"/>
      <input type="button" value="<fmt:message key='project_index_customizations_remove_popup.button.cancel'/>"
             onclick="closePopUp()" title="<fmt:message key='project_index_customizations_remove_popup.button.cancel.title'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_index_customization_remove_popup.jsp#1 $$Change: 651360 $--%>
