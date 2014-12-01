
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>
<!-- Title: Page Gear Removed Alert Notification -->

<dsp:setvalue param="messageFrom" value="no-one@atg.com"/>
<dsp:setvalue param="messageSubject" value="Alert Notification"/>
<dsp:setvalue param="messageReplyTo" value="no-one@atg.com"/>
<dsp:setvalue param="mailingName" value="Alert Notification"/>

<p>The gear named <dsp:valueof param="alertMsg.gearId">unknown</dsp:valueof> has
been removed from page <dsp:valueof param="alertMsg.pageId">unknown</dsp:valueof> in
community <dsp:valueof param="alertMsg.communityId">unknown</dsp:valueof> by
<dsp:valueof param="alertMsg.profileId">unknown</dsp:valueof>.

</dsp:page>


<%-- i18n KEYS
SUBJECT_PAF_PAGE_GEAR_REMOVED
BODY_HEADER_PAGE_GEAR_REMOVED
BODY_CONTENT_PAGE_GEAR_REMOVED
--%>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/email/PageGearRemovedEmail.jsp#2 $$Change: 651448 $--%>
