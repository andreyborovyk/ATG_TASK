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
 * A client-side object that manages resizable screen elements.
 *
 * Note that this object is not designed for general use.  Its behavior is
 * tailored to the layout of the Asset Manager.
 *
 * @version $Id$$Change$
 * @updated $DateTime$$Author$
 */
function ResizeHandler() {

  //--------------------------------------------------------------------------//
  // Fields
  //--------------------------------------------------------------------------//

  /** Indicates if the page is currently loading. */
  this.loading = false;

  /** The current browser window height, initially obtained from the server. */
  this.currentWindowHeight = null;

  /** A variable that will become set to true if it is determined that a
      resizable DOM element's height is not yet known. */
  this.needInitialSize = false;

  /** The URL of the page that will store the dimensions map on the server. */
  this.dimensionsUrl = null;

  /** List of data for resizable elements. */
  this.resizableElementData = new Array();
}

//----------------------------------------------------------------------------//
// Public methods
//----------------------------------------------------------------------------//

/**
 * Add a DOM element to the list of resizable elements.
 *
 * @param pKey                      A unique identifier for the element
 * @param pId                       The DOM ID of the resizable element
 * @param pExtraVerticalElementIds  A comma-separated list of IDs of extra
 *                                  vertical elements under the resizable element
 */
ResizeHandler.prototype.addResizableElement = function(pKey, pId, pExtraVerticalElementIds) {

  var elemData = new Object();
  elemData.key                     = pKey;
  elemData.id                      = pId;
  elemData.extraVerticalElementIds = pExtraVerticalElementIds.split(",");
  this.resizableElementData[this.resizableElementData.length] = elemData;
}

//----------------------------------------------------------------------------//
/**
 * Handle a window onload event.
 */
ResizeHandler.prototype.handleOnLoad = function() {

  // NB: Do we need to set a timeout to delay the action of setting of
  // this.loading back to false?
  this.loading = true;
  this._handleResize();
  this.loading = false;
}

//----------------------------------------------------------------------------//
/**
 * Handle a window onresize event.
 */
ResizeHandler.prototype.handleOnResize = function() {
  if (! this.loading)
    this._handleResize();
}

//----------------------------------------------------------------------------//
/**
 * Force handle a window onresize event.
 */
ResizeHandler.prototype.forceHandleOnResize = function() {
  if (! this.loading) {
    this.needInitialSize = true;
    this._handleResize();
  }
}

//----------------------------------------------------------------------------//
// Internal methods
//----------------------------------------------------------------------------//

/**
 * Handle a window resize event.
 */
ResizeHandler.prototype._handleResize = function() {

  // Get the top margin of the page header element.
  var header = document.getElementById("globalHeader");
  var margin = (header ? cascadedstyle(header, "marginTop").replace(/px/gi, "") : 0);

  // Use the same margin on the bottom of the window when calculating the height
  // of the displayable area.
  var newHeight = getWindowHeight() - margin;

  // If the rendering of this page has included resizable DOM elements who don't
  // yet know their desired size, or the browser has been resized by the user,
  // we need to invoke all of the resize handlers.
  if (this.needInitialSize || (newHeight != this.currentWindowHeight)) {

    // Save the new window height.
    this.currentWindowHeight = newHeight;

    // Resize all of the visible, resizable elements, and store the new sizes
    // in a map.
    var dimensions = new Object();
    for (var i = 0; i < this.resizableElementData.length; i++) {
      var elemData = this.resizableElementData[i];
      var key = elemData.key;
      dimensions[key] = this._resizeElement(elemData, newHeight);
    }

    // Store all of the newly created entries from the dimensions map in a
    // string to be passed to the server.  If this function was called as a
    // result of a page load, and some elements didn't know their size, begin
    // the string with a ';' to indicate that the server-side map should not be
    // initially cleared.  Otherwise (if this function was called as a result
    // of a page resize), the map must be cleared before specifying new values.
    var dimensionsString = (this.needInitialSize ? ";" : "");

    // Always store the current window height in the map.
    dimensionsString += "mainWindow:" + this.currentWindowHeight;

    // Add entries for the resized elements.
    for (var key in dimensions) {
      dimensionsString += ";" + key + ":" + dimensions[key];
    }

    // Clear the needInitialSize flag, so that, if the browser is resized, the
    // server-side map will be cleared.
    this.needInitialSize = false;

    // Transmit the new map values to the server.
    var url = this.dimensionsUrl + "&value=" + dimensionsString;
    issueRequest(url);
  }
}

//----------------------------------------------------------------------------//
/**
 * Resize an individual element.
 *
 * @param pId                       The ID of the resizable element.
 * @param pKey                      The key of the map entry in which to store
 *                                  the new element height.
 * @param pExtraVerticalElementIds  A comma-separated list of extra vertical
 *                                  elements under the resizable element.
 *
 * @return  The new height for the element.
 */
ResizeHandler.prototype._resizeElement = function(pElemData, pNewHeight) {

  var resizableElement = document.getElementById(pElemData.id);
  if (resizableElement == null)
    return;
  var position = findPosY(resizableElement);
  var newHeight = pNewHeight - position;

  for (var i = 0; i < pElemData.extraVerticalElementIds.length; i++) {
    var extraElem = document.getElementById(pElemData.extraVerticalElementIds[i]);
    if (extraElem)
      newHeight -= extraElem.offsetHeight;
  }

  newHeight = Math.max(newHeight, 100);
  resizableElement.style.height = newHeight + "px";

  return newHeight;
}

//----------------------------------------------------------------------------//
// End of resizeHandler.js
//----------------------------------------------------------------------------//