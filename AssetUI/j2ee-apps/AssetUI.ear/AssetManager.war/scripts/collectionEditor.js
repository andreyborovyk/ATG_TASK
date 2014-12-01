/*<ATGCOPYRIGHT>
 * Copyright (C) 2005-2009 Art Technology Group, Inc.
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
 * A client-side object that manipulates an editor for List, Map, and
 * Set properties.
 *
 * @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/scripts/collectionEditor.js#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
function CollectionEditor() {

  //--------------------------------------------------------------------------//
  // Fields
  //--------------------------------------------------------------------------//

  /** The ID of the table that is being managed. */
  this.tableId = null;

  /** The table that is being managed. */
  this.table = null;

  /** The ID of the body object for the table that is being managed. */
  this.tbodyId = null;

  /** The body object for the table that is being managed. */
  this.tbody = null;

  /** ID of div with hidden inputs that change the property value to null. */
  this.nullDivId = null;

  /** ID of the removable child div of the nullDivId element. */
  this.nullSubDivId = null;

  /** ID of the div around the entire collection editor. */
  this.outerDivId = null;

  /** ID of the span around the "Add Existing" button. */
  this.addSpanId = null;

  /** ID of the span around the "Add New" button. */
  this.newSpanId = null;

  /** ID of the span around the "Sort" button. */
  this.sortSpanId = null;

  /** The initial contents of the nullDivId element. */
  this.nullDivContents = null;

  /** The values of the location text fields. */
  this.sortInfos = null;

  /** The index of the row that is to be used as a template for new rows. */
  this.templateRowIndex = null;

  /** The contents of the row that is to be used as a template for new rows. */
  this.templateRowData = null;

  /** Prefix for the IDs of all table rows. */
  this.rowIdPrefix = null;

  /** Prefix for the IDs of all hidden DSP input fields. */
  this.dspInputIdPrefix = null;

  /** Prefix for the IDs of all map key input fields. */
  this.keyIdPrefix = null;

  /** Prefix for the IDs of all location input fields. */
  this.locationIdPrefix = null;

  /** Prefix for the IDs of all element value input fields. */
  this.valueIdPrefix = null;

  /** Indicates if the collection is empty at initialization time. */
  this.initiallyEmpty = null;

  /** Indicates if the property is a map. */
  this.isMap = null;

  /** Separator between keys/values in DSP input fields for map properties. */
  this.keyValueSeparator = null;

  /** Callback function, called after adding a row */
  this.onAddRow = null;

  /** Callback function, called before creating */
  this.onCreateAsset = null;

  /** Callback function, called before deleting a row */
  this.onDeleteRow = null;

  /** Callback function, called after adding or removing a row */
  this.onTotalCountChange = null;

  /** Callback function, called before transfering user values */
  this.onTransferUserValues = null;

  /** The index to be used when adding a new row */
  this.newRowIndex = null;

  /** The maxiumum number of elements before hiding the buttons for adding
      elements.  If negative, there is no limit on the number of elements. */
  this.maxCount = -1;

  /** Indicates if this editor uses numeric fields for sorting */
  this.useNumericSort = false;

  /** Display string for invalid values in a sort position */
  this.invalidLocationValueMessage = "Invalid location value:";

  /** Indicates if this collection editor should use the asset picker for
      adding/editing row values */
  this.useAssetPicker = false;

  /** Index of the element to which the asset picker applies */
  this.assetPickerElementIndex = null;

  /** Indicates if the asset picker is being used to add rows to the collection */
  this.addingRows = false;

  /** The URL to be used by the drillDown function */
  this.drillDownUrl = null;

  /** The URL to be used by the createAsset function */
  this.createAssetUrl = null;

  /** The repository name for the asset picker */
  this.repositoryName = null;

  /** The repository path for the asset picker */
  this.repositoryPath = null;

  /** The item type for the asset picker */
  this.itemType = null;

  /** The tree component (if applicable) for the asset picker */
  this.treeComponent = null;

  /** The URL for rendering the asset picker */
  this.assetPickerUrl = null;

  /** The asset picker title string */
  this.assetPickerTitle = "Select An Asset";

  /** Callback function, called once for each asset selected using the picker */
  this.onPick = null;

  /** The name of the window variable holding this instance */
  this.varName = null;

  /** don't allow last number of rows to move when sorting */
  this.stickLastRows = 0;

  /** Indicates if a sort should be performed on submit */
  this.needSort = false;
}

//----------------------------------------------------------------------------//
// Public functions
//----------------------------------------------------------------------------//

/**
 * Initialize the object after the page has completely loaded.
 */
