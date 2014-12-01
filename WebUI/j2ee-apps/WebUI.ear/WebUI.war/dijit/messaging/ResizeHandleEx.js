dojo.provide("atg.widget.messaging.ResizeHandleEx");

/*
 * Define extension properties for resize handles
 */
atg.widget.messaging.ResizeHandleEx = {
  resizeDirection: "right", // for default left-justified rendering

	_beginSizing: function(/*Event*/ e){
		if (this._isSizing){ return false; }

		// get the target dom node to adjust.  targetElmId can refer to either a widget or a simple node
		this.targetWidget = dojo.widget.byId(this.targetElmId);
		this.targetDomNode = this.targetWidget ? this.targetWidget.domNode : dojo.byId(this.targetElmId);
		if (!this.targetDomNode){ return; }

		this._isSizing = true;
		this.startPoint  = {'x':e.clientX, 'y':e.clientY};
		var mb = dojo.html.getMarginBox(this.targetDomNode);
		this.startSize  = {'w':mb.width, 'h':mb.height};

		dojo.connect(
/*			srcObj: dojo.body(), 
			srcFunc: "onmousemove",
			targetObj: this,
			targetFunc: "_changeSizing",
			rate: 25
*/
			dojo.body(), "onmousemove",this,"_changeSizing",25
	  );
		dojo.connect(dojo.body(), "onmouseup", this, "_endSizing");
	  // do the same for all the visible iframes on the page
	  // otherwise, resizing will "freeze" over any iframe on the page
	  // this isn't in dojo 0.4.2
	  var iframes = document.getElementsByTagName("iframe");
	  for (var i = 0; i < iframes.length; i++) {
	    var frameBody = iframes[i].contentWindow.document.body;
  		dojo.connect(frameBody, "onmousemove", this, "_changeSizing",25);
  		dojo.connect(frameBody, "onmouseup", this, "_endSizing");
	  }

		e.preventDefault();
	},

  // Only one character is different from the replaced function
  // ('+' instead of '-' at 'var newW = this.startSize.w + dx;')
  // but unfortunately there is no finer granularity at which to change
  // the direction in which the floating dialog resizes
  _changeSizing: function(/*Event*/ e){
  	// On IE, if you move the mouse above/to the left of the object being resized,
  	// sometimes clientX/Y aren't set, apparently.  Just ignore the event.
  	try{
  		if(!e.clientX  || !e.clientY){ return; }
  	}catch(e){
  		// sometimes you get an exception accessing above fields...
  		return;
  	}
  	var dx = this.startPoint.x - e.clientX;
  	var dy = this.startPoint.y - e.clientY;
  	
  	var newW = (this.resizeDirection === "right") ? this.startSize.w - dx : this.startSize.w + dx;
  	var newH = this.startSize.h - dy;
  
  	// minimum size check
  	if (this.minSize) {
  		var mb = dojo.html.getMarginBox(this.targetDomNode);
  		if (newW < this.minSize.w) {
  			newW = mb.width;
  		}
  		if (newH < this.minSize.h) {
  			newH = mb.height;
  		}
  	}
  	
  	if(this.targetWidget){
  		this.targetWidget.resizeTo(newW, newH);
  	}else{
  		dojo.marginBox(this.targetDomNode, { w: newW, h: newH});
  	}
  	
  	e.preventDefault();
  },

	_endSizing: function(/*Event*/ e){
	  // TODO change these to use the handles returned from connect
		dojo.disconnect(dojo.body(), "onmousemove", this, "_changeSizing");
		dojo.disconnect(dojo.body(), "onmouseup", this, "_endSizing");
    // do same for all iframes
    // otherwise resizing will never stop over the iframe - see comment under _beginSizing
	  var iframes = document.getElementsByTagName("iframe");
	  for (var i = 0; i < iframes.length; i++) {
	    var frameBody = iframes[i].contentWindow.document.body;
  		dojo.disconnect(frameBody, "onmousemove", this, "_changeSizing");
  		dojo.disconnect(frameBody, "onmouseup", this, "_endSizing");
	  }


		this._isSizing = false;
	}
}
