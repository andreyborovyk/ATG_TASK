<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: OneMonthEmail -->
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:setvalue value="10% off your next order." param="messageSubject"/>
<%-- 
<dsp:setvalue value="orders@example.com" param="messageFrom"/>
<dsp:setvalue value="orders@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue beanvalue="Profile.email" param="messageTo"/>
<dsp:setvalue value="OneMonth" param="mailingName"/>





<p> Dear <dsp:valueof bean="Profile.firstName">Valued Customer</dsp:valueof>
 <dsp:valueof bean="Profile.lastName"/>,
<p>

Your last order for <dsp:valueof bean="Profile.parentOrganization.name">your company</dsp:valueof> was placed more than a month ago. We want to ensure that Motorprise is meeting all of your company's needs, so we are offering 10% off of your next order. This discount will automatically be applied when you log in and place your next order. 


<p>Sincerely,<br>
Motorprise, Inc.


</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/OneMonth.jsp#2 $$Change: 651448 $--%>
