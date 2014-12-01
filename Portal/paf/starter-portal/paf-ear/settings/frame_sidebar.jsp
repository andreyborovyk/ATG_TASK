<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<% String CsettingsURLStr = null;%>
<% String CusersURLStr = null; %>
<% String CpagesURLStr = null; %>
<% String CgearsURLStr = null; %>

<i18n:bundle baseName="atg.portal.admin.SettingsResources" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:setFrameworkLocale />
<dsp:page>

<admin:InitializeAdminEnvironment id="adminEnv">
 
<paf:hasCommunityRole roles="leader,settings-manager,user-manager,page-manager,gear-manager" barrier="true"/>

<html>
<head>
<title></title>

<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
</head>


<body bgcolor="#ffffff" text="#333333" link="#3366ff" vlink="#3366ff" marginheight=0 marginwidth=0 leftmargin=0 rightmargin=0 topmargin=0 >

<table width="150" border="0" cellpadding="0" cellspacing="0">
<tr>
<td valign="top">&nbsp;&nbsp;</td>

<td width="150" valign="top">
<%  String thisNavHighlight = "";
    if ( request.getParameter("navHighLight") != null ) {
      thisNavHighlight = request.getParameter("navHighLight") ;  // used for header_main
    }
    String currPid = "0";
    if ( request.getParameter("currPid") != null ) {
      currPid = request.getParameter("currPid") ;  // used for sidebar_main
    }

    String clearGif =  response.encodeURL("images/clear.gif");

    String mode          = "1";          // used for sub page high light
                                         // this copies an index array to a rendering array 
                                         // that provides the side links
    String outputStr     = "";           // the output for header and sidebar includes

    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to available gears
    }
    
%>

 <dsp:include page="nav_sidebar.jsp" flush="false">
  <dsp:param  name="mode" value="<%=mode%>" /> 
  <dsp:param  name="sideBarIndex" value="<%=currPid%>" /> 
 </dsp:include>

</td></tr></table>

</body>
</html>

</admin:InitializeAdminEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/frame_sidebar.jsp#2 $$Change: 651448 $--%>
