/**
 * eStara click-to-connect call initiation library.
 * 
 * @version $Id: //product/ClickToConnect/version/10.0.3/src/web-apps/clicktoconnect/scripts/c2c.js#1 $$Change: 651360 $
 * @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
 */

// First we load the eStara JavaScript library onload of this
// JavaScript library.
if(typeof(window.addEventListener)!='undefined'){
  window.addEventListener('load',_atg_estara_loadlr,false);
}else if(typeof(document.addEventListener)!='undefined'){
  document.addEventListener('load',_atg_estara_loadlr,false);
}

else if(typeof(window.attachEvent)!='undefined'){
  window.attachEvent('onload',_atg_estara_loadlr);
}


// Loads the eStara javascript.
function _atg_estara_loadlr(){
  try{
    var s=document.createElement('script');
    s.setAttribute('type','text/javascript');
    s.setAttribute('src',_atg_estara_js_src);
    s.setAttribute('charset','UTF-8');
    if(typeof(window.attachEvent)!='undefined')
      document.body.appendChild(s);
    else 
      document.getElementsByTagName('head').item(0).appendChild(s);
  }catch(e){}
}


// This function is called by eStara RuleBuilder whenever a call is initiated.
// One should configure a RunJavascript rule, which will call this function 
// when a customer clicks on a call initiation button.
// 
// Rule builder allows you to create a "RunJavascript" rule that will run 
// arbitrary JavaScript when the GUI is popped up.  You can set it up by 
// setting the Rule Type of a rule to "RunJavascript:, then setting 
// Argument 2 to "0".  Argument 1 is the javascript you want RuleBuilder 
// to call.  So, that should be configured to call clickToConnectSave().
// eStara will then call this function, which will make a callback to the
// ATG server to allow it to persist any customer data for later retrieval.
//
function clickToConnectSave()
{

  _issueRequest("../../../clicktoconnect/save?_atg_estara_call_token="+encodeURIComponent(_atg_estara_call_token)+"&_atg_estara_site="+encodeURIComponent(_atg_estara_site)+"&pushSite="+encodeURIComponent(_atg_estara_site));
}

//----------------------------------------------------------------------------//
//
// Issue a request using an XMLHttpRequest object.
// (This function is not guaranteed to remain available in future releases)
//
function _issueRequest(url) {

  var xmlHttpRequestObj = _createXMLHttpRequestObject();
  if (!xmlHttpRequestObj) {
    alert("Error: Can't create XMLHttpRequest");
    return;
  }
  try {

    xmlHttpRequestObj.open("POST", url, true);
    xmlHttpRequestObj.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

    xmlHttpRequestObj.onreadystatechange = function() {
      if (xmlHttpRequestObj.readyState == 4) {
        //do nothing
        
      }
    }

    xmlHttpRequestObj.send(null);
  }
  catch (e) {
    alert("Error: XMLHttpRequest failed: " + e);
  }
}

//----------------------------------------------------------------------------//
//
// Attempt to create an XMLHttpRequest object.
// (This function is not guaranteed to remain available in future releases)
//
function _createXMLHttpRequestObject() {

  if (typeof XMLHttpRequest != "undefined") {
    // Browsers such as Firefox and IE7 include XMLHttpRequest objects in JavaScript.
    return new XMLHttpRequest();
  }
  else if (typeof ActiveXObject != "undefined") {
    // On IE6, XMLHttpRequest is implemented using ActiveX.
    try {
      return new ActiveXObject("Msxml2.XMLHTTP");
    }
    catch (e1) {
      try {
        return new ActiveXObject("Microsoft.XMLHTTP");
      }
      catch (e2) {}
    }
  }
  return null;
}


// $Id: //product/ClickToConnect/version/10.0.3/src/web-apps/clicktoconnect/scripts/c2c.js#1 $$Change: 651360 $
