
dojo.provide("atg.assetmanager.saveconfirm");

atg.assetmanager.saveconfirm = {

  mTransientAsset: false,
  mReadOnlyAsset: false,
  mApplyToAllMode: false,
  mListMode: false,
  mRightURL: null,
  mTopURL: null,
  mFinishSaveConfirmFunction: null,
  mFinishSaveConfirmFunctionName: null,
  mFinishSaveConfirmParam: null,

  // -------------------------------------
  //
  // initialize variables
  //
  initialize: function (pTransientAsset, pReadOnlyAsset, pApplyToAllMode, pListMode) {
    this.mTransientAsset = pTransientAsset;
    this.mReadOnlyAsset = pReadOnlyAsset;
    this.mApplyToAllMode = pApplyToAllMode;
    this.mListMode = pListMode;
  },

  // -------------------------------------
  //
  // initialize popup dialog string variables
  //
  initializeDialogStrings: function (pConfirmCreateButtonText, pDontCreateButtonText, pConfirmCreateMessage, pConfirmCreateTitle,
                                     pConfirmSaveButtonText, pDontSaveButtonText, pConfirmSaveMessage, pConfirmSaveTitle) {
    this.mConfirmCreateButtonText =  pConfirmCreateButtonText;
    this.mDontCreateButtonText = pDontCreateButtonText;
    this.mConfirmCreateMessage = pConfirmCreateMessage;
    this.mConfirmCreateTitle = pConfirmCreateTitle;
    this.mConfirmSaveButtonText = pConfirmSaveButtonText;
    this.mDontSaveButtonText = pDontSaveButtonText;
    this.mConfirmSaveMessage = pConfirmSaveMessage;
    this.mConfirmSaveTitle = pConfirmSaveTitle;
   },


  // -------------------------------------
  //
  // popup the dialog
  //
  showSaveCreateConfirmDialog: function(pRightURL) {
    window.top.document.getElementById("saveCreateConfirmDialogBody").style.display = "block";
    window.top.dijit.byId("saveCreateConfirmDialog").show();
  },


  // -------------------------------------
  //
  // This function will prompt the user to save or create before preceeding.
  // Pass in the javascript function (finishSaveFunction/Name, and finishSaveFunctionParam) that should be called if the
  // user chooses to continue OR pass in the url to proceed to.
  //
  // If the asset isn't modified, it just continues to the rightURL/topURL or calls javascript function without prompting user.
  //
  saveBeforeLeaveAsset: function(pRightURL, pTopURL, pFinishSaveFunction, pFinishSaveParam, pFinishSaveFunctionName, pHideDontCreateButton, pIsList) {

    if (actionsAreLocked()) {
      return;
    }

    // pop up the dialog if the asset is modified.

    if (!this.mReadOnlyAsset && this.checkForModifiedAsset(pIsList)) {
      this.promptSaveBeforeLeaveAsset(pRightURL, pTopURL, pFinishSaveFunction, pFinishSaveParam, pFinishSaveFunctionName, pHideDontCreateButton);
      return;
    }

    // or continue to URL or javascript function

    else if (top.window.frames['rightPaneIframe'] && pRightURL) {
      window.frames.rightPaneIframe.document.location = pRightURL;
    }
    else if (pTopURL) {
      window.top.document.location = pTopURL;
    }
    else if (pFinishSaveFunction != null) {
      pFinishSaveFunction(pFinishSaveParam);
    }
  },


  // -------------------------------------
  //
  // If the asset is modified, this function will prompt the user to save or create and return true.
  // Pass in the javascript function (finishSaveFunction/Name, and finishSaveFunctionParam) that should be called if the
  // user chooses to save OR pass inthe url.
  //
  // If the asset isn't modified, it returns false.  It is up to the caller to proceed.
  //
  saveDontLeaveAsset: function(pRightURL, pTopURL, pFinishSaveFunction, pFinishSaveParam, pFinishSaveFunctionName, pHideDontCreateButton, pIsList) {

    if (actionsAreLocked()) {
      return false;
    }

    if (!this.mReadOnlyAsset && this.checkForModifiedAsset(pIsList)) {
      this.promptSaveBeforeLeaveAsset(pRightURL, pTopURL, pFinishSaveFunction, pFinishSaveParam, pFinishSaveFunctionName, pHideDontCreateButton);
      return true;
    }
    return false;
  },


  // -------------------------------------
  //
  // shortcut when only passing in a top url or function.
  //
  saveBeforeLeaveParentFrame: function(pTopURL, pFinishSaveFunction, pFinishSaveFunctionName ) {
   if (this.checkForModifiedAsset()) {
     this.saveBeforeLeaveAsset(null, pTopURL, pFinishSaveFunction, null, pFinishSaveFunctionName);
     return;
   }
   if (pTopURL != null)
     window.top.document.location = pTopURL;
   else pFinishSaveFunction();
  },


  // -------------------------------------
  //
  // called from the confirm dialog, when the user chooses "Don't Save" or "Don't Create".
  //
  finishLeaveAsset: function() {
     if (top.window.frames['rightPaneIframe'] && this.mRightURL) {
       window.frames.rightPaneIframe.document.location = this.mRightURL;
     }
     if (this.mTopURL) {
       window.top.document.location = this.mTopURL;
     }
     if (this.mFinishSaveConfirmFunction) {
       this.mFinishSaveConfirmFunction(this.mFinishSaveConfirmParam);
     }
  },


  // -------------------------------------
  //
  // Called from the save confirm dialog, when the user decides to save before continuing.
  // After the save, you can set the parent url or call a parent javascript function using params in the url
  //
  finishSaveBeforeLeaveAsset: function() {
    if (this.mTransientAsset) {

      if (this.mBeforeFinishSaveFunction){
        this.mBeforeFinishSaveFunction(this.mBeforeFinishSaveParam);
        return;
      }

      var addSuccessURL = window.frames.rightPaneIframe.document.getElementById("addSuccessURLField");
      if (addSuccessURL != null && this.mRightURL) {
        addSuccessURL.value = this.mRightURL;
      }
      if (addSuccessURL && this.mTopURL) {
        addSuccessURL.value = addSuccessURL.value + "&setParentURL=" + escape(this.mTopURL);
      }
      if (addSuccessURL && this.mFinishSaveConfirmFunctionName) {
        addSuccessURL.value = addSuccessURL.value + "&finishSaveConfirmFunction=" + this.mFinishSaveConfirmFunctionName;
        if (this.mFinishSaveConfirmParam) {
          addSuccessURL.value = addSuccessURL.value + "&finishSaveConfirmParam=" + this.mFinishSaveConfirmParam;
        }
      }
      window.frames.rightPaneIframe.actionButtonClicked("addButton");
    } else {
      var updateSuccessURL = window.frames.rightPaneIframe.document.getElementById("updateSuccessURLField");
      if (updateSuccessURL != null && this.mRightURL) {
        updateSuccessURL.value = this.mRightURL;
      }
      if (updateSuccessURL && this.mTopURL) {
        updateSuccessURL.value = updateSuccessURL.value + "&setParentURL=" + escape(this.mTopURL);
      }
      if (updateSuccessURL && this.mFinishSaveConfirmFunctionName) {
        updateSuccessURL.value = updateSuccessURL.value + "&finishSaveConfirmFunction=" + this.mFinishSaveConfirmFunctionName;
        if (this.mFinishSaveConfirmParam) {
          updateSuccessURL.value = updateSuccessURL.value + "&finishSaveConfirmParam=" + this.mFinishSaveConfirmParam;
        }
      }

      if (top.window.frames['rightPaneIframe']) {
        if (this.mApplyToAllMode) {    //<c:when test="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_APPLY_TO_ALL}">
            window.frames.rightPaneIframe.actionButtonClicked("applyToAllButton");
         } else if (this.mListMode) { // <c:when test="${multiEditMode == MODE_MULTI && multiEditOperation == OPERATION_LIST}">
            window.frames.rightPaneIframe.actionButtonClicked("listEditButton");
         } else {
            window.frames.rightPaneIframe.actionButtonClicked("saveButton");
         }
       }

    }
  },


  // -------------------------------------
  //
  // save the urls and functions then pop up the confirmation dialog
  //
  promptSaveBeforeLeaveAsset: function( pRightURL, pTopURL, pFinishSaveFunction, pFinishSaveParam, pFinishSaveFunctionName, pHideDontCreateButton) {

     // save the URLs and javascript function, it is used when return from confirm dialog
     this.mRightURL = pRightURL;
     this.mTopURL = pTopURL;
     this.mFinishSaveConfirmFunction = pFinishSaveFunction;
     this.mFinishSaveConfirmFunctionName = pFinishSaveFunctionName;
     this.mFinishSaveConfirmParam = pFinishSaveParam;

     var saveButtonText = window.top.document.getElementById("confirmSaveButton");
     var noSaveButtonText = window.top.document.getElementById("confirmNoSaveButton");
     var noSaveButton = window.top.document.getElementById("confirmNoSaveButtonOuter");
     var confirmMessage = window.top.document.getElementById("confirmSaveMessage");
     var confirmTitle = window.top.document.getElementById("confirmSaveTitle");

     if (this.mTransientAsset) {
        // set create labels
         if (saveButtonText) {
           saveButtonText.innerHTML = this.mConfirmCreateButtonText;// 'assetEditor.confirm.create';
         }

         if (noSaveButtonText) {
           if (pHideDontCreateButton) {
             noSaveButton.style.display="none";
           }
           else {
             noSaveButtonText.innerHTML = this.mDontCreateButtonText; // 'assetEditor.confirm.nocreate'
             noSaveButton.style.display="inline";
           }
         }
         if (confirmMessage) {
           confirmMessage.innerHTML = this.mConfirmCreateMessage; // 'assetEditor.confirmCreateMessage'
         }
         if (confirmTitle) {
           confirmTitle.innerHTML = this.mConfirmCreateTitle; // 'assetEditor.confirmCreateTitle'
         }
      } else {
        // set save labels
        if (saveButtonText) {
          saveButtonText.innerHTML= this.mConfirmSaveButtonText; // 'assetEditor.confirm.save'
        }
        if (noSaveButtonText) {
          noSaveButtonText.innerHTML = this.mDontSaveButtonText; //  'assetEditor.confirm.nosave'
          noSaveButton.style.display="inline";
        }
        if (confirmMessage) {
          confirmMessage.innerHTML = this.mConfirmSaveMessage; // 'assetEditor.confirmSaveMessage'
        }
        if (confirmTitle) {
          confirmTitle.innerHTML = this.mConfirmSaveTitle; // 'assetEditor.confirmSaveTitle'
        }
    }

    this.showSaveCreateConfirmDialog();

   },


  // -------------------------------------
  //
  // check if the asset is modified
  //
  checkForModifiedAsset: function(isList) {
    if (this.mDisableSaveConfirmation)
      return false;

    if (this.mApplyToAllMode) { //<c:when test="${multiEditMode == MODE_MULTI && (multiEditOperation == OPERATION_LIST || multiEditOperation == OPERATION_APPLY_TO_ALL)}">

        if (isList)
          return false;
        else if (window.frames.rightPaneIframe.isAssetModified)
          return window.frames.rightPaneIframe.isAssetModified();

     } else if (window.frames.rightPaneIframe && window.frames.rightPaneIframe.isAssetModified) {
        return  window.frames.rightPaneIframe.isAssetModified();
     }

     return false;
  },
  
  // need for SearchTest assets
  setBeforeFinishSave: function(pFunction, pParam) {
    this.mBeforeFinishSaveFunction = pFunction;
    this.mBeforeFinishSaveParam = pParam;
  },
  
  getRightURL: function() {
    return this.mRightURL;
  },
  
  getTopURL: function() {
    return this.mTopURL;
  }  
}


