<dsp:page>

  <%-- This page is the company's "Corporate Site" page --%>

  <crs:pageContainer divId="atg_store_company" 
                     bodyClass="atg_store_corporate atg_store_leftCol atg_store_company"
                     titleKey="">
    <crs:getMessage var="corporateSiteLink" key="common.corporateSiteLink"/>

    <div class="atg_store_nonCatHero">
      <h2 class="title">
        <fmt:message key="company.corp_site.corpSite"/>
      </h2>
    </div>
    
    <div class="atg_store_main">
      <p>
        <crs:outMessage key="company.corp_site.clickToGoToCorpSite" corporateSiteLink="${corporateSiteLink}"/>
      </p>
     </div>

     <div class="atg_store_companyNavigation aside">
      <dsp:include page="/company/gadgets/navigationPanel.jsp"/>
    </div>
  </crs:pageContainer>
</dsp:page>

<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/company/corpSite.jsp#3 $$Change: 635816 $--%>

