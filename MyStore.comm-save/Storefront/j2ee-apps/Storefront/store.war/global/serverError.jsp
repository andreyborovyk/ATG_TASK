<dsp:page>

  <%--
    This page is called upon 500 HTTP error - internal server error
  --%>

  <crs:pageContainer divId="atg_store_serverErrorIntro" titleKey="" bodyClass="atg_store_internalServerError">
    <div class="atg_store_nonCatHero">
      <h2 class="title"><fmt:message key="global_serverError.title"/></h2>
    </div>
  
    <crs:messageContainer>
      <jsp:body>
        <p>
          <fmt:message key="global_serverError.serverErrorMsg"/>
          <fmt:message key="global_serverError.notifyAboutErrorMsg"/>
        </p>
        <p><crs:outMessage key="company.gadget.customer_service.byEmail"/></p>
      </jsp:body>
    </crs:messageContainer>
  </crs:pageContainer>

</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/global/serverError.jsp#3 $$Change: 635816 $--%>