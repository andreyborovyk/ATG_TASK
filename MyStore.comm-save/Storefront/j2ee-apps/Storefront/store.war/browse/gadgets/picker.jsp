<dsp:page>

  <%-- This page expects the following input parameters
       productId - the product id being displayed
       categoryId (optional) - the id of the category the product is viewed from
  --%>
  <div id="atg_store_picker">     
    <dsp:include page="pickerContents.jsp">
      <dsp:param name="productId" param="productId"/>
      <dsp:param name="categoryId" param="categoryId"/>
    </dsp:include>
  </div><%-- atg_store_picker --%>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/browse/gadgets/picker.jsp#3 $$Change: 635816 $--%>
