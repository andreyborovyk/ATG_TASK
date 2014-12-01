dojo.provide("atg.widget.messaging.MessageBar");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.date.common");

/*
 * Base class for message bar
 * Manages the list of messages with callbacks to the sub-classes
 * when something changes.
 */
dojo.declare(
  "atg.widget.messaging.MessagingUIBase",
  null,
  {
    messages: [],
    recentMessageCount: 0, // how many new messages since last request
    serviceUrl: null,
    slotService: null,

    // default messages
    confirmationMessage: "",
    errorMessage: "",
    warningMessage: "",
    informationMessage: "",

    //methods
    addMessage: function(msg) {
      if (!msg) {
        return;
      }

      this.initializeSummary(msg);
      this.initializeDatetime(msg);
      this.messages.push(msg);
      this.recentMessageCount++;
      this.refresh();
      this.setDetails([msg]);
      this.showInConsole(msg);
    },

    // Clear all messages
    clearMessages: function() {
      this.messages = [];
      this.recentMessageCount = 0; // Move all remaining messages to history
    },

    hasMessage: function(msg, compareFunction) {
      var found = false;
      for (var i = 0; i < this.messages.length; i++) {
        if (compareFunction.apply(this, [this.messages[i], msg])) {
          found = true;
          break;
        }
      }
      return found;
    },

    initializeDatetime: function (msg) {
      if (msg.datetime) {
        msg.datetime = new Date(Date.parse(msg.datetime));
      }
      else {
        msg.datetime = new Date();
      }
    },

    initializeSummary: function(msg) {
      if (!msg.summary) {
        switch (msg.type) {
        case "error":
          msg.summary = this.errorMessage;
          break;
        case "confirmation":
          msg.summary = this.confirmationMessage;
          break;
        case "warning":
          msg.summary = this.warningMessage;
          break;
        case "information":
          msg.summary = this.informationMessage;
          break;
        default:
          break;
        }
      }
    },

    removeLastMessageBySummary: function(summary) {
      var found = false;
      for (var i = this.messages.length - 1; i >=0; i--) {
        var msg = this.messages[i];
        if (msg.summary && msg.summary == summary) {
          this.messages.splice(i, 1);
          this.recentMessageCount--;
          found = true;
          dojo.debug("Removing message by summary ", summary);
          break;
        }
      }
      if (found) {
        this.refresh();
      }
    },

    removeMessagesForWidget: function(widgetId) {
      if (!widgetId) {
        return;
      }

      var counter = 0;
      var index = 0;
      while (index < this.messages.length) {
        var msg = this.messages[index];
        if (msg.sourceId && msg.sourceId === widgetId) {
          this.messages.splice(index, 1);
          this.recentMessageCount--;
          counter++;
        }
        else {
          index++;
        }
      }
      if (counter > 0) {
        dojo.debug("Removing ", counter, " messages for widget ", widgetId);
        this.refresh();
      }
    },

    retrieveMessages: function(response) {
      // Algorithm to prevent unnecessary message retrieval calls to server:
      // When a request is made to fetch messages, 
      // do not immediately send a server request to fetch messages.
      // Instead, start a timer to see if any other requests come in under the timer.
      // If they do, reset the timer without making a request.
      // Submit a request only when the timer expires.
      if (this.timer) {
        dojo.lang.clearTimeout(this.timer);
      }
      this.timer = dojo.lang.setTimeout(this, this.submitRetrieval, 1000);
      dojo.debug("Setting message retrieval timer for ", this.widgetId);
      return response;
    },

    submitRetrieval: function() {
      // Read the messaging slot via JSON
      if (this.serviceUrl === null || this.serviceUrl === "") {
        dojo.debug("serviceUrl is null, setting up read messages JSON service");
        this.serviceUrl = window.windowId ?
          "/WebUI/widget/messaging/readMessages.jsp?_windowid=" + window.windowId :
          "/WebUI/widget/messaging/readMessages.jsp";
        var readMsgSlotDescriptor = {
          "serviceType": "JSON-RPC",
          "serviceURL": this.serviceUrl,
          "methods":[
          {
            "name": "readMessages",
            "parameters":[{"name": "isDebug"}]
          }]
        };
        this.slotService = new dojo.rpc.JsonService({smdObj: readMsgSlotDescriptor});
        dojo.debug("=== set up slot service " + this.slotService);
      }
      var result = this.slotService.readMessages(djConfig.isDebug ? true : false);
      result.addCallback(dojo.lang.hitch(this, this.unpackMessages));
      dojo.debug("Retrieving user messages for ", this.widgetId);
    },

    showInConsole: function(msg) {
      if (window.console) {
        if (window.console.log) {
          switch (msg.type) {
          case "error":
            window.console.log("MessageBar error: ", msg);
            break;
          case "warning":
            window.console.log("MessageBar warning: ", msg);
            break;
          case "information":
            window.console.log("MessageBar info: ", msg);
            break;
          default:
            window.console.log("MessageBar confirmation: ", msg);
            break;
          }
        }
      }
    },

    unpackMessages: function(response) {
      this.timer = null;
      delete this.timer;

      if(!(dojo.lang.isArray(response.messages))){
        this.clearOldMessages();
        return;
      }

      dojo.debug("Unpacking messages", response);
      this.recentMessageCount = response.messages.length;
      for (var i = 0; i < response.messages.length; i++) {
        var msg = response.messages[i];
        this.initializeSummary(msg);
        this.initializeDatetime(msg);
        this.messages.push(msg);
        this.showInConsole(msg);
      }
      if (response.messages.length > 0) {
        this.refresh();
        this.setDetails(response.messages);
      }
    }
  }
);

