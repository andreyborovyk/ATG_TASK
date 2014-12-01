<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<%/* A shopping cart-like display of order information */%>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<table cellspacing=2 cellpadding=0 border=0>
<tr>
<td><b>数量</b></td>
<td></td>
<td>&nbsp;&nbsp;</td>
<td><b>製品</b></td>
<td>&nbsp;&nbsp;</td>
<td><b>SKU</b></td>
<td>&nbsp;&nbsp;</td>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td><b>在庫あり？</b></td>
    <td>&nbsp;&nbsp;</td>
  </dsp:oparam>
</dsp:droplet>

<td align=right><b>表示価格</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>割引価格</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>合計価格</b></td>
</tr>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <tr><td colspan=14><hr size=0></td></tr>
  </dsp:oparam>
  <dsp:oparam name="default">
    <tr><td colspan=12><hr size=0></td></tr>
  </dsp:oparam>
</dsp:droplet>

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
			    <dsp:valueof param="item.auxiliaryData.productRef.displayName">不明</dsp:valueof>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td>
			    <dsp:valueof param="item.auxiliaryData.catalogRef.displayName">不明</dsp:valueof>
			</td>
			<td>&nbsp;&nbsp;</td>

			    <dsp:droplet name="Switch">
			      <dsp:param name="value" param="displayStockStatus"/>
			      <dsp:oparam name="true">    
				<td>
				    <dsp:droplet name="InventoryLookup">
				      <dsp:param name="itemId" param="item.catalogRefId"/>
				      <dsp:param name="useCache" value="true"/>
				      <dsp:oparam name="output">
					<dsp:droplet name="Switch">
					  <dsp:param name="value" param="inventoryInfo.availabilityStatus"/>
					  <dsp:oparam name="1000">
					    <b>はい</b>
					  </dsp:oparam>
					  <dsp:oparam name="default">
					    いいえ
					  </dsp:oparam>
				        </dsp:droplet>
				      </dsp:oparam>
				    </dsp:droplet>
				</td>
				<td>&nbsp;&nbsp;</td>
			      </dsp:oparam>
			    </dsp:droplet>

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
					<dsp:oparam name="false">
					        販売価格なし
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
<dsp:droplet name="/atg/dynamo/droplet/Compare">
<dsp:param name="obj1" param="order.priceInfo.amount"/>
<dsp:param name="obj2" param="order.priceInfo.rawSubtotal"/>
<dsp:oparam name="equal">
</dsp:oparam>
<dsp:oparam name="default">
  <dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <tr><td colspan=14><hr size=0></td></tr>
    <tr>
      <td colspan=13 align=right>割引</td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <tr><td colspan=12><hr size=0></td></tr>
    <tr>
      <td colspan=11 align=right>割引</td>
  </dsp:oparam>
</dsp:droplet>

  <td align=right>
	
        <b><dsp:getvalueof id="pval0" param="order.priceInfo.discountAmount">
             <dsp:include page="../../common/DisplayCurrencyType.jsp">
               <dsp:param name="currency" value="<%=pval0%>"/>
             </dsp:include>
           </dsp:getvalueof></b>  	  	
	
  </td>
</tr>
</dsp:oparam>
</dsp:droplet>
    

<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <tr><td colspan=14><hr size=0></td></tr>
    <tr>
      <td colspan=13 align=right>小計</td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <tr><td colspan=12><hr size=0></td></tr>
    <tr>
      <td colspan=11 align=right>小計</td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
  <dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.amount">価格なし</dsp:valueof>
</td>
</tr>

<tr>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td colspan=13 align=right>配達</td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <td colspan=11 align=right>配達</td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
	<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.shipping">価格なし</dsp:valueof>
</td>
</tr>

<tr>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td colspan=13 align=right>税</td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <td colspan=11 align=right>税</td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
	<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.tax">価格なし</dsp:valueof>
</td>
</tr>

<tr>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td colspan=13 align=right><b>合計</b></td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <td colspan=11 align=right><b>合計</b></td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
	<b><dsp:valueof converter="currency" locale="Profile.priceList.locale" param="order.priceInfo.total">価格なし</dsp:valueof></b>
</td>
</tr>
</table>
<br>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/email/fulfillment/DisplayOrderSummary.jsp#2 $$Change: 651448 $--%>
