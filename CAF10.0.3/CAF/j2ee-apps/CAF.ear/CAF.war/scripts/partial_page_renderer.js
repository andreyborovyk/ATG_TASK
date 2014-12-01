// Please note: The partial page renderer requires the atg-ui_common.js script to be included on the page.
function Action(id) {

  // TODO: these should come from the form
  this.action = "";
  this.bean = "";
  this.errorUrl = "";
  this.handler = "";
  this.requestUri = "";
  this.successUrl = "";

  this.id = id;
  this.queryParameters = new Array();
  this.sourceElements = new Array();
  this.targetElements = new Array();
  this.formData = null;
  this.xmlHttpRequest = null;

}
// We only need one copy of these functions
Action.prototype.addForm = ActionAddForm;
Action.prototype.addQuery = ActionAddQuery;
Action.prototype.clearElements = ActionClearElements;
Action.prototype.getProtocol = function() { return this.protocol; };
Action.prototype.registerSourceElement = ActionRegisterSourceElement;
Action.prototype.registerTargetElement = ActionRegisterTargetElement;
Action.prototype.retransact = ActionRetransact;
Action.prototype.setHideElements = function (elementIds) { this.hideElementIds = elementIds; };
Action.prototype.setHistoryUrl = ActionSetHistoryUrl;
Action.prototype.setShowElements = function (elementIds) { this.showElementIds = elementIds; };
Action.prototype.setSynchronizeTransaction = ActionSetSynchronizeTransaction;
Action.prototype.transact = ActionTransact;
// These are defaults that can be overridden by instances if necessary
Action.prototype.allowWaitIndicator = true;
Action.prototype.protocol = "formhandlers";
Action.prototype.redirectToErrorUrl = false;
Action.prototype.synchronizeTransaction = false;
Action.prototype.useRequestDispatcherForward = true;
  
function ActionAddForm(name, value, isNewConnection) {
  if (!name || !value || name == "")
    return;

  if (!this.formData) {
    this.formData = "";
  }

  var encodedValue = encodeURIComponent(value);
  if (this.protocol == "formhandlers") {
    var property = this.bean + "." + name;
    this.formData += encodeURIComponent("_D:" + property) + "=+&";
    this.formData += encodeURIComponent(property) + "=" + encodedValue + "&";
  }
  else {
    this.formData += encodeURIComponent(name) + "=" + encodedValue + "&";
  }

  // Browser history
  if (this.historyUrl && isNewConnection) {
    if (!this.historyQueryString) {
      this.historyQueryString = "?";
    }
    this.historyQueryString += name + "=" + encodedValue + "&";
  }
}
function ActionAddQuery(name, value) {
  var queryParameter = new Object();
  queryParameter.name = name;
  queryParameter.value = value;
  this.queryParameters.push(queryParameter);
}
function ActionClearElements() {
  this.queryParameters = new Array();
  this.sourceElements = new Array();
  this.targetElements = new Array();
}
function ActionRetransact(url) {
  if (window.__ajax_impl) {
    window.__ajax_impl.retransactAction(this, url);
  }
}
function ActionRegisterSourceElement(elementId, formName, valueAccessorScript, value, isHistoryTitle, isQuery) {
  var sourceElement = new Object();
  sourceElement.elementId = elementId;
  sourceElement.formName = formName;
  sourceElement.isHistoryTitle = isHistoryTitle;
  sourceElement.isQuery = isQuery;
  sourceElement.valueAccessorScript = valueAccessorScript;
  sourceElement.value = value;
  this.sourceElements.push(sourceElement);
}
function ActionRegisterTargetElement(elementId, pathToXslt, xsltString, valueAccessorScript) {
  var targetElement = new Object();
  targetElement.elementId = elementId;
  targetElement.isXmlTarget = function () { return (this.pathToXslt != null || this.xsltString != null) ? true : false; };
  targetElement.pathToXslt = pathToXslt;
  targetElement.xsltString = xsltString;
  targetElement.valueAccessorScript = valueAccessorScript;
  this.targetElements.push(targetElement);
}
function ActionSetHistoryUrl(historyUrl) {
  // Browser history
  this.historyFrame = document.getElementById("__ppr_history_element__");
  if (this.historyFrame) {
    this.historyQueryString = null;
    this.historyUrl = historyUrl;
  }
}
function ActionSetSynchronizeTransaction(synchronizeTransaction) {
  this.synchronizeTransaction = synchronizeTransaction;
}
function ActionTransact() {
  if (window.__ajax_impl) {
    window.__ajax_impl.transactAction(this);
  }
}

