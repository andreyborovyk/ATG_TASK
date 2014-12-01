<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="An item has been removed from you order!" param="messageSubject"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="ItemRemoved" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>

<p> Dear <dsp:valueof param="profile.firstName">Valued Customer</dsp:valueof>
 <dsp:valueof param="profile.lastName"/>,

<p>The following item has been removed from your order:
<table cellspacing=2 cellpadding=0 border=0>
  <tr valign=top>
    <td>
	<dsp:valueof param="message.commerceItem.quantity"/>
    </td>
    <td>
	<dsp:valueof param="message.product.displayName"/>
    </td>
    <td>&nbsp;&nbsp;</td>
    <td>
	<dsp:valueof param="message.catalogRef.displayName"/>
    </td>
  </tr>
</table>
<p>The confirmation number for this order is: 
<dsp:valueof param="order.id">(Please contact customer service.)</dsp:valueof>

<hr>

<!-- Itemized order -->
<br>
<table cellspacing=2 cellpadding=0 border=0>
<tr>
<td><b>Quantity</b></td>
<td></td>
<td>&nbsp;&nbsp;</td>
<td><b>Product</b></td>
<td>&nbsp;&nbsp;</td>
<td><b>SKU</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>List Price</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>Sale Price</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>Total Price</b></td>
</tr>

<tr><td colspan=12><hr size=0></td></tr>


<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.commerceItems"/>
  <dsp:param name="elementName" value="item"/>
  <dsp:oparam name="output">
		<tr valign=top>
			<td>
				<dsp:valueof param="item.quantity">no quantity</dsp:valueof>
			</td>
			<td></td>
			<td>&nbsp;&nbsp;</td>
			<td>
			    <dsp:valueof param="item.auxiliaryData.productRef.displayName">No display name.</dsp:valueof>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td>
			    <dsp:valueof param="item.auxiliaryData.catalogRef.displayName"/>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td align=right>
				<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="item.priceInfo.listPrice">no price</dsp:valueof>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td align=right>
				<dsp:droplet name="Switch">
					<dsp:param name="value" param="item.priceInfo.onSale"/>
					<dsp:oparam name="true">
						<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="item.priceInfo.salePrice"/>
					</dsp:oparam>
				</dsp:droplet>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td align=right>
				<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="item.priceInfo.amount">no price</dsp:valueof>
			</td>
		</tr>
  </dsp:oparam>

  <dsp:oparam name="empty"><tr colspan=10 valign=top><td>No Items</td></tr></dsp:oparam>
</dsp:droplet>

<tr><td colspan=12><hr size=0></td></tr>
<tr>
<td colspan=11 align=right>Subtotal</td>
<td align=right>
	<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.amount">no price</dsp:valueof>
</td>
</tr>

<tr>
<td colspan=11 align=right>Shipping</td>
<td align=right>
	<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.shipping">no price</dsp:valueof>
</td>
</tr>

<tr>
<td colspan=11 align=right>Tax</td>
<td align=right>
	<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.tax">no price</dsp:valueof>
</td>
</tr>
        
<tr>
<td colspan=11 align=right><b>Total</b></td>
<td align=right>
	<b><dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.total">no price</dsp:valueof></b>
</td>
</tr>
</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/email/fulfillment/ItemRemovedFromOrder.jsp#2 $$Change: 651448 $--%>
