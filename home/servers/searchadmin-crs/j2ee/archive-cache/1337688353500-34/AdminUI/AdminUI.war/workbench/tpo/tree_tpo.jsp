<%--
  Tpo Tree JSP

  @author: atsishko
  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tree_tpo.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<%-- Menu init --%>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<div dojoType="atg.searchadmin.tree.DojoTreeMenu" id="tpoTreeContextMenu" style="display:none">
  <div dojoType="dijit.MenuItem" class="context_menu_index" id="treeContextMenuNewTpoIndex"
       redirectUrl="${contextPath}${tpoPath}/tpo_index_edit_set.jsp">
       <span><fmt:message key="tpo_sets.dojo.menu.new_index_tpo_set"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_index" id="treeContextMenuImportTpoIndex"
       redirectUrl="${contextPath}${tpoPath}/tpo_set_import.jsp?level=index">
       <span><fmt:message key="tpo_sets.dojo.menu.import_index_tpo_set"/></span></div>
  
  <div dojoType="dijit.MenuItem" class="context_menu_content" id="treeContextMenuNewTpoContent"
       redirectUrl="${contextPath}${tpoPath}/tpo_content_edit_set.jsp">
       <span><fmt:message key="tpo_sets.dojo.menu.new_content_tpo_set"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_content" id="treeContextMenuImportTpoContent"
       redirectUrl="${contextPath}${tpoPath}/tpo_set_import.jsp?level=content">
       <span><fmt:message key="tpo_sets.dojo.menu.import_content_tpo_set"/></span></div>
  
  <div dojoType="dijit.MenuItem" class="context_menu_tpo" id="treeContextMenuCopyToNewTpo"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TPOSetBaseFormHandler"
       handleMethod="handleCopy"
       handlerIdField="TPOSetId"
       action="copyToNew">
       <span><fmt:message key="tpo_sets.dojo.menu.copy_to_new"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_tpo" id="treeContextMenuExportTpo"
       redirectUrl="${contextPath}${tpoPath}/tpo_set_export.jsp?tpoSetId=" levelSource="nodeType">
       <span><fmt:message key="tpo_sets.dojo.menu.export"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_tpo" id="treeContextMenuDeleteTpo"
       popUpUrl="${contextPath}${tpoPath}/tpo_set_delete_popup.jsp?tpoSetId=" levelSource="nodeType">
       <span><fmt:message key="tpo_sets.dojo.menu.delete_tpo_set"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_tpo" id="treeContextMenuRenameTpo"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TPOSetBaseFormHandler"
       handleMethod="handleRename"
       handlerIdField="TPOSetId"
       renameFieldName="TPOSetName"
       action="rename">
       <span><fmt:message key="tpo_sets.dojo.menu.rename"/></span></div>
   
</div>

<div dojoType="atg.searchadmin.tree.LazyLoadStore"
           jsId="tpo_set_store"
           url="tree_controller.dojo?_dyncharset=UTF-8"
           label="titleText"
           identifier="id">
      </div>
<div dojoType="atg.searchadmin.tree.DojoTree"
           id="tpo_set_dojo_Tree"
           store="tpo_set_store"
           class="atg"
           successUrl="${tpoPath}/tpo_json_response.jsp"
           errorUrl="${tpoPath}/tpo_json_response.jsp"
           expandUrl="${tpoPath}/tpo_nodes.jsp"
           persist="false"
           menuId="tpoTreeContextMenu"
           dndController="atg.searchadmin.tree.DojoTreeDnDSource"
           renameDiv="treeNodeRenameDiv">
           <script type="dojo/connect">
                var menu = dijit.byId("tpoTreeContextMenu");
                menu.bindDomNode(this.domNode);
           </script>
</div>
<div id="tpoSetTreeTooltip" dojoType="atg.searchadmin.tree.DojoTreeTooltip" connectId="tpo_set_dojo_Tree" class="ea" style="display: none;"></div>

<script type="text/javascript">
  nodeInfo["rootTpoNode"] = {nodeUrl: "${pageContext.request.contextPath}${tpoPath}/tpo_sets_general.jsp", 
    actionsHided: ["tpo", "index", "content"], actionsDisabled: ["addChild","move"]};
    
  nodeInfo["indexTpoNode"] = {nodeUrl: "${pageContext.request.contextPath}${tpoPath}/tpo_browse.jsp?level=index", 
    actionsHided: ["tpo", "content"], actionsDisabled: ["addChild","move"]};
    
  nodeInfo["contentTpoNode"] = {nodeUrl: "${pageContext.request.contextPath}${tpoPath}/tpo_browse.jsp?level=content",
    actionsHided: ["tpo", "index"], actionsDisabled: ["addChild","move"]};
    
  nodeInfo["index"] = {nodeUrl: "${pageContext.request.contextPath}${tpoPath}/tpo_index_edit_set.jsp?tpoSetId=", rootParentNodeId : "indexTpoNode",
    actionsHided: ["index", "content"], actionsDisabled: ["addChild","move"]};
    
  nodeInfo["content"] = {nodeUrl: "${pageContext.request.contextPath}${tpoPath}/tpo_content_edit_set.jsp?tpoSetId=", rootParentNodeId : "contentTpoNode",
    actionsHided: ["index", "content"], actionsDisabled: ["addChild","move"]};
</script>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/tpo/tree_tpo.jsp#1 $$Change: 651360 $--%>
