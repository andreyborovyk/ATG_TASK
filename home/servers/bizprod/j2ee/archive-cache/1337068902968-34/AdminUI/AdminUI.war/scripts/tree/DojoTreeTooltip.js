dojo.provide("atg.searchadmin.tree.DojoTreeTooltip");

dojo.require("dijit.Tooltip");

dojo.declare("atg.searchadmin.tree.DojoTreeTooltip", dijit.Tooltip, {
  open: function(/*DomNode*/ target){
    var node = dijit.getEnclosingWidget(target);
    var store = node.tree.store;
    // summary: display the tooltip; usually not called directly.
    if (node){
      target = node.labelNode;
      if (this._connectNode == target) return;
      //target = target || this._connectNodes[0];
      //if(!target){ return; }

      if(this._showTimer){
        clearTimeout(this._showTimer);
        delete this._showTimer;
      }
      var thisNodeInfo = nodeInfo[store.getNodeType(node.item)];
      var tooltipUrl = thisNodeInfo == null ? null : thisNodeInfo.tooltipUrl;
      var _this = this;
      
      var handleLoad = function(data) {
        if (data.tooltip && data.tooltip.length > 0 && data.tooltip != node.label){
          dijit.showTooltip(data.tooltip, target);
        }
        _this._connectNode = target;
      };
         
      if (tooltipUrl) {
        var xhrArgs = {
          url: tooltipUrl + store.getNodeId(node.item),
          handleAs: "json-comment-optional",
          sync: true
        };
        var doFetch = dojo.xhrPost(xhrArgs);
        doFetch.addCallback(handleLoad);
      } else {
        var tooltipText = store.getValue(node.item, "toolTip");
        handleLoad({tooltip: tooltipText});
      }
      
    }
  }
});
