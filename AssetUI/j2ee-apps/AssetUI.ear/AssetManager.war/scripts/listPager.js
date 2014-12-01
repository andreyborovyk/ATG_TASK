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

/**
 * A client-side object that manipulates an editor for List, Map, and
 * Set properties.
 *
 * @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/scripts/listPager.js#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
function ListPager() {

  //--------------------------------------------------------------------------//
  // Fields
  //--------------------------------------------------------------------------//

  /** A prefix to be applied to all page element IDs. */
  this.idPrefix = null;

  /** The current page index. */
  this.pageIndex = null;

  /** The number of items per page. */
  this.itemsPerPage = null;

  /** The total number of pages. */
  this.numPages = null;

  /** The total number of items. */
  this.numItems = null;

  /** The max number of items. */
  this.maxResults = null;

  /** The base URL for the item list panel. */
  this.panelURL = null;

  /** The ID of the container for the item list panel. */
  this.containerId = null;

  /** The element that displays the item count */
  this.numItemsLabel = null;

  /** The element that displays the paging controls */
  this.pageControls = null;

  /** The element that displays the page number */
  this.pageNumberInput = null;

  /** The button for going to the first page */
  this.pageFirstButton = null;

  /** The button for going to the previous page */
  this.pagePrevButton = null;

  /** The button for going to the next page */
  this.pageNextButton = null;

  /** The button for going to the last page */
  this.pageLastButton = null;

  /** The element that displays the page count */
  this.numPagesLabel = null;
}

//----------------------------------------------------------------------------//
// Public methods
//----------------------------------------------------------------------------//

/**
 * Initialize the object after the page has completely loaded.
 */
ListPager.prototype.initialize = function() {

  // Derive some element IDs.
  this.containerId = "scrollContainer";

  // Get references to some elements.
  this.numItemsLabel   = document.getElementById(this.idPrefix + "numItems");
  this.numPagesLabel   = document.getElementById(this.idPrefix + "numPages");
  this.pageControls    = document.getElementById(this.idPrefix + "pageControls");
  this.pageNumberInput = document.getElementById(this.idPrefix + "pageNumber");
  this.pageFirstButton = document.getElementById(this.idPrefix + "pageFirst");
  this.pagePrevButton  = document.getElementById(this.idPrefix + "pagePrev");
  this.pageNextButton  = document.getElementById(this.idPrefix + "pageNext");
  this.pageLastButton  = document.getElementById(this.idPrefix + "pageLast");
}

//----------------------------------------------------------------------------//
/**
 * Go to the first page.
 */
ListPager.prototype.firstPage = function() {
  this.goToPage(1);
}

//----------------------------------------------------------------------------//
/**
 * Go to the previous page.
 */
ListPager.prototype.previousPage = function() {
  this.goToPage(this.pageIndex - 1);
}

//----------------------------------------------------------------------------//
/**
 * Go to the next page.
 */
ListPager.prototype.nextPage = function() {
  var ix = new Number(this.pageIndex);
  this.goToPage(ix + 1);
}

//----------------------------------------------------------------------------//
/**
 * Go to the last page.
 */
ListPager.prototype.lastPage = function() {
  this.goToPage(this.numPages) - 1;
}

//----------------------------------------------------------------------------//
/**
 * Update the page index without modifying the item list.
 *
 * @param pIndex  The index of the page
 */
ListPager.prototype.setPageIndex = function(pIndex) {

  // Ignore attempts to page out-of-bounds.
  if (pIndex <= 0 || pIndex > this.numPages)
    return;

  // Save the new index.
  this.pageIndex = pIndex;

  // Update the page number label.
  this.handleNewPageNumber();
}

//----------------------------------------------------------------------------//
/**
 * Go to a given page.
 *
 * @param pIndex  The index of the page
 */
ListPager.prototype.goToPage = function(pIndex) {
  // Ignore attempts to page out-of-bounds.
  if (pIndex <= 0 || pIndex > this.numPages)
    return;

  // Tell the item list to display the specified page.
  var url = this.panelURL + "&listPageNumber=" + pIndex;
  var listFrame = document.getElementById(this.containerId);
  if (listFrame) {
    listFrame.src = url;
  }

  this.setPageIndex(pIndex);
  this.handleNewPageNumber();
}

//----------------------------------------------------------------------------//
/**
 * Handle a focus event in the page number input.
 */
