<%--
Allows to join collection of items using some delimiter.
Example of usage:
<tags:join items="array" var="item" delimiter=", ">
  <c:if test="${item.good}">${item.name}</c:if>
</tags:join>
--%>
<%@ tag %>
<%@ include file="/templates/top.tagf" %>
<%@ attribute name="items" required="true" type="java.lang.Object" %>
<%@ attribute name="var" required="true" rtexprvalue="false" %>
<%@ attribute name="prefix" required="false" rtexprvalue="true" %>
<%@ attribute name="postfix" required="false" rtexprvalue="true" %>
<%@ attribute name="delimiter" required="true" %>
<%@ variable name-from-attribute="var" variable-class="java.lang.Object" alias="item" scope="NESTED" %>
<c:set var="joinedString" value="" />
<c:forEach items="${items}" var="item">
  <c:set var="itemBody"><jsp:doBody/></c:set>
  <c:set var="itemBody" value="${fn:trim(itemBody)}" />
  <c:if test="${not empty itemBody}">
    <c:set var="joinedString"><c:if test="${not empty joinedString}">${joinedString}${delimiter}</c:if>${itemBody}</c:set>
  </c:if>
</c:forEach>
<c:if test="${not empty joinedString}">${prefix}${joinedString}${postfix}</c:if>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/join.tag#2 $$Change: 651448 $--%>
