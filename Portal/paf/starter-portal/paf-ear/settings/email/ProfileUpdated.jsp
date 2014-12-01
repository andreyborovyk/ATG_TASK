

<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<i18n:bundle baseName="atg.portal.admin.email_template" 
             localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message id="title"            key="EMAIL_HTML_TITLE_WELCOME"  />
<i18n:message id="emailSubject"     key="SUBJECT_PROFILE_UPDATED"  />
<i18n:message id="emailMailingName" key="EMAIL_NOTICE_NAME_WELCOME"  />
<i18n:message id="fromEmail"        key="FROM_ADDRESS_WELCOME"       />
<i18n:message id="replyEmail"       key="REPLY_TO_ADDRESS_WELCOME"   />

<dsp:setvalue param="messageSubject"      value="<%= emailSubject %>" />
<dsp:setvalue param="messageFrom"         value="<%= fromEmail %>"/>
<dsp:setvalue param="messageReplyTo"      value="<%= replyEmail %>"/>
<dsp:setvalue param="mailingName"         value="<%= emailMailingName %>"/>

<HTML>
</HEAD>
<TITLE><i18n:message key="BODY_PROFILE_UPDATED_HEADER_PROFILE_UPDATED"/></TITLE>
</HEAD>
<BODY>
<i18n:message key="BODY_PROFILE_UPDATED_GREETING">
 Sir or Madam,
</i18n:message><br><br>
<i18n:message key="BODY_PROFILE_UPDATED_CONTENT_A">
Thank you for taking the time to update your personal details.
</i18n:message>
<br />
<i18n:message key="BODY_PROFILE_UPDATED_CONTENT_B">
This email is to confirm that you indeed wanted your profile updated.
</i18n:message>
<br />
<i18n:message key="BODY_PROFILE_UPDATED_CONTENT_C">
</i18n:message>
<br />
<i18n:message key="BODY_PROFILE_UPDATED_FOOTER">
Please let us know if this update was not done by you.
</i18n:message>


<p>
<i18n:message key="BODY_UPDATED_FOOTER_SALUTATION">Sincerely,</i18n:message><br>
<i18n:message key="BODY_UPDATED_FOOTER_SIGNATURE_A">N. E. Body</i18n:message><br>
<i18n:message key="BODY_UPDATED_FOOTER_SIGNATURE_B">Portal Adminstrator</i18n:message>

</BODY> </HTML>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/email/ProfileUpdated.jsp#2 $$Change: 651448 $--%>
