<%--
  Default editor for boolean elements of collection properties.

  The following request-scoped variables are expected to be set:

  @param  mpv  A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/booleanComponentEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramInitialize"    param="initialize"/>
  <dspel:getvalueof var="paramIndex"         param="index"/>
  <dspel:getvalueof var="paramValueId"       param="valueId"/>
  <dspel:getvalueof var="paramValueIdPrefix" param="valueIdPrefix"/>
  <dspel:getvalueof var="paramInfoObj"       param="infoObj"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>
  <c:set var="collectionType" value="${propertyView.propertyDescriptor.typeName}"/>
  <c:set var="isMap" value="${collectionType == 'map'}"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="id"                     value="${requestScope.uniqueAssetID}${propertyView.uniqueId}"/>
  <c:set var="transferUserValues"     value="${id}_transferUserValues"/>
  <c:set var="transferOriginalValues" value="${id}_transferOriginalValues"/>
  <c:set var="radioIdPrefix"          value="${id}_radio_"/>
  <c:set var="radioId"                value="${radioIdPrefix}${paramIndex}"/>

  <c:choose>
    <c:when test="${paramInitialize}">
      <script type="text/javascript">

        // Called after the page has loaded.
        function <c:out value="${transferOriginalValues}"/>() {
          var totalCount = <c:out value="${paramInfoObj}"/>.getRowCount();
          for (var i = 0; i < totalCount; i++) {
            // For each element in the collection, check the radio box that corresponds to the current element value.
            var valueInput = document.getElementById("<c:out value='${paramValueIdPrefix}'/>" + i);
            var value = valueInput.value;
            var radioInput = null;
            if (value == "true" || value == "1")
              radioInput = document.getElementById("<c:out value='${radioIdPrefix}'/>" + i + "_true");
            else
              radioInput = document.getElementById("<c:out value='${radioIdPrefix}'/>" + i + "_false");
            if (radioInput != null)
              radioInput.checked = true;
          }
        }

        // Called prior to submitting the form.
        function <c:out value="${transferUserValues}"/>() {
          var totalCount = <c:out value="${paramInfoObj}"/>.getRowCount();
          for (var i = 0; i < totalCount; i++) {
            // For each element in the collection, set the current element value according to which radio button is checked.
            var index = <c:out value="${paramInfoObj}"/>.getRowIndex(i);
            var radioInput = document.getElementById("<c:out value='${radioIdPrefix}'/>" + index + "_true");
            var valueInput = document.getElementById("<c:out value='${paramValueIdPrefix}'/>" + index);

            <c:choose>
            <c:when test="${isMap}">
              valueInput.value = (radioInput.checked ? "1" : "0");
            </c:when>
            <c:otherwise>
              valueInput.value = (radioInput.checked ? "true" : "false");
            </c:otherwise>
            </c:choose>
          }
        }

        // Assign the form-submission callback in the CollectionEditor object.
        <c:out value="${paramInfoObj}"/>.onTransferUserValues = <c:out value="${transferUserValues}"/>;

        // Register our initialization function.
        registerOnLoad(function() {
          <c:out value="${transferOriginalValues}"/>();
        });

      </script>
    </c:when>
    <c:otherwise>

      <td class="formValueCell">

        <%-- Store the property value in a hidden input whose value
             will be manipulated by CollectionEditor functions --%>
        <input type="hidden" id="<c:out value='${paramValueId}'/>"
               class="formTextField" value=""/>

        <%-- Display the property value in a radio group --%>
        <input type="radio" id="<c:out value='${radioId}'/>_true"
               name="<c:out value='${radioId}'/>" value="true"
               onchange="markAssetModified()"
               onpropertychange="formFieldModified()"/>
        <label for="<c:out value='${radioId}'/>_true">
          <fmt:message key="propertyEditor.yes"/>
        </label>
        <input type="radio" id="<c:out value='${radioId}'/>_false"
               name="<c:out value='${radioId}'/>" value="false"
               onchange="markAssetModified()"
               onpropertychange="formFieldModified()"/>
        <label for="<c:out value='${radioId}'/>_false">
          <fmt:message key="propertyEditor.no"/>
        </label>

      </td>
    </c:otherwise>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/booleanComponentEditor.jsp#2 $$Change: 651448 $--%>
