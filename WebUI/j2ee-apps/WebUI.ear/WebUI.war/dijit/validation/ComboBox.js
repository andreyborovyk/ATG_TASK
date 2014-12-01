dojo.provide("atg.widget.validation.ComboBox");

dojo.require("dijit.form.ComboBox");
dojo.require("dijit.form.ValidationTextBox");

dojo.declare(
	"atg.widget.validation.ComboBoxMixin",
	[dijit.form.ComboBoxMixin],
	{
		
		templatePath: "/WebUI/dijit/validation/templates/ComboBox.html",
		

		_showResultList: function(){
			this._hideResultList();
			var items = this._popupWidget.getItems(),
				visibleCount = Math.min(items.length,this.maxListLength);
			this._arrowPressed();
			// hide the tooltip
			this._displayMessage("");
			
			// Position the list and if it's too big to fit on the screen then
			// size it to the maximum possible height
			// Our dear friend IE doesnt take max-height so we need to calculate that on our own every time
			// TODO: want to redo this, see http://trac.dojotoolkit.org/ticket/3272, http://trac.dojotoolkit.org/ticket/4108
			with(this._popupWidget.domNode.style){
				// natural size of the list has changed, so erase old width/height settings,
				// which were hardcoded in a previous call to this function (via dojo.marginBox() call) 
				width="";
				height="";
			}
			var best=this.open();
			// #3212: only set auto scroll bars if necessary
			// prevents issues with scroll bars appearing when they shouldn't when node is made wider (fractional pixels cause this)
			var popupbox=dojo.marginBox(this._popupWidget.domNode);
			this._popupWidget.domNode.style.overflow=((best.h==popupbox.h)&&(best.w==popupbox.w))?"hidden":"auto";
			// #4134: borrow TextArea scrollbar test so content isn't covered by scrollbar and horizontal scrollbar doesn't appear
			var newwidth=best.w;
			if(best.h<this._popupWidget.domNode.scrollHeight){newwidth+=16;}
			dojo.marginBox(this._popupWidget.domNode, {h:best.h,w:Math.max(newwidth,this.textbox.offsetWidth)});
		}
		
		
	}
);

		
		
dojo.declare(
	"atg.widget.validation.ComboBox",
	[dijit.form.ValidationTextBox, atg.widget.validation.ComboBoxMixin],
	{
		postMixInProperties: function(){
			atg.widget.validation.ComboBoxMixin.prototype.postMixInProperties.apply(this, arguments);
			dijit.form.ValidationTextBox.prototype.postMixInProperties.apply(this, arguments);
		}
	}
);