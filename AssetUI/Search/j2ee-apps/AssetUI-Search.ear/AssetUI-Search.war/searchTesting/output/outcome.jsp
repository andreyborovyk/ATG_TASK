<%--
  Tracked item outcome display.
  This page displays a single message based on the outcome parameter value

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param outcome    outcome (comes from item-to-outcome map provided by the custom tag in the including page).

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/outcome.jsp#2 $$Change: 651448 $
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

<c:if test="${outcome == null}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.na.stage" />
</c:if>

<c:if test="${outcome == 'accessIndex_failed'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.notFound" />
</c:if>

<c:if test="${outcome == 'retrieveCandidates_failed'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.doNotContainMatchingTerms" />
</c:if>

<c:if test="${outcome == 'checkConstraints_failed'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.excludedByConstraints" />
</c:if>

<c:if test="${outcome == 'matchingTerms_notMetRequerements'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.notMetTerm" />
</c:if>

<c:if test="${outcome == 'matchingTerms_excludedByOperators'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.excludedByOperators" />
</c:if>

<c:if test="${outcome == 'statementTerms_notMetRequerements'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.noStatements" />
</c:if>

<c:if test="${outcome == 'statementTerms_notConsidered'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.notConsidered" />
</c:if>

<c:if test="${outcome == 'statementTerms_relevanceTooLow'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.relevanceTooLow" />
</c:if>

<c:if test="${outcome == 'calculateScores_failed'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.belowThreshold" />
</c:if>

<c:if test="${outcome == 'collectResults_succeed'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.removedFromResultSet" />
</c:if>

<c:if test="${outcome == 'collectResults_failed'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.finalResultSet" />
</c:if>

<c:if test="${outcome == 'collectResults_excludedByType'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.excludedByType" />
</c:if>

<c:if test="${outcome == 'collectResults_excludedByStatement'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.excludedByStatement" />
</c:if>

<c:if test="${outcome == 'collectResults_excludedByGroupMax'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.excludedByGroupMax" />
</c:if>

<c:if test="${outcome == 'grouping_succeed'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.grouping" />
</c:if>

<c:if test="${outcome == 'withinFirstPage_succeed'}">
  <c:set var="key" value="searchTestingResultSearchProcessingSummary.outcome.finalOnFirstPage" />
</c:if>

<fmt:message key="${key}" />

</dsp:page>

<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/outcome.jsp#2 $$Change: 651448 $--%>
