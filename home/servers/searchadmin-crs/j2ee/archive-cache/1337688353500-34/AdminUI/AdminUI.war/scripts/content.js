// and add event checkForm() to all link at current page in rightFrame
function initCheckForm() {
  top.uiConfig.fieldsChanged = false;
  initCheckFields(document.getElementsByTagName("input"));
  initCheckFields(document.getElementsByTagName("select"));
  initCheckFields(document.getElementsByTagName("textarea"));
}
function initCheckFields(fields) {
  for (var i = 0; i < fields.length; i++) {
    var f = fields[i];
    if (f.name != null && f.name != "" && f.name.charAt(0) != "_" && f.name.charAt(0) != "/" && // f.type != "button"
        f.className.indexOf("unchanged") < 0) {
      addEvent(f, f.type == "checkbox" ? "click" : "change", setChangeFlag);
      if (f.type == "text" || f.nodeName == "TEXTAREA"){
        addEvent(f, "keypress", setChangeFlag);
      }
    }
  }
}

function checkForm() {
  alerting.showMessages();
  return true;
}

// add event setChangeFlag() to all visible input fields at current page
function setChangeFlag(){
  top.uiConfig.fieldsChanged = true;
}

/* Sets focus on some element. By default it uses first possible element of the first form. */
function setFormFocus() {
  var focusFunction = focusFirstFormElement;
  if (typeof focusFormElement == "function") {
    focusFunction = focusFormElement;
    focusFormElement = null;
  }
  focusFunction();
}

//open appropriate selected child edit child page (ex. term_edit )
function onSelectLinkChange(selectInput) {
  if (selectInput.value != "") {
    loadRightPanel(selectInput.value);
    selectInput.selectedIndex = 0;
  }
}

function openSeparateWindow(pUrl, pName, pWidth, pHeight) {
  var specs = 'resizable=yes,scrollbars=yes,toolbar=no,status=yes';
  if (pWidth && pHeight) {
    specs = "toolbar=no,location=no,directories=no,status=no,menubar=no,scrollbars=yes,resizable=no," +
        "width=" + pWidth + ",height=" + pHeight;
  }
  var w = window.open(pUrl, pName || '_blank', specs);
  if (w.focus) {
    w.focus();
  }
  return false;
}

function showHideSettings(pSettingsId, pShow) {
  document.getElementById(pSettingsId).style.display = pShow ? "" : "none";
  document.getElementById(pSettingsId + ":hideLink").style.display = pShow ? "" : "none";
  document.getElementById(pSettingsId + ":showLink").style.display = pShow ? "none" : "";
  return false;
}
