<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

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
          <td align=right><span class=smallb>請求先住所</span></td>
          <td width=75%>
            <dsp:valueof param="pGroup.billingAddress.address1"/><br>
            <dsp:valueof param="pGroup.billingAddress.address2"/><br>
            <dsp:valueof param="pGroup.billingAddress.city"/>
            <dsp:valueof param="pGroup.billingAddress.state"/>
            <dsp:valueof param="pGroup.billingAddress.postalCode"/><br>
          </td>
        </tr>
        <tr valign=top>
          <td align=right><span class=smallb>支払い方法</span></td>
          <td width=75%><dsp:valueof param="pGroup.creditCardType"/>
            <dsp:valueof param="pGroup.creditCardNumber" converter="creditcard"/></td>
        </tr>

      </dsp:oparam>
      <dsp:oparam name="invoiceRequest">
        <tr><td><img src="../images/d.gif" vspace=0></td></tr>
        <tr valign=top>
          <td align=right><span class=smallb>請求先住所</span></td>
          <td width=75%>
            <dsp:valueof param="pGroup.billingAddress.address1"/><br>
            <dsp:valueof param="pGroup.billingAddress.address2"/><br>
            <dsp:valueof param="pGroup.billingAddress.city"/>
            <dsp:valueof param="pGroup.billingAddress.state"/>
            <dsp:valueof param="pGroup.billingAddress.postalCode"/><br>
          </td>
        </tr>
        <tr valign=top>
          <td align=right><span class=smallb>支払い方法</span></td>
          <td width=75%>インボイス </td>
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
        送付先 <dsp:valueof param="sGroup.emailAddress"/>
      </dsp:oparam>
      <dsp:oparam name="default">

        <tr valign=top>
          <td align=right><span class=smallb>配達先住所</span></td>
          <td width=75%>
            <dsp:valueof param="sGroup.shippingAddress.address1"/><br>
            <dsp:valueof param="sGroup.shippingAddress.address2"/><br>
            <dsp:valueof param="sGroup.shippingAddress.city"/>
            <dsp:valueof param="sGroup.shippingAddress.state"/>
            <dsp:valueof param="sGroup.shippingAddress.postalCode"/><br>
          </td>
        </tr>
        <tr valign=top>
          <td align=right><span class=smallb>配達方法</span></td>
          <td width=75%><dsp:valueof param="sGroup.shippingMethod">配達方法の指定なし</dsp:valueof></td>
        </tr>

      </dsp:oparam>
    </dsp:droplet><%/*Switch*/%>
  </dsp:oparam>
</dsp:droplet><%/*ForEach*/%>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/user/order_details.jsp#2 $$Change: 651448 $--%>
