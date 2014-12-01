//************************************************************************************************************************
//
// atg-ui_common.js File
//
// (C) Copyright 1997-2009 Art Technology Group, Inc.
// All rights reserved.
//
//  This library is cross browser compatible
//
//************************************************************************************************************************


/* -------------------------*/ 
/* Self version info        */
/* -------------------------*/

var script_ui_common              = new Object();
script_ui_common.type             = "text/javascript";
script_ui_common.versionMajor     = 1;
script_ui_common.versionMinor     = 0;

// this can be used by other javascript file to determine if they need to import this file
/*    example  
 *
 *    if ( typeof script_ui_common != "object" ) 
 *         document.write("<scr"+"ipt type='text/javascript' src='/CAF/scripts/atg-ui_common.js'><"+"/"+"script>");
 *
*/



/* ------------------------- */
/* variable declaraton */
/* ------------------------- */
 
// ATGAct a collection of objects that represent actions
// an action is a single notion... openWindow , validat text , hide div, etc
ATGAct = new Object;
// ATGAg is the user agent of the requesting browser
ATGAg = window.navigator.userAgent;
// ATGBVers is the browser version of the rerequesting browser
ATGBVers = parseInt(ATGAg.charAt(ATGAg.indexOf("/")+1),10);
// flag used to bypass completion of action(s)
ATGStopExecution = false;

ATGUIDebugging                  = new Object();
ATGUIDebugging.debug            = false;
ATGUIDebugging.debugOut         = voidDebug;
ATGUIDebugging.debugLevelAdjust = voidDebug;
ATGUIDebugging.level            = 10;

function voidDebug(str){
  if ( parent && parent.ATGUIDebugging && parent.ATGUIDebugging != ATGUIDebugging ) parent.ATGUIDebugging.debugOut(str);
}

/* --------------------- */
/* browser detection */
/* --------------------- */
function Is () {
  // convert all characters to lowercase to simplify testing
  var agt=ATGAg.toLowerCase()
  // --- BROWSER VERSION ---
  this.major = stringToNumber(navigator.appVersion)
  this.minor = parseFloat(navigator.appVersion)
  this.nav  = ((agt.indexOf('mozilla')!=-1) && ((agt.indexOf('spoofer')== -1)
            && (agt.indexOf('compatible') == -1)))
  this.nav2 = (this.nav && (this.major == 2))
  this.nav3 = (this.nav && (this.major == 3))
  this.nav4 = (this.nav && (this.major == 4))
  //Netscape 6
  this.nav5 = (this.nav && (this.major == 5))
  this.nav6 = (this.nav && (this.major == 5))
  this.gecko = (this.nav && (this.major >= 5))
  this.ie   = (agt.indexOf("msie") != -1) 
  if ( this.ie ) {
      this.major = stringToNumber( agt.charAt( agt.indexOf("msie") + 5 ));
      this.ie3  = (this.ie && (this.major == 2))
      this.ie4  = (this.ie && (this.major == 3))
      this.ie5  = (this.ie && (this.major == 4))
  }
  this.opera = (agt.indexOf("opera") != -1)
  this.nav4up = this.nav && (this.major >= 4)
  this.ie4up  = this.ie  && (this.major >= 4)
  this.ie5up  = (this.ie && (this.major >= 5))
  this.ie6up  = (this.ie && (this.major >= 6))
}
var is = new Is();

/* ------------------------- */
/* body onload events */
/* ------------------------- */

// append to the 'list' of actions to be taken apon document.onLoad event
// string expected points to object reference in ACTAct.
function ATGAddToOnLoad(target) { 
  ATGAct['bodyOnLoad'][ATGAct['bodyOnLoad'].length] = new String (target);
}
// append to the 'list' of actions to be taken apon document.onUnload event
// string expected points to object reference in ACTAct.
function ATGAddToOnUnLoad(target) { 
  ATGAct['bodyOnUnLoad'][ATGAct['bodyOnUnLoad'].length] = new String (target);
}

// initialize common variables at document.onload
// to use in page add two line to script section at top of page
// ATGAct['ATGBodyInit'] = new Array (ATGBodyInit);
// ATGAddToOnLoad('ATGBodyInit');
function ATGBodyInit() {
  
  // to capture keystrokes , used often to prohibit enterKey form submissions
  if( ! IsIE() && (document.layers ) ) {
    document.captureEvents(Event.KEYPRESS);
    document.onkeypress = blockKeyStrokeNet;
  } else {
    window.onkeypress="return blockKeyStrokeIE(event.keyCode)";
  }
  // window and screen size variables
  var ww = ( IsIE() ) ? document.body.offsetWidth-20 : window.innerWidth-16 ;
  var wh = ( IsIE() ) ? document.body.offsetHeight-20 : window.innerHeight-16 ;
  var sw = ( IsIE() ) ? screen.width   : screen.availWidth ;
  var sh = ( IsIE() ) ? screen.height   : screen.availHeight ;
}
// function to determine if browser is Internet Explorer
function IsIE() {  return ( is.ie && is.major >= 5 ); }
// first of two function that process ATGAct objects
function ATGAction(array) { 
if (ATGUIDebugging.debug)ATGUIDebugging.debugOut(" Function ATGAction , arrary =  " +array , 10 );
return ATGAction2(ATGAct, array); }
// second of two function that process ATGAct objects
function ATGAction2(fct, array) { 
  if (ATGUIDebugging.debug)ATGUIDebugging.debugOut(" Function ATGAction2 ",10);
  var result;
  for (var i=0;i<array.length;i++) {
    if(ATGStopExecution) return false; 
    var actArray = fct[array[i]];
    if(actArray == null) return false; 
    var tempArray = new Array;
    for(var j=1;j<actArray.length;j++) {
       if (ATGUIDebugging.debug) 
           ATGUIDebugging.debugOut(" in ATGAction2 for each actArray value at index "+j+" = " +actArray[j],8);
      if((actArray[j] != null) && (typeof(actArray[j]) == "object") && (actArray[j].length == 2)) {
        if(actArray[j][0] == "VAR") {
          if (ATGUIDebugging.debug)
              ATGUIDebugging.debugOut(" in ATGAction2 value at index ["+j+"][0] = " +actArray[j][0],10);
          if (ATGUIDebugging.debug)
              ATGUIDebugging.debugOut(" in ATGAction2 value at index ["+j+"][1] = " +actArray[j][1],10);
	  tempArray[j-1] = actArray[j][1] ;
          if (ATGUIDebugging.debug)
              ATGUIDebugging.debugOut(" in ATGAction2 " +typeof(tempArray[j]) + "["+j+"] = " +tempArray[j],10);
        } else {
          if(actArray[j][0] == "ACT") {
             if (ATGUIDebugging.debug)
                 ATGUIDebugging.debugOut(" in ATGAction2/ACT recursive call to ATGAction" ,10);
	     tempArray[j] = ATGAction(new Array(new String(actArray[j][1])));
          } else {
            tempArray[j] = actArray[j];
            if (ATGUIDebugging.debug)
                ATGUIDebugging.debugOut(" in ATGAction2/ACT/else  tempArray "+j+" = " +actArray[j] ,10);
          }
        }
      } else {
          if (ATGUIDebugging.debug)
              ATGUIDebugging.debugOut("in ATGAction2 actArray[j] in major else  = " + actArray[j] ,10);
         tempArray[j] = actArray[j];
      }
    }
    if (ATGUIDebugging.debug)
       ATGUIDebugging.debugOut(" actArray[0](tempArray) = " +actArray[0] + "("+tempArray +")" ,10);
    /* this is what actually calls the function 
     */
    result = actArray[0](tempArray);
  }
  return result;
}
// ATGActionGroup is a way to consolidate multiple ATGAction into one referencable action
function ATGActionGroup (action) {
  for(var i=1; i<action.length ; i++) {
    //alert ( " in action group , executing item " + i +" of type " + typeof(action[i]) + " named " + action[i]);
     if ( typeof( action[i]) == "object" ) {
      ATGAction(new Array(action[i])); 
     }
  }
}
// a cross browser way to find elements 
// ly can be a multiDom object
function ATGFindElement(n,ly) {
  if (ATGBVers < 4) return document[n];
  var curDoc = ly ? ly.document : document; var elem = curDoc[n];
  if (!elem) { for (var i=0;i<curDoc.layers.length;i++) {
    elem = ATGFindElement(n,curDoc.layers[i]); if (elem) return elem; }
  }
  return elem;
}
// is the reference passed in a valid DOM object
function ATGisObj(s){
  if (IsIE()) {
    return ( document.getElementById(s) );
  } else if (ATGAg.indexOf("Opera 6") > 0 ) {
    return ATG6Styl(s);
  } else if (document.layers ) {
    return ATGNSStyl(s) ;
  } else {
   return ATG6Styl(s) ;
  }
  return false;
}


/* ------------------------- */
/* style related  */
/* position and visibility */
/* ------------------------- */
function getElt () {
    var name = getElt.arguments[getElt.arguments.length-1];
    var element = document.getElementById(name);
    return element;
}
function ATGIEStyl(s) { return document.all.tags("div")[s].style; }
function ATG6Styl(s)  { return document.getElementById(s);  }
function ATGNSStyl(s) { return ATGFindElement(s,0);   }
// uses the above three functions to assertain the correct reference to the DOM object
function ATGStyl(s) {
  if (IsIE()) {
    return document.all.tags("div")[s] ; //ATGIEStyl(s);
  } else if (ATGAg.indexOf("Opera 6") > 0   ) {
    return ATG6Styl(s);
  } else if (document.layers ) {   
    return ATGNSStyl(s);
  } else {
    return ATG6Styl(s).style;
  }
}
// this will toggle the visibilty of an div/layer object
// not to be confused with the display property
function ATGSetStyleVis(s,v) {
  //  alert(ATGAg +"\n"+document.layers +"\n"+ ATGBVers+"\n"+s+"\n"+v);
  //  alert( getElt(s) + "is here " );
   if (is.nav4) getElt(s).visibility =  (v == 0) ? "hidden" : "visible";
   else if (getElt(s).style) getElt(s).style.visibility =  (v == 0) ? "hidden" : "visible";
}
// if defined in css file this function toggles visibility
// not to be confused with the display property
function cssStyleSetVisibility(visibility) { 
  this.styleObj.visibility = visibility; 
}
// returns the current visibility state of dom object
// not to be confused with the display property
function ATGGetStyleVis(s) {
  if (is.nav4) {
    var value = getElt(s).visibility;
    if (value == "show") return 1;
      else if (value == "hide") return 0;
      else return value;
    } else if (getElt(s).style) {
      var value = getElt(s).style.visibility;
      if (value == "visible") return 1;
      if (value == "hidden") return 0;
      else return value;
    }
    return;
}
// primary action to begin toggle or set visibility of div or layer
// not to be confused with the display property
function ATGShowHide(action) {
  if (action[1] == '') return;
  if (! ATGisObj(action[1]) ) return;
  var type=action[2];
  if(type==0) ATGSetStyleVis(action[1],0);
  else if(type==1) ATGSetStyleVis(action[1],1);
  else if(type==2) { 
    if (ATGGetStyleVis(action[1]) == 0) ATGSetStyleVis(action[1],1);
    else ATGSetStyleVis(action[1],0);
  }
}
//primary action to begin toggle or set visibility of div or layer
//not to be confused with the display property
function ATGShowHideLayer(elementId,toggleValue) {
  if (elementId == '') {
    return;
  }
  if(!ATGisObj(elementId)) {
    return;
  }
  if(toggleValue == 0) {
    ATGSetStyleVis(elementId,0);
  } else if(toggleValue == 1) {
    ATGSetStyleVis(elementId,1);
  } else if(toggleValue == 2) { 
    if(ATGGetStyleVis(elementId) == 0) {
      ATGSetStyleVis(elementId,1);
    } else {
      ATGSetStyleVis(elementId,0);
    }
  }
}
// sets the absolute position of the DOM object relative to it's parent container
function ATGSetStylePos(s,d,p) {
  if (IsIE()) {
    if (d == 0) ATGIEStyl(s).posLeft = p; else ATGIEStyl(s).posTop = p; 
  } else if (document.layers ) {
    if (d == 0) ATGNSStyl(s).left = p; else ATGNSStyl(s).top = p; 
  } else {
    if (d == 0) ATG6Styl(s).style.left = p; else ATG6Styl(s).style.top = p; 
  }
}
// return the position of the DOM object relative to it's parent container
function ATGGetStylePos(s,d) {
  if (IsIE()) { if (d == 0) return ATGIEStyl(s).posLeft; else return ATGIEStyl(s).posTop; }
  else { if (d == 0) return ATGNSStyl(s).left; else return ATGNSStyl(s).top; }
}

// DISPLAY methods.
// All of these methods affect the display css style, mainly on divs. This is useful for collapsing/expanding
// inline divs among other things.

// Takes a toggle value and shows/hides an element by setting the css display property.
// Toggle values are 0 to hide, 1 to show and 2 to flip the current value.
// Hiding of elements occurs by setting display to "none". Default property for showing elements
// is "inline". This can be overriden by sending a third parameter to this method.
function ATGDisplayHide(element, toggleValue, showProperty) {
  if (!ATGisObj(element) ) return;

  if(toggleValue != 0 && toggleValue != 1 && toggleValue != 2) {
    toggleValue = 2;
  }
  
  if(showProperty != "inline" && 
      showProperty == "block" && 
      showProperty == "inline-block" && 
      showProperty == "compact" &&
      showProperty == "run-in" &&
      showProperty == "table" &&
      showProperty == "list-item") {
    showProperty = "inline";
  }
  
  if(toggleValue == 0 || (toggleValue == 2 && ATGGetStyleDisplay(element) != "none")) {
    ATGSetStyleDisplay(element, "none");
  } else if(toggleValue == 1 || (toggleValue == 2 && ATGGetStyleDisplay(element) == "none")) {
    ATGSetStyleDisplay(element, showProperty);
  }
}

// Returns the display style on an element.
function ATGGetStyleDisplay(element) {
  if (getElt(element).style) {
    return getElt(element).style.display;
  }
  return;
}

// Sets the display style on an element. If the selected style cannot be set (most likely because
// the style is not supported or non-existent then a value of true is returned indicating that an error
// was encountered.
function ATGSetStyleDisplay(element, display) {
  // display can be any string that can legitimately be set as a value on the "display" style for css.
  var error = 0;
  if (getElt(element).style) {
    try {
      getElt(element).style.display = display;
    } catch(e) {
      error = 1;
    }
  } else {
    // Can not access style collection on element.
    error = 2;
  }
  return error;
}

/* ------------------------- */
/* utilities (form elements) */
/* ------------------------- */
function validateCheckboxes(elementBaseName,refType,validateMode) {
  // baseName is the repeatable part of the check boxes name or id
  // normally a unique name is assigned to each checkbox, even if the bean name is used
  // dev people can use the varStatus in c:forEch loops to get the index and append it to the basename

  // refType is either "id" or "name" ( as in form.element.name ) 
  if ( refType == null ) refType = "id";

  // validateMode is ( "atLeastOne" , "allChecked" , "notAll" , "noneChecked" );
  if ( validateMode == null ) validateMode = "atLeastOne";
  var totalChecks = 0;
  var totalChecked = 0;
  returnVal = false; 
  for ( f = 0 ; f < document.forms.length ; f++ ) {
    formRef = document.forms[f] ;
    for ( e = 0 ; e < formRef.elements.length ; e++ ) {
      elemRef = formRef.elements[e];
      if  (  formRef.elements[e].type == "checkbox" ) {
        if ( refType.toLowerCase() == "id" ) {
          label = formRef.elements[e].id;
        } else {
          label = formRef.elements[e].name;
        }
        if ( label.indexOf( elementBaseName ) > -1 ) {
          totalChecks++;
          if ( elemRef.checked ) totalChecked++;
        } 
      }
    }
  }
  if ( validateMode.toLowerCase() == "atleastone" ) {
    return ( totalChecked > 0 && totalChecks > 0) ;
  }
  if ( validateMode.toLowerCase() == "allchecked" ) {
    return ( totalChecked == totalChecks && totalChecks > 0 ) ;
  }
  if ( validateMode.toLowerCase() == "notall" ) {
    return ( totalChecked < totalChecks && totalChecks > 0 ) ;
  }
  if ( validateMode.toLowerCase() == "nonechecked" ) {
    return ( totalChecks == 0 && totalChecks > 0 ) ;
  }
  return returnVal;
}

function selectAllCheckboxes(elementBaseName,refType,checkMode) {
  // baseName is the repeatable part of the check boxes name or id
  // normally a unique name is assigned to each checkbox, even if the bean name is used
  // dev people can use the varStatus in c:forEch loops to get the index and append it to the basename

  // refType is either "id" or "name" ( as in form.element.name ) 
  if ( refType == null ) refType = "id";

  // checkMode is ( false /* unchecked */ ||  true /* checked */ );
  if ( checkMode == null ||  checkMode != "true" ||  checkMode != "false" ) checkMode = true ;

  for ( f = 0 ; f < document.forms.length ; f++ ) {
    formRef = document.forms[f] ;
    for ( e = 0 ; e < formRef.elements.length ; e++ ) {
      elemRef = formRef.elements[e];
      if ( refType.toLowerCase() == "id" ) {
        label = formRef.elements[e].id;
      } else {
        label = formRef.elements[e].name;
      }
      if ( label.indexOf( elementBaseName ) > -1 ) {
  if  (  formRef.elements[e].type == "checkbox" )
          elemRef.checked = checkMode;
      } 
    }
  }
  return ;
}
// to add a (1)name/value option to a select field
function selectAddOption( selectId , pName , pValue ) {
  var selector = document.getElementById(selectId);
  if ( selector && selector.options) {
    if ( selector.options.length > 0 ) {
      for ( i=0 ; i < selector.options.length ;i++) {
        if ( selector.options[i].value == pValue ) return;
      }
    }
    selector.options[selector.options.length] = new Option (unEscapeXML(pName) , pValue , false , false ) ;  
  }
}
// selects all options of a select field
function selectAll(selectId) {
  var selector = document.getElementById(selectId);
  if ( selector && selector.options) {
    if ( selector.options.length > 0 ) {
      for ( i=0 ; i < selector.options.length ;i++) {
        selector.options[i].selected = true;
      }
      return true;
    } else {
      return false
    }
  } 
  return false; 
}
// removes option(s) from a select field. 
// the values of the options to be passed are contained in a comma delimted list
function selectRemove(selectId , commaDelimList) {
  var ids = commaDelimList.split(",");
  var selectionObj = document.getElementById(selectId);
  for ( j = 0 ; j < ids.length ; j++ ) {
    if ( selectionObj ) {
      if ( selectionObj.options.length > 0 ) {
        for ( i = selectionObj.options.length ; i > -1; i--) { 
          if ( selectionObj.options[i] ) {
            if (selectionObj.options[i].value == ids[j] ) selectionObj.options[i] = null; 
          }
        }
      }
    }
  }
}
// remove all option from a select field
function selectRemoveAll(selectId){
  var selectionObj = document.getElementById(selectId);
  if ( selectionObj ) {
    if ( selectionObj.options.length > 0 ) {
      for ( i = selectionObj.options.length ; i > -1; i--) { selectionObj.options[i] = null; }
    }
  }
}
// move an option from one select feild to another select field
// options to be moved are selected in the origin feild
// if none are slected , none are moved
function selectMoveToSelect(srcSelectId,destSelectId){
  var selectorSrc = document.getElementById(srcSelectId);
  var selectorDest = document.getElementById(destSelectId);
  if ( selectorSrc && selectorDest ) {
    if ( selectorSrc.options.length > 0 ) {
      for ( outerLoop=0 ; outerLoop < selectorSrc.options.length ;outerLoop++ ) {
        if( selectorSrc.options[outerLoop].selected == true ) {
          if ( ! selectFieldContains( destSelectId , selectorSrc.options[outerLoop].value ) ) {
            selectAddOption( destSelectId /* select field ID */ ,
            selectorSrc.options[outerLoop].text /* name */ ,
            selectorSrc.options[outerLoop].value/* id */ );
          }
        }   
      }
    }
  }
}
// removes options that are selected within a select feild
function removeSelectedItems(selectId) { 
  var selector = document.getElementById(selectId);
  var tempArray = new Array();
  if ( selector ) {
    if ( selector.options.length > 0 ) {
      count = 0 ;
      for ( i = 0 ; i < selector.options.length ;i++) {
        if ( selector.options[i].selected ) {
          tempArray[count++] = selector.options[i].value;
        }
      }
      if ( tempArray.length == 0 )return ;  
      for ( j = 0 ; j < tempArray.length ; j++ ) {
        for ( i = 0 ; i < selector.options.length ; i++ ) {
          if ( selector.options[i].value == tempArray[j] ) {
            selector.options[i] = null;
          }
        }
      }
    }
  }
}
// check to see if a select field contains an option with given value
function selectFieldContains( selectId , value ) {
  var selector = document.getElementById(selectId);
  if ( selector ) {
    if ( selector.options.length > 0 ) {
      for ( i=0 ; i < selector.options.length ;i++) {
        if ( selector.options[i].value == value ) return true;
      }
    }
  } 
  return false;  
}
// arranges options within a select feild , moving option on index space at a time
function selectMoveInSelect(selectId,dir) {
  var selector = document.getElementById(selectId);
  var selected = selector.selectedIndex;
  if ( selector.options.length < 1 || selected == -1 ) return; // If list contains 1 or less OR none selected
 
  // There's more than one in the list, rearrange the list order
  if ( selected == 0    && dir == "up" ) return;
  if ( selected == selector.options.length-1 && dir == "down" ) return;

  newSelected = ( dir == "up" ) ? selected-1 : selected+1 ;  
  // Get the text/value of the one directly above the hightlighted entry as
  // well as the highlighted entry; then flip them
  var moveText1 = selector.options[newSelected].text;
  var moveText2 = selector.options[selected].text;
  var moveValue1 = selector.options[newSelected].value;
  var moveValue2 = selector.options[selected].value;
  selector.options[selected].text = moveText1;
  selector.options[selected].value = moveValue1;
  selector.options[newSelected].text = moveText2;
  selector.options[newSelected].value = moveValue2;
  selector.options.selectedIndex = newSelected; // Select the one that was selected before
}

