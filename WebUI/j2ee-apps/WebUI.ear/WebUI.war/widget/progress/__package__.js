// Provide all your sub widgets
dojo.provide("atg.widget.progress");

dojo.require("atg.widget.progress.base");
dojo.require("atg.widget.progress.clockwise");
dojo.require("dojo.date");
dojo.require("dojo.date.format");


// Register the namespace of yoru widgets - might already exist
dojo.registerModulePath("progress", "/WebUI/widget/progress");
dojo.registerNamespace("progress", "atg.widget.progress");
