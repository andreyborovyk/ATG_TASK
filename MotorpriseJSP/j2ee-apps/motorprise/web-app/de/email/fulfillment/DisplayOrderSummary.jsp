<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%/* A shopping cart-like display of order information */%>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/inventory/InventoryLookup"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<table cellspacing=2 cellpadding=0 border=0>
<tr>
<td><b>Menge</b></td>
<td></td>
<td>&nbsp;&nbsp;</td>
<td><b>Produkt</b></td>
<td>&nbsp;&nbsp;</td>
<td><b>Artikelposition</b></td>
<td>&nbsp;&nbsp;</td>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td><b>Auf Lager?</b></td>
    <td>&nbsp;&nbsp;</td>
  </dsp:oparam>
</dsp:droplet>

<td align=right><b>Listenpreis</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>Verkaufspreis</b></td>
<td>&nbsp;&nbsp;</td>
<td align=right><b>Gesamtpreis</b></td>
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
			    <dsp:valueof param="item.quantity">Keine Menge</dsp:valueof>
			</td>
			<td></td>
			<td>&nbsp;&nbsp;</td>
			<td>
			    <dsp:valueof param="item.auxiliaryData.productRef.displayName">Unbekannt</dsp:valueof>
			</td>
			<td>&nbsp;&nbsp;</td>
			<td>
			    <dsp:valueof param="item.auxiliaryData.catalogRef.displayName">Unbekannt</dsp:valueof>
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
					    <b>Ja</b>
					  </dsp:oparam>
					  <dsp:oparam name="default">
					    Nein
					  </dsp:oparam>
				        </dsp:droplet>
				      </dsp:oparam>
				    </dsp:droplet>
				</td>
				<td>&nbsp;&nbsp;</td>
			      </dsp:oparam>
			    </dsp:droplet>

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
					<dsp:oparam name="false">
					        Kein Verkaufspreis
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
      <td colspan=13 align=right>Skonto</td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <tr><td colspan=12><hr size=0></td></tr>
    <tr>
      <td colspan=11 align=right>Skonto</td>
  </dsp:oparam>
</dsp:droplet>

  <td align=right>
    <b>                
      <dsp:getvalueof id="pval0" param="order.priceInfo.discountAmount">
        <dsp:include page="../../common/DisplayCurrencyType.jsp">
          <dsp:param name="currency" value="<%=pval0%>"/>
        </dsp:include>
      </dsp:getvalueof>
    </b>                 

  </td>
</tr>
</dsp:oparam>
</dsp:droplet>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <tr><td colspan=14><hr size=0></td></tr>
    <tr>
      <td colspan=13 align=right>Zwischensumme</td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <tr><td colspan=12><hr size=0></td></tr>
    <tr>
      <td colspan=11 align=right>Zwischensumme</td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
  <dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="order.priceInfo.amount">Kein Preis</dsp:valueof>
</td>
</tr>

<tr>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td colspan=13 align=right>Versand</td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <td colspan=11 align=right>Versand</td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
	<dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="order.priceInfo.shipping">Kein Preis</dsp:valueof>
</td>
</tr>

<tr>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td colspan=13 align=right>Steuer</td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <td colspan=11 align=right>Steuer</td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
	<dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="order.priceInfo.tax">Kein Preis</dsp:valueof>
</td>
</tr>
        
<tr>
<dsp:droplet name="Switch">
  <dsp:param name="value" param="displayStockStatus"/>
  <dsp:oparam name="true">    
    <td colspan=13 align=right><b>Summe</b></td>
  </dsp:oparam>
  <dsp:oparam name="default">
    <td colspan=11 align=right><b>Summe</b></td>
  </dsp:oparam>
</dsp:droplet>

<td align=right>
	<b><dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="order.priceInfo.total">Kein Preis</dsp:valueof></b>
</td>
</tr>
</table>
<br>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/fulfillment/DisplayOrderSummary.jsp#2 $$Change: 651448 $--%>
