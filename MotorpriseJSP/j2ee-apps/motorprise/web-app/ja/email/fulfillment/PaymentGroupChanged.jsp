<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="支払い額の更新" param="messageSubject"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="PaymentGroupChanged" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.paymentGroups" param="paymentGroups"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>

<p> 拝啓<dsp:valueof param="profile.lastName">お客</dsp:valueof>
<dsp:valueof param="profile.firstName"/>様、

<p>このオーダーの確認番号は次のとおりです：
<dsp:valueof param="order.id">（カスタマサービスにご連絡ください。）</dsp:valueof>

<hr>

<p>
<dsp:droplet name="ForEach">
  <dsp:param name="array" param="paymentGroups"/>
  <dsp:param name="elementName" value="paymentGroup"/>
  <dsp:oparam name="outputStart">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="message.subType"/>
      <dsp:oparam name="PaymentGroupCredited">
	<dsp:setvalue value="お支払額を請求させていただきます。" param="messageSubject"/>
	お支払いのご請求先：<br>
      </dsp:oparam>
      <dsp:oparam name="PaymentGroupDebited">
	<dsp:setvalue value="お支払いを受領しました。" param="messageSubject"/>
	受領したお支払い額：<br>
      </dsp:oparam>
      <dsp:oparam name="PaymentGroupDebitFailed">
	<dsp:setvalue value="お支払いが未納です。" param="messageSubject"/>
	未納のお支払い額：<br>
      </dsp:oparam>
      <dsp:oparam name="default">
	<dsp:setvalue value="お支払い額を更新させていただきました。" param="messageSubject"/>
	更新したお支払い額：<br>	
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="paymentGroup.paymentMethod"/>
      <dsp:oparam name="creditCard">
	<dsp:valueof param="paymentGroup.creditCardType"/> # 
	<dsp:valueof param="paymentGroup.creditCardNumber" converter="creditcard" groupingsize="4">番号なし</dsp:valueof> :
	</dsp:oparam>
      <dsp:oparam name="giftCertificate">
	商品券番号 <dsp:valueof param="paymentGroup.giftCertificateNumber"/> : 
      </dsp:oparam>
    </dsp:droplet>
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="message.subType"/>
      <dsp:oparam name="PaymentGroupCredited">
	    請求済み金額 = <dsp:valueof converter="currency" locale="Profile.priceList.locale" param="paymentGroup.amountCredited"/><br>	
      </dsp:oparam>
      <dsp:oparam name="PaymentGroupDebited">
	    金額 = <dsp:valueof converter="currency" locale="Profile.priceList.locale" param="paymentGroup.amountDebited"/><br>	
      </dsp:oparam>
      <dsp:oparam name="PaymentGroupDebitFailed">
	    金額 = <dsp:valueof converter="currency" locale="Profile.priceList.locale" param="paymentGroup.amount"/><br>	
      </dsp:oparam>
      <dsp:oparam name="default">
	    新しい金額 = <dsp:valueof converter="currency" locale="Profile.priceList.locale" param="paymentGroup.amount"/><br>	
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="empty">
    <dsp:setvalue value="お支払い額を更新させていただきました。" param="messageSubject"/>
    お支払い額を更新させていただきました。<br>    
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/email/fulfillment/PaymentGroupChanged.jsp#2 $$Change: 651448 $--%>
