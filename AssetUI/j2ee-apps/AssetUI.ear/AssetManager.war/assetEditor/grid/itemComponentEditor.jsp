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

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/grid/itemComponentEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ page pageEncoding="UTF-8" %>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="json"     uri="http://www.atg.com/taglibs/json"                  %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<dspel:page>

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

        // Optionally display an ID column.
        <c:if test="${paramShowIdColumn == 'true'}">
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
              field: 2,
              formatter: <c:out value="${paramEditorObj}"/>.formatItemId,
              width: idColumnWidth };
        </c:if>

        // Cell formatter for drilldown links to individual items.
        <c:out value="${paramEditorObj}"/>.formatLink =
          function(pData, pRowIndex) {
            // "this" refers to the Dojox grid cell object.
            var m = this.grid.model;
            var uri = m.getDatum(pRowIndex, this.extraField);
            var html = '<a class="EllipsisClassHere" title="pData" href="#" onclick="editorId.drilldown(\'uri\', pRowIndex);">pData</a>';
            html = html.replace('editorId', '<c:out value="${paramEditorObj}"/>');
            html = html.replace('uri', uri);
            html = html.replace(/pData/g, pData);
            html = html.replace('pRowIndex', pRowIndex);
            return html;
          };

        // Add a column for item links.
        <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
          { name: '<fmt:message key="collectionEditor.item"/>',
            field: 0,
            extraField: 1,
            <c:if test="${!requestScope.atgIsMultiEditView}">
              formatter: <c:out value="${paramEditorObj}"/>.formatLink,
            </c:if>
            width: 'auto'};

        /////////////////////////////////////////////////////////////////////
        //        ACTION METHODS (RESPOND TO USER INPUT)
        /////////////////////////////////////////////////////////////////////
        <c:choose>
          <c:when test="${config.addAssetsToBottom == 'true'}" >
            // Method invoked when user clicks the Add Existing button.
            <c:out value='${paramEditorObj}'/>.addExisting = function() {
              <c:out value="${paramEditorObj}"/>.insertIndex = -1;
              <c:out value="${paramEditorObj}"/>.pickAsset(true);
            };
            // Method invoked when user clicks the Add New button.
            <c:out value='${paramEditorObj}'/>.addNew = function() {
              <c:out value='${paramEditorObj}'/>.createNewItem(false);
            };
          </c:when>
          <c:otherwise>
            // Method invoked when user clicks the Add Existing button.
	    <c:out value='${paramEditorObj}'/>.addExisting = function() {
	      <c:out value="${paramEditorObj}"/>.insertIndex = 0;
	      <c:out value="${paramEditorObj}"/>.pickAsset(true);
	    };
	    // Method invoked when user clicks the Add New button.
	    <c:out value='${paramEditorObj}'/>.addNew = function() {
	      <c:out value="${paramEditorObj}"/>.insertIndex = 0;
	      <c:out value='${paramEditorObj}'/>.createNewItem(true);
            };
          </c:otherwise>
        </c:choose>

      </script>

    </c:when>

    <%-- Operation: remove --%>
    <c:when test="${not empty paramRemoveIndex}">
      <asset-ui:collectionAdapterOperation
        var="result"
        collectionAdapter="${collectionAdapter}"
        removeIndex="${paramRemoveIndex}"/>

      <json:object>
        <json:property name="removedItemIndex">
          <c:out value="${result}"/>
        </json:property>
      </json:object>
    </c:when>

    <%-- Operation: add --%>
    <c:when test="${not empty paramAddIds}">
      <asset-ui:collectionAdapterOperation
        var="result"
        collectionAdapter="${collectionAdapter}"
        addIds="${paramAddIds}"/>

      <json:object>
        <json:property name="numItemsAdded">
          <c:out value="${result}"/>
        </json:property>
           
	<json:property name="numDuplicates">
	  <dspel:valueof bean="${paramCollectionAdapter}.numDuplicates"/>
        </json:property>
      </json:object>
    </c:when>

    <%-- Operation: insert --%>
    <c:when test="${not empty paramInsertIds && not empty paramInsertIndex}">
      <asset-ui:collectionAdapterOperation
        var="result"
        collectionAdapter="${collectionAdapter}"
        insertIds="${paramInsertIds}"
        insertIndex="${paramInsertIndex}"/>

      <json:object>
        <json:property name="numItemsInserted">
          <c:out value="${result}"/>
        </json:property>

        <json:property name="numDuplicates">
          <dspel:valueof bean="${paramCollectionAdapter}.numDuplicates"/>
        </json:property>
      </json:object>
    </c:when>

    <%-- Operation: reorder --%>
    <c:when test="${not empty paramReorderIndices}">
      <asset-ui:collectionAdapterOperation
        var="result"
        collectionAdapter="${collectionAdapter}"
        reorderIndices="${paramReorderIndices}"/>

      <json:object>
        <json:property name="numItemsReordered">
          <c:out value="${result}"/>
        </json:property>
      </json:object>
    </c:when>

    <%-- Render the items between startIndex and endIndex as JSON. Return an array
         of rows, each of which contains an object representing an individual
         item in the collection. --%>
    <c:when test="${not empty paramStartIndex && not empty paramEndIndex}">

      <dspel:getvalueof var="items" bean="${paramCollectionBeanProp}"/>

      <json:array prettyPrint="true" escapeXml="false">

        <c:forEach items="${items}" var="repItem" varStatus="itemLoop"
                   begin="${paramStartIndex}" end="${paramEndIndex}">

          <%-- If supplied, use the elementBeanProp for accessing individual
               items. This is used to unwrap RepositoryFormLists. --%>
          <c:if test="${not empty paramElementBeanProp}">
            <dspel:getvalueof var="repItem"
                              bean="${paramElementBeanProp}[${itemLoop.index}]"/>
          </c:if>

          <c:choose>
            <c:when test="${paramContainsURI == 'true'}">
              <c:set var="itemURI" value="${repItem}"/>
              <asset-ui:resolveAsset var="repItemWrapper" uri="${itemURI}"/>
              <c:set var="repItem" value="${repItemWrapper.asset}"/>
            </c:when>

            <c:otherwise>
              <asset-ui:createAssetURI var="itemURI"
                componentName="${repItem.repository.repositoryName}"
                itemDescriptorName="${repItem.itemDescriptor.itemDescriptorName}"
                itemId="${repItem.repositoryId}"/>
            </c:otherwise>
          </c:choose>

          <json:array>

            <%-- Cell 0: item display name --%>
            <json:property name="displayName">
              <c:out value="${repItem.itemDisplayName}"/>
            </json:property>

            <%-- Cell 1: asset URI --%>
            <json:property name="uri">
              <c:out value="${itemURI}"/>
            </json:property>

            <%-- Cell 2: item repository ID --%>
            <c:if test="${paramShowIdColumn == 'true'}">
              <json:property name="repositoryId">
                <c:out value="${repItem.repositoryId}"/>
              </json:property>
            </c:if>

          </json:array>

        </c:forEach>

      </json:array>

    </c:when>

  </c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/grid/itemComponentEditor.jsp#2 $$Change: 651448 $--%>
