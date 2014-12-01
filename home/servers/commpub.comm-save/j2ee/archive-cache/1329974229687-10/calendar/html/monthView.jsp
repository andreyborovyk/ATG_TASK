<!-- Begin calendar Gear display -->
<%@ taglib uri="/calendar-taglib" prefix="calendar" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<core:demarcateTransaction id="monthXA">
<paf:InitializeGearEnvironment id="pafEnv">
<dsp:page>

<i18n:bundle baseName="atg.portal.gear.calendar.CalendarResources" localeAttribute="userLocale" changeResponseLocale="false" />

<%@ page import="java.io.*,java.util.*,atg.portal.gear.calendar.CalendarConstants" %>
<%@ page import="atg.repository.RepositoryItem" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Date" %>

<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/portal/gear/calendar/MonthEventSource" />
<dsp:importbean bean="/atg/portal/gear/calendar/DayEventSource" />

<%

  // maximum number of event label characters to display within a day
  // beyond this it is abbreviated with an ellipsis...
  int MAX_EVENT_CHARS=15;

  String gearId=pafEnv.getGear().getId();
  // Use gear title and highlight colors for day headers and events
  atg.portal.framework.ColorPalette cp = pafEnv.getPage().getColorPalette();
  String highlightTextColor = cp.getHighlightTextColor();
  String highlightBGColor = cp.getHighlightBackgroundColor();
  String titleBGColor = cp.getGearTitleBackgroundColor();
  String titleTextColor = cp.getGearTitleTextColor();

  // set type of event to use based on gear parameter, default to "detail"
  // for backward compatibility
  String eventTypeParam="eventType";
  String baseEventParamVal="base-event";
  String detailEventParamVal="detail-event";
  String defaultEventType=pafEnv.getGearInstanceParameter("defaultEventType");
  if (defaultEventType==null) 
     defaultEventType=detailEventParamVal;
  

    String yearRangeParam=pafEnv.getGear().getGearInstanceParameter("yearEndRange");
    int yearEndRange=2005;  // default if empty
    if (yearRangeParam!=null)
       yearEndRange=Integer.parseInt(yearRangeParam);
 
    // set up mapping to keep track of event alignment for events
    // that span multiple days
    java.util.Map yesterdaysEventMap = new java.util.HashMap(10); 
    java.util.Map todaysEventMap = new java.util.HashMap(10); 

    // Set some useful numerical parameters for the HTML table for
    // displaying the colored bars for the events of the day
    String tipWidth = "9%";
    String middleWidth = "82%";
    String eventHeight = "13";
    String spacerHeight = "2";
    String emptyDayWidth = "80";
    String emptyDayHeight = "40";

    String backgroundColor = highlightBGColor;
    String leftArrowURL = "arrow-start.gif";
    String rightArrowURL = "arrow-end.gif";
%>

<i18n:message id="newEventLink" key="newEventLink"/>

 <table width="100%" border="0" cellpadding="3" cellspacing="0" class="small">
  
