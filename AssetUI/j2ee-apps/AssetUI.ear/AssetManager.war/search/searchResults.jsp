<%--
  Search results panel for asset manager UI.

  @param  operation      An optional operation to be performed on the search
                         form prior to rendering it.
                         Valid values are "changeItemType" and "displayResults".

  @param  resetList      When set to true, the results list would be reset,
                         otherwise the existing results list would be rendered.
                         The result list would be reset if the operation is
                         "changeItemType".

  Request scope parameters

  @param searchResultsViewConfig The request scope variable that holds the configuration
                                 for the form view in search tab.

  @param tabConfig            The request scope variable that holds the configuration
                              for the search tab.

  @param managerConfig        The request scope variable that holds the configuration
                              for the asset manager UI.

  @param resultsListVisible   The request scope variable if set to true the results
                              list would be displayed unless there are exceptions.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchResults.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>
  <%-- set the debug flag --%>
  <c:set var="debug" value="${false}"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfoName" value="/atg/web/assetmanager/SessionInfo"/>
  <dspel:importbean var="sessionInfo"
                    bean="${sessionInfoName}"/>

  <%-- Unpack all page parameters --%>
  <dspel:getvalueof var="operationParam" param="operation"/>
  <dspel:getvalueof var="resetSearchResultsListParam" param="resetList"/>

  <%-- Set the page-level variables --%>
  <c:set var="hasExceptions" value="${false}"/>
  <c:set var="assetBrowser" value="${sessionInfo.currentAssetBrowser}"/>

  <%--  Set the correct resource bundle --%> 
  <c:choose>
    <c:when test="${not empty requestScope.searchResultsViewConfig}">
      <fmt:setBundle basename="${requestScope.searchResultsViewConfig.resourceBundle}" />
    </c:when>
    <c:when test="${not empty requestScope.tabConfig}">
      <fmt:setBundle basename="${requestScope.tabConfig.resourceBundle}" />
    </c:when>
    <c:otherwise>
      <fmt:setBundle basename="${config.resourceBundle}" />
    </c:otherwise>
  </c:choose>

  <%-- Check if operation requested is displayResults.
    if so get validation exceptions. --%>
  <c:if test="${operationParam == 'displayResults'}">
    <c:if test="${not empty assetBrowser}">
      <c:set scope="request" var="validationExceptions" value="${assetBrowser.validationExceptions}"/>
    </c:if>
    <dspel:test var="exceptions" value="${requestScope.validationExceptions}"/>
    <c:set var="hasExceptions" value="${exceptions.size > 0}"/>
  </c:if>

  <%-- Initialize the page - START --%>
  <c:set var="formHandlerPath" value="/atg/web/assetmanager/action/ListActionFormHandler"/>
  <dspel:importbean var="formHandler"
                    bean="${formHandlerPath}"/>
  <c:set target="${assetBrowser}" property="formHandlerPath" value="${formHandlerPath}"/>

  <%-- reset the Search results list if requested --%>
  <c:if test="${resetSearchResultsListParam}">
    <web-ui:invoke bean="${assetBrowser}" method="reset"/>
  </c:if>
  
  <%-- Get the empty list message --%>
  <fmt:message var="emptyListMessage" key="searchResults.emptyListMessage"/>
  
  <%-- Get the value of the flag that shall be used to display the results list --%>
  <c:set var="displayResultsList" value="${not hasExceptions and requestScope.resultsListVisible}"/>
  
  <%-- set the asset editor url --%>
  <c:url var="assetEditorURL" value="${requestScope.tabConfig.editorConfiguration.page}"/>
  <%-- Initialize the page - END --%>

  <c:if test="${debug}">
    <!-- 
    [searchResults.jsp] displayResultsList: <c:out value="${displayResultsList}"/><br />
    [searchResults.jsp] resultsListVisible: <c:out value="${requestScope.resultsListVisible}"/><br />
    [searchResults.jsp] hasExceptions: <c:out value="${hasExceptions}"/><br />
    [searchResults.jsp] validationExeptions: <c:out value="${requestScope.validationExceptions}"/><br />
    [searchResults.jsp] operation: <c:out value="${operationParam}"/><br />
    [searchResults.jsp] resetSearchResultsListParam: <c:out value="${resetSearchResultsListParam}"/><br />
    -->
  </c:if>

  <c:choose>
   <%-- display the exceptions if there are any instead of the regular 
        results list. --%>
   <c:when test="${hasExceptions}">
      <%-- render the button toolbar placeholder --%>
      <table id="leftPaneToolbar">
        <tr>
          <td id="leftPaneToolbarBack">
            <div class="leftPaneToolbarRight">
            </div>
            <div class="leftPaneToolbarLeft">
            </div>
          </td>
        </tr>
      </table>
      <%-- Display the list inside a scrolling subwindow --%>
      <c:url var="searchResultsErrorURL" value="/search/searchResultsError.jsp"/>
    
      <c:set scope="request" var="resizableStyle" value="border: none;"/>
    
      <dspel:include page="/components/resizeHandler.jsp">
        <dspel:param name="resizableElementId"      value="scrollContainer"/>
        <dspel:param name="heightKey"               value="searchResultsErrorScrollContainer"/>
      </dspel:include>
      <dspel:iframe id="scrollContainer" name="scrollContainer" frameborder="0" src="${searchResultsErrorURL}" style="${requestScope.resizableStyle}"/>
   </c:when>

   <%-- include the results list only if it is visible --%>
   <c:when test="${displayResultsList}">
    <%-- Get the current project and determine if it is editable so we can decide whether to render checkboxes
         and the add to project button --%>
    <c:set var="projectEditable" value="false"/>
    <pws:getCurrentProject var="projectContext"/>
    <c:if test="${projectContext.project ne null and projectContext.project.editable}">
      <c:set var="projectEditable" value="true"/>
    </c:if>

    <%-- figure out if this page should be readonly --%>
    <c:set var="dataEditable" value="false"/>
    <asset-ui:isDataEditable>
      <c:set var="dataEditable" value="true"/>
    </asset-ui:isDataEditable>

    <c:if test="${debug}">
      <!-- 
      [searchResults.jsp] managerConfig : <c:out value="${requestScope.managerConfig}"/><br />
      [searchResults.jsp] tabConfig : <c:out value="${requestScope.tabConfig}"/><br />
      [searchResults.jsp] searchResultsViewConfig : <c:out value="${requestScope.searchResultsViewConfig}"/><br />
      [searchResults.jsp] searchResultsViewConfig.operations : <c:out value="${requestScope.searchResultsViewConfig.operations}"/><br />
      [searchResults.jsp] projectEditable: <c:out value="${projectEditable}"/><br />
      [searchResults.jsp] dataEditable: <c:out value="${dataEditable}"/><br />
      [searchResults.jsp] projectContext.project: <c:out value="${projectContext.project}"/><br />
      [searchResults.jsp] projectContext.project.editable: <c:out value="${projectContext.project.editable}"/><br />
      [searchResults.jsp] sessionInfo.currentSearchItemType: <c:out value="${sessionInfo.currentSearchItemType}"/><br />
      -->
    </c:if>

    <%-- The url to the search results (this page) --%>
    <c:url var="actionURL" value="${requestScope.managerConfig.page}">
      <c:param name="operation" value="displayResults"/>
    </c:url>

    <%-- The url to the search criteria page. --%>
    <c:url var="searchFormURL" value="${requestScope.managerConfig.page}">
      <c:param name="operation" value="displayResults"/>
    </c:url>

    <%@ include file="/components/formStatus.jspf" %>

    <dspel:form name="listActionForm" action="${actionURL}" method="post">

      <dspel:input type="hidden"
                   bean="${formHandlerPath}.deleteSuccessUrl"
                   value="${searchFormURL}"/>
      <dspel:input type="hidden"
                   bean="${formHandlerPath}.duplicateSuccessUrl"
                   value="${searchFormURL}"/>

      <dspel:input id="pickedAssetURI"
                   type="hidden"
                   bean="${formHandlerPath}.destinationAssetURI"
                   value=""/>

      <dspel:input id="pickedAssetURIs"
                   type="hidden"
                   bean="${formHandlerPath}.destinationAssetURIs"
                   value=""/>

      <%-- render the button toolbar --%>
      <c:if test="${debug}">
        <!-- 
        [searchResults.jsp] assetBrowser.currentPageIndex: <c:out value="${assetBrowser.currentPageIndex}"/><br />
        [searchResults.jsp] assetBrowser.assetCount: <c:out value="${assetBrowser.assetCount}"/><br />
        [searchResults.jsp] assetBrowser.itemsPerPage: <c:out value="${assetBrowser.itemsPerPage}"/><br />
        -->
      </c:if>

      <c:choose>
        <c:when test="${assetBrowser.assetCount > 0}">
          <%-- set up what operations are allowed --%>
          <c:set var="allowNewSearch" value="${false}"/>
          <c:if test="${dataEditable}">
            <dspel:contains var="allowCreate" object="create" values="${requestScope.searchResultsViewConfig.operations}"/>
            <dspel:contains var="allowDuplicate" object="duplicate" values="${requestScope.searchResultsViewConfig.operations}"/>
            <dspel:contains var="allowLink" object="link" values="${requestScope.searchResultsViewConfig.operations}"/>
            <dspel:contains var="allowUnlink" object="unlink" values="${requestScope.searchResultsViewConfig.operations}"/>
            <dspel:contains var="allowMove" object="move" values="${requestScope.searchResultsViewConfig.operations}"/>
            <dspel:contains var="allowDelete" object="delete" values="${requestScope.searchResultsViewConfig.operations}"/>
            <dspel:contains var="allowAddToMultiEdit" object="addToMultiEdit" values="${requestScope.searchResultsViewConfig.operations}"/>
            <c:if test="${projectEditable}">
              <dspel:contains var="allowAddToProject" object="addToProject" values="${requestScope.searchResultsViewConfig.operations}"/>
            </c:if>
            <dspel:contains var="allowExport" object="export" values="${requestScope.searchResultsViewConfig.operations}"/>
            <c:set var="allowCheckAll" value="${true}"/>
          </c:if>

          <c:choose>
            <c:when test="${assetBrowser.areAssetsLinkable}">
              <c:set var="allowDuplicateAndMove" value="${allowDuplicate}"/>
              <c:set var="allowDuplicate" value="false"/>
              <c:set var="allowLink" value="${allowLink}"/>
              <c:set var="allowUnlink" value="${allowUnlink}"/>
              <c:set var="allowMove" value="${allowMove}"/>
            </c:when>
            <c:otherwise>
              <c:set var="allowDuplicateAndMove" value="false"/>
              <c:set var="allowDuplicate" value="${allowDuplicate}"/>
              <c:set var="allowLink" value="false"/>
              <c:set var="allowUnlink" value="false"/>
              <c:set var="allowMove" value="false"/>
            </c:otherwise>
          </c:choose>

          <c:if test="${debug}">
            <!-- 
            [searchResults.jsp] projectEditable: <c:out value="${projectEditable}"/><br />
            [searchResults.jsp] dataEditable: <c:out value="${dataEditable}"/><br />
            [searchResults.jsp] allowCreate: <c:out value="${allowCreate}"/><br />
            [searchResults.jsp] allowDuplicate: <c:out value="${allowDuplicate}"/><br />
            [searchResults.jsp] allowLink: <c:out value="${allowLink}"/><br />
            [searchResults.jsp] allowUnlink: <c:out value="${allowUnlink}"/><br />
            [searchResults.jsp] allowMove: <c:out value="${allowMove}"/><br />
            [searchResults.jsp] allowDelete: <c:out value="${allowDelete}"/><br />
            [searchResults.jsp] allowAddToMultiEdit: <c:out value="${allowAddToMultiEdit}"/><br />
            [searchResults.jsp] allowAddToProject: <c:out value="${allowAddToProject}"/><br />
            [searchResults.jsp] allowCheckAll: <c:out value="${allowCheckAll}"/><br />
            [searchResults.jsp] displayResultsList: <c:out value="${displayResultsList}"/><br />
            -->
          </c:if>

          <%-- render the button toolbar --%>
          <dspel:include page="/components/toolbar.jsp">
            <dspel:param name="tab"                       value="search"/>
            <dspel:param name="formHandlerPath"           value="${formHandlerPath}"/>
            <dspel:param name="allowCreate"               value="${allowCreate}"/>
            <dspel:param name="allowDuplicate"            value="${allowDuplicate}"/>
            <dspel:param name="allowDuplicateAndMove"     value="${allowDuplicateAndMove}"/>
            <dspel:param name="allowDelete"               value="${allowDelete}"/>
            <dspel:param name="allowAddToProject"         value="${allowAddToProject}"/>
            <dspel:param name="allowRemoveFromProject"    value="${false}"/>
            <dspel:param name="allowAddToMultiEdit"       value="${allowAddToMultiEdit}"/>
            <dspel:param name="allowRemoveFromMultiEdit"  value="false"/>
            <dspel:param name="allowLink"                 value="${allowLink}"/>
            <dspel:param name="allowMove"                 value="${allowMove}"/>
            <dspel:param name="allowUnlink"               value="${allowUnlink}"/>
            <dspel:param name="allowCheckAll"             value="${allowCheckAll}"/>
            <dspel:param name="allowNewSearch"            value="${allowNewSearch}"/>
            <dspel:param name="allowExport"            value="${allowExport}"/>
            <dspel:param name="fullType"                  value="${sessionInfo.currentSearchItemType}"/>
          </dspel:include>
        </c:when>

        <c:otherwise>
          <%-- render the button toolbar placeholder --%>
          <table id="leftPaneToolbar">
            <tr>
              <td id="leftPaneToolbarBack">
                <div class="leftPaneToolbarRight">
                </div>
                <div class="leftPaneToolbarLeft">
                </div>
              </td>
            </tr>
          </table>
        </c:otherwise>
      </c:choose>
      
      <%-- Render the scrollable, pageable list of assets --%>
      <dspel:include page="/components/pageableList.jsp">
        <dspel:param name="shouldRenderCheckboxes"   value="${dataEditable}"/>
        <dspel:param name="scrollContainerHeightKey" value="searchResultsScrollContainer"/>
        <dspel:param name="shouldSort"               value="${true}"/>
        <dspel:param name="emptyListMessage"         value="${emptyListMessage}"/>
      </dspel:include>
    
    </dspel:form>
   </c:when>
  </c:choose>
  
</dspel:page>


<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/search/searchResults.jsp#2 $$Change: 651448 $ --%>
