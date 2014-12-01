// Media-Hive COMMON.JS Basic Javascript Functions //
// File Includes // 
/*

findPosY(obj)
findPosX(obj)
backUpToTag(t, targetTag)
addEvent(elm, evType, fn, useCapture)
removeEvent(elm, evType, fn, useCapture)
getElementsByClassName(className, doIframes)
percent(a, b)
cascadedstyle(el, cssproperty)
getPageScroll()
getPageSize()


*/
// LAST UPDATED JAN 2006 - Jon Sykes //


// FINDS THE Y POSITION OF A ELEMENT CROSS BROWSER //
function findPosY(obj) {
  
  var curtop = 0;
  if(obj){
    if (obj.offsetParent) {
      while (obj.offsetParent) {
        curtop += obj.offsetTop
        obj = obj.offsetParent;
      }
    }
    else if (obj.y)
      curtop += obj.y;
  }
  return curtop;

}


function findPosX(obj)
{
  var curleft = 0;
  if (obj.offsetParent)
  {
    while (obj.offsetParent)
    {
      curleft += obj.offsetLeft
      obj = obj.offsetParent;
    }
  }
  else if (obj.x)
    curleft += obj.x;
  return curleft;
}


// Crawl back up the DOM from where you are until you hit a specific tag you need
// Example Use: Finding the parent Table from an element you just clicked
// backUpToTag(currentRow, 'Table')


function backUpToTag(t, targetTag) {
  do {
    t = t.parentNode
  } while (t.nodeName.toUpperCase() !== targetTag.toUpperCase());
  return t;
}


// prevents firefixes quirk of finding text nodes on newlines and whitespace
// use like: nextChild=getNextSibling(child1);

function getNextSibling(startSibling) {
  var endSibling = startSibling.nextSibling;
  if (endSibling) {
    while (endSibling.nodeType != 1) {
      endSibling = endSibling.nextSibling;
    }
    return endSibling;
  } else {
    return false;
  }
}

/* STRING MANIPULATION FUNCTIONS */


// gets the last word, useful for classes
function getLastWord(string) {
  var a = string.split(/\s+/g); // split the sentence into an array of words
  return a[a.length - 1];
}

// Trims all whitespace from a string //
function trimString(str) {
  str = this != window? this : str;
  return str.replace(/^\s+/g, '').replace(/\s+$/g, '');
}


/* EVENT BASED FUNCTIONS */

function addEvent(elm, evType, fn, useCapture) {
  if (elm.addEventListener) {
    elm.addEventListener(evType, fn, useCapture);
  } else if (elm.attachEvent) {
    elm.attachEvent('on' + evType, fn);
  } else {
    elm['on' + evType] = fn;
  }
}

function removeEvent(elm, evType, fn, useCapture) {
  if (elm.removeEventListener) {
    elm.removeEventListener(evType, fn, useCapture);
  } else if (elm.detachEvent) {
    elm.detachEvent("on" + evType, fn);
  } else {
    alert("Handler could not be removed");
  }
}

// get an events trigger cross browser //

function getEventSrc(e) {
  // get a reference to the IE/windows event object
  if (!e) e = window.event;

  // DOM-compliant name of event source property
  if (e.target)
    return e.target;
  // IE/windows name of event source property
  else //if (e.srcElement)
    return e.srcElement;
}

/* TARGETTING FUNCTIONS */

// to find by className rather than unique ID would be cool
// so will use this function to do that, it'll generate an array
// containing references to all elements with a className
// it'll even recurse through all the frames/ iframes in a page

function getElementsByClassName(className) {
  var isDojo = (typeof (dojo) != "undefined") ? true : false;
  var elements;
  if (isDojo && window != window.top) {
    elements = dojo.html.getElementsByClass(className, document, "*");
  } else {
    elements = findElements(document, "*", className);
    // loop through all the frames and basically do the same as above in each one.
    for (var i = 0; i < window.frames.length; i++) {
      findElements(window.frames[i].document, "*", className, elements);
    }
  }
  // return the array of all elements that have that class in there class name attribute
  return elements;
}

/**
 * Finds all subelements with given tag and class name.
 * @param pParent Search starting with element specified by this parameter.
 * @param pTagName Search within elements with given tag name. You may use "*" to find all elements.
 * @param pClassName Class name to find.
 * @param pElements Optional array to fill found elements. New array will be created if it's null or undefined.
 * @return Array of found elements.
 */
