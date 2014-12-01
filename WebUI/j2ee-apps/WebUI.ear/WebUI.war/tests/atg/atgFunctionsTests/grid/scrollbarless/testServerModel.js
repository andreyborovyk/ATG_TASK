/*<ATGCOPYRIGHT>
 * Copyright (C) 2007-2008 Art Technology Group, Inc.
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
 * Declare the CollectionEditorData class which extends the
 * turbo.data.dynamic model. This class is used to lazily load and manipulate
 * the elements of a collection via AJAX requests to a server URL.
 *
 * @version $Id: //product/WebUI/version/10.0.3/src/web-apps/WebUI/tests/atg/atgFunctionsTests/grid/scrollbarless/testServerModel.js#2 $$Change: 651448 $
 * @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
 */
dojo.provide("atg.assetmanager");

dojo.require('dojox.grid._data.model');

dojo.declare("atg.assetmanager.TestEditorData", dojox.grid.data.Dynamic, {
  
  constructor: function(pUrl) {
    this._server = pUrl;
  },

  // Standard message used to indicate errors communicating with the server.
  standardErrorMessage: null,

  // Message used to indicate duplicates during an insert.
  duplicatesDetectedMessage: null,

  ///////////////////////////////////////////////////////////////////////////
  //      Methods to set up and receive AJAX communication with            //
  //      the server                                                       //
  ///////////////////////////////////////////////////////////////////////////

  // Server send / receive. Any params specified in the content property are
  // appended onto the url.
  send: function(pAsync, pParams, pCallbacks) {
    var deferred = dojo.xhrPost({url: this._server,
                                content: pParams,
                                handleAs: "json",
                                encoding: "utf-8",
                                sync: !pAsync});
    deferred.addCallbacks(dojo.hitch(this, "receive", pCallbacks), dojo.hitch(this, "receiveError", pCallbacks));
    return deferred;
  },

  _callback: function(pCallback, pErrback, pData) {
    try {
      if (pCallback) {
        pCallback(pData);
      }
    }
    catch(e) {
      if (pErrback) {
        pErrback(pData, e);
      }
    }
  },

  // Invoke callbacks to deal with data sent back from a successful request.
  receive: function(pCallbacks, pData) {
    if (pCallbacks) {
      this._callback(pCallbacks.callback, pCallbacks.errback, pData);
    }
  },

  // Handle errors by popping up a message in the asset manager, then invoke
  // any error callbacks.
  receiveError: function(pCallbacks, pErr) {
    if (parent && parent.messages) {
      parent.messages.addError(this.standardErrorMessage);
    }

    if (pCallbacks) {
      this._callback(pCallbacks.errback, null, pErr);
    }
  },

  ///////////////////////////////////////////////////////////////////////////
  //      Methods that issue requests to manipulate or retrieve data       //
  ///////////////////////////////////////////////////////////////////////////

  // Request a page of items from the server, where page size is determined
  // by the page size set on the grid.
  requestRows: function(pRowIndex, pCount) {
    // fake some data when it is requested
    for (var i=0, len=pCount; i<len; i++) {
      var index = pRowIndex + i;
      var itemname = "Item " + index;
      var uri = "z" + index;
      var id = "id" + index;
      var rowObj = [itemname, uri, id];
      this.setRow(rowObj, pRowIndex + i);
    }
  },

  // Issue a request to delete an element on the server.
  requestDelete: function(pRemoveIndex) {
    var params = {
      removeIndex: pRemoveIndex
    };
    var cbs = {
      callback: dojo.hitch(this, this._requestDeleteCallback, pRemoveIndex)
      // no errback:
    };

    this.send(true, params, cbs);
  },

  // Issue a request to insert one or more items into a collection on the server.
  requestInsertItems: function(pIdArray, pInsertIndex) {
    // Clear any prior messsages.
    parent.messages.hide();

    var idString = "";
    for (var i = 0; i < pIdArray.length; i++) {
      if (i > 0) {
        idString += ",";
      }
      idString += pIdArray[i];
    }
    var params = {
      insertIds: idString,
      insertIndex: pInsertIndex
    };
    var cbs = {
      callback: dojo.hitch(this, this._requestInsertItemsCallback, pIdArray, pInsertIndex)
      // no errback:
    };
    this.send(true, params, cbs);
  },

  // Issue a request to add one or more items into a collection on the server.
  requestAddItems: function(pIdArray) {
    var idString = "";
    for (var i = 0; i < pIdArray.length; i++) {
      if (i > 0) {
        idString += ",";
      }
      idString += pIdArray[i];
    }
    var params = {
      addIds: idString
    };
    var cbs = {
      callback: dojo.hitch(this, this._requestAddItemsCallback, pIdArray)
      // no errback:
    };
    this.send(true, params, cbs);
  },

  requestReorder: function(pEncodedIndices) {
    var params = {
      reorderIndices: pEncodedIndices
    };
    var cbs = {
      callback: dojo.hitch(this, this._requestReorderCallback, pEncodedIndices)
      // no errback:
    };
    this.send(true, params, cbs);
  },

  ///////////////////////////////////////////////////////////////////////////
  //   Callback methods which are run after a server request is completed  //
  ///////////////////////////////////////////////////////////////////////////

  // Parse results of a request and stuff data into the model.
  _requestRowsCallback: function(pRowIndex, pData) {
    for (var i=0, len=pData.length; i<len; i++) {
      this.setRow(pData[i], pRowIndex + i);
    }
  },

  // Delete the item from the client side model.
  _requestDeleteCallback: function(pRemoveIndex, pData) {
    if (pData.removedItemIndex > -1) {
      var selected = [];
      selected[0] = pData.removedItemIndex;
      this.remove(selected);
    }
  },

  // Update the client side model once new items are inserted.
  _requestInsertItemsCallback: function(pIdArray, pInsertIndex, pData) {
    // Display info about any duplicates detected.
    if (pData.numDuplicates > 0) {
      var msg = this.duplicatesDetectedMessage;
      msg = msg.replace('{0}', pData.numItemsInserted);
      msg = msg.replace('{1}', pData.numDuplicates);
      parent.messages.addAlert(msg);
    }

    if (pData.numItemsInserted > 0) {
      this.count = parseInt(this.count) + parseInt(pData.numItemsInserted);
      this.clearData();
      var args = [];
      args[0] = pInsertIndex;
      //this.insertion(args);
                        // Pass additional parameter specifying how many items have been inserted (so the view can sparkle)
                        this.insertion(args, pData.numItemsInserted);
    }
  },

  // Update the client side model once new items are added.
  _requestAddItemsCallback: function(pIdArray, pData) {
    if (pData.numItemsAdded > 0) {
      this.count = parseInt(this.count) + parseInt(pData.numItemsAdded);
      this.clearData();
      this.insertion();
    }
  },

  // Clear the client side model after a reorder operation.
  _requestReorderCallback: function(pEncodedIndices, pData) {
    if (pData.numItemsReordered > 0) {
      this.clearData();
      this.notify("Reorder", null);
    }
  },

  ///////////////////////////////////////////////////////////////////////////
  //      Utility methods to invoke grid operations                        //
  ///////////////////////////////////////////////////////////////////////////

  removeRow: function(pRemoveIndex) {
    // remove the faked row data
    this.remove([pRemoveIndex]);
    // this.requestDelete(pRemoveIndex);
  },

  insertItems: function(pIdArray, pInsertIndex) {
    this.requestInsertItems(pIdArray, pInsertIndex);
  },

  addItems: function(pIdArray) {
    // insert a fake row at index 0
    var itemname = "New Item";
    var uri = "n&uuml;";
    var id = "id-new";
    var rowObj = [itemname, uri, id];
    this.insert(rowObj, 0);
    //this.requestAddItems(pIdArray);
  },

  reorder: function(pEncodedIndices) {
    this.requestReorder(pEncodedIndices);
  },

  // Disable sorting.
  canSort: function (pSortIndex) {
    return false;
  }

});

//----------------------------------------------------------------------------//
/**
 * Called when the user makes a selection from the asset picker.  This is just a
 * wrapper around the real onPick handler. Unfortunately, naming this
 * atg.assetmanager.gridEditorOnPick caused the asset picker callback mechanism
 * to fail.
 *
 * @param  pSelected  An object containing info about the selected asset
 * @param  pData      Data specified by the code that invoked the picker
 *                    (namely, the ID of the window variable referring to the
 *                    GridEditor instance that invoked the picker)
 */
GridEditor_onPick = function(pSelected, pData) {

  // Find the window variable that refers to the GridEditor instance that
  // invoked the asset picker.  Then, invoke its _onPick function.
  var paths = (pData.indexOf(".") >= 0 ? pData.split(".") : [pData]);
  var obj = window;
  for (var i = 0; i < paths.length; i++) {
    obj = obj[paths[i]];
  }

  if (obj) {
    obj._onPick(pSelected);
  }
  else {
    alert("Internal error: Can't locate grid editor " + pData);
  }
};
