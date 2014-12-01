<dsp:page>

  <%-- This page expects the following input parameters
       product - the product object whose details are shown
       categoryId (optional) - the id of the category the product is viewed from
    --%>
  <div class="atg_store_productImageContainer">
    <div class="atg_store_productImage"> 
      <dsp:include page="gadgets/productImage.jsp">
        <dsp:param name="product" param="product"/>
      </dsp:include>
    </div>
  </div>

  <dsp:include page="gadgets/productAsSeenIn.jsp">
    <dsp:param name="product" param="product"/>
  </dsp:include>  

</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/browse/productDetailCachedContainer.jsp#3 $$Change: 635816 $ --%>