<%-- Get the user's (profile) ID from the Nucleus component for the user profile --%>
<dsp:getvalueof id="profileId" bean="Profile.id" idtype="java.lang.String">

  <%-- Use the profile ID as the person whose events should be displayed --%>
  <%-- But if the query parameter "calUser" is defined, then use that instead --%>

  <%
    String vieweeId = profileId;
    String userId = request.getParameter("calUser");
    if (userId != null)
      vieweeId = userId;
  %>

        <%
          // Set a variable that will be passed to the event detail page
          // to indicate how to get back to this page, taking into account
          // whether the calendar is displaying in the shared or full view

          String backTarget;

          String currentDisplayMode = request.getParameter("paf_dm");
          if (currentDisplayMode == null)
            currentDisplayMode = "shared";

          if (currentDisplayMode.equals("full"))
            backTarget = "month_full";
          else
            backTarget = "month_shared";
        %>

        <%
        /*
        Determine which month to display:
        1.  If the parameter "calMonthYear" is present, attempt to decode it
            for text.  The format is per the example 1-2002.
        2.  If that fails, look for a Date value of the session attribute whose
            name is "atg.portal.gear.calendar" concatenated with the gear ID.
            If so, display the month in which that Date falls.
        3.  If that fails, display the current month.
        */

        java.text.SimpleDateFormat selectMonthFormat =
              new java.text.SimpleDateFormat("M-yyyy");
        String sessionKey = "atg.portal.gear.calendar." + pafEnv.getGear().getId();
        String monthYear = request.getParameter("calMonthYear");

        java.util.Date monthRequested;

        Object sessionMonthObject = session.getAttribute(sessionKey);
        if (sessionMonthObject != null && sessionMonthObject instanceof java.util.Date)
          monthRequested = (java.util.Date) sessionMonthObject;
        else
          monthRequested = new java.util.Date();

        try
        {
          if (monthYear != null)
            monthRequested = selectMonthFormat.parse(monthYear);
        }
        catch (java.text.ParseException e)
        {
          monthRequested = new java.util.Date();
        }

        /*
        Set the session variable so that it can be used for future requests.
        */
        session.setAttribute(sessionKey, monthRequested);
        /*
        Also, set a text variable to the month in format "2-2002", which is used
        below to determine which month in the drop-down list is the current month
        and therefore should be pre-selected.
        */
        monthYear = selectMonthFormat.format(monthRequested);
      %>

        <%--
        Display the drop-down list of months, from which the user can select, to display
        a different month.
        The SelectMonth tag produces a List of Date objects, one for each month to display
        in the drop-down list.
        --%>

        <calendar:SelectMonth id="selectMonth" endYear="<%=yearEndRange%>">

        <form name="monthForm">
        <input type=hidden name="jumperCheck" value="true">
        <input type="hidden" name="zoneOffset" value="unknown">
			
	<tr valign="top">
	  <td>
					
          <table border="0" cellpadding="3" cellspacing="0" width="100%" class="small">
             <tr>
          	<td align="left" class="small">

	 	  <%-- Randomize name so multiple calendars on same page work --%>
		  <% String selName = "destUrl" + System.currentTimeMillis(); %>
		
		   <select name="<%= selName %>" 
			   onChange="<%= "document.location.href=document.monthForm."
			             + selName
			             + "[document.monthForm."
				     + selName
			             + ".selectedIndex].value"%>">
		       <%-- Iterate over the List of Date objects representing months to display --%>

		       <core:ForEach id="monthLoop" values="<%= selectMonth.getMonthRange() %>"
			           castClass="java.util.Date"
			           elementId="thisSelectMonth">
			    <%
			      String thisMonthURL = "";
			    %>
			    <core:CreateUrl id="changeMonthUrl"
			         url="<%= pafEnv.getOriginalRequestURI() %>">
			
			       <core:UrlParam param="calMonthYear"
			              value="<%= selectMonth.formatMonthYearForSubmit(thisSelectMonth) %>"/>
			        <% thisMonthURL = changeMonthUrl.getNewUrl(); %>
			
	                    </core:CreateUrl>

              		<%-- Pre-select the current month --%>
                        <option value="<%= thisMonthURL %>"
                	<core:If value="<%= monthYear.equals( selectMonth.formatMonthYearForSubmit(thisSelectMonth)) %>"> selected </core:If>>

            		<%= selectMonth.formatMonthYearForDisplay(thisSelectMonth) %>

            		</option>
          	       </core:ForEach>
          	    </select>

          <noscript><input type=submit name=submit value=go></noscript>

          </td>

        <calendar:hasAccess gearEnv="<%=pafEnv%>" isPublic="<%=false%>">
         <core:CreateUrl id="fullGearUrl" url="<%= pafEnv.getOriginalRequestURI() %>">
           <core:UrlParam param="paf_dm" value="full"/>
           <core:UrlParam param="action" value="create_event"/>
           <core:UrlParam param="paf_gear_id" value="<%= pafEnv.getGear().getId() %>"/>
           <core:UrlParam param="sharedMonthYear" value="<%= monthYear %>"/>
           <core:UrlParam param="<%=eventTypeParam%>" value="<%= defaultEventType %>"/>
           <td align="right" nowrap><font class="small"><a href="<%= fullGearUrl.getNewUrl() %>"><%=newEventLink%></a></font></td>
         </core:CreateUrl>
        </calendar:hasAccess>
          </tr>
          </table>
          
         </td>
         </tr> 
          </form>
        </calendar:SelectMonth>
        <%-- Now display the calendar for the chosen month --%>

        <%-- The Month tag produces a List of Date objects, each representing the
             first day of a week to display within the month calendar
        --%>

        <calendar:Month id="month" monthRequested="<%= monthRequested %>">


          <%-- Get the Nucleus component that supplies calendar events --%>
          <dsp:getvalueof
           id="monthEventSource"
           bean="/atg/portal/gear/calendar/MonthEventSource"
           idtype="atg.portal.gear.calendar.CalendarEventSource">

            <%-- Query the event source for events for the current user for the entire month --%>
            <%-- Another event source is used, see below, to do pseudo-queries for each day
                 in the month by manually filtering events from this "real" event source --%>

            <calendar:Events id="monthEvents"
             startTime="<%= month.getBorderStartTime() %>"
             endTime="<%= month.getBorderEndTime() %>"
             userId="<%= vieweeId %>"
             gearId="<%= gearId %>"
             eventSource = "<%= monthEventSource %>">

              <% 
                 // This appears not to be used
                 month.setGearId(pafEnv.getGear().getId()); 
              %>

              <%
                String imageDocRoot = pafEnv.getGear().getServletContext() + "/images/";
                String clearGifURL = "clear.gif";
                String clearGifFullURL = imageDocRoot + clearGifURL;
              %>
						<tr valign="top"><td>
              <table width="100%" cols="7" border="1" cellspacing="0" cellpadding="0" class="small">

              <%-- Display the day-name labels --%>

              <tr BGCOLOR="#CCCCFF" align="center">

              <core:ForEach id="dayNameLoop" values="<%= month.getDayNames() %>"
               castClass="java.lang.String" elementId="dayName">

                <td bgcolor="<%=titleBGColor%>" width="14%" class="subhead"><font class="small" color="<%=titleTextColor%>"><%= dayName %></font></td>

              </core:ForEach>

              </tr>

              <%-- Iterate over the weeks in the month --%>

              <core:ForEach id="weekLoop" values="<%= month.getWeekFullDates() %>"
               castClass="java.util.Date" elementId="thisWeek">

                <% int numBlankRowsBeforeFirstEvent = 0; %>

                <%-- The Week tag produces a List of Date objects that represent the days
                     in the week --%>

                <calendar:Week id="week" startDate="<%= thisWeek %>" >

                  <tr align=left valign=top>

                  <%-- Iterate over the days in the week --%>

                  <core:ForEach id="dayLoop" values="<%= week.getDateList()%>"
                   castClass="java.util.Date" elementId="thisDate">

                    <%-- If the day is not within the month, i.e., if it is
                         a "filler" day at the beginning of the first week or
                         at the end of the last week in the month,
                         then make the background color gray --%>

		    <core:exclusiveIf>
                     <core:ifNot value="<%= month.contains(thisDate) %>">
                      <td bgcolor="#BBBBBB" class="small">
                     </core:ifNot>
                     <core:defaultCase>
                      <td class="small">
                     </core:defaultCase>
		    </core:exclusiveIf>

                    <%-- Establish the date to be shown within each cell in the calendar month --%>

                      <calendar:Date id="date" date="<%= thisDate %>">

                        <%-- Get the Nucleus component that supplies calendar events --%>
                        <%-- This CalendarEventSource gets its events from the set of events
                             that were already pre-loaded, above, for the entire month, and
                             does pseudo-queries to get the events for each day --%>

                        <dsp:getvalueof
                         id="dayEventSource"
                         bean="/atg/portal/gear/calendar/DayEventSource"
                         idtype="atg.portal.gear.calendar.CalendarEventSource">

                          <%-- Query the event source for events for the current day --%>

                          <calendar:Events
                           id="dayEvents"
                           startTime="<%= date.getStartTime() %>" endTime="<%= date.getEndTime() %>"
                           userId="<%= vieweeId %>"
             		   gearId="<%= gearId %>"
                           eventSource = "<%= dayEventSource %>">

                            <%-- Display the numeral for the date --%>
                            <%-- bold tags only on dates within the current month --%>

			    <core:exclusiveIf>
                              <core:if value="<%= month.contains(thisDate) %>">
                                <b><%= date.getDayOfMonth() %></b>
                              </core:if>
                              <core:defaultCase>
                                <%= date.getDayOfMonth() %>
                              </core:defaultCase>
			    </core:exclusiveIf>

                            <BR>

                            <%-- If the day has no events, output some filler --%>

			   <core:exclusiveIf>
                            <core:if value="<%= ! dayEvents.hasEvents()%>">
                              <table width="100%" border="0" cellpadding="0" cellspacing="0">
                                <TR>
                                <TD><IMG SRC="<%= clearGifFullURL %>" WIDTH="<%=emptyDayWidth%>" HEIGHT="<%=emptyDayHeight%>" alt="" /></TD>
				<TR>
				</table>
                            </core:if>

                            <%-- If the day has events, display them --%>

                            <core:defaultCase>

                              <table width="100%" border="0" cellpadding="0" cellspacing="0">


			      <%  // keep separate counter that can be adjusted for blank rows
			          int currentEventSlot=0; %>
                              <%-- Iterate over the events of the day --%>

                              <core:ForEach id="eventLoop"
                               values="<%= dayEvents.getEvents() %>"
                               castClass="java.lang.Object"
                               elementId="thisEvent" >

                                <%
                                  String eventText = null;
				  String thisEventType=(String)dayEvents.getPropertyValue(thisEvent,"type");
                                  String eventIdParam=(String) dayEvents.getPropertyValue(thisEvent,"id");
                                %>

				<%-- determine if we need some blank space before this event
				     to ensure alignment across days
				--%>
				<core:if value="<%=yesterdaysEventMap.containsKey(eventIdParam)%>">
				<% 
			          Integer yesterdaysSlot=(Integer)yesterdaysEventMap.get(eventIdParam);
				  int diff=yesterdaysSlot.intValue()-currentEventSlot;

                                  for (int i=0; i < diff; i++)
                                  {
			            currentEventSlot++;
                                  %>
                                    <TR>
                                    <TD colspan="3"><IMG SRC="<%= clearGifFullURL %>" WIDTH="1" HEIGHT="<%= eventHeight %>" alt="" /></TD>
                                    </TR>
                                    <TR>
                                    <TD colspan="3"><IMG SRC="<%= clearGifFullURL %>" WIDTH="1" HEIGHT="<%= spacerHeight %>" alt="" /></TD>
                                    </TR>
                                  <%
                                  }
                                  %>
				</core:if>

                                <%
                                  // Determine what kind of overlap the event has with the current day,
                                  // i.e., does the event begin and/or end within the current day

                                  int overlapType = dayEvents.getOverlap(thisEvent);
                                %>

                                <%
                                  // If today is the first day for the event, then set the text to
                                  // display within its colored bar for today

                                  // if (overlapType == CalendarConstants.RANGE_START ||
                                  //    overlapType == CalendarConstants.RANGE_START_END)
                                  //{

                                      RepositoryItem eventRepositoryItem = (RepositoryItem) thisEvent;
				      String rawEventText= (String)eventRepositoryItem.getPropertyValue("name");
				      // abbreviate text and add ellipsis if it exceeds MAX_EVENT_CHARS
				      if (rawEventText.length()>MAX_EVENT_CHARS) {
				        eventText=rawEventText.substring(0,MAX_EVENT_CHARS-1)+"&#133;";
				      } else {
				        eventText=rawEventText;
				      }
				      
                                  // }
                                %>

                                <%
                                  // Choose the 3 GIFs that will make up the colored bar for this
                                  // event for the current day, based on the event type and overlap type

                                  String leftImage = "";
                                  String rightImage = "";
                                  String middleImage = clearGifURL;

                                  switch (overlapType)
                                  {
                                    case CalendarConstants.RANGE_END:
                                    {
                                      leftImage = clearGifURL;
                                      rightImage = rightArrowURL;
                                      break;
                                    }
                                    case CalendarConstants.RANGE_START:
                                    {
                                      leftImage = leftArrowURL;
                                      rightImage = clearGifURL;
				      // add to event map for to be used by next day
				      todaysEventMap.put(eventIdParam, new Integer(currentEventSlot));
                                      break;
                                    }
                                    case CalendarConstants.RANGE_START_END:
                                    {
                                      leftImage = leftArrowURL;
                                      rightImage = rightArrowURL;
                                      break;
                                    }
                                    case CalendarConstants.RANGE_MIDDLE:
                                    {
                                      leftImage = clearGifURL;
                                      rightImage = clearGifURL;
				      // add to event map for to be used by next day
				      todaysEventMap.put(eventIdParam, new Integer (currentEventSlot));
                                      break;
                                    }
                                  }

                                %>

                                <%-- Set up the hypertext link to the full-page display for
                                     the event --%>

                                <core:createUrl id="viewEvent"
                                 url="<%= pafEnv.getOriginalRequestURI() %>">
                                  <core:UrlParam param="paf_dm" value="full"/>
                                  <core:UrlParam param="paf_gear_id"
                                   value="<%= pafEnv.getGear().getId() %>"/>
                                  <core:UrlParam param="action" value="show_event"/>
                                  <core:UrlParam param="<%= eventTypeParam %>" value="<%=thisEventType %>"/>
                                  <core:UrlParam param="event_id" value="<%= eventIdParam %>"/>
                                  <core:UrlParam param="calUser" value="<%= vieweeId %>"/>
                                  <core:UrlParam param="back" value="<%= backTarget %>"/>
                                  <core:UrlParam param="backCalMonth" value="<%= monthYear %>" />

                                  <%-- Display the colored bar for this event for the current day --%>

                                  <TR BGCOLOR="<%= backgroundColor %>">
                                  <TD WIDTH="<%= tipWidth %>" ><A HREF="<%= viewEvent.getNewUrl() %>"><IMG SRC="<%= imageDocRoot + leftImage %>" WIDTH="100%" HEIGHT="<%= eventHeight %>" BORDER="0" alt="" /></A></TD>

                                  <%-- without text --%>
                                  <core:IfNull value="<%= eventText %>">
                                    <TD WIDTH="<%= middleWidth %>"><font class="smaller">&nbsp;</font><A HREF="<%= viewEvent.getNewUrl() %>"><IMG SRC="<%= imageDocRoot + middleImage %>" WIDTH="100%" HEIGHT="<%= eventHeight %>" BORDER="0" alt="" /></A></TD>
                                  </core:IfNull>

                                  <%-- with text --%>
                                  <core:IfNotNull value="<%= eventText %>">
                                    <TD WIDTH="<%= middleWidth %>" nowrap><font color="<%=highlightTextColor%>" class="smaller"><A HREF="<%= viewEvent.getNewUrl() %>"><%= eventText %></A></font></TD>
                                  </core:IfNotNull>

                                  <TD WIDTH="<%= tipWidth %>" ><A HREF="<%= viewEvent.getNewUrl() %>"><IMG SRC="<%= imageDocRoot + rightImage %>" WIDTH="100%" HEIGHT="<%= eventHeight %>" BORDER="0" alt="" /></A></TD>
                                  </TR>

                                  <%-- Display a spacer after each colored bar --%>

                                  <TR BGCOLOR="" HEIGHT="<%= spacerHeight %>" COLSPAN="3">
                                  <TD HEIGHT="<%= spacerHeight %>"><IMG SRC="<%= clearGifFullURL %>" HEIGHT="<%= spacerHeight %>" BORDER="0" alt="" /></TD>
                                  </TR>

                                </core:createUrl>

			        <% currentEventSlot++; %>
                              </core:ForEach>
                              <%-- End iterating over the events of the day --%>
			      <%  // reset the event maps for the next day's use 
			          yesterdaysEventMap=todaysEventMap; %>

                              </table>
                              <%-- End table for the events of the day --%>
                            </core:defaultCase>
                           </core:exclusiveIf>
                           <%-- End if there were events today --%>
                          </calendar:Events>
                        </dsp:getvalueof>  <%-- dayEventSource --%>
                      </calendar:Date>
                      </td>
                  </core:ForEach>
                  <%-- End iterating over the dates in the week --%>
                  </tr>
                </calendar:Week>
              </core:ForEach>
              <%-- End iterating over the weeks in the month --%>
              </table>
	     </td></tr>
            </calendar:Events>
          </dsp:getvalueof>  <%-- monthEventSource --%>
        </calendar:Month>

</dsp:getvalueof>

</table>

</dsp:page>
</paf:InitializeGearEnvironment>
</core:demarcateTransaction>

<%-- Version: $Change: 651448 $$DateTime: 2011/06/07 13:55:45 $--%>
<%-- @version $Id: //app/portal/version/10.0.3/calendar/calendar.war/html/monthView.jsp#2 $$Change: 651448 $--%>
