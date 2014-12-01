<dsp:page>
  <fmt:message var="itemLabel" key="navigation_richCart.checkout"/>
  <fmt:message var="itemTitle" key="navigation_personalNavigation.linkTitle">
    <fmt:param value="${itemLabel}"/>
  </fmt:message>

  <dsp:a page="/cart/cart.jsp" title="${itemTitle}"
         iclass="atg_store_navCart">
    <fmt:message key="navigation_richCart.checkout" />
  </dsp:a>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/navigation/gadgets/shoppingCart.jsp#3 $$Change: 635816 $ --%>
