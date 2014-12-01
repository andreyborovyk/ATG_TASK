<%--
Redirect to the Business Control Center (BCC)

@version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/bcc.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ include file="/templates/top.jspf" %>

<d:page>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html><head>
  <d:importbean bean="/atg/searchadmin/adminui/Configuration" var="configuration"/>
  <script type="text/javascript">
    document.location = "${configuration.BCCHomeURL}";
  </script>
</head></html>
</d:page>
<%-- @version $Id: //application/SearchAdmin/version/10.0.3/admin-ui/src/web-apps/AdminUI/bcc.jsp#2 $$Change: 651448 $--%>
