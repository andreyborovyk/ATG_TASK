<%--
  Sub-item table page.
  
  Includes sub-item table header and row as configured in formHandler.pageConfigMap.

  Can be included as a secondary page into resultTableRow.jsp.

  The following parameters are passed into the page:

  @param resultItem             search result item (top-level, product for skus groued by products configuration)
  @param debugExclusion         true if excluded items are shown
  @param disablePositioning     true if positioning rules are disabled

  The following request-scoped variables are expected to be set:

  @param formHandler            SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/subTable.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="resultItem" param="resultItem"/>
  <dspel:getvalueof var="debugExclusion" param="debugExclusion"/>
  <dspel:getvalueof var="disablePositioning" param="disablePositioning"/>
  
  <table class="atg_dataTable atg_smerch_summaryTable">

    <c:if test="${formHandler.searchResponse.diagnostics != null && formHandler.searchResponse.diagnostics.trace != null 
      && formHandler.searchResponse.diagnostics.trace.items != null}">
      <assetui-search:getTracedResults resultItems="${resultItem}" tracedItems="${formHandler.searchResponse.diagnostics.trace.items}" 
      var="tracedResults"/>
    </c:if>

    <dspel:include page="${formHandler.pageConfigMap.subTableHeaderPage}" 
                   otherContext="${formHandler.pageConfigMap.subTableHeaderContext}">
        <dspel:param name="areThereTrackedItems" value="${! empty tracedResults}"/>
    </dspel:include>

    <c:forEach items="${resultItem}" var="result">
      <dspel:include page="${formHandler.pageConfigMap.subTableRowPage}" 
                     otherContext="${formHandler.pageConfigMap.subTableRowContext}">
        <dspel:param name="result" value="${result}" />
        <dspel:param name="debugExclusion" value="${debugExclusion}" />
        <dspel:param name="disablePositioning" value="${disablePositioning}" />
        <dspel:param name="tracedResults" value="${tracedResults}" />
      </dspel:include>
    </c:forEach>
  </table>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/subTable.jsp#2 $$Change: 651448 $--%>
