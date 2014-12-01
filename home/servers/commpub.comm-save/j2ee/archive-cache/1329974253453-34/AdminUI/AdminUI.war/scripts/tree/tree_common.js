//Need for DnD (and other) operations to store some data.
if (!dojoSession) {
  var dojoSession = new Object();
  dojoSession.clipboardOperation = new Object();
  var copiedItemsIntoColsedParent = null;
}
if (!nodeInfo) {
  var nodeInfo = new Object();
}
//current opened node hierarchy. Filled by right pane. Need for sync between rightPane and dojo tree.
if (!hierarchy) {
  var hierarchy = new Array();
}
//constants
if (!dojoConstants) {
  var dojoConstants = new Object();
  dojoConstants.attributes = new Object();
  dojoConstants.attributes.input = "input";
  dojoConstants.attributes.contextMenuClassPrefix = "context_menu_";
  dojoConstants.actions = new Object();
  dojoConstants.actions.paste = "paste";
  dojoConstants.actions.move = "move";
  dojoConstants.actions.cut = "cut";
  dojoConstants.actions.copy = "copy";
  dojoConstants.actions.update = "update";
  dojoConstants.actions.create = "create";
  dojoConstants.actions._delete = "delete";
  dojoConstants.actions.sort = "sort";
  dojoConstants.clazz = new Object();
  dojoConstants.clazz.nodeCopy = "nodeCopy";
  dojoConstants.clazz.nodeCut = "nodeCut";
}

var currentTree = null;

//NEW DOJO FUNCTIONS

//creates widgetId from type and item id.
function createCompositeId(type, id) {
  return (type) ? type + ":" + id : id;
}

function createVirtualCompositeId(id, index){
  index++;
  return "virtualNode:" + id + ":" + index + ":" + index; 
}

function createVirtualTitle(startName, endName){
  return startName + " - " + endName;
}

function createVirtualTooltip(startIndex, endIndex, tooltipLabel, itemLabel){
  startIndex++;
  endIndex++;
  return startIndex + " - " + endIndex + tooltipLabel + itemLabel;
}

//return node clone.
function copyAllNodeProperties(node) {
  var result = new Object();
  for (var i in node) {
    result[i] = node[i];
  }
  return result;
}

//Adds dojo response messages to alerting.
function addDojoResponseMessages(messages) {
  alerting.showMessages(messages);
}

//Prepare object with given messages types inside.
function analyzeResponceMessages(messages) {
  var result = new Object();
  if (messages) {
    for (var i = 0; i < messages.length; i++) {
      result[messages[i].type] = true;
    }
  }
  return result;
}

//iterate over hierarchy (reqursion) from top node to current.
//Expand nodes step by step. Last node of hierarchy will be selected instead of expand.
function syncTree(node) {
  if (currentTree){
    for (var i=0; i < hierarchy.length; i++){
      var identity = createCompositeId(hierarchy[i].treeNodeType, hierarchy[i].id);
      var node = currentTree.getNodeById(identity);
      if (node){ 
        currentTree._expandNode(node);
      }
    }
    if (node){
      node.tree.selectNode(node);
      dojoSession.lastSelectedNode = copyAllNodeProperties(node);
    }
  }
}

//apply tree structure changes after right pane operation (delete, import, create , update etc.)
function applyTreeStructure(messages) {
  var needSync = false;
  for (var i = 0; i < messages.length; i++) {
    if (alerting.isConfirmMessage(messages[i]) && currentTree) {
      var store = currentTree.store;
      switch (messages[i].operation) {
        case dojoConstants.actions._delete:
          node = currentTree.getNodeById(createCompositeId(messages[i].treeNodeType, messages[i].itemId));
          var parentNode = node.getParent();
          if (parentNode.isVirtual()){
            refreshVirtualNodes(parentNode.getParent());
          }
          applyCancelClipboard(node);
          store.deleteItem(node.item);
          break;
        case dojoConstants.actions.create:
          if (!currentTree.getNodeById(createCompositeId(messages[i].treeNodeType, messages[i].itemId))){
            applyNewNode(messages[i]);
            needSync = true;
          }
          break;
        case dojoConstants.actions.update:
          node = currentTree.getNodeById(createCompositeId(messages[i].treeNodeType, messages[i].itemId));
          var parentNode = node.getParent();
          if (parentNode.isVirtual()){
            var parentExpanded = parentNode.isExpanded; 
            var childNodes = parentNode.getChildren();
            var childIndex = childNodes.indexOf(node);
            if (childIndex == 0){
              store.refreshVirtualTitle(parentNode.item, messages[i].itemName, true);
              store.refreshVirtualToolTip(parentNode.item);
            }
            if (childIndex == childNodes.length - 1){
              store.refreshVirtualTitle(parentNode.item, messages[i].itemName, false);
              store.refreshVirtualToolTip(parentNode.item);
            }
          }
          node = currentTree.getNodeById(createCompositeId(messages[i].treeNodeType, messages[i].itemId));
          var parentItem = node.getParent().item;
          var isFolder = store.getValue(node.item, "isFolder");
          if (messages[i].index == -1){
            messages[i].index = node.getIndexInParent();
          }
          var isExpanded = node.isExpanded;
          store.deleteItem(node.item);
          store.save();
          applyNewNode(messages[i], parentItem, isFolder, isExpanded);
          if (parentExpanded){
            currentTree._expandNode(parentNode);
          }
          needSync = true;
          break;
      }
    }
  }
  if (needSync){
    syncTree();
  }
}

