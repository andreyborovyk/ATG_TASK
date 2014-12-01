<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<!-- Title: GiftCertificate  -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
--%>
<dsp:setvalue value="商品券が届いています。" param="messageSubject"/>
<dsp:setvalue value="GiftCertificate" param="mailingName"/>

<h3>これは電子メール商品券のサンプルです。希望のテンプレートを使用できます。
別のテンプレートに変更するには、
<code>/atg/commerce/fulfillment/SoftgoodFulfiller.giftCertificateEmailTemplate</code> の
設定を使用してください。</h3><br>

<p>このテンプレートで、商品券購入者のプロファイル、
購入された商品券の数、および商品券の
額にアクセスします。<br>

<p><dsp:valueof param="purchaser.firstName"></dsp:valueof> <dsp:valueof param="purchaser.lastName">お客</dsp:valueof>
様からの 
<dsp:droplet name="Switch">
  <dsp:param name="value" param="quantity"/>
  <dsp:oparam name="1">
    商品券 
  </dsp:oparam>
  <dsp:oparam name="default">
    <dsp:valueof param="quantity"/> 商品券 
  </dsp:oparam>
</dsp:droplet>

をお預かりしています。金額は次のとおりです： 
<dsp:valueof converter="currency" locale="Profile.priceList.locale" param="giftCertificate.amount"/> 
<br>

<p>使用していただくクレーム コードは、
商品券の repositoryId と同じです。<br>

<p>次のクレームコードを使用してください： <dsp:valueof param="giftCertificate.repositoryId"/><br>
  
<p> ありがとうございました。 
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/email/fulfillment/GiftCertificate.jsp#2 $$Change: 651448 $--%>
