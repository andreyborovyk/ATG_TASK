dojo.provide("atg.widget.validation.SimpleComboBox");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit._Container");

dojo.declare(
  "atg.widget.validation.SimpleComboBox",
  [dijit._Widget,dijit._Templated,dijit._Container],
  {
    id: "",
    name: "",
    templatePath: "/WebUI/dijit/validation/SimpleComboBox.html",

    postCreate: function(/*Object*/ args, /*Object*/ frag){
      if (!this.domNode) return;
      var node = this.srcNodeRef;
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
