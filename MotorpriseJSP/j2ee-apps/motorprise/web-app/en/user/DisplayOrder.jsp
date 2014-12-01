<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<!-- billing and shipping information -->
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<decleareparam name="order"
               class="atg.commerce.order.Order"
               description="the current order">

<%/*display all paymentGroups*/%>
<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.paymentGroups"/>
  <dsp:param name="elementName" value="pGroup"/>
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="pGroup.paymentMethod"/>
      <dsp:oparam name="giftCertificate">
      </dsp:oparam>
      <dsp:oparam name="creditCard">

        <!-- vertical space -->
        <tr><td><img src="../images/d.gif" vspace=0></td></tr>
        <tr valign=top>
          <td></td>
          <td align=right><span class=smallb>Billing address</span></td>
          <td width=75%>
            <dsp:valueof param="pGroup.billingAddress.address1"/><br>
            <dsp:valueof param="pGroup.billingAddress.address2"/><br>
            <dsp:valueof param="pGroup.billingAddress.city"/>
            <dsp:valueof param="pGroup.billingAddress.state"/>
            <dsp:valueof param="pGroup.billingAddress.postalCode"/><br>
          </td>
        </tr>
        <tr valign=top>
          <td></td>
          <td align=right><span class=smallb>Payment method</span></td>
          <td width=75%><dsp:valueof param="pGroup.creditCardType"/>
            <dsp:valueof param="pGroup.creditCardNumber" converter="creditcard"/></td>
        </tr>

      </dsp:oparam>
      <dsp:oparam name="invoiceRequest">
        <tr><td><img src="../images/d.gif" vspace=0></td></tr>
        <tr valign=top>
          <td></td>
          <td align=right><span class=smallb>Billing address</span></td>
          <td width=75%>
            <dsp:valueof param="pGroup.billingAddress.address1"/><br>
            <dsp:valueof param="pGroup.billingAddress.address2"/><br>
            <dsp:valueof param="pGroup.billingAddress.city"/>
            <dsp:valueof param="pGroup.billingAddress.state"/>
            <dsp:valueof param="pGroup.billingAddress.postalCode"/><br>
          </td>
        </tr>
        <tr valign=top>
          <td></td>
          <td align=right><span class=smallb>Payment method</span></td>
          <td width=75%>Invoice </td>
        </tr>

      </dsp:oparam>
    </dsp:droplet><%/*Switch*/%>
  </dsp:oparam>
</dsp:droplet><%/*ForEach*/%>

<%/*display all shippingGroups*/%>
<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.shippingGroups"/>
  <dsp:param name="elementName" value="sGroup"/>
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="sGroup.shippingGroupClassType"/>
      <dsp:oparam name="electronicShippingGroup">
        Delivered to <dsp:valueof param="sGroup.emailAddress"/>
      </dsp:oparam>
      <dsp:oparam name="default">

        <tr valign=top>
          <td></td>
          <td colspan="2"><b>Shipping Group <dsp:valueof param="count"/></b></td>
        </tr>
        <tr valign=top>
          <td></td>
          <td align=right>Shipping address</b></td>
          <td width=75%>
            <dsp:valueof param="sGroup.shippingAddress.address1"/><br>
            <dsp:valueof param="sGroup.shippingAddress.address2"/><br>
            <dsp:valueof param="sGroup.shippingAddress.city"/>
            <dsp:valueof param="sGroup.shippingAddress.state"/>
            <dsp:valueof param="sGroup.shippingAddress.postalCode"/><br>
          </td>
        </tr>
        <tr valign=top>
          <td></td>
          <td align=right>Shipping method</b></td>
          <td width=75%><dsp:valueof param="sGroup.shippingMethod">no shipping method</dsp:valueof></td>
        </tr>
        <tr>
          <td></td>
          <td colspan="2">
            <table border=0 cellpadding=4 cellspacing=1 width=100%>
             <tr bgcolor="#FF9900" valign=bottom>
                <td colspan=2><span class=smallbw>Part #</span></td>
                <td colspan=2><span class=smallbw>Name</span></td>
                <td colspan=2 align=center><span class=smallbw>Price</span></td>
                <td colspan=2 align=center><span class=smallbw>Qty</span></td>
                <td colspan=2 align=center><span class=smallbw>Total</span></td>
                <td colspan=2><span class=smallbw>Cost center</span></b></td>
              </tr>



          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="sGroup.CommerceItemRelationships"/>
            <dsp:param name="elementName" value="CiRel"/>
            <dsp:oparam name="output">
              <tr valign=top>
              <td><dsp:valueof param="CiRel.commerceItem.auxiliaryData.catalogRef.manufacturer_part_number"/></td>
              <td>&nbsp;</td>
              <td><dsp:a href="../catalog/product.jsp"><dsp:param name="id" param="CiRel.commerceItem.auxiliaryData.productId"/>
               <dsp:valueof param="CiRel.commerceItem.auxiliaryData.productRef.displayName"/></dsp:a></td>
              <td>&nbsp;</td>
              <td align=right><dsp:valueof converter="currency" locale="Profile.priceList.locale" param="CiRel.commerceItem.auxiliaryData.catalogRef.listPrice"/></td>
              <td>&nbsp;</td>
              <td align=center><dsp:valueof param="CiRel.commerceItem.quantity"/></td>
              <td>&nbsp;</td>

              <%/* display total list price and any discounted total price */%>
              <dsp:getvalueof id="pval0" param="CiRel.commerceItem"><dsp:include page="../common/display_amount.jsp"><dsp:param name="item" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

              <td>&nbsp;</td>
              <td><dsp:valueof param="CiRel.commerceItem.costCenter">N/A</dsp:valueof></td>
              <td>&nbsp;</td>
              </tr>
          </dsp:oparam>
          </dsp:droplet><%/*ForEach*/%>
         </table>
          </td>
        </tr>

      </dsp:oparam>
    </dsp:droplet><%/*Switch*/%>
  </dsp:oparam>
</dsp:droplet><%/*ForEach*/%>
  <tr>
    <td colspan="3"></td>
  </tr>

  <tr>
    <td colspan="3" align=right>Subtotal <dsp:valueof converter="currency" param="order.priceInfo.rawSubTotal"/></td>
  </tr>
  <tr>
    <td colspan="3" align=right>Shipping <dsp:valueof converter="currency" param="order.priceInfo.shipping"/></td>
  </tr>
  <tr>
    <td colspan="3" align=right>Sales tax <dsp:valueof converter="currency" param="order.priceInfo.tax"/></td>
  </tr>
  <tr>
    <td colspan="3" align=right>Total <b><dsp:valueof converter="currency" param="order.priceInfo.total"/></b></td>
  </tr>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/user/DisplayOrder.jsp#2 $$Change: 651448 $--%>
