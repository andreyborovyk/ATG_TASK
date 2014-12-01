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

dojo.require("dijit.Menu");

/* Global variables declared */
var onShow = "";
var aclOnShow = "";
var backupAclString = "";
var aclEditable = true;
var aclEditorStrings = {};

//----------------------------------------------------------------------------//
/**
 * Popup menu implementation.
 */
dojo.provide("atg.assetmanager.ACLEditorMenu");
dojo.declare("atg.assetmanager.ACLEditorMenu", null, {

  //--------------------------------------------------------------------------//
  // Fields
  //--------------------------------------------------------------------------//

  /** ID of the menu widget */
  menuId: null,

  /** ID of the icon that launches the menu */
  iconId: null,

  /** ID of the associated persona */
  persona: null,

  /** ID of the associated access right */
  accessRight: null,

  //--------------------------------------------------------------------------//
  // Methods
  //--------------------------------------------------------------------------//

  /**
   * Create the menu widget.
   */
  createMenu: function() {
    var menu = new dijit.Menu({id: this.menuId});
    var self = this;
    menu.addChild(new dijit.MenuItem({label:     aclEditorStrings.allowLabel,
                                      iconClass: "aclMenuIcon aclMenuIcon_allow",
                                      onClick:   function() {self.onAllow();}}));
    menu.addChild(new dijit.MenuItem({label:     aclEditorStrings.denyLabel,
                                      iconClass: "aclMenuIcon aclMenuIcon_deny",
                                      onClick:   function() {self.onDeny();}}));
    menu.addChild(new dijit.MenuItem({label:     aclEditorStrings.unspecifiedLabel,
                                      iconClass: "aclMenuIcon aclMenuIcon_unspecified",
                                      onClick:   function() {self.onClear();}}));
    menu.startup();
  },
  
  //--------------------------------------------------------------------------//
  /**
   * Callback for when "Allow" is selected from the menu.
   */
  onAllow: function() {
    this.changeMode("allow", "iconATA_allow");
  },
  
  
  //--------------------------------------------------------------------------//
  /**
   * Callback for when "Deny" is selected from the menu.
   */
  onDeny: function() {
    this.changeMode("deny", "iconATA_deny");
  },
  
  //--------------------------------------------------------------------------//
  /**
   * Callback for when "Unspecified" is selected from the menu.
   */
  onClear: function() {
    this.changeMode("mixed", "iconATA_mixed");
  },

  //--------------------------------------------------------------------------//
  /**
   * Change the ACL as specified.
   * 
   * @param mode       The new mode
   * @param iconClass  The new class for the launching icon
   */
  changeMode: function(mode, iconClass) {

    // Update the display of the launching icon.
    var icon = document.getElementById(this.iconId);
    icon.className = iconClass;

    // Apply the new mode to the given persona/access-right.
    modifyAccessControlList(this.persona, this.accessRight, mode);
  }
});

//----------------------------------------------------------------------------//
    
/* EVENT BASED FUNCTIONS */
function backUpToTag(t, targetTag) {
  do {
    t = t.parentNode
  } while ( t.nodeName.toUpperCase() !== targetTag.toUpperCase());
  return t;
}

// prevents firefixes quirk of finding text nodes on newlines and whitespace
// use like: nextChild = getNextSibling(child1);
function getNextSibling(startSibling) {
  if(endSibling=startSibling.nextSibling) {
    while(endSibling.nodeType!=1) {
      endSibling = endSibling.nextSibling;
    }
    return endSibling;
  }else {
    return false;
  }
}

// Show and Hide rule sets
function rulesDisplay(e) {
  var targ;
  if (!e) 
    var e = window.event;
    
  if (e.target) 
    targ = e.target;
  else if (e.srcElement) 
    targ = e.srcElement;
    
  if (targ.nodeType == 3) {
    // defeat Safari bug
    targ = targ.parentNode;
  }
  var detail = getNextSibling(targ.parentNode.parentNode);
  
  detail.style.display = (detail.style.display != 'none')? "none" : "block";
  targ.className = (detail.style.display != 'none')? "iconPanelMinimize" : "iconPanelMaximize";
  return false;
}

/* Initalise the Radio selection switched */
function initRulesDisplay() {
  var els = getElementsByClassName("rulesTrigger", true);
  if(els.length > 0) {
    var i = els.length;
    do {
      addEvent(els[i-1], 'click', rulesDisplay, false);
      els[i-1].href = "javascript:void(0)";
    }
    while (--i);
  }
}

