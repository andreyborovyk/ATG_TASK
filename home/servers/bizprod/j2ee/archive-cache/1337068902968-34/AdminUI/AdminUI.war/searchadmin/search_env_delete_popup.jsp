<%--
 Search environment delete JSP. Allows to delete an environment.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_delete_popup.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId" />
  <%-- Search environment id --%>
  <d:getvalueof param="environmentId" var="environmentId"/>

  <d:importbean bean="/atg/searchadmin/adminui/formhandlers/DeleteSearchEnvironmentFormHandler"/>
  <c:set var="formHandler" value="DeleteSearchEnvironmentFormHandler"/>
  <c:choose>
    <c:when test="${!empty projectId}">
      <c:url var="targetURL" value="/searchadmin/project_search_envs.jsp">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>
    </c:when>
    <c:otherwise>
      <c:url var="targetURL" value="/searchadmin/global_objects_search_envs.jsp"/>
    </c:otherwise>
  </c:choose>
  <c:choose>
    <c:when test="${!empty projectId}">
      <c:url var="errorURL" value="/searchadmin/search_env_delete_popup.jsp">
        <c:param name="projectId" value="${projectId}"/>
      </c:url>
    </c:when>
    <c:otherwise>
      <c:url var="errorURL" value="/searchadmin/search_env_delete_popup.jsp"/>
    </c:otherwise>
  </c:choose>
  
  <d:form action="${errorURL}" method="POST">
    <d:input type="hidden" bean="${formHandler}.environmentId" value="${environmentId}" name="environmentId"/>
    <d:input type="hidden" bean="${formHandler}.successURL" value="${targetURL}" name="successUrl"/>
    <d:input type="hidden" bean="${formHandler}.errorURL" value="${errorURL}" name="errorUrl"/>

    <div class="content">

      <span id="alertListPopup"></span>

      <p>
        <fmt:message key="search_env_delete.question">
          <fmt:param>
            <strong>
              <fmt:message key="search_env_delete.environment">
                <fmt:param>
                  <admin-beans:getSearchEnvironment environmentId="${environmentId}" var="searchEnv"/>
                  <c:out value="${searchEnv.envName}"/>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>

      <c:choose>
        <c:when test="${!empty param.projectId}">
          <table class="form" cellpadding="0" cellspacing="0">
            <tr>
              <td>
                <d:input type="radio" name="deleteEnvironment" bean="${formHandler}.action" value="delete" id="deleteEnvironment_delete" checked="true"/>
                <label for="deleteEnvironment_delete">
                  <fmt:message key="search_env_delete.option.delete"/>
                </label>
              </td>
            </tr>
            <tr>
              <td>
                <d:input type="radio" name="deleteEnvironment" bean="${formHandler}.action" value="detach" id="deleteEnvironment_detach"/>
                <label for="deleteEnvironment_detach">
                  <fmt:message key="search_env_delete.option.detach"/>
                </label>
              </td>
            </tr>
          </table>
        </c:when>
        <c:otherwise>
          <d:input type="hidden" bean="${formHandler}.action" value="delete" name="deleteAction"/>
        </c:otherwise>
      </c:choose>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="search_env_delete.button.delete" var="deleteButton"/>
      <fmt:message key="search_env_delete.button.delete.tooltip" var="deleteButtonToolTip"/>
      <d:input type="submit" value="${deleteButton}" iclass="formsubmitter"
                   bean="${formHandler}.delete" title="${deleteButtonToolTip}"/>
      <input type="button" value="<fmt:message key='search_env_delete.button.cancel'/>"
             onclick="closePopUp()" title="<fmt:message key='search_env_delete.button.cancel.tooltip'/>"/>
    </div>

  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/search_env_delete_popup.jsp#2 $$Change: 651448 $--%>
