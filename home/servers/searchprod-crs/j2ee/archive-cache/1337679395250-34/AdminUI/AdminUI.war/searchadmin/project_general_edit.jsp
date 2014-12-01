<%--
General section for search project page, used to show form inputs for creating or editing search project.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_general_edit.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean var="formHandler" bean="/atg/searchadmin/adminui/formhandlers/ManageProjectFormHandler" />
  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label">
          <span id="projectNameAlert"><span class="required"><fmt:message key="project_general.required_field" /></span></span>
          <fmt:message key="project_general.project_name_field" />
        </td>
        <td>
          <d:input type="text" bean="ManageProjectFormHandler.projectName"
                   iclass="textField" name="projectName" id="projectName" />
        </td>
      </tr>
      <tr>
        <td class="label">
          <span id="projectDescriptionAlert"></span>
          <fmt:message key="project_general.description_field" />
        </td>
        <td colspan="2">
          <d:input type="text" bean="ManageProjectFormHandler.projectDescription"
                   iclass="textField" name="projectDescription" id="description" />
        </td>
      </tr>
      <tr>
        <td class="label">
          <span id="targetTypeAlert"><span class="required"><fmt:message key="project_general.required_field" /></span>
          </span>
          <fmt:message key="project_general.target_type" />
        </td>
        <td>
          <tags:select bean="/atg/searchadmin/adminui/formhandlers/ManageProjectFormHandler.targetType"
              items="${formHandler.allTargetTypes}" emptyMessageKey="project_general.target_type.empty" />
          <c:set var="parentProject" value="${formHandler.parentProject}" />
          <c:if test="${not empty parentProject}">
            <fmt:message key="project_general.target_type.linked">
              <fmt:param><c:out value="${parentProject.name}" /></fmt:param>
            </fmt:message>
            <p>
              <fmt:message key="project_general.target_type.note">
                <fmt:param>
                  <a href="searchadmin/global_settings.jsp" onclick="return loadRightPanel(this.href)">
                    <fmt:message key="project_general.target_type.note.link" /></a>
                </fmt:param>
              </fmt:message>
            </p>
          </c:if>
        </td>
      </tr>
      <tr>
        <td></td>
        <td>
          <d:input type="checkbox" bean="ManageProjectFormHandler.noContent" id="noContent" name="noContent" disabled="${not empty parentProject or not formHandler.emptyContent}"/>
          <fmt:message key="project_general.no_content"/>
        </td>
      </tr>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_general_edit.jsp#2 $$Change: 651448 $--%>
