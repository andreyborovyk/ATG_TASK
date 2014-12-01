<%@ taglib uri="/dsp" prefix="dsp" %>
<%@ taglib uri="/jakarta-i18n-1.0" prefix="i18n" %>
<dsp:page>

<i18n:bundle baseName="atg.commerce.gears.orderapproval.OrderApprovalResources" localeAttribute="userLocale" changeResponseLocale="false" />

<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/Compare"/>

<DECLAREPARAM NAME="item" CLASS="atg.commerce.order.CommerceItem"
    DESCRIPTION="item to display the total amount for">

<dsp:droplet name="IsNull">
 <dsp:param name="value" param="item.priceInfo"/>
 <dsp:oparam name="false">
  <dsp:droplet name="Compare">
    <dsp:param name="obj1" param="item.priceInfo.amount"/>
    <dsp:param name="obj2" param="item.priceInfo.rawTotalPrice"/>
      <dsp:oparam name="equal">
        <td align=right>
         <dsp:valueof converter="currency" param="item.priceInfo.amount"><i18n:message key="no_price"/></dsp:valueof>
        </td>
      </dsp:oparam>
      <dsp:oparam name="default">
        <td align=right>
          <dsp:valueof converter="currency" param="item.priceInfo.amount"><i18n:message key="no_price"/></dsp:valueof>
        </td>
      </dsp:oparam>
      </dsp:droplet>
    </dsp:oparam>
  </dsp:droplet>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/gears/OrderApproval/orderapproval.war/html/content/display_amount.jsp#2 $$Change: 651448 $--%>
