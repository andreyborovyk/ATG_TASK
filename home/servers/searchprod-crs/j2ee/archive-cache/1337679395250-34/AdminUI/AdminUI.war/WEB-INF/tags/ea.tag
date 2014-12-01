<%@ tag language="java" body-content="empty"%>
<%@ attribute name="key" required="true" %>
<%@ attribute name="id" required="false" %>
<%@ attribute name="bundle" required="false" type="java.lang.Object"%>
<%@ include file="/templates/top.tagf" %>
<c:choose>
<c:when test="${empty bundle}"><fmt:message var="message" key="${key}"/></c:when>
<c:otherwise><fmt:message var="message" key="${key}" bundle="${bundle}"/></c:otherwise>
</c:choose>
<c:if test="${not empty message}">
  <c:set var="missed" value="???${key}???" />
  <c:if test="${message != missed}">
    <c:if test="${not empty id}"><c:set var="key" value="${id}" /></c:if>
    <span id="${key}"></span><script>eaRegister("${key}", "${adminfunctions:escapeJsString(message)}");</script>
  </c:if>
</c:if><c:if test="${empty message or message == missed}">&nbsp;</c:if>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/ea.tag#2 $$Change: 651448 $--%>
