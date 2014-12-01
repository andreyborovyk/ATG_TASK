<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: OneMonthEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>


<dsp:setvalue value="10% Nachlass auf Ihre nächste Bestellung." param="messageSubject"/>
<%-- 
<dsp:setvalue value="orders@example.com" param="messageFrom"/>
<dsp:setvalue value="orders@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue paramvalue="message.profile.email" param="messageTo"/>
<dsp:setvalue value="25% Over Average" param="mailingName"/>


<dsp:setvalue paramvalue="message.profile" param="recipient"/>


<p> <dsp:valueof param="recipient.firstName">Sehr geehrter Kunde</dsp:valueof>
 <dsp:valueof param="recipient.lastName"/>,

<p> Ihre letzte Bestellsumme lag deutlich über dem Wert der Bestellungen, die Sie früher bei uns aufgegeben haben. Damit Sie weiter gern bei uns bestellen, erhalten Sie auf Ihre nächste Bestellung einen Nachlass von 10 %.

<p>Mit freundlichen Grüßen,<br>
Motorprise, Inc.
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/25OverAverage.jsp#2 $$Change: 651448 $--%>
