/*<ATGCOPYRIGHT>
 * Copyright (C) 2009 Art Technology Group, Inc.
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
 * Functions for managing selection state in list views.
 *
 * @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/scripts/selectionManagement.js#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

// The base URL for the currently displayed asset editor pane.
var atg_selectionManagement_assetEditorURL = "";

// Indicates if the Multi-Edit tab is currently visible.
var atg_selectionManagement_isMultiEdit = false;

// The version manager uri of the currently selected asset.
var atg_selectionManagement_assetContextURI = null;

// The index into the array of asset cells of the currently selected asset
var atg_selectionManagement_currentSelectedAssetIndex = -1;

function continueListSelectAfterConfirm(assetURI) {
  editAsset(assetURI, true, true);
}

// get the DOM elements that are the table cells containing asset names 
function getAssetCells() {
  var subdocument = document.getElementById("scrollContainer").contentWindow.document;
  var assetList = subdocument.getElementById("assetListContentTable");
  if (assetList == null) return null;
  var cells = assetList.getElementsByTagName("td");
  var assetcells = new Array();
  for (var ii=0; ii<cells.length; ii++) {
    var cellid = cells[ii].id;
    if ("nameCell_" == cellid.substring(0,9)) {
      assetcells.push(cells[ii]);
    }
  }
  return assetcells;
}

// this is for Step mode.  select the first asset for editing without user doing anything
function editFirstAsset() {
  var cells = getAssetCells();
  if(cells && cells.length > 0) {
    var cellid = cells[0].id;
    var uri = cellid.substring(9);
    atg_selectionManagement_currentSelectedAssetIndex = 0;
    autoEditAsset(uri, cells[0]);
  }
}

// this is for Step mode. Is there an asset in the array of assets after the currently selected one? 
// returns boolean
function nextAssetExists() {
  var cells = getAssetCells();
  return (cells && cells.length > atg_selectionManagement_currentSelectedAssetIndex + 1);
}

// this is for Step mode. Is there an asset in the array of assets before the currently selected one? 
// returns boolean
function prevAssetExists() {
  var cells = getAssetCells();
  return (cells && atg_selectionManagement_currentSelectedAssetIndex >= 1);
}

// select the next asset in the list for editing
function editNextAsset() {
  atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, null, parent.continueEditNextAsset, null, "continueEditNextAsset");
}      

// select the next asset in the list for editing
function continueEditNextAsset() {
  var cells = getAssetCells();
  if(cells && cells.length > atg_selectionManagement_currentSelectedAssetIndex) {
    var nextindex = atg_selectionManagement_currentSelectedAssetIndex+1;
    atg_selectionManagement_currentSelectedAssetIndex = nextindex;
    var cellid = cells[nextindex].id;
    var uri = cellid.substring(9);
    autoEditAsset(uri, cells[nextindex]);
  }
}

// select the previous asset in the list for editing
function editPrevAsset() {
  atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, null, parent.continueEditPrevAsset, null, "continueEditPrevAsset");
}      

// select the previous asset in the list for editing
function continueEditPrevAsset() {
  var cells = getAssetCells();
  if(cells && cells.length > 0 && atg_selectionManagement_currentSelectedAssetIndex >= 1) {
    var previndex = atg_selectionManagement_currentSelectedAssetIndex-1;
    atg_selectionManagement_currentSelectedAssetIndex = previndex;
    var cellid = cells[previndex].id;
    var uri = cellid.substring(9);
    autoEditAsset(uri,cells[previndex]);
  }
}

// called by editPrevAsset, editNextAsset, and editFirstAsset
function autoEditAsset(assetURI, nameCellElement) {
  unselectCurrentSelection();
  atg_selectionManagement_assetContextURI = assetURI;
  selectAsset(assetURI, nameCellElement);
  displayAssetRightPane(assetURI);
}

// get url for right hand side
function urlForRightPaneWithIndex(assetURI, assetIndex) {
  var url = atg_selectionManagement_assetEditorURL + "?assetURI=" + encodeURIComponent(assetURI) + "&assetIndex=" + ix + "&clearContext=true";;
  return url;
}

// display this asset in the right pane and set the index
function displayAssetRightPaneWithIndex(assetURI, assetIndex) {
  var url = urlForRightPaneWithIndex(assetURI,assetIndex);
  document.getElementById("rightPaneIframe").src = url;
}

// get url for right hand side
function urlForRightPane(assetURI) {
  var ix = atg_selectionManagement_currentSelectedAssetIndex + 1;
  var url = atg_selectionManagement_assetEditorURL + "?assetURI=" + encodeURIComponent(assetURI) + "&assetIndex=" + ix + "&clearContext=true";
  return url;
}

// display this asset in the right pane
function displayAssetRightPane(assetURI) {
  var url = urlForRightPane(assetURI);
  document.getElementById("rightPaneIframe").src = url;
}

// set the style of the current asset lable to selected
function selectAsset (assetURI, nameCellElement) {
  if (nameCellElement) {
    nameCellElement.className += " selected";
    var linkid = "nameLink_" + assetURI;
    var subdocument = document.getElementById("scrollContainer").contentWindow.document;
    var linkElement = subdocument.getElementById(linkid);
    linkElement.className="selected";
  }
}

// unselect all assets in the list to make way for the new selection
function unselectAll() {
  atg_selectionManagement_currentSelectedAssetIndex = -1;
  var cells = getAssetCells();
  for (var ii=0; ii<cells.length; ii++) { 
    cells[ii].className = cells[ii].className.replace(/ selected/, '');
  }
}

// unselect the current selection and clear the right pane
function clearCurrentSelection() {
  unselectCurrentSelection();
  displayAssetRightPane(null);
}

// set the style of the previously selected asset to unselected
function unselectCurrentSelection() {
  if (atg_selectionManagement_assetContextURI) {
    var cellid = "nameCell_" + atg_selectionManagement_assetContextURI;
    var subdocument = document.getElementById("scrollContainer").contentWindow.document;
    var nameCell = subdocument.getElementById(cellid);
    if (nameCell) {
      nameCell.className="nameCell";
      var linkid = "nameLink_" + atg_selectionManagement_assetContextURI;
      var subdocument = document.getElementById("scrollContainer").contentWindow.document;
      var linkElement = subdocument.getElementById(linkid);
      linkElement.className="";
    }
  }
}

// set style of the given asset to "selected" 
function editAsset(assetURI, ignoreUnselectLeftPaneEvent, ignoreSelectLeftPaneEvent) {
  if (atg_selectionManagement_isMultiEdit && parent.multiEditWait) return;

  if (!ignoreUnselectLeftPaneEvent) { 
    var propPage = urlForRightPane(assetURI);
    if (atg.assetmanager.saveconfirm.saveDontLeaveAsset(propPage, null, parent.continueListSelectAfterConfirm, assetURI, "continueListSelectAfterConfirm",false, true))
      return;
  }

  unselectAll();
  atg_selectionManagement_assetContextURI = assetURI;

  // select the new seleted node 
  var cellid = "nameCell_" + assetURI;
  var subdocument = document.getElementById("scrollContainer").contentWindow.document;
  var cell = subdocument.getElementById(cellid);
  selectAsset(assetURI, cell);

  // set the atg_selectionManagement_currentSelectedAssetIndex to the index of the selected cell
  var cells = getAssetCells();
  for (var ii=0; ii<cells.length; ii++) { 
    if (cellid == cells[ii].id) {
      atg_selectionManagement_currentSelectedAssetIndex = ii;
      break;
    }
  }

  if (ignoreSelectLeftPaneEvent) return;

  // it's possible the right hand pane wants to submit itself or jump to a location instead of sending a new url
  // (currently, this is only true for apply to all and list selections)
  var selectionHandled = false;
  var rightframe = parent.rightPaneIframe;

  if (rightframe != null && rightframe.handleSelection) {
    selectionHandled = rightframe.handleSelection(assetURI);
  }  


  // if it wasn't handled by right hand side already, send the new url
  if (!selectionHandled) {
      displayAssetRightPane(assetURI);
  }
}

function displaySelectedListAssetRightPane () {
  if (atg_selectionManagement_assetEditorURL == "") return false;
  var uri = atg_selectionManagement_assetContextURI;
  var ix = atg_selectionManagement_currentSelectedAssetIndex + 1;
  var url;
  if (uri) {
    url = atg_selectionManagement_assetEditorURL + "?assetURI=" + encodeURIComponent(uri) + "&assetIndex=" + ix;
  }       
  else  {
      url = atg_selectionManagement_assetEditorURL + "?clearContext=true";
  }
  document.getElementById("rightPaneIframe").src = url;
  return true;
}

//----------------------------------------------------------------------------//
