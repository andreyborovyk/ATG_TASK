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

/*
 * Methods needed by tree.jsp.  
 * This includes selection management, checkbox management, and tree refresh methods. 
 */ 
/*
 * global variables 
 */
var numChecks,     // used by tree widget pages 
  g_treeOptions; // storage for params to the initialize function


/*
 * Invoke this when the page loads to initialize values
 *
 * Params:
 *
 * treeURL                // the url to be invoked to display the tree
 * numChecks              // the number of items checked 
 * assetEditorURL         // the url to display in the asset detail (right) pane when an asset is selected
 * uncheckAllURL          // the url to invoke when the user clicks the uncheck-all widget
 */ 
function initializeTree(params) {
  g_treeOptions = params;
  numChecks = g_treeOptions.numChecks;
}
	
/*
 * this will uncheck all checkboxes
 */
function uncheckAll() {
  
  var uncheckAllCheckbox, // the DOM element for the uncheck all widget
    subdocument,          // the DOM element that contains the checkboxes
    checkboxes,           // the list of DOM elements that are the checkboxes
    ii,                   // iteration index
    actionFrame;          // a hidden iframe to which action urls should be submitted
  
  // this check box should never appear to be checked
  uncheckAllCheckbox = document.getElementById("uncheckAllCheckbox");
  if (! uncheckAllCheckbox) {
    return;
  }
  uncheckAllCheckbox.checked = false;
  subdocument = document.getElementById("scrollContainer").contentWindow.document;
  // uncheck all on the browser
  checkboxes = subdocument.getElementsByName("treeCheckbox");
  for (ii=0; ii<checkboxes.length; ii++) {
    checkboxes[ii].checked=false;
  }
  numChecks = 0;
  
  // NOTE: I need to communicate to the server, and
  // it is convenient to do it in the same way all the other tree actions do it.
  // I have to do it from here because the uncheckAll control is on this page.
  // Implementing this in this way means that we are now dependant on the tree
  // impl that we include.
  
  // uncheck all in the server side model
  actionFrame = subdocument.getElementById("actionFrame");
  if (actionFrame) {
    actionFrame.src = g_treeOptions.uncheckAllURL;
  }
}

/*
 * this is invoked when a tree node is unselected
 */
function nodeUnselect(path, info) {
  var propPage;

  messages.hide();
  if (info) {
    propPage = g_treeOptions.assetEditorURL +
      "?assetURI=" + encodeURIComponent(info.URI) +
      "&clearContext=true";
  } 
  return atg.assetmanager.saveconfirm.saveDontLeaveAsset(propPage, null, parent.continueTreeSelectAfterConfirm, path, "continueTreeSelectAfterConfirm");
}

/*
 * this is invoked when a tree node is selected
 */
function nodeSelected(path, info) {
  var propPage;

  messages.hide();
  // Show the item in the right pane
  propPage = g_treeOptions.assetEditorURL +
    "?assetURI=" + encodeURIComponent(info.URI) +
    "&clearContext=true";
  document.getElementById("rightPaneIframe").src = propPage;
}

/*
 * this is invoked when a tree node is checked
 */
function checkNode(path, info) {
  messages.hide();
  numChecks++;
}

/*
 * this is invoked when a tree node is checked
 */
function uncheckNode(path, info) {
  messages.hide();
  numChecks--;
}

/*
 * refresh the tree to pick up latest data
 */
function refresh() {
  var subdocument = document.getElementById("scrollContainer").contentWindow.document;
  subdocument.location = g_treeOptions.treeURL;
}

/*
 * invoked when new node is selected to confirm whether to save the previously selected node
 */
function continueTreeSelectAfterConfirm(selpath) {
  window.frames['scrollContainer'].tree.nodeSelected(selpath, null, true, true);
}


/*
 * invoked when new node is selected to confirm whether to save the previously selected node
 */
function treeLoaded() {
  stopWait();
  // hideLeftLoadingIcon(); this is done inside WebUI tree.js
}
