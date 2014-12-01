<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%-- This page fragment displays a short version of the product comparison list --%>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductList"/>

<dsp:droplet name="/atg/dynamo/droplet/ForEach">
  <dsp:param bean="/atg/commerce/catalog/comparison/ProductList.items" name="array"/>
  <%-- <dsp:param name="sortProperties" value=""/> --%>

  
  <dsp:oparam name="outputStart">
    <b>Zu vergleichende Produkte</b>
    <blockquote>
    <table border=0 cellpadding=2 cellspacing=1 width=100%>
  </dsp:oparam>

  <dsp:oparam name="output">
    <tr>
      <td><dsp:valueof valueishtml="<%=true%>" param="element.productLink"/> - <dsp:valueof param="element.product.description"/></td>
    </tr>
  </dsp:oparam>

  <dsp:oparam name="outputEnd">
    </table>
    <table border=0 cellpadding=1 cellspacing=1 vspace=10 align=left>
      <tr><td><br><span class=smallb><dsp:a href="compare_delete.jsp">Vergleichsliste bearbeiten</dsp:a></span></td></tr>
	  <tr>
        <td><br><dsp:form action="compare_result.jsp"><input type="submit" value="Vergleichen">&nbsp;</dsp:form></td>
        <!--<td><dsp:form action="compare_delete.jsp"><input type="submit" value="Remove items"></dsp:form></td>-->
      </tr>
    </table>
    </blockquote>
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/catalog/compareList.jsp#2 $$Change: 651448 $--%>
