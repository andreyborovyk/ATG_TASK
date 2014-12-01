<%--
  This page provides the information about the sub-items if the result item,
  returned from the search engine.

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response.   

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/retrieveItemCandidate.jsp#2 $$Change: 651448 $
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

  <c:set var="cmt" value="${false}" scope="request"/>
  <c:choose>
    <c:when test="${item.itemRetrieval.termLookup.terms != null}">
      <c:forEach items="${item.itemRetrieval.termLookup.terms}" var="term">
        <c:if test="${'succeed' eq term.status}">
            <c:set var="cmt" value="${true}" scope="request"/>
        </c:if>
      </c:forEach>
      <c:if test="${not cmt}">
        <p class="atg_excludedConstraint">
          <fmt:message key="searchTestingRetrieveItemCandidate.excludedConstraint"/>
        </p>
      </c:if>
    </c:when>
    <c:otherwise>
      <%--<c:set var="containMatchingTerms" value="${not ('fail' eq item.summary.retrieval)}" scope="request"/>--%>
      <c:if test="${('fail' eq item.summary.retrieval)}">
        <p class="atg_excludedConstraint">
          <fmt:message key="searchTestingRetrieveItemCandidate.notInCategory"/>
        </p>
      </c:if>
    </c:otherwise>
  </c:choose>
  <c:set var="containMatchingTerms" value="${cmt or (not ('fail' eq item.summary.retrieval))}"
      scope="request"/>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/retrieveItemCandidate.jsp#2 $$Change: 651448 $--%>
