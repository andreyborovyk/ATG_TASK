dojo.provide("atg.widget.messaging.DetailItem");

dojo.require("dijit.layout.ContentPane");
dojo.require("dijit._Templated");
dojo.require("dijit._Container");

dojo.declare(
  "atg.widget.messaging.DetailItem",
  [dijit.layout.ContentPane,dijit._Templated,dijit._Container],
  {
    templatePath: "/WebUI/dijit/messaging/template/DetailItem.html",
    templateString: "", // block any that are in parent classes so our templatePath gets used
    detail: "",

    postCreate: function(/*Object*/ args, /*Object*/ frag){
      this.inherited('postCreate', arguments);
      this.setContent(this.detail.description);
    }
  }
);
