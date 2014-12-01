<%--
  JSP, showing "general" tab of update facet set page. This page is included from facetset.jsp page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_general.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean bean="/atg/searchadmin/workbenchui/formhandlers/ManageFacetSetFormHandler" var="handler"/>
  <d:getvalueof bean="ManageFacetSetFormHandler.facetSetId" var="facetSetId"/>
  <facets:facetSetFindByPrimaryKey facetSetId="${facetSetId}" var="facetSet"/>

  <h3>
    <fmt:message key="facetset_basics.title"/>
  </h3>
  <table class="form" cellspacing="0" cellpadding="0">
    <tr>
      <td class="label">
        <label for="facetSetName">
          <span id="facetSetNameAlert"></span>
          <fmt:message key="facetset_basics.name"/>
        </label>
      </td>
      <td>
        <d:input type="text" iclass="textField" bean="ManageFacetSetFormHandler.facetSetName"
                 name="facetSetName" id="facetSetName"/>
      </td>
    </tr>
    <tr>
      <td class="label">
        <label for="facetSetDescription">
          <span id="facetSetDescriptionAlert"></span>
          <fmt:message key="facetset_basics.description"/>
        </label>
      </td>
      <td>
        <d:input type="text" iclass="textField" bean="ManageFacetSetFormHandler.facetSetDescription"
                 name="facetSetDescription" id="facetSetDescription" />
      </td>
    </tr>
    <tr>
      <td class="label">
        <label for="facetSetMappings">
          <span id="facetSetMappingsAlert"></span><fmt:message key="facetset_basics.mappings"/>
        </label>
      </td>
      <td style="padding:0">
        <d:getvalueof bean="ManageFacetSetFormHandler.facetSetMappings" var="mappings"/>

        <table id="mappingFacetTable" cellspacing="0" cellpadding="0" width="100%">
          <c:forEach items="${mappings}" var="mapping">
            <tr>
              <td class="">
                <d:input bean="ManageFacetSetFormHandler.facetSetMappings" type="text" name="topicName" value="${mapping}"
                         onkeyup="addEmptyField(this);" onchange="addEmptyField(this);" iclass="textField" />
              </td>
            </tr>
          </c:forEach>
        </table>
      </td>
    </tr>
  </table>
  <script type="text/javascript">
    initTable(document.getElementById("mappingFacetTable"));
  </script>

  <facets:facetSetFindByPrimaryKey facetSetId="${facetSetId}" var="facetSet"/>
  <h3>
    <fmt:message key="facetset.top.level.facets">
      <fmt:param value="${fn:length(facetSet.childFacets)}" />
    </fmt:message>
  </h3>

  <%-- Add new facet button --%>
  <p>
    <c:url value="${facetPath}/facet_new.jsp" var="addNewFacetURL">
      <c:param name="baseFacetId" value="${facetSetId}"/>
    </c:url>
    <tags:buttonLink titleKey="facetset.button.add.new.facet" tooltipKey="facetset.button.tooltip.add.new.facet"
                     href="${addNewFacetURL}"/>
  </p>

  <%-- Define URL after copying--%>
  <c:url value="${facetPath}/facetset.jsp" var="backURL">
    <c:param name="facetSetId" value="${facetSetId}" />
  </c:url>

  <d:getvalueof bean="/atg/searchadmin/adminui/sort/ComparatorFactory.facetComparator" var="comparator"/>
  <admin-ui:sort var="facets" items="${facetSet.childFacets}" comparator="${comparator}" sortMode="undefined"/>

  <admin-ui:table renderer="/templates/table_simple.jsp" modelVar="table"
                  var="facet" items="${facets}" >
    <admin-ui:column title="table_readonly.facet.name" name="name" type="static">
      <c:url var="facetUrl" value="${facetPath}/facet.jsp"/>
      <d:a href="${facetUrl}" onclick="return loadRightPanel(this.href);">
        <d:param name="facetId" value="${facet.id}"/>
        <c:out value="${facet.name}" />
      </d:a>
    </admin-ui:column>
    <admin-ui:column title="table_readonly.facet.property" name="description" type="static">
      <c:out value="${facet.property}" />
    </admin-ui:column>
    <admin-ui:column title="table_readonly.facet.property.type" name="mappings" type="static">
      ${facet.propertyType}
    </admin-ui:column>
    <admin-ui:column type="icon">
      <fmt:message key="facet_table.copy" var="copyTitle"/>
      <a class="icon propertyCopy" title="${copyTitle}" href="#"
         onclick="return submitFacetsForm(${facet.id}, 'copyInput');">copy</a>
      <d:input type="hidden" bean="ManageFacetSetFormHandler.refConfigId"
               value="${facet.id}" id="refConfigId" name="refConfigId"/>
      <d:input bean="ManageFacetSetFormHandler.copyFacet"
               value="field mode" type="submit" iclass="formsubmitter"
               id="copyInput" style="display:none" name="copyInput"/>
      <d:input type="hidden" bean="ManageFacetSetFormHandler.successURL"
               value="${backURL}" id="successURL" name="successURL"/>
      <d:input type="hidden" bean="ManageFacetSetFormHandler.errorURL"
               value="${backURL}" id="errorURL" name="errorURL"/>
    </admin-ui:column>
    <admin-ui:column type="icon">
      <fmt:message key="facet_table.delete" var="deleteTitle"/>
      <c:url value="${facetPath}/facet_delete_popup.jsp" var="popUrl">
        <c:param name="facetId" value="${facet.id}"/>
      </c:url>
      <a class="icon propertyDelete" title="${deleteTitle}" href="${popUrl}"
         onclick="return showPopUp(this.href);">del</a>
    </admin-ui:column>
  </admin-ui:table>
  <script type="text/javascript">
    function submitFacetsForm(id, mode) {
      document.getElementById('refConfigId').value = id;
      document.getElementById(mode).click();
      return false;
    }
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facetset_general.jsp#2 $$Change: 651448 $--%>
