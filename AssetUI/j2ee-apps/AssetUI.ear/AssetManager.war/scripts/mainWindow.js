/* INIT FUNCTIONS TO APPLY FUNCTIONAL BEHAVIOR BASED ON CONTENT */

// Set the height of all the content in the browser to fit within the viewable window

function fixDivHeight() {

        // Cross Browser find the inner height of the browser window.

        if (self.innerHeight)
        {
                browserHeight = self.innerHeight;
        }
        else if (document.documentElement && document.documentElement.clientHeight)
        {
                browserHeight = document.documentElement.clientHeight;
        }
        else if (document.body)
        {

                browserHeight = document.body.clientHeight;
        }


  //Set some Varianbles


        var topSpace = findPosY(document.getElementById('rightPane'));
        //alert(topSpace);
        var contentHeight = (browserHeight-topSpace)-25;
        var heightCap = 445; // Minimum height of the right iFrame

        if (heightCap < browserHeight){

        document.body.style.overflow = 'hidden';

        }


        // is there a right pane iframe
        if (document.getElementById('rightPaneIframe')){

    var rightPaneIframe = document.getElementById('rightPaneIframe');
    var rightPaneScroll = parent.rightPaneIframe.document.getElementById('rightPaneContent');
    var leftPaneScroll = document.getElementById('scrollContainer');
    var topFade = (parent.rightPaneIframe.document.getElementById('topFade'))?  parent.rightPaneIframe.document.getElementById('topFade') : "null";

    document.getElementById('rightPaneIframe').style.height = contentHeight+'px';

    var leftAdditionalHeight = (document.getElementById('scrollFooter').offsetHeight);

    //alert(leftAdditionalHeight);
    /*
    if (document.getElementById('assetListSubHeader')){
    leftAdditionalHeight =leftAdditionalHeight+25;
    }*/
    //leftPaneScroll.style.height = contentHeight+ "px";
    //document.getElementById('leftPane').style.height = contentHeight+'px';

    leftHeight = contentHeight-leftAdditionalHeight-25;
    document.getElementById('scrollContainer').style.height = leftHeight+'px';

                if(document.getElementById('assetListSubHeader')){
      leftHeight = contentHeight-leftAdditionalHeight-document.getElementById('assetListSubHeader').offsetHeight-50;
                        document.getElementById('scrollContainer').style.height = leftHeight+'px';
                }

                if(document.getElementById('leftPaneToolbar') && !document.getElementById('assetListSubHeader')){
      leftHeight = contentHeight-leftAdditionalHeight-document.getElementById('leftPaneToolbar').offsetHeight-25;
                        document.getElementById('scrollContainer').style.height = leftHeight+'px';
                }


    // Set the right panel height to fill browser, if it isn't smaller than
    // the fail safe height

    if (rightPaneIframe.offsetHeight < heightCap) {
      rightPaneIframe.style.height = heightCap + "px";
      leftPaneScroll.style.height = heightCap + "px";
    }


    // Set initial size of the content div within right panel
    // iFrame Based on elements that are present

    if (parent.rightPaneIframe.document.getElementById('subNav')) {
      var iFrameSubNav = parent.rightPaneIframe.document.getElementById('subNav');
    }

    if (parent.rightPaneIframe.document.getElementById('rightPaneScrollFooter')) {
      var iFrameActionButtons = parent.rightPaneIframe.document.getElementById('rightPaneScrollFooter');
    }

    if (parent.rightPaneIframe.document.getElementById('rightPaneHeader')) {
      var iFrameHeader = parent.rightPaneIframe.document.getElementById('rightPaneHeader');
    }


    if (iFrameSubNav && iFrameActionButtons && iFrameHeader && rightPaneScroll) {

      rightPaneScroll.style.height = rightPaneIframe.offsetHeight - iFrameActionButtons.offsetHeight - iFrameSubNav.offsetHeight - iFrameHeader.offsetHeight + "px";

    } else if (iFrameSubNav && !(iFrameActionButtons)){
      rightPaneScroll.style.height = rightPaneIframe.offsetHeight - iFrameSubNav.offsetHeight - iFrameHeader.offsetHeight + "px";

    } else if (iFrameActionButtons && !(iFrameSubNav)){
      rightPaneScroll.style.height = rightPaneIframe.offsetHeight - iFrameActionButtons.offsetHeight - iFrameHeader.offsetHeight + "px";

    } else if (!iFrameHeader){

    }else if (rightPaneScroll) {
      rightPaneScroll.style.height = rightPaneIframe.offsetHeight - iFrameHeader.offsetHeight + "px";
    }


    // Set top position content gradient fade to the top position of the content div


    if (topFade != "null" && rightPaneScroll){

    topFade.style.top = rightPaneScroll.offsetTop + "px";
    topFade.style.display = "block";

  }

    // If the bottom left panel is present, set it's minimum
    // allowable height based on the height of the right iFrame

    if(document.getElementById('leftPaneFooterSubNavContent')) {
      var leftPaneBottomScroll = document.getElementById('leftPaneFooterSubNavContent');
      var	leftSubNavHeight = document.getElementById('leftPaneFooterSubNav').offsetHeight;
      var leftFooterButtons = document.getElementById('scrollFooter');
      var leftHeader = document.getElementById('assetListHeader');
      var leftContent = document.getElementById('scrollContainer');

      if (rightPaneIframe.offsetHeight > heightCap) {
        leftBottom = contentHeight-leftFooterButtons.offsetHeight-leftHeader.offsetHeight-leftContent.offsetHeight-leftSubNavHeight + leftPaneBottomScroll.offsetHeight + 4;
        leftPaneBottomScroll.style.height = leftBottom+'px';

      } else if (rightPaneIframe.offsetHeight == heightCap) {
        leftPaneBottomScroll.style.height = heightCap-leftFooterButtons.offsetHeight-leftHeader.offsetHeight-leftContent.offsetHeight-leftSubNavHeight + leftPaneBottomScroll.offsetHeight + 4 + "px";
      }

    }

  // no right hand iframe / check to see if there is the div 'rightpaneContent'
  }else if(document.getElementById('rightPaneContent')){

  // for pages that aren't in the two column layout, and have just one pane for content.
  refRightPaneContent = document.getElementById('rightPaneContent');
  rightPaneContentY = findPosY(document.getElementById('rightPaneContent'));
  // difference between Y position and height of the window, - an amount for the bottom margin.
  refRightPaneContent.style.height = browserHeight - rightPaneContentY - 25+"px";

  }else{
  // can't find elements with ID of rightPaneIframe, or rightPaneContent
  // don't do any resizing
  // or we can add further versions

  }

initScreen('leftPane');
}


