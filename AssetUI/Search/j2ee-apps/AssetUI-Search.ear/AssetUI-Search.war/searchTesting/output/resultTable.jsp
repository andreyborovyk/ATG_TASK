<%--
  Result table page.
  
  Includes result table header and row as configured in formHandler.pageConfigMap, includes paging.

  Included into results.jsp

  The following parameters are passed into the page:

  @param searchResponse         search response
  @param debugExclusion         true if excluded items are shown
  @param disablePositioning     true if positioning rules are disabled
  @param resultMode             switch between result modes (sub-items/no sub-items/ranking calculation)
  @param exclusionRulesCount    number of exclusion rules
  @param positioningRulesCount  number of positioning rules
  @param resultCount            number of results

  The following request-scoped variables are expected to be set:

  @param formHandler            SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/resultTable.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="propertyManager" bean="/atg/search/repository/SearchTestingPropertyManager"/>

  <c:set value="/atg/search/web/assetmanager/paging/SearchTestingResultsPaging" var="pagingSearchComponentPath"/>
  <dspel:importbean bean="${pagingSearchComponentPath}" var="pagingSearchComponent"/>

  <dspel:getvalueof var="searchResponse" param="searchResponse"/>
  <dspel:getvalueof var="debugExclusion" param="debugExclusion"/>
  <dspel:getvalueof var="disablePositioning" param="disablePositioning"/>
  <dspel:getvalueof var="resultMode" param="resultMode"/>
  <dspel:getvalueof var="exclusionRulesCount" param="exclusionRulesCount"/>
  <dspel:getvalueof var="positioningRulesCount" param="positioningRulesCount"/>
  <dspel:getvalueof var="resultsCount" param="resultsCount"/>

  <c:set var="resultItems"
      value=""/>
  <c:choose>
    <c:when test="${formHandler.subitemsValid}">
      <c:set var="resultItems"
          value="${searchResponse.resultsWithGrouping}"/>
    </c:when>
    <c:otherwise>
      <c:set var="resultItems"
          value="${searchResponse.results}"/>
    </c:otherwise>
  </c:choose>

  <c:set var="secondaryPage"
      value=""/>
  <c:choose>
    <c:when test="${propertyManager.subItemsResultsEnumValue eq resultMode}">
      <c:set var="secondaryPage" value="subTable.jsp"/>
    </c:when>
    <c:when test="${propertyManager.rankingResultsEnumValue eq resultMode}">
      <c:set var="secondaryPage" value="scoreCalculation.jsp"/>
    </c:when>
    <c:otherwise>
      <c:set var="secondaryPage" value="${formHandler.pageConfigMap.resultTableSecondaryPropertiesPage}"/>
      <c:set var="secondaryContext" value="${formHandler.pageConfigMap.resultTableSecondaryPropertiesContext}"/>
    </c:otherwise>
  </c:choose>

  <c:if test="${empty secondaryContext}">
    <c:set var="secondaryContext" value="AssetUI-Search" />
  </c:if>

  <c:if test="${altFieldSet}">
    <fieldset class="altGroup">
  </c:if>
  <c:if test="${not altFieldSet}">
    <fieldset>
  </c:if>
  <c:set var="altFieldSet" value="${!altFieldSet}" scope="request" />

    <legend><span>
      <fmt:message key="searchTestingResultBaseTable.resultPrompt"/>
    </span></legend>
    <%-- Show/Apply excluded results and position rules links --%>
    <c:choose>
      <c:when test="${exclusionRulesCount > 0 || positioningRulesCount > 0}">
        <p class="atg_tableViewSwitch">
          <c:if test="${positioningRulesCount > 0}">
            <c:choose>
              <c:when test="${disablePositioning}">
                <a class="buttonSmall" href="#" onclick="changeRuleMode(false);">
                  <fmt:message key="searchTestResultPositioningRules.showResultsWith.link"/>
                </a>
              </c:when>
              <c:otherwise>
                <a class="buttonSmall" href="#" onclick="changeRuleMode(true);">
                  <fmt:message key="searchTestResultPositioningRules.showResultsWithout.link"/>
                </a>
              </c:otherwise>
            </c:choose>
          </c:if>
          &nbsp;          
          <c:if test="${exclusionRulesCount > 0}">
            <c:choose>
              <c:when test="${debugExclusion}">
                <a class="buttonSmall" href="#" onclick="changeDebugExclusion(false);">
                  <fmt:message key="searchTestResultExclusionRules.applyRules.link"/>
                </a>
              </c:when>
              <c:otherwise>
                <a class="buttonSmall" href="#" onclick="changeDebugExclusion(true);">
                  <fmt:message key="searchTestResultExclusionRules.showExcludedResults.link"/>
                </a>
              </c:otherwise>
            </c:choose>
          </c:if>          
        </p>
      </c:when>
      <c:otherwise>
        <p/>
      </c:otherwise>
    </c:choose>

    <c:choose>
      <c:when test="${resultsCount > 0}">
        <%--paging--%>
        <a name="topPagingAnchor" id="topPagingAnchor"></a>
        <div class="atg_pagination atg_tablePagination atg_topPagination">
          <dspel:include page="/searchTesting/paging/tablePaging.jsp">
            <dspel:param name="pagingSearchComponentPath" value="${pagingSearchComponentPath}"/>
            <dspel:param name="pagingAnchorName" value="topPagingAnchor"/>
            <dspel:param name="pagingAlign" value="top"/>
          </dspel:include>
        </div>

        <%-- Check if results are of different languages. --%>
        <c:forEach items="${resultItems}" var="resultItem" end="0">
          <c:choose>
            <c:when test="${formHandler.subitemsValid}">
              <c:set var="currentItem" value="${resultItem[0]}"/>
            </c:when>
            <c:otherwise>
              <c:set var="currentItem" value="${resultItem}"/>
            </c:otherwise>
          </c:choose>
        </c:forEach>
        <!-- Expanded Results -->
        <table class="atg_dataTable atg_smerch_summaryTable">
          <dspel:getvalueof bean="${formHandlerPath}.value.categoryId" var="categoryId" />
          <c:if test="${!formHandler.subitemsValid && searchResponse.diagnostics != null && searchResponse.diagnostics.trace != null && searchResponse.diagnostics.trace.items != null}">
            <assetui-search:getTracedResults resultItems="${resultItems}" tracedItems="${searchResponse.diagnostics.trace.items}" var="tracedResults"/>
          </c:if>
          <dspel:include page="${formHandler.pageConfigMap.resultTableHeaderPage}"
                         otherContext="${formHandler.pageConfigMap.resultTableHeaderContext}">
            <dspel:param name="multiLanguage" value="${formHandler.multiTargetLanguages or !empty categoryId}"/>
            <dspel:param name="areThereTrackedItems" value="${! empty tracedResults}"/>
          </dspel:include>
          <c:forEach items="${resultItems}" var="resultItem" varStatus="status">
            <dspel:include page="${formHandler.pageConfigMap.resultTableRowPage}"
                           otherContext="${formHandler.pageConfigMap.resultTableRowContext}">
              <dspel:param name="disablePositioning" value="${disablePositioning}"/>
              <dspel:param name="debugExclusion" value="${debugExclusion}"/>
              <dspel:param name="resultItem" value="${resultItem}"/>
              <dspel:param name="resultMode" value="${resultMode}"/>
              <dspel:param name="index" value="${status.index}"/>
              <dspel:param name="firstOutputedItemNumber" value="${pagingSearchComponent.firstItemNumber}"/>
              <dspel:param name="secondaryPage" value="${secondaryPage}"/>
              <dspel:param name="secondaryContext" value="${secondaryContext}"/>
              <dspel:param name="multiLanguage" value="${formHandler.multiTargetLanguages or !empty categoryId}"/>
              <dspel:param name="tracedResults" value="${tracedResults}"/>
            </dspel:include>
          </c:forEach>
        </table>

        <%--paging--%>
        <a name="bottomPagingAnchor" id="bottomPagingAnchor"></a>
        <div class="atg_pagination atg_tablePagination atg_topPagination">
          <dspel:include page="/searchTesting/paging/tablePaging.jsp">
            <dspel:param name="pagingSearchComponentPath" value="${pagingSearchComponentPath}"/>
            <dspel:param name="pagingAnchorName" value="bottomPagingAnchor"/>
            <dspel:param name="pagingAlign" value="bottom"/>
          </dspel:include>
        </div>

      </c:when>
      <c:otherwise>
        <p class="noResults">
          <fmt:message key="searchTestingResult.noResults"/>
        </p>
      </c:otherwise>
    </c:choose>

  </fieldset>

<script type="text/javascript">
  function changeRuleMode(mode) {
    document.getElementById('searchTestingEditorDisablePositioning').value = mode;
    document.getElementById('debugRulesSearchButton').click();
  }
</script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/resultTable.jsp#2 $$Change: 651448 $--%>
