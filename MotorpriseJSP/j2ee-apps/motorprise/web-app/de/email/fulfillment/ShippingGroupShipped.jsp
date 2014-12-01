<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="Ihre Bestellung wurde ausgeliefert!" param="messageSubject"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="OrderShipped" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.shippingGroup" param="shippingGroup"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>

<p> <dsp:valueof param="profile.firstName">Sehr geehrter Kunde</dsp:valueof>
 <dsp:valueof param="profile.lastName"/>,

<p> Die Bestätigungsnummer für Ihre Bestellung ist: 
<dsp:valueof param="order.id">Nicht definiert</dsp:valueof>

<p>Die folgenden Artikel aus Ihrer Bestellung wurden ausgeliefert.

<!-- Shipping information -->
<table>
<tr valign=top>
  <td>
      <b>Diese Artikel wurden ausgeliefert:</b><br>
      <dsp:droplet name="ForEach">
	<dsp:param name="array" param="shippingGroup.commerceItemRelationships"/>
	<dsp:param name="elementName" value="itemRel"/>
	<dsp:oparam name="empty">
	  In dieser Versandgruppe waren keine Artikel vorhanden.
	</dsp:oparam>
	<dsp:oparam name="output">
	  <dsp:droplet name="Switch">
	    <dsp:param name="value" param="itemRel.relationshipTypeAsString"/>
	    <dsp:oparam name="SHIPPINGQUANTITY">
	      <dsp:valueof param="itemRel.quantity">Keine Menge angegeben</dsp:valueof> von
	    </dsp:oparam> 
	    <dsp:oparam name="SHIPPINGQUANTITYREMAINING">
	      Der Rest von
	    </dsp:oparam> 
          </dsp:droplet>
	      <dsp:valueof param="itemRel.commerceItem.auxiliaryData.catalogRef.displayName">Kein Anzeigename.</dsp:valueof>
	  <br>
       </dsp:oparam>
     </dsp:droplet>
 </td>
 <td>    
     <p><b>Folgende Daten werden verwendet:</b><br>
     <dsp:droplet name="Switch">
       <dsp:param name="value" param="shippingGroup.shippingGroupClassType"/>
       <dsp:oparam name="hardgoodShippingGroup">
	 <i>Versand per <dsp:valueof param="shippingGroup.shippingMethod">Keine Versandart</dsp:valueof> an:</i><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.firstName"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.lastName"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.address1"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.address2"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.city"/>, 
	 <dsp:valueof param="shippingGroup.shippingAddress.state"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.postalCode"/><BR>
       </dsp:oparam>
       <dsp:oparam name="b2bHardgoodShippingGroup">
	 <i>Versand per <dsp:valueof param="shippingGroup.shippingMethod">Keine Versandart</dsp:valueof> an:</i><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.firstName"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.lastName"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.address1"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.address2"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.city"/>, 
	 <dsp:valueof param="shippingGroup.shippingAddress.state"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.postalCode"/><BR>
       </dsp:oparam>
       <dsp:oparam name="electronicShippingGroup">
	 <i>Versand per E-Mail an:</i><BR>
	 <dsp:valueof param="shippingGroup.emailAddress">unbekannte E-Mail-Adresse</dsp:valueof><BR>
       </dsp:oparam>
     </dsp:droplet>
 </td>
</tr>
<tr></tr>
</table>

<p>Bitte überprüfen Sie die folgenden Informationen.

<hr>

<!-- Itemized order -->
<br>
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayOrderSummary.jsp"><dsp:param name="order" value="<%=pval0%>"/><dsp:param name="displayStockStatus" value="false"/></dsp:include></dsp:getvalueof>

<!-- Shipping information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayShippingInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

<!-- Payment information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayPaymentInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/fulfillment/ShippingGroupShipped.jsp#2 $$Change: 651448 $--%>
