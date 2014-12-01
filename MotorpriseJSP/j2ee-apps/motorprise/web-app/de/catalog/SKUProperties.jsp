<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<DECLAREPARAM NAME="product" CLASS="java.lang.String"
                     DESCRIPTION="The parent product">

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<dsp:droplet name="ForEach">
  <dsp:param name="array" param="product.childSKUs"/>
  <dsp:oparam name="output">
    <table cellspacing="0" cellpadding="0" border="0">
      <tr>
        <td>&nbsp;</td>
        <td><span class=smallb>Produktnummer:</span></td>
        <td>&nbsp;</td>
        <td><span class=small><dsp:valueof param="element.manufacturer_part_number">keine Kennung</dsp:valueof></span>
        <td>&nbsp;</td>
      </tr>
      <tr>
        <td>&nbsp;</td>
        <td><span class=smallb>Hersteller:</span></td>
        <td>&nbsp;</td>
        <td><span class=small><dsp:valueof param="product.manufacturer.displayName">Unbekannt</dsp:valueof>
  <td>&nbsp;</td>
      </tr>

      <dsp:droplet name="/atg/dynamo/droplet/Switch">
        <dsp:param bean="Profile.transient" name="value"/>
        <dsp:oparam name="false">
          <tr>
            <td>&nbsp;</td>
            <td><span class=smallb>Verfügbarkeit:</span></td>
            <td>&nbsp;</td>
            <dsp:droplet name="/atg/commerce/inventory/InventoryLookup">
              <dsp:param name="itemId" param="element.repositoryId"/>
              <dsp:oparam name="output">
                <td><span class=small><dsp:valueof param="inventoryInfo.availabilityStatusMsg">Unbekannt</dsp:valueof></span>
              </dsp:oparam>
            </dsp:droplet>
            <td>&nbsp;</td>
          </tr>
      
          <dsp:droplet name="/atg/dynamo/droplet/Switch">
            <dsp:param bean="Profile.parentOrganization.customerType" name="value"/>
             <dsp:oparam name="Enterprise">
               <tr>
                 <td>&nbsp;</td>
                 <td><span class=smallb>Lagerstand:</span></td>
                 <td>&nbsp;</td>
                 <dsp:droplet name="/atg/commerce/inventory/InventoryLookup">
                   <dsp:param name="itemId" param="element.repositoryId"/>
                   <dsp:oparam name="output">
                     <td><span class=small><dsp:valueof param="inventoryInfo.stockLevel">Unbekannt</dsp:valueof></span>
                   </dsp:oparam>
                 </dsp:droplet>
                 <td>&nbsp;</td>
               </tr>
             </dsp:oparam>
           </dsp:droplet>
           <dsp:getvalueof id="pval0" param="product"><dsp:getvalueof id="pval1" param="element"><dsp:include page="DisplayPrice.jsp"><dsp:param name="Product" value="<%=pval0%>"/><dsp:param name="Sku" value="<%=pval1%>"/></dsp:include></dsp:getvalueof></dsp:getvalueof>  
        </dsp:oparam>
      </dsp:droplet>
    </table>
  </dsp:oparam>
</dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/catalog/SKUProperties.jsp#2 $$Change: 651448 $--%>
