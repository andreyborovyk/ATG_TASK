<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: OrderRejectedEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>
<dsp:setvalue paramvalue="message.orderOwnerProfile" param="orderOwnerProfile"/>

<dsp:setvalue paramvalue="orderOwnerProfile.email" param="messageTo"/>    
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="Order Rejected" param="messageSubject"/>
<dsp:setvalue value="OrderRejected" param="mailingName"/>

<p>Dear <dsp:valueof param="orderOwnerProfile.firstName">Valued Customer</dsp:valueof>
 <dsp:valueof param="orderOwnerProfile.lastName"/>,<br>

<p>The order you submitted for approval, #<dsp:valueof param="order.id">No order id</dsp:valueof>, has been rejected.<br> 

<b>Approver Messages</b>
<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.approverMessages"/>
  <dsp:param name="elementName" value="message"/>
  <dsp:oparam name="output">
    - <dsp:valueof param="message">No reasons</dsp:valueof><br>
  </dsp:oparam>
  <dsp:oparam name="empty">
    No messages
  </dsp:oparam>
</dsp:droplet>

<br>

<b>Reason order required approval:</b>
<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.approvalSystemMessages"/>
  <dsp:param name="elementName" value="message"/>
  <dsp:oparam name="output">
    - <dsp:valueof param="message">No reasons</dsp:valueof><br>
  </dsp:oparam>
  <dsp:oparam name="empty">
    No system messages
  </dsp:oparam>
</dsp:droplet>

<br>

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

<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/order_rejected.jsp#2 $$Change: 651448 $ --%>
