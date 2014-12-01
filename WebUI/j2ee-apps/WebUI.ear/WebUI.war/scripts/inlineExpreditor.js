
//----------------------------------------------------------------------------//
//
// Utility functions for the inline expression editor.
//
//----------------------------------------------------------------------------//

// Create the atg.expreditor "namespace", if necessary.  (This should be
// replaced with dojo.provide("atg.expreditor") when all applications that
// use this functionality have Dojo available.

if (typeof atg == "undefined")
  atg = {};
if (typeof atg.expreditor == "undefined")
  atg.expreditor = {

    // A div representing the popup terminal menu.
    _terminalMenu: null,

    // Indicates if the terminal menu is currently visible.
    _terminalMenuVisible: false,

    // Windows that currently have event handlers to pop down the menu.
    _popdownEventTargets: [],

    // The current literal text editor, if any.
    _textField: null,

    // The anchor label that launched the terminal menu.
    _menuAnchor: null,

    // A temporary copy of _menuAnchor that is saved while displayTerminalMenu
    // is executing.
    _menuAnchorTemp: null,

    // Indicates if there are pending changes in the terminal menu.
    _pendingChanges: false,

    // The container ID for the current expression editor.
    _containerId: null,

    // A unique ID for the expression editor.
    _editorId: null,

    // The ID of the terminal expression associated with the menu.
    _terminalId: null,

    // The current calendar date format, if any.
    _dateFormat: null,

    // The value (if any) to be displayed in the menu anchor label for a blank text-field value.
    _blankValue: null,

    // A parameter list that is being saved while applying pending changes.
    _deferredArgs: null,

    // A list of container-specific callbacks allowing special handling of the Enter key.
    _onEnterCallbacks: [],

    // Indicates if an applyPendingChanges request is currently pending.
    _requestPending: false,

    // Currently registered expression editor data.
    _expressionEditors: [],

    // Indicates if debug alerts should be displayed.
    _showDebugAlerts: false
  };

//----------------------------------------------------------------------------//
//
// Register data for a specific expression editor.
//
atg.expreditor.registerExpressionEditor = function(containerId, data) {
  atg.expreditor._expressionEditors[containerId] = data;
}

//----------------------------------------------------------------------------//
//
// Register a function to be called when the user presses the Enter key in a
// literal text field in the expression editor for a given container.  Note
// that the callback is called instead of the normal processing code.  This is
// intended to allow the Enter key to be used for initiate a search, for example.
//
atg.expreditor.registerOnEnter = function(containerId, func) {
  atg.expreditor._onEnterCallbacks[containerId] = func;
}

//----------------------------------------------------------------------------//
//
// Get the popup terminal menu, creating it if necessary.
//
atg.expreditor.getTerminalMenu = function() {
  if (atg.expreditor._terminalMenu == null) {

    // Allow the menu to be specified in the HTML.  Otherwise, create it as an
    // invisible div that is a direct child of the document body.
    var menu = document.getElementById("atg.expreditor.terminalMenu");
    if (menu == null) {
      menu = document.createElement("div");
      menu.style.position = "absolute";
      menu.style.zIndex = "2000";
      menu.style.display = "none";
      document.body.appendChild(menu);
    }
    atg.expreditor._terminalMenu = menu;
  }
  return atg.expreditor._terminalMenu;
}

//----------------------------------------------------------------------------//
//
// Hide the popup terminal menu, if it exists and is visible.
//
atg.expreditor.hideTerminalMenu = function() {
  var menu = atg.expreditor._terminalMenu;
  if (menu && atg.expreditor._terminalMenuVisible) {
    atg.expreditor._terminalMenuVisible = false;
    menu.style.display = "none";
    // Remove the onclick handlers that were installed when the menu was
    // popped up.  But ignore exceptions, since it is possible that a window
    // (e.g. the asset picker iframe) may have been deleted in the interim.
    while (atg.expreditor._popdownEventTargets.length) {
      var target = atg.expreditor._popdownEventTargets.pop();
      try {
        if (target.removeEventListener)
          target.removeEventListener("click", atg.expreditor.onPopdownEvent, false);
        else if (target.document && target.document.detachEvent)
          target.document.detachEvent("onclick", atg.expreditor.onPopdownEvent);
      }
      catch (e) {}
    }

    // Clear out the data associated with the popup operation.
    atg.expreditor._textField = null;
    atg.expreditor._menuAnchor = null;
    atg.expreditor._pendingChanges = false;
    atg.expreditor._containerId = null;
    atg.expreditor._editorId = null;
    atg.expreditor._terminalId = null;
    atg.expreditor._dateFormat = null;
    atg.expreditor._blankValue = null;
  }
}

