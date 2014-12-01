<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<DECLAREPARAM NAME="product" 
              CLASS="java.lang.Object" 
              DESCRIPTION="A Product repository Item - REQUIRED.">                                  

<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>

<dsp:form action="product.jsp" method="post">
  <input name="id" type="hidden" value="<dsp:valueof param="product.repositoryId"/>">  
  <dsp:input bean="CartModifierFormHandler.addItemToOrderSuccessURL" type="hidden" value="../checkout/cart.jsp?noCrumbs=false"/>
  <dsp:input bean="CartModifierFormHandler.SessionExpirationURL" type="hidden" value="../common/session_expired.jsp"/>
  <dsp:input bean="CartModifierFormHandler.productId" paramvalue="product.repositoryId" type="hidden"/>
  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
    <dsp:param name="array" param="product.childSKUs"/>
    <dsp:oparam name="output">
	 <table border=0 cellpadding=3 width=100%>
	  <tr>
	  <td>
      <dsp:input bean="CartModifierFormHandler.catalogRefIds" paramvalue="element.repositoryId" type="hidden"/> 
      <span class=smallb>Qty</span>&nbsp;
      <dsp:input bean="CartModifierFormHandler.quantity" size="4" type="text" value="1"/>&nbsp;&nbsp;
      <dsp:input bean="CartModifierFormHandler.addItemToOrder" type="submit" value="Add to order"/>
      <br>
	  </td>
	  </tr>
	</table>
      
    </dsp:oparam>
  </dsp:droplet>
</dsp:form>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/catalog/AddToCart.jsp#2 $$Change: 651448 $--%>
