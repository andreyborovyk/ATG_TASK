/*
-----------------------------------------------------------------------------------
######## DEPRECATED - USE THE VERSION IN WEBUI/DIJIT/ASSISTANCE INSTEAD ###########
-----------------------------------------------------------------------------------
*/


/*
	EmbeddedAssistancePopup Widget v0.1
	
	- For showing various types of inline and hover context help.

***************************************************************************/
dojo.provide("atg.widget.assistance.popup");


dojo.widget.defineWidget("atg.widget.assistancePopup", dojo.widget.HtmlWidget,{

	isContainer: true, // help might contain other widgets in the future
	templatePath: dojo.uri.dojoUri("/WebUI/widget/assistance/templates/popup.html"), // popup html template
	templateCssPath: dojo.uri.dojoUri("/WebUI/widget/assistance/templates/popup.css"), // popup css template
	content:"",		// content that will be rendered in the help bubble
  isShowing: true, // initial setting that it should be created visible
	position: {},  // expecting a position object of coordinates
	showDelay: 300, // ms delay before help pops up
	hideDelay: 200, // ms delay before it closes (used in rolling from icon to the bubble)
	orient:'TL,TR,BL,BR', // Orientation preferences
		
	fillInTemplate: function(args, frag){
		
		console.debug(this.widgetType+'['+this.widgetId+'].fillInTemplate()');
		
		
		this.helpContent.innerHTML = this.content;
		
		this.show(this.position);
		
		var _this = this;
		
		dojo.event.connect(this.helpContent, "onmouseover", function(evt) { 
			
			if(_this._hideTimer) {
				clearTimeout(_this._hideTimer);
				delete _this._hideTimer;
			}
			
		});
		
		dojo.event.connect(this.helpContent, "onmouseout", this, "hide");
		
		this.domNode.id = this.widgetId;

	},

	initialize: function(){
	},

	uninitialize: function(){
	},
	
	show: function(position){
		
		this.position = position;
		
		if(this._hideTimer) {
			clearTimeout(this._hideTimer);
			delete this._hideTimer;
		}
		
		if(!this.isShowingNow && !this._showTimer){
			this._showTimer = setTimeout(dojo.lang.hitch(this, "open"), this.showDelay);
			this.domNode.style.display = "block";
			this.domNode.style.top = "0px";
			this.domNode.style.left = "0px";
			
			this.domNode.style.visibility = "hidden";
		}
		
	},
	
	hide: function(){
		if(this._showTimer){
			clearTimeout(this._showTimer);
			delete this._showTimer;
		}
		if(!this._hideTimer){
			this._hideTimer = setTimeout(dojo.lang.hitch(this, "close"), this.hideDelay);
		}
	},
		
	
	open: function(){

		this.positioned = (dojo.html.placeOnScreen(this.domNode, this.position.x, this.position.y, [0, 0], false, this.orient));
		
		
		switch (this.positioned.corner){
			case 'TL':
				this.domNode.style.top = this.positioned.y+2+this.position.height+"px";
				this.domNode.style.left = (this.positioned.x+this.position.width)-36-(this.position.width/2)+"px";
			break;
			case 'TR':
				this.domNode.style.top = this.positioned.y+2+this.position.height+"px";
				this.domNode.style.left = (this.positioned.x+this.position.width)+18+(this.position.width/2)+"px";
			break;
			case 'BL':
				this.domNode.style.left = (this.positioned.x+this.position.width)-37-(this.position.width/2)+"px";
				if(!dojo.html.hasClass(this.domNode, "ATG_EA_BL")){
					this.domNode.style.top = this.positioned.y-8+"px";
				}
			break;
			case 'BR':
				this.domNode.style.left = (this.positioned.x+this.position.width)+18+(this.position.width/2)+"px";
				if(!dojo.html.hasClass(this.domNode, "ATG_EA_BR")){
					this.domNode.style.top = this.positioned.y-8+"px";
				}
			break;

		}
		
		
		dojo.html.setClass(this.domNode, 'ATG_EA_popup ATG_EA_'+this.positioned.corner);
		
		//dojo.html.setOpacity(this.domNode, 0.0);	
		this.domNode.style.visibility = "visible";
		//dojo.lfx.html.fadeShow(this.domNode, 300, false, false).play();
		
		if(dojo.render.html.ie){
			if(!this.bgIframe){
				this.bgIframe = new dojo.html.BackgroundIframe();
				this.bgIframe.setZIndex(this.domNode);
			}

			this.bgIframe.size(this.domNode);
			this.bgIframe.show();
		}
		
		
	},
	
	close: function(){
		this.domNode.style.display = "none";
		if(this.bgIframe){
			this.bgIframe.remove();
			this.bgIframe = false;
		}
		
	},
	
	
	destroy: function(){
		
	}
});