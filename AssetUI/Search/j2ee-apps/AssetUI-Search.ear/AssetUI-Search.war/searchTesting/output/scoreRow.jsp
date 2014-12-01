<%--
  Score calculation table row.

  Included into scoreCalculation.jsp once for each weighed property.

  The following parameters are passed into the page:

  @param prop             rank configuration that was executed during the search
  @param denom            denominator for the rank config's property weighting
  @param propNameKey      Optional key that is used to retrieve property name from the resource bundle. If it's empty, prop.name will be used.

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/scoreRow.jsp#2 $$Change: 651448 $
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

  <dspel:getvalueof var="prop" param="prop"/>
  <dspel:getvalueof var="denom" param="denom"/>
  <dspel:getvalueof var="propNameKey" param="propNameKey"/>

  <c:set var="score" value="0"/>
  <c:set var="propValue" value=""/>

  <c:choose>
    <c:when test="${'$relevance' eq prop.name}">
      <c:set var="score" value="${(prop.weight / denom) * prop.mapping}"/>
      <fmt:formatNumber type="number"
                minFractionDigits="1"
                maxFractionDigits="1"
                value="${(prop.mapping / prop.max) * 100}"
				var="propValue"/>
    </c:when>
    <c:when test="${'$field' eq prop.name}">
      <c:set var="score" value="${(prop.weight / denom) * (prop.mapping / prop.max) * 100}"/>
      <fmt:formatNumber type="number"
                minFractionDigits="1"
                maxFractionDigits="1"
                value="${(prop.mapping / prop.max) * 100}"
				var="propValue"/>
    </c:when>
    <c:otherwise>
      <c:set var="score" value="${(prop.weight / denom) * (prop.mapping / prop.max) * 100}"/>
      <c:if test="${! empty prop.value}"> 
        <assetui-search:propertyValueDisplayTextResolver propertyName="${prop.name}" propertyValue="${prop.value}"
            var="propValue"/>
      </c:if>
    </c:otherwise>
  </c:choose>

        <tr>
          <td>
            <assetui-search:propertyDisplayNameResolver var="displayName"
                propertyName="${prop.name}"/>
            <c:choose>
              <c:when test="${!empty propNameKey}">
                <fmt:message key="${propNameKey}" />
              </c:when>
              <c:when test="${(empty propNameKey) and (!empty displayName)}">
                <c:out value="${displayName}"/>
              </c:when>
              <c:otherwise>
                <c:out value="${prop.name}"/>
              </c:otherwise>
            </c:choose>
          </td>
          <td>
            <c:out value="${propValue}"/>
          </td>
          <td class="atg_numberValue">
            <fmt:formatNumber type="number"
                minFractionDigits="1"
                maxFractionDigits="1"
                value="${(prop.weight / denom) * 100}"/>%
          </td>
          <td class="atg_numberValue">
            <fmt:formatNumber type="number"
                minFractionDigits="1"
                maxFractionDigits="1"
                value="${(prop.mapping / prop.max) * 100}"/>
            <c:if test="${(not (empty prop.rank))
                and (not (empty prop.maxRank))
                and (prop.maxRank > 0)
                and (prop.rank > 0)}">
              <fmt:message key="searchTestingResultScoreRow.rankOf">
                <fmt:param value="${prop.rank}"/>
                <fmt:param value="${prop.maxRank}"/>
              </fmt:message>
            </c:if>
          </td>
          <td class="atg_numberValue">
            <fmt:formatNumber type="number"
                minFractionDigits="1"
                maxFractionDigits="1"
                value="${score}"/>
          </td>
        </tr>


</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/scoreRow.jsp#2 $$Change: 651448 $--%>
