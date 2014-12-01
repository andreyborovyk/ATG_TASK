<%--
  This page is a secondary properties section placeholder for the result

  OOTB this page is included in resultTableRow.jsp and can be overridden in 
  /atg/search/web/assetmanager/SearchTestingFormHandler.pageConfigMap.resultTableSecondaryPropertiesPage

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/resultTableSecondaryProperties.jsp#2 $$Change: 651448 $
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
  <c:choose>
    <c:when test="${formHandler.subitemsValid}">
      <c:set var="currentItem" value="${resultItem[0]}"/>
    </c:when>
    <c:otherwise>
      <c:set var="currentItem" value="${resultItem}"/>
    </c:otherwise>
  </c:choose>

  <dl class="atg_smerch_configList">
  </dl>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/resultTableSecondaryProperties.jsp#2 $$Change: 651448 $--%>
