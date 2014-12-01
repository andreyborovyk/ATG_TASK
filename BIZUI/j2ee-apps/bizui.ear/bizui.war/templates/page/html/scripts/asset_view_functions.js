<!--

// This file contains scripts which support asset and property views


/**
 * Collection handling functions for Asset views...  BNB
 **/


/** Here are some helpers for remembering items picked via the Asset Browser iframe  **/
function collectionSetPickerItem( fieldId) {
   // Set the form field in the form saver to keep this info lying around...
   document.getElementById( 'fs_collectionPickerItem').value = fieldId;
}
function collectionGetPickerItem() {
   // Get the value of the form field in the form saver that keeps this info around...
   return document.getElementById( 'fs_collectionPickerItem').value;
}

/** Here are helpers to deal with different odd field types **/
function collectionBoolSetValue ( fieldId, value) {
  // onclick callback to set the collectionValue hidden field behind a radio group
  var valField = 'collectionValue_' + fieldId;
  document.getElementById( valField).value = value;
}


/** Starting Collection support - BNB **/

collectionData = new Object();
collectionNames = new Array();

function collectionRegister( collName, collType, valuePath, nullPath) {
  // Register a new Collection on the form...
  var collData = new Object();
  collData.name = collName;
  collData.type = collType;
  collData.valuePath = valuePath;
  collData.nullPath = nullPath;
  collData.fnGenHTML = 'collectionDrawItem_' + collName;
  collData.items = new Array();
  collectionData[collName] = collData;
  collectionNames.push( collName);
  return collData;
}

function collectionAddItem( collName, key, value, displayName, assetURI) {
  // Register a new entry with the given collection...
  var collData = collectionData[collName];
  var collEntry = new Object();
  collEntry.key = key;
  collEntry.value = value;
  collEntry.assetURI = assetURI;

  // Remove line breaks and truncate to 255 chracters
  var tmpDN = truncateString( displayName );

  // Decode the display name
  tmpDN = decodeJavaURIComponent( tmpDN );

  // Re-encode it...
  collEntry.displayName = encodeURIComponent(tmpDN);

  collData.items.push(collEntry);
}

function collectionGrabValues( collName) {
  // Pull data from the Collection form and into memory
  var collData = collectionData[collName];

  for (var x in collData.items) {
    collData.items[x].value = document.getElementById("collectionValue_" + collName + "_" + x).value;
    if ( collData.type == "map") {
      collData.items[x].key = document.getElementById("collectionKey_" + collName + "_" + x).value;
    }
    var formItem = document.getElementById("collectionDisplayName_" + collName + "_" + x);
    if ( formItem != null) {
      collData.items[x].displayName = formItem.value;
    }
    var URIItem = document.getElementById("collectionAssetURI_" + collName + "_" + x);
    if ( URIItem != null) {
      collData.items[x].assetURI = URIItem.value;
    }
  }
}

