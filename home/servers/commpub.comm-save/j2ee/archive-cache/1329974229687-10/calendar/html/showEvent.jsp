<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/calendar-taglib" prefix="calendar" %>

<paf:InitializeGearEnvironment id="pafEnv">
<dsp:page>
<i18n:bundle baseName="atg.portal.gear.calendar.CalendarResources" localeAttribute="userLocale" changeResponseLocale="false"/>

<paf:setFrameworkLocale/>


<%@ page import="java.io.*,java.util.*,atg.portal.gear.calendar.CalendarConstants" %>

<dsp:importbean bean="/atg/portal/gear/calendar/EventLookupDroplet" />
<dsp:importbean bean="/atg/portal/gear/calendar/EventFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />

<%
    String eventId = request.getParameter("event_id");
    String currentUserId=null;

    atg.portal.framework.ColorPalette cp = pafEnv.getPage().getColorPalette();
    String titleBGColor = cp.getHighlightBackgroundColor();
    String titleTextColor = cp.getHighlightTextColor();
    String gearBGColor = cp.getGearBackgroundColor();
    String gearTextColor = cp.getGearTextColor();

     String imageDocRoot = pafEnv.getGear().getServletContext() + "/images/";
     String clearGifUrl = imageDocRoot + "clear.gif";
     String infoGifURL = imageDocRoot + "info.gif";

     // determine what type of event we have, based on url param, default to "detail"
     // for backward compatibility
     String eventTypeParam="eventType";
     String baseEventParamVal="base-event";
     String detailEventParamVal="detail-event";
     String thisEventType=request.getParameter(eventTypeParam);
     if (thisEventType==null)
        thisEventType=detailEventParamVal;

     // this gets set to false when the event has been deleted, so we don't attempt
     // to retreive it
     boolean showEvent=true;
%>

<%-- Get the user's (profile) ID from the Nucleus component for the user profile --%>
<dsp:getvalueof id="profileId" bean="Profile.id" idtype="java.lang.String">
  <% currentUserId=profileId; %>
</dsp:getvalueof>

<i18n:message id="eventTypeLabel" key="eventTypeLabel"/>
<i18n:message id="publicLabel" key="publicLabel"/>
<i18n:message id="privateLabel" key="privateLabel"/>
<i18n:message id="eventDetailTitle" key="eventDetailTitle"/>
<i18n:message id="eventNameLabel" key="eventNameLabel"/>
<i18n:message id="eventDescriptionLabel" key="eventDescriptionLabel"/>
<i18n:message id="eventStartTimeLabel" key="eventStartTimeLabel"/>
<i18n:message id="eventEndTimeLabel" key="eventEndTimeLabel"/>
<i18n:message id="startDateLabel" key="startDateLabel"/>
<i18n:message id="endDateLabel" key="endDateLabel"/>
<i18n:message id="editButtonLabel" key="editButtonLabel"/>
<i18n:message id="deleteButtonLabel" key="deleteButtonLabel"/>
<i18n:message id="deleteEventFeedbackMsg" key="deleteEventFeedbackMsg"/>
<i18n:message id="deleteConfirmationMessage" key="deleteConfirmationMessage"/>
<i18n:message id="noTimeLabelDisplay" key="noTimeLabelDisplay"/>

<%!
  // Utility method used when displaying event info:

  private boolean emptyOrNull(Object pText)
  {
    if (pText == null)
      return true;

    if (! (pText instanceof String))
      return false;

    return (((String) pText).length() == 0);
  }
%>


<table width="100%" border="0" cellpadding="3" cellspacing="0" class="small">
<tr>
  <td colspan="3"><font class="small" color="#<%= gearTextColor %>"><b><%=eventDetailTitle%></b></font></td>
</tr>

<%-- error/feedback msg --%>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="EventFormHandler.formError"/>
  <dsp:oparam name="true">
    <font class="error"><UL>
      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="EventFormHandler.formExceptions"/>
        <dsp:oparam name="output">
	 <tr><td colspan="2">
          <LI> <dsp:valueof param="message"></dsp:valueof>
	 </td></tr>
        </dsp:oparam>
      </dsp:droplet>
    </UL></font>
  </dsp:oparam>
  <dsp:oparam name="false">
    <core:ifNotNull value='<%=request.getParameter("formSubmitDelete")%>'>
    <% showEvent=false; %>
     <tr><td colspan="2">
      &nbsp;<img src="<%=infoGifURL%>">&nbsp;&nbsp;<font class="small"><%=deleteEventFeedbackMsg%></font>
     </td></tr>
    </core:ifNotNull>
  </dsp:oparam>
