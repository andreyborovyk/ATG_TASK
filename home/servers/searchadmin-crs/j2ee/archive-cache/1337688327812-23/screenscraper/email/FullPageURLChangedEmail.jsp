<%@ taglib uri="/dsp-taglib" prefix="dsp" %>
<%@ taglib uri="http://jakarta.apache.org/taglibs/i18n-1.0" prefix="i18n" %>

<dsp:page>


<i18n:bundle baseName="atg.portal.gear.screenscraper.email_template"
             localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message id="title"            key="EMAIL_HTML_TITLE"   />
<i18n:message id="emailSubject"     key="SUBJECT_NEW_EVENT"  />
<i18n:message id="emailSubject2"    key="SUBJECT_SS_FULL_CHANGED" />
<i18n:message id="emailMailingName" key="EMAIL_NOTICE_NAME"  />
<i18n:message id="fromEmail"        key="FROM_ADDRESS"       />
<i18n:message id="replyEmail"       key="REPLY_TO_ADDRESS"   />


<dsp:setvalue param="messageSubject"      value='<%= emailSubject+" "+emailSubject2 %>' />
<dsp:setvalue param="messageFrom"         value="<%= fromEmail %>"/>
<dsp:setvalue param="messageReplyTo"      value="<%= replyEmail %>"/>
<dsp:setvalue param="mailingName"         value="<%= emailMailingName %>"/>


<dsp:getvalueof id="oldUrl"    param="alertMsg.oldURL">
<dsp:getvalueof id="newUrl"    param="alertMsg.newURL">
<dsp:getvalueof id="eventName" param="alertMsg.gearName">
<dsp:getvalueof id="commName"  param="alertMsg.communityName">


<HTML><HEAD><TITLE><%= title %></TITLE></HEAD>
<BODY BGCOLOR="#FFFFFF">
<h3><i18n:message  key="BODY_HEADER_FULL_CHANGED"/></h3>

<i18n:message  key="BODY_CONTENT_FULL_CHANGED">
 <i18n:messageArg value="<%=eventName%>"/>
 <i18n:messageArg value="<%=commName%>"/>
</i18n:message>
<br /><br />

<i18n:message  key="INFO_NEW_URL">
 <i18n:messageArg value="<%=newUrl%>"/>
</i18n:message>
<br />
<i18n:message  key="INFO_OLD_URL">
 <i18n:messageArg value="<%=oldUrl%>"/>
</i18n:message>

<br /><br /><br />

<font color="#666666" size="-1">
<i18n:message  key="BODY_FOOTER_EVENT"/><br />
</font>

</BODY> </HTML>


</dsp:getvalueof><%-- gearName  --%>
</dsp:getvalueof><%-- communityName --%>
</dsp:getvalueof><%-- newURL    --%>
</dsp:getvalueof><%-- oldURL    --%>
</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/screenscraper/screenscraper.war/email/FullPageURLChangedEmail.jsp#2 $$Change: 651448 $--%>
