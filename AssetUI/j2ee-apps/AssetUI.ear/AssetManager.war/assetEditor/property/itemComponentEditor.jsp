<%--
  Default editor for RepositoryItem elements of collection properties.

  The following request-scoped variables are expected to be set:

  @param  mpv  A MappedPropertyView item for this view

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/itemComponentEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"     uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel" uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"   uri="http://java.sun.com/jstl/fmt"                     %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramInitialize"     param="initialize"/>
  <dspel:getvalueof var="paramIndex"          param="index"/>
  <dspel:getvalueof var="paramInfoObj"        param="infoObj"/>
  <dspel:getvalueof var="paramValueId"        param="valueId"/>
  <dspel:getvalueof var="paramDisplayName"    param="displayName"/>
  <dspel:getvalueof var="paramAssetURI"       param="assetURI"/>
  <dspel:getvalueof var="paramCollectionType" param="collectionType"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="id"           value="${requestScope.atgPropertyViewId}"/>
  <c:set var="linkIdPrefix" value="${id}_link_"/>
  <c:set var="linkId"       value="${linkIdPrefix}${paramIndex}"/>
  <c:set var="onPickFunc"   value="${id}_onPick"/>

  <c:choose>
    <c:when test="${paramInitialize}">

      <%-- Indicate to the parent editor, via a request variable, the name of the
           function to be called when the "Add New" button is clicked --%>
      <c:set scope="request" var="addNewFunc" value="${paramInfoObj}.createAsset"/>

      <script type="text/javascript">

        // Indicate that the collection editor should use the asset picker, and
        // specify a selection callback.
        <c:out value="${paramInfoObj}"/>.useAssetPicker = true;
        <c:out value="${paramInfoObj}"/>.onPick = <c:out value='${onPickFunc}'/>;

        // Called when the user makes a selection from the asset picker.
        //
        // @param  pSelected  An object containing info about the selected asset
        // @param  pIndex     The index of the collection element to which the
        //                    asset picker applies
        //
        function <c:out value="${onPickFunc}"/>(pSelected, pIndex) {

          // Change the display string and destination of the drill-down link
          // field (if it is a link) to reflect the selected asset.
          var link = document.getElementById("<c:out value='${linkIdPrefix}'/>" + pIndex);
          if (link) {
            link.innerHTML = pSelected.displayName;
            if (typeof link.href != "undefined") {
              link.href = "#";
              link.onclick = function(){<c:out value='${paramInfoObj}'/>.drillDown(pSelected.uri);};
            }
          }
        }

      </script>

    </c:when>

    <c:otherwise>

      <c:choose>
        <c:when test="${not propertyView.propertyDescriptor.cascadeDelete and propertyView.attributes.hideAddExisting != true and paramCollectionType != 'set'}">
          <td class="formValueCell">
        </c:when>
        <c:otherwise>
          <td class="formValueCell" colspan="2">
        </c:otherwise>
      </c:choose>
        <%-- Store the property value's repositoryId in a hidden input whose value
             will be manipulated by CollectionEditor functions --%>
        <input type="hidden" id="<c:out value='${paramValueId}'/>"
               class="formTextField" value=""/>

        <%-- Display the asset name, either as a label or a link --%>
        <c:choose>
          <c:when test="${requestScope.atgIsMultiEditView or
                          propertyView.attributes.prohibitDrillDown}">
            <span id="<c:out value='${linkId}'/>">
              <c:out value="${paramDisplayName}"/>
            </span>
          </c:when>
          <c:otherwise>
            <a id="<c:out value='${linkId}'/>"
               href="#"
               onclick="<c:out value='${paramInfoObj}.drillDown("${paramAssetURI}")'/>">
              <c:out value="${paramDisplayName}"/>
            </a>
          </c:otherwise>
        </c:choose>
      </td>
      
      <%-- Display an icon for modifying the reference using the asset picker --%>
      <c:if test="${not propertyView.propertyDescriptor.cascadeDelete and propertyView.attributes.hideAddExisting != true and paramCollectionType != 'set'}">
        <td class="iconCell">
            <a href="javascript:<c:out value='${paramInfoObj}.modifyReference(${paramIndex})'/>"
               class="propertyEdit" title="<fmt:message key='propertyEditor.edit'/>"/>
        </td>
      </c:if>

    </c:otherwise>
  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/itemComponentEditor.jsp#2 $$Change: 651448 $--%>