</dsp:droplet>
 <%-- end of error msg --%>
	    
<tr>
  <td colspan="3"><img src="<%=clearGifUrl%>" height="5" width="1"></td>
</tr>

<core:if value="<%=showEvent%>">

<dsp:droplet name="EventLookupDroplet">
  <dsp:param name="id" value="<%= eventId %>" />
  <dsp:param name="elementName" value="eventParam" />
  <dsp:oparam name="output">

    <dsp:getvalueof id="thisEvent" param="eventParam"
     idtype="atg.repository.RepositoryItem">

      <core:If value='<%= !emptyOrNull(thisEvent.getPropertyValue("publicEvent")) %>'>
        <tr valign="top" align="left">
          <td width="5">&nbsp;</td>
          <td nowrap><font class="small" color="#<%= gearTextColor %>"><%=eventTypeLabel%>:</font></td>
	  <core:exclusiveIf>
	    <core:if value='<%=((Boolean) thisEvent.getPropertyValue("publicEvent")).booleanValue()%>'>
          <td><font class="small" color="#<%= gearTextColor %>"><%= publicLabel %></font></td>
	    </core:if>
	    <core:defaultCase>
          <td><font class="small" color="#<%= gearTextColor %>"><%= privateLabel %></font></td>
	    </core:defaultCase>
	  </core:exclusiveIf>
        </tr>
      </core:If>

      <tr valign="top" align="left">
      	<td width="5">&nbsp;</td>
      	<td nowrap width="150"><font class="small" color="#<%= gearTextColor %>"><%=eventNameLabel%>:</font></td>
      	<td width="345"><font class="small" color="#<%= gearTextColor %>"><%= thisEvent.getPropertyValue("name") %></font></td>
      </tr>

      <core:If value='<%= !emptyOrNull(thisEvent.getPropertyValue("description")) %>'>
        <tr valign="top" align="left">
          <td width="5">&nbsp;</td>
          <td nowrap><font class="small" color="#<%= gearTextColor %>"><%=eventDescriptionLabel%>:</font></td>
          <td><font class="small" color="#<%= gearTextColor %>"><%= thisEvent.getPropertyValue("description") %></font></td>
        </tr>
      </core:If>

      <tr valign="top" align="left">
      	<td width="5">&nbsp;</td>
	<core:exclusiveIf>
	  <core:if value='<%=((Boolean) thisEvent.getPropertyValue("ignoreTime")).booleanValue()%>'>
      	<td nowrap><font class="small" color="#<%= gearTextColor %>"><%=startDateLabel%>:</font></td>
      	<td><font class="small" color="#<%= gearTextColor %>">
	    <i18n:formatDate value='<%=(java.util.Date) thisEvent.getPropertyValue("startDate")%>' style="medium" />&nbsp;
	  </core:if>
	  <core:defaultCase>
      	<td nowrap><font class="small" color="#<%= gearTextColor %>"><%=eventStartTimeLabel%>:</font></td>
      	<td><font class="small" color="#<%= gearTextColor %>">
	    <i18n:formatDateTime value='<%= (java.util.Date) thisEvent.getPropertyValue("startTime")%>' dateStyle="medium" timeStyle="short" />&nbsp;
	  </core:defaultCase>
	</core:exclusiveIf>
	
	</font>
        </td>
      </tr>
      <tr valign="top" align="left">
        <td width="5">&nbsp;</td>
	<core:exclusiveIf>
	  <core:if value='<%=((Boolean) thisEvent.getPropertyValue("ignoreTime")).booleanValue()%>'>
        <td nowrap><font class="small" color="#<%= gearTextColor %>"><%=endDateLabel%>:</font></td>
      	<td><font class="small" color="#<%= gearTextColor %>">
	    <i18n:formatDate value='<%=(java.util.Date) thisEvent.getPropertyValue("endDate")%>' style="medium" />&nbsp;
	    <br><%=noTimeLabelDisplay%>
	  </core:if>
	  <core:defaultCase>
        <td nowrap><font class="small" color="#<%= gearTextColor %>"><%=eventEndTimeLabel%>:</font></td>
      	<td><font class="small" color="#<%= gearTextColor %>">
	    <i18n:formatDateTime value='<%= (java.util.Date) thisEvent.getPropertyValue("endTime")%>' dateStyle="medium" timeStyle="short" />&nbsp;
	  </core:defaultCase>
	</core:exclusiveIf>
	</font>
        </td>
      </tr>

      <%-- Show other event info based on event type --%>
      <core:ExclusiveIf>
        <core:If value='<%= thisEventType.equals(detailEventParamVal)%>'>
          <%@ include file="showDetailEvent.jspf" %>
        </core:If>
      </core:ExclusiveIf>

    <tr><td colspan="3" height="5"><spacer type="block" width="1" height="5"></td></tr>

