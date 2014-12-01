<!--
parent.document.iframeOpen = false;
var iewin=(document.all && navigator.appVersion.indexOf("Mac") == -1);
var nn=(navigator.appName.indexOf("Netscape")!=-1);

var     currentPicker = null;

//
// List of methods to call for onload
// notification
//
var onLoadMethods = new Array();
var onResizeMethods = new Array();
var onSubmitMethods = new Array();

//
// Register a function to be called when the document
// is loaded
//
function registerOnLoad(fnct){
  onLoadMethods[ onLoadMethods.length ]=fnct;
}

function registerOnResize(fnct){
  onResizeMethods[ onResizeMethods.length ]=fnct;
}

function registerOnSubmit(fnct)
{
  onSubmitMethods[ onSubmitMethods.length ]=fnct;
}

//
// Called by BizUIPageTemplate, or other
// top-level pages's body or frame tags
// to provide notification of this even
// to page fragments that don't have access
// to the tag.
//
function fireOnLoad()
{
  for( var ii=0; ii < onLoadMethods.length; ++ii )
    onLoadMethods[ii]();
}

function fireOnResize()
{
  for( var ii=0; ii < onResizeMethods.length; ++ii )
    onResizeMethods[ii]();
}

function fireOnSubmit()
{
  for( var ii=0; ii < onSubmitMethods.length; ++ii )
    onSubmitMethods[ii]();
}

//
// Set focus to document element identified
// by ID
//

function focusTo( inputItem )
{
  var item = document.getElementById( inputItem );
  if ( item != undefined )
    item.focus();
  else
    alert( "focusTo( " + inputItem + " ): Element undefined" );
}

function init() {
  var numArgs = init.arguments.length;

  for(i=1; i<=numArgs; i++){
    if(init.arguments[i-1] == "assetBrowser" && document.all){
    }
    if (iewin) {
      document.all[init.arguments[i-1]].style.position = "absolute";
    }
  }
}

function showProcessOptions(descList, elem) {
  var processOpt = document.getElementById(descList).getElementsByTagName('table');
  var howMany = processOpt.length;

  //alert(test);

  for (i=0; i<howMany; i++) {
    if (elem == i) {
      with (processOpt[i].style) {
                          display = "block";
                          backgroundColor = '#F9F9F9';
                          borderTop = '1px solid #FFF';
                          borderBottom = '1px solid #999';
                          borderRight = '1px solid #999';
      }
                        with(document.getElementById('processCat' + i).style) {
        backgroundColor = '#F9F9F9';
        borderTop = '1px solid #FFF';
        borderBottom = '1px solid #999';
        borderLeft = '1px solid #E5E5E5';
        color = '#666';
                        }
    } else {
      with (processOpt[i].style) {
                          display = "none";
                          backgroundColor = '#EEF2F4';
                          borderTop = 'none';
                          borderBottom = 'none';
      }
                        if (document.getElementById('processCat' + i)) {
                          with (document.getElementById('processCat' + i).style) {
                            backgroundColor = '#EEF2F4';
                            backgroundImage = 'none';
                            borderTop = '1px solid #EEF2F4';
                            borderBottom = '1px solid #EEF2F4';
                            borderLeft = '1px solid #EEF2F4';
                            color = '#999';
                          }
                        }
    }
  }
}


function switchDesc2(descList, elem) {
  var processDesc = document.getElementById(descList).getElementsByTagName('p');
  var howMany = processDesc.length;
  for (i=0; i<howMany; i++) {
    if (elem == i) {
      processDesc[i].style.display = "block";
      document.getElementById('processType' + i).style.backgroundColor = '#fff';
      document.getElementById('processType' + i).style.border = '1px solid #E5E5E5';
      document.getElementById('processType' + i).style.color = '#0066CC';
      document.getElementById('processType' + i).style.cursor = 'pointer'
    } else {
      processDesc[i].style.display = "none";
      document.getElementById('processType' + i).style.backgroundColor = 'F9F9F9';
      document.getElementById('processType' + i).style.color = '#666';
      document.getElementById('processType' + i).style.border = '1px solid #F9F9F9';
    }
  }
}

function switchDesc3(descList, elem) {
  var processDesc = document.getElementById(descList).getElementsByTagName('p');
  var howMany = processDesc.length;
  processDesc[elem].style.display = "none";
  document.getElementById('processType' + elem).style.backgroundColor = '#F9F9F9';
  document.getElementById('processType' + elem).style.border = '1px solid #F9F9F9';
  document.getElementById('processType' + elem).style.color = '#666';
}

function switchDesc(elem, descList) {
var processDesc = document.getElementById(descList).getElementsByTagName('p');
var selectSize = document.getElementById(elem).options.length;
var currentSelected = document.getElementById(elem).options.selectedIndex;

  for (i=0; i<selectSize; i++) {
    if (currentSelected == i) {
      processDesc[i].style.display = "block";
    } else {
      processDesc[i].style.display = "none";
    }
  }
}

