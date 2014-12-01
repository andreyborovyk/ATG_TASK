<%--
JSP, that allows to delete a project.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_delete_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- id of project, used to be deleted--%>
  <d:getvalueof param="projectId" var="projectId" />
  
  <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>
  <c:url var="errorURL" value="/searchadmin/project_delete_popup.jsp">
    <c:param name="projectId" value="${projectId}"/>
  </c:url>
  <%-- form, used to delete project --%>
  <d:form action="browse_all_projects.jsp" method="POST">
  
    <d:input type="hidden" bean="/atg/searchadmin/adminui/formhandlers/DeleteProjectFormHandler.errorURL"
             value="${errorURL}"/>
    
    <div class="content">
      <span id="alertListPopup"></span>
      <d:input type="hidden"
               bean="/atg/searchadmin/adminui/formhandlers/DeleteProjectFormHandler.id" value="${projectId}"/>
      <%-- Display message if user is trying to delete project while indexing job is running --%>
      <admin-beans:getSynchronizationProjectStatus varCurrent="current" projectId="${projectId}"/>
      <c:if test="${!current.emptyBean}">
        <p>
          <span class="error"><fmt:message key="delete_project.warning.indexing.active"/></span>
        </p>
      </c:if>
      
      <p>
        <fmt:message key="delete_project.question2">
          <fmt:param>
            <strong>
               <c:out value="${project.name}"/>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
    
      <table class="form" cellpadding="0" cellspacing="0">
        <tr>
          <td colspan="2">
            <fmt:message key="delete_project.question.search.env"/>
          </td>
        </tr>
        <tr>
          <td>
            <%-- Radiobutton, used to determine to delete unused search environments or not --%>
            <d:input bean="/atg/searchadmin/adminui/formhandlers/DeleteProjectFormHandler.deleteSearchEnvironment"
                     value="true"
                     name="deleteSearch" id="deleteSearch_true"
                     type="radio"
                     checked="true"/>
          </td>
          <td width="100%">
            <label for="deleteSearch_true"><fmt:message key="delete_project.search.env.delete"/></label>
          </td>
        </tr>
        <tr>
          <td>
            <%-- Radiobutton, used to determine to delete unused search environments or not --%>
            <d:input bean="/atg/searchadmin/adminui/formhandlers/DeleteProjectFormHandler.deleteSearchEnvironment"
                     value="false"
                     name="deleteSearch" id="deleteSearch_false"
                     type="radio"
                     checked="false"/>
          </td>
          <td width="100%">
            <label for="deleteSearch_false"><fmt:message key="delete_project.search.env.do.not.delete"/></label>
          </td>
        </tr>
      </table>
    </div>
    
    <div class="footer" id="popupFooter">
      <fmt:message key="delete_project.delete.button" var="delete_button_value"/>
      <fmt:message key="delete_project.delete.button.tooltip" var="delete_button_tooltip"/>
      <d:input bean="/atg/searchadmin/adminui/formhandlers/DeleteProjectFormHandler.deleteProject" type="submit"
               value="${delete_button_value}" iclass="formsubmitter"
               title="${delete_button_tooltip}"/>
      <input type="button" value="<fmt:message key='delele_project.cancel.button'/>" onclick="closePopUp()"
             title="<fmt:message key='delele_project.cancel.button.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/project_delete_popup.jsp#2 $$Change: 651448 $--%>
