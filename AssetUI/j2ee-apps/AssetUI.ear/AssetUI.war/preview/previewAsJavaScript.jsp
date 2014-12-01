<%--
  Contains javascript for previewing as a specified user.
  It includes jsp so it can't be a .js file.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/preview/previewAsJavaScript.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt" %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="web-ui"  uri="http://www.atg.com/taglibs/web-ui" %>

<dspel:page>

  <dspel:importbean var="config" bean="/atg/assetui/Configuration"/>
  <fmt:bundle basename="${config.resourceBundle}">
    <fmt:message var="previewAsPickerTitle" key="preview-as-picker-title"/>
    <web-ui:encodeParameterValue var="previewAsPickerTitle"
                                 value="${previewAsPickerTitle}"/>
  </fmt:bundle>

  <script type="text/javascript">

    // Create the atg.preview "namespace", if necessary.  (This should be
    // replaced with dojo.provide("atg.preview") when all applications that
    // use this functionality have Dojo available.

    if (typeof atg == "undefined")
      atg = {};
    if (typeof atg.preview == "undefined")
      atg.preview = {};

    //----------------------------------------------------------------------------//
    //
    // Invoke the asset picker to pick a user to "preview as".
    // Note: specifying the callbackFunc is optional. If unspecified,
    // we assume the callback is named atg.preview.previewAsUser.
    //
    atg.preview.pickUserForPreview = function(callbackFunc) {

      if (!callbackFunc)
        callbackFunc = "atg.preview.previewAsUser";

      var allowableTypes = new Array();
      var assetType = new Object();
      assetType.typeCode       = "repository";
      assetType.displayName    = "NotUsed";
      assetType.repositoryName = "UserProfiles";
      assetType.createable     = "false";
      assetType.typeName       = "user";
      assetType.componentPath  = "/atg/userprofiling/ProfileAdapterRepository";
      allowableTypes[allowableTypes.length] = assetType;

      var viewConfiguration = new Array();
      viewConfiguration.treeComponent = "";
      viewConfiguration.itemTypes     = "user";

      // Handle both AssetManager and non-AssetManager frameworks.
      var frameElement = document.getElementById("iFrame");
      if (!frameElement)
        frameElement = parent.document.getElementById("iFrame");

      var picker = new AssetBrowser();
      picker.mapMode                 = "AssetManager.assetPicker.search";
      picker.clearAttributes         = "true";
      picker.pickerURL               = "/AssetManager/assetPicker.jsp?apView=1&";
      picker.browserMode             = "pick";
      picker.createMode              = "none";
      picker.onSelect                = callbackFunc;
      picker.closeAction             = "hide";
      picker.assetTypes              = allowableTypes;
      picker.assetPickerParentWindow = window;
      picker.assetPickerFrameElement = frameElement;
      picker.assetPickerDivId        = "browser";
      picker.isAllowMultiSelect      = "false";
      picker.defaultView             = "Search";
      picker.assetPickerTitle        = '<c:out value="${previewAsPickerTitle}"/>';
      picker.dialogType              = "okCancel";

      //picker.invoke();

      // Instead of using picker.invoke(), the following bit of magic
      // allows us to run the AssetManager's asset picker plugins in
      // a non-AssetManager UI.
      picker.assetPickerParentWindow.parent.currentPicker = picker;
      picker.encodeURLOptions();
      picker.assetPickerFrameElement.src = picker.encodeURLOptions();
      window.showIframe(picker.assetPickerDivId, picker.onSelect);
    }

  </script>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetUI/preview/previewAsJavaScript.jsp#2 $$Change: 651448 $--%>