// Show and Hide rule sets
function priceListsSecurityDisplay(e) {
  var targ;
  if (!e) var e = window.event;
  if (e.target) targ = e.target;
  else if (e.srcElement) targ = e.srcElement;
  if (targ.nodeType == 3) {// defeat Safari bug
  targ = targ.parentNode;
  }
  var movinUp = backUpToTag(targ, "tbody")
  var detail = getNextSibling(movinUp)
  if (document.all) {
  detail.style.display = (detail.style.display != 'none')? "none" : "block";
  }
  else {
  detail.style.display = (detail.style.display != 'none')? "none" : "table-row-group";
  }
  targ.innerHTML = (targ.innerHTML != null && targ.innerHTML.indexOf("Hide") < 0)? "Hide" : "Show";
  return false;
}

/* Initalize the Show and Hide rule sets */
function initPriceListsSecurityDisplay() {
  var els = getElementsByClassName("priceSecurityTrigger", true);
  if(els.length > 0) {
    var i = els.length;
    do {
      addEvent(els[i-1], 'click', priceListsSecurityDisplay, false);
      els[i-1].href = "javascript:void(0)";
    }
    while (--i);
  }
}

function priceListSecurityDefault() {
  var els = getElementsByClassName("defaultTrigger");
  var dataContent = getElementsByClassName('priceListSecurity')[0].getElementsByTagName('tbody');
  var tableButtons = getElementsByClassName('addNew')[0];
  for(i = 0; i<dataContent.length; i++) {
    if(document.all) {
    dataContent[i].style.display = (els[0].checked)? "none" : "block";
    }
    else {
    dataContent[i].style.display = (els[0].checked)? "none" : "table-row-group";
    }
  }
  if(document.all) {
  tableButtons.style.display = (els[0].checked)? "none" : "block";
  }
  else {
  tableButtons.style.display = (els[0].checked)? "none" : "block";
  }
  
  if(els[0].checked) {
    var rolesId = document.getElementById("principalRolesId");
    var userId = document.getElementById("principalUserId");
    var orgId = document.getElementById("principalOrgId");

    rolesId.innerHTML = "Hide";
    userId.innerHTML = "Hide";
    orgId.innerHTML = "Hide";
  }
}

function initPriceListSecurityDefault() {
  var els = getElementsByClassName("defaultTrigger", true);
  priceListSecurityDefault();
  if(els.length > 0) {
    var i = els.length;
    do {
      addEvent(els[i-1], 'click', priceListSecurityDefault, false);
      els[i-1].href = "javascript:void(0)";
    }
    while (--i);
  }
}

// Show and Hide alert details
function sortSwitch(e) {
  var targ;
  if (!e) var e = window.event;
  if (e.target) targ = e.target;
  else if (e.srcElement) targ = e.srcElement;
  if (targ.nodeType == 3) {// defeat Safari bug
  targ = targ.parentNode;
  }

  var options = targ.getElementsByTagName('option');
  currentSelection = options[targ.selectedIndex].innerHTML;

  var detail = targ.parentNode.firstChild

  detail.className = (currentSelection == 'Ascending')? "iconAscending" : "iconDescending";
  return false;
}

/* Initalise the Radio selection switched */
function initSortSwitch() {
  var els = getElementsByClassName("sortTrigger", true);
  if(els.length > 0) {
    var i = els.length;
    do {
      addEvent(els[i-1], 'change', sortSwitch, false);
      els[i-1].href = "javascript:void(0)";
    }
    while (--i);
  }
}

