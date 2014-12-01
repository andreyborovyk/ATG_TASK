<dsp:page>

  <%--
    Top-level page for showing user related as well as global promotions available on the Store
  --%>

  <crs:pageContainer divId="atg_store_promotionsIntro" titleKey="" bodyClass="atg_store_promotions">
 <div class="atg_store_nonCatHero"><h2 class="title"><fmt:message key="browse_promotions.title"/></h2></div>
    <dsp:include page="/global/gadgets/promotions.jsp">
      <dsp:param name="divId" value="atg_store_promotions"/>
    </dsp:include>

  </crs:pageContainer>

</dsp:page>

<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/promo/promotions.jsp#3 $$Change: 635816 $--%>