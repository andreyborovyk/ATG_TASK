<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ taglib prefix="dspel"     uri="http://www.atg.com/taglibs/daf/dspjspTaglib1_0" %>
<%@ taglib prefix="caf" uri="http://www.atg.com/taglibs/caf" %>

<dspel:importbean var="CAFUIConfig" bean="/atg/web/ui/UIConfiguration"/>
<c:set var="CAFUIConfig" value="${CAFUIConfig}" scope="request"/>

<html>
  <head>

    <title>Test Page for  Javascript tree</title>

    <script type="text/javascript" src='<c:out value="${CAFUIConfig.contextRoot}/scripts/atg-ui_common.js"/>' ></script>
    <link   type="text/css"        href="/CAF/css/dynamicTree.css"    rel="stylesheet" />

    
     
  </head>
  <body onload="ATGAction(new Array('bodyOnLoad'));"
        onunload="ATGAction(new Array('bodyOnUnLoad'));" style="margin:0px;font-family:verdana,arial,geneva;">

 <div style="width:100%;height:40;position:absolute;top:0;
                 left:0px;background-color:#dedede;boder:solid 1px #999999;
                 font-size:10pt;color:#333333;padding:10px;
                 border-bottom:2px solid #999999;font-family:verdana,arial,geneva;">
 This page is a test page for the tree component in CAF and uses the treeInstance and treeBranch tags. There are three trees one of which houses two branches.</div> 


