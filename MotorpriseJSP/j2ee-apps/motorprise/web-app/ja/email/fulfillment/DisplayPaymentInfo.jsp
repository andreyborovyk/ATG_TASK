<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<%/* A shopping cart-like display of order information  */%>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>

<dsp:droplet name="IsNull">
  <dsp:param name="value" param="order.paymentGroups"/>
  <dsp:oparam name="true">
   <p>このオーダーには、支払いグループがありません。
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="order.paymentGroups"/>
      <dsp:param name="elementName" value="pg"/>
      <dsp:oparam name="empty">
	支払いグループがありません。<br>
      </dsp:oparam>
      <dsp:oparam name="output">
	<dsp:droplet name="Switch">
	  <dsp:param name="value" param="pg.paymentMethod"/>
	  <dsp:oparam name="creditCard">
            <p><b>支払い情報：クレジットカード</b><br>
	    金額 = <dsp:valueof converter="currency" locale="Profile.priceList.locale" param="pg.amount"/><br>
	    クレジットカード番号<dsp:valueof param="pg.creditCardType"/>：# 
	    <dsp:valueof param="pg.creditCardNumber" converter="creditcard" groupingsize="4">no number</dsp:valueof><BR>
	    有効期限： <dsp:valueof param="pg.expirationMonth"/>/ 
	    <dsp:valueof param="pg.expirationYear"/><br>
	    請求先住所<br>
	    <dsp:valueof param="pg.billingAddress.lastName"/> 
	    <dsp:valueof param="pg.billingAddress.firstName"/><BR>
	    <dsp:valueof param="pg.billingAddress.address1"/> 
	    <dsp:valueof param="pg.billingAddress.address2"/><BR>
	    <dsp:valueof param="pg.billingAddress.city"/>, 
	    <dsp:valueof param="pg.billingAddress.state"/> 
	    <dsp:valueof param="pg.billingAddress.postalCode"/><BR>
	  </dsp:oparam>
	  <dsp:oparam name="giftCertificate">
            <p><b>支払い情報： <dsp:valueof param="pg.paymentMethod"/></b><br>
	    金額 = <dsp:valueof converter="currency" locale="Profile.priceList.locale" param="pg.amount"/><br>
	    商品券で支払い<dsp:valueof param="pg.giftCertificateNumber"/>：<br>
	  </dsp:oparam>
          <dsp:oparam name="default">
            <p><b>支払い情報： <dsp:valueof param="pg.paymentMethod"/></b><br>
	    金額 = <dsp:valueof converter="currency" locale="Profile.priceList.locale" param="pg.amount"/><br>
          </dsp:oparam>
	</dsp:droplet>
      </dsp:oparam>
    </dsp:droplet>    
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/email/fulfillment/DisplayPaymentInfo.jsp#2 $$Change: 651448 $--%>
