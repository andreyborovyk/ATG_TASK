// 
// toolbar.js
// 
// This javascript object encapsulates the left pane toolbar functionality.
//
// NOTE: there is still significant javascript left in toolbar.jsp, but 
// it should all move into here eventually.

dojo.provide("atg.assetmanager.toolbar");
atg.assetmanager.toolbar = 
  {
  // the url for viewing the asset in the right pane
  assetEditorUrl: null,

  initialize: function() {
    // Listen for events coming from toolbar actions
    dojo.subscribe("/atg/assetmanager/toolbarResults", this, this.toolbarResponseFinish);
  },

  // 
  // This is invoked by quietFormSubmitter.handleResponse.  
  //
  toolbarResponseFinish: function (data) {

    if (data.refresh != 'true') {
      stopWait();
    }

    messages.hide();
    // clear the checkmarks as appropriate
    if (data.clearChecks) { 
      if (atg.assetmanager.checkboxes != null && typeof atg.assetmanager.checkboxes.uncheckAll === 'function') {
        atg.assetmanager.checkboxes.uncheckAll();
      }
      if (typeof uncheckAll === 'function') {
        uncheckAll();
      }
    }

    // update the multiedit asset count on the tab if it is changed
    if (data.multiEditAssetCount) {
      updateMultiEditTabCount(data.multiEditAssetCount);
    }

    // refresh the left pane if needed 
    if(data.refresh === 'true' && typeof refresh === 'function'){
      refresh();
    } 

    // refresh the right pane too
    if (data.dataStale === 'true') {
      document.getElementById("rightPaneIframe").src = this.assetEditorUrl;
    }

    // display the success message if it exists 
    if (data.successMessage) {
      messages.addAlert(data.successMessage);
    }

    // display any "expected" errors
    if (data.errors) {
      hideLeftLoadingIcon();
      for (var i=0; i<data.errors.length; i++){
        messages.addError(data.errors[i]);
      }
    }

    // close up the confirmation iframe (it may not 
    // have been opened, but this won't hurt)
    hideIframe("confirm");
  }
}
