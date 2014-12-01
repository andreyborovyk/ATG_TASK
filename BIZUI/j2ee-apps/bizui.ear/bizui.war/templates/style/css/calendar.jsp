<%@ page contentType="text/css" %>
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dsp"     uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<%
  int ONE_DAY_MILLIS = 1000 * 60 * 60 * 24;
  int ONE_DAY_SECONDS = 60 * 60 * 24;
  long now = System.currentTimeMillis();
  ((HttpServletResponse)response).setDateHeader("Last-Modified", now);
  now += ONE_DAY_MILLIS; // 7 days from now
  ((HttpServletResponse)response).setDateHeader("Expires", now);
  ((HttpServletResponse)response).setHeader("Cache-Control", "max-age=" + ONE_DAY_SECONDS);
%>

<c:set var="contextPath"><%= request.getContextPath() %></c:set>

.atgHTML_div {
	display: block;	
	position: absolute;
	z-index: 200;
	filter:progid:DXImageTransform.Microsoft.Shadow(color="#000000", Direction=135, Strength=3);
}

.atgHTML_iframe {
	display: block;	
	position: absolute;
	z-index: 200;
	filter:progid:DXImageTransform.Microsoft.Shadow(color="#000000", Direction=135, Strength=3);
}

.atgCalendar_divHide {
	display: none;
	position: absolute;
	z-index: 10;
}

.atgCalendar_divShow {
	display: block;	
	position: absolute;
	z-index: 200;
	filter:progid:DXImageTransform.Microsoft.Shadow(color="#000000", Direction=135, Strength=3);
}

.atgCalendar_container {
	width: 210px;
	z-index: 200;
}

.atgCalendar_curtain {
	width: 100%;
	height: 100%;
	background-color: #CCC;
	opacity: .5;
	-moz-opacity: .5;
	filter: alpha(opacity=50);
	z-index: 100;
	display: none;
	position: absolute;
}

.atgCalendar_monthContainer {
   background-image: url(<c:url value="/images/calendar/sidecolumnheader.gif" context="${contextPath}"/>);
   text-align: center;
   padding-left: 6px;
   padding-right: 6px;
}

.atgCalendar_previousMonthRow {
	width: 50%;
	empty-cells: show;
	text-align: left;
	white-space: nowrap;
}

.atgCalendar_monthRow {
   vertical-align: top;
}

.atgCalendar_nextMonthRow {
	width: 50%;
	empty-cells: show;
	text-align: right;
	white-space: nowrap;
}

.atgCalendar_dateLabels  {
   font-family: "Trebuchet MS", Verdana, Helvetica, sans-serif;
   font-size: 10px;
   font-weight: bold;
   text-align: center;
   height: 20px;
   vertical-align: bottom;
}

.atgCalendar_dateLabelsWeekend  {
	font-family: "Trebuchet MS", Verdana, Helvetica, sans-serif;
	font-size: 10px;
	font-weight: bold;
	text-align: center;
	color: #1964C5;
   height: 20px;
	vertical-align: bottom;
}

.atgCalendar_numerics  {
	height: 15px;
   width: 14%;
   font-family: Verdana;
	font-size: 10px;
	text-align: center;
	vertical-align: middle;
	cursor: hand;
}

.atgCalendar_numericsWeekend  {
	height: 15px;
   width: 14%;
	font-family: Verdana;
	font-size: 10px;
	text-align: center;
	color: #1964C5;
	vertical-align: middle;
	cursor: hand;
}

.atgCalendar_numericsWeekendToday  {
	height: 15px;
   width: 14%;
	font-family: Verdana;
	font-size: 10px;
	text-align: center;
	color: #1964C5;
	vertical-align: middle;
	cursor: hand;
	border: 1px;
	border-style: solid;
	border-color: #6666CC;
}

.atgCalendar_numericsToday  {
	height: 15px;
   width: 14%;
	font-family: Verdana;
	font-size: 10px;
	text-align: center;
	vertical-align: middle;
	cursor: hand;
	border: 1px;
	border-style: solid;
	border-color: #557AA1;
}

.atgCalendar_numericsSelected  {
   height: 15px;
   width: 14%;
   font-family: Verdana;
   font-size: 10px;
   text-align: center;
   border: 1px;
   border-style: solid;
   border-color: #557AA1;
   background-color: #6699CC;
   cursor: hand;
}

