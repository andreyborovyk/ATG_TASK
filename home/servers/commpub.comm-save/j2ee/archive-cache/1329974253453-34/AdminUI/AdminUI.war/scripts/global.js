/* drag and drop panel resizing */

/* Puts focus on first input elements in first page form, if found */
function focusFirstFormElement() {
  var popup = dijit.byId('dialog');
  var isPopupOpened = popup && popup.open;
  var form = isPopupOpened ? popup.domNode.getElementsByTagName("form")[0] : window.document.forms[0];
  if (form) {
    var allElements = form.elements;
    if (allElements) {
      for (var i = 0; i < allElements.length; i++) {
        var el = allElements[i];
        if (el.type != "hidden" && el.type != "submit" && el.type != "button" && el.type != "select-one" &&
            !el.disabled && el.style.display != "none") {
          try {
            el.focus();
            return;
          } catch (e) {
            // do nothing with exception, continue to iterate over elements.
            // this exception can be caused, for example if we are trying to set focus on visible element, but in
            // invisible div.
          }
        }
      }
    }
  }
}

/* Open the popup window with url parameter */
function showPopUp(popupUrl) {
  var popup = dijit.byId("dialog");
  if (!popup) {
    // init popup window
    popup = new dijit.Dialog({id: 'dialog', "class": "popUpFrame"}, "dialogDiv");
    popup.startup();
    document.getElementById("dialogContentPane").style.display = "";
  }
  popup.show();
  dijit.byId("dialogContentPane").setHref(popupUrl);
  return false;
}

/* Close the popup window */
function closePopUp() {
  var popup = dijit.byId('dialog');
  if (popup) {
    popup.hide();
  }
  return false;
}

//functions for hiding popup menu after mouse moving   
function MenuItem(el, submenuEl, divEl) {
  this.interval = 200;
  this.timeout = null;
  var self = this;
  el.onmouseover = function() {self.over()};
  el.onmouseout = function() {self.out()};
  this.el = el;
  this.divEl = divEl;
  this.submenuEl = submenuEl;
  submenuEl.onmouseover = function() {self.clearTimer()};
  submenuEl.onmouseout = function() {self.out()};
};
MenuItem.prototype.clearTimer = function() {
  if (this.timeout) {
    window.clearTimeout(this.timeout);
    this.timeout = null;
  }
};
MenuItem.prototype.hide = function() {
  this.clearTimer();
  this.submenuEl.style.display = "none";
};
MenuItem.prototype.over = function() {
  this.clearTimer();
  this.submenuEl.style.left = findPosX(this.el) -6 + 'px';
  this.submenuEl.style.display = "";
  var offPage = (findPosX(this.divEl) + this.divEl.offsetWidth) - (findPosX(this.el) -6 + this.submenuEl.offsetWidth);
  if (offPage < 0){
    this.submenuEl.style.left = findPosX(this.el) + offPage -6 + 'px';
  }


};
MenuItem.prototype.out = function() {
  var self = this;
  this.timeout = window.setTimeout(function() {self.hide();}, this.interval);
};

var areaSupport = {
  currentAreaId: null,
  selectTab: function(areaId) {
    if (areaId != this.currentAreaId) {
      var navPaneElements = document.getElementById("tabContainer").getElementsByTagName("LI");
      if (this.currentAreaId != null) {
        document.getElementById("area_" + this.currentAreaId).className = "";
        document.getElementById("tab_area_" + this.currentAreaId).style.display = "none";
        this.onResize();
      }
      this.currentAreaId = areaId;
      document.getElementById("area_" + areaId).className = "current";
      var area = document.getElementById("tab_area_" + areaId)
      if (area) {
        area.style.display = "";
      }
      this.onResize();
    }
    return false;
  },
  switchArea: function(areaId) {
    //this.selectTab(areaId);
    return loadRightPanel(uiConfig.contextPath + '/templates/treestate.jsp?areaId=' + areaId);
  },
  onResize: function() {
    var area = dijit.byId("tab_area_" + this.currentAreaId);
    if (area) {
      area.resize();
    }
  }
};

/****  CLOSE NONE PRODUCTION CODE HERE FOR DEMO PURPOSES ONLY ****/