/*
 * Simple message bar
 */
dojo.widget.defineWidget(
  // widget name and class
  "atg.widget.messaging.MessageBar",

  // superclass
  [dojo.widget.ContentPane, atg.widget.messaging.MessagingUIBase],

  // properties and methods
  {
    // constants
    classPrefix: "atg_messaging_",
    iconSuffix: "Icon",
    textSuffix: "Text",

    // settings
    isContainer: true,
    widgetType: "MessageBar",
    templatePath: dojo.uri.dojoUri("/WebUI/widget/messaging/MessageBar.html"),
    templateCssPath: dojo.uri.dojoUri("/WebUI/widget/messaging/MessageBar.css"),

    // user properties
    cssfloat: "",
    targetDialog: "", // ID of dialog for messaging details

    // Localized strings for show/hide details link
    // The string assigned should contain the number of details
    // placeholder in the standard resource bundle parameterization format
    // e.g. Hide details - {0}
    hideLinkText: "",
    showLinkText: "",

		initialize: function(/*Object*/args, /*Object*/frag, /*Widget*/parent){
			dojo.event.topic.subscribe("/atg/message/addNew", this, "unpackMessages");
		},

		uninitialize: function() {
      dojo.event.topic.subscribe("/atg/message/addNew", this, "unpackMessages");
		},

    // callbacks
    postCreate: function() {
      atg.widget.messaging.MessageBar.superclass.postCreate.apply(this, arguments);

      var targetDlg = dojo.widget.byId(this.targetDialog);
      if (targetDlg && targetDlg.hide) {
        targetDlg.hide();
      }
    },

    fillInTemplate: function() {
      if(dojo.render.html.ie55 || dojo.render.html.ie60) {
        atg.widget.messaging.loadCss(this, "/WebUI/widget/messaging/MessageBar_IE6.css");
      }
    },

    // clicking on the bar
    barClick: function(e) {
      var targetDlg = dojo.widget.byId(this.targetDialog);
      if (targetDlg && targetDlg.toggleShowing) {
        targetDlg.toggleShowing();
      }
      this.refreshDetailLink();
      this.onClick(e);
    },

    onClick: function(e) {
      // summary: callback for when button is clicked; user can override this function
    },

    clearOldMessages: function() {
      // Do nothing
    },

    refresh: function() {
      if (this.messages.length > 0) {
        var msg = this.messages[this.messages.length - 1];
        if (msg) {
          this.initializeSummary(msg);
          // display top message in summary
          this.messageNode.innerHTML = msg.summary;
          dojo.html.setStyle(this.messageNode, "display", "inline");
          if (msg["type"]) {
            dojo.html.setClass(this.messageNode, this.classPrefix + msg.type + this.textSuffix);
            dojo.html.setClass(this.iconNode, this.classPrefix + msg.type + this.iconSuffix);
            dojo.html.setStyle(this.iconNode, "display", "inline");
          }
          dojo.html.setStyle(this.detailLinkContainerNode, "display", "inline");
          if (!this.hiddenAncestor) {
            this.hiddenAncestor = this.getHiddenAncestorNode();
            if (this.hiddenAncestor) {
              dojo.html.setStyle(this.hiddenAncestor, "display", "block");
            }
          }
        }
        this.refreshDetailLink();
      }
      else {
        this.setNodeContent(this.messageNode, "");
        dojo.html.setStyle(this.messageNode, "display", "none");
        if (this.hiddenAncestor) {
          dojo.html.setStyle(this.hiddenAncestor, "display", "none");
          this.hiddenAncestor = null;
        }
        dojo.html.setStyle(this.iconNode, "display", "none");
        dojo.html.setStyle(this.detailLinkContainerNode, "display", "none");
      }
    },

    refreshDetailLink: function() {
      // toggle detail link message and count
      var targetDlg = dojo.widget.byId(this.targetDialog);
      if (!targetDlg) {
        return;
      }

      var link;
      var msgCount = this.messages.length;
      if (targetDlg.isShowing()) {
        link = this.hideLinkText;
      }
      else {
        link = this.showLinkText;
      }
      link = link.replace(/\{0\}/gi, msgCount.toString());
      this.setNodeContent(this.detailLinkNode, link);
    },

    setNodeContent: function(node, content) {
      this.containerNode = node;
      this.setContent(content);
      this.containerNode = null;
    },

    setDetails: function(messages) {
      var targetDlg = dojo.widget.byId(this.targetDialog);
      if (!targetDlg && !targetDlg.setContent) {
        return;
      }

      // Add messages to the details dialog
      targetDlg.addMessages(messages);
    },

    getHiddenAncestorNode: function() {
      // find first hidden ancestor if any
      var ancestor = null;
      var parent = this.messageNode.parentNode;
      while (parent) {
        if (!dojo.html.isShowing(parent)) {
          ancestor = parent;
          break;
        }
        parent = parent.parentNode;
      }
      return ancestor;
    }
  }
);

