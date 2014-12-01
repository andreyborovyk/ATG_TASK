<%--
  Search form panel for asset manager UI.

  Request scope parameters

  @param searchFormViewConfig The request scope variable that holds the configuration
                              for the form view in search tab.

  @param tabConfig            The request scope variable that holds the configuration
                              for the search tab.

  @param managerConfig        The request scope variable that holds the configuration
                              for the asset manager UI.

  @param resultsListVisible   The request scope variable if set to true the results
                              list would be displayed unless there are exceptions.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchForm.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>
  <%-- set the debug flag --%>
  <c:set var="debug" value="${false}"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfoPath" value="/atg/web/assetmanager/SessionInfo"/>
  <dspel:importbean var="sessionInfo"
                    bean="${sessionInfoPath}"/>

  <%--  Set the correct resource bundle --%>
  <c:choose>
    <c:when test="${not empty requestScope.searchFormViewConfig}">
      <fmt:setBundle basename="${requestScope.searchFormViewConfig.resourceBundle}" />
    </c:when>
    <c:when test="${not empty requestScope.tabConfig}">
      <fmt:setBundle basename="${requestScope.tabConfig.resourceBundle}" />
    </c:when>
    <c:otherwise>
      <fmt:setBundle basename="${config.resourceBundle}" />
    </c:otherwise>
  </c:choose>

  <c:if test="${debug}">
  <!--
  [searchForm.jsp] SessionInfoPath: <c:out value="${sessionInfoPath}"/><br/>
  [searchForm.jsp] SessionInfo: <c:out value="${sessionInfo}"/><br/>
  [searchForm.jsp] ManagerConfig: <c:out value="${requestScope.managerConfig}"/><br/>
  [searchForm.jsp] TabConfig: <c:out value="${requestScope.tabConfig}"/><br/>
  [searchForm.jsp] searchFormViewConfig: <c:out value="${requestScope.searchFormViewConfig}"/><br/>
  [searchForm.jsp] PreviousItemType: <c:out value="${sessionInfo.currentSearchItemType}"/><br/>
  [searchForm.jsp] resultsListVisible: <c:out value="${requestScope.resultsListVisible}"/><br/>
  -->
  </c:if>

  <%-- Error display  - START --%>
  <c:set var="listActionFormHandlerPath" value="/atg/web/assetmanager/action/ListActionFormHandler"/>
  <dspel:importbean var="listActionFormHandler"
                    bean="${listActionFormHandlerPath}"/>

  <!-- list form handler errors -->
  <c:forEach items="${listActionFormHandler.formExceptions}" var="ex">
    <script type="text/javascript" >
      messages.addError('<c:out value="${ex.message}"/>');
    </script>
  </c:forEach>

  <!-- list form handler success -->
  <c:if test="${not empty listActionFormHandler.successMessage}">
    <script>
      messages.addAlert('<c:out value="${listActionFormHandler.successMessage}"/>');
    </script>
  </c:if>

  <%-- Error display  - END --%>

  <%-- uncheck all on the server side every time a new search is run --%>
  <asset-ui:performListAction listManager="${sessionInfoPath}" action="removeAll"/>

  <%-- Intialization - START --%>
  <c:set var="currentSearchItemType" value="${sessionInfo.currentSearchItemType}"/>
  <c:if test="${empty currentSearchItemType}">
    <c:set target="${sessionInfo}" property="currentSearchItemType" value="${requestScope.searchFormViewConfig.itemTypes[0]}"/>
    <c:set var="currentSearchItemType" value="${sessionInfo.currentSearchItemType}"/>
  </c:if>
  <%-- Intialization - END --%>

  <%-- Search criteria - START --%>
  <%-- Set the item types and property configuration file into the context for
       the searchExpressionPanel --%>
  <web-ui:decodeRepositoryItemType var="currentItemTypeInfo" encodedRepositoryItemType="${currentSearchItemType}"/>

  <c:if test="${not empty currentItemTypeInfo}">

    <%-- Set the item types and property configuration file into the context for
         the searchExpressionPanel --%>
    <dspel:getvalueof var="expressionContext"
                      bean="/atg/web/assetmanager/search/TargetingExpressionContext"/>
    <c:set target="${expressionContext}"
           property="itemRepository"
           value="${currentItemTypeInfo.repositoryPath}"/>
    <c:set target="${expressionContext}"
           property="itemType"
           value="${currentItemTypeInfo.repositoryItemType}"/>
    <c:set target="${expressionContext}"
           property="targetingPropertyConfigurationFile"
           value="${requestScope.tabConfig.views['form'].propertyConfigurationFile}"/>

    <%-- If the results list is currently hidden, add the left-pane body to the
         list of resizable elements. --%>
    <c:set scope="request" var="resizableStyle" value=""/>
    <c:if test="${not requestScope.resultsListVisible}">
      <dspel:include page="/components/resizeHandler.jsp">
        <dspel:param name="resizableElementId" value="expressionContainer"/>
        <dspel:param name="heightKey"          value="expressionContainer"/>
      </dspel:include>
    </c:if>

    <div id="expressionContainer"
         class="searchCriteriaPanel"
         style="<c:out value='${requestScope.resizableStyle}'/>">
      <div id="searchExprEditorContainer">
        <dspel:include page="/search/searchExpressionPanel.jsp"/>
      </div><!-- id="searchExprEditorContainer" -->

      <%-- Display the search form --%>
      <c:url var="searchActionURL" value="${requestScope.managerConfig.page}">
        <c:param name="operation" value="displayResults"/>
      </c:url>

      <%-- Search button, specify the id of the submit type input in the form. --%>
      <script type="text/javascript">
        atg.expreditor.registerOnEnter("searchExprEditorPanel", function() {
          atg.assetmanager.search.doSearch("<c:out value='${searchActionURL}'/>");
        });
      </script>
      <a class="buttonSmall find expreditorControl" style="float:right" title="<fmt:message key='searchForm.find.title'/>"
         onclick="javascript:atg.assetmanager.search.doSearch('<c:out value="${searchActionURL}"/>')">
        <span>
          <fmt:message key="searchForm.find"/>
        </span>
      </a>
    </div><!-- id="expressionContainer"  class="searchCriteriaPanel" -->
  </c:if><!--  non empty currentItemTypeInfo -->
  <c:if test="${empty currentItemTypeInfo}">
    <span>
      <fmt:message key="searchForm.noItemTypeSelected"/>
    </span>
  </c:if><!--  empty currentItemTypeInfo -->

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchForm.jsp#2 $$Change: 651448 $ --%>
