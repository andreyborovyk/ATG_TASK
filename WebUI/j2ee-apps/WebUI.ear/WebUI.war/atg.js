/*
    ATG Parent Object JavaScript File

------------------------------------------------------------------------------*/

if (typeof atg == "undefined")
  atg = {};

/*
 * ATG Visual Gesture Features
 * 
 * ------------------------------------------------------------------------------
 */

dojo.mixin(atg, {
  noop : function() {}, // a utility function that deliberately does nothing

  // Sparkle is the pulsar function that indicates that an element on a page
  // has changed or updated
  // Useage:
  // atg.sparkle(domNodeReference);
  //
  sparkle : function(node) {
    var off = {
      node :node, // always required
      duration :200, // always required
      properties :
      { // core of _Animation
        opacity :
        {
          start :'1',
          end :'0'
        }
      }
    };
    var on = {
      node :node, // always required
      duration :100, // always required
      properties :
      { // core of _Animation
        opacity :
        {
          start :'0',
          end :'1'
        }
      }
    };
    dojo.fx.chain(
      [ dojo.animateProperty(off), dojo.animateProperty(on),
        dojo.animateProperty(off), dojo.animateProperty(on),
        dojo.animateProperty(off), dojo.animateProperty(on) 
      ]).play();
  },

  truncateMid : function(str, abbr) {
    var abbr = (abbr == false) ? false : true;
    var len = str.length;
    if (len > 35) {
      var str1 = str.substr(0, 20);
      var str2 = str.substr(len - 15);

      if (abbr != true) {
        return (str1 + "..." + str2);
      }
      else {
        return ("<abbr title='" + str + "' style='cursor:help'>" + str1 + "..." + str2 + "</abbr>");
      }
    }
    else {
      return str;
    }
  },


	// InnerHTML setting with widget leak prevention
	setInnerHTML : function(node, content){
		dojo.query('[widgetId]', node).forEach(function(n) {
			dijit.byNode(n).destroy();
		});
		node.innerHTML = content;
		dojo.parser.parse(node);
	}

});

/* 
 	XHR multi-node update handler utility method, uses atg.setInnerHTML for leak prevention.
	the function is expecting JSON back from the server in the format....

{updates:[{
  "id":"updateNodeId",
  "html":'<em>Foo Bar</em>'
  },
  {
    "id":"anotherUpdateNodeId",
    "html":'<input type="checkbox"><label>Enabled</label>'
    }
  ]
}

------------------------------------------------------------------------------*/

dojo.mixin(dojo._contentHandlers, {
  "node-update-server": function(xhr){
		console.debug('node-update-server');
	
    var json = dojo._contentHandlers.json(xhr);
		console.debug(json.updates);
    
		dojo.forEach(json.updates, function(update){
			console.debug(update);	
      var node = dojo.byId(update.id);
      if(node){
        atg.setInnerHTML(node, update.html);
      }
    });
  }
});


// form node manager for IE which can't delete old form nodes and thus causes a pseudo memory leak
dojo.mixin(atg, {

	formManager: {
	  formNodes:[],
	  createForm: function(){
			console.debug("atg.formManager.createForm - formNodes.length: ", this.formNodes.length);
	
	    return (this.formNodes.length>0)? this.formNodes.pop() : document.createElement('FORM');
	
	  },
	  recycleForm: function(formNode){
	    
	    formNode.removeNode(true);   
			this.formNodes.push(formNode);

			console.debug("atg.formManager.recycleForm - formNodes.length: ", this.formNodes.length);		
	       
	  },
		
		debugFormLength: function(){
			console.debug("atg.formManager.formNodes.length: ", this.formNodes.length);
		}
	}	
});
