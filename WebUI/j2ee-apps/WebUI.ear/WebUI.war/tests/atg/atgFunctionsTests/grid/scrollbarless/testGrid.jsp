<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  
  
  
  <style type="text/css" media="screen">
    
    
   .atg .noScroll .dojoxGrid-scrollbox{
      overflow: auto;
    }
    
  </style>
  
  
  <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>

    <link rel="stylesheet" href="/WebUI/css/cssModules/foundation.css" type="text/css" media="all" />
    <link rel="stylesheet" href="/WebUI/css/cssFramework.css" type="text/css" media="all" />
  <!--[if IE]>
    <link rel="stylesheet" href="/WebUI/css/cssFramework_IE6.css" type="text/css" media="all" />
  <![endif]-->

    <link type="text/css" href="/dojo-1/dojo/resources/dojo.css" rel="stylesheet" />
    <link type="text/css" href="/dojo-1/dijit/themes/dijit.css" rel="stylesheet" />
    <link type="text/css" href="/dojo-1/dijit/themes/atg/atg.css" rel="stylesheet" />

  <script type="text/javascript">
    var djConfig = {
      isDebug: true,
      parseOnLoad: true
    };
  </script>

  <script type="text/javascript" src="/dojo-1/dojo/dojo.js"></script>
  <script type="text/javascript" src="/dojo-1/dojo/dojo-atg-apps.js"></script>
  <script type="text/javascript" src="/dojo-1/dijit/dijit.js"></script>
  <script type="text/javascript" src="/dojo-1/dijit/dijit-atg-apps.js"></script>
  <script type="text/javascript" src="/dojo-1/dojox/dojox-atg-apps.js"></script>

  <script type="text/javascript">
    dojo.require("dojo.parser");
    dojo.require("dijit.dijit");
    dojo.require("dojox.grid.Grid");
    dojo.require("dojox.grid._data.model");
  </script>

  <script type="text/javascript" src="/dojo-1/dojo-fixes.js"></script>

  <script type="text/javascript" src="/WebUI/atg.js">
  </script>

  <script type="text/javascript" src="testServerModel.js">
  </script>

  <script type="text/javascript" src="testGridCollectionEditor.js">
  </script>
</head>

