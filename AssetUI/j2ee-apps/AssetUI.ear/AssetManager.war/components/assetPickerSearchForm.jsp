<%--
  Asset Picker Search form panel for asset manager UI.

  @param assetInfo the currently selected asset type.
  @param selectedAsset  the currently selected asset type.
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerSearchForm.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>
  <%--
    Declare any debugging variables
    --%>
  <c:set var="debug" value="${false}"/>
  
  <%-- Unpack all page parameters and set the page-level or request-level parameters --%>
  <dspel:getvalueof var="assetInfoPath" param="assetInfo"/>
  <dspel:getvalueof var="selectedAsset" param="selectedAsset"/>

  <%-- set up the asset picker's assetInfo stuff --%>
  <c:if test="${empty assetInfoPath}">
    <c:set var="assetInfoPath" value="/atg/epub/servlet/ProcessAssetInfo"/>
  </c:if>

  <%-- set up the selected asset in the picker's --%>
  <c:if test="${empty selectedAsset}">
    <c:set var="selectedAsset" value="${config.sessionInfo.assetPickerExpressionService.encodedRepositoryItemTypeList[0]}"/>
  </c:if>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <%-- Get the assetInfo component --%>
  <dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>
  <dspel:importbean var="sourceTree" bean="${assetInfo.attributes.treeComponent}"/> 

  <%--  Set the resource bundle  --%>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- set the default value for local variables --%>

  <c:if test="${debug}">
  <!-- 
    [assetPickerSearchForm.jsp] assetView: <c:out value="${assetView}"/><br/>
    [assetPickerSearchForm.jsp] assetBrowser: <c:out value="${assetBrowser}"/><br/>
    [assetPickerSearchForm.jsp] assetInfoPath: <c:out value="${assetInfoPath}"/><br/>
    [assetPickerSearchForm.jsp] searchableTypesExist: <c:out value="${param.searchableTypesExist}"/><br/>
    [assetPickerSearchForm.jsp] resultsListVisible: <c:out value="${requestScope.resultsListVisible}"/><br/>
    [assetPickerSearchForm.jsp] selectedAsset: <c:out value="${selectedAsset}"/><br/>
  -->
  </c:if>
  
  <%-- Set the item types and property configuration file into the context for
       the assetPickerSearchExpressionPanel --%>
  <web-ui:decodeRepositoryItemType var="currentItemTypeInfo" encodedRepositoryItemType="${selectedAsset}"/>

  <c:if test="${not empty currentItemTypeInfo}">
    <%-- Set the item types and property configuration file into the context for
         the searchExpressionPanel --%>
    <%--
       Get the current expression context in AssetPickerExpressionService.
       AssetPickerExpressionService is of type MultiTypeTargetingExpressionService which
       stores a copy of the default context per item type key.
       Hence simply resolving the nucleus component would modify
       the default JSP expression context and not the one used for creating a query.
    --%>
    <c:set var="expressionContext" value="${config.sessionInfo.assetPickerExpressionService.currentJspExpressionContext}"/>
    <c:set target="${expressionContext}"
           property="itemRepository"
           value="${currentItemTypeInfo.repositoryPath}"/>
    <c:set target="${expressionContext}"
           property="itemType"
           value="${currentItemTypeInfo.repositoryItemType}"/>
  
    <%-- If the results list is currently hidden, expand the size
         of the expression container. --%>
    <c:choose>
      <c:when test="${not requestScope.resultsListVisible && not requestScope.hasExceptions}">
        <c:set var="assetPickerSearchExprContainerClass" value="searchCriteriaPanel searchCriteriaPanelLarge"/>
      </c:when>
      <c:otherwise>
        <c:set var="assetPickerSearchExprContainerClass" value="searchCriteriaPanel searchCriteriaPanelSmall"/>
      </c:otherwise>
    </c:choose>
  
    <div id="assetPickerSearchExpressionContainer"
         class="<c:out value='${assetPickerSearchExprContainerClass}'/>">
      <div id="assetPickerSearchExpression">
        <dspel:include page="assetPickerSearchExpressionPanel.jsp"/>
      </div><!--  id=assetPickerSearchExpression -->
  
      <c:url var="resultsUrl" context="/${config.assetUIWebAppName}" value="/assetPicker/assetPicker.jsp">
        <c:param name="assetInfo" value="${assetInfoPath}"/>
        <c:param name="assetView" value="results"/>
        <c:param name="selectedAsset" value="${selectedAsset}"/>
      </c:url> 
  
      <%-- Submit a search if the user presses Enter in the expression editor --%>
      <script type="text/javascript">
          atg.expreditor.registerOnEnter("assetPickerSearchExprEditorPanel", function() {
            atg.assetpicker.search.showResults("<c:out value='${resultsUrl}'  escapeXml='${false}'/>");
          });
      </script>
  
      <%-- Search button --%>
      <%-- NOTE: The class "expreditorControl" must be used for all elements
           whose onclick handlers trigger the atg.expreditor.applyPendingChanges
           function, in order to prevent atg.expreditor.onPopdownEvent from
           calling applyPendingChanges itself --%>
      <a id="assetPickerSearchButton" class="buttonSmall find expreditorControl" style="float:right" title="<fmt:message key='searchForm.find.title'/>"
         onclick="javascript:atg.assetpicker.search.showResults('<c:out value="${resultsUrl}" escapeXml="${false}"/>')">
        <span>
          <fmt:message key="searchForm.find"/>
        </span>
      </a>
    </div><!-- id="assetPickerSearchExpressionContainer" -->
  </c:if><!--  non empty currentItemTypeInfo -->
  <c:if test="${empty currentItemTypeInfo}">
    <span>
      <fmt:message key="searchForm.noItemTypeSelected"/>
    </span>
  </c:if><!--  empty currentItemTypeInfo -->

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerSearchForm.jsp#2 $$Change: 651448 $ --%>
