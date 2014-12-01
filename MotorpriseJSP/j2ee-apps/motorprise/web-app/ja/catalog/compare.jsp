<%@ taglib uri="dsp" prefix="dsp" %>
<%@ page contentType="text/html; charset=Shift_JIS" %>
<dsp:page>

<%
/* 
 * This page should be identical to compare_select.jsp except that 
 * it omits the compareSearchResult fragment between the product
 * comparison list and the comparison search form.  If you edit
 * this page, edit compare_select.jsp to match.
 */
%> 

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="製品の比較"/></dsp:include>

<!-- table to contain whole page -->
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>

  <!-- breadcrumbs -->
  <tr bgcolor="#DBDBDB"> 
    <td colspan=2 height=18>
    <dsp:a href="../home.jsp"><span class="small">&nbsp; 製品カタログ</dsp:a> &gt;</span>
    <span class="small">製品の比較</span> </td>
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
        <td><span class=big>製品の比較</span></td>
      </tr>
      <tr>
        <td></td>
	<td><dsp:include page="compareList.jsp"></dsp:include></td>
      </tr>
      <tr>
        <td></td>
        <td><dsp:include page="compareSearchForm.jsp"></dsp:include></td>
      </tr>
     </table>
    </td>
  </tr>
</table>
</div>
</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/ja/catalog/compare.jsp#2 $$Change: 651448 $--%>
