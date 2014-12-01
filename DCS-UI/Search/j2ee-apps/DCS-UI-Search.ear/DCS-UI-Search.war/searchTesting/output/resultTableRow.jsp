<%--
  This page is a single row in the result table

  This page is included in resultTable.jsp and can be overridden in 
  /atg/search/web/assetmanager/SearchTestingFormHandler.pageConfigMap.resultTableRowPage

  @param  disablePositioning      true if positioning rules are disabled
  @param  debugExclusion          true if excluded items are shown
  @param  index                   index of the result (0,1,2...)
  @param  firstOutputedItemNumber index of the 1st item on the current page of results
  @param  secondaryPage           JSP that displays secondary information (secondary props or ranking calculations)
  @param  secondaryContext        context for the secondary page
  @param  multiLanguage           true if results can be in different languages
  @param  resultMode              if equals to propertyManager.subItemsResultsEnumValue, we dislpay sub-items

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/output/resultTableRow.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<%@ taglib prefix="ee"             uri="http://www.atg.com/taglibs/expreditor"       %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="propertyManager" bean="/atg/search/repository/SearchTestingPropertyManager"/>
  <dspel:importbean var="propertyMappingBean" bean="/atg/search/testing/SearchTestingUIPropertyMapping" />
  <c:set var="mapping" value="${propertyMappingBean.propertyMapping}" />

  <dspel:getvalueof var="disablePositioning" param="disablePositioning"/>
  <dspel:getvalueof var="debugExclusion" param="debugExclusion"/>
  <dspel:getvalueof var="resultItem" param="resultItem"/>
  <dspel:getvalueof var="index" param="index"/>
  <dspel:getvalueof var="firstOutputedItemNumber" param="firstOutputedItemNumber"/>
  <dspel:getvalueof var="secondaryPage" param="secondaryPage"/>
  <dspel:getvalueof var="secondaryContext" param="secondaryContext"/>
  <dspel:getvalueof var="multiLanguage" param="multiLanguage"/>
  <dspel:getvalueof var="resultMode" param="resultMode"/>
  <dspel:getvalueof var="tracedResults" param="tracedResults"/>

  <c:set var="ASSETUI_SEARCH_CONTEXT"
      value="/AssetUI-Search"/>

  <c:choose>
    <c:when test="${formHandler.subitemsValid}">
      <c:set var="currentItem" value="${resultItem[0]}"/>
      <c:set var="viewItemIdPropertyName" value="${mapping.subItemId}" />
      <c:set var="viewItemNamePropertyName" value="${mapping.viewSubItemDisplayName}" />
      <%-- check if group is excluded --%>
      <assetui-search:isGroupExcluded subItems="${resultItem}" var="isItemExcluded" />
    </c:when>
    <c:otherwise>
      <c:set var="currentItem" value="${resultItem}"/>
      <c:set var="isItemExcluded" value="${currentItem.excludedName != null}"/>
      <c:set var="viewItemIdPropertyName" value="${mapping.itemId}" />
      <c:set var="viewItemNamePropertyName" value="${mapping.viewItemDisplayName}" />
    </c:otherwise>
  </c:choose>

  <c:set var="finalScore" value="${currentItem.score / 10}" />

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
    <!-- trace icon of a result item -->
    <c:if test="${! formHandler.subitemsValid && ! empty tracedResults}">
      <td class="atg_iconCell">
        <c:if test="${tracedResults[currentItem.document.url] != null}">
          <dspel:include page="trackedItemIcon.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
            <dspel:param name="item" value="${tracedResults[currentItem.document.url]}"/>
          </dspel:include>
        </c:if>
      </td> 
    </c:if>
    <td>
      <c:url value="/searchTesting/output/contentItem.jsp" context="${ASSETUI_SEARCH_CONTEXT}" var="itemUrl">
        <c:param name="docUrl" value="${currentItem.document.url}"/>
        <c:param name="idPropertyName" value="${viewItemIdPropertyName}"/>
        <c:param name="namePropertyDisplayName" value="${viewItemNamePropertyName}"/>
      </c:url>

      <c:choose>
        <%--Show product name as plain text--%>
        <c:when test="${propertyManager.subItemsResultsEnumValue eq resultMode}">
          <c:set var="itemLabel">
            <dspel:include page="propertyValueAsText.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
              <dspel:param name="propertyValue" value="${currentItem.document.properties[mapping.itemDisplayName]}"/>
            </dspel:include>
          </c:set>
        </c:when>
        <%--Show sku name as link --%>
        <c:otherwise>
          <c:set var="itemLabel">
            <a href="<c:out value='${itemUrl}'/>" class="atg_smerch_ciPop">
              <dspel:include page="propertyValueAsText.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
                <dspel:param name="propertyValue" value="${currentItem.document.properties[mapping.itemDisplayName]}"/>
              </dspel:include>
            </a>
          </c:set>
        </c:otherwise>
      </c:choose>
      <c:choose>
        <%--excluded by rule shown as striked text--%>
        <c:when test="${(debugExclusion) && (isItemExcluded)}">
          <del class="atg_exclusion" title="<fmt:message key='searchTestResult.excludedItem.title'/>">
              <c:out value="${itemLabel}" escapeXml="false"/>
          </del>
        </c:when>
        <c:otherwise>
          <%--sku name as a link--%>
          <c:out value="${itemLabel}" escapeXml="false"/>
        </c:otherwise>
      </c:choose>
    </td>
    <c:if test="${multiLanguage}">
      <td>
        <assetui-search:getLanguageName language="${currentItem.document.language}" var="language" />
        <%--<c:out value="${language}"/>--%>
        <dspel:include page="propertyValueAsText.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
          <dspel:param name="propertyValue" value="${language}"/>
        </dspel:include>
      </td>
    </c:if>
    <td>
      <c:out value="${currentItem.document.properties[mapping.itemId]}"/>
    </td>
    <ee:isMultisiteMode var="multisiteMode"/>
    <c:if test="${multisiteMode}">
      <td>
        <c:forEach items="${currentItem.document.properties['$siteId']}" var="siteId">
          <assetui-search:propertyValueDisplayTextResolver propertyName="$siteId" propertyValue="${siteId}" var="propertyValueDisplayName"/>
          <c:out value="${propertyValueDisplayName}"/>
          <br/>
        </c:forEach>
      </td>
    </c:if>
    <td class="atg_numberValue">
      <c:choose>
        <c:when test="${!disablePositioning && currentItem.moveRuleName != null}">
          <span class="atg_positionUp" title="<fmt:message key='searchTestResult.repositionedItem.title'/>">
            <fmt:formatNumber type="number"
                minFractionDigits="1"
                maxFractionDigits="1"
                value="${finalScore}"/>
          </span>
        </c:when>
        <c:otherwise>
            <fmt:formatNumber type="number"
                minFractionDigits="1"
                maxFractionDigits="1"
                value="${finalScore}"/>
        </c:otherwise>
      </c:choose>
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
        <dspel:param name="finalScore" value="${finalScore}" />
      </dspel:include>
    </td>
  </tr>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/output/resultTableRow.jsp#2 $$Change: 651448 $--%>
