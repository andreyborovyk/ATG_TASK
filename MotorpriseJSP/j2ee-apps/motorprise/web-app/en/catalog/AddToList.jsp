<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/projects/b2bstore/purchaselists/PurchaselistFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>

<dsp:droplet name="ErrorMessageForEach">
   <dsp:param bean="PurchaselistFormHandler.formExceptions" name="exceptions"/>
      <dsp:oparam name="outputStart">
         <font color="red">Errors:</font>
      </dsp:oparam>
      <dsp:oparam name="output">
         <font color="blue">
         <dsp:valueof param="propertyName"/> :: 
            <dsp:valueof param="message"/>
         </font>
      </dsp:oparam>
</dsp:droplet>
<%--   <dsp:input bean="PurchaselistFormHandler.addItemToPurchaselistErrorURL" type="hidden" value="product.jsp"/>
--%>

<dsp:form action="product.jsp" method="post">
  <input name="id" type="hidden" value='<dsp:valueof param="product.repositoryId"/>'>
  <dsp:input bean="PurchaselistFormHandler.productId" paramvalue="product.repositoryId" type="hidden"/>
  <dsp:droplet name="/atg/dynamo/droplet/ForEach">
    <dsp:param name="array" param="product.childSKUs"/>
    <dsp:oparam name="output">
      <table border=0 cellpadding=3 width=100%>
        <tr>
         <td><dsp:input bean="PurchaselistFormHandler.catalogRefIds" paramvalue="element.repositoryId" type="hidden"/> 
         <span class=smallb>Qty</span>&nbsp;
         <dsp:input bean="PurchaselistFormHandler.quantity" size="2" type="text" value="1"/>&nbsp;
    </dsp:oparam>
  </dsp:droplet>        
  
  <dsp:select bean="PurchaselistFormHandler.purchaseListId">    
    <dsp:droplet name="ForEach">
      <dsp:param bean="Profile.purchaselists" name="array"/>      
      <dsp:oparam name="output">
        <dsp:getvalueof id="elem" idtype="atg.repository.RepositoryItem" param="element">
          <dsp:option value="<%=elem.getRepositoryId()%>"/>
          <dsp:valueof param="element.eventName">Unnamed Purchase List</dsp:valueof> 
        </dsp:getvalueof>  
      </dsp:oparam>
    </dsp:droplet>    
  </dsp:select></td>
  </tr>
  <tr>
    <td><dsp:input bean="PurchaselistFormHandler.addItemToPurchaselist" type="submit" value="Add to list"/></td>
  </tr>
 
  <tr>
    <td>
    <table border=0 cellpadding=3 width=100%>
      <tr>
        <td><span class=smallb><dsp:a href="../user/purchase_lists.jsp?noCrumbs=false"><dsp:param name="product" param="product.repositoryId"/><dsp:param name="noCrumbs" value="false"/>Create new purchase list</dsp:a></span></td>
      </tr>
    </table>
    </td>
  </tr>
 
  </table>
</dsp:form>        

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/catalog/AddToList.jsp#2 $$Change: 651448 $--%>
