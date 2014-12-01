<%--

  TurboGrid for displaying org's all members in a collection. Note the actual
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
  @param uniqueId                A unique identifier for the collection that will
                                 be used to construct Javascript and other identifiers.
  @param showIdColumn            Indicates whether an ID column should be displayed.
  @param showIconColumn          Indicates whether an Icon column should be displayed.
  @param showNumItems            Indicates whether an total count of items should be displayed.
  @param formHandlerPath         The form handler used for the retrieval of current organization
                                 and all its members.

  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/grid/orgAllMembersGrid.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"         uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"       uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"    uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramComponentContextRoot"    param="componentContextRoot"/>
  <dspel:getvalueof var="paramComponentURI"            param="componentURI"/>
  <dspel:getvalueof var="paramModelPageSize"           param="modelPageSize"/>
  <dspel:getvalueof var="paramKeepRows"                param="keepRows"/>
  <dspel:getvalueof var="paramUseColumnHeaders"        param="useColumnHeaders"/>
  <dspel:getvalueof var="paramNumElements"             param="numElements"/>
  <dspel:getvalueof var="paramUniqueId"                param="uniqueId"/>
  <dspel:getvalueof var="paramShowIdColumn"            param="showIdColumn"/>
  <dspel:getvalueof var="paramShowIconColumn"          param="showIconColumn"/>
  <dspel:getvalueof var="paramShowNumItems"            param="showNumItems"/>
  <dspel:getvalueof var="paramFormHandlerPath"         param="formHandlerPath"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- This is the URL issued to retrieve and manipulate grid data. --%>
  <c:url context="${paramComponentContextRoot}" value="${paramComponentURI}" var="componentURL">
    <c:param name="showIdColumn" value="${paramShowIdColumn}"/>
    <c:param name="showIconColumn" value="${paramShowIconColumn}"/>
    <c:param name="formHandlerPath" value="${paramFormHandlerPath}"/>
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
  <c:set var="insertMenuId" value="atg_assetmanager_${paramUniqueId}_insertMenuId"/>

  <script type="text/javascript">

    dojo.require("dojox.grid.Grid");
    dojo.require("dojo.parser");

    // Create an instance of the GridEditor object containing JavaScript
    // methods for setting up and manipulating the grid.
    <c:out value="${editorObj}"/> = new atg.assetmanager.GridEditor();

    // Set properties of the GridEditor object.
    <c:out value="${editorObj}"/>.allowInsert = <c:out value="${false}"/>;
    <c:out value="${editorObj}"/>.allowReorder = <c:out value="${false}"/>;
    <c:out value="${editorObj}"/>.allowDelete = <c:out value="${false}"/>;
    <c:out value="${editorObj}"/>.useColumnHeaders =
      <c:out value="${paramUseColumnHeaders == 'true'}"/>;
    <c:out value="${editorObj}"/>.containsRepItems = <c:out value="${false}"/>;
    <c:out value="${editorObj}"/>.editorObjId = '<c:out value="${editorObj}"/>';
    <c:out value="${editorObj}"/>.gridElementId = '<c:out value="${gridObj}"/>';
    <c:out value="${editorObj}"/>.numItemsDivId = '<c:out value="${numItemsDivId}"/>';
    <c:out value="${editorObj}"/>.noItemsAddedDivId = '<c:out value="${noItemsAddedDivId}"/>';
    <c:out value="${editorObj}"/>.reorderSpanId = '<c:out value="${reorderSpanId}"/>';
    <c:out value="${editorObj}"/>.addNewSpanId = '<c:out value="${addNewSpanId}"/>';
    <c:out value="${editorObj}"/>.addExistingSpanId = '<c:out value="${addExistingSpanId}"/>';
    <c:out value="${editorObj}"/>.insertMenuId = '<c:out value="${insertMenuId}"/>';
    <c:out value="${editorObj}"/>.itemsLabel = '<fmt:message key="orgAllMembers.members"/>';
    <c:out value="${editorObj}"/>.itemLabel = '<fmt:message key="orgAllMembers.member"/>';
    <c:out value="${editorObj}"/>.reorderLabel = '<fmt:message key="collectionEditor.index"/>';
    <c:out value="${editorObj}"/>.deleteLabel = '<fmt:message key="collectionEditor.delete"/>';
    <c:out value="${editorObj}"/>.insertLabel = '<fmt:message key="collectionEditor.insert"/>';
    <c:out value="${editorObj}"/>.insertExistingAboveLabel = '<fmt:message key="collectionEditor.insertExistingAbove"/>';
    <c:out value="${editorObj}"/>.insertExistingBelowLabel = '<fmt:message key="collectionEditor.insertExistingBelow"/>';
    <c:out value="${editorObj}"/>.insertNewAboveLabel = '<fmt:message key="collectionEditor.insertNewAbove"/>';
    <c:out value="${editorObj}"/>.insertNewBelowLabel = '<fmt:message key="collectionEditor.insertNewBelow"/>';
    <c:out value="${editorObj}"/>.componentColumns = [];

    // Orveride the default updateEditor function
    // in gridCollectionEditor. For all member we do
    // not support modifying the list hence no need to update
    // those divs.
    <c:out value="${editorObj}"/>.updateEditor = function(pNumItems) {
      var isEmpty = (pNumItems === 0);
      <c:out value="${editorObj}"/>.showGrid(!isEmpty);
      <c:out value="${editorObj}"/>.showNoItemsLabel(isEmpty);
      <c:if test="${paramShowNumItems == 'true'}">
        <c:out value="${editorObj}"/>.showNumItemsLabel(!isEmpty);
        <c:out value="${editorObj}"/>.updateNumItemsLabel(pNumItems);
      </c:if>
    };

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
    <dspel:param name="showIconColumn"    value="${paramShowIconColumn}"/>
    <dspel:param name="formHandlerPath"   value="${paramFormHandlerPath}"/>
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

      // Create the insert menu.
      if (<c:out value="${editorObj}"/>.allowInsert) {
        <c:out value="${editorObj}"/>.createInsertMenu();
      }

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

  <%-- Supply some styling of the grid and controls.
       FIXME: Move to CSS file?
       --%>
  <style type="text/css">
    #<c:out value='${gridObj}'/> {
      width: 100%;
      height: 200px;
      border: 1px solid silver;
    }
    #<c:out value='${numItemsDivId}'/> {
       text-align: right;
    }
  </style>

  <div id="<c:out value='${numItemsDivId}'/>" style="display:none">
  </div>

  <div id="<c:out value='${noItemsAddedDivId}'/>" style="display:none">
  </div>

  <%-- Create the TurboGrid inside of a DIV. --%>
  <div id="<c:out value='${gridObj}'/>"
       jsid="<c:out value='${gridObj}'/>"
       dojoType="dojox.Grid"
       model="<c:out value="${modelObj}"/>"
       structure="<c:out value="${layoutObj}"/>">
  </div>
  <%--<br\>&nbsp;<br\>--%>
</dspel:page>
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/profile/grid/orgAllMembersGrid.jsp#2 $$Change: 651448 $--%>
