<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>

  <!-- This page sets the catalog navigation, breadcrumbs etc..
    This is included from products page.-->

  <tr bgcolor="#DBDBDB">
    <td colspan=2 height=18><span class=small> &nbsp;
    <%-- Suppress breadcrumbs if no_new_crumb is set; this will be the case 
         if we are about to show an error message for a product that doesn't 
	 exist or isn't in the user's catalog --%>
    <dsp:droplet name="IsEmpty">
      <dsp:param name="value" param="no_new_crumb"/>
      <dsp:oparam name="true">
        <dsp:include page="../common/breadcrumbs.jsp"><dsp:param name="displaybreadcrumbs" value="true"/></dsp:include> &nbsp;</span>
      </dsp:oparam>
    </dsp:droplet>
    </td>
  </tr>
  <tr valign=top> 
    <td width=175> 
      <!-- catalog nav -->
      <dsp:include page="../common/CatalogNav.jsp"></dsp:include>
      <!-- incentives slot -->
      <dsp:include page="../common/Incentive.jsp"></dsp:include>
    </td>

    <td width=625>
      <table border=0 cellpadding=0 cellspacing=0 width=100%>
        <!--this row used to ensure proper spacing of table cell-->
        <tr><td colspan=3><dsp:img src="../images/d.gif" vspace="5"/></td></tr>
        <tr>
        <!--left gutter-->
          <td width=9><dsp:img src="../images/d.gif" hspace="4"/></td> 

          <dsp:include page="../common/HeadBody.jsp">
          </dsp:include>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/catalog/ProductHead.jsp#2 $$Change: 651448 $--%>
