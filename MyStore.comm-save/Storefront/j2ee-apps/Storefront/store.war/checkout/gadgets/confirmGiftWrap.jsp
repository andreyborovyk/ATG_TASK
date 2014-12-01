<%--
  This gadget displays gift wrap for the current order
 --%>
<dsp:page>
  <dsp:importbean bean="/atg/commerce/ShoppingCart"/>
  
  <dsp:getvalueof var="items" vartype="java.lang.Object" bean="ShoppingCart.current.commerceItems"/>
  
  <c:forEach var="currentItem" items="${items}" varStatus="status">
      <dsp:param name="currentItem" value="${currentItem}"/>
      <dsp:getvalueof var="commerceItemClassType" param="currentItem.commerceItemClassType"/>
      <c:if test="${commerceItemClassType == 'giftWrapCommerceItem'}">
        <%-- TODO display gift wrap here --%>
        <dsp:include page="/global/gadgets/orderItemsRenderer.jsp">
          <dsp:param name="currentItem" value="${currentItem}"/>
          <dsp:param name="count" value="1"/>
          <dsp:param name="size" value="1"/>
          <dsp:param name="hideSiteIndicator" value="true"/>
        </dsp:include>
      </c:if>
  </c:forEach>    
  
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/checkout/gadgets/confirmGiftWrap.jsp#3 $$Change: 635816 $--%>