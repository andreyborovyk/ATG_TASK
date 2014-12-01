<%--
  Main form of the Search Testing application.
  Includes inputs for facets and track items. Includes environment and search config pickers.
  After the search was performed, includes results.

  The following request-scoped variables are expected to be set:
  
  @param  formHandlerPath Nucleus path to the SearchTestingFormHandler
  @param  formHandler     SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/inputs.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                      %>
<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0"   %>
<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                       %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>
<%@ taglib prefix="web-ui"         uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="ee"             uri="http://www.atg.com/taglibs/expreditor_rt"       %>

<dspel:page>

  <c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
  <c:set var="formHandler"     value="${requestScope.formHandler}"/>

  <%-- Get the resource bundle used by the RefinementRepository --%>
  <dspel:importbean var="propertyManager" bean="/atg/search/repository/SearchTestingPropertyManager"/>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <dspel:importbean var="configInfo" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <c:set var="sessionInfo" value="${configInfo.sessionInfo}"/>
  <dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>
  <dspel:importbean var="searchTestingService" bean="/atg/search/testing/SearchTestingService"/>
  <dspel:importbean var="categoryPathHelper" bean="/atg/search/web/assetmanager/CategoryPathHelper"/>

  <dspel:getvalueof var="testUndeployed" param="isTestUndeployed"/>
  <dspel:getvalueof var="categoryId" param="categoryId"/>
  <dspel:getvalueof var="searchConfigId" param="searchConfigId"/>
  <%-- if Test Search button on Search Configuration or Facet Page was pressed and project is active --%>
  <c:if test="${(('true' eq testUndeployed) or (not empty categoryId) or (not empty searchConfigId)) and (formHandler.projectActive)}">
    <%-- set Undeployed testing --%>
    <dspel:setvalue bean="${formHandlerPath}.testingMode" value="true"/>
  </c:if>
  <c:if test="${(not empty categoryId) and (formHandler.exclusiveCategoryAndSearchTextModeValue eq formHandler.categoryAndSearchTextMode)}">
    <dspel:setvalue bean="${formHandlerPath}.searchInputMode" value="${formHandler.categorySearchInputModeValue}"/>
  </c:if>
  
  <c:set var="tabConfig" value="${sessionInfo.taskConfiguration.tabs[sessionInfo.assetManagerTab]}"/>
  <c:url var="assetEditorURL" value="${tabConfig.editorConfiguration.page}"/>
  
  <%-- Do we have a transient asset?  (i.e. Is this a create vs an update?) To find out takes 3 steps:--%>
  <c:set var="transient" value="false"/>
  <%-- 1. Get the current left tab --%>
  <c:set var="currentView" value="${sessionInfo.assetEditorViewID}"/>
  <%-- 2. Get the assetEditor for the current left tab --%>
  <c:if test="${not empty currentView}">
    <c:set var="assetEditorPath" value="${sessionInfo.assetEditors[currentView]}"/>
    <dspel:importbean var="assetEditor" bean="${assetEditorPath}"/>
    <%-- 3. see if the asset context has a transient asset --%>
    <c:if test="${not empty assetEditor.assetContext.transientAsset}">
      <c:set var="transient" value="true"/>
    </c:if>
  </c:if>

  <dspel:input id="inputsDebugExclusion" type="hidden" bean="${formHandlerPath}.debugExclusion"/>
  <dspel:input id="searchTestingEditorDisablePositioning" type="hidden" bean="${formHandlerPath}.disablePositioning"/>
  <dspel:input id="searchTestingInputsFacetClicked" type="hidden" bean="${formHandlerPath}.facetInputModel.facetLinkControl"/>
  <dspel:input id="searchTestingInputsApplyFacets" type="hidden" bean="${formHandlerPath}.applyChangedFacets"/>
  
  <dspel:input id="debugRulesSearchButton" type="submit" bean="${formHandlerPath}.debugRulesSearch" style="display:none"/>
  <dspel:input id="facetSearchButton" type="submit" bean="${formHandlerPath}.facetSearch" style="display:none"/>
  <dspel:input id="applyFacetChangesButton" type="submit" bean="${formHandlerPath}.applyFacetChanges" style="display:none"/>

  <dspel:input id="searchCriteriaChangedValue" type="hidden" bean="${formHandlerPath}.searchCriteriaChanged" />

  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <dspel:input id="testingContentLabel" type="hidden" bean="${formHandlerPath}.testEnvironmentModel.contentLabel" />
  <dspel:input id="testingTargetType"   type="hidden" bean="${formHandlerPath}.testEnvironmentModel.targetType" />
  <dspel:input id="testingMode"         type="hidden" bean="${formHandlerPath}.testEnvironmentModel.testingMode" />

  <script type="text/javascript">
    registerOnLoad(function() {
      parent.atg.assetmanager.saveconfirm.setBeforeFinishSave(showSaveAsPicker, "onLeaveSearchTestPickerSelect")
    });
    
    registerOnModified(function () {
      var saveButton = document.getElementById("searchSaveButton");
      if (saveButton != null) {
        saveButton.title = "<fmt:message key='searchTestingInputs.save.button.enabled.title'/>";
        saveButton.disabled = false;
      } 
    });
    
    function markSearchCriteriaChanged() {
      markAssetModified();
      <c:if test="${formHandler.wasSearch or formHandler.facetInputModel.selectedFacetsSize gt 0}">
        var searchCriteriaChangedValue = document.getElementById('searchCriteriaChangedValue');
        if (searchCriteriaChangedValue)
          searchCriteriaChangedValue.value = 'true';
        clearRefinementSelection();
      </c:if>
    }
    
    function queryTextOnChange() {
      markSearchCriteriaChanged();
      return true;
    }

    var categorySearchInputModeChange;
    
    function searchInputModeChange(mode) {
      markSearchCriteriaChanged();
      
      var queryText = document.getElementById('queryText');
      if (queryText) {
        var disabled = ('<c:out value="${formHandler.categorySearchInputModeValue}"/>' == mode);
        queryText.disabled = disabled;
        if (disabled)
          queryText.value = '';
      }
      
      if (categorySearchInputModeChange)
        categorySearchInputModeChange(mode);
        
      return true;
    }

  </script>

  <div class="atg_noFieldset">
    <dl class="atg_smerch_configList">
      <dt><fmt:message key="searchTestingInputs.testingEnvironment.text"/></dt>
      <dd>
        <c:choose>
          <c:when test="${empty formHandler.testEnvironmentModel}">
            <span id="envMsg">
              <fmt:message key="searchTestingEnvironmentPicker.noEnvironmentsConfigured"/>
            </span>
          </c:when>
          <c:otherwise>
            <span id="envMsg">
              <c:choose>
                <c:when test="${formHandler.testEnvironmentModel.testingMode}">
                  <c:set var="testEnvMsgKey" value="searchTestingEnvironmentPicker.testUndeployed.text" />
                </c:when>
                <c:otherwise>
                  <c:set var="testEnvMsgKey" value="searchTestingEnvironmentPicker.testDeployed.text" />
                </c:otherwise>
              </c:choose>
              <fmt:message key="${testEnvMsgKey}">
                <fmt:param value="${formHandler.testEnvironmentModel.contentLabel}"/>
                <fmt:param value="${formHandler.testEnvironmentModel.targetType}"/>
              </fmt:message>
            </span>
            <dspel:importbean var="multisiteService" bean="/atg/search/routing/MultisiteService"/>
            <assetui-search:getContainerSize var="contentLabelsCount" container="${multisiteService.contentLabels}"/>
            <assetui-search:getContainerSize var="targetTypesCount" container="${multisiteService.targetTypes}"/>
            <c:if test="${formHandler.projectActive or contentLabelsCount gt 1 or targetTypesCount gt 1}">
              <script type="text/javascript">
                function showEnvironmentDialog() {
                  top.assetManagerDialog.titleNode.innerHTML = '<fmt:message key="searchTestingEnvironmentPicker.environment.title"/>';
                  <c:url var="assetManagerDialogHref" context="${config.contextRoot}" value="/searchTesting/input/environmentPicker.jsp">
                    <c:param name="formHandlerPath" value="${formHandlerPath}" />
                  </c:url>
                  top.assetManagerDialog.setHref('<c:out value="${assetManagerDialogHref}" />');
                  top.assetManagerDialog.execute = function(formContents) {
                    document.getElementById('testingContentLabel').value = formContents.contentLabel;
                    document.getElementById('testingTargetType').value = formContents.targetType;
                    document.getElementById('testingMode').value = formContents.testingMode;
                    document.getElementById("selectSearchTestingEnvironment").click();
                  };
                  top.assetManagerDialog.show();
                }
              </script>
              <dspel:input id="selectSearchTestingEnvironment" type="submit" bean="${formHandlerPath}.selectSearchTestingEnvironment" style="display: none" />
              <input type="button" value="<fmt:message key="searchTestingEnvironmentPicker.select.link"/>" onclick="showEnvironmentDialog(); return false" />
            </c:if>
          </c:otherwise>
        </c:choose>
      </dd>
    </dl>
  </div>

  <fieldset class="atg_flushPanel">
    <legend><span><fmt:message key="searchTestingInputs.testingEnvironment.legend"/></span></legend>
    <dl class="atg_smerch_configList">
      <%-- Show fields for holding parentFolder and name properties only if this is a non-transient asset. --%>
      <c:choose>
        <c:when test="${transient}">
          <dspel:input id="hiddenParentFolderField" type="hidden" bean="${formHandlerPath}.value.parentFolder.repositoryId" />
          <dspel:input id="hiddenDisplayNameField" type="hidden" bean="${formHandlerPath}.searchTestName" /> 
        </c:when>
        <c:otherwise>
          <dt><fmt:message key="searchTestingInputs.searchTest.name"/></dt>
          <dd><dspel:input id="displayNameField" iclass="formTextField" type="text" size="50" bean="${formHandlerPath}.searchTestName"
                           oninput="return queryTextOnChange()" onpropertychange="return queryTextOnChange()"/></dd>

          <dt><fmt:message key="searchTestingInputs.searchTest.parentFolder"/></dt>
          <dd>
            <dspel:input type="hidden" id="parentId" bean="${formHandlerPath}.value.parentFolder.repositoryId" />
            <dspel:getvalueof var="displayNameValue" bean="${formHandlerPath}.value.parentFolder.displayName"/>
            <c:if test="${empty displayNameValue}">
               <dspel:getvalueof var="displayNameValue" bean="${formHandlerPath}.value.parentFolder.repositoryId"/>
            </c:if>
            <span id="parentDisplayName"><c:out value="${displayNameValue}" /></span>
            <input type="button" value="Select" onclick="parentPickAssetFunc(); return false;" />
            <script type="text/javascript">
              function parentOnPickFunc(selected) {
                markAssetModified();
                document.getElementById("parentId").value = selected.id;
                document.getElementById("parentDisplayName").innerHTML = selected.displayName;
              }
            
              function parentPickAssetFunc() {
                var allowableTypes = new Array();
                var assetType = new Object();
                assetType.typeCode       = "repository";
                assetType.displayName    = "NotUsed";
                assetType.repositoryName = "<c:out value='${formHandler.repository.repositoryName}'/>";
                assetType.createable     = "false";
          
                // The following two properties determine which view is to be displayed in the AP
                // NOTE: the type name needs to exist in the repository but it is not used in our AP plugin.
                assetType.typeName       = "searchTestFolder";
                assetType.componentPath  = "<c:out value='${formHandler.repository.absoluteName}'/>";
                allowableTypes[allowableTypes.length] = assetType;
          
                var viewConfiguration = new Array();
          
                <%-- get the tree component --%>
                <web-ui:getTree var="treePath"
                                repository="${formHandler.repository.absoluteName}"
                                itemType="searchTestFolder"
                                treeRegistry="${sessionInfo.treeRegistryPath}"/>
                viewConfiguration.treeComponent = "<c:out value='${treePath}'/>";
                viewConfiguration.itemTypes = "searchTestFolder";
                var picker = new AssetBrowser();
                picker.mapMode                 = "AssetManager.assetPicker";
                picker.clearAttributes         = "true";
                picker.pickerURL               = "<c:out value='${configInfo.assetPickerRoot}${configInfo.assetPicker}'/>?apView=1&";
                picker.assetPickerTitle        = "Select an item";
                picker.browserMode             = "pick";
                picker.isAllowMultiSelect      = "false";
                picker.createMode              = "none";
                picker.onSelect                = "parentOnPickFunc";
                picker.closeAction             = "hide";
                picker.assetTypes              = allowableTypes;
                picker.assetPickerParentWindow = window;
                picker.assetPickerFrameElement = parent.document.getElementById("iFrame");
                picker.assetPickerDivId        = "browser";
                <c:choose>
                  <c:when test="${not empty treePath}">
                    picker.defaultView       = "Browse";
                    picker.viewConfiguration = viewConfiguration;
                  </c:when>
                  <c:otherwise>
                    picker.defaultView       = "Search";
                    picker.mapMode           = "AssetManager.assetPicker.search";
                  </c:otherwise>
                </c:choose>
                picker.invoke();
              }
            </script>
          </dd>
        </c:otherwise>
      </c:choose>
      <ee:isMultisiteMode var="multisiteMode"/>
      <c:if test="${multisiteMode}">
        <dspel:droplet name="/atg/search/multisite/droplets/FindSiteAssociations">
          <%-- Input Parameters --%>
          <dspel:param name="contentLabels" value="${formHandler.testEnvironmentModel.contentLabel}"/>

          <dspel:oparam name="output">
            <dspel:getvalueof var="availableSites" param="sites"/>

            <assetui-search:getContainerSize var="availableSitesCount" container="${availableSites}"/>
            <c:if test="${availableSitesCount gt 0}">
              <c:choose>
                <c:when test="${availableSitesCount eq 1}">
                  <c:forEach var="site" items="${availableSites}">
                    <dspel:input id="searchSiteValue" type="hidden" bean="${formHandlerPath}.searchSite" value="${site.repositoryId}" />
                    <c:if test="${empty formHandler.searchSite}">
                      <c:set target="${formHandler}" property="searchSite" value="${site.repositoryId}" />
                    </c:if>
                  </c:forEach>
                </c:when>
                <c:otherwise>
                  <dt><fmt:message key="searchTestingInputs.searchSite.text"/></dt>
                  <dd>
                    <dspel:select id="selectSearchSiteDropdown" bean="${formHandlerPath}.searchSite" onchange="issueRequest('/AssetUI-Search/searchTesting/input/updateAssetPickerSiteFilter.jsp?siteId='+this.options[this.selectedIndex].value, null, null, null, true)">
                      <c:forEach var="site" items="${availableSites}">
                        <dspel:option value="${site.repositoryId}"><c:out value="${site.itemDisplayName}"/></dspel:option>
                        <c:if test="${empty formHandler.searchSite}">
                          <c:set target="${formHandler}" property="searchSite" value="${site.repositoryId}" />
                        </c:if>
                      </c:forEach>
                    </dspel:select>
                  </dd>
                  <dt>&nbsp;</dt>
                  <dd>
                    <label>
                      <dspel:input bean="${formHandlerPath}.crossSiteSearch" type="checkbox" name="crossSiteSearch" />
                      <fmt:message key="searchTestingInputs.crossSiteSearch.checkbox"/>
                    </label>
                  </dd>
                </c:otherwise>
              </c:choose>
            </c:if>
          </dspel:oparam>
        </dspel:droplet>

        <dspel:include page="updateAssetPickerSiteFilter.jsp">
          <dspel:param name="siteId" value="${formHandler.searchSite}" />
        </dspel:include>
      </c:if>
      <dspel:getvalueof bean="${formHandlerPath}.availableLocales" var="availableLocales" />
      <c:if test="${!empty availableLocales}">
        <assetui-search:getContainerSize var="availableLocalesCount" container="${availableLocales}"/>
        <c:choose>
          <c:when test="${availableLocalesCount eq 1}">
            <dspel:input type="hidden" bean="${formHandlerPath}.language" value="${availableLocales[0]}" />
          </c:when>
          <c:otherwise>
            <dt><fmt:message key="searchTestingInputs.language.text"/></dt>
            <dd>
              <dspel:select id="selectLanguageDropdown" bean="${formHandlerPath}.language">
                <c:forEach var="availableLocale" items="${formHandler.availableLocales}">
                  <dspel:option value="${availableLocale}"><assetui-search:getLocaleName locale="${availableLocale}" /></dspel:option>
                </c:forEach>
              </dspel:select>
            </dd>
          </c:otherwise>
        </c:choose>
      </c:if>
      <dt><fmt:message key="searchTestingInputs.searchConfig.text"/></dt>
      <dd><dspel:include page="selectSearchConfig.jsp" /></dd>
      <dt>
        <c:choose>
          <c:when test="${formHandler.exclusiveCategoryAndSearchTextModeValue eq formHandler.categoryAndSearchTextMode}">
            <fmt:message key="searchTestingInputs.searchInput.text"/>
          </c:when>
          <c:otherwise>
            <fmt:message key="searchTestingInputs.searchText.text"/>
          </c:otherwise>
        </c:choose>
      </dt>
      <dd>
        <c:if test="${formHandler.exclusiveCategoryAndSearchTextModeValue eq formHandler.categoryAndSearchTextMode}">
          <label><dspel:input bean="${formHandlerPath}.searchInputMode" type="radio" name="searchInputMode" value="${formHandler.searchTextSearchInputModeValue}" onclick="return searchInputModeChange('${formHandler.searchTextSearchInputModeValue}')" />
          <fmt:message key="searchTestingInputs.searchText.text"/></label>
        </c:if>
        <c:if test="${formHandler.exclusiveCategoryAndSearchTextModeValue eq formHandler.categoryAndSearchTextMode and formHandler.searchTextSearchInputModeValue ne formHandler.searchInputMode}">
          <dspel:setvalue bean="${formHandlerPath}.value.queryText" value="" />
        </c:if>
        <dspel:input id="queryText" iclass="formTextField" type="text" size="50" bean="${formHandlerPath}.value.queryText" disabled="${formHandler.exclusiveCategoryAndSearchTextModeValue eq formHandler.categoryAndSearchTextMode and formHandler.searchTextSearchInputModeValue ne formHandler.searchInputMode}" oninput="return queryTextOnChange()" onpropertychange="return queryTextOnChange()"/>
      </dd>
      <dt>
        <c:if test="${formHandler.cooperativeCategoryAndSearchTextModeValue eq formHandler.categoryAndSearchTextMode}">
          <fmt:message key="searchTestingInputs.category.text"/>
        </c:if>
      </dt>
      <dd>
        <c:if test="${formHandler.exclusiveCategoryAndSearchTextModeValue eq formHandler.categoryAndSearchTextMode}">
          <label><dspel:input bean="${formHandlerPath}.searchInputMode" type="radio" name="searchInputMode" value="${formHandler.categorySearchInputModeValue}" onclick="return searchInputModeChange('${formHandler.categorySearchInputModeValue}');" />
          <fmt:message key="searchTestingInputs.category.text"/></label>
        </c:if>
        <c:if test="${!empty categoryPathHelper.categorySelectJsp}">
          <dspel:include otherContext="${categoryPathHelper.categoryJspContextRoot}" page="${categoryPathHelper.categorySelectJsp}" />
        </c:if>
      </dd>
      <dt><fmt:message key="searchTestingInputs.trackItems.text"/></dt>
      <dd><dspel:include page="selectTrackItems.jsp"/></dd>
      <dt><fmt:message key="searchTestingInputs.sortBy.text"/></dt>
      <dd>
        <dspel:select id="selectSortDropdown" bean="${formHandlerPath}.sortBy" onchange="return markAssetModified()">
          <dspel:option value="${propertyManager.relevanceSortByTypeEnumValue}">
            <fmt:message key="searchTestingInputs.textRelevance.text"/>
          </dspel:option>
          <c:if test="${not (empty searchTestingService.sortByPropertyNames)}">
            <c:forEach items="${searchTestingService.sortByPropertyNames}" var="propName">
              <assetui-search:propertyDisplayNameResolver propertyName="${propName}"
                  var="displayName"/>
              <c:choose>
                <c:when test="${not (empty displayName)}">
                  <c:set value="${displayName}" var="optionLabel"/>
                </c:when>
                <c:otherwise>
                  <c:set value="${propName}" var="optionLabel"/>
                </c:otherwise>
              </c:choose>
              <dspel:option value="${propName}___asc">
                <fmt:message key="searchTestingInputs.sortAsc.text">
                  <fmt:param>
                    <c:out value="${optionLabel}"/>
                  </fmt:param>
                </fmt:message>
              </dspel:option>
              <dspel:option value="${propName}___desc">
                <fmt:message key="searchTestingInputs.sortDesc.text">
                  <fmt:param>
                    <c:out value="${optionLabel}"/>
                  </fmt:param>
                </fmt:message>
              </dspel:option>
            </c:forEach>
          </c:if>
        </dspel:select>
      </dd>
      <dspel:include page="${formHandler.pageConfigMap.advancedInputsPage}" 
                   otherContext="${formHandler.pageConfigMap.advancedInputsContext}" />
      <dt><fmt:message key="searchTestingInputs.results.text"/></dt>
      <dd>
        <ul class="atg_horizontalList">
          <li><label><dspel:input bean="${formHandlerPath}.value.results" type="radio" name="searchResults" value="${propertyManager.standardResultsEnumValue}" onclick="return markAssetModified()" />
          <fmt:message key="searchTestingInputs.standart.radio"/></label></li>
          <li><label><dspel:input bean="${formHandlerPath}.value.results" type="radio" name="searchResults" value="${propertyManager.rankingResultsEnumValue}" onclick="return markAssetModified()" />
          <fmt:message key="searchTestingInputs.rancCalc.radio"/></label></li>
          <c:if test="${formHandler.subitemsValid}">
            <li><label><dspel:input bean="${formHandlerPath}.value.results" type="radio" name="searchResults" value="${propertyManager.subItemsResultsEnumValue}" onclick="return markAssetModified()" />
            <fmt:message key="searchTestingInputs.subItems.radio"/></label></li>
          </c:if>
          <li><label><dspel:input bean="${formHandlerPath}.value.results" type="radio" name="searchResults" value="${propertyManager.noneResultsEnumValue}" onclick="return markAssetModified()" />
          <fmt:message key="searchTestingInputs.none.radio"/></label></li>
        </ul>
      </dd>
      <dt><fmt:message key="searchTestingInputs.outputDetails.text"/></dt>
      <dd>
        <ul class="atg_horizontalList">
          <li><label><dspel:input bean="${formHandlerPath}.value.testTypes" type="checkbox" name="searchOutput" value="${propertyManager.rankingTestTypesEnumValue}" onclick="return markAssetModified()" />
          <fmt:message key="searchTestingInputs.searchConfig.checkbox"/></label></li>
          <li><label><dspel:input bean="${formHandlerPath}.value.testTypes" type="checkbox" name="searchOutput" value="${propertyManager.termsTestTypesEnumValue}" onclick="return markAssetModified()" />
          <fmt:message key="searchTestingInputs.terms.checkbox"/></label></li>
          <li><label>
            <c:choose>
              <c:when test="${!formHandler.testTypesExist && transient}">
                <dspel:input bean="${formHandlerPath}.value.testTypes" checked="${true}" type="checkbox" name="searchOutput" value="${propertyManager.rulesTestTypesEnumValue}" onclick="return markAssetModified()" />
              </c:when>
              <c:otherwise>
                <dspel:input bean="${formHandlerPath}.value.testTypes"  type="checkbox" name="searchOutput" value="${propertyManager.rulesTestTypesEnumValue}" onclick="return markAssetModified()" />
              </c:otherwise>
            </c:choose>
          <fmt:message key="searchTestingInputs.queryRules.checkbox"/></label></li>
          <li><label><dspel:input bean="${formHandlerPath}.value.testTypes" type="checkbox" name="searchOutput" value="${propertyManager.trackingTestTypesEnumValue}" onclick="return markAssetModified()" />
          <fmt:message key="searchTestingInputs.trackItems.checkbox"/></label></li>
          <li><label><dspel:input bean="${formHandlerPath}.value.testTypes" type="checkbox" name="searchOutput" value="${propertyManager.facetTestTypesEnumValue}" onclick="return markAssetModified()" />
          <fmt:message key="searchTestingInputs.facets.checkbox"/></label></li>
        </ul>
      </dd>
    </dl>

    <dspel:include page="facets.jsp"/>

    <div class="atg_panelFooter">
      <ul>
        <li>
          <dspel:input id="clearButton" type="submit" bean="${formHandlerPath}.clear" style="display:none"/>
          <input id="searchClearButton" type="button" value="Clear" onclick="return clearForm(<c:out value='${transient}'/>);" />
        </li>
        <c:choose>
          <c:when test="${transient}">
            <li><input id="searchSaveButton" type="button" disabled="true" value="<fmt:message key="searchTestingInputs.save.button"/>" onclick="saveAs(); return false" 
              title="<fmt:message key='searchTestingInputs.save.button.disabled.title'/>" /></li>
          </c:when>
          <c:otherwise>
            <li><input id="searchSaveButton" type="button" disabled="true" value="<fmt:message key="searchTestingInputs.save.button"/>" onclick="actionButtonClicked('saveButton'); return false" 
              title="<fmt:message key='searchTestingInputs.save.button.disabled.title'/>" /></li>
          </c:otherwise>
        </c:choose>
        <li class="atg_searchButton"><fmt:message key="searchTestingInputs.search.button" var="searchButton" /><dspel:input id="searchButton" type="submit" bean="${formHandlerPath}.search" value="${searchButton}" /></li>
      </ul>
    </div>

  </fieldset>

  <c:if test="${formHandler.wasSearch}">
    <dspel:include page="/searchTesting/output/results.jsp"/>
  </c:if>

  <!-- Set application name from session info -->
  <dspel:setvalue bean="${formHandlerPath}.value.application" value="${configInfo.sessionInfo.currentApplicationName}"/>
  <c:if test="${empty formHandler.value.creationAuthor}">
    <dspel:setvalue bean="${formHandlerPath}.value.creationAuthor" value="${profile.repositoryId}"/>
  </c:if>
  <dspel:setvalue bean="${formHandlerPath}.value.lastModifiedAuthor" value="${profile.repositoryId}"/>
  <dspel:setvalue bean="${formHandlerPath}.value.ownerId" value="${profile.repositoryId}"/>

  

  <script type="text/javascript">
    disableSaveConfirmation = true;

    dojo.addOnLoad(function() {
      // Look for all links requiring popup with the class of atg_smerch_ciPop
      dojo.query("a.atg_smerch_ciPop", "rightPaneIframe").forEach(function(node) {
        // Add popup even to the links
        this.dojo.connect(node, "onclick", function(e) {
          e.preventDefault();
          top.assetManagerDialog.titleNode.innerHTML = '<fmt:message key='searchTestingResult.contentItem.title'/>';
          top.assetManagerDialog.setHref(e.target.href);
          top.assetManagerDialog.show();
        });
      });
    });
    
    function searchConfig_drillDown(searchConfigURI) {
      // Derive the drill-down URL based on the currently specified item ID.
      var propPage = '<c:out value="${assetEditorURL}"/>' +
                     '?assetURI=' + escape(searchConfigURI) +
                     '&pushContext=true';
      // Force a save/create of the current asset, then go to new page.
      parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(propPage, null, null, null, null, true, null);
      return false;
    }
    
    function saveAs() {
      showSaveAsPicker("onSaveAsPickerSelect"); 
    }

    //
    // Invoke Asset Picker iFrame
    //
    function showSaveAsPicker(onSelectMethodName) {
      var allowableTypes = new Array( 0 );
      var allowableTypesCommaList = "";

      <web-ui:getTree var="treepath" 
                      repository="${formHandler.repositoryItem.repository.absoluteName}" 
                      itemType="${formHandler.repositoryItem.itemDescriptor.itemDescriptorName}"
                      treeRegistry="${sessionInfo.treeRegistryPath}"/>

      <c:if test="${not empty treepath}">
        // get the possible parent types for the current item type
        <web-ui:getPossibleParentTypes var="typelist" 
                                       type="${formHandler.repositoryItem.itemDescriptor.itemDescriptorName}"
                                       tree="${treepath}"/>

        // for each of the possible parent types, add an "assetType" to the asset picker's list
        // Put only a single type in the picker.assetTypes list so that we skip the screen
        // where you have to pick a repository and item type.  Put all the types in
        // viewConfiguration.itemTypes because this will allow multiple pickable types.
        <c:forEach var="parenttype" items="${typelist}" varStatus="loop">
          <c:set var="index" value="${loop.index}"/>
          var assetType = new Object();
          assetType.typeCode       = 'repository';
          assetType.displayName    = '<c:out value="${parenttype}"/>';
          assetType.typeName       = '<c:out value="${parenttype}"/>';
          assetType.repositoryName = '<c:out value="${asset.containerName}"/>';
          assetType.componentPath  = '<c:out value="${formHandler.repositoryItem.repository.absoluteName}"/>';
          assetType.createable     = "true"; 
          <c:choose>
            <c:when test="${index == 0}">
              allowableTypes[ allowableTypes.length ] = assetType;
            </c:when>
            <c:otherwise>
              allowableTypesCommaList += ",";
            </c:otherwise>
          </c:choose>
          allowableTypesCommaList += '<c:out value="${parenttype}"/>';
        </c:forEach>

        var viewConfiguration = new Array();

        viewConfiguration.treeComponent="<c:out value='${treepath}'/>";
        viewConfiguration.itemTypes = allowableTypesCommaList;
 
        var picker = new AssetBrowser();
        picker.mapMode                 = "AssetManager.assetPicker";
        picker.clearAttributes         = "true";
        picker.pickerURL               = '<c:out value="${configInfo.assetPickerRoot}${configInfo.assetPicker}"/>?apView=1&';
        <fmt:message var="assetPickerTitle" key="searchTestingInputs.saveAsPickerTitle"/>
        <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>
        picker.assetPickerTitle        = "<c:out value='${assetPickerTitle}'/>";
        picker.browserMode             = "pick";
        picker.hideTabs                = "true";
        picker.isAllowMultiSelect      = "false";
        picker.createMode              = "none";
        picker.onSelect                = onSelectMethodName;
        picker.closeAction             = "hide";
        picker.defaultView             = "Browse";
        picker.assetTypes              = allowableTypes;
        picker.assetPickerParentWindow = window;
        picker.assetPickerFrameElement = parent.document.getElementById('iFrame');
        picker.viewConfiguration       = viewConfiguration;
        picker.assetPickerDivId        = "browser";
        picker.extraFieldId            = "<c:out value='${formHandler.itemDescriptor.itemDisplayNameProperty}'/>";
        picker.extraFieldLabel         = "<fmt:message key='searchTestingInputs.searchTest.name'/>";
        picker.extraFieldValueRequired = "true";
        picker.extraFieldRequiredMessage = "<fmt:message key='searchTestingInputs.searchTest.required.message'/>";
        picker.invoke();
      </c:if>
    }

    //
    // this is called when OK is clicked in the asset picker
    //
    function onSaveAsPickerSelect(checkedItem, attributes ) {
      // set the parentFolder and name properties with values from the picker
      document.getElementById("hiddenParentFolderField").value=checkedItem.id;
      document.getElementById("hiddenDisplayNameField").value=attributes.displayName;

      // click the "real" create/add button
      actionButtonClicked('addButton');
    }

    // called when transient Search Test is being left
    // in such case addButton mustn't be clicked there as it will be clicked latter by finishSaveBeforeLeaveAsset method 
    function onLeaveSearchTestPickerSelect(checkedItem, attributes ) {
      // set the parentFolder and name properties with values from the picker
      document.getElementById("hiddenParentFolderField").value=checkedItem.id;
      document.getElementById("hiddenDisplayNameField").value=attributes.displayName;

      //parent.window.frames.rightPaneIframe.document.location = propPage;
      var addSuccessURL = parent.window.frames.rightPaneIframe.document.getElementById("addSuccessURLField");      
      if (parent.atg.assetmanager.saveconfirm.getRightURL()) {
        addSuccessURL.value = parent.atg.assetmanager.saveconfirm.getRightURL();
      } else {
      	if (parent.atg.assetmanager.saveconfirm.getTopURL()) {
      	  addSuccessURL.value = addSuccessURL.value + "&setParentURL=" + escape(parent.atg.assetmanager.saveconfirm.getTopURL());
      	}
      }
          
      actionButtonClicked('addButton');
       
    }
  </script>

  <script type="text/javascript">
    /* Clears search inputs. Sets pop ups to default values. */
    function clearForm(pTransient){
    
      if (pTransient){
        document.getElementById('hiddenParentFolderField').value = '';
        document.getElementById('hiddenDisplayNameField').value = '';
      }

      document.getElementById('searchTestingInputsApplyFacets').value = false;
      document.getElementById('searchTestingInputsFacetClicked').value = false;
      document.getElementById('searchTestingEditorDisablePositioning').value = false;
      document.getElementById('inputsDebugExclusion').value = '';
      
      //language dropdown could be absent
      var languageDropdown = document.getElementById('selectLanguageDropdown');
      if (languageDropdown){
        languageDropdown.options[0].selected = true;
      }
      //sort dropdown could be absent
      var sortDropdown = document.getElementById('selectSortDropdown');
      if (sortDropdown){
        sortDropdown.options[0].selected = true;
      }
      //exclusive mode radio buttons
      var searchInputElements = document.getElementsByName('searchInputMode');
      if (searchInputElements && searchInputElements[0]){
        searchInputElements[0].checked = true;
      }
      if(clearCategory){
        clearCategory();
      }
      //query text
      var queryTextElement = document.getElementById('queryText');
      queryTextElement.disabled = false;
      queryTextElement.value = '';
      
      clearRefinementSelection();
      clearSearchConfig();
      clearTrackItemsInput();
      //result radio buttons
      var searchResultElements = document.getElementsByName('searchResults');
      searchResultElements[0].checked = true;
      //output checkboxes
      var searchOutputElements = document.getElementsByName('searchOutput');
      for (var i = 0; i < searchOutputElements.length; i++){
        if (searchOutputElements[i].value != '<c:out value="${propertyManager.rulesTestTypesEnumValue}"/>'){
          searchOutputElements[i].checked = false;
        } else {
          searchOutputElements[i].checked = true;
        }
      } 
      markAssetModified();
      document.getElementById('clearButton').click();
    }


    function doOnEnterPressed(evt) {
      if (null == evt) {
        evt = event;
      }
      var charCode = evt.charCode;
      if ((null == charCode)
          || (0 == charCode)) {
        charCode = evt.keyCode;
      }
      if (null == charCode) {
        charCode = evt.which;
      }

      if (13 == charCode) {
        document.getElementById("searchButton").click();
        return false;
      }
    }

    var btn = document.getElementById("searchButton");
    if ((btn != null)
        && (btn.form != null)
        && (btn.form != undefined)) {
      btn.form.onkeydown = doOnEnterPressed;
    }
    
  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/inputs.jsp#2 $$Change: 651448 $--%>
