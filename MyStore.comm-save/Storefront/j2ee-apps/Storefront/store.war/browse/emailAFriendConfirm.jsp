<dsp:page>

  <%--  This page displays the confirmation message that Email has been send 
        Parameters: 
        productId - ID of the Product regarding which Email is to be sent
        categoryId - ID of the parent category of the Product regarding which Email is to be sent
  --%>

  <crs:popupPageContainer divId="atg_store_emailConfirmIntro" titleKey="browse_emailAFriendConfirm.title">

    <jsp:body>
      <dsp:include page="gadgets/emailAFriendConfirm.jsp">
        <dsp:param name="productId" param="productId"/>
        <dsp:param name="categoryId" param="categoryId"/>
      </dsp:include>
    </jsp:body>

  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/browse/emailAFriendConfirm.jsp#3 $$Change: 635816 $--%>
