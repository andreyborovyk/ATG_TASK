<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>


<i18n:bundle baseName="atg.gears.discussion.email_template"
             localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message id="title"            key="EMAIL_HTML_TITLE"   />
<i18n:message id="emailSubject"     key="SUBJECT_NEW_EVENT"  />
<i18n:message id="emailSubject2"    key="SUBJECT_DIS_FORUM_ADDED" />
<i18n:message id="emailMailingName" key="EMAIL_NOTICE_NAME"  />
<i18n:message id="fromEmail"        key="FROM_ADDRESS"       />
<i18n:message id="replyEmail"       key="REPLY_TO_ADDRESS"   />


<dsp:setvalue param="messageSubject"      value="<%= emailSubject+emailSubject2 %>" />
<dsp:setvalue param="messageFrom"         value="<%= fromEmail %>"/>
<dsp:setvalue param="messageReplyTo"      value="<%= replyEmail %>"/>
<dsp:setvalue param="mailingName"         value="<%= emailMailingName %>"/>


<dsp:getvalueof id="eventName" param="alertMsg.forumName">
<dsp:getvalueof id="commName" param="alertMsg.communityId">


<HTML><HEAD><TITLE><%= title %></TITLE></HEAD>
<BODY BGCOLOR="#FFFFFF">
<h3><i18n:message  key="BODY_HEADER_FORUM_ADDED"/></h3>

<i18n:message  key="BODY_CONTENT_FORUM_ADDED">
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



<%--  OLD TEMPLATE 
<!-- Title: Forum Created Alert Notification -->

<setvalue param="messageFrom" value="no-one@example.com">
<setvalue param="messageSubject" value="New Forum Notification">
<setvalue param="messageReplyTo" value="no-one@example.com">
<setvalue param="mailingName" value="Forum Alert Notification">

<p>A new discussion forum has been created: <valueof param="alertMsg.forumName">unknown</valueof> in the <valueof param="alertMsg.communityId">unknown</valueof> community.
<java>/* @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/email/NewForumEmail.jsp#2 $$Change: 651448 $*/</java>
--%>
<%-- @version $Id: //app/portal/version/10.0.3/discussion/discussion.war/email/NewForumEmail.jsp#2 $$Change: 651448 $--%>
