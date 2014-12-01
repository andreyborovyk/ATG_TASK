<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Checkout"/></dsp:include>

<%/*
  This page displays a screen to split item, shipping & tax across different
  cost centers. It takes parameter init to determine whether this page
  is being called first time. We initialize the cost center droplet to generate
  new cost centers if it is first time, other wise we use exisitng cost center groups
  of the order related to different items.
*/%>

<dsp:droplet name="Switch">
  <dsp:param name="value" param="init"/>
  <dsp:oparam name="unset">
    <dsp:include page="costCentersLineItemDetails.jsp"><dsp:param name="init" value="true"/></dsp:include>
  </dsp:oparam>
  <dsp:oparam name="default">
    <dsp:getvalueof id="pval0" param="init"><dsp:include page="costCentersLineItemDetails.jsp"><dsp:param name="init" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
  </dsp:oparam>
</dsp:droplet>

</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/cost_centers_line_item.jsp#2 $$Change: 651448 $--%>
