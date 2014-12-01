<%--
  Exclusion rules section of the query rule output

  Included into queryRules.jsp.

  The following parameters are passed into the page:

  @param queryResponse   search response object
  @param exclusionRules  exclusion rules collection from search response object
  @param debugExclusion  true if excluded items are shown

  The following request-scoped variables are expected to be set:
  
  @param formHandler     SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/exclusionRules.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<dspel:page>
<dspel:getvalueof param="queryResponse" var="queryResponse"/>
<dspel:getvalueof param="exclusionRules" var="exclusionRules"/>
<dspel:getvalueof param="debugExclusion" var="debugExclusion"/>

<dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle basename="${config.resourceBundle}"/>
<c:choose>
<c:when test="${debugExclusion}">
  <p class="atg_tableViewSwitch">
    <a class="buttonSmall" href="#" onclick="changeDebugExclusion(false);">
      <fmt:message key="searchTestResultExclusionRules.applyRules.link"/>
    </a>
  </p>
  <table class="atg_dataTable atg_smerch_summaryTable">
    <tr>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestResultExclusionRules.rulesActivated.title"/>
      </th>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestResultExclusionRules.index.title"/>
      </th>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestResultExclusionRules.exclusionItems.title"/>
      </th>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestResultExclusionRules.id.title"/>
      </th>
    </tr>
    <c:forEach items="${exclusionRules}" var="exclusionRule" varStatus="status">
      <assetui-search:getExcludedItems queryRuleBean="${exclusionRule}"
                                       rules="${queryResponse.ruleExclusions.rules}"
                                       searchResults="${queryResponse.results}"
                                       idPropertyName="${formHandler.indexedItemIdPropertyName}"
                                       displaynamePropertyName="${formHandler.indexedItemNamePropertyName}"/>
      <c:choose>
        <c:when test="${0 == (status.index mod 2)}">
          <c:set var="trClass" value=""/>
        </c:when>
        <c:otherwise>
          <c:set var="trClass" value="atg_altRow"/>
        </c:otherwise>
      </c:choose>
      <assetui-search:getContainerSize container="${exclusionRule.affectedItemsFromCurrentPage}"
                                       var="affectedItemsFromCurrentPageCount"/>
      <c:choose>
        <c:when test="${affectedItemsFromCurrentPageCount == 0}">
          <tr class="<c:out value='${trClass}'/>">
            <td>
              <c:out value="${exclusionRule.description}"/>
            </td>
            <td></td>
            <td>
                <%--Items not on current page--%>
              <fmt:message key="searchTestResultExclusionRules.notOnCurrentPage">
                <fmt:param>
                  <c:out value="${exclusionRule.allAffectedItemsCount - affectedItemsFromCurrentPageCount}"/>
                </fmt:param>
              </fmt:message>
            </td>
            <td></td>
          </tr>
        </c:when>
        <c:otherwise>
          <c:forEach items="${exclusionRule.affectedItemsFromCurrentPage}" var="affectedItem" varStatus="status">
            <tr class="<c:out value='${trClass}'/>">
              <c:if test="${status.index == 0}">
                <td rowspan="<c:out value='${affectedItemsFromCurrentPageCount + 1}'/>">
                  <c:out value="${exclusionRule.description}"/>
                </td>
              </c:if>
              <td>
                <c:out value="${affectedItem.index}"/>
              </td>
              <td>
                <c:url value="/searchTesting/output/contentItem.jsp" context="/AssetUI-Search" var="itemUrl">
                  <c:param name="docUrl" value="${affectedItem.url}"/>
                  <c:param name="idPropertyName" value="${formHandler.indexedItemIdPropertyName}"/>
                  <c:param name="namePropertyDisplayName" value="${formHandler.viewIndexedItemNamePropertyName}"/>
                </c:url>
                <a href="<c:out value='${itemUrl}'/>" class="atg_smerch_ciPop">
                  <c:out value="${affectedItem.name}"/>
                </a>
              </td>
              <td>
                <c:out value="${affectedItem.id}"/>
              </td>
            </tr>
          </c:forEach>
          <%--Items not on current page--%>
          <tr class="<c:out value='${trClass}'/>">
            <td></td>
            <td>
              <fmt:message key="searchTestResultExclusionRules.notOnCurrentPage">
                <fmt:param>
                  <c:out value="${exclusionRule.allAffectedItemsCount}"/>
                </fmt:param>
              </fmt:message>
            </td>
            <td></td>
          </tr>
        </c:otherwise>
      </c:choose>
    </c:forEach>
  </table>
</c:when>
<c:otherwise>
  <p class="atg_tableViewSwitch">
    <a class="buttonSmall" href="#" onclick="changeDebugExclusion(true);">
      <fmt:message key="searchTestResultExclusionRules.showExcludedResults.link"/>
    </a>
  </p>
  <table class="atg_dataTable atg_smerch_summaryTable">
    <tr>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestResultExclusionRules.rulesActivated.title"/>
      </th>
    </tr>
    <c:set var="trClass" value="atg_altRow"/>
    <c:forEach items="${exclusionRules}" var="exclusionRule">
      <c:choose>
        <c:when test="${trClass == 'atg_altRow'}">
          <c:set var="trClass" value=""/>
        </c:when>
        <c:otherwise>
          <c:set var="trClass" value="atg_altRow"/>
        </c:otherwise>
      </c:choose>
      <tr class="<c:out value='${trClass}'/>">
        <td>
          <c:out value="${exclusionRule.description}"/>
        </td>
      </tr>
    </c:forEach>
  </table>
</c:otherwise>
</c:choose>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/exclusionRules.jsp#2 $$Change: 651448 $--%>
