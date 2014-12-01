<%--
  Standard editor for repository item properties...  which pops up an IFRAME asset browser

  @param   mpv  A request scoped, MappedPropertyView item for this view
  
  @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/image/imageLinkEditor.jsp#2 $$Change: 651448 $
  @updated $DateTime: 2011/06/07 13:55:45 $$Author: rbarbier $
  --%>

<%@ taglib prefix="c"       uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt"     uri="http://java.sun.com/jstl/fmt"  %>
<%@ taglib prefix="dspel"   uri="http://www.atg.com/taglibs/daf/dspjspELTaglib1_0" %>
<%@ taglib prefix="asset-ui" uri="http://www.atg.com/taglibs/asset-ui" %>
<%@ taglib prefix="web-ui" uri="http://www.atg.com/taglibs/web-ui" %>

<dspel:page>

  <dspel:importbean var="config"
                    bean="/atg/web/assetmanager/ConfigurationInfo"/>

  <fmt:setBundle basename="${config.resourceBundle}"/>
  <c:set var="sessionInfo" value="${config.sessionInfo}"/>

  <%-- Unpack DSP parameters --%>
  <dspel:getvalueof var="pProp" param="prop"/>

  <%--
   Display the property view title...
   Use the property display name by default.  Check 'title' attribute
   for override...
   --%>
  <c:set var="pvTitle" value="${pProp.displayName}"/>
  <c:if test="${mpv.attributes.title != null}">
    <c:set var="pvTitle" value="${mpv.attributes.title}"/>
  </c:if>

  <%--
   Determine the input class of this item..
   --%>
  <c:set var="pvClassAttr" value="class=\"formLabel\""/>
  <c:if test="${mpv.propertyDescriptor.required}">
    <c:set var="pvClassAttr" value="class=\"formLabel formLabelRequired\""/>
  </c:if>

  <c:set var="pvIsDisabled" value="false"/>
  <c:if test="${!mpv.propertyDescriptor.writable}">
    <c:set var="pvIsDisabled" value="true"/>
  </c:if>

  <c:set var="uniqueId" value="${mpv.uniqueId}"/>
  <c:if test="${not empty requestScope.uniqueAssetID}">
    <c:set var="uniqueId" value="${requestScope.uniqueAssetID}${uniqueId}"/>  
  </c:if>

  <%-- A hidden field which maps to the real bean property... --%>
  <dspel:input type="hidden" 
             nullable="${ ! mpv.propertyDescriptor.required }"
             bean="${mpv.formHandlerProperty}.repositoryId"
             id="${uniqueId}Value"/>

  <dspel:getvalueof var="assetId"
    bean="${mpv.formHandlerProperty}.repositoryId"/>

  <c:set var="itemDescriptor" value="${mpv.repositoryItemDescriptor}"/>

  <asset-ui:createAssetURI var="emptyRelatedAssetURI"
    componentName="${itemDescriptor.repository.repositoryName}"
    itemDescriptorName="${itemDescriptor.itemDescriptorName}"
    itemId=""/>

    <%-- NB: Need to allow all subtypes to be created, too. --%>
    <c:url var="createURL" value="/assetEditor/subTypeSelection.jsp">
      <c:param name="repositoryPathName" value="${itemDescriptor.repository.absoluteName}"/>
      <c:param name="itemDescriptorName" value="${itemDescriptor.itemDescriptorName}"/>
      <c:param name="linkPropertyName" value="${mpv.propertyName}"/>
      <c:param name="cancelSubTypeURL" value="${requestScope.cancelSubTypeURL}"/>
    </c:url>

    <%-- asset picker --%>
    <dspel:include page="/components/assetPickerLauncher.jsp">
      <dspel:param name="assetPickerHeader"    value="${assetPickerHeader}"/>
      <dspel:param name="assetType"            value="${mpv.repositoryItemDescriptor.itemDescriptorName}"/>
      <dspel:param name="isPickingParent"      value="false"/>
      <dspel:param name="launchPickerFunction" value="${uniqueId}_pick"/>
      <dspel:param name="onSelectFunction"     value="${uniqueId}_set"/>
      <dspel:param name="repositoryName"       value="${mpv.repositoryItemDescriptor.repository.repositoryName}"/>
      <dspel:param name="repositoryPath"       value="${mpv.repositoryItemDescriptor.repository.absoluteName}"/>
    </dspel:include>

  <script type="text/javascript">

    function <c:out value="${uniqueId}"/>_pushContext() {
      var input = document.getElementById( '<c:out value="${uniqueId}"/>' + "Value" );
      var propPage = "<c:out value='${config.contextRoot}'/>/assetEditor/editAsset.jsp" + 
                       "?assetURI=<c:out value='${emptyRelatedAssetURI}'/>" + input.value +
                       "&pushContext=true" +
                       "&linkPropertyName=<c:out value='${mpv.propertyName}'/>";

     // force save of current asset, then go to linked asset.
     parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset(propPage);
    }


    // this function is a callback, called by the asset browser
    // when a related asset is chosen by the user
    function <c:out value="${uniqueId}"/>_set( selected, attributes ) {
      var input = 
        document.getElementById( '<c:out value="${uniqueId}"/>' + "Value" );
      input.value = selected.id;
      markAssetModified();
      submitForm();
    }

    <c:if test="${ ! mpv.propertyDescriptor.required }">
      <%-- 
         Inserts the word "null" into the hidden dspel:input tag value
         for empty refernces that can be null.
      --%>
      function <c:out value="${uniqueId}"/>_checkNull() {
        var input = 
          document.getElementById( '<c:out value="${uniqueId}"/>' + "Value" );
        if ( input.value == null || input.value.length==0 )
          input.value="null";
      } 
      registerOnLoad( function() { <c:out value="${uniqueId}"/>_checkNull(); } );
    </c:if>


    function <c:out value="${uniqueId}"/>_create() {
      // Force a save of the current asset, then move to the creation URL.
      parent.atg.assetmanager.saveconfirm.saveBeforeLeaveAsset("<c:out value='${createURL}' escapeXml='false'/>");
    }

  </script>

  <%-- Get the name of the property in this item that is the display name --%>
  <c:set var="displayNameProperty"
     value="${mpv.propertyDescriptor.propertyBeanInfo.itemDisplayNameProperty}"/>


  <c:if test="${not empty displayNameProperty}">
    <dspel:getvalueof var="displayNameValue" bean="${mpv.formHandlerProperty}.${displayNameProperty}"/>
  </c:if>

  <c:if test="${empty displayNameValue}">
    <dspel:getvalueof var="displayNameValue" bean="${mpv.formHandlerProperty}.repositoryId"/>
  </c:if>