<div id="tree" style="width:380;left:10px;position:absolute;top:60;">

     <script type="text/javascript" > 
      function testSelect(){
        if ( this.googleStr ) 
              document.getElementById("content").src = "http://www.google.com/search?hl=en&q="+this.googleStr;
        else 
               document.getElementById("content").src = "http://www.google.com/";

        renderObjectInfo(this);
        return false;

      } 
      function renderObjectInfo(obj,str){
        str = ( typeof str == "undefined" ) ? "" : str;
        str = str + "This does not include properties that are Functions or Objects, just thier references\n";
        for ( var i in obj ) {
          tab = ( i.length > 24 ) ? " " : ( i.length < 16 ) ? "\t\t" : "\t";
          if ( typeof ( obj[i] ) != "object" && typeof ( obj[i] ) != "function" ) {
            str = str + "\n"+i+tab+ obj[i];
          } else {
            str = str + "\n"+i+  tab +"Object type = " + typeof ( obj[i] ) ;
          }

        }
        document.getElementById("textB").value = str;
      }
      function proccessDragDropEvent(src,dest) {
         str = "You have dragged an item of type "+ src.type + " with an id of " + src.sourceId + " from tree "+src.tree.id;
         str = str + "\n\n and dropped it on an item of type "+ dest.type + " with an id of " + dest.sourceId + " from tree "+dest.tree.id;
         document.getElementById("textB").value = str;
         return;
      }


      // this function will show the code
      function show() {
          subItems = new Array ( "helper" ,"tag" ,"many" , "script" );
          for ( var i = 0; i < subItems.length ; i++ ) {
            document.getElementById( subItems[i] ).style.display    = 'none';
          }
          document.getElementById('treeTag').style.display   = 'inline';
          document.getElementById( this.selectActionParam /* note */ ).style.display   = 'inline';
      }

      function checkerClick( box ) {
            // if a treeInstance tag declares 
            //     checkboxOnclick="checkerClick" , a method such as this can be called after the
            // tree.js handles it's internal handling of the click
             renderObjectInfo( box, "You checked a checkbox that is part of the branch object "+ box.branch.id + 
                                    "\n\nCheckbox info:\n");

      }
      function getCheckedItems(treeRef){
        if ( findTreeById(treeRef) ){
           var tree =  findTreeById(treeRef);
           var checkboxes          = 0; 
           var checkboxesChecked   = 0; 
           var checkboxesEnabled   = 0;
           var checkedAndEnabled   = 0
           var checkedEnabledId    = new Array();
           for ( i =  0 ; i < tree.branches.length ; i++){
             if (  tree.branches[i].checkboxes  ) {
                checkboxes++;
                 if (  tree.branches[i].checkbox.checked ) checkboxesChecked++;
                 if ( ! tree.branches[i].checkbox.disabled ) checkboxesEnabled++;
                 if (  tree.branches[i].checkbox.checked &&( ! tree.branches[i].checkbox.disabled) ) {
                   checkedEnabledId[checkedEnabledId.length] = tree.branches[i];
                   checkedAndEnabled++;
                 }

             }
           }

          if (confirm ( " total checkboxes = " + checkboxes + "\n" +
            " total checked = " + checkboxesChecked + "\n" +
            " total enabled = " + checkboxesEnabled + "\n" +
            " enabled and checked = " + checkedAndEnabled + "\n\nWould you "+
            " like to see the list of branch id of items that are checked and enabled ?" ) ){
              var idList = "";
              for ( i = 0 ; i < checkedEnabledId.length ; i++ ) {
                idList = idList + checkedEnabledId[i].id + "\n";
              }
              renderObjectInfo( checkedEnabledId, idList+"\n\n");
           }

        }
      }
    </script>

   <br />
   <div id="fristTree" style="width:300;height:180;overflow:auto;">


     <caf:treeInstance 
          treeId="tree_1" 
          cssDots="true"
          checkboxes="true" 
          checkboxOnclickFunction="checkerClick"
          checkboxBehaviors="checkDisable,uncheckEnable,inheritAncestor,checkMimic,uncheckMimic" 
       >
       <caf:treeBranch
            branchId="1"
            type="branch"
            label="Check boxes and dots"
            labelClassName="dTree_node"
            labelHighlightClassName="dTree_highlighted"
            selectable="true"
            selectActionFunction="testSelect"
            hasChildren="true"
            loadActionUrl="/CAF/test/treeChildren.jsp"
            iconOpened="/CAF/images/tree/folderActionMinus.gif"
            iconClosed="/CAF/images/tree/folderActionPlus.gif"
            first="true"
            last="false"
            canDrag="true"
            canDrop="true"
            initStateOpen="true"
            dropTypes="one;two;genericDataType"
            dynamicProperties="googleStr=ATG solutions"
        >
       </caf:treeBranch> 
       <caf:treeBranch
            branchId="12"
            type="branch"
            label="Check boxes and dots"
            labelClassName="dTree_node"
            labelHighlightClassName="dTree_highlighted"
            selectable="true"
            selectActionFunction="testSelect"
            hasChildren="true"
            loadActionUrl="/CAF/test/treeChildren.jsp"
            iconOpened="/CAF/images/tree/folderActionMinus.gif"
            iconClosed="/CAF/images/tree/folderActionPlus.gif"
            first="false"
            last="true"
            canDrag="true"
            canDrop="true"
            initStateOpen="closed"
            dropTypes="one;two;genericDataType"
            dynamicProperties="googleStr=ATG solutions"
        >
        </caf:treeBranch> 
 
     </caf:treeInstance>
   </div>

     <caf:treeInstance 
          treeId="tree_2"
          cssDotsDelayed="true" >
 
       <caf:treeBranch
            branchId="1"
            type="branch"
            label="Checkboxes disabled"
            labelClassName="dTree_node"
            labelHighlightClassName="dTree_highlighted"
            selectable="true"
            selectActionFunction="testSelect"
            hasChildren="true"
            loadActionUrl="/CAF/test/treeChildren.jsp"

            iconOpened="/CAF/images/tree/folderActionMinus.gif"
            iconClosed="/CAF/images/tree/folderActionPlus.gif"
            first="false"
            last="true"
        />
  

 
     </caf:treeInstance>

     <caf:treeInstance 
          treeId="tree_3" 
          checkboxes="true" 
          checkboxBehaviors="checkDisable,uncheckEnable,inheritAncestor" 
           >
 
        <caf:treeBranch
            branchId="3"
            type="branch"
            label="Show me this tag"
            labelClassName="dTree_node"
            labelHighlightClassName="dTree_highlighted"
            selectable="true"
            selectActionFunction="show"
            hasChildren="true"
            loadActionUrl="/CAF/test/treeChildren.jsp"
            iconOpened="/CAF/images/tree/folderActionMinus.gif"
            iconClosed="/CAF/images/tree/folderActionPlus.gif"
            first="false"
            last="true"
            checkboxes="true"
            dynamicProperties="selectActionParam=tag"
        >
       </caf:treeBranch>

       <caf:treeBranch
            branchId="1"
            type="branch"
            label="Show me the helper"
            labelClassName="dTree_node"
            labelHighlightClassName="dTree_highlighted"
            selectable="true"
            selectActionFunction="show"
            hasChildren="true"
            loadActionUrl="/CAF/test/treeChildren.jsp"
            iconOpened="/CAF/images/tree/folderActionMinus.gif"
            iconClosed="/CAF/images/tree/folderActionPlus.gif"
            first="true"
            last="true"
            checkboxes="true"
            dynamicProperties="selectActionParam=helper"
        >
        </caf:treeBranch>
 
       <caf:treeBranch
            branchId="2"
            type="branch"
            label="What about the scripts ?"
            labelClassName="dTree_node"
            labelHighlightClassName="dTree_highlighted"
            selectable="true"
            selectActionFunction="show"
            hasChildren="false"
            loadActionUrl="/CAF/test/treeChildren.jsp"
            iconOpened="/CAF/images/tree/folderActionMinus.gif"
            iconClosed="/CAF/images/tree/folderActionPlus.gif"
            first="true"
            last="true"
            dynamicProperties="selectActionParam=script"
        >
        </caf:treeBranch>

 
     </caf:treeInstance>

  <br><br>
  test links<br>
  <a href="javascript:getCheckedItems('tree_1');" style="font-size:10pt;" >Checked items in first tree</a>

