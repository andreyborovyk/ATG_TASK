<%--
  Search processing summary section of the tracked items output.

  Included into results.jsp.

  The following parameters are passed into the page:

  @param searchResponse         search response
  @param subitemsValid          true if we have grouped results (skus grouped by products)

  The following request-scoped variables are expected to be set:
  
  @param formHandler     SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/searchProcessingSummary.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<dspel:page>
<dspel:getvalueof var="searchResponse" param="searchResponse"/>
<dspel:getvalueof var="subitemsValid" param="subitemsValid"/>
<dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle basename="${config.resourceBundle}"/>
<c:if test="${empty searchResponse.diagnostics.trace.items}">
  <p><fmt:message key="searchTestingResultSearchProcessingSummary.noItems.text"/></p>
</c:if>
<div class="searchTestingTableHeading "><fmt:message key="searchTestingResultSearchProcessingSummary.title"/></div>
<table class="atg_dataTable atg_smerch_summaryTable">
<tr>
  <th class="atg_smerch_summaryTitle">
    <fmt:message key="searchTestingResultSearchProcessingSummary.stage.title"/>
  </th>
  <th class="atg_smerch_summaryTitle">
    <fmt:message key="searchTestingResultSearchProcessingSummary.itemRemaining.title"/>
  </th>
  <c:if test="${subitemsValid}">
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultSearchProcessingSummary.groupedResults.title"/>
    </th>
  </c:if>
  <c:if test="${!empty searchResponse.diagnostics.trace.items}">
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultSearchProcessingSummary.outcomes.title"/>
    </th>
  </c:if>
</tr>
  <%-- Access Search Index section --%>
<tr>
  <td class="atg_searchProcessStage">
    <dl>
      <dt>
        <fmt:message key="searchTestingResultSearchProcessingSummary.accessSearchIndex"/>
      </dt>
      <dd>
        <fmt:message key="searchTestingResultSearchProcessingSummary.startWithAll"/>
      </dd>
    </dl>
  </td>
  <td>
    <c:out value="${searchResponse.diagnostics.summary.totalCount}"/>
  </td>
  <c:if test="${subitemsValid}">
    <td></td>
  </c:if>
  <c:if test="${!empty searchResponse.diagnostics.trace.items}">
    <td class="atg_trackedItemOutcomes">
      <c:choose>
        <c:when test="${stagesOutcomes['accessIndex'] != null}">
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.notFound"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['accessIndex']['failed']}"/>
          </dspel:include>
        </c:when>
        <c:otherwise>
          <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems"/>
        </c:otherwise>
      </c:choose>
    </td>
  </c:if>
</tr>
  <%-- Retrieve Items Candidates section --%>
<tr class="atg_altRow">
  <td class="atg_searchProcessStage">
    <dl>
      <dt>
        <fmt:message key="searchTestingResultSearchProcessingSummary.retrieveCandidates"/>
      </dt>
      <dd>
        <fmt:message key="searchTestingResultSearchProcessingSummary.atListOne"/>
      </dd>
    </dl>
  </td>
  <td>
    <c:out value="${searchResponse.diagnostics.summary.retrievalCount}"/>
  </td>
  <c:if test="${subitemsValid}">
    <td></td>
  </c:if>
  <c:if test="${!empty searchResponse.diagnostics.trace.items}">
    <td class="atg_trackedItemOutcomes">
        <%--todo check message!--%>
      <c:choose>
        <c:when test="${stagesOutcomes['retrieveCandidates'] != null}">
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.doNotContainMatchingTerms"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['retrieveCandidates']['failed']}"/>
          </dspel:include>
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.notAppearInCategory"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['retrieveCandidates']['notInCategory']}"/>
          </dspel:include>
        </c:when>
        <c:otherwise>
          <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems"/>
        </c:otherwise>
      </c:choose>
    </td>
  </c:if>
</tr>
  <%-- Check Items Constraints section --%>
<tr>
  <td class="atg_searchProcessStage">
    <dl>
      <dt>
        <fmt:message key="searchTestingResultSearchProcessingSummary.checkConstraints"/>
      </dt>
      <dd>
        <fmt:message key="searchTestingResultSearchProcessingSummary.meetAllConstraints"/>
      </dd>
    </dl>
  </td>
  <td>
    <c:out value="${searchResponse.diagnostics.summary.constraintCount}"/>
  </td>
  <c:if test="${subitemsValid}">
    <td></td>
  </c:if>
  <c:if test="${!empty searchResponse.diagnostics.trace.items}">
    <td class="atg_trackedItemOutcomes">
      <c:choose>
        <c:when test="${stagesOutcomes['checkConstraints'] != null}">
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.excludedByConstraints"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['checkConstraints']['failed']}"/>
          </dspel:include>
        </c:when>
        <c:otherwise>
          <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems"/>
        </c:otherwise>
      </c:choose>
    </td>
  </c:if>
