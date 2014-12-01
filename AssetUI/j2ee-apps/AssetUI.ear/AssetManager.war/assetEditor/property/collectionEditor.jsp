<%--
  Default property editor for array, list, map, and set values.

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/collectionEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- Get the collection type for this property --%>
  <c:set var="collectionType" value="${propertyView.propertyDescriptor.typeName}"/>
  <c:set var="isMap"  value="${collectionType == 'map'}"/>
  <c:set var="isSet"  value="${collectionType == 'set'}"/>
  <c:set var="isList" value="${not isMap and not isSet}"/>

  <%-- Determine if this is a collection of RepositoryItems --%>
  <c:set var="componentDescriptor" value="${propertyView.componentRepositoryItemDescriptor}"/>
  <c:set var="containsRepositoryItems" value="${not empty componentDescriptor}"/>

  <%-- Determine the full bean path of the property being edited --%>
  <c:set var="beanProp" value="${propertyView.formHandlerProperty}"/>

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

  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="id" value="${requestScope.atgPropertyViewId}"/>
  <c:set var="tableId"          value="${id}_table"/>
  <c:set var="tbodyId"          value="${id}_tbody"/>
  <c:set var="nullDivId"        value="${id}_nullDiv"/>
  <c:set var="nullSubDivId"     value="${id}_nullSubDiv"/>
  <c:set var="outerDivId"       value="${id}_outerDiv"/>
  <c:set var="addSpanId"        value="${id}_addSpan"/>
  <c:set var="newSpanId"        value="${id}_newSpan"/>
  <c:set var="rowIdPrefix"      value="${id}_row_"/>
  <c:set var="dspInputIdPrefix" value="${id}_dspInput_"/>
  <c:set var="keyIdPrefix"      value="${id}_key_"/>
  <c:set var="locationIdPrefix" value="${id}_location_"/>
  <c:set var="valueIdPrefix"    value="${id}_val_"/>
  <c:set var="deleteIdPrefix"   value="${id}_delete_"/>
  <c:set var="infoObj"          value="${id}_info"/>
  <c:set var="sortSpanId"       value="${id}_sortSpan"/>

  <%-- This value is stored as the "index" in the template HTML, so that it can
       be substituted with the real index when it is copied into the DOM. --%>
  <c:set var="templateRowIndex" value="__INDEX__"/>

  <%-- Define the name of the function that should be called when the "Add New"
       button is clicked.  This is initially null, because we don't include the
       button by default.  Component editor JSPs can set this variable if they
       wish to do so. --%>
  <c:set scope="request" var="addNewFunc" value="${null}"/>

  <%-- This request scope variable allows the component editor JSPs to hide this
       collectionEditor initially--%>
  <c:set scope="request" var="atgCollectionEditorVisibility" value="block"/>

  <%-- These request scope variables allow the component editor JSPs to specify
       a converter and attributes for the hidden DSP input tag --%>
  <c:set scope="request" var="atgCollectionInputConverter" value=""/>
  <c:set scope="request" var="atgCollectionInputConverterAttributes" value=""/>

  <%-- Allow the maximum assignable collection size to be specified using a
       view mapping attribute --%>
  <c:set var="maxCount" value="-1"/>
  <c:if test="${not empty propertyView.attributes.maxNumElements}">
    <c:set var="maxCount" value="${propertyView.attributes.maxNumElements}"/>
  </c:if>

  <script type="text/javascript">

    // Create an instance of a CollectionEditor object containing JavaScript
    // methods for manipulating the element table.
    var <c:out value="${infoObj}"/> = new CollectionEditor();

    // Initialize the properties of the CollectionEditor object.
    <c:out value="${infoObj}"/>.tableId           = "<c:out value='${tableId}'/>";
    <c:out value="${infoObj}"/>.tbodyId           = "<c:out value='${tbodyId}'/>";
    <c:out value="${infoObj}"/>.nullDivId         = "<c:out value='${nullDivId}'/>";
    <c:out value="${infoObj}"/>.nullSubDivId      = "<c:out value='${nullSubDivId}'/>";
    <c:out value="${infoObj}"/>.outerDivId        = "<c:out value='${outerDivId}'/>";
    <c:out value="${infoObj}"/>.addSpanId         = "<c:out value='${addSpanId}'/>";
    <c:out value="${infoObj}"/>.newSpanId         = "<c:out value='${newSpanId}'/>";
    <c:out value="${infoObj}"/>.sortSpanId        = "<c:out value='${sortSpanId}'/>";
    <c:out value="${infoObj}"/>.templateRowIndex  = "<c:out value='${templateRowIndex}'/>";
    <c:out value="${infoObj}"/>.rowIdPrefix       = "<c:out value='${rowIdPrefix}'/>";
    <c:out value="${infoObj}"/>.dspInputIdPrefix  = "<c:out value='${dspInputIdPrefix}'/>";
    <c:out value="${infoObj}"/>.keyIdPrefix       = "<c:out value='${keyIdPrefix}'/>";
    <c:out value="${infoObj}"/>.locationIdPrefix  = "<c:out value='${locationIdPrefix}'/>";
    <c:out value="${infoObj}"/>.valueIdPrefix     = "<c:out value='${valueIdPrefix}'/>";
    <c:out value="${infoObj}"/>.initiallyEmpty    = <c:out value="${numElements == 0}"/>;
    <c:out value="${infoObj}"/>.isMap             = <c:out value="${isMap}"/>;
    <c:out value="${infoObj}"/>.keyValueSeparator = "<c:out value='${formHandler.mapKeyValueSeparator}'/>";
    <c:out value="${infoObj}"/>.newRowIndex       = <c:out value="${numElements}"/>;
    <c:out value="${infoObj}"/>.maxCount          = <c:out value="${maxCount}"/>;
    <c:if test="${propertyView.attributes.useNumericSort == true}">
      <c:out value="${infoObj}"/>.useNumericSort = true;
      <c:out value="${infoObj}"/>.invalidLocationValueMessage = "<fmt:message key='collectionEditor.invalidLocation'/>";
    </c:if>

    <%-- For repository item collections, set more properties in order to provide
         support for component editors that use the asset picker --%>
    <c:if test="${containsRepositoryItems}">

      <%-- Derive a URL for creating a new asset --%>
      <c:url var="createAssetUrl" context="${config.contextRoot}" value="/assetEditor/subTypeSelection.jsp">
        <c:param name="repositoryPathName" value="${componentDescriptor.repository.absoluteName}"/>
        <c:param name="itemDescriptorName" value="${componentDescriptor.itemDescriptorName}"/>
        <c:param name="linkPropertyName"   value="${propertyView.propertyName}"/>
        <c:param name="cancelSubTypeURL"   value="${requestScope.cancelSubTypeURL}"/>
        <c:if test="${isMap}">
          <c:param name="requireMapKey" value="true"/>
        </c:if>
      </c:url>

      <%-- Derive a URL for drilling down into an asset --%>
      <c:url var="drillDownUrl" context="${config.contextRoot}" value="/assetEditor/editAsset.jsp">
        <c:param name="pushContext"      value="true"/>
        <c:param name="linkPropertyName" value="${propertyView.propertyName}"/>
      </c:url>

      <%-- Get the tree component for the current asset type --%>
      <web-ui:getTree var="treePath"
                      repository="${componentDescriptor.repository.absoluteName}"
                      itemType="${componentDescriptor.itemDescriptorName}"
                      treeRegistry="${sessionInfo.treeRegistryPath}"/>

      <%-- Set the extra properties --%>
      <c:out value="${infoObj}"/>.repositoryName = "<c:out value='${componentDescriptor.repository.repositoryName}'/>";
      <c:out value="${infoObj}"/>.itemType       = "<c:out value='${componentDescriptor.itemDescriptorName}'/>";
      <c:out value="${infoObj}"/>.repositoryPath = "<c:out value='${componentDescriptor.repository.absoluteName}'/>";
      <c:out value="${infoObj}"/>.createAssetUrl = "<c:out value='${createAssetUrl}' escapeXml='false'/>";
      <c:out value="${infoObj}"/>.drillDownUrl   = "<c:out value='${drillDownUrl}' escapeXml='false'/>";
      <c:out value="${infoObj}"/>.treeComponent  = "<c:out value='${treePath}'/>";
    </c:if>

    <%-- Set some other asset-picker-related properties for all collection types.
         This is needed by, e.g., the segment list component editor, which uses an
         asset picker to populate a string array. --%>
    <c:out value="${infoObj}"/>.varName        = "<c:out value='${infoObj}'/>";
    <c:out value="${infoObj}"/>.assetPickerUrl = "<c:out value='${config.assetPickerRoot}${config.assetPicker}'/>?apView=1&";
    <fmt:message var="assetPickerTitle" key="propertyEditor.assetPickerTitle"/>
    <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>
    <c:out value="${infoObj}"/>.assetPickerTitle = "<c:out value='${assetPickerTitle}'/>";

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


  <%-- Display the property name. --%>
  <dspel:getvalueof var="informationText" param="informationText"/>
  <c:if test="${propertyView.attributes.fullWidth == 'true'}">
    <div class="formLabelCE">
      <c:out value="${propertyView.propertyDescriptor.displayName}"/>
      <c:if test="${not empty informationText}">
        (<c:out value="${informationText}"/>)
      </c:if>
    </div>
  </c:if>

  <%-- First, allow the component editor JSP to create initial code (e.g.
       JavaScript utilities) not related to any actual array elements --%>
  <c:out value="<!-- Begin component JSP: index=init property=${propertyView.propertyName} contextRoot=${propertyView.componentContextRoot} URI=${propertyView.componentUri} -->"
         escapeXml="false"/>
  <dspel:include otherContext="${propertyView.componentContextRoot}" page="${propertyView.componentUri}">
    <dspel:param name="initialize"       value="true"/>
    <dspel:param name="infoObj"          value="${infoObj}"/>
    <dspel:param name="valueIdPrefix"    value="${valueIdPrefix}"/>
    <dspel:param name="rowIdPrefix"      value="${rowIdPrefix}"/>
    <dspel:param name="keyIdPrefix"      value="${keyIdPrefix}"/>
    <dspel:param name="locationIdPrefix" value="${locationIdPrefix}"/>
    <dspel:param name="deleteIdPrefix"   value="${deleteIdPrefix}"/>
  </dspel:include>
  <c:out value="<!-- End component JSP for index=init -->"
         escapeXml="false"/>

  <%-- Render the entire collection editor inside a div that may have been made
       initially invisible (via a request-scoped variable) by the component
       editor JSP in the call above. --%>
  <div id="<c:out value='${outerDivId}'/>"
       style="display:<c:out value='${requestScope.atgCollectionEditorVisibility}'/>">

    <%-- Create a table that will contain all of the collection elements in
         the current property value. --%>
    <c:set var="collectionTableClass" value="${propertyView.attributes.collectionTableClass}"/>
    <c:if test="${empty collectionTableClass}">
      <c:set var="collectionTableClass" value="formStringList"/>
    </c:if>

    <table id="<c:out value='${tableId}'/>" class="<c:out value='${collectionTableClass}'/>" cellpadding="0" cellspacing="0">

      <%-- For a map property, we need to include a table header that indicates
           the key and value fields.  Note that the header visibility will be
           modified using JavaScript, depending on whether the collection
           contains elements.  Also note that the component editor can render
           the header itself if it wants to. --%>
      <c:if test="${isMap && propertyView.attributes.renderHeaderInComponentEditor != true && empty propertyView.attributes.headerUri}">
        <thead>
          <tr class="header">
            <th class="hidden">
              <%-- Column for hidden inputs --%>
            </th>
            <th class="formValueCell">
              <fmt:message key="collectionEditor.mapKey"/>
            </th>
            <th class="formValueCell">
              <fmt:message key="collectionEditor.mapValue"/>
            </th>
            <th class="formValueCell">
              <%-- Column for Edit button --%>
              &nbsp;
            </th>
            <th class="formValueCell">
              <%-- Column for Delete button --%>
              &nbsp;
            </th>
          </tr>
        </thead>
      </c:if>

      <%-- Allow the component editor to render the header if requested.
           Note that we allow this even for non-map properties. --%>
      <c:if test="${propertyView.attributes.renderHeaderInComponentEditor == true}">
        <c:out value="<!-- Begin component JSP: index=header property=${propertyView.propertyName} contextRoot=${propertyView.componentContextRoot} URI=${propertyView.componentUri} -->"
               escapeXml="false"/>
        <dspel:include otherContext="${propertyView.componentContextRoot}" page="${propertyView.componentUri}">
          <dspel:param name="renderHeader"     value="true"/>
          <dspel:param name="infoObj"          value="${infoObj}"/>
          <dspel:param name="valueIdPrefix"    value="${valueIdPrefix}"/>
          <dspel:param name="rowIdPrefix"      value="${rowIdPrefix}"/>
          <dspel:param name="keyIdPrefix"      value="${keyIdPrefix}"/>
          <dspel:param name="locationIdPrefix" value="${locationIdPrefix}"/>
          <dspel:param name="deleteIdPrefix"   value="${deleteIdPrefix}"/>
        </dspel:include>
        <c:out value="<!-- End component JSP for index=header -->"
               escapeXml="false"/>
      </c:if>

      <%-- A JSP page is provided in viewmapping which renders the header --%>
      <c:if test="${not empty propertyView.attributes.headerUri}">
        <c:out value="<!-- Begin header JSP: index=header contextRoot=${propertyView.attributes.headerUriContextRoot} URI=${propertyView.attributes.headerUri} -->"
               escapeXml="false"/>
        <dspel:include otherContext="${propertyView.attributes.headerUriContextRoot}" page="${propertyView.attributes.headerUri}"/>
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
          <c:set var="locationId" value="${locationIdPrefix}${index}"/>
          <c:set var="valueId"    value="${valueIdPrefix}${index}"/>
          <c:set var="deleteId"   value="${deleteIdPrefix}${index}"/>

          <%-- As a convenience for component property editors, determine the
               display name and URI of the current repository item element --%>
          <c:set var="displayName" value="${null}"/>
          <c:set var="assetURI"    value="${null}"/>
          <c:if test="${containsRepositoryItems and not doTemplate}">
            <c:set var="displayNameProperty"
                   value="${componentDescriptor.itemDisplayNameProperty}"/>
            <c:if test="${not empty displayNameProperty}">
              <dspel:getvalueof var="displayName"
                                bean="${propertyView.formHandlerProperty}[${index}].${displayNameProperty}"/>
            </c:if>
            <c:if test="${empty displayName}">
              <dspel:getvalueof var="displayName"
                                bean="${propertyView.formHandlerProperty}[${index}].repositoryId"/>
            </c:if>
            <dspel:getvalueof var="assetId" bean="${propertyView.formHandlerProperty}.repositoryIds[${index}]"/>
            <asset-ui:createAssetURI var="assetURI"
                                     componentName="${componentDescriptor.repository.repositoryName}"
                                     itemDescriptorName="${componentDescriptor.itemDescriptorName}"
                                     itemId="${assetId}"/>
          </c:if>

          <%-- Add a new table row for the current element --%>
          <c:choose>
            <c:when test="${loop.count % 2 == 0}">
              <tr id="<c:out value='${rowId}'/>" class="altRow">
            </c:when>
            <c:otherwise>
              <tr id="<c:out value='${rowId}'/>">
            </c:otherwise>
          </c:choose>

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
                               converter="${requestScope.atgCollectionInputConverter}"
                               converterattributes="${requestScope.atgCollectionInputConverterAttributes}"
                               value=""/>
                </c:when>
                <c:otherwise>
                  <dspel:input type="hidden" id="${dspInputId}" iclass="formTextField"
                               bean="${beanSubProp}"
                               converter="${requestScope.atgCollectionInputConverter}"
                               converterattributes="${requestScope.atgCollectionInputConverterAttributes}"
                               beanvalue="${beanValueSubProp}[${index}]"/>
                </c:otherwise>
              </c:choose>
            </td>

            <%-- For a map property, add a cell with a text field for specifying
                 the map key (unless the component editor wants to do this) --%>
            <c:if test="${isMap && propertyView.attributes.renderKeyInComponentEditor != true}">
              <td class="formValueCell">
                <input type="text" id="<c:out value='${keyId}'/>"
                       class="formTextField" value=""
                       onpropertychange="formFieldModified()"
                       oninput="markAssetModified()"/>
              </td>
            </c:if>

            <%-- If requested, use numeric fields to specify list element positions --%>
            <c:if test="${propertyView.attributes.useNumericSort == true}">
              <c:set var="displayedIndex" value=""/>
              <c:if test="${not doTemplate}">
                <c:set var="displayedIndex" value="${index + 1}"/>
              </c:if>
              <td class="formValueCell">
                <input type="text" class="formTextField"
                                   id="<c:out value='${locationId}'/>"
                                   maxlength="20"
                                   size="3"
                                   onkeypress="<c:out value='return ${infoObj}.onPositionFieldKeyPress(${index}, event)'/>"
                                   onchange="<c:out value='return ${infoObj}.onPositionFieldChange(${index}, event)'/>"
                                   value="<c:out value='${displayedIndex}'/>"/>
              </td>
            </c:if>

            <%-- Allow the component editor JSP to add cell(s) containing user
                 input fields --%>
            <c:out value="<!-- Begin component JSP: index=${index} property=${propertyView.propertyName} contextRoot=${propertyView.componentContextRoot} URI=${propertyView.componentUri} -->"
                   escapeXml="false"/>
            <dspel:include otherContext="${propertyView.componentContextRoot}" page="${propertyView.componentUri}">
              <dspel:param name="template"       value="${doTemplate}"/>
              <dspel:param name="valueIdPrefix"  value="${valueIdPrefix}"/>
              <dspel:param name="index"          value="${index}"/>
              <dspel:param name="valueId"        value="${valueId}"/>
              <dspel:param name="keyId"          value="${keyId}"/>
              <dspel:param name="locationId"     value="${locationId}"/>
              <dspel:param name="rowId"          value="${rowId}"/>
              <dspel:param name="deleteId"       value="${deleteId}"/>
              <dspel:param name="infoObj"        value="${infoObj}"/>
              <dspel:param name="displayName"    value="${displayName}"/>
              <dspel:param name="assetURI"       value="${assetURI}"/>
              <dspel:param name="collectionType" value="${collectionType}"/>
            </dspel:include>
            <c:out value="<!-- End component JSP for index=${index} -->"
                   escapeXml="false"/>

            <c:if test="${isList && (propertyView.attributes.useNumericSort != true)}">

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
            <c:if test="${propertyView.attributes.hideComponentDelete != true}">
              <td class="iconCell">
                <span id="<c:out value='${deleteId}'/>" >
                  <a href="<c:out value='javascript:${infoObj}.deleteRow("${rowId}")'/>"
                     onclick="markAssetModified()"
                     class="propertyDelete" title="<fmt:message key='collectionEditor.delete'/>"></a>
                </span>
              </td>
            </c:if>

          </tr>
        </c:forEach>
      </tbody>
    </table>

    <%-- Set the initial visiblity of the "Add" buttons depending on if the
         collection size exceeds the maximum (if specified) --%>
    <c:set var="addButtonVisibility" value="inline"/>
    <c:if test="${maxCount != -1 && maxCount <= numElements}">
      <c:set var="addButtonVisibility" value="none"/>
    </c:if>

    <div class="colEditorActions">

    <%-- Render an "Add Existing" button, unless property is cascade delete or
         the button is configured to be hidden --%>
    <c:if test="${not propertyView.propertyDescriptor.cascadeDelete and propertyView.attributes.hideAddExisting != true}">
      <span id="<c:out value='${addSpanId}'/>" style="display:<c:out value='${addButtonVisibility}'/>" >
        <a href="<c:out value='javascript:${infoObj}.addRow()'/>" class="buttonSmall">
          <c:choose>
            <c:when test="${containsRepositoryItems}">
              <fmt:message key="collectionEditor.addExisting"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="collectionEditor.add"/>
            </c:otherwise>
          </c:choose>
        </a>
      </span>
    </c:if>

    <%-- Render an "Add New" button, if an "Add New" function has been defined
         by the component editor JSP, and we are not in multi-edit mode, and the
         button is configured to be present, and the property is not a collection
         of references from a non-versioned item to versioned items. --%>
    <c:if test="${not empty requestScope.addNewFunc and
                  not requestScope.atgIsMultiEditView and
                  propertyView.attributes.hideAddNew != true and
                  not requestScope.atgPropertyDescriptorWrapper.referenceToVersionedItem}">
      <span id="<c:out value='${newSpanId}'/>" style="display:<c:out value='${addButtonVisibility}'/>" >
        <a href="javascript:<c:out value='${requestScope.addNewFunc}'/>()" class="buttonSmall">
          <fmt:message key="collectionEditor.addNew"/>
        </a>
      </span>
    </c:if>

    <%-- Render a Sort button if supported.  Make it initially invisible if the
         collection contains less than two elements. --%>
    <c:if test="${propertyView.attributes.useNumericSort == true || propertyView.attributes.displaySortButton == true}">
      <c:set var="sortButtonVisibility" value="inline"/>
      <c:if test="${numElements <= 1}">
        <c:set var="sortButtonVisibility" value="none"/>
      </c:if>
      <span id="<c:out value='${sortSpanId}'/>" style="display:<c:out value='${sortButtonVisibility}'/>">
        <a href="<c:out value='javascript:${infoObj}.sortRows()'/>"
           onclick="markAssetModified()" class="buttonSmall">
          <span>
            <fmt:message key="collectionEditor.sort"/>
          </span>
        </a>
      </span>
    </c:if>
    </div>
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

  </div>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/collectionEditor.jsp#2 $$Change: 651448 $--%>
