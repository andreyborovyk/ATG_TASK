<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="Your order will be delayed." param="messageSubject"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="OrderHasUnavailableItems" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.shipItemRels" param="itemRels"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>

<p> Dear <dsp:valueof param="profile.firstName">Valued Customer</dsp:valueof>
 <dsp:valueof param="profile.lastName"/>,

<p>The confirmation number for your order is: 
<dsp:valueof param="order.id">Not defined</dsp:valueof>

<p>Your order could not be completed at this time.

<!-- Shipping information -->
<table>
<tr valign=top>
  <td>
      <b>These items are temporarily unavailable:</b><br>
      <dsp:droplet name="ForEach">
	<dsp:param name="array" param="itemRels"/>
	<dsp:param name="elementName" value="itemRel"/>
	<dsp:oparam name="empty">
	  Please ignore this email.
	</dsp:oparam>
	<dsp:oparam name="output">
	   <dsp:valueof param="itemRel.commerceItem.auxiliaryData.catalogRef.displayName">No display name.</dsp:valueof>
	   is
	  <dsp:droplet name="Switch">
	    <dsp:param name="value" param="itemRel.state"/>
	    <dsp:oparam name="3">
	      PRE_ORDERED
	    </dsp:oparam>
	    <dsp:oparam name="4">
	      BACK_ORDERED
	    </dsp:oparam>	    
	    <dsp:oparam name="11">
	      OUT_OF_STOCK
	    </dsp:oparam>	    
	  </dsp:droplet>
	  <br>
       </dsp:oparam>
     </dsp:droplet>
 </td>
</tr>
<tr></tr>
</table>

<p>Please verify the following information.

<hr>

<!-- Itemized order -->
<br>
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayOrderSummary.jsp"><dsp:param name="order" value="<%=pval0%>"/><dsp:param name="displayStockStatus" value="false"/></dsp:include></dsp:getvalueof>

<!-- Shipping information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayShippingInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

<!-- Payment information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayPaymentInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/fulfillment/OrderHasUnavailableItems.jsp#2 $$Change: 651448 $--%>
