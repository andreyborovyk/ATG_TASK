<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/pricing/priceLists/PriceDroplet"/>
<dsp:importbean bean="/atg/dynamo/droplet/CurrencyFormatter"/>
<dsp:importbean bean="/atg/dynamo/droplet/CurrencyConversionFormatter"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>


<dsp:droplet name="PriceDroplet">
  <dsp:param name="product" param="Product"/>
  <dsp:param name="sku" param="Sku"/>
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">
      <dsp:param name="value" param="price.pricingScheme"/>
      <dsp:oparam name="listPrice">
        <tr>
          <td>&nbsp;</td>
          <td><span class=smallb>Preis:</span></td>
          <td>&nbsp; </td>
          <td>
            <%/*Display price for locale of user: */%>
            <dsp:droplet name="CurrencyConversionFormatter">
                 <dsp:param name="currency" param="price.listPrice"/>
                 <dsp:param bean="Profile.priceList.locale" name="locale"/>
                 <dsp:param bean="Profile.priceList.locale" name="targetLocale"/>
                 <dsp:param name="euroSymbol" value="&euro;"/>
                 <dsp:oparam name="output">
                   <span class=small><dsp:valueof valueishtml="<%=true%>" param="formattedCurrency">Kein Preis</dsp:valueof></span>
                 </dsp:oparam>
               </dsp:droplet>
            
                     <%/*Display price in DM for German users: */%>
                     <dsp:droplet name="CurrencyConversionFormatter">
                       <dsp:param name="currency" param="price.listPrice"/>
                       <dsp:param bean="Profile.priceList.locale" name="locale"/>
                       <dsp:param name="targetLocale" value="de_DE"/>
                       <dsp:oparam name="output">
                         <span class=small>(<dsp:valueof param="formattedCurrency">Kein Preis</dsp:valueof>)</span>    
                       </dsp:oparam>
                     </dsp:droplet>
          </td>  
          <td>&nbsp;</td>
        </tr>
      </dsp:oparam>

      <dsp:oparam name="bulkPrice">
        <dsp:getvalueof id="pval0" param="price.complexPrice"><dsp:getvalueof id="pval1" param="price.pricingScheme"><dsp:include page="DisplayComplexPrice.jsp"><dsp:param name="complexPrice" value="<%=pval0%>"/><dsp:param name="pricingScheme" value="<%=pval1%>"/></dsp:include></dsp:getvalueof></dsp:getvalueof>
      </dsp:oparam>

      <dsp:oparam name="tieredPrice">
        <dsp:getvalueof id="pval0" param="price.complexPrice"><dsp:getvalueof id="pval1" param="price.pricingScheme"><dsp:include page="DisplayComplexPrice.jsp"><dsp:param name="complexPrice" value="<%=pval0%>"/><dsp:param name="pricingScheme" value="<%=pval1%>"/></dsp:include></dsp:getvalueof></dsp:getvalueof>
      </dsp:oparam>
    </dsp:droplet>

  </dsp:oparam>
  <dsp:oparam name="error">
    Es liegt ein Fehler bei der Preisgestaltung vor.
  </dsp:oparam>

</dsp:droplet>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/catalog/DisplayPrice.jsp#2 $$Change: 651448 $--%>
