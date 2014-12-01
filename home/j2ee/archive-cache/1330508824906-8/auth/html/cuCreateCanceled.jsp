<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>

<i18n:bundle baseName="atg.portal.authentication.MembershipCaptureResources" localeAttribute="userLocale" changeResponseLocale="false" />

<html>
<head>
<title></title>

<link rel="STYLESHEET" type="text/css" href="/portal/settings/css/default.css" src="/portal/settings/css/default.css">


</head>
<body bgcolor="#ffffff" topmargin="0" marginheight="0" leftmargin="0" marginwidth="0">

<dsp:getvalueof id="dsp_community_id" idtype="java.lang.String" param="paf_community_id">
<dsp:getvalueof id="dsp_page_url"     idtype="java.lang.String" param="paf_page_url">
<dsp:getvalueof id="mode"             idtype="java.lang.String" param="mode">
 
Cancel message

</dsp:getvalueof><%-- mode             --%>
</dsp:getvalueof><%-- paf_page_url     --%>
</dsp:getvalueof><%-- paf_community_id --%>

</body>
</html>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/cuCreateCanceled.jsp#2 $$Change: 651448 $--%>
