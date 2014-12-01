dojo.provide("atg.searchadmin.tree.DojoTree");

dojo.require("dijit.Tree");

dojo.extend(dijit._TreeNode, {
  postCreate: function(){
    // set label, escaping special characters
    this.setLabelNode(this.label);

    // set expand icon for leaf   
    this._setExpando();

    // set icon and label class based on item
    this._updateItemClasses(this.item);
    if (this.item){
      var nodeIdentity = this.tree.store.getIdentity(this.item);
      if(this.isExpandable){
        dijit.setWaiState(this.labelNode, "expanded", this.isExpanded);
        for (var i = 0; i < hierarchy.length; i++){
          var identity = createCompositeId(hierarchy[i].treeNodeType, hierarchy[i].id);
          if (identity == nodeIdentity){
            this.tree._expandNode(this);
          }
        }
      }
      if (hierarchy.length > 0 && identity == nodeIdentity){
        this.tree.selectNode(this);
        dojoSession.lastSelectedNode = copyAllNodeProperties(this);
      }
    } else {
      this._expandNode(this);
    }    
  },
  
  _addChildren: function(/* object[] */ childrenArray){
    // summary:
    //    adds the children to this node.
    //    Takes array of objects like: {label: ...}  (_TreeNode options basically)

    //    See parameters of _TreeNode for details.
    var nodeMap = {};
    if(childrenArray && childrenArray.length > 0){
      dojo.forEach(childrenArray, function(childParams){
        var child = new dijit._TreeNode(
          dojo.mixin({
            tree: this.tree,
            label: this.tree.getLabel(childParams.item)
          }, childParams)
        );
        if (child.item.index){
          this.addChild(child, this.tree.store.getValue(child.item, "index"));
        } else {
          this.addChild(child);
        }
        nodeMap[this.tree.store.getIdentity(childParams.item)] = child;
      }, this);

      dojo.forEach(this.getChildren(), function(child, idx){
        child._updateLayout();
      });
    }

    return nodeMap;
  },
  
  getIndexInParent: function(){ 
    var p = this.getParent();
    if(!p){return -1;} 
    return p.getChildren().indexOf(this); 
  },
  
  isVirtual: function(){
    return (this.tree.store.getNodeType(this.item) == "virtualNode");
  },
  
  hasVirtualChildren: function(){
    var children = this.getChildren();
    for (var i = 0; i < children.length; i++){
      if (children[i].isVirtual()){
        return true;
      }
    }
    return false;
  }
});

dojo.declare("atg.searchadmin.tree.DojoTree", dijit.Tree, {

  expandUrl: "",
  successUrl: "",
  errorUrl: "",
  moveFormHandler: "",
  moveHandleMethod: "",
  moveIdField: "",
  moveNameField: "",
  menuId: "",
  maxRange: null,
    
  selectedNode: null,
  renameDiv: "",
  isEditing: false,
    
  mayHaveChildren: function(/*dojo.data.Item*/ item){
    return this.store.getValue(item, "isFolder");
  },

  getItemChildren: function(/*dojo.data.Item*/ parentItem, /*function(items)*/ onComplete){
    if (parentItem){
      var widgetId = this.store.getIdentity(parentItem);
    } else {
      var widgetId = "rootNode";
    }
    this.store.fetch({expandUrl: this.expandUrl, widgetId: widgetId, onComplete: onComplete});
  },

  onClick: function(/* dojo.data */ item, /*TreeNode*/ node){
    if (top.uiConfig.checkForm() && !this.isEditing){
      this.selectNode(node);
      this.linkToUrl(item);
    }
  },
  
  groupInVirtualNodesOnAdd: function(parentNode){
    return this.maxRange && parentNode.getChildren().length >= 2*this.maxRange;
  },
  
  linkToUrl: function(item){
    var nodeType = this.store.getNodeType(item);
    var nodeUrl = nodeInfo[nodeType].nodeUrl;
    if (nodeUrl.indexOf("=") == nodeUrl.length - 1){
      nodeUrl += this.store.getNodeId(item);
    }
    loadRightPanel(nodeUrl);
  },
 
  getIconClass: function(/*dojo.data.Item*/ item){
    if (item){
      return this.store.getNodeType(item) + "Icon";
    }
  },
  
  getNodeById: function(id) {
    return this._itemNodeMap[id];
  },
  
  _onKeyPress: function(/*Event*/ e){
    // summary: translates keypress events into commands for the controller
    if(e.altKey){ return; }
    var treeNode = dijit.getEnclosingWidget(e.target);
    if(!treeNode){ return; }

    // Note: On IE e.keyCode is not 0 for printables so check e.charCode.
    // In dojo charCode is universally 0 for non-printables.
    if(e.charCode){  // handle printables (letter navigation)
      // Check for key navigation.
      var navKey = e.charCode;
      if(!e.altKey && !e.ctrlKey && !e.shiftKey && !e.metaKey && !this.isEditing){
        navKey = (String.fromCharCode(navKey)).toLowerCase();
        this._onLetterKeyNav( { node: treeNode, key: navKey } );
        dojo.stopEvent(e);
      }
    }else if (!this.isEditing){  // handle non-printables (arrow keys)
      var map = this._keyHandlerMap;
      if(!map){
        // setup table mapping keys to events
        map = {};
        map[dojo.keys.ENTER]="_onEnterKey";
        map[dojo.keys.LEFT_ARROW]="_onLeftArrow";
        map[dojo.keys.RIGHT_ARROW]="_onRightArrow";
        map[dojo.keys.UP_ARROW]="_onUpArrow";
        map[dojo.keys.DOWN_ARROW]="_onDownArrow";
        map[dojo.keys.HOME]="_onHomeKey";
        map[dojo.keys.END]="_onEndKey";
        this._keyHandlerMap = map;
      }
      if(this._keyHandlerMap[e.keyCode]){
        this[this._keyHandlerMap[e.keyCode]]( { node: treeNode, item: treeNode.item } );  
        dojo.stopEvent(e);
      }
    }
  },
  
  selectNode: function(pNode){
    this.removeSelection();
    var spans = pNode.domNode.childNodes;
    for (var i = 0; i < spans.length; i++){
      var span = spans[i];
      if (span.className == "dijitTreeContent"){
        span = span.getElementsByTagName("span")[0];
        dojo.addClass(span, "selected");
        this.selectedNode = span;
      }
    }
  },
  
  removeSelection: function(pNode){
    var node = (pNode) ? pNode : this.selectedNode;
    if (node){
      dojo.removeClass(node, "selected");
    }
  },
  
  _expandNode: function(/*_TreeNode*/ node){
    updateClipboardSelection();
    if(!node.isExpandable){
      return;
    }

    var store = this.store;
    var getValue = this.store.getValue;

    switch(node.state){
      case "LOADING":
        // ignore clicks while we are in the process of loading data
        return;

      case "UNCHECKED":
        // need to load all the children, and then expand
        node.markProcessing();
        var _this = this;
        var onComplete = function(childItems){
          node.unmarkProcessing();
          _this._onLoadAllItems(node, childItems);
        };
        this.getItemChildren(node.item, onComplete);
        break;

      default:
        // data is already loaded; just proceed
        if(node.expand){  // top level Tree doesn't have expand() method
          node.expand();
          if(this.persist && node.item){
            this._openedItemIds[this.store.getIdentity(node.item)] = true;
            this._saveState();
          }
        }
        break;
    }
  }
  
});