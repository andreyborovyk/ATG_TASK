<%--
  The inherited language property editor

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/inheritedLanguageEditor.jsp#2 $$Change: 651448 $
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

  <%-- Get the resource bundle used by the RefinementRepository --%>
  <fmt:setBundle var="refinementBundle" basename="atg.repository.search.refinement.RefinementRepositoryTemplateResources"/>

  <dspel:droplet name="/atg/search/web/droplet/SearchConfigLanguageSelection">
    <%-- Input Parameters --%>
    <dspel:param name="item"       value="${requestScope.atgCurrentAsset.asset}"/>
    <dspel:param name="parentItem" value="${requestScope.atgCurrentParentAsset.asset}"/>
    
    <%-- Output Parameters --%>
    <dspel:setvalue param="language" paramvalue="element"/>

    <%-- Open Parameters --%>
    <dspel:oparam name="empty">
    </dspel:oparam>

    <dspel:oparam name="outputStart">
      <%-- <td> supplied by propertyContainer.jsp --%>
        <div class="formLabel">
          <%-- Property name --%>
          <fmt:message bundle="${refinementBundle}" key="locale"/>:
        </div>
      </td>
      <td>
    </dspel:oparam>

    <dspel:oparam name="languageInherited">
      <%-- Language value --%>
      <dspel:valueof param="language"/>
    </dspel:oparam>

    <dspel:oparam name="languageNotInherited">
      <c:choose>
        <c:when test="${empty propertyView.attributes.readOnly}">
          <dspel:include page="localeSelector.jsp"/>:
        </c:when>
        <c:otherwise>
          <fmt:message bundle="${assetUIBundle}" key="inheritedLanguageEditor.languageNotInherited"/>
        </c:otherwise>
      </c:choose>
    </dspel:oparam>

    <dspel:oparam name="outputEnd">
      <%-- </td> supplied by propertyContainer.jsp --%>
    </dspel:oparam>
  </dspel:droplet>

  <c:set value="/atg/search/config/SearchDimensionManagerService" var="searchDimensionManagerServicePath"/>

  <%-- retrive list of ancestor dimension service/dimension values --%>
  <dspel:droplet name="/atg/search/web/droplet/AncestorDimensionsList">

    <%-- input parameters --%>
    <dspel:param name="item"       value="${requestScope.atgCurrentAsset.asset}"/>
    <dspel:param name="parentItem" value="${requestScope.atgCurrentParentAsset.asset}"/>

    <%-- Open Parameters --%>
    <dspel:oparam name="empty">
    </dspel:oparam>

    <dspel:oparam name="outputStart">
    </dspel:oparam>

    <dspel:oparam name="ancestorDimension">
      </td></tr><tr><td>
      <div class="formLabel">
        <dspel:getvalueof param="element.dimensionService" var="dimensionServiceName"/>
        <dspel:getvalueof bean="${searchDimensionManagerServicePath}.searchDimensionMap.${dimensionServiceName}" var="searchDimension"/>
        <c:out value="${searchDimension.displayName}"/>:
      </div>
      </td><td>
        <dspel:getvalueof param="element.dimensionValue" var="dimensionValue"/>
        <assetui-search:getDimensionDisplayValue var="displayValue" service="${dimensionServiceName}" value="${dimensionValue}"/>
        <c:out value="${displayValue}"/>
    </dspel:oparam>

    <dspel:oparam name="outputEnd">
       <!--</td> supplied by propertyContainer.jsp -->
    </dspel:oparam>
  </dspel:droplet>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/inheritedLanguageEditor.jsp#2 $$Change: 651448 $--%>
