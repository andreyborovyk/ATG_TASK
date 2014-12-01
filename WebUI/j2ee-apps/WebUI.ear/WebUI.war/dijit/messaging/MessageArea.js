dojo.provide("atg.widget.messaging.MessageArea");
dojo.require("dojox.layout.ContentPane");
dojo.require("dijit._Templated");
dojo.require("dijit._Container");

dojo.declare(
  "atg.widget.messaging.MessageArea",
  [dijit._Widget,dijit._Templated,dijit._Container],
  {
    cssClass: "",
    templatePath: "/WebUI/dijit/messaging/template/MessageArea.html",

    addMessageItems: function(msgItems) {
      //console.debug("MessageArea::addMessageItems");
      if (dojo.isArray(msgItems)) {
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
      //console.debug("MessageArea::removeMessageItems");
      var msgItems = [];
      while (this.domNode.childNodes.length > 0) {
        if (!this.domNode.childNodes[0].tagName) {
          dojo._destroyElement(this.domNode.childNodes[0]); // use destroyNode to prevent IE memory leak
          continue;
        }
        var item; 
        if(this.domNode.childNodes[0] && this.domNode.childNodes[0].parentNode){
          item = this.domNode.childNodes[0].parentNode.removeChild(this.domNode.childNodes[0]); //Node
        };
        item.className = "atg_messaging_recentMessages";
        msgItems.push(item);
      }
      return msgItems;
    }
  }
);