CollectionEditor.prototype.initialize = function() {

  this.table = document.getElementById(this.tableId);
  this.tbody = document.getElementById(this.tbodyId);
  this._initTemplateRowData();
  this._initNullDivContents();
  if (this.useNumericSort)
    this._initSortInfos();
  if (this.initiallyEmpty)
    this._handleClearedTable();
  else
    this._handleUnclearedTable();
  this.transferOriginalValues();
}

//----------------------------------------------------------------------------//
/**
 * Move a row within the table.
 *
 * @param pId         The ID of the row element
 * @param pIncrement  The number of steps in the downward direction in which
 *                    to move the row
 */
CollectionEditor.prototype.moveRow = function(pId, pIncrement) {

  // Find the index of the row with the given ID.
  var srcIndex = this._getRowIndex(pId);
  if (srcIndex == null)
    return;

  // Find the new index for the row, and ensure that it is within range.
  var tbody = this.tbody;
  var numRows = tbody.rows.length;
  var dstIndex = srcIndex + pIncrement;
  if (dstIndex < 0 || dstIndex >= numRows)
    return;

  // Extract the contents of the source and destination rows.
  var srcContent = this._getRowData(tbody.rows[srcIndex]);
  var dstContent = this._getRowData(tbody.rows[dstIndex]);

  // Populate an array of row data.  Use the existing data, but reverse the
  // source and destination rows.
  var newRows = new Array();
  for (var i = 0; i < numRows; i++) {
    if (i == srcIndex)
      newRows[newRows.length] = dstContent;
    else if (i == dstIndex)
      newRows[newRows.length] = srcContent;
    else
      newRows[newRows.length] = this._getRowData(tbody.rows[i]);
  }

  // Rebuild the table using the new row data.
  this._rebuildTable(newRows);
}

//----------------------------------------------------------------------------//
/**
 * Quick sort implementation
 */
CollectionEditor.prototype._quickSort = function(array, left, right, comparator) {
  if (right <= left)
    return;
  var v = array[right];
  var i = left - 1;
  var j = right;
  while (true) {
    while (i < right && comparator(array[++i], v) < 0);
    while (j > left && comparator(array[--j], v) > 0);
    if (i >= j)
      break;
    var t = array[i];
    array[i] = array[j];
    array[j] = t;
  }
  var t = array[i];
  array[i] = array[right];
  array[right] = t;
  this._quickSort(array, left, i - 1, comparator);
  this._quickSort(array, i + 1, right, comparator);
}

//----------------------------------------------------------------------------//
/**
 * Sort rows within the table.
 */
CollectionEditor.prototype.sortRows = function() {

  // Clear the flag that will sort the rows when the form is submitted.
  this.needSort = false;

  // Sort the sortInfos array according to the currently specified locations.
  var numRows = this.sortInfos.length;
  this._quickSort(this.sortInfos, 0, numRows - 1, function(a, b) {
    var new1 = a.requestedLocation;
    var new2 = b.requestedLocation;
    if (new1 != new2)
      return new1 - new2;
    var diff1 = new1 - a.currentIndex - 1;
    var diff2 = new2 - b.currentIndex - 1;
    if (diff1 != 0 && diff2 != 0)
      return 0;
    if (diff1 != 0)
      return diff1;
    else
      return -diff2;
  });

  // Populate an array of row data using the new location values.
  var tbody = this.tbody;
  var newRows = new Array();
  for (var i = 0; i < numRows; i++) {
    var index = this.sortInfos[i].currentIndex;
    if (index >= tbody.rows.length)
      alert("Internal error: Sort info contains invalid index " + index +
            " (maximum is " + (tbody.rows.length - 1) + ")");
    else
      newRows[newRows.length] = this._getRowData(tbody.rows[index]);
  }
  for (var i = numRows; i < tbody.rows.length; i++) {
    newRows[newRows.length] = this._getRowData(tbody.rows[i]);
  }

  // Rebuild the table using the new row data.
  this._rebuildTable(newRows);
}

//----------------------------------------------------------------------------//
/**
 * Delete a row from the table.
 *
 * @param pId  The ID of the row element
 */
