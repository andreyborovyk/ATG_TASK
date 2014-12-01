dojo.provide("atg.searchadmin.tree.BrowseDojoTree");
dojo.require("dijit.Tree");

dojo.declare("atg.searchadmin.tree.BrowseDojoTree", dijit.Tree, {
  expandUrl: "",
  selectedNode: null,

  mayHaveChildren: function(/*dojo.data.Item*/ item){
    return true;
  },

  getItemChildren: function(/*dojo.data.Item*/ parentItem, /*function(items)*/ onComplete){
    if (parentItem){
      var widgetId = this.store.getIdentity(parentItem);
    } else {
      var widgetId = "rootNode";
    }
    this.store.fetch({expandUrl: this.expandUrl, widgetId: widgetId, onComplete: onComplete});
  },
  
  selectNode: function(node){
    if (this.selectedNode){
      dojo.removeClass(this.selectedNode, "selected");
    }
    var spans = node.domNode.childNodes;
    for (var i = 0; i < spans.length; i++){
      var span = spans[i];
      if (span.className == "dijitTreeContent"){
        span = span.getElementsByTagName("span")[0];
        dojo.addClass(span, "selected");
        this.selectedNode = span;
      }
    }
  },

  onClick: function(/* dojo.data */ item, /*TreeNode*/ node){
    if (!this.isEditing){
      this.selectNode(node);
    }
  },

  getNodeById: function(id) {
    return this._itemNodeMap[id];
  },

  /** AGA: Overriding to pre-open child nodes */
  _expandNode: function(/*_TreeNode*/ node){
    //atg.searchadmin.tree.BrowseDojoTree.superclass._expandNode.call(this, node);

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
        var onComplete = function(childItems) {
          node.unmarkProcessing();
          // AGA: if this node is auto expanded and it doesn't have children, we'll expand it to remove [+] button.
          if (node.autoExpand) {
            node.autoExpand = false;
            if (childItems.length > 0) {
              node.state = "UNCHECKED";
              return;
            }
          }
          _this._onLoadAllItems(node, childItems);
          // AGA: the following loop will work for non-auto-expanded node only. So we will try to expand their children.
          dojo.forEach(childItems, function(child) {
            var childId = _this.store.getIdentity(child);
            var childNode = _this.getNodeById(childId);
            setTimeout(function() {
              if (childNode.state == "UNCHECKED") {
                childNode.autoExpand = childId != "root";
                _this._expandNode(childNode);
              }
            }, 100);
          });
        };
        this.getItemChildren(node.item, onComplete);
        break;

      default:
        // data is already loaded; just proceed
        if (node.expand) {  // top level Tree doesn't have expand() method
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