function findElements(pParent, pTagName, pClassName, pElements) {
  if (!pElements) {
    pElements = new Array();
  }
  // loop through all of the found tags and see if any of them have a child with teh right class name
  var children = pParent.getElementsByTagName(pTagName) || pParent.all;
  for (var i = 0; i < children.length; i++) {
    var child = children[i];
    var classNames = child.className.split(' ');
    for (var j = 0; j < classNames.length; j++) {
      if (classNames[j] == pClassName) {
        // add them to the elements array
        pElements.push(child);
        break;
      }
    }
  }
  return pElements;
}

// bascic percentage with rounding function (used in the positioning scripts)
function percent(a, b) {
  var c = a / 100;
  var d = Math.round(c * b);
  return d - 1;
}


// Cascaded Style, finds style attribute values using JS that
// have not been set either inline or with JS.
// i.e. it will find styles from the css, using JS.

function cascadedstyle(el, cssproperty) {

  // current style is an IE only function
  // Represents the cascaded format and style of the object as specified
  // by global style sheets, inline styles, and HTML attributes.
  if (el.currentStyle)
  {
    return el.currentStyle[cssproperty]

    //
  } else //if (window.getComputedStyle)
  {

    // Gecko borwsers require the css property to be split with a '-' so
    // backgroundColor becomes background-color so we use a quick regex to achive this.
    var replacement = '$1-$2';
    var regex = '([^/s$])([A-Z])';
    var re = new RegExp(regex, "g");
    var csspropertyNS = cssproperty.replace(re, replacement);

    // Make it all lowercase - I don't think this matters but it's 'supposed' to be this way
    csspropertyNS = csspropertyNS.toLowerCase();

    var elstyle = window.getComputedStyle(el, "")
    return elstyle.getPropertyValue(csspropertyNS)
  }
}



// Returns array with x,y page scroll values.//

function getPageScroll() {

  var yScroll;

  if (self.pageYOffset) {
    yScroll = self.pageYOffset;
  } else if (document.documentElement && document.documentElement.scrollTop) {  // Explorer 6 Strict
    yScroll = document.documentElement.scrollTop;
  } else if (document.body) {// all other Explorers
    yScroll = document.body.scrollTop;
  }

  return new Array('', yScroll)
}


// Returns array with page width, height and window width, height //

function getPageSize() {

  var xScroll, yScroll;

  if (document.body.scrollHeight > document.body.offsetHeight) { // all but Explorer Mac
    xScroll = document.body.scrollWidth;
    yScroll = document.body.scrollHeight;
  } else { // Explorer Mac...would also work in Explorer 6 Strict, Mozilla and Safari
    xScroll = document.body.offsetWidth;
    yScroll = document.body.offsetHeight;
  }

  var windowWidth, windowHeight;
  if (self.innerHeight) { // all except Explorer
    windowWidth = self.innerWidth;
    windowHeight = self.innerHeight;
  } else if (document.documentElement && document.documentElement.clientHeight) { // Explorer 6 Strict Mode
    windowWidth = document.documentElement.clientWidth;
    windowHeight = document.documentElement.clientHeight;
  } else if (document.body) { // other Explorers
    windowWidth = document.body.clientWidth;
    windowHeight = document.body.clientHeight;
  }

  // for small pages with total height less then height of the viewport
  var pageHeight = yScroll;
  if (yScroll < windowHeight) {
    pageHeight = windowHeight;
  }

  // for small pages with total width less then width of the viewport
  var pageWidth = xScroll;
  if (xScroll < windowWidth) {
    pageWidth = windowWidth;
  }


  return new Array(pageWidth, pageHeight, windowWidth, windowHeight)
}


// Show and Hide content based on dropdown value

function selectSwitch(selectObject) {
  var options = selectObject.options;
  for (var i = 0; i < options.length; i++) {
    var value = options[i].value;
    var element = document.getElementById(selectObject.id + value);
    if (element && value.length > 0) {
      element.style.display = (selectObject.selectedIndex != i)? "none" : "";
    }
  }
}

// Show and Hide content based on dropdown value

function radioSwitch(radioObject) {
  var currentSelection = radioObject.value;
  var radioFamilyID = radioObject.parentNode.parentNode.id;
  if (radioFamilyID) {
    var options = radioObject.parentNode.parentNode.getElementsByTagName('input');
    var m = 0;
    for (var l = 0; l < options.length; l++) {
      if(options[l].type == "hidden"){
        m++;
      }
    }
    var k, i = options.length - m;
    do {
      document.getElementById(radioFamilyID + i).style.display = (currentSelection != i)? "none" : "";
    }
    while (--i);
  }
}

