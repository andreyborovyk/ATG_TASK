dojo.provide("atg.widget.tree.MultiSelectTree");

dojo.require("dijit.Tree");

dojo.extend(dijit._TreeNode, {

  //determines whether node is selected
  isSelected: false,

  postCreate: function() {
    //THIS IS THE ORIGINAL CODE FROM TREE.JS
    this.setLabelNode(this.label);
    this._setExpando();
    this._updateItemClasses(this.item);
    if(this.isExpandable){
      dijit.setWaiState(this.labelNode, "expanded", this.isExpanded);
    }

    //THIS CODE WAS ADDED
    //if node is an instance of dijit._TreeNode, not dijit.Tree(atg.widget.tree.MultiSelectTree)
    //then add select icon to the right of the node, mark node as selected(if need),
    //add tooltip value(if need)
    if (!this.isTree) {
      this.createSelectIcon();
      if (this.tree.persist && this.tree.selectedNodeIds[this.id]) {
        this.selectNode(true);
      }
      if (this.tree.enableTooltips) {
        this.labelNode.title = this.item.tooltip;
      }
      dojo.publish(this.tree.id + "PostCreateNode", [this]);
    }

  },

  /**
   * creates <img> element with unique ID and place it after node
   */
  createSelectIcon: function() {
    var icon = document.createElement("img");
    icon.id = "selectIcon_" + this.tree.id + "_" + this.id;
    icon.src = "/WebUI/images/icons/icon_select.gif";
    //image is not visible by default
    icon.style.visibility = "hidden";
    icon.className = "selectIcon";
    dojo.place(icon, this.labelNode, "after");
    //assign the reference to the created image to the 'selectIcon' property of the node
    this.selectIcon = icon;
  },

  /**
   * show/hides select icon
   */
  selectNode: function(isSelected) {
    this.selectIcon.style.visibility = isSelected ? "visible" : "hidden";
    this.isSelected = isSelected;
  }

});

dojo.declare("atg.widget.tree.MultiSelectTree", dijit.Tree, {

  enableTooltips: false,
  multiSelection: true,

  postCreate: function(){
    this.cookieNameSelect = this.id + "SelectedNodes";
    //initialize properties for storing info about tree state
    this.selectedNodeIds = {};
    this.openedNodeIds = {};
    //if tree is persistent then restore all previously selected nodes
    if (this.persist) {
      var cookie = dojo.cookie(this.cookieNameSelect);
      if (cookie) {
        dojo.forEach(cookie.split(','), function(nodeId) {
          this.selectedNodeIds[nodeId] = true;
        }, this);
      }
    }
    //call postCreate() function of the super class dijit.Tree
    //that function restore previously expanded nodes in addition
    this.inherited(arguments);
    dojo.publish(this.id + "PostCreateTree", [this]);
  },

  mayHaveChildren: function(/*dojo.data.Item*/ item){
    return this.store.getValue(item, "isExpandable");
  },

  getItemChildren: function(/*dojo.data.Item*/ parentItem, /*function(items)*/ onComplete){
    var parentItemId = parentItem != null ? parentItem.id : null;
    var onError = function(err) {
      console.error(err);
    };
    this.store.fetch({parentItemId: parentItemId, onComplete: onComplete, onError: onError});
  },

  /**
   * this function is overridden to avoid bug with moving focus to node label (exception is thrown under IE only)
   * and to guarantee further processing if only expand icon or node label was clicked on
   */
  _onClick: function(/*Event*/ e){
    //THIS IS THE ORIGINAL CODE FROM TREE.JS
    var domElement = e.target;
    var nodeWidget = dijit.getEnclosingWidget(domElement);
    if (!nodeWidget || !nodeWidget.isTreeNode) {
      return;
    }

    //BELOW IS THE FIXED CODE
    if (domElement == nodeWidget.expandoNode && nodeWidget.isExpandable) {
      this._onExpandoClick({node:nodeWidget});
    }
    if (domElement == nodeWidget.labelNode) {
      this.onClick(nodeWidget.item, nodeWidget);
      this.focusNode(nodeWidget);
    }

    dojo.stopEvent(e);
  },

  /**
   * performs multiple/single node selection
   */
  onClick: function(/* dojo.data */ item, /*TreeNode*/ node){
    if (node.isTree) return;

    //if node is already selected it should be deselected inspite of selection mode
    if (node.isSelected) {
      node.selectNode(false);
      if (!this.multiSelection || this.persist) {
        delete this.selectedNodeIds[node.id];
      }
    //if node isn't selected
    } else {
      //for single selection mode delete previous selection at first
      if (!this.multiSelection) {
        for (var id in this.selectedNodeIds) {
          var nodeId = id;
          delete this.selectedNodeIds[id];
        }
        if (nodeId) {
          var selectedNode = dijit.byId(nodeId);
          selectedNode.selectNode(false);
        }
      }
      //select node
      node.selectNode(true);
      if (!this.multiSelection || this.persist) {
        this.selectedNodeIds[node.id] = true;
      }
    }

    //in case of persistent tree save info about selected nodes to cookies
    if (this.persist) {
      var ids = this.getSelectedNodeIds();
      dojo.cookie(this.cookieNameSelect, ids.join());
    }

    dojo.publish(this.id + "PostSelectNode", [node]);
  },


  /**
   * Overrides the same function of the super class
   */
  _collapseNode: function(/*_TreeNode*/ node) {
    //call _collapseNode(node) function of the super class dijit.Tree
    this.inherited(arguments);
    //delete node id from the map of expanded node ids
    delete this.openedNodeIds[node.id];
    dojo.publish(this.id + "PostCollapseNode", [node]);
  },

  /**
   * Overrides the same function of the super class
   */
  _expandNode: function(/*_TreeNode*/ node){
    //THIS IS THE ORIGINAL CODE FROM TREE.JS

    // summary: called when the user has requested to expand the node

    // clicking the expando node might have erased focus from the current item; restore it
    var t = node.tree;
    if(t.lastFocused){ t.focusNode(t.lastFocused); }

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
        if(node.expand){	// top level Tree doesn't have expand() method
          node.expand();
          if(this.persist && node.item){
            this._openedItemIds[this.store.getIdentity(node.item)] = true;
            this._saveState();
          }
          //THIS IS THE CODE ADDED TO HANDLE EXPANDED NODE IDS
          if (!node.isTree) {
            this.openedNodeIds[node.id] = true;
            dojo.publish(this.id + "PostExpandNode", [node]);
          }
        }
        break;
    }
  },


  getOpenedNodeIds: function() {
    return this.getNodeIds(true);
  },

  getSelectedNodeIds: function() {
    return this.getNodeIds(false);
  },

  /**
   * Returns array of the expanded/selected node ids getting it from the related map
   */
  getNodeIds: function(isOpenedNodeIds) {
    var ids = [];
    var map = isOpenedNodeIds ? this.openedNodeIds : this.selectedNodeIds;
    for (var id in map){
      ids.push(id);
    }
    return ids;
  }

});