dojo.provide("atg.searchadmin.tree.LazyLoadStore");

dojo.require("dojo.data.ItemFileWriteStore");

dojo.declare("atg.searchadmin.tree.LazyLoadStore", dojo.data.ItemFileWriteStore, {

  label: "",
  identifier: "",
  activeProjectId: null,
  
  constructor: function(/* Object */ params) {
    this._labelAttr = params.label;
    this._features['dojo.data.api.Identity'] = params.identifier;
    this.activeProjectId = params.activeProjectId;
  },

  /**
   * Performs XmlHttpRequest to provided url and parse received JSON data
   */
  _fetchItems: function(/* Object */ keywordArgs, /* Function */ findCallback, /* Function */ errorCallback) {
    var store = this;

    //constructor initializes '_jsonFileUrl' property with the value passed
    //to the 'url' attribute of the dojo.data.ItemFileReadStore(atg.widget.tree.LazyLoadStore) widget
    if(this._jsonFileUrl) {
      if (!keywordArgs.widgetId){
        keywordArgs.widgetId = 0;
      }
      var node = {widgetId: this.getIdFromIdentity(keywordArgs.widgetId)};
      var treeNodeType = this.getNodeTypeFromIdentity(keywordArgs.widgetId);
      var indexItems = this.getIndexItemsFromIdentity(keywordArgs.widgetId);
      var params = {expandUrl: keywordArgs.expandUrl, treeNodeType: treeNodeType,  
            node: node, indexItems: indexItems};
      var xhrArgs = {
        url: this.getJsonRequestUrl("getChildren"),
        content: {data: dojo.toJson(params)},
        handleAs: "json-comment-optional",
        sync: true
      };

      //this callback function parses received JSON
      //it converts all items into format used by ItemFileReadStore object and stores them in '_arrayOfAllItems' property.
      var handleLoad = function(data) {
        var items = data.children;//items;
        dojo.forEach(items, function(item) {
            for(attr in item) {
              var value = item[attr];
              if(!dojo.isArray(value)) {
                item[attr] = [value];
              }
            }
            item[this._storeRefPropName] = this;
            item[this._itemNumPropName] = this._arrayOfAllItems.length;
            this._arrayOfAllItems.push(item);
          }, store);
        findCallback(items, keywordArgs);
      };

      var handleError = function(error) {
        errorCallback(error, keywordArgs);
      };

      var doFetch = dojo.xhrPost(xhrArgs);
      doFetch.addCallback(handleLoad);
      doFetch.addErrback(handleError);
    } else {
      errorCallback(new Error("atg.widget.tree.LazyLoadStore: Required attribute URL isn't provided."), keywordArgs);
    }
  },
  
  newItem: function(/* Object? */ keywordArgs, /* Object? */ parentInfo){
    // summary: See dojo.data.api.ItemFileWriteStore.newItem()

    this._assert(!this._saveInProgress);

    if(typeof keywordArgs != "object" && typeof keywordArgs != "undefined"){
      throw new Error("newItem() was passed something other than an object");
    }
    var newIdentity = null;
    var identifierAttribute = this._getIdentifierAttribute();
    if(identifierAttribute === Number){
      newIdentity = this._arrayOfAllItems.length;
    }else{
      newIdentity = keywordArgs[identifierAttribute];
      if (typeof newIdentity === "undefined"){
        throw new Error("newItem() was not passed an identity for the new item");
      }
      if (dojo.isArray(newIdentity)){
        throw new Error("newItem() was not passed an single-valued identity");
      }
    }
    
    // make sure this identity is not already in use by another item, if identifiers were 
    // defined in the file.  Otherwise it would be the item count, 
    // which should always be unique in this case.
    if(this._itemsByIdentity){
      this._assert(typeof this._itemsByIdentity[newIdentity] === "undefined");
    }
    this._assert(typeof this._pending._newItems[newIdentity] === "undefined");
    this._assert(typeof this._pending._deletedItems[newIdentity] === "undefined");
    
    var newItem = {};
    newItem[this._storeRefPropName] = this;   
    newItem[this._itemNumPropName] = this._arrayOfAllItems.length;
    //Clone over the properties to the new item
    for(var key in keywordArgs){
      if(key === this._storeRefPropName || key === this._itemNumPropName){
        // Bummer, the user is trying to do something like
        // newItem({_S:"foo"}).  Unfortunately, our superclass,
        // ItemFileReadStore, is already using _S in each of our items
        // to hold private info.  To avoid a naming collision, we 
        // need to move all our private info to some other property 
        // of all the items/objects.  So, we need to iterate over all
        // the items and do something like: 
        //    item.__S = item._S;
        //    item._S = undefined;
        // But first we have to make sure the new "__S" variable is 
        // not in use, which means we have to iterate over all the 
        // items checking for that.
        throw new Error("encountered bug in ItemFileWriteStore.newItem");
      }
      var value = keywordArgs[key];
      if(!dojo.isArray(value)){
        value = [value];
      }
      newItem[key] = value;
    }
    if(this._itemsByIdentity){
      this._itemsByIdentity[newIdentity] = newItem;
    }
    this._arrayOfAllItems.push(newItem);

    this._pending._newItems[newIdentity] = newItem;
    
    this.onNew(newItem, parentInfo); // dojo.data.api.Notification call
    return newItem; // item
  },
  
  currentMenuItem: null,
  
  doRenameNode: function(treeNode, newName, parentId){
    if (!this.currentMenuItem){
      return;
    }
    var isNew = false;
    var handlerFields = this.fillHandlerFields(treeNode.tree);
    if (this.currentMenuItem.handlerIdField){
      handlerFields[this.currentMenuItem.handlerIdField] = this.getNodeId(treeNode.item);
    }
    handlerFields[this.currentMenuItem.renameFieldName] = newName;
    if (parentId){
      handlerFields.parentId = parentId;
      isNew = true;
    }
    var index = this.getValue(treeNode.item, "index");
    if (index){
      handlerFields.index = index;
      var parentNode = treeNode.getParent();
      if (parentNode.isVirtual()){
        handlerFields.index += this.getValue(parentNode.item, "startIndex");
      }
    }
    var requestData = this.fillRequestData(this.currentMenuItem);
    var params = {handlerFields: handlerFields, requestData: requestData};
    var _this = this;
    var xhrArgs = {
      url: this.getJsonRequestUrl("rename"),
      content: {data: dojo.toJson(params) },
      handleAs: "json-comment-optional",
      sync: true,
      load: function(data) {
        // suggested data is dead, fresh data from server is used
        _this.doRenameNodeProcessResponse(data, treeNode, isNew);
      }
    };
    dojo.xhrPost(xhrArgs);
  },
  
  doRenameNodeProcessResponse: function(data, treeNode, isNew){
    var messagesTypes = analyzeResponceMessages(data.messages);
    if (!(messagesTypes.error)) {
      var tree = treeNode.tree;
      var isFolder = this.getValue(treeNode.item, "isFolder");
      var parentNode = treeNode.getParent();
      var parentExpanded = parentNode.isExpanded;
      var nodeType = this.getNodeType(treeNode.item);
      var parentNode = treeNode.getParent();
      this.deleteItem(treeNode.item);
      //in case of leaf _TreeNode should be removed manually
      if (treeNode.domNode){
        tree.deleteNode(treeNode);
      }
      this.save();
      
      if (isNew && (parentNode.isVirtual() || tree.groupInVirtualNodesOnAdd(parentNode.getParent()))){
        refreshVirtualNodes(parentNode.getParent());
      } else {
        var newItem = new Object();
        newItem.id = createCompositeId(nodeType, data.data.id);
        newItem.isFolder = isFolder;
        newItem.titleText = data.data.name;
        newItem.index = data.data.treeIndex ? data.data.treeIndex : data.data.seq;
        if (parentNode.isVirtual()){
          newItem.index -= this.getValue(parentNode.item, "startIndex");
        }
        pInfo = {item: parentNode.item, attribute: tree.childrenAttr};
        var newItem = this.newItem(newItem, pInfo);
        if (parentNode.isVirtual()){
          var childNodes = parentNode.getChildren();
          if (newItem.index == 0){
            this.refreshVirtualTitle(parentNode.item, data.data.name, true);
            this.refreshVirtualToolTip(parentNode.item);
          } 
          if (newItem.index == childNodes.length - 1){
            this.refreshVirtualTitle(parentNode.item, data.data.name, false);
            this.refreshVirtualToolTip(parentNode.item);
          }
        }
        if (parentExpanded){
          tree._expandNode(parentNode);
        }
      }
      updateClipboardSelection();
      var currentSelectedNode = dojoSession.lastSelectedNode;
      if (isSelectedNodeCurrent(currentSelectedNode, parentNode)) {
        var updateInfo = getUpdateInfo(dojoConstants.actions.create, parentNode);
        updateFramesAndPrintMessages(updateInfo, data.messages);
      } else if (isSelectedNodeCurrent(currentSelectedNode, treeNode)) {
        var updateInfo = getUpdateInfo(dojoConstants.actions.create, treeNode);
        updateFramesAndPrintMessages(updateInfo, data.messages);
      }
    } else {
      if (isNew){
        this.deleteItem(treeNode.item);
      } else {
        this.setValue(treeNode.item, "titleText", this.getLabel(treeNode.item));
      }
    }
    addDojoResponseMessages(data.messages);
  },
  
  doMoveNodeProcessResponse: function(data, nodeToDelete, item, parentNode){
    var messagesTypes = analyzeResponceMessages(data.messages);
    var tree = nodeToDelete.tree;
    if (!(messagesTypes.error)) {
      var currentSelectedNode = dojoSession.lastSelectedNode;
      var result = new Object();
      if (isSelectedNodeCurrent(currentSelectedNode, nodeToDelete) ||
          isSelectedNodeChild(currentSelectedNode, nodeToDelete)) {
        result.update = true;
      } else if (isSelectedNodeParent(currentSelectedNode, nodeToDelete) || 
                 isSelectedNodeCurrent(currentSelectedNode, parentNode)) {
        result.update = true;
        result.updateObject = currentSelectedNode;
      } 
      
      var nodeType = item.id.split(':')[0];
      item.id = createCompositeId(nodeType, data.data.id);
      var indexToPaste = data.data.treeIndex ? data.data.treeIndex : data.data.seq;
      if (isSelectedNodeCurrent(nodeToDelete.getParent(), parentNode) && indexToPaste > nodeToDelete.getIndexInParent()) {
        indexToPaste--;
      }
      item.index = indexToPaste;
      
      if (nodeToDelete.getParent().isVirtual()){
        refreshVirtualNodes(nodeToDelete.getParent().getParent());
      } else {
        this.deleteItem(nodeToDelete.item);
        this.save();
      }
      applyCancelClipboard(nodeToDelete);
            
      if (parentNode.hasVirtualChildren() || tree.groupInVirtualNodesOnAdd(parentNode)){
        refreshVirtualNodes(parentNode);
      } else if (parentNode.isVirtual()){
        refreshVirtualNodes(parentNode.getParent());
      } else {
        pInfo = {item: parentNode.item, attribute: tree.childrenAttr};
        var newItem = this.newItem(item, pInfo);
        tree._expandNode(parentNode);
      }
            
      if (result.update && !result.updateObject){
        result.updateObject = tree.getNodeById(this.getIdentity(newItem));
      }
      updateFramesAndPrintMessages(result, data.messages);
    } else {
      //delete/paste of node in case of error is made to prevent onclick event 
      //that occures in this case in IE
      item.index = nodeToDelete.getIndexInParent();
      var parentNode = nodeToDelete.getParent();
      this.deleteItem(nodeToDelete.item);
      this.save();
      pInfo = {item: parentNode.item, attribute: tree.childrenAttr};
      var newItem = this.newItem(item, pInfo);
      tree._expandNode(parentNode);
    }
    addDojoResponseMessages(data.messages);
  },
  
  doCopyToNewNode: function(treeNode){
    if (!this.currentMenuItem){
      return;
    }
    var parentNode = dijit.getEnclosingWidget(treeNode.domNode.parentNode);
    var handlerFields = this.fillHandlerFields(treeNode.tree);
    handlerFields[this.currentMenuItem.handlerIdField] = this.getNodeId(treeNode.item);
    var type = this.getNodeType(treeNode.item);
    if (type == "saoDefault"){
      handlerFields[this.currentMenuItem.handlerIdField] = null;
    }
    if (type == "sao" || type == "saoDefault"){
      handlerFields.projectId = this.activeProjectId;
    }
    var requestData = this.fillRequestData(this.currentMenuItem);
    var params = {handlerFields: handlerFields, requestData: requestData};
    var _this = this;
    var xhrArgs = {
      url: this.getJsonRequestUrl("copyChild"),
      content: {data: dojo.toJson(params) },
      handleAs: "json-comment-optional",
      sync: true,
      load: function(data) {
        // suggested data is dead, fresh data from server is used
        _this.doCopyToNewProcessResponse(data, treeNode, parentNode);
      }
    };
    dojo.xhrPost(xhrArgs);
  },
  
  doCopyToNewProcessResponse: function(data, node, parentNode) {
    var messagesTypes = analyzeResponceMessages(data.messages);
    if (node && !(messagesTypes.error)){
      var tree = node.tree;
      if (parentNode.hasVirtualChildren() || tree.groupInVirtualNodesOnAdd(parentNode)){
        refreshVirtualNodes(parentNode);
      } else if (parentNode.isVirtual()){
        refreshVirtualNodes(parentNode.getParent());
      } else {
        newItem = new Object();
        var nodeType = this.getNodeType(node.item);
        if (nodeType == "saoDefault"){
          nodeType = "sao";
        } 
        newItem.id = createCompositeId(nodeType, data.data.id);
        newItem.titleText = data.data.name;
        newItem.isFolder = this.getValue(node.item, "isFolder");
        if (this.getValue(node.item, "toolTip") && (this.getNodeType(node.item) != "queryRuleSet")){
          newItem.toolTip = this.getValue(node.item, "toolTip");
        }
        var index = data.data.treeIndex ? data.data.treeIndex : data.data.seq;
        if (index >= 0){
          newItem.index = index;
        }
        pInfo = {item: parentNode.item, attribute: tree.childrenAttr};
        this.newItem(newItem, pInfo);
        
        tree._expandNode(parentNode);
      }
      var currentSelectedNode = dojoSession.lastSelectedNode;
      if (isSelectedNodeCurrent(currentSelectedNode, parentNode)) {
        var updateInfo = getUpdateInfo(dojoConstants.actions.copy, parentNode);
        updateFramesAndPrintMessages(updateInfo, data.messages);
      }
    }
    addDojoResponseMessages(data.messages);
  },
  
  doPasteNode: function(parentNode, index){
    if (!this.currentMenuItem){
      return;
    }
    var menuItem = this.currentMenuItem;
    var nodeToPaste = dojoSession.clipboardOperation.node;
    var treeNodeType = this.getNodeType(nodeToPaste.item);
    if ((nodeToPaste && nodeToPaste.tree.id == parentNode.tree.id) &&
        !(isSelectedNodeCurrent(parentNode, nodeToPaste) || isSelectedNodeChild(parentNode, nodeToPaste))) {
      
      if (!dojoSession.clipboardOperation) {
        // maybe print some message?
      } else if ((dojoSession.clipboardOperation.description == dojoConstants.actions.copy) ||
               (dojoSession.clipboardOperation.description == dojoConstants.actions.cut)) {
        var requestData = this.fillRequestData(menuItem);
        if (dojoSession.clipboardOperation.description == dojoConstants.actions.copy){
          requestData.handlerMethod = menuItem.handleCopyPasteMethod;
        } else {
          requestData.handlerMethod = menuItem.handleCutPasteMethod;
        }
        var handlerFields = this.fillHandlerFields(parentNode.tree);
        handlerFields[menuItem.handlerIdField] = this.getNodeId(nodeToPaste.item);
        handlerFields[menuItem.handlerNameField] = this.getLabel(nodeToPaste.item);
        if (index >=0){
          handlerFields.index = index;
          if (parentNode.isVirtual() || parentNode.tree.groupInVirtualNodesOnAdd(parentNode.getParent())){
            handlerFields.index += this.getValue(parentNode.item, "startIndex");
          }
        }
        handlerFields.parentId = this.getNodeId(parentNode.item);
        
        var params = {handlerFields: handlerFields, requestData: requestData};
        var _this = this;
        newItem = new Object();
        this.fillKeyAttributes(newItem, nodeToPaste.item);
        var xhrArgs = {
          url: this.getJsonRequestUrl("copyChild"),
          content: {data: dojo.toJson(params) },
          handleAs: "json-comment-optional",
          sync: true,
          load: function(data) {
            // suggested data is dead, fresh data from server is used
            if (dojoSession.clipboardOperation.description == dojoConstants.actions.copy){
              _this.doCopyToNewProcessResponse(data, nodeToPaste, parentNode);
            } else {
              _this.doMoveNodeProcessResponse(data, nodeToPaste, newItem, parentNode);
            }
          }
        };
        dojo.xhrPost(xhrArgs);
      } 
    }
  },

  doSortChildren: function(parentNode){
    if (!this.currentMenuItem){
      return;
    }
    var menuItem = this.currentMenuItem;
    var handlerFields = this.fillHandlerFields(parentNode.tree);
    handlerFields[menuItem.handlerIdField] = this.getNodeId(parentNode.item);
    var requestData = this.fillRequestData(menuItem);
    var params = {handlerFields: handlerFields, requestData: requestData};
    var _this = this;
    var xhrArgs = {
        url: this.getJsonRequestUrl("sortChildren"),
        content: {data: dojo.toJson(params) },
        handleAs: "json-comment-optional",
        sync: true,
        load: function(data) {
          _this.doSortChildrenResponse(data, parentNode);
        }
      };
      dojo.xhrPost(xhrArgs);
  },

  doSortChildrenResponse: function(data, parentNode){
    var messagesTypes = analyzeResponceMessages(data.messages);
    if (parentNode && !(messagesTypes.error)){
      var tree = parentNode.tree;
      refreshNode(parentNode);
    }
    addDojoResponseMessages(data.messages);
  },

  getNodeType: function(item){
    return this.getNodeTypeFromIdentity(this.getIdentity(item));
  },
  
  getNodeId: function(item){
    return this.getIdFromIdentity(this.getIdentity(item));
  },
  
  refreshVirtualToolTip: function(item){
    var startIndex = this.getValue(item, "startIndex") + 1;
    var endIndex = this.getValue(item, "endIndex") + 1;
    var newToolTip = startIndex + "-" + endIndex + this.getValue(item, "toolTipLabel") + this.getLabel(item);
    this.setValue(item, "toolTip", newToolTip); 
  },
  
  refreshVirtualTitle: function(item, name, isPrefix){
    var newLabel;
    if (isPrefix){
      newLabel = name + this.getLabel(item).substring(this.getLabel(item).indexOf('-') - 1);
    } else {
      newLabel = this.getLabel(item).substring(0, this.getLabel(item).indexOf('-') + 2) + name;
    }
    this.setValue(item, "titleText", newLabel);
  },
  
  getNodeCountInVirtualNode: function(item){
    return this.getValue(item, "endIndex") - this.getValue(item, "startIndex") + 1;
  },
  
  getIdFromIdentity: function(identity){
    var array = identity.split(':');
    if (array.length >= 2){
      return array[1];
    }
    return array[0];
  },
  
  getNodeTypeFromIdentity: function(identity){
    return identity.split(':')[0];
  },
  
  getIndexItemsFromIdentity: function(identity){
    var indexItems = null;
    if (this.getNodeTypeFromIdentity(identity) == 'virtualNode') {
      indexItems = identity.split(':')[2];
    }
    return indexItems;
  },
  
  getJsonRequestUrl: function(action){
    return this._jsonFileUrl + (this._jsonFileUrl.indexOf("?")>-1 ? "&" : "?") + "action=" + action;
  },
  
  fillKeyAttributes: function(item, keywordArgs){
    item.id = this.getSingleValue(keywordArgs.id);
    item.titleText = this.getSingleValue(keywordArgs.titleText);
    item.isFolder = this.getSingleValue(keywordArgs.isFolder);
    if (keywordArgs.toolTip){
      item.toolTip = this.getSingleValue(keywordArgs.toolTip);
    }
  },
  
  getSingleValue: function(value){
    if (dojo.isArray(value)){
      return value[0];
    } else {
      return value;
    }
  },
  
  fillRequestData: function(info){
    var requestData = new Object();
    requestData.formHandler = info.formHandler;
    requestData.handlerMethod = info.handleMethod;
    return requestData;
  },
  
  fillHandlerFields: function(info){
    var handlerFields = {successURL: info.successUrl, errorURL: info.errorUrl};
    return handlerFields;
  }

});
