<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<DECLAREPARAM NAME="address" CLASS="atg.repository.RepositoryItem" DESCRIPTION="expected to be of type contactInfo.. displays only the address related fields">

<dsp:valueof param="address.address1"/><br>
<dsp:valueof param="address.address2"/><br> <% //todo: only if not null%>
<dsp:valueof param="address.city"/>, <dsp:valueof param="address.state"/> <dsp:valueof param="address.postalCode"/><br>
<dsp:valueof param="address.country"/>
</dsp:page>


<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/common/display_address.jsp#2 $$Change: 651448 $--%>
