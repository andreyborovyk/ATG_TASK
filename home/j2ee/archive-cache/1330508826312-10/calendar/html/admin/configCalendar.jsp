<%-- 
Page:   	configCalendar.jsp
Gear:  	 	Calendar Gear
gearmode: 	instanceConfig
displayMode: 	full

Author:      	Jeff Banister
Description: 	This page is included by instanceConfig.jsp, and renders and handles a form to 
		set general gear parameters
--%>

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/core-taglib" prefix="core" %>
<%@ taglib uri="/paf-taglib" prefix="paf" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<%@ page import="java.io.*,java.util.*,atg.portal.gear.calendar.CalendarConstants" %>
<%@ page import="java.util.Calendar" %>

<dsp:page>

<i18n:bundle baseName="atg.portal.gear.calendar.CalendarResources" localeAttribute="userLocale" changeResponseLocale="false" />
<i18n:message id="saveButtonLabel" key="save-button-label"/>
<i18n:message id="baseEventLabel" key="baseEventLabel"/>
<i18n:message id="detailEventLabel" key="detailEventLabel"/>

<paf:InitializeGearEnvironment id="pafEnv">

<% String origURI= pafEnv.getOriginalRequestURI();
   String gearID=pafEnv.getGear().getId();
   String pageID = request.getParameter("paf_page_id");
   String pageURL = request.getParameter("paf_page_url");
   String pageURLEncoded = atg.servlet.ServletUtil.escapeURLString(pageURL);
   String communityID = request.getParameter("paf_community_id");
   String pafSuccessURL = request.getParameter("paf_success_url");
   String pafSuccessURLEncoded = atg.servlet.ServletUtil.escapeURLString(pafSuccessURL);
   String clearGif = response.encodeURL("/gear/calendar/images/clear.gif");
   String infoGif = response.encodeURL("/gear/calendar/images/info.gif");
   String errorGif = response.encodeURL("/gear/calendar/images/error.gif");


   // get current year value for year end range configuration
   Calendar rightNow = Calendar.getInstance();
   int currentYear=rightNow.get(Calendar.YEAR);
   int MAX_YEAR_RANGE=2020;


%>

<dsp:importbean bean="/atg/portal/gear/GearConfigFormHandler"/>


<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="GearConfigFormHandler.formError"/>
  <dsp:oparam name="true">
    <font class="error">
      <dsp:droplet name="/atg/dynamo/droplet/ErrorMessageForEach">
        <dsp:param name="exceptions" bean="GearConfigFormHandler.formExceptions"/>
        <dsp:oparam name="output">
          &nbsp;<img src="<%=errorGif%>">&nbsp;&nbsp; <dsp:valueof param="message"></dsp:valueof><br>
        </dsp:oparam>
      </dsp:droplet>
    </font>
  </dsp:oparam>
  <dsp:oparam name="false">
    <core:ifNotNull value='<%=request.getParameter("formSubmit")%>'>
    <font class="info">
      <br>&nbsp;<img src="<%=infoGif%>">&nbsp;&nbsp;<i18n:message key="configFeedbackMsg"/> <br> <br></font>
    </core:ifNotNull>
  </dsp:oparam>
</dsp:droplet>


<i18n:message id="i18n_quote" key="html_quote"/>
<table cellpadding="1" cellspacing="0" border="0" bgcolor="#666666" width="98%"><tr><td>
<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="100%"><tr><td>
  <font class="pageheader_edit">
   <i18n:message key="calendarConfigTitle">
       <i18n:messageArg value="<%=i18n_quote%>"/>
       <i18n:messageArg value="<%=pafEnv.getGear().getName(response.getLocale())%>"/>
    </i18n:message>
</td></tr></table>
</td></tr></table>

<table cellpadding="2" cellspacing="0" border="0" bgcolor="#EAEEF0" width="98%"><tr><td>
        <font class="smaller"><i18n:message key="calendarConfigHelperText"/>
        </td></tr></table>
  <img src="<%=clearGif%>" height="1" width="1" border="0"><br>

<table cellpadding="4" cellspacing="0" border="0" bgcolor="#BAD8EC" width="98%">
<tr><td>
<table cellpadding="1" cellspacing="0" border="0">

