
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<i18n:bundle baseName="atg.gears.communities.email_template" 
             localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message id="title"            key="EMAIL_HTML_TITLE"  />
<i18n:message id="emailSubject"     key="SUBJECT_NEW_EVENT"  />
<i18n:message id="emailSubject2"    key="SUBJECT_COMM_REMOVED" />
<i18n:message id="emailMailingName" key="EMAIL_NOTICE_NAME"  />
<i18n:message id="fromEmail"        key="FROM_ADDRESS"       />
<i18n:message id="replyEmail"       key="REPLY_TO_ADDRESS"   />

<dsp:setvalue param="messageSubject"      value='<%= emailSubject+" "+emailSubject2 %>' />
<dsp:setvalue param="messageFrom"         value="<%= fromEmail %>"/>
<dsp:setvalue param="messageReplyTo"      value="<%= replyEmail %>"/>
<dsp:setvalue param="mailingName"         value="<%= emailMailingName %>"/>


<dsp:getvalueof id="favName"    param="alertMsg.favoriteName">
<dsp:getvalueof id="profileID"  param="alertMsg.profileId">

<HTML><HEAD><TITLE><%= title %></TITLE></HEAD>

<BODY BGCOLOR="#FFFFFF">

<h3><i18n:message  key="BODY_HEADER_REMOVED"/></h3>

 <i18n:message  key="BODY_CONTENT_REMOVED">
  <i18n:messageArg value="<%=favName%>" />
  <i18n:messageArg value="<%=profileID%>" />
 </i18n:message>

<br /><br /><br />

<font color="#666666" size="-1">
<i18n:message  key="BODY_FOOTER_EVENT"/><br />
</font>

</BODY> </HTML>

</dsp:getvalueof><%-- favoriteName  --%>
</dsp:getvalueof><%-- userID --%>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/communities/communities.war/email/FavoriteRemovedEmail.jsp#2 $$Change: 651448 $--%>
