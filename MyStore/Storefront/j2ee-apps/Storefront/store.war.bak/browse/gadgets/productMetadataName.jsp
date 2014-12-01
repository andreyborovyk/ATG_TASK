<dsp:page>

  <%-- This page expects the following input parameters
       product - the product object being displayed
  --%>
  <dsp:getvalueof id="product" param="product"/>
  
  <div id="atg_store_productMetadataName">
    <p class="atg_store_productName">
      <dsp:valueof param="product.description">
        <fmt:message key="browse_productMetadata.descriptionDefault"/>
      </dsp:valueof>
    </p>
  </div>
    
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/browse/gadgets/productMetadataName.jsp#3 $$Change: 635816 $ --%>
