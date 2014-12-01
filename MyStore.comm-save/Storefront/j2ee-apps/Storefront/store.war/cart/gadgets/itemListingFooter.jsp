<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  
  <dsp:getvalueof var="currencyCode" vartype="java.lang.String" bean="ShoppingCart.current.priceInfo.currencyCode"/>
  <dsp:getvalueof var="subtotal" vartype="java.lang.Double" bean="ShoppingCart.current.priceInfo.amount"/>
  <dsp:getvalueof var="strshipping" vartype="java.lang.Double" bean="ShoppingCart.current.priceInfo.shipping" />
  <dsp:getvalueof var="strtax" vartype="java.lang.Double" bean="ShoppingCart.current.priceInfo.tax" />
  
  <%--
    This page ends the tbody which was opened in itemListingHeader page
  --%>
  </tbody>
  </table>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/cart/gadgets/itemListingFooter.jsp#3 $$Change: 635816 $--%>