/* Render the principal rows.*/
function addPrincipalRow(aclEntry, aclAllEntries, denied, imageClass) {
  var defaultAccess="allow";
  var values = {
    list: defaultAccess,
    read: defaultAccess,
    write: defaultAccess,
    destroy: defaultAccess,
    write_owner: defaultAccess,
    read_acl: defaultAccess,
    write_acl: defaultAccess
  };
  var personae = "";
  var rights = new Array();
  var keyValue = new Array();
  var aclRights;
  var principalName;
  var temp = null;
  var found = false;

  // Get all rights for the ACL Entry
  aclRights = aclEntry.substring(aclEntry.indexOf(":") + 1);

  // Remove '}' curly braces which is presend in case of deny string
  if (aclRights.indexOf("}") != -1) {
    aclRights = aclRights.substring(0, aclRights.indexOf("}"));
  }

  // Break the ACL rights string into ACL rights array
  if (aclRights.indexOf(",") != -1) 
    rights = aclRights.split(',');      
  else 
    rights[0] = aclRights;

  // Get the name of the principal
  principalName = aclEntry.substring(aclEntry.lastIndexOf('$') + 1, aclEntry.indexOf(':'));

  var keyValueString = document.getElementById("aclKeyValue").value;
  keyValue = keyValueString.split(';');

  for(var i=0;i<keyValue.length;i++) {
     if (principalName == keyValue[i].substring(0,keyValue[i].indexOf(',')))
      principalName =keyValue[i].substring(keyValue[i].indexOf(',')+1,keyValue[i].length); 
  }

  // Get the personae and checks whether it exists in the acl list or not only in case of deny acl. 
  if (aclEntry.indexOf("deny") != -1) {
    personae = aclEntry.substring(aclEntry.indexOf('{') + 1, aclEntry.indexOf(':'));
    for (var i = 0; i < aclAllEntries.length; i++) {
      if (aclAllEntries[i].indexOf(personae) != -1 && aclAllEntries[i].charAt(aclAllEntries[i].indexOf(personae) - 1) != "{") {
        found = true;
        break;
      } // end of if
    } // end of for
  }// end of if

  // Checks --
  for (var i = 0; i < denied.length; i++) {
    var denyAclEntry = denied[i];
    var denyPersona = denyAclEntry.substring(denyAclEntry.indexOf("{") + 1, denyAclEntry.indexOf(":"));

    if (aclEntry.indexOf(denyPersona) != -1) {
      denyAclEntry = denyAclEntry.substring(0, denyAclEntry.indexOf("}"));
      temp = denyAclEntry;
    } // end of if
  } // end of for

  // Populate right objects with the values viz. allow or deny or mixed
  if (aclEntry.indexOf("deny") == -1) {
    personae = aclEntry.substring(0, aclEntry.indexOf(':'));
    if (temp != null) {
      var denyRight = null;
      var denyRights = new Array();
      denyRight = temp.substring(temp.indexOf(":") + 1);
      if (denyRight != null) {
        if (denyRight.indexOf(",") != -1) 
          denyRights = denyRight.split(',');
        else 
          denyRights[0] = denyRight;            
      } // END OF IF

      if (denyRights.length >= 1) {
        values.list = getAccessRight(rights, denyRights, "list");
        values.read = getAccessRight(rights, denyRights, "read");
        values.write = getAccessRight(rights, denyRights, "write");
        values.destroy = getAccessRight(rights, denyRights, "destroy");
        values.read_owner = getAccessRight(rights, denyRights, "read_owner");
        values.write_owner = getAccessRight(rights, denyRights, "write_owner");
        values.read_acl = getAccessRight(rights, denyRights, "read_acl");
        values.write_acl = getAccessRight(rights, denyRights, "write_acl");
      } // END OF IF
    }
    else {
      values.list = getAclAccessRight(rights, "list");
      values.read = getAclAccessRight(rights, "read");
      values.write = getAclAccessRight(rights, "write");
      values.destroy = getAclAccessRight(rights, "destroy");
      values.read_owner = getAclAccessRight(rights, "read_owner");
      values.write_owner = getAclAccessRight(rights, "write_owner");
      values.read_acl = getAclAccessRight(rights, "read_acl");
      values.write_acl = getAclAccessRight(rights, "write_acl");
    }
  }
  else if (found == false) {
    values.list = getDenyAclAccessRight(rights, "list");
    values.read = getDenyAclAccessRight(rights, "read");
    values.write = getDenyAclAccessRight(rights, "write");
    values.destroy = getDenyAclAccessRight(rights, "destroy");
    values.read_owner = getDenyAclAccessRight(rights, "read_owner");
    values.write_owner = getDenyAclAccessRight(rights, "write_owner");
    values.read_acl = getDenyAclAccessRight(rights, "read_acl");
    values.write_acl = getDenyAclAccessRight(rights, "write_acl");
  }
  else {
    personae = null;
  }

  // Renders the row on the UI when personae is not null.
  if (personae != null) {
    var tbodyId = personae.substring(personae.indexOf('$') + 1, personae.lastIndexOf('$'));
    addRowForPersona(tbodyId, personae, principalName, values);
  }
}

//----------------------------------------------------------------------------//
/**
 * Add a table row for a given persona.  Note that this function is called during
 * initial construction of the table and after the user selects persona(e) to be
 * added using the asset picker.
 * 
 * @param tbodyId      The ID of the table body corresponding to the persona type
 * @param persona      The ID of the persona
 * @param displayName  The display name of the persona
 * @param values       Object containing key-value pairs for the given persona's
 *                     access rights (e.g. list="allow")
 */
