<dsp:page>
  
  <dsp:importbean bean="/atg/store/StoreConfiguration" />
  <dsp:getvalueof var="atgSearchInstalled" bean="StoreConfiguration.atgSearchInstalled" />
  <div id="atg_store_search">
  <c:choose>
    <c:when test="${atgSearchInstalled == 'true'}">
      <dsp:include page="/atgsearch/gadgets/atgSearch.jsp" />
    </c:when>
    <c:otherwise>
      <dsp:include page="/search/gadgets/simpleSearch.jsp" />
    </c:otherwise>
  </c:choose>
  </div>
  
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/navigation/gadgets/search.jsp#3 $$Change: 635816 $ --%>
