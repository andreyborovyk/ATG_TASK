<%--
  This page displays an icon for the tracked item,
  returned from the search engine, which indicates the item position in the result set
  (on the forst page, in the list, not in the list)

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response.   

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/trackedItemIcon.jsp#2 $$Change: 651448 $
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

  <c:choose>
    <c:when test="${'succeed' ne item.summary.result}">
      <span class="atg_tableIcon atg_trackedItem_notIncluded" title="<fmt:message key="searchTestingTrackedItemIcon.notIncluded.title"/>">&nbsp;</span>
    </c:when>
    <c:otherwise>
      <c:choose>
          <c:when test="${not (empty item.resultCollection.grouping.groups)
                        and (1 == item.resultCollection.grouping.groups[0].page)}">
          <span class="atg_tableIcon atg_trackedItem_firstPage" title="<fmt:message key="searchTestingTrackedItemIcon.firstPage.title"/>">&nbsp;</span>
        </c:when>
        <c:otherwise>
          <span class="atg_tableIcon atg_trackedItem_notFirstPage" title="<fmt:message key="searchTestingTrackedItemIcon.notFirstPage.title"/>">&nbsp;</span>
        </c:otherwise>
      </c:choose>
    </c:otherwise>
  </c:choose>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/trackedItemIcon.jsp#2 $$Change: 651448 $--%>
