<%--
  This page shows all children of given navigation tree node.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/tree_node_children.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof var="children" param="children"/>

  <c:forEach items="${children}" var="child">
    <c:set var="children" value="${child.children}" />
    <c:set var="treeNodeType" value="leafTreeNode" />
    <c:if test="${not empty children}">
      <c:set var="treeNodeType" value="closedTreeNode" />
      <%-- Uncomment the following section to open "Browse Projects" node by default --%>
      <%--c:if test="${child.node.children[0].type == 'project'}">
        <c:set var="treeNodeType" value="openedTreeNode" />
      </c:if--%>
    </c:if>
    
    <div class="treeNode ${treeNodeType}">
      <fmt:message key="${child.node.treeTitle}" var="title">
        <c:forEach var="titleParam" items="${child.titleParameters}">
          <fmt:param value="${titleParam}" />
        </c:forEach>
      </fmt:message>
      <a onclick="return loadRightPanel(this.href);"
         href="${pageContext.request.contextPath}${child.link}"><c:out value="${title}"/></a>
      <d:include page="tree_node_children.jsp">
        <d:param name="children" value="${children}" />
      </d:include>
    </div>
  </c:forEach>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/tree_node_children.jsp#2 $$Change: 651448 $--%>
