<%--
COPIED FROM Commerce Reference Store 10.0
Modified by ATG EDUCATION for use in BIZ CLASS
 --%>
<dsp:page>
  <crs:pageContainer>
    
    <jsp:attribute name="SEOTagRenderer">
      <dsp:include page="/global/gadgets/metaDetails.jsp" flush="true" />
    </jsp:attribute>
    <jsp:attribute name="bodyClass">atg_store_pageHome</jsp:attribute>
    
    <jsp:body>
      <dsp:include page="/navigation/gadgets/homePromotions.jsp" flush="true" />
      <%-- ATG EDU MODIFICATION: change homeFeaturedProducts to homeFeaturedProducts_biz --%>
      <dsp:include page="/promo/gadgets/homeFeaturedProducts_biz.jsp" flush="true" />
      <%-- END ATG EDU MODIFICATION --%>
    </jsp:body>
    
  </crs:pageContainer>
</dsp:page>
