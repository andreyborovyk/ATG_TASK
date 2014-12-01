dojo.provide("atg.widget.validation");
dojo.kwCompoundRequire({
	common: ["atg.widget.validation.common",
	         "atg.widget.validation.ValidationBase",
	         "atg.widget.validation.ValidationTextboxEx",
	         "atg.widget.validation.SimpleComboBox",
	         "atg.widget.validation.SubmitButton",
	         "atg.widget.validation.TextArea"]
});
dojo.require("dojo.ns");
dojo.registerModulePath("validation", "/WebUI/widget/validation");
dojo.registerNamespace("validation", "atg.widget.validation");
