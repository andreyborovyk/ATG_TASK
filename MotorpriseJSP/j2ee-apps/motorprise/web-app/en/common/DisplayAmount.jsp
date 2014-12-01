<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<DECLAREPARAM NAME="item" CLASS="atg.commerce.order.CommerceItem"
    DESCRIPTION="item to display the total amount for">

<dsp:droplet name="IsNull">
 <dsp:param name="value" param="item.priceInfo"/>
 <dsp:oparam name="false">
  <dsp:droplet name="Compare">
    <dsp:param name="obj1" param="item.priceInfo.amount"/>
    <dsp:param name="obj2" param="item.priceInfo.rawTotalPrice"/>
      <dsp:oparam name="equal">
        <td align=right>
        <dsp:getvalueof id="pval0" param="item.priceInfo.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
        </td>
      </dsp:oparam>
      <dsp:oparam name="default">
        <td align=right>
          <dsp:getvalueof id="pval0" param="item.priceInfo.amount"><dsp:include page="../common/DisplayCurrencyType.jsp"><dsp:param name="currency" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
        </td>
      </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/common/DisplayAmount.jsp#2 $$Change: 651448 $--%>
