dojo.provide("atg.searchadmin.tree.BrowseLazyStore");

dojo.require("dojo.data.ItemFileWriteStore");

dojo.declare("atg.searchadmin.tree.BrowseLazyStore", dojo.data.ItemFileWriteStore, {
  label: "",
  identifier: "",

  constructor: function(/* Object */ params) {
    this._labelAttr = params.label;
    this._features['dojo.data.api.Identity'] = params.identifier;
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
      var params = {expandUrl: keywordArgs.expandUrl, treeNodeType: treeNodeType, node: node};
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

  getJsonRequestUrl: function(action){
    return this._jsonFileUrl + (this._jsonFileUrl.indexOf("?")>-1 ? "&" : "?") + "action=" + action;
  },

  getIdFromIdentity: function(identity){
    var array = identity.split(':');
    if (array.length >= 2){
      var index = identity.indexOf(':');
      return identity.substring(index + 1);
    }
    return array[0];
  },

  getNodeType: function(item){
    return "";
  },

  getNodeTypeFromIdentity: function(identity){
    return identity.split(':')[0];
  }
});