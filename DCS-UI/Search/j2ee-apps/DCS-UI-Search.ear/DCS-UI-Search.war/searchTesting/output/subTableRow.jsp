<%--
  This page is a single row for the sub-item table

  This page is included in subTable.jsp and can be overridden in 
  /atg/search/web/assetmanager/SearchTestingFormHandler.pageConfigMap.subTableRowPage

  @param  result                  result item
  @param  disablePositioning      true if positioning rules are disabled
  @param  debugExclusion          true if excluded items are shown

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/output/subTableRow.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <dspel:importbean var="propertyMappingBean" bean="/atg/search/testing/SearchTestingUIPropertyMapping" />
  <c:set var="mapping" value="${propertyMappingBean.propertyMapping}" />

  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="ASSETUI_SEARCH_CONTEXT"
      value="/AssetUI-Search"/>

  <dspel:getvalueof var="result" param="result"/>
  <dspel:getvalueof var="debugExclusion" param="debugExclusion"/>
  <dspel:getvalueof var="disablePositioning" param="disablePositioning"/>
  <dspel:getvalueof var="tracedResults" param="tracedResults"/>

  <c:url value="/searchTesting/output/contentItem.jsp" context="/AssetUI-Search" var="itemUrl">
    <c:param name="docUrl" value="${result.document.url}"/>
    <c:param name="idPropertyName" value="${mapping.subItemId}"/>
    <c:param name="namePropertyDisplayName" value="${mapping.viewSubItemDisplayName}"/>
  </c:url>    
  <tr>
    <c:if test="${! empty tracedResults}">
      <td class="atg_iconCell">
        <c:if test="${tracedResults[result.document.url] != null}">
  
          <dspel:include page="trackedItemIcon.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
            <dspel:param name="item" value="${tracedResults[result.document.url]}"/>
          </dspel:include>
        </c:if>
      </td>
    </c:if>
    <td>
      <c:choose>
        <%--excluded by rule shown as striked text--%>
        <c:when test="${(debugExclusion) && (result.excludedName != null)}">
          <del class="atg_exclusion" title="<fmt:message key='searchTestResult.excludedItem.title'/>">
            <a href="<c:out value='${itemUrl}'/>" class="atg_smerch_ciPop">
              <dspel:include page="propertyValueAsText.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
                <dspel:param name="propertyValue"
                    value="${result.document.properties[mapping.subItemDisplayName]}"/>
              </dspel:include>
            </a>
          </del>
        </c:when>
        <c:otherwise>
          <%--sku name as a link--%>
          <a href="<c:out value='${itemUrl}'/>" class="atg_smerch_ciPop">
            <dspel:include page="propertyValueAsText.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
              <dspel:param name="propertyValue"
                  value="${result.document.properties[mapping.subItemDisplayName]}"/>
            </dspel:include>
          </a>
        </c:otherwise>
      </c:choose>
    </td>
    <td>
      <c:out value="${result.document.properties[mapping.subItemId]}"/>
    </td>
    <td>
      <%--<c:out value="${result.document.properties[mapping.subItemSize]}"/>--%>
      <dspel:include page="propertyValueAsText.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
        <dspel:param name="propertyValue" value="${result.document.properties[mapping.subItemSize]}"/>
      </dspel:include>
    </td>
    <td>
      <%--<c:out value="${result.document.properties[mapping.subItemColor]}"/>--%>
      <dspel:include page="propertyValueAsText.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
        <dspel:param name="propertyValue" value="${result.document.properties[mapping.subItemColor]}"/>
      </dspel:include>
    </td>
    <td class="atg_numberValue">
      <%--<c:out value="${result.document.properties[mapping.subItemPrice]}"/>--%>
      <dspel:include page="propertyValueAsText.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
        <dspel:param name="propertyValue" value="${result.document.properties[mapping.subItemPrice]}"/>
      </dspel:include>
    </td>
    <td class="atg_numberValue">
      <c:choose>
        <c:when test="${!disablePositioning && result.moveRuleName != null}">
          <span class="atg_positionUp" title="<fmt:message key='searchTestResult.repositionedItem.title'/>">
            <fmt:formatNumber type="number"
                minFractionDigits="1"
                maxFractionDigits="1"
                value="${(result.score / 10)}"/>
          </span>
        </c:when>
        <c:otherwise>
          <fmt:formatNumber type="number"
              minFractionDigits="1"
              maxFractionDigits="1"
              value="${(result.score / 10)}"/>
        </c:otherwise>
      </c:choose>
    </td>
  </tr>
</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/output/subTableRow.jsp#2 $$Change: 651448 $--%>
