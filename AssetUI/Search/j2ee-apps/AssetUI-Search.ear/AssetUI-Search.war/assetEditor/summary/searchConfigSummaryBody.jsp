<%--
  This is the search configuration body cell in the SearchConfig Summary column of the summary table

  @param  ivm   MappedItemView
  
  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/summary/searchConfigSummaryBody.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>

<dspel:page>
  <dspel:importbean var="config"
                    bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="ivm" param="ivm"/>
  
  <dspel:droplet name="/atg/search/web/droplet/SearchConfigSummary">
    <%-- Input parameters --%>
    <dspel:param name="item" value="${requestScope.atgCurrentAsset.asset}"/>
    <dspel:param name="tab"   value="${ivm.displayName}"/>
    
    <%-- Open parameters --%>
    <dspel:oparam name="empty">
    </dspel:oparam>

    <dspel:oparam name="redirectionRules">
      <dspel:valueof param="element"/> <fmt:message bundle="${bundle}" key="searchConfigSummaryBody.rules"/>
    </dspel:oparam>

    <dspel:oparam name="propertyWeighting">
      <dspel:getvalueof var="useBase" param="useBase"/> 
      <dspel:getvalueof var="element" param="element"/> 
      <dspel:getvalueof var="error"   param="error"/> 
      <c:choose>
        <c:when test="${!empty useBase}">
          <fmt:message bundle="${bundle}" key="searchConfigSummaryBody.useBaseConfiguration"/>
        </c:when>
        <c:when test="${!empty element}">
          <c:out value="${element}"/> <fmt:message bundle="${bundle}" key="searchConfigSummaryBody.properties"/>
        </c:when>
        <c:when test="${!empty error}">
          <fmt:message bundle="${bundle}" key="searchConfigSummaryBody.error"/>
        </c:when>
      </c:choose>
    </dspel:oparam>

    <dspel:oparam name="exclusionRules">
      <dspel:valueof param="element"/> <fmt:message bundle="${bundle}" key="searchConfigSummaryBody.rules"/>
    </dspel:oparam>

    <dspel:oparam name="positioningRules">
      <dspel:valueof param="element"/> <fmt:message bundle="${bundle}" key="searchConfigSummaryBody.rules"/>
    </dspel:oparam>
  </dspel:droplet>
  
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/summary/searchConfigSummaryBody.jsp#2 $$Change: 651448 $--%>
