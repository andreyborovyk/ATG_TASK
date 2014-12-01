<dsp:page>

  <%--
    This page renders the contents of the cart as JSON data. 
    It determines if the cart is empty, and renders the appropriate JSP.
  --%>
  <dsp:getvalueof bean="/atg/commerce/ShoppingCart.current.CommerceItemCount" var="itemCount"/>
  <c:choose>
    <c:when test="${itemCount==0}">
      <%-- Cart is empty --%>
      <dsp:include page="cartContentsEmpty.jsp" flush="true"/>
    </c:when>
    <c:otherwise>
      <%-- Cart is not empty - render contents of cart --%>
      <dsp:include page="cartItems.jsp"/>
    </c:otherwise>
  </c:choose>

</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/cart/json/cartContentsData.jsp#3 $$Change: 635816 $--%>