<body style="padding: 20px" class="atg">


  <script type="text/javascript">

    // Create an instance of the GridEditor object containing JavaScript
    // methods for setting up and manipulating the grid.
    editorObj = new atg.assetmanager.GridEditor();

    // Set properties of the GridEditor object.
    editorObj.allowInsert = true;
    editorObj.allowReorder = true;
    editorObj.allowDelete = true;
    editorObj.useColumnHeaders = true;
    editorObj.containsRepItems = true;
    editorObj.editorObjId = 'editorObj';
    editorObj.gridElementId = 'gridObj';
    editorObj.numItemsDivId = 'numItemsDivId';
    editorObj.noItemsAddedDivId = 'noItemsAddedDivId';
    editorObj.reorderSpanId = 'reorderSpanId';
    editorObj.addNewSpanId = 'addNewSpanId';
    editorObj.addButtonToolbarId = 'addButtonToolbarId';
    editorObj.addExistingSpanId = 'addExistingSpanId';
    editorObj.insertMenuId = 'insertMenuId';
    editorObj.itemsLabel = 'Items';
    editorObj.reorderLabel = 'Reorder';
    editorObj.deleteLabel = 'Delete';
    editorObj.insertLabel = 'Insert';
    editorObj.insertExistingAboveLabel = 'Insert Existing Above';
    editorObj.insertExistingBelowLabel = 'Insert Existing Below';
    editorObj.insertNewAboveLabel = 'Insert New Above';
    editorObj.insertNewBelowLabel = 'Insert New Below';
    editorObj.invalidReorderIndexMessage = 'Not a valid reorder index';
    editorObj.componentColumns = [];

  </script>

  <script type="text/javascript">

    // Initialize the GridEditor object. This must be done after the component
    // JSP is initialized so that the popup menu functions exist.
    editorObj.initialize();

    // Create a data model for the grid.
    modelObj = new atg.assetmanager.TestEditorData('FakeComponentURLForAJAX');
    modelObj.count = 6;
    modelObj.rowsPerPage = 5;
    modelObj.standardErrorMessage = 'An error in the grid model has occurred';
    editorObj.setModel(modelObj);

    // Fake the layout object (this is almost identical to the real one in use).
     var columns = [
        { name: editorObj.reorderLabel,
          formatter: editorObj.formatReorderInput,
          styles: 'text-align:center;',
          width: '60px' },
        { name: 'Item Id',
              field: 2,
              styles: 'text-align:center;',
              width: '60px' },
        { name: 'Item',
            field: 0,
            extraField: 1,
            formatter: editorObj.formatLink,
            styles: 'text-align: center;',
            width: 'auto'},
        { name: " ",
          formatter: editorObj.formatInsertButton,
          styles: 'text-align:center;',
          width: '30px' },
        { name: " ",
          formatter: editorObj.formatDeleteButton,
          styles: 'text-align:center;',
          width: '30px' }
     ];

    // Fake some layout
     var columns = [
        { name: editorObj.reorderLabel,
          formatter: editorObj.formatReorderInput,
          styles: 'text-align:center;',
          width: '60px' },
        { name: 'Item Id',
              field: 2,
              styles: 'text-align:center;',
              width: '60px' },
        { name: 'Item',
            field: 0,
            extraField: 1,
            formatter: editorObj.formatLink,
            styles: 'text-align: center;',
            width: 'auto'},
        { name: " ",
          formatter: editorObj.formatInsertButton,
          styles: 'text-align:center;',
          width: '30px' },
        { name: " ",
          formatter: editorObj.formatDeleteButton,
          styles: 'text-align:center;',
          width: '30px' }
     ];

    layoutObj = [{
        cells: [columns]
      }];

    // Stuff to do once the page and grid are loaded.
    dojo.addOnLoad(function() {

      // Update the Item Count label and other controls as appropriate.
      editorObj.updateEditor(5);

      // Create the insert menu.
      if (editorObj.allowInsert) {
        //editorObj.createInsertMenu();
      }

      // Track window resize events so we can resize the grid.
      editorObj._resizeHandler =
        dojo.connect(window,
                    "resize",
                    dojo.hitch(dijit.byId('gridObj'), "update"));
    });

    dojo.addOnUnload(function() {
      dojo.disconnect(editorObj._resizeHandler);
    });

  </script>

  <%-- Supply some styling of the grid and controls.
       FIXME: Move to CSS file?
       --%>
  <style type="text/css">
    #gridObj {
      width: 100%;
      height: 380px;
      border: 1px solid silver;
    }
    #numItemsDivId {
       text-align: right;
    }
  </style>
  
  <!--
    
    
    dijit.byId('gridObj').model.addItems();
    
    -->

  <div id="numItemsDivId" style="display:none">
  </div>

  <div id="noItemsAddedDivId" style="display:none">
    <label for="noItemsAddedLabel">
      No items
    </label>
  </div>

  <%-- Create the TurboGrid inside of a DIV. --%>
  <div id="gridObj"
       jsid="gridObj"
       dojoType="dojox.Grid"
       model="modelObj"
       structure="layoutObj">
  </div>
  <br\>&nbsp;<br\>

  <div id="addButtonToolbarId">
    <%-- Button for Add Existing --%>
    <span id="addExistingSpanId">
      <a href="javascript:editorObj.addExisting();" class="buttonSmall">
        Add Existing
      </a>
    </span>

    <%-- Button for Add New --%>
    <span id="addNewSpanId">
      <a href="javascript:editorObj.addNew();" class="buttonSmall">
        Add item
      </a>
    </span>

    <%-- Button for Reorder --%>
    <span id="reorderSpanId">
      <a href="javascript:editorObj.reorder();" class="buttonSmall">
        Reorder
      </a>
    </span>
    
    <%-- Button for Reorder --%>
    <span id="reorderSpanId"> 
      <a href="javascript:dijit.byId('gridObj').model.addItems();" class="buttonSmall">
        Add Test
      </a>
    </span>
    
  </div>

</body>
</html>
<%-- @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tests/atg/atgFunctionsTests/grid/scrollbarless/testGrid.jsp#2 $$Change: 651448 $--%>
