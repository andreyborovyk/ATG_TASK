<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<!-- Title: FulfillmentEmail -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceItem"/>

<dsp:setvalue value="お客様のオーダーを発送いたしました。" param="messageSubject"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="OrderShipped" param="mailingName"/>

<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.shippingGroup" param="shippingGroup"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>

<p> <dsp:valueof param="profile.firstName">お客様へ</dsp:valueof>
<dsp:valueof param="profile.lastName"/>、

<p>オーダーの確認番号は次のとおりです：
<dsp:valueof param="order.id">定義されていません</dsp:valueof>

<p>オーダーの内、次の商品を発送いたしました。

<!-- Shipping information -->
<table>
<tr valign=top>
  <td>
      <b>次のアイテムを発送しました：</b><br>
      <dsp:droplet name="ForEach">
	<dsp:param name="array" param="shippingGroup.commerceItemRelationships"/>
	<dsp:param name="elementName" value="itemRel"/>
	<dsp:oparam name="empty">
	  この配達グループにはアイテムがありません。
	</dsp:oparam>
	<dsp:oparam name="output">
	  <dsp:droplet name="Switch">
	    <dsp:param name="value" param="itemRel.relationshipTypeAsString"/>
	    <dsp:oparam name="SHIPPINGQUANTITY">
	      の<dsp:valueof param="itemRel.quantity">数量が指定されていません。</dsp:valueof>
	    </dsp:oparam> 
	    <dsp:oparam name="SHIPPINGQUANTITYREMAINING">
	      未配達分： 
	    </dsp:oparam> 
          </dsp:droplet>
	      <dsp:valueof param="itemRel.commerceItem.auxiliaryData.catalogRef.displayName">表示名なし。</dsp:valueof>
	  <br>
       </dsp:oparam>
     </dsp:droplet>
 </td>
 <td>    
     <p><b>次の情報を使用：</b><br>
     <dsp:droplet name="Switch">
       <dsp:param name="value" param="shippingGroup.shippingGroupClassType"/>
       <dsp:oparam name="hardgoodShippingGroup">
	 <dsp:valueof param="shippingGroup.shippingMethod">配達方法の指定なし</dsp:valueof>による配達先：<BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.lastName"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.firstName"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.address1"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.address2"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.city"/>, 
	 <dsp:valueof param="shippingGroup.shippingAddress.state"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.postalCode"/><BR>
       </dsp:oparam>
       <dsp:oparam name="b2bHardgoodShippingGroup">
	 <dsp:valueof param="shippingGroup.shippingMethod">配達方法の指定なし</dsp:valueof>による配達先：<BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.firstName"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.lastName"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.address1"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.address2"/><BR>
	 <dsp:valueof param="shippingGroup.shippingAddress.city"/>, 
	 <dsp:valueof param="shippingGroup.shippingAddress.state"/> 
	 <dsp:valueof param="shippingGroup.shippingAddress.postalCode"/><BR>
       </dsp:oparam>
       <dsp:oparam name="electronicShippingGroup">
	 電子メールによる配達先：<BR>
	 <dsp:valueof param="shippingGroup.emailAddress">不明な電子メールアドレス</dsp:valueof><BR>
       </dsp:oparam>
     </dsp:droplet>
 </td>
</tr>
<tr></tr>
</table>

<p>次の情報を確認してください。

<hr>

<!-- Itemized order -->
<br>
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayOrderSummary.jsp"><dsp:param name="order" value="<%=pval0%>"/><dsp:param name="displayStockStatus" value="false"/></dsp:include></dsp:getvalueof>

<!-- Shipping information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayShippingInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

<!-- Payment information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="DisplayPaymentInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/email/fulfillment/ShippingGroupShipped.jsp#2 $$Change: 651448 $--%>
