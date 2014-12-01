dojo.registerModulePath("atg.widget.assistance", "/WebUI/dijit/assistance");
//dojo.require("atg.widget.assistance.Inline");
dojo.require("atg.widget.assistance.Popup");
dojo.require("atg.widget.assistance.Base");

function loadRightPanel(url) {
  if (!uiConfig.checkForm()) {
    return false;
  }
//  var rightPanel = dijit.byId("rightPanel");
//  rightPanel.setHref(url);
  atg.searchadmin.adminui.formsubmitter.sendGetRequest(url);
  return false;
}

function loadRightPanelContent(content) {
  alerting.showMessages();
  var rightPanel = dijit.byId("rightPanel");
  rightPanel.setContent(content);
}

function loadPopupContent(content) {
  var popup = dijit.byId("dialogContentPane");
  popup.setContent(content);
}

atg.helpFields = new Array();
atg.helpContent = {helpItem: atg.helpFields, byId: {}};
function eaRegister(id, content) {
  if (!atg.helpContent.byId[id]) {
    atg.helpFields.push({id: id, type: "popup", helpId: id, content: content});
    atg.helpContent.byId[id] = true;
  }
}
new atg.widget.assistance.Base();

var loadingStatus = {
  _errorUrl: null,
  _redirectUrl: null,
  setRedirectUrl: function(pUrl) {
    this._redirectUrl = pUrl;
  },
  setErrorUrl: function(pUrl) {
    this._errorUrl = pUrl;
  },
  needRedirect: function() {
    return this._redirectUrl || this._errorUrl;
  },
  getRedirectUrl: function() {
    var url = this._redirectUrl || this._errorUrl;
    this._redirectUrl = null;
    this._errorUrl = null;
    return url;
  }
};

function dialogLoad() {
  if (loadingStatus.needRedirect()) {
    closePopUp();
    loadRightPanel(loadingStatus.getRedirectUrl());
    return;
  }
  atg.searchadmin.adminui.formsubmitter.initialize();
  copyFooter("popupFooter", "popupFooterCopy");
  dijit.byId("popup").layout();
  if (messagesData) {
    alerting.showPopupMessages(messagesData.alerting.messages);
    messagesData = null;
  }
  if (customLoad){
    customLoad();
    customLoad = null;
  }
  setFormFocus();
}

function rightPanelLoad() {
  if (loadingStatus.needRedirect()) {
    loadRightPanel(loadingStatus.getRedirectUrl());
    return;
  }
  atg.searchadmin.adminui.formsubmitter.initialize(loadingStatus.customResponseFunction);
  loadingStatus.customResponseFunction = null;
  copyFooter("paneFooter", "paneFooterCopy");
  var viewPane = dijit.byId("viewPane");
  if (viewPane != null) {
    viewPane.layout();
  }
  addResizeEvent();
  if (messagesData) {
    alerting.showMessages(messagesData.alerting.messages, messagesData.alerting.title);
    messagesData = null;
  }
  if (customLoad) {
    customLoad();
    customLoad = null;
  }
  initCheckForm();
  if (dijit.byId('assistanceBase')) { 
    dijit.byId('assistanceBase').buildEA();
  }
  setFormFocus();
}

function setStyleText(inNode, inStyleText) {
  if (inNode.style.cssText == undefined) {
    inNode.setAttribute("style", inStyleText);
  } else {
    inNode.style.cssText = inStyleText;
  }
}

function getStyleText(inNode) {
  return (inNode.style.cssText == undefined ? inNode.getAttribute("style") : inNode.style.cssText);
}

function copyFooter(footerId, footerCopyId){
  var hiddenFooter = document.getElementById(footerId);
  var footer = document.getElementById(footerCopyId);
  if (hiddenFooter != null){
    hiddenFooter.style.display = "none";
    var elements = hiddenFooter.getElementsByTagName("*");
    var firstSubmit = false;
    for (var i = 0; i < elements.length; i++){
      var element = elements[i];
      if (element.type != "hidden" && (element.type != "submit" || element.value)) {
        var newElement = document.createElement(element.tagName);
        newElement.originalElement = element;
        element.copiedElement = newElement;
        var type = element.getAttribute("type");
        newElement.setAttribute("type",type);
        if (type != "submit"){
          newElement.className = element.className;
        } else if (!firstSubmit) {
          // fix for the bug SEARCH-166349
          firstSubmit = true;
          var form = element.form;
          if (form != null) {
            var newSubmitElement = document.createElement(element.tagName);
            newSubmitElement.originalElement = element;
            newSubmitElement.setAttribute("type", type);
            newSubmitElement.setAttribute("tabIndex", -1);
            newSubmitElement.onclick = function() {
              if (this.originalElement) {
                this.originalElement.click();
              }
              return false;
            };
            form.insertBefore(newSubmitElement, form.firstChild);
            newSubmitElement.style.position = "absolute";
            newSubmitElement.style.width = "10px";
            newSubmitElement.style.left = "-10000px";
          }
        }
        newElement.setAttribute("title",element.getAttribute("title"));
        newElement.setAttribute("value",element.getAttribute("value"));
        setStyleText(newElement, getStyleText(element));
        newElement.disabled = element.disabled;
        dojo.connect(newElement, "onclick", function(event) {
          this.originalElement.click();
        });
        footer.appendChild(newElement);
      }
    }
    footer.style.display = "";
  } else if (footer != null) {
    footer.style.display = "none";
  }
}

function addResizeEvent(){
  var rightPanel = dojo.query(".dijitSplitContainerSizerH")[0];
  dojo.connect(rightPanel, "onclick", function(evt){
    dijit.byId("viewPane").resize();
    areaSupport.onResize();
  });
}
 
dojo.addOnLoad(function() {
   //fix of dojo bug 6155: dragging on IE tends to select the underlying text
   dojo.connect(document.body, "onselectstart", function(event){
    if (dojo.dnd.manager().source) {
      dojo.stopEvent(event);
    }
   });
});

//fixing tomcat warning: "[Parameters] Parameters: Invalid chunk ignored." 
//making empty name inputs not to appear in the form
dojo._formToObject = dojo.formToObject;
dojo.formToObject = function(/*DOMNode||String*/ formNode){
  var form = dojo._formToObject(formNode);
  delete form[""];
  return form;
}