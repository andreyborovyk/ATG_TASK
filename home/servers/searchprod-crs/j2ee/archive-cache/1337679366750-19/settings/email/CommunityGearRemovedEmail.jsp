
<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>

<dsp:page>

<!-- Title: Community Gear Removed Alert Notification -->

<dsp:setvalue param="messageFrom" value="no-one@atg.com"/>
<dsp:setvalue param="messageSubject" value="Alert Notification"/>
<dsp:setvalue param="messageReplyTo" value="no-one@atg.com"/>
<dsp:setvalue param="mailingName" value="Alert Notification"/>

<p>The gear named <dsp:valueof param="alertMsg.gearId">unknown</dsp:valueof> has
been removed from community <dsp:valueof param="alertMsg.communityId">unknown</dsp:valueof> by
<dsp:valueof param="alertMsg.profileId">unknown</dsp:valueof>.

</dsp:page>


<%-- i18n KEYS
SUBJECT_PAF_COMM_GEAR_REMOVED
BODY_HEADER_COMM_GEAR_REMOVED
BODY_CONTENT_COMM_GEAR_REMOVED
--%>
<%-- @version $Id: //app/portal/version/10.0.3/paf/settings.war/email/CommunityGearRemovedEmail.jsp#2 $$Change: 651448 $--%>
