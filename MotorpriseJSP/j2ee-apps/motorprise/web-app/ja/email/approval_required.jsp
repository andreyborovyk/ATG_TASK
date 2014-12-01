<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<!-- Title: ApprovalRequiredEmail -->
<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>
<dsp:setvalue paramvalue="message.profile.approvers[0]" param="approver"/>

<dsp:setvalue paramvalue="approver.email" param="messageTo"/>
<%-- 
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
--%>
<dsp:setvalue value="オーダー承認が必要です" param="messageSubject"/>
<dsp:setvalue value="ApprovalRequired" param="mailingName"/>

<p> <dsp:valueof param="approver.firstName">オーダー承認担当者様</dsp:valueof>
<dsp:valueof param="approver.lastName"/>、

<p>承りましたオーダーの承認をお願いいたします。<br>
<p>オーダー ID： <dsp:valueof param="order.id"/> <!-- Need link to order approval page.--> <br>
購入者：<dsp:valueof param="profile.firstName">お客様</dsp:valueof>
<dsp:valueof param="profile.lastName"/><br>

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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/email/approval_required.jsp#2 $$Change: 651448 $ --%>
