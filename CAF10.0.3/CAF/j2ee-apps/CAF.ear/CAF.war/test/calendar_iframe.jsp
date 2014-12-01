<%@ taglib prefix="caf" uri="http://www.atg.com/taglibs/caf" %> 

<html>
<head>
<title>Calendar iFrame Test Page</title>
<meta http-equiv="Content-Type" content="text/html; charset= iso-8859-1" />
<script language="Javascript" src="../scripts/calendar_date.js"></script>
<script language="Javascript" src="../scripts/calendar_functions.js"></script>
<script language="Javascript" src="../scripts/calendar_html.js"></script>
<script language="Javascript" src="../scripts/calendar_validation.js"></script>
<script language="Javascript" src="../scripts/atg-ui_validation.js"></script>
<link rel="stylesheet" href="../css/calendar.css" type="text/css">
</head>

<body style="margin:0">

<caf:container containerId="calendarContainerIFrame" iframe="true" src="calendar_iframe_contents.jsp" />
<caf:curtain curtainId="calendarCurtainIframe" iframe="true" />
<caf:curtain curtainId="calendarCurtain" />

<br>
&nbsp;&nbsp;<span class="atgCalendar_Text"><b>ATG Calendar Sample Page</b></span>

<br>
<div style="margin-top:20px;margin-left:200px">
<span class="atgCalendar_Text">Date:</span>
<input id="calendarInput" type="text" size="10">
<img id="calendarAnchor" src="../images/calendar/calendar2_off.gif" style="cursor:pointer" onclick="atgCalendar_openCalendarIFrame('calendarContainerIFrame')">
</div>


</body>
</html>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/calendar_iframe.jsp#2 $$Change: 651448 $--%>
