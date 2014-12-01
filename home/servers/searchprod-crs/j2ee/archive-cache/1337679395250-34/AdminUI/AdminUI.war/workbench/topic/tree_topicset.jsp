<%--
  Topic Tree JSP

  @author: sshulman
  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/tree_topicset.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<!-- Menu init -->
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<div dojoType="atg.searchadmin.tree.DojoTreeMenu" id="topicSetTreeContextMenu" style="display:none">
  <div dojoType="dijit.MenuItem" class="context_menu_rootTopicSetNode" id="treeContextMenuNewTopicSet"
       redirectUrl="${contextPath}${topicPath}/topicset_new.jsp">
       <span><fmt:message key="topicset.dojo.menu.new_topic_set"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_rootTopicSetNode" id="treeContextMenuImportTopicSet"
       redirectUrl="${contextPath}${topicPath}/topicset_import.jsp">
       <span><fmt:message key="topicset.dojo.menu.import_topic_set"/></span></div>

  <div dojoType="dijit.MenuItem" class="context_menu_topicset" id="treeContextMenuCopyToNewTopicSet"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TopicTopicSetsFormHandler"
       handleMethod="handleCopy"
       handlerIdField="topicSetId"
       action="copyToNew">
       <span><fmt:message key="topicset.dojo.menu.copy_to_new"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topicset" id="treeContextMenuExportTopicSet"
       redirectUrl="${contextPath}${topicPath}/topicset_export.jsp?topicSetId=">
       <span><fmt:message key="topicset.dojo.menu.export"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_topicset"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topicset" id="treeContextMenuDeleteTopicSet"
       popUpUrl="${contextPath}${topicPath}/topicset_delete_popup.jsp?topicSetId=">
       <span><fmt:message key="topicset.dojo.menu.delete"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topicset" id="treeContextMenuRenameTopicSet"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/ManageTopicSetFormHandler"
       handleMethod="handleRename"
       renameFieldName="topicSetName"
       handlerIdField="topicSetId"
       action="rename">
       <span><fmt:message key="topicset.dojo.menu.rename"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_topicset"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topicset" id="treeContextMenuPasteAsChildTopic"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler"
       handlerIdField="topicId"
       handlerNameField="topicName"
       handleCutPasteMethod="handleTransferTopic"
       handleCopyPasteMethod="handleCopyTopic" 
       action="pasteAsChild" disabled="true">
       <span><fmt:message key="topicset.dojo.menu.paste_as_child_topic"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_topicset"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topicset" id="treeContextMenuSortTopicSet"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TopicsFormHandler"
       handleMethod="handleSort"
       handlerIdField="parentId"
       action="sort">
       <span><fmt:message key="topicset.dojo.menu.sort"/></span></div>

  <div dojoType="dijit.MenuItem" class="context_menu_topic" id="treeContextMenuNewTopic"
       labelNewItem="<fmt:message key="topicset.dojo.menu.new_topics"/>"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TopicsFormHandler"
       handleMethod="handleSimpleCreateTopics"
       renameFieldName="topicName"
       action="new">
       <span><fmt:message key="topicset.dojo.menu.new_topics"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topic" id="treeContextMenuNewChildTopic"
       labelNewItem="<fmt:message key="topicset.dojo.menu.new_child_topics"/>"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TopicsFormHandler"
       handleMethod="handleSimpleCreateTopics"
       renameFieldName="topicName"
       action="newChild">
       <span><fmt:message key="topicset.dojo.menu.new_child_topics"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_topic"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topic" id="treeContextMenuCutTopic"
       action="cut">
       <span><fmt:message key="topicset.dojo.menu.cut"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topic" id="treeContextMenuCopyTopic"
       action="copy">
       <span><fmt:message key="topicset.dojo.menu.copy"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topic" id="treeContextMenuPasteTopic"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler"
       handlerIdField="topicId"
       handlerNameField="topicName"
       handleCutPasteMethod="handleTransferTopic"
       handleCopyPasteMethod="handleCopyTopic" 
       action="paste" disabled="true">
       <span><fmt:message key="topicset.dojo.menu.paste"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topic" id="treeContextMenuPasteChildTopic"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler"
       handlerIdField="topicId"
       handlerNameField="topicName"
       handleCutPasteMethod="handleTransferTopic"
       handleCopyPasteMethod="handleCopyTopic" 
       action="pasteChild" disabled="true">
       <span><fmt:message key="topicset.dojo.menu.paste_as_child"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_topic"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topic" id="treeContextMenuDeleteTopic"
       popUpUrl="${contextPath}${topicPath}/topic_delete_popup.jsp?topicId=">
       <span><fmt:message key="topicset.dojo.menu.delete"/></span></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topic" id="treeContextMenuRenameTopic"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler"
       handleMethod="handleRename"
       renameFieldName="topicName"
       handlerIdField="topicId"
       action="rename">
       <span><fmt:message key="topicset.dojo.menu.rename"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_topic"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topic" id="treeContextMenuSortTopic"
       formHandler="/atg/searchadmin/workbenchui/formhandlers/TopicsFormHandler"
       handleMethod="handleSort"
       handlerIdField="parentId"
       action="sort">
       <span><fmt:message key="topicset.dojo.menu.sort"/></span></div>
  <div dojoType="dijit.MenuSeparator" class="context_menu_topic"></div>
  <div dojoType="dijit.MenuItem" class="context_menu_topic" id="treeContextMenuTopicCancelSelection"
       action="cancelSelection" disabled="true">
       <span><fmt:message key="topicset.dojo.menu.cancel_selection"/></span></div>
