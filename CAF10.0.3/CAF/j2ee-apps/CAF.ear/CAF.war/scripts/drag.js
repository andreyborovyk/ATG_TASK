//**********************************************************************************************************************************************
//
// Drag.js File
//
// (C) Copyright 1997-2009 Art Technology Group, Inc.
// All rights reserved.
//
// This page is the encapsulates a helper object that is responsible for 
// controlling drag and drop behavior and calling event handlers
// associated with those events.  This library is cross browser compatible
//
//**********************************************************************************************************************************************


/* -------------------------*/ 
/* Script version info        */
/* -------------------------*/

var script_drag                  = new Object();
script_drag.type                 = "text/javascript";
script_drag.versionMajor         = 0;
script_drag.versionMinor         = 9;



//**********************************************************************************************************************************************
// Dynamically include all required object definitions
//
//**********************************************************************************************************************************************

// Create variable to check to verify that page is loaded
//
var dragReady                             = false;

// Create registry variable here so it is all ready for use
//
var DragDropRegistry                      = new DragDropRegistry();

/*************************************************************
 **
 **
 **  DragDropRegristry Object
 **
 **
 **  This object activates components for drag and drop
 **  operations.
 **
 *************************************************************/

function DragDropRegistry()
{
   //**********************************************************************************************************************************************
   // Initialize Properties
   //
   this.dragDropRegistry                  = this;
   this.dragDropRegistry.currentElement   = null;
   this.dragDropRegistry.dropElement      = null;
   this.dragDropRegistry.mouseX           = "";
   this.dragDropRegistry.mouseY           = "";

   //**********************************************************************************************************************************************
   // Initialize methods
   //   
   this.dragDropRegistry.registerDrag     = DragDropRegisterDrag;
   this.dragDropRegistry.registerDrop     = DragDropRegisterDrop;
   this.dragDropRegistry.start            = DragDropStart;
   this.dragDropRegistry.drag             = DragDropDrag;
   this.dragDropRegistry.end              = DragDropEnd;
   this.dragDropRegistry.loadEvent        = DragDropLoadEvent;
   this.dragDropRegistry.dropWatch        = DragDropWatch;
   this.dragDropRegistry.dropCancel       = DragDropCancel;
   this.dragDropRegistry.drop             = DragDropDrop;
}

/*************************************************************
 **
 **
 **  DragDropRegister Function
 **
 **
 **  This function is responsible for activating the element
 **  with the drag and drop event handlers and refrence variables.
 **  The function takes the following parameters:
 **
 **  objElementHandle:  This is the element that needs to be
 **                     clicked to be drug. 
 **
 **  objElement: If the handle is seperate from the element you
 **              want to drag, include the element you want to 
 **              have drug.
 **
 **  Min/max variables define a box that the object can not 
 **  be drug out of. 
 **
 **
 *************************************************************/
 
function DragDropRegisterDrag(objElementHandle, objElement, minX, maxX, minY, maxY)
{
   var dragDropHandler           = this;
   
   // Refrence the registry
   //
   objElementHandle.registry     = dragDropHandler;
   
   // Register the event
   //
   if (document.all)
   {
      objElementHandle.ondragstart  = dragDropHandler.start;
      objElementHandle.ondrag       = dragDropHandler.drag;
      objElementHandle.ondragend    = dragDropHandler.end;
   }
   else
   {
	   objElementHandle.onmousedown  = dragDropHandler.start;
	}
	

   // If an element was passed in as a base, save it here
   //
	objElementHandle.base = objElement;
	
	if (objElement == null) 
      objElementHandle.base = objElementHandle;

   // Set left and top styles if they have not allready been set
   // We need to move this into the start method so we can have
   // elements that are not required to have absolute positioning
   //
	if (isNaN(parseInt(objElementHandle.base.style.left))) 
	   objElementHandle.base.style.left   = "0px";
	if (isNaN(parseInt(objElementHandle.base.style.top))) 
	   objElementHandle.base.style.top    = "0px";

   // set boundaries if they have been passed in
   //
	objElementHandle.minX	= typeof minX != 'undefined' ? minX : null;
	objElementHandle.minY	= typeof minY != 'undefined' ? minY : null;
	objElementHandle.maxX	= typeof maxX != 'undefined' ? maxX : null;
	objElementHandle.maxY	= typeof maxY != 'undefined' ? maxY : null;

   // Initialize integration functions
   //
	objElementHandle.base.onDragStart	= new Function();
	objElementHandle.base.onDragEnd	   = new Function();
	objElementHandle.base.onDrag		   = new Function();
	objElementHandle.base.onDrop		   = new Function();
	
	return false;
}