.atgCalendar_numericsOff  {
	width: 14%;
   font-family: Verdana;
	color: #CCCCCC;
	font-size: 10px;
	text-align: center;
}

.atgCalendar_borderTopLeft {
	background-repeat: repeat-y;
	background-position: left;
	background-color: #CCCCCC;
	width: 1px;
	height: 1px;
	empty-cells: show;
}

.atgCalendar_borderTopMiddle {
	background-repeat: repeat-x;
	background-color: #CCCCCC;
	width: 100%;
	height: 1px;
	empty-cells: show;
}

.atgCalendar_borderTopRight {
	background-repeat: repeat-y;
	background-position: right;
	background-color: #CCCCCC;
	width: 1px;
	height: 1px;
	empty-cells: show;
}

.atgCalendar_borderBottomLeft {
	background-repeat: no-repeat;
	background-position: left;
	background-color: #CCCCCC;
	width: 1px;
	height: 1px;
	empty-cells: show;
}

.atgCalendar_borderBottomMiddle {
	background-repeat: repeat-x;
	background-color: #CCCCCC;
	width: 100%;
	height: 1px;
	empty-cells: show;
}

.atgCalendar_borderBottomRight {
	background-repeat: no-repeat;
	background-position: right;
	background-color: #CCCCCC;
	width: 1px;
	height: 1px;
	empty-cells: show;
}

.atgCalendar_left {
	background-repeat: repeat-y;
	background-color: #CCCCCC;
	width: 1px;
	height: 100%;
	empty-cells: show;
}

.atgCalendar_middle {
   background-color: #F0F0F0;
   border: 1px solid #CCCCCC;
}

.atgCalendar_right {
	background-repeat: repeat-y;
	background-color: #CCCCCC;
	width: 1px;
	height: 100%;
	empty-cells: show;
}

.atgCalendar_bottomSpacer{
	height: 5px;
	empty-cells: show;
}

.atgCalendar_cursor  {
	cursor: hand;
}

.atgCalendar_text  {
	font-family: "Trebuchet MS", Verdana, Helvetica, sans-serif;
	font-size: 11px;
	color: #6666CC;
}

.atgCalendar_titleText  {
	font-family: "Trebuchet MS", Verdana, Helvetica, sans-serif;
	font-weight: bold;
	font-size: 12px;
	color: #000000;
	text-align: top;
}

.atgCalendar_previousMonthIcon {
	background-image: url(<c:url value="/images/calendar/arrow_left.gif" context="${contextPath}"/>);
	width: 15px;
  background-repeat: no-repeat;
  background-position: left 1px;
	cursor: pointer;
}

.atgCalendar_nextMonthIcon {
	background-image: url(<c:url value="/images/calendar/arrow_right.gif" context="${contextPath}"/>);
	width: 15px;
  background-repeat: no-repeat;
	background-position: right 1px;
	cursor: pointer;
}

.atgCalendar_closeIcon {
	background-image: url(<c:url value="/images/calendar/close.gif" context="${contextPath}"/>);
	background-repeat: no-repeat;
	background-position: right center;
	height: 10px;
	width: 10px;
	cursor: pointer;
}

.atgCalendar_todayIcon {
	background-image: url(<c:url value="/images/calendar/calendar2_off.gif" context="${contextPath}"/>);
	background-repeat: no-repeat;
	background-position: left center;
	height: 10px;
	width: 20px;
	cursor: pointer;
}

.atgCalendar_additionalTodayRow {
	width: 50%;
	font-family: "Trebuchet MS", Verdana, Helvetica, sans-serif;
	font-weight: normal;
	font-size: 10px;
	color: #1964C5;
	padding-left: 8px;
	padding-top: 8px;
}

.atgCalendar_additionalCloseRow {
	width:50%;
	text-align: right;
	font-family: "Trebuchet MS", Verdana, Helvetica, sans-serif;
	font-weight: normal;
	font-size: 10px;
	color: #1964C5;
	padding-right: 10px;
	padding-top: 8px;
}

</dsp:page>

<%-- @version $Id: //product/BIZUI/version/10.0.3/web-apps/bizui/templates/style/css/calendar.jsp#2 $$Change: 651448 $--%>
