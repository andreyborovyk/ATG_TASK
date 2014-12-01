<%@ tag language="java" %>
<%@ include file="/templates/top.tagf" %>
<%@ attribute name="defaultOptionKey" required="true" %>
<%@ attribute name="items" required="true" type="java.lang.Object" %>
<%@ attribute name="var" required="true" rtexprvalue="false" %>
<%@ variable name-from-attribute="var" variable-class="java.lang.Object" alias="item" scope="NESTED" %>
<select class="small" onchange="onSelectLinkChange(this)">
  <option value=""><fmt:message key="${defaultOptionKey}"/></option>
  <c:forEach items="${items}" var="item">
    <jsp:doBody/>
  </c:forEach>
</select>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/selectLink.tag#2 $$Change: 651448 $--%>
