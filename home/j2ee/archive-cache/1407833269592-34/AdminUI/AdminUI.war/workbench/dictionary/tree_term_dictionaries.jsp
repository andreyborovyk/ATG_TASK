<%--
  Dictionary Dojo Tree init JSP

  @author: pkouzmit
  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/tree_term_dictionaries.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.activeProjectId" var="activeProjectId"/>

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
<div dojoType="atg.searchadmin.tree.DojoTreeMenu" id="dictionaryTreeContextMenu" style="display:none">
  <div dojoType="dijit.MenuItem" class="context_menu_rootDictionaryNode" id="treeContextMenuNewTermDictionary"
       redirectUrl="${contextPath}${dictionaryPath}/new_term_dictionary.jsp">
       <span><fmt:message key="termdicts.dojo.menu.new_term_dictionary"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_rootDictionaryNode" id="treeContextMenuImportTermDictionary"
       redirectUrl="${contextPath}${dictionaryPath}/termdict_import.jsp">
       <span><fmt:message key="termdicts.dojo.menu.import_term_dictionary"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_dictionary" id="treeContextMenuCopyToNewTermDictionary"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/ManageTermDictionaryFormHandler"
       handleMethod="handleCopy"
       handlerIdField="dictId"
       action="copyToNew">
       <span><fmt:message key="termdicts.dojo.menu.copy_to_new"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_dictionary" id="treeContextMenuExportTermDictionary"
       redirectUrl="${contextPath}${dictionaryPath}/termdict_export.jsp?dictionaryId=">
       <span><fmt:message key="termdicts.dojo.menu.export"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_dictionary"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_dictionary" id="treeContextMenuDeleteTermDictionary"
       popUpUrl="${contextPath}${dictionaryPath}/term_dict_delete_popup.jsp?dictId=">
       <span><fmt:message key="termdicts.dojo.menu.delete"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_dictionary" id="treeContextMenuRenameDictionary"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TermDictsFormHandler"
       handleMethod="handleUpdateDictionaryName"
       handlerIdField="dictId"
       renameFieldName="dictionaryName"
       action="rename">
       <span><fmt:message key="termdicts.dojo.menu.rename"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_dictionary"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_dictionary" id="treeContextMenuAddTermToDictionary"
       redirectUrl="${contextPath}${dictionaryPath}/term_create.jsp?parentDictId=">
       <span><fmt:message key="termdicts.dojo.menu.add_terms"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_dictionary" id="treeContextMenuPasteAsChildTerm"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler"
       handlerIdField="termId"
       handlerNameField="term"
       handleCutPasteMethod="handleTransferTerm"
       handleCopyPasteMethod="handleCopyTerm" 
       action="pasteAsChild" disabled="true">
       <span><fmt:message key="termdicts.dojo.menu.paste_as_child_term"/></span></div>

  <div dojoType="dijit.MenuItem" class="context_menu_term" id="treeContextMenuNewTerm"
       labelNewItem="<fmt:message key="termdicts.dojo.label.new_term_name"/>"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler"
       handleMethod="handleCreateTermSimple"
       renameFieldName="term"
       action="new">
       <span><fmt:message key="termdicts.dojo.menu.new_term"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_term" id="treeContextMenuNewChildTerm"
       labelNewItem="<fmt:message key="termdicts.dojo.label.new_term_name"/>"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler"
       handleMethod="handleCreateTermSimple"
       renameFieldName="term"
       action="newChild">
       <span><fmt:message key="termdicts.dojo.menu.new_child_terms"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_term"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_term" id="treeContextMenuCutTerm" 
       action="cut">
       <span><fmt:message key="termdicts.dojo.menu.cut"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_term" id="treeContextMenuCopyTerm" 
       action="copy">
       <span><fmt:message key="termdicts.dojo.menu.copy"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_term" id="treeContextMenuPasteTerm"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler"
       handlerIdField="termId"
       handlerNameField="term"
       handleCutPasteMethod="handleTransferTerm"
       handleCopyPasteMethod="handleCopyTerm"
       action="paste" disabled="true">
       <span><fmt:message key="termdicts.dojo.menu.paste"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_term" id="treeContextMenuPasteChildTerm"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler"
       handlerIdField="termId"
       handlerNameField="term"
       handleCutPasteMethod="handleTransferTerm"
       handleCopyPasteMethod="handleCopyTerm" 
       action="pasteChild" disabled="true">
       <span><fmt:message key="termdicts.dojo.menu.paste_as_child"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_term"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_term" id="treeContextMenuDeleteTerm"
       popUpUrl="${contextPath}${dictionaryPath}/term_delete_popup.jsp?termId=">
       <span><fmt:message key="termdicts.dojo.menu.delete"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_term" id="treeContextMenuRenameTerm"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler"
       handleMethod="handleRenameTermSimple"
       handlerIdField="termId"
       renameFieldName="term"
       action="rename">
       <span><fmt:message key="termdicts.dojo.menu.rename"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_term"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_term" id="treeContextMenuTermCancelSelection"
       action="cancelSelection" disabled="true">
       <span><fmt:message key="termdicts.dojo.menu.cancel_selection"/></span></div>
       
  <div dojoType="dijit.MenuItem" class="context_menu_saoSetsNode" id="treeContextMenuNewSaoSet"
       redirectUrl="${contextPath}${dictionaryPath}/sao/sao_set_edit.jsp">
       <span><fmt:message key="termdicts.dojo.menu.new_sao_set"/></span></div>
  
  <div dojoType="dijit.MenuItem" class="context_menu_saoDefault" id="treeContextMenuCopyToNewSaoSet"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TPOSetSaoFormHandler"
       handleMethod="handleCopy"
       handlerIdField="TPOSetId"
       action="copyToNew">
       <span><fmt:message key="termdicts.dojo.menu.copy_to_new"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_sao"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_sao" id="treeContextMenuDeleteSaoSet"
       popUpUrl="${contextPath}${tpoPath}/tpo_set_delete_popup.jsp?level=sao&tpoSetId=">
       <span><fmt:message key="termdicts.dojo.menu.delete"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_sao" id="treeContextMenuRenameSaoSet"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TPOSetBaseFormHandler"
       handleMethod="handleRename" 
       handlerIdField="TPOSetId" 
       renameFieldName="TPOSetName" 
       action="rename">
       <span><fmt:message key="termdicts.dojo.menu.rename"/></span></div>
       
