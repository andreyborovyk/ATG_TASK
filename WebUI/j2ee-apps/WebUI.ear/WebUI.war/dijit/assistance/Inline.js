/*
---------------------------------------------------------------------
Embedded Assistance (EA) Inline Widget
Used to show Conspicuous (Inline) EA
---------------------------------------------------------------------
*/

dojo.provide("atg.widget.assistance.Inline");
dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("atg.widget.assistance.Inline", [dijit._Widget, dijit._Templated], {

	widgetType: "toggleView", 
	isContainer: true, // in the future the content could contain sub widgets
	templatePath: "/WebUI/dijit/assistance/templates/inline.html", // inline html template
	content:"",		// content will be passed a sa string
	moreTriggerString:"&hellip;more", // more trigger string - this could be passed from a resource bundle
	lessTriggerString:"&hellip;less", // less trigger string - this could be passed from a resource bundle
	isShowing: true, // create the widget shown
	showDelay: 500, // delay before it shows
	hideDelay: 200, // delay before it hides

	postCreate: function(){
		this.widgetId = this.id;

		//console.debug("EA | " + this.widgetType+'['+this.widgetId+'].fillInTemplate()');

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
		if (this.content.content.length == 0) {
		  this.moreTrigger.style.display = "none";
		}
	},

	onShowMore: function(e){

		this.lessTrigger.style.display = "inline";
		this.moreTrigger.style.display = "none";
		this.helpContent.style.display = "inline";

		if (e.preventDefault){ e.preventDefault(); }			
	},

	onShowLess: function(e){

		this.lessTrigger.style.display = "none";
		this.moreTrigger.style.display = "inline";
		this.helpContent.style.display = "none";
		// squelch click event
		if (e.preventDefault){ e.preventDefault(); }
	}


});
