dojo.provide("atg.widget.messaging.MessageBar");
dojo.require("dojox.layout.ContentPane");
dojo.require("dojo.date");
dojo.require("dojo.rpc.JsonService");
dojo.require("dijit._Templated");
dojo.require("dijit._Container");

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
    
    debug: false,

    //methods
    addMessage: function(msg) {
      console.log("MessagingUIBase.addMessage called");
      if (!msg) {
        return;
      }

      this.initializeSummary(msg);
      this.initializeDatetime(msg);
      this.messages.push(msg);
      this.recentMessageCount++;
      this.refresh(1);
      this.setDetails([msg]);
      this.showInConsole(msg);
    },

    // Clear all messages
    clearMessages: function() {
      console.log("MessagingUIBase.clearMessages called");
      this.messages = [];
      this.recentMessageCount = 0; // Move all remaining messages to history
    },

    hasMessage: function(msg, compareFunction) {
      console.log("MessagingUIBase.hasMessage called");
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
      if (this.debug) console.log("MessagingUIBase.initializeDatetime called");
      if (msg.datetime) {
        msg.datetime = new Date(msg.datetime); //msg.datetime is long time millis here. See bug 157331
      }
      else {
        msg.datetime = new Date();
      }
    },

    initializeSummary: function(msg) {
      if (this.debug) console.log("MessagingUIBase.initializeSummary called");
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
      if (this.debug) console.log("MessagingUIBase.removeLastMessageBySummary called");
      var found = false;
      for (var i = this.messages.length - 1; i >=0; i--) {
        var msg = this.messages[i];
        if (msg.summary && msg.summary == summary) {
          this.messages.splice(i, 1);
          this.recentMessageCount--;
          found = true;
         	console.debug("Removing message by summary ", summary);
          break;
        }
      }
      if (found) {
        this.refresh(0);
      }
    },

    removeMessagesForWidget: function(id) {
      if (this.debug) console.log("MessagingUIBase.removeMessagesForWidget called");
      if (!id) {
        return;
      }

      var counter = 0;
      var index = 0;
      while (index < this.messages.length) {
        var msg = this.messages[index];
        if (msg.sourceId && msg.sourceId === id) {
          this.messages.splice(index, 1);
          this.recentMessageCount--;
          counter++;
        }
        else {
          index++;
        }
      }
      if (counter > 0) {
        console.debug("Removing ", counter, " messages for widget ", id);
        this.refresh(0);
      }
    },

    retrieveMessages: function(response) {
      if (this.debug) console.log("MessagingUIBase.retrieveMessages called");
      // Algorithm to prevent unnecessary message retrieval calls to server:
      // When a request is made to fetch messages, 
      // do not immediately send a server request to fetch messages.
      // Instead, start a timer to see if any other requests come in under the timer.
      // If they do, reset the timer without making a request.
      // Submit a request only when the timer expires.
      if (this.timer) {
        clearTimeout(this.timer);
      }
      this.timer = setTimeout(dojo.hitch(this, 'submitRetrieval'), 1000);
      //console.debug("Setting message retrieval timer for ", this.id);
      return response;
    },

    submitRetrieval: function() {
      if (this.debug) console.log("MessagingUIBase.submitRetrieval called");
      // Read the messaging slot via JSON
      if (this.serviceUrl === null || this.serviceUrl === "") {
        console.debug("serviceUrl is null, setting up read messages JSON service");
        this.serviceUrl = window.windowId ?
          "/WebUI/dijit/messaging/readMessages.jsp?_windowid=" + window.windowId :
          "/WebUI/dijit/messaging/readMessages.jsp";
        var readMsgSlotDescriptor = {
          serviceUrl: this.serviceUrl,
          timeout: 30000, // service is async, so let's wait for all the messages
          methods: [{
              "name": "readMessages",
              "parameters":[{"name": "isDebug"}]
          }]
        };
        this.slotService = new dojo.rpc.JsonService(readMsgSlotDescriptor);
        //dojo.debug("=== set up slot service " + this.slotService.toSource());
      }
      var result = this.slotService.readMessages(djConfig.isDebug ? true : false);
      result.addCallback(dojo.hitch(this, "unpackMessages"));
      //dojo.debug("Retrieving user messages for ", this.id);
    },

    showInConsole: function(msg) {
      if (this.debug) console.log("MessagingUIBase.showInConsole called");
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
      if (this.debug) console.log("MessagingUIBase.unpackMessages called");
      this.timer = null;
      delete this.timer;

      if(!(dojo.isArray(response.messages))){
        this.clearOldMessages();
        return;
      }

      console.debug("Unpacking messages", response);
      this.recentMessageCount = response.messages.length;
      for (var i = 0; i < response.messages.length; i++) {
        var msg = response.messages[i];
        this.initializeSummary(msg);
        this.initializeDatetime(msg);
        this.messages.push(msg);
        this.showInConsole(msg);
      }
      if (response.messages.length > 0) {
        this.refresh(response.messages.length);
        this.setDetails(response.messages);
      }
    }
  }
);