</div>

<div dojoType="atg.searchadmin.tree.LazyLoadStore"
           jsId="term_dict_store"
           url="tree_controller.dojo?_dyncharset=UTF-8"
           label="titleText"
           identifier="id"
           activeProjectId="${activeProjectId}">
      </div>
<div dojoType="atg.searchadmin.tree.DojoTree"
           id="term_dict_dojo_Tree"
           store="term_dict_store"
           class="atg"
           maxRange=100
           successUrl="${dictionaryPath}/dict_json_response.jsp"
           errorUrl="${dictionaryPath}/dict_json_response.jsp"
           expandUrl="${dictionaryPath}/term_dictionaries_nodes.jsp"
           moveFormHandler="/atg/searchadmin/workbenchui/formhandlers/TermFormHandler"
           moveHandleMethod="handleTransferTerm"
           moveIdField="termId"
           moveNameField="term"
           persist="false"
           menuId="dictionaryTreeContextMenu"
           dndController="atg.searchadmin.tree.DojoTreeDnDSource"
           renameDiv="treeNodeRenameDiv">
           <script type="dojo/connect">
                var menu = dijit.byId("dictionaryTreeContextMenu");
                menu.bindDomNode(this.domNode);
           </script>
</div>
<div id=termDictTreeTooltip" dojoType="atg.searchadmin.tree.DojoTreeTooltip" connectId="term_dict_dojo_Tree" class="ea" style="display: none;"/>

