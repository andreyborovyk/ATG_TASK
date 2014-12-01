<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: GiftPurchasedEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="A Gift has been purchased for you." param="messageSubject"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue paramvalue="message.profile.email" param="messageTo"/>
<dsp:setvalue value="GiftPurchased" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="recipient"/>
<dsp:setvalue paramvalue="message.item" param="item"/>

<p> Dear <dsp:valueof param="recipient.firstName">Valued Customer</dsp:valueof>
 <dsp:valueof param="recipient.lastName"/>

<p> A gift has been purchased for you by <dsp:valueof bean="/atg/userprofiling/Profile.firstName">unknown</dsp:valueof>  <dsp:valueof bean="/atg/userprofiling/Profile.lastName">unknown</dsp:valueof>

<p> The following item was purchased:

<p> Thank you,
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/fulfillment/GiftPurchased.jsp#2 $$Change: 651448 $--%>
