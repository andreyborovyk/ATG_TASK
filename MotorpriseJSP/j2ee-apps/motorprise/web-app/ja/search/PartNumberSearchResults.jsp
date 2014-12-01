<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>


<% /*
---------------------------------------------------------------
This JSP droplet displays the contents of search
that potentially returns both category and product repository items.
The one paramater, ResultArray, accepts a HashMap that contains
elements with the keys "category" and "product".  The values of these
keys are collections of category or product repository items found in
the search.
---------------------------------------------------------------- */
%>


<DECLAREPARAM NAME="Skus" 
              CLASS="java.util.List" 
              DESCRIPTION="Array of Search Results">


<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach"/>


<dsp:droplet name="ForEach">
  <dsp:param name="array" param="Skus"/>
  <dsp:param name="elementName" value="element"/>
  <dsp:oparam name="outputStart">
   <b>検索条件に適合する製品が見つかりました</b>
   <br><br>
  </dsp:oparam>
  <dsp:oparam name="output"> 
    
  <dsp:droplet name="RQLQueryForEach">
    <dsp:param name="skuId" param="element"/>
    <dsp:param name="repository" bean="/atg/commerce/catalog/ProductCatalog" />
    <dsp:param name="itemDescriptor" value="product"/>
    <dsp:param name="queryRQL" value="childSKUs INCLUDES :skuId  "/>
    <dsp:param bean="/atg/dynamo/transaction/TransactionManager" name="transactionManager"/>
    <dsp:oparam name="output">
      <%-- Display a link to the element: --%>
      <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.template.url">
      <dsp:a page="<%=urlStr%>">
        <dsp:param name="id" param="element.repositoryId"/>
	<dsp:param name="navAction" value="jump"/>
	<dsp:param name="Item" param="element"/>   
	<dsp:valueof param="element.displayName">名前なし</dsp:valueof>&nbsp;-&nbsp;<dsp:valueof param="element.description"/>
      </dsp:a>
      </dsp:getvalueof>
      <br>
     </dsp:oparam>
  </dsp:droplet>
  </dsp:oparam>
  <dsp:oparam name="empty">
    <b>検索の条件に合うカテゴリは見つかりませんでした</b>
    <p>
  </dsp:oparam>
</dsp:droplet>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/search/PartNumberSearchResults.jsp#2 $$Change: 651448 $--%>
