

<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />
<paf:setFrameworkLocale />
<dsp:page>


<html>
<head>
<title></title>

<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">
</head>


<body bgcolor="#ffffff" text="#333333" link="#3366ff" vlink="#3366ff" marginheight=0 marginwidth=0 leftmargin=0 rightmargin=0 topmargin=0 >
<br>
<table width="170" border="0" cellpadding="0" cellspacing="0">
<tr>
<td valign="top" width="20">&nbsp;&nbsp;</td>

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

  <%@ include file="nav_sidebar.jspf" %>

</td></tr></table>

</body>
</html>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/frame_sidebar.jsp#2 $$Change: 651448 $--%>
