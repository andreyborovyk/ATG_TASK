dojo.provide("atg.searchadmin.tree.DojoTreeMenu");

dojo.require("dijit.Menu");

dojo.extend(dijit.MenuItem, {
  redirectUrl: "",
  popUpUrl: "",
  //used in tpo tree for adding level attribute to url 
  levelSource: null,
  
  action: "",
  formHandler: "",
  handleMethod: "",
  handlerIdField: "",
  handlerNameField: "",
  
  // new/rename action attributes
  renameFieldName: "",
  labelNewItem: "",
  
  // cut/copy/paste action attributes
  clipboard: "",
  handleCutPasteMethod: "",
  handleCopyPasteMethod: "",
  
  _onClick: function(e){
    if (this.disabled){
      return;
    }
    var menu = dijit.getEnclosingWidget(this.domNode.parentNode);
    if (!menu){
      return;
    }
    var store = menu.currentNode.tree.store;
    var id = store.getNodeId(menu.currentNode.item);
    var redirectUrl = this.redirectUrl;
    var node = menu.currentNode;
    if (redirectUrl.length > 0){
      if (redirectUrl.indexOf("=") == redirectUrl.length - 1){
        redirectUrl = redirectUrl + id;
      }
      redirectUrl = this.addLevelToUrl(redirectUrl, store, node.item);
      loadRightPanel(redirectUrl);
    } else if (this.popUpUrl.length > 0){
      var url = this.addLevelToUrl(this.popUpUrl + id, store, node.item);
      top.showPopUp(url);
    } else if (this.action.length > 0){
      store.currentMenuItem = this;
      switch (this.action){
        case "rename":
          renameNode(node, dojoConstants.actions.update);
          break;
        case "copyToNew":
          store.doCopyToNewNode(node);
          break;
        case "new":
          //index must be next after currentNode
          var nodeType = store.getNodeType(node.item);
          createNewNode(node.getParent(), node.getIndexInParent() + 1, this.labelNewItem, nodeType);
          break;
        case "newChild":
          var nodeType = store.getNodeType(node.item);
          if (node.isExpandable && !node.isExpanded){
            node.tree._expandNode(node);
          }
          createNewNode(node, 0, this.labelNewItem, nodeType);
          break;
        case "cut":
          doClipboardOperation(node, dojoConstants.actions.cut);
          break;
        case "copy":
          doClipboardOperation(node, dojoConstants.actions.copy);
          break;
        case "paste":
          store.doPasteNode(node.getParent(), node.getIndexInParent() + 1);
          break;
        case "pasteChild":
          store.doPasteNode(node, 0);
          break;
        case "pasteAsChild":
          store.doPasteNode(node, 0);
          break;
        case "cancelSelection":
          doCancelSelection(node)
          break;
        case "sort":
          store.doSortChildren(node);
          break;
      }
    }
    menu._onBlur();
  },
  
  addLevelToUrl: function(url, store, item) {
    if (this.levelSource){
      if (url.indexOf("?") != url.length - 1) {
        url += "&";
      }
      url += "level=";
      if (this.levelSource == "nodeType") {
        url += store.getNodeType(item);
      }
    }
    return url;
  }
});

dojo.declare("atg.searchadmin.tree.DojoTreeMenu", dijit.Menu, {
  hiddenClasses: [],
  disabledClasses: [],
  currentNode: null,
  classPrefix: "context_menu_",
  
  _openMyself: function(/*Event*/ e){
    for (var j=0;j<this.hiddenClasses.length; j++){
      dojo.query("." + this.classPrefix + this.hiddenClasses[j]).forEach(function(i){ 
        i.style.display = "";
      });
    }
    for (var j=0;j<this.disabledClasses.length; j++){
      dojo.query("." + this.classPrefix + this.disabledClasses[j]).forEach(function(i){
        if (dijit.getEnclosingWidget(i).setDisabled){
          dijit.getEnclosingWidget(i).setDisabled(false);
        }
      });
    }
    var node = dijit.getEnclosingWidget(e.target);
    if (node.tree.isEditing){
      return;
    }
    this.currentNode = node;
    var nodeType = node.tree.store.getNodeType(node.item);
    this.hiddenClasses = nodeInfo[nodeType].actionsHided;
    for (var j=0;j<this.hiddenClasses.length; j++){
      dojo.query("." + this.classPrefix + this.hiddenClasses[j]).forEach(function(i){ 
        i.style.display = "none";
      });
    }
    this.disabledClasses = nodeInfo[nodeType].actionsDisabled;
    for (var j=0;j<this.disabledClasses.length; j++){
      dojo.query("." + this.classPrefix + this.disabledClasses[j]).forEach(function(i){
        if (dijit.getEnclosingWidget(i).setDisabled){
          dijit.getEnclosingWidget(i).setDisabled(true);
        } 
      });
    }
    this.inherited("_openMyself",arguments);
  } 
  
});

