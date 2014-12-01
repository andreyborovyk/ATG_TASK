<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: GiftCertificate  -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="A Gift Certificate for you!" param="messageSubject"/>
<dsp:setvalue value="GiftCertificate" param="mailingName"/>

<h3>This is a sample gift certificate email.  You may use any template
you like, use the configuration for
<code>/atg/commerce/fulfillment/SoftgoodFulfiller.giftCertificateEmailTemplate</code> to
change to a different template.</h3><br>

<p>Within this template you have access to the profile of the
purchaser of the gift certificate, the quantity of gift certificates
purchased, and the amount of that gift certificate.<br>

<p><dsp:valueof param="purchaser.firstName">unknown</dsp:valueof> <dsp:valueof param="purchaser.lastName">unknown</dsp:valueof> 
has purchased 
<dsp:droplet name="Switch">
  <dsp:param name="value" param="quantity"/>
  <dsp:oparam name="1">
    a gift certificate 
  </dsp:oparam>
  <dsp:oparam name="default">
    <dsp:valueof param="quantity"/> gift certificates 
  </dsp:oparam>
</dsp:droplet>

for you in the amount of 
<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="giftCertificate.amount"/> 
<br>

<p>The claim code that should be used is equivalent to the
repositoryId of the gift certificate.<br>

<p>Please use claim code <dsp:valueof param="giftCertificate.repositoryId"/><br>
  
<p> Thank you
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/fulfillment/GiftCertificate.jsp#2 $$Change: 651448 $--%>
