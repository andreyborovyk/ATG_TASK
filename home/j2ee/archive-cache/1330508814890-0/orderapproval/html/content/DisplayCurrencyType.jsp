<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/CurrencyConversionFormatter"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>

<i18n:bundle baseName="atg.commerce.gears.orderapproval.OrderApprovalResources" localeAttribute="userLocale" changeResponseLocale="false" />

<DECLAREPARAM NAME="currency" CLASS="java.lang.String" DESCRIPTION="currency value of product">

<dsp:droplet name="CurrencyConversionFormatter">
  <dsp:param name="currency" param="currency"/>
  <dsp:param bean="Profile.priceList.locale" name="locale"/>
  <dsp:param bean="Profile.priceList.locale" name="targetLocale"/>
  <dsp:param name="euroSymbol" value="&euro;"/>
  <dsp:oparam name="output">
    <dsp:valueof valueishtml="<%=true%>" param="formattedCurrency"><i18n:message key="no_price">no price</i18n:message></dsp:valueof>  
  </dsp:oparam>
</dsp:droplet>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/DisplayCurrencyType.jsp#2 $$Change: 651448 $--%>
