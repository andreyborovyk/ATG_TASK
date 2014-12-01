<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Redirect"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<%--
Determine if the user has any cost centers associated with them.  If 
they do then send them to the cost centers page, else send them to the
confirmaton page.
--%>

<dsp:getvalueof id="linkname" idtype="java.lang.String" param="link">

<dsp:droplet name="IsEmpty">
  <dsp:param bean="Profile.costCenters" name="value"/>
  <dsp:oparam name="true">
    <dsp:droplet name="Redirect">
      <dsp:param name="url" value="confirmation.jsp"/>
    </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:droplet name="Redirect">        
      <dsp:param name="url" value='<%="cost_centers.jsp?link="+linkname%>'/>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>
</dsp:getvalueof>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/checkout/IsEmptyCostCenters.jsp#2 $$Change: 651448 $--%>
