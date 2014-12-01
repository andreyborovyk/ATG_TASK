<%--
  Facet Sets Dojo Tree init JSP

  @author: Anderi_Tsishkouski
  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/tree_facet_sets.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

  <!-- Menu init -->
  <%-- Additional menu item attributes:
  redirectUrl - url to redirect right pane after this menu item selecting.
  formHandler - method handler class witch method will be invoked.
  handleMethod - handler method to invoke.
  popUpUrl - url of popup to redirect to after this menu item selecting (now used to open confirm delete operations).
  renameFieldName - name of property of form handler. This property will be set before rename operation (also used in create operations).
  handleCutPasteMethod - name of transfer handler method.
  handleCopyPasteMethod - name of copy handler method.
  labelNewItem - new name of item that will appear in rename input before create operation. (pkouzmit)
  --%>
  <c:set var="contextPath" value="${pageContext.request.contextPath}"/>
  <div dojoType="atg.searchadmin.tree.DojoTreeMenu" id="facetSetsTreeContextMenu" style="display:none">
    <div dojoType="dijit.MenuItem" class="context_menu_rootFacetSetNode" id="treeContextMenuNewFacetSet"
         redirectUrl="${contextPath}${facetPath}/facetset_new.jsp">
         <span><fmt:message key='facetsets.dojo.menu.new_facet_set'/></span></div>
    <div dojoType="dijit.MenuItem" class="context_menu_rootFacetSetNode" id="treeContextMenuImportFacetSet"
         redirectUrl="${contextPath}${facetPath}/facetset_import.jsp">
         <span><fmt:message key="facetsets.dojo.menu.import_facet_set"/></span></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facetSet" id="treeContextMenuCopyToNewFacetSet"
         formHandler="/atg/searchadmin/workbenchui/formhandlers/RefConfigFormHandler"
         handleMethod="handleCopy"
         handlerIdField="refConfigId"
         action="copyToNew">
         <span><fmt:message key="facetsets.dojo.menu.copy_to_new"/></span></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facetSet" id="treeContextMenuExportFacetSet"
         redirectUrl="${contextPath}${facetPath}/facetset_export.jsp?facetSetId=">
         <span><fmt:message key="facetsets.dojo.menu.export"/></span></div>
    <div dojoType="dijit.MenuSeparator" class="context_menu_facetSet"></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facetSet" id="treeContextMenuDeleteFacetSet"
         popUpUrl="${contextPath}${facetPath}/facetset_delete_popup.jsp?facetSetId=">
         <span><fmt:message key="facetsets.dojo.menu.delete"/></span></div>
     <div dojoType="dijit.MenuItem" class="context_menu_facetSet" id="treeContextMenuRenameFacetSet"
         formHandler="/atg/searchadmin/workbenchui/formhandlers/ManageFacetSetFormHandler"
         handleMethod="handleRename"
         handlerIdField="facetSetId"
         renameFieldName="facetSetName"
         action="rename">
         <span><fmt:message key="facetsets.dojo.menu.rename"/></span></div>

    <div dojoType="dijit.MenuItem" class="context_menu_facet" id="treeContextMenuNewFacet"
         redirectUrl="${contextPath}${facetPath}/facet_new.jsp?baseFacetId=">
         <span><fmt:message key='facetsets.dojo.label.new_facet_name'/></span></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facet" id="treeContextMenuNewChildFacet"
         redirectUrl="${contextPath}${facetPath}/facet_new.jsp?baseFacetId=">
         <span><fmt:message key='facetsets.dojo.menu.new_child_facet'/></span></div>
    <div dojoType="dijit.MenuSeparator" class="context_menu_facet"></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facet" id="treeContextMenuCutFacet"
         action="cut">
         <span><fmt:message key="facetsets.dojo.menu.cut"/></span></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facet" id="treeContextMenuCopyFacet"
         action="copy">
         <span><fmt:message key="facetsets.dojo.menu.copy"/></span></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facet" id="treeContextMenuPasteFacet"
         formHandler="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"
         handlerIdField="facetId"
         handlerNameField="facetName"
         handleCutPasteMethod="handleTransferFacet"
         handleCopyPasteMethod="handleCopyFacet" 
         action="paste" disabled="true">
         <span><fmt:message key="facetsets.dojo.menu.paste"/></span></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facet" id="treeContextMenuPasteChildFacet"
         formHandler="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"
         handlerIdField="facetId"
         handlerNameField="facetName"
         handleCutPasteMethod="handleTransferFacet"
         handleCopyPasteMethod="handleCopyFacet"
         action="pasteChild" disabled="true">
         <span><fmt:message key="facetsets.dojo.menu.paste_as_child"/></span></div>
    <div dojoType="dijit.MenuSeparator" class="context_menu_facet"></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facet" id="treeContextMenuDeleteFacet"
         popUpUrl="${contextPath}${facetPath}/facet_delete_popup.jsp?facetId=">
         <span><fmt:message key="facetsets.dojo.menu.delete"/></span></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facet" id="treeContextMenuRenameFacet"
         formHandler="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"
         handleMethod="handleRename"
         handlerIdField="facetId"
         renameFieldName="facetName"
         action="rename">
         <span><fmt:message key="facetsets.dojo.menu.rename"/></span></div>
    <div dojoType="dijit.MenuSeparator" class="context_menu_facet"></div>
    <div dojoType="dijit.MenuItem" class="context_menu_facet" id="treeContextMenuCancelSelectionFacetSet"
         action="cancelSelection" disabled="true">
         <span><fmt:message key="facetsets.dojo.menu.cancel_selection"/></span></div>
  </div>

