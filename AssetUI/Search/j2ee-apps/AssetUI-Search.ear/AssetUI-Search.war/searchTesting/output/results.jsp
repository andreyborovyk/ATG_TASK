<%--
  The base result page of the Search Testing.

  Depending on the results which should be displayed, this page includes:
    - searchConfig.jsp (Search Configuration Details)
    - queryTerms.jsp (Term Details)
    - queryRules.jsp (Query Rule Details)
    - searchProcessingSummary.jsp (Search Processing Summary Table)
    - trackedItemDetailsBaseTable.jsp (Tracked Item Details)
    - resultTable.jsp (Search Result Items)
    - facets.jsp (Facet Details)

  The following request-scoped variables are expected to be set:

  @param formHandler            SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/results.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="searchFormHandlerPath" value="/atg/search/formhandlers/QueryFormHandler"/>
  <dspel:importbean bean="${searchFormHandlerPath}" var="searchFormHandler"/>

  <dspel:importbean var="propertyManager" bean="/atg/search/repository/SearchTestingPropertyManager"/>

  <p/>

  <c:set var="searchResponse" value="${formHandler.searchResponse}" />
  <c:set var="isTraceEmpty" value="${empty searchResponse.diagnostics.trace.items}" />

  <c:set var="displayTrackedDetails" value="${false}"/>
  <c:set var="displayTermDetails" value="${false}"/>

  <c:forEach items="${formHandler.value['TESTTYPES']}" var="testType">
    <c:choose>
      <c:when test="${propertyManager.trackingTestTypesEnumValue eq testType}">
        <c:set var="displayTrackedDetails" value="${true}"/>
      </c:when>
      <c:when test="${propertyManager.termsTestTypesEnumValue eq testType}">
        <c:set var="displayTermDetails" value="${true}"/>
      </c:when>
      <c:when test="${propertyManager.rulesTestTypesEnumValue eq testType}">
        <c:set var="displayQueryRulesDetails" value="${true}"/>
      </c:when>
      <c:when test="${propertyManager.facetTestTypesEnumValue eq testType}">
        <c:set var="displayFacetDetails" value="${true}"/>
      </c:when>
      <c:when test="${propertyManager.rankingTestTypesEnumValue eq testType}">
        <c:set var="displayRankingDetails" value="${true}"/>
      </c:when>
    </c:choose>
  </c:forEach>

  <c:set var="altFieldSet" value="${false}" scope="request" />

  <c:if test="${displayRankingDetails}">
    <dspel:include page="searchConfig.jsp" />
  </c:if>

  <%-- count exclusion rules--%>
  <assetui-search:getExclusionRules queryRules="${searchResponse.queryRule}" ruleAppendix="${searchResponse.ruleAppendix}"
                                      var="exclusionRules"/>
  <assetui-search:getContainerSize container="${exclusionRules}" var="exclusionRulesCount"/>

  <%-- count position rules--%>
  <assetui-search:getPositioningRules queryActions="${searchResponse.queryAction}" ruleAppendix="${searchResponse.ruleAppendix}"
                                        var="positioningRules"/>
  <assetui-search:getContainerSize container="${positioningRules}" var="positioningRulesCount"/>

  <%-- choose display name property name for content item page --%>
  <c:choose>
    <c:when test="${formHandler.subitemsValid}">
      <c:set var="namePropertyDisplayName" value="displayname"/>
    </c:when>
    <c:otherwise>
      <c:set var="namePropertyDisplayName" value="childskus.displayname"/>
    </c:otherwise>
  </c:choose>

  <c:if test="${displayTermDetails}">
    <dspel:include page="queryTerms.jsp">
      <dspel:param name="searchResponse" value="${searchResponse}"/>
    </dspel:include>
  </c:if>

  <c:if test="${displayQueryRulesDetails}">
    <dspel:include page="queryRules.jsp">
      <dspel:param name="formHandler" value="${formHandler}"/>
      <dspel:param name="searchResponse" value="${searchResponse}"/>
      <dspel:param name="exclusionRulesCount" value="${exclusionRulesCount}" />
      <dspel:param name="exclusionRules" value="${exclusionRules}" />
      <dspel:param name="positioningRulesCount" value="${positioningRulesCount}" />
      <dspel:param name="positioningRules" value="${positioningRules}" />
    </dspel:include>
  </c:if>

  <c:if test="${displayTrackedDetails or !isTraceEmpty}">

    <c:if test="${altFieldSet}">
      <fieldset class="altGroup">
    </c:if>
    <c:if test="${not altFieldSet}">
      <fieldset>
    </c:if>
    <c:set var="altFieldSet" value="${!altFieldSet}" scope="request" />

      <legend><span>
        <fmt:message key="searchTestingResultSearchProcessingSummary.outcomes.legend"/>
      </span></legend>
      <br/>
      <assetui-search:getTrackedItemOutcomes diagnostics="${searchResponse.diagnostics}" var="stagesOutcomes" varByItem="itemsToOutcomes" scope="request" />
      <c:if test="${displayTrackedDetails}">
        <dspel:include page="searchProcessingSummary.jsp">
          <dspel:param name="searchResponse" value="${searchResponse}"/>
          <dspel:param name="subitemsValid" value="${formHandler.subitemsValid}"/>
        </dspel:include>
      </c:if>
      <c:if test="${!isTraceEmpty}">
        <dspel:include page="trackedItemDetailsBaseTable.jsp">
          <dspel:param name="searchResponse" value="${searchResponse}" />
          <dspel:param name="displayTrackedDetails" value="${displayTrackedDetails}" />
        </dspel:include>
      </c:if>
    </fieldset>
  </c:if>

  <assetui-search:getContainerSize container="${searchResponse.results}" var="resultsCount"/>
  <c:if test="${!(formHandler.value['RESULTS'] eq propertyManager.noneResultsEnumValue)}">
    <dspel:include page="resultTable.jsp">
      <%--two next params necessary for "Show/Hide Excluded results"--%>
      <dspel:param name="debugExclusion" value="${formHandler.debugExclusion}"/>
      <dspel:param name="disablePositioning" value="${formHandler.disablePositioning}"/>
      <dspel:param name="searchResponse" value="${searchResponse}"/>
      <dspel:param name="resultMode" value="${formHandler.value['RESULTS']}"/>
      <dspel:param name="exclusionRulesCount" value="${exclusionRulesCount}" />
      <dspel:param name="positioningRulesCount" value="${positioningRulesCount}" />
      <dspel:param name="resultsCount" value="${resultsCount}" />
    </dspel:include>
  </c:if>

  <c:if test="${displayFacetDetails}">
    <dspel:include page="facets.jsp">
      <dspel:param name="resultsCount" value="${resultsCount}" />
      <dspel:param name="formHandler" value="${formHandler}"/>
    </dspel:include>
  </c:if>

  <script type="text/javascript">
    function changeDebugExclusion(mode) {
      document.getElementById('inputsDebugExclusion').value = mode;
      document.getElementById('debugRulesSearchButton').click();
    }
  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/results.jsp#2 $$Change: 651448 $--%>
