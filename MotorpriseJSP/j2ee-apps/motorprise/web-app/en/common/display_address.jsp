<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/commerce/util/PlaceLookup"/>

<DECLAREPARAM NAME="address" CLASS="atg.repository.RepositoryItem" DESCRIPTION="expected to be of type contactInfo.. displays only the address related fields">

<dsp:valueof param="address.address1"/><br>
<dsp:valueof param="address.address2"/><br> <% //todo: only if not null%>
<dsp:valueof param="address.city"/>, <dsp:valueof param="address.state"/> <dsp:valueof param="address.postalCode"/><br>

<%-- <dsp:valueof param="address.country"/>
--%>

<dsp:getvalueof id="places" idtype="atg.commerce.util.PlaceList" bean="/atg/commerce/util/CountryList">
  <dsp:droplet name="PlaceLookup">
    <dsp:param name="placeList" value="<%=places%>"/>
    <dsp:param name="code" param="address.country"/>  
    <dsp:oparam name="output">
      <dsp:valueof param="displayName"/>
    </dsp:oparam>  
  </dsp:droplet>
</dsp:getvalueof>


</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/common/display_address.jsp#2 $$Change: 651448 $--%>
