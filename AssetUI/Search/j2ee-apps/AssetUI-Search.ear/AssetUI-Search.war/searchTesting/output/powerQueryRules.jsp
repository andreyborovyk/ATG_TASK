<%--
  Admin rules section of the query rule output (rules created in Search Admin)

  Included into queryRules.jsp.

  The following parameters are passed into the page:

  @param searchAdminRules rules collection

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/powerQueryRules.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<dspel:page>
  <dspel:getvalueof param="searchAdminRules" var="searchAdminRules"/>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <table class="atg_dataTable atg_smerch_summaryTable">
    <tr>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestingResultPowerQueryRules.rulesActivated.title"/>
      </th>
      <th class="atg_smerch_summaryTitle">
        <fmt:message key="searchTestingResultPowerQueryRules.rulesSource.title"/>
      </th>
    </tr>
    <c:set var="multipleItems" value="false"/>
    <c:forEach items="${searchAdminRules}" var="searchAdminRule" varStatus="status">
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
          <c:choose>
            <c:when test="${searchAdminRule.patternsCount > 1}">
              <fmt:message key="searchTestingResultPowerQueryRules.patterns">
                <fmt:param value="${searchAdminRule.patternsCount}"/>
              </fmt:message>
              <c:set var="multipleItems" value="true"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="searchTestingResultPowerQueryRules.pattern"/>
            </c:otherwise>
          </c:choose>
          <c:out value="${searchAdminRule.pattern}"/>
          <br/>
          <c:choose>
            <c:when test="${searchAdminRule.actionsCount > 1}">
              <fmt:message key="searchTestingResultPowerQueryRules.actions">
                <fmt:param value="${searchAdminRule.actionsCount}"/>
              </fmt:message>
              <c:set var="multipleItems" value="true"/>
            </c:when>
            <c:otherwise>
              <fmt:message key="searchTestingResultPowerQueryRules.action"/>
            </c:otherwise>
          </c:choose>
          <c:out value="${searchAdminRule.action}"/>
        </td>
        <td>
          <c:out value="${searchAdminRule.path}"/>
        </td>
      </tr>
    </c:forEach>
  </table>
  <c:if test="${multipleItems}">
    <p class="atg_tableNote"><fmt:message key="searchTestingResultPowerQueryRules.onlyFirst.note"/></p>
  </c:if>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/powerQueryRules.jsp#2 $$Change: 651448 $--%>
