<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<%@ taglib uri="/calendar-taglib" prefix="calendar" %>

<paf:includeOnly/>
<dsp:page>

<paf:InitializeGearEnvironment id="pafEnv">

<%@ page import="java.io.*,java.util.*,atg.portal.gear.calendar.CalendarConstants" %>
<%@ page import="java.util.Calendar" %>

<dsp:importbean bean="/atg/portal/gear/calendar/EventFormHandler"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<i18n:bundle baseName="atg.portal.gear.calendar.CalendarResources" localeAttribute="userLocale" changeResponseLocale="false"/>

<i18n:message id="eventTypeLabel" key="eventTypeLabel"/>
<i18n:message id="publicLabel" key="publicLabel"/>
<i18n:message id="privateLabel" key="privateLabel"/>
<i18n:message id="saveButtonLabel" key="saveButtonLabel"/>
<i18n:message id="startDateLabel" key="startDateLabel" />
<i18n:message id="startTimeLabel" key="startTimeLabel" />
<i18n:message id="amLabel" key="amLabel" />
<i18n:message id="pmLabel" key="pmLabel" />
<i18n:message id="endDateLabel" key="endDateLabel" />
<i18n:message id="endTimeLabel" key="endTimeLabel" />
<i18n:message id="timeZoneLabel" key="timeZoneLabel" />
<i18n:message id="eventNameLabel" key="eventNameLabel" />
<i18n:message id="eventDescriptionLabel" key="eventDescriptionLabel" />
<i18n:message id="required1" key="required1" />
<i18n:message id="backtocalendarLink" key="backtocalendarLink" />
<i18n:message id="newEventTitle" key="newEventTitle" />
<i18n:message id="newEventFeedbackMsg" key="newEventFeedbackMsg" />
<i18n:message id="month1" key="month1" />
<i18n:message id="month2" key="month2" />
<i18n:message id="month3" key="month3" />
<i18n:message id="month4" key="month4" />
<i18n:message id="month5" key="month5" />
<i18n:message id="month6" key="month6" />
<i18n:message id="month7" key="month7" />
<i18n:message id="month8" key="month8" />
<i18n:message id="month9" key="month9" />
<i18n:message id="month10" key="month10" />
<i18n:message id="month11" key="month11" />
<i18n:message id="month12" key="month12" />
<i18n:message id="minuteVal00" key="minuteVal00" />
<i18n:message id="minuteVal15" key="minuteVal15" />
<i18n:message id="minuteVal30" key="minuteVal30" />
<i18n:message id="minuteVal45" key="minuteVal45" />
<i18n:message id="dateFormat" key="date-format" />
<i18n:message id="dateHelperText" key="date-format-helper-text" />
<i18n:message id="noTimeLabel" key="noTimeLabel" />


  <%
    String gearId=pafEnv.getGear().getId();
    atg.portal.framework.ColorPalette cp = pafEnv.getPage().getColorPalette();
    String titleBGColor = cp.getHighlightBackgroundColor();
    String titleTextColor = cp.getHighlightTextColor();
    String gearBGColor = cp.getGearBackgroundColor();
    String gearTextColor = cp.getGearTextColor();

    String infoGifURL = pafEnv.getGear().getServletContext() + "/images/info.gif";
    String yearRangeParam=pafEnv.getGear().getGearInstanceParameter("yearEndRange");
    int yearEndRange=2005;
    if (yearRangeParam!=null)
       yearEndRange=Integer.parseInt(yearRangeParam);


     // use these to select today's date by default if not set
     Calendar rightNow = Calendar.getInstance();
     int currentDay=rightNow.get(Calendar.DAY_OF_MONTH);
     int currentMonth=rightNow.get(Calendar.MONTH);
     int currentYear=rightNow.get(Calendar.YEAR);

     boolean hasPublicEventAccess=false;
  %>

<dsp:form name="EventForm" action="<%= pafEnv.getOriginalRequestURI() %>" method="POST" >

<input type="hidden" name="action" value="create_event">
<input type="hidden" name="paf_dm" value="full">
<input type="hidden" name="paf_gear_id" value="<%= gearId %>">
<input type="hidden" name="paf_community_id" value="<%= pafEnv.getCommunity().getId() %>">
<input type="hidden" name="formSubmit" value="true"/>
<dsp:input type="hidden" bean="EventFormHandler.gearId" value="<%=gearId%>" />

<core:createUrl id="successUrl" url="<%= pafEnv.getOriginalRequestURI() %>" >
    <core:UrlParam param="action" value="create_event"/>
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearId %>" />
    <core:UrlParam param="eventType" value="base-event" />
    <core:UrlParam param="formSubmit" value="true"/>
    <dsp:input type="HIDDEN" bean="EventFormHandler.successUrl"
 value="<%= successUrl.getNewUrl() %>" />
