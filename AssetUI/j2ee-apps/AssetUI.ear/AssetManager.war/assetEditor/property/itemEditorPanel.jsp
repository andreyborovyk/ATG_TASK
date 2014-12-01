<%--
  Default property editor for RepositoryItem values.  This can be invoked directly
  from any page by passing in parameters or it may be invoked via viewmappings from
  itemEditor.jsp

  The following request-scoped variables are expected to be set:

  @param  formHandler         The form handler for the form that displays this view
  @param  cancelSubTypeURL    ???
  @param  atgIsMultiEditView  True if we are in multiedit.

  The following paramters may be passed in:

  @param cutsomOnPickFunc       Optional. A custom javascript function to invoke when asset is picked.
  @param customClearValueFunc   Optional. A custom javascript function to invoke when asset is cleared.
  @param assetPickerHeader      Optional. The string to show at the top of the asset picker pop up.
  @param propItemDescriptorName Required. The name of the items descriptor of the property to be set.
  @param repositoryPath         Required. The path to the repository that contains the item to be picked.
  @param repositoryName         Required. The name of the repository.
  @param propertyName           Required. The name of the property to be set.
  @param displayNameProperty    Required. The property that is the display name of the item descriptor to be picked.
  @param displayNameValue       Required. The value of the displayName of the currently selected item.
  @param nullable               Required. True if the property value may be cleared (set to null).  False otherwise.
  @param allowCreate            Required. True if a new item may be created from here. False otherwise.
  @param allowDrilldown         Required. True if drill-down should be allowed.
  @param allowSelect            Optional. True if select should be allowed. (Defaults to true)
  @param formFieldId            Required. The id of the form field.
  @param formFieldBean          Required. The bean for the form field that communicates with the server.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/itemEditorPanel.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>
  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <%-- Unpack all DSP parameters --%>
  <dspel:getvalueof var="customOnPickFunc"       param="onPickFunc"/>
  <dspel:getvalueof var="customClearValueFunc"   param="clearValueFunc"/>
  <dspel:getvalueof var="assetPickerHeader"      param="assetPickerHeader"/>
  <dspel:getvalueof var="propItemDescriptorName" param="propItemDescriptorName"/>
  <dspel:getvalueof var="repositoryPath"         param="repositoryPath"/>
  <dspel:getvalueof var="repositoryName"         param="repositoryName"/>
  <dspel:getvalueof var="propertyName"           param="propertyName"/>
  <dspel:getvalueof var="displayNameProperty"    param="displayNameProperty"/>
  <dspel:getvalueof var="displayNameValue"       param="displayNameValue"/>
  <dspel:getvalueof var="nullable"               param="nullable"/>
  <dspel:getvalueof var="allowCreate"            param="allowCreate"/>
  <dspel:getvalueof var="allowDrilldown"         param="allowDrilldown"/>
  <dspel:getvalueof var="formFieldId"            param="formFieldId"/>
  <dspel:getvalueof var="formFieldBean"          param="formFieldBean"/>

  <%-- Get the value of allowSelect, or default to true --%>
  <dspel:getvalueof var="allowSelect" param="allowSelect"/>
  <c:if test="${empty allowSelect}">
    <c:set var="allowSelect" value="true"/>
  </c:if>

  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="inputId"         value="${formFieldId}_value"/>
  <c:set var="linkId"          value="${formFieldId}_link"/>
  <c:set var="tableId"         value="${formFieldId}_table"/>
  <%-- Javascript function should not start with a number.
       Hence, prefixing the function name.
       For details, see bug 135390 --%>
  <c:set var="clearValueFunc"  value="clearValue_${formFieldId}_func"/>
  <c:set var="createAssetFunc" value="createAsset_${formFieldId}_func"/>
  <c:set var="drillDownFunc"   value="drillDown_${formFieldId}_func"/>
  <c:set var="onPickFunc"      value="onPick_${formFieldId}_func"/>
  <c:set var="pickAssetFunc"   value="pickAsset_${formFieldId}_func"/>
  <c:set var="updateValueFunc" value="updateValue_${formFieldId}_func"/>

  <%-- Derive the URL for the Create button --%>
  <c:url var="createURL" value="/assetEditor/subTypeSelection.jsp">
    <c:param name="repositoryPathName" value="${repositoryPath}"/>
    <c:param name="itemDescriptorName" value="${propItemDescriptorName}"/>
    <c:param name="linkPropertyName"   value="${propertyName}"/>
    <c:param name="cancelSubTypeURL"   value="${requestScope.cancelSubTypeURL}"/>
  </c:url>

  <%-- Derive an asset URI for navigating the asset editor to an asset of the
       type that is referenced by the current property, but without a specific
       ID.  This will be used in conjunction with the currently specified ID
       when the user clicks the drill-down link. --%>

  <asset-ui:createAssetURI var="emptyDrillDownURI"
    componentName="${repositoryName}"
    itemDescriptorName="${propItemDescriptorName}"
    itemId=""/>

    <%-- Asset Picker --%>
    <c:choose>
      <c:when test="${empty customOnPickFunc}">
        <c:set var="onSelectFunction" value="${onPickFunc}"/>
      </c:when>
      <c:otherwise>
        <c:set var="onSelectFunction" value="${customOnPickFunc}"/>
      </c:otherwise>
    </c:choose>
    <dspel:include page="/components/assetPickerLauncher.jsp">
      <dspel:param name="assetPickerHeader"    value="${assetPickerHeader}"/>
      <dspel:param name="assetType"            value="${propItemDescriptorName}"/>
      <dspel:param name="isPickingParent"      value="false"/>
      <dspel:param name="launchPickerFunction" value="${pickAssetFunc}"/>
      <dspel:param name="onSelectFunction"     value="${onSelectFunction}"/>
      <dspel:param name="repositoryName"       value="${repositoryName}"/>
      <dspel:param name="repositoryPath"       value="${repositoryPath}"/>
    </dspel:include>

  <script type="text/javascript">

    // Called when the Create button is clicked.
    function <c:out value="${createAssetFunc}"/>() {

      // Force a save of the current asset, then go to the asset creation URL.
     parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset("<c:out value='${createURL}' escapeXml='false'/>", null, null, null, null, true);
    }

    // Called when the drill-down link is clicked.
    function <c:out value="${drillDownFunc}"/>() {
      // Derive the drill-down URL based on the currently specified item ID.
      var input = document.getElementById("<c:out value='${inputId}'/>");
      var propPage = "<c:out value='${config.contextRoot}'/>/assetEditor/editAsset.jsp" +
                     "?assetURI=<c:out value='${emptyDrillDownURI}'/>" + escape(input.value) +
                     "&pushContext=true" +
                     "&linkPropertyName=<c:out value='${propertyName}'/>";
      // Force a save/create of the current asset, then go to new page.
      parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(propPage, null, null, null, null, true);
    }

    // Called when the user makes a selection from the asset picker.
    //
    // @param  selected  An object containing info about the selected asset
    //
    function <c:out value="${onPickFunc}"/>(selected) {
      <c:out value="${updateValueFunc}"/>(selected.id, selected.displayName);
      markAssetModified();
    }

    // Called when the user clicks the Clear button.
    function <c:out value="${clearValueFunc}"/>() {
      <c:out value="${updateValueFunc}"/>(null, null);
      markAssetModified();
    }

    // Update the currently specified property value.
    //
    // @param  id           The current property value (repository ID)
    // @param  displayName  The display name of the current repository item
    //
    function <c:out value="${updateValueFunc}"/>(id, displayName) {
      // Update the hidden DSP input to indicate the currently specified
      // repository ID.  If ID is null, we explicitly set the input value to
      // the string "null", which tells the form handler that it should change
      // the property value to null.
      var input = document.getElementById("<c:out value='${inputId}'/>");
      input.value = (id != null ? id : "null");

      // Update the drill-down label to indicate the display name of the
      // current repository item.
      var link = document.getElementById("<c:out value='${linkId}'/>");
      link.innerHTML = displayName;

      // If the value is null, hide the box that contains the Clear button.
      var table = document.getElementById("<c:out value='${tableId}'/>");
      table.style.display = (id != null ? "block" : "none");
    }

    <%-- Ensure that the hidden DSP input contains the string "null" if there
         is no initial value. --%>
    registerOnLoad(function() {
      var input = document.getElementById("<c:out value='${inputId}'/>");
      if (input.value == null || input.value.length == 0)
        <c:out value="${updateValueFunc}"/>(null, null);
    });
  </script>

  <%-- Create a hidden input field that uses the DSP interface to communicate
       with the form handler.  This will be filled in using JavaScript, in
       response to user input. --%>
  <dspel:input type="hidden"
               id="${inputId}"
               bean="${formFieldBean}"
               nullable="${nullable}"/>

  <%-- Begin visible content --%>
  <div class="singleSelectItem">
    <table class="itemEditorTable">
      <tr>
        <td>
          <div id="<c:out value='${tableId}'/>" class="itemEditorTableValueBox">
            <table>
              <tr>
                <td nowrap class="formValueCell">

                  <%-- Display a hyperlink for drilling down into the current asset,
                       unless we are in Multi-edit mode or drilling down is otherwise
                       prohibited, in which case we just display the property value. --%>
                  <c:choose>
                    <c:when test="${requestScope.atgIsMultiEditView or
                                    not allowDrilldown}">

                      <span id="<c:out value='${linkId}'/>">
                        <c:out value="${displayNameValue}"/>
                      </span>
                    </c:when>
                    <c:otherwise>
                      <a id="<c:out value='${linkId}'/>"
                        href="javascript:<c:out value='${drillDownFunc}'/>()"/>
                        <c:out value="${displayNameValue}"/>
                      </a>
                    </c:otherwise>
                  </c:choose>
                </td>

                <c:if test="${nullable}">
                  <td class="iconCell">

                    <c:set var="theClearValueFunc" value="${clearValueFunc}"/>
                    <c:if test="${not empty customClearValueFunc}">
                        <c:set var="theClearValueFunc" value="${customClearValueFunc}"/>
                    </c:if>

                    <%-- Display an icon for setting the property value to null --%>
                    <a href="javascript:<c:out value='${theClearValueFunc}'/>()"
                       class="icon propertyClear"
                       title="<fmt:message key='propertyEditor.clear'/>"/>
                  </td>
                </c:if>
              </tr>
            </table>
          </div>
        </td>

        <%-- Select button pops up the picker. --%>
        <c:if test="${allowSelect}">
          <td>
            <a href="javascript:<c:out value='${pickAssetFunc}'/>()" class="buttonSmall">
              <fmt:message key="propertyEditor.select"/>
            </a>
          </td>
        </c:if>

        <%-- Create button opens form for new transient asset.  This button is not
             displayed in Multi-edit mode, or if the button is configured to be hidden,
             or if the property is a reference from a non-versioned item to a versioned item.
             Also, if the asset editor item is transient, it is shown only if this
             property is optional.  We currently don't support the creation of required
             reference properties during the creation of an item, since we must always
             save the current item when drilling down. --%>
        <c:if test="${not requestScope.atgIsMultiEditView and
                      allowCreate}">
          <c:if test="${nullable or empty formHandler.transientItem}">
            <td>
              <a href="javascript:<c:out value='${createAssetFunc}'/>()" class="buttonSmall">
                <fmt:message key="propertyEditor.create"/>
              </a>
            </td>
          </c:if>
        </c:if>
      </tr>
    </table>
  </div>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/itemEditorPanel.jsp#2 $$Change: 651448 $--%>
