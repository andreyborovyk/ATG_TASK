/* DROP DOWN MENU CREATOR */

function initDropDownMenus(iframeURL, frameClass, triggerClass, blankURL){
  
  // make an array of all iconDD class objects
  // PR 151807: Do not check iframes
  //iconDD = getElementsByClassName(triggerClass, true);
  iconDD = getElementsByClassName(triggerClass);
   
  // if the array contains objects carry on
  if (iconDD.length !== 0){
    
    // create the iframe that will be used in each example, we'll be accessing this using the 
    // document.getElementById('testiframe').src method, so it'll need it's own ID
    
    var iframeDD = document.createElement('IFRAME');
    var divDD = document.createElement('DIV');
    
    iframeDD.name = "dropdowniframe";
    
    iframeDD.style.display = 'none';
    divDD.style.display = 'none';
    iframeDD.className = frameClass;
    iframeDD.id = "iframeDD"+triggerClass;
    iframeDD.URL = iframeURL;
    iframeDD.src = blankURL;
    //alert(triggerClass);
    divDD.id = "divDD"+triggerClass;
    divDD.className = "parent"+triggerClass;

    
    // allowtransparency="true"
    var z = document.createAttribute('allowtransparency');
    var y = document.createAttribute('frameborder');
    var x = document.createAttribute('scrolling');
    x.value = 'no';
    y.value = '0';
    z.value = 'true';
    iframeDD.setAttributeNode(z);
    iframeDD.setAttributeNode(y);
    iframeDD.setAttributeNode(x);
    
    document.body.appendChild(divDD);
    
    
    document.getElementById('divDD'+triggerClass).appendChild(iframeDD);
   
   
    for(var i=0; i<iconDD.length; i++){
   	
      addEvent(iconDD[i], 'click', showDD, false);
   	
      // After changelist 395221, we must discontinue support for predefined
      // hrefs (or rewrite the code below so it can handle href="#").
      //if(iconDD[i].href){
      //  var urlString = iconDD[i].href;
      //  iconDD[i].whichMenu = urlString.substring(urlString.lastIndexOf('/') + 1, urlString.lastIndexOf('.'));
      //
      //  iconDD[i].href = "#";
      //}else{
        iconDD[i].whichMenu = iconDD[i].id;
        iconDD[i].href = "#";

      //}
   	  
      //addEvent(iconDD[i], 'mouseout', checkDD, false);

    }

  }

}
/* END OF DROP DOWN CREATOR */

var currentTarg = null;


function showDD(e){
//alert("e: "+e);
  var targ;
	if (!e){ var e = window.event;}
	if (e.target){ targ = e.target;}
	else if (e.srcElement){ targ = e.srcElement;}
	if (targ.nodeType == 3){// defeat Safari bug
	targ = targ.parentNode;
	}
	
	var thisClassValue = targ.className;
	var theClassNames = thisClassValue.split(' ');
	var thisClass = theClassNames[theClassNames.length-1];
	
	
	targX = findPosX(targ)-8+"px";
	targY = findPosY(targ)+targ.offsetHeight+"px";
	

	if (targX != currentTarg){	
	
	if (actionsAreLocked()) 
          return false;

    theIframe = document.getElementById('iframeDD'+thisClass);
    
    theDivDD = document.getElementById('divDD'+thisClass);
    
    
    theDivDD.style.position = "absolute";
    theDivDD.style.top = targY;
    theDivDD.style.left = targX;
    theDivDD.style.display = "block";
    
    theIframe.style.display = "block";  
    theIframe.src = theIframe.URL+"?section="+targ.whichMenu;
    
    addEvent(document.body, 'mousedown', closeDD, true);
    addEvent(window.frames['rightPaneIframe'].document.body, 'mousedown', closeDD, true);
    addEvent(window.frames['scrollContainer'].document.body, 'mousedown', closeDD, true);
  
    currentTarg = (currentTarg == targX)? null : targX;

  }else{
  
    currentTarg = null;
 
 } 

	return false;  

}



function closeDD(e){
  var targ;
	if (!e) e = window.event;
	if (e.target) targ = e.target;
	else if (e.srcElement) targ = e.srcElement;
	if (targ.nodeType == 3){// defeat Safari bug
	targ = targ.parentNode;
	}


  var openTagClass = /DD/;
  
  if (targ.className.match(openTagClass)){
  
  
  }else{

  currentTarg = null;

  }

  removeEvent(document.body, 'mousedown', closeDD, true);
  removeEvent(window.frames['rightPaneIframe'].document.body, 'mousedown', closeDD, true);
  removeEvent(window.frames['scrollContainer'].document.body, 'mousedown', closeDD, true);

  theIframe.style.display = "none";
  theDivDD.style.display = "none";
  
  // PR 151807: Do not check iframes
  //var iconDD = getElementsByClassName('divDDClass', true);
  var iconDD = getElementsByClassName('divDDClass');
  

  if (iconDD.length !== 0){
  
    for(var i=0; i<iconDD.length; i++){
  
    iconDD[i].style.display = 'none';
    }
  }
}
