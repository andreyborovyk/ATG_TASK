<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<%         
/* -------------------------------------------------------
 * Display an address
 * ------------------------------------------------------- */
%> 
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/util/PlaceLookup"/>


<DECLAREPARAM NAME="address" 
              CLASS="java.lang.Object" 
              DESCRIPTION="A ContactInfo Repository Item to display">


<dsp:droplet name="Switch">
  <dsp:param name="value" param="address.country"/>
  <dsp:oparam name="jp">
    <table cellspacing=0 cellpadding=0 broder=0>
      <tr>
        <td>
          <nobr><dsp:valueof param="address.companyName"/></nobr><br>
          <nobr>
          <dsp:getvalueof id="places" idtype="atg.commerce.util.PlaceList" bean="/atg/commerce/util/CountryList_ja">
            <dsp:droplet name="PlaceLookup">
              <dsp:param name="placeList" value="<%=places%>"/>
              <dsp:param name="code" param="address.country"/>
              <dsp:oparam name="output">
                <dsp:valueof param="displayName"/>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:getvalueof>
          </nobr><br>
          <nobr>Åß<dsp:valueof param="address.postalCode"/></nobr><br>
          <nobr><dsp:valueof param="address.state"/><dsp:valueof param="address.city"/><dsp:valueof param="address.address1"/><dsp:valueof param="address.address2"/></nobr>
        </td>
      </tr>
    </table>
  </dsp:oparam>
  <dsp:oparam name="de">
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
          <dsp:valueof param="address.postalCode"/> <dsp:valueof param="address.city"/>
          <br>
          <dsp:getvalueof id="places" idtype="atg.commerce.util.PlaceList" bean="/atg/commerce/util/CountryList_ja">
            <dsp:droplet name="PlaceLookup">
              <dsp:param name="placeList" value="<%=places%>"/>
              <dsp:param name="code" param="address.country"/>  
              <dsp:oparam name="output">
                <dsp:valueof param="displayName"/>
              </dsp:oparam>
            </dsp:droplet>
          </dsp:getvalueof>
        </td>
      </tr>
    </table>
  </dsp:oparam>
  <dsp:oparam name="default">
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
          <dsp:valueof param="address.city"/>,
          <dsp:valueof param="address.state"/>
          <dsp:valueof param="address.postalCode"/>
          <br>
          <dsp:getvalueof id="places" idtype="atg.commerce.util.PlaceList" bean="/atg/commerce/util/CountryList_ja">
            <dsp:droplet name="PlaceLookup">
              <dsp:param name="placeList" value="<%=places%>"/>
              <dsp:param name="code" param="address.country"/>  
              <dsp:oparam name="output">
                <dsp:valueof param="displayName"/>
              </dsp:oparam>  
            </dsp:droplet>
          </dsp:getvalueof>          

        </td>
      </tr>
    </table>
  </dsp:oparam>
</dsp:droplet>
<%/* @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/common/DisplayAddress.jsp#2 $$Change: 651448 $*/%> </dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/common/DisplayAddress.jsp#2 $$Change: 651448 $--%>
