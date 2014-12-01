<%--
  This page provides the information about the 'matching statements evaluation'
  for the result per-stage tracking information.

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response.   

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/evaluateStatementTerm.jsp#2 $$Change: 651448 $
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

  <c:set var="estimateMaxStatus"
      value="${item.statementRetrieval.limits.limitsMap['estimateMax'].status}"/>
  <c:set var="estimateMaxSetting"
      value="${item.statementRetrieval.limits.limitsMap['estimateMax'].setting}"/>

  <c:set var="estimateMinStatus"
      value="${item.statementRetrieval.limits.limitsMap['estimateMin'].status}"/>
  <c:set var="estimateMinSetting"
      value="${item.statementRetrieval.limits.limitsMap['estimateMin'].setting}"/>

  <c:set var="retMaxStatus"
      value="${item.statementRetrieval.limits.limitsMap['retMax'].status}"/>
  <c:set var="retMaxSetting"
      value="${item.statementRetrieval.limits.limitsMap['retMax'].setting}"/>

  <c:set var="retLimitStatus"
      value="${item.statementRetrieval.limits.limitsMap['retLimit'].status}"/>
  <c:set var="retLimitSetting"
      value="${item.statementRetrieval.limits.limitsMap['retLimit'].setting}"/>

  <c:set var="relevMinStatus"
      value="${item.statementRetrieval.limits.limitsMap['relevMin'].status}"/>
  <c:set var="relevMinSetting"
      value="${item.statementRetrieval.limits.limitsMap['relevMin'].setting}"/>

  <c:choose>
    <c:when test="${'succeed' eq item.summary.statement}">
      <p class="atg_includedConstraint">
        <fmt:message key="searchTestingEvaluateStatementTerm.includedConstraintTitle"/>
      </p>
      <dspel:include page="evaluateStatementTermTable.jsp">
        <dspel:param name="item" value="${item}"/>
      </dspel:include>
    </c:when>
    <c:otherwise>
      <c:choose>
        <c:when test="${'fail' eq estimateMaxStatus}">
          <p class="atg_excludedConstraint">
            <fmt:message key="searchTestingEvaluateStatementTerm.excludedConstraintTitle.estimateMaxStatusFail">
              <fmt:param value="${estimateMaxSetting}"/>
            </fmt:message>
          </p>
        </c:when>
        <c:when test="${'fail' eq retMaxStatus}">
          <p class="atg_excludedConstraint">
            <fmt:message key="searchTestingEvaluateStatementTerm.excludedConstraintTitle.retMaxStatusFail">
              <fmt:param value="${retMaxSetting}"/>
            </fmt:message>
          </p>
        </c:when>
        <c:when test="${'fail' eq retLimitStatus}">
          <p class="atg_excludedConstraint">
            <fmt:message key="searchTestingEvaluateStatementTerm.excludedConstraintTitle.retLimitStatusFail">
              <fmt:param value="${retLimitSetting}"/>
            </fmt:message>
          </p>
        </c:when>
        <c:when test="${'fail' eq estimateMinStatus}">
          <p class="atg_excludedConstraint">
            <fmt:message key="searchTestingEvaluateStatementTerm.excludedConstraintTitle.estimateMinStatusFail">
              <fmt:param value="${estimateMinSetting}"/>
            </fmt:message>
          </p>
        </c:when>
        <c:when test="${'fail' eq relevMinStatus}">
          <p class="atg_includedConstraint">
            <fmt:message key="searchTestingEvaluateStatementTerm.includedConstraintTitle"/>
          </p>
          <dspel:include page="evaluateStatementTermTable.jsp">
            <dspel:param name="item" value="${item}"/>
          </dspel:include>
          <p class="atg_excludedConstraint">
            <fmt:message key="searchTestingEvaluateStatementTerm.excludedConstraintTitle.relevMinStatusFail"/>
          </p>
        </c:when>
        <c:otherwise>
          <p class="atg_excludedConstraint">
            <fmt:message key="searchTestingEvaluateStatementTerm.excludedConstraintTitle.other"/>
          </p>
          <dspel:include page="evaluateStatementTermTable.jsp">
            <dspel:param name="item" value="${item}"/>
          </dspel:include>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/evaluateStatementTerm.jsp#2 $$Change: 651448 $--%>
