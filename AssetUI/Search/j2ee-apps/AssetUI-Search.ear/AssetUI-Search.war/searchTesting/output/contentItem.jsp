<%--
  This page shows content item properties. 
  Opened from search test result list (resultTableRow.jsp or whatever is configured in pageConfigMap)

  The following parameters are passed into the page:

  @param docUrl document URL to get content item from search engine
  @param idPropertyName name of the ID property ($repositoryId/childSKUs.$repositoryId for products/skus)
  @param namePropertyDisplayName name of the display name property (displayName/childSKUs.displayName for products/skus)

@version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/contentItem.jsp#2 $$Change: 651448 $
@updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

<dspel:getvalueof param="docUrl" var="docUrl"/>
<dspel:getvalueof param="idPropertyName" var="idPropertyName"/>
<dspel:getvalueof param="namePropertyDisplayName" var="namePropertyDisplayName"/>

<dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
<fmt:setBundle basename="${config.resourceBundle}"/>

<c:set var="formHandlerPath" value="atg/search/web/assetmanager/SearchTestingFormHandler"/>
<dspel:getvalueof bean="${formHandlerPath}.value.queryText" var="queryText" />

<assetui-search:getContentItem documentUrl="${docUrl}" var="response"/>

<div class="contentItemContainer">

  <h3>
    <fmt:message key="searchTestContentItem.contentItem">
      <fmt:param value="${response.itemInspect.sentenceMap[namePropertyDisplayName][0].sentenceValue}"/>
      <fmt:param value="${response.itemInspect.properties[idPropertyName][0].value}"/>
    </fmt:message>
  </h3>

  <div class="searchTestingTableHeading">
    <fmt:message key="searchTestContentItem.metaProperty.title"/>
  </div>
  <table class="atg_dataTable atg_smerch_summaryTable">
    <tr>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestContentItem.property.title"/>
      </th>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestContentItem.value.title"/>
      </th>
    </tr>
    <c:set var="propIndex" value="0"/>
    <c:forEach items="${response.itemInspect.properties}" var="simplePropertyList">
      <c:forEach items="${simplePropertyList.value}" var="simpleProperty">
        <c:choose>
          <c:when test="${0 == (propIndex mod 2)}">
            <c:set var="trClass" value=""/>
          </c:when>
          <c:otherwise>
            <c:set var="trClass" value="atg_altRow"/>
          </c:otherwise>
        </c:choose>
        <tr class="<c:out value='${trClass}'/>">
          <td>
            <c:out value="${simpleProperty.name}"/>
          </td>
          <td>
            <assetui-search:propertyValueDisplayTextResolver propertyName="${simpleProperty.name}" propertyValue="${simpleProperty.value}" var="displayName"/>
            <assetui-search:highlighting hclass="highlight" pattern="${queryText}" text="${displayName}"/>
          </td>
        </tr>
        <c:set var="propIndex" value="${propIndex + 1}"/>

      </c:forEach>
    </c:forEach>
    <c:choose>
      <c:when test="${0 == (propIndex mod 2)}">
        <c:set var="oddClass" value=""/>
        <c:set var="evenClass" value="atg_altRow"/>
      </c:when>
      <c:otherwise>
        <c:set var="oddClass" value="atg_altRow"/>
        <c:set var="evenClass" value=""/>
      </c:otherwise>

    </c:choose>
    <tr class="<c:out value='${oddClass}'/>">
      <td>
        <fmt:message key="searchTestContentItem.type"/>
      </td>
      <td>
        <fmt:message key="searchTestContentItem.structured"/>
      </td>
    </tr>
    <tr class="<c:out value='${evenClass}'/>">
      <td>
        <fmt:message key="searchTestContentItem.language"/>
      </td>
      <td>
        <c:out value="${response.itemInspect.language}"/>
      </td>
    </tr>
    <tr class="<c:out value='${oddClass}'/>">
      <td>
        <fmt:message key="searchTestContentItem.format"/>
      </td>
      <td>
        <c:out value="${response.itemInspect.type}"/>
      </td>
    </tr>
    <tr class="<c:out value='${evenClass}'/>">
      <td>
        <fmt:message key="searchTestContentItem.itemSet"/>
      </td>
      <td>
        <c:out value="${response.itemInspect.docset}"/>
      </td>
    </tr>
  </table>

  <div class="searchTestingTableHeading">
    <fmt:message key="searchTestContentItem.searchableProperty.title"/>
  </div>
  <table class="atg_dataTable atg_smerch_summaryTable">
    <tr>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestContentItem.property.title"/>
      </th>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestContentItem.text.title"/>
      </th>
    </tr>
    <c:forEach items="${response.itemInspect.sentences}" var="searchableProperty" varStatus="status">
      <assetui-search:getSentenceFeature features="${searchableProperty.featureList}"
                                         featureName="role" var="featureValue"/>
      <c:choose>
        <c:when test="${0 == (status.index mod 2)}">
          <c:set var="trClass" value=""/>
        </c:when>
        <c:otherwise>
          <c:set var="trClass" value="atg_altRow"/>
        </c:otherwise>
      </c:choose>

      <tr class="<c:out value='${trClass}'/>">
        <td>
          <c:out value="${featureValue}"/>
        </td>
        <td>
          <assetui-search:highlighting hclass="highlight" pattern="${queryText}" text="${searchableProperty.sentenceValue}"/>
        </td>
      </tr>
    </c:forEach>
  </table>
</div>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/contentItem.jsp#2 $$Change: 651448 $--%>
