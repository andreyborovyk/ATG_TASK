/*
	ATG PROGRESS CLOCKWISE
	<WIDGETDESCRIPTION>

	Created by Jim Barraud on Oct 2nd 2007.
	Copyright (c) 2007 Media-Hive. All rights reserved.

***************************************************************************/

/*
What do we provide
**************************************************************************/
dojo.provide("atg.widget.progress.clock.wise");

dojo.require("dojo.fx");
dojo.require("dojo.number");
dojo.require("atg.widget.progress.base");


dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("atg.widget.progressClock", [dijit._Widget, dijit._Templated, atg.widget.progressBase],  {
	
	// DOJO PROPERTIES //
	templatePath: "/WebUI/dijit/progress/templates/clock.html",

	transition: 'jump',

	postCreate: function(){
		this.domNode.id=this.widgetId;
		this.transition = (this.pingUrl)? this.transition : 'jump';
		this.updateView();
		
		if(this.pingDelay !== 0 && this.pingUrl !== ''){
			this.startPolling();
		}
		
		dojo.subscribe("/atg/assetManager/multiEdit/progress", this, "handleTopic");
		
	},
	
	
	// WIDGET SPECIFIC METHODS //
	
	jumpProgress: function(){
		this.domNode.title = this.percentProgress+"%";
		this.domNode.style.backgroundPosition = 0-(Math.floor(this.percentProgress/10) * 11)+"px";
		
		if(this.percentProgress >= 100){
			this.progressComplete();
		}else{
			dojo.removeClass(this.domNode, "complete");	
		}
	},
	
	progressComplete: function(){
		dojo.addClass(this.domNode, "complete");	
		dojo.publish("/atg/progress/complete",{'id': this.widgetId });
		this.domNode.title="Completed";
	},


 	noLastComma: '' /* Saves my sanity */
});