<%--
  JSP shows all dictionaries and provides creation/deletion ,import/export, copying of dictionaries

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdicts_general.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ManageTermDictionaryFormHandler"/>
  <d:getvalueof bean="ManageTermDictionaryFormHandler.sort" var="sortValue"/>
  <dictionary:termDictionaryFindAll var="dictionaries"/>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.termDictionaryComparator" var="comparator"/>
  <admin-ui:sort var="sortedDictionaries" items="${dictionaries}" comparator="${comparator}" sortMode="${sortValue}"/>

  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"
                var="activeProjectId"/>
  <fmt:message key="termdicts_general.table.partOfActiveProject" var="activeSearchProjectTitle"/>

  <c:url value="${dictionaryPath}/new_term_dictionary.jsp" var="createNewDictURL"/>
  <c:url value="${dictionaryPath}/termdict_import.jsp" var="importURL"/>
  <c:url value="${dictionaryPath}/termdicts_general.jsp" var="backURL"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <d:form action="${backURL}" method="post">
        <p>
          <input type="button"
                 value="<fmt:message key='termdicts_general.button.new_dict'/>"
                 title="<fmt:message key='termdicts_general.button.new_dict.tooltip'/>"
                 onclick="return loadRightPanel('${createNewDictURL}');"/>
          <input type="button"
                 value="<fmt:message key='termdicts_general.button.import'/>"
                 title="<fmt:message key='termdicts_general.button.import.tooltip'/>"
                 onclick="return loadRightPanel('${importURL}');"/>
          <span class="ea"><tags:ea key="embedded_assistant.termdicts_general" /></span>
        </p>

        <%-- Table contain info about Dictionary. columns is sortable. --%>
        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="termDictionary" items="${sortedDictionaries}"
                        sort="${sortValue}" onSort="tableOnSort">
          <admin-ui:column title="termdicts_general.table.name" type="sortable" name="name">
            <c:url var="termDictUrl" value="${dictionaryPath}/term_dictionary.jsp">
              <c:param name="dictId" value="${termDictionary.id}"/>
            </c:url>
            <a href="${termDictUrl}" onclick="return loadRightPanel(this.href);">
              <c:out value="${termDictionary.name}"/>
            </a>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <c:set var="display" value="none"/>
            <admin-beans:getProjectsByCustomizationItem var="projects" itemId="${termDictionary.id}" itemType="term_dict"/>
            <c:set var="allUsedProjects"/>
            <c:forEach items="${projects}" var="currentProject">
              <c:set var="allUsedProjects" value="${allUsedProjects} ${currentProject.id}"/>
              <c:if test="${currentProject.id eq activeProjectId}">
                <c:set var="display" value="inline"/>
              </c:if>
            </c:forEach>
            <a class="icon partOfActiveSearchProjectAsset ${allUsedProjects}" title="${activeSearchProjectTitle}"
               href="#" style="display:${display}">proj</a>
          </admin-ui:column>
          <admin-ui:column title="termdicts_general.table.description" type="sortable" name="description">
            <c:out value="${termDictionary.description}"/>
          </admin-ui:column>
          <admin-ui:column title="termdicts_general.table.language" type="sortable" name="language">
            <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${termDictionary.language}"/>
            <c:out value="${localizedLanguage}"/>
          </admin-ui:column>
          <admin-ui:column title="termdicts_general.table.modified" type="sortable" name="modified">
            <fmt:message var="timeFormat" key="timeFormat"/>
            <fmt:formatDate value="${termDictionary.lastModified}" type="both"
                   pattern="${timeFormat}"/>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="termdicts_general.table.copy" var="copyTitle"/>
            <a class="icon propertyCopy" title="${copyTitle}" href="#"
               onclick="return submitTermDictsForm(${termDictionary.id}, 'copyInput');">copy</a>
            <d:input type="hidden" bean="ManageTermDictionaryFormHandler.dictId"
                     value="${termDictionary.id}" id="dictId"/>
            <d:input bean="ManageTermDictionaryFormHandler.copy"
                     value="field mode" type="submit" iclass="formsubmitter"
                     id="copyInput" style="display:none"/>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="termdicts_general.table.export" var="exportTitle"/>
            <%-- TODO insert link for 'export' icon --%>
            <c:url value="${dictionaryPath}/termdict_export.jsp" var="exportURL">
              <c:param name="dictionaryId" value="${termDictionary.id}"/>
            </c:url>
            <a class="icon propertyExport" title="${exportTitle}" href="${exportURL}"
                 onclick="return loadRightPanel(this.href);">exp</a>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="termdicts_general.table.delete" var="deleteTitle"/>
            <c:url value="${dictionaryPath}/term_dict_delete_popup.jsp" var="popUrl">
              <c:param name="dictId" value="${termDictionary.id}"/>
            </c:url>
            <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
                 onclick="return showPopUp(this.href);">del</a>
          </admin-ui:column>
        </admin-ui:table>

        <script type="text/javascript">
          function tableOnSort(tableId, columnName, sortDirection) {
            var sortButton = document.getElementById('sortInput');
            sortButton.value = columnName + " " + sortDirection;
            sortButton.click();
          }
          function submitTermDictsForm(id, mode) {
            document.getElementById('dictId').value = id;
            document.getElementById(mode).click();
            return false;
          }
          function refreshRightPane(obj) {
            icons = getElementsByClassName('partOfActiveSearchProjectAsset');
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
        <d:input type="submit" bean="ManageTermDictionaryFormHandler.sort" style="display:none" iclass="formsubmitter" id="sortInput"/>
        <d:input bean="ManageTermDictionaryFormHandler.successURL" type="hidden" value="${backURL}" name="successURL"/>
        <d:input bean="ManageTermDictionaryFormHandler.errorURL" type="hidden" value="${backURL}" name="errorURL"/>
      </d:form>
    </div>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootDictNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/termdicts_general.jsp#2 $$Change: 651448 $--%>
