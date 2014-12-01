//**********************************************************************************************************************************************
//
// tree.js File
//
// (C) Copyright 1997-2009 Art Technology Group, Inc.
// All rights reserved.
//
// This page is the encapsulates a helper object that is responsible for 
// controlling dhtml tree behavior and calling event handlers
// associated with those events.  This library is cross browser compatible
//
//**********************************************************************************************************************************************


/* -------------------------*/ 
/* Script version info        
/* -------------------------*/

var script_tree                  = new Object();
script_tree.type                 = "text/javascript";
script_tree.versionMajor         = 0;
script_tree.versionMinor         = 9;
script_tree.debug                = 0;


// this can be used by other javascript file to determine if they need to import this file
/*    example  
 *
 *    if ( typeof script_tree != "object" ) 
 *         document.write("<scr"+"ipt type='text/javascript' src='/CAF/scripts/tree.js'><"+"/"+"script>");
 *
*/

/*************************************************************
 **
 **
 **  Tree Object
 **
 **
 **  This object defines the global area to be occupied by the
 **  main tree.  This object is a management object that defines
 **  a starting point forthe tree.  This object is a container
 **  for child Branch objects.
 **
 *************************************************************/

function Tree(id)
{
   //**********************************************************************************************************************************************
   // Initialize Element
   //
   this.tree                              = document.createElement("div");
   this.tree.id                           = id;
   
   //**********************************************************************************************************************************************
   // Initialize Properties
   //
   this.tree.branches                     = new Array();
   this.tree.branchesById                 = new Array();
   this.tree.loadedBranches               = new Array();
   this.tree.highlightedBranch            = null;
   this.tree.checkboxes                   = false;
   this.tree.checkboxBehaviors            = null;
   this.tree.cssDots                      = false;
   this.tree.rootNodes                    = new Array();
   this.tree.branchStateAction            = null;
   this.tree.checkedItems                 = new Array();

   //**********************************************************************************************************************************************
   // Initialize Methods
   //
   this.tree.addBranch                    = TreeAddBranch;
   this.tree.getBranchById                = TreeGetBranchById;
   this.tree.openToBranch                 = TreeOpenToBranch;
   this.tree.getCheckedItems              = TreeGetCheckedItems;
   this.tree.setCheckedItems              = TreeSetCheckedItems;
   this.tree.addCheckedItem               = TreeAddCheckedItem;
   this.tree.removeCheckedItem            = TreeRemoveCheckedItem;
   this.tree.deselectAllItems             = TreeDeselectAllItems;
   this.tree.checkboxInitState            = CheckboxStateInit;
   this.tree.checkboxOnclick              = null;
   this.tree.resizeWindow                 = null;
}

/*************************************************************
 **
 **
 **  TreeAddBranch
 **
 **  This function adds a new branch to the tree.  This function
 **  accepts the following params:
 **  id: ID of the branch, must be unique.
 **  label: text to be used as the branch label
 **  type: Application specific type to be used for drag and drop
 **        operations
 **
 *************************************************************/
function TreeAddBranch(id, label, type)
{
   var tree                               = this;
   
   var newBranch                          = new Branch(tree, tree, id, label, type).branch
   newBranch                              = tree.appendChild(newBranch);
   tree.branchesById[newBranch.id]        = newBranch;
   tree.branches[tree.branches.length]    = newBranch;
   tree.rootNodes[tree.rootNodes.length]  = newBranch;

   tree.resizeWindow();
   return newBranch;
}

/*************************************************************
 **
 **
 **  TreeGetBranchById
 **
 **  This function returns a branch by ID or false if not found
 **  or loaded.
 **
 *************************************************************/
function TreeGetBranchById(id)
{
   var tree                               = this;

   if ( tree.branchesById[id]) 
      return tree.branchesById[id];

   return false;
}

/*************************************************************
 **
 **
 **  TreeOpenToBranch
 **
 **  This function opens the tree to a specified node.  This 
 **  function accepts an array of node id's to use as a path
 **  to the desired node.
 **
 *************************************************************/
function TreeOpenToBranch(pathArray)
{
   var tree                               = this;
   
   var currentNode                        = tree;
   var pastNode                           = tree;

   // Cycle through the path array and find the 
   // path node one at a time.  Open each node
   // as you get there.
   //
   for (var i = 0; i < pathArray.length; i++)
   {
      if (currentNode.branches.length > 0)
      {
         for (var x = 0; x < currentNode.branches.length; x++)
         {
            if (currentNode.branches[x].id == pathArray[i])
            {
               pastNode       = currentNode;
               currentNode    = currentNode.branches[x];
               currentNode.open();
               break;
            }
         }
      }
      if (currentNode == pastNode)
      {
         alert("not found " + currentNode.id);
         break;
      }
   }
}

