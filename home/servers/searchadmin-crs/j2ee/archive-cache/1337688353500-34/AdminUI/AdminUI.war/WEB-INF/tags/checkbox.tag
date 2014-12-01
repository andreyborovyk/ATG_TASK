<%--
  This tag inserts check box field which is linked with a hidden field to be sure that boolean[] will be filled correctly.

  @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/checkbox.tag#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ tag body-content="empty" %>
<%@ include file="/templates/top.tagf" %>
<%@ attribute name="beanName" required="true" rtexprvalue="true" %>
<%@ attribute name="name" required="true" rtexprvalue="true" %>
<%@ attribute name="checked" required="true" rtexprvalue="true" %>
<%@ attribute name="id" required="false" rtexprvalue="true" %>
<input type="checkbox" <c:if test="${checked}">checked="true"</c:if> <c:if test="${not empty id}">id="${id}"</c:if>
onclick="this.nextSibling.value=this.checked;" /><d:input type="hidden" name="${name}" bean="${beanName}.${name}" value="${checked}" />
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/checkbox.tag#2 $$Change: 651448 $--%>