<fieldset class="buttons">
  <legend><c:out value="${pvTitle}"/></legend>

  <%--
   Display any errors that were caught for this property...
   --%>
  <c:if test="${ ! empty formHandler.propertyExceptions[mpv.propertyName]}">
    <span class="error">
      <c:out value="${formHandler.propertyExceptions[mpv.propertyName].message}"/>
    </span>
    <br/>
  </c:if>

  <table class="formTable">
    <c:choose>
      <c:when test="${not empty assetId}">
        <tr>
          <%-- Display a hyperlink for drilling down into the current asset --%>

          <td rowspan="4">
            <dspel:getvalueof id="url" bean="${mpv.formHandlerProperty}.url" />
            <web-ui:invoke var="hasValidExtension"
                           componentPath="/atg/web/assetmanager/editor/IsValidMediaName"
                           method="checkValidFileName">
              <web-ui:parameter type="java.lang.String" value="${url}"/>
            </web-ui:invoke>

            <fmt:message var="altMessage" key="imageLinkEditor.altMessage">
              <fmt:param value="${url}"/>
            </fmt:message>
            <c:if test="${! hasValidExtension}">
              <fmt:message var="altMessage" key="imageLinkEditor.extensionErrorMessage"/>
            </c:if>
            <a id='<c:out value="${uniqueId}"/>Image' onmouseover="status='';return true;"
              href="javascript:<c:out value="${uniqueId}"/>_pushContext();">
              <img src='<c:out value="${url}"/>' alt='<c:out value="${altMessage}"/>' width="75" height="75" class="mediaImage" />
            </a>
          </td>

        </tr>

        <tr>
          <td class="formLabel">
            <fmt:message key="imageLinkEditor.name"/>
          </td>
          <td>
            <a id='<c:out value="${uniqueId}"/>DisplayName' onmouseover="status='';return true;"
               href="javascript:<c:out value="${uniqueId}"/>_pushContext();">
              <c:out value="${displayNameValue}"/>
            </a>
          </td>
        </tr>

        <tr>
          <td class="formLabel">
            <fmt:message key="imageLinkEditor.type"/>
          </td>
          <td>
            <dspel:getvalueof id="mimeType" bean="${mpv.formHandlerProperty}.mimeType" />
            <c:out value="${mimeType}"/>
          </td>
        </tr>

        <tr>
          <td class="formLabel">
            <fmt:message key="imageLinkEditor.creationDate"/>
          </td>
          <td>
            <dspel:getvalueof id="creationDate" bean="${mpv.formHandlerProperty}.creationDate" />
            <c:out value="${creationDate}"/>
          </td>
        </tr>

      </c:when>
      <c:otherwise>
        <tr>
          <td class="noImage"><fmt:message key="imageLinkEditor.noImage"/></td>
	</tr>
      </c:otherwise>
    </c:choose>
  </table>

  <table class="fieldsetFooterContainer">

    <tr>
      <td class="fieldsetFooter">
      <!-- Right Pane Footer Action Buttons : Includes right pane footer action buttons -->
        <div class="fieldsetFooterRight">
            <c:if test="${not empty assetId}">              
              <c:set var="clearFunction" value="${uniqueId}_set({id: 'null', displayName: ''})"/>
              <a href="javascript:<c:out value='${clearFunction}'/>" 
                class="buttonSmall" title="<fmt:message 
                key='imageLinkEditor.clearMedia.title'/>"><span><fmt:message 
                key="imageLinkEditor.clearMedia"/></span></a>
            </c:if>
            <a href="javascript:<c:out value='${uniqueId}'/>_pick()" 
               class="buttonSmall" title="<fmt:message 
               key='imageLinkEditor.select.title'/>"><span><fmt:message 
               key="imageLinkEditor.select"/></span></a>
            <%-- Display a button for creating a new asset --%>
            <a href="javascript:<c:out value='${uniqueId}'/>_create()"
                 class="buttonSmall" title="<fmt:message key='imageLinkEditor.createNew.title'/>">
              <span><fmt:message key="imageLinkEditor.createNew"/></span></a> 

        </div>
        <div class="fieldsetFooterLeft">
        </div>
      </td>
    </tr>
  </table>

</fieldset>

</dspel:page>
<%-- @version $Id: //product/AssetUI/version/10.0.3/src/web-apps/AssetManager/assetEditor/property/image/imageLinkEditor.jsp#2 $$Change: 651448 $--%>