/*************************************************************
 **
 **
 **  TreeGetSelectedItems
 **
 **  This function returns all branches that have been checked
 **  by the user
 **
 *************************************************************/
function TreeGetCheckedItems()
{
  var tree                               = this;
  return tree.checkedItems;
}

/*************************************************************
 **
 **
 **  TreeSetSelectedItems
 **
 **  This function set all branches that have been checked
 **  by the user
 **
 *************************************************************/
function TreeSetCheckedItems(checkedItems)
{
  var tree                               = this;
  tree.checkedItems                      = checkedItems;
}

function TreeAddCheckedItem(item)
{
  var tree                                      = this;
  tree.checkedItems[tree.checkedItems.length]   = item;
}

function TreeRemoveCheckedItem(item)
{
  var tree                               = this;

  for (var i = 0; i < tree.checkedItems.length; i++)
  {
    if (tree.checkedItems[i] == item)
    {
      var aTemp = tree.checkedItems.splice(i, 1);
      break;
    }
  }
}

/*************************************************************
 **
 **
 **  TreeDeselectAllItems
 **
 **  This function uncheckes all checked nodes.
 **
 *************************************************************/
function TreeDeselectAllItems()
{
   var tree                               = this;
   
   for (var i = 0; i < tree.checkedItems.length; i++)
   {  
      var branch                          = tree.checkedItems[i];
      branch.checkbox.checked             = false;
      branch.imageCheck.style.display     = "none";
   }
   tree.checkedItems                      = new Array();   
}


/*************************************************************
 **
 **
 **  Branch Object
 **
 **
 **  This object defines the main display object of the tree
 **  Each node of the tree is a "Branch" and is responsible for
 **  maintaining it's own display parameters as well as providing
 **  an interface for adding child branches.  This element will
 **  be implemented as a table element to take advantage of layout
 **  attributes.
 **
 *************************************************************/

function Branch(tree, parent, id, label, type)
{
   //**********************************************************************************************************************************************
   // Initialize Element
   //
   this.branch                            = document.createElement("div");
   this.branch.id                         = tree.id+ "_" + type + "_" + id;
   this.branch.sourceId                   = id;
   this.branch.type                       = type;

   //**********************************************************************************************************************************************
   // Initialize Properties
   //
   this.branch.tree                       = tree;
   this.branch.parent                     = parent;
   this.branch.loadLevel                  = 0;
   this.branch.loadAction                 = null;
   this.branch.hasChildren                = true;
   this.branch.iconOpen                   = null;
   this.branch.iconClosed                 = null;
   this.branch.iconCheck                  = null;
   this.branch.selectable                 = false;
   this.branch.label                      = label;
   this.branch.itemCount                  = null;
   this.branch.displayNode                = null;
   this.branch.highlighted                = false;
   this.branch.classNameStr               = null;
   this.branch.highlightedClass           = null;
   this.branch.branches                   = new Array();
   this.branch.displayRow                 = null;
   this.branch.type                       = type;
   this.branch.initStateOpen              = false;
   this.branch.initStateChecked           = false;
   this.branch.initStateSelected          = false;
   this.branch.isFirst                    = false;
   this.branch.isLast                     = false;
   this.branch.checkboxes                 = tree.checkboxes;
   this.branch.canDrag                    = false;
   this.branch.canDrop                    = false;
   
   //**********************************************************************************************************************************************
   // Initialize Refrence Variables
   //
   this.branch.childCell                  = "";
   this.branch.displayCell                = "";
   this.branch.icon                       = "";
   this.branch.checkbox                   = new CheckBox ( this.branch,
                                                           this.branch.tree,
                                                           false /*disabled*/,
                                                           false /*checked*/ )  ;
   this.branch.imageCheck                 = null;

   //**********************************************************************************************************************************************
   // Initialize Methods
   //
   this.branch.display                    = BranchDisplay;
   this.branch.open                       = BranchOpen;
   this.branch.close                      = BranchClose;
   this.branch.notify                     = BranchNotify;
   this.branch.loadChildren               = BranchLoadChildren;
   this.branch.unloadAction               = BranchUnloadAction;
   this.branch.addChild                   = BranchAddChild;
   this.branch.select                     = BranchSelect;
   this.branch.toggle                     = BranchToggle;
   this.branch.getDisplayCell             = BranchGetDisplayCell;
   this.branch.highlight                  = BranchHighlight;
   this.branch.checkChildLoad             = BranchCheckChildLoad;
   this.branch.dragOver                   = BranchDragOver;
   this.branch.dragOut                    = BranchDragOut;
   this.branch.drop                       = BranchDrop;
   this.branch.selectAction               = null;
   this.branch.onfocus                    = BranchFocus;
   this.branch.onblur                     = BranchBlur;
   this.branch.onkeydown                  = BranchKeyPress;
}



