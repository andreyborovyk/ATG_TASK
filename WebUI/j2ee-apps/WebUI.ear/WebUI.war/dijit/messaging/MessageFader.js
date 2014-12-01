dojo.provide("atg.widget.messaging.MessageFader");
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit._Container");
dojo.require("dijit._Templated");
dojo.require("dojox.layout.ResizeHandle"); 

dojo.declare(
  "atg.widget.messaging.MessageFader",
  [dijit.layout.ContentPane,dijit._Container,dijit._Templated],
  {
    // resizeAxis: String
    //    x | xy | y to limit pane's sizing direction
    resizeAxis: "xy",

    // duration: Integer
    //    time is MS to spend toggling in/out node
    duration: 200,

    // animation holders for toggle
    _showAnim: null,
    _hideAnim: null, 

    templateString: null,

    templatePath: "/WebUI/dijit/messaging/template/MessageFader.html",

    setMessage: function(/*String*/ msg) {
      while (this.containerNode.childNodes.length > 0) {
        dojo._destroyElement(this.containerNode.childNodes[0]); // use destroyNode to prevent IE memory leak
      }
      var msgItem = new atg.widget.messaging.MessageItem(
        {message: msg, cssClass: "atg_messaging_currentMessage"});
      this.containerNode.appendChild(msgItem.domNode);
      this.show();
      window.setTimeout("dijit.byId(\"" + this.id + "\").hide()", 5000);
    },

    setMessages: function(/*Array*/ messages) {
      while (this.containerNode.childNodes.length > 0) {
        dojo._destroyElement(this.containerNode.childNodes[0]); // use destroyNode to prevent IE memory leak
      }
      for (var i = 0; i < messages.length; i++) {
	      var msgItem = new atg.widget.messaging.MessageItem(
		      {message: messages[i], cssClass: "atg_messaging_currentMessage"});
		    this.containerNode.appendChild(msgItem.domNode);
      }
      this.show();
      window.setTimeout("dijit.byId(\"" + this.id + "\").hide()", 5000);
    },

    postCreate: function(){
      // summary: 
      this.inherited("postCreate",arguments);
			console.debug(this);

      //var foo = dojo.marginBox(this.domNode);
      //this.domNode.style.width = foo.w+"px";

    },

    startup: function(){
      this.inherited("startup",arguments);

      //if(dojo.isIE){
      //  this.canvas.style.overflow = "auto";
      //} else {
        this.containerNode.style.overflow = "auto";
      //}
      var tmp = new dojox.layout.ResizeHandle({
        //targetContainer: this.containerNode,
        targetId: this.id,
        resizeAxis: this.resizeAxis
      },this.resizeHandle);

    },

    hide: function(/* Function? */ callback){
      //console.debug("MessageFader::hide");
      // summary: close but do not destroy this widget
			//this.bgIframe.hide();

      dojo.fadeOut({
        node:this.domNode,
        duration:this.duration,
        onEnd: dojo.hitch(this,function() {
          this.domNode.style.display = "none";
          this.domNode.style.visibility = "hidden";
          if(callback){
            callback();
          }
        })
      }).play();

    },

    show: function(/* Function? */callback){
			/* These are needed to overwrite the content panel positioning */
			this.domNode.style.left = "auto";
			this.domNode.style.right = "0px";
	
      //console.debug("MessageFader::show");
      // summary: show the FloatingPane
      var anim = dojo.fadeIn({node:this.domNode, duration:this.duration,
        beforeBegin: dojo.hitch(this,function(){
          this.domNode.style.display = "block";
          this.domNode.style.visibility = "visible";
          if (typeof callback == "function") { callback(); }
        })
      }).play();
      //this.resize(dojo.coords(this.domNode));
			
			if(dojo.isIE){
				if(!this.bgIframe){
					this.bgIframe = new dijit.BackgroundIframe(this.domNode);
				}
			}
			
			
    },

    resize: function(/* Object */dim){
      //console.debug("MessageFader::resize");
      // summary: size the widget and place accordingly
      this._currentState = dim;
      var dns = this.domNode.style;

      dns.top = dim.t+"px";
      dns.left = dim.l+"px";

      dns.width = dim.w+"px"; 
      this.canvas.style.width = dim.w+"px";

      dns.height = dim.h+"px";
      this.canvas.style.height = (dim.h)+"px";
    },

    destroy: function(){
      // summary: Destroy this FloatingPane completely
      this.inherited("destroy", arguments);
    }

});
