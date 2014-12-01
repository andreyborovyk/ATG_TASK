<dsp:page>

<%--
  This page Return Policy of the Merchant, as a popup 
--%>

  <fmt:message var="pageTitle" key="company_returnPolicyPopup.title"/>
  <crs:popupPageContainer pageTitle="${pageTitle}">
    <jsp:body>
      <dsp:include page="gadgets/returnPolicyPopupIntro.jsp">
        <dsp:param name="useCloseImage" value="false"/>
      </dsp:include>
    </jsp:body>
  </crs:popupPageContainer>

</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/company/returnPolicyPopup.jsp#3 $$Change: 635816 $ --%>
