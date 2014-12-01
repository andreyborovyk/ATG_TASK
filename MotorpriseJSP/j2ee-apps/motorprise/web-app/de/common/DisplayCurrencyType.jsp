<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/CurrencyConversionFormatter"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<DECLAREPARAM NAME="currency" CLASS="java.lang.String" DESCRIPTION="currency value of product">

<dsp:droplet name="CurrencyConversionFormatter">
  <dsp:param name="currency" param="currency"/>
  <dsp:param bean="Profile.priceList.locale" name="locale"/>
  <dsp:param bean="Profile.priceList.locale" name="targetLocale"/>
  <dsp:param name="euroSymbol" value="&euro;"/>
  <dsp:oparam name="output">
    <dsp:valueof valueishtml="<%=true%>" param="formattedCurrency">Kein Preis</dsp:valueof>  
  </dsp:oparam>
</dsp:droplet>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/common/DisplayCurrencyType.jsp#2 $$Change: 651448 $--%>