function DragDropRegisterDrop(objElementHandle, objElement, aTypes)
{
   var dragDropHandler           = this;
   
   objElementHandle.base         = objElement;
   
   // Refrence the registry
   //
   objElement.registry           = dragDropHandler;
   
   // Store acceptible drop types
   //
   objElement.dropTypes          = aTypes;
   
   // Register the event
   //
   if (document.all)
   {
      objElementHandle.ondragenter  = dragDropHandler.dropWatch;
      objElementHandle.ondragleave  = dragDropHandler.dropCancel;
      objElementHandle.ondrop       = dragDropHandler.drop;
   }
   else
   {
	   objElementHandle.onmouseover	= dragDropHandler.dropWatch;
	}
	
   // Initialize integration functions
   //
	objElement.onDropStart        = new Function();
	objElement.onDrop		         = new Function();
	objElement.onDragOver         = new Function();
	objElement.onDragOut          = new Function();
	
	return false;
}

function DragDropStart(event)
{
   // Grab object refrences
   //
   var objElement                      = this;
   var dragDropRegistry                = objElement.registry;
   dragDropRegistry.currentElement     = objElement;
   
   // Initialize Drag Icon
   //
   if (!dragDropRegistry.dragIcon)
   {
      var dragIcon                           = document.createElement("img");
      dragIcon.src                           = "/CAF/images/tree/folderBActionPlus.gif";
      dragIcon.style.visibility              = "hidden";
      dragIcon.style.position                = "absolute";
      dragDropRegistry.dragIcon              = document.body.appendChild(dragIcon);
   }
         
   // Load client event
   //
	clientEvent                         = dragDropRegistry.loadEvent(event);
	objElement.lastMouseX	            = clientEvent.clientX;
	objElement.lastMouseY	            = clientEvent.clientY;
	
	var y    = parseInt(objElement.base.style.top);
	var x    = parseInt(objElement.base.style.left);
	
	dragDropRegistry.dragIcon.visibility   = "";
	dragDropRegistry.dragIcon.style.top    = y + "px";
	dragDropRegistry.dragIcon.style.left   = x + "px";
	
	// Call integration
	//
	objElement.base.onDragStart(x, y);

	if (objElement.minX != null)	objElement.minMouseX	= clientEvent.clientX - x + objElement.minX;
	if (objElement.maxX != null)	objElement.maxMouseX	= objElement.minMouseX + objElement.maxX - objElement.minX;

	if (objElement.minY != null)	objElement.minMouseY	= clientEvent.clientY - y + objElement.minY;
	if (objElement.maxY != null)	objElement.max
   
   if (!document.all)
   {
	   document.onmousemove	   = dragDropRegistry.drag;
	   document.onmouseup		= dragDropRegistry.end;
	}
	
	return false;
}

