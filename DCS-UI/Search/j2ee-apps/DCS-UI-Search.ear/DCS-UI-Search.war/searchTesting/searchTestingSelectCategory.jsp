<%--
  The locale property editor

  The following request-scoped variables are expected to be set:
  
  @param  mpv          A MappedPropertyView item for this view

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/searchTestingSelectCategory.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:getvalueof var="categoryId" param="categoryId"/>
  <%-- see if we have a prepopulated categoryId --%>
  <c:if test="${not empty categoryId}">
    <dspel:setvalue bean="${formHandlerPath}.preloadedCategoryId" value="${categoryId}"/>
    <script type="text/javascript">
      dojo.connect(window, "onload", markAssetModified);
    </script>
  </c:if>

  <dspel:importbean var="config" bean="/atg/commerce/search/web/Configuration"/>
  <fmt:setBundle var="bundle" basename="${config.resourceBundle}"/>

  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
  <c:set var="formHandler"     value="${requestScope.formHandler}"/>

  <c:if test="${formHandler.exclusiveCategoryAndSearchTextModeValue eq formHandler.categoryAndSearchTextMode and formHandler.categorySearchInputModeValue ne formHandler.searchInputMode}">
    <dspel:setvalue bean="${formHandlerPath}.value.categoryId" value="" />
  </c:if>
  <dspel:getvalueof bean="${formHandlerPath}.value.categoryId" var="categoryId" />
  <input id="categoryCurrId" type="hidden" value="<c:out value="${categoryId}"/>" disabled="disabled"/>

  <c:set var="id" value="cats${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>

  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle var="assetManagerBundle" basename="${assetManagerConfig.resourceBundle}"/>

  <c:set var="sessionInfo" value="${assetManagerConfig.sessionInfo}"/>

  <dspel:importbean var="repository"  bean="/atg/commerce/catalog/MerchandisingProductCatalog"/>

  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="onClickSelectButton" value="onClickSelectButton_${id}"/>
  <c:set var="onSelectOKButton"    value="onSelectOKButton_${id}"/>
  <c:set var="onSelectClearButton" value="onSelectClearButton_${id}"/>
  <c:set var="selectSourceInputId" value="selectSourceInput_${id}"/>
  <c:set var="selectSubmitId"      value="selectSubmit_${id}"/>

  <%-- A hidden input that is used to pass the selected source category ID to
       the handleCopyRankings method --%>
  <dspel:input type="hidden" id="${selectSourceInputId}" bean="${formHandlerPath}.value.categoryId" />

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
      assetType.typeName       = "category";
      assetType.componentPath  = "<c:out value='${repository.absoluteName}'/>";
      allowableTypes[allowableTypes.length] = assetType;

      var viewConfiguration = new Array();

      <%-- get the tree component --%>
      <web-ui:getTree var="treePath"
                      repository="${repository.absoluteName}"
                      itemType="category"
                      treeRegistry="${sessionInfo.treeRegistryPath}"/>
      viewConfiguration.treeComponent = "<c:out value='${treePath}'/>";
      viewConfiguration.itemTypes = "category";

      var encodedItemType = "<c:out value='${propertyView.encodedComponentType}'/>";
      var decodedItemType = encodedItemType.split(":");

      var picker = new AssetBrowser();
      picker.mapMode                 = "AssetManager.assetPicker";
      picker.clearAttributes         = "true";
      picker.pickerURL               = "<c:out value='${assetManagerConfig.assetPickerRoot}${assetManagerConfig.assetPicker}'/>?apView=1&";
      <fmt:message var="assetPickerTitle" bundle="${assetManagerBundle}" key="propertyEditor.assetPickerTitle"/>
      <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>
      picker.assetPickerTitle        = "<c:out value='${assetPickerTitle}'/>";
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
      if (selected.id == document.getElementById('categoryCurrId').value) {
        alert("<fmt:message bundle="${bundle}" key='searchTestingSelectCategory.noSelectCurrentCategory'/>");
      }
      else {
        markSearchCriteriaChanged();
        document.getElementById("<c:out value='${selectSourceInputId}'/>").value = selected.id;
        document.getElementById("<c:out value='${selectSubmitId}'/>").click();
      }
    }

    function <c:out value="${onSelectClearButton}"/>(selected) {
      markSearchCriteriaChanged();
      document.getElementById("<c:out value='${selectSourceInputId}'/>").value = "";
      document.getElementById("<c:out value='${selectSubmitId}'/>").click();
    }

    var categorySearchInputModeChange = function (mode) {
      var disabled = ('<c:out value="${formHandler.searchTextSearchInputModeValue}"/>' == mode);

      var categoryId = document.getElementById('<c:out value="${selectSourceInputId}"/>');
      if (categoryId && disabled)
        categoryId.value = '';

      var displayName = document.getElementById('<c:out value="${selectSourceInputId}"/>_displayName');
      if (displayName)
        displayName.innerHTML = '<fmt:message bundle="${bundle}" key="searchTestingSelectCategory.all.text"/>';
        
      var button = document.getElementById('<c:out value="${selectSourceInputId}"/>_button');
      if (button)
        button.disabled = disabled;
        
      return true;
    }
  </script>

  <span id="<c:out value="${selectSourceInputId}"/>_displayName">
  <c:choose>
    <c:when test="${!empty categoryId}">
      <c:out value="${formHandler.categoryName}" />

      <input type="image" src="/AssetManager/images/icon_propertyClear.gif" onclick="<c:out value='${onSelectClearButton}()'/>; return false" title="Clear" />
    </c:when>
    <c:otherwise>
      <fmt:message bundle="${bundle}" key="searchTestingSelectCategory.all.text"/>
    </c:otherwise>
  </c:choose>
  </span>

  <dspel:input type="submit" id="${selectSubmitId}" bean="${formHandlerPath}.selectCategory" style="display:none"/>
  <input id="<c:out value="${selectSourceInputId}"/>_button" type="button" value="<fmt:message bundle="${bundle}" key="searchTestingSelectCategory.select.text"/>"<c:if test="${formHandler.exclusiveCategoryAndSearchTextModeValue eq formHandler.categoryAndSearchTextMode and formHandler.categorySearchInputModeValue ne formHandler.searchInputMode}"> disabled</c:if> onclick="<c:out value='${onClickSelectButton}()'/>; return false;" />
  
  <script type="text/javascript">
    function clearCategory(){
      document.getElementById('categoryCurrId').value = '';
      document.getElementById("<c:out value='${selectSourceInputId}'/>").value = '';
      document.getElementById('<c:out value="${selectSourceInputId}"/>_displayName').innerHTML = '<fmt:message bundle="${bundle}" key="searchTestingSelectCategory.all.text" /> ';
    }
  </script>

</dspel:page>
<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/searchTesting/searchTestingSelectCategory.jsp#2 $$Change: 651448 $--%>
