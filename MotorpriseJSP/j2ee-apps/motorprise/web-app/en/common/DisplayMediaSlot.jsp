<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>


<dsp:droplet name="/atg/targeting/TargetingFirst">
  <dsp:param name="howMany" value="1"/>
  <dsp:param bean="/atg/registry/Slots/MediaSlot" name="targeter"/>
  <dsp:param name="fireContentEvent" value="false"/>
  <dsp:param name="fireContentTypeEvent" value="false"/>
  <dsp:oparam name="output">
    <img border="1" src="<dsp:valueof param="element.url"/>">
  </dsp:oparam>  
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/common/DisplayMediaSlot.jsp#2 $$Change: 651448 $--%>
