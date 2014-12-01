<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ArrayIncludesValue"/>

<DECLAREPARAM NAME="id" CLASS="java.lang.String" 
              DESCRIPTION="The id of the parent category to display">


<dsp:droplet name="/atg/commerce/catalog/CategoryLookup">  
  <dsp:oparam name="output">   

  <dsp:getvalueof id="page_title" param="element.displayName">
  <dsp:include page="../common/HeadBody.jsp">
    <dsp:param name="pagetitle" value="<%=page_title%>"/>
  </dsp:include>
  </dsp:getvalueof>

  <table border=0 cellpadding=0 cellspacing=0 width=800>
    <tr>
      <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
    </tr>

    <tr bgcolor="#DBDBDB"> 
      <td colspan=2 height=18><span class="small"> &nbsp;
        <dsp:droplet name="ArrayIncludesValue">
          <dsp:param name="array" bean="Profile.catalog.allrootcategories"/>
          <dsp:param name="value" param="element"/>
          <dsp:oparam name="false">
            <dsp:include page="../common/breadcrumbs.jsp"><dsp:param name="displaybreadcrumbs" value="true"/></dsp:include>
          </dsp:oparam>
          <dsp:oparam name="true">
            <dsp:include page="../common/breadcrumbs.jsp"><dsp:param name="displaybreadcrumbs" value="true"/><dsp:param name="navAction" value="jump"/><dsp:param name="navCount" value="0"/></dsp:include>
          </dsp:oparam>
        </dsp:droplet>
      &nbsp;</span>
      </td>
    </tr>
    
  
    <tr valign=top> 
      <td width=175>
        <!-- category navigation -->
        <dsp:include page="../common/CatalogNav.jsp"></dsp:include>
        <!-- incentives slot -->
        <dsp:include page="../common/Incentive.jsp"></dsp:include>

      </td>
      <td width=625>
      <!-- promotion slot -->
      <table border=0 cellpadding=4 width=100%>
        <!--this row used to ensure proper spacing of table cell-->
        <tr><td colspan=2><dsp:img src="../images/d.gif" hspace="304"/></td></tr>
        <tr>
          <td colspan=2>&nbsp;<span class="categoryhead">
            <dsp:valueof param="element.itemDisplayName">名前なし</dsp:valueof></span></td>
        </tr>
        <tr>
      <td>

        <dsp:droplet name="/atg/dynamo/droplet/ForEach">
          <dsp:param name="array" param="element.childCategories"/>
          <dsp:oparam name="outputStart">
            <table border=0 cellpadding=3 width=100%>                
          </dsp:oparam>
          <dsp:oparam name="output">
            <tr>
              <td width=3><dsp:img src="../images/d.gif" hspace="1"/></td>
              <td>
                <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.template.url">
		<dsp:a page="<%=urlStr%>">
		  <dsp:param name="id" param="element.repositoryId"/>
		  <dsp:valueof param="element.displayName">名前なし</dsp:valueof>
		</dsp:a>
                </dsp:getvalueof>
                <br>&nbsp;<dsp:valueof param="element.description"/>
              </td>
            </tr>
          </dsp:oparam>
          <dsp:oparam name="empty">
            <tr>
              <td width=3></td>
              <td>子カテゴリが見つかりません。</td>
            </tr>
          </dsp:oparam>
        </dsp:droplet> 
            </table>         
            </td>
          </tr>
    </table>
    </td>
  </tr>
 </table>
  </dsp:oparam>
</dsp:droplet>


</body>
</html>

</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/catalog/category.jsp#2 $$Change: 651448 $--%>