/*
 * Small message bar
 */
dojo.widget.defineWidget(
  // widget name and class
  "atg.widget.messaging.SmallMessageBar",

  // superclass
  [dojo.widget.ContentPane, atg.widget.messaging.MessagingUIBase],

  // properties and methods
  {
    // constants
    classPrefix: "atg_messaging_",
    iconSuffix: "Icon",
    textSuffix: "Text",

    // settings
    isContainer: true,
    widgetType: "SmallMessageBar",
    templatePath: dojo.uri.dojoUri("/WebUI/widget/messaging/SmallMessageBar.html"),
    templateCssPath: dojo.uri.dojoUri("/WebUI/widget/messaging/SmallMessageBar.css"),

    // user properties
    cssfloat: "",
    initialText: "",
    preArchiveSeconds: 20, // Number of seconds to wait before archiving the current messages in message histor
    targetDialog: "", // ID of dialog for messaging details
    targetFader: "", // ID of widget for messaging fader effects
    togglerCloseImg: "/WebUI/images/messaging-common/icon_messageboxHide.gif",
    togglerOpenImg: "/WebUI/images/messaging-common/icon_messageboxShow.gif",

    // callbacks
    postCreate: function() {
      atg.widget.messaging.SmallMessageBar.superclass.postCreate.apply(this, arguments);

      var targetDlg = dojo.widget.byId(this.targetDialog);
      if (targetDlg && targetDlg.hide) {
        targetDlg.hide();
      }
      this.toggler.src = this.togglerOpenImg;
    },

    fillInTemplate: function() {
      if(dojo.render.html.ie55 || dojo.render.html.ie60) {
        atg.widget.messaging.loadCss(this, "/WebUI/widget/messaging/MessageBar_IE6.css");
      }
    },

    // clicking on the bar
    barClick: function(e) {
      var targetDlg = dojo.widget.byId(this.targetDialog);
      if (targetDlg) {
        // Click handler MUST be disabled during wipe-in/wipe-out times!
        if (!this._barClick_fn) {
          this._barClick_fn = this.barClick;
          this.barClick = function (){};
        }
        if (targetDlg.isShowing()) {
          dojo.lfx.html.wipeOut(targetDlg.domNode, 500).play();          
          this.toggler.src = this.togglerOpenImg;
          dojo.html.setClass(this.textNode, "atg_messaging_smallMessageBar");
          targetDlg.moveCurrentMessagesToHistory();
        }
        else {
          if (!this.__init) {
            targetDlg.show();
            this.__init = true;
          }
          dojo.lfx.html.wipeIn(targetDlg.domNode, 500).play();          
          var targetFader = dojo.widget.byId(this.targetFader);
          if (targetFader) {
            targetFader.hide();
          }
          this.toggler.src = this.togglerCloseImg;
        }
        dj_global.setTimeout(dojo.lang.hitch(this, "enableBarClick"), 500);
      }
      this.onClick(e);
    },

    onClick: function(e) {
      // summary: callback for when button is clicked; user can override this function
    },

    enableBarClick: function() {
      if (this._barClick_fn) {
        this.barClick = this._barClick_fn;
        this._barClick_fn = null;
        delete this._barClick_fn;
      }
    },

    clearOldMessages: function() {
      if (this.messages.length > 0) {
        var msg = this.messages[this.messages.length - 1];
        if (msg && msg.datetime) {
          var timeDiff = dojo.date.diff(msg.datetime, new Date(), dojo.date.dateParts.SECOND);
          if (timeDiff > this.preArchiveSeconds) {
            dojo.debug("Archiving current messages");
            dojo.html.setClass(this.textNode, "atg_messaging_smallMessageBar");
            var targetDlg = dojo.widget.byId(this.targetDialog);
            if (targetDlg) {
              targetDlg.moveCurrentMessagesToHistory();
            }
          }
        }
      }
    },

    refresh: function() {
      if (this.messages.length > 0) {
        var msg = this.messages[this.messages.length - 1];
        if (msg && msg.type) {
          dojo.html.setClass(this.textNode, "atg_messaging_smallMessageBar");
          if (msg.datetime) {
            var timeDiff = dojo.date.diff(msg.datetime, new Date(), dojo.date.dateParts.SECOND);
            if (timeDiff <= this.preArchiveSeconds) {
              dojo.html.addClass(this.textNode, "atg_messaging_" + msg.type);
            }
          }
          var targetDlg = dojo.widget.byId(this.targetDialog);
          if (targetDlg && !targetDlg.isShowing()) {
            var targetFader = dojo.widget.byId(this.targetFader);
            if (targetFader) {
              targetFader.setMessage(msg);
            }
          }
        }
      }
    },

    setDetails: function(messages) {
      var targetDlg = dojo.widget.byId(this.targetDialog);
      if (!targetDlg && !targetDlg.setContent) {
        return;
      }

      // Add messages to the details dialog
      targetDlg.addMessages(messages);
    }
  }
);

// Comparison function for defining equality by matching summary
atg.widget.messaging.compareMessagesBySummary = function(msg1, msg2) {
  if (!msg1.summary || !msg2.summary) {
    return false;
  }

  return (msg1.summary === msg2.summary) ? true : false;
};