function applyNewNode(message, parent, isFolder, isExpanded){
  var parentItem = parent;
  var parentNode = null;
  var isFirstLevelNode = false;
  var store = currentTree.store;
  var newIndex = null;
  if (!parentItem){
    if (message.parentId) {
      parentNode = currentTree.getNodeById(createCompositeId(message.parentNodeType, 
            message.parentId));
      var parentItem = parentNode.item;
    } else {
      parentNode = currentTree.getNodeById(nodeInfo[message.treeNodeType].rootParentNodeId);
      parentItem = parentNode.item;
      isFirstLevelNode = (parentNode.getIndexInParent() == -1);
    }
  }
  if (!parentItem){
    return;
  }
  if (isFirstLevelNode && parentNode.getChildren().length == 0){
    newItem = new Object();
    newItem.id = store.getIdentity(parentItem);
    newItem.titleText = store.getLabel(parentItem);
    newItem.isFolder = true;
    newItem.toolTip = store.getValue(parentItem, "toolTip");
    newItem.index = currentTree.getChildren().indexOf(parentNode);
    store.deleteItem(parentItem);
    store.save();
    store.newItem(newItem);
    return;
  } else if (parentNode && parentNode.hasVirtualChildren()){
    var virtChildren = parentNode.getChildren();
    var lastVirtNode = virtChildren[virtChildren.length - 1];
    var virtNodeCount = store.getNodeCountInVirtualNode(lastVirtNode.item);
    if (virtNodeCount < currentTree.maxRange){
      parentItem = lastVirtNode.item;
      store.refreshVirtualTitle(parentItem, message.itemName, false);
      store.setValue(parentItem, "endIndex", store.getValue(parentItem, "endIndex") + 1);
      store.refreshVirtualToolTip(parentItem);
      newIndex = virtNodeCount;
    } else {
      newItem = new Object();
      var newStartIndex = store.getValue(lastVirtNode.item, "endIndex") + 1;
      newItem.id = createVirtualCompositeId(message.itemId, newStartIndex);
      newItem.titleText = createVirtualTitle(message.itemName, message.itemName);
      newItem.isFolder = true;
      newItem.toolTip = createVirtualTooltip(newStartIndex, newStartIndex, 
            store.getValue(lastVirtNode.item, "toolTipLabel"), newItem.titleText);
      newItem.index = virtChildren.length;
      var pInfo = {item: parentItem, attribute: currentTree.childrenAttr};
      store.newItem(newItem, pInfo);
      parentItem = currentTree.getNodeById(newItem.id).item;
    }
  } else if (parentNode && parentNode.tree.groupInVirtualNodesOnAdd(parentNode)){
    refreshVirtualNodes(parentNode);
    return;
  }
  newItem = new Object();
  newItem.id = createCompositeId(message.treeNodeType, message.itemId);
  newItem.titleText = message.itemName;
  if (isFolder){
    newItem.isFolder = isFolder;
  } else {
    newItem.isFolder = message.isFolder;
  }
  if (message.itemDescription){
    newItem.toolTip = message.itemDescription;
  }
  if (message.index >=0){
    newItem.index = message.index;
  }
  if (newIndex){
    newItem.index = newIndex;
  }
  var pInfo = {item: parentItem, attribute: currentTree.childrenAttr};
  store.newItem(newItem, pInfo);
  if (isExpanded){
    currentTree._expandNode(currentTree.getNodeById(newItem.id));
  }
}

