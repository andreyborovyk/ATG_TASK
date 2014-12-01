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
//
// Utility functions for the AM search .
//
//----------------------------------------------------------------------------//

dojo.provide("atg.assetmanager.search");
atg.assetmanager.search = {
  mHideCriteriaLinkText: "Hide Criteria(Default)",
  mShowCriteriaLinkText: "Show Criteria(Default)",

  /**
   * @param pEvent                   Event object 
   * @param pRequestUrl              Target url
   * @param pExpressionContainerId   DOM ID of the search expression editor container
   * @param pShowHideCriteriaLink    DOM ID of the link that toggle the display
   *                                 of search form
   * @param pRightPaneContainerId    DOM ID of the right pane container
   * @param pRightPaneUrl            Right pane source URL
   * @param pResultsContainerId      DOM ID of the results container element
   * @param pSearchFormElementId     DOM ID of the Search form container element
   * @param pSearchFormResizableElementId DOM ID of the container that should be
   *                                 resized when the search form is displayed
   * @param pHeightKey               Unique key that stores the height of
   *                                 the resizable elemnt for search form
   * @param pExtraVerticalElementIds List of vertical elements below the
   *                                 resizable element for search form
   * 
   * Note that the property resultsListVisible in SearchSessionInfo is set to
   * false via the refresh of pRequestUrl in pExpressionContainerId 
   */
  onItemTypeSelect: function(pEvent, pRequestUrl, pExpressionContainerId, pShowHideCriteriaLink, pRightPaneContainerId, pRightPaneUrl, pResultsContainerId, pSearchFormElementId, pSearchFormResizableElementId, pHeightKey, pExtraVerticalElementIds) {
    //alert("ItemType changed called. \nExpressionContainerId:" + pExpressionContainerId + "\nExpressionContainerUrl:" + pRequestUrl
    //          + "\nShowHideCriteriaLink:" + pShowHideCriteriaLink
    //          + "\nRightPaneContainerId:" + pRightPaneContainerId + "\nRightPaneUrl:" + pRightPaneUrl
    //          + "\nResultsContainerId:" + pResultsContainerId
    //          + "\nSearchFormElementId:" + pSearchFormElementId + "\nSearchFormResizableElementId:" + pSearchFormResizableElementId
    //          + "\nHeightKey:" + pHeightKey + "\nExtraVerticalElementIds:" + pExtraVerticalElementIds);
    // Find the event target.
    if (pEvent == null) pEvent = event;
    var target;
    if (pEvent.srcElement)
      target = pEvent.srcElement;
    else if (pEvent.target)
      target = pEvent.target;
  
    if (target) {
      pRequestUrl += "&value=" + encodeURIComponent(target.value);
      // refresh the expression container
      // this page would set SearchSessionInfo.resultsListVisible to false
      atg.assetmanager.search.refreshElementAjax(pRequestUrl, pExpressionContainerId);
      // hide the show/hide criteria link
      atg.assetmanager.search._hideElementDisplay(pShowHideCriteriaLink);
      // refresh the right pane
      atg.assetmanager.search.refreshRightPane(pRightPaneContainerId, pRightPaneUrl);
      // hide the results panel
      atg.assetmanager.search._hideElementDisplay(pResultsContainerId);
      // display the search form
      atg.assetmanager.search._showElementDisplay(pSearchFormElementId);
      // now resize the search form
      atg.assetmanager.search._resizeElement(pSearchFormResizableElementId, pHeightKey, pExtraVerticalElementIds);
    }
  },

  /**
   * @param pEvent      the event object 
   * @param pRequestUrl the target url 
   */
  onItemTypeSelectSave: function(pEvent, pRequestUrl) {
   if (pEvent == null) pEvent = event;
    var target;
    if (pEvent.srcElement)
      target = pEvent.srcElement;
    else if (pEvent.target)
      target = pEvent.target;
  
    if (target) {
      pRequestUrl += "&value=" + encodeURIComponent(target.value);
    }

    atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, null, atg.assetmanager.search.continueOnItemTypeSelect, pRequestUrl, "atg.assetmanager.search.continueOnItemTypeSelect");
  },

     

  /**
   * @param pRequestUrl the target url 
   */
  continueOnItemTypeSelect: function(pRequestUrl) {
    // NB: Remove hard-coded URL.
    var pRightHandUrl = "/AssetManager/assetEditor/editAsset.jsp";
    // refresh the expression container
    // this page would set SearchSessionInfo.resultsListVisible to false
    atg.assetmanager.search.refreshElementAjax(pRequestUrl, 'searchExprEditorContainer');
    // hide the show/hide criteria link
    atg.assetmanager.search._hideElementDisplay('searchCriteriaPanelTrigger');
    // refresh the right pane
    atg.assetmanager.search.refreshRightPane('rightPaneIframe', pRightHandUrl);
    // hide the results panel
    atg.assetmanager.search._hideElementDisplay('searchTabResultsPanel');
    // display the search form
    atg.assetmanager.search._showElementDisplay('searchTabExpressionPanel');
    // now resize the search form
    atg.assetmanager.search._resizeElement('expressionContainer', 'expressionContainer', '');
  },

  /**
   * @param pEvent      the event object 
   * @param pRequestUrl the target url 
   */
  onItemTypeSelectNonAjax: function(pEvent, pRequestUrl) {
    //alert("ItemType changed(non-ajax");
    // Find the event target.
    if (pEvent == null) pEvent = event;
    var target;
    if (pEvent.srcElement)
      target = pEvent.srcElement;
    else if (pEvent.target)
      target = pEvent.target;
  
    if (target) {
      pRequestUrl += "&value=" + encodeURIComponent(target.value);
      //alert(pRequestUrl);
      atg.assetmanager.search.refreshElementNonAjax(pRequestUrl);
    }
  },
  
  /**
   * @param pId                       The DOM ID of the element whose display should be toggled
   * @param pResizableElementId       The DOM ID of the element to be resized
   * @param pHeightKey                A unique identifier for the height key
   * @param pExtraVerticalElementIds  A comma-separated list of IDs of extra
   *                                  vertical elements under the element to be hidden
   * @param pCriteraShowHideLinkId    The DOM ID of the criteria show hide link
   */
  toggleCriteriaDisplayAndResize: function (pId, pResizableElementId, pHeightKey, pExtraVerticalElementIds, pCriteraShowHideLinkId) {
    //alert("ToggleCriteriaDisplayAndResize called for element name:" + pId + "\nHeightKey:" + pHeightKey + "\nResultsElementId:" + pResultsElementId + "\nExtraVerticalElementIds:" + pExtraVerticalElementIds + "\nCriteraShowHideLinkId: " + pCriteraShowHideLinkId);
    //show or hide the criteria
    atg.assetmanager.search.toggleCriteriaDisplay(pId, pCriteraShowHideLinkId);
    // now resize the results panel
    atg.assetmanager.search._resizeElement(pResizableElementId, pHeightKey, pExtraVerticalElementIds);
  },

  /**
   * @param pId       The DOM ID of the criteria element
   * @param pCriteraShowHideLinkId    The DOM ID of the criteria show hide link
   */
  toggleCriteriaDisplay: function (pId, pCriteraShowHideLinkId) {
    //alert("ToggleCriteriaDisplay called for element name:" + pId + " and criteria link id:" + pCriteraShowHideLinkId);
    // return true if criteria is hidden
    if (pId) {
      var criteriaElement = document.getElementById(pId);
      if (criteriaElement != null) {
        if ((criteriaElement.style.display == null) || (criteriaElement.style.display == "")) {
          criteriaElement.style.display = 'none';
          atg.assetmanager.search._changeElementDisplayText(pCriteraShowHideLinkId, atg.assetmanager.search.mShowCriteriaLinkText);
        }
        else {
          criteriaElement.style.display = '';
          atg.assetmanager.search._changeElementDisplayText(pCriteraShowHideLinkId, atg.assetmanager.search.mHideCriteriaLinkText);
        }
      }
    }
  },

  /**
   * @param pId                       The DOM ID of the criteria element
   * @param pRequestUrl                A unique identifier for the height key
   */
  refreshRightPane: function (pId, pRequestUrl) {
    //alert("refreshRightPane called for element name:" + pId + "\nRequestUrl:" + pRequestUrl);
    if (pId) {
      var rightPaneElement = document.getElementById(pId);
      //alert("rightPaneElement:" + rightPaneElement);
      //alert("RequestUrl:" + pRequestUrl);
      if (rightPaneElement != null) {
        rightPaneElement.src = pRequestUrl; 
      }
    }
  },

  /**
   * @param pRequestUrl  The Url to be fired to perform search
   */
  doSearch: function(pRequestUrl) {
        
    if (!actionsAreLocked()) {
      atg.expreditor.applyPendingChanges(function() {
        atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(null, null, atg.assetmanager.search.continueDoSearch, pRequestUrl, "atg.assetmanager.search.continueDoSearch");
      });
    }
  },

  continueDoSearch: function(pRequestUrl) {
    parent.displayLeftLoadingIcon();
    document.location = pRequestUrl;
  },

  /**
   * @param pRequestUrl  The Url used to fill the content of the container
   * @param pContainerId The DOM ID of the container
   */
  refreshElementAjax: function(pRequestUrl, pContainerId) {
    atg.assetmanager.search.refreshElementAjax(pRequestUrl, pContainerId, false);
  },

  /**
   * @param pRequestUrl  The Url used to fill the content of the container
   * @param pContainerId The DOM ID of the container
   */
  refreshElementAjax: function(pRequestUrl, pContainerId, pEnableConcurrentCalls) {
    if ((pContainerId != null) && (pRequestUrl != null)) {
      //alert(pRequestUrl);
      if (pEnableConcurrentCalls) {
       atg.assetmanager.search._issueRequest(pRequestUrl, pContainerId);
      }
      else {
        issueRequest(pRequestUrl, pContainerId);
      }
    }
  },

  /**
   * @param pRequestUrl  The Url used to fill the content of the container
   * @param pContainerId The DOM ID of the container
   */
  refreshElementNonAjax: function(pRequestUrl, pContainerId) {
    if (pRequestUrl != null) {
      //alert(pRequestUrl);
      atg.assetmanager.saveconfirm.saveBeforeLeaveParentFrame(pRequestUrl);
    }
  },

  /**
   * @param pId                             The DOM ID of the criteria element
   * @param pResizeHeightKey                A unique identifier for the height key
   * @param pResizableElementId             The DOM ID of the results element
   * @param pExtraVerticalElementIds  A comma-separated list of IDs of extra
   *                                        vertical elements under the results element
   */
  hideElementAndResize: function (pId, pResizableElementId, pHeightKey, pExtraVerticalElementIds) {
    //alert("hideResultsListDisplayAndResize called for element name:" + pId + "\nHeightKey:" + pHeightKey + "\nResultsElementId:" + pResultsElementId + "\nExtraVerticalElementIds:" + pExtraVerticalElementIds);
    //atg.assetmanager.search.toggleCriteriaDisplay(pId, pCriteraShowHideLinkId);
    atg.assetmanager.search._hideElementDisplay(pId);
    // now resize the results panel
    atg.assetmanager.search._resizeElement(pResizableElementId, pHeightKey, pExtraVerticalElementIds);
  },

  /**
   * @param pId    The DOM ID of the element to be hidden
   */
  _toggleElementDisplay: function (pId) {
    //alert("toggleElementDisplay called for element name:" + pId );
    if (pId) {
      var criteriaElement = document.getElementById(pId);
      if (criteriaElement != null) {
        if ((criteriaElement.style.display == null) || (criteriaElement.style.display == "")) {
          criteriaElement.style.display = 'none';
        }
        else {
          criteriaElement.style.display = '';
        }
      }
    }
  },

  /**
   * @param pId    The DOM ID of the element to be hidden
   */
  _hideElementDisplay: function (pId) {
    //alert("hideElementDisplay called for element name:" + pId );
    if (pId) {
      var criteriaElement = document.getElementById(pId);
      if (criteriaElement != null) {
        if ((criteriaElement.style.display == null) || (criteriaElement.style.display == "")) {
          criteriaElement.style.display = 'none';
        }
      }
    }
  },

  /**
   * @param pId    The DOM ID of the element to be hidden
   */
  _showElementDisplay: function (pId) {
    //alert("showElementDisplay called for element name:" + pId );
    if (pId) {
      var criteriaElement = document.getElementById(pId);
      if (criteriaElement != null) {
        //alert("ShowElementDisplay(" + pId + "): style.display=" + criteriaElement.style.display);
        if ((criteriaElement.style.display == null) || (criteriaElement.style.display == "")) {
          // already visible - do nothing
        }
        else {
          criteriaElement.style.display = '';
        }
      }
    }
  },

  /**
   * @param pElementId       The DOM ID of the element whose text needs to be changed
   * @param pNewDisplayText  The new text to be display for the element
   */
  _changeElementDisplayText: function (pElementId, pNewDisplayText) {
    //alert("changeElementText called for element name:" + pElementId + " with new display text:" + pNewDisplayText);
    if (pElementId) {
      var element = document.getElementById(pElementId);
      if (element != null) {
        element.innerHTML = pNewDisplayText; 
      }
    }
  },

  /**
   * Resize the an element.
   * 
   * @param pResizableElementId       The DOM ID of the element to be resized
   * @param pHeightKey                A unique identifier for the height key
   * @param pExtraVerticalElementIds  A comma-separated list of IDs of extra
   *                                  vertical elements under the resizable element
   * This code is copied from resizeHandler.js in order to resize the results element.
   */
  _resizeElement: function (pResizableElementId, pHeightKey, pExtraVerticalElementIds) {
    // Get the top margin of the page header element.
    var header = document.getElementById("globalHeader");
    var margin = (header ? cascadedstyle(header, "marginTop").replace(/px/gi, "") : 0);

    // Use the same margin on the bottom of the window when calculating the height
    // of the displayable area.
    var displayableAreaHeight = getWindowHeight() - margin;

    var extraVerticalElementIds = pExtraVerticalElementIds.split(",");
    var resizableElement = document.getElementById(pResizableElementId);
    if (resizableElement == null)
      return;
    var position = findPosY(resizableElement);
    var newHeight = displayableAreaHeight - position;

    for (var i = 0; i < extraVerticalElementIds.length; i++) {
      var extraElem = document.getElementById(extraVerticalElementIds[i]);
      if (extraElem)
        newHeight -= extraElem.offsetHeight;
    }

    newHeight = Math.max(newHeight, 100);
    resizableElement.style.height = newHeight + "px";
  },
  
  /**
   * Issue a request using an XMLHttpRequest object created locally
   */
  _issueRequest: function(pRequestUrl, pElementId) {
  
    var xmlHttpRequestObj = atg.assetmanager.search._createXMLHttpRequestObject();
    if (!xmlHttpRequestObj) {
      alert("Error: Can't create XMLHttpRequest");
      return;
    }
    try {
      document.body.style.cursor = "wait";
  
      xmlHttpRequestObj.open("GET", pRequestUrl, true);
  
      xmlHttpRequestObj.onreadystatechange = function() {
        if (xmlHttpRequestObj.readyState == 4) {
          if (pElementId) {
            var target = document.getElementById(pElementId);
            if (target) {
              target.innerHTML = "";
              target.innerHTML = xmlHttpRequestObj.responseText;
            }
          }
          document.body.style.cursor = "default";
        }
      }
  
      xmlHttpRequestObj.send(null);
    }
    catch (e) {
      alert("Error: XMLHttpRequest failed: " + e.message);
    }
  },
  
  /**
   * Attempt to create an XMLHttpRequest object.
   */
  _createXMLHttpRequestObject: function() {
  
    if (typeof ActiveXObject != "undefined") {
      // On IE, XMLHttpRequest is implemented using ActiveX.
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
    else if (typeof XMLHttpRequest != "undefined") {
      // Browsers such as Firefox include XMLHttpRequest objects in JavaScript.
      return new XMLHttpRequest();
    }
    return null;
  }  
}



 
