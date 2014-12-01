<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<declareparam name="order"
class="atg.commerce.order.Order"
description="the current order">

<tr valign=top>
  <td colspan=2>
  <table width=100% cellpadding=3 cellspacing=0 border=0>
    <tr>
      <td class=box-top>&nbsp;Rechnungsinfo</td>
    </tr>
  </table>
  </td>
</tr>
<%-- display all paymentGroups --%>
<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.paymentGroups"/>
  <dsp:param name="elementName" value="pGroup"/>

  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="pGroup.paymentMethod"/>
      <dsp:oparam name="giftCertificate">
      </dsp:oparam>

      <dsp:oparam name="creditCard">
        <tr valign=top>
          <td align=right><span class=smallb>Zahlungsweise
          <dsp:valueof param="count"/></span></td>
          <td width=75%><dsp:valueof param="pGroup.creditCardType"/>
          <dsp:valueof converter="creditcard" param="pGroup.creditCardNumber"/></td>
        </tr>
        <tr valign=top>
          <td align=right><span class=smallb>Betrag</span></td>
          <td width=75%><dsp:valueof converter="currency" locale="Profile.priceList.locale" param="pGroup.amount"/>
          </td>
        </tr>
        
        <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
        <tr valign=top>
              <td align=right><span class=smallb>Rechnungsanschrift</span></td>
              <td width=75%>
              <dsp:getvalueof id="pval0" param="pGroup.billingAddress">
                <dsp:include page="../common/DisplayAddress.jsp">
                  <dsp:param name="address" value="<%=pval0%>"/>
                </dsp:include>
              </dsp:getvalueof>

          </td>
        </tr>

      </dsp:oparam>

      <dsp:oparam name="invoiceRequest">
      <tr><td><dsp:img src="../images/d.gif" vspace="0"/></td></tr>
      <tr valign=top>
          <td align=right><span class=smallb>Zahlungsweise
          <dsp:valueof param="count"/></span></td>
          <td width=75%>Rechnung&nbsp;<dsp:valueof param="pGroup.PONumber"/>&nbsp;<dsp:valueof param="pGroup.requisitionNumber"/>
          </td>
      </tr>
      <tr valign=top>
        <td align=right><span class=smallb>Betrag</span></td>
        <td width=75%><dsp:valueof converter="currency" locale="Profile.priceList.locale" param="pGroup.amount"/>
        </td>
      </tr>

      <tr valign=top>
        <td align=right><span class=smallb>Rechnungsanschrift</span></td>
        <td width=75%>

        <dsp:getvalueof id="pval0" param="pGroup.billingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
        </td>
      </tr>
    </dsp:oparam>

   </dsp:droplet><%/*Switch*/%>
  </dsp:oparam>

</dsp:droplet><%/*ForEach*/%>

<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.costCenters"/>
  <dsp:param name="elementName" value="costCenter"/>

  <dsp:oparam name="outputStart">
    <tr valign=top>
      <td align=right><span class=smallb>Kostenstelle</span></td>
      <td width=75%>
  </dsp:oparam>

  <dsp:oparam name="output">
    <dsp:valueof param="costCenter.Identifier">n/a</dsp:valueof><BR>
  </dsp:oparam>

  <dsp:oparam name="outputEnd">
    </td>
    </tr>
  </dsp:oparam>

</dsp:droplet>

<dsp:droplet name="Switch">
 <dsp:param name="value" param="source"/>
 <dsp:oparam name="scheduledOrder">
 </dsp:oparam>
 <dsp:oparam name="default"> 

<dsp:droplet name="/atg/projects/b2bstore/order/ItemsSplitbyType">
  <dsp:param name="order" param="order"/>
  <dsp:param name="type" value="paymentGroup"/>

  <dsp:oparam name="true">
    <tr valign=top>
      <td></td>
      <td><span class=smallb><dsp:a href="split_payment.jsp?init=false">Zahlungsinfo bearbeiten</dsp:a></span></td>
    </tr>
  </dsp:oparam>
  
</dsp:droplet>

<dsp:droplet name="/atg/projects/b2bstore/order/ItemsSplitbyType">
  <dsp:param name="order" param="order"/>
  <dsp:param name="type" value="costCenter"/>

  <dsp:oparam name="true">
    <tr valign=top>
      <td></td>
      <td><span class=smallb><dsp:a href="confirm_cc_details.jsp">Kostenstelle-Info bearbeiten</dsp:a></span></td>
    </tr>
  </dsp:oparam>

</dsp:droplet>
  
<tr>
  <td></td>
  <td><span class=smallb><dsp:a href="billing.jsp">Rechnung bearbeiten </dsp:a></span></td>
</tr>       

 </dsp:oparam>
</dsp:droplet>

<%/*display all shippingGroups*/%>

<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.shippingGroups"/>
  <dsp:param name="elementName" value="sGroup"/>

  <dsp:oparam name="output">

    <dsp:droplet name="Switch">
      <dsp:param name="value" param="size"/>

      <dsp:oparam name="1">
        <dsp:getvalueof id="pval0" param="sGroup"><dsp:include page="displaySingleShipping.jsp"><dsp:param name="shippingGroup" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
     </dsp:oparam>

     <dsp:oparam name="default">
       <dsp:getvalueof id="pval0" param="sGroup"><dsp:include page="displayMulShipping.jsp"><dsp:param name="shippingGroup" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
     </dsp:oparam>

</dsp:droplet>

  </dsp:oparam>

  <dsp:oparam name="outputEnd">
    <tr>
      <td></td>
      <dsp:droplet name="Switch">
       <dsp:param name="value" param="source"/>
       <dsp:oparam name="scheduledOrder">
       </dsp:oparam>
       <dsp:oparam name="default"> 
        <td><span class=smallb><dsp:a href="shipping.jsp">Versand bearbeiten </dsp:a></span></td>
       </dsp:oparam>
      </dsp:droplet>
    </tr>       
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/displayOrder.jsp#2 $$Change: 651448 $--%>