// Add a function called trim as a method of the prototype
// object of the String constructor.
String.prototype.trim = function() {
   // Use a regular expression to replace leading and trailing
   // spaces with the empty string
   return this.replace(/(^\s*)|(\s*$)/g, "");
}

//adds param to url. Three variants of url format are allowed:
//1. test.com?
//2. test.com?param1=value1
//3. test.com
function addParamToUrl(url, paramName, paramValue){
  var enquiryIndex = url.indexOf('?');
  if (enquiryIndex  != -1){
    if(enquiryIndex == url.length - 1){
      //test.com?
      url = url + paramName + "=" + paramValue;
    } else {
      //test.com?param1=value1
      url = url + '&' + paramName + "=" + paramValue;
    }
  } else {
    //test.com
    url = url + "?" + paramName + "=" + paramValue;
  }
  return url;
}
//adds given class to object className
function addClassToElement(element, className){
  if (element.className.length > 0){
    if (element.className.search(className) == -1){
      if (element.className.lastIndexOf(" ") != element.className.length - 1){
         element.className += " ";
      }
      element.className += className;
    }
  } else {
    element.className = className;
  }
}

/**
 * Finds all subelements with given tag and name.
 * @param pParent Search starting with element specified by this parameter.
 * @param pTagName Search within elements with given tag name. You may use "*" to find all elements.
 * @param pName Name elements to find.
 * @param pElements Optional array to fill found elements. New array will be created if it's null or undefined.
 * @return Array of found elements.
 */
function findElementsByName(pParent, pTagName, pName, pElements) {
  if (!pElements) {
    pElements = new Array();
  }
  // loop through all of the found tags and see if any of them have a child with teh right class name
  var children = pParent.getElementsByTagName(pTagName) || pParent.all;
  for (var i = 0; i < children.length; i++) {
    var child = children[i];
    if (child.name == pName) {
      // add them to the elements array
      pElements.push(child);
    }
  }
  return pElements;
}

function setChildCheckboxesState(pContainerId, pCheckboxesName, pChecked, pFunc) {
  var container = document.getElementById(pContainerId);
  var elements = findElementsByName(container, "input", pCheckboxesName);
  for (var i = 0; i < elements.length; i++) {
    elements[i].checked = pChecked;
    if (typeof pFunc == "function") {
      pFunc(elements[i]);
    }
  }
  return true;
}

function getChildCheckboxesState(pContainerId, pCheckboxesName) {
  var container = document.getElementById(pContainerId);
  var elements = findElementsByName(container, "input", pCheckboxesName);
  var allChecked = true;
  for (var i = 0; i < elements.length; i++) {
    if (!elements[i].checked) {
      allChecked = false;
      break;
    }
  }
  return allChecked;
}

//Javascript, used to switch content and navigation tabs.
//This method was added by Epam.
function switchContent(num, contentPrefix) {
  var navPane = document.getElementById("subNav");
  var navPaneElements = navPane.getElementsByTagName("li");
  for (var i = 1; i <= navPaneElements.length; i++) {
   navPaneElements[i - 1].className = i == num ? "current" : "";
   var el = document.getElementById(contentPrefix + i);
   if (el) {
     el.style.display = i == num ? "" : "none";
   }
  }
  return false;
}

/**
 * Allow to check field name on-the-fly very carefully.
 * It's required for enabling/disabling submit buttons, for example.
 * Example of usage:
 *   new ExactFieldValueObserver(document.getElementById("termInput"),
 *     function(value) {document.getElementById("lookupSubmit").disabled = (trimString(value).length == 0);});
 * @param field field element to observe, like document.geteElementById('field_id')
 * @param onchange function, which is called every time when field's value is changed
 */
function ExactFieldValueObserver(field, onchange) {
  this.field = field;
  this.onchange = onchange;
  var self = this;
  this.check = function() {
    if (self.value != self.field.value) {
      self.value = self.field.value;
      self.onchange(self.value);
    }
    window.setTimeout(self.check, 100);
  }
  this.value = null;
  this.check();
}

/**
 * Helper function which turn on enabling/disabling the specified button based on the specified field value.
 * @param buttonId button id for enabling/disabling
 * @param fieldId field id, which value is observing
 */
function disableButtonWhenFieldIsEmpty(buttonId, fieldId) {
  var button = document.getElementById(buttonId);
  var field = document.getElementById(fieldId);
  new ExactFieldValueObserver(field, function(v) {button.disabled = (trimString(v).length == 0);});
}