</table>

<table align="center">
<%-- Check permissions for edit/delete - owner has access, admin has access for public events --%>
<%  boolean hasWriteAccess=false; %>
 <core:ifNotNull value='<%= ((String) thisEvent.getPropertyValue("owner")) %>'>
  <core:if value='<%= ((String) thisEvent.getPropertyValue("owner")).equals(currentUserId) %>'>
     <% hasWriteAccess=true; %>
  </core:if>
 </core:ifNotNull> 
 
 <core:ifNot value="<%=hasWriteAccess %>">
  <core:if value='<%=((Boolean) thisEvent.getPropertyValue("publicEvent")).booleanValue()%>'>
     <paf:hasGearRole roles="<%=CalendarConstants.ADMIN_ROLENAME%>">
       <% hasWriteAccess=true; %>
     </paf:hasGearRole>
  </core:if>
 </core:ifNot>

 <%-- Edit and Delete buttons --%>
 <core:if value="<%=hasWriteAccess%>">
  <tr>
    <td>
        <form action="<%= pafEnv.getOriginalRequestURI() %>">
          <input type="hidden" name="paf_dm" value="full">
          <input type="hidden" name="action" value="edit_event">
          <input type="hidden" name="<%=eventTypeParam%>" value="<%=thisEventType%>">
          <input type="hidden" name="eventId" value="<%=eventId%>">
          <input type="hidden" name="paf_gear_id"
               value="<%= pafEnv.getGear().getId() %>">
          <input type="hidden" name="sharedMonthYear"
               value='<%=request.getParameter("sharedMonthYear")%>' >
	  <input type="hidden" name="paf_community_id" value="<%= pafEnv.getCommunity().getId() %>">
          <input type="submit" name=editEvent
            value="<%=editButtonLabel%>" >
        </form>
      &nbsp;
    </td>
    <td>
        <dsp:form name="deleteForm" action="<%= pafEnv.getOriginalRequestURI() %>" 
			method="POST" 
			onclick="return doubleCheck()">
          <input type="hidden" name="paf_dm" value="full">
          <input type="hidden" name="action" value="show_event">
          <input type="hidden" name="<%=eventTypeParam%>" value="<%=thisEventType%>">
          <input type="hidden" name="eventId" value="<%=eventId%>">
          <input type="hidden" name="formSubmitDelete" value="true">
          <input type="hidden" name="paf_gear_id" value="<%= pafEnv.getGear().getId() %>">
	  <input type="hidden" name="paf_community_id" value="<%= pafEnv.getCommunity().getId() %>">
          <input type="hidden" name="sharedMonthYear" value='<%=request.getParameter("sharedMonthYear")%>' >
          <dsp:input type="HIDDEN" bean="EventFormHandler.eventId" value="<%= eventId %>" />
          <dsp:input type="HIDDEN" bean="EventFormHandler.eventType" value="<%= thisEventType %>" />
        <dsp:input type="submit" value="<%=deleteButtonLabel%>" bean="EventFormHandler.delete" />
        </dsp:form>
      &nbsp;
    </td>
  </tr>
 </core:if>

 <calendar:sendViewedMessage env="<%=pafEnv%>" 
 			     eventId="<%=eventId%>"
			     eventName='<%=(String)thisEvent.getPropertyValue("name")%>'
	  		     ignoreTime='<%=((Boolean) thisEvent.getPropertyValue("ignoreTime")).booleanValue()%>'
			     startDate='<%=(java.util.Date) thisEvent.getPropertyValue("startTime")%>'/>
	  
 				


</dsp:getvalueof>  <!-- eventParam / thisEvent -->
</dsp:oparam>  <!-- output oparam for EventLookupDroplet -->

  <dsp:oparam name="empty">
    <tr align="left"><td colspan="3">No information was found for this event.<br><br></td></tr>

  </dsp:oparam>
  <dsp:oparam name="error">
    <tr align="left"><td colspan="3">There was an error in attempting to get information for this event.<br><br></td></tr>
  </dsp:oparam>

</dsp:droplet> <!-- EventLookupDroplet -->

 </core:if>  <%-- if showEvent --%>
</table>

<script>
<!---
function doubleCheck(f) {
  return confirm("<%=deleteConfirmationMessage%>")
}
-->
</script>
</dsp:page>
</paf:InitializeGearEnvironment>
<%-- @version $Id: //app/portal/version/10.0.3/calendar/calendar.war/html/showEvent.jsp#2 $$Change: 651448 $--%>
