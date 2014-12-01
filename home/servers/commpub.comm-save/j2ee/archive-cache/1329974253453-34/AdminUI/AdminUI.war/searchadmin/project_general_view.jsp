<%--
  JSP, showing general information about search project.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_general_view.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Search project id --%>
  <d:getvalueof param="projectId" var="projectId"/>
  <%-- Custom tag, used to retrieve search project by id --%>
  <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>
  <table class="form" cellspacing="0" cellpadding="0">
    <tbody>
      <tr>
        <td class="label">
          <fmt:message key="project_general.project_name_field"/>
        </td>
        <td>
          <c:out value="${project.name}"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <fmt:message key="project_general.description_field"/>
        </td>
        <td>
          <c:out value="${project.description}"/>
        </td>
      </tr>
      <tr>
        <td class="label">
          <fmt:message key="project_general.target_type"/>
        </td>
        <td>
          <c:out value="${project.targetType}"/>
        </td>
      </tr>
      <c:if test="${project.noContent}">
        <td/>
        <td>
          <fmt:message key="project_general.no_content"/>
        </td>
      </c:if>
    </tbody>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_general_view.jsp#2 $$Change: 651448 $--%>
