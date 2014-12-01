<%--
  This is the default summary body cell in the Summary column of the summary table

  @param  ivm   MappedItemView
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/summary/tabSummaryBody.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"           %>

<dspel:page>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="ivm"   param="ivm"/>
  <dspel:getvalueof var="tabId" param="tabId"/>

  <c:set var="bundleName" value="${ivm.attributes.resourceBundle}"/>
  <c:choose>
    <c:when test="${not empty bundleName}">
      <fmt:setBundle var="resBundle" basename="${bundleName}"/>
      <fmt:message var="tabDisplayName" key="${ivm.displayName}" bundle="${resBundle}"/>
    </c:when>
    <c:otherwise>
      <c:set var="tabDisplayName" value="${ivm.displayName}"/>
    </c:otherwise>
  </c:choose>
  
  <a href="javascript:submitViewChange('<c:out value="${tabId}"/>')"><c:out value="${tabDisplayName}"/></a>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/summary/tabSummaryBody.jsp#2 $$Change: 651448 $--%>
