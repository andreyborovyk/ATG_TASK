<!-- COPIED FROM ATG Commerce Reference Store 10.0
     Modified by ATG Education for BIZ class -->

<dsp:page>

  <!-- start ATG EDU modifications here -->
  <div class="atg_edu_biz_outreach">

  <ul class="atg_store_product">
      <li>
        <!-- Outreach image slot -->
	<dsp:droplet name="/atg/targeting/TargetingFirst">
	   <dsp:param name="targeter" bean="/atg/registry/Slots/OutreachImageSlot"/>
  	   <dsp:oparam name="output">
  	   	Outreach image slot data:
		<img src="<dsp:valueof param='element.url'/>"/>
  	   </dsp:oparam>
  	   <dsp:oparam name="empty">
	   	 OutreachImageSlot is empty at this time.
	   </dsp:oparam>

  	</dsp:droplet>
     </li>
     <li>
         <!-- Outreach text slot -->
	<dsp:droplet name="/atg/targeting/TargetingFirst">
	   <dsp:param name="targeter" bean="/atg/registry/Slots/OutreachTextSlot"/>
  	   <dsp:oparam name="output">
  	   	Outreach text slot data:
  	        <dsp:valueof valueishtml="true" param="element.data"/>
  	   </dsp:oparam>
  	   <dsp:oparam name="empty">
	   	 OutreachTextSlot is empty at this time.
	   </dsp:oparam>

  	</dsp:droplet>
    </li>
  </ul>
  </div>
  <!-- end ATG EDU modifications here -->
  
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
