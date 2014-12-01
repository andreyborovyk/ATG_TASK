<!-- Begin calendar gear display -->
<%@ taglib uri="/calendar-taglib" prefix="calendar" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<core:demarcateTransaction id="calendarFullXA">

<paf:InitializeGearEnvironment id="pafEnv">

<%@ page import="java.io.*,java.util.*,atg.portal.gear.calendar.CalendarConstants" %>
<%
  // determine what type of event we have, based on url param, default to "detail"
  // for backward compatibility
  String eventTypeParam="eventType";
  String baseEventParamVal="base-event";
  String detailEventParamVal="detail-event";
  String thisEventType=request.getParameter(eventTypeParam);
  if (thisEventType==null) 
     thisEventType=detailEventParamVal;

 %>
 
<dsp:page>

  <core:IfNotNull value='<%= request.getParameter("action")%>'>
     <core:ExclusiveIf>
	<core:If value='<%= request.getParameter("action").equals("create_event")%>'>
  	  <calendar:hasAccess gearEnv="<%=pafEnv%>" isPublic="<%=false%>">
            <core:ExclusiveIf>
	      <core:If value='<%= thisEventType.equals(baseEventParamVal)%>'>
	        <jsp:include page="createEvent.jsp" flush="true"/>
	      </core:If>    
	      <core:If value='<%= thisEventType.equals(detailEventParamVal)%>'>
	        <jsp:include page="createDetailEvent.jsp" flush="true"/>
	      </core:If>    
            </core:ExclusiveIf>
  	  </calendar:hasAccess>
	</core:If>    

	<core:If value='<%= request.getParameter("action").equals("show_event")%>'>
	      <jsp:include page="showEvent.jsp" flush="true"/>
	</core:If>    

	<core:If value='<%= request.getParameter("action").equals("edit_event")%>'>
  	  <calendar:hasAccess gearEnv="<%=pafEnv%>" isPublic="<%=false%>">
            <core:ExclusiveIf>
	      <core:If value='<%= thisEventType.equals(baseEventParamVal)%>'>
	        <jsp:include page="editBaseEvent.jsp" flush="true"/>
	      </core:If>    
	      <core:If value='<%= thisEventType.equals(detailEventParamVal)%>'>
	        <jsp:include page="editDetailEvent.jsp" flush="true"/>
	      </core:If>    
            </core:ExclusiveIf>
  	  </calendar:hasAccess>
	</core:If>    
     </core:ExclusiveIf>
  </core:IfNotNull> 

</dsp:page>
</paf:InitializeGearEnvironment>

</core:demarcateTransaction>
<!-- End calendar gear display -->
<%-- @version $Id: //app/portal/version/10.0.3/calendar/calendar.war/html/calendarFull.jsp#2 $$Change: 651448 $--%>
