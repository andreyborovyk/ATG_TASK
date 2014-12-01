<%--
  General facet sets page, shows all facet sets and provides creation/deletion, import/export, copying of facet sets.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetsets_general.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <%-- Form handler, used to implement copy of facet set and table sorting --%>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/RefConfigFormHandler"/>

  <%-- Current sort value --%>
  <d:getvalueof bean="RefConfigFormHandler.sort" var="sortValue"/>

  <%-- URL Definitions--%>
  <%-- Create new facet set URL --%>
  <c:url value="${facetPath}/facetset_new.jsp" var="createFacetSetURL"/>
  <%-- Import facet set URL --%>
  <c:url value="${facetPath}/facetset_import.jsp" var="importURL"/>
  <%-- Success url, used by copying and table sorting --%>
  <c:url value="${facetPath}/facetsets_general.jsp" var="successURL"/>

  <facets:facetSetFindAll var="facetSets"/>
  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.facetSetsComparator" var="comparator"/>
  <admin-ui:sort var="sortedFacetSets" items="${facetSets}" comparator="${comparator}" sortMode="${sortValue}"/>

  <div id="rightPanelContent" dojoType="dojox.layout.ContentPane" layoutAlign="client" 
       executeScripts="true" cacheContent="false" scriptSeparation="false">
    <div id="paneContent">
      <d:form action="${successURL}" method="POST">
        <p>
          <input type="button"
                 value="<fmt:message key='facetsets_general.button.new.weight.set'/>"
                 title="<fmt:message key='facetsets_general.button.new.weight.set.tooltip'/>"
                 onclick="return loadRightPanel('${createFacetSetURL}');" name="create"/>
          <input type="button"
                 value="<fmt:message key='facetsets_general.button.import.weight.set'/>"
                 title="<fmt:message key='facetsets_general.button.import.weight.set.tooltip'/>"
                 onclick="return loadRightPanel('${importURL}');" name="import"/>
        </p>
        <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                        var="facetSet" items="${sortedFacetSets}"
                        sort="${sortValue}" onSort="tableOnSort">
          <admin-ui:column title="facetsets_general.table.name" type="sortable" name="name">
            <c:url var="facetsetUrl" value="${facetPath}/facetset.jsp"/>
            <d:a href="${facetsetUrl}" onclick="return loadRightPanel(this.href);">
              <d:param name="facetSetId" value="${facetSet.id}"/>
              <c:out value="${facetSet.name}"/>
            </d:a>
          </admin-ui:column>
          <admin-ui:column title="facetsets_general.table.description" type="sortable" name="description">
            <c:out value="${facetSet.description}"/>
          </admin-ui:column>
          <admin-ui:column title="facetsets_general.table.last.modified" type="sortable" name="modified">
            <fmt:message var="timeFormat" key="timeFormat"/>
            <fmt:formatDate value="${facetSet.lastModified}" type="both" pattern="${timeFormat}"/>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="facetsets_general.table.copy" var="copyTitle"/>
            <a class="icon propertyCopy" title="${copyTitle}" href="#"
                 onclick="return submitFacetSetsForm(${facetSet.id}, 'copyInput');">copy</a>
            <d:input type="hidden" bean="RefConfigFormHandler.refConfigId"
                     value="${facetSet.id}" id="refConfigId" name="refConfigId"/>
            <d:input bean="RefConfigFormHandler.copy" iclass="formsubmitter"
                     value="field mode" type="submit"
                     id="copyInput" style="display:none" name="copyInput"/>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="facetsets_general.table.export" var="exportTitle"/>
            <c:url var="exportUrl" value="${facetPath}/facetset_export.jsp"/>
            <d:a iclass="icon propertyExport" onclick="return loadRightPanel(this.href);" title="${exportTitle}" href="${exportUrl}">
              <d:param name="facetSetId" value="${facetSet.id}"/>
              exp
            </d:a>
          </admin-ui:column>
          <admin-ui:column type="icon">
            <fmt:message key="facetsets_general.table.delete" var="deleteTitle"/>
            <c:url value="${facetPath}/facetset_delete_popup.jsp" var="popUrl">
              <c:param name="facetSetId" value="${facetSet.id}"/>
            </c:url>
            <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
               onclick="return showPopUp(this.href);">del</a>
          </admin-ui:column>
        </admin-ui:table>
        <d:input bean="RefConfigFormHandler.sort" type="submit" style="display:none" id="sortInput" name="sortInput" iclass="formsubmitter"/>
        <d:input bean="RefConfigFormHandler.successURL"   type="hidden" value="${successURL}" name="successURL"/>
        <d:input bean="RefConfigFormHandler.errorURL"     type="hidden" value="${successURL}" name="errorURL"/>
      </d:form>
    </div>
  </div>

  <script type="text/javascript">
  function tableOnSort(tableId, columnName, sortDirection) {
    var sortButton = document.getElementById('sortInput');
    sortButton.value = columnName + " " + sortDirection;
    sortButton.click();
  }
  function submitFacetSetsForm(id, mode) {
    document.getElementById('refConfigId').value = id;
    document.getElementById(mode).click();
    return false;
  }
  </script>
  <script type="text/javascript">
    //dojo tree refresh
    top.hierarchy = [{id:"rootFacetSetNode"}];
    top.syncTree();
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetsets_general.jsp#2 $$Change: 651448 $--%>
