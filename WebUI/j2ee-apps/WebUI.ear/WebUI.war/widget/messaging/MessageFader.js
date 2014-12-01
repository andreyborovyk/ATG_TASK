dojo.provide("atg.widget.messaging.MessageFader");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ContentPane");
dojo.require("dojo.lfx.html");

dojo.widget.defineWidget(
  "atg.widget.messaging.MessageFader",
  dojo.widget.ContentPane,
  {
    isContainer: true,
    templatePath: dojo.uri.dojoUri("/WebUI/widget/messaging/MessageFader.html"),
    templateCssPath: dojo.uri.dojoUri("/WebUI/widget/messaging/MessageFader.css"),

    hide: function() {
      this.domNode.style.display = "none";
    },

    setMessage: function(/*String*/ msg) {
      while (this.domNode.childNodes.length > 0) {
        dojo.dom.destroyNode(this.domNode.childNodes[0]); // use destroyNode to prevent IE memory leak
      }
      var msgItem = dojo.widget.createWidget("messaging:MessageItem",
        {message: msg, cssClass: "atg_messaging_currentMessage"});
      this.domNode.appendChild(msgItem.domNode);
      dojo.lfx.html.fadeOut(this.domNode, 1).play();
      this.show();
      dojo.lfx.html.fadeIn(this.domNode, 500).play();
      window.setTimeout("dojo.lfx.html.fadeHide(dojo.widget.byId(\"" + this.widgetId + "\").domNode, 700).play()", 5000);
    },

    show: function() {
      this.domNode.style.display = "block";
    }
  }
);
