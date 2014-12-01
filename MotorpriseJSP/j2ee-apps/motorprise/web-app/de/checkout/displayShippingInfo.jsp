<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>

<dsp:droplet name="ForEach">
  <dsp:param name="array" param="order.shippingGroups"/>
  <dsp:param name="elementName" value="sGroup"/>

  <dsp:oparam name="output">

    <dsp:droplet name="Switch">
      <dsp:param name="value" param="size"/>

      <dsp:oparam name="1">
        <dsp:getvalueof id="pval0" param="sGroup">        
          <dsp:include page="../checkout/displaySingleShipping.jsp">
            <dsp:param name="shippingGroup" value="<%=pval0%>"/>
            <dsp:param name="order" param="order"/>
          </dsp:include>
        </dsp:getvalueof>
     </dsp:oparam>

     <dsp:oparam name="default">
       <dsp:getvalueof id="pval0" param="sGroup">
         <dsp:include page="../checkout/displayMulShipping.jsp">
           <dsp:param name="shippingGroup" value="<%=pval0%>"/>
           <dsp:param name="order" param="order"/>
         </dsp:include>
       </dsp:getvalueof>
     </dsp:oparam>

   </dsp:droplet>

  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/displayShippingInfo.jsp#2 $$Change: 651448 $--%>
