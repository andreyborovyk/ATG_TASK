<dsp:page>

  <%--  This page presents the Email Information form to the Shopper 
        Parameters: 
        productId - ID of the Product regarding which Email is to be sent
        categoryId - ID of the parent category of the Product regarding which Email is to be sent
  --%>

  <crs:popupPageContainer divId="atg_store_emailAFriendIntro" titleKey="browse_emailAFriend.title">

    <jsp:body>
      <dsp:include page="/browse/gadgets/emailAFriend.jsp">
        <dsp:param name="productId" param="productId"/>
        <dsp:param name="categoryId" param="categoryId"/>
        <dsp:param name="container" value="/browse/emailAFriendContainer.jsp"/>
        <dsp:param name="templateUrl" value="/emailtemplates/emailAFriend.jsp"/>
      </dsp:include>
    </jsp:body>

  </crs:popupPageContainer>
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/browse/emailAFriend.jsp#3 $$Change: 635816 $--%>
