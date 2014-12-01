<%--
  Page, showing "basic" information in general tab on create/edit facet page.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_edit.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:importbean var="facetFormHandler" bean="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"/>
  <h3>
    <fmt:message key="facet.facet_value_selections"/>
  </h3>
  <table class="form" cellpadding="0" cellspacing="0">
    <%---------------- Facet Value Display methods ----------------------------------------%>
    <tr>
      <td class="label">
        <fmt:message key="facet.facet_value_display_method"/>
      </td>
      <td>
        <d:input bean="FacetFormHandler.facetDisplayMethod" id="oneSelection"
                 type="radio" name="facetDisplayMethod" value="oneSelection" onclick="facetSelections.onDisplayMethodChange(this.value);"/>
        <label for="oneSelection"><fmt:message key="facet.method_one_selection"/></label>
        &nbsp;
        <d:input bean="FacetFormHandler.facetDisplayMethod" id="ranges"
                 type="radio" name="facetDisplayMethod" value="ranges" onclick="facetSelections.onDisplayMethodChange(this.value);"/>
        <label for="ranges"><fmt:message key="facet.method_ranges"/></label>
        <br/>&nbsp;
      </td>
    </tr>

    <tr id="rangeOrder1Row" style="display:none">
      <td class="label">
        <fmt:message key="facet.selection_option"/>
      </td>
      <td>
        <%---------------------- Dynamic-range selections  --------------------------------%>
        <d:input bean="FacetFormHandler.selectionOption" id="selectionOption1"
                 type="radio" name="selectionOption" value="free" onclick="facetSelections.onSelectionChange(this.value);"/>
        <label for="selectionOption1"><fmt:message key="facet.selection_option.1"/></label>

        <div id="freeDiv" style="display:none">
          <ul style="margin:0 0 0 40px;">
            <li>
              <fmt:message key="facet.selection_option.1.guidelines.0"/>
              &nbsp;
              <d:input bean="FacetFormHandler.maxNumberSelections" id="maxNumberSelections"
                       iclass="textField number small" type="text" name="maxNumberSelections"/>
            </li>
            <li>
              <fmt:message key="facet.selection_option.1.guidelines.1"/>
              &nbsp;
              <d:input bean="FacetFormHandler.minFacetProperty" id="minFacetProperty"
                       iclass="textField number small" type="text" name="minFacetProperty"/>
            </li>
            <li>
              <fmt:message key="facet.selection_option.1.guidelines.2"/>
              &nbsp;
              <d:input bean="FacetFormHandler.minNumericalSpan" id="minNumericalSpan"
                       iclass="textField number small" type="text" name="minNumericalSpan"/>
            </li>
            <li>
              <fmt:message key="facet.selection_option.1.guidelines.3"/>
              &nbsp;
              <d:input bean="FacetFormHandler.roundSelection" id="roundSelection"
                       iclass="textField number small" type="text" name="roundSelection"/>
            </li>
          </ul>
        </div>
      </td>
    </tr>
    <tr id="rangeOrder2Row" style="display:none">
      <td>&nbsp;</td>
      <td>
          <%---------------------- Fixed-range selections   --------------------------------%>
        <d:input bean="FacetFormHandler.selectionOption" id="selectionOption2"
                 type="radio" name="selectionOption" value="fixed" onclick="facetSelections.onSelectionChange(this.value);"/>
        <label for="selectionOption2"><fmt:message key="facet.selection_option.2"/></label>

        <div id="fixedDiv" style="display:none">
          <ul style="margin:0 0 0 40px;">
            <li>
              <fmt:message key="facet.selection_option.2.guidelines.0"/>
              &nbsp;
              <d:input bean="FacetFormHandler.numberSelections" id="numberSelections"
                       iclass="textField number small" type="text" name="numberSelections"/>
            </li>
            <li>
              <fmt:message key="facet.selection_option.2.guidelines.1"/>
              &nbsp;
              <d:input bean="FacetFormHandler.numericalSpan" id="numericalSpan"
                       iclass="textField number small" type="text" name="numericalSpan"/>
            </li>
          </ul>
        </div>
      </td>
    </tr>
    <tr id="rangeOrder3Row" style="display:none">
      <td>&nbsp;</td>
      <td>
          <%---------------------- Specified selection ranges  --------------------------------%>
        <d:input bean="FacetFormHandler.selectionOption" id="selectionOption3"
                 type="radio" name="selectionOption" value="explicit" onclick="facetSelections.onSelectionChange(this.value);"/>
        <label for="selectionOption3"><fmt:message key="facet.selection_option.3"/></label>

        <div id="explicitDiv" style="display:none">
          <d:getvalueof bean="FacetFormHandler.specifiedFacetRangesAsList" var="specifiedFacetRanges"/>
          <ul style="margin:0 0 0 40px;">
            <li>
              <div id="addRangePointDiv" style="display:none;">
                <d:input type="hidden" bean="FacetFormHandler.rangePointRefreshUrl" value="${facetPath}/range_points.jsp" />
                <d:input id="sortRangePointsSubmit" style="display:none" bean="FacetFormHandler.sortRangePoints" type="submit" />
                <d:input id="deleteRangePointIndexHidden" type="hidden" bean="FacetFormHandler.deleteRangePointIndex" value="0" />
                <d:input id="deleteRangePointSubmit" style="display:none" bean="FacetFormHandler.deleteRangePoint" type="submit" />
                <d:input id="rangePointInput" type="text" bean="FacetFormHandler.rangePoint" iclass="textField number small" 
                         oninput="facetSelections.disableAddButton(this);"
                         onpropertychange="facetSelections.disableAddButton(this);"
                         onchange="facetSelections.disableAddButton(this);"/>
                <fmt:message var="addRangePointButtonTitle" key='facet.add_range_point.button'/>
                <d:input id="addRangePointSubmit" bean="FacetFormHandler.addRangePoint" type="submit" disabled="true"
                         value="${addRangePointButtonTitle}" title="${addRangePointButtonTitle}" />&nbsp;
                <d:getvalueof bean="FacetFormHandler.specifiedFacetRangesAsList" var="specifiedFacetRanges"/>
                <div id="rangePointsTableDiv"></div>
                <script>
                  facetSelections.initRangePoints(
                      <d:include page="range_points.jsp">
                        <d:param name="addMessages" value="no" />
                      </d:include>);
                </script>
                <%-- !Important! don't remove --%>
                <tags:setHiddenField name="specifiedFacetRanges" beanName="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"/>
              </div>
            </li>
          </ul>
        </div>
        <br/>&nbsp;
      </td>
    </tr>

    <tr>
      <td class="label">
        <fmt:message key="facet.value_sorting"/>
      </td>
      <td>
        <d:input bean="FacetFormHandler.facetSorting" id="facetSorting0"
                 type="radio" name="facetSorting" value="numeric" onclick="facetSelections.onSortingChange(this.value);"/>
        <label for="facetSorting0"><fmt:message key="facet.value_sorting.0"/></label>
      </td>
    </tr>
    <tr>
      <td class="label">&nbsp;</td>
      <td>
        <d:input bean="FacetFormHandler.facetSorting" id="facetSorting1"
                 type="radio" name="facetSorting" value="valueCount" onclick="facetSelections.onSortingChange(this.value);"/>
        <label for="facetSorting1"><fmt:message key="facet.value_sorting.1"/></label>
      </td>
    </tr>
    <tr id="rangeSortingDiv" style="display:none">
      <td class="label">&nbsp;</td>
      <td>
        <d:input bean="FacetFormHandler.facetSorting" id="facetSorting2"
                 type="radio" name="facetSorting" value="rangeOrder" onclick="facetSelections.onSortingChange(this.value);"/>
        <label for="facetSorting2"><fmt:message key="facet.value_sorting.2"/></label>
      </td>
    </tr>
    <tr id="explicitSortingDiv">
      <td class="label">&nbsp;</td>
      <td>
        <d:input bean="FacetFormHandler.facetSorting" id="facetSorting3"
                 type="radio" name="facetSorting" value="explicitOrder" onclick="facetSelections.onSortingChange(this.value);"/>
        <label for="facetSorting3"><fmt:message key="facet.value_sorting.3"/></label>
      </td>
    </tr>

    <tr id="specifiedValuesRow" style="display:none">
      <td class="label"><fmt:message key="facet_selections_specified.specified.title"/></td>
      <td>
        <fmt:message key="facet_selections_specified.specified.message"/>
        <div id="caseSensitiveDiv" style="display:none">
          <br/>
          <d:input type="checkbox" bean="FacetFormHandler.caseSensitive" id="caseSensitive" name="caseSensitive"/>
          <label for="caseSensitive"><fmt:message key="facet_selections_specified.specified.caseSensitive"/></label>
        </div>
        <br/>
        <d:getvalueof bean="FacetFormHandler.specifiedValues" var="facetSpecifiedValues"/>
        <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table" var="specValue"
                        tableId="specifiedValuesTable" items="${facetSpecifiedValues}" emptyRow="true">
          <admin-ui:column type="static">
            <d:input bean="FacetFormHandler.specifiedFacetValue" value="${specValue}" type="text"
                     name="specifiedFacetValue"  iclass="synChanged"
                     onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
          </admin-ui:column>
          <admin-ui:column type="icon" width="20">
            <fmt:message key="facet_selections_specified.table.specified.moveup" var="moveupTitle"/>
            <a class="icon propertyUp" title="${moveupTitle}" href="#" onclick="return moveField(this,true);">up</a>
          </admin-ui:column>
          <admin-ui:column type="icon" width="20">
            <fmt:message key="facet_selections_specified.table.specified.movedown" var="movedownTitle"/>
            <a class="icon propertyDown" title="${movedownTitle}" href="#" onclick="return moveField(this,false);">down</a>
          </admin-ui:column>
          <admin-ui:column type="icon" width="20">
            <fmt:message key="facet_selections_specified.table.specified.delete" var="deleteTitle"/>
            <a class="icon propertyDelete" title="${deleteTitle}" href="#" onclick="return deleteField(this);">del</a>
          </admin-ui:column>
        </admin-ui:table>
      </td>
    </tr>

    <tr id="excludedValuesRow" style="display:none">
      <td class="label"><fmt:message key="facet_selections_specified.excluded.title"/></td>
      <td>
        <fmt:message key="facet_selections_specified.excluded.message"/>
        <br/>
        <d:getvalueof bean="FacetFormHandler.excludedValues" var="facetExcludedValues"/>
        <admin-ui:table renderer="/templates/table_data.jsp" modelVar="table" var="excludValue"
                        tableId="excludedValuesTable" items="${facetExcludedValues}" emptyRow="true">
          <admin-ui:column type="static">
            <d:input bean="FacetFormHandler.excludedFacetValue" value="${excludValue}"
                      name="excludedFacetValue" type="text" iclass="synChanged"
                      onkeyup="addEmptyField(this);" onchange="addEmptyField(this);"/>
          </admin-ui:column>
          <admin-ui:column type="icon" width="20">
            <fmt:message key="facet_selections_specified.table.excluded.moveup" var="moveupTitle"/>
            <a class="icon propertyUp" title="${moveupTitle}" href="#" onclick="return moveField(this,true);">up</a>
          </admin-ui:column>
          <admin-ui:column type="icon" width="20">
            <fmt:message key="facet_selections_specified.table.excluded.movedown" var="movedownTitle"/>
            <a class="icon propertyDown" title="${movedownTitle}" href="#" onclick="return moveField(this,false);">down</a>
          </admin-ui:column>
          <admin-ui:column type="icon" width="20">
            <fmt:message key="facet_selections_specified.table.excluded.delete" var="deleteTitle"/>
            <a class="icon propertyDelete" title="${deleteTitle}" href="#" onclick="return deleteField(this);">del</a>
          </admin-ui:column>
        </admin-ui:table>
      </td>
    </tr>
    <%------------- advanced options -----------%>
    <tr>
      <td><h3><fmt:message key="facet_edit.advanced_options.title"/></h3></td>
      <td></td>
    </tr>
    <tr>
      <td></td>
      <td>
        <d:input type="checkbox" bean="FacetFormHandler.filterActive" id="filterActive" name="filterActive"/>
        <label for="filterActive">
          <fmt:message key="facet_edit.advanced_options.filter_values_active"/>
        </label>
      </td>
    </tr>
    <tr>
      <td nowrap="" class="label"><fmt:message key="facet_edit.advanced_options.decimal_precision"/></td>
      <td>
        <d:input bean="FacetFormHandler.facetDecimalPrecision" id="facetDecimalPrecision"
                 type="text" name="facetDecimalPrecision" iclass="textField"/>
      </td>
    </tr>
    <tr>
      <td class="label"><fmt:message key="facet_edit.advanced_options.eval_levels"/></td>
      <td>
        <d:input bean="FacetFormHandler.evaluationLevels" id="evaluationLevels"
                 type="text" name="evaluationLevels" iclass="textField"/>
      </td>
    </tr>
    <tr>
      <td class="label"><fmt:message key="facet_edit.advanced_options.mapped_date"/></td>
      <td>
        <d:input bean="FacetFormHandler.mappedData" id="mappedData"
                 type="text" name="mappedData" iclass="textField"/>
      </td>
    </tr>
    <tr>
      <td class="label"><fmt:message key="facet_edit.advanced_options.parent_sel"/></td>
      <td>
        <d:input bean="FacetFormHandler.parentSelection" id="parentSelection"
                 type="text" name="parentSelection" iclass="textField"/>
      </td>
    </tr>
  </table>

  <script type="text/javascript">
    facetSelections.init("${facetFormHandler.facetDisplayMethod}",
        "${facetFormHandler.selectionOption}", "${facetFormHandler.facetSorting}");
  </script>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/facet_edit.jsp#2 $$Change: 651448 $--%>
