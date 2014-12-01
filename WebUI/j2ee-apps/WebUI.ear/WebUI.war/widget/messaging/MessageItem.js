dojo.provide("atg.widget.messaging.MessageItem");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ContentPane");

dojo.widget.defineWidget(
  "atg.widget.messaging.MessageItem",
  dojo.widget.ContentPane,
  {
    // constants
    classPrefix: "atg_messaging_",

    // settings
    isContainer: true,
    cssClass: "",
    templatePath: dojo.uri.dojoUri("/WebUI/widget/messaging/MessageItem.html"),
    templateCssPath: dojo.uri.dojoUri("/WebUI/widget/messaging/MessageItem.css"),
    message: "",

		fillInTemplate: function(/*Object*/ args, /*Object*/ frag){
  		atg.widget.messaging.MessageItem.superclass.fillInTemplate.call(this, args, frag);
		  this.containerNode.className = this.classPrefix + this.message.type;
      if (this.message.summary) {
        this.summaryNode.innerHTML = this.message.summary;
      }
      if (this.message.details && this.message.details.length > 0) {
        this.detailsNode = document.createElement("ol");
        this.detailsNode.className = "atg_messaging_subMessages";
        this.containerNode.appendChild(this.detailsNode);
        for (var i = 0, l = this.message.details.length; i < l; i++) {
          var item = dojo.widget.createWidget("messaging:DetailItem", {detail: this.message.details[i]});
          this.detailsNode.appendChild(item.domNode);
        }
      }
		}
  }
);
