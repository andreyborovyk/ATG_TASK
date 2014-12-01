<%@ taglib uri="http://www.atg.com/dsp.tld" prefix="dsp" %>
<dsp:page>

<!-- Title: ApprovalRequiredEmail -->
<dsp:setvalue paramvalue="message.order" param="order"/>
<dsp:setvalue paramvalue="message.profile" param="profile"/>
<dsp:setvalue paramvalue="message.profile.approvers[0]" param="approver"/>

<dsp:setvalue paramvalue="approver.email" param="messageTo"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageReplyTo"/>
<dsp:setvalue value="DCS5Team@example.com" param="messageFrom"/>
<dsp:setvalue value="Order approval required" param="messageSubject"/>
<dsp:setvalue value="ApprovalRequired" param="mailingName"/>

<p> Dear <dsp:valueof param="approver.firstName">Order Approver</dsp:valueof>
 <dsp:valueof param="approver.lastName"/>,

<p>An order has been placed that requires your approval.<br>
<p>Order id: <dsp:valueof param="order.id"/> <!-- Need link to order approval page.--> <br>
Buyer: <dsp:valueof param="profile.firstName">Valued Customer</dsp:valueof>
 <dsp:valueof param="profile.lastName"/><br>

<!-- Itemized order -->
<br>
<dsp:getvalueof id="pval0" param="order"><dsp:include page="/Dynamo/commerce/en/email_templates/DisplayOrderSummary.jsp" flush="true"><dsp:param name="order" value="<%=pval0%>"/><dsp:param name="displayStockStatus" value="false"/></dsp:include></dsp:getvalueof>

<!-- Shipping information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="/Dynamo/commerce/en/email_templates/DisplayShippingInfo.jsp" flush="true"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

<!-- Payment information -->
<dsp:getvalueof id="pval0" param="order"><dsp:include page="/Dynamo/commerce/en/email_templates/DisplayPaymentInfo.jsp" flush="true"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>



</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/B2BCommerce/html/en/email_templates/jsp/approval_required.jsp#2 $$Change: 651448 $--%>
