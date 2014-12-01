/*<ATGCOPYRIGHT>
 * Copyright (C) 2008 Art Technology Group, Inc.
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
 * A client-side object used to configure the appearance and behavior of
 * the asset picker.
 *
 * @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/scripts/assetPickerLauncher.js#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
dojo.provide("atg.assetmanager");

// The default view of the asset picker.  set this to Browse or Search globally.
// Override with defaultView member var (below)
globalAssetPickerDefaultView = null;

dojo.provide("atg.assetmanager.AssetPickerLauncher");

dojo.declare("atg.assetmanager.AssetPickerLauncher", null, {

  /////////////////////////////////////////////////////////////////////
  //        FIELDS
  /////////////////////////////////////////////////////////////////////

  // array of assetType objects. Required.  To avoid the "select asset type" screen
  // just put one type in here.  Put a comma delimited list of all pickable types
  // in pickableTypesCommaList
  assetTypes: null,

  // a comma delimited list of the names of all pickable types.  this will be used
  // by the browse view to decide which types are pickable.
  pickableTypesCommaList: null,

  // The default view of the picker to show (Browse, Search, or null)
  defaultView: null,

  // The tree component path for the asset picker.
  treeComponent: null,

  // The URL for rendering the asset picker
  assetPickerUrl: null,

  // The asset picker title string
  assetPickerTitle: null,

  // The asset picker header string
  assetPickerHeader: null,

  // caller data
  callerData: null,

  // browser mode
  browserMode: "pick",

  // whether to allow multiselect?  true or false
  allowMultiSelect: "false",

  // function to call when picker OK button is clicked
  onSelectFunction: "onPickerSelect",

  // function to call when the picker is closed
  onHideFunction: null,

  // data to be passed to the onHideFunction
  onHideData: null,
  
  mapMode: null,

  /////////////////////////////////////////////////////////////////////
  //        UTILITY METHODS
  /////////////////////////////////////////////////////////////////////

  // Launch the asset picker.
  invoke: function() {

    // instantiate the picker
    var picker = new AssetBrowser();

    // get the default view
    if (this.defaultView == null) {
      this.defaultView = globalAssetPickerDefaultView;
    }

    // if we have a tree, then we want to configure a browse tab
    if (this.treeComponent) {
      var viewConfiguration = new Array();
      viewConfiguration.treeComponent=this.treeComponent;
      viewConfiguration.itemTypes = this.pickableTypesCommaList;

      // set values on the picker
      picker.viewConfiguration = viewConfiguration;
      picker.mapMode = ((null == this.mapMode) || (0 == this.mapMode.length))
          ? "AssetManager.assetPicker"
          : this.mapMode;

      if (this.defaultView == null || this.defaultView == "Browse") {
        picker.defaultView = "Browse";
      }
    }
    else {
      // no tree?  then configure just the search view
      picker.mapMode = "AssetManager.assetPicker.search";
      picker.defaultView = "Search";
    }

    if (picker.defaultView == null) {
      picker.defaultView = this.defaultView;
    }

    // attributes that don't change for whole asset manager
    picker.clearAttributes         = "true";
    picker.createMode              = "none";
    picker.closeAction             = "hide";
    picker.assetPickerParentWindow = window;
    picker.assetPickerFrameElement = parent.document.getElementById('iFrame');
    picker.assetPickerDivId        = "browser";

    // attributes that should be configured for use in asset manager
    picker.assetTypes              = this.assetTypes;
    picker.browserMode             = this.browserMode;
    picker.isAllowMultiSelect      = this.allowMultiSelect;
    picker.assetPickerTitle        = this.assetPickerTitle;
    picker.assetPickerHeader       = this.assetPickerHeader;
    picker.pickerURL               = this.pickerURL;
    picker.onSelect                = this.onSelectFunction;
    picker.callerData              = this.callerData;
    if (this.onHideFunction) {
      picker.onHide = this.onHideFunction;
    }
    if (this.onHideData) {
      picker.onHideData = this.onHideData;
    }

    // all systems go!
    picker.invoke();
  }
});