</core:createUrl>
<core:createUrl id="failureUrl" url="<%= pafEnv.getOriginalRequestURI() %>" >
    <core:UrlParam param="action" value="create_event"/>
    <core:UrlParam param="paf_dm" value="full"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearId %>" />
    <core:UrlParam param="eventType" value="base-event" />
    <dsp:input type="HIDDEN" bean="EventFormHandler.failureUrl"
 value="<%= failureUrl.getNewUrl() %>" />
</core:createUrl>
<core:createUrl id="cancelUrl" url="<%= pafEnv.getOriginalRequestURI() %>" >
    <core:UrlParam param="paf_dm" value="shared"/>
    <dsp:input type="hidden" bean="EventFormHandler.cancelURL" value="<%=cancelUrl.getNewUrl()%>" />
</core:createUrl>


<table width="500" border="0" cellpadding="3" cellspacing="0" class="small">						
   <tr valign="top" align="left">
    	<td colspan="2" class="small_bold"><%=newEventTitle%></td>
	<%-- removing this link - redundant with breadcrumb link
        <core:createUrl id="backUrl" url="<%= pafEnv.getOriginalRequestURI() %>" >
            <core:UrlParam param="paf_dm" value="shared"/>
            <td><a href="<%=backUrl.getNewUrl()%>"><%=backtocalendarLink%></a></td>
        </core:createUrl>
	--%>
	<td>&nbsp;</td>
   </tr>
   <tr><td height="5"><spacer type="block" width="1" height="5"></td></tr>

<%-- error msg --%>
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
    <core:ifNotNull value='<%=request.getParameter("formSubmit")%>'>
     <tr><td colspan="2">
      &nbsp;<img src="<%=infoGifURL%>">&nbsp;&nbsp;<font class="small"><%=newEventFeedbackMsg%></font>
     </td></tr>
    </core:ifNotNull>
  </dsp:oparam>
</dsp:droplet>
 <%-- end of error msg --%>
	    