function showIframe(elementID,onSelect) {
var myHeight = getInnerHeight();
var scrOfY = getScrollingOffset();

  if(elementID == 0){
    alert("You must choose an action from the menu");
    return;
  }

  if(parent.document.iframeOpen){

    if(parent.document.currentID != elementID){
      parent.document.getElementById(parent.document.currentID).style.display = "none";
      parent.document.getElementById(elementID).style.display = "block";
      parent.document.iframeOpen = true;
    }else{
      parent.document.getElementById(elementID).style.display = "none";
      parent.document.iframeOpen = false;
    }
  }else{
    parent.document.getElementById(elementID).style.display = "block";
    parent.document.iframeOpen = true;

  }

  parent.document.currentID = elementID;
        parent.document.onSelect = onSelect;
}

function resizeAssetBrowser() {
  totalWidth = document.all?document.body.offsetWidth:window.innerWidth;
  reference = parent.document.getElementById("assetBrowser");
  if ( reference ) {
    temp = (totalWidth-700)/2;
    if (temp<0) {temp = 0;}
    u = String(temp) + "px";
    reference.style.left = u;
  }
}

function hideIframe(elementID) {
  var element = parent.document.getElementById( elementID );
  if ( element ) {
    element.style.display = "none";
    parent.document.iframeOpen = false;
  }
}

function refreshParentWindow()
{
  // calling reload will cause the form data to be posted back to the server which causes
  // a warning in Firefox 1.5 and later to popup. Reloading by setting the location back
  // to itself avoids this problem and reloads the page without posting the data back to the server.
  window.parent.location=window.parent.location;
}


/*
 * In some cases we can't simply replace innerHTML 
 *   document.getElementById(id).innerHTML = tempHTML;
 * due to the following IE bug
 *   http://support.microsoft.com/default.aspx/kb/927917
 * instead we need to update a copy of the node and replace the original with the updated copy.
 */
function replaceInnerHtml( domElement, strHtml )
{
  var parentNode = domElement.parentNode;
  
  if ( parentNode != null)
  {
    var clonedElement = domElement.cloneNode(true);
    
    clonedElement.innerHTML = strHtml;
    parentNode.replaceChild(clonedElement, domElement);
    return clonedElement;
  }
  else
  {
    domElement.innerHTML = strHtml;
    return domElement;
  }
}


/*
 * In some cases we can't simply append to innerHTML 
 *   document.getElementById(id).innerHTML += tempHTML;
 * due to the following IE bug
 *   http://support.microsoft.com/default.aspx/kb/927917
 * instead we need to update a copy of the node and replace the original with the updated copy.
 */
function appendInnerHtml( domElement, strHtml )
{
  var parentNode = domElement.parentNode;
  
  if ( parentNode != null)
  {
    var clonedElement = domElement.cloneNode(true);
    
    clonedElement.innerHTML += strHtml;
    parentNode.replaceChild(clonedElement, domElement);
    return clonedElement;
  }
  else
  {
    domElement.innerHTML += strHtml;
    return domElement;
  }
}


function showAssetBrowser( selectMode, createMode, closeAction, notUsed,
                           targetassetInfoPath )
{
  var url = "/PubPortlets/html/ProjectsPortlet/assetBrowser.jsp?";

  url += "browserMode=project";

  url += "&selectMode=" +
   ((typeof(selectMode)=="undefined") ? "multi" : selectMode);

  url += "&createMode=" +
   ((typeof(createMode)=="undefined") ? "none" : createMode);

  url += "&closeAction=" +
   ((typeof(closeAction)=="undefined") ? "refresh" : closeAction);

  if (assetInfoPath != null) {
    url += "&assetInfo=" + assetInfoPath;
  }

  document.getElementById( 'assetBrowserIframe' ).src = url;
  showIframe( 'assetBrowser' );
}

function showFileAssetPicker( onSelect, itemType, displayName,
  repositoryName, selectMode, createMode, closeAction, assetInfoPath)
{
  var url = "/PubPortlets/html/ProjectsPortlet/assetBrowser.jsp?";

  url += "browserMode=pick";
  url += "&onSelect=" + onSelect;
  url += "&itemType=" + encodeURIComponent( itemType );
  url += "&itemTypeDisplayName=" + encodeURIComponent( displayName );
  url += "&itemTypeRepositoryName=" + encodeURIComponent( repositoryName );
  url += "&createTypeCode=file";

  if (assetInfoPath != null) {
    url += "&assetInfo=" + assetInfoPath;
  }

  //if ( typeof(propertyName) != "undefined" )
  //  url += "&propertyName=" + propertyName;

  url += "&selectMode=" +
   ((typeof(selectMode)=="undefined") ? "single" : selectMode);

  if ( typeof(createMode) != "undefined" )
    url += "&createMode=" + createMode;
  else
    url += "&createMode=none";

  url += "&closeAction=" +
   ((typeof(closeAction)=="undefined") ? "hide" : closeAction);

  url += "&typeCode=file";

  document.getElementById( 'assetBrowserIframe' ).src = url;
  showIframe( 'assetBrowser', onSelect );
}

