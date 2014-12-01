<%--
  This page provides the information about the 'matching statements evaluation'
  for the result per-stage tracking information.

  Included into evaluateStatementTerm.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response.   

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/evaluateStatementTermTable.jsp#2 $$Change: 651448 $
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

  <c:set var="terms"
      value="${item.statementRetrieval.termLookup.terms}"/>
  <c:set var="booleanLogicMode"
      value="${item.statementRetrieval.booleanLogic.mode}"/>

  <c:if test="${not (empty terms)}">
    <table class="atg_dataTable atg_smerch_summaryTable">
      <tr>
        <th>
          <fmt:message key="searchTestingEvaluateStatementTermTable.headerTitle.queryTerm"/>
        </th>
        <th>
          <fmt:message key="searchTestingEvaluateStatementTermTable.headerTitle.statementLevelOperatorEffects"/>
        </th>
        <th>
          <fmt:message key="searchTestingEvaluateStatementTermTable.headerTitle.searchFor"/>
        </th>
      </tr>
      <c:forEach items="${terms}" var="term">
        <tr>
          <td>
            <c:out value="${term.label}"/>
          </td>
          <td>
            <c:choose>
              <c:when test="${not ('none' eq term.op)}">
                <c:out value="${term.op}"/>
              </c:when>
              <c:otherwise>
                <fmt:message key="searchTestingEvaluateStatementTermTable.termOperator.none"/>
              </c:otherwise>
            </c:choose>
          </td>
          <td>
            <c:set var="displayStemForms"
                value="${false}"/>
            <c:set var="displaySynonyms"
                value="${false}"/>
            <assetui-search:getContainerSize container="${term.stems}"
                var="stemCount"/>

            <c:forEach items="${term.stems}" var="stem">
              <c:choose>
                <c:when test="${stem.synonym}">
                  <c:set var="displaySynonyms"
                      value="${true}"/>
                </c:when>
                <c:otherwise>
                  <c:set var="displayStemForms"
                      value="${true}"/>
                </c:otherwise>
              </c:choose>
            </c:forEach>

            <c:if test="${(stemCount > 0)
                and (displayStemForms)}">
              <fmt:message key="searchTestingEvaluateStatementTermTable.listTitle.formsOfStem"/>:
              <ul>
                <c:forEach items="${term.stems}" var="stem">
                  <c:if test="${not stem.synonym}">
                    <li><c:out value="${stem.string}"/></li>
                  </c:if>
                </c:forEach>
              </ul>
            </c:if>

            <c:if test="${(stemCount > 0)
                and (displaySynonyms)}">
              <fmt:message key="searchTestingEvaluateStatementTermTable.listTitle.formsOfSynonyms"/>:
              <ul>
                <c:forEach items="${term.stems}" var="stem">
                  <c:if test="${stem.synonym}">
                    <li><c:out value="${stem.string}"/></li>
                  </c:if>
                </c:forEach>
              </ul>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td colspan="3">
          <c:set var="booleanLogicModeStr"
              value=""/>
          <c:choose>
            <c:when test="${'or' eq booleanLogicMode}">
              <fmt:message key="searchTestingEvaluateStatementTermTable.booleanLogicMode.or"
                  var="booleanLogicModeStr"/>
            </c:when>
            <c:when test="${'and' eq booleanLogicMode}">
              <fmt:message key="searchTestingEvaluateStatementTermTable.booleanLogicMode.and"
                  var="booleanLogicModeStr"/>
            </c:when>
          </c:choose>
          <fmt:message key="searchTestingEvaluateStatementTermTable.queryTermsRequired">
            <fmt:param value="${booleanLogicModeStr}"/>
          </fmt:message>
          <br/>
          <assetui-search:getContainerSize var="statementCount"
              container="${item.statementRetrieval.candidates.statements}"/>
          <fmt:message key="searchTestingEvaluateStatementTermTable.metRequirementStatement">
            <fmt:param value="${statementCount}"/>
          </fmt:message>
        </td>
      </tr>
    </table>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/evaluateStatementTermTable.jsp#2 $$Change: 651448 $--%>
