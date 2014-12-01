<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>


<%
/* ---------------------------------------------------------------
This JSP droplet displays the contents of search
that potentially returns both category and product repository items.
The one paramater, ResultArray, accepts a HashMap that contains
elements with the keys "category" and "product".  The values of these
keys are collections of category or product repository items found in
the search.
----------------------------------------------------------------*/
%>


<DECLAREPARAM NAME="ResultArray" 
              CLASS="java.util.Map" 
              DESCRIPTION="Array of Search Results">


<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/RQLQueryForEach"/>


<dsp:droplet name="ForEach">
  <dsp:param name="array" param="ResultArray"/>
  
  <%/*Each item in this array is a Collection of Categories or Products...*/%>
  <dsp:param name="elementName" value="ResultCollection"/>
  
  <dsp:oparam name="output">
    <dsp:droplet name="Switch">

      <%--The key tells us if this is a Collection of Products or Categories: --%>
      <dsp:param name="value" param="key"/>

      <%--For the list of CATEGORIES: --%>
      <dsp:oparam name="category">

        <blockquote>

        <dsp:droplet name="Switch">
          <dsp:param name="value" param="ResultCollection"/>
          <dsp:oparam name="default">
            <p>
            
            <%-- For each Category in the Collection: --%>
            <dsp:droplet name="ForEach">
              <dsp:param name="array" param="ResultCollection"/>  
              <dsp:param name="sortProperties" value="+displayName"/>
              <dsp:param name="elementName" value="Category"/>
              <dsp:oparam name="outputStart">
                <b>検索条件に適合するカテゴリが見つかりました</b>
                <p>
              </dsp:oparam>
              <dsp:oparam name="output">

                <%-- Display a link to the Category: --%>
                <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="Category.template.url">
                <dsp:a page="<%=urlStr%>">
                  <dsp:param name="id" param="Category.repositoryId"/>
                  <dsp:param name="navAction" value="jump"/>
                  <dsp:param name="Item" param="Category"/>   
                  <dsp:valueof param="Category.displayName">名前なし</dsp:valueof>
		</dsp:a>
		</dsp:getvalueof>
                <br>
              </dsp:oparam>
              <dsp:oparam name="empty">
                <b>検索の条件に合うカテゴリは見つかりませんでした</b>
                <p>
              </dsp:oparam>
            </dsp:droplet>          
          </dsp:oparam>
          <%-- If NO Categories returned by the search: --%>
          <dsp:oparam name="unset">
            検索条件に適合するカテゴリアイテムがカタログに見つかりません            
          </dsp:oparam>
        </dsp:droplet><%/*ForEach Category*/%>
        
        </blockquote>
        <P>
      </dsp:oparam>
      
      <%-- For the list of PRODUCTS: --%>
      <dsp:oparam name="product">
        <blockquote><p>

        <dsp:droplet name="Switch">
          <dsp:param name="value" param="ResultCollection"/>

          <dsp:oparam name="default">

            <%/*For each Product in the Collection: */%>
            <dsp:droplet name="ForEach">
              <dsp:param name="array" param="ResultCollection"/>
              <dsp:param name="sortProperties" value="+displayName"/>
              <dsp:param name="elementName" value="Product"/>
              <dsp:oparam name="outputStart">
                <p>
                <b>検索条件に適合する製品が見つかりました</b>
                <p>
              </dsp:oparam>
              <dsp:oparam name="output">
                <%-- Display a link to the Product: --%>
                <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="Product.template.url">
                <dsp:a page="<%=urlStr%>">
                  <dsp:param name="id" param="Product.repositoryId"/>
                  <dsp:param name="navAction" value="jump"/>
                  <dsp:param name="Item" param="Product"/>   
                  <dsp:valueof param="Product.displayName">名前なし</dsp:valueof>&nbsp;-&nbsp;<dsp:valueof param="Product.description"/>
		</dsp:a> 
		</dsp:getvalueof>
                <br>
              </dsp:oparam>
              <dsp:oparam name="empty">
                <b>検索の条件に合うカテゴリは見つかりませんでした</b>
                <p>
              </dsp:oparam>

            </dsp:droplet> <%/*ForEach Product*/%>
            
         </dsp:oparam>

          <%/*If NO Products returned by the search: */%>
          <dsp:oparam name="unset">
            検索条件に適合する製品アイテムがカタログに見つかりません<p>
          </dsp:oparam>
          
        </dsp:droplet>
        </blockquote><P>
      </dsp:oparam>
    </dsp:droplet> 
    
  </dsp:oparam>

</dsp:droplet> <%/*ForEach Item returned by Search */%>
  
<%-- </dsp:droplet> commented out by Naveen --%>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/search/SearchResults.jsp#2 $$Change: 651448 $--%>
