/*
	CheckAll Widget v0.5
	
	- Shift click to select/deselect a range of items

	CheckAll Widget v0.4
	
	- For use with Tables, Lists or Tree where CheckAll and unCheckAll functionality is needed.

	Created by Sykes on 2006-08-10.
	Copyright (c) 2006 Media~Hive Inc.. All rights reserved.

***************************************************************************/
dojo.provide("atg.widget.CheckAll");

dojo.widget.defineWidget("atg.widget.checkAll", dojo.widget.DomWidget, {
    
	widgetType: "checkAll",  	
  isContainer: false,
  debugName: 'checkAll',
  parentTag: 'UL',
  noSelectAll: false,
  titleCheckAll: "Select All",
  titleUnCheckAll: "Deselect All",
	excludeCheckClass: "noCheckAll",
  	
  initialize: function(){

  	dojo.event.connect(this.domNode, "onclick", this, "masterCheck");
          	    
  	this.parentBlock = dojo.dom.getAncestorsByTag(this.domNode, this.parentTag, true);

  	this.relatedInputs = this.parentBlock.getElementsByTagName('input');

    var i = this.relatedInputs.length;
		var somethingUnchecked = false;
		
    do{
    	if (this.relatedInputs[i-1].type =='checkbox' && this.relatedInputs[i-1].id != this.domNode.id  && !dojo.html.hasClass(this.relatedInputs[i-1], this.excludeCheckClass)){
      	
				// it's a checkall checkbox, add the event and add it's location as well.
				dojo.event.connect(this.relatedInputs[i-1], "onclick", this, "servantCheck");
				
				this.relatedInputs[i-1].orderPos = i;

				if(somethingUnchecked == false){

	    		if (this.relatedInputs[i-1].checked != true){
			  		this.domNode.checked = false;
			    	this.domNode.title = this.titleCheckAll;
						somethingUnchecked = true;
		      }else{
						this.domNode.checked = true;
				    this.domNode.title = this.titleUnCheckAll;
					}
				}
      }
    }while (--i);
	},
  	
    
	servantCheck: function(evt){

		var trigger = dojo.html.getEventTarget(evt);
		
		//alert(trigger.orderPos);
		
		
	  if(!trigger.checked){
	  	this.domNode.checked = false;
	    this.domNode.title = this.titleCheckAll;
	  }else{
			var i = this.relatedInputs.length;		
	    	do{
	    		if (this.relatedInputs[i-1].type =='checkbox' && this.relatedInputs[i-1].checked != true && !dojo.html.hasClass(this.relatedInputs[i-1], this.excludeCheckClass)){
			    	this.domNode.checked = false;
			      this.domNode.title = this.titleCheckAll;
						break;
	        }else{
			    	this.domNode.checked = true;
			      this.domNode.title = this.titleUnCheckAll;
					}
				}while (--i);	    
			}  
    },
  	
  	
  	masterCheck: function(){

			if(this.domNode.checked){
				this.onSelectAll();	           	    
			}else{
				this.onDeselectAll();	           	    
	    }        
  	},

	onSelectRange: function(a,b){
		
		
		
	},
	
	onSelectAll: function(){
		this.domNode.title = this.titleUnCheckAll;
    var i = this.relatedInputs.length;
    
		do{
    	if (this.relatedInputs[i-1].type =='checkbox' && this.relatedInputs[i-1].id != this.domNode.id && !dojo.html.hasClass(this.relatedInputs[i-1], this.excludeCheckClass)){
      	this.relatedInputs[i-1].checked = true;
       }
     }while (--i);
	},

	onDeselectAll: function(){
		this.domNode.title = this.titleCheckAll;
		var i = this.relatedInputs.length;

    do{
    	if (this.relatedInputs[i-1].type =='checkbox' && this.relatedInputs[i-1].id != this.domNode.id && !dojo.html.hasClass(this.relatedInputs[i-1], this.excludeCheckClass)){
      	this.relatedInputs[i-1].checked = false;
    	}
    }while (--i);
	}
});