//----------------------------------------------------------------------------//
//
// Load the popup terminal menu with the contents of the menu with the given ID,
// and display the menu under the element with the given anchor ID.  If a text
// field ID is given, assign focus to the field and select all of its text.
//
atg.expreditor.displayTerminalMenu = function(args) {
  if (atg.expreditor._requestPending) {
    if (atg.expreditor._showDebugAlerts)
      alert("displayTerminalMenu detected double click!");
    return;
  }
  atg.expreditor._deferredArgs = args;
  atg.expreditor._menuAnchorTemp = atg.expreditor._menuAnchor;
  atg.expreditor.applyPendingChanges(atg.expreditor.doDisplayTerminalMenu);
}
atg.expreditor.doDisplayTerminalMenu = function() {
  var args = atg.expreditor._deferredArgs;
  atg.expreditor._requestPending = false;

  var sourceMenu = document.getElementById(args.menuId);
  if (args.isDynamic) {
    atg.expreditor.displayMenu( sourceMenu,
                                args.textFieldId,
                                args.literalValue,
                                args.choices,
                                args.choiceClass,
                                args.currentChoiceClass,
                                args.currentChoiceIndex );
  }
  var anchor = document.getElementById(args.anchorId);
  var menu = atg.expreditor.getTerminalMenu();

  if (sourceMenu && anchor && menu) {

    // Ensure that the menu is hidden.  But do nothing else if the item that
    // was clicked was the one that launched the already visible menu.
    var sameAnchor = (atg.expreditor._menuAnchorTemp === anchor);
    atg.expreditor._menuAnchorTemp = null;
    if (sameAnchor)
      return;

    // Attach the onPopdownEvent function to the click event of the
    // top-level window and all frames therein.
    var windows = atg.expreditor._getAllWindows();
    for (var i = 0; i < windows.length; i++) {
      var target = windows[i];
      if (target.addEventListener)
        target.addEventListener("click", atg.expreditor.onPopdownEvent, false);
      else if (target.document && target.document.attachEvent)
        target.document.attachEvent("onclick", atg.expreditor.onPopdownEvent);
    }
    atg.expreditor._popdownEventTargets = windows;
    atg.expreditor._terminalMenuVisible = true;

    // Save data that was passed in.
    atg.expreditor._menuAnchor = anchor;
    atg.expreditor._containerId = args.containerId;
    atg.expreditor._editorId = args.editorId;
    atg.expreditor._terminalId = args.terminalId;
    atg.expreditor._dateFormat = args.dateFormat;
    atg.expreditor._blankValue = args.blankValue;

    // Clear out the existing menu.  Note that we can't use removeNode on the
    // children of the menu, because that does not actually destroy the nodes.
    menu.innerHTML = "";

    // Create a clone of the given "template node".  Change its ID so that it
    // won't be confused with the original node if displayTerminalMenu is
    // called again.  Since the template is invisible, we need to make the
    // clone visible so that it will show up when the popup is finally displayed.
    var suffix = "clone";
    var newMenu = sourceMenu.cloneNode(true);
    newMenu.id = sourceMenu.id + suffix;
    newMenu.style.display = "block";

    // Change the IDs of all input fields in the cloned menu so that they
    // are different from the original IDs.  This is necessary in order to
    // allow the text field to be located so it can be assigned the focus
    // (see below).
    var inputs = newMenu.getElementsByTagName("input");
    if (inputs) {
      for (var i = 0; i < inputs.length; i++)
        inputs[i].id = inputs[i].id + suffix;
    }

    // Add the cloned menu template to the popup menu.
    menu.appendChild(newMenu);

    // Get the text field, if any, for later use.
    var textField = (args.textFieldId ? document.getElementById(args.textFieldId + suffix) : null);
    atg.expreditor._textField = textField;

    // Set up the calendar, if a container id was specified.
    // NOTE: Move this to an editor-specific JS class.
    if (args.calendarContainerId) {

      // First, try to find the parent inside the menu.
      var calendarContainer = null;
      var divs = newMenu.getElementsByTagName("div");
      if (divs) {
        for (var i = 0; i < divs.length; i++) {
          if (divs[i].id == args.calendarContainerId) {
            calendarContainer = divs[i];
            break;
          }
        }
      }

      // Create and initialize the calendar.
      if (calendarContainer) {
        var calendar = new dijit._Calendar({
          onValueSelected: atg.expreditor.onCalendarSelect
        }, calendarContainer);
        calendar.setValue(args.date || new Date());
      }
    }

    // Set up enum checkboxes, if specified.
    // NOTE: Move this to an editor-specific JS class.
    if (args.isEnum) {
      var value = textField.value;
      if (value) {
        var values = value.split(",");
        var labels = newMenu.getElementsByTagName("label");
        if (labels) {
          for (var i = 0; i < labels.length; i++) {
            var label = labels[i];
            for (var j = 0; j < values.length; j++) {
              if (label.innerHTML == values[j]) {
                var checkbox = document.getElementById(label.htmlFor + "clone");
                if (checkbox)
                  checkbox.checked = true;
                break;
              }
            }
          }
        }
      }
    }

    // Display the menu under the anchor element, unless the menu contains a
    // text field, in which case we display the menu on top of the anchor.
    // Note that we position the menu twice, once before and once after making
    // it visible.  When we position it the first time, we don't yet know its
    // size, so it may initially end up partially offscreen.  After it has
    // been made visible, we can reposition it to attempt to ensure that it
    // is fully visible.
    //
    // NOTE: On Firefox, the text field must be enclosed in a div with
    // overflow=auto in order for the caret to appear.  We set this here
    // because we don't want to specify it for other browsers, since (on IE7,
    // for example) the text field may become almost invisible when the
    // scrollbar is in use.
    //
    // See https://bugzilla.mozilla.org/show_bug.cgi?id=167801
    //
    var menuLeft = atg.expreditor._getOffsetLeft(anchor);
    var menuTop = atg.expreditor._getOffsetTop(anchor);
    if (textField && textField.type == "text") {
      if (navigator.appName == "Netscape" && textField.parentNode)
        textField.parentNode.style.overflow = "auto";
    }
    else {
      menuTop += anchor.offsetHeight;
    }
    menu.style.position = "absolute";
    atg.expreditor._moveElementTo(menu, menuLeft, menuTop);
    menu.style.display = "block";
    atg.expreditor._moveElementTo(menu, menuLeft, menuTop);

    // Assign the focus to the text field and select all of its text, but,
    // to avoid browser timing issues, use a timeout to delay the selection
    // operation (ugh!).
    if (textField && textField.type == "text")
      setTimeout("atg.expreditor.selectTextField('" + textField.id + "')", 100);
  }
}

