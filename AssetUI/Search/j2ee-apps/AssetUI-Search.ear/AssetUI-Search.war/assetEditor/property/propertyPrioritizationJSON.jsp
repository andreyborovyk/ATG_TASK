<%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui"         uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui"       uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search"        %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${assetManagerConfig.sessionInfo}"/>
  <fmt:setBundle var="assetManagerBundle" basename="${assetManagerConfig.resourceBundle}"/>

  <dspel:importbean var="propManager" bean="/atg/search/repository/SearchConfigurationPropertyManager"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <dspel:getvalueof var="formHandler" bean="${requestScope.formHandlerPath}" />
  <c:set var="componentItemDescriptor" value="${propertyView.componentRepositoryItemDescriptor}"/>
  <c:set var="repPath" value="${componentItemDescriptor.repository.absoluteName}"/>
  <c:set var="repName" value="${componentItemDescriptor.repository.repositoryName}"/>

  <dspel:getvalueof var="rankProperties"
      bean="${propertyView.formHandlerProperty}"/>
  <assetui-search:getContainerSize var="rankPropertyCount"
      container="${rankProperties}"/>

  <c:set var="initialFieldWeighting" value="${formHandler.fieldWeighting}" />
  <c:set var="initialRelevanceWeighting" value="${formHandler.relevanceWeighting}" />
  <c:set var="initialPropertiesWeighting" value="${100 - initialRelevanceWeighting - initialFieldWeighting}" />
  <c:if test="${initialPropertiesWeighting lt 0}">
    <c:set var="initialPropertiesWeighting" value="${0}" />
  </c:if>

  <script type="text/javascript" charset="utf-8">
    atg.ppData = {
      "allotter":{
          "propertiesWeight": <c:out value="${initialPropertiesWeighting}" />,
          "propertiesWeightId": "weightPropertiesId",
          "overallTextRelevance": <c:out value="${initialRelevanceWeighting}" />,
          "overallTextRelevanceId": "weightRelevanceId",
          "textRelevanceByField": <c:out value="${initialFieldWeighting}" />,
          "textRelevanceByFieldId": "weightFieldId"
      },
      "properties":[
      <c:forEach var="rankProperty" items="${rankProperties}" varStatus="status">
        <dspel:getvalueof var="weightingValue"
            bean="${propertyView.formHandlerProperty}[${status.index}].weighting"/>
        <dspel:getvalueof var="repositoryId"
            bean="${propertyView.formHandlerProperty}[${status.index}].repositoryId"/>
        <dspel:getvalueof var="displayName"
            bean="${propertyView.formHandlerProperty}[${status.index}].displayName"/>
        <dspel:getvalueof var="app"
            bean="${propertyView.formHandlerProperty}[${status.index}].availableRankingProperty.application"/>

        <c:if test="${app ne 'Any'}">
          <asset-ui:createAssetURI
              var="rankPropURI"
              componentName="${repName}"
              itemDescriptorName="${componentItemDescriptor.itemDescriptorName}"
              itemId="${repositoryId}"/>
          <asset-ui:resolveAsset var="rankingPropertyAssetWrapper" uri="${rankPropURI}"/>
          <assetui-search:getRankingPropertySummary var="ranksummary"
              item="${rankingPropertyAssetWrapper.asset}"
              bundle="${config.resourceBundle}" />
          {
            "id":     "<c:out value="${repositoryId}"/>",
            "domId":  "<c:out value="pp_${repositoryId}"/>",
            "name":   "<c:out value="${displayName}"/>",
            "weight": "<c:out value="${weightingValue}"/>",
            "valueRanking": "<c:choose><c:when test="${empty ranksummary}"><fmt:message key="incompleteValueRanks" /></c:when><c:otherwise><c:out value="${ranksummary}"/></c:otherwise></c:choose>",
            "valueLink": "javascript:drillDownFunc('<c:out value="${rankPropURI}"/>');"
          }<c:if test="${not (status.index eq (rankPropertyCount - 1))}">,</c:if>
        </c:if>
      </c:forEach>
      ]
    };
  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/propertyPrioritizationJSON.jsp#2 $$Change: 651448 $--%>
