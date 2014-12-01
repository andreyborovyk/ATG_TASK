<%@ taglib uri="dsp" prefix="dsp" %> 
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/dynamo/droplet/BeanProperty"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsNull"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductList"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/projects/b2bstore/catalog/MemberComparisonTable"/>
<dsp:importbean bean="/atg/projects/b2bstore/catalog/GuestComparisonTable"/>

<%/*
  First decide which table info object to use when building the product
  comparison table.  Members who are logged in get one table, while guests 
  get a different table that omits price information.
*/%> 

<dsp:droplet name="Switch">
  <dsp:param bean="Profile.transient" name="value"/>
  <dsp:oparam name="true">
    <dsp:setvalue bean="ProductList.tableInfo" beanvalue="GuestComparisonTable"/>
  </dsp:oparam>
  <dsp:oparam name="false">
    <dsp:setvalue bean="ProductList.tableInfo" beanvalue="MemberComparisonTable"/>
  </dsp:oparam>
</dsp:droplet>


<%/*
  Next handle updating sort directives if we were passed a sortBy property 
  name and an optional sort direction. 
*/%>

<dsp:droplet name="IsNull">
  <dsp:param name="value" param="sortBy"/>

  <!-- If we don't have a sortBy parameter, reset the sort info -->

  <dsp:oparam name="true">
    <dsp:setvalue bean="ProductList.tableInfo.reset" value="<%=null%>"/>
  </dsp:oparam>

  <!-- If we got a sortBy parameter, load it into SortPropertiesDroplet -->
  <!-- Look to see if we got the optional sortDir parameter too.        -->
  <!-- If so, use it, else default to cycle_up_if_primary.              -->

  <dsp:oparam name="false">
    <dsp:droplet name="IsNull">
      <dsp:param name="value" param="sortDir"/>
      <dsp:oparam name="true">
        <dsp:setvalue bean="ProductList.tableColumns[param:sortBy].sortDirection" value="cycle_up_if_primary"/>
      </dsp:oparam>
      <dsp:oparam name="false">
        <dsp:setvalue bean="ProductList.tableColumns[param:sortBy].sortDirection" paramvalue="sortDir"/>
      </dsp:oparam>
    </dsp:droplet>
  </dsp:oparam>
</dsp:droplet>

<%/* Load the latest inventory information into the product comparison list */%>
<dsp:setvalue bean="ProductList.refreshInventoryData" value=""/>

<%/* Now we can go ahead and generate the page. */%>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="製品の比較"/></dsp:include>


