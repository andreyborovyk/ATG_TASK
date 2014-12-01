<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<%
/* 
 * This page should be identical to compare.jsp except that it
 * includes the compareSearchResult fragment between the product
 * comparison list and the comparison search form.  If you edit
 * this page, edit compare.jsp to match.
 */
%>

<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value="Produktvergleiche"/></dsp:include>

<!-- table to contain whole page -->
<table border=0 cellpadding=0 cellspacing=0 width=800>
  <tr>
    <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
  </tr>

  <!-- breadcrumbs -->
  <tr bgcolor="#DBDBDB"> 
    <td colspan=2 height=18>
    <dsp:a href="../home.jsp"><span class="small">&nbsp; Produktkatalog</dsp:a> &gt;</span>
    <span class="small">Produktvergleiche</span> </td>
  </tr>
  
  <tr valign=top>
    <td width=175>
    <!-- catalog categories -->
    <dsp:include page="../common/CatalogNav.jsp"></dsp:include> 
    </td>

    <td width=625>
    <!-- main content area -->
    <table border=0 cellpadding=4 width=100%>
      <tr>
        <td width=25><dsp:img src="../images/d.gif" hspace="12"/></td>
        <td><span class=big>Produktvergleiche</span></td>
      </tr>
      <tr>
        <td></td>
	<td><dsp:include page="compareList.jsp"></dsp:include></td>
      </tr>
      <tr>
        <td></td>
	<td><dsp:include page="compareSearchResult.jsp"></dsp:include></td>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/catalog/compare_select.jsp#2 $$Change: 651448 $--%>
