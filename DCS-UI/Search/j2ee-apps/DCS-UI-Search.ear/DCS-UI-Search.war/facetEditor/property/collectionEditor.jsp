<%--
  Facet sub-property editor for array, list, map, and set values.

  The following request-scoped variables are expected to be set:
  
  @param  view         A MappedItemView for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/collectionEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="dcsui-search" uri="http://www.atg.com/taglibs/dcsui-srch"          %>

<dspel:page>
  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="formHandler" scope="request" value="${requestScope.formHandler}"/>
  <dspel:getvalueof var="property"             param="property" />
  <dspel:getvalueof var="subProperty"          param="subProperty" />
  <dspel:getvalueof var="subPropertyType"      param="subPropertyType" />
  <dspel:getvalueof var="hideComponentReorder" param="hideComponentReorder" />
  <c:set var="mpv" scope="request" value="${requestScope.view.propertyMappings[property]}"/>

  <c:choose>
    <c:when test="${not empty subProperty}">
      <biz:getItemMapping var="mappedIv" 
                          mode="edit" 
                          readOnlyMode="view"
                          showExpert="true"
                          itemName="${property}"
                          itemPath="/atg/search/repository/RefinementRepository"/>

      <c:forEach items="${mappedIv.viewMappings}" var="miv">
        <c:set var="mappedPv" value="${miv.propertyMappings[subProperty]}"/>
          <c:if test="${!empty mappedPv}">
            <c:set var="propertyView" scope="request" value="${mappedPv}"/>
          </c:if>
      </c:forEach>
    </c:when>
    <c:otherwise>
      <c:set var="propertyView" scope="request" value="${requestScope.mpv}"/>
    </c:otherwise>
  </c:choose>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- determine which mode we're in --%>
  <c:choose>
    <c:when test="${requestScope.view.itemMapping.mode == 'AssetManager.edit'}">
      <c:set var="disabled" value="false"/>
    </c:when>
    <c:otherwise>
      <c:set var="disabled" value="true"/>
    </c:otherwise>
  </c:choose>

  <%-- Get the collection type for this property --%>
  <%-- TODO: don't hardcode this --%>
  <%-- <c:set var="collectionType" value="list"/> --%>
  <c:set var="collectionType" value="${propertyView.propertyDescriptor.typeName}"/>
  <c:set var="isMap"  value="${collectionType == 'map'}"/>
  <c:set var="isSet"  value="${collectionType == 'set'}"/>
  <c:set var="isList" value="${not isMap and not isSet}"/>
  
  <%-- Determine if this is a collection of RepositoryItems --%>
  <c:set var="containsRepositoryItems" value="true"/>

  <%-- Determine the full bean path of the property being edited 
  <c:set var="beanProp" value="${propertyView.formHandlerProperty}"/>
  --%>
  <%-- TODO: don't hardcode this --%>
  <c:choose>
    <c:when test="${not empty subProperty}">
      <c:set var="beanProp" scope="request" value="${formHandlerPath}.value.${property}.${subProperty}"/>
    </c:when>
    <c:otherwise>
      <c:set var="beanProp" scope="request" value="${formHandlerPath}.value.${property}"/>
    </c:otherwise>
  </c:choose>

  <%-- Determine the names of the "bean" and "beanvalue" attributes for the
       hidden dspel:input fields that will manipulate the values of the
       elements in this collection. --%>
  <c:choose>
    <c:when test="${isMap}">
      <c:choose>
        <c:when test="${containsRepositoryItems}">
          <c:set var="beanSubProp"      value="${beanProp}.keysAndRepositoryIds"/>
          <c:set var="beanValueSubProp" value="${beanProp}.keysAndRepositoryIds"/>
        </c:when>
        <c:otherwise>
          <c:set var="beanSubProp"      value="${beanProp}.keysAndValues"/>
          <c:set var="beanValueSubProp" value="${beanProp}.keysAndValues"/>
        </c:otherwise>
      </c:choose>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${containsRepositoryItems}">
          <c:set var="beanSubProp"      value="${beanProp}.repositoryIds"/>
          <c:set var="beanValueSubProp" value="${beanProp}.repositoryIds"/>
        </c:when>
        <c:otherwise>
          <c:set var="beanSubProp"      value="${beanProp}"/>
          <c:set var="beanValueSubProp" value="${beanProp}.values"/>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>

  <%-- Get the number of elements in the current property value --%>
  <dspel:getvalueof var="values" bean="${beanProp}"/>
  <web-ui:collectionPropertySize var="numElements" collection="${values}"/>

  <%-- Get the descriptor for this property's item type --%>
  <c:set var="itemDescriptor" value="${propertyView.componentRepositoryItemDescriptor}"/>

  <c:if test="${'refineElement' eq itemDescriptor.itemDescriptorName}">
    <dcsui-search:reorderFacetByPriority collection="${values}" />
  </c:if>

  <%-- TODO: don't hardcode this --%>
  <%-- Derive IDs for some form elements and data objects 
  <c:set var="id"               value="refineElements"/>
  --%>
  <c:set var="id"               value="${propertyView.uniqueId}"/>
  
  <%--
  <c:if test="${not empty requestScope.uniqueAssetID}">
    <c:set var="id" value="${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  
  </c:if>
  --%>
  
  <c:set var="tableId"          value="${id}_table"/>
  <c:set var="tbodyId"          value="${id}_tbody"/>
  <c:set var="nullDivId"        value="${id}_nullDiv"/>
  <c:set var="nullSubDivId"     value="${id}_nullSubDiv"/>
  <c:set var="rowIdPrefix"      value="${id}_row_"/>
  <c:set var="dspInputIdPrefix" value="${id}_dspInput_"/>
  <c:set var="keyIdPrefix"      value="${id}_key_"/>
  <c:set var="valueIdPrefix"    value="${id}_val_"/>
  <c:set var="infoObj"          value="${id}_info"/>

  <%-- This value is stored as the "index" in the template HTML, so that it can
       be substituted with the real index when it is copied into the DOM. --%>
  <c:set var="templateRowIndex" value="__INDEX__"/>

  <%-- Define the name of the function that should be called when the "Add New"
       button is clicked.  This is initially null, because we don't include the
       button by default.  Component editor JSPs can set this variable if they
       wish to do so. --%>
  <c:set scope="request" var="addNewFunc" value="${null}"/>
  
  <script type="text/javascript">

    // Create an instance of a CollectionEditor object containing JavaScript
    // methods for manipulating the element table.
    var <c:out value="${infoObj}"/> = new CollectionEditor();

    // Initialize the properties of the CollectionEditor object.
    <c:out value="${infoObj}"/>.tableId           = "<c:out value='${tableId}'/>";
    <c:out value="${infoObj}"/>.tbodyId           = "<c:out value='${tbodyId}'/>";
    <c:out value="${infoObj}"/>.nullDivId         = "<c:out value='${nullDivId}'/>";
    <c:out value="${infoObj}"/>.nullSubDivId      = "<c:out value='${nullSubDivId}'/>";
    <c:out value="${infoObj}"/>.templateRowIndex  = "<c:out value='${templateRowIndex}'/>";
    <c:out value="${infoObj}"/>.rowIdPrefix       = "<c:out value='${rowIdPrefix}'/>";
    <c:out value="${infoObj}"/>.dspInputIdPrefix  = "<c:out value='${dspInputIdPrefix}'/>";
    <c:out value="${infoObj}"/>.keyIdPrefix       = "<c:out value='${keyIdPrefix}'/>";
    <c:out value="${infoObj}"/>.valueIdPrefix     = "<c:out value='${valueIdPrefix}'/>";
    <c:out value="${infoObj}"/>.initiallyEmpty    = <c:out value="${numElements == 0}"/>;
    <c:out value="${infoObj}"/>.isMap             = <c:out value="${isMap}"/>;
    <c:out value="${infoObj}"/>.keyValueSeparator = "<c:out value='${formHandler.mapKeyValueSeparator}'/>";
    <c:out value="${infoObj}"/>.newRowIndex       = <c:out value="${numElements}"/>;

    // This function is called after the containing page has fully loaded, in
    // order to initialize the CollectionEditor object.
    registerOnLoad(function() {
      <c:out value="${infoObj}"/>.initialize();
    });

    // This function is called before the containing form is submitted. It
    // copies the values from the user input fields into the hidden DSP inputs.
    registerOnSubmit(function() {
      <c:out value="${infoObj}"/>.transferUserValues();
    });

  </script>

  <%-- First, allow the component editor JSP to create initial code that is not
       related to any actual array elements (e.g. JavaScript utilities) --%>
  <!-- dspel:include itemComponentEditor.jsp -->
  <dspel:include page="itemComponentEditor.jsp">
    <dspel:param name="initialize"    value="true"/>
    <dspel:param name="valueIdPrefix" value="${valueIdPrefix}"/>
    <dspel:param name="infoObj"       value="${infoObj}"/>
    <dspel:param name="rowIdPrefix"   value="${rowIdPrefix}"/>
  </dspel:include>
  <%-- NB: It's weird that we have to pass in valueIdPrefix here and in the
       loop.  Maybe it should just be request scoped? --%>

  <%-- Create a table that will contain all of the collection elements in
       the current property value. --%>
  <table id="<c:out value='${tableId}'/>" class="formStringList">

    <%-- For a map property, we need to include a table header that indicates
         the key and value fields.  Note that the header must be shown or
         hidden, depending on whether the collection contains elements. --%>
    <c:if test="${isMap}">
      <thead>
        <tr class="header">
          <th class="hidden">
          </th>
          <th class="formValueCell">
            <fmt:message key="collectionEditor.mapKey"/>
          </th>
          <th class="formValueCell">
            <fmt:message key="collectionEditor.mapValue"/>
          </th>
          <th>
            &nbsp;
          </th>
        </tr>
      </thead>
    </c:if>

    <tbody id="<c:out value='${tbodyId}'/>">

      <%-- Loop through all of the elements in the current property value,
           plus an additional element, to be used for rendering the template
           for new rows.  Note that the CollectionEditor initialization
           JavaScript will extract the contents of the template row and then
           remove the row from the table. --%>
      <c:forEach begin="0" end="${numElements}" varStatus="loop">

        <c:set var="index" value="${loop.index}"/>
        
        <%-- The final iteration of this loop is used for rendering the
             row template. --%>
        <c:set var="doTemplate" value="${loop.last}"/>

        <%-- For the row template, use an index string that can be substituted
             with a valid index string when the template is being used to
             create a new row. --%>
        <c:if test="${doTemplate}">
          <c:set var="index" value="${templateRowIndex}"/>
        </c:if>

        <%-- Derive IDs for form elements in this row --%>
        <c:set var="rowId"      value="${rowIdPrefix}${index}"/>
        <c:set var="dspInputId" value="${dspInputIdPrefix}${index}"/>
        <c:set var="keyId"      value="${keyIdPrefix}${index}"/>
        <c:set var="valueId"    value="${valueIdPrefix}${index}"/>

        <%-- Add a new table row for the current element --%>
        <tr id="<c:out value='${rowId}'/>">

          <%-- Add a cell that contains a hidden input which will contain a
               textual representation of the value of the current element.
               This input field will use the DSP interface to communicate with
               the form handler.  It will use JavaScript to interface with the
               visible controls on the form.  For the template row, the input
               field should be empty.  Otherwise, it should contain the current
               value of the element. --%>
          <td class="hidden">
            <c:choose>
              <c:when test="${doTemplate}">
                <dspel:input type="hidden" id="${dspInputId}" iclass="formTextField"
                             bean="${beanSubProp}"
                             value=""/>
              </c:when>
              <c:otherwise>
                <dspel:input type="hidden" id="${dspInputId}" iclass="formTextField"
                             bean="${beanSubProp}"
                             beanvalue="${beanProp}[${index}].repositoryId"/>
              </c:otherwise>
            </c:choose>
          </td>

          <c:if test="${'refineElement' eq itemDescriptor.itemDescriptorName}">
            <td align="right" width="1%">
              <c:choose>
                <c:when test="${doTemplate}">
                    &nbsp;
                </c:when>
                <c:otherwise>
                  <dspel:valueof bean="${beanProp}[${index}].priority"/>
                </c:otherwise>
              </c:choose>
            </td>
          </c:if>

          <%-- For a map property, add a cell with a text field for specifying
               the map key --%>  
          <c:if test="${isMap}">
            <td class="formValueCell">
              <input type="text" id="<c:out value='${keyId}'/>"
                     class="formTextField" value=""/>
            </td>
          </c:if>

          <%-- Allow the component editor JSP to add cell(s) containing user
               input fields --%>
          <!-- dspel:include itemComponentEditor.jsp -->
          <dspel:include page="itemComponentEditor.jsp">
            <dspel:param name="template"      value="${doTemplate}"/>
            <dspel:param name="valueIdPrefix" value="${valueIdPrefix}"/>
            <dspel:param name="index"         value="${index}"/>
            <dspel:param name="valueId"       value="${valueId}"/>
          </dspel:include>

          <c:if test="${disabled == 'false'}">
            <c:if test="${isList and not hideComponentReorder}">
              <%-- "Move Up" button --%>
              <td class="iconCell">
                <a href="<c:out value='javascript:${infoObj}.moveRow("${rowId}", -1)'/>"
                   onclick="markAssetModified()"
                   class="propertyUp" title="<fmt:message key='collectionEditor.moveUp'/>"></a>
              </td>

              <%-- "Move Down" button --%>
              <td class="iconCell">
                <a href="<c:out value='javascript:${infoObj}.moveRow("${rowId}", 1)'/>"
                   onclick="markAssetModified()"
                   class="propertyDown" title="<fmt:message key='collectionEditor.moveDown'/>"></a>
              </td>
            </c:if>

            <%-- "Delete" button --%>
            <td class="iconCell">
              <a href="<c:out value='javascript:${infoObj}.deleteRow("${rowId}")'/>"
                 onclick="markAssetModified()"
                 class="propertyDelete" title="<fmt:message key='collectionEditor.delete'/>"></a>
            </td>
          </c:if>
        </tr>
      </c:forEach>
    </tbody>
  </table>

  <%-- "Add New" button (only if an "Add New" function has been defined by the
       component editor JSP) --%>
  <c:if test="${not empty requestScope.addNewFunc}">
    <c:if test="${disabled == 'false'}">
      <a href="javascript:<c:out value='${requestScope.addNewFunc}'/>()" class="buttonSmall">
        <span>
          <fmt:message key="collectionEditor.addNew"/>
        </span>
      </a>
    </c:if>
  </c:if>

  <%-- When present, the following hidden input causes the property value to be
       set to null.  The "nullSubDivId" div will be removed from the document by
       the CollectionEditor initialization JavaScript if the collection is not
       actually empty.  If the user deletes all of the rows from the table, the
       div will be added back into the document. --%>
  <div id="<c:out value='${nullDivId}'/>">
    <div id="<c:out value='${nullSubDivId}'/>">
      <dspel:input type="hidden" bean="${beanProp}.values" value="null"/>
    </div>
  </div>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/collectionEditor.jsp#2 $$Change: 651448 $--%>