function showAssetPicker( onSelect, 
  encodedItemType,
  itemDisplayName,
  repositoryDisplayName,
  typeCode,
  displayNameProperty,
  isItemCreatable,
  browserMode,
  isAllowMultiSelect,
  createMode,
  closeAction,
  defaultView,
  assetPickerTitle,
  assetInfoPath) {
  
  var decodedItemType = encodedItemType.split(":");

  var assetType = new Object();
    assetType.typeCode            = typeCode;
    assetType.displayName         = itemDisplayName;
    assetType.displayNameProperty = displayNameProperty;
    assetType.typeName            = decodedItemType[1];
    assetType.componentPath       = decodedItemType[0];
    assetType.repositoryName      = repositoryDisplayName;
    assetType.createable           = isItemCreatable;
  
  var allowableTypes = new Array(0);
  allowableTypes[ allowableTypes.length ] = assetType;
  
  var picker = new AssetBrowser( assetInfoPath );
    picker.assetPickerTitle   = assetPickerTitle;
    picker.browserMode        = browserMode;
    picker.isAllowMultiSelect = isAllowMultiSelect;
    picker.createMode         = createMode;
    picker.onSelect           = onSelect;
    picker.closeAction        = closeAction;
    picker.defaultView        = defaultView;
    picker.assetTypes         = allowableTypes;
    picker.invoke();
}

function showAssetPickerX( onSelect, itemType, displayName,
  repositoryName, selectMode, createMode, closeAction, assetInfoPath)
{
  var url = "/PubPortlets/html/ProjectsPortlet/assetBrowser.jsp?";

  url += "browserMode=pick";
  url += "&onSelect=" + onSelect;
  url += "&itemType=" + encodeURIComponent( itemType );
  url += "&itemTypeDisplayName=" + encodeURIComponent( displayName );
  url += "&itemTypeRepositoryName=" + encodeURIComponent( repositoryName );

  if (assetInfoPath != null) {
    url += "&assetInfo=" + assetInfoPath;
  }

  //if ( typeof(propertyName) != "undefined" )
  //  url += "&propertyName=" + propertyName;

  url += "&selectMode=" +
   ((typeof(selectMode)=="undefined") ? "single" : selectMode);

  url += "&createMode=" +
   ((typeof(createMode)=="undefined") ? "none" : createMode);

  url += "&createTypeCode=repository";

  url += "&closeAction=" +
   ((typeof(closeAction)=="undefined") ? "hide" : closeAction);

  url += "&typeCode=repository";

  document.getElementById( 'assetBrowserIframe' ).src = url;
  showIframe( 'assetBrowser', onSelect );
}

//
// AssetBrowser constructor
//
function AssetBrowser( assetInfoPath )
{
  this.assetTypes; // array of assetType objects. Required

  // Parameters to be encoded in the URL
  this.browserMode        = "project";
  this.assetInfoPath      = assetInfoPath;
  this.isAllowMultiSelect = "true";
  this.createMode         = "none";
  this.closeAction        = "refresh";
  this.clearAttributes    = "false";
  this.mapMode            = "pick";
  this.assetPickerTitle;
  this.dialogType;
  this.callerData;
  this.onHideData;

  // Default display elements
  this.assetPickerParentWindow = window;
  this.assetPickerFrameElement = document.getElementById('iFrame');
  this.assetPickerDivId        = "browser";

  // Optional callback methods: used in property editors
  this.onSelect;
  this.onHide;

  // Optional formhandler: used in projectAssets.jsp
  this.formHandler;
  this.formHandlerAction;

  // Optional view configuration
  this.defaultView;
  this.viewConfiguration;
}

//
// AssetBrowser class data
//
AssetBrowser.prototype.pickerURL = "/AssetUI/assetPicker/assetPicker.jsp?apView=1&";

//
// AssetBrowser encodeURLOptions
//

AssetBrowser.prototype.encodeURLOptions = function()
{
  var url = this.pickerURL;

  url += "browserMode=" + this.browserMode;
  if ( this['assetInfoPath'] != undefined )
    url += "&assetInfo=" + this.assetInfoPath;

  url += "&isAllowMultiSelect=" + this.isAllowMultiSelect;
  url += "&createMode=" + this.createMode;
  url += "&closeAction=" + this.closeAction;
  url += "&clearAttributes=" + this.clearAttributes;
  url += "&mapMode=" + this.mapMode;

  if ( this['assetPickerTitle'] != undefined )
    url += "&assetPickerTitle=" + this.assetPickerTitle;
  if ( this['onSelect'] != undefined )
    url += "&onSelect=" + this.onSelect;
  if ( this['onHide'] != undefined )
    url += "&onHide=" + this.onHide;
  if ( this['dialogType'] != undefined )
    url += "&dialogType=" + this.dialogType;
  if ( this['formHandler'] != undefined )
    url += "&formHandler=" + this.formHandler;
  if ( this['formHandlerAction'] != undefined )
    url += "&formHandlerAction=" + this.formHandlerAction;
  if ( this['defaultView'] != undefined )
    url += "&defaultView=" + this.defaultView;
  if ( this['callerData'] != undefined )
    url += "&callerData=" + encodeURIComponent( this.callerData );
  if ( this['onHideData'] != undefined )
    url += "&onHideData=" + encodeURIComponent( this.onHideData );

  return url;
}

