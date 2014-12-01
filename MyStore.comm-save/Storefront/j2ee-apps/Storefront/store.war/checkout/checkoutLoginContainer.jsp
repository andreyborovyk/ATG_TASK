<%--
  This gadget renders the checkout login page
--%>

<dsp:page>
  
    <fmt:message key="checkout_title.checkout" var="title"/>
    <crs:checkoutContainer currentStage="login"
                           showOrderSummary="false"
                           skipSecurityCheck="true"
                           title="${title}">
      <jsp:body>
        <dsp:include page="gadgets/checkoutLogin.jsp" flush="true"/>
      </jsp:body>
    </crs:checkoutContainer>

</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/checkout/checkoutLoginContainer.jsp#3 $$Change: 635816 $--%>
