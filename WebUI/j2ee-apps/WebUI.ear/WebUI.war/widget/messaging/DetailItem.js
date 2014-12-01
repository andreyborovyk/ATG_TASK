dojo.provide("atg.widget.messaging.DetailItem");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ContentPane");

dojo.widget.defineWidget(
  "atg.widget.messaging.DetailItem",
  dojo.widget.ContentPane,
  {
    isContainer: true,
    templatePath: dojo.uri.dojoUri("/WebUI/widget/messaging/DetailItem.html"),
    detail: "",

		fillInTemplate: function(/*Object*/ args, /*Object*/ frag){
  		atg.widget.messaging.DetailItem.superclass.fillInTemplate.call(this, args, frag);
      this.setContent(this.detail.description);
		}
  }
);