//
// AssetBrowser invoke method
//
AssetBrowser.prototype.invoke = function()
{
  this.assetPickerParentWindow.parent.currentPicker = this;

  this.encodeURLOptions();

  this.assetPickerFrameElement.src =
    this.encodeURLOptions();
    
  showIframe( this.assetPickerDivId, this.onSelect );
}

function moveAssetBrowser () {
  opened = !opened;
  if(!opened){
    document.getElementById("assetBrowser").className = "assetBrowserClosed";
    document.getElementById("assetBrowserButton").className = "closed";
  }else{
    document.getElementById("assetBrowser").className = "assetBrowserOpen";
    document.getElementById("assetBrowserButton").style.backgroundImage = "url(/images/browserButton_bgOn.gif)";
    CurrentPopup = "assetBrowser";
  }
}

function doRedirect( url ) {
  document.location.href = url;
}

/* ACW:
   This function was modified from the default. I changed the hierarchyTypes to "file".
*/
function processHierarchyPane(id) {
  if ( id == null || id.selectedIndex < 0 )
    return;
  ref = "hierarchyPane"
  ref2 = "hierarchyPaneLabel"
  ref3 = "inFolderLabel"
  hierarchyTypes = new Array("file");
  var listArray = id.options[id.selectedIndex].value.split("|");
  chosen = listArray[1];
  isMatch = false;
  for (var i in hierarchyTypes) {
    if (chosen == hierarchyTypes[i]) {
      isMatch = true;
    }
  }

  if (!isMatch && document.getElementById('inFolderLabel')) {
    document.getElementById(ref).className = "hierarchyPaneClosed";
    document.getElementById(ref2).className = "hierarchyPaneClosed";
    document.getElementById(ref3).className = "formLabelHidden formLabelRequiredHidden";
  } else if (document.getElementById('inFolderLabel')) {
    document.getElementById(ref).className = "hierarchyPaneOpen";
    document.getElementById(ref2).className = "hierarchyPaneOpen";
    document.getElementById(ref3).className = "formLabel formLabelRequired";
  } else if (!isMatch && !document.getElementById('inFolderLabel')) {
    document.getElementById(ref).className = "hierarchyPaneClosed";
  } else {
    document.getElementById(ref).className = "hierarchyPaneOpen";
    }
}

function processRepositoryHierarchyPane(id) {
 var repositoryType = id.options[id.selectedIndex].label;
 // THESE ARE THE ASSET TYPES THAT WILL BE POPULATED BASED ON THE 3 ARBITRARY REPOSITORY TYPES
if (repositoryType == "Repository 1") {
  assetTypes = new Array("Profile Group","Content Group","Scenario","Slot");
 } else if (repositoryType == "Repository 2") {
   assetTypes = new Array("Scenario","Slot");
 } else if (repositoryType == "Repository 3") {
   assetTypes = new Array("Content Group","Profile Group");
 }
 var fullCode = "<select class=\"formElement\" id=\"assetTypePulldown\" onchange=\"processHierarchyPane(this);\">";
 for (x = 0; x < assetTypes.length;x++) {
   fullCode += "<option label=\""+assetTypes[x]+"\" value=\"0\">"+assetTypes[x]+"</option>";
 }

 fullCode += "</select>";
 replaceInnerHtml(document.getElementById("repositoryPulldownCode"), fullCode);
 processHierarchyPane(document.getElementById('assetTypePulldown'));
}

function closeProcess (elementID){
  document.getElementById(elementID).displayElement = !document.getElementById(elementID).displayElement;

  if(!document.getElementById(elementID).displayElement){
    document.getElementById(elementID).style.display = "none";
    document.getElementById(elementID + "_container").className = "containerIconClosed colapsablePanelTitle";
  }else{
    document.getElementById(elementID).style.display = "block";
    document.getElementById(elementID + "_container").className = "containerIconOpen colapsablePanelTitle";
  }
}

function closeAdminDetails (elementID){
  document.getElementById(elementID).displayElement = !document.getElementById(elementID).displayElement;


  if(!document.getElementById(elementID).displayElement){
    document.getElementById(elementID).style.display = "none";
    document.getElementById(elementID + "_container").className = "containerIconClosed adminDetails";
  }else{
    document.getElementById(elementID).style.display = "block";
    document.getElementById(elementID + "_container").className = "containerIconOpen adminDetails";
  }
}

function closeProcessHierarchy (elementID, level){
  document.getElementById(elementID).displayElement = !document.getElementById(elementID).displayElement;

  if(!document.getElementById(elementID).displayElement){
    document.getElementById(elementID).style.display = "none";
    document.getElementById(elementID + "_container").className = "containerIconClosed" + level;
  }else{
    document.getElementById(elementID).style.display = "block";
    document.getElementById(elementID + "_container").className = "containerIconOpen" + level;
  }
}

