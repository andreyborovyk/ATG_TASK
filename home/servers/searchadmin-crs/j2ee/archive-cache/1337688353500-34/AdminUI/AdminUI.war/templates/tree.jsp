<%--
  This page shows navigation tree.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/tree.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="navigationState" bean="/atg/searchadmin/adminui/navigation/NavigationStateComponent" />
  <d:getvalueof var="navigationTree" bean="/atg/searchadmin/adminui/navigation/NavigationTreeComponent" />
  <c:set var="workbenchChildren" value="${navigationTree.areasById.workbench.children}" />
  <script type="text/javascript">
    var workbenchItemsLinks = {
      <c:set var="workbenchStateNode" value="${navigationTree.areasById.workbench.stateNode}" />
      <tags:join items="${workbenchChildren}" var="custItem" delimiter=", ">
        "${custItem.activeProjectType}": "${pageContext.request.contextPath}${custItem.url}"
      </tags:join>
    };
  </script>

  <div id="tab_area_projects" dojoType="dojox.layout.ContentPane" label="<fmt:message key='tree.tab.search.admin'/>"
       style="width:100%; height:100%;<c:if test="${navigationState.current.areaId != 'projects'}">display:none;</c:if>">
    <div id="projectsTree" onclick="treeOnClick(event, 'skip')">
      <d:include page="tree_node_children.jsp">
        <d:param name="children" value="${navigationTree.areasById.projects.stateNode.children}"/>
      </d:include>
    </div>
    <script type="text/javascript">
      initTree();
    </script>
  </div>
  <div id="tab_area_workbench" dojoType="dijit.layout.LayoutContainer"
       label="<fmt:message key='tree.tab.search.workbench'/>" class="treebackground"
       style="width:100%; height:100%;<c:if test="${navigationState.current.areaId != 'workbench'}">display:none;</c:if>">
    <div class="paneHeader" dojoType="dojox.layout.ContentPane"  layoutAlign="top"><div>
      <span><fmt:message key="select_custom_items.show" /></span>
      <select id="workbench_items_dropdown" onchange="workbenchItemChange(this)">
        <c:forEach items="${workbenchChildren}" var="custItem">
          <option value="${custItem.activeProjectType}">
            <fmt:message key="${empty custItem.activeProjectType ? 'select_custom_items.select' : custItem.title}" />
          </option>
        </c:forEach>
      </select>
    </div></div>

    <div id="activeProjectPane" dojoType="dojox.layout.ContentPane"  layoutAlign="top"
         href="${pageContext.request.contextPath}/workbench/active_project_control.jsp"
         executeScripts="true" cacheContent="false" scriptSeparation="false" class="projectHeader">
    </div>
    <!-- rename div -->
    <div id="treeNodeRenameDiv" style="display:none">
      <input type="text" class="textField" name="nodeRename" value=""
             onkeypress="checkForRenameSubmit(this, event)" onblur="renameNodeSubmit();">
    </div>

    <div id="tree_topic_set" class="treeDiv" style="display:none;overflow:auto" dojoType="dojox.layout.ContentPane" layoutAlign="client"
         href="${pageContext.request.contextPath}/workbench/topic/tree_topicset.jsp"
         executeScripts="true" cacheContent="false" loadingMessage="<fmt:message key='tree.dojo.loading'/>"
         scriptSeparation="false">
    </div>

    <div id="tree_term_dict" class="treeDiv" style="display:none;overflow:auto" dojoType="dojox.layout.ContentPane" layoutAlign="client"
         href="${pageContext.request.contextPath}/workbench/dictionary/tree_term_dictionaries.jsp"
         executeScripts="true" cacheContent="false" loadingMessage="<fmt:message key='tree.dojo.loading'/>"
         scriptSeparation="false">
    </div>

    <div id="tree_tpo_set" class="treeDiv" style="display:none;overflow:auto" dojoType="dojox.layout.ContentPane" layoutAlign="client"
         href="${pageContext.request.contextPath}/workbench/tpo/tree_tpo.jsp"
         executeScripts="true" cacheContent="false" loadingMessage="<fmt:message key='tree.dojo.loading'/>"
         scriptSeparation="false">
    </div>

    <div id="tree_query_sets" class="treeDiv" style="display:none;overflow:auto" dojoType="dojox.layout.ContentPane" layoutAlign="client"
         href="${pageContext.request.contextPath}/workbench/query/tree_query_rule_sets.jsp"
         executeScripts="true" cacheContent="false" loadingMessage="<fmt:message key='tree.dojo.loading'/>"
         scriptSeparation="false">
    </div>

    <div id="tree_term_weight" class="treeDiv" style="display:none;overflow:auto" dojoType="dojox.layout.ContentPane" layoutAlign="client"
         href="${pageContext.request.contextPath}/workbench/weight/tree_term_weight_sets.jsp"
         executeScripts="true" cacheContent="false" loadingMessage="<fmt:message key='tree.dojo.loading'/>"
         scriptSeparation="false">
    </div>

    <div id="tree_facet_set" class="treeDiv" style="display:none;overflow:auto" dojoType="dojox.layout.ContentPane" layoutAlign="client"
         href="${pageContext.request.contextPath}/workbench/facet/tree_facet_sets.jsp"
         executeScripts="true" cacheContent="false" loadingMessage="<fmt:message key='tree.dojo.loading'/>"
         scriptSeparation="false">
    </div>

    <script type="text/javascript">
      areaSupport.selectTab("${navigationState.current.areaId}"); 
      switchTree('<c:out value="${navigationState.current.activeProjectType}" />', false);
    </script>
  </div>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/tree.jsp#2 $$Change: 651448 $--%>
