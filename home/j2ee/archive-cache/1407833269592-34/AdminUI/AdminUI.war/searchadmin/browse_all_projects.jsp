<%--
Shows all projects, according to user's rights.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/browse_all_projects.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <%-- Description if it's not empty --%>
      <c:if test="${not empty description}">
        <p><fmt:message key="browse_all_projects.description"/></p>
      </c:if>
      <%-- "New Project" button. --%>
      <p>
        <c:url var="newProjectUrl" value="/searchadmin/new_project.jsp"/>
        <input type="button" onclick="return loadRightPanel('${newProjectUrl}');"
               value="<fmt:message key='browse_all_projects.new.project.button'/>"
               title="<fmt:message key='browse_all_projects.new.project.button.tooltip'/>"/>
      </p>
      <%-- Table with info about all projects --%>
      <d:getvalueof bean="/atg/searchadmin/repository/service/SearchProjectService.productionProjects" var="productionProjects"/>
      <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.searchProjectComparator" var="comparator"/>
      <admin-ui:sort var="projects" items="${productionProjects}" comparator="${comparator}" sortMode="undefined"/>

      <table class="data groupped" cellspacing="0" cellpadding="0">
        <thead>
          <tr>
            <th><fmt:message key="browse_all_projects.project.name"/></th>
            <th><fmt:message key="browse_all_projects.project.description"/></th>
            <th><fmt:message key="browse_all_projects.project.target_type"/></th>
            <th>&nbsp;</th>
            <th>&nbsp;</th>
          </tr>
        </thead>
        <tbody>
          <c:if test="${empty projects}">
            <tr>
              <td colspan="5"><fmt:message key="error_message.empty_table"/></td>
            </tr>
          </c:if>
          <fmt:message key="browse_all_projects.project.tooltip" var="projectTooltip"/>
          <c:forEach var="project" items="${projects}" varStatus="projectStatus">
            <c:set var="childProjects" value="${project.childProjects}" />
            <tr class="groupstart<c:if test="${(projectStatus.index % 2) == 1}"> alt</c:if>">
              <td>
                <c:url value="/searchadmin/project.jsp" var="projectUrl">
                  <c:param name="projectId" value="${project.id}"/>
                </c:url>
                <a href="${projectUrl}" title="${projectTooltip}" onclick="return loadRightPanel(this.href);">
                  <c:out value="${project.name}"/>
                </a>
              </td>
              <td><c:out value="${project.description}"/></td>
              <td><c:out value="${project.targetType}"/></td>
              <td nowrap="">
                <fmt:message key="browse_all_projects.copy.tooltip" var="copyTitle"/>
                <c:url value="/searchadmin/project_copy_popup.jsp" var="copyUrl">
                  <c:param name="projectId" value="${project.id}"/>
                </c:url>
                <a title="${copyTitle}" href="${copyUrl}" onclick="return showPopUp(this.href);">
                  <img src="images/icons/icon_copyAndLink.gif" border="0" />
                  <fmt:message key="browse_all_projects.copy.title" />
                </a>
              </td>
              <td class="iconCell">
                <c:if test="${empty childProjects}">
                  <fmt:message key="browse_all_projects.delete.tooltip" var="deleteTitle"/>
                  <c:url value="/searchadmin/project_delete_popup.jsp" var="deleteUrl">
                    <c:param name="projectId" value="${project.id}"/>
                  </c:url>
                  <a class="icon propertyDelete" title="${deleteTitle}"
                     href="${deleteUrl}" onclick="return showPopUp(this.href);">del</a>
                </c:if>
              </td>
            </tr>
            <admin-ui:sort var="childProjects" items="${childProjects}" comparator="${comparator}" sortMode="undefined"/>
            <c:forEach var="childProject" items="${childProjects}">
              <tr <c:if test="${(projectStatus.index % 2) == 1}">class="alt"</c:if>>
                <td>
                  <fmt:message key="browse_all_projects.project.tooltip" var="projectTooltip"/>
                  <c:url value="/searchadmin/project.jsp" var="projectUrl">
                    <c:param name="projectId" value="${childProject.id}"/>
                  </c:url>
                  <fmt:message key="browse_all_projects.project.tooltip.linked" var="linkedProjectTooltip">
                    <fmt:param value="${project.name}" />
                  </fmt:message>
                  &nbsp;
                  <a href="${projectUrl}" title="${linkedProjectTooltip}" onclick="return loadRightPanel(this.href);">
                    <img src="images/icons/icon_linkedProject.gif" border="0" />
                    &nbsp;
                    <c:out value="${childProject.name}"/>
                  </a>
                </td>
                <td><c:out value="${childProject.description}"/></td>
                <td><c:out value="${childProject.targetType}"/></td>
                <td>&nbsp;</td>
                <td class="iconCell">
                  <fmt:message key="browse_all_projects.delete.tooltip" var="deleteTitle"/>
                  <c:url value="/searchadmin/project_delete_popup.jsp" var="deleteUrl">
                    <c:param name="projectId" value="${childProject.id}"/>
                  </c:url>
                  <a class="icon propertyDelete" title="${deleteTitle}"
                     href="${deleteUrl}" onclick="return showPopUp(this.href);">del</a>
                </td>
              </tr>
            </c:forEach>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/searchadmin/browse_all_projects.jsp#2 $$Change: 651448 $--%>
