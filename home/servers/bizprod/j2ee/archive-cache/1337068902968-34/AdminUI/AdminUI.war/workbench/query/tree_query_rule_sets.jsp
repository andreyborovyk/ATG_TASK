<%--
  Query rule tree.

  @author: atsichko
  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/tree_query_rule_sets.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<%-- Menu initialization --%>
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
<div dojoType="atg.searchadmin.tree.DojoTreeMenu" id="qrTreeContextMenu" style="display:none">
<%-- Actions for Query Rule Set --%>
<div dojoType="dijit.MenuItem" class="context_menu_root_qr_set" id="treeContextMenuNewQueryRuleSet"
     redirectUrl="${contextPath}${queryPath}/queryset_new.jsp">
     <span><fmt:message key="querysets.dojo.menu.new_qr_set"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_root_qr_set" id="treeContextMenuImportQueryRuleSet"
     redirectUrl="${contextPath}${queryPath}/querysets_import.jsp">
     <span><fmt:message key="querysets.dojo.menu.import_qr_set"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_root_qr_set" id="treeContextMenuEditMacrosQueryRuleSet"
     redirectUrl="${contextPath}${queryPath}/querysets_macros.jsp">
     <span><fmt:message key="querysets.dojo.menu.edit_macros_qr_set"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_set" id="treeContextMenuCopyToNewQueryRuleSet"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QuerySetsFormHandler"
     handleMethod="handleCopyQueryRuleSet"
     handlerIdField="querySetId"
     action="copyToNew">
     <span><fmt:message key="querysets.dojo.menu.copy_to_new"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_set" id="treeContextMenuExportQueryRuleSet"
     redirectUrl="${contextPath}${queryPath}/querysets_export.jsp?querySetId=">
     <span><fmt:message key="querysets.dojo.menu.export"/></span></div>
<div dojoType="dijit.MenuSeparator" class="context_menu_qr_set"></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_set" id="treeContextMenuDeleteQueryRuleSet"
     popUpUrl="${contextPath}${queryPath}/querysets_delete_popup.jsp?querySetId=">
     <span><fmt:message key="querysets.dojo.menu.delete"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_set" id="treeContextMenuRenameQueryRuleSet"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleSetFormHandler"
     handleMethod="handleRename"
     handlerIdField="querySetId"
     renameFieldName="querySetName"
     action="rename">
     <span><fmt:message key="querysets.dojo.menu.rename"/></span></div>
<div dojoType="dijit.MenuSeparator" class="context_menu_qr_set"></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_set" id="treeContextMenuAddGroupToQueryRuleSet"
     redirectUrl="${contextPath}${queryPath}/add_new_query_rule_groups.jsp?parentId=">
     <span><fmt:message key="querysets.dojo.menu.add_groups"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_set" id="treeContextMenuPasteAsChildQueryRuleGroup"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleTreeFormHandler"
     handlerIdField="queryGroupId"
     handlerNameField="queryGroupName"
     handleCutPasteMethod="handleTransferQueryRuleGroup"
     handleCopyPasteMethod="handleCopyQueryRuleGroup" 
     action="pasteAsChild" disabled="true">
     <span><fmt:message key="querysets.dojo.menu.paste_group_as_child"/></span></div>
<%-- Actions for Query Rule Group --%>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuCopyToNewQueryRuleGroup"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleTreeFormHandler"
     handleMethod="handleCopyToNewQueryRuleGroup"
     handlerIdField="queryGroupId"
     action="copyToNew">
     <span><fmt:message key="querysets.dojo.menu.copy_to_new"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuDeleteQueryRuleGroup"
     popUpUrl="${contextPath}${queryPath}/query_rule_group_delete_popup.jsp?queryRuleGroupId=">
     <span><fmt:message key="querysets.dojo.menu.delete"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuRenameQueryRuleGroup"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleTreeFormHandler"
     handleMethod="handleRename"
     handlerIdField="queryGroupId"
     renameFieldName="queryGroupName"
     action="rename">
     <span><fmt:message key="querysets.dojo.menu.rename"/></span></div>
