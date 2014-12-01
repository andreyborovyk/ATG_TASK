if(!dojo._hasResource["atg.widget.form.CurrencyTextBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["atg.widget.form.CurrencyTextBox"] = true;
dojo.provide("atg.widget.form.CurrencyTextBox");

//FIXME: dojo.experimental throws an unreadable exception?
//dojo.experimental("atg.widget.form.CurrencyTextBox");

dojo.require("dojo.currency");
dojo.require("atg.widget.form.NumberTextBox");

dojo.declare(
	"atg.widget.form.CurrencyTextBox",
	atg.widget.form.NumberTextBox,
	{
		// code: String
		//		the ISO4217 currency code, a three letter sequence like "USD"
		//		See http://en.wikipedia.org/wiki/ISO_4217
		currency: "",

		regExpGen: dojo.currency.regexp,
		format: dojo.currency.format,
		parse: dojo.currency.parse,

		postMixInProperties: function(){
			if(this.constraints === atg.widget.form.ValidationTextBox.prototype.constraints){
				// declare a constraints property on 'this' so we don't overwrite the shared default object in 'prototype'
				this.constraints = {};
			}
			this.constraints.currency = this.currency;
			atg.widget.form.CurrencyTextBox.superclass.postMixInProperties.apply(this, arguments);
		}
	}
);

}
