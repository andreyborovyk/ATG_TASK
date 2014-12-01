<%@ taglib uri="http://www.atg.com/dsp.tld" prefix="dsp" %>
<dsp:page>

<!-- Title: OrderRejectedEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>
<dsp:setvalue paramvalue="message.orderOwnerProfile" param="orderOwnerProfile"/>

<dsp:setvalue paramvalue="orderOwnerProfile.email" param="messageTo"/>    
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
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
<dsp:getvalueof id="pval0" param="order"><dsp:include page="/Dynamo/commerce/en/email_templates/DisplayOrderSummary.jsp" flush="true"><dsp:param name="order" value="<%=pval0%>"/><dsp:param name="displayStockStatus" value="false"/></dsp:include></dsp:getvalueof>

<!-- Shipping information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="/Dynamo/commerce/en/email_templates/DisplayShippingInfo.jsp" flush="true"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

<!-- Payment information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="/Dynamo/commerce/en/email_templates/DisplayPaymentInfo.jsp" flush="true"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/B2BCommerce/html/en/email_templates/jsp/order_rejected.jsp#2 $$Change: 651448 $--%>
