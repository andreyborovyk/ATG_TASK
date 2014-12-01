<%--
  This page provides the information about the 'matching terms evaluation'
  for the result per-stage tracking information.

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response.   

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/evaluateMatchingTerm.jsp#2 $$Change: 651448 $
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

  <c:set var="maxDocumentsStatus"
      value="${item.itemRetrieval.limits.limitsMap['maxDocuments'].status}"/>
  <c:set var="maxDocumentsSetting"
      value="${item.itemRetrieval.limits.limitsMap['maxDocuments'].setting}"/>
  <c:choose>
    <c:when test="${'succeed' eq item.summary.item}">
      <p class="atg_includedConstraint">
        <fmt:message key="searchTestingEvaluateMatchingTerm.includedConstraintTitle"/>
      </p>
      <dspel:include page="evaluateMatchingTermTable.jsp">
        <dspel:param name="item" value="${item}"/>
      </dspel:include>
    </c:when>
    <c:when test="${'fail' eq item.summary.item}">
      <c:choose>
        <c:when test="${'fail' eq maxDocumentsStatus}">
          <p class="atg_excludedConstraint">
            <fmt:message key="searchTestingEvaluateMatchingTerm.excludedConstraintTitle.notConsidered">
              <fmt:param value="${maxDocumentsSetting}"/>
            </fmt:message>
          </p>
        </c:when>
        <c:otherwise>
          <p class="atg_excludedConstraint">
            <fmt:message key="searchTestingEvaluateMatchingTerm.excludedConstraintTitle.nMetTermRequirements"/>
          </p>
          <dspel:include page="evaluateMatchingTermTable.jsp">
            <dspel:param name="item" value="${item}"/>
          </dspel:include>
        </c:otherwise>
      </c:choose>
    </c:when>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/evaluateMatchingTerm.jsp#2 $$Change: 651448 $--%>
