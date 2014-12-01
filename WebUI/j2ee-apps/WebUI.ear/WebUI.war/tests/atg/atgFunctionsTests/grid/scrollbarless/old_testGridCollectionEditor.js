/*<ATGCOPYRIGHT>
 * Copyright (C) 2007 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

//----------------------------------------------------------------------------//

/**
 * A client-side object used to configure the appearance and behavior of
 * the grid collection editor.
 *
 * @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tests/atg/atgFunctionsTests/grid/scrollbarless/old_testGridCollectionEditor.js#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
dojo.provide("atg.assetmanager");

dojo.require("dojo.parser");  // scan page for widgets and instantiate them
dojo.require("dijit.Menu");

dojo.declare("atg.assetmanager.GridEditor", null, {

  /////////////////////////////////////////////////////////////////////
  //        FIELDS
  /////////////////////////////////////////////////////////////////////

  // Controls whether the grid shows a reorder input on each row.
  allowReorder: false,

  // Controls whether the grid shows an insert button on each row.
  allowInsert: false,

  // Controls whether the grid shows a delete button on each row.
  allowDelete: false,

  // Controls whether the grid shows column headers.
  useColumnHeaders: false,

  // Whether or not the grid contains a collection of repository items.
  containsRepItems: false,

  // ID of the GridEditor instance.
  editorObjId: null,

  // ID of the element used to display the TurboGrid.
  gridElementId: null,

  // ID of the element used to display the num items.
  numItemsDivId: null,

  // ID of the element used to display a "No Items" label.
  noItemsAddedDivId: null,

  // ID of the element used to display the Reorder button.
  reorderSpanId: null,

  // ID of the element used to display the Add New button.
  addNewSpanId: null,

  // ID of the element used to display the Add Existing button.
  addExistingSpanId: null,

  // ID of the element used to display the insert popup menu.
  insertMenuId: null,

  // The "items" string.
  itemsLabel: null,

  // The "reorder" column label.
  reorderLabel: null,

  // The "insert" column label.
  insertLabel: null,

  // The "insert" icon tooltip.
  insertIconTooltip: null,

  // The "delete" column label.
  deleteLabel: null,

  // The "delete" icon tooltip.
  deleteIconTooltip: null,

  // The "Insert Existing Above" menu label.
  insertExistingAboveLabel: null,

  // The "Insert Existing Below" menu label.
  insertExistingBelowLabel: null,

  // The "Insert New Above" menu label.
  insertNewAboveLabel: null,

  // The "Insert New Below" menu label.
  insertNewBelowLabel: null,

  // Columns supplied by component editor JSPs.
  componentColumns: [],

  // The insert index. An index of -1 means we are adding items.
  insertIndex: -1,

  // URL which can be used for drilling down into individual items.
  drilldownURL: null,

  // Name of a JS function which can be used for drilldown.
  drilldownFunc: null,

  // The repository containing items stored in the collection.
  itemRepository: null,

  // The item type of items stored in the collection.
  itemType: null,

  // The tree component for the asset picker.
  treeComponent: null,

  // The URL for rendering the asset picker
  assetPickerUrl: null,

  // The asset picker title string
  assetPickerTitle: null,

  // Store order-by-number changes requested by the user.
  _reorderIndices: [],

  /////////////////////////////////////////////////////////////////////
  //        UTILITY METHODS
  /////////////////////////////////////////////////////////////////////

  // Perform any necessary initialization of the editor.
  initialize: function() {

    // Use Javascript closures to get necessary values from the GridEditor
    // when the following methods are run. At invocation time the "this"
    // keyword no longer refers to the grid editor object.
    var editorId = this.editorObjId;
    var deleteTxt = this.deleteIconTooltip;
    var insertTxt = this.insertIconTooltip;

    // Create a cell formatter that renders a delete button.
    this.formatDeleteButton = function(pData, pRowIndex) {
      var html = '<a onclick="(editorId).removeRow(\'(rowIndex)\');" class="propertyClear" title="(deleteTxt)"></a>';
      html = html.replace('(editorId)', editorId);
      html = html.replace('(rowIndex)', pRowIndex);
      html = html.replace('(deleteTxt)', deleteTxt);
      return html;
    };

    // Create a cell formatter that renders an insert button.
    this.formatInsertButton = function(pData, pRowIndex) {
      var html = '<a class="propertyInsert" title="(insertTxt)" onclick="(editorId).showInsertMenu(event, \'(rowIndex)\');"></a>';
      html = html.replace('(insertTxt)', insertTxt);
      html = html.replace('(editorId)', editorId);
      html = html.replace('(rowIndex)', pRowIndex);
      return html;
    };

    // Cell formatter showing an input for reorder indices.
    this.formatReorderInput = function(pData, pRowIndex) {
      var reorderIndex = pRowIndex + 1;
      var html = '<input type="text" size="5" maxlength="9" value="reorderIndex"';
      html += ' onkeypress="return editorObj.onReorderInputKeyPress(event, \'rowIndex\');"';
      html += ' onchange="editorObj.onReorderIndexChange(event, \'rowIndex\');"></input>';
      html = html.replace('reorderIndex', reorderIndex);
      html = html.replace(/editorObj/g, editorId);
      html = html.replace(/rowIndex/g, pRowIndex);
      return html;
    };

    if (this.containsRepItems) {

      // Create a drilldown function for repository item collections.
      if (this.drilldownURL != null) {
        this.drilldown = function(pUri) {
          // Force a save of the current asset, then move to the URL.
          var linkURL = this.drilldownURL + "&assetURI=" + pUri;
          saveBeforeLeaveAsset(linkURL);
        };
      }
      else {
        this.drilldown = function(pUri) {
          if (parent[this.drilldownFunc]) {
            parent[this.drilldownFunc](pUri);
          }
          else {
            window[this.drilldownFunc](pUri);
          }
        };
      }

      // If the collection allows inserts, create an insert popup menu and
      // define an asset picker function for picking assets.
      if (this.allowInsert) {
        //var pickerObj = this.createPickerObject();
        //this.pickAsset = function(pAllowMultiSelect) {
        //  pickerObj.isAllowMultiSelect = (pAllowMultiSelect ? "true" : "false");
        //  pickerObj.invoke();
        //};
      }
    }
  },

  // Create a popup menu for inserting items into a collection.
  createInsertMenu: function() {
    this.insertMenuObj = new dijit.Menu({id: this.insertMenuId});
    this.insertMenuObj.addChild(new dijit.MenuItem({
                                  label: this.insertExistingAboveLabel,
                                  onClick: this.insertExistingAbove}));
    this.insertMenuObj.addChild(new dijit.MenuItem({
                                  label: this.insertExistingBelowLabel,
                                  onClick: this.insertExistingBelow}));
    this.insertMenuObj.addChild(new dijit.MenuSeparator());
    this.insertMenuObj.addChild(new dijit.MenuItem({
                                  label: this.insertNewAboveLabel,
                                  onClick: this.insertNewAbove}));
    this.insertMenuObj.addChild(new dijit.MenuItem({
                                  label: this.insertNewBelowLabel,
                                  onClick: this.insertNewBelow}));
    this.insertMenuObj.startup();
  },

  // Construct and return a default layout for the grid. This method can
  // be overwritten by component JSPs wishing to customize the column display.
  constructGridLayout: function() {
    var columns = [];

    // Reorder column.
    if (this.allowReorder) {
      columns[columns.length] =
        { name: this.reorderLabel,
          formatter: this.formatReorderInput,
          styles: 'text-align:center;',
          width: '60px' };
    }

    // Column(s) displaying info about the elements in the collection.
    var numComponentColumns = this.componentColumns.length;
    for (var i=0; i < numComponentColumns; i++) {
      columns[columns.length] = this.componentColumns[i];
    }

    // Insert column.
    if (this.allowInsert) {
      columns[columns.length] =
        { name: " ",
          formatter: this.formatInsertButton,
          styles: 'text-align:center;',
          width: '18px' };
    }

    // Delete column.
    if (this.allowDelete) {
      columns[columns.length] =
        { name: " ",
          formatter: this.formatDeleteButton,
          styles: 'text-align:center;',
          width: '18px' };
    }

    var layoutObj;
    if (this.useColumnHeaders) {
      layoutObj = [{
        cells: [columns]
      }];
    }
    else {
      layoutObj = [{
        onBeforeRow: this.onBeforeRow,
        cells: [columns]
      }];
    }

    return layoutObj;
  },

  // Handle changes to the collection by updating widgets as needed.
  updateEditor: function(pNumItems) {
    var isEmpty = (pNumItems == 0);
    this.updateNumItemsLabel(pNumItems);
    this.showGrid(!isEmpty);
    this.showNumItemsLabel(!isEmpty);
    this.showNoItemsLabel(isEmpty);
    this.showReorderButton(this.allowReorder && !isEmpty);
    this.showAddNewOrExistingButtons(this.containsRepItems && this.allowInsert && isEmpty);
  },

  // Update the Num Items label.
  updateNumItemsLabel: function(pNumItems) {
    var numItemsLabel = dojo.byId(this.numItemsDivId);
    numItemsLabel.innerHTML = pNumItems + " " + this.itemsLabel;
  },

  // Show/hide the Num Items Label.
  showNumItemsLabel: function(pShow) {
    var numItemsLabel = dojo.byId(this.numItemsDivId);
    numItemsLabel.style.display = (pShow ? "" : "none");
  },

  // Show/hide the grid.
  showGrid: function(pShow) {
    var grid = dojo.byId(this.gridElementId);
    grid.style.display = (pShow ? "" : "none");
  },

  // Show/hide the "no items" label.
  showNoItemsLabel: function(pShow) {
    var noItemsLabel = dojo.byId(this.noItemsAddedDivId);
    noItemsLabel.style.display = (pShow ? "" : "none");
  },

  // Show/hide the reorder button.
  showReorderButton: function(pShow) {
    var reorderButton = dojo.byId(this.reorderSpanId);
    reorderButton.style.display = (pShow ? "" : "none");
  },

  // Show/hide the buttons for Add New and Add Existing.
  showAddNewOrExistingButtons: function(pShow) {
    var addNewButton = dojo.byId(this.addNewSpanId);
    addNewButton.style.display = (pShow ? "" : "none");
    //var addExistingButton = dojo.byId(this.addExistingSpanId);
    //addExistingButton.style.display = (pShow ? "" : "none");
  },

  /////////////////////////////////////////////////////////////////////
  //        MODEL RELATED FUNCTIONS
  /////////////////////////////////////////////////////////////////////

  // Set the grid model object which does the work of communicating
  // with the server.
  setModel: function(pModel) {
    if (this._gridModel != null) {
      this._gridModel.notObserver(this);
    }

    // Listen for model events.
    pModel.observer(this);
    this._gridModel = pModel;
  },

  // Listen for items being removed from the grid model.
  modelRemoval: function(pKeys) {
    var count = this._gridModel.count;
    this.updateEditor(count);
    //markAssetModified();
  },

  // Listen for items being inserted or added into the grid model.
  modelInsertion: function(pRowIndex){
    var count = this._gridModel.count;
    this.updateEditor(count);
    //markAssetModified();
    if (pRowIndex && pRowIndex > -1) {
      var grid = dijit.byId(this.gridElementId);
      grid.scrollToRow(pRowIndex);
    }
  },

  // Handle reorder events.
  modelReorder: function() {
    var grid = dijit.byId(this.gridElementId);
    var reorderRows = [];
    if (this._reorderIndices.length > 0) {
      for (var i=0; i < this._reorderIndices.length; i++) {
        var requestedIndex = this._reorderIndices[i];
        if (requestedIndex != null) {
          reorderRows[reorderRows.length] = i;
        }
      }
    }

    // Reset the reorder array, set our dirty flag, and clear any highlighted rows.
    this._reorderIndices = [];
    markAssetModified();
    if (reorderRows.length > 0) {
      grid.highlightRows(reorderRows, false);
    }

    // Reset the grid position.
    grid.scrollToRow(0);
  },

  /////////////////////////////////////////////////////////////////////
  //        GRID ROW AND COLUMN FORMATTERS
  /////////////////////////////////////////////////////////////////////

  // Row formatter that handles hiding of column headers.
  onBeforeRow: function(pRowIndex, pSubRows) {
    // For the header row pRowIndex == -1. Note, we aren't using
    // compound rows so pSubRows[0] is the actual row.
    pSubRows[0].hidden = (pRowIndex == -1);
  },

  /////////////////////////////////////////////////////////////////////
  //        ACTION METHODS
  /////////////////////////////////////////////////////////////////////

  // Fake adding items to an empty model
  addNew: function() {
    // insert 1 item at index 0
    this._gridModel.addItems();
  },

  // Remove a row from the collection.
  removeRow: function(pRowIndex) {
    this._gridModel.removeRow(pRowIndex);
  },

  // Show a popup menu containing functions for insert.
  showInsertMenu: function(pEvent, pRowIndex) {
    if (pEvent == null) {
      pEvent = window.event;
    }

    // Using _openMyself will close the popup automatically whenever the user
    // makes a selection, hits ESC, or clicks outside the popup.
    this.insertIndex = pRowIndex;
    var menu = dijit.byId(this.insertMenuId);
    menu._openMyself(pEvent);

    // var domNode = (pEvent.currentTarget ? pEvent.currentTarget : pEvent.srcElement);
    // var args = {popup: menu, around: domNode};
    // dijit.popup.open(args);
  },

  // Handle key presses inside a reorder input.
  onReorderInputKeyPress: function(pEvent, pRowIndex) {

    // Determine which key was pressed.
    if (pEvent == null) pEvent = window.event;
    var charCode = pEvent.charCode;
    if (charCode == null || charCode == 0) charCode = pEvent.keyCode;
    if (charCode == null) charCode = pEvent.which;

    if (charCode == 13 || charCode == 3) {
      var target = (pEvent.currentTarget ? pEvent.currentTarget : pEvent.srcElement);
      var adjustedRowIndex = parseInt(pRowIndex) + 1;
      //console.log("onReorderInputKeyPress: current=" +adjustedRowIndex + " target= " +target.value+ " , reorderIndices= " + this._reorderIndices[pRowIndex]);

      // Commit any changes in the current input.
      if (adjustedRowIndex != target.value || this._reorderIndices[pRowIndex] != null) {
        //console.log("onReorderInputKeyPress: detected update, calling onReorderIndexChange");
        this.onReorderIndexChange(pEvent, pRowIndex);
      }

      // Reorder the collection if the user hits ENTER within a reorder input.
      this.reorderCollection();
      return false;
    }

    // All other keys are processed normally.
    return true;
  },

  // Handle user updates to an order-by-number text input.
  onReorderIndexChange: function(pEvent, pRowIndex) {

    // Determine the event target.
    if (pEvent == null) {
      pEvent = window.event;
    }
    var target = (pEvent.currentTarget ? pEvent.currentTarget : pEvent.srcElement);
    var rowIndex = parseInt(pRowIndex);
    var grid = dijit.byId(this.gridElementId);
    var rows;
    var requested = parseInt(target.value);

    if (target.value == "" ||
        isNaN(target.value) ||
        target.value.indexOf('.') != -1 ||
        isNaN(requested) ||
        requested <= 0)
    {
      // Reject invalid values.
      //console.log("onReorderIndexChange: Rejected invalid request " +target.value);
      target.value = rowIndex + 1;
    }
    else {
      // Store the requested index change and highlight the row.
      var newIndex = requested-1;
      this._reorderIndices[rowIndex] = newIndex;
      rows = [];
      rows[0] = rowIndex;
      grid.highlightRows(rows, true);
      //console.log("onReorderIndexChange: Accepted reorder from " +rowIndex+ " " +newIndex);
    }
  },

  // Create a new item and either add or insert it into the collection.
  createNewItem: function(pInsert) {
    var createUrl = this.createAssetUrl;
    if (pInsert) {
      createUrl += "&linkPropertyIndex=" + this.insertIndex;
    }

    // Force a save of the current asset, then move to the URL. Note that
    // we pass the linkPropertyIndex if pInsert == true, indicating we
    // are performing an insert operation.
    saveBeforeLeaveAsset(createUrl, null, null, null, null, true);
  },

  // Reorder the collection.
  reorderCollection: function() {
    var encodedReorderString = "";
    if (this._reorderIndices.length > 0) {
      for (var i=0; i < this._reorderIndices.length; i++) {
        var requestedIndex = this._reorderIndices[i];
        if (requestedIndex != null) {
          if (encodedReorderString.length > 0) {
            encodedReorderString += ",";
          }
          encodedReorderString += i + ":" + requestedIndex;
        }
      }

      if (encodedReorderString !== "") {
        this._gridModel.reorder(encodedReorderString);
      }
    }
  },

  /////////////////////////////////////////////////////////////////////
  //        ASSET PICKER METHODS
  /////////////////////////////////////////////////////////////////////

  // Create a Javascript object for invoking the asset picker.
  createPickerObject: function() {
    var allowableTypes = [];
    var assetType = {};
    assetType.typeCode       = "repository";
    assetType.displayName    = "NotUsed";
    //assetType.repositoryName = this.repositoryName;
    assetType.createable     = "false";

    // The following two properties determine which view is to be displayed in the AP
    // NOTE: the type name needs to exist in the repository but it is not used in our AP plugin.
    assetType.typeName       = this.itemType;
    assetType.componentPath  = this.itemRepository;
    allowableTypes[allowableTypes.length] = assetType;

    var viewConfiguration = [];
    viewConfiguration.treeComponent = this.treeComponent;
    viewConfiguration.itemTypes = this.itemType;

    var picker = new AssetBrowser();
    picker.mapMode                 = "AssetManager.assetPicker";
    picker.clearAttributes         = "true";
    picker.pickerURL               = this.assetPickerUrl;
    picker.assetPickerTitle        = this.assetPickerTitle;
    picker.browserMode             = "pick";
    picker.createMode              = "none";
    picker.onSelect                = "GridEditor_onPick";
    picker.callerData              = this.editorObjId;
    //picker.onHide                = "GridEditor_onAssetPickerHide";
    //picker.onHideData              = this.editorObjId;
    picker.closeAction             = "hide";
    picker.assetTypes              = allowableTypes;
    picker.assetPickerParentWindow = window;
    picker.assetPickerFrameElement = parent.document.getElementById("iFrame");
    picker.assetPickerDivId        = "browser";

    if (this.treeComponent !== null && this.treeComponent !== "") {
      picker.defaultView           = "Browse";
      picker.viewConfiguration     = viewConfiguration;
    }
    else {
      picker.defaultView           = "Search";
      picker.mapMode               = "AssetManager.assetPicker.search";
    }

    return picker;
  },

  // This is the callback method invoked by the asset picker when items are selected.
  _onPick: function(pSelected) {
    var idArray = [];
    if ( pSelected.length > 0 ) {
      for (var i=0; i < pSelected.length; i++) {
        idArray[idArray.length] = pSelected[i].id;
      }
    }
    else {
      idArray[0] = pSelected.id;
    }

    var grid = dijit.byId(this.gridElementId);
    if (this.insertIndex >= 0) {
      this._gridModel.insertItems(idArray, this.insertIndex);
    }
    else {
      this._gridModel.addItems(idArray);
    }
  }

});
