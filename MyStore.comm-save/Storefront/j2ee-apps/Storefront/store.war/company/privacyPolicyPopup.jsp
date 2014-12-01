<%--
  This page displays Privacy Policy as a popup. 
--%>

<dsp:page>
  <fmt:message var="pageTitle" key="company_privacy.title"/>
  
  <crs:popupPageContainer pageTitle="${pageTitle}">
    <jsp:body>
            
      <fmt:message var="introText" key="company_privacyPolicyPopup.text">
        <fmt:param>
          <crs:outMessage key="common.storeName"/>
        </fmt:param>
      </fmt:message>
      
      <dsp:include page="/global/gadgets/popupPageIntro.jsp" flush="true">
        <dsp:param name="divId" value="atg_store_privacyPolicy"/>
        <dsp:param name="titleKey" value="company_privacy.title"/>
        <dsp:param name="textString" value="${introText}"/>
        <dsp:param name="useCloseImage" value="false"/>
      </dsp:include>

    </jsp:body>
  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/company/privacyPolicyPopup.jsp#3 $$Change: 635816 $ --%>
