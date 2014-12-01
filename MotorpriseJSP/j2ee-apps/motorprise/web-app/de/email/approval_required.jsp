<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: ApprovalRequiredEmail -->
<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>
<dsp:setvalue paramvalue="message.profile.approvers[0]" param="approver"/>

<dsp:setvalue paramvalue="approver.email" param="messageTo"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
--%>
<dsp:setvalue value="Bestellung erfordert Bewilligung" param="messageSubject"/>
<dsp:setvalue value="ApprovalRequired" param="mailingName"/>

<p> Sehr geehrter<dsp:valueof param="approver.firstName">Bestellungsprüfer</dsp:valueof>
 <dsp:valueof param="approver.lastName"/>,

<p>Wir haben eine Bestellung erhalten, für die Ihre Bewilligung erforderlich ist.<br>
<p>Bestellungskennung: <dsp:valueof param="order.id"/> <!-- Need link to order approval page.--> <br>
Käufer: <dsp:valueof param="profile.firstName">Sehr geehrter Kunde</dsp:valueof>
 <dsp:valueof param="profile.lastName"/><br>

<!-- Itemized order -->
<br>
<dsp:include page="fulfillment/DisplayOrderSummary.jsp">
  <dsp:param name="order" param="order"/>
  <dsp:param name="displayStockStatus" value="false"/>
</dsp:include>

<!-- Shipping information -->
<dsp:include page="fulfillment/DisplayShippingInfo.jsp">
  <dsp:param name="order" param="order"/>
</dsp:include>

<!-- Payment information -->
<dsp:include page="fulfillment/DisplayPaymentInfo.jsp">
  <dsp:param name="order" param="order"/>
</dsp:include>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/approval_required.jsp#2 $$Change: 651448 $ --%>
