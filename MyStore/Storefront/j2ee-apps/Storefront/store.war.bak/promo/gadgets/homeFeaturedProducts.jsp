<dsp:page>
  <div class="atg_store_homepage_products">

    <ul class="atg_store_product">
      <li>
        <%-- The first featured product --%>
        <dsp:include page="/global/gadgets/targetingRandom.jsp" flush="true">
          <dsp:param name="targeter" bean="/atg/registry/Slots/HomeFeaturedProduct1"/>
          <dsp:param name="renderer" value="/promo/gadgets/promotionalItemRenderer.jsp"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="showAddToCart" value="false"/>
        </dsp:include>
      </li>
          
      <li>
        <%-- The second featured product --%>
        <dsp:include page="/global/gadgets/targetingRandom.jsp" flush="true">
          <dsp:param name="targeter" bean="/atg/registry/Slots/HomeFeaturedProduct2"/>
          <dsp:param name="renderer" value="/promo/gadgets/promotionalItemRenderer.jsp"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="showAddToCart" value="false"/>
        </dsp:include>
      </li>
      
      <li>
        <%-- The third featured product --%>
        <dsp:include page="/global/gadgets/targetingRandom.jsp" flush="true">
          <dsp:param name="targeter" bean="/atg/registry/Slots/HomeFeaturedProduct3"/>
          <dsp:param name="renderer" value="/promo/gadgets/promotionalItemRenderer.jsp"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="showAddToCart" value="false"/>
        </dsp:include>
      </li>
      
      <li>
        <%-- The forth featured product --%>
        <dsp:include page="/global/gadgets/targetingRandom.jsp" flush="true">
          <dsp:param name="targeter" bean="/atg/registry/Slots/HomeFeaturedProduct4"/>
          <dsp:param name="renderer" value="/promo/gadgets/promotionalItemRenderer.jsp"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="showAddToCart" value="false"/>
        </dsp:include>
      </li>
      
      <li>
        <%-- The fifth featured product --%>
        <dsp:include page="/global/gadgets/targetingRandom.jsp" flush="true">
          <dsp:param name="targeter" bean="/atg/registry/Slots/HomeFeaturedProduct5"/>
          <dsp:param name="renderer" value="/promo/gadgets/promotionalItemRenderer.jsp"/>
          <dsp:param name="elementName" value="product"/>
          <dsp:param name="showAddToCart" value="false"/>
        </dsp:include>
      </li>
      
    </ul>
  </div>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/promo/gadgets/homeFeaturedProducts.jsp#3 $$Change: 635816 $--%>
