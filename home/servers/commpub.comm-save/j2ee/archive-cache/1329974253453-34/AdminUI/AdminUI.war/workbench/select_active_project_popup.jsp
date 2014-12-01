<%--
JSP, that allows to select  active project.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/select_active_project_popup.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <script type="text/javascript">
    function customLoad(){
      atg.searchadmin.adminui.formsubmitter.customPopupHandleResponse = function(data) {
        refreshTreeArea(false);
      };
    }
  </script>
  <d:form action="select_active_project_popup.jsp" method="POST">

    <c:url var="errorURL" value="/workbench/select_active_project_popup.jsp"/>

    <d:input type="hidden" value="${errorURL}" name="errorURL"
             bean="/atg/searchadmin/workbenchui/formhandlers/ActiveSearchProjectFormHandler.errorURL" />
    <d:input type="hidden" value="1" id="partitionNumber" name="selectedPartitionNumber"
             bean="/atg/searchadmin/workbenchui/formhandlers/ActiveSearchProjectFormHandler.selectedPartitionNumber" />
    <d:input type="hidden" value="false" name="needInitialization"
             bean="/atg/searchadmin/workbenchui/formhandlers/ActiveSearchProjectFormHandler.needInitialization" />

    <d:importbean var="activeSearchProject" bean="/atg/searchadmin/workbenchui/formhandlers/ActiveSearchProjectFormHandler"/>
    
    <admin-ui:initializeFormHandler handler="${activeSearchProject}"/>

    <div class="content">

      <p><fmt:message key="active_project_control.label.text"/></p>
      <common:searchProjectFindAll var="projects"/>
      <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.searchProjectComparator" var="comparator"/>
      <admin-ui:sort var="searchProjects" items="${projects}" comparator="${comparator}" sortMode="undefined"/>

      <c:set var="isProjects" value="${not empty searchProjects}"/>

      <c:if test="${isProjects}">
        <table class="form">
          <tr>
            <td class="label">
              <span class="required"><fmt:message key="project_general.required_field"/></span>
              <fmt:message key="active_project.search_project"/>
            </td>
            <td>
              <c:if test="${empty activeSearchProject.selectedProjectId}">
                <d:setvalue bean="ActiveSearchProjectFormHandler.selectedProjectId" value="${searchProjects[0].id}"/>
              </c:if>

              <d:select bean="ActiveSearchProjectFormHandler.selectedProjectId" iclass="small" name="selectedProjectId"
                  id="selectedProjectIdSelect" onchange="document.getElementById('changeProjectButton').click();">
                <c:forEach items="${searchProjects}" var="project">
                  <d:option value="${project.id}">
                    <c:out value="${project.name}"/>
                  </d:option>
                </c:forEach>
              </d:select>
            </td>
          </tr>

          <common:searchProjectFindByPrimaryKey searchProjectId="${activeSearchProject.selectedProjectId}" var="project"/>
          <tr>
            <td class="label">
              <span class="required"><fmt:message key="project_general.required_field"/></span>
              <fmt:message key="active_project.environment"/>
            </td>
            <td>
              <admin-beans:getSearchEnvironments var="searchEnvironments" projectId="${activeSearchProject.selectedProjectId}"
                  deploymentOnly="true" />
              <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.searchEnvironmentComparator" var="comparator"/>
              <admin-ui:sort var="sortedSearchEnvironments" items="${searchEnvironments}" comparator="${comparator}"
                             sortMode="${sortValue}"/>
              <table cellpadding="0" cellspacing="0">
                <c:forEach items="${sortedSearchEnvironments}" var="searchEnvironment">
                  <admin-beans:getSearchEnvironmentStatus var="statusBean" environmentId="${searchEnvironment.id}"/>
                  <tr>
                    <td>
                      <c:if test="${empty activeSearchProject.selectedEnvironmentId and not statusBean.stopped}">
                        <d:setvalue bean="ActiveSearchProjectFormHandler.selectedEnvironmentId" value="${searchEnvironment.id}"/>
                      </c:if>
                      <d:input type="radio" name="insertEnvironment"
                          bean="ActiveSearchProjectFormHandler.selectedEnvironmentId" value="${searchEnvironment.id}"
                          id="insertEnvironment_${searchEnvironment.id}" disabled="${statusBean.stopped}" />
                      <label for="insertEnvironment_${searchEnvironment.id}"><c:out value="${searchEnvironment.envName}" /></label>
                    </td>
                  </tr>
                </c:forEach>
              </table>
            </td>
          </tr>
        </table>
        <d:input type="submit" bean="ActiveSearchProjectFormHandler.changeProject" name="changeProject"
                 style="display:none;" id="changeProjectButton" iclass="formsubmitter"/>
      </c:if>
      <c:if test="${not isProjects}">
        <h3><fmt:message key="active_project_control.label.no_any_projects"/></h3>
      </c:if>
      <span id="facetsLabel" style="display:none"><fmt:message key="facetsets.dojo.node.label.indexed.sets">
        <fmt:param value="${project.name}" />
      </fmt:message></span>
    </div>

    <div class="footer" id="popupFooter">
      <c:if test="${isProjects}">
        <fmt:message key="active_project.ok.button" var="button_ok"/>
        <fmt:message key="active_project.ok.tooltip" var="tooltip_ok"/>
        <d:input type="submit" bean="ActiveSearchProjectFormHandler.selectProject" name="selectProject"
                 value="${button_ok}" title="${tooltip_ok}" iclass="formsubmitter"
                 id="selectProjectButton" disabled="${empty activeSearchProject.selectedEnvironmentId}"/>
      </c:if>
      <input type="button" value="<fmt:message key='active_project.cancel.button'/>"
             onclick="closePopUp()" name="cancel "
             title="<fmt:message key='active_project.cancel.tooltip'/>"/>
    </div>
  </d:form>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/select_active_project_popup.jsp#2 $$Change: 651448 $--%>