CollectionEditor.prototype.deleteRow = function(pId) {

  // This callback could be used to popup a confirmation dialog.
  if (this.onDeleteRow) {
    if (! this.onDeleteRow())
      return;
  }

  // Find the index of the row with the given ID.
  var srcIndex = this._getRowIndex(pId);
  if (srcIndex == null)
    return;

  // Delete the row from the table.
  var tbody = this.tbody;
  tbody.deleteRow(srcIndex);

  if (this.useNumericSort) {
    if (srcIndex > (tbody.rows.length - this.stickLastRows)) {
       this.stickLastRows = this.stickLastRows -1;
    }
    else {
      // Remove the corresponding entry from the sortInfos array.
      this.sortInfos.splice(srcIndex, 1);

      // Adjust the remaining values in the sortInfos array.
      for (var i = srcIndex; i < this.sortInfos.length; i++) {
        var sortInfo = this.sortInfos[i];
        var originalIndex = sortInfo.originalIndex;
        document.getElementById(this.locationIdPrefix + originalIndex).value = i + 1;
        sortInfo.requestedLocation = i + 1;
        sortInfo.currentIndex = i;
      }
    }
  }

  // Perform any operations that are necessary if the table has become empty.
  if (tbody.rows.length < 1)
    this._handleClearedTable();

  // If there is a maximum element count, ensure that the add buttons are
  // visible if the current count is within range.
  if (this.maxCount != -1 && this.maxCount > tbody.rows.length)
    this._setAddButtonVisibility(true);

  // Set the correct visibility for the Sort button.
  this._updateSortButtonVisibility();

  // Call the count-change callback, if specified.
  if (this.onTotalCountChange)
    this.onTotalCountChange();
}

//----------------------------------------------------------------------------//
/**
 * Add a new row to the table.
 */
CollectionEditor.prototype.addRow = function() {
  this._doAddRow(false);
}

//----------------------------------------------------------------------------//
/**
 * Internal function for adding a new row to the table.
 *
 * @param  pInMultiSelectSeries  Indicates if we are in the middle of adding
 *                               a series of rows to the table
 */
CollectionEditor.prototype._doAddRow = function(pInMultiSelectSeries) {

  // Perform any operations that are necessary if the table is currently empty.
  var tbody = this.tbody;
  if (tbody.rows.length < 1)
    this._handleUnclearedTable();

  // Add a new row to the table.
  var index = tbody.rows.length;
  var tr = tbody.insertRow(index);

  // Determine the index number to be used for IDs of elements in the row.
  var rowIndex = this.newRowIndex++;

  // Assign the appropriate ID to the row.
  tr.id = this.rowIdPrefix + rowIndex;

  // Copy the template row cell data into cells in the newly-created row.  Note
  // that all instances of the template row index in the cell HTML are replaced
  // with the actual index.
  var cells = this.templateRowData;
  if (cells) {
    for (var i = 0; i < cells.length; i++) {
      var cell = cells[i];
      var td = tr.insertCell(tr.cells.length);
      td.className = cell.className;
      td.id        = cell.id.replace(new RegExp(this.templateRowIndex, "g"), rowIndex);
      td.innerHTML = cell.innerHTML.replace(new RegExp(this.templateRowIndex, "g"), rowIndex);
    }
  }

  if (this.useNumericSort) {
    // Append a new entry to our sortInfo array, and assign the sort field value.
    var sortInfo = new Object();
    sortInfo.originalIndex = rowIndex;
    sortInfo.currentIndex = index;
    sortInfo.requestedLocation = index + 1;
    document.getElementById(this.locationIdPrefix + rowIndex).value = index + 1;
    this.sortInfos[index] = sortInfo;
  }

  // If there is a maximum element count, ensure that the add buttons are
  // hidden if the current count is out of range.
  if (this.maxCount != -1 && this.maxCount <= tbody.rows.length)
    this._setAddButtonVisibility(false);

  // Set the correct visibility for the Sort button.
  this._updateSortButtonVisibility();

  // Call the count-change callback, if specified.
  if (this.onTotalCountChange)
    this.onTotalCountChange();

  // If this collection editor uses the asset picker, then we need to pop up
  // the asset picker in order to prompt the user the select an asset, UNLESS
  // this function has been called by the onSelect callback of the asset picker.
  // This will happen if the user selected more than one item from the asset
  // picker, in which case we will need to add multiple rows to the table.
  //
  // If we are not using the asset picker, call the add-row callback, if
  // specified.  Note that the callback is responsible for marking the asset
  // as modified.
  if (this.useAssetPicker) {
    if (!pInMultiSelectSeries)
      this._pickAsset(rowIndex, true);
  }
  else if (this.onAddRow) {
    this.onAddRow(rowIndex);
  }
  else {
    markAssetModified();
  }
}

//----------------------------------------------------------------------------//
/**
 * Populate the user input fields for the collection elements with the
 * original property values from the hidden DSP inputs.  This action should
 * be performed whenever the page is loaded.
 */
CollectionEditor.prototype.transferOriginalValues = function() {
  this._transferElementValues(CollectionEditor.TO_USER_INPUTS);
}

//----------------------------------------------------------------------------//
/**
 * Transfer the values from the user input fields back to the hidden DSP
 * inputs.  This action should be performed whenever the form is submitted.
 */
