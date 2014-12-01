<%--
  This page provides the information about the 'item constraints checking'
  for the result per-stage tracking information.

  Included into trackedItemDetailsBaseTable.jsp.

  The following parameters are passed into the page:

  @param item   Tracked item object from the search response. 

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/checkItemConstraints.jsp#2 $$Change: 651448 $
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

  <c:choose>
    <c:when test="${'succeed' eq item.summary.constraint}">
      <p class="atg_includedConstraint">
        <fmt:message key="searchTestingCheckItemConstraints.includedConstraintTitle"/>
      </p>
    </c:when>
    <c:otherwise>
      <p class="atg_excludedConstraint">
        <fmt:message key="searchTestingCheckItemConstraints.excludedConstraintTitle"/>
      </p>
    </c:otherwise>
  </c:choose>
  <c:set var="constraints"
      value="${item.constraints.constraints}"/>
  <c:if test="${not (empty constraints)}">
    <table class="atg_dataTable atg_smerch_summaryTable">
      <tr>
        <th>
          <fmt:message key="searchTestingCheckItemConstraints.constraintTable.constraintTypeTitle"/>
        </th>
        <th>
          <fmt:message key="searchTestingCheckItemConstraints.constraintTable.constraintTitle"/>
        </th>
        <th>
          <fmt:message key="searchTestingCheckItemConstraints.constraintTable.metConstraintTitle"/>
        </th>
      </tr>
      <c:forEach items="${constraints}" var="constraint">
        <assetui-search:stringStartsWith string="${constraint.name}"
            prefix="rule"
            var="isRuleConstraint"/>
        <c:set var="constraintType"
            value=""/>
        <c:choose>
          <c:when test="${'base' eq constraint.name}">
            <fmt:message key="searchTestingCheckItemConstraints.constraintType.general"
                var="constraintType"/>
            <%--<c:set var="constraintType"--%>
                <%--value="General"/>--%>
          </c:when>
          <c:when test="${'refinement' eq constraint.name}">
            <fmt:message key="searchTestingCheckItemConstraints.constraintType.facet"
                var="constraintType"/>
            <%--<c:set var="constraintType"--%>
                <%--value="Facet"/>--%>
          </c:when>
          <c:when test="${isRuleConstraint}">
            <fmt:message key="searchTestingCheckItemConstraints.constraintType.rule"
                var="constraintType"/>
            <%--<c:set var="constraintType"--%>
                <%--value="Rule"/>--%>
          </c:when>
        </c:choose>
        <tr>
          <td>
            <c:out value="${constraintType}"/>
          </td>
          <c:choose>
            <c:when test="${'succeed' eq constraint.status}">
              <td class="atg_smerch_facetRules">
            </c:when>
            <c:otherwise>
              <td class="atg_smerch_facetRules notMet">
            </c:otherwise>
          </c:choose>
            <assetui-search:displayConstraint constraint="${constraint}"
                resourceBundle="${config.resourceBundle}"/>
          </td>
          <td>
            <c:choose>
              <c:when test="${'succeed' eq constraint.status}">
                <fmt:message key="searchTestingCheckItemConstraints.constraintStatus.success"/>
              </c:when>
              <c:otherwise>
                <fmt:message key="searchTestingCheckItemConstraints.constraintStatus.fail"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
    </table>
  </c:if>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/checkItemConstraints.jsp#2 $$Change: 651448 $--%>
