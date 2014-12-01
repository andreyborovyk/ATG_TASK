/*
	Created by Sykes on 2006-08-10.
	Copyright (c) 2006 Media~Hive Inc.. All rights reserved.

***************************************************************************/
dojo.provide("atg.widget.checkAll.checkAll");

dojo.declare("atg.widget.checkAll", dijit._Widget, {
    
	widgetType: "checkAll",  	
  isContainer: false,
  debugName: 'checkAll',
  parentTag: 'UL',
  noSelectAll: false,
  titleCheckAll: "Select All",
  titleUnCheckAll: "Deselect All",
  excludeCheckClass: "noCheckAll",
  	
  postCreate: function(){

    console.debug("atg.widget.checkAll: ", "Post Create");
    dojo.connect(this.domNode, "onclick", this, "masterCheck");
    
    // TODO: needs to be looked at this current logic seems to rigid, we should target a 
    this.parentBlock = dojo.html.getParentByType(this.domNode, this.parentTag);

    this.relatedInputs = this.parentBlock.getElementsByTagName('input');

    var i = this.relatedInputs.length;
    var somethingUnchecked = false;
		
    do{
      if (this.relatedInputs[i-1].type =='checkbox' && this.relatedInputs[i-1].id != this.domNode.id  && !dojo.hasClass(this.relatedInputs[i-1], this.excludeCheckClass)){
      	
        // it's a checkall checkbox, add the event and add it's location as well.
        dojo.connect(this.relatedInputs[i-1], "onclick", this, "servantCheck");
				
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

    //var trigger = dojo.html.getEventTarget(evt);
    var trigger = evt.target;
    //alert(trigger.orderPos);
		

    if(!trigger.checked){
      this.domNode.checked = false;
      this.domNode.title = this.titleCheckAll;
    }else{
      var i = this.relatedInputs.length;		
      do{
        if (this.relatedInputs[i-1].type =='checkbox' && this.relatedInputs[i-1].checked != true && !dojo.hasClass(this.relatedInputs[i-1], this.excludeCheckClass)){
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
      if (this.relatedInputs[i-1].type =='checkbox' && this.relatedInputs[i-1].id != this.domNode.id && !dojo.hasClass(this.relatedInputs[i-1], this.excludeCheckClass)){
      	this.relatedInputs[i-1].checked = true;
       }
     }while (--i);
     dojo.publish("/atg/widget/checkAll",[true, this.widgetId ]);
   },

   onDeselectAll: function(){
     this.domNode.title = this.titleCheckAll;
     var i = this.relatedInputs.length;

    do{
      if (this.relatedInputs[i-1].type =='checkbox' && this.relatedInputs[i-1].id != this.domNode.id && !dojo.hasClass(this.relatedInputs[i-1], this.excludeCheckClass)){
        this.relatedInputs[i-1].checked = false;
      }
    }while (--i);

    dojo.publish("/atg/widget/checkAll",[false, this.widgetId ]);
  }
});