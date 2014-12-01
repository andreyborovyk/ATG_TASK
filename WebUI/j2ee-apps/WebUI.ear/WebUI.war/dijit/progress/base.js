/*
	ATG PROGRESS BAR
	<WIDGETDESCRIPTION>

	Created by Jim Barraud on Oct 2nd 2007.
	Copyright (c) 2007 Media-Hive. All rights reserved.

***************************************************************************/

/*
What do we provide
**************************************************************************/
dojo.provide("atg.widget.progress.base");

dojo.require("dojo.fx");
dojo.require("dojo.number");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("atg.widget.progressBase", [dijit._Widget, dijit._Templated], {
	
	// Widget Templates //
	templatePath: "/WebUI/dijit/progress/templates/progress.html",
	

	// PROGRESS BAR PROPERTIES //
	
	barColor: 'blue', 						// hex value
	percentProgress: 0, 			// initial progress bar width as a percentage
	pingDelay: 2000, 					// ms value for the delay between pings
	/* TODO :
	pingUrl probably needs to be updated moving forward so that the progress widget doesn't
	manage it's own AJAX calls, but rather using a common topic system it just uses pub/sub
	to request and handle responses. But as ATG doesn't have a current standard we'll keep it
	here.
	*/
	pingUrl: '', 							// url that the widget should use for the ping update, blank means no live update will occur.
	topicDriven: true,
	containerWidth: '100%', 	// string representing the container width (should be px or %)
	showValue: true, 					// boolean that shows the % value in the widget
	transition: 'slide', 			// string, current supported transitions are 'jump' and 'slide'
	
	// PRIVATE VARIABLES //
	_containerPxWidth: 0,			// Calculated pixel width of the container
	_barPixWidth: 0, 					// Calculated pixel width of the bar
	_waitingForResponse: false,	// flag that indicates the bar is already waiting for a response
	forwardOnly: false,
 
	// METHODS STUBS In ORDER OF FIRING //

	
	uninitialize: function() {
		clearInterval(this.poller);
		clearInterval(this.slideAni);
	},

	postCreate: function(){
		//dojo.debug("Firing fillInTemplate for: "+this.widgetId);
		this.domNode.id=this.widgetId;
		console.debug("This Progress Bar id: ",this.widgetId);
		this.domNode.style.width = this.containerWidth;
		this.transition = (this.pingUrl)? this.transition : 'jump';
		this.updateView();
		dojo.addClass(this.widthValue, this.barColor);	

		this.numericalValue.style.display = (this.showValue)? "block" : "none";
		
		if(this.pingDelay != 0 && this.pingUrl != ''){
			this.startPolling();
		}
		
			
		dojo.subscribe("/atg/assetManager/multiEdit/progress", this, "handleTopic");

	},
	

	
	
	// WIDGET SPECIFIC METHODS //
	
	// Handle Topic Update
	
	
	handleTopic: function(obj){
		if(obj.value < 101 && obj.value >= 0){
			if(this.forwardOnly && (obj.value <= this.percentProgress)) return true;
			this.oldPercent = this.percentProgress;
			this.percentProgress = Math.floor(obj.value);
			console.debug("this.percentProgress", this.percentProgress);
			this.updateView();
		}else{
			// error handling maybe ?
		}
	
	},	
	
	
	// Start the Polling process
	startPolling: function(){
		var _this = this;
		this.poller=setInterval(function(){
	      // Call the poll function
				if(!_this._waitingForResponse){
					console.debug("polling not first time: "+_this.id);
					_this.doPing();
				}
      }
      ,(this.pingDelay));
	},
	
	// Should be passed a pixel value that updates the bar
	updateView: function(){
		if(this.percentProgress >= 100){
			clearInterval(this.poller);
		}
	
		switch(this.transition){
			case 'slide':
				this.slideProgress();
				break;
			default:
				this.jumpProgress();
				break;
		}
	},
	
	jumpProgress: function(){
		this.numericalValue.innerHTML = this.percentProgress+"%";
		this.widthValue.style.width = this.percentProgress+"%";
		if(this.percentProgress >= 100){
			this.progressComplete();
		}
	},
	
	slideProgress: function(){
		if(!this.oldPercent) this.oldPercent = 0;
		this.positive = this.oldPercent <= this.percentProgress;
		var _this = this;
		clearInterval(this.slideAni);
		this.slideAni=setInterval(function(){
		
			if(_this.oldPercent != _this.percentProgress && _this.oldPercent < 100){
				if(_this.positive){
					_this.oldPercent++;
				}else{
					_this.oldPercent--;
				}
				_this.widthValue.style.width = _this.oldPercent+"%";
				_this.numericalValue.innerHTML = _this.oldPercent+"%";
			}else{
				_this.slideDone();
			}
			
		}		
		, ((this.pingDelay*0.8)/(this.percentProgress-this.oldPercent)));
	},
	
	
	slideDone: function(){
		clearInterval(this.slideAni);
		if(this.percentProgress > 99){
			this.progressComplete();
		}
	},
	
	
	progressComplete: function(){
		console.debug('The Progress Bar is Complete');
		dojo.addClass(this.domNode, "complete");	
		dojo.publish("/atg/progress/complete",{'id': this.widgetId });
	},
	
	// method that goes to the server (to the url defined in pingUrl)
	// outcome would be a handle success being passed the new value
	// or handle error passed the error message
	doPing: function(){
		var _this = this;
		this._waitingForResponse = true;
		dojo.xhrPost({
		  url: this.pingUrl,
		  // This content is only needed for the ping.jsp test case
			// not to be used in live environment
			content: {
	      "i": this.percentProgress
	    },
	    handleAs: 'json',
	
	    load: function (data) {
				_this.handleSuccess(data);  		
				_this._waitingForResponse = false;
			},
			error: function(error) {
			  _this.handleError(error.message);
				_this._waitingForResponse = false;
		  }
		});
	},
	
	handleSuccess: function(obj){
		if(obj.value < 101 && obj.value >= 0){
			this.oldPercent = this.percentProgress;
			this.percentProgress = Math.floor(obj.value);
			console.debug("this.percentProgress", this.percentProgress);
			this.updateView();
		}else{
			// error handling maybe ?
		}
	},
	
	// How to handle an error from the ping (either handle in this widget or throw 
	// out to atg.messaging widget)
	handleError: function(e){

		var	errorMessage = 	{ "messages":
													[
			                			{ "summary":e,
			                  			"type":"Error",
			                  			"datetime":dojo.date.format(new Date(), {formatLength:'full', locale:'en-us'}),
			                  			"details":[
			                    			{ "description":e
			                    			}
			                  			]
			                			}
			              			]
			            			};

		// Check that the messagebar widget exists send the error message there
		var messageBar = dojo.widget.manager.getWidgetsByType("messagBar");
		if(messageBar != ''){
			messageBar.unpackMessages(errorMessage); 
		}
		
		// publish the error in case there are other error handling widgets created
		dojo.topic.publish("/atg/message/addNew",errorMessage);
    
		// debug statement the error message
		console.debug(errorMessage)

		// clear the polling if there's an issue
		clearInterval(this.poller);
	},

	handleTimeOut: function(e){


		this.handleError(e);
	},

 	noLastComma: '' /* Saves my sanity */
});