function refreshVirtualNodes(dictNode){
  if (!currentTree){
    currentTree = dictNode.tree;
  }
  if (dictNode.hasVirtualChildren()){
    var expandedNodes = new Array();
    var virtNodes = dictNode.getChildren();
    for (var i = 0; i < virtNodes.length; i++){
      if (virtNodes[i].isExpanded){
        expandedNodes.push(i);
      }
    }
  }
  var store = currentTree.store;
  var dictItem = dictNode.item;
  newItem = new Object();
  newItem.id = store.getIdentity(dictItem);
  newItem.titleText = store.getLabel(dictItem);
  newItem.isFolder = true;
  if (store.getValue(dictItem, "toolTip")){
    newItem.toolTip = store.getValue(dictItem, "toolTip");
  }
  newItem.index = dictNode.getIndexInParent();
  var pInfo = {item: dictNode.getParent().item, attribute: currentTree.childrenAttr};
  store.deleteItem(dictItem);
  store.save();
  store.newItem(newItem, pInfo);
  var newDictNode = currentTree.getNodeById(newItem.id);
  currentTree._expandNode(newDictNode);
  if (newDictNode.hasVirtualChildren() && expandedNodes){
    var virtNodes = newDictNode.getChildren();
    for (var i = 0; i < expandedNodes.length; i++){
      var virtNode = virtNodes[expandedNodes[i]];
      if (virtNode){
        currentTree._expandNode(virtNode);
      }
    } 
  }
}

function refreshNode(pNode) {
  if (!currentTree){
    currentTree = pNode.tree;
  }
  var store = currentTree.store;
  var item = pNode.item;
  newItem = new Object();
  newItem.id = store.getIdentity(item);
  newItem.titleText = store.getLabel(item);
  newItem.isFolder = true;
  if (store.getValue(item, "toolTip")){
    newItem.toolTip = store.getValue(item, "toolTip");
  }
  newItem.index = pNode.getIndexInParent();
  var info = {item: pNode.getParent().item, attribute: currentTree.childrenAttr};
  var parentItemId = info.item.id;
  store.deleteItem(item);
  store.save();
  store.newItem(newItem, info);
  currentTree._expandNode(currentTree.getNodeById(newItem.id));
  currentTree._expandNode(currentTree.getNodeById(parentItemId));
}

//Show input field in coordinates where current node situated, places focus to it.
function renameNode(pNode, operation) {
  var mRenameDiv = document.getElementById(pNode.tree.renameDiv);
  dojoSession.renameOperation = new Object();
  dojoSession.renameOperation.nodeToRename = pNode;
  dojoSession.renameOperation.operation = operation;
  dojoSession.renameOperation.oldTitle = pNode.tree.store.getLabel(pNode.item);
  pNode.editingNode = true;
  pNode.labelNode.innerHTML = mRenameDiv.innerHTML;
  var renameInput = getFirstInputElement(pNode.labelNode);
  if (renameInput) {
    renameInput.focus();
    renameInput.select();
    renameInput.value = dojoSession.renameOperation.oldTitle;
  }
  pNode.tree.isEditing = true;
}
//Return first input tag element in parent.
function getFirstInputElement(parent) {
  var res = parent.getElementsByTagName(dojoConstants.attributes.input);
  return res[0];
}
//Enter/Esc key on Rename
function checkForRenameSubmit(obj, e) {
  if (e.keyCode == 13) {
    //disable onblur of input
    obj.onblur = null;
    renameNodeSubmit();
  } else if (e.keyCode == 27) {
    cancelRenameNode();
  }
}
//When user submits new name of node. Calls controller.
function renameNodeSubmit() {
  var node = dojoSession.renameOperation.nodeToRename;
  var store = node.tree.store;
  var renameInput = getFirstInputElement(node.labelNode);
  if (renameInput) {
    //node.tree.controller.doRenameNode(dojoSession.renameOperation.nodeToRename, dojoSession.renameOperation.operation);
    if (store.getValue(node.item, "removeNodeOnCancel")) {
      store.doRenameNode(node, renameInput.value, store.getNodeId(node.getParent().item));
    } else {
      store.doRenameNode(node, renameInput.value);
    }
  }
  store.save();
  node.tree.isEditing = false;
}
//When user cancel rename of node.
function cancelRenameNode() {
  var node = dojoSession.renameOperation.nodeToRename;
  var store = node.tree.store;
  if (store.getValue(node.item, "removeNodeOnCancel")) {
    store.deleteItem(node.item);
    if (node.domNode){
      var parentNode = node.getParent();
      node.tree.deleteNode(node);
      parentNode._setChildren([]);
    }
  } else {
    store.setValue(node.item, "titleText", dojoSession.renameOperation.oldTitle);
  }
  store.save();
  node.tree.isEditing = false;
}

