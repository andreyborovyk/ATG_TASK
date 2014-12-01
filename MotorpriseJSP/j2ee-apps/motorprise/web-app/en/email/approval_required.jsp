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
<dsp:setvalue value="Order approval required" param="messageSubject"/>
<dsp:setvalue value="ApprovalRequired" param="mailingName"/>

<p> Dear <dsp:valueof param="approver.firstName">Order Approver</dsp:valueof>
 <dsp:valueof param="approver.lastName"/>,

<p>An order has been placed that requires your approval.<br>
<p>Order id: <dsp:valueof param="order.id"/> <!-- Need link to order approval page.--> <br>
Buyer: <dsp:valueof param="profile.firstName">Valued Customer</dsp:valueof>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/approval_required.jsp#2 $$Change: 651448 $ --%>
