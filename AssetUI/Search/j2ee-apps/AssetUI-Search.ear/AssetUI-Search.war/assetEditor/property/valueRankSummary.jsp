<%--
  Page fragment for displaying the summary of a value rank.  It displays  
  briefly the type of ranking being performed for this property.  For each
  type, here are the possible values we will display.

  Text
    - Ranked list of text items
  Enum
    - Ranked list of text items
  Number
    - Higher values are better
    - Lower values are better
    - Ranked list of number ranges
  Date
    - Newer values are better
    - Older values are better
    - Ranked list of date ranges

  This fragment is included  by searchConfigPropertyWeightingEditor.jsp 
  and propertyWeightingComponentEditor.jsp.

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  The following parameters are passed into the page:

  @param rankPropURI   The uri of the asset of type rankingProperty that 
                       we will display a summary of. 


  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/valueRankSummary.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

  <%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                    %>
  <%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
  <%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
  <%@ taglib prefix="asset-ui"       uri="http://www.atg.com/taglibs/asset-ui"              %>
  <%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

  <dspel:page>

    <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
    <fmt:setBundle basename="${config.resourceBundle}"/>

    <%-- unpack page parameters --%>
    <dspel:getvalueof var="rankPropURI" param="assetURI"/>
 
    <%-- get the rankingProperty asset --%>
    <asset-ui:resolveAsset var="rankingPropertyAssetWrapper" uri="${rankPropURI}"/>

    <%-- get the appropriate string for the given datatype and range method --%>
    <assetui-search:getRankingPropertySummary var="ranksummary" item="${rankingPropertyAssetWrapper.asset}" bundle="${config.resourceBundle}" />

    <%-- if no valueRanks are defined, warn --%>
    <c:if test="${empty ranksummary}">
      <%-- Display the warning as a link to the rankingProperty item page --%>
      <a href="javascript:<c:out value='drillDownFunc("${rankPropURI}")'/>">
        <span class="atg_smerch_property_conflictField">
          <fmt:message key="incompleteValueRanks" />
        </span>
      </a>
    </c:if>

    <%-- Display the rank summary as a link to the rankingProperty item page --%>
    <a href="javascript:<c:out value='drillDownFunc("${rankPropURI}")'/>">
      <c:out value="${ranksummary}"/>
    </a>

  </dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/valueRankSummary.jsp#2 $$Change: 651448 $--%>