var CurrentPopup;
function scrollCurrentPopup () {
  element = document.getElementById(CurrentPopup);
  element.style.top = getScrollingOffset ();
  return;
}

function getInnerHeight () {
  var x,y;
  if (self.innerHeight) // all except Explorer
  {
    y = self.innerHeight;
  }
  else if (document.documentElement && document.documentElement.clientHeight)
      // Explorer 6 Strict Mode
  {
    y = document.documentElement.clientHeight;
  }
  else if (document.body) // other Explorers
  {
    y = document.body.clientHeight;
  }
  return y;
}

function getScrollingOffset () {
  var x,y;
  if (self.pageYOffset) // all except Explorer
  {
    y = self.pageYOffset;
  }
  else if (document.documentElement && document.documentElement.scrollTop)
      // Explorer 6 Strict
  {
    y = document.documentElement.scrollTop;
  }
  else if (document.body) // all other Explorers
  {
    y = document.body.scrollTop;
  }
    return y;
}

function showActions(elementID) {
  var myHeight = getInnerHeight();
  var scrOfY = getScrollingOffset();

  parent.document.getElementById(elementID).opened = !parent.document.getElementById(elementID).opened;

  if(!parent.document.getElementById(elementID).opened){
    parent.document.getElementById(elementID).style.display = "none";
  }else{
    parent.document.getElementById(elementID).style.display = "block";
    parent.document.getElementById(elementID).style.marginTop = scrOfY + myHeight/2-150 + "px";
  }
}

function changePage(URL) {
  location.href = URL;
}

function changePages (hierarchyURL, listURL) {
  parent.hierarchy.location = hierarchyURL;
  parent.folderList.location = listURL;
}

function changePagesAssetBrowser (URL1, URL2) {
  parent.results.location = URL1;
  parent.select.location = URL2;
}

function changePagesAssetBrowser2 (URL1, URL2) {
  parent.results.location = URL1;
  parent.display.location = URL2;
}

function changePagesAssetBrowser3 (URL1, URL2, URL3) {
  parent.results.location = URL1;
  parent.select.location = URL2;
  parent.display.location = URL3;
}


function insertIframeHolder(elementID) {
  var bodyTag = document.body;
  var divTag = document.getElementById(elementID);
  var divParent = divTag.parentNode;
  divParent.removeChild(divTag);
  bodyTag.appendChild(divTag);
}


/**
* Updates a named input in the form with the given name in the current
* document to the new value specified.
**/
function setFormElement(formName,inputName,value) {
  var form = document.getElementById(formName);
  if(form == null) return false;
  var input = form.elements[inputName];
  if(input == null) return false;
//  alert('in setFormElement(' + formName + ', ' + inputName + ', ' + value + ')');
  input.value=value;
  return true;
}

/**
* Updates a form action with the given value.
**/
function setFormAction(formName, formAction) {
  var form = document.getElementById(formName);
  if(form == null) return false;
//  alert('in setFormAction(' + formName + ', ' + formAction + ')');
  form.action = formAction;
  return true;
}

/**
* Submits a form with the given name in the current document
**/
function submitForm(formName) {
  var form = document.getElementById(formName);
  if(form == null) return false;
  //alert('in submitForm(' + formName + ')');
  form.submit();
}

function checkValue(s,n,v) {
  var found = false;
  for (c=0;c<s["revealList"].length;c++) {
    if (v == s["revealList"][c]) {
      found = true;
    }
  }
  ref = document.getElementById(n);
  if (found) {
      ref.style.display = "block";
  } else {
      ref.style.display = "none";
  }
}

function drawTable(which) {
  var tempHTML = "";
  for (var x in which["data"]) {
    var temp_code_line = which["line"];
    temp_code_line = temp_code_line.replace(/__LINENUMBER__/g,String(Number(x)+1));
    temp_code_line = temp_code_line.replace(/__TRUENUMBER__/g,String(x));
    temp_code_line = temp_code_line.replace(/\b__COUNT__\b/ig,String(which["data"].length));
    var optionCounter = 0;
    while(temp_code_line.indexOf("__OPTIONSET"+optionCounter+"__")!=-1) {
      var optionCode = "";
      for (var y in which["optionset"+optionCounter]) {
        var optionSplit = which["optionset"+optionCounter][y].split("~");
        var selected = "";
        if (optionSplit[0] == which["data"][x]) {
          selected = "SELECTED";
        }
        var optionCodeItem = which["optionset"+optionCounter+"code"];
        optionCodeItem = optionCodeItem.replace(/__VAL1__/ig,optionSplit[0]);
        optionCodeItem = optionCodeItem.replace(/__VAL2__/ig,optionSplit[1]);
        optionCodeItem = optionCodeItem.replace(/__SELECTED__/ig,selected);
        optionCode += optionCodeItem;

      }

      temp_code_line = temp_code_line.replace("__OPTIONSET"+optionCounter+"__",optionCode);
      optionCounter++;
    }
    tempHTML += temp_code_line;
  }
  var fullCode = which["open"] + tempHTML + which["close"];
  fullCode = fullCode.replace(/__LINENUMBER__/g,String(Number(x)+1));
  fullCode = fullCode.replace(/__TRUENUMBER__/g,String(x));
  fullCode = fullCode.replace(/__COUNTPLUS2__/ig,String(Number(which["data"].length)+2));
  alert

  replaceInnerHtml(document.getElementById(which["div"]), fullCode);
  if (which["revealList"] && which["hiddenDiv"]) {
    for (var x in which["data"]) {
      checkValue(which,"message"+x,which["data"][x]);
    }
  }
}
function alterTable(which, action, index, var1, var2) {
  if (action == "add") {
    which["data"].push(1);
  } else if (action == "remove") {
    which["data"].splice(index,1);
  }
  drawTable(which);
}

