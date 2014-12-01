/*
-----------------------------------------------------------------------------------
######## DEPRECATED - USE THE VERSION IN WEBUI/DIJIT/ASSISTANCE INSTEAD ###########
-----------------------------------------------------------------------------------
*/


/*
	EmbeddedAssistanceInline Widget v0.1
	
	- For showing various types of inline and hover context help.

***************************************************************************/
dojo.provide("atg.widget.assistance.inline");

dojo.widget.defineWidget("atg.widget.assistanceInline", dojo.widget.HtmlWidget, {
    
	console.debug("EA | Inline widget initialized");
	widgetType: "toggleView",  	
  isContainer: true, // in the future the content could contain sub widgets
	templatePath: dojo.uri.dojoUri("/WebUI/widget/assistance/templates/inline.html"), // inline html template
	templateCssPath: dojo.uri.dojoUri("/WebUI/widget/assistance/templates/inline.css"), // inline css template
	content:"",		// content will be passed as a string
	moreTriggerString:"&hellip;more", // more trigger string - this could be passed from a resource bundle
	lessTriggerString:"&hellip;less", // less trigger string - this could be passed from a resource bundle
  isShowing: true, // create the widget shown
	showDelay: 500, // delay before it shows
	hideDelay: 200, // delay before it hides
	
	fillInTemplate: function(args, frag){
		
		console.debug("EA | " + this.widgetType+'['+this.widgetId+'].fillInTemplate()');
		
		// set the trigger content strings
		this.moreTrigger.innerHTML = this.moreTriggerString;
		this.lessTrigger.innerHTML = this.lessTriggerString;
		// set the excerpt text
		this.helpExcerpt.innerHTML = this.content.excerpt;
		// set the extended help text and then hide it
		this.helpContent.style.display = "none";
		this.helpContent.innerHTML = this.content.content;
		// hide the less trigger
		this.lessTrigger.style.display = "none";
		// if there is no content, we should disable the more button
		console..debug("EA | Content value: " + this.content.content + " ###");
		console..debug("EA | Content length: " + this.content.content.length + " ###");
		if (this.content.content.length == 0) {
		  this.moreTrigger.style.display = "none";
		}
		
	},
	
	onShowMore: function(e){
		
		this.lessTrigger.style.display = "inline";
		this.moreTrigger.style.display = "none";
		this.helpContent.style.display = "inline";
		dojo.html.setOpacity(this.helpContent, 0.0);	
		dojo.lfx.html.fadeShow(this.helpContent, 200, false, false).play();
		// squelch click event
		if (e.preventDefault){ e.preventDefault(); }			
	},
	
	onShowLess: function(e){
		
		this.lessTrigger.style.display = "none";
		this.moreTrigger.style.display = "inline";
		this.helpContent.style.display = "none";
		// squelch click event
		if (e.preventDefault){ e.preventDefault(); }			
	},
	
	
	destroy: function(){
	}	
});