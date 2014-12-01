<%--
  This page provides the information about the 'result collection'
  for the result per-stage tracking information.

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response.   

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/collectResult.jsp#2 $$Change: 651448 $
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

  <c:set var="controls"
      value="${item.resultCollection.resultListControls.resultListControls}"/>
  <c:set var="excludedByStatementMaximum"
      value="${'fail' eq controls['t'].status}"/>

  <c:set var="excludedByGroupMaximum"
      value="${((not (empty controls['doc'])) and ('fail' eq controls['doc'].status))
          or ((not (empty controls['ans'])) and ('fail' eq controls['ans'].status))
          or ((not (empty controls['prop'])) and ('fail' eq controls['prop'].status))}"/>
  <c:set var="groupMaximumValue"
      value=""/>
  <c:choose>
    <c:when test="${not (empty controls['doc'])}">
      <c:set var="groupMaximumValue"
          value="${controls['doc'].setting}"/>
    </c:when>
    <c:when test="${not (empty controls['ans'])}">
      <c:set var="groupMaximumValue"
          value="${controls['ans'].setting}"/>
    </c:when>
    <c:when test="${not (empty controls['prop'])}">
      <c:set var="groupMaximumValue"
          value="${controls['prop'].setting}"/>
    </c:when>
  </c:choose>

  <c:choose>
    <c:when test="${('fail' eq item.summary.result)
        and (excludedByGroupMaximum)}">
      <p class="atg_excludedConstraint">
        <fmt:message key="searchTestingCollectResult.excludedConstraintTitle.byGroupMaximum">
          <fmt:param value="${groupMaximumValue}"/>
        </fmt:message>
      </p>
    </c:when>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/collectResult.jsp#2 $$Change: 651448 $--%>
