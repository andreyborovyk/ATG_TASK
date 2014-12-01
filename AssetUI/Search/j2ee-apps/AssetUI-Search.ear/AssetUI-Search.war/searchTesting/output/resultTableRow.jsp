<%--
  This page is a single row placeholder in the result table

  This page is included in resultTable.jsp and can be overridden in 
  /atg/search/web/assetmanager/SearchTestingFormHandler.pageConfigMap.resultTableRowPage

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/resultTableRow.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="propertyManager" bean="/atg/search/repository/SearchTestingPropertyManager"/>

  <dspel:getvalueof var="disablePositioning" param="disablePositioning"/>
  <dspel:getvalueof var="debugExclusion" param="debugExclusion"/>
  <dspel:getvalueof var="resultItem" param="resultItem"/>
  <dspel:getvalueof var="index" param="index"/>
  <dspel:getvalueof var="firstOutputedItemNumber" param="firstOutputedItemNumber"/>
  <dspel:getvalueof var="secondaryPage" param="secondaryPage"/>
  <dspel:getvalueof var="secondaryContext" param="secondaryContext"/>
  <dspel:getvalueof var="multiLanguage" param="multiLanguage"/>
  <dspel:getvalueof var="resultMode" param="resultMode"/>

  <c:choose>
    <c:when test="${formHandler.subitemsValid}">
      <c:set var="currentItem" value="${resultItem[0]}"/>
      <%-- check if group is excluded --%>
      <assetui-search:isGroupExcluded subItems="${resultItem}" var="isItemExcluded" />
    </c:when>
    <c:otherwise>
      <c:set var="currentItem" value="${resultItem}"/>
      <c:set var="isItemExcluded" value="${currentItem.excludedName != null}"/>
    </c:otherwise>
  </c:choose>

  <c:choose>
    <c:when test="${0 == (index mod 2)}">
      <tr class="atg_altRow">
    </c:when>
    <c:otherwise>
      <tr>
    </c:otherwise>
  </c:choose>
    <td>
      <c:out value="${firstOutputedItemNumber + index}"/>
    </td>
  </tr>
  <c:choose>
    <c:when test="${0 == (index mod 2)}">
      <tr class="atg_altRow atg_secondaryProp">
    </c:when>
    <c:otherwise>
      <tr class="atg_secondaryProp">
    </c:otherwise>
  </c:choose>
    <td colspan="6">
      <dspel:include page="${secondaryPage}" otherContext="${secondaryContext}">
        <dspel:param name="debugExclusion" value="${debugExclusion}"/>
        <dspel:param name="disablePositioning" value="${disablePositioning}"/>
        <dspel:param name="resultItem" value="${resultItem}"/>
        <dspel:param name="rankConfig" value="${currentItem.rankConfig}"/>
        <dspel:param name="minScore" value="${0}"/>
      </dspel:include>
    </td>
  </tr>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/resultTableRow.jsp#2 $$Change: 651448 $--%>