/*************************************************************
 **
 **
 **  BranchDisplay
 **
 **  This function is responsible for creating the layout of
 **  the branch object
 **
 *************************************************************/
function BranchDisplay()
{
   var branch                             = this; 
   /* so get the level of nesting for this branch use this function 
      0 for root items, 1-N  for the child items
   */
   // var level = getBranchLevel(this.tree , this.parent.id);

   branch.actionFrameDiv                  = document.createElement("div");
   branch.actionFrameDiv.style.width      = "0";
   branch.actionFrameDiv.style.height     = "0";
   branch.actionFrameDiv.style.position   = "absolute";
   branch.actionFrameDiv.style.top        = 6;
   branch.actionFrameDiv.style.left       = 6;

   branch.actionFrame                     = document.createElement("iframe");
   branch.actionFrame.height              = ( script_tree.debug > 5) ? 100 : 0;
   branch.actionFrame.width               = ( script_tree.debug > 5) ? 100 : 0;
   branch.actionFrame.border              = 0;
   branch.actionFrame.style.visibility    = ( script_tree.debug > 5) ? "visible" : "hidden";

   branch.actionFrameDiv.appendChild(branch.actionFrame);
   branch.appendChild(branch.actionFrameDiv);

   branch.mainDiv                         = document.createElement("div");
   branch.mainDiv.style.position          = "relative";
   branch.mainDiv.style.width             = "98%";
   branch.mainDiv.style.margin            = 0;
   branch.table                           = document.createElement("table");

   if ( branch.tree.cssDots ) 
   {
      branch.table.cellPadding            = 0;
      branch.table.cellSpacing            = 0;
      branch.table.border                 = 0;
      branch.table.style.margin           = -1;
      branch.table.className              = "dTree_tree_dotted";
   } 
   else 
   {
      branch.table.cellPadding            = 0;
      branch.table.cellSpacing            = 0;
      branch.table.border                 = 0;
      branch.table.style.margin           = 0;
      branch.table.className              = "dTree_tree_not_dotted";
   }
   branch.displayRow                      = branch.table.insertRow(0);
   branch.displayRow.className            = "dTree_tree_table_row_displayRow";

   // Create icon cell
   //
   var iconCell                           = branch.displayRow.insertCell(branch.displayRow.cells.length);
   iconCell.colSpan                       = 1;

   if (branch.hasChildren)
   {
      iconCell.className                  = "dTree_controlSpace";
      var openIcon                        = document.createElement("img");
      // alert( branch.iconOpen);
      if ( branch.iconOpen != null && branch.iconOpen != "null") {
        openIcon.src                      = branch.iconOpen;
      } else {
        openIcon.src                      = branch.parent.openIcon.src;
        branch.iconOpen                   = branch.parent.openIcon.src;
      }
      branch.openIcon                     = iconCell.appendChild(openIcon);
      branch.openIcon.branch              = branch;
      branch.openIcon.onclick             = branch.toggle;
      branch.openIcon.style.cursor        = "hand";
      branch.openIcon.style.display       = "none";      
   
      var closedIcon                      = document.createElement("img");
      if ( branch.iconClosed != null && branch.iconClosed != "null") {
        closedIcon.src                    = branch.iconClosed;
      } else {
        closedIcon.src                    = branch.parent.closedIcon.src;
        branch.iconClosed                 = branch.parent.closedIcon.src;
      }
      branch.closedIcon                   = iconCell.appendChild(closedIcon);
      branch.closedIcon.branch            = branch;
      branch.closedIcon.onclick           = branch.toggle;
      branch.closedIcon.style.cursor      = "hand";
      branch.icon                         = branch.closedIcon;
   } 
   else 
   {
      iconCell.innerHTML                  = "<div class='controlSpace'>&nbsp;</div>";
   }
  
   if ( branch.tree.cssDots ) 
   {
     iconCell.className                   = "dTree_dots-branch-item";
     if ( branch.isFirst && branch.parent.id == branch.tree.id )
          iconCell.className              = "dTree_dots-branch-first-item";
     if ( branch.isLast ) 
          iconCell.className              = "dTree_dots-branch-last-item";
     if ( branch.isFirst && branch.isLast ) 
          iconCell.className              = "dTree_dots-branch-only-item";
   } 

   // CHECK BOX CELL

   var showCheck = ( branch.tree.checkboxes && (branch.checkboxes != false )) ? true : false ;
   if ( showCheck ) {
       var checkboxCell                   = branch.displayRow.insertCell(branch.displayRow.cells.length);
       checkboxCell.colSpan               = 1;
       branch.checkbox.control            = branch.checkbox.display();
       if ( branch.initStateChecked ) branch.checkbox.check();
       checkboxCell.appendChild(  branch.checkbox.control );
   }
   // TITLE CELL
   // If we have a display node, we insert that in the title cell;
   // if not, we use the tree label
   //
   branch.displayCell                     = branch.displayRow.insertCell(branch.displayRow.cells.length);
   branch.displayCell.colSpan = 1;
   branch.displayCell.setAttribute("width","96%");
   branch.displayCell.className           = branch.classNameStr;    
   if (branch.displayNode)
   {
      branch.displayNode                  = branch.displayCell.appendChild(branch.displayNode);
      if (branch.selectable)
      {
          branch.displayNode.style.cursor = "hand";  
          branch.displayNode.onclick      = branch.select;
          branch.displayNode.branch       = branch
      }          
   }     
      
   else
   {
      if (branch.selectable)
      {
         var labelSpan                    = document.createElement("a");
         labelSpan.branch                 = branch;

         // set the HREF properly, setting it to "#" will cause the page to move, potentially
         //
         if (branch.iconCheck == null)
           labelSpan.href                   = "#";
         else
           labelSpan.href                   = "javascript:";
	       labelSpan.className              = "dTree_label_anchor"; 
         labelSpan.innerHTML              = branch.label;
         labelSpan.onclick                = branch.select;
         labelSpan                        = branch.displayCell.appendChild(labelSpan);
         labelSpan.branch                 = branch;

      }
      else {
         branch.displayCell.innerHTML     = branch.label;
	 //         branch.displayCell.className     = branch.className; 
      }
      if (branch.itemCount)
      {
         labelSpan.innerHTML += "&nbsp;[" + branch.itemCount + "]";
      }
   }
   
   // Put the check.gif in place
   //
   if (branch.iconCheck != null)
   {
     var imageCheck             = document.createElement("img");
     imageCheck.src             = branch.iconCheck;
     imageCheck.style.display   = "none";
     imageCheck                 = branch.displayCell.appendChild(imageCheck);
     branch.imageCheck          = imageCheck;
   }

   branch.childRow                        = branch.table.insertRow(1);
   //branch.childRow.style.display          = "none";
 
   var stretchCell                        = branch.childRow.insertCell(branch.childRow.cells.length);
   stretchCell.colSpan = 1;
     stretchCell.innerHTML                = "<blank />";
  
   if ( ! branch.isLast && branch.tree.cssDots ) 
       stretchCell.className              = "dTree_dots-branch";

   branch.childDivCell                    =  branch.childRow.insertCell(branch.childRow.cells.length); //document.createElement("div");
   branch.childDivCell.colSpan            = ( branch.tree.checkboxes ) ? 2:1;

   branch.childDiv                        = document.createElement("div");
   branch.childDivCell.appendChild(branch.childDiv);

   // if checkboxes add extra cell here ( empty spacer ) 
   // 
   if ( ! showCheck )        
          branch.childDiv.colSpan           = 2;

   branch.childDiv.style.display            = "none";
   branch.childDiv.loaded                   = false;
   
   /*
   if (branch.canDrag)
   {
      branch.tree.dragDropRegistry.registerDrag(branch.openIcon, branch);
      branch.tree.dragDropRegistry.registerDrag(branch.closedIcon, branch);
   }
   
   if (branch.canDrop)
   {
      branch.tree.dragDropRegistry.registerDrop(branch.openIcon, branch, branch.dropType);
      branch.tree.dragDropRegistry.registerDrop(branch.closedIcon, branch, branch.dropType);
      branch.onDrop                 = branch.drop;
      branch.onDragOver             = branch.dragOver;
      branch.onDragOut              = branch.dragOut;
   }
   */
   
   branch.mainDiv.appendChild(branch.table);
   branch.appendChild(branch.mainDiv);
   // alert(branch.innerHTML);
   if ( showCheck ) {
     branch.checkbox.initState();
   }

   if ( script_tree.debug  > 3) alert( branch.innerHTML);

   if ( branch.initStateOpen ) branch.open();

   // Load branches as defined by object constructor
   //
   // if (branch.loadLevel && (branch.loadLevel > 0))  branch.loadChildren();

   branch.tree.resizeWindow();
}