/*
 * Simple message bar
 */
dojo.declare(
  // widget name and class
  "atg.widget.messaging.MessageBar",

  // superclass
  [dijit._Widget,atg.widget.messaging.MessagingUIBase,dijit._Templated,dijit._Container],

  // properties and methods
  {
    // constants
    classPrefix: "atg_messaging_",
    iconSuffix: "Icon",
    textSuffix: "Text",

    // settings
    isContainer: true,
    widgetType: "MessageBar",
    templateString: null,
    templatePath: "/WebUI/dijit/messaging/template/MessageBar.html",

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
      dojo.subscribe("/atg/message/addNew", this, "unpackMessages");
    },

    uninitialize: function() {
      dojo.subscribe("/atg/message/addNew", this, "unpackMessages");
    },

    // callbacks
    postCreate: function() {
      this.inherited("postCreate", arguments);
			console.debug(this);

      var targetDlg = dijit.byId(this.targetDialog);
      if (targetDlg && targetDlg.hide) {
        targetDlg.hide();
      }
    },

    // clicking on the bar
    barClick: function(e) {
      if (this.debug) console.debug("MessageBar::barClick");
      var targetDlg = dijit.byId(this.targetDialog);
      if (targetDlg && targetDlg.domNode.style.display == "none") {
        targetDlg.show();
      }
      else {
        targetDlg.hide();
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
      if (this.debug) console.debug("MessageBar::refresh");
      if (this.messages.length > 0) {
        var msg = this.messages[this.messages.length - 1];
        if (msg) {
          this.initializeSummary(msg);
          // display top message in summary
          this.messageNode.innerHTML = msg.summary;
          dojo.style(this.messageNode, "display", "inline");
          if (msg["type"]) {
            dojo.toggleClass(this.messageNode, this.classPrefix + msg.type + this.textSuffix, true);
            dojo.toggleClass(this.iconNode, this.classPrefix + msg.type + this.iconSuffix, true);
            dojo.style(this.iconNode, "display", "inline");
          }
          dojo.style(this.detailLinkContainerNode, "display", "inline");
          if (!this.hiddenAncestor) {
            this.hiddenAncestor = this.getHiddenAncestorNode();
            if (this.hiddenAncestor) {
              dojo.style(this.hiddenAncestor, "display", "block");
            }
          }
        }
        this.refreshDetailLink();
      }
      else {
        this.setNodeContent(this.messageNode, "");
        dojo.style(this.messageNode, "display", "none");
        if (this.hiddenAncestor) {
          dojo.style(this.hiddenAncestor, "display", "none");
          this.hiddenAncestor = null;
        }
        dojo.style(this.iconNode, "display", "none");
        dojo.style(this.detailLinkContainerNode, "display", "none");
      }
    },

    refreshDetailLink: function() {
      if (this.debug) console.debug("MessageBar::refreshDetailLink");
      // toggle detail link message and count
      var targetDlg = dijit.byId(this.targetDialog);
      if (!targetDlg) {
        return;
      }

      var link;
      var msgCount = this.messages.length;
      if (targetDlg.domNode.style.display != "none") {
        link = this.hideLinkText;
      }
      else {
        link = this.showLinkText;
      }
      link = link.replace(/\{0\}/gi, msgCount.toString());
      this.setNodeContent(this.detailLinkNode, link);
    },

    setNodeContent: function(node, content) {
      if (this.debug) console.debug("MessageBar::setNodeContent");
      this.containerNode = node;
      this.setContent(content);
      this.containerNode = null;
    },

    setDetails: function(messages) {
      if (this.debug) console.debug("MessageBar::setDetails");
      var targetDlg = dijit.byId(this.targetDialog);
      if (!targetDlg && !targetDlg.setContent) {
        return;
      }

      // Add messages to the details dialog
      targetDlg.addMessages(messages);
    },

    getHiddenAncestorNode: function() {
      if (this.debug) console.debug("MessageBar::getHiddenAncestorNode");
      // find first hidden ancestor if any
      var ancestor = null;
      var parent = this.messageNode.parentNode;
      while (parent) {
        if (dojo.style(parent, "display") == "none") {
          ancestor = parent;
          break;
        }
        parent = parent.parentNode;
      }
      return ancestor;
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