//----------------------------------------------------------------------------//
//
// Called when a mouse button is clicked while the terminal menu is visible.
//
atg.expreditor.onPopdownEvent = function(evt) {
  if (atg.expreditor._requestPending) {
    if (atg.expreditor._showDebugAlerts)
      alert("onPopdownEvent detected double click!");
    return true;
  }

  // Find the event target.
  if (evt == null) evt = event;
  var target;
  if (evt.srcElement)
    target = evt.srcElement;
  else if (evt.target)
    target = evt.target;

  // Ignore this event if it came from the existing terminal menu.
  if (atg.expreditor._isDescendant(target, atg.expreditor._terminalMenu))
    return true;

  // Ignore this event if it came from another control within the expression panel.
  if (atg.expreditor._isDescendantOfClass(target, "expreditorControl"))
    return true;

  // Pop down the terminal menu after applying any pending changes therein.
  atg.expreditor.applyPendingChanges(atg.expreditor.finishPopdownEvent);
  return true;
}

//----------------------------------------------------------------------------//
//
// Finish up with the execution of onPopdownEvent after pending changes have
// been applied.
//
atg.expreditor.finishPopdownEvent = function() {
  atg.expreditor._requestPending = false;
}

//----------------------------------------------------------------------------//
//
// Apply any pending changes on the currently visible terminal menu, if any.
// Then, call the specified callback function.
//
// IMPORTANT: If you call this from an onclick handler of an element that
// resides outside of the expression editor (e.g. Search button / Save button),
// you must apply the class "expreditorControl" to the triggering element, in
// order to prevent the atg.expreditor.onPopdownEvent function (which has also
// been attached to that element) from itself calling applyPendingChanges, in
// case it happens to fire before the element's on onclick function.  This has
// been observed to occur if the triggering element resides in the parent of an
// iframe that contains the expression editor.  (e.g. the post-2007.1 asset
// picker Search tab).
//
atg.expreditor.applyPendingChanges = function(callback) {

  // Don't allow this function to be called while a request is already pending.
  if (atg.expreditor._requestPending) {
    if (atg.expreditor._showDebugAlerts)
      alert("applyPendingChanges detected double click!");
    return;
  }

  // If there are no pending changes, just pop down the terminal menu and call
  // the callback immediately.
  if (! atg.expreditor._pendingChanges) {
    atg.expreditor.hideTerminalMenu();
    callback();
    return;
  }

  atg.expreditor._requestPending = true;

  // Currently, the only pending changes we need to worry about are changes
  // in the literal text field.  These need to be copied back into both the
  // menu anchor label AND the original text field from which the one in the
  // popup menu was cloned.

  // For the menu anchor, use the "blankValue" string, if supplied, for an
  // empty or all-white-space text field value.
  var textValue = atg.expreditor._textField.value;
  if (atg.expreditor._blankValue && textValue.replace(/^\s*(.*?)\s*$/, "$1").length == 0)
    textValue = atg.expreditor._blankValue;
  atg.expreditor._menuAnchor.innerHTML = textValue;

  // Generate a URL that will update the text field's associated literal
  // expression on the server, without re-rendering the expression panel.
  var containerId = atg.expreditor._containerId;
  var editor = atg.expreditor._expressionEditors[containerId];
  var operation = (atg.expreditor._dateFormat ? "setDate" : "updateLiteral");
  var requestUrl = editor.panelUrl;
  if (atg.expreditor._editorId)
    requestUrl += "&editorId=" + atg.expreditor._editorId;
  requestUrl += "&terminalId=" + atg.expreditor._terminalId;
  requestUrl += "&operation=" + operation;
  requestUrl += "&value=" + encodeURIComponent(atg.expreditor._textField.value);

  // Pop down the terminal menu, update the server expression, and call the callback.
  atg.expreditor.hideTerminalMenu();
  atg.expreditor._issueRequest(requestUrl, containerId, callback, null);
}

