<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/ErrorMessageForEach"/>
<dsp:importbean bean="/atg/commerce/catalog/RepositoryValues"/>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductList"/>
<dsp:importbean bean="/atg/projects/b2bstore/catalog/SearchCompare"/>

<%/* Display the heading for this section of the page */%>

<dsp:droplet name="IsEmpty">
  <dsp:param bean="ProductList.items" name="value"/>
  <dsp:oparam name="true"><b>比較する製品の検索</b></dsp:oparam>
  <dsp:oparam name="false"><b>比較する製品の追加検索</b></dsp:oparam>
</dsp:droplet>

<%/* Display any errors resulting from a failed search attempt */%>

<dsp:droplet name="Switch">
  <dsp:param bean="SearchCompare.formError" name="value"/>
  <dsp:oparam name="true">
    <font color=cc0000><P><STRONG><UL>
      <dsp:droplet name="ErrorMessageForEach">
        <dsp:param bean="SearchCompare.formExceptions" name="exceptions"/>
        <dsp:oparam name="output">
        <LI> <dsp:valueof param="message"/>
        </dsp:oparam>
      </dsp:droplet>
    </UL></STRONG></font>
  </dsp:oparam>
</dsp:droplet>

<%/* Display the search form */%>

<dsp:form action="compare_select.jsp" method="POST">
<table border=0 cellpadding=8 cellspacing=0>
  <tr>
    <td align=right>カテゴリによる検索</td>
    <td>
    <dsp:select bean="SearchCompare.hierarchicalCategoryId">
      <dsp:option value=""/>-- すべてのカテゴリ --
      <dsp:droplet name="RepositoryValues">
	<dsp:param name="itemDescriptorName" value="category"/>
	<dsp:oparam name="output">
	  <dsp:droplet name="ForEach">
	    <dsp:param name="array" param="values"/>
	    <dsp:param name="sortProperties" value="+displayName"/>
	    <dsp:oparam name="output">
	      <dsp:getvalueof id="categoryID" idtype="java.lang.String" param="element.repositoryId">
	        <dsp:option value="<%=categoryID%>"/>
	        <dsp:valueof param="element.displayName"/>
	      </dsp:getvalueof>
	    </dsp:oparam>
	  </dsp:droplet>
	</dsp:oparam>
      </dsp:droplet>
    </dsp:select>
    </td>
  </tr>

  <tr>
    <td align=right>メーカ</td>
    <td>
    <dsp:select bean="SearchCompare.propertyValues.manufacturer">
      <dsp:option value=""/>-- すべて --
      <dsp:droplet name="RepositoryValues">
        <dsp:param name="itemDescriptorName" value="product"/>
        <dsp:param name="propertyName" value="manufacturer"/>
	  <dsp:oparam name="output">
	  <dsp:droplet name="ForEach">
	    <dsp:param name="array" param="values"/>
	    <dsp:param name="sortProperties" value="+displayName"/>
	    <dsp:oparam name="output">
	      <dsp:getvalueof id="manufacturer" idtype="java.lang.String" param="element.displayName">
	        <dsp:option value="<%=manufacturer%>"/>
		<%=manufacturer%>
              </dsp:getvalueof>
	  </dsp:oparam>
        </dsp:droplet>
        </dsp:oparam>
      </dsp:droplet>
    </dsp:select> 
    </td>
  </tr>

  <tr>
    <td align=right>テキストの検索</td>
    <td><dsp:input bean="SearchCompare.searchInput" size="25" type="text"/>
        <%/*Include hidden input field so hitting return here submits the form*/%>
        <dsp:input bean="SearchCompare.search" type="hidden" value="Search"/></td>
  </tr>

  <tr>
    <td></td>
    <td><dsp:input bean="SearchCompare.search" type="submit" value="検索"/></td>
  </tr>

</table>
</dsp:form>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/catalog/compareSearchForm.jsp#2 $$Change: 651448 $--%>