function addRowForPersona(tbodyId, persona, displayName, values) {

  // Protect against null "values".
  values = values || {};

  // Add a row to the table body.  Use a class name that corresponds to the
  // persona ID, so that showRow and hideRow can locate the row by persona.
  var tbodyElement = document.getElementById(tbodyId);              
  var trElement = tbodyElement.insertRow(tbodyElement.rows.length);
  trElement.className = persona;

  // The first cell contains a persona-type-specific icon along with the
  // persona's display name.
  var tdElement = trElement.insertCell(trElement.cells.length);
  var imgClass = "wideLabel " + tbodyId + "Label";
  tdElement.innerHTML = '<div class="' + imgClass + '"/>' + displayName;

  // Add one table cell for each of the available access rights.
  addCellForAccessRight(trElement, persona, "list", values.list);
  addCellForAccessRight(trElement, persona, "read", values.read);
  addCellForAccessRight(trElement, persona, "write", values.write);
  addCellForAccessRight(trElement, persona, "destroy", values.destroy);
  addCellForAccessRight(trElement, persona, "read_owner", values.read_owner);
  addCellForAccessRight(trElement, persona, "write_owner", values.write_owner);
  addCellForAccessRight(trElement, persona, "read_acl", values.read_acl);
  addCellForAccessRight(trElement, persona, "write_acl", values.write_acl);

  // The final cell contains an icon for removing the row from the table (if
  // the ACL is editable).
  tdElement = trElement.insertCell(trElement.cells.length);
  tdElement.className = "iconCell"
  if (aclEditable)
    tdElement.innerHTML = '<a href="javascript:submitRemovePersona(\'' + persona + '\')"' +
                          ' class="icon propertyClear" title="' +
                          aclEditorStrings.removePersonaTitle + '"/>';
}

//----------------------------------------------------------------------------//
/**
 * Add a table cell for a given access right.
 * 
 * @param trElement    The table row to which the cell should be added
 * @param persona      The ID of the persona to which the access right applies
 * @param accessRight  The name of the access right
 * @param value        The value of the access right ("allow", "deny", or "mixed")
 */
function addCellForAccessRight(trElement, persona, accessRight, value) {

  // Derive unique IDs for DOM elements, and determine the icon class.
  var key = persona + accessRight;
  var iconId = "ataDD_" + key;
  var menuId = "aclMenu_" + key;
  var iconClass = (value ? getIconClassName(value) : getIconClassName("mixed"));

  // Add a table call containing an icon button that pops up an ACL menu.
  var tdElement = trElement.insertCell(trElement.cells.length);
  var innerHTML = "";
  innerHTML += '<div class="ataDD">';
  innerHTML += '  <a id="' + iconId + '" href="#" class="' + iconClass + '"';
  innerHTML += '     onclick="displayMenuAtEventTarget(\'' + menuId + '\', event)"';
  innerHTML += '     title="' + aclEditorStrings.accessRightTooltip + '"></a>';
  innerHTML += '</div>';
  tdElement.innerHTML = innerHTML;

  // Create the popup menu.
  if (aclEditable) {
    var menu = new atg.assetmanager.ACLEditorMenu();
    menu.menuId      = menuId;
    menu.iconId      = iconId;
    menu.persona     = persona;
    menu.accessRight = accessRight;
    menu.createMenu();
  }
}

/* Returns the CSS class to render the access right.*/
function getIconClassName(pRight) {
  if (pRight == "allow") 
    return "iconATA_allow";

  if (pRight == "deny") 
    return "iconATA_deny";

  if (pRight == "mixed") 
    return "iconATA_mixed";      
}

/* Returns the access right*/
function getAclRight(pRight, rightName, returnValue) {
  var accessRight = "mixed";
  for (var i = 0; i < pRight.length; i++) {
    if (pRight[i] == rightName) {
      accessRight = returnValue;
    }
  }
  return accessRight;
}

/* Returns the access right for acl */
function getAclAccessRight(pRight, rightName) {
  var returnValue = "allow";  
  return getAclRight(pRight, rightName, returnValue);
}

/* Returns the access right for deny acl */
function getDenyAclAccessRight(pRight, rightName) {
   var returnValue = "deny";  
   return getAclRight(pRight, rightName, returnValue);
}