ListPager.prototype.pageNumberFocus = function() {

  // Always select the text when the focus is received.
  this.pageNumberInput.select();
  return true;
}

//----------------------------------------------------------------------------//
/**
 * Handle a keypress event in the page number input.
 */
ListPager.prototype.pageNumberKeyPress = function(evt) {

  // Determine which key was pressed.
  if (evt == null) evt = event;
  var charCode = evt.charCode;
  if (charCode == null || charCode == 0) charCode = evt.keyCode;
  if (charCode == null) charCode = evt.which;

  // If the user hit Enter, go to the indicated page.
  if (charCode == 13 || charCode == 3) {

    var index = this.pageNumberInput.value;

    if (index <= 0 || index > this.numPages) {

      // If the requested page number is invalid, just redisplay the current
      // page and reselect the text.
      this.handleNewPageNumber();
      this.pageNumberInput.select();
    }
    else {

      // Go to the requested page number.
      this.goToPage(index);
    }
    return false;
  }
  else if (charCode >= 32 && (charCode < 48 || charCode > 57)) {
    // Ignore non-numeric text.
    return false;
  }
  // Process other characters normally.
  return true;
}

//----------------------------------------------------------------------------//
/**
 * Update the item count and page index for this control.
 */
ListPager.prototype.updatePager = function(pNumItems, pPageNumber) {
  this.pageIndex = pPageNumber + 1;
  this.handleNewPageNumber();
  this.numItems = pNumItems;
  this.handleNewItemCount();
}

//----------------------------------------------------------------------------//
/**
 * Make the necessary changes when the item count has changed.
 */
ListPager.prototype.handleNewItemCount = function() {

  // Update the count label.
  this.numItemsLabel.innerHTML = this.numItems;

  // Adjust the page count and label.
  this.numPages = Math.floor((this.numItems + this.itemsPerPage - 1)/ this.itemsPerPage);
  this.numPagesLabel.innerHTML = this.numPages;

  // Adjust the visibility of the paging controls.
  var visible = (this.numItems > this.itemsPerPage ? "inline" : "none");
  this.pageControls.style.display = visible;

  // The page count may have changed, so we may need to update the VCR buttons.
  this.updateVCRButtons();
}

//----------------------------------------------------------------------------//
/**
 * Make the necessary changes when the page number has changed.
 */
ListPager.prototype.handleNewPageNumber = function() {

  // Adjust the page number indicator.
  var pageNumber = this.pageIndex;
  // if the total number is more than max results
  // pageNumberInput is not a text box but
  // a simple span element, hence update the
  // innerHTML instead.
  if ((this.maxResults != null) && (this.numItems > this.maxResults)) {
    this.pageNumberInput.innerHTML = pageNumber;
  }
  else {
    this.pageNumberInput.value = pageNumber;
  }

  this.updateVCRButtons();
}

//----------------------------------------------------------------------------//
/**
 * Change the display of the VCR buttons to indicate which ones are active.
 */
ListPager.prototype.updateVCRButtons = function() {

  if (this.pageIndex <= 1) {
    this.pageFirstButton.className = "buttonPageFirstOff";
    this.pagePrevButton.className  = "buttonPagePrevOff";
    this.pageNextButton.className  = "buttonPageNext";
    // if the number of items is more than max results
    // the last page control is disabled.
    if ((this.maxResults != null) && (this.numItems > this.maxResults)) {
      this.pageLastButton.className  = "buttonPageLastOff";
    }
    else {
      this.pageLastButton.className  = "buttonPageLast";
    }
  }
  else if (this.pageIndex >= this.numPages) {
    this.pageFirstButton.className = "buttonPageFirst";
    this.pagePrevButton.className  = "buttonPagePrev";
    this.pageNextButton.className  = "buttonPageNextOff";
    this.pageLastButton.className  = "buttonPageLastOff";
  }
  else {
    this.pageFirstButton.className = "buttonPageFirst";
    this.pagePrevButton.className  = "buttonPagePrev";
    this.pageNextButton.className  = "buttonPageNext";
    // if the number of items is more than max results
    // the last page control is disabled.
    if ((this.maxResults != null) && (this.numItems > this.maxResults)) {
      this.pageLastButton.className  = "buttonPageLastOff";
    }
    else {
      this.pageLastButton.className  = "buttonPageLast";
    }
  }
}

//----------------------------------------------------------------------------//
// End of listPager.js
//----------------------------------------------------------------------------//
