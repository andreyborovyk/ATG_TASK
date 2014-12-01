<%--
  Search tab container panel for asset picker UI.

  @param assetView   The view to display - search or results.
                     Valid value is "results". Any other way means
                     search form.

  @param assetInfo   Client path to nucleus AssetInfo context object.
                     Source: client/container

  @param selectAsset The asset type which is current selected. Valid
                     if there are more than one assets on which the
                     search can be performed.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerSearch.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%--
    Declare any debugging variables
    --%>
  <c:set var="debug" value="${false}"/>

  <%-- import the required beans --%>
  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <dspel:importbean var="assetBrowser" bean="/atg/web/assetmanager/list/AssetPickerSearchAssetBrowser"/>

  <%-- Unpack all page parameters and set the page-level or request-level parameters --%>
  <dspel:getvalueof var="assetView" param="assetView"/>
  <dspel:getvalueof var="assetInfoPath" param="assetInfo"/>
  <dspel:getvalueof var="selectedAsset" param="selectedAsset"/>

  <%-- set up the asset picker's assetInfo stuff --%>
  <c:if test="${empty assetInfoPath}">
    <c:set var="assetInfoPath" value="/atg/epub/servlet/ProcessAssetInfo"/>
  </c:if>

  <dspel:importbean var="assetInfo" bean="${assetInfoPath}"/>
  <dspel:importbean var="sourceTree" bean="${assetInfo.attributes.treeComponent}"/>

  <%--  Set the resource bundle  --%>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- if we're in a tree situation, check if there are valid destinations for all checked assets --%>
  <c:if test="${not empty sourceTree}">
    <c:if test="${not empty assetInfo.attributes.getPickableTypesFromTree}">
      <dspel:importbean var="pickableTypesTree" bean="${assetInfo.attributes.getPickableTypesFromTree}"/>
      <c:if test="${empty pickableTypesTree.parentTypeForCheckedNodes}">
        <c:set var="novaliddest" value="true"/>
      </c:if>
    </c:if>
  </c:if>

  <%-- if there are no valid pickable types, then close the asset picker and show the error --%>
  <c:choose>
    <c:when test="${novaliddest}">
      <fmt:message var="nodest" key="assetPicker.error.noValidDestinations"/>
      <script type="text/javascript">
         parent.hideIframe( "browser" );
         parent.messages.addError("<c:out value='${nodest}'/>");
      </script>
    </c:when>
    <c:otherwise>

  <%-- set the default value for local variables --%>
  <c:set scope="request" var="resultsListVisible" value="${false}"/>
  <c:set scope="request" var="criteriaVisible" value="${true}"/>
  <c:set scope="request" var="hasExceptions" value="${false}"/>

  <c:choose>
    <c:when test="${assetView eq 'results'}">
      <c:set scope="request" var="resultsListVisible" value="${true}"/>
      <c:if test="${not empty assetBrowser}">
        <c:set scope="request" var="validationExceptions" value="${assetBrowser.validationExceptions}"/>
      </c:if>
      <dspel:test var="exceptions" value="${requestScope.validationExceptions}"/>
      <c:set scope="request" var="hasExceptions" value="${exceptions.size > 0}"/>
    </c:when>
  </c:choose>

  <c:set scope="request" var="criteriaVisible" value="${not resultsListVisible || hasExceptions}"/>

  <%-- Get the value of the flag that shall be used to display the results list --%>
  <c:set var="displayResultsList" value="${requestScope.resultsListVisible}"/>


  <%-- include the javascript methods required for this tab --%>
  <script type="text/javascript"
          src="/AssetManager/scripts/assetPickerSearch.js">
  </script>

  <script type="text/javascript">
    // initialize the asset picker search javascript
    // hideCriteriaLinkText - The hide criteria link text to be displayed when
    //                        the search criteria is hidden.
    // showCriteriaLinkText - The hide criteria link text to be displayed when
    //                        the search criteria is hidden.
    // searchFormPanelClassLarge - The class name for the search form panel HTML
    //                        element when the search criteria is the only one
    //                        to be displayed in the asset picker.
    // searchResultsPanelClassLarge - The class name for the search results panel
    //                        HTML element when the search results list is the
    //                        only one to be displayed in the asset picker.
    // searchResultsPanelClassSmall - The class name for the search results panel
    //                        HTML element when is displayed along with the
    //                        search criteria.
    // searchResultsExceptionPanelClassLarge - The class name for the search
    //                        results panel HTML element with search exceptions
    //                        when it is the only one to be displayed in Asset Picker
    // searchResultsExceptionPanelClassSmall - The class name for the search
    //                        results panel HTML element with search exceptions
    //                        when it is displayed along with the search criteria
    atg.assetpicker.search.initialize({
      hideCriteriaLinkText: "<fmt:message key='searchHeader.hideCriteria'/>",
      showCriteriaLinkText: "<fmt:message key='searchHeader.showCriteria'/>",
      searchFormPanelClassLarge: "searchCriteriaPanel searchCriteriaPanelLarge",
      searchResultsPanelClassLarge: "",
      searchResultsPanelClassSmall: "searchResultsPanelSmall",
      searchResultsExceptionPanelClassLarge: "searchResultsExceptionsLarge",
      searchResultsExceptionPanelClassSmall: "searchResultsExceptionsSmall"
    });
  </script>

  <%-- Display the correct page for the current view. --%>
  <c:catch var="ex">

    <%-- set up the searchable types --%>
    <c:set var="searchableTypesExist" value="true"/>
    <c:choose>
      <c:when test="${not empty assetInfo.attributes.getPickableTypesFromTree}">
        <dspel:importbean var="pickableTypesTree"
                          bean="${assetInfo.attributes.getPickableTypesFromTree}"/>
        <c:choose>
          <c:when test="${not empty pickableTypesTree.parentTypeForCheckedNodes}">
            <web-ui:makeList var="typeList">
              <c:forEach var="type" items="${pickableTypesTree.parentTypeForCheckedNodes}">
                <web-ui:listItem value="${assetInfo.attributes.componentPath}:${type}"/>
              </c:forEach>
            </web-ui:makeList>
            <c:set target="${config.sessionInfo.assetPickerExpressionService}"
               property="encodedRepositoryItemTypeList"
               value="${typeList}"/>
          </c:when>
          <c:otherwise>
            <c:set var="searchableTypesExist" value="false"/>
          </c:otherwise>
        </c:choose>
      </c:when>
      <c:when test="${not empty assetInfo.attributes.itemTypes}">
        <web-ui:makeList var="typeList">
          <c:forTokens var="type" items="${assetInfo.attributes.itemTypes}" delims=",">
            <web-ui:listItem value="${assetInfo.attributes.componentPath}:${type}"/>
          </c:forTokens>
        </web-ui:makeList>
        <c:set target="${config.sessionInfo.assetPickerExpressionService}"
           property="encodedRepositoryItemTypeList"
           value="${typeList}"/>
      </c:when>
      <c:when test="${assetInfo.attributes.itemType ne null}">
        <web-ui:makeList var="typeList">
          <web-ui:listItem value="${assetInfo.attributes.componentPath}:${assetInfo.attributes.itemType}"/>
        </web-ui:makeList>
        <c:set target="${config.sessionInfo.assetPickerExpressionService}"
           property="encodedRepositoryItemTypeList"
           value="${typeList}"/>
      </c:when>
      <c:otherwise>
        <c:set var="searchableTypesExist" value="false"/>
      </c:otherwise>
    </c:choose>

    <c:if test="${debug}">
    <!--
      [assetPickerSearch.jsp] assetView: <c:out value="${assetView}"/><br/>
      [assetPickerSearch.jsp] assetBrowser: <c:out value="${assetBrowser}"/><br/>
      [assetPickerSearch.jsp] assetInfoPath: <c:out value="${assetInfoPath}"/><br/>
      [assetPickerSearch.jsp] assetInfo: <c:out value="${assetInfo}"/><br/>
      [assetPickerSearch.jsp] searchableTypesExist: <c:out value="${searchableTypesExist}"/><br/>
      [assetPickerSearch.jsp] resultsListVisible: <c:out value="${requestScope.resultsListVisible}"/><br/>
      [assetPickerSearch.jsp] criteriaVisible: <c:out value="${criteriaVisible}"/><br/>
      [assetPickerSearch.jsp] hasExceptions: <c:out value="${requestScope.hasExceptions}"/><br/>
      [assetPickerSearch.jsp] displayResultsList: <c:out value="${displayResultsList}"/><br/>
      [assetPickerSearch.jsp] searchFormHidable: <c:out value="${requestScope.resultsListVisible}"/><br/>
      [assetPickerSearch.jsp] typeList: <c:out value="${typeList}"/><br/>
      [assetPickerSearch.jsp] AssetInfo.itemType: <c:out value="${assetInfo.attributes.itemType}"/><br/>
      [assetPickerSearch.jsp] AssetInfo.itemTypes: <c:out value="${assetInfo.attributes.itemTypes}"/><br/>
      [assetPickerSearch.jsp] pickableTypesFromTree: <c:out value="${assetInfo.attributes.getPickableTypesFromTree}"/><br/>
      [assetPickerSearch.jsp] pickableTypes.parentType: <c:out value="${pickableTypesTree.parentTypeForCheckedNodes}"/><br/>
      [assetPickerSearch.jsp] AssetInfo.attributes: <c:out value="${assetInfo.attributes}"/><br/>
      [assetPickerSearch.jsp] selectedAsset: <c:out value="${selectedAsset}"/><br/>
    -->
    </c:if>

    <c:choose>
      <c:when test="${searchableTypesExist}">
        <%-- Reset the SearchAssetBrowser every time this page is visited --%>
        <web-ui:invoke bean="${assetBrowser}" method="reset"/>

        <div id="assetBrowserContentBody">
          <div id="assetListHeader">
            <div id="assetListHeaderRight">
              <c:if test="${requestScope.resultsListVisible || requestScope.hasExceptions}">
                <span class="subHeaderText">
                  <%-- Show/Hide Criteria is result pane is visible --%>
                  <a id="assetPickerSearchCriteriaTrigger"
                     href="javascript:atg.assetpicker.search.toggleCriteriaDisplayAndResize('assetPickerSearchExpressionPanel',
                                                                                            'scrollContainer',
                                                                                            'assetListSubHeader',
                                                                                            'assetPickerSearchCriteriaTrigger',
                                                                                            <c:out value='${requestScope.hasExceptions}'/>)">
                    <c:choose>
                      <c:when test="${criteriaVisible}">
                        <fmt:message key="searchHeader.hideCriteria"/>
                      </c:when>
                      <c:otherwise>
                        <fmt:message key="searchHeader.showCriteria"/>
                      </c:otherwise>
                    </c:choose>
                  </a>
                </span>
              </c:if>
            </div><!-- id=assetListHeaderRight -->
            <div id="assetListHeaderLeft">
              <span id="subHeaderText">
                <fmt:message key="searchHeader.assetTypeLabel"/>
                <c:if test="${empty typeList}">
                  <c:set var="typeList" value="${config.sessionInfo.assetPickerExpressionService.encodedRepositoryItemTypeList}"/>
                </c:if>
                <dspel:test var="assetTypeList" value="${typeList}"/>
                <!--
                [assetPickerSearch.jsp] assetTypes: <c:out value="${typeList}"/><br/>
                [assetPickerSearch.jsp] assetTypeList.size: <c:out value="${assetTypeList.size}"/><br/>
                 -->
                <c:choose>
                  <c:when test="${assetTypeList.size > 1}">
                    <c:if test="${empty selectedAsset}">
                      <c:set var="selectedAsset" value="${typeList[0]}"/>
                    </c:if>
                    <c:url var="changeItemTypeRequestUrl_Expr" context="${config.contextRoot}" value="/components/assetPickerSearchExpressionPanel.jsp">
                      <c:param name="operation" value="changeItemType"/>
                    </c:url>
                    <c:url var="changeItemTypeRequestUrl_Results" context="/${config.assetUIWebAppName}" value="/assetPicker/assetPicker.jsp">
                      <c:param name="assetInfo" value="${assetInfoPath}"/>
                      <c:param name="assetView" value="results"/>
                    </c:url>

                    <select id="itemTypeSelect"
                      onchange="javascript:atg.assetpicker.search.onItemTypeSelect(event,
                                                            '<c:out value="${changeItemTypeRequestUrl_Expr}" escapeXml="false"/>',
                                                            'assetPickerSearchExpression',
                                                            'assetPickerSearchCriteriaTrigger',
                                                            'assetPickerSearchResultsPanel',
                                                            'assetPickerSearchExpressionPanel',
                                                            'assetPickerSearchExpressionContainer',
                                                            '<c:out value="${changeItemTypeRequestUrl_Results}" escapeXml="false"/>'
                                                            )">
                    <c:forEach var="itemType" items="${typeList}">
                      <web-ui:decodeRepositoryItemType var="itemTypeInfo" encodedRepositoryItemType="${itemType}"/>
                      <c:if test="${not empty itemTypeInfo}">
                        <c:choose>
                          <c:when test="${itemType eq selectedAsset}">
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
                    </select>
                  </c:when>
                  <c:when test="${assetTypeList.size == 1}">
                    <c:set var="selectedAsset" value="${typeList[0]}"/>
                    <web-ui:decodeRepositoryItemType var="itemTypeInfo" encodedRepositoryItemType="${typeList[0]}"/>
                    <c:out value="${itemTypeInfo.displayName}"/>
                  </c:when>
                  <c:otherwise>
                    ...
                  </c:otherwise>
                </c:choose>
              </span>
            </div><!-- id=assetListHeaderLeft -->
          </div><!-- id=assetListHeader -->

          <c:if test="${debug}">
          <!--
            [assetPickerSearch.jsp] assetPickerSearchFormURL: <c:out value="${assetPickerSearchFormURL}"/><br/>
            [assetPickerSearch.jsp] assetPickerSearchResultsURL: <c:out value="${assetPickerSearchResultsURL}"/><br/>
            [assetPickerSearch.jsp] selectedAsset: <c:out value="${selectedAsset}"/><br/>
          -->
          </c:if>

          <%-- hide the criteria if required --%>

          <c:choose>
            <c:when test="${not criteriaVisible}">
          <div id="assetPickerSearchExpressionPanel" style="display:none;">
            </c:when>
            <c:otherwise>
          <div id="assetPickerSearchExpressionPanel">
            </c:otherwise>
          </c:choose>
            <dspel:include otherContext="${config.contextRoot}" page="/components/assetPickerSearchForm.jsp">
              <dspel:param name="searchableTypesExist" value="${searchableTypesExist}"/>
              <dspel:param name="selectedAsset" value="${selectedAsset}"/>
            </dspel:include>
          </div><!--  id=assetPickerSearchExpressionPanel -->

          <div id="assetPickerSearchResultsPanel">
            <c:choose>
              <c:when test="${requestScope.hasExceptions}">
                <%-- display the validation errors --%>
                <%-- render the button toolbar placeholder --%>
                <table id="assetListSubHeader">
                  <tr>
                    <td id="assetListSubHeaderLeft">
                    </td>
                  </tr>
                </table>
                <div id="scrollContainer" class="searchResultsExceptionsSmall">
                  <fmt:message key="searchResults.searchCriteriaErrorMessage"/>
                  <%-- Validation Error display  - START - - % >
                  <ul>
                    < % - -  check if there are any errors - - % >
                    <c:forEach items="${requestScope.validationExceptions}" var="ex">
                      <li><c:out value="${ex.message}"/></li>
                    </c:forEach>
                  </ul>
                  < % - - Validation Error display  - END --%>
                </div>
              </c:when>
              <%-- include the results list only if it is visible --%>
              <c:when test="${displayResultsList}">
                <dspel:include otherContext="${config.contextRoot}" page="/components/assetPickerSearchResults.jsp">
                </dspel:include>
              </c:when>
            </c:choose>
          </div><!--  id=assetPickerSearchResultsPanel -->
        </div><!-- id=assetBrowserContentBody -->
      </c:when>
      <c:otherwise>
        <div id="assetBrowserContentBody">

          <div id="assetListHeader">

            <div id="assetListHeaderRight">
              <!--  empty -->
            </div><!--  id=assetListHeaderRight -->

            <div id="assetListHeaderLeft">
              <!--  empty -->
            </div><!--  id=assetListHeaderLeft -->

          </div><!--  id=assetListHeader -->

          <div id="assetPickerSearchExpressionPanel">
            <br/>
            <br/>
            <fmt:message key="assetPicker.error.noValidDestinations"/>
            <br/>
            <br/>
          </div><!--  id=assetPickerSearchExpressionPanel -->

          <div id="assetPickerSearchResultsPanel">
            <!--  empty -->
          </div><!--  id=assetPickerSearchResultsPanel -->

        </div><!-- id=assetBrowserContentBody -->
      </c:otherwise>
    </c:choose>

  </c:catch>

  <%
    Throwable tt = (Throwable) pageContext.getAttribute("ex");
    if ( tt != null ) {
      System.out.println("Caught exception in assetPickerSearch.jsp:");
      tt.printStackTrace();
    }
  %>
    </c:otherwise> <%-- test for no checkable types --%>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/components/assetPickerSearch.jsp#2 $$Change: 651448 $ --%>
