
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>


<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>


<html>
<head>
<title></title>

<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">

</head>

<%

    String clearGif =  response.encodeURL("images/clear.gif"); 
    String mode  = "";
    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to available gears
    }

%>



<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
</head>

<body bgcolor="#ffffff" text="#333333" link="#3366ff" vlink="#3366ff" marginheight=0 marginwidth=0 leftmargin=0 rightmargin=0 topmargin=0 >
<br>
<%@include file="community_gears_fragment_listing.jspf"%>


</body>
</html>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/frame_content_gears.jsp#2 $$Change: 651448 $--%>
