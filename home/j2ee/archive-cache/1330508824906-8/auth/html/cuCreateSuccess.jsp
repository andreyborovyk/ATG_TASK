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
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>

<font class="info">
<img src='<%= response.encodeURL("/portal/settings/images/info.gif")%>'>&nbsp;<i18n:message key="community_users_create_success_message"/>
</font>
<font class="smaller">
<br><br>
<%--
<core:CreateUrl id="newUserURL" url="community_users_create.jsp">
 <i18n:message id="createLinkText" key="community_users_create_success_create_link"/>
 <i18n:message  key="community_users_create_success_create_link_params">
  <i18n:messageArg value="<%=createLinkText%>" />
  <i18n:messageArg value='<%=  "<a href=" + newUserURL.getNewUrl() + " target='_self'>" %>' />
  <i18n:messageArg value="</a>" />
 </i18n:message>
</core:CreateUrl>--%>

</td></tr></table>

</dsp:getvalueof><%-- mode             --%>
</dsp:getvalueof><%-- paf_page_url     --%>
</dsp:getvalueof><%-- paf_community_id --%>

</body>
</html>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/authentication/auth.war/web/html/cuCreateSuccess.jsp#2 $$Change: 651448 $--%>
