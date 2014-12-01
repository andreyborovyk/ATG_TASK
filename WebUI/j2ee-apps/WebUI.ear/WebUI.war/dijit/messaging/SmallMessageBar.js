dojo.provide("atg.widget.messaging.SmallMessageBar");
dojo.require("dojox.layout.ContentPane");
dojo.require("dijit._Templated");
dojo.require("dijit._Container");
dojo.require("atg.widget.messaging.MessageBar");
dojo.require("dojo.date");

/*
 * Small message bar
 */
dojo.declare(
  // widget name and class
  "atg.widget.messaging.SmallMessageBar",

  // superclass
  [dijit._Widget,atg.widget.messaging.MessagingUIBase,dijit._Templated,dijit._Container],

  // properties and methods
  {
    // constants
    classPrefix: "atg_messaging_",
    iconSuffix: "Icon",
    textSuffix: "Text",

    // settings
    widgetType: "SmallMessageBar",
    templatePath: "/WebUI/dijit/messaging/template/SmallMessageBar.html",

    // user properties
    cssfloat: "",
    initialText: "",
    preArchiveSeconds: 20, // Number of seconds to wait before archiving the current messages in message history
    targetDialog: "", // ID of dialog for messaging details
    targetFader: "", // ID of widget for messaging fader effects
    togglerCloseImg: "/WebUI/images/messaging-common/icon_messageboxHide.gif",
    togglerOpenImg: "/WebUI/images/messaging-common/icon_messageboxShow.gif",

    // callbacks
    postCreate: function() {
      //console.log("SmallMessageBar: postCreate");
      this.inherited('postCreate', arguments);

      var targetDlg = dijit.byId(this.targetDialog);
      if (targetDlg && targetDlg.hide) {
        targetDlg.hide();
      }
      this.toggler.src = this.togglerOpenImg;
    },

    // clicking on the bar
    barClick: function(e) {
      if (this.debug) console.debug("SmallMessageBar::barClick");
      var targetDlg = dijit.byId(this.targetDialog);
      if (targetDlg) {
        //console.log("found targetDlg " + targetDlg);
        // Click handler MUST be disabled during wipe-in/wipe-out times!
        if (!this._barClick_fn) {
          this._barClick_fn = this.barClick;
          this.barClick = function (){};
        }
        if (dojo.style(targetDlg.domNode, "display") != "none") {
          //console.log("targetDlg was showing, wipeOut targetDlg");
          targetDlg.hide();
          this.toggler.src = this.togglerOpenImg;
          //console.log("toggling class on textNode");
          this.textNode.className = "atg_messaging_smallMessageBar";
          targetDlg.moveCurrentMessagesToHistory();
        }
        else {
          //console.log("targetDlg was hidden");
          if (!this.__init) {
            //console.log("calling show");
            targetDlg.show();
            this.__init = true;
          }
          //console.log("wipeIn targetDlg");
          targetDlg.show();
          var targetFader = dijit.byId(this.targetFader);
          if (targetFader) {
            //console.log("found targetFader, hiding it");
            targetFader.hide();
          }
          this.toggler.src = this.togglerCloseImg;
        }
        setTimeout(dojo.hitch(this, "enableBarClick"), 500);
      }
      this.onClick(e);
    },

    onClick: function(e) {
      if (this.debug) console.log("SmallMessageBar: onClick");
      // summary: callback for when button is clicked; user can override this function
    },

    enableBarClick: function() {
      if (this.debug) console.log("SmallMessageBar: enableBarClick");
      if (this._barClick_fn) {
        this.barClick = this._barClick_fn;
        this._barClick_fn = null;
        delete this._barClick_fn;
      }
    },

    clearOldMessages: function() {
      if (this.debug) console.log("SmallMessageBar: clearOldMessages");
      if (this.messages.length > 0) {
        var msg = this.messages[this.messages.length - 1];
        if (msg && msg.datetime) {
          var timeDiff = dojo.date.difference(msg.datetime, new Date(), "second");
          if (timeDiff > this.preArchiveSeconds) {
            console.debug("Archiving current messages");
            dojo.toggleClass(this.textNode, "atg_messaging_smallMessageBar",true);
            var targetDlg = dijit.byId(this.targetDialog);
            if (targetDlg) {
              targetDlg.moveCurrentMessagesToHistory();
            }
          }
        }
      }
    },

    refresh: function(newCount) {
      if (this.debug) console.log("SmallMessageBar: refresh");
      if (this.messages.length > 0) {
	      var highlightedCount = 1;
	      if (newCount && newCount <= this.messages.length) { highlightedCount = newCount; }
      	var newMessages = [];
      	var type = "information";
        for (var i = this.messages.length - highlightedCount; i < this.messages.length; i++) {
        	newMessages.push(this.messages[i]);
          // pick the highest priority type in order: error, warning, confirmation, info (default)
        	if (this.messages[i].type == "error") {
        		type = "error";
        	}
        	else if (type != "error" && this.messages[i].type == "warning") {
        		type = "warning";
        	}
        	else if (type != "error" && type != "warning" && this.messages[i].type == "confirmation") {
        		type = "confirmation";
        	}
        }
        if (newMessages.length > 0) {
          this.textNode.className = ""; // clear classes so we don't leave the wrong type icon around
          dojo.toggleClass(this.textNode, "atg_messaging_smallMessageBar", true);
        	if (newMessages[newMessages.length - 1].datetime) {
            var timeDiff = dojo.date.difference(newMessages[newMessages.length - 1].datetime, new Date(), "second");
            if (timeDiff <= this.preArchiveSeconds) {
              dojo.toggleClass(this.textNode, "atg_messaging_" + type, true);
            }
        	}
          var targetDlg = dijit.byId(this.targetDialog);
          if (targetDlg && dojo.style(targetDlg.domNode, "display") == "none") {
            var targetFader = dijit.byId(this.targetFader);
            if (targetFader) {
              targetFader.setMessages(newMessages);
            }
          }
        }
      }
    },

    setDetails: function(messages) {
      if (this.debug) console.log("SmallMessageBar: setDetails");
      var targetDlg = dijit.byId(this.targetDialog);
      if (!targetDlg && !targetDlg.setContent) {
        return;
      }

      // Add messages to the details dialog
      targetDlg.addMessages(messages);
    }
  }
);

