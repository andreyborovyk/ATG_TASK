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
 * A client-side "tree widget".
 *
 * @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/scripts/tree.js#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
function WebTree() {

  //--------------------------------------------------------------------------//
  // Fields
  //--------------------------------------------------------------------------//

  /** The URL for populating a node. */
  this.populateNodeURL = null;

  /** The URL for opening a node. */
  this.openURL = null;

  /** The URL for closing a node. */
  this.closeURL = null;

  /** The URL for checking a node. */
  this.checkURL = null;

  /** The URL for checking a radio node. */
  this.clearAllAndCheckURL = null;

  /** The URL for unchecking a node. */
  this.uncheckURL = null;

  /** The URL for selecting a node. */
  this.selectURL = null;

  /** A function to be called when the user selects a node. */
  this.onSelect = null;

  /** A function to be called when the user unselects a node. */
  this.onUnselect = null;

  /** A function to be called when the user checks a node. */
  this.onCheck = null;

  /** A function to be called when the user unchecks a node. */
  this.onUncheck = null;

  /** The currently selected display element. */
  this.selectedItem = null;
 
  /** A node path whose associated display element should be pre-selected. */
  this.selectedPath = null;

  /** One of the highlighted items. */
  this.highlightedItem = null;

  /** A list of node paths whose associated display element should be highlighted.
      The highlighted nodes might be those that have been operated on behind the scenes. */
  this.highlightedPaths = null;

  /** wait before showing (Opening...) message */
  this.openingTimeout = null;
}

//----------------------------------------------------------------------------//
// Methods
//----------------------------------------------------------------------------//

/**
 * Send the invisible "action frame" to the given URL.  This is intended to
 * allow mouse clicks to control state information on the server without
 * refreshing the entire page.
 */
WebTree.prototype.sendAction = function(url) {
  var actionFrame = document.getElementById("actionFrame");
  if (actionFrame)
    actionFrame.src = url;
}

//----------------------------------------------------------------------------//
/**
 * This is called when the user selects a node.        
 */
WebTree.prototype.nodeSelected = function(path, info, ignoreUnselectCallback, ignoreSelectCallback) {

  // Unhighlight the selected item, if there is one.
  if (this.selectedItem) {
    if (this.onUnselect) {
     
      if (!ignoreUnselectCallback && this.onUnselect) {
        // Call the event listener in the parent window.
        var saveAssetFirst = this.onUnselect(path, info);   
        // if asset is modified, prompt user first.
        if (saveAssetFirst) return;         
      }
    }
    this.selectedItem.className = "treeNode";
    this.selectedItem = null;
  }
  
  // Highlight the selected item.
  this.selectedItem = document.getElementById("node_" + path);
  if (this.selectedItem) {
    this.selectedItem.className = "treeNodeSelected";
    this.selectedItem.path = path;
  }

  // Save the selected item on the server.
  var url = this.selectURL + "&nodeId=" + encodeURIComponent(path);
  this.sendAction(url);

  if (this.onSelect) {
    // Call the event listener in the parent window.
    if (!ignoreSelectCallback)
      this.onSelect(path, info);
  }
}

//----------------------------------------------------------------------------//
/**
 * Update the model on the server when the user checks a node's checkbox.
 */
WebTree.prototype.checkNode = function(path, info) {
  var elemid = "check_" + path;
  if (document.getElementById(elemid).checked)
    this.doCheckNode(path, info);
  else 
    this.doUncheckNode(path, info);
}

//----------------------------------------------------------------------------//
/**
 * Update the model on the server when the user checks a node's checkbox.
 */
WebTree.prototype.doCheckNode = function(path, info) {
  var url = this.checkURL + "&nodeId=" + encodeURIComponent(path);
  this.sendAction(url);

  if (this.onCheck) {
    // Call the event listener in the parent window.
    this.onCheck(path, info);
  }
}

//----------------------------------------------------------------------------//
/**
 * Update the model on the server when the user checks a node's radio button.
 */
WebTree.prototype.clearChecksAndCheckNode = function(path, info) {
  var url = this.clearAllAndCheckURL + "&nodeId=" + encodeURIComponent(path);
  this.sendAction(url);

  if (this.onCheck) {
    // Call the event listener in the parent window.
    this.onCheck(path, info);
  }
}

//----------------------------------------------------------------------------//
/**
 * Update the model on the server when the user unchecks a node's checkbox.
 */
WebTree.prototype.doUncheckNode = function(path, info) {
  var url = this.uncheckURL + "&nodeId=" + encodeURIComponent(path);
  this.sendAction(url);

  if (this.onUncheck) {
    // Call the event listener in the parent window.
    this.onUncheck(path, info);
  }
}

//----------------------------------------------------------------------------//
/**
 * Toggle the open/closed state of a node.
 */
WebTree.prototype.toggleNode = function(path, info) {
  elem = document.getElementById("openCloseIcon_" + path);
  if (elem) {
    if (elem.className == "openIcon")
      this.openNode(path);
    else {
      if (this.selectedItem &&
          this.selectedItem.path &&
          this.selectedItem.path != path &&
          this.selectedItem.path.indexOf(path) != -1) {
        this.nodeSelected(path, info);
      }
      this.closeNode(path);
    }
  }
}

//----------------------------------------------------------------------------//
/**
 * Close a node.
 */