<div dojoType="dijit.MenuSeparator" class="context_menu_qr_group"></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuCutQueryRuleGroup"
     action="cut">
     <span><fmt:message key="querysets.dojo.menu.cut"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuCopyQueryRuleGroup"
     action="copy">
     <span><fmt:message key="querysets.dojo.menu.copy"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuPasteQueryRuleGroup"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleTreeFormHandler"
     handlerIdField="queryGroupId"
     handlerNameField="queryGroupName"
     handleCutPasteMethod="handleTransferQueryRuleGroup"
     handleCopyPasteMethod="handleCopyQueryRuleGroup" 
     action="paste" disabled="true">
     <span><fmt:message key="querysets.dojo.menu.paste"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuPasteChildQueryRuleGroup"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleTreeFormHandler"
     handlerIdField="queryGroupId"
     handlerNameField="queryGroupName"
     handleCutPasteMethod="handleTransferQueryRuleGroup"
     handleCopyPasteMethod="handleCopyQueryRuleGroup" 
     action="pasteChild" disabled="true">
     <span><fmt:message key="querysets.dojo.menu.paste_group_as_child"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuPasteChildQueryRule"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleTreeFormHandler"
     handlerIdField="queryRuleId"
     handlerNameField="queryRuleName"
     handleCutPasteMethod="handleTransferQueryRule"
     handleCopyPasteMethod="handleCopyQueryRule"
     action="pasteChild" disabled="true">
     <span><fmt:message key="querysets.dojo.menu.paste_rule_as_child"/></span></div>
<div dojoType="dijit.MenuSeparator" class="context_menu_qr_group"></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuAddGroupToQueryRuleGroup"
     redirectUrl="${contextPath}${queryPath}/add_new_query_rule_groups.jsp?parentId=">
     <span><fmt:message key="querysets.dojo.menu.add_groups"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuAddRuleToQueryGroup"
     redirectUrl="${contextPath}${queryPath}/queryrule_new.jsp?queryGroupId=">
     <span><fmt:message key="querysets.dojo.menu.add_rules"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_qr_group" id="treeContextMenuQueryRuleGroupCancelSelection"
     action="cancelSelection" disabled="true">
     <span><fmt:message key="querysets.dojo.menu.cancel_selection"/></span></div>

<%-- Actions for Query Rule --%>
<div dojoType="dijit.MenuItem" class="context_menu_queryRule" id="treeContextMenuNewQueryRule"
     labelNewItem="<fmt:message key="querysets.dojo.label.new_rule_name"/>"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleTreeFormHandler"
     handleMethod="handleCreate"
     renameFieldName="queryRuleName"
     action="new">
     <span><fmt:message key="querysets.dojo.menu.new_rule"/></span></div>
<div dojoType="dijit.MenuSeparator" class="context_menu_queryRule"></div>
<div dojoType="dijit.MenuItem" class="context_menu_queryRule" id="treeContextMenuCutQueryRule"
     action="cut">
     <span><fmt:message key="querysets.dojo.menu.rule.cut"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_queryRule" id="treeContextMenuCopyQueryRyle"
     action="copy">
     <span><fmt:message key="querysets.dojo.menu.rule.copy"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_queryRule" id="treeContextMenuPasteQueryRule"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleTreeFormHandler"
     handlerIdField="queryRuleId"
     handlerNameField="queryRuleName"
     handleCutPasteMethod="handleTransferQueryRule"
     handleCopyPasteMethod="handleCopyQueryRule" 
     action="paste" disabled="true">
     <span><fmt:message key="querysets.dojo.menu.rule.paste"/></span></div>
<div dojoType="dijit.MenuSeparator" class="context_menu_queryRule"></div>
<div dojoType="dijit.MenuItem" class="context_menu_queryRule" id="treeContextMenuDeleteQueryRule"
     popUpUrl="${contextPath}${queryPath}/query_rule_delete_popup.jsp?queryRuleId=">
     <span><fmt:message key="querysets.dojo.menu.delete"/></span></div>
<div dojoType="dijit.MenuItem" class="context_menu_queryRule" id="treeContextMenuRenameQueryRule"
     formHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleTreeFormHandler"
     handlerIdField="queryRuleId"
     handleMethod="handleUpdate"
     renameFieldName="queryRuleName"
     action="rename">
     <span><fmt:message key="querysets.dojo.menu.rename"/></span></div>
<div dojoType="dijit.MenuSeparator" class="context_menu_queryRule"></div>
<div dojoType="dijit.MenuItem" class="context_menu_queryRule" id="treeContextMenuQueryRuleCancelSelection"
     action="cancelSelection" disabled="true">
     <span><fmt:message key="querysets.dojo.menu.cancel_selection"/></span></div>
