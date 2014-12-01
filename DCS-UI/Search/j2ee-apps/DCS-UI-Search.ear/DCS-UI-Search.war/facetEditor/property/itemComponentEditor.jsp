<%--
  Default editor for RepositoryItem elements of collection properties.

  The following request-scoped variables are expected to be set:


  @param  beanProp      The formhandler bean property
  @param  propertyView  A MappedPropertyView item for this view

  @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/itemComponentEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"        uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"    uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"      uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="pws"      uri="http://www.atg.com/taglibs/pws"                   %>
<%@ taglib prefix="biz"      uri="http://www.atg.com/taglibs/bizui"                 %>
<%@ taglib prefix="web-ui"   uri="http://www.atg.com/taglibs/web-ui"                %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui"              %>

<!-- Begin itemComponentEditor.jsp -->

<dspel:page>

  <%-- Unpack all DSP parameters (see coding standards for more info) --%>
  <dspel:getvalueof var="paramInitialize"    param="initialize"/>
  <dspel:getvalueof var="paramTemplate"      param="template"/>
  <dspel:getvalueof var="paramValueIdPrefix" param="valueIdPrefix"/>
  <dspel:getvalueof var="paramIndex"         param="index"/>
  <dspel:getvalueof var="paramValueId"       param="valueId"/>
  <dspel:getvalueof var="paramInfoObj"       param="infoObj"/>
  <dspel:getvalueof var="paramRowIdPrefix"   param="rowIdPrefix"/>

  <%-- TODO: don't hardcode this 
  <biz:getItemMapping var="testimap" 
                      mode="edit" 
                      readOnlyMode="view"
                      showExpert="true"
                      itemName="refineConfig"
                      itemPath="/atg/search/repository/RefinementRepository"/>

  <c:forEach items="${testimap.viewMappings}" var="miv">
    <c:set var="mappedPv" value="${miv.propertyMappings['refineElements']}"/>
    <c:if test="${!empty mappedPv}">
      <c:set var="propertyView" value="${mappedPv}"/>
    </c:if>
  </c:forEach>
  --%>
  
  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="beanProp"     value="${requestScope.beanProp}"/>
  <c:set var="propertyView" value="${requestScope.propertyView}"/>
  <dspel:getvalueof var="property"        param="property" />
  <dspel:getvalueof var="subProperty"     param="subProperty" />
  <dspel:getvalueof var="subPropertyType" param="subPropertyType" />



  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>

  <%-- Get the descriptor for this property's item type --%>
  <c:set var="itemDescriptor" value="${propertyView.componentRepositoryItemDescriptor}"/>

  <%-- TODO: don't hardcode this --%>
  <%-- Derive IDs for some form elements and data objects --%>
  <%-- 
  <c:set var="id"            value="refineElements"/>
  --%>
  <c:set var="id"            value="${propertyView.uniqueId}"/>
  <c:set var="linkIdPrefix"  value="${id}_link_"/>
  <c:set var="linkId"        value="${linkIdPrefix}${paramIndex}"/>
  <c:set var="indexObj"      value="${id}_index"/>
  <c:set var="onPickFunc"    value="${id}_onPick"/>
  <c:set var="onAddRowFunc"  value="${id}_onAddRow"/>
  <c:set var="onCancelPickFunc"  value="${id}_onCancelPick"/>
  <c:set var="pickAssetFunc" value="${id}_pickAsset"/>
  <c:set var="drillDownFunc" value="${id}_drillDown"/>

  <%-- The name of the addNew function is passed back to the parent container
       in the request scope. --%>
  <c:set scope="request" var="addNewFunc" value="${id}_addNew"/>

  <%-- TODO: come back to these --%>

  <%-- NB: FIXME: Don't hard code editAsset.jsp --%>
  <c:url var="linkURL" value="/assetEditor/editAsset.jsp">
    <c:param name="pushContext"      value="true"/>
    <%-- TODO: don't hardcode this --%>
    <c:param name="linkPropertyName" value="${property}"/>
    <c:if test="${not empty requestScope.categoryId}">
      <c:param name="categoryId" value="${requestScope.categoryId}"/>
    </c:if>
  </c:url>

  <%-- NB: FIXME: Don't hard code subTypeSelection.jsp --%>
  <c:url var="createURL" value="/assetEditor/subTypeSelection.jsp">
    <c:param name="repositoryPathName" value="${itemDescriptor.repository.absoluteName}"/>
    <c:param name="itemDescriptorName" value="${itemDescriptor.itemDescriptorName}"/>
    <%-- TODO: don't hardcode this --%>
    <c:choose>
      <c:when test="${not empty subProperty}">
        <c:param name="linkPropertyName" value="${property}.${subProperty}"/>
      </c:when>
      <c:otherwise>
        <c:param name="linkPropertyName" value="${property}"/>
      </c:otherwise>
    </c:choose>
    <c:if test="${propertyView.propertyDescriptor.typeName == 'map'}">
      <c:param name="requireMapKey" value="true"/>
    </c:if>
  </c:url>

  <c:choose>
    <c:when test="${paramInitialize}">

      <script type="text/javascript">
        //<![CDATA[
        // This variable holds the index of the element whose value is currently
        // being edited using the asset picker.
        var <c:out value="${indexObj}"/> = -1;

        <c:out value="${paramInfoObj}"/>.onAddRow = <c:out value='${onAddRowFunc}'/>;

        //
        // This function is called when the user makes a selection from the asset
        // picker.
        //
        // @param  selected  An object containing info about the selected asset
        //
        function <c:out value="${onPickFunc}"/>(selected) {

            if(typeof selected == "object" && Object.prototype.toString.apply(selected) == "[object Array]")
              {
                  //if object is array of selected items.
                  for(var i=0; i<selected.length;i++)
                  {
                     var  index = <c:out value="${indexObj}"/>;
                     if (index >= 0) {
                           // Store the ID of the selected asset in the value input.
                           var input = document.getElementById("<c:out value='${paramValueIdPrefix}'/>" + index);
                           if (input)
                             input.value = selected[i].id;

                            // Change the display string and destination of the drill-down link
                            // field to reflect the selected asset.
                            var link = document.getElementById("<c:out value='${linkIdPrefix}'/>" + index);
                           if (link) {

                              link.innerHTML = selected[i].displayName;
                              link.href = "javascript:<c:out value='${drillDownFunc}'/>('" + selected[i].uri + "')";
                            }
                          }

                  <c:out value="${indexObj}"/> = -1;
                  <c:out value="${paramInfoObj}"/>.addRow();
                }
               }
            else{
                   // if object is selcted item itself.
                   var  index = <c:out value="${indexObj}"/>;
                   if (index >= 0) {
                      // Store the ID of the selected asset in the value input.
                var input = document.getElementById("<c:out value='${paramValueIdPrefix}'/>" + index);
                if (input)
                  input.value = selected.id;

                // Change the display string and destination of the drill-down link
                // field to reflect the selected asset.
                var link = document.getElementById("<c:out value='${linkIdPrefix}'/>" + index);
                if (link) {

               link.innerHTML = selected.displayName;
               link.href = "javascript:<c:out value='${drillDownFunc}'/>('" + selected.uri + "')";
               }
             }
                <c:out value="${indexObj}"/> = -1;

          }
         markAssetModified();
       }

        //
        // This function is called when the user clicks the "Edit" button.
        // It allows the user to specify a value for the property by selecting an
        // asset from the asset picker.
        //
        function <c:out value="${pickAssetFunc}"/>(index,useOnHide) {

          // Save the given item index so that the onPick function can find it.
          <c:out value="${indexObj}"/> = index;

          var allowableTypes = new Array();
          var assetType = new Object();
          assetType.typeCode       = "repository";
          assetType.displayName    = "NotUsed";
          assetType.repositoryName = "<c:out value='${itemDescriptor.repository.repositoryName}'/>";
          assetType.createable     = "false";

          // The following two properties determine which view is to be displayed in the AP
          // NOTE: the type name needs to exist in the repository but it is not used in our AP plugin.
          assetType.typeName       = "<c:out value='${itemDescriptor.itemDescriptorName}'/>";
          assetType.componentPath  = "<c:out value='${itemDescriptor.repository.absoluteName}'/>";
          allowableTypes[allowableTypes.length] = assetType;

          var viewConfiguration = new Array();

          <%-- get the tree component --%>
          <%-- NB: FIXME: Remove hardcoded tree registry? --%>
          <web-ui:getTree var="treePath"
                          repository="${itemDescriptor.repository.absoluteName}"
                          itemType="${itemDescriptor.itemDescriptorName}"
                          treeRegistry="/atg/web/assetmanager/TreeRegistry"/>
          viewConfiguration.treeComponent = "<c:out value='${treePath}'/>";

          // Populate the allowableTypes array with the top-level asset type
          // and any subtypes.
          <pws:getItemSubTypes var="subtypes"
                               repositoryPath="${itemDescriptor.repository.absoluteName}"
                               itemType="${itemDescriptor.itemDescriptorName}"/>
          var itemTypes = "";
          <c:forEach var="subtype" items="${subtypes}" varStatus="loop">
            itemTypes += "<c:out value='${subtype.itemDescriptorName}'/>";
            <c:if test="${not loop.last}">
              itemTypes += ",";
            </c:if>
          </c:forEach>
          viewConfiguration.itemTypes = itemTypes;

          var encodedItemType = "<c:out value='${propertyView.encodedComponentType}'/>";
          var decodedItemType = encodedItemType.split(":");

          var picker = new AssetBrowser();
          picker.mapMode                 = "AssetManager.assetPicker";
          picker.clearAttributes         = "true";
          picker.pickerURL               = "<c:out value='${config.assetPickerRoot}${config.assetPicker}'/>?apView=1&";
          <fmt:message var="assetPickerTitle" key="propertyEditor.assetPickerTitle"/>
          <web-ui:encodeParameterValue var="assetPickerTitle" value="${assetPickerTitle}"/>
          picker.assetPickerTitle        = "<c:out value='${assetPickerTitle}'/>";
          picker.browserMode             = "pick";
          picker.createMode              = "none";
          picker.onSelect                = "<c:out value='${onPickFunc}'/>";
          picker.closeAction             = "hide";
          picker.assetTypes              = allowableTypes;
          picker.assetPickerParentWindow = window;
          picker.assetPickerFrameElement = parent.document.getElementById("iFrame");
          picker.assetPickerDivId        = "browser";
          if (useOnHide)
            picker.onHide           = "<c:out value='${onCancelPickFunc}'/>";

         // disable multi-select in case of edit selcted asset.
          if (!useOnHide)
            picker.isAllowMultiSelect      = "false";

           <c:choose>
            <c:when test="${not empty treePath}">
              picker.defaultView        = "Browse";
              picker.viewConfiguration  = viewConfiguration;
            </c:when>
            <c:otherwise>
              picker.defaultView        = "Search";
            </c:otherwise>
          </c:choose>
          picker.invoke();
        }


        //
        // This function is called after adding a new row.
        // It will automatically popup the asset picker after adding a new row
        // resulting from "Add Existing".
        //
        function <c:out value="${onAddRowFunc}"/>(index) {
          <c:out value="${pickAssetFunc}"/>(index,true);
        }


        //
        // This function is a called after cancelling the asset picker which was
        // from a new row.
        //
        function <c:out value="${onCancelPickFunc}"/>() {
          var index = <c:out value="${indexObj}"/>;
          if (index >= 0)
            <c:out value="${paramInfoObj}"/>.deleteRow("<c:out value='${paramRowIdPrefix}'/>" + index);
          <c:out value="${indexObj}"/> = -1;
        }

        //
        // This function is called when the user clicks a link to drill down
        // into another asset.
        //
        function <c:out value="${drillDownFunc}"/>(uri) {

          var linkURL = "<c:out value='${linkURL}' escapeXml='false'/>&assetURI=" + uri;

          // Force a save of the current asset, then move to the URL.
          parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(linkURL,null,null,null,null,true);
        }

        //
        // This function is called when the user clicks the "Add New" button.
        //
        function <c:out value="${requestScope.addNewFunc}"/>() {

          // Force a save of the current asset, then move to the creation URL.
          parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset("<c:out value='${createURL}' escapeXml='false'/>",null,null,null,null,true);
        }
        //]]>
      </script>
    </c:when>

    <c:otherwise>

      <%-- Get the current property value, unless we are rendering a template --%>
      <c:if test="${not paramTemplate}">
        <%-- TODO 
        <dspel:getvalueof var="assetId" bean="${propertyView.formHandlerProperty}.repositoryIds[${paramIndex}]"/>
        --%>
        <dspel:getvalueof var="assetId" bean="${beanProp}[${paramIndex}].repositoryId"/>
      </c:if>

      <td class="formValueCell">

        <%-- Store the property value's repositoryId in a hidden input whose value
             will be manipulated by callbacks from the asset picker and the "Clear"
             button. --%>
        <input type="hidden" id="<c:out value='${paramValueId}'/>"
               class="formTextField" value=""/>

        <c:choose>
          <c:when test="${not empty assetId && multiEditConfiguration}">

            <c:set var="displayNameProperty"
                   value="${propertyView.componentRepositoryItemDescriptor.itemDisplayNameProperty}"/>

            <span id="<c:out value='${linkId}'/>" >
              <%-- TODO
              <dspel:valueof bean="${propertyView.formHandlerProperty}[${paramIndex}].${displayNameProperty}"/>
              --%>
              <dspel:valueof bean="${beanProp}[${paramIndex}].${displayNameProperty}"/>
            </span>
          </c:when>

          <c:when test="${not empty assetId}">

            <%-- Display a hyperlink for drilling down into the current asset --%>

            <c:set var="displayNameProperty"
                   value="${propertyView.componentRepositoryItemDescriptor.itemDisplayNameProperty}"/>

            <asset-ui:createAssetURI var="linkedURI"
              componentName="${itemDescriptor.repository.repositoryName}"
              itemDescriptorName="${itemDescriptor.itemDescriptorName}"
              itemId="${assetId}"/>

            <a id="<c:out value='${linkId}'/>"
               href="javascript:<c:out value='${drillDownFunc}("${linkedURI}")'/>">
              <%-- TODO 
              <dspel:valueof bean="${propertyView.formHandlerProperty}[${paramIndex}].${displayNameProperty}"/>
              --%>
              <dspel:valueof bean="${beanProp}[${paramIndex}].${displayNameProperty}"/>
            </a>

          </c:when>
          <c:otherwise>
            <a id="<c:out value='${linkId}'/>" href="#">
            </a>
          </c:otherwise>
        </c:choose>
      </td>
    </c:otherwise>
  </c:choose>

</dspel:page>

<!-- End itemComponentEditor.jsp -->

<%-- @version $Id: //product/DCS-UI/version/10.0.3/search/src/web-apps/DCS-UI-Search/facetEditor/property/itemComponentEditor.jsp#2 $$Change: 651448 $--%>
