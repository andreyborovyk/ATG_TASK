// 
// quietFormSubmitter.js
// 
// This javascript object enables quiet form submissions.  In other words,
// the form submits without reloading the page and the response comes back 
// for display in the status area. 
//
// The atg.assetmanager.quietformsubmitter is initialized during page load.
// During the initialization process, we look for all DOM elements whose class 
// is "quietformsubmit" and turns these into AJAX form submits.
//
// When the form is submitted and the response or error returned, the 
// functions handleResponse or handleError are invoked.

dojo.provide("atg.assetmanager.quietFormSubmitter");
atg.assetmanager.quietFormSubmitter = 
  {
    hijackClassName: "quietformsubmitter",

    initialize: function() {
      this.hijackAllFormSubmitNodes();
    },

    hijackAllFormSubmitNodes: function() {
      dojo.query("." + atg.assetmanager.quietFormSubmitter.hijackClassName).forEach(function(node){
        atg.assetmanager.quietFormSubmitter.hijackNode(node);
      });
    },

    hijackNode: function(node) {
      if (node.isHijacked){
        return;
      }
      node.isHijacked=true;

      // Create object with common params for io.bind call
      var _this = this;
      var bindParams={
        headers: { "Accept" : "application/json" },
        handleAs: "json",
        load: function(data, ioArgs) {
          _this.handleResponse(data, ioArgs);
        },
        error: function(evt, ioArgs) {
          _this.handleError(evt);
        },
        timeout: function(evt, ioArgs) {
          _this.handleError(evt);
        }
      };
  
      if (node.nodeName=="INPUT"){
        dojo.connect(node, "onclick", function(evt){
          evt.preventDefault();

          // Get parent form node for this input node
          var formNode=dojo.html.getParentByType(node,"FORM");
      
          // Create content object with the name/value pair of the submit button that's been clicked
          // dojo.io.bind doesn't send the value of submit buttons when serializing the form as it 
          // doesn't know which one has been clicked. Server side FormHandlers need this data to
          // invoke the correct formHandler method.
          var content={};
          content[node.name]=node.value;

          // Add the form node and the submit button name/value to the io.bind params
          dojo.mixin(bindParams,{
            form: formNode,
            content: content
          });
      
          dojo.xhrPost(bindParams);
        });
      }
      else if (node.nodeName=="A"){        
        dojo.connect(node, "onclick", function(evt){
          evt.preventDefault();

          // Ensure it's not a double click
          if (node.currentlyAdding && node.currentlyAdding===true){
            return;
          }

          // Add the URL of the clicked link to the io.bind params
          dojo.mixin(bindParams,{
            url: node.href
          });
     
          dojo.xhrPost(bindParams);
        });
      }
    },
    
    handleResponse: function(data, ioArgs){
      if (data.publishToTopic) {
        dojo.publish(data.publishToTopic, [data]);
      }
    },
    
    handleError: function(evt){
      messages.addError(evt.message);
    }
};