</div>

<div dojoType="atg.searchadmin.tree.LazyLoadStore"
           jsId="query_sets_store"
           url="tree_controller.dojo?_dyncharset=UTF-8"
           label="titleText"
           identifier="id">
      </div>
<div dojoType="atg.searchadmin.tree.DojoTree"
           id="query_sets_dojo_Tree"
           store="query_sets_store"
           class="atg"
           successUrl="${queryPath}/qr_json_response.jsp"
           errorUrl="${queryPath}/qr_json_response.jsp"
           expandUrl="${queryPath}/qr_nodes.jsp"
           moveFormHandler="/atg/searchadmin/workbenchui/formhandlers/QueryRuleTreeFormHandler"
           moveHandleMethod="handleTransferQueryRuleGroup"
           moveIdField="queryGroupId"
           moveNameField="queryGroupName"
           persist="false"
           menuId="qrTreeContextMenu"
           dndController="atg.searchadmin.tree.DojoTreeDnDSource"
           renameDiv="treeNodeRenameDiv">
           <script type="dojo/connect">
                var menu = dijit.byId("qrTreeContextMenu");
                menu.bindDomNode(this.domNode);
           </script>
</div>
<div id=queryRuleTreeTooltip" dojoType="atg.searchadmin.tree.DojoTreeTooltip" connectId="query_sets_dojo_Tree" class="ea" style="display: none;"/>

<script type="text/javascript">
  //disabledMenuIds - ids of menu items which "disabled" status will be change after "copy", "cut", "paste" etc. of current node operations
  //actionsHided - as all menus points of nodes of different types are actual one menu, It's necessary to hide menu items that are not related to given node.
  //nodeUrl - rightPane will be redirected to this url after clicking to given tree node
  //childIconSrc - icon of tree node
  //actionsDisabled - menu or DnD operations that not allowed to given node
  //permitedTypesToPaste - define what types of tree nodes possible to paste to given node.
  //moveFormHandler - form handler class that will carry out DnD operation. (can be set globaly - for all tree. see tree definition comments)
  //moveHandleMethod - form handler method name that will carry out DnD operation. (can be set globaly - for all tree. see tree definition comments)  (pkouzmit)
  nodeInfo["rootQrNode"] = {nodeUrl: "${pageContext.request.contextPath}${queryPath}/querysets_general.jsp?",
    actionsHided: ["qr_set", "qr_group", "queryRule"], actionsDisabled: ["move", "addChild"]};
  
  nodeInfo["queryRuleSet"] = {nodeUrl: "${pageContext.request.contextPath}${queryPath}/queryset.jsp?querySetId=", 
    actionsHided: ["root_qr_set","qr_group", "queryRule"], actionsDisabled: ["move"],
    permitedTypesToPaste: ["queryRuleGroup"], rootParentNodeId: "rootQrNode"};

  nodeInfo["queryRuleGroup"] = {nodeUrl: "${pageContext.request.contextPath}${queryPath}/query_rule_group.jsp?queryGroupId=",
    actionsHided: ["root_qr_set","qr_set", "queryRule"], actionsDisabled: [],
    permitedTypesToPaste: ["queryRuleGroup", "queryRule"],
    disabledMenuIds: ["treeContextMenuPasteAsChildQueryRuleGroup", "treeContextMenuPasteQueryRuleGroup",
        "treeContextMenuPasteChildQueryRuleGroup","treeContextMenuQueryRuleGroupCancelSelection",
        "treeContextMenuQueryRuleGroupCancelSelection"],
    differentTypesChildren: true};

  nodeInfo["queryRule"] = {nodeUrl: "${pageContext.request.contextPath}${queryPath}/queryrule.jsp?queryRuleId=",
    actionsHided: ["root_qr_set","qr_set","qr_group"], actionsDisabled: ["addChild"], permitedTypesToPaste: [],
    disabledMenuIds: ["treeContextMenuPasteQueryRule", "treeContextMenuQueryRuleCancelSelection",
        "treeContextMenuPasteChildQueryRule", "treeContextMenuQueryRuleGroupCancelSelection"],
    moveHandleMethod:"handleTransferQueryRule",
    moveIdField: "queryRuleId",
    moveNameField: "queryRuleName"};
</script>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/query/tree_query_rule_sets.jsp#2 $$Change: 651448 $--%>
