/*
---------------------------------------------------------------------
Embedded Assistance (EA) Base Widget
Used to contain functionality common to all EA widgets
---------------------------------------------------------------------
*/

dojo.provide("atg.widget.assistance.Base");

dojo.declare("atg.widget.assistance.Base", [dijit._Widget], {

	id: "assistanceBase",
	isContainer: false,
	helpData: {}, // help data passed as an object
	helpFields: [], // help fields passed as an array
	iconGIF: "/WebUI/dijit/assistance/images/icon_helpText_off.gif", // gif icon location for IE6 
	iconPNG: "/WebUI/dijit/assistance/images/icon_helpText_off.png", // png icon for everyone else

	postCreate: function(){
		this.domNode.id = this.widgetId;
		this.helpFields = atg.helpFields;
		this.helpData = atg.helpContent;
		this.buildEA();
		
	},

	uninitialize: function(){

	},

	positionHelp: function(target){
		var fromPos = dojo.coords(target, false);
		//console.debug("EA | " + fromPos);
		fromPos.width = target.offsetWidth;
		fromPos.height = target.offsetHeight;
		return fromPos;
	},

	buildEA: function(){
		this.popupIcon = (dojo.isIE > 0 && dojo.isIE < 7)? this.iconGIF : this.iconPNG;

		dojo.forEach(this.helpFields,
			function(helpField){
				
				//console.debug("EA | " + helpField);
				
				switch(helpField.type){
					
					case 'popup':
					
						if(dojo.byId("trigger_"+helpField.id)){
							//console.debug("EA | Trigger with ID: 'trigger_"+helpField.id+" already exists, skip it");
							break;
						}

						if(!dojo.byId(helpField.id)){
							//console.debug("EA | Trigger with ID: 'trigger_"+helpField.id+" cannot be found");
							break;
						}
						
						//console.debug("EA | Popup being created");
						
						// create the HTML for the popup trigger
						this.createPopupTrigger(helpField);
						// add the events and actions to the trigger
						this.addPopupTriggerEvents(helpField);
					
						// TODO : Jon Sykes, Tue Nov 28 10:22:19 2006
						// need to add some checks for select options so that we 
						// don't add the trigger inside the select form element

						// can this node have a child node, if so add into it, if not append it after
						if(dojo.byId(helpField.id).firstChild){
							dojo.byId(helpField.id).appendChild( this.helpTrigger );
						}else{
							dojo.place(this.helpTrigger, dojo.byId(helpField.id), "after");
						}


						// kill the click event, use Widget.connect for auto-cleanup at destroy time
						this.connect(this.helpTrigger, "onclick", "_squelchEvent");

					break;

					case 'inline':
					
						helpObject = helpField;

						
						if(!dojo.byId(helpField.id)){
							//console.debug("EA | Inline Trigger with ID: "+helpField.id+", doesn't exist, skip it");
							break;
						}

						var targetElement = dojo.byId(helpField.id);

						if(!dijit.byId("EA_"+helpObject.id)){

							//console.debug('EA | this.helpData: ', this.helpData);
							
							dojo.forEach(this.helpData.helpItem,
							    function(oneItem){

											if(oneItem.id == helpObject.helpId){
												//console.debug('EA | oneItem.content',oneItem.content);
												EAItem = new atg.widget.assistance.Inline({id: "EA_"+helpObject.id, content: oneItem } );
											}

							    });


							/* TODO: , 02/23/07, 17:15:42, 
							The logic below is NOT fool proof, you can in theory add help into places that will make no sense
							additional logic is needed to cover more of these weird cases, but if adding EA is a trained effort
							we can probably avoid these with information rather than tests and logic.
							*/ 
							// Logic to look at what the surrounding HTML is where the inline is being loaded
							// Some elements we should add the item after like in heading h1 tags
							// Some we need to add it inside the tag as after would not be semantic, like list items

							// inside seems to be the lesser case, so we'll check for them
							var insideTags = ['LI', 'DT', 'DD', 'TD', 'TH', 'DIV']

							if(dojo.indexOf(insideTags, targetElement.tagName) != -1){

								targetElement.appendChild(EAItem.domNode);

							}else{

								dojo.place(EAItem.domNode, targetElement, "after");

							}

							// Should we handle error state - TR's for example 

						}else{
							//console.debug("EA | inline Trigger with ID: 'trigger_"+helpField.id+" already exists, skip it");
						}
					break;					
					
				}
				
			}, this);
	},
	
	_squelchEvent: function(evt) { 
    // squelch all the propagation, bubbles and default event actions
    dojo.stopEvent(evt);
    if (evt.preventDefault){ evt.preventDefault(); }      
  },
  
	createPopupTrigger: function(obj){
		
		//console.debug("EA | Popup being created");
		var helpTrigger = document.createElement("IMG");
		helpTrigger.src = this.popupIcon;
		helpTrigger.id = "trigger_"+obj.id;
		helpTrigger.className = "ATG_EA_helpTrigger";

		this.helpTrigger = helpTrigger.cloneNode(true);

		this.helpTrigger.helpObj = obj;
		
	},
	
	addPopupTriggerEvents: function(obj){
		
		// use Widget.connect to get auto cleanup of connected events on widget destroy
		this.connect(this.helpTrigger, "onmouseover", "_handlePopupTrigger");

		this.connect(this.helpTrigger, "onmouseout", "_mouseOut");
		
	},
	
	_handlePopupTrigger: function(evt) {

    var helpObject = evt.target.helpObj;

    var fromPos = this.positionHelp(evt.target);

    // check that the widget doesn't already exist
    if(!dijit.byId("EA_"+helpObject.id)){

      // TODO: Jon: Could Use dojo.filter here for value matching
      dojo.forEach(dijit.byId('assistanceBase').helpData.helpItem,
        function(oneItem){
          if(oneItem.id == helpObject.helpId){ 
            thisItem = oneItem;
          }
        });

      EAItem = new atg.widget.assistance.Popup({
        id: "EA_"+helpObject.id, 
        content: thisItem.content,
        position: fromPos,
        target: evt.target}
      );        
      
      dojo.body().appendChild(EAItem.domNode); 
    }

    dijit.byId("EA_"+helpObject.id).target = evt.target;

    dijit.byId("EA_"+helpObject.id).show(fromPos);
    
    // squelch all the propogation, bubbles and default event actions
    if (evt.preventDefault){ evt.preventDefault(); }
    dojo.stopEvent(evt);

  },
  
	_mouseOut: function(evt){

    /* TODO: , 02/26/07, 17:18:30, 
    Confirm that this Safari fix needs to stay here, if it isn't supported this should probably be pulled.
    */ 
    // safari fix seems to fire event one level lower in the dom than everything else, so just bump it up
    if(evt.target.nodeType == 3){
      var helpObject = evt.target.parentNode.helpObj;
    }else{
      var helpObject = evt.target.helpObj;
    }
    if(dijit.byId("EA_"+helpObject.id)){
      dijit.byId("EA_"+helpObject.id).hide();
    }
    // squelch all the propogation, bubbles and default event actions
    if (evt.preventDefault){ evt.preventDefault(); }
    dojo.stopEvent(evt);

  },

	// prevents trailing comma, all edits above this line please //
	sanitySaver:''
});
