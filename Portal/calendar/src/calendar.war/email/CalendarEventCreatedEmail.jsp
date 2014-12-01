<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>


<i18n:bundle baseName="atg.portal.gear.calendar.email_templates"
             localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message id="title"            key="EMAIL_HTML_TITLE"   />
<i18n:message id="emailSubject"     key="SUBJECT_NEW_EVENT"  />
<i18n:message id="emailSubject2"    key="SUBJECT_CAL_EVENTCREATED" />
<i18n:message id="emailMailingName" key="EMAIL_NOTICE_NAME"  />
<i18n:message id="fromEmail"        key="FROM_ADDRESS"       />
<i18n:message id="replyEmail"       key="REPLY_TO_ADDRESS"   />


<dsp:setvalue param="messageSubject"      value='<%= emailSubject+ " " + emailSubject2 %>' />
<dsp:setvalue param="messageFrom"         value="<%= fromEmail %>"/>
<dsp:setvalue param="messageReplyTo"      value="<%= replyEmail %>"/>
<dsp:setvalue param="mailingName"         value="<%= emailMailingName %>"/>


<dsp:getvalueof id="eventName" param="alertMsg.eventName">
<dsp:getvalueof id="commName" param="alertMsg.communityName">


<HTML><HEAD><TITLE><%= title %></TITLE></HEAD>
<BODY BGCOLOR="#FFFFFF">
<h3><i18n:message  key="BODY_HEADER_NEW_EVENT"/></h3>

<i18n:message  key="BODY_CONTENT_NEW_EVENT">
 <i18n:messageArg value="<%=eventName%>"/>
 <i18n:messageArg value="<%=commName%>"/>
</i18n:message>

<br /><br /><br />

<font color="#666666" size="-1">
<i18n:message  key="BODY_FOOTER_EVENT"/><br />
</font>

</BODY> </HTML>


</dsp:getvalueof><%-- commName  --%>
</dsp:getvalueof><%-- eventName --%>
</dsp:page>


<%--   <!-- OLD JHTML VERSION -->
<setvalue param="messageFrom" value="no-one@example.com">
<setvalue param="messageSubject" value="Alert Notification">
<setvalue param="messageReplyTo" value="no-one@example.com">
<setvalue param="mailingName" value="Alert Notification">
<p>A new calendar event <valueof param="alertMsg.eventName">unknown</valueof> 
has been added to the  <valueof param="alertMsg.communityName">unknown</valueof> community calendar.
<p><valueof param="alertMsg.eventName"></valueof><br>
<valueof param="alertMsg.startDateString"></valueof><br>
<valueof param="alertMsg.city"></valueof><br>
<valueof param="alertMsg.state"></valueof>
--%>
<%-- @version $Id: //app/portal/version/10.0.3/calendar/calendar.war/email/CalendarEventCreatedEmail.jsp#2 $$Change: 651448 $--%>
