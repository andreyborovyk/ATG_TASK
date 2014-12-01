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

  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/assetValidateComponent.jsp#2 $$Change: 651448 $
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

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>


  <c:set var="importAssetPath" value="/atg/web/assetmanager/transfer/ImportAsset"/>
  <dspel:importbean var="importAsset"
                    bean="${importAssetPath}"/>

  <c:set var="WARNING" value="${importAsset.warningCompare}"/>
  <c:set var="FAILURE" value="${importAsset.failureCompare}"/>
  <c:set var="SUCCESS" value="${importAsset.successCompare}"/>

  <c:choose>

    <%-- Javascript initialization --%>
    <c:when test="${not empty paramInitialize}">

      <script type="text/javascript">

        /////////////////////////////////////////////////////////////////////
        //        COLUMN FORMATTERS AND SETUP
        /////////////////////////////////////////////////////////////////////

        // Add first warning column
        <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
          { name: ' ',
            field: 0,
            width: '20px'};

        // Add first status column
        <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
          { name: 'status',
            field: 1,
            width: '200px'};


         <c:forEach items="${importAsset.propertyDisplayNames}" var="prop" varStatus="itemPropLoop">
          // Add a column for property
          <c:out value="${paramEditorObj}"/>.componentColumns[<c:out value="${paramEditorObj}"/>.componentColumns.length] =
            { name: '<c:out value='${prop}'/>',
              field: <c:out value='${itemPropLoop.count + 1}'/>,
              width: '200px'};
         </c:forEach>


      </script>

   </c:when>


    <%-- Render the items between startIndex and endIndex as JSON. Return an array
         of rows, each of which contains an object representing an individual
         item in the collection. --%>
    <c:when test="${not empty paramStartIndex && not empty paramEndIndex}">

      <dspel:getvalueof var="items" bean="${paramCollectionBeanProp}"/>

      <json:array prettyPrint="true" escapeXml="false">

        <c:forEach items="${items}" var="asset" varStatus="itemLoop"
                   begin="${paramStartIndex}" end="${paramEndIndex}">

          <%-- If supplied, use the elementBeanProp for accessing individual
               items. This is used to unwrap RepositoryFormLists. --%>
          <c:if test="${not empty paramElementBeanProp}">
            <dspel:getvalueof var="asset"
                              bean="${paramElementBeanProp}[${itemLoop.index}]"/>
          </c:if>

          <json:array>

            <%-- Cell 0: Warning --%>
            <json:property name=" ">
              <c:choose>
                <c:when test="${asset.validateResult eq WARNING}">
                  <img src="../images/icon_msgWarning.gif" alt="Warning" title="Warning" />
                </c:when>
                <c:when test="${asset.validateResult eq FAILURE}">
                <img src="../images/icon_msgError.gif" alt="Error" title="Error" />
                </c:when>
                <c:otherwise>
                </c:otherwise>
              </c:choose>
            </json:property>


            <%-- Cell 1: Status --%>
            <json:property name="status">
              <c:choose>
                <c:when test="${asset.isNew}">
                   <b><fmt:message key='transfer.importDialog.new'/></b>
                </c:when>
                <c:when test="${asset.modified}">
                   <b><fmt:message key='transfer.importDialog.changed'/></b>
                </c:when>
                <c:otherwise>
                </c:otherwise>
              </c:choose>
            </json:property>


            <c:forEach items="${asset.propertyValidateInfos}" var="prop" varStatus="itemPLoop">
              <%-- Cell N: Property --%>
              <json:property  name="${importAsset.propertyDisplayNames[itemPLoop.index]}">
              <div class="import_<c:out value="${prop.validateResult}"/>" title="<c:out value='${prop.errorMessage}'/>">
                <c:choose>
                  <c:when test="${prop.isCollection}"> 
                    <c:forEach items="${prop.truncatedValue}" var="compValue" varStatus="lStatus"><c:choose><c:when test="${prop.componentValidateResults[lStatus.index] eq WARNING}"><span class='impWarning' title="<c:out value='${prop.errorMessage}'/>"><c:out value="${compValue}"/></span></c:when><c:when test="${prop.componentValidateResults[lStatus.index] eq FAILURE}"><span class='impError' title="<c:out value='${prop.errorMessage}'/>"><c:out value="${compValue}"/></span></c:when><c:otherwise><c:out value="${compValue}"/></c:otherwise></c:choose><c:if test="${! lStatus.last}">,</c:if></c:forEach><c:if test="${prop.isTruncated}"><b><fmt:message key='transfer.importDialog.truncated'/></b></c:if>
                  </c:when>
                  <c:otherwise>
                    <c:choose><c:when test="${prop.validateResult eq WARNING}"><span class='impWarning' title="<c:out value='${prop.errorMessage}'/>"><c:out value="${prop.truncatedValue}"/></span></c:when><c:when test="${prop.validateResult eq FAILURE}"><span class='impError' title="<c:out value='${prop.errorMessage}'/>"><c:out value="${prop.truncatedValue}"/></span></c:when><c:otherwise><c:out value="${prop.truncatedValue}"/></c:otherwise></c:choose><c:if test="${prop.isTruncated}"><b><fmt:message key='transfer.importDialog.truncated'/></b></c:if>
                  </c:otherwise>
                </c:choose>
              </div>
              </json:property>

            </c:forEach>

          </json:array>

        </c:forEach>

      </json:array>
</c:when>
</c:choose>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/transfer/assetValidateComponent.jsp#2 $$Change: 651448 $--%>
