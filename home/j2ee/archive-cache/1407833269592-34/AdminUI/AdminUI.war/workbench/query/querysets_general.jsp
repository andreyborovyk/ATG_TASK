<%--
  JSP,  showing all Query Rules Sets.
  Used allow import/export Topic Set.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/querysets_general.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/QuerySetsFormHandler"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <queryrule:queryRuleSetFindAll var="querySets"/>

    <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId" var="activeProjectId"/>
    <fmt:message key="querysets_general.table.partOfActiveProject" var="activeSearchProjectTitle"/>

    <%-- Urls --%>
    <c:url value="${queryPath}/querysets_import.jsp" var="importURL"/>
    <c:url value="${queryPath}/queryset_new.jsp" var="newQuerySetURL" />
    <c:url value="${queryPath}/querysets_general.jsp" var="backURL"/>
    <c:url value="${queryPath}/querysets_macros.jsp" var="macrosURL"/>

    <%-- Retrieving current value for table sorting --%>
    <d:getvalueof bean="QuerySetsFormHandler.sort" var="sortValue"/>
    <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.queryRuleSetComparator" var="comparator"/>
    <admin-ui:sort var="querySets" items="${querySets}" comparator="${comparator}" sortMode="${sortValue}"/>

    <d:form action="${backURL}" method="post">
      <div id="paneContent">
        <p>
          <%-- New button --%>
          <tags:buttonLink titleKey="querysets_general.button.new_query"
                           tooltipKey="querysets_general.button.new_query.tooltip" href="${newQuerySetURL}" />
          <%-- Import button --%>
          <tags:buttonLink titleKey="querysets_general.button.import_query"
                           tooltipKey="querysets_general.button.import_query.tooltip" href="${importURL}" />
          <%-- Link to macros page --%>
          <fmt:message key="querysets_general.message.global_macro">
            <queryrule:globalQueryRuleSetMacroFindAll var="globalMacros"/>
            <fmt:param value="${fn:length(globalMacros)}"/>
          </fmt:message>
          [<a href="${macrosURL}" onclick="return loadRightPanel(this.href);" title="<fmt:message key='querysets_general.link.edit_macros.tooltip'/>">
            <fmt:message key="querysets_general.link.edit_macros"/>
          </a>]
          <span class="ea"><tags:ea key="embedded_assistant.querysets_general" /></span>
        </p>

        <%-- table for show query rule sets --%>
        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="querySet" items="${querySets}"
                        sort="${sortValue}" onSort="tableOnSort">
          <admin-ui:column title="querysets_general.table.name" type="sortable" name="name">
            <c:url value="${queryPath}/queryset.jsp" var="querySetURL">
              <c:param name="querySetId" value="${querySet.id}"/>
            </c:url>
            <a href="${querySetURL}" onclick="return loadRightPanel(this.href);">
              <c:out value="${querySet.name}"/>
            </a>
          </admin-ui:column>

          <admin-ui:column type="icon">
            <c:set var="display" value="none"/>
            <admin-beans:getProjectsByCustomizationItem var="projects" itemId="${querySet.id}" itemType="query_rules"/>
            <c:set var="allUsedProjects"/>
            <c:forEach items="${projects}" var="currentProject">
              <c:set var="allUsedProjects" value="${allUsedProjects} ${currentProject.id}"/>
              <c:if test="${currentProject.id eq activeProjectId}">
                <c:set var="display" value="inline"/>
              </c:if>
            </c:forEach>
            <a class="icon partOfActiveSearchProjectAsset ${allUsedProjects}" title="${activeSearchProjectTitle}"
                 href="#" style="display:${display}" onclick="return false;">proj</a>
          </admin-ui:column>

          <admin-ui:column title="querysets_general.table.description" type="sortable" name="description">
            <c:out value="${querySet.description}"/>
          </admin-ui:column>

          <admin-ui:column title="querysets_general.table.modified" type="sortable" name="lastModified">
            <fmt:message var="timeFormat" key="timeFormat"/>
            <fmt:formatDate value="${querySet.lastModified}" type="both" pattern="${timeFormat}"/>
          </admin-ui:column>

          <admin-ui:column type="icon">
            <fmt:message key="querysets_general.table.copy" var="copyTitle"/>
            <a class="icon propertyCopy" title="${copyTitle}" href="#"
                 onclick="return submitQuerySetForm(${querySet.id}, 'copyInput');">copy</a>
          </admin-ui:column>

          <admin-ui:column type="icon">
            <fmt:message key="querysets_general.table.export" var="exportTitle"/>
            <c:url value="${queryPath}/querysets_export.jsp" var="exportURL">
              <c:param name="querySetId" value="${querySet.id}"/>
            </c:url>
            <a class="icon propertyExport" onclick="return loadRightPanel(this.href);" title="${exportTitle}"
               href="${exportURL}">exp</a>
          </admin-ui:column>

          <admin-ui:column type="icon">
            <fmt:message key="querysets_general.table.delete" var="deleteTitle"/>
            <c:url value="${queryPath}/querysets_delete_popup.jsp" var="popUrl">
              <c:param name="querySetId" value="${querySet.id}"/>
            </c:url>
            <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
               onclick="return showPopUp(this.href);">del</a>
          </admin-ui:column>
        </admin-ui:table>

      </div>

      <d:input bean="QuerySetsFormHandler.successURL" name="successURL" type="hidden" value="${backURL}"/>
      <d:input bean="QuerySetsFormHandler.errorURL" name="errorURL" type="hidden" value="${backURL}"/>
      <d:input bean="QuerySetsFormHandler.cancelURL" name="cancelURL" type="hidden" value="${backURL}"/>

      <d:input bean="QuerySetsFormHandler.copyQueryRuleSet" value="field mode" type="submit" id="copyInput" 
               iclass="formsubmitter" style="display:none"/>
      <d:input bean="QuerySetsFormHandler.querySetId" type="hidden" id="querySetId"/>
      <d:input bean="QuerySetsFormHandler.sort" value="field mode" type="submit" id="sortInput" 
               iclass="formsubmitter" style="display:none"/>
    </d:form>

    <script type="text/javascript">
      function submitQuerySetForm(id, mode) {
        document.getElementById('querySetId').value = id;
        document.getElementById(mode).click();
        return false;
      }
      function tableOnSort(tableId, columnName, sortDirection) {
        var sortButton = document.getElementById('sortInput');
        sortButton.value = columnName + " " + sortDirection;
        sortButton.click();
      }
      function refreshRightPane(obj) {
        var icons = getElementsByClassName('partOfActiveSearchProjectAsset');
        for (var i = 0; i < icons.length; i++) {
          icons[i].style.display = 'none';
        }
        if (obj.activeProjectId) {
          icons = getElementsByClassName(obj.activeProjectId);
          for (var i = 0; i < icons.length; i++) {
            icons[i].style.display = 'inline';
          }
        }
      }
    </script>
    <script type="text/javascript">
      //dojo tree refresh
      top.hierarchy = [{id:"rootQrNode"}];
      top.syncTree();
    </script>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/querysets_general.jsp#2 $$Change: 651448 $--%>