/* Returns the access right for acl*/
function getAccessRight(pRight, pDenyRight, rightName) {
  var retVal = getAclAccessRight(pRight,rightName);
  if( retVal != "allow") {
    retVal =getDenyAclAccessRight(pDenyRight, rightName);
  }
  return retVal;
}

/* Remove entry form the access control list*/
function removeEntry(personaIndex, accessControlListString) {
   var aclString;
   var newlen;
   var beforeAcl = accessControlListString.substring(0,personaIndex);
   accessControlListString = accessControlListString.substring(personaIndex);

   if( accessControlListString.indexOf(";") == -1 )
     aclString = accessControlListString.substring(0,accessControlListString.length);
   else
     aclString = accessControlListString.substring(0,accessControlListString.indexOf(";")+1);

  if( onShow!="" ) {
    if( onShow.indexOf(";") == -1 )
      onShow = onShow+";" + aclString;
     else
      onShow = onShow + aclString;
  }
  else 
   onShow = onShow + aclString;

   newlen = aclString.length;
   accessControlListString = accessControlListString.substring(newlen);
   accessControlListString = beforeAcl + accessControlListString;

   if( accessControlListString.lastIndexOf(";") == accessControlListString.length-1 )
      accessControlListString = accessControlListString.substring(0,accessControlListString.lastIndexOf(";"));

   return accessControlListString;
}

/* Removes the desired persona from the accessControlList .*/
function submitRemovePersona(personae) {
  onShow = "";
  var aclCompleteString = document.getElementById("aclText").value;        
  var denyString= "deny{" + personae;
  var denyindex = aclCompleteString.indexOf(denyString);
  var indexAcl;
  aclOnShow = aclCompleteString;

  if(denyindex != -1) {
   aclCompleteString = removeEntry(denyindex,aclCompleteString);
  }

  indexAcl = aclCompleteString.indexOf(personae);

  if(indexAcl !=-1) {
    aclCompleteString = removeEntry(indexAcl,aclCompleteString);
  }  
  hideRow(personae);
  document.getElementById("aclText").value=aclCompleteString;
  markAssetModified();
}

/* Hide the principal row when clicked on remove persona button .*/
function hideRow(personae) {  
  var els = getElementsByClassName("iconCell");
  if( personae.indexOf(" ") != -1 )
    personae = personae.substring(0,personae.indexOf(" "));
  var dataContent = getElementsByClassName(personae);  
  var tableButtons = getElementsByClassName(personae)[0];
  for( i=1; i < dataContent.length; i++ ) {
    if( document.all ) {
      dataContent[i].style.display = (els[0].click)? "none" : "block";
    }
    else {
      dataContent[i].style.display = (els[0].click)? "none" : "table-row-group";
    }
  }
  if( document.all ) {
    tableButtons.style.display = (els[0].click)? "none" : "block";
  }
  else {
    /* Fix for firefox Beta 2,the functions is added as there is no event handler when we click on a HTMLCellElement */
    HTMLElement.prototype.click = function () { 
    }
    tableButtons.style.display = (els[0].click)? "none" : "block";
  }
}

/* Show the principal row when prinicpal is selected from Asset Picker .*/
function showRow(personae) {
  var dataContent = getElementsByClassName(personae);
  var tableButtons = getElementsByClassName(personae)[0];
  for( i = 1; i < dataContent.length; i++) {    
      dataContent[i].style.display = "";      
  }
  if( document.all ) 
    tableButtons.style.display = "";  
  else 
    tableButtons.style.display = "table-row";

  var checkPersonae = "%%";
  if( onShow.indexOf("deny") != -1 )
    checkPersonae = onShow.substring(onShow.indexOf("{")+1,onShow.indexOf(":"));   
  else {
    if(onShow != "")
      checkPersonae = onShow.substring(0,onShow.indexOf(":"));
   }

  if( aclOnShow.indexOf(checkPersonae) != -1 ) {
    var newAclString = document.getElementById("aclText").value;
    if( newAclString!="" )
      newAclString = newAclString+";"+onShow;
    else
      newAclString = newAclString+onShow;

    if( newAclString.lastIndexOf(";") == newAclString.length-1 ) {
      newAclString=newAclString.substring(0,newAclString.lastIndexOf(";"));
    }
      document.getElementById("aclText").value = newAclString;   
      markAssetModified();
  }
}

