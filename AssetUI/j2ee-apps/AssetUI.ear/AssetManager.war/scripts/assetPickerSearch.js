//----------------------------------------------------------------------------//
//
// Utility functions for the AM search .
//
//----------------------------------------------------------------------------//

dojo.provide("atg.assetpicker.search");
atg.assetpicker.search = {
  options: null,   // storage for params to the initialize function
  mHideCriteriaLinkText: null, // see the initialize method for details
  mShowCriteriaLinkText: null, // see the initialize method for details
  mSearchFormPanelClassLarge: null, // see the initialize method for details
  mSearchResultsPanelClassLarge: null, // see the initialize method for details
  mSearchResultsPanelClassSmall: null, // see the initialize method for details
  mSearchResultsExceptionsPanelClassLarge: null, // see the initialize method for details
  mSearchResultsExceptionPanelClassSmall: null, // see the initialize method for details

  /**
   * hideCriteriaLinkText - The hide criteria link text to be displayed when
   *                        the search criteria is hidden.
   * showCriteriaLinkText - The hide criteria link text to be displayed when
   *                        the search criteria is hidden.
   * searchFormPanelClassLarge - The class name for the search form panel HTML
   *                        element when the search criteria is the only one
   *                        to be displayed in the asset picker.
   * searchResultsPanelClassLarge - The class name for the search results panel
   *                        HTML element when the search results list is the
   *                        only one to be displayed in the asset picker.
   * searchResultsPanelClassSmall - The class name for the search results panel
   *                        HTML element when is displayed along with the
   *                        search criteria.
   * searchResultsExceptionPanelClassLarge - The class name for the search
   *                        results panel HTML element with search exceptions
   *                        when it is the only one to be displayed in Asset Picker
   * searchResultsExceptionPanelClassSmall - The class name for the search
   *                        results panel HTML element with search exceptions
   *                        when it is displayed along with the search criteria
   */  
  initialize: function(options) {
    // alert('atg.assetpicker.search.initialize called');
    this.options = options; // store the options in case there are more than the defined.
    this.mHideCriteriaLinkText = options.hideCriteriaLinkText;
    this.mShowCriteriaLinkText = options.showCriteriaLinkText;
    this.mSearchFormPanelClassLarge = options.searchFormPanelClassLarge;
    this.mSearchResultsPanelClassLarge = options.searchResultsPanelClassLarge;
    this.mSearchResultsPanelClassSmall = options.searchResultsPanelClassSmall;
    this.mSearchResultsExceptionsPanelClassLarge = options.searchResultsExceptionPanelClassLarge;
    this.mSearchResultsExceptionPanelClassSmall = options.searchResultsExceptionPanelClassSmall;
  },

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
   * 
   * Note that the property resultsListVisible in SearchSessionInfo is set to
   * false via the refresh of pRequestUrl in pExpressionContainerId 
   */
  onItemTypeSelect: function(pEvent, pRequestUrl, pExpressionContainerId, pShowHideCriteriaLink, pResultsContainerId, pSearchFormElementId, pSearchFormResizableElementId, pSearchResultsUrl) {
    // alert("ItemType changed called. \nExpressionContainerId:" + pExpressionContainerId + "\nExpressionContainerUrl:" + pRequestUrl
    //           + "\nShowHideCriteriaLink:" + pShowHideCriteriaLink
    //           + "\nResultsContainerId:" + pResultsContainerId
    //           + "\nSearchFormElementId:" + pSearchFormElementId + "\nSearchFormResizableElementId:" + pSearchFormResizableElementId);
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
      issueRequest(pRequestUrl, pExpressionContainerId);
      // hide the show/hide criteria link
      atg.assetpicker.search._hideElementDisplay(pShowHideCriteriaLink);
      // hide the results panel
      atg.assetpicker.search._hideElementDisplay(pResultsContainerId);
      // display the search form
      atg.assetpicker.search._showElementDisplay(pSearchFormElementId);
      // now resize the search form
      if (pSearchFormResizableElementId) {
        var searchFormResizableElement = document.getElementById(pSearchFormResizableElementId);
        if (searchFormResizableElement != null) {
          searchFormResizableElement.className = this.mSearchFormPanelClassLarge;
        }
      }

      pSearchResultsUrl += "&selectedAsset=" + encodeURIComponent(target.value);
      var searchButton= document.getElementById("assetPickerSearchButton");
      if (searchButton) {
        searchButton.onclick = function(event) {
          atg.assetpicker.search.showResults(pSearchResultsUrl);
        }
      }
      atg.expreditor.registerOnEnter("assetPickerSearchExprEditorPanel", function() {
        atg.assetpicker.search.showResults(pSearchResultsUrl);
      });
    }
  },

  toggleCriteriaDisplayAndResize: function(pId, pSearchResultsElementId, pResultsSubHeaderId, pCriteraShowHideLinkId, pHasExceptions) {
    // alert("ToggleCriteriaDisplayAndResize called for element name:" + pId + "\nCriteraShowHideLinkId: " + pCriteraShowHideLinkId);
    // show or hide the criteria
    atg.assetpicker.search.toggleCriteriaDisplay(pId, pCriteraShowHideLinkId);
    // now resize the results panel
    if (pId && pSearchResultsElementId) {
      var criteriaElement = document.getElementById(pId);
      var searchResultsElement = document.getElementById(pSearchResultsElementId);
      if (searchResultsElement != null) {
        // show the results sub header element if it exists
        this.toggleElementDisplay(pResultsSubHeaderId);
        if ((criteriaElement == null) || (criteriaElement.style.display == "none")) {
          if (pHasExceptions) {
            searchResultsElement.className = this.mSearchResultsExceptionsPanelClassLarge;
          }
          else {
            searchResultsElement.className = this.mSearchResultsPanelClassLarge
          }
        }
        else {
          if (pHasExceptions) {
            searchResultsElement.className = this.mSearchResultsExceptionPanelClassSmall;
          }
          else {
            searchResultsElement.className = this.mSearchResultsPanelClassSmall;
          }
        }
      }
    }
  },

  toggleElementDisplay: function(pId) {
    // alert("ToggleElementDisplay called for element name:" + pId);
    if (pId) {
      var elem = document.getElementById(pId);
      if (elem != null) {
        if ((elem.style.display == null) || (elem.style.display == "")) {
          elem.style.display = 'none';
        }
        else {
          elem.style.display = '';
        }
      }
    }
  },

  toggleCriteriaDisplay: function(pId, pCriteraShowHideLinkId) {
    // alert("ToggleCriteriaDisplay called for element name:" + pId + " and criteria link id:" + pCriteraShowHideLinkId);
    if (pId) {
      var criteriaElement = document.getElementById(pId);
      if (criteriaElement != null) {
        if ((criteriaElement.style.display == null) || (criteriaElement.style.display == "")) {
          criteriaElement.style.display = 'none';
          this._changeElementDisplayText(pCriteraShowHideLinkId, this.mShowCriteriaLinkText);
        }
        else {
          criteriaElement.style.display = '';
          this._changeElementDisplayText(pCriteraShowHideLinkId, this.mHideCriteriaLinkText);
        }
      }
    }
  },

  _changeElementDisplayText: function(pElementId, pNewDisplayText) {
    // alert("changeElementText called for element name:" + pElementId + " with new display text:" + pNewDisplayText);
    if (pElementId) {
      var element = document.getElementById(pElementId);
      if (element != null) {
        element.innerHTML = pNewDisplayText; 
      }
    }
  },

  /**
   * @param pId    The DOM ID of the element to be hidden
   */
  _hideElementDisplay: function(pId) {
    // alert("hideElementDisplay called for element name:" + pId );
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
  _showElementDisplay: function(pId) {
    // alert("showElementDisplay called for element name:" + pId );
    if (pId) {
      var criteriaElement = document.getElementById(pId);
      if (criteriaElement != null) {
        // alert("ShowElementDisplay(" + pId + "): style.display=" + criteriaElement.style.display);
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
   * This function is invoked when the Search button is clicked.  It displays
   * the search results page after applying any pending changes in the search
   * expression editor.
   */
  showResults: function(requestUrl) {
    // alert("showResults: results URL: " + requestUrl);
    atg.expreditor.applyPendingChanges(function() {
      document.location = requestUrl;
    });
  }
};

dojo.provide("atg.assetpicker.checkboxes");
atg.assetpicker.checkboxes = {
  // global variables
  checkedItems: null, // array of checkedItem objects
  allowMultiSelect: false, // multi selection allowed
  checkboxElementName: null, // element name for the checkboxes
  
  initialize: function(options) {
    // alert("Called atg.assetpicker.checkboxes.initialize() with options:" + options);
    this.allowMultiSelect = options.allowMultiSelect;
    this.checkboxElementName = options.checkboxElementName;
  },

  /**
   * See if any checkboxes were already checked on a previous page
   * visit during this invocation of the asset picker.  If so, check them.
   */
  initializeCheckboxes: function(pCheckBoxElementName) {
    // alert("Called atg.assetpicker.checkboxes.initializeCheckboxes(" + pCheckBoxElementName + ") - checkedItems: " + this.checkedItems);
    if (this.checkedItems != null && this.checkedItems.length > 0) {
      // Loop through all of the result set checkboxes (designated by a specific name).
      var subdocument = document.getElementById("scrollContainer").contentWindow.document;
      var checkboxes = subdocument.getElementsByName(pCheckBoxElementName); // this.checkboxElementNames);
      for (var i = 0; i < checkboxes.length; i++) {
        var checkbox = checkboxes[i];
        
        // Extract the associated asset URI from the checkbox's ID.
        var checkboxUri = checkbox.id.replace(/^checkbox_/, "");
        
        // Look for an item with the given URI in the parent document's list
        // of checked items.
        for (var j = 0; j < this.checkedItems.length; j++) {
          if (this.checkedItems[j].uri == checkboxUri) {
            checkbox.checked = true;
            break;
          }
        }
      }
    }
  },

  /**
   * this is invoked when an item is checked or unchecked
   */
  checkItem: function(uri,itemId,label) {
    // alert("called atg.assetpicker.checkboxes.checkItem() -\n uri:[" + uri + "]\n itemId:[" + itemId + "]\n label:[" + label + "]");
    // if the node is checked, we are unchecking it, so search for the item
    var foundItem = false;
    label = this.replaceEscapeCharacters(label);
    if (this.checkedItems != null) {
      var newCheckedItems = new Array();
      for (var ii=0; ii < this.checkedItems.length; ii++) {
        if (this.checkedItems[ii].uri == uri) {
          this.checkedItems[ii] = null;
          foundItem = true;
        }
        else {
          newCheckedItems[newCheckedItems.length] = this.checkedItems[ii];
        }
      }

      if (foundItem) {
        this.checkedItems = newCheckedItems;
        return;
      }
    }

    // item was not found among the checked nodes.
    var checkedItem = new Object();
    checkedItem.id = itemId;
    checkedItem.displayName = label;
    checkedItem.uri = uri;
    if (this.checkedItems == null) {
      this.checkedItems = new Array();
    }
    if (this.allowMultiSelect) {
      this.checkedItems[this.checkedItems.length] = checkedItem;
    }
    else {
      this.checkedItems[0] = checkedItem;
    }
  },
  
  /**
   * to replace escape characters if any
   */ 
  replaceEscapeCharacters: function(oriStr) {
    var escStr = oriStr.replace(/\\\\'/g, "'");
    escStr = escStr.replace(/\\\\\"/g, "\"");
    return escStr;
  }
};

//
// This is the method called by the asset picker container
// which returns the action string in as a parameter to the
// parent Javascript method defined by picker.onSelect
// Note: cannot move this javascript method to the javascript file
// as this is required by assetPickerContainter.jsp. 
//
function getSelectedAssets() {
  return atg.assetpicker.checkboxes.checkedItems;
}

