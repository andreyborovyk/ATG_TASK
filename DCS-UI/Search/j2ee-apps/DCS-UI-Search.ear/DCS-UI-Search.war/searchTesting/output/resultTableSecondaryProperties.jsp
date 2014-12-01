<%--
  This page is a secondary properties section for the result

  OOTB this page is included in resultTableRow.jsp and can be overridden in 
  /atg/search/web/assetmanager/SearchTestingFormHandler.pageConfigMap.resultTableSecondaryPropertiesPage

  @param resultItem result item

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/output/resultTableSecondaryProperties.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <dspel:importbean var="propertyMappingBean" bean="/atg/search/testing/SearchTestingUIPropertyMapping" />
  <c:set var="mapping" value="${propertyMappingBean.propertyMapping}" />

  <c:set var="ASSETUI_SEARCH_CONTEXT"
      value="/AssetUI-Search"/>

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
    <dt>
      <fmt:message key="searchTestingResultSecondaryProperties.descriptionProperty"/>:
    </dt>
    <dd>
      <%--<c:out value="${currentItem.document.properties['role:description']}"/>--%>
      <dspel:include page="propertyValueAsText.jsp" otherContext="${ASSETUI_SEARCH_CONTEXT}">
        <dspel:param name="propertyValue" value="${currentItem.document.properties[mapping.itemDescription]}"/>
      </dspel:include>
    </dd>
  </dl>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/output/resultTableSecondaryProperties.jsp#2 $$Change: 651448 $--%>
