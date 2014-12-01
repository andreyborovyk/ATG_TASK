<%--
  This page provides the information about the 'matching terms evaluation'
  for the result per-stage tracking information.

  Included into evaluateMatchingTerm.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response.   

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/evaluateMatchingTermTable.jsp#2 $$Change: 651448 $
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
      value="${item.itemRetrieval.termLookup.terms}"/>

  <c:if test="${not (empty terms)}">
    <table class="atg_dataTable atg_smerch_summaryTable">
      <tr>
        <th>
          <fmt:message key="searchTestingEvaluateMatchingTermTable.headerTitle.queryTerm"/>
        </th>
        <th>
          <fmt:message key="searchTestingEvaluateMatchingTermTable.headerTitle.itemLevelOperatorEffects"/>
        </th>
        <th>
          <fmt:message key="searchTestingEvaluateMatchingTermTable.headerTitle.searchFor"/>
        </th>
        <th>
          <fmt:message key="searchTestingEvaluateMatchingTermTable.headerTitle.match"/>
        </th>
      </tr>
      <c:forEach items="${terms}" var="term">
        <tr>
          <td>
            <c:if test="${'succeed' eq term.status}">
              <b>
            </c:if>
            <c:out value="${term.label}"/>
            <c:if test="${'succeed' eq term.status}">
              </b>
            </c:if>
          </td>
          <td>
            <c:choose>
              <c:when test="${not ('none' eq term.op)}">
                <c:out value="${term.op}"/>
              </c:when>
              <c:otherwise>
                <fmt:message key="searchTestingEvaluateMatchingTermTable.termOperator.none"/>
              </c:otherwise>
            </c:choose>
          </td>
          <td>
            <c:if test="${not (empty term.stems)}">
              <ul>
                <c:forEach items="${term.stems}" var="stem">
                  <li>
                    <c:if test="${'succeed' eq term.status}">
                      <b>
                    </c:if>
                    <c:out value="${stem.string}"/>
                    <c:if test="${'succeed' eq term.status}">
                      </b>
                    </c:if>
                  </li>
                </c:forEach>
              </ul>
            </c:if>
          </td>
          <td>
            <c:choose>
              <c:when test="${'succeed' eq term.status}">
                <fmt:message key="searchTestingEvaluateMatchingTermTable.termStatus.success"/>
              </c:when>
              <c:otherwise>
                <fmt:message key="searchTestingEvaluateMatchingTermTable.termStatus.fail"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      <tr>
        <td colspan="3">
          <c:set var="booleanLogicMode"
              value=""/>
          <c:choose>
            <c:when test="${'or' eq item.itemRetrieval.booleanLogic.mode}">
              <fmt:message key="searchTestingEvaluateMatchingTermTable.booleanLogicMode.or"
                  var="booleanLogicMode"/>
              <%--<c:out value="At lease one (mode = OR)"/>--%>
            </c:when>
            <c:when test="${'and' eq item.itemRetrieval.booleanLogic.mode}">
              <fmt:message key="searchTestingEvaluateMatchingTermTable.booleanLogicMode.and"
                  var="booleanLogicMode"/>
              <%--<c:out value="All (mode = AND)"/>--%>
            </c:when>
          </c:choose>
          <fmt:message key="searchTestingEvaluateMatchingTermTable.queryTermsRequired">
            <fmt:param value="${booleanLogicMode}"/>
          </fmt:message>
          <c:if test="${'succeed' eq item.itemRetrieval.booleanLogic.quoted or 'fail' eq item.itemRetrieval.booleanLogic.quoted}">
            <br/>
            <fmt:message key="searchTestingEvaluateMatchingTermTable.termsMustBeInSpecifiedOrder" />
          </c:if>
        </td>
        <td class="atg_queryTermTotal">
          <c:choose>
            <c:when test="${'succeed' eq item.itemRetrieval.booleanLogic.status}">
              <fmt:message key="searchTestingEvaluateMatchingTermTable.booleanLogicStatus.success"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="searchTestingEvaluateMatchingTermTable.booleanLogicStatus.fail"/>
            </c:otherwise>
          </c:choose>
          <c:if test="${'succeed' eq item.itemRetrieval.booleanLogic.quoted or 'fail' eq item.itemRetrieval.booleanLogic.quoted}">
            <br/>
            <c:choose>
              <c:when test="${'succeed' eq item.itemRetrieval.booleanLogic.quoted}">
                <fmt:message key="searchTestingEvaluateMatchingTermTable.booleanLogicStatus.success"/>
              </c:when>
              <c:otherwise>
                <strong class="notMet"><fmt:message key="searchTestingEvaluateMatchingTermTable.booleanLogicStatus.fail"/></strong>
              </c:otherwise>
            </c:choose>
          </c:if>
        </td>
      </tr>
    </table>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/evaluateMatchingTermTable.jsp#2 $$Change: 651448 $--%>