</tr>
  <%-- Evaluate Matching Terms section --%>
<tr class="atg_altRow">
  <td class="atg_searchProcessStage">
    <dl>
      <dt>
        <fmt:message key="searchTestingResultSearchProcessingSummary.evaluateTerms"/>
      </dt>
      <dd>
        <fmt:message key="searchTestingResultSearchProcessingSummary.meetRequirements"/>
      </dd>
    </dl>
  </td>
  <td>
    <c:out value="${searchResponse.diagnostics.summary.itemCount}"/>
  </td>
  <c:if test="${subitemsValid}">
    <td></td>
  </c:if>
  <c:if test="${!empty searchResponse.diagnostics.trace.items}">
    <td class="atg_trackedItemOutcomes">
      <%-- When search with selected category some stages could be N/A due to absence of necessary info in search responce
          see bug 150670  --%>
      <c:choose>
        <c:when test="${searchResponse.question != null}">
          <c:choose>
            <c:when test="${stagesOutcomes['matchingTerms'] != null}">
              <dspel:include page="stageOutcome.jsp">
                <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.notMetTerm"/>
                <dspel:param name="stageOutcome" value="${stagesOutcomes['matchingTerms']['notMetRequerements']}"/>
              </dspel:include>
              <dspel:include page="stageOutcome.jsp">
                <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.excludedByOperators"/>
                <dspel:param name="stageOutcome" value="${stagesOutcomes['matchingTerms']['excludedByOperators']}"/>
              </dspel:include>
            </c:when>
            <c:otherwise>
              <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems"/>
            </c:otherwise>
          </c:choose>
        </c:when>
        <c:otherwise>
          <fmt:message key="searchTestingResultSearchProcessingSummary.na.stage"/>
        </c:otherwise>
      </c:choose>
    </td>
  </c:if>
</tr>
  <%-- Evaluate Statement Terms section --%>
<tr>
  <td class="atg_searchProcessStage">
    <dl>
      <dt>
        <fmt:message key="searchTestingResultSearchProcessingSummary.evaluateStatements"/>
      </dt>
      <dd>
        <fmt:message key="searchTestingResultSearchProcessingSummary.itemsRelevant"/>
      </dd>
    </dl>
  </td>
  <td>
    <c:out value="${searchResponse.diagnostics.summary.statementCount}"/>
  </td>
  <c:if test="${subitemsValid}">
    <td></td>
  </c:if>
  <c:if test="${!empty searchResponse.diagnostics.trace.items}">
    <td class="atg_trackedItemOutcomes">
        <%-- When search with selected category some stages could be N/A due to absence of necessary info in search responce
          see bug 150670  --%>
      <c:choose>
        <c:when test="${searchResponse.question != null}">
          <c:choose>
            <c:when test="${stagesOutcomes['statementTerms'] != null}">
              <dspel:include page="stageOutcome.jsp">
                <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.noStatements"/>
                <dspel:param name="stageOutcome" value="${stagesOutcomes['statementTerms']['notMetRequerements']}"/>
              </dspel:include>
              <dspel:include page="stageOutcome.jsp">
                <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.notConsidered"/>
                <dspel:param name="stageOutcome" value="${stagesOutcomes['statementTerms']['notConsidered']}"/>
              </dspel:include>
              <dspel:include page="stageOutcome.jsp">
                <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.relevanceTooLow"/>
                <dspel:param name="stageOutcome" value="${stagesOutcomes['statementTerms']['relevanceTooLow']}"/>
              </dspel:include>
            </c:when>
            <c:otherwise>
              <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems"/>
            </c:otherwise>
          </c:choose>
        </c:when>
        <c:otherwise>
          <fmt:message key="searchTestingResultSearchProcessingSummary.na.stage"/>
        </c:otherwise>
      </c:choose>
    </td>
  </c:if>
</tr>
  <%-- Calculate Item Scores section --%>
<tr class="atg_altRow">
  <td class="atg_searchProcessStage">
    <dl>
      <dt>
        <fmt:message key="searchTestingResultSearchProcessingSummary.calcScores"/>
      </dt>
      <dd>
        <fmt:message key="searchTestingResultSearchProcessingSummary.relevantEnough"/>
      </dd>
    </dl>
  </td>
  <td>
    <c:out value="${searchResponse.diagnostics.summary.relevantCount}"/>
  </td>
  <c:if test="${subitemsValid}">
    <td></td>
  </c:if>
  <c:if test="${!empty searchResponse.diagnostics.trace.items}">
    <td class="atg_trackedItemOutcomes">
      <c:choose>
        <c:when test="${empty searchResponse.question and (empty formHandler.categoryId or empty formHandler.searchConfigId)}">
          <fmt:message key="searchTestingResultSearchProcessingSummary.na.stage"/>
        </c:when>
        <c:when test="${stagesOutcomes['calculateScores'] != null}">
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.belowThreshold"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['calculateScores']['failed']}"/>
          </dspel:include>
        </c:when>
        <c:otherwise>
          <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems"/>
        </c:otherwise>
      </c:choose>
    </td>
  </c:if>
