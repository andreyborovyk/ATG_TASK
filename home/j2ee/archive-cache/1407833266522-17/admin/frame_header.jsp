
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/admin-taglib" prefix="admin" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:setFrameworkLocale />

<dsp:page>


<i18n:bundle baseName="atg.portal.admin.PortalAdminResources" localeAttribute="userLocale" changeResponseLocale="false" />


<dsp:importbean bean="/atg/userprofiling/Profile"/>

<html>
<head>
<title></title>

<link rel="STYLESHEET" type="text/css" href="css/default.css" src="css/default.css">

</head>

<%  String thisNavHighlight = "";
    if ( request.getParameter("navHighLight") != null ) {
      thisNavHighlight = request.getParameter("navHighLight") ;  // used for header_main
    }

    String clearGif =  response.encodeURL("images/clear.gif");

    String mode          = "1";          // used for sub page high light
    String currPid       = "1";          // used for sidebar sub selection
                                         // this copies an index array to a rendering array 
                                         // that provides the side links
    String outputStr     = "";           // the output for header and sidebar includes

    if ( request.getParameter("mode") != null) {
       mode = request.getParameter("mode");
    } else {
       mode = "1"; // default to available gears
    }
    String greetingName = "";
%>



<%@ include file="nav_header_main.jspf" %>




</body>
</html>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/admin.war/frame_header.jsp#2 $$Change: 651448 $--%>
