<%@ taglib uri="dsp" prefix="dsp" %>
<dsp:page>

	<DECLAREPARAM NAME="id" CLASS="java.lang.String" 
                     DESCRIPTION="The id of the product to display">
                     
<dsp:importbean bean="/atg/commerce/order/purchase/CartModifierFormHandler"/>
<dsp:importbean bean="/atg/userprofiling/Profile"/>
<dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
<dsp:importbean bean="/atg/dynamo/droplet/Switch"/>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductList"/>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListContains"/>
<dsp:importbean bean="/atg/commerce/catalog/comparison/ProductListHandler"/>

<dsp:droplet name="IsEmpty">
  <dsp:param name="value" param="id"/>
  <dsp:oparam name="true">
    <span class=error>
      This page can not be viewed without a valid product id.
      <p>
      <dsp:a href="../home.jsp">Product Catalog </dsp:a>
    </span>
  </dsp:oparam>
</dsp:droplet>

<%-- Set up the basic page branding, navigation elements, etc. --%>

<table border=0 cellpadding=0 cellspacing=0 width=800>
<tr>
  <td colspan=2><dsp:include page="../common/BrandNav.jsp"></dsp:include></td>
</tr>


<%-- Now try to look up the product and display it.  In case of errors display an
     error message in the main content area of the page and set the page title
     accordingly --%>

<dsp:droplet name="/atg/commerce/catalog/ProductLookup">  

  <dsp:oparam name="empty">
    <dsp:include page="../catalog/ProductHead.jsp">
      <dsp:param name="pagetitle" value="No such product"/>
      <dsp:param name="no_new_crumb" value="true"></dsp:param>
    </dsp:include>

    <td valign="top" colspan=2>
    <b>The selected product that does not exist in the product catalog.</b>
    </td>
  </dsp:oparam>

  <dsp:oparam name="wrongCatalog">
    <dsp:include page="../catalog/ProductHead.jsp">
      <dsp:param name="pagetitle" value="Product not in catalog"/>
      <dsp:param name="no_new_crumb" value="true"></dsp:param>
    </dsp:include>
 
    <td valign="top" colspan=2>                  
    <b>The selected product does not exist in your organization's product catalog.</b>
    </td>
  </dsp:oparam>

  <dsp:oparam name="noCatalog">
    <dsp:include page="../catalog/ProductHead.jsp">
      <dsp:param name="pagetitle" value="No catalog specified"/>
      <dsp:param name="no_new_crumb" value="true"></dsp:param>
    </dsp:include>
 
    <td valign="top" colspan=2>                  
    <b>The system could not determine which catalog to use.</b>
    </td>
  </dsp:oparam>

  <dsp:oparam name="output"> 
    <%/* Add this product to the user's list of products browsed. */%>
    <dsp:droplet name="/atg/commerce/catalog/ProductBrowsed">
      <dsp:param name="eventobject" param="element"/>
    </dsp:droplet>
         
    <dsp:getvalueof id="page_title" param="element.displayName">
      <dsp:include page="../catalog/ProductHead.jsp">
        <dsp:param name="pagetitle" value="<%=page_title%>"/>
      </dsp:include>

    </dsp:getvalueof>

          <!-- main content area -->
          <td valign="top" colspan=2>                  
            <table border=0 cellpadding=0 cellspacing=0 width=100%>
            <tr>
              <td valign="top" width=45%>
                <table border=0 cellpadding=4>
                <tr>
                  <td>
                    <span class=categoryhead>
                    <dsp:valueof param="element.displayName">No name</dsp:valueof></span>
                    <br>
                    <b><dsp:valueof param="element.description"/></b></td>
                  </td>
                </tr>
                <tr valign=top>
                  <td>
                    <dsp:include page="../common/FormError.jsp"></dsp:include>
                    <dsp:droplet name="IsEmpty">
                      <dsp:param name="value" param="element.largeImage.url"/>
                      <dsp:oparam name="false">
		        <dsp:getvalueof id="imageURL" param="element.largeImage.url" idtype="java.lang.String">
                        <dsp:img hspace="70" alt="Product image" src="<%=imageURL%>"/>
			</dsp:getvalueof>
                      </dsp:oparam>
                    </dsp:droplet>

                    <dsp:getvalueof id="pval0" param="element"><dsp:include page="SKUProperties.jsp"><dsp:param name="product" value="<%=pval0%>"/></dsp:include></dsp:getvalueof>
                    <br>                             
                    <span class=smallb>Product Description</span><br>
                    <span class=small><dsp:valueof param="element.longDescription">No description</dsp:valueof>
                   </span>
                  </td>
                </tr>
                </table>
              </td>
              <!-- gutter cell -->
              <td rowspan=2>&nbsp;</td>
              <td valign="top" width=45% align=right rowspan=2>
              <!-- display order information -->
                <table border=0 cellpadding=0 cellspacing=0 width=90% bgcolor="#E5E5E5">
                <dsp:droplet name="Switch">
                  <dsp:param bean="Profile.transient" name="value"/>
                  <dsp:oparam name="false">
                <tr>
                  <td bgcolor="#999999">
                    <table border=0 cellpadding=1 width=100%>
                    <tr><td><span class=smallbw>&nbsp;Add to Order</span></td></tr>
                    </table>
                  </td>
                </tr>
                <tr>
                  <td>
                  
                    <dsp:getvalueof id="pval0" param="element">
                      <dsp:include page="AddToCart.jsp">
                        <dsp:param name="product" value="<%=pval0%>"/>
                      </dsp:include>
                    </dsp:getvalueof>
                    
                  </td>
                </tr>
                <tr><td bgcolor="#FFFFFF"><dsp:img src="../images/d.gif" vspace="2"/></td></tr>
                <tr>
                  <td bgcolor="#999999">
                    <table border=0 cellpadding=1 width=100%>
                      <tr><td><span class=smallbw>&nbsp;Purchase Lists</span></td></tr>
                    </table>
                  </td>
                </tr>
                <dsp:droplet name="IsEmpty">
                  <dsp:param bean="Profile.purchaseLists" name="value"/>
                  <dsp:oparam name="false">
                <tr>
                  <td>
                    <dsp:getvalueof id="pval0" param="element">
                      <dsp:include page="AddToList.jsp">
                        <dsp:param name="product" value="<%=pval0%>"/>
                      </dsp:include>
                    </dsp:getvalueof>
                   </td>
                </tr>
                  </dsp:oparam>
                  <dsp:oparam name="true">
                <tr>
                  <td>
                    <table border=0 cellpadding=3 width=100%>
                    <tr>
                      <td><span class=smallb><dsp:a href="../user/purchase_lists.jsp">
              <dsp:param name="noCrumbs" value="false"/>Create new purchase list</dsp:a></span>
                      </td>
                    </tr>
                    <tr><td><dsp:img src="../images/d.gif" vspace="1"/></td></tr>
                    </table>
                  </td>
                </tr>
                </dsp:oparam>
                </dsp:droplet>
                <tr><td bgcolor="#FFFFFF"><dsp:img src="../images/d.gif" vspace="2"/></td></tr>
      </dsp:oparam>
          </dsp:droplet>
                <tr>
                  <td bgcolor="#999999">
                    <table border=0 cellpadding=1 width=100%>
                    <tr><td><span class=smallbw>&nbsp;Product Comparisons</span></td></tr>
                    </table>
                  </td>
                </tr>
                <tr><td><dsp:img src="../images/d.gif" vspace="1"/></td></tr>
                <tr>
                  <td>
                    <table border=0 cellpadding=3>
                    <dsp:form action="product.jsp" method="post">
                      <tr>
                        <td bgcolor="#E5E5E5">  
                        <input name="id" type="hidden" value="<dsp:valueof param="element.repositoryId"/>">  
                        <dsp:input bean="CartModifierFormHandler.SessionExpirationURL" type="hidden" value="../common/session_expired.jsp"/>
                        <dsp:input bean="CartModifierFormHandler.productId" paramvalue="product.repositoryId" type="hidden"/>
          
                        <dsp:droplet name="ProductListContains">
                          <dsp:param bean="ProductList" name="productList"/>
                          <dsp:param name="productID" param="element.repositoryId"/>
                          <dsp:oparam name="true">
                            <dsp:input bean="ProductListHandler.productID" paramvalue="productID" type="hidden"/>
                            <dsp:input bean="ProductListHandler.removeProduct" type="submit" value="Remove from list"/>
                          </dsp:oparam>
                          <dsp:oparam name="false">
                            <dsp:input bean="ProductListHandler.productID" paramvalue="productID" type="hidden"/>
                            <dsp:input bean="ProductListHandler.addProduct" type="submit" value="Add to comparison list"/>
                           </dsp:oparam>
                         </dsp:droplet> 
                      </td>
                    </tr>            
