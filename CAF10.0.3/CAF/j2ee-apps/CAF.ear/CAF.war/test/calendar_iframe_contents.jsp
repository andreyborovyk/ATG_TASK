<%@ taglib prefix="caf" uri="http://www.atg.com/taglibs/caf" %> 

<html>
<head>
<title>Calendar iFrame Page</title>
<meta http-equiv="Content-Type" content="text/html; charset= iso-8859-1" />
<script type="text/javascript" src="../scripts/calendar_date.js"></script>
<script type="text/javascript" src="../scripts/calendar_functions.js"></script>
<script type="text/javascript" src="../scripts/calendar_html.js"></script>
<script type="text/javascript" src="../scripts/calendar_validation.js"></script>
<script type="text/javascript" src="../scripts/atg-ui_validation.js"></script>
<link rel="stylesheet" href="../css/calendar.css" type="text/css">

<script type="text/javascript">
function atgCalendar_associateCalendarIFrame(iFrameId) {
}

function atgCalendar_openIFrame(iFrameId) {
  calendarInstance.iFrameId = iFrameId;
  atgCalendar_openCalendar(calendarInstance);
}
</script>

</head>

<body style="margin:0">

<caf:container containerId="calendarContainerDiv" />

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

</body>
</html>
<%-- @version $Id: //application/CAF/version/10.0.3/src/web-apps/CAF/test/calendar_iframe_contents.jsp#2 $$Change: 651448 $--%>
