/*<ATGCOPYRIGHT>
 * Copyright (C) 2009 Art Technology Group, Inc.
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Art Technology Group.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * Art Technology Group (ATG) MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ATG SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING,
 * MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 * "Dynamo" is a trademark of Art Technology Group, Inc.
 </ATGCOPYRIGHT>*/

//----------------------------------------------------------------------------//

/**
 * Asset Manager utility functions.
 *
 * @version $Id$$Change$
 * @updated $DateTime$$Author$
 */

// List of functions to be called by the fireOnLoad function.
var onLoadFunctions = new Array();

// List of functions to be called by the fireOnResize function.
var onResizeFunctions = new Array();

// List of functions to be called by the fireOnSubmit function.
var onSubmitFunctions = new Array();

// List of functions to be called by the fireOnActivate function.
var onActivateFunctions = new Array();

// List of functions to be called by the fireOnModified function.
var onModifiedFunctions = new Array();

// List of functions to be called by the fireOnSaved function.
var onSavedFunctions = new Array();

// A reference to an XMLHttpRequest object.
var xmlHttpRequestObj = null;

// Indicates if an XMLHttpRequest is active.
var xmlHttpRequestObjReady = true;

// the dirty flag for the currently selected asset.
var assetModified = false;

// start listening for modified events (not until page is fully loaded)
var listenModified = false;

// set to true when you don't want to pop up the save confirmation message
var disableSaveConfirmation = false;

// don't clear assetModified flag if it's only a tab change
var tabChange = false;

// The default title for the "showAssetPickerForItemToken" asset picker.
var defaultAssetPickerTitle = "Select an item";

// set to true when the page contains a java applet
var usesApplets = false;

//----------------------------------------------------------------------------//
//
// Register a function to be called by the fireOnLoad function.
//
function registerOnLoad(func){
  onLoadFunctions[onLoadFunctions.length] = func;
}

//----------------------------------------------------------------------------//
//
// Register a function to be called by the fireOnResize function.
//
function registerOnResize(func){
  onResizeFunctions[onResizeFunctions.length] = func;
}

//----------------------------------------------------------------------------//
//
// Register a function to be called by the fireOnSubmit function.
//
function registerOnSubmit(func){
  onSubmitFunctions[onSubmitFunctions.length] = func;
}

//----------------------------------------------------------------------------//
//
// Register a function to be called by the fireOnActivate function.
//
function registerOnActivate(id, func){
  onActivateFunctions[id] = func;
}


//----------------------------------------------------------------------------//
//
// Register a function to be called by the fireOnModified function.
//
function registerOnModified(func){
  onModifiedFunctions[onModifiedFunctions.length] = func;
}

//----------------------------------------------------------------------------//
//
// Call all of the functions which were registered using registerOnLoad.
//
function fireOnLoad() {
  for (var i = 0; i < onLoadFunctions.length; i++)
    onLoadFunctions[i]();
}


//----------------------------------------------------------------------------//
//
// Call all of the functions which were registered using registerOnResize.
//
function fireOnResize() {
  for (var i = 0; i < onResizeFunctions.length; i++)
    onResizeFunctions[i]();
}

//----------------------------------------------------------------------------//
//
// Call all of the functions which were registered using registerOnSubmit.
//
function fireOnSubmit() {
  for (var i = 0; i < onSubmitFunctions.length; i++)
    onSubmitFunctions[i]();
}

//----------------------------------------------------------------------------//
//
// Call the given function that was registered using registerOnActivate.
//
function fireOnActivate(id) {
  if (onActivateFunctions[id])
    onActivateFunctions[id]();
}


//----------------------------------------------------------------------------//
//
// Call all of the functions which were registered using registerOnModified
//
function fireOnModified() {
  for (var i = 0; i < onModifiedFunctions.length; i++)
    onModifiedFunctions[i]();
}


//----------------------------------------------------------------------------//
//
// Call all of the functions which were registered using registerOnSaved
//
function fireOnSaved() {
  for (var i = 0; i < onSavedFunctions.length; i++)
    onSavedFunctions[i]();
}

