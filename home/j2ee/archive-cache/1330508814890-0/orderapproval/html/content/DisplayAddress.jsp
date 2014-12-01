<%@ taglib uri="/dsp" prefix="dsp" %>
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
        <td><span class=small>
          <nobr><dsp:valueof param="address.companyName"/></nobr><br>
          <nobr><dsp:valueof param="address.postalCode"/></nobr><br>
          <nobr><dsp:valueof param="address.state"/><dsp:valueof param="address.city"/><dsp:valueof param="address.address1"/></nobr><br>
          <dsp:droplet name="IsEmpty">
            <dsp:param name="value" param="address.address2"/>
            <dsp:oparam name="false">
              <nobr><dsp:valueof param="address.address2"/></nobr><br>
            </dsp:oparam>
          </dsp:droplet>
          <%-- <dsp:valueof param="address.country"/> --%>
        </span></td>
      </tr>
    </table>
  </dsp:oparam>
  <dsp:param name="value" param="address.country"/>
  <dsp:oparam name="de">
    <table cellspacing=0 cellpadding=0 broder=0>
      <tr>
        <td><span class=small>
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
          <dsp:valueof param="address.country"/>
        </span></td>
      </tr>
    </table>
  </dsp:oparam>
  <dsp:oparam name="default">
    <table cellspacing=0 cellpadding=0 broder=0>
      <tr>
        <td><span class=small>
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
          <dsp:valueof param="address.country"/>

        </span></td>
      </tr>
    </table>
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/DisplayAddress.jsp#2 $$Change: 651448 $--%>