/* Hide the principal row when clicked on default acl checkbox .*/
function hideACL() {
  var aclString = document.getElementById("aclText").value;
  var check = document.getElementById("default").checked;
  if(check) {
    backupAclString = aclString;
    aclString = "";
  }
  else   
    aclString = backupAclString;    
  document.getElementById("aclText").value = aclString;
  markAssetModified();
}

 // Render table
function initPrincipalsData() {
  var aclListArray = new Array();
  var roles = new Array(0);
  var users = new Array(0);
  var organization = new Array(0);
  var denied = new Array(0);
  var accessControlList = document.getElementById("aclText").value;
  if(accessControlList == "")
    document.getElementById("default").checked = true;
  if(accessControlList.indexOf(";")!=-1)
    aclListArray = accessControlList.split(";");
  else
    aclListArray[0] = accessControlList;

  for (var i = 0; i < aclListArray.length; i++) {
    if (aclListArray[i].indexOf("deny") != -1) {
      var personaePrincipal = aclListArray[i].substring(aclListArray[i].indexOf('$') + 1, aclListArray[i].lastIndexOf('$'));
      if (personaePrincipal == "role")
        roles[roles.length] = aclListArray[i];

      if (personaePrincipal == "user")
        users[users.length] = aclListArray[i];

      if (personaePrincipal == "org")
        organization[organization.length] = aclListArray[i];

    denied[denied.length] = aclListArray[i];
    }
    else {
      var personae = aclListArray[i].substring(0, aclListArray[i].indexOf(':'));
      var personaePrincipal = aclListArray[i].substring(aclListArray[i].indexOf('$') + 1, aclListArray[i].lastIndexOf('$'));

      if (personaePrincipal == "role")
        roles[roles.length] = aclListArray[i];

      if (personaePrincipal == "user")
        users[users.length] = aclListArray[i];

      if (personaePrincipal == "org")
        organization[organization.length] = aclListArray[i];
    }
  }

  if(roles.length<1)
     document.getElementById("roleHeading").style.display="none";

  if(users.length<1)
    document.getElementById("userHeading").style.display="none";

  if(organization.length<1)
    document.getElementById("orgHeading").style.display="none";

  roles.sort();
  users.sort();
  organization.sort();

  for(var x=0; x < roles.length; x++) {  
    addPrincipalRow(roles[x],aclListArray, denied,'wideLabel roleLabel');
  }

  for(var i=0; i < users.length; i++) {
    addPrincipalRow(users[i], aclListArray, denied, 'wideLabel userLabel');
  }

  for(var z=0; z < organization.length; z++) {
    addPrincipalRow(organization[z], aclListArray, denied, 'wideLabel orgLabel');
  }
}

/* Renders the table data on the row*/
function renderTableData(iconID, pRightName, title, pRight) {
  var tableData='<div class="ataDD" id=\"' + iconID + pRightName + '\" title=\"' + title + '\" ><a href="#" class=\"' + getIconClassName(pRight) + '\"  title="Select Allow, Deny or Unspecified" ></a></div>';
  return tableData;
}

// Toggle Asset Browser size 
function frameInit() {
  initPrincipalsData();
  initRulesDisplay();
  initSortSwitch();
  initPriceListsSecurityDisplay();
  initPriceListSecurityDefault();
}

/* Saves the modifed access control list */
function modifyAccessControlList(persona, right, access) {
  var accessControlList = document.getElementById("aclText").value;
  var accessControlEntry = getACLEntries(persona, accessControlList);
  accessControlEntry = modifyAccessRights(accessControlEntry, right, access, persona);
  accessControlList = setACLEntries(accessControlEntry, accessControlList);
  document.getElementById("aclText").value = accessControlList;
  markAssetModified();
}

