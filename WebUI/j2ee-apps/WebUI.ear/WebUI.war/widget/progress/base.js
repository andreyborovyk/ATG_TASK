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

dojo.widget.defineWidget(

	// 	widget name and class
	"atg.widget.progress", 

	// 	Widget Superclass	
	dojo.widget.HtmlWidget, {
	
	// DOJO PROPERTIES //
	templatePath: "/WebUI/widget/progress/templates/progress.html",
	templateCssPath: "/WebUI/widget/progress/templates/progress.css",  	

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
	containerWidth: '100%', 	// string representing the container width (should be px or %)
	showValue: true, 					// boolean that shows the % value in the widget
	transition: 'slide', 			// string, current supported transitions are 'jump' and 'slide'
	
	// PRIVATE VARIABLES //
	_containerPxWidth: 0,			// Calculated pixel width of the container
	_barPixWidth: 0, 					// Calculated pixel width of the bar
	_waitingForResponse: false,	// flag that indicates the bar is already waiting for a response
 
	// METHODS STUBS In ORDER OF FIRING //

	initialize: function(/*Object*/args, /*Object*/frag, /*Widget*/parent){
		// summary: stub function.
		return false;
	},
	
	uninitialize: function() {
		clearInterval(this.poller);
		clearInterval(this.slideAni);
	},

	fillInTemplate: function(){
		//dojo.debug("Firing fillInTemplate for: "+this.widgetId);
		this.domNode.id=this.widgetId;
		this.domNode.style.width = this.containerWidth;
		this.transition = (this.pingUrl)? this.transition : 'jump';
		this.updateView();
		dojo.html.addClass(this.widthValue, this.barColor);	

		this.numericalValue.style.display = (this.showValue)? "block" : "none";
		
		if(this.pingDelay != 0 && this.pingUrl != ''){
			this.startPolling();
		}
	},
	
	
	// WIDGET SPECIFIC METHODS //
	
	// Start the Polling process
	startPolling: function(){
		var _this = this;
		this.poller=setInterval(function(){
	      // Call the poll function
				if(!_this._waitingForResponse){
					dojo.debug("polling not first time: "+_this.widgetId);
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
		dojo.debugShallow(this.slideAni);
		clearInterval(this.slideAni);
		if(this.percentProgress > 99){
			this.progressComplete();
		}
	},
	
	
	progressComplete: function(){
		dojo.debug('The Progress Bar is Complete');
		dojo.html.addClass(this.domNode, "complete");	
		dojo.event.topic.publish("/atg/progress/complete",{'id': this.widgetId });
	},
	
	// method that goes to the server (to the url defined in pingUrl)
	// outcome would be a handle success being passed the new value
	// or handle error passed the error message
	doPing: function(){
		var _this = this;
		this._waitingForResponse = true;
		dojo.io.bind({
		  url: this.pingUrl,
		  // This content is only needed for the ping.jsp test case
			// not to be used in live environment
			content: {
	      "i": this.percentProgress
	    },
		  load: function(t, obj, e) {
				_this.handleSuccess(obj);  		
				_this._waitingForResponse = false;
			},
			error: function(t, e) {
			  _this.handleError(e.message);
				_this._waitingForResponse = false;
		  },
		  timeout: function(type){ 
		    dojo.debug("Time Out Error on Server Polling on widget: "+this.widgetId);
			  _this.handleTimeOut('Time Out Error on Server Polling');
				_this._waitingForResponse = false;
		  },
			mimetype: "text/json",
			method: "GET"
		});
	},
	
	handleSuccess: function(obj){
		dojo.debug(obj);
		if(obj.value < 101 && obj.value > 0){
			this.oldPercent = this.percentProgress;
			this.percentProgress = Math.floor(obj.value);
			this.updateView();
		}else{
			//this.handleError("Value returned wasn't between 0-100%!");
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
		dojo.event.topic.publish("/atg/message/addNew",errorMessage);
    
		// debug statement the error message
		dojo.debug(errorMessage)

		// clear the polling if there's an issue
		clearInterval(this.poller);
	},

	handleTimeOut: function(e){


		this.handleError(e);
	},

 	noLastComma: '' /* Saves my sanity */
});