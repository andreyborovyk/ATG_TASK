<%--
  The locale drop-down selector

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/localeSelector.jsp#2 $$Change: 651448 $
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
  
  <fmt:setBundle var="assetUIBundle" basename="${config.resourceBundle}"/>
  <fmt:setBundle var="refinementBundle" basename="atg.repository.search.refinement.RefinementRepositoryTemplateResources"/>

  <%-- Get the default LanguageDimensionService --%>
  <c:set var="searchDimSerivcePath" value="/atg/search/config/SearchDimensionManagerService"/>
  <dspel:importbean var="searchDimService" bean="${searchDimSerivcePath}"/>
  <c:set var="languageDimService"   value="${searchDimService.languageDimensionService}"/>

  <%-- Get a list of possible language dimension values --%>
  <c:set var="locales"              value="${languageDimService.locales}"/>
  
  <%-- Get the current property value --%>
  <dspel:getvalueof var="propVal" bean="${propertyView.formHandlerProperty}"/>

  <%-- Get an ID for the input field --%>
  <c:set var="inputId" value="propertyValue_${propertyView.uniqueId}"/>
  
  <%-- Display the property value in a select box --%>
  <dspel:select id="${inputId}"
                bean="${propertyView.formHandlerProperty}"
                onchange="markAssetModified()">

    <%-- Check the null case --%>
    <c:set var="isNull" value="${empty propVal}"/>
    <dspel:option value="null" selected="${isNull}">
      <fmt:message bundle="${assetUIBundle}" key="languageEditor.languageNotSpecified" />
    </dspel:option>

    <c:forEach var="locale" items="${locales}">
      <assetui-search:getLocaleName var="localeName" locale="${locale}"/>

      <%-- Determine if this is the current value. --%>
      <c:set var="isSelected" value="${propVal == locale}"/>

      <dspel:option value="${locale}" selected="${isSelected}">
        <c:out value="${localeName}"/>
      </dspel:option>
    </c:forEach>
  </dspel:select>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/localeSelector.jsp#2 $$Change: 651448 $--%>