//Creating new node and calls rename functionallity for it.
function createNewNode(node, index, label, nodeType) {
  var store = node.tree.store; 
  var newItem = new Object();
  newItem.id = createCompositeId(nodeType, "temporary");
  newItem.isFolder = false;
  newItem.titleText = label; 
  newItem.removeNodeOnCancel = true;
  if (index >= 0){
    newItem.index = index;
  }
  pInfo = {item: node.item, attribute: node.tree.childrenAttr};
  var newItem = store.newItem(newItem, pInfo);
  var nodeToRename = node.tree.getNodeById(newItem.id);
  if (!node.isExpanded){
    //in case of leaf _TreeNode should be created manually
    var nodeMap = node._setChildren([{item:newItem}]);
    node.tree._expandNode(node);
    nodeToRename = nodeMap[newItem.id];
  }
  var func = function () {
     renameNode(nodeToRename, dojoConstants.actions.create);
  };
  window.setTimeout(func, 500);
}

function doClipboardOperation(node, operation){
  deSelectClipboardNode(dojoSession.clipboardOperation.node);
  dojoSession.clipboardOperation.description = operation;
  dojoSession.clipboardOperation.node = node;
  selectClipboardNode(node, operation);
  enablePasteMenuItems(node);
}
      
function doCancelSelection(node){
  deSelectClipboardNode(dojoSession.clipboardOperation.node);
  applyCancelClipboard();
}

function applyCancelClipboard(node) {
  if (node && dojoSession.clipboardOperation.node && !isSelectedNodeCurrent(dojoSession.clipboardOperation.node, node)){
    return;
  }
  disablePasteMenuItems(dojoSession.clipboardOperation.node);
  dojoSession.clipboardOperation.node = null;
}

function updateClipboardSelection(){
  selectClipboardNode(dojoSession.clipboardOperation.node, 
      dojoSession.clipboardOperation.description);
}

//select tree node and children after "cut" or "paste" operation
function selectClipboardNode(node, operation) {
  if (node){
    switch (operation) {
      case dojoConstants.actions.copy:
        dojo.addClass(node.labelNode, dojoConstants.clazz.nodeCopy);
        break;
      case dojoConstants.actions.cut:
        dojo.addClass(node.labelNode, dojoConstants.clazz.nodeCut);
        break;
    }
    var children = node.getChildren();
    for (var i = 0; i < children.length; i++) {
      selectClipboardNode(children[i], operation);
    }
  }
}
//deselect tree node and children after "cut" or "paste" operation
function deSelectClipboardNode(node) {
  if (node) {
    for (var i in dojoConstants.clazz) {
      dojo.removeClass(node.labelNode, i);
    }
    var children = node.getChildren();
    for (var i = 0; i < children.length; i++) {
      deSelectClipboardNode(children[i]);
    }
  }
}

//disable "paste" menu items
function disablePasteMenuItems(node) {
  if (node) {
    var treeNodeType = node.tree.store.getNodeType(node.item);
    var items = nodeInfo[treeNodeType].disabledMenuIds;
    disablePasteMenu(items);
  }
}
//disable "paste" menu items
function disablePasteMenu(items) {
  for (var i = 0; i < items.length; i++) {
    dijit.byId(items[i]).setDisabled(true);
  }
}
//enable "paste" menu items
function enablePasteMenuItems(node) {
  var treeNodeType = node.tree.store.getNodeType(node.item);
  var items = nodeInfo[treeNodeType].disabledMenuIds;
  for (var i = 0; i < items.length; i++) {
    var menuItem = dijit.byId(items[i]);
    menuItem.setDisabled(false);
  }
}

function isSelectedNodeCurrent(currentNode, node){
  var store = currentNode.tree.store; 
  return store.getNodeId(currentNode.item) == store.getNodeId(node.item);  
}

//true if given node is among children
function isSelectedNodeParent(selectedNode, nodeToFind) {
  var parentNodeToFind = nodeToFind.getParent();
  return isSelectedNodeCurrent(selectedNode, parentNodeToFind); 
}

//true if given node is among parents.
function isSelectedNodeChild(selectedNode, nodeToFind) {
  var result = false;
  var selectedNode = selectedNode.getParent();
  while (selectedNode && (!selectedNode.isTree)) {
    if (isSelectedNodeCurrent(selectedNode, nodeToFind)) {
      result = true;
      break;
    }
    selectedNode = selectedNode.getParent();
  }
  return result;
}

//true if right pane need to update.
function getUpdateInfo(operation, node) {
  var result = new Object();
  var currentSelectedNode = dojoSession.lastSelectedNode;
    
  if (operation == dojoConstants.actions.create ||
      operation == dojoConstants.actions.copy ||
      operation == dojoConstants.actions.sort) {
    result.update = true;
    result.updateObject = node;
  } 
  return result;
}

