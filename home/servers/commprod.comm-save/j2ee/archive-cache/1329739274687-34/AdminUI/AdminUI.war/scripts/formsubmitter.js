dojo.provide("atg.searchadmin.adminui.formsubmitter");
dojo.require("dojo.parser");  // scan page for widgets and instantiate them

atg.searchadmin.adminui.formsubmitter =
{
  hijackClassName: "formsubmitter",
  loadingMessage: "Loading...",
  _currentRequest: null,
  _send: function(method, bindParams) {
    if (this._currentRequest && typeof this._currentRequest.cancel == "function") {
      this._currentRequest.cancel();
    }
    alerting.showMessages(new Array({type: "load", message: this.loadingMessage}));
    this._currentRequest = method(bindParams);
  },
  _finish: function() {
    this._currentRequest = null;
    alerting.showMessages();
  },

  initialize: function(customResponseFunction) {
    this.hijackAllFormSubmitNodes(customResponseFunction);
    this.customPopupHandleResponse = null;
  },

  hijackAllFormSubmitNodes: function(customResponseFunction) {
    var classForm = "." + this.hijackClassName;
    var elements = dojo.query(classForm);
    for (var i = 0; i < elements.length; i++) {
      this.hijackNode(elements[i], customResponseFunction);
    }
  },

  hijackNode: function(node, customResponseFunction) {
    if (node.isHijacked){
      return;
    }
    node.isHijacked=true;

    // Create object with common params for io.bind call
    var _this = this;

    var bindParams={
      headers: { "Accept" : "application/json,text/html" },
      handleAs: "json",
      timeout: uiConfig.ajaxTimeout,
      load: function(response, agrs) {
        _this.handleResponse(response, node, customResponseFunction);
      },
      error: function(response, agrs) {
        _this.handleError(response, node);
      }
    };
    if (node.nodeName=="INPUT") {
      // Get parent form node for this input node
      var formNode = node;
      while (formNode != null && formNode.tagName != "FORM") {
        formNode = formNode.parentNode;
      }
      if (formNode == null) {
        return;
      }
      // preventing submission of this form
      if (formNode.onsubmit == null) {
        formNode.onsubmit = function() {
          return;
        }
      }
      dojo.connect(node, "onclick", function(evt) {
        evt.preventDefault();
        if (node.disabled || node.copiedElement && node.copiedElement.disabled) {
          return false;
        }
        // Create content object with the name/value pair of the submit button that's been clicked
        // dojo.io.bind doesn't send the value of submit buttons when serializing the form as it
        // doesn't know which one has been clicked. Server side FormHandlers need this data to
        // invoke the correct formHandler method.
        var content={};
        content[node.name]=node.value;
        // Add the form node and the submit button name/value to the io.bind params
        dojo.mixin(bindParams,{
          form: formNode,
          content: content
        });
        if (formNode.getAttribute("enctype") == "multipart/form-data"){
          _this.disableNode(node, true);
          _this._send(function(bindParams) {dojo.io.iframe.send(bindParams);}, bindParams);
        } else {
          _this.disableNode(node);
          _this._send(dojo.xhrPost, bindParams);
        }
      });
    }
    else if (node.nodeName=="A"){
      dojo.connect(node, "onclick", function(evt){
        evt.preventDefault();

        // Ensure it's not a double click
        if (node.currentlyAdding && node.currentlyAdding===true){
          return;
        }
        // Add the URL of the clicked link to the io.bind params
        dojo.mixin(bindParams,{
          url: node.href
        });
        _this.disableNode(node);
        _this._send(dojo.xhrPost, bindParams);
      });
    }
    else{
      //dojo.debug("Node is not a form submit or an anchor - ignoring");
    } 
  },

  sendGetRequest: function(href) {
    // Create object with common params for io.bind call
    var _this = this;

    var bindParams={
      headers: { "Accept" : "text/html" },
      handleAs: "text",
      timeout: uiConfig.ajaxTimeout,
      load: function(response, agrs) {
        _this.publicHandleResponse(response);
      },
      error: function(response, agrs) {
        _this.handleError(response, null);
      },
      url: href
    };
    this._send(dojo.xhrGet, bindParams);
  },

  publicHandleResponse: function(data) {
    this._finish();
    var responseAlerting = data.alerting;
    closePopUp();
    if (typeof data.data == "string") {
      loadRightPanelContent(data.data);
    } else if (typeof data != "object") {
      loadRightPanelContent(data);
    }
    if (responseAlerting) {
      alerting.showMessages(responseAlerting.messages, responseAlerting.title);
      if (syncStatusMonitor.needSavingMessage){
        syncStatusMonitor.savingMessage = responseAlerting;
        syncStatusMonitor.needSavingMessage = false;
      }
    }
  },

  popupHandleResponse: function(data) {
    var responseAlerting = data.alerting;
    if (data.popupAction == "refresh") {
      if (typeof data.data == "string") {
        loadPopupContent(data.data);
      }
      if (data.alerting != null) {
        alerting.showPopupMessages(data.alerting.messages);
      }
    } else if (data.popupAction == "close") {
      if (this.customPopupHandleResponse != null) {
        this.customPopupHandleResponse();
      } else if (typeof data.data == "string") {
        loadRightPanelContent(data.data);
      }
      closePopUp();
      if (responseAlerting) {
        alerting.showMessages(responseAlerting.messages, new Array(responseAlerting.title.label, responseAlerting.title.title));
      }
    }
  },

  handleResponse: function(data, node, customResponseFunction) {
    this._finish();
    if (node) {
      this.enableNode(node);
    }
    if (customResponseFunction && customResponseFunction(data)) {
      // do nothing
    } else if (typeof data.popupAction == "string") {
      this.popupHandleResponse(data);
    } else {
      this.publicHandleResponse(data);
    }
  },

  handleError: function(data, node) {
    this._finish();
    if (node) {
      this.enableNode(node);
    }
    closePopUp();
    var message = data.message;
    var status = data.status;
    // the following block is required for dojo 1.0 and can be deleted after switching to dojo 1.4
    if (typeof status == "undefined") {
      var pos = message.lastIndexOf(":");
      if (pos >= 0) {
        status = message.substring(pos + 1);
      }
    }
    if (typeof status != "undefined" && status != null && status != "") {
      if (status == 0 || status == 12029) {
        message = uiConfig.serverUnavailableMessage;
      }
      if (status == 409) {
        message = uiConfig.sessionExpiredMessage;
      }
    }
    alerting.showMessages(new Array({type: "error", message: message}));
  },

  disableNode: function(el, needRemove) {
    var node = el.copiedElement ? el.copiedElement : el;

    // Store original properties we're about to mess with
    node.originalProps={};
    node.originalProps.width = node.style.width;
    node.originalProps.height = node.style.height;

    // Set values for width and height so element doesn't change size
    
    //node.style.width=dojo.html.getBorderBox(node).width+"px";
    //node.style.width=dojo.style.html.getBorderWidth(node);
    //node.style.width=dojo.style(node,'width')+"px";
    
    if (node.nodeName == "INPUT") {
      node.originalProps.value = node.value;
      node.disabled = true;
      node.value = top.uiConfig.responseWaiting;
    } else if (node.nodeName == "A") {
      node.originalProps.innerHTML = node.innerHTML;
      node.currentlyAdding = true;
      node.innerHTML = top.uiConfig.responseWaiting;
    }

    if (needRemove) {
      if (dojo.isFF){
        node.originalProps.nodeType = el.type;
        el.type = "hidden";
      } else if (dojo.isIE){
        node.originalProps.nodeParent = el.parentNode;
        el.parentNode.removeChild(el);
      }
    }
  },

  enableNode: function(el) {
    var node = el.copiedElement ? el.copiedElement : el;

    if (node.nodeName == "INPUT") {
      node.disabled = false;
      node.value = node.originalProps.value;
    } else if (node.nodeName == "A") {
      node.currentlyAdding = false;
      node.innerHTML = node.originalProps.innerHTML;
    }

    if (node.originalProps.nodeParent) {
      node.originalProps.nodeParent.appendChild(el);
    }
    if (node.originalProps.nodeType) {
      el.type = node.originalProps.nodeType;
    }

    // Reset size attributes
    node.style.width = node.originalProps.width;
    node.style.height = node.originalProps.height;
    node.originalProps = null;
  }

};
