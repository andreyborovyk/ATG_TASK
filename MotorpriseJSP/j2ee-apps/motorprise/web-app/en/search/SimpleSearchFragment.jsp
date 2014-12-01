<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%/* This page fragment is intended to be embedded on any page that
         requires a search capability.  */%>


<dsp:importbean bean="/atg/commerce/catalog/CatalogSearch"/>

<dsp:form action="simple_search.jsp" method="POST">
<table cellspacing="0" cellpadding="0" border="0" bgcolor="#999999" width=100%>

  <tr><td colspan=3><dsp:img src="../images/d.gif" vspace="0"/></td></td></tr>
  <tr>
    <td><dsp:img src="../images/d.gif" hspace="3"/></td>
    <td width=100>
    <!-- form elements -->
    <input name="repositoryKey" type="hidden" value="<dsp:valueof bean='/OriginatingRequest.requestLocale.locale'/>">
    <dsp:input bean="CatalogSearch.searchInput" size="15" type="text" value=""/>
    </td>
	<!--  use this hidden form tag to make sure the search handler is invoked if
       someone does not hit the submit button -->
	<td align=center>
    <dsp:input bean="CatalogSearch.search" type="hidden" value="Search"/>
    <dsp:input type="image" src="../images/motorprise-search.gif" bean="CatalogSearch.search" name="search" value="Search" border="0"/> </td> 
    <!-- end form elements -->

  </tr>
  <tr>
    <td><dsp:img src="../images/d.gif" hspace="3"/></td>
    <td colspan=2><dsp:a href="advanced_search.jsp"><font color="#FFFFFF"><span class=smallw>Advanced search</span></font></dsp:a></td>
  </tr>
  <tr><td colspan=3><dsp:img src="../images/d.gif" vspace="0"/></td></tr>

 
</dsp:form> 
</table>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/search/SimpleSearchFragment.jsp#2 $$Change: 651448 $--%>
