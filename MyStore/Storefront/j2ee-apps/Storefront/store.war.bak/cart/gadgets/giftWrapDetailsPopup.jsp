<dsp:page>

  <%--
    This page renders the gift wrap details as a popup
  --%>

  <%-- Get the supplied gift wrap price --%>
  <dsp:getvalueof var="price" param="giftWrapPrice"/>
  
  <%-- Apply the gift wrap price parameter to the resource string --%>
  <c:set var="giftWrapText">
	  <crs:outMessage key="cart_giftWrapPopup.text" price="${price}"/>
  </c:set>

  <%-- Supply the gift wrap text details to the popup page container --%>
  <crs:popupPageContainer divId="atg_store_giftWrapDetails"
                          titleKey="cart_giftWrapPopup.title"
                          textString="${giftWrapText}"
                          useCloseImage="false">
  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/cart/gadgets/giftWrapDetailsPopup.jsp#3 $$Change: 635816 $ --%>
