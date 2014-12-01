/**
 * A client-side object that provides an interface to the asset picker.
 * 
 * @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/scripts/assetPickerFunctions.js#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

//
// AssetBrowser constructor
//
function AssetBrowser()
{
  this.assetTypes; // array of assetType objects. Required

  // Parameters to be encoded in the URL
  this.browserMode        = "project";
  this.assetInfoPath      = "/atg/web/assetmanager/AssetPickerInfo";
  this.isAllowMultiSelect = "true";
  this.createMode         = "none";
  this.closeAction        = "refresh";
  this.clearAttributes    = "false";
  this.mapMode            = "pick";
  this.assetPickerTitle;
  this.assetPickerHeader;
  this.dialogType = "okCancel";  // Differs from default in BIZUI.
  this.styleSheet = "/AssetManager/css/styles.css";
  this.headIncludeContextRoot = "/AssetManager"; // context root for the default include file
  this.headIncludeFile = "/components/head.jsp"; // default include file
  this.callerData;
  this.onHideData;

  // whether to hide the tabs for search and browse. leaves user stuck on the default tab.
  this.hideTabs;

  // adding an extra field to the picked (e.g. a "name" field for creating a save as picker)
  this.extraFieldId;
  this.extraFieldLabel;
  this.extraFieldValueRequired;
  this.extraFieldRequiredMessage;

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

  if ( this['hideTabs'] != undefined )
    url += "&hideTabs=" + encodeURIComponent( this.hideTabs );

  url += "&isAllowMultiSelect=" + this.isAllowMultiSelect;
  url += "&createMode=" + this.createMode;
  url += "&closeAction=" + this.closeAction;
  url += "&clearAttributes=" + this.clearAttributes;
  url += "&mapMode=" + this.mapMode;

  if ( this['assetPickerTitle'] != undefined )
    url += "&assetPickerTitle=" + encodeURIComponent(this.assetPickerTitle);
  if ( this['assetPickerHeader'] != undefined )
    url += "&assetPickerHeader=" + encodeURIComponent(this.assetPickerHeader);
  if ( this['onSelect'] != undefined )
    url += "&onSelect=" + this.onSelect;
  if ( this['onHide'] != undefined )
    url += "&onHide=" + this.onHide;
  if ( this['dialogType'] != undefined )
    url += "&dialogType=" + this.dialogType;
  if ( this['styleSheet'] != undefined )
    url += "&styleSheet=" + this.styleSheet;
  if ( this['headIncludeFile'] != undefined ) {
    if ( this['headIncludeContextRoot'] != undefined )
      url += "&headIncludeContextRoot=" + encodeURIComponent(this.headIncludeContextRoot);
    url += "&headIncludeFile=" + encodeURIComponent(this.headIncludeFile);
  }
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
  if ( this['extraFieldId'] != undefined )
    url += "&extraFieldId=" + encodeURIComponent( this.extraFieldId );
  if ( this['extraFieldLabel'] != undefined )
    url += "&extraFieldLabel=" + encodeURIComponent( this.extraFieldLabel );
  if ( this['extraFieldValueRequired'] != undefined )
    url += "&extraFieldValueRequired=" + encodeURIComponent( this.extraFieldValueRequired );
  if ( this['extraFieldRequiredMessage'] != undefined )
    url += "&extraFieldRequiredMessage=" + encodeURIComponent( this.extraFieldRequiredMessage );

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
