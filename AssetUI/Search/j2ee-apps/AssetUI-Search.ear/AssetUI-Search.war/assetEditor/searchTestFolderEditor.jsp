<%--
  Some jsp at the bottom of the searchTestFolder edit page.

  The following request-scoped variables are expected to be set:
    atgCurrentAsset

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/searchTestFolderEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>


<%@ taglib prefix="dspel"          uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="c"              uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="fmt"            uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search"        %>
<%@ taglib prefix="web-ui"         uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="configInfo" bean="/atg/web/assetmanager/ConfigurationInfo" />

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <dspel:importbean var="propertyManager" bean="/atg/search/repository/SearchTestingPropertyManager" />

  <dspel:getvalueof var="folderType" bean="${formHandlerPath}.value.${propertyManager.searchTestFolderFolderTypePropertyName}" />
  <c:if test="${empty folderType}">
    <c:set var="folderType" value="${propertyManager.normalFolderTypeEnumValue}" />
    <dspel:input type="hidden" bean="${formHandlerPath}.value.${propertyManager.searchTestFolderFolderTypePropertyName}" value="${folderType}" />
  </c:if>

  <fieldset class="atg_flushPanel">
    <legend><span><fmt:message key="searchTestFolder.basic.legend"/></span></legend>
    <dl class="atg_smerch_configList">
      <dt><span class="required" title="Required">*</span>&nbsp;<fmt:message key="searchTestFolder.name.text"/></dt>
      <dd><dspel:input iclass="formTextField" type="text" size="50" bean="${formHandlerPath}.value.${propertyManager.searchTestFolderDisplayNamePropertyName}" disabled="${propertyManager.normalFolderTypeEnumValue ne folderType}" oninput="return markAssetModified()" onpropertychange="formFieldModified()" /></dd>
      <c:if test="${propertyManager.normalFolderTypeEnumValue eq folderType}">
        <dt><span class="required" title="Required">*</span>&nbsp;<fmt:message key="searchTestFolder.parentFolder.text"/></dt>
        <dd>
          <dspel:input type="hidden" id="parentFolder_id" bean="${formHandlerPath}.value.${propertyManager.searchTestFolderParentFolderPropertyName}.repositoryId" />
          <dspel:getvalueof var="displayNameValue" bean="${formHandlerPath}.value.${propertyManager.searchTestFolderParentFolderPropertyName}.displayName"/>
          <c:if test="${empty displayNameValue}">
             <dspel:getvalueof var="displayNameValue" bean="${formHandlerPath}.value.${propertyManager.searchTestFolderParentFolderPropertyName}.repositoryId"/>
          </c:if>
          <span id="parentFolder_displayName" class="atg_testFolder_name"><c:out value="${displayNameValue}" /></span>
          <input type="button" value="<fmt:message key="searchTestFolder.select.text"/>" onclick="selectParentFolder(); return false;" />
          <script type="text/javascript">
            function showAssetPickerForParentFolder(
                requestUrl,
                repositoryPath,
                repositoryName,
                itemType,
                treePath,
                pickerURL,
                allowMulti) {
      
              var allowableTypes = new Array(0);
              var assetType = new Object();
              assetType.typeCode       = "repository";
              assetType.displayName    = "NotUsed";
              assetType.repositoryName = repositoryName;
              assetType.createable     = "false";
              assetType.typeName       = itemType;
              assetType.componentPath  = repositoryPath;
              allowableTypes[allowableTypes.length] = assetType;
      
              var picker = new AssetBrowser();
              picker.mapMode                 = "AssetManager.assetPicker";
              picker.clearAttributes         = "true";
              picker.pickerURL               = pickerURL + "&";
              picker.assetPickerTitle        = '<fmt:message key="searchTestingSelectTrackItems.selectItem.pickerTitle"/>';
              picker.browserMode             = "pick";
              picker.isAllowMultiSelect      = allowMulti;
              picker.createMode              = "none";
              picker.onSelect                = "doOnSelect";
              picker.callerData              = requestUrl;
              picker.closeAction             = "hide";
              picker.assetTypes              = allowableTypes;
              picker.assetPickerParentWindow = window;
              picker.assetPickerFrameElement = parent.document.getElementById("iFrame");
              picker.assetPickerDivId        = "browser";
      
              if (treePath) {
                picker.defaultView = "Browse";
                var viewConfiguration = new Array();
                viewConfiguration.treeComponent = treePath;
                viewConfiguration.itemTypes = itemType;
                picker.viewConfiguration = viewConfiguration;
              }
              else {
                picker.defaultView = "Search";
                picker.mapMode = "AssetManager.assetPicker.search";
              }
      
              picker.assetPickerParentWindow.parent.checkedItems = new Array();
      
              picker.assetPickerParentWindow.parent.currentPicker = picker;
              picker.encodeURLOptions();
              picker.assetPickerFrameElement.src = picker.encodeURLOptions();
              window.showIframe(picker.assetPickerDivId, picker.onSelect);
            }
      
            function doOnSelect(elems) {
              if (elems)
                parentFolder_updateValue(elems.id, elems.displayName);
              markAssetModified();
            }
      
            function parentFolder_updateValue(id, name) {
              var textField = document.getElementById('parentFolder_dispalyName');
              if (textField)
                textField.innerHTML = name;
              var inputField = document.getElementById('parentFolder_id');
              if (inputField)
                inputField.value = id;
            }
            
            function selectParentFolder() {
              <web-ui:getTree var="treePath"
                              repository="${formHandler.repository.absoluteName}"
                              itemType="${propertyManager.searchTestFolderItemName}"
                              treeRegistry="${configInfo.sessionInfo.treeRegistryPath}"/>
              showAssetPickerForParentFolder(
                null,
                '<c:out value="${formHandler.repository.absoluteName}"/>',
                '<c:out value="${formHandler.repository.repositoryName}"/>',
                '<c:out value="${propertyManager.searchTestFolderItemName}"/>',
                '<c:out value="${treePath}"/>',
                '<c:out value="${configInfo.assetPickerRoot}${configInfo.assetPicker}"/>?apView=1&',
                'false');
            }

            <c:if test="${!empty formHandler.parentRepositoryItem}">
              var input = document.getElementById("parentFolder_id");
              if (input.value == null || input.value.length == 0)
                parentFolder_updateValue(
                  '<c:out value="${formHandler.parentRepositoryItem.repositoryId}" />',
                  '<c:out value="${formHandler.parentRepositoryItem.itemDisplayName}" />'
                );
            </c:if>
          </script>
        </dd>
      </c:if>
      <dt><fmt:message key="searchTestFolder.description.text"/></dt>
      <dd><dspel:input iclass="formTextField" type="text" size="50" bean="${formHandlerPath}.value.${propertyManager.searchTestFolderDescriptionPropertyName}" disabled="${propertyManager.normalFolderTypeEnumValue ne folderType}" oninput="return markAssetModified()" onpropertychange="formFieldModified()" /></dd>
    </dl>
  </fieldset>

  <dspel:getvalueof var="appName" bean="${formHandlerPath}.value.${propertyManager.searchTestFolderApplicationPropertyName}" />
  <c:if test="${empty appName}">
    <%-- Get the value we will set for this property --%>
    <c:set var="appName" value="${configInfo.sessionInfo.currentApplicationName}" />
    <dspel:input type="hidden" bean="${formHandlerPath}.value.${propertyManager.searchTestFolderApplicationPropertyName}" value="${appName}" />
  </c:if>

  <dspel:importbean var="profile" bean="/atg/userprofiling/Profile"/>
  <dspel:setvalue bean="${formHandlerPath}.value.ownerId" value="${profile.repositoryId}"/>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/searchTestFolderEditor.jsp#2 $$Change: 651448 $--%>
