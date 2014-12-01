dojo.provide("atg.widget.validation.common");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");

/*
 * Function to generate a widget ID for an element in a form so 
 * that form elements do not need to use an ID.
 * 
 * The constructed widget ID is the ("id"|"name"|"") of the form (if any) 
 *   plus the ("name"|"id") of the node.
 * If the node is not in a containing form, the node can be referenced by id.
 * If both the node name and id are empty, return an empty string to allow
 *   dojo to generate its own widget ID.
 */
atg.widget.validation.generateWidgetId = function(node) {
  var wid = "";
  if(node){
    wid = node.id;
    if (!wid) {
      var nid = node.name;
      if (nid) {
        var form = dojo.html.getParentByType(node, "form");
        var fid = form ? form.id || form.name || "" : "";
        wid = fid ? fid + "_" + nid : nid;
      }
    }
  }
  return wid;
}

/*
 * Function to mix in validation capabilities to any widget prototype
 */
atg.widget.validation.enableValidation = function(widget) {
  dojo.lang.extend(widget, atg.widget.validation.ValidationBase);

  // Since we're mixing in with a widget prototype, 
  // make sure we rename the existing methods before replacing them.
  // We'll invoke them later (if present) from our own copies of the methods.
  if (widget.prototype.destroy) {
    widget.prototype._destroy_fn = widget.prototype.destroy;
  }
  if (widget.prototype.postMixInProperties) {
    widget.prototype._postMixInProperties_fn = widget.prototype.postMixInProperties;
  }
  if (widget.prototype.postCreate) {
    widget.prototype._postCreate_fn = widget.prototype.postCreate;
  }

  dojo.lang.extend(widget, {
    // These are the events on which to perform validation for the widget,
    // assigned after each widget instance gets created
    _validation_events : [],

    // Supply empty stub for standard update interface
    update : function() {},

    // Conditional expression to test whether input widget is missing.
    // When the expression is false, the widget will display the missing indicators.
    // For example, this can be used with a ComboBox to ensure that the user has
    // selected a value other than the prompt, i.e. "-- Choose a value --"
    // The missingIf property can be a simple expression or a function.apply() where
    // the boolean result is evaluated to determine the valid state of the widget.
    missingIf: "",

    // Implement standard isMissing interface with stub behavior
    // If a custom missingIf expression is provided, define missing based on 
    // the evaluation of the conditional expression
    isMissing : function() { 
      var result = false;
      if (this.missingIf) {
        result = eval(this.missingIf);
      }
      return result;
    },

    // Supply stub for standard isInRange interface
    isInRange : function() { return true; },

    // Always free resources
    destroy : function() {
      // Unregister with the submit button(s)
      atg.widget.validation.SubmitManager.removeValidationWidget(this);
      // Invoke the hierarchy's native destroy method that we've replaced
      if (this._destroy_fn) {
        this._destroy_fn.apply(this, arguments);
      }
    },

    postMixInProperties: function(args, frag) {
      // Invoke the hierarchy's native postMixInProperties method that we've replaced
      if (this._postMixInProperties_fn) {
        this._postMixInProperties_fn.apply(this, arguments);
      }
      // Generate a widget ID by concatenating the form ID to the form element name.
      this.widgetId = atg.widget.validation.generateWidgetId(this.getFragNodeRef(frag));
    },

    // Widget validation setup
    postCreate : function() {
      // Invoke the hierarchy's native postCreate method that we've replaced
      if (this._postCreate_fn) {
        this._postCreate_fn.apply(this, arguments);
      }
      // Hook up our events if there's a validation condition
      // Events to connect to are passed in the arguments
      var i = 0;
      if (this.validIf || this.missingIf) {
        for(i=0, l=this._validation_events.length; i<l; i++){
          dojo.event.connectOnce(this.domNode, this._validation_events[i], this, "updateUI");
        }
      }
      // Register with the submit button(s)
      atg.widget.validation.SubmitManager.addValidationWidget(this);
      // initial validation
      this.doValidation();
    }
  });
  
  for(var i=1, l=arguments.length; i<l; i++){
    widget.prototype._validation_events.push(arguments[i]);
  }
};

