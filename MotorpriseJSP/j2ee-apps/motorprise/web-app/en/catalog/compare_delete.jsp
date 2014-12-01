<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductList"/>   
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>   

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="Product Comparisons"/></dsp:include>

<!-- table to contain whole page -->
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>

  <!-- breadcrumbs -->
  <tr bgcolor="#DBDBDB"> 
    <td colspan=2 height=18>&nbsp;
    <dsp:a href="../home.jsp"><span class="small"> Product Catalog</dsp:a> &gt;</span>
    <dsp:a href="compare.jsp"><span class="small">Product Comparisons</dsp:a> &gt;</span> 
    <span class="small">Edit Comparison List</span></td>
  </tr>
  
  <tr valign=top>
    <td width=175>
     
    <!-- catalog categories -->
    <dsp:include page="../common/CatalogNav.jsp"></dsp:include> 
    </td>
    <td width=625>
    <!-- main content area -->
    <table border=0 cellpadding=4 width=100%>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr>
        <td width=25><dsp:img src="../images/d.gif" hspace="12"/></td>
        <td><span class=big>Product Comparisons</span></td>
      </tr>

      <tr>
      <td></td>
      <td><dsp:form action="compare_delete.jsp" method="POST">
      <dsp:droplet name="ForEach">
	<dsp:param bean="ProductList.items" name="array"/>
	<dsp:param bean="ProductList.sortString" name="sortProperties"/>
	<!-- Sort in same order as product comparison table -->

	<dsp:oparam name="empty">
	  There are no items in your comparison list.
	</dsp:oparam>

	<dsp:oparam name="outputStart">
	  <b>Remove items from comparison list</b>
	  <blockquote>
	</dsp:oparam>

	<dsp:oparam name="output">
	  <dsp:setvalue paramvalue="element" param="currentEntry"/>
	  <dsp:input bean="ProductListHandler.entryIds" paramvalue="currentEntry.id" type="checkbox"/>
	  <dsp:valueof valueishtml="<%=true%>" param="currentEntry.productLink"/> - <dsp:valueof param="currentEntry.product.description"/></br>
	</dsp:oparam>

	<dsp:oparam name="outputEnd">
	  </blockquote>
	  <br>
	  <dsp:input bean="ProductListHandler.clearListSuccessURL" type="hidden" value="compare.jsp"/>
	  <dsp:input bean="ProductListHandler.clearList" type="submit" value="Remove all"/> &nbsp;
	  <dsp:input bean="ProductListHandler.removeProductSuccessURL" type="hidden" value="compare.jsp"/>
	  <dsp:input bean="ProductListHandler.removeEntries" type="submit" value="Remove selected items"/>
	</dsp:oparam>
      </dsp:droplet>
      </dsp:form></td>

      </td>
      </tr>
    </table> 
    </td>
  </tr>
</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/catalog/compare_delete.jsp#2 $$Change: 651448 $--%>