function chooseType() {
  var basic = document.getElementById('basicSelect');
  var campaign = document.getElementById('campaignSelect');
  var selected = document.getElementById('selectType');

  if (selected.value == 'basicProcess') {
    basic.style.display = 'block';
  } else if (selected.value == 'campaignProcess') {
    basic.style.display = 'none';
  } else {
    basic.style.display = 'none';
  }

}

/* Reorder Script ********************************************/
theList = new Array("Item #1","Item #2","Item #3","Item #4","Item #5","Item #6","Item #7");
reposID = null;
function redrawList() {
  var tempHTML = "";
  var alternateRow = false;
  for (var x in theList) {
    alternateRow = !alternateRow;
    tempTag = "<tr id='rlContainer_"+x+"' onmouseover='overItem(this);' onmouseout='offItem(this);'";

    if(alternateRow){
      tempTag += " class='alternateRowHighlight' ";
    }

    tempTag += "><td id='rlText_"+x+"' class='reorderListElement'><a href='#' class='rlLink'>"+theList[x]+"</a></td>";
    tempTag += "<td id='rlImg1_"+x+"' class='reorderListImg1' onmousedown='initRepos(this)' title='Move From/To Location'><img src='images/pixel.gif' width='15' height='15' alt=''></td>";
    tempTag += "<td id='rlImg2_"+x+"' class='reorderListImg2' onmousedown='removeItem(this)' title='Remove From List'><img src='images/pixel.gif' width='15' height='15' alt=''></td></tr>";
    tempHTML += tempTag;
  }
  tempHTML = "<table cellpadding='0' cellspacing='0' border='0' width='100%' style='margin-left: 0px;'>"+tempHTML+"</table>";
  //alert(tempHTML);
  replaceInnerHtml(document.getElementById("reorderList"), tempHTML);
}

function overItem(o) {
  var n = o.id.split("_");
  var r = parseInt(n[1]);
  document.getElementById("rlImg2_"+r).className = "reorderListImg2Show";
  var arrow;
  if (reposID == null) {
    if (r == 0) {arrow = "Down";}
    else if (r>=theList.length-1) {arrow = "Up";}
    else {arrow = "Both";}
    document.getElementById("rlImg1_"+r).className = "reorderListImg1"+arrow;
  } else {
    if (r<reposID) {
      arrow = "Up";
      document.getElementById("rlImg1_"+r).className = "reorderListImg1Repos"+arrow+"Over";
    } else if (r>reposID) {
      arrow = "Down";
      document.getElementById("rlImg1_"+r).className = "reorderListImg1Repos"+arrow+"Over";
    }
  }
}
function offItem(o) {
  var n = o.id.split("_");
  var r = parseInt(n[1]);
  var arrow;
  document.getElementById("rlImg2_"+r).className = "reorderListImg2";
  if (reposID == null) {
    document.getElementById("rlImg1_"+r).className = "reorderListImg1";
  } else {
    if (r<reposID) {
      arrow = "Up";
      document.getElementById("rlImg1_"+r).className = "reorderListImg1Repos"+arrow;
    }
    else if (r>reposID) {
      arrow = "Down";
      document.getElementById("rlImg1_"+r).className = "reorderListImg1Repos"+arrow;
    }
  }
}
function removeItem(o) {
  var n = o.id.split("_");
  var r = parseInt(n[1]);
  theList.splice(r,1);
  redrawList();
  reposID = null;
}
function initRepos(o) {
  var n = o.id.split("_");
  var r = parseInt(n[1]);
  if (reposID == null) {
    for (var x in theList) {
      if (x<r) {
        document.getElementById("rlImg1_"+x).className = "reorderListImg1ReposUp";
      } else if (x>r) {
        document.getElementById("rlImg1_"+x).className = "reorderListImg1ReposDown";
      }
    }
    reposID = r;
  } else {
    tempID = theList[reposID];
    if (r>reposID) {
      theList.splice(reposID,1);
      theList.splice(r,0,tempID);
    } else if (r<reposID) {
      theList.splice(reposID,1);
      theList.splice(r,0,tempID);
    }
    reposID = null;
    redrawList();
  }
}

function showAddBox() {
  document.getElementById("addtoListPane").style.display = "block";
  document.getElementById("addItem").style.display = "none";
}