</tr>
  <%-- Calculate Item Scores section --%>
<tr>
  <td class="atg_searchProcessStage">
    <dl>
      <dt>
        <fmt:message key="searchTestingResultSearchProcessingSummary.collectResults"/>
      </dt>
      <dd>
        <fmt:message key="searchTestingResultSearchProcessingSummary.partOfResult"/>
      </dd>
    </dl>
  </td>
  <td>
    <c:out value="${searchResponse.diagnostics.summary.resultCount}"/>
  </td>
  <c:if test="${subitemsValid}">
    <td></td>
  </c:if>
  <c:if test="${!empty searchResponse.diagnostics.trace.items}">
    <td class="atg_trackedItemOutcomes">
      <c:choose>
        <c:when test="${stagesOutcomes['collectResults'] != null}">
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.removedFromResultSet"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['collectResults']['failed']}"/>
          </dspel:include>
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.excludedByType"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['collectResults']['excludedByType']}"/>
          </dspel:include>
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.excludedByStatement"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['collectResults']['excludedByStatement']}"/>
          </dspel:include>
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.excludedByGroupMax"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['collectResults']['excludedByGroup']}"/>
          </dspel:include>
        </c:when>
        <c:otherwise>
          <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems"/>
        </c:otherwise>
      </c:choose>
    </td>
  </c:if>
</tr>
  <%-- Group Items into Results section --%>
<c:if test="${subitemsValid}">
  <tr class="atg_altRow">
    <td class="atg_searchProcessStage">
      <dl>
        <dt>
          <fmt:message key="searchTestingResultSearchProcessingSummary.groupItems"/>
        </dt>
      </dl>
    </td>
    <td>
      <c:out value="${searchResponse.diagnostics.summary.resultCount}"/>
    </td>
    <td class="groupCount">
      <c:out value="${searchResponse.groupCount}"/>
    </td>
    <c:if test="${!empty searchResponse.diagnostics.trace.items}">
      <td class="atg_trackedItemOutcomes">
        <c:choose>
          <c:when test="${stagesOutcomes['grouping'] != null}">
            <dspel:include page="stageOutcome.jsp">
              <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.grouping"/>
              <dspel:param name="stageOutcome" value="${stagesOutcomes['grouping']['succeed']}"/>
            </dspel:include>
          </c:when>
          <c:otherwise>
            <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems"/>
          </c:otherwise>
        </c:choose>
      </td>
    </c:if>
  </tr>
</c:if>
  <%-- Within First Page of Results section --%>
  <c:if test="${!subitemsValid}">
    <tr class="atg_altRow">
  </c:if>
  <c:if test="${subitemsValid}">
    <tr>
  </c:if>
  <td class="atg_searchProcessStage">
    <dl>
      <dt>
        <fmt:message key="searchTestingResultSearchProcessingSummary.withinFirstPage"/>
      </dt>
      <dd>
        <fmt:message key="searchTestingResultSearchProcessingSummary.onFirstPage"/>
      </dd>
    </dl>
  </td>
  <c:if test="${subitemsValid}">
    <td></td>
  </c:if>
  <c:set var="resultCount" value="${searchResponse.diagnostics.summary.resultCount}" />
  <c:if test="${subitemsValid}">
    <c:set var="resultCount" value="${searchResponse.groupCount}" />
  </c:if>
  <td>
    <c:choose>
      <c:when test="${searchResponse.pageSize < resultCount}">
         <c:out value="${searchResponse.pageSize}"/>
      </c:when>
      <c:otherwise>
         <c:out value="${resultCount}"/>
      </c:otherwise>
    </c:choose>
  </td>
  <c:if test="${!empty searchResponse.diagnostics.trace.items}">
    <td class="atg_trackedItemOutcomes">
      <c:choose>
        <c:when test="${stagesOutcomes['withinFirstPage'] != null}">
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.finalOnFirstPage"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['withinFirstPage']['succeed']}"/>
          </dspel:include>
          <dspel:include page="stageOutcome.jsp">
            <dspel:param name="outcomeDescriptionKey" value="searchTestingResultSearchProcessingSummary.outcome.finalResultSet"/>
            <dspel:param name="stageOutcome" value="${stagesOutcomes['withinFirstPage']['failed']}"/>
          </dspel:include>
        </c:when>
        <c:otherwise>
          <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems"/>
        </c:otherwise>
      </c:choose>
    </td>
  </c:if>
</tr>
</table>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/searchProcessingSummary.jsp#2 $$Change: 651448 $--%>
