<%--
  Positioning rules section of the query rule output

  Included into queryRules.jsp.

  The following parameters are passed into the page:

  @param queryResponse      search response object
  @param positioningRules   positioning rules collection from search response object
  @param disablePositioning true if positioning rules are disabled

  The following request-scoped variables are expected to be set:
  
  @param formHandler     SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/positioningRules.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<dspel:page>
<dspel:getvalueof param="queryResponse" var="queryResponse"/>
<dspel:getvalueof param="positioningRules" var="positioningRules"/>
<dspel:getvalueof var="disablePositioning" param="disablePositioning"/>

<dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle basename="${config.resourceBundle}"/>
<c:choose>
  <c:when test="${disablePositioning}">
    <p class="atg_tableViewSwitch">
      <a class="buttonSmall" href="#" onclick="changeRuleMode(false);">
        <fmt:message key="searchTestResultPositioningRules.showResultsWith.link"/>
      </a>
      <br/>
      <fmt:message key="searchTestResultPositioningRules.temporarilyDisabled"/>
    </p>
  </c:when>
  <c:otherwise>
    <p class="atg_tableViewSwitch">
      <a class="buttonSmall" href="#" onclick="changeRuleMode(true);">
        <fmt:message key="searchTestResultPositioningRules.showResultsWithout.link"/>
      </a>
    </p>
    <table class="atg_dataTable atg_smerch_summaryTable">
      <tr>
        <th class="atg_smerch_summaryTitle">
          <fmt:message key="searchTestResultPositioningRules.rulesActivated.title"/>
        </th>
        <th class="atg_smerch_summaryTitle"></th>
        <th class="atg_smerch_summaryTitle">
          <fmt:message key="searchTestResultPositioningRules.index.title"/>
        </th>
        <th class="atg_smerch_summaryTitle">
          <fmt:message key="searchTestResultPositioningRules.repositionedItems.title"/>
        </th>
        <th class="atg_smerch_summaryTitle">
          <fmt:message key="searchTestResultPositioningRules.id.title"/>
        </th>
      </tr>
      <c:set var="trClass" value="atg_altRow"/>
      <c:forEach items="${positioningRules}" var="positioningRule" varStatus="status">
        <assetui-search:getRepositionedItems queryRuleBean="${positioningRule}"
                                             rules="${queryResponse.rulePositioning.rules}"
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
        <assetui-search:getContainerSize container="${positioningRule.affectedItemsFromCurrentPage}"
                                         var="affectedItemsFromCurrentPageCount"/>
        <c:choose>
          <c:when test="${affectedItemsFromCurrentPageCount == 0}">
            <tr class="<c:out value='${trClass}'/>">
              <td>
                <c:out value="${positioningRule.description}"/>
              </td>
              <td></td>
              <td></td>
              <td>
                <fmt:message key="searchTestResultPositioningRules.notOnCurrentPage">
                  <fmt:param>
                    <c:out value="${positioningRule.allAffectedItemsCount - affectedItemsFromCurrentPageCount}"/>
                  </fmt:param>
                </fmt:message>
              </td>
              <td></td>
            </tr>
          </c:when>
          <c:otherwise>
            <c:forEach items="${positioningRule.affectedItemsFromCurrentPage}" var="affectedItem" varStatus="status">
              <tr class="<c:out value='${trClass}'/>">
                <c:if test="${status.index == 0}">
                  <td rowspan="<c:out value='${affectedItemsFromCurrentPageCount + 1}'/>">
                    <c:out value="${positioningRule.description}"/>
                  </td>
                </c:if>
                <td class="atg_iconCell"><span class="atg_tableIcon atg_arrowUp"></span></td>
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
              <td></td>
              <td>
                <fmt:message key="searchTestResultPositioningRules.notOnCurrentPage">
                  <fmt:param>
                    <c:out value="${positioningRule.allAffectedItemsCount - affectedItemsFromCurrentPageCount}"/>
                  </fmt:param>
                </fmt:message>
              </td>
              <td></td>
            </tr>
          </c:otherwise>
        </c:choose>
      </c:forEach>
    </table>
    <p class="atg_tableNote">
      <fmt:message key="searchTestResultPositioningRules.note"/>
    </p>
  </c:otherwise>
</c:choose>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/positioningRules.jsp#2 $$Change: 651448 $--%>
