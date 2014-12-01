<%--
  Custom editor for groups property of userSegmentList repository item.

  The following request-scoped variables are expected to be set:

  @param  mpv  A MappedPropertyView item for this view
  @author amitj
  @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/segmentlist/segmentListComponentEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>
  
<!-- Begin DPS-UI's /assetEditor/property/segmentlist/segmentListComponentEditor.jsp -->

<%@ taglib prefix="c"      uri="http://java.sun.com/jstl/core"                    %>
<%@ taglib prefix="dspel"  uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="fmt"    uri="http://java.sun.com/jstl/fmt"                     %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui"                %>

<dspel:page>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="paramInitialize"    param="initialize"/>
  <dspel:getvalueof var="paramTemplate"      param="template"/>
  <dspel:getvalueof var="paramValueIdPrefix" param="valueIdPrefix"/>
  <dspel:getvalueof var="paramIndex"         param="index"/>
  <dspel:getvalueof var="paramValueId"       param="valueId"/>
  <dspel:getvalueof var="paramInfoObj"       param="infoObj"/>

  <%-- Unpack request-scoped parameters into page parameters --%>
  <c:set var="propertyView" value="${requestScope.mpv}"/>

  <dspel:importbean var="config" bean="/atg/web/assetmanager/ConfigurationInfo"/>
  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>  

  <%-- Derive IDs for some form elements and data objects --%>
  <c:set var="id"            value="${requestScope.atgPropertyViewId}"/>
  <c:set var="labelIdPrefix" value="${id}_label_"/>
  <c:set var="labelId"       value="${labelIdPrefix}${paramIndex}"/>
  <c:set var="onPickFunc"    value="${id}_onPick"/>

  <c:choose>
    <c:when test="${paramInitialize}">

      <script type="text/javascript">

        // Indicate that the collection editor should use the asset picker, and
        // specify a selection callback.
        <c:out value="${paramInfoObj}"/>.useAssetPicker = true;
        <c:out value="${paramInfoObj}"/>.onPick = <c:out value='${onPickFunc}'/>;

        // Since this property is not a collection of repository items, we need
        // to explicitly specify the repository to be used for the asset picker.
        <c:out value="${paramInfoObj}"/>.repositoryName = "PublishingFiles";
        <c:out value="${paramInfoObj}"/>.repositoryPath = "/atg/epub/file/SecuredPublishingFileRepository";
        <c:out value="${paramInfoObj}"/>.itemType       = "segment";
        <web-ui:getTree var="treePath"
                        repository="/atg/epub/file/SecuredPublishingFileRepository"
                        itemType="segment"
                        treeRegistry="${sessionInfo.treeRegistryPath}"/>
        <c:out value="${paramInfoObj}"/>.treeComponent = "<c:out value='${treePath}'/>";

        // Called when the user makes a selection from the asset picker.
        //
        // @param  pSelected  An object containing info about the selected asset
        // @param  pIndex     The index of the collection element to which the
        //                    asset picker applies
        //
        function <c:out value="${onPickFunc}"/>(pSelected, pIndex) {
          
          // The segment name is the name of the selected asset minus the
          // ".properties" extension.
          var segmentName = pSelected.displayName.replace(/\.properties/g, "");                  

          // Store the segment name in the value input and the label.
          var input = document.getElementById("<c:out value='${paramValueIdPrefix}'/>" + pIndex);
          if (input)
            input.value = segmentName;
          var label = document.getElementById("<c:out value='${labelIdPrefix}'/>" + pIndex);
          if (label)
            label.innerHTML = segmentName;
        }

      </script>
    </c:when>
    <c:otherwise>
      <%-- Get the current property value, unless we are rendering a template --%>
      <c:if test="${not paramTemplate}">
        <dspel:getvalueof var="currentVal" bean="${propertyView.formHandlerProperty}[${paramIndex}]"/>
      </c:if>
      
      <td class="formValueCell">
        <%-- Store the property value in a hidden input whose value will be 
             manipulated by the onPick function --%>
        <input type="hidden" id="<c:out value='${paramValueId}'/>"
               class="formTextField" value="<c:out value='${currentVal}'/>"/>

        <%-- Use a label to display the property value --%>
        <span id="<c:out value='${labelId}'/>" >
          <c:out value="${currentVal}"/>
        </span>
      </td>
    </c:otherwise>
  </c:choose>

</dspel:page>

<!-- End DPS-UI's /assetEditor/property/segmentlist/segmentListComponentEditor.jsp -->
<%-- @version $Id: //product/DPS-UI/version/10.0.3/src/web-apps/DPS-UI/assetEditor/property/segmentlist/segmentListComponentEditor.jsp#2 $$Change: 651448 $--%>
