<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="Your order has shipped!" param="messageSubject"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="OrderShipped" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.shippingGroup" param="shippingGroup"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>

<p> Dear <dsp:valueof param="profile.firstName">Valued Customer</dsp:valueof>
 <dsp:valueof param="profile.lastName"/>,

<p>The confirmation number for your order is: 
<dsp:valueof param="order.id">Not defined</dsp:valueof>

<p>The following pieces of your order have shipped.

<!-- Shipping information -->
<table>
<tr valign=top>
  <td>
      <b>These items shipped:</b><br>
      <dsp:droplet name="ForEach">
	<dsp:param name="array" param="shippingGroup.commerceItemRelationships"/>
	<dsp:param name="elementName" value="itemRel"/>
	<dsp:oparam name="empty">
	  There were no items in this shipping group.
	</dsp:oparam>
	<dsp:oparam name="output">
	  <dsp:droplet name="Switch">
	    <dsp:param name="value" param="itemRel.relationshipTypeAsString"/>
	    <dsp:oparam name="SHIPPINGQUANTITY">
	      <dsp:valueof param="itemRel.quantity">No quantity specified</dsp:valueof> of
	    </dsp:oparam> 
	    <dsp:oparam name="SHIPPINGQUANTITYREMAINING">
	      The rest of 
	    </dsp:oparam> 
          </dsp:droplet>
	      <dsp:valueof param="itemRel.commerceItem.auxiliaryData.catalogRef.displayName">No display name.</dsp:valueof>
	  <br>
       </dsp:oparam>
     </dsp:droplet>
 </td>
 <td>    
     <p><b>Using this information:</b><br>
     <dsp:droplet name="Switch">
       <dsp:param name="value" param="shippingGroup.shippingGroupClassType"/>
       <dsp:oparam name="hardgoodShippingGroup">
	 <i>Ship via <dsp:valueof param="shippingGroup.shippingMethod">No shipping method</dsp:valueof> to:</i><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.firstName"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.lastName"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.address1"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.address2"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.city"/>, 
	 <dsp:valueof param="shippingGroup.shippingAddress.state"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.postalCode"/><BR>
       </dsp:oparam>
       <dsp:oparam name="b2bHardgoodShippingGroup">
	 <i>Ship via <dsp:valueof param="shippingGroup.shippingMethod">No shipping method</dsp:valueof> to:</i><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.firstName"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.lastName"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.address1"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.address2"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.city"/>, 
	 <dsp:valueof param="shippingGroup.shippingAddress.state"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.postalCode"/><BR>
       </dsp:oparam>
       <dsp:oparam name="electronicShippingGroup">
	 <i>Ship via email to:</i><BR>
	 <dsp:valueof param="shippingGroup.emailAddress">unknown email address</dsp:valueof><BR>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/fulfillment/ShippingGroupShipped.jsp#2 $$Change: 651448 $--%>
