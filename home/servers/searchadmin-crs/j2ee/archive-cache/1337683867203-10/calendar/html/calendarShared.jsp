<!-- Begin calendar Gear display -->
<%@ taglib uri="/calendar-taglib" prefix="calendar" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<paf:InitializeGearEnvironment id="pafEnv">
<dsp:page>
   <dsp:include page="monthView.jsp"/>
</dsp:page>
</paf:InitializeGearEnvironment>



<%-- @version $Id: //app/portal/version/10.0.3/calendar/calendar.war/html/calendarShared.jsp#2 $$Change: 651448 $--%>