<script type="text/javascript">
  //disabledMenuIds - ids of menu items which "disabled" status will be change after "copy", "cut", "paste" etc. of current node operations
  //actionsHided - as all menus points of nodes of different types are actual one menu, It's necessary to hide menu items that are not related to given node.
  //nodeUrl - rightPane will be redirected to this url after clicking to given tree node
  //childIconSrc - icon of tree node
  //actionsDisabled - menu or DnD operations that not allowed to given node
  //moveFormHandler - form handler class that will carry out DnD operation. (can be set globaly - for all tree. see tree definition comments)
  //moveHandleMethod - form handler method name that will carry out DnD operation. (can be set globaly - for all tree. see tree definition comments)
  //permitedTypesToPaste - define what types of tree nodes possible to paste to given node.(pkouzmit)
  nodeInfo["rootDictNode"] = {nodeUrl: "${pageContext.request.contextPath}${dictionaryPath}/termdicts_general.jsp?",
    actionsHided: ["dictionary","term","saoSetsNode" , "sao", "saoDefault"], actionsDisabled: ["addChild","move"]};
  nodeInfo["rootDictInspectNode"] = {nodeUrl: "${pageContext.request.contextPath}${dictionaryPath}/inspection/introduction.jsp?",
    actionsHided: ["rootDictionaryNode","dictionary", "term", "saoSetsNode", "sao", "saoDefault"], 
    actionsDisabled: ["addChild","move"]};
  nodeInfo["termDictionary"] = {nodeUrl : "${pageContext.request.contextPath}${dictionaryPath}/term_dictionary.jsp?dictId=",
    actionsHided : ["rootDictionaryNode", "term", "saoSetsNode", "sao", "saoDefault"],
    permitedTypesToPaste : ["term"], actionsDisabled : ["move"], rootParentNodeId: "rootDictNode"};
  nodeInfo["virtualNode"] = {nodeUrl : "${pageContext.request.contextPath}${dictionaryPath}/term_dictionary.jsp?dictId=",
    actionsHided : ["rootDictionaryNode", "dictionary", "term", "saoSetsNode", "sao", "saoDefault"],
    permitedTypesToPaste : ["term"], actionsDisabled : ["move"]};
  nodeInfo["term"] = {nodeUrl : "${pageContext.request.contextPath}${dictionaryPath}/term_edit.jsp?termId=",
    actionsHided : ["rootDictionaryNode", "dictionary", "saoSetsNode", "sao", "saoDefault"],
    permitedTypesToPaste : ["term"], actionsDisabled : [],
    disabledMenuIds: ["treeContextMenuPasteAsChildTerm",
        "treeContextMenuPasteTerm","treeContextMenuPasteChildTerm","treeContextMenuTermCancelSelection"],
    tooltipUrl: "${pageContext.request.contextPath}${dictionaryPath}/term_tooltip.jsp?termId=",
    tooltipLoadingMessage:"<fmt:message key='tree.dojo.loading'/>"};
  nodeInfo["saoSetsNode"] = {nodeUrl : "${pageContext.request.contextPath}${dictionaryPath}/sao/sao_sets_browse.jsp?",
    actionsHided : ["rootDictionaryNode", "dictionary", "term", "sao", "saoDefault"],
    actionsDisabled : ["move" <c:if test="${not empty activeProjectId}">, "saoSetsNode"</c:if> ], rootParentNodeId: "rootDictIncpectNode"};
  nodeInfo["sao"] = {nodeUrl : "${pageContext.request.contextPath}${dictionaryPath}/sao/sao_set_edit.jsp?saoSetId=",
    actionsHided : ["rootDictionaryNode", "dictionary", "term", "saoSetsNode"], 
    actionsDisabled: ["addChild", "move"], rootParentNodeId: "saoSetsNode"};
  nodeInfo["saoDefault"] = {nodeUrl : "${pageContext.request.contextPath}${dictionaryPath}/sao/sao_set_details.jsp?saoSetId=",
    actionsHided : ["rootDictionaryNode", "dictionary", "term", "saoSetsNode"], 
    actionsDisabled : ["addChild", "move", "sao"], rootParentNodeId: "saoSetsNode"};
</script>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/dictionary/tree_term_dictionaries.jsp#1 $$Change: 651360 $--%>