//----------------------------------------------------------------------------//
//
// Called when a terminal menu item is selected.
//
atg.expreditor.onTerminalSelect = function(index) {
  if (atg.expreditor._requestPending) {
    if (atg.expreditor._showDebugAlerts)
      alert("onTerminalSelect detected double click!");
    return;
  }

  // Generate a URL that will update the expression.
  var containerId = atg.expreditor._containerId;
  var editor = atg.expreditor._expressionEditors[containerId];
  var requestUrl = editor.panelUrl;
  if (atg.expreditor._editorId)
    requestUrl += "&editorId=" + atg.expreditor._editorId;
  requestUrl += "&terminalId=" + atg.expreditor._terminalId;
  requestUrl += "&operation=" + "updateChoice";
  requestUrl += "&value=" + index;

  // Pop down the terminal menu, and issue a request to update the
  // expression and redisplay the expression editor.
  atg.expreditor.hideTerminalMenu();
  atg.expreditor._issueRequest(requestUrl, containerId, editor.callback, editor.callbackData);
}

//----------------------------------------------------------------------------//
//
// Called when a literal text field is updated.  This just sets the flag
// indicating that there are pending changes.
//
atg.expreditor.onLiteralInput = function() {

  // NOTE: On IE, this handler will be called as a result of the loop in
  // displayTerminalMenu that appends a suffix to all input elements in the
  // menu.  We need to ignore this event in this case.
  if (window.event && window.event.propertyName && window.event.propertyName == "id")
    return true;

  atg.expreditor._pendingChanges = true;

  // We also need to call the expressionChanged callback.
  var containerId = atg.expreditor._containerId;
  var editor = atg.expreditor._expressionEditors[containerId];
  if (editor.callback)
    editor.callback(editor.callbackData);
}

//----------------------------------------------------------------------------//
//
// Called when a literal text field is updated.
//
atg.expreditor.onLiteralChange = function(evt, target) { // NB: Fix this API

  // Find the event target.
  if (!target) {
    if (evt == null) evt = event;
    if (evt.srcElement)
      target = evt.srcElement;
    else if (evt.target)
      target = evt.target;
  }

  if (target) {

    // Generate a URL that will update the expression.
    var containerId = atg.expreditor._containerId;
    var editor = atg.expreditor._expressionEditors[containerId];
    var operation = (atg.expreditor._dateFormat ? "setDate" : "updateLiteral");
    var requestUrl = editor.panelUrl;
    if (atg.expreditor._editorId)
      requestUrl += "&editorId=" + atg.expreditor._editorId;
    requestUrl += "&terminalId=" + atg.expreditor._terminalId;
    requestUrl += "&operation=" + operation;
    requestUrl += "&value=" + encodeURIComponent(target.value);

    // Pop down the terminal menu, and issue a request to update the
    // expression, including information about the current text field value,
    // and then redisplay the expression editor.
    atg.expreditor.hideTerminalMenu();
    atg.expreditor._issueRequest(requestUrl, containerId, editor.callback, editor.callbackData);
  }
}

