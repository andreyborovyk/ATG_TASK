<%--
  Human-readable outcome for a particular tracked item.

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param outcome    outcome (comes from item-to-outcome map provided by the custom tag in the including page).

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/trackedItemOutcome.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"
           uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel"
           uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"
           uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search"
           uri="http://www.atg.com/taglibs/assetui-search" %>
<%@ taglib prefix="dsp"     
           uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dsp:page>


<dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle basename="${config.resourceBundle}"/>

<dspel:getvalueof var="outcome" param="outcome" />

<c:if test="${(null == outcome)
    or ('NOT_AVAILABLE' eq outcome)}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.na.stage" />
</c:if>

<c:if test="${'ACCESS_SEARCH_INDEX_FAILED' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.notFound" />
</c:if>

<c:if test="${'RETRIEVE_ITEM_CANDIDATES_FAILED' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.doNotContainMatchingTerms" />
</c:if>

<c:if test="${'NOT_IN_CATEGORY' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.notAppearInCategory" />
</c:if>

<c:if test="${'CHECK_ITEM_CONSTRAINTS_FAILED' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.excludedByConstraints" />
</c:if>

<c:if test="${'NOT_CONSIDERED' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.notConsidered" />
</c:if>

<c:if test="${'EXCLUDED_BY_OPERATORS' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.excludedByOperators" />
</c:if>

<c:if test="${'NOT_MEET_TERM_REQUIREMENTS' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.notMetTerm" />
</c:if>

<c:if test="${'LACKS_RELEVANT_ENOUGH_STATEMENTS' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.relevanceTooLow" />
</c:if>

<c:if test="${'CALCULATE_ITEM_SCORES_FAILED' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.belowThreshold" />
</c:if>

<c:if test="${'WITHIN_FIRST_RESULT_PAGE' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.finalOnFirstPage" />
</c:if>

<c:if test="${'GROUP_ITEMS_INTO_RESULTS_SUCCEED' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.grouping" />
</c:if>

<c:if test="${'COLLECT_RESULTS_SUCCEED' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.finalResultSet"/>
</c:if>

<c:if test="${'EXCLUDED_BY_GROUP_MAXIMUM' eq outcome}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.excludedByGroupMax"/>
</c:if>

<fmt:message key="${key}" />

</dsp:page>

<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/trackedItemOutcome.jsp#2 $$Change: 651448 $--%>
