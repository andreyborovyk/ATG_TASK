<%--
  The language property viewer

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/languageViewer.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>
  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <dspel:importbean var="config"
                    bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Get the current property value --%>
  <dspel:getvalueof var="propVal" bean="${propertyView.formHandlerProperty}"/>
  
  <c:choose>
    <c:when test="${empty propVal}">
      <fmt:message key="languageEditor.languageNotSpecified" />
    </c:when>
    <c:otherwise>
      <assetui-search:getLocaleName locale="${propVal}"/>
    </c:otherwise>
  </c:choose>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/languageViewer.jsp#2 $$Change: 651448 $--%>