<dsp:setvalue bean="GearConfigFormHandler.initializeDefaultValues" value="foo"/>

<dsp:form enctype="multipart/form-data" method="post" action="<%= origURI %>">
	

  <input type="hidden" name="paf_gear_id" value="<%= gearID %>"/>
  <input type="hidden" name="paf_page_id" value="<%= pageID %>"/>
  <input type="hidden" name="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
  <input type="hidden" name="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
  <input type="hidden" name="paf_page_url" value="<%= pageURLEncoded %>"/>
  <input type="hidden" name="paf_community_id" value="<%= communityID %>"/>
  <input type="hidden" name="paf_success_url" value="<%= pafSuccessURLEncoded %>"/>
  <input type="hidden" name="formSubmit" value="true"/>

  <core:CreateUrl id="successUrl" url="<%= origURI %>">
    <core:UrlParam param="paf_community_id" value="<%= communityID %>"/>
    <paf:encodeUrlParam param="paf_page_url" value="<%= pageURL %>"/>
    <core:UrlParam param="paf_dm" value="<%=pafEnv.getDisplayMode() %>"/>
    <core:UrlParam param="paf_gm" value="<%=pafEnv.getGearMode() %>"/>
    <core:UrlParam param="config_page" value="calendar"/>
    <core:UrlParam param="paf_gear_id" value="<%= gearID %>"/>
    <core:UrlParam param="paf_page_id" value="<%= pageID %>"/>
    <core:UrlParam param="formSubmit" value="true"/>
    <paf:encodeUrlParam param="paf_success_url" value="<%= pafSuccessURL%>"/>
    <dsp:input type="hidden" bean="GearConfigFormHandler.successUrl" value="<%= successUrl.getNewUrl()%>"/>
    <dsp:input type="hidden" bean="GearConfigFormHandler.cancelUrl" value="<%= successUrl.getNewUrl()%>"/>
  </core:CreateUrl>





    <tr>
      <td colspan="2"><font class="smaller"><i18n:message key="defaultEventTypeLabel"/></font><br>
	  <dsp:setvalue bean="GearConfigFormHandler.values.defaultEventType"
	            value='<%= pafEnv.getGearInstanceParameter("defaultEventType") %>' />

	  <font class="smaller">
          <dsp:input type="radio" bean="GearConfigFormHandler.values.defaultEventType" value="<%=CalendarConstants.BASE_EVENT_TYPE%>"/>&nbsp;<%=baseEventLabel%>
	  <br>
          <dsp:input type="radio" bean="GearConfigFormHandler.values.defaultEventType" value="<%=CalendarConstants.DETAIL_EVENT_TYPE%>"/>&nbsp;<%=detailEventLabel%>
	  </font>
<br><br>
	  </td>
    </tr>

    <tr>
      <td colspan="2"><font class="smaller"><i18n:message key="yearEndRangeLabel"/><br>
	  <font class="smaller"><i18n:message key="yearEndRangeDesc"/></font><br>
	  <dsp:setvalue bean="GearConfigFormHandler.values.yearEndRange"
	            value='<%= pafEnv.getGearInstanceParameter("yearEndRange") %>' />

	  <font class="smaller">
	    <dsp:select bean="GearConfigFormHandler.values.yearEndRange">
	      <% for(int i=currentYear; i <= MAX_YEAR_RANGE; ++i) { %>
	          <dsp:option value="<%=Integer.toString(i)%>" /><%=Integer.toString(i)%>
	      <% } %>
	    </dsp:select>
	  </font>

	  </td>
    </tr>
    <tr VALIGN="top" ALIGN="left"> 
      <td><br><dsp:input type="submit" value="<%=saveButtonLabel%>" bean="GearConfigFormHandler.confirm"/></td>
      <td align="left">&nbsp;&nbsp;&nbsp;
      </td>
    </tr>
  </TABLE>

</dsp:form>
</td></tr></table><br><br>
</paf:InitializeGearEnvironment>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/calendar/calendar.war/html/admin/configCalendar.jsp#2 $$Change: 651448 $--%>
