<%--
  This tag inserts a select with options or a label with a hidden field if only one option.
  --%>
<%@ tag body-content="empty" %>
<%@ include file="/templates/top.tagf" %>
<%@ attribute name="bean" required="true" rtexprvalue="true" %>
<%@ attribute name="items" required="true" type="java.lang.Object" %>
<%@ attribute name="emptyMessageKey" required="true" rtexprvalue="true" %>
<c:choose>
  <c:when test="${empty items}">
    <fmt:message key="${emptyMessageKey}" />
    <d:input type="hidden" bean="${bean}" value=""/>
  </c:when>
  <c:when test="${fn:length(items) > 1}">
    <d:select iclass="small" bean="${bean}">
      <c:forEach items="${items}" var="item">
        <admin-ui:option value="${item}"><c:out value="${item}" /></admin-ui:option>
      </c:forEach>
    </d:select>
  </c:when>
  <c:otherwise>
    <c:out value="${items[0]}" />
    <d:input type="hidden" bean="${bean}" value="${items[0]}"/>
  </c:otherwise>
</c:choose>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/select.tag#2 $$Change: 651448 $--%>
