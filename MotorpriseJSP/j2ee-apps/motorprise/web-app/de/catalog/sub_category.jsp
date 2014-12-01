<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/ArrayIncludesValue"/>

<DECLAREPARAM NAME="id" CLASS="java.lang.String" 
                     DESCRIPTION="The id of the category to display">

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
          <dsp:param name="array" bean="Profile.catalog.rootcategories"/>
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
      <!-- left panel -->
      <dsp:include page="../common/CatalogNav.jsp"></dsp:include>
      <!-- incentives slot -->
      <dsp:include page="../common/Incentive.jsp"></dsp:include>
      </td>
      <td width=625><!-- main content -->
      
      <table border=0 cellpadding=4 width=100%>
        <!--this row used to ensure proper spacing of table cell-->
        <tr><td colspan=2><dsp:img src="../images/d.gif" hspace="304"/></td></tr>
    <tr>
          <td colspan=2>
          &nbsp;<span class="categoryhead">
          <dsp:valueof param="element.itemDisplayName">Kein Name</dsp:valueof></span>
          <br><dsp:img src="../images/d.gif" vspace="4"/><br>
            <table border=0 cellpadding=4 cellspacing=1 width=100%>
              <dsp:setvalue param="childPrds" paramvalue="element.childProducts"/>              

              <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                <dsp:param name="array" param="childPrds"/>
                <dsp:oparam name="outputStart">
                  <tr valign="bottom">
                    <td><dsp:img src="../images/d.gif" hspace="1"/></td>
                    <td bgcolor="#666666" colspan=2><span class="smallbw">Produktnr.</span></td>
                    <td bgcolor="#666666" colspan=2><span class="smallbw">Produkt</span></td>
                    <td bgcolor="#666666" colspan=2><span class="smallbw">Beschreibung</span></td>
          <td bgcolor="#666666" colspan=2><span class="smallbw">Hrst.</span></td>
                  </tr>
                </dsp:oparam>
                <dsp:oparam name="output">
                  <tr valign="top">
            <td><dsp:img src="../images/d.gif" hspace="1"/></td> 
                    <dsp:droplet name="/atg/dynamo/droplet/ForEach">
                      <dsp:param name="array" param="element.childSKUs"/>
                      <dsp:param name="elementName" value="sku"/>
                      <dsp:oparam name="output">                         
                       <td><nobr>
                       <dsp:getvalueof id="skuURL" idtype="java.lang.String" param="element.template.url">
		       <dsp:a page="<%=skuURL%>">
		         <dsp:param name="id" param="element.repositoryId"/>
                         <dsp:valueof param="sku.manufacturer_part_number">Keine Produktnummer</dsp:valueof>
		       </dsp:a>
                       </nobr></td>
		       </dsp:getvalueof>
                      </dsp:oparam>
                    </dsp:droplet>
                    
                    <td>&nbsp;</td>
                    <td>
		    <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.template.url">
		    <dsp:a page="<%=urlStr%>">
		      <dsp:param name="id" param="element.repositoryId"/>
		      <dsp:valueof param="element.displayName">Kein Name</dsp:valueof>
		    </dsp:a> 
		    </dsp:getvalueof>
                    </td>
                   
                    <td>&nbsp;</td>
                    <td><dsp:valueof param="element.description">Keine Beschreibung</dsp:valueof></td>
                    
                    <td>&nbsp;</td>
                    <td><dsp:valueof param="element.manufacturer.displayName">Unbekannt</dsp:valueof></td>

                    
                    
                  </tr>
                </dsp:oparam>
                <dsp:oparam name="outputEnd">
                  <tr>
                    <td>&nbsp;</td>
                  </tr>
                </dsp:oparam>
                <dsp:oparam name="empty">
                  <tr><td>&nbsp;&nbsp;Keine Unterprodukte gefunden.</td></tr>
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
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/de/catalog/sub_category.jsp#2 $$Change: 651448 $--%>