function hideAddBox() {
  document.getElementById("addtoListPane").style.display = "none";
  document.getElementById("addItem").style.display = "block";
  document.getElementById("nameToAdd").value = "";
}

function addtoList() {
  document.getElementById("addtoListPane").style.display = "none";
  document.getElementById("addItem").style.display = "block";

  var newVal = document.getElementById("nameToAdd").value;
  if (newVal.length>0) {
  theList.push(newVal);
  redrawList();
  }
}

keyList = new Array("Key String 1","Key String 2","Key String 3","Key String 4");
valueList = new Array("Value String 1","Value String 2","Value String 3","Value String 4");

function redrawMapEdit() {
  var tempHTML = '<table class="map"><tr><th class="leftAligned"><img src="images\/checkMark1.gif" alt="select items in this column" width="7" height="8" border="0" \/><\/th><th>Key<\/th><th>Value<\/th><th>&nbsp;<\/th><\/tr>';

  for (var x in keyList) {
    tempHTML += '<tr><td class="checkBox"><input type="checkbox" id="listDeleteCheckBox_'+x+ '" \/><\/td>';
    tempHTML += '<td><input type="text" value="' +keyList[x]+ '" id="listKeyText_' + x + '" class="textField" \/><\/td>';
    tempHTML += '<td><input type="text" value="' +valueList[x]+ '" id="listValueText_' +x+ '" class="textField" \/><\/td>';
    tempHTML += '<td class="button"><input type="button" value="edit link" onmousedown="javascript:showIframe(\'assetBrowser\')" \/><\/td><\/tr>';
    }

  tempHTML = tempHTML+"<\/table>";
  replaceInnerHtml(document.getElementById("generatedForm").innerHTML, tempHTML);
}

// JR - NOTE: I arbitrarily chose this data construction - you will probably need to change it
// for each item in the list, there are three values, separated by exclamation points
// within each value, if there are more than one item within the pulldown, they are
// separated by ampersands
// the first one is the selected one
// newGroupItem is an arbitraty set of data that a new characteristic will default to

groupList = new Array("Income&Postal Code!is >!50000");
newGroupItem = "Income&Postal Code!is >&is <&is!50000";

function groupAdd() {
  groupList.push(newGroupItem);
  redrawGroupForm();
}
function groupRemove(w) {
    groupList.splice(w,1);
    redrawGroupForm();
}

function redrawGroupForm() {

  var tempHTML = '<table border="0" cellpadding="3" cellspacing="0">';

  for (var x in groupList) {
    split1 = groupList[x].split("!");
    char0 = split1[0].split("&");
    char1 = split1[1].split("&");
    char2 = split1[2];


    tempHTML += '<tr><td class="leftAlign"><span class="tableInfo">';
    tempHTML += '<select name="property" class="table tableInfo" style="width: auto;">';
    for (y = 0; y<char0.length; y++) {
      tempHTML += '<option>'+char0[y]+'<\/option>';
    }

    tempHTML += '<\/select>';
    tempHTML += '<select name="property" class="table tableInfo" style="width: auto;">';
    for (y = 0; y<char1.length; y++) {
      tempHTML += '<option>'+char1[y]+'<\/option>';
    }
    tempHTML += '<\/select>';
    tempHTML += '<input value="'+char2+'" size="30" maxlength="254" type="text" \/><\/span> ';
    tempHTML += '<\/td>';
    tempHTML += '<td><span class="tableInfo"><input type="button" value="  remove  "  onclick="groupRemove('+x+')"\/><\/span><\/td>';
    tempHTML += '<\/tr>';
  }

  tempHTML += '<tr><td class="rightAlign"> <span class="tableInfo"><input type="button" value="Add Characteristic" onclick="groupAdd()"><\/span><\/td>';
  tempHTML += '<td>&nbsp;<\/td><tr><\/table>';
  replaceInnerHtml(document.getElementById("groupForm"), tempHTML);
}

function addItem() {
  updateValues();
  keyList.push("New Key String");
  valueList.push("New Value String");
  redrawMapEdit();
  }

function deleteItems() {
  updateValues();
  for (x = keyList.length-1;x>=0;x--) {
    var p = document.getElementById("listDeleteCheckBox_"+x).checked;
    if (p) {
      keyList.splice(x,1);
      valueList.splice(x,1);
    }
  }
  redrawMapEdit();
}

function updateValues() {
  for (var x in keyList) {
  keyList[x] = document.getElementById("listKeyText_"+x).value;
  valueList[x] = document.getElementById("listValueText_"+x).value;
  }
}

// Utility method for converting XML strings to printable text...
// This implementation is, admitted, quite simplistic for now...
function escapeXMLText(string) {
  var newstring = string.replace( RegExp("\<","g"), '&lt;');
  return newstring.replace( RegExp("\>","g"), '&gt;');
}

