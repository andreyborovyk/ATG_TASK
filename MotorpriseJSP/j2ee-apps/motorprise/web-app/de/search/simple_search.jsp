<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/dynamo/droplet/ForEach"/>
<dsp:importbean bean="/atg/commerce/catalog/CatalogSearch"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>

<DECLAREPARAM NAME="noCrumbs" 
              CLASS="java.lang.String" 
              DESCRIPTION="This is for deciding what kind of breadcrumbs to display.
                           If this is true, then breadcrumbs will show: Store Home|Search,
                           instead of nav history. Default is false."
                           OPTIONAL>


<dsp:include page="../common/HeadBody.jsp"><dsp:param name="pagetitle" value=" Einfache Suche"/></dsp:include>
<table border=0 cellpadding=0 cellspacing=0 width=800>
<tr>
  <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
</tr>
<tr bgcolor="#DBDBDB" > 
  <td colspan=2 height=18>&nbsp; 
  <!-- breadcrumbs --> 
  <span class="small">
  <dsp:droplet name="Switch">
    <dsp:param name="value" param="noCrumbs"/>
      <dsp:oparam name="true"><dsp:a href="../home.jsp">Produktkatalog</dsp:a> &nbsp; Einfache Suche</dsp:oparam>
      <dsp:oparam name="default"><dsp:param name="noCrumbs" value="false"/>
        <dsp:include page="../common/breadcrumbs.jsp"><dsp:param name="displaybreadcrumbs" value="true"/><dsp:param name="no_new_crumb" value="true"/></dsp:include>
      </dsp:oparam>
  </dsp:droplet></span> 
  </td>
</tr>


<tr> 
  <td width=55><dsp:img src="../images/d.gif" hspace="27"/></td>
  <td valign="top" width=745><br><span class="big">Suche</span></td>
</tr>
            
<tr>
  <td width="40"><dsp:img src="../images/d.gif"/></td>
  <td><br>
    <dsp:getvalueof id="pval0" bean="CatalogSearch.searchResultsByItemType"><dsp:include page="SearchResults.jsp"><dsp:param name="ResultArray" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
  </td>
</tr>

<tr><td colspan=2><dsp:img src="../images/d.gif" vspace="6"/></td></tr>

<tr>
  <td width="40"><dsp:img src="../images/d.gif"/></td>
  <td><!-- simple search box -->
    <table bgcolor="#FFCC66" border=0 cellpadding=0 cellspacing=0>
      <tr>
        <td colspan=3>
          <table width=100% cellpadding=4 cellspacing=0 border=0>
          <tr><td class=box-top>&nbsp;Einfache Suche</td></tr>
          </table>
        </td>
      </tr>
      
      <tr><td bgcolor="#666666"><dsp:img src="../images/d.gif" width="1"/></td> 
          <td>
            <dsp:form action="simple_search.jsp" method="POST">
            <table width=100% cellpadding=6 cellspacing=0 border=0>
            <tr>
              <td></td>
              <td bgcolor="#ffcc66">
              <input name="repositoryKey" type="hidden" value="<dsp:valueof bean="/OriginatingRequest.requestLocale.locale"/>">
              <dsp:input bean="CatalogSearch.searchInput" size="30" type="text"/>
              <input name="noCrumbs" type="hidden" value="<dsp:valueof param="noCrumbs"/>">
              <!--  use this hidden form tag to make sure the search handler is invoked if someone does not hit the submit button -->
              <dsp:input bean="CatalogSearch.search" type="hidden" value="Search"/>
              <dsp:input bean="CatalogSearch.search" type="submit" value="Suche"/><br><!--<span class="help">Separate words or phrases by <b>AND</b> or <b>OR</b></span>-->    
              <p>
              <span class=smallb><dsp:a href="advanced_search.jsp">
                <dsp:param name="noCrumbs" param="noCrumbs"/>
                Erweiterte Suche verwenden</span></dsp:a>
              </td>
            </tr>
            <tr>
              <td></td>
              <td>
              <span class=smallb><dsp:a href="part_number_search.jsp">
               <dsp:param name="noCrumbs" param="noCrumbs"/>
               Verwenden Sie das Suchformular für Produktnummern.</dsp:a></span>
              </td>
            </tr>        
            </table>
            </dsp:form>  
          </td>
          <td bgcolor="#666666"><dsp:img src="../images/d.gif" width="1"/></td>
      </tr>
      <tr><td bgcolor="#666666" colspan=3><dsp:img src="../images/d.gif"/></td></tr>
      </table>
    </td>
  </tr>
  </table>


</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/search/simple_search.jsp#2 $$Change: 651448 $--%>
