<%--
  JSP shows all dictionaries and provides creation/deletion ,import/export, copying of dictionaries

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_sets.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/TermWeightSetsFormHandler"/>
  <c:url value="${weightPath}/new_term_weight_set.jsp" var="createWeightSetURL"/>
  <c:url value="${weightPath}/term_weight_set_import.jsp" var="importURL"/>
  <c:url value="${weightPath}/term_weight_sets.jsp" var="successURL"/>

  <termweights:termWeightSetFindAll var="weights"/>

  <%-- Retrieving current value for table sorting --%>
  <d:getvalueof bean="TermWeightSetsFormHandler.sort" var="sortValue"/>

  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.termWeightSetComparator" var="comparator"/>
  <admin-ui:sort var="weights" items="${weights}" comparator="${comparator}" sortMode="${sortValue}"/>

  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId"
                var="activeProjectId"/>
  <fmt:message key="term_weight_sets.table.partOfActiveProject" var="activeSearchProjectTitle"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <d:form action="${successURL}" method="post">
      <div id="paneContent">
        <p>
          <input type="button"
                 value="<fmt:message key='term_weight_sets.button.new.weight.set'/>"
                 title="<fmt:message key='term_weight_sets.button.new.weight.set.tooltip'/>"
                 onclick="return loadRightPanel('${createWeightSetURL}');"/>
          <input type="button"
                 value="<fmt:message key='term_weight_sets.button.import.weight.set'/>"
                 title="<fmt:message key='term_weight_sets.button.import.weight.set.tooltip'/>"
                 onclick="return loadRightPanel('${importURL}');"/>
        </p>
        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="termWeight" items="${weights}"
                        sort="${sortValue}" onSort="tableOnSort">
          <admin-ui:column title="termdicts_general.table.name" type="sortable" name="name">
            <c:url var="weightSetUrl" value="${weightPath}/term_weight_set.jsp">
              <c:param name="termWeightId" value="${termWeight.id}"/>
            </c:url>
            <a href="${weightSetUrl}" onclick="return loadRightPanel(this.href);">
              <c:out value="${termWeight.name}"/>
            </a>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <c:set var="display" value="none"/>
            <admin-beans:getProjectsByCustomizationItem var="projects" itemId="${termWeight.id}" itemType="term_weight"/>
            <c:set var="allUsedProjects"/>
            <c:forEach items="${projects}" var="currentProject">
              <c:set var="allUsedProjects" value="${allUsedProjects} ${currentProject.id}"/>
              <c:if test="${currentProject.id eq activeProjectId}">
                <c:set var="display" value="inline"/>
              </c:if>
            </c:forEach>
            <a class="icon partOfActiveSearchProjectAsset ${allUsedProjects}" title="${activeSearchProjectTitle}" href="#"
               style="display:${display}" onclick="return false;">proj</a>
          </admin-ui:column>
          <admin-ui:column title="termdicts_general.table.description" type="sortable" name="description">
            <c:out value="${termWeight.description}"/>
          </admin-ui:column>
          <admin-ui:column title="term_weight_sets.table.language" type="sortable" name="language">
            <c:if test="${empty termWeight.language}">
              <fmt:message key="term_weight_sets.table.language.none"/>
            </c:if>
            <c:if test="${not empty termWeight.language}">
              <admin-beans:getLocalizedLanguage var="localizedLanguage" language="${termWeight.language}"/>
              <c:out value="${localizedLanguage}"/>
            </c:if>
          </admin-ui:column>
          <admin-ui:column title="termdicts_general.table.modified" type="sortable" name="lastModified">
            <fmt:message var="timeFormat" key="timeFormat"/>
            <fmt:formatDate value="${termWeight.lastModified}" type="both" pattern="${timeFormat}"/>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="term_weight_sets.table.copy" var="copyTitle"/>
            <a class="icon propertyCopy" title="${copyTitle}" href="#"
               onclick="submitTermWeightSetsForm(${termWeight.id}, 'copyInput'); return false;">copy</a>
            <d:input type="hidden" bean="TermWeightSetsFormHandler.weightSetId"
                     value="${termWeight.id}" id="weightSetId"/>
            <d:input bean="TermWeightSetsFormHandler.copy"
                     value="field mode" type="submit" iclass="formsubmitter"
                     id="copyInput" style="display:none"/>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="term_weight_sets.table.export" var="exportTitle"/>
            <c:url var="exportUrl" value="${weightPath}/term_weight_set_export.jsp">
              <c:param name="termWeightId" value="${termWeight.id}"/>
            </c:url>
            <a class="icon propertyExport" title="${exportTitle}" href="${exportUrl}"
               onclick="return loadRightPanel(this.href);">exp</a>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="term_weight_sets.table.delete" var="deleteTitle"/>
            <c:url value="${weightPath}/term_weight_set_delete_popup.jsp" var="popUrl">
              <c:param name="weightSetId" value="${termWeight.id}"/>
            </c:url>
            <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
               onclick="return showPopUp(this.href);">del</a>
          </admin-ui:column>
        </admin-ui:table>
        <d:input bean="TermWeightSetsFormHandler.successURL" type="hidden" value="${successURL}" name="successURL"/>
      </div>

      <d:input bean="TermWeightSetsFormHandler.sort" value="field mode" type="submit"
               id="sortInput" iclass="formsubmitter" style="display:none"/>

      <script type="text/javascript">
        function submitTermWeightSetsForm(id, mode) {
          document.getElementById('weightSetId').value = id;
          document.getElementById(mode).click();
        }
        function tableOnSort(tableId, columnName, sortDirection) {
          var sortButton = document.getElementById('sortInput');
          sortButton.value = columnName + " " + sortDirection;
          sortButton.click();
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
    </d:form>
  </div>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootTermWeightNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/term_weight_sets.jsp#2 $$Change: 651448 $--%>
