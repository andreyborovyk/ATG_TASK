<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: OrderApprovedEmail -->
<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>
<dsp:setvalue paramvalue="message.orderOwnerProfile" param="orderOwnerProfile"/>

<dsp:setvalue paramvalue="orderOwnerProfile.email" param="messageTo"/>    
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="Order Approved" param="messageSubject"/>
<dsp:setvalue value="OrderApproved" param="mailingName"/>

<p>Dear <dsp:valueof param="orderOwnerProfile.firstName">Valued Customer</dsp:valueof>
 <dsp:valueof param="orderOwnerProfile.lastName"/>,<br>

<p>Order #<dsp:valueof param="order.id">No order id.</dsp:valueof> has been approved and placed.<br>

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

<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/order_approved.jsp#2 $$Change: 651448 $ --%>