CollectionEditor.prototype.transferUserValues = function() {

  // Always perform a sort prior to the data transfer, to prevent the user from
  // having to click the Sort button in order for changes to be applied.
  if (this.useNumericSort && this.needSort)
    this.sortRows();

  // Call the pre-transfer callback, if specified.
  if (this.onTransferUserValues)
    this.onTransferUserValues();

  this._transferElementValues(CollectionEditor.FROM_USER_INPUTS);
}

//----------------------------------------------------------------------------//
/**
 * A convenience function to return the number of rows in the table.
 */
CollectionEditor.prototype.getRowCount = function() {
  return this.tbody.rows.length;;
}

//----------------------------------------------------------------------------//
/**
 * A convenience function to return the index that is currently being used
 * for elements in the table row with the given actual index.
 */
CollectionEditor.prototype.getRowIndex = function(pIndex) {
  var rowId = this.tbody.rows[pIndex].id;
  return rowId.replace(new RegExp(this.rowIdPrefix), "");
}

//----------------------------------------------------------------------------//
/**
 * Set the visibility of the entire collection editor.
 */
CollectionEditor.prototype.setVisibility = function(pVisible) {
  var elem = document.getElementById(this.outerDivId);
  if (elem)
    elem.style.display = (pVisible ? "block" : "none");
}

//----------------------------------------------------------------------------//
/**
 * Show the "Add Existing" and "Add New" buttons.
 */
CollectionEditor.prototype.showAddButtons = function() {
  this._setAddButtonVisibility(true);
}

//----------------------------------------------------------------------------//
/**
 * Hide the "Add Existing" and "Add New" buttons.
 */
CollectionEditor.prototype.hideAddButtons = function() {
  this._setAddButtonVisibility(false);
}

//----------------------------------------------------------------------------//
/**
 * Show the "Sort" button.
 */
CollectionEditor.prototype.showSortButton = function() {
  this._setSortButtonVisibility(true);
}

//----------------------------------------------------------------------------//
/**
 * Hide the "Sort" button.
 */
CollectionEditor.prototype.hideSortButton = function() {
  this._setSortButtonVisibility(false);
}

//----------------------------------------------------------------------------//
/**
 * A convenience function for creating a new repository item.
 */
CollectionEditor.prototype.createAsset = function() {

  // This callback could be used to popup a confirmation dialog.
  if (this.onCreateAsset) {
    if (! this.onCreateAsset())
      return;
  }

  // Force a save of the current asset, then move to the URL.
  parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(this.createAssetUrl, null, null, null, null, true);
}

//----------------------------------------------------------------------------//
/**
 * A convenience function for drilling down into a repository item.
 *
 * @param  pUri  The URI of the item
 */
CollectionEditor.prototype.drillDown = function(pUri) {

  var linkUrl = this.drillDownUrl + "&assetURI=" + encodeURIComponent(pUri);

  // Force a save of the current asset, then move to the URL.
  parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(linkUrl, null, null, null, null, true);
}

//----------------------------------------------------------------------------//
/**
 * A function to be called by a component property editor for popping up the
 * asset picker to specify the value of a specific collection element.
 *
 * @param  pIndex  The index of the collection element to which the asset picker
 *                 is to apply
 */
CollectionEditor.prototype.modifyReference = function(pIndex) {
  this._pickAsset(pIndex, false);
}

//----------------------------------------------------------------------------//
/**
 * An internal function that pops up the asset picker in order to specify the
 * value of a specific collection element.
 *
 * @param  pIndex       The index of the collection element to which the asset
 *                      picker is to apply
 * @param  pAddingRows  Indicates that this collection element is being added
 *                      (as opposed to already being in existence)
 */
CollectionEditor.prototype._pickAsset = function(pIndex, pAddingRows) {

  // Save the given item index so that the _onPick function can find it.
  this.assetPickerElementIndex = pIndex;
  this.addingRows = pAddingRows;

  var allowableTypes = new Array();
  var assetType = new Object();
  assetType.typeCode       = "repository";
  assetType.displayName    = "NotUsed";
  assetType.repositoryName = this.repositoryName;
  assetType.createable     = "false";

  // The following two properties determine which view is to be displayed in the AP
  // NOTE: the type name needs to exist in the repository but it is not used in our AP plugin.
  assetType.typeName       = this.itemType;
  assetType.componentPath  = this.repositoryPath;
  allowableTypes[allowableTypes.length] = assetType;

  var pickerLauncher = new atg.assetmanager.AssetPickerLauncher();
  pickerLauncher.pickerURL               = this.assetPickerUrl;
  pickerLauncher.assetPickerTitle        = this.assetPickerTitle;
  pickerLauncher.onSelectFunction        = "CollectionEditor_onPick";
  pickerLauncher.callerData              = this.editorObjId;
  pickerLauncher.assetTypes              = allowableTypes;
  pickerLauncher.treeComponent           = this.treeComponent;
  pickerLauncher.pickableTypesCommaList  = this.itemType;
  pickerLauncher.callerData              = this.varName;
  pickerLauncher.allowMultiSelect        = (pAddingRows ? "true" : "false");
  pickerLauncher.onHideFunction          = "CollectionEditor_onAssetPickerHide";
  pickerLauncher.onHideData              = this.varName;

  pickerLauncher.invoke();
}

