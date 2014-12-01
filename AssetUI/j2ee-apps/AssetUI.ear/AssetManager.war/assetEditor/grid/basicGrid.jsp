<%--

  Basic grid widget for displaying elements in a collection. Note the actual
  rendering of individual elements is handled by a component JSP page that
  knows how to handle items of a specific type (e.g. repository items,
  strings, etc.)

  The following parameters are required to set up the grid:
  @param componentContextRoot    The context root of a component JSP page.
  @param componentURI            The URI of a component JSP page.
  @param modelPageSize           The number of rows retrieved at a time by the grid.
  @param keepRows                The number of rows in the grid rendering cache.
  @param useColumnHeaders        Control use of column headers.
  @param numElements             The number of elements in the grid.
  @param containsRepositoryItems Indicates if the collection contains repository items.
  @param uniqueId                A unique identifier for the collection that will
                                 be used to construct Javascript and other identifiers.

  Some optional parameters:
  @param showIdColumn       Indicates whether an ID column should be displayed.
  @param idColumnWidth      Width of the ID column in pixels.
  @param isListCollection   Is the collection a list or not.

  The following parameters control the editability of the collection. You can either
  set allowEdit once or set individual flags to control each operation separately:
  @param allowEdit          Specifies whether the collection can be edited in any way.
  @param allowDelete        Specifies whether the collection supports delete.
  @param allowReorder       Specifies whether the collection supports reorder.
  @param hideAddNew         Specifies whether the Add New button should be hidden.
  @param hideAddExisting    Specifies whether the Add Existing button should be hidden.

  The following parameters are passed to the component editor to access the collection:
  @param collectionAdapter  The path to a CollectionAdapter that can modify the collection (optional).
  @param collectionBeanProp The full bean path to the collection (required).
  @param elementBeanProp    The full bean path to individual elements in the collection (optional).
  @param containsURI        Indicates whether collectionBeanProp contains URIs instead
                            of items or values (by default we assume the latter).

  The following are used for item drilldown. Either drilldownURL or drilldownFunction
  is required for repository item collections:
  @param drilldownURL       URL that can be used to drilldown to individual items.
  @param drilldownFunction  Javascript function that can be used to drilldown.

  The following is used to create new component items. This is required for
  repository item collections:
  @param createAssetURL     URL that can be used to create new items.
  --%>

