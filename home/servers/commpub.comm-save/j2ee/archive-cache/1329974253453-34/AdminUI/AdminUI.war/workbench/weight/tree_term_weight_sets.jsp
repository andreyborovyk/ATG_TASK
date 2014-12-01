<%--
  Dictionary Dojo Tree init JSP

  @author: pkouzmit
  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/tree_term_weight_sets.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<!-- Menu init -->
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<div dojoType="atg.searchadmin.tree.DojoTreeMenu" id="termWeightSetTreeContextMenu" style="display:none">
  <div dojoType="dijit.MenuItem" class="context_menu_rootTermWeightSetNode" id="treeContextMenuNewTermWeightSet"
       redirectUrl="${contextPath}${weightPath}/new_term_weight_set.jsp">
       <span><fmt:message key="termweights.dojo.menu.new_term_weight_set"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_rootTermWeightSetNode" id="treeContextMenuImportTermWeightSet"
       redirectUrl="${contextPath}${weightPath}/term_weight_set_import.jsp">
       <span><fmt:message key="termweights.dojo.menu.import_term_weight_set"/></span></div>
       
  <div dojoType="dijit.MenuItem" class="context_menu_termWeightSet" id="treeContextMenuCopyToNewTermWeightSet"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TermWeightSetsFormHandler"
       handleMethod="handleCopy"
       handlerIdField="weightSetId"
       action="copyToNew">
       <span><fmt:message key="termweights.dojo.menu.copy_to_new"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_termWeightSet" id="treeContextMenuExportTermWeightSet"
       redirectUrl="${contextPath}${weightPath}/term_weight_set_export.jsp?termWeightId=">
       <span><fmt:message key="termweights.dojo.menu.export"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_termWeightSet"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_termWeightSet" id="treeContextMenuDeleteTermWeightSet"
       popUpUrl="${contextPath}${weightPath}/term_weight_set_delete_popup.jsp?weightSetId=">
       <span><fmt:message key="termweights.dojo.menu.delete"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_termWeightSet" id="treeContextMenuRenameTermWeightSet"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/ManageTermWeightSetFormHandler"
       handleMethod="handleUpdateName"
       handlerIdField="termId"
       renameFieldName="termName"
       action="rename">
       <span><fmt:message key="termweights.dojo.menu.rename"/></span></div>
</div>

<div dojoType="atg.searchadmin.tree.LazyLoadStore"
           jsId="term_weight_store"
           url="tree_controller.dojo?_dyncharset=UTF-8"
           label="titleText"
           identifier="id">
      </div>
<div dojoType="atg.searchadmin.tree.DojoTree"
           id="term_weight_dojo_Tree"
           store="term_weight_store"
           class="atg"
           successUrl="${weightPath}/weight_json_response.jsp"
           errorUrl="${weightPath}/weight_json_response.jsp"
           expandUrl="${weightPath}/term_weight_nodes.jsp"
           persist="false"
           menuId="termWeightSetTreeContextMenu"
           dndController="atg.searchadmin.tree.DojoTreeDnDSource"
           renameDiv="treeNodeRenameDiv">
           <script type="dojo/connect">
                var menu = dijit.byId("termWeightSetTreeContextMenu");
                menu.bindDomNode(this.domNode);
           </script>
</div>
<div id=termWeightTreeTooltip" dojoType="atg.searchadmin.tree.DojoTreeTooltip" connectId="term_weight_dojo_Tree" class="ea" style="display: none;"/>

<script type="text/javascript">
  nodeInfo["rootTermWeightNode"] = {nodeUrl: "${pageContext.request.contextPath}${weightPath}/term_weight_sets.jsp?",
    actionsHided: ["termWeightSet"], actionsDisabled: ["addChild","move"]};
  
  nodeInfo["termWeightSet"] = {nodeUrl : "${pageContext.request.contextPath}${weightPath}/term_weight_set.jsp?termWeightId=",
    actionsHided : ["rootTermWeightSetNode"], actionsDisabled : ["addChild", "move"], 
    rootParentNodeId: "rootTermWeightNode"};
</script>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/weight/tree_term_weight_sets.jsp#2 $$Change: 651448 $--%>
