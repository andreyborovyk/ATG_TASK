dojo.provide("atg.widget.validation.SubmitButton");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");

dojo.declare(
  // widget name and class
  "atg.widget.validation.SubmitButton",

  // superclass
  [dijit._Widget,dijit._Templated],

  // properties and methods
  {
    // parameters
    form: "",
    id: "",
    name: "",
    type: "",
    value: "",

    // settings
    isContainer: true,
    templatePath: "/WebUI/dijit/validation/SubmitButton.html",

    // callbacks
    buttonClick: function(/*Event*/ e){
      // summary: internal function for handling button clicks
      if(!this.buttonNode.disabled){ 
        // focus may fail when tabIndex is not supported on div's
        // by the browser, or when the node is disabled
// This line is causing an XULElement error on Firefox
// Need to find another way to handle the focus issue
//        try { this.buttonNode.focus(); } catch(e2) {}
        this.onClick(e); 
      }
    },

    onClick: function(/*Event*/ e) {
      // summary: callback for when button is clicked; user can override this function
    },

    buildRendering: function(args, frag) {
      this.inherited('buildRendering', arguments);

      var node = this.srcNodeRef;
      if (node && node.id && this.buttonNode) {
        this.buttonNode.id = node.id;
      }
    },

    postMixInProperties: function(args, frag) {
      this.inherited('postMixInProperties', arguments);
    },

    postCreate: function() {
      atg.widget.validation.SubmitButton.superclass.postCreate.apply(this, arguments);
    },

    disableButton: function() {
      if (!this.buttonNode.disabled) {
        this.buttonNode.disabled = true;
      }
    },

    enableButton: function() {
      this.buttonNode.removeAttribute("disabled", false);
    },

    destroy: function(finalize) {
      dojo.debug("SubmitButton widget destroy() was called");
      atg.widget.validation.SubmitButton.superclass.destroy.call(this);
    }
  }
);
