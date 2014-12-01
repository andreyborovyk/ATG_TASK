<dsp:page>

  <%-- This page expects the following input parameters
       product - the product object being displayed
  --%>
  <dsp:getvalueof id="product" param="product"/>
  
  <dsp:valueof param="product.longDescription" valueishtml="true">
    <fmt:message key="common.longDescriptionDefault"/>
  </dsp:valueof>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/browse/gadgets/productMetadataDescription.jsp#3 $$Change: 635816 $ --%>
