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
