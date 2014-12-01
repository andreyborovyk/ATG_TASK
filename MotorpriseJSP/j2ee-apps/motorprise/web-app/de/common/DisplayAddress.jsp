<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%         
/* -------------------------------------------------------
 * Display an address
 * ------------------------------------------------------- */
%>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/util/PlaceLookup"/>


<DECLAREPARAM NAME="address" 
              CLASS="java.lang.Object" 
              DESCRIPTION="A ContactInfo Repository Item to display">


<table cellspacing=0 cellpadding=0 broder=0>
  <tr>
    <td>
      <nobr><dsp:valueof param="address.companyName"/></nobr><br>
      <nobr><dsp:valueof param="address.address1"/></nobr><br>
      <dsp:droplet name="IsEmpty">
        <dsp:param name="value" param="address.address2"/>
        <dsp:oparam name="false">
          <nobr><dsp:valueof param="address.address2"/></nobr><br>
        </dsp:oparam>
      </dsp:droplet>    
      <dsp:droplet name="Switch">
        <dsp:param name="value" param="address.country"/>
        <dsp:oparam name="de">
          <dsp:valueof param="address.postalCode"/> <dsp:valueof param="address.city"/>
        </dsp:oparam>
        <dsp:oparam name="default">
          <dsp:valueof param="address.city"/>,
          <dsp:valueof param="address.state"/>
          <dsp:valueof param="address.postalCode"/>
        </dsp:oparam>
      </dsp:droplet>
      <br>
      <dsp:droplet name="PlaceLookup">
        <dsp:param name="placeList" bean="/atg/commerce/util/CountryList_de"/>
        <dsp:param name="code" param="address.country"/>  
        <dsp:oparam name="output">
          <dsp:valueof valueishtml="<%=true%>" param="displayName"/>
        </dsp:oparam>  
      </dsp:droplet>
    </td>
  </tr>
</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/common/DisplayAddress.jsp#2 $$Change: 651448 $--%>
