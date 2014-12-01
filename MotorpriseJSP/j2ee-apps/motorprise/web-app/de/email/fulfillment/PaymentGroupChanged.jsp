<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="Zahlungsaktualisierung!" param="messageSubject"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="PaymentGroupChanged" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.paymentGroups" param="paymentGroups"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>

<p> <dsp:valueof param="profile.firstName">Sehr geehrter Kunde</dsp:valueof> 
 <dsp:valueof param="profile.lastName"/>,

<p> Die Bestätigungsnummer für diese Bestellung ist: 
<dsp:valueof param="order.id">(Bitte setzen Sie sich mit dem Kundendienst in Verbindung.)</dsp:valueof>

<hr>

<p>
<dsp:droplet name="ForEach">
  <dsp:param name="array" param="paymentGroups"/>
  <dsp:param name="elementName" value="paymentGroup"/>
  <dsp:oparam name="outputStart">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="message.subType"/>
      <dsp:oparam name="PaymentGroupCredited">
	<dsp:setvalue value="Die Zahlung wurde gutgeschrieben!" param="messageSubject"/>
	Die Zahlung wurde gutgeschrieben an:<br>
      </dsp:oparam>
      <dsp:oparam name="PaymentGroupDebited">
	<dsp:setvalue value="Die Zahlung wurde empfangen!" param="messageSubject"/>
	Die Zahlung wurde empfangen für:<br>
      </dsp:oparam>
      <dsp:oparam name="PaymentGroupDebitFailed">
	<dsp:setvalue value="Bei der Zahlung ist ein Fehler aufgetreten!" param="messageSubject"/>
	Ein Fehler ist aufgetreten bei der Zahlung für:<br>
      </dsp:oparam>
      <dsp:oparam name="default">
	<dsp:setvalue value="Die Zahlung wurde aktualisiert!" param="messageSubject"/>
	Die Zahlung wurde aktualisiert für:<br>	
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="paymentGroup.paymentMethod"/>
      <dsp:oparam name="creditCard">
	<dsp:valueof param="paymentGroup.creditCardType"/> #
	<dsp:valueof param="paymentGroup.creditCardNumber" converter="creditcard" groupingsize="4">Keine Nummer</dsp:valueof> :
      </dsp:oparam>
      <dsp:oparam name="giftCertificate">
	Geschenkgutschein-Nr.<dsp:valueof param="paymentGroup.giftCertificateNumber"/>  
      </dsp:oparam>
    </dsp:droplet>
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="message.subType"/>
      <dsp:oparam name="PaymentGroupCredited">
	    Gutgeschriebener Betrag =<dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="paymentGroup.amountCredited"/><br> 	
      </dsp:oparam>
      <dsp:oparam name="PaymentGroupDebited">
	    Betrag = <dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="paymentGroup.amountDebited"/><br>	
      </dsp:oparam>
      <dsp:oparam name="PaymentGroupDebitFailed">
	    Betrag = <dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="paymentGroup.amount"/><br>	
      </dsp:oparam>
      <dsp:oparam name="default">
	    Neuer Betrag = <dsp:valueof euro="true" locale="de_DE" symbol="&euro;" param="paymentGroup.amount"/><br>	
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="empty">
    <dsp:setvalue value="Die Zahlung wurde aktualisiert!" param="messageSubject"/>
    Die Zahlung wurde aktualisiert.<br>    
  </dsp:oparam>
</dsp:droplet>


<!-- Itemized order -->
<br>
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayOrderSummary.jsp"><dsp:param name="order" value="<%=pval0%>"/><dsp:param name="displayStockStatus" value="false"/></dsp:include></dsp:getvalueof>

<!-- Shipping information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayShippingInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

<!-- Payment information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayPaymentInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/fulfillment/PaymentGroupChanged.jsp#2 $$Change: 651448 $--%>