function BranchOpen()
{
   var branch                              = this;
   
   // Show branches and switch icon
   //
   branch.childDiv.style.display           = "block";
   if ( branch.hasChildren ) {
     branch.closedIcon.style.display       = "none";  
     branch.openIcon.style.display         = "block";  
     branch.icon                           = branch.openIcon;
     if (! branch.childDiv.loaded  ) {
       branch.loadChildren();
     } else {
       branch.notify();
       ContinueOpeningToPath( this.tree );
     }
   } 

   //   if (branch.branches.length == 0)    branch.loadChildren();
}

function BranchClose()
{
   var branch                             = this;
   
   // Hide branches and switch icon
   //
   branch.childDiv.style.display          = "none";
   branch.openIcon.style.display          = "none";  
   branch.closedIcon.style.display        = "block";  
   branch.icon                            = branch.closedIcon;
   branch.notify();

}
function BranchNotify() 
{
   var branch                             = this;
   if (  this.tree.branchStateAction ) {
     nodeState = (  this.icon == this.closedIcon) ? "closed" : "open";
     params                                  = (this.tree.branchStateAction.indexOf("?") == -1 )? "?" : "&";
     params                                  += "treeId="      + this.tree.id     ;
     params                                  += "&sourceId="   + branch.sourceId  ;
     params                                  += "&sourceType=" + branch.type      ;
     params                                  += "&compId="     + this.tree.id +"_"+branch.type+"_"+ branch.sourceId  ;
     params                                  += "&nodeState="  + nodeState        ;

     if ( script_tree.debug  > 2 ) window.open( this.tree.branchStateAction + params, "test");
     branch.actionFrame.src                 =  this.tree.branchStateAction + params;
   }
}

