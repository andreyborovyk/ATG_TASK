dojo.provide("atg.widget.validation.TextArea");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ValidationTextbox");
dojo.require("atg.widget.validation.common");

dojo.widget.defineWidget(
  "atg.widget.validation.TextArea",
  dojo.widget.ValidationTextbox,
  {
    cssStyle: "",
    cols: "",
    rows: "",
    title: "",
		templateString:"<span style='float:${this.htmlfloat};'><textarea name='${this.name}' class='${this.className}' style='${this.cssStyle}' title='${this.title}' type='${this.type}' cols='${this.cols}' rows='${this.rows}' tabindex='${this.tabindex}' dojoAttachPoint='textbox' dojoAttachEvent='onblur;onfocus;onkeyup'>${this.value}</textarea><span dojoAttachPoint='invalidSpan' class='${this.invalidClass}'>${this.messages.invalidMessage}</span><span dojoAttachPoint='missingSpan' class='${this.missingClass}'>${this.messages.missingMessage}</span><span dojoAttachPoint='rangeSpan' class='${this.rangeClass}'>${this.messages.rangeMessage}</span></span>",
		maxlength: "",
		minlength: "",
		isInRange: function() {
		  var maxl = parseInt(this.maxlength);
		  var minl = parseInt(this.minlength);
    	var len = this.getValue().length;
			return len >= (isNaN(minl) ? 0 : minl) && len <= (isNaN(maxl) ? Infinity : maxl);
		},
    buildRendering: function(args, frag) {
      atg.widget.validation.TextArea.superclass.buildRendering.apply(this, arguments);
      var node = this.getFragNodeRef(frag);
      if (node && node.id && this.textbox) {
        this.textbox.id = node.id;
      }
    }
  }
);
