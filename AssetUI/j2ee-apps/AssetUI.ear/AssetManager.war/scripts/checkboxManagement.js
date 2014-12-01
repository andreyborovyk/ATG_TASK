// 
// checkboxManagement.js
// 
// This javascript object encapsulates the left pane checkbox functionality.
//

dojo.provide("atg.assetmanager.checkboxes");
atg.assetmanager.checkboxes = 
  {
  /*
   * global variables
   */
  options: null,   // storage for params to the initialize function
  numChecks: 0,
 
  /*
   * Invoke this when the page loads to initialize values
   * 
   * Params: 
   *
   * numChecks              the number of items checked 
   * numAssets              the number of assets available to be checked
   * clearChecksOnTabChange whether or not to clear all checks when 
   *                        the user moves among tabs
   * checkURL               the url to invoke when the user (un)checks 
   *                        a checkbox for a single item 
   * checkAllURL            the url to invoke when the user clicks the 
   *                        check-all widget
   * uncheckAllURL          the url to invoke when the user clicks the 
   *                        uncheck-all widget
   */
  initialize: function(params) {
    this.options = params;
    this.numChecks = this.options.numChecks;
  },

  /* 
   * set the num checks
   */
  setNumChecks: function(num) {
    this.numChecks = num;
  },

  /*
   * this will uncheck all checkboxes on the server upon tab change
   */
  uncheckAll: function() {
    this.sendAction(this.options.uncheckAllURL);
    subdocument = parent.document.getElementById("scrollContainer").contentWindow.document;
    checkboxes = subdocument.getElementsByName("assetCheckboxElement");
    for (ii=0; ii<checkboxes.length; ii++) {
      checkboxes[ii].checked=false;
    }
    // uncheck the checkAll checkbox
    parent.document.getElementById("checkAllCheckbox").checked=false;

    this.numChecks = 0;
  },

  /*
   * this will check all or uncheck all depending on current state of checkboxes
   */
  checkAll: function() {
    var ii,         // loop indexer
      subdocument,  // the DOM object holding the list of checkboxes 
      checkboxes,   // the DOM elements that represent the checkboxes
      checkEm;      // whether the checkALL means to check or to uncheck all elements

    subdocument = parent.document.getElementById("scrollContainer").contentWindow.document;
    checkboxes = subdocument.getElementsByName("assetCheckboxElement");

    // figure out if we are checking or unchecking
    if (this.numChecks > 0) {
      checkEm = false;
    } else {
      checkEm = true;
    }

    // set the various checkbox values in the page
    for (ii=0; ii<checkboxes.length; ii++) {
      checkboxes[ii].checked=checkEm;
    }
    // the checkall widget value should follow the value of the other checkboxes 
    parent.document.getElementById("checkAllCheckbox").checked = checkEm;
  
    // communcate to the server 
    if (checkEm) {
      this.numChecks = this.options.numAssets;
      this.sendAction(this.options.checkAllURL);
    } else {
      this.numChecks = 0;
      this.sendAction(this.options.uncheckAllURL);
    }
  },

  /*
   * Update the model on the server when the user checks an item's checkbox.
   */
  checkItem: function(uri) {
    var url,       // the action url to invoke to send check to the server
      action,      // 'remove' or 'add'
      subdocument; // the DOM element that contains all the checkboxes

    subdocument = parent.document.getElementById("scrollContainer").contentWindow.document;
    if (subdocument.getElementById("checkbox_" + uri).checked) {
      action = "add";
       this.numChecks++;
    }
    else {
      action = "remove";
       this.numChecks--;
    }
    url = this.options.checkURL +
      "&action=" + action +
      "&item=" + encodeURIComponent(uri);
    this.sendAction(url);
  },

  /*
   * Send the invisible "action frame" to the given URL.  This is intended
   * to allow mouse clicks to control state information on the server
   * without refreshing the entire page.
   */
  sendAction: function(url) {
    var actionFrame = document.getElementById("actionFrame");
    if (actionFrame) {
      actionFrame.src = url;
    }
  }
}
