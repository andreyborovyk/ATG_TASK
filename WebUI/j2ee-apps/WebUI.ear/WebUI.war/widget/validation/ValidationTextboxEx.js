dojo.provide("atg.widget.validation.ValidationTextboxEx");

dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");
dojo.require("dojo.widget.ValidationTextbox");

/*
 * Define extension properties for all validaton text boxes
 */
atg.widget.validation.ValidationTextboxBase = {

  autocomplete: "",
  tabindex: "",

  // extraTemplateCssPath: String
  //    The path to inline indicator CSS.
  extraTemplateCssPath: dojo.uri.dojoUri("/WebUI/widget/validation/ValidationTextboxEx.css"),

  templateString: "<span style='float:${this.htmlfloat};'><input dojoAttachPoint='textbox' type='${this.type}' dojoAttachEvent='onblur;onfocus;onkeyup' name='${this.name}' size='${this.size}' maxlength='${this.maxlength}' class='${this.className}' style=''><span dojoAttachPoint='invalidSpan' class='${this.invalidClass}'>${this.messages.invalidMessage}</span><span dojoAttachPoint='missingSpan' class='${this.missingClass}'>${this.messages.missingMessage}</span><span dojoAttachPoint='rangeSpan' class='${this.rangeClass}'>${this.messages.rangeMessage}</span></span>",

  // updateClass: Function
  //    Remove behavior by overriding with null implementation
  updateClass: function() {
    // intentionally empty
  },

  buildRendering: function(args, frag) {
    dojo.widget.ValidationTextbox.superclass.buildRendering.apply(this, arguments);

    var node = this.getFragNodeRef(frag);
    if (node && node.id && this.textbox) {
      this.textbox.id = node.id;
    }

    // load extra CSS file for inline indicators on top of base class CSS
    var cpath = this.extraTemplateCssPath;
    if(cpath && !dojo.widget._cssFiles[cpath.toString()]){
      if((!this.extraTemplateCssString)&&(cpath)){
        this.extraTemplateCssString = dojo.hostenv.getText(cpath);
        this.extraTemplateCssPath = null;
      }
      dojo.widget._cssFiles[cpath.toString()] = true;
    }
    if((this.extraTemplateCssString)&&(!this.extraTemplateCssString.loaded)){
      dojo.html.insertCssText(this.extraTemplateCssString, null, cpath);
      if(!this.extraTemplateCssString){ this.extraTemplateCssString = ""; }
      this.extraTemplateCssString.loaded = true;
    }

    // Connect events
    dojo.event.connectOnce(this, "onblur", this, "updateUI");
    dojo.event.connectOnce(this, "onfocus", this, "updateUI");
    dojo.event.connectOnce(this, "onkeyup", this, "updateUI");

    // Either 1) validation on empty values triggered as result of conditional expression
    // or 2) validation triggered as result of conditional expression
    if (this.requiredIf || this.validateIf) {
      dojo.event.connectOnce("before", this, "onblur", this, "maybeValidate");
      dojo.event.connectOnce("before", this, "onfocus", this, "maybeValidate");
      dojo.event.connectOnce("before", this, "onkeyup", this, "maybeValidate");
    }

    // only relevant if SubtotalTextbox widgets are pointing at this textbox to get their total
    if (atg.widget.validation.totalWidgetMap) {
      for (var totalId in atg.widget.validation.totalWidgetMap) {
        if (totalId === this.widgetId) {
          var subtotalTextbox = atg.widget.validation.totalWidgetMap[totalId][0];
          if (subtotalTextbox) {
            dojo.event.connectOnce(this, "onblur", subtotalTextbox, "isInRange");
            dojo.event.connectOnce(this, "onfocus", subtotalTextbox, "isInRange");
            dojo.event.connectOnce(this, "onkeyup", subtotalTextbox, "isInRange");
          }
          break;
        }
      }
    }

    // Hide unnecessary span tags that popup in the wrong places and show hard-coded
    // dojo validation messages.
    // (Maybe dojo will allow turning this off through the configuration in a future release.)
    //
    // For now, we will insert an undisplayed node into the widget hierarchy and move the
    // hard-coded span tags into this undisplayed node.
    // (Since the base class code refers to them, we need to keep the references around.)
    if (this.invalidSpan) {
      var hider = document.createElement("span");
      hider.style.display = "none";
      var parent = this.invalidSpan.parentNode;
      hider.appendChild(parent.removeChild(this.invalidSpan));
      hider.appendChild(parent.removeChild(this.missingSpan));
      hider.appendChild(parent.removeChild(this.rangeSpan));
      parent.appendChild(hider);
    }

    // Set autocomplete if defined
    if (this.autocomplete && this.textbox) {
      this.textbox.autocomplete = this.autocomplete;
    }

    // Set tabindex if defined
    if (!isNaN(this.tabindex) && this.textbox) {
      this.textbox.tabIndex = this.tabindex;
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

  postCreate: function() {
    // Invoke the hierarchy's native postCreate method that we've replaced
    if (this._postCreate_fn) {
      this._postCreate_fn.apply(this, arguments);
    }
    // Register with the submit button(s)
    atg.widget.validation.SubmitManager.addValidationWidget(this);
    // Fix dojo API by splicing out the standard isValid function and replacing
    // with a function that performs the required promptMessage check from within the API
    // so that all API users get the same behavior (not just internal dojo).
    // Any existing isValid method will still be called subject to the API-defined conditions
    // (such as, is there any promptMessage?)
    if (this.isValid && !this._originalIsValid_fn) {
      this._originalIsValid_fn = this.isValid;
      this.isValid = function() {
        var result = true;
        // this required check borrowed from dojo ValidationTextbox
        // for benefit of anyone who needs to test validity
        if (this.promptMessage != this.getValue()) {
          result = this._originalIsValid_fn.apply(this, arguments);
        }
        return result;
      };
    }
    // initial validation since value is in widget now
    this.doValidation();
  },

  destroy: function() {
    dojo.debug("ValidationTextboxEx widget destroy() was called");
    atg.widget.validation.SubmitManager.removeValidationWidget(this);
    dojo.widget.ValidationTextbox.superclass.destroy.call(this);
  }
}

// Include base validation properties
dojo.lang.extend(dojo.widget.ValidationTextbox, atg.widget.validation.ValidationBase);

// Since we're mixing in with a widget prototype, 
// make sure we rename the existing methods before replacing them.
// We'll invoke them later (if present) from our own copies of the methods.
if (dojo.widget.ValidationTextbox.prototype.postMixInProperties) {
  dojo.widget.ValidationTextbox.prototype._postMixInProperties_fn = dojo.widget.ValidationTextbox.prototype.postMixInProperties;
}
if (dojo.widget.ValidationTextbox.prototype.postCreate) {
  dojo.widget.ValidationTextbox.prototype._postCreate_fn = dojo.widget.ValidationTextbox.prototype.postCreate;
}

// Include base validation text box properties
dojo.lang.extend(dojo.widget.ValidationTextbox, atg.widget.validation.ValidationTextboxBase);