//----------------------------------------------------------------------------//
//
// Called when a key is pressed in a literal text field.
//
atg.expreditor.onLiteralKeyPress = function(evt) {
  if (atg.expreditor._requestPending) {
    if (atg.expreditor._showDebugAlerts)
      alert("onLiteralKeyPress detected double submit!");
    return false;
  }

  // Determine which key was pressed.
  if (evt == null) evt = event;
  var charCode = evt.charCode;
  if (charCode == null || charCode == 0) charCode = evt.keyCode;
  if (charCode == null) charCode = evt.which;

  if (charCode == 13 || charCode == 3) {

    // The Enter key updates the value and submits the change, unless an
    // onEnterCallback was registered for the current container, in which
    // case we just call that.
    var containerId = atg.expreditor._containerId;
    var callback = atg.expreditor._onEnterCallbacks[containerId];
    if (callback) {
      callback();
      return false;
    }
    atg.expreditor.onLiteralChange(evt);
    return false;
  }
  else if (charCode == 27) {

    // The Esc key cancels the operation and hides the popup menu.
    atg.expreditor.hideTerminalMenu();
    return false;
  }

  // All other keys are processed normally.

  // But, on IE, we need to indicate that a change is pending here, because
  // the onpropertychange event is not fired unless more than one character
  // is added to or removed from an existing string within a text field.
  if (/msie/i.test(navigator.userAgent))
    atg.expreditor.onLiteralInput();

  return true;
}

//----------------------------------------------------------------------------//
//
// Called when a keydown event occurs in a literal text field.
//
atg.expreditor.onLiteralKeyDown = function(evt) {
  if (atg.expreditor._requestPending) {
    if (atg.expreditor._showDebugAlerts)
      alert("onLiteralKeyDown detected double submit!");
    return false;
  }

  // Ignore this event unless the browser is IE.
  if (! /msie/i.test(navigator.userAgent))
    return true;

  // If the backspace key is pressed on IE, we need to indicate that a change is
  // pending here, because the onpropertychange event is not fired unless more
  // than one character is added to or removed from an existing string within a
  // text field.
  if (event.keyCode == 8)
    atg.expreditor.onLiteralInput();

  return true;
}

//----------------------------------------------------------------------------//
//
// Called when a enum set checkbox is checked.
//
atg.expreditor.onEnumCheckboxInput = function() {

  // Transfer the checked checkbox values into the text field.
  var value = "";
  var labels = atg.expreditor._terminalMenu.getElementsByTagName("label");
  if (labels) {
    for (var i = 0; i < labels.length; i++) {
      var label = labels[i];
      var checkbox = document.getElementById(label.htmlFor + "clone");
      if (checkbox && checkbox.checked) {
        if (value)
          value += ",";
        value += label.innerHTML;
      }
    }
  }
  atg.expreditor._textField.value = value;

  // Indicate that a change is pending.
  atg.expreditor.onLiteralInput();
}

//----------------------------------------------------------------------------//
//
// Called when the user changes the date on the calendar widget.
//
atg.expreditor.onCalendarSelect = function(value) {

  // Transfer the specified date into the text field and indicate that a
  // change is pending.
  atg.expreditor._textField.value = dojo.date.locale.format(value, {selector: "date", datePattern: atg.expreditor._dateFormat});
  atg.expreditor.onLiteralInput();
}

//----------------------------------------------------------------------------//
//
// Pop down the calendar or enum-set menu.
//
atg.expreditor.popdownMenu = function() {

  // This is equivalent to hitting Enter in the calendar text field (except
  // we never invoke any onEnter callback).
  atg.expreditor.onLiteralChange(null, atg.expreditor._textField);
}

//----------------------------------------------------------------------------//
//
// Called when the button for a ButtonExpression is clicked.
//
atg.expreditor.onButtonClick = function(args) {
  if (atg.expreditor._requestPending) {
    if (atg.expreditor._showDebugAlerts)
      alert("onButtonClick detected double click!");
    return;
  }
  atg.expreditor._deferredArgs = args;
  atg.expreditor.applyPendingChanges(atg.expreditor.doOnButtonClick);
}
atg.expreditor.doOnButtonClick = function() {
  var args = atg.expreditor._deferredArgs;
  atg.expreditor._requestPending = false;

  // Issue a request to update the expression and redisplay the expression editor.
  var containerId = args.containerId;
  var editor = atg.expreditor._expressionEditors[containerId];
  var requestUrl = editor.panelUrl;
  if (args.editorId)
    requestUrl += "&editorId=" + args.editorId;
  requestUrl += "&terminalId=" + args.terminalId;
  requestUrl += "&operation=" + "clickButton";

  atg.expreditor._issueRequest(requestUrl, containerId, editor.callback, editor.callbackData);
}