// Utility method for escaping quotes and apostrophes
function escapeQuotes(string) {
  var escapedString = new String(string);
  
  escapedString = escapedString.replace( RegExp("\'","g"), "\\'" ); // apostrophe
  escapedString = escapedString.replace( RegExp("\"","g"), "\\\"" ); // quotation

  return escapedString;
}

// Utility method for decoding Java URI encoded strings...
// The built-in decoder isn't compatible with Java's encoder because it
// won't decode '+' back into ' '...    that's really, really stupid, isn't it?
function decodeJavaURIComponent(string) {
  if ( string == null) {
    return null;
  } else {
    var tmp = string.replace( RegExp("\\+","g"), ' ');
    return decodeURIComponent(tmp);
  }
}

// Utility method for decoding strings escaped by the jstl 
// c:out tag with escapeXml="true"
function decodeEscapedString( input ) {
  var inputString = new String( input );

  var quoteRegExp      = new RegExp( "&#034;", "g" );
  var apostropheRegExp = new RegExp( "&#039;", "g" );
  var lessRegExp       = new RegExp( "&#060;", "g" );
  var greaterRegExp    = new RegExp( "&#062;", "g" );
  var ampersandRegExp  = new RegExp( "&#038;", "g" );
  
  // decode the following characters <,>,&,',"
  inputString = inputString.replace( quoteRegExp,      "\'" );
  inputString = inputString.replace( apostropheRegExp, "\'" );
  inputString = inputString.replace( lessRegExp,       "\<" );
  inputString = inputString.replace( greaterRegExp,    "\>" );
  inputString = inputString.replace( ampersandRegExp,  "\&" );
  
  return inputString;
}

// Utility method to replace line breaks with spaces and to 
// truncate the resulting string to 255 characters
function truncateString( string ) {
  
  // remove line breaks (IE)
  var IELineBreakRegExp = new RegExp("%0D%0A", "g");
  string = string.replace( IELineBreakRegExp, "%20" );
  
  // remove line breaks (Mozilla)
  var MozLineBreakRegExp = new RegExp("%0A", "g");
  string = string.replace( MozLineBreakRegExp, "%20");

  // truncate to 255 characters
  if ( string.length > 255) {
    string = string.substring(0,255) + " [...]";
  }
  return string;
}

function popup(url, name, width, height) {
  atg_popupSettings="toolbar=no,location=no,directories=no,"+
           "status=no,menubar=no,scrollbars=yes,"+
           "resizable=no,width="+width+",height="+height;
  atg_myNewWindow=window.open(url,name,atg_popupSettings);
}

function toggleDisplay(me) {
  if (me.style.display=="block"){
    me.style.display="none";
  }
  else {
    me.style.display="block";
  }
}

function displayToggle(boxid, onclass, offclass){

  if(document.getElementById){
    document.getElementById(boxid).className =(document.getElementById(boxid).className == offclass)? onclass : offclass;
  }
}

/* XMLHttpRequest object */
var xmlhttp=false;

function issueRequest(url, elementName) {

  document.body.style.cursor = "wait";
  if (!xmlhttp)
    ensureXMLHttpRequestObject();

  xmlhttp.open("GET", url, true);
  xmlhttp.onreadystatechange=function() {
    if (xmlhttp.readyState==4) {
      var target = document.getElementById(elementName);
      target=replaceInnerHtml(target, "");
      // 
      // get the response text, search the start of the text for the flag LOGINFORMSERVEDBYAJAX
      // if found, the session is invalid and the login form has been served in the div tag, so redirect the window to 
      // the login page
      var responseText = xmlhttp.responseText;
      var startOfResponseText = responseText.substring(0, 800);

      if (startOfResponseText.indexOf("LOGINFORMSERVEDBYAJAX")>-1) {
        window.location = "/atg/bcc/";

      } else {
        target=replaceInnerHtml(target, responseText);
      }
      document.body.style.cursor = "default";
    }
  }
  xmlhttp.send(null)

}

/* initToggleStates                           */
/*  used to initialize the help bubble states */
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

function initToggleStates(eventType, eventId, targetId, onClass, offClass){
  // get the element ID
  if(document.getElementById(eventId)){
    var element = document.getElementById(eventId);
  }
  if(element){
    //var currentEvents = (element.onclick) ? element.onclick : function () {};
    addEvent(element, 'click', function(){ displayToggle(targetId, onClass, offClass);}, true)
  }else{
    //no element found, debug error handling here if needed
    return true;
  }

}
function ensureXMLHttpRequestObject() {
  if (typeof ActiveXObject != "undefined") {
    // On IE, XMLHttpRequest is implemented using ActiveX.
    try {
      xmlhttp = new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e1) {
      try {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
      }
      catch (e2) {}
    }
  }
  else if (typeof XMLHttpRequest != "undefined") {
    // Browsers such as Firefox include XMLHttpRequest objects in JavaScript.
    xmlhttp = new XMLHttpRequest();
  }
}
function isEscaped(str){
  var result;
  var i= str.indexOf('\'');
  var x = i-1;
   if(str.charAt(x) == '\\'){
     result = new Boolean(true);
    }
  else{
   result = new Boolean(false);
   }
  return result
}
-->
