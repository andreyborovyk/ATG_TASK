dojo.provide("atg.widget.messaging.MessagePane");
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.layout._LayoutWidget");
dojo.require("atg.widget.messaging.MessageArea");
dojo.require("atg.widget.messaging.MessageItem");

dojo.declare(
  "atg.widget.messaging.MessagePane",
  [dijit.layout.ContentPane,dijit._Container,dijit._Templated],
  {
    // resizeAxis: String
    //    x | xy | y to limit pane's sizing direction
    resizeAxis: "xy",

    // duration: Integer
    //    time is MS to spend toggling in/out node
    duration: 500,

    // animation holders for toggle
    _showAnim: null,
    _hideAnim: null, 

    contentClass: "dojoxFloatingPaneContent",

    templateString: null,

    templatePath: "/WebUI/dijit/messaging/template/MessagePane.html",

    initialMessage: "",

    postCreate: function(){
      this.inherited("postCreate",arguments);
			console.debug(this);

      // Initial message
      if (dojo.trim(this.initialMessage).length != 0) {
        this.initialElement = document.createElement("div");
        this.initialElement.className = "atg_messaging_initialElement";
        this.initialElement.innerHTML = this.initialMessage;
        this.containerNode.appendChild(this.initialElement);
      }

      //var foo = dojo.marginBox(this.domNode);
      //this.domNode.style.width = foo.w+"px";

    },

    /**
     * Adds the passed in messages to the current message area and
     * pushes the messages currently in the current message area to
     * the history area.
     */
    addMessages: function(/* array */ messages) {
      console.debug("Adding messages to pane", messages);
      if (this.initialElement) {
        dojo._destroyElement(this.initialElement); // use destroyNode to prevent IE memory leak
        this.initialElement = null;
        delete this.initialElement;
      }
      if (!this.currentArea) {
        this.currentArea = new atg.widget.messaging.MessageArea(
          {cssClass: "atg_messaging_currentMessages"}
        );
        this.containerNode.appendChild(this.currentArea.domNode);
      }
      this.moveCurrentMessagesToHistory();
      if (dojo.isArray(messages)) {
        for (var i = 0, l = messages.length; i < l; i++) {
          this.addMessage(messages[i]);
        }
        this.addedMessages = true;
      }
    },

    addMessage: function(msg) {
      console.debug("Adding message to message area", msg);
      var msgItem = new atg.widget.messaging.MessageItem(
        {message: msg, cssClass: "atg_messaging_currentMessage"}
      );
      if (this.currentArea) {
        this.currentArea.addMessageItems([msgItem.domNode]);
      }
    },

    moveCurrentMessagesToHistory: function() {
      this.prepareHistoryArea();
      if (this.historyArea && this.currentArea) {
        this.historyArea.addMessageItems(this.currentArea.removeMessageItems());
      }
    },

    prepareHistoryArea: function() {
      if (!this.historyArea && this.addedMessages) { // add history first time
        this.historyArea = new atg.widget.messaging.MessageArea(
          {cssClass: "atg_messaging_recentMessages"}
        );
        this.containerNode.appendChild(this.historyArea.domNode);
        delete this.addedMessages; // remove flag, not used any more
      }
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
      //console.debug("MessagePane::hide");
      // summary: close but do not destroy this widget
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
			
      //console.debug("MessagePane::show");
      // summary: show the FloatingPane
      var anim = dojo.fadeIn({node:this.domNode, duration:this.duration,
        beforeBegin: dojo.hitch(this,function(){
          this.domNode.style.display = "block";
          this.domNode.style.visibility = "visible";
          if (typeof callback == "function") { callback(); }
        })
      }).play();

			if(dojo.isIE){
				if(!this.bgIframe){
					this.bgIframe = new dijit.BackgroundIframe(this.domNode);
				}
			}
			
    },

    resize: function(/* Object */dim){
      //console.debug("MessagePane::resize");
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
