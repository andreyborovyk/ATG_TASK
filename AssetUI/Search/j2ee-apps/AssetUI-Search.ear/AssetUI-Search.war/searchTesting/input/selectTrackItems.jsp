<%--
  Track items input controls and popup picker.

  The following request-scoped variables are expected to be set:
  
  @param  formHandlerPath Nucleus path to the SearchTestingFormHandler
  @param  formHandler     SearchTestingFormHandler instance

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/selectTrackItems.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:set var="formHandlerPath" value="${requestScope.formHandlerPath}"/>
  <c:set var="formHandler"     value="${requestScope.formHandler}"/>

  <script type="text/javascript">

      function showAssetPickerForTrackItems(
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
        if (null == elems) {
          return;
        }

        var elemCount = elems.length;
        var textField = document.getElementById('trackItemsInputControl');
        var str = textField.value;
        for (var elemNo = 0; elemNo < elemCount; elemNo++) {
          var elem = elems[elemNo];
          if (str != null && str.length > 0)
            str += ', ';
          str += elem.id;
        }

        textField.value = str;
      }

      function addTrackItems() {
        showAssetPickerForTrackItems(
            null,
            '/atg/commerce/catalog/MerchandisingProductCatalog',
            '<c:out value="${formHandler.indexingPropertyProvider.indexedRepository.repositoryName}"/>',
            '<c:out value="${formHandler.indexingPropertyProvider.indexedItemDescriptor.itemDescriptorName}"/>',
            '/atg/commerce/web/browse/CatalogTreeStateSiteSupport',
            '/AssetManager/assetPicker.jsp?apView=1',
            'true');
      }

  </script>

  <dspel:input id="trackItemsInputControl" type="text" iclass="formTextField" name="trackItems" size="50" bean="${formHandlerPath}.trackItems" onkeypress="return markAssetModified()" onchange="return markAssetModified()"/>
  <input type="button" value="<fmt:message key="searchTestingSelectTrackItems.select.button"/>" onclick="addTrackItems(); return false;" />
  
  <script type="text/javascript">
    function clearTrackItemsInput(){
      document.getElementById('trackItemsInputControl').value = '';
    }
  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/searchTesting/input/selectTrackItems.jsp#2 $$Change: 651448 $--%>
