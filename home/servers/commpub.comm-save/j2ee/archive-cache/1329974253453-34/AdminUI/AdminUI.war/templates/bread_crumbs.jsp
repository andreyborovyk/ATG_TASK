<%--
This page shows "bread crumbs" header for current page.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/bread_crumbs.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <div id="paneHeader" dojoType="dojox.layout.ContentPane" layoutAlign="top">
    <d:include page="reset_tree.jsp"/>

    <d:getvalueof var="navigationState" bean="/atg/searchadmin/adminui/navigation/NavigationStateComponent" />
    <d:getvalueof var="activeProject" bean="/atg/searchadmin/workbenchui/beans/ActiveSearchProject" />
    <script type="text/javascript">
      areaSupport.selectTab("${navigationState.current.areaId}"); 
      switchTree('<c:out value="${navigationState.current.activeProjectType}" />', ${activeProject.needUpdate});
    </script>

    <c:set var="currentStateNode" value="${navigationState.currentStateNode}"/>
    <c:set var="titleStateNode" value="${currentStateNode}"/>
    <c:if test="${titleStateNode.node.tab}"><c:set var="titleStateNode" value="${titleStateNode.parent}"/></c:if>

    <d:include page="/templates/bread_crumbs_set_title.jsp">
      <d:param name="stateNode" value="${titleStateNode}" />
    </d:include>

    <div style="display:none;" id="documentTitle">
      <fmt:message key="appTitle">
        <fmt:param><c:out value="${headerText}"/></fmt:param>
      </fmt:message>
    </div>

    <c:set var="breadcrumbs"><c:out value="${headerText}" /></c:set>
    <d:include page="/templates/bread_crumbs_print.jsp">
      <d:param name="stateNode" value="${titleStateNode}" />
      <d:param name="breadcrumbs" value="${breadcrumbs}" />
    </d:include>

    <script type="text/javascript">
      var documentTitle = document.getElementById("documentTitle");
      if (documentTitle) {
        document.title = documentTitle.firstChild.data.trim();
        top.document.title = document.title;
      }
    </script>
  </div>
  <c:if test="${currentStateNode.node.tab}">
    <c:set var="linkBuilder" value="${navigationState.linkBuilder}"/>
    <div id="subNav" dojoType="dojox.layout.ContentPane" layoutAlign="top">
      <ul>
        <c:forEach items="${currentStateNode.node.parent.tabChildren}" var="tabChild">
          <c:set target="${linkBuilder}" property="node" value="${tabChild}"/>
          <li <c:if test="${tabChild == navigationState.current}">class="current"</c:if>>
            <a href="${pageContext.request.contextPath}${linkBuilder.link}" onclick="return loadRightPanel(this.href)">
              <fmt:message key="${linkBuilder.title}">
                <c:forEach var="titleParam" items="${linkBuilder.titleParameters}">
                  <fmt:param value="${titleParam}" />
                </c:forEach>
              </fmt:message>
            </a>
          </li>
        </c:forEach>
      </ul>
    </div>
  </c:if>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/bread_crumbs.jsp#2 $$Change: 651448 $--%>
