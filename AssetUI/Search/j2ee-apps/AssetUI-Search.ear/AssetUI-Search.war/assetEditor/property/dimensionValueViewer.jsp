<%--
  property viewer for dimension values

  The following request-scoped variables are expected to be set:
  
  @param  mpv  A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/dimensionValueViewer.jsp#1 $$Change: 651360 $
  @updated $DateTime: 2011/06/07 09:18:53 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  
  <%-- Get the parent dimension service --%>
  <assetui-search:getDimensionValueChoices var="dimensionValueChoicesResult" item="${requestScope.atgCurrentAsset.asset}"/>
  
  <c:if test="${dimensionValueChoicesResult != null}">
    <%-- <td> supplied by propertyContainer.jsp --%>
      <%-- Display the parent dimension service name --%>
      <div class="formLabel">
        <c:out value="${dimensionValueChoicesResult.searchDimensionDisplayName}"/>:
      </div>
    </td>
    <td>
      <%-- Display the property value --%>
      <dspel:getvalueof var="propVal" bean="${propertyView.formHandlerProperty}"/>
      <c:choose>
        <c:when test="${empty propVal}">
          <fmt:message key="dimensionValueEditor.allOthers"/>
        </c:when>
        <c:otherwise>
          <%-- Iterate over each of the possible values --%>
          <c:forEach var="key" items="${dimensionValueChoicesResult.dimensionValueChoiceKeys}" varStatus="i">
            <c:if test="${propVal == key}">
              <c:out value="${dimensionValueChoicesResult.dimensionValueChoiceDisplayNames[i.index]}"/>
            </c:if>
          </c:forEach>
        </c:otherwise>
      </c:choose>
    <%-- </td> supplied by propertyContainer.jsp --%>
  </c:if>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/dimensionValueViewer.jsp#1 $$Change: 651360 $--%>
