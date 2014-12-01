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
 * Derived property popup menu.
 *
 * @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/scripts/derivedPropertyMenu.js#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */

dojo.require("dijit.Menu");

//----------------------------------------------------------------------------//
/**
 * Localizable display strings.
 */
dojo.provide("atg.assetmanager.DerivedPropertyMenuStrings");
atg.assetmanager.DerivedPropertyMenuStrings = {
  overrideLabel: null,
  inheritLabel:  null
};

//----------------------------------------------------------------------------//
/**
 * Menu implementation.
 */
dojo.provide("atg.assetmanager.DerivedPropertyMenu");
dojo.declare("atg.assetmanager.DerivedPropertyMenu", null, {

  //--------------------------------------------------------------------------//
  // Fields
  //--------------------------------------------------------------------------//

  /** ID of the form input that stores the value of the update mode */
  inputId: null,

  /** ID of the menu widget */
  menuId: null,

  /** ID of the icon that launches the menu */
  iconId: null,

  /** ID of the div containing the associated property editor */
  propEditorDivId: null,

  /** ID of the div containing the inherited value viewer */
  inheritedValueDivId: null,

  //--------------------------------------------------------------------------//
  // Methods
  //--------------------------------------------------------------------------//

  /**
   * Create the menu widget.
   */
  createMenu: function() {
    var menu = new dijit.Menu({id: this.menuId});
    var self = this;
    menu.addChild(new dijit.MenuItem({label:     atg.assetmanager.DerivedPropertyMenuStrings.overrideLabel,
                                      iconClass: "derivedPropertyMenuIcon derivedPropertyMenuIcon_override",
                                      onClick:   function() {self.onOverride();}}));
    menu.addChild(new dijit.MenuItem({label:     atg.assetmanager.DerivedPropertyMenuStrings.inheritLabel,
                                      iconClass: "derivedPropertyMenuIcon derivedPropertyMenuIcon_inherit",
                                      onClick:   function() {self.onInherit();}}));
    menu.startup();
  },
    
  //--------------------------------------------------------------------------//
  /**
   * Callback for when "Override" is selected from the menu.
   */
  onOverride: function() {
    this.changeMode("change", "iconATA_override");
  },
  
  //--------------------------------------------------------------------------//
  /**
   * Callback for when "Inherit" is selected from the menu.
   */
  onInherit: function() {
    this.changeMode("inherit", "iconATA_inherit");
  },
    
  //--------------------------------------------------------------------------//
  /**
   * Change the update mode as specified.
   * 
   * @param mode       The new mode
   * @param iconClass  The new class for the launching icon
   */
  changeMode: function(mode, iconClass) {

    // Store the new mode value in the form input.
    var input = document.getElementById(this.inputId);
    input.value = mode;

    // Update the display of the launching icon.
    var icon = document.getElementById(this.iconId);
    icon.className = iconClass;

    var propEditor = document.getElementById(this.propEditorDivId);
    var inheritDiv = document.getElementById(this.inheritedValueDivId);

    // If the "inherit" mode was specified, hide the property editor and display
    // the inherited value.  Otherwise, do the opposite.
    if (mode == "inherit") {
      propEditor.style.display = "none";
      inheritDiv.style.display = "inline";
    }
    else {
      propEditor.style.display = "inline";
      inheritDiv.style.display = "none";
    }

    markAssetModified();
  }
});
