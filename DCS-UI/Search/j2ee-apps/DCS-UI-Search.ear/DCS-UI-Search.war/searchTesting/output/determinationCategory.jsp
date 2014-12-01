<%--
Determination of category table

Included into facets.jsp, but it can be configured in /atg/search/web/assetmanager/CategoryPathHelper.categoryDeterminationJsp

@param  searchResponse     search response object

@version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/output/determinationCategory.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<%@ taglib prefix="dcsui-srch" uri="http://www.atg.com/taglibs/dcsui-srch" %>

<dspel:page>
<dspel:getvalueof var="searchResponse" param="searchResponse"/>
<dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle basename="${config.resourceBundle}"/>

<dcsui-srch:reorderCategories determinedCategories="${searchResponse.refinements.selectConfig.mappings}"
 itemsCount="${searchResponse.refinements.selectConfig.total}" />

<div class="atg_categoryDeterminationInfo">
  <h3>
    <fmt:message key="searchTestingResultDeterminationCategory.determinationCategory"/>
  </h3>

  <p>
    <fmt:message key="searchTestingResultDeterminationCategory.searchEngine.title"/>
  </p>
  <ul class="atg_categoryDeterminationList">
    <li>
      <fmt:message key="searchTestingResultDeterminationCategory.examine.title"/>
    </li>
    <li>
      <fmt:message key="searchTestingResultDeterminationCategory.find.title"/>
    </li>
    <li>
      <fmt:message key="searchTestingResultDeterminationCategory.based.title"/>
    </li>
    <li>
      <fmt:message key="searchTestingResultDeterminationCategory.ifThere.title"/>
    </li>
  </ul>
</div>

<table class="atg_dataTable atg_smerch_summaryTable">
  <tr>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultDeterminationCategory.category.title"/>
    </th>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultDeterminationCategory.results.title"/>
    </th>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultDeterminationCategory.used.title"/>
    </th>
  </tr>
  <assetui-search:getContainerSize container="${searchResponse.refinements.selectConfig.mappings}" var="mapCount"/>

  <c:forEach items="${searchResponse.refinements.selectConfig.mappings}" var="mapping" begin="${mapCount - 1}"
             end="${mapCount - 1}">
    <tr>
      <td>
        <c:if test="${mapping.selected}"><b></c:if><%-- bold selected category --%>
        <fmt:message key="searchTestingResultDeterminationCategory.global.title"/>
        <c:if test="${mapping.selected}"></b></c:if>
      </td>
      <td>
        <c:if test="${mapping.selected}"><b></c:if><%-- bold selected category --%>
        <fmt:message key="searchTestingResultDeterminationCategory.all.value">
          <fmt:param value="${mapping.count}"/>
        </fmt:message>
        <c:if test="${mapping.selected}"></b></c:if>
      </td>
      <c:choose>
        <c:when test="${mapping.selected}">
          <td class="atg_categoryUsedIndicator"></td>
        </c:when>
        <c:otherwise>
          <td></td>
        </c:otherwise>
      </c:choose>
    </tr>
  </c:forEach>

  <c:if test="${mapCount > 1}">
    <c:forEach items="${searchResponse.refinements.selectConfig.mappings}" var="mapping" varStatus="status"
               end="${mapCount - 2}">
      <c:choose>
        <c:when test="${0 == (status.index mod 2)}">
          <c:set var="trClass" value="atg_altRow"/>
        </c:when>
        <c:otherwise>
          <c:set var="trClass" value=""/>
        </c:otherwise>
      </c:choose>
      <tr class="<c:out value='${trClass}'/>">
        <td>
          <c:if test="${mapping.selected}"><b></c:if><%-- bold selected category --%>
          <dcsui-srch:getCategoryAncestors categoryId="${mapping.mappingValue}" var="categoryAncestors"/>
          <c:forEach items="${categoryAncestors}" var="ancestor" varStatus="status">
            <c:out value="${ancestor}"/>
            <c:if test="${! status.last}">></c:if>
          </c:forEach>
          <c:if test="${mapping.selected}"></b></c:if>
        </td>
        <td>
          <c:if test="${mapping.selected}"><b></c:if><%-- bold selected category --%>
          <c:choose>
            <c:when test="${mapping.count == searchResponse.refinements.selectConfig.total}">
              <fmt:message key="searchTestingResultDeterminationCategory.all.value">
                <fmt:param value="${mapping.count}"/>
              </fmt:message>
            </c:when>
            <c:otherwise>
              <c:out value="${mapping.count}"/>
            </c:otherwise>
          </c:choose>
          <c:if test="${mapping.selected}"></b></c:if>
        </td>
        <c:choose>
          <c:when test="${mapping.selected}">
            <td class="atg_categoryUsedIndicator"></td>
          </c:when>
          <c:otherwise>
            <td></td>
          </c:otherwise>
        </c:choose>
      </tr>
    </c:forEach>
  </c:if>
</table>
<p class="atg_tableNote">
  <fmt:message key="searchTestingResultDeterminationCategory.facetSet.title"/>
  <c:out value="${searchResponse.refinements.name}"/>
</p>
</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/output/determinationCategory.jsp#2 $$Change: 651448 $--%>
