<%--
JSP, used to create new project.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/new_project.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean var="handler" bean="/atg/searchadmin/adminui/formhandlers/ManageProjectFormHandler" />

  <%-- Head section --%>
  <d:include page="project_head.jsp">
    <d:param name="status" value="1"/>
  </d:include>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
      executeScripts="true" cacheContent="false" scriptSeparation="false">
    <admin-ui:initializeFormHandler handler="${handler}">
      <admin-ui:param name="action" value="new"/>
    </admin-ui:initializeFormHandler>
    <c:url value="/searchadmin/project.jsp" var="projectUrl"/>
    <c:url value="/searchadmin/new_project.jsp" var="newProjectUrl"/>
    <d:form action="${projectUrl}" method="POST">
      <div id="paneContent">
        <%-- General section --%>
        <h3 class="overview">
          <d:include src="project_step.jsp">
            <d:param name="step" value="1"/>
            <d:param name="section" value="1"/>
          </d:include>
          <fmt:message key="project_general.base_name"/>
        </h3>
        <d:include src="project_general_edit.jsp"/>
        <%-- Specify Initial Content Set section --%>
        <h3><fmt:message key="project_general.initial_content_set.title" /></h3>
        <d:include src="content_set_edit.jsp" />
        <%-- Create Search Project button --%>
        <table class="form" cellspacing="0" cellpadding="0">
          <tbody>
            <tr>
              <td class="label">&nbsp;</td>
              <td>
                <d:input type="hidden" bean="ManageProjectFormHandler.successURL" name="successURL" value="${projectUrl}" />
                <d:input type="hidden" bean="ManageProjectFormHandler.errorURL" name="errorURL" value="${newProjectUrl}" />
                <d:input type="hidden" bean="ManageProjectFormHandler.needInitialization" value="false" />
                <d:input type="hidden" bean="ManageProjectFormHandler.action" />

                <fmt:message key="project_footer.buttons.create" var="createButtonTitle" />
                <fmt:message key="project_footer.buttons.create.tooltip" var="createButtonToolTip" />
                <d:input type="submit" bean="ManageProjectFormHandler.create" name="createButton" iclass="formsubmitter"
                         value="${createButtonTitle}" title="${createButtonToolTip}" onclick="return checkForm();" />
              </td>
            </tr>
          </tbody>
        </table>

        <%-- Index section --%>
        <h3 class="overview">
          <d:include src="project_step.jsp">
            <d:param name="step" value="1"/>
            <d:param name="section" value="2"/>
          </d:include>
          <fmt:message key="project_index.base_name"/>
        </h3>
        <p>
          <fmt:message key="project_index.title"/>
        </p>
        <p>
          <%-- Add content set button. Disabled. --%>
          <input type="button" disabled="disabled" value="<fmt:message key='project_index.buttons.new_content'/>"
                 title="<fmt:message key='project_index.buttons.new_content.tooltip'/>"/>
        </p>
        <p>
          <fmt:message key="project_index.empty"/>
        </p>

        <%-- Synchronization section --%>
        <h3 class="overview">
          <d:include src="project_step.jsp">
            <d:param name="step" value="1"/>
            <d:param name="section" value="3"/>
          </d:include>
          <fmt:message key="project_synch.base_name"/>
        </h3>
        <p>
          <fmt:message key="project_synch.title"/>
        </p>
        <p>
          <input type="button" disabled="disabled" value="<fmt:message key='project_synch.buttons.perform'/>"
                 title="<fmt:message key='project_synch.buttons.perform.tooltip'/>" />
        </p>
        <p>
          <fmt:message key="project_synch.empty"/>
        </p>

        <%-- Environments section --%>
        <h3 class="overview">
          <fmt:message key="project_senv.base_name"/>
        </h3>
        <p>
          <fmt:message key="project_senv.title"/>
        </p>
        <p>
          <fmt:message key="project_senv.empty"/>
        </p>
        <p>&nbsp;</p>
      </div>

      <admin-validator:validate beanName="ManageProjectFormHandler"/>
    </d:form>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/new_project.jsp#2 $$Change: 651448 $--%>