/* Updates the accessControlList array */
function setACLEntries(accessControlEntry, accessControlList) {
  var aclAllEntries = new Array();
  var aclEntries = new Array();
  var newEntries = new Array();

  if (accessControlList.indexOf(";") != -1)
    aclAllEntries = accessControlList.split(";");
  else
    aclAllEntries[0] = accessControlList;

  for (var i = 0; i < accessControlEntry.length; i++) {
    for (var x = 0; x < aclAllEntries.length; x++) {
      var changedPersona = accessControlEntry[i].substring(0, accessControlEntry[i].indexOf(":"));      
      var persona = aclAllEntries[x].substring(0, aclAllEntries[x].indexOf(":"));
      if (changedPersona == persona)
        aclAllEntries[x] = accessControlEntry[i];
    }
  }

  for (var i = 0; i < accessControlEntry.length; i++) {
    var changedPersona = accessControlEntry[i].substring(0, accessControlEntry[i].indexOf(":"));
    var notAvailaible = true;
    for (var x = 0; x < aclAllEntries.length; x++) {
      var persona = aclAllEntries[x].substring(0, aclAllEntries[x].indexOf(":"));
      if (changedPersona == persona) {
        notAvailaible = false;
        break;
      }
    }
    if (notAvailaible == true) 
      newEntries[newEntries.length] = accessControlEntry[i];    
  }
  
  aclAllEntries = removeACL(aclAllEntries);
  for (var i = 0; i < newEntries.length; i++) {
    aclAllEntries[aclAllEntries.length] = newEntries[i];
  }
  
  var newAccessControlList = "";
  for (var i = 0; i < aclAllEntries.length; i++) {
    if (aclAllEntries[i] != "")
      newAccessControlList = newAccessControlList + aclAllEntries[i] + ";";
  }
  accessControlList = newAccessControlList.substring(0, newAccessControlList.lastIndexOf(";"));
  return accessControlList;
}

/* Returns the acl entries respective to the persona passed  */
function getACLEntries(persona, accessControlList) {
  var aclAllEntries = new Array();
  var aclEntries = new Array();

  if (accessControlList.indexOf(";") != -1)
    aclAllEntries = accessControlList.split(";");
  else
    aclAllEntries[0] = accessControlList;

  for (var i = 0; i < aclAllEntries.length; i++) {
    if (aclAllEntries[i].indexOf(persona) != -1)  
      aclEntries[aclEntries.length] = aclAllEntries[i];    
  }
  return aclEntries;
}

/* Modifies the access control list based on access viz. 'allow', 'mixed' and 'deny' */
function modifyAccessRights(accessControlEntry, right, access, newPersona) {
  if (access == "allow") {
    accessControlEntry = setAllowRights(accessControlEntry, newPersona, right);
    return accessControlEntry;
  }

  if (access == "mixed") {
    accessControlEntry = setMixedRights(accessControlEntry, right)
    return accessControlEntry;
  } 
  if (access == "deny") {
    accessControlEntry = setDenyRights(accessControlEntry, newPersona, right);
    return accessControlEntry;
  }
  return null;
}

/* Removes right from the access right array */
function removeRight(accessRights, right) {
  var accessAllRights = new Array();
  if (accessRights.indexOf(",") != -1)
    accessAllRights = accessRights.split(",");
  else
    accessAllRights[0] = accessRights;
  for (var i = 0; i <= accessAllRights.length; i++) {
    if (accessAllRights[i] == right) 
      accessAllRights[i] = "";
  }
  return accessAllRights;
}

/* Add right to the access right array */
function addRight(accessRights, right) {
  var accessAllRights = new Array();
  var notAvailaible = true;

  if (accessRights.indexOf(",") != -1)
    accessAllRights = accessRights.split(",");
  else
    accessAllRights[0] = accessRights;

  for (var i = 0; i <= accessAllRights.length; i++) {
    if (accessAllRights[i] == right)
      notAvailaible = false;
  }
  if (notAvailaible)
    accessAllRights[accessAllRights.length] = right;
  return accessAllRights;
}

/* Removes the persona from the array which doesn't contains any right */
function removeACL(aclListArray) {
  for (var i = 0; i < aclListArray.length; i++) {
    if (aclListArray[i].charAt(aclListArray[i].length - 1) == ':')    
      aclListArray[i] = "";    
  }
  return aclListArray;
}

/* Add right to non-deny Entry*/
function modifyEntry(pAclEntry,right) {
  var accessRights = pAclEntry.substring(pAclEntry.indexOf(":") + 1);
  var rights = addRight(accessRights, right);
  var persona = pAclEntry.substring(0, pAclEntry.indexOf(":") + 1);
  for (var x = 0; x < rights.length; x++) {
    persona = persona + rights[x] + ",";
  }
  if (persona.indexOf(",") != -1) 
    persona = persona.substring(0, persona.lastIndexOf(","));
  pAclEntry = persona;
  return pAclEntry;
}

/* Remove right from non-deny Entry*/
function modifyRemoveEntry(pAclEntry,right) {
  var accessRights = pAclEntry.substring(pAclEntry.indexOf(":") + 1);
  var rights = removeRight(accessRights, right);
  var persona = pAclEntry.substring(0, pAclEntry.indexOf(":") + 1);
  if (rights.length == 1) {
    if (rights[x] == "") 
      persona = persona;
  }
  else {
    for (var x = 0; x < rights.length; x++) {
      if (rights[x] != "")
        persona = persona + rights[x] + ",";              
    }// end of for
  }
  if (persona.indexOf(",") != -1) 
    persona = persona.substring(0, persona.lastIndexOf(","));

  pAclEntry = persona;
  return pAclEntry;
}

