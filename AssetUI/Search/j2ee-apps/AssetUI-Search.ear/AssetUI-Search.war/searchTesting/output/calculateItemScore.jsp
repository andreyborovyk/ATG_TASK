<%--
  This page provides the information about 'the item scores calculation'
  for the result per-stage tracking information.

  Included into trackedItemDetailsBaseTable.jsp.
  Includes scoreCalculation.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response. 

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/calculateItemScore.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"
    uri="http://java.sun.com/jstl/core"%>
<%@ taglib prefix="dspel"
    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"%>
<%@ taglib prefix="fmt"
    uri="http://java.sun.com/jstl/fmt"%>
<%@ taglib prefix="assetui-search"
    uri="http://www.atg.com/taglibs/assetui-search"%>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="item" param="item"/>

  <c:set var="minScore"
      value="${item.relevancy.threshold.setting}"/>
  <c:if test="${empty minScore}">
    <fmt:formatNumber var="minScore"
        type="number"
        minFractionDigits="1"
        maxFractionDigits="1"
        value="${item.resultCollection.resultListControls.resultListControls['minScore'].setting / 10}"/>
  </c:if>
  <c:choose>
    <c:when test="${'fail' eq item.summary.relevance}">
      <p class="atg_excludedConstraint">
        <fmt:message key="searchTestingCalculateItemScore.excludedConstraintTitle">
          <fmt:param>
            <dspel:include page="propertyValueAsText.jsp">
              <dspel:param name="propertyValue" value="${minScore}"/>
            </dspel:include>
          </fmt:param>
        </fmt:message>
      </p>
    </c:when>
    <c:otherwise>
      <p class="atg_includedConstraint">
        <fmt:message key="searchTestingCalculateItemScore.includedConstraintTitle"/>
      </p>
    </c:otherwise>
  </c:choose>

  <dspel:include page="scoreCalculation.jsp">
    <dspel:param name="rankConfig" value="${item.relevancy.rankConfig}"/>
    <dspel:param name="minScore" value="${minScore}"/>
    <dspel:param name="finalScore" value="${item.relevance}" />
    <dspel:param name="movements" value="${item.relevancy.movements}"/>
  </dspel:include>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/calculateItemScore.jsp#2 $$Change: 651448 $--%>
