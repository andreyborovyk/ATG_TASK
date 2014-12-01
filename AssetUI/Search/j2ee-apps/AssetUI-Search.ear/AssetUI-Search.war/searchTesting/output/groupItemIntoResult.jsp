<%--
  This page provides the information about the 'grouping property of the result items'
  for the result per-stage tracking information.

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response.   

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/groupItemIntoResult.jsp#2 $$Change: 651448 $
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

  <dspel:getvalueof var="item" param="item"/>

  <c:set var="queryResponse"
      value="${requestScope.formHandler.searchResponse}"/>
  <c:set var="isGroupingMode"
      value="${requestScope.formHandler.subitemsValid}"/>
  <c:if test="${(isGroupingMode)
      and ('succeed' eq item.summary.result)}">
    <c:set var="sortPropName" value=""/>
    <%--
        To retrieve the property name, which was used for grouping, we should parse a string of the following pattern:
        <property_type>:<property_name>:<extra_data>,
        where property_type is the data type of the property, e.g. 'string', property_name - property name (the data we are interested in),
        extra_data - search engige specific data.
        example: string:$repositoryId:1
    --%>
    <c:forTokens items="${queryResponse.sortProp}" delims=":" var="token" varStatus="status">
      <c:if test="${1 == status.index}">
        <c:set var="sortPropName" value="${token}"/>
      </c:if>
    </c:forTokens>
    <assetui-search:propertyDisplayNameResolver var="displayName"
        propertyName="${sortPropName}"/>
    <p class="atg_includedConstraint">
      <fmt:message key="searchTestingGroupItemIntoResult.itemGroupedBy">
        <fmt:param value="${item.resultCollection.grouping.mode}"/>
        <fmt:param value="${displayName}"/>
        <fmt:param value="${item.resultCollection.grouping.groups[0].value}"/>
      </fmt:message>
    </p>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/groupItemIntoResult.jsp#2 $$Change: 651448 $--%>