function collectionDraw( collName) {
  // Render the visible form for the collection
  var collData = collectionData[collName];
  var tempHTML = '';

  // Get standard button display text
  var deleteButtonText = "delete selected elements";
  if ( collData.deleteButtonText != null) {
    deleteButtonText = collData.deleteButtonText;
  }
  var addButtonText = "add a new element";
  if ( collData.addButtonText != null) {
    addButtonText = collData.addButtonText;
  }
  var editButtonText = "edit link";
  if ( collData.editItemButtonText != null) {
    editButtonText = collData.editItemButtonText;
  }

  // Setup the table header...
  if ( collData.items.length > 0) {
    tempHTML += '<table class="' + collData.type + '">';
    tempHTML +=   '<tr>';
    tempHTML +=     '<th class="leftAligned"><img src="\/atg\/images\/checkMark1.gif" alt="select items in this column" width="7" height="8" border="0" \/><\/th>';

    // Leave room for the up/down buttons if it's a list/array
    if ( collData.type == "list" || collData.type == "array") {
      tempHTML +=   '<th>&nbsp;<\/th>';
      tempHTML +=   '<th>&nbsp;<\/th>';
    }
	
    // Leave room for the key column if it's a map...
    if ( collData.type == "map") {
      tempHTML +=   '<th>Key<\/th>';
    }
	
    tempHTML +=     '<th>Value<\/th>';
    tempHTML +=   '<\/tr>';
  }
      
  // Draw the items...
  for (var x in collData.items) {
    tempHTML += '<tr>';

    // Draw the checkbox used to select items for deletion
    tempHTML += '<td class="checkBox">';
    tempHTML +=   '<input type="checkbox" id="collectionCB_' + collName + '_' + x + '" \/>';
    tempHTML += '<\/td>';

    // Draw the up/down buttons if its a list/array
    if ( (collData.hideUpDownButtons == null || collData.hideUpDownButtons != 'true') &&
         (collData.type == "list" || collData.type == "array")) {
      tempHTML += '<td>'
      tempHTML +=   '<input type="button" class="button" value="up" ';
      tempHTML +=          'onmousedown="collectionSwapItem(\'' + collName + '\',\'' + x + '\',\'' + (parseInt(x)-1) + '\')" />';
      tempHTML += '<\/td><td>';
      tempHTML +=   '<input type="button" class="button" value="down" ';
      tempHTML +=          'onmousedown="collectionSwapItem(\'' + collName + '\',\'' + x + '\',\'' + (parseInt(x)+1) + '\')" />';
      tempHTML += '<\/td>';
    }
	
    // Draw the key field, if it's a map...
    if ( collData.type == "map") {
      tempHTML += '<td>';
      tempHTML +=   '<input type="text" value="' + collData.items[x].key + '" id="collectionKey_' + collName + '_' + x + '" \/>';
      tempHTML += '<\/td>';
    }
	
    // Get the HTML for the value component from the registered function
    tempHTML += '<td>';
    var fieldId = collName + '_' + x;
    var textValue= collData.items[x].value;
	if(isEscaped(textValue) == true){
	tempHTML += eval( collData.fnGenHTML + "('" + fieldId + "','" + collData.items[x].value + "','" + escapeQuotes( collData.items[x].displayName ) + "','" + collData.items[x].assetURI + "','" + editButtonText + "')");
	}
	else{
	tempHTML += eval( collData.fnGenHTML + "('" + fieldId + "','" + escapeQuotes(collData.items[x].value) + "','" + escapeQuotes( collData.items[x].displayName ) + "','" + collData.items[x].assetURI + "','" + editButtonText + "')");
	}
	tempHTML += '<\/td>';
    tempHTML += '<\/tr>';
  }
  tempHTML += '<\/table>';
      
  // Draw the buttons at the bottom
  tempHTML += '<div class="mapButtons formPadding">';
  if ( collData.items.length > 0) {
    tempHTML +=  '<input type="button" class="button" value="' + deleteButtonText + '" ';
    tempHTML +=         'onmousedown="collectionDeleteItems( \'' + collName + '\')" />';
  }
  tempHTML +=  '<input type="button" class="button" value="' + addButtonText + '" ';
  tempHTML +=         'onmousedown="collectionDoAddItem( \'' + collName + '\', \'\', \'\', \'\')" />';
  tempHTML += '<\/div>';

  /*
   * Bug148419
   * We can't simply replace innerHTML here
   *   document.getElementById("collectionForm_" + collName).innerHTML = tempHTML;
   * due to the following IE bug
   *   http://support.microsoft.com/default.aspx/kb/927917
   * instead we need to update a copy of the node and replace the original with the updated copy.
   */
  var orginalNode = document.getElementById("collectionForm_" + collName);
  var clonedNode = orginalNode.cloneNode(true);
  var parentNode = orginalNode.parentNode;
  
  clonedNode.innerHTML = tempHTML;
  parentNode.replaceChild(clonedNode, orginalNode);
}

function collectionMakeHidden( collName) {
  // Render the hidden fields which will ultimately be pushed back to the server
  var collData = collectionData[collName];
  var tempHTML = '';
  var value = '';

  if ( collData.items.length == 0) {
    tempHTML += '<input type="hidden" value="null" name="' + collectionData[collName].nullPath + '" />';
    tempHTML += '<input type="hidden" value=" " name="_D:' + collectionData[collName].nullPath + '" />\n';
  } else {
    for (var x in collData.items) {
      if ( collData.type == "map") {
        value = collData.items[x].key + '=' + collData.items[x].value;
      } else {
        value = collData.items[x].value;
      }
      tempHTML += '<input type="hidden" id="collectionSubmit_' + collName + '_' + x + '" value="' + value + '" name="' + collectionData[collName].valuePath + '" />';
      tempHTML += '<input type="hidden" value=" " name="_D:' + collectionData[collName].valuePath + '" />\n';
    }
  }
	 
  /*
   * Bug148419
   * We can't simply replace innerHTML here
   *   document.getElementById("collectionHidden_" + collName).innerHTML = tempHTML;
   * due to the following IE bug
   *   http://support.microsoft.com/default.aspx/kb/927917
   * instead we need to update a copy of the node and replace the original with the updated copy.
   */
  var orginalNode = document.getElementById("collectionHidden_" + collName);
  var clonedNode = orginalNode.cloneNode(true);
  var parentNode = orginalNode.parentNode;
  
  clonedNode.innerHTML = tempHTML;
  parentNode.replaceChild(clonedNode, orginalNode);
}

function collectionDoAddItem( collName, key, value, displayName, assetURI) {
  collectionGrabValues( collName);
  collectionAddItem( collName, key, value, displayName, assetURI);
  collectionMakeHidden( collName);
  collectionDraw( collName);
}

