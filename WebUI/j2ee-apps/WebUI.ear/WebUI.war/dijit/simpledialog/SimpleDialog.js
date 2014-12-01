dojo.provide("atg.widget.simpledialog.SimpleDialog");

dojo.require("dijit.Dialog");

dojo.declare(
	"atg.widget.simpledialog.SimpleDialog",
	[dijit.Dialog],
	{
		// summary:
		//		Pops up a dialog that doesn't have a title bar

		templateString: null,
		templatePath: "/WebUI/dijit/simpledialog/template/SimpleDialog.html"

	}	
);