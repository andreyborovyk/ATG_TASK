dojo.provide("atg.widget.tree.LazyLoadStore");

dojo.require("dojo.data.ItemFileReadStore");

dojo.declare("atg.widget.tree.LazyLoadStore", dojo.data.ItemFileReadStore, {

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
      var xhrArgs = {
        url: this._jsonFileUrl,
        content: {parentItemId: keywordArgs.parentItemId},
        handleAs: "json-comment-optional",
        sync: true
      };

      //this callback function parses received JSON
      //it converts all items into format used by ItemFileReadStore object and stores them in '_arrayOfAllItems' property.
      var handleLoad = function(data) {
        var items = data.items;
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
  }

});