/* Removes right from deny Entry*/
function removeRightFromDeny(pAclEntry,right) {
  var accessRights = pAclEntry.substring(pAclEntry.indexOf(":") + 1, pAclEntry.indexOf("}"));
  var rights = removeRight(accessRights, right);
  var persona = pAclEntry.substring(0, pAclEntry.indexOf(":") + 1);
  if (rights.length == 1) {
    if (rights[0] != "")
      persona = persona + rights[0];
  }
  else {
    for (var x = 0; x < rights.length; x++) {
      if (rights[x] != "") 
        persona = persona + rights[x] + ",";
    }
  }
  if (persona.indexOf(",") != -1) 
    persona = persona.substring(0, persona.lastIndexOf(","));

  persona = persona + "}";
  if (persona.charAt(persona.indexOf(":") + 1) != '}')
    pAclEntry = persona;          
  else
    pAclEntry = persona.substring(0, persona.indexOf("}")); 

  return pAclEntry;
}

/* Add rights to deny Entry*/
function addRightToDeny(pAclEntry,right) {
  var accessRights = pAclEntry.substring(pAclEntry.indexOf(":") + 1, pAclEntry.indexOf("}"));
  var rights = addRight(accessRights, right);
  var persona = pAclEntry.substring(0, pAclEntry.indexOf(":") + 1);
  for (var x = 0; x < rights.length; x++) {
    persona = persona + rights[x] + ",";
  }
  persona = persona.substring(0, persona.lastIndexOf(","));
  pAclEntry = persona + "}";
  return pAclEntry;
}

/* Set access right for "ALLOW"*/
function setAllowRights(accessControlEntry,newPersona,right) {
  if (accessControlEntry.length == 1) {
    if (accessControlEntry[0].indexOf("deny") != -1) {
      accessControlEntry[1] = newPersona + ":" + right;
      accessControlEntry[0]=removeRightFromDeny(accessControlEntry[0], right);
    }
    else {
     accessControlEntry[0] = modifyEntry(accessControlEntry[0], right);
    } 
  }
  else if (accessControlEntry.length == 0) {
    accessControlEntry[accessControlEntry.length] = newPersona + ":" + right;
  }
  else {
    for (var i = 0; i < accessControlEntry.length; i++) {
      if (accessControlEntry[i].indexOf("deny") != -1) {
         accessControlEntry[i]=removeRightFromDeny(accessControlEntry[i], right);
      }
      else {
         accessControlEntry[i] = modifyEntry(accessControlEntry[i], right);
      }
    }
  } 
  return accessControlEntry;
}

/* Set access right for "Mixed"*/
function setMixedRights(accessControlEntry,right) {
  for (var i = 0; i < accessControlEntry.length; i++) {
    if (accessControlEntry[i].indexOf("deny") != -1) {
      accessControlEntry[i] = removeRightFromDeny(accessControlEntry[i], right);
    }
    else {
      accessControlEntry[i] = modifyRemoveEntry(accessControlEntry[i], right);
    }
  }
  return accessControlEntry;
}

/* Set access right for "Deny"*/
function setDenyRights(accessControlEntry,newPersona,right) {
  if (accessControlEntry.length == 1) {
    if (accessControlEntry[0].indexOf("deny") != -1) { 
       accessControlEntry[0] = addRightToDeny( accessControlEntry[0], right);
    }
    else {
      accessControlEntry[0] =  modifyRemoveEntry(accessControlEntry[0], right);
      accessControlEntry[accessControlEntry.length] = "deny{" + newPersona + ":" + right + "}";
     }
  }
  else if (accessControlEntry.length == 0) {
    accessControlEntry[accessControlEntry.length] = "deny{" + newPersona + ":" + right + "}";
  }
  else {
    for (var i = 0; i < accessControlEntry.length; i++) {
      if (accessControlEntry[i].indexOf("deny") != -1) {
        accessControlEntry[i] = addRightToDeny(accessControlEntry[i], right);
      } 
      else {
        accessControlEntry[i] = modifyRemoveEntry(accessControlEntry[i], right);
      } 
    } 
  } 
  return accessControlEntry;
}
