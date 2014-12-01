if(!dojo._hasResource["atg.searchadmin.DateTextBox"]){ //_hasResource checks added by build. Do not use _hasResource directly in your code.
dojo._hasResource["atg.searchadmin.DateTextBox"] = true;
dojo.provide("atg.searchadmin.DateTextBox");

//dojo.require("dojo.date");
//dojo.require("dojo.date.locale");
//dojo.require("dojo.date.stamp");
dojo.require("dijit.form.DateTextBox");

dojo.declare(
  "atg.searchadmin.DateTextBox",
  dijit.form.DateTextBox,
  {
    // summary:
    // A date text box.

    templateString:"<input dojoAttachPoint='textbox,focusNode' dojoAttachEvent='onfocus,onblur:_onMouse,onkeyup,onkeypress:_onKeyPress' autocomplete=\"off\"\r\n\t\t\ttype='${type}' name='${name}'\r\n\t\t/>\r\n",

    create: function(params, srcNodeRef){
      var attr = dojo.byId(srcNodeRef).attributes["setValueToId"];
      if (attr) {
        this.setValueToId = attr.value;
      }
      this.inherited('create', arguments);
    },

    setValue: function(/*Date*/ value, /*Boolean, optional*/ priorityChange) {
      this.inherited('setValue', arguments);
      if (this.setValueToId) {
        var input = dojo.byId(this.setValueToId);
        if (input) {
          input.value = this.textbox.value;
        }
      }
    },

    validate: function(/* Boolean*/ isFocused) {
      // don't need to validate here!
    }
  }
);

}
