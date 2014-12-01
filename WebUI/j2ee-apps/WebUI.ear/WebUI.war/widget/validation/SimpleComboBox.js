dojo.provide("atg.widget.validation.SimpleComboBox");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");
dojo.require("atg.widget.validation.common");

dojo.widget.defineWidget(
  "atg.widget.validation.SimpleComboBox",
  dojo.widget.HtmlWidget,
  {
    id: "",
    name: "",
    isContainer: true,
    templatePath: dojo.uri.dojoUri("/WebUI/widget/validation/SimpleComboBox.html"),

    fillInTemplate: function(/*Oject*/ args, /*Object*/ frag){
      if (!this.domNode) return;
      var node = this.getFragNodeRef(frag);
      if((node)&&(node.nodeName.toLowerCase() == "select")){
        if (node.id) { // set dom ID ONLY if defined
          this.domNode.id = node.id;
        }
        var opts = node.getElementsByTagName("option");
        var ol = opts.length;
        for (var x=0;x<ol;x++){
          var opt = document.createElement("option");
          opt.value = opts[x].value;
          opt.selected = opts[x].selected;
          opt.innerHTML = opts[x].innerHTML;
          this.domNode.appendChild(opt);
        }
      }
    },

    getValue: function(){
      return (this.domNode) ? this.domNode.value : "";
    }
  }
);

atg.widget.validation.enableValidation(atg.widget.validation.SimpleComboBox, "onchange", "onfocus", "onblur", "onkeyup");