//----------------------------------------------------------------------------//
//
// Perform a given operation on a given expression.
//
atg.expreditor.performOperation = function(args) {
  if (atg.expreditor._requestPending) {
    if (atg.expreditor._showDebugAlerts)
      alert("performOperation detected double click!");
    return;
  }
  atg.expreditor._deferredArgs = args;
  atg.expreditor.applyPendingChanges(atg.expreditor.doPerformOperation);
}
atg.expreditor.doPerformOperation = function() {
  var args = atg.expreditor._deferredArgs;
  atg.expreditor._requestPending = false;

  // Issue a request to update the expression and redisplay the expression editor.
  var containerId = args.containerId;
  var editor = atg.expreditor._expressionEditors[containerId];
  var requestUrl = editor.panelUrl;
  if (args.editorId)
    requestUrl += "&editorId=" + args.editorId;
  requestUrl += "&terminalId=" + args.terminalId;
  requestUrl += "&operation=" + args.operation;
  if (args.value)
    requestUrl += "&value=" + args.value;

  atg.expreditor._issueRequest(requestUrl, containerId, editor.callback, editor.callbackData);
}

//----------------------------------------------------------------------------//
//
// Called when a button is clicked in a targeting expression panel.
//
atg.expreditor.performTargetingOperation = function(args) {
  if (atg.expreditor._requestPending) {
    if (atg.expreditor._showDebugAlerts)
      alert("performTargetingOperation detected double click!");
    return;
  }
  atg.expreditor._deferredArgs = args;
  atg.expreditor.applyPendingChanges(atg.expreditor.doPerformTargetingOperation);
}
atg.expreditor.doPerformTargetingOperation = function() {
  var args = atg.expreditor._deferredArgs;
  atg.expreditor._requestPending = false;

  // Issue a request to update the expression and redisplay the expression editor.
  var containerId = args.containerId;
  var editor = atg.expreditor._expressionEditors[containerId];
  var requestUrl = editor.panelUrl;
  if (args.editorId)
    requestUrl += "&editorId=" + args.editorId;
  if (args.rulesetGroupIndex)
    requestUrl += "&rulesetGroupIndex=" + args.rulesetGroupIndex;
  if (args.rulesetIndex)
    requestUrl += "&rulesetIndex=" + args.rulesetIndex;
  if (args.ruleIndex)
    requestUrl += "&ruleIndex=" + args.ruleIndex;
  requestUrl += "&operation=" + args.operation;

  atg.expreditor._issueRequest(requestUrl, containerId, editor.callback, editor.callbackData);
}

//----------------------------------------------------------------------------//
//
// Assign focus to a given text field, and select all of its text.
// (NOTE: This should be located in a more generic package)
//
atg.expreditor.selectTextField = function(id) {
  var elem = document.getElementById(id);
  if (elem) {
    elem.focus();
    elem.select();
  }
}

//----------------------------------------------------------------------------//
//
// Returns the true offsetLeft position of an element.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._getOffsetLeft = function(elem) {
  if (elem.getBoundingClientRect) { // IE
    var rect = elem.getBoundingClientRect();
    return rect.left;
  }
  else if (document.getBoxObjectFor) { // Firefox
    var box = document.getBoxObjectFor(elem);
    var left = box.x;
    while (elem) {
      if (elem.scrollLeft)
        left -= elem.scrollLeft;
      elem = elem.parentNode;
    }
    return left - 1;
  }
  else { // fallback; based on CAF/scripts/calendar_functions.js
    var left = 0;
    if (elem.offsetParent) {
      while (elem.offsetParent) {
        left += elem.offsetLeft;
        elem = elem.offsetParent;
      }
    }
    else if (elem.x)
      left += elem.x;
    return left;
  }
}

//----------------------------------------------------------------------------//
//
// Returns the true offsetTop position of an element.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._getOffsetTop = function(elem) {
  if (elem.getBoundingClientRect) { // IE
    var rect = elem.getBoundingClientRect();
    return rect.top;
  }
  else if (document.getBoxObjectFor) { // Firefox
    var box = document.getBoxObjectFor(elem);
    var top = box.y;
    while (elem) {
      if (elem.scrollTop)
        top -= elem.scrollTop;
      elem = elem.parentNode;
    }
    return top - 1;
  }
  else { // fallback; based on CAF/scripts/calendar_functions.js
    var top = 0;
    if (elem.offsetParent) {
      while (elem.offsetParent) {
        top += elem.offsetTop;
        elem = elem.offsetParent;
      }
    }
    else if (elem.y)
      top += elem.y;
    return top;
  }
}