function BranchLoadChildren()
{
   var branch                             = this;
   // Constuct base parameters passed to loadAction via query params
   //
   params                                  = (branch.loadAction.indexOf("?") == -1 )? "?" : "&";
   params                                  += "treeId="      + this.tree.id     ;
   params                                  += "&parentId="   + branch.id        ;
   params                                  += "&sourceId="   + branch.sourceId  ;
   params                                  += "&sourceType=" + branch.type      ;


   // Constuct additional parameters passed to loadAction via query params
   // these are provided by the branch originator object and is optional
   if ( branch.loadActionParams ) 
   {
      for ( var j = 0 ; j < branch.loadActionParams.length ; j++ ) 
      {
         if ( branch.loadActionParams[j] ) 
         {
            if ( branch.loadActionParams[j].indexOf(";") > -1 ) 
            {
               temp = branch.loadActionParams[j].split(";");
               if ( temp[0] != "" )
                  params += "&" + temp[0]+ "=" + escape(temp[1]);
           }
         }
      }
   }

   // Load constructed url in to branch's iFrame 
   //  
   if ( script_tree.debug  > 0 ) window.open( branch.loadAction + params, "test");
   

   branch.actionFrame.src                 = branch.loadAction + params;
   branch.childDiv.loaded                 = true;
}

function BranchUnloadAction()
{
   var branch                             = this;
   branch.actionFrame.src                 = "";
}

function BranchAddChild(id, label, type)
{
   var branch                                = this;
   
   var child                                 = new Branch(branch.tree, branch, id, label, type).branch;
   child                                     = branch.childDiv.appendChild(child);
   child.parent                              = branch;
   
   
   branch.tree.branches[branch.tree.branches.length]        = child;
   branch.tree.branchesById[child.id]                       = child;

   branch.branches[branch.branches.length]   =  child;
   branch.tree.resizeWindow();
   return child;
}

function BranchSelect()
{
   var branch                             = this.branch ? this.branch : this;
   
   // Unselect previous selection
   //
   if (branch.tree.highlightedBranch)
      branch.tree.highlightedBranch.highlight();
      
   // Highlight current selection
   //
   branch.highlight();    
   
   // Call integration
   //
   branch.selectAction();
}

function BranchToggle()
{
   var branch                             = this.branch;
   if (branch.childDiv.style.display == "none")
      branch.open();
   else
      branch.close();

   branch.tree.resizeWindow();
}

function BranchGetDisplayCell()
{
   var branch                             = this;
   return branch.displayCell;
}

function BranchHighlight()
{
   var branch                             = this.branch ? this.branch : this;

   if (branch.highlighted)
   {
      branch.highlighted                  = false;
      branch.displayCell.className        = branch.classNameStr;
      branch.tree.highlightedBranch       = null;
   }
   else
   {
      branch.highlighted                  = true;
      branch.displayCell.className        = branch.highlightedClass;
      branch.tree.highlightedBranch       = branch;
   }
}

function BranchCheckChildLoad(loadLevel)
{
   var branch                             = this;
   var firstNode                          = (loadLevel == branch.loadLevel)
   branch.loadLevel                       = loadLevel;
   if (branch.loadLevel > 0)
   {
      if (firstNode)
      {
         for (var i = 0; i < branch.branches.length-1; i++)
         {
            branch.branches[i].checkChildLoad(branch.loadLevel);
         }
      }
      else if(branch.loadLevel == 1)
      {
         branch.loadChildren();
      }
      else
      {
         for (var i = 0; i < branch.branches.length; i++)
         {
            branch.branches[i].checkChildLoad(branch.loadLevel - 1);
         }
      }
   }      
}


