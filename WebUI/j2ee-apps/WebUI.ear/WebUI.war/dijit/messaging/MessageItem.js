dojo.provide("atg.widget.messaging.MessageItem");
dojo.require("dojox.layout.ContentPane");
dojo.require("dijit._Templated");
dojo.require("dijit._Container");
dojo.require("atg.widget.messaging.DetailItem");

dojo.declare(
  "atg.widget.messaging.MessageItem",
  [dijit.layout.ContentPane,dijit._Templated,dijit._Container],
  {
    // constants
    classPrefix: "atg_messaging_",

    // settings
    cssClass: "",
    templateString: null,
    templatePath: "/WebUI/dijit/messaging/template/MessageItem.html",
    message: "",

    postCreate: function(/*Object*/ args, /*Object*/ frag){
			console.debug(this);
	
	
      this.inherited('postCreate', arguments);
      this.containerNode.className = this.classPrefix + this.message.type;
      if (this.message.summary) {
        this.summaryNode.innerHTML = this.message.summary;
      }
      if (this.message.details && this.message.details.length > 0) {
        this.detailsNode = document.createElement("ol");
        this.detailsNode.className = "atg_messaging_subMessages";
        this.containerNode.appendChild(this.detailsNode);
        for (var i = 0, l = this.message.details.length; i < l; i++) {
          var item = new atg.widget.messaging.DetailItem({detail: this.message.details[i]});
          this.detailsNode.appendChild(item.domNode);
        }
      }
    }
  }
);
