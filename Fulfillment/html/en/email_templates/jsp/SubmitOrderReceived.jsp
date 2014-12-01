<%@ taglib uri="http://www.atg.com/dsp.tld" prefix="dsp" %>
<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="Your order has been received." param="messageSubject"/>

<%/*
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
*/%>
<dsp:setvalue value="OrderShipped" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>

<p> Dear <dsp:valueof param="profile.firstName">Valued Customer</dsp:valueof>
 <dsp:valueof param="profile.lastName"/>,

<p>Your order has been received.  Thank you for your business.
You will be notified when your order ships.

<p>The confirmation number for your order is: 
<dsp:valueof param="order.id">Not defined</dsp:valueof>

<p>Please verify the following information.

<hr>

<!-- Itemized order -->
<br>
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayOrderSummary.jsp" flush="true"><dsp:param name="order" value="<%=pval0%>"/><dsp:param name="displayStockStatus" value="true"/></dsp:include></dsp:getvalueof>

<!-- Shipping information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayShippingInfo.jsp" flush="true"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

<!-- Payment information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayPaymentInfo.jsp" flush="true"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>


</dsp:page>
<%-- @version $Id: //product/DCS/version/10.0.3/release/Fulfillment/html/en/email_templates/jsp/SubmitOrderReceived.jsp#2 $$Change: 651448 $--%>
