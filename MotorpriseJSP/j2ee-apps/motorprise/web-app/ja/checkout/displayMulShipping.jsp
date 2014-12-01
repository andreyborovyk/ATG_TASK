<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>

<declareparam name="sGroup"
class="atg.commerce.order.ShippingGroup"
description="the Shipping Group">

<tr valign=top>
  <td colspan=2>
  <table width=100% cellpadding=3 cellspacing=0 border=0>
    <tr>
      <td class=box-top>&nbsp;配達グループ <dsp:valueof param="count"/></td>
    </tr>
  </table>
  </td>
</tr>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="sGroup.shippingGroupClassType"/>
  <dsp:oparam name="electronicShippingGroup">
    送付先 <dsp:valueof param="sGroup.emailAddress"/>
  </dsp:oparam>
  <dsp:oparam name="default">

  <tr valign=top>
          <td align=right><span class=smallb>配達先住所</span></td>
          <td width=75%>
            <dsp:getvalueof id="pval0" param="sGroup.shippingAddress"><dsp:include page="../common/DisplayAddress.jsp"><dsp:param name="address" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
        </td>
        </tr>
        <tr valign=top>
          <td align=right><span class=smallb>配達方法</span></td>
          <td width=75%><dsp:valueof param="sGroup.shippingMethod">配達方法の指定なし</dsp:valueof></td>
        </tr>
        <tr valign=top>
              <td align=right><span class=smallb>アイテム</span></td>
              <td width="75%">
              <table border=0 cellpadding=0 cellspacing=5 width=100%>
                <tr valign=bottom>
                  <td><span class=smallb>数量</span></td>
                  <td><span class=smallb>部品番号</span></td>
                  <td><span class=smallb>名前</span></td>
                  <td align=right><span class=smallb>価格</span></td>
                </tr>

          <tr>
            <td bgcolor="#666666" colspan=4><dsp:img src="../images/d.gif"/></td>
          </tr>
          <dsp:droplet name="ForEach">
            <dsp:param name="array" param="sGroup.CommerceItemRelationships"/>
            <dsp:param name="elementName" value="CiRel"/>
            <dsp:oparam name="output">
              <tr valign=top>
                <td align=center><dsp:valueof param="CiRel.quantity"/></td>
              <td><dsp:valueof param="CiRel.commerceItem.auxiliaryData.catalogRef.manufacturer_part_number"/></td>
                  <td><dsp:a href="../catalog/product.jsp"><dsp:param name="id" param="CiRel.commerceItem.auxiliaryData.productId"/>
               <dsp:valueof param="CiRel.commerceItem.auxiliaryData.productRef.displayName"/></dsp:a></td>
                  <td align=right>
                  <dsp:getvalueof id="pval0" param="CiRel.amountByAverage"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                  </td>
              
              <%-- display total list price and any discounted total price
              <dsp:getvalueof id="pval0" param="CiRel.commerceItem"><dsp:include page="../common/display_amount.jsp" flush="true"><dsp:param name="item" value="<%=pval0%>"/></dsp:include></dsp:getvalueof> --%>
                            </tr>
          </dsp:oparam>
          </dsp:droplet><%--ForEach--%>


          <tr>
            <td bgcolor="#666666" colspan=4><dsp:img src="../images/d.gif"/></td>
          </tr>
          <tr valign=top>
            <td colspan=3 align=right>小計</td>
            <td align=right>
            <dsp:droplet name="/atg/projects/b2bstore/order/ShippingGroupSubtotal">
              <dsp:param name="sg" param="sGroup"/>
              <dsp:oparam name="output">
                <dsp:getvalueof id="pval0" param="subtotal"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                </dsp:oparam>
          </dsp:droplet>
          </td>
          </tr>
          <tr valign=top>
            <td colspan=3 align=right>送料</td>
            <td align=right>
            <dsp:getvalueof id="pval0" param="sGroup.priceInfo.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
              </td>
              </tr>
              <dsp:droplet name="Compare">
                  <dsp:param name="obj1" param="size"/>
                  <dsp:param name="obj2" param="count"/>
                  <dsp:oparam name="equal">

          <tr><td><br></td></tr>

          <tr valign=top>
            <td colspan=3 align=right>消費税</td>
            <td align=right>
            <dsp:getvalueof id="pval0" param="order.priceInfo.tax"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
              </td>
              </tr>
              <dsp:droplet name="Compare">
                <dsp:param name="obj1" param="order.priceInfo.amount"/>
                <dsp:param name="obj2" param="order.priceInfo.rawSubtotal"/>
                <dsp:oparam name="equal">
                </dsp:oparam>
                <dsp:oparam name="default">
                  <tr valign=top>
                    <td colspan=3 align=right>割引</td>
                    <td align=right>
                    <dsp:getvalueof id="pval0" param="order.priceInfo.discountAmount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                      </td>
                    </tr>
                </dsp:oparam>
              </dsp:droplet>

          <tr valign=top>
            <td colspan=3 align=right><span class=smallb>合計</span></td>
            <td align=right>
            <dsp:getvalueof id="pval0" param="order.priceInfo.total"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
              </td>
              </tr>
              </dsp:oparam>
              </dsp:droplet>
              </table>
              </td>
              </tr>
      </dsp:oparam>
    </dsp:droplet><%/*Switch*/%>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/checkout/displayMulShipping.jsp#2 $$Change: 651448 $--%>
