<%--
  This is the last cell in the footer row of the search config summary table

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/summary/searchConfigSummaryFooterCellFinal.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>
  <%-- Get our MultiBundleFormatter --%>
  <c:set var="formatterPath" value="/atg/core/i18n/MultiBundleFormatter"/>
  <dspel:importbean var="formatter" bean="${formatterPath}"/>

  <asset-ui:getResourceValue formatter="${formatter}" key="searchConfigSummaryFooterCellFinal.bodyText"/>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/summary/searchConfigSummaryFooterCellFinal.jsp#2 $$Change: 651448 $--%>
