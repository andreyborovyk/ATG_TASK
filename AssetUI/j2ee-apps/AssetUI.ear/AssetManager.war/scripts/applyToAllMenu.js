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
 * Apply-to-all popup menu.
 *
 * @version $Id$$Change$
 * @updated $DateTime$$Author$
 */

dojo.require("dijit.Menu");

//----------------------------------------------------------------------------//
/**
 * Localizable display strings.
 */
dojo.provide("atg.assetmanager.ApplyToAllMenuStrings");
atg.assetmanager.ApplyToAllMenuStrings = {
  changeLabel:      null,
  clearLabel:       null,
  clearDescription: null,
  keepLabel:        null,
  keepDescription:  null
};

//----------------------------------------------------------------------------//
/**
 * Menu implementation.
 */
dojo.provide("atg.assetmanager.ApplyToAllMenu");
dojo.declare("atg.assetmanager.ApplyToAllMenu", null, {

  //--------------------------------------------------------------------------//
  // Fields
  //--------------------------------------------------------------------------//

  /** The name of the variable that stores this instance */
  name: null,

  /** ID of the form input that stores the value of the apply-to-all mode */
  inputId: null,

  /** ID of the menu widget */
  menuId: null,

  /** ID of the icon that launches the menu */
  iconId: null,

  /** ID of the div containing the associated property editor */
  propEditorDivId: null,

  /** ID of the div containing the description of the apply-to-all mode */
  descriptionDivId: null,

  /** The name of the current property view */
  propertyViewId: null,

  /** Is this property required */
  propertyRequired: false,

  //--------------------------------------------------------------------------//
  // Methods
  //--------------------------------------------------------------------------//

  /**
   * Create the menu widget.
   */
  createMenu: function() {
    var menu = new dijit.Menu({id: this.menuId});
    var self = this;
    menu.addChild(new dijit.MenuItem({label:     atg.assetmanager.ApplyToAllMenuStrings.changeLabel,
                                      iconClass: "applyToAllMenuIcon applyToAllMenuIcon_change",
                                      onClick:   function() {self.onChange();}}));
    if (!this.propertyRequired) {
    menu.addChild(new dijit.MenuItem({label:     atg.assetmanager.ApplyToAllMenuStrings.clearLabel,
                                      iconClass: "applyToAllMenuIcon applyToAllMenuIcon_clear",
                                      onClick:   function() {self.onClear();}}));
    }
    menu.addChild(new dijit.MenuItem({label:     atg.assetmanager.ApplyToAllMenuStrings.keepLabel,
                                      iconClass: "applyToAllMenuIcon applyToAllMenuIcon_keep",
                                      onClick:   function() {self.onKeep();}}));
    menu.startup();
  },
  
  //--------------------------------------------------------------------------//
  /**
   * Callback for when "Change" is selected from the menu.
   */
  onChange: function() {
    this.changeMode("Change", "iconATA_change");
  },
  
  //--------------------------------------------------------------------------//
  /**
   * Callback for when "Clear" is selected from the menu.
   */
  onClear: function() {
    this.changeMode("MakeNull", "iconATA_null", atg.assetmanager.ApplyToAllMenuStrings.clearDescription);
  },
  
  //--------------------------------------------------------------------------//
  /**
   * Callback for when "Keep Existing Value" is selected from the menu.
   */
  onKeep: function() {
    this.changeMode("Keep", "iconATA_leave", atg.assetmanager.ApplyToAllMenuStrings.keepDescription);
  },
  
  //--------------------------------------------------------------------------//
  /**
   * Change the apply-to-all mode as specified.
   * 
   * @param mode         The new mode
   * @param iconClass    The new class for the launching icon
   * @param description  The description of the new mode
   */
  changeMode: function(mode, iconClass, description) {

    // Store the new mode value in the form input.
    var input = document.getElementById(this.inputId);
    input.value = mode;

    // Update the display of the launching icon.
    var icon = document.getElementById(this.iconId);
    icon.className = iconClass;

    var propEditor = document.getElementById(this.propEditorDivId);
    var descriptionArea = document.getElementById(this.descriptionDivId);

    // If a description was provided, display it in the description area as a
    // link that is equivalent to selecting "Change" from the popup menu.  Ensure
    // that the description area is visible and the property editor is not.
    //
    // If no description was provided, "Change" was selected from the popup menu.
    // Hide the description area, and make the property editor visible.
    if (description) {
      propEditor.style.display = "none";
      var innerHTML = "<em><a href=\"javascript:" + this.name + ".onChange()\">" + description + "</a></em>";
      descriptionArea.style.display = "inline";
      descriptionArea.innerHTML = innerHTML;
    }
    else {
      propEditor.style.display = "inline";
      descriptionArea.style.display = "none";
      fireOnActivate(this.propertyViewId);
    }
  }
});
