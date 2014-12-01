<%--
  Thie file is used in conjunction with basicGrid.jsp to display and edit
  RepositoryItem collections.

  Parameters used during initialization:
  @param initialize    Indicates we are in initialization mode.
  @param editorObj     The name of a Javascript object that can be used to add
                       grid customizations, such as extra columns and behaviors.
  @param showIdColumn  Indicates whether an ID column should be displayed.
  @param idColumnWidth Width of the ID column in pixels.

  Parameters used to access and manipulate the collection:
  @param collectionBeanProp  The full bean path to the collection (required).
  @param elementBeanProp     The full bean path to individual elements (optional).
  @param collectionAdapter   The bean path to a CollectionAdapter that can modify
                             the collection (required if collection is editable).
  @param containsURI         Indicates whether collectionBeanProp contains URIs
                             instead of items.

  Parameters issued via AJAX requests from the grid:
  @param startIndex     The start index of the items to render.
  @param endIndex       The end index of the items to render.
  @param removeIndex    The index of an item to remove.
  @param addIds         A list of item IDs to add.
  @param insertIds      A list of item IDs to insert.
  @param insertIndex    The index to insert additional items at.
  @param reorderIndices An encoded string representing a set of reorder changes.

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/assetConfirmationComponent.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="json"     uri="http://www.atg.com/taglibs/json"                  %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

  <c:set var="importAssetPath" value="/atg/web/assetmanager/transfer/ImportAsset"/>
  <dspel:importbean var="importAsset"
                    bean="${importAssetPath}"/>

  <%-- Unpack DSP and other parameters --%>
  <dspel:getvalueof var="paramInitialize"           param="initialize"/>
  <dspel:getvalueof var="paramEditorObj"            param="editorObj"/>
  <dspel:getvalueof var="paramShowIdColumn"         param="showIdColumn"/>
  <dspel:getvalueof var="paramIdColumnWidth"        param="idColumnWidth"/>
  <dspel:getvalueof var="paramCollectionAdapter"    param="collectionAdapter"/>
  <dspel:getvalueof var="paramCollectionBeanProp"   param="collectionBeanProp"/>
  <dspel:getvalueof var="paramElementBeanProp"      param="elementBeanProp"/>
  <dspel:getvalueof var="paramContainsURI"          param="containsURI"/>
  <dspel:getvalueof var="paramStartIndex"           param="startIndex"/>
  <dspel:getvalueof var="paramEndIndex"             param="endIndex"/>
  <dspel:getvalueof var="paramRemoveIndex"          param="removeIndex"/>
  <dspel:getvalueof var="paramAddIds"               param="addIds"/>
  <dspel:getvalueof var="paramInsertIds"            param="insertIds"/>
  <dspel:getvalueof var="paramInsertIndex"          param="insertIndex"/>
  <dspel:getvalueof var="paramReorderIndices"       param="reorderIndices"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <c:if test="${not empty paramCollectionAdapter}">
    <dspel:getvalueof var="collectionAdapter" bean="${paramCollectionAdapter}"/>
  </c:if>

  <c:choose>

    <%-- Javascript initialization --%>
    <c:when test="${not empty paramInitialize}">

      <script type="text/javascript">

        /////////////////////////////////////////////////////////////////////
        //        COLUMN FORMATTERS AND SETUP
        /////////////////////////////////////////////////////////////////////

        // display an ID column.
          <c:set var="idColumnWidth" value="60"/>
          <c:if test="${not empty paramIdColumnWidth}">
            <c:set var="idColumnWidth" value="${paramIdColumnWidth}"/>
          </c:if>

          <c:out value="${paramEditorObj}"/>.formatItemId =
            function(pData, pRowIndex) {
              var html = '<span class="EllipsisClassHere" title="itemId">itemId</span>';
              html = html.replace(/itemId/g, pData);
              return html;
            };
          var idColumnWidth = '<c:out value="${idColumnWidth}"/>' + 'px';
          <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
            { name: '<fmt:message key="collectionEditor.itemId"/>',
              field: 0,
              formatter: <c:out value="${paramEditorObj}"/>.formatItemId,
              width: idColumnWidth };

        // Add a column for items
        <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
          { name: '<fmt:message key="collectionEditor.item"/>',
            field: 1,
            width: 'auto'};

        // Optionally display an error message column.
        <c:if test="${paramShowIdColumn == 'true'}">
          <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
            { name: '<fmt:message key="import.errormsg"/>',
              field: 2,
              width: 'auto'};
        </c:if>
      </script>

    </c:when>

    <%-- Render the items between startIndex and endIndex as JSON. Return an array
         of rows, each of which contains an object representing an individual
         item in the collection. --%>
    <c:when test="${not empty paramStartIndex && not empty paramEndIndex}">

      <dspel:getvalueof var="items" bean="${paramCollectionBeanProp}"/>

      <json:array prettyPrint="true" escapeXml="false">

        <c:forEach items="${items}" var="repItem" varStatus="itemLoop"
                   begin="${paramStartIndex}" end="${paramEndIndex}">


          <c:choose>
            <c:when test="${paramContainsURI == 'true'}">
              <c:set var="itemURI" value="${repItem}"/>
              <asset-ui:resolveAsset var="repItemWrapper" uri="${itemURI}"/>
              <c:set var="repItem" value="${repItemWrapper.asset}"/>
            </c:when>

            <c:otherwise>
              <c:set var="itemURI" value="${repItem}"/>
              <c:set var="repItem" value=""/>
            </c:otherwise>
          </c:choose>

          <json:array>

            <%-- Cell 1: item repository ID --%>
            <json:property name="repositoryId">
              <c:choose>
                <c:when test="${repItem != ''}">
                  <c:out value="${repItem.repositoryId}"/>
                </c:when>
                <c:otherwise>
                   <c:out value="${importAsset.newAssetErrorDisplayIds[itemURI]}"/>
                </c:otherwise>
              </c:choose>
            </json:property>

            <%-- Cell 0: item display name --%>
            <json:property name="displayName">
              <c:if test="${repItem != ''}">
                <b><c:out value="${repItem.itemDisplayName}"/></b>
              </c:if>
            </json:property>

            <%-- Cell 2: errorMessage --%>
            <c:if test="${paramShowIdColumn == 'true'}">
              <json:property name="errormsg">
                <c:set var="assetErrors" value="${importAsset.assetErrors[itemURI]}"/>
                <c:forEach items="${assetErrors}" var="errorMsg" varStatus="msgLoopStatus">
                  <c:if test="${!msgLoopStatus.first}"><br></c:if>
                  <c:out value="${errorMsg}"/>
                </c:forEach>
              </json:property>
            </c:if>

          </json:array>

        </c:forEach>

      </json:array>

    </c:when>

  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/assetConfirmationComponent.jsp#2 $$Change: 651448 $--%>
