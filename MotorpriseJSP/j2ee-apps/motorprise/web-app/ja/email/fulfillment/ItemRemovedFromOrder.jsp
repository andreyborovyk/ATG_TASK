<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="アイテムがお客様のオーダーから削除されました。" param="messageSubject"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="ItemRemoved" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>

<p><dsp:valueof param="profile.lastName">お客</dsp:valueof>
<dsp:valueof param="profile.firstName"/>様へ、

<p>次のアイテムがお客様のオーダーから削除されています：
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
<p>このオーダーの確認番号は次のとおりです：
<dsp:valueof param="order.id">（カスタマサービスにご連絡ください。）</dsp:valueof>

<hr>

<!-- Itemized order -->
<br>
<table cellspacing=2 cellpadding=0 border=0>
<tr>
<td><b>数量</b></td>
<td></td>
<td>&nbsp;&nbsp;</td>
<td><b>製品</b></td>
<td>&nbsp;&nbsp;</td>
<td><b>SKU</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>表示価格</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>割引価格</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>合計価格</b></td>
</tr>

<tr><td colspan=12><hr size=0></td></tr>


<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.commerceItems"/>
  <dsp:param name="elementName" value="item"/>
  <dsp:oparam name="output">
		<tr valign=top>
			<td>
				<dsp:valueof param="item.quantity">数量なし</dsp:valueof>
			</td>
			<td></td>
			<td>&nbsp;&nbsp;</td>
			<td>
			    <dsp:valueof param="item.auxiliaryData.productRef.displayName">表示名なし。</dsp:valueof>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td>
			    <dsp:valueof param="item.auxiliaryData.catalogRef.displayName"/>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td align=right>
				<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="item.priceInfo.listPrice">価格なし</dsp:valueof>
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
				<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="item.priceInfo.amount">価格なし</dsp:valueof>
			</td>
		</tr>
  </dsp:oparam>

  <dsp:oparam name="empty"><tr colspan=10 valign=top><td>アイテムなし</td></tr></dsp:oparam>
</dsp:droplet>

<tr><td colspan=12><hr size=0></td></tr>
<tr>
<td colspan=11 align=right>小計</td>
<td align=right>
	<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.amount">価格なし</dsp:valueof>
</td>
</tr>

<tr>
<td colspan=11 align=right>配達</td>
<td align=right>
	<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.shipping">価格なし</dsp:valueof>
</td>
</tr>

<tr>
<td colspan=11 align=right>税</td>
<td align=right>
	<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.tax">価格なし</dsp:valueof>
</td>
</tr>
        
<tr>
<td colspan=11 align=right><b>合計</b></td>
<td align=right>
	<b><dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.total">価格なし</dsp:valueof></b>
</td>
</tr>
</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/email/fulfillment/ItemRemovedFromOrder.jsp#2 $$Change: 651448 $--%>
