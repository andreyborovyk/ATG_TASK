<%--
JSP, that allows to remove active project.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/remove_active_project_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="projectId" var="projectId" />

  <d:importbean var="handler" bean="/atg/searchadmin/workbenchui/formhandlers/ActiveSearchProjectFormHandler" />
  <script type="text/javascript">
    function customLoad(){
      atg.searchadmin.adminui.formsubmitter.customPopupHandleResponse = function(data) {
        refreshTreeArea(true);
      };
    }
  </script>
  
  <d:form action="remove_active_project_popup.jsp" method="POST">
    <c:url var="errorURL" value="/workbench/remove_active_project_popup.jsp">
      <c:param name="projectId" value="${projectId}"/>
    </c:url>

    <d:input type="hidden" bean="ActiveSearchProjectFormHandler.errorURL" value="${errorURL}" />
    <c:catch>
      <%-- It's possible that current Active Project is removed. In this case Active Project area should be refreshed. --%>
      <common:searchProjectFindByPrimaryKey searchProjectId="${projectId}" var="project"/>
    </c:catch>
    <c:set value="/atg/searchadmin/workbenchui/formhandlers/ActiveSearchProjectFormHandler" var="handler"/>

    <div class="content">
      <p>
        <fmt:message key="remove_active_project.label1.text">
          <fmt:param>
            <strong>
              <fmt:message key="remove_active_project.label2.text">
                <fmt:param>
                  <c:if test="${not empty project}"><c:out value="${project.name}"/></c:if>
                </fmt:param>
              </fmt:message>
            </strong>
          </fmt:param>
        </fmt:message>
      </p>
    </div>

    <div class="footer" id="popupFooter">
      <fmt:message key="active_project.ok.button" var="button_ok"/>
      <fmt:message key="remove_active_project.ok.tooltip" var="tooltip_ok"/>
      <d:input type="submit" bean="ActiveSearchProjectFormHandler.deleteProject" id="deleteProjectButton" 
               name="deleteProject" value="${button_ok}" title="${tooltip_ok}" iclass="formsubmitter"/>
      <input type="button" value="<fmt:message key='active_project.cancel.button'/>"
             onclick="closePopUp();" name="cancel "
             title="<fmt:message key='active_project.cancel.tooltip'/>"/>
    </div>
  </d:form>
  <c:if test="${empty project}">
    <%-- If current Active Project is removed, Active Project area should be refreshed. Submitting the form to do it. --%>
    <script>document.getElementById("deleteProjectButton").click();</script>
  </c:if>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/remove_active_project_popup.jsp#2 $$Change: 651448 $--%>