//----------------------------------------------------------------------------//
/**
 * Called when the user makes a selection from the asset picker.  This is just a
 * wrapper around the real onPick handler.  It is necessary because the asset
 * picker API requires the onSelect function to be specified as a string.
 *
 * @param  pSelected  An object containing info about the selected asset
 * @param  pData      Data specified by the code that invoked the picker
 *                    (namely, the ID of the window variable referring to the
 *                    CollectionEditor instance that invoked the picker)
 */
CollectionEditor_onPick = function(pSelected, pData) {

  // Find the window variable that refers to the CollectionEditor instance that
  // invoked the asset picker.  Then, invoke its _onPick function.
  var editor = window[pData];
  if (editor)
    editor._onPick(pSelected);
  else
    alert("Internal error: Can't locate collection editor " + pData);
}

//----------------------------------------------------------------------------//
/**
 * Called when the user makes a selection from the asset picker.
 *
 * @param  pSelected  An object containing info about the selected asset
 */
CollectionEditor.prototype._onPick = function(pSelected) {

  // Determine the index of the collection element to which the asset picker applies.
  var index = this.assetPickerElementIndex;

  // If rows are being added to the collection, then the asset picker is in
  // multi-select mode, and the selection data is an arrow.  Otherwise, it is
  // a single object, which we will wrap in an array.
  var selected = (this.addingRows ? pSelected : [pSelected]);

  // Loop through all of the selected assets, and modify collection elements
  // starting at the specified index.
  for (var i = 0; i < selected.length; i++, index++) {

    // Store the ID of the selected asset in the associated value input.
    var input = document.getElementById(this.valueIdPrefix + index);
    if (input)
      input.value = selected[i].id;

    // Call the onPick callback if one was specified.
    if (this.onPick)
      this.onPick(selected[i], index);

    // Unless this is the last item in the array, add a new row to the table.
    if (i < selected.length - 1)
      this._doAddRow(true);
  }

  markAssetModified();
  this.addingRows = false;
}

//----------------------------------------------------------------------------//
/**
 * Called when the user hides the asset picker.  This is just a wrapper around
 * the real onAssetPickerHide handler.  It is necessary because the asset
 * picker API requires the onSelect function to be specified as a string.
 *
 * @param  pData  Data specified by the code that invoked the picker
 *                (namely, the ID of the window variable referring to the
 *                CollectionEditor instance that invoked the picker)
 */
CollectionEditor_onAssetPickerHide = function(pData) {
  var editor = window[pData];
  if (editor)
    editor._onAssetPickerHide();
  else
    alert("Internal error: Can't locate collection editor " + pData);
}

//----------------------------------------------------------------------------//
/**
 * Called when the user hides the asset picker.
 */
CollectionEditor.prototype._onAssetPickerHide = function() {

  // If the assetPicker was being used to add rows to the collection, then, if
  // the user clicked Cancel on the asset picker, the addingRows flag will still
  // be true by the time this function is called.  In that case, we need to
  // delete the row that was added by the addRow function.
  if (this.addingRows) {
    this.deleteRow(this.rowIdPrefix + this.assetPickerElementIndex);
    this.addingRows = false;
  }
}

//----------------------------------------------------------------------------//
/**
 * Rearrange the editor so that the rows appear in the specified order.
 *
 * @param pIndices  An array indicating the row indices
 */
CollectionEditor.prototype.rearrangeTable = function(pIndices) {

  // Populate an array of row data using the given indices.
  var tbody = this.tbody;
  var newRows = new Array();
  for (var i = 0; i < pIndices.length; i++) {
    var index = pIndices[i];
    newRows[newRows.length] = this._getRowData(tbody.rows[index]);
  }

  // Rebuild the table using the new row data.
  this._rebuildTable(newRows);
}

//----------------------------------------------------------------------------//
/**
 * Handle a key press event in a row-positioning field.
 *
 * @param pIndex  The row index for the field
 * @param pEvent  The event (if W3C DOM is supported; use window.event otherwise)
 */