<!-- table to contain whole page -->
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>

  <!-- breadcrumbs -->
  <tr bgcolor="#DBDBDB"> 
    <td colspan=2 height=18><dsp:a href="../home.jsp">
    <span class="small">&nbsp; 製品カタログ</dsp:a> &gt;</span>
    <span class="small">製品の比較</span> </td>
  </tr>
  
  <tr valign=top>
    <!-- catalog categories -->
    <td width=175><dsp:include page="../common/CatalogNav.jsp"></dsp:include></td>
    <!-- main content area -->

    <td width=625>
    <table border=0 cellpadding=4 width=100%>
      <tr><td><dsp:img src="../images/d.gif"/></td></tr>
      <tr>
        <td width=25><dsp:img src="../images/d.gif" hspace="12"/></td>
        <td><span class=big>製品の比較</span></td>
      </tr>

      <tr>
        <td></td>
        <td><dsp:droplet name="ForEach">
    <dsp:param bean="ProductList.items" name="array"/>
    <dsp:param bean="ProductList.sortString" name="sortProperties"/>
    <dsp:oparam name="empty">
      比較リストにアイテムがありません。
    </dsp:oparam>

    <%/*
      Generate column headings.   Each heading is a link.  Clicking
      on the link makes that column the primary sort column, cycling
      through sort directions if it was already the primary sort column
    */%>
    
    <dsp:oparam name="outputStart">
      <table border=0 cellpadding=4 cellspacing=1 width=100%>
            <tr> 
              <td colspan=4><span class=help>カラムヘッダをクリックすると結果をソートできます。
              </span></td>
            </tr>
      <tr bgcolor="#66666">      
      <dsp:droplet name="ForEach">
        <dsp:param bean="ProductList.tableColumns" name="array"/>
        <dsp:param name="sortProperties" value=""/>
        <dsp:oparam name="output">
    <dsp:setvalue paramvalue="element.heading" param="heading"/>
    <dsp:setvalue paramvalue="element.property" param="property"/>
    <TD><span class="smallbw">

      <dsp:a href="compare_result.jsp">
        <dsp:param name="sortBy" param="index"/>
        
        <%/*
        If and only if the current column is the primary sort column
        we want to display + or - as a directional indicator to show
        the sort direction for that column.
        */%> 
        
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="element.sortPosition"/>

          <!-- Primary sort column gets special treatment -->
          <dsp:oparam name="1"><font color=ffffff>
      <dsp:droplet name="Switch">
        <dsp:param name="value" param="element.sortDirection"/>
        <dsp:oparam name="ascending">
          <span class="smallbw"><dsp:valueof valueishtml="<%=true%>" param="heading"/>&nbsp[+]</span>
        </dsp:oparam>
        <dsp:oparam name="descending">
          <span class="smallbw"><dsp:valueof valueishtml="<%=true%>" param="heading"/>&nbsp[-]</span>
        </dsp:oparam>
        <dsp:oparam name="null">
          <span class="smallbw"><dsp:valueof valueishtml="<%=true%>" param="heading"/></span>
        </dsp:oparam>
      </dsp:droplet>
                        </font>
          </dsp:oparam>

          <!-- Others just display the column name -->
          <dsp:oparam name="default"><font color=ffffff>
      <span class="smallbw"><dsp:valueof valueishtml="<%=true%>" param="heading"/></span></font>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:a>
    </span></TD>
        </dsp:oparam>
      </dsp:droplet>
      </TR>
    </dsp:oparam>

    <!-- Generate a row per product -->

    <dsp:oparam name="output">
      <dsp:setvalue paramvalue="element" param="currentProduct"/>
      <tr valign=top>
      <dsp:droplet name="ForEach">
        <%/* 
        Here we are going to iterate over all of the property names
        we want to display in this row using BeanProperty to display
        each one.  We handle the "Price" column specially, using a 
        currency converter on the value to get properly localized
        currency symbols and formatting.
        */%>       
        <dsp:param bean="ProductList.tableColumns" name="array"/>
        <dsp:param name="sortProperties" value=""/>
        <dsp:oparam name="output">
          <td><dsp:droplet name="BeanProperty">
      <dsp:param name="bean" param="currentProduct"/>
      <dsp:param name="propertyName" param="element.property"/>
      <dsp:oparam name="output">
        <dsp:droplet name="Switch">
          <dsp:param name="value" param="element.name"/>
          <dsp:oparam name="price">
            <dsp:getvalueof id="useLocale" bean="Profile.priceList.locale">
              <dsp:valueof converter="currency" locale="<%=useLocale%>" param="propertyValue"/>
            </dsp:getvalueof>
          </dsp:oparam>
          <dsp:oparam name="default">
            <dsp:valueof valueishtml="<%=true%>" param="propertyValue"/>
          </dsp:oparam>
        </dsp:droplet>
      </dsp:oparam>
    </dsp:droplet></td>
        </dsp:oparam>
      </dsp:droplet>
      </tr>
    </dsp:oparam>

    <!-- Close the table -->

    <dsp:oparam name="outputEnd">
      </TABLE>
      <br>
      <dsp:a href="compare_delete.jsp"><span class="smallb">比較リストの編集</span></dsp:a>
    </dsp:oparam>
  </dsp:droplet></td>
      </tr>
    </table>
    </td>
  </tr>
</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/catalog/compare_result.jsp#2 $$Change: 651448 $--%>
