dojo.provide("atg.widget.messaging.MessageArea");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ContentPane");

dojo.widget.defineWidget(
  "atg.widget.messaging.MessageArea",
  dojo.widget.ContentPane,
  {
    isContainer: true,
    cssClass: "",
    templatePath: dojo.uri.dojoUri("/WebUI/widget/messaging/MessageArea.html"),
    templateCssPath: dojo.uri.dojoUri("/WebUI/widget/messaging/MessageArea.css"),

    addMessageItems: function(msgItems) {
      if (dojo.lang.isArray(msgItems)) {
        for (var i = msgItems.length - 1; i >= 0; i--) {
          if (this.domNode.childNodes.length > 0) {
            this.domNode.insertBefore(msgItems[i], this.domNode.childNodes[0]);
          }
          else {
            this.domNode.appendChild(msgItems[i]);
          }          
        }
      }
    },

    removeMessageItems: function() {
      var msgItems = [];
      while (this.domNode.childNodes.length > 0) {
        if (!this.domNode.childNodes[0].tagName) {
          dojo.dom.destroyNode(this.domNode.childNodes[0]); // use destroyNode to prevent IE memory leak
          continue;
        }
        var item = dojo.dom.removeNode(this.domNode.childNodes[0]);
        item.className = "atg_messaging_recentMessages";
        msgItems.push(item);
      }
      return msgItems;
    }
  }
);
