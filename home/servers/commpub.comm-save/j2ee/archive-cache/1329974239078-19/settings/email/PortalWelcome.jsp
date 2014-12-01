
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<i18n:bundle baseName="atg.portal.admin.email_template" 
             localeAttribute="userLocale" changeResponseLocale="false" />

<i18n:message id="title"            key="EMAIL_HTML_TITLE_WELCOME"  />
<i18n:message id="emailSubject"     key="SUBJECT_WELCOME"  />
<i18n:message id="emailMailingName" key="EMAIL_NOTICE_NAME_WELCOME"  />
<i18n:message id="fromEmail"        key="FROM_ADDRESS_WELCOME"       />
<i18n:message id="replyEmail"       key="REPLY_TO_ADDRESS_WELCOME"   />

<dsp:setvalue param="messageSubject" value="<%= emailSubject %>"/>
<dsp:setvalue param="messageFrom"    value="<%= fromEmail %>"/>
<dsp:setvalue param="messageReplyTo" value="<%= replyEmail %>"/>
<dsp:setvalue param="mailingName"    value="<%= emailMailingName %>"/>

<HTML><HEAD>
<TITLE>Welcome to the Portal Community</TITLE>
</HEAD>

<dsp:importbean bean="/atg/userprofiling/Profile"/>

<font face="verdana" size=2>
<dsp:droplet name="/atg/dynamo/droplet/Switch">
  <dsp:param name="value" bean="/atg/userprofiling/Profile.firstname"/>
  <dsp:oparam name="unset">
     <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param name="value" bean="/atg/userprofiling/Profile.login"/>
          <dsp:oparam name="unset">
                <i18n:message key="BODY_PORTAL_WELCOME_GREETING"/>
          </dsp:oparam>
          <dsp:oparam name="default">
             <dsp:valueof bean="Profile.login"></dsp:valueof>
          </dsp:oparam>
     </dsp:droplet>

   </dsp:oparam>
  <dsp:oparam name="default">
    <dsp:valueof bean="Profile.firstName"></dsp:valueof>
    <dsp:valueof bean="Profile.lastName"></dsp:valueof>
  </dsp:oparam>
</dsp:droplet>

<p>
<i18n:message key="BODY_PORTAL_WELCOME_CONTENT_A">
Thank you for registering with our Community.  We are excited to have you aboard.<p>
</i18n:message><br><br>
<i18n:message key="BODY_PORTAL_WELCOME_CONTENT_B">
 <i18n:messageArg value="http://localhost:8840/" />
</i18n:message><br><br> 
<%--  ??
http://localhost:8840<valueof bean="Profile.registrationURL"></valueof><p>
--%>
<i18n:message key="BODY_PORTAL_WELCOME_CONTENT_C">Again, thanks for joining us.</i18n:message>

<p>
<i18n:message key="BODY_PORTAL_WELCOME_FOOTER"/><br>
<i18n:message key="BODY_PORTAL_WELCOME_FOOTER_SALUTATION">Sincerely,</i18n:message><br>
<i18n:message key="BODY_PORTAL_WELCOME_FOOTER_SIGNATURE_A">N. E. Body</i18n:message><br>
<i18n:message key="BODY_PORTAL_WELCOME_FOOTER_SIGNATURE_B">Portal Adminstrator</i18n:message>

</HTML>

</dsp:page>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/email/PortalWelcome.jsp#2 $$Change: 651448 $--%>
