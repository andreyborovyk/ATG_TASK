dojo.provide("atg.widget.messaging");
dojo.kwCompoundRequire({
	common: ["atg.widget.messaging.common",
	         "atg.widget.messaging.DetailItem",
	         "atg.widget.messaging.MessageArea",
	         "atg.widget.messaging.MessageBar",
	         "atg.widget.messaging.MessageFader",
	         "atg.widget.messaging.MessageItem",
	         "atg.widget.messaging.MessagePane",
	         "atg.widget.messaging.ResizeHandleEx"]
});
dojo.require("dojo.ns");
dojo.registerModulePath("messaging", "/WebUI/widget/messaging");
dojo.registerNamespace("messaging", "atg.widget.messaging");