function DragDropDrag(event)
{
	var objElement = DragDropRegistry.currentElement;
	clientEvent    = DragDropRegistry.loadEvent(event);

	var eventY	   = clientEvent.clientY;
	var eventX	   = clientEvent.clientX;
	var displaceX, displaceY;
	
	if (objElement.base.style.position != "absolute")
	{
	   var startY                       = objElement.base.offsetTop;
	   //objElement.base.style.position   = "absolute";
	   //objElement.base.style.top        = startY;
	}	
	
	var elementY   = parseInt(objElement.base.style.top);
	var elementX   = parseInt(objElement.base.style.left);   

	if (objElement.minX != null) eventX = Math.max(eventX, objElement.minMouseX);
	if (objElement.maxX != null) eventX = Math.min(eventX, objElement.maxMouseX);
	if (objElement.minY != null) eventY = Math.max(eventY, objElement.minMouseY);
	if (objElement.maxY != null) eventY = Math.min(eventY, objElement.maxMouseY);

	displaceX = elementX + (eventX - objElement.lastMouseX)
	displaceY = elementY + (eventY - objElement.lastMouseY)
	
	//DragDropRegistry.currentElement.base.style.left    = displaceX + "px";
	//DragDropRegistry.currentElement.base.style.top     = displaceY + "px";
   DragDropRegistry.dragIcon.style.visibility         =  "";
	DragDropRegistry.dragIcon.style.left               = eventX + 10;
	DragDropRegistry.dragIcon.style.top                = eventY + 10;
	DragDropRegistry.currentElement.lastMouseX	      = eventX;
	DragDropRegistry.currentElement.lastMouseY	      = eventY;

	DragDropRegistry.currentElement.base.onDrag(displaceX, displaceY);
	
	return false;
}

function DragDropEnd()
{
	document.onmousemove = null;
	document.onmouseup   = null;
	
	DragDropRegistry.currentElement.base.style.zIndex     = 0;
	
	DragDropRegistry.currentElement.base.style.position   = "";
	
	DragDropRegistry.currentElement.base.onDragEnd(	parseInt(DragDropRegistry.currentElement.base.style.left), 
								parseInt(DragDropRegistry.currentElement.base.style.top));
								
	DragDropRegistry.currentElement = null;
	DragDropRegistry.dragIcon.style.visibility            = "hidden";
	
	return false;
}

function DragDropLoadEvent(event)
{
	if (typeof event == 'undefined') 
	   event = window.event;
	if (typeof event.layerX == 'undefined') 
	   event.layerX = event.offsetX;
	if (typeof event.layerY == 'undefined') 
	   event.layerY = event.offsetY;
	return event;
}

function DragDropWatch()
{ 
   // Grab object refrences
   //
   var objElementHandle                   = this;
   var objElement                         = objElementHandle.base;
   var dragDropRegistry                   = objElement.registry;
   
   // Check to make sure that we have a current drag element,
   // the current drag element has a type, and that the type
   // matches what we can accept 
   if (dragDropRegistry.currentElement)
   {
      var currentElement                  = dragDropRegistry.currentElement.base;
         
      if (objElement.dropTypes)
      {
         if (currentElement.type)
         {
            for (var i = 0; i < objElement.dropTypes.length; i++)
            {
               if (currentElement.type == objElement.dropTypes[i])
               {
                  dragDropRegistry.dropElement     = objElement;
                  if (!document.all)
                  {
                     objElementHandle.onmouseup       = dragDropRegistry.drop;
                     objElementHandle.onmouseout      = dragDropRegistry.dropCancel;
                  }
                  objElement.onDragOver();
                  break;
               }
            }
         }
      }
      else
      {
         dragDropRegistry.dropElement     = objElement;
         if (!document.all)
         {
            objElementHandle.onmouseup       = dragDropRegistry.drop;
            objElementHandle.onmouseout      = dragDropRegistry.dropCancel;
         }
         objElement.onDragOver();
      }
   }
   if (document.all)
	{
	   window.event.returnValue   = false;
	}
	return false;
}

function DragDropCancel()
{
   // Grab object refrences
   //
   var objElementHandle                   = this;
   var objElement                         = objElementHandle.base;
   var dragDropRegistry                   = objElement.registry;
   dragDropRegistry.dropElement           = null;
   objElement.onDragOut();
   
   objElementHandle.onmouseup             = null;
   objElementHandle.onmouseout            = null;
   return false;
}

// This function passes the moved item first followed by the destination item
// The event is thrown to both source and destination
//
function DragDropDrop()
{
   // Grab object refrences
   //
   var objElementHandle                   = this;
   var objElement                         = objElementHandle.base;
   var dragDropRegistry                   = objElement.registry;
   if (!dragDropRegistry.currentElement)
      return;
      
   var movedElement                       = dragDropRegistry.currentElement.base;
   
   objElement.onDrop(movedElement, objElement);
   movedElement.onDrop(movedElement, objElement);
   
   return false;
}
