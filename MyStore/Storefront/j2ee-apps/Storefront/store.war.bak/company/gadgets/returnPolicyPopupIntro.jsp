<dsp:page>

  <%-- This page expects the following input parameters
       useCloseImage - passed on to the popupPageIntro.jsp gadget
    --%>
  <dsp:getvalueof id="useCloseImage" param="useCloseImage"/>

  <crs:outMessage var="introText" key="company_returnPolicyPopup.text"/>

  <dsp:include page="/global/gadgets/popupPageIntro.jsp" flush="true">
    <dsp:param name="divId" value="atg_store_returnPolicy"/>
    <dsp:param name="titleKey" value="company_returnPolicyPopup.title"/>
    <dsp:param name="textString" value="${introText}"/>
    <dsp:param name="useCloseImage" value="${useCloseImage}"/>
  </dsp:include>

</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/company/gadgets/returnPolicyPopupIntro.jsp#3 $$Change: 635816 $--%>
