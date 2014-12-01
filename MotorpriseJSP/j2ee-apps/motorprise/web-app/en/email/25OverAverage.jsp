<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: OneMonthEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


<dsp:setvalue value="10% off your next order." param="messageSubject"/>
<%-- 
<dsp:setvalue value="orders@example.com" param="messageFrom"/>
<dsp:setvalue value="orders@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue paramvalue="message.profile.email" param="messageTo"/>
<dsp:setvalue value="25% Over Average" param="mailingName"/>


<dsp:setvalue paramvalue="message.profile" param="recipient"/>


<p> Dear <dsp:valueof param="recipient.firstName">Valued Customer</dsp:valueof>
 <dsp:valueof param="recipient.lastName"/>,

<p>Your last order total showed a significant increase over other orders you have placed with us. As an incentive to keep you coming back you will be getting 10% off your next order.

<p>Sincerely,<br>
Motorprise, Inc.
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/25OverAverage.jsp#2 $$Change: 651448 $--%>
