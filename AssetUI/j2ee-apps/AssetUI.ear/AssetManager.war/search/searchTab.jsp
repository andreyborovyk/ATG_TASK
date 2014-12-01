<%--
  Search tab container panel for asset manager UI.

  @param  operation   An optional operation to be performed on the search form
                      prior to rendering it.  Valid value is "displayResults".

  @param  resetTab    An optional flag that indicates if the tab should be reset. Default is true.
                      If set to true or not specified, the search results list and the right pane
                      will be cleared.

  Request scope parameters

  @param tabConfig            The request scope variable that holds the configuration
                              for the search tab.

  @param managerConfig        The request scope variable that holds the configuration
                              for the asset manager UI.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchTab.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>
  <%-- set the debug flag --%>
  <c:set var="debug" value="${false}"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- include the javascript methods required for this tab --%>
  <script type="text/javascript"
          src="/AssetManager/scripts/searchForm.js">
  </script>

  <%-- Store the ID of the current view in the session, for use by the right-side pane, etc. --%>
  <c:set target="${sessionInfo}" property="assetEditorViewID" value="form"/>

  <%-- Get view configuration from taskConfiguration.xml --%>  
  <%-- Get the view configuration for search form --%>
  <c:set scope="request"  var="searchFormViewConfig" value="${requestScope.tabConfig.views['form']}"/>

  <%-- Get the view configuration for search results --%>
  <c:set scope="request" var="searchResultsViewConfig" value="${requestScope.tabConfig.views['results']}"/>

  <%-- Set the flag to reset the tab --%>
  <c:set var="resetTab" value="${empty param.resetTab || param.resetTab}"/>

  <%-- set the operation to be performed --%>
  <c:if test="${resetTab}">
    <c:set target="${sessionInfo}" property="currentSearchTabOperation" value="${param.operation}"/>
  </c:if>
  <%-- we try to get this value once as SessionInfo does a 
    lookup in the map for the current activity. --%>
  <c:set var="operation" value="${sessionInfo.currentSearchTabOperation}"/>
  
  <%-- Set the flag to display the results list --%>
  <c:set scope="request" var="resultsListVisible" value="${operation == 'displayResults'}"/>

  <c:if test="${debug}">
  <!--
  [searchTab.jsp] resetTab: <c:out value="${resetTab}"/><br/>
  [searchTab.jsp] operation: <c:out value="${operation}"/><br/>
  [searchTab.jsp] resultsListVisible: <c:out value="${requestScope.resultsListVisible}"/><br/>
  [searchTab.jsp] searchFormViewConfig: <c:out value="${requestScope.searchFormViewConfig}"/><br/>
  [searchTab.jsp] searchResultsViewConfig: <c:out value="${requestScope.searchResultsViewConfig}"/><br/>
  [searchTab.jsp] tabConfig: <c:out value="${requestScope.tabConfig}"/><br/>
  [searchTab.jsp] searchFormViewConfig.resourceBundle: <c:out value="${requestScope.searchFormViewConfig.resourceBundle}"/><br/>
  [searchTab.jsp] searchResultsViewConfig.resourceBundle: <c:out value="${requestScope.searchResultsViewConfig.resourceBundle}"/><br/>
  [searchTab.jsp] tabConfig.resourceBundle: <c:out value="${requestScope.tabConfig.resourceBundle}"/><br/>
  [searchTab.jsp] config.resourceBundle: <c:out value="${config.resourceBundle}"/><br/>
  -->
  </c:if>

  <%-- Get the right-side pane URL from the task configuration. --%>
  <c:url var="assetEditorURL" value="${requestScope.tabConfig.editorConfiguration.page}"/>

  <table id="contentTable" cellpadding="0" cellspacing="0">
    <tbody>
      <tr>
        <td id="leftPane">
          <div id="searchTabHeader">
            <%-- include the search tab body that includes the search expression and results --%>
            <dspel:include page="/search/searchTabHeader.jsp">
              <dspel:param name="searchFormHidable"                    value="${requestScope.resultsListVisible}"/>
              <dspel:param name="rightPaneContextRoot"                 value="${config.contextRoot}"/>
              <dspel:param name="rightPanePage"                        value="${requestScope.tabConfig.editorConfiguration.page}"/>
            </dspel:include>
          </div> <!--  id="searchTabHeader" -->

          <div id="searchTabBodyContainer">
            <%-- include the search tab body that includes the search expression and results --%>
            <dspel:include page="/search/searchTabBody.jsp">
              <dspel:param name="operation"             value="${operation}"/>
              <dspel:param name="resetSearchList"       value="${resetTab}"/>
            </dspel:include>
          </div><!--  id="searchTabBodyContainer" -->
        </td><!--  id="leftPane" -->

        <%-- Display the asset editor in the right-pane iframe. --%>
        <td id="rightPane">

          <%-- Get the assetEditor for the current left tab --%>
          <c:if test="${not empty sessionInfo.assetEditorViewID}">
            <c:set var="assetEditorPath" value="${sessionInfo.assetEditors[sessionInfo.assetEditorViewID]}"/>
            <dspel:importbean var="assetEditor" bean="${assetEditorPath}"/>
          </c:if>

          <%-- reset the asset editor if the tab should be reset. --%>
          <c:if test="${resetTab}">
            <web-ui:invoke bean="${assetEditor}" method="clearContext"/>
          </c:if>

          <%-- Add the right-pane iframe to the list of resizable elements. --%>
          <c:set scope="request" var="resizableStyle" value="border: none;"/>
          <dspel:include page="/components/resizeHandler.jsp">
            <dspel:param name="resizableElementId" value="rightPaneIframe"/>
            <dspel:param name="heightKey"          value="rightPane"/>
          </dspel:include>

          <dspel:iframe id="rightPaneIframe"
                        name="rightPaneIframe"
                        iclass="assetEditorFrame"
                        frameborder="0"
                        allowtransparency="true"
                        scrolling="no"
                        src="${assetEditorURL}"
                        style="${requestScope.resizableStyle}"/>
        </td><!--  id="rightPane" -->
      </tr>
    </tbody>
  </table>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchTab.jsp#2 $$Change: 651448 $ --%>
