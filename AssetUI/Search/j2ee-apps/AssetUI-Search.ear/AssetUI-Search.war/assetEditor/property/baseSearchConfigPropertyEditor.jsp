<%--

  Custom editor for property searchConfig.baseSearchConfig which is a 
  repositoryItem of type baseSearchConfig.

  This editor is a basic single valued linked item editor with the 
  following change in flow for clearing the property value:

     1. User clicks the clear icon to remove the baseSearchConfig.

     2. We intercept the clear action and set the value of useBaseRankingProperties to false. 

     3. Normal behavior resumes - baseSearchConfig property

  And the following change in flow for setting the property value: 

     1. User clicks select to pop up asset picker (standard)

     2. User picks an asset and clicks OK to close the picker. (standard)

     3. (custom behavior starts here) We pop up a dialog : 

            Do you want to replace the Property Prioritization 
            settings in this Search Configuration with the ones 
            from this Base Search configuration?
            [Yes]   [No]

     4. If the user clicks Use Base, set useBaseRankingProperties = true.  If the user 
        clicks Use Existing, set that property to false.

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/baseSearchConfigPropertyEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
--%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="assetui-search" uri="http://www.atg.com/taglibs/assetui-search" %>

<dspel:page>

  <dspel:importbean var="assetManagerConfig" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle var="assetManagerBundle" basename="${assetManagerConfig.resourceBundle}"/>
  <dspel:importbean var="config" bean="/atg/search/web/assetmanager/Configuration"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  
  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="id" value="${requestScope.atgPropertyViewId}"/>
  <c:set var="myClearValueFunc"  value="${id}_clearValueBaseSearchConfig"/>
  <c:set var="defaultClearValueFunc"    value="clearValue_${id}_func"/>
  <c:set var="myOnPickFunc"      value="${id}_onPickBaseSearchConfig"/>
  <c:set var="defaultOnPickFunc" value="onPick_${id}_func"/>
  <c:set var="launchPickerFunction" value="pickAsset_${id}_func2"/>

  <script type="text/javascript">

    dojo.require("dijit.Dialog");

    // Pop up dialog about overriding property weightings
    function showOverridePropWeightsFromNewBaseDialog() {
      top.assetManagerDialog.titleNode.innerHTML = '<fmt:message key="baseSearchConfigPropertyEditor.overrideRankingProperties.title" />';
      <c:url var="assetManagerDialogHref" context="${config.contextRoot}" value="/assetEditor/property/overridePropWeightsDialog.jsp" />
      top.assetManagerDialog.setHref('<c:out value="${assetManagerDialogHref}" />');
      top.assetManagerDialog.execute = function(result) {
        var checkbox = document.getElementById("useBaseCheckbox");
        checkbox.checked = result;
        top.assetManagerDialog.hide();
      };
      top.assetManagerDialog.show();
    }

    // Override the method called when the user clears the value from this property
    //
    // @param  selected  An object containing info about the selected asset
    //
    function <c:out value="${myClearValueFunc}"/>() {
      checkUseBaseCheckbox(false);
      <c:out value="${defaultClearValueFunc}"/>();
    }

    // Override the method called when the user makes a selection from the asset picker.
    //
    // @param  selected  An object containing info about the selected asset
    //
    function <c:out value="${myOnPickFunc}"/>(selected) {
      showOverridePropWeightsFromNewBaseDialog();
      <c:out value="${defaultOnPickFunc}"/>(selected);
    }

    // Set the value of the useBaseCheckbox based on the 
    // boolean param passed in.  
    function checkUseBaseCheckbox(shouldCheck) {
      var checkbox = document.getElementById("useBaseCheckbox");
      checkbox.checked = shouldCheck;
    }

    function <c:out value="${launchPickerFunction}"/>() {
      var pickerLauncher = new atg.assetmanager.AssetPickerLauncher();
  
      // array to contain only those types to be pickable in browser
      // these pickable types will be the possible parent types for current item type
      var allowableTypes = new Array( 0 );
      var allowableTypesCommaList = '';

      var assetType = new Object();
      assetType.typeCode       = 'repository';
      assetType.typeCode       = 'repository';
      assetType.displayName    = 'baseSearchConfig';
      assetType.typeName       = 'baseSearchConfig';
      assetType.repositoryName = 'RefinementRepository';
      assetType.componentPath  = '/atg/search/repository/RefinementRepository';
      assetType.createable     = 'true';
      allowableTypes[ 0 ] = assetType;
      allowableTypesCommaList += 'baseSearchConfig';

      pickerLauncher.assetTypes             = allowableTypes;
      pickerLauncher.pickableTypesCommaList = allowableTypesCommaList;
      pickerLauncher.pickerURL              = '<c:out value="${assetManagerConfig.assetPickerRoot}${assetManagerConfig.assetPicker}"/>?apView=1&';

      <fmt:message var="assetPickerTitle" key="propertyEditor.assetPickerTitle" bundle="${assetManagerBundle}"/>
      <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>

      pickerLauncher.assetPickerTitle       = '<c:out value="${assetPickerTitle}"/>';
      pickerLauncher.onSelectFunction       = '<c:out value="${myOnPickFunc}"/>';
      pickerLauncher.callerData             = '';

      <fmt:message var="assetPickerHeader" key="${requestScope.mpv.attributes.assetPickerHeader}"/>
      
      <%-- retrive actual language for asset picker header --%>
      <c:choose>
        <c:when test="${not empty requestScope.atgCurrentParentAsset}">
          <assetui-search:getDimensionValueChoices var="dimensionValueChoicesResult" item="${requestScope.atgCurrentAsset.asset}" parentItem="${requestScope.atgCurrentParentAsset.asset}"/>
        </c:when>
        <c:otherwise>
          <assetui-search:getDimensionValueChoices var="dimensionValueChoicesResult" item="${requestScope.atgCurrentAsset.asset}"/>
        </c:otherwise>
      </c:choose>

      <c:choose>
        <c:when test="${'language' == dimensionValueChoicesResult.searchDimensionName}">
          var el = dojo.byId('propertyValue_dimensionValue');
          pickerLauncher.assetPickerHeader = '<c:out value="${assetPickerHeader}"/>' + ' ' + el.options[el.selectedIndex].text;
        </c:when>
        <c:otherwise>
          pickerLauncher.assetPickerHeader = '<c:out value="${assetPickerHeader}"/> <fmt:message key="dimensionValueEditor.allOthers"/>';
        </c:otherwise>
      </c:choose>
      
      pickerLauncher.allowMultiSelect       = '';

      pickerLauncher.invoke();
    }

    dojo.addOnLoad(function() {
      dojo.query('div#propEditorDiv_<c:out value="${id}"/> a.buttonSmall')[0].href = 'javascript:<c:out value="${launchPickerFunction}"/>()';
    });

  </script>

  <%-- retrive actual language for asset picker header --%>
  <c:choose>
    <c:when test="${not empty requestScope.atgCurrentParentAsset}">
      <assetui-search:getDimensionValueChoices var="dimensionValueChoicesResult" item="${requestScope.atgCurrentAsset.asset}" parentItem="${requestScope.atgCurrentParentAsset.asset}"/>
    </c:when>
    <c:otherwise>
      <assetui-search:getDimensionValueChoices var="dimensionValueChoicesResult" item="${requestScope.atgCurrentAsset.asset}"/>
    </c:otherwise>
  </c:choose>

  <%-- include the standard editor with the custom myOnPickFunc --%>
  <dspel:include otherContext="${assetManagerConfig.contextRoot}" page="/assetEditor/property/itemEditor.jsp">
    <dspel:param name="clearValueFunc"  value="${myClearValueFunc}"/>
  </dspel:include>

  <%-- hidden checkbox for useBaseRankingProperties --%>
  <div style="display:none">
    <dspel:input type="checkbox" id="useBaseCheckbox"
       bean="${requestScope.formHandlerPath}.value.useBaseRankingProperties" />
  </div>
</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/Search/src/web-apps/AssetUI-Search/assetEditor/property/baseSearchConfigPropertyEditor.jsp#2 $$Change: 651448 $--%>
