<%--
  This page is a single row placeholder for the sub-item table

  This page is included in subTable.jsp and can be overridden in 
  /atg/search/web/assetmanager/SearchTestingFormHandler.pageConfigMap.subTableRowPage

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/subTableRow.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:getvalueof var="result" param="result"/>
  <dspel:getvalueof var="debugExclusion" param="debugExclusion"/>
  <dspel:getvalueof var="disablePositioning" param="disablePositioning"/>

  <tr>
    <td>&nbsp;
    </td>
  </tr>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/subTableRow.jsp#2 $$Change: 651448 $--%>