CollectionEditor.prototype.onPositionFieldKeyPress = function(pIndex, pEvent) {

  // Determine which key was pressed.
  if (pEvent == null)
    pEvent = event;
  var charCode = pEvent.charCode;
  if (charCode == null || charCode == 0)
    charCode = pEvent.keyCode;
  if (charCode == null)
    charCode = pEvent.which;

  // Don't allow keys that represent text other than digits.
  if (!pEvent.ctrlKey && !pEvent.altKey && charCode > 31 && (charCode < 48 || charCode > 57)) {
    return false;
  }

  // If the Enter key was pressed, call the onchange function.
  // Always return false, because the Enter key submits the form on Firefox.
  if (charCode == 13 || charCode == 3) {
    this.onPositionFieldChange(pIndex, pEvent);
    return false;
  }

  markAssetModified();
  return true;
}

//----------------------------------------------------------------------------//
/**
 * Handle a change event in a row-positioning field.
 */
CollectionEditor.prototype.onPositionFieldChange = function(pIndex, pEvent) {

  // Find the event target.
  if (pEvent == null)
    pEvent = event;
  var target = null;
  if (pEvent.srcElement)
    target = pEvent.srcElement;
  else if (pEvent.target)
    target = pEvent.target;

  // Find the sortInfo object for the given field index.
  var sortInfo = null;
  var found = false;
  for (var i = 0; i < this.sortInfos.length; i++) {
    sortInfo = this.sortInfos[i];
    if (sortInfo.originalIndex == pIndex) {
      found = true;
      break;
    }
  }
  if (!found) {
    alert("Internal error: Can't find info for sort-position field " + pIndex);
    return true;
  }

  // Display an error dialog and reject the input if it is not valid.
  var value = parseInt(target.value);
  if (isNaN(value)) {
    alert(this.invalidLocationValueMessage + " " + target.value);
    target.value = sortInfo.requestedLocation;
    return false;
  }

  // If the value is OK, save it.
  sortInfo.requestedLocation = value;
  this.needSort = true;
  markAssetModified();
  return true;
}


//----------------------------------------------------------------------------//
/**
 * Set the visibility of the entire collection editor.
 */
CollectionEditor.prototype.setStickLastRowCount = function(pCount) {
  this.stickLastRows = pCount;
}

//----------------------------------------------------------------------------//
// Internal functions
//----------------------------------------------------------------------------//

/**
 * Initialize the data for the table row that is to be used as the template for
 * runtime creation of new rows.
 */
CollectionEditor.prototype._initTemplateRowData = function() {

  // Get the template row element.
  var rowElem = document.getElementById(this.rowIdPrefix + this.templateRowIndex);
  if (rowElem) {

    // Extract the contents of all of the cells in the row, and then delete the
    // row from the table.
    this.templateRowData = this._getCellData(rowElem);
    rowElem.parentNode.removeChild(rowElem);
  }
}

//----------------------------------------------------------------------------//
/**
 * Initialize the data for the hidden inputs that can be used to set the
 * property value to null.
 */
CollectionEditor.prototype._initNullDivContents = function() {

  // Get the div that contains the inputs, and extract its contents of the div.
  var div = document.getElementById(this.nullDivId);
  if (div)
    this.nullDivContents = div.innerHTML;
}

//----------------------------------------------------------------------------//
/**
 * Initialize the location values.
 */
CollectionEditor.prototype._initSortInfos = function() {

  // There is one location value for every visible row in the table.
  this.sortInfos = new Array();
  var tbody = this.tbody;
  var numRows = tbody.rows.length;
  for (var i = 0; i < numRows; i++) {
    if (i < (numRows - this.stickLastRows)) {
      var sortInfo = new Object();
      sortInfo.originalIndex = i;
      sortInfo.currentIndex = i;
      sortInfo.requestedLocation = i + 1;
      this.sortInfos[i] = sortInfo;
    }
    else {
      document.getElementById(this.locationIdPrefix + i).style.display = "none";
    }
  }
}

//----------------------------------------------------------------------------//
/**
 * Rebuild the table using a given array of row data.
 *
 * @param pRows  The row data array
 */
