<%--
Separate window template tag. It wraps separate window page content with header and footer sections.
It describes all required JS and CSS files.

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/separateWindow.tag#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ tag language="java" %>
<%@ include file="/templates/top.tagf" %>

<%@ attribute name="title" required="false" %>
<%@ attribute name="titleName" required="false" %>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
  <c:if test="${empty title}">
    <title>${titleName}</title>
  </c:if>
  <c:if test="${not empty title}">
    <title><fmt:message key="${title}"/></title>
  </c:if>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/content.css" media="all"/>
  <script type="text/javascript" src="${pageContext.request.contextPath}/scripts/common.js"></script>
  <style>
    html, body{
      height: 100%;
      width: 100%;
      margin: 0;
      padding: 0;
      overflow: hidden;
    }
  </style>
</head>

<body>
  <tags:catch errorUrl="/searchadmin/close_window.jsp">
    <jsp:doBody/>
    <script>
      var messagesData = <admin-dojo:jsonObject><admin-dojo:jsonValue name="alerting" alreadyJson="true"><tags:ajax_messages/></admin-dojo:jsonValue></admin-dojo:jsonObject>;
    </script>
  </tags:catch>
</body>
</html>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/WEB-INF/tags/separateWindow.tag#2 $$Change: 651448 $--%>