function collectionDeleteItems( collName) {
  // Traverse all the checkboxes for the given Collection and remove any checked items
  var collData = collectionData[collName];
  var newList = new Array();

  // Get the latest field values, just in case...	 
  collectionGrabValues( collName);
	 
  for ( var x in collData.items) {
    fieldId = 'collectionCB_' + collName + '_' + x;
    if ( !document.getElementById(fieldId).checked) {
      newList.push( collData.items[x]);
    }
  }
  collData.items = newList;

  // Rebuild the form and redraw
  collectionMakeHidden( collName);
  collectionDraw( collName);
}

function collectionClear( collName) {
   // Clear all items from the given collection and redraw
   var collData = collectionData[collName];
   collData.items = new Array();
   collectionMakeHidden( collName);
   collectionDraw( collName);
}

function collectionSwapItem( collName, index1, index2) {
  // Swap the items at index1 and index2
  var collData = collectionData[collName];
  var collLen = collData.items.length;

  if ( index1 < 0 || index1 >= collLen || index2 < 0 || index2 >= collLen) {
    return;
  }

  collectionGrabValues( collName);
  var item1 = collData.items[index1];
  var item2 = collData.items[index2];
  collData.items[index1] = item2;
  collData.items[index2] = item1;
  collectionDraw( collName);
}

function collectionOnSubmit () {
  // It is registered as an onSubmit method for asset forms so that each Collection
  //   can do its pre-submit housekeeping.
  for (var x in collectionNames) {
    collectionGrabValues( collectionNames[x]);
    collectionMakeHidden( collectionNames[x]);
  }
}

// Register the collectionOnSubmit method
registerOnSubmit( collectionOnSubmit);

/** End of Collection support  -  BNB  **/



/**
 * This set of function provides support for asset form serialization/restoration
 *  It allows asset edit views to store away the state of an unsaved form for future reference
 *  in the event that we need to restore it.  For instance, if a form save fails, we want to show
 *  the edits that caused the save to fail.  There are other uses for this, as well, such as when
 *  navigating through item properties to other assets.
 **/

// START FORM SAVER FUNCTIONS
      
// Manage the to-save property list for the current asset view
fsPropertyList = new Array();
function fsRegisterProperty( propName) {
  fsPropertyList[propName] = 'true';
}

fsValuesPath = '';
function fsSetFHPath( path) {
  fsValuesPath = path;
}

// Call the "getter" method for this form item with the given property name
function fsGetPropertyValue( propName) {
  // Build the getter function name and call it, but only if the property is registered
  if ( !fsPropertyList[propName]) {
    return;
  }
  var getterName = 'get_' + propName + '()';
  return eval( getterName);
}

// Call the "setter" method for this form item with the given property name
function fsSetPropertyValue( propName, value) {
  // Build the setter function name and call it, but only if this property is registered
  if ( !fsPropertyList[propName]) {
    return;
  }
  var setterName = 'set_' + propName + '( value )';
  value = decodeEscapedString( value );
  return eval( setterName);
}

// Create the form for saving the current asset form settings
function fsFillForm( formName) {

  // Clear out the old DIV in the form with all the predefined bogus form fields
  replaceInnerHtml(document.getElementById( formName + 'Div'), "");

  // Get a reference to the form
  var theForm = document.getElementById( formName + 'Form');

// Add the fields to the form...
  for ( var propName in fsPropertyList) {

    // Get the current serialized value of the form property
    var propValue = fsGetPropertyValue( propName);

    // Create the form fields for this property
    var propKeyPath = fsValuesPath + '.fs_' + propName;
    var i = document.createElement('INPUT');
    i.type = 'hidden';
    i.name = propKeyPath;
    i.value = propValue;
    if(navigator.appName.indexOf("Microsoft")!=-1)
    { // IE
      theForm.insertBefore(i);
    }
    else
    {
      theForm.insertBefore(i,null);
    }

    i = document.createElement('INPUT');
    i.type = 'hidden';
    i.name = '_D:' + propKeyPath;
    i.value = ' ';
    if(navigator.appName.indexOf("Microsoft")!=-1)
    { // IE
      theForm.insertBefore(i);
    }
    else
    {
      theForm.insertBefore(i,null);
    }

    //tempHTML += '<input type="hidden" value="' + propValue + '" name="' + propKeyPath + '" />';
    //tempHTML += '<input type="hidden" value=" " name="_D:' + propKeyPath + '" />\n';
  }
}

// called to save current form values.
// Returns true if a submit of the parent page has been queued.
function fsOnSubmit( formName) {
  if ( formName != null) {
    fsFillForm( formName);
  } else {
    fsFillForm( "formSaver");
    var myForm = document.getElementById( "formSaverForm");
    myForm.target = 'formSaverIframe';
    myForm.submit();
    return true;
  }

  return false;
}

//  We're letting the various "save" methods do this now...
//    registerOnSubmit( fsOnSubmit);

    // END FORM SAVER FUNCTIONS



-->
