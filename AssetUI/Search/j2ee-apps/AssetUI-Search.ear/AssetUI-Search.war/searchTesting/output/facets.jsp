<%--
  Facets section of the output.

  Included into results.jsp.

  The following request-scoped variables are expected to be set:
  
  @param formHandler     SearchTestingFormHandler instance
  @param resultCount     total number of results

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/facets.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<dspel:page>
  <dspel:getvalueof var="formHandler" param="formHandler"/>
  <dspel:getvalueof var="resultsCount" param="resultsCount"/>
  <c:set var="searchResponse" value="${formHandler.searchResponse}"/>
  <dspel:importbean var="categoryPathHelper" bean="/atg/search/web/assetmanager/CategoryPathHelper"/>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
    <c:if test="${altFieldSet}">
      <fieldset class="altGroup">
    </c:if>
    <c:if test="${not altFieldSet}">
      <fieldset>
    </c:if>
    <c:set var="altFieldSet" value="${!altFieldSet}" scope="request" />
      <legend><span>
        <fmt:message key="facets.outcomes.legend"/>
      </span></legend>
      <c:choose>
        <c:when test="${resultsCount != 0 && searchResponse.refinements != null}">
          <c:if test="${formHandler.facetSources != null && ! empty formHandler.facetSources}">
            <dspel:include page="facetsSource.jsp">
              <dspel:param name="facetSources" value="${formHandler.facetSources}"/>
            </dspel:include>
          </c:if> 
          <dspel:include page="facetsDetails.jsp">
            <dspel:param name="facets" value="${formHandler.facetInputModel.allFacets}"/>
            <dspel:param name="searchResponse" value="${searchResponse}"/>
            <dspel:param name="applyChangedFacets" value="${formHandler.applyChangedFacets}"/>
            <dspel:param name="categoryId" value="${formHandler.categoryId}"/>
          </dspel:include>
          <c:if test="${!empty categoryPathHelper.categoryDeterminationJsp}">
            <c:if test="${not empty formHandler.facetInputModel.allFacets && 
              not empty searchResponse.refinements.refinementsList && not empty searchResponse.refinements.selectConfig.mappings}">
                <dspel:include page="${categoryPathHelper.categoryDeterminationJsp}" otherContext="${categoryPathHelper.categoryJspContextRoot}">
                  <dspel:param name="searchResponse" value="${searchResponse}"/>
                </dspel:include>
            </c:if>
          </c:if>
        </c:when>
        <c:otherwise>
          <p>
            <fmt:message key="facets.noFacetResult"/>
          </p>
        </c:otherwise>
      </c:choose>
   </fieldset>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/facets.jsp#2 $$Change: 651448 $--%>
