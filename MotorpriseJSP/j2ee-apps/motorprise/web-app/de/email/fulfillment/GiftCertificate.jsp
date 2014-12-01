<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: GiftCertificate  -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="Ein Geschenkgutschein für Sie!" param="messageSubject"/>
<dsp:setvalue value="GiftCertificate" param="mailingName"/>

<h3>Dies ist ein Muster-Geschenkgutschein per E-Mail.  Sie können jede gewünschte Vorlage verwenden.
Über die Konfiguration für
<code>/atg/commerce/fulfillment/SoftgoodFulfiller.giftCertificateEmailTemplate</code> können Sie
eine andere Vorlage einstellen.</h3><br>

<p>Innerhalb dieser Vorlage haben Sie Zugriff auf das Profil des
Geschenkgutschein-Käufers, die Menge der erworbenen Geschenkgutscheine
und den Betrag des betreffenden Geschenkgutscheins.<br>

<p><dsp:valueof param="purchaser.firstName">Unbekannt</dsp:valueof> <dsp:valueof param="purchaser.lastName">Unbekannt</dsp:valueof> 
hat für Sie
<dsp:droplet name="Switch">
  <dsp:param name="value" param="quantity"/>
  <dsp:oparam name="1">
    einen Geschenkgutschein
  </dsp:oparam>
  <dsp:oparam name="default">
    <dsp:valueof param="quantity"/> Geschenkgutscheine
  </dsp:oparam>
</dsp:droplet>

erworben. Der Betrag ist
<dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="giftCertificate.amount"/> 
<br>

<p>Die zu verwendende Anspruchskennung ist gleich der
Archivkennung des Geschenkgutscheins.<br>

<p>Bitte verwenden Sie die Anspruchskennung <dsp:valueof param="giftCertificate.repositoryId"/><br>
  
<p> Danke
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/fulfillment/GiftCertificate.jsp#2 $$Change: 651448 $--%>