/**
* Updates a named input in the form with the given name in the current
* document to the new value specified.
**/
function setFormElement(formName,inputName,value) {
  var form = document.forms[formName];
  if(form == null) return false;
  var input = form.elements[inputName];
  if(input == null) return false;
  //alert('in setFormElement(' + formName + ', ' + inputName + ', ' + value + ')');
  input.value=value;
  return true;
}
 
/**
* Updates a form action with the given value.
**/
function setFormAction(formName, action) {
  var form = document.forms[formName];
  if(form == null) return false;
  //alert('in setFormAction(' + formName + ', ' + action + ')');
  form.action = action;
  return true;
}
 
/**
* Submits a form with the given name in the current document
**/
function submitForm(formName) {
  var form = document.forms[formName];
  if(form == null) return false;
  //alert('in submitForm(' + formName + ')');
  form.submit();
  return true;
}

/* ------------------------- */
/* utilities strings  */
/* ------------------------- */


function unEscapeXML(str) {
/* will convert &amp; --> &
   &lt; --> < 
   &gt; --> > 
   &#034; --> " 
   &#039; --> ' 
   this will conter the effect of the c:out tags' escapedXML values
*/
  str = str.replace(/\&#034;/g,"\"");
  str = str.replace(/\&#039;/g,"'");
  str = str.replace(/\&lt;/g ,"<");
  str = str.replace(/\&gt;/g ,">");
  str = str.replace(/\&amp;/g ,"&");
  return str;
}

function escapeXML(str) {
/* will convert &amp; --> &
   &lt; --> < 
   &gt; --> > 
   &#034; --> " 
   &#039; --> ' 
   this will conter the effect of the c:out tags' escapedXML values
*/
  str = str.replace(/\"/g  ,"&#034;");
  str = str.replace(/"'"/g ,"&#039;");
  str = str.replace(/"<"/g ,"&lt;"  );
  str = str.replace(/">"/g ,"&gt;"  );
  str = str.replace(/"&"/g ,"&amp;" );
  return str;
}




/* A version of stringToNumber that returns 0 if there is NaN returned, 
 * or number is part of a string, like 100px... 
*/
function stringToNumber(s)
{
 return parseInt(('0' + s), 10)
}

function blockKeyStrokeIE(keyCode) {  if( keyCode == "13" ) { return false;} else {  return true; } }
function blockKeyStrokeNet(e) { if( e.which == 13 ) {return false;} else { return true; }}


/* ------------------------- */
/* utilities window  */
/* ------------------------- */

/* to open a dialog window in the middle of the screen 
 *
*/
function ATG_centeredDialog( url , winName , dialogWidth , dialogHeight, dialogParams) {
  dialogParams = (dialogParams == null) ? "" : "," + dialogParams;
  xpos = (sw - dialogWidth)/2;
  ypos = (sh - dialogHeight)/2;
  dialogParams = "left="+xpos+
                  ",top="+ypos+
                  ",width="+dialogWidth+
                  ",height="+dialogHeight+
                  dialogParams;
  ATG_openWin(url, winName, dialogWidth, dialogHeight, dialogParams);
}
function ATG_dialog( url, winName,  dialogWidth , dialogHeight, dialogParams ){
  // default/init values 
  if ( dialogParams == "" ) 
    dialogParams =  "status=no,toolbar=no,menubar=no,resizable=no,scrollbars=yes,titlebar=no";
  if ( dialogWidth  == "" ) dialogWidth ="680";
  if ( dialogHeight == "" ) dialogHeight="500";
  if ( winName == "" )  winName = "atgDialog";

  ATG_openWin (url, winName , dialogWidth, dialogHeight , dialogParams );
}
function ATG_openWin ( url , winName , dialogWidth , dialogHeight, dialogParams) {
  winRef =  window.open( url, winName ,"width="+dialogWidth+",height="+dialogHeight+","+ dialogParams );
  if ( winRef !=  null ) winRef.focus();
}


/* ------------------------- */
/* action collections */
/* ------------------------- */

/* out of the box collections as part of the body onLoad and OnUnload events
 * they are initialized here as action groups 
 * various parts of thepage can append to these groups action they would like to have happen on load or
 * on unLoad

   ... sample ...
        // page specific functionality
        function pageInit(){
          ATGShowHide(new Array ( '','LOGOUT'  , 0 ) );
          ATGShowHide(new Array ( '','USERNAME', 1 ) );
        }
        // add reference to this function to the ATGAct Object
        ATGAct['pageInit'] =  new Array(pageInit);
        // register key/string to the group of actions to be taken at bodys' onload
        ATGAddToOnLoad('pageInit');
 *
 */
ATGAct['bodyOnLoad'] = new Array(ATGActionGroup);
ATGAct['bodyOnUnLoad'] = new Array(ATGActionGroup);
// This method controls showing or hiding the help tip.
function showHelpTip(e, sHtml, bHideSelects) {
  // find anchor element
  var el = e.target || e.srcElement;
  while (el.tagName != "A")
    el = el.parentNode;

  // is there already a tooltip? If so, remove it
  if (el._helpTip) {
    helpTipHandler.hideHelpTip(el);
  }

  helpTipHandler.hideSelects = Boolean(bHideSelects);

  // create element and insert last into the body
  helpTipHandler.createHelpTip(el, sHtml);

  // position tooltip
  helpTipHandler.positionToolTip(e);

  // add a listener to the blur event.
  // When blurred remove tooltip and restore anchor
  el.onblur = helpTipHandler.anchorBlur;
  el.onkeydown = helpTipHandler.anchorKeyDown;
}

// Object containing the rest of the methods and state for managing help tips.
var helpTipHandler = {

  // param that enables hiding ALL selects on the page since on most browsers
  // they "show through" divs
  hideSelects: false,

  // html content of help tip
  helpTip: null,

  // method to show selects after a help tip is hidden
  showSelects:
    function (bVisible) {
      if (!this.hideSelects) return;
      // only IE actually do something in here
      var selects = [];
      if (document.all)
        selects = document.all.tags("SELECT");
      var l = selects.length;
      for (var i=0; i<l; i++)
        selects[i].runtimeStyle.visibility = bVisible ? "" : "hidden";
    },

  // constructor
  create:
    function () {
      var d = document.createElement("DIV");
      d.className = "help-tooltip";
      d.onmousedown = this.helpTipMouseDown;
      d.onmouseup = this.helpTipMouseUp;
      document.body.appendChild(d);
      this.helpTip = d;
    },

  // wrapper for constructor; creates or sets helpTip property
  createHelpTip:
    function (el, sHtml) {
      if (this.helpTip == null) {
        this.create();
      }
      var d = this.helpTip;
      d.innerHTML = sHtml;
      d._boundAnchor = el;
      el._helpTip = d;
      return d;
    },

  // Allow clicks on A elements inside help tip
  helpTipMouseDown:
    function (e) {
      var d = this;
      var el = d._boundAnchor;
      if (!e) e = event;
      var t = e.target || e.srcElement;
      while (t.tagName != "A" && t != d)
        t = t.parentNode;
      if (t == d) return;
      el._onblur = el.onblur;
      el.onblur = null;
    },
  
  // handles the mouse up event
  helpTipMouseUp:        
    function () {
      var d = this;
      var el = d._boundAnchor;
      el.onblur = el._onblur;
      el._onblur = null;
      el.focus();
    },

  // handles the blur event
  anchorBlur:
    function (e) {
      var el = this;
      helpTipHandler.hideHelpTip(el);
    },

  // handles the key down event
  anchorKeyDown:
    function (e) {
      if (!e) e = window.event
        if (e.keyCode == 27) {        // ESC
          helpTipHandler.hideHelpTip(this);
        }
    },

  // Removes text from help tip
  removeHelpTip:
    function (d) {
      d._boundAnchor = null;
      d.style.filter = "none";
      d.innerHTML = "";
      d.onmousedown = null;
      d.onmouseup = null;
      d.parentNode.removeChild(d);
      //d.style.display = "none";
    },

  // hides the help tip.
  hideHelpTip:
    function (el) {
      var d = el._helpTip;
      /*        Mozilla (1.2+) starts a selection session when moved
                and this destroys the mouse events until reloaded
                d.style.top = -el.offsetHeight - 100 + "px";
      */
      d.style.visibility = "hidden";
      //d._boundAnchor = null;
      el.onblur = null;
      el._onblur = null;
      el._helpTip = null;
      el.onkeydown = null;

      this.showSelects(true);
    },

  // Sets the position of the help tip
  positionToolTip:
    function (e) {
      this.showSelects(false);
      var scroll = this.getScroll();
      var d = this.helpTip;
        
      // width
      if (d.offsetWidth >= scroll.width)
        d.style.width = scroll.width - 10 + "px";
      else
        d.style.width = "";

      // left
      if (e.clientX > scroll.width - d.offsetWidth)
        d.style.left = scroll.width - d.offsetWidth + scroll.left + "px";
      else
        d.style.left = e.clientX - 2 + scroll.left + "px";

      // top
      if (e.clientY + d.offsetHeight + 18 < scroll.height)
        d.style.top = e.clientY + 18 + scroll.top + "px";
      else if (e.clientY - d.offsetHeight > 0)
        d.style.top = e.clientY + scroll.top - d.offsetHeight + "px";
      else
        d.style.top = scroll.top + 5 + "px";

      d.style.visibility = "visible";
    },

  // returns the scroll left and top for the browser viewport.
  getScroll:
    function () {
      if (document.all && typeof document.body.scrollTop != "undefined") {        // IE model
        var ieBox = document.compatMode != "CSS1Compat";
        var cont = ieBox ? document.body : document.documentElement;
        return {
          left: cont.scrollLeft,
          top: cont.scrollTop,
          width: cont.clientWidth,
          height: cont.clientHeight
        };
      } else {
        return {
          left: window.pageXOffset,
          top: window.pageYOffset,
          width: window.innerWidth,
          height: window.innerHeight
        };
      }
    }
};
// -------------------------------------------------------------------------------------
// Static validation objects
// -------------------------------------------------------------------------------------

/**
  * Primitive and Composite validation functions:
  * atgValidation_validateRequiredField(testString)
  * atgValidation_validateMinMaxLength(testString, minLength, maxLength)
  * atgValidation_validateNoSpecialCharacters(testString)
  * atgValidation_validateAlphaCharacters(testString)
  * atgValidation_validateAlphaNumericCharacters(testString)
  * atgValidation_validateNumericCharacters(testNumber)
  * atgValidation_validateNumericRange(testNumber, lowRange, hiRange)
  * atgValidation_validateEmailAddress(testString)
  * atgValidation_validateURL(testString)
  * atgValidation_validateDate(testString)
  */

// Static final object for constants
constants = {
  generalErrorMessageId: "page_message",
  alertMessageSeparator: "\n",
  generalMessageSeparator: "<br/>",
  inlineMessageSeparator: ", ",
  defaultSubstitutionToken: "###"
}

validationCode = {
  MESSAGE_GENERAL:"There were errors with your submission. The following fields need to be corrected:",
  MESSAGE_SPECIFIC:"",
  SUCCESS:"Validation successful",
  ERROR_GENERAL:"General validation error",
  ERROR_NULL_STRING: "You must enter a value in this field",
  ERROR_EMPTY_STRING: "You must enter a value in this field",
  ERROR_MIN_LENGTH: "You must enter a value that contains at least " + constants.defaultSubstitutionToken + " characters",
  ERROR_MAX_LENGTH: "You must enter a value that contains no more than " + constants.defaultSubstitutionToken + " characters",
  ERROR_SPECIAL_CHARACTERS: "Please do not enter special characters in this field",
  ERROR_ALPHA_CHARACTERS: "Please enter only alphabetic characters in this field",
  ERROR_ALPHA_NUMERIC_CHARACTERS: "Please enter only alphanumeric characters in this field",
  
  ERROR_NUMERIC_CHARACTERS: "This field may only contain numeric characters",
  ERROR_NUMERIC_RANGE_LOW: "You must enter a number that is greater or equal to " + constants.defaultSubstitutionToken,
  ERROR_NUMERIC_RANGE_HIGH: "You must enter a number that is less than or equal to " + constants.defaultSubstitutionToken,
  
  ERROR_EMAIL_ADDRESS_FORMAT: "The e-mail address is not in the correct format. Please try again. E-mail addresses must be formatted as name@domainname.extension",
  ERROR_URL_FORMAT: "The URL is not in the correct format. Please try again and using  www.domain.extension or http://www.domainname.extension as the format",
  ERROR_DATE_FORMAT: "Please check that your date is in the format " + constants.defaultSubstitutionToken,
  ERROR_DATE_RANGE_DAY: "Please check your day value and enter a number from 1 to 31",
  ERROR_DATE_RANGE_MONTH: "Please check your month value and enter a number from 1 to 12",
  ERROR_DATE_RANGE_YEAR: "Please check your year value and enter a number between 1900 and 2050",
  ERROR_DATE_DAYS_IN_MONTH: "Please check your date and enter a day value that occurs in the selected month"
}

// This object is required to keep track of any validation objects created on the screen
// Its contents should be initialized and hidden at the start of every validation
currentValidationObjectArray= new Array();

// Define the global success variable that will be used to determine whether the validation function succeeds or
// fails overall
var isSuccess = true;


// -------------------------------------------------------------------------------------
// Primitive validation functions
// -------------------------------------------------------------------------------------

// Validates a required field and returns a validation code. The optional allowWhitespace
// attribute (when set to false) will ensure that whitespace prevents successful validation
function atgValidation_validateRequiredField(testString, allowWhitespace) {
  if (testString == null)
    return validationCode.ERROR_NULL_STRING;
    
  if (testString.length == 0)
    return validationCode.ERROR_EMPTY_STRING;

  if ((allowWhitespace != null) && (allowWhitespace == false)) {
    // Test for the presence of whitespace
    var onlyWhitespace = true;
    for (charCount = 0; charCount < testString.length; charCount++) {   
        charLetter = testString.charAt(charCount);
        if (charLetter != " ") {
        	onlyWhitespace = false;
        }
    }
    
    if (onlyWhitespace == true)
      return validationCode.ERROR_EMPTY_STRING;
  }
  
  return validationCode.SUCCESS;
}

// Validates a required field and returns a validation code
function atgValidation_validateMinMaxLength(testString, minLength, maxLength) {
  if (testString == null)
    return validationCode.ERROR_NULL_STRING;
    
  
  if (minLength != null) {
    if (testString.length < minLength)
      return atgCommon_substituteToken(validationCode.ERROR_MIN_LENGTH, minLength, constants.defaultSubstitutionToken);
  }
  
  if (maxLength != null) { 
    if (testString.length > maxLength)
      return atgCommon_substituteToken(validationCode.ERROR_MAX_LENGTH, maxLength, constants.defaultSubstitutionToken);
  }
  
   return validationCode.SUCCESS;
}

// Validates a string for the presence of any special characters
function atgValidation_validateNoSpecialCharacters(testString) {
  var invalidCharacters = "!@#$%^&*()+=-[]\\\';,./{}|\":<>?";
  for (charCount = 0; charCount < testString.length; charCount++) {   
      charLetter = testString.charAt(charCount);
      if (invalidCharacters.indexOf(charLetter,0) != -1) {
      	return validationCode.ERROR_SPECIAL_CHARACTERS;
      }
  }
  
  return validationCode.SUCCESS;
}

// Validates a string to ensure that only alphabetic characters are specified
function atgValidation_validateAlphaCharacters(testString) {
  var alphaCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ";
  for (charCount = 0; charCount < testString.length; charCount++) {   
      charLetter = testString.charAt(charCount);
      if (alphaCharacters.indexOf(charLetter,0) == -1) {
      	return validationCode.ERROR_ALPHA_CHARACTERS;
      }
  }
  
  return validationCode.SUCCESS;
}

// Validates a string to ensure that only alphanumeric characters are specified
function atgValidation_validateAlphaNumericCharacters(testString) {
  var alphaNumericCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ";
  for (charCount = 0; charCount < testString.length; charCount++) {   
      charLetter = testString.charAt(charCount);
      if (alphaNumericCharacters.indexOf(charLetter,0) == -1) {
      	return validationCode.ERROR_ALPHA_NUMERIC_CHARACTERS;
      }
  }
  
  return validationCode.SUCCESS;
}

// Validates a string to ensure that only numeric characters are specified
function atgValidation_validateNumericCharacters(testNumber) {
  for (charCount = 0; charCount < testNumber.length; charCount++) {   
      charLetter = testNumber.charAt(charCount);
      if (((charLetter < "0") || (charLetter > "9"))) {
      	return validationCode.ERROR_NUMERIC_CHARACTERS;
      }
  }
  
  return validationCode.SUCCESS;
}

// Validates an integer to ensure that it is within the specified range
function atgValidation_validateNumericRange(testNumber, lowRange, hiRange) {
  if (lowRange != null) {
    if (testNumber < lowRange) {
      return atgCommon_substituteToken(validationCode.ERROR_NUMERIC_RANGE_LOW, lowRange, constants.defaultSubstitutionToken);
      
    }
  }
  
  if (hiRange != null) {
    if (testNumber > hiRange) {
      return atgCommon_substituteToken(validationCode.ERROR_NUMERIC_RANGE_HIGH, hiRange, constants.defaultSubstitutionToken);
    }
  }
    
  return validationCode.SUCCESS;
}

// -------------------------------------------------------------------------------------
// Composite validation functions
// -------------------------------------------------------------------------------------

// Validates an e-mail address by ensuring that an @ symbol and a . are specified. This
// function will eventually be moved into a regular expression to better match the input
function atgValidation_validateEmailAddress(testString) {
  // If the field is null or empty, do not proceed with validation. If this is a required field,
  // the required field validator should be used in conjunction with this validator
  if ((testString == null) || (testString == ""))
    return validationCode.SUCCESS;
  
  var invalidCharacters = "!#$%^&*()+=[]\\\';,/{}|\":<>?";
  for (charCount = 0; charCount < testString.length; charCount++) {   
      charLetter = testString.charAt(charCount);
      if (invalidCharacters.indexOf(charLetter,0) != -1) {
      	return validationCode.ERROR_EMAIL_ADDRESS_FORMAT;
      }
  }

  // Test that there is at least one '@' symbol and one or more '.' symbols - and make sure that the last '.' 
  // symbol is after the '@' symbol
  if ((testString.indexOf("@") < 0) || (testString.indexOf(".") < 2) || (testString.indexOf("@") > testString.lastIndexOf(".")))
    return validationCode.ERROR_EMAIL_ADDRESS_FORMAT;
   
  // Test that there is only one '@' symbol in the string
  if (testString.indexOf("@") != testString.lastIndexOf("@"))
    return validationCode.ERROR_EMAIL_ADDRESS_FORMAT;
  
  return validationCode.SUCCESS;
}

// Validates a URL by ensuring that an no special characters (other than those permitted by URLs)
// are used. In addition the function ensures that the a . is always specified. This
// function will eventually be moved into a regular expression to better match the input
function atgValidation_validateURL(testString) {
  // If the field is null or empty, do not proceed with validation. If this is a required field,
  // the required field validator should be used in conjunction with this validator
  if ((testString == null) || (testString == ""))
    return validationCode.SUCCESS;
  
  var invalidCharacters = "!#$^*()+[]\\\',{}|\"<>";
  for (charCount = 0; charCount < testString.length; charCount++) {   
      charLetter = testString.charAt(charCount);
      if (invalidCharacters.indexOf(charLetter,0) != -1) {
      	return validationCode.ERROR_URL_FORMAT;
      }
  }

  return validationCode.SUCCESS;
}

// Validates the String to ensure that it is a correctly formatted date
function atgValidation_validateDate(testString, dateFormat, optionalDateInputId, returnUpdatedDate) {
  // If the field is null or empty, do not proceed with validation. If this is a required field,
  // the required field validator should be used in conjunction with this validator
  if ((testString == null) || (testString == ""))
    return validationCode.SUCCESS;

  var defaultDateFormat = "MM/DD/YY";
  // Reformat the date to conform to the i18n date format
  if ((dateFormat == null) || (dateFormat == "")) {
    dateFormat = defaultDateFormat;
  }

  var i18nDateArray = atgCalendar_getI18nDateArrayFromDateString(testString, dateFormat);
  if (i18nDateArray == null)
    return atgCommon_substituteToken(validationCode.ERROR_DATE_FORMAT, dateFormat, constants.defaultSubstitutionToken);
  
  var theMonth = i18nDateArray[0];
	var theDay = i18nDateArray[1];
	var theYear = i18nDateArray[2];

	// Numeric character validation
	var numericValidationMonth = atgValidation_validateNumericCharacters(theMonth);
	var numericValidationDay = atgValidation_validateNumericCharacters(theDay);
	var numericValidationYear = atgValidation_validateNumericCharacters(theYear);

	if ((numericValidationMonth != validationCode.SUCCESS) || (numericValidationDay != validationCode.SUCCESS)
	  || (numericValidationYear != validationCode.SUCCESS))
	  return validationCode.ERROR_NUMERIC_CHARACTERS;
	
  // Explicitly convert any numeric strings to integers
	theMonth = new Number(theMonth);
	theDay = new Number(theDay);
	theYear = new Number(theYear);
	
	// Range checking validation
	var rangeValidationMonth = atgValidation_validateNumericRange(theMonth, 1, 12);
	var rangeValidationDay = atgValidation_validateNumericRange(theDay, 1, 31);
	var rangeValidationYear = atgValidation_validateNumericRange(theYear, 1900, 2050);
	
	if (rangeValidationMonth != validationCode.SUCCESS)
	  return validationCode.ERROR_DATE_RANGE_MONTH;
	  
	if (rangeValidationDay != validationCode.SUCCESS)
	  return validationCode.ERROR_DATE_RANGE_DAY;
	
	if (rangeValidationYear != validationCode.SUCCESS)
	  return validationCode.ERROR_DATE_RANGE_YEAR;
	
  // Range check days of month with respect to current month
  var maxDaysInMonth = atgCalendar_getDaysInMonth(theMonth-1,theYear);
	if (theDay > maxDaysInMonth)
	  return validationCode.ERROR_DATE_DAYS_IN_MONTH;
 
  // If this point has been reached, the validation is successful
  // Since atgCalendar_getI18nDateArrayFromDateString() generates array of date values
  // to fit MM/DD/YY format we should pass correct date format string.  
  var newDateString = atgCalendar_getI18nDateString((theMonth + "/" + theDay + "/" + theYear), defaultDateFormat);
  
  // Update the content of the date field with the updated date
  if (optionalDateInputId != null) {
    dateInputObject = atgCalendar_findElementById(optionalDateInputId);
    if (dateInputObject != null)
      dateInputObject.value = atgCalendar_getI18nDateString(newDateString, defaultDateFormat);
  }

  // Determine whether to return the validation code or the validation object
  if (returnUpdatedDate == true) {
    var validationObject = new Object();
    validationObject.isValidDate = validationCode.SUCCESS;
    validationObject.openDate = newDateString;
    return validationObject;
  }
  else 
    return validationCode.SUCCESS;
}


// -------------------------------------------------------------------------------------
// Page validation functions
// -------------------------------------------------------------------------------------

// Initializes the validation array by setting the contents of all message objects to empty strings
// and hiding them if they are visible
function atgValidation_initValidationObjects() {
  
  // Reinitialize the success variable
  isSuccess = true;
  
  for (var i = 0; i < currentValidationObjectArray.length; i++) {
    var validationId = currentValidationObjectArray[i];
    
    var validationObject = document.getElementById(validationId);
    if (validationObject != null) {
      validationObject.innerHTML = "";
      validationObject.style.display="none";
    }
  }  
  
  // After cleaning up the array, initialize it to an empty array
  currentValidationObjectArray = new Array();
  
  // Set the default Substitution Token
  if (validationCode.DEFAULT_SUBSTITUTION_TOKEN != null)
    constants.defaultSubstitutionToken = validationCode.DEFAULT_SUBSTITUTION_TOKEN;
}


// -------------------------------------------------------------------------------------
// Error reporting functions
// -------------------------------------------------------------------------------------

// Tests the validationResult and reports any errors if necessary
function atgValidation_testValidationResult(fieldId, fieldName, validationResult) {
  if (validationResult != validationCode.SUCCESS) {
    atgValidation_showError(fieldId, fieldName, validationResult);
    isSuccess = false;
  }
}

// Displays an error message above the label of the respective field
function atgValidation_showError(fieldId, fieldName, errorMessage) {
  var fieldMessageObject = document.getElementById(fieldId + "_message");
  var generalMessageObject = document.getElementById(constants.generalErrorMessageId);

  var separatorType = constants.inlineMessageSeparator;
  if ((fieldMessageObject == null) && (generalMessageObject == null))
      separatorType = constants.alertMessageSeparator;
  
  var generalErrorMessage = validationCode.MESSAGE_SPECIFIC + " <b>" + fieldName + "</b>";
  var fieldErrorMessage = validationResult;
  var combinedErrorMessage = generalErrorMessage + separatorType + fieldErrorMessage;
    
  /*
   Error decision logic:
   Ideally, the field name will be written to the general message object, while the 
   error message will be written to the field message object. If the general message object is absent,
   only the error message will be written to the field message object. If the error message object is
   absent, both messages will be written to the general message object. If both objects are absent, 
   the combined message is displayed as an alert.
  */
  if ((fieldMessageObject != null) && (generalMessageObject != null)) {
    atgValidation_setGeneralErrorMessage(generalMessageObject);
    atgValidation_appendErrorMessage(generalMessageObject, generalErrorMessage, separatorType);
    atgValidation_appendErrorMessage(fieldMessageObject, fieldErrorMessage, separatorType);
    currentValidationObjectArray.push(generalMessageObject.id);
    currentValidationObjectArray.push(fieldMessageObject.id);
  }
  else if (generalMessageObject != null) {
    atgValidation_setGeneralErrorMessage(generalMessageObject);
    atgValidation_appendErrorMessage(generalMessageObject, combinedErrorMessage, separatorType);
    currentValidationObjectArray.push(generalMessageObject.id);
  }
  else if (fieldMessageObject != null) {
    atgValidation_appendErrorMessage(fieldMessageObject, fieldErrorMessage, separatorType);
    currentValidationObjectArray.push(fieldMessageObject.id);
  }
  else {
    alert(combinedErrorMessage);
  }
}

// Appends the error message to the DOM object and makes that object visible if hidden
function atgValidation_appendErrorMessage(domObject, errorMessage, separatorType) {
  var messageContents = "";
  if (domObject.innerHTML != "") {
    messageContents = domObject.innerHTML;
    
    var testLength = validationCode.MESSAGE_GENERAL + constants.generalMessageSeparator;
    if (messageContents.length != testLength.length-1) {
      messageContents += separatorType;
    }
  }

  domObject.innerHTML = messageContents + errorMessage;
  if (domObject.style.display == "none")
    domObject.style.display = "block";
}

// Sets the general message area with the validationCode.MESSAGE_GENERAL string if it is empty
function atgValidation_setGeneralErrorMessage(domObject) {
  if (domObject.innerHTML == "") {
    domObject.innerHTML = validationCode.MESSAGE_GENERAL + constants.generalMessageSeparator;
  }
}

// -------------------------------------------------------------------------------------
// General functions
// -------------------------------------------------------------------------------------

// Escapes single and double quotes to allow the string concatenation to successfully validate without
// abnormally terminating the validation string
function atgValidation_escapeQuotes(testValue) {
  var regExp = /\'/g;
  testValue = testValue.replace(regExp, "\\\'");

  regExp = /\"/g;
  testValue = testValue.replace(regExp, "\\\"");

  regExp = /\r/g;
  testValue = testValue.replace(regExp, "\\r");
  
  regExp = /\n/g;
  testValue = testValue.replace(regExp, "\\n");

  return testValue;
}

// Substitutes an array of tokens within a String to return the new String
function atgCommon_substituteTokenArray(originalString, substitutionStringArray, substitutionTokenArray) {
  var newString = originalString;
  
  // Loop through the array and update the String accordingly
  for (var i=0; i< substitutionTokenArray.length; i++) {
    newString = atgCommon_substituteToken(newString, substitutionStringArray[i], substitutionTokenArray[i]);
  }
  
  return newString;
}

// Substitutes a token within a String to return the new String
function atgCommon_substituteToken(originalString, substitutionString, substitutionToken) {
  var newString = null;
  var regExp = eval("/"+substitutionToken+"/gi");
  newString = originalString.replace(regExp, substitutionString);
  return newString;
}

// Returns the DOM object for an element specified by its id
function atgCommon_findElementById(elementId) {
  return document.getElementById(elementId); 
}
// -------------------------------------------------------------------------------------
// Calendar date calculations
// -------------------------------------------------------------------------------------

// Returns the day of the week for the first day of a given month
function atgCalendar_getFirstDayOfWeek(newDate) 
{
	var newYear = newDate.getFullYear();
	var newMonth = newDate.getMonth();
	var firstDay = new Date(newYear,newMonth,1);
	return firstDay.getDay();
}

// Returns the day of the week for the last day of a given month
function atgCalendar_getLastDayOfWeek(newDate) 
{
	var newYear = newDate.getFullYear();
	var newMonth = newDate.getMonth();
	var numDays = atgCalendar_getDaysInMonth(newMonth, newYear);
	var lastDay = new Date(newYear,newMonth,numDays);
	return lastDay.getDay();
}

// Returns the day of the month from a given date object
function atgCalendar_getSelectedDay(newDate)
{
	return newDate.getDate();
}

// Returns the name of the month from a given date object
function atgCalendar_getSelectedMonth(newDate)
{
	var newMonth = newDate.getMonth();
	return calendarI18n.monthNames[newMonth];
}

// Returns the four-digit year from a given date object
function atgCalendar_getSelectedYear(newDate)
{
	return newDate.getFullYear();
}

// Returns the name of the day of the week given a day of the month integer
function atgCalendar_getDayOfWeek(dayNum)
{
	return calendarI18n.dayNames[dayNum];
}

// Returns the number of days in a month for a given date object
function atgCalendar_getDaysInMonth(theMonth,theYear)
{
	// Compensate for the month array starting at zero
	theMonth ++;
	
	if (theMonth == 2)
	{
		// Test for leap year
		if (((theYear % 4 == 0) && (theYear % 100 != 0)) || (theYear % 400 == 0)) {
			daysInMonth[1] = 29;
		}
		else
			daysInMonth[1] = 28;
	}

	return daysInMonth[theMonth-1];
}

// Returns a date object representing the day after a given date object
function atgCalendar_getNextDay(newDate)
{
	newDate.setDate(newDate.getDate()+1);
	return newDate;
}

// Returns a date object representing the day before a given date object
function atgCalendar_getPreviousDay(newDate)
{
	newDate.setDate(newDate.getDate()-1);
	return newDate;
}

// Returns a serialized date String of the form MM/DD/YYYY for the day after a given String date
function atgCalendar_getNextDayAsString(newDateString, i18nSep)
{
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
	var newDate = new Date(newDateString);
	newDate.setDate(newDate.getDate()+1);
	var newDateString = newDate.getMonth() + 1 + splitChar + newDate.getDate() + splitChar + newDate.getFullYear();
	return newDateString;
}

// Returns a serialized date String of the form MM/DD/YYYY for the day before a given String date
function atgCalendar_getPreviousDayAsString(newDateString, i18nSep)
{
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
	var newDate = new Date(newDateString);
	newDate.setDate(newDate.getDate()-1);
	var newDateString = newDate.getMonth() + 1 + splitChar + newDate.getDate() + SplitChar + newDate.getFullYear();
	return newDateString;
}

// Returns a short form of a specified date
function atgCalendar_formatShortDate(theDate, i18nSep)
{
	if (theDate == "")
		return "";

  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
	return (theDate.getMonth() + 1) + splitChar + theDate.getDate() + splitChar + theDate.getFullYear();
}

// Returns the long form of a specified date
function atgCalendar_formatLongDate(theDate)
{
	if (theDate == "")
		return "";
		
	var weekday = atgCalendar_getDayOfWeek(theDate.getDay());
	var monthName = atgCalendar_getSelectedMonth(theDate);
	return weekday + ", " + monthName + " " + theDate.getDate() + ", " + theDate.getFullYear();
}

// Formats a four-digit year representation of the current date
function atgCalendar_formatYear(yearString)
{
	theYear = new Number(yearString);
	if (isNaN(yearString))
	  return yearString;
	
	if ((theYear > 20) && (theYear < 100)) {
		theYear = 1900 + new Number(theYear);
	}
	else if ((theYear > 0) && (theYear < 21)) {
		theYear = 2000 + new Number(theYear);
	}
		
	return theYear;
}

// Given an i18n format and an i18n date string, this function returns a
// correctly-formatted date object
function atgCalendar_getI18nDateObject(i18nDateString, i18nFormat, i18nSep) {
  var i18nDateArray = atgCalendar_getI18nDateArrayFromDateString(i18nDateString, i18nFormat, i18nSep);
  if (i18nDateArray == null)
    return null;
  
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
  var dateObject = new Date(i18nDateArray[0] + splitChar + i18nDateArray[1] + splitChar + i18nDateArray[2]);
  return dateObject;
}

// Given an i18n format and an i18n date string, this function returns a 
// correctly-formatted date string
function atgCalendar_getI18nDateString(i18nDateString, i18nFormat, i18nSep) {
  var i18nDateArray = atgCalendar_getI18nDateArrayFromDateString(i18nDateString, i18nFormat, i18nSep);
  if (i18nDateArray == null)
    return null;
  
  var splitChar = "/";
  if (i18nSep != null) splitChar = i18nSep;
  var serializedDate = i18nDateArray[0] + splitChar + i18nDateArray[1] + splitChar + i18nDateArray[2];
  return serializedDate;
}


// Given an i18n format and an i18n date object, this function returns a 
// correctly-formatted date string
function atgCalendar_getI18nDateStringFromObject(i18nDateObject, i18nFormat, i18nSep) {
  var i18nDateArray = atgCalendar_getI18nDateArrayFromDateObject(i18nDateObject, i18nFormat);
  if (i18nDateArray == null)
    return null;
  
  var splitChar;
  if (i18nSep == null){
    if (i18nFormat.indexOf("/") != -1)
      splitChar = "/";
    else if (i18nFormat.indexOf(".") != -1)
      splitChar = ".";
    // we don't know how to format this date, so return null.
    else return null;
  } else splitChar = i18nSep;

  var serializedDate = i18nDateArray[0] + splitChar + i18nDateArray[1] + splitChar + i18nDateArray[2];
  return serializedDate;
}


// Given an i18n format and an i18n date string, this function returns 
// an array of date values
function atgCalendar_getI18nDateArrayFromDateString(i18nDateString, i18nFormat, i18nSep) {
  var splitChar;
  if (i18nSep == null){
    if (i18nFormat.indexOf("/") != -1)
      splitChar = "/";
    else if (i18nFormat.indexOf(".") != -1)
      splitChar = ".";
    // we don't know how to format this date, so return null.
    else return null;
  } else splitChar = i18nSep;

  var formatArray = i18nFormat.split(splitChar);
  var dateArray = i18nDateString.split(splitChar);
  var i18nDateArray = new Array(-1,-1,-1);
  
  if ((formatArray.length < 3) || (dateArray.length < 3))
    return null;
  
  // Calculate the position of the date format
  for (var i=0; i< formatArray.length; i++) {
    if ((formatArray[i].indexOf("M") != -1) || (formatArray[i].indexOf("m") != -1)) {
      i18nDateArray[0] = dateArray[i];
    }
    else if ((formatArray[i].indexOf("D") != -1) || (formatArray[i].indexOf("d") != -1)) {
      i18nDateArray[1] = dateArray[i];
    }
    else if ((formatArray[i].indexOf("Y") != -1) || (formatArray[i].indexOf("y") != -1)) {
      i18nDateArray[2] = dateArray[i];
    }
  }
  
  var yearReference = i18nDateArray[2];
  i18nDateArray[2] = atgCalendar_formatYear(i18nDateArray[2]);
  
  return i18nDateArray;
}


// Given an i18n format and an i18n date object, this function returns 
// an array of date values
function atgCalendar_getI18nDateArrayFromDateObject(i18nDateObject, i18nFormat, i18nSep) {
  var splitChar;
  if (i18nSep == null){
    if (i18nFormat.indexOf("/") != -1)
      splitChar = "/";
    else if (i18nFormat.indexOf(".") != -1)
      splitChar = ".";
    // we don't know how to format this date, so return null.
    else return null;
  } else splitChar = i18nSep;

  var formatArray = i18nFormat.split(splitChar);
  var i18nDateArray = new Array(-1,-1,-1);
  
  if ((formatArray.length < 3) || (i18nDateObject == null))
    return null;
  
  // Calculate the position of the date format
  for (var i=0; i< formatArray.length; i++) {
    if ((formatArray[i].indexOf("M") != -1) || (formatArray[i].indexOf("m") != -1)) {
      i18nDateArray[i] = i18nDateObject.getMonth()+1;
    }
    else if ((formatArray[i].indexOf("D") != -1) || (formatArray[i].indexOf("d") != -1)) {
      i18nDateArray[i] = i18nDateObject.getDate();
    }
    else if ((formatArray[i].indexOf("Y") != -1) || (formatArray[i].indexOf("y") != -1)) {
      i18nDateArray[i] = i18nDateObject.getFullYear();
    }
  } 
  
  return i18nDateArray;
}

// -------------------------------------------------------------------------------------
// Global definitions
// -------------------------------------------------------------------------------------
var daysInMonth = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
var calendarInstance = null;
var today = new Date();
var restoreDateClassName = "";
var restoreDateId = "";
var calendarStyles = null;
var calendarI18n = null;

// -------------------------------------------------------------------------------------
// Calendar object functions
// -------------------------------------------------------------------------------------

// Associates a single calendar instance with multiple anchors, given that the calendar instance is
// residing inside an iFrame
function atgCalendar_associateCalendarIFrame(newAnchorId, newDateInputId, calendarInstanceId) {
  //TODO: 
}

// Associates a single calendar instance with multiple anchors, by dynamically switching the date input id
// and anchor id at run time
function atgCalendar_associateCalendar(newAnchorId, newDateInputId, calendarInstanceId) {
  eval(calendarInstanceId).anchorId = newAnchorId;
  eval(calendarInstanceId).dateInputId = newDateInputId;
  atgCalendar_openCalendar(eval(calendarInstanceId));
}

// Calls the openCalendar function, but providing the id of the iFrame so that the objects may be
// correctly initialized. The name of the calendar instance object is assumed to be unchanged in the iFrame
function atgCalendar_openCalendarIFrame(iFrameId) {
  var iFrameObject = atgCalendar_findElementById(iFrameId);
  if (iFrameObject != null) {
    iFrameObject.contentWindow.atgCalendar_openIFrame(iFrameId);
  }
}

// Opens the calendar by accepting allowing a user-specified date to override the openDate.
// If neither dates are specified, today's date is selected.
function atgCalendar_openCalendar(calendarInstanceInput) {
  calendarInstance = calendarInstanceInput;
	calendarStyles = eval(calendarInstance.stylesId);
	calendarI18n = eval(calendarInstance.i18nId);
	
	var openDate = calendarInstance.openDate;
	var dateInputObject = null;
	if (calendarInstance.dateInputId != null)
		dateInputObject = atgCalendar_findElementById(calendarInstance.dateInputId);

	// The date input will always override the open date attribute
	if ((dateInputObject != null) && (dateInputObject.value != ""))
		openDate = dateInputObject.value;

  if ((openDate == null) || (openDate == "null")) {
    calendarInstance.selectedDate = new Date();
    
    if (calendarInstance.setDateOnCalendarOpen == true) {
  	  atgCalendar_setSelectedDate(calendarInstance.selectedDate);  
  	}

    calendarInstance.displayDate = new Date(calendarInstance.selectedDate);
  	atgCalendar_changeDate(calendarInstance);
  	atgCalendar_showCalendar();
  }
  else {
    var returnUpdatedDate = true;
    var validationResult = atgValidation_validateDate(openDate, calendarI18n.dateFormat, calendarInstance.dateInputId, returnUpdatedDate);
    var isValid = false;
    var dateObject = null;
    
    // Test the response for an Object or boolean value
    if (validationResult.isValidDate == null) {
      if (validationResult == validationCode.SUCCESS) {
        isValid = true;
        dateObject = new Date(openDate);
      }
    }
    else if (validationResult.isValidDate == validationCode.SUCCESS) {
      dateObject = new Date(validationResult.openDate);
      if (validationResult.openDate != openDate) {
        dateInputObject.value = validationResult.openDate;
      }
      isValid = true;
    }
    
    if (isValid) {
      calendarInstance.selectedDate = dateObject;
   	  atgCalendar_setSelectedDate(calendarInstance.selectedDate);  
      calendarInstance.displayDate = new Date(calendarInstance.selectedDate);
      atgCalendar_changeDate(calendarInstance);
    	atgCalendar_showCalendar();
    }
    else {
      calendarInstance.selectedDate = new Date();
      calendarInstance.displayDate = new Date(calendarInstance.selectedDate);
      
      if (calendarInstance.clearDateOnError == true) {
        dateInputObject.value = "";
        atgCalendar_changeDate(calendarInstance);
        atgCalendar_showCalendar();
      }
      else {
        atgCalendar_alert(validationResult);
      }
    }
  }
}	

// Sets the current date back to the input control
function atgCalendar_setSelectedDate(optionalDateObject) {
	var dateInputObject = atgCalendar_findElementById(calendarInstance.dateInputId);
	if (dateInputObject != null) {
	  var i18nFormat = calendarI18n.dateFormat;
	  var dateObject = calendarInstance.selectedDate;
	  
	  if (optionalDateObject != null)
	    dateObject = optionalDateObject;
	  
	  dateInputObject.value = atgCalendar_getI18nDateStringFromObject(dateObject, i18nFormat);
    if (dateInputObject.fireEvent) {
      dateInputObject.fireEvent("onchange");
    }
    else {
      var ev = document.createEvent("Events");
      ev.initEvent("change", true, true);
      dateInputObject.dispatchEvent(ev);
    }
	}
	
	return;
}

// Changes the date in the calendar
function atgCalendar_changeDate(calendarInstance) {
	var theDate = calendarInstance.displayDate;
	if ((theDate == null) || (theDate == ""))
		theDate = new Date();

  var containerObject = atgCalendar_findElementById(calendarInstance.containerId);
  var nodeType = containerObject.tagName;

  if (nodeType == "DIV")
    containerObject.innerHTML = atgCalendar_getCalendarHTML(calendarInstance);
  else {
    var iframeObject = containerObject.contentWindow.document;
    var iframeBodyObject = iframeObject.body;
    iframeBodyObject.innerHTML = atgCalendar_getCalendarHTML(calendarInstance);
  }
	
	if ((theDate.getFullYear() != today.getFullYear())
		||(theDate.getMonth() != today.getMonth())
		||(theDate.getDate() != today.getDate()))
	{
		atgCalendar_highlightDate(theDate.getDate());
	}
}

// Displays the previous month in the calendar
function atgCalendar_showPreviousMonth(calendarInstance) {
	var displayDate = calendarInstance.displayDate;
	if (displayDate == null)
		displayDate = calendarInstance.selectedDate;
	
	var currentMonth = displayDate.getMonth();
	var currentYear = displayDate.getFullYear();
	
	if (currentMonth == 1)
	{
		currentMonth = 12;
		currentYear --;
		displayDate.setFullYear(currentYear);
	}
	else
		currentMonth --;
	
	var daysInNewMonth = atgCalendar_getDaysInMonth(currentMonth,currentYear);
	if (displayDate.getDate() > daysInNewMonth) {
	  displayDate.setDate(daysInNewMonth);
	}
	
	displayDate.setMonth(currentMonth);
	calendarInstance.displayDate = displayDate;
	
	atgCalendar_findElementById(calendarInstance.containerId).innerHTML = atgCalendar_getCalendarHTML(calendarInstance);
	atgCalendar_resizeIFrameContainer();
	atgCalendar_highlightSelectedDate();
}

// Displays the next month in the calendar
function atgCalendar_showNextMonth() {
	var displayDate = calendarInstance.displayDate;
	if (displayDate == null)
		displayDate = calendarInstance.selectedDate;
	
	var currentMonth = displayDate.getMonth();
	var currentYear = displayDate.getFullYear();
	
	if (currentMonth == 12)
	{
		currentMonth = 1;
		currentYear ++;
		displayDate.setFullYear(currentYear);
	}
	else
		currentMonth ++;
	
	var daysInNewMonth = atgCalendar_getDaysInMonth(currentMonth,currentYear);
	if (displayDate.getDate() > daysInNewMonth) {
	  displayDate.setDate(daysInNewMonth);
	}
	
	displayDate.setMonth(currentMonth);
	calendarInstance.displayDate = displayDate;

	atgCalendar_findElementById(calendarInstance.containerId).innerHTML = atgCalendar_getCalendarHTML(calendarInstance);
	atgCalendar_resizeIFrameContainer();
	atgCalendar_highlightSelectedDate();
}

// Sets a new date in the calendar
function atgCalendar_setNewDate(newDate) {
	calendarInstance.selectedDate = newDate;
	calendarInstance.displayDate = new Date(calendarInstance.selectedDate);
	atgCalendar_changeDate(calendarInstance);
	
	if (calendarInstance.closeAfterSelection)
		atgCalendar_hideCalendar();
}

// Sets a specified date for the current month in the calendar
function atgCalendar_setDateInMonth(dateInMonth) {
	atgCalendar_highlightDate(dateInMonth);
		
	if (calendarInstance.closeAfterSelection)
		atgCalendar_hideCalendar();
}

// Highlights a specified date in the calendar
function atgCalendar_highlightDate(theDate) {
	if ((restoreDateId != "") && (restoreDateClassName != ""))
	{
		var restoreObject = atgCalendar_findElementById(restoreDateId);
		if (restoreObject != null)
		{
			if ((calendarInstance.displayDate.getFullYear() == calendarInstance.selectedDate.getFullYear()) &&
				(calendarInstance.displayDate.getMonth() == calendarInstance.selectedDate.getMonth()))
			{
				restoreObject.className = restoreDateClassName;
			}
		}
	}
	restoreDateId = calendarInstance.calendarInstanceId + "_" + theDate;

	calendarInstance.displayDate.setDate(theDate);
	calendarInstance.selectedDate = new Date(calendarInstance.displayDate);

	var dateObject = atgCalendar_findElementById(restoreDateId);
	
	if (dateObject != null)
	{
		restoreDateClassName = dateObject.className;
		dateObject.className = calendarStyles.NumericsSelected;
	}
	
}

// Highlights the currently-selected date
function atgCalendar_highlightSelectedDate() {
	if ((calendarInstance.displayDate.getFullYear() == calendarInstance.selectedDate.getFullYear()) &&
		(calendarInstance.displayDate.getMonth() == calendarInstance.selectedDate.getMonth()))
	{
		var dateObject = atgCalendar_findElementById(restoreDateId);
		if (dateObject != null)
			dateObject.className = calendarStyles.NumericsSelected;
	}
}

// Positions and displays the calendar
function atgCalendar_showCalendar() {
	if ((calendarInstance.curtainId != null) && (calendarInstance.curtainId != ""))
	{
		var curtainObject = atgCalendar_findElementById(calendarInstance.curtainId);
		if (curtainObject != null) {
			
			var windowWidth = document.body.offsetWidth;
    	var windowHeight = document.body.offsetHeight;
    	var scrollWidth = document.body.scrollWidth;
    	var scrollHeight = document.body.scrollHeight;
    	
    	if (calendarInstance.iFrameId != null) {
    	  windowWidth = parent.document.body.offsetWidth;
    	  windowHeight = parent.document.body.offsetHeight;
    	  scrollWidth = parent.document.body.scrollWidth;
    	  scrollHeight = parent.document.body.scrollHeight;
    	}
			
			atgCalendar_debug("Curtain object before: Width="+curtainObject.style.width+", Height="+curtainObject.style.height);
			
			if (scrollWidth > windowWidth)
			  curtainObject.style.width = scrollWidth;
			else
			  curtainObject.style.width = windowWidth;
			  
      if (scrollHeight > windowHeight)
			  curtainObject.style.height = scrollHeight;
			else
			  curtainObject.style.height = windowHeight;
			
			atgCalendar_debug("Curtain object after: Width="+curtainObject.style.width+", Height="+curtainObject.style.height);
			curtainObject.style.display = "block";
		}
	}
	
	var anchorObject = atgCalendar_findElementById(calendarInstance.anchorId);
	var originalCalendarObject = atgCalendar_findElementById(calendarInstance.containerId);
  var calendarObject = atgCalendar_findElementById(calendarInstance.containerId);
  
  var isIFrame = false;
  if (calendarInstance.iFrameId != null) {
    calendarObject.style.display = "block";
    isIFrame = true;
	  calendarObject = atgCalendar_findElementById(calendarInstance.iFrameId);
	  calendarObject.style.display="block";
	}
	
	var domLeft = getOffsetLeft(calendarInstance.anchorId);
	var domTop = getOffsetTop(calendarInstance.anchorId);
	var domWidth = calendarObject.offsetWidth;
	var domHeight = calendarObject.offsetHeight;
	
	atgCalendar_debug("Calendar object: Width="+domWidth+", Height="+domHeight);
	atgCalendar_debug("Ideal positioning object: L="+domLeft+", R="+(domLeft+domWidth)+", T="+domTop+", B="+(domTop+domHeight));
	
	var horizontalOffset = calendarInstance.horizontalOffset;
	var verticalOffset = calendarInstance.verticalOffset;
	
	if (horizontalOffset == null)
		horizontalOffset = 0;
		
	if (verticalOffset == null)
		verticalOffset = 0;
	
	if ((calendarInstance.displayRightOfAnchor != null) && (calendarInstance.displayRightOfAnchor == false)) {
		calendarObject.style.left = (domLeft - domWidth) + "px";
	}
	else {
		calendarObject.style.left = (domLeft + new Number(horizontalOffset)) + "px";
	}
		
	if ((calendarInstance.displayBelowAnchor != null) && (calendarInstance.displayBelowAnchor == false)) {
		calendarObject.style.top = (domTop - domHeight) + "px";
	}
	else {
		calendarObject.style.top = (domTop + new Number(verticalOffset)) + "px";
	}

	// Test for overlap on the screen. If there is overlap, and the autoFlip attribute is set to true,
	// flip the calendar around one or both axes
	if (calendarInstance.autoFlip == true) {
		var positionObject = {
  			isIFrame: isIFrame,
  			left: parseInt(calendarObject.style.left),
  			top: parseInt(calendarObject.style.top),
  			width: domWidth,
  			height: domHeight
  		}

		atgCalendar_debug("Position object: L="+positionObject.left+", R="+(positionObject.left+positionObject.width)+", T="+positionObject.top+", B="+(positionObject.top+positionObject.height));
		var overlapObject = atgCalendar_getOverlapObject(positionObject);
		atgCalendar_debug("Overlap object: L="+overlapObject.leftOverlap+", R="+overlapObject.rightOverlap+", T="+overlapObject.topOverlap+", B="+overlapObject.bottomOverlap);

	  if (overlapObject.leftOverlap > 0) 
			calendarObject.style.left = (domLeft + new Number(horizontalOffset)) + "px";
		else if (overlapObject.rightOverlap > 0)
			calendarObject.style.left = (domLeft - domWidth) + "px";
		
		if (overlapObject.topOverlap > 0) 
			calendarObject.style.top = (domTop + new Number(verticalOffset)) + "px";
		else if (overlapObject.bottomOverlap > 0)
			calendarObject.style.top = (domTop - domHeight) + "px";
	}

	if (isIFrame == true) {
  	atgCalendar_resizeIFrameContainer();
  	originalCalendarObject.style.display = "block";
  }
  else {
    calendarObject.className = calendarStyles.CalendarDivShow;
	  calendarObject.style.display = "block";
  }
}

// Hides the calendar
function atgCalendar_hideCalendar() {
	if (calendarStyles != null)
		atgCalendar_findElementById(calendarInstance.containerId).className = calendarStyles.CalendarDivHide;
	atgCalendar_findElementById(calendarInstance.containerId).style.display="none";
	
	if ((calendarInstance.curtainId != null) && (calendarInstance.curtainId != ""))
	{
		var curtainObject = atgCalendar_findElementById(calendarInstance.curtainId);
		if (curtainObject != null)
			curtainObject.style.display = "none";
	}
	
	if (calendarInstance.iFrameId != null) {
    var calendarIFrame = atgCalendar_findElementById(calendarInstance.iFrameId);
    calendarIFrame.style.display = "none";
  }
}

// Resizes the iFrame container based on the size of the Div container
function atgCalendar_resizeIFrameContainer() {
  if (calendarInstance.iFrameId != null) {
	  var calendarObject = atgCalendar_findElementById(calendarInstance.containerId);
	  var domWidth = calendarObject.offsetWidth;
	  var domHeight = calendarObject.offsetHeight;
	  
	  calendarIFrame = atgCalendar_findElementById(calendarInstance.iFrameId);
	  calendarIFrame.width = domWidth;
	  calendarIFrame.height = domHeight;
	 }
}

// Returns true if the calendar is current visible
function atgCalendar_isCalendarVisible() {
	var calendarObject = atgCalendar_findElementById(calendarInstance.containerId);
	if (calendarObject == null)
		return false;
	
	if (calendarObject.style.display == "block")
		return true;
	else
		return false;
}

// Returns an object describing the vertical and horizontal overlap of the object with respect
// to the dimensions of the DOM
function atgCalendar_getOverlapObject(positionObject) {
	var overlapObject = {
		leftOverlap: 0,
		rightOverlap: 0,
		topOverlap: 0,
		bottomOverlap: 0
	}
	
	var right = (new Number(positionObject.left)) + (new Number(positionObject.width));
	var bottom = (new Number(positionObject.top)) + (new Number(positionObject.height));
	var windowWidth = null;
	var windowHeight = null;
	
	if (positionObject.isIFrame) {
	  windowWidth = parent.document.body.offsetWidth;
	  windowHeight = parent.document.body.offsetHeight;
	}
	else {
	  windowWidth = document.body.offsetWidth;
	  windowHeight = document.body.offsetHeight;
	}
	
	var testWidth = self.innerWidth;
	var testHeight = self.innerHeight;
	
	if (testWidth > windowWidth) 
	  windowWidth = testWidth;
	if (testHeight > windowHeight) 
	  windowHeight = testHeight;
	
	atgCalendar_debug("Window width = " + windowWidth + ", Window height = " + windowHeight);
	
	overlapObject.leftOverlap = 0 - positionObject.left;
	overlapObject.rightOverlap = right - windowWidth;
	overlapObject.topOverlap = 0 - positionObject.top;
	overlapObject.bottomOverlap = bottom - windowHeight;

	return overlapObject;
}

// Returns the true offsetLeft position of an anchor
function getOffsetLeft(anchorId) {
	var anchorObject = atgCalendar_findElementById(anchorId);
	var curleft = 0;
	if (anchorObject.offsetParent)
	{
		while (anchorObject.offsetParent)
		{
			curleft += anchorObject.offsetLeft
			anchorObject = anchorObject.offsetParent;
		}
	}
	else if (anchorObject.x)
		curleft += anchorObject.x;
	return curleft;
}

// Returns the true offsetTop position of an anchor
function getOffsetTop(anchorId) {
	var anchorObject = atgCalendar_findElementById(anchorId);
	var curtop = 0;
	if (anchorObject.offsetParent)
	{
		while (anchorObject.offsetParent)
		{
			curtop += anchorObject.offsetTop
			anchorObject = anchorObject.offsetParent;
		}
	}
	else if (anchorObject.y)
		curtop += anchorObject.y;
	return curtop;
}

// Returns the element given the id by looking in the current DOM or any ancestor DOMs
function atgCalendar_findElementById(elementId) {
  var domObject = document.getElementById(elementId);
  if (domObject == null) {
    domObject = parent.document.getElementById(elementId);
  }
  
  return domObject;
}

// Simple alert debugging
function atgCalendar_debug(debugString) {
  //alert(debugString);
}

// Simple alert function
function atgCalendar_alert(alertString) {
  alert(alertString);
}
// -------------------------------------------------------------------------------------
// Calendar HTML generation functions
// -------------------------------------------------------------------------------------

// Calls the calendar generation sub-functions
function atgCalendar_getCalendarHTML(calendarInstance)
{
	var calendarHTML = "";
	
	calendarHTML += _atgCalendar_generateCalendarHeader(calendarInstance);
	calendarHTML += _atgCalendar_generateCalendarMonth(calendarInstance);
	calendarHTML += _atgCalendar_generateCalendarBody(calendarInstance);
	calendarHTML += _atgCalendar_generateCalendarFooter(calendarInstance);
	
	return calendarHTML;
}

// Generates the calendar header
function _atgCalendar_generateCalendarHeader(calendarInstance)
{
	var calendarHeader = "";
	
	calendarHeader += "<table class=\""+calendarStyles.Container+"\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\n";
	calendarHeader += "<tr>\n";
	calendarHeader += "<td class=\""+calendarStyles.BorderTopLeft+"\"></td>\n";
	calendarHeader += "<td class=\""+calendarStyles.BorderTopMiddle+"\"></td>\n";
	calendarHeader += "<td class=\""+calendarStyles.BorderTopRight+"\"></td>\n";
	calendarHeader += "</tr>\n";
		
	return calendarHeader;
}

// Generates the calendar month and year labels
function _atgCalendar_generateCalendarMonth(calendarInstance)
{
	var newDate = calendarInstance.displayDate;
	var calendarMonth = "";
	
	calendarMonth += "<tr>\n";
	calendarMonth += "<td nowrap=\"nowrap\" class=\""+calendarStyles.Left+"\"></td>\n";
	calendarMonth += "<td class=\""+calendarStyles.Middle+"\">\n";
	calendarMonth += "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
	calendarMonth += "<tr>\n";
	calendarMonth += "<td colspan=\"7\" class=\""+calendarStyles.MonthContainer+"\">\n";
	calendarMonth += "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
	calendarMonth += "<tr>\n";
	calendarMonth += "<td class=\""+calendarStyles.PreviousMonthRow+"\" nowrap=\"nowrap\"><span class=\""+calendarStyles.IconArrowLeft+"\" title=\"previous month\" onclick=\"atgCalendar_showPreviousMonth(calendarInstance)\">&nbsp;&nbsp;&nbsp;</span></td>\n";
	calendarMonth += "<td class=\""+calendarStyles.MonthRow+"\" nowrap=\"nowrap\"><span id=\""+calendarInstance.calendarInstanceId+"_currentMonth\" class=\""+calendarStyles.CurrentMonth+"\">"
	calendarMonth += atgCalendar_getSelectedMonth(newDate);
	calendarMonth += " ";
	calendarMonth += atgCalendar_getSelectedYear(newDate)+"\n";
	calendarMonth += "<td class=\""+calendarStyles.NextMonthRow+"\" nowrap=\"nowrap\"><span class=\""+calendarStyles.IconArrowRight+"\" title=\"next month\" onclick=\"atgCalendar_showNextMonth(calendarInstance)\">&nbsp;&nbsp;&nbsp;</span></td>\n";
	calendarMonth += "</tr>\n";
	calendarMonth += "</table>\n";
	calendarMonth += "</td>\n";
	calendarMonth += "</tr>\n";
	calendarMonth += "<tr>\n";
	calendarMonth += "<td align=\"center\">\n";
	calendarMonth += "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
	calendarMonth += "<tr>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabelsWeekend+"\">"+calendarI18n.daySymbols[0]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabels+"\">"+calendarI18n.daySymbols[1]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabels+"\">"+calendarI18n.daySymbols[2]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabels+"\">"+calendarI18n.daySymbols[3]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabels+"\">"+calendarI18n.daySymbols[4]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabels+"\">"+calendarI18n.daySymbols[5]+"</td>\n";
	calendarMonth += "<td class=\""+calendarStyles.DateLabelsWeekend+"\">"+calendarI18n.daySymbols[6]+"</td>\n";
	calendarMonth += "</tr>\n";
	calendarMonth += "<tr>\n";
	calendarMonth += "<td colspan=\"7\"><hr style=\"width:100%;color:#CCCCCC\" size=\"1\" /></td>\n";
	calendarMonth += "</tr>\n";
	
	return calendarMonth;
}

// Generates the body of the calendar for the specific month
function _atgCalendar_generateCalendarBody(calendarInstance)
{
	var calendarBody = "";
	var newDate = calendarInstance.displayDate;
	var newYear = newDate.getFullYear();
	var newMonth = newDate.getMonth();
	var daysInMonth = atgCalendar_getDaysInMonth(newMonth, newYear);
	var firstDayOfWeek = atgCalendar_getFirstDayOfWeek(newDate);
	var dayCount=1;
	var counter=1;

	for (var weekCount=0; weekCount<6; weekCount++) 
	{
		calendarBody += "<tr>\n";
		for (var iterator=0; iterator<calendarI18n.dayNames.length; iterator++) 
		{
			if (counter <= firstDayOfWeek)
			{
				calendarBody += "<td class=\""+calendarStyles.NumericsOff+"\"></td>\n";
				counter ++;
			}
			else if (dayCount <= daysInMonth)
			{
				var dayStyle = calendarStyles.Numerics;
				
				// Check for weekend
				if ((iterator == 0)||(iterator == 6))
					dayStyle = calendarStyles.NumericsWeekend;
				
				// Check for current day
				var iterationDate = new Date(newDate);
				iterationDate.setDate(dayCount);

				if ((iterationDate.getFullYear() == today.getFullYear())
					&&(iterationDate.getMonth() == today.getMonth())
					&&(iterationDate.getDate() == today.getDate()))
				{
					if (dayStyle == calendarStyles.NumericsWeekend)
						dayStyle = calendarStyles.NumericsWeekendToday;
					else
						dayStyle = calendarStyles.NumericsToday;
				}
					
				calendarBody += "<td id=\""+calendarInstance.calendarInstanceId+"_"+dayCount+"\" class="+dayStyle+" onclick=\"atgCalendar_setDateInMonth("+dayCount+");atgCalendar_setSelectedDate();\">"+dayCount+"</td>\n";
				dayCount ++;
			}
		}
		calendarBody += "</tr>\n";
		if (dayCount > daysInMonth)
		{
			weekCount = 6;
		}
	}
	return calendarBody;
}


// Generates the bottom part of the calendar
function _atgCalendar_generateCalendarFooter(calendarInstance)
{
	var calendarFooter = "";

  calendarFooter += "</table>\n";
	calendarFooter += "</td>\n";
	calendarFooter += "</tr>\n";
	calendarFooter += "<tr>\n";
	calendarFooter += "<td colspan=\"7\" class=\""+calendarStyles.BottomSpacer+"\">\n";
	
	if ((calendarInstance.hasToday == true) || (calendarInstance.hasClose == true))
	{
		calendarFooter += "<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n";
		calendarFooter += "<tr>\n";
		if (calendarInstance.hasToday == true)
			calendarFooter += "<td class=\""+calendarStyles.AdditionalTodayRow+"\"><span onclick=\"atgCalendar_setNewDate(today);atgCalendar_setSelectedDate();\" class=\""+calendarStyles.TodayIcon+"\">"+calendarI18n.todayTextString+"</span></td>\n";
		else
			calendarFooter += "<td></td>";
			
		if (calendarInstance.hasClose == true)
			calendarFooter += "<td class=\""+calendarStyles.AdditionalCloseRow+"\"><span onclick=\"atgCalendar_hideCalendar()\" class=\""+calendarStyles.CloseIcon+"\">"+calendarI18n.closeTextString+"</span></td>\n";
		else
			calendarFooter += "<td></td>";
			
		calendarFooter += "</tr>\n";
		calendarFooter += "</table>\n";
	}
	
	calendarFooter += "</td>\n";
	calendarFooter += "</tr>\n";
	
	calendarFooter += "<tr>\n";
	calendarFooter += "<td colspan=\"3\" class=\""+calendarStyles.BottomSpacer+"\"></td>\n";
	calendarFooter += "</tr>\n";
	calendarFooter += "</table>\n";
	calendarFooter += "</td>\n";
	calendarFooter += "<td nowrap=\"nowrap\" class=\""+calendarStyles.Right+"\"></td>\n";
	calendarFooter += "</tr>\n";
	
	calendarFooter += "<tr>\n";
	calendarFooter += "<td class=\""+calendarStyles.BorderBottomLeft+"\"></td>\n";
	calendarFooter += "<td class=\""+calendarStyles.BorderBottomMiddle+"\"></td>\n";
	calendarFooter += "<td class=\""+calendarStyles.BorderBottomRight+"\"></td>\n";
	calendarFooter += "</tr>\n";
	calendarFooter += "</table>\n";
	
	return calendarFooter;
}
//**********************************************************************************************************************************************
//
// Drag.js File
//
// (C) Copyright 1997-2009 Art Technology Group, Inc.
// All rights reserved.
//
// This page is the encapsulates a helper object that is responsible for 
// controlling drag and drop behavior and calling event handlers
// associated with those events.  This library is cross browser compatible
//
//**********************************************************************************************************************************************


/* -------------------------*/ 
/* Script version info        */
/* -------------------------*/

var script_drag                  = new Object();
script_drag.type                 = "text/javascript";
script_drag.versionMajor         = 0;
script_drag.versionMinor         = 9;



//**********************************************************************************************************************************************
// Dynamically include all required object definitions
//
//**********************************************************************************************************************************************

// Create variable to check to verify that page is loaded
//
var dragReady                             = false;

// Create registry variable here so it is all ready for use
//
var DragDropRegistry                      = new DragDropRegistry();

/*************************************************************
 **
 **
 **  DragDropRegristry Object
 **
 **
 **  This object activates components for drag and drop
 **  operations.
 **
 *************************************************************/

function DragDropRegistry()
{
   //**********************************************************************************************************************************************
   // Initialize Properties
   //
   this.dragDropRegistry                  = this;
   this.dragDropRegistry.currentElement   = null;
   this.dragDropRegistry.dropElement      = null;
   this.dragDropRegistry.mouseX           = "";
   this.dragDropRegistry.mouseY           = "";

   //**********************************************************************************************************************************************
   // Initialize methods
   //   
   this.dragDropRegistry.registerDrag     = DragDropRegisterDrag;
   this.dragDropRegistry.registerDrop     = DragDropRegisterDrop;
   this.dragDropRegistry.start            = DragDropStart;
   this.dragDropRegistry.drag             = DragDropDrag;
   this.dragDropRegistry.end              = DragDropEnd;
   this.dragDropRegistry.loadEvent        = DragDropLoadEvent;
   this.dragDropRegistry.dropWatch        = DragDropWatch;
   this.dragDropRegistry.dropCancel       = DragDropCancel;
   this.dragDropRegistry.drop             = DragDropDrop;
}

/*************************************************************
 **
 **
 **  DragDropRegister Function
 **
 **
 **  This function is responsible for activating the element
 **  with the drag and drop event handlers and refrence variables.
 **  The function takes the following parameters:
 **
 **  objElementHandle:  This is the element that needs to be
 **                     clicked to be drug. 
 **
 **  objElement: If the handle is seperate from the element you
 **              want to drag, include the element you want to 
 **              have drug.
 **
 **  Min/max variables define a box that the object can not 
 **  be drug out of. 
 **
 **
 *************************************************************/
 
function DragDropRegisterDrag(objElementHandle, objElement, minX, maxX, minY, maxY)
{
   var dragDropHandler           = this;
   
   // Refrence the registry
   //
   objElementHandle.registry     = dragDropHandler;
   
   // Register the event
   //
   if (document.all)
   {
      objElementHandle.ondragstart  = dragDropHandler.start;
      objElementHandle.ondrag       = dragDropHandler.drag;
      objElementHandle.ondragend    = dragDropHandler.end;
   }
   else
   {
	   objElementHandle.onmousedown  = dragDropHandler.start;
	}
	

   // If an element was passed in as a base, save it here
   //
	objElementHandle.base = objElement;
	
	if (objElement == null) 
      objElementHandle.base = objElementHandle;

   // Set left and top styles if they have not allready been set
   // We need to move this into the start method so we can have
   // elements that are not required to have absolute positioning
   //
	if (isNaN(parseInt(objElementHandle.base.style.left))) 
	   objElementHandle.base.style.left   = "0px";
	if (isNaN(parseInt(objElementHandle.base.style.top))) 
	   objElementHandle.base.style.top    = "0px";

   // set boundaries if they have been passed in
   //
	objElementHandle.minX	= typeof minX != 'undefined' ? minX : null;
	objElementHandle.minY	= typeof minY != 'undefined' ? minY : null;
	objElementHandle.maxX	= typeof maxX != 'undefined' ? maxX : null;
	objElementHandle.maxY	= typeof maxY != 'undefined' ? maxY : null;

   // Initialize integration functions
   //
	objElementHandle.base.onDragStart	= new Function();
	objElementHandle.base.onDragEnd	   = new Function();
	objElementHandle.base.onDrag		   = new Function();
	objElementHandle.base.onDrop		   = new Function();
	
	return false;
}

function DragDropRegisterDrop(objElementHandle, objElement, aTypes)
{
   var dragDropHandler           = this;
   
   objElementHandle.base         = objElement;
   
   // Refrence the registry
   //
   objElement.registry           = dragDropHandler;
   
   // Store acceptible drop types
   //
   objElement.dropTypes          = aTypes;
   
   // Register the event
   //
   if (document.all)
   {
      objElementHandle.ondragenter  = dragDropHandler.dropWatch;
      objElementHandle.ondragleave  = dragDropHandler.dropCancel;
      objElementHandle.ondrop       = dragDropHandler.drop;
   }
   else
   {
	   objElementHandle.onmouseover	= dragDropHandler.dropWatch;
	}
	
   // Initialize integration functions
   //
	objElement.onDropStart        = new Function();
	objElement.onDrop		         = new Function();
	objElement.onDragOver         = new Function();
	objElement.onDragOut          = new Function();
	
	return false;
}

function DragDropStart(event)
{
   // Grab object refrences
   //
   var objElement                      = this;
   var dragDropRegistry                = objElement.registry;
   dragDropRegistry.currentElement     = objElement;
   
   // Initialize Drag Icon
   //
   if (!dragDropRegistry.dragIcon)
   {
      var dragIcon                           = document.createElement("img");
      dragIcon.src                           = "/CAF/images/tree/folderBActionPlus.gif";
      dragIcon.style.visibility              = "hidden";
      dragIcon.style.position                = "absolute";
      dragDropRegistry.dragIcon              = document.body.appendChild(dragIcon);
   }
         
   // Load client event
   //
	clientEvent                         = dragDropRegistry.loadEvent(event);
	objElement.lastMouseX	            = clientEvent.clientX;
	objElement.lastMouseY	            = clientEvent.clientY;
	
	var y    = parseInt(objElement.base.style.top);
	var x    = parseInt(objElement.base.style.left);
	
	dragDropRegistry.dragIcon.visibility   = "";
	dragDropRegistry.dragIcon.style.top    = y + "px";
	dragDropRegistry.dragIcon.style.left   = x + "px";
	
	// Call integration
	//
	objElement.base.onDragStart(x, y);

	if (objElement.minX != null)	objElement.minMouseX	= clientEvent.clientX - x + objElement.minX;
	if (objElement.maxX != null)	objElement.maxMouseX	= objElement.minMouseX + objElement.maxX - objElement.minX;

	if (objElement.minY != null)	objElement.minMouseY	= clientEvent.clientY - y + objElement.minY;
	if (objElement.maxY != null)	objElement.max
   
   if (!document.all)
   {
	   document.onmousemove	   = dragDropRegistry.drag;
	   document.onmouseup		= dragDropRegistry.end;
	}
	
	return false;
}

function DragDropDrag(event)
{
	var objElement = DragDropRegistry.currentElement;
	clientEvent    = DragDropRegistry.loadEvent(event);

	var eventY	   = clientEvent.clientY;
	var eventX	   = clientEvent.clientX;
	var displaceX, displaceY;
	
	if (objElement.base.style.position != "absolute")
	{
	   var startY                       = objElement.base.offsetTop;
	   //objElement.base.style.position   = "absolute";
	   //objElement.base.style.top        = startY;
	}	
	
	var elementY   = parseInt(objElement.base.style.top);
	var elementX   = parseInt(objElement.base.style.left);   

	if (objElement.minX != null) eventX = Math.max(eventX, objElement.minMouseX);
	if (objElement.maxX != null) eventX = Math.min(eventX, objElement.maxMouseX);
	if (objElement.minY != null) eventY = Math.max(eventY, objElement.minMouseY);
	if (objElement.maxY != null) eventY = Math.min(eventY, objElement.maxMouseY);

	displaceX = elementX + (eventX - objElement.lastMouseX)
	displaceY = elementY + (eventY - objElement.lastMouseY)
	
	//DragDropRegistry.currentElement.base.style.left    = displaceX + "px";
	//DragDropRegistry.currentElement.base.style.top     = displaceY + "px";
   DragDropRegistry.dragIcon.style.visibility         =  "";
	DragDropRegistry.dragIcon.style.left               = eventX + 10;
	DragDropRegistry.dragIcon.style.top                = eventY + 10;
	DragDropRegistry.currentElement.lastMouseX	      = eventX;
	DragDropRegistry.currentElement.lastMouseY	      = eventY;

	DragDropRegistry.currentElement.base.onDrag(displaceX, displaceY);
	
	return false;
}

function DragDropEnd()
{
	document.onmousemove = null;
	document.onmouseup   = null;
	
	DragDropRegistry.currentElement.base.style.zIndex     = 0;
	
	DragDropRegistry.currentElement.base.style.position   = "";
	
	DragDropRegistry.currentElement.base.onDragEnd(	parseInt(DragDropRegistry.currentElement.base.style.left), 
								parseInt(DragDropRegistry.currentElement.base.style.top));
								
	DragDropRegistry.currentElement = null;
	DragDropRegistry.dragIcon.style.visibility            = "hidden";
	
	return false;
}

function DragDropLoadEvent(event)
{
	if (typeof event == 'undefined') 
	   event = window.event;
	if (typeof event.layerX == 'undefined') 
	   event.layerX = event.offsetX;
	if (typeof event.layerY == 'undefined') 
	   event.layerY = event.offsetY;
	return event;
}

function DragDropWatch()
{ 
   // Grab object refrences
   //
   var objElementHandle                   = this;
   var objElement                         = objElementHandle.base;
   var dragDropRegistry                   = objElement.registry;
   
   // Check to make sure that we have a current drag element,
   // the current drag element has a type, and that the type
   // matches what we can accept 
   if (dragDropRegistry.currentElement)
   {
      var currentElement                  = dragDropRegistry.currentElement.base;
         
      if (objElement.dropTypes)
      {
         if (currentElement.type)
         {
            for (var i = 0; i < objElement.dropTypes.length; i++)
            {
               if (currentElement.type == objElement.dropTypes[i])
               {
                  dragDropRegistry.dropElement     = objElement;
                  if (!document.all)
                  {
                     objElementHandle.onmouseup       = dragDropRegistry.drop;
                     objElementHandle.onmouseout      = dragDropRegistry.dropCancel;
                  }
                  objElement.onDragOver();
                  break;
               }
            }
         }
      }
      else
      {
         dragDropRegistry.dropElement     = objElement;
         if (!document.all)
         {
            objElementHandle.onmouseup       = dragDropRegistry.drop;
            objElementHandle.onmouseout      = dragDropRegistry.dropCancel;
         }
         objElement.onDragOver();
      }
   }
   if (document.all)
	{
	   window.event.returnValue   = false;
	}
	return false;
}

function DragDropCancel()
{
   // Grab object refrences
   //
   var objElementHandle                   = this;
   var objElement                         = objElementHandle.base;
   var dragDropRegistry                   = objElement.registry;
   dragDropRegistry.dropElement           = null;
   objElement.onDragOut();
   
   objElementHandle.onmouseup             = null;
   objElementHandle.onmouseout            = null;
   return false;
}

// This function passes the moved item first followed by the destination item
// The event is thrown to both source and destination
//
function DragDropDrop()
{
   // Grab object refrences
   //
   var objElementHandle                   = this;
   var objElement                         = objElementHandle.base;
   var dragDropRegistry                   = objElement.registry;
   if (!dragDropRegistry.currentElement)
      return;
      
   var movedElement                       = dragDropRegistry.currentElement.base;
   
   objElement.onDrop(movedElement, objElement);
   movedElement.onDrop(movedElement, objElement);
   
   return false;
}
// Please note: The partial page renderer requires the atg-ui_common.js script to be included on the page.
function Action(id) {

  // TODO: these should come from the form
  this.action = "";
  this.bean = "";
  this.errorUrl = "";
  this.handler = "";
  this.requestUri = "";
  this.successUrl = "";

  this.id = id;
  this.queryParameters = new Array();
  this.sourceElements = new Array();
  this.targetElements = new Array();
  this.formData = null;
  this.xmlHttpRequest = null;

}
// We only need one copy of these functions
Action.prototype.addForm = ActionAddForm;
Action.prototype.addQuery = ActionAddQuery;
Action.prototype.clearElements = ActionClearElements;
Action.prototype.getProtocol = function() { return this.protocol; };
Action.prototype.registerSourceElement = ActionRegisterSourceElement;
Action.prototype.registerTargetElement = ActionRegisterTargetElement;
Action.prototype.retransact = ActionRetransact;
Action.prototype.setHideElements = function (elementIds) { this.hideElementIds = elementIds; };
Action.prototype.setHistoryUrl = ActionSetHistoryUrl;
Action.prototype.setShowElements = function (elementIds) { this.showElementIds = elementIds; };
Action.prototype.setSynchronizeTransaction = ActionSetSynchronizeTransaction;
Action.prototype.transact = ActionTransact;
// These are defaults that can be overridden by instances if necessary
Action.prototype.allowWaitIndicator = true;
Action.prototype.protocol = "formhandlers";
Action.prototype.redirectToErrorUrl = false;
Action.prototype.synchronizeTransaction = false;
Action.prototype.useRequestDispatcherForward = true;
  
function ActionAddForm(name, value, isNewConnection) {
  if (!name || !value || name == "")
    return;

  if (!this.formData) {
    this.formData = "";
  }

  var encodedValue = encodeURIComponent(value);
  if (this.protocol == "formhandlers") {
    var property = this.bean + "." + name;
    this.formData += encodeURIComponent("_D:" + property) + "=+&";
    this.formData += encodeURIComponent(property) + "=" + encodedValue + "&";
  }
  else {
    this.formData += encodeURIComponent(name) + "=" + encodedValue + "&";
  }

  // Browser history
  if (this.historyUrl && isNewConnection) {
    if (!this.historyQueryString) {
      this.historyQueryString = "?";
    }
    this.historyQueryString += name + "=" + encodedValue + "&";
  }
}
function ActionAddQuery(name, value) {
  var queryParameter = new Object();
  queryParameter.name = name;
  queryParameter.value = value;
  this.queryParameters.push(queryParameter);
}
function ActionClearElements() {
  this.queryParameters = new Array();
  this.sourceElements = new Array();
  this.targetElements = new Array();
}
function ActionRetransact(url) {
  if (window.__ajax_impl) {
    window.__ajax_impl.retransactAction(this, url);
  }
}
function ActionRegisterSourceElement(elementId, formName, valueAccessorScript, value, isHistoryTitle, isQuery) {
  var sourceElement = new Object();
  sourceElement.elementId = elementId;
  sourceElement.formName = formName;
  sourceElement.isHistoryTitle = isHistoryTitle;
  sourceElement.isQuery = isQuery;
  sourceElement.valueAccessorScript = valueAccessorScript;
  sourceElement.value = value;
  this.sourceElements.push(sourceElement);
}
function ActionRegisterTargetElement(elementId, pathToXslt, xsltString, valueAccessorScript) {
  var targetElement = new Object();
  targetElement.elementId = elementId;
  targetElement.isXmlTarget = function () { return (this.pathToXslt != null || this.xsltString != null) ? true : false; };
  targetElement.pathToXslt = pathToXslt;
  targetElement.xsltString = xsltString;
  targetElement.valueAccessorScript = valueAccessorScript;
  this.targetElements.push(targetElement);
}
function ActionSetHistoryUrl(historyUrl) {
  // Browser history
  this.historyFrame = document.getElementById("__ppr_history_element__");
  if (this.historyFrame) {
    this.historyQueryString = null;
    this.historyUrl = historyUrl;
  }
}
function ActionSetSynchronizeTransaction(synchronizeTransaction) {
  this.synchronizeTransaction = synchronizeTransaction;
}
function ActionTransact() {
  if (window.__ajax_impl) {
    window.__ajax_impl.transactAction(this);
  }
}

function PartialPageRenderer() {
  this.debugRegularExpression = /((\x0d\x0a){1,} *)+/gi;
  this.elementSeparator = "<701cf83a4e9f>";

  if (!(window.__ajax_impl)) {
    window.__ajax_impl = this;
  }

  this.applyStylesheet          = PartialPageRendererApplyStylesheet;
  this.completeTransaction      = PartialPageRendererCompleteTransaction;
  this.connect                  = PartialPageRendererConnect;
  this.createErrorMessage       = PartialPageRendererCreateErrorMessage;
  this.createXmlHttpRequest     = PartialPageRendererCreateXmlHttpRequest;
  this.getIsXmlRequest          = PartialPageRendererGetIsXmlRequest;
  this.getXmlDocumentFromString = PartialPageRendererGetXmlDocumentFromString;
  this.getXmlDocumentFromUrl    = PartialPageRendererGetXmlDocumentFromUrl;
  this.prepareTransaction       = PartialPageRendererPrepareTransaction;
  this.readyStateChange         = PartialPageRendererReadyStateChange;
  this.retransactAction         = PartialPageRendererRetransactAction;
  this.startAction              = PartialPageRendererStartAction;
  this.stopAction               = PartialPageRendererStopAction;
  this.transactAction           = PartialPageRendererTransactAction;
  this.updateTargetElement      = PartialPageRendererUpdateTargetElement;
  
  this.beforeTransact           = new Function("return true");

  // Opera support
  this.iframe = null;

  try {
    if (is.gecko) {
      Document.prototype.loadXML = function (s) {
        // Parse the string to a new doc
        //
        var doc2 = (new DOMParser()).parseFromString(s, "text/xml");
    
        // Remove all initial children
        //
        while (this.hasChildNodes()) {
          this.removeChild(this.lastChild);
        }
  
        // Insert and import nodes
        for (var i = 0; i < doc2.childNodes.length; i++) {
          this.appendChild(this.importNode(doc2.childNodes[i], true));
        }
      }
    }
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "1.", this.createErrorMessage(null));
  }
}
function PartialPageRendererApplyStylesheet(xml, xslt) {
  var html = "";
  try {
    if (window.XSLTProcessor) { // pretty much everyone else
      var xsltProcessor = new XSLTProcessor();
      xsltProcessor.importStylesheet(xslt);
      var docFragment = xsltProcessor.transformToFragment(xml, document);
      var div = document.createElement("div");
      div.appendChild(docFragment);
      html = div.innerHTML;
    }
    else if (is.ie) { // IE
      html = xml.transformNode(xslt);
    }
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error applying stylesheet", this.createErrorMessage(null));
  }
  return html;
}
function PartialPageRendererAtgUiCommonRequired(ex, location, context) {
  alert(location + "\n\n" + context + "\n\n" + ex.message);
}
function PartialPageRendererBodyOnLoad() {
  if (parent) {
    var targetElements = new Array();
    var urlParts = document.URL.split('?');
    if (urlParts.length == 2) {
      var nameValuePairs = urlParts[1].split('&');
      for (var i = 0; i < nameValuePairs.length; i++) {
        var parts = nameValuePairs[i].split('=');
        if (parts.length == 2 && parts[0].indexOf("__element") == 0) {
          var element = document.getElementById(parts[1]);
          if (element && element.innerHTML) {
            var targetElement = new Object();
            targetElement.id = parts[1];
            targetElement.html = element.innerHTML;
            targetElements.push(targetElement);
          }
          else if (element && element.value) {
            var targetElement = new Object();
            targetElement.id = parts[1];
            targetElement.value = element.value;
            targetElements.push(targetElement);
          }
        }
      }
    }
    parent.PartialPageRendererCompleteTransaction(targetElements);
  }
}
function PartialPageRendererCompleteTransaction(targetElements) {
  for (var i = 0; i < targetElements.length; i++) {
    var element = document.getElementById(targetElements[i].id);
    if (element && targetElements[i].html) {
      var span = document.createElement("span");
      span.innerHTML = targetElements[i].html;
      element.innerHTML = "";
      element.appendChild(span);
    }
    else if (element && targetElements[i].value) {
      element.value = targetElements[i].value;
    }
  }
}
function PartialPageRendererConnect(action, isNewConnection) {
  try {
    // Formhandler parameters
    if (action.protocol == "formhandlers") {
      if (action.requestUri == "") {
        action.requestUri = action.action;
      }

      action.formData = "_dyncharset=UTF-8&" + action.formData;
      action.formData += encodeURIComponent(action.bean + ".successURL") + "=" + encodeURIComponent(action.successUrl) + "&";
      action.formData += encodeURIComponent("_D:" + action.bean + ".successURL") + "=+&";
      if (action.errorUrl) {
        action.formData += encodeURIComponent(action.bean + ".errorURL") + "=" + encodeURIComponent(action.errorUrl) + "&";
        action.formData += encodeURIComponent("_D:" + action.bean + ".errorURL") + "=+&";
      }
      action.formData += encodeURIComponent(action.bean + "." + action.handler) + "=&";
      action.formData += encodeURIComponent("_D:" + action.bean + "." + action.handler) + "=+&";
      action.formData += "_DARGS=" + encodeURIComponent(action.requestUri + "." + action.id) + "&";
			if (action.useRequestDispatcherForward) {
        action.formData += "atg.formHandlerUseForwards=true&";
      }
      action.formData += "_handler=" + encodeURIComponent(action.handler) + "&";
      action.formData += "_isppr=true&";
    }

    // Submit form pairs via HTTP POST
    action.xmlHttpRequest = this.createXmlHttpRequest();
    var isAsynchronous = (action.synchronizeTransaction) ? false : true;
    var actionUrl = action.action;
    actionUrl.indexOf("?") > -1 ? actionUrl += "&" : actionUrl += "?";
    for (var i = 0; i < action.queryParameters.length; i++) {
      actionUrl += action.queryParameters[i].name + "=" + action.queryParameters[i].value + "&"; // value already encoded
    }
    if (action.protocol == "formhandlers") {
      actionUrl += "_DARGS=" + action.requestUri + "." + action.id + "&";
    }

    // The HTTP connection
    action.xmlHttpRequest.open("POST", actionUrl, isAsynchronous);
    // Browser history
    if (action.historyFrame && action.historyUrl && isNewConnection) {
      action.historyFrame.src = action.historyUrl + action.historyQueryString;
    }
    // Register XMLHTTPRequest callback
    if (!action.synchronizeTransaction) {
      var self = this;
      if (window.ActiveXObject) {
        action.xmlHttpRequest.onreadystatechange = function () { self.readyStateChange(self, action); };
      }
      else if (window.XMLHttpRequest) {
        action.xmlHttpRequest.onload = function () { self.readyStateChange(self, action); };
      }
    }
    if (this.getIsXmlRequest(action)) {
      action.xmlHttpRequest.setRequestHeader("Accept", "text/xml");
    }
    action.xmlHttpRequest.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    action.xmlHttpRequest.send(action.formData);

    // Wait for XMLHTTPRequest to unblock
    if (action.synchronizeTransaction) {
      this.readyStateChange(this, action);
    }
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error connecting", this.createErrorMessage(action));
  }

  // Reset form and query data
  action.formData = null;
  action.historyQueryString = null;
  action.queryParameters = new Array();
}
function PartialPageRendererCreateErrorMessage(action) {
  var errorMessage = "";
  if (is.ie) {
    errorMessage += "Browser = Internet Explorer\n";
  }
  else if (is.gecko) {
    errorMessage += "Browser = Gecko\n";
  }
  else if (is.opera) {
    errorMessage += "Browser = Opera\n";
  }
  else {
    errorMessage += "Browser = Unknown\n";
  }
  if (action) {
    errorMessage += "Action = " + action.action + "\n";
    errorMessage += "Bean = " + action.bean + "\n";
    errorMessage += "Error URL = " + action.errorUrl + "\n";
    errorMessage += "Handler = " + action.handler + "\n";
    errorMessage += "ID = " + action.id + "\n";
    errorMessage += "Protocol = " + action.protocol + "\n";
    errorMessage += "Request URI = " + action.requestUri + "\n";
    errorMessage += "Success URL = " + action.successUrl + "\n";
    if (action.synchronizeTransaction) {
      errorMessage += "Synchronous = true\n";
    }
    else {
      errorMessage += "Synchronous = false\n";
    }
  }
  return errorMessage;
}
function PartialPageRendererCreateXmlHttpRequest() {
  var httpRequest = null;
  try {
     // do this in a more portable way
     // check the presence of the necessary object instead of a is.ie/is.gecko check
     // this will allow newer operas and gecko clones like camino to work
     var ua = navigator.userAgent.toLowerCase();
     if (!window.ActiveXObject)
       httpRequest = new XMLHttpRequest();
     else if (ua.indexOf('msie 5') == -1)
       httpRequest = new ActiveXObject("Msxml2.XMLHTTP");
     else
       httpRequest = new ActiveXObject("Microsoft.XMLHTTP");
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error creating request", this.createErrorMessage(null));
  }
  return httpRequest;
}
function PartialPageRendererGetIsXmlRequest(action) {
  var isXmlRequest = false;
  for (var i = 0; i < action.targetElements.length; i++) {
    var targetElement = action.targetElements[i];
    if (targetElement.isXmlTarget) {
      isXmlRequest = true;
      break;
    }
  }
  return isXmlRequest;
}
function PartialPageRendererGetXmlDocumentFromString(xmlString) {
  var xmlDocument = null;
  try {
    if (window.ActiveXObject) { // IE
      xmlDocument = new ActiveXObject("Msxml.DOMDocument");
      xmlDocument.async = false;
      xmlDocument.resolveExternals = false;
      xmlDocument.validateOnParse = true;
      xmlDocument.loadXML(xmlString);
    }
    else if (document.implementation) { // Gecko
      xmlDocument = document.implementation.createDocument("", "", null);
      xmlDocument.loadXML(xmlString);
    }
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error getting XML document from string", this.createErrorMessage(null));
  }
  return xmlDocument;
}
function PartialPageRendererGetXmlDocumentFromUrl(xmlUrl) {
  var httpRequest = this.createXmlHttpRequest();
  httpRequest.open("GET", xmlUrl, false);
  httpRequest.send(null);
  return httpRequest.responseXML;
}
function PartialPageRendererPrepareTransaction(action, isNewConnection) {
  var success = true;
  try {
    if (is.ie || is.gecko || is.opera) {
      // Populate form pairs from source elements
      if (!action.formData) {
        action.formData = "";
      }

      // Build history query string
      if (action.historyUrl && isNewConnection && !action.historyQueryString) {
        action.historyQueryString = "?";
      }
    }
    else {
      success = false;
    }
  }
  catch (ex) {
    PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error preparing transaction", this.createErrorMessage(action));
    success = false;
  }
  return success;
}
function PartialPageRendererReadyStateChange(self, action) {
  if (action.xmlHttpRequest && action.xmlHttpRequest.readyState == 4 && (action.xmlHttpRequest.status == 200 || action.xmlHttpRequest.status == 500)) {

    // Exception handling to error URL
    if (action.xmlHttpRequest.status == 500 && action.redirectToErrorUrl) {
      if (is.ie) {
        document.location.pathname = action.errorUrl;
      }
      else if (window.location.pathname) {
        window.location.pathname = action.errorUrl;
      }
      return;
    }

    // Refresh target elements via XSLT
    var targetElems = action.targetElements;
    var targetElement;
    for (var i = 0; i < targetElems.length; i++) {
      targetElement = targetElems[i];
      var html = "";
      if (targetElement.isXmlTarget()) {
        var xslt = (targetElement.xsltString != null) ? self.getXmlDocumentFromString(targetElement.xsltString) : self.getXmlDocumentFromUrl(targetElement.pathToXslt);
        html = self.applyStylesheet(action.xmlHttpRequest.responseXML, xslt);
      }
      else
      {
        html = action.xmlHttpRequest.responseText;
      }
      self.updateTargetElement(targetElement, html);
    }

    // Refresh target elements and invoke target scripts via JavaScript
    var nameValuePairs = action.xmlHttpRequest.responseText.split(self.elementSeparator);
    var counter = 0;
    while (counter < nameValuePairs.length - 1) {
      counter++;
      var name = nameValuePairs[counter++];
      var mode = nameValuePairs[counter++];
      var value = nameValuePairs[counter++];
      if (!name || name == "" || !value)
        continue;

      if (name == "sessioninvalid") {
        // Value contains redirect URL for invalid session
        window.sessioninvalid = true;
        if (is.ie) {
          document.location.href = document.location.protocol + "//" + document.location.hostname + ":" + document.location.port + value;
        }
        else if (window.location.pathname) {
          window.location.pathname = value;
        }
        return;
      }
      else if (name == "javascript") {
        try {
          eval(value);
        }
        catch (ex) {
          var message = self.createErrorMessage(action);
          //alert("JavaScript Error\n" + message + ex.message);
        }
      }
      else if (document.getElementById(name)) {
        var element = document.getElementById(name);
        if (element.value) {
          if (mode == "overwrite") {
            element.value = value;
          }
          else { // append
            var temp = element.value;
            temp += value;
            element.value = temp;
          }
        }
        else {
          element.innerHTML = value;
          var scripts = element.getElementsByTagName("script");
          for (var i = 0; i < scripts.length; i++) {
            try {
              eval(scripts[i].innerHTML);
            }
            catch (ex) { // JavaScript errors are of interest
              var message = self.createErrorMessage(action);
              //alert("JavaScript Error\n" + message + ex.message);
            }
          }
        }
      }
      else {
        // Element not found with name
      }
    }
    action.xmlHttpRequest = null;
    self.stopAction(action);
  }
}
function PartialPageRendererRetransactAction(action, queryString) {
  if (!this.prepareTransaction(action, false))
    return;

  var nameValuePairs = queryString.split("&");
  for (var i = 0; i < nameValuePairs.length; i++) {
    var pairs = nameValuePairs[i].split("=");
    if (pairs.length != 2)
      continue;

    // Ignore private parameters
    if (pairs[0].indexOf("__") == 0)
      continue;

    action.addForm(pairs[0], pairs[1], false);
  }

  this.connect(action, false);
}
function PartialPageRendererStartAction(action) {
	if (action.allowWaitIndicator == false)
		return;

  if (window.javaScriptErrors && window.javaScriptErrors.length == 0 && window.__ppr_errorelements_) {
    for (var i = 0; i < window.__ppr_errorelements_.length; i++) {
      document.getElementById(window.__ppr_errorelements_[i]).style.display = 'none';
    }
  }

  if (action.synchronizeTransaction) // please wait screen functionality only works with asynchronous XmlHttpRequest
    return;

  var code = "";
  var numberString;

  var hideElements = action.hideElementIds;
  if (!hideElements) {
    hideElements = window.__ppr_hideelements_;
  }
  if (hideElements) {
    for (var i = 0; i < hideElements.length; i++) {
      numberString = String(i);
      code += "var hide" + numberString + " = document.getElementById('" + hideElements[i] + "'); ";
      code += "if (hide" + numberString + ") hide" + numberString + ".style.display = 'none'; ";
    }
  }
  var showElements = action.showElementIds;
  if (!showElements) {
    showElements = window.__ppr_showelements_;
  }
  if (showElements) {
    for (var i = 0; i < showElements.length; i++) {
      numberString = String(i);
      code += "var show" + numberString + " = document.getElementById('" + showElements[i] + "'); ";
      code += "if (show" + numberString + ") show" + numberString + ".style.display = 'block'; ";
    }
  }
  window.setTimeout(code, 0);
}
function PartialPageRendererStopAction(action) {
  if (action.synchronizeTransaction || (action.allowWaitIndicator == false)) // please wait screen functionality only works with asynchronous XmlHttpRequest
    return;

  var code = "";
  var numberString;

  var showElements = action.showElementIds;
  if (!showElements) {
    showElements = window.__ppr_showelements_;
  }
  if (showElements) {
    for (var i = 0; i < showElements.length; i++) {
      numberString = String(i);
      code += "var show" + numberString + " = document.getElementById('" + showElements[i] + "'); ";
      code += "if (show" + numberString + ") show" + numberString + ".style.display = 'none'; ";
    }
  }
  var hideElements = action.hideElementIds;
  if (!hideElements) {
    hideElements = window.__ppr_hideelements_;
  }
  if (hideElements) {
    for (var i = 0; i < hideElements.length; i++) {
      numberString = String(i);
      code += "var hide" + numberString + " = document.getElementById('" + hideElements[i] + "'); ";
      code += "if (hide" + numberString + ") hide" + numberString + ".style.display = 'block'; ";
    }
  }
  window.setTimeout(code, 1);
  if (window.__ppr_errorelements_ && window.javaScriptErrors && window.javaScriptErrors.length > 0) {
    for (var i = 0; i < window.__ppr_errorelements_.length; i++) {
      var error = document.getElementById(window.__ppr_errorelements_[i]);
      if (error) {
        error.style.display = "block";
      }
    }
  }
}
function PartialPageRendererTransactAction(action) {
  if (this.beforeTransact())
  {
    this.startAction(action);
  
    if (!this.prepareTransaction(action, true))
      return;
  
    try {
      var sourceElts = action.sourceElements;
      for (var i = 0; i < sourceElts.length; i++) {
        var se = sourceElts[i]; // compression-safe 2-char name
        var sourceElementId = se.elementId;
        var script = "";
        var value = "";
        if (se.value) {
          script = "se.value";
        }
        else if (se.elementId) {
          // When dealing with elements on the page, we need to handle radio buttons as a special case,
          // because they will be implemented in an array of elements, so we must determine which item
          // is selected, and pass that on back.
          var tmpJS = "document.getElementById('" + sourceElementId + "')";
          var obj = eval(tmpJS);
          if (obj && obj.type && obj.type == "radio") {
            tmpJS = "document.getElementsByName('" + sourceElementId + "')";
            var radioButtons = eval(tmpJS);
    
            for (var j = 0; j < radioButtons.length; j++) {
              if (radioButtons[j].checked) {
                script = "document.getElementsByName('" + sourceElementId + "')[" + j + "]." + se.valueAccessorScript;
                break;
              }
            }
          }
          else if (obj && obj.type && obj.type == "checkbox") {
            // Checkboxes are pretty simple, and we need to pass along the specific string values
            if (obj.value) {
              if (obj.checked) {
                script = "document.getElementById('" + sourceElementId + "')." + se.valueAccessorScript;
              }
              else { // if check box with value is not checked, do not submit form!
                continue;
              }
            }
            else {
              script = (obj.checked ? "true" : "false");
            }
          }
          else {
            script = "document.getElementById('" + sourceElementId + "')." + se.valueAccessorScript;
          }      
        }
        else {
          if (se.valueAccessorScript != null)
           script = se.valueAccessorScript;
        }
        if (se.isQuery) { // Query parameter
          action.addQuery(se.formName, encodeURIComponent(eval(script)));
        }
        else if (action.protocol == "formhandlers") { // ATG form header parameter
          try {
            if (script != ''){
              value = encodeURIComponent(eval(script));
            }else{
              value = '';
            }

            var property = action.bean + "." + se.formName;
            action.formData += encodeURIComponent(property) + "=" + value + "&";
            action.formData += encodeURIComponent("_D:" + property) + "=+&";
          }
          catch (ex) {
            // allow conditionally present elements with display:none to fail gracefully (Mozilla)
            // because eval will throw exceptions for these elements
          }
        }
        else { // Form header parameter
          value = encodeURIComponent(eval(script));
          action.formData += encodeURIComponent(se.formName) + "=" + value + "&";
        }
  
        // Browser history
        if (action.historyUrl) {
          action.historyQueryString += se.formName + "=" + value + "&";
          if (se.isHistoryTitle) {
            action.historyQueryString += "__title=" + value + "&";
          }
        }
      }
  
      if (action.historyUrl) {
        action.historyQueryString += "__actionId=" + action.id + "&__historyId=" + Math.random() + "&";
      }
  
      this.connect(action, true);
    }
    catch (ex) {
      PartialPageRendererAtgUiCommonRequired(ex, "JavaScript error transacting", this.createErrorMessage(action));
    }
  }
}
function PartialPageRendererUpdateTargetElement(targetElement, html) {
  //html = html.replace(/\r\n/gi, "").replace(/\n/gi, "").replace(/\\/gi, "\\\\").replace(/\"/gi, "\\\"");

  var script = "";
  if (targetElement.elementId) {
    script = "if (document.getElementById('" + targetElement.elementId + "')) { document.getElementById('" + targetElement.elementId + "')." + targetElement.valueAccessorScript + " = \"" + html + "\"; }";
  }
  else {
    script = targetElement.valueAccessorScript;
  }
  try {
    eval(script);
  }
  catch (ex) {
  }
}
function ppr_transact(actionElementId,synch){
  if (actionElementId.indexOf("__ppr_") == -1)
  {
    actionElementId = "__ppr_" + actionElementId;
  }
  if (window[actionElementId]) {
    if (synch == true) {
      window[actionElementId].setSynchronizeTransaction(true);
    }
    window[actionElementId].transact(); 
  }
}
if (!(window.__ajax_impl)) {
  window.__ajax_impl = new PartialPageRenderer();
}
//**********************************************************************************************************************************************
//
// tree.js File
//
// (C) Copyright 1997-2009 Art Technology Group, Inc.
// All rights reserved.
//
// This page is the encapsulates a helper object that is responsible for 
// controlling dhtml tree behavior and calling event handlers
// associated with those events.  This library is cross browser compatible
//
//**********************************************************************************************************************************************


/* -------------------------*/ 
/* Script version info        
/* -------------------------*/

var script_tree                  = new Object();
script_tree.type                 = "text/javascript";
script_tree.versionMajor         = 0;
script_tree.versionMinor         = 9;
script_tree.debug                = 0;


// this can be used by other javascript file to determine if they need to import this file
/*    example  
 *
 *    if ( typeof script_tree != "object" ) 
 *         document.write("<scr"+"ipt type='text/javascript' src='/CAF/scripts/tree.js'><"+"/"+"script>");
 *
*/

/*************************************************************
 **
 **
 **  Tree Object
 **
 **
 **  This object defines the global area to be occupied by the
 **  main tree.  This object is a management object that defines
 **  a starting point forthe tree.  This object is a container
 **  for child Branch objects.
 **
 *************************************************************/

function Tree(id)
{
   //**********************************************************************************************************************************************
   // Initialize Element
   //
   this.tree                              = document.createElement("div");
   this.tree.id                           = id;
   
   //**********************************************************************************************************************************************
   // Initialize Properties
   //
   this.tree.branches                     = new Array();
   this.tree.branchesById                 = new Array();
   this.tree.loadedBranches               = new Array();
   this.tree.highlightedBranch            = null;
   this.tree.checkboxes                   = false;
   this.tree.checkboxBehaviors            = null;
   this.tree.cssDots                      = false;
   this.tree.rootNodes                    = new Array();
   this.tree.branchStateAction            = null;
   this.tree.checkedItems                 = new Array();

   //**********************************************************************************************************************************************
   // Initialize Methods
   //
   this.tree.addBranch                    = TreeAddBranch;
   this.tree.getBranchById                = TreeGetBranchById;
   this.tree.openToBranch                 = TreeOpenToBranch;
   this.tree.getCheckedItems              = TreeGetCheckedItems;
   this.tree.setCheckedItems              = TreeSetCheckedItems;
   this.tree.addCheckedItem               = TreeAddCheckedItem;
   this.tree.removeCheckedItem            = TreeRemoveCheckedItem;
   this.tree.deselectAllItems             = TreeDeselectAllItems;
   this.tree.checkboxInitState            = CheckboxStateInit;
   this.tree.checkboxOnclick              = null;
   this.tree.resizeWindow                 = null;
}

/*************************************************************
 **
 **
 **  TreeAddBranch
 **
 **  This function adds a new branch to the tree.  This function
 **  accepts the following params:
 **  id: ID of the branch, must be unique.
 **  label: text to be used as the branch label
 **  type: Application specific type to be used for drag and drop
 **        operations
 **
 *************************************************************/
function TreeAddBranch(id, label, type)
{
   var tree                               = this;
   
   var newBranch                          = new Branch(tree, tree, id, label, type).branch
   newBranch                              = tree.appendChild(newBranch);
   tree.branchesById[newBranch.id]        = newBranch;
   tree.branches[tree.branches.length]    = newBranch;
   tree.rootNodes[tree.rootNodes.length]  = newBranch;

   tree.resizeWindow();
   return newBranch;
}

/*************************************************************
 **
 **
 **  TreeGetBranchById
 **
 **  This function returns a branch by ID or false if not found
 **  or loaded.
 **
 *************************************************************/
function TreeGetBranchById(id)
{
   var tree                               = this;

   if ( tree.branchesById[id]) 
      return tree.branchesById[id];

   return false;
}

/*************************************************************
 **
 **
 **  TreeOpenToBranch
 **
 **  This function opens the tree to a specified node.  This 
 **  function accepts an array of node id's to use as a path
 **  to the desired node.
 **
 *************************************************************/
function TreeOpenToBranch(pathArray)
{
   var tree                               = this;
   
   var currentNode                        = tree;
   var pastNode                           = tree;

   // Cycle through the path array and find the 
   // path node one at a time.  Open each node
   // as you get there.
   //
   for (var i = 0; i < pathArray.length; i++)
   {
      if (currentNode.branches.length > 0)
      {
         for (var x = 0; x < currentNode.branches.length; x++)
         {
            if (currentNode.branches[x].id == pathArray[i])
            {
               pastNode       = currentNode;
               currentNode    = currentNode.branches[x];
               currentNode.open();
               break;
            }
         }
      }
      if (currentNode == pastNode)
      {
         alert("not found " + currentNode.id);
         break;
      }
   }
}

/*************************************************************
 **
 **
 **  TreeGetSelectedItems
 **
 **  This function returns all branches that have been checked
 **  by the user
 **
 *************************************************************/
function TreeGetCheckedItems()
{
  var tree                               = this;
  return tree.checkedItems;
}

/*************************************************************
 **
 **
 **  TreeSetSelectedItems
 **
 **  This function set all branches that have been checked
 **  by the user
 **
 *************************************************************/
function TreeSetCheckedItems(checkedItems)
{
  var tree                               = this;
  tree.checkedItems                      = checkedItems;
}

function TreeAddCheckedItem(item)
{
  var tree                                      = this;
  tree.checkedItems[tree.checkedItems.length]   = item;
}

function TreeRemoveCheckedItem(item)
{
  var tree                               = this;

  for (var i = 0; i < tree.checkedItems.length; i++)
  {
    if (tree.checkedItems[i] == item)
    {
      var aTemp = tree.checkedItems.splice(i, 1);
      break;
    }
  }
}

/*************************************************************
 **
 **
 **  TreeDeselectAllItems
 **
 **  This function uncheckes all checked nodes.
 **
 *************************************************************/
function TreeDeselectAllItems()
{
   var tree                               = this;
   
   for (var i = 0; i < tree.checkedItems.length; i++)
   {  
      var branch                          = tree.checkedItems[i];
      branch.checkbox.checked             = false;
      branch.imageCheck.style.display     = "none";
   }
   tree.checkedItems                      = new Array();   
}


/*************************************************************
 **
 **
 **  Branch Object
 **
 **
 **  This object defines the main display object of the tree
 **  Each node of the tree is a "Branch" and is responsible for
 **  maintaining it's own display parameters as well as providing
 **  an interface for adding child branches.  This element will
 **  be implemented as a table element to take advantage of layout
 **  attributes.
 **
 *************************************************************/

function Branch(tree, parent, id, label, type)
{
   //**********************************************************************************************************************************************
   // Initialize Element
   //
   this.branch                            = document.createElement("div");
   this.branch.id                         = tree.id+ "_" + type + "_" + id;
   this.branch.sourceId                   = id;
   this.branch.type                       = type;

   //**********************************************************************************************************************************************
   // Initialize Properties
   //
   this.branch.tree                       = tree;
   this.branch.parent                     = parent;
   this.branch.loadLevel                  = 0;
   this.branch.loadAction                 = null;
   this.branch.hasChildren                = true;
   this.branch.iconOpen                   = null;
   this.branch.iconClosed                 = null;
   this.branch.iconCheck                  = null;
   this.branch.selectable                 = false;
   this.branch.label                      = label;
   this.branch.itemCount                  = null;
   this.branch.displayNode                = null;
   this.branch.highlighted                = false;
   this.branch.classNameStr               = null;
   this.branch.highlightedClass           = null;
   this.branch.branches                   = new Array();
   this.branch.displayRow                 = null;
   this.branch.type                       = type;
   this.branch.initStateOpen              = false;
   this.branch.initStateChecked           = false;
   this.branch.initStateSelected          = false;
   this.branch.isFirst                    = false;
   this.branch.isLast                     = false;
   this.branch.checkboxes                 = tree.checkboxes;
   this.branch.canDrag                    = false;
   this.branch.canDrop                    = false;
   
   //**********************************************************************************************************************************************
   // Initialize Refrence Variables
   //
   this.branch.childCell                  = "";
   this.branch.displayCell                = "";
   this.branch.icon                       = "";
   this.branch.checkbox                   = new CheckBox ( this.branch,
                                                           this.branch.tree,
                                                           false /*disabled*/,
                                                           false /*checked*/ )  ;
   this.branch.imageCheck                 = null;

   //**********************************************************************************************************************************************
   // Initialize Methods
   //
   this.branch.display                    = BranchDisplay;
   this.branch.open                       = BranchOpen;
   this.branch.close                      = BranchClose;
   this.branch.notify                     = BranchNotify;
   this.branch.loadChildren               = BranchLoadChildren;
   this.branch.unloadAction               = BranchUnloadAction;
   this.branch.addChild                   = BranchAddChild;
   this.branch.select                     = BranchSelect;
   this.branch.toggle                     = BranchToggle;
   this.branch.getDisplayCell             = BranchGetDisplayCell;
   this.branch.highlight                  = BranchHighlight;
   this.branch.checkChildLoad             = BranchCheckChildLoad;
   this.branch.dragOver                   = BranchDragOver;
   this.branch.dragOut                    = BranchDragOut;
   this.branch.drop                       = BranchDrop;
   this.branch.selectAction               = null;
   this.branch.onfocus                    = BranchFocus;
   this.branch.onblur                     = BranchBlur;
   this.branch.onkeydown                  = BranchKeyPress;
}



/*************************************************************
 **
 **
 **  BranchDisplay
 **
 **  This function is responsible for creating the layout of
 **  the branch object
 **
 *************************************************************/
function BranchDisplay()
{
   var branch                             = this; 
   /* so get the level of nesting for this branch use this function 
      0 for root items, 1-N  for the child items
   */
   // var level = getBranchLevel(this.tree , this.parent.id);

   branch.actionFrameDiv                  = document.createElement("div");
   branch.actionFrameDiv.style.width      = "0";
   branch.actionFrameDiv.style.height     = "0";
   branch.actionFrameDiv.style.position   = "absolute";
   branch.actionFrameDiv.style.top        = 6;
   branch.actionFrameDiv.style.left       = 6;

   branch.actionFrame                     = document.createElement("iframe");
   branch.actionFrame.height              = ( script_tree.debug > 5) ? 100 : 0;
   branch.actionFrame.width               = ( script_tree.debug > 5) ? 100 : 0;
   branch.actionFrame.border              = 0;
   branch.actionFrame.style.visibility    = ( script_tree.debug > 5) ? "visible" : "hidden";

   branch.actionFrameDiv.appendChild(branch.actionFrame);
   branch.appendChild(branch.actionFrameDiv);

   branch.mainDiv                         = document.createElement("div");
   branch.mainDiv.style.position          = "relative";
   branch.mainDiv.style.width             = "98%";
   branch.mainDiv.style.margin            = 0;
   branch.table                           = document.createElement("table");

   if ( branch.tree.cssDots ) 
   {
      branch.table.cellPadding            = 0;
      branch.table.cellSpacing            = 0;
      branch.table.border                 = 0;
      branch.table.style.margin           = -1;
      branch.table.className              = "dTree_tree_dotted";
   } 
   else 
   {
      branch.table.cellPadding            = 0;
      branch.table.cellSpacing            = 0;
      branch.table.border                 = 0;
      branch.table.style.margin           = 0;
      branch.table.className              = "dTree_tree_not_dotted";
   }
   branch.displayRow                      = branch.table.insertRow(0);
   branch.displayRow.className            = "dTree_tree_table_row_displayRow";

   // Create icon cell
   //
   var iconCell                           = branch.displayRow.insertCell(branch.displayRow.cells.length);
   iconCell.colSpan                       = 1;

   if (branch.hasChildren)
   {
      iconCell.className                  = "dTree_controlSpace";
      var openIcon                        = document.createElement("img");
      // alert( branch.iconOpen);
      if ( branch.iconOpen != null && branch.iconOpen != "null") {
        openIcon.src                      = branch.iconOpen;
      } else {
        openIcon.src                      = branch.parent.openIcon.src;
        branch.iconOpen                   = branch.parent.openIcon.src;
      }
      branch.openIcon                     = iconCell.appendChild(openIcon);
      branch.openIcon.branch              = branch;
      branch.openIcon.onclick             = branch.toggle;
      branch.openIcon.style.cursor        = "hand";
      branch.openIcon.style.display       = "none";      
   
      var closedIcon                      = document.createElement("img");
      if ( branch.iconClosed != null && branch.iconClosed != "null") {
        closedIcon.src                    = branch.iconClosed;
      } else {
        closedIcon.src                    = branch.parent.closedIcon.src;
        branch.iconClosed                 = branch.parent.closedIcon.src;
      }
      branch.closedIcon                   = iconCell.appendChild(closedIcon);
      branch.closedIcon.branch            = branch;
      branch.closedIcon.onclick           = branch.toggle;
      branch.closedIcon.style.cursor      = "hand";
      branch.icon                         = branch.closedIcon;
   } 
   else 
   {
      iconCell.innerHTML                  = "<div class='controlSpace'>&nbsp;</div>";
   }
  
   if ( branch.tree.cssDots ) 
   {
     iconCell.className                   = "dTree_dots-branch-item";
     if ( branch.isFirst && branch.parent.id == branch.tree.id )
          iconCell.className              = "dTree_dots-branch-first-item";
     if ( branch.isLast ) 
          iconCell.className              = "dTree_dots-branch-last-item";
     if ( branch.isFirst && branch.isLast ) 
          iconCell.className              = "dTree_dots-branch-only-item";
   } 

   // CHECK BOX CELL

   var showCheck = ( branch.tree.checkboxes && (branch.checkboxes != false )) ? true : false ;
   if ( showCheck ) {
       var checkboxCell                   = branch.displayRow.insertCell(branch.displayRow.cells.length);
       checkboxCell.colSpan               = 1;
       branch.checkbox.control            = branch.checkbox.display();
       if ( branch.initStateChecked ) branch.checkbox.check();
       checkboxCell.appendChild(  branch.checkbox.control );
   }
   // TITLE CELL
   // If we have a display node, we insert that in the title cell;
   // if not, we use the tree label
   //
   branch.displayCell                     = branch.displayRow.insertCell(branch.displayRow.cells.length);
   branch.displayCell.colSpan = 1;
   branch.displayCell.setAttribute("width","96%");
   branch.displayCell.className           = branch.classNameStr;    
   if (branch.displayNode)
   {
      branch.displayNode                  = branch.displayCell.appendChild(branch.displayNode);
      if (branch.selectable)
      {
          branch.displayNode.style.cursor = "hand";  
          branch.displayNode.onclick      = branch.select;
          branch.displayNode.branch       = branch
      }          
   }     
      
   else
   {
      if (branch.selectable)
      {
         var labelSpan                    = document.createElement("a");
         labelSpan.branch                 = branch;

         // set the HREF properly, setting it to "#" will cause the page to move, potentially
         //
         if (branch.iconCheck == null)
           labelSpan.href                   = "#";
         else
           labelSpan.href                   = "javascript:";
	       labelSpan.className              = "dTree_label_anchor"; 
         labelSpan.innerHTML              = branch.label;
         labelSpan.onclick                = branch.select;
         labelSpan                        = branch.displayCell.appendChild(labelSpan);
         labelSpan.branch                 = branch;

      }
      else {
         branch.displayCell.innerHTML     = branch.label;
	 //         branch.displayCell.className     = branch.className; 
      }
      if (branch.itemCount)
      {
         labelSpan.innerHTML += "&nbsp;[" + branch.itemCount + "]";
      }
   }
   
   // Put the check.gif in place
   //
   if (branch.iconCheck != null)
   {
     var imageCheck             = document.createElement("img");
     imageCheck.src             = branch.iconCheck;
     imageCheck.style.display   = "none";
     imageCheck                 = branch.displayCell.appendChild(imageCheck);
     branch.imageCheck          = imageCheck;
   }

   branch.childRow                        = branch.table.insertRow(1);
   //branch.childRow.style.display          = "none";
 
   var stretchCell                        = branch.childRow.insertCell(branch.childRow.cells.length);
   stretchCell.colSpan = 1;
     stretchCell.innerHTML                = "<blank />";
  
   if ( ! branch.isLast && branch.tree.cssDots ) 
       stretchCell.className              = "dTree_dots-branch";

   branch.childDivCell                    =  branch.childRow.insertCell(branch.childRow.cells.length); //document.createElement("div");
   branch.childDivCell.colSpan            = ( branch.tree.checkboxes ) ? 2:1;

   branch.childDiv                        = document.createElement("div");
   branch.childDivCell.appendChild(branch.childDiv);

   // if checkboxes add extra cell here ( empty spacer ) 
   // 
   if ( ! showCheck )        
          branch.childDiv.colSpan           = 2;

   branch.childDiv.style.display            = "none";
   branch.childDiv.loaded                   = false;
   
   /*
   if (branch.canDrag)
   {
      branch.tree.dragDropRegistry.registerDrag(branch.openIcon, branch);
      branch.tree.dragDropRegistry.registerDrag(branch.closedIcon, branch);
   }
   
   if (branch.canDrop)
   {
      branch.tree.dragDropRegistry.registerDrop(branch.openIcon, branch, branch.dropType);
      branch.tree.dragDropRegistry.registerDrop(branch.closedIcon, branch, branch.dropType);
      branch.onDrop                 = branch.drop;
      branch.onDragOver             = branch.dragOver;
      branch.onDragOut              = branch.dragOut;
   }
   */
   
   branch.mainDiv.appendChild(branch.table);
   branch.appendChild(branch.mainDiv);
   // alert(branch.innerHTML);
   if ( showCheck ) {
     branch.checkbox.initState();
   }

   if ( script_tree.debug  > 3) alert( branch.innerHTML);

   if ( branch.initStateOpen ) branch.open();

   // Load branches as defined by object constructor
   //
   // if (branch.loadLevel && (branch.loadLevel > 0))  branch.loadChildren();

   branch.tree.resizeWindow();
}

function BranchOpen()
{
   var branch                              = this;
   
   // Show branches and switch icon
   //
   branch.childDiv.style.display           = "block";
   if ( branch.hasChildren ) {
     branch.closedIcon.style.display       = "none";  
     branch.openIcon.style.display         = "block";  
     branch.icon                           = branch.openIcon;
     if (! branch.childDiv.loaded  ) {
       branch.loadChildren();
     } else {
       branch.notify();
       ContinueOpeningToPath( this.tree );
     }
   } 

   //   if (branch.branches.length == 0)    branch.loadChildren();
}

function BranchClose()
{
   var branch                             = this;
   
   // Hide branches and switch icon
   //
   branch.childDiv.style.display          = "none";
   branch.openIcon.style.display          = "none";  
   branch.closedIcon.style.display        = "block";  
   branch.icon                            = branch.closedIcon;
   branch.notify();

}
function BranchNotify() 
{
   var branch                             = this;
   if (  this.tree.branchStateAction ) {
     nodeState = (  this.icon == this.closedIcon) ? "closed" : "open";
     params                                  = (this.tree.branchStateAction.indexOf("?") == -1 )? "?" : "&";
     params                                  += "treeId="      + this.tree.id     ;
     params                                  += "&sourceId="   + branch.sourceId  ;
     params                                  += "&sourceType=" + branch.type      ;
     params                                  += "&compId="     + this.tree.id +"_"+branch.type+"_"+ branch.sourceId  ;
     params                                  += "&nodeState="  + nodeState        ;

     if ( script_tree.debug  > 2 ) window.open( this.tree.branchStateAction + params, "test");
     branch.actionFrame.src                 =  this.tree.branchStateAction + params;
   }
}

function BranchLoadChildren()
{
   var branch                             = this;
   // Constuct base parameters passed to loadAction via query params
   //
   params                                  = (branch.loadAction.indexOf("?") == -1 )? "?" : "&";
   params                                  += "treeId="      + this.tree.id     ;
   params                                  += "&parentId="   + branch.id        ;
   params                                  += "&sourceId="   + branch.sourceId  ;
   params                                  += "&sourceType=" + branch.type      ;


   // Constuct additional parameters passed to loadAction via query params
   // these are provided by the branch originator object and is optional
   if ( branch.loadActionParams ) 
   {
      for ( var j = 0 ; j < branch.loadActionParams.length ; j++ ) 
      {
         if ( branch.loadActionParams[j] ) 
         {
            if ( branch.loadActionParams[j].indexOf(";") > -1 ) 
            {
               temp = branch.loadActionParams[j].split(";");
               if ( temp[0] != "" )
                  params += "&" + temp[0]+ "=" + escape(temp[1]);
           }
         }
      }
   }

   // Load constructed url in to branch's iFrame 
   //  
   if ( script_tree.debug  > 0 ) window.open( branch.loadAction + params, "test");
   

   branch.actionFrame.src                 = branch.loadAction + params;
   branch.childDiv.loaded                 = true;
}

function BranchUnloadAction()
{
   var branch                             = this;
   branch.actionFrame.src                 = "";
}

function BranchAddChild(id, label, type)
{
   var branch                                = this;
   
   var child                                 = new Branch(branch.tree, branch, id, label, type).branch;
   child                                     = branch.childDiv.appendChild(child);
   child.parent                              = branch;
   
   
   branch.tree.branches[branch.tree.branches.length]        = child;
   branch.tree.branchesById[child.id]                       = child;

   branch.branches[branch.branches.length]   =  child;
   branch.tree.resizeWindow();
   return child;
}

function BranchSelect()
{
   var branch                             = this.branch ? this.branch : this;
   
   // Unselect previous selection
   //
   if (branch.tree.highlightedBranch)
      branch.tree.highlightedBranch.highlight();
      
   // Highlight current selection
   //
   branch.highlight();    
   
   // Call integration
   //
   branch.selectAction();
}

function BranchToggle()
{
   var branch                             = this.branch;
   if (branch.childDiv.style.display == "none")
      branch.open();
   else
      branch.close();

   branch.tree.resizeWindow();
}

function BranchGetDisplayCell()
{
   var branch                             = this;
   return branch.displayCell;
}

function BranchHighlight()
{
   var branch                             = this.branch ? this.branch : this;

   if (branch.highlighted)
   {
      branch.highlighted                  = false;
      branch.displayCell.className        = branch.classNameStr;
      branch.tree.highlightedBranch       = null;
   }
   else
   {
      branch.highlighted                  = true;
      branch.displayCell.className        = branch.highlightedClass;
      branch.tree.highlightedBranch       = branch;
   }
}

function BranchCheckChildLoad(loadLevel)
{
   var branch                             = this;
   var firstNode                          = (loadLevel == branch.loadLevel)
   branch.loadLevel                       = loadLevel;
   if (branch.loadLevel > 0)
   {
      if (firstNode)
      {
         for (var i = 0; i < branch.branches.length-1; i++)
         {
            branch.branches[i].checkChildLoad(branch.loadLevel);
         }
      }
      else if(branch.loadLevel == 1)
      {
         branch.loadChildren();
      }
      else
      {
         for (var i = 0; i < branch.branches.length; i++)
         {
            branch.branches[i].checkChildLoad(branch.loadLevel - 1);
         }
      }
   }      
}


function BranchDragOver()
{
   var branch                             = this;
   branch.displayCell.className           = "dTree_dragover";
}

function BranchDragOut()
{
   var branch                             = this;
   branch.displayCell.className           = branch.className;
   return false;
}

function BranchDrop(movedElement, dropElement)
{
   var branch                             = this;
   
   if (dropElement == branch)
   {
     // proccessDragDropEvent should be a method provided by the hosting application 
     if ( proccessDragDropEvent ) {
       proccessDragDropEvent(movedElement,dropElement); 
     } 
   }
}

/*************************************************************
 **
 **
 **  Branch Key Strokes / Event Functions
 **
 **
 *************************************************************/
function BranchFocus() 
{
  return true;
}
function BranchBlur() 
{
  return true;
}
function BranchKeyPress(event) 
{  
  if ( ! event && window.event ) {
     event = window.event ;
  }
  if ( event ) 
     _keyCode = event.keyCode;
  else 
     return;

  if (  branchTargetKeyCode(_keyCode) ) {
    switch ( _keyCode ) {
       case 39: /* right arrow key */ 
        if ( this.hasChildren ) this.open();
        break;

       case 37: /* left arrow key */
        if ( this.hasChildren ) this.close();
        break;   

       /* testing tab and shift tab replacements */
       case 40: /* down arrow key */
       case 38: /* up arrow key */

    }
    event.cancelBubble = true;
    event.returnValue  = false;
    return false;
  } else {
    return true;
  }
}

function branchTargetKeyCode(_keyCode)
{
  if ( _keyCode == 38 ) return true;
  if ( _keyCode == 40 ) return true;
  if ( _keyCode == 37 ) return true;
  if ( _keyCode == 39 ) return true;
  return false;
}


//************************************************************************************
// this function is called from the iframes and is used torender child items
//
//
function InsertBranches(tree, parentId, branches)
{

  var tree                          = document.getElementById(tree);
  var parentBranch                  = tree.getBranchById(parentId);

  if (parentBranch && typeof(branches) == 'object') 
  {
    for ( var i = 0 ; i < branches.length ; i++ ) 
    {
      // Create child branch
      //
      var child                     = parentBranch.addChild(branches[i].id ,branches[i].label,branches[i].type);
     
      // base property copy
      child.classNameStr               = branches[i].classNameStr;

      child.hasChildren = 
        ( typeof branches[i].hasChildren == 'boolean' ) ? 
           branches[i].hasChildren : 
	  (branches[i].hasChildren == "true");

      child.initStateOpen  = 
        ( typeof branches[i].initStateOpen == 'boolean' ) ? 
           branches[i].initStateOpen : 
	  (branches[i].initStateOpen == "true");

      child.initStateSelected  = 
        ( typeof branches[i].initStateSelected == 'boolean' ) ? 
           branches[i].initStateSelected : 
	  (branches[i].initStateSelected == "true");

      child.initStateChecked  = 
        ( typeof branches[i].initStateChecked == 'boolean' ) ? 
           branches[i].initStateChecked : 
	  (branches[i].initStateChecked == "true");



      child.iconOpen                = branches[i].iconOpen;
      child.iconClosed              = branches[i].iconClosed;
      child.iconCheck               = branches[i].iconCheck;
      child.canDrag                 = branches[i].canDrag;
      child.canDrop                 = branches[i].canDrop;
      child.selectable              = branches[i].selectable;
      child.highlightedClass        = branches[i].highlightedClass;
      child.loadAction              = branches[i].loadAction;
      child.loadActionParams        = branches[i].loadActionParams;

      // Select Action is passed as a string reference to the target function
      // if it not contained in the parent it will create errors
      if (  branches[i].selectAction ) {
       if ( typeof eval( branches[i].selectAction) == "function")
          child.selectAction = eval( branches[i].selectAction);
      } else {
        child.selectAction            = parentBranch.selectAction;
      }

      // CHECKBOXES 
      // Checkbox Action is passed as a string reference to the targeted function
      // if it not contained in the parent it will create errors
      if ( branches[i].checkboxAction ) {
       if ( typeof eval( branches[i].checkboxAction) == "function")
           child.checkboxAction =  eval( branches[i].checkboxAction);
      } else {
        child.checkboxAction           = parentBranch.checkboxAction;
      }

      // some branches may want to disable the checkbox on there row
      if ( typeof   branches[i].checkboxes == "boolean" )
        child.checkboxes = branches[i].checkboxes; 

      // Used for the class references of the <TD> cells using the dotted images
      child.isFirst                   = ( i == 0 )?                 true : false;
      child.isLast                    = ( i == branches.length-1 )? true : false;

      if ( branches[i].mappedProperties ) 
      {
        for ( var j = 0 ; j < branches[i].mappedProperties.length ; j++ ) 
        {
          if ( branches[i].mappedProperties[j] ) 
          {
            if ( branches[i].mappedProperties[j].indexOf(";") > -1 ) 
            {
              temp = branches[i].mappedProperties[j].split(";");
              if ( temp[0] != "" )
                eval( "child."+ temp[0]+ "= \""+ temp[1] +"\"" );
                //alert ( eval( "child."+ temp[0]) );
            }
          }
        }
      }

      // SHOW CHILD BRANCH
      child.display();

    } // end for each branch loop 
    parentBranch = null;
  } // end if
  ContinueOpeningToPath(tree );
}

var branchItemsToOpenList = new Array();

function BranchOpenToPath( tree, ancestors ){

  branchItemsToOpenList         = ancestors;
  if ( tree ) {
    var branch                  = tree.getBranchById(tree.id+"_"+ancestors[0]);
    //alert ( branchItemsToOpenList.length );
    if ( branch ){
     if (  branchItemsToOpenList.length > 1  ) {
        branch.open();
        // if ( tree.highlightedBranch) tree.highlightedBranch.highlight();
        // branch.highlight();
        //      ContinueOpeningToPath(tree );
      } else {
        if ( tree.highlightedBranch) tree.highlightedBranch.highlight();
        branch.highlight();
        // alert(  branchItemsToOpenList[0] +"\n\n length" +  branchItemsToOpenList.length  );
      }
    }
  }
}
function ContinueOpeningToPath(tree ){
    //***  trim first item off array and repeat till all items are gone
  
      if ( branchItemsToOpenList ) {
        if ( branchItemsToOpenList.length > 1 ) {
          tempArray = new Array();
          for ( i = 1 ; i <  branchItemsToOpenList.length ; i++ ) {
            tempArray[i-1]      = branchItemsToOpenList[i];
          }
          branchItemsToOpenList = tempArray;
          BranchOpenToPath (tree, branchItemsToOpenList ) ;
        } else { 
          branchItemsToOpenList = new Array(); 
        } 
      }
      return;
}



/*************************************************************************
 *
 * Utility and convenience methods
 *
 ************************************************************************/
function findTreeById(str){
  
  if ( typeof (  eval ( str ) ) == "object" ) {
    return (  eval ( str ) );
  }
  return false;
}

function findBranchAncestors( tree, branchId, ancestors ) 
{
    if ( ancestors == null ) 
       var ancestors = new Array();
    var branch = tree.getBranchById(branchId);
    if ( branch ) {
      ancestors[ancestors.length] = branch;
      if ( branch.parent ) {
         findBranchAncestors( tree, branch.parent.id, ancestors )
      }
    }
    return ancestors ;  
}
function findBranchDescendants ( tree, branchId ) 
{
    var children= new Array();
    var i = 0 ;   
    while (  i  <  tree.branches.length  ) {
      var branch =  tree.branches[i]; 
      if ( branch.parent &&  branch.parent.id != null  ){
        var ancestors = findBranchAncestors( tree, branch.parent.id , null );
        for ( p = 0 ; p < ancestors.length ;p++ ) {
          if ( ancestors[p].id == branchId ) children[children.length] = branch ;
        }
      }
      i++;
    }
    return children;
}
function findBranchChildren( tree, branchId ) 
{
     var children= new Array();
     for ( var i = 0 ; i < tree.branches.length ; i++ ) {
       if ( tree.branches[i].parent ) {
         if ( tree.branches[i].parent.id ==  branchId ){
           children[children.length] = tree.branches[i];
         }
       }
     }
     return children ;  
}
function getBranchLevel( tree, branchId ) {

  if ( branchId == tree.id ) return 0;
  if ( branchId == null ) return -1 ;
  var levelArr =  findBranchAncestors( tree, branchId, null );
  if ( levelArr ) {
    return levelArr.length;
  } else {
    return -1;
  }
}

/*************************************************************************
 *
 * Check box Object and Methods
 *
 ************************************************************************/


function CheckBox( branch, tree, disabled , checked ) {


  this.control                            = "";
  this.checked                            = checked ;
  this.disabled                           = disabled ;

  this.branch                             = branch;
  this.tree                               = tree;
  this.disable                            = CheckboxDisable;
  this.enable                             = CheckboxEnable;
  this.onclick                            = CheckboxClick;
  this.check                              = CheckboxCheck;
  this.uncheck                            = CheckboxUncheck;

  this.display                            = CheckboxDisplay;
  this.initState                          = ( tree.checkboxInitState ) ? tree.checkboxInitState : null;
}

function CheckboxClick() 
{
   var box = this.checkbox;
   
   if ( this.checkbox.checked ) {
     this.checkbox.uncheck();
   } else {
     this.checkbox.check();
   }
   CheckboxApplyBehavior(box);
   if  ( typeof box.tree.checkboxOnclick == 'function' ) {
     box.tree.checkboxOnclick(box);
   }
}

function CheckboxDisable()
{
       var box = this;
       box.disabled                = true;
       box.control.disabled        = box.disabled;
}
function CheckboxEnable() 
{
       var box = this;
       box.disabled                = false;
       box.control.disabled        = box.disabled;
}
function CheckboxCheck() 
{
       var box = this;
       box.checked                 = true;
       box.control.defaultChecked  = box.checked;
}
function CheckboxUncheck() 
{
       var box = this;
       box.checked                = false;
       box.control.checked = box.checked;
}

function CheckboxDisplay() 
{
       var box = this;
       var checkboxInput                       = document.createElement("input");
       checkboxInput.type                      = "checkbox";
       checkboxInput.className                 = "dTree_tree_checkbox";
       checkboxInput.checkbox                  = box;
       checkboxInput.onclick                   = box.onclick;
       checkboxInput.disabled                  = box.disabled;
       checkboxInput.defaultChecked            = box.checked;
       box.control                             = checkboxInput;
       return box.control;
}

function isAncestorBranchSelected(box)
{
  var aParentIsSelected = false;
  var branchList =  findBranchAncestors( box.tree, box.branch.id, null );
  if ( branchList ) {
    for ( i = 0 ; i < branchList.length ; i++ ) {
      if ( branchList[i].checkbox.control.checked ) 
           aParentIsSelected = true;
    }
  }
  return aParentIsSelected;
}


function CheckboxStateInit()
{
   var box = this;
   inheritP   = hasCheckBehavior(box.tree, "inheritParent" );
   inheritA   = hasCheckBehavior(box.tree, "inheritAncestor" );
   ccDisable  = hasCheckBehavior(box.tree, "checkDisable" );
   ccMimic    = hasCheckBehavior(box.tree, "checkMimic"   );
   var modelNode = null;
   if ( box.branch.parent ){
     var ancestors = findBranchAncestors( box.tree, box.branch.parent.id , null );
     for ( p = 0 ; p < ancestors.length ;p++ ) {
       if ( p == 0 && inheritP && ( typeof  ancestors[p].checkbox.control == "object" )) {
         modelNode = ancestors[p];
       }
       if ( inheritA && ( typeof  ancestors[p].checkbox.control ==  "object" )) {
         modelNode = ancestors[p];
         break;
       }
     }
     if ( modelNode != null ) {
        if ( modelNode.checkbox.checked ) {
          if (ccMimic)    box.check();
          if (ccDisable ) box.disable();
        }
        if ( modelNode.checkbox.disabled ){
          if ( ccDisable ) box.disable();
        }
     }
   }
}
function CheckboxApplyBehavior(box)
{
   ccDisable    = hasCheckBehavior(box.tree, "checkDisable" );
   ccEnable     = hasCheckBehavior(box.tree, "checkEnable"  );
   ccMimic      = hasCheckBehavior(box.tree, "checkMimic"   );
   ccInvert     = hasCheckBehavior(box.tree, "checkInvert"  );
   uccDisable   = hasCheckBehavior(box.tree, "uncheckDisable" );
   uccEnable    = hasCheckBehavior(box.tree, "uncheckEnable"  );
   uccMimic     = hasCheckBehavior(box.tree, "uncheckMimic"   );
   uccInvert    = hasCheckBehavior(box.tree, "uncheckInvert"  );
   var children =  findBranchDescendants( box.tree, box.branch.id );
   if ( box.checked ) {
     for ( i = 0 ; i < children.length ; i++ ){
        if (ccMimic   )   children[i].checkbox.check() ;
        if (ccInvert  )   children[i].checkbox.uncheck() ;
        if (ccDisable )   children[i].checkbox.disable() ;
        if (ccEnable  )   children[i].checkbox.enable() ;
      }
   } else {
      for ( i = 0 ; i < children.length ; i++ ){
        if (uccMimic  )  children[i].checkbox.uncheck() ;
        if (uccInvert )  children[i].checkbox.check() ;
        if (uccEnable )  children[i].checkbox.enable() ;
        if (uccDisable)  children[i].checkbox.disable() ;
      }
   }
}
function hasCheckBehavior(tree, str)
{
  if ( tree.checkboxBehaviors == null ) return false;
  returnValue = false;
  for ( i=0 ; i < tree.checkboxBehaviors.length ; i++ ) {
    if ( tree.checkboxBehaviors[i] == str )  returnValue = true;   
  }
  return returnValue;
}
