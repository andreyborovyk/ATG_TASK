<%@ taglib uri="dsp" prefix="dsp" %>
<%@ taglib uri="core" prefix="core" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/order/OrderLookup"/>

<%--
  Checks whether the order under approval uses requisition instead of PO nos. If ordder
  uses requisitions we should allow the approver to enter PO nos. corresponding to
  requisition no., so we redirect the approver to add_po_number.jhtml page, otherwise
  redirect to approve_order.jhtml page where approver can approve the order.
--%>

<dsp:droplet name="OrderLookup">
  <dsp:param name="orderId" param="orderId"/>
  <dsp:oparam name="output">
    <%--
      Get the order and iterate through the payment groups of the order checking for 
      requistion numbers.   Redirect to add_po_number.jhtml page if at least one payment 
      group is found with a requistion number that is not null or the empty string.
    --%>
    <dsp:setvalue paramvalue="result" param="order"/>
    <dsp:droplet name="ForEach">
      <dsp:param name="array" param="result.paymentGroups"/>
      <dsp:oparam name="output">
        <dsp:droplet name="Switch">
        <dsp:param name="value" param="element.requisitionNumber"/>
        <dsp:oparam name="unset"></dsp:oparam>
	<dsp:oparam name=""></dsp:oparam>
        <dsp:oparam name="default">
	  <dsp:getvalueof id="order_id" param="orderId">
	  <core:createUrl id="addPoNumber" url="../user/add_po_number.jsp">
	    <core:urlParam param="orderId" value="<%=order_id%>"/>
	    <%-- System.out.println("Found requisition - redirect to PO page"); --%>
	    <core:redirect url="<%=addPoNumber.getNewUrl()%>"/>
	  </core:createUrl>
	  </dsp:getvalueof>
        </dsp:oparam>
      </dsp:droplet><%/* End Switch Droplet */%>
    </dsp:oparam>
  </dsp:droplet><%/* End ForEach Droplet */%>
  </dsp:oparam><%/* End OrderLookup.output OPARAM */%>
</dsp:droplet><%/* End OrderLookup Droplet */%>

<%--
  We found no payment groups with requisition, so redirect to approve order page.
--%>

<dsp:getvalueof id="order_id" param="orderId">
<core:createUrl id="addPoNumber" url="../user/approve_order.jsp">
  <core:urlParam param="orderId" value="<%=order_id%>"/>
  <%-- System.out.println("Found no requisition - redirect to approval page"); --%>
  <core:redirect url="<%=addPoNumber.getNewUrl()%>"/>
</core:createUrl>
</dsp:getvalueof>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/user/IsOrderWithRequisition.jsp#2 $$Change: 651448 $--%>
