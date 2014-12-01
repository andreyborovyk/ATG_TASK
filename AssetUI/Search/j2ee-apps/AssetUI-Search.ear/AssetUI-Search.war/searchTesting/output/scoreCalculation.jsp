<%--
  Score calculation table for the single ressult item.

  Includes scoreRow.jsp for each property that was weighed to get the final score.

  Included into calculateItemScore.jsp and can be included into resultTableRow.jsp as a secondary page.

  The following parameters are passed into the page:

  @param rankConfig             rank configuration that was executed during the search
  @param minScore               minimal score (threshold setting)
  @param finalScore             final score for the item
  @param movements              movements of the inspected item (when included in calculateItemScore.jsp)

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/scoreCalculation.jsp#2 $$Change: 651448 $
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

  <dspel:getvalueof var="rankConfig" param="rankConfig"/>
  <dspel:getvalueof var="minScore" param="minScore" />
  <dspel:getvalueof var="finalScore" param="finalScore" />
  <dspel:getvalueof var="movements" param="movements" />

  <table class="atg_dataTable atg_smerch_summaryTable">

  <tr>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultScoreCalculation.propertyTableHeader"/>
    </th>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultScoreCalculation.propertyValueTableHeader"/>
    </th>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultScoreCalculation.relativePropertyWeightTableHeader"/>
    </th>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultScoreCalculation.propertyValueStrengthTableHeader"/>
    </th>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultScoreCalculation.scoreTableHeader"/>
    </th>
  </tr>

  <c:if test="${not (empty rankConfig.properties)}">
    <c:set var="denom" value="${rankConfig.denom}"/>
    <c:set var="totalScore" value="0"/>


      <c:remove var="propField" />
      <c:remove var="propRelevance" />

      <c:forEach items="${rankConfig.properties}" var="prop">
        <c:set var="score" value="0"/>
        <c:choose>
          <c:when test="${'$relevance' eq prop.name}">
            <c:set var="score" value="${(prop.weight / denom) * prop.mapping}"/>
          </c:when>
          <c:otherwise>
            <c:set var="score" value="${(prop.weight / denom) * (prop.mapping / prop.max) * 100}"/>
          </c:otherwise>
        </c:choose>
        <c:set var="totalScore" value="${totalScore + score}"/>

        <c:set var="showProperty" value="${true}" />
        <c:if test="${prop.name eq '$field'}">
          <c:set var="showProperty" value="${false}" />
          <c:set var="propField" value="${prop}" />
        </c:if>
        <c:if test="${prop.name eq '$relevance'}">
          <c:set var="showProperty" value="${false}" />
          <c:set var="propRelevance" value="${prop}" />
        </c:if>
        <c:if test="${showProperty}">
          <dspel:include page="scoreRow.jsp">
            <dspel:param name="prop" value="${prop}"/>
            <dspel:param name="denom" value="${denom}"/>
          </dspel:include>
        </c:if>
      </c:forEach>

      <c:if test="${!empty propRelevance}">
        <dspel:include page="scoreRow.jsp">
          <dspel:param name="prop" value="${propRelevance}"/>
          <dspel:param name="denom" value="${denom}"/>
          <dspel:param name="propNameKey" value="searchTestingResultScoreCalculation.property.name.relevance" />
        </dspel:include>
      </c:if>

      <c:if test="${!empty propField}">
        <dspel:include page="scoreRow.jsp">
          <dspel:param name="prop" value="${propField}"/>
          <dspel:param name="denom" value="${denom}"/>
          <dspel:param name="propNameKey" value="searchTestingResultScoreCalculation.property.name.field" />
        </dspel:include>
      </c:if>

      <tr class="atg_calculatedScore">
        <td colspan="4" class="atg_scoreLabel">
          <c:choose>
            <c:when test="${(empty rankConfig.tops) && (empty movements)}">
              <fmt:message key="searchTestingResultScoreCalculation.calcScore" />
            </c:when>
            <c:otherwise>
              <fmt:message key="searchTestingResultScoreCalculation.propScore" />
            </c:otherwise>
          </c:choose>
        </td>
        <td class="atg_scoreTotal atg_numberValue">
          <strong>
            <fmt:formatNumber type="number"
              minFractionDigits="1"
              maxFractionDigits="1"
              value="${totalScore}"/>
          </strong>
        </td>
      </tr>

      <c:if test="${(!empty rankConfig.tops) || (!empty movements)}">
        <tr>
          <td>
            <fmt:message key="searchTestingResultScoreCalculation.positioningRulesPrompt"/>
          </td>
          <td colspan="3">
            <fmt:message key="searchTestingResultScoreCalculation.positioningMessage"/>
          </td>
          <td></td>
        </tr>

        <tr class="atg_calculatedScore">
          <td colspan="4" class="atg_scoreLabel">
              <fmt:message key="searchTestingResultScoreCalculation.finalScore" />
          </td>
          <td class="atg_scoreTotal atg_numberValue">
            <strong>
              <fmt:formatNumber type="number"
                minFractionDigits="1"
                maxFractionDigits="1"
                value="${finalScore}"/>
            </strong>
          </td>
        </tr>

      </c:if>
  </c:if>

  <c:if test="${(empty rankConfig.properties) and !(empty finalScore)}">
    <tr>
      <td>
        <fmt:message key="searchTestingResultScoreCalculation.property.name.relevance" />
      </td>
      <td>
        <c:out value="${finalScore}"/>
      </td>
      <td class="atg_numberValue">
        <fmt:formatNumber type="number"
            minFractionDigits="1"
            maxFractionDigits="1"
            value="${100}"/>%
      </td>
      <td class="atg_numberValue">
        <fmt:formatNumber type="number"
            minFractionDigits="1"
            maxFractionDigits="1"
            value="${finalScore}"/>
      </td>
      <td class="atg_numberValue">
        <fmt:formatNumber type="number"
            minFractionDigits="1"
            maxFractionDigits="1"
            value="${finalScore}"/>
      </td>
    </tr>

    <tr class="atg_calculatedScore">
      <td colspan="4" class="atg_scoreLabel">
        <fmt:message key="searchTestingResultScoreCalculation.finalScore" />
      </td>
      <td class="atg_scoreTotal atg_numberValue">
        <strong<c:if test="${finalScore lt minScore}"> class="notMet"</c:if>>
          <fmt:formatNumber type="number"
                minFractionDigits="1"
                maxFractionDigits="1"
                value="${finalScore}"/>
        </strong>
      </td>
    </tr>
  </c:if>
  </table>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/scoreCalculation.jsp#2 $$Change: 651448 $--%>
