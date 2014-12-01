<%--
  This page is a header placehoder for the sub-item table

  This page is included in subTable.jsp and can be overridden in 
  /atg/search/web/assetmanager/SearchTestingFormHandler.pageConfigMap.subTableHeaderPage

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/subTableHeader.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <tr>
    <th class="atg_smerch_summaryTitle">
      <fmt:message key="searchTestingResultSubItems.subItemTableHeader"/>
    </th>
  </tr>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/subTableHeader.jsp#2 $$Change: 651448 $--%>
