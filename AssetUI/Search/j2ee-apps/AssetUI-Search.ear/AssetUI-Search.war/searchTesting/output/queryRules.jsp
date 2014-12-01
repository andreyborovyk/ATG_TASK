<%--
  Query Rules section of the output.
  Displays exclusion, positioning, redirection and admin rules.

  Included into results.jsp.

  The following parameters are passed into the page:

  @param formHandler            SearchTestingFormHandler instance
  @param searchResponse         search response
  @param exclusionRulesCount    number of exclusion rules
  @param exclusionRules         exclusion rules collection from search response object
  @param positioningRulesCount  number of positioning rules
  @param positioningRules       positioning rules collection from search response object


  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/queryRules.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<dspel:page>
  <dspel:getvalueof param="formHandler" var="formHandler"/>
  <dspel:getvalueof param="searchResponse" var="queryResponse"/>
  <dspel:getvalueof param="exclusionRulesCount" var="exclusionRulesCount"/>
  <dspel:getvalueof param="exclusionRules" var="exclusionRules"/>
  <dspel:getvalueof param="positioningRulesCount" var="positioningRulesCount"/>
  <dspel:getvalueof param="positioningRules" var="positioningRules"/>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:if test="${altFieldSet}">
    <fieldset class="altGroup">
  </c:if>
  <c:if test="${not altFieldSet}">
    <fieldset>
  </c:if>
  <c:set var="altFieldSet" value="${!altFieldSet}" scope="request" />

    <legend><span>
      <fmt:message key='searchTestResult.queryRules.legend'/>
    </span></legend>

    <assetui-search:getRedirectionRules queryActions="${queryResponse.queryAction}" ruleAppendix="${queryResponse.ruleAppendix}" 
      var="redirectionRules"/>
    <assetui-search:getSearchAdminQueryRules ruleAppendix="${queryResponse.ruleAppendix}" var="searchAdminRules"/>

    <c:set var="noQueryRules" value="true"/>
    <c:if test="${not empty redirectionRules}">
      <dspel:include page="redirectionRules.jsp">
        <dspel:param name="redirectionRules" value="${redirectionRules}"/>
      </dspel:include>
      <c:set var="noQueryRules" value="false"/>
    </c:if>
    <c:if test="${exclusionRulesCount > 0}">
      <dspel:include page="exclusionRules.jsp">
        <dspel:param name="queryResponse" value="${queryResponse}"/>
        <dspel:param name="exclusionRules" value="${exclusionRules}"/>
        <dspel:param name="debugExclusion" value="${formHandler.debugExclusion}"/>
      </dspel:include>
      <c:set var="noQueryRules" value="false"/>
    </c:if>
    <c:if test="${positioningRulesCount > 0}">
      <dspel:include page="positioningRules.jsp">
        <dspel:param name="positioningRules" value="${positioningRules}"/>
        <dspel:param name="queryResponse" value="${queryResponse}"/>
        <dspel:param name="disablePositioning" value="${formHandler.disablePositioning}"/>
      </dspel:include>
      <c:set var="noQueryRules" value="false"/>
    </c:if>
    <c:if test="${not empty searchAdminRules}">
      <dspel:include page="powerQueryRules.jsp">
        <dspel:param name="searchAdminRules" value="${searchAdminRules}"/>
      </dspel:include>
      <c:set var="noQueryRules" value="false"/>
    </c:if>
    <c:if test="${noQueryRules}">
      <p>
        <fmt:message key="searchTestResult.noQueryRules"/>
      </p>
    </c:if>
  </fieldset>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/output/queryRules.jsp#2 $$Change: 651448 $--%>
