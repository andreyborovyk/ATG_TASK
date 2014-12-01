<%--
  The table for the tracked items details section of the outputs.

  Included into results.jsp

  The following parameters are passed into the page:

  @param searchResponse         search response
  @param displayTrackedDetails  true if the user wants detailed per-stage tracking

  The following request-scoped variables are expected to be set:

  @param formHandler            SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/trackedItemDetailsBaseTable.jsp#2 $$Change: 651448 $
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

  <dspel:getvalueof var="searchResponse" param="searchResponse"/>
  <dspel:getvalueof var="displayTrackedDetails" param="displayTrackedDetails" />

  <assetui-search:getTrackedItemOutcomes var="stagesOutcomes"
      diagnostics="${searchResponse.diagnostics}"
      varByItem="itemsToOutcomes"
      scope="request"/>

  <%-- Check if results are of different languages. --%>
  <div class="searchTestingTableHeading "><fmt:message key="searchTestingTrackedItemDetailsBaseTable.title"/></div>
  <table class="atg_dataTable atg_smerch_summaryTable">
    <tr>
      <th>
        <fmt:message key="searchTestingTrackedItemDetailsBaseTable.noTableHeader"/>
      </th>
      <th>&nbsp;</th>
      <th>
        <fmt:message key="searchTestingTrackedItemDetailsBaseTable.nameTableHeader"/>
      </th>
      <c:if test="${formHandler.multiTargetLanguages}">
        <th>
          <fmt:message key="searchTestingTrackedItemDetailsBaseTable.languageTableHeader"/>
        </th>
      </c:if>
      <th>
        <fmt:message key="searchTestingTrackedItemDetailsBaseTable.idTableHeader"/>
      </th>
      <th>
        <fmt:message key="searchTestingTrackedItemDetailsBaseTable.outcomeTableHeader"/>
      </th>
      <c:choose>
        <c:when test="${requestScope.formHandler.subitemsValid}">
          <th class="atg_numberValue">
            <fmt:message key="searchTestingTrackedItemDetailsBaseTable.subItemScoreTableHeader"/>
          </th>
        </c:when>
        <c:otherwise>
          <th class="atg_numberValue">
            <fmt:message key="searchTestingTrackedItemDetailsBaseTable.finalScoreTableHeader"/>
          </th>
        </c:otherwise>
      </c:choose>
    </tr>
    <c:set var="items"
        value="${searchResponse.diagnostics.trace.items}"/>
    <c:forEach items="${items}" var="item" varStatus="status">
      <c:set var="inspectInfo"
          value="${item.linkedItemInspect}"/>
      <c:choose>
        <c:when test="${0 == (status.index mod 2)}">
          <tr class="atg_altRow">
        </c:when>
        <c:otherwise>
          <tr>
        </c:otherwise>
      </c:choose>
        <c:choose>
          <c:when test="${itemsToOutcomes[item] == 'accessIndex_failed'}">
            <td>
              <a id="track_<c:out value="${item.trackId}"/>" name="track_<c:out value="${item.trackId}"/>"></a>
              <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems" />
            </td>
            <td class="atg_iconCell">
              <dspel:include page="trackedItemIcon.jsp">
                <dspel:param name="item" value="${item}"/>
              </dspel:include>
            </td>
            <td>
              <fmt:message key="searchTestingResultSearchProcessingSummary.unknown" />
            </td>
            <c:if test="${formHandler.multiTargetLanguages}">
              <td>
                <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems" />
              </td>
            </c:if>
            <td>
              <c:out value="${item.trackId}"/>
            </td>
            <td>
              <dspel:include page="outcome.jsp">
                <dspel:param name="outcome" value="${itemsToOutcomes[item]}"/>
              </dspel:include>
            </td>
            <td class="atg_numberValue">
              <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems" />
            </td>
          </c:when>
          <c:otherwise>
            <td>
              <a id="track_<c:out value="${inspectInfo.properties[formHandler.indexedItemIdPropertyName][0].value}"/>" name="track_<c:out value="${inspectInfo.properties[formHandler.indexedItemIdPropertyName][0].value}"/>"></a>
              <c:set var="pos"/>
              <fmt:message var="napos"
                  key="searchTestingResultSearchProcessingSummary.outcome.noItems"/>
              <c:if test="${(not (empty item.resultCollection))
                  and (not (empty item.resultCollection.grouping))
                  and (not (empty item.resultCollection.grouping.groups))
                  and (not (empty item.resultCollection.grouping.groups[0]))}">
                <c:set var="pos" value="${item.resultCollection.grouping.groups[0].id}"/>
              </c:if>
              <c:choose>
                <c:when test="${empty pos}">
                  <c:out value="${napos}"/>
                </c:when>
                <c:otherwise>
                  <c:out value="${pos}"/>
                </c:otherwise>
              </c:choose>
            </td>
            <td class="atg_iconCell">
              <dspel:include page="trackedItemIcon.jsp">
                <dspel:param name="item" value="${item}"/>
              </dspel:include>
            </td>
            <td>
              <c:url value="/searchTesting/output/contentItem.jsp" context="/AssetUI-Search" var="itemUrl">
                <c:param name="docUrl" value="${inspectInfo.url}"/>
                <c:param name="index" value="${status.index + 1}"/>
                <c:param name="idPropertyName" value="${formHandler.indexedItemIdPropertyName}"/>
                <c:param name="namePropertyDisplayName" value="${formHandler.viewIndexedItemNamePropertyName}" />
              </c:url>
              <a href="<c:out value='${itemUrl}'/>" class="atg_smerch_ciPop">
                <%--<c:out value="${inspectInfo.sentenceMap['displayname'][0].sentenceValue}"/>--%>
                <dspel:include page="propertyValueAsText.jsp">
                  <dspel:param name="propertyValue" value="${inspectInfo.sentenceMap[formHandler.viewIndexedItemNamePropertyName][0].sentenceValue}"/>
                </dspel:include>
              </a>
            </td>
            <c:if test="${formHandler.multiTargetLanguages}">
              <td>
                <assetui-search:getLanguageName language="${inspectInfo.language}" var="language"/>
                <%--<c:out value="${language}"/>--%>
                <dspel:include page="propertyValueAsText.jsp">
                  <dspel:param name="propertyValue" value="${language}"/>
                </dspel:include>
              </td>
            </c:if>
            <td>
              <c:out value="${inspectInfo.properties[formHandler.indexedItemIdPropertyName][0].value}"/>
            </td>
            <td>
              <assetui-search:getTrackedItemOutcomeText item="${item}"
                  var="outcome"/>
              <dspel:include page="trackedItemOutcome.jsp">
                <dspel:param name="outcome" value="${outcome}"/>
              </dspel:include>
            </td>
            <td class="atg_numberValue">
              <c:choose>
                <%-- item is included in result or if it failed on stage after "Evaluate Statement Term" stage (on "Determine Item Scores" or "Collect Results" stages)
                	(100 eq item.relevance) needs when we make category navigation without search text
                     BUGS-FIXED 159652 and 159655--%>
                <c:when test="${('succeed' eq item.summary.result) or ('succeed' eq item.summary.statement) or (0 ne item.relevance)}">
                <fmt:formatNumber type="number"
                    minFractionDigits="1"
                    maxFractionDigits="1"
                    value="${item.relevance}"/> 
                </c:when>
                <c:otherwise>
                  <fmt:message key="searchTestingResultSearchProcessingSummary.outcome.noItems" />
                </c:otherwise>
              </c:choose>
            </td>
          </c:otherwise>
        </c:choose>
      </tr>
      <c:if test="${displayTrackedDetails}">
        <c:choose>
          <c:when test="${0 == (status.index mod 2)}">
            <tr class="atg_altRow">
          </c:when>
          <c:otherwise>
            <tr>
          </c:otherwise>
        </c:choose>
          <c:set var="colspan" value="${6}" />
          <c:if test="${formHandler.multiTargetLanguages}">
            <c:set var="colspan" value="${colspan+1}" />
          </c:if>
          <c:if test="${requestScope.formHandler.subitemsValid}">
            <c:set var="colspan" value="${colspan+1}" />
          </c:if>
          <td colspan="<c:out value="${colspan}" />">
          <div class="atg_itemDetails">
            <dspel:include page="itemAccessSearchIndexTrackingInfo.jsp">
              <dspel:param name="item" value="${item}"/>
            </dspel:include>
            <c:if test="${not ('fail' eq item.summary.indexed)}">
              <dspel:include page="retrieveItemCandidate.jsp">
                <dspel:param name="item" value="${item}"/>
              </dspel:include>
              <c:if test="${containMatchingTerms}">
                <dspel:include page="checkItemConstraints.jsp">
                  <dspel:param name="item" value="${item}"/>
                </dspel:include>
                <c:if test="${not ('fail' eq item.summary.constraint)}">
                  <c:if test="${item.itemRetrieval != null}">
                    <dspel:include page="evaluateMatchingTerm.jsp">
                      <dspel:param name="item" value="${item}"/>
                    </dspel:include>
                  </c:if>
                  <c:if test="${not ('fail' eq item.summary.item)}">
                    <c:if test="${item.statementRetrieval != null}">
                      <dspel:include page="evaluateStatementTerm.jsp">
                        <dspel:param name="item" value="${item}"/>
                      </dspel:include>
                    </c:if>
                    <c:if test="${not ('fail' eq item.summary.statement)}">
                      <c:if test="${(not formHandler.categoryNavigation)
                          or (formHandler.categoryNavigation and formHandler.searchConfigSelected)}">
                        <dspel:include page="calculateItemScore.jsp">
                          <dspel:param name="item" value="${item}"/>
                        </dspel:include>
                      </c:if>
                      <c:if test="${not ('fail' eq item.summary.relevance)}">
                        <dspel:include page="groupItemIntoResult.jsp">
                          <dspel:param name="item" value="${item}"/>
                        </dspel:include>
                        <dspel:include page="collectResult.jsp">
                          <dspel:param name="item" value="${item}"/>
                        </dspel:include>
                      </c:if>
                    </c:if>
                  </c:if>
                </c:if>
              </c:if>
            </c:if>
            <dspel:include page="assembleFirstPageResults.jsp">
              <dspel:param name="item" value="${item}"/>
            </dspel:include>
          </div>
          </td>
        </tr>
      </c:if>
    </c:forEach>
  </table>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/trackedItemDetailsBaseTable.jsp#2 $$Change: 651448 $--%>
