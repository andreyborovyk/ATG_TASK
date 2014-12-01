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
 * A client-side object that manages alerts and error messages.
 *
 * Note that this object is not designed for general use.  Its behavior is
 * tailored to the content of the Asset Manager.
 *
 * @version $Id$$Change$
 * @updated $DateTime$$Author$
 */
function Messages() {

  //--------------------------------------------------------------------------//
  // Fields
  //--------------------------------------------------------------------------//

  /** List of errors that were added during page rendering. */
  this.errorsDuringRendering = new Array();

  /** used to time delay the wait message */
  this.startWaitFlag = false;


}

//----------------------------------------------------------------------------//
// Public methods
//----------------------------------------------------------------------------//



/**
 * Append the given text to the alert area.
 *
 * @param pText  The text to be added
 */
Messages.prototype.addAlert = function(pText) {
  this.endWait();
  // Hiding all alerts before showing new one to avoid alerts duplication in case of cascade windows (SEARCHMERCH-168002)
  // So new alert actually replaces the old one, but is not added to it. So ONLY ONE alert can be shown on page at a time.
  this.hideAlerts();
  var elem = document.getElementById("alert");
  if (elem) {
    elem.innerHTML += pText;
    elem.innerHTML += "<br/>";
    elem.style.display = "inline";

    window.resizeHandler.forceHandleOnResize();
  }
}

/**
 * Append the given text to the alert area.
 *
 * @param pText  The text to be added
 */
Messages.prototype.wait = function() { 
  var elem = document.getElementById("wait");
  if (elem) {
    elem.style.display = "inline";

    window.resizeHandler.forceHandleOnResize();
  }
}

//----------------------------------------------------------------------------//
/**
 * Append the given text to the error dialog, and display the dialog.
 *
 * @param pText  The text to be added
 */
Messages.prototype.addError = function(pText) {
  this.endWait();
  var div = document.getElementById("errorDialogContainer");
  if (div && window.frames.errorDialogFrame)
    this._doAddError(pText);
  else
    this.errorsDuringRendering[this.errorsDuringRendering.length] = pText;
}

Messages.prototype.endWait = function () {
  var elem = document.getElementById("wait");
  if (elem) {
    elem.style.display = "none";

    window.resizeHandler.forceHandleOnResize();
  }
}

//----------------------------------------------------------------------------//
/**
 * Hide all alerts.
 */
Messages.prototype.hideAlerts = function() {
  var elem = document.getElementById("alert");
  if (elem) {
    elem.style.display = "none";
    elem.innerHTML = "";

    window.resizeHandler.forceHandleOnResize();
  }
  elem = document.getElementById("error");
  if (elem) {
    elem.style.display = "none";
    elem.innerHTML = "";
  }
}

//----------------------------------------------------------------------------//
/**
 * Hide all errors.
 */
Messages.prototype.hideErrorDialog = function() {
  var div = document.getElementById("errorDialogContainer");
  if (div && parent.window.errorDialogFrame) {
    var elem = parent.window.errorDialogFrame.document.getElementById("errorDialogMessage");
    if (elem) {
      div.style.display = "none";
      elem.innerHTML = "";
      var curtain = document.getElementById("curtain");
      if (curtain)
        curtain.style.display = "none";
      //---------------------------------------------------
      // add the class appletKiller to the main document.
      //---------------------------------------------------
      if (usesApplets) {
        removeAppletKillerFromDocument(document, "appletKiller");
      }
    }
  }
}

//----------------------------------------------------------------------------//
/**
 * Hide all of the alerts and errors.
 */
Messages.prototype.hide = function() {
  this.endWait();
  this.hideAlerts();
  this.hideErrorDialog();
}

//----------------------------------------------------------------------------//
/**
 * Handle a window onload event.
 */
Messages.prototype.handleOnLoad = function() {
  for (var i = 0; i < this.errorsDuringRendering.length; i++) {
    this._doAddError(this.errorsDuringRendering[i]);
  }
}

//----------------------------------------------------------------------------//
// Internal methods
//----------------------------------------------------------------------------//

/**
 * Append the given text to the error dialog, and display the dialog.
 *
 * @param pText  The text to be added
 */
Messages.prototype._doAddError = function(pText) {
  this.endWait();
  var div = document.getElementById("errorDialogContainer");
  if (div && window.frames.errorDialogFrame) {
    var elem = window.frames.errorDialogFrame.document.getElementById("errorDialogMessage");
    if (elem) {
      //---------------------------------------------------
      // add the class appletKiller to the main document.
      //---------------------------------------------------
      if (usesApplets) {
        addAppletKillerToDocument(document, "appletKiller");
      }

      var curtain = document.getElementById("curtain");
      if (curtain)
        curtain.style.display = "block";
      elem.innerHTML += pText;
      elem.innerHTML += "<br/>";
      div.style.display = "block";
    }
  }
}

//----------------------------------------------------------------------------//
// End of messages.js
//----------------------------------------------------------------------------//
