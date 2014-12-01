<dsp:page>
  <%-- This page renders the facet contents.
      Parameters - 
       - facetTrail - The facet trail traversed upto this point
       - trailSize - The trail size (number of facets traversed) upto this point
       - numResults - Number of search results
       - categoryId - The category under consideration
       - addFacet - The facet value for which refinement is requested
       - trail - The facet trail traversed so far
    --%>
  <dsp:getvalueof var="numResults" param="numResults"/>
  <dsp:getvalueof var="categoryId" param="categoryId"/>
  <dsp:getvalueof var="addFacet" param="addFacet"/>
  <dsp:getvalueof var="trail" param="trail"/>
  <dsp:getvalueof var="facetTrail" param="facetTrail"/>
  <dsp:getvalueof var="trailSize" param="trailSize"/>
  <dsp:getvalueof var="removeFacet" param="removeFacet"/>



  <script type="text/javascript">
    //set the facet search target for live updating of a div
      setFacetTarget("searchResults");

      var jsonFacets = <%@include file="/facetjson/facetData.jsp" %>;
    
      var content = {
          removeFacet:'${removeFacet}',
          trailSize:'${trailSize}',
          facetTrail:'${facetTrail}',
          addFacet:'${addFacet}',
          categoryId:'${categoryId}',
          numResults:'${numResults}',
          question:'${question}'
      };

      // set the globle variable nRedirect
      // it indicate that whether need to redirect the whole page to the urlFacet location, instead of updating
      setRedirect(0);

      dojo.addOnLoad(function() {
          handleFacetLoad(jsonFacets, '${categoryId}');
      });
  </script>

  <dsp:getvalueof var="facetHolders" bean="FacetSearchTools.facets"/>
  <c:if test="${(not empty facetHolders) || (not empty param.facetOrder)}">
    <div id="atg_store_facets">
      <dsp:include page="/facet/gadgets/facetNavigationRenderer.jsp" />
    </div>
  </c:if>

   
  
</dsp:page>
<%-- @version $Id: //edu/ILT-Courses/main/COMM/StudentFiles/COM/setup/copy-files/apps/MyStore/Storefront/j2ee-apps/Storefront/store.war/facet/gadgets/facetPanelContainer.jsp#3 $$Change: 635816 $--%>
