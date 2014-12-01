<%--
  Browse TPO Sets JSP

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_browse.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="level" var="level"/>

  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TPOSetBaseFormHandler"/>

  <c:url value="${tpoPath}/tpo_browse.jsp" var="successURL">
    <c:param name="level" value="${level}"/>
  </c:url>

  <fmt:message key="tpo_browse.title" var="tpoTitle">
    <fmt:param value="level"/>
  </fmt:message>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">

      <c:if test="${level eq 'index'}">
        <c:set var="buttonNameParam" value="Search Project"/>
        <fmt:message key="tpo_browse.project_head" var="headMessage"/>
      </c:if>
      <c:if test="${level eq 'content'}">
        <c:set var="buttonNameParam" value="Content"/>
        <fmt:message key="tpo_browse.content_head" var="headMessage"/>
      </c:if>
      <p>
        <c:out value="${headMessage}"/>
      </p>

      <c:url value="${tpoPath}/tpo_${level}_edit_set.jsp" var="newTPOSetURL"/>

      <c:url value="${tpoPath}/tpo_set_import.jsp" var="importURL">
        <c:param name="level" value="${level}"/>
      </c:url>

      <p>
        <input type="button"
               value="<fmt:message key='tpo_browse.new.button'><fmt:param value="${buttonNameParam}"/></fmt:message>"
               title="<fmt:message key='tpo_browse.new.tooltip'><fmt:param value="${buttonNameParam}"/></fmt:message>"
               onclick="return loadRightPanel('${newTPOSetURL}');"/>
        <input type="button"
               value="<fmt:message key='tpo_browse.import.button'><fmt:param value="${buttonNameParam}"/></fmt:message>"
               title="<fmt:message key='tpo_browse.import.tooltip'><fmt:param value="${buttonNameParam}"/></fmt:message>"
               onclick="return loadRightPanel('${importURL}');"/>
      </p>

      <tpo:textProcessingOptionsSetFindByLevel var="tpo_sets" level="${level}"/>

      <%-- Retrieving current value for table sorting --%>
      <d:getvalueof bean="TPOSetBaseFormHandler.sort" var="sortValue"/>

      <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.tpoSetComparator" var="comparator"/>
      <admin-ui:sort var="tpo_sets" items="${tpo_sets}" comparator="${comparator}" sortMode="${sortValue}"/>

      <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                      var="tpoSet" items="${tpo_sets}"
                      sort="${sortValue}" onSort="tableOnSort">
        <admin-ui:column title="tpo_browse.table.option_set_name" type="sortable" name="name">
          <fmt:message key="browse_all_projects.project.tooltip" var="projectTooltip"/>
          <c:url var="editTpoUrl" value="${tpoPath}/tpo_${level}_edit_set.jsp">
            <c:param name="tpoSetId" value="${tpoSet.id}"/>
          </c:url>
          <a href="${editTpoUrl}" title="${projectTooltip}" onclick="return loadRightPanel(this.href);">
            <c:out value="${tpoSet.name}"/>
          </a>
        </admin-ui:column>
        <admin-ui:column title="tpo_browse.table.option_created" type="sortable" name="creationDate">
          <fmt:message key="tpo_browse.table.option_created.tooltip" var="projectTooltip"/>
          <fmt:message var="timeFormat" key="timeFormat"/>
          <fmt:formatDate value="${tpoSet.created}" type="both" pattern="${timeFormat}"/>
        </admin-ui:column>
        <admin-ui:column title="tpo_browse.table.option_modified" type="sortable" name="modificationDate">
          <fmt:message key="tpo_browse.table.option_modified.tooltip" var="projectTooltip"/>
          <fmt:message var="timeFormat" key="timeFormat"/>
          <fmt:formatDate value="${tpoSet.lastModified}" type="both" pattern="${timeFormat}"/>
        </admin-ui:column>
        <admin-ui:column type="icon">
          <fmt:message key="tpo_browse.table.copy.tooltip" var="copyTitle"/>
          <a class="icon propertyCopy" title="${copyTitle}" href="#"
             onclick="return submitForm(${tpoSet.id}, 'copyInput');">copy</a>
        </admin-ui:column>
        <admin-ui:column type="icon">
          <fmt:message key="tpo_browse.table.export.tooltip" var="exportTitle"/>
          <c:url var="exportTpoUrl" value="${tpoPath}/tpo_set_export.jsp">
            <c:param name="level" value="${level}"/>
            <c:param name="tpoSetId" value="${tpoSet.id}"/>
          </c:url>
          <a class="icon propertyExport" title="${exportTitle}" href="${exportTpoUrl}"
             onclick="return loadRightPanel(this.href);">exp</a>
        </admin-ui:column>
        <admin-ui:column type="icon">
          <fmt:message key="tpo_browse.table.delete.tooltip" var="deleteTitle"/>
          <c:url value="${tpoPath}/tpo_set_delete_popup.jsp" var="popUrl">
            <c:param name="tpoSetId" value="${tpoSet.id}"/>
            <c:param name="level" value="${level}"/>
          </c:url>
          <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
             onclick="return showPopUp(this.href);">del</a>
        </admin-ui:column>
      </admin-ui:table>

      <d:form method="POST" action="tpo_browse.jsp">
        <d:input bean="TPOSetBaseFormHandler.copy" value="field mode" type="submit" iclass="formsubmitter"
                 name="copyInput" id="copyInput" style="display:none"/>
        <d:input bean="TPOSetBaseFormHandler.TPOSetId" type="hidden" name="TPOSetId" id="TPOSetId"/>
        <d:input bean="TPOSetBaseFormHandler.successURL" value="${successURL}" type="hidden" name="successURL"
                 id="successURL"/>
        <d:input bean="TPOSetBaseFormHandler.sort" value="field mode" type="submit"
                 id="sortInput" iclass="formsubmitter" style="display:none"/>
      </d:form>

      <script type="text/javascript">
        function tableOnSort(tableId, columnName, sortDirection) {
          var sortButton = document.getElementById('sortInput');
          sortButton.value = columnName + " " + sortDirection;
          sortButton.click();
        }
        function submitForm(id, mode) {
          document.getElementById('TPOSetId').value = id;
          document.getElementById(mode).click();
          return false;
        }
      </script>
    </div>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootTpoNode"}, {id:"<c:out value="${level}"/>TpoNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tpo_browse.jsp#2 $$Change: 651448 $--%>