//----------------------------------------------------------------------------//
//
// Issue a request for a given URL using an XMLHttpRequest object.  Load the
// resulting HTML into the element with the given ID, if supplied.  Call the
// given function, if supplied, with the specified callback data.
//
// PR 128848: We currently ignore calls to this function while a request is
// in progress, unless the "async" flag is supplied.
//
function issueRequest(url, elementId, callback, callbackData, async) {

  // Create the XMLHttpRequest object if not already done.
  if (! xmlHttpRequestObj)
    ensureXMLHttpRequestObject();

  if (xmlHttpRequestObj && (xmlHttpRequestObjReady || async)) {
    xmlHttpRequestObjReady = false;
    try {
      document.body.style.cursor = "wait";

      xmlHttpRequestObj.open("GET", url, true);

      xmlHttpRequestObj.onreadystatechange = function() {
        if (xmlHttpRequestObj.readyState == 4) {
          xmlHttpRequestObjReady = true;
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

      xmlHttpRequestObj.send(null);
    }
    catch (e) {
      xmlHttpRequestObjReady = true;
    }
  }
}

//----------------------------------------------------------------------------//
//
// Attempt to create an XMLHttpRequest object.
//
function ensureXMLHttpRequestObject() {

  if (xmlHttpRequestObj != null)
    return;

  if (typeof ActiveXObject != "undefined") {
    // On IE, XMLHttpRequest is implemented using ActiveX.
    try {
      xmlHttpRequestObj = new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e1) {
      try {
        xmlHttpRequestObj = new ActiveXObject("Microsoft.XMLHTTP");
      }
      catch (e2) {}
    }
  }
  else if (typeof XMLHttpRequest != "undefined") {
    // Browsers such as Firefox include XMLHttpRequest objects in JavaScript.
    xmlHttpRequestObj = new XMLHttpRequest();
  }
}

//----------------------------------------------------------------------------//
//
// Find a request parameter in a URL.
//
function findRequestParameter(url, parameter) {
  var substr = parameter + "=";
  var index = url.indexOf(substr);
  if (index >= 0) {
    var remainder = url.substring(index + substr.length);
    index = remainder.indexOf("&");
    if (index >= 0)
      remainder = remainder.substring(0, index);
    return remainder;
  }
  return null;
}

//----------------------------------------------------------------------------//
//
// Called when one or more items are selected from the asset picker for a
// RepositoryItemSetExpression.
//
function itemTokenAssetSelected(selected, data) {

  // Obtain the containerId, terminalId, and editorId from the string that
  // was specified as the asset picker call data.
  data = decodeURIComponent(data);
  var components = data.split("&");
  var containerId = components[0];
  var terminalId = components[1];
  var editorId = (components.length > 2 ? components[2] : null);

  // If more than one item was selected, construct a comma separated list of ids
  var value = "";
  if (typeof selected == "object" &&
      Object.prototype.toString.apply(selected) == "[object Array]") {
    for (var i = 0; i < selected.length; i++) {
      if (i > 0)
        value += ",";
      value += encodeURIComponent(selected[i].id);
    }
  }
  else {
    value = encodeURIComponent(selected.id);
  }

  // Update the expression editor.  
  atg.expreditor.performOperation(
    {
      containerId: containerId,
      editorId:    editorId,
      terminalId:  terminalId,
      operation:   "updateToken",
      value:       value
    }
  );
}

//----------------------------------------------------------------------------//
//
// Pop up the asset picker in order to specify the value of a
// RepositoryItemSetExpression.
//

var atg_AssetPickerForItemToken_pickerLauncher;

function showAssetPickerForItemToken(containerId, terminalId, editorId, repositoryPath, repositoryName, itemType, treePath, pickerURL, allowMulti, mapMode) {

  // Assemble the containerId, terminalId, and editorId into a single string to
  // be specified as the asset picker call data string.
  var callerData = containerId + "&" + terminalId;
  if (editorId)
    callerData += "&" + editorId;

  var allowableTypes = new Array(0);
  var assetType = new Object();
  assetType.typeCode       = "repository";
  assetType.displayName    = "NotUsed";
  assetType.repositoryName = repositoryName;
  assetType.createable     = "false";
  assetType.typeName       = itemType;
  assetType.componentPath  = repositoryPath;
  allowableTypes[allowableTypes.length] = assetType;

  atg_AssetPickerForItemToken_pickerLauncher = new atg.assetmanager.AssetPickerLauncher();
  atg_AssetPickerForItemToken_pickerLauncher.pickerURL               = pickerURL + "&";
  atg_AssetPickerForItemToken_pickerLauncher.assetPickerTitle        = defaultAssetPickerTitle;
  atg_AssetPickerForItemToken_pickerLauncher.allowMultiSelect        = allowMulti;
  atg_AssetPickerForItemToken_pickerLauncher.mapMode                 = mapMode;
  atg_AssetPickerForItemToken_pickerLauncher.onSelectFunction        = "itemTokenAssetSelected";
  atg_AssetPickerForItemToken_pickerLauncher.callerData              = encodeURIComponent(callerData);
  atg_AssetPickerForItemToken_pickerLauncher.assetTypes              = allowableTypes;
  atg_AssetPickerForItemToken_pickerLauncher.treeComponent           = treePath;
  atg_AssetPickerForItemToken_pickerLauncher.pickableTypesCommaList  = itemType;
  // Pop down the terminal menu if it is currently visible.
  atg.expreditor.applyPendingChanges(function() {
      atg.expreditor._requestPending = false;
      atg_AssetPickerForItemToken_pickerLauncher.invoke();
  });
}

//----------------------------------------------------------------------------//
//
// Layout the components in the asset editor (right pane).  The asset editor
// may include a header, a tab panel, a scrolling content panel, and a footer.
// To ensure that the footer appears at the bottom of the panel when it is in
// an iframe, we have to size the content panel whenever the page is loaded
// or resized.
//
function layoutAssetEditor() {

  var header       = document.getElementById("rightPaneHeader");
  var tabPanel     = document.getElementById("subNav");
  var topFade      = document.getElementById("topFade");
  var contentPanel = document.getElementById("rightPaneContent");
  var footer       = document.getElementById("rightPaneScrollFooter");
  if (contentPanel && footer) {
    var headerHeight = (header ? header.offsetHeight : 0);
    var tabPanelHeight = (tabPanel ? tabPanel.offsetHeight : 0);
    var padding = 1;
    contentPanel.style.height = getWindowHeight()
                                - footer.offsetHeight
                                - tabPanelHeight
                                - padding
                                - headerHeight + "px";
    if (topFade) {
      topFade.style.top = contentPanel.offsetTop + "px";
      topFade.style.display = "block";
    }
  }
}

//----------------------------------------------------------------------------//
//
// Convert a Java DateFormat pattern to a format that can be used by the
// dynarch.com calendar widget.
//
// The following pattern components are currently unsupported:
//   G, ww, W, F, aa, kk, KK, hh, SSS, z, Z
//
// If an unsupported component is detected, an error alert is displayed, and
// a null format is returned.
//
// NOTE: The reason AM/PM is not supported is because the calendar widget does
// not support localization of the AM/PM strings.  Therefore, only 24-hour time
// format is supported at the moment.
//
function convertToDynarchDateFormat(pattern) {

  var format = new String(pattern);

  // Find all of the components in the given pattern.
  var components = format.split(/\W+/);
  
  // Replace each component with the analogous component for the widget.
  for (i = 0; i < components.length; i++) {
    var component = components[i];
    var replacement = null;
    switch (component) {
      case "yy": // 2-digit year
        replacement = "%y";
        break;
      case "yyyy": // 4-digit year
        replacement = "%Y";
        break;
      case "MM": // month (1-12)
        replacement = "%m";
        break;
      case "MMM": // abbreviated month name
        replacement = "%b";
        break;
      case "MMMM": // full month name
        replacement = "%B";
        break;
      case "DDD": // day in year (1-366)
        replacement = "%j";
        break;
      case "dd": // day in month (1-31)
        replacement = "%d";
        break;
      case "EEE": // abbreviated weekday name
        replacement = "%a";
        break;
      case "EEEE": // full weekday name
        replacement = "%A";
        break;
      case "HH": // hour (0-23)
        replacement = "%H";
        break;
      case "mm": // minute (0-59)
        replacement = "%M";
        break;
      case "ss": // second (0-59)
        replacement = "%S";
        break;
      default:
        alert("Error: Unsupported DateFormat component: " + component);
        return null;
    }
    format = format.replace(new RegExp(component, "g"), replacement);
  }

  return format;
}

//----------------------------------------------------------------------------//
//
// Display a dijit.Menu over the target of the given event.
//
function displayMenuAtEventTarget(menuId, evt) {
  evt = evt || window.event;

  var menu = dijit.byId(menuId);
  if (menu) {
    // Pop up the menu by simulating the behavior of the menu's internal
    // _openMyself function, but using the target's origin as the location.
    dojo.stopEvent(evt);
    var target = evt.srcElement || evt.target;
    var coords = dojo.coords(target, true);
    var savedMenu = menu;
    var savedFocus = dijit.getFocus(menu);
    function closeAndRestoreFocus() {
      dijit.focus(savedFocus);
      dijit.popup.close(savedMenu);
    }
    dijit.popup.open({
      popup: menu,
      x: coords.x,
      y: coords.y,
      onExecute: closeAndRestoreFocus,
      onCancel: closeAndRestoreFocus,
      orient: menu.isLeftToRight() ? 'L' : 'R'
    });
    menu.focus();
    menu._onBlur = function() {
      dijit.popup.close(menu);
    }
  }
}

//----------------------------------------------------------------------------//

/* EVENT BASED FUNCTIONS */

function addEvent(elm, evType, fn, useCapture) {
        if (elm.addEventListener) {
                elm.addEventListener(evType, fn, useCapture);
                return true;
        } else if (elm.attachEvent) {
                var r = elm.attachEvent('on' + evType, fn);
                return r;
        } else {
        elm['on' + evType] = fn;
        }
}

function removeEvent(elm, evType, fn, useCapture){
  if (elm.removeEventListener){
      elm.removeEventListener(evType, fn, useCapture);
      return true;
  } else if (elm.detachEvent){
      var r = elm.detachEvent("on"+evType, fn);
      return r;
  } else {
      alert("Handler could not be removed");
  }
}



/* TARGETTING FUNCTIONS */

function getElementsByClassName(className, doIframes) {

  //get all the current documents elements
  var children = document.getElementsByTagName('*') || document.all;
  var elements = new Array();

  // loop through all of the found tags and see if any of them have a child with teh right class name
  for (var i = 0; i < children.length; i++) {
    var child = children[i];
    var classNames = child.className.split(' ');
    for (var j = 0; j < classNames.length; j++) {
      if (classNames[j] == className) {
        // add them to the elements array
        elements.push(child);
        break;
      }
    }
  }

  // include a quick recurse through all the iframes in the page
  if(doIframes){

    // loop through all the frames and basically do the same as above in each one.
    for(var k=0; k<window.frames.length; k++){
    //alert(window.frames.length+" many frames");
      var frameChildren = null;
      try {
        frameChildren = window.frames[k].document.getElementsByTagName('*') || document.all;
      }
      catch (e) {
        //alert("Can't get frame children");
      }
      //var elements = new Array();

      if (frameChildren != null) {
        for (var l = 0; l < frameChildren.length; l++) {
          var frameChild = frameChildren[l];
          var framClassNames = frameChild.className.split(' ');
          for (var m = 0; m < framClassNames.length; m++) {
            if (framClassNames[m] == className) {
              elements.push(frameChild);
              break;
            }
          }
        }
      }
    }
  }
// return the array of all elements that have that class in there class name attribute
return elements;
}




/* STYLE, POSITION BASED FUNCTIONS */


// find the Y position of an element in relation to the whole browser window
// bascially loops back from the current element until it reaches the main window
// adds all those y positions together to get the total Y value
// ...or if the browser will do it it just uses obj.y (IE doesn't support this)
function findPosY(obj){

// error hiding
try{

        var curtop = 0;
        if (obj.offsetParent){
                while (obj.offsetParent){
                        curtop += obj.offsetTop;
                        obj = obj.offsetParent;
                }
        }
        else if (obj.y) {
    curtop += obj.y;
  }
        return curtop;

}catch(e){
  return false;
}
}

function findPosX(obj) {

// error hiding
try{

        var curtop = 0;
        if (obj.offsetParent){
                while (obj.offsetParent){
                        curtop += obj.offsetLeft;
                        obj = obj.offsetParent;
                }
        }
        else if (obj.x) {
    curtop += obj.x;
  }
        return curtop;

}catch(e){
  return false;
}
}


// Cascaded Style, finds style attribute values using JS that
// have not been set either inline or with JS.
// i.e. it will find styles from the css, using JS.

function cascadedstyle(el, cssproperty){

  // current style is an IE only function
  // Represents the cascaded format and style of the object as specified
  // by global style sheets, inline styles, and HTML attributes.
  if (el.currentStyle)
  {
    return el.currentStyle[cssproperty];

  //
  } else if (window.getComputedStyle)
  {

  // Gecko borwsers require the css property to be split with a '-' so
  // backgroundColor becomes background-color so we use a quick regex to achive this.
    var property = cssproperty;
    var replacement = '$1-$2';
    var regex = '([^/s$])([A-Z])';
    var re = new RegExp(regex, "g");
    var csspropertyNS = property.replace(re, replacement);

    // Make it all lowercase - I don't think this matters but it's 'supposed' to be this way
    csspropertyNS = csspropertyNS.toLowerCase();

    var elstyle=window.getComputedStyle(el, "");
    return elstyle.getPropertyValue(csspropertyNS);
  }
}


//function displayToggle(boxid, onclass, offclass){
//
//	if(document.getElementById){
//		document.getElementById(boxid).className =(document.getElementById(boxid).className == offclass)? onclass : offclass;
//
//	}
//}


// Truncate Text

function truncateSearchResults(){
        if(document.getElementById('limitText')){
                var truncateCap = 25;
                var searchContainer = document.getElementById('limitText');
                var searchResult = searchContainer.firstChild;
                if(searchResult.nodeValue.length>=truncateCap){
                        searchResult.nodeValue = searchResult.nodeValue.substring(0,truncateCap)+'...';
                        searchContainer.style.display = "inline"
                }
        }
}




/* TIME BASED FUNCTIONS */

//########################################################//
// JS has issue with settimeout and setinterval, the skinny
// is that they loose scope if used in a class or object
// all variables become global - which is not good for OO work
// We use the following group of classes, functions and prototypes
// to basically "rewrite" how JS handles these so they keep scope.
// The constructor should be called with
// the parent object (optional, defaults to window).

function Timer(){
    this.obj = (arguments.length)?arguments[0]:window;
    return this;
}

// The set functions should be called with:
// - The name of the object method (as a string) (required)
// - The millisecond delay (required)
// - Any number of extra arguments, which will all be
//   passed to the method when it is evaluated.

Timer.prototype.setInterval = function(func, msec){
    var i = Timer.getNew();
    var t = Timer.buildCall(this.obj, i, arguments);
    Timer.set[i].timer = window.setInterval(t,msec);
    return i;
}

Timer.prototype.setTimeout = function(func, msec){
    var i = Timer.getNew();
    Timer.buildCall(this.obj, i, arguments);
    Timer.set[i].timer = window.setTimeout("Timer.callOnce("+i+");",msec);
    return i;
}

// The clear functions should be called with
// the return value from the equivalent set function.

Timer.prototype.clearInterval = function(i){
    if(!Timer.set[i]) return;
    window.clearInterval(Timer.set[i].timer);
    Timer.set[i] = null;
}
Timer.prototype.clearTimeout = function(i){
    if(!Timer.set[i]) return;
    window.clearTimeout(Timer.set[i].timer);
    Timer.set[i] = null;
}

// Private data

Timer.set = new Array();
Timer.buildCall = function(obj, i, args){
    var t = "";
    Timer.set[i] = new Array();
    if(obj != window){
        Timer.set[i].obj = obj;
        t = "Timer.set["+i+"].obj.";
    }
    t += args[0]+"(";
    if(args.length > 2){
        Timer.set[i][0] = args[2];
        t += "Timer.set["+i+"][0]";
        for(var j=1; (j+2)<args.length; j++){
            Timer.set[i][j] = args[j+2];
            t += ", Timer.set["+i+"]["+j+"]";
    }}
    t += ");";
    Timer.set[i].call = t;
    return t;
}
Timer.callOnce = function(i){
    if(!Timer.set[i]) return;

    eval(Timer.set[i].call);

    Timer.set[i] = null;
}
Timer.getNew = function(){
    var i = 0;
    while(Timer.set[i]) i++;
    return i;
}

//##########################################################//


/* MISC. FUNCTIONS */

function addCommas (pNumber) {

  var retNumber = pNumber;
  var index = retNumber.indexOf(",");
  if (index > 0) return retNumber;

  var retNumber = pNumber;
  var rgx = /(\d+)(\d{3})/;
  while (rgx.test(retNumber)) {
    retNumber = retNumber.replace(rgx, '$1' + ',' + '$2');
  }
  return retNumber;
}

// bascic percentage with rounding function (used in the positioning scripts)
function percent(a, b) {
  c = a/100;
  d = Math.round(c*b);
  return d-1;
}

// returns a color which is between orig color and final color or white
function getGradientColor(red1, green1, blue1, fract, red2, green2, blue2) {

 var r1=red1;
 var g1=green1;
 var b1=blue1;
 var r2 = 255;
 var g2 = 255;
 var b2 = 255;

 if (red2) {
  r2=red2;
  g2=green2;
  b2=blue2;
 }
  
 var r= Math.floor(r1+(fract*(r2-r1)));
 var g= Math.floor(g1+(fract*(g2-g1)));
 var b= Math.floor(b1+(fract*(b2-b1)));
    
 return("#" + r.toString(16) + g.toString(16) + b.toString(16));
}

// show the iframe identified by elementID.
// if that iframe is already showing, close it unless 
// leaveExsitingIframe is set, in which case we do nothing.  
function showIframe(elementID,leaveExistingIframe) {

      if(elementID == 0){
          alert("You must choose an action from the menu");
          return;
       }

        if(parent.document.iframeOpen){
                if(parent.document.currentID != elementID || leaveExistingIframe){
                        if (usesApplets) {
                          addAppletKillerToDocument(parent.document, "appletKiller");
                        }
                        if (parent.document.getElementById("curtain")){
                          var curtain = parent.document.getElementById("curtain");
                          curtain.style.display = "block";
                          parent.dijit.BackgroundIframe(curtain);
                          }
                        parent.document.getElementById(parent.document.currentID).style.display = "none";
                        parent.document.getElementById(elementID).style.display = "block";
                        parent.document.iframeOpen = true;
                }else{
                        parent.document.getElementById(elementID).style.display = "none";
                        if (parent.document.getElementById("curtain")){
                          var curtain = parent.document.getElementById("curtain");
                          curtain.style.display = "none";
                          }
                        if (usesApplets) {
                          removeAppletKillerFromDocument(parent.document, "appletKiller");
                        }
                        parent.document.iframeOpen = false;
                }
        }else{
                if (usesApplets) {
                  addAppletKillerToDocument(parent.document, "appletKiller");
                }
                if (parent.document.getElementById("curtain")){
                  var curtain = parent.document.getElementById("curtain");
                  curtain.style.display = "block";
                  parent.dijit.BackgroundIframe(curtain);
                  }
                parent.document.getElementById(elementID).style.display = "block";
                parent.document.iframeOpen = true;

        }

        parent.document.currentID = elementID;

}

function hideIframe(elementID) {
  var element = parent.document.getElementById(elementID);
  if (element) {
    element.style.display = "none";
    var curtain = parent.document.getElementById("curtain");
    if (curtain)
      curtain.style.display = "none";
    if (usesApplets)
      removeAppletKillerFromDocument(parent.document, "appletKiller");
    parent.document.iframeOpen = false;
  }
}

// Return the height of the current window.
function getWindowHeight() {

  if (self.innerHeight)
    return self.innerHeight;
  if (document.documentElement && document.documentElement.clientHeight)
    return document.documentElement.clientHeight;
  if (document.body)
    return document.body.clientHeight;

  alert("Can't determine window height");
}

    
function setTabChange() {
  tabChange = true;
}

function handlePreLoad() {
  listenModified = false;
}

function handlePostLoad() {
  listenModified = true;
}

function setModifiedFlag(setval) {      
  var modCB = document.getElementById("assetModifiedCB");
  if (setval && assetModified != setval)
    fireOnModified();
  assetModified = setval;       
  if (modCB) modCB.checked = setval;
}

// Called from property editors to mark asset modified
function formFieldModified() {
  // In IE only, event is defined.  We only want to respond to changed values.  
  if (typeof event != "undefined") {
    //alert("event.propertyName= " + event.propertyName);
    if (event.propertyName != "value" && event.propertyName != "checked")
      return;
  }
  markAssetModified();
}

//  mark asset modified
function markAssetModified() {
  if (listenModified) {
    setModifiedFlag(true);
  } 
}

function isAssetModified() {
  return assetModified;
}

function openWindow(theurl) {
        window.open(theurl);
}

//----------------------------------------------------------------------------//
//  Basic validation functions
//

var validationMessages = new Array;

function createValidationMessage(key, msg) {
  var validationMessage = new Object();
  validationMessage.key = key;
  validationMessage.message = msg;

  return validationMessage;
}

function addValidationMessage(key, msg) {
  validationMessages[validationMessages.length] = createValidationMessage(key, msg);
}

function removeValidationMessage(key) {
  var newMessages = new Array();

  for(var i = 0; i < validationMessages.length; i++) {
    var m = validationMessages[i];

    if(m.key != key) {
      newMessages[newMessages.length] = createValidationMessage(m.key, m.message);
    }
  }

  validationMessages = newMessages;
}

function getValidationMessage() {
  if(validationMessages.length > 0) {
    return validationMessages[0].message;
  }

  return null;
}


//----------------------------------------------------------------------------//
//  Loading icon functions
//

var checkTimeout = null;
var actionsLocked = false;

// Call this when a possibly long action begins. 
// It will perform some 'loading icon' features 
// immediately and call others after a short timeout.
//
function startWait(pWaitMessage, pLeftLoadingIcon, pRightLoadingIcon) {

 // immediate loading features
 actionsLocked = true;

 // trigger delayed loading features
 if (checkTimeout) clearTimeout(checkTimeout);
 checkTimeout = setTimeout("doWait("+pWaitMessage + "," + pLeftLoadingIcon + "," + pRightLoadingIcon + ")",800);
}

// If the action takes some time, these loading icon features are done.
//
function doWait(pWaitMessage, pLeftLoadingIcon, pRightLoadingIcon) {
  if (checkTimeout == null) return;
  // display loading icon 
  if (pLeftLoadingIcon)displayLeftLoadingIcon();
  if (pRightLoadingIcon)displayRightLoadingIcon();
  // show 'Please wait...' message
  if (pWaitMessage)top.messages.wait();
}

// Call this when the action that called startWait, finishes
//
function stopWait() {  
  //cancel the delayed features if they haven't run yet.
  if (checkTimeout) {
    clearTimeout(checkTimeout);
    checkTimeout = null;
  }

  if (top.messages ) 
    top.messages.endWait();
  actionsLocked = false;
}


// Show the loading icon and hide the left pane
//
function displayLeftLoadingIcon() {
  if (top.window.frames['scrollContainer']) {
     var screenDiv = top.window.frames['scrollContainer'].document.getElementById("screen");
     if (screenDiv) {
       //screenDiv.style.backgroundImage = "url('/AssetManager/images/loading.gif')";
       screenDiv.style.display = "block";
     }
  }
}


// Hide the loading icon and show the left pane again.
//
function hideLeftLoadingIcon() {
  if (top.window.frames['scrollContainer']) {
     var screenDiv = top.window.frames['scrollContainer'].document.getElementById("screen");
     if (screenDiv) {
       //screenDiv.style.backgroundImage = "none";
       screenDiv.style.display = "none";
       top.window.frames['scrollContainer'].frameElement.scrolling = "auto";
     }
  }
}


// Show the loading icon and hide the right pane
//
function displayRightLoadingIcon() {
   if (top.window.frames['rightPaneIframe']) {
     var screenDiv = top.window.frames['rightPaneIframe'].document.getElementById("screen");
     if (screenDiv) {
       //screenDiv.style.backgroundImage = "url('/AssetManager/images/loading.gif')";
       screenDiv.style.display = "block";
     }
  }
}

// Hide the loading icon and show the right pane again.
//
function hideRightLoadingIcon() {
  if (top.window.frames['rightPaneIframe']) {
     var screenDiv = top.window.frames['rightPaneIframe'].document.getElementById("screen");
     if (screenDiv) {
       //screenDiv.style.backgroundImage = "none";
       screenDiv.style.display = "none";
     }
  }
}


function actionsAreLocked() {
  return actionsLocked;
}

// Functions to add a class to the document and all the iframe content documents in there.
function addClassToElement(pElement, pClassStr) {
  if (!pElement || !pClassStr) {
    return;
  }
  var cls = pElement.className;
  if((" "+cls+" ").indexOf(" "+pClassStr+" ") < 0){
    pElement.className = cls + (cls ? ' ' : '') + pClassStr;
  }
}
function removeClassFromElement(pElement, pClassStr) {
  if (!pElement || !pClassStr) {
    return;
  }
  var t = ((" " + pElement.className + " ").replace(" " + pClassStr + " ", " "));
  if (pElement.className != t) {
    pElement.className = t;
  }
}

function addAppletKillerToDocument(pDocument, pClassStr) {
  if (!pClassStr || !pDocument) {
    return;
  }
  // alert("addAppletKillerToDocument called for document:" + pDocument.URL);
  var b = pDocument.body;
  addClassToElement(b, pClassStr);
  var iframes = b.getElementsByTagName("iframe");
  for (var i = 0; i < iframes.length; i++) {
    var iframeElem = iframes[i];
    if(iframeElem.contentDocument){
      // Firefox, Opera
      iframeDoc = iframeElem.contentDocument;
    }else if(iframeElem.contentWindow){
      // Internet Explorer
      iframeDoc = iframeElem.contentWindow.document;
    }else if(iframeElem.document){
      // Others?
      iframeDoc = iframeElem.document;
    }
    addClassToElement(iframeDoc.body, pClassStr);
  }
}

function removeAppletKillerFromDocument(pDocument, pClassStr) {
  if (!pClassStr || !pDocument) {
    return;
  }
  // alert("removeAppletKillerFromDocument called for document:" + pDocument.URL);
  var b = pDocument.body;
  removeClassFromElement(b, pClassStr);
  var iframes = b.getElementsByTagName("iframe");
  for (var i = 0; i < iframes.length; i++) {
    var iframeElem = iframes[i];
    if(iframeElem.contentDocument){
      // Firefox, Opera
      iframeDoc = iframeElem.contentDocument;
    }else if(iframeElem.contentWindow){
      // Internet Explorer
      iframeDoc = iframeElem.contentWindow.document;
    }else if(iframeElem.document){
      // Others?
      iframeDoc = iframeElem.document;
    }
    removeClassFromElement(iframeDoc.body, pClassStr);
  }
}