//updates right frame if necessary. Call updateRightPane method if exist or make common reload.
//newNode param used only
function updateFrames(updateInfo) {
  if (updateInfo.update) {
    var nodeToUpdate = updateInfo.updateObject;
    nodeToUpdate.tree.linkToUrl(nodeToUpdate.item);
    //store node data
    dojoSession.lastSelectedNode = copyAllNodeProperties(nodeToUpdate);
  }
}

var customLoad;

function updateFramesAndPrintMessages(updateInfo, messages){
  customLoad = function(){
    addDojoResponseMessages(messages);
  };
  updateFrames(updateInfo);
}

function createSaoSetsNode(isFolder, projectId) {
  if (!currentTree){
    currentTree = dijit.byId("term_dict_dojo_Tree");
  }
  if (!currentTree){
    return;
  }
  var oldNode = currentTree.getNodeById("saoSetsNode");
  if (oldNode == null) {
    return;
  }
  
  if (isFolder){
    currentTree.store.activeProjectId = projectId;
  } else {
    currentTree.store.activeProjectId = null;
  } 
  currentTree.store.setValue(oldNode.item, "isFolder", isFolder);
  if (!isFolder){
    dojo.query("." + dojoConstants.attributes.contextMenuClassPrefix + "saoSetsNode").forEach(function(i){
      dijit.getEnclosingWidget(i).setDisabled(true); 
    });
    oldNode._setChildren(null);
  }else{
    dojo.query("." + dojoConstants.attributes.contextMenuClassPrefix + "saoSetsNode").forEach(function(i){
      dijit.getEnclosingWidget(i).setDisabled(false); 
    });
    
    var node = {widgetId: currentTree.store.getIdentity(oldNode)};
    var params = {expandUrl: currentTree.expandUrl, treeNodeType: currentTree.store.getNodeType(oldNode.item),  
          node: node};
    var xhrArgs = {
      url: currentTree.store.getJsonRequestUrl("getChildren"),
      content: {data: dojo.toJson(params)},
      handleAs: "json-comment-optional",
      sync: true
    };
  
    var handleLoad = function(data) {
      var items = data.children;//items;
      
      var onComplete = function(childItems){
        oldNode.unmarkProcessing();
        currentTree._onLoadAllItems(oldNode, childItems);
      };
      
      currentTree.store.fetch({expandUrl: currentTree.expandUrl, treeNodeType: currentTree.store.getNodeType(oldNode.item),
      widgetId: currentTree.store.getIdentity(oldNode.item), onComplete: onComplete});
    };
  
    var doFetch = dojo.xhrPost(xhrArgs);
    doFetch.addCallback(handleLoad);
  }
}

function deleteIndexedFacetSetNode() {
  if (!currentTree){
    currentTree = dijit.byId("facet_set_dojo_Tree");
  }
  if (!currentTree){
    return;
  }
  var indexedFacetsNode = currentTree.getNodeById('rootIndexedFacetSetNode');
  var deletedTitle = null;
  if (indexedFacetsNode){
    deletedTitle = indexedFacetsNode.item.titleText;
    currentTree.store.deleteItem(indexedFacetsNode.item);
    currentTree.store.save();
  }
  return deletedTitle;
}

function createIndexedFacetSetNode(isFolder, divLabel, recreate) {
  if (!currentTree){
    currentTree = dijit.byId("facet_set_dojo_Tree");
  }
  if (!currentTree || currentTree.id != "facet_set_dojo_Tree"){
    return;
  }
  var oldTitle = deleteIndexedFacetSetNode();
  newItem = new Object();
  newItem.id = "rootIndexedFacetSetNode";
  newItem.titleText = recreate ? oldTitle : divLabel;
  if (!newItem.titleText) {
    return;
  }
  newItem.isFolder = isFolder;
  newItem.toolTip = divLabel;
  newItem.index = 0;
  currentTree.store.newItem(newItem);
}

function refreshTreeArea(deleteOnly) {
  var projContentPane = dijit.byId("activeProjectPane");
  if(projContentPane) {
    projContentPane.refresh();
  }
  if (!deleteOnly) {
    var facetLabel = document.getElementById('facetsLabel');
    if (facetLabel != null){
      createIndexedFacetSetNode(true, facetLabel.innerHTML);
    } else {
      createIndexedFacetSetNode(true, null, true);
    }
    var projectSelect = document.getElementById('selectedProjectIdSelect');
    if (projectSelect != null) {
      createSaoSetsNode(true, projectSelect.value);
    }
  } else {
    deleteIndexedFacetSetNode();
    createSaoSetsNode(false);
  }
}
