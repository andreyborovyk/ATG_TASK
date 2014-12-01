<%--
  This page provides the information if the tracked item is on the first page of the results list
  for the result per-stage tracking information.

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response.
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

  <c:if test="${'succeed' eq item.summary.result}">
    <c:choose>
      <c:when test="${not (empty item.resultCollection.grouping.groups)
          and (1 == item.resultCollection.grouping.groups[0].page)}">
        <p class="atg_includedConstraint">
          <fmt:message key="searchTestingAssembleFirstPageResults.included"/>
        </p>
      </c:when>
      <c:otherwise>
        <p class="atg_excludedConstraint">
          <fmt:message key="searchTestingAssembleFirstPageResults.excluded"/>
        </p>
      </c:otherwise>
    </c:choose>
  </c:if>  

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/assembleFirstPageResults.jsp#2 $$Change: 651448 $--%>
