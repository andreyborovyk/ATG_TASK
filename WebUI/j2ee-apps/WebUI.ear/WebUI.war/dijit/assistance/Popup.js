/*
---------------------------------------------------------------------
Embedded Assistance (EA) Popup Widget
Used to show Unobtrusive (Popup) EA
---------------------------------------------------------------------
*/

dojo.provide("atg.widget.assistance.Popup");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare("atg.widget.assistance.Popup", [dijit._Widget,dijit._Templated],{

	isContainer: true, // help might contain other widgets in the future
	templatePath: "/WebUI/dijit/assistance/templates/popup.html", // popup html template
	content:"",		// content that will be rendered in the help bubble
	isShowing: true, // initial setting that it should be created visible
	position: {},  // expecting a position object of coordinates
	showDelay: 300, // ms delay before help pops up
	hideDelay: 200, // ms delay before it closes (used in rolling from icon to the bubble)
	//orient:["TL","TR","BL","BR"], // Orientation preferences
	orient: {'BL': 'BR', 'BR': 'BL', 'TL': 'TR', 'TR': 'TL'}, // Orientation preferences

	postCreate: function(){

		this.helpContent.innerHTML = this.content;
				
    // use Widget.connect to get auto cleanup of connected events on destroy
		this.connect(this.helpContent, "onmouseover", "_clearHideTimer");

		this.connect(this.helpContent, "onmouseout", "hide");

		this.domNode.id = this.id;

	},

	_clearHideTimer: function(evt) { 

    if(this._hideTimer) {
      clearTimeout(this._hideTimer);
      delete this._hideTimer;
    }

  },
	
	show: function(position){

		this.position = position;

		if(this._hideTimer) {
			clearTimeout(this._hideTimer);
			delete this._hideTimer;
		}

		if(!this.isShowingNow && !this._showTimer){
			this._showTimer = setTimeout(dojo.hitch(this, "open"), this.showDelay);
			this.domNode.style.display = "block";
			this.domNode.style.top = "-1000px";
			this.domNode.style.left = "-1000px";

			this.domNode.style.visibility = "hidden";
		}

	},

	hide: function(){
		if(this._showTimer){
			clearTimeout(this._showTimer);
			delete this._showTimer;
		}
		if(!this._hideTimer){
			this._hideTimer = setTimeout(dojo.hitch(this, "close"), this.hideDelay);
		}
	},


	open: function(){
	
		this.domNode.style.visibility = "visible";

		this.positioned = dijit.placeOnScreenAroundElement(this.domNode, this.target, this.orient);
		this.domNode.className='ATG_EA_popup ATG_EA_'+this.positioned.corner;
		
		// ToDo have to call this twice since the class makes teh positioning off the first time
		// There must be a better solution than this.
		this.positioned = dijit.placeOnScreenAroundElement(this.domNode, this.target, this.orient);
		
		
		switch (this.positioned.corner){
			case 'TL':
				this.domNode.style.left = (this.positioned.x-44)+"px";
			break;
			case 'TR':
				this.domNode.style.left = (this.positioned.x+32)+"px";
			break;
			case 'BL':
				this.domNode.style.left = (this.positioned.x-44)+"px";
				this.domNode.style.top = (this.positioned.y-8)+"px";
			break;
			case 'BR':
				this.domNode.style.left = (this.positioned.x+32)+"px";
				this.domNode.style.top = (this.positioned.y-8)+"px";
			break;

		}
		
		//dojo.html.setOpacity(this.domNode, 0.0);
		//dojo.lfx.html.fadeShow(this.domNode, 300, false, false).play();
		
		this.bgIframe = new dijit.BackgroundIframe(this.domNode);
		
	},

	close: function(){
		this.domNode.style.display = "none";
		if(this.bgIframe){
			this.bgIframe.destroy();
			this.bgIframe = false;
		}
		
	}

});