<div dojoType="atg.searchadmin.tree.LazyLoadStore"
           jsId="facet_set_store"
           url="tree_controller.dojo?_dyncharset=UTF-8"
           label="titleText"
           identifier="id">
      </div>
<div dojoType="atg.searchadmin.tree.DojoTree"
           id="facet_set_dojo_Tree"
           store="facet_set_store"
           class="atg"
           successUrl="${facetPath}/facet_json_response.jsp"
           errorUrl="${facetPath}/facet_json_response.jsp"
           expandUrl="${facetPath}/facet_sets_nodes.jsp"
           moveFormHandler="/atg/searchadmin/workbenchui/formhandlers/FacetFormHandler"
           moveHandleMethod="handleTransferFacet"
           moveIdField="facetId"
           moveNameField="facetName"
           persist="false"
           menuId="facetSetsTreeContextMenu"
           dndController="atg.searchadmin.tree.DojoTreeDnDSource"
           renameDiv="treeNodeRenameDiv">
           <script type="dojo/connect">
                var menu = dijit.byId("facetSetsTreeContextMenu");
                menu.bindDomNode(this.domNode);
           </script>
</div>
<div id="facetSetTreeTooltip" dojoType="atg.searchadmin.tree.DojoTreeTooltip" connectId="facet_set_dojo_Tree" class="ea" style="display: none;"/>


<script type="text/javascript">
  //disabledMenuIds - ids of menu items which "disabled" status will be change after "copy", "cut", "paste" etc. of current node operations
  //actionsHided - as all menus points of nodes of different types are actual one menu, It's necessary to hide menu items that are not related to given node.
  //nodeUrl - rightPane will be redirected to this url after clicking to given tree node
  //childIconSrc - icon of tree node
  //actionsDisabled - menu or DnD operations that not allowed to given node
  //moveFormHandler - form handler class that will carry out DnD operation. (can be set globaly - for all tree. see tree definition comments)
  //moveHandleMethod - form handler method name that will carry out DnD operation. (can be set globaly - for all tree. see tree definition comments)
  //permitedTypesToPaste - define what types of tree nodes possible to paste to given node.(pkouzmit)
  nodeInfo["rootFacetSetNode"] = {nodeUrl : "${pageContext.request.contextPath}${facetPath}/facetsets_general.jsp?",
    actionsHided : ["facetSet", "facet"], actionsDisabled : ["addChild", "move"]};
  
  nodeInfo["adaptors"] = {nodeUrl : "${pageContext.request.contextPath}${facetPath}/facetset_adaptors.jsp?",
    actionsHided : ["rootFacetSetNode", "facetSet", "facet"], actionsDisabled : ["addChild", "move"]};
  
  nodeInfo["rootIndexedFacetSetNode"] = {nodeUrl : "${pageContext.request.contextPath}${facetPath}/facetsets_general_readonly.jsp?",
    actionsHided : ["rootFacetSetNode", "facetSet", "facet"], actionsDisabled : ["addChild", "move"]};
  
  nodeInfo["facetSet"] = {nodeUrl : "${pageContext.request.contextPath}${facetPath}/facetset.jsp?facetSetId=",
    actionsHided : ["rootFacetSetNode", "facet"],  permitedTypesToPaste : ["facet"], 
    actionsDisabled : ["move"], rootParentNodeId: "rootFacetSetNode"};

  nodeInfo["indexedFacetSet"] = {nodeUrl : "${pageContext.request.contextPath}${facetPath}/facetset_readonly.jsp?adapterItemId=",
    actionsHided : ["facetSet", "rootFacetSetNode", "facet"], actionsDisabled : ["move"], 
    rootParentNodeId: "rootIndexedFacetSetNode"};

  nodeInfo["facet"] = {nodeUrl : "${pageContext.request.contextPath}${facetPath}/facet.jsp?facetId=",
    actionsHided : ["rootFacetSetNode", "facetSet"], permitedTypesToPaste : ["facet"], actionsDisabled : [], 
    disabledMenuIds: ["treeContextMenuPasteFacet","treeContextMenuPasteChildFacet","treeContextMenuCancelSelectionFacetSet"]};

  nodeInfo["indexedFacet"] = {nodeUrl : "${pageContext.request.contextPath}${facetPath}/facet_readonly.jsp?adapterItemId=",
    actionsHided : ["facetSet", "rootFacetSetNode", "facet"], permitedTypesToPaste : ["facet"], 
    actionsDisabled : ["move"], rootParentNodeId: "rootIndexedFacetSetNode"};
</script>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/facet/tree_facet_sets.jsp#2 $$Change: 651448 $--%>
