<%--
Catch tag. Allows to redirect to /error servlet if there is an error in the body.
This tag uses scriptlets because standard <c:catch> tag doesn't work for tag files.
todo: investigate the reasons.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/catch.tag#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ tag language="java" %>
<%@ attribute name="customJsAction" required="false" %>
<%@ attribute name="errorUrl" required="false" %>
<%
  try {
%>
<jsp:doBody/>
<%
  } catch (Throwable e) {
    session.setAttribute(atg.searchadmin.adminui.navigation.ErrorHandlerServlet.ERROR_SESSION_ATTR, e);
    String custAction = (String) getJspContext().getAttribute("customJsAction");
    String url = (String) getJspContext().getAttribute("errorUrl");
    if (custAction == null && url == null) {
      url = "/error?" + atg.searchadmin.adminui.navigation.ErrorHandlerServlet.REDIRECT_URL_ATTR + "=" +
          atg.searchadmin.adminui.navigation.NavigationLinkHelper.getParentLink(atg.servlet.ServletUtil.getDynamoRequest(request), true);
    }
%>
<script type="text/javascript">
  function customAfterLoad() {
    <% if (custAction == null) { %>
    loadingStatus.setRedirectUrl("${pageContext.request.contextPath}<%= url %>");
    <% } else { %>
    <%= custAction %>
    <% } %>
  }
</script>
<%
  }
%>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/catch.tag#2 $$Change: 651448 $--%>
