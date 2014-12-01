<%--
  Special property editor for properties of type string where the string
  indicates the path of a content group.

  The following request-scoped variables are expected to be set:

  @param  mpv          A MappedPropertyView item for this view
  @param  formHandler  The form handler for the form that displays this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/contentGroupPathEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="formHandler"  value="${requestScope.formHandler}"/>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="id" value="${requestScope.atgPropertyViewId}"/>
  <c:set var="inputId"         value="${id}_value"/>
  <c:set var="linkId"          value="${id}_link"/>
  <c:set var="tableId"         value="${id}_table"/>
  <c:set var="clearValueFunc"  value="${id}_clearValue"/>
  <c:set var="onPickFunc"      value="${id}_onPick"/>
  <c:set var="pickAssetFunc"   value="${id}_pickAsset"/>
  <c:set var="updateValueFunc" value="${id}_updateValue"/>

  <%-- Asset Picker --%>
  <dspel:include page="/components/assetPickerLauncher.jsp">
    <dspel:param name="assetType"            value="contentGroup"/>
    <dspel:param name="assetTypeCode"        value="file"/>
    <dspel:param name="isPickingParent"      value="false"/>
    <dspel:param name="launchPickerFunction" value="${pickAssetFunc}"/>
    <dspel:param name="onSelectFunction"     value="${onPickFunc}"/>
    <dspel:param name="repositoryName"       value="PublishingFiles"/>
    <dspel:param name="repositoryPath"       value="/atg/epub/file/SecuredPublishingFileRepository"/>
  </dspel:include>

  <script type="text/javascript">

    // Called when the user makes a selection from the asset picker.
    //
    // @param  selected  An object containing info about the selected asset
    //
    function <c:out value="${onPickFunc}"/>(selected) {
      // Note that we use the display name of the selected asset, which
      // indicates the name of the content group, rather than the ID of the
      // asset, which indicates the path of the file that defines the group.
      <c:out value="${updateValueFunc}"/>(selected.displayName);
      markAssetModified();
    }

    // Called when the user clicks the Clear button.
    function <c:out value="${clearValueFunc}"/>() {
      <c:out value="${updateValueFunc}"/>(null);
      markAssetModified();
    }

    // Update the currently specified property value.
    //
    // @param  value  The current value
    //
    function <c:out value="${updateValueFunc}"/>(value) {
      
      // Update the hidden DSP input to indicate the currently specified value.
      var input = document.getElementById("<c:out value='${inputId}'/>");
      input.value = value;

      // Update the value label to indicate the currently specified value.
      var link = document.getElementById("<c:out value='${linkId}'/>");
      link.innerHTML = value;
      
      // If the value is null, hide the box that contains the Clear button.
      var table = document.getElementById("<c:out value='${tableId}'/>");
      table.style.display = (value != null ? "block" : "none");
    }

    <%-- Ensure that the Clear button is hidden if there is no initial value. --%>
    registerOnLoad(function() {
      var input = document.getElementById("<c:out value='${inputId}'/>");
      if (input.value == null || input.value.length == 0)
        <c:out value="${updateValueFunc}"/>(null);
    });
  </script>

  <%-- Create a hidden input field that uses the DSP interface to communicate
       with the form handler.  This will be filled in using JavaScript, in
       response to user input. --%>
  <dspel:input type="hidden"
               id="${inputId}"
               bean="${propertyView.formHandlerProperty}"
               nullable="${not propertyView.propertyDescriptor.required}"/>

  <%-- Begin visible content --%>

  <table class="itemEditorTable">
    <tr>
      <td>
        <div id="<c:out value='${tableId}'/>" class="itemEditorTableValueBox">
          <table class="formStringList">
            <tr>
              <td nowrap class="formValueCell">

                <%-- Display the property value --%>
                <span id="<c:out value='${linkId}'/>">
                  <dspel:valueof bean="${propertyView.formHandlerProperty}"/>
                </span>
              </td>

              <c:if test="${not propertyView.propertyDescriptor.required}">
                <td class="iconCell">

                  <%-- Display an icon for setting the property value to null --%>
                  <a href="javascript:<c:out value='${clearValueFunc}'/>()"
                     class="icon propertyClear"
                     title="<fmt:message key='propertyEditor.clear'/>"/>
                </td>
              </c:if>
            </tr>
          </table>
        </div>
      </td>

      <%-- Display a button for popping up the asset picker --%>
      <td>
        <a href="javascript:<c:out value='${pickAssetFunc}'/>()" class="buttonSmall">
          <fmt:message key="propertyEditor.select"/>
        </a>
      </td>
    </tr>
  </table>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/contentGroupPathEditor.jsp#2 $$Change: 651448 $--%>
