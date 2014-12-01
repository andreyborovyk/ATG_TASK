<%--
  Page shows "basic" information of facet.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_edit_readonly.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="indexedFacet" var="indexedFacet" />
  <c:set var="sortType" value="${indexedFacet.sortType}"/>
  <c:set var="selectionType" value="${indexedFacet.selectionType}"/>
  <h3>
    <fmt:message key="facet.facet_value_selections"/>
  </h3>
  <table class="form" cellpadding="0" cellspacing="0">
    <%-- Facet Value Display Method --%>
    <tr>
      <td class="label" width="30%">
        <fmt:message key="facet.facet_value_display_method"/>
      </td>
      <td>
      <c:if test="${selectionType ne 'oneSelection'}">
        <fmt:message key="facet.method_ranges" />
      </c:if>
      <c:if test="${selectionType eq 'oneSelection'}">
        <fmt:message key="facet.method_one_selection" />
      </c:if>
    </tr>
    <tr>
      <td class="label">
        <fmt:message key="facet.value_sorting"/>
      </td>
      <td>
        <c:if test="${sortType eq 'numeric'}">
          <fmt:message key="facet.value_sorting.0"/>
        </c:if>
        <c:if test="${sortType eq 'valueCount'}">
          <fmt:message key="facet.value_sorting.1"/>
        </c:if>
        <c:if test="${sortType eq 'rangeOrder'}">
          <fmt:message key="facet.value_sorting.2"/>
        </c:if>
        <c:if test="${sortType eq 'explicitOrder'}">
          <fmt:message key="facet.value_sorting.3"/>
        </c:if>
      </td>
    </tr>
    <c:if test="${selectionType ne 'oneSelection'}">
      <tr>
        <td class="label">
          <fmt:message key="facet.selection_option"/>
        </td>
        <td>
          <d:include page="facet_selections_readonly.jsp">
            <d:param name="indexedFacet" value="${indexedFacet}" />
          </d:include>
        </td>
      </tr>
    </c:if>
    <c:if test="${sortType eq 'explicitOrder'}">
      <tr>
        <td class="label">
          <fmt:message key="facet_selections_specified.specified.title"/>
        </td>
        <td>
          <fmt:message key="facet_selections_specified.specified.message"/>
          <div id="caseSensitiveReadDiv">
            <input type="checkbox" id="caseSensitive" name="caseSensitive" disabled="true"
              <c:if test="${indexedFacet.case}">checked="true"</c:if> />
            <fmt:message key="facet_selections_specified.specified.caseSensitive"/>
          </div>
          <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                          var="specValue" items="${indexedFacet.specifiedFacetValues}">
            <admin-ui:column type="static">
              ${specValue}
            </admin-ui:column>
          </admin-ui:table>    
        </td>
      </tr>
      <tr>
        <td class="label">
          <fmt:message key="facet_selections_specified.excluded.title"/>
        </td>
        <td>
          <fmt:message key="facet_selections_specified.excluded.message"/>
          <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table"
                          var="excludValue" items="${indexedFacet.excludedFacetValues}">
            <admin-ui:column type="static">
              ${excludValue}
            </admin-ui:column>
          </admin-ui:table>
        </td>
      </tr>
    </c:if>
    <%------------- advanced options -----------%>
    <tr>
      <td><h3><fmt:message key="facet_edit.advanced_options.title"/></h3></td>
      <td></td>
    </tr>
    <tr>
      <td colspan="2">
        <table class="form" cellpadding="0" cellspacing="0">
          <tr>
            <td></td>
            <td>
              <label for="<c:out value='filterActive'/>">
                <input type="checkbox" id="filterActive" name="filterActive" disabled="true"
                  <c:if test='${indexedFacet.filter}'>checked="true"</c:if> />
                <fmt:message key="facet_edit.advanced_options.filter_values_active"/>
              </label>
            </td>
          </tr>
          <tr>
            <td nowrap="true" class="label"><fmt:message key="facet_edit.advanced_options.decimal_precision"/></td>
            <td>
              ${indexedFacet.precision}
            </td>
          </tr>
          <tr>
            <td class="label"><fmt:message key="facet_edit.advanced_options.eval_levels"/></td>
            <td>
              ${indexedFacet.levels}
            </td>
          </tr>
          <tr>
            <td class="label"><fmt:message key="facet_edit.advanced_options.mapped_date"/></td>
            <td>
              ${indexedFacet.valuedata}
            </td>
          </tr>
          <tr>
            <td class="label"><fmt:message key="facet_edit.advanced_options.parent_sel"/></td>
            <td>
              ${indexedFacet.select}
            </td>
          </tr>

        </table>
      </td>
    </tr>
  </table>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_edit_readonly.jsp#2 $$Change: 651448 $--%>
