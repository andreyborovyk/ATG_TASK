<%--
  This jsp fragment displays property value or "N/A" if it's empty

  It is included in the following pages: 
    resultTableRow.jsp
    resultTableSecondaryProperties.jsp
    subTableRow.jsp
    calculateItemScore.jsp
    trackedItemDetailsBaseTable.jsp

  The following parameters are passed into the page:

  @param propertyValue  property value

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/propertyValueAsText.jsp#2 $$Change: 651448 $
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

  <dspel:getvalueof var="propValue" param="propertyValue"/>

  <c:choose>
    <c:when test="${not (empty propValue)}">
      <c:out value="${propValue}"/>
    </c:when>
    <c:otherwise>
      <fmt:message key="searchTestingPropertyValueAsText.propertyValueNotAvailable"/>
    </c:otherwise>
  </c:choose>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/propertyValueAsText.jsp#2 $$Change: 651448 $--%>
