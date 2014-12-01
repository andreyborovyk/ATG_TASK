<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/commerce/order/purchase/CommitOrderFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/purchase/CancelOrderFormHandler"/>
<dsp:importbean bean="/atg/commerce/order/scheduled/ScheduledOrderFormHandler"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" チェックアウト"/></dsp:include>

<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=3>
      <dsp:include page="../common/BrandNav.jsp"></dsp:include>
    </td>
    <!- this needs to be put somewhere -->
    <dsp:include page="../common/FormError.jsp"></dsp:include>
  </tr>
  <dsp:droplet name="IsEmpty">
   <dsp:param bean="Profile.costCenters" name="value"/>
    <dsp:oparam name="true">
      <%-- put breadcrumbs here --%>
      <tr bgcolor="#DBDBDB">
        <td colspan=3 height=18><span class=small>
          &nbsp; <dsp:a href="cart.jsp">現在のオーダー</dsp:a> &gt; 
          <dsp:a href="shipping.jsp">配達</dsp:a> &gt; 
          <dsp:a href="billing.jsp"> 請求</dsp:a> &gt; 確認 &nbsp;</span>
        </td>
      </tr>
    </dsp:oparam>
    <dsp:oparam name="false">
     <tr bgcolor="#DBDBDB">
      <%-- put breadcrumbs here --%>
      <td colspan=3 height=18><span class=small>
        &nbsp; <dsp:a href="cart.jsp">現在のオーダー</dsp:a> &gt; 
        <dsp:a href="shipping.jsp">配達</dsp:a> &gt; 
        <dsp:a href="billing.jsp"> 請求</dsp:a> &gt; 
        <dsp:a href="cost_centers.jsp">コストセンタ</dsp:a> &gt; 確認 &nbsp;</span>
      </td>
    </tr>
  </dsp:oparam>
  </dsp:droplet>

  <tr>
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
    <td valign="top" width=745>
    <table border=0 cellpadding=4 width=80%>
      <tr>
        <td valign="top" colspan=3>

        <dsp:form action="../user/scheduled_order_new.jsp" method="post">
        <table border=0 cellpadding=4 width=100%>
          <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
          <tr>
            <td colspan=3><span class="big">オーダーの確認</span></td>
          </tr>
          <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

          <%-- billing and shipping information --%>
          
          <dsp:getvalueof id="pval0" bean="ShoppingCart.current"><dsp:include page="displayPaymentInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

          <%-- Display cost center info --%>
          <dsp:getvalueof id="pval0" bean="ShoppingCart.current"><dsp:include page="displayCostCenterInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

          <%-- Check whether items are split by payment groups and cost centers --%>
          <%-- Commenting this code for now...
          <dsp:droplet name="/atg/projects/b2bstore/order/ItemsSplitbyType">
            <dsp:param bean="ShoppingCart.current" name="order"/>
            <dsp:param name="type" value="paymentGroup"/>

            <dsp:oparam name="true">
              <tr valign=top>
                <td></td>
                <td><span class=smallb><dsp:a href="split_payment.jsp?init=false">View line item billing details</dsp:a></span></td>
              </tr>
            </dsp:oparam>
  
          </dsp:droplet>
            --%>
            <dsp:droplet name="/atg/projects/b2bstore/order/ItemsSplitbyType">
            <dsp:param bean="ShoppingCart.current" name="order"/>
            <dsp:param name="type" value="costCenter"/>

            <dsp:oparam name="true">
              <tr valign=top>
                <td></td>
                <td><span class=smallb><dsp:a href="confirm_cc_details.jsp">コストセンタの品目の詳細を表示 </dsp:a></span></td>
              </tr>
            </dsp:oparam>
          </dsp:droplet>

          <%-- Display billing info link --%>
          <dsp:include page="displayBillingLink.jsp"></dsp:include>
          <%-- Display Shipping Information --%>
          <dsp:getvalueof id="pval0" bean="ShoppingCart.current"><dsp:include page="displayShippingInfo.jsp"><dsp:param name="order" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

          <%-- Display shipping link --%>
          <dsp:droplet name="ForEach">
            <dsp:param bean="ShoppingCart.current.shippingGroups" name="array"/>
            <dsp:oparam name="outputStart">
              <dsp:droplet name="Switch">
                <dsp:param name="value" param="size"/>
                <dsp:oparam name="1">
                  <tr>
                    <td></td>
                    <td><span class=smallb><dsp:a href="shipping.jsp">配達の編集 </dsp:a></span></td>
                  </tr>       
                </dsp:oparam>
                <dsp:oparam name="default">
                  <tr>
                    <td></td>
                    <td><span class=smallb><dsp:a href="ship_to_multiple.jsp">配達の編集 </dsp:a></span></td>
                  </tr>       
                </dsp:oparam>
              </dsp:droplet>
            </dsp:oparam>
          </dsp:droplet>

          <%-- vertical space --%>
          <tr>
            <td><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <%-- vertical space --%>
          <tr>
            <td><dsp:img src="../images/d.gif" vspace="0"/></td>
          </tr>
          <tr>
            <td></td><td>
           
            
            <input name="prototypeOrderId" type="hidden" value="<dsp:valueof bean="ShoppingCart.current.id"/>">

                                                
            <dsp:input bean="CommitOrderFormHandler.commitOrder" type="submit" value="今すぐオーダー"/>

            <dsp:input bean="CommitOrderFormHandler.orderId" beanvalue="ShoppingCart.current.id" type="hidden"/>
            
            <dsp:input bean="CommitOrderFormHandler.commitOrderSuccessURL" type="hidden" value="../checkout/thank_you.jsp"/>
            
            <dsp:input bean="CommitOrderFormHandler.commitOrderErrorURL" type="hidden" value="../checkout/confirmation.jsp"/>

            <dsp:input bean="CancelOrderFormHandler.cancelCurrentOrder" type="submit" value="オーダーのキャンセル"/>

            <dsp:input bean="CancelOrderFormHandler.orderIdToCancel" beanvalue="ShoppingCart.current.id" type="hidden"/>

            <dsp:input bean="CancelOrderFormHandler.cancelOrderSuccessURL" type="hidden" value="../checkout/order_not_submitted_cancelled.jsp"/>

            <dsp:input bean="CancelOrderFormHandler.cancelOrderErrorURL" type="hidden" value="../checkout/confirmation.jsp"/>
            <p>
            <%-- <dsp:input submitvalue="bean:/Constants.null" bean="ScheduledOrderFormHandler.repositoryId" type="submit" value="Create scheduled order"/> --%>
            <dsp:input submitvalue="" bean="ScheduledOrderFormHandler.repositoryId" type="submit" value="スケジュール済みオーダーの作成"/> 
            </td>
          </tr>
          <tr>
            <td></td>
          </tr>
        </table>
        </dsp:form>
      </td>
    </tr>
  </table>
</td>
</tr>
</table>

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/confirmation.jsp#2 $$Change: 651448 $--%>
