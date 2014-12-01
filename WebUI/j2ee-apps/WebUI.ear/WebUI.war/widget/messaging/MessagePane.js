dojo.provide("atg.widget.messaging.MessagePane");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.FloatingPane");

dojo.widget.defineWidget(
  "atg.widget.messaging.MessagePane",
  dojo.widget.FloatingPane,
  {
    isContainer: true,
    extraTemplateCssPath: dojo.uri.dojoUri("/WebUI/widget/messaging/MessagePane.css"),
    resizeDirection: "right",
    initialMessage: "",

		fillInTemplate: function(/*Object*/ args, /*Object*/ frag) {
  		atg.widget.messaging.MessagePane.superclass.fillInTemplate.call(this, args, frag);
      atg.widget.messaging.loadCss(this, this["extraTemplateCssPath"]);
		},
    postCreate: function() {
      atg.widget.messaging.MessagePane.superclass.postCreate.apply(this, arguments);

      // Tweak our resize handle for sizing to left or right
      dojo.lang.mixin(this.resizeHandle, atg.widget.messaging.ResizeHandleEx);

      // Support resizing of floating panes in the non-default direction
      if (this.resizeDirection === "left") {
        this.resizeHandle.domNode.style.background = "url(/WebUI/widget/messaging/grabCornerLeft.gif)";
        this.resizeHandle.domNode.style.cursor = "ne-resize";
        this.resizeHandle.domNode.style.float = "left";
        this.resizeHandle.domNode.style.left = "-10px";
        this.resizeBar.style.overflow = "visible";
      }
      if (this.resizeDirection === "left" || this.resizeDirection === "right") {
        this.resizeHandle.resizeDirection = this.resizeDirection;
      }

      // Point all the dojo CSS classes to our own CSS classes the first time
      this.titleBar.className = "atg_messaging_detailPaneTitleBar";
      this.domNode.className = "atg_messaging_detailPane";
      this.containerNode.className = "atg_messaging_detailPaneClient";
      this.resizeBar.className = "atg_messaging_detailPaneResizebar";

      // Initial message
      if (!dojo.string.isBlank(this.initialMessage)) {
        this.initialElement = document.createElement("div");
        this.initialElement.className = "atg_messaging_initialElement";
        this.initialElement.innerHTML = this.initialMessage;
        this.containerNode.appendChild(this.initialElement);
      }
      // Force IE to do layout positioning
      if(dojo.render.html.ie55 || dojo.render.html.ie60) {
  			dojo.widget.html.layout(this.domNode,
  				[
  				  {domNode: this.titleBar, layoutAlign: "top"},
  				  {domNode: this.resizeBar, layoutAlign: "bottom"},
  				  {domNode: this.containerNode, layoutAlign: "client"}
  				] );
  			dojo.widget.html.layout(this.containerNode, this.children, "top-bottom");
      }
    },

    /**
     * Adds the passed in messages to the current message area and
     * pushes the messages currently in the current message area to
     * the history area.
     */
    addMessages: function(/* array */ messages) {
      dojo.debug("Adding messages to pane", messages);
      if (this.initialElement) {
        dojo.dom.destroyNode(this.initialElement); // use destroyNode to prevent IE memory leak
        this.initialElement = null;
        delete this.initialElement;
      }
      if (!this.currentArea) {
        this.currentArea = dojo.widget.createWidget("messaging:MessageArea",
          {cssClass: "atg_messaging_currentMessages"});
        this.containerNode.appendChild(this.currentArea.domNode);
      }
      this.moveCurrentMessagesToHistory();
      if (dojo.lang.isArray(messages)) {
        for (var i = 0, l = messages.length; i < l; i++) {
          this.addMessage(messages[i]);
        }
        this.addedMessages = true;
      }
    },

    addMessage: function(msg) {
      dojo.debug("Adding message to message area", msg);
      var msgItem = dojo.widget.createWidget("messaging:MessageItem", 
        {message: msg, cssClass: "atg_messaging_currentMessage"});
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
        this.historyArea = dojo.widget.createWidget("messaging:MessageArea",
          {cssClass: "atg_messaging_recentMessages"});
        this.containerNode.appendChild(this.historyArea.domNode);
        delete this.addedMessages; // remove flag, not used any more
      }
    }
  }
);
