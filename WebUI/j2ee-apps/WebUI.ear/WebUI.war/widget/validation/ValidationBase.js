dojo.provide("atg.widget.validation.ValidationBase");
dojo.require("dojo.event.*");
dojo.require("dojo.io.*");
dojo.require("dojo.widget.*");

/*
 * Define extension properties for all validators
 */
atg.widget.validation.ValidationBase = {
  // CONSTANTS
  //

  // Class prefix used to identify validation CSS classes applied to the inline
  // indicator
  validationClassPrefix: "atg_messaging_",

  // ATTRIBUTES
  //

  // validationEnabled: boolean
  //    Tracks whether the validation handlers are currently
  //    active based on the state of the conditional validation
  //    expressions (if any).
  //    By default validation starts in the enabled state.
  validationEnabled: true,

  // requiredIf: String
  //    Conditional expression to test before enforcing non-empty contents.
  //    The condition must return true for non-empty contents in a field to 
  //    trigger a warning and prevent the submit. 
  //    Allows a field to be conditionally required, while allowing other types of 
  //    validation to be performed on the non-empty contents of the field.
  //    Supercedes validateIf expression.
  requiredIf: "",

  // validateIf: String
  //    Conditional expression to test before validating.
  //    The condition must return true for validation to be performed.
  //    A common condition will involve comparison with a value in another element,
  //    such as to see whether a given radio button is checked before validating
  //    whether a field is required.
  //    If blank, the default behavior will occur of always performing validation.
  validateIf: "",

  // Message to show when widget contains invalid contents
  invalidMessage: "",

  // Message to show when widget contains missing contents
  missingMessage: "",

  // Message to show when widget contains out-of-range contents
  outOfRangeMessage: "",

  // missingIndicatorStyle: String
  //    The default CSS style for the inline indicator for missing values.
  //    Override this attribute to configure the style and graphics for missing indicators.
  missingIndicatorStyle: "atg_messaging_missingIndicator",

  // invalidIndicatorStyle: String
  //    The default CSS style for the inline indicator for invalid values.
  //    Override this attribute to configure the style and graphics for invalid indicators.
  invalidIndicatorStyle: "atg_messaging_invalidIndicator",

  // outOfRangeIndicatorStyle: String
  //    The default CSS style for the inline indicator for missing values.
  //    Override this attribute to configure the style and graphics for missing indicators.
  outOfRangeIndicatorStyle: "atg_messaging_outOfRangeIndicator",

  // inlineIndicator: String
  //    The ID of a DOM element to insert inline indicator graphic.
  inlineIndicator: "",

  // currentValidationClass: String
  //    The current validation CSS class of the inline indicator
  currentValidationClass: "",

  // messageBar: String
  //    The widget ID of the message bar.
  messageBar: "",

  // When this widget is validated, should the entire form be validated as well.
  // Set this flag if the widget being tested has a dependency on the state of
  // another widget in the form, so that the whole form can be re-validated.
  validateAllWidgets: "",

  // Conditional expression to test whether input widget is valid.
  // When the expression is false, the widget will display the invalid indicators.
  // For example, this can be used to implement a custom validation behavior on a widget.
  // The validIf property can be a simple expression or a function.apply() where
  // the boolean result is evaluated to determine the valid state of the widget.
  validIf: "",

  // doValidation: Function
  // This method causes the submit manager to validate this widget against any submit
  // buttons that are in the form.
  // Called initially for each widget after creation. After widget creation this method
  // is called to do the post validation. Also this method can be called to do the validation and
  // update the indicators from the page.
  doValidation: function() {
    // initial validation for this widget
    this.updateUI();
    // this will cover already-existing submit buttons ONLY
    // (code in SubmitButton.js takes care of not-yet-existing sb's)
    atg.widget.validation.SubmitManager.validateWidget(this);
  },

  // doFormValidation: Function
  // This method causes the entire form to validate which is useful if there are
  // validation dependencies between widgets.
  doFormValidation: function() {
    // Validate the whole form that this widget is in
    atg.widget.validation.SubmitManager.validateForm(this);
  },

  // getInitialIndicatorStyle: Function
  // Determines the correct validation style to which to restore the inline indicators
  // when widget is changing from invalid state to valid state
  getInitialIndicatorStyle: function() {
    var style = "";
    var element = dojo.byId(this.inlineIndicator);
    if (!element) return style;
    if (!atg.widget.validation.ValidationBase.initialValidationClasses) {
      atg.widget.validation.ValidationBase.initialValidationClasses = {};
    }
    // Get the original validation CSS class of the inline indicator when the page was loaded
    style = atg.widget.validation.ValidationBase.initialValidationClasses[element.id];
    if (!style) {
      var maybeValidationClass = "";
      var initialClasses = dojo.html.getClasses(element);
      for (var i = 0, l = initialClasses.length; i < l; i++) {
        maybeValidationClass = initialClasses[i];
        if (maybeValidationClass.indexOf(this.validationClassPrefix) == 0) {
          break;
        }
        maybeValidationClass = "";
      }
      atg.widget.validation.ValidationBase.initialValidationClasses[element.id] = style = maybeValidationClass;
    }
    return style;
  },

  // Remove validation-related styles from inline indicator
  removeInlineIndicatorStyles: function(element) {
    var classes = dojo.html.getClasses(element);
    for (var i = 0, l = classes.length; i < l; i++) {
      if (classes[i].indexOf(this.validationClassPrefix) == 0) {
        dojo.html.removeClass(element, classes[i]);
      }
    }
  },

  // Set inline indicator CSS style
  setInlineIndicatorStyle: function(style) {
    var element = dojo.byId(this.inlineIndicator);
    this.removeInlineIndicatorStyles(element);
    dojo.html.addClass(element, style);
  },

  // Implement standard isValid interface:
  // Replace the existing isValid method with a new one based on the evaluation
  // of the conditional expression
  isValid : function() {
    var result = true;
    if (this.validIf) {
      result = eval(this.validIf);
    }
    return result;
  },

  // maybeValidate: Function
  //    Determines whether validation can proceed based on the
  //    result of a conditional expression in the requiredIf or validateIf attributes.
  //    The requiredIf attribute takes priority over the validateIf attribute.
  maybeValidate: function() {
    if (!this.validateIf && !this.requiredIf)
      return;

    try {
      var result = this.requiredIf ? eval(this.requiredIf) : eval(this.validateIf);
      if (!result) {
        if (this.validationEnabled) { // Turn off validation
          // Store existing functions in holder vars and blank out references
          if (this.requiredIf) {
            dojo.debug("Disabling required handlers for widget " + this.widgetId);
            this._isMissing_fn = this.isMissing;
            this.isMissing = function() { return false; };
            // Rules changed, validation needs to happen again
            this.update();
            this.updateUI();
          }
          else { // this.validateIf
            dojo.debug("Disabling validation handlers for widget " + this.widgetId);
            this._update_fn = this.update;
            this._isMissing_fn = this.isMissing;
            this._isValid_fn = this.isValid;
            this._isInRange_fn = this.isInRange;
            this._updateUI_fn = this.updateUI;
            this.update = function() {};
            this.isMissing = function() { return false; };
            this.isValid = function() { return true; };
            this.isInRange = function() { return true; };
            this.updateUI = function() {};
            // Clear out messages and indicators
            this.setInlineIndicatorStyle(this.getInitialIndicatorStyle());
            var messageBarWidget = dojo.widget.byId(this.messageBar);
            if (messageBarWidget) {
              messageBarWidget.removeMessagesForWidget(this.widgetId);
            }
          }
          this.validationEnabled = false;
        }
      }
      else {
        if (!this.validationEnabled) { // Turn on validation again
          // Restore function references and blank out holder vars
          if (this.requiredIf) {
            dojo.debug("Restoring required handlers for widget " + this.widgetId);
            this.isMissing = this._isMissing_fn;
          }
          else { // this.validateIf
            dojo.debug("Restoring validation handlers for widget " + this.widgetId);
            this.update = this._update_fn;
            this.isMissing = this._isMissing_fn;
            this.isValid = this._isValid_fn;
            this.isInRange = this._isInRange_fn;
            this.updateUI = this._updateUI_fn;
            this._update_fn = null;
            this._updateUI_fn = null;
          }
          this.validationEnabled = true;
          // Invoke validation to sync up messaging
          this.update();
          this.updateUI();
        }
      }
    }
    catch (e) {
      // if syntax is wrong, insert debug expression and proceed with validation
      dojo.debug("Bad syntax in conditional validation expression \"", 
                 this.requiredIf ? this.requiredIf : this.validateIf,
                 "\" in widget ", this.widgetId, e);
    }
  },

  // updateUI: Function
  //    Update various parts of the UI based on validity of
  //    textbox contents.
  //    Combine in one method so just one event join point is needed.
  updateUI: function(evt) {
    // Update inline indicator with graphic
    this.updateInlineIndicator(evt);

    // Update message bar with message
    this.updateMessageBar(evt);

    // Revalidate submit manager
    atg.widget.validation.SubmitManager.validateWidget(this);
  },

  /*
   * Define standard refreshing of update indicators
   */
  updateInlineIndicator: function(evt) {
    // The widget has its own private indicator (usually)
    var indicatorElement = dojo.byId(this.inlineIndicator);
    if (indicatorElement) {
      dojo.debug("Updating inline indicators for widget ", this.widgetId);
      this.setInlineIndicatorStyle(this.getInitialIndicatorStyle());
      if (!(dojo.lang.isUndefined(this._originalIndicatorTitle))) {
        indicatorElement.title = this._originalIndicatorTitle;
      }
      if (this.isMissing()) {
        (indicatorElement.title) ? this._originalIndicatorTitle = indicatorElement.title : this._originalIndicatorTitle = "";
        this.setInlineIndicatorStyle(this.missingIndicatorStyle);
        indicatorElement.title = this.missingMessage;
      }
      else if (!(this.isValid())) {
        (indicatorElement.title) ? this._originalIndicatorTitle = indicatorElement.title : this._originalIndicatorTitle = "";
        this.setInlineIndicatorStyle(this.invalidIndicatorStyle);
        indicatorElement.title = this.invalidMessage;
      }
      else if (!(this.isInRange())) {
        (indicatorElement.title) ? this._originalIndicatorTitle = indicatorElement.title : this._originalIndicatorTitle = "";
        this.setInlineIndicatorStyle(this.outOfRangeIndicatorStyle);
        indicatorElement.title = this.rangeMessage;
      }
    }
  },

  /*
   * Define standard message management in message bar
   */
  updateMessageBar: function(evt) {
    // The widget shares the message bar with all the widgets on the page
    var messageBarWidget = dojo.widget.byId(this.messageBar);
    if (messageBarWidget) {
      dojo.debug("Updating message bar for widget ", this.widgetId);
      // Intentionally avoid wiping out everything and starting over:
      // Although removing all messages and re-adding requires less code,
      // it causes the widget to refresh for each connected event (keyup, blur, focus, etc.)
      // instead just when there is a change
      if (this.isMissing()) {
        messageBarWidget.removeLastMessageBySummary(this.invalidMessage);
        messageBarWidget.removeLastMessageBySummary(this.rangeMessage);
        messageBarWidget.addMessage({summary: this.missingMessage,
                                     type: "warning",
                                     priority: -5,
                                     datetime: new Date(),
                                     origin: "client",
                                     sourceId: this.widgetId});
      }
      else {
        messageBarWidget.removeLastMessageBySummary(this.missingMessage);
        if (!(this.isValid())) {
          messageBarWidget.removeLastMessageBySummary(this.rangeMessage);
          messageBarWidget.addMessage({summary: this.invalidMessage,
                                       type: "error",
                                       priority: 10,
                                       datetime: new Date(),
                                       origin: "client",
                                       sourceId: this.widgetId});
        }
        else {
          messageBarWidget.removeLastMessageBySummary(this.invalidMessage);
          if (!(this.isInRange())) {
            messageBarWidget.addMessage({summary: this.rangeMessage,
                                         type: "error",
                                         priority: 10,
                                         datetime: new Date(),
                                         origin: "client",
                                         sourceId: this.widgetId});
          }
          else {
            messageBarWidget.removeLastMessageBySummary(this.rangeMessage);
          }
        }
      }
    }
  }
};
