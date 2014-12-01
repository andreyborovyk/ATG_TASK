<%--
This page shows "bread crumbs" parent element (as a link).

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/bread_crumbs_print.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ include file="/templates/top.jspf" %>

<d:page>
  <d:getvalueof param="stateNode" var="stateNode"/>
  <d:getvalueof param="breadcrumbs" var="breadcrumbs"/>

  <c:choose>
    <c:when test="${not stateNode.node.virtual}">
      <h2>${breadcrumbs}</h2>
    </c:when>
    <c:otherwise>
      <c:set var="stateNode" value="${stateNode.parent}" />

      <%-- set appropriate link --%>
      <c:set var="headerLink" value="${pageContext.request.contextPath}${stateNode.link}"/>

      <d:include page="/templates/bread_crumbs_set_title.jsp">
        <d:param name="stateNode" value="${stateNode}" />
      </d:include>

      <c:if test="${stateNode.node.tab}">
        <c:set var="stateNode" value="${stateNode.parent}" />
        <c:set var="headerText" value="${requestScope.headerText}" />
        <d:include page="/templates/bread_crumbs_set_title.jsp">
          <d:param name="stateNode" value="${stateNode}" />
        </d:include>
        <c:set var="headerText" value="${requestScope.headerText} (${headerText})" />
      </c:if>

      <c:set var="breadcrumbs">
        <a href="${headerLink}" onclick="return loadRightPanel(this.href);"><c:out value="${headerText}"/></a>
        &gt;
        ${breadcrumbs}
      </c:set>
      <d:include page="/templates/bread_crumbs_print.jsp">
        <d:param name="stateNode" value="${stateNode}" />
        <d:param name="breadcrumbs" value="${breadcrumbs}" />
      </d:include>
    </c:otherwise>
  </c:choose>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/templates/bread_crumbs_print.jsp#2 $$Change: 651448 $--%>
