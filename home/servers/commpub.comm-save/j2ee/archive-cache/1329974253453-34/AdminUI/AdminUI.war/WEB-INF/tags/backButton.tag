<%@ tag body-content="empty" %>
<%@ attribute name="value" required="false" %>
<%@ attribute name="title" required="false" %>
<input type="button" value="${value}" title="${title}" onclick="return loadRightPanel('${pageContext.request.contextPath}/back')"/>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/backButton.tag#2 $$Change: 651448 $--%>