<!--check to see if there are items in comparison list-->
                    <dsp:droplet name="IsEmpty">
                      <dsp:param bean="ProductList.items" name="value"/>
                      <dsp:oparam name="false">
                    <tr>
                      <td><span class=smallb><dsp:a href="compare.jsp">View comparison list</dsp:a></td>
                    </tr>
                      </dsp:oparam>
                      </dsp:droplet>
                      </dsp:form>
                    </table>
                  </td>
                </tr>
                <tr><td><dsp:img src="../images/d.gif" vspace="3"/></td></tr>
                <tr><td bgcolor="#FFFFFF"><dsp:img src="../images/d.gif" vspace="2"/></td></tr>
                <!-- related items -->
                <dsp:droplet name="/atg/commerce/catalog/ForEachItemInCatalog">
                  <dsp:param name="array" param="element.relatedProducts"/>
                  <dsp:oparam name="outputStart">
                    <tr>
                      <td bgcolor="#999999">
                      <table border=0 cellpadding=1 width=100%>
                        <tr><td><span class=smallbw>&nbsp;Related Items</span></td></tr>
                      </table>
                      </td>
                    </tr>
                    <tr>
                      <td>
                      <table border=0 cellpadding=1 width=100%>
                        <tr>
                          <td></td>
                          <td>
                  </dsp:oparam>  
                  <dsp:oparam name="outputEnd">
                          </td>
                        </tr>
                      </table>
		  </dsp:oparam>
                  <dsp:oparam name="output">
                    <dsp:getvalueof id="urlStr" idtype="java.lang.String" param="element.template.url">
		    <dsp:a page="<%=urlStr%>">
		      <dsp:param name="id" param="element.repositoryId"/>
		      <dsp:param name="navAction" value="jump"/>
		      <dsp:param name="Item" param="element"/>
                      <dsp:valueof param="element.displayName"/>
		    </dsp:a>
                    </dsp:getvalueof>
                    <br>
                  </dsp:oparam>
                </dsp:droplet>
                      </td>
                    </tr>  
                  </table>
              </td>
            </tr>
            </table>
</dsp:oparam>
</dsp:droplet>

<%-- Close the main content area of the page and all enclosing tables --%>
        </tr>
        </table>
      </td>
    </tr>
    </table>   

</body>
</html>
</dsp:page>
<%-- @version $Id: //product/B2BCommerce/version/10.0.3/release/MotorpriseJSP/j2ee-apps/motorprise/web-app/en/catalog/product.jsp#2 $$Change: 651448 $--%>
