<%--
JSP, used to be popup with delete partition functionality

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/reset_tree.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>
<d:page>

  <d:getvalueof bean="/atg/searchadmin/adminui/navigation/NavigationStateComponent" var="navigationState"/>
  <d:getvalueof bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.needUpdate" var="needUpdate"/>
  
  <c:if test="${navigationState.update or needUpdate}">
    <div id="treeNew" style="display:none">
      <d:getvalueof var="root" bean="/atg/searchadmin/adminui/navigation/NavigationTreeComponent.areasById.projects"/>
      <d:include page="tree_node_children.jsp">
        <d:param name="children" value="${root.stateNode.children}"/>
      </d:include>
    </div>
    <script type="text/javascript">
      top.resetTree(document.getElementById("treeNew"));
      refreshTreeArea(false);
    </script> 
    
    <c:set target="${navigationState}" property="update" value="${false}"/>
    <d:setvalue bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject.needUpdate" value="false"/>
  </c:if>

  <c:if test="${navigationState.current.areaId == 'projects'}">
    <c:set var="linkBuilder" value="${navigationState.linkBuilder}"/>
    <c:set target="${linkBuilder}" property="node" value="${navigationState.currentInTree}"/>
    <script type="text/javascript">
      top.setCurrentNode("${linkBuilder.link}");
    </script>
  </c:if>
  
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/reset_tree.jsp#2 $$Change: 651448 $--%>
