<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:setFrameworkLocale />
<dsp:page>

<%-- Access control was left off of this page deliberately --%>

<html>
<head>
<meta http-equiv="Refresh" content='<%="0; URL="+ request.getParameter("target") %>'> 

</head>
<body bgcolor="#ffffff">
<table height="100%" width="100%">
<tr>
<td align="center">
<font color="#cccccc" size="+2" face="arial,geneva,helvetica,verdana">
<b><i18n:message key="frame_loading"/></b>
</font>
</td>
</tr></table>


</body>
</html>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/frame_loading.jsp#2 $$Change: 651448 $--%>
