<%--
  This is the default description body cell in the Description column of the summary table

  @param  ivm   MappedItemView
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/summary/tabDescriptionBody.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"           %>

<dspel:page>
  <%-- Get our MultiBundleFormatter --%>
  <c:set var="formatterPath" value="/atg/core/i18n/MultiBundleFormatter"/>
  <dspel:importbean var="formatter" bean="${formatterPath}"/>

  <dspel:getvalueof var="displayKey" param="ivm.displayName"/>
  <c:set var="resourceKey" value="tabDescriptionBody.bodyText.${displayKey}"/>

  <asset-ui:getResourceValue var="description" formatter="${formatter}" key="${resourceKey}"/>
  <c:if test="${description == resourceKey || empty description}">
    <asset-ui:getResourceValue var="description" formatter="${formatter}" key="tabDescriptionBody.bodyText"/>
  </c:if>
  
  <c:out value="${description}"/>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/summary/tabDescriptionBody.jsp#2 $$Change: 651448 $--%>