//----------------------------------------------------------------------------//
//
// Returns the width of the current frame window.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._getWindowWidth = function() {
  if (window.innerWidth)
    return window.innerWidth;
  else if (document.documentElement && document.documentElement.clientWidth)
    return document.documentElement.clientWidth;
  else if (document.body)
    return document.body.clientWidth;
  else
    return 0;
}

//----------------------------------------------------------------------------//
//
// Returns the height of the current frame window.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._getWindowHeight = function() {
  if (window.innerHeight)
    return window.innerHeight;
  else if (document.documentElement && document.documentElement.clientHeight)
    return document.documentElement.clientHeight;
  else if (document.body)
    return document.body.clientHeight;
  else
    return 0;
}

//----------------------------------------------------------------------------//
//
// Returns the distance that the window is scrolled along the X axis.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._getWindowScrollX = function() {
  if (typeof window.pageXOffset != "undefined")
    return window.pageXOffset;
  else if (document.documentElement && typeof document.documentElement.scrollLeft != "undefined")
    return document.documentElement.scrollLeft;
  else if (document.body && typeof document.body.scrollLeft != "undefined")
    return document.body.scrollLeft;
  else
    return 0;
}

//----------------------------------------------------------------------------//
//
// Returns the distance that the window is scrolled along the Y axis.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._getWindowScrollY = function() {
  if (typeof window.pageYOffset != "undefined")
    return window.pageYOffset;
  else if (document.documentElement && typeof document.documentElement.scrollTop != "undefined")
    return document.documentElement.scrollTop;
  else if (document.body && typeof document.body.scrollTop != "undefined")
    return document.body.scrollTop;
  else
    return 0;
}

//----------------------------------------------------------------------------//
//
// Move the given element to the given position, but adjust the position, if
// necessary, to attempt to ensure that the entire element is visible.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._moveElementTo = function(elem, xpos, ypos) {

  var width = elem.offsetWidth;
  var x = xpos + width;
  var windowWidth = atg.expreditor._getWindowWidth();
  if (x > windowWidth)
    x = windowWidth - width;
  else
    x = xpos;
  x += atg.expreditor._getWindowScrollX();

  var height = elem.offsetHeight;
  var y = ypos + height;
  var windowHeight = atg.expreditor._getWindowHeight();
  if (y > windowHeight)
    y = windowHeight - height;
  else
    y = ypos;
  y += atg.expreditor._getWindowScrollY();

  elem.style.left = x + "px";
  elem.style.top  = y + "px";
}

//----------------------------------------------------------------------------//
//
// The following function is called to minimize or maximize a rule panel
// within a segment, targeter, or group.
// (NOTE: This should be located in a different package)
//
atg.expreditor.rulesDisplay = function(e, minTooltipTxt, maxTooltipTxt) {
  targ = e;

  var parent = targ.parentNode.parentNode;
  while (!(parent.className == 'containerHeader' || parent.className == 'panelHeader'))
    parent = parent.parentNode;

  var detail = atg.expreditor._getNextSibling(parent);

  detail.style.display = (detail.style.display != 'none')? "none" : "block";
  targ.className = (detail.style.display != 'none')? "iconPanelMinimize" : "iconPanelMaximize";
  targ.title = (detail.style.display != 'none')? minTooltipTxt : maxTooltipTxt;
  return false;
}

//----------------------------------------------------------------------------//
//
// Prevents firefox quirk of finding text nodes on newlines and whitespace.
// use like: nextChild=getNextSibling(child1);
// (NOTE: This should be located in a more generic package)
//
atg.expreditor._getNextSibling = function(startSibling) {
  if (endSibling = startSibling.nextSibling) {
    while(endSibling.nodeType != 1) {
      endSibling = endSibling.nextSibling;
    }
    return endSibling;
  } else {
    return false;
  }
}

//----------------------------------------------------------------------------//
//
// A utility function to get all existing windows.
//
atg.expreditor._getAllWindows = function() {

  // Find the top-level window.
  var topWindow = window;
  while (true) {
    if (!topWindow.parent || topWindow.parent === topWindow)
      break;
    topWindow = topWindow.parent;
  }

  // Return the top window and all of its subwindows.
  var windows = new Array();
  atg.expreditor._appendAllSubwindows(windows, topWindow);
  return windows;
}

//----------------------------------------------------------------------------//
//
// A utility function to append to an array a window and all of its subwindows.
//
atg.expreditor._appendAllSubwindows = function(windows, frame) {
  windows.push(frame);
  try {
    for (var i = 0; i < frame.frames.length; i++)
      atg.expreditor._appendAllSubwindows(windows, frame.frames[i]);
  }
  catch (e) {
  }
}

