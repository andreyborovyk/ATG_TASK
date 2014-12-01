<%--
  Search Configuration input controls and popup picker.

  The following parameters are passed into the page:

  @param searchConfigId   Optional, pre-populates the searchConfig field with this id. 

  The following request-scoped variables are expected to be set:
  
  @param  formHandlerPath Nucleus path to the SearchTestingFormHandler
  @param  formHandler     SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/selectSearchConfig.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"         uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search"        %>

<dspel:page>

  <dspel:getvalueof var="searchConfigIdParam" param="searchConfigId"/>
  <%-- see if we have a prepopulated searchConfigId --%>
  <c:if test="${not empty searchConfigIdParam}">
    <dspel:setvalue bean="${formHandlerPath}.preloadedSearchConfigId" value="${searchConfigIdParam}"/>
    <script type="text/javascript">
      dojo.connect(window, "onload", markAssetModified);
    </script>
  </c:if>

  <c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
  <c:set var="formHandler"     value="${requestScope.formHandler}"/>

  <c:set value="/atg/search/config/SearchDimensionManagerService" var="searchDimensionManagerServicePath" />
  <dspel:importbean bean="${searchDimensionManagerServicePath}" var="searchDimensionManagerService" />

  <dspel:getvalueof bean="${formHandlerPath}.value.searchConfigName" var="searchConfigId" />
  <input id="searchConfigCurrId" type="hidden" value="<c:out value="${searchConfigId}"/>" disabled="disabled"/>

  <c:set var="id" value="searchConfig"/>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="sessionInfo" value="${assetManagerConfig.sessionInfo}"/>

  <dspel:importbean var="propManager" bean="/atg/search/repository/SearchConfigurationPropertyManager"/>
  <dspel:importbean var="repository"  bean="/atg/search/repository/RefinementRepository"/>

  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="onClickSelectButton" value="onClickSelectButton_${id}"/>
  <c:set var="onSelectOKButton"    value="onSelectOKButton_${id}"/>
  <c:set var="onSelectClearButton" value="onSelectClearButton_${id}"/>
  <c:set var="selectSourceInputId" value="selectSourceInput_${id}"/>
  <c:set var="selectSubmitId"      value="selectSubmit_${id}"/>

  <%-- A hidden input that is used to pass the selected source config ID to
       the handleCopyRankings method --%>
  <dspel:input id="selectDimensionType" type="hidden" bean="${formHandlerPath}.selectDimensionType" />
  <c:forEach items="${searchDimensionManagerService.searchDimensions}" var="searchDimension">
    <dspel:input id="${searchDimension.dimensionName}DimensionValue" type="hidden" bean="${formHandlerPath}.dimensionValues.${searchDimension.dimensionName}" />
  </c:forEach>
  <dspel:input id="previewUserIdValue" type="hidden" bean="${formHandlerPath}.value.previewUserId" />

  <dspel:input type="hidden" id="${selectSourceInputId}" bean="${formHandlerPath}.value.searchConfigName"/>

  <dspel:importbean bean="/atg/search/config/SearchDimensionManagerService" var="searchDimensionManagerService" />

  <script type="text/javascript">

    // This function is called when the user clicks the "Copy From..." button.
    // It allows the user to use the asset picker to specify a searchConfig
    // from which to copy property weightings.
    //
    function <c:out value="${onClickSelectButton}"/>() {

      var allowableTypes = new Array();
      var assetType = new Object();
      assetType.typeCode       = "repository";
      assetType.displayName    = "NotUsed";
      assetType.repositoryName = "<c:out value='${repository.repositoryName}'/>";
      assetType.createable     = "false";

      // The following two properties determine which view is to be displayed in the asset picker.
      // NOTE: the type name needs to exist in the repository but it is not used in our asset picker plugin.
      assetType.typeName       = "<c:out value='${propManager.searchConfigItemName}'/>";
      assetType.componentPath  = "<c:out value='${repository.absoluteName}'/>";
      allowableTypes[allowableTypes.length] = assetType;

      var viewConfiguration = new Array();

      <%-- get the tree component --%>
      <web-ui:getTree var="treePath"
                      repository="${repository.absoluteName}"
                      itemType="${propManager.searchConfigItemName}"
                      treeRegistry="${sessionInfo.treeRegistryPath}"/>
      viewConfiguration.treeComponent = "<c:out value='${treePath}'/>";
      viewConfiguration.itemTypes = "<c:out value='${propManager.searchConfigItemName}'/>";


      var picker = new AssetBrowser();
      picker.mapMode                 = "AssetManager.assetPicker";
      picker.clearAttributes         = "true";
      picker.pickerURL               = "<c:out value='${assetManagerConfig.assetPickerRoot}${assetManagerConfig.assetPicker}'/>?apView=1&";
      picker.assetPickerTitle        = '<fmt:message key="searchTestingDimensionValuesPicker.selectItem.pickerTitle"/>';
      picker.browserMode             = "pick";
      picker.createMode              = "none";
      picker.onSelect                = "<c:out value='${onSelectOKButton}'/>";
      picker.closeAction             = "hide";
      picker.assetTypes              = allowableTypes;
      picker.assetPickerParentWindow = window;
      picker.assetPickerFrameElement = parent.document.getElementById("iFrame");
      picker.assetPickerDivId        = "browser";
      picker.isAllowMultiSelect      = "false";
      picker.defaultView             = "Browse";
      picker.viewConfiguration       = viewConfiguration;
      picker.invoke();
    }

    //
    // This function is called when the user makes a selection from the asset
    // picker that is displayed when the "Copy From..." button is clicked.
    //
    // @param  selected  An object containing info about the selected asset
    //
    function <c:out value="${onSelectOKButton}"/>(selected) {
      if (selected.id == document.getElementById('searchConfigCurrId').value) {
        alert("<fmt:message key='selectSearchConfig.noSelectCurrentConfig'/>");
      }
      else {
        markAssetModified();
        document.getElementById('selectDimensionType').value = '<c:out value="${formHandler.selectDirectlyValue}" />';
        document.getElementById("<c:out value='${selectSourceInputId}'/>").value = selected.id;
        document.getElementById("<c:out value='${selectSubmitId}'/>").click();
      }
    }

    function <c:out value="${onSelectClearButton}"/>(selected) {
      markAssetModified();
      document.getElementById('selectDimensionType').value = '';
      document.getElementById("<c:out value='${selectSourceInputId}'/>").value = "";
      document.getElementById("<c:out value='${selectSubmitId}'/>").click();
    }

    function showDimensionValuesDialog() {
      top.assetManagerDialog.titleNode.innerHTML = '<fmt:message key="searchTestingDimensionValuesPicker.selectDimension.title"/>';
      <c:url var="assetManagerDialogHref" context="${config.contextRoot}" value="/searchTesting/input/dimensionValuesPicker.jsp">
        <c:param name="formHandlerPath" value="${formHandlerPath}" />
      </c:url>
      var href = '<c:out value="${assetManagerDialogHref}" />';
      var searchSiteValue = document.getElementById('searchSiteValue');
      if (searchSiteValue)
        href += '&siteId=' + searchSiteValue.value;
      else {
        var selectSearchSiteDropdown = document.getElementById('selectSearchSiteDropdown');
        if (selectSearchSiteDropdown)
          href += '&siteId=' + selectSearchSiteDropdown.options[selectSearchSiteDropdown.selectedIndex].value;
      }
      top.assetManagerDialog.setHref(href);
      top.assetManagerDialog.execute = function(formContents) {
        markAssetModified();
        document.getElementById('selectDimensionType').value = formContents.testDimensionType;
        <c:forEach items="${searchDimensionManagerService.searchDimensions}" var="searchDimension">
          document.getElementById('<c:out value="${searchDimension.dimensionName}DimensionValue"/>').value = formContents.<c:out value="${searchDimension.dimensionName}Select"/>;
        </c:forEach>
        document.getElementById('previewUserIdValue').value = formContents.userProfileID;
        document.getElementById('selectSearchConfigByDimensionSubmit').click();
      };
      top.assetManagerDialog.show();
    }

  </script>

  <span id='searchConfigSpan' title="">
    <c:choose>
      <c:when test="${formHandler.selectDirectlyValue eq formHandler.selectDimensionType}">
        <a href="#" 
          onclick="return searchConfig_drillDown('<c:out value="${formHandler.searchConfigURI}"/>')">
          <c:out value="${formHandler.searchConfigName}" />
        </a>
      </c:when>
      <c:when test="${formHandler.selectByUserProfileValue eq formHandler.selectDimensionType}">
        <fmt:message key="selectSearchConfig.userProfile.text"/> <c:out value="${formHandler.previewUserName}" />
      </c:when>
      <c:when test="${formHandler.selectByDimensionValuesValue eq formHandler.selectDimensionType}">
        <fmt:message key="selectSearchConfig.based.text"/>
        <c:set var="delimiter" value=", " />
        <c:forEach items="${formHandler.dimensionValues}" var="entry" varStatus="status">
          <c:set value="${entry.key}" var="dimensionName" />
          <c:set value="${entry.value}" var="dimensionValue" />
          <c:choose>
            <c:when test="${empty dimensionValue}">
              <fmt:message key="searchTestingDimensionValuesPicker.all.others.option" var="displayValue" />
            </c:when>
            <c:otherwise>
              <assetui-search:getDimensionDisplayValue var="displayValue" service="${dimensionName}" value="${dimensionValue}" />
            </c:otherwise>
          </c:choose>
          <c:if test="${status.last}">
            <c:set var="delimiter" value="" />
          </c:if>
          <dspel:getvalueof bean="${searchDimensionManagerServicePath}.searchDimensionMap.${dimensionName}" var="searchDimension" />
          <c:out value="${searchDimension.displayName}=${displayValue}${delimiter}" />
        </c:forEach>
      </c:when>
      <c:otherwise>
        <fmt:message key="selectSearchConfig.none.text"/>
      </c:otherwise>
      </c:choose>

  <c:if test="${formHandler.selectDirectlyValue eq formHandler.selectDimensionType or formHandler.selectByUserProfileValue eq formHandler.selectDimensionType or formHandler.selectByDimensionValuesValue eq formHandler.selectDimensionType}">
    <input type="image" src="/AssetManager/images/icon_propertyClear.gif" onclick="<c:out value='${onSelectClearButton}()'/>; return false" title="Clear" />
  </c:if></span>

  <dspel:input type="submit" id="${selectSubmitId}" bean="${formHandlerPath}.selectSearchConfig" style="display:none"/>
  <input type="button" value="Select Directly" onclick="<c:out value='${onClickSelectButton}()'/>; return false;" />

  <dspel:input type="submit" id="selectSearchConfigByDimensionSubmit" bean="${formHandlerPath}.selectSearchConfigByDimension" style="display:none"/>
  <input type="button" value="Select by Dimensions" onclick="showDimensionValuesDialog(); return false;" />
  
  <script type="text/javascript">

    function clearSearchConfig(){
      document.getElementById('searchConfigCurrId').value = '';
      document.getElementById('selectDimensionType').value = '';
      document.getElementById("<c:out value='${selectSourceInputId}'/>").value = '';
      document.getElementById('searchConfigSpan').innerHTML = '<fmt:message key="selectSearchConfig.none.text"/> ';
    }


    function trim(str) {
      return str.replace(/^\s+/, "").replace(/\s+$/, "");
    }


    function formatSearchConfigOutput() {
      var elem = document.getElementById("searchConfigSpan");
      if (null == elem) {
        return;
      }

      var TYPE_TEXT_NODE = 3;
      var textNode = elem.firstChild;
      if ((null == textNode)
          || (textNode.nodeType != TYPE_TEXT_NODE)) {
        return;
      }

      var text = (textNode.nodeValue != null)
          ? textNode.nodeValue : "";
      var tokens = text.split(/\s/);
      var output = "";
      for (var i = 0; i < tokens.length; i++) {
        var token = tokens[i];
        if (0 == trim(token).length) {
          continue;
        }

        output += (token + " ");
      }

      textNode.nodeValue = "";
      elem.innerHTML = atg.truncateMid(output, true) + elem.innerHTML;
    }

    dojo.connect(window, "onload", formatSearchConfigOutput);

  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/selectSearchConfig.jsp#2 $$Change: 651448 $--%>
