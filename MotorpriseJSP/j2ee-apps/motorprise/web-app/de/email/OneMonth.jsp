<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: OneMonthEmail -->
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="10% Nachlass auf Ihre nächste Bestellung." param="messageSubject"/>
<%-- 
<dsp:setvalue value="orders@example.com" param="messageFrom"/>
<dsp:setvalue value="orders@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue beanvalue="Profile.email" param="messageTo"/>
<dsp:setvalue value="OneMonth" param="mailingName"/>





<p> <dsp:valueof bean="Profile.firstName">Sehr geehrter Kunde</dsp:valueof>
 <dsp:valueof bean="Profile.lastName"/>,
<p>

Die letzte Bestellung von <dsp:valueof bean="Profile.parentOrganization.name">Ihrer Firma</dsp:valueof> liegt bereits mehr als einen Monat zurück. Uns liegt daran, dass Motorprise den Anforderungen Ihres Unternehmens gerecht wird. Deshalb bieten wir Ihnen auf Ihre nächste Bestellung einen Nachlass von 10%. Der Nachlass wird automatisch einberechnet, wenn Sie sich das nächstemal anmelden und eine Bestellung aufgeben. 


<p>Mit freundlichen Grüßen,<br>
Motorprise, Inc.


</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/OneMonth.jsp#2 $$Change: 651448 $--%>