function BranchDragOver()
{
   var branch                             = this;
   branch.displayCell.className           = "dTree_dragover";
}

function BranchDragOut()
{
   var branch                             = this;
   branch.displayCell.className           = branch.className;
   return false;
}

function BranchDrop(movedElement, dropElement)
{
   var branch                             = this;
   
   if (dropElement == branch)
   {
     // proccessDragDropEvent should be a method provided by the hosting application 
     if ( proccessDragDropEvent ) {
       proccessDragDropEvent(movedElement,dropElement); 
     } 
   }
}

/*************************************************************
 **
 **
 **  Branch Key Strokes / Event Functions
 **
 **
 *************************************************************/
function BranchFocus() 
{
  return true;
}
function BranchBlur() 
{
  return true;
}
function BranchKeyPress(event) 
{  
  if ( ! event && window.event ) {
     event = window.event ;
  }
  if ( event ) 
     _keyCode = event.keyCode;
  else 
     return;

  if (  branchTargetKeyCode(_keyCode) ) {
    switch ( _keyCode ) {
       case 39: /* right arrow key */ 
        if ( this.hasChildren ) this.open();
        break;

       case 37: /* left arrow key */
        if ( this.hasChildren ) this.close();
        break;   

       /* testing tab and shift tab replacements */
       case 40: /* down arrow key */
       case 38: /* up arrow key */

    }
    event.cancelBubble = true;
    event.returnValue  = false;
    return false;
  } else {
    return true;
  }
}

function branchTargetKeyCode(_keyCode)
{
  if ( _keyCode == 38 ) return true;
  if ( _keyCode == 40 ) return true;
  if ( _keyCode == 37 ) return true;
  if ( _keyCode == 39 ) return true;
  return false;
}


//************************************************************************************
// this function is called from the iframes and is used torender child items
//
//
function InsertBranches(tree, parentId, branches)
{

  var tree                          = document.getElementById(tree);
  var parentBranch                  = tree.getBranchById(parentId);

  if (parentBranch && typeof(branches) == 'object') 
  {
    for ( var i = 0 ; i < branches.length ; i++ ) 
    {
      // Create child branch
      //
      var child                     = parentBranch.addChild(branches[i].id ,branches[i].label,branches[i].type);
     
      // base property copy
      child.classNameStr               = branches[i].classNameStr;

      child.hasChildren = 
        ( typeof branches[i].hasChildren == 'boolean' ) ? 
           branches[i].hasChildren : 
	  (branches[i].hasChildren == "true");

      child.initStateOpen  = 
        ( typeof branches[i].initStateOpen == 'boolean' ) ? 
           branches[i].initStateOpen : 
	  (branches[i].initStateOpen == "true");

      child.initStateSelected  = 
        ( typeof branches[i].initStateSelected == 'boolean' ) ? 
           branches[i].initStateSelected : 
	  (branches[i].initStateSelected == "true");

      child.initStateChecked  = 
        ( typeof branches[i].initStateChecked == 'boolean' ) ? 
           branches[i].initStateChecked : 
	  (branches[i].initStateChecked == "true");



      child.iconOpen                = branches[i].iconOpen;
      child.iconClosed              = branches[i].iconClosed;
      child.iconCheck               = branches[i].iconCheck;
      child.canDrag                 = branches[i].canDrag;
      child.canDrop                 = branches[i].canDrop;
      child.selectable              = branches[i].selectable;
      child.highlightedClass        = branches[i].highlightedClass;
      child.loadAction              = branches[i].loadAction;
      child.loadActionParams        = branches[i].loadActionParams;

      // Select Action is passed as a string reference to the target function
      // if it not contained in the parent it will create errors
      if (  branches[i].selectAction ) {
       if ( typeof eval( branches[i].selectAction) == "function")
          child.selectAction = eval( branches[i].selectAction);
      } else {
        child.selectAction            = parentBranch.selectAction;
      }

      // CHECKBOXES 
      // Checkbox Action is passed as a string reference to the targeted function
      // if it not contained in the parent it will create errors
      if ( branches[i].checkboxAction ) {
       if ( typeof eval( branches[i].checkboxAction) == "function")
           child.checkboxAction =  eval( branches[i].checkboxAction);
      } else {
        child.checkboxAction           = parentBranch.checkboxAction;
      }

      // some branches may want to disable the checkbox on there row
      if ( typeof   branches[i].checkboxes == "boolean" )
        child.checkboxes = branches[i].checkboxes; 

      // Used for the class references of the <TD> cells using the dotted images
      child.isFirst                   = ( i == 0 )?                 true : false;
      child.isLast                    = ( i == branches.length-1 )? true : false;

      if ( branches[i].mappedProperties ) 
      {
        for ( var j = 0 ; j < branches[i].mappedProperties.length ; j++ ) 
        {
          if ( branches[i].mappedProperties[j] ) 
          {
            if ( branches[i].mappedProperties[j].indexOf(";") > -1 ) 
            {
              temp = branches[i].mappedProperties[j].split(";");
              if ( temp[0] != "" )
                eval( "child."+ temp[0]+ "= \""+ temp[1] +"\"" );
                //alert ( eval( "child."+ temp[0]) );
            }
          }
        }
      }

      // SHOW CHILD BRANCH
      child.display();

    } // end for each branch loop 
    parentBranch = null;
  } // end if
  ContinueOpeningToPath(tree );
}

