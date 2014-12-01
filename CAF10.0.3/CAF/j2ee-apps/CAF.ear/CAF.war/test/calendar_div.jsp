<%@ taglib prefix="caf" uri="http://www.atg.com/taglibs/caf" %> 

<html>
<head>
<title>Calendar Div Test Page</title>
<meta http-equiv="Content-Type" content="text/html; charset= iso-8859-1" />
<script type="text/javascript" src="../scripts/calendar_date.js"></script>
<script type="text/javascript" src="../scripts/calendar_functions.js"></script>
<script type="text/javascript" src="../scripts/calendar_html.js"></script>
<script type="text/javascript" src="../scripts/calendar_validation.js"></script>
<script type="text/javascript" src="../scripts/atg-ui_validation.js"></script>
<link rel="stylesheet" href="../css/calendar.css" type="text/css">

</head>

<body style="margin:0">

<caf:container containerId="calendarContainerDiv" />

<caf:curtain curtainId="calendarCurtain" />

<caf:calendarInstance 
  anchorId="calendarAnchor"
  autoFlip="true"
  calendarInstanceId="calendarInstance" 
  clearDateOnError="true"
  closeAfterSelection="true"
  containerId="calendarContainerDiv"
  curtainId="calendarCurtain"
  dateInputId="calendarInput"
  hasClose="true"
  hasToday="true"
  horizontalOffset="18"
  i18nId="calendarI18n"
  setDateOnCalendarOpen="true"
  stylesId="calendarStyles"
  verticalOffset="0"
 />

<caf:calendarI18n calendarI18nId="calendarI18n" />

<caf:calendarStyles calendarStylesId="calendarStyles" />

<br>
&nbsp;&nbsp;<span class="atgCalendar_Text"><b>ATG Calendar Sample Page</b></span>

<br>
<div style="margin-top:20px;margin-left:200px">
<span class="atgCalendar_Text">Date:</span>
<input id="calendarInput" type="text" size="10">
<img id="calendarAnchor" src="../images/calendar/calendar2_off.gif" style="cursor:pointer" onclick="atgCalendar_openCalendar(calendarInstance)">
</div>

</body>
</html>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/calendar_div.jsp#2 $$Change: 651448 $--%>