CollectionEditor.prototype._rebuildTable = function(pRows) {

  // Clear the table.
  var tbody = this.tbody;
  while (tbody.rows.length > 0) {
    tbody.deleteRow(0);
  }

  // Re-populate the table using the extracted data.
  for (var i = 0; i < pRows.length; i++) {
    var tr = tbody.insertRow(tbody.rows.length);
    tr.id = pRows[i].id;
    var cells = pRows[i].cells;
    for (var j = 0; j < cells.length; j++) {
      var td = tr.insertCell(tr.cells.length);
      var cell = cells[j];
      td.className = cell.className;
      td.id        = cell.id;
      td.innerHTML = cell.innerHTML;

      // This is needed because Firefox does not include input values in the
      // innerHTML for a node.
      var values = cell.values;
      if (values) {
        for (var k = 0; k < values.length; k++) {
          var val = values[k];
          var elem = document.getElementById(val.id);
          if (elem)
            elem.value = val.value;
        }
      }
    }
  }

  if (this.useNumericSort) {
    // Update the sort field and the requestedLocation and currentIndex of all sortInfos.
    for (var i = 0; i < pRows.length; i++) {
      if (i < (pRows.length - this.stickLastRows)) {
        var sortInfo = this.sortInfos[i];
        var originalIndex = sortInfo.originalIndex;
        document.getElementById(this.locationIdPrefix + originalIndex).value = i + 1;
        sortInfo.requestedLocation = i + 1;
        sortInfo.currentIndex = i;
      }
    }
  }
}

//----------------------------------------------------------------------------//
/**
 * Transfer all of the collection element values between the hidden DSP inputs
 * and the values that will receive actual user input.
 *
 * @param pDirection  Indicates the direction in which values should be
 *                    transferred
 */

CollectionEditor.TO_USER_INPUTS   = 1; // transfer from DSP to user inputs
CollectionEditor.FROM_USER_INPUTS = 2; // transfer from user to DSP inputs

CollectionEditor.prototype._transferElementValues = function(pDirection) {

  // Loop through all of the rows in the table.
  var tbody = this.tbody;
  var numRows = tbody.rows.length;
  for (var i = 0; i < numRows; i++) {
    var rowId = tbody.rows[i].id;
    var rowIndex = rowId.replace(new RegExp(this.rowIdPrefix), "");

    // Get the DSP input field and the user input field for the current row.
    var dspInputElem = document.getElementById(this.dspInputIdPrefix + rowIndex);
    var valueElem = document.getElementById(this.valueIdPrefix + rowIndex);
    if (dspInputElem && valueElem) {
      if (this.isMap) {

        // For a map property, also get the input field for the map key.
        var keyElem = document.getElementById(this.keyIdPrefix + rowIndex);
        if (keyElem) {
          if (pDirection == CollectionEditor.FROM_USER_INPUTS) {

            // When transferring values from user inputs to DSP inputs, combine
            // the specified key and value strings into a single string of the
            // form "key=value".
            dspInputElem.value = keyElem.value + this.keyValueSeparator + valueElem.value;
          }
          else {

            // When transferring from DSP inputs to user inputs, extract the
            // key and value from the DSP input value, which is specified in
            // the form "key=value", and store each string in the appropriate
            // input field.
            var separatorPos = dspInputElem.value.indexOf(this.keyValueSeparator);
            keyElem.value = dspInputElem.value.substring(0, separatorPos);
            valueElem.value = dspInputElem.value.substring(separatorPos + 1);
          }
        }
      }
      else {

        // For a property that is not a map, just copy the string from one
        // input field to another, in the appropriate direction.
        if (pDirection == CollectionEditor.FROM_USER_INPUTS)
          dspInputElem.value = valueElem.value;
        else
          valueElem.value = dspInputElem.value;
      }
    }
  }
}

//----------------------------------------------------------------------------//
/**
 * Find a table row with a given ID.
 *
 * @param pId  The ID of the table row
 *
 * @return  The index, or null if the row is not found
 */
CollectionEditor.prototype._getRowIndex = function(pId) {
  var tbody = this.tbody;
  for (var i = 0; i < tbody.rows.length; i++) {
    if (tbody.rows[i].id == pId)
      return i;
  }
  return null;
}

//----------------------------------------------------------------------------//
/**
 * Get an array of input value data for a given cell.
 *
 * @param pCell  The cell object
 *
 * @return  The array of input value data
 */
function _CollectionEditorInputValueData() {
  this.id    = null; // The ID of the input element
  this.value = null; // The value of the input element
}
CollectionEditor.prototype._getInputValueData = function(pCell) {

  var values = new Array();

  // Loop through all of the input elements in the cell.
  var elements = pCell.getElementsByTagName("input");
  if (elements) {
    for (var i = 0; i < elements.length; i++) {
      var elem = elements[i];

      // Add a data object for the current input element, if it has an ID
      // and a value.
      if (elem.id && elem.value) {

        var valueData = new _CollectionEditorInputValueData();
        valueData.id    = elem.id;
        valueData.value = elem.value;

        values[values.length] = valueData;
      }
    }
  }

  return values;
}

//----------------------------------------------------------------------------//
/**
 * Get an array of cell data for a given row.
 *
 * @param pRow  The row object
 *
 * @return  The array of cell data
 */
