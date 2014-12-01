<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="Ein Artikel wurde aus Ihrem Auftrag entfernt!" param="messageSubject"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="ItemRemoved" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>

<p> <dsp:valueof param="profile.firstName">Sehr geehrter Kunde</dsp:valueof>
 <dsp:valueof param="profile.lastName"/>,

<p> Der folgende Artikel wurde aus Ihrem Auftrag entfernt:
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
<p>Die Bestätigungsnummer für diese Bestellung ist: 
<dsp:valueof param="order.id">(Bitte setzen Sie sich mit dem Kundendienst in Verbindung.)</dsp:valueof>

<hr>

<!-- Itemized order -->
<br>
<table cellspacing=2 cellpadding=0 border=0>
<tr>
<td><b>Menge</b></td>
<td></td>
<td>&nbsp;&nbsp;</td>
<td><b>Produkt</b></td>
<td>&nbsp;&nbsp;</td>
<td><b>Artikelposition</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>Listenpreis</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>Verkaufspreis</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>Gesamtpreis</b></td>
</tr>

<tr><td colspan=12><hr size=0></td></tr>


<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.commerceItems"/>
  <dsp:param name="elementName" value="item"/>
  <dsp:oparam name="output">
		<tr valign=top>
			<td>
				<dsp:valueof param="item.quantity">Keine Menge</dsp:valueof>
			</td>
			<td></td>
			<td>&nbsp;&nbsp;</td>
			<td>
			    <dsp:valueof param="item.auxiliaryData.productRef.displayName">Kein Anzeigename.</dsp:valueof>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td>
			    <dsp:valueof param="item.auxiliaryData.catalogRef.displayName"/>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td align=right>
				<dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="item.priceInfo.listPrice">Kein Preis</dsp:valueof>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td align=right>
				<dsp:droplet name="Switch">
					<dsp:param name="value" param="item.priceInfo.onSale"/>
					<dsp:oparam name="true">
						<dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="item.priceInfo.salePrice"/>
					</dsp:oparam>
				</dsp:droplet>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td align=right>
				<dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="item.priceInfo.amount">Kein Preis</dsp:valueof>
			</td>
		</tr>
  </dsp:oparam>

  <dsp:oparam name="empty"><tr colspan=10 valign=top><td>Keine Artikel</td></tr></dsp:oparam>
</dsp:droplet>

<tr><td colspan=12><hr size=0></td></tr>
<tr>
<td colspan=11 align=right>Zwischensumme</td>
<td align=right>
	<dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="order.priceInfo.amount">Kein Preis</dsp:valueof>
</td>
</tr>

<tr>
<td colspan=11 align=right>Versand</td>
<td align=right>
	<dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="order.priceInfo.shipping">Kein Preis</dsp:valueof>
</td>
</tr>

<tr>
<td colspan=11 align=right>Steuer</td>
<td align=right>
	<dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="order.priceInfo.tax">Kein Preis</dsp:valueof>
</td>
</tr>
        
<tr>
<td colspan=11 align=right><b>Summe</b></td>
<td align=right>
	<b><dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="order.priceInfo.total">Kein Preis</dsp:valueof></b>
</td>
</tr>
</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/fulfillment/ItemRemovedFromOrder.jsp#2 $$Change: 651448 $--%>
