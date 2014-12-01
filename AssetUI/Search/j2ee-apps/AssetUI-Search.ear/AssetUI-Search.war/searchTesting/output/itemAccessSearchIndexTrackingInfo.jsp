<%--
  This page provides the information about the 'accessibility of the search index'
  for the result per-stage tracking information.

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response.   

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/itemAccessSearchIndexTrackingInfo.jsp#2 $$Change: 651448 $
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

  <c:if test="${'fail' eq item.summary.indexed}">
    <p class="atg_excludedConstraint">
      <fmt:message key="searchTestingItemAccessSearchIndexTrackingInfo.excludedConstraintTitle"/>
    </p>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/itemAccessSearchIndexTrackingInfo.jsp#2 $$Change: 651448 $--%>
