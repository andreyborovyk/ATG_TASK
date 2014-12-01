dojo.provide("atg.widget.form.defaultText");
dojo.require("dijit._Widget");
 
dojo.declare("atg.widget.form.defaultText", dijit._Widget, {
	
	defaultText: "",
	type: "",
	
	postCreate: function(){
	
		console.debug(this.domNode);
		dojo.connect(this.domNode, 'onfocus', this, "_onfocus");
		dojo.connect(this.domNode, 'onblur', this, "_onblur");
		this.defaultText = this.domNode.value;
		dojo.toggleClass(this.domNode, "atg_widget_defaultText_view")
		console.debug(this.domNode.type);
		this.type = this.domNode.type;
		if(this.type == "password"){
			if(!dojo.isIE){
				this.domNode.type = "text";
			}
		}
		
	},
	
	_onfocus: function(){
		console.debug("You focused on: ", this.domNode);
		if(this.domNode.value == this.defaultText){
			this.domNode.value = "";
			dojo.toggleClass(this.domNode, "atg_widget_defaultText_view")
			if(this.type == "password"){
				if(!dojo.isIE){
					this.domNode.type = "password";
				}
			}
		}
	},
	
	_onblur: function(){
		console.debug("You blurred: ", this.domNode);
		if(this.domNode.value == ""){
			this.domNode.value = this.defaultText;
			dojo.toggleClass(this.domNode, "atg_widget_defaultText_view")
			if(this.type == "password"){
				if(!dojo.isIE){
					this.domNode.type = "text";
				}
			}
		}
	}

});