/* DISPLAY TOGGLE SCRIPTS */
// Toggle styles of a given element with the id boxid
// between onclass and offclass

function displayToggle(boxid, onclass, offclass, passFunction){
// erro catching
try{

          var element;
         // get the element in both the main document and the rightPanelIframe
         // next release look at recursion to cover all future possible iFrames
         if(document.getElementById(boxid)){

      element = document.getElementById(boxid);

   }else if(parent.rightPaneIframe.document.getElementById(boxid)){

      element = parent.rightPaneIframe.document.getElementById(boxid);

   }

         if(element){

    //toggle the class
    element.className =(element.className == offclass)? onclass : offclass;
         fixDivHeight();

    return false;

         }
  }catch(e){
    /* debug error handling here */
  }
}



// set up the event trigger for the toggle states
// eventType - click, dblclick, mouseover, mouseoff, etc, etc...
// eventId - the id of the element that the event will be added to
// targetId - the ID of the element that will be targeted by the event
// onClass - the onClass name
// offClass - the offClass name
// (the last two are the toggle class values)

function initToggleStates(eventType, eventId, targetId, onClass, offClass){
try{

    var element;
    // get the element ID
    if(document.getElementById(eventId)){

      element = document.getElementById(eventId);

    }else if(parent.rightPaneIframe.document.getElementById(eventId)){

      element = parent.rightPaneIframe.document.getElementById(eventId);
    }

    if(element){

        // Get the current onclick events (just to prevent overwriting)
        var currentEvents = (element.onclick) ? element.onclick : function () {};

        // Browser supports attacheEent, probably IE, so lets do things the
        // none DOM way, and use old skool techniques.
        if(document.attachEvent){

            element.attachEvent("on"+eventType, function () {currentEvents(); displayToggle(targetId, onClass, offClass);}, true);


        }

        // Browser allows DOM functionality, so do it the proper way
        if(document.addEventListener){

            element.addEventListener(eventType, function () {currentEvents(); displayToggle(targetId, onClass, offClass);}, true);

         }

    }else{
     //no element found, debug error handling here
    }

  }catch(e){
    /* function error handling */
  }
}