var branchItemsToOpenList = new Array();

function BranchOpenToPath( tree, ancestors ){

  branchItemsToOpenList         = ancestors;
  if ( tree ) {
    var branch                  = tree.getBranchById(tree.id+"_"+ancestors[0]);
    //alert ( branchItemsToOpenList.length );
    if ( branch ){
     if (  branchItemsToOpenList.length > 1  ) {
        branch.open();
        // if ( tree.highlightedBranch) tree.highlightedBranch.highlight();
        // branch.highlight();
        //      ContinueOpeningToPath(tree );
      } else {
        if ( tree.highlightedBranch) tree.highlightedBranch.highlight();
        branch.highlight();
        // alert(  branchItemsToOpenList[0] +"\n\n length" +  branchItemsToOpenList.length  );
      }
    }
  }
}
function ContinueOpeningToPath(tree ){
    //***  trim first item off array and repeat till all items are gone
  
      if ( branchItemsToOpenList ) {
        if ( branchItemsToOpenList.length > 1 ) {
          tempArray = new Array();
          for ( i = 1 ; i <  branchItemsToOpenList.length ; i++ ) {
            tempArray[i-1]      = branchItemsToOpenList[i];
          }
          branchItemsToOpenList = tempArray;
          BranchOpenToPath (tree, branchItemsToOpenList ) ;
        } else { 
          branchItemsToOpenList = new Array(); 
        } 
      }
      return;
}



/*************************************************************************
 *
 * Utility and convenience methods
 *
 ************************************************************************/
function findTreeById(str){
  
  if ( typeof (  eval ( str ) ) == "object" ) {
    return (  eval ( str ) );
  }
  return false;
}

function findBranchAncestors( tree, branchId, ancestors ) 
{
    if ( ancestors == null ) 
       var ancestors = new Array();
    var branch = tree.getBranchById(branchId);
    if ( branch ) {
      ancestors[ancestors.length] = branch;
      if ( branch.parent ) {
         findBranchAncestors( tree, branch.parent.id, ancestors )
      }
    }
    return ancestors ;  
}
function findBranchDescendants ( tree, branchId ) 
{
    var children= new Array();
    var i = 0 ;   
    while (  i  <  tree.branches.length  ) {
      var branch =  tree.branches[i]; 
      if ( branch.parent &&  branch.parent.id != null  ){
        var ancestors = findBranchAncestors( tree, branch.parent.id , null );
        for ( p = 0 ; p < ancestors.length ;p++ ) {
          if ( ancestors[p].id == branchId ) children[children.length] = branch ;
        }
      }
      i++;
    }
    return children;
}
function findBranchChildren( tree, branchId ) 
{
     var children= new Array();
     for ( var i = 0 ; i < tree.branches.length ; i++ ) {
       if ( tree.branches[i].parent ) {
         if ( tree.branches[i].parent.id ==  branchId ){
           children[children.length] = tree.branches[i];
         }
       }
     }
     return children ;  
}
function getBranchLevel( tree, branchId ) {

  if ( branchId == tree.id ) return 0;
  if ( branchId == null ) return -1 ;
  var levelArr =  findBranchAncestors( tree, branchId, null );
  if ( levelArr ) {
    return levelArr.length;
  } else {
    return -1;
  }
}

/*************************************************************************
 *
 * Check box Object and Methods
 *
 ************************************************************************/


function CheckBox( branch, tree, disabled , checked ) {


  this.control                            = "";
  this.checked                            = checked ;
  this.disabled                           = disabled ;

  this.branch                             = branch;
  this.tree                               = tree;
  this.disable                            = CheckboxDisable;
  this.enable                             = CheckboxEnable;
  this.onclick                            = CheckboxClick;
  this.check                              = CheckboxCheck;
  this.uncheck                            = CheckboxUncheck;

  this.display                            = CheckboxDisplay;
  this.initState                          = ( tree.checkboxInitState ) ? tree.checkboxInitState : null;
}

