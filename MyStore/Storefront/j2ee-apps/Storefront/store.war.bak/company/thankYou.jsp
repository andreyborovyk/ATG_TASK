<dsp:page>

  <%-- This page appears after a customer fills out the "Email Us" form and displays a ThankYou note --%>
  <dsp:importbean bean="/atg/store/order/purchase/CartFormHandler"/>

  <crs:pageContainer divId="atg_store_company" titleKey="company_thankYou.title">

    <div id="atg_store_thankYou">

      <fmt:message key="company_thankYou.appreciateText"/>

      <dsp:form action="thankYou.jsp" method="post" formid="thankyouform">
        <crs:continueShopping>
          <dsp:input type="hidden" bean="CartFormHandler.cancelURL"
                     value="${continueShoppingURL}"/>
        </crs:continueShopping>
        <fmt:message key="common.button.continueShoppingText" var="continueShopping"/>
        <dsp:input type="submit" bean="CartFormHandler.cancel" value="${continueShopping}"
                   iclass="atg_store_button"/>
      </dsp:form>

    </div>

  </crs:pageContainer>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/company/thankYou.jsp#3 $$Change: 635816 $--%>