WebTree.prototype.closeNode = function(path) {

  // Change the minus icon to a plus icon.
  elem = document.getElementById("openCloseIcon_" + path);
  if (elem)
    elem.className = "openIcon";

  // Hide the node's children.
  var elem = document.getElementById("subtreeContainer_" + path);
  if (elem)
    elem.style.display = "none";

  // Update the model on the server.
  var url = this.closeURL + "&nodeId=" + encodeURIComponent(path);
  this.sendAction(url);
}

//----------------------------------------------------------------------------//
/**
 * Open a node.
 */
WebTree.prototype.openNode = function(path) {

  // Change the plus icon to a minus icon.
  elem = document.getElementById("openCloseIcon_" + path);
  if (elem)
    elem.className = "closeIcon";

  // If the node has already been populated, just display it.
  // Otherwise, populate it with data from the server.      
  var elem = document.getElementById("subtreeDisplay_" + path);
  if (elem)
    this.openPopulatedNode(path);
  else
    this.populateNode(path);
}

//----------------------------------------------------------------------------//
/**
 * Open a node that has already been populated.
 */
WebTree.prototype.openPopulatedNode = function(path) {

  // Set the visibility to true.
  elem = document.getElementById("subtreeContainer_" + path);
  if (elem)
    elem.style.display = "inline";

  // Update the model on the server.
  var url = this.openURL + "&nodeId=" + encodeURIComponent(path);
  this.sendAction(url);
}

//----------------------------------------------------------------------------//
/**
 * Populate a node with data from the server.  This sends the invisible
 * action frame to treeFrame.jsp.  We will be notified when the frame
 * finishes loading, so we can copy the display from the frame. (See the
 * actionFrameLoaded function below).
 * If it takes longer than .8 seconds, show "(Opening...)"
 */
WebTree.prototype.populateNode = function(path) {
  var self = this;
  if (this.openingTimeout) clearTimeout(this.openingTimeout);
  this.openingTimeout = setTimeout(function() {self.showOpening(path)}, 800);
  var url = this.populateNodeURL + "&rootNodeId=" + encodeURIComponent(path);
  this.sendAction(url);
}

//----------------------------------------------------------------------------//
/**
 * Show "(Opening...)" while waiting to populate node
 */
WebTree.prototype.showOpening = function(path) {
  var expandingLabel = document.getElementById("expanding_" + path);
  if (expandingLabel)
    expandingLabel.style.display = "inline";
}

//----------------------------------------------------------------------------//
/**
 * Hide "(Opening...)" once node is populated
 */
WebTree.prototype.hideOpening = function(path) {
  if (this.openingTimeout)
    clearTimeout(this.openingTimeout);
  var expandingLabel = document.getElementById("expanding_" + path);
  if (expandingLabel)
    expandingLabel.style.display = "none";
}

//----------------------------------------------------------------------------//
/**
 * This is called after the given node's children have been pulled over
 * from the server and displayed in the hidden iframe.  We need to copy
 * the display from the iframe into the proper location in the main window.
 */
WebTree.prototype.populatePath = function(path) {
  this.hideOpening(path);
  var subtreeContainer = document.getElementById("subtreeContainer_" + path);
  var actionFrame = document.getElementById("actionFrame");
  if (actionFrame) {
    var elemId = "subtreeDisplay_" + path;
    var elem = actionFrame.contentWindow.document.getElementById(elemId);
    if (elem) {
      // This only works on Firefox, but the code following seems to
      // work on IE and Firefox.
      // var newElem = elem.cloneNode(true);
      var newElem = document.createElement("div");
      if (subtreeContainer) { // do the append first to keep IE happy
        subtreeContainer.appendChild(newElem);
      }
      newElem.setAttribute("id", elemId);
      newElem.innerHTML = elem.innerHTML;
    }
  }
}

//----------------------------------------------------------------------------//
/**
 * This function is called after the page is fully loaded.  It must be
 * invoked by the body.onload handler of any page that includes this object.
 */
WebTree.prototype.initializeAfterLoad = function() {


  // hide the loading screen
  var screenPanel   = document.getElementById("screen");
  if (screenPanel) {
    screenPanel.style.display = "none";
  }

  // Select an asset, if necessary.
  if (this.selectedPath != null) {
    this.selectedItem = document.getElementById("node_" + this.selectedPath);
    if (this.selectedItem) {
      this.selectedItem.className = "treeNodeSelected";
      this.selectedItem.path = this.selectedPath;
    }
  }

  // Highlight any items in the hightlightedPaths list.
  if (this.highlightedPaths != null) {
    var paths = this.highlightedPaths.split(",");
    for (var i = 0; i < paths.length; i++) {
      if (paths[i] != "") {
        this.highlightedItem = document.getElementById("node_" + paths[i]);
        if (this.highlightedItem)
          this.highlightedItem.className = "newItem";
      }
    }
  }
  
  // If there are highlighted items, they need to be visible.  If there are none, 
  // then the selected item should be visible.
	if(this.shouldScrollIntoView){
	  // If there are highlighted items, they need to be visible.  If there are none, 
	  // then the selected item should be visible.
	  if (this.highlightedItem)
	    this.highlightedItem.scrollIntoView();
	  else if (this.selectedItem)
	    this.selectedItem.scrollIntoView();
	}
}


//----------------------------------------------------------------------------//
// End of tree.js
//----------------------------------------------------------------------------//
