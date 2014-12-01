/*
-----------------------------------------------------------------------------------
######## DEPRECATED - USE THE VERSION IN WEBUI/DIJIT/ASSISTANCE INSTEAD ###########
-----------------------------------------------------------------------------------
*/

/*
	EA Widget v0.1
	
	- For showing various types of inline and hover context help.

***************************************************************************/
dojo.provide("atg.widget.assistance.base");


dojo.widget.defineWidget("atg.widget.assistanceBase", dojo.widget.DomWidget, {
    
	widgetType: "toggleView", 
	widgetId: "assistanceBase", 	
  isContainer: false,
	helpData: {}, // help data passed as an object
	helpFields: [], // help fields passed as an array
	templateCssPath: dojo.uri.dojoUri("/WebUI/widget/assistance/templates/base.css"), // popup css template
	iconGIF: "/WebUI/widget/assistance/images/icon_helpText_off.gif", // gif icon location for IE6 
	iconPNG: "/WebUI/widget/assistance/images/icon_helpText_off.png", // png icon for everyone else

  	
  initialize: function(){				
		console.debug("EA | " + this.widgetType+'['+this.widgetId+'].initialize()');
	
		helpFields = atg.helpFields;
		this.goToServer();
	},

	uninitialize: function(){

	},

	positionHelp: function(target){

		fromPos = dojo.html.getAbsolutePosition(target, false);
		fromPos.width = target.offsetWidth;
		fromPos.height = target.offsetHeight;
		return fromPos;

	},


  goToServer: function() {
    var _this = this;
    dojo.io.bind({
     	form: "POST",
			url: "help/helpContent.js",
      mimetype: "text/json", // we're expecting JSON to be returned
			useCache: true,

      load: function(type, data, evt) {

        //console.debug(data); //helpContent
				_this.handleIO(data);
      },

      error: function(t, e) {
        console.debug("EA | Widget XHR Error: "+e.message);
				if(dojo.widget.byId('messageBar'))dojo.widget.byId('messageBar').addMessage({
					type:"warning",
				  summary:e.message}
				);


      },

      timeout: function(type) { 
      	console.debug("EA | Widget XHR Server Timeout");
				dojo.widget.byId('messageBar').addMessage({
					type:"warning",
				  summary:'Server Timeout: '+type}
				);

  		}
    });
  },

	handleIO: function(obj){
		this.helpData = obj;
		//console.debug(this.helpData);
		this.buildEA();

	},

	buildEA: function(){
		
		this.popupIcon = (dojo.render.html.ie60)? this.iconGIF : this.iconPNG;
		

		var j = i = helpFields.length;

		console.debug("EA | helpFields.length: "+helpFields.length);


		do{
			console.debug("EA | helpFields[j-i].type: "+helpFields[j-i].type);
			switch(helpFields[j-i].type){

				
				case 'popup':
				
				if(dojo.byId("trigger_"+helpFields[j-i].id)){
					//console.debug("EA | Trigger with ID: 'trigger_"+helpFields[j-i].id+" already exists, skip it");
					break;
				}
				
				if(!dojo.byId(helpFields[j-i].id)){
					//console.debug("EA | Trigger with ID: 'trigger_"+helpFields[j-i].id+" not found.");
					break;
				}
				
				console.debug("EA | Popup being created");

					var helpTrigger = document.createElement("IMG");
					helpTrigger.src = this.popupIcon;
					helpTrigger.id = "trigger_"+helpFields[j-i].id;
					helpTrigger.className = "ATG_EA_helpTrigger";

					var thisHelpTrigger = helpTrigger.cloneNode(true);

					thisHelpTrigger.helpObj = helpFields[j-i];

					var _this = this;

					dojo.event.connect(thisHelpTrigger, "onmouseover", function(evt) { 	

									
						var helpObject = evt.target.helpObj;

						// check that the widget doesn't already exist
						if(!dojo.widget.byId("EA_"+helpObject.id)){

							var fromPos = _this.positionHelp(evt.target);


							EAItem = dojo.widget.createWidget("assistancePopup", {
																									widgetId: "EA_"+helpObject.id, 
																									content: dojo.widget.byId('assistanceBase').helpData.helpItem[helpObject.helpId-1].content,
																									position: fromPos}
																									);
							document.body.appendChild(EAItem.domNode); 
						}else{
							var fromPos = _this.positionHelp(evt.target);
							dojo.widget.byId("EA_"+helpObject.id).show(fromPos);
						}
						// squelch all the propogation, bubbles and default event actions
						if (evt.preventDefault){ evt.preventDefault(); }
						dojo.event.browser.stopEvent(evt);

					});

					dojo.event.connect(thisHelpTrigger, "onmouseout", function(evt){

						/* TODO: , 02/26/07, 17:18:30, 
						Confirm that this Safari fix needs to stay here, if it isn't supported this should probably be pulled.
						*/ 
						// safari fix seems to fire event one level lower in the dom than everything else, so just bump it up
						if(evt.target.nodeType == 3){
							var helpObject = evt.target.parentNode.helpObj;
						}else{
							var helpObject = evt.target.helpObj;
						}
						if(dojo.widget.byId("EA_"+helpObject.id)){
							dojo.widget.byId("EA_"+helpObject.id).hide();
						}
						// squelch all the propogation, bubbles and default event actions
						if (evt.preventDefault){ evt.preventDefault(); }	
						dojo.event.browser.stopEvent(evt);

					});

					// TODO : Jon Sykes, Tue Nov 28 10:22:19 2006
					// need to add some checks for select options so that we 
					// don't add the trigger inside the select form element

					// can this node have a child node, if so add into it, if not append it after
					if(dojo.byId(helpFields[j-i].id).firstChild){
						dojo.byId(helpFields[j-i].id).appendChild( thisHelpTrigger );
					}else{
						dojo.html.insertAfter(thisHelpTrigger, dojo.byId(helpFields[j-i].id) );
					}


					// kill the click event
					dojo.event.connect(thisHelpTrigger, "onclick", function(evt) { 	
						// squelch all the propogation, bubbles and default event actions
						dojo.event.browser.stopEvent(evt);
						if (evt.preventDefault){ evt.preventDefault(); }			

					});


				break;

				case 'inline':

					helpObject = helpFields[j-i];
					if(!dojo.byId(helpFields[j-i].id)){
						break;
					}
					
					var targetElement = dojo.byId(helpFields[j-i].id);

					if(!dojo.widget.byId("EA_"+helpObject.id)){


						EAItem = dojo.widget.createWidget("assistanceInline", {widgetId: "EA_"+helpObject.id, content: this.helpData.helpItem[helpObject.helpId-1] } );

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

						if(dojo.lang.inArray(insideTags, targetElement.tagName)){

							targetElement.appendChild(EAItem.domNode);

						}else{

							dojo.dom.insertAfter(EAItem.domNode, targetElement);

						}

						// Should we handle error state - TR's for example 

					}


				break;			
			}

     }while (--i);

	},



	// prevents trailing comma, all edits above this line please //
	sanitySaver:'' 
});