function _CollectionEditorCellData() {
  this.className = null; // The class name for the table cell
  this.id        = null; // The id of the table cell
  this.innerHTML = null; // The contents of the cell
  this.values    = null; // Data for input elements in the cell
}
CollectionEditor.prototype._getCellData = function(pRow) {

  var cells = new Array();

  // Loop through all of the cells in the row.
  for (var i = 0; i < pRow.cells.length; i++) {
    var cell = pRow.cells[i];

    // Add a data object for the current cell.
    var cellData = new _CollectionEditorCellData();
    cellData.className = cell.className;
    cellData.id        = cell.id;
    cellData.innerHTML = cell.innerHTML;

    // We don't need to get the values on IE, because they are already stored
    // in the innerHTML.
    if (! dojo.isIE)
      cellData.values = this._getInputValueData(cell);

    cells[cells.length] = cellData;
  }

  return cells;
}

//----------------------------------------------------------------------------//
/**
 * Get data for a given row.
 *
 * @param pRow  The row object
 *
 * @return  The data object
 */
function _CollectionEditorRowData() {
  this.id        = null; // The ID of the row object
  this.cells     = null; // Data for all cells in the row
}
CollectionEditor.prototype._getRowData = function(pRow) {

  var rowData = new _CollectionEditorRowData();

  rowData.id = pRow.id;
  rowData.cells = this._getCellData(pRow);

  return rowData;
}

//----------------------------------------------------------------------------//
/**
 * Handle the case where the table has become empty.
 */
CollectionEditor.prototype._handleClearedTable = function() {

  // Hide the table, in case it is displaying a header.
  var table = this.table;
  if (table && table.style.display != "none")
    table.style.display = "none";

  // Find the div into which we will add input fields that will cause the
  // property value to be set to null.
  var div = document.getElementById(this.nullDivId);

  // Copy into the div the HTML for the input fields (which was obtained during
  // initialization by _initNullDivContents).
  if (div)
    div.innerHTML = this.nullDivContents;
}

//----------------------------------------------------------------------------//
/**
 * Handle the case where the first row is about to be added to the table.
 */
CollectionEditor.prototype._handleUnclearedTable = function() {

  // Find the div that contains the input fields which set the property value
  // to null.
  var div = document.getElementById(this.nullSubDivId);

  // Remove the div from its parent.
  if (div)
    div.parentNode.removeChild(div);

  // Make the table visible.
  var table = this.table;
  if (table && table.style.display == "none") {
    if (dojo.isFF) {
      table.style.display = "table";
    }
    else {
      table.style.display = "block";
    }
  }
}

//----------------------------------------------------------------------------//
/**
 * Set the visibility of the "Add Existing" and "Add New" buttons.
 */
CollectionEditor.prototype._setAddButtonVisibility = function(pVisible) {

  var visibility = (pVisible ? "inline" : "none");

  // Find the span that contains each button.
  var addSpan = document.getElementById(this.addSpanId);
  var newSpan = document.getElementById(this.newSpanId);

  if (addSpan)
    addSpan.style.display = visibility;

  if (newSpan)
    newSpan.style.display = visibility;
}

//----------------------------------------------------------------------------//
/**
 * Display the Sort button if the collection contains more than one element;
 * hide it otherwise.
 */
CollectionEditor.prototype._updateSortButtonVisibility = function() {

  if (this.tbody.rows.length <= 1) {
    this._setSortButtonVisibility(false);
  } else {
    this._setSortButtonVisibility(true);
  }

}


//----------------------------------------------------------------------------//
/**
 * Display the Sort button if the collection contains more than one element;
 * hide it otherwise.
 */
CollectionEditor.prototype._setSortButtonVisibility = function(pVisible) {

  var visibility = (pVisible ? "inline" : "none");

  // Find the span that contains the button.
  var reorderSpan = document.getElementById(this.sortSpanId);

  if (reorderSpan) {
      reorderSpan.style.display=visibility;
  }
}


//----------------------------------------------------------------------------//
/**
 * Set the visibility of the Delete buttons (on each row)
 */
CollectionEditor.prototype._setDeleteButtonsVisibility = function(pVisible) {

  var visibility = (pVisible ? "inline" : "none");

  // Find the span that contains each button.
  var addSpan = document.getElementById(this.addSpanId);
  var newSpan = document.getElementById(this.newSpanId);

  if (addSpan)
    addSpan.style.display = visibility;

  if (newSpan)
    newSpan.style.display = visibility;
}

//----------------------------------------------------------------------------//
// End of collectionEditor.js
//----------------------------------------------------------------------------//
