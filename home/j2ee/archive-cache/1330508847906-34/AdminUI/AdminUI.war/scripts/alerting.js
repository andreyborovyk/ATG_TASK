var alerting = {
  maxMessageTitleLength: 90,
  cuttedTitleLength: 80,
  alertHeaderAShow: "alertHeaderAShow",
  alertHeaderAHide: "alertHeaderAHide",
  alertHeaderTitleShow: "alertHeaderTitleShow",
  alertHeaderTitleHide: "alertHeaderTitleHide",
  storedMessagesData: null,

  showMessages: function(messages, titleMessages) {
    this._clearMessages();
    if (messages && messages.length > 0) {
      this._printMessages(messages, titleMessages);
      // adjust tree (tree_common.js)
      applyTreeStructure(messages);
    }
  },

  showPopupMessages: function(messages) {
    // print messages
    var messList = document.getElementById("alertListPopup");
    if (messList) {
      var messagesHTML = "";
      if (messages) {
        for (var i = 0; i < messages.length; i++) {
          messagesHTML += "<span class=" + messages[i].type + "Popup" + ">" +
              this._escapeXml(messages[i].message) + "</span><br>";
        }
      }
      messList.innerHTML = messagesHTML;
    }
  },
  
  showStoredMessages: function(){
    if (this.storedMessagesData) {
      alerting.showMessages(this.storedMessagesData.alerting.messages, this.storedMessagesData.alerting.title);
      this.storedMessagesData = null;
    }
  },  

  detailsOnClick: function() {
    var detail = document.getElementById("alertPop");
    var showAlertPopup = detail.style.display == 'none';
    detail.style.display = showAlertPopup ? "block" : "none";

    var detailText = document.getElementById("alert");
    detailText.title = showAlertPopup ? this.alertHeaderTitleHide : this.alertHeaderTitleShow;

    var detailTextValue = detailText.getElementsByTagName("span");
    detailTextValue[1].innerHTML = showAlertPopup ? this.alertHeaderAHide : this.alertHeaderAShow;
    detailTextValue[0].style.marginLeft = showAlertPopup ? "10px" : dojo.isIE ? "18px" : "15px";

    return false;
  },

  _markedErrorFieldNames: null,
  _messageTypeClasses: {error: "alertError", confirm: "alertConfirm", warning: "alertWarning", info: "alertInfo"},
  _clearMessages: function() {
    // hide title
    document.getElementById("alertPop").style.display = "none";
    document.getElementById("alert").style.display = "none";
    document.getElementById("alertBrief").style.display = "none";
    // clear alerting details
    var messList = top.frames.alertFrame.document.getElementById("alertList");
    if (messList) {
      messList.innerHTML = "";
    }
    // clear error field markers
    if (this._markedErrorFieldNames) {
      for (var i = 0; i < this._markedErrorFieldNames.length; i++) {
        var span = document.getElementById(this._markedErrorFieldNames[i] + "Alert");
        if (span) {
          span.title = "";
          span.className = "";
          ispan = span.getElementsByTagName("span");
          if (ispan && ispan.length > 0) {
            ispan[0].style.display = 'inline';
          }
        }
      }
      this._markedErrorFieldNames = null;
    }
  },
  // TODO re-check dojo for the same function
  _escapeXml: function(/*String*/str, /*Boolean*/noSingleQuotes){
    //summary:
    //    Adds escape sequences for special characters in XML: &<>"'
    //    Optionally skips escapes for single quotes
    str = str.replace(/&/gm, "&amp;").replace(/</gm, "&lt;").replace(/>/gm, "&gt;").replace(/"/gm, "&quot;");
    if(!noSingleQuotes){
      str = str.replace(/'/gm, "&#39;");
    }
    return str; // string
  },
  _printMessages: function(messages, titleMessages) {
    var detailsDoc = top.frames.alertFrame.document;
    var messagesElement = document.getElementById("alert");
    var oneMessageAsTilteElement = document.getElementById("alertBrief");
    var oneMessageAsTilteElementTitle = document.getElementById("alertBriefTitle");
    var titleField = document.getElementById("alertTitleMessage");
    var alertHeader = detailsDoc.getElementById("alertHeader");
    var showOnlyTitle = false;
    //avoid alert header disappearing
    if (alertHeader) alertHeader.style.display = "";
    if (messages.length == 1) {
      if (messages[0].message.length > this.maxMessageTitleLength) {
        titleField.innerHTML = this._escapeXml(this._prepareTitleMessage(messages[0].message));
        if (alertHeader) alertHeader.style.display = "none";
      } else {
        oneMessageAsTilteElementTitle.innerHTML = this._escapeXml(messages[0].message);
        showOnlyTitle = true;
      }
    } else {
      //title messages can be set by custom tag. If title messages set use them, else create title from 1st message.
      if (titleMessages) {
        titleField.innerHTML = this._escapeXml(this._prepareTitleMessage(titleMessages.label));
        if (alertHeader) alertHeader.innerHTML = this._escapeXml(titleMessages.title);
      } else {
        titleField.innerHTML = this._escapeXml(this._prepareTitleMessage(messages[0].message));
        if (alertHeader) alertHeader.innerHTML = this._escapeXml(messages[0].message);
      }
    }
    if (showOnlyTitle) {
      oneMessageAsTilteElementTitle.className = this._getTitleClass(messages);
      oneMessageAsTilteElement.style.display = 'inline';
      messagesElement.style.display = 'none';
    } else {
      messagesElement.className = this._getTitleClass(messages);
      //show detailed messages element.
      messagesElement.style.display = 'inline';
      oneMessageAsTilteElement.style.display = 'none';
      //add count of messages to "show/hide" element.
      var spans = messagesElement.getElementsByTagName("span");
      spans[2].innerHTML = messages.length + " ";
    }

    var mefn = new Array();
    for (var i = 0; i < messages.length; i ++) {
      var fieldName = messages[i].fieldName;
      //add message to html messages list. Don't show message if there is only one and it's size is suitable.
      if (!showOnlyTitle) {
        var li = detailsDoc.createElement("li");
        li.className = this._messageTypeClasses[messages[i].type];
        li.appendChild(detailsDoc.createTextNode(messages[i].message));
        detailsDoc.getElementById("alertList").appendChild(li);
      }
      //try to set focus on form field that contains user input error.
      if (i == 0) {
        try {
          var fields = document.getElementsByName(fieldName);
          if (fields.length > 0) {
            fields[0].focus();
          }
        } catch(e) {
        }
      }
      //try to mark error field. If there is special element before field, some icon or something else appear before field.
      if (fieldName && fieldName.length > 0){
        var span = document.getElementById(fieldName + "Alert");
        if (span) {
          span.title = messages[i].message;
          span.className = "error";
          var ispan = span.getElementsByTagName("span");
          if (ispan[0]) {
            ispan[0].style.display = 'none';
          }
          mefn[mefn.length] = fieldName;
        }
      }
    }
    if (mefn.length > 0) {
      this._markedErrorFieldNames = mefn;
    }
  },
  _getTitleClass: function(messages) {
    var result = new Object;
    for (var i = 0; i < messages.length; i++) {
      result[messages[i].type] = true;
    }
    return result["load"] ? "load" : result["error"] ? "error" : result["warning"] ? "warning" :
      result["information"] || result["info"] ? "info" : "confirm";
  },
  _prepareTitleMessage: function(message) {
    var result = message;
    if (result.length > this.maxMessageTitleLength) {
      result = message.substring(0, this.cuttedTitleLength);
      result += "...";
    }
    return result;
  },

  isConfirmMessage: function(message) {
    return message.type == "confirm";
  }
};