<%@ taglib prefix="c"         uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"       uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"    uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%@ include file="/multiEdit/multiEditConstants.jspf" %>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramComponentContextRoot"    param="componentContextRoot"/>
  <dspel:getvalueof var="paramComponentURI"            param="componentURI"/>
  <dspel:getvalueof var="paramModelPageSize"           param="modelPageSize"/>
  <dspel:getvalueof var="paramKeepRows"                param="keepRows"/>
  <dspel:getvalueof var="paramUseColumnHeaders"        param="useColumnHeaders"/>
  <dspel:getvalueof var="paramNumElements"             param="numElements"/>
  <dspel:getvalueof var="paramUniqueId"                param="uniqueId"/>
  <dspel:getvalueof var="paramContainsRepositoryItems" param="containsRepositoryItems"/>
  <dspel:getvalueof var="paramShowIdColumn"            param="showIdColumn"/>
  <dspel:getvalueof var="paramIdColumnWidth"           param="idColumnWidth"/>
  <dspel:getvalueof var="paramIsList"                  param="isListCollection"/>

  <dspel:getvalueof var="paramAllowEdit"            param="allowEdit"/>
  <dspel:getvalueof var="paramAllowDelete"          param="allowDelete"/>
  <dspel:getvalueof var="paramAllowReorder"         param="allowReorder"/>
  <dspel:getvalueof var="paramHideAddNew"           param="hideAddNew"/>
  <dspel:getvalueof var="paramHideAddExisting"      param="hideAddExisting"/>

  <dspel:getvalueof var="paramCollectionAdapter"    param="collectionAdapter"/>
  <dspel:getvalueof var="paramCollectionBeanProp"   param="collectionBeanProp"/>
  <dspel:getvalueof var="paramElementBeanProp"      param="elementBeanProp"/>
  <dspel:getvalueof var="paramContainsURI"          param="containsURI"/>
  <dspel:getvalueof var="paramDrilldownURL"         param="drilldownURL"/>
  <dspel:getvalueof var="paramDrilldownFunction"    param="drilldownFunction"/>
  <dspel:getvalueof var="paramCreateAssetURL"       param="createAssetURL"/>
 
  <dspel:getvalueof var="paramOnStyleRowFunction"           param="onStyleRowFunction"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- This is the URL issued to retrieve and manipulate grid data. --%>
  <c:url context="${paramComponentContextRoot}" value="${paramComponentURI}" var="componentURL">
    <c:param name="collectionAdapter" value="${paramCollectionAdapter}"/>
    <c:param name="collectionBeanProp" value="${paramCollectionBeanProp}"/>
    <c:param name="elementBeanProp" value="${paramElementBeanProp}"/>
    <c:param name="containsURI" value="${paramContainsURI}"/>
    <c:param name="showIdColumn" value="${paramShowIdColumn}"/>
  </c:url>

  <%-- Derive IDs for some Javascript objects and page elements --%>
  <c:set var="editorObj" value="atg.assetmanager.${paramUniqueId}Editor"/>
  <c:set var="modelObj" value="atg.assetmanager.${paramUniqueId}Model"/>
  <c:set var="layoutObj" value="atg.assetmanager.${paramUniqueId}Layout"/>
  <c:set var="gridObj" value="atg_assetmanager_${paramUniqueId}_grid"/>
  <c:set var="buttonToolbarId" value="atg_assetmanager_${paramUniqueId}_buttonToolbarId"/>
  <c:set var="addExistingSpanId" value="atg_assetmanager_${paramUniqueId}_addExistingSpanId"/>
  <c:set var="addNewSpanId" value="atg_assetmanager_${paramUniqueId}_addNewSpanId"/>
  <c:set var="reorderSpanId" value="atg_assetmanager_${paramUniqueId}_reorderSpanId"/>
  <c:set var="numItemsDivId" value="atg_assetmanager_${paramUniqueId}_numItemsDivId"/>
  <c:set var="noItemsAddedDivId" value="atg_assetmanager_${paramUniqueId}_noItemsAddedDivId"/>
  <c:set var="addButtonToolbarId" value="atg_assetmanager_${paramUniqueId}_addButtonToolbarId"/>

  <c:if test="${not empty paramCollectionAdapter}">
    <dspel:getvalueof var="collectionAdapter" bean="${paramCollectionAdapter}"/>
    <c:set target="${collectionAdapter}" property="supportsIsModified" value="true"/>
  </c:if>

  <%-- The name of the collection property.  --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="propertyName" value="${propertyView.propertyName}"/>

  <script type="text/javascript">

    dojo.require("dojox.grid.Grid");
    dojo.require("dojo.parser");

    // Create an instance of the GridEditor object containing JavaScript
    // methods for setting up and manipulating the grid.
    <c:out value="${editorObj}"/> = new atg.assetmanager.GridEditor();

    // Set properties of the GridEditor object.
    <c:out value="${editorObj}"/>.allowReorder =
      <c:out value="${paramAllowReorder == 'true' || paramAllowEdit == 'true'}"/>;
    <c:out value="${editorObj}"/>.allowDelete =
      <c:out value="${paramAllowDelete == 'true' || paramAllowEdit == 'true'}"/>;
    <c:out value="${editorObj}"/>.showAddNew =
      <c:out value="${paramHideAddNew == 'false' || paramAllowEdit == 'true'}"/>;
    <c:out value="${editorObj}"/>.showAddExisting =
      <c:out value="${paramHideAddExisting == 'false' || paramAllowEdit == 'true'}"/>;
    <c:out value="${editorObj}"/>.useColumnHeaders =
      <c:out value="${paramUseColumnHeaders == 'true'}"/>;
    <c:out value="${editorObj}"/>.containsRepItems =
      <c:out value="${paramContainsRepositoryItems == 'true'}"/>;
    <c:out value="${editorObj}"/>.editorObjId = '<c:out value="${editorObj}"/>';
    <c:out value="${editorObj}"/>.gridElementId = '<c:out value="${gridObj}"/>';
    <c:out value="${editorObj}"/>.numItemsDivId = '<c:out value="${numItemsDivId}"/>';
    <c:out value="${editorObj}"/>.noItemsAddedDivId = '<c:out value="${noItemsAddedDivId}"/>';
    <c:out value="${editorObj}"/>.reorderSpanId = '<c:out value="${reorderSpanId}"/>';
    <c:out value="${editorObj}"/>.addNewSpanId = '<c:out value="${addNewSpanId}"/>';
    <c:out value="${editorObj}"/>.addExistingSpanId = '<c:out value="${addExistingSpanId}"/>';
    <c:out value="${editorObj}"/>.addButtonToolbarId = '<c:out value="${addButtonToolbarId}"/>';
    <c:out value="${editorObj}"/>.itemsLabel = '<fmt:message key="collectionEditor.items"/>';
    <c:out value="${editorObj}"/>.itemLabel = '<fmt:message key="collectionEditor.item"/>';
    <c:out value="${editorObj}"/>.reorderLabel = '<fmt:message key="collectionEditor.index"/>';
    <c:out value="${editorObj}"/>.deleteLabel = '<fmt:message key="collectionEditor.delete"/>';
    <c:out value="${editorObj}"/>.deleteIconTooltip = '<fmt:message key="collectionEditor.removeItem"/>';
    <c:out value="${editorObj}"/>.componentColumns = [];
    <c:out value="${editorObj}"/>.createAssetUrl = '<c:out value="${paramCreateAssetURL}" escapeXml='false'/>';
    <c:out value="${editorObj}"/>.drilldownFunc = '<c:out value="${paramDrilldownFunction}"/>';
    <c:out value="${editorObj}"/>.collectionPropertyName = '<c:out value="${propertyName}"/>';
    <c:if test="${not empty paramDrilldownURL}">
      <c:out value="${editorObj}"/>.drilldownURL = '<c:out value="${paramDrilldownURL}" escapeXml='false'/>';
    </c:if>

    <%-- For repository item collections, set more properties in order to provide
         support for component editors that use the asset picker. --%>
    <c:if test="${paramContainsRepositoryItems == 'true' &&
                  not empty paramCollectionAdapter &&
                  (paramHideAddExisting == 'false' || paramAllowEdit == 'true')}">

      <c:set var="sessionInfo" value="${config.sessionInfo}"/>
      <dspel:getvalueof bean="${paramCollectionAdapter}.componentItemRepository"
                        var="itemRepository"/>
      <dspel:getvalueof bean="${paramCollectionAdapter}.componentItemType"
                        var="itemType"/>
      <web-ui:getTree var="treePath"
                      repository="${itemRepository}"
                      itemType="${itemType}"
                      treeRegistry="${sessionInfo.treeRegistryPath}"/>
      <c:set var="assetPickerUrl" value="${config.assetPickerRoot}${config.assetPicker}?apView=1&"/>
      <fmt:message var="assetPickerTitle" key="propertyEditor.assetPickerTitle"/>
      <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>

      <%-- Set the extra properties. --%>
      <c:out value="${editorObj}"/>.itemRepository = '<c:out value="${itemRepository}"/>';
      <c:out value="${editorObj}"/>.itemType = '<c:out value="${itemType}"/>';
      <c:out value="${editorObj}"/>.treeComponent = '<c:out value="${treePath}"/>';
      <c:out value="${editorObj}"/>.assetPickerUrl = '<c:out value="${assetPickerUrl}" escapeXml='false'/>';
      <c:out value="${editorObj}"/>.assetPickerTitle = '<c:out value="${assetPickerTitle}"/>';

    </c:if>

  </script>

  <%-- Allow the component JSP to create initial code, e.g. Javascript that
       extends the GridEditor or any specific styles needed by cells in the
       grid. Note the use of the 'initialize' param. --%>
  <c:out value="<!-- Begin component JSP: index=init contextRoot=${paramComponentContextRoot} URI=${paramComponentURI} -->"
         escapeXml="false"/>
  <dspel:include otherContext="${paramComponentContextRoot}" page="${paramComponentURI}">
    <dspel:param name="initialize"        value="true"/>
    <dspel:param name="editorObj"         value="${editorObj}"/>
    <dspel:param name="showIdColumn"      value="${paramShowIdColumn}"/>
    <dspel:param name="idColumnWidth"     value="${paramIdColumnWidth}"/>
  </dspel:include>
  <c:out value="<!-- End component JSP for index=init -->"
         escapeXml="false"/>

  <script type="text/javascript">

    // Initialize the GridEditor object. This must be done after the component
    // JSP is initialized so that the popup menu functions exist.
    <c:out value="${editorObj}"/>.initialize();

    // Create a data model for the grid.
    <c:out value="${modelObj}"/> =
      new atg.assetmanager.CollectionEditorData('<c:out value="${componentURL}" escapeXml="false"/>');
    <c:out value="${modelObj}"/>.count = <c:out value="${paramNumElements}"/>;
    <c:out value="${modelObj}"/>.rowsPerPage = <c:out value="${paramModelPageSize}"/>;
    <c:out value="${modelObj}"/>.standardErrorMessage = '<fmt:message key="collectionEditor.error.server"/>';
    <c:out value="${modelObj}"/>.duplicatesDetectedMessage = '<fmt:message key="collectionEditor.insertHasDups"/>';
    <c:out value="${editorObj}"/>.setModel(<c:out value="${modelObj}"/>);

    // Create a layout object.
    <c:out value="${layoutObj}"/> =
      <c:out value="${editorObj}"/>.constructGridLayout();

    // Stuff to do once the page and grid are loaded.
    dojo.addOnLoad(function() {

      // Update the Item Count label and other controls as appropriate.
      <c:out value="${editorObj}"/>.updateEditor(<c:out value="${paramNumElements}"/>);

      // Set the grid parameters.
      <c:out value="${gridObj}"/>.keepRows = <c:out value="${paramKeepRows}"/>;
      <c:out value="${gridObj}"/>.rowsPerPage = <c:out value="${paramModelPageSize}"/>;

      // Extend the grid to provide row highlighting.
      <c:out value="${gridObj}"/>.highlightRows =
          function(pIndexes, pShowHighlight) {
            dojo.forEach(pIndexes, function(pIndex) {
              var rowNode = this.views.views[0].getRowNode(pIndex);
              if (rowNode != null) {
                if (pShowHighlight) {
                  dojo.addClass(rowNode, 'changedRow');
                }
                else {
                  dojo.removeClass(rowNode, 'changedRow');
                }
              }
            }, this);
          };

      // Restore any previously stored index if appropriate.
      <c:if test="${paramIsList && not empty requestScope.currentListIndex &&
                    not empty propertyName &&
                    propertyName == requestScope.currentListIndexPropertyName}">
        <c:out value="${gridObj}"/>.scrollToRow(<c:out value="${requestScope.currentListIndex}"/>);
      </c:if>

      // Sparkle a new item that was added to the list, if any.
      <c:if test="${paramIsList && not empty requestScope.listInsertIndex &&
                    not empty propertyName &&
                    propertyName == requestScope.listInsertPropertyName}">
        <c:out value="${gridObj}"/>.scrollToRow(<c:out value="${requestScope.listInsertIndex}"/>);
        setTimeout(dojo.hitch(<c:out value="${editorObj}"/>, "showNewRows", <c:out value="${requestScope.listInsertIndex}"/>, 1), 500);
      </c:if>

      // Track window resize events so we can resize the grid.
      <c:out value="${editorObj}"/>._resizeHandler =
        dojo.connect(window,
                    "resize",
                    dojo.hitch(dijit.byId('<c:out value="${gridObj}"/>'), "update"));
    });

    dojo.addOnUnload(function() {
      dojo.disconnect(<c:out value="${editorObj}"/>._resizeHandler);
    });

  </script>

  <div id="<c:out value='${numItemsDivId}'/>" class="ceNumItems" style="display:none">
  </div>

  <div id="<c:out value='${noItemsAddedDivId}'/>" style="display:none" class="noItemsAdded">
    <fmt:message key="collectionEditor.noItemsAdded"/>
  </div>

  <%-- Create the grid inside of a DIV. --%>
  <c:set var="autoHeightValue" value="false"/>
  <c:if test="${requestScope.atgIsMultiEditView and
                requestScope.multiEditOperation == OPERATION_APPLY_TO_ALL}">
    <c:set var="autoHeightValue" value="true"/>
  </c:if>

  <div class="ceGrid"
       id="<c:out value='${gridObj}'/>"
       jsid="<c:out value='${gridObj}'/>"
       dojoType="dojox.Grid"
       model="<c:out value="${modelObj}"/>"
       structure="<c:out value="${layoutObj}"/>"
       autoHeight="<c:out value="${autoHeightValue}"/>"
       onMouseOverRow="atg.noop()"
       onRowClick="atg.noop()"
       onCellClick="atg.noop()"
       onKeyDown="atg.noop()"
       <c:if test="${not empty paramOnStyleRowFunction}">
         onStyleRow="<c:out value='${paramOnStyleRowFunction}'/>"
       </c:if>
       >
  </div>
  <br\>&nbsp;<br\>

  <div id="<c:out value='${buttonToolbarId}'/>">
    <%-- Button for Reorder --%>
    <span id="<c:out value='${reorderSpanId}'/>" class="ceReorderButton" style="display:none">
      <a href="javascript:<c:out value='${editorObj}'/>.reorderCollection();" class="buttonSmall">
        <fmt:message key="collectionEditor.sort"/>
      </a>
    </span>

    <div id="<c:out value='${addButtonToolbarId}'/>">
      <%-- Button for Add Existing --%>
      <span id="<c:out value='${addExistingSpanId}'/>" style="display:none">
        <a href="javascript:<c:out value='${editorObj}'/>.addExisting();" class="buttonSmall">
          <fmt:message key="collectionEditor.addExisting"/>
        </a>
      </span>

      <%-- Button for Add New --%>
      <span id="<c:out value='${addNewSpanId}'/>" style="display:none">
        <a href="javascript:<c:out value='${editorObj}'/>.addNew();" class="buttonSmall">
          <fmt:message key="collectionEditor.addNew"/>
        </a>
      </span>
    </div>

  </div>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/grid/basicGrid.jsp#2 $$Change: 651448 $--%>
