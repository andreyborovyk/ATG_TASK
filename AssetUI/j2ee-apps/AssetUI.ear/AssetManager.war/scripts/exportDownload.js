// 
// exportDownload.js
// 
// This javascript object encapsulates all javascript functionality needed 
// for the export dialog.  
//

dojo.provide("atg.widget.exportDownload");
dojo.declare("atg.widget.exportDownload", [dijit._Widget],{

  // Object Properties
  intervalSecs: 1, // Delay between calls in secs
  timeoutSecs: 30, // How long to wait for a poll response in secs
  started: false,
  timer: null,
  pollRequestUrl: "/AssetManager/transfer/exportStatus.jsp",
  errorHandle:null,
  previewHandle:null,
  checkAllHandle:null,

  // Methods

  /**
   * Initialize the widget.
   */
  postCreate: function() {
    console.debug("atg.assetManager.export.Download", "postCreate()");
    if (this.errorHandle == null)
      this.errorHandle = dojo.subscribe("atg/assetManager/exportError", this, "exportError");
    if (this.previewHandle == null) 
      this.previewHandle = dojo.subscribe("/atg/assetmanager/exportPreview", this, this.displayPreview);
    if (this.checkAllHandle == null)
      this.checkAllHandle = dojo.subscribe("/atg/widget/checkAll", this, this.changeProperties);

    // ensure that proper property selection div shows
    this.changeType();
  },

  /**
   * get the selected asset type
   */
  getSelectedType: function() {
    var selBox = dojo.byId("typeSelectBox");
    if (selBox != null)
      return selBox[selBox.selectedIndex].value;
    return null;
  },

  /**
   * When the preview button is clicked, clear hidden 
   * checks, and then click the hidden real preview button.
   */
  doPreview: function() {
    this.clearHiddenChecks();
    dojo.byId('previewButton').click();
  },

  /**
   * Clear the checks for the unselected item types.  This is 
   * invoked upon export or preview so that we don't attempt to 
   * export any properties for the item types that were 
   * previously selected.
   */
  clearHiddenChecks: function() {
    var _this=this; // Maintain reference inside anonymous function
    var keepChecksInDivId = "propList_" + _this.getSelectedType();
    dojo.query("[name='exportPropDiv']").forEach(
      function f(x) {
        if (x.id != keepChecksInDivId) {
          dojo.query("[type='checkbox']", x).forEach(
            function f(y) {
              y.checked=false;
          });
        }
     });
   },

  /**
   * When the asset type is changed, this will change the 
   * list of selectable properties that is shown.
   */
  changeType: function() {
    var _this=this; // Maintain reference inside anonymous function

    // hide all prop lists
    dojo.query("[name='exportPropDiv']").forEach(
      function f(x) {
        dojo.style(x, "display", "none");
    });

    // show the one we want to
    var propList = dojo.byId("propList_" + _this.getSelectedType());
    dojo.style(propList, "display", "block");

    _this.changeProperties();
  },

  /**
   * invoked when property selections are changed
   */
  changeProperties: function() {
    var _this=this; // Maintain reference inside anonymous function
    _this.disEnableVerticalFormat();
    _this.erasePreview();
  },

  /**
   * Enable the Vertical Format Checkbox when there is exactly one multiproperty selected.
   */
  disEnableVerticalFormat: function() {
    var _this=this; // Maintain reference inside anonymous function

    //count the number of checked items for the curent item type
    var divid = "propList_" + _this.getSelectedType();
    var div = dojo.byId(divid);
    var singlecount = 0;
    var multicount = 0;
    dojo.query(".singleprop", div).forEach(
      function f(x) {
        if (x.checked) {
          singlecount++;
        }
    });

    dojo.query(".multiprop", div).forEach(
      function f(x) {
        if (x.checked) {
          multicount++;
        }
    });

    var vbox = dojo.byId("useVerticalFormatCheckbox");
    var vlabel = dojo.byId("verticalFormatLabel");
    if (singlecount == 0 && multicount == 1) {
      vbox.disabled = false;
      dojo.removeClass(vlabel, 'disableShowCollections');
    }
    else {
      vbox.checked = false;
      vbox.disabled = true;
      dojo.addClass(vlabel, 'disableShowCollections');
    }    
  },

  /**
   * erase the data in the preview table.
   */
  erasePreview: function() {
    var previewDiv = document.getElementById("previewData");
    previewDiv.innerHTML = "";
  },

  /**
   * This is invoked by quietFormSubmitter.handleResponse to display the preview data
   */
  displayPreview: function (data) {

    var previewDiv = document.getElementById("previewData");
    previewDiv.innerHTML = data.previewDataTable;

    // display any errors 
    if (data.errors) {
      for (var i=0; i<data.errors.length; i++){
        messages.addError(data.errors[i]);
      }
    }
  },

  displayStatus:  function(statusMessage, completeCount) {
    dojo.byId("numExported").innerHTML = completeCount;
    dojo.byId("exportStatusMessage").innerHTML = statusMessage;
    dojo.byId("exportStatusMessage").style.display = "block";
  },
  displayError:  function(errorMessage) {
    dojo.byId("exportError").innerHTML = errorMessage;
    dojo.byId("exportError").style.display = "block";
    dojo.byId("exportDialogCloseButton").style.display = "block";
  },
  displaySuccess:  function(successMessage) {
    dojo.byId("exportStatusMessage").innerHTML = successMessage;
    dojo.byId("exportStatusMessage").style.display = "block";
    dojo.byId("exportDialogCloseButton").style.display = "block";
  },

  /**
   * Tear down functions
   */
  noErrorDestroy: function() {
    console.debug("atg.assetManager.export.Download", "noErrorDestroy()");
    
    dojo.unsubscribe("atg/assetManager/exportError", this, "exportError");
    // stop the polling
    this.pollStop();
        
    // remove the loading screen
    this.stopScreen();
    
    // hide the export dialog popover
    //hideExportDialog();
    //dojo.byId("exportToolbarButton").focus();
  },
	
  /**
   * Do the export
   */
  sendDownloadRequest: function(){
    console.debug("atg.assetManager.export.Download", "sendDownloadRequest()");
    
    this.downloadForm=dojo.byId("exportFormForm");
    
    /* submit the form to the iframe */
    dojo.byId("exportr").click();

    /* start polling */
    this.pollStart();

    /* hide the export form and show the export status panel */
    dojo.byId("exportFormForm").style.display="none";
    dojo.byId("exportStatusPanel").style.display="block";

    this.startScreen();
  },

  startScreen: function(){
    dojo.byId("screen").style.display="block";
  },
  
  stopScreen: function(){
    dojo.byId("screen").style.display="none";
  },

//  removeIframe: function(){
    /* remove the iframe and do some clean up */
    /* TODO: do we need to worry about memory leaks form the iframe removal? */
//    this.secretIframe.parentNode.removeChild(this.secretIframe);
//  },

  exportError: function(obj){
    dojo.event.topic.publish("atg/assetManager/serverError",obj);
    // TODO: what do we do if there is an external error, like an error pre download
  },


  pollStart: function(){
    //dojo.debug("Poller starting with "+this.intervalSecs+" sec interval");
    // is the poller already doing a poll, don't poll again then
 
    if (this.started) return;
    var _this=this; // Maintain reference inside anonymous function
    this.timer=window.setInterval(function(){
      // Ensure poller instance hasn't been stopped
      if (!_this.started) return;

      // Call the poll function
      _this.doPoll(true);
      }
      ,(this.intervalSecs*1000));
    this.started=true;
  },


  pollStop: function(){

    //dojo.debug("Poller stopped");
    if (!this.started) return;
    window.clearInterval(this.timer);
    this.started=false;

    /* close down the popup and the doing stuff icon */

  },


  /* code that does the ajax poll */
  doPoll: function(){
    // Submit form
    var _this = this;
    dojo.xhrGet({
      url: this.pollRequestUrl,
      handleAs: "json",
      method: "post",
      timeoutSeconds: this.timeoutSecs,

      load: function(responseObject, ioArgs){
        //var status = obj.xhr.responseText.replace(/^\s+|\s+$/g,"");
        _this.displayStatus(responseObject.statusMessage, responseObject.completeCount);
        console.debug("Download done? ", responseObject.complete);
        
        if(responseObject.complete == false){
          // download has not finished, don't do anything keep going
          if (_this.started){
            //dojo.debug("Restarting poller");
            _this.pollStart();
          }

        }else{

          // loop through error messages and add them to the end of the error log array.
          if (responseObject.messages.length != 0) {
            dojo.forEach(responseObject.messages, 
              function(oneEntry, index, array) {
                _this.displayError(oneEntry);
            })
          }
          else {
           _this.displaySuccess(responseObject.statusMessage);
          }

          // We're done.  Close up the dialog.
          _this.noErrorDestroy();
        }
      },

      error: function(t, e) {
        // TODO: Do we need to handle polling errors in the UI?  Or just keep going?
        dojo.debug("Export Download Error:" + e.message);
        //messages.addError("Polling error: " + e.message);
        dojo.topic.publish("atg/assetManager/serverError",e);
     },

      timeout: function(type){
        // TODO: Do we need to handle polling timesouts in the UI?  Or just keep going?
        dojo.debug("Export Download Error", "Poll timed out");
        //messages.addError(_this.pollTimedOutMessage);
        dojo.topic.publish("atg/assetManager/serverError",type);
      }

    });

  }

});