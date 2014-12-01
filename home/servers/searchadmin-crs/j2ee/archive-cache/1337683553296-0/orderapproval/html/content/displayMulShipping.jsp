<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>

<declareparam name="sGroup"
class="atg.commerce.order.ShippingGroup"
description="the Shipping Group">

<i18n:bundle baseName="atg.commerce.gears.orderapproval.OrderApprovalResources" localeAttribute="userLocale" changeResponseLocale="false" />

<tr><td colspan=2 class=box-top><span class="medium"><b>
  <dsp:getvalueof id="count" param="count">
    <i18n:message key="shippingGroup">
      <i18n:messageArg value="<%=count%>" />
    </i18n:message>
  </dsp:getvalueof>
  </b></span>
  <hr size="1" noshade color="#666666">
</td></tr>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="sGroup.shippingGroupClassType"/>
  <dsp:oparam name="electronicShippingGroup">
    <dsp:getvalueof id="email_address" param="sGroup.emailAddress">
      <i18n:message key="deliveredTo">
        <i18n:messageArg value="<%=email_address%>"/>
      </i18n:message>
    </dsp:getvalueof>
  </dsp:oparam>
  <dsp:oparam name="default">

  <tr valign=top>
          <td align=right><span class=smallb><i18n:message key="shippingAddress"/></span></td>
          <td>
            <dsp:getvalueof id="pval0" param="sGroup.shippingAddress"><dsp:include page="DisplayAddress.jsp" flush="false"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
        </td>
        </tr>
        <tr valign=top>
          <td align=right><span class=smallb><i18n:message key="shippingMethod"/></span></td>
          <td><span class=small><dsp:valueof param="sGroup.shippingMethod"><i18n:message key="noShippingMethod"/></dsp:valueof></span></td>
        </tr>
        <tr valign=top>
          <td align=right><span class=smallb><i18n:message key="itemsTitle"/></span></td>
          <td>
              <table border=0 cellpadding=0 cellspacing=5 width=100%>
                <tr valign=bottom>
                  <td><span class=smallb><i18n:message key="qtyTitle"/></span></td>
                  <td><span class=smallb><i18n:message key="partNumTitle"/></span></td>
                  <td><span class=smallb><i18n:message key="nameTitle"/></span></td>
                  <td align=right><span class=smallb><i18n:message key="priceTitle"/></span></td>
                </tr>

          <tr><td colspan=4><hr size="1" noshade color="#666666"></td></tr>
          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="sGroup.CommerceItemRelationships"/>
            <dsp:param name="elementName" value="CiRel"/>
            <dsp:oparam name="output">
              <tr valign=top>
                <td align=center><span class=small><dsp:valueof param="CiRel.quantity"/></span></td>
              <td><span class=small><dsp:valueof param="CiRel.commerceItem.auxiliaryData.catalogRef.manufacturer_part_number"/></span></td>
                  <td><span class=small><dsp:param name="id" param="CiRel.commerceItem.auxiliaryData.productId"/>
               <dsp:valueof param="CiRel.commerceItem.auxiliaryData.productRef.displayName"/></span></td>
                  <td align=right><span class=small>
                  <dsp:getvalueof id="pval0" param="CiRel.amountByAverage"><dsp:include page="DisplayCurrencyType.jsp" flush="false"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                  </span></td>  
            </tr>
          </dsp:oparam>
          </dsp:droplet><%--ForEach--%>


          <tr><td colspan=4><hr size="1" noshade color="#666666"></td></tr>
          <tr valign=top>
            <td colspan=3 align=right><span class=small><i18n:message key="subtotalTitle"/></span></td>
            <td align=right><span class=small>
            <dsp:droplet name="/atg/projects/b2bstore/order/ShippingGroupSubtotal">
              <dsp:param name="sg" param="sGroup"/>
              <dsp:oparam name="output">
                <dsp:getvalueof id="pval0" param="subtotal"><dsp:include page="DisplayCurrencyType.jsp" flush="false"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                </dsp:oparam>
          </dsp:droplet>
          </span></td>
          </tr>
          <tr valign=top>
            <td colspan=3 align=right><span class=small><i18n:message key="shippingTitle"/></span></td>
            <td align=right><span class=small>
            <dsp:getvalueof id="pval0" param="sGroup.priceInfo.amount"><dsp:include page="DisplayCurrencyType.jsp" flush="false"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
              </span></td>
              </tr>
              <dsp:droplet name="Compare">
                  <dsp:param name="obj1" param="size"/>
                  <dsp:param name="obj2" param="count"/>
                  <dsp:oparam name="equal">

          <tr><td><br></td></tr>

          <tr valign=top>
            <td colspan=3 align=right><span class=small><i18n:message key="salesTaxTitle"/></span></td>
            <td align=right><span class=small>
            <dsp:getvalueof id="pval0" param="order.priceInfo.tax"><dsp:include page="DisplayCurrencyType.jsp" flush="false"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
              </span></td>
              </tr>
              <dsp:droplet name="Compare">
                <dsp:param name="obj1" param="order.priceInfo.amount"/>
                <dsp:param name="obj2" param="order.priceInfo.rawSubtotal"/>
                <dsp:oparam name="equal">
                </dsp:oparam>
                <dsp:oparam name="default">
                  <tr valign=top>
                    <td colspan=3 align=right><span class=small><i18n:message key="discountTitle"/></span></td>
                    <td align=right><span class=small>
                    <dsp:getvalueof id="pval0" param="order.priceInfo.discountAmount"><dsp:include page="DisplayCurrencyType.jsp" flush="false"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                      </span></td>
                    </tr>
                </dsp:oparam>
              </dsp:droplet>

          <tr valign=top>
            <td colspan=3 align=right><span class=small><i18n:message key="totalTitle"/></span></td>
            <td align=right><span class=small>
            <dsp:getvalueof id="pval0" param="order.priceInfo.total"><dsp:include page="DisplayCurrencyType.jsp" flush="false"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
              </span></td>
              </tr>
              </dsp:oparam>
              </dsp:droplet>
              </table>
              </td>
              </tr>
      </dsp:oparam>
    </dsp:droplet><%/*Switch*/%>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/displayMulShipping.jsp#2 $$Change: 651448 $--%>