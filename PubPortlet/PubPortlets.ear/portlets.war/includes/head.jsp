<!-- BEGIN FILE head.jsp -->
<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
  <dspel:page>
	<style type="text/css" media="all">
	@import url("<c:url value='/templates/style/css/style.jsp' context="${initParam['atg.bizui.ContextPath']}"/>");
	</style>

	<link rel="stylesheet" href="<c:url value='/templates/style/css/calendar.jsp' context="${initParam['atg.bizui.ContextPath']}"/>" type="text/css"/>

	<script language="JavaScript" type="text/javascript" src="<c:url value='/templates/page/html/scripts/scripts.js' context="${initParam['atg.bizui.ContextPath']}"/>"></script>
	<script language="Javascript" type="text/javascript" src="<c:url value='/templates/page/html/scripts/calendar_date.js' context="${initParam['atg.bizui.ContextPath']}"/>"></script>
	<script language="Javascript" type="text/javascript" src="<c:url value='/templates/page/html/scripts/calendar_validation.js' context="${initParam['atg.bizui.ContextPath']}"/>"></script>
	<script language="Javascript" type="text/javascript" src="<c:url value='/templates/page/html/scripts/calendar_functions.js' context="${initParam['atg.bizui.ContextPath']}"/>"></script>
	<script language="Javascript" type="text/javascript" src="<c:url value='/templates/page/html/scripts/calendar_html.js' context="${initParam['atg.bizui.ContextPath']}"/>"></script>
	<script language="Javascript" type="text/javascript" src="<c:url value='/templates/page/html/scripts/calendarInstance1.js' context="${initParam['atg.bizui.ContextPath']}"/>"></script>
	<script language="Javascript" type="text/javascript" src="<c:url value='/templates/page/html/scripts/calendarInstance2.js' context="${initParam['atg.bizui.ContextPath']}"/>"></script>
	<script language="Javascript" type="text/javascript" src="<c:url value='/templates/page/html/scripts/calendarStyles1.js' context="${initParam['atg.bizui.ContextPath']}"/>"></script>
 <%@ include file="calendarI18n.jspf" %>
  </dspel:page>
<!-- END FILE head.jsp -->
<%-- @version $Id: //product/PubPortlet/version/10.0.3/portlets.war/includes/head.jsp#2 $$Change: 651448 $--%>
