dojo.provide("atg.searchadmin.tree.DojoTreeDnDSource");

dojo.require("dijit._tree.dndSource");

dojo.declare("atg.searchadmin.tree.DojoTreeDnDSource", dijit._tree.dndSource, {
  //onMouseUp works instead of onDndDrop
  
  checkItemAcceptance: function(node, source){
    var tree = dijit.getEnclosingWidget(node).tree;
    if (this.checkActionDisabled(node, "addChild")){
      var acceptNode = dijit.getEnclosingWidget(node);
      var type = tree.store.getNodeType(acceptNode.item);
      for (var sel in this.selection){
        var selectedNode = dijit.getEnclosingWidget(this.selection[sel]);
        if (isSelectedNodeCurrent(acceptNode, selectedNode) || isSelectedNodeParent(acceptNode, selectedNode)){
          break;
        }
        var selectType = tree.store.getNodeType(selectedNode.item);
        var permittedTypes = nodeInfo[type].permitedTypesToPaste;
        if (permittedTypes) {
          for (var i=0; i<permittedTypes.length; i++){
            if (selectType == permittedTypes[i]){
              tree.selectNode(acceptNode);
              if (acceptNode.isExpandable && !acceptNode.isExpanded){
                acceptNode.tree._expandNode(acceptNode);
              }
              return true;
            }
          }
        }
      } 
    }
    tree.removeSelection();
    return false;
  },
  
  checkActionDisabled: function(node, action){
    if (dijit.getEnclosingWidget(node)){
      var tree = dijit.getEnclosingWidget(node).tree;
      var type = tree.store.getNodeType(dijit.getEnclosingWidget(node).item);
      var disabled = nodeInfo[type].actionsDisabled;
      for (var i=0; i<disabled.length; i++){
        if (disabled[i] == action){
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  },
  
  getSelectedNodes: function(){
    var selections = this.selection;
    for (var i in selections){
      if (!this.checkActionDisabled(selections[i], "move")){
        selections = null;
      }
    }
    return selections;
  },
  
  itemCreator: function(nodes){
    var items = []
    var item, newItem;
    for(var i=0;i<nodes.length;i++){
      var treeNode = this.tree.getNodeById(nodes[i].id);
      if (treeNode){
        item = treeNode.item;
        newItem = new Object();
        this.tree.store.fillKeyAttributes(newItem, item);
        items.push(newItem);
      }
    }
    return items;
  },
  
  onDndDrop: function(source, nodes, copy){
    // summary: topic event processor for /dnd/drop, called to finish the DnD operation
    // source: Object: the source which provides items
    // nodes: Array: the list of transferred items
    // copy: Boolean: copy items, if true, move items otherwise
    
    if(this.containerState == "Over"){
      this.isDragging = false;
      var target = this.current;
      var items = this.itemCreator(nodes, target);
      if(target && target != this.tree.domNode){
        for(var i = 0; i < items.length; i++){
          var tree = this.tree;
          var store = tree.store;
          var node = tree.getNodeById(nodes[i].id);
          var parentNode = dijit.getEnclosingWidget(target);
          if(store._jsonFileUrl && !(isSelectedNodeCurrent(node, parentNode) || isSelectedNodeChild(parentNode, node))) {
            if (parentNode.isVirtual()){
              var indexInVirtual = store.getValue(parentNode.item, "startIndex");
              parentNode = parentNode.getParent();
            }
            var parentId = store.getNodeId(parentNode.item);
            var handlerFields = store.fillHandlerFields(this.tree);
            handlerFields.parentId = parentId;
            handlerFields.index = 0;
            if (indexInVirtual){
              handlerFields.index = indexInVirtual;
            }
            
            var sourceItem = dijit.getEnclosingWidget(source.anchor).item;
            var sourceNodeType = store.getNodeType(sourceItem);
            var sourceMoveIdField = nodeInfo[sourceNodeType].moveIdField;
            var sourceMoveNameField = nodeInfo[sourceNodeType].moveNameField;
            var sourceMoveHandleMethod = nodeInfo[sourceNodeType].moveHandleMethod;
    
            handlerFields[sourceMoveIdField ? sourceMoveIdField : tree.moveIdField] = store.getIdFromIdentity(items[i].id);
            handlerFields[sourceMoveNameField ? sourceMoveNameField : tree.moveNameField] = items[i].titleText;
            var requestData = new Object();
            requestData.formHandler = tree.moveFormHandler;
            requestData.handlerMethod = sourceMoveHandleMethod ? sourceMoveHandleMethod : tree.moveHandleMethod;
            var params = {handlerFields: handlerFields, requestData: requestData};
            var xhrArgs = {
              url: store.getJsonRequestUrl("moveChild"),
              content: {data: dojo.toJson(params) },
              handleAs: "json-comment-optional",
              sync: true,
              load: function(data) {
                // suggested data is dead, fresh data from server is used
                store.doMoveNodeProcessResponse(data, node, items[i], parentNode);
              }
            };
      
            dojo.xhrPost(xhrArgs);
          } 
        }
      }
    }
    this.onDndCancel();
  }
    
});

//fixing bug SEARCH-168121 (removing counter <td> for moving node avatar)
dojo.require("dojo.dnd.Avatar");
dojo.extend(dojo.dnd.Avatar, {
  construct: function(){
    // summary: a constructor function;
    //  it is separate so it can be (dynamically) overwritten in case of need
    var a = dojo.doc.createElement("table");
    a.className = "dojoDndAvatar";
    a.style.position = "absolute";
    a.style.zIndex = 1999;
    a.style.margin = "0px"; // to avoid dojo.marginBox() problems with table's margins
    var b = dojo.doc.createElement("tbody");
    var k = Math.min(5, this.manager.nodes.length);
    var source = this.manager.source;
    for(var i = 0; i < k; ++i){
      tr = dojo.doc.createElement("tr");
      tr.className = "dojoDndAvatarItem";
      td = dojo.doc.createElement("td");
      var node = source.creator ?
        // create an avatar representation of the node
        node = source._normalizedCreator(source.getItem(this.manager.nodes[i].id).data, "avatar").node :
        // or just clone the node and hope it works
        node = this.manager.nodes[i].cloneNode(true);
        source.tree.removeSelection(node);
      node.id = "";
      td.appendChild(node);
      tr.appendChild(td);
      dojo.style(tr, "opacity", (9 - i) / 10);
      b.appendChild(tr);
    }
    a.appendChild(b);
    this.node = a;
  },
  
  update: function(){
    // summary: updates the avatar to reflect the current DnD state
    dojo[(this.manager.canDropFlag ? "add" : "remove") + "Class"](this.node, "dojoDndAvatarCanDrop");
  }
});

dojo.require("dojo.dnd.Manager");
dojo.extend(dojo.dnd.Manager, {
  //pre-dnd tree node selection is saved here to restore the selection after dnd
  selectedNode: null,
  
  startDrag: function(source, nodes, copy){
    // summary: called to initiate the DnD operation
    // source: Object: the source which provides items
    // nodes: Array: the list of transferred items
    // copy: Boolean: copy items, if true, move items otherwise
    this.source = source;
    this.nodes  = nodes;
    this.copy   = Boolean(copy); // normalizing to true boolean
    this.avatar = this.makeAvatar();
    dojo.body().appendChild(this.avatar.node);
    dojo.publish("/dnd/start", [source, nodes, this.copy]);
    this.events = [
      dojo.connect(dojo.doc, "onmousemove", this, "onMouseMove"),
      dojo.connect(dojo.doc, "onmouseup",   this, "onMouseUp"),
      dojo.connect(dojo.doc, "onkeydown",   this, "onKeyDown"),
      dojo.connect(dojo.doc, "onkeyup",     this, "onKeyUp")
    ];
    var c = "dojoDnd" + (copy ? "Copy" : "Move");
    dojo.addClass(dojo.body(), c);
    
    var tree = source.tree;
    if (tree.selectedNode) { 
      this.selectedNode = tree.selectedNode;
      tree.removeSelection();
    }
  },
  
  stopDrag: function(){
    // summary: stop the DnD in progress
    dojo.removeClass(dojo.body(), "dojoDndCopy");
    dojo.removeClass(dojo.body(), "dojoDndMove");
    dojo.forEach(this.events, dojo.disconnect);
    this.events = [];
    this.avatar.destroy();
    this.avatar = null;
    this.source = null;
    this.nodes = [];
    
    if (this.selectedNode) {
      var node = dijit.getEnclosingWidget(this.selectedNode);
      if (node) {
        node.tree.selectNode(node);
      }
      this.selectedNode = null;
    }
  }
});