</div>

<div dojoType="atg.searchadmin.tree.LazyLoadStore"
           jsId="topic_set_store"
           url="tree_controller.dojo?_dyncharset=UTF-8"
           label="titleText"
           identifier="id">
      </div>
<div dojoType="atg.searchadmin.tree.DojoTree"
           id="topic_set_dojo_Tree"
           store="topic_set_store"
           class="atg"
           successUrl="${topicPath}/topic_set_json_response.jsp"
           errorUrl="${topicPath}/topic_set_json_response.jsp"
           expandUrl="${topicPath}/topic_tree.jsp"
           moveFormHandler="/atg/searchadmin/workbenchui/formhandlers/ManageTopicFormHandler"
           moveHandleMethod="handleTransferTopic"
           moveIdField="topicId"
           moveNameField="topicName"
           persist="false"
           menuId="topicSetTreeContextMenu"
           dndController="atg.searchadmin.tree.DojoTreeDnDSource"
           renameDiv="treeNodeRenameDiv">
           <script type="dojo/connect">
                var menu = dijit.byId("topicSetTreeContextMenu");
                menu.bindDomNode(this.domNode);
           </script>
</div>
<div id=topicSetTreeTooltip" dojoType="atg.searchadmin.tree.DojoTreeTooltip" connectId="topic_set_dojo_Tree" class="ea" style="display: none;"/>

<script type="text/javascript">
  nodeInfo["rootTopicSetNode"] = {nodeUrl: "${pageContext.request.contextPath}${topicPath}/global_general.jsp?",
    actionsHided: ["topic", "topicset"], actionsDisabled: ["addChild","move"]};

  nodeInfo["TopicSet"] = {nodeUrl : "${pageContext.request.contextPath}${topicPath}/topicset.jsp?topicSetId=",
    actionsHided : ["rootTopicSetNode", "topic"], actionsDisabled : ["move"],
    permitedTypesToPaste : ["Topic"], rootParentNodeId: "rootTopicSetNode"};

  nodeInfo["Topic"] = {nodeUrl : "${pageContext.request.contextPath}${topicPath}/topic.jsp?topicId=",
    actionsHided : ["rootTopicSetNode", "topicset"], actionsDisabled : [],
    permitedTypesToPaste : ["Topic"], disabledMenuIds: ["treeContextMenuPasteAsChildTopic",
        "treeContextMenuPasteTopic","treeContextMenuPasteChildTopic","treeContextMenuTopicCancelSelection"]};
</script>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/workbench/topic/tree_topicset.jsp#2 $$Change: 651448 $--%>
