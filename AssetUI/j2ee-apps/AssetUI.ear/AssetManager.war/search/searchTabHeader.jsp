<%--
  Search tab's header in asset manager.

  @param searchFormHidable          Flag to determine if the search expression
                                    can be hidden. If set to true the link to
                                    hide the Search Form would be displayed.

  @param rightPaneContextRoot       The context root of the right pane URL.
  @param rightPanePage              The URL of the JSP that contains the right pane
                                    This would be reloaded when the user interacts
                                    with the controls in this page.

  Request scope parameters

  @param searchResultsViewConfig The request scope variable that holds the configuration
                                 for the form view in search tab.

  @param searchFormViewConfig The request scope variable that holds the configuration
                              for the form view in search tab.

  @param tabConfig            The request scope variable that holds the configuration
                              for the search tab.

  @param managerConfig        The request scope variable that holds the configuration
                              for the asset manager UI.

  @param resultsListVisible   The request scope variable if set to true the results
                              list would be displayed unless there are exceptions.
                              There is no special handling on this page, but it
                              is passed on to the results page.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchTabHeader.jsp#2 $$Change: 651448 $
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
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- Unpack all page parameters and set the page-level or request-level parameters --%>
  <%-- general --%>
  <dspel:getvalueof var="searchFormHidable" param="searchFormHidable"/>
  <%-- right pane attributes --%>
  <dspel:getvalueof var="rightPaneContextRootParam" param="rightPaneContextRoot"/>
  <dspel:getvalueof var="rightPanePageParam" param="rightPanePage"/>

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

  <c:if test="${empty searchFormHidable}">
    <c:set var="searchFormHidable" value="${false}"/>
  </c:if>

  <c:set var="currentSearchItemType" value="${sessionInfo.currentSearchItemType}"/>
  <c:if test="${empty currentSearchItemType}">
    <c:set target="${sessionInfo}" property="currentSearchItemType" value="${requestScope.searchFormViewConfig.itemTypes[0]}"/>
    <c:set var="currentSearchItemType" value="${sessionInfo.currentSearchItemType}"/>
  </c:if>

  <script type="text/javascript">
    atg.assetmanager.search.mHideCriteriaLinkText = '<fmt:message key="searchHeader.hideCriteria"/>';
    atg.assetmanager.search.mShowCriteriaLinkText = '<fmt:message key="searchHeader.showCriteria"/>';
  </script>

  <c:if test="${debug}">
  <!--
  [searchTabHeader.jsp] SessionInfoPath: <c:out value="${requestScope.sessionInfoPath}"/><br/>
  [searchTabHeader.jsp] SessionInfo: <c:out value="${sessionInfo}"/><br/>
  [searchTabHeader.jsp] ManagerConfig: <c:out value="${requestScope.managerConfig}"/><br/>
  [searchTabHeader.jsp] TabConfig: <c:out value="${requestScope.tabConfig}"/><br/>
  [searchTabHeader.jsp] SearchFormViewConfig: <c:out value="${requestScope.searchFormViewConfig}"/><br/>
  [searchTabHeader.jsp] searchFormHidable: <c:out value="${searchFormHidable}"/><br/>
  -->
  </c:if> <!-- debug -->

  <%-- Header - START --%>
  <div id="assetListHeader">
    <div id="assetListHeaderRight">
      <c:if test="${searchFormHidable}">
        <span class="subHeaderText">
          <%-- Show/Hide Criteria is result pane is visible --%>
          <a id="searchCriteriaPanelTrigger"
             href="javascript:atg.assetmanager.search.toggleCriteriaDisplayAndResize('searchTabExpressionPanel',
                                                                                     'scrollContainer',
                                                                                     'leftPane',
                                                                                     'scrollFooter',
                                                                                     'searchCriteriaPanelTrigger')">
            <fmt:message key="searchHeader.hideCriteria"/>
          </a>
        </span>
      </c:if>
    </div>
    <div id="assetListHeaderLeft">
      <span id="subHeaderText">
      <fmt:message key="searchHeader.assetTypeLabel"/>
      </span>
        <%-- add the Asset Type selector --%>
        <%-- Assemble the URL to be invoked when the user updates the value.  Note
             that the actual value will be appended to the URL using JavaScript. --%>
        <c:url var="changeItemTypeRequestUrl_Expr" value="search/searchExpressionPanel.jsp">
          <c:param name="operation"      value="changeItemType"/>
        </c:url>

        <c:url var="changeItemTypeRequestUrl_RightPane" context="${rightPaneContextRootParam}" value="${rightPanePageParam}">
          <c:param name="clearContext"      value="${true}" />
        </c:url>

        <select id="itemTypeSelect"
          onchange="javascript:atg.assetmanager.search.onItemTypeSelectSave(event,
                                                           '<c:out value="${changeItemTypeRequestUrl_Expr}" escapeXml="false"/>')">
          <c:if test="${not empty requestScope.searchFormViewConfig.itemTypes}">
            <c:forEach var="itemType" items="${requestScope.searchFormViewConfig.itemTypes}">
              <web-ui:decodeRepositoryItemType var="itemTypeInfo" encodedRepositoryItemType="${itemType}" />
              <c:if test="${not empty itemTypeInfo}">
                <c:choose>
                  <c:when test="${itemType eq currentSearchItemType}">
                    <option value="<c:out value='${itemType}'/>" selected>
                  </c:when>
                  <c:otherwise>
                    <option value="<c:out value='${itemType}'/>">
                  </c:otherwise>
                </c:choose>
                <c:out value="${itemTypeInfo.displayName}" />
                </option>
              </c:if>
            </c:forEach>
          </c:if>
        </select>
    </div>
  </div>
  <%-- Header - END --%>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchTabHeader.jsp#2 $$Change: 651448 $ --%>
