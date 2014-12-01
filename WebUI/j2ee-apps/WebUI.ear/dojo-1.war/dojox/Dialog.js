if(!dojo._hasResource["dojox.Dialog"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["dojox.Dialog"] = true;
dojo.provide("dojox.Dialog");

dojo.require("dojox.layout.ContentPane");
dojo.require("dijit.Dialog");

dojo.declare(
	"dojox.Dialog",
	[dojox.layout.ContentPane, dijit._Templated, dijit.form._FormMixin],
	{
		// summary: A modal dialog Widget
		//
		// description:
		//	Pops up a modal dialog window, blocking access to the screen
		//	and also graying out the screen Dialog is extended from
		//	ContentPane so it supports all the same parameters (href, etc.)
		//
		// example:
		// |	<div dojoType="dijit.Dialog" href="test.html"></div>
		//
		// example:
		// |	<div id="test">test content</div>
		// |	...
		// |	var foo = new dijit.Dialog({ title: "test dialog" },dojo.byId("test"));
		// |	foo.startup();
		
		templateString: null,
		templateString:"<div class=\"dijitDialog\">\r\n\t<div dojoAttachPoint=\"titleBar\" class=\"dijitDialogTitleBar\" tabindex=\"0\" waiRole=\"dialog\">\r\n\t<span dojoAttachPoint=\"titleNode\" class=\"dijitDialogTitle\">${title}</span>\r\n\t<span dojoAttachPoint=\"closeButtonNode\" class=\"dijitDialogCloseIcon\" dojoAttachEvent=\"onclick: hide\">\r\n\t\t<span dojoAttachPoint=\"closeText\" class=\"closeText\">x</span>\r\n\t</span>\r\n\t</div>\r\n\t\t<div dojoAttachPoint=\"containerNode\" class=\"dijitDialogPaneContent\"></div>\r\n\t<span dojoAttachPoint=\"tabEnd\" dojoAttachEvent=\"onfocus:_cycleFocus\" tabindex=\"0\"></span>\r\n</div>\r\n",

		// open: Boolean
		//		is True or False depending on state of dialog
		open: false,

		// duration: Integer
		//		The time in milliseconds it takes the dialog to fade in and out
		duration: 100,

		// _lastFocusItem: DomNode
		//		The pointer to which node has focus prior to our dialog
		_lastFocusItem:null,

		attributeMap: dojo.mixin(dojo.clone(dijit._Widget.prototype.attributeMap), {title: "titleBar"}),

		postCreate: function(){
			dojo.body().appendChild(this.domNode);
			this.inherited("postCreate",arguments);
			this.domNode.style.display="none";
			this.connect(this, "onExecute", "hide");
			this.connect(this, "onCancel", "hide");
		},

		onLoad: function(){
			// summary: when href is specified we need to reposition the dialog after the data is loaded
			this._position();
			this.inherited("onLoad",arguments);
		},

		_setup: function(){
			// summary:
			//		stuff we need to do before showing the Dialog for the first
			//		time (but we defer it until right beforehand, for
			//		performance reasons)

			this._modalconnects = [];

			if(this.titleBar){
				this._moveable = new dojo.dnd.Moveable(this.domNode, { handle: this.titleBar });
			}

			this._underlay = new dijit.DialogUnderlay();

			var node = this.domNode;
			this._fadeIn = dojo.fx.combine(
				[dojo.fadeIn({
					node: node,
					duration: this.duration
				 }),
				 dojo.fadeIn({
					node: this._underlay.domNode,
					duration: this.duration,
					onBegin: dojo.hitch(this._underlay, "show")
				 })
				]
			);

			this._fadeOut = dojo.fx.combine(
				[dojo.fadeOut({
					node: node,
					duration: this.duration,
					onEnd: function(){
						node.style.display="none";
					}
				 }),
				 dojo.fadeOut({
					node: this._underlay.domNode,
					duration: this.duration,
					onEnd: dojo.hitch(this._underlay, "hide")
				 })
				]
			);
		},

		uninitialize: function(){
			if(this._fadeIn.status() == "playing"){
				this._fadeIn.stop();
			}
			if(this._fadeOut.status() == "playing"){
				this._fadeOut.stop();
			}
			if(this._underlay){
				this._underlay.destroy();
			}
		},

		_position: function(){
			// summary: position modal dialog in center of screen
			
			if(dojo.hasClass(dojo.body(),"dojoMove")){ return; }
			var viewport = dijit.getViewport();
			var mb = dojo.marginBox(this.domNode);

			var style = this.domNode.style;
			style.left = Math.floor((viewport.l + (viewport.w - mb.w)/2)) + "px";
			style.top = Math.floor((viewport.t + (viewport.h - mb.h)/2)) + "px";
		},

		_findLastFocus: function(/*Event*/ evt){
			// summary:  called from onblur of dialog container to determine the last focusable item
			this._lastFocused = evt.target;
		},

		_cycleFocus: function(/*Event*/ evt){
			// summary: when tabEnd receives focus, advance focus around to titleBar

			// on first focus to tabEnd, store the last focused item in dialog
			if(!this._lastFocusItem){
				this._lastFocusItem = this._lastFocused;
			}
			this.titleBar.focus();
		},

		_onKey: function(/*Event*/ evt){
			// summary: handles the keyboard events for accessibility reasons
			if(evt.keyCode){
				var node = evt.target;
				// see if we are shift-tabbing from titleBar
				if(node == this.titleBar && evt.shiftKey && evt.keyCode == dojo.keys.TAB){
					if(this._lastFocusItem){
						this._lastFocusItem.focus(); // send focus to last item in dialog if known
					}
					dojo.stopEvent(evt);
				}else{
					// see if the key is for the dialog
					while(node){
						if(node == this.domNode){
							if(evt.keyCode == dojo.keys.ESCAPE){
								this.hide();
							}else{
								return; // just let it go
							}
						}
						node = node.parentNode;
					}
					// this key is for the disabled document window
					if(evt.keyCode != dojo.keys.TAB){ // allow tabbing into the dialog for a11y
						dojo.stopEvent(evt);
					// opera won't tab to a div
					}else if (!dojo.isOpera){
						try{
							this.titleBar.focus();
						}catch(e){/*squelch*/}
					}
				}
			}
		},

		show: function(){
			// summary: display the dialog

			// first time we show the dialog, there's some initialization stuff to do			
			if(!this._alreadyInitialized){
				this._setup();
				this._alreadyInitialized=true;
			}

			if(this._fadeOut.status() == "playing"){
				this._fadeOut.stop();
			}

			this._modalconnects.push(dojo.connect(window, "onscroll", this, "layout"));
			this._modalconnects.push(dojo.connect(document.documentElement, "onkeypress", this, "_onKey"));

			// IE doesn't bubble onblur events - use ondeactivate instead
			var ev = typeof(document.ondeactivate) == "object" ? "ondeactivate" : "onblur";
			this._modalconnects.push(dojo.connect(this.containerNode, ev, this, "_findLastFocus"));

			dojo.style(this.domNode, "opacity", 0);
			this.domNode.style.display="block";
			this.open = true;
			this._loadCheck(); // lazy load trigger

			this._position();

			this._fadeIn.play();

			this._savedFocus = dijit.getFocus(this);

			// set timeout to allow the browser to render dialog
			setTimeout(dojo.hitch(this, function(){
				dijit.focus(this.titleBar);
			}), 50);
		},

		hide: function(){
			// summary: Hide the dialog

			// if we haven't been initialized yet then we aren't showing and we can just return
			if(!this._alreadyInitialized){
				return;
			}

			if(this._fadeIn.status() == "playing"){
				this._fadeIn.stop();
			}
			this._fadeOut.play();

			if (this._scrollConnected){
				this._scrollConnected = false;
			}
			dojo.forEach(this._modalconnects, dojo.disconnect);
			this._modalconnects = [];

			this.connect(this._fadeOut,"onEnd",dojo.hitch(this,function(){
				dijit.focus(this._savedFocus);
			}));
			this.open = false;
		},

		layout: function() {
			// summary: position the Dialog and the underlay
			if(this.domNode.style.display == "block"){
				this._underlay.layout();
				this._position();
			}
		}
	}
);

dojo.declare(
	"dijit.TooltipDialog",
	[dojox.layout.ContentPane, dijit._Templated, dijit.form._FormMixin],
	{
		// summary:
		//		Pops up a dialog that appears like a Tooltip
		//
		// title: String
		// 		Description of tooltip dialog (required for a11Y)
		title: "",

		// _lastFocusItem: DomNode
		//		The domNode that had focus before we took it.
		_lastFocusItem: null,

		templateString: null,
		templateString:"<div class=\"dijitTooltipDialog\" >\r\n\t<div class=\"dijitTooltipContainer\">\r\n\t\t<div class =\"dijitTooltipContents dijitTooltipFocusNode\" dojoAttachPoint=\"containerNode\" tabindex=\"0\" waiRole=\"dialog\"></div>\r\n\t</div>\r\n\t<span dojoAttachPoint=\"tabEnd\" tabindex=\"0\" dojoAttachEvent=\"focus:_cycleFocus\"></span>\r\n\t<div class=\"dijitTooltipConnector\" ></div>\r\n</div>\r\n",

		postCreate: function(){
			this.inherited("postCreate",arguments);
			this.connect(this.containerNode, "onkeypress", "_onKey");

			// IE doesn't bubble onblur events - use ondeactivate instead
			var ev = typeof(document.ondeactivate) == "object" ? "ondeactivate" : "onblur";
			this.connect(this.containerNode, ev, "_findLastFocus");
			this.containerNode.title=this.title;
		},

		orient: function(/*Object*/ corner){
			// summary: configure widget to be displayed in given position relative to the button
			this.domNode.className="dijitTooltipDialog " +"dijitTooltipAB"+(corner.charAt(1)=='L'?"Left":"Right")+"dijitTooltip"+(corner.charAt(0)=='T' ? "Below" : "Above");
		},

		onOpen: function(/*Object*/ pos){
			// summary: called when dialog is displayed
			this.orient(pos.corner);
			this._loadCheck(); // lazy load trigger
			this.containerNode.focus();
		},

		_onKey: function(/*Event*/ evt){
			// summary: keep keyboard focus in dialog; close dialog on escape key
			if(evt.keyCode == dojo.keys.ESCAPE){
				this.onCancel();
			}else if(evt.target == this.containerNode && evt.shiftKey && evt.keyCode == dojo.keys.TAB){
				if (this._lastFocusItem){
					this._lastFocusItem.focus();
				}
				dojo.stopEvent(evt);
			}else if(evt.keyCode == dojo.keys.TAB){
				// we want the browser's default tab handling to move focus
				// but we don't want the tab to propagate upwards
				evt.stopPropagation();
			}
		},

		_findLastFocus: function(/*Event*/ evt){
			// summary: called from onblur of dialog container to determine the last focusable item
			this._lastFocused = evt.target;
		},

		_cycleFocus: function(/*Event*/ evt){
			// summary: when tabEnd receives focus, advance focus around to containerNode

			// on first focus to tabEnd, store the last focused item in dialog
			if(!this._lastFocusItem){
				this._lastFocusItem = this._lastFocused;
			}
			this.containerNode.focus();
		}
	}	
);

}
