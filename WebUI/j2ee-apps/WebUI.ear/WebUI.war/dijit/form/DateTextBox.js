if(!dojo._hasResource["atg.widget.form.DateTextBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["atg.widget.form.DateTextBox"] = true;
dojo.provide("atg.widget.form.DateTextBox");

dojo.require("dijit._Calendar");
dojo.require("atg.widget.form.TimeTextBox");

dojo.declare(
	"atg.widget.form.DateTextBox",
	atg.widget.form.TimeTextBox,
	{
		// summary:
		//		A validating, serializable, range-bound date text box.

		_popupClass: "dijit._Calendar",

		postMixInProperties: function(){
			this.inherited('postMixInProperties', arguments);
			this.constraints.selector = 'date';
		}
	}
);

}
