<%--
  Default property editor for enumerated values.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/searchDimensionEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"               %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <%-- Get an ID for the property's input element --%>  
  <c:set var="inputId" value="propertyValue_${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>  

  <%-- Get the current property value --%>
  <dspel:getvalueof var="propVal" bean="${propertyView.formHandlerProperty}"/>

  <assetui-search:getSearchDimensionChoices var="searchDimensionChoicesResult" item="${requestScope.atgCurrentAsset.asset}" parentItem="${formHandler.parentRepositoryItem}"/>

  <c:if test="${searchDimensionChoicesResult != null}">

    <c:set var="showHideSearchDimSelector" value="inline"/>
    <c:if test="${not empty searchDimensionChoicesResult.readOnlyValue}">
      <span id="searchDimensionReadOnlyValue">
        <c:out value="${searchDimensionChoicesResult.readOnlyValue}"/>
      </span>
      <c:set var="showHideSearchDimSelector" value="none"/>
    </c:if>  

    <span id="searchDimensionSelector" style="display:<c:out value='${showHideSearchDimSelector}'/>" >

      <%-- Display the property value in a select box --%>
      <dspel:select id="${inputId}"
                    bean="${propertyView.formHandlerProperty}"
                    onchange="markAssetModified()"
                    converter="nullable">


        <%-- Iterate over each of the possible values --%>
        <c:forEach var="choice" items="${searchDimensionChoicesResult.searchDimensionChoicesMap}">
 
          <c:set var="selected" value="${propVal == choice.key}"/>
 
          <dspel:option value="${choice.key}" selected="${selected}">
            <c:out value="${choice.value}"/>
          </dspel:option>

        </c:forEach>

      </dspel:select>  
 
    </span>

  </c:if>  

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/searchDimensionEditor.jsp#2 $$Change: 651448 $--%>

