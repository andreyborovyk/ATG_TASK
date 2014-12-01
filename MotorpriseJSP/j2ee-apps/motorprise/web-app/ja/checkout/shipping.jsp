<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/commerce/ShoppingCart"/>
<dsp:importbean bean="/atg/commerce/pricing/UserPricingModels"/>
<dsp:importbean bean="/atg/commerce/pricing/AvailableShippingMethods"/>
<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupDroplet"/>
<dsp:importbean bean="/atg/commerce/order/purchase/ShippingGroupFormHandler"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" チェックアウト"/></dsp:include>

<%--
The ShippingGroupDroplet is used here to initialize the user's HardgoodShippingGroups.
--%>
  <dsp:droplet name="ShippingGroupDroplet">
  <dsp:param name="clear" value="true"/>
  <dsp:param name="shippingGroupTypes" value="hardgoodShippingGroup"/>
  <dsp:param name="initShippingGroups" value="true"/>
  <dsp:param name="initShippingInfos" value="true"/>
  <dsp:oparam name="output">


<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=3>
      <dsp:include page="../common/BrandNav.jsp"></dsp:include>
    </td>
  </tr>

  <tr bgcolor="#DBDBDB">
    <%/* put breadcrumbs here */%>
    <td colspan=3 height=18><span class=small>
       &nbsp; <dsp:a href="cart.jsp">現在のオーダー</dsp:a> &gt; 配達 &nbsp;</span>
    </td>
  </tr>
  <tr>
    <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
    <td valign="top" width=745>
    <dsp:form action="shipping.jsp" method="post">
    <table border=0 cellpadding=4 width=80%>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr valign=top>
        <td colspan=2><span class=big>配達 </span>
        <dsp:include page="../common/FormError.jsp"></dsp:include></td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr valign=top>
        <td align=right width=25%><span class=smallb>配達先住所</span></td>
        <td>

        <%--
        This displays the Profile.defaultShippingAddress, and if Profile.shippingAddrs
        is not empty then we provide a link for shipping to an alternate address.
        --%>
        <dsp:getvalueof id="pval0" bean="Profile.defaultShippingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
        <dsp:droplet name="IsEmpty">
        <dsp:param bean="Profile.shippingAddrs" name="value"/>
        <dsp:oparam name="false">
          <p><span class=smallb><dsp:a href="shipping_address.jsp">別の住所への配達</dsp:a></span>
        </dsp:oparam></dsp:droplet>
        </td>
      </tr>
            
      <tr>
        <td></td>
        <td>
          <dsp:droplet name="IsEmpty">
            <dsp:param bean="Profile.shippingAddrs" name="value"/>
            <dsp:oparam name="false">
              <span class=smallb><dsp:a href="ship_to_multiple.jsp?init=true">複数の住所への配達</dsp:a></span>
            </dsp:oparam>
          </dsp:droplet>
        </td>
      </tr>
      <tr><td><dsp:img src="../images/d.gif" vspace="2"/></td></tr>
      <tr>

        <%--
        The AvailableShippingMethods servlet bean permits the user to select a
        shipping method that is applied to the current ShippingGroup.
        --%>
        
        <dsp:droplet name="IsNull">
          <dsp:param bean="ShippingGroupDroplet.defaultShippingGroup" name="value"/>
          <dsp:oparam name="false">
            <td align=right><span class=smallb>配達方法</span></td>
            <dsp:droplet name="AvailableShippingMethods">
              <dsp:param bean="ShippingGroupDroplet.defaultShippingGroup" name="shippingGroup"/>
              <dsp:param bean="UserPricingModels.shippingPricingModels" name="pricingModels"/>
              <dsp:param bean="Profile" name="profile"/>
              <dsp:oparam name="output">
              <td>
              <dsp:select bean="ShippingGroupDroplet.defaultShippingGroup.shippingMethod">
              <dsp:droplet name="ForEach">
                <dsp:param name="array" param="availableShippingMethods"/>
                <dsp:param name="elementName" value="method"/>
                <dsp:oparam name="output">
                  <dsp:getvalueof id="methodName" idtype="String" param="method">
                    <dsp:option value="<%=methodName%>"/><dsp:valueof param="method"/>
                  </dsp:getvalueof>
                </dsp:oparam>
              </dsp:droplet>
              </dsp:select>
              </td>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:oparam>
        </dsp:droplet>
      </tr>
      <tr>
        <td></td>
        <td><br>

        <%--
        Invoke the applyShippingGroups handler and redirect to billing.jsp upon success.
        --%>
        
        <dsp:input bean="ShippingGroupFormHandler.applyShippingGroupsErrorURL" type="hidden" value="shipping.jsp"/>
        <dsp:input bean="ShippingGroupFormHandler.applyShippingGroupsSuccessURL" type="hidden" value="billing.jsp"/>
        <dsp:input bean="ShippingGroupFormHandler.applyShippingGroups" type="submit" value="続行"/></td>
    </tr>
    </table>
    </dsp:form>
    </td>
  </tr>
</table>


  </dsp:oparam>
</dsp:droplet>

</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/shipping.jsp#2 $$Change: 651448 $--%>