<font class="small">
    <tr><td colspan="3"><hr></td></tr>

  <%-- check for access level to create public events (must have admin role) --%>
  <calendar:hasAccess gearEnv="<%=pafEnv%>" isPublic="<%=true%>">
  <% hasPublicEventAccess=true;  // we check this below %>
    <tr>
    	<td width="5">*</td>
          <td nowrap><font class="small" color="#<%= gearTextColor %>"><%=eventTypeLabel%>:</font></td>
	  <td>
        <span class="inputbutton"><dsp:input type="RADIO" bean="EventFormHandler.publicEvent" value="false" /></span><%=privateLabel%><br><span class="inputbutton"><dsp:input type="RADIO" bean="EventFormHandler.publicEvent" value="true" /></span><%=publicLabel%> &nbsp;&nbsp;
    </td>
    </tr>
  </calendar:hasAccess>
  <core:ifNot value="<%=hasPublicEventAccess%>">
        <dsp:input type="hidden" bean="EventFormHandler.publicEvent" value="false" />
  </core:ifNot>

    <tr>
      <td width="5">*</td>
      <td nowrap><%=startDateLabel%>:&nbsp;<%=dateHelperText%></td>
      <td nowrap>
	   <dsp:input bean="EventFormHandler.eventStartDate"  date="<%=dateFormat%>"/>
      </td>
    </tr>

    <tr>
      <td width="5">&nbsp;</td>
      <td nowrap><%=noTimeLabel%></td>
      <td nowrap>
	   <dsp:input bean="EventFormHandler.ignoreTime" type="checkbox"/>
      </td>
    </tr>

    <tr valign="top" align="left">
    	<td width="5">*</td>
	<td nowrap><%=startTimeLabel%>:</td>
    	<td>
	 <dsp:select bean="EventFormHandler.startHour">
	    <% for(int i=1; i <= 12; ++i) { %>
	       <dsp:option value="<%=Integer.toString(i)%>"/><%=Integer.toString(i)%>
	    <% } %>
	 </dsp:select>
        &nbsp;:&nbsp;

        <%-- startMinute should be a required field, but there is an apparent bug
             in the DSP tag library (51275) where the "required" tag converter causes the
             number="<format"> tag converter not to work.  So for now the form
             handler is enforcing the requiredness of this field. --%>

			<dsp:select bean="EventFormHandler.startMinute">
			<dsp:option value="00"/><%=minuteVal00%>
			<dsp:option value="15"/><%=minuteVal15%>
			<dsp:option value="30"/><%=minuteVal30%>
			<dsp:option value="45"/><%=minuteVal45%>
			</dsp:select>

        <span class="inputbutton"><dsp:input type="RADIO" bean="EventFormHandler.startAmPm" value="AM" /></span>
        	<%=amLabel%> &nbsp;&nbsp;
        <span class="inputbutton"><dsp:input type="RADIO" bean="EventFormHandler.startAmPm" value="PM" /></span>
        	<%=pmLabel%>
    	</td>
    </tr>

    <tr>
      <td width="5">*</td>
      <td nowrap><%=endDateLabel%>:&nbsp;<%=dateHelperText%></td>
      <td nowrap>
         <dsp:input bean="EventFormHandler.eventEndDate"  date="<%=dateFormat%>"/>
      </td>
    </tr>

    <tr valign="top" align="left">
    	<td width="5">*</td>
    	<td nowrap><%=endTimeLabel%>:</td>
    	<td>
	 <dsp:select bean="EventFormHandler.EndHour">
	    <% for(int i=1; i <= 12; ++i) { %>
	       <dsp:option value="<%=Integer.toString(i)%>"/><%=Integer.toString(i)%>
	    <% } %>
	 </dsp:select>
        &nbsp;:&nbsp;

        <%-- endMinute should be a required field, but there is an apparent bug
             in the DSP tag library where the "required" tag converter causes the
             number="<format"> tag converter not to work.  So for now the form
             handler is enforcing the requiredness of this field. --%>

			<dsp:select bean="EventFormHandler.endMinute">
			<dsp:option value="00"/><%=minuteVal00%>
			<dsp:option value="15"/><%=minuteVal15%>
			<dsp:option value="30"/><%=minuteVal30%>
			<dsp:option value="45"/><%=minuteVal45%>
			</dsp:select>

        <span class="inputbutton"><dsp:input type="RADIO" bean="EventFormHandler.endAmPm" value="AM" /></span>
        <%=amLabel%> &nbsp;&nbsp;
        <span class="inputbutton"><dsp:input type="RADIO" bean="EventFormHandler.endAmPm" value="PM" /></span>
        <%=pmLabel%>
    	</td>
    </tr>
    <%-- taking time zone out for now
	<tr valign="top" align="left">
    	<td width="5">*</td>
    	<td nowrap><%=timeZoneLabel%>:</td>
    	<td>
			<dsp:select bean="EventFormHandler.timeZone">
			<dsp:option value="EST"/>EST
			<dsp:option value="CST"/>CST
			<dsp:option value="MST"/>MST
			<dsp:option value="PST"/>PST
			</dsp:select>
		</td>
    </tr>
    --%>


    <tr valign="top" align="left">
    	<td width="5">*</td>
    	<td nowrap><%=eventNameLabel%>:</td>
    	<td><dsp:input type="TEXT" bean="EventFormHandler.eventName" size="25" maxlength="50" required="<%=true%>" /></td>
    </tr>
    <tr valign="top" align="left">
    	<td width="5">&nbsp;</td>
    	<td nowrap><%=eventDescriptionLabel%>:</td>
    	<td><dsp:textarea type="TEXT" bean="EventFormHandler.description" rows="10" cols="25"><dsp:valueof bean="EventFormHandler.description" /></dsp:textarea></td>
    </tr>


    <tr valign="top" align="left">
    	<td width="5">&nbsp;</td>
    	<td>&nbsp;</td>
			<td><br>
        <dsp:input type="submit" value="<%=saveButtonLabel%>" bean="EventFormHandler.create" />
	&nbsp;&nbsp;
    	</td>
    </tr>
     <tr valign="top" align="left">
    	<td width="5">&nbsp;</td>
    	<td>&nbsp;</td>
    	<td>* <%=required1%></td>
    </tr>	
   </font>
</table>

</dsp:form>

<!-- JavaScript to set end date to start date -->

<SCRIPT>
function setEndMonth(evnt)
{
  document.forms.EventForm.EventEndMonth.value=
  document.forms.EventForm.EventStartMonth.value;
  return true;
}
function setEndDay(evnt)
{
  document.forms.EventForm.EventEndDay.value=
  document.forms.EventForm.EventStartDay.value;
  return true;
}
function setEndYear(evnt)
{
  document.forms.EventForm.EventEndYear.value=
  document.forms.EventForm.EventStartYear.value;
  return true;
}

</SCRIPT>
<%--  Taking these out, as it's causing a problem between forms
document.forms.EventForm.EventStartMonth.onchange=setEndMonth;
document.forms.EventForm.EventStartDay.onchange=setEndDay;
document.forms.EventForm.EventStartYear.onchange=setEndYear;
--%>

</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/calendar/calendar.war/html/createEvent.jsp#2 $$Change: 651448 $--%>
