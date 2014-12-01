<%--
  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/updateAssetPickerSiteFilter.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:getvalueof var="siteId" param="siteId"/>

  <dspel:importbean var="siteDimensionService" bean="/atg/search/config/SiteDimensionService"/>
  
  <assetui-search:isSearchDimensionTreeContainsService var="isSearchDimensionTreeContainsSite" service="${siteDimensionService.dimensionName}" />
  
  <c:if test="${isSearchDimensionTreeContainsSite}">
    <dspel:setvalue bean="/atg/search/web/browse/SearchConfigTreeSiteFilter.siteId" value="${siteId}"/>
  </c:if>

  <dspel:setvalue bean="/atg/commerce/web/browse/CatalogTreeSiteFilter.siteId" value="${siteId}"/>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/updateAssetPickerSiteFilter.jsp#2 $$Change: 651448 $--%>
