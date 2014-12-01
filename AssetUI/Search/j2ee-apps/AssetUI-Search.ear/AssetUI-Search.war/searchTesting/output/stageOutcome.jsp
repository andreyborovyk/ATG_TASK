<%--
  JSP fragment for the Search Processing Summary table - Tracked Item Outcomes column.

  Included into searchProcessingSummary.jsp

  The following parameters are passed into the page:

  @param stageOutcome           outcome of the stage
  @param outcomeDescriptionKey  message key for the outcome

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/stageOutcome.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<dspel:page>
  <dspel:getvalueof param="stageOutcome" var="stageOutcome"/>
  <dspel:getvalueof param="outcomeDescriptionKey" var="outcomeDescriptionKey"/>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <assetui-search:getContainerSize container="${stageOutcome}" var="notFoundItemsCount"/>
  <c:if test="${notFoundItemsCount > 0}">
    <dl>
      <dt>
        <fmt:message key="${outcomeDescriptionKey}"/>
        <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.colon"/>
      </dt>
      <c:forEach items="${stageOutcome}" var="item">
        <c:choose>
          <%-- todo item.summary.result--%>
          <c:when test="${item.summary.result != 'succeed'}">
            <dd class="atg_trackedItem_notIncluded">
          </c:when>
          <c:otherwise>
            <c:set var="onFirstPage" value="false"/>
            <c:forEach items="${item.resultCollection.grouping.groups}" var="group">
              <c:if test="${group.page == 1}">
                <c:set var="onFirstPage" value="true"/>
              </c:if>
            </c:forEach>
            <c:choose>
              <c:when test="${onFirstPage}">
                <dd class="atg_trackedItem_firstPage">
              </c:when>
              <c:otherwise>
                <dd class="atg_trackedItem_notFirstPage">
              </c:otherwise>
            </c:choose>
          </c:otherwise>
        </c:choose>
        <a href="#track_<c:out value='${item.trackId}'/>">
          <c:out value="${item.trackId}"/>
        </a>
        </dd>
      </c:forEach>
    </dl>
  </c:if>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/stageOutcome.jsp#2 $$Change: 651448 $--%>