function CheckboxClick() 
{
   var box = this.checkbox;
   
   if ( this.checkbox.checked ) {
     this.checkbox.uncheck();
   } else {
     this.checkbox.check();
   }
   CheckboxApplyBehavior(box);
   if  ( typeof box.tree.checkboxOnclick == 'function' ) {
     box.tree.checkboxOnclick(box);
   }
}

function CheckboxDisable()
{
       var box = this;
       box.disabled                = true;
       box.control.disabled        = box.disabled;
}
function CheckboxEnable() 
{
       var box = this;
       box.disabled                = false;
       box.control.disabled        = box.disabled;
}
function CheckboxCheck() 
{
       var box = this;
       box.checked                 = true;
       box.control.defaultChecked  = box.checked;
}
function CheckboxUncheck() 
{
       var box = this;
       box.checked                = false;
       box.control.checked = box.checked;
}

function CheckboxDisplay() 
{
       var box = this;
       var checkboxInput                       = document.createElement("input");
       checkboxInput.type                      = "checkbox";
       checkboxInput.className                 = "dTree_tree_checkbox";
       checkboxInput.checkbox                  = box;
       checkboxInput.onclick                   = box.onclick;
       checkboxInput.disabled                  = box.disabled;
       checkboxInput.defaultChecked            = box.checked;
       box.control                             = checkboxInput;
       return box.control;
}

function isAncestorBranchSelected(box)
{
  var aParentIsSelected = false;
  var branchList =  findBranchAncestors( box.tree, box.branch.id, null );
  if ( branchList ) {
    for ( i = 0 ; i < branchList.length ; i++ ) {
      if ( branchList[i].checkbox.control.checked ) 
           aParentIsSelected = true;
    }
  }
  return aParentIsSelected;
}


function CheckboxStateInit()
{
   var box = this;
   inheritP   = hasCheckBehavior(box.tree, "inheritParent" );
   inheritA   = hasCheckBehavior(box.tree, "inheritAncestor" );
   ccDisable  = hasCheckBehavior(box.tree, "checkDisable" );
   ccMimic    = hasCheckBehavior(box.tree, "checkMimic"   );
   var modelNode = null;
   if ( box.branch.parent ){
     var ancestors = findBranchAncestors( box.tree, box.branch.parent.id , null );
     for ( p = 0 ; p < ancestors.length ;p++ ) {
       if ( p == 0 && inheritP && ( typeof  ancestors[p].checkbox.control == "object" )) {
         modelNode = ancestors[p];
       }
       if ( inheritA && ( typeof  ancestors[p].checkbox.control ==  "object" )) {
         modelNode = ancestors[p];
         break;
       }
     }
     if ( modelNode != null ) {
        if ( modelNode.checkbox.checked ) {
          if (ccMimic)    box.check();
          if (ccDisable ) box.disable();
        }
        if ( modelNode.checkbox.disabled ){
          if ( ccDisable ) box.disable();
        }
     }
   }
}
function CheckboxApplyBehavior(box)
{
   ccDisable    = hasCheckBehavior(box.tree, "checkDisable" );
   ccEnable     = hasCheckBehavior(box.tree, "checkEnable"  );
   ccMimic      = hasCheckBehavior(box.tree, "checkMimic"   );
   ccInvert     = hasCheckBehavior(box.tree, "checkInvert"  );
   uccDisable   = hasCheckBehavior(box.tree, "uncheckDisable" );
   uccEnable    = hasCheckBehavior(box.tree, "uncheckEnable"  );
   uccMimic     = hasCheckBehavior(box.tree, "uncheckMimic"   );
   uccInvert    = hasCheckBehavior(box.tree, "uncheckInvert"  );
   var children =  findBranchDescendants( box.tree, box.branch.id );
   if ( box.checked ) {
     for ( i = 0 ; i < children.length ; i++ ){
        if (ccMimic   )   children[i].checkbox.check() ;
        if (ccInvert  )   children[i].checkbox.uncheck() ;
        if (ccDisable )   children[i].checkbox.disable() ;
        if (ccEnable  )   children[i].checkbox.enable() ;
      }
   } else {
      for ( i = 0 ; i < children.length ; i++ ){
        if (uccMimic  )  children[i].checkbox.uncheck() ;
        if (uccInvert )  children[i].checkbox.check() ;
        if (uccEnable )  children[i].checkbox.enable() ;
        if (uccDisable)  children[i].checkbox.disable() ;
      }
   }
}
function hasCheckBehavior(tree, str)
{
  if ( tree.checkboxBehaviors == null ) return false;
  returnValue = false;
  for ( i=0 ; i < tree.checkboxBehaviors.length ; i++ ) {
    if ( tree.checkboxBehaviors[i] == str )  returnValue = true;   
  }
  return returnValue;
}