//----------------------------------------------------------------------------//
//
// Determine if an element is a descendant of a given ancestor.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._isDescendant = function(elem, ancestor) {
  if (ancestor) {
    while (elem) {
      if (elem === ancestor)
        return true;
      elem = elem.parentNode;
    }
  }
  return false;
}

//----------------------------------------------------------------------------//
//
// Determine if an element is a descendant of a given ancestor.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._isDescendantOfClass = function(elem, className) {
  var expr = new RegExp(className);
  while (elem) {
    if (elem.className && elem.className.match(expr))
      return true;
    elem = elem.parentNode;
  }
  return false;
}

//----------------------------------------------------------------------------//
//
// Issue a request using an XMLHttpRequest object.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._issueRequest = function(url, elementId, callback, callbackData) {

  var xmlHttpRequestObj = atg.expreditor._createXMLHttpRequestObject();
  if (!xmlHttpRequestObj) {
    alert("Error: Can't create XMLHttpRequest");
    return;
  }
  try {

    var elems = url.split("?");
    var location = elems[0];
    var formData = (elems.length > 1 ? elems[1] : null);

    document.body.style.cursor = "wait";

    xmlHttpRequestObj.open("POST", location, true);
    xmlHttpRequestObj.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

    xmlHttpRequestObj.onreadystatechange = function() {
      if (xmlHttpRequestObj.readyState == 4) {
        if (elementId) {
          var target = document.getElementById(elementId);
          if (target) {
            target.innerHTML = "";
            target.innerHTML = xmlHttpRequestObj.responseText;
          }
        }
        document.body.style.cursor = "default";
        if (callback)
          callback(callbackData, xmlHttpRequestObj.responseText);
      }
    }

    xmlHttpRequestObj.send(formData);
  }
  catch (e) {
    alert("Error: XMLHttpRequest failed: " + e.message);
  }
}

//----------------------------------------------------------------------------//
//
// Attempt to create an XMLHttpRequest object.
// (This function is not guaranteed to remain available in future releases)
//
atg.expreditor._createXMLHttpRequestObject = function() {

  if (typeof XMLHttpRequest != "undefined") {
    // Browsers such as Firefox and IE7 include XMLHttpRequest objects in JavaScript.
    return new XMLHttpRequest();
  }
  else if (typeof ActiveXObject != "undefined") {
    // On IE6, XMLHttpRequest is implemented using ActiveX.
    try {
      return new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e1) {
      try {
        return new ActiveXObject("Microsoft.XMLHTTP");
      }
      catch (e2) {}
    }
  }
  return null;
}

//----------------------------------------------------------------------------//
//
// Create menu HTML in a target div
// pLiteralId          literal id for literal editors, null for menus
// pLiteralValue       literal value for literal editors, null for menus
// pChoices            array of menu choices
// pChoiceClass        css class for the menu choices
// pCurrentChoiceClass css class for the current menu selection
// pCurrentChoiceIndex index of the current menu selection
//
atg.expreditor.displayMenu = function(
     pTargetDiv,
     pLiteralId,
     pLiteralValue,
     pChoices,
     pChoiceClass,
     pCurrentChoiceClass,
     pCurrentChoiceIndex ) {
  var menuhtml = "";
  if (pLiteralId != null) {
    menuhtml += "<div><input type=\"text\" id=\"" + pLiteralId + "\" ";
    menuhtml += "value=\"" + pLiteralValue + "\" ";
    menuhtml += "onkeydown=\"return atg.expreditor.onLiteralKeyDown(event)\" ";
    menuhtml += "onkeypress=\"return atg.expreditor.onLiteralKeyPress(event)\" ";
    menuhtml += "onpropertychange=\"atg.expreditor.onLiteralInput()\" ";
    menuhtml += "oninput=\"atg.expreditor.onLiteralInput()\" ";
    menuhtml += "onpaste=\"atg.expreditor.onLiteralInput()\"></div>";
  }
  menuhtml += "<table border=\"0\" cellpadding=\"2\" cellspacing=\"0\">";

  for (var i=0; i<pChoices.length; i++) {
    menuhtml += "<tr><td nowrap>";
    if (pChoices.size == 1) {
      menuhtml += "<span class=\"terminalMenuLabel\" />";
      menuhtml += pChoices[i] + "</span>";
    } else {
      var css = (i == pCurrentChoiceIndex) ? pCurrentChoiceClass : pChoiceClass;
      menuhtml += "<a class=\"" + css + "\" ";
      menuhtml += "href=\"javascript:atg.expreditor.onTerminalSelect(" + i + ")\">";
      menuhtml += pChoices[i] + "</a>";
    }
    menuhtml += "</td></tr>"
  }
  menuhtml += "</table>";

  pTargetDiv.innerHTML = menuhtml;
}