function PartialPageRenderer() {
  this.debugRegularExpression = /((\x0d\x0a){1,} *)+/gi;
  this.elementSeparator = "<701cf83a4e9f>";

  if (!(window.__ajax_impl)) {
    window.__ajax_impl = this;
  }

  this.applyStylesheet          = PartialPageRendererApplyStylesheet;
  this.completeTransaction      = PartialPageRendererCompleteTransaction;
  this.connect                  = PartialPageRendererConnect;
  this.createErrorMessage       = PartialPageRendererCreateErrorMessage;
  this.createXmlHttpRequest     = PartialPageRendererCreateXmlHttpRequest;
  this.getIsXmlRequest          = PartialPageRendererGetIsXmlRequest;
  this.getXmlDocumentFromString = PartialPageRendererGetXmlDocumentFromString;
  this.getXmlDocumentFromUrl    = PartialPageRendererGetXmlDocumentFromUrl;
  this.prepareTransaction       = PartialPageRendererPrepareTransaction;
  this.readyStateChange         = PartialPageRendererReadyStateChange;
  this.retransactAction         = PartialPageRendererRetransactAction;
  this.startAction              = PartialPageRendererStartAction;
  this.stopAction               = PartialPageRendererStopAction;
  this.transactAction           = PartialPageRendererTransactAction;
  this.updateTargetElement      = PartialPageRendererUpdateTargetElement;
  
  this.beforeTransact           = new Function("return true");

  // Opera support
  this.iframe = null;

  try {
    if (is.gecko) {
      Document.prototype.loadXML = function (s) {
        // Parse the string to a new doc
        //
        var doc2 = (new DOMParser()).parseFromString(s, "text/xml");
    
        // Remove all initial children
        //
        while (this.hasChildNodes()) {
          this.removeChild(this.lastChild);
        }
  
        // Insert and import nodes
        for (var i = 0; i < doc2.childNodes.length; i++) {
          this.appendChild(this.importNode(doc2.childNodes[i], true));
        }
      }
    }
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "1.", this.createErrorMessage(null));
  }
}
function PartialPageRendererApplyStylesheet(xml, xslt) {
  var html = "";
  try {
    if (window.XSLTProcessor) { // pretty much everyone else
      var xsltProcessor = new XSLTProcessor();
      xsltProcessor.importStylesheet(xslt);
      var docFragment = xsltProcessor.transformToFragment(xml, document);
      var div = document.createElement("div");
      div.appendChild(docFragment);
      html = div.innerHTML;
    }
    else if (is.ie) { // IE
      html = xml.transformNode(xslt);
    }
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error applying stylesheet", this.createErrorMessage(null));
  }
  return html;
}
function PartialPageRendererAtgUiCommonRequired(ex, location, context) {
  alert(location + "\n\n" + context + "\n\n" + ex.message);
}
function PartialPageRendererBodyOnLoad() {
  if (parent) {
    var targetElements = new Array();
    var urlParts = document.URL.split('?');
    if (urlParts.length == 2) {
      var nameValuePairs = urlParts[1].split('&');
      for (var i = 0; i < nameValuePairs.length; i++) {
        var parts = nameValuePairs[i].split('=');
        if (parts.length == 2 && parts[0].indexOf("__element") == 0) {
          var element = document.getElementById(parts[1]);
          if (element && element.innerHTML) {
            var targetElement = new Object();
            targetElement.id = parts[1];
            targetElement.html = element.innerHTML;
            targetElements.push(targetElement);
          }
          else if (element && element.value) {
            var targetElement = new Object();
            targetElement.id = parts[1];
            targetElement.value = element.value;
            targetElements.push(targetElement);
          }
        }
      }
    }
    parent.PartialPageRendererCompleteTransaction(targetElements);
  }
}
function PartialPageRendererCompleteTransaction(targetElements) {
  for (var i = 0; i < targetElements.length; i++) {
    var element = document.getElementById(targetElements[i].id);
    if (element && targetElements[i].html) {
      var span = document.createElement("span");
      span.innerHTML = targetElements[i].html;
      element.innerHTML = "";
      element.appendChild(span);
    }
    else if (element && targetElements[i].value) {
      element.value = targetElements[i].value;
    }
  }
}
function PartialPageRendererConnect(action, isNewConnection) {
  try {
    // Formhandler parameters
    if (action.protocol == "formhandlers") {
      if (action.requestUri == "") {
        action.requestUri = action.action;
      }

      action.formData = "_dyncharset=UTF-8&" + action.formData;
      action.formData += encodeURIComponent(action.bean + ".successURL") + "=" + encodeURIComponent(action.successUrl) + "&";
      action.formData += encodeURIComponent("_D:" + action.bean + ".successURL") + "=+&";
      if (action.errorUrl) {
        action.formData += encodeURIComponent(action.bean + ".errorURL") + "=" + encodeURIComponent(action.errorUrl) + "&";
        action.formData += encodeURIComponent("_D:" + action.bean + ".errorURL") + "=+&";
      }
      action.formData += encodeURIComponent(action.bean + "." + action.handler) + "=&";
      action.formData += encodeURIComponent("_D:" + action.bean + "." + action.handler) + "=+&";
      action.formData += "_DARGS=" + encodeURIComponent(action.requestUri + "." + action.id) + "&";
			if (action.useRequestDispatcherForward) {
        action.formData += "atg.formHandlerUseForwards=true&";
      }
      action.formData += "_handler=" + encodeURIComponent(action.handler) + "&";
      action.formData += "_isppr=true&";
    }

    // Submit form pairs via HTTP POST
    action.xmlHttpRequest = this.createXmlHttpRequest();
    var isAsynchronous = (action.synchronizeTransaction) ? false : true;
    var actionUrl = action.action;
    actionUrl.indexOf("?") > -1 ? actionUrl += "&" : actionUrl += "?";
    for (var i = 0; i < action.queryParameters.length; i++) {
      actionUrl += action.queryParameters[i].name + "=" + action.queryParameters[i].value + "&"; // value already encoded
    }
    if (action.protocol == "formhandlers") {
      actionUrl += "_DARGS=" + action.requestUri + "." + action.id + "&";
    }

    // The HTTP connection
    action.xmlHttpRequest.open("POST", actionUrl, isAsynchronous);
    // Browser history
    if (action.historyFrame && action.historyUrl && isNewConnection) {
      action.historyFrame.src = action.historyUrl + action.historyQueryString;
    }
    // Register XMLHTTPRequest callback
    if (!action.synchronizeTransaction) {
      var self = this;
      if (window.ActiveXObject) {
        action.xmlHttpRequest.onreadystatechange = function () { self.readyStateChange(self, action); };
      }
      else if (window.XMLHttpRequest) {
        action.xmlHttpRequest.onload = function () { self.readyStateChange(self, action); };
      }
    }
    if (this.getIsXmlRequest(action)) {
      action.xmlHttpRequest.setRequestHeader("Accept", "text/xml");
    }
    action.xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    action.xmlHttpRequest.send(action.formData);

    // Wait for XMLHTTPRequest to unblock
    if (action.synchronizeTransaction) {
      this.readyStateChange(this, action);
    }
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error connecting", this.createErrorMessage(action));
  }

  // Reset form and query data
  action.formData = null;
  action.historyQueryString = null;
  action.queryParameters = new Array();
}
function PartialPageRendererCreateErrorMessage(action) {
  var errorMessage = "";
  if (is.ie) {
    errorMessage += "Browser = Internet Explorer\n";
  }
  else if (is.gecko) {
    errorMessage += "Browser = Gecko\n";
  }
  else if (is.opera) {
    errorMessage += "Browser = Opera\n";
  }
  else {
    errorMessage += "Browser = Unknown\n";
  }
  if (action) {
    errorMessage += "Action = " + action.action + "\n";
    errorMessage += "Bean = " + action.bean + "\n";
    errorMessage += "Error URL = " + action.errorUrl + "\n";
    errorMessage += "Handler = " + action.handler + "\n";
    errorMessage += "ID = " + action.id + "\n";
    errorMessage += "Protocol = " + action.protocol + "\n";
    errorMessage += "Request URI = " + action.requestUri + "\n";
    errorMessage += "Success URL = " + action.successUrl + "\n";
    if (action.synchronizeTransaction) {
      errorMessage += "Synchronous = true\n";
    }
    else {
      errorMessage += "Synchronous = false\n";
    }
  }
  return errorMessage;
}
function PartialPageRendererCreateXmlHttpRequest() {
  var httpRequest = null;
  try {
     // do this in a more portable way
     // check the presence of the necessary object instead of a is.ie/is.gecko check
     // this will allow newer operas and gecko clones like camino to work
     var ua = navigator.userAgent.toLowerCase();
     if (!window.ActiveXObject)
       httpRequest = new XMLHttpRequest();
     else if (ua.indexOf('msie 5') == -1)
       httpRequest = new ActiveXObject("Msxml2.XMLHTTP");
     else
       httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error creating request", this.createErrorMessage(null));
  }
  return httpRequest;
}
function PartialPageRendererGetIsXmlRequest(action) {
  var isXmlRequest = false;
  for (var i = 0; i < action.targetElements.length; i++) {
    var targetElement = action.targetElements[i];
    if (targetElement.isXmlTarget) {
      isXmlRequest = true;
      break;
    }
  }
  return isXmlRequest;
}
function PartialPageRendererGetXmlDocumentFromString(xmlString) {
  var xmlDocument = null;
  try {
    if (window.ActiveXObject) { // IE
      xmlDocument = new ActiveXObject("Msxml.DOMDocument");
      xmlDocument.async = false;
      xmlDocument.resolveExternals = false;
      xmlDocument.validateOnParse = true;
      xmlDocument.loadXML(xmlString);
    }
    else if (document.implementation) { // Gecko
      xmlDocument = document.implementation.createDocument("", "", null);
      xmlDocument.loadXML(xmlString);
    }
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error getting XML document from string", this.createErrorMessage(null));
  }
  return xmlDocument;
}
function PartialPageRendererGetXmlDocumentFromUrl(xmlUrl) {
  var httpRequest = this.createXmlHttpRequest();
  httpRequest.open("GET", xmlUrl, false);
  httpRequest.send(null);
  return httpRequest.responseXML;
}
function PartialPageRendererPrepareTransaction(action, isNewConnection) {
  var success = true;
  try {
    if (is.ie || is.gecko || is.opera) {
      // Populate form pairs from source elements
      if (!action.formData) {
        action.formData = "";
      }

      // Build history query string
      if (action.historyUrl && isNewConnection && !action.historyQueryString) {
        action.historyQueryString = "?";
      }
    }
    else {
      success = false;
    }
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error preparing transaction", this.createErrorMessage(action));
    success = false;
  }
  return success;
}
function PartialPageRendererReadyStateChange(self, action) {
  if (action.xmlHttpRequest && action.xmlHttpRequest.readyState == 4 && (action.xmlHttpRequest.status == 200 || action.xmlHttpRequest.status == 500)) {

    // Exception handling to error URL
    if (action.xmlHttpRequest.status == 500 && action.redirectToErrorUrl) {
      if (is.ie) {
        document.location.pathname = action.errorUrl;
      }
      else if (window.location.pathname) {
        window.location.pathname = action.errorUrl;
      }
      return;
    }

    // Refresh target elements via XSLT
    var targetElems = action.targetElements;
    var targetElement;
    for (var i = 0; i < targetElems.length; i++) {
      targetElement = targetElems[i];
      var html = "";
      if (targetElement.isXmlTarget()) {
        var xslt = (targetElement.xsltString != null) ? self.getXmlDocumentFromString(targetElement.xsltString) : self.getXmlDocumentFromUrl(targetElement.pathToXslt);
        html = self.applyStylesheet(action.xmlHttpRequest.responseXML, xslt);
      }
      else
      {
        html = action.xmlHttpRequest.responseText;
      }
      self.updateTargetElement(targetElement, html);
    }

    // Refresh target elements and invoke target scripts via JavaScript
    var nameValuePairs = action.xmlHttpRequest.responseText.split(self.elementSeparator);
    var counter = 0;
    while (counter < nameValuePairs.length - 1) {
      counter++;
      var name = nameValuePairs[counter++];
      var mode = nameValuePairs[counter++];
      var value = nameValuePairs[counter++];
      if (!name || name == "" || !value)
        continue;

      if (name == "sessioninvalid") {
        // Value contains redirect URL for invalid session
        window.sessioninvalid = true;
        if (is.ie) {
          document.location.href = document.location.protocol + "//" + document.location.hostname + ":" + document.location.port + value;
        }
        else if (window.location.pathname) {
          window.location.pathname = value;
        }
        return;
      }
      else if (name == "javascript") {
        try {
          eval(value);
        }
        catch (ex) {
          var message = self.createErrorMessage(action);
          //alert("JavaScript Error\n" + message + ex.message);
        }
      }
      else if (document.getElementById(name)) {
        var element = document.getElementById(name);
        if (element.value) {
          if (mode == "overwrite") {
            element.value = value;
          }
          else { // append
            var temp = element.value;
            temp += value;
            element.value = temp;
          }
        }
        else {
          element.innerHTML = value;
          var scripts = element.getElementsByTagName("script");
          for (var i = 0; i < scripts.length; i++) {
            try {
              eval(scripts[i].innerHTML);
            }
            catch (ex) { // JavaScript errors are of interest
              var message = self.createErrorMessage(action);
              //alert("JavaScript Error\n" + message + ex.message);
            }
          }
        }
      }
      else {
        // Element not found with name
      }
    }
    action.xmlHttpRequest = null;
    self.stopAction(action);
  }
}
function PartialPageRendererRetransactAction(action, queryString) {
  if (!this.prepareTransaction(action, false))
    return;

  var nameValuePairs = queryString.split("&");
  for (var i = 0; i < nameValuePairs.length; i++) {
    var pairs = nameValuePairs[i].split("=");
    if (pairs.length != 2)
      continue;

    // Ignore private parameters
    if (pairs[0].indexOf("__") == 0)
      continue;

    action.addForm(pairs[0], pairs[1], false);
  }

  this.connect(action, false);
}
function PartialPageRendererStartAction(action) {
	if (action.allowWaitIndicator == false)
		return;

  if (window.javaScriptErrors && window.javaScriptErrors.length == 0 && window.__ppr_errorelements_) {
    for (var i = 0; i < window.__ppr_errorelements_.length; i++) {
      document.getElementById(window.__ppr_errorelements_[i]).style.display = 'none';
    }
  }

  if (action.synchronizeTransaction) // please wait screen functionality only works with asynchronous XmlHttpRequest
    return;

  var code = "";
  var numberString;

  var hideElements = action.hideElementIds;
  if (!hideElements) {
    hideElements = window.__ppr_hideelements_;
  }
  if (hideElements) {
    for (var i = 0; i < hideElements.length; i++) {
      numberString = String(i);
      code += "var hide" + numberString + " = document.getElementById('" + hideElements[i] + "'); ";
      code += "if (hide" + numberString + ") hide" + numberString + ".style.display = 'none'; ";
    }
  }
  var showElements = action.showElementIds;
  if (!showElements) {
    showElements = window.__ppr_showelements_;
  }
  if (showElements) {
    for (var i = 0; i < showElements.length; i++) {
      numberString = String(i);
      code += "var show" + numberString + " = document.getElementById('" + showElements[i] + "'); ";
      code += "if (show" + numberString + ") show" + numberString + ".style.display = 'block'; ";
    }
  }
  window.setTimeout(code, 0);
}
function PartialPageRendererStopAction(action) {
  if (action.synchronizeTransaction || (action.allowWaitIndicator == false)) // please wait screen functionality only works with asynchronous XmlHttpRequest
    return;

  var code = "";
  var numberString;

  var showElements = action.showElementIds;
  if (!showElements) {
    showElements = window.__ppr_showelements_;
  }
  if (showElements) {
    for (var i = 0; i < showElements.length; i++) {
      numberString = String(i);
      code += "var show" + numberString + " = document.getElementById('" + showElements[i] + "'); ";
      code += "if (show" + numberString + ") show" + numberString + ".style.display = 'none'; ";
    }
  }
  var hideElements = action.hideElementIds;
  if (!hideElements) {
    hideElements = window.__ppr_hideelements_;
  }
  if (hideElements) {
    for (var i = 0; i < hideElements.length; i++) {
      numberString = String(i);
      code += "var hide" + numberString + " = document.getElementById('" + hideElements[i] + "'); ";
      code += "if (hide" + numberString + ") hide" + numberString + ".style.display = 'block'; ";
    }
  }
  window.setTimeout(code, 1);
  if (window.__ppr_errorelements_ && window.javaScriptErrors && window.javaScriptErrors.length > 0) {
    for (var i = 0; i < window.__ppr_errorelements_.length; i++) {
      var error = document.getElementById(window.__ppr_errorelements_[i]);
      if (error) {
        error.style.display = "block";
      }
    }
  }
}
function PartialPageRendererTransactAction(action) {
  if (this.beforeTransact())
  {
    this.startAction(action);
  
    if (!this.prepareTransaction(action, true))
      return;
  
    try {
      var sourceElts = action.sourceElements;
      for (var i = 0; i < sourceElts.length; i++) {
        var se = sourceElts[i]; // compression-safe 2-char name
        var sourceElementId = se.elementId;
        var script = "";
        var value = "";
        if (se.value) {
          script = "se.value";
        }
        else if (se.elementId) {
          // When dealing with elements on the page, we need to handle radio buttons as a special case,
          // because they will be implemented in an array of elements, so we must determine which item
          // is selected, and pass that on back.
          var tmpJS = "document.getElementById('" + sourceElementId + "')";
          var obj = eval(tmpJS);
          if (obj && obj.type && obj.type == "radio") {
            tmpJS = "document.getElementsByName('" + sourceElementId + "')";
            var radioButtons = eval(tmpJS);
    
            for (var j = 0; j < radioButtons.length; j++) {
              if (radioButtons[j].checked) {
                script = "document.getElementsByName('" + sourceElementId + "')[" + j + "]." + se.valueAccessorScript;
                break;
              }
            }
          }
          else if (obj && obj.type && obj.type == "checkbox") {
            // Checkboxes are pretty simple, and we need to pass along the specific string values
            if (obj.value) {
              if (obj.checked) {
                script = "document.getElementById('" + sourceElementId + "')." + se.valueAccessorScript;
              }
              else { // if check box with value is not checked, do not submit form!
                continue;
              }
            }
            else {
              script = (obj.checked ? "true" : "false");
            }
          }
          else {
            script = "document.getElementById('" + sourceElementId + "')." + se.valueAccessorScript;
          }      
        }
        else {
          if (se.valueAccessorScript != null)
           script = se.valueAccessorScript;
        }
        if (se.isQuery) { // Query parameter
          action.addQuery(se.formName, encodeURIComponent(eval(script)));
        }
        else if (action.protocol == "formhandlers") { // ATG form header parameter
          try {
            if (script != ''){
              value = encodeURIComponent(eval(script));
            }else{
              value = '';
            }

            var property = action.bean + "." + se.formName;
            action.formData += encodeURIComponent(property) + "=" + value + "&";
            action.formData += encodeURIComponent("_D:" + property) + "=+&";
          }
          catch (ex) {
            // allow conditionally present elements with display:none to fail gracefully (Mozilla)
            // because eval will throw exceptions for these elements
          }
        }
        else { // Form header parameter
          value = encodeURIComponent(eval(script));
          action.formData += encodeURIComponent(se.formName) + "=" + value + "&";
        }
  
        // Browser history
        if (action.historyUrl) {
          action.historyQueryString += se.formName + "=" + value + "&";
          if (se.isHistoryTitle) {
            action.historyQueryString += "__title=" + value + "&";
          }
        }
      }
  
      if (action.historyUrl) {
        action.historyQueryString += "__actionId=" + action.id + "&__historyId=" + Math.random() + "&";
      }
  
      this.connect(action, true);
    }
    catch (ex) {
      PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error transacting", this.createErrorMessage(action));
    }
  }
}
function PartialPageRendererUpdateTargetElement(targetElement, html) {
  //html = html.replace(/\r\n/gi, "").replace(/\n/gi, "").replace(/\\/gi, "\\\\").replace(/\"/gi, "\\\"");

  var script = "";
  if (targetElement.elementId) {
    script = "if (document.getElementById('" + targetElement.elementId + "')) { document.getElementById('" + targetElement.elementId + "')." + targetElement.valueAccessorScript + " = \"" + html + "\"; }";
  }
  else {
    script = targetElement.valueAccessorScript;
  }
  try {
    eval(script);
  }
  catch (ex) {
  }
}
function ppr_transact(actionElementId,synch){
  if (actionElementId.indexOf("__ppr_") == -1)
  {
    actionElementId = "__ppr_" + actionElementId;
  }
  if (window[actionElementId]) {
    if (synch == true) {
      window[actionElementId].setSynchronizeTransaction(true);
    }
    window[actionElementId].transact(); 
  }
}
if (!(window.__ajax_impl)) {
  window.__ajax_impl = new PartialPageRenderer();
}
