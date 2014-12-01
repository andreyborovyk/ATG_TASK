dojo.provide("atg.widget.exportDownload");
dojo.declare("atg.widget.exportDownload", [dijit._Widget],{

  // Object Properties
  intervalSecs: 1, // Delay between calls in secs
  timeoutSecs: 30, // How long to wait for a poll response in secs
  started: false,
  timer: null,
  pollRequestUrl: "/AssetManager/transfer/exportStatus.jsp",
  errorLog: [],

  // Methods
  /**
	 * Initialize the widget.
	 */
  postCreate: function() {
    console.debug("atg.assetManager.export.Download", "postCreate()");
    
    dojo.subscribe("atg/assetManager/exportError", this, "exportError");
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
		hideExportDialog();
	},
	
	errorDestroy: function(){
	  console.debug("atg.assetManager.export.Download", "errorDestroy()");
    
    dojo.unsubscribe("atg/assetManager/exportError", this, "exportError");
    // stop the polling
		this.pollStop();
        
		// remove the loading screen
		this.stopScreen();
    
		// replace the export dialog content with the error message
		// this may need to be readdressed
		atg.setInnerHTML(dojo.byId("exportBody"), this.errorLog.toString());
		
		
	},

  sendDownloadRequest: function(){
    console.debug("atg.assetManager.export.Download", "sendDownloadRequest()");
    
    this.downloadForm=dojo.byId("exportFormForm");
    
    /* submit the form to the iframe */
    dojo.byId("exportr").click();

    /* start polling */
    this.pollStart();
    this.startScreen();
  },

  startScreen: function(){
    dojo.byId("screen").style.display="block";
  },
  
  stopScreen: function(){
    dojo.byId("screen").style.display="none";
  },

  removeIframe: function(){
    /* remove the iframe and do some clean up */
    /* TODO: do we need to worry about memory leaks form the iframe removal? */
    this.secretIframe.parentNode.removeChild(this.secretIframe);
  },

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
      timeoutSeconds: this.timeoutSeconds,

      load: function(responseObject, ioArgs){
        //var status = obj.xhr.responseText.replace(/^\s+|\s+$/g,"");
        
        console.debug("Download done? ", responseObject.complete);
        
        // loop through error messages and add them to the end of the error log array.
        dojo.forEach(responseObject.messages, 
          function(oneEntry, index, array) {

                  _this.errorLog.push(oneEntry);
                  console.debug("Number of Errors in Export Log: ", _this.errorLog.length)
              }
        )
        
        if(responseObject.complete == false){
          // download has not finished, don't do anything keep going
          if (_this.started){
            //dojo.debug("Restarting poller");
            _this.pollStart();
          }

        }else{
          // download has finished, shut everything down
          //are there errors?
          if(_this.errorLog.length != 0){
            // Yes, do the shut down with errors
            _this.errorDestroy();        
          }else{  
            // No, do the shut down without errors
            _this.noErrorDestroy();
          }
        }

      },

      error: function(t, e) {
        // TODO: Do we need to handle polling errors in the UI?  Or just keep going?
  		  dojo.debug("Export Download Error:" + e.message);
  		  _this.errorLog.push(e.message);
  		  dojo.topic.publish("atg/assetManager/serverError",e);
  	  },

      timeout: function(type){
        // TODO: Do we need to handle polling timesouts in the UI?  Or just keep going?
        dojo.debug("Export Download Error", "Poll timed out");
        _this.errorLog.push("Polling has Timed out");
        dojo.topic.publish("atg/assetManager/serverError",type);
      }

    });

  }

});