</div>

<div id="tree" style="width:380;left:400px;position:absolute;top:60px;">

  Content frame:<br/>
  <iframe id="content" src="about:blank" height"200" width="500" ></iframe>

  Object inspection:<br>
  <textarea cols="80" rows="10" id="textB" style="font-size:8pt;font-family:verdana,arial;"></textarea>

</div>


<!-- CODE SAMPLES -->
<div  id="treeTag" style="left:100px;position:absolute;top:10px;border:solid 1px #996633;
                          background-color:#ededed;font-size:10pt;color:#333333;
                          padding:10px;display:none;
                          filter:progid:DXImageTransform.Microsoft.Shadow(color='#000000', Direction=135, Strength=3)" >
  
 <div style="width:100%;
             background-color:#cccccc;border:solid 1px #999999;
             font-size:10pt;color:#333333;padding:4px;
             font-family:verdana,arial,geneva;text-align:right;"
      onclick="document.getElementById('treeTag').style.display='none'">
    <a href="#" onclick="document.getElementById('treeTag').style.display='none'">close</a>
 </div> 

  <div id="tag">
   <pre>
     &lt;caf:treeInstance 
          treeId="tree_4" 
          cssDots="true"
          checkboxes="true" 
          checkboxBehaviors="checkDisable,uncheckEnable,inheritAncestor" 
           &gt;
       &lt;caf:treeBranch
            branchId="1"
            type="branch"
            label="Show me the tag !"
            labelClassName="dTree_node"
            labelHighlightClassName="dTree_highlighted"
            selectable="true"
            selectActionFunction="show"
            hasChildren="true"
            loadActionUrl="/CAF/test/treeChildren.jsp"
            iconOpened="/CAF/images/tree/folderActionMinus.gif"
            iconClosed="/CAF/images/tree/folderActionPlus.gif"
            first="true"
            last="true"
            loadActionParams="kbId=kb10001"
            dynamicProperties="googleStr=ATG solutions;selectActionParam=tag"
        /&gt;

      &lt;/caf:treeBranch&gt;

      &lt;!-- there can be many treeBranches tags used of various types contained 
             in this tree, only one is shown here --&gt;

   &lt;/caf:treeInstance&gt;
  </pre> 
  </div>

  <div id="helper">
   <pre>

     &lt;caf:branchChildren
          parentId="${param.parentId}" 
          treeId="${param.treeId}"&gt;

            &lt;caf:branchChild
               branchId="${param.sourceId}_0"
               type="generic"
               label="Label A"
               labelClassName="dTree_node"
               labelHighlightClassName="dTree_highlighted"
               selectable="true"
               selectActionFunction="testSelect"
               hasChildren="true"
               checkboxes="true"
               canDrag="true"
               canDrop="true"
               dropTypes="one;two;genericDataType"
               loadActionUrl="/CAF/test/treeChildren.jsp"
               iconOpened="/CAF/images/tree/arrow-preview-opened.gif"
               iconClosed="/CAF/images/tree/arrow-preview-closed.gif"
               loadActionParams="MARK=asd${param.MARK};ATG=asd;"
               dynamicProperties="googleStr=Mapped Properties"
             /&gt;


     &lt;/caf:branchChildren>
   </pre>
  </div>


  <div id="many">
   <pre>
    &lt;caf:branchChildren
          parentId="${param.parentId}" 
          treeId="${param.treeId}"&gt;
          &lt;c:forTokens var="loopVarA" items="0,1,2,3,4,5,6,7,8,9" delims=","&gt;
          &lt;c:forTokens var="loopVarB" items="0,1,2,3,4,5,6,7,8,9" delims=","&gt;
            &lt;caf:branchChild
               branchId="${param.sourceId}_${loopVarA}${loopVarB}"
               type="1OF100"
               label="Label ${loopVarA}${loopVarB}"
               labelClassName="dTree_node"
               labelHighlightClassName="dTree_highlighted"
               selectable="true"
               selectActionFunction="show"
               hasChildren="true"
               checkboxes="true"
               loadActionUrl="/CAF/test/treeChildren.jsp"
               iconOpened="/CAF/images/tree/arrow-preview-opened.gif"
               iconClosed="/CAF/images/tree/arrow-preview-closed.gif"
               canDrag="true"
               canDrop="true"
               dropTypes="one;two"
               dynamicProperties="selectActionParam=many"
             />
        &lt;/c:forTokens&gt;
       &lt;/c:forTokens&gt;
     &lt;/caf:branchChildren&gt;

  </pre> 
  </div>

  <div id="script">
   <pre>
