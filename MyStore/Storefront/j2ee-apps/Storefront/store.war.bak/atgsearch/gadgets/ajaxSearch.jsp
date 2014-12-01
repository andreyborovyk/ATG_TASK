<dsp:page>

 <dsp:importbean bean="/atg/dynamo/droplet/ForEach" />
<dsp:importbean bean="/atg/dynamo/droplet/ComponentExists"/>
<dsp:importbean bean="/atg/commerce/search/catalog/QueryFormHandler"/>
<dsp:importbean bean="/atg/search/repository/FacetSearchTools"/>
<dsp:importbean bean="/atg/userprofiling/Profile" />
<dsp:importbean bean="/atg/commerce/search/refinement/CommerceFacetTrailDroplet" />
<dsp:importbean bean="/atg/commerce/catalog/CategoryLookup" />
<dsp:importbean bean="/atg/commerce/search/StoreBrowseConstraints"/>
  <dsp:importbean bean="/atg/dynamo/droplet/IsEmpty"/>
  <dsp:importbean bean="/atg/registry/RepositoryTargeters/RefinementRepository/GlobalCategoryFacet"/>
  <dsp:importbean bean="/atg/search/repository/FacetSearchTools"/>
  <dsp:importbean bean="/atg/targeting/TargetingFirst"/>

 <dsp:getvalueof var="categoryId" param="categoryId"/>
  
  <c:choose>
    <c:when test="${categoryId != null && categoryId !=''}">
      <%@include file="/atgsearch/gadgets/categorySearch.jspf" %>
    </c:when>
    <c:otherwise>
      <%@include file="/atgsearch/gadgets/querySearch.jspf" %>
    </c:otherwise>
  </c:choose>

  <%@include file="/facetjson/facetData.jsp" %>

</dsp:page>

<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/atgsearch/gadgets/ajaxSearch.jsp#3 $$Change: 635816 $ --%>
