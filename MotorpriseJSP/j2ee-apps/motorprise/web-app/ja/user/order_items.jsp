<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/pricing/PriceEachItem"/>

<decleareparam name="order"
               class="atg.commerce.order.Order"
               description="the current order">

<table border=0 cellpadding=4 cellspacing=1>
  <tr bgcolor="#666666" valign=bottom>
    <td colspan=2><span class=smallbw>部品番号</span></td>
    <td colspan=2><span class=smallbw>名前</span></td>
    <td colspan=2 align=center><span class=smallbw>価格</span></td>
    <td colspan=2 align=center><span class=smallbw>数量</span></td>
    <td colspan=2 align=center><span class=smallbw>合計</span></td>
    <td colspan=2><span class=smallbw>コストセンタ</span></b></td>
  </tr>


<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.commerceItems"/>
  <dsp:oparam name="output">
      <tr valign=top>
        <td><dsp:valueof param="element.auxiliaryData.catalogRef.manufacturer_part_number"/></td>
        <td>&nbsp;</td>
        <td><dsp:a href="../catalog/product.jsp"><dsp:param name="id" param="element.auxiliaryData.productId"/>
           <dsp:valueof param="element.auxiliaryData.productRef.displayName"/></dsp:a></td>
        <td>&nbsp;</td>
        <td align=right><dsp:valueof converter="currency" param="element.auxiliaryData.catalogRef.listPrice"/></td>
        <td>&nbsp;</td>
        <td align=center><dsp:valueof param="element.quantity"/></td>
        <td>&nbsp;</td>

        <%/* display total list price and any discounted total price */%>
        <dsp:getvalueof id="pval0" param="element"><dsp:include page="../common/display_amount.jsp"><dsp:param name="item" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>

        <td>&nbsp;</td>
        <td><dsp:valueof param="element.costCenter">N/A</dsp:valueof></td>
        <td>&nbsp;</td>
      </tr>
  </dsp:oparam>
</dsp:droplet><%/*ForEach*/%>
  <tr>
    <td colspan=12><hr color=#666666 size=0></td>
  </tr>
  <tr>
    <td colspan=6>&nbsp; </td>
    <td align=right>小計</td>
    <td> </td>
    <td align=right><dsp:valueof converter="currency" param="order.priceInfo.rawSubTotal"/></td>
  </tr>
  <tr>
    <td colspan=6>&nbsp; </td>
    <td align=right>配達</td>
    <td> </td>
    <td align=right><dsp:valueof converter="currency" param="order.priceInfo.shipping"/></td>
  </tr>
  <tr>
    <td colspan=6>&nbsp; </td>
    <td align=right>売上税</td>
    <td> </td>
    <td align=right><dsp:valueof converter="currency" param="order.priceInfo.tax"/></td>
  </tr>
  <tr>
    <td colspan=6>&nbsp; </td>
    <td align=right>合計</td>
    <td> </td>
    <td align=right><b><dsp:valueof converter="currency" param="order.priceInfo.total"/></b></td>
  </tr>
</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/user/order_items.jsp#2 $$Change: 651448 $--%>
