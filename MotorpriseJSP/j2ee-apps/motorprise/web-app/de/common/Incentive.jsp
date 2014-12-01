<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>
<!-- incentives slot -->
<dsp:droplet name="/atg/targeting/TargetingFirst">
  <dsp:param bean="/atg/registry/Slots/Incentives" name="targeter"/>
  <dsp:param name="howMany" value="1"/>
  <dsp:oparam name="output">
    <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.url">
      <dsp:include page="<%=urlStr%>"></dsp:include>
    </dsp:getvalueof>
  </dsp:oparam>  
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/common/Incentive.jsp#2 $$Change: 651448 $--%>