function testSelect(){
   if ( this.googleStr ) 
        document.getElementById("content").src = "http://www.google.com/search?hl=en&q="+this.googleStr;
   else 
        document.getElementById("content").src = "http://www.google.com/";
   renderObjectInfo(this);
   return false;
} 
function renderObjectInfo(obj){
    str = "This does not include properties that are Functions or Objects, just thier references\n\n";
    for ( var i in obj ) {
       tab = ( i.length > 24 ) ? " " : ( i.length < 16 ) ? "\t\t" : "\t";
       if ( typeof ( obj[i] ) != "object" && typeof ( obj[i] ) != "function" ) {
         str = str + "\n"+i+tab+ obj[i];
       } else {
         str = str + "\n"+i+  tab +"Object type = " + typeof ( obj[i] ) ;
       }
    }
   document.getElementById("textB").value = str;
}
function proccessDragDropEvent(src,dest) {
   str = " You have attempted to Drag an item of type "+ src.type + " with an id of " + src.sourceId ;
   str = str + "\n\n and dropped it on an item of type "+ dest.type + " with an id of " + dest.sourceId ;
   document.getElementById("textB").value = str;
   return;
}

function show() {
   subItems = new Array ( "helper" ,"tag" ,"many" , "script" );
   for ( var i = 0; i < subItems.length ; i++ ) {
      document.getElementById( subItems[i] ).style.display    = 'none';
   }
   document.getElementById('treeTag').style.display   = 'inline';
   document.getElementById( this.selectActionParam /* note */ ).style.display   = 'inline';
}

  </pre> 
  </div>


 </div>
<script type="text/javascript">
 // alert ( typeof ( script_tree));
  if ( typeof  (script_tree) == "object" ) script_tree.debug = 0;

</script>




  </body>
</html>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/tree.jsp#2 $$Change: 651448 $--%>
