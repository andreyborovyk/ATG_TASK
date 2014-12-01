<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- Title: OrderRejectedEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>
<dsp:setvalue paramvalue="message.orderOwnerProfile" param="orderOwnerProfile"/>

<dsp:setvalue paramvalue="orderOwnerProfile.email" param="messageTo"/>    
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="Bestellung nicht bewilligt" param="messageSubject"/>
<dsp:setvalue value="OrderRejected" param="mailingName"/>

<p><dsp:valueof param="orderOwnerProfile.firstName">Sehr geehrter Kunde</dsp:valueof>
 <dsp:valueof param="orderOwnerProfile.lastName"/>,<br>

<p>Die von Ihnen zur Bewilligung eingereichte Bestellung Nr. <dsp:valueof param="order.id">Keine Bestellungskennung</dsp:valueof> wurde nicht bewilligt.<br> 

<b>Prüfermeldungen</b>
<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.approverMessages"/>
  <dsp:param name="elementName" value="message"/>
  <dsp:oparam name="output">
    - <dsp:valueof param="message">Keine Gründe</dsp:valueof><br>
  </dsp:oparam>
  <dsp:oparam name="empty">
    Keine Meldungen
  </dsp:oparam>
</dsp:droplet>

<br>

<b>Gründe für Bewilligungspflicht der Bestellung:</b>
<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.approvalSystemMessages"/>
  <dsp:param name="elementName" value="message"/>
  <dsp:oparam name="output">
    - <dsp:valueof param="message">Keine Gründe</dsp:valueof><br>
  </dsp:oparam>
  <dsp:oparam name="empty">
    Keine Systemmeldungen
  </dsp:oparam>
</dsp:droplet>

<br>

<!-- Itemized order -->
<br>
<dsp:include page="fulfillment/DisplayOrderSummary.jsp">
  <dsp:param name="order" param="order"/>
  <dsp:param name="displayStockStatus" value="false"/>
</dsp:include>

<!-- Shipping information -->
<dsp:include page="fulfillment/DisplayShippingInfo.jsp">
  <dsp:param name="order" param="order"/>
</dsp:include>

<!-- Payment information -->
<dsp:include page="fulfillment/DisplayPaymentInfo.jsp">
  <dsp:param name="order" param="order"/>
</dsp:include>

</dsp:page>

<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/email/order_rejected.jsp#2 $$Change: 651448 $ --%>
