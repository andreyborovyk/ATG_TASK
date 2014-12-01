/*
	ATG CLOCK PROGRESS BAR
	<WIDGETDESCRIPTION>

	Created by Jim Barraud on Oct 2nd 2007.
	Copyright (c) 2007 Media-Hive. All rights reserved.

***************************************************************************/

/*
What do we provide
**************************************************************************/
dojo.provide("atg.widget.progress.clock");

dojo.widget.defineWidget(

	// 	widget name and class
	"atg.widget.progressClock", 

	// 	Widget Superclass	
	atg.widget.progress, {
	
	// DOJO PROPERTIES //
	templatePath: "/WebUI/widget/progress/templates/clock.html",
	templateCssPath: "/WebUI/widget/progress/templates/clock.css",  	

	transition: 'jump',

	fillInTemplate: function(){
		//dojo.debug("Firing fillInTemplate for: "+this.widgetId);
		this.domNode.id=this.widgetId;
		//this.domNode.style.width = this.containerWidth;
		this.transition = (this.pingUrl)? this.transition : 'jump';
		this.updateView();
		dojo.html.addClass(this.widthValue, this.barColor);	

		
		if(this.pingDelay != 0 && this.pingUrl != ''){
			this.startPolling();
		}
	},
	
	
	// WIDGET SPECIFIC METHODS //
	
	jumpProgress: function(){
		this.domNode.title = this.percentProgress+"%";
		this.domNode.style.backgroundPosition = 0-(Math.floor(this.percentProgress/11) * 11)+"px";
		
		if(this.percentProgress >= 100){
			this.progressComplete();
		}
	},
	
	progressComplete: function(){
		dojo.debug('The Progress Bar is Complete');
		dojo.html.addClass(this.domNode, "complete");	
		dojo.event.topic.publish("/atg/progress/complete",{'id': this.widgetId });
		this.domNode.title="Completed";
	},


 	noLastComma: '' /* Saves my sanity */
});