<%@ taglib uri="dsp" prefix="dsp" %> 
<dsp:page> 

<dsp:importbean bean="/atg/userprofiling/Profile"/> 

<dsp:droplet name="/atg/targeting/TargetingFirst"> 
  <dsp:param bean="/atg/registry/Slots/MessageSlot_de" name="targeter"/> 
  <dsp:param name="howMany" value="1"/> 
  <dsp:oparam name="output"> 
    <span class=small> 
    <dsp:valueof valueishtml="<%=true%>" param="element.data"/> 
    </span> 
  </dsp:oparam> 
  <dsp:oparam name="empty"> 
    leer 
  </dsp:oparam> 
</dsp:droplet> 
</dsp:page> 
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/common/DisplayMessage.jsp#2 $$Change: 651448 $--%>
