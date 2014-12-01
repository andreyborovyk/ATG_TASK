<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Checkout"/></dsp:include>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="init"/>
  <dsp:oparam name="unset">
    <dsp:include page="SplitPaymentDetails.jsp"><dsp:param name="init" value="true"/></dsp:include>
  </dsp:oparam>
  <dsp:oparam name="default">
    <dsp:getvalueof id="pval0" param="init"><dsp:include page="SplitPaymentDetails.jsp"><dsp:param name="init" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
  </dsp:oparam>
</dsp:droplet>


</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/checkout/split_payment.jsp#2 $$Change: 651448 $--%>
