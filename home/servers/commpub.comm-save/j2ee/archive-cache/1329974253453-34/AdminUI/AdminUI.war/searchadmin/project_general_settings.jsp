<%--
JSP, used to show & modify general information about project: it's name, description and secure organizations.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_general_settings.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- projectId parameter used to determine, which project information must be shown. --%>
  <d:getvalueof param="projectId" var="projectId" />

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/ManageProjectFormHandler" var="handler" />
  <admin-ui:initializeFormHandler handler="${handler}">
    <admin-ui:param name="projectId" value="${projectId}"/>
    <admin-ui:param name="action" value="edit"/>
  </admin-ui:initializeFormHandler>

  <c:url value="/searchadmin/project.jsp" var="viewProjectURL">
    <c:param name="projectId" value="${projectId}"/>
  </c:url>
  <c:url value="/searchadmin/project_general_settings.jsp" var="errorURL">
    <c:param name="projectId" value="${projectId}"/>
  </c:url>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${viewProjectURL}" method="POST">
      <div id="paneContent">
        <br/>
        <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>
        <d:include page="project_general_edit.jsp"/>
      </div>

      <%-- Footer --%>
      <div id="paneFooter">
        <d:input type="hidden" bean="ManageProjectFormHandler.projectId" name="projectId" />
        <d:input type="hidden" bean="ManageProjectFormHandler.successURL" name="successURL" value="${viewProjectURL}" />
        <d:input type="hidden" bean="ManageProjectFormHandler.errorURL" name="errorURL" value="${errorURL}" />
        <d:input type="hidden" bean="ManageProjectFormHandler.needInitialization" name="needInitialization" value="false" />
        <d:input type="hidden" bean="ManageProjectFormHandler.projectId" name="projectId" />

        <fmt:message key="project_footer.buttons.update" var="updateButtonTitle"/>
        <fmt:message key="project_footer.buttons.update.tooltip" var="updateButtonToolTip"/>
        <d:input type="submit" bean="ManageProjectFormHandler.update" name="updateButton" iclass="formsubmitter"
                 value="${updateButtonTitle}" title="${updateButtonToolTip}" onclick="return checkForm();" />

        <fmt:message key="project_footer.buttons.cancel" var="cancelButtonTitle"/>
        <fmt:message key="project_footer.buttons.cancel.tooltip" var="cancelButtonToolTip"/>
        <d:input type="submit" bean="ManageProjectFormHandler.cancel" name="cancelButton"  iclass="formsubmitter"
                 value="${cancelButtonTitle}" title="${cancelButtonToolTip}"/>
      </div>
    </d:form>
  </div>
  <admin-validator:validate beanName="ManageProjectFormHandler"/>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_general_settings.jsp#2 $$Change: 651448 $--%>