/* THESE FUNCTION ARE NOT USED IN THE IMPLEMENTATION */

// Initialise the Asset Browser event trigger on all a tags with the class abTrigger

function initAssetBrowserEvents(w){
try{
    var eventType = "click";

    try{

        //loop thru page and see if class exists

        currentLocal = w.document.getElementsByTagName('a');

        for (var i = 0; i < currentLocal.length; i++) {

          var currentLink = currentLocal[i];

          // if you find an element with the right class name (anywhere in the class names)
          if ( currentLink.className.indexOf('abTrigger') != -1) {

            var currentEvents = (currentLink.onclick) ? currentLink.onclick : function () {};

            if(document.attachEvent){
            //IE do the IE thang

              currentLink.attachEvent("on"+eventType, function () {currentEvents(); showIframe('browser');}, true);

            }

            if(document.addEventListener){
            // Do the DOM thing

            currentLink.addEventListener(eventType, function () {currentEvents(); showIframe('browser'); this.blur();}, true);
            }
          }
        }
        // we want to cover all future frames/ and iframes depths (just in case)
        // recurse through all frames and apply this function to all of them
        // Note this might not be a great idea with the tree (and all it's iframes)
        // this could be easily fixed with a conditional though.
        for(var j=0; j<w.frames.length; j++){
            initAssetBrowserEvents(w.frames[j]);
        }
    }catch(e){
        /* error handling */
    }

  }catch(e){
    /* error handling */
  }
}



// INITIALISE THE FADER OBJECTS

function initFader(className, color2fade2, property2fade, stepsinfade)
{

  // get all objects in every frame with the trigger classname
  allTheElements = getElementsByClassName(className, true);

  // loop through all those elements
  for (var t=0; t<allTheElements.length; t++){

    // get the background CSS color for the curent element
     var BG = cascadedstyle(allTheElements[t], property2fade);


    // parse the color to a usable format
    //(default to white if the color matches the start fade color)
    var parsedBG = (parseColor(BG) == color2fade2)? "FFFFFF" : parseColor(BG);

    // make a new object in the current element with the right variables
    allTheElements[t].fader = new fader(color2fade2, parsedBG , stepsinfade, property2fade, allTheElements[t]);

    // start the process
    allTheElements[t].fader.highlightStyle();
  }
}
/* END OF THE COLOR FADE FUNCTIONS */



/* SCREEN INIT CODE - BETA VERSION FOR TEMPLATE WALK THROUGH */


function initScreen(target){

  if (document.getElementById('theScreen')){

    var theSreenDiv =  document.getElementById('theScreen');


    target=document.getElementById(target);
    document.getElementById('theScreen').style.top = findPosY(target)+"px";
    document.getElementById('theScreen').style.left = findPosX(target)+"px";

    document.getElementById('theScreen').style.width = document.getElementById(target).offsetWidth+"px";
    document.getElementById('theScreen').style.height = document.getElementById(target).offsetHeight+"px";

    document.getElementById('theScreen').style.display = "block";


  }
}

