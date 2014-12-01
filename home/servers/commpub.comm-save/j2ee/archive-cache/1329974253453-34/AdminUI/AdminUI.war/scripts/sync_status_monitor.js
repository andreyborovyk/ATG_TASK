var syncStatusMonitor = {
  autoRefresh: true,
  updatePerRequest: true,
  isWaitingForUserInput: false,
  savingMessage: null,
  needSavingMessage: false, 
  
  //this variable is localized in sync_sm_estimation.jsp
  physicalPartitionError: "Physical Partitions should contain integer positive number only.",

  refresh: function() {
    var button = document.getElementById('refreshView');
    if (button) {
      this.updatePerRequest = true;
      button.click();
    }
  },
  _timeoutId: null,
  refreshHandleResponse: function(data, timeout) {
    if (data.syncStatusMonitor && this.updatePerRequest) {
      dijit.byId("refreshContent").setContent(data.syncStatusMonitor);
      this._showFooterPane(data.isInProgress, data.isWaitingForUserInput);
      if (this._timeoutId) {
        window.clearTimeout(this._timeoutId);
        this._timeoutId = null;
      }
      if (data.isInProgress && this.autoRefresh && !this.isWaitingForUserInput) {
        this._timeoutId = window.setTimeout(this.refresh, timeout);
      }
      var cancelIndexingButton = document.getElementById("cancelIndexingButton");
      if (cancelIndexingButton) {
        cancelIndexingButton.disabled = data.cancelButtonDisable;
        cancelIndexingButton.copiedElement.disabled = data.cancelButtonDisable;
      }
    }
    if (this.savingMessage) {
      alerting.showMessages(this.savingMessage.messages, this.savingMessage.title);
    }
    return true;
  },

  setAutoRefresh: function(value) {
    this.autoRefresh = value;
    if (value) {
      this.refresh();
    } else {
      this.updatePerRequest = false;
      this._showFooterPane(true, false);
    }
    return true;
  },
  displayDetails: function(showDetails) {
    document.getElementById('showDetails').value = showDetails;
    this.refresh();
    return false;
  },
  beforeContinue: function() {
    this.isWaitingForUserInput = false;
    this.updatePerRequest = true;
    return false;
  },

  _showFooterPane: function(value, isWaiting){
    document.getElementById('paneFooterCopy').style.display = value ? "" : "none";
    dijit.byId('viewPane').layout();
    var refreshIndexingButton = document.getElementById('refreshIndexingButton');
    refreshIndexingButton.style.display = isWaiting ? "none" : "";
    refreshIndexingButton.copiedElement.style.display = isWaiting ? "none" : "";
    var isDisableAutoRefreshButton = isWaiting || this.autoRefresh;
    var enableRefreshButton = document.getElementById('enableRefreshButton');
    enableRefreshButton.style.display = isDisableAutoRefreshButton ? "none" : "";
    enableRefreshButton.copiedElement.style.display = isDisableAutoRefreshButton ? "none" : "";
    var disableRefreshButton = document.getElementById('disableRefreshButton');
    disableRefreshButton.style.display = isWaiting || !this.autoRefresh ? "none" : "";
    disableRefreshButton.copiedElement.style.display = isWaiting || !this.autoRefresh ? "none" : "";
    var continueIndexingButton = document.getElementById('continueIndexingButton');
    continueIndexingButton.style.display = isWaiting ? "" : "none";
    continueIndexingButton.copiedElement.style.display = isWaiting ? "" : "none";
    this.isWaitingForUserInput = isWaiting;
    return true;
  },

  updateDeplPlan: function(pSyncTaskId, pPartitionId) {
    var result = this._validateOnChange(pPartitionId);
    var continueIndexingButton = document.getElementById("continueIndexingButton");
    if (continueIndexingButton) {
      continueIndexingButton.disabled = !result;
      continueIndexingButton.copiedElement.disabled = !result;
    }
    if (!result) {
      alerting.showMessages(new Array({type: "error", message: this.physicalPartitionError,
        fieldName: document.getElementById(pPartitionId).name}));
    } else {
      alerting.showMessages();
      var physicalPartitionValue = document.getElementById(pPartitionId).value;
      if( physicalPartitionValue.trim() != '' && physicalPartitionValue.length != 0 ){
        var data = {
          handlerFields: {
            physicalPartition: physicalPartitionValue,
            physicalPartitionId: pPartitionId,
            syncTaskId: pSyncTaskId,
            contentViewURL: top.uiConfig.contextPath + '/searchadmin/sync_sm_deployment_plan.jsp?syncTaskId=' + pSyncTaskId
          },
          requestData: {
            formHandler: "/atg/searchadmin/adminui/formhandlers/SynchronizationStatusMonitorFormHandler",
            handlerMethod: "handleRecalculate"
          }
        };
        var bindArgs = {
          url: top.uiConfig.contextPath + '/tree_controller.dojo',
          content: {
            action: 'invokeHandlerMethod',
            data: dojo.toJson(data)
          },
          load: function(pData, pEvent) {
            var contentPane = dijit.byId('output');
            contentPane.setContent(pData);
          },
          error: function(pType, pData, pEvent) {
            // TODO: error processing
            var contentPane = dijit.byId('output');
            contentPane.setContent("Error");
          },
          mimetype: 'text/html'
        };
        dojo.xhrPost(bindArgs);
      }
    }
  },
  _numbersArray: "0123456789",
  _validateOnChange: function(pPartitionId){
    var str = document.getElementById(pPartitionId).value;
    var valid = false;
    var value = 0;
    for (var i = 0; i < str.length; i++) {
      var pos = this._numbersArray.indexOf(str.charAt(i));
      if (pos == -1) {
        return false;
      } else {
        value = value * 10 + pos;
      }
    }
    return (value > 0);
  }
};
