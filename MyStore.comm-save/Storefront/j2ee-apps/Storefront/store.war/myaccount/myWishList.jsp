<dsp:page>

  <%-- This page displays the items on the shoppers wish list
      Parameters:
      -   giftlistId - The Id of the Wish List to manage
      -   giftId (optional) - The Id of the item to remove from the specified Wish List
  --%>

  <crs:pageContainer index="false" follow="false" bodyClass="atg_store_myAccountPage atg_store_leftCol">
    <jsp:body>
      <dsp:include page="gadgets/myWishListIntro.jsp"/>
      <dsp:include page="gadgets/myAccountMenu.jsp" flush="true">
       <dsp:param name="selpage" value="WISHLIST" />
      </dsp:include>

      <div class="atg_store_main atg_store_myAccount">
        
        <dsp:include page="gadgets/myWishList.jsp"/>

      </div>
    </jsp:body>
  </crs:pageContainer>
  


</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/myaccount/myWishList.jsp#3 $$Change: 635816 $--%>