// END OF SCREEN CREATION FUNCTION





// Truncate Text

function truncateSearchResults(){
        if(document.getElementById('limitText')){
                var truncateCap = 25;
                var searchContainer = document.getElementById('limitText');
                var searchResult = searchContainer.firstChild;
                if(searchResult.nodeValue.length>=truncateCap){
                        searchResult.nodeValue = searchResult.nodeValue.substring(0,truncateCap)+'...';
                        searchContainer.style.display = "inline"
                }
        }
}





// Get the party started

//function init(){
function createEventHandlers(blankURL){




    // Fix the layout to full page height

    // JV: disable sizing for now...
    //fixDivHeight();

    // SET ALL THE TOGGLE STATE BEHAVIORS

    // Add Browser Launch Code to all triggerAB classed links
    // JV: This is not needed since buttons are explictly associated with actions.
    //initAssetBrowserEvents(window);

    // Add all click behaviors to form legends to show/hide their child form table
    //initFormToggle(window);


    // initToggleStates(eventType, eventId, targetId, onClass, offClass)


    // Drop Down Menu Toggle Visible
    //initToggleStates('click','newButton','newDropDown', 'newDropDownHide', 'newDropDownShow');
    //initToggleStates('click','newButton','newButton', 'newButtonOff', 'newButtonOn');

    // Toggle the page info speech bubble
    initToggleStates('click','toggleDetails','box', 'noBox', 'box');
    initToggleStates('click','closePageInfo','box', 'noBox', 'box');

    // Page Info Button, toggle state
    initToggleStates('click','toggleDetails','toggleDetails', 'toggleDetailsOff', 'toggleDetailsOn');
    initToggleStates('click','closePageInfo','toggleDetails', 'toggleDetailsOff', 'toggleDetailsOn');


    // error pages, toggle details, inits

    initToggleStates('click','errorDetailsExpand','errorDetails', 'errorDetailsOff', 'errorDetailsOn');
    initToggleStates('click','errorDetailsExpand','errorDetailsIconOff', 'errorDetailsIconOff', 'errorDetailsIconOn');
    initToggleStates('click','errorDetailsIconOff','errorDetails', 'errorDetailsOff', 'errorDetailsOn');
    initToggleStates('click','errorDetailsIconOff','errorDetailsIconOff', 'errorDetailsIconOff', 'errorDetailsIconOn');


    // error at the top of the page

   initToggleStates('click','errorClose','error', 'errorShow', 'errorHide');
   initToggleStates('click','errorClose','errorClose', 'errorCloseShow', 'errorCloseHide');
   initToggleStates('click','error','errorDetail', 'errorShow', 'errorHide');




       // JV: disable sizing for now...
       //oIframe = document.getElementById('rightPaneIframe');

       //if(document.attachEvent){
            //IE do the IE thang
            //oIframe.attachEvent('onload', function () {init();});

        //}

        //if(document.addEventListener){
            // Do the DOM thing (well not quite, but this works on DOM browsers
            //oIframe.onload = function () {init();};
        //}



    // PR 151807: Since this function calls getElementsByClassName with the
    // iframe-traversal flag set to true, we can't enable this feature.
    //initFader('newItem', 'FFFFCD', 'backgroundColor', 20);

    //initDropDownMenus('MultiEdit_dropDown_iframe.jsp', 'dropdown', 'iconDD', 'contentTable');
    // We use a different URL for this iframe.
    initDropDownMenus('components/dropDownIframe.jsp', 'dropdown', 'iconDD', blankURL);
    initDropDownMenus('components/dropDownIframe.jsp', 'dropdownlink', 'linkDD', blankURL);

    //truncateSearchResults();
}

// Browser check that it can do the DOM

// JV: disable sizing for now...  and invoke init handlers explicitly.
//if (document.getElementById && document.getElementsByTagName && document.createTextNode) {
//	window.onload = init;
//	window.onresize = fixDivHeight;
//}
