<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%         
/* -------------------------------------------------------
 * Display an address 
 * ------------------------------------------------------- */
%>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<DECLAREPARAM NAME="address" 
              CLASS="java.lang.Object" 
              DESCRIPTION="A ContactInfo Repository Item to display">

Falsche Adressanzeige. Verwenden Sie ../common/DisplayAddress<br>

<dsp:valueof param="address.address1"/><br>
<dsp:droplet name="IsEmpty">
  <dsp:param name="value" param="address.address2"/>
  <dsp:oparam name="false">
    <dsp:valueof param="address.address2"/><br>
  </dsp:oparam>
</dsp:droplet>    
<dsp:valueof param="address.city"/>,
<dsp:valueof param="address.state"/>
<dsp:valueof param="address.postalCode"/>
<br>
<dsp:valueof param="address.country"/>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/display_address.jsp#2 $$Change: 651448 $--%>
