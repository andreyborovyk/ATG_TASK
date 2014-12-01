dojo.provide("atg.widget.validation.SubmitButton");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.HtmlWidget");

/*
 * Management for validation-aware submit buttons that enable
 * or disable based on the values inside validation widgets.
 */
atg.widget.validation.SubmitManager = {
  // List of invalid widgets which all must be valid before enabling
  buttons: {},
  invalidWidgets: {},
  widgets: {},

  // Register a submit button with a form
  addSubmitButton: function(w) {
    this._addWidgetToMap(w, "buttons");
  },

  removeSubmitButton: function(w) {
    this._removeWidgetFromMap(w, "buttons");
  },
  
  // Register a validation widget with a form
  addValidationWidget: function(w) {
    // Widget must exist and support dojo's validation interface
    if (w && w.isValid) {
      this._addWidgetToMap(w, "widgets");
    }
  },
  
  removeValidationWidget: function(w) {
    this._removeWidgetFromMap(w, "widgets");
    this._removeWidgetFromMap(w, "invalidWidgets");
  },

  // Validate a single widget by ID    
  validateWidgetById: function(id) {
    var w = dojo.widget.manager.getWidgetById(id);
    this.validateWidget(w);
  },

  // Validate a single widget
  validateWidget: function(w) {
    if (dojo.lang.isString(w)) {
      w = dojo.widget.manager.getWidgetById(w);
    }
    if (w.validateAllWidgets) { // validate all widgets in form?
      this.validateForm(w);
    }
    else { // just validate this widget
      this.validateSingleWidget(w);
    }
  },

  validateSingleWidget: function(w) {
    // Does the widget implement the correct validation interface?
    if (w && w.isMissing && w.isValid && w.isInRange) {
      dojo.debug("Submit manager validating widget", w.widgetId);
      if (w.isMissing() || !w.isValid() || !w.isInRange()) {
        this.disableSubmitButtons(w);
        this.addInvalidWidget(w);
      }
      else {
        if (this.isInvalidWidget(w)) {
          this.removeInvalidWidget(w);
          if (!this.hasInvalidWidgets(w)) {
            this.enableSubmitButtons(w);
          }
        }
      }
      if (djConfig && djConfig.isDebug) {
        var iw = this._getMap(w, "invalidWidgets");
        var dbg = new dojo.string.Builder;
        dbg.append("SUBMITBUTTON - Invalid widgets: ");
        for (x in iw) {
          dbg.append(x, ", ");
        }
        dojo.debug(dbg.valueOf());
      }
    }
  },

  // Test every registered validation widget in the form
  validateForm: function(sb) {
    var map = this._getMap(sb, "widgets");
    for (var w in map) {
      this.validateSingleWidget(map[w]);
    }
  },

  // Add widget by form and ID only once
  addInvalidWidget: function(w) {
    this._addWidgetToMap(w, "invalidWidgets");
  },

  disableSubmitButtons: function(w) {
    var map = this._getMap(w, "buttons");
    for (var b in map) {
      dojo.debug("Disabling button ", map[b]);
      map[b].disableButton();
    }
  },

  enableSubmitButtons: function(w) {
    var map = this._getMap(w, "buttons");
    for (var b in map) {
      dojo.debug("Enabling button ", map[b]);
      map[b].enableButton();
    }
  },

  // Does form containing the widget have invalid widgets?
  hasInvalidWidgets: function(w) {
    var yn = false;
    var map = this._getMap(w, "invalidWidgets");
    for (var widget in map) {
      yn = true;
      break;
    }
    return yn;
  },

  // Is the widget in the map of invalid widgets?
  isInvalidWidget: function(w) {
    // look for widget in invalid list
    var map = this._getMap(w, "invalidWidgets");
    return w.widgetId in map;
  },

  // Remove widget from map of invalid widgets  
  removeInvalidWidget: function(w) {
    // widget is now valid, so remove association
    var map = this._getMap(w, "invalidWidgets");
    delete map[w.widgetId];
  },

  // Add widget to any map on this object
  // All maps are keyed by form
  _addWidgetToMap: function(w, mapKey) {
    var map = this._getMap(w, mapKey);
    if (map) {
      map[w.widgetId] = w;
    }
  },

  _removeWidgetFromMap: function(w, mapKey) {
    var map = this._getMap(w, mapKey);
    if (map) {
      delete map[w.widgetId];
    }
  },
  
  // Get map by form. Possible maps: "widgets", "buttons" and "invalidWidgets"
  // Form is found by finding form parent of specified widget.
  // If no form, use a global map.
  _getMap: function(w, mapKey) {
    // Does widget have a parent form?
    var form = null;
    var map = null;
    
    if (w["form"]) {
      form = dojo.byId(w.form);
    }
    if (!form){
      form = dojo.html.getParentByType(w.domNode, "form");
    }
    if (!form) {
      map = this[mapKey][""];
      if (!map) {
        map = {};
        this[mapKey][""] = map; // init global map
      }
      return map;
    }

    var fid = form.id || form.name || "";
    map = this[mapKey][fid];
    if (!map) {
      map = {};
      this[mapKey][fid] = map; // init form map
    }
    return map;
  }
};

dojo.widget.defineWidget(
  // widget name and class
  "atg.widget.validation.SubmitButton",

  // superclass
  dojo.widget.HtmlWidget,

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
    templatePath: dojo.uri.dojoUri("/WebUI/widget/validation/SubmitButton.html"),
    
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
      atg.widget.validation.SubmitButton.superclass.buildRendering.apply(this, arguments);

      var node = this.getFragNodeRef(frag);
      if (node && node.id && this.buttonNode) {
        this.buttonNode.id = node.id;
      }
    },

    postMixInProperties: function(args, frag) {
      atg.widget.validation.SubmitButton.superclass.postMixInProperties.apply(this, arguments);
      // Generate a widget ID by concatenating the form ID to the form element name.
      this.widgetId = atg.widget.validation.generateWidgetId(this.getFragNodeRef(frag));
    },

    postCreate: function() {
      atg.widget.validation.SubmitButton.superclass.postCreate.apply(this, arguments);
      // Register with manager
      atg.widget.validation.SubmitManager.addSubmitButton(this);
      // Upon initial rendering, respond to invalid values in the form.
      // This will take care of submit buttons rendered after the validation widgets
      atg.widget.validation.SubmitManager.validateForm(this);
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
      atg.widget.validation.SubmitManager.removeSubmitButton(this);
      atg.widget.validation.SubmitButton.superclass.destroy.call(this);
    }
